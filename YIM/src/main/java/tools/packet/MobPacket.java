package tools.packet;

import client.status.MonsterStatus;
import client.status.MonsterStatusEffect;
import constants.GameConstants;
import handling.SendPacketOpcode;
import java.awt.Point;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.log4j.Logger;
import server.life.ChangeableStats;
import server.life.MapleMonster;
import server.life.MapleMonsterStats;
import server.life.MobSkill;
import server.movement.LifeMovementFragment;
import tools.ConcurrentEnumMap;
import tools.MaplePacketCreator;
import tools.data.MaplePacketLittleEndianWriter;

public class MobPacket {

    private static final Logger log = Logger.getLogger(MobPacket.class);
    private static boolean showPacket = false;

    public static byte[] damageMonster(int oid, long damage) {
        if (showPacket) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.DAMAGE_MONSTER.getValue());
        mplew.writeInt(oid);
        mplew.write(0);
        if (damage > 2147483647L) {
            mplew.writeInt(2147483647);
        } else {
            mplew.writeInt((int) damage);
        }

        return mplew.getPacket();
    }

    public static byte[] damageFriendlyMob(MapleMonster mob, long damage, boolean display) {
        if (showPacket) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.DAMAGE_MONSTER.getValue());
        mplew.writeInt(mob.getObjectId());
        mplew.write(display ? 1 : 2);
        if (damage > 2147483647L) {
            mplew.writeInt(2147483647);
        } else {
            mplew.writeInt((int) damage);
        }
        if (mob.getHp() > 2147483647L) {
            mplew.writeInt((int) (mob.getHp() / mob.getMobMaxHp() * 2147483647.0D));
        } else {
            mplew.writeInt((int) mob.getHp());
        }
        if (mob.getMobMaxHp() > 2147483647L) {
            mplew.writeInt(2147483647);
        } else {
            mplew.writeInt((int) mob.getMobMaxHp());
        }

        return mplew.getPacket();
    }

    public static byte[] killMonster(int oid, int animation) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.KILL_MONSTER.getValue());
        mplew.writeInt(oid);
        mplew.write(animation);
        if (animation == 4) {
            mplew.writeInt(-1);
        }

        return mplew.getPacket();
    }

    public static byte[] suckMonster(int oid, int chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.KILL_MONSTER.getValue());
        mplew.writeInt(oid);
        mplew.write(4);
        mplew.writeInt(chr);

        return mplew.getPacket();
    }

    public static byte[] healMonster(int oid, int heal) {
        if (showPacket) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.DAMAGE_MONSTER.getValue());
        mplew.writeInt(oid);
        mplew.write(0);
        mplew.writeInt(-heal);

        return mplew.getPacket();
    }

    public static byte[] showMonsterHP(int oid, int remhppercentage) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_MONSTER_HP.getValue());
        mplew.writeInt(oid);
        mplew.write(remhppercentage);

        return mplew.getPacket();
    }

    public static byte[] showBossHP(MapleMonster mob) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.BOSS_ENV.getValue());
        mplew.write(5);
        mplew.writeInt(mob.getId() == 9400589 ? 9300184 : mob.getId());
        if (mob.getHp() > 2147483647L) {
            mplew.writeInt((int) (mob.getHp() / mob.getMobMaxHp() * 2147483647.0D));
        } else {
            mplew.writeInt((int) mob.getHp());
        }
        if (mob.getMobMaxHp() > 2147483647L) {
            mplew.writeInt(2147483647);
        } else {
            mplew.writeInt((int) mob.getMobMaxHp());
        }
        mplew.write(mob.getStats().getTagColor());
        mplew.write(mob.getStats().getTagBgColor());

        return mplew.getPacket();
    }

    public static byte[] showBossHP(int monsterId, long currentHp, long maxHp) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.BOSS_ENV.getValue());
        mplew.write(5);
        mplew.writeInt(monsterId);
        if (currentHp > 2147483647L) {
            mplew.writeInt((int) (currentHp / maxHp * 2147483647.0D));
        } else {
            mplew.writeInt((int) (currentHp <= 0L ? -1L : currentHp));
        }
        if (maxHp > 2147483647L) {
            mplew.writeInt(2147483647);
        } else {
            mplew.writeInt((int) maxHp);
        }
        mplew.write(6);
        mplew.write(5);

        return mplew.getPacket();
    }

    public static byte[] moveMonster(boolean useskill, int action, int skillId, int skillLevel, int delay, int oid, Point startPos, List<LifeMovementFragment> moves) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MOVE_MONSTER.getValue());
        mplew.writeInt(oid);
        mplew.write(useskill ? 1 : 0);
        mplew.write(action);
        mplew.write(skillId);
        mplew.write(skillLevel);
        mplew.writeShort(delay);
        mplew.writeShort(0);
        mplew.writePos(startPos);
        mplew.writeInt(0);
        PacketHelper.serializeMovementList(mplew, moves);

        return mplew.getPacket();
    }

    public static byte[] spawnMonster(MapleMonster life, int spawnType, int link) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPAWN_MONSTER.getValue());
        mplew.writeInt(life.getObjectId());
        mplew.write(1);
        mplew.writeInt(life.getId());

        mplew.writeZeroBytes(25);
        mplew.write(224);
        mplew.writeShort(19);
        mplew.writeInt(0);
        mplew.write(136);
        for (int i = 1; i <= 4; i++) {
            mplew.writeLong(0);
            mplew.writeShort(Integer.valueOf((int) System.currentTimeMillis()).shortValue());
        }
        mplew.writeInt(0);
        mplew.write(0);//104
        mplew.writePos(life.getTruePosition());
        mplew.write(life.getStance());
        mplew.writeShort(0);
        mplew.writeShort(life.getFh());
        mplew.write(spawnType);
        if ((spawnType == -3) || (spawnType >= 0)) {
            mplew.writeInt(link);
        }
        mplew.write(life.getCarnivalTeam());
        mplew.writeInt(0);//104
        mplew.writeInt(0);
        mplew.write(-1);

        return mplew.getPacket();
    }

    public static void addMonsterStatus(MaplePacketLittleEndianWriter mplew, MapleMonster life) {
        if (life.getStati().size() <= 1) {
            life.addEmpty();
        }
        if (GameConstants.GMS) {
            mplew.write(life.getChangedStats() != null ? 1 : 0);
            if (life.getChangedStats() != null) {
                mplew.writeInt(life.getChangedStats().hp > 2147483647L ? 2147483647 : (int) life.getChangedStats().hp);
                mplew.writeInt(life.getChangedStats().mp);
                mplew.writeInt(life.getChangedStats().exp);
                mplew.writeInt(life.getChangedStats().watk);
                mplew.writeInt(life.getChangedStats().matk);
                mplew.writeInt(life.getChangedStats().PDRate);
                mplew.writeInt(life.getChangedStats().MDRate);
                mplew.writeInt(life.getChangedStats().acc);
                mplew.writeInt(life.getChangedStats().eva);
                mplew.writeInt(life.getChangedStats().pushed);
                mplew.writeInt(life.getChangedStats().level);
            }
        }
        boolean ignore_imm = (life.getStati().containsKey(MonsterStatus.反射物攻)) || (life.getStati().containsKey(MonsterStatus.反射魔攻));
        Collection<MonsterStatusEffect> buffs = life.getStati().values();
        getLongMask_NoRef(mplew, buffs, ignore_imm);
        for (MonsterStatusEffect buff : buffs) {
            if ((buff != null) && (buff.getStati() != MonsterStatus.反射物攻) && (buff.getStati() != MonsterStatus.反射魔攻) && ((!ignore_imm) || ((buff.getStati() != MonsterStatus.免疫物攻) && (buff.getStati() != MonsterStatus.免疫魔攻) && (buff.getStati() != MonsterStatus.免疫伤害)))) {
                if ((buff.getStati() != MonsterStatus.召唤怪物) && ((buff.getStati() != MonsterStatus.EMPTY_2) || (GameConstants.GMS)) && ((buff.getStati() != MonsterStatus.EMPTY_3) || (!GameConstants.GMS))) {
                    if ((buff.getStati() == MonsterStatus.EMPTY_1) || (buff.getStati() == MonsterStatus.EMPTY_2) || (buff.getStati() == MonsterStatus.EMPTY_3) || (buff.getStati() == MonsterStatus.EMPTY_4) || (buff.getStati() == MonsterStatus.EMPTY_5) || (buff.getStati() == MonsterStatus.EMPTY_6)) {
                        mplew.writeShort(Integer.valueOf((int) System.currentTimeMillis()).shortValue());
                        if (GameConstants.GMS) {
                            mplew.writeShort(0);
                        }
                    } else if (GameConstants.GMS) {
                        mplew.writeInt(buff.getX().intValue());
                    } else {
                        mplew.writeShort(buff.getX().shortValue());
                    }

                    if (buff.getMobSkill() != null) {
                        mplew.writeShort(buff.getMobSkill().getSkillId());
                        mplew.writeShort(buff.getMobSkill().getSkillLevel());
                    } else if (buff.getSkill() > 0) {
                        mplew.writeInt(buff.getSkill());
                    }
                }
                mplew.writeShort(buff.getStati().isEmpty() ? 0 : buff.getStati() == MonsterStatus.心灵控制 ? 40 : 1);
                if ((buff.getStati() == MonsterStatus.EMPTY_1) || (buff.getStati() == MonsterStatus.EMPTY_3)) {
                    mplew.writeShort(0);
                } else if ((buff.getStati() == MonsterStatus.EMPTY_4) || (buff.getStati() == MonsterStatus.EMPTY_5)) {
                    mplew.writeInt(0);
                }
            }
        }
    }

    public static byte[] controlMonster(MapleMonster life, boolean newSpawn, boolean aggro) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPAWN_MONSTER_CONTROL.getValue());
        mplew.write(aggro ? 2 : 1);
        mplew.writeInt(life.getObjectId());
        mplew.write(1);
        mplew.writeInt(life.getId());

        mplew.writeZeroBytes(25);
        mplew.write(224);
        mplew.writeShort(19);
        mplew.writeInt(0);
        mplew.write(136);
        for (int i = 1; i <= 4; i++) {
            mplew.writeLong(0L);
            mplew.writeShort(Integer.valueOf((int) System.currentTimeMillis()).shortValue());
        }
        mplew.writeInt(0);
        mplew.write(0);//104
        mplew.writePos(life.getTruePosition());
        mplew.write(life.getStance());
        mplew.writeShort(0);
        mplew.writeShort(life.getFh());
        mplew.write(newSpawn ? -2 : life.isFake() ? -4 : -1);
        mplew.write(life.getCarnivalTeam());
        mplew.writeInt(0);
        mplew.writeInt(0);//104
        mplew.write(-1);

        return mplew.getPacket();
    }

    public static byte[] stopControllingMonster(int oid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPAWN_MONSTER_CONTROL.getValue());
        mplew.write(0);
        mplew.writeInt(oid);

        return mplew.getPacket();
    }

    public static byte[] makeMonsterReal(MapleMonster life) {
        return spawnMonster(life, -1, 0);
    }

    public static byte[] makeMonsterFake(MapleMonster life) {
        return spawnMonster(life, -4, 0);
    }

    public static byte[] makeMonsterEffect(MapleMonster life, int effect) {
        return spawnMonster(life, effect, 0);
    }

    public static byte[] moveMonsterResponse(int objectid, short moveid, int currentMp, boolean useSkills, int skillId, int skillLevel) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MOVE_MONSTER_RESPONSE.getValue());
        mplew.writeInt(objectid);
        mplew.writeShort(moveid);
        mplew.write(useSkills ? 1 : 0);
        mplew.writeShort(currentMp);
        mplew.write(skillId);
        mplew.write(skillLevel);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    private static void getLongMask_NoRef(MaplePacketLittleEndianWriter mplew, Collection<MonsterStatusEffect> ss, boolean ignore_imm) {
        int[] mask = new int[8];
        for (MonsterStatusEffect statup : ss) {
            if ((statup != null) && (statup.getStati() != MonsterStatus.反射物攻) && (statup.getStati() != MonsterStatus.反射魔攻) && ((!ignore_imm) || ((statup.getStati() != MonsterStatus.免疫物攻) && (statup.getStati() != MonsterStatus.免疫魔攻) && (statup.getStati() != MonsterStatus.免疫伤害)))) {
                mask[(statup.getStati().getPosition() - 1)] |= statup.getStati().getValue();
            }
        }
        for (int i = mask.length; i >= 1; i--) {
            mplew.writeInt(mask[(i - 1)]);
        }
    }

    public static byte[] applyMonsterStatus(int oid, MonsterStatus mse, int x, MobSkill skil) {
        if (showPacket) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.APPLY_MONSTER_STATUS.getValue());
        mplew.writeInt(oid);
        PacketHelper.writeSingleMask(mplew, mse);

        mplew.writeInt(x);
        mplew.writeInt(skil.getSkillId());
        mplew.writeShort(skil.getSkillLevel());
        mplew.writeShort(mse.isEmpty() ? 1 : 0);
        mplew.writeShort(0);
        mplew.write(1);
        mplew.write(1);

        return mplew.getPacket();
    }

    public static byte[] applyMonsterStatus(MapleMonster mons, MonsterStatusEffect ms) {
        if (showPacket) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.APPLY_MONSTER_STATUS.getValue());
        mplew.writeInt(mons.getObjectId());

        PacketHelper.writeSingleMask(mplew, ms.getStati());

        mplew.writeInt(ms.getX().intValue());
        if (ms.isMonsterSkill()) {
            mplew.writeShort(ms.getMobSkill().getSkillId());
            mplew.writeShort(ms.getMobSkill().getSkillLevel());
        } else if (ms.getSkill() > 0) {
            mplew.writeInt(ms.getSkill());
        }
        mplew.writeShort(ms.getStati().isEmpty() ? 1 : 0);
        mplew.writeShort(0);
        mplew.write(1);
        mplew.write(1);

        return mplew.getPacket();
    }

    public static byte[] applyMonsterStatus(MapleMonster mons, List<MonsterStatusEffect> mse) {
        if (showPacket) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        if ((mse.size() <= 0) || (mse.get(0) == null)) {
            return MaplePacketCreator.enableActions();
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.APPLY_MONSTER_STATUS.getValue());
        mplew.writeInt(mons.getObjectId());
        MonsterStatusEffect ms = (MonsterStatusEffect) mse.get(0);
        if (ms.getStati() == MonsterStatus.中毒) {
            PacketHelper.writeSingleMask(mplew, MonsterStatus.空白BUFF);
            mplew.write(mse.size());
            for (MonsterStatusEffect m : mse) {
                mplew.writeInt(m.getFromID());
                if (m.isMonsterSkill()) {
                    mplew.writeShort(m.getMobSkill().getSkillId());
                    mplew.writeShort(m.getMobSkill().getSkillLevel());
                } else if (m.getSkill() > 0) {
                    mplew.writeInt(m.getSkill());
                }
                mplew.writeInt(m.getX().intValue());
                mplew.writeLong(1000L);
                mplew.writeLong(5L);
            }
            mplew.writeShort(300);
            mplew.write(1);
        } else {
            PacketHelper.writeSingleMask(mplew, ms.getStati());
            mplew.writeInt(ms.getX().intValue());
            if (ms.isMonsterSkill()) {
                mplew.writeShort(ms.getMobSkill().getSkillId());
                mplew.writeShort(ms.getMobSkill().getSkillLevel());
            } else if (ms.getSkill() > 0) {
                mplew.writeInt(ms.getSkill());
            }
            mplew.writeShort(0);
            mplew.writeShort(0);
            mplew.write(1);
            mplew.write(1);
        }

        return mplew.getPacket();
    }

    public static byte[] applyMonsterStatus(int oid, Map<MonsterStatus, Integer> stati, List<Integer> reflection, MobSkill skil) {
        if (showPacket) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.APPLY_MONSTER_STATUS.getValue());
        mplew.writeInt(oid);
        PacketHelper.writeMask(mplew, stati.keySet());

        for (Map.Entry mse : stati.entrySet()) {
            mplew.writeInt(((Integer) mse.getValue()).intValue());
            mplew.writeShort(skil.getSkillId());
            mplew.writeShort(skil.getSkillLevel());
            mplew.writeShort(0);
        }
        for (Integer ref : reflection) {
            mplew.writeInt(ref.intValue());
        }
        mplew.writeLong(0L);
        mplew.writeShort(0);
        int size = stati.size();
        if (reflection.size() > 0) {
            size /= 2;
        }
        mplew.write(size);
        mplew.write(1);

        return mplew.getPacket();
    }

    public static byte[] cancelMonsterStatus(int oid, MonsterStatus stat) {
        if (showPacket) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CANCEL_MONSTER_STATUS.getValue());
        mplew.writeInt(oid);
        PacketHelper.writeSingleMask(mplew, stat);
        mplew.write(1);
        mplew.write(2);

        return mplew.getPacket();
    }

    public static byte[] cancelPoison(int oid, MonsterStatusEffect m) {
        if (showPacket) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CANCEL_MONSTER_STATUS.getValue());
        mplew.writeInt(oid);
        PacketHelper.writeSingleMask(mplew, MonsterStatus.空白BUFF);
        mplew.writeInt(0);
        mplew.writeInt(1);
        mplew.writeInt(m.getFromID());
        if (m.isMonsterSkill()) {
            mplew.writeShort(m.getMobSkill().getSkillId());
            mplew.writeShort(m.getMobSkill().getSkillLevel());
        } else if (m.getSkill() > 0) {
            mplew.writeInt(m.getSkill());
        }
        mplew.write(3);

        return mplew.getPacket();
    }

    public static byte[] talkMonster(int oid, int itemId, String msg) {
        if (showPacket) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.TALK_MONSTER.getValue());
        mplew.writeInt(oid);
        mplew.writeInt(500);
        mplew.writeInt(itemId);
        mplew.write(itemId <= 0 ? 0 : 1);
        mplew.write((msg == null) || (msg.length() <= 0) ? 0 : 1);
        if ((msg != null) && (msg.length() > 0)) {
            mplew.writeMapleAsciiString(msg);
        }
        mplew.writeInt(1);

        return mplew.getPacket();
    }

    public static byte[] removeTalkMonster(int oid) {
        if (showPacket) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.REMOVE_TALK_MONSTER.getValue());
        mplew.writeInt(oid);
        return mplew.getPacket();
    }
}