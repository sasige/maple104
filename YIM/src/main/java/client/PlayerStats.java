package client;

import client.inventory.Equip;
import client.inventory.EquipAdditions;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.MapleWeaponType;
import constants.GameConstants;
import handling.world.World.Guild;
import handling.world.guild.MapleGuild;
import handling.world.guild.MapleGuildSkill;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import server.StructItemOption;
import server.StructSetItem;
import server.StructSetItem.SetItem;
import server.life.Element;
import tools.MaplePacketCreator;
import tools.Pair;
import tools.data.MaplePacketLittleEndianWriter;

public class PlayerStats implements Serializable {

    private static final long serialVersionUID = -679541993413738569L;
    private Map<Integer, Integer> setHandling = new HashMap();
    private Map<Integer, Integer> skillsIncrement = new HashMap();
    private Map<Integer, Integer> damageIncrease = new HashMap();
    private EnumMap<Element, Integer> elemBoosts = new EnumMap(Element.class);
    private List<Equip> durabilityHandling = new ArrayList();
    private List<Equip> equipLevelHandling = new ArrayList();
    private transient float shouldHealHP;
    private transient float shouldHealMP;
    public int level;
    public short str;
    public short dex;
    public short luk;
    public short int_;
    public int hp;
    public int maxhp;
    public int mp;
    public int maxmp;
    public int job;
    private transient short passive_sharpeye_min_percent;
    private transient short passive_sharpeye_percent;
    private transient short passive_sharpeye_rate;
    private transient byte passive_mastery;
    private transient int localstr;
    private transient int localdex;
    private transient int localluk;
    private transient int localint_;
    private transient int localmaxhp;
    private transient int localmaxmp;
    private transient int magic;
    private transient int watk;
    private transient int hands;
    private transient int accuracy;
    public transient boolean equippedWelcomeBackRing;
    public transient boolean hasClone;
    public transient boolean hasPartyBonus;
    public transient boolean Berserk;
    public transient boolean canFish;
    public transient boolean canFishVIP;
    public transient double expBuff;
    public transient double dropBuff;
    public transient double mesoBuff;
    public transient double cashBuff;
    public transient double mesoGuard;
    public transient double mesoGuardMeso;
    public transient double expMod;
    public transient double pickupRange;
    public transient double dam_r;
    public transient double bossdam_r;
    public transient int recoverHP;
    public transient int recoverMP;
    public transient int mpconReduce;
    public transient int mpconPercent;
    public transient int incMesoProp;
    public transient int reduceCooltime;
    public transient int incRewardProp;
    public transient int DAMreflect;
    public transient int DAMreflect_rate;
    public transient int ignoreDAMr;
    public transient int ignoreDAMr_rate;
    public transient int ignoreDAM;
    public transient int ignoreDAM_rate;
    public transient int mpRestore;
    public transient int hpRecover;
    public transient int hpRecoverProp;
    public transient int hpRecoverPercent;
    public transient int mpRecover;
    public transient int mpRecoverProp;
    public transient int RecoveryUP;
    public transient int BuffUP;
    public transient int RecoveryUP_Skill;
    public transient int BuffUP_Skill;
    public transient int incAllskill;
    public transient int combatOrders;
    public transient int ignoreTargetDEF;
    public transient int defRange;
    public transient int BuffUP_Summon;
    public transient int dodgeChance;
    public transient int speed;
    public transient int jump;
    public transient int harvestingTool;
    public transient int equipmentBonusExp;
    public transient int dropMod;
    public transient int cashMod;
    public transient int levelBonus;
    public transient int ASR;
    public transient int TER;
    public transient int pickRate;
    public transient int decreaseDebuff;
    public transient int equippedFairy;
    public transient int equippedSummon;
    public transient int percent_hp;
    public transient int percent_mp;
    public transient int percent_str;
    public transient int percent_dex;
    public transient int percent_int;
    public transient int percent_luk;
    public transient int percent_acc;
    public transient int percent_atk;
    public transient int percent_matk;
    public transient int percent_wdef;
    public transient int percent_mdef;
    public transient int pvpDamage;
    public transient int hpRecoverTime = 0;
    public transient int mpRecoverTime = 0;
    public transient int dot;
    public transient int dotTime;
    public transient int questBonus;
    public transient int pvpRank;
    public transient int pvpExp;
    public transient int wdef;
    public transient int mdef;
    public transient int trueMastery;
    private transient float localmaxbasedamage;
    private transient float localmaxbasepvpdamage;
    private transient float localmaxbasepvpdamageL;
    public transient int def;
    public transient int element_ice;
    public transient int element_fire;
    public transient int element_light;
    public transient int element_psn;
    private transient int equippedForce;
    private static final int[] allJobs = {0, 10000, 10000000, 20000000, 20010000, 20020000, 30000000, 30010000};
    public static final int[] pvpSkills = {1000007, 2000007, 3000006, 4000010, 5000006, 5010004, 11000006, 12000006, 13000005, 14000006, 15000005, 21000005, 22000002, 23000004, 31000005, 32000012, 33000004, 35000005};


    public void init(MapleCharacter chra) {
        recalcLocalStats(chra);
    }
    
    public int getLevel() {
        return this.level;
    }

    public short getStr() {
        return this.str;
    }

    public short getDex() {
        return this.dex;
    }

    public short getLuk() {
        return this.luk;
    }

    public short getInt() {
        return this.int_;
    }
    
    public int getHP() {
        return this.hp;
    }
    
    public int getMP() {
        return this.mp;
    }
    
    public int getJob() {
        return this.job;
    }
    
    public void setLevel(int level, MapleCharacter chra) {
        this.level = level;
        recalcLocalStats(chra);
    }
    
    public void setStr(short str, MapleCharacter chra) {
        this.str = str;
        recalcLocalStats(chra);
    }

    public void setDex(short dex, MapleCharacter chra) {
        this.dex = dex;
        recalcLocalStats(chra);
    }

    public void setLuk(short luk, MapleCharacter chra) {
        this.luk = luk;
        recalcLocalStats(chra);
    }

    public void setInt(short int_, MapleCharacter chra) {
        this.int_ = int_;
        recalcLocalStats(chra);
    }

    public boolean setHp(int newhp, MapleCharacter chra) {
        return setHp(newhp, false, chra);
    }

    public boolean setHp(int newhp, boolean silent, MapleCharacter chra) {
        int oldHp = this.hp;
        int thp = newhp;
        if (thp < 0) {
            thp = 0;
        }
        if (thp > this.localmaxhp) {
            thp = this.localmaxhp;
        }
        this.hp = thp;
        if (chra != null) {
            if (!silent) {
                chra.checkBerserk();
                chra.updatePartyMemberHP();
            }
            if ((oldHp > this.hp) && (!chra.isAlive())) {
                chra.playerDead();
            }
        }
        return this.hp != oldHp;
    }

    public boolean setMp(int newmp, MapleCharacter chra) {
        int oldMp = this.mp;
        int tmp = newmp;
        if (tmp < 0) {
            tmp = 0;
        }
        if (tmp > this.localmaxmp) {
            tmp = this.localmaxmp;
        }
        this.mp = tmp;
        return this.mp != oldMp;
    }

    public void setInfo(int maxhp, int maxmp, int hp, int mp) {
        this.maxhp = maxhp;
        this.maxmp = maxmp;
        this.hp = hp;
        this.mp = mp;
    }

    public void setMaxHp(int hp, MapleCharacter chra) {
        this.maxhp = hp;
        recalcLocalStats(chra);
    }

    public void setMaxMp(int mp, MapleCharacter chra) {
        this.maxmp = mp;
        recalcLocalStats(chra);
    }

    public int getHp() {
        return this.hp;
    }

    public int getMaxHp() {
        return this.maxhp;
    }

    public int getMp() {
        return this.mp;
    }

    public int getMaxMp() {
        return this.maxmp;
    }

    public int getTotalDex() {
        return this.localdex;
    }

    public int getTotalInt() {
        return this.localint_;
    }

    public int getTotalStr() {
        return this.localstr;
    }

    public int getTotalLuk() {
        return this.localluk;
    }

    public int getTotalMagic() {
        return this.magic;
    }

    public int getSpeed() {
        return this.speed;
    }

    public int getJump() {
        return this.jump;
    }

    public int getTotalWatk() {
        return this.watk;
    }

    public int getCurrentMaxHp() {
        return this.localmaxhp;
    }

    public int getCurrentMaxMp(int job) {
        if (GameConstants.is恶魔猎手(job)) {
            return GameConstants.getMPByJob(job);
        }
        return this.localmaxmp;
    }

    public int getEquippedForce() {
        return this.equippedForce;
    }

    public int getHands() {
        return this.hands;
    }

    public float getCurrentMaxBaseDamage() {
        return this.localmaxbasedamage;
    }

    public float getCurrentMaxBasePVPDamage() {
        return this.localmaxbasepvpdamage;
    }

    public float getCurrentMaxBasePVPDamageL() {
        return this.localmaxbasepvpdamageL;
    }

    public void recalcLocalStats(MapleCharacter chra) {
        recalcLocalStats(false, chra);
    }

    public void recalcLocalStats(boolean first_login, MapleCharacter chra) {
        if (chra.isClone()) {
            return;
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        int oldmaxhp = this.localmaxhp;
        int localmaxhp_ = getMaxHp();
        int localmaxmp_ = getMaxMp();
        this.accuracy = 0;
        this.wdef = 0;
        this.mdef = 0;
        this.localdex = getDex();
        this.localint_ = getInt();
        this.localstr = getStr();
        this.localluk = getLuk();
        this.speed = 100;
        this.jump = 100;
        this.pickupRange = 0.0D;
        this.decreaseDebuff = 0;
        this.ASR = 0;
        this.TER = 0;
        this.dot = 0;
        this.questBonus = 1;
        this.dotTime = 0;
        this.trueMastery = 0;
        this.percent_wdef = 0;
        this.percent_mdef = 0;
        this.percent_hp = 0;
        this.percent_mp = 0;
        this.percent_str = 0;
        this.percent_dex = 0;
        this.percent_int = 0;
        this.percent_luk = 0;
        this.percent_acc = 0;
        this.percent_atk = 0;
        this.percent_matk = 0;
        this.passive_sharpeye_rate = 5;
        this.passive_sharpeye_min_percent = 20;
        this.passive_sharpeye_percent = 50;
        this.magic = 0;
        this.watk = 0;
        if ((chra.getJob() == 500) || ((chra.getJob() >= 520) && (chra.getJob() <= 522))) {
            this.watk = 20;
        } else if ((chra.getJob() == 400) || ((chra.getJob() >= 410) && (chra.getJob() <= 412)) || ((chra.getJob() >= 1400) && (chra.getJob() <= 1412))) {
            this.watk = 30;
        }

        this.dodgeChance = 0;
        this.pvpDamage = 0;
        this.mesoGuard = 50.0D;
        this.mesoGuardMeso = 0.0D;
        this.dam_r = 100.0D;
        this.bossdam_r = 100.0D;
        this.expBuff = 100.0D;
        this.cashBuff = 100.0D;
        this.dropBuff = 100.0D;
        this.mesoBuff = 100.0D;
        this.recoverHP = 0;
        this.recoverMP = 0;
        this.mpconReduce = 0;
        this.mpconPercent = 100;
        this.incMesoProp = 0;
        this.reduceCooltime = 0;
        this.incRewardProp = 0;
        this.DAMreflect = 0;
        this.DAMreflect_rate = 0;
        this.ignoreDAMr = 0;
        this.ignoreDAMr_rate = 0;
        this.ignoreDAM = 0;
        this.ignoreDAM_rate = 0;
        this.ignoreTargetDEF = 0;
        this.hpRecover = 0;
        this.hpRecoverProp = 0;
        this.hpRecoverPercent = 0;
        this.mpRecover = 0;
        this.mpRecoverProp = 0;
        this.mpRestore = 0;
        this.pickRate = 0;
        this.equippedWelcomeBackRing = false;
        this.equippedFairy = 0;
        this.equippedSummon = 0;
        this.hasPartyBonus = false;
        this.hasClone = false;
        this.Berserk = false;
        this.canFish = GameConstants.GMS;
        this.canFishVIP = false;
        this.equipmentBonusExp = 0;
        this.RecoveryUP = 100;
        this.BuffUP = 100;
        this.RecoveryUP_Skill = 100;
        this.BuffUP_Skill = 100;
        this.BuffUP_Summon = 100;
        this.dropMod = 1;
        this.expMod = 1.0D;
        this.cashMod = 1;
        this.levelBonus = 0;
        this.incAllskill = 0;
        this.combatOrders = 0;
        this.defRange = 0;
        this.durabilityHandling.clear();
        this.equipLevelHandling.clear();
        this.skillsIncrement.clear();
        this.damageIncrease.clear();
        this.setHandling.clear();
        this.harvestingTool = 0;
        this.element_fire = 100;
        this.element_ice = 100;
        this.element_light = 100;
        this.element_psn = 100;
        this.def = 100;
        for (MapleTrait.MapleTraitType t : MapleTrait.MapleTraitType.values()) {
            chra.getTrait(t).clearLocalExp();
        }
        Iterator<Item> itera = chra.getInventory(MapleInventoryType.EQUIPPED).newList().iterator();
        while (itera.hasNext()) {
            Equip equip = (Equip) itera.next();
            if ((equip.getPosition() == -11)
                    && (GameConstants.isMagicWeapon(equip.getItemId()))) {
                Map eqstat = MapleItemInformationProvider.getInstance().getEquipStats(equip.getItemId());
                if (eqstat != null) {
                    if (eqstat.containsKey("incRMAF")) {
                        this.element_fire = ((Integer) eqstat.get("incRMAF")).intValue();
                    }
                    if (eqstat.containsKey("incRMAI")) {
                        this.element_ice = ((Integer) eqstat.get("incRMAI")).intValue();
                    }
                    if (eqstat.containsKey("incRMAL")) {
                        this.element_light = ((Integer) eqstat.get("incRMAL")).intValue();
                    }
                    if (eqstat.containsKey("incRMAS")) {
                        this.element_psn = ((Integer) eqstat.get("incRMAS")).intValue();
                    }
                    if (eqstat.containsKey("elemDefault")) {
                        this.def = ((Integer) eqstat.get("elemDefault")).intValue();
                    }
                }
            }

            if ((equip.getItemId() / 10000 == 166) && (equip.getAndroid() != null) && (chra.getAndroid() == null)) {
                chra.setAndroid(equip.getAndroid());
            }

            chra.getTrait(MapleTrait.MapleTraitType.craft).addLocalExp(equip.getHands());
            this.accuracy += equip.getAcc();
            localmaxhp_ += equip.getHp();
            localmaxmp_ += equip.getMp();
            this.localdex += equip.getDex();
            this.localint_ += equip.getInt();
            this.localstr += equip.getStr();
            this.localluk += equip.getLuk();
            this.magic += equip.getMatk();
            this.watk += equip.getWatk();
            this.wdef += equip.getWdef();
            this.mdef += equip.getMdef();
            this.speed += equip.getSpeed();
            this.jump += equip.getJump();
            this.pvpDamage += equip.getPVPDamage();
            switch (equip.getItemId()) {
                case 1112918:// 回归戒指
                    this.equippedWelcomeBackRing = true;
                    break;
                case 1122017:
                    this.equippedFairy = 10;
                    break;
                case 1122158:
                    this.equippedFairy = 5;
                    break;
                case 1112585:
                    this.equippedSummon = 1085;
                    break;
                case 1112586:
                    this.equippedSummon = 1087;
                    break;
                case 1112663:
                    this.equippedSummon = 1179;
                    break;
                default:
                    for (int eb_bonus : GameConstants.Equipments_Bonus) {
                        if (equip.getItemId() == eb_bonus) {
                            this.equipmentBonusExp += GameConstants.Equipment_Bonus_EXP(eb_bonus);
                            break;
                        }
                    }
            }

            this.percent_hp += ii.getItemIncMHPr(equip.getItemId());
            this.percent_mp += ii.getItemIncMMPr(equip.getItemId());
            Integer set = ii.getSetItemID(equip.getItemId());
            if ((set != null) && (set.intValue() > 0)) {
                int value = 1;
                if (this.setHandling.containsKey(set)) {
                    value += ((Integer) this.setHandling.get(set)).intValue();
                }
                this.setHandling.put(set, Integer.valueOf(value));
            }
            Iterator i$;
            if ((equip.getIncSkill() > 0) && (ii.getEquipSkills(equip.getItemId()) != null)) {
                for (i$ = ii.getEquipSkills(equip.getItemId()).iterator(); i$.hasNext();) {
                    int zzz = ((Integer) i$.next()).intValue();
                    Skill skil = SkillFactory.getSkill(zzz);
                    if ((skil != null) && (skil.canBeLearnedBy(chra.getJob()))) {
                        int value = 1;
                        if (this.skillsIncrement.get(Integer.valueOf(skil.getId())) != null) {
                            value += ((Integer) this.skillsIncrement.get(Integer.valueOf(skil.getId()))).intValue();
                        }
                        this.skillsIncrement.put(Integer.valueOf(skil.getId()), Integer.valueOf(value));
                    }
                }
            }
            EnumMap<EquipAdditions, Pair<Integer, Integer>> additions = ii.getEquipAdditions(equip.getItemId());
            if (additions != null) {
                for (Entry<EquipAdditions, Pair<Integer, Integer>> add : additions.entrySet()) {
                    switch (add.getKey()) {
                        case elemboost:
                            int value = add.getValue().right;
                            Element key = Element.getFromId(add.getValue().left);
                            if (this.elemBoosts.get(key) != null) {
                                value += elemBoosts.get(key);
                            }
                            this.elemBoosts.put(key, Integer.valueOf(value));
                            break;
                        case mobcategory:
                            this.dam_r *= (add.getValue().right + 100.0D) / 100.0D;
                            this.bossdam_r += (add.getValue().right + 100.0D) / 100.0D;
                            break;
                        case critical:
                            this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + ((Integer) ((Pair) add.getValue()).left).intValue());
                            this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent + ((Integer) ((Pair) add.getValue()).right).intValue());
                            this.passive_sharpeye_percent = (short) (this.passive_sharpeye_percent + ((Integer) ((Pair) add.getValue()).right).intValue());
                            break;
                        case boss:
                            this.bossdam_r *= (((Integer) ((Pair) add.getValue()).right).intValue() + 100.0D) / 100.0D;
                            break;
                        case mobdie:
                            if (((Integer) ((Pair) add.getValue()).left).intValue() > 0) {
                                this.hpRecover += ((Integer) ((Pair) add.getValue()).left).intValue();
                                this.hpRecoverProp += 5;
                            }
                            if (((Integer) ((Pair) add.getValue()).right).intValue() <= 0) {
                                break;
                            }
                            this.mpRecover += ((Integer) ((Pair) add.getValue()).right).intValue();
                            this.mpRecoverProp += 5;
                            break;
                        case skill:
                            if (!first_login) {
                                break;
                            }
                            chra.changeSkillLevel_Skip(SkillFactory.getSkill(((Integer) ((Pair) add.getValue()).left).intValue()), (byte) ((Integer) ((Pair) add.getValue()).right).intValue(), (byte) 0);
                            break;
                        case hpmpchange:
                            this.recoverHP += ((Integer) ((Pair) add.getValue()).left).intValue();
                            this.recoverMP += ((Integer) ((Pair) add.getValue()).right).intValue();
                    }
                }
            }

            if (equip.getState() >= 5) {
                int[] potentials = {equip.getPotential1(), equip.getPotential2(), equip.getPotential3()};
                for (int i : potentials) {
                    if (i > 0) {
                        StructItemOption soc = (StructItemOption) ii.getPotentialInfo(i).get(ii.getReqLevel(equip.getItemId()) / 10);
                        if (soc != null) {
                            localmaxhp_ += soc.get("incMHP");
                            localmaxmp_ += soc.get("incMMP");
                            handleItemOption(soc, chra, first_login);
                        }
                    }
                }
            }
            if (equip.getSocketState() >= 19) {
                int[] sockets = {equip.getSocket1(), equip.getSocket2(), equip.getSocket3()};
                for (int i : sockets) {
                    if (i > 0) {
                        StructItemOption soc = ii.getSocketInfo(i);
                        if (soc != null) {
                            localmaxhp_ += soc.get("incMHP");
                            localmaxmp_ += soc.get("incMMP");
                            handleItemOption(soc, chra, first_login);
                        }
                    }
                }
            }
            if (equip.getDurability() > 0) {
                this.durabilityHandling.add(equip);
            }
            if ((GameConstants.getMaxLevel(equip.getItemId()) > 0) && (GameConstants.getStatFromWeapon(equip.getItemId()) == null ? equip.getEquipLevel() <= GameConstants.getMaxLevel(equip.getItemId()) : equip.getEquipLevel() < GameConstants.getMaxLevel(equip.getItemId()))) {
                this.equipLevelHandling.add(equip);
            }
        }
        Iterator<Entry<Integer, Integer>> iter = this.setHandling.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<Integer, Integer> entry = (Map.Entry) iter.next();
            final StructSetItem set = ii.getSetItem(entry.getKey());
            if (set != null) {
                final Map<Integer, SetItem> itemz = set.getItems();
                for (Entry<Integer, SetItem> ent : itemz.entrySet()) {
                    if (ent.getKey() <= entry.getValue()) {
                        SetItem se = ent.getValue();
                        this.localstr += se.incSTR + se.incAllStat;
                        this.localdex += se.incDEX + se.incAllStat;
                        this.localint_ += se.incINT + se.incAllStat;
                        this.localluk += se.incLUK + se.incAllStat;
                        this.watk += se.incPAD;
                        this.magic += se.incMAD;
                        this.speed += se.incSpeed;
                        this.accuracy += se.incACC;
                        localmaxhp_ += se.incMHP;
                        localmaxmp_ += se.incMMP;
                        this.percent_hp += se.incMHPr;
                        this.percent_mp += se.incMMPr;
                        this.wdef += se.incPDD;
                        this.mdef += se.incMDD;
                        if ((se.option1 > 0) && (se.option1Level > 0)) {
                            StructItemOption soc = (StructItemOption) ii.getPotentialInfo(se.option1).get(se.option1Level);
                            if (soc != null) {
                                localmaxhp_ += soc.get("incMHP");
                                localmaxmp_ += soc.get("incMMP");
                                handleItemOption(soc, chra, first_login);
                            }
                        }
                        if ((se.option2 > 0) && (se.option2Level > 0)) {
                            StructItemOption soc = (StructItemOption) ii.getPotentialInfo(se.option2).get(se.option2Level);
                            if (soc != null) {
                                localmaxhp_ += soc.get("incMHP");
                                localmaxmp_ += soc.get("incMMP");
                                handleItemOption(soc, chra, first_login);
                            }
                        }
                    }
                }
            }
        }
        handleProfessionTool(chra);
        int hour = Calendar.getInstance().get(11);
        for (Item item : chra.getInventory(MapleInventoryType.CASH).newList()) {
            if (item.getItemId() / 100000 == 52) {
                if ((this.expMod < 3.0D) && (item.getItemId() == 5211060)) {
                    this.expMod = 3.0D;
                } else if ((this.expMod < 2.0D) && ((item.getItemId() == 5210000) || (item.getItemId() == 5210001) || (item.getItemId() == 5210002) || (item.getItemId() == 5210003) || (item.getItemId() == 5210004) || (item.getItemId() == 5210005) || (item.getItemId() == 5210006) || (item.getItemId() == 5211047))) {
                    this.expMod = 2.0D;
                } else if ((this.expMod < 1.5D) && ((item.getItemId() == 5211063) || (item.getItemId() == 5211064) || (item.getItemId() == 5211065) || (item.getItemId() == 5211066) || (item.getItemId() == 5211069) || (item.getItemId() == 5211070))) {
                    this.expMod = 1.5D;
                } else if ((this.expMod < 1.2D) && ((item.getItemId() == 5211071) || (item.getItemId() == 5211072) || (item.getItemId() == 5211073) || (item.getItemId() == 5211074) || (item.getItemId() == 5211075) || (item.getItemId() == 5211076) || (item.getItemId() == 5211067))) {
                    this.expMod = 1.2D;
                }
            } else if ((this.dropMod == 1) && (item.getItemId() / 10000 == 536)) {
                if ((item.getItemId() == 5360000) || (item.getItemId() == 5360014) || (item.getItemId() == 5360015) || (item.getItemId() == 5360016)) {
                    this.dropMod = 2;
                }
            } else if (item.getItemId() == 5650000) {
                this.hasPartyBonus = true;
            } else if (item.getItemId() == 5590001) {
                this.levelBonus = 10;
            } else if ((this.levelBonus == 0) && (item.getItemId() == 5590000)) {
                this.levelBonus = 5;
            } else if (item.getItemId() == 5710000) {
                this.questBonus = 2;
            } else if (item.getItemId() == 5340000) {
                this.canFish = true;
            } else if (item.getItemId() == 5340001) {
                this.canFish = true;
                this.canFishVIP = true;
            }
        }
        for (Item item : chra.getInventory(MapleInventoryType.ETC).list()) {
            switch (item.getItemId()) {
                case 4030003:
                    break;
                case 4030004:
                case 4030005:
            }

        }

        if ((first_login) && (chra.getLevel() >= 30)) {
            if (chra.isGM()) {
                for (int i = 0; i < allJobs.length; i++) {
                    chra.changeSkillLevel_Skip(SkillFactory.getSkill(1085 + allJobs[i]), 1, (byte) 0);
                    chra.changeSkillLevel_Skip(SkillFactory.getSkill(1087 + allJobs[i]), 1, (byte) 0);
                    chra.changeSkillLevel_Skip(SkillFactory.getSkill(1179 + allJobs[i]), 1, (byte) 0);
                }
            } else {
                chra.changeSkillLevel_Skip(SkillFactory.getSkill(getSkillByJob(1085, chra.getJob())), 1, (byte) 0);
                chra.changeSkillLevel_Skip(SkillFactory.getSkill(getSkillByJob(1087, chra.getJob())), 1, (byte) 0);
                chra.changeSkillLevel_Skip(SkillFactory.getSkill(getSkillByJob(1179, chra.getJob())), 1, (byte) 0);
            }
        }
        if (this.equippedSummon > 0) {
            this.equippedSummon = getSkillByJob(this.equippedSummon, chra.getJob());
        }

        this.localstr = (int) (this.localstr + Math.floor(this.localstr * this.percent_str / 100.0F));
        this.localdex = (int) (this.localdex + Math.floor(this.localdex * this.percent_dex / 100.0F));
        this.localint_ = (int) (this.localint_ + Math.floor(this.localint_ * this.percent_int / 100.0F));
        this.localluk = (int) (this.localluk + Math.floor(this.localluk * this.percent_luk / 100.0F));

        if (this.localint_ > this.localdex) {
            this.accuracy = (int) (this.accuracy + (this.localint_ + Math.floor(this.localluk * 1.2D)));
        } else {
            this.accuracy = (int) (this.accuracy + (this.localluk + Math.floor(this.localdex * 1.2D)));
        }
        this.wdef = (int) (this.wdef + Math.floor(this.localstr * 1.2D + (this.localdex + this.localluk) * 0.5D + this.localint_ * 0.4D));
        this.mdef = (int) (this.mdef + Math.floor(this.localstr * 0.4D + (this.localdex + this.localluk) * 0.5D + this.localint_ * 1.2D));
        this.accuracy = (int) (this.accuracy + Math.floor(this.accuracy * this.percent_acc / 100.0F));
        Skill bx;
        int bof;
        MapleStatEffect eff = chra.getStatForBuff(MapleBuffStat.骑兽技能);
        if ((eff != null) && (eff.getSourceId() == 33001001)) {
            this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + eff.getW());
            this.percent_hp += eff.getZ();
        }

        Integer buff = chra.getBuffedValue(MapleBuffStat.幸运骰子);
        if (buff != null) {
            this.percent_wdef += GameConstants.getDiceStat(buff.intValue(), 2);
            this.percent_mdef += GameConstants.getDiceStat(buff.intValue(), 2);
            this.percent_hp += GameConstants.getDiceStat(buff.intValue(), 3);
            this.percent_mp += GameConstants.getDiceStat(buff.intValue(), 3);
            this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + GameConstants.getDiceStat(buff.intValue(), 4));
            this.dam_r *= (GameConstants.getDiceStat(buff.intValue(), 5) + 100.0D) / 100.0D;
            this.bossdam_r *= (GameConstants.getDiceStat(buff.intValue(), 5) + 100.0D) / 100.0D;
            this.expBuff *= (GameConstants.getDiceStat(buff.intValue(), 6) + 100.0D) / 100.0D;
        }
        buff = chra.getBuffedValue(MapleBuffStat.终极无限);
        if (buff != null) {
            this.percent_matk += buff.intValue() - 1;
        }
        buff = chra.getBuffedValue(MapleBuffStat.玛瑙的保佑);
        if (buff != null) {
            this.dodgeChance += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStat.PVP_DAMAGE);
        if (buff != null) {
            this.pvpDamage += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStat.PVP_ATTACK);
        if (buff != null) {
            this.pvpDamage += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStat.暴走形态);
        if (buff != null) {
            this.percent_hp += buff.intValue();
        }
        eff = chra.getStatForBuff(MapleBuffStat.蓝色灵气);
        if (eff != null) {
            this.percent_wdef += eff.getZ() + eff.getY();
            this.percent_mdef += eff.getZ() + eff.getY();
        }
        buff = chra.getBuffedValue(MapleBuffStat.幻灵转化);
        if (buff != null) {
            this.percent_hp += buff.intValue();
        } else {
            buff = chra.getBuffedValue(MapleBuffStat.MAXHP);
            if (buff != null) {
                this.percent_hp += buff.intValue();
            }
        }
        buff = chra.getBuffedValue(MapleBuffStat.MAXMP);
        if (buff != null) {
            this.percent_mp += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStat.MP_BUFF);
        if (buff != null) {
            this.percent_mp += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStat.百分比MaxHp);
        if (buff != null) {
            this.percent_hp += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStat.百分比MaxMp);
        if (buff != null) {
            this.percent_mp += buff.intValue();
        }
        buff = chra.getBuffedSkill_X(MapleBuffStat.属性抗性);
        if (buff != null) {
            this.BuffUP_Skill += buff.intValue();
        }
        if (GameConstants.is骑士团(chra.getJob())) {
            bx = SkillFactory.getSkill(10000074);
            bof = chra.getSkillLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                this.percent_hp += eff.getX();
                this.percent_mp += eff.getX();
            }
        }
        switch (chra.getJob()) {
            case 200:
            case 210:
            case 211:
            case 212:
            case 220:
            case 221:
            case 222:
            case 230:
            case 231:
            case 232:
                bx = SkillFactory.getSkill(2000006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof <= 0) {
                    break;
                }
                this.percent_mp += bx.getEffect(bof).getPercentMP();
                break;
            case 1200:
            case 1210:
            case 1211:
            case 1212:
                bx = SkillFactory.getSkill(12000005);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    this.percent_mp += bx.getEffect(bof).getPercentMP();
                }

                bx = SkillFactory.getSkill(12100008);
                bof = chra.getTotalSkillLevel(bx);
                if (bof <= 0) {
                    break;
                }
                this.localint_ += bx.getEffect(bof).getIntX();
                break;
            case 1100:
            case 1110:
            case 1111:
            case 1112:
                bx = SkillFactory.getSkill(11000005);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    this.percent_hp += bx.getEffect(bof).getPercentHP();
                }

                bx = SkillFactory.getSkill(11100007);
                bof = chra.getTotalSkillLevel(bx);
                if (bof <= 0) {
                    break;
                }
                eff = bx.getEffect(bof);
                this.localstr += eff.getStrX();
                this.localdex += eff.getDexX();
                break;
            case 501:
            case 530:
            case 531:
            case 532:
                this.defRange = 200;
                bx = SkillFactory.getSkill(5010003);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    this.watk += bx.getEffect(bof).getAttackX();
                }
                bx = SkillFactory.getSkill(5300008);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.localstr += eff.getStrX();
                    this.localdex += eff.getDexX();
                }
                bx = SkillFactory.getSkill(5311001);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    this.damageIncrease.put(Integer.valueOf(5301001), Integer.valueOf(bx.getEffect(bof).getDAMRate()));
                }
                bx = SkillFactory.getSkill(5310007);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.percent_hp += eff.getPercentHP();
                    this.ASR += eff.getASRRate();
                    this.percent_wdef += eff.getWDEFRate();
                }
                bx = SkillFactory.getSkill(5310006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    this.watk += bx.getEffect(bof).getAttackX();
                }
                bx = SkillFactory.getSkill(5320009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof <= 0) {
                    break;
                }
                eff = bx.getEffect(bof);
                this.dam_r *= (eff.getDAMRate() + 100.0D) / 100.0D;
                this.bossdam_r *= (eff.getDAMRate() + 100.0D) / 100.0D;
                this.ignoreTargetDEF += eff.getIgnoreMob();
                break;
            case 570:
            case 571:
            case 572:
                bx = SkillFactory.getSkill(5700003);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.localstr += eff.getStrX();
                    this.localdex += eff.getDexX();
                }
                bx = SkillFactory.getSkill(5710004);
                bof = chra.getTotalSkillLevel(bx);
                if (bof <= 0) {
                    break;
                }
                eff = bx.getEffect(bof);
                this.percent_wdef += eff.getWDEFRate();
                this.percent_mdef += eff.getMDEFRate();
                localmaxhp_ += eff.getMaxHpX();
                localmaxmp_ += eff.getMaxMpX();
                break;
            case 3001:
            case 3100:
            case 3110:
            case 3111:
            case 3112:
                this.mpRecoverProp = 100;
                bx = SkillFactory.getSkill(31000003);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    this.percent_hp += bx.getEffect(bof).getPercentHP();
                }
                bx = SkillFactory.getSkill(31100007);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.damageIncrease.put(Integer.valueOf(31000004), Integer.valueOf(eff.getDAMRate()));
                    this.damageIncrease.put(Integer.valueOf(31001006), Integer.valueOf(eff.getDAMRate()));
                    this.damageIncrease.put(Integer.valueOf(31001007), Integer.valueOf(eff.getDAMRate()));
                    this.damageIncrease.put(Integer.valueOf(31001008), Integer.valueOf(eff.getDAMRate()));
                }
                bx = SkillFactory.getSkill(31100005);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.localstr += eff.getStrX();
                    this.localdex += eff.getDexX();
                }
                bx = SkillFactory.getSkill(31110010);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.damageIncrease.put(Integer.valueOf(31000004), Integer.valueOf(eff.getX()));
                    this.damageIncrease.put(Integer.valueOf(31001006), Integer.valueOf(eff.getX()));
                    this.damageIncrease.put(Integer.valueOf(31001007), Integer.valueOf(eff.getX()));
                    this.damageIncrease.put(Integer.valueOf(31001008), Integer.valueOf(eff.getX()));
                }
                bx = SkillFactory.getSkill(31110007);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.dam_r *= (eff.getDAMRate() + 100.0D) / 100.0D;
                    this.bossdam_r *= (eff.getDAMRate() + 100.0D) / 100.0D;
                }
                bx = SkillFactory.getSkill(31110008);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.dodgeChance += eff.getX();
                    this.hpRecoverPercent += eff.getY();
                    this.hpRecoverProp += eff.getX();
                    this.mpRecover += eff.getY();
                    this.mpRecoverProp += eff.getX();
                }
                bx = SkillFactory.getSkill(31110009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.mpRecover += 1;
                    this.mpRecoverProp += eff.getProb();
                }
                bx = SkillFactory.getSkill(31110006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.dam_r *= (eff.getX() + 100.0D) / 100.0D;
                    this.bossdam_r *= (eff.getX() + 100.0D) / 100.0D;
                    this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + eff.getY());
                }
                bx = SkillFactory.getSkill(31121006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    this.ignoreTargetDEF += bx.getEffect(bof).getIgnoreMob();
                }
                bx = SkillFactory.getSkill(31120011);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.damageIncrease.put(Integer.valueOf(31000004), Integer.valueOf(eff.getX()));
                    this.damageIncrease.put(Integer.valueOf(31001006), Integer.valueOf(eff.getX()));
                    this.damageIncrease.put(Integer.valueOf(31001007), Integer.valueOf(eff.getX()));
                    this.damageIncrease.put(Integer.valueOf(31001008), Integer.valueOf(eff.getX()));
                }
                bx = SkillFactory.getSkill(31120008);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.watk += eff.getAttackX();
                    this.trueMastery += eff.getMastery();
                    this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent + eff.getCriticalMin());
                }
                bx = SkillFactory.getSkill(31120009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    this.percent_wdef += bx.getEffect(bof).getT();
                }
                bx = SkillFactory.getSkill(30010112);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.bossdam_r += eff.getBossDamage();
                    this.mpRecover += eff.getX();
                    this.mpRecoverProp += eff.getBossDamage();
                }
                bx = SkillFactory.getSkill(30010185);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    chra.getTrait(MapleTrait.MapleTraitType.will).addLocalExp(GameConstants.getTraitExpNeededForLevel(eff.getY()));
                    chra.getTrait(MapleTrait.MapleTraitType.charisma).addLocalExp(GameConstants.getTraitExpNeededForLevel(eff.getZ()));
                }
                bx = SkillFactory.getSkill(30010111);
                bof = chra.getTotalSkillLevel(bx);
                if (bof <= 0) {
                    break;
                }
                eff = bx.getEffect(bof);
                this.hpRecoverPercent += eff.getX();
                this.hpRecoverProp += eff.getProb();
                break;
            case 580:
            case 581:
            case 582:
                bx = SkillFactory.getSkill(5800009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof <= 0) {
                    break;
                }
                this.percent_hp += bx.getEffect(bof).getPercentHP();
                break;
            case 1510:
            case 1511:
            case 1512:
                bx = SkillFactory.getSkill(15000008);
                bof = chra.getTotalSkillLevel(bx);
                if (bof <= 0) {
                    break;
                }
                this.percent_hp += bx.getEffect(bof).getPercentHP();
                break;
            case 400:
            case 410:
            case 411:
            case 412:
                this.defRange = 200;

                bx = SkillFactory.getSkill(4000010);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.percent_hp += eff.getPercentHP();
                    this.ASR += eff.getASRRate();
                }
                bx = SkillFactory.getSkill(4100007);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.localluk += eff.getLukX();
                    this.localdex += eff.getDexX();
                }
                bx = SkillFactory.getSkill(4110008);
                bof = chra.getTotalSkillLevel(bx);
                if (bof <= 0) {
                    break;
                }
                eff = bx.getEffect(bof);
                this.percent_hp += eff.getPercentHP();
                this.ASR += eff.getASRRate();
                break;
            case 420:
            case 421:
            case 422:
                bx = SkillFactory.getSkill(4200007);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.localluk += eff.getLukX();
                    this.localdex += eff.getDexX();
                }
                bx = SkillFactory.getSkill(4210013);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.percent_hp += eff.getPercentHP();
                    this.ASR += eff.getASRRate();
                }
                bx = SkillFactory.getSkill(4200010);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.percent_wdef += eff.getX();
                    this.percent_mdef += eff.getX();
                }
                bx = SkillFactory.getSkill(4210012);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.mesoBuff *= (eff.getMesoRate() + 100.0D) / 100.0D;
                    this.pickRate += eff.getU();
                    this.mesoGuard -= eff.getV();
                    this.mesoGuardMeso -= eff.getW();
                    this.damageIncrease.put(Integer.valueOf(4211006), Integer.valueOf(eff.getX()));
                }
                bx = SkillFactory.getSkill(4221013);
                bof = chra.getTotalSkillLevel(bx);
                if (bof <= 0) {
                    break;
                }
                this.ignoreTargetDEF += bx.getEffect(bof).getIgnoreMob();
                break;
            case 431:
            case 432:
            case 433:
            case 434:
                bx = SkillFactory.getSkill(4310006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.localluk += eff.getLukX();
                    this.localdex += eff.getDexX();
                }
                bx = SkillFactory.getSkill(4330008);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.percent_hp += eff.getPercentHP();
                    this.ASR += eff.getASRRate();
                }
                bx = SkillFactory.getSkill(4341006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof <= 0) {
                    break;
                }
                eff = bx.getEffect(bof);
                this.percent_wdef += eff.getWDEFRate();
                this.percent_mdef += eff.getMDEFRate();
                break;
            case 100:
            case 110:
            case 111:
            case 112:
            case 120:
            case 121:
            case 122:
            case 130:
            case 131:
            case 132:
                bx = SkillFactory.getSkill(1000006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    this.percent_hp += bx.getEffect(bof).getPercentHP();
                }
                bx = SkillFactory.getSkill(1210001);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.percent_wdef += eff.getX();
                    this.percent_mdef += eff.getX();
                }
                bx = SkillFactory.getSkill(1220005);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    this.percent_wdef += bx.getEffect(bof).getT();
                }
                bx = SkillFactory.getSkill(1220010);
                bof = chra.getTotalSkillLevel(bx);
                if (bof <= 0) {
                    break;
                }
                this.trueMastery += bx.getEffect(bof).getMastery();
                break;
            case 322:
                bx = SkillFactory.getSkill(3220004);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.watk += eff.getX();
                    this.trueMastery += eff.getMastery();
                    this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent + eff.getCriticalMin());
                }
                bx = SkillFactory.getSkill(3220009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);

                    this.ignoreTargetDEF += eff.getIgnoreMob();
                }
                bx = SkillFactory.getSkill(3220005);
                bof = chra.getTotalSkillLevel(bx);
                if ((bof <= 0) || (chra.getBuffedValue(MapleBuffStat.精神连接) == null)) {
                    break;
                }
                eff = bx.getEffect(bof);
                this.percent_hp += eff.getX();
                this.dam_r *= (eff.getDamage() + 100.0D) / 100.0D;
                this.bossdam_r *= (eff.getDamage() + 100.0D) / 100.0D;
                break;
            case 312:
                bx = SkillFactory.getSkill(3120005);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    this.watk += bx.getEffect(bof).getX();
                }
                bx = SkillFactory.getSkill(3120011);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);

                    this.ignoreTargetDEF += eff.getIgnoreMob();
                }
                bx = SkillFactory.getSkill(3120006);
                bof = chra.getTotalSkillLevel(bx);
                if ((bof <= 0) || (chra.getBuffedValue(MapleBuffStat.精神连接) == null)) {
                    break;
                }
                eff = bx.getEffect(bof);
                this.percent_hp += eff.getX();
                this.dam_r *= (eff.getDamage() + 100.0D) / 100.0D;
                this.bossdam_r *= (eff.getDamage() + 100.0D) / 100.0D;
                break;
            case 3510:
            case 3511:
            case 3512:
                this.defRange = 200;
                bx = SkillFactory.getSkill(35100011);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.localstr += eff.getStrX();
                    this.localdex += eff.getDexX();
                }
                bx = SkillFactory.getSkill(35100000);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    this.watk += bx.getEffect(bof).getAttackX();
                }
                bx = SkillFactory.getSkill(35120000);
                bof = chra.getTotalSkillLevel(bx);
                if (bof <= 0) {
                    break;
                }
                this.trueMastery += bx.getEffect(bof).getMastery();
                break;
            case 3211:
            case 3212:
                bx = SkillFactory.getSkill(32110000);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    this.ASR += bx.getEffect(bof).getASRRate();
                }
                bx = SkillFactory.getSkill(32110001);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.dam_r *= (eff.getDAMRate() + 100.0D) / 100.0D;
                    this.bossdam_r *= (eff.getDAMRate() + 100.0D) / 100.0D;
                    this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent + eff.getCriticalMin());
                }
                bx = SkillFactory.getSkill(32120000);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    this.magic += bx.getEffect(bof).getMagicX();
                }
                bx = SkillFactory.getSkill(32120001);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    this.dodgeChance += bx.getEffect(bof).getER();
                }
                bx = SkillFactory.getSkill(32120009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof <= 0) {
                    break;
                }
                this.percent_hp += bx.getEffect(bof).getPercentHP();
                break;
            case 3300:
            case 3310:
            case 3311:
            case 3312:
                this.defRange = 200;

                bx = SkillFactory.getSkill(33100010);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.localstr += eff.getStrX();
                    this.localdex += eff.getDexX();
                }
                bx = SkillFactory.getSkill(33120000);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.watk += eff.getX();
                    this.trueMastery += eff.getMastery();
                    this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent + eff.getCriticalMin());
                }
                bx = SkillFactory.getSkill(33110000);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.dam_r *= (eff.getDamage() + 100.0D) / 100.0D;
                    this.bossdam_r *= (eff.getDamage() + 100.0D) / 100.0D;
                }
                bx = SkillFactory.getSkill(33120010);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.ignoreTargetDEF += eff.getIgnoreMob();
                    this.dodgeChance += eff.getER();
                }
                bx = SkillFactory.getSkill(32110001);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.dam_r *= (eff.getDAMRate() + 100.0D) / 100.0D;
                    this.bossdam_r *= (eff.getDAMRate() + 100.0D) / 100.0D;
                }
                bx = SkillFactory.getSkill(33120011);
                bof = chra.getTotalSkillLevel(bx);
                if (bof <= 0) {
                    break;
                }
                eff = bx.getEffect(bof);
                this.watk += eff.getAttackX();
                this.damageIncrease.put(Integer.valueOf(33100009), Integer.valueOf(eff.getDamage()));
                break;
            case 2200:
            case 2210:
            case 2211:
            case 2212:
            case 2213:
            case 2214:
            case 2215:
            case 2216:
            case 2217:
            case 2218:
                this.magic += chra.getTotalSkillLevel(SkillFactory.getSkill(22000000));
                bx = SkillFactory.getSkill(22150000);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.mpconPercent += eff.getX() - 100;
                    this.dam_r *= eff.getY() / 100.0D;
                    this.bossdam_r *= eff.getY() / 100.0D;
                }
                bx = SkillFactory.getSkill(22160000);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.dam_r *= (eff.getDamage() + 100.0D) / 100.0D;
                    this.bossdam_r *= (eff.getDamage() + 100.0D) / 100.0D;
                }
                bx = SkillFactory.getSkill(22170001);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.magic += eff.getX();
                    this.trueMastery += eff.getMastery();
                    this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent + eff.getCriticalMin());
                }

                bx = SkillFactory.getSkill(22120001);
                bof = chra.getTotalSkillLevel(bx);
                if (bof <= 0) {
                    break;
                }
                this.localint_ += bx.getEffect(bof).getIntX();
                break;
            case 2112:
                bx = SkillFactory.getSkill(21120001);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.watk += eff.getX();
                    this.trueMastery += eff.getMastery();
                    this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent + eff.getCriticalMin());
                }
                bx = SkillFactory.getSkill(21120012);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.watk += eff.getAttackX();
                    this.damageIncrease.put(Integer.valueOf(21100010), Integer.valueOf(eff.getDamage()));
                }
                bx = SkillFactory.getSkill(21120004);
                bof = chra.getTotalSkillLevel(bx);
                if (bof <= 0) {
                    break;
                }
                this.percent_hp += bx.getEffect(bof).getPercentHP();
                this.percent_wdef += bx.getEffect(bof).getT();
                break;
            case 1411:
            case 1412:
                bx = SkillFactory.getSkill(14110009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof <= 0) {
                    break;
                }
                eff = bx.getEffect(bof);
                this.percent_hp += eff.getPercentHP();
                this.ASR += eff.getASRRate();
                break;
            case 2400:
            case 2410:
            case 2411:
            case 2412:
                bx = SkillFactory.getSkill(24001002);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.speed += eff.getPassiveSpeed();
                    this.jump += eff.getPassiveJump();
                }
                bx = SkillFactory.getSkill(24000003);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.dodgeChance += eff.getX();
                }
                bx = SkillFactory.getSkill(24100006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.localluk += eff.getLukX();
                }
                bx = SkillFactory.getSkill(24111002);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.localluk += eff.getLukX();
                }
                bx = SkillFactory.getSkill(24111006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.damageIncrease.put(Integer.valueOf(24101002), Integer.valueOf(eff.getDAMRate()));
                }
                bx = SkillFactory.getSkill(24121003);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.damageIncrease.put(Integer.valueOf(24111006), Integer.valueOf(eff.getDAMRate()));
                    this.damageIncrease.put(Integer.valueOf(24111008), Integer.valueOf(eff.getDAMRate()));
                }
                bx = SkillFactory.getSkill(24120006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.watk += eff.getAttackX();
                    this.trueMastery += eff.getMastery();
                    this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent + eff.getCriticalMin());
                }
                bx = SkillFactory.getSkill(24120002);
                bof = chra.getTotalSkillLevel(bx);
                if (bof <= 0) {
                    break;
                }
                eff = bx.getEffect(bof);
                this.dodgeChance += eff.getX();
        }

        this.watk = (int) (this.watk + Math.floor(this.watk * this.percent_atk / 100.0F));
        this.magic = (int) (this.magic + Math.floor(this.magic * this.percent_matk / 100.0F));
        this.localint_ = (int) (this.localint_ + Math.floor(this.localint_ * this.percent_matk / 100.0F));
        bx = SkillFactory.getSkill(80000000);
        bof = chra.getSkillLevel(bx);
        if (bof > 0) {
            eff = bx.getEffect(bof);
            this.localstr += eff.getStrX();
            this.localdex += eff.getDexX();
            this.localint_ += eff.getIntX();
            this.localluk += eff.getLukX();
            this.percent_hp = (int) (this.percent_hp + eff.getHpR());
            this.percent_mp = (int) (this.percent_mp + eff.getMpR());
        }
        bx = SkillFactory.getSkill(80000001);
        bof = chra.getSkillLevel(bx);
        if (bof > 0) {
            eff = bx.getEffect(bof);
            this.bossdam_r += eff.getBossDamage();
        }
        bx = SkillFactory.getSkill(80000002);
        bof = chra.getSkillLevel(bx);
        if (bof > 0) {
            eff = bx.getEffect(bof);
            this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + eff.getCr());
        }
        bx = SkillFactory.getSkill(80001040);
        bof = chra.getSkillLevel(bx);
        if ((bof <= 0)
                || (GameConstants.is冒险家(chra.getJob()))) {
            bx = SkillFactory.getSkill(74);
            bof = chra.getSkillLevel(bx);
            if (bof > 0) {
                this.levelBonus += bx.getEffect(bof).getX();
            }
            bx = SkillFactory.getSkill(80);
            bof = chra.getSkillLevel(bx);
            if (bof > 0) {
                this.levelBonus += bx.getEffect(bof).getX();
            }
            bx = SkillFactory.getSkill(10074);
            bof = chra.getSkillLevel(bx);
            if (bof > 0) {
                this.levelBonus += bx.getEffect(bof).getX();
            }
            bx = SkillFactory.getSkill(10080);
            bof = chra.getSkillLevel(bx);
            if (bof > 0) {
                this.levelBonus += bx.getEffect(bof).getX();
            }
            bx = SkillFactory.getSkill(110);
            bof = chra.getSkillLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                this.localstr += eff.getStrX();
                this.localdex += eff.getDexX();
                this.localint_ += eff.getIntX();
                this.localluk += eff.getLukX();
                this.percent_hp = (int) (this.percent_hp + eff.getHpR());
                this.percent_mp = (int) (this.percent_mp + eff.getMpR());
            }
            bx = SkillFactory.getSkill(10110);
            bof = chra.getSkillLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                this.localstr += eff.getStrX();
                this.localdex += eff.getDexX();
                this.localint_ += eff.getIntX();
                this.localluk += eff.getLukX();
                this.percent_hp = (int) (this.percent_hp + eff.getHpR());
                this.percent_mp = (int) (this.percent_mp + eff.getMpR());
            }
        }
        long now;
        if (chra.getGuildId() > 0) {
            MapleGuild g = Guild.getGuild(chra.getGuildId());
            if ((g != null) && (g.getSkills().size() > 0)) {
                now = System.currentTimeMillis();
                for (MapleGuildSkill gs : g.getSkills()) {
                    if ((gs.timestamp > now) && (gs.activator.length() > 0)) {
                        MapleStatEffect e = SkillFactory.getSkill(gs.skillID).getEffect(gs.level);
                        this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + e.getCr());
                        this.watk += e.getAttackX();
                        this.magic += e.getMagicX();
                        this.expBuff *= (e.getEXPRate() + 100.0D) / 100.0D;
                        this.dodgeChance += e.getER();
                        this.percent_wdef += e.getWDEFRate();
                        this.percent_mdef += e.getMDEFRate();
                    }
                }
            }
        }
        localmaxhp_ = (int) (localmaxhp_ + Math.floor(this.percent_hp * localmaxhp_ / 100.0F));
        localmaxmp_ = (int) (localmaxmp_ + Math.floor(this.percent_mp * localmaxmp_ / 100.0F));
        this.wdef = (int) (this.wdef + Math.min(30000.0D, Math.floor(this.wdef * this.percent_wdef / 100.0F)));
        this.mdef = (int) (this.mdef + Math.min(30000.0D, Math.floor(this.wdef * this.percent_mdef / 100.0F)));

        buff = chra.getBuffedValue(MapleBuffStat.力量);
        if (buff != null) {
            this.localstr += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStat.敏捷);
        if (buff != null) {
            this.localdex += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStat.智力);
        if (buff != null) {
            this.localint_ += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStat.运气);
        if (buff != null) {
            this.localluk += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStat.ANGEL_STAT);
        if (buff != null) {
            this.localstr += buff.intValue();
            this.localdex += buff.intValue();
            this.localint_ += buff.intValue();
            this.localluk += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStat.增强_MAXHP);
        if (buff != null) {
            localmaxhp_ += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStat.增强_MAXMP);
        if (buff != null) {
            localmaxmp_ += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStat.增强_物理防御);
        if (buff != null) {
            this.wdef += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStat.增强_魔法防御);
        if (buff != null) {
            this.mdef += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStat.物理防御);
        if (buff != null) {
            this.wdef += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStat.物理防御);
        if (buff != null) {
            this.mdef += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStat.HP_BOOST);
        if (buff != null) {
            localmaxhp_ += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStat.MP_BOOST);
        if (buff != null) {
            localmaxmp_ += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStat.冒险岛勇士);
        if (buff != null) {
            double d = buff.doubleValue() / 100.0D;
            this.localstr = (int) (this.localstr + d * this.str);
            this.localdex = (int) (this.localdex + d * this.dex);
            this.localluk = (int) (this.localluk + d * this.luk);
            this.localint_ = (int) (this.localint_ + d * this.int_);
        }
        buff = chra.getBuffedValue(MapleBuffStat.英雄回声);
        if (buff != null) {
            double d = buff.doubleValue() / 100.0D;
            this.watk += (int) (this.watk * d);
            this.magic += (int) (this.magic * d);
        }
        buff = chra.getBuffedValue(MapleBuffStat.矛连击强化);
        if (buff != null) {
            this.watk += buff.intValue() / 10;
        }
        buff = chra.getBuffedValue(MapleBuffStat.金钱护盾);
        if (buff != null) {
            this.mesoGuardMeso += buff.doubleValue();
        }
        bx = SkillFactory.getSkill(GameConstants.getBOF_ForJob(chra.getJob()));
        bof = chra.getSkillLevel(bx);
        if (bof > 0) {
            eff = bx.getEffect(bof);
            this.watk += eff.getX();
            this.magic += eff.getY();
            this.accuracy += eff.getX();
        }
        bx = SkillFactory.getSkill(GameConstants.getEmpress_ForJob(chra.getJob()));
        bof = chra.getSkillLevel(bx);
        if (bof > 0) {
            eff = bx.getEffect(bof);
            this.watk += eff.getX();
            this.magic += eff.getY();
            this.accuracy += eff.getZ();
        }
        buff = chra.getBuffedValue(MapleBuffStat.EXPRATE);
        if (buff != null) {
            this.expBuff *= buff.doubleValue() / 100.0D;
        }
        buff = chra.getBuffedValue(MapleBuffStat.DROP_RATE);
        if (buff != null) {
            this.dropBuff *= buff.doubleValue() / 100.0D;
        }
        buff = chra.getBuffedValue(MapleBuffStat.ACASH_RATE);
        if (buff != null) {
            this.cashBuff *= buff.doubleValue() / 100.0D;
        }
        buff = chra.getBuffedValue(MapleBuffStat.MESO_RATE);
        if (buff != null) {
            this.mesoBuff *= buff.doubleValue() / 100.0D;
        }
        buff = chra.getBuffedValue(MapleBuffStat.聚财术);
        if (buff != null) {
            this.mesoBuff *= buff.doubleValue() / 100.0D;
        }
        buff = chra.getBuffedValue(MapleBuffStat.命中率);
        if (buff != null) {
            this.accuracy += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStat.ANGEL_ACC);
        if (buff != null) {
            this.accuracy += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStat.ANGEL_ATK);
        if (buff != null) {
            this.watk += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStat.ANGEL_MATK);
        if (buff != null) {
            this.magic += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStat.物理攻击);
        if (buff != null) {
            this.watk += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStat.精神注入);
        if (buff != null) {
            this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + buff.intValue());
            this.dam_r *= (buff.intValue() + 100.0D) / 100.0D;
            this.bossdam_r *= (buff.intValue() + 100.0D) / 100.0D;
        }
        buff = chra.getBuffedValue(MapleBuffStat.增强_物理攻击);
        if (buff != null) {
            this.watk += buff.intValue();
        }
        eff = chra.getStatForBuff(MapleBuffStat.能量获得);
        if (eff != null) {
            this.watk += eff.getWatk();
            this.accuracy += eff.getAcc();
        }
        buff = chra.getBuffedValue(MapleBuffStat.魔法攻击);
        if (buff != null) {
            this.magic += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStat.移动速度);
        if (buff != null) {
            this.speed += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStat.跳跃力);
        if (buff != null) {
            this.jump += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStat.疾驰速度);
        if (buff != null) {
            this.speed += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStat.疾驰跳跃);
        if (buff != null) {
            this.jump += buff.intValue();
        }
        eff = chra.getStatForBuff(MapleBuffStat.潜力解放);
        if (eff != null) {
            this.passive_sharpeye_rate = 100;
            this.ASR = 100;
            this.wdef += eff.getX();
            this.mdef += eff.getX();
            this.watk += eff.getX();
            this.magic += eff.getX();
        }
        buff = chra.getBuffedValue(MapleBuffStat.DAMAGE_BUFF);
        if (buff != null) {
            this.dam_r *= (buff.doubleValue() + 100.0D) / 100.0D;
            this.bossdam_r *= (buff.doubleValue() + 100.0D) / 100.0D;
        }
        buff = chra.getBuffedSkill_Y(MapleBuffStat.终极斩);
        if (buff != null) {
            this.dam_r *= buff.doubleValue() / 100.0D;
            this.bossdam_r *= buff.doubleValue() / 100.0D;
        }
        buff = chra.getBuffedSkill_Y(MapleBuffStat.死亡猫头鹰);
        if (buff != null) {
            this.dam_r *= buff.doubleValue() / 100.0D;
            this.bossdam_r *= buff.doubleValue() / 100.0D;
        }
        buff = chra.getBuffedSkill_X(MapleBuffStat.狂暴战魂);
        if (buff != null) {
            this.dam_r *= buff.doubleValue() / 100.0D;
            this.bossdam_r *= buff.doubleValue() / 100.0D;
        }
        eff = chra.getStatForBuff(MapleBuffStat.牧师祝福);
        if (eff != null) {
            this.watk += eff.getX();
            this.magic += eff.getY();
            this.accuracy += eff.getV();
        }
        buff = chra.getBuffedSkill_X(MapleBuffStat.集中精力);
        if (buff != null) {
            this.mpconReduce += buff.intValue();
        }
        eff = chra.getStatForBuff(MapleBuffStat.进阶祝福);
        if (eff != null) {
            this.watk += eff.getX();
            this.magic += eff.getY();
            this.accuracy += eff.getV();
            this.mpconReduce += eff.getMPConReduce();
        }

        eff = chra.getStatForBuff(MapleBuffStat.抗魔领域);
        if (eff != null) {
            this.ASR += eff.getX();
        }
        eff = chra.getStatForBuff(MapleBuffStat.斗气集中);
        buff = chra.getBuffedValue(MapleBuffStat.斗气集中);
        if ((eff != null) && (buff != null)) {
            this.dam_r *= (100.0D + (eff.getV() + eff.getDAMRate()) * (buff.intValue() - 1)) / 100.0D;
            this.bossdam_r *= (100.0D + (eff.getV() + eff.getDAMRate()) * (buff.intValue() - 1)) / 100.0D;
        }
        eff = chra.getStatForBuff(MapleBuffStat.召唤兽);
        if ((eff != null)
                && (eff.getSourceId() == 35121010)) {
            this.dam_r *= (eff.getX() + 100.0D) / 100.0D;
            this.bossdam_r *= (eff.getX() + 100.0D) / 100.0D;
        }

        eff = chra.getStatForBuff(MapleBuffStat.黑暗灵气);
        if (eff != null) {
            this.dam_r *= (eff.getX() + 100.0D) / 100.0D;
            this.bossdam_r *= (eff.getX() + 100.0D) / 100.0D;
        }
        eff = chra.getStatForBuff(MapleBuffStat.幻灵霸体);
        if (eff != null) {
            this.dam_r *= (eff.getV() + 100.0D) / 100.0D;
            this.bossdam_r *= (eff.getV() + 100.0D) / 100.0D;
        }
        eff = chra.getStatForBuff(MapleBuffStat.灵魂助力);
        if (eff != null) {
            this.trueMastery += eff.getMastery();
        }
        eff = chra.getStatForBuff(MapleBuffStat.金属机甲);
        if (eff != null) {
            this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + eff.getCr());
        }
        eff = chra.getStatForBuff(MapleBuffStat.PYRAMID_PQ);
        if ((eff != null) && (eff.getBerserk() > 0)) {
            this.dam_r *= eff.getBerserk() / 100.0D;
            this.bossdam_r *= eff.getBerserk() / 100.0D;
        }
        eff = chra.getStatForBuff(MapleBuffStat.属性攻击);
        if (eff != null) {
            this.dam_r *= eff.getDamage() / 100.0D;
            this.bossdam_r *= eff.getDamage() / 100.0D;
        }
        eff = chra.getStatForBuff(MapleBuffStat.敛财术);
        if (eff != null) {
            this.pickRate = eff.getProb();
        }
        eff = chra.getStatForBuff(MapleBuffStat.反制攻击);
        if (eff != null) {
            this.dam_r *= (eff.getDAMRate() + 100.0D) / 100.0D;
            this.bossdam_r *= (eff.getDAMRate() + 100.0D) / 100.0D;
        }
        eff = chra.getStatForBuff(MapleBuffStat.雷鸣冲击);
        if (eff != null) {
            this.dam_r *= eff.getDamage() / 100.0D;
            this.bossdam_r *= eff.getDamage() / 100.0D;
        }
        eff = chra.getStatForBuff(MapleBuffStat.风影漫步);
        if (eff != null) {
            this.dam_r *= eff.getDamage() / 100.0D;
            this.bossdam_r *= eff.getDamage() / 100.0D;
        }
        eff = chra.getStatForBuff(MapleBuffStat.祝福护甲);
        if (eff != null) {
            this.watk += eff.getEnhancedWatk();
        }
        buff = chra.getBuffedSkill_Y(MapleBuffStat.隐身术);
        if (buff != null) {
            this.dam_r *= (buff.intValue() + 100.0D) / 100.0D;
            this.bossdam_r *= (buff.intValue() + 100.0D) / 100.0D;
        }
        buff = chra.getBuffedSkill_X(MapleBuffStat.葵花宝典);
        if (buff != null) {
            this.dam_r *= (buff.intValue() + 100.0D) / 100.0D;
            this.bossdam_r *= (buff.intValue() + 100.0D) / 100.0D;
        }
        buff = chra.getBuffedSkill_X(MapleBuffStat.战斗命令);
        if (buff != null) {
            this.combatOrders += buff.intValue();
        }
        eff = chra.getStatForBuff(MapleBuffStat.火眼晶晶);
        if (eff != null) {
            this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + eff.getX());
            this.passive_sharpeye_percent = (short) (this.passive_sharpeye_percent + eff.getCriticalMax());
        }

        eff = chra.getStatForBuff(MapleBuffStat.古老意志);
        if (eff != null) {
            this.dam_r += eff.getDAMRate() / 100.0D;
            this.bossdam_r += eff.getDAMRate() / 100.0D;
            localmaxhp_ += eff.getEnhancedHP();
        }
        buff = chra.getBuffedValue(MapleBuffStat.CRITICAL_RATE_BUFF);
        if (buff != null) {
            this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + buff.intValue());
        }
        if (this.speed > 140) {
            this.speed = 140;
        }
        if (this.jump > 123) {
            this.jump = 123;
        }
        bx = SkillFactory.getSkill(228);
        bof = chra.getSkillLevel(bx);
        if (bof > 0) {
            this.speed += 50;
            this.jump += 20;
        }
        buff = chra.getBuffedValue(MapleBuffStat.骑兽技能);
        if (buff != null) {
            this.jump = 120;
            switch (buff.intValue()) {
                case 1:
                    this.speed = 150;
                    break;
                case 2:
                    this.speed = 170;
                    break;
                case 3:
                    this.speed = 180;
                    break;
                default:
                    this.speed = 200;
            }
        }

        this.hands = (this.localdex + this.localint_ + this.localluk);
        calculateFame(chra);
        this.ignoreTargetDEF += chra.getTrait(MapleTrait.MapleTraitType.charisma).getLevel() / 10;
        this.pvpDamage += chra.getTrait(MapleTrait.MapleTraitType.charisma).getLevel() / 10;

        localmaxmp_ += chra.getTrait(MapleTrait.MapleTraitType.sense).getLevel() * 20;

        localmaxhp_ += chra.getTrait(MapleTrait.MapleTraitType.will).getLevel() * 20;
        this.ASR += chra.getTrait(MapleTrait.MapleTraitType.will).getLevel() / 5;

        this.accuracy += chra.getTrait(MapleTrait.MapleTraitType.insight).getLevel() * 15 / 10;

        this.localmaxhp = Math.min(99999, Math.abs(Math.max(-99999, localmaxhp_)));
        this.localmaxmp = Math.min(99999, Math.abs(Math.max(-99999, localmaxmp_)));

        if ((chra.getEventInstance() != null) && (chra.getEventInstance().getName().startsWith("PVP"))) {
            this.localmaxhp = Math.min(40000, this.localmaxhp * 3);
            this.localmaxmp = Math.min(20000, this.localmaxmp * 2);

            for (int i : pvpSkills) {
                Skill skil = SkillFactory.getSkill(i);
                if ((skil != null) && (skil.canBeLearnedBy(chra.getJob()))) {
                    chra.changeSkillLevel_Skip(skil, 1, (byte) 0);
                    eff = skil.getEffect(1);
                    switch (i / 1000000 % 10) {
                        case 1:
                            if (eff.getX() <= 0) {
                                break;
                            }
                            this.pvpDamage += this.wdef / eff.getX();
                            break;
                        case 3:
                            this.hpRecoverProp += eff.getProb();
                            this.hpRecover += eff.getX();
                            this.mpRecoverProp += eff.getProb();
                            this.mpRecover += eff.getX();
                            break;
                        case 5:
                            this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + eff.getProb());
                            this.passive_sharpeye_percent = 100;
                        case 2:
                        case 4:
                    }
                    break;
                }
            }
            eff = chra.getStatForBuff(MapleBuffStat.变身术);
            if ((eff != null) && (eff.getSourceId() % 10000 == 1105)) {
                this.localmaxhp = 99999;
                this.localmaxmp = 99999;
            }
        }
        if (GameConstants.is恶魔猎手(chra.getJob())) {
            this.localmaxmp = (GameConstants.getMPByJob(chra.getJob()) + this.equippedForce);
        }

        switch (chra.getJob()) {
            case 210:
            case 211:
            case 212:
                bx = SkillFactory.getSkill(2100007);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    this.localint_ += bx.getEffect(bof).getIntX();
                }
                bx = SkillFactory.getSkill(2110000);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.dotTime += eff.getX();
                    this.dot += eff.getZ();
                }
                bx = SkillFactory.getSkill(2110001);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.mpconPercent += eff.getX() - 100;
                    this.dam_r *= eff.getY() / 100.0D;
                    this.bossdam_r *= eff.getY() / 100.0D;
                }
                bx = SkillFactory.getSkill(2121003);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.damageIncrease.put(Integer.valueOf(2111003), Integer.valueOf(eff.getX()));
                }
                bx = SkillFactory.getSkill(2120009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.magic += eff.getMagicX();
                    this.BuffUP_Skill += eff.getX();
                }
                bx = SkillFactory.getSkill(2121009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    this.magic += bx.getEffect(bof).getMagicX();
                }
                bx = SkillFactory.getSkill(2120010);
                bof = chra.getTotalSkillLevel(bx);
                if (bof <= 0) {
                    break;
                }
                eff = bx.getEffect(bof);
                this.dam_r *= (eff.getX() * eff.getY() + 100.0D) / 100.0D;
                this.bossdam_r *= (eff.getX() * eff.getY() + 100.0D) / 100.0D;
                this.ignoreTargetDEF += eff.getIgnoreMob();
                break;
            case 220:
            case 221:
            case 222:
                bx = SkillFactory.getSkill(2200007);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    this.localint_ += bx.getEffect(bof).getIntX();
                }
                bx = SkillFactory.getSkill(2210000);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    this.dot += bx.getEffect(bof).getZ();
                }
                bx = SkillFactory.getSkill(2210001);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.mpconPercent += eff.getX() - 100;
                    this.dam_r *= eff.getY() / 100.0D;
                    this.bossdam_r *= eff.getY() / 100.0D;
                }
                bx = SkillFactory.getSkill(2220009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.magic += eff.getMagicX();
                    this.BuffUP_Skill += eff.getX();
                }
                bx = SkillFactory.getSkill(2221009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    this.magic += bx.getEffect(bof).getMagicX();
                }
                bx = SkillFactory.getSkill(2220010);
                bof = chra.getTotalSkillLevel(bx);
                if (bof <= 0) {
                    break;
                }
                eff = bx.getEffect(bof);
                this.dam_r *= (eff.getX() * eff.getY() + 100.0D) / 100.0D;
                this.bossdam_r *= (eff.getX() * eff.getY() + 100.0D) / 100.0D;
                this.ignoreTargetDEF += eff.getIgnoreMob();
                break;
            case 1211:
            case 1212:
                bx = SkillFactory.getSkill(12110001);
                bof = chra.getTotalSkillLevel(bx);
                if (bof <= 0) {
                    break;
                }
                eff = bx.getEffect(bof);
                this.mpconPercent += eff.getX() - 100;
                this.dam_r *= eff.getY() / 100.0D;
                this.bossdam_r *= eff.getY() / 100.0D;
                break;
            case 230:
            case 231:
            case 232:
                bx = SkillFactory.getSkill(2300007);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    this.localint_ += bx.getEffect(bof).getIntX();
                }
                bx = SkillFactory.getSkill(2310008);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + bx.getEffect(bof).getCr());
                }
                bx = SkillFactory.getSkill(2320010);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.magic += eff.getMagicX();
                    this.BuffUP_Skill += eff.getX();
                }
                bx = SkillFactory.getSkill(2321010);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    this.magic += bx.getEffect(bof).getMagicX();
                }
                bx = SkillFactory.getSkill(2320005);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    this.ASR += bx.getEffect(bof).getASRRate();
                }
                bx = SkillFactory.getSkill(2320011);
                bof = chra.getTotalSkillLevel(bx);
                if (bof <= 0) {
                    break;
                }
                eff = bx.getEffect(bof);
                this.dam_r *= (eff.getX() * eff.getY() + 100.0D) / 100.0D;
                this.bossdam_r *= (eff.getX() * eff.getY() + 100.0D) / 100.0D;
                this.ignoreTargetDEF += eff.getIgnoreMob();
                break;
            case 2001:
            case 2300:
            case 2310:
            case 2311:
            case 2312:
                this.defRange = 200;
                bx = SkillFactory.getSkill(20021110);
                bof = chra.getSkillLevel(bx);
                if (bof > 0);
                bx = SkillFactory.getSkill(20020112);
                bof = chra.getSkillLevel(bx);
                if (bof > 0) {
                    chra.getTrait(MapleTrait.MapleTraitType.charm).addLocalExp(GameConstants.getTraitExpNeededForLevel(30));
                }
                bx = SkillFactory.getSkill(23000001);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    this.dodgeChance += bx.getEffect(bof).getER();
                }
                bx = SkillFactory.getSkill(23100008);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.localstr += eff.getStrX();
                    this.localdex += eff.getDexX();
                }
                bx = SkillFactory.getSkill(23100004);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    this.dodgeChance += bx.getEffect(bof).getProb();
                }
                bx = SkillFactory.getSkill(23110006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    this.damageIncrease.put(Integer.valueOf(23101001), Integer.valueOf(bx.getEffect(bof).getDAMRate()));
                }
                bx = SkillFactory.getSkill(23121004);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    this.dodgeChance += bx.getEffect(bof).getProb();
                }
                bx = SkillFactory.getSkill(23120009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.watk += eff.getX();
                    this.trueMastery += eff.getMastery();
                    this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent + eff.getCriticalMin());
                }
                bx = SkillFactory.getSkill(23120010);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    this.ignoreTargetDEF += bx.getEffect(bof).getX();
                }
                bx = SkillFactory.getSkill(23120011);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    this.damageIncrease.put(Integer.valueOf(23101001), Integer.valueOf(bx.getEffect(bof).getDAMRate()));
                }
                bx = SkillFactory.getSkill(23120012);
                bof = chra.getTotalSkillLevel(bx);
                if (bof <= 0) {
                    break;
                }
                eff = bx.getEffect(bof);
                this.watk += eff.getAttackX();
                this.damageIncrease.put(Integer.valueOf(23100005), Integer.valueOf(eff.getDamage()));
                break;
            case 1300:
            case 1310:
            case 1311:
            case 1312:
                this.defRange = 200;
                bx = SkillFactory.getSkill(13000001);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    this.defRange += bx.getEffect(bof).getRange();
                }
                bx = SkillFactory.getSkill(13110008);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    this.dodgeChance += bx.getEffect(bof).getER();
                }
                bx = SkillFactory.getSkill(13110003);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.trueMastery += eff.getMastery();
                    this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent + eff.getCriticalMin());
                }

                bx = SkillFactory.getSkill(13100008);
                bof = chra.getTotalSkillLevel(bx);
                if (bof <= 0) {
                    break;
                }
                eff = bx.getEffect(bof);
                this.localstr += eff.getStrX();
                this.localdex += eff.getDexX();
                break;
            case 300:
            case 310:
            case 311:
            case 312:
                this.defRange = 200;
                bx = SkillFactory.getSkill(3000002);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    this.defRange += bx.getEffect(bof).getRange();
                }
                bx = SkillFactory.getSkill(3100006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.damageIncrease.put(Integer.valueOf(3001004), Integer.valueOf(eff.getX()));
                    this.damageIncrease.put(Integer.valueOf(3000001), Integer.valueOf(eff.getY()));
                    this.localstr += eff.getStrX();
                    this.localdex += eff.getDexX();
                }
                bx = SkillFactory.getSkill(3110007);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    this.dodgeChance += bx.getEffect(bof).getER();
                }
                bx = SkillFactory.getSkill(3120005);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.trueMastery += eff.getMastery();
                    this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent + eff.getCriticalMin());
                }
                bx = SkillFactory.getSkill(3120008);
                bof = chra.getTotalSkillLevel(bx);
                if (bof <= 0) {
                    break;
                }
                eff = bx.getEffect(bof);
                this.watk += eff.getAttackX();
                this.damageIncrease.put(Integer.valueOf(3100001), Integer.valueOf(eff.getDamage()));
                break;
            case 320:
            case 321:
            case 322:
                this.defRange = 200;
                bx = SkillFactory.getSkill(3000002);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    this.defRange += bx.getEffect(bof).getRange();
                }
                bx = SkillFactory.getSkill(3200006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.damageIncrease.put(Integer.valueOf(3001004), Integer.valueOf(eff.getX()));
                    this.damageIncrease.put(Integer.valueOf(3000001), Integer.valueOf(eff.getY()));
                    this.localstr += eff.getStrX();
                    this.localdex += eff.getDexX();
                }
                bx = SkillFactory.getSkill(3220010);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    this.damageIncrease.put(Integer.valueOf(3211006), Integer.valueOf(bx.getEffect(bof).getDamage() - 150));
                }
                bx = SkillFactory.getSkill(3210007);
                bof = chra.getTotalSkillLevel(bx);
                if (bof <= 0) {
                    break;
                }
                this.dodgeChance += bx.getEffect(bof).getER();
                break;
            case 411:
            case 412:
                bx = SkillFactory.getSkill(4110014);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.RecoveryUP += eff.getX() - 100;
                    this.BuffUP += eff.getY() - 100;
                }
                bx = SkillFactory.getSkill(4110012);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.damageIncrease.put(Integer.valueOf(4001344), Integer.valueOf(eff.getDAMRate()));
                    this.damageIncrease.put(Integer.valueOf(4101008), Integer.valueOf(eff.getDAMRate()));
                    this.damageIncrease.put(Integer.valueOf(4101010), Integer.valueOf(eff.getDAMRate()));
                    this.damageIncrease.put(Integer.valueOf(4111010), Integer.valueOf(eff.getDAMRate()));
                }
                bx = SkillFactory.getSkill(4120012);
                bof = chra.getTotalSkillLevel(bx);
                if (bof <= 0) {
                    break;
                }
                eff = bx.getEffect(bof);
                this.watk += eff.getX();
                this.trueMastery += eff.getMastery();
                break;
            case 422:
                bx = SkillFactory.getSkill(4221007);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.damageIncrease.put(Integer.valueOf(4201005), Integer.valueOf(eff.getDAMRate()));
                    this.damageIncrease.put(Integer.valueOf(4201004), Integer.valueOf(eff.getDAMRate()));
                    this.damageIncrease.put(Integer.valueOf(4211002), Integer.valueOf(eff.getDAMRate()));
                    this.damageIncrease.put(Integer.valueOf(4211011), Integer.valueOf(eff.getDAMRate()));
                }
                bx = SkillFactory.getSkill(4220012);
                bof = chra.getTotalSkillLevel(bx);
                if (bof <= 0) {
                    break;
                }
                eff = bx.getEffect(bof);
                this.watk += eff.getX();
                this.trueMastery += eff.getMastery();
                break;
            case 433:
            case 434:
                bx = SkillFactory.getSkill(4330007);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.hpRecoverProp += eff.getProb();
                    this.hpRecoverPercent += eff.getX();
                }
                bx = SkillFactory.getSkill(4341002);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.damageIncrease.put(Integer.valueOf(4311002), Integer.valueOf(eff.getDAMRate()));
                    this.damageIncrease.put(Integer.valueOf(4311003), Integer.valueOf(eff.getDAMRate()));
                    this.damageIncrease.put(Integer.valueOf(4321000), Integer.valueOf(eff.getDAMRate()));
                    this.damageIncrease.put(Integer.valueOf(4321001), Integer.valueOf(eff.getDAMRate()));
                    this.damageIncrease.put(Integer.valueOf(4331000), Integer.valueOf(eff.getDAMRate()));
                    this.damageIncrease.put(Integer.valueOf(4321004), Integer.valueOf(eff.getDAMRate()));
                    this.damageIncrease.put(Integer.valueOf(4331005), Integer.valueOf(eff.getDAMRate()));
                }
                bx = SkillFactory.getSkill(4341006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof <= 0) {
                    break;
                }
                this.dodgeChance += bx.getEffect(bof).getER();
                break;
            case 2110:
            case 2111:
            case 2112:
                bx = SkillFactory.getSkill(21100008);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.localstr += eff.getStrX();
                    this.localdex += eff.getDexX();
                }
                bx = SkillFactory.getSkill(21101006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.dam_r *= (eff.getDAMRate() + 100.0D) / 100.0D;
                    this.bossdam_r *= (eff.getDAMRate() + 100.0D) / 100.0D;
                }
                bx = SkillFactory.getSkill(21110002);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    this.damageIncrease.put(Integer.valueOf(21000004), Integer.valueOf(bx.getEffect(bof).getW()));
                }
                bx = SkillFactory.getSkill(21110010);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    this.ignoreTargetDEF += bx.getEffect(bof).getIgnoreMob();
                }
                bx = SkillFactory.getSkill(21120002);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    this.damageIncrease.put(Integer.valueOf(21100007), Integer.valueOf(bx.getEffect(bof).getZ()));
                }
                bx = SkillFactory.getSkill(21120011);
                bof = chra.getTotalSkillLevel(bx);
                if (bof <= 0) {
                    break;
                }
                eff = bx.getEffect(bof);
                this.damageIncrease.put(Integer.valueOf(21100002), Integer.valueOf(eff.getDAMRate()));
                this.damageIncrease.put(Integer.valueOf(21110003), Integer.valueOf(eff.getDAMRate()));
                break;
            case 3511:
            case 3512:
                bx = SkillFactory.getSkill(35110014);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.damageIncrease.put(Integer.valueOf(35001003), Integer.valueOf(eff.getDAMRate()));
                    this.damageIncrease.put(Integer.valueOf(35101003), Integer.valueOf(eff.getDAMRate()));
                }
                bx = SkillFactory.getSkill(35121006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.damageIncrease.put(Integer.valueOf(35111001), Integer.valueOf(eff.getDAMRate()));
                    this.damageIncrease.put(Integer.valueOf(35111009), Integer.valueOf(eff.getDAMRate()));
                    this.damageIncrease.put(Integer.valueOf(35111010), Integer.valueOf(eff.getDAMRate()));
                }
                bx = SkillFactory.getSkill(35120001);
                bof = chra.getTotalSkillLevel(bx);
                if (bof <= 0) {
                    break;
                }
                eff = bx.getEffect(bof);
                this.damageIncrease.put(Integer.valueOf(35111005), Integer.valueOf(eff.getX()));
                this.damageIncrease.put(Integer.valueOf(35111011), Integer.valueOf(eff.getX()));
                this.damageIncrease.put(Integer.valueOf(35121009), Integer.valueOf(eff.getX()));
                this.damageIncrease.put(Integer.valueOf(35121010), Integer.valueOf(eff.getX()));
                this.damageIncrease.put(Integer.valueOf(35121011), Integer.valueOf(eff.getX()));
                this.BuffUP_Summon += eff.getY();
                break;
            case 110:
            case 111:
            case 112:
                bx = SkillFactory.getSkill(1100009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.damageIncrease.put(Integer.valueOf(1001004), Integer.valueOf(eff.getX()));
                    this.damageIncrease.put(Integer.valueOf(1001005), Integer.valueOf(eff.getY()));
                    this.localstr += eff.getStrX();
                    this.localdex += eff.getDexX();
                }
                bx = SkillFactory.getSkill(1110009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.dam_r *= eff.getDamage() / 100.0D;
                    this.bossdam_r *= eff.getDamage() / 100.0D;
                }
                bx = SkillFactory.getSkill(1120012);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    this.ignoreTargetDEF += bx.getEffect(bof).getIgnoreMob();
                }
                bx = SkillFactory.getSkill(1120013);
                bof = chra.getTotalSkillLevel(bx);
                if (bof <= 0) {
                    break;
                }
                eff = bx.getEffect(bof);
                this.watk += eff.getAttackX();
                this.damageIncrease.put(Integer.valueOf(1100002), Integer.valueOf(eff.getDamage()));
                break;
                /*
            case 5110:
            case 5111:
            case 5112:
                bx = SkillFactory.getSkill(51120002);
                bof = chra.getTotalSkillLevel(bx);
                if (bof <= 0) {
                break;
                }
                eff = bx.getEffect(bof);
                this.watk += eff.getAttackX();
                this.damageIncrease.put(Integer.valueOf(51100002), Integer.valueOf(eff.getDamage()));
                break;
                
                * 
                */
            case 120:
            case 121:
            case 122:
                bx = SkillFactory.getSkill(1200009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.damageIncrease.put(Integer.valueOf(1001004), Integer.valueOf(eff.getX()));
                    this.damageIncrease.put(Integer.valueOf(1001005), Integer.valueOf(eff.getY()));
                    this.localstr += eff.getStrX();
                    this.localdex += eff.getDexX();
                }
                bx = SkillFactory.getSkill(1220006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof <= 0) {
                    break;
                }
                this.ASR += bx.getEffect(bof).getASRRate();
                break;
            case 130:
            case 131:
            case 132:
                bx = SkillFactory.getSkill(1300009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.damageIncrease.put(Integer.valueOf(1001004), Integer.valueOf(eff.getX()));
                    this.damageIncrease.put(Integer.valueOf(1001005), Integer.valueOf(eff.getY()));
                    this.localstr += eff.getStrX();
                    this.localdex += eff.getDexX();
                }
                bx = SkillFactory.getSkill(1310009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + eff.getCr());
                    this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent + eff.getCriticalMin());
                    this.hpRecoverProp += eff.getProb();
                    this.hpRecoverPercent += eff.getX();
                }
                bx = SkillFactory.getSkill(1320006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof <= 0) {
                    break;
                }
                eff = bx.getEffect(bof);
                this.dam_r *= (eff.getDamage() + 100.0D) / 100.0D;
                this.bossdam_r *= (eff.getDamage() + 100.0D) / 100.0D;
                break;
            case 581:
            case 582:
                bx = SkillFactory.getSkill(5810008);
                bof = chra.getTotalSkillLevel(bx);
                if (bof <= 0) {
                    break;
                }
                eff = bx.getEffect(bof);
                this.damageIncrease.put(Integer.valueOf(5801002), Integer.valueOf(eff.getX()));
                this.damageIncrease.put(Integer.valueOf(5801003), Integer.valueOf(eff.getY()));
                this.damageIncrease.put(Integer.valueOf(5801004), Integer.valueOf(eff.getZ()));
                break;
            case 590:
            case 591:
            case 592:
                this.defRange = 200;
                bx = SkillFactory.getSkill(5920001);
                bof = chra.getTotalSkillLevel(bx);
                if (bof <= 0) {
                    break;
                }
                eff = bx.getEffect(bof);
                this.damageIncrease.put(Integer.valueOf(5911004), Integer.valueOf(eff.getDamage()));
                this.damageIncrease.put(Integer.valueOf(5911005), Integer.valueOf(eff.getDamage()));
                break;
            case 1400:
            case 1410:
            case 1411:
            case 1412:
                this.defRange = 300;

                bx = SkillFactory.getSkill(14100010);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    this.localluk += eff.getLukX();
                    this.localdex += eff.getDexX();
                }
                bx = SkillFactory.getSkill(14110011);
                bof = chra.getTotalSkillLevel(bx);
                if (bof <= 0) {
                    break;
                }
                this.RecoveryUP += bx.getEffect(bof).getX() - 100;
        }

        if (GameConstants.is反抗者(chra.getJob())) {
            bx = SkillFactory.getSkill(30000002);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
                this.RecoveryUP += bx.getEffect(bof).getX() - 100;
            }
        }
        CalcPassive_SharpEye(chra);
        CalcPassive_Mastery(chra);
        recalcPVPRank(chra);
        if (first_login) {
            chra.silentEnforceMaxHpMp();
            relocHeal(chra);
        } else {
            chra.enforceMaxHpMp();
        }

        calculateMaxBaseDamage(Math.max(this.magic, this.watk), this.pvpDamage, chra);
        this.trueMastery = Math.min(100, this.trueMastery);
        this.passive_sharpeye_min_percent = (short) Math.min(this.passive_sharpeye_min_percent, this.passive_sharpeye_percent);
        if ((oldmaxhp != 0) && (oldmaxhp != this.localmaxhp)) {
            chra.updatePartyMemberHP();
        }
    }

    public boolean checkEquipLevels(MapleCharacter chr, int gain) {
        if (chr.isClone()) {
            return false;
        }
        boolean changed = false;
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        List<Equip> all = new ArrayList<>(this.equipLevelHandling);
        for (Equip eq : all) {
            int lvlz = eq.getEquipLevel();
            eq.setItemEXP(eq.getItemEXP() + gain);
            if (eq.getEquipLevel() > lvlz) {
                Iterator i$;
                for (int i = eq.getEquipLevel() - lvlz; i > 0; i--) {
                    Map inc = ii.getEquipIncrements(eq.getItemId());
                    if ((inc != null) && (inc.containsKey(Integer.valueOf(lvlz + i)))) {
                        eq = ii.levelUpEquip(eq, (Map) inc.get(Integer.valueOf(lvlz + i)));
                    }

                    if ((GameConstants.getStatFromWeapon(eq.getItemId()) == null) && (GameConstants.getMaxLevel(eq.getItemId()) < lvlz + i) && (Math.random() < 0.1D) && (eq.getIncSkill() <= 0) && (ii.getEquipSkills(eq.getItemId()) != null)) {
                        for (i$ = ii.getEquipSkills(eq.getItemId()).iterator(); i$.hasNext();) {
                            int zzz = ((Integer) i$.next()).intValue();
                            Skill skil = SkillFactory.getSkill(zzz);
                            if ((skil != null) && (skil.canBeLearnedBy(chr.getJob()))) {
                                eq.setIncSkill(skil.getId());
                                chr.dropMessage(5, "Your skill has gained a levelup: " + skil.getName() + " +1");
                            }
                        }
                    }
                }
                changed = true;
            }
            chr.forceReAddItem(eq.copy(), MapleInventoryType.EQUIPPED);
        }
        if (changed) {
            chr.equipChanged();
            chr.getClient().getSession().write(MaplePacketCreator.showItemLevelupEffect());
            chr.getMap().broadcastMessage(chr, MaplePacketCreator.showForeignItemLevelupEffect(chr.getId()), false);
        }
        return changed;
    }

    public boolean checkEquipDurabilitys(MapleCharacter chr, int gain) {
        return checkEquipDurabilitys(chr, gain, false);
    }

    public boolean checkEquipDurabilitys(MapleCharacter chr, int gain, boolean aboveZero) {
        if ((chr.isClone()) || (chr.inPVP())) {
            return true;
        }
        List<Equip> all = new ArrayList<>(this.durabilityHandling);
        for (Equip item : all) {
            if (item != null) {
                if (item.getPosition() >= 0 == aboveZero) {
                    item.setDurability(item.getDurability() + gain);
                    if (item.getDurability() < 0) {
                        item.setDurability(0);
                    }
                }
            }
        }
        for (Equip eqq : all) {
            if ((eqq != null) && (eqq.getDurability() == 0) && (eqq.getPosition() < 0)) {
                if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
                    chr.getClient().getSession().write(MaplePacketCreator.getInventoryFull());
                    chr.getClient().getSession().write(MaplePacketCreator.getShowInventoryFull());
                    return false;
                }
                this.durabilityHandling.remove(eqq);
                short pos = chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot();
                MapleInventoryManipulator.unequip(chr.getClient(), eqq.getPosition(), pos);
            } else if (eqq != null) {
                chr.forceReAddItem(eqq.copy(), MapleInventoryType.EQUIPPED);
            }
        }
        return true;
    }

    public void handleProfessionTool(MapleCharacter chra) {
        if ((chra.getProfessionLevel(92000000) > 0) || (chra.getProfessionLevel(92010000) > 0)) {
            Iterator itera = chra.getInventory(MapleInventoryType.EQUIP).newList().iterator();
            while (itera.hasNext()) {
                Equip equip = (Equip) itera.next();
                if (((equip.getDurability() != 0) && (equip.getItemId() / 10000 == 150) && (chra.getProfessionLevel(92000000) > 0)) || ((equip.getItemId() / 10000 == 151) && (chra.getProfessionLevel(92010000) > 0))) {
                    if (equip.getDurability() > 0) {
                        this.durabilityHandling.add(equip);
                    }
                    this.harvestingTool = equip.getPosition();
                    break;
                }
            }
        }
    }

    private void CalcPassive_Mastery(MapleCharacter player) {
        if (player.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -11) == null) {
            this.passive_mastery = 0;
            return;
        }

        MapleWeaponType weaponType = GameConstants.getWeaponType(player.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -11).getItemId());
        boolean acc = true;
        int skil;
        switch (weaponType) {
            case BOW:
                skil = GameConstants.is骑士团(player.getJob()) ? 13100000 : 3100000;
                break;
            case CLAW:
                skil = GameConstants.is幻影(player.getJob()) ? 24100004 : 4100000;
                break;
            case CANNON:
                skil = 5300005;
                break;
            case KATARA:
            case DAGGER:
                skil = (player.getJob() >= 430) && (player.getJob() <= 434) ? 4300000 : 4200000;
                break;
            case CROSSBOW:
                skil = GameConstants.is反抗者(player.getJob()) ? 33100000 : 3200000;
                break;
            case AXE1H:
            case BLUNT1H:
                skil = player.getJob() > 112 ? 1200000 : GameConstants.is骑士团(player.getJob()) ? 11100000 : GameConstants.is反抗者(player.getJob()) ? 31100004 : 1100000;
                break;
            case AXE2H:
            case SWORD1H:
            case SWORD2H:
            case BLUNT2H:
                skil = player.getJob() > 112 ? 1200000 : GameConstants.is骑士团(player.getJob()) ? 11100000 : 1100000;
                break;
            case POLE_ARM:
                skil = GameConstants.is战神(player.getJob()) ? 21100000 : 1300000;
                break;
            case SPEAR:
                skil = 1300000;
                break;
            case KNUCKLE:
                skil = GameConstants.is骑士团(player.getJob()) ? 15100001 : 5800001;
                break;
            case GUN:
                skil = GameConstants.is龙的传人(player.getJob()) ? 5700000 : GameConstants.is反抗者(player.getJob()) ? 35100000 : 5900000;
                break;
            case DUAL_BOW:
                skil = 23100005;
                break;
            case WAND:
            case STAFF:
                acc = false;
                skil = player.getJob() <= 2000 ? 12100007 : player.getJob() <= 232 ? 2300006 : player.getJob() <= 222 ? 2200006 : player.getJob() <= 212 ? 2100006 : GameConstants.is反抗者(player.getJob()) ? 32100006 : 22120002;
                break;
            default:
                this.passive_mastery = 0;
                return;
        }

        if (player.getSkillLevel(skil) <= 0) {
            this.passive_mastery = 0;
            return;
        }
        MapleStatEffect eff = SkillFactory.getSkill(skil).getEffect(player.getTotalSkillLevel(skil));
        if (acc) {
            this.accuracy += eff.getX();
            if (skil == 35100000) {
                this.watk += eff.getX();
            }
        } else {
            this.magic += eff.getX();
        }
        this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + eff.getCr());
        this.passive_mastery = eff.getMastery();
        this.trueMastery += eff.getMastery() + weaponType.getBaseMastery();
    }

    private void calculateFame(MapleCharacter player) {
        player.getTrait(MapleTrait.MapleTraitType.charm).addLocalExp(player.getFame());
        for (MapleTrait.MapleTraitType t : MapleTrait.MapleTraitType.values()) {
            player.getTrait(t).recalcLevel();
        }
    }

    private void CalcPassive_SharpEye(MapleCharacter player) {
        if (GameConstants.is反抗者(player.getJob())) {
            Skill critSkill = SkillFactory.getSkill(30000022);
            int critlevel = player.getTotalSkillLevel(critSkill);
            if (critlevel > 0) {
                this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + critSkill.getEffect(critlevel).getProb());
                this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent + critSkill.getEffect(critlevel).getCriticalMin());
            }
            critSkill = SkillFactory.getSkill(30010022);
            critlevel = player.getTotalSkillLevel(critSkill);
            if (critlevel > 0) {
                this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + critSkill.getEffect(critlevel).getProb());
                this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent + critSkill.getEffect(critlevel).getCriticalMin());
            }
        }
        switch (player.getJob()) {
            case 410:
            case 411:
            case 412: {
                Skill critSkill = SkillFactory.getSkill(4100001);
                int critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel <= 0) {
                    break;
                }
                this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + critSkill.getEffect(critlevel).getProb());
                this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent + critSkill.getEffect(critlevel).getCriticalMin());
                break;
            }
            case 1410:
            case 1411:
            case 1412: {
                Skill critSkill = SkillFactory.getSkill(14100001);
                int critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel <= 0) {
                    break;
                }
                this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + critSkill.getEffect(critlevel).getProb());
                this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent + critSkill.getEffect(critlevel).getCriticalMin());
                break;
            }
            case 3100:
            case 3110:
            case 3111:
            case 3112: {
                Skill critSkill = SkillFactory.getSkill(31100006);
                int critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel <= 0) {
                    break;
                }
                this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + (short) critSkill.getEffect(critlevel).getCr());
                this.watk += critSkill.getEffect(critlevel).getAttackX();
                break;
            }
            case 2300:
            case 2310:
            case 2311:
            case 2312: {
                Skill critSkill = SkillFactory.getSkill(23000003);
                int critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel <= 0) {
                    break;
                }
                this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + (short) critSkill.getEffect(critlevel).getCr());
                this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent + critSkill.getEffect(critlevel).getCriticalMin());
                break;
            }
            case 3210:
            case 3211:
            case 3212: {
                Skill critSkill = SkillFactory.getSkill(32100006);
                int critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel <= 0) {
                    break;
                }
                this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + (short) critSkill.getEffect(critlevel).getCr());
                this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent + critSkill.getEffect(critlevel).getCriticalMin());
                break;
            }
            case 434: {
                Skill critSkill = SkillFactory.getSkill(4340010);
                int critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel <= 0) {
                    break;
                }
                this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + critSkill.getEffect(critlevel).getProb());
                this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent + critSkill.getEffect(critlevel).getCriticalMin());
                break;
            }
            case 590:
            case 591:
            case 592: {
                Skill critSkill = SkillFactory.getSkill(5900007);
                int critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel <= 0) {
                    break;
                }
                this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + (short) critSkill.getEffect(critlevel).getCr());
                this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent + critSkill.getEffect(critlevel).getCriticalMin());
                break;
            }
            case 211:
            case 212: {
                Skill critSkill = SkillFactory.getSkill(2110009);
                int critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel <= 0) {
                    break;
                }
                this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + (short) critSkill.getEffect(critlevel).getCr());
                this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent + critSkill.getEffect(critlevel).getCriticalMin());
                break;
            }
            case 221:
            case 222: {
                Skill critSkill = SkillFactory.getSkill(2210009);
                int critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel <= 0) {
                    break;
                }
                this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + (short) critSkill.getEffect(critlevel).getCr());
                this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent + critSkill.getEffect(critlevel).getCriticalMin());
                break;
            }
            case 231:
            case 232: {
                Skill critSkill = SkillFactory.getSkill(2310010);
                int critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel <= 0) {
                    break;
                }
                this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + (short) critSkill.getEffect(critlevel).getCr());
                this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent + critSkill.getEffect(critlevel).getCriticalMin());
                break;
            }
            case 1211:
            case 1212: {
                Skill critSkill = SkillFactory.getSkill(12110000);
                int critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel <= 0) {
                    break;
                }
                this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + (short) critSkill.getEffect(critlevel).getCr());
                this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent + critSkill.getEffect(critlevel).getCriticalMin());
                break;
            }
            case 530:
            case 531:
            case 532: {
                Skill critSkill = SkillFactory.getSkill(5300004);
                int critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel <= 0) {
                    break;
                }
                this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + (short) critSkill.getEffect(critlevel).getCr());
                this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent + critSkill.getEffect(critlevel).getCriticalMin());
                break;
            }
            case 580:
            case 581:
            case 582: {
                Skill critSkill = SkillFactory.getSkill(5810000);
                int critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + critSkill.getEffect(critlevel).getProb());
                    this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent + critSkill.getEffect(critlevel).getCriticalMin());
                }
                Skill critSkill2 = SkillFactory.getSkill(5800008);
                int critlevel2 = player.getTotalSkillLevel(critSkill);
                if (critlevel2 <= 0) {
                    break;
                }
                this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + (short) critSkill2.getEffect(critlevel2).getCr());
                this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent + critSkill2.getEffect(critlevel2).getCriticalMin());
                break;
            }
            case 1500:
            case 1510:
            case 1511:
            case 1512: {
                Skill critSkill = SkillFactory.getSkill(15000006);
                int critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + critSkill.getEffect(critlevel).getProb());
                    this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent + critSkill.getEffect(critlevel).getCriticalMin());
                }
                Skill critSkill1 = SkillFactory.getSkill(15110009);
                int critlevel1 = player.getTotalSkillLevel(critSkill1);
                if (critlevel1 <= 0) {
                    break;
                }
                this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + critSkill1.getEffect(critlevel1).getProb());
                this.passive_sharpeye_percent = (short) (this.passive_sharpeye_percent + critSkill1.getEffect(critlevel1).getCriticalMin());
                this.bossdam_r += critSkill1.getEffect(critlevel1).getProb();
                break;
            }
            case 2111:
            case 2112: {
                Skill critSkill = SkillFactory.getSkill(21110000);
                int critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel <= 0) {
                    break;
                }
                this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + (short) (critSkill.getEffect(critlevel).getX() * critSkill.getEffect(critlevel).getY() + critSkill.getEffect(critlevel).getCr()));
                break;
            }
            case 300:
            case 310:
            case 311:
            case 312:
            case 320:
            case 321:
            case 322: {
                Skill critSkill = SkillFactory.getSkill(3000001);
                int critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel <= 0) {
                    break;
                }
                this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + critSkill.getEffect(critlevel).getProb());
                this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent + critSkill.getEffect(critlevel).getCriticalMin());
                break;
            }
            case 1300:
            case 1310:
            case 1311:
            case 1312: {
                Skill critSkill = SkillFactory.getSkill(13000000);
                int critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel <= 0) {
                    break;
                }
                this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + critSkill.getEffect(critlevel).getProb());
                this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent + critSkill.getEffect(critlevel).getCriticalMin());
                break;
            }
            case 2214:
            case 2215:
            case 2216:
            case 2217:
            case 2218: {
                Skill critSkill = SkillFactory.getSkill(22140000);
                int critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel <= 0) {
                    break;
                }
                this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + critSkill.getEffect(critlevel).getProb());
                this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent + critSkill.getEffect(critlevel).getCriticalMin());
                break;
            }
            case 570:
            case 571:
            case 572: {
                Skill critSkill = SkillFactory.getSkill(5080004);
                int critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + (short) critSkill.getEffect(critlevel).getCr());
                    this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent + critSkill.getEffect(critlevel).getCriticalMin());
                }
                Skill critSkill1 = SkillFactory.getSkill(5710005);
                int critlevel1 = player.getTotalSkillLevel(critSkill1);
                if (critlevel1 > 0) {
                    this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + (short) critSkill1.getEffect(critlevel1).getCr());
                }
                Skill critSkill2 = SkillFactory.getSkill(5720008);
                int critlevel2 = player.getTotalSkillLevel(critSkill2);
                if (critlevel2 <= 0) {
                    break;
                }
                this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + (short) critSkill2.getEffect(critlevel2).getCr());
                this.passive_sharpeye_percent = (short) (this.passive_sharpeye_percent + critSkill2.getEffect(critlevel2).getCriticalMin());
                this.bossdam_r += critSkill2.getEffect(critlevel2).getProb();
                break;
            }
            case 2411:
            case 2412: {
                Skill critSkill = SkillFactory.getSkill(24110007);
                int critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel <= 0) {
                    break;
                }
                this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + (short) critSkill.getEffect(critlevel).getCr());
                break;
            }
        }
    }

    public short passive_sharpeye_min_percent() {
        return this.passive_sharpeye_min_percent;
    }

    public short passive_sharpeye_percent() {
        return this.passive_sharpeye_percent;
    }

    public short passive_sharpeye_rate() {
        return this.passive_sharpeye_rate;
    }

    public byte passive_mastery() {
        return this.passive_mastery;
    }

    public void calculateMaxBaseDamage(int watk, int pvpDamage, MapleCharacter chra) {
        if (watk <= 0) {
            this.localmaxbasedamage = 1.0F;
            this.localmaxbasepvpdamage = 1.0F;
        } else {
            Item weapon_item = chra.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -11);
            Item weapon_item2 = chra.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -10);
            int job = chra.getJob();
            MapleWeaponType weapon = weapon_item == null ? MapleWeaponType.NOT_A_WEAPON : GameConstants.getWeaponType(weapon_item.getItemId());
            MapleWeaponType weapon2 = weapon_item2 == null ? MapleWeaponType.NOT_A_WEAPON : GameConstants.getWeaponType(weapon_item2.getItemId());

            boolean mage = ((job >= 200) && (job <= 232)) || ((job >= 1200) && (job <= 1212)) || ((job >= 2200) && (job <= 2218)) || ((job >= 3200) && (job <= 3212));
            int mainstat;
            int secondarystat;
            int mainstatpvp;
            int secondarystatpvp;
            switch (weapon) {
                case BOW:
                case CROSSBOW:
                case GUN:
                    mainstat = this.localdex;
                    secondarystat = this.localstr;
                    mainstatpvp = this.dex;
                    secondarystatpvp = this.str;
                    break;
                case DAGGER:
                case KATARA:
                case CLAW:
                    mainstat = this.localluk;
                    secondarystat = this.localdex + this.localstr;
                    mainstatpvp = this.luk;
                    secondarystatpvp = this.dex + this.str;
                    break;
//      case 3:
//      case 7:
//      case 8:
//      case 9:
//      case 10:
//      case 11:
//      case 12:
//      case 13:
//      case 14:
//      case 15:
                default:
                    if (mage) {
                        mainstat = this.localint_;
                        secondarystat = this.localluk;
                        mainstatpvp = this.int_;
                        secondarystatpvp = this.luk;
                    } else {
                        mainstat = this.localstr;
                        secondarystat = this.localdex;
                        mainstatpvp = this.str;
                        secondarystatpvp = this.dex;
                    }
            }

            this.localmaxbasepvpdamage = (weapon.getMaxDamageMultiplier() * (4 * mainstatpvp + secondarystatpvp) * (100.0F + pvpDamage / 100.0F));
            this.localmaxbasepvpdamageL = (weapon.getMaxDamageMultiplier() * (4 * mainstat + secondarystat) * (100.0F + pvpDamage / 100.0F));
            if ((weapon2 != MapleWeaponType.NOT_A_WEAPON) && (weapon_item != null) && (weapon_item2 != null)) {
                Equip we1 = (Equip) weapon_item;
                Equip we2 = (Equip) weapon_item2;
                this.localmaxbasedamage = (weapon.getMaxDamageMultiplier() * (4 * mainstat + secondarystat) * ((watk - (mage ? we2.getMatk() : we2.getWatk())) / 100.0F));
                this.localmaxbasedamage += weapon2.getMaxDamageMultiplier() * (4 * mainstat + secondarystat) * ((watk - (mage ? we1.getMatk() : we1.getWatk())) / 100.0F);
            } else {
                this.localmaxbasedamage = (weapon.getMaxDamageMultiplier() * (4 * mainstat + secondarystat) * (watk / 100.0F));
            }
        }
    }

    public float getHealHP() {
        return this.shouldHealHP;
    }

    public float getHealMP() {
        return this.shouldHealMP;
    }

    public void relocHeal(MapleCharacter chra) {
        if (chra.isClone()) {
            return;
        }
        int playerjob = chra.getJob();
        this.shouldHealHP = (10 + this.recoverHP);
        this.shouldHealMP = (3 + this.mpRestore + this.recoverMP + this.localint_ / 10);
        this.mpRecoverTime = 0;
        this.hpRecoverTime = 0;
        if ((playerjob == 111) || (playerjob == 112)) {
            Skill effect = SkillFactory.getSkill(1110000);
            int lvl = chra.getSkillLevel(effect);
            if (lvl > 0) {
                MapleStatEffect eff = effect.getEffect(lvl);
                if (eff.getHp() > 0) {
                    this.shouldHealHP += eff.getHp();
                    this.hpRecoverTime = 4000;
                }
                this.shouldHealMP += eff.getMp();
                this.mpRecoverTime = 4000;
            }
        } else if ((playerjob == 1111) || (playerjob == 1112)) {
            Skill effect = SkillFactory.getSkill(11110000);
            int lvl = chra.getSkillLevel(effect);
            if (lvl > 0) {
                this.shouldHealMP += effect.getEffect(lvl).getMp();
                this.mpRecoverTime = 4000;
            }
        } else if ((playerjob == 570) || (playerjob == 571) || (playerjob == 572)) {
            Skill effect = SkillFactory.getSkill(5700005);
            int lvl = chra.getSkillLevel(effect);
            if (lvl > 0) {
                this.shouldHealHP += effect.getEffect(lvl).getX() * this.localmaxhp / 100;
                this.hpRecoverTime = (effect.getEffect(lvl).getY() * 1000);
                this.shouldHealMP += effect.getEffect(lvl).getX() * this.localmaxmp / 100;
                this.mpRecoverTime = (effect.getEffect(lvl).getY() * 1000);
            }
        } else if (GameConstants.is双弩精灵(playerjob)) {
            Skill effect = SkillFactory.getSkill(20020109);
            int lvl = chra.getSkillLevel(effect);
            if (lvl > 0) {
                this.shouldHealHP += effect.getEffect(lvl).getX() * this.localmaxhp / 100;
                this.hpRecoverTime = 4000;
                this.shouldHealMP += effect.getEffect(lvl).getX() * this.localmaxmp / 100;
                this.mpRecoverTime = 4000;
            }
        } else if ((playerjob == 3111) || (playerjob == 3112)) {
            Skill effect = SkillFactory.getSkill(31110009);
            int lvl = chra.getSkillLevel(effect);
            if (lvl > 0) {
                this.shouldHealMP += effect.getEffect(lvl).getY();
                this.mpRecoverTime = 4000;
            }
        }
        if (chra.getChair() != 0) {
            this.shouldHealHP += 99.0F;
            this.shouldHealMP += 99.0F;
        } else if (chra.getMap() != null) {
            float recvRate = chra.getMap().getRecoveryRate();
            if (recvRate > 0.0F) {
                this.shouldHealHP *= recvRate;
                this.shouldHealMP *= recvRate;
            }
        }
    }

    public void connectData(MaplePacketLittleEndianWriter mplew) {
        mplew.writeShort(this.str);
        mplew.writeShort(this.dex);
        mplew.writeShort(this.int_);
        mplew.writeShort(this.luk);

        mplew.writeInt(this.hp);
        mplew.writeInt(this.maxhp);
        mplew.writeInt(this.mp);
        mplew.writeInt(this.maxmp);
    }

    public static int getSkillByJob(int skillID, int job) {
        if (GameConstants.is骑士团(job)) {
            return skillID + 10000000;
        }
        if (GameConstants.is战神(job)) {
            return skillID + 20000000;
        }
        if (GameConstants.is龙神(job)) {
            return skillID + 20010000;
        }
        if (GameConstants.is双弩精灵(job)) {
            return skillID + 20020000;
        }
        if (GameConstants.is幻影(job)) {
            return skillID + 20030000;
        }
        if (GameConstants.is恶魔猎手(job)) {
            return skillID + 30010000;
        }
        if (GameConstants.is反抗者(job)) {
            return skillID + 30000000;
        }
        if (GameConstants.is米哈尔(job)) {
            return skillID + 50000000;
        }
        return skillID;
    }

    public int getSkillIncrement(int skillID) {
        if (this.skillsIncrement.containsKey(Integer.valueOf(skillID))) {
            return ((Integer) this.skillsIncrement.get(Integer.valueOf(skillID))).intValue();
        }
        return 0;
    }

    public int getElementBoost(Element key) {
        if (this.elemBoosts.containsKey(key)) {
            return ((Integer) this.elemBoosts.get(key)).intValue();
        }
        return 0;
    }

    public int getDamageIncrease(int key) {
        if (this.damageIncrease.containsKey(Integer.valueOf(key))) {
            return ((Integer) this.damageIncrease.get(Integer.valueOf(key))).intValue();
        }
        return 0;
    }

    public int getAccuracy() {
        return this.accuracy;
    }

    public void heal_noUpdate(MapleCharacter chra) {
        setHp(getCurrentMaxHp(), chra);
        setMp(getCurrentMaxMp(chra.getJob()), chra);
    }

    public void heal(MapleCharacter chra) {
        heal_noUpdate(chra);
        chra.updateSingleStat(MapleStat.HP, getCurrentMaxHp());
        chra.updateSingleStat(MapleStat.MP, getCurrentMaxMp(chra.getJob()));
    }

    public void handleItemOption(StructItemOption soc, MapleCharacter chra, boolean first_login) {
        this.localstr += soc.get("incSTR");
        this.localdex += soc.get("incDEX");
        this.localint_ += soc.get("incINT");
        this.localluk += soc.get("incLUK");
        this.accuracy += soc.get("incACC");

        this.speed += soc.get("incSpeed");
        this.jump += soc.get("incJump");
        this.watk += soc.get("incPAD");
        this.magic += soc.get("incMAD");
        this.wdef += soc.get("incPDD");
        this.mdef += soc.get("incMDD");
        this.percent_str += soc.get("incSTRr");
        this.percent_dex += soc.get("incDEXr");
        this.percent_int += soc.get("incINTr");
        this.percent_luk += soc.get("incLUKr");
        this.percent_hp += soc.get("incMHPr");
        this.percent_mp += soc.get("incMMPr");
        this.percent_acc += soc.get("incACCr");
        this.dodgeChance += soc.get("incEVAr");
        this.percent_atk += soc.get("incPADr");
        this.percent_matk += soc.get("incMADr");
        this.percent_wdef += soc.get("incPDDr");
        this.percent_mdef += soc.get("incMDDr");
        this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + soc.get("incCr"));
        this.bossdam_r *= (soc.get("incDAMr") + 100.0D) / 100.0D;
        if (soc.get("boss") <= 0) {
            this.dam_r *= (soc.get("incDAMr") + 100.0D) / 100.0D;
        }
        this.recoverHP += soc.get("RecoveryHP");
        this.recoverMP += soc.get("RecoveryMP");
        if (soc.get("HP") > 0) {
            this.hpRecover += soc.get("HP");
            this.hpRecoverProp += soc.get("prop");
        }
        if ((soc.get("MP") > 0) && (!GameConstants.is恶魔猎手(chra.getJob()))) {
            this.mpRecover += soc.get("MP");
            this.mpRecoverProp += soc.get("prop");
        }
        this.ignoreTargetDEF += soc.get("ignoreTargetDEF");
        if (soc.get("ignoreDAM") > 0) {
            this.ignoreDAM += soc.get("ignoreDAM");
            this.ignoreDAM_rate += soc.get("prop");
        }
        this.incAllskill += soc.get("incAllskill");
        if (soc.get("ignoreDAMr") > 0) {
            this.ignoreDAMr += soc.get("ignoreDAMr");
            this.ignoreDAMr_rate += soc.get("prop");
        }
        this.RecoveryUP += soc.get("RecoveryUP");
        this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent + soc.get("incCriticaldamageMin"));
        this.passive_sharpeye_percent = (short) (this.passive_sharpeye_percent + soc.get("incCriticaldamageMax"));
        this.TER += soc.get("incTerR");
        this.ASR += soc.get("incAsrR");
        if (soc.get("DAMreflect") > 0) {
            this.DAMreflect += soc.get("DAMreflect");
            this.DAMreflect_rate += soc.get("prop");
        }
        this.mpconReduce += soc.get("mpconReduce");
        this.reduceCooltime += soc.get("reduceCooltime");
        this.incMesoProp += soc.get("incMesoProp");
        this.dropBuff *= (100 + soc.get("incRewardProp")) / 100.0D;
        if ((first_login) && (soc.get("skillID") > 0)) {
            chra.changeSkillLevel_Skip(SkillFactory.getSkill(getSkillByJob(soc.get("skillID"), chra.getJob())), 1, (byte) 0);
        }
    }

    public void recalcPVPRank(MapleCharacter chra) {
        this.pvpRank = 10;
        this.pvpExp = chra.getTotalBattleExp();
        for (int i = 0; i < 10; i++) {
            if (this.pvpExp > GameConstants.getPVPExpNeededForLevel(i + 1)) {
                this.pvpRank -= 1;
                this.pvpExp -= GameConstants.getPVPExpNeededForLevel(i + 1);
            }
        }
    }

    public int getHPPercent() {
        return (int) Math.ceil(this.hp * 100.0D / this.localmaxhp);
    }
}