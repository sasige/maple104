package tools.packet;

import client.BuddylistEntry;
import handling.SendPacketOpcode;
import java.util.Collection;
import org.apache.log4j.Logger;
import server.ServerProperties;
import tools.data.MaplePacketLittleEndianWriter;

public class BuddyListPacket {

    private static final Logger log = Logger.getLogger(BuddyListPacket.class);

    public static byte[] buddylistMessage(byte message) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.BUDDYLIST.getValue());
        mplew.write(message);

        return mplew.getPacket();
    }

    public static byte[] updateBuddylist(Collection<BuddylistEntry> buddylist) {
        return updateBuddylist(buddylist, 7);
    }

    public static byte[] updateBuddylist(Collection<BuddylistEntry> buddylist, int deleted) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.BUDDYLIST.getValue());
        mplew.write(deleted);
        mplew.write(buddylist.size());
        for (BuddylistEntry buddy : buddylist) {
            mplew.writeInt(buddy.getCharacterId());
            mplew.writeAsciiString(buddy.getName(), 13);
            mplew.write(buddy.isVisible() ? 0 : 1);
            mplew.writeInt(buddy.getChannel() == -1 ? -1 : buddy.getChannel() - 1);
            mplew.writeAsciiString(buddy.getGroup(), 17);
        }
        for (int x = 0; x < buddylist.size(); x++) {
            mplew.writeInt(0);
        }
        return mplew.getPacket();
    }

    public static byte[] requestBuddylistAdd(int cidFrom, String nameFrom, int levelFrom, int jobFrom) {//
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.BUDDYLIST.getValue());
        mplew.write(9);
        mplew.writeInt(cidFrom);
        mplew.writeMapleAsciiString(nameFrom);
        mplew.writeInt(levelFrom);
        mplew.writeInt(jobFrom);
        mplew.writeInt(0);//104
        mplew.writeInt(cidFrom);
        mplew.writeAsciiString(nameFrom, 13);
        mplew.write(1);
        mplew.writeInt(5);
        mplew.writeAsciiString("未指定群组", 17);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] updateBuddyChannel(int characterid, int channel) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.BUDDYLIST.getValue());
        mplew.write(20);
        mplew.writeInt(characterid);
        mplew.write(0);
        mplew.writeInt(channel);

        return mplew.getPacket();
    }

    public static byte[] updateBuddyCapacity(int capacity) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.BUDDYLIST.getValue());
        mplew.write(21);
        mplew.write(capacity);

        return mplew.getPacket();
    }
}