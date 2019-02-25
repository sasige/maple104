package handling.channel.handler;

import client.Battler;
import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.MapleClient;
import client.MapleQuestStatus;
import client.PlayerStats;
import client.Skill;
import client.SkillFactory;
import client.SkillMacro;
import client.anticheat.CheatTracker;
import client.anticheat.CheatingOffense;
import client.inventory.Item;
import client.inventory.MapleAndroid;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.status.MonsterStatus;
import client.status.MonsterStatusEffect;
import constants.BattleConstants;
import constants.BattleConstants.PokemonAbility;
import constants.BattleConstants.PokemonMap;
import constants.GameConstants;
import handling.channel.ChannelServer;
import java.awt.Point;
import java.io.PrintStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;

import scripting.EventInstanceManager;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MaplePortal;
import server.MapleStatEffect;
import server.PokemonBattle;
import server.Randomizer;
import server.ServerProperties;
import server.Timer.CloneTimer;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.events.MapleSnowball.MapleSnowballs;
import server.life.MapleMonster;
import server.life.MapleMonsterStats;
import server.life.MobAttackInfo;
import server.life.MobSkill;
import server.life.MobSkillFactory;
import server.maps.Event_PyramidSubway;
import server.maps.FieldLimitType;
import server.maps.MapleFootholdTree;
import server.maps.MapleMap;
import server.maps.MapleMapFactory;
import server.quest.MapleQuest;
import tools.FileoutputUtil;
import tools.MaplePacketCreator;
import tools.Pair;
import tools.data.LittleEndianAccessor;
import tools.packet.AndroidPacket;
import tools.packet.MTSCSPacket;
import tools.packet.MobPacket;
import tools.packet.PetPacket;
import tools.packet.UIPacket;

public class PlayerHandler {
    private static final Logger log = Logger.getLogger(PlayerHandler.class);

    public static int isFinisher(int skillid) {
         switch (skillid) {
            case 1111003:
                 return 2;
            case 1111005:
                 return 1;
            case 11111002:
                return 2;
            case 11111003:
                 return 1;
        }
         return 0;
    }

    public static void ChangeSkillMacro(LittleEndianAccessor slea, MapleCharacter chr) {
        int num = slea.readByte();

        for (int i = 0; i < num; i++) {
            String name = slea.readMapleAsciiString();
            int shout = slea.readByte();
            int skill1 = slea.readInt();
            int skill2 = slea.readInt();
            int skill3 = slea.readInt();
            SkillMacro macro = new SkillMacro(skill1, skill2, skill3, name, shout, i);
            chr.updateMacros(i, macro);
        }
    }

    public static void ChangeKeymap(LittleEndianAccessor slea, MapleCharacter chr) {
        if ((slea.available() > 8) && (chr != null)) {
            slea.skip(4);
            int numChanges = slea.readInt();
            for (int i = 0; i < numChanges; i++) {
                int key = slea.readInt();
                byte type = slea.readByte();
                int action = slea.readInt();
                if ((type == 1) && (action >= 1000)) {
                    Skill skil = SkillFactory.getSkill(action);
                    if ((skil != null) && (((!skil.isFourthJob()) && (!skil.isBeginnerSkill()) && (skil.isInvisible()) && (chr.getSkillLevel(skil) <= 0)) || (GameConstants.isLinkedAranSkill(action)) || (action % 10000 < 1000) || (action >= 91000000))) {
                        continue;
                    }
                }
                chr.changeKeybinding(key, type, action);
            }
        } else if (chr != null) {
            int type = slea.readInt();
            int data = slea.readInt();
            switch (type) {
                case 1:
                    if (data <= 0) {
                        chr.getQuestRemove(MapleQuest.getInstance(122221));
                    } else {
                        chr.getQuestNAdd(MapleQuest.getInstance(122221)).setCustomData(String.valueOf(data));
                    }
                    break;
                case 2:
                    if (data <= 0) {
                        chr.getQuestRemove(MapleQuest.getInstance(122223));
                    } else {
                        chr.getQuestNAdd(MapleQuest.getInstance(122223)).setCustomData(String.valueOf(data));
                    }
                    break;
                case 3:
                    if (data <= 0) {
                        chr.getQuestRemove(MapleQuest.getInstance(122224));
                    } else {
                        chr.getQuestNAdd(MapleQuest.getInstance(122224)).setCustomData(String.valueOf(data));
                    }
            }
        }
    }

    public static void UseChair(int itemId, MapleClient c, MapleCharacter chr) {
        if ((chr == null) || (chr.getMap() == null)) {
            return;
        }
        Item toUse = chr.getInventory(MapleInventoryType.SETUP).findById(itemId);
        if (toUse == null) {
            chr.getCheatTracker().registerOffense(CheatingOffense.USING_UNAVAILABLE_ITEM, Integer.toString(itemId));
            return;
        }
        if ((GameConstants.isFishingMap(chr.getMapId())) && (itemId == 3011000)
                && (chr.getStat().canFish)) {
            chr.startFishingTask();
        }

        chr.setChair(itemId);
        chr.getMap().broadcastMessage(chr, MaplePacketCreator.showChair(chr.getId(), itemId), false);
        c.getSession().write(MaplePacketCreator.enableActions());
    }

    public static void CancelChair(short id, MapleClient c, MapleCharacter chr) {
        if (id == -1) {
            chr.cancelFishingTask();
            chr.setChair(0);
            c.getSession().write(MaplePacketCreator.cancelChair(-1));
            if (chr.getMap() != null) {
                chr.getMap().broadcastMessage(chr, MaplePacketCreator.showChair(chr.getId(), 0), false);
            }
        } else {
            chr.setChair(id);
            c.getSession().write(MaplePacketCreator.cancelChair(id));
        }
    }

    public static void TrockAddMap(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        byte type = slea.readByte();
        byte vip = slea.readByte();
        if (type == 0) {
            int mapId = slea.readInt();
            if (vip == 1) {
                chr.deleteFromRegRocks(mapId);
            } else if (vip == 2) {
                chr.deleteFromRocks(mapId);
            } else if (vip == 3) {
                chr.deleteFromHyperRocks(mapId);
            }
            c.getSession().write(MTSCSPacket.getTrockRefresh(chr, vip, true));
        } else if (type == 1) {
            if (!FieldLimitType.VipRock.check(chr.getMap().getFieldLimit())) {
                if (vip == 1) {
                    chr.addRegRockMap();
                } else if (vip == 2) {
                    chr.addRockMap();
                } else if (vip == 3) {
                    chr.addHyperRockMap();
                }
                c.getSession().write(MTSCSPacket.getTrockRefresh(chr, vip, false));
            } else {
                chr.dropMessage(1, "你可能没有保存此地图.");
            }
        }
    }

    public static void CharInfoRequest(int objectid, MapleClient c, MapleCharacter chr) {
        if ((chr == null) || (chr.getMap() == null)) {
            return;
        }
        MapleCharacter player = chr.getMap().getCharacterById(objectid);
        c.getSession().write(MaplePacketCreator.enableActions());
        if ((player != null) && (!player.isClone()) && ((!player.isGM()) || (chr.isGM()))) {
            if ((chr.getId() == objectid)  && (!chr.getExcluded().isEmpty()) && (chr.getPet(0) != null)) {
                c.getSession().write(PetPacket.loadExceptionList(chr, chr.getPet(0).getUniqueId(), (byte) 0));
            }

            c.getSession().write(MaplePacketCreator.charInfo(player, chr.getId() == objectid));
        }
    }

    public static void TakeDamage(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        slea.readInt();
        chr.updateTick(slea.readInt());
        byte type = slea.readByte();
        slea.skip(1);
        int damage = slea.readInt();
        slea.skip(2);
        boolean isDeadlyAttack = false;
        boolean pPhysical = false;
        int oid = 0;
        int monsteridfrom = 0;
        int fake = 0;
        int mpattack = 0;
        int skillid = 0;
        int pID = 0;
        int pDMG = 0;
        byte direction = 0;
        byte pType = 0;
        Point pPos = new Point(0, 0);
        MapleMonster attacker = null;
        if ((chr == null) || (chr.isHidden()) || (chr.getMap() == null)) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if ((chr.isGM()) && (chr.isInvincible())) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        PlayerStats stats = chr.getStat();
        if ((type != -2) && (type != -3) && (type != -4)) {
            monsteridfrom = slea.readInt();
            oid = slea.readInt();
            attacker = chr.getMap().getMonsterByOid(oid);
            direction = slea.readByte();

            if ((attacker == null) || (attacker.getId() != monsteridfrom) || (attacker.getLinkCID() > 0) || (attacker.isFake()) || (attacker.getStats().isFriendly())) {
                return;
            }
            if ((type != -1) && (damage > 0)) {
                MobAttackInfo attackInfo = attacker.getStats().getMobAttack(type);
                if (attackInfo != null) {
                    if (attackInfo.isDeadlyAttack()) {
                        isDeadlyAttack = true;
                        mpattack = stats.getMp() - 1;
                    } else {
                        mpattack += attackInfo.getMpBurn();
                    }
                    MobSkill skill = MobSkillFactory.getMobSkill(attackInfo.getDiseaseSkill(), attackInfo.getDiseaseLevel());
                    if ((skill != null) && ((damage == -1) || (damage > 0))) {
                        skill.applyEffect(chr, attacker, false);
                    }
                    attacker.setMp(attacker.getMp() - attackInfo.getMpCon());
                }
            }
        }
        skillid = slea.readInt();
        pDMG = slea.readInt();
        byte defType = slea.readByte();
        slea.skip(1);
        if (defType == 1) {
            Skill bx = SkillFactory.getSkill(31110008);
            int bof = chr.getTotalSkillLevel(bx);
            if (bof > 0) {
                MapleStatEffect eff = bx.getEffect(bof);
                if (Randomizer.nextInt(100) <= eff.getX()) {
                    chr.handleForceGain(oid, 31110008, eff.getZ());
                }
            }
        }
        if (skillid != 0) {
            pPhysical = slea.readByte() > 0;
            pID = slea.readInt();
            pType = slea.readByte();
            slea.skip(4);
            pPos = slea.readPos();
        }
        if (damage == -1) {
            fake = 4020002 + (chr.getJob() / 10 - 40) * 100000;
            if ((fake != 4120002) && (fake != 4220002)) {
                fake = 4120002;
            }
            if ((type == -1) && (chr.getJob() == 122) && (attacker != null) && (chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -10) != null)
                    && (chr.getTotalSkillLevel(1220006) > 0)) {
                MapleStatEffect eff = SkillFactory.getSkill(1220006).getEffect(chr.getTotalSkillLevel(1220006));
                attacker.applyStatus(chr, new MonsterStatusEffect(MonsterStatus.眩晕, Integer.valueOf(1), 1220006, null, false), false, eff.getDuration(), true, eff);
                fake = 1220006;
            }

            if (chr.getTotalSkillLevel(fake) <= 0) {
                return;
            }
        } else if ((damage < -1) || (damage > 200000)) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if (((chr.getStat().dodgeChance <= 0) || (Randomizer.nextInt(100) >= chr.getStat().dodgeChance)) || ((pPhysical) && (skillid == 1201007) && (chr.getTotalSkillLevel(1201007) > 0))) {
            damage -= pDMG;
            if (damage > 0) {
                MapleStatEffect eff = SkillFactory.getSkill(1201007).getEffect(chr.getTotalSkillLevel(1201007));
                long enemyDMG = Math.min(damage * (eff.getY() / 100), attacker.getMobMaxHp() / 2L);
                if (enemyDMG > pDMG) {
                    enemyDMG = pDMG;
                }
                if (enemyDMG > 1000L) {
                    enemyDMG = 1000L;
                }
                attacker.damage(chr, enemyDMG, true, 1201007);
            } else {
                damage = 1;
            }
        }
        chr.getCheatTracker().checkTakeDamage(damage);
        Pair modify = chr.modifyDamageTaken(damage, attacker);
        damage = ((Double) modify.left).intValue();
        if (damage > 0) {
            chr.getCheatTracker().setAttacksWithoutHit(false);

            if (chr.getBuffedValue(MapleBuffStat.变身术) != null) {
                chr.cancelMorphs();
            }

            boolean mpAttack = (chr.getBuffedValue(MapleBuffStat.金属机甲) != null) && (chr.getBuffSource(MapleBuffStat.金属机甲) != 35121005);
            if (chr.getBuffedValue(MapleBuffStat.魔法盾) != null) {
                int hploss = 0;
                int mploss = 0;
                if (isDeadlyAttack) {
                    if (stats.getHp() > 1) {
                        hploss = stats.getHp() - 1;
                    }
                    if (stats.getMp() > 1) {
                        mploss = stats.getMp() - 1;
                    }
                    if (chr.getBuffedValue(MapleBuffStat.终极无限) != null) {
                        mploss = 0;
                    }
                    chr.addMPHP(-hploss, -mploss);
                } else {
                    mploss = (int) (damage * (chr.getBuffedValue(MapleBuffStat.魔法盾).doubleValue() / 100.0D)) + mpattack;
                    hploss = damage - mploss;
                    if (chr.getBuffedValue(MapleBuffStat.终极无限) != null) {
                        mploss = 0;
                    } else if (mploss > stats.getMp()) {
                        mploss = stats.getMp();
                        hploss = damage - mploss + mpattack;
                    }
                    chr.addMPHP(-hploss, -mploss);
                }
            } else if (chr.getStat().mesoGuardMeso > 0.0D) {
                if (chr.isAdmin()) {
                    chr.dropMessage(5, new StringBuilder().append("受到伤害: ").append(damage).toString());
                }
                damage = (int) Math.ceil(damage * chr.getStat().mesoGuard / 100.0D);
                int mesoloss = (int) (damage * (chr.getStat().mesoGuardMeso / 100.0D));
                if (chr.isAdmin()) {
                    chr.dropMessage(5, new StringBuilder().append("金钱护盾 - 最终伤害: ").append(damage).append(" 减少金币: ").append(mesoloss).toString());
                }
                if (chr.getMeso() < mesoloss) {
                    chr.gainMeso(-chr.getMeso(), false);
                    chr.cancelBuffStats(new MapleBuffStat[]{MapleBuffStat.金钱护盾});
                } else {
                    chr.gainMeso(-mesoloss, false);
                }
                if ((isDeadlyAttack) && (stats.getMp() > 1)) {
                    mpattack = stats.getMp() - 1;
                }
                chr.addMPHP(-damage, -mpattack);
            } else if (isDeadlyAttack) {
                chr.addMPHP(stats.getHp() > 1 ? -(stats.getHp() - 1) : 0, (stats.getMp() > 1) && (!mpAttack) ? -(stats.getMp() - 1) : 0);
            } else {
                chr.addMPHP(-damage, mpAttack ? 0 : -mpattack);
            }

            chr.handleBattleshipHP(-damage);
            if ((chr.inPVP()) && (chr.getStat().getHPPercent() <= 20)) {
                SkillFactory.getSkill(PlayerStats.getSkillByJob(93, chr.getJob())).getEffect(1).applyTo(chr);
            }
        }
        byte offset = 0;
        int offset_d = 0;
        if (slea.available() == 1L) {
            offset = slea.readByte();
            if ((offset == 1) && (slea.available() >= 4L)) {
                offset_d = slea.readInt();
            }
            if ((offset < 0) || (offset > 2)) {
                offset = 0;
            }
        }
        if (((!chr.isAdmin()) || (!ServerProperties.ShowPacket())) || ((damage < -1) || (damage > 2100000000))) {
            FileoutputUtil.log("log\\掉血错误.log", new StringBuilder().append("玩家[").append(chr.getName()).append(" 职业: ").append(chr.getJob()).append("]掉血错误 - 类型: ").append(type).append(" 怪物ID: ").append(monsteridfrom).append(" 伤害: ").append(damage).append(" fake: ").append(fake).append(" direction: ").append(direction).append(" oid: ").append(oid).append(" offset: ").append(offset).append(" 封包:").append(slea.toString(true)).toString());
            return;
        }
        c.getSession().write(MaplePacketCreator.enableActions());
        chr.getMap().broadcastMessage(chr, MaplePacketCreator.damagePlayer(chr.getId(), type, damage, monsteridfrom, direction, skillid, pDMG, pPhysical, pID, pType, pPos, offset, offset_d, fake), false);
    }

    public static void AranCombo(MapleClient c, MapleCharacter chr, int toAdd) {
        if ((chr != null) && (chr.getJob() >= 2000) && (chr.getJob() <= 2112)) {
            short combo = chr.getCombo();
            long curr = System.currentTimeMillis();
            if ((combo > 0) && (curr - chr.getLastCombo() > 7000L)) {
                combo = 0;
            }
            combo = (short) Math.min(30000, combo + toAdd);
            chr.setLastCombo(curr);
            chr.setCombo(combo);
            c.getSession().write(MaplePacketCreator.AranCombo(combo));
            switch (combo) {
                case 10:
                case 20:
                case 30:
                case 40:
                case 50:
                case 60:
                case 70:
                case 80:
                case 90:
                case 100:
                    if (chr.getSkillLevel(21000000) < combo / 10) {
                        break;
                    }
                    SkillFactory.getSkill(21000000).getEffect(combo / 10).applyComboBuff(chr, combo);
            }
        }
    }

    public static void UseItemEffect(int itemId, MapleClient c, MapleCharacter chr) {
        if (itemId == 0) {
            chr.setItemEffect(0);
        } else {
            Item toUse = chr.getInventory(MapleInventoryType.CASH).findById(itemId);
            if ((toUse == null) || (toUse.getItemId() != itemId) || (toUse.getQuantity() < 1)) {
                c.getSession().write(MaplePacketCreator.enableActions());
                return;
            }
            if (itemId != 5510000) {
                chr.setItemEffect(itemId);
            }
        }
        chr.getMap().broadcastMessage(chr, MaplePacketCreator.itemEffect(chr.getId(), itemId), false);
    }

    public static void UseTitleEffect(int itemId, MapleClient c, MapleCharacter chr) {
        if (itemId == 0) {
            chr.setTitleEffect(0);
        } else {
            Item toUse = chr.getInventory(MapleInventoryType.SETUP).findById(itemId);
            if ((toUse == null) || (toUse.getItemId() != itemId) || (toUse.getQuantity() < 1)) {
                c.getSession().write(MaplePacketCreator.enableActions());
                return;
            }
            if ((itemId >= 3700000) && (itemId <= 3700026)) {
                chr.setTitleEffect(itemId);
            }
        }
        chr.getMap().broadcastMessage(chr, MaplePacketCreator.showTitleEffect(chr.getId(), itemId), false);
    }

    public static void CancelItemEffect(int id, MapleCharacter chr) {
        chr.cancelEffect(MapleItemInformationProvider.getInstance().getItemEffect(-id), false, -1L);
    }

    public static void CancelBuffHandler(int sourceid, MapleCharacter chr) {
        if ((chr == null) || (chr.getMap() == null)) {
            return;
        }
        Skill skill = SkillFactory.getSkill(sourceid);
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("取消技能BUFF: 技能ID ").append(sourceid).append(" 技能名字 ").append(SkillFactory.getSkillName(sourceid)).toString());
        }
        if (skill.isChargeSkill()) {
            chr.setKeyDownSkill_Time(0L);
            chr.getMap().broadcastMessage(chr, MaplePacketCreator.skillCancel(chr, sourceid), false);
        } else {
            if (ServerProperties.ShowPacket()) {
                log.info(new StringBuilder().append("取消技能BUFF: chr.cancelEffect ").append(skill.getEffect(1)).toString());
            }
            chr.cancelEffect(skill.getEffect(1), false, -1L);
        }
    }

    public static void CancelMech(LittleEndianAccessor slea, MapleCharacter chr) {
        if (chr == null) {
            return;
        }
        int sourceid = slea.readInt();
        if ((sourceid % 10000 < 1000) && (SkillFactory.getSkill(sourceid) == null)) {
            sourceid += 1000;
        }
        Skill skill = SkillFactory.getSkill(sourceid);
        if (skill == null) {
            return;
        }
        if (skill.isChargeSkill()) {
            chr.setKeyDownSkill_Time(0L);
            chr.getMap().broadcastMessage(chr, MaplePacketCreator.skillCancel(chr, sourceid), false);
        } else {
            chr.cancelEffect(skill.getEffect(slea.readByte()), false, -1L);
        }
    }

    public static void QuickSlot(LittleEndianAccessor slea, MapleCharacter chr) {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            ret.append(slea.readAsciiString(1));
            slea.skip(3);
        }
        chr.getQuestNAdd(MapleQuest.getInstance(123000)).setCustomData(ret.toString());
    }

    public static void SkillEffect(LittleEndianAccessor slea, MapleCharacter chr) {
        int skillId = slea.readInt();
        if (skillId >= 91000000) {
            chr.getClient().getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        byte level = slea.readByte();
        byte flags = slea.readByte();
        byte speed = slea.readByte();
        byte unk = slea.readByte();

        Skill skill = SkillFactory.getSkill(GameConstants.getLinkedAranSkill(skillId));
        if ((chr == null) || (skill == null) || (chr.getMap() == null)) {
            return;
        }
        int skilllevel_serv = chr.getTotalSkillLevel(skill);
        if ((skilllevel_serv > 0) && (skilllevel_serv == level) && ((skillId == 33101005) || (skill.isChargeSkill()))) {
            chr.setKeyDownSkill_Time(System.currentTimeMillis());
            if (skillId == 33101005) {
                chr.setLinkMid(slea.readInt(), 0);
            }
            chr.getMap().broadcastMessage(chr, MaplePacketCreator.skillEffect(chr, skillId, level, flags, speed, unk), false);
        }
    }

    public static void SpecialMove(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        if ((chr == null) || (chr.hasBlockedInventory()) || (chr.getMap() == null) || (slea.available() < 9L)) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        slea.skip(4);
        int skillid = slea.readInt();
        if (skillid >= 91000000) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if (skillid == 23111008) {
            skillid += Randomizer.nextInt(2);
        }
        int skillLevel = slea.readByte();
        Skill skill = SkillFactory.getSkill(skillid);
        if ((skill == null) || ((GameConstants.isAngel(skillid)) && (chr.getStat().equippedSummon % 10000 != skillid % 10000)) || ((chr.inPVP()) && (skill.isPVPDisabled()))) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if ((chr.getTotalSkillLevel(GameConstants.getLinkedAranSkill(skillid)) <= 0) || (chr.getTotalSkillLevel(GameConstants.getLinkedAranSkill(skillid)) != skillLevel)) {
            if ((!GameConstants.isMulungSkill(skillid)) && (!GameConstants.isPyramidSkill(skillid)) && (chr.getTotalSkillLevel(GameConstants.getLinkedAranSkill(skillid)) <= 0)) {
                c.getSession().close();
                return;
            }
            if (GameConstants.isMulungSkill(skillid)) {
                if (chr.getMapId() / 10000 != 92502) {
                    return;
                }
                if (chr.getMulungEnergy() < 10000) {
                    return;
                }
                chr.mulung_EnergyModify(false);
            } else if ((GameConstants.isPyramidSkill(skillid))
                    && (chr.getMapId() / 10000 != 92602) && (chr.getMapId() / 10000 != 92601)) {
                return;
            }
        }

        if (GameConstants.isEventMap(chr.getMapId())) {
            for (MapleEventType t : MapleEventType.values()) {
                MapleEvent e = ChannelServer.getInstance(chr.getClient().getChannel()).getEvent(t);
                if ((e.isRunning()) && (!chr.isGM())) {
                    for (int i : e.getType().mapids) {
                        if (chr.getMapId() == i) {
                            chr.dropMessage(5, "无法在这里使用.");
                            return;
                        }
                    }
                }
            }
        }
        skillLevel = chr.getTotalSkillLevel(GameConstants.getLinkedAranSkill(skillid));
        MapleStatEffect effect = chr.inPVP() ? skill.getPVPEffect(skillLevel) : skill.getEffect(skillLevel);
        if ((effect.is生命分流()) && (chr.getStat().getHp() < chr.getStat().getMaxHp() / 100 * 10)) {
            c.getPlayer().dropMessage(5, "HP不够，无法使用这个技能.");
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if ((effect.getCooldown() > 0) && (!chr.isGM())) {
            if (chr.skillisCooling(skillid)) {
                c.getSession().write(MaplePacketCreator.enableActions());
                return;
            }
            if ((skillid != 5921006) && (skillid != 35111002)) {
                c.getSession().write(MaplePacketCreator.skillCooldown(skillid, effect.getCooldown()));
                chr.addCooldown(skillid, System.currentTimeMillis(), effect.getCooldown() * 1000);
            }
        }
        int mobID;
        //MapleMonster mob;
        switch (skillid) {
            case 1121001:
            case 1221001:
            case 1321001:
            case 9001020:
            case 9101020:
            case 31111003: {
                byte number_of_mobs = slea.readByte();
                slea.skip(3);
                for (int i = 0; i < number_of_mobs; i++) {
                    int mobId = slea.readInt();
                    MapleMonster mob = chr.getMap().getMonsterByOid(mobId);
                    if (mob == null) {
                        continue;
                    }
                    mob.switchController(chr, mob.isControllerHasAggro());
                    mob.applyStatus(chr, new MonsterStatusEffect(MonsterStatus.眩晕, Integer.valueOf(1), skillid, null, false), false, effect.getDuration(), true, effect);
                }

                chr.getMap().broadcastMessage(chr, MaplePacketCreator.showBuffeffect(chr.getId(), skillid, 1, chr.getLevel(), skillLevel, slea.readByte()), chr.getTruePosition());
                c.getSession().write(MaplePacketCreator.enableActions());
                break;
            }
            case 30001061: {
                mobID = slea.readInt();
                MapleMonster mob = chr.getMap().getMonsterByOid(mobID);
                if (mob != null) {
                    boolean success = (mob.getHp() <= mob.getMobMaxHp() / 2L) && (mob.getId() >= 9304000) && (mob.getId() < 9305000);
                    chr.getMap().broadcastMessage(chr, MaplePacketCreator.showBuffeffect(chr.getId(), skillid, 1, chr.getLevel(), skillLevel, (byte) (success ? 1 : 0)), chr.getTruePosition());
                    if (success) {
                        chr.getQuestNAdd(MapleQuest.getInstance(111112)).setCustomData(String.valueOf((mob.getId() - 9303999) * 10));
                        chr.getMap().killMonster(mob, chr, true, false, (byte) 1);
                        chr.cancelEffectFromBuffStat(MapleBuffStat.骑兽技能);
                        c.getSession().write(MaplePacketCreator.updateJaguar(chr));
                    } else {
                        chr.dropMessage(5, "怪物体力过高，捕抓失败。");
                    }
                }
                c.getSession().write(MaplePacketCreator.enableActions());
                break;
            }
            case 30001062:
                chr.dropMessage(5, "没有能被召唤的怪物，请先捕抓怪物。");
                c.getSession().write(MaplePacketCreator.enableActions());
                break;
            case 33101005:
                mobID = chr.getFirstLinkMid();
                MapleMonster mob = chr.getMap().getMonsterByOid(mobID);
                chr.setKeyDownSkill_Time(0L);
                chr.getMap().broadcastMessage(chr, MaplePacketCreator.skillCancel(chr, skillid), false);
                if (mob != null) {
                    boolean success = (mob.getStats().getLevel() < chr.getLevel()) && (mob.getId() < 9000000) && (!mob.getStats().isBoss());
                    if (success) {
                        chr.getMap().broadcastMessage(MobPacket.suckMonster(mob.getObjectId(), chr.getId()));
                        chr.getMap().killMonster(mob, chr, false, false, (byte) -1);
                    } else {
                        chr.dropMessage(5, "怪物的体力过高，无法进行吞噬。");
                    }
                } else {
                    chr.dropMessage(5, "没有发现怪物，使用技能失败。");
                }
                c.getSession().write(MaplePacketCreator.enableActions());
                break;
            case 4341003:
                chr.setKeyDownSkill_Time(0L);
                chr.getMap().broadcastMessage(chr, MaplePacketCreator.skillCancel(chr, skillid), false);
            default:
                Point pos = null;
                if ((slea.available() == 5L) || (slea.available() == 7L)) {
                    pos = slea.readPos();
                }
                if (effect.is时空门()) {
                    if (!FieldLimitType.MysticDoor.check(chr.getMap().getFieldLimit())) {
                        effect.applyTo(c.getPlayer(), pos);
                    } else {
                        c.getSession().write(MaplePacketCreator.enableActions());
                    }
                } else {
                    int mountid = MapleStatEffect.parseMountInfo(c.getPlayer(), skill.getId());
                    if ((mountid != 0) && (mountid != GameConstants.getMountItem(skill.getId(), c.getPlayer())) && (!c.getPlayer().isIntern()) && (c.getPlayer().getBuffedValue(MapleBuffStat.骑兽技能) == null) && (c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short) -122) == null)
                            && (!GameConstants.isMountItemAvailable(mountid, c.getPlayer().getJob()))) {
                        c.getSession().write(MaplePacketCreator.enableActions());
                        return;
                    }

                    effect.applyTo(c.getPlayer(), pos);
                }
        }
    }

    public static void closeRangeAttack(LittleEndianAccessor slea, MapleClient c, final MapleCharacter chr, final boolean energy) {
        if ((chr == null) || ((energy) && (chr.getBuffedValue(MapleBuffStat.能量获得) == null) && (chr.getBuffedValue(MapleBuffStat.战神抗压) == null) && (chr.getBuffedValue(MapleBuffStat.黑暗灵气) == null) && (chr.getBuffedValue(MapleBuffStat.幻灵飓风) == null) && (chr.getBuffedValue(MapleBuffStat.召唤兽) == null) && (chr.getBuffedValue(MapleBuffStat.地雷) == null) && (chr.getBuffedValue(MapleBuffStat.快速移动精通) == null))) {
            return;
        }
        if ((chr.hasBlockedInventory()) || (chr.getMap() == null)) {
            return;
        }
        if ((chr.getGMLevel() >= 3) && (chr.getGMLevel() <= 5) && (chr.getMap().isBossMap())) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }

        if ((!chr.isAdmin()) && (chr.getMap().isMarketMap())) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        AttackInfo attack = DamageParse.parseCloseRangeAttack(slea, chr);
        if (attack == null) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        final boolean mirror = chr.getBuffedValue(MapleBuffStat.影分身) != null;
        double maxdamage = chr.getStat().getCurrentMaxBaseDamage();
        Item shield = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short) -10);
        int attackCount = (shield != null) && (shield.getItemId() / 10000 == 134) ? 2 : 1;
        int skillLevel = 0;
        MapleStatEffect effect = null;
        Skill skill = null;

        if (attack.skill != 0) {
            skill = SkillFactory.getSkill(GameConstants.getLinkedAranSkill(attack.skill));
            if ((skill == null) || ((GameConstants.isAngel(attack.skill)) && (chr.getStat().equippedSummon % 10000 != attack.skill % 10000))) {
                c.getSession().write(MaplePacketCreator.enableActions());
                return;
            }
            skillLevel = chr.getTotalSkillLevel(skill);
            effect = attack.getAttackEffect(chr, skillLevel, skill);
            if (effect == null) {
                return;
            }
            if (GameConstants.isEventMap(chr.getMapId())) {
                for (MapleEventType t : MapleEventType.values()) {
                    MapleEvent e = ChannelServer.getInstance(chr.getClient().getChannel()).getEvent(t);
                    if ((e.isRunning()) && (!chr.isGM())) {
                        for (int i : e.getType().mapids) {
                            if (chr.getMapId() == i) {
                                chr.dropMessage(5, "无法在这个地方使用.");
                                return;
                            }
                        }
                    }
                }
            }
            maxdamage *= (effect.getDamage() + chr.getStat().getDamageIncrease(attack.skill)) / 100.0D;
            attackCount = effect.getAttackCount();
            if ((effect.getCooldown() > 0) && (!chr.isGM()) && (!energy)) {
                if ((chr.skillisCooling(attack.skill)) && (attack.skill != 24121005)) {
                    chr.dropMessage(5, "技能由于冷取时间限制，暂时无法使用。");
                    c.getSession().write(MaplePacketCreator.enableActions());
                    return;
                }
                if (!chr.skillisCooling(attack.skill)) {
                    c.getSession().write(MaplePacketCreator.skillCooldown(attack.skill, effect.getCooldown()));
                    chr.addCooldown(attack.skill, System.currentTimeMillis(), effect.getCooldown() * 1000);
                }
            }
        }
        attack = DamageParse.Modify_AttackCrit(attack, chr, 1, effect);
        attackCount *= (mirror ? 2 : 1);
        if (!energy) {
            if (((chr.getMapId() == 109060000) || (chr.getMapId() == 109060002) || (chr.getMapId() == 109060004)) && (attack.skill == 0)) {
                MapleSnowballs.hitSnowball(chr);
            }

            int numFinisherOrbs = 0;
            Integer comboBuff = chr.getBuffedValue(MapleBuffStat.斗气集中);
            if (isFinisher(attack.skill) > 0) {
                if (comboBuff != null) {
                    numFinisherOrbs = comboBuff.intValue() - 1;
                }
                if (numFinisherOrbs <= 0) {
                    return;
                }
                chr.handleOrbconsume(isFinisher(attack.skill));
                maxdamage *= numFinisherOrbs;
            }
        }
        chr.checkFollow();
        if (!chr.isHidden()) {
            chr.getMap().broadcastMessage(chr, MaplePacketCreator.closeRangeAttack(chr.getId(), attack.tbyte, attack.skill, skillLevel, attack.display, attack.speed, attack.allDamage, energy, chr.getLevel(), chr.getStat().passive_mastery(), attack.unk, attack.charge), chr.getTruePosition());
        } else {
            chr.getMap().broadcastGMMessage(chr, MaplePacketCreator.closeRangeAttack(chr.getId(), attack.tbyte, attack.skill, skillLevel, attack.display, attack.speed, attack.allDamage, energy, chr.getLevel(), chr.getStat().passive_mastery(), attack.unk, attack.charge), false);
        }
        DamageParse.applyAttack(attack, skill, c.getPlayer(), attackCount, maxdamage, effect, mirror ? AttackType.NON_RANGED_WITH_MIRROR : AttackType.NON_RANGED);
        WeakReference[] clones = chr.getClones();
        for (int i = 0; i < clones.length; i++) {
            if (clones[i].get() != null) {
                final MapleCharacter clone = (MapleCharacter) clones[i].get();
                final Skill skil2 = skill;
                final int skillLevel2 = skillLevel;
                final int attackCount2 = attackCount;
                final double maxdamage2 = maxdamage;
                final MapleStatEffect eff2 = effect;
                final AttackInfo attack2 = DamageParse.DivideAttack(attack, chr.isGM() ? 1 : 4);
                CloneTimer.getInstance().schedule(new Runnable() {

                    @Override
                    public void run() {
                        if (!clone.isHidden()) {
                            clone.getMap().broadcastMessage(MaplePacketCreator.closeRangeAttack(clone.getId(), attack2.tbyte, attack2.skill, skillLevel2, attack2.display, attack2.speed, attack2.allDamage, energy, clone.getLevel(), clone.getStat().passive_mastery(), attack2.unk, attack2.charge));
                        } else {
                            clone.getMap().broadcastGMMessage(clone, MaplePacketCreator.closeRangeAttack(clone.getId(), attack2.tbyte, attack2.skill, skillLevel2, attack2.display, attack2.speed, attack2.allDamage, energy, clone.getLevel(), clone.getStat().passive_mastery(), attack2.unk, attack2.charge), false);
                        }
                        DamageParse.applyAttack(attack2, skil2, chr, attackCount2, maxdamage2, eff2, mirror ? AttackType.NON_RANGED_WITH_MIRROR : AttackType.NON_RANGED);
                    }
                }, 500 * i + 500);
            }
        }
    }

    public static void rangedAttack(LittleEndianAccessor slea, MapleClient c, final MapleCharacter chr) {
        if ((chr == null) || (chr.hasBlockedInventory()) || (chr.getMap() == null)) {
            return;
        }
        if ((chr.getGMLevel() >= 3) && (chr.getGMLevel() <= 5) && (chr.getMap().isBossMap())) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }

        if ((!chr.isAdmin()) && (chr.getMap().isMarketMap())) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        AttackInfo attack = DamageParse.parseRangedAttack(slea, chr);
        if (attack == null) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        int bulletCount = 1;
        int skillLevel = 0;
        MapleStatEffect effect = null;
        Skill skill = null;
        boolean AOE = false;
        boolean noBullet = ((chr.getJob() >= 3500) && (chr.getJob() <= 3512)) || (GameConstants.is火炮手(chr.getJob())) || (GameConstants.is双弩精灵(chr.getJob())) || (GameConstants.is龙的传人(chr.getJob())) || (GameConstants.is米哈尔(chr.getJob()));
        if (attack.skill != 0) {
            skill = SkillFactory.getSkill(GameConstants.getLinkedAranSkill(attack.skill));
            if ((skill == null) || ((GameConstants.isAngel(attack.skill)) && (chr.getStat().equippedSummon % 10000 != attack.skill % 10000))) {
                c.getSession().write(MaplePacketCreator.enableActions());
                return;
            }
            skillLevel = chr.getTotalSkillLevel(skill);
            effect = attack.getAttackEffect(chr, skillLevel, skill);
            if (effect == null) {
                return;
            }
            if (GameConstants.isEventMap(chr.getMapId())) {
                for (MapleEventType t : MapleEventType.values()) {
                    MapleEvent e = ChannelServer.getInstance(chr.getClient().getChannel()).getEvent(t);
                    if ((e.isRunning()) && (!chr.isGM())) {
                        for (int i : e.getType().mapids) {
                            if (chr.getMapId() == i) {
                                chr.dropMessage(5, "无法在这个地方使用.");
                                return;
                            }
                        }
                    }
                }
            }
            switch (attack.skill) {
                case 1077:
                case 1078:
                case 1079:
                case 11077:
                case 11078:
                case 11079:
                case 4121003:
                case 5821002:
                case 11101004:
                case 13101005:
                case 13111007:
                case 14101006:
                case 15111007:
                case 21000004:
                case 21100004:
                case 21100007:
                case 21110004:
                case 21110011:
                case 21120006:
                case 33101002:
                case 33101007:
                case 33121001:
                case 33121002:     
                case 51001004://米哈尔
                case 51111007://米哈尔
                case 51121008://米哈尔
                    AOE = true;
                    bulletCount = effect.getAttackCount();
                    break;
                case 35111004:
                case 35121005:
                case 35121013:
                    AOE = true;
                    bulletCount = 6;
                    break;
                case 3111004:
                case 3211004:
                case 4111013:
                case 13111000:
                case 14111008:
                    bulletCount = effect.getAttackCount();
                    break;
                default:
                    bulletCount = effect.getBulletCount();
            }

            if ((chr.isGM()) && (ServerProperties.ShowPacket())) {
                chr.dropMessage(-5, new StringBuilder().append("远距离攻击 noBullet ").append(noBullet).append(" BulletCount ").append(effect.getBulletCount()).append(" AttackCount ").append(effect.getAttackCount()).toString());
            }
            if ((noBullet) && (effect.getBulletCount() < effect.getAttackCount())) {
                bulletCount = effect.getAttackCount();
            }
            if ((effect.getCooldown() > 0) && (!chr.isGM()) && (((attack.skill != 35111004) && (attack.skill != 35121013)) || (chr.getBuffSource(MapleBuffStat.金属机甲) != attack.skill))) {
                if (chr.skillisCooling(attack.skill)) {
                    c.getSession().write(MaplePacketCreator.enableActions());
                    return;
                }
                c.getSession().write(MaplePacketCreator.skillCooldown(attack.skill, effect.getCooldown()));
                chr.addCooldown(attack.skill, System.currentTimeMillis(), effect.getCooldown() * 1000);
            }
        }
        attack = DamageParse.Modify_AttackCrit(attack, chr, 2, effect);
        Integer ShadowPartner = chr.getBuffedValue(MapleBuffStat.影分身);
        boolean mirror = chr.getBuffedValue(MapleBuffStat.影分身) != null;
        bulletCount *= (mirror ? 2 : 1);

        if ((chr.isGM()) && (ServerProperties.ShowPacket())) {
            chr.dropMessage(-5, new StringBuilder().append("远距离攻击 - 影分身: mirror ").append(mirror).append(" ShadowPartner ").append(ShadowPartner).append(" bulletCount ").append(bulletCount).toString());
        }
        int projectile = 0;
        int visProjectile = 0;
        if ((!AOE) && (chr.getBuffedValue(MapleBuffStat.无形箭弩) == null) && (!noBullet)) {
            Item ipp = chr.getInventory(MapleInventoryType.USE).getItem((short) attack.slot);
            if (ipp == null) {
                return;
            }
            projectile = ipp.getItemId();
            if (attack.csstar > 0) {
                if (chr.getInventory(MapleInventoryType.CASH).getItem((short) attack.csstar) == null) {
                    return;
                }
                visProjectile = chr.getInventory(MapleInventoryType.CASH).getItem((short) attack.csstar).getItemId();
            } else {
                visProjectile = projectile;
            }

            if (chr.getBuffedValue(MapleBuffStat.暗器伤人) == null) {
                int bulletConsume = bulletCount;
                if ((effect != null) && (effect.getBulletConsume() != 0)) {
                    bulletConsume = effect.getBulletConsume() * (mirror ? 2 : 1);
                }
                if ((chr.getJob() == 412) && (bulletConsume > 0) && (ipp.getQuantity() < MapleItemInformationProvider.getInstance().getSlotMax(projectile))) {
                    Skill expert = SkillFactory.getSkill(4110012);
                    if (chr.getTotalSkillLevel(expert) > 0) {
                        MapleStatEffect eff = expert.getEffect(chr.getTotalSkillLevel(expert));
                        if (eff.makeChanceResult()) {
                            ipp.setQuantity((short) (ipp.getQuantity() + 1));
                            c.getSession().write(MaplePacketCreator.updateInventorySlot(MapleInventoryType.USE, ipp, false));
                            bulletConsume = 0;
                            c.getSession().write(MaplePacketCreator.getInventoryStatus());
                        }
                    }
                }
                if ((bulletConsume > 0)
                        && (!MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, projectile, bulletConsume, false, true))) {
                    chr.dropMessage(5, "您的箭/子弹/飞镖不足。");
                    return;
                }
            }
        } else if ((chr.getJob() >= 3500) && (chr.getJob() <= 3512)) {
            visProjectile = 2333000;
        } else if (GameConstants.is火炮手(chr.getJob())) {
            visProjectile = 2333001;
        }

        int projectileWatk = 0;
        if (projectile != 0) {
            projectileWatk = MapleItemInformationProvider.getInstance().getWatkForProjectile(projectile);
        }
        PlayerStats statst = chr.getStat();
        double basedamage;
        switch (attack.skill) {
            case 4001344:
            case 4111010:
            case 14001004:
                basedamage = Math.max(statst.getCurrentMaxBaseDamage(), statst.getTotalLuk() * 5.0F * (statst.getTotalWatk() + projectileWatk) / 100.0F);
                break;
            default:
                basedamage = statst.getCurrentMaxBaseDamage();
                switch (attack.skill) {
                    case 3101005:
                        basedamage *= effect.getX() / 100.0D;
                }

        }

        if (effect != null) {
            basedamage *= (effect.getDamage() + statst.getDamageIncrease(attack.skill)) / 100.0D;
            int money = effect.getMoneyCon();
            if (money != 0) {
                if (money > chr.getMeso()) {
                    money = chr.getMeso();
                }
                chr.gainMeso(-money, false);
            }
        }
        chr.checkFollow();
        if (!chr.isHidden()) {
            if (attack.skill == 3211006) {
                chr.getMap().broadcastMessage(chr, MaplePacketCreator.strafeAttack(chr.getId(), attack.tbyte, attack.skill, skillLevel, attack.display, attack.speed, visProjectile, attack.allDamage, attack.position, chr.getLevel(), chr.getStat().passive_mastery(), attack.unk, chr.getTotalSkillLevel(3220010)), chr.getTruePosition());
            } else {
                chr.getMap().broadcastMessage(chr, MaplePacketCreator.rangedAttack(chr.getId(), attack.tbyte, attack.skill, skillLevel, attack.display, attack.speed, visProjectile, attack.allDamage, attack.position, chr.getLevel(), chr.getStat().passive_mastery(), attack.unk), chr.getTruePosition());
            }
        } else if (attack.skill == 3211006) {
            chr.getMap().broadcastGMMessage(chr, MaplePacketCreator.strafeAttack(chr.getId(), attack.tbyte, attack.skill, skillLevel, attack.display, attack.speed, visProjectile, attack.allDamage, attack.position, chr.getLevel(), chr.getStat().passive_mastery(), attack.unk, chr.getTotalSkillLevel(3220010)), false);
        } else {
            chr.getMap().broadcastGMMessage(chr, MaplePacketCreator.rangedAttack(chr.getId(), attack.tbyte, attack.skill, skillLevel, attack.display, attack.speed, visProjectile, attack.allDamage, attack.position, chr.getLevel(), chr.getStat().passive_mastery(), attack.unk), false);
        }

        DamageParse.applyAttack(attack, skill, chr, bulletCount, basedamage, effect, mirror ? AttackType.RANGED_WITH_SHADOWPARTNER : AttackType.RANGED);
        WeakReference[] clones = chr.getClones();
        for (int i = 0; i < clones.length; i++) {
            if (clones[i].get() != null) {
                final MapleCharacter clone = (MapleCharacter) clones[i].get();
                final Skill skil2 = skill;
                final MapleStatEffect eff2 = effect;
                final double basedamage2 = basedamage;
                final int bulletCount2 = bulletCount;
                final int visProjectile2 = visProjectile;
                final int skillLevel2 = skillLevel;
                final AttackInfo attack2 = DamageParse.DivideAttack(attack, chr.isGM() ? 1 : 4);
                CloneTimer.getInstance().schedule(new Runnable() {

                    @Override
                    public void run() {
                        if (!clone.isHidden()) {
                            if (attack2.skill == 3211006) {
                                clone.getMap().broadcastMessage(MaplePacketCreator.strafeAttack(clone.getId(), attack2.tbyte, attack2.skill, skillLevel2, attack2.display, attack2.speed, visProjectile2, attack2.allDamage, attack2.position, clone.getLevel(), clone.getStat().passive_mastery(), attack2.unk, chr.getTotalSkillLevel(3220010)));
                            } else {
                                clone.getMap().broadcastMessage(MaplePacketCreator.rangedAttack(clone.getId(), attack2.tbyte, attack2.skill, skillLevel2, attack2.display, attack2.speed, visProjectile2, attack2.allDamage, attack2.position, clone.getLevel(), clone.getStat().passive_mastery(), attack2.unk));
                            }
                        } else if (attack2.skill == 3211006) {
                            clone.getMap().broadcastGMMessage(clone, MaplePacketCreator.strafeAttack(clone.getId(), attack2.tbyte, attack2.skill, skillLevel2, attack2.display, attack2.speed, visProjectile2, attack2.allDamage, attack2.position, clone.getLevel(), clone.getStat().passive_mastery(), attack2.unk, chr.getTotalSkillLevel(3220010)), false);
                        } else {
                            clone.getMap().broadcastGMMessage(clone, MaplePacketCreator.rangedAttack(clone.getId(), attack2.tbyte, attack2.skill, skillLevel2, attack2.display, attack2.speed, visProjectile2, attack2.allDamage, attack2.position, clone.getLevel(), clone.getStat().passive_mastery(), attack2.unk), false);
                        }

                        DamageParse.applyAttack(attack2, skil2, chr, bulletCount2, basedamage2, eff2, AttackType.RANGED);
                    }
                }, 500 * i + 500);
            }
        }
    }

    public static void MagicDamage(LittleEndianAccessor slea, MapleClient c, final MapleCharacter chr) {
        if ((chr == null) || (chr.hasBlockedInventory()) || (chr.getMap() == null)) {
            return;
        }
        if ((chr.getGMLevel() >= 3) && (chr.getGMLevel() <= 5) && (chr.getMap().isBossMap())) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }

        if ((!chr.isAdmin()) && (chr.getMap().isMarketMap())) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        AttackInfo attack = DamageParse.parseMagicDamage(slea, chr);
        if (attack == null) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        Skill skill = SkillFactory.getSkill(GameConstants.getLinkedAranSkill(attack.skill));
        if ((skill == null) || ((GameConstants.isAngel(attack.skill)) && (chr.getStat().equippedSummon % 10000 != attack.skill % 10000))) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        int skillLevel = chr.getTotalSkillLevel(skill);
        MapleStatEffect effect = attack.getAttackEffect(chr, skillLevel, skill);
        if (effect == null) {
            return;
        }
        attack = DamageParse.Modify_AttackCrit(attack, chr, 3, effect);
        if (GameConstants.isEventMap(chr.getMapId())) {
            for (MapleEventType t : MapleEventType.values()) {
                MapleEvent e = ChannelServer.getInstance(chr.getClient().getChannel()).getEvent(t);
                if ((e.isRunning()) && (!chr.isGM())) {
                    for (int i : e.getType().mapids) {
                        if (chr.getMapId() == i) {
                            chr.dropMessage(5, "无法在这个地方使用.");
                            return;
                        }
                    }
                }
            }
        }
        double maxdamage = chr.getStat().getCurrentMaxBaseDamage() * (effect.getDamage() + chr.getStat().getDamageIncrease(attack.skill)) / 100.0D;
        if (GameConstants.isPyramidSkill(attack.skill)) {
            maxdamage = 1.0D;
        } else if ((GameConstants.is新手职业(skill.getId() / 10000)) && (skill.getId() % 10000 == 1000)) {
            maxdamage = 40.0D;
        }
        if ((effect.getCooldown() > 0) && (!chr.isGM())) {
            if (chr.skillisCooling(attack.skill)) {
                c.getSession().write(MaplePacketCreator.enableActions());
                return;
            }
            c.getSession().write(MaplePacketCreator.skillCooldown(attack.skill, effect.getCooldown()));
            chr.addCooldown(attack.skill, System.currentTimeMillis(), effect.getCooldown() * 1000);
        }
        chr.checkFollow();
        if (!chr.isHidden()) {
            chr.getMap().broadcastMessage(chr, MaplePacketCreator.magicAttack(chr.getId(), attack.tbyte, attack.skill, skillLevel, attack.display, attack.speed, attack.allDamage, attack.charge, chr.getLevel(), attack.unk), chr.getTruePosition());
        } else {
            chr.getMap().broadcastGMMessage(chr, MaplePacketCreator.magicAttack(chr.getId(), attack.tbyte, attack.skill, skillLevel, attack.display, attack.speed, attack.allDamage, attack.charge, chr.getLevel(), attack.unk), false);
        }
        DamageParse.applyAttackMagic(attack, skill, c.getPlayer(), effect, maxdamage);
        WeakReference[] clones = chr.getClones();
        for (int i = 0; i < clones.length; i++) {
            if (clones[i].get() != null) {
                final MapleCharacter clone = (MapleCharacter) clones[i].get();
                final Skill skil2 = skill;
                final MapleStatEffect eff2 = effect;
                final double maxd = maxdamage;
                final int skillLevel2 = skillLevel;
                final AttackInfo attack2 = DamageParse.DivideAttack(attack, chr.isGM() ? 1 : 4);
                CloneTimer.getInstance().schedule(new Runnable() {

                    @Override
                    public void run() {
                        if (!clone.isHidden()) {
                            clone.getMap().broadcastMessage(MaplePacketCreator.magicAttack(clone.getId(), attack2.tbyte, attack2.skill, skillLevel2, attack2.display, attack2.speed, attack2.allDamage, attack2.charge, clone.getLevel(), attack2.unk));
                        } else {
                            clone.getMap().broadcastGMMessage(clone, MaplePacketCreator.magicAttack(clone.getId(), attack2.tbyte, attack2.skill, skillLevel2, attack2.display, attack2.speed, attack2.allDamage, attack2.charge, clone.getLevel(), attack2.unk), false);
                        }
                        DamageParse.applyAttackMagic(attack2, skil2, chr, eff2, maxd);
                    }
                }, 500 * i + 500);
            }
        }
    }

    public static void DropMeso(int meso, MapleCharacter chr) {
        if ((!chr.isAlive()) || (meso < 10) || (meso > 50000) || (meso > chr.getMeso())) {
            chr.getClient().getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        chr.gainMeso(-meso, false, true);
        chr.getMap().spawnMesoDrop(meso, chr.getTruePosition(), chr, chr, true, (byte) 0);
        chr.getCheatTracker().checkDrop(true);
    }

    public static void ChangeAndroidEmotion(int emote, MapleCharacter chr) {
        if ((emote > 0) && (chr != null) && (chr.getMap() != null) && (!chr.isHidden()) && (emote <= 17) && (chr.getAndroid() != null)) {
            chr.getMap().broadcastMessage(AndroidPacket.showAndroidEmotion(chr.getId(), emote));
        }
    }

    public static void getphantomequip(LittleEndianAccessor slea, MapleCharacter chr) {
        int chrid=slea.readInt();
        int skillid=slea.readInt();
        int jobid=skillid/10000;
        int newskill=0;//0,1,2,3
        int type=0;//大类
            if(jobid==100 
                    || jobid==200 
                    || jobid==300 
                    || jobid==400 
                    || jobid==500){
                newskill=24001001;
                type=1;
            }
            //2th
            if(jobid==110 
                    || jobid==120 
                    || jobid==130 
                    || jobid==210 
                    || jobid==220 
                    || jobid==230 
                    || jobid==310 
                    || jobid==320 
                    || jobid==410 
                    || jobid==420 
                    || jobid==510 
                    || jobid==520){
                newskill=24101001;
                type=2;
            }
            //3th
            if(jobid==111 
                    || jobid==121 
                    || jobid==131 
                    || jobid==211 
                    || jobid==221 
                    || jobid==231 
                    || jobid==311 
                    || jobid==321 
                    || jobid==411 
                    || jobid==421 
                    || jobid==511 
                    || jobid==521){
                newskill=24111001;
                type=3;
            }
            //4th
            if(jobid==112 
                    || jobid==122 
                    || jobid==132 
                    || jobid==212 
                    || jobid==222 
                    || jobid==232 
                    || jobid==312 
                    || jobid==322 
                    || jobid==412 
                    || jobid==422 
                    || jobid==512 
                    || jobid==522){
                newskill=24121001;
                type=4;
            }

        if(skillid==0){//unequip
            chr.savephantomskill(type, 0);
            chr.getClient().getSession().write(MaplePacketCreator.sendphantomunequip(chrid));
        }else{//equip
            chr.savephantomskill(type, skillid);
            chr.getClient().getSession().write(MaplePacketCreator.sendphantomunequip(newskill));
            chr.getClient().getSession().write(MaplePacketCreator.sendphantomequip(newskill,skillid));
        }
    }
    public static void getphantomview(LittleEndianAccessor slea, MapleCharacter chr) {
        //复制技能23 01 ，CE CC 10 00 ，06 00 00 00 ，00
        //删除技能23 01 ，2B 46 0F 00 ，00 00 00 00 ，01
        int skillid=slea.readInt();
        slea.readInt();//chrid
        Byte stats=slea.readByte();//状态，复制技能，还是删除技能。
            final Skill skill = SkillFactory.getSkill(skillid);
            int jobid=skillid/10000;
            int type=0;//0,1,2,3
            int xtype=0;//0,1,2,3
            //1th job
            if(jobid==100 || jobid==200 || jobid==300 || jobid==400 || jobid==500){
                type=1;
            }
            //2th
            if(jobid==110 || jobid==120 || jobid==130 || jobid==210 || jobid==220 || jobid==230 || jobid==310 || jobid==320 || jobid==410 || jobid==420 || jobid==510 || jobid==520){
                type=2;
            }
            //3th
            if(jobid==111 || jobid==121 || jobid==131 || jobid==211 || jobid==221 || jobid==231 || jobid==311 || jobid==321 || jobid==411 || jobid==421 || jobid==511 || jobid==521){
                type=3;
            }
            //4th
            if(jobid==112 || jobid==122 || jobid==132 || jobid==212 || jobid==222 || jobid==232 || jobid==312 || jobid==322 || jobid==412 || jobid==422 || jobid==512 || jobid==522){
                type=4;
            }
         //前提，已经给出大类TYPE
        //复制技能》找出现在的空格.
        //删除技能》根据TYPE和技能ID，删除技能.
        
        if(stats==0){//复制技能，需要得到大类TYPE，和小类TYPE
            //2E 00 ，01 00 ，04 00 00 00 ，01 00 00 00 ，6C 9F 2F 00 0A 00 00 00 ，1E 00 00 00
            //04 00 00 00 = 大类（1，2，3，4），01 00 00 00=小类（0，1，2，3）？？？
            xtype=chr.getskillidbyskill(type,0);//获得小类
            chr.savephantomskill(type*10+xtype, skillid);
            chr.getClient().getSession().write(MaplePacketCreator.sendphantomview(skillid,skill.getMaxLevel(), type, xtype));
        }else{//删除技能，需要得到大类TYPE，和小类TYPE
            //2A 00 ,01 03 ,01 00 00 00 ,02 00 00 00
            //01 00 00 00 = 大类（1，2，3，4） ,02 00 00 00=小类（0，1，2，3）
            xtype=chr.getskillidbyskill(type,skillid);//获得小类
            chr.savephantomskill(type*10+xtype, 0);
            chr.getClient().getSession().write(MaplePacketCreator.sendphantomdrop(skillid, type, xtype));
        }
    }
    public static void getphantomskill(int chrsid, MapleCharacter chr) {
        int jobid=chr.getClient().getChannelServer().getPlayerStorage().getCharacterById(chrsid).getJob();
        List<Integer> skills = new ArrayList<Integer>();
        if(jobid>=100 && jobid<=132){
            skills.add(1001003);
            skills.add(1001004);
            skills.add(1001005);
            if(jobid>=110 && jobid<=112){
                skills.add(1101006);
                skills.add(1101007);
                skills.add(1101008);
                if(jobid>=111 && jobid<=112){
                    skills.add(1111002);
                    skills.add(1111003);
                    skills.add(1111005);
                    skills.add(1111007);
                    skills.add(1111008);
                    skills.add(1111010);
                    if(jobid==112){
                        skills.add(1121001);
                        skills.add(1121002);
                        skills.add(1121006);
                        skills.add(1121008);
                    }
                }
            }
            if(jobid>=120 && jobid<=122){
                skills.add(1201006);
                skills.add(1201007);
                skills.add(1201008);
                if(jobid>=121 && jobid<=122){
                    skills.add(1111002);
                    skills.add(1111003);
                    skills.add(1111005);
                    skills.add(1111007);
                    skills.add(1111008);
                    skills.add(1111010);
                    if(jobid==122){
                        skills.add(1211002);
                        skills.add(1211004);
                        skills.add(1221006);
                        skills.add(1221008);
                        skills.add(1221009);
                    }
                }
            }
            if(jobid>=130 && jobid<=132){
                skills.add(1301006);
                skills.add(1301007);
                skills.add(1301008);
                if(jobid>=131 && jobid<=132){
                    skills.add(1311001);
                    skills.add(1311003);
                    skills.add(1311005);
                    skills.add(1311006);
                    skills.add(1311007);
                    skills.add(1311008);
                    if(jobid==132){
                        skills.add(1311001);
                        skills.add(1311002);
                        skills.add(1321003);
                        skills.add(1321012);
                    }
                }
            }
        }
        if(jobid>=200 && jobid<=232){
            skills.add(2001002);
            skills.add(2001003);
            skills.add(2001004);
            skills.add(2001005);
            if(jobid>=210 && jobid<=212){
                skills.add(2101001);
                skills.add(2101003);
                skills.add(2101004);
                skills.add(2101005);
                if(jobid>=211 && jobid<=212){
                    skills.add(2111002);
                    skills.add(2111003);
                    skills.add(2111004);
                    skills.add(2111006);
                    skills.add(2111008);
                    if(jobid==212){
                        skills.add(2121001);
                        skills.add(2121003);
                        skills.add(2121006);
                        skills.add(2121007);
                    }
                }
            }
            if(jobid>=220 && jobid<=222){
                skills.add(2201001);
                skills.add(2201003);
                skills.add(2201004);
                skills.add(2201005);
                if(jobid>=221 && jobid<=222){
                    skills.add(2211002);
                    skills.add(2211003);
                    skills.add(2211004);
                    skills.add(2211006);
                    skills.add(2211008);
                    if(jobid==222){
                        skills.add(2221001);
                        skills.add(2221003);
                        skills.add(2221006);
                        skills.add(2221007);
                    }
                }
            }
            if(jobid>=230 && jobid<=232){
                skills.add(2301002);
                skills.add(2301003);
                skills.add(2301004);
                skills.add(2301005);
                if(jobid>=231 && jobid<=232){
                    skills.add(2311001);
                    skills.add(2311002);
                    skills.add(2311003);
                    skills.add(2311004);
                    skills.add(2311005);
                    if(jobid==232){
                        skills.add(2321001);
                        skills.add(2321005);
                        skills.add(2321006);
                        skills.add(2321007);
                        skills.add(2321008);
                    }
                }
            }
        }
        if(jobid>=300 && jobid<=322){
            skills.add(3001004);
            skills.add(3001005);
            if(jobid>=310 && jobid<=312){
                skills.add(3101005);
                if(jobid>=311 && jobid<=312){
                    skills.add(3111000);
                    skills.add(3111002);
                    skills.add(3111003);
                    skills.add(3111004);
                    skills.add(3111006);
                    skills.add(3111008);
                    if(jobid==312){
                        skills.add(3121002);
                        skills.add(3121004);
                    }
                }
            }
            if(jobid>=320 && jobid<=322){
                skills.add(3201005);
                if(jobid>=321 && jobid<=322){
                    skills.add(3211000);
                    skills.add(3211002);
                    skills.add(3211003);
                    skills.add(3211004);
                    skills.add(3211006);
                    skills.add(3211008);
                    if(jobid==322){
                        skills.add(3221001);
                        skills.add(3221002);
                        skills.add(3221007);
                        skills.add(3220010);
                    }
                }
            }
        }
        if(jobid>=400 && jobid<=422){
            skills.add(4001003);
            skills.add(4001005);
            if(jobid>=410 && jobid<=412){
                skills.add(4101008);
                skills.add(4101010);
                if(jobid>=411 && jobid<=412){
                    skills.add(4111003);
                    skills.add(4111010);
                    skills.add(4111013);
                    if(jobid==412){
                        skills.add(4121003);
                        skills.add(4121013);
                        skills.add(4121014);
                        skills.add(4121015);
                        skills.add(4121016);
                    }
                }
            }
            if(jobid>=420 && jobid<=422){
                skills.add(4201004);
                skills.add(4201005);
                skills.add(4201009);
                skills.add(4201011);
                if(jobid>=421 && jobid<=422){
                    skills.add(4211002);
                    skills.add(4211003);
                    skills.add(4211011);
                    if(jobid==422){
                        skills.add(4221001);
                        skills.add(4221006);
                        skills.add(4221007);
                        skills.add(4221013);
                    }
                }
            }
        }
        if(jobid>=500 && jobid<=522){
            skills.add(5001002);
            skills.add(5001003);
            if(jobid>=510 && jobid<=512){
                if(jobid>=511 && jobid<=512){
                    skills.add(5111007);
                    if(jobid==512){
                        skills.add(5121001);
                        skills.add(5121010);
                        skills.add(5121016);
                    }
                }
            }
            if(jobid>=520 && jobid<=522){
                if(jobid>=521 && jobid<=522){
                    skills.add(5211007);
                    if(jobid==522){
                        skills.add(5221004);
                    }
                }
            }
        }
        chr.getClient().getSession().write(MaplePacketCreator.sendphantomskill(chrsid, jobid, skills));
    }    
    public static void MoveAndroid(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        slea.skip(8);
        List res = MovementParse.parseMovement(slea, 3);
        if ((res != null) && (chr != null) && (!res.isEmpty()) && (chr.getMap() != null) && (chr.getAndroid() != null)) {
            Point pos = new Point(chr.getAndroid().getPos());
            chr.getAndroid().updatePosition(res);
            chr.getMap().broadcastMessage(chr, AndroidPacket.moveAndroid(chr.getId(), pos, res), false);
        }
    }

    public static void ChangeEmotion(final int emote, MapleCharacter chr) {
        if (emote > 7) {
            int emoteid = 5159992 + emote;
            MapleInventoryType type = GameConstants.getInventoryType(emoteid);
            if (chr.getInventory(type).findById(emoteid) == null) {
                chr.getCheatTracker().registerOffense(CheatingOffense.USING_UNAVAILABLE_ITEM, Integer.toString(emoteid));
                return;
            }
        }
        if ((emote > 0) && (chr != null) && (chr.getMap() != null) && (!chr.isHidden())) {
            chr.getMap().broadcastMessage(chr, MaplePacketCreator.facialExpression(chr, emote), false);
            WeakReference[] clones = chr.getClones();
            for (int i = 0; i < clones.length; i++) {
                if (clones[i].get() != null) {
                    final MapleCharacter clone = (MapleCharacter) clones[i].get();
                    CloneTimer.getInstance().schedule(new Runnable() {

                        @Override
                        public void run() {
                            clone.getMap().broadcastMessage(MaplePacketCreator.facialExpression(clone, emote));
                        }
                    }, 500 * i + 500);
                }
            }
        }
    }

    public static void Heal(LittleEndianAccessor slea, MapleCharacter chr) {
        if (chr == null) {
            return;
        }
        chr.updateTick(slea.readInt());
        slea.skip(4);
        slea.skip(4);
        int healHP = slea.readShort();
        int healMP = slea.readShort();
        PlayerStats stats = chr.getStat();
        if (stats.getHp() <= 0) {
            return;
        }
        long now = System.currentTimeMillis();
        if ((healHP != 0) && (chr.canHP(now + 1000L))) {
            if (healHP > stats.getHealHP()) {
                healHP = (int) stats.getHealHP();
            }
            chr.addHP(healHP);
        }
        if ((healMP != 0) && (!GameConstants.is恶魔猎手(chr.getJob())) && (chr.canMP(now + 1000L))) {
            if (healMP > stats.getHealMP()) {
                healMP = (int) stats.getHealMP();
            }
            chr.addMP(healMP);
        }
    }

    public static void MovePlayer(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        if (chr == null) {
            return;
        }
        final Point Original_Pos = chr.getPosition();
        slea.skip(17);
        List res;
        try {
            res = MovementParse.parseMovement(slea, 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(new StringBuilder().append("AIOBE Type1:\n").append(slea.toString(true)).toString());
            return;
        }

        if ((res != null) && (c.getPlayer().getMap() != null)) {
            if ((slea.available() < 11L) || (slea.available() > 26L)) {
                return;
            }
            final MapleMap map = c.getPlayer().getMap();

            if (chr.isHidden()) {
                chr.setLastRes(res);
                c.getPlayer().getMap().broadcastGMMessage(chr, MaplePacketCreator.movePlayer(chr.getId(), res, Original_Pos), false);
            } else {
                c.getPlayer().getMap().broadcastMessage(c.getPlayer(), MaplePacketCreator.movePlayer(chr.getId(), res, Original_Pos), false);
            }

            MovementParse.updatePosition(res, chr, 0);
            final Point pos = chr.getTruePosition();
            map.movePlayer(chr, pos);
            if ((chr.getFollowId() > 0) && (chr.isFollowOn()) && (chr.isFollowInitiator())) {
                MapleCharacter fol = map.getCharacterById(chr.getFollowId());
                if (fol != null) {
                    Point original_pos = fol.getPosition();
                    fol.getClient().getSession().write(MaplePacketCreator.moveFollow(Original_Pos, original_pos, pos, res));
                    MovementParse.updatePosition(res, fol, 0);
                    map.movePlayer(fol, pos);
                    map.broadcastMessage(fol, MaplePacketCreator.movePlayer(fol.getId(), res, original_pos), false);
                } else {
                    chr.checkFollow();
                }
            }
            WeakReference[] clones = chr.getClones();
            for (int i = 0; i < clones.length; i++) {
                if (clones[i].get() != null) {
                    final MapleCharacter clone = (MapleCharacter) clones[i].get();
                    final List res3 = res;
                    CloneTimer.getInstance().schedule(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                if (clone.getMap() == map) {
                                    if (clone.isHidden()) {
                                        map.broadcastGMMessage(clone, MaplePacketCreator.movePlayer(clone.getId(), res3, Original_Pos), false);
                                    } else {
                                        map.broadcastMessage(clone, MaplePacketCreator.movePlayer(clone.getId(), res3, Original_Pos), false);
                                    }
                                    MovementParse.updatePosition(res3, clone, 0);
                                    map.movePlayer(clone, pos);
                                }
                            } catch (Exception e) {
                            }
                        }
                    }, 500 * i + 500);
                }

            }

            int count = c.getPlayer().getFallCounter();
            boolean samepos = (pos.y > c.getPlayer().getOldPosition().y) && (Math.abs(pos.x - c.getPlayer().getOldPosition().x) < 5);
            if ((samepos) && ((pos.y > map.getBottom() + 250) || (map.getFootholds().findBelow(pos) == null))) {
                if (count > 5) {
                    c.getPlayer().changeMap(map, map.getPortal(0));
                    c.getPlayer().setFallCounter(0);
                } else {
                    count++;
                    c.getPlayer().setFallCounter(count);
                }
            } else if (count > 0) {
                c.getPlayer().setFallCounter(0);
            }
            c.getPlayer().setOldPosition(pos);
            if ((!samepos) && (c.getPlayer().getBuffSource(MapleBuffStat.黑暗灵气) == 32120000)) {
                c.getPlayer().getStatForBuff(MapleBuffStat.黑暗灵气).applyMonsterBuff(c.getPlayer());
            } else if ((!samepos) && (c.getPlayer().getBuffSource(MapleBuffStat.黄色灵气) == 32120001)) {
                c.getPlayer().getStatForBuff(MapleBuffStat.黄色灵气).applyMonsterBuff(c.getPlayer());
            }
            BattleConstants.PokemonMap mapp = BattleConstants.getMap(c.getPlayer().getMapId());
            if ((!samepos) && (c.getPlayer().getBattler(0) != null) && (mapp != null) && (!c.getPlayer().isHidden()) && (!c.getPlayer().hasBlockedInventory())) {
                if (Randomizer.nextInt(c.getPlayer().getBattler(0).getAbility() == BattleConstants.PokemonAbility.Illuminate ? 5 : c.getPlayer().getBattler(0).getAbility() == BattleConstants.PokemonAbility.Stench ? 20 : 10) == 0) {
                    LinkedList<Pair<Integer, Integer>> set = BattleConstants.getMobs(mapp);
                    Collections.shuffle(set);
                    int resulting = 0;
                    for (Pair<Integer, Integer> i : set) {
                        if (Randomizer.nextInt(((Integer) i.right).intValue()) == 0) {
                            resulting = ((Integer) i.left).intValue();
                            break;
                        }
                    }
                    if (resulting > 0) {
                        PokemonBattle wild = new PokemonBattle(c.getPlayer(), resulting, mapp);
                        c.getPlayer().changeMap(wild.getMap(), wild.getMap().getPortal(mapp.portalId));
                        if (c.getPlayer() != null) {
                            c.getPlayer().setBattle(wild);
                            wild.initiate(c.getPlayer(), mapp);
                        }
                    }
                }
            }
        }
    }

    public static void ChangeMapSpecial(String portal_name, MapleClient c, MapleCharacter chr) {
        if ((chr == null) || (chr.getMap() == null)) {
            return;
        }
        MaplePortal portal = chr.getMap().getPortal(portal_name);
        if ((portal != null) && (!chr.hasBlockedInventory())) {
            portal.enterPortal(c);
        } else {
            c.getSession().write(MaplePacketCreator.enableActions());
        }
    }

    public static void ChangeMap(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        if ((chr == null) || (chr.getMap() == null)) {
            return;
        }
        long startTime = System.currentTimeMillis();
        if (chr.isBanned()) {
            MapleMap to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(910000000);
            chr.changeMap(to, to.getPortal(0));
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if (slea.available() != 0L) {
            slea.readByte();
            int targetid = slea.readInt();
            MaplePortal portal = chr.getMap().getPortal(slea.readMapleAsciiString());
            if (slea.available() >= 7L) {
                chr.updateTick(slea.readInt());
            }
            slea.skip(1);
            boolean wheel = (slea.readShort() > 0) && (!GameConstants.isEventMap(chr.getMapId())) && (chr.haveItem(5510000, 1, false, true)) && (chr.getMapId() / 1000000 != 925);
            if ((targetid != -1) && (!chr.isAlive())) {
                chr.setStance(0);
                if ((chr.getEventInstance() != null) && (chr.getEventInstance().revivePlayer(chr)) && (chr.isAlive())) {
                    return;
                }
                if (chr.getPyramidSubway() != null) {
                    chr.getStat().setHp(50, chr);
                    chr.getPyramidSubway().fail(chr);
                    return;
                }
                if (!wheel) {
                    chr.getStat().setHp(50, chr);
                    MapleMap to = chr.getMap().getReturnMap();
                    chr.changeMap(to, to.getPortal(0));
                } else {
                    c.getSession().write(MTSCSPacket.useWheel((byte) (chr.getInventory(MapleInventoryType.CASH).countById(5510000) - 1)));
                    chr.getStat().setHp(chr.getStat().getMaxHp() / 100 * 40, chr);
                    MapleInventoryManipulator.removeById(c, MapleInventoryType.CASH, 5510000, 1, true, false);
                    MapleMap to = chr.getMap();
                    chr.changeMap(to, to.getPortal(0));
                }
            } else if ((targetid != -1) && (chr.isIntern())) {
                MapleMap to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(targetid);
                chr.changeMap(to, to.getPortal(0));
            } else if ((targetid != -1) && (!chr.isIntern())) {
                int divi = chr.getMapId() / 100;
                boolean unlock = false;
                boolean warp = false;
                if (divi == 9130401) {
                    warp = (targetid / 100 == 9130400) || (targetid / 100 == 9130401);
                    if (targetid / 10000 != 91304) {
                        warp = true;
                        unlock = true;
                        targetid = 130030000;
                    }
                } else if (divi == 9130400) {
                    warp = (targetid / 100 == 9130400) || (targetid / 100 == 9130401);
                    if (targetid / 10000 != 91304) {
                        warp = true;
                        unlock = true;
                        targetid = 130030000;
                    }
                } else if (divi == 9140900) {
                    warp = (targetid == 914090011) || (targetid == 914090012) || (targetid == 914090013) || (targetid == 140090000);
                } else if ((divi == 9120601) || (divi == 9140602) || (divi == 9140603) || (divi == 9140604) || (divi == 9140605)) {
                    warp = (targetid == 912060100) || (targetid == 912060200) || (targetid == 912060300) || (targetid == 912060400) || (targetid == 912060500) || (targetid == 3000100);
                    unlock = true;
                } else if (divi == 9101500) {
                    warp = (targetid == 910150006) || (targetid == 101050010);
                    unlock = true;
                } else if ((divi == 9140901) && (targetid == 140000000)) {
                    unlock = true;
                    warp = true;
                } else if ((divi == 9240200) && (targetid == 924020000)) {
                    unlock = true;
                    warp = true;
                } else if ((targetid == 980040000) && (divi >= 9800410) && (divi <= 9800450)) {
                    warp = true;
                } else if ((divi == 9140902) && ((targetid == 140030000) || (targetid == 140000000))) {
                    unlock = true;
                    warp = true;
                } else if ((divi == 9000900) && (targetid / 100 == 9000900) && (targetid > chr.getMapId())) {
                    warp = true;
                } else if ((divi / 1000 == 9000) && (targetid / 100000 == 9000)) {
                    unlock = (targetid < 900090000) || (targetid > 900090004);
                    warp = true;
                } else if ((divi / 10 == 1020) && (targetid == 1020000)) {
                    unlock = true;
                    warp = true;
                } else if ((chr.getMapId() == 900090101) && (targetid == 100030100)) {
                    unlock = true;
                    warp = true;
                } else if ((chr.getMapId() == 2010000) && (targetid == 104000000)) {
                    unlock = true;
                    warp = true;
                } else if ((chr.getMapId() == 106020001) || (chr.getMapId() == 106020502)) {
                    if (targetid == chr.getMapId() - 1) {
                        unlock = true;
                        warp = true;
                    }
                } else if ((chr.getMapId() == 0) && (targetid == 10000)) {
                    unlock = true;
                    warp = true;
                } else if ((chr.getMapId() == 931000011) && (targetid == 931000012)) {
                    unlock = true;
                    warp = true;
                } else if ((chr.getMapId() == 931000021) && (targetid == 931000030)) {
                    unlock = true;
                    warp = true;
                }
                if (unlock) {
                    c.getSession().write(UIPacket.IntroDisableUI(false));
                    c.getSession().write(UIPacket.IntroLock(false));
                    c.getSession().write(MaplePacketCreator.enableActions());
                }
                if (warp) {
                    MapleMap to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(targetid);
                    chr.changeMap(to, to.getPortal(0));
                }
            } else if ((portal != null) && (!chr.hasBlockedInventory())) {
                portal.enterPortal(c);
            } else {
                c.getSession().write(MaplePacketCreator.enableActions());
            }
        }

        if (chr.isAdmin()) {
            long endTime = System.currentTimeMillis() - startTime;
            chr.dropMessage(-11, new StringBuilder().append("切换地图完成 耗时: ").append(endTime).append(" 毫秒 ==> ").append(endTime / 1000L).append(" 秒..").toString());
        }
     //  c.getPlayer().setMaplewingmap();
    }

    public static void InnerPortal(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        if ((chr == null) || (chr.getMap() == null)) {
            return;
        }
        MaplePortal portal = chr.getMap().getPortal(slea.readMapleAsciiString());
        int toX = slea.readShort();
        int toY = slea.readShort();

        if (portal == null) {
            return;
        }
        if ((portal.getPosition().distanceSq(chr.getTruePosition()) > 22500.0D) && (!chr.isGM())) {
            chr.getCheatTracker().registerOffense(CheatingOffense.USING_FARAWAY_PORTAL);
            return;
        }
        chr.getMap().movePlayer(chr, new Point(toX, toY));
        chr.checkFollow();
    }

    public static void snowBall(LittleEndianAccessor slea, MapleClient c) {
        c.getSession().write(MaplePacketCreator.enableActions());
    }

    public static void leftKnockBack(LittleEndianAccessor slea, MapleClient c) {
        if (c.getPlayer().getMapId() / 10000 == 10906) {
            c.getSession().write(MaplePacketCreator.leftKnockBack());
            c.getSession().write(MaplePacketCreator.enableActions());
        }
    }

    public static void ReIssueMedal(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        MapleQuest q = MapleQuest.getInstance(slea.readShort());
        if ((q != null) && (q.getMedalItem() > 0) && (chr.getQuestStatus(q.getId()) == 2) && (!chr.haveItem(q.getMedalItem(), 1, true, true)) && (q.getMedalItem() == slea.readInt()) && (MapleInventoryManipulator.checkSpace(c, q.getMedalItem(), 1, ""))) {
            MapleInventoryManipulator.addById(c, q.getMedalItem(), (short) 1, new StringBuilder().append("Redeemed item through medal quest ").append(q.getId()).append(" on ").append(FileoutputUtil.CurrentReadable_Date()).toString());
        }
        c.getSession().write(MaplePacketCreator.enableActions());
    }

    public static void PlayerUpdate(MapleClient c, MapleCharacter chr) {
    }

    public static void TeachSkill(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        if ((chr == null) || (chr.getMap() == null) || (chr.hasBlockedInventory()) || (chr.getLevel() < 70)) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        int skillId = slea.readInt();
        if (chr.getSkillLevel(skillId) < 1) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        int toChrId = slea.readInt();
        Pair toChrInfo = MapleCharacterUtil.getNameById(toChrId, 0);
        if (toChrInfo == null) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        int toChrAccId = ((Integer) toChrInfo.getRight()).intValue();
        String toChrName = (String) toChrInfo.getLeft();
        MapleQuest quest = MapleQuest.getInstance(7783);
        if ((quest != null) && (chr.getAccountID() == toChrAccId)) {
            int toSkillId;
            if (GameConstants.is火炮手(chr.getJob())) {
                toSkillId = 80000000;
            } else {
                if (GameConstants.is恶魔猎手(chr.getJob())) {
                    toSkillId = 80000001;
                } else {
                    if (GameConstants.is双弩精灵(chr.getJob())) {
                        toSkillId = 80001040;
                    } else {
                        if (GameConstants.is幻影(chr.getJob())) {
                            toSkillId = 80000002;
                        } else {
                            chr.dropMessage(1, "传授技能失败");
                            c.getSession().write(MaplePacketCreator.enableActions());
                            return;
                        }
                    }
                }
            }
            if ((chr.teachSkill(toSkillId, toChrId) > 0) && (toSkillId >= 80000000)) {
                chr.changeTeachSkill(skillId, toChrId);
                quest.forceComplete(chr, 0);
                c.getSession().write(MaplePacketCreator.teachMessage(skillId, toChrId, toChrName));
            } else {
                chr.dropMessage(1, new StringBuilder().append("传授技能失败角色[").append(toChrName).append("]已经获得该技能").toString());
            }
        } else {
            chr.dropMessage(1, "传授技能失败。");
        }
        c.getSession().write(MaplePacketCreator.enableActions());
    }

    public static void ChangeMarketMap(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        if ((chr == null) || (chr.getMap() == null) || (chr.hasBlockedInventory())) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        int chc = slea.readByte() + 1;
        int toMapId = slea.readInt();

        if ((toMapId >= 910000001) && (toMapId <= 910000022)) {
            if (c.getChannel() != chc) {
                if (chr.getMapId() != toMapId) {
                    MapleMap to = ChannelServer.getInstance(chc).getMapFactory().getMap(toMapId);
                    chr.setMap(to);
                    chr.changeChannel(chc);
                } else {
                    chr.changeChannel(chc);
                }
            } else {
                MapleMap to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(toMapId);
                chr.changeMap(to, to.getPortal(0));
            }
        } else {
            c.getSession().write(MaplePacketCreator.enableActions());
        }
    }

    public static void 超时空卷(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        if ((chr == null) || (chr.getMap() == null) || (chr.hasBlockedInventory())) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        chr.updateTick(slea.readInt());
        int toMapId = slea.readInt();
        if (isBossMap(toMapId)) {
            c.getSession().write(MTSCSPacket.getTrockMessage((byte) 11));
            c.getSession().write(MaplePacketCreator.时空移动错误());
            return;
        }
        MapleMap moveTo = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(toMapId);
        if (chr.haveItem(5040005, 1)) {
            chr.removeAll(5040005);
            chr.changeMap(moveTo, moveTo.getPortal(0));
        } else {
            if (chr.getBossLog("超时空卷") < 30) {
                chr.setBossLog("超时空卷");
                chr.dropMessage(5, new StringBuilder().append("您使用了").append(c.getChannelServer().getServerName()).append("免费传送功能从 ").append(chr.getMap().getMapName()).append(" --> ").append(moveTo.getMapName()).append(" 今天还可以使用: ").append(30 - chr.getBossLog("超时空卷")).append(" 次。").toString());
                chr.changeMap(moveTo, moveTo.getPortal(0));
            } else if (chr.getCSPoints(2) >= 200) {
                chr.dropMessage(5, new StringBuilder().append("您使用了").append(c.getChannelServer().getServerName()).append("传送功能从 ").append(chr.getMap().getMapName()).append(" --> ").append(moveTo.getMapName()).append(" 抵用卷减少 200 点。").toString());
                chr.changeMap(moveTo, moveTo.getPortal(0));
                chr.modifyCSPoints(2, -200);
            } else {
                chr.dropMessage(5, "传送失败，您今天的免费传送次数已经用完或者您的抵用卷不足200点。");
            }
            if (chr.getBossLog("超时空卷") == 30) {
                chr.dropMessage(1, "今天的免费传送次数已经使用完\r\n在次使用将消耗抵用卷200点。");
            }
        }
    }

    public static boolean isBossMap(int mapid) {
        switch (mapid) {
            case 105100300:
            case 105100400:
            case 211070100:
            case 211070101:
            case 211070110:
            case 220080001:
            case 240040700:
            case 240060200:
            case 240060201:
            case 270050100:
            case 271040100:
            case 703200400:
            case 271040200:
            case 280030000:
            case 280030001:
            case 300030310:
            case 551030200:
            case 802000111:
            case 802000211:
            case 802000311:
            case 802000411:
            case 802000611:
            case 802000711:
            case 802000801:
            case 802000802:
            case 802000803:
            case 802000821:
            case 802000823:
                return true;
        }
        return false;
    }
}