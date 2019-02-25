package server;

import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleQuestStatus;
import client.MapleTrait;
import client.MapleTrait.MapleTraitType;
import client.PlayerStats;
import client.SkillFactory;
import client.anticheat.CheatTracker;
import client.inventory.Equip;
import client.inventory.EquipAdditions.RingSet;
import client.inventory.InventoryException;
import client.inventory.Item;
import client.inventory.ItemFlag;
import client.inventory.MapleAndroid;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryIdentifier;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import constants.GameConstants;
import java.awt.Point;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

import server.maps.MapleMap;
import server.quest.MapleQuest;
import tools.MaplePacketCreator;
import tools.packet.AndroidPacket;
import tools.packet.MTSCSPacket;

public class MapleInventoryManipulator {

    private static final Logger log = Logger.getLogger(MapleInventoryManipulator.class);

    public static void addRing(MapleCharacter chr, int itemId, int ringId, int sn) {
        CashItemInfo csi = CashItemFactory.getInstance().getItem(sn);
        if (csi == null) {
            return;
        }
        Item ring = chr.getCashInventory().toItem(csi, ringId);
        if ((ring == null) || (ring.getUniqueId() != ringId) || (ring.getUniqueId() <= 0) || (ring.getItemId() != itemId)) {
            return;
        }
        chr.getCashInventory().addToInventory(ring);

        chr.getClient().getSession().write(MTSCSPacket.购买商城道具(ring, sn, chr.getClient().getAccID()));
    }

    public static boolean addbyItem(MapleClient c, Item item) {
        return addbyItem(c, item, false) >= 0;
    }

    public static short addbyItem(MapleClient c, Item item, boolean fromcs) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        MapleInventoryType type = GameConstants.getInventoryType(item.getItemId());
        short newSlot = c.getPlayer().getInventory(type).addItem(item);
        if (newSlot == -1) {
            if (!fromcs) {
                c.getSession().write(MaplePacketCreator.getInventoryFull());
                c.getSession().write(MaplePacketCreator.getShowInventoryFull());
            }
            return newSlot;
        }
        if (GameConstants.isHarvesting(item.getItemId())) {
            c.getPlayer().getStat().handleProfessionTool(c.getPlayer());
        }
        c.getSession().write(MaplePacketCreator.addInventorySlot(type, item));
        c.getPlayer().havePartyQuest(item.getItemId());
        return newSlot;
    }

    public static int getUniqueId(int itemId, MaplePet pet) {
        int uniqueid = -1;
        if (GameConstants.isPet(itemId)) {
            if (pet != null) {
                uniqueid = pet.getUniqueId();
            } else {
                uniqueid = MapleInventoryIdentifier.getInstance();
            }
        } else if ((GameConstants.getInventoryType(itemId) == MapleInventoryType.CASH) || (MapleItemInformationProvider.getInstance().isCash(itemId))) {
            uniqueid = MapleInventoryIdentifier.getInstance();
        }
        return uniqueid;
    }

    public static boolean addById(MapleClient c, int itemId, short quantity, String gmLog) {
        return addById(c, itemId, quantity, null, null, 0L, 0, gmLog);
    }

    public static boolean addById(MapleClient c, int itemId, short quantity, long period, String gmLog) {
        return addById(c, itemId, quantity, null, null, period, 0, gmLog);
    }

    public static boolean addById(MapleClient c, int itemId, short quantity, long period, int state, String gmLog) {
        return addById(c, itemId, quantity, null, null, period, state, gmLog);
    }

    public static boolean addById(MapleClient c, int itemId, short quantity, String owner, String gmLog) {
        return addById(c, itemId, quantity, owner, null, 0L, 0, gmLog);
    }

    public static byte addId(MapleClient c, int itemId, short quantity, String owner, String gmLog) {
        return addId(c, itemId, quantity, owner, null, 0L, 0, gmLog);
    }

    public static boolean addById(MapleClient c, int itemId, short quantity, String owner, MaplePet pet, String gmLog) {
        return addById(c, itemId, quantity, owner, pet, 0L, 0, gmLog);
    }

    public static boolean addById(MapleClient c, int itemId, short quantity, String owner, MaplePet pet, long period, String gmLog) {
        return addById(c, itemId, quantity, owner, pet, period, 0, gmLog);
    }

    public static boolean addById(MapleClient c, int itemId, short quantity, String owner, MaplePet pet, long period, int state, String gmLog) {
        return addId(c, itemId, quantity, owner, pet, period, state, gmLog) >= 0;
    }

    public static byte addId(MapleClient c, int itemId, short quantity, String owner, MaplePet pet, long period, int state, String gmLog) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (((ii.isPickupRestricted(itemId)) && (c.getPlayer().haveItem(itemId, 1, true, false))) || (!ii.itemExists(itemId))) {
            c.getSession().write(MaplePacketCreator.getInventoryFull());
            c.getSession().write(MaplePacketCreator.showItemUnavailable());
            return -1;
        }
        MapleInventoryType type = GameConstants.getInventoryType(itemId);
        int uniqueid = getUniqueId(itemId, pet);
        short newSlot = -1;
        if (!type.equals(MapleInventoryType.EQUIP)) {
            short slotMax = ii.getSlotMax(itemId);
            List existing = c.getPlayer().getInventory(type).listById(itemId);
            if (!GameConstants.isRechargable(itemId)) {
                if (existing.size() > 0) {
                    Iterator i = existing.iterator();
                    while ((quantity > 0)
                            && (i.hasNext())) {
                        Item eItem = (Item) i.next();
                        short oldQ = eItem.getQuantity();
                        if ((oldQ < slotMax) && ((eItem.getOwner().equals(owner)) || (owner == null)) && (eItem.getExpiration() == -1L)) {
                            short newQ = (short) Math.min(oldQ + quantity, slotMax);
                            quantity = (short) (quantity - (newQ - oldQ));
                            eItem.setQuantity(newQ);
                            c.getSession().write(MaplePacketCreator.updateInventorySlot(type, eItem, false));
                        }
                    }
                }
                while (quantity > 0) {
                    short newQ = (short) Math.min(quantity, slotMax);
                    if (newQ != 0) {
                        quantity = (short) (quantity - newQ);
                        Item nItem = new Item(itemId, (short) 0, newQ, (short) 0, uniqueid);
                        newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                        if (newSlot == -1) {
                            c.getSession().write(MaplePacketCreator.getInventoryFull());
                            c.getSession().write(MaplePacketCreator.getShowInventoryFull());
                            return -1;
                        }
                        if (gmLog != null) {
                            nItem.setGMLog(gmLog);
                        }
                        if (owner != null) {
                            nItem.setOwner(owner);
                        }
                        if (period > 0L) {
                            nItem.setExpiration(System.currentTimeMillis() + period * 24L * 60L * 60L * 1000L);
                        }
                        if (pet != null) {
                            nItem.setPet(pet);
                            pet.setInventoryPosition(newSlot);
                            c.getPlayer().addPet(pet);
                        }
                        c.getSession().write(MaplePacketCreator.addInventorySlot(type, nItem));
                        if ((GameConstants.isRechargable(itemId)) && (quantity == 0)) {
                            break;
                        }
                    } else {
                        c.getPlayer().havePartyQuest(itemId);
                        c.getSession().write(MaplePacketCreator.enableActions());
                        return (byte) newSlot;
                    }
                }
            } else {
                Item nItem = new Item(itemId, (short) 0, quantity, (short) 0, uniqueid);
                newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                if (newSlot == -1) {
                    c.getSession().write(MaplePacketCreator.getInventoryFull());
                    c.getSession().write(MaplePacketCreator.getShowInventoryFull());
                    return -1;
                }
                if (period > 0L) {
                    nItem.setExpiration(System.currentTimeMillis() + period * 24L * 60L * 60L * 1000L);
                }
                if (gmLog != null) {
                    nItem.setGMLog(gmLog);
                }
                c.getSession().write(MaplePacketCreator.addInventorySlot(type, nItem));
                c.getSession().write(MaplePacketCreator.enableActions());
            }
        } else if (quantity == 1) {
            Item nEquip = ii.getEquipById(itemId, uniqueid);
            if (owner != null) {
                nEquip.setOwner(owner);
            }
            if (gmLog != null) {
                nEquip.setGMLog(gmLog);
            }
            if (period > 0L) {
                nEquip.setExpiration(System.currentTimeMillis() + period * 24L * 60L * 60L * 1000L);
            }
            newSlot = c.getPlayer().getInventory(type).addItem(nEquip);
            if (newSlot == -1) {
                c.getSession().write(MaplePacketCreator.getInventoryFull());
                c.getSession().write(MaplePacketCreator.getShowInventoryFull());
                return -1;
            }
            c.getSession().write(MaplePacketCreator.addInventorySlot(type, nEquip));
            if (GameConstants.isHarvesting(itemId)) {
                c.getPlayer().getStat().handleProfessionTool(c.getPlayer());
            }
        } else {
            throw new InventoryException("Trying to create equip with non-one quantity");
        }

        c.getPlayer().havePartyQuest(itemId);
        return (byte) newSlot;
    }

    public static Item addbyId_Gachapon(MapleClient c, int itemId, short quantity) {
        if ((c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNextFreeSlot() == -1) || (c.getPlayer().getInventory(MapleInventoryType.USE).getNextFreeSlot() == -1) || (c.getPlayer().getInventory(MapleInventoryType.ETC).getNextFreeSlot() == -1) || (c.getPlayer().getInventory(MapleInventoryType.SETUP).getNextFreeSlot() == -1)) {
            return null;
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (((ii.isPickupRestricted(itemId)) && (c.getPlayer().haveItem(itemId, 1, true, false))) || (!ii.itemExists(itemId))) {
            c.getSession().write(MaplePacketCreator.getInventoryFull());
            c.getSession().write(MaplePacketCreator.showItemUnavailable());
            return null;
        }
        MapleInventoryType type = GameConstants.getInventoryType(itemId);
        if (!type.equals(MapleInventoryType.EQUIP)) {
            short slotMax = ii.getSlotMax(itemId);
            List existing = c.getPlayer().getInventory(type).listById(itemId);
            if (!GameConstants.isRechargable(itemId)) {
                Item nItem = null;
                boolean recieved = false;
                if (existing.size() > 0) {
                    Iterator i = existing.iterator();
                    while ((quantity > 0)
                            && (i.hasNext())) {
                        nItem = (Item) i.next();
                        short oldQ = nItem.getQuantity();
                        if (oldQ < slotMax) {
                            recieved = true;
                            short newQ = (short) Math.min(oldQ + quantity, slotMax);
                            quantity = (short) (quantity - (newQ - oldQ));
                            nItem.setQuantity(newQ);
                            c.getSession().write(MaplePacketCreator.updateInventorySlot(type, nItem, false));
                        }

                    }

                }

                while (quantity > 0) {
                    short newQ = (short) Math.min(quantity, slotMax);
                    if (newQ == 0) {
                        break;
                    }
                    quantity = (short) (quantity - newQ);
                    nItem = new Item(itemId, (short) 0, newQ, (short) 0);
                    short newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                    if ((newSlot == -1) && (recieved)) {
                        return nItem;
                    }
                    if (newSlot == -1) {
                        return null;
                    }
                    recieved = true;
                    c.getSession().write(MaplePacketCreator.addInventorySlot(type, nItem));
                    if ((GameConstants.isRechargable(itemId)) && (quantity == 0)) {
                        break;
                    }

                }

                if (recieved) {
                    c.getPlayer().havePartyQuest(nItem.getItemId());
                    return nItem;
                }
            } else {
                Item nItem = new Item(itemId, (short) 0, quantity, (short) 0);
                short newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                if (newSlot == -1) {
                    return null;
                }
                c.getSession().write(MaplePacketCreator.addInventorySlot(type, nItem));
                c.getPlayer().havePartyQuest(nItem.getItemId());
                return nItem;
            }
        } else {
            if (quantity == 1) {
                Item item = ii.randomizeStats((Equip) ii.getEquipById(itemId));
                short newSlot = c.getPlayer().getInventory(type).addItem(item);
                if (newSlot == -1) {
                    return null;
                }
                c.getSession().write(MaplePacketCreator.addInventorySlot(type, item, true));
                c.getPlayer().havePartyQuest(item.getItemId());
                return item;
            }
            throw new InventoryException("Trying to create equip with non-one quantity");
        }

        return null;
    }

    public static boolean addFromDrop(MapleClient c, Item item, boolean show) {
        return addFromDrop(c, item, show, false);
    }

    public static boolean addFromDrop(MapleClient c, Item item, boolean show, boolean enhance) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        MapleInventoryType type = GameConstants.getInventoryType(item.getItemId());
        if ((c.getPlayer() == null) || ((ii.isPickupRestricted(item.getItemId())) && (c.getPlayer().haveItem(item.getItemId(), 1, true, false))) || (!ii.itemExists(item.getItemId()))) {
            c.getSession().write(MaplePacketCreator.getInventoryFull());
            c.getSession().write(MaplePacketCreator.showItemUnavailable());
            return false;
        }
        int before = c.getPlayer().itemQuantity(item.getItemId());
        short quantity = item.getQuantity();

        if (!type.equals(MapleInventoryType.EQUIP)) {
            short slotMax = ii.getSlotMax(item.getItemId());
            List existing = c.getPlayer().getInventory(type).listById(item.getItemId());
            if (!GameConstants.isRechargable(item.getItemId())) {
                if (quantity <= 0) {
                    c.getSession().write(MaplePacketCreator.getInventoryFull());
                    c.getSession().write(MaplePacketCreator.showItemUnavailable());
                    return false;
                }
                if (existing.size() > 0) {
                    Iterator i = existing.iterator();
                    while (quantity > 0) {
                        if (i.hasNext()) {
                            final Item eItem = (Item) i.next();
                            final short oldQ = eItem.getQuantity();
                            if ((oldQ < slotMax) && (item.getOwner().equals(eItem.getOwner())) && (item.getExpiration() == eItem.getExpiration())) {
                                final short newQ = (short) Math.min(oldQ + quantity, slotMax);
                                quantity -= (newQ - oldQ);
                                eItem.setQuantity(newQ);
                                c.getSession().write(MaplePacketCreator.updateInventorySlot(type, eItem, true));
                            }
                        } else {
                            break;
                        }
                    }

                }

                while (quantity > 0) {
                    short newQ = (short) Math.min(quantity, slotMax);
                    quantity -= newQ;
                    Item nItem = new Item(item.getItemId(), (short) 0, newQ, item.getFlag());
                    nItem.setExpiration(item.getExpiration());
                    nItem.setOwner(item.getOwner());
                    nItem.setPet(item.getPet());
                    nItem.setGMLog(item.getGMLog());
                    short newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                    if (newSlot == -1) {
                        c.getSession().write(MaplePacketCreator.getInventoryFull());
                        c.getSession().write(MaplePacketCreator.getShowInventoryFull());
                        item.setQuantity((short) (quantity + newQ));
                        return false;
                    }
                    c.getSession().write(MaplePacketCreator.addInventorySlot(type, nItem, true));
                }
            } else {
                Item nItem = new Item(item.getItemId(), (short) 0, quantity, item.getFlag());
                nItem.setExpiration(item.getExpiration());
                nItem.setOwner(item.getOwner());
                nItem.setPet(item.getPet());
                nItem.setGMLog(item.getGMLog());
                short newSlot = c.getPlayer().getInventory(type).addItem(nItem);
                if (newSlot == -1) {
                    c.getSession().write(MaplePacketCreator.getInventoryFull());
                    c.getSession().write(MaplePacketCreator.getShowInventoryFull());
                    return false;
                }
                c.getSession().write(MaplePacketCreator.addInventorySlot(type, nItem));
                c.getSession().write(MaplePacketCreator.enableActions());
            }
        } else {
            if (quantity == 1) {
                if (enhance) {
                    item = checkEnhanced(item, c.getPlayer());
                }
                short newSlot = c.getPlayer().getInventory(type).addItem(item);
                if (newSlot == -1) {
                    c.getSession().write(MaplePacketCreator.getInventoryFull());
                    c.getSession().write(MaplePacketCreator.getShowInventoryFull());
                    return false;
                }
                c.getSession().write(MaplePacketCreator.addInventorySlot(type, item, true));
                if (GameConstants.isHarvesting(item.getItemId())) {
                    c.getPlayer().getStat().handleProfessionTool(c.getPlayer());
                }
            } else {
                throw new RuntimeException("Trying to create equip with non-one quantity");
            }
        }
        if ((item.getQuantity() >= 50) && (item.getItemId() == 2340000)) {
            c.setMonitored(true);
        }
        if (before == 0) {
            switch (item.getItemId()) {
                case 4001128:
                    c.getPlayer().dropMessage(5, "You have gained a Powder Keg, you can give this in to Aramia of Henesys.");
                    break;
                case 4001246:
                    c.getPlayer().dropMessage(5, "You have gained a Warm Sun, you can give this in to Maple Tree Hill through @joyce.");
                    break;
                case 4001473:
                    c.getPlayer().dropMessage(5, "You have gained a Tree Decoration, you can give this in to White Christmas Hill through @joyce.");
                    break;
            }
        }

        c.getPlayer().havePartyQuest(item.getItemId());
        if (show) {
            c.getSession().write(MaplePacketCreator.getShowItemGain(item.getItemId(), item.getQuantity()));
        }
        return true;
    }

    private static Item checkEnhanced(Item before, MapleCharacter chr) {
        if ((before instanceof Equip)) {
            Equip eq = (Equip) before;
            if ((eq.getState() == 0) && ((eq.getUpgradeSlots() >= 1) || (eq.getLevel() >= 1)) && (GameConstants.canScroll(eq.getItemId())) && (Randomizer.nextInt(100) >= 80)) {
                eq.resetPotential();
            }
        }

        return before;
    }

    public static boolean checkSpace(MapleClient c, int itemid, int quantity, String owner) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if ((c.getPlayer() == null) || ((ii.isPickupRestricted(itemid)) && (c.getPlayer().haveItem(itemid, 1, true, false))) || (!ii.itemExists(itemid))) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return false;
        }
        if ((quantity <= 0) && (!GameConstants.isRechargable(itemid))) {
            return false;
        }
        MapleInventoryType type = GameConstants.getInventoryType(itemid);
        if ((c == null) || (c.getPlayer() == null) || (c.getPlayer().getInventory(type) == null)) {
            return false;
        }
        if (!type.equals(MapleInventoryType.EQUIP)) {
            short slotMax = ii.getSlotMax(itemid);
            List<Item> existing = c.getPlayer().getInventory(type).listById(itemid);
            if ((!GameConstants.isRechargable(itemid))
                    && (existing.size() > 0)) {
                for (Item eItem : existing) {
                    short oldQ = eItem.getQuantity();
                    if ((oldQ < slotMax) && (owner != null) && (owner.equals(eItem.getOwner()))) {
                        short newQ = (short) Math.min(oldQ + quantity, slotMax);
                        quantity -= newQ - oldQ;
                    }
                    if (quantity <= 0) {
                        break;
                    }
                }
            }
            int numSlotsNeeded;
            if ((slotMax > 0) && (!GameConstants.isRechargable(itemid))) {
                numSlotsNeeded = (int) Math.ceil(quantity / slotMax);
            } else {
                numSlotsNeeded = 1;
            }
            return !c.getPlayer().getInventory(type).isFull(numSlotsNeeded - 1);
        }
        return !c.getPlayer().getInventory(type).isFull();
    }

    public static boolean removeFromSlot(MapleClient c, MapleInventoryType type, short slot, short quantity, boolean fromDrop) {
        return removeFromSlot(c, type, slot, quantity, fromDrop, false);
    }

    public static boolean removeFromSlot(MapleClient c, MapleInventoryType type, short slot, short quantity, boolean fromDrop, boolean consume) {
        if ((c.getPlayer() == null) || (c.getPlayer().getInventory(type) == null)) {
            return false;
        }
        Item item = c.getPlayer().getInventory(type).getItem(slot);
        if (item != null) {
            boolean allowZero = (consume) && (GameConstants.isRechargable(item.getItemId()));
            c.getPlayer().getInventory(type).removeItem(slot, quantity, allowZero);
            if (GameConstants.isHarvesting(item.getItemId())) {
                c.getPlayer().getStat().handleProfessionTool(c.getPlayer());
            }

            if ((item.getQuantity() == 0) && (!allowZero)) {
                c.getSession().write(MaplePacketCreator.clearInventoryItem(type, item.getPosition(), fromDrop));
            } else {
                c.getSession().write(MaplePacketCreator.updateInventorySlot(type, item, fromDrop));
            }
            return true;
        }
        return false;
    }

    public static boolean removeById(MapleClient c, MapleInventoryType type, int itemId, int quantity, boolean fromDrop, boolean consume) {
        int remremove = quantity;
        if ((c.getPlayer() == null) || (c.getPlayer().getInventory(type) == null)) {
            return false;
        }
        for (Item item : c.getPlayer().getInventory(type).listById(itemId)) {
            int theQ = item.getQuantity();
            if ((remremove <= theQ) && (removeFromSlot(c, type, item.getPosition(), (short) remremove, fromDrop, consume))) {
                remremove = 0;
                break;
            }
            if ((remremove > theQ) && (removeFromSlot(c, type, item.getPosition(), item.getQuantity(), fromDrop, consume))) {
                remremove -= theQ;
            }
        }
        return remremove <= 0;
    }

    public static boolean removeFromSlot_Lock(MapleClient c, MapleInventoryType type, short slot, short quantity, boolean fromDrop, boolean consume) {
        if ((c.getPlayer() == null) || (c.getPlayer().getInventory(type) == null)) {
            return false;
        }
        Item item = c.getPlayer().getInventory(type).getItem(slot);
        if (item != null) {
            if ((ItemFlag.LOCK.check(item.getFlag())) || (ItemFlag.UNTRADEABLE.check(item.getFlag()))) {
                return false;
            }
            return removeFromSlot(c, type, slot, quantity, fromDrop, consume);
        }
        return false;
    }

    public static boolean removeById_Lock(MapleClient c, MapleInventoryType type, int itemId) {
        for (Item item : c.getPlayer().getInventory(type).listById(itemId)) {
            if (removeFromSlot_Lock(c, type, item.getPosition(), (short) 1, false, false)) {
                return true;
            }
        }
        return false;
    }

    public static void removeAllById(MapleClient c, int itemId, boolean checkEquipped) {
        MapleInventoryType type = GameConstants.getInventoryType(itemId);
        for (Item item : c.getPlayer().getInventory(type).listById(itemId)) {
            if (item != null) {
                removeFromSlot(c, type, item.getPosition(), item.getQuantity(), true, false);
            }
        }
        if (checkEquipped) {
            Item ii = c.getPlayer().getInventory(type).findById(itemId);
            if (ii != null) {
                c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).removeItem(ii.getPosition());
                c.getPlayer().equipChanged();
            }
        }
    }

    public static void move(MapleClient c, MapleInventoryType type, short src, short dst) {
        if ((src < 0) || (dst < 0) || (src == dst) || (type == MapleInventoryType.EQUIPPED)) {
            return;
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        Item source = c.getPlayer().getInventory(type).getItem(src);
        Item initialTarget = c.getPlayer().getInventory(type).getItem(dst);
        if (source == null) {
            return;
        }
        boolean bag = false;
        boolean switchSrcDst = false;
        boolean bothBag = false;
        short eqIndicator = -1;
        if (dst > c.getPlayer().getInventory(type).getSlotLimit()) {
            if ((type == MapleInventoryType.ETC) && (dst > 100) && (dst % 100 != 0)) {
                int eSlot = c.getPlayer().getExtendedSlot(dst / 100 - 1);
                if (eSlot > 0) {
                    MapleStatEffect ee = ii.getItemEffect(eSlot);
                    if ((dst % 100 > ee.getSlotCount()) || (ee.getType() != ii.getBagType(source.getItemId())) || (ee.getType() <= 0)) {
                        c.getPlayer().dropMessage(1, "无法将该道具移动到小背包.");
                        c.getSession().write(MaplePacketCreator.enableActions());
                        return;
                    }
                    eqIndicator = 0;
                    bag = true;
                } else {
                    c.getPlayer().dropMessage(1, "无法将该道具移动到小背包.");
                    c.getSession().write(MaplePacketCreator.enableActions());
                    return;
                }
            } else {
                c.getPlayer().dropMessage(1, "You may not move it there.");
                c.getSession().write(MaplePacketCreator.enableActions());
                return;
            }
        }
        if ((src > c.getPlayer().getInventory(type).getSlotLimit()) && (type == MapleInventoryType.ETC) && (src > 100) && (src % 100 != 0)) {
            if (!bag) {
                switchSrcDst = true;
                eqIndicator = 0;
                bag = true;
            } else {
                bothBag = true;
            }
        }
        short olddstQ = -1;
        if (initialTarget != null) {
            olddstQ = initialTarget.getQuantity();
        }
        short oldsrcQ = source.getQuantity();
        short slotMax = ii.getSlotMax(source.getItemId());
        c.getPlayer().getInventory(type).move(src, dst, slotMax);
        if (GameConstants.isHarvesting(source.getItemId())) {
            c.getPlayer().getStat().handleProfessionTool(c.getPlayer());
        }
        if ((!type.equals(MapleInventoryType.EQUIP)) && (initialTarget != null) && (initialTarget.getItemId() == source.getItemId()) && (initialTarget.getOwner().equals(source.getOwner())) && (initialTarget.getExpiration() == source.getExpiration()) && (!GameConstants.isRechargable(source.getItemId())) && (!type.equals(MapleInventoryType.CASH))) {
            if (GameConstants.isHarvesting(initialTarget.getItemId())) {
                c.getPlayer().getStat().handleProfessionTool(c.getPlayer());
            }
            if (olddstQ + oldsrcQ > slotMax) {
                c.getSession().write(MaplePacketCreator.moveAndMergeWithRestInventoryItem(type, src, dst, (short) (olddstQ + oldsrcQ - slotMax), slotMax, bag, switchSrcDst, bothBag));
            } else {
                c.getSession().write(MaplePacketCreator.moveAndMergeInventoryItem(type, src, dst, c.getPlayer().getInventory(type).getItem(dst).getQuantity(), bag, switchSrcDst, bothBag));
            }
        } else {
            c.getSession().write(MaplePacketCreator.moveInventoryItem(type, switchSrcDst ? dst : src, switchSrcDst ? src : dst, eqIndicator, bag, bothBag));
        }
    }

    public static void equip(MapleClient c, short src, short dst) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        MapleCharacter chr = c.getPlayer();
        if (chr == null) {
            return;
        }
        PlayerStats statst = chr.getStat();
        Equip source = (Equip) chr.getInventory(MapleInventoryType.EQUIP).getItem(src);
        Equip target = (Equip) chr.getInventory(MapleInventoryType.EQUIPPED).getItem(dst);
        if ((source == null) || (source.getDurability() == 0) || (GameConstants.isHarvesting(source.getItemId()))) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        //log.info(new StringBuilder().append("穿戴装备 - ").append(source.getItemId()).append(" dst: ").append(src).append(" dst: ").append(src).toString());
        if (((source.getItemId() == 1003142) || (source.getItemId() == 1002140) || (source.getItemId() == 1042003) || (source.getItemId() == 1062007) || (source.getItemId() == 1322013))
                && (!chr.isIntern())) {
            chr.dropMessage(1, "无法佩带此物品");
            log.info(new StringBuilder().append("[作弊] 非管理员玩家: ").append(chr.getName()).append(" 非法穿戴GM装备 ").append(source.getItemId()).toString());
            removeById(c, MapleInventoryType.EQUIP, source.getItemId(), 1, true, false);
            AutobanManager.getInstance().autoban(chr.getClient(), "无理由.");
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }

        Map stats = ii.getEquipStats(source.getItemId());
        if (stats == null) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if ((dst > -1200) && (dst < -999) && (!GameConstants.is龙龙装备(source.getItemId())) && (!GameConstants.is机甲装备(source.getItemId()))) {
            //log.info(new StringBuilder().append("穿戴装备 - 1 ").append(source.getItemId()).toString());
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if (((dst < -5003) || ((dst >= -999) && (dst < -99))) && (!stats.containsKey("cash"))) {
            //log.info(new StringBuilder().append("穿戴装备 - 2 ").append(source.getItemId()).append(" dst: ").append(dst).append(" 检测1: ").append(dst <= -1200).append(" 检测2: ").append((dst >= -999) && (dst < -99)).append(" 检测3: ").append(!stats.containsKey("cash")).toString());
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if ((dst <= -1200) && (dst > -1300) && (chr.getAndroid() == null)) {
            //log.info(Boolean.valueOf(new StringBuilder().append("穿戴装备 - 3 ").append(source.getItemId()).append(" dst: ").append(dst).append(" 检测1: ").append((dst <= -1200) && (dst > -1300)).append(" 检测2: ").append(chr.getAndroid()).toString() == null));
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if (!ii.canEquip(stats, source.getItemId(), chr.getLevel(), chr.getJob(), chr.getFame(), statst.getTotalStr(), statst.getTotalDex(), statst.getTotalLuk(), statst.getTotalInt(), chr.getStat().levelBonus)) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if ((GameConstants.isWeapon(source.getItemId())) && (dst != -10) && (dst != -11)) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if ((dst == -23) && (!GameConstants.isMountItemAvailable(source.getItemId(), chr.getJob()))) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if ((dst == -123) && (source.getItemId() / 10000 != 190)) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if ((dst <= -5000) && (dst > -5003) && (source.getItemId() / 10000 != 120)) {
            //log.info(new StringBuilder().append("穿戴装备 - 3 ").append(source.getItemId()).append(" dst: ").append(dst).append(" 检测1: ").append((dst <= -5000) && (dst >= -5003)).append(" 检测2: ").append(source.getItemId() / 10000 != 120).toString());
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if (dst == -37) {
            MapleQuestStatus stat = chr.getQuestNoAdd(MapleQuest.getInstance(122700));
            if ((stat == null) || (stat.getCustomData() == null) || (Long.parseLong(stat.getCustomData()) < System.currentTimeMillis())) {
                c.getSession().write(MaplePacketCreator.enableActions());
                return;
            }
        }
        if ((GameConstants.is双刀副手(source.getItemId())) || (source.getItemId() / 10000 == 135)) {
            dst = -10;
        }
        if ((GameConstants.is龙龙装备(source.getItemId())) && ((chr.getJob() < 2200) || (chr.getJob() > 2218))) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }

        if ((GameConstants.is机甲装备(source.getItemId())) && ((chr.getJob() < 3500) || (chr.getJob() > 3512))) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }

        if (source.getItemId() / 1000 == 1112) {
            List theList;
            for (RingSet s : RingSet.values()) {
                if (s.id.contains(Integer.valueOf(source.getItemId()))) {
                    theList = chr.getInventory(MapleInventoryType.EQUIPPED).listIds();
                    for (Integer i : s.id) {
                        if (theList.contains(i)) {
                            chr.dropMessage(1, "您无法佩戴这个道具，因为您已经佩戴了过相同效果的道具。");
                            c.getSession().write(MaplePacketCreator.enableActions());
                            return;
                        }
                    }
                }
            }
        }

        switch (dst) {
            case -6: {
                Item top = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -5);
                if ((top == null) || (!GameConstants.isOverall(top.getItemId()))) {
                    break;
                }
                if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
                    c.getSession().write(MaplePacketCreator.getInventoryFull());
                    c.getSession().write(MaplePacketCreator.getShowInventoryFull());
                    return;
                }
                unequip(c, (short) -5, chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                break;
            }
            case -5: {
                Item top = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -5);
                Item bottom = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -6);
                if ((top != null) && (GameConstants.isOverall(source.getItemId()))) {
                    if (chr.getInventory(MapleInventoryType.EQUIP).isFull((bottom != null) && (GameConstants.isOverall(source.getItemId())) ? 1 : 0)) {
                        c.getSession().write(MaplePacketCreator.getInventoryFull());
                        c.getSession().write(MaplePacketCreator.getShowInventoryFull());
                        return;
                    }
                    unequip(c, (short) -5, chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                }
                if ((bottom == null) || (!GameConstants.isOverall(source.getItemId()))) {
                    break;
                }
                if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
                    c.getSession().write(MaplePacketCreator.getInventoryFull());
                    c.getSession().write(MaplePacketCreator.getShowInventoryFull());
                    return;
                }
                unequip(c, (short) -6, chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                break;
            }
            case -10: {
                Item weapon = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -11);
                if (GameConstants.is双刀副手(source.getItemId())) {
                    if (((chr.getJob() == 900) || ((chr.getJob() >= 430) && (chr.getJob() <= 434))) && (weapon != null) && (GameConstants.is双刀主手(weapon.getItemId()))) {
                        break;
                    }
                    c.getSession().write(MaplePacketCreator.getInventoryFull());
                    c.getSession().write(MaplePacketCreator.getShowInventoryFull());
                    return;
                } else {
                    if ((weapon == null) || (!GameConstants.isTwoHanded(weapon.getItemId(), chr.getJob()))) {
                        break;
                    }
                    if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
                        c.getSession().write(MaplePacketCreator.getInventoryFull());
                        c.getSession().write(MaplePacketCreator.getShowInventoryFull());
                        return;
                    }
                    unequip(c, (short) -11, chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                }
                break;
            }
            case -11: {
                Item shield = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -10);
                if ((shield == null) || (!GameConstants.isTwoHanded(source.getItemId(), chr.getJob()))) {
                    break;
                }
                if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
                    c.getSession().write(MaplePacketCreator.getInventoryFull());
                    c.getSession().write(MaplePacketCreator.getShowInventoryFull());
                    return;
                }
                unequip(c, (short) -10, chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                break;
            }
            case -9:
            case -8:
            case -7:
        }
        source = (Equip) chr.getInventory(MapleInventoryType.EQUIP).getItem(src);
        target = (Equip) chr.getInventory(MapleInventoryType.EQUIPPED).getItem(dst);
        if (source == null) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        short flag = source.getFlag();
        boolean upgrade = false;
        boolean charm = false;
        if (((stats.get("equipTradeBlock") != null) || (source.getItemId() / 10000 == 167))
                && (!ItemFlag.UNTRADEABLE.check(flag))) {
            flag = (short) (flag | ItemFlag.UNTRADEABLE.getValue());
            source.setFlag(flag);
            upgrade = true;
        }

        if (source.getItemId() / 10000 == 166) {
            if (source.getAndroid() == null) {
                int uid = MapleInventoryIdentifier.getInstance();
                source.setUniqueId(uid);
                source.setAndroid(MapleAndroid.create(source.getItemId(), uid));
                flag = (short) (flag | ItemFlag.LOCK.getValue());
                flag = (short) (flag | ItemFlag.UNTRADEABLE.getValue());
                flag = (short) (flag | ItemFlag.ANDROID_ACTIVATED.getValue());
                source.setFlag(flag);
                upgrade = true;
            }

            chr.removeAndroid();
            chr.setAndroid(source.getAndroid());
        } else if (chr.getAndroid() != null) {
            if (dst <= -1300) {
                chr.setAndroid(chr.getAndroid());
            } else if (dst <= -1200) {
                chr.updateAndroid(dst, source.getItemId());
            }
        }
        if ((source.getCharmEXP() > 0) && (!ItemFlag.CHARM_EQUIPPED.check(flag))) {
            chr.getTrait(MapleTrait.MapleTraitType.charm).addExp(source.getCharmEXP(), chr);
            source.setCharmEXP((short) 0);
            flag = (short) (flag | ItemFlag.CHARM_EQUIPPED.getValue());
            source.setFlag(flag);
            upgrade = true;
            charm = true;
        }

        chr.getInventory(MapleInventoryType.EQUIP).removeSlot(src);
        if (target != null) {
            chr.getInventory(MapleInventoryType.EQUIPPED).removeSlot(dst);
        }
        source.setPosition(dst);
        chr.getInventory(MapleInventoryType.EQUIPPED).addFromDB(source);
        if (target != null) {
            target.setPosition(src);
            chr.getInventory(MapleInventoryType.EQUIP).addFromDB(target);
        }
        if (GameConstants.isWeapon(source.getItemId())) {
            chr.cancelEffectFromBuffStat(MapleBuffStat.攻击加速);
            chr.cancelEffectFromBuffStat(MapleBuffStat.暗器伤人);
            chr.cancelEffectFromBuffStat(MapleBuffStat.无形箭弩);
            chr.cancelEffectFromBuffStat(MapleBuffStat.属性攻击);
            chr.cancelEffectFromBuffStat(MapleBuffStat.雷鸣冲击);
        }
        if ((source.getItemId() / 10000 == 190) || (source.getItemId() / 10000 == 191)) {
            chr.cancelEffectFromBuffStat(MapleBuffStat.骑兽技能);
            chr.cancelEffectFromBuffStat(MapleBuffStat.金属机甲);
        } else if (GameConstants.isReverseItem(source.getItemId())) {
            chr.finishAchievement(9);
        } else if (GameConstants.isTimelessItem(source.getItemId())) {
            chr.finishAchievement(10);
        } else if ((stats.containsKey("reqLevel")) && (((Integer) stats.get("reqLevel")).intValue() >= 140)) {
            chr.finishAchievement(41);
        } else if ((stats.containsKey("reqLevel")) && (((Integer) stats.get("reqLevel")).intValue() >= 130)) {
            chr.finishAchievement(40);
        } else if (source.getItemId() == 1122017) {
            chr.startFairySchedule(true, true);
        }
        if (source.getState() >= 5) {
            int[] potentials = {source.getPotential1(), source.getPotential2(), source.getPotential3()};
            for (int i : potentials) {
                if (i > 0) {
                    StructItemOption pot = (StructItemOption) ii.getPotentialInfo(i).get(ii.getReqLevel(source.getItemId()) / 10);
                    if ((pot != null) && (pot.get("skillID") > 0)) {
                        chr.changeSkillLevel_Skip(SkillFactory.getSkill(PlayerStats.getSkillByJob(pot.get("skillID"), chr.getJob())), 1, (byte) 0, true);
                    }
                }
            }
        }
        if (source.getSocketState() >= 19) {
            int[] sockets = {source.getSocket1(), source.getSocket2(), source.getSocket3()};
            for (int i : sockets) {
                if (i > 0) {
                    StructItemOption soc = ii.getSocketInfo(i);
                    if ((soc != null) && (soc.get("skillID") > 0)) {
                        chr.changeSkillLevel_Skip(SkillFactory.getSkill(PlayerStats.getSkillByJob(soc.get("skillID"), chr.getJob())), 1, (byte) 0, true);
                    }
                }
            }
        }
        if ((upgrade) && (charm)) {
            c.getSession().write(MaplePacketCreator.moveAndUpgradeItem(GameConstants.getInventoryType(source.getItemId()), source, src, dst, chr));
        } else if (upgrade) {
            c.getSession().write(MaplePacketCreator.moveAndUpgradeItem(MapleInventoryType.EQUIP, source, src, dst, chr));
        } else {
            c.getSession().write(MaplePacketCreator.moveInventoryItem(MapleInventoryType.EQUIP, src, dst, (short) 2, false, false));
        }
        chr.equipChanged();
    }

    public static void unequip(MapleClient c, short src, short dst) {
        Equip source = (Equip) c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(src);
        Equip target = (Equip) c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(dst);

        if ((dst < 0) || (source == null) || ((GameConstants.GMS) && (src == -55))) {
            return;
        }
        if ((target != null) && (src <= 0)) {
            c.getSession().write(MaplePacketCreator.getInventoryFull());
            return;
        }
        c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).removeSlot(src);
        if (target != null) {
            c.getPlayer().getInventory(MapleInventoryType.EQUIP).removeSlot(dst);
        }
        source.setPosition(dst);
        c.getPlayer().getInventory(MapleInventoryType.EQUIP).addFromDB(source);
        if (target != null) {
            target.setPosition(src);
            c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).addFromDB(target);
        }

        if (GameConstants.isWeapon(source.getItemId())) {
            c.getPlayer().cancelEffectFromBuffStat(MapleBuffStat.攻击加速);
            c.getPlayer().cancelEffectFromBuffStat(MapleBuffStat.暗器伤人);
            c.getPlayer().cancelEffectFromBuffStat(MapleBuffStat.无形箭弩);
            c.getPlayer().cancelEffectFromBuffStat(MapleBuffStat.属性攻击);
            c.getPlayer().cancelEffectFromBuffStat(MapleBuffStat.雷鸣冲击);
        } else if ((source.getItemId() / 10000 == 190) || (source.getItemId() / 10000 == 191)) {
            c.getPlayer().cancelEffectFromBuffStat(MapleBuffStat.骑兽技能);
            c.getPlayer().cancelEffectFromBuffStat(MapleBuffStat.金属机甲);
        } else if (source.getItemId() / 10000 == 166) {
            c.getPlayer().removeAndroid();
        } else if ((source.getItemId() / 10000 == 167) && (c.getPlayer().getAndroid() != null)) {
            c.getSession().write(AndroidPacket.removeAndroidHeart());
        } else if (c.getPlayer().getAndroid() != null) {
            if (src <= -1300) {
                c.getPlayer().setAndroid(c.getPlayer().getAndroid());
            } else if (src <= -1200) {
                c.getPlayer().updateAndroid(src, 0);
            }
        } else if (source.getItemId() == 1122017) {
            c.getPlayer().cancelFairySchedule(true);
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (source.getState() >= 5) {
            int[] potentials = {source.getPotential1(), source.getPotential2(), source.getPotential3()};
            for (int i : potentials) {
                if (i > 0) {
                    StructItemOption pot = (StructItemOption) ii.getPotentialInfo(i).get(ii.getReqLevel(source.getItemId()) / 10);
                    if ((pot != null) && (pot.get("skillID") > 0)) {
                        c.getPlayer().changeSkillLevel_Skip(SkillFactory.getSkill(PlayerStats.getSkillByJob(pot.get("skillID"), c.getPlayer().getJob())), 0, (byte) 0, true);
                    }
                }
            }
        }
        if (source.getSocketState() >= 19) {
            int[] sockets = {source.getSocket1(), source.getSocket2(), source.getSocket3()};
            for (int i : sockets) {
                if (i > 0) {
                    StructItemOption soc = ii.getSocketInfo(i);
                    if ((soc != null) && (soc.get("skillID") > 0)) {
                        c.getPlayer().changeSkillLevel_Skip(SkillFactory.getSkill(PlayerStats.getSkillByJob(soc.get("skillID"), c.getPlayer().getJob())), 0, (byte) 0, true);
                    }
                }
            }
        }
        c.getSession().write(MaplePacketCreator.moveInventoryItem(MapleInventoryType.EQUIP, src, dst, (short) 1, false, false));
        c.getPlayer().equipChanged();
    }

    public static boolean drop(MapleClient c, MapleInventoryType type, short src, short quantity) {
        return drop(c, type, src, quantity, false);
    }

    public static boolean drop(MapleClient c, MapleInventoryType type, short src, short quantity, boolean npcInduced) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (src < 0) {
            type = MapleInventoryType.EQUIPPED;
        }
        if ((c.getPlayer() == null) || (c.getPlayer().getMap() == null)) {
            return false;
        }
        Item source = c.getPlayer().getInventory(type).getItem(src);
        if ((quantity < 0) || (source == null) || ((GameConstants.GMS) && (src == -55)) || ((!npcInduced) && (GameConstants.isPet(source.getItemId()))) || ((quantity == 0) && (!GameConstants.isRechargable(source.getItemId()))) || (c.getPlayer().inPVP())) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return false;
        }

        short flag = source.getFlag();
        if ((quantity > source.getQuantity()) && (!GameConstants.isRechargable(source.getItemId()))) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return false;
        }
        if ((ItemFlag.LOCK.check(flag)) || ((quantity != 1) && (type == MapleInventoryType.EQUIP))) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return false;
        }
        Point dropPos = new Point(c.getPlayer().getPosition());
        c.getPlayer().getCheatTracker().checkDrop();
        if ((quantity < source.getQuantity()) && (!GameConstants.isRechargable(source.getItemId()))) {
            Item target = source.copy();
            target.setQuantity(quantity);
            source.setQuantity((short) (source.getQuantity() - quantity));
            c.getSession().write(MaplePacketCreator.dropInventoryItemUpdate(type, source));

            if ((ii.isDropRestricted(target.getItemId())) || (ii.isAccountShared(target.getItemId()))) {
                if (ItemFlag.KARMA_EQ.check(flag)) {
                    target.setFlag((short) (byte) (flag - ItemFlag.KARMA_EQ.getValue()));
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), target, dropPos, true, true);
                } else if (ItemFlag.KARMA_USE.check(flag)) {
                    target.setFlag((short) (byte) (flag - ItemFlag.KARMA_USE.getValue()));
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), target, dropPos, true, true);
                } else {
                    c.getPlayer().getMap().disappearingItemDrop(c.getPlayer(), c.getPlayer(), target, dropPos);
                }
            } else if ((GameConstants.isPet(source.getItemId())) || (ItemFlag.UNTRADEABLE.check(flag))) {
                c.getPlayer().getMap().disappearingItemDrop(c.getPlayer(), c.getPlayer(), target, dropPos);
            } else {
                c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), target, dropPos, true, true);
            }
        } else {
            c.getPlayer().getInventory(type).removeSlot(src);
            if (GameConstants.isHarvesting(source.getItemId())) {
                c.getPlayer().getStat().handleProfessionTool(c.getPlayer());
            }
            c.getSession().write(MaplePacketCreator.dropInventoryItem(src < 0 ? MapleInventoryType.EQUIP : type, src));
            if (src < 0) {
                c.getPlayer().equipChanged();
            }

            if ((ii.isDropRestricted(source.getItemId())) || (ii.isAccountShared(source.getItemId()))) {
                if (ItemFlag.KARMA_EQ.check(flag)) {
                    source.setFlag((short) (byte) (flag - ItemFlag.KARMA_EQ.getValue()));
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), source, dropPos, true, true);
                } else if (ItemFlag.KARMA_USE.check(flag)) {
                    source.setFlag((short) (byte) (flag - ItemFlag.KARMA_USE.getValue()));
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), source, dropPos, true, true);
                } else {
                    c.getPlayer().getMap().disappearingItemDrop(c.getPlayer(), c.getPlayer(), source, dropPos);
                }
            } else if ((GameConstants.isPet(source.getItemId())) || (ItemFlag.UNTRADEABLE.check(flag))) {
                c.getPlayer().getMap().disappearingItemDrop(c.getPlayer(), c.getPlayer(), source, dropPos);
            } else {
                c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), source, dropPos, true, true);
            }
        }

        return true;
    }
}