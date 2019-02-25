package handling.channel.handler;

import client.*;
import constants.GameConstants;
import java.util.EnumMap;
import java.util.Map;
import server.Randomizer;
import server.ServerProperties;
import tools.MaplePacketCreator;
import tools.Pair;
import tools.data.LittleEndianAccessor;

public class StatsHandling {

    public static void DistributeAP(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        Map statupdate = new EnumMap(MapleStat.class);
        c.getSession().write(MaplePacketCreator.updatePlayerStats(statupdate, true, chr));
        chr.updateTick(slea.readInt());

        PlayerStats stat = chr.getStat();
        int job = chr.getJob();
        int statLimit = c.getChannelServer().getStatLimit();
        if (chr.getRemainingAp() > 0) {
            switch (slea.readInt()) {
                case 64:
                    if (stat.getStr() >= statLimit) {
                        return;
                    }
                    stat.setStr((short) (stat.getStr() + 1), chr);
                    statupdate.put(MapleStat.力量, Integer.valueOf(stat.getStr()));
                    break;
                case 128:
                    if (stat.getDex() >= statLimit) {
                        return;
                    }
                    stat.setDex((short) (stat.getDex() + 1), chr);
                    statupdate.put(MapleStat.敏捷, Integer.valueOf(stat.getDex()));
                    break;
                case 256:
                    if (stat.getInt() >= statLimit) {
                        return;
                    }
                    stat.setInt((short) (stat.getInt() + 1), chr);
                    statupdate.put(MapleStat.智力, Integer.valueOf(stat.getInt()));
                    break;
                case 512:
                    if (stat.getLuk() >= statLimit) {
                        return;
                    }
                    stat.setLuk((short) (stat.getLuk() + 1), chr);
                    statupdate.put(MapleStat.运气, Integer.valueOf(stat.getLuk()));
                    break;
                case 2048:
                    int maxhp = stat.getMaxHp();
                    if ((chr.getHpApUsed() >= 10000) || (maxhp >= 99999)) {
                        return;
                    }
                    if (GameConstants.is新手职业(job)) {
                        maxhp += Randomizer.rand(8, 12);
                    } else if (((job >= 100) && (job <= 132)) || (GameConstants.is米哈尔(job)) || ((job >= 3200) && (job <= 3212)) || ((job >= 1100) && (job <= 1112)) || ((job >= 3100) && (job <= 3112))) {
                        maxhp += Randomizer.rand(36, 42);
                    } else if (((job >= 200) && (job <= 232)) || (GameConstants.is龙神(job))) {
                        maxhp += Randomizer.rand(10, 20);
                    } else if (((job >= 300) && (job <= 322)) || ((job >= 400) && (job <= 434)) || ((job >= 1300) && (job <= 1312)) || ((job >= 1400) && (job <= 1412)) || ((job >= 3300) && (job <= 3312)) || ((job >= 2300) && (job <= 2312)) || ((job >= 2400) && (job <= 2412))) {
                        maxhp += Randomizer.rand(16, 20);
                    } else if (((job >= 510) && (job <= 512)) || ((job >= 1510) && (job <= 1512))) {
                        maxhp += Randomizer.rand(28, 32);
                    } else if (((job >= 500) && (job <= 532)) || (GameConstants.is龙的传人(job)) || ((job >= 3500) && (job <= 3512)) || (job == 1500)) {
                        maxhp += Randomizer.rand(18, 22);
                    } else if ((job >= 1200) && (job <= 1212)) {
                        maxhp += Randomizer.rand(15, 21);
                    } else if ((job >= 2000) && (job <= 2112)) {
                        maxhp += Randomizer.rand(38, 42);
                    } else {
                        maxhp += Randomizer.rand(18, 26);
                    }
                    maxhp = Math.min(99999, Math.abs(maxhp));
                    chr.setHpApUsed((short) (chr.getHpApUsed() + 1));
                    stat.setMaxHp(maxhp, chr);
                    statupdate.put(MapleStat.MAXHP, Integer.valueOf(maxhp));
                    break;
                case 8192:
                    int maxmp = stat.getMaxMp();
                    if ((chr.getHpApUsed() >= 10000) || (stat.getMaxMp() >= 99999)) {
                        return;
                    }
                    if (GameConstants.is新手职业(job)) {
                        maxmp += Randomizer.rand(6, 8);
                    } else {
                        if ((job >= 3100) && (job <= 3112)) {
                            return;
                        }
                        if (((job >= 200) && (job <= 232)) || (GameConstants.is龙神(job)) || ((job >= 3200) && (job <= 3212)) || ((job >= 1200) && (job <= 1212))) {
                            maxmp += Randomizer.rand(38, 40);
                        } else if (((job >= 300) && (job <= 322)) || ((job >= 400) && (job <= 434)) || ((job >= 500) && (job <= 532)) || (GameConstants.is龙的传人(job)) || ((job >= 3200) && (job <= 3212)) || ((job >= 3500) && (job <= 3512)) || ((job >= 1300) && (job <= 1312)) || ((job >= 1400) && (job <= 1412)) || ((job >= 1500) && (job <= 1512)) || ((job >= 2300) && (job <= 2312)) || ((job >= 2400) && (job <= 2412))) {
                            maxmp += Randomizer.rand(10, 12);
                        } else if (((job >= 100) && (job <= 132)) || ((job >= 1100) && (job <= 1112)) || ((job >= 2000) && (job <= 2112)) || (GameConstants.is米哈尔(job))) {
                            maxmp += Randomizer.rand(6, 9);
                        } else {
                            maxmp += Randomizer.rand(6, 12);
                        }
                    }
                    maxmp = Math.min(99999, Math.abs(maxmp));
                    chr.setHpApUsed((short) (chr.getHpApUsed() + 1));
                    stat.setMaxMp(maxmp, chr);
                    statupdate.put(MapleStat.MAXMP, Integer.valueOf(maxmp));
                    break;
                default:
                    c.getSession().write(MaplePacketCreator.updatePlayerStats(MaplePacketCreator.EMPTY_STATUPDATE, true, chr));
                    return;
            }
            chr.setRemainingAp((short) (chr.getRemainingAp() - 1));
            statupdate.put(MapleStat.AVAILABLEAP, Integer.valueOf(chr.getRemainingAp()));
            c.getSession().write(MaplePacketCreator.updatePlayerStats(statupdate, true, chr));
        }
    }

    //(chr.isAdmin()) && (ServerProperties.ShowPacket())
    
    public static void DistributeSP(int skillid, MapleClient c, MapleCharacter chr) {
        if (chr.isAdmin()) {
            chr.dropMessage(5, "开始加技能点 - 技能ID: " + skillid);
        }
        boolean isBeginnerSkill = false;
        int remainingSp;
        if ((GameConstants.is新手职业(skillid / 10000)) && ((skillid % 10000 == 1000) || (skillid % 10000 == 1001) || (skillid % 10000 == 1002) || (skillid % 10000 == 2))) {
            boolean resistance = (skillid / 10000 == 3000) || (skillid / 10000 == 3001);
            int snailsLevel = chr.getSkillLevel(SkillFactory.getSkill(skillid / 10000 * 10000 + 1000));
            int recoveryLevel = chr.getSkillLevel(SkillFactory.getSkill(skillid / 10000 * 10000 + 1001));
            int nimbleFeetLevel = chr.getSkillLevel(SkillFactory.getSkill(skillid / 10000 * 10000 + (resistance ? 2 : 1002)));
            remainingSp = Math.min(chr.getLevel() - 1, resistance ? 9 : 6) - snailsLevel - recoveryLevel - nimbleFeetLevel;
            isBeginnerSkill = true;
        } else {
            if (GameConstants.is新手职业(skillid / 10000)) {
                if (chr.isAdmin()) {
                    chr.dropMessage(5, "加技能点错误 - 1");
                }
                c.getSession().write(MaplePacketCreator.enableActions());
                return;
            }
            remainingSp = chr.getRemainingSp(GameConstants.getSkillBookForSkill(skillid));
        }
        Skill skill = SkillFactory.getSkill(skillid);
        for (Pair ski : skill.getRequiredSkills()) {
            if (chr.getSkillLevel(SkillFactory.getSkill(((Integer) ski.left).intValue())) < ((Byte) ski.right).byteValue()) {
                if (chr.isAdmin()) {
                    chr.dropMessage(5, "加技能点错误 - 2");
                }
                c.getSession().write(MaplePacketCreator.enableActions());
                return;
            }
        }
        int maxlevel = skill.isFourthJob() ? chr.getMasterLevel(skill) : skill.getMaxLevel();
        int curLevel = chr.getSkillLevel(skill);
        if ((skill.isInvisible()) && (chr.getSkillLevel(skill) == 0) && (((skill.isFourthJob()) && (chr.getMasterLevel(skill) == 0)) || ((!skill.isFourthJob()) && (maxlevel < 10) && (!isBeginnerSkill)))) {
            if (chr.isAdmin()) {
                chr.dropMessage(5, "加技能点错误 - 3");
            }
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }

        for (int i : GameConstants.blockedSkills) {
            if (skill.getId() == i) {
                chr.dropMessage(1, "这个技能未修复，暂时无法加点.");
                c.getSession().write(MaplePacketCreator.enableActions());
                return;
            }
        }
        if (chr.isAdmin()) {
            chr.dropMessage(5, "开始加技能点 - remainingSp: " + remainingSp + " curLevel: " + curLevel + " maxlevel: " + maxlevel + " canBeLearnedBy: " + skill.canBeLearnedBy(chr.getJob()));
        }
        if ((remainingSp > 0) && (curLevel + 1 <= maxlevel) && (skill.canBeLearnedBy(chr.getJob()))) {
            if (!isBeginnerSkill) {
                int skillbook = GameConstants.getSkillBookForSkill(skillid);
                chr.setRemainingSp(chr.getRemainingSp(skillbook) - 1, skillbook);
            }
            c.getSession().write(MaplePacketCreator.updateSp(chr, false));
            chr.changeSkillLevel(skill, (byte) (curLevel + 1), chr.getMasterLevel(skill));
        } else {
            if (chr.isAdmin()) {
                chr.dropMessage(5, "加技能点错误 - 4");
            }
            c.getSession().write(MaplePacketCreator.enableActions());
        }
    }

    public static void AutoAssignAP(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        chr.updateTick(slea.readInt());
        slea.skip(4);
        if (slea.available() < 16L) {
            return;
        }
        int PrimaryStat = (int) slea.readLong();
        int amount = slea.readInt();
        int SecondaryStat = (int) slea.readLong();
        int amount2 = slea.readInt();
        if ((amount < 0) || (amount2 < 0)) {
            return;
        }
        PlayerStats playerst = chr.getStat();

        Map statupdate = new EnumMap(MapleStat.class);
        c.getSession().write(MaplePacketCreator.updatePlayerStats(statupdate, true, chr));
        int statLimit = c.getChannelServer().getStatLimit();
        if (chr.getRemainingAp() == amount + amount2) {
            switch (PrimaryStat) {
                case 64:
                    if (playerst.getStr() + amount > statLimit) {
                        return;
                    }
                    playerst.setStr((short) (playerst.getStr() + amount), chr);
                    statupdate.put(MapleStat.力量, Integer.valueOf(playerst.getStr()));
                    break;
                case 128:
                    if (playerst.getDex() + amount > statLimit) {
                        return;
                    }
                    playerst.setDex((short) (playerst.getDex() + amount), chr);
                    statupdate.put(MapleStat.敏捷, Integer.valueOf(playerst.getDex()));
                    break;
                case 256:
                    if (playerst.getInt() + amount > statLimit) {
                        return;
                    }
                    playerst.setInt((short) (playerst.getInt() + amount), chr);
                    statupdate.put(MapleStat.智力, Integer.valueOf(playerst.getInt()));
                    break;
                case 512:
                    if (playerst.getLuk() + amount > statLimit) {
                        return;
                    }
                    playerst.setLuk((short) (playerst.getLuk() + amount), chr);
                    statupdate.put(MapleStat.运气, Integer.valueOf(playerst.getLuk()));
                    break;
                default:
                    c.getSession().write(MaplePacketCreator.updatePlayerStats(MaplePacketCreator.EMPTY_STATUPDATE, true, chr));
                    return;
            }
            switch (SecondaryStat) {
                case 64:
                    if (playerst.getStr() + amount2 > statLimit) {
                        return;
                    }
                    playerst.setStr((short) (playerst.getStr() + amount2), chr);
                    statupdate.put(MapleStat.力量, Integer.valueOf(playerst.getStr()));
                    break;
                case 128:
                    if (playerst.getDex() + amount2 > statLimit) {
                        return;
                    }
                    playerst.setDex((short) (playerst.getDex() + amount2), chr);
                    statupdate.put(MapleStat.敏捷, Integer.valueOf(playerst.getDex()));
                    break;
                case 256:
                    if (playerst.getInt() + amount2 > statLimit) {
                        return;
                    }
                    playerst.setInt((short) (playerst.getInt() + amount2), chr);
                    statupdate.put(MapleStat.智力, Integer.valueOf(playerst.getInt()));
                    break;
                case 512:
                    if (playerst.getLuk() + amount2 > statLimit) {
                        return;
                    }
                    playerst.setLuk((short) (playerst.getLuk() + amount2), chr);
                    statupdate.put(MapleStat.运气, Integer.valueOf(playerst.getLuk()));
                    break;
                default:
                    c.getSession().write(MaplePacketCreator.updatePlayerStats(MaplePacketCreator.EMPTY_STATUPDATE, true, chr));
                    return;
            }
            chr.setRemainingAp((short) (chr.getRemainingAp() - (amount + amount2)));
            statupdate.put(MapleStat.AVAILABLEAP, Integer.valueOf(chr.getRemainingAp()));
            c.getSession().write(MaplePacketCreator.updatePlayerStats(statupdate, true, chr));
        }
    }
}
