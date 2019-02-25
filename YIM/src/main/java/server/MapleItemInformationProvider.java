package server;

import client.MapleCharacter;
import client.MapleTrait;
import client.MapleTrait.MapleTraitType;
import client.inventory.Equip;
import client.inventory.EquipAdditions;
import client.inventory.Item;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import database.DatabaseConnectionWZ;
import java.awt.Point;
import java.io.File;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import provider.MapleData;
import provider.MapleDataDirectoryEntry;
import provider.MapleDataEntry;
import provider.MapleDataFileEntry;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import provider.MapleDataType;
import server.StructSetItem.SetItem;
import tools.Pair;
import tools.Triple;

public class MapleItemInformationProvider {

    private static MapleItemInformationProvider instance = new MapleItemInformationProvider();
    protected MapleDataProvider chrData;
    private MapleDataProvider etcData;
    protected MapleDataProvider itemData;
    protected Map<Integer, ItemInformation> dataCache;
    protected Map<String, List<Triple<String, Point, Point>>> afterImage;
    protected Map<Integer, List<StructItemOption>> potentialCache;
    protected Map<Integer, Map<Integer, StructItemOption>> socketCache;
    protected Map<Integer, MapleStatEffect> itemEffects;
    protected Map<Integer, MapleStatEffect> itemEffectsEx;
    protected Map<Integer, Integer> mobIds;
    protected Map<Integer, Pair<Integer, Integer>> potLife;
    protected Map<Integer, StructFamiliar> familiars;
    protected Map<Integer, StructFamiliar> familiars_Item;
    protected Map<Integer, StructFamiliar> familiars_Mob;
    protected Map<Integer, Pair<List<Integer>, List<Integer>>> androids;
    protected Map<Integer, Triple<Integer, List<Integer>, List<Integer>>> monsterBookSets;
    protected Map<Short, StructSetItem> setItems;
    protected Map<Integer, Map<String, String>> getExpCardTimes;
    protected Map<Integer, ScriptedItem> scriptedItemCache;
    protected Map<Integer, Boolean> floatCashItem;
    protected Map<Integer, Short> petFlagInfo;
    protected Map<Integer, Boolean> nActivatedSocket;
    protected Map<Integer, Short> itemIncMHPr;
    protected Map<Integer, Short> itemIncMMPr;
    private ItemInformation tmpInfo;

    public MapleItemInformationProvider() {
        this.chrData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("wzpath") + "/Character.wz"));
        this.etcData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("wzpath") + "/Etc.wz"));
        this.itemData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("wzpath") + "/Item.wz"));
        this.dataCache = new HashMap<>();
        this.afterImage = new HashMap<>();
        this.potentialCache = new HashMap<>();
        this.socketCache = new HashMap<>();
        this.itemEffects = new HashMap<>();
        this.itemEffectsEx = new HashMap<>();
        this.mobIds = new HashMap<>();
        this.potLife = new HashMap<>();
        this.familiars = new HashMap<>();
        this.familiars_Item = new HashMap<>();
        this.familiars_Mob = new HashMap<>();
        this.androids = new HashMap<>();
        this.monsterBookSets = new HashMap<>();
        this.setItems = new HashMap<>();
        this.getExpCardTimes = new HashMap<>();
        this.scriptedItemCache = new HashMap<>();
        this.floatCashItem = new HashMap<>();
        this.petFlagInfo = new HashMap<>();
        this.nActivatedSocket = new HashMap<>();
        this.itemIncMHPr = new HashMap<>();
        this.itemIncMMPr = new HashMap<>();

        this.tmpInfo = null;
    }

    public void runEtc() {
        if (!this.setItems.isEmpty() || !this.potentialCache.isEmpty() || !this.socketCache.isEmpty()) {
            return;
        }
        final MapleData setsData = etcData.getData("SetItemInfo.img");
        StructSetItem itemz;
        SetItem itez;
        for (MapleData dat : setsData) {
            itemz = new StructSetItem();
            itemz.setItemID = Short.parseShort(dat.getName());
            itemz.completeCount = (byte) MapleDataTool.getIntConvert("completeCount", dat, 0);
            for (MapleData level : dat.getChildByPath("ItemID")) {
                if (level.getType() != MapleDataType.INT) {
                    for (MapleData leve : level) {
                        if (!leve.getName().equals("representName") && !leve.getName().equals("typeName")) {
                            itemz.itemIDs.add(MapleDataTool.getInt(leve));
                        }
                    }
                } else {
                    itemz.itemIDs.add(MapleDataTool.getInt(level));
                }
            }
            for (MapleData level : dat.getChildByPath("Effect")) {
                itez = new StructSetItem.SetItem();
                itez.incPDD = MapleDataTool.getIntConvert("incPDD", level, 0);
                itez.incMDD = MapleDataTool.getIntConvert("incMDD", level, 0);
                itez.incSTR = MapleDataTool.getIntConvert("incSTR", level, 0);
                itez.incDEX = MapleDataTool.getIntConvert("incDEX", level, 0);
                itez.incINT = MapleDataTool.getIntConvert("incINT", level, 0);
                itez.incLUK = MapleDataTool.getIntConvert("incLUK", level, 0);
                itez.incACC = MapleDataTool.getIntConvert("incACC", level, 0);
                itez.incPAD = MapleDataTool.getIntConvert("incPAD", level, 0);
                itez.incMAD = MapleDataTool.getIntConvert("incMAD", level, 0);
                itez.incSpeed = MapleDataTool.getIntConvert("incSpeed", level, 0);
                itez.incMHP = MapleDataTool.getIntConvert("incMHP", level, 0);
                itez.incMMP = MapleDataTool.getIntConvert("incMMP", level, 0);
                itez.incMHPr = MapleDataTool.getIntConvert("incMHPr", level, 0);
                itez.incMMPr = MapleDataTool.getIntConvert("incMMPr", level, 0);
                itez.incAllStat = MapleDataTool.getIntConvert("incAllStat", level, 0);
                itez.option1 = MapleDataTool.getIntConvert("Option/1/option", level, 0);
                itez.option2 = MapleDataTool.getIntConvert("Option/2/option", level, 0);
                itez.option1Level = MapleDataTool.getIntConvert("Option/1/level", level, 0);
                itez.option2Level = MapleDataTool.getIntConvert("Option/2/level", level, 0);
                itemz.items.put(Integer.valueOf(level.getName()), itez);
            }
            this.setItems.put(itemz.setItemID, itemz);
        }

        MapleData potsData = this.itemData.getData("ItemOption.img");

        for (MapleData dat : potsData) {
            List<StructItemOption> items = new LinkedList<>();
            for (MapleData potLevel : dat.getChildByPath("level")) {
                StructItemOption item = new StructItemOption();
                item.opID = Integer.parseInt(dat.getName());
                item.optionType = MapleDataTool.getIntConvert("info/optionType", dat, 0);
                item.reqLevel = MapleDataTool.getIntConvert("info/reqLevel", dat, 0);
                for (String i : StructItemOption.types) {
                    if (i.equals("face")) {
                        item.face = MapleDataTool.getString("face", potLevel, "");
                    } else {
                        int level = MapleDataTool.getIntConvert(i, potLevel, 0);
                        if (level > 0) {
                            item.data.put(i, level);
                        }
                    }
                }
                switch (item.opID) {
                    case 31001:
                    case 31002:
                    case 31003:
                    case 31004:
                        item.data.put("skillID", item.opID - 23001);
                        break;
                    case 41005:
                    case 41006:
                    case 41007:
                        item.data.put("skillID", item.opID - 33001);
                }

                items.add(item);
            }
            this.potentialCache.put(Integer.valueOf(dat.getName()), items);
        }

        Map<Integer,StructItemOption> gradeS = new HashMap<>();
        Map<Integer,StructItemOption> gradeA = new HashMap<>();
        Map<Integer,StructItemOption> gradeB = new HashMap<>();
        Map<Integer,StructItemOption> gradeC = new HashMap<>();
        Map<Integer,StructItemOption> gradeD = new HashMap<>();
        MapleData nebuliteData = this.itemData.getData("Install/0306.img");
        for (MapleData dat : nebuliteData) {
            StructItemOption item = new StructItemOption();
            item.opID = Integer.parseInt(dat.getName());
            item.optionType = MapleDataTool.getInt("optionType", dat.getChildByPath("socket"), 0);
            for (MapleData info : dat.getChildByPath("socket/option")) {
                String optionString = MapleDataTool.getString("optionString", info, "");
                int level = MapleDataTool.getInt("level", info, 0);
                if (level > 0) {
                    item.data.put(optionString, Integer.valueOf(level));
                }
            }
            switch (item.opID) {
                case 3063370:
                    item.data.put("skillID", 8000);
                    break;
                case 3063380:
                    item.data.put("skillID", 8001);
                    break;
                case 3063390:
                    item.data.put("skillID", 8002);
                    break;
                case 3063400:
                    item.data.put("skillID", 8003);
                    break;
                case 3064470:
                    item.data.put("skillID", 8004);
                    break;
                case 3064480:
                    item.data.put("skillID", 8005);
                    break;
                case 3064490:
                    item.data.put("skillID", 8006);
            }

            switch (GameConstants.getNebuliteGrade(item.opID)) {
                case 4:
                    gradeS.put(Integer.valueOf(dat.getName()), item);
                    break;
                case 3:
                    gradeA.put(Integer.valueOf(dat.getName()), item);
                    break;
                case 2:
                    gradeB.put(Integer.valueOf(dat.getName()), item);
                    break;
                case 1:
                    gradeC.put(Integer.valueOf(dat.getName()), item);
                    break;
                case 0:
                    gradeD.put(Integer.valueOf(dat.getName()), item);
            }
        }

        this.socketCache.put(4, gradeS);
        this.socketCache.put(3, gradeA);
        this.socketCache.put(2, gradeB);
        this.socketCache.put(1, gradeC);
        this.socketCache.put(0, gradeD);

        MapleDataDirectoryEntry e = (MapleDataDirectoryEntry) this.etcData.getRoot().getEntry("Android");
        for (MapleDataEntry d : e.getFiles()) {
            MapleData iz = this.etcData.getData("Android/" + d.getName());
            List<Integer> hair = new ArrayList<>();
            List<Integer> face = new ArrayList<>();
            for (MapleData ds : iz.getChildByPath("costume/hair")) {
                hair.add(MapleDataTool.getInt(ds, 30000));
            }
            for (MapleData ds : iz.getChildByPath("costume/face")) {
                face.add(MapleDataTool.getInt(ds, 20000));
            }
            Pair<List<Integer>,List<Integer>> pair = new Pair<>(hair, face);
            this.androids.put(Integer.parseInt(d.getName().substring(0, 4)), pair);
        }

        MapleData lifesData = this.etcData.getData("ItemPotLifeInfo.img");
        for (MapleData d : lifesData) {
            if ((d.getChildByPath("info") != null) && (MapleDataTool.getInt("type", d.getChildByPath("info"), 0) == 1)) {
                this.potLife.put(MapleDataTool.getInt("counsumeItem", d.getChildByPath("info"), 0), new Pair(Integer.valueOf(d.getName()), d.getChildByPath("level").getChildren().size()));
            }
        }

        List thePointK = new ArrayList();
        List thePointA = new ArrayList();

        MapleDataDirectoryEntry a = (MapleDataDirectoryEntry) this.chrData.getRoot().getEntry("Afterimage");
        for (MapleDataEntry b : a.getFiles()) {
            MapleData iz = this.chrData.getData("Afterimage/" + b.getName());
            List thePoint = new ArrayList();
            Map<String, Pair> dummy = new HashMap<String, Pair>();
            for (MapleData i : iz) {
                for (MapleData xD : i) {
                    if ((xD.getName().contains("prone")) || (xD.getName().contains("double")) || (xD.getName().contains("triple")) || (((b.getName().contains("bow")) || (b.getName().contains("Bow"))) && ((!xD.getName().contains("shoot")) || (((b.getName().contains("gun")) || (b.getName().contains("cannon"))) && (!xD.getName().contains("shot")))))) {
                        continue;
                    }
                    if (dummy.containsKey(xD.getName())) {
                        if (xD.getChildByPath("lt") != null) {
                            Point lt = (Point) xD.getChildByPath("lt").getData();
                            Point ourLt = (Point) ((Pair) dummy.get(xD.getName())).left;
                            if (lt.x < ourLt.x) {
                                ourLt.x = lt.x;
                            }
                            if (lt.y < ourLt.y) {
                                ourLt.y = lt.y;
                            }
                        }
                        if (xD.getChildByPath("rb") != null) {
                            Point rb = (Point) xD.getChildByPath("rb").getData();
                            Point ourRb = (Point) ((Pair) dummy.get(xD.getName())).right;
                            if (rb.x > ourRb.x) {
                                ourRb.x = rb.x;
                            }
                            if (rb.y > ourRb.y) {
                                ourRb.y = rb.y;
                            }
                        }
                    } else {
                        Point lt = null;
                        Point rb = null;
                        if (xD.getChildByPath("lt") != null) {
                            lt = (Point) xD.getChildByPath("lt").getData();
                        }
                        if (xD.getChildByPath("rb") != null) {
                            rb = (Point) xD.getChildByPath("rb").getData();
                        }
                        dummy.put(xD.getName(), new Pair(lt, rb));
                    }
                }
            }
            for (Entry<String, Pair> ez : dummy.entrySet()) {
                if ((((String) ez.getKey()).length() > 2) && (((String) ez.getKey()).substring(((String) ez.getKey()).length() - 2, ((String) ez.getKey()).length() - 1).equals("D"))) {
                    thePointK.add(new Triple(ez.getKey(), ((Pair) ez.getValue()).left, ((Pair) ez.getValue()).right));
                } else if (((String) ez.getKey()).contains("PoleArm")) {
                    thePointA.add(new Triple(ez.getKey(), ((Pair) ez.getValue()).left, ((Pair) ez.getValue()).right));
                } else {
                    thePoint.add(new Triple(ez.getKey(), ((Pair) ez.getValue()).left, ((Pair) ez.getValue()).right));
                }
            }
            this.afterImage.put(b.getName().substring(0, b.getName().length() - 4), thePoint);
        }
        this.afterImage.put("katara", thePointK);
        this.afterImage.put("aran", thePointA);
    }

    public void runItems() {
        if (GameConstants.GMS) {
            MapleData fData = this.etcData.getData("FamiliarInfo.img");
            for (MapleData d : fData) {
                StructFamiliar f = new StructFamiliar();
                f.grade = 0;
                f.mob = MapleDataTool.getInt("mob", d, 0);
                f.passive = MapleDataTool.getInt("passive", d, 0);
                f.itemid = MapleDataTool.getInt("consume", d, 0);
                f.familiar = Integer.parseInt(d.getName());
                this.familiars.put(Integer.valueOf(f.familiar), f);
                this.familiars_Item.put(Integer.valueOf(f.itemid), f);
                this.familiars_Mob.put(Integer.valueOf(f.mob), f);
            }
            MapleDataDirectoryEntry e = (MapleDataDirectoryEntry) this.chrData.getRoot().getEntry("Familiar");
            for (MapleDataEntry d : e.getFiles()) {
                int id = Integer.parseInt(d.getName().substring(0, d.getName().length() - 4));
                if (this.familiars.containsKey(Integer.valueOf(id))) {
                    ((StructFamiliar) this.familiars.get(Integer.valueOf(id))).grade = (byte) MapleDataTool.getInt("grade", this.chrData.getData("Familiar/" + d.getName()).getChildByPath("info"), 0);
                }
            }

            MapleData mSetsData = this.etcData.getData("MonsterBookSet.img");
            for (MapleData d : mSetsData.getChildByPath("setList")) {
                if (MapleDataTool.getInt("deactivated", d, 0) > 0) {
                    continue;
                }
                List set = new ArrayList();
                List potential = new ArrayList(3);
                for (MapleData ds : d.getChildByPath("stats/potential")) {
                    if ((ds.getType() != MapleDataType.STRING) && (MapleDataTool.getInt(ds, 0) > 0)) {
                        potential.add(Integer.valueOf(MapleDataTool.getInt(ds, 0)));
                        if (potential.size() >= 5) {
                            break;
                        }
                    }
                }
                for (MapleData ds : d.getChildByPath("cardList")) {
                    set.add(Integer.valueOf(MapleDataTool.getInt(ds, 0)));
                }
                this.monsterBookSets.put(Integer.valueOf(Integer.parseInt(d.getName())), new Triple(Integer.valueOf(MapleDataTool.getInt("setScore", d, 0)), set, potential));
            }
        }
        try {
            Connection con = DatabaseConnectionWZ.getConnection();

            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_itemdata");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                initItemInformation(rs);
            }
            rs.close();
            ps.close();

            ps = con.prepareStatement("SELECT * FROM wz_itemequipdata ORDER BY itemid");
            rs = ps.executeQuery();
            while (rs.next()) {
                initItemEquipData(rs);
            }
            rs.close();
            ps.close();

            ps = con.prepareStatement("SELECT * FROM wz_itemadddata ORDER BY itemid");
            rs = ps.executeQuery();
            while (rs.next()) {
                initItemAddData(rs);
            }
            rs.close();
            ps.close();

            ps = con.prepareStatement("SELECT * FROM wz_itemrewarddata ORDER BY itemid");
            rs = ps.executeQuery();
            while (rs.next()) {
                initItemRewardData(rs);
            }
            rs.close();
            ps.close();

            for (Map.Entry entry : this.dataCache.entrySet()) {
                if (GameConstants.getInventoryType(((Integer) entry.getKey()).intValue()) == MapleInventoryType.EQUIP) {
                    finalizeEquipData((ItemInformation) entry.getValue());
                }
            }
        } catch (SQLException ex) {
            System.out.println("[ItemLoader] 加载装备数据出错." + ex);
        }
        System.out.println("共加载 " + this.dataCache.size() + " 个道具信息.");
    }

    public List<StructItemOption> getPotentialInfo(int potId) {
        return (List) this.potentialCache.get(Integer.valueOf(potId));
    }

    public Map<Integer, List<StructItemOption>> getAllPotentialInfo() {
        return this.potentialCache;
    }

    public StructItemOption getSocketInfo(int socketId) {
        int grade = GameConstants.getNebuliteGrade(socketId);
        if (grade == -1) {
            return null;
        }
        return (StructItemOption) ((Map) this.socketCache.get(Integer.valueOf(grade))).get(Integer.valueOf(socketId));
    }

    public Map<Integer, StructItemOption> getAllSocketInfo(int grade) {
        return (Map) this.socketCache.get(Integer.valueOf(grade));
    }

    public Collection<Integer> getMonsterBookList() {
        return this.mobIds.values();
    }

    public Map<Integer, Integer> getMonsterBook() {
        return this.mobIds;
    }

    public Pair<Integer, Integer> getPot(int f) {
        return (Pair) this.potLife.get(Integer.valueOf(f));
    }

    public StructFamiliar getFamiliar(int f) {
        return (StructFamiliar) this.familiars.get(Integer.valueOf(f));
    }

    public Map<Integer, StructFamiliar> getFamiliars() {
        return this.familiars;
    }

    public StructFamiliar getFamiliarByItem(int f) {
        return (StructFamiliar) this.familiars_Item.get(Integer.valueOf(f));
    }

    public StructFamiliar getFamiliarByMob(int f) {
        return (StructFamiliar) this.familiars_Mob.get(Integer.valueOf(f));
    }

    public static MapleItemInformationProvider getInstance() {
        return instance;
    }

    public Collection<ItemInformation> getAllItems() {
        return this.dataCache.values();
    }

    public Pair<List<Integer>, List<Integer>> getAndroidInfo(int i) {
        return (Pair) this.androids.get(Integer.valueOf(i));
    }

    public Triple<Integer, List<Integer>, List<Integer>> getMonsterBookInfo(int i) {
        return (Triple) this.monsterBookSets.get(Integer.valueOf(i));
    }

    public Map<Integer, Triple<Integer, List<Integer>, List<Integer>>> getAllMonsterBookInfo() {
        return this.monsterBookSets;
    }

    protected MapleData getItemData(int itemId) {
        MapleData ret = null;
        String idStr = "0" + String.valueOf(itemId);
        MapleDataDirectoryEntry root = itemData.getRoot();
        for (MapleDataDirectoryEntry topDir : root.getSubdirectories()) {
            for (MapleDataFileEntry iFile : topDir.getFiles()) {
                if (iFile.getName().equals(idStr.substring(0, 4) + ".img")) {
                    ret = this.itemData.getData(topDir.getName() + "/" + iFile.getName());
                    if (ret == null) {
                        return null;
                    }
                    ret = ret.getChildByPath(idStr);
                    return ret;
                }
                if (iFile.getName().equals(idStr.substring(1) + ".img")) {
                    ret = this.itemData.getData(topDir.getName() + "/" + iFile.getName());
                    return ret;
                }
            }
        }
        MapleDataDirectoryEntry topDir;
        root = this.chrData.getRoot();
        for (Iterator i$ = root.getSubdirectories().iterator(); i$.hasNext();) {
            topDir = (MapleDataDirectoryEntry) i$.next();
            for (MapleDataFileEntry iFile : topDir.getFiles()) {
                if (iFile.getName().equals(idStr + ".img")) {
                    ret = this.chrData.getData(topDir.getName() + "/" + iFile.getName());
                    return ret;
                }
            }
        }
        return ret;
    }

    public Integer getItemIdByMob(int mobId) {
        return (Integer) this.mobIds.get(Integer.valueOf(mobId));
    }

    public Integer getSetId(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return Integer.valueOf(i.cardSet);
    }

    public short getSlotMax(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return 0;
        }
        return i.slotMax;
    }

    public int getWholePrice(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return 0;
        }
        return i.wholePrice;
    }

    public double getPrice(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return -1.0D;
        }
        return i.price;
    }

    protected int rand(int min, int max) {
        return Math.abs(Randomizer.rand(min, max));
    }

    public Equip levelUpEquip(Equip equip, Map<String, Integer> sta) {
        Equip nEquip = (Equip) equip.copy();
        try {
            for (Entry<String, Integer> stat : sta.entrySet()) {
                if (stat.getKey().equals("STRMin")) {
                    nEquip.setStr((short) (nEquip.getStr() + rand(stat.getValue(), sta.get("STRMax"))));
                } else if (stat.getKey().equals("DEXMin")) {
                    nEquip.setDex((short) (nEquip.getDex() + rand(stat.getValue(), sta.get("DEXMax"))));
                } else if (stat.getKey().equals("INTMin")) {
                    nEquip.setInt((short) (nEquip.getInt() + rand(stat.getValue(), sta.get("INTMax"))));
                } else if (stat.getKey().equals("LUKMin")) {
                    nEquip.setLuk((short) (nEquip.getLuk() + rand(stat.getValue(), sta.get("LUKMax"))));
                } else if (stat.getKey().equals("PADMin")) {
                    nEquip.setWatk((short) (nEquip.getWatk() + rand(stat.getValue(), sta.get("PADMax"))));
                } else if (stat.getKey().equals("PDDMin")) {
                    nEquip.setWdef((short) (nEquip.getWdef() + rand(stat.getValue(), sta.get("PDDMax"))));
                } else if (stat.getKey().equals("MADMin")) {
                    nEquip.setMatk((short) (nEquip.getMatk() + rand(stat.getValue(), sta.get("MADMax"))));
                } else if (stat.getKey().equals("MDDMin")) {
                    nEquip.setMdef((short) (nEquip.getMdef() + rand(stat.getValue(), sta.get("MDDMax"))));
                } else if (stat.getKey().equals("ACCMin")) {
                    nEquip.setAcc((short) (nEquip.getAcc() + rand(stat.getValue(), sta.get("ACCMax"))));
                } else if (stat.getKey().equals("EVAMin")) {
                    nEquip.setAvoid((short) (nEquip.getAvoid() + rand(stat.getValue(), sta.get("EVAMax"))));
                } else if (stat.getKey().equals("SpeedMin")) {
                    nEquip.setSpeed((short) (nEquip.getSpeed() + rand(stat.getValue(), sta.get("SpeedMax"))));
                } else if (stat.getKey().equals("JumpMin")) {
                    nEquip.setJump((short) (nEquip.getJump() + rand(stat.getValue(), sta.get("JumpMax"))));
                } else if (stat.getKey().equals("MHPMin")) {
                    nEquip.setHp((short) (nEquip.getHp() + rand(stat.getValue(), sta.get("MHPMax"))));
                } else if (stat.getKey().equals("MMPMin")) {
                    nEquip.setMp((short) (nEquip.getMp() + rand(stat.getValue(), sta.get("MMPMax"))));
                } else if (stat.getKey().equals("MaxHPMin")) {
                    nEquip.setHp((short) (nEquip.getHp() + rand(stat.getValue(), sta.get("MaxHPMax"))));
                } else if (stat.getKey().equals("MaxMPMin")) {
                    nEquip.setMp((short) (nEquip.getMp() + rand(stat.getValue(), sta.get("MaxMPMax"))));
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return nEquip;
    }

    public EnumMap<EquipAdditions, Pair<Integer, Integer>> getEquipAdditions(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return i.equipAdditions;
    }

    public Map<Integer, Map<String, Integer>> getEquipIncrements(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return i.equipIncs;
    }

    public List<Integer> getEquipSkills(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return i.incSkill;
    }

    public Map<String, Integer> getEquipStats(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return i.equipStats;
    }

    public boolean canEquip(Map<String, Integer> stats, int itemid, int level, int job, int fame, int str, int dex, int luk, int int_, int supremacy) {
        if (level + supremacy >= (stats.containsKey("reqLevel") ? stats.get("reqLevel") : 0)) {
            if (str >= (stats.containsKey("reqSTR") ? stats.get("reqSTR") : 0)) {
                if (dex >= (stats.containsKey("reqDEX") ? stats.get("reqDEX") : 0)) {
                    if (luk >= (stats.containsKey("reqLUK") ? stats.get("reqLUK") : 0)) {
                        if (int_ >= (stats.containsKey("reqINT") ? stats.get("reqINT") : 0)) {
                            Integer fameReq = stats.get("reqPOP");

                            return (fameReq == null) || (fame >= fameReq.intValue());
                        }
                    }
                }
            }
        }



        return false;
    }

    public int getReqLevel(int itemId) {
        if ((getEquipStats(itemId) == null) || (!getEquipStats(itemId).containsKey("reqLevel"))) {
            return 0;
        }
        return getEquipStats(itemId).get("reqLevel");
    }

    public int getSlots(int itemId) {
        if ((getEquipStats(itemId) == null) || (!getEquipStats(itemId).containsKey("tuc"))) {
            return 0;
        }
        return getEquipStats(itemId).get("tuc");
    }

    public Integer getSetItemID(int itemId) {
        if ((getEquipStats(itemId) == null) || (!getEquipStats(itemId).containsKey("setItemID"))) {
            return 0;
        }
        return getEquipStats(itemId).get("setItemID");
    }

    public StructSetItem getSetItem(int setItemId) {
        return setItems.get((byte) setItemId);
    }

    public List<Integer> getScrollReqs(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return i.scrollReqs;
    }
//卷轴使用
    public Item scrollEquipWithId(Item equip, Item scrollId, boolean ws, MapleCharacter chr, int vegas) {
        if (equip.getType() == 1) {
            Equip nEquip = (Equip) equip;
            Map<String, Integer> stats = getEquipStats(scrollId.getItemId());
            Map<String, Integer> eqstats = getEquipStats(equip.getItemId());
            int succ = (GameConstants.is强化卷轴(scrollId.getItemId())) || (GameConstants.is潜能卷轴(scrollId.getItemId())) || (!stats.containsKey("success")) ? 0 : GameConstants.is饰品制炼书(scrollId.getItemId()) ? GameConstants.getSuccessTablet(scrollId.getItemId(), nEquip.getLevel()) : stats.get("success");
            int curse = (GameConstants.is强化卷轴(scrollId.getItemId())) || (GameConstants.is潜能卷轴(scrollId.getItemId())) || (!stats.containsKey("cursed")) ? 0 : GameConstants.is饰品制炼书(scrollId.getItemId()) ? GameConstants.getCurseTablet(scrollId.getItemId(), nEquip.getLevel()) : stats.get("cursed");
            int craft = GameConstants.is白医卷轴(scrollId.getItemId()) ? 0 : chr.getTrait(MapleTrait.MapleTraitType.craft).getLevel() / 10;
            //lucksKey--辛运日加成
            int lucksKey = ItemFlag.幸运卷轴.check(equip.getFlag()) ? 10 : 0;
            //added -- 额外成功率
            int added = (GameConstants.is潜能卷轴(scrollId.getItemId())) || (GameConstants.is强化卷轴(scrollId.getItemId())) ? 0 : lucksKey + craft;
            //success--总成功概率
            int success = succ + ((vegas == 5610001) && (succ == 60) ? 30 : (vegas == 5610000) && (succ == 10) ? 20 : 0) + added;

            //使用卷轴后如果有幸运日效果则减去
            if ((ItemFlag.幸运卷轴.check(equip.getFlag())) && (!GameConstants.is潜能卷轴(scrollId.getItemId())) && (!GameConstants.is强化卷轴(scrollId.getItemId())) && (!GameConstants.is特殊卷轴(scrollId.getItemId()))) {
                equip.setFlag((short) (equip.getFlag() - ItemFlag.幸运卷轴.getValue()));
            }
            if ((GameConstants.is潜能卷轴(scrollId.getItemId())) || (GameConstants.is强化卷轴(scrollId.getItemId())) || (GameConstants.is周年庆卷轴(scrollId.getItemId())) || (GameConstants.is特殊卷轴(scrollId.getItemId())) || (Randomizer.nextInt(100) <= success)) {
                switch (scrollId.getItemId()) {
                    case 2049000:
                    case 2049001:
                    case 2049002:
                    case 2049003:
                    case 2049004://白衣卷
                    case 2049005: {
                        if ((!eqstats.containsKey("tuc")) || (nEquip.getLevel() + nEquip.getUpgradeSlots() >= eqstats.get("tuc") + nEquip.getViciousHammer())) {
                            break;
                        }
                        nEquip.setUpgradeSlots((byte) (nEquip.getUpgradeSlots() + 1));
                        break;
                    }
                    case 2049006:
                    case 2049007:
                    case 2049008: {
                        if ((!eqstats.containsKey("tuc")) || (nEquip.getLevel() + nEquip.getUpgradeSlots() >= eqstats.get("tuc") + nEquip.getViciousHammer())) {
                            break;
                        }
                        nEquip.setUpgradeSlots((byte) (nEquip.getUpgradeSlots() + 2));
                        break;
                    }
                    case 2040727: {
                        short flag = nEquip.getFlag();
                        flag = (short) (flag | ItemFlag.鞋子防滑.getValue());
                        nEquip.setFlag(flag);
                        break;
                    }
                    case 2041058: {
                        short flag = nEquip.getFlag();
                        flag = (short) (flag | ItemFlag.披风防寒.getValue());
                        nEquip.setFlag(flag);
                        break;
                    }
                    case 2530000:
                         {
                        short flag = nEquip.getFlag();
                        flag = (short) (flag | ItemFlag.幸运卷轴.getValue());
                        nEquip.setFlag(flag);
                        break;
                    }
                    case 2530001:
                         {
                        short flag = nEquip.getFlag();
                        flag = (short) (flag | ItemFlag.幸运卷轴.getValue());
                        nEquip.setFlag(flag);
                        break;
                    }
                    case 2530002:
                         {
                        short flag = nEquip.getFlag();
                        flag = (short) (flag | ItemFlag.幸运卷轴.getValue());
                        nEquip.setFlag(flag);
                        break;
                    }
                    case 5063100: {
                        short flag = nEquip.getFlag();
                        flag = (short) (flag | ItemFlag.幸运卷轴.getValue());
                        nEquip.setFlag(flag);
                        break;
                    }
                    case 2531000:
                    case 5064000: {
                        short flag = nEquip.getFlag();
                        flag = (short) (flag | ItemFlag.防爆卷轴.getValue());
                        nEquip.setFlag(flag);
                        break;
                    }
                    case 5064100: {
                        short flag = nEquip.getFlag();
                        flag = (short) (flag | ItemFlag.保护卷轴.getValue());
                        nEquip.setFlag(flag);
                        break;
                    }
                    case 5064300: {
                        short flag = nEquip.getFlag();
                        flag = (short) (flag | ItemFlag.防护卷轴.getValue());
                        nEquip.setFlag(flag);
                        break;
                    }
                    default:
                        if (GameConstants.is枫叶卷轴(scrollId.getItemId())) {
                            
                            int z = GameConstants.getChaosNumber(scrollId.getItemId());
                            if (nEquip.getStr() > 0) {
                                nEquip.setStr((short) (nEquip.getStr() + (Randomizer.nextInt(z) + 1) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getDex() > 0) {
                                nEquip.setDex((short) (nEquip.getDex() + (Randomizer.nextInt(z) + 1) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getInt() > 0) {
                                nEquip.setInt((short) (nEquip.getInt() + (Randomizer.nextInt(z) + 1) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getLuk() > 0) {
                                nEquip.setLuk((short) (nEquip.getLuk() + (Randomizer.nextInt(z) + 1) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getWatk() > 0) {
                                nEquip.setWatk((short) (nEquip.getWatk() + (Randomizer.nextInt(z) + 1) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getWdef() > 0) {
                                nEquip.setWdef((short) (nEquip.getWdef() + (Randomizer.nextInt(z) + 1) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getMatk() > 0) {
                                nEquip.setMatk((short) (nEquip.getMatk() + (Randomizer.nextInt(z) + 1) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getMdef() > 0) {
                                nEquip.setMdef((short) (nEquip.getMdef() + (Randomizer.nextInt(z) + 1) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getAcc() > 0) {
                                nEquip.setAcc((short) (nEquip.getAcc() + (Randomizer.nextInt(z) + 1) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getAvoid() > 0) {
                                nEquip.setAvoid((short) (nEquip.getAvoid() + (Randomizer.nextInt(z) + 1) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getSpeed() > 0) {
                                nEquip.setSpeed((short) (nEquip.getSpeed() + (Randomizer.nextInt(z) + 1) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getJump() > 0) {
                                nEquip.setJump((short) (nEquip.getJump() + (Randomizer.nextInt(z) + 1) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getHp() > 0) {
                                nEquip.setHp((short) (nEquip.getHp() + (Randomizer.nextInt(z) + 1) * (Randomizer.nextBoolean() ? 1 : -1)));
                            }
                            if (nEquip.getMp() <= 0) {
                                break;
                            }
                            nEquip.setMp((short) (nEquip.getMp() + (Randomizer.nextInt(z) + 1) * (Randomizer.nextBoolean() ? 1 : -1)));
                            
                            
                        } else if (GameConstants.is周年庆卷轴(scrollId.getItemId())) {
                            
                            int 成功率 = GameConstants.is周年庆卷轴成功率(scrollId.getItemId()) + added;
                            
                            //下面这句 是 使用卷轴失败
                            if (Randomizer.nextInt(100) <= 成功率) {
                                return null;
                            }
                            
                            switch (scrollId.getItemId()) {
                                //+7 物攻
                                case 2046006:
                                case 2046008:
                                case 2046010:
                                case 2046106:
                                case 2046108:
                                case 2046110:
                                case 2046119:
                                    if (nEquip.getWatk() >= 0) {
                                        nEquip.setWatk((short) (nEquip.getWatk() + 7));
                                    }
                                    break;
                            
                                    // +7 魔攻
                                case 2046007:
                                case 2046026:
                                case 2046107:
                                case 2046120:
                                    if (nEquip.getMatk() >= 0) {
                                        nEquip.setMatk((short) (nEquip.getMatk() + 7));
                                    }
                                    break;
                                    //最大HP+200，最大MP+200
                                case 2046213://周年庆防具强化卷轴
                                case 2046308://周年庆饰品强化卷轴
                                    if ((nEquip.getHp() >= 0) || (nEquip.getMp() >= 0)) {
                                        nEquip.setHp((short) (nEquip.getHp() + 200));
                                        nEquip.setMp((short) (nEquip.getMp() + 200));
                                       
                                    }
                                    break;
                                    //力量+2，智力+2，敏捷+2，运气+2
                                case 2046214:
                                case 2046222:
                                case 2046309:
                                case 2046313:
                                    if ((nEquip.getStr() >= 0) || (nEquip.getDex() >= 0) || (nEquip.getInt() >= 0) || (nEquip.getLuk() >= 0)) {
                                        nEquip.setStr((short) (nEquip.getStr() + 2));
                                        nEquip.setDex((short) (nEquip.getDex() + 2));
                                        nEquip.setInt((short) (nEquip.getInt() + 2));
                                        nEquip.setLuk((short) (nEquip.getLuk() + 2));
                                       
                                    }
                                    break;
                                    //MaxHP +70, MaxMP +70
                                case 2046219:
                                case 2046221:
                                case 2046310:
                                case 2046312:
                                    if ((nEquip.getHp() >= 0) || (nEquip.getMp() >= 0)) {
                                        nEquip.setHp((short) (nEquip.getHp() + 70));
                                        nEquip.setMp((short) (nEquip.getMp() + 70));
                                       
                                    }
                                    break;
                                case 2043108://【周年庆】单手斧攻击卷轴   物理攻击力+3,力量+2\
                                case 2043208://【周年庆】单手钝器攻击卷轴
                                case 2044008://【周年庆】双手剑攻击卷轴
                                case 2044108://【周年庆】双手斧攻击卷轴
                                case 2044208://【周年庆】双手钝器攻击卷轴
                                case 2044308://【周年庆】枪攻击卷轴
                                case 2044408://【周年庆】矛攻击卷轴
                                case 2044810://【周年庆】指节攻击卷轴40%
                                    if ((nEquip.getWatk() >= 0) || nEquip.getStr() >= 0) {
                                        nEquip.setWatk((short) (nEquip.getWatk() + 3));
                                        nEquip.setStr((short) (nEquip.getStr() + 2));
                                    }
                                    break;
                                    
                                case 2043308://【周年庆】短剑攻击卷轴  物理攻击力+3,运气+2\
                                case 2043405://【周年庆】刀攻击力卷轴
                                    if ((nEquip.getWatk() >= 0) || nEquip.getLuk() >= 0) {
                                        nEquip.setWatk((short) (nEquip.getWatk() + 3));
                                        nEquip.setLuk((short) (nEquip.getLuk() + 2));
                                    }
                                    break;
                                    
                                case 2043708://【周年庆】短杖魔力卷轴  
                                case 2043808://【周年庆】长杖魔力卷轴   魔法攻击力+3，智力+2
                                    if ((nEquip.getMatk() >= 0) || nEquip.getInt() >= 0) {
                                        nEquip.setMatk((short) (nEquip.getMatk() + 3));
                                        nEquip.setInt((short) (nEquip.getInt() + 2));
                                    }
                                    break;
                                    
                                case 2044508://【周年庆】弓攻击卷轴  命中值+20
                                    if ((nEquip.getWatk() >= 0) || nEquip.getAcc() >= 0) {
                                        nEquip.setWatk((short) (nEquip.getWatk() + 3));
                                        nEquip.setAcc((short) (nEquip.getAcc() + 20));
                                    }
                                    break;
                                    
                                case 2044608://【周年庆】弩攻击卷轴  命中值+40
                                case 2044708://【周年庆】拳套攻击卷轴
                                case 2044905://【周年庆】短枪攻击卷轴40%
                                    if ((nEquip.getWatk() >= 0) || nEquip.getAcc() >= 0) {
                                        nEquip.setWatk((short) (nEquip.getWatk() + 3));
                                        nEquip.setAcc((short) (nEquip.getAcc() + 40));
                                    }
                                    break;
                                
                            }
                            
                        } else if (GameConstants.is强化卷轴(scrollId.getItemId())) {
                            //chanc 强化成功最大概率
                            int chanc = Math.max((((scrollId.getItemId() == 2049308) || (scrollId.getItemId() == 2049310)) ? 50 : scrollId.getItemId() == 2049309 ? 80 : scrollId.getItemId() == 2049305 ? 60 : (scrollId.getItemId() == 2049300) || (scrollId.getItemId() == 2049303) ? 100 : 80) - nEquip.getEnhance() * 10, 10) + added;
                            //下面这句 是 使用卷轴失败
                            if (Randomizer.nextInt(100) > chanc) {
                                return null;
                            }
                            //for 循环，执行装备强化次数
                            for (int i = 0; i < (((scrollId.getItemId() == 2049308) || (scrollId.getItemId() == 2049310)) ? 5 : scrollId.getItemId() == 2049309 ? 2 : scrollId.getItemId() == 2049304 ? 3 : scrollId.getItemId() == 2049305 ? 4 : 1); i++) {
                                if ((nEquip.getStr() > 0) || (Randomizer.nextInt(50) == 1)) {
                                    nEquip.setStr((short) (nEquip.getStr() + Randomizer.nextInt(5)));
                                }
                                if ((nEquip.getDex() > 0) || (Randomizer.nextInt(50) == 1)) {
                                    nEquip.setDex((short) (nEquip.getDex() + Randomizer.nextInt(5)));
                                }
                                if ((nEquip.getInt() > 0) || (Randomizer.nextInt(50) == 1)) {
                                    nEquip.setInt((short) (nEquip.getInt() + Randomizer.nextInt(5)));
                                }
                                if ((nEquip.getLuk() > 0) || (Randomizer.nextInt(50) == 1)) {
                                    nEquip.setLuk((short) (nEquip.getLuk() + Randomizer.nextInt(5)));
                                }
                                if ((nEquip.getWatk() > 0) && (GameConstants.isWeapon(nEquip.getItemId()))) {
                                    nEquip.setWatk((short) (nEquip.getWatk() + Randomizer.nextInt(5)));
                                }
                                if ((nEquip.getWdef() > 0) || (Randomizer.nextInt(40) == 1)) {
                                    nEquip.setWdef((short) (nEquip.getWdef() + Randomizer.nextInt(5)));
                                }
                                if ((nEquip.getMatk() > 0) && (GameConstants.isWeapon(nEquip.getItemId()))) {
                                    nEquip.setMatk((short) (nEquip.getMatk() + Randomizer.nextInt(5)));
                                }
                                if ((nEquip.getMdef() > 0) || (Randomizer.nextInt(40) == 1)) {
                                    nEquip.setMdef((short) (nEquip.getMdef() + Randomizer.nextInt(5)));
                                }
                                if ((nEquip.getAcc() > 0) || (Randomizer.nextInt(20) == 1)) {
                                    nEquip.setAcc((short) (nEquip.getAcc() + Randomizer.nextInt(5)));
                                }
                                if ((nEquip.getAvoid() > 0) || (Randomizer.nextInt(20) == 1)) {
                                    nEquip.setAvoid((short) (nEquip.getAvoid() + Randomizer.nextInt(5)));
                                }
                                if ((nEquip.getSpeed() > 0) || (Randomizer.nextInt(10) == 1)) {
                                    nEquip.setSpeed((short) (nEquip.getSpeed() + Randomizer.nextInt(5)));
                                }
                                if ((nEquip.getJump() > 0) || (Randomizer.nextInt(10) == 1)) {
                                    nEquip.setJump((short) (nEquip.getJump() + Randomizer.nextInt(5)));
                                }
                                if ((nEquip.getHp() > 0) || (Randomizer.nextInt(5) == 1)) {
                                    nEquip.setHp((short) (nEquip.getHp() + Randomizer.nextInt(5)));
                                }
                                if ((nEquip.getMp() > 0) || (Randomizer.nextInt(5) == 1)) {
                                    nEquip.setMp((short) (nEquip.getMp() + Randomizer.nextInt(5)));
                                }
                                nEquip.setEnhance((byte) (nEquip.getEnhance() + 1));
                            }

                        } else if (GameConstants.is潜能卷轴(scrollId.getItemId())) {
                            if ((nEquip.getState() <= 17) && (scrollId.getItemId() / 100 == 20497)) {
                                int chanc = (scrollId.getItemId() == 2049700 ? 100 : 80) + added;
                                if (Randomizer.nextInt(100) > chanc) {
                                    return null;
                                }
                                //附加A级潜能
                                if (scrollId.getItemId()/100 == 20497) {
                                    nEquip.resetPotentialA();//附加A级潜能
                                } else {
                                nEquip.renewPotential(2);
                                }
                            } else {
                                if (nEquip.getState() != 0) {
                                    break;
                                }
                                //chanc 潜能附加成功概率
                                int chanc = (scrollId.getItemId() == 2049400 ? 90 : (scrollId.getItemId() == 5534000) || (scrollId.getItemId() == 2049402) || (scrollId.getItemId() == 2049405) || (scrollId.getItemId() == 2049406) ? 100 : 70) + added;
                                if (Randomizer.nextInt(100) > chanc) {
                                    return null;
                                }
                                //附加A级潜能
                            //    if (scrollId.getItemId()/100 == 20497) {
                              //      nEquip.resetPotentialA();//附加A级潜能
                              //  } else {
                                nEquip.resetPotential();
                              //  }
                            }
                        } else {
                            //普通卷子 砸了之后。
                            for (Entry<String, Integer> stat : stats.entrySet()) {
                                String key = (String) stat.getKey();
                                if (key.equals("STR")) {
                                    nEquip.setStr((short) (nEquip.getStr() + stat.getValue()));
                                } else if (key.equals("DEX")) {
                                    nEquip.setDex((short) (nEquip.getDex() + stat.getValue()));
                                } else if (key.equals("INT")) {
                                    nEquip.setInt((short) (nEquip.getInt() + stat.getValue()));
                                } else if (key.equals("LUK")) {
                                    nEquip.setLuk((short) (nEquip.getLuk() + stat.getValue()));
                                } else if (key.equals("PAD")) {
                                    nEquip.setWatk((short) (nEquip.getWatk() + stat.getValue()));
                                } else if (key.equals("PDD")) {
                                    nEquip.setWdef((short) (nEquip.getWdef() + stat.getValue()));
                                } else if (key.equals("MAD")) {
                                    nEquip.setMatk((short) (nEquip.getMatk() + stat.getValue()));
                                } else if (key.equals("MDD")) {
                                    nEquip.setMdef((short) (nEquip.getMdef() + stat.getValue()));
                                } else if (key.equals("ACC")) {
                                    nEquip.setAcc((short) (nEquip.getAcc() + stat.getValue()));
                                } else if (key.equals("EVA")) {
                                    nEquip.setAvoid((short) (nEquip.getAvoid() + stat.getValue()));
                                } else if (key.equals("Speed")) {
                                    nEquip.setSpeed((short) (nEquip.getSpeed() + stat.getValue()));
                                } else if (key.equals("Jump")) {
                                    nEquip.setJump((short) (nEquip.getJump() + stat.getValue()));
                                } else if (key.equals("MHP")) {
                                    nEquip.setHp((short) (nEquip.getHp() + stat.getValue()));
                                } else if (key.equals("MMP")) {
                                    nEquip.setMp((short) (nEquip.getMp() + stat.getValue()));
                                }
                            }
                            /*
                            if (GameConstants.is必成卷轴(scrollId.getItemId())) {
                               short oldFlag = nEquip.getFlag();
                               if (ItemFlag.保护卷轴.check(oldFlag)) {
                                  nEquip.setFlag((short) (oldFlag - ItemFlag.保护卷轴.getValue()));
                               } else {
                                  nEquip.setUpgradeSlots((byte) (nEquip.getUpgradeSlots() - 1));
                                  nEquip.setLevel((byte) (nEquip.getLevel() + 1));
                              }
                           }
                           * 
                           */
                            
                        }

                }
                
                //普通卷轴这里砸卷适用！

                if ((!GameConstants.is白医卷轴(scrollId.getItemId())) && (!GameConstants.is特殊卷轴(scrollId.getItemId())) && (!GameConstants.is强化卷轴(scrollId.getItemId())) && (!GameConstants.is周年庆卷轴(scrollId.getItemId())) && (!GameConstants.is必成卷轴(scrollId.getItemId())) && (!GameConstants.is潜能卷轴(scrollId.getItemId()))) {
                    short oldFlag = nEquip.getFlag();
                    if (ItemFlag.保护卷轴.check(oldFlag)) {
                        nEquip.setFlag((short) (oldFlag - ItemFlag.保护卷轴.getValue()));
                    }
                    nEquip.setUpgradeSlots((byte) (nEquip.getUpgradeSlots() - 1));
                    nEquip.setLevel((byte) (nEquip.getLevel() + 1));
                }
            } else {//711  砸卷失败？
                if (((!ws) && (!GameConstants.is白医卷轴(scrollId.getItemId())) && (!GameConstants.is特殊卷轴(scrollId.getItemId())) && (!GameConstants.is强化卷轴(scrollId.getItemId())) && (!GameConstants.is潜能卷轴(scrollId.getItemId()))) || ((!GameConstants.is周年庆卷轴(scrollId.getItemId())) && (!GameConstants.is必成卷轴(scrollId.getItemId())))) {
                    short oldFlag = nEquip.getFlag();
                    if (ItemFlag.保护卷轴.check(oldFlag)) {
                        nEquip.setFlag((short) (oldFlag - ItemFlag.保护卷轴.getValue()));
                    } else {
                        nEquip.setUpgradeSlots((byte) (nEquip.getUpgradeSlots() - 1));
                    }
                }
                if (Randomizer.nextInt(99) < curse) {
                    return null;
                }
            }
            
            if (GameConstants.is必成卷轴(scrollId.getItemId()) || GameConstants.is周年庆卷轴(scrollId.getItemId())) {
                               short oldFlag = nEquip.getFlag();
                               if (ItemFlag.保护卷轴.check(oldFlag)) {
                                  nEquip.setFlag((short) (oldFlag - ItemFlag.保护卷轴.getValue()));
                               } else {
                                  nEquip.setUpgradeSlots((byte) (nEquip.getUpgradeSlots() - 1));
                                  nEquip.setLevel((byte) (nEquip.getLevel() + 1));
                              }
                           }
            
            
        }
         
        
        return equip;
    }

    public Item getEquipById(int equipId) {
        return getEquipById(equipId, -1);
    }

    public Item getEquipById(int equipId, int ringId) {
        ItemInformation i = getItemInformation(equipId);
        if (i == null) {
            return new Equip(equipId, (short) 0, ringId, (short) 0);
        }
        Item eq = i.eq.copy();
        eq.setUniqueId(ringId);
        return eq;
    }

    protected short getRandStatFusion(short defaultValue, int value1, int value2) {
        if (defaultValue == 0) {
            return 0;
        }
        int range = (value1 + value2) / 2 - defaultValue;
        int rand = Randomizer.nextInt(Math.abs(range) + 1);
        return (short) (defaultValue + (range < 0 ? -rand : rand));
    }

    protected short getRandStat(short defaultValue, int maxRange) {
        if (defaultValue == 0) {
            return 0;
        }

        int lMaxRange = (int) Math.min(Math.ceil(defaultValue * 0.1D), maxRange);
        return (short) (defaultValue - lMaxRange + Randomizer.nextInt(lMaxRange * 2 + 1));
    }

    protected short getRandStatAbove(short defaultValue, int maxRange) {
        if (defaultValue <= 0) {
            return 0;
        }
        int lMaxRange = (int) Math.min(Math.ceil(defaultValue * 0.1D), maxRange);
        return (short) (defaultValue + Randomizer.nextInt(lMaxRange + 1));
    }

    public Equip randomizeStats(Equip equip) {
        equip.setStr(getRandStat(equip.getStr(), 5));
        equip.setDex(getRandStat(equip.getDex(), 5));
        equip.setInt(getRandStat(equip.getInt(), 5));
        equip.setLuk(getRandStat(equip.getLuk(), 5));
        equip.setMatk(getRandStat(equip.getMatk(), 5));
        equip.setWatk(getRandStat(equip.getWatk(), 5));
        equip.setAcc(getRandStat(equip.getAcc(), 5));
        equip.setAvoid(getRandStat(equip.getAvoid(), 5));
        equip.setJump(getRandStat(equip.getJump(), 5));
        equip.setHands(getRandStat(equip.getHands(), 5));
        equip.setSpeed(getRandStat(equip.getSpeed(), 5));
        equip.setWdef(getRandStat(equip.getWdef(), 10));
        equip.setMdef(getRandStat(equip.getMdef(), 10));
        equip.setHp(getRandStat(equip.getHp(), 10));
        equip.setMp(getRandStat(equip.getMp(), 10));
        return equip;
    }

    public Equip randomizeStats_Above(Equip equip) {
        equip.setStr(getRandStatAbove(equip.getStr(), 5));
        equip.setDex(getRandStatAbove(equip.getDex(), 5));
        equip.setInt(getRandStatAbove(equip.getInt(), 5));
        equip.setLuk(getRandStatAbove(equip.getLuk(), 5));
        equip.setMatk(getRandStatAbove(equip.getMatk(), 5));
        equip.setWatk(getRandStatAbove(equip.getWatk(), 5));
        equip.setAcc(getRandStatAbove(equip.getAcc(), 5));
        equip.setAvoid(getRandStatAbove(equip.getAvoid(), 5));
        equip.setJump(getRandStatAbove(equip.getJump(), 5));
        equip.setHands(getRandStatAbove(equip.getHands(), 5));
        equip.setSpeed(getRandStatAbove(equip.getSpeed(), 5));
        equip.setWdef(getRandStatAbove(equip.getWdef(), 10));
        equip.setMdef(getRandStatAbove(equip.getMdef(), 10));
        equip.setHp(getRandStatAbove(equip.getHp(), 10));
        equip.setMp(getRandStatAbove(equip.getMp(), 10));
        return equip;
    }

    public Equip fuse(Equip equip1, Equip equip2) {
        if (equip1.getItemId() != equip2.getItemId()) {
            return equip1;
        }
        Equip equip = (Equip) getEquipById(equip1.getItemId());
        equip.setStr(getRandStatFusion(equip.getStr(), equip1.getStr(), equip2.getStr()));
        equip.setDex(getRandStatFusion(equip.getDex(), equip1.getDex(), equip2.getDex()));
        equip.setInt(getRandStatFusion(equip.getInt(), equip1.getInt(), equip2.getInt()));
        equip.setLuk(getRandStatFusion(equip.getLuk(), equip1.getLuk(), equip2.getLuk()));
        equip.setMatk(getRandStatFusion(equip.getMatk(), equip1.getMatk(), equip2.getMatk()));
        equip.setWatk(getRandStatFusion(equip.getWatk(), equip1.getWatk(), equip2.getWatk()));
        equip.setAcc(getRandStatFusion(equip.getAcc(), equip1.getAcc(), equip2.getAcc()));
        equip.setAvoid(getRandStatFusion(equip.getAvoid(), equip1.getAvoid(), equip2.getAvoid()));
        equip.setJump(getRandStatFusion(equip.getJump(), equip1.getJump(), equip2.getJump()));
        equip.setHands(getRandStatFusion(equip.getHands(), equip1.getHands(), equip2.getHands()));
        equip.setSpeed(getRandStatFusion(equip.getSpeed(), equip1.getSpeed(), equip2.getSpeed()));
        equip.setWdef(getRandStatFusion(equip.getWdef(), equip1.getWdef(), equip2.getWdef()));
        equip.setMdef(getRandStatFusion(equip.getMdef(), equip1.getMdef(), equip2.getMdef()));
        equip.setHp(getRandStatFusion(equip.getHp(), equip1.getHp(), equip2.getHp()));
        equip.setMp(getRandStatFusion(equip.getMp(), equip1.getMp(), equip2.getMp()));
        return equip;
    }

    public int getTotalStat(Equip equip) {
        return equip.getStr() + equip.getDex() + equip.getInt() + equip.getLuk() + equip.getMatk() + equip.getWatk() + equip.getAcc() + equip.getAvoid() + equip.getJump() + equip.getHands() + equip.getSpeed() + equip.getHp() + equip.getMp() + equip.getWdef() + equip.getMdef();
    }

    public MapleStatEffect getItemEffect(int itemId) {
        MapleStatEffect ret = itemEffects.get(itemId);
        if (ret == null) {
            MapleData item = getItemData(itemId);
            if ((item == null) || (item.getChildByPath("spec") == null)) {
                return null;
            }
            ret = MapleStatEffect.loadItemEffectFromData(item.getChildByPath("spec"), itemId);
            itemEffects.put(itemId, ret);
        }
        return ret;
    }

    public MapleStatEffect getItemEffectEX(int itemId) {
        MapleStatEffect ret = itemEffectsEx.get(itemId);
        if (ret == null) {
            MapleData item = getItemData(itemId);
            if ((item == null) || (item.getChildByPath("specEx") == null)) {
                return null;
            }
            ret = MapleStatEffect.loadItemEffectFromData(item.getChildByPath("specEx"), itemId);
            itemEffectsEx.put(itemId, ret);
        }
        return ret;
    }

    public int getCreateId(int id) {
        ItemInformation i = getItemInformation(id);
        if (i == null) {
            return 0;
        }
        return i.create;
    }

    public int getCardMobId(int id) {
        ItemInformation i = getItemInformation(id);
        if (i == null) {
            return 0;
        }
        return i.monsterBook;
    }

    public int getBagType(int id) {
        ItemInformation i = getItemInformation(id);
        if (i == null) {
            return 0;
        }
        return i.flag & 0xF;
    }

    public int getWatkForProjectile(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if ((i == null) || (i.equipStats == null) || (i.equipStats.get("incPAD") == null)) {
            return 0;
        }
        return ((Integer) i.equipStats.get("incPAD")).intValue();
    }
//判断是卷轴？
    public boolean canScroll(int scrollid, int itemid) {
        return (scrollid / 100 % 100 == itemid / 10000 % 100) || ((itemid >= 1672000) && (itemid <= 1672010));
    }

    public String getName(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return i.name;
    }

    public String getDesc(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return i.desc;
    }

    public String getMsg(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return i.msg;
    }

    public short getItemMakeLevel(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return 0;
        }
        return i.itemMakeLevel;
    }

    public boolean isDropRestricted(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return ((i.flag & 0x200) != 0) || ((i.flag & 0x400) != 0) || (GameConstants.isDropRestricted(itemId));
    }

    public boolean isPickupRestricted(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return (((i.flag & 0x80) != 0) || (GameConstants.isPickupRestricted(itemId))) && (itemId != 4001168);
    }

    public boolean isAccountShared(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return (i.flag & 0x100) != 0;
    }

    public int getStateChangeItem(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return 0;
        }
        return i.stateChange;
    }

    public int getMeso(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return 0;
        }
        return i.meso;
    }

    public boolean isShareTagEnabled(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return (i.flag & 0x800) != 0;
    }

    public boolean isKarmaEnabled(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return i.karmaEnabled == 1;
    }

    public boolean isPKarmaEnabled(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return i.karmaEnabled == 2;
    }

    public boolean isPickupBlocked(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return (i.flag & 0x40) != 0;
    }

    public boolean isLogoutExpire(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return (i.flag & 0x20) != 0;
    }

    public boolean cantSell(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return (i.flag & 0x10) != 0;
    }

    public Pair<Integer, List<StructRewardItem>> getRewardItem(int itemid) {
        ItemInformation i = getItemInformation(itemid);
        if (i == null) {
            return null;
        }
        return new Pair<Integer, List<StructRewardItem>>(i.totalprob, i.rewardItems);
    }

    public boolean isMobHP(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return (i.flag & 0x1000) != 0;
    }

    public boolean isQuestItem(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return ((i.flag & 0x200) != 0) && (itemId / 10000 != 301);
    }

    public Pair<Integer, List<Integer>> questItemInfo(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return new Pair<Integer, List<Integer>>(i.questId, i.questItems);
    }

    public Pair<Integer, String> replaceItemInfo(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return new Pair<Integer, String>(i.replaceItem, i.replaceMsg);
    }

    public List<Triple<String, Point, Point>> getAfterImage(String after) {
        return afterImage.get(after);
    }

    public String getAfterImage(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return i.afterImage;
    }

    public boolean itemExists(int itemId) {
        if (GameConstants.getInventoryType(itemId) == MapleInventoryType.UNDEFINED) {
            return false;
        }
        return getItemInformation(itemId) != null;
    }

    public boolean isCash(int itemId) {
        if (getEquipStats(itemId) == null) {
            return GameConstants.getInventoryType(itemId) == MapleInventoryType.CASH;
        }
        return (GameConstants.getInventoryType(itemId) == MapleInventoryType.CASH) || (getEquipStats(itemId).get("cash") != null);
    }

    public ItemInformation getItemInformation(int itemId) {
        if (itemId <= 0) {
            return null;
        }
        return dataCache.get(itemId);
    }

    public void initItemRewardData(ResultSet sqlRewardData) throws SQLException {
        int itemID = sqlRewardData.getInt("itemid");
        if ((this.tmpInfo == null) || (this.tmpInfo.itemId != itemID)) {
            if (!this.dataCache.containsKey(itemID)) {
                System.out.println("[initItemRewardData] Tried to load an item while this is not in the cache: " + itemID);
                return;
            }
            this.tmpInfo = dataCache.get(itemID);
        }

        if (this.tmpInfo.rewardItems == null) {
            this.tmpInfo.rewardItems = new ArrayList();
        }

        StructRewardItem add = new StructRewardItem();
        add.itemid = sqlRewardData.getInt("item");
        add.period = (add.itemid == 1122017 ? Math.max(sqlRewardData.getInt("period"), 7200) : sqlRewardData.getInt("period"));
        add.prob = sqlRewardData.getInt("prob");
        add.quantity = sqlRewardData.getShort("quantity");
        add.worldmsg = (sqlRewardData.getString("worldMsg").length() <= 0 ? null : sqlRewardData.getString("worldMsg"));
        add.effect = sqlRewardData.getString("effect");

        this.tmpInfo.rewardItems.add(add);
    }

    public void initItemAddData(ResultSet sqlAddData) throws SQLException {
        int itemID = sqlAddData.getInt("itemid");
        if ((tmpInfo == null) || (tmpInfo.itemId != itemID)) {
            if (!dataCache.containsKey(itemID)) {
                System.out.println("[initItemAddData] Tried to load an item while this is not in the cache: " + itemID);
                return;
            }
            tmpInfo = dataCache.get(itemID);
        }

        if (tmpInfo.equipAdditions == null) {
            tmpInfo.equipAdditions = new EnumMap<EquipAdditions, Pair<Integer, Integer>>(EquipAdditions.class);
        }

        EquipAdditions z = EquipAdditions.fromString(sqlAddData.getString("key"));
        if (z != null) {
            this.tmpInfo.equipAdditions.put(z, new Pair<Integer, Integer>(sqlAddData.getInt("value1"), sqlAddData.getInt("value2")));
        }
    }

    public void initItemEquipData(ResultSet sqlEquipData) throws SQLException {
        int itemID = sqlEquipData.getInt("itemid");
        if ((this.tmpInfo == null) || (this.tmpInfo.itemId != itemID)) {
            if (!this.dataCache.containsKey(itemID)) {
                System.out.println("[initItemEquipData] Tried to load an item while this is not in the cache: " + itemID);
                return;
            }
            this.tmpInfo = dataCache.get(itemID);
        }

        if (this.tmpInfo.equipStats == null) {
            this.tmpInfo.equipStats = new HashMap<String, Integer>();
        }

        int itemLevel = sqlEquipData.getInt("itemLevel");
        if (itemLevel == -1) {
            this.tmpInfo.equipStats.put(sqlEquipData.getString("key"), sqlEquipData.getInt("value"));
        } else {
            if (this.tmpInfo.equipIncs == null) {
                this.tmpInfo.equipIncs = new HashMap<Integer, Map<String, Integer>>();
            }

            Map<String, Integer> toAdd = tmpInfo.equipIncs.get(itemLevel);
            if (toAdd == null) {
                toAdd = new HashMap<String, Integer>();
                tmpInfo.equipIncs.put(itemLevel, toAdd);
            }
            toAdd.put(sqlEquipData.getString("key"), sqlEquipData.getInt("value"));
        }
    }

    public void finalizeEquipData(ItemInformation item) {
        int itemId = item.itemId;

        if (item.equipStats == null) {
            item.equipStats = new HashMap<String, Integer>();
        }

        item.eq = new Equip(itemId, (short) 0, -1, (short) 0);
        short stats = GameConstants.getStat(itemId, 0);
        if (stats > 0) {
            item.eq.setStr(stats);
            item.eq.setDex(stats);
            item.eq.setInt(stats);
            item.eq.setLuk(stats);
        }
        stats = GameConstants.getATK(itemId, 0);
        if (stats > 0) {
            item.eq.setWatk(stats);
            item.eq.setMatk(stats);
        }
        stats = GameConstants.getHpMp(itemId, 0);
        if (stats > 0) {
            item.eq.setHp(stats);
            item.eq.setMp(stats);
        }
        stats = GameConstants.getDEF(itemId, 0);
        if (stats > 0) {
            item.eq.setWdef(stats);
            item.eq.setMdef(stats);
        }
        if (item.equipStats.size() > 0) {
            for (Entry<String, Integer> stat : item.equipStats.entrySet()) {
                String key = stat.getKey();
                if (key.equals("STR")) {
                    item.eq.setStr(GameConstants.getStat(itemId, stat.getValue()));
                } else if (key.equals("DEX")) {
                    item.eq.setDex(GameConstants.getStat(itemId, stat.getValue()));
                } else if (key.equals("INT")) {
                    item.eq.setInt(GameConstants.getStat(itemId, stat.getValue()));
                } else if (key.equals("LUK")) {
                    item.eq.setLuk(GameConstants.getStat(itemId, stat.getValue()));
                } else if (key.equals("PAD")) {
                    item.eq.setWatk(GameConstants.getATK(itemId, stat.getValue()));
                } else if (key.equals("PDD")) {
                    item.eq.setWdef(GameConstants.getDEF(itemId, stat.getValue()));
                } else if (key.equals("MAD")) {
                    item.eq.setMatk(GameConstants.getATK(itemId, stat.getValue()));
                } else if (key.equals("MDD")) {
                    item.eq.setMdef(GameConstants.getDEF(itemId, stat.getValue()));
                } else if (key.equals("ACC")) {
                    item.eq.setAcc((short) (stat.getValue()).intValue());
                } else if (key.equals("EVA")) {
                    item.eq.setAvoid((short) (stat.getValue()).intValue());
                } else if (key.equals("Speed")) {
                    item.eq.setSpeed((short) (stat.getValue()).intValue());
                } else if (key.equals("Jump")) {
                    item.eq.setJump((short) (stat.getValue()).intValue());
                } else if (key.equals("MHP")) {
                    item.eq.setHp(GameConstants.getHpMp(itemId, stat.getValue()));
                } else if (key.equals("MMP")) {
                    item.eq.setMp(GameConstants.getHpMp(itemId, stat.getValue()));
                } else if (key.equals("tuc")) {
                    item.eq.setUpgradeSlots((stat.getValue()).byteValue());
                } else if (key.equals("Craft")) {
                    item.eq.setHands((stat.getValue()).shortValue());
                } else if (key.equals("durability")) {
                    item.eq.setDurability((stat.getValue()).intValue());
                } else if (key.equals("charmEXP")) {
                    item.eq.setCharmEXP((stat.getValue()).shortValue());
                } else if (key.equals("PVPDamage")) {
                    item.eq.setPVPDamage((stat.getValue()).shortValue());
                }
            }
            if ((item.equipStats.get("cash") != null) && (item.eq.getCharmEXP() <= 0)) {
                short exp = 0;
                int identifier = itemId / 10000;
                if ((GameConstants.isWeapon(itemId)) || (identifier == 106)) {
                    exp = 60;
                } else if (identifier == 100) {
                    exp = 50;
                } else if ((GameConstants.isAccessory(itemId)) || (identifier == 102) || (identifier == 108) || (identifier == 107)) {
                    exp = 40;
                } else if ((identifier == 104) || (identifier == 105) || (identifier == 110)) {
                    exp = 30;
                }
                item.eq.setCharmEXP(exp);
            }
        }
    }

    public void initItemInformation(ResultSet sqlItemData) throws SQLException {
        ItemInformation ret = new ItemInformation();
        int itemId = sqlItemData.getInt("itemid");
        ret.itemId = itemId;
        ret.slotMax = (GameConstants.getSlotMax(itemId) > 0 ? GameConstants.getSlotMax(itemId) : sqlItemData.getShort("slotMax"));
        ret.price = Double.parseDouble(sqlItemData.getString("price"));
        ret.wholePrice = sqlItemData.getInt("wholePrice");
        ret.stateChange = sqlItemData.getInt("stateChange");
        ret.name = sqlItemData.getString("name");
        ret.desc = sqlItemData.getString("desc");
        ret.msg = sqlItemData.getString("msg");

        ret.flag = sqlItemData.getInt("flags");

        ret.karmaEnabled = sqlItemData.getByte("karma");
        ret.meso = sqlItemData.getInt("meso");
        ret.monsterBook = sqlItemData.getInt("monsterBook");
        ret.itemMakeLevel = sqlItemData.getShort("itemMakeLevel");
        ret.questId = sqlItemData.getInt("questId");
        ret.create = sqlItemData.getInt("create");
        ret.replaceItem = sqlItemData.getInt("replaceId");
        ret.replaceMsg = sqlItemData.getString("replaceMsg");
        ret.afterImage = sqlItemData.getString("afterImage");
        ret.cardSet = 0;
        if ((ret.monsterBook > 0) && (itemId / 10000 == 238)) {
            this.mobIds.put(ret.monsterBook, itemId);
            for (Entry<Integer, Triple<Integer, List<Integer>, List<Integer>>> set : monsterBookSets.entrySet()) {
                if ((set.getValue().mid).contains(itemId)) {
                    ret.cardSet = set.getKey();
                    break;
                }
            }
        }

        String scrollRq = sqlItemData.getString("scrollReqs");
        if (scrollRq.length() > 0) {
            ret.scrollReqs = new ArrayList();
            String[] scroll = scrollRq.split(",");
            for (String s : scroll) {
                if (s.length() > 1) {
                    ret.scrollReqs.add(Integer.parseInt(s));
                }
            }
        }

        String consumeItem = sqlItemData.getString("consumeItem");
        if (consumeItem.length() > 0) {
            ret.questItems = new ArrayList();
            String[] scroll = scrollRq.split(",");
            for (String s : scroll) {
                if (s.length() > 1) {
                    ret.questItems.add(Integer.parseInt(s));
                }
            }
        }

        ret.totalprob = sqlItemData.getInt("totalprob");

        String incRq = sqlItemData.getString("incSkill");
        if (incRq.length() > 0) {
            ret.incSkill = new ArrayList();
            String[] scroll = incRq.split(",");
            for (String s : scroll) {
                if (s.length() > 1) {
                    ret.incSkill.add(Integer.parseInt(s));
                }
            }
        }
        this.dataCache.put(Integer.valueOf(itemId), ret);
    }

    public boolean isExpOrDropCardTime(int itemId) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/ShangHai"));
        String day = MapleDayInt.getDayInt(cal.get(7));
        Map times;
        if (this.getExpCardTimes.containsKey(itemId)) {
            times = (Map) this.getExpCardTimes.get(itemId);
        } else {
            List<MapleData> data = getItemData(itemId).getChildByPath("info").getChildByPath("time").getChildren();
            Map<String, String> hours = new HashMap<String, String>();
            for (MapleData childdata : data) {
                String[] time = MapleDataTool.getString(childdata).split(":");
                hours.put(time[0], time[1]);
            }
            times = hours;
            getExpCardTimes.put(itemId, hours);
            cal.get(7);
        }
        if (times.containsKey(day)) {
            String[] hourspan = ((String) times.get(day)).split("-");
            int starthour = Integer.parseInt(hourspan[0]);
            int endhour = Integer.parseInt(hourspan[1]);
            if ((cal.get(11) >= starthour) && (cal.get(11) <= endhour)) {
                return true;
            }
        }
        return false;
    }

    public ScriptedItem getScriptedItemInfo(int itemId) {
        if (this.scriptedItemCache.containsKey(itemId)) {
            return (ScriptedItem) this.scriptedItemCache.get(itemId);
        }
        if (itemId / 10000 != 243) {
            return null;
        }
        ScriptedItem script = new ScriptedItem(MapleDataTool.getInt("spec/npc", getItemData(itemId), 0), MapleDataTool.getString("spec/script", getItemData(itemId), ""), MapleDataTool.getInt("spec/runOnPickup", getItemData(itemId), 0) == 1);

        this.scriptedItemCache.put(itemId, script);
        return (ScriptedItem) this.scriptedItemCache.get(itemId);
    }

    public boolean isFloatCashItem(int itemId) {
        if (this.floatCashItem.containsKey(itemId)) {
            return ((Boolean) this.floatCashItem.get(itemId)).booleanValue();
        }
        if (itemId / 10000 != 512) {
            return false;
        }
        boolean floatType = MapleDataTool.getIntConvert("info/floatType", getItemData(itemId), 0) > 0;
        this.floatCashItem.put(itemId, floatType);
        return floatType;
    }

    public short getPetFlagInfo(int itemId) {
        if (this.petFlagInfo.containsKey(itemId)) {
            return petFlagInfo.get(itemId);
        }
        short flag = 0;
        if (itemId / 10000 != 500) {
            return flag;
        }
        MapleData item = getItemData(itemId);
        if (item == null) {
            return flag;
        }
        if (MapleDataTool.getIntConvert("info/pickupItem", item, 0) > 0) {
            flag = (short) (flag | 0x1);
        }
        if (MapleDataTool.getIntConvert("info/longRange", item, 0) > 0) {
            flag = (short) (flag | 0x2);
        }
        if (MapleDataTool.getIntConvert("info/pickupAll", item, 0) > 0) {
            flag = (short) (flag | 0x4);
        }
        if (MapleDataTool.getIntConvert("info/sweepForDrop", item, 0) > 0) {
            flag = (short) (flag | 0x10);
        }
        if (MapleDataTool.getIntConvert("info/consumeHP", item, 0) > 0) {
            flag = (short) (flag | 0x20);
        }
        if (MapleDataTool.getIntConvert("info/consumeMP", item, 0) > 0) {
            flag = (short) (flag | 0x40);
        }
        if (MapleDataTool.getIntConvert("info/autoBuff", item, 0) > 0) {
            flag = (short) (flag | 0x200);
        }
        this.petFlagInfo.put(itemId, flag);
        return flag;
    }

    public boolean isActivatedSocketItem(int itemId) {
        if (this.nActivatedSocket.containsKey(itemId)) {
            return nActivatedSocket.get(itemId);
        }
        boolean socket = MapleDataTool.getIntConvert("info/nActivatedSocket", getItemData(itemId), 0) > 0;
        this.nActivatedSocket.put(itemId, socket);
        return socket;
    }

    public short getItemIncMHPr(int itemId) {
        if (this.itemIncMHPr.containsKey(itemId)) {
            return itemIncMHPr.get(itemId);
        }
        short incMHPr = (short) MapleDataTool.getIntConvert("info/incMHPr", getItemData(itemId), 0);
        this.itemIncMHPr.put(itemId, incMHPr);
        return incMHPr;
    }

    public short getItemIncMMPr(int itemId) {
        if (this.itemIncMMPr.containsKey(itemId)) {
            return itemIncMMPr.get(itemId);
        }
        short IncMMPr = (short) MapleDataTool.getIntConvert("info/IncMMPr", getItemData(itemId), 0);
        this.itemIncMMPr.put(itemId, IncMMPr);
        return IncMMPr;
    }

    public static class MapleDayInt {

        public static String getDayInt(int day) {
            if (day == 1) {
                return "SUN";
            }
            if (day == 2) {
                return "MON";
            }
            if (day == 3) {
                return "TUE";
            }
            if (day == 4) {
                return "WED";
            }
            if (day == 5) {
                return "THU";
            }
            if (day == 6) {
                return "FRI";
            }
            if (day == 7) {
                return "SAT";
            }
            return null;
        }
    }
}