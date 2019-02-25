package handling.channel.handler;

import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleDisease;
import client.PlayerStats;
import client.Skill;
import client.SkillFactory;
import client.SummonSkillEntry;
import client.anticheat.CheatTracker;
import client.anticheat.CheatingOffense;
import client.status.MonsterStatus;
import client.status.MonsterStatusEffect;
import constants.GameConstants;
import handling.world.World.Broadcast;
import java.awt.Point;
import java.awt.Rectangle;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import scripting.EventInstanceManager;
import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import server.Randomizer;
import server.Timer.CloneTimer;
import server.life.MapleMonster;
import server.maps.MapleDragon;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.MapleSummon;
import server.maps.SummonMovementType;
import tools.AttackPair;
import tools.MaplePacketCreator;
import tools.Pair;
import tools.data.LittleEndianAccessor;
import tools.packet.MobPacket;

public class SummonHandler {

    public static void MoveDragon(LittleEndianAccessor slea, MapleCharacter chr) {
        slea.skip(8);
        final List res = MovementParse.parseMovement(slea, 5);
        if ((chr != null) && (chr.getDragon() != null) && (res.size() > 0)) {
            Point pos = chr.getDragon().getPosition();
            MovementParse.updatePosition(res, chr.getDragon(), 0);
            if (!chr.isHidden()) {
                chr.getMap().broadcastMessage(chr, MaplePacketCreator.moveDragon(chr.getDragon(), pos, res), chr.getTruePosition());
            }

            WeakReference[] clones = chr.getClones();
            for (int i = 0; i < clones.length; i++) {
                if (clones[i].get() != null) {
                    final MapleMap map = chr.getMap();
                    final MapleCharacter clone = (MapleCharacter) clones[i].get();
                    CloneTimer.getInstance().schedule(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                if ((clone.getMap() == map) && (clone.getDragon() != null)) {
                                    Point startPos = clone.getDragon().getPosition();
                                    MovementParse.updatePosition(res, clone.getDragon(), 0);
                                    if (!clone.isHidden()) {
                                        map.broadcastMessage(clone, MaplePacketCreator.moveDragon(clone.getDragon(), startPos, res), clone.getTruePosition());
                                    }
                                }
                            } catch (Exception e) {
                            }
                        }
                    }, 500 * i + 500);
                }
            }
        }
    }

    public static void MoveSummon(LittleEndianAccessor slea, MapleCharacter chr) {
        if ((chr == null) || (chr.getMap() == null)) {
            return;
        }
        MapleMapObject obj = chr.getMap().getMapObject(slea.readInt(), MapleMapObjectType.SUMMON);
        if (obj == null) {
            return;
        }
        if ((obj instanceof MapleDragon)) {
            MoveDragon(slea, chr);
            return;
        }
        MapleSummon sum = (MapleSummon) obj;
        if ((sum.getOwnerId() != chr.getId()) || (sum.getSkillLevel() <= 0) || (sum.getMovementType() == SummonMovementType.STATIONARY)) {
            return;
        }
        slea.skip(8);
        List res = MovementParse.parseMovement(slea, 4);

        Point pos = sum.getPosition();
        MovementParse.updatePosition(res, sum, 0);
        if (res.size() > 0) {
            chr.getMap().broadcastMessage(chr, MaplePacketCreator.moveSummon(chr.getId(), sum.getObjectId(), pos, res), sum.getTruePosition());
        }
    }

    public static void DamageSummon(LittleEndianAccessor slea, MapleCharacter chr) {
        int unkByte = slea.readByte();
        int damage = slea.readInt();
        int monsterIdFrom = slea.readInt();

        Iterator iter = chr.getSummonsReadLock().iterator();

        boolean remove = false;
        try {
            while (iter.hasNext()) {
                MapleSummon summon = (MapleSummon) iter.next();
                if ((summon.isPuppet()) && (summon.getOwnerId() == chr.getId()) && (damage > 0)) {
                    summon.addHP((short) (-damage));
                    if (summon.getHP() <= 0) {
                        remove = true;
                    }
                    chr.getMap().broadcastMessage(chr, MaplePacketCreator.damageSummon(chr.getId(), summon.getSkill(), damage, unkByte, monsterIdFrom), summon.getTruePosition());
                }
            }
        } finally {
            chr.unlockSummonsReadLock();
        }
        if (remove) {
            chr.cancelEffectFromBuffStat(MapleBuffStat.ÃÊ…Ì ı);
        }
    }

    public static void SummonAttack(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        if ((chr == null) || (!chr.isAlive()) || (chr.getMap() == null)) {
            return;
        }
        MapleMap map = chr.getMap();
        MapleMapObject obj = map.getMapObject(slea.readInt(), MapleMapObjectType.SUMMON);
        if ((obj == null) || (!(obj instanceof MapleSummon))) {
            chr.dropMessage(5, "’ŸªΩ ﬁ“—æ≠œ˚ ß°£");
            return;
        }
        MapleSummon summon = (MapleSummon) obj;
        if ((summon.getOwnerId() != chr.getId()) || (summon.getSkillLevel() <= 0)) {
            chr.dropMessage(5, "≥ˆœ÷¥ÌŒÛ.");
            return;
        }
        SummonSkillEntry sse = SkillFactory.getSummonData(summon.getSkill());
        if ((summon.getSkill() / 1000000 != 35) && (summon.getSkill() != 33101008) && (sse == null)) {
            chr.dropMessage(5, "’ŸªΩ ﬁπ•ª˜¥¶¿Ì≥ˆ¥Ì°£");
            return;
        }

        int tick = slea.readInt();
        if ((sse != null) && (sse.delay > 0)) {
            chr.updateTick(tick);
            summon.CheckSummonAttackFrequency(chr, tick);
            chr.getCheatTracker().checkSummonAttack();
        }

        byte animation = slea.readByte();

        byte numAttacked = slea.readByte();
        if ((sse != null) && (numAttacked > sse.mobCount)) {
            chr.dropMessage(5, "[æØ∏Ê] «Î≤ª“™ π”√∑«∑®≥Ã–Ú°£");
            chr.getCheatTracker().registerOffense(CheatingOffense.SUMMON_HACK_MOBS);

            return;
        }
        slea.skip(summon.getSkill() == 35111002 ? 24 : 12);
        List<Pair<Integer, Integer>> allDamage = new ArrayList<Pair<Integer, Integer>>();
        for (int i = 0; i < numAttacked; i++) {
            MapleMonster mob = map.getMonsterByOid(slea.readInt());
            if (mob == null) {
                continue;
            }
            slea.skip(4);
            slea.skip(14);
            slea.skip(4);
            int damge = slea.readInt();
            if (chr.isGM()) {
                chr.dropMessage(-5, "’ŸªΩ ﬁπ•ª˜ ¥Úπ÷…À∫¶: " + damge + " π•ª˜¥Œ ˝: " + numAttacked);
            }
            if ((damge > 999999) && (!chr.isGM())) {
                damge = 999999;
            } else if ((damge > 99999999) && (!chr.isGM())) {
                damge = 99999999;
                Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM Message] " + chr.getName() + " (µ»º∂ " + chr.getLevel() + ") ’ŸªΩ ﬁπ•ª˜…À∫¶“Ï≥£°£¥Úπ÷…À∫¶: " + damge + " µÿÕºID: " + chr.getMapId()));
            }
            allDamage.add(new Pair(mob.getObjectId(), damge));
        }

        map.broadcastMessage(chr, MaplePacketCreator.summonAttack(summon.getOwnerId(), summon.getObjectId(), animation, allDamage, chr.getLevel(), false), summon.getTruePosition());

        Skill summonSkill = SkillFactory.getSkill(summon.getSkill());
        MapleStatEffect summonEffect = summonSkill.getEffect(summon.getSkillLevel());
        if (summonEffect == null) {
            chr.dropMessage(5, "’ŸªΩ ﬁπ•ª˜≥ˆœ÷¥ÌŒÛ.");
            return;
        }
        for (Pair<Integer, Integer> attackEntry : allDamage) {
            int toDamage = ((Integer) attackEntry.right).intValue();
            MapleMonster mob = map.getMonsterByOid(attackEntry.left);
            if (mob == null) {
                continue;
            }
            if ((sse != null) && (sse.delay > 0) && (summon.getMovementType() != SummonMovementType.STATIONARY) && (summon.getMovementType() != SummonMovementType.CIRCLE_STATIONARY) && (summon.getMovementType() != SummonMovementType.WALK_STATIONARY) && (chr.getTruePosition().distanceSq(mob.getTruePosition()) > 400000.0D)) {
                chr.getCheatTracker().registerOffense(CheatingOffense.ATTACK_FARAWAY_MONSTER_SUMMON);
            }
            if ((toDamage > 0) && (summonEffect.getMonsterStati().size() > 0)
                    && (summonEffect.makeChanceResult())) {
                for (Map.Entry<MonsterStatus,Integer> z : summonEffect.getMonsterStati().entrySet()) {
                    mob.applyStatus(chr, new MonsterStatusEffect(z.getKey(), z.getValue(), summonSkill.getId(), null, false), summonEffect.isPoison(), 4000, true, summonEffect);
                }
            }

            if ((chr.isGM()) || (toDamage < chr.getStat().getCurrentMaxBaseDamage() * 5.0D * (summonEffect.getSelfDestruction() + summonEffect.getDamage() + chr.getStat().getDamageIncrease(summonEffect.getSourceId())) / 100.0D)) {
                mob.damage(chr, toDamage, true);
                chr.checkMonsterAggro(mob);
                if (!mob.isAlive()) {
                    chr.getClient().getSession().write(MobPacket.killMonster(mob.getObjectId(), 1));
                }
            } else {
                chr.dropMessage(5, "[æØ∏Ê] ’ŸªΩ ﬁπ•ª˜π÷ŒÔ…À∫¶π˝∏ﬂ.");
                break;
            }
        }
        if (!summon.isMultiAttack()) {
            chr.getMap().broadcastMessage(MaplePacketCreator.removeSummon(summon, true));
            chr.getMap().removeMapObject(summon);
            chr.removeVisibleMapObject(summon);
            chr.removeSummon(summon);
            if (summon.getSkill() != 35121011) {
                chr.cancelEffectFromBuffStat(MapleBuffStat.’ŸªΩ ﬁ);
            }
        }
    }

    public static void RemoveSummon(LittleEndianAccessor slea, MapleClient c) {
        MapleMapObject obj = c.getPlayer().getMap().getMapObject(slea.readInt(), MapleMapObjectType.SUMMON);
        if ((obj == null) || (!(obj instanceof MapleSummon))) {
            return;
        }
        MapleSummon summon = (MapleSummon) obj;
        if ((summon.getOwnerId() != c.getPlayer().getId()) || (summon.getSkillLevel() <= 0)) {
            c.getPlayer().dropMessage(5, "“∆≥˝’ŸªΩ ﬁ≥ˆœ÷¥ÌŒÛ.");
            return;
        }
        if ((summon.getSkill() == 35111002) || (summon.getSkill() == 35121010)) {
            return;
        }
        c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.removeSummon(summon, true));
        c.getPlayer().getMap().removeMapObject(summon);
        c.getPlayer().removeVisibleMapObject(summon);
        c.getPlayer().removeSummon(summon);
        if (summon.getSkill() != 35121011) {
            c.getPlayer().cancelEffectFromBuffStat(MapleBuffStat.’ŸªΩ ﬁ);
        }
    }

    public static void SubSummon(LittleEndianAccessor slea, MapleCharacter chr) {
        MapleMapObject obj = chr.getMap().getMapObject(slea.readInt(), MapleMapObjectType.SUMMON);
        if ((obj == null) || (!(obj instanceof MapleSummon))) {
            return;
        }
        MapleSummon sum = (MapleSummon) obj;
        if ((sum == null) || (sum.getOwnerId() != chr.getId()) || (sum.getSkillLevel() <= 0) || (!chr.isAlive())) {
            return;
        }
        switch (sum.getSkill()) {
            case 35121009:
                if (!chr.canSummon(2000)) {
                    return;
                }
                for (int i = 0; i < 3; i++) {
                    MapleSummon tosummon = new MapleSummon(chr, SkillFactory.getSkill(35121011).getEffect(sum.getSkillLevel()), new Point(sum.getTruePosition().x, sum.getTruePosition().y - 5), SummonMovementType.WALK_STATIONARY);
                    chr.getMap().spawnSummon(tosummon);
                    chr.addSummon(tosummon);
                }
                break;
            case 35111011:
                if (!chr.canSummon(1000)) {
                    return;
                }
                chr.addHP((int) (chr.getStat().getCurrentMaxHp() * SkillFactory.getSkill(sum.getSkill()).getEffect(sum.getSkillLevel()).getHp() / 100.0D));
                chr.getClient().getSession().write(MaplePacketCreator.showOwnBuffEffect(sum.getSkill(), 2, chr.getLevel(), sum.getSkillLevel()));
                chr.getMap().broadcastMessage(chr, MaplePacketCreator.showBuffeffect(chr.getId(), sum.getSkill(), 2, chr.getLevel(), sum.getSkillLevel()), false);
                break;
            case 1321007:
                Skill bHealing = SkillFactory.getSkill(slea.readInt());
                int bHealingLvl = chr.getTotalSkillLevel(bHealing);
                if ((bHealingLvl <= 0) || (bHealing == null)) {
                    return;
                }
                MapleStatEffect healEffect = bHealing.getEffect(bHealingLvl);
                if (bHealing.getId() == 1320009) {
                    healEffect.applyTo(chr);
                } else if (bHealing.getId() == 1320008) {
                    if (!chr.canSummon(healEffect.getX() * 1000)) {
                        return;
                    }
                    chr.addHP(healEffect.getHp());
                }
                chr.getClient().getSession().write(MaplePacketCreator.showOwnBuffEffect(sum.getSkill(), 2, chr.getLevel(), bHealingLvl));
                chr.getMap().broadcastMessage(MaplePacketCreator.summonSkill(chr.getId(), sum.getSkill(), bHealing.getId() == 1320008 ? 5 : Randomizer.nextInt(3) + 6));
                chr.getMap().broadcastMessage(chr, MaplePacketCreator.showBuffeffect(chr.getId(), sum.getSkill(), 2, chr.getLevel(), bHealingLvl), false);
        }

        if (GameConstants.isAngel(sum.getSkill())) {
            if (sum.getSkill() % 10000 == 1087) {
                MapleItemInformationProvider.getInstance().getItemEffect(2022747).applyTo(chr);
            } else if (sum.getSkill() % 10000 == 1179) {
                MapleItemInformationProvider.getInstance().getItemEffect(2022823).applyTo(chr);
            } else {
                MapleItemInformationProvider.getInstance().getItemEffect(2022746).applyTo(chr);
            }
            chr.getClient().getSession().write(MaplePacketCreator.showOwnBuffEffect(sum.getSkill(), 2, 2, 1));
            chr.getMap().broadcastMessage(chr, MaplePacketCreator.showBuffeffect(chr.getId(), sum.getSkill(), 2, 2, 1), false);
        }
    }

    public static void SummonPVP(LittleEndianAccessor slea, MapleClient c) {
        MapleCharacter chr = c.getPlayer();
        if ((chr == null) || (chr.isHidden()) || (!chr.isAlive()) || (chr.hasBlockedInventory()) || (chr.getMap() == null) || (!chr.inPVP()) || (!chr.getEventInstance().getProperty("started").equals("1"))) {
            return;
        }
        MapleMap map = chr.getMap();
        MapleMapObject obj = map.getMapObject(slea.readInt(), MapleMapObjectType.SUMMON);
        if ((obj == null) || (!(obj instanceof MapleSummon))) {
            chr.dropMessage(5, "’ŸªΩ ﬁ“—æ≠œ˚ ß°£");
            return;
        }
        int tick = -1;
        if (slea.available() == 27L) {
            slea.skip(23);
            tick = slea.readInt();
        }
        MapleSummon summon = (MapleSummon) obj;
        if ((summon.getOwnerId() != chr.getId()) || (summon.getSkillLevel() <= 0)) {
            chr.dropMessage(5, "≥ˆœ÷¥ÌŒÛ");
            return;
        }
        Skill skil = SkillFactory.getSkill(summon.getSkill());
        MapleStatEffect effect = skil.getEffect(summon.getSkillLevel());
        int lvl = Integer.parseInt(chr.getEventInstance().getProperty("lvl"));
        int type = Integer.parseInt(chr.getEventInstance().getProperty("type"));
        int ourScore = Integer.parseInt(chr.getEventInstance().getProperty(String.valueOf(chr.getId())));
        int addedScore = 0;
        boolean magic = skil.isMagic();
        boolean killed = false;
        boolean didAttack = false;
        double maxdamage = chr.getStat().getCurrentMaxBasePVPDamage();
        maxdamage *= (effect.getDamage() + chr.getStat().getDamageIncrease(summon.getSkill())) / 100.0D;
        int mobCount = 1;
        int attackCount = 1;
        int ignoreDEF = chr.getStat().ignoreTargetDEF;

        SummonSkillEntry sse = SkillFactory.getSummonData(summon.getSkill());
        if ((summon.getSkill() / 1000000 != 35) && (summon.getSkill() != 33101008) && (sse == null)) {
            chr.dropMessage(5, "’ŸªΩ ﬁπ•ª˜¥¶¿Ì≥ˆ¥Ì°£");
            return;
        }
        Point rb;
        Point lt;
        if (sse != null) {
            if (sse.delay > 0) {
                if (tick != -1) {
                    summon.CheckSummonAttackFrequency(chr, tick);
                    chr.updateTick(tick);
                } else {
                    summon.CheckPVPSummonAttackFrequency(chr);
                }
                chr.getCheatTracker().checkSummonAttack();
            }
            mobCount = sse.mobCount;
            attackCount = sse.attackCount;
            lt = sse.lt;
            rb = sse.rb;
        } else {
            lt = new Point(-100, -100);
            rb = new Point(100, 100);
        }
        Rectangle box = MapleStatEffect.calculateBoundingBox(chr.getTruePosition(), chr.isFacingLeft(), lt, rb, 0);
        List ourAttacks = new ArrayList();

        maxdamage *= chr.getStat().dam_r / 100.0D;
        for (MapleMapObject mo : chr.getMap().getCharactersIntersect(box)) {
            MapleCharacter attacked = (MapleCharacter) mo;
            if ((attacked.getId() != chr.getId()) && (attacked.isAlive()) && (!attacked.isHidden()) && ((type == 0) || (attacked.getTeam() != chr.getTeam()))) {
                double rawDamage = maxdamage / Math.max(0.0D, (magic ? attacked.getStat().mdef : attacked.getStat().wdef) * Math.max(1.0D, 100.0D - ignoreDEF) / 100.0D * (type == 3 ? 0.1D : 0.25D));
                if ((attacked.getBuffedValue(MapleBuffStat.INVINCIBILITY) != null) || (PlayersHandler.inArea(attacked))) {
                    rawDamage = 0.0D;
                }
                rawDamage += rawDamage * chr.getDamageIncrease(attacked.getId()) / 100.0D;
                rawDamage *= attacked.getStat().mesoGuard / 100.0D;
                rawDamage = ((Double) attacked.modifyDamageTaken(rawDamage, attacked).left).doubleValue();
                double min = rawDamage * chr.getStat().trueMastery / 100.0D;
                List attacks = new ArrayList(attackCount);
                int totalMPLoss = 0;
                int totalHPLoss = 0;
                for (int i = 0; i < attackCount; i++) {
                    int mploss = 0;
                    double ourDamage = Randomizer.nextInt((int) Math.abs(Math.round(rawDamage - min)) + 1) + min;
                    if ((attacked.getStat().dodgeChance > 0) && (Randomizer.nextInt(100) < attacked.getStat().dodgeChance)) {
                        ourDamage = 0.0D;
                    }

                    if (attacked.getBuffedValue(MapleBuffStat.ƒß∑®∂‹) != null) {
                        mploss = (int) Math.min(attacked.getStat().getMp(), ourDamage * attacked.getBuffedValue(MapleBuffStat.ƒß∑®∂‹).doubleValue() / 100.0D);
                    }
                    ourDamage -= mploss;
                    if (attacked.getBuffedValue(MapleBuffStat.÷’º´Œﬁœﬁ) != null) {
                        mploss = 0;
                    }
                    attacks.add(new Pair(Integer.valueOf((int) Math.floor(ourDamage)), Boolean.valueOf(false)));

                    totalHPLoss = (int) (totalHPLoss + Math.floor(ourDamage));
                    totalMPLoss += mploss;
                }
                attacked.addMPHP(-totalHPLoss, -totalMPLoss);
                ourAttacks.add(new AttackPair(attacked.getId(), attacked.getPosition(), attacks));
                attacked.getCheatTracker().setAttacksWithoutHit(false);
                if (totalHPLoss > 0) {
                    didAttack = true;
                }
                if (attacked.getStat().getHPPercent() <= 20) {
                    SkillFactory.getSkill(PlayerStats.getSkillByJob(93, attacked.getJob())).getEffect(1).applyTo(attacked);
                }
                if (effect != null) {
                    if ((effect.getMonsterStati().size() > 0) && (effect.makeChanceResult())) {
                        for (Map.Entry z : effect.getMonsterStati().entrySet()) {
                            MapleDisease d = MonsterStatus.getLinkedDisease((MonsterStatus) z.getKey());
                            if (d != null) {
                                attacked.giveDebuff(d, ((Integer) z.getValue()).intValue(), effect.getDuration(), d.getDisease(), 1);
                            }
                        }
                    }
                    effect.handleExtraPVP(chr, attacked);
                }
                chr.getClient().getSession().write(MaplePacketCreator.getPVPHPBar(attacked.getId(), attacked.getStat().getHp(), attacked.getStat().getCurrentMaxHp()));
                addedScore += totalHPLoss / 100 + totalMPLoss / 100;
                if (!attacked.isAlive()) {
                    killed = true;
                }

                if (ourAttacks.size() >= mobCount) {
                    break;
                }
            }
        }
        if ((killed) || (addedScore > 0)) {
            chr.getEventInstance().addPVPScore(chr, addedScore);
            chr.getClient().getSession().write(MaplePacketCreator.getPVPScore(ourScore + addedScore, killed));
        }
        if (didAttack) {
            chr.getMap().broadcastMessage(MaplePacketCreator.pvpSummonAttack(chr.getId(), chr.getLevel(), summon.getObjectId(), summon.isFacingLeft() ? 4 : 132, summon.getTruePosition(), ourAttacks));
            if (!summon.isMultiAttack()) {
                chr.getMap().broadcastMessage(MaplePacketCreator.removeSummon(summon, true));
                chr.getMap().removeMapObject(summon);
                chr.removeVisibleMapObject(summon);
                chr.removeSummon(summon);
                if (summon.getSkill() != 35121011) {
                    chr.cancelEffectFromBuffStat(MapleBuffStat.’ŸªΩ ﬁ);
                }
            }
        }
    }
}