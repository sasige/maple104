package client;

import constants.GameConstants;
import java.util.ArrayList;
import java.util.List;
import provider.MapleData;
import provider.MapleDataTool;
import server.MapleStatEffect;
import server.Randomizer;
import server.life.Element;
import tools.Pair;

public class Skill {

    private String name = "";
    private final List<MapleStatEffect> effects = new ArrayList();
    private List<MapleStatEffect> pvpEffects = null;
    private List<Integer> animation = null;
    private final List<Pair<Integer, Byte>> requiredSkill = new ArrayList();
    private Element element = Element.NEUTRAL;
    private int id;
    private int animationTime = 0;
    private int masterLevel = 0;
    private int maxLevel = 0;
    private int delay = 0;
    private int trueMax = 0;
    private int eventTamingMob = 0;
    private int skillType = 0;
    private boolean invisible = false;
    private boolean chargeskill = false;
    private boolean timeLimited = false;
    private boolean combatOrders = false;
    private boolean pvpDisabled = false;
    private boolean magic = false;
    private boolean casterMove = false;
    private boolean pushTarget = false;
    private boolean pullTarget = false;

    public Skill(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public static Skill loadFromData(int id, MapleData data, MapleData delayData) {
        Skill ret = new Skill(id);

        boolean isBuff = false;
        int skillType = MapleDataTool.getInt("skillType", data, -1);
        String elem = MapleDataTool.getString("elemAttr", data, null);
        if (elem != null) {
            ret.element = Element.getFromChar(elem.charAt(0));
        }
        ret.skillType = skillType;
        ret.invisible = (MapleDataTool.getInt("invisible", data, 0) > 0);
        ret.timeLimited = (MapleDataTool.getInt("timeLimited", data, 0) > 0);
        ret.combatOrders = (MapleDataTool.getInt("combatOrders", data, 0) > 0);
        ret.masterLevel = MapleDataTool.getInt("masterLevel", data, 0);
        ret.eventTamingMob = MapleDataTool.getInt("eventTamingMob", data, 0);
        MapleData inf = data.getChildByPath("info");
        if (inf != null) {
            ret.pvpDisabled = (MapleDataTool.getInt("pvp", inf, 1) <= 0);
            ret.magic = (MapleDataTool.getInt("magicDamage", inf, 0) > 0);
            ret.casterMove = (MapleDataTool.getInt("casterMove", inf, 0) > 0);
            ret.pushTarget = (MapleDataTool.getInt("pushTarget", inf, 0) > 0);
            ret.pullTarget = (MapleDataTool.getInt("pullTarget", inf, 0) > 0);
        }
        MapleData effect = data.getChildByPath("effect");
        if (skillType == 2) {
            isBuff = true;
        } else if (skillType == 3) {
            ret.animation = new ArrayList();
            ret.animation.add(Integer.valueOf(0));
            isBuff = effect != null;
        } else {
            MapleData action_ = data.getChildByPath("action");
            MapleData hit = data.getChildByPath("hit");
            MapleData ball = data.getChildByPath("ball");

            boolean action = false;
            if ((action_ == null)
                    && (data.getChildByPath("prepare/action") != null)) {
                action_ = data.getChildByPath("prepare/action");
                action = true;
            }

            isBuff = (effect != null) && (hit == null) && (ball == null);
            String d;
            if (action_ != null) {
                d = null;
                if (action) {
                    d = MapleDataTool.getString(action_, null);
                } else {
                    d = MapleDataTool.getString("0", action_, null);
                }
                if (d != null) {
                    isBuff |= d.equals("alert2");
                    MapleData dd = delayData.getChildByPath(d);
                    if (dd != null) {
                        for (MapleData del : dd) {
                            ret.delay += Math.abs(MapleDataTool.getInt("delay", del, 0));
                        }
                        if (ret.delay > 30) {
                            ret.delay = (int) Math.round(ret.delay * 11.0D / 16.0D);
                            ret.delay -= ret.delay % 30;
                        }
                    }
                    if (SkillFactory.getDelay(d) != null) {
                        ret.animation = new ArrayList();
                        ret.animation.add(SkillFactory.getDelay(d));
                        if (!action) {
                            for (MapleData ddc : action_) {
                                if (!MapleDataTool.getString(ddc, d).equals(d)) {
                                    String c = MapleDataTool.getString(ddc);
                                    if (SkillFactory.getDelay(c) != null) {
                                        ret.animation.add(SkillFactory.getDelay(c));
                                    }
                                }
                            }
                        }
                    }
                }
            }
            switch (id) {
                case 1076:
                case 2111002:
                case 2111003:
                case 2121001:
                case 2221001:
                case 2301002:
                case 2321001:
                case 12111005:
                case 14111006:
                case 22161003:
                case 32121006:
                    isBuff = false;
                    break;
                case 93:
                case 1004:
                case 1026:
                case 1111002:
                case 1111007:
                case 1211009:
                case 1220013:
                case 1311007:
                case 1320009:
                case 2120010:
                case 2121009:
                case 2220010:
                case 2221009:
                case 2320011:
                case 2321010:
                case 3120006:
                case 3220005:
                case 4211003:
                case 4221013:
                case 4321000:
                case 4341002:
                case 5091005:
                case 5311005:
                case 5320007:
                case 5321003:
                case 5321004:
                case 5711001:
                case 5711011:
                case 5720005:
                case 5720012:
                case 5721003:
                case 5810001:
                case 5811005:
                case 5811007:
                case 5820011:
                case 5821003:
                case 5821009:
                case 5911001:
                case 5911002:
                case 5911006:
                case 5911007:
                case 5920002:
                case 5920011:
                case 5920012:
                case 9001004:
                case 9101004:
                case 10000093:
                case 10001004:
                case 10001026:
                case 13111005:
                case 15001003:
                case 15100004:
                case 15111005:
                case 15111006:
                case 15111011:
                case 20000093:
                case 20001004:
                case 20001026:
                case 20010093:
                case 20011004:
                case 20011026:
                case 20020093:
                case 20021026:
                case 21000000:
                case 21101003:
                case 22131001:
                case 22131002:
                case 22141002:
                case 22151002:
                case 22151003:
                case 22161002:
                case 22161004:
                case 22171000:
                case 22171004:
                case 22181000:
                case 22181003:
                case 22181004:
                case 24111002:
                case 30000093:
                case 30001026:
                case 30010093:
                case 30011026:
                case 31121005:
                case 32001003:
                case 32101003:
                case 32110000:
                case 32110007:
                case 32110008:
                case 32110009:
                case 32111005:
                case 32111006:
                case 32111012:
                case 32120000:
                case 32120001:
                case 32121003:
                case 33101005:
                case 33111003:
                case 35001001:
                case 35001002:
                case 35101005:
                case 35101007:
                case 35101009:
                case 35111001:
                case 35111002:
                case 35111004:
                case 35111005:
                case 35111009:
                case 35111010:
                case 35111011:
                case 35111013:
                case 35120000:
                case 35121003:
                case 35121005:
                case 35121006:
                case 35121009:
                case 35121010:
                case 35121013:
                case 80001000:
                case 80001089:
                    isBuff = true;
            }
        }

        ret.chargeskill = (data.getChildByPath("keydown") != null);

        MapleData level = data.getChildByPath("common");
        if (level != null) {
            ret.maxLevel = MapleDataTool.getInt("maxLevel", level, 1);
            ret.trueMax = (ret.maxLevel + (ret.combatOrders ? 2 : 0));
            for (int i = 1; i <= ret.trueMax; i++) {
                ret.effects.add(MapleStatEffect.loadSkillEffectFromData(level, id, isBuff, i, "x"));
            }
        } else {
            for (MapleData leve : data.getChildByPath("level")) {
                ret.effects.add(MapleStatEffect.loadSkillEffectFromData(leve, id, isBuff, Byte.parseByte(leve.getName()), null));
            }
            ret.maxLevel = ret.effects.size();
            ret.trueMax = ret.effects.size();
        }
        boolean loadPvpSkill = false;
        if (loadPvpSkill) {
            MapleData level2 = data.getChildByPath("PVPcommon");
            if (level2 != null) {
                ret.pvpEffects = new ArrayList();
                for (int i = 1; i <= ret.trueMax; i++) {
                    ret.pvpEffects.add(MapleStatEffect.loadSkillEffectFromData(level2, id, isBuff, i, "x"));
                }
            }
        }
        MapleData reqDataRoot = data.getChildByPath("req");
        if (reqDataRoot != null) {
            for (MapleData reqData : reqDataRoot.getChildren()) {
                ret.requiredSkill.add(new Pair(Integer.valueOf(Integer.parseInt(reqData.getName())), Byte.valueOf((byte) MapleDataTool.getInt(reqData, 1))));
            }
        }
        ret.animationTime = 0;
        if (effect != null) {
            for (MapleData effectEntry : effect) {
                ret.animationTime += MapleDataTool.getIntConvert("delay", effectEntry, 0);
            }
        }
        return ret;
    }

    public MapleStatEffect getEffect(int level) {
        if (effects.size() < level) {
            if (effects.size() > 0) {
                return effects.get(effects.size() - 1);
            }
            return null;
        }
        if (level <= 0) {
            return effects.get(0);
        }
        return effects.get(level - 1);
    }

    public MapleStatEffect getPVPEffect(int level) {
        if (pvpEffects == null) {
            return getEffect(level);
        }
        if (pvpEffects.size() < level) {
            if (pvpEffects.size() > 0) {
                return pvpEffects.get(pvpEffects.size() - 1);
            }
            return null;
        }
        if (level <= 0) {
            return pvpEffects.get(0);
        }
        return pvpEffects.get(level - 1);
    }

    public int getSkillType() {
        return skillType;
    }

    public List<Integer> getAllAnimation() {
        return animation;
    }

    public int getAnimation() {
        if (animation == null) {
            return -1;
        }
        return animation.get(Randomizer.nextInt(animation.size()));
    }

    public boolean isPVPDisabled() {
        return this.pvpDisabled;
    }

    public boolean isChargeSkill() {
        return this.chargeskill;
    }

    public boolean isInvisible() {
        return this.invisible;
    }

    public boolean hasRequiredSkill() {
        return this.requiredSkill.size() > 0;
    }

    public List<Pair<Integer, Byte>> getRequiredSkills() {
        return this.requiredSkill;
    }

    public int getMaxLevel() {
        return this.maxLevel;
    }

    public int getTrueMax() {
        return this.trueMax;
    }

    public boolean combatOrders() {
        return this.combatOrders;
    }

    public boolean canBeLearnedBy(int job) {
        int jid = job;
        int skillForJob = this.id / 10000;
        if (skillForJob == 2001) {
            return GameConstants.is龙神(job);
        }
        if (skillForJob == 0) {
            return GameConstants.is冒险家(job);
        }
        if (skillForJob == 1000) {
            return GameConstants.is骑士团(job);
        }
        if (skillForJob == 2000) {
            return GameConstants.is战神(job);
        }
        if (skillForJob == 3000) {
            return GameConstants.is反抗者(job);
        }
        if (skillForJob == 5000) {
            return GameConstants.is米哈尔(job);
        }
        if (skillForJob == 501) {
            return GameConstants.is火炮手(job);
        }
        if (skillForJob == 3001) {
            return GameConstants.is恶魔猎手(job);
        }
        if (skillForJob == 2002) {
            return GameConstants.is双弩精灵(job);
        }
        if (skillForJob == 508) {
            return GameConstants.is龙的传人(job);
        }
        if (skillForJob == 2003) {
            return GameConstants.is幻影(job);
        }
        if (skillForJob == 509) {
            return (GameConstants.is拳手新(job)) || (GameConstants.is火枪手新(job));
        }
        if (skillForJob == 500) {
            return (GameConstants.is拳手(job)) || (GameConstants.is火枪手(job));
        }
        if (jid / 100 != skillForJob / 100) {
            return false;
        }
        if (jid / 1000 != skillForJob / 1000) {
            return false;
        }
        if ((GameConstants.is幻影(skillForJob)) && (!GameConstants.is幻影(job))) {
            return false;
        }
        if ((GameConstants.is龙的传人(skillForJob)) && (!GameConstants.is龙的传人(job))) {
            return false;
        }
        if ((GameConstants.is火炮手(skillForJob)) && (!GameConstants.is火炮手(job))) {
            return false;
        }
        if ((GameConstants.is拳手(skillForJob)) && (!GameConstants.is拳手(job))) {
            return false;
        }
        if ((GameConstants.is火枪手(skillForJob)) && (!GameConstants.is火枪手(job))) {
            return false;
        }
        if ((GameConstants.is拳手新(skillForJob)) && (!GameConstants.is拳手新(job))) {
            return false;
        }
        if ((GameConstants.is火枪手新(skillForJob)) && (!GameConstants.is火枪手新(job))) {
            return false;
        }
        if ((GameConstants.is恶魔猎手(skillForJob)) && (!GameConstants.is恶魔猎手(job))) {
            return false;
        }
        if ((GameConstants.is冒险家(skillForJob)) && (!GameConstants.is冒险家(job))) {
            return false;
        }
        if ((GameConstants.is骑士团(skillForJob)) && (!GameConstants.is骑士团(job))) {
            return false;
        }
        if ((GameConstants.is战神(skillForJob)) && (!GameConstants.is战神(job))) {
            return false;
        }
        if ((GameConstants.is龙神(skillForJob)) && (!GameConstants.is龙神(job))) {
            return false;
        }
        if ((GameConstants.is双弩精灵(skillForJob)) && (!GameConstants.is双弩精灵(job))) {
            return false;
        }
        if ((GameConstants.is反抗者(skillForJob)) && (!GameConstants.is反抗者(job))) {
            return false;
        }
    //    if ((GameConstants.is米哈尔(skillForJob)) && (!GameConstants.is米哈尔(job))) {
       //     return false;
      //  }
        if ((jid / 10 % 10 == 0) && (skillForJob / 10 % 10 > jid / 10 % 10)) {
            return false;
        }
        if ((skillForJob / 10 % 10 != 0) && (skillForJob / 10 % 10 != jid / 10 % 10)) {
            return false;
        }
        return skillForJob % 10 <= jid % 10;
    }

    public boolean isTimeLimited() {
        return this.timeLimited;
    }

    public boolean isFourthJob() {
        if ((this.id == 23120011) || (this.id == 5720008) || (this.id == 4320005) || (this.id == 4340010)) {
            return false;
        }
        if ((this.id / 10000 == 2312) || (this.id / 10000 == 2412)) {
            return true;
        }
        if (((getMaxLevel() <= 15) && (!this.invisible) && (getMasterLevel() <= 0)) || (this.id == 3220010) || (this.id == 3120011) || (this.id == 33121005) || (this.id == 33120010) || (this.id == 5321006) || (this.id == 21120011) || (this.id == 22181004)) {
            return false;
        }
        if ((this.id / 10000 >= 2212) && (this.id / 10000 < 3000)) {
            return this.id / 10000 % 10 >= 7;
        }
        if ((this.id / 10000 >= 430) && (this.id / 10000 <= 434)) {
            return (this.id / 10000 % 10 == 4) || (getMasterLevel() > 0);
        }
        return (this.id / 10000 % 10 == 2) && (this.id < 90000000) && (!isBeginnerSkill());
    }

    public Element getElement() {
        return this.element;
    }

    public int getAnimationTime() {
        return this.animationTime;
    }

    public int getMasterLevel() {
        return this.masterLevel;
    }

    public int getDelay() {
        return this.delay;
    }

    public int getTamingMob() {
        return this.eventTamingMob;
    }

    public boolean isBeginnerSkill() {
        int jobId = this.id / 10000;
        return GameConstants.is新手职业(jobId);
    }

    public boolean isMagic() {
        return this.magic;
    }

    public boolean isMovement() {
        return this.casterMove;
    }

    public boolean isPush() {
        return this.pushTarget;
    }

    public boolean isPull() {
        return this.pullTarget;
    }

    public boolean isSpecialSkill() {
        int jobId = this.id / 10000;
        return (jobId == 900) || (jobId == 800) || (jobId == 9000) || (jobId == 9200) || (jobId == 9201) || (jobId == 9202) || (jobId == 9203) || (jobId == 9204);
    }
}