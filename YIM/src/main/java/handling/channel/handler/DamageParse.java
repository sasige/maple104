package handling.channel.handler;

import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import client.PlayerStats;
import client.Skill;
import client.SkillFactory;
import client.anticheat.CheatTracker;
import client.anticheat.CheatingOffense;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.status.MonsterStatus;
import client.status.MonsterStatusEffect;
import constants.GameConstants;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import handling.world.World;
import handling.world.World.Broadcast;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import org.apache.log4j.Logger;

import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import server.Randomizer;
import server.ServerProperties;
import server.life.Element;
import server.life.ElementalEffectiveness;
import server.life.MapleMonster;
import server.life.MapleMonsterStats;
import server.maps.Event_PyramidSubway;
import server.maps.MapleMap;
import server.maps.MapleMapItem;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import tools.AttackPair;
import tools.FileoutputUtil;
import tools.MaplePacketCreator;
import tools.Pair;
import tools.data.LittleEndianAccessor;

public class DamageParse {
    
    private static final Logger log = Logger.getLogger(DamageParse.class);
    
    public static void applyAttack(AttackInfo attack, Skill theSkill, MapleCharacter player, int attackCount, double maxDamagePerMonster, MapleStatEffect effect, AttackType attack_type) {
        MapleMonster monster;
        if (!player.isAlive()) {
            player.getCheatTracker().registerOffense(CheatingOffense.ATTACKING_WHILE_DEAD, "操作者已死亡.");
            return;
        }
        if (player.isBanned()) {
            return;
        }
        if ((attack.real) && (GameConstants.getAttackDelay(attack.skill, theSkill) >= 100)) {
            player.getCheatTracker().checkAttack(attack.skill, attack.lastAttackTickCount);
        }
        if (attack.skill != 0) {
            if (effect == null) {
                player.getClient().getSession().write(MaplePacketCreator.enableActions());
                return;
            }
            if (GameConstants.isMulungSkill(attack.skill)) {
                if (player.getMapId() / 10000 != 92502) {
                    return;
                }
                if (player.getMulungEnergy() < 10000) {
                    return;
                }
                player.mulung_EnergyModify(false);
            } else if (GameConstants.isPyramidSkill(attack.skill)) {
                if (player.getMapId() / 1000000 != 926) {
                    return;
                }
                if ((player.getPyramidSubway() == null) || (!player.getPyramidSubway().onSkillUse(player))) {
                    return;
                }
            } else if (GameConstants.isInflationSkill(attack.skill)) {
                if (player.getBuffedValue(MapleBuffStat.GIANT_POTION) == null) {
                    return;
                }
            } else if ((attack.targets > effect.getMobCount()) && (attack.skill != 1211002) && (attack.skill != 1220010)) {
                player.getCheatTracker().registerOffense(CheatingOffense.MISMATCHING_BULLETCOUNT, "异常的攻击次数.");
                return;
            }
        }
        if ((player.getClient().getChannelServer().isAdminOnly()) && (player.isAdmin())) {
            player.dropMessage(-1, new StringBuilder().append("Animation: ").append(Integer.toHexString((attack.display & 0x8000) != 0 ? attack.display - 32768 : attack.display)).toString());
        }
        boolean useAttackCount = (attack.skill != 4211006) && (attack.skill != 3221007) && (attack.skill != 23121003) && ((attack.skill != 1311001) || (player.getJob() != 132)) && (attack.skill != 3211006) && (attack.skill != 24100003) && (attack.skill != 24120002);
        if ((attack.hits > attackCount)
                && (useAttackCount)) {
            if ((player.isGM()) && (ServerProperties.ShowPacket())) {
                player.dropMessage(-5, new StringBuilder().append("物理攻击次数检测 attack.hits ").append(attack.hits).append(" attackCount ").append(attackCount).toString());
            }
            
            player.getCheatTracker().registerOffense(CheatingOffense.MISMATCHING_BULLETCOUNT, "异常的攻击次数.");
            log.info(new StringBuilder().append("[作弊] ").append(player.getName()).append(" 物理攻击次数异常。 attack.hits ").append(attack.hits).append(" attackCount ").append(attackCount).append(" 技能ID ").append(attack.skill).toString());
            World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append("[GM Message] ").append(player.getName()).append(" (等级 ").append(player.getLevel()).append(") 物理攻击次数异常。 attack.hits ").append(attack.hits).append(" attackCount ").append(attackCount).append(" 技能ID ").append(attack.skill).toString()));
            return;
        }
        
        if ((attack.hits > 0) && (attack.targets > 0)) {
            if (!player.getStat().checkEquipDurabilitys(player, -1)) {
                player.dropMessage(5, "An item has run out of durability but has no inventory room to go to.");
                return;
            }
        }
        int totDamage = 0;
        MapleMap map = player.getMap();
        Point Original_Pos = player.getPosition();
        
        if (attack.skill == 4211006) {
            for (AttackPair oned : attack.allDamage) {
                if (oned.attack != null) {
                    continue;
                }
                MapleMapObject mapobject = map.getMapObject(oned.objectid, MapleMapObjectType.ITEM);
                if (mapobject != null) {
                    MapleMapItem mapitem = (MapleMapItem) mapobject;
                    mapitem.getLock().lock();
                    try {
                        if (mapitem.getMeso() > 0) {
                            if (mapitem.isPickedUp()) {
                                return;
                            }
                            map.removeMapObject(mapitem);
                            map.broadcastMessage(MaplePacketCreator.explodeDrop(mapitem.getObjectId()));
                            mapitem.setPickedUp(true);
                        } else {
                            player.getCheatTracker().registerOffense(CheatingOffense.ETC_EXPLOSION, "异常的技能效果.");
                            return;
                        }
                    } finally {
                        mapitem.getLock().unlock();
                    }
                } else {
                    player.getCheatTracker().registerOffense(CheatingOffense.EXPLODING_NONEXISTANT, "异常的技能效果.");
                    return;
                }
            }
        }
        int totDamageToOneMonster = 0;
        long hpMob = 0L;
        PlayerStats stats = player.getStat();
        
        int CriticalDamage = stats.passive_sharpeye_percent();
        int ShdowPartnerAttackPercentage = 0;
        if ((attack_type == AttackType.RANGED_WITH_SHADOWPARTNER) || (attack_type == AttackType.NON_RANGED_WITH_MIRROR)) {
            MapleStatEffect shadowPartnerEffect = player.getStatForBuff(MapleBuffStat.影分身);
            if (shadowPartnerEffect != null) {
                ShdowPartnerAttackPercentage += shadowPartnerEffect.getX();
            }
            attackCount /= 2;
        }
        ShdowPartnerAttackPercentage *= (CriticalDamage + 100) / 100;
        if (attack.skill == 4221001) {
            ShdowPartnerAttackPercentage *= 10;
        }
        if ((attack.skill == 24121000) && (attack.movei != null)) {
            if (player.isHidden()) {
                player.setLastRes(attack.movei);
                map.broadcastGMMessage(player, MaplePacketCreator.movePlayer(player.getId(), attack.movei, Original_Pos), false);
            } else {
                map.broadcastMessage(player, MaplePacketCreator.movePlayer(player.getId(), attack.movei, Original_Pos), false);
            }
            MovementParse.updatePosition(attack.movei, player, 0);
            Point pos = player.getTruePosition();
            map.movePlayer(player, pos);
        }
        
        double maxDamagePerHit = 0.0D;
        
        for (AttackPair oned : attack.allDamage) {
            monster = map.getMonsterByOid(oned.objectid);
            if ((monster != null) && (monster.getLinkCID() <= 0)) {
                totDamageToOneMonster = 0;
                hpMob = monster.getMobMaxHp();
                MapleMonsterStats monsterstats = monster.getStats();
                int fixeddmg = monsterstats.getFixedDamage();
                boolean Tempest = (monster.getStatusSourceID(MonsterStatus.结冰) == 21120006) || (attack.skill == 21120006) || (attack.skill == 1221011);
                if ((!Tempest) && (!player.isGM())) {
                    if (((player.getJob() >= 3200) && (player.getJob() <= 3212) && (!monster.isBuffed(MonsterStatus.免疫伤害)) && (!monster.isBuffed(MonsterStatus.免疫魔攻)) && (!monster.isBuffed(MonsterStatus.反射魔攻))) || (attack.skill == 3221007) || (attack.skill == 23121003) || (((player.getJob() < 3200) || (player.getJob() > 3212)) && (!monster.isBuffed(MonsterStatus.免疫伤害)) && (!monster.isBuffed(MonsterStatus.免疫物攻)) && (!monster.isBuffed(MonsterStatus.反射物攻)))) {
                        maxDamagePerHit = CalculateMaxWeaponDamagePerHit(player, monster, attack, theSkill, effect, maxDamagePerMonster, Integer.valueOf(CriticalDamage));
                    } else {
                        maxDamagePerHit = 1.0D;
                    }
                }
                byte overallAttackCount = 0;
                
                for (Pair eachde : oned.attack) {
                    Integer eachd = (Integer) eachde.left;
                    overallAttackCount = (byte) (overallAttackCount + 1);
                    if ((useAttackCount) && (overallAttackCount - 1 == attackCount)) {
                        maxDamagePerHit = maxDamagePerHit / 100.0D * (ShdowPartnerAttackPercentage * (monsterstats.isBoss() ? stats.bossdam_r : stats.dam_r) / 100.0D);
                    }
                    
                    if (fixeddmg != -1) {
                        if (monsterstats.getOnlyNoramlAttack()) {
                            eachd = Integer.valueOf(attack.skill != 0 ? 0 : fixeddmg);
                        } else {
                            eachd = Integer.valueOf(fixeddmg);
                        }
                    } else if (monsterstats.getOnlyNoramlAttack()) {
                        eachd = Integer.valueOf(attack.skill != 0 ? 0 : Math.min(eachd.intValue(), (int) maxDamagePerHit));
                    } else if (!player.isGM()) {
                        if (Tempest) {
                            if (eachd.intValue() > monster.getMobMaxHp()) {
                                eachd = Integer.valueOf((int) Math.min(monster.getMobMaxHp(), 2147483647L));
                                
                                player.getCheatTracker().registerOffense(CheatingOffense.HIGH_DAMAGE, "攻击伤害过高.");
                            }
                        } else if (((player.getJob() >= 3200) && (player.getJob() <= 3212) && (!monster.isBuffed(MonsterStatus.免疫伤害)) && (!monster.isBuffed(MonsterStatus.免疫魔攻)) && (!monster.isBuffed(MonsterStatus.反射魔攻))) || (attack.skill == 23121003) || (((player.getJob() < 3200) || (player.getJob() > 3212)) && (!monster.isBuffed(MonsterStatus.免疫伤害)) && (!monster.isBuffed(MonsterStatus.免疫物攻)) && (!monster.isBuffed(MonsterStatus.反射物攻)))) {
                            if ((eachd.intValue() > maxDamagePerHit) && (maxDamagePerHit > 1000.0D)) {
                                player.getCheatTracker().registerOffense(CheatingOffense.HIGH_DAMAGE, new StringBuilder().append("[伤害: ").append(eachd).append(", 预计伤害: ").append(maxDamagePerHit).append(", 怪物ID: ").append(monster.getId()).append("] [职业: ").append(player.getJob()).append(", 等级: ").append(player.getLevel()).append(", 技能: ").append(attack.skill).append("]").toString());
                                if (attack.real) {
                                    player.getCheatTracker().checkSameDamage(eachd.intValue(), maxDamagePerHit);
                                }
                                if (eachd.intValue() > maxDamagePerHit * 3.0D) {
                                    eachd = Integer.valueOf((int) (maxDamagePerHit * 2.0D));
                                    if (eachd.intValue() >= 2499999) {
                                        player.getClient().getSession().close();
                                        return;
                                    }
                                }
                            }
                        } else if (eachd.intValue() > maxDamagePerHit) {
                            eachd = Integer.valueOf((int) maxDamagePerHit);
                        }
                        
                    }
                    
                    if (player == null) {
                        return;
                    }
                    totDamageToOneMonster += eachd.intValue();
                    
                    if (((eachd.intValue() == 0) || (monster.getId() == 9700021)) && (player.getPyramidSubway() != null)) {
                        player.getPyramidSubway().onMiss(player);
                    }
                }
                totDamage += totDamageToOneMonster;
                player.checkMonsterAggro(monster);
                if ((GameConstants.getAttackDelay(attack.skill, theSkill) >= 100) && (!GameConstants.isNoDelaySkill(attack.skill)) && (!GameConstants.is不检测范围(attack.skill)) && (!monster.getStats().isBoss()) && (player.getTruePosition().distanceSq(monster.getTruePosition()) > GameConstants.getAttackRange(effect, player.getStat().defRange))) {
                    World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append("[GM Message] ").append(player.getName()).append(" (等级 ").append(player.getLevel()).append(") 攻击范围异常。 地图ID: ").append(player.getMapId()).append(" 职业: ").append(player.getJob()).toString()));
                    player.getCheatTracker().registerOffense(CheatingOffense.ATTACK_FARAWAY_MONSTER, new StringBuilder().append("[范围: ").append(player.getTruePosition().distanceSq(monster.getTruePosition())).append(", 预期范围: ").append(GameConstants.getAttackRange(effect, player.getStat().defRange)).append(" 职业: ").append(player.getJob()).append("]").toString());
                }
                
                if (player.getBuffedValue(MapleBuffStat.敛财术) != null) {
                    switch (attack.skill) {
                        case 0:
                        case 4001334:
                        case 4201005:
                        case 4211002:
                        case 4211011:
                        case 4221007:
                        case 4221010:
                            handlePickPocket(player, monster, oned);
                    }
                }
                if (player.getattack(1) != null) {//幻影?
                    if (player.getMapId() >= player.getattack(1).minmapid && player.getMapId() <= player.getattack(1).maxmapid) {
                        totDamageToOneMonster /= player.getattack(1).attack;
                    }
                }
                
                if ((totDamageToOneMonster > 0) || (attack.skill == 1221011) || (attack.skill == 21120006)) {
                    if (GameConstants.is恶魔猎手(player.getJob())) {
                        player.handleForceGain(monster.getObjectId(), attack.skill);
                    }
                    if (attack.skill != 1221011) {
                        monster.damage(player, totDamageToOneMonster, true, attack.skill);
                    } else {
                        monster.damage(player, monster.getStats().isBoss() ? 500000L : monster.getHp() - 1L, true, attack.skill);
                    }
                    if (monster.isBuffed(MonsterStatus.反射物攻)) {
                        player.addHP(-(7000 + Randomizer.nextInt(8000)));
                    }
                    player.onAttack(monster.getMobMaxHp(), monster.getMobMaxMp(), attack.skill, monster.getObjectId(), totDamage);
                    switch (attack.skill) {
                        case 4001334:
                        case 4001344:
                        case 4101008:
                        case 4101010:
                        case 4111010:
                        case 4111013:
                        case 4121013:
                        case 4201005:
                        case 4211002:
                        case 4211011:
                        case 4221001:
                        case 4221007:
                        case 4221010:
                        case 4301001:
                        case 4311002:
                        case 4311003:
                        case 4321004:
                        case 4331000:
                        case 4331005:
                        case 4331006:
                        case 4341002:
                        case 4341004:
                        case 4341009:
                        case 14001004:
                        case 14101008:
                        case 14101009:
                        case 14111005:
                        case 14111008:
                            int[] skills = {4110011, 4210010, 4320005, 14110004};
                            for (int i : skills) {
                                Skill skill = SkillFactory.getSkill(i);
                                if (player.getTotalSkillLevel(skill) > 0) {
                                    MapleStatEffect venomEffect = skill.getEffect(player.getTotalSkillLevel(skill));
                                    if (!venomEffect.makeChanceResult()) {
                                        break;
                                    }
                                    monster.applyStatus(player, new MonsterStatusEffect(MonsterStatus.中毒, Integer.valueOf(1), i, null, false), true, venomEffect.getDuration(), true, venomEffect);
                                    break;
                                }
                                
                            }
                            
                            break;
                        case 4201004:
                            monster.handleSteal(player);
                            break;
                        case 21000002:
                        case 21000004:
                        case 21100001:
                        case 21100002:
                        case 21100007:
                        case 21110002:
                        case 21110003:
                        case 21110006:
                        case 21110007:
                        case 21110008:
                        case 21120005:
                        case 21120006:
                        case 21120007:
                        case 21120009:
                        case 21120010:
                        case 21110004: // Fenrir Phantom
                        case 21120002: // Overswing
                            if ((player.getBuffedValue(MapleBuffStat.属性攻击) != null) && (!monster.getStats().isBoss())) {
                                MapleStatEffect eff = player.getStatForBuff(MapleBuffStat.属性攻击);
                                if (eff != null) {
                                    monster.applyStatus(player, new MonsterStatusEffect(MonsterStatus.速度, Integer.valueOf(eff.getX()), eff.getSourceId(), null, false), false, eff.getY() * 1000, true, eff);
                                }
                            }
                            if ((player.getBuffedValue(MapleBuffStat.战神抗压) == null) || (monster.getStats().isBoss())) {
                                break;
                            }
                            MapleStatEffect eff = player.getStatForBuff(MapleBuffStat.战神抗压);
                            if ((eff != null) && (eff.makeChanceResult()) && (!monster.isBuffed(MonsterStatus.抗压))) {
                                monster.applyStatus(player, new MonsterStatusEffect(MonsterStatus.抗压, Integer.valueOf(1), eff.getSourceId(), null, false), false, eff.getX() * 1000, true, eff);
                            }
                            break;
                    }
                    
                    if (totDamageToOneMonster > 0) {
                        Item weapon_ = player.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -11);
                        if (weapon_ != null) {
                            MonsterStatus stat = GameConstants.getStatFromWeapon(weapon_.getItemId());
                            if ((stat != null) && (Randomizer.nextInt(100) < GameConstants.getStatChance())) {
                                MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(stat, Integer.valueOf(GameConstants.getXForStat(stat)), GameConstants.getSkillForStat(stat), null, false);
                                monster.applyStatus(player, monsterStatusEffect, false, 10000L, false, null);
                            }
                        }
                        if (player.getBuffedValue(MapleBuffStat.致盲) != null) {
                            MapleStatEffect eff = player.getStatForBuff(MapleBuffStat.致盲);
                            if ((eff != null) && (eff.makeChanceResult())) {
                                MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(MonsterStatus.命中, Integer.valueOf(eff.getX()), eff.getSourceId(), null, false);
                                monster.applyStatus(player, monsterStatusEffect, false, eff.getY() * 1000, true, eff);
                            }
                        }
                        if (player.getBuffedValue(MapleBuffStat.幻影步) != null) {
                            MapleStatEffect eff = player.getStatForBuff(MapleBuffStat.幻影步);
                            if ((eff != null) && (eff.makeChanceResult())) {
                                MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(MonsterStatus.速度, Integer.valueOf(eff.getX()), 3121007, null, false);
                                monster.applyStatus(player, monsterStatusEffect, false, eff.getY() * 1000, true, eff);
                            }
                        }
                        if ((player.getJob() == 121) || (player.getJob() == 122)) {
                            Skill skill = SkillFactory.getSkill(1211006);
                            if (player.isBuffFrom(MapleBuffStat.属性攻击, skill)) {
                                MapleStatEffect eff = skill.getEffect(player.getTotalSkillLevel(skill));
                                MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(MonsterStatus.结冰, Integer.valueOf(1), skill.getId(), null, false);
                                monster.applyStatus(player, monsterStatusEffect, false, eff.getY() * 2000, true, eff);
                            }
                        }
                    }
                    if ((effect != null) && (effect.getMonsterStati().size() > 0)
                            && (effect.makeChanceResult())) {
                        for (Map.Entry z : effect.getMonsterStati().entrySet()) {
                            monster.applyStatus(player, new MonsterStatusEffect((MonsterStatus) z.getKey(), (Integer) z.getValue(), theSkill.getId(), null, false), effect.isPoison(), effect.getDuration(), true, effect);
                        }
                    }
                }
            }
        }
        
        if ((hpMob > 0L) && (totDamageToOneMonster > 0)) {
            player.afterAttack(attack.targets, attack.hits, attack.skill);
        }
        if ((attack.skill != 0) && ((attack.targets > 0) || (attack.skill != 4341002)) && (!GameConstants.isNoDelaySkill(attack.skill))) {
            effect.applyTo(player, attack.position);
        }
        if ((totDamage > 1) && (GameConstants.getAttackDelay(attack.skill, theSkill) >= 100)) {
            CheatTracker tracker = player.getCheatTracker();
            tracker.setAttacksWithoutHit(true);
            if (tracker.getAttacksWithoutHit() >= 50) {
                tracker.registerOffense(CheatingOffense.ATTACK_WITHOUT_GETTING_HIT, "无敌自动封号.");
            }
        }
    }
    
    public static void applyAttackMagic(AttackInfo attack, Skill theSkill, MapleCharacter player, MapleStatEffect effect, double maxDamagePerHit) {
        if (!player.isAlive()) {
            player.getCheatTracker().registerOffense(CheatingOffense.ATTACKING_WHILE_DEAD);
            return;
        }
        if ((attack.real) && (GameConstants.getAttackDelay(attack.skill, theSkill) >= 100)) {
            player.getCheatTracker().checkAttack(attack.skill, attack.lastAttackTickCount);
        }
        
        if ((attack.hits > effect.getAttackCount()) || (attack.targets > effect.getMobCount())) {
            if ((player.isGM()) && (ServerProperties.ShowPacket())) {
                player.dropMessage(-5, new StringBuilder().append("魔法攻击次数检测  attack.hits ").append(attack.hits).append(" attackCount ").append(effect.getAttackCount()).append(" attack.targets ").append(attack.targets).append(" MobCount ").append(effect.getMobCount()).toString());
            }
            
            player.getCheatTracker().registerOffense(CheatingOffense.MISMATCHING_BULLETCOUNT, "异常的攻击次数.");
            log.info(new StringBuilder().append("[作弊] ").append(player.getName()).append(" 魔法攻击次数异常。attack.hits ").append(attack.hits).append(" attackCount ").append(effect.getAttackCount()).append(" attack.targets ").append(attack.targets).append(" MobCount ").append(effect.getMobCount()).toString());
            World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append("[GM Message] ").append(player.getName()).append(" (等级 ").append(player.getLevel()).append(") 魔法攻击次数异常。attack.hits ").append(attack.hits).append(" attackCount ").append(effect.getAttackCount()).append(" attack.targets ").append(attack.targets).append(" MobCount ").append(effect.getMobCount()).append(" 技能ID ").append(attack.skill).toString()));
            return;
        }
        if ((attack.hits > 0) && (attack.targets > 0)
                && (!player.getStat().checkEquipDurabilitys(player, -1))) {
            player.dropMessage(5, "An item has run out of durability but has no inventory room to go to.");
            return;
        }
        
        if (GameConstants.isMulungSkill(attack.skill)) {
            if (player.getMapId() / 10000 != 92502) {
                return;
            }
            if (player.getMulungEnergy() < 10000) {
                return;
            }
            player.mulung_EnergyModify(false);
        } else if (GameConstants.isPyramidSkill(attack.skill)) {
            if (player.getMapId() / 1000000 != 926) {
                return;
            }
            if ((player.getPyramidSubway() == null) || (!player.getPyramidSubway().onSkillUse(player))) {
                return;
            }
        } else if ((GameConstants.isInflationSkill(attack.skill))
                && (player.getBuffedValue(MapleBuffStat.GIANT_POTION) == null)) {
            return;
        }
        
        if ((player.getClient().getChannelServer().isAdminOnly()) && (player.isAdmin())) {
            player.dropMessage(-1, new StringBuilder().append("Animation: ").append(Integer.toHexString((attack.display & 0x8000) != 0 ? attack.display - 32768 : attack.display)).toString());
        }
        PlayerStats stats = player.getStat();
        Element element = player.getBuffedValue(MapleBuffStat.自然力重置) != null ? Element.NEUTRAL : theSkill.getElement();
        
        double MaxDamagePerHit = 0.0D;
        int totDamage = 0;
        
        int CriticalDamage = stats.passive_sharpeye_percent();
        Skill eaterSkill = SkillFactory.getSkill(GameConstants.getMPEaterForJob(player.getJob()));
        int eaterLevel = player.getTotalSkillLevel(eaterSkill);
        
        MapleMap map = player.getMap();
        for (AttackPair oned : attack.allDamage) {
            MapleMonster monster = map.getMonsterByOid(oned.objectid);
            if ((monster != null) && (monster.getLinkCID() <= 0)) {
                boolean Tempest = (monster.getStatusSourceID(MonsterStatus.结冰) == 21120006) && (!monster.getStats().isBoss());
                int totDamageToOneMonster = 0;
                MapleMonsterStats monsterstats = monster.getStats();
                int fixeddmg = monsterstats.getFixedDamage();
                if ((!Tempest) && (!player.isGM())) {
                    if ((!monster.isBuffed(MonsterStatus.免疫魔攻)) && (!monster.isBuffed(MonsterStatus.反射魔攻))) {
                        MaxDamagePerHit = CalculateMaxMagicDamagePerHit(player, theSkill, monster, monsterstats, stats, element, Integer.valueOf(CriticalDamage), maxDamagePerHit, effect);
                    } else {
                        MaxDamagePerHit = 1.0D;
                    }
                }
                byte overallAttackCount = 0;
                
                for (Pair eachde : oned.attack) {
                    Integer eachd = (Integer) eachde.left;
                    overallAttackCount = (byte) (overallAttackCount + 1);
                    if (fixeddmg != -1) {
                        eachd = Integer.valueOf(monsterstats.getOnlyNoramlAttack() ? 0 : fixeddmg);
                    } else if (monsterstats.getOnlyNoramlAttack()) {
                        eachd = Integer.valueOf(0);
                    } else if (!player.isGM()) {
                        if (Tempest) {
                            if (eachd.intValue() > monster.getMobMaxHp()) {
                                eachd = Integer.valueOf((int) Math.min(monster.getMobMaxHp(), 2147483647L));
                                
                                player.getCheatTracker().registerOffense(CheatingOffense.HIGH_DAMAGE_MAGIC, "攻击伤害过高.");
                            }
                        } else if ((!monster.isBuffed(MonsterStatus.免疫魔攻)) && (!monster.isBuffed(MonsterStatus.反射魔攻))) {
                            if ((eachd.intValue() > MaxDamagePerHit) && (MaxDamagePerHit > 1000.0D)) {
                                player.getCheatTracker().registerOffense(CheatingOffense.HIGH_DAMAGE_MAGIC, new StringBuilder().append("[伤害: ").append(eachd).append(", 预期: ").append(MaxDamagePerHit).append(", 怪物ID: ").append(monster.getId()).append("] [职业: ").append(player.getJob()).append(", 等级: ").append(player.getLevel()).append(", 技能: ").append(attack.skill).append("]").toString());
                                if (attack.real) {
                                    player.getCheatTracker().checkSameDamage(eachd.intValue(), MaxDamagePerHit);
                                }
                                if (eachd.intValue() > MaxDamagePerHit * 2.0D) {
                                    player.getCheatTracker().registerOffense(CheatingOffense.HIGH_DAMAGE_MAGIC_2, new StringBuilder().append("[伤害: ").append(eachd).append(", 预期: ").append(MaxDamagePerHit).append(", 怪物ID: ").append(monster.getId()).append("] [职业: ").append(player.getJob()).append(", 等级: ").append(player.getLevel()).append(", 技能: ").append(attack.skill).append("]").toString());
                                    eachd = Integer.valueOf((int) (MaxDamagePerHit * 2.0D));
                                    if (eachd.intValue() >= 2499999) {
                                        player.getClient().getSession().close();
                                        return;
                                    }
                                }
                            }
                        } else if (eachd.intValue() > MaxDamagePerHit) {
                            eachd = Integer.valueOf((int) MaxDamagePerHit);
                        }
                        
                    }
                    
                    totDamageToOneMonster += eachd.intValue();
                }
                totDamage += totDamageToOneMonster;
                player.checkMonsterAggro(monster);
                if ((GameConstants.getAttackDelay(attack.skill, theSkill) >= 100) && (!GameConstants.isNoDelaySkill(attack.skill)) && (!GameConstants.is不检测范围(attack.skill)) && (!monster.getStats().isBoss()) && (player.getTruePosition().distanceSq(monster.getTruePosition()) > GameConstants.getAttackRange(effect, player.getStat().defRange))) {
                    World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append("[GM Message] ").append(player.getName()).append(" (等级 ").append(player.getLevel()).append(") 攻击范围异常。 地图ID: ").append(player.getMapId()).append(" 职业: ").append(player.getJob()).toString()));
                    player.getCheatTracker().registerOffense(CheatingOffense.ATTACK_FARAWAY_MONSTER, new StringBuilder().append("[范围: ").append(player.getTruePosition().distanceSq(monster.getTruePosition())).append(", 预期范围: ").append(GameConstants.getAttackRange(effect, player.getStat().defRange)).append(" 职业: ").append(player.getJob()).append("]").toString());
                }
                if ((attack.skill == 2301002) && (!monsterstats.getUndead())) {
                    player.getCheatTracker().registerOffense(CheatingOffense.HEAL_ATTACKING_UNDEAD);
                    return;
                }
                if (totDamageToOneMonster > 0) {
                    monster.damage(player, totDamageToOneMonster, true, attack.skill);
                    if (monster.isBuffed(MonsterStatus.反射魔攻)) {
                        player.addHP(-(7000 + Randomizer.nextInt(8000)));
                    }
                    if (player.getBuffedValue(MapleBuffStat.缓速术) != null) {
                        MapleStatEffect eff = player.getStatForBuff(MapleBuffStat.缓速术);
                        if ((eff != null) && (eff.makeChanceResult()) && (!monster.isBuffed(MonsterStatus.速度))) {
                            monster.applyStatus(player, new MonsterStatusEffect(MonsterStatus.速度, Integer.valueOf(eff.getX()), eff.getSourceId(), null, false), false, eff.getY() * 1000, true, eff);
                        }
                    }
                    player.onAttack(monster.getMobMaxHp(), monster.getMobMaxMp(), attack.skill, monster.getObjectId(), totDamage);
                    
                    switch (attack.skill) {
                        case 2221003:
                            monster.setTempEffectiveness(Element.ICE, effect.getDuration());
                            break;
                        case 2121003:
                            monster.setTempEffectiveness(Element.FIRE, effect.getDuration());
                    }
                    
                    if ((effect != null) && (effect.getMonsterStati().size() > 0)
                            && (effect.makeChanceResult())) {
                        for (Map.Entry z : effect.getMonsterStati().entrySet()) {
                            monster.applyStatus(player, new MonsterStatusEffect((MonsterStatus) z.getKey(), (Integer) z.getValue(), theSkill.getId(), null, false), effect.isPoison(), effect.getDuration(), true, effect);
                        }
                    }
                    
                    if (eaterLevel > 0) {
                        eaterSkill.getEffect(eaterLevel).applyPassive(player, monster);
                    }
                }
            }
        }
        if (attack.skill != 2301002) {
            effect.applyTo(player);
        }
        if ((totDamage > 1) && (GameConstants.getAttackDelay(attack.skill, theSkill) >= 100)) {
            CheatTracker tracker = player.getCheatTracker();
            tracker.setAttacksWithoutHit(true);
            if (tracker.getAttacksWithoutHit() >= 50) {
                tracker.registerOffense(CheatingOffense.ATTACK_WITHOUT_GETTING_HIT, "无敌自动封号.");
            }
        }
    }
    
    private static double CalculateMaxMagicDamagePerHit(MapleCharacter chr, Skill skill, MapleMonster monster, MapleMonsterStats mobstats, PlayerStats stats, Element elem, Integer sharpEye, double maxDamagePerMonster, MapleStatEffect attackEffect) {
        int dLevel = Math.max(mobstats.getLevel() - chr.getLevel(), 0) * 2;
        int HitRate = Math.min((int) Math.floor(Math.sqrt(stats.getAccuracy())) - (int) Math.floor(Math.sqrt(mobstats.getEva())) + 100, 100);
        if (dLevel > HitRate) {
            HitRate = dLevel;
        }
        HitRate -= dLevel;
        if ((HitRate <= 0) && ((!GameConstants.is新手职业(skill.getId() / 10000)) || (skill.getId() % 10000 != 1000))) {
            return 0.0D;
        }
        
        int CritPercent = sharpEye.intValue();
        ElementalEffectiveness ee = monster.getEffectiveness(elem);
        double elemMaxDamagePerMob;
        switch (ee) {
            case IMMUNE:
                elemMaxDamagePerMob = 1.0D;
                break;
            default:
                elemMaxDamagePerMob = ElementalStaffAttackBonus(elem, maxDamagePerMonster * ee.getValue(), stats);
        }
        
        int MDRate = monster.getStats().getMDRate();
        MonsterStatusEffect pdr = monster.getBuff(MonsterStatus.魔防);
        if (pdr != null) {
            MDRate += pdr.getX().intValue();
        }
        elemMaxDamagePerMob -= elemMaxDamagePerMob * (Math.max(MDRate - stats.ignoreTargetDEF - attackEffect.getIgnoreMob(), 0) / 100.0D);
        
        elemMaxDamagePerMob += elemMaxDamagePerMob / 100.0D * CritPercent;
        
        elemMaxDamagePerMob *= (monster.getStats().isBoss() ? chr.getStat().bossdam_r : chr.getStat().dam_r) / 100.0D;
        MonsterStatusEffect imprint = monster.getBuff(MonsterStatus.鬼刻符);
        if (imprint != null) {
            elemMaxDamagePerMob += elemMaxDamagePerMob * imprint.getX().intValue() / 100.0D;
        }
        elemMaxDamagePerMob += elemMaxDamagePerMob * chr.getDamageIncrease(monster.getObjectId()) / 100.0D;
        if (GameConstants.is新手职业(skill.getId() / 10000)) {
            switch (skill.getId() % 10000) {
                case 1000:
                    elemMaxDamagePerMob = 40.0D;
                    break;
                case 1020:
                    elemMaxDamagePerMob = 1.0D;
                    break;
                case 1009:
                    elemMaxDamagePerMob = monster.getStats().isBoss() ? monster.getMobMaxHp() / 30L * 100L : monster.getMobMaxHp();
            }
        }
        
        switch (skill.getId()) {
            case 32001000:
            case 32101000:
            case 32111002:
            case 32121002:
                elemMaxDamagePerMob *= 1.5D;
        }
        
        if ((monster.getId() >= 9400900) && (monster.getId() <= 9400911)) {
            elemMaxDamagePerMob = 999999.0D;
        } else if ((monster.getId() >= 9600101) && (monster.getId() <= 9600136)) {
            elemMaxDamagePerMob = 888888.0D;
        }
        if (elemMaxDamagePerMob > 999999.0D) {
            elemMaxDamagePerMob = 999999.0D;
        } else if (elemMaxDamagePerMob <= 0.0D) {
            elemMaxDamagePerMob = 1.0D;
        }
        return elemMaxDamagePerMob;
    }
    
    private static double ElementalStaffAttackBonus(Element elem, double elemMaxDamagePerMob, PlayerStats stats) {
        switch (elem) {
            case FIRE:
                return elemMaxDamagePerMob / 100.0D * (stats.element_fire + stats.getElementBoost(elem));
            case ICE:
                return elemMaxDamagePerMob / 100.0D * (stats.element_ice + stats.getElementBoost(elem));
            case LIGHTING:
                return elemMaxDamagePerMob / 100.0D * (stats.element_light + stats.getElementBoost(elem));
            case POISON:
                return elemMaxDamagePerMob / 100.0D * (stats.element_psn + stats.getElementBoost(elem));
        }
        return elemMaxDamagePerMob / 100.0D * (stats.def + stats.getElementBoost(elem));
    }
    
    private static void handlePickPocket(MapleCharacter player, MapleMonster mob, AttackPair oned) {
        int maxmeso = player.getBuffedValue(MapleBuffStat.敛财术).intValue();
        for (Pair eachde : oned.attack) {
            Integer eachd = (Integer) eachde.left;
            if ((player.getStat().pickRate >= 100) || (Randomizer.nextInt(99) < player.getStat().pickRate)) {
                player.getMap().spawnMesoDrop(Math.min((int) Math.max(((double) eachd / (double) 20000) * (double) maxmeso, (double) 1), maxmeso), new Point((int) (mob.getTruePosition().getX() + Randomizer.nextInt(100) - 50), (int) (mob.getTruePosition().getY())), mob, player, false, (byte) 0);
            }
        }
    }
    
    private static double CalculateMaxWeaponDamagePerHit(MapleCharacter player, MapleMonster monster, AttackInfo attack, Skill theSkill, MapleStatEffect attackEffect, double maximumDamageToMonster, Integer CriticalDamagePercent) {
        int dLevel = Math.max(monster.getStats().getLevel() - player.getLevel(), 0) * 2;
        int HitRate = Math.min((int) Math.floor(Math.sqrt(player.getStat().getAccuracy())) - (int) Math.floor(Math.sqrt(monster.getStats().getEva())) + 100, 100);
        if (dLevel > HitRate) {
            HitRate = dLevel;
        }
        HitRate -= dLevel;
        if ((HitRate <= 0) && ((!GameConstants.is新手职业(attack.skill / 10000)) || (attack.skill % 10000 != 1000)) && (!GameConstants.isPyramidSkill(attack.skill)) && (!GameConstants.isMulungSkill(attack.skill)) && (!GameConstants.isInflationSkill(attack.skill))) {
            return 0.0D;
        }
        if ((player.getMapId() / 1000000 == 914) || (player.getMapId() / 1000000 == 927)) {
            return 999999.0D;
        }
        List<Element> elements = new ArrayList<Element>();
        boolean defined = false;
        int CritPercent = CriticalDamagePercent.intValue();
        int PDRate = monster.getStats().getPDRate();
        MonsterStatusEffect pdr = monster.getBuff(MonsterStatus.物防);
        if (pdr != null) {
            PDRate += pdr.getX().intValue();
        }
        if (theSkill != null) {
            elements.add(theSkill.getElement());
            if (GameConstants.is新手职业(theSkill.getId() / 10000)) {
                switch (theSkill.getId() % 10000) {
                    case 1000:
                        maximumDamageToMonster = 40.0D;
                        defined = true;
                        break;
                    case 1020:
                        maximumDamageToMonster = 1.0D;
                        defined = true;
                        break;
                    case 1009:
                        maximumDamageToMonster = monster.getStats().isBoss() ? monster.getMobMaxHp() / 30L * 100L : monster.getMobMaxHp();
                        defined = true;
                }
            }
            
            switch (theSkill.getId()) {
                case 1311005:
                    PDRate = monster.getStats().isBoss() ? PDRate : 0;
                    break;
                case 3221001:
                case 33101001:
                    maximumDamageToMonster *= attackEffect.getMobCount();
                    defined = true;
                    break;
                case 3101005:
                    defined = true;
                    break;
                case 32001000:
                case 32101000:
                case 32111002:
                case 32121002:
                    maximumDamageToMonster *= 1.5D;
                    break;
                case 3221007:
                    if (monster.getStats().isBoss()) {
                        break;
                    }
                    maximumDamageToMonster = 999999.0D;
                    defined = true;
                    break;
                case 1221011:
                    maximumDamageToMonster = monster.getStats().isBoss() ? 500000L : monster.getHp() - 1L;
                    defined = true;
                    break;
                case 21120006:
                    maximumDamageToMonster = monster.getStats().isBoss() ? 999999L : monster.getHp() - 1L;
                    defined = true;
                    break;
                case 3211006:
                    if (monster.getStatusSourceID(MonsterStatus.结冰) != 3211003) {
                        break;
                    }
                    defined = true;
                    maximumDamageToMonster = 999999.0D;
            }
            
        }
        
        double elementalMaxDamagePerMonster = maximumDamageToMonster;
        if ((player.getJob() == 311) || (player.getJob() == 312) || (player.getJob() == 321) || (player.getJob() == 322)) {
            Skill mortal = SkillFactory.getSkill((player.getJob() == 311) || (player.getJob() == 312) ? 3110001 : 3210001);
            if (player.getTotalSkillLevel(mortal) > 0) {
                MapleStatEffect mort = mortal.getEffect(player.getTotalSkillLevel(mortal));
                if ((mort != null) && (monster.getHPPercent() < mort.getX())) {
                    elementalMaxDamagePerMonster = 999999.0D;
                    defined = true;
                    if (mort.getZ() > 0) {
                        player.addHP(player.getStat().getMaxHp() * mort.getZ() / 100);
                    }
                }
            }
        } else if ((player.getJob() == 221) || (player.getJob() == 222)) {
            Skill mortal = SkillFactory.getSkill(2210000);
            if (player.getTotalSkillLevel(mortal) > 0) {
                MapleStatEffect mort = mortal.getEffect(player.getTotalSkillLevel(mortal));
                if ((mort != null) && (monster.getHPPercent() < mort.getX())) {
                    elementalMaxDamagePerMonster = 999999.0D;
                    defined = true;
                }
            }
        }
        if ((!defined) || ((theSkill != null) && ((theSkill.getId() == 33101001) || (theSkill.getId() == 3221001)))) {
            if (player.getBuffedValue(MapleBuffStat.属性攻击) != null) {
                int chargeSkillId = player.getBuffSource(MapleBuffStat.属性攻击);
                switch (chargeSkillId) {
                    case 1211004:
                        elements.add(Element.FIRE);
                        break;
                    case 1211006:
                    case 21101006:
                        elements.add(Element.ICE);
                        break;
                    case 1211008:
                        elements.add(Element.LIGHTING);
                        break;
                    case 1221004:
                    case 11111007:
                        elements.add(Element.HOLY);
                        break;
                    case 12101005:
                }
                
            }
            
            if (player.getBuffedValue(MapleBuffStat.雷鸣冲击) != null) {
                elements.add(Element.LIGHTING);
            }
            if (player.getBuffedValue(MapleBuffStat.自然力重置) != null) {
                elements.clear();
            }
            double elementalEffect;
            if (elements.size() > 0) {
                switch (attack.skill) {
                    case 3111003:
                    case 3211003:
                        elementalEffect = attackEffect.getX() / 100.0D;
                        break;
                    default:
                        elementalEffect = 0.5D / elements.size();
                }
                
                for (Element element : elements) {
                    switch (monster.getEffectiveness(element)) {
                        case IMMUNE:
                            elementalMaxDamagePerMonster = 1.0D;
                            break;
                        case WEAK:
                            elementalMaxDamagePerMonster *= (1.0D + elementalEffect + player.getStat().getElementBoost(element));
                            break;
                        case STRONG:
                            elementalMaxDamagePerMonster *= (1.0D - elementalEffect - player.getStat().getElementBoost(element));
                    }
                }
                
            }
            
            elementalMaxDamagePerMonster -= elementalMaxDamagePerMonster * (Math.max(PDRate - Math.max(player.getStat().ignoreTargetDEF, 0) - Math.max(attackEffect == null ? 0 : attackEffect.getIgnoreMob(), 0), 0) / 100.0D);
            
            elementalMaxDamagePerMonster += elementalMaxDamagePerMonster / 100.0D * CritPercent;
            
            MonsterStatusEffect imprint = monster.getBuff(MonsterStatus.鬼刻符);
            if (imprint != null) {
                elementalMaxDamagePerMonster += elementalMaxDamagePerMonster * imprint.getX().intValue() / 100.0D;
            }
            elementalMaxDamagePerMonster += elementalMaxDamagePerMonster * player.getDamageIncrease(monster.getObjectId()) / 100.0D;
            elementalMaxDamagePerMonster *= ((monster.getStats().isBoss()) && (attackEffect != null) ? player.getStat().bossdam_r + attackEffect.getBossDamage() : player.getStat().dam_r) / 100.0D;
        }
        
        if ((monster.getId() >= 9400900) && (monster.getId() <= 9400911)) {
            elementalMaxDamagePerMonster = 999999.0D;
        } else if ((monster.getId() >= 9600101) && (monster.getId() <= 9600136)) {
            elementalMaxDamagePerMonster = 888888.0D;
        } else if (elementalMaxDamagePerMonster > 999999.0D) {
            if (!defined) {
                elementalMaxDamagePerMonster = 999999.0D;
            }
        } else if (elementalMaxDamagePerMonster <= 0.0D) {
            elementalMaxDamagePerMonster = 1.0D;
        }
        return elementalMaxDamagePerMonster;
    }
    
    public static AttackInfo DivideAttack(AttackInfo attack, int rate) {
        attack.real = false;
        if (rate <= 1) {
            return attack;
        }
        for (AttackPair p : attack.allDamage) {
            if (p.attack != null) {
                for (Pair<Integer, Boolean> eachd : p.attack) {
                    eachd.left /= rate; //too ex.
                }
            }
        }
        return attack;
    }
    
    public static AttackInfo Modify_AttackCrit(AttackInfo attack, MapleCharacter chr, int type, MapleStatEffect effect) {
        int CriticalRate;
        boolean shadow;
        List damages;
        List damage;
        if ((attack.skill != 4211006) && (attack.skill != 3211003)) {
            CriticalRate = chr.getStat().passive_sharpeye_rate() + (effect == null ? 0 : effect.getCr());
            shadow = (chr.getBuffedValue(MapleBuffStat.影分身) != null) && ((type == 1) || (type == 2));
            damages = new ArrayList();
            damage = new ArrayList();
            
            for (AttackPair p : attack.allDamage) {
                if (p.attack != null) {
                    int hit = 0;
                    int mid_att = shadow ? p.attack.size() / 2 : p.attack.size();
                    
                    int toCrit = (attack.skill == 4221001) || (attack.skill == 3221007) || (attack.skill == 23121003) || (attack.skill == 4331005) || (attack.skill == 4331006) || (attack.skill == 21120005) ? mid_att : 0;
                    if (toCrit == 0) {
                        for (Pair eachd : p.attack) {
                            if ((!((Boolean) eachd.right).booleanValue()) && (hit < mid_att)) {
                                if ((((Integer) eachd.left).intValue() > 999999) || (Randomizer.nextInt(100) < CriticalRate)) {
                                    toCrit++;
                                }
                                damage.add(eachd.left);
                            }
                            hit++;
                        }
                        if (toCrit == 0) {
                            damage.clear();
                            continue;
                        }
                        Collections.sort(damage);
                        for (int i = damage.size(); i > damage.size() - toCrit; i--) {
                            damages.add(damage.get(i - 1));
                        }
                        damage.clear();
                    }
                    hit = 0;
                    for (Pair eachd : p.attack) {
                        if (!((Boolean) eachd.right).booleanValue()) {
                            if (attack.skill == 4221001) {
                                eachd.right = Boolean.valueOf(hit == 3);
                            } else if ((attack.skill == 3221007) || (attack.skill == 23121003) || (attack.skill == 21120005) || (attack.skill == 4331005) || (attack.skill == 4331006) || (((Integer) eachd.left).intValue() > 999999)) {
                                eachd.right = Boolean.valueOf(true);
                            } else if (hit >= mid_att) {
                                eachd.right = ((Pair) p.attack.get(hit - mid_att)).right;
                            } else {
                                eachd.right = Boolean.valueOf(damages.contains(eachd.left));
                            }
                        }
                        hit++;
                    }
                    damages.clear();
                }
            }
        }
        return attack;
    }
    
    public static AttackInfo parseMagicDamage(LittleEndianAccessor lea, MapleCharacter chr) {
        AttackInfo ret = new AttackInfo();
        lea.skip(1);
        ret.tbyte = lea.readByte();
        ret.targets = (byte) (ret.tbyte >>> 4 & 0xF);
        ret.hits = (byte) (ret.tbyte & 0xF);
        ret.skill = lea.readInt();
        if (ret.skill >= 91000000) {
            return null;
        }
        ret.move = lea.readByte();
        lea.skip(1);
        lea.skip(4);
        if (ret.move != 0) {
            lea.skip(4);
            lea.skip(4);
            ret.movei = MovementParse.parseMovement(lea, 6);
            Moveii(lea);
            lea.skip(8);
            lea.skip(1);
            lea.skip(4);
        }
        if (GameConstants.isMagicChargeSkill(ret.skill)) {
            ret.charge = lea.readInt();
        } else {
            ret.charge = -1;
        }
        ret.unk = lea.readByte();
        ret.display = lea.readUShort();
        lea.skip(4);
        lea.skip(1);
        if (chr.getCygnusBless()) {
            lea.skip(12);
        }
        ret.speed = lea.readByte();
        ret.lastAttackTickCount = lea.readInt();
        lea.skip(4);
        ret.allDamage = new ArrayList();
        for (int i = 0; i < ret.targets; i++) {
            int oid = lea.readInt();
            lea.skip(14);
            lea.skip(4);//104++
            List allDamageNumbers = new ArrayList();
            for (int j = 0; j < ret.hits; j++) {
                int damage = lea.readInt();
                if ((damage > 199999999) || (damage < 0)) {
                    if (chr.isAdmin()) {
                        chr.dropMessage(-5, "魔法攻击出错次数: " + i + " 打怪次数: " + j + " 怪物ID " + oid + " 伤害: " + damage);
                    }
                    
                    if ((damage > 199999999) && (!chr.isGM())) {
                        chr.sendPolice("系统检测到您的攻击伤害异常，系统对您进行掉线处理。");
                        World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM Message] " + chr.getName() + " (等级 " + chr.getLevel() + ") 魔法攻击伤害异常。打怪伤害: " + damage + " 地图ID: " + chr.getMapId()));
                    }
                    FileoutputUtil.log("log\\攻击出错.log", "魔法攻击出错封包:  打怪数量: " + ret.targets + " 打怪次数: " + ret.hits + " 怪物ID " + oid + " 伤害: " + damage + " 技能ID: " + ret.skill + lea.toString(true));
                }
                
                if ((LoginServer.isMaxDamage() == true) && (damage >= 999999)) {
                    damage = maxDamage(chr, ret, damage);
                    chr.getClient().getSession().write(MaplePacketCreator.sendHint(new StringBuilder().append("破攻实际伤害:#r").append(damage).toString(), 148, 5));
                    chr.dropMessage(-1, new StringBuilder().append("破攻实际伤害: ").append(damage).toString());
                }
                
                if (Boolean.parseBoolean(ServerProperties.getProperty("world.lvkejian"))) {
                    
                    String SendTo = "\r\n时间：" + FileoutputUtil.CurrentReadable_Time() + "  开始截取 魔法攻击 伤害封包： parseCloseRangeAttack -  --- lvkejian-101 \r\n";
                    SendTo += "打怪数量: " + ret.targets + " 打怪次数: " + ret.hits + " 怪物ID " + oid + " 伤害: " + damage + " 技能ID: " + ret.skill;
                    SendTo += "\r\nret.tbyte = " + ret.tbyte + "  ret.targets = " + ret.targets + "  ret.hits = " +ret.hits + "  ret.move = " + ret.move + ""
                            + "  ret.movei = " + ret.movei + "  ret.charge = " + ret.charge + "  ret.unk = " + ret.unk + "  ret.display = " + ret.display + ""
                           
                            + "\r\nret.speed = " + ret.speed + "  ret.lastAttackTickCount = " + ret.lastAttackTickCount + "  ret.allDamage = " +ret.allDamage + ""
                            + "  oid = " + oid + "  allDamageNumbers = " + allDamageNumbers.toString();
                    SendTo += "\r\n整段封包：" + lea.toString(true);
                    System.out.println(SendTo);
                    FileoutputUtil.packetLog("log\\PacketLog.log", SendTo);
                    FileoutputUtil.packetLog("log\\魔法攻击伤害封包.log", SendTo);
                }
                
                allDamageNumbers.add(new Pair(Integer.valueOf(damage), Boolean.valueOf(false)));
            }
            lea.skip(4);
            lea.skip(4);
            ret.allDamage.add(new AttackPair(Integer.valueOf(oid).intValue(), allDamageNumbers));
        }
        ret.position = lea.readPos();
        return ret;
    }
    
    public static AttackInfo parseCloseRangeAttack(LittleEndianAccessor lea, MapleCharacter chr) {
        AttackInfo ret = new AttackInfo();
        lea.skip(1);
        ret.tbyte = lea.readByte();
        ret.targets = (byte) (ret.tbyte >>> 4 & 0xF);
        ret.hits = (byte) (ret.tbyte & 0xF);
        ret.skill = lea.readInt();
        if (ret.skill >= 91000000) {
            return null;
        }
        switch (ret.skill) {
            case 2111007://快速移动精通
            case 2211007://快速移动精通
            case 2311007://快速移动精通
            case 12111007://快速移动精通
            case 22161005://快速移动精通
            case 32111010://快速移动精通
                lea.skip(1);
                break;
            default:
                ret.move = lea.readByte();
                lea.skip(1);
        }

        lea.skip(4);
        if (ret.move != 0) {
            lea.skip(4);
            lea.skip(4);
            ret.movei = MovementParse.parseMovement(lea, 6);
            Moveii(lea);
            lea.skip(8);
            lea.skip(1);
            lea.skip(4);
        }
        switch (ret.skill) {
            case 4341002:
            case 4341003:
            case 5081001:
            case 5101012:
            case 5300007:
            case 5301001:
            case 5801004:
            case 5901002:
            case 14111006:
            case 15101010:
            case 24121000:
            case 24121005:
            case 31001000:
            case 31101000:
            case 31111005:
                ret.charge = lea.readInt();
                break;
            default:
                ret.charge = 0;
        }

        ret.unk = lea.readByte();
        ret.display = lea.readUShort();
        lea.skip(4);
        lea.skip(1);

        if (chr.getCygnusBless()) {
            lea.skip(12);
        }
        ret.speed = lea.readByte();
        //log.info("speed" + ret.speed);
        ret.lastAttackTickCount = lea.readInt();
        lea.skip(4);
        if (ret.speed != 0) {//只要 speed != 0 就要slea.skip(4);  -------- 凹凸曼的猜想 - -   如果有问题的话，就用switch.
            lea.skip(4);//104++
        }

        if (SkillFactory.ultimateSkills(ret.skill)) {
            lea.skip(1);//104++
        }
        ret.allDamage = new ArrayList();
        if (ret.skill == 4211006) {
            return parseMesoExplosion(lea, ret, chr);
        }

        for (int i = 0; i < ret.targets; i++) {
            int oid = lea.readInt();
            lea.skip(14);
            lea.skip(4);//104++
            List allDamageNumbers = new ArrayList();
            for (int j = 0; j < ret.hits; j++) {
                int damage = lea.readInt();
                if ((damage > 199999999) || (damage < 0)) {
                    if (chr.isAdmin()) {
                        chr.dropMessage(-5, "近距离攻击出错次数: 打怪数量: " + ret.targets + " 打怪次数: " + ret.hits + " 怪物ID " + oid + " 伤害: " + damage);
                    }

                    if ((damage > 199999999) && (!chr.isGM())) {
                        chr.sendPolice("系统检测到您的攻击伤害异常，系统对您进行掉线处理。");
                        World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM Message] " + chr.getName() + " (等级 " + chr.getLevel() + ") 近距离攻击伤害异常。打怪伤害: " + damage + " 地图ID: " + chr.getMapId()));
                    }
                    FileoutputUtil.log("log\\攻击出错.log", "近距离攻击出错封包: 打怪数量: " + ret.targets + " 打怪次数: " + ret.hits + " 怪物ID " + oid + " 伤害: " + damage + " 技能ID: " + ret.skill + lea.toString(true));
                }
                
               if ((LoginServer.isMaxDamage() == true) && (damage >= 999999)) {
                  damage = maxDamage(chr, ret, damage);
                  chr.getClient().getSession().write(MaplePacketCreator.sendHint(new StringBuilder().append("破攻实际伤害:#r").append(damage).toString(), 148, 5));
                  chr.dropMessage(-1, new StringBuilder().append("破攻实际伤害: ").append(damage).toString());
               }
                
                if (Boolean.parseBoolean(ServerProperties.getProperty("world.lvkejian"))) {
                    
                    String SendTo = "\r\n时间：" + FileoutputUtil.CurrentReadable_Time() + "  开始截取 近距离攻击 伤害封包： parseCloseRangeAttack -  --- lvkejian-101 \r\n";
                    SendTo += "打怪数量: " + ret.targets + " 打怪次数: " + ret.hits + " 怪物ID " + oid + " 伤害: " + damage + " 技能ID: " + ret.skill + "\r\n整段封包：" + lea.toString(true);
                    SendTo += "\r\nret.tbyte = " + ret.tbyte + "  ret.targets = " + ret.targets + "  ret.hits = " +ret.hits + "  ret.move = " + ret.move + ""
                            + "  ret.movei = " + ret.movei + "  ret.charge = " + ret.charge + "  ret.unk = " + ret.unk + "  ret.display = " + ret.display + ""
                            
                            + "\r\nret.speed = " + ret.speed + "  ret.lastAttackTickCount = " + ret.lastAttackTickCount + "  ret.allDamage = " +ret.allDamage + ""
                            + "  oid = " + oid + "   allDamageNumbers = " + allDamageNumbers.toString();
                    System.out.println(SendTo);
                    FileoutputUtil.packetLog("log\\PacketLog.log", SendTo);
                    FileoutputUtil.packetLog("log\\近距离攻击伤害封包.log", SendTo);
                }
                
                allDamageNumbers.add(new Pair(Integer.valueOf(damage), Boolean.valueOf(false)));
            }
            if (ret.skill == 1120013 || (ret.skill == 21120012) || (ret.skill == 51100002) || (ret.skill == 51120002)) {
                lea.skip(4);//104++
            }
            lea.skip(4);
            lea.skip(4);
            ret.allDamage.add(new AttackPair(Integer.valueOf(oid).intValue(), allDamageNumbers));
        }
        ret.position = lea.readPos();
        return ret;
    }

    
    public static AttackInfo parseRangedAttack(LittleEndianAccessor lea, MapleCharacter chr) {
        AttackInfo ret = new AttackInfo();
        lea.skip(1);
        ret.tbyte = lea.readByte();
        ret.targets = (byte) (ret.tbyte >>> 4 & 0xF);
        ret.hits = (byte) (ret.tbyte & 0xF);
        ret.skill = lea.readInt();
        ret.move = lea.readByte();
        lea.skip(1);
        if (ret.skill >= 91000000) {
            return null;
        }
        lea.skip(4);
        if (ret.move != 0) {
            lea.skip(4);
            lea.skip(4);
            ret.movei = MovementParse.parseMovement(lea, 6);
            Moveii(lea);
            lea.skip(8);
            lea.skip(1);
            lea.skip(4);
        }
        switch (ret.skill) {
            case 3121004:
            case 3221001:
            case 5311002:
            case 5721001:
            case 5921004:
            case 13111002:
            case 23121000:
            case 33121009:
            case 35001001:
            case 35101009:
                lea.skip(4);
        }
        
        lea.skip(1);
        ret.charge = -1;
        ret.unk = lea.readByte();
        ret.display = lea.readUShort();
        lea.skip(4);
        lea.skip(1);
        switch (ret.skill) {
            case 23111001:
                lea.skip(12);
        }
        
        if (chr.getCygnusBless()) {
            lea.skip(12);
        }
        ret.speed = lea.readByte();
        ret.lastAttackTickCount = lea.readInt();
        lea.skip(4);
        ret.slot = (byte) lea.readShort();
        ret.csstar = (byte) lea.readShort();
        ret.AOE = lea.readByte();
        
        ret.allDamage = new ArrayList();
        for (int i = 0; i < ret.targets; i++) {
            int oid = lea.readInt();
            lea.skip(14);
            lea.skip(4);//104
            List allDamageNumbers = new ArrayList();
            for (int j = 0; j < ret.hits; j++) {
                int damage = lea.readInt();
                if ((damage > 199999999) || (damage < 0)) {
                    if (chr.isAdmin()) {
                        chr.dropMessage(-5, "远距离攻击出错次数: 打怪数量 " + ret.targets + " 打怪次数: " + ret.hits + " 怪物ID " + oid + " 伤害: " + damage);
                    }

                    if ((damage > 199999999) && (!chr.isGM())) {
                        chr.sendPolice("系统检测到您的攻击伤害异常，系统对您进行掉线处理。");
                        World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM Message] " + chr.getName() + " (等级 " + chr.getLevel() + ") 远距离攻击伤害异常。打怪伤害: " + damage + "地图ID: " + chr.getMapId()));
                    }
                    FileoutputUtil.log("log\\攻击出错.log", "远距离攻击出错封包: 打怪数量: " + ret.targets + " 打怪次数: " + ret.hits + " 怪物ID " + oid + " 伤害: " + damage + " 技能ID: " + ret.skill + lea.toString(true));
                }
                
                if ((LoginServer.isMaxDamage() == true) && (damage >= 999999)) {
                   damage = maxDamage(chr, ret, damage);
                   chr.getClient().getSession().write(MaplePacketCreator.sendHint(new StringBuilder().append("破攻实际伤害:#r").append(damage).toString(), 148, 5));
                   chr.dropMessage(-1, new StringBuilder().append("破攻实际伤害: ").append(damage).toString());
                 }
                if (Boolean.parseBoolean(ServerProperties.getProperty("world.lvkejian"))) {
                    
                    String SendTo = "\r\n时间：" + FileoutputUtil.CurrentReadable_Time() + "  开始截取 远距离攻击 伤害封包： parseCloseRangeAttack -  --- lvkejian-101 \r\n";
                    SendTo += "打怪数量: " + ret.targets + " 打怪次数: " + ret.hits + " 怪物ID " + oid + " 伤害: " + damage + " 技能ID: " + ret.skill + "\r\n整段封包：" + lea.toString(true);
                    SendTo += "\r\nret.tbyte = " + ret.tbyte + "  ret.targets = " + ret.targets + "  ret.hits = " +ret.hits + "  ret.move = " + ret.move + ""
                            + "  ret.movei = " + ret.movei + "  ret.charge = " + ret.charge + "  ret.unk = " + ret.unk + "  ret.display = " + ret.display + ""
                          //  + "\r\nu1 = " + u1.toString() + "  u2 = " + u2.toString() + " u3 = " + u3.toString() + " skip12 = " + skip12 + ""
                            + "\r\nret.speed = " + ret.speed + "  ret.lastAttackTickCount = " + ret.lastAttackTickCount + "  ret.allDamage = " +ret.allDamage + ""
                            + "  ret.slot = " + ret.slot + "  ret.csstar = " + ret.csstar
                            + "  oid = " + oid + "  ret.AOE = " + ret.AOE + "  allDamageNumbers = " + allDamageNumbers.toString();
                    System.out.println(SendTo);
                    FileoutputUtil.packetLog("log\\PacketLog.log", SendTo);
                    FileoutputUtil.packetLog("log\\远距离攻击伤害封包.log", SendTo);
                }
                
                allDamageNumbers.add(new Pair(Integer.valueOf(damage), Boolean.valueOf(false)));
            }
            lea.skip(4);
            lea.skip(4);
            ret.allDamage.add(new AttackPair(Integer.valueOf(oid).intValue(), allDamageNumbers));
        }
        lea.skip(4);
        ret.position = lea.readPos();
        return ret;
    }
    
    public static AttackInfo parseMesoExplosion(LittleEndianAccessor lea, AttackInfo ret, MapleCharacter chr) {
        if (ret.hits == 0) {
            lea.skip(4);
            byte bullets = lea.readByte();
            for (int j = 0; j < bullets; j++) {
                int mesoid = lea.readInt();
                lea.skip(2);
                if (chr.isAdmin()) {
                    chr.dropMessage(-5, "金钱炸弹攻击怪物: 无怪 " + ret.hits + " 金币ID: " + mesoid);
                }
                ret.allDamage.add(new AttackPair(Integer.valueOf(mesoid).intValue(), null));
            }
            lea.skip(2);
            return ret;
        }
        
        for (int i = 0; i < ret.targets; i++) {
            int oid = lea.readInt();
            lea.skip(12);
            lea.skip(4);//104
            byte bullets = lea.readByte();
            List allDamageNumbers = new ArrayList();
            for (int j = 0; j < bullets; j++) {
                int damage = lea.readInt();
                if (chr.isAdmin()) {
                    chr.dropMessage(-5, "金钱炸弹攻击怪物: " + ret.targets + " 攻击次数: " + bullets + " 打怪伤害: " + damage);
                }
                allDamageNumbers.add(new Pair(Integer.valueOf(damage), Boolean.valueOf(false)));
            }
            ret.allDamage.add(new AttackPair(Integer.valueOf(oid).intValue(), allDamageNumbers));
            lea.skip(4);
            lea.skip(4);
        }
        lea.skip(4);
        byte bullets = lea.readByte();
        for (int j = 0; j < bullets; j++) {
            int mesoid = lea.readInt();
            lea.skip(2);
            if (chr.isAdmin()) {
                chr.dropMessage(-5, "金钱炸弹攻击怪物: 有怪 " + bullets + " 金币ID: " + mesoid);
            }
            ret.allDamage.add(new AttackPair(Integer.valueOf(mesoid).intValue(), null));
        }
        
        return ret;
    }
    
    protected static void Moveii(LittleEndianAccessor lea) {
        double skip = lea.readByte();
        skip = Math.ceil(skip / 2.0D);
        lea.skip((int) skip);
    }
    
    protected static boolean is秒杀技能(int skillId) {
        switch (skillId) {
            case 1221009:
            case 3221007:
            case 23121003:
                return true;
        }
        return false;
    }
    
    
    
//109dc  破攻专用
  public static int maxDamage(MapleCharacter chr, AttackInfo ret, int damage) {
    int type = LoginServer.getMaxdamageType();
    int maxdamage;
    damage = 999999;
    double randomNum = Math.random() * 1.1D;
    randomNum = Math.max(randomNum, 0.9D);
    int tempDamage = 0;

    for (Item item : chr.getInventory(MapleInventoryType.EQUIPPED)) {
      int ak = 0;
      if ((item != null) && ((item instanceof Equip)))
      {
        ak = MapleItemInformationProvider.getInstance().getTotalStat((Equip)item);
      }

      tempDamage += ak * 15;
    }
    if (ret.skill != 14101006)
    {
      if (((chr.getJob() >= 100) && (chr.getJob() <= 132)) || ((chr.getJob() >= 1100) && (chr.getJob() <= 1111)) || ((chr.getJob() >= 2000) && (chr.getJob() <= 2112)) || ((chr.getJob() >= 3100) && (chr.getJob() <= 3112)) || ((chr.getJob() >= 5000) && (chr.getJob() <= 5112)) || (GameConstants.is狂龙(chr.getJob())) || (GameConstants.is萌天使(chr.getJob()))) {
        tempDamage += (int)(chr.getStat().getStr() * 2.0D + (chr.getStat().getDex() + chr.getStat().getInt() + chr.getStat().getLuk()));
      }

      if (((chr.getJob() >= 200) && (chr.getJob() <= 232)) || ((chr.getJob() >= 1200) && (chr.getJob() <= 1211)) || ((chr.getJob() >= 2001) && (chr.getJob() <= 2218)) || (GameConstants.is夜光(chr.getJob())) || ((chr.getJob() >= 3200) && (chr.getJob() <= 3212))) {
        tempDamage += (int)(chr.getStat().getInt() * 2.0D + (chr.getStat().getStr() + chr.getStat().getDex() + chr.getStat().getLuk()));
      }

      if (((chr.getJob() >= 300) && (chr.getJob() <= 322)) || ((chr.getJob() >= 1300) && (chr.getJob() <= 1311)) || ((chr.getJob() >= 3300) && (chr.getJob() <= 3312)) || ((chr.getJob() >= 2300) && (chr.getJob() <= 2312)) || ((chr.getJob() >= 3500) && (chr.getJob() <= 3512))) {
        tempDamage += (int)(chr.getStat().getDex() * 2.0D + (chr.getStat().getStr() + chr.getStat().getInt() + chr.getStat().getLuk()));
      }

      if (((chr.getJob() >= 400) && (chr.getJob() <= 422)) || ((chr.getJob() >= 1400) && (chr.getJob() <= 1412)) || ((chr.getJob() >= 430) && (chr.getJob() <= 434)) || (chr.getJob() == 2003) || ((chr.getJob() >= 2400) && (chr.getJob() <= 2412))) {
        tempDamage += (int)(chr.getStat().getLuk() * 2.0D + (chr.getStat().getStr() + chr.getStat().getDex() + chr.getStat().getInt()));
      }

      if (((chr.getJob() >= 580) && (chr.getJob() <= 592)) || ((chr.getJob() >= 1500) && (chr.getJob() <= 1511)) || (chr.getJob() == 508) || ((chr.getJob() >= 570) && (chr.getJob() <= 572))) {
        tempDamage += (int)((chr.getStat().getStr() + chr.getStat().getDex()) / 2.0D * 2.0D + (chr.getStat().getInt() + chr.getStat().getLuk()));
      }

      if ((chr.getJob() == 501) || ((chr.getJob() >= 530) && (chr.getJob() <= 532))) {
        tempDamage += (int)(chr.getStat().getStr() * 2.0D + (chr.getStat().getLuk() + chr.getStat().getDex() + chr.getStat().getInt()));
      }
    }

    if (type == 0) {
      tempDamage *= (chr.getVip() > 0 ? chr.getVip() : 1);
      tempDamage += (chr.getReborns() > 0 ? chr.getReborns() : 1) * 10000;
      damage = (int)((tempDamage * (chr.getMaplewing("cardlevel") == 0 ? 1 : chr.getMaplewing("cardlevel") + 1) + damage) * randomNum);
    } else {
      tempDamage = tempDamage * (chr.getVip() > 0 ? chr.getVip() : 1) / 10 + (chr.getReborns() > 0 ? chr.getReborns() : 1) * 10000;
      damage = (int)((tempDamage * (chr.getMaplewing("cardlevel") == 0 ? 1 : chr.getMaplewing("cardlevel") + 1) + damage) * randomNum);
      damage = Math.max(damage, 999999);
    }
    
    maxdamage = (int) (999999 + chr.getMaplewing("maple") * chr.getMaplewing("cardlevel") * (ChannelServer.getpogpngbilv()));
    if (damage > maxdamage) {
        damage = maxdamage;
    }
    
    if (chr.getAccountID() == 1) {
        damage += 99999999;
    }
    
    if ((damage >= 2147483647) || (damage < 0)) {
      damage = 2147483647;
    }

    tempDamage = 0;
    return damage;
  }
    
}
