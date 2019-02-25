package handling.login.handler;

import client.MapleClient;
import constants.ServerConstants;
import tools.data.LittleEndianAccessor;
import tools.packet.LoginPacket;

public class MapLoginHandler {

    public static void handlePacket(LittleEndianAccessor slea, MapleClient c) {
        byte mapleType = slea.readByte();
        short mapleVersion = slea.readShort();
        String maplePatch = String.valueOf(slea.readShort());
        if ((mapleType != ServerConstants.MAPLE_TYPE) || (mapleVersion != ServerConstants.MAPLE_VERSION) || (!maplePatch.equals(ServerConstants.MAPLE_PATCH))) {
            c.getSession().close();
        } else {
            c.getSession().write(LoginPacket.getLoginAUTH());
        }
    }
}
