package handling.login.handler;

import client.MapleClient;
import handling.SendPacketOpcode;
import tools.FileoutputUtil;
import tools.StringUtil;
import tools.data.LittleEndianAccessor;

public class PacketErrorHandler {

    public static void handlePacket(LittleEndianAccessor slea, MapleClient c) {
        if (slea.available() >= 6) {
            short badPacketSize = slea.readShort();
            slea.skip(4);
            int pHeader = slea.readShort();
            String pHeaderStr = Integer.toHexString(pHeader).toUpperCase();
            pHeaderStr = StringUtil.getLeftPaddedStr(pHeaderStr, '0', 4);
            String op = lookupRecv(pHeader);
            String from = "";
            if (c.getPlayer() != null) {
                from = "角色: " + c.getPlayer().getName() + " 等级(" + c.getPlayer().getLevel() + ") 职业: " + c.getPlayer().getJob() + " \r\n";
            }
            String Recv = "处理Recv封包出错: " + op + " [" + pHeaderStr + "] (" + (badPacketSize - 4) + ")" + slea.toString(true);
            FileoutputUtil.packetLog("log\\封包出错.log", from + Recv);
        }
    }

    private static String lookupRecv(int val) {
        for (SendPacketOpcode op : SendPacketOpcode.values()) {
            if (op.getValue() == val) {
                return op.name();
            }
        }
        return "UNKNOWN";
    }
}
