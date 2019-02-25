package handling.login.handler;

import client.MapleCharacterUtil;
import client.MapleClient;
import handling.login.LoginInformationProvider;
import tools.data.LittleEndianAccessor;
import tools.packet.LoginPacket;

public class CheckCharNameHandler {

    public static void handlePacket(LittleEndianAccessor slea, MapleClient c) {
        String name = slea.readMapleAsciiString();
        c.getSession().write(LoginPacket.charNameResponse(name, (!MapleCharacterUtil.canCreateChar(name, c.isGm())) || ((LoginInformationProvider.getInstance().isForbiddenName(name)) && (!c.isGm()))));
    }
}
