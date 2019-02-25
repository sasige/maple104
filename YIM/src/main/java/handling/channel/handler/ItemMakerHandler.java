package handling.channel.handler;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleQuestStatus;
import client.MapleTrait;
import client.MapleTrait.MapleTraitType;
import client.PlayerStats;
import client.SkillFactory;
import client.SkillFactory.CraftingEntry;
import client.anticheat.CheatTracker;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.ItemFlag;
import client.inventory.MapleImp;
import client.inventory.MapleImp.ImpFlag;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import handling.channel.ChannelServer;
import handling.world.World.Broadcast;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.log4j.Logger;

import server.ItemMakerFactory;
import server.ItemMakerFactory.GemCreateEntry;
import server.ItemMakerFactory.ItemMakerCreateEntry;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import server.Randomizer;
import server.maps.MapleExtractor;
import server.maps.MapleMap;
import server.maps.MapleReactor;
import server.quest.MapleQuest;
import tools.FileoutputUtil;
import tools.MaplePacketCreator;
import tools.Pair;
import tools.Triple;
import tools.data.LittleEndianAccessor;

public class ItemMakerHandler {

    private static final Logger log = Logger.getLogger(ItemMakerHandler.class);
    private static final Map<String, Integer> craftingEffects = new HashMap();

    public static void ItemMaker(LittleEndianAccessor slea, MapleClient c) {
        int makerType = slea.readInt();

        switch (makerType) {
            case 1:
                int toCreate = slea.readInt();
                if (GameConstants.isGem(toCreate)) {
                    ItemMakerFactory.GemCreateEntry gem = ItemMakerFactory.getInstance().getGemInfo(toCreate);
                    if (gem == null) {
                        return;
                    }
                    if (!hasSkill(c, gem.getReqSkillLevel())) {
                        return;
                    }
                    if (c.getPlayer().getMeso() < gem.getCost()) {
                        return;
                    }
                    int randGemGiven = getRandomGem(gem.getRandomReward());
                    if (c.getPlayer().getInventory(GameConstants.getInventoryType(randGemGiven)).isFull()) {
                        return;
                    }
                    int taken = checkRequiredNRemove(c, gem.getReqRecipes());
                    if (taken == 0) {
                        return;
                    }
                    c.getPlayer().gainMeso(-gem.getCost(), false);
                    MapleInventoryManipulator.addById(c, randGemGiven, (short) (byte) (taken == randGemGiven ? 9 : 1), "Made by Gem " + toCreate + " on " + FileoutputUtil.CurrentReadable_Date());

                    c.getSession().write(MaplePacketCreator.ItemMaker_Success());
                    c.getPlayer().getMap().broadcastMessage(c.getPlayer(), MaplePacketCreator.ItemMaker_Success_3rdParty(c.getPlayer().getId()), false);
                } else if (GameConstants.isOtherGem(toCreate)) {
                    ItemMakerFactory.GemCreateEntry gem = ItemMakerFactory.getInstance().getGemInfo(toCreate);
                    if (gem == null) {
                        return;
                    }
                    if (!hasSkill(c, gem.getReqSkillLevel())) {
                        return;
                    }
                    if (c.getPlayer().getMeso() < gem.getCost()) {
                        return;
                    }
                    if (c.getPlayer().getInventory(GameConstants.getInventoryType(toCreate)).isFull()) {
                        return;
                    }
                    if (checkRequiredNRemove(c, gem.getReqRecipes()) == 0) {
                        return;
                    }
                    c.getPlayer().gainMeso(-gem.getCost(), false);
                    if (GameConstants.getInventoryType(toCreate) == MapleInventoryType.EQUIP) {
                        MapleInventoryManipulator.addbyItem(c, MapleItemInformationProvider.getInstance().getEquipById(toCreate));
                    } else {
                        MapleInventoryManipulator.addById(c, toCreate, (short) 1, "Made by Gem " + toCreate + " on " + FileoutputUtil.CurrentReadable_Date());
                    }

                    c.getSession().write(MaplePacketCreator.ItemMaker_Success());
                    c.getPlayer().getMap().broadcastMessage(c.getPlayer(), MaplePacketCreator.ItemMaker_Success_3rdParty(c.getPlayer().getId()), false);
                } else {
                    boolean stimulator = slea.readByte() > 0;
                    int numEnchanter = slea.readInt();
                    ItemMakerFactory.ItemMakerCreateEntry create = ItemMakerFactory.getInstance().getCreateInfo(toCreate);
                    if (create == null) {
                        return;
                    }
                    if (numEnchanter > create.getTUC()) {
                        return;
                    }
                    if (!hasSkill(c, create.getReqSkillLevel())) {
                        return;
                    }
                    if (c.getPlayer().getMeso() < create.getCost()) {
                        return;
                    }
                    if (c.getPlayer().getInventory(GameConstants.getInventoryType(toCreate)).isFull()) {
                        return;
                    }
                    if (checkRequiredNRemove(c, create.getReqItems()) == 0) {
                        return;
                    }
                    c.getPlayer().gainMeso(-create.getCost(), false);

                    MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                    Equip toGive = (Equip) ii.getEquipById(toCreate);

                    if ((stimulator) || (numEnchanter > 0)) {
                        if (c.getPlayer().haveItem(create.getStimulator(), 1, false, true)) {
                            ii.randomizeStats_Above(toGive);
                            MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, create.getStimulator(), 1, false, false);
                        }
                        for (int i = 0; i < numEnchanter; i++) {
                            int enchant = slea.readInt();
                            if (c.getPlayer().haveItem(enchant, 1, false, true)) {
                                Map stats = ii.getEquipStats(enchant);
                                if (stats != null) {
                                    addEnchantStats(stats, toGive);
                                    MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, enchant, 1, false, false);
                                }
                            }
                        }
                    }
                    if ((!stimulator) || (Randomizer.nextInt(10) != 0)) {
                        MapleInventoryManipulator.addbyItem(c, toGive);
                        c.getPlayer().getMap().broadcastMessage(c.getPlayer(), MaplePacketCreator.ItemMaker_Success_3rdParty(c.getPlayer().getId()), false);
                    } else {
                        c.getPlayer().dropMessage(5, "The item was overwhelmed by the stimulator.");
                    }
                    c.getSession().write(MaplePacketCreator.ItemMaker_Success());
                }

                break;
            case 3:
                int etc = slea.readInt();
                if (!c.getPlayer().haveItem(etc, 100, false, true)) {
                    break;
                }
                MapleInventoryManipulator.addById(c, getCreateCrystal(etc), (short) 1, "Made by Maker " + etc + " on " + FileoutputUtil.CurrentReadable_Date());
                MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, etc, 100, false, false);

                c.getSession().write(MaplePacketCreator.ItemMaker_Success());
                c.getPlayer().getMap().broadcastMessage(c.getPlayer(), MaplePacketCreator.ItemMaker_Success_3rdParty(c.getPlayer().getId()), false);
                break;
            case 4:
                int itemId = slea.readInt();
                c.getPlayer().updateTick(slea.readInt());
                byte slot = (byte) slea.readInt();

                Item toUse = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((short) slot);
                if ((toUse == null) || (toUse.getItemId() != itemId) || (toUse.getQuantity() < 1)) {
                    return;
                }
                MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

                if ((!ii.isDropRestricted(itemId)) && (!ii.isAccountShared(itemId))) {
                    int[] toGive = getCrystal(itemId, ii.getReqLevel(itemId));
                    MapleInventoryManipulator.addById(c, toGive[0], (short) (byte) toGive[1], "Made by disassemble " + itemId + " on " + FileoutputUtil.CurrentReadable_Date());
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.EQUIP, (short) slot, (short) 1, false);
                }
                c.getSession().write(MaplePacketCreator.ItemMaker_Success());
                c.getPlayer().getMap().broadcastMessage(c.getPlayer(), MaplePacketCreator.ItemMaker_Success_3rdParty(c.getPlayer().getId()), false);
                break;
            case 2:
        }
    }

    private static int getCreateCrystal(int etc) {
        short level = MapleItemInformationProvider.getInstance().getItemMakeLevel(etc);
        int itemid;
        if ((level >= 31) && (level <= 50)) {
            itemid = 4260000;
        } else {
            if ((level >= 51) && (level <= 60)) {
                itemid = 4260001;
            } else {
                if ((level >= 61) && (level <= 70)) {
                    itemid = 4260002;
                } else {
                    if ((level >= 71) && (level <= 80)) {
                        itemid = 4260003;
                    } else {
                        if ((level >= 81) && (level <= 90)) {
                            itemid = 4260004;
                        } else {
                            if ((level >= 91) && (level <= 100)) {
                                itemid = 4260005;
                            } else {
                                if ((level >= 101) && (level <= 110)) {
                                    itemid = 4260006;
                                } else {
                                    if ((level >= 111) && (level <= 120)) {
                                        itemid = 4260007;
                                    } else {
                                        if (level >= 121) {
                                            itemid = 4260008;
                                        } else {
                                            throw new RuntimeException("Invalid Item Maker id");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return itemid;
    }

    private static int[] getCrystal(int itemid, int level) {
        int[] all = new int[2];
        all[0] = -1;
        if ((level >= 31) && (level <= 50)) {
            all[0] = 4260000;
        } else if ((level >= 51) && (level <= 60)) {
            all[0] = 4260001;
        } else if ((level >= 61) && (level <= 70)) {
            all[0] = 4260002;
        } else if ((level >= 71) && (level <= 80)) {
            all[0] = 4260003;
        } else if ((level >= 81) && (level <= 90)) {
            all[0] = 4260004;
        } else if ((level >= 91) && (level <= 100)) {
            all[0] = 4260005;
        } else if ((level >= 101) && (level <= 110)) {
            all[0] = 4260006;
        } else if ((level >= 111) && (level <= 120)) {
            all[0] = 4260007;
        } else if ((level >= 121) && (level <= 200)) {
            all[0] = 4260008;
        } else {
            throw new RuntimeException("Invalid Item Maker type" + level);
        }
        if ((GameConstants.isWeapon(itemid)) || (GameConstants.isOverall(itemid))) {
            all[1] = Randomizer.rand(5, 11);
        } else {
            all[1] = Randomizer.rand(3, 7);
        }
        return all;
    }

    private static void addEnchantStats(Map<String, Integer> stats, Equip item) {
        Integer s = (Integer) stats.get("PAD");
        if ((s != null) && (s.intValue() != 0)) {
            item.setWatk((short) (item.getWatk() + s.intValue()));
        }
        s = (Integer) stats.get("MAD");
        if ((s != null) && (s.intValue() != 0)) {
            item.setMatk((short) (item.getMatk() + s.intValue()));
        }
        s = (Integer) stats.get("ACC");
        if ((s != null) && (s.intValue() != 0)) {
            item.setAcc((short) (item.getAcc() + s.intValue()));
        }
        s = (Integer) stats.get("EVA");
        if ((s != null) && (s.intValue() != 0)) {
            item.setAvoid((short) (item.getAvoid() + s.intValue()));
        }
        s = (Integer) stats.get("Speed");
        if ((s != null) && (s.intValue() != 0)) {
            item.setSpeed((short) (item.getSpeed() + s.intValue()));
        }
        s = (Integer) stats.get("Jump");
        if ((s != null) && (s.intValue() != 0)) {
            item.setJump((short) (item.getJump() + s.intValue()));
        }
        s = (Integer) stats.get("MaxHP");
        if ((s != null) && (s.intValue() != 0)) {
            item.setHp((short) (item.getHp() + s.intValue()));
        }
        s = (Integer) stats.get("MaxMP");
        if ((s != null) && (s.intValue() != 0)) {
            item.setMp((short) (item.getMp() + s.intValue()));
        }
        s = (Integer) stats.get("STR");
        if ((s != null) && (s.intValue() != 0)) {
            item.setStr((short) (item.getStr() + s.intValue()));
        }
        s = (Integer) stats.get("DEX");
        if ((s != null) && (s.intValue() != 0)) {
            item.setDex((short) (item.getDex() + s.intValue()));
        }
        s = (Integer) stats.get("INT");
        if ((s != null) && (s.intValue() != 0)) {
            item.setInt((short) (item.getInt() + s.intValue()));
        }
        s = (Integer) stats.get("LUK");
        if ((s != null) && (s.intValue() != 0)) {
            item.setLuk((short) (item.getLuk() + s.intValue()));
        }
        s = (Integer) stats.get("randOption");
        if ((s != null) && (s.intValue() != 0)) {
            int ma = item.getMatk();
            int wa = item.getWatk();
            if (wa > 0) {
                item.setWatk((short) (Randomizer.nextBoolean() ? wa + s.intValue() : wa - s.intValue()));
            }
            if (ma > 0) {
                item.setMatk((short) (Randomizer.nextBoolean() ? ma + s.intValue() : ma - s.intValue()));
            }
        }
        s = (Integer) stats.get("randStat");
        if ((s != null) && (s.intValue() != 0)) {
            int str = item.getStr();
            int dex = item.getDex();
            int luk = item.getLuk();
            int int_ = item.getInt();
            if (str > 0) {
                item.setStr((short) (Randomizer.nextBoolean() ? str + s.intValue() : str - s.intValue()));
            }
            if (dex > 0) {
                item.setDex((short) (Randomizer.nextBoolean() ? dex + s.intValue() : dex - s.intValue()));
            }
            if (int_ > 0) {
                item.setInt((short) (Randomizer.nextBoolean() ? int_ + s.intValue() : int_ - s.intValue()));
            }
            if (luk > 0) {
                item.setLuk((short) (Randomizer.nextBoolean() ? luk + s.intValue() : luk - s.intValue()));
            }
        }
    }

    private static int getRandomGem(List<Pair<Integer, Integer>> rewards) {
        List items = new ArrayList();
        for (Pair p : rewards) {
            int itemid = ((Integer) p.getLeft()).intValue();
            for (int i = 0; i < ((Integer) p.getRight()).intValue(); i++) {
                items.add(Integer.valueOf(itemid));
            }
        }
        return ((Integer) items.get(Randomizer.nextInt(items.size()))).intValue();
    }

    private static int checkRequiredNRemove(MapleClient c, List<Pair<Integer, Integer>> recipe) {
        int itemid = 0;
        for (Pair p : recipe) {
            if (!c.getPlayer().haveItem(((Integer) p.getLeft()).intValue(), ((Integer) p.getRight()).intValue(), false, true)) {
                return 0;
            }
        }
        for (Pair p : recipe) {
            itemid = ((Integer) p.getLeft()).intValue();
            MapleInventoryManipulator.removeById(c, GameConstants.getInventoryType(itemid), itemid, ((Integer) p.getRight()).intValue(), false, false);
        }
        return itemid;
    }

    private static boolean hasSkill(MapleClient c, int reqlvl) {
        return c.getPlayer().getSkillLevel(SkillFactory.getSkill(PlayerStats.getSkillByJob(1007, c.getPlayer().getJob()))) >= reqlvl;
    }

    public static void UseRecipe(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        if ((chr == null) || (!chr.isAlive()) || (chr.getMap() == null) || (chr.hasBlockedInventory())) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        c.getPlayer().updateTick(slea.readInt());
        byte slot = (byte) slea.readShort();
        int itemId = slea.readInt();
        Item toUse = chr.getInventory(MapleInventoryType.USE).getItem((short) slot);

        if ((toUse == null) || (toUse.getQuantity() < 1) || (toUse.getItemId() != itemId) || (itemId / 10000 != 251)) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if (MapleItemInformationProvider.getInstance().getItemEffect(toUse.getItemId()).applyTo(chr)) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, (short) slot, (short) 1, false);
        }
    }

    public static void MakeExtractor(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        if ((chr == null) || (!chr.isAlive()) || (chr.getMap() == null) || (chr.hasBlockedInventory())) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        int itemId = slea.readInt();
        int fee = slea.readInt();
        Item toUse = chr.getInventory(MapleInventoryType.SETUP).findById(itemId);
        if ((toUse == null) || (toUse.getQuantity() < 1) || (itemId / 10000 != 304) || (fee <= 0) || (chr.getExtractor() != null) || (!chr.getMap().isTown())) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        chr.setExtractor(new MapleExtractor(chr, itemId, fee, chr.getFH()));
        chr.getMap().spawnExtractor(chr.getExtractor());
    }

    public static void UseBag(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        if ((chr == null) || (!chr.isAlive()) || (chr.getMap() == null) || (chr.hasBlockedInventory())) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        c.getPlayer().updateTick(slea.readInt());
        byte slot = (byte) slea.readShort();
        int itemId = slea.readInt();
        Item toUse = chr.getInventory(MapleInventoryType.ETC).getItem((short) slot);

        if ((toUse == null) || (toUse.getQuantity() < 1) || (toUse.getItemId() != itemId) || (itemId / 10000 != 433)) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        boolean firstTime = !chr.getExtendedSlots().contains(Integer.valueOf(itemId));
        if (firstTime) {
            chr.getExtendedSlots().add(Integer.valueOf(itemId));
            chr.changedExtended();
            short flag = toUse.getFlag();
            flag = (short) (flag | ItemFlag.LOCK.getValue());
            flag = (short) (flag | ItemFlag.UNTRADEABLE.getValue());
            toUse.setFlag(flag);
            c.getSession().write(MaplePacketCreator.updateSpecialItemUse(toUse, (byte) 4, toUse.getPosition(), true, chr));
        }
        c.getSession().write(MaplePacketCreator.openBag(chr.getExtendedSlots().indexOf(Integer.valueOf(itemId)), itemId, firstTime));
        c.getSession().write(MaplePacketCreator.enableActions());
    }

    public static void StartHarvest(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        MapleReactor reactor = c.getPlayer().getMap().getReactorByOid(slea.readInt());
        if ((reactor == null) || (!reactor.isAlive()) || (reactor.getReactorId() > 200011) || (chr.getStat().harvestingTool <= 0) || (reactor.getTruePosition().distanceSq(chr.getTruePosition()) > 10000.0D) || (c.getPlayer().getFatigue() >= 200)) {
            return;
        }
        Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((short) c.getPlayer().getStat().harvestingTool);
        if ((item == null) || (((Equip) item).getDurability() == 0)) {
            c.getPlayer().getStat().handleProfessionTool(c.getPlayer());
            return;
        }
        MapleQuestStatus marr = c.getPlayer().getQuestNAdd(MapleQuest.getInstance(122501));
        if (marr.getCustomData() == null) {
            marr.setCustomData("0");
        }
        long lastTime = Long.parseLong(marr.getCustomData());
        if (lastTime + 5000L > System.currentTimeMillis()) {
            c.getPlayer().dropMessage(5, "还无法进行采集。");
        } else {
            marr.setCustomData(String.valueOf(System.currentTimeMillis()));
            c.getSession().write(MaplePacketCreator.harvestMessage(reactor.getObjectId(), 11));
            c.getPlayer().getMap().broadcastMessage(chr, MaplePacketCreator.showHarvesting(chr.getId(), item.getItemId()), false);
        }
    }

    public static void StopHarvest(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
    }

    public static void ProfessionInfo(LittleEndianAccessor slea, MapleClient c) {
        try {
            String asdf = slea.readMapleAsciiString();
            int level1 = slea.readInt();
            int level2 = slea.readInt();
            if (asdf.equalsIgnoreCase("honorLeveling")) {
                c.getSession().write(MaplePacketCreator.professionInfo(asdf, level1, level2, 500));
            } else {
                c.getSession().write(MaplePacketCreator.professionInfo(asdf, level1, level2, Math.max(0, 100 - (level1 + 1 - c.getPlayer().getProfessionLevel(Integer.parseInt(asdf))) * 20)));
            }
        } catch (NumberFormatException nfe) {
        }
    }

    public static void CraftEffect(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        if ((chr.getMapId() != 910001000) && (chr.getMap().getExtractorSize() <= 0)) {
            return;
        }
        String effect = slea.readMapleAsciiString();
        Integer profession = craftingEffects.get(effect);
        if ((profession != null) && ((c.getPlayer().getProfessionLevel(profession.intValue()) > 0) || ((profession.intValue() == 92040000) && (chr.getMap().getExtractorSize() > 0)))) {
            int time = slea.readInt();
            if ((time > 6000) || (time < 3000)) {
                time = 4000;
            }
            c.getSession().write(MaplePacketCreator.showOwnCraftingEffect(effect, time, effect.endsWith("Extract") ? 1 : 0));
            chr.getMap().broadcastMessage(chr, MaplePacketCreator.showCraftingEffect(chr.getId(), effect, time, effect.endsWith("Extract") ? 1 : 0), false);
        }
    }

    public static void CraftMake(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        if ((chr.getMapId() != 910001000) && (chr.getMap().getExtractorSize() <= 0)) {
            return;
        }

        int something = slea.readInt();

        int time = slea.readInt();
        if ((time > 6000) || (time < 3000)) {
            time = 4000;
        }
        chr.getMap().broadcastMessage(MaplePacketCreator.craftMake(chr.getId(), something, time));
    }

    public static void CraftComplete(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        if (!chr.getCheatTracker().canCraftMake()) {
            c.getSession().write(MaplePacketCreator.enableActions());
            log.info("[作弊] " + chr.getName() + " (等级 " + chr.getLevel() + ") 专业技术制作速度异常。地图ID: " + chr.getMapId());
            Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM Message] " + chr.getName() + " (等级 " + chr.getLevel() + ") 专业技术制作速度异常。地图ID: " + chr.getMapId()));
            return;
        }
        int craftID = slea.readInt();
        SkillFactory.CraftingEntry ce = SkillFactory.getCraft(craftID);
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (((chr.getMapId() != 910001000) && ((craftID != 92049000) || (chr.getMap().getExtractorSize() <= 0))) || (ce == null) || (chr.getFatigue() >= 200)) {
            return;
        }
        int theLevl = c.getPlayer().getProfessionLevel(craftID / 10000 * 10000);
        if ((theLevl <= 0) && (craftID != 92049000)) {
            return;
        }
        List showItems = new ArrayList();
        int toGet = 0;
        int expGain = 0;
        int fatigue = 0;
        short quantity = 1;
        CraftRanking cr = CraftRanking.GOOD;
        if (craftID == 92049000) {
            int extractorId = slea.readInt();
            int itemId = slea.readInt();
            long invId = slea.readLong();
            int reqLevel = ii.getReqLevel(itemId);
            Item item = chr.getInventory(MapleInventoryType.EQUIP).findByInventoryId(invId, itemId);
            if ((item == null) || (chr.getInventory(MapleInventoryType.ETC).isFull())) {
                return;
            }
            if (extractorId <= 0) {
                if (theLevl != 0) {
                    if (theLevl >= (reqLevel > 130 ? 6 : (reqLevel - 30) / 20));
                } else {
                    return;
                }
            }
            if (extractorId > 0) {
                MapleCharacter extract = chr.getMap().getCharacterById(extractorId);
                if ((extract == null) || (extract.getExtractor() == null)) {
                    return;
                }
                MapleExtractor extractor = extract.getExtractor();
                if (extractor.owner != chr.getId()) {
                    if (chr.getMeso() < extractor.fee) {
                        return;
                    }
                    MapleStatEffect eff = ii.getItemEffect(extractor.itemId);
                    if ((eff != null) && (eff.getUseLevel() < reqLevel)) {
                        return;
                    }
                    chr.gainMeso(-extractor.fee, true);
                    MapleCharacter owner = chr.getMap().getCharacterById(extractor.owner);
                    if ((owner != null) && (owner.getMeso() < 2147483647 - extractor.fee)) {
                        owner.gainMeso(extractor.fee, false);
                    }
                }
            }
            toGet = 4031016;
            quantity = (short) Randomizer.rand(3, (GameConstants.isWeapon(itemId)) || (GameConstants.isOverall(itemId)) ? 11 : 7);
            if (reqLevel <= 60) {
                toGet = 4021013;
            } else if (reqLevel <= 90) {
                toGet = 4021014;
            } else if (reqLevel <= 120) {
                toGet = 4021015;
            }
            if (quantity <= 5) {
                cr = CraftRanking.SOSO;
            }
            if ((Randomizer.nextInt(5) == 0) && (toGet != 4031016)) {
                toGet++;
                quantity = 1;
                cr = CraftRanking.COOL;
            }
            fatigue = 3;
            MapleInventoryManipulator.addById(c, toGet, quantity, "分解获得 " + itemId + " 时间 " + FileoutputUtil.CurrentReadable_Date());
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.EQUIP, item.getPosition(), (short) 1, false);
            showItems.add(new Pair(Integer.valueOf(itemId), Integer.valueOf(-1)));
            showItems.add(new Pair(Integer.valueOf(toGet), Integer.valueOf(quantity)));
        } else if (craftID == 92049001) {
            int itemId = slea.readInt();
            long invId1 = slea.readLong();
            long invId2 = slea.readLong();
            int reqLevel = ii.getReqLevel(itemId);
            Equip item1 = (Equip) chr.getInventory(MapleInventoryType.EQUIP).findByInventoryIdOnly(invId1, itemId);
            Equip item2 = (Equip) chr.getInventory(MapleInventoryType.EQUIP).findByInventoryIdOnly(invId2, itemId);
            for (short i = 0; i < chr.getInventory(MapleInventoryType.EQUIP).getSlotLimit(); i = (short) (i + 1)) {
                Item item = chr.getInventory(MapleInventoryType.EQUIP).getItem(i);
                if ((item != null) && (item.getItemId() == itemId) && (item != item1) && (item != item2)) {
                    if (item1 == null) {
                        item1 = (Equip) item;
                    } else if (item2 == null) {
                        item2 = (Equip) item;
                        break;
                    }
                }
            }
            if ((item1 == null) || (item2 == null)) {
                return;
            }
            if (theLevl < (reqLevel > 130 ? 6 : (reqLevel - 30) / 20)) {
                return;
            }
            int potentialState = 5;
            int potentialChance = theLevl * 2;
            int toRemove = 1;
            if (reqLevel <= 30) {
                toRemove = 1;
            } else if (reqLevel <= 70) {
                toRemove = 2;
            } else if (reqLevel <= 120) {
                toRemove = 3;
            } else {
                toRemove = 4;
            }
            if (!chr.haveItem(4021017, toRemove)) {
                chr.dropMessage(5, "合成装备需要的炼金术士之石不够，当前需要" + toRemove + "个。");
                c.getSession().write(MaplePacketCreator.enableActions());
                return;
            }
            if ((item1.getState() > 0) && (item2.getState() > 0)) {
                potentialChance = 100;
            } else if ((item1.getState() > 0) || (item2.getState() > 0)) {
                potentialChance *= 2;
            }
            if ((item1.getState() == item2.getState()) && (item1.getState() > 5)) {
                potentialState = item1.getState();
            }

            Equip newEquip = ii.fuse(item1.getLevel() > 0 ? (Equip) ii.getEquipById(itemId) : item1, item2.getLevel() > 0 ? (Equip) ii.getEquipById(itemId) : item2);
            int newStat = ii.getTotalStat(newEquip);
            if ((newStat > ii.getTotalStat(item1)) || (newStat > ii.getTotalStat(item2))) {
                cr = CraftRanking.COOL;
            } else if ((newStat < ii.getTotalStat(item1)) || (newStat < ii.getTotalStat(item2))) {
                cr = CraftRanking.SOSO;
            }
            if (Randomizer.nextInt(100) < ((newEquip.getUpgradeSlots() > 0) || (potentialChance >= 100) ? potentialChance : potentialChance / 2)) {
                newEquip.resetPotential_Fuse(theLevl > 5, potentialState);
            }
            newEquip.setFlag((short) ItemFlag.CRAFTED.getValue());
            newEquip.setOwner(chr.getName());
            toGet = newEquip.getItemId();
            expGain = 60 - (theLevl - 1) * 4;
            fatigue = 3;
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.EQUIP, item1.getPosition(), (short) 1, false);
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.EQUIP, item2.getPosition(), (short) 1, false);
            MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, 4021017, toRemove, false, false);
            MapleInventoryManipulator.addbyItem(c, newEquip);
            showItems.add(new Pair(Integer.valueOf(itemId), Integer.valueOf(-1)));
            showItems.add(new Pair(Integer.valueOf(itemId), Integer.valueOf(-1)));
            showItems.add(new Pair(Integer.valueOf(4021017), Integer.valueOf(-toRemove)));
            showItems.add(new Pair(Integer.valueOf(toGet), Integer.valueOf(1)));
        } else {
            if ((ce.needOpenItem) && (chr.getSkillLevel(craftID) <= 0)) {
                return;
            }
            for (Map.Entry e : ce.reqItems.entrySet()) {
                if (!chr.haveItem(((Integer) e.getKey()).intValue(), ((Integer) e.getValue()).intValue())) {
                    return;
                }
            }
            for (Triple i : ce.targetItems) {
                if (!MapleInventoryManipulator.checkSpace(c, ((Integer) i.left).intValue(), ((Integer) i.mid).intValue(), "")) {
                    return;
                }
            }
            for (Entry<Integer, Integer> e : ce.reqItems.entrySet()) {
                MapleInventoryManipulator.removeById(c, GameConstants.getInventoryType(e.getKey()), e.getKey(), e.getValue(), false, false);
                showItems.add(new Pair(e.getKey(), Integer.valueOf(-e.getValue())));
            }
            if ((Randomizer.nextInt(100) < 100 - (ce.reqSkillLevel - theLevl) * 20) || (craftID / 10000 <= 9201)) {
                while (true) {
                    boolean passed = false;
                    for (Triple<Integer, Integer, Integer> i : ce.targetItems) {
                        if (Randomizer.nextInt(100) < ((Integer) i.right).intValue()) {
                            toGet = ((Integer) i.left).intValue();
                            quantity = ((Integer) i.mid).shortValue();
                            Item receive = null;
                            if (GameConstants.getInventoryType(toGet) == MapleInventoryType.EQUIP) {
                                Equip first = (Equip) ii.getEquipById(toGet);
                                if (Randomizer.nextInt(100) < theLevl * 2) {
                                    first = ii.randomizeStats(first);
                                    cr = CraftRanking.COOL;
                                }
                                if (Randomizer.nextInt(100) < theLevl * (first.getUpgradeSlots() > 0 ? 2 : 1)) {
                                    first.resetPotential();
                                    cr = CraftRanking.COOL;
                                }
                                receive = first;
                                receive.setFlag((short) ItemFlag.CRAFTED.getValue());
                            } else {
                                receive = new Item(toGet, (short) 0, quantity, (short) ItemFlag.CRAFTED_USE.getValue());
                            }
                            if (ce.period > 0) {
                                receive.setExpiration(System.currentTimeMillis() + ce.period * 60000);
                            }
                            receive.setOwner(chr.getName());
                            receive.setGMLog("制作装备 " + craftID + " 在 " + FileoutputUtil.CurrentReadable_Date());
                            MapleInventoryManipulator.addFromDrop(c, receive, false, false);
                            showItems.add(new Pair(Integer.valueOf(receive.getItemId()), Integer.valueOf(receive.getQuantity())));
                            if (ce.needOpenItem) {
                                byte mLevel = chr.getMasterLevel(craftID);
                                if (mLevel == 1) {
                                    chr.changeSkillLevel(ce, 0, (byte) 0);
                                } else if (mLevel > 1) {
                                    chr.changeSkillLevel(ce, Integer.MAX_VALUE, (byte) (chr.getMasterLevel(craftID) - 1));
                                }
                            }
                            fatigue = ce.incFatigability;
                            expGain = ce.incSkillProficiency == 0 ? fatigue * 20 - (ce.reqSkillLevel - theLevl) * 4 : ce.incSkillProficiency;
                            chr.getTrait(MapleTrait.MapleTraitType.craft).addExp(cr.craft, chr);
                            passed = true;
                            break;
                        }
                    }
                    if (passed) {
                        break;
                    }
                    continue;
                }
            } else {
                quantity = 0;
                cr = CraftRanking.FAIL;
            }
        }
        if ((expGain > 0) && (theLevl < 10)) {
            expGain *= (chr.isAdmin() ? 20 : chr.getClient().getChannelServer().getTraitRate());
            if (Randomizer.nextInt(100) < chr.getTrait(MapleTrait.MapleTraitType.craft).getLevel() / 5) {
                expGain *= 2;
            }
            String s = "炼金术";
            switch (craftID / 10000) {
                case 9200:
                    s = "采药";
                    break;
                case 9201:
                    s = "采矿";
                    break;
                case 9202:
                    s = "装备制作";
                    break;
                case 9203:
                    s = "饰品制作";
            }

            chr.dropMessage(-5, s + "的熟练度提高了。(+" + expGain + ")");
            if (chr.addProfessionExp(craftID / 10000 * 10000, expGain)) {
                chr.dropMessage(-5, s + "的等级提升了。");
            }
        } else {
            expGain = 0;
        }
        MapleQuest.getInstance(2550).forceStart(c.getPlayer(), 9031000, "1");
        chr.setFatigue((byte) (chr.getFatigue() + fatigue));
        chr.getMap().broadcastMessage(MaplePacketCreator.craftFinished(chr.getId(), craftID, cr.i, toGet, quantity, expGain));
        if (!showItems.isEmpty()) {
            c.getSession().write(MaplePacketCreator.getShowItemGain(showItems));
        }
    }

    public static void UsePot(LittleEndianAccessor slea, MapleClient c) {
        int itemid = slea.readInt();
        Item slot = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slea.readShort());
        if ((slot == null) || (slot.getQuantity() <= 0) || (slot.getItemId() != itemid) || (itemid / 10000 != 244) || (MapleItemInformationProvider.getInstance().getPot(itemid) == null)) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        c.getSession().write(MaplePacketCreator.enableActions());
        for (int i = 0; i < c.getPlayer().getImps().length; i++) {
            if (c.getPlayer().getImps()[i] == null) {
                c.getPlayer().getImps()[i] = new MapleImp(itemid);
                c.getSession().write(MaplePacketCreator.updateImp(c.getPlayer().getImps()[i], MapleImp.ImpFlag.SUMMONED.getValue(), i, false));
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot.getPosition(), (short) 1, false, false);
                return;
            }
        }
    }

    public static void ClearPot(LittleEndianAccessor slea, MapleClient c) {
        int index = slea.readInt() - 1;
        if ((index < 0) || (index >= c.getPlayer().getImps().length) || (c.getPlayer().getImps()[index] == null)) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        c.getSession().write(MaplePacketCreator.updateImp(c.getPlayer().getImps()[index], MapleImp.ImpFlag.REMOVED.getValue(), index, false));
        c.getPlayer().getImps()[index] = null;
    }

    public static void FeedPot(LittleEndianAccessor slea, MapleClient c) {
        int itemid = slea.readInt();
        Item slot = c.getPlayer().getInventory(GameConstants.getInventoryType(itemid)).getItem((short) slea.readInt());
        if ((slot == null) || (slot.getQuantity() <= 0) || (slot.getItemId() != itemid)) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        int level = GameConstants.getInventoryType(itemid) == MapleInventoryType.ETC ? MapleItemInformationProvider.getInstance().getItemMakeLevel(itemid) : MapleItemInformationProvider.getInstance().getReqLevel(itemid);
        if ((level <= 0) || (level < Math.min(120, c.getPlayer().getLevel()) - 50) || ((GameConstants.getInventoryType(itemid) != MapleInventoryType.ETC) && (GameConstants.getInventoryType(itemid) != MapleInventoryType.EQUIP))) {
            c.getPlayer().dropMessage(1, "喂养道具宝宝出错。");
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        int index = slea.readInt() - 1;
        if (c.getPlayer().isAdmin()) {
            c.getPlayer().dropMessage(5, "喂养道具宝宝 index: " + index);
        }
        if ((index < 0) || (index >= c.getPlayer().getImps().length) || (c.getPlayer().getImps()[index] == null) || (c.getPlayer().getImps()[index].getLevel() >= ((Integer) MapleItemInformationProvider.getInstance().getPot(c.getPlayer().getImps()[index].getItemId()).right).intValue() - 1) || (c.getPlayer().getImps()[index].getState() != 1)) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        int mask = MapleImp.ImpFlag.FULLNESS.getValue();
        mask |= MapleImp.ImpFlag.FULLNESS_2.getValue();
        mask |= MapleImp.ImpFlag.UPDATE_TIME.getValue();
        mask |= MapleImp.ImpFlag.AWAKE_TIME.getValue();

        c.getPlayer().getImps()[index].setFullness(c.getPlayer().getImps()[index].getFullness() + 100 * (GameConstants.getInventoryType(itemid) == MapleInventoryType.EQUIP ? 2 : 1));
        if (Randomizer.nextBoolean()) {
            mask |= MapleImp.ImpFlag.CLOSENESS.getValue();
            c.getPlayer().getImps()[index].setCloseness(c.getPlayer().getImps()[index].getCloseness() + 1 + Randomizer.nextInt(5 * (GameConstants.getInventoryType(itemid) == MapleInventoryType.EQUIP ? 2 : 1)));
        } else if (Randomizer.nextInt(5) == 0) {
            c.getPlayer().getImps()[index].setState(4);
            mask |= MapleImp.ImpFlag.STATE.getValue();
        }
        if (c.getPlayer().getImps()[index].getFullness() >= 1000) {
            c.getPlayer().getImps()[index].setState(1);
            c.getPlayer().getImps()[index].setFullness(0);
            c.getPlayer().getImps()[index].setLevel(c.getPlayer().getImps()[index].getLevel() + 1);
            mask |= MapleImp.ImpFlag.SUMMONED.getValue();
            if (c.getPlayer().getImps()[index].getLevel() >= ((Integer) MapleItemInformationProvider.getInstance().getPot(c.getPlayer().getImps()[index].getItemId()).right).intValue() - 1) {
                c.getPlayer().getImps()[index].setState(5);
            }
        }
        MapleInventoryManipulator.removeFromSlot(c, GameConstants.getInventoryType(itemid), slot.getPosition(), (short) 1, false, false);
        c.getSession().write(MaplePacketCreator.updateImp(c.getPlayer().getImps()[index], mask, index, false));
    }

    public static void CurePot(LittleEndianAccessor slea, MapleClient c) {
        int itemid = slea.readInt();
        Item slot = c.getPlayer().getInventory(MapleInventoryType.ETC).getItem((short) slea.readInt());
        if ((slot == null) || (slot.getQuantity() <= 0) || (slot.getItemId() != itemid) || (itemid / 10000 != 434)) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        int index = slea.readInt() - 1;
        if (c.getPlayer().isAdmin()) {
            c.getPlayer().dropMessage(5, "治愈道具宝宝 index: " + index);
        }
        if ((index < 0) || (index >= c.getPlayer().getImps().length) || (c.getPlayer().getImps()[index] == null) || (c.getPlayer().getImps()[index].getState() != 4)) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        c.getPlayer().getImps()[index].setState(1);
        c.getSession().write(MaplePacketCreator.updateImp(c.getPlayer().getImps()[index], MapleImp.ImpFlag.STATE.getValue(), index, false));
        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.ETC, slot.getPosition(), (short) 1, false, false);
    }

    public static void RewardPot(LittleEndianAccessor slea, MapleClient c) {
        int index = slea.readInt() - 1;
        if (c.getPlayer().isAdmin()) {
            c.getPlayer().dropMessage(5, "道具宝宝奖励 index: " + index);
        }
        if ((index < 0) || (index >= c.getPlayer().getImps().length) || (c.getPlayer().getImps()[index] == null) || (c.getPlayer().getImps()[index].getLevel() < ((Integer) MapleItemInformationProvider.getInstance().getPot(c.getPlayer().getImps()[index].getItemId()).right).intValue() - 1)) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        int itemid = GameConstants.getRewardPot(c.getPlayer().getImps()[index].getItemId(), c.getPlayer().getImps()[index].getCloseness());
        if ((itemid <= 0) || (!MapleInventoryManipulator.checkSpace(c, itemid, 1, ""))) {
            c.getPlayer().dropMessage(1, "您的背包空间不足。");
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        MapleInventoryManipulator.addById(c, itemid, (short) 1, "道具宝宝 " + c.getPlayer().getImps()[index].getItemId() + " 在 " + FileoutputUtil.CurrentReadable_Date());
        c.getSession().write(MaplePacketCreator.updateImp(c.getPlayer().getImps()[index], MapleImp.ImpFlag.REMOVED.getValue(), index, false));
        c.getPlayer().getImps()[index] = null;
    }

    static {
        craftingEffects.put("Effect/BasicEff.img/professions/herbalism", Integer.valueOf(92000000));
        craftingEffects.put("Effect/BasicEff.img/professions/mining", Integer.valueOf(92010000));
        craftingEffects.put("Effect/BasicEff.img/professions/herbalismExtract", Integer.valueOf(92000000));
        craftingEffects.put("Effect/BasicEff.img/professions/miningExtract", Integer.valueOf(92010000));

        craftingEffects.put("Effect/BasicEff.img/professions/equip_product", Integer.valueOf(92020000));
        craftingEffects.put("Effect/BasicEff.img/professions/acc_product", Integer.valueOf(92030000));
        craftingEffects.put("Effect/BasicEff.img/professions/alchemy", Integer.valueOf(92040000));
    }

    public static enum CraftRanking {

        SOSO(19, 30),
        GOOD(20, 40),
        COOL(21, 50),
        FAIL(23, 20);
        public int i;
        public int craft;

        private CraftRanking(int i, int craft) {
            this.i = i;
            this.craft = craft;
        }
    }
}