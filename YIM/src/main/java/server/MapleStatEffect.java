package server;

import client.*;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.status.MonsterStatus;
import client.status.MonsterStatusEffect;
import constants.GameConstants;
import handling.channel.ChannelServer;
import handling.world.MaplePartyCharacter;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;
import org.apache.log4j.Logger;
import provider.MapleData;
import provider.MapleDataTool;
import provider.MapleDataType;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.maps.*;
import tools.*;

public class MapleStatEffect
        implements Serializable {

    private static final long serialVersionUID = 9179541993413738569L;
    private byte mastery;
    private byte mhpR;
    private byte mmpR;
    private byte mobCount;
    private byte attackCount;
    private byte bulletCount;
    private byte reqGuildLevel;
    private byte period;
    private byte expR;
    private byte familiarTarget;
    private byte iceGageCon;
    private byte recipeUseCount;
    private byte recipeValidDay;
    private byte reqSkillLevel;
    private byte slotCount;
    private byte effectedOnAlly;
    private byte effectedOnEnemy;
    private byte type;
    private byte preventslip;
    private byte immortal;
    private byte bs;
    private short hp;
    private short mp;
    private short watk;
    private short matk;
    private short wdef;
    private short mdef;
    private short acc;
    private short avoid;
    private short hands;
    private short speed;
    private short jump;
    private short psdSpeed;
    private short psdJump;
    private short mpCon;
    private short hpCon;
    private short forceCon;
    private short bdR;
    private short damage;
    private short prop;
    private short ehp;
    private short emp;
    private short ewatk;
    private short ematk;
    private short ewdef;
    private short emdef;
    private short ignoreMob;
    private short dot;
    private short dotTime;
    private short criticaldamageMin;
    private short criticaldamageMax;
    private short pddR;
    private short mddR;
    private short asrR;
    private short er;
    private short damR;
    private short padX;
    private short madX;
    private short mesoR;
    private short thaw;
    private short selfDestruction;
    private short PVPdamage;
    private short indiePad;
    private short indieMad;
    private short fatigueChange;
    private short str;
    private short dex;
    private short int_;
    private short luk;
    private short strX;
    private short dexX;
    private short intX;
    private short lukX;
    private short lifeId;
    private short imhp;
    private short immp;
    private short inflation;
    private short useLevel;
    private short mpConReduce;
    private short indieMhp;
    private short indieMmp;
    private short indieAllStat;
    private short indieSpeed;
    private short indieJump;
    private short indieAcc;
    private short indieEva;
    private short indiePdd;
    private short indieMdd;
    private short incPVPdamage;
    private short indieMhpR;
    private short indieMmpR;
    private short mobSkill;
    private short mobSkillLevel;
    private short mhpX;
    private short mmpX;
    private short indieDamR;
    private double hpR;
    private double mpR;
    private Map<MapleTrait.MapleTraitType, Integer> traits;
    private int duration;
    private int subTime;
    private int sourceid;
    private int recipe;
    private int moveTo;
    private int t;
    private int u;
    private int v;
    private int w;
    private int x;
    private int y;
    private int z;
    private int cr;
    private int itemCon;
    private int itemConNo;
    private int bulletConsume;
    private int moneyCon;
    private int cooldown;
    private int morphId;
    private int expinc;
    private int exp;
    private int consumeOnPickup;
    private int range;
    private int price;
    private int extendPrice;
    private int charColor;
    private int interval;
    private int rewardMeso;
    private int totalprob;
    private int cosmetic;
    private boolean overTime;
    private boolean skill;
    private boolean partyBuff;
    private ArrayList<Pair<MapleBuffStat, Integer>> statups;
    private ArrayList<Pair<Integer, Integer>> availableMap;
    private EnumMap<MonsterStatus, Integer> monsterStatus;
    private Point lt;
    private Point rb;
    private int expBuff;
    private int itemup;
    private int mesoup;
    private int cashup;
    private int berserk;
    private int illusion;
    private int booster;
    private int berserk2;
    private int cp;
    private int nuffSkill;
    private byte level;
    private List<MapleDisease> cureDebuffs;
    private List<Integer> petsCanConsume;
    private List<Integer> familiars;
    private List<Integer> randomPickup;
    private List<Triple<Integer, Integer, Integer>> rewardItem;
    private static final Logger log = Logger.getLogger(MapleStatEffect.class);

    public MapleStatEffect() {
        this.morphId = 0;

        this.partyBuff = true;
    }

    public static MapleStatEffect loadSkillEffectFromData(MapleData source, int skillid, boolean overtime, int level, String variables) {
        return loadFromData(source, skillid, true, overtime, level, variables);
    }

    public static MapleStatEffect loadItemEffectFromData(MapleData source, int itemid) {
        return loadFromData(source, itemid, false, false, 1, null);
    }

    private static void addBuffStatPairToListIfNotZero(List<Pair<MapleBuffStat, Integer>> list, MapleBuffStat buffstat, Integer val) {
        if (val.intValue() != 0) {
            list.add(new Pair(buffstat, val));
        }
    }

    private static int parseEval(String path, MapleData source, int def, String variables, int level) {
        if (variables == null) {
            return MapleDataTool.getIntConvert(path, source, def);
        }
        MapleData dd = source.getChildByPath(path);
        if (dd == null) {
            return def;
        }
        if (dd.getType() != MapleDataType.STRING) {
            return MapleDataTool.getIntConvert(path, source, def);
        }
        String dddd = MapleDataTool.getString(dd).replace(variables, String.valueOf(level));
        if (dddd.substring(0, 1).equals("-")) {
            if ((dddd.substring(1, 2).equals("u")) || (dddd.substring(1, 2).equals("d"))) {
                dddd = "n(" + dddd.substring(1, dddd.length()) + ")";
            } else {
                dddd = "n" + dddd.substring(1, dddd.length());
            }
        } else if (dddd.substring(0, 1).equals("=")) {
            dddd = dddd.substring(1, dddd.length());
        }
        return (int) new CaltechEval(dddd).evaluate();
    }

    private static MapleStatEffect loadFromData(MapleData source, int sourceid, boolean skill, boolean overTime, int level, String variables) {
        MapleStatEffect ret = new MapleStatEffect();
        ret.sourceid = sourceid;
        ret.skill = skill;
        ret.level = (byte) level;
        if (source == null) {
            return ret;
        }
        ret.duration = parseEval("time", source, -1, variables, level);
        ret.subTime = parseEval("subTime", source, -1, variables, level);
        ret.hp = (short) parseEval("hp", source, 0, variables, level);
        ret.hpR = (parseEval("hpR", source, 0, variables, level) / 100.0D);
        ret.mp = (short) parseEval("mp", source, 0, variables, level);
        ret.mpR = (parseEval("mpR", source, 0, variables, level) / 100.0D);
        ret.mhpR = (byte) parseEval("mhpR", source, 0, variables, level);
        ret.mmpR = (byte) parseEval("mmpR", source, 0, variables, level);
        ret.pddR = (short) parseEval("pddR", source, 0, variables, level);
        ret.mddR = (short) parseEval("mddR", source, 0, variables, level);
        ret.mhpX = (short) parseEval("mhpX", source, 0, variables, level);
        ret.mmpX = (short) parseEval("mmpX", source, 0, variables, level);
        ret.ignoreMob = (short) parseEval("ignoreMobpdpR", source, 0, variables, level);
        ret.asrR = (short) parseEval("asrR", source, 0, variables, level);
        ret.bdR = (short) parseEval("bdR", source, 0, variables, level);
        ret.damR = (short) parseEval("damR", source, 0, variables, level);
        ret.mesoR = (short) parseEval("mesoR", source, 0, variables, level);
        ret.thaw = (short) parseEval("thaw", source, 0, variables, level);
        ret.padX = (short) parseEval("padX", source, 0, variables, level);
        ret.madX = (short) parseEval("madX", source, 0, variables, level);
        ret.dot = (short) parseEval("dot", source, 0, variables, level);
        ret.dotTime = (short) parseEval("dotTime", source, 0, variables, level);
        ret.criticaldamageMin = (short) parseEval("criticaldamageMin", source, 0, variables, level);
        ret.criticaldamageMax = (short) parseEval("criticaldamageMax", source, 0, variables, level);
        ret.mpConReduce = (short) parseEval("mpConReduce", source, 0, variables, level);
        ret.forceCon = (short) parseEval("forceCon", source, 0, variables, level);
        ret.mpCon = (short) parseEval("mpCon", source, 0, variables, level);
        ret.hpCon = (short) parseEval("hpCon", source, 0, variables, level);
        ret.prop = (short) parseEval("prop", source, 100, variables, level);
        ret.cooldown = Math.max(0, parseEval("cooltime", source, 0, variables, level));
        ret.interval = parseEval("interval", source, 0, variables, level);
        ret.expinc = parseEval("expinc", source, 0, variables, level);
        ret.exp = parseEval("exp", source, 0, variables, level);
        ret.range = parseEval("range", source, 0, variables, level);
        ret.morphId = parseEval("morph", source, 0, variables, level);
        ret.cp = parseEval("cp", source, 0, variables, level);
        ret.cosmetic = parseEval("cosmetic", source, 0, variables, level);
        ret.er = (short) parseEval("er", source, 0, variables, level);
        ret.slotCount = (byte) parseEval("slotCount", source, 0, variables, level);
        ret.preventslip = (byte) parseEval("preventslip", source, 0, variables, level);
        ret.useLevel = (short) parseEval("useLevel", source, 0, variables, level);
        ret.nuffSkill = parseEval("nuffSkill", source, 0, variables, level);
        ret.familiarTarget = (byte) (parseEval("familiarPassiveSkillTarget", source, 0, variables, level) + 1);
        ret.mobCount = (byte) parseEval("mobCount", source, 1, variables, level);
        ret.immortal = (byte) parseEval("immortal", source, 0, variables, level);
        ret.iceGageCon = (byte) parseEval("iceGageCon", source, 0, variables, level);
        ret.expR = (byte) parseEval("expR", source, 0, variables, level);
        ret.reqGuildLevel = (byte) parseEval("reqGuildLevel", source, 0, variables, level);
        ret.period = (byte) parseEval("period", source, 0, variables, level);
        ret.type = (byte) parseEval("type", source, 0, variables, level);
        ret.bs = (byte) parseEval("bs", source, 0, variables, level);
        ret.attackCount = (byte) parseEval("attackCount", source, 1, variables, level);
        ret.bulletCount = (byte) parseEval("bulletCount", source, 1, variables, level);
        int priceUnit = parseEval("priceUnit", source, 0, variables, level);
        if (priceUnit > 0) {
            ret.price = (parseEval("price", source, 0, variables, level) * priceUnit);
            ret.extendPrice = (parseEval("extendPrice", source, 0, variables, level) * priceUnit);
        } else {
            ret.price = 0;
            ret.extendPrice = 0;
        }
        if (ret.skill) {
            switch (sourceid) {
                case 1100002:
                case 1120013:
                case 1200002:
                case 1300002:
                case 2111007:
                case 2211007:
                case 2311007:
                case 3100001:
                case 3120008:
                case 3200001:
                case 11101002:
                case 12111007:
                case 13101002:
                case 21100010:
                case 21120012:
                case 22150004:
                case 22161005:
                case 22181004:
                case 23100006:
                case 23120012:
                case 32111010:
                case 33100009:
                case 33120011:
                case 51100002:
                case 51120002:
                    ret.mobCount = 6;
                    break;
                case 24100003:
                case 24120002:
                case 35111004:
                case 35121005:
                case 35121013:
                    ret.attackCount = 6;
                    ret.bulletCount = 6;
            }

            if (GameConstants.isNoDelaySkill(sourceid)) {
                ret.mobCount = 6;
            }
        }
        if ((!ret.skill) && (ret.duration > -1)) {
            ret.overTime = true;
        } else {
            ret.duration *= 1000;
            ret.subTime *= 1000;
            ret.overTime = ((overTime) || (ret.isMorph()) || (ret.isPirateMorph()) || (ret.is终极弓剑()) || (ret.isAngel()));
        }
        ret.statups = new ArrayList();
        ret.mastery = (byte) parseEval("mastery", source, 0, variables, level);
        ret.watk = (short) parseEval("pad", source, 0, variables, level);
        ret.wdef = (short) parseEval("pdd", source, 0, variables, level);
        ret.matk = (short) parseEval("mad", source, 0, variables, level);
        ret.mdef = (short) parseEval("mdd", source, 0, variables, level);
        ret.ehp = (short) parseEval("emhp", source, 0, variables, level);
        ret.emp = (short) parseEval("emmp", source, 0, variables, level);
        ret.ewatk = (short) parseEval("epad", source, 0, variables, level);
        ret.ematk = (short) parseEval("emad", source, 0, variables, level);
        ret.ewdef = (short) parseEval("epdd", source, 0, variables, level);
        ret.emdef = (short) parseEval("emdd", source, 0, variables, level);
        ret.acc = (short) parseEval("acc", source, 0, variables, level);
        ret.avoid = (short) parseEval("eva", source, 0, variables, level);
        ret.speed = (short) parseEval("speed", source, 0, variables, level);
        ret.jump = (short) parseEval("jump", source, 0, variables, level);
        ret.psdSpeed = (short) parseEval("psdSpeed", source, 0, variables, level);
        ret.psdJump = (short) parseEval("psdJump", source, 0, variables, level);
        ret.indieDamR = (short) parseEval("indieDamR", source, 0, variables, level);
        ret.indiePad = (short) parseEval("indiePad", source, 0, variables, level);
        ret.indieMad = (short) parseEval("indieMad", source, 0, variables, level);
        ret.indieMhp = (short) parseEval("indieMhp", source, 0, variables, level);
        ret.indieMmp = (short) parseEval("indieMmp", source, 0, variables, level);
        ret.indieMhpR = (short) parseEval("indieMhpR", source, 0, variables, level);
        ret.indieMmpR = (short) parseEval("indieMmpR", source, 0, variables, level);
        ret.indieSpeed = (short) parseEval("indieSpeed", source, 0, variables, level);
        ret.indieJump = (short) parseEval("indieJump", source, 0, variables, level);
        ret.indieAcc = (short) parseEval("indieAcc", source, 0, variables, level);
        ret.indieEva = (short) parseEval("indieEva", source, 0, variables, level);
        ret.indiePdd = (short) parseEval("indiePdd", source, 0, variables, level);
        ret.indieMdd = (short) parseEval("indieMdd", source, 0, variables, level);
        ret.indieAllStat = (short) parseEval("indieAllStat", source, 0, variables, level);
        ret.str = (short) parseEval("str", source, 0, variables, level);
        ret.dex = (short) parseEval("dex", source, 0, variables, level);
        ret.int_ = (short) parseEval("int", source, 0, variables, level);
        ret.luk = (short) parseEval("luk", source, 0, variables, level);
        ret.strX = (short) parseEval("strX", source, 0, variables, level);
        ret.dexX = (short) parseEval("dexX", source, 0, variables, level);
        ret.intX = (short) parseEval("intX", source, 0, variables, level);
        ret.lukX = (short) parseEval("lukX", source, 0, variables, level);
        ret.expBuff = parseEval("expBuff", source, 0, variables, level);
        ret.cashup = parseEval("cashBuff", source, 0, variables, level);
        ret.itemup = parseEval("itemupbyitem", source, 0, variables, level);
        ret.mesoup = parseEval("mesoupbyitem", source, 0, variables, level);
        ret.berserk = parseEval("berserk", source, 0, variables, level);
        ret.berserk2 = parseEval("berserk2", source, 0, variables, level);
        ret.booster = parseEval("booster", source, 0, variables, level);
        ret.lifeId = (short) parseEval("lifeId", source, 0, variables, level);
        ret.inflation = (short) parseEval("inflation", source, 0, variables, level);
        ret.imhp = (short) parseEval("imhp", source, 0, variables, level);
        ret.immp = (short) parseEval("immp", source, 0, variables, level);
        ret.illusion = parseEval("illusion", source, 0, variables, level);
        ret.consumeOnPickup = parseEval("consumeOnPickup", source, 0, variables, level);
        if ((ret.consumeOnPickup == 1)
                && (parseEval("party", source, 0, variables, level) > 0)) {
            ret.consumeOnPickup = 2;
        }

        ret.charColor = 0;
        String cColor = MapleDataTool.getString("charColor", source, null);
        if (cColor != null) {
            ret.charColor |= Integer.parseInt("0x" + cColor.substring(0, 2));
            ret.charColor |= Integer.parseInt("0x" + cColor.substring(2, 4) + "00");
            ret.charColor |= Integer.parseInt("0x" + cColor.substring(4, 6) + "0000");
            ret.charColor |= Integer.parseInt("0x" + cColor.substring(6, 8) + "000000");
        }
        ret.traits = new EnumMap(MapleTrait.MapleTraitType.class);
        for (MapleTrait.MapleTraitType t : MapleTrait.MapleTraitType.values()) {
            int expz = parseEval(t.name() + "EXP", source, 0, variables, level);
            if (expz != 0) {
                ret.traits.put(t, Integer.valueOf(expz));
            }
        }
        ret.recipe = parseEval("recipe", source, 0, variables, level);
        ret.recipeUseCount = (byte) parseEval("recipeUseCount", source, 0, variables, level);
        ret.recipeValidDay = (byte) parseEval("recipeValidDay", source, 0, variables, level);
        ret.reqSkillLevel = (byte) parseEval("reqSkillLevel", source, 0, variables, level);

        ret.effectedOnAlly = (byte) parseEval("effectedOnAlly", source, 0, variables, level);
        ret.effectedOnEnemy = (byte) parseEval("effectedOnEnemy", source, 0, variables, level);

        List cure = new ArrayList(5);
        if (parseEval("poison", source, 0, variables, level) > 0) {
            cure.add(MapleDisease.POISON);
        }
        if (parseEval("seal", source, 0, variables, level) > 0) {
            cure.add(MapleDisease.SEAL);
        }
        if (parseEval("darkness", source, 0, variables, level) > 0) {
            cure.add(MapleDisease.DARKNESS);
        }
        if (parseEval("weakness", source, 0, variables, level) > 0) {
            cure.add(MapleDisease.WEAKEN);
        }
        if (parseEval("curse", source, 0, variables, level) > 0) {
            cure.add(MapleDisease.CURSE);
        }
        ret.cureDebuffs = cure;

        ret.petsCanConsume = new ArrayList();
        for (int i = 0;; i++) {
            int dd = parseEval(String.valueOf(i), source, 0, variables, level);
            if (dd <= 0) {
                break;
            }
            ret.petsCanConsume.add(Integer.valueOf(dd));
        }

        MapleData mdd = source.getChildByPath("0");
        if ((mdd != null) && (mdd.getChildren().size() > 0)) {
            ret.mobSkill = (short) parseEval("mobSkill", mdd, 0, variables, level);
            ret.mobSkillLevel = (short) parseEval("level", mdd, 0, variables, level);
        } else {
            ret.mobSkill = 0;
            ret.mobSkillLevel = 0;
        }
        MapleData pd = source.getChildByPath("randomPickup");
        if (pd != null) {
            ret.randomPickup = new ArrayList();
            for (MapleData p : pd) {
                ret.randomPickup.add(Integer.valueOf(MapleDataTool.getInt(p)));
            }
        }
        MapleData ltd = source.getChildByPath("lt");
        if (ltd != null) {
            ret.lt = ((Point) ltd.getData());
            ret.rb = ((Point) source.getChildByPath("rb").getData());
        }
        MapleData ltc = source.getChildByPath("con");
        if (ltc != null) {
            ret.availableMap = new ArrayList();
            for (MapleData ltb : ltc) {
                ret.availableMap.add(new Pair(Integer.valueOf(MapleDataTool.getInt("sMap", ltb, 0)), Integer.valueOf(MapleDataTool.getInt("eMap", ltb, 999999999))));
            }
        }
        MapleData ltb = source.getChildByPath("familiar");
        if (ltb != null) {
            ret.fatigueChange = (short) (parseEval("incFatigue", ltb, 0, variables, level) - parseEval("decFatigue", ltb, 0, variables, level));
            ret.familiarTarget = (byte) parseEval("target", ltb, 0, variables, level);
            MapleData lta = ltb.getChildByPath("targetList");
            if (lta != null) {
                ret.familiars = new ArrayList();
                for (MapleData ltz : lta) {
                    ret.familiars.add(Integer.valueOf(MapleDataTool.getInt(ltz, 0)));
                }
            }
        } else {
            ret.fatigueChange = 0;
        }
        int totalprob = 0;
        MapleData lta = source.getChildByPath("reward");
        if (lta != null) {
            ret.rewardMeso = parseEval("meso", lta, 0, variables, level);
            MapleData ltz = lta.getChildByPath("case");
            if (ltz != null) {
                ret.rewardItem = new ArrayList();
                for (MapleData lty : ltz) {
                    ret.rewardItem.add(new Triple(Integer.valueOf(MapleDataTool.getInt("id", lty, 0)), Integer.valueOf(MapleDataTool.getInt("count", lty, 0)), Integer.valueOf(MapleDataTool.getInt("prop", lty, 0))));
                    totalprob += MapleDataTool.getInt("prob", lty, 0);
                }
            }
        } else {
            ret.rewardMeso = 0;
        }
        ret.totalprob = totalprob;
        ret.cr = parseEval("cr", source, 0, variables, level);
        ret.t = parseEval("t", source, 0, variables, level);
        ret.u = parseEval("u", source, 0, variables, level);
        ret.v = parseEval("v", source, 0, variables, level);
        ret.w = parseEval("w", source, 0, variables, level);
        ret.x = parseEval("x", source, 0, variables, level);
        ret.y = parseEval("y", source, 0, variables, level);
        ret.z = parseEval("z", source, 0, variables, level);
        ret.damage = (short) parseEval("damage", source, 100, variables, level);
        ret.PVPdamage = (short) parseEval("PVPdamage", source, 0, variables, level);
        ret.incPVPdamage = (short) parseEval("incPVPDamage", source, 0, variables, level);
        ret.selfDestruction = (short) parseEval("selfDestruction", source, 0, variables, level);
        ret.bulletConsume = parseEval("bulletConsume", source, 0, variables, level);
        ret.moneyCon = parseEval("moneyCon", source, 0, variables, level);

        ret.itemCon = parseEval("itemCon", source, 0, variables, level);
        ret.itemConNo = parseEval("itemConNo", source, 0, variables, level);
        ret.moveTo = parseEval("moveTo", source, -1, variables, level);
        ret.monsterStatus = new EnumMap(MonsterStatus.class);
        if ((ret.overTime) && (ret.getSummonMovementType() == null) && (!ret.is能量获得())) {
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.物理攻击, Integer.valueOf(ret.watk));
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.物理防御, Integer.valueOf(ret.wdef));
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.魔法攻击, Integer.valueOf(ret.matk));
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.魔法防御, Integer.valueOf(ret.mdef));
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.命中率, Integer.valueOf(ret.acc));
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.回避率, Integer.valueOf(ret.avoid));
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.移动速度, (sourceid == 32120001) || (sourceid == 32101003) ? Integer.valueOf(ret.x) : Integer.valueOf(ret.speed));
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.跳跃力, Integer.valueOf(ret.jump));
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.MAXHP, Integer.valueOf(ret.mhpR));
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.MAXMP, Integer.valueOf(ret.mmpR));
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.攻击加速, Integer.valueOf(ret.booster));
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.HP_LOSS_GUARD, Integer.valueOf(ret.thaw));
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.EXPRATE, Integer.valueOf(ret.expBuff));
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.ACASH_RATE, Integer.valueOf(ret.cashup));
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.DROP_RATE, Integer.valueOf(GameConstants.getModifier(ret.sourceid, ret.itemup)));
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.MESO_RATE, Integer.valueOf(GameConstants.getModifier(ret.sourceid, ret.mesoup)));
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.狂暴战魂, Integer.valueOf(ret.berserk2));
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.ILLUSION, Integer.valueOf(ret.illusion));
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.PYRAMID_PQ, Integer.valueOf(ret.berserk));
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.增强_MAXHP, Integer.valueOf(ret.ehp));
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.增强_MAXMP, Integer.valueOf(ret.emp));
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.增强_物理攻击, Integer.valueOf(ret.ewatk));
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.增强_魔法攻击, Integer.valueOf(ret.ematk));
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.增强_物理防御, Integer.valueOf(ret.ewdef));
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.增强_魔法防御, Integer.valueOf(ret.emdef));
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.GIANT_POTION, Integer.valueOf(ret.inflation));
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.力量, Integer.valueOf(ret.str));
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.敏捷, Integer.valueOf(ret.dex));
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.智力, Integer.valueOf(ret.int_));
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.运气, Integer.valueOf(ret.luk));
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.ANGEL_ATK, Integer.valueOf(ret.indiePad));
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.ANGEL_MATK, Integer.valueOf(ret.indieMad));
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.HP_BOOST, Integer.valueOf(ret.imhp));
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.MP_BOOST, Integer.valueOf(ret.immp));
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.HP_BOOST, Integer.valueOf(ret.indieMhp));
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.MP_BOOST, Integer.valueOf(ret.indieMmp));
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.PVP_DAMAGE, Integer.valueOf(ret.incPVPdamage));
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.ANGEL_JUMP, Integer.valueOf(ret.indieJump));
            if (sourceid != 35001002) {
                addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.ANGEL_SPEED, Integer.valueOf(ret.indieSpeed));
            }
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.ANGEL_ACC, Integer.valueOf(ret.indieAcc));
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.ANGEL_AVOID, Integer.valueOf(ret.indieEva));
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.ANGEL_STAT, Integer.valueOf(ret.indieAllStat));
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.PVP_ATTACK, Integer.valueOf(ret.PVPdamage));
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.INVINCIBILITY, Integer.valueOf(ret.immortal));
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.NO_SLIP, Integer.valueOf(ret.preventslip));
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStat.FAMILIAR_SHADOW, Integer.valueOf(ret.charColor > 0 ? 1 : 0));
            if ((sourceid == 5921006) || (ret.isPirateMorph())) {
                ret.statups.add(new Pair(MapleBuffStat.双刀荆棘, Integer.valueOf(100)));
            }
        }
        if (ret.skill) {
            switch (sourceid) {
                case 2001002:
                case 12001001:
                case 22111001:
                    ret.statups.add(new Pair(MapleBuffStat.魔法盾, Integer.valueOf(ret.x)));
                    break;
                case 2301003:
                    ret.statups.add(new Pair(MapleBuffStat.神之保护, Integer.valueOf(ret.x)));
                    break;
                case 35120000:
                    ret.duration = 2100000000;
                    ret.statups.add(new Pair(MapleBuffStat.骑兽技能, Integer.valueOf(0)));
                    break;
                case 35001002:
                    ret.duration = 2100000000;
                    ret.statups.add(new Pair(MapleBuffStat.骑兽技能, Integer.valueOf(0)));

                    break;
                case 9001004:
                case 9101004:
                    ret.duration = 2100000000;
                    ret.statups.add(new Pair(MapleBuffStat.隐身术, Integer.valueOf(ret.x)));
                    break;
                case 13101006:
                    ret.statups.add(new Pair(MapleBuffStat.风影漫步, Integer.valueOf(ret.x)));
                    break;
                case 4330001:
                    ret.statups.add(new Pair(MapleBuffStat.隐身术, Integer.valueOf(ret.level)));
                    break;
                case 4001003:
                case 14001003:
                    ret.statups.add(new Pair(MapleBuffStat.隐身术, Integer.valueOf(ret.x)));
                    break;
                case 4211003:
                    ret.duration = 2100000000;
                    ret.statups.add(new Pair(MapleBuffStat.敛财术, Integer.valueOf(ret.x)));
                    break;
                case 4201011:
                    ret.statups.add(new Pair(MapleBuffStat.金钱护盾, Integer.valueOf(ret.x)));
                    break;
                case 4111002:
                case 4331002:
                case 14111000:
                    ret.statups.add(new Pair(MapleBuffStat.影分身, Integer.valueOf(ret.x)));
                    break;
                case 4211008:
                    ret.statups.add(new Pair(MapleBuffStat.影分身, Integer.valueOf(ret.level)));
                    break;
                case 4221013:
                    ret.statups.add(new Pair(MapleBuffStat.ANGEL_ATK, Integer.valueOf(ret.x)));
                    break;
                case 11101002:
                case 13101002:
                    ret.statups.add(new Pair(MapleBuffStat.终极弓剑, Integer.valueOf(ret.x)));
                    break;
                case 22161004:
                    ret.statups.add(new Pair(MapleBuffStat.玛瑙的保佑, Integer.valueOf(ret.x)));
                    break;
                case 2311002:
                case 3101004:
                case 3201004:
                case 13101003:
                case 33101003:
                case 35101005:
                    ret.statups.add(new Pair(MapleBuffStat.无形箭弩, Integer.valueOf(ret.x)));
                    break;
                case 2121009:
                case 2221009:
                case 2321010:
                    ret.duration = 2100000000;
                    ret.statups.add(new Pair(MapleBuffStat.属性抗性, Integer.valueOf(ret.x)));
                    break;
                case 2120010:
                case 2220010:
                case 2320011:
                    ret.duration = 2100000000;
                    ret.statups.add(new Pair(MapleBuffStat.神秘瞄准术, Integer.valueOf(ret.x)));
                    break;
                case 1211004:
                case 1211006:
                case 1211008:
                case 1221004:
                case 11111007:
                case 21101006:
                    ret.statups.add(new Pair(MapleBuffStat.属性攻击, Integer.valueOf(ret.x)));
                    break;
                case 2111008:
                case 2211008:
                case 12101005:
                case 22131002:
                    ret.statups.add(new Pair(MapleBuffStat.自然力重置, Integer.valueOf(ret.x)));
                    break;
                case 3111000:
                case 3211000:
                case 13111001:
                    ret.statups.add(new Pair(MapleBuffStat.集中精力, Integer.valueOf(ret.x)));
                    break;
                case 5810001:
                case 15100004:
                    ret.statups.add(new Pair(MapleBuffStat.能量获得, Integer.valueOf(0)));
                    break;
                case 1101004:
                case 1201004:
                case 1301004:
                case 2111005:
                case 2211005:
                case 2311006:
                case 3101002:
                case 3201002:
                case 4101003:
                case 4201002:
                case 4301002:
                case 5301002:
                case 5701005:
                case 5801006:
                case 5901003:
                case 11101001:
                case 12101004:
                case 13101001:
                case 14101002:
                case 15101002:
                case 22141002:
                case 23101002:
                case 24101005:
                case 31001001:
                case 32101005:
                case 33001003:
                case 35101006:
                case 51101003:
                    ret.statups.add(new Pair(MapleBuffStat.攻击加速, Integer.valueOf(ret.x)));
                    break;
                case 21001003:
                    ret.statups.add(new Pair(MapleBuffStat.攻击加速, Integer.valueOf(-ret.y)));
                    break;
                case 5311005:
                case 5320007:
                case 5711011:
                case 5720005:
                case 5811007:
                case 5911007:
                case 15111011:
                case 35111013:
                    ret.statups.add(new Pair(MapleBuffStat.幸运骰子, Integer.valueOf(0)));
                    break;
                case 5311004:
                    ret.statups.add(new Pair(MapleBuffStat.随机橡木桶, Integer.valueOf(Randomizer.nextInt(3) + 1)));
                    break;
                case 5720012:
                case 5820011:
                case 5920012:
                    ret.statups.add(new Pair(MapleBuffStat.反制攻击, Integer.valueOf(ret.indieDamR)));
                    break;
                case 5821009:
                case 15111005:
                    ret.statups.add(new Pair(MapleBuffStat.极速领域, Integer.valueOf(ret.x)));
                    break;
                case 4321000:
                    ret.duration = 1000;
                    ret.statups.add(new Pair(MapleBuffStat.疾驰速度, Integer.valueOf(100 + ret.x)));
                    ret.statups.add(new Pair(MapleBuffStat.疾驰跳跃, Integer.valueOf(ret.y)));
                    break;
                case 5091005:
                case 15001003:
                    ret.statups.add(new Pair(MapleBuffStat.疾驰速度, Integer.valueOf(ret.x)));
                    ret.statups.add(new Pair(MapleBuffStat.疾驰跳跃, Integer.valueOf(ret.y)));
                    break;
                case 1101007:
                case 1201007:
                    ret.statups.add(new Pair(MapleBuffStat.伤害反击, Integer.valueOf(ret.x)));
                    break;
                case 32111004:
                    ret.statups.add(new Pair(MapleBuffStat.幻灵转化, Integer.valueOf(ret.x)));
                    break;
                case 1301007:
                case 9001008:
                case 9101008:
                    ret.statups.add(new Pair(MapleBuffStat.MAXHP, Integer.valueOf(ret.x)));
                    ret.statups.add(new Pair(MapleBuffStat.MAXMP, Integer.valueOf(ret.x)));
                    break;
                case 1111002:
                case 11111001:
                    ret.statups.add(new Pair(MapleBuffStat.斗气集中, Integer.valueOf(1)));
                    break;
                case 21120007:
                    ret.statups.add(new Pair(MapleBuffStat.战神之盾, Integer.valueOf(ret.x)));
                    break;
                case 5721003:
                case 5911006:
                case 5920011:
                case 22151002:
                    ret.duration = 2100000000;
                    ret.statups.add(new Pair(MapleBuffStat.导航辅助, Integer.valueOf(ret.x)));
                    break;
                case 4341007:
                    ret.statups.add(new Pair(MapleBuffStat.双刀荆棘, Integer.valueOf(ret.prop)));
                    break;
                case 1311005:
                case 1311006:
                case 21111009:
                    ret.hpR = (-ret.x / 100.0D);
                    break;
                case 1211010:
                case 51111004:
                    ret.hpR = (ret.x / 100.0D);
                    break;
                case 4341002:
                    ret.duration = 60000;
                    ret.hpR = (-ret.x / 100.0D);
                    ret.statups.add(new Pair(MapleBuffStat.终极斩, Integer.valueOf(ret.y)));
                    break;
                case 2111007:
                case 2211007:
                case 2311007:
                case 22161005:
                case 32111010:
                    ret.mpCon = (short) ret.y;
                    ret.duration = 2100000000;
                    ret.statups.add(new Pair(MapleBuffStat.快速移动精通, Integer.valueOf(ret.x)));
                    ret.monsterStatus.put(MonsterStatus.眩晕, Integer.valueOf(1));
                    break;
                case 1311008:
                    ret.statups.add(new Pair(MapleBuffStat.龙之力, Integer.valueOf(ret.str)));
                    break;
                case 1121000:
                case 1221000:
                case 1321000:
                case 2121000:
                case 2221000:
                case 2321000:
                case 3121000:
                case 3221000:
                case 4121000:
                case 4221000:
                case 4341000:
                case 5321005:
                case 5721000:
                case 5821000:
                case 5921000:
                case 21121000:
                case 22171000:
                case 23121005:
                case 24121008:
                case 31121004:
                case 32121007:
                case 33121007:
                case 35121007:
                case 51121005:
                    ret.statups.add(new Pair(MapleBuffStat.冒险岛勇士, Integer.valueOf(ret.x)));
                    break;
                case 15111006:
                    ret.statups.add(new Pair(MapleBuffStat.闪光击, Integer.valueOf(ret.x)));
                    break;
                case 3121002:
                case 3221002:
                case 33121004:
                    ret.statups.add(new Pair(MapleBuffStat.火眼晶晶, Integer.valueOf((ret.x << 8) + ret.criticaldamageMax)));
                    break;
                case 22151003:
                    ret.statups.add(new Pair(MapleBuffStat.抗魔领域, Integer.valueOf(ret.x)));
                    break;
                case 2000007:
                case 12000006:
                case 22000002:
                case 32000012:
                    ret.statups.add(new Pair(MapleBuffStat.精灵弱化, Integer.valueOf(ret.x)));
                    break;
                case 21101003:
                    ret.statups.add(new Pair(MapleBuffStat.战神抗压, Integer.valueOf(ret.x)));
                    break;
                case 21000000:
                    ret.statups.add(new Pair(MapleBuffStat.矛连击强化, Integer.valueOf(100)));
                    break;
                case 51111003:
                case 51121006:
                    ret.statups.add(new Pair(MapleBuffStat.精神注入, Integer.valueOf(ret.x)));
                    break;
                case 23101003:
                    ret.statups.add(new Pair(MapleBuffStat.精神连接, Integer.valueOf(ret.x)));
                    ret.statups.add(new Pair(MapleBuffStat.精神注入, Integer.valueOf(ret.x)));
                    break;
                case 21100005:
                case 31121002:
                case 32101004:
                    ret.statups.add(new Pair(MapleBuffStat.连环吸血, Integer.valueOf(ret.x)));
                    break;
                case 21111001:
                    ret.statups.add(new Pair(MapleBuffStat.战神威势, Integer.valueOf(ret.x)));
                    break;
                case 23121004:
                    ret.statups.add(new Pair(MapleBuffStat.反制攻击, Integer.valueOf(ret.level)));
                    ret.statups.add(new Pair(MapleBuffStat.古老意志, Integer.valueOf(ret.level)));
                    break;
                case 1111007:
                case 1211009:
                case 1311007:
                case 51111005:
                    ret.monsterStatus.put(MonsterStatus.魔击无效, Integer.valueOf(1));
                    break;
                case 1220013:
                    ret.statups.add(new Pair(MapleBuffStat.祝福护甲, Integer.valueOf(ret.x + 1)));
                    break;
                case 1211011:
                    ret.statups.add(new Pair(MapleBuffStat.战斗命令, Integer.valueOf(ret.x)));
                    break;
                case 23111005:
                    ret.statups.add(new Pair(MapleBuffStat.双弩水盾, Integer.valueOf(ret.x)));
                    break;
                case 22131001:
                    ret.statups.add(new Pair(MapleBuffStat.魔法屏障, Integer.valueOf(ret.x)));
                    break;
                case 22181003:
                    ret.statups.add(new Pair(MapleBuffStat.灵魂之石, Integer.valueOf(1)));
                    break;
                case 32121003:
                    ret.statups.add(new Pair(MapleBuffStat.幻灵飓风, Integer.valueOf(ret.x)));
                    break;
                case 2311009:
                    ret.statups.add(new Pair(MapleBuffStat.神圣魔法盾, Integer.valueOf(ret.x)));
                    ret.cooldown = ret.y;
                    ret.hpR = (ret.z / 100.0D);
                    break;
                case 32111005:
                    ret.duration = 60000;
                    ret.statups.add(new Pair(MapleBuffStat.幻灵霸体, Integer.valueOf(ret.level)));
                    break;
                case 5921009:
                    ret.monsterStatus.put(MonsterStatus.心灵控制, Integer.valueOf(1));
                    break;
                case 4341003:
                    ret.monsterStatus.put(MonsterStatus.怪物炸弹, Integer.valueOf(ret.damage));
                    break;
                case 1201006:
                    ret.monsterStatus.put(MonsterStatus.物攻, Integer.valueOf(ret.x));
                    ret.monsterStatus.put(MonsterStatus.物防, Integer.valueOf(ret.x));
                    ret.monsterStatus.put(MonsterStatus.恐慌, Integer.valueOf(ret.z));
                    break;
                case 1111005:
                case 1111008:
                case 1121001:
                case 1211002:
                case 1321001:
                case 2211003:
                case 2221006:
                case 2311004:
                case 3101005:
                case 3120010:
                case 4201004:
                case 4221007:
                case 4331005:
                case 5301001:
                case 5310008:
                case 5311001:
                case 5311002:
                case 5801002:
                case 5801003:
                case 5811002:
                case 5821004:
                case 5821005:
                case 5821007:
                case 5901004:
                case 9001020:
                case 9101020:
                case 15101005:
                case 21110006:
                case 22131000:
                case 22141001:
                case 22151001:
                case 22181001:
                case 31101002:
                case 31111001:
                case 32101001:
                case 32111011:
                case 32121004:
                case 33101001:
                case 33101002:
                case 33111002:
                case 33121002:
                case 35101003:
                case 35111015:
                case 51111007:
                case 51121008:
                    ret.monsterStatus.put(MonsterStatus.眩晕, Integer.valueOf(1));
                    break;
                case 1111003:
                case 4321002:
                case 11111002:
                case 90001004:
                    ret.monsterStatus.put(MonsterStatus.恐慌, Integer.valueOf(ret.x));
                    break;
                case 4121003:
                case 33121005:
                    ret.monsterStatus.put(MonsterStatus.挑衅, Integer.valueOf(ret.x));
                    ret.monsterStatus.put(MonsterStatus.魔防, Integer.valueOf(ret.x));
                    ret.monsterStatus.put(MonsterStatus.物防, Integer.valueOf(ret.x));
                    break;
                case 31121003:
                    ret.monsterStatus.put(MonsterStatus.挑衅, Integer.valueOf(ret.w));
                    ret.monsterStatus.put(MonsterStatus.魔防, Integer.valueOf(ret.x));
                    ret.monsterStatus.put(MonsterStatus.物防, Integer.valueOf(ret.x));
                    ret.monsterStatus.put(MonsterStatus.魔攻, Integer.valueOf(ret.x));
                    ret.monsterStatus.put(MonsterStatus.物攻, Integer.valueOf(ret.x));
                    ret.monsterStatus.put(MonsterStatus.命中, Integer.valueOf(ret.x));
                    break;
                case 23121002:
                    ret.monsterStatus.put(MonsterStatus.物防, Integer.valueOf(-ret.x));
                    break;
                case 2121006:
                case 2201004:
                case 2211002:
                case 2211006:
                case 2221001:
                case 2221003:
                case 2221007:
                case 3211003:
                case 5911005:
                case 21120006:
                case 22121000:
                case 90001006:
                    ret.monsterStatus.put(MonsterStatus.结冰, Integer.valueOf(1));
                    ret.duration *= 2;
                    break;
                case 2101003:
                case 2201003:
                case 12101001:
                case 90001002:
                    ret.monsterStatus.put(MonsterStatus.速度, Integer.valueOf(ret.x));
                    break;
                case 5011002:
                    ret.monsterStatus.put(MonsterStatus.速度, Integer.valueOf(ret.z));
                    break;
                case 1121010:
                    ret.statups.add(new Pair(MapleBuffStat.葵花宝典, Integer.valueOf(ret.x * 100 + ret.mobCount)));
                    break;
                case 22161002:
                case 23111002:
                    ret.monsterStatus.put(MonsterStatus.鬼刻符, Integer.valueOf(ret.x));
                    break;
                case 90001003:
                    ret.monsterStatus.put(MonsterStatus.中毒, Integer.valueOf(1));
                    break;
                case 2311005:
                    ret.monsterStatus.put(MonsterStatus.巫毒, Integer.valueOf(1));
                    break;
                case 32111006:
                    ret.statups.add(new Pair(MapleBuffStat.幻灵重生, Integer.valueOf(1)));
                    break;
                case 35121003:
                    ret.duration = 2100000000;
                    ret.statups.add(new Pair(MapleBuffStat.召唤兽, Integer.valueOf(1)));
                    break;
                case 35111001:
                case 35111009:
                case 35111010:
                    ret.duration = 2100000000;
                    ret.statups.add(new Pair(MapleBuffStat.替身术, Integer.valueOf(1)));
                    break;
                case 3111002:
                case 3120012:
                case 3211002:
                case 3220012:
                case 4341006:
                case 5321003:
                case 5911001:
                case 5920002:
                case 13111004:
                case 33111003:
                    ret.statups.add(new Pair(MapleBuffStat.替身术, Integer.valueOf(1)));
                    break;
                case 3120006:
                    ret.statups.add(new Pair(MapleBuffStat.精神连接, Integer.valueOf(3111005)));
                    break;
                case 3220005:
                    ret.statups.add(new Pair(MapleBuffStat.精神连接, Integer.valueOf(3211005)));
                    break;
                case 2121005:
                case 3101007:
                case 3111005:
                case 3201007:
                case 3211005:
                case 23111008:
                case 23111009:
                case 23111010:
                case 33111005:
                case 35111002:
                    ret.statups.add(new Pair(MapleBuffStat.召唤兽, Integer.valueOf(1)));
                    ret.monsterStatus.put(MonsterStatus.眩晕, Integer.valueOf(1));
                    break;
                case 2221005:
                    ret.statups.add(new Pair(MapleBuffStat.召唤兽, Integer.valueOf(1)));
                    ret.monsterStatus.put(MonsterStatus.结冰, Integer.valueOf(1));
                    break;
                case 35111005:
                    ret.statups.add(new Pair(MapleBuffStat.召唤兽, Integer.valueOf(1)));
                    ret.monsterStatus.put(MonsterStatus.速度, Integer.valueOf(ret.x));
                    ret.monsterStatus.put(MonsterStatus.物防, Integer.valueOf(ret.y));
                    break;
                case 5711001:
                    ret.statups.add(new Pair(MapleBuffStat.召唤兽, Integer.valueOf(1)));
                    break;
                case 1321007:
                    ret.statups.add(new Pair(MapleBuffStat.灵魂助力, Integer.valueOf(ret.level)));
                    break;
                case 2321003:
                case 4111007:
                case 4211007:
                case 5321004:
                case 5911002:
                case 11001004:
                case 12001004:
                case 12111004:
                case 13001004:
                case 14001005:
                case 14111010:
                case 15001004:
                case 33101008:
                case 35111011:
                case 35121009:
                case 35121011:
                    ret.statups.add(new Pair(MapleBuffStat.召唤兽, Integer.valueOf(1)));
                    break;
                case 35121010:
                    ret.duration = 60000;
                    ret.statups.add(new Pair(MapleBuffStat.DAMAGE_BUFF, Integer.valueOf(ret.x)));
                    break;
                case 2311003:
                case 9001002:
                case 9101002:
                    ret.statups.add(new Pair(MapleBuffStat.神圣祈祷, Integer.valueOf(ret.x)));
                    break;
                case 80001034:
                case 80001035:
                case 80001036:
                    ret.statups.add(new Pair(MapleBuffStat.神圣拯救者的祝福, Integer.valueOf(1)));
                    break;
                case 2111004:
                case 2211004:
                case 12111002:
                case 90001005:
                    ret.monsterStatus.put(MonsterStatus.封印, Integer.valueOf(1));
                    break;
                case 4111003:
                case 14111001:
                    ret.monsterStatus.put(MonsterStatus.影网, Integer.valueOf(1));
                    break;
                case 4111009:
                case 14111007:
                    ret.statups.add(new Pair(MapleBuffStat.暗器伤人, Integer.valueOf(0)));
                    break;
                case 2121004:
                case 2221004:
                case 2321004:
                    ret.hpR = (ret.y / 100.0D);
                    ret.mpR = (ret.y / 100.0D);
                    ret.statups.add(new Pair(MapleBuffStat.终极无限, Integer.valueOf(ret.x)));
                    ret.statups.add(new Pair(MapleBuffStat.双刀荆棘, Integer.valueOf(ret.prop)));
                    break;
                case 22181004:
                    ret.statups.add(new Pair(MapleBuffStat.玛瑙的意志, Integer.valueOf(ret.x)));
                    ret.statups.add(new Pair(MapleBuffStat.双刀荆棘, Integer.valueOf(ret.prop)));
                    break;
                case 1121002:
                case 1221002:
                case 1321002:
                case 5321010:
                case 21121003:
                case 32111014:
                case 51121004:
                    ret.statups.add(new Pair(MapleBuffStat.双刀荆棘, Integer.valueOf(ret.prop)));
                    break;
                case 2121002:
                case 2221002:
                case 2321002:
                    ret.statups.add(new Pair(MapleBuffStat.魔法反击, Integer.valueOf(1)));
                    break;
                case 2321005:
                    ret.statups.clear();
                    ret.statups.add(new Pair(MapleBuffStat.进阶祝福, Integer.valueOf(ret.x)));
                    ret.statups.add(new Pair(MapleBuffStat.HP_BOOST, Integer.valueOf(ret.indieMhp)));
                    ret.statups.add(new Pair(MapleBuffStat.MP_BOOST, Integer.valueOf(ret.indieMmp)));
                    break;
                case 3121007:
                case 3221006:
                    ret.statups.add(new Pair(MapleBuffStat.幻影步, Integer.valueOf(ret.x)));
                    ret.monsterStatus.put(MonsterStatus.速度, Integer.valueOf(ret.x));
                    break;
                case 33111004:
                    ret.statups.add(new Pair(MapleBuffStat.致盲, Integer.valueOf(ret.x)));
                    ret.monsterStatus.put(MonsterStatus.命中, Integer.valueOf(ret.x));
                    break;
                case 33111007:
                    ret.statups.add(new Pair(MapleBuffStat.移动速度, Integer.valueOf(ret.z)));
                    ret.statups.add(new Pair(MapleBuffStat.ATTACK_BUFF, Integer.valueOf(ret.y)));
                    ret.statups.add(new Pair(MapleBuffStat.暴走形态, Integer.valueOf(ret.x)));
                    break;
                case 2301004:
                case 9001003:
                case 9101003:
                    ret.statups.add(new Pair(MapleBuffStat.牧师祝福, Integer.valueOf(ret.level)));
                    break;
                case 32120000:
                    ret.dot = ret.damage;
                    ret.dotTime = 3;
                case 32001003:
                case 32110007:
                    ret.duration = (sourceid == 32110007 ? 60000 : 2100000000);
                    ret.statups.add(new Pair(MapleBuffStat.幻灵灵气, Integer.valueOf(ret.level)));
                    ret.statups.add(new Pair(MapleBuffStat.黑暗灵气, Integer.valueOf(ret.x)));
                    break;
                case 32110000:
                case 32110008:
                case 32111012:
                    ret.duration = (sourceid == 32110008 ? 60000 : 2100000000);
                    ret.statups.add(new Pair(MapleBuffStat.幻灵灵气, Integer.valueOf(ret.level)));
                    ret.statups.add(new Pair(MapleBuffStat.蓝色灵气, Integer.valueOf(ret.level)));
                    break;
                case 32120001:
                    ret.monsterStatus.put(MonsterStatus.速度, Integer.valueOf(ret.speed));
                case 32101003:
                case 32110009:
                    ret.duration = (sourceid == 32110009 ? 60000 : 2100000000);
                    ret.statups.add(new Pair(MapleBuffStat.幻灵灵气, Integer.valueOf(ret.level)));
                    ret.statups.add(new Pair(MapleBuffStat.黄色灵气, Integer.valueOf(ret.level)));
                    break;
                case 33101004:
                    ret.statups.add(new Pair(MapleBuffStat.地雷, Integer.valueOf(ret.x)));
                    break;
                case 35101007:
                    ret.duration = 2100000000;
                    ret.statups.add(new Pair(MapleBuffStat.完美机甲, Integer.valueOf(ret.x)));
                    break;
                case 31101003:
                    ret.statups.add(new Pair(MapleBuffStat.伤害反击, Integer.valueOf(ret.y)));
                    break;
                case 31111004:
                    ret.statups.add(new Pair(MapleBuffStat.异常抗性, Integer.valueOf(ret.y)));
                    ret.statups.add(new Pair(MapleBuffStat.属性抗性, Integer.valueOf(ret.z)));
                    ret.statups.add(new Pair(MapleBuffStat.黑暗忍耐, Integer.valueOf(ret.x)));
                    break;
                case 31121005:
                    ret.statups.add(new Pair(MapleBuffStat.反制攻击, Integer.valueOf(ret.level)));
                    ret.statups.add(new Pair(MapleBuffStat.百分比MaxHp, Integer.valueOf(ret.indieMhpR)));
                    break;
                case 31121007:
                    ret.statups.add(new Pair(MapleBuffStat.无限精气, Integer.valueOf(1)));
                    break;
                case 35121006:
                    ret.duration = 2100000000;
                    ret.statups.add(new Pair(MapleBuffStat.卫星防护_PROC, Integer.valueOf(ret.x)));
                    ret.statups.add(new Pair(MapleBuffStat.卫星防护_吸收, Integer.valueOf(ret.y)));
                    break;
                case 20021110:
                case 20031203:
                case 80001040:
                    ret.moveTo = ret.x;
                    break;
                case 80001089:
                    ret.duration = 2100000000;
                    ret.statups.add(new Pair(MapleBuffStat.飞行骑乘, Integer.valueOf(1)));
                    break;
                case 35001001:
                case 35101009:
                    ret.duration = 1000;
                    ret.statups.add(new Pair(MapleBuffStat.金属机甲, Integer.valueOf(level)));
                    break;
                case 35111004:
                case 35121013:
                    ret.duration = 2100000000;
                    ret.statups.add(new Pair(MapleBuffStat.金属机甲, Integer.valueOf(level)));
                    break;
                case 35121005:
                    ret.duration = 2100000000;
                    ret.statups.add(new Pair(MapleBuffStat.金属机甲, Integer.valueOf(level)));
                    break;
                case 10001075:
                    ret.statups.add(new Pair(MapleBuffStat.英雄回声, Integer.valueOf(ret.x)));
                    break;
                case 5721009:
                    ret.statups.add(new Pair(MapleBuffStat.双刀荆棘, Integer.valueOf(ret.x)));
                    ret.statups.add(new Pair(MapleBuffStat.反制攻击, Integer.valueOf(ret.damR)));
                    ret.statups.add(new Pair(MapleBuffStat.异常抗性, Integer.valueOf(ret.x)));
                    ret.statups.add(new Pair(MapleBuffStat.属性抗性, Integer.valueOf(ret.x)));
                    break;
                case 20031205:
                    ret.statups.add(new Pair(MapleBuffStat.幻影屏障, Integer.valueOf(ret.x)));
                    break;
                case 24111003:
                    ret.statups.add(new Pair(MapleBuffStat.异常抗性, Integer.valueOf(ret.x)));
                    ret.statups.add(new Pair(MapleBuffStat.属性抗性, Integer.valueOf(ret.x)));
                    ret.statups.add(new Pair(MapleBuffStat.百分比MaxHp, Integer.valueOf(ret.indieMhpR)));
                    ret.statups.add(new Pair(MapleBuffStat.百分比MaxMp, Integer.valueOf(ret.indieMmpR)));
                    break;
                case 24121004:
                    ret.statups.add(new Pair(MapleBuffStat.反制攻击, Integer.valueOf(ret.damR)));
                    ret.statups.add(new Pair(MapleBuffStat.百分比无视防御, Integer.valueOf(ret.damR)));
                    break;
                case 24111002:
                    break;
            }

            if (GameConstants.is新手职业(sourceid / 10000)) {
                switch (sourceid % 10000) {
                    case 1087:
                        break;
                    case 1085:
                    case 1090:
                        break;
                    case 1179:
                        break;
                    case 1105:
                        ret.statups.add(new Pair(MapleBuffStat.ICE_SKILL, Integer.valueOf(1)));
                        ret.duration = 2100000000;
                        break;
                    case 93:
                        ret.statups.add(new Pair(MapleBuffStat.潜力解放, Integer.valueOf(1)));
                        break;
                    case 8001:
                        ret.statups.add(new Pair(MapleBuffStat.无形箭弩, Integer.valueOf(ret.x)));
                        break;
                    case 1005:
                        ret.statups.add(new Pair(MapleBuffStat.英雄回声, Integer.valueOf(ret.x)));
                        break;
                    case 1011:
                        ret.statups.add(new Pair(MapleBuffStat.狂暴战魂, Integer.valueOf(ret.x)));
                        break;
                    case 1010:
                        ret.statups.add(new Pair(MapleBuffStat.金刚霸体, Integer.valueOf(1)));
                        break;
                    case 1001:
                        if ((sourceid / 10000 == 3001) || (sourceid / 10000 == 3000)) {
                            ret.statups.add(new Pair(MapleBuffStat.潜入, Integer.valueOf(ret.x)));
                        } else {
                            ret.statups.add(new Pair(MapleBuffStat.RECOVERY, Integer.valueOf(ret.x)));
                        }
                        break;
                    case 8003:
                        ret.statups.add(new Pair(MapleBuffStat.MAXHP, Integer.valueOf(ret.x)));
                        ret.statups.add(new Pair(MapleBuffStat.MAXMP, Integer.valueOf(ret.x)));
                        break;
                    case 8004:
                        ret.statups.add(new Pair(MapleBuffStat.战斗命令, Integer.valueOf(ret.x)));
                        break;
                    case 8005:
                        ret.statups.add(new Pair(MapleBuffStat.进阶祝福, Integer.valueOf(1)));
                        break;
                    case 8006:
                        ret.statups.add(new Pair(MapleBuffStat.极速领域, Integer.valueOf(ret.x)));
                        break;
                    case 103:
                        ret.monsterStatus.put(MonsterStatus.眩晕, Integer.valueOf(1));
                        break;
                    case 99:
                    case 104:
                        ret.monsterStatus.put(MonsterStatus.结冰, Integer.valueOf(1));
                        ret.duration *= 2;
                        break;
                    case 8002:
                        ret.statups.add(new Pair(MapleBuffStat.火眼晶晶, Integer.valueOf((ret.x << 8) + ret.criticaldamageMax)));
                        break;
                    case 1026:
                    case 1142:
                        ret.duration = 2100000000;
                        ret.statups.add(new Pair(MapleBuffStat.飞行骑乘, Integer.valueOf(1)));
                }
            }
        } else {
            switch (sourceid) {
                case 2022746:
                case 2022747:
                case 2022823:
                    ret.statups.clear();
                    ret.statups.add(new Pair(MapleBuffStat.PYRAMID_PQ, Integer.valueOf(1)));
            }
        }

        if (ret.isPoison()) {
            ret.monsterStatus.put(MonsterStatus.中毒, Integer.valueOf(1));
        }
        if ((ret.isMorph()) || (ret.isPirateMorph())) {
            ret.statups.add(new Pair(MapleBuffStat.变身术, Integer.valueOf(ret.getMorph())));
        }
        ret.statups.trimToSize();
        return ret;
    }

    public void applyPassive(MapleCharacter applyto, MapleMapObject obj) {
        if (makeChanceResult()) {
            switch (this.sourceid) {
                case 2100000:
                case 2200000:
                case 2300000:
                    if ((obj == null) || (obj.getType() != MapleMapObjectType.MONSTER)) {
                        return;
                    }
                    MapleMonster mob = (MapleMonster) obj;
                    if (mob.getStats().isBoss()) {
                        break;
                    }
                    int absorbMp = Math.min((int) (mob.getMobMaxMp() * (getX() / 100.0D)), mob.getMp());
                    if (absorbMp <= 0) {
                        break;
                    }
                    mob.setMp(mob.getMp() - absorbMp);
                    applyto.getStat().setMp(applyto.getStat().getMp() + absorbMp, applyto);
                    applyto.getClient().getSession().write(MaplePacketCreator.showOwnBuffEffect(this.sourceid, 1, applyto.getLevel(), this.level));
                    applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.showBuffeffect(applyto.getId(), this.sourceid, 1, applyto.getLevel(), this.level), false);
            }
        }
    }

    public boolean applyTo(MapleCharacter chr) {
        return applyTo(chr, chr, true, null, this.duration);
    }

    public boolean applyTo(MapleCharacter chr, Point pos) {
        return applyTo(chr, chr, true, pos, this.duration);
    }

    public boolean applyTo(MapleCharacter applyfrom, MapleCharacter applyto, boolean primary, Point pos, int newDuration) {
        if ((!applyfrom.isAdmin()) && (applyfrom.getMap().isMarketMap())) {
            applyfrom.getClient().getSession().write(MaplePacketCreator.enableActions());
            return false;
        }
        if ((is群体治愈()) && ((applyfrom.getMapId() == 749040100) || (applyto.getMapId() == 749040100))) {
            applyfrom.getClient().getSession().write(MaplePacketCreator.enableActions());
            return false;
        }
        if (((isSoaring_Mount()) && (applyfrom.getBuffedValue(MapleBuffStat.骑兽技能) == null)) || ((isSoaring_Normal()) && (!applyfrom.getMap().canSoar()))) {
            applyfrom.getClient().getSession().write(MaplePacketCreator.enableActions());
            return false;
        }
        if ((this.sourceid == 4341006) && (applyfrom.getBuffedValue(MapleBuffStat.影分身) == null)) {
            applyfrom.getClient().getSession().write(MaplePacketCreator.enableActions());
            return false;
        }
        if ((this.sourceid == 33101008) && ((applyfrom.getBuffedValue(MapleBuffStat.地雷) == null) || (applyfrom.getBuffedValue(MapleBuffStat.召唤兽) != null) || (!applyfrom.canSummon()))) {
            applyfrom.getClient().getSession().write(MaplePacketCreator.enableActions());
            return false;
        }
        if ((is影分身()) && (applyfrom.getJob() / 100 % 10 != 4)) {
            applyfrom.getClient().getSession().write(MaplePacketCreator.enableActions());
            return false;
        }
        if ((this.sourceid == 33101004) && (applyfrom.getMap().isTown())) {
            applyfrom.dropMessage(5, "在城市中无法使用这个技能.");
            applyfrom.getClient().getSession().write(MaplePacketCreator.enableActions());
            return false;
        }
        int hpchange = calcHPChange(applyfrom, primary);
        int mpchange = calcMPChange(applyfrom, primary);
        PlayerStats stat = applyto.getStat();
        if (primary) {
            if ((this.itemConNo != 0) && (!applyto.isClone()) && (!applyto.inPVP())) {
                if (!applyto.haveItem(this.itemCon, this.itemConNo, false, true)) {
                    applyto.getClient().getSession().write(MaplePacketCreator.enableActions());
                    return false;
                }
                MapleInventoryManipulator.removeById(applyto.getClient(), GameConstants.getInventoryType(this.itemCon), this.itemCon, this.itemConNo, false, true);
            }
        } else if ((!primary) && (is复活术())) {
            hpchange = stat.getMaxHp();
            applyto.setStance(0);
        }
        if ((is净化()) && (makeChanceResult())) {
            applyto.dispelDebuffs();
        } else if (is勇士的意志()) {
            applyto.dispelDebuffs();
        } else if (this.cureDebuffs.size() > 0) {
            for (MapleDisease debuff : this.cureDebuffs) {
                applyfrom.dispelDebuff(debuff);
            }
        } else if (is生命分流()) {
            int toDecreaseHP = stat.getMaxHp() / 100 * 10;
            if (stat.getHp() > toDecreaseHP) {
                hpchange += -toDecreaseHP;
                mpchange += toDecreaseHP / 100 * getY();
            } else {
                hpchange = stat.getHp() == 1 ? 0 : stat.getHp() - 1;
            }
        }
        Map hpmpupdate = new EnumMap(MapleStat.class);
        if (hpchange != 0) {
            if ((hpchange < 0) && (-hpchange > stat.getHp()) && (!applyto.hasDisease(MapleDisease.ZOMBIFY))) {
                applyto.getClient().getSession().write(MaplePacketCreator.enableActions());
                return false;
            }
            stat.setHp(stat.getHp() + hpchange, applyto);
        }
        if (mpchange != 0) {
            if ((mpchange < 0) && (-mpchange > stat.getMp())) {
                applyto.getClient().getSession().write(MaplePacketCreator.enableActions());
                return false;
            }

            stat.setMp(stat.getMp() + mpchange, applyto);
            hpmpupdate.put(MapleStat.MP, Integer.valueOf(stat.getMp()));
        }
        hpmpupdate.put(MapleStat.HP, Integer.valueOf(stat.getHp()));
        applyto.getClient().getSession().write(MaplePacketCreator.updatePlayerStats(hpmpupdate, true, applyto));
        if (this.expinc != 0) {
            applyto.gainExp(this.expinc, true, true, false);
            applyto.getClient().getSession().write(MaplePacketCreator.showSpecialEffect(21));
        } else if (this.sourceid / 10000 == 238) {
            if (GameConstants.GMS) {
                MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                int mobid = ii.getCardMobId(this.sourceid);
                if (mobid > 0) {
                    boolean done = applyto.getMonsterBook().monsterCaught(applyto.getClient(), mobid, MapleLifeFactory.getMonsterStats(mobid).getName());
                    applyto.getClient().getSession().write(MaplePacketCreator.getCard(done ? this.sourceid : 0, 1));
                }
            }
        } else if (isReturnScroll()) {
            applyReturnScroll(applyto);
        } else if ((this.useLevel > 0) && (!this.skill)) {
            applyto.setExtractor(new MapleExtractor(applyto, this.sourceid, this.useLevel * 50, 1440));
            applyto.getMap().spawnExtractor(applyto.getExtractor());
        } else {
            
            if (is迷雾爆发()) {
                int i = this.y;
                for (MapleMist m : applyto.getMap().getAllMistsThreadsafe()) {
                    if ((m.getOwnerId() == applyto.getId()) && (m.getSourceSkill().getId() == 2111003)) {
                        if (m.getSchedule() != null) {
                            m.getSchedule().cancel(false);
                            m.setSchedule(null);
                        }
                        if (m.getPoisonSchedule() != null) {
                            m.getPoisonSchedule().cancel(false);
                            m.setPoisonSchedule(null);
                        }
                        applyto.getMap().broadcastMessage(MaplePacketCreator.removeMist(m.getObjectId(), true));
                        applyto.getMap().removeMapObject(m);
                        i--;
                        if (i <= 0) {
                            break;
                        }
                    }
                }
            } else if (this.cosmetic > 0) {
                if (this.cosmetic >= 30000) {
                    applyto.setHair(this.cosmetic);
                    applyto.updateSingleStat(MapleStat.发型, this.cosmetic);
                } else if (this.cosmetic >= 20000) {
                    applyto.setFace(this.cosmetic);
                    applyto.updateSingleStat(MapleStat.脸型, this.cosmetic);
                } else if (this.cosmetic < 100) {
                    applyto.setSkinColor((byte) this.cosmetic);
                    applyto.updateSingleStat(MapleStat.皮肤, this.cosmetic);
                }
                applyto.equipChanged();
            } else if (this.bs > 0) {
                if (!applyto.inPVP()) {
                    return false;
                }
                int xx = Integer.parseInt(applyto.getEventInstance().getProperty(String.valueOf(applyto.getId())));
                applyto.getEventInstance().setProperty(String.valueOf(applyto.getId()), String.valueOf(xx + this.bs));
                applyto.getClient().getSession().write(MaplePacketCreator.getPVPScore(xx + this.bs, false));
            } else if (this.iceGageCon > 0) {
                if (!applyto.inPVP()) {
                    return false;
                }
                int xx = Integer.parseInt(applyto.getEventInstance().getProperty("icegage"));
                if (xx < this.iceGageCon) {
                    return false;
                }
                applyto.getEventInstance().setProperty("icegage", String.valueOf(xx - this.iceGageCon));
                applyto.getClient().getSession().write(MaplePacketCreator.getPVPIceGage(xx - this.iceGageCon));
                applyto.applyIceGage(xx - this.iceGageCon);
            } else if (this.recipe > 0) {
                if ((applyto.getSkillLevel(this.recipe) > 0) || (applyto.getProfessionLevel(this.recipe / 10000 * 10000) < this.reqSkillLevel)) {
                    return false;
                }
                applyto.changeSkillLevel(SkillFactory.getCraft(this.recipe), 2147483647, this.recipeUseCount, this.recipeValidDay > 0 ? System.currentTimeMillis() + this.recipeValidDay * 24L * 60L * 60L * 1000L : -1L);
            } else if (is斗气重生()) {
                applyto.setCombo((short) Math.min(30000, applyto.getCombo() + this.y));
                applyto.setLastCombo(System.currentTimeMillis());
                applyto.getClient().getSession().write(MaplePacketCreator.rechargeCombo(applyto.getCombo()));
                SkillFactory.getSkill(21000000).getEffect(10).applyComboBuff(applyto, applyto.getCombo());
            } else if (is飞龙传动()) {
                MaplePortal portal = applyto.getMap().getPortal(Randomizer.nextInt(applyto.getMap().getPortals().size()));
                if (portal != null) {
                    applyto.getClient().getSession().write(MaplePacketCreator.dragonBlink(portal.getId()));
                    applyto.getMap().movePlayer(applyto, portal.getPosition());
                    applyto.checkFollow();
                }
            } else if ((is暗器伤人()) && (!applyto.isClone())) {
                MapleInventory use = applyto.getInventory(MapleInventoryType.USE);
                boolean itemz = false;
                for (int i = 0; i < use.getSlotLimit(); i++) {
                    Item item = use.getItem((short) (byte) i);
                    if ((item == null)
                            || (!GameConstants.isThrowingStar(item.getItemId())) || (item.getQuantity() < 200)) {
                        continue;
                    }
                    MapleInventoryManipulator.removeFromSlot(applyto.getClient(), MapleInventoryType.USE, (short) i, (short)200, false, true);
                    itemz = true;
                    break;
                }

                if (!itemz) {
                    return false;
                }
            } else if ((this.cp != 0) && (applyto.getCarnivalParty() != null)) {
                applyto.getCarnivalParty().addCP(applyto, this.cp);
                applyto.CPUpdate(false, applyto.getAvailableCP(), applyto.getTotalCP(), 0);
                for (MapleCharacter chr : applyto.getMap().getCharactersThreadsafe()) {
                    chr.CPUpdate(true, applyto.getCarnivalParty().getAvailableCP(), applyto.getCarnivalParty().getTotalCP(), applyto.getCarnivalParty().getTeam());
                }
            } else {
                MapleCarnivalFactory.MCSkill skil;
                MapleDisease dis;
                if ((this.nuffSkill != 0) && (applyto.getParty() != null)) {
                    skil = MapleCarnivalFactory.getInstance().getSkill(this.nuffSkill);
                    if (skil != null) {
                        dis = skil.getDisease();
                        for (MapleCharacter chr : applyto.getMap().getCharactersThreadsafe()) {
                            if (((applyto.getParty() == null) || (chr.getParty() == null) || (chr.getParty().getId() != applyto.getParty().getId())) && ((skil.targetsAll) || (Randomizer.nextBoolean()))) {
                                if (dis == null) {
                                    chr.dispel();
                                } else if (skil.getSkill() == null) {
                                    chr.giveDebuff(dis, 1, 30000L, dis.getDisease(), 1);
                                } else {
                                    chr.giveDebuff(dis, skil.getSkill());
                                }
                                if (!skil.targetsAll) {
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    if (((this.effectedOnEnemy > 0) || (this.effectedOnAlly > 0)) && (primary) && (applyto.inPVP())) {
                        int types = Integer.parseInt(applyto.getEventInstance().getProperty("type"));
                        if ((types > 0) || (this.effectedOnEnemy > 0)) {
                            for (MapleCharacter chr : applyto.getMap().getCharactersThreadsafe()) {
                                if ((chr.getId() != applyto.getId()) && (this.effectedOnAlly > 0 ? chr.getTeam() != applyto.getTeam() : (chr.getTeam() != applyto.getTeam()) || (types == 0))) {
                                    applyTo(applyto, chr, false, pos, newDuration);
                                }
                            }
                        }
                    } else if ((this.mobSkill > 0) && (this.mobSkillLevel > 0) && (primary) && (applyto.inPVP())) {
                        
                        if (this.effectedOnEnemy > 0) {
                            int types = Integer.parseInt(applyto.getEventInstance().getProperty("type"));
                            for (MapleCharacter chr : applyto.getMap().getCharactersThreadsafe()) {
                                if ((chr.getId() != applyto.getId()) && ((chr.getTeam() != applyto.getTeam()) || (types == 0))) {
                                    chr.disease(this.mobSkill, this.mobSkillLevel);
                                }
                            }
                        } else if ((this.sourceid == 2910000) || (this.sourceid == 2910001)) {
                            applyto.getClient().getSession().write(MaplePacketCreator.showOwnBuffEffect(this.sourceid, 13, applyto.getLevel(), this.level));
                            applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.showBuffeffect(applyto.getId(), this.sourceid, 13, applyto.getLevel(), this.level), false);
                            applyto.getClient().getSession().write(MaplePacketCreator.showOwnCraftingEffect("UI/UIWindow2.img/CTF/Effect", 0, 0));
                            applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.showCraftingEffect(applyto.getId(), "UI/UIWindow2.img/CTF/Effect", 0, 0), false);
                            if (applyto.getTeam() == this.sourceid - 2910000) {
                                if (this.sourceid == 2910000) {
                                    applyto.getEventInstance().broadcastPlayerMsg(-7, "The Red Team's flag has been restored.");
                                } else {
                                    applyto.getEventInstance().broadcastPlayerMsg(-7, "The Blue Team's flag has been restored.");
                                }
                                applyto.getMap().spawnAutoDrop(this.sourceid, (Point) ((Pair) applyto.getMap().getGuardians().get(this.sourceid - 2910000)).left);
                            } else {
                                applyto.disease(this.mobSkill, this.mobSkillLevel);
                                if (this.sourceid == 2910000) {
                                    applyto.getEventInstance().setProperty("redflag", String.valueOf(applyto.getId()));
                                    applyto.getEventInstance().broadcastPlayerMsg(-7, "The Red Team's flag has been captured!");
                                    applyto.getClient().getSession().write(MaplePacketCreator.showOwnCraftingEffect("UI/UIWindow2.img/CTF/Tail/Red", 600000, 0));
                                    applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.showCraftingEffect(applyto.getId(), "UI/UIWindow2.img/CTF/Tail/Red", 600000, 0), false);
                                } else {
                                    applyto.getEventInstance().setProperty("blueflag", String.valueOf(applyto.getId()));
                                    applyto.getEventInstance().broadcastPlayerMsg(-7, "The Blue Team's flag has been captured!");
                                    applyto.getClient().getSession().write(MaplePacketCreator.showOwnCraftingEffect("UI/UIWindow2.img/CTF/Tail/Blue", 600000, 0));
                                    applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.showCraftingEffect(applyto.getId(), "UI/UIWindow2.img/CTF/Tail/Blue", 600000, 0), false);
                                }
                            }
                        } else {
                            applyto.disease(this.mobSkill, this.mobSkillLevel);
                        }
                    } else if ((this.randomPickup != null) && (this.randomPickup.size() > 0)) {
                        MapleItemInformationProvider.getInstance().getItemEffect(((Integer) this.randomPickup.get(Randomizer.nextInt(this.randomPickup.size()))).intValue()).applyTo(applyto);
                    }
                }
            }
        }
        for (Map.Entry traitType : this.traits.entrySet()) {
            applyto.getTrait((MapleTrait.MapleTraitType) traitType.getKey()).addExp(((Integer) traitType.getValue()).intValue(), applyto);
        }
        SummonMovementType summonMovementType = getSummonMovementType();
        if ((summonMovementType != null) && ((this.sourceid != 32111006) || ((applyfrom.getBuffedValue(MapleBuffStat.幻灵重生) != null) && (!primary))) && (!applyto.isClone())) {
            int summId = this.sourceid;
            if (ServerProperties.ShowPacket()) {
                log.info("开始召唤召唤兽 - 召唤兽ID: 1 - " + summId);
            }
            if (this.sourceid == 3111002) {
                Skill elite = SkillFactory.getSkill(3120012);
                if (applyfrom.getTotalSkillLevel(elite) > 0) {
                    return elite.getEffect(applyfrom.getTotalSkillLevel(elite)).applyTo(applyfrom, applyto, primary, pos, newDuration);
                }
            } else if (this.sourceid == 3211002) {
                Skill elite = SkillFactory.getSkill(3220012);
                if (applyfrom.getTotalSkillLevel(elite) > 0) {
                    return elite.getEffect(applyfrom.getTotalSkillLevel(elite)).applyTo(applyfrom, applyto, primary, pos, newDuration);
                }
            }
            MapleSummon tosummon = new MapleSummon(applyfrom, summId, getLevel(), new Point(pos == null ? applyfrom.getTruePosition() : pos), summonMovementType);
            if (!tosummon.isPuppet()) {
                applyfrom.getCheatTracker().resetSummonAttack();
            }
            applyfrom.cancelEffect(this, true, -1L, this.statups);
            applyfrom.getMap().spawnSummon(tosummon);
            applyfrom.addSummon(tosummon);
            tosummon.addHP((short) this.x);
            if (is灵魂助力()) {
                tosummon.addHP((short)1);
            } else if (this.sourceid == 4341006) {
                applyfrom.cancelEffectFromBuffStat(MapleBuffStat.影分身);
            } else {
                if (this.sourceid == 32111006) {
                    return true;
                }
                if (this.sourceid == 35111002) {
                    List count = new ArrayList();
                    List<MapleSummon> ss = applyfrom.getSummonsReadLock();
                    try {
                        for (MapleSummon s : ss) {
                            if (s.getSkill() == this.sourceid) {
                                count.add(Integer.valueOf(s.getObjectId()));
                            }
                        }
                    } finally {
                        applyfrom.unlockSummonsReadLock();
                    }
                    if (count.size() != 3) {
                        return true;
                    }
                    applyfrom.getClient().getSession().write(MaplePacketCreator.skillCooldown(this.sourceid, getCooldown()));
                    applyfrom.addCooldown(this.sourceid, System.currentTimeMillis(), getCooldown() * 1000);
                    applyfrom.getMap().broadcastMessage(MaplePacketCreator.teslaTriangle(applyfrom.getId(), ((Integer) count.get(0)).intValue(), ((Integer) count.get(1)).intValue(), ((Integer) count.get(2)).intValue()));
                } else if (this.sourceid == 35121003) {
                    applyfrom.getClient().getSession().write(MaplePacketCreator.enableActions());
                }
            }
        } else if (is机械传送门()) {
            int newId = 0;
            boolean applyBuff = false;
            if (applyto.getMechDoors().size() >= 2) {
                MechDoor remove = (MechDoor) applyto.getMechDoors().remove(0);
                newId = remove.getId();
                applyto.getMap().broadcastMessage(MaplePacketCreator.removeMechDoor(remove, true));
                applyto.getMap().removeMapObject(remove);
            } else {
                for (MechDoor d : applyto.getMechDoors()) {
                    if (d.getId() == newId) {
                        applyBuff = true;
                        newId = 1;
                        break;
                    }
                }
            }
            MechDoor door = new MechDoor(applyto, new Point(pos == null ? applyto.getTruePosition() : pos), newId);
            applyto.getMap().spawnMechDoor(door);
            applyto.addMechDoor(door);
            applyto.getClient().getSession().write(MaplePacketCreator.mechPortal(door.getTruePosition()));
            if (!applyBuff) {
                return true;
            }
        }
        if ((primary) && (this.availableMap != null)) {
            for (Pair e : this.availableMap) {
                if ((applyto.getMapId() < ((Integer) e.left).intValue()) || (applyto.getMapId() > ((Integer) e.right).intValue())) {
                    applyto.getClient().getSession().write(MaplePacketCreator.enableActions());
                    return true;
                }
            }
        }
        if ((this.overTime) && (!is能量获得())) {
            applyBuffEffect(applyfrom, applyto, primary, newDuration);
        }
        if (this.skill) {
            removeMonsterBuff(applyfrom);
        }
        if (primary) {
            if (((this.overTime) || (is群体治愈())) && (!is能量获得())) {
                applyBuff(applyfrom, newDuration);
            }
            if (isMonsterBuff()) {
                applyMonsterBuff(applyfrom);
            }
        }
        if (is时空门()) {
            MapleDoor door = new MapleDoor(applyto, new Point(pos == null ? applyto.getTruePosition() : pos), this.sourceid);
            if (door.getTownPortal() != null) {
                applyto.getMap().spawnDoor(door);
                applyto.addDoor(door);
                MapleDoor townDoor = new MapleDoor(door);
                applyto.addDoor(townDoor);
                door.getTown().spawnDoor(townDoor);
                if (applyto.getParty() != null) {
                    applyto.silentPartyUpdate();
                }
            } else {
                applyto.dropMessage(5, "You may not spawn a door because all doors in the town are taken.");
            }
        } else if (isMist()) {
            Rectangle bounds = calculateBoundingBox(pos != null ? pos : applyfrom.getPosition(), applyfrom.isFacingLeft());
            MapleMist mist = new MapleMist(bounds, applyfrom, this);
            if (getCooldown() > 0) {
                applyfrom.getClient().getSession().write(MaplePacketCreator.skillCooldown(this.sourceid, getCooldown()));
                applyfrom.addCooldown(this.sourceid, System.currentTimeMillis(), getCooldown() * 1000);
            }
            applyfrom.getMap().spawnMist(mist, getDuration(), false);
        } else if (is伺机待发()) {
            for (MapleCoolDownValueHolder i : applyto.getCooldowns()) {
                if (i.skillId != 5821010) {
                    applyto.removeCooldown(i.skillId);
                    applyto.getClient().getSession().write(MaplePacketCreator.skillCooldown(i.skillId, 0));
                }
            }
        } else {
            for (WeakReference chrz : applyto.getClones()) {
                if (chrz.get() != null) {
                    applyTo((MapleCharacter) chrz.get(), (MapleCharacter) chrz.get(), primary, pos, newDuration);
                }
            }
        }
        if ((this.fatigueChange != 0) && (applyto.getSummonedFamiliar() != null) && ((this.familiars == null) || (this.familiars.contains(Integer.valueOf(applyto.getSummonedFamiliar().getFamiliar()))))) {
            applyto.getSummonedFamiliar().addFatigue(applyto, this.fatigueChange);
        }
        if (this.rewardMeso != 0) {
            applyto.gainMeso(this.rewardMeso, false);
        }
        if ((this.rewardItem != null) && (this.totalprob > 0)) {
            for (Triple reward : this.rewardItem) {
                if ((MapleInventoryManipulator.checkSpace(applyto.getClient(), ((Integer) reward.left).intValue(), ((Integer) reward.mid).intValue(), "")) && (((Integer) reward.right).intValue() > 0) && (Randomizer.nextInt(this.totalprob) < ((Integer) reward.right).intValue())) {
                    if (GameConstants.getInventoryType(((Integer) reward.left).intValue()) == MapleInventoryType.EQUIP) {
                        Item item = MapleItemInformationProvider.getInstance().getEquipById(((Integer) reward.left).intValue());
                        item.setGMLog("Reward item (effect): " + this.sourceid + " on " + FileoutputUtil.CurrentReadable_Date());
                        MapleInventoryManipulator.addbyItem(applyto.getClient(), item);
                    } else {
                        MapleInventoryManipulator.addById(applyto.getClient(), ((Integer) reward.left).intValue(), ((Integer) reward.mid).shortValue(), "Reward item (effect): " + this.sourceid + " on " + FileoutputUtil.CurrentReadable_Date());
                    }
                }
            }
        }
        if ((this.familiarTarget == 2) && (applyfrom.getParty() != null) && (primary)) {
            for (MaplePartyCharacter mpc : applyfrom.getParty().getMembers()) {
                if ((mpc.getId() != applyfrom.getId()) && (mpc.getChannel() == applyfrom.getClient().getChannel()) && (mpc.getMapid() == applyfrom.getMapId()) && (mpc.isOnline())) {
                    MapleCharacter mc = applyfrom.getMap().getCharacterById(mpc.getId());
                    if (mc != null) {
                        applyTo(applyfrom, mc, false, null, newDuration);
                    }
                }
            }
        } else if ((this.familiarTarget == 3) && (primary)) {
            for (MapleCharacter mc : applyfrom.getMap().getCharactersThreadsafe()) {
                if (mc.getId() != applyfrom.getId()) {
                    applyTo(applyfrom, mc, false, null, newDuration);
                }
            }
        }
        return true;
    }

    public boolean applyReturnScroll(MapleCharacter applyto) {
        if (this.moveTo != -1) {
            if ((this.sourceid != 2031010) || (this.sourceid != 2030021)) {
                MapleMap target = null;
                boolean nearest = false;
                if (this.moveTo == 999999999) {
                    nearest = true;
                    if (applyto.getMap().getReturnMapId() != 999999999) {
                        target = applyto.getMap().getReturnMap();
                    }
                } else {
                    target = ChannelServer.getInstance(applyto.getClient().getChannel()).getMapFactory().getMap(this.moveTo);
                    int targetMapId = target.getId() / 10000000;
                    int charMapId = applyto.getMapId() / 10000000;
                    if ((targetMapId != 60) && (charMapId != 61)
                            && (targetMapId != 21) && (charMapId != 20)
                            && (targetMapId != 12) && (charMapId != 10)
                            && (targetMapId != 10) && (charMapId != 12)
                            && (targetMapId != charMapId)) {
                        log.info("玩家 " + applyto.getName() + " 尝试回到一个非法的位置 (" + applyto.getMapId() + "->" + target.getId() + ")");
                        return false;
                    }

                }

                if ((target == applyto.getMap()) || ((nearest) && (applyto.getMap().isTown()))) {
                    return false;
                }
                applyto.changeMap(target, target.getPortal(0));
                return true;
            }
        }
        return false;
    }

    private boolean is灵魂之石() {
        return (this.skill) && (this.sourceid == 22181003);
    }

    private void applyBuff(MapleCharacter applyfrom, int newDuration) {
        if (is灵魂之石()) {
            if (applyfrom.getParty() != null) {
                int membrs = 0;
                for (MapleCharacter chr : applyfrom.getMap().getCharactersThreadsafe()) {
                    if ((!chr.isClone()) && (chr.getParty() != null) && (chr.getParty().getId() == applyfrom.getParty().getId()) && (chr.isAlive())) {
                        membrs++;
                    }
                }
                List<MapleCharacter> awarded = new ArrayList<MapleCharacter>();
                while (awarded.size() < Math.min(membrs, this.y)) {
                    for (MapleCharacter chr : applyfrom.getMap().getCharactersThreadsafe()) {
                        if ((chr != null) && (!chr.isClone()) && (chr.isAlive()) && (chr.getParty() != null) && (chr.getParty().getId() == applyfrom.getParty().getId()) && (!awarded.contains(chr)) && (Randomizer.nextInt(this.y) == 0)) {
                            awarded.add(chr);
                        }
                    }
                }
                for (MapleCharacter chr : awarded) {
                    applyTo(applyfrom, chr, false, null, newDuration);
                    chr.getClient().getSession().write(MaplePacketCreator.showOwnBuffEffect(this.sourceid, 2, applyfrom.getLevel(), this.level));
                    chr.getMap().broadcastMessage(chr, MaplePacketCreator.showBuffeffect(chr.getId(), this.sourceid, 2, applyfrom.getLevel(), this.level), false);
                }
            }
        } else if ((isPartyBuff()) && ((applyfrom.getParty() != null) || (isGmBuff()) || (applyfrom.inPVP()))) {
            Rectangle bounds = calculateBoundingBox(applyfrom.getTruePosition(), applyfrom.isFacingLeft());
            List<MapleMapObject> affecteds = applyfrom.getMap().getMapObjectsInRect(bounds, Arrays.asList(MapleMapObjectType.PLAYER));
            for (MapleMapObject affectedmo : affecteds) {
                MapleCharacter affected = (MapleCharacter) affectedmo;
                if ((affected.getId() != applyfrom.getId()) && ((isGmBuff()) || ((applyfrom.inPVP()) && (affected.getTeam() == applyfrom.getTeam()) && (Integer.parseInt(applyfrom.getEventInstance().getProperty("type")) != 0)) || ((applyfrom.getParty() != null) && (affected.getParty() != null) && (applyfrom.getParty().getId() == affected.getParty().getId())))) {
                    if (((is复活术()) && (!affected.isAlive())) || ((!is复活术()) && (affected.isAlive()))) {
                        applyTo(applyfrom, affected, false, null, newDuration);
                        affected.getClient().getSession().write(MaplePacketCreator.showOwnBuffEffect(this.sourceid, 2, applyfrom.getLevel(), this.level));
                        affected.getMap().broadcastMessage(affected, MaplePacketCreator.showBuffeffect(affected.getId(), this.sourceid, 2, applyfrom.getLevel(), this.level), false);
                    }
                    if (is伺机待发()) {
                        for (MapleCoolDownValueHolder i : affected.getCooldowns()) {
                            if (i.skillId != 5821010) {
                                affected.removeCooldown(i.skillId);
                                affected.getClient().getSession().write(MaplePacketCreator.skillCooldown(i.skillId, 0));
                            }
                        }
                    }
                }
            }
        }
        
    }

    private void removeMonsterBuff(MapleCharacter applyfrom) {
        List<MonsterStatus> cancel = new ArrayList<MonsterStatus>();
        switch (this.sourceid) {
            case 1111007:
            case 1211009:
            case 1311007:
                cancel.add(MonsterStatus.物防提升);
                cancel.add(MonsterStatus.魔防提升);
                cancel.add(MonsterStatus.物攻提升);
                cancel.add(MonsterStatus.魔攻提升);
                break;
            default:
                return;
        }
        Rectangle bounds = calculateBoundingBox(applyfrom.getTruePosition(), applyfrom.isFacingLeft());
        List<MapleMapObject> affected = applyfrom.getMap().getMapObjectsInRect(bounds, Arrays.asList(MapleMapObjectType.MONSTER));
        int i = 0;
        for (MapleMapObject mo : affected) {
            if (makeChanceResult()) {
                for (MonsterStatus stat : cancel) {
                    ((MapleMonster) mo).cancelStatus(stat);
                }
            }
            i++;
            if (i >= this.mobCount) {
                break;
            }
        }
    }

    public void applyMonsterBuff(MapleCharacter applyfrom) {
        Rectangle bounds = calculateBoundingBox(applyfrom.getTruePosition(), applyfrom.isFacingLeft());
        boolean pvp = applyfrom.inPVP();
        MapleMapObjectType types = pvp ? MapleMapObjectType.PLAYER : MapleMapObjectType.MONSTER;
        List<MapleMapObject> affected = this.sourceid == 35111005 ? applyfrom.getMap().getMapObjectsInRange(applyfrom.getTruePosition(), (1.0D / 0.0D), Arrays.asList(types)) : applyfrom.getMap().getMapObjectsInRect(bounds, Arrays.asList(types));
        int i = 0;
        for (MapleMapObject mo : affected) {
            if (makeChanceResult()) {
                for (Entry<MonsterStatus, Integer> stat : getMonsterStati().entrySet()) {
                    if (pvp) {
                        MapleCharacter chr = (MapleCharacter) mo;
                        MapleDisease d = MonsterStatus.getLinkedDisease(stat.getKey());
                        if (d != null) {
                            chr.giveDebuff(d, stat.getValue(), getDuration(), d.getDisease(), 1);
                        }
                    } else {
                        MapleMonster mons = (MapleMonster) mo;
                        if ((this.sourceid == 35111005) && (mons.getStats().isBoss())) {
                            break;
                        }
                        mons.applyStatus(applyfrom, new MonsterStatusEffect( stat.getKey(),stat.getValue(), this.sourceid, null, false), isPoison(), getDuration(), true, this);
                    }
                }
                if ((pvp) && (this.skill)) {
                    MapleCharacter chr = (MapleCharacter) mo;
                    handleExtraPVP(applyfrom, chr);
                }
            }
            i++;
            if ((i >= this.mobCount) && (this.sourceid != 35111005)) {
                break;
            }
        }
    }

    public boolean isSubTime(int source) {
        switch (source) {
            case 1201006:
            case 23111008:
            case 23111009:
            case 23111010:
            case 31101003:
            case 31121003:
            case 31121005:
                return true;
        }
        return false;
    }

    public void handleExtraPVP(MapleCharacter applyfrom, MapleCharacter chr) {
        if ((this.sourceid == 2311005) || (this.sourceid == 5821005) || (this.sourceid == 1201006) || ((GameConstants.is新手职业(this.sourceid / 10000)) && (this.sourceid % 10000 == 104))) {
            long starttime = System.currentTimeMillis();
            int localsourceid = this.sourceid == 5821005 ? 90002000 : this.sourceid;
            List localstatups = new ArrayList();
            if (this.sourceid == 2311005) {
                localstatups = Collections.singletonList(new Pair(MapleBuffStat.变身术, Integer.valueOf(7)));
            } else if (this.sourceid == 1201006) {
                localstatups = Collections.singletonList(new Pair(MapleBuffStat.压制术, Integer.valueOf(this.level)));
            } else if (this.sourceid == 5821005) {
                localstatups = Collections.singletonList(new Pair(MapleBuffStat.拳手索命, Integer.valueOf(1)));
            } else {
                localstatups = Collections.singletonList(new Pair(MapleBuffStat.变身术, Integer.valueOf(this.x)));
            }
            chr.getClient().getSession().write(MaplePacketCreator.giveBuff(localsourceid, getDuration(), localstatups, this));
            chr.registerEffect(this, starttime, Timer.BuffTimer.getInstance().schedule(new CancelEffectAction(chr, this, starttime, localstatups), getDuration()), localstatups, false, getDuration(), applyfrom.getId());
        }
    }

    public Rectangle calculateBoundingBox(Point posFrom, boolean facingLeft) {
        return calculateBoundingBox(posFrom, facingLeft, this.lt, this.rb, this.range);
    }

    public Rectangle calculateBoundingBox(Point posFrom, boolean facingLeft, int addedRange) {
        return calculateBoundingBox(posFrom, facingLeft, this.lt, this.rb, this.range + addedRange);
    }

    public static Rectangle calculateBoundingBox(Point posFrom, boolean facingLeft, Point lt, Point rb, int range) {
        if ((lt == null) || (rb == null)) {
            return new Rectangle((facingLeft ? -200 - range : 0) + posFrom.x, -100 - range + posFrom.y, 200 + range, 100 + range);
        }
        Point myrb;
        Point mylt;
        if (facingLeft) {
            mylt = new Point(lt.x + posFrom.x - range, lt.y + posFrom.y);
            myrb = new Point(rb.x + posFrom.x, rb.y + posFrom.y);
        } else {
            myrb = new Point(lt.x * -1 + posFrom.x + range, rb.y + posFrom.y);
            mylt = new Point(rb.x * -1 + posFrom.x, lt.y + posFrom.y);
        }
        return new Rectangle(mylt.x, mylt.y, myrb.x - mylt.x, myrb.y - mylt.y);
    }

    public double getMaxDistanceSq() {
        int maxX = Math.max(Math.abs(this.lt == null ? 0 : this.lt.x), Math.abs(this.rb == null ? 0 : this.rb.x));
        int maxY = Math.max(Math.abs(this.lt == null ? 0 : this.lt.y), Math.abs(this.rb == null ? 0 : this.rb.y));
        return maxX * maxX + maxY * maxY;
    }

    public void setDuration(int d) {
        this.duration = d;
    }

    public void silentApplyBuff(MapleCharacter chr, long starttime, int localDuration, List<Pair<MapleBuffStat, Integer>> statup, int cid) {
        chr.registerEffect(this, starttime, Timer.BuffTimer.getInstance().schedule(new CancelEffectAction(chr, this, starttime, statup), starttime + localDuration - System.currentTimeMillis()), statup, true, localDuration, cid);
        SummonMovementType summonMovementType = getSummonMovementType();
        if (summonMovementType != null) {
            MapleSummon tosummon = new MapleSummon(chr, this, chr.getTruePosition(), summonMovementType);
            if (!tosummon.isPuppet()) {
                chr.getCheatTracker().resetSummonAttack();
                chr.getMap().spawnSummon(tosummon);
                chr.addSummon(tosummon);
                tosummon.addHP((short) this.x);
                if (is灵魂助力()) {
                    tosummon.addHP((short)1);
                }
            }
        }
    }

    public void applyComboBuff(MapleCharacter applyto, short combo) {
        List stat = Collections.singletonList(new Pair(MapleBuffStat.矛连击强化, Integer.valueOf(combo)));
        applyto.getClient().getSession().write(MaplePacketCreator.giveBuff(this.sourceid, 99999, stat, this));
        long starttime = System.currentTimeMillis();

        applyto.registerEffect(this, starttime, null, applyto.getId());
    }

    public void applyEnergyBuff(MapleCharacter applyto, boolean infinity) {
        long starttime = System.currentTimeMillis();
        if (infinity) {
            applyto.getClient().getSession().write(MaplePacketCreator.giveEnergyChargeTest(0, this.duration / 1000));
            applyto.registerEffect(this, starttime, null, applyto.getId());
        } else {
            List stat = Collections.singletonList(new Pair(MapleBuffStat.能量获得, Integer.valueOf(10000)));
            applyto.cancelEffect(this, true, -1L, stat);
            applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveEnergyChargeTest(applyto.getId(), 10000, this.duration / 1000), false);
            CancelEffectAction cancelAction = new CancelEffectAction(applyto, this, starttime, stat);
            ScheduledFuture schedule = Timer.BuffTimer.getInstance().schedule(cancelAction, starttime + this.duration - System.currentTimeMillis());
            applyto.registerEffect(this, starttime, schedule, stat, false, this.duration, applyto.getId());
        }
    }

    private void applyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, boolean primary, int newDuration) {
        if ((!applyto.isAdmin()) && (applyto.getMap().isMarketMap())) {
            applyto.getClient().getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        int localDuration = newDuration;
        if (primary) {
            localDuration = Math.max(newDuration, alchemistModifyVal(applyfrom, localDuration, false));
        }
        List<Pair<MapleBuffStat, Integer>> localstatups = this.statups;
        List maskedStatups = null;
        boolean normal = true;
        boolean showEffect = primary;
        int maskedDuration = 0;
        switch (this.sourceid) {
            case 5311005:
            case 5711011:
            case 5811007:
            case 5911007:
            case 15111011:
            case 35111013:{
                int zz = Randomizer.nextInt(6) + 1;
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.showDiceEffect(applyto.getId(), this.sourceid, zz, -1, this.level), false);
                applyto.getClient().getSession().write(MaplePacketCreator.showOwnDiceEffect(this.sourceid, zz, -1, this.level));
                if (zz <= 1) {
                    applyto.dropMessage(-10, "幸运骰子技能失败。");
                    return;
                }
                localstatups = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.幸运骰子, zz));
                applyto.dropMessage(-10, "幸运骰子技能发动了[" + zz + "]号效果。");
                applyto.getClient().getSession().write(MaplePacketCreator.giveDice(zz, this.sourceid, localDuration, localstatups));
                normal = false;
                showEffect = false;
                break;
            }
            case 5320007:
            case 5720005:{
                int zz = Randomizer.nextInt(6) + 1;
                int zz2 = makeChanceResult() ? Randomizer.nextInt(6) + 1 : 0;
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.showDiceEffect(applyto.getId(), this.sourceid, zz, zz2 > 0 ? -1 : 0, this.level), false);
                applyto.getClient().getSession().write(MaplePacketCreator.showOwnDiceEffect(this.sourceid, zz, zz2 > 0 ? -1 : 0, this.level));
                if ((zz <= 1) && (zz2 <= 1)) {
                    applyto.dropMessage(-10, "双幸运骰子技能失败。");
                    return;
                }
                int buffid = zz2 <= 1 ? zz : zz <= 1 ? zz2 : zz == zz2 ? zz * 100 : zz * 10 + zz2;
                if (buffid >= 100) {
                    applyto.dropMessage(-10, "[双幸运骰子] You have rolled a Double Down! (" + buffid / 100 + ")");
                } else if (buffid >= 10) {
                    applyto.dropMessage(-10, "[双幸运骰子] You have rolled two dice. (" + buffid / 10 + " and " + buffid % 10 + ")");
                }
                localstatups = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.幸运骰子, buffid));
                applyto.getClient().getSession().write(MaplePacketCreator.giveDice(zz, this.sourceid, localDuration, localstatups));
                normal = false;
                showEffect = false;
                break;
            }
            case 33101006:
                applyto.clearLinkMid();
                MapleBuffStat theBuff = null;
                int theStat = this.y;
                switch (Randomizer.nextInt(6)) {
                    case 0:
                        theBuff = MapleBuffStat.CRITICAL_RATE_BUFF;
                        break;
                    case 1:
                        theBuff = MapleBuffStat.MP_BUFF;
                        break;
                    case 2:
                        theBuff = MapleBuffStat.DAMAGE_TAKEN_BUFF;
                        theStat = this.x;
                        break;
                    case 3:
                        theBuff = MapleBuffStat.DODGE_CHANGE_BUFF;
                        theStat = this.x;
                        break;
                    case 4:
                        theBuff = MapleBuffStat.DAMAGE_BUFF;
                        break;
                    case 5:
                        theBuff = MapleBuffStat.ATTACK_BUFF;
                }

                localstatups = Collections.singletonList(new Pair<MapleBuffStat, Integer>(theBuff,theStat));
                applyto.getClient().getSession().write(MaplePacketCreator.giveBuff(this.sourceid, localDuration, localstatups, this));
                normal = false;
                break;
            case 8006:
            case 10008006:
            case 20008006:
            case 20018006:
            case 20028006:
            case 30008006:
            case 30018006:
                applyto.dropMessage(1, "无法使用这个技能。");
                return;
            case 4321000:
            case 5091005:
            case 5821009:
            case 15001003:
            case 15111005:
                if (applyfrom.getStatForBuff(MapleBuffStat.飞行骑乘) != null) {
                    break;
                }
                applyto.getClient().getSession().write(MaplePacketCreator.givePirate(this.statups, localDuration / 1000, this.sourceid));
                if (!applyto.isHidden()) {
                    applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignPirate(this.statups, localDuration / 1000, applyto.getId(), this.sourceid), false);
                }
                normal = false;
                break;
            case 5721003:
            case 5911006:
            case 5920011:
            case 22151002:
                if (applyto.getFirstLinkMid() > 0) {
                    applyto.getClient().getSession().write(MaplePacketCreator.cancelHoming());
                    applyto.getClient().getSession().write(MaplePacketCreator.giveHoming(this.sourceid, applyto.getFirstLinkMid(), 1));
                } else {
                    return;
                }
                normal = false;
                break;
            case 2120010:
            case 2220010:
            case 2320011:
                if (applyto.getFirstLinkMid() > 0) {
                    applyto.getClient().getSession().write(MaplePacketCreator.giveArcane(applyto.getAllLinkMid(), localDuration));
                } else {
                    return;
                }
                normal = false;
                break;
            case 30001001:
            case 30011001:{
                if (applyto.isHidden()) {
                    break;
                }
                List stat = Collections.singletonList(new Pair(MapleBuffStat.潜入, Integer.valueOf(0)));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 13101006:{
                if (applyto.isHidden()) {
                    break;
                }
                List stat = Collections.singletonList(new Pair(MapleBuffStat.风影漫步, Integer.valueOf(0)));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 4001003:
                if (applyfrom.getTotalSkillLevel(4330001) <= 0) {
                    break;
                }
                SkillFactory.getSkill(4330001).getEffect(applyfrom.getTotalSkillLevel(4330001)).applyBuffEffect(applyfrom, applyto, primary, newDuration);
                return;
            case 4330001:
            case 14001003:{
                if (applyto.isHidden()) {
                    return;
                }
                List stat = Collections.singletonList(new Pair(MapleBuffStat.隐身术, Integer.valueOf(0)));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 23111005:{
                List stat = new ArrayList();
                stat.add(new Pair(MapleBuffStat.异常抗性, Integer.valueOf(this.y)));
                stat.add(new Pair(MapleBuffStat.属性抗性, Integer.valueOf(this.y)));
                stat.add(new Pair(MapleBuffStat.双弩水盾, Integer.valueOf(this.x)));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 23101003:{
                List stat = new ArrayList();
                stat.add(new Pair(MapleBuffStat.精神连接, Integer.valueOf(this.x)));
                stat.add(new Pair(MapleBuffStat.精神注入, Integer.valueOf(this.x)));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 32121003:{
                if (applyto.isHidden()) {
                    break;
                }
                List stat = Collections.singletonList(new Pair(MapleBuffStat.幻灵飓风, Integer.valueOf(this.x)));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 32111005:{
                applyto.cancelEffectFromBuffStat(MapleBuffStat.幻灵霸体);

                int sourcez = 0;
                Pair<MapleBuffStat, Integer> statt;
                if (applyfrom.getStatForBuff(MapleBuffStat.黑暗灵气) != null) {
                    sourcez = 32001003;
                    statt = new Pair(MapleBuffStat.黑暗灵气, Integer.valueOf(this.level + 10 + applyto.getTotalSkillLevel(32001003)));
                } else {
                    if (applyfrom.getStatForBuff(MapleBuffStat.黄色灵气) != null) {
                        sourcez = 32101003;
                        statt = new Pair(MapleBuffStat.黄色灵气, Integer.valueOf(applyto.getTotalSkillLevel(32101003)));
                    } else {
                        if (applyfrom.getStatForBuff(MapleBuffStat.蓝色灵气) != null) {
                            sourcez = 32111012;
                            localDuration = 10000;
                            statt = new Pair(MapleBuffStat.蓝色灵气, Integer.valueOf(applyto.getTotalSkillLevel(32111012)));
                        } else {
                            return;
                        }
                    }
                }
                localstatups = new ArrayList();
                localstatups.add(new Pair(MapleBuffStat.幻灵霸体, Integer.valueOf(this.level)));
                applyto.getClient().getSession().write(MaplePacketCreator.giveBuff(this.sourceid, localDuration, localstatups, this));
                localstatups.add(statt);
                applyto.cancelEffectFromBuffStat(MapleBuffStat.蓝色灵气, applyfrom.getId());
                applyto.cancelEffectFromBuffStat(MapleBuffStat.黄色灵气, applyfrom.getId());
                applyto.cancelEffectFromBuffStat(MapleBuffStat.黑暗灵气, applyfrom.getId());
                applyto.getClient().getSession().write(MaplePacketCreator.giveBuff(sourcez, localDuration, Collections.singletonList(statt), this));
                normal = false;
                break;
            }
            case 32001003:{
                if (applyfrom.getTotalSkillLevel(32120000) > 0) {
                    SkillFactory.getSkill(32120000).getEffect(applyfrom.getTotalSkillLevel(32120000)).applyBuffEffect(applyfrom, applyto, primary, newDuration);
                    return;
                }
            }
            case 32110007:{
                applyto.cancelEffectFromBuffStat(MapleBuffStat.黑暗灵气);

                applyto.cancelEffectFromBuffStat(MapleBuffStat.幻灵霸体);
                List statt = Collections.singletonList(new Pair(this.sourceid == 32110007 ? MapleBuffStat.幻灵霸体 : MapleBuffStat.幻灵灵气, Integer.valueOf(this.level)));
                applyto.getClient().getSession().write(MaplePacketCreator.giveBuff(this.sourceid, localDuration, statt, this));
                statt = Collections.singletonList(new Pair(MapleBuffStat.黑暗灵气, Integer.valueOf(this.x)));
                applyto.getClient().getSession().write(MaplePacketCreator.giveBuff(this.sourceid, localDuration, statt, this));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), statt, this), false);
                normal = false;
                break;
            }
            case 32120000:{
                applyto.cancelEffectFromBuffStat(MapleBuffStat.黑暗灵气);

                applyto.cancelEffectFromBuffStat(MapleBuffStat.幻灵霸体);
                List<Pair<MapleBuffStat,Integer>> statt = Collections.singletonList(new Pair<MapleBuffStat,Integer>(MapleBuffStat.幻灵灵气, applyfrom.getTotalSkillLevel(32001003)));
                applyto.getClient().getSession().write(MaplePacketCreator.giveBuff(32001003, localDuration, statt, this));
                statt = Collections.singletonList(new Pair<MapleBuffStat,Integer>(MapleBuffStat.黑暗灵气, this.x));
                applyto.getClient().getSession().write(MaplePacketCreator.giveBuff(this.sourceid, localDuration, statt, this));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), statt, this), false);
                normal = false;
                break;
            }
            case 32111012:{
                if (applyfrom.getTotalSkillLevel(32110000) > 0) {
                    SkillFactory.getSkill(32110000).getEffect(applyfrom.getTotalSkillLevel(32110000)).applyBuffEffect(applyfrom, applyto, primary, newDuration);
                    return;
                }
            }
            case 32110008:{
                localDuration = 10000;
            }
            case 32110000:{
                applyto.cancelEffectFromBuffStat(MapleBuffStat.蓝色灵气);

                applyto.cancelEffectFromBuffStat(MapleBuffStat.幻灵霸体);
                List statt = Collections.singletonList(new Pair(this.sourceid == 32110008 ? MapleBuffStat.幻灵霸体 : MapleBuffStat.幻灵灵气, Integer.valueOf(this.sourceid == 32110000 ? applyfrom.getTotalSkillLevel(32111012) : this.level)));
                applyto.getClient().getSession().write(MaplePacketCreator.giveBuff(this.sourceid == 32110000 ? 32111012 : this.sourceid, localDuration, statt, this));
                statt = Collections.singletonList(new Pair(MapleBuffStat.蓝色灵气, Integer.valueOf(this.level)));
                applyto.getClient().getSession().write(MaplePacketCreator.giveBuff(this.sourceid, localDuration, statt, this));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), statt, this), false);
                normal = false;
                break;
            }
            case 32101003:{
                if (applyfrom.getTotalSkillLevel(32120001) > 0) {
                    SkillFactory.getSkill(32120001).getEffect(applyfrom.getTotalSkillLevel(32120001)).applyBuffEffect(applyfrom, applyto, primary, newDuration);
                    return;
                }
            }
            case 32110009:
            case 32120001:{
                applyto.cancelEffectFromBuffStat(MapleBuffStat.黄色灵气);

                applyto.cancelEffectFromBuffStat(MapleBuffStat.幻灵霸体);
                List statt = Collections.singletonList(new Pair(this.sourceid == 32110009 ? MapleBuffStat.幻灵霸体 : MapleBuffStat.幻灵灵气, Integer.valueOf(this.sourceid == 32120001 ? applyfrom.getTotalSkillLevel(32101003) : this.level)));
                applyto.getClient().getSession().write(MaplePacketCreator.giveBuff(this.sourceid == 32120001 ? 32101003 : this.sourceid, localDuration, statt, this));
                statt = Collections.singletonList(new Pair(MapleBuffStat.黄色灵气, Integer.valueOf(this.level)));
                applyto.getClient().getSession().write(MaplePacketCreator.giveBuff(this.sourceid, localDuration, statt, this));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), statt, this), false);
                normal = false;
                break;
            }
            case 1211008:{
                if ((applyto.getBuffedValue(MapleBuffStat.属性攻击) != null) && (applyto.getBuffSource(MapleBuffStat.属性攻击) != this.sourceid)) {
                    localstatups = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.雷鸣冲击, Integer.valueOf(1)));
                } else if (!applyto.isHidden()) {
                    List stat = Collections.singletonList(new Pair(MapleBuffStat.属性攻击, Integer.valueOf(1)));
                    applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), stat, this), false);
                }
                applyto.getClient().getSession().write(MaplePacketCreator.giveBuff(this.sourceid, localDuration, localstatups, this));
                normal = false;
                break;
            }
            case 35111004:{
                if ((applyto.getBuffedValue(MapleBuffStat.金属机甲) != null) && (applyto.getBuffSource(MapleBuffStat.金属机甲) == 35121005)) {
                    SkillFactory.getSkill(35121013).getEffect(this.level).applyBuffEffect(applyfrom, applyto, primary, newDuration);
                    return;
                }
                if (applyto.isHidden()) {
                    break;
                }
                List stat = Collections.singletonList(new Pair(MapleBuffStat.金属机甲, Integer.valueOf(1)));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 35001001:
            case 35101009:
            case 35121005:
            case 35121013:{
                if (applyto.isHidden()) {
                    break;
                }
                List stat = Collections.singletonList(new Pair(MapleBuffStat.金属机甲, Integer.valueOf(1)));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 1220013:{
                if (applyto.isHidden()) {
                    break ;
                }
                List stat = Collections.singletonList(new Pair(MapleBuffStat.祝福护甲, Integer.valueOf(1)));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 1111002:
            case 11111001:{
                if (applyto.isHidden()) {
                    break ;
                }
                List stat = Collections.singletonList(new Pair(MapleBuffStat.斗气集中, Integer.valueOf(0)));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 3101004:
            case 3201004:
            case 13101003:{
                if (applyto.isHidden()) {
                    break;
                }
                List stat = Collections.singletonList(new Pair(MapleBuffStat.无形箭弩, Integer.valueOf(0)));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 2321005:
                applyto.cancelEffectFromBuffStat(MapleBuffStat.牧师祝福);
                break;
            case 4111002:
            case 4211008:
            case 4331002:
            case 14111000:{
                if (applyto.isHidden()) {
                    break ;
                }
                List<Pair<MapleBuffStat,Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat,Integer>(MapleBuffStat.影分身, Integer.valueOf(0)));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 15111006:
                localstatups = Collections.singletonList(new Pair<MapleBuffStat,Integer>(MapleBuffStat.闪光击, Integer.valueOf(this.x)));
                applyto.getClient().getSession().write(MaplePacketCreator.giveBuff(this.sourceid, localDuration, localstatups, this));
                normal = false;
                break;
            case 4341002:
                localstatups = Collections.singletonList(new Pair<MapleBuffStat,Integer>(MapleBuffStat.终极斩, Integer.valueOf(this.y)));
                applyto.getClient().getSession().write(MaplePacketCreator.giveBuff(this.sourceid, localDuration, localstatups, this));
                normal = false;
                break;
            case 3211005:
                if (applyfrom.getTotalSkillLevel(3220005) <= 0) {
                    break;
                }
                SkillFactory.getSkill(3220005).getEffect(applyfrom.getTotalSkillLevel(3220005)).applyBuffEffect(applyfrom, applyto, primary, newDuration);
                break;
            case 3111005:
                if (applyfrom.getTotalSkillLevel(3120006) <= 0) {
                    break ;
                }
                SkillFactory.getSkill(3120006).getEffect(applyfrom.getTotalSkillLevel(3120006)).applyBuffEffect(applyfrom, applyto, primary, newDuration);
                break;
            case 1211004:
            case 1211006:
            case 1221004:
            case 11111007:
            case 21101006:{
                if (applyto.isHidden()) {
                    break;
                }
                List<Pair<MapleBuffStat,Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat,Integer>(MapleBuffStat.属性攻击, Integer.valueOf(1)));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 3120006:
            case 3220005:{
                if (applyto.isHidden()) {
                    break;
                }
                List<Pair<MapleBuffStat,Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat,Integer>(MapleBuffStat.精神连接, Integer.valueOf(0)));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 2121004:
            case 2221004:
            case 2321004:
                maskedDuration = alchemistModifyVal(applyfrom, 4000, false);
                break;
            case 1121010:
                applyto.handleOrbconsume(10);
                break;
            case 35001002:
                if (applyfrom.getTotalSkillLevel(35120000) > 0) {
                    SkillFactory.getSkill(35120000).getEffect(applyfrom.getTotalSkillLevel(35120000)).applyBuffEffect(applyfrom, applyto, primary, newDuration);
                    return;
                }
        }
        if (isPirateMorph()) {
            List stat = Collections.singletonList(new Pair(MapleBuffStat.变身术, Integer.valueOf(getMorph(applyto))));
            applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), stat, this), false);
            applyto.getClient().getSession().write(MaplePacketCreator.giveBuff(this.sourceid, localDuration, stat, this));
            maskedStatups = new ArrayList(localstatups);
            maskedStatups.remove(new Pair(MapleBuffStat.变身术, Integer.valueOf(getMorph())));
            normal = maskedStatups.size() > 0;
        } else if (isMorph()) {
            if (!applyto.isHidden()) {
                if (is冰骑士()) {
                    List stat = Collections.singletonList(new Pair(MapleBuffStat.冰骑士, Integer.valueOf(2)));
                    applyto.getClient().getSession().write(MaplePacketCreator.giveBuff(0, localDuration, stat, this));
                }
                List stat = Collections.singletonList(new Pair(MapleBuffStat.变身术, Integer.valueOf(getMorph(applyto))));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), stat, this), false);
            }
        } else if (isInflation()) {
            if (!applyto.isHidden()) {
                List stat = Collections.singletonList(new Pair(MapleBuffStat.GIANT_POTION, Integer.valueOf(this.inflation)));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), stat, this), false);
            }
        } else if (this.charColor > 0) {
            if (!applyto.isHidden()) {
                List stat = Collections.singletonList(new Pair(MapleBuffStat.FAMILIAR_SHADOW, Integer.valueOf(1)));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), stat, this), false);
            }
        } else if (is骑兽技能()) {
            localDuration = 2100000000;
            localstatups = new ArrayList(this.statups);
            localstatups.add(new Pair(MapleBuffStat.骑兽技能, Integer.valueOf(1)));
            int mountid = parseMountInfo(applyto, this.sourceid);
            int mountid2 = parseMountInfo_Pure(applyto, this.sourceid);
            if ((mountid != 0) && (mountid2 != 0)) {
                List stat = Collections.singletonList(new Pair(MapleBuffStat.骑兽技能, Integer.valueOf(0)));
                applyto.cancelEffectFromBuffStat(MapleBuffStat.伤害反击);
                applyto.cancelEffectFromBuffStat(MapleBuffStat.魔法反击);
                applyto.getClient().getSession().write(MaplePacketCreator.giveMount(mountid2, this.sourceid, stat));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.showMonsterRiding(applyto.getId(), stat, mountid, this.sourceid), false);
            } else {
                return;
            }
            maskedStatups = new ArrayList(localstatups);
            maskedStatups.remove(new Pair(MapleBuffStat.骑兽技能, Integer.valueOf(1)));
            normal = maskedStatups.size() > 0;
        } else if (isSoaring()) {
            if (!applyto.isHidden()) {
                List stat = Collections.singletonList(new Pair(MapleBuffStat.飞行骑乘, Integer.valueOf(1)));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), stat, this), false);
            }
        } else if (this.berserk > 0) {
            if (!applyto.isHidden()) {
                List stat = Collections.singletonList(new Pair(MapleBuffStat.PYRAMID_PQ, Integer.valueOf(0)));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), stat, this), false);
            }
        } else if ((is狂暴战魂()) || (this.berserk2 > 0)) {
            if (!applyto.isHidden()) {
                List stat = Collections.singletonList(new Pair(MapleBuffStat.狂暴战魂, Integer.valueOf(1)));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), stat, this), false);
            }
        } else if (is金刚霸体()) {
            if (!applyto.isHidden()) {
                List stat = Collections.singletonList(new Pair(MapleBuffStat.金刚霸体, Integer.valueOf(1)));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), stat, this), false);
            }
        } else if ((is天使效果()) && (applyto.getStat().equippedSummon > 0)) {
            localstatups = new ArrayList(this.statups);
            int equippedSummon = applyto.getStat().equippedSummon % 10000;
            if ((equippedSummon == 1085) || (equippedSummon == 1090)) {
                localstatups.add(new Pair(MapleBuffStat.ANGEL_ATK, Integer.valueOf(5)));
                localstatups.add(new Pair(MapleBuffStat.ANGEL_MATK, Integer.valueOf(5)));
            } else if (equippedSummon == 1087) {
                localstatups.add(new Pair(MapleBuffStat.ANGEL_ATK, Integer.valueOf(10)));
                localstatups.add(new Pair(MapleBuffStat.ANGEL_MATK, Integer.valueOf(10)));
            } else if (equippedSummon == 1179) {
                localstatups.add(new Pair(MapleBuffStat.ANGEL_ATK, Integer.valueOf(12)));
                localstatups.add(new Pair(MapleBuffStat.ANGEL_MATK, Integer.valueOf(12)));
            }

        }

        label4894:
        if ((showEffect) && (!applyto.isHidden())) {
            if (ServerProperties.ShowPacket()) {
                log.info("开始加状态 - showBuffeffect");
            }
            applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.showBuffeffect(applyto.getId(), this.sourceid, 1, applyto.getLevel(), this.level), false);
        }
        if (isMechPassive()) {
            if (ServerProperties.ShowPacket()) {
                log.info("开始加状态 - showOwnBuffEffect");
            }
            applyto.getClient().getSession().write(MaplePacketCreator.showOwnBuffEffect(this.sourceid - 1000, 1, applyto.getLevel(), this.level, (byte)1));
        }
        if ((!is骑兽技能()) && (!is机械传送门())) {
            applyto.cancelEffect(this, true, -1L, localstatups);
        }

        if (getCooldown() > 0) {
            applyfrom.getClient().getSession().write(MaplePacketCreator.skillCooldown(this.sourceid, getCooldown()));
            applyfrom.addCooldown(this.sourceid, System.currentTimeMillis(), getCooldown() * 1000);
        }

        if ((normal) && (localstatups.size() > 0)) {
            if (ServerProperties.ShowPacket()) {
                log.info("开始加状态 - normal && localstatups.size() > 0");
            }
            if (is火焰咆哮()) {
                applyto.getClient().getSession().write(MaplePacketCreator.give火焰咆哮(this.sourceid, localDuration, localstatups, applyto.getStat().equippedSummon % 10000));
            } else {
                applyto.getClient().getSession().write(MaplePacketCreator.giveBuff(this.skill ? this.sourceid : -this.sourceid, localDuration, maskedStatups == null ? localstatups : maskedStatups, this));
            }
        }

        long starttime = System.currentTimeMillis();
        CancelEffectAction cancelAction = new CancelEffectAction(applyto, this, starttime, localstatups);
        ScheduledFuture schedule = Timer.BuffTimer.getInstance().schedule(cancelAction, localDuration);
        applyto.registerEffect(this, starttime, schedule, localstatups, false, localDuration, applyfrom.getId());
    }

    public static int parseMountInfo(MapleCharacter player, int skillid) {
        switch (skillid) {
            case 1004:
            case 11004:
            case 10001004:
            case 20001004:
            case 20011004:
            case 20021004:
            case 80001000:
                if ((player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-123) != null) && (player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-124) != null)) {
                    return player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-123).getItemId();
                }
                return parseMountInfo_Pure(player, skillid);
        }
        return GameConstants.getMountItem(skillid, player);
    }

    public static int parseMountInfo_Pure(MapleCharacter player, int skillid) {
        switch (skillid) {
            case 1004:
            case 11004:
            case 10001004:
            case 20001004:
            case 20011004:
            case 20021004:
            case 80001000:
                if ((player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-18) != null) && (player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-19) != null)) {
                    return player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-18).getItemId();
                }
                return 0;
        }
        return GameConstants.getMountItem(skillid, player);
    }

    private int calcHPChange(MapleCharacter applyfrom, boolean primary) {
        int hpchange = 0;
        if (this.hp != 0) {
            if (!this.skill) {
                if (primary) {
                    hpchange += alchemistModifyVal(applyfrom, this.hp, true);
                } else {
                    hpchange += this.hp;
                }
                if (applyfrom.hasDisease(MapleDisease.ZOMBIFY)) {
                    hpchange /= 2;
                }
            } else {
                hpchange += makeHealHP(this.hp / 100.0D, applyfrom.getStat().getTotalMagic(), 3.0D, 5.0D);
                if (applyfrom.hasDisease(MapleDisease.ZOMBIFY)) {
                    hpchange = -hpchange;
                }
            }
        }
        if (this.hpR != 0.0D) {
            hpchange += (int) (applyfrom.getStat().getCurrentMaxHp() * this.hpR) / (applyfrom.hasDisease(MapleDisease.ZOMBIFY) ? 2 : 1);
        }

        if ((primary)
                && (this.hpCon != 0)) {
            hpchange -= this.hpCon;
        }

        return hpchange;
    }

    private static int makeHealHP(double rate, double stat, double lowerfactor, double upperfactor) {
        return (int) (Math.random() * ((int) (stat * upperfactor * rate) - (int) (stat * lowerfactor * rate) + 1) + (int) (stat * lowerfactor * rate));
    }

    private int calcMPChange(MapleCharacter applyfrom, boolean primary) {
        int mpchange = 0;
        if (this.mp != 0) {
            if (primary) {
                mpchange += alchemistModifyVal(applyfrom, this.mp, true);
            } else {
                mpchange += this.mp;
            }
        }
        if (this.mpR != 0.0D) {
            mpchange += (int) (applyfrom.getStat().getCurrentMaxMp(applyfrom.getJob()) * this.mpR);
        }
        if (GameConstants.is恶魔猎手(applyfrom.getJob())) {
            mpchange = 0;
        }
        if (primary) {
            if ((this.mpCon != 0) && (!GameConstants.is恶魔猎手(applyfrom.getJob()))) {
                if (applyfrom.getBuffedValue(MapleBuffStat.终极无限) != null) {
                    mpchange = 0;
                } else {
                    mpchange = (int) (mpchange - (this.mpCon - this.mpCon * applyfrom.getStat().mpconReduce / 100) * (applyfrom.getStat().mpconPercent / 100.0D));
                }
            } else if ((this.forceCon != 0) && (GameConstants.is恶魔猎手(applyfrom.getJob()))) {
                if (applyfrom.getBuffedValue(MapleBuffStat.无限精气) != null) {
                    mpchange = 0;
                } else {
                    mpchange -= this.forceCon;
                }
            }
        }
        return mpchange;
    }

    public int alchemistModifyVal(MapleCharacter chr, int val, boolean withX) {
        if (!this.skill) {
            return val * (withX ? chr.getStat().RecoveryUP : chr.getStat().BuffUP) / 100;
        }
        return val * (withX ? chr.getStat().RecoveryUP : chr.getStat().BuffUP_Skill + (getSummonMovementType() == null ? 0 : chr.getStat().BuffUP_Summon)) / 100;
    }

    public void setSourceId(int newid) {
        this.sourceid = newid;
    }

    public boolean isGmBuff() {
        switch (this.sourceid) {
            case 9001000:
            case 9001001:
            case 9001002:
            case 9001003:
            case 9001005:
            case 9001008:
            case 9101000:
            case 9101001:
            case 9101002:
            case 9101003:
            case 9101005:
            case 9101008:
            case 10001075:
                return true;
        }
        return (GameConstants.is新手职业(this.sourceid / 10000)) && (this.sourceid % 10000 == 1005);
    }

    public boolean isInflation() {
        return this.inflation > 0;
    }

    public int getInflation() {
        return this.inflation;
    }

    public boolean is能量获得() {
        return (this.skill) && ((this.sourceid == 5810001) || (this.sourceid == 15100004));
    }

    private boolean isMonsterBuff() {
        switch (this.sourceid) {
            case 1111007:
            case 1201006:
            case 1211009:
            case 1311007:
            case 2101003:
            case 2111004:
            case 2201003:
            case 2211004:
            case 2311005:
            case 4111003:
            case 4321002:
            case 4341003:
            case 5011002:
            case 12101001:
            case 12111002:
            case 14111001:
            case 22121000:
            case 22151001:
            case 22161002:
            case 32120000:
            case 32120001:
            case 35111005:
            case 90001002:
            case 90001003:
            case 90001004:
            case 90001005:
            case 90001006:
                return this.skill;
        }
        return false;
    }

    public void setPartyBuff(boolean pb) {
        this.partyBuff = pb;
    }

    private boolean isPartyBuff() {
        if ((this.lt == null) || (this.rb == null) || (!this.partyBuff)) {
            return is灵魂之石();
        }
        switch (this.sourceid) {
            case 1211004:
            case 1211006:
            case 1211008:
            case 1221004:
            case 4341002:
            case 11111007:
            case 12101005:
            case 35121005:
                return false;
        }

        return !GameConstants.isNoDelaySkill(this.sourceid);
    }

    public boolean is神秘瞄准术() {
        return (this.skill) && ((this.sourceid == 2320011) || (this.sourceid == 2220010) || (this.sourceid == 2120010));
    }

    public boolean is群体治愈() {
        return (this.skill) && ((this.sourceid == 2301002) || (this.sourceid == 9101000) || (this.sourceid == 9001000));
    }

    public boolean is复活术() {
        return (this.skill) && ((this.sourceid == 9001005) || (this.sourceid == 9101005) || (this.sourceid == 2321006));
    }

    public boolean is伺机待发() {
        return (this.skill) && (this.sourceid == 5821010);
    }

    public boolean is黑暗灵气() {
        return (this.skill) && (this.sourceid == 32001003);
    }

    public boolean is黄色灵气() {
        return (this.skill) && (this.sourceid == 32101003);
    }

    public boolean is蓝色灵气() {
        return (this.skill) && (this.sourceid == 32111012);
    }

    public boolean is精神连接() {
        return (this.skill) && ((this.sourceid == 3120006) || (this.sourceid == 3220005));
    }

    public short getHp() {
        return this.hp;
    }

    public short getMp() {
        return this.mp;
    }

    public double getHpR() {
        return this.hpR;
    }

    public double getMpR() {
        return this.mpR;
    }

    public byte getMastery() {
        return this.mastery;
    }

    public short getWatk() {
        return this.watk;
    }

    public short getMatk() {
        return this.matk;
    }

    public short getWdef() {
        return this.wdef;
    }

    public short getMdef() {
        return this.mdef;
    }

    public short getAcc() {
        return this.acc;
    }

    public short getAvoid() {
        return this.avoid;
    }

    public short getHands() {
        return this.hands;
    }

    public short getSpeed() {
        return this.speed;
    }

    public short getJump() {
        return this.jump;
    }

    public short getPassiveSpeed() {
        return this.psdSpeed;
    }

    public short getPassiveJump() {
        return this.psdJump;
    }

    public int getDuration() {
        return this.duration;
    }

    public int getSubTime() {
        return this.subTime;
    }

    public boolean isOverTime() {
        return this.overTime;
    }

    public List<Pair<MapleBuffStat, Integer>> getStatups() {
        return this.statups;
    }

    public boolean sameSource(MapleStatEffect effect) {
        return (effect != null) && (this.sourceid == effect.sourceid) && (this.skill == effect.skill);
    }

    public int getCr() {
        return this.cr;
    }

    public int getT() {
        return this.t;
    }

    public int getU() {
        return this.u;
    }

    public int getV() {
        return this.v;
    }

    public int getW() {
        return this.w;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public short getDamage() {
        return this.damage;
    }

    public short getPVPDamage() {
        return this.PVPdamage;
    }

    public byte getAttackCount() {
        return this.attackCount;
    }

    public byte getBulletCount() {
        return this.bulletCount;
    }

    public int getBulletConsume() {
        return this.bulletConsume;
    }

    public byte getMobCount() {
        return this.mobCount;
    }

    public int getMoneyCon() {
        return this.moneyCon;
    }

    public int getCooldown() {
        return this.cooldown;
    }

    public Map<MonsterStatus, Integer> getMonsterStati() {
        return this.monsterStatus;
    }

    public int getBerserk() {
        return this.berserk;
    }

    public boolean is隐藏术() {
        return (this.skill) && ((this.sourceid == 9001004) || (this.sourceid == 9101004));
    }

    public boolean is龙之力() {
        return (this.skill) && (this.sourceid == 1311008);
    }

    public boolean is团队治疗() {
        return (this.skill) && ((this.sourceid == 1001) || (this.sourceid == 10001001) || (this.sourceid == 20001001) || (this.sourceid == 20011001) || (this.sourceid == 20021001) || (this.sourceid == 11001) || (this.sourceid == 35121005));
    }

    public boolean is黑暗力量() {
        return (this.skill) && (this.sourceid == 1320006);
    }

    public boolean is灵魂助力() {
        return (this.skill) && (this.sourceid == 1321007);
    }

    public boolean is生命分流() {
        return (this.skill) && (this.sourceid == 5801005);
    }

    public boolean is终极无限() {
        return (this.skill) && ((this.sourceid == 2121004) || (this.sourceid == 2221004) || (this.sourceid == 2321004));
    }

    public boolean is骑兽技能_() {
        return (this.skill) && ((this.sourceid == 1004) || (this.sourceid == 10001004) || (this.sourceid == 20001004) || (this.sourceid == 20011004) || (this.sourceid == 11004) || (this.sourceid == 20021004) || (this.sourceid == 80001000));
    }

    public boolean is骑兽技能() {
        return (this.skill) && ((is骑兽技能_()) || (GameConstants.getMountItem(this.sourceid, null) != 0)) && (!is机械骑兽());
    }

    public boolean is机械骑兽() {
        return (this.skill) && ((this.sourceid == 35001002) || (this.sourceid == 35120000));
    }

    public boolean is时空门() {
        return (this.skill) && ((this.sourceid == 2311002) || (this.sourceid % 10000 == 8001));
    }

    public boolean is金钱护盾() {
        return (this.skill) && (this.sourceid == 4201011);
    }

    public boolean is机械传送门() {
        return (this.skill) && (this.sourceid == 35101005);
    }

    public boolean is斗气重生() {
        return (this.skill) && (this.sourceid == 21111009);
    }

    public boolean is飞龙传动() {
        return (this.skill) && (this.sourceid == 22141004);
    }

    public boolean is火焰咆哮() {
        return (this.skill) && (this.sourceid == 23111004);
    }

    public boolean isCharge() {
        switch (this.sourceid) {
            case 1211008:
            case 11111007:
            case 12101005:
            case 21101006:
                return this.skill;
        }
        return false;
    }

    public boolean isPoison() {
        return (this.dot > 0) && (this.dotTime > 0);
    }

    private boolean isMist() {
        return (this.skill) && ((this.sourceid == 2111003) || (this.sourceid == 4221006) || (this.sourceid == 12111005) || (this.sourceid == 14111006) || (this.sourceid == 22161003) || (this.sourceid == 32121006) || (this.sourceid == 1076) || (this.sourceid == 11076));
    }

    private boolean is暗器伤人() {
        return (this.skill) && ((this.sourceid == 4111009) || (this.sourceid == 14111007));
    }

    private boolean is净化() {
        return (this.skill) && ((this.sourceid == 2311001) || (this.sourceid == 9001000) || (this.sourceid == 9101000));
    }

    private boolean is勇士的意志() {
        switch (this.sourceid) {
            case 1121011:
            case 1221012:
            case 1321010:
            case 2121008:
            case 2221008:
            case 2321009:
            case 3121009:
            case 3221008:
            case 4121009:
            case 4221008:
            case 4341008:
            case 5321006:
            case 5821008:
            case 5921010:
            case 21121008:
            case 22171004:
            case 23121008:
            case 32121008:
            case 33121008:
            case 35121008:
                return this.skill;
        }
        return false;
    }

    public boolean is矛连击强化() {
        return this.sourceid == 21000000;
    }

    public boolean is斗气集中() {
        switch (this.sourceid) {
            case 1111002:
            case 11111001:
                return this.skill;
        }
        return false;
    }

    public boolean isPirateMorph() {
        switch (this.sourceid) {
            case 5811005:
            case 5821003:
            case 13111005:
                return this.skill;
        }
        return false;
    }

    public boolean isMorph() {
        return this.morphId > 0;
    }

    public int getMorph() {
        switch (this.sourceid) {
            case 5811005:
                return 1000;
            case 5821003:
                return 1001;
            case 5801007:
                return 1002;
            case 13111005:
                return 1003;
        }
        return this.morphId;
    }

    public boolean is金刚霸体() {
        return (this.skill) && (GameConstants.is新手职业(this.sourceid / 10000)) && (this.sourceid % 10000 == 1010);
    }

    public boolean is祝福护甲() {
        switch (this.sourceid) {
            case 1220013:
                return this.skill;
        }
        return false;
    }

    public boolean is狂暴战魂() {
        return (this.skill) && (GameConstants.is新手职业(this.sourceid / 10000)) && (this.sourceid % 10000 == 1011);
    }

    public int getMorph(MapleCharacter chr) {
        int morph = getMorph();
        switch (morph) {
            case 1000:
            case 1001:
            case 1003:
                return morph + (chr.getGender() == 1 ? 100 : 0);
            case 1002:
        }
        return morph;
    }

    public byte getLevel() {
        return this.level;
    }

    public SummonMovementType getSummonMovementType() {
        if (!this.skill) {
            return null;
        }
        switch (this.sourceid) {
            case 3111002:
            case 3120012:
            case 3211002:
            case 3220012:
            case 4111007:
            case 4211007:
            case 4341006:
            case 5321003:
            case 5711001:
            case 5911001:
            case 5920002:
            case 13111004:
            case 14111010:
            case 33101008:
            case 33111003:
            case 35111002:
            case 35111005:
            case 35111011:
            case 35121003:
            case 35121009:
            case 35121010:
            case 35121011:
                return SummonMovementType.STATIONARY;
            case 3101007:
            case 3111005:
            case 3201007:
            case 3211005:
            case 23111008:
            case 23111009:
            case 23111010:
            case 33111005:
                return SummonMovementType.CIRCLE_FOLLOW;
            case 5911002:
                return SummonMovementType.CIRCLE_STATIONARY;
            case 32111006:
                return SummonMovementType.WALK_STATIONARY;
            case 1321007:
            case 2121005:
            case 2221005:
            case 2321003:
            case 5321004:
            case 11001004:
            case 12001004:
            case 12111004:
            case 13001004:
            case 14001005:
            case 15001004:
            case 35111001:
            case 35111009:
            case 35111010:
                return SummonMovementType.FOLLOW;
        }
        if (isAngel()) {
            return SummonMovementType.FOLLOW;
        }
        return null;
    }

    public boolean isAngel() {
        return GameConstants.isAngel(this.sourceid);
    }

    public boolean isSkill() {
        return this.skill;
    }

    public int getSourceId() {
        return this.sourceid;
    }

    public boolean is冰骑士() {
        return (this.skill) && (GameConstants.is新手职业(this.sourceid / 10000)) && (this.sourceid % 10000 == 1105);
    }

    public boolean isSoaring() {
        return (isSoaring_Normal()) || (isSoaring_Mount());
    }

    public boolean isSoaring_Normal() {
        return (this.skill) && (GameConstants.is新手职业(this.sourceid / 10000)) && (this.sourceid % 10000 == 1026);
    }

    public boolean isSoaring_Mount() {
        return (this.skill) && (((GameConstants.is新手职业(this.sourceid / 10000)) && (this.sourceid % 10000 == 1142)) || (this.sourceid == 80001089));
    }

    public boolean is终极弓剑() {
        switch (this.sourceid) {
            case 11101002:
            case 13101002:
                return this.skill;
        }
        return false;
    }

    public boolean is迷雾爆发() {
        switch (this.sourceid) {
            case 2121003:
                return this.skill;
        }
        return false;
    }

    public boolean is影分身() {
        switch (this.sourceid) {
            case 4111002:
            case 4211008:
            case 4331002:
            case 14111000:
                return this.skill;
        }
        return false;
    }

    public boolean isMechPassive() {
        switch (this.sourceid) {
            case 35121013:
                return true;
        }
        return false;
    }

    public boolean is天使效果() {
        return (!this.skill) && ((this.sourceid == 2022746) || (this.sourceid == 2022747) || (this.sourceid == 2022823));
    }

    public boolean makeChanceResult() {
        return (this.prop >= 100) || (Randomizer.nextInt(100) < this.prop);
    }

    public short getProb() {
        return this.prop;
    }

    public short getIgnoreMob() {
        return this.ignoreMob;
    }

    public int getEnhancedHP() {
        return this.ehp;
    }

    public int getEnhancedMP() {
        return this.emp;
    }

    public int getEnhancedWatk() {
        return this.ewatk;
    }

    public int getEnhancedMatk() {
        return this.ematk;
    }

    public int getEnhancedWdef() {
        return this.ewdef;
    }

    public int getEnhancedMdef() {
        return this.emdef;
    }

    public short getDOT() {
        return this.dot;
    }

    public short getDOTTime() {
        return this.dotTime;
    }

    public short getCriticalMax() {
        return this.criticaldamageMax;
    }

    public short getCriticalMin() {
        return this.criticaldamageMin;
    }

    public short getASRRate() {
        return this.asrR;
    }

    public short getDAMRate() {
        return this.damR;
    }

    public short getMesoRate() {
        return this.mesoR;
    }

    public int getEXP() {
        return this.exp;
    }

    public short getAttackX() {
        return this.padX;
    }

    public short getMagicX() {
        return this.madX;
    }

    public int getPercentHP() {
        return this.mhpR;
    }

    public int getPercentMP() {
        return this.mmpR;
    }

    public int getConsume() {
        return this.consumeOnPickup;
    }

    public int getSelfDestruction() {
        return this.selfDestruction;
    }

    public int getCharColor() {
        return this.charColor;
    }

    public List<Integer> getPetsCanConsume() {
        return this.petsCanConsume;
    }

    public boolean isReturnScroll() {
        return (this.skill) && ((this.sourceid == 20031203) || (this.sourceid == 80001040) || (this.sourceid == 20021110));
    }

    public boolean isMechChange() {
        switch (this.sourceid) {
            case 35001001:
            case 35101009:
            case 35111004:
            case 35121005:
            case 35121013:
                return this.skill;
        }
        return false;
    }

    public int getRange() {
        return this.range;
    }

    public short getER() {
        return this.er;
    }

    public int getPrice() {
        return this.price;
    }

    public int getExtendPrice() {
        return this.extendPrice;
    }

    public byte getPeriod() {
        return this.period;
    }

    public byte getReqGuildLevel() {
        return this.reqGuildLevel;
    }

    public byte getEXPRate() {
        return this.expR;
    }

    public short getLifeID() {
        return this.lifeId;
    }

    public short getUseLevel() {
        return this.useLevel;
    }

    public byte getSlotCount() {
        return this.slotCount;
    }

    public short getStr() {
        return this.str;
    }

    public short getStrX() {
        return this.strX;
    }

    public short getDex() {
        return this.dex;
    }

    public short getDexX() {
        return this.dexX;
    }

    public short getInt() {
        return this.int_;
    }

    public short getIntX() {
        return this.intX;
    }

    public short getLuk() {
        return this.luk;
    }

    public short getLukX() {
        return this.lukX;
    }

    public short getMPConReduce() {
        return this.mpConReduce;
    }

    public short getIndieMHp() {
        return this.indieMhp;
    }

    public short getIndieMMp() {
        return this.indieMmp;
    }

    public short getIndieMhpR() {
        return this.indieMhpR;
    }

    public short getIndieMmpR() {
        return this.indieMmpR;
    }

    public short getIndieAllStat() {
        return this.indieAllStat;
    }

    public short getIndiePdd() {
        return this.indiePdd;
    }

    public short getIndieMdd() {
        return this.indieMdd;
    }

    public short getIndieDamR() {
        return this.indieDamR;
    }

    public byte getType() {
        return this.type;
    }

    public int getBossDamage() {
        return this.bdR;
    }

    public int getInterval() {
        return this.interval;
    }

    public ArrayList<Pair<Integer, Integer>> getAvailableMaps() {
        return this.availableMap;
    }

    public short getWDEFRate() {
        return this.pddR;
    }

    public short getMDEFRate() {
        return this.mddR;
    }

    public short getMaxHpX() {
        return this.mhpX;
    }

    public short getMaxMpX() {
        return this.mmpX;
    }

    public static class CancelEffectAction implements Runnable {

        private MapleStatEffect effect;
        private WeakReference<MapleCharacter> target;
        private long startTime;
        private List<Pair<MapleBuffStat, Integer>> statup;

        public CancelEffectAction(MapleCharacter target, MapleStatEffect effect, long startTime, List<Pair<MapleBuffStat, Integer>> statup) {
            this.effect = effect;
            this.target = new WeakReference(target);
            this.startTime = startTime;
            this.statup = statup;
        }

        public void run() {
            MapleCharacter realTarget = (MapleCharacter) this.target.get();
            if ((realTarget != null) && (!realTarget.isClone())) {
                realTarget.cancelEffect(this.effect, false, this.startTime, this.statup);
            }
        }
    }
}