package server;

import client.MapleClient;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.ItemLoader;
import client.inventory.MapleInventoryIdentifier;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.inventory.MapleRing;
import constants.GameConstants;
import database.DatabaseConnection;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import tools.FileoutputUtil;
import tools.Pair;
import tools.packet.MTSCSPacket;

public class CashShop
        implements Serializable {

    private static final long serialVersionUID = 231541893513373579L;
    private int accountId;
    private int characterId;
    private ItemLoader factory = ItemLoader.现金道具;
    private List<Item> inventory = new ArrayList();
    private List<Integer> uniqueids = new ArrayList();

    public CashShop(int accountId, int characterId, int jobType) throws SQLException {
        this.accountId = accountId;
        this.characterId = characterId;
        for (Pair<Item, MapleInventoryType> item : this.factory.loadItems(false, accountId).values()) {
            this.inventory.add(item.getLeft());
        }
    }

    public int getItemsSize() {
        return this.inventory.size();
    }

    public List<Item> getInventory() {
        return this.inventory;
    }

    public Item findByCashId(int cashId) {
        for (Item item : this.inventory) {
            if (item.getUniqueId() == cashId) {
                return item;
            }
        }

        return null;
    }

    public void checkExpire(MapleClient c) {
        List<Item> toberemove = new ArrayList<Item>();
        for (Item item : this.inventory) {
            if ((item != null) && (!GameConstants.isPet(item.getItemId())) && (item.getExpiration() > 0L) && (item.getExpiration() < System.currentTimeMillis())) {
                toberemove.add(item);
            }
        }
        if (toberemove.size() > 0) {
            for (Item item : toberemove) {
                removeFromInventory(item);
                c.getSession().write(MTSCSPacket.cashItemExpired(item.getUniqueId()));
            }
            toberemove.clear();
        }
    }

    public Item toItem(CashItemInfo cItem) {
        return toItem(cItem, MapleInventoryManipulator.getUniqueId(cItem.getId(), null), "");
    }

    public Item toItem(CashItemInfo cItem, String gift) {
        return toItem(cItem, MapleInventoryManipulator.getUniqueId(cItem.getId(), null), gift);
    }

    public Item toItem(CashItemInfo cItem, int uniqueid) {
        return toItem(cItem, uniqueid, "");
    }

    public Item toItem(CashItemInfo cItem, int uniqueid, String gift) {
        if (uniqueid <= 0) {
            uniqueid = MapleInventoryIdentifier.getInstance();
        }
        long period = cItem.getPeriod();
        Item ret = null;
        if (GameConstants.getInventoryType(cItem.getId()) == MapleInventoryType.EQUIP) {
            Equip eq = (Equip) MapleItemInformationProvider.getInstance().getEquipById(cItem.getId(), uniqueid);
            if (period > 0) {
                eq.setExpiration(System.currentTimeMillis() + period * 24 * 60 * 60 * 1000);
            }
            eq.setGMLog("商城购买 " + cItem.getSN() + " 时间 " + FileoutputUtil.CurrentReadable_Date());
            eq.setGiftFrom(gift);
            if (GameConstants.isEffectRing(cItem.getId()) && uniqueid > 0) {
                MapleRing ring = MapleRing.loadFromDb(uniqueid);
                if (ring != null) {
                    eq.setRing(ring);
                }
            }
            ret = eq.copy();
        } else {
            Item item = new Item(cItem.getId(), (short)0, (short) cItem.getCount(), (short)0, uniqueid);
            if (GameConstants.isPet(cItem.getId())) {
                period = 90;
                item.setExpiration(System.currentTimeMillis() + period * 24 * 60 * 60 * 1000);
            } else if ((cItem.getId() == 5211047) || (cItem.getId() == 5360014)) {
                item.setExpiration(System.currentTimeMillis() + 10800000);
            } else if (cItem.getId() == 5211060) {
                item.setExpiration(System.currentTimeMillis() + 7200000);
            } else if (period > 0) {
                item.setExpiration(System.currentTimeMillis() + period * 24 * 60 * 60 * 1000);
            } else {
                item.setExpiration(-1);
            }
            item.setGMLog("商城购买 " + cItem.getSN() + " 时间 " + FileoutputUtil.CurrentReadable_Date());
            item.setGiftFrom(gift);
            if (GameConstants.isPet(cItem.getId())) {
                MaplePet pet = MaplePet.createPet(cItem.getId(), uniqueid);
                if (pet != null) {
                    item.setPet(pet);
                }
            }
            ret = item.copy();
        }
        return ret;
    }

    public void addToInventory(Item item) {
        this.inventory.add(item);
    }

    public void removeFromInventory(Item item) {
        this.inventory.remove(item);
    }

    public void gift(int recipient, String from, String message, int sn) {
        gift(recipient, from, message, sn, 0);
    }

    public void gift(int recipient, String from, String message, int sn, int uniqueid) {
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("INSERT INTO `gifts` VALUES (DEFAULT, ?, ?, ?, ?, ?)");
            ps.setInt(1, recipient);
            ps.setString(2, from);
            ps.setString(3, message);
            ps.setInt(4, sn);
            ps.setInt(5, uniqueid);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    public List<Pair<Item, String>> loadGifts() {
        List<Pair<Item, String>> gifts = new ArrayList<Pair<Item, String>>();
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM `gifts` WHERE `recipient` = ?");
            ps.setInt(1, this.characterId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                CashItemInfo cItem = CashItemFactory.getInstance().getItem(rs.getInt("sn"));
                if (cItem == null) {
                    continue;
                }
                Item item = toItem(cItem, rs.getInt("uniqueid"), rs.getString("from"));
                gifts.add(new Pair<Item, String>(item, rs.getString("message")));
                uniqueids.add(item.getUniqueId());
                List<Integer> packages = CashItemFactory.getInstance().getPackageItems(cItem.getId());
                if ((packages != null) && (packages.size() > 0)) {
                    for (int packageItem : packages) {
                        CashItemInfo pack = CashItemFactory.getInstance().getSimpleItem(packageItem);
                        if (pack != null) {
                            addToInventory(toItem(pack, rs.getString("from")));
                        }
                    }
                } else {
                    addToInventory(item);
                }
            }

            rs.close();
            ps.close();
            ps = con.prepareStatement("DELETE FROM `gifts` WHERE `recipient` = ?");
            ps.setInt(1, this.characterId);
            ps.executeUpdate();
            ps.close();
            save();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return gifts;
    }

    public boolean canSendNote(int uniqueid) {
        return this.uniqueids.contains(uniqueid);
    }

    public void sendedNote(int uniqueid) {
        for (int i = 0; i < uniqueids.size(); i++) {
            if (uniqueids.get(i) == uniqueid) {
                uniqueids.remove(i);
            }
        }
    }

    public void save() throws SQLException {
        List<Pair<Item, MapleInventoryType>> itemsWithType = new ArrayList<Pair<Item, MapleInventoryType>>();

        for (Item item : inventory) {
            itemsWithType.add(new Pair<Item, MapleInventoryType>(item, GameConstants.getInventoryType(item.getItemId())));
        }

        factory.saveItems(itemsWithType, this.accountId);
    }
}