package tools.packet;

import client.MapleCharacter;
import client.inventory.Item;
import client.inventory.MapleAndroid;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import handling.SendPacketOpcode;
import java.awt.Point;
import java.util.List;
import server.movement.LifeMovementFragment;
import tools.HexTool;
import tools.data.MaplePacketLittleEndianWriter;

public class AndroidPacket {

    public static byte[] spawnAndroid(MapleCharacter cid, MapleAndroid android) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ANDROID_SPAWN.getValue());
        mplew.writeInt(cid.getId());

        mplew.write(android.getType());
        mplew.writePos(android.getPos());
        mplew.write(android.getStance());
        mplew.writeShort(0);
        mplew.writeShort(android.getSkin());
        mplew.writeShort(android.getHair() - 30000);
        mplew.writeShort(android.getFace() - 20000);
        mplew.writeMapleAsciiString(android.getName());
        for (short i = -1200; i > -1207; i = (short) (i - 1)) {
            Item item = cid.getInventory(MapleInventoryType.EQUIPPED).getItem(i);
            mplew.writeInt(item != null ? item.getItemId() : 0);
        }
        return mplew.getPacket();
    }

    public static byte[] moveAndroid(int cid, Point pos, List<LifeMovementFragment> res) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ANDROID_MOVE.getValue());
        mplew.writeInt(cid);
        mplew.writePos(pos);
        mplew.write(HexTool.getByteArrayFromHexString("C1 30 41 02"));
        PacketHelper.serializeMovementList(mplew, res);
        return mplew.getPacket();
    }

    public static byte[] showAndroidEmotion(int cid, int animation) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ANDROID_EMOTION.getValue());
        mplew.writeInt(cid);
        mplew.write(0);
        mplew.write(animation);
        return mplew.getPacket();
    }

    public static byte[] updateAndroidLook(int cid, int size, int itemId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ANDROID_UPDATE.getValue());
        mplew.writeInt(cid);
        switch (size) {
            case -1200:
                mplew.write(1);
                break;
            case -1201:
                mplew.write(2);
                break;
            case -1202:
                mplew.write(4);
                break;
            case -1203:
                mplew.write(8);
                break;
            case -1204:
                mplew.write(16);
                break;
            case -1205:
                mplew.write(32);
                break;
            case -1206:
                mplew.write(64);
        }

        mplew.writeInt(itemId);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static byte[] deactivateAndroid(int cid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ANDROID_DEACTIVATED.getValue());
        mplew.writeInt(cid);
        return mplew.getPacket();
    }

    public static byte[] removeAndroidHeart() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(19);
        return mplew.getPacket();
    }
}