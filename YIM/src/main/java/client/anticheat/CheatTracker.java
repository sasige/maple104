package client.anticheat;

import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.MapleClient;
import client.SkillFactory;
import constants.GameConstants;
import handling.world.World;
import handling.world.World.Broadcast;
import java.awt.Point;
import java.lang.ref.WeakReference;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.log4j.Logger;

import server.AutobanManager;
import server.Timer;
import server.Timer.CheatTimer;
import tools.FileoutputUtil;
import tools.MaplePacketCreator;
import tools.StringUtil;

public class CheatTracker {

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private Lock rL = this.lock.readLock();
    private Lock wL = this.lock.writeLock();
    private Map<CheatingOffense, CheatingOffenseEntry> offenses = new EnumMap(CheatingOffense.class);
    private WeakReference<MapleCharacter> chr;
    private long lastAttackTime = 0L;
    private int lastAttackTickCount = 0;
    private byte Attack_tickResetCount = 0;
    private long Server_ClientAtkTickDiff = 0L;
    private long lastDamage = 0L;
    private long takingDamageSince;
    private int numSequentialDamage = 0;
    private long lastDamageTakenTime = 0L;
    private byte numZeroDamageTaken = 0;
    private int numSequentialSummonAttack = 0;
    private long summonSummonTime = 0L;
    private int numSameDamage = 0;
    private Point lastMonsterMove;
    private int monsterMoveCount;
    private int attacksWithoutHit = 0;
    private byte dropsPerSecond = 0;
    private long lastDropTime = 0L;
    private byte msgsPerSecond = 0;
    private long lastMsgTime = 0L;
    private ScheduledFuture<?> invalidationTask;
    private int gm_message = 0;
    private int lastTickCount = 0;
    private int tickSame = 0;
    private long lastSmegaTime = 0L;
    private long lastBBSTime = 0L;
    private long lastASmegaTime = 0L;
    private long lastMZDTime = 0L;
    private long lastCraftTime = 0L;
    private long lastSaveTime = 0L;
    private long lastLieDetectorTime = 0L;
    private boolean saveToDB = false;
    private int numSequentialFamiliarAttack = 0;
    private long familiarSummonTime = 0L;
    private static final Logger log = Logger.getLogger(CheatTracker.class);

    public CheatTracker(MapleCharacter chr) {
        start(chr);
    }

    public void checkAttack(int skillId, int tickcount) {
        int AtkDelay = GameConstants.getAttackDelay(skillId, skillId == 0 ? null : SkillFactory.getSkill(skillId));
        if (tickcount - this.lastAttackTickCount < AtkDelay) {
            registerOffense(CheatingOffense.FASTATTACK, "攻击速度异常.");
        }
        this.lastAttackTime = System.currentTimeMillis();
        if ((this.chr.get() != null) && (this.lastAttackTime - ((MapleCharacter) this.chr.get()).getChangeTime() > 600000L)) {
            ((MapleCharacter) this.chr.get()).setChangeTime();
        }
        long STime_TC = this.lastAttackTime - tickcount;
        if (this.Server_ClientAtkTickDiff - STime_TC > 1000L) {
            registerOffense(CheatingOffense.FASTATTACK2, "攻击速度异常.");
        }

        this.Attack_tickResetCount = (byte) (this.Attack_tickResetCount + 1);
        if (this.Attack_tickResetCount >= (AtkDelay <= 200 ? 1 : 4)) {
            this.Attack_tickResetCount = 0;
            this.Server_ClientAtkTickDiff = STime_TC;
        }
        updateTick(tickcount);
        this.lastAttackTickCount = tickcount;
    }

    public void checkPVPAttack(int skillId) {
        int AtkDelay = GameConstants.getAttackDelay(skillId, skillId == 0 ? null : SkillFactory.getSkill(skillId));
        long STime_TC = System.currentTimeMillis() - this.lastAttackTime;
        if (STime_TC < AtkDelay) {
            registerOffense(CheatingOffense.FASTATTACK, "攻击速度异常.");
        }
        this.lastAttackTime = System.currentTimeMillis();
    }

    public long getLastAttack() {
        return this.lastAttackTime;
    }

    public void checkTakeDamage(int damage) {
        this.numSequentialDamage += 1;
        this.lastDamageTakenTime = System.currentTimeMillis();

        if (this.lastDamageTakenTime - this.takingDamageSince / 500L < this.numSequentialDamage) {
            registerOffense(CheatingOffense.FAST_TAKE_DAMAGE, "掉血次数异常.");
        }
        if (this.lastDamageTakenTime - this.takingDamageSince > 4500L) {
            this.takingDamageSince = this.lastDamageTakenTime;
            this.numSequentialDamage = 0;
        }

        if (damage == 0) {
            this.numZeroDamageTaken = (byte) (this.numZeroDamageTaken + 1);
            if (this.numZeroDamageTaken >= 50) {
                this.numZeroDamageTaken = 0;
                registerOffense(CheatingOffense.HIGH_AVOID, "回避率过高.");
            }
        } else if (damage != -1) {
            this.numZeroDamageTaken = 0;
        }
    }

    public void checkSameDamage(int dmg, double expected) {
        if ((dmg > 2000) && (this.lastDamage == dmg) && (this.chr.get() != null) && ((((MapleCharacter) this.chr.get()).getLevel() < 180) || (dmg > expected * 2.0D))) {
            this.numSameDamage += 1;
            if (this.numSameDamage > 5) {
                registerOffense(CheatingOffense.SAME_DAMAGE, new StringBuilder().append(this.numSameDamage).append(" times, 攻击伤害 ").append(dmg).append(", 预期伤害 ").append(expected).append(" [等级: ").append(((MapleCharacter) this.chr.get()).getLevel()).append(", 职业: ").append(((MapleCharacter) this.chr.get()).getJob()).append("]").toString());
                this.numSameDamage = 0;
            }
        } else {
            this.lastDamage = dmg;
            this.numSameDamage = 0;
        }
    }

    public void checkHighDamage(int eachd, double maxDamagePerHit, int mobId, int skillId) {
        if ((eachd > maxDamagePerHit) && (maxDamagePerHit > 2000.0D) && (this.chr.get() != null)) {
            registerOffense(CheatingOffense.HIGH_DAMAGE, new StringBuilder().append("[伤害: ").append(eachd).append(", 预计伤害: ").append(maxDamagePerHit).append(", 怪物ID: ").append(mobId).append("] [职业: ").append(((MapleCharacter) this.chr.get()).getJob()).append(", 等级: ").append(((MapleCharacter) this.chr.get()).getLevel()).append(", 技能: ").append(skillId).append("]").toString());
            if (eachd > maxDamagePerHit * 2.0D) {
                registerOffense(CheatingOffense.HIGH_DAMAGE_2, new StringBuilder().append("[伤害: ").append(eachd).append(", 预计伤害: ").append(maxDamagePerHit).append(", 怪物ID: ").append(mobId).append("] [职业: ").append(((MapleCharacter) this.chr.get()).getJob()).append(", 等级: ").append(((MapleCharacter) this.chr.get()).getLevel()).append(", 技能: ").append(skillId).append("]").toString());
            }
        }
    }

    public void checkMoveMonster(Point pos) {
        if (pos == this.lastMonsterMove) {
            this.monsterMoveCount += 1;
            if (this.monsterMoveCount > 10) {
                registerOffense(CheatingOffense.MOVE_MONSTERS, new StringBuilder().append("吸怪 坐标: ").append(pos.x).append(", ").append(pos.y).toString());
                this.monsterMoveCount = 0;
            }
        } else {
            this.lastMonsterMove = pos;
            this.monsterMoveCount = 1;
        }
    }

    public void resetSummonAttack() {
        this.summonSummonTime = System.currentTimeMillis();
        this.numSequentialSummonAttack = 0;
    }

    public boolean checkSummonAttack() {
        this.numSequentialSummonAttack += 1;
        if ((System.currentTimeMillis() - this.summonSummonTime) / 1001L < this.numSequentialSummonAttack) {
            registerOffense(CheatingOffense.FAST_SUMMON_ATTACK, "召唤兽攻击速度过快.");
            return false;
        }
        return true;
    }

    public void resetFamiliarAttack() {
        this.familiarSummonTime = System.currentTimeMillis();
        this.numSequentialFamiliarAttack = 0;
    }

    public boolean checkFamiliarAttack(MapleCharacter chr) {
        this.numSequentialFamiliarAttack += 1;

        if ((System.currentTimeMillis() - this.familiarSummonTime) / 601L < this.numSequentialFamiliarAttack) {
            registerOffense(CheatingOffense.FAST_SUMMON_ATTACK, "召唤兽攻击速度异常.");
            return false;
        }
        return true;
    }

    public void checkDrop() {
        checkDrop(false);
    }

    public void checkDrop(boolean dc) {
        if (System.currentTimeMillis() - this.lastDropTime < 1000L) {
            this.dropsPerSecond = (byte) (this.dropsPerSecond + 1);
            if (this.dropsPerSecond >= (dc ? 32 : 16)) {
                if ((this.chr.get() != null) && (!((MapleCharacter) this.chr.get()).isGM())) {
                    if (dc) {
                        ((MapleCharacter) this.chr.get()).getClient().getSession().close();
                        log.info(new StringBuilder().append("[作弊] ").append(((MapleCharacter) this.chr.get()).getName()).append(" (等级 ").append(((MapleCharacter) this.chr.get()).getLevel()).append(") checkDrop 次数: ").append(this.dropsPerSecond).append(" 服务器断开他的连接。").toString());
                    } else {
                        ((MapleCharacter) this.chr.get()).getClient().setMonitored(true);
                    }
                }
            }
        } else {
            this.dropsPerSecond = 0;
        }
        this.lastDropTime = System.currentTimeMillis();
    }

    public void checkMsg() {
        if (System.currentTimeMillis() - this.lastMsgTime < 1000L) {
            this.msgsPerSecond = (byte) (this.msgsPerSecond + 1);
            if ((this.msgsPerSecond > 10) && (this.chr.get() != null) && (!((MapleCharacter) this.chr.get()).isGM())) {
                ((MapleCharacter) this.chr.get()).getClient().getSession().close();
                log.info(new StringBuilder().append("[作弊] ").append(((MapleCharacter) this.chr.get()).getName()).append(" (等级 ").append(((MapleCharacter) this.chr.get()).getLevel()).append(") checkMsg 次数: ").append(this.msgsPerSecond).append(" 服务器断开他的连接。").toString());
            }
        } else {
            this.msgsPerSecond = 0;
        }
        this.lastMsgTime = System.currentTimeMillis();
    }

    public int getAttacksWithoutHit() {
        return this.attacksWithoutHit;
    }

    public void setAttacksWithoutHit(boolean increase) {
        if (increase) {
            this.attacksWithoutHit += 1;
        } else {
            this.attacksWithoutHit = 0;
        }
    }

    public void registerOffense(CheatingOffense offense) {
        registerOffense(offense, null);
    }

    public void registerOffense(CheatingOffense offense, String param) {
        MapleCharacter chrhardref = (MapleCharacter) this.chr.get();
        if ((chrhardref == null) || (!offense.isEnabled()) || (chrhardref.isClone()) || (chrhardref.isGM())) {
            return;
        }
        CheatingOffenseEntry entry = null;
        this.rL.lock();
        try {
            entry = (CheatingOffenseEntry) this.offenses.get(offense);
        } finally {
            this.rL.unlock();
        }
        if ((entry != null) && (entry.isExpired())) {
            expireEntry(entry);
            entry = null;
            this.gm_message = 0;
        }
        if (entry == null) {
            entry = new CheatingOffenseEntry(offense, chrhardref.getId());
        }
        if (param != null) {
            entry.setParam(param);
        }
        entry.incrementCount();
        if (offense.shouldAutoban(entry.getCount())) {
            byte type = offense.getBanType();
            if (type == 1) {
                AutobanManager.getInstance().autoban(chrhardref.getClient(), StringUtil.makeEnumHumanReadable(offense.name()));
            } else if (type == 2) {
                chrhardref.getClient().getSession().close();
                log.info(new StringBuilder().append("[作弊] ").append(chrhardref.getName()).append(" (等级 ").append(chrhardref.getLevel()).append(") registerOffense  服务器断开他的连接。").toString());
            }
            this.gm_message = 0;
            return;
        }
        this.wL.lock();
        try {
            this.offenses.put(offense, entry);
        } finally {
            this.wL.unlock();
        }
        switch (offense) {
            case HIGH_DAMAGE_MAGIC_2:
            case HIGH_DAMAGE_2:
            case ATTACK_FARAWAY_MONSTER:
            case ATTACK_FARAWAY_MONSTER_SUMMON:
            case SAME_DAMAGE:
                this.gm_message += 1;
                if (this.gm_message % 100 == 0) {
                    log.info(new StringBuilder().append("[作弊] ").append(MapleCharacterUtil.makeMapleReadable(chrhardref.getName())).append(" (等级 ").append(chrhardref.getLevel()).append(") 使用非法程序! ").append(StringUtil.makeEnumHumanReadable(offense.name())).append(param == null ? "" : new StringBuilder().append(" - ").append(param).toString()).toString());
                    World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append("[GM Message] ").append(MapleCharacterUtil.makeMapleReadable(chrhardref.getName())).append(" (等级 ").append(chrhardref.getLevel()).append(") 使用非法程序! ").append(StringUtil.makeEnumHumanReadable(offense.name())).append(param == null ? "" : new StringBuilder().append(" - ").append(param).toString()).toString()));
                }
                if (this.gm_message < 20) {
                    break;
                }
                if (chrhardref.getLevel() >= (offense == CheatingOffense.SAME_DAMAGE ? 180 : 190)) {
                    break;
                }
                Timestamp chrCreated = chrhardref.getChrCreated();
                long time = System.currentTimeMillis();
                if (chrCreated != null) {
                    time = chrCreated.getTime();
                }
                if (time + 1296000000L >= System.currentTimeMillis()) {
                    AutobanManager.getInstance().autoban(chrhardref.getClient(), new StringBuilder().append(StringUtil.makeEnumHumanReadable(offense.name())).append(" over 500 times ").append(param == null ? "" : new StringBuilder().append(" - ").append(param).toString()).toString());
                } else {
                    this.gm_message = 0;
                    World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append("[GM Message] ").append(MapleCharacterUtil.makeMapleReadable(chrhardref.getName())).append(" (等级 ").append(chrhardref.getLevel()).append(") 使用非法程序! ").append(StringUtil.makeEnumHumanReadable(offense.name())).append(param == null ? "" : new StringBuilder().append(" - ").append(param).toString()).toString()));
                    FileoutputUtil.log("log\\Hacker.log", new StringBuilder().append("[GM Message] ").append(MapleCharacterUtil.makeMapleReadable(chrhardref.getName())).append(" (等级 ").append(chrhardref.getLevel()).append(") 使用非法程序! ").append(StringUtil.makeEnumHumanReadable(offense.name())).append(param == null ? "" : new StringBuilder().append(" - ").append(param).toString()).toString());
                }

        }

        CheatingOffensePersister.getInstance().persistEntry(entry);
    }

    public void updateTick(int newTick) {
        if (newTick <= this.lastTickCount) {
            if ((this.tickSame >= 30) && (this.chr.get() != null) && (!((MapleCharacter) this.chr.get()).isGM())) {
                ((MapleCharacter) this.chr.get()).getClient().getSession().close();
                log.info(new StringBuilder().append("[作弊] ").append(((MapleCharacter) this.chr.get()).getName()).append(" (等级 ").append(((MapleCharacter) this.chr.get()).getLevel()).append(") updateTick 次数: ").append(this.tickSame).append(" 服务器断开他的连接。").toString());
            } else {
                this.tickSame += 1;
            }
        } else {
            this.tickSame = 0;
        }

        this.lastTickCount = newTick;
    }

    public boolean canSmega() {
        if ((this.lastSmegaTime > System.currentTimeMillis()) && (this.chr.get() != null) && (!((MapleCharacter) this.chr.get()).isGM())) {
            return false;
        }
        this.lastSmegaTime = System.currentTimeMillis();
        return true;
    }

    public boolean canAvatarSmega() {
        if ((this.lastASmegaTime > System.currentTimeMillis()) && (this.chr.get() != null) && (!((MapleCharacter) this.chr.get()).isGM())) {
            return false;
        }
        this.lastASmegaTime = System.currentTimeMillis();
        return true;
    }

    public boolean canBBS() {
        if ((this.lastBBSTime + 60000L > System.currentTimeMillis()) && (this.chr.get() != null) && (!((MapleCharacter) this.chr.get()).isGM())) {
            return false;
        }
        this.lastBBSTime = System.currentTimeMillis();
        return true;
    }

    public boolean canMZD() {
        if ((this.lastMZDTime > System.currentTimeMillis()) && (this.chr.get() != null) && (!((MapleCharacter) this.chr.get()).isGM())) {
            return false;
        }
        this.lastMZDTime = System.currentTimeMillis();
        return true;
    }

    public boolean canCraftMake() {
        if ((this.lastCraftTime + 3000L > System.currentTimeMillis()) && (this.chr.get() != null) && (!((MapleCharacter) this.chr.get()).isGM())) {
            return false;
        }
        this.lastCraftTime = System.currentTimeMillis();
        return true;
    }

    public boolean canSaveDB() {
        if (!this.saveToDB) {
            this.saveToDB = true;
            return false;
        }
        if ((this.lastSaveTime + 60000L > System.currentTimeMillis()) && (this.chr.get() != null)) {
            return false;
        }
        this.lastSaveTime = System.currentTimeMillis();
        return true;
    }

    public boolean canLieDetector() {
        if ((this.lastLieDetectorTime + 300000L > System.currentTimeMillis()) && (this.chr.get() != null)) {
            return false;
        }
        this.lastLieDetectorTime = System.currentTimeMillis();
        return true;
    }

    public void expireEntry(CheatingOffenseEntry coe) {
        this.wL.lock();
        try {
            this.offenses.remove(coe.getOffense());
        } finally {
            this.wL.unlock();
        }
    }

    public int getPoints() {
        int ret = 0;

        this.rL.lock();
        CheatingOffenseEntry[] offenses_copy;
        try {
            offenses_copy = (CheatingOffenseEntry[]) this.offenses.values().toArray(new CheatingOffenseEntry[this.offenses.size()]);
        } finally {
            this.rL.unlock();
        }
        for (CheatingOffenseEntry entry : offenses_copy) {
            if (entry.isExpired()) {
                expireEntry(entry);
            } else {
                ret += entry.getPoints();
            }
        }
        return ret;
    }

    public Map<CheatingOffense, CheatingOffenseEntry> getOffenses() {
        return Collections.unmodifiableMap(this.offenses);
    }

    public String getSummary() {
        final StringBuilder ret = new StringBuilder();
        final List<CheatingOffenseEntry> offenseList = new ArrayList<CheatingOffenseEntry>();
        this.rL.lock();
        try {
            for (CheatingOffenseEntry entry : this.offenses.values()) {
                if (!entry.isExpired()) {
                    offenseList.add(entry);
                }
            }
        } finally {
            this.rL.unlock();
        }
        Collections.sort(offenseList, new Comparator<CheatingOffenseEntry>() {

            @Override
            public int compare(CheatingOffenseEntry o1, CheatingOffenseEntry o2) {
                int thisVal = o1.getPoints();
                int anotherVal = o2.getPoints();
                return thisVal == anotherVal ? 0 : thisVal < anotherVal ? 1 : -1;
            }
        });
        int to = Math.min(offenseList.size(), 4);
        for (int x = 0; x < to; x++) {
            ret.append(StringUtil.makeEnumHumanReadable(((CheatingOffenseEntry) offenseList.get(x)).getOffense().name()));
            ret.append(": ");
            ret.append(((CheatingOffenseEntry) offenseList.get(x)).getCount());
            if (x != to - 1) {
                ret.append(" ");
            }
        }
        return ret.toString();
    }

    public void dispose() {
        if (this.invalidationTask != null) {
            this.invalidationTask.cancel(false);
        }
        this.invalidationTask = null;
        this.chr = new WeakReference(null);
    }

    public final void start(MapleCharacter chr) {
        this.chr = new WeakReference(chr);
        invalidationTask = CheatTimer.getInstance().register(new InvalidationTask(), 60000);
        this.takingDamageSince = System.currentTimeMillis();
    }

    private class InvalidationTask implements Runnable {

        private InvalidationTask() {
        }

        @Override
        public void run() {
            CheatTracker.this.rL.lock();
            CheatingOffenseEntry[] offenses_copy;
            try {
                offenses_copy = (CheatingOffenseEntry[]) CheatTracker.this.offenses.values().toArray(new CheatingOffenseEntry[CheatTracker.this.offenses.size()]);
            } finally {
                CheatTracker.this.rL.unlock();
            }
            for (CheatingOffenseEntry offense : offenses_copy) {
                if (offense.isExpired()) {
                    CheatTracker.this.expireEntry(offense);
                }
            }
            if (CheatTracker.this.chr.get() == null) {
                CheatTracker.this.dispose();
            }
        }
    }
}
