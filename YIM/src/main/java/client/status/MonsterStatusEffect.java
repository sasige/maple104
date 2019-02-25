package client.status;

import client.MapleCharacter;
import java.lang.ref.WeakReference;
import server.life.MapleMonster;
import server.life.MobSkill;

public class MonsterStatusEffect {

    private MonsterStatus stati;
    private int skill;
    private MobSkill mobskill;
    private boolean monsterSkill;
    private WeakReference<MapleCharacter> weakChr = null;
    private Integer x;
    private int poisonSchedule = 0;
    private boolean reflect = false;
    private long cancelTime = 0L;

    public MonsterStatusEffect(MonsterStatus stat, Integer x, int skillId, MobSkill mobskill, boolean monsterSkill) {
        this.stati = stat;
        this.skill = skillId;
        this.monsterSkill = monsterSkill;
        this.mobskill = mobskill;
        this.x = x;
    }

    public MonsterStatusEffect(MonsterStatus stat, Integer x, int skillId, MobSkill mobskill, boolean monsterSkill, boolean reflect) {
        this.stati = stat;
        this.skill = skillId;
        this.monsterSkill = monsterSkill;
        this.mobskill = mobskill;
        this.x = x;
        this.reflect = reflect;
    }

    public MonsterStatus getStati() {
        return this.stati;
    }

    public Integer getX() {
        return this.x;
    }

    public void setValue(MonsterStatus status, Integer newVal) {
        this.stati = status;
        this.x = newVal;
    }

    public int getSkill() {
        return this.skill;
    }

    public MobSkill getMobSkill() {
        return this.mobskill;
    }

    public boolean isMonsterSkill() {
        return this.monsterSkill;
    }

    public void setCancelTask(long cancelTask) {
        this.cancelTime = (System.currentTimeMillis() + cancelTask);
    }

    public long getCancelTask() {
        return this.cancelTime;
    }

    public void setPoisonSchedule(int poisonSchedule, MapleCharacter chrr) {
        this.poisonSchedule = poisonSchedule;
        this.weakChr = new WeakReference(chrr);
    }

    public int getPoisonSchedule() {
        return this.poisonSchedule;
    }

    public boolean shouldCancel(long now) {
        return (this.cancelTime > 0L) && (this.cancelTime <= now);
    }

    public void cancelTask() {
        this.cancelTime = 0L;
    }

    public boolean isReflect() {
        return this.reflect;
    }

    public int getFromID() {
        return (this.weakChr == null) || (this.weakChr.get() == null) ? 0 : ((MapleCharacter) this.weakChr.get()).getId();
    }

    public void cancelPoisonSchedule(MapleMonster mm) {
        mm.doPoison(this, this.weakChr);
        this.poisonSchedule = 0;
        this.weakChr = null;
    }

    public static int genericSkill(MonsterStatus stat) {
        switch (stat) {
            case Ñ£ÔÎ:
                return 90001001;
            case ËÙ¶È:
                return 90001002;
            case ÖÐ¶¾:
                return 90001003;
            case ÐÄÁé¿ØÖÆ:
                return 90001004;
            case ·âÓ¡:
                return 90001005;
            case ½á±ù:
                return 90001006;
            case Ä§»÷ÎÞÐ§:
                return 1111007;
            case ÌôÐÆ:
                return 4121003;
            case ¹í¿Ì·û:
                return 22161002;
            case Ó°Íø:
                return 4111003;
            case ¿Ö»Å:
                return 5211004;
            case Î×¶¾:
                return 2311005;
            case ÁÒÑæÅçÉä:
                return 4121004;
        }
        return 0;
    }
}
