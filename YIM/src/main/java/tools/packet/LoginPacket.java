package tools.packet;

import client.MapleCharacter;
import client.MapleClient;
import constants.GameConstants;
import constants.ServerConstants;
import handling.SendPacketOpcode;
import handling.login.LoginServer;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import server.Randomizer;
import server.ServerProperties;
import tools.DateUtil;
import tools.HexTool;
import tools.MaplePacketCreator;
import tools.data.MaplePacketLittleEndianWriter;

public class LoginPacket {

    private static final Logger log = Logger.getLogger(LoginPacket.class);

    public static byte[] getHello(short mapleVersion, byte[] sendIv, byte[] recvIv, boolean testServer) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(15 + ServerConstants.MAPLE_PATCH.length());

        mplew.writeShort(13 + ServerConstants.MAPLE_PATCH.length());
        byte[] 假ivRecv = {70, 114, 122, 82};
        byte[] 假ivSend = {82, 48, 120, 115};
        假ivRecv[3] = (byte) (int) (Math.random() * 255.0D);
        假ivSend[3] = (byte) (int) (Math.random() * 255.0D);
        byte[] 假ivRecv1 = {70, 114, 122, 82};
        byte[] 假ivSend1 = {82, 48, 120, 115};
        假ivRecv1[3] = (byte) (int) (Math.random() * 255.0D);
        假ivSend1[3] = (byte) (int) (Math.random() * 255.0D);
        byte[] 假ivRecv2 = {70, 114, 122, 82};
        byte[] 假ivSend2 = {82, 48, 120, 115};
        假ivRecv2[3] = (byte) (int) (Math.random() * 255.0D);
        假ivSend2[3] = (byte) (int) (Math.random() * 255.0D);
        mplew.writeShort(mapleVersion);
        mplew.writeMapleAsciiString(ServerConstants.MAPLE_PATCH);
        mplew.writeMapleAsciiString("http://mxd.sdo.com/");
        mplew.write(假ivRecv);
        mplew.write(假ivSend1);
        mplew.write(recvIv);
        mplew.write(假ivSend);
        mplew.write(recvIv);
        mplew.write(假ivSend2);
        mplew.write(假ivRecv1);
        mplew.write(sendIv);
        mplew.write(假ivRecv2);
        mplew.write(假ivSend);
        mplew.write(recvIv);
        mplew.write(sendIv);
        mplew.write(假ivRecv1);
        mplew.write(假ivSend2);
        mplew.write(7);

        return mplew.getPacket();
    }

    public static byte[] getHello(short mapleVersion, byte[] sendIv, byte[] recvIv) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(15 + ServerConstants.MAPLE_PATCH.length());

        mplew.writeShort(13 + ServerConstants.MAPLE_PATCH.length());
        mplew.writeShort(mapleVersion);
        mplew.writeMapleAsciiString(ServerConstants.MAPLE_PATCH);
        mplew.write(recvIv);
        mplew.write(sendIv);
        mplew.write(ServerConstants.MAPLE_TYPE);

        return mplew.getPacket();
    }

    public static byte[] getPing() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(2);

        mplew.writeShort(SendPacketOpcode.PING.getValue());

        return mplew.getPacket();
    }

    public static byte[] getLoginAUTH() {//新连接
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(13);

        mplew.writeShort(SendPacketOpcode.LOGIN_AUTH.getValue());
        int rand = Randomizer.nextInt(3);
        mplew.writeMapleAsciiString(new StringBuilder().append("MapLogin").append(rand == 0 ? "" : rand).toString());
        mplew.writeInt(DateUtil.getTime());
      //  mplew.write(LoginServer.is开启幻影() ? 1 : 0);//104注释
        return mplew.getPacket();
    }

    public static byte[] StrangeDATA() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.RSA_KEY.getValue());

        mplew.writeMapleAsciiString("30819F300D06092A864886F70D010101050003818D0030818902818100994F4E66B003A7843C944E67BE4375203DAA203C676908E59839C9BADE95F53E848AAFE61DB9C09E80F48675CA2696F4E897B7F18CCB6398D221C4EC5823D11CA1FB9764A78F84711B8B6FCA9F01B171A51EC66C02CDA9308887CEE8E59C4FF0B146BF71F697EB11EDCEBFCE02FB0101A7076A3FEB64F6F6022C8417EB6B87270203010001");

        return mplew.getPacket();
    }

    public static byte[] getCustomEncryption() {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(26);

        mplew.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
        mplew.writeLong(611816276193195499L);
        mplew.writeLong(1877319832L);
        mplew.writeLong(202227478981090217L);
        return mplew.getPacket();
    }

    public static byte[] licenseResult() {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.LICENSE_RESULT.getValue());
        mplew.write(1);

        return mplew.getPacket();
    }

    public static byte[] genderNeeded(MapleClient c) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);

        mplew.writeShort(SendPacketOpcode.CHOOSE_GENDER.getValue());
        mplew.writeMapleAsciiString(c.getAccountName());

        return mplew.getPacket();
    }

    public static byte[] genderChanged(MapleClient c) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);

        mplew.writeShort(SendPacketOpcode.GENDER_SET.getValue());
        mplew.write(0);
        mplew.writeMapleAsciiString(c.getAccountName());
        mplew.writeMapleAsciiString(String.valueOf(c.getAccID()));

        return mplew.getPacket();
    }

    public static byte[] getLoginFailed(int reason) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);

        mplew.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
        mplew.write(reason);
        if (reason == 84) {
            mplew.writeLong(PacketHelper.getTime(-2));
        } else if (reason == 7) {
            mplew.writeZeroBytes(5);
        }
        return mplew.getPacket();
    }

    public static byte[] getPermBan(byte reason) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);

        mplew.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
        mplew.writeShort(2);
        mplew.write(0);
        mplew.writeShort(reason);
        mplew.write(HexTool.getByteArrayFromHexString("01 01 01 01 00"));

        return mplew.getPacket();
    }

    public static byte[] getTempBan(long timestampTill, byte reason) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(17);

        mplew.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
        mplew.writeShort(2);
        mplew.writeInt(0);
        mplew.write(reason);
        mplew.writeLong(timestampTill);

        return mplew.getPacket();
    }

    public static byte[] getAuthSuccessRequest(MapleClient client) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
        mplew.write(0);
        mplew.writeInt(client.getAccID());
        mplew.write(client.getGender());
        mplew.write(client.isGm() ? 1 : 0);
        mplew.write(client.isGm() ? 1 : 0);
        mplew.writeMapleAsciiString(client.getAccountName());
        mplew.writeZeroBytes(6);
        //mplew.writeShort(3);
        mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
        mplew.writeZeroBytes(9);
        mplew.writeLong(Randomizer.nextLong());
        mplew.writeZeroBytes(3);
        mplew.writeMapleAsciiString(String.valueOf(client.getAccID()));
        mplew.writeMapleAsciiString(client.getAccountName());
        mplew.write(1);
        mplew.writeShort(1);
        mplew.write(1);
        return mplew.getPacket();
    }

    public static byte[] deleteCharResponse(int cid, int state) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.DELETE_CHAR_RESPONSE.getValue());
        mplew.writeInt(cid);
        mplew.write(state);

        return mplew.getPacket();
    }

    public static byte[] secondPwError(byte mode) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);

        mplew.writeShort(SendPacketOpcode.SECONDPW_ERROR.getValue());
        mplew.write(mode);

        return mplew.getPacket();
    }

    public static byte[] enableRecommended() {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.ENABLE_RECOMMENDED.getValue());
        mplew.write(0);
        return mplew.getPacket();
    }

    public static byte[] sendRecommended(int world, String message) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SEND_RECOMMENDED.getValue());
        mplew.write((message != null) && (GameConstants.GMS) ? 1 : 0);
        if ((message != null) && (GameConstants.GMS)) {
            mplew.writeInt(world);
            mplew.writeMapleAsciiString(message);
        }
        return mplew.getPacket();
    }
//服务器公告
    public static byte[] getServerList(int serverId, Map<Integer, Integer> channelLoad) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SERVERLIST.getValue());
        mplew.writeShort(serverId);
        String worldName = LoginServer.getTrueServerName();
        mplew.writeMapleAsciiString(worldName);
        mplew.write(LoginServer.getFlag());
        mplew.writeMapleAsciiString(LoginServer.getEventMessage());
        mplew.writeShort(100);
        mplew.writeShort(100);
        int lastChannel = 1;
        Set channels = channelLoad.keySet();
        for (int i = 30; i > 0; i--) {
            if (channels.contains(i)) {
                lastChannel = i;
                break;
            }
        }
        mplew.write(lastChannel);
        mplew.writeInt(400);

        for (int i = 1; i <= lastChannel; i++) {
            int load;
            if (channels.contains(i)) {
                load = channelLoad.get(i);
            } else {
                load = 1200;
            }
            mplew.writeMapleAsciiString(new StringBuilder().append(worldName).append("-").append(i).toString());
            mplew.writeInt(load);
            mplew.write(serverId);
            mplew.writeShort(i - 1);
        }
        mplew.writeShort(0);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static byte[] getEndOfServerList() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SERVERLIST.getValue());
        mplew.write(255);
        mplew.write(255);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] getLoginWelcome() {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        return MaplePacketCreator.spawnFlags(null);
    }

    public static byte[] getServerStatus(int status) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SERVERSTATUS.getValue());
        mplew.write(status);

        return mplew.getPacket();
    }

    public static byte[] EventCheck() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.EVENT_CHECK.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("00 05 00 00 10 40 00 46 E5 58 00 57 F5 98 00 04 00 00 00 5F F5 98 00 04 00 00 00 6C F5 98 00 94 CA 07 00 D0 C3 A0 00 1C 16 01 00"));
        return mplew.getPacket();
    }

    public static byte[] getChannelSelected() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CHANNEL_SELECTED.getValue());
        mplew.writeInt(3);

        return mplew.getPacket();
    }

    public static byte[] getCharList(String secondpw, List<MapleCharacter> chars, int charslots) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CHARLIST.getValue());
        //mplew.writeInt(0);
        mplew.write(0);
        mplew.write(chars.size());
        for (MapleCharacter chr : chars) {
            addCharEntry(mplew, chr, true, false);
            mplew.write(0);//104
        }
        mplew.writeShort(3);
        mplew.writeLong(charslots);
        mplew.write(HexTool.getByteArrayFromHexString("53 80 CD 01 90 A6 7D 1C"));//这是倒装时间封包 其实倒过来了 90 A6 7D 1C 53 80 CD 01  盛大应该特地把他倒过来吧
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] addNewCharEntry(MapleCharacter chr, boolean worked) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ADD_NEW_CHAR_ENTRY.getValue());
        mplew.write(worked ? 0 : 1);
        addCharEntry(mplew, chr, false, false);

        return mplew.getPacket();
    }

    public static byte[] charNameResponse(String charname, boolean nameUsed) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CHAR_NAME_RESPONSE.getValue());
        mplew.writeMapleAsciiString(charname);
        mplew.write(nameUsed ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] charNameResponse(String charname, byte type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CHAR_NAME_RESPONSE.getValue());
        mplew.writeMapleAsciiString(charname);
        mplew.write(type);

        return mplew.getPacket();
    }

    private static void addCharEntry(MaplePacketLittleEndianWriter mplew, MapleCharacter chr, boolean ranking, boolean viewAll) {
        PacketHelper.addCharStats(mplew, chr);
        PacketHelper.addCharLook(mplew, chr, true, viewAll);
//        if(ranking == true){
//            mplew.write(0);
//        }
        //mplew.write(ranking ? 1 : 0);
//        if (ranking) {
//            mplew.writeInt(chr.getRank());
//            mplew.writeInt(chr.getRankMove());
//            mplew.writeInt(chr.getJobRank());
//            mplew.writeInt(chr.getJobRankMove());
//        }
    }

    public static byte[] showAllCharacter(int chars) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.ALL_CHARLIST.getValue());
        mplew.write(1);
        mplew.writeInt(chars);
        mplew.writeInt(chars + (3 - chars % 3));
        return mplew.getPacket();
    }

    public static byte[] showAllCharacterInfo(int worldid, List<MapleCharacter> chars, String pic) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.ALL_CHARLIST.getValue());
        mplew.write(chars.isEmpty() ? 5 : 0);
        mplew.write(worldid);
        mplew.write(chars.size());
        for (MapleCharacter chr : chars) {
            addCharEntry(mplew, chr, true, true);
        }
        mplew.write(pic.equals("") ? 2 : pic == null ? 0 : 1);
        return mplew.getPacket();
    }
}