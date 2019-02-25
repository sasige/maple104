package handling.login.handler;

import client.MapleClient;

import tools.data.LittleEndianAccessor;
import tools.packet.LoginPacket;

public class LicenseRequestHandler {

    public static void handlePacket(LittleEndianAccessor slea, MapleClient c) {
        if (slea.readByte() == 1) {
            c.getSession().write(LoginPacket.licenseResult());
            c.updateLoginState(0);
        } else {
            c.getSession().close();
        }
    }
}