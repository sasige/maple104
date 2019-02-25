package handling.channel.handler;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Item;
import client.inventory.ItemLoader;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import database.DatabaseConnection;
import handling.world.World;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MerchItemPackage;
import server.shops.HiredMerchant;
import tools.FileoutputUtil;
import tools.MaplePacketCreator;
import tools.Pair;
import tools.data.LittleEndianAccessor;
import tools.packet.PlayerShopPacket;

public class HiredMerchantHandler {

    private static final Logger log = Logger.getLogger(HiredMerchantHandler.class);

    public static boolean UseHiredMerchant(MapleClient c, boolean packet) {
        MapleCharacter chr = c.getPlayer();
        if (c.getChannelServer().isShutdown()) {
            chr.dropMessage(1, "服务器即将关闭维护，暂时无法进行开店。");
            return false;
        }
        if ((chr.getMap() != null) && (chr.getMap().allowPersonalShop())) {
            HiredMerchant merchant = World.getMerchant(chr.getAccountID(), chr.getId());
            if (merchant != null) {
                c.getSession().write(PlayerShopPacket.sendTitleBox(merchant.getMapId(), merchant.getChannel() - 1));
            } else {
                try {
                    if ((ItemLoader.雇佣道具.loadItems(false, chr.getId()).isEmpty()) && (chr.getMerchantMeso() == 0)) {
                        if (packet) {
                            c.getSession().write(PlayerShopPacket.sendTitleBox());
                        }
                        return true;
                    }
                    c.getSession().write(PlayerShopPacket.retrieveFirstMessage());
                } catch (SQLException ex) {
                    return false;
                }
            }
        }
        return false;
    }

    private static int getMerchMesos(MapleCharacter chr) {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * from hiredmerch where characterid = ?");
            ps.setInt(1, chr.getId());
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                ps.close();
                rs.close();
                return 0;
            }
            int mesos = rs.getInt("Mesos");
            rs.close();
            ps.close();
            return mesos > 0 ? mesos : 0;
        } catch (SQLException se) {
        }
        return 0;
    }

    public static void MerchantItemStore(LittleEndianAccessor slea, MapleClient c) {
        MapleCharacter chr = c.getPlayer();
        if (chr == null) {
            return;
        }
        if (c.getChannelServer().isShutdown()) {
            chr.dropMessage(1, "服务器即将关闭维护，暂时无法进行道具取回。");
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        byte operation = slea.readByte();
        switch (operation) {
            case 21:
                HiredMerchant merchant = World.getMerchant(chr.getAccountID(), chr.getId());
                if (merchant != null) {
                    c.getSession().write(PlayerShopPacket.merchItemStore(merchant.getMapId(), merchant.getChannel() - 1));
                    chr.setConversation(0);
                } else {
                    MerchItemPackage pack = loadItemFrom_Database(chr);

                    if (pack == null) {
                        c.getSession().write(PlayerShopPacket.merchItemStore(999999999, 0));
                        chr.setConversation(0);
                    } else {
                        c.getSession().write(PlayerShopPacket.merchItemStore_ItemData(pack));
                    }
                }
                break;
            case 25:
                if (chr.getConversation() != 3) {
                    return;
                }
                c.getSession().write(PlayerShopPacket.merchItemStore((byte) 39));
                break;
            case 27:
                if (chr.getConversation() != 3) {
                    return;
                }
                boolean merch = World.hasMerchant(chr.getAccountID(), chr.getId());
                if (merch) {
                    chr.dropMessage(1, "请关闭现有的商店.");
                    chr.setConversation(0);
                    return;
                }
                MerchItemPackage pack = loadItemFrom_Database(chr);
                if (pack == null) {
                    chr.dropMessage(1, "发生了未知错误.");
                    return;
                }
                if (!check(chr, pack)) {
                    c.getSession().write(PlayerShopPacket.merchItem_Message((byte) 36));
                    return;
                }
                if (deletePackage(chr.getId())) {
                    if (pack.getMesos() > 0) {
                        chr.gainMeso(pack.getMesos(), false);
                        log.info("[雇佣] " + chr.getName() + " 雇佣取回获得金币: " + pack.getMesos() + " 时间: " + FileoutputUtil.CurrentReadable_Date());
                    }
                    for (Item item : pack.getItems()) {
                        MapleInventoryManipulator.addFromDrop(c, item, false);
                    }
                    c.getSession().write(PlayerShopPacket.merchItem_Message((byte) 32));
                } else {
                    chr.dropMessage(1, "发生了未知错误.");
                }
                break;
            case 29:
                chr.setConversation(0);
                break;
            case 22:
            case 23:
            case 24:
            case 26:
            case 28:
            default:
                System.out.println("弗洛兰德：未知的操作类型 " + operation);
        }
    }

    public static void RemoteStore(LittleEndianAccessor slea, MapleClient c) {
        MapleCharacter chr = c.getPlayer();
        HiredMerchant merchant = World.getMerchant(chr.getAccountID(), chr.getId());
        if (merchant != null) {
            if (merchant.getChannel() == chr.getClient().getChannel()) {
                merchant.setOpen(false);
                merchant.removeAllVisitors(16, 0);
                chr.setPlayerShop(merchant);
                c.getSession().write(PlayerShopPacket.getHiredMerch(chr, merchant, false));
            } else {
                c.getSession().write(PlayerShopPacket.remoteChannelChange(merchant.getChannel() - 1));
            }
        } else {
            chr.dropMessage(1, "你没有开设商店");
        }

        c.getSession().write(MaplePacketCreator.enableActions());
    }

    private static boolean check(MapleCharacter chr, MerchItemPackage pack) {
        if (chr.getMeso() + pack.getMesos() < 0) {
            return false;
        }
        byte eq = 0;
        byte use = 0;
        byte setup = 0;
        byte etc = 0;
        byte cash = 0;
        for (Item item : pack.getItems()) {
            MapleInventoryType invtype = GameConstants.getInventoryType(item.getItemId());
            if (invtype == MapleInventoryType.EQUIP) {
                eq = (byte) (eq + 1);
            } else if (invtype == MapleInventoryType.USE) {
                use = (byte) (use + 1);
            } else if (invtype == MapleInventoryType.SETUP) {
                setup = (byte) (setup + 1);
            } else if (invtype == MapleInventoryType.ETC) {
                etc = (byte) (etc + 1);
            } else if (invtype == MapleInventoryType.CASH) {
                cash = (byte) (cash + 1);
            }
            if ((MapleItemInformationProvider.getInstance().isPickupRestricted(item.getItemId())) && (chr.haveItem(item.getItemId(), 1))) {
                return false;
            }
        }

        return (chr.getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() >= eq) && (chr.getInventory(MapleInventoryType.USE).getNumFreeSlot() >= use) && (chr.getInventory(MapleInventoryType.SETUP).getNumFreeSlot() >= setup) && (chr.getInventory(MapleInventoryType.ETC).getNumFreeSlot() >= etc) && (chr.getInventory(MapleInventoryType.CASH).getNumFreeSlot() >= cash);
    }

    private static boolean deletePackage(int charId) {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("DELETE from hiredmerch where characterid = ?");
            ps.setInt(1, charId);
            ps.executeUpdate();
            ps.close();
            ItemLoader.雇佣道具.saveItems(null, charId);
            return true;
        } catch (SQLException e) {
            System.out.println("删除弗洛兰德道具信息出错" + e);
        }
        return false;
    }

    private static MerchItemPackage loadItemFrom_Database(MapleCharacter chr) {
        try {
            MerchItemPackage pack = new MerchItemPackage();
            pack.setMesos(chr.getMerchantMeso());
            Map<Long, Pair<Item, MapleInventoryType>> items = ItemLoader.雇佣道具.loadItems(false, chr.getId());
            if ((chr.getMerchantMeso() == 0) && (items.isEmpty())) {
                return null;
            }
            if (!items.isEmpty()) {
                List<Item> iters = new ArrayList<Item>();
                for (Pair<Item, MapleInventoryType> z : items.values()) {
                    iters.add(z.left);
                }
                pack.setItems(iters);
            }
            return pack;
        } catch (SQLException e) {
            System.out.println("加载弗洛兰德道具信息出错" + e);
        }
        return null;
    }
}