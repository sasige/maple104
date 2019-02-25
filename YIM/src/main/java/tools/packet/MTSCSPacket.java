package tools.packet;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import handling.SendPacketOpcode;
import handling.channel.ChannelServer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import server.CashItemFactory;
import server.CashItemInfo.CashModInfo;
import server.CashShop;
import server.MTSStorage.MTSItemInfo;
import server.MapleStorage;
import tools.HexTool;
import tools.Pair;
import tools.data.MaplePacketLittleEndianWriter;

public class MTSCSPacket {

    private static byte[] warpCS = HexTool.getByteArrayFromHexString("00 00 00 02 00 00 00 30 00 00 00 90 00 06 00 A6 00 08 07 A0 EB 60 06 20 26CE 05 04 00 02 00 A4 00 0A 07 90 35 13 10 98 B5 12 10 78 00 4C 00 65 00 76 00 65 00 6C 00 00 00 00 00 02 00 06 00 98 01 08 07 02 00 00 00 30 00 00 00 02 00 08 00 9E 01 08 07 02 00 00 00 31 00 00 00 05 00 0A 00 9C 00 08 07 A0 01 15 00 40 5A 11 10 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");

    public static byte[] warpchartoCS(MapleClient c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_CHAR.getValue());
        PacketHelper.addCharacterInfo(mplew, c.getPlayer());
        mplew.writeInt(0);//104
        mplew.write(HexTool.getByteArrayFromHexString("DC E2 77 02 0D 4D 76 00"));//104

        mplew.writeLong(4);//长度
        for (int x = 0; x < 4; x++) {//104
            mplew.writeLong(9410165 + x);
        }

        return mplew.getPacket();
    }

    public static byte[] warpCS(MapleClient c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPEN.getValue());
        MapleCharacter chr = c.getPlayer();
        mplew.writeMapleAsciiString(chr.getClient().getAccountName());
        mplew.write(HexTool.getByteArrayFromHexString(c.getChannelServer().getShopPack()));
        return mplew.getPacket();
    }

    public static byte[] playCashSong(int itemid, String name) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CASH_SONG.getValue());
        mplew.writeInt(itemid);
        mplew.writeMapleAsciiString(name);
        return mplew.getPacket();
    }

    public static byte[] useCharm(byte charmsleft, byte daysleft) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
        mplew.write(8);
        mplew.write(1);
        mplew.write(charmsleft);
        mplew.write(daysleft);

        return mplew.getPacket();
    }

    public static byte[] useWheel(byte charmsleft) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
        mplew.write(23);
        mplew.writeLong(charmsleft);

        return mplew.getPacket();
    }

    public static byte[] itemExpired(int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(2);
        mplew.writeInt(itemid);

        return mplew.getPacket();
    }

    public static byte[] useAlienSocket(boolean start) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ALIEN_SOCKET_CREATOR.getValue());
        mplew.write(start ? 0 : 2);

        return mplew.getPacket();
    }

    public static byte[] ViciousHammer(boolean start, int hammered) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.VICIOUS_HAMMER.getValue());

        mplew.write(start ? 59 : 67);
        mplew.writeInt(0);
        if (start) {
            mplew.writeInt(hammered);
        }
        return mplew.getPacket();
    }

    public static byte[] changePetFlag(int uniqueId, boolean added, int flagAdded) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PET_FLAG_CHANGE.getValue());

        mplew.writeLong(uniqueId);
        mplew.write(added ? 1 : 0);
        mplew.writeShort(flagAdded);

        return mplew.getPacket();
    }

    public static byte[] changePetName(MapleCharacter chr, String newname, int slot) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PET_NAMECHANGE.getValue());

        mplew.writeInt(chr.getId());
        mplew.write(0);
        mplew.writeMapleAsciiString(newname);
        mplew.writeInt(slot);

        return mplew.getPacket();
    }

    public static byte[] showNotes(ResultSet notes, int count) throws SQLException {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_NOTES.getValue());
        mplew.write(3);
        mplew.write(count);
        for (int i = 0; i < count; i++) {
            mplew.writeInt(notes.getInt("id"));
            mplew.writeMapleAsciiString(notes.getString("from"));
            mplew.writeMapleAsciiString(notes.getString("message"));
            mplew.writeLong(PacketHelper.getKoreanTimestamp(notes.getLong("timestamp")));
            mplew.write(notes.getInt("gift"));
            notes.next();
        }

        return mplew.getPacket();
    }

    public static byte[] useChalkboard(int charid, String msg) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CHALKBOARD.getValue());

        mplew.writeInt(charid);
        if ((msg == null) || (msg.length() <= 0)) {
            mplew.write(0);
        } else {
            mplew.write(1);
            mplew.writeMapleAsciiString(msg);
        }

        return mplew.getPacket();
    }

    public static byte[] getTrockRefresh(MapleCharacter chr, byte vip, boolean delete) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.TROCK_LOCATIONS.getValue());
        mplew.write(delete ? 2 : 3);
        mplew.write(vip);
        if (vip == 1) {
            int[] map = chr.getRegRocks();
            for (int i = 0; i < 5; i++) {
                mplew.writeInt(map[i]);
            }
        } else if (vip == 2) {
            int[] map = chr.getRocks();
            for (int i = 0; i < 10; i++) {
                mplew.writeInt(map[i]);
            }
        } else if (vip == 3) {
            int[] map = chr.getHyperRocks();
            for (int i = 0; i < 13; i++) {
                mplew.writeInt(map[i]);
            }
        }
        return mplew.getPacket();
    }

    public static byte[] getTrockMessage(byte op) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.TROCK_LOCATIONS.getValue());

        mplew.writeShort(op);
        return mplew.getPacket();
    }

    public static void addModCashItemInfo(MaplePacketLittleEndianWriter mplew, CashModInfo item) {
        int flags = item.flags;
        mplew.writeInt(item.sn);
        mplew.writeInt(flags);
        if ((flags & 0x1) != 0) {
            mplew.writeInt(item.itemid);
        }
        if ((flags & 0x2) != 0) {
            mplew.writeShort(item.count);
        }
        if ((flags & 0x4) != 0) {
            mplew.writeInt(item.discountPrice);
        }
        if ((flags & 0x8) != 0) {
            mplew.write(item.unk_1 - 1);
        }
        if ((flags & 0x10) != 0) {
            mplew.write(item.priority);
        }
        if ((flags & 0x20) != 0) {
            mplew.writeShort(item.period);
        }
        if ((flags & 0x40) != 0) {
            mplew.writeInt(0);
        }
        if ((flags & 0x80) != 0) {
            mplew.writeInt(item.meso);
        }
        if ((flags & 0x100) != 0) {
            mplew.write(item.unk_2 - 1);
        }
        if ((flags & 0x200) != 0) {
            mplew.write(item.gender);
        }
        if ((flags & 0x400) != 0) {
            mplew.write(item.showUp ? 1 : 0);
        }
        if ((flags & 0x800) != 0) {
            mplew.write(item.mark);
        }
        if ((flags & 0x1000) != 0) {
            mplew.write(item.unk_3 - 1);
        }
        if ((flags & 0x2000) != 0) {
            mplew.writeShort(0);
        }
        if ((flags & 0x4000) != 0) {
            mplew.writeShort(0);
        }
        if ((flags & 0x8000) != 0) {
            mplew.writeShort(0);
        }
        if ((flags & 0x10000) != 0) {
            mplew.writeShort(0);
        }
        if ((flags & 0x20000) != 0) {
            mplew.writeShort(0);
        }
        if ((flags & 0x40000) != 0) {
            List pack = CashItemFactory.getInstance().getPackageItems(item.sn);
            if (pack == null) {
                mplew.write(0);
            } else {
                mplew.write(pack.size());
                for (int i = 0; i < pack.size(); i++) {
                    mplew.writeInt(((Integer) pack.get(i)).intValue());
                }
            }
        }
        if ((flags & 0x80000) != 0) {
            mplew.writeInt(0);
        }
        if ((flags & 0x100000) != 0) {
            mplew.writeInt(0);
        }
    }

    public static byte[] enableCSUse() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_USE.getValue());

        mplew.write(1);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] 刷新点卷信息(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_UPDATE.getValue());
        mplew.writeInt(chr.getCSPoints(1));
        mplew.writeInt(chr.getCSPoints(2));

        return mplew.getPacket();
    }

    public static byte[] 商城道具栏信息(MapleClient c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());

        mplew.write(85);
        CashShop mci = c.getPlayer().getCashInventory();
        int size = 0;
        mplew.writeShort(mci.getItemsSize());
        for (Item itemz : mci.getInventory()) {
            addCashItemInfo(mplew, itemz, c.getAccID(), 0);
            if (GameConstants.isPet(itemz.getItemId())) {
                size++;
            }
        }
        mplew.writeInt(size);
        if (mci.getInventory().size() > 0) {
            for (Item itemz : mci.getInventory()) {
                if (GameConstants.isPet(itemz.getItemId())) {
                    PacketHelper.addItemInfo(mplew, itemz, true, true);
                }
            }
        }
        mplew.writeShort(c.getPlayer().getStorage().getSlots());
        mplew.writeInt(c.getCharacterSlots());
        mplew.writeShort(3);

        return mplew.getPacket();
    }

    public static byte[] 商城礼物信息(MapleClient c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());

        mplew.write(88);
        List<Pair<Item, String>> mci = c.getPlayer().getCashInventory().loadGifts();
        mplew.writeShort(mci.size());
        for (Pair<Item, String> mcz : mci) {
            mplew.writeLong(((Item) mcz.getLeft()).getUniqueId());
            mplew.writeInt(((Item) mcz.getLeft()).getItemId());
            mplew.writeAsciiString(((Item) mcz.getLeft()).getGiftFrom(), 13);
            mplew.writeAsciiString((String) mcz.getRight(), 73);
        }

        return mplew.getPacket();
    }

    public static byte[] 商城购物车(MapleCharacter chr, boolean update) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());

        mplew.write(update ? 94 : 90);
        int[] list = chr.getWishlist();
        for (int i = 0; i < 10; i++) {
            mplew.writeInt(list[i] != -1 ? list[i] : 0);
        }
        return mplew.getPacket();
    }

    public static byte[] 购买商城道具(Item item, int sn, int accid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());

        mplew.write(96);
        addCashItemInfo(mplew, item, accid, sn);

        return mplew.getPacket();
    }

    public static byte[] 商城购买礼包(Map<Integer, Item> ccc, int accid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());

        mplew.write(150);
        mplew.write(ccc.size());
        int size = 0;
        for (Map.Entry sn : ccc.entrySet()) {
            addCashItemInfo(mplew, (Item) sn.getValue(), accid, ((Integer) sn.getKey()).intValue());
            if ((GameConstants.isPet(((Item) sn.getValue()).getItemId())) || (GameConstants.getInventoryType(((Item) sn.getValue()).getItemId()) == MapleInventoryType.EQUIP)) {
                size++;
            }
        }
        mplew.writeInt(size);
        if (ccc.size() > 0) {
            for (Item itemz : ccc.values()) {
                if ((GameConstants.isPet(itemz.getItemId())) || (GameConstants.getInventoryType(itemz.getItemId()) == MapleInventoryType.EQUIP)) {
                    PacketHelper.addItemInfo(mplew, itemz, true, true);
                }
            }
        }
        mplew.writeShort(0);

        return mplew.getPacket();
    }

    public static byte[] 商城送礼(int itemid, int quantity, String receiver) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());

        mplew.write(103);
        mplew.writeMapleAsciiString(receiver);
        mplew.writeInt(itemid);
        mplew.writeShort(quantity);

        return mplew.getPacket();
    }

    public static byte[] 商城送礼包(int price, int itemid, int quantity, String receiver) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(101);
        mplew.writeMapleAsciiString(receiver);
        mplew.writeInt(itemid);
        mplew.writeShort(quantity);
        mplew.writeShort(0);
        mplew.writeInt(price);

        return mplew.getPacket();
    }

    public static byte[] 扩充道具栏(int inv, int slots) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());

        mplew.write(105);
        mplew.write(inv);
        mplew.writeShort(slots);

        return mplew.getPacket();
    }

    public static byte[] 扩充仓库(int slots) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(106);
        mplew.writeShort(slots);

        return mplew.getPacket();
    }

    public static byte[] 购买角色卡(int slots) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());

        mplew.write(109);
        mplew.writeShort(slots);

        return mplew.getPacket();
    }

    public static byte[] 商城到背包(Item item) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());

        mplew.write(115);
        mplew.writeShort(item.getPosition());
        PacketHelper.addItemInfo(mplew, item, true, true);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] 背包到商城(Item item, int accId, int sn) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());

        mplew.write(117);
        addCashItemInfo(mplew, item, accId, sn);

        return mplew.getPacket();
    }

    public static byte[] 商城换购道具(int uniqueId, int Money) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());

        mplew.write(146);
        mplew.writeLong(uniqueId);
        mplew.writeLong(Money);

        return mplew.getPacket();
    }

    public static byte[] cashItemExpired(int uniqueid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());

        mplew.write(121);
        mplew.writeLong(uniqueid);
        return mplew.getPacket();
    }

    public static byte[] 商城购买任务道具(int price, short quantity, byte position, int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());

        mplew.write(154);
        mplew.writeInt(price);
        mplew.writeShort(quantity);
        mplew.writeShort(position);
        mplew.writeInt(itemid);

        return mplew.getPacket();
    }

    public static void addCashItemInfo(MaplePacketLittleEndianWriter mplew, Item item, int accId, int sn) {
        mplew.writeLong(item.getUniqueId() > 0 ? item.getUniqueId() : 0L);
        mplew.writeLong(accId);
        mplew.writeInt(item.getItemId());
        mplew.writeInt(sn);
        mplew.writeShort(item.getQuantity());
        mplew.writeAsciiString(item.getGiftFrom(), 13);
        PacketHelper.addExpirationTime(mplew, item.getExpiration());
        mplew.writeLong(item.getExpiration() == -1L ? 30L : 0L);
        mplew.writeZeroBytes(10);
        mplew.writeZeroBytes(20);
        PacketHelper.addExpirationTime(mplew, item.getExpiration());
        mplew.writeZeroBytes(16);
    }

    public static byte[] 商城错误提示(int err) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(153);
        mplew.write(err);

        return mplew.getPacket();
    }

    public static byte[] showCouponRedeemedItem(int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.writeShort(105);
        mplew.writeInt(0);
        mplew.writeInt(1);
        mplew.writeShort(1);
        mplew.writeShort(26);
        mplew.writeInt(itemid);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] showCouponRedeemedItem(Map<Integer, Item> items, int mesos, int maplePoints, MapleClient c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(105);
        mplew.write(items.size());
        for (Map.Entry item : items.entrySet()) {
            addCashItemInfo(mplew, (Item) item.getValue(), c.getAccID(), ((Integer) item.getKey()).intValue());
        }
        mplew.writeLong(maplePoints);
        mplew.writeInt(mesos);

        return mplew.getPacket();
    }

    public static byte[] redeemResponse() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());

        mplew.write(176);
        mplew.writeInt(0);
        mplew.write(1);

        return mplew.getPacket();
    }

    public static byte[] 商城提示(int 消费, int 达到, int mode) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_MSG.getValue());

        mplew.write(195);
        mplew.writeInt(消费);
        mplew.writeInt(达到);
        mplew.write(mode);

        return mplew.getPacket();
    }

    public static byte[] 商城未知封包1() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_UNK1.getValue());
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] 热点推荐(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_HOT.getValue());
        int[] hotSn = {20000521, 20100085, 20300284, 20800173};
        mplew.writeInt(hotSn.length);
        for (int i = 0; i < hotSn.length; i++) {
            mplew.writeInt(hotSn[i]);
        }

        return mplew.getPacket();
    }

    public static byte[] 每日特卖() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_DAILY.getValue());
        mplew.writeInt(getTime());
        mplew.writeLong(0L);

        return mplew.getPacket();
    }

    public static int getTime() {
        String time = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).replace("-", "");
        return Integer.valueOf(time).intValue();
    }

    public static byte[] 商城未知封包2() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_UNK2.getValue());
        mplew.writeInt(0);
        mplew.writeInt(10300009);
        mplew.writeInt(50);
        mplew.writeInt(5560);
        mplew.writeInt(0);
        mplew.writeInt(20120615);
        mplew.writeInt(20120624);

        return mplew.getPacket();
    }

    public static byte[] showXmasSurprise(int idFirst, Item item, int accid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.XMAS_SURPRISE.getValue());
        mplew.write(230);
        mplew.writeLong(idFirst);
        mplew.writeInt(0);
        addCashItemInfo(mplew, item, accid, 0);
        mplew.writeInt(item.getItemId());
        mplew.write(1);
        mplew.write(1);

        return mplew.getPacket();
    }

    public static byte[] getBoosterFamiliar(int cid, int familiar, int id) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.BOOSTER_FAMILIAR.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(familiar);
        mplew.writeLong(id);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] getBoosterPack(int f1, int f2, int f3) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.BOOSTER_PACK.getValue());
        mplew.write(215);
        mplew.writeInt(f1);
        mplew.writeInt(f2);
        mplew.writeInt(f3);

        return mplew.getPacket();
    }

    public static byte[] getBoosterPackClick() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.BOOSTER_PACK.getValue());
        mplew.write(213);

        return mplew.getPacket();
    }

    public static byte[] getBoosterPackReveal() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.BOOSTER_PACK.getValue());
        mplew.write(214);

        return mplew.getPacket();
    }

    public static byte[] sendMesobagFailed() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MESOBAG_FAILURE.getValue());
        return mplew.getPacket();
    }

    public static byte[] sendMesobagSuccess(int mesos) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MESOBAG_SUCCESS.getValue());
        mplew.writeInt(mesos);
        return mplew.getPacket();
    }

    public static byte[] startMTS(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MTS_OPEN.getValue());

        PacketHelper.addCharacterInfo(mplew, chr);
        if (!GameConstants.GMS) {
            mplew.writeMapleAsciiString("T13333333337W");
        }
        mplew.writeInt(10000);
        mplew.writeInt(5);
        mplew.writeInt(0);
        mplew.writeInt(24);
        mplew.writeInt(168);
        mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
        return mplew.getPacket();
    }

    public static byte[] sendMTS(List<MTSItemInfo> items, int tab, int type, int page, int pages) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MTS_OPERATION.getValue());

        mplew.write(21);
        mplew.writeInt(pages);
        mplew.writeInt(items.size());
        mplew.writeInt(tab);
        mplew.writeInt(type);
        mplew.writeInt(page);
        mplew.write(1);
        mplew.write(1);

        for (MTSItemInfo item : items) {
            addMTSItemInfo(mplew, item);
        }
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] showMTSCash(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GET_MTS_TOKENS.getValue());
        mplew.writeInt(chr.getCSPoints(2));
        return mplew.getPacket();
    }

    public static byte[] getMTSWantedListingOver(int nx, int items) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MTS_OPERATION.getValue());
        mplew.write(61);
        mplew.writeInt(nx);
        mplew.writeInt(items);
        return mplew.getPacket();
    }

    public static byte[] getMTSConfirmSell() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MTS_OPERATION.getValue());
        mplew.write(29);
        return mplew.getPacket();
    }

    public static byte[] getMTSFailSell() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MTS_OPERATION.getValue());
        mplew.write(30);
        mplew.write(66);
        return mplew.getPacket();
    }

    public static byte[] getMTSConfirmBuy() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MTS_OPERATION.getValue());
        mplew.write(51);
        return mplew.getPacket();
    }

    public static byte[] getMTSFailBuy() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MTS_OPERATION.getValue());
        mplew.write(52);
        mplew.write(66);
        return mplew.getPacket();
    }

    public static byte[] getMTSConfirmCancel() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MTS_OPERATION.getValue());
        mplew.write(37);
        return mplew.getPacket();
    }

    public static byte[] getMTSFailCancel() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MTS_OPERATION.getValue());
        mplew.write(38);
        mplew.write(66);
        return mplew.getPacket();
    }

    public static byte[] getMTSConfirmTransfer(int quantity, int pos) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MTS_OPERATION.getValue());
        mplew.write(39);
        mplew.writeInt(quantity);
        mplew.writeInt(pos);
        return mplew.getPacket();
    }

    private static void addMTSItemInfo(MaplePacketLittleEndianWriter mplew, MTSItemInfo item) {
        PacketHelper.addItemInfo(mplew, item.getItem(), true, true);
        mplew.writeInt(item.getId());
        mplew.writeInt(item.getTaxes());
        mplew.writeInt(item.getPrice());
        mplew.writeZeroBytes(GameConstants.GMS ? 4 : 8);
        mplew.writeLong(PacketHelper.getTime(item.getEndingDate()));
        mplew.writeMapleAsciiString(item.getSeller());
        mplew.writeMapleAsciiString(item.getSeller());
        mplew.writeZeroBytes(28);
    }

    public static byte[] getNotYetSoldInv(List<MTSItemInfo> items) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MTS_OPERATION.getValue());
        mplew.write(35);

        mplew.writeInt(items.size());

        for (MTSItemInfo item : items) {
            addMTSItemInfo(mplew, item);
        }

        return mplew.getPacket();
    }

    public static byte[] getTransferInventory(List<Item> items, boolean changed) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MTS_OPERATION.getValue());
        mplew.write(33);

        mplew.writeInt(items.size());
        int i = 0;
        for (Item item : items) {
            PacketHelper.addItemInfo(mplew, item, true, true);
            mplew.writeInt(2147483647 - i);
            mplew.writeZeroBytes(GameConstants.GMS ? 52 : 56);
            i++;
        }
        mplew.writeInt(-47 + i - 1);
        mplew.write(changed ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] addToCartMessage(boolean fail, boolean remove) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MTS_OPERATION.getValue());
        if (remove) {
            if (fail) {
                mplew.write(44);
                mplew.writeInt(-1);
            } else {
                mplew.write(43);
            }
        } else if (fail) {
            mplew.write(42);
            mplew.writeInt(-1);
        } else {
            mplew.write(41);
        }

        return mplew.getPacket();
    }
}