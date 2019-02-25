package server;

import client.MapleClient;
import client.SkillFactory;
import client.inventory.Item;
import client.inventory.MapleInventoryIdentifier;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import constants.GameConstants;
import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import tools.FileoutputUtil;
import tools.MaplePacketCreator;
import tools.Pair;

public class MapleShop {

    private static final Set<Integer> rechargeableItems = new LinkedHashSet();
    private int id;
    private int npcId;
    private List<MapleShopItem> items = new LinkedList();
    private List<Pair<Integer, String>> ranks = new ArrayList();

    static {
        rechargeableItems.add(2070000);
        rechargeableItems.add(2070001);
        rechargeableItems.add(2070002);
        rechargeableItems.add(2070003);
        rechargeableItems.add(2070004);
        rechargeableItems.add(2070005);
        rechargeableItems.add(2070006);
        rechargeableItems.add(2070007);
        rechargeableItems.add(2070008);
        rechargeableItems.add(2070009);
        rechargeableItems.add(2070010);
        rechargeableItems.add(2070011);
        rechargeableItems.add(2070012);
        rechargeableItems.add(2070013);
        rechargeableItems.add(2070015);
        rechargeableItems.add(2070016);
        rechargeableItems.add(2070017);
        rechargeableItems.add(2070019);
        rechargeableItems.add(2070020);
        rechargeableItems.add(2070021);
        rechargeableItems.add(2070023);
        rechargeableItems.add(2070024);
        rechargeableItems.add(2330000);
        rechargeableItems.add(2330001);
        rechargeableItems.add(2330002);
        rechargeableItems.add(2330003);
        rechargeableItems.add(2330004);
        rechargeableItems.add(2330005);
        rechargeableItems.add(2330006);
        rechargeableItems.add(2330007);
        rechargeableItems.add(2330008);
    }

    private MapleShop(int id, int npcId) {
        this.id = id;
        this.npcId = npcId;
    }

    public void addItem(MapleShopItem item) {
        this.items.add(item);
    }

    public List<MapleShopItem> getItems() {
        return this.items;
    }

    public void sendShop(MapleClient c) {
        c.getPlayer().setShop(this);
        c.getSession().write(MaplePacketCreator.getNPCShop(getNpcId(), this, c));
    }

    public void sendShop(MapleClient c, int customNpc) {
        c.getPlayer().setShop(this);
        c.getSession().write(MaplePacketCreator.getNPCShop(customNpc, this, c));
    }

    public void buy(MapleClient c, int itemId, short quantity, short position) {
        if (quantity <= 0) {
            AutobanManager.getInstance().addPoints(c, 1000, 0L, "购买道具数量: " + quantity + " 道具: " + itemId);
            return;
        }
        if (itemId == 4000463) {
            AutobanManager.getInstance().addPoints(c, 1000, 0L, "商店非法购买道具: " + itemId + " 数量: " + quantity);
            return;
        }
        if ((itemId / 10000 == 190) && (!GameConstants.isMountItemAvailable(itemId, c.getPlayer().getJob()))) {
            c.getPlayer().dropMessage(1, "您无法够买这个道具。");
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

        if ((position >= this.items.size()) && (!c.getPlayer().getRebuy().isEmpty())) {
            int pos = position - this.items.size();

            Item rebuyItem = c.getPlayer().findById(itemId, pos);
            if (rebuyItem != null) {
                int price = (int) Math.max(Math.ceil(ii.getPrice(itemId) * (GameConstants.isRechargable(itemId) ? 1 : rebuyItem.getQuantity())), 0.0D);
                if ((price >= 0) && (c.getPlayer().getMeso() >= price)) {
                    if (MapleInventoryManipulator.checkSpace(c, itemId, rebuyItem.getQuantity(), rebuyItem.getOwner())) {
                        c.getPlayer().gainMeso(-price, false);
                        MapleInventoryManipulator.addbyItem(c, rebuyItem);
                        c.getPlayer().getRebuy().remove(pos);
                        c.getSession().write(MaplePacketCreator.confirmShopTransaction((byte) 0, this, c, pos));
                    } else {
                        c.getPlayer().dropMessage(1, "您的背包是满的，请整理下背包。");
                        c.getSession().write(MaplePacketCreator.confirmShopTransaction((byte) 0, this, c, -1));
                    }
                } else {
                    c.getSession().write(MaplePacketCreator.confirmShopTransaction((byte) 0, this, c, -1));
                }
            } else {
                c.getPlayer().dropMessage(1, "购买道具出错。");
                c.getSession().write(MaplePacketCreator.confirmShopTransaction((byte) 0, this, c, -1));
            }
            return;
        }
        MapleShopItem item = findById(itemId, position);
        if ((item != null) && (item.getPrice() > 0) && (item.getReqItem() == 0)) {
            if (item.getRank() >= 0) {
                boolean passed = true;
                int y = 0;
                for (Pair i : getRanks()) {
                    if ((c.getPlayer().haveItem(((Integer) i.left).intValue(), 1, true, true)) && (item.getRank() >= y)) {
                        passed = true;
                        break;
                    }
                    y++;
                }
                if (!passed) {
                    c.getPlayer().dropMessage(1, "You need a higher rank.");
                    c.getSession().write(MaplePacketCreator.enableActions());
                    return;
                }
            }
            int price = GameConstants.isRechargable(itemId) ? item.getPrice() : item.getPrice() * quantity;
            if ((price >= 0) && (c.getPlayer().getMeso() >= price)) {
                if (MapleInventoryManipulator.checkSpace(c, itemId, quantity, "")) {
                    c.getPlayer().gainMeso(-price, false);
                    if (GameConstants.isPet(itemId)) {
                        MapleInventoryManipulator.addById(c, itemId, quantity, "", MaplePet.createPet(itemId, MapleInventoryIdentifier.getInstance()), -1L, "Bought from shop " + this.id + ", " + this.npcId + " on " + FileoutputUtil.CurrentReadable_Date());
                    } else {
                        if (GameConstants.isRechargable(itemId)) {
                            quantity = ii.getSlotMax(item.getItemId());
                        }
                        MapleInventoryManipulator.addById(c, itemId, quantity, "商店购买 " + this.id + ", " + this.npcId + " 时间 " + FileoutputUtil.CurrentReadable_Date());
                    }
                } else {
                    c.getPlayer().dropMessage(1, "您的背包是满的，请整理下背包。");
                }
                c.getSession().write(MaplePacketCreator.confirmShopTransaction((byte) 0, this, c, -1));
            }
        } else if ((item != null) && (item.getReqItem() > 0) && (item.getReqItemQ() > 0) && (c.getPlayer().haveItem(item.getReqItem(), item.getReqItemQ() * quantity, false, true))) {
            if (MapleInventoryManipulator.checkSpace(c, itemId, quantity, "")) {
                MapleInventoryManipulator.removeById(c, GameConstants.getInventoryType(item.getReqItem()), item.getReqItem(), item.getReqItemQ() * quantity, false, false);
                if (GameConstants.isPet(itemId)) {
                    MapleInventoryManipulator.addById(c, itemId, quantity, "", MaplePet.createPet(itemId, MapleInventoryIdentifier.getInstance()), -1L, "商店购买 " + this.id + ", " + this.npcId + " 时间 " + FileoutputUtil.CurrentReadable_Date());
                } else if (!GameConstants.isRechargable(itemId)) {
                    int state = item.getState();
                    long period = item.getPeriod();
                    MapleInventoryManipulator.addById(c, itemId, quantity, period, state, "商店购买 " + this.id + ", " + this.npcId + " 时间 " + FileoutputUtil.CurrentReadable_Date());
                } else {
                    quantity = ii.getSlotMax(item.getItemId());
                    MapleInventoryManipulator.addById(c, itemId, quantity, "商店购买 " + this.id + ", " + this.npcId + " 时间 " + FileoutputUtil.CurrentReadable_Date());
                }
            } else {
                c.getPlayer().dropMessage(1, "您的背包是满的，请整理下背包。");
            }
            c.getSession().write(MaplePacketCreator.confirmShopTransaction((byte) 0, this, c, -1));
        }
    }

    public void sell(MapleClient c, MapleInventoryType type, byte slot, short quantity) {
        if (quantity == 65535 || quantity == 0) {
            quantity = 1;
        }
        Item item = c.getPlayer().getInventory(type).getItem((short) slot);
        if (item == null) {
            return;
        }
        if (GameConstants.isThrowingStar(item.getItemId()) || GameConstants.isBullet(item.getItemId())) {
            quantity = item.getQuantity();
        }
        if (quantity < 0) {
            AutobanManager.getInstance().addPoints(c, 1000, 0, "卖出道具 " + quantity + " " + item.getItemId() + " (" + type.name() + "/" + slot + ")");
            return;
        }
        short iQuant = item.getQuantity();
        if (iQuant == 65535) {
            iQuant = 1;
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if ((ii.cantSell(item.getItemId())) || (GameConstants.isPet(item.getItemId()))) {
            return;
        }
        if (quantity <= iQuant && iQuant > 0) {
            if (c.getPlayer().isIntern()) {
                if (item.getQuantity() == quantity) {
                    c.getPlayer().getRebuy().add(item.copy());
                } else {
                    c.getPlayer().getRebuy().add(item.copyWithQuantity(quantity));
                }
            }
            MapleInventoryManipulator.removeFromSlot(c, type, (short) slot, quantity, false);
            double price;
            if ((GameConstants.isThrowingStar(item.getItemId())) || (GameConstants.isBullet(item.getItemId()))) {
                price = ii.getWholePrice(item.getItemId()) / ii.getSlotMax(item.getItemId());
            } else {
                price = ii.getPrice(item.getItemId());
            }
            int recvMesos = (int) Math.max(Math.ceil(price * quantity), 0.0D);
            if ((price != -1.0D) && (recvMesos > 0)) {
                c.getPlayer().gainMeso(recvMesos, false);
            }
            c.getSession().write(MaplePacketCreator.confirmShopTransaction((byte) 4, this, c, -1));
        }
    }

    public void recharge(MapleClient c, byte slot) {
        Item item = c.getPlayer().getInventory(MapleInventoryType.USE).getItem((short) slot);
        if ((item == null) || ((!GameConstants.isThrowingStar(item.getItemId())) && (!GameConstants.isBullet(item.getItemId())))) {
            return;
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        short slotMax = ii.getSlotMax(item.getItemId());
        int skill = GameConstants.getMasterySkill(c.getPlayer().getJob());
        if (skill != 0) {
            slotMax = (short) (slotMax + c.getPlayer().getTotalSkillLevel(SkillFactory.getSkill(skill)) * 10);
        }
        if (item.getQuantity() < slotMax) {
            int price = (int) Math.round(ii.getPrice(item.getItemId()) * (slotMax - item.getQuantity()));
            if (c.getPlayer().getMeso() >= price) {
                item.setQuantity(slotMax);
                c.getSession().write(MaplePacketCreator.updateInventorySlot(MapleInventoryType.USE, item, false));
                c.getPlayer().gainMeso(-price, false, false);
                c.getSession().write(MaplePacketCreator.confirmShopTransaction((byte) 8, this, c, -1));
            }
        }
    }

    protected MapleShopItem findById(int itemId) {
        for (MapleShopItem item : this.items) {
            if (item.getItemId() == itemId) {
                return item;
            }
        }
        return null;
    }

    protected MapleShopItem findById(int itemId, int pos) {
        MapleShopItem item = (MapleShopItem) this.items.get(pos);

        if (item.getItemId() == itemId) {
            return item;
        }
        for (MapleShopItem itemX : this.items) {
            if (itemX.getItemId() == itemId) {
                return itemX;
            }
        }
        return null;
    }

    public static MapleShop createFromDB(int id, boolean isShopId) {
        MapleShop ret = null;
        int shopId;
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(isShopId ? "SELECT * FROM shops WHERE shopid = ?" : "SELECT * FROM shops WHERE npcid = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                shopId = rs.getInt("shopid");
                ret = new MapleShop(shopId, rs.getInt("npcid"));
                rs.close();
                ps.close();
            } else {
                rs.close();
                ps.close();
                return null;
            }
            ps = con.prepareStatement("SELECT * FROM shopitems WHERE shopid = ? ORDER BY position ASC");
            ps.setInt(1, shopId);
            rs = ps.executeQuery();
            List<Integer> recharges = new ArrayList(rechargeableItems);
            while (rs.next()) {
                if (!ii.itemExists(rs.getInt("itemid"))) {
                    continue;
                }
                if ((GameConstants.isThrowingStar(rs.getInt("itemid"))) || (GameConstants.isBullet(rs.getInt("itemid")))) {
                    MapleShopItem starItem = new MapleShopItem((short) 1, rs.getInt("itemid"), rs.getInt("price"), rs.getInt("reqitem"), rs.getInt("reqitemq"), rs.getInt("period"), rs.getInt("state"), rs.getInt("rank"));
                    ret.addItem(starItem);
                    if (rechargeableItems.contains(Integer.valueOf(starItem.getItemId()))) {
                        recharges.remove(Integer.valueOf(starItem.getItemId()));
                    }
                    continue;
                }
                ret.addItem(new MapleShopItem((short) 1000, rs.getInt("itemid"), rs.getInt("price"), rs.getInt("reqitem"), rs.getInt("reqitemq"), rs.getInt("period"), rs.getInt("state"), rs.getInt("rank")));
            }
//            for (Integer recharge : recharges) {
//                ret.addItem(new MapleShopItem((short) 1, recharge.intValue(), 0, 0, 0, 0, 0, 0));
//            }
            rs.close();
            ps.close();

            ps = con.prepareStatement("SELECT * FROM shopranks WHERE shopid = ? ORDER BY rank ASC");
            ps.setInt(1, shopId);
            rs = ps.executeQuery();
            while (rs.next()) {
                if (!ii.itemExists(rs.getInt("itemid"))) {
                    continue;
                }
                ret.ranks.add(new Pair<>(rs.getInt("itemid"), rs.getString("name")));
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Could not load shop");
        }
        return ret;
    }

    public int getNpcId() {
        return this.npcId;
    }

    public int getId() {
        return this.id;
    }

    public List<Pair<Integer, String>> getRanks() {
        return this.ranks;
    }
}