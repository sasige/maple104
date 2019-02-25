package tools.packet;

import handling.SendPacketOpcode;
import org.apache.log4j.Logger;
import server.ServerProperties;
import tools.MaplePacketCreator;
import tools.data.MaplePacketLittleEndianWriter;

public class UIPacket {

    private static final Logger log = Logger.getLogger(UIPacket.class);

    public static byte[] EarnTitleMsg(String msg) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.EARN_TITLE_MSG.getValue());
        mplew.writeMapleAsciiString(msg);

        return mplew.getPacket();
    }

    public static byte[] getSPMsg(byte sp, short job) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(4);
        mplew.writeShort(job);
        mplew.write(sp);

        return mplew.getPacket();
    }

    public static byte[] getGPMsg(int itemid) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(7);
        mplew.writeInt(itemid);

        return mplew.getPacket();
    }

    public static byte[] getBPMsg(int amount) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(23);
        mplew.writeInt(amount);

        return mplew.getPacket();
    }

    public static byte[] getGPContribution(int itemid) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(8);
        mplew.writeInt(itemid);

        return mplew.getPacket();
    }

    public static byte[] getTopMsg(String msg) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.TOP_MSG.getValue());
        mplew.writeMapleAsciiString(msg);

        return mplew.getPacket();
    }

    public static byte[] getMidMsg(String msg, boolean keep, int index) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MID_MSG.getValue());
        mplew.write(index);
        mplew.writeMapleAsciiString(msg);
        mplew.write(keep ? 0 : 1);

        return mplew.getPacket();
    }

    public static byte[] clearMidMsg() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CLEAR_MID_MSG.getValue());

        return mplew.getPacket();
    }

    public static byte[] getStatusMsg(int itemid) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(9);
        mplew.writeInt(itemid);

        return mplew.getPacket();
    }

    public static byte[] MapEff(String path) {
        return MaplePacketCreator.environmentChange(path, 3);
    }

    public static byte[] MapNameDisplay(int mapid) {
        return MaplePacketCreator.environmentChange("maplemap/enter/" + mapid, 3);
    }

    public static byte[] Aran_Start() {
        return MaplePacketCreator.environmentChange("Aran/balloon", 4);
    }

    public static byte[] AranTutInstructionalBalloon(String data) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
        mplew.write(25);
        mplew.writeMapleAsciiString(data);
        mplew.writeInt(1);

        return mplew.getPacket();
    }

    public static byte[] ShowWZEffect(String data) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
        mplew.write(21);
        mplew.writeMapleAsciiString(data);

        return mplew.getPacket();
    }

    public static byte[] playMovie(String data, boolean show) {
        if (show) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PLAY_MOVIE.getValue());
        mplew.writeMapleAsciiString(data);
        mplew.write(show ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] summonHelper(boolean summon) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SUMMON_HINT.getValue());
        mplew.write(summon ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] summonMessage(int type) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SUMMON_HINT_MSG.getValue());
        mplew.write(1);
        mplew.writeInt(type);
        mplew.writeInt(7000);

        return mplew.getPacket();
    }

    public static byte[] summonMessage(String message) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SUMMON_HINT_MSG.getValue());
        mplew.write(0);
        mplew.writeMapleAsciiString(message);
        mplew.writeInt(200);
        mplew.writeInt(10000);

        return mplew.getPacket();
    }

    public static byte[] IntroLock(boolean enable) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CYGNUS_INTRO_LOCK.getValue());
        mplew.write(enable ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] getDirectionStatus(boolean enable) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.DIRECTION_STATUS.getValue());
        mplew.write(enable ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] getDirectionInfo(int type, int value) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.DIRECTION_INFO.getValue());
        mplew.write(type);
        mplew.writeInt(value);

        return mplew.getPacket();
    }

    public static byte[] getDirectionInfo(String data, int value, int x, int y, int pro) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.DIRECTION_INFO.getValue());
        mplew.write(2);
        mplew.writeMapleAsciiString(data);
        mplew.writeInt(value);
        mplew.writeInt(x);
        mplew.writeInt(y);
        mplew.writeShort(pro);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] IntroEnableUI(int wtf) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CYGNUS_INTRO_ENABLE_UI.getValue());
        mplew.write(wtf > 0 ? 1 : 0);
        if (wtf > 0) {
            mplew.writeShort(wtf);
        }

        return mplew.getPacket();
    }

    public static byte[] IntroDisableUI(boolean enable) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CYGNUS_INTRO_DISABLE_UI.getValue());
        mplew.write(enable ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] fishingUpdate(byte type, int id) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.FISHING_BOARD_UPDATE.getValue());
        mplew.write(type);
        mplew.writeInt(id);

        return mplew.getPacket();
    }

    public static byte[] fishingCaught(int chrid) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.FISHING_CAUGHT.getValue());
        mplew.writeInt(chrid);

        return mplew.getPacket();
    }
}