package handling.login.handler;

import client.MapleClient;
import tools.data.LittleEndianAccessor;
import tools.packet.LoginPacket;

public class DeleteCharHandler {

    private static boolean loginFailCount(MapleClient c) {
        c.loginAttempt = (short) (c.loginAttempt + 1);

        return c.loginAttempt > 5;
    }

    public static void handlePacket(LittleEndianAccessor slea, MapleClient c) {
        if (!c.isGm()) {
            return;
        }
        String Secondpw_Client = null;
        if ((Secondpw_Client == null)
                && (slea.readByte() > 0)) {
            Secondpw_Client = slea.readMapleAsciiString();
        }

        int Character_ID = slea.readInt();
        if ((!c.login_Auth(Character_ID)) || (!c.isLoggedIn()) || (loginFailCount(c))) {
            c.getSession().close();
            return;
        }
        byte state = 0;
        if (c.getSecondPassword() != null) {
            if (Secondpw_Client == null) {
                c.getSession().close();
                return;
            }
            if (!c.CheckSecondPassword(Secondpw_Client)) {
                state = 12;
            }
        }

        if (state == 0) {
            state = (byte) c.deleteCharacter(Character_ID);
        }
        c.getSession().write(LoginPacket.deleteCharResponse(Character_ID, state));
    }
}
