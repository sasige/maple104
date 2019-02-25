package handling.login.handler;

import client.MapleClient;
import constants.GameConstants;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import java.util.concurrent.ScheduledFuture;

import tools.MaplePacketCreator;
import tools.data.LittleEndianAccessor;
import tools.packet.LoginPacket;

public class WithoutSecondPasswordHandler {

    private static boolean loginFailCount(MapleClient c) {
        c.loginAttempt = (short) (c.loginAttempt + 1);

        return c.loginAttempt > 5;
    }

    public static void handlePacket(LittleEndianAccessor slea, MapleClient c, boolean haspic, boolean view) {
        slea.readByte();
        int charId = slea.readInt();
        if (view) {
            c.setChannel(1);
            c.setWorld(slea.readInt());
        }
        String currentpw = c.getSecondPassword();
        if ((!c.isLoggedIn()) || (loginFailCount(c)) || ((currentpw != null) && ((!currentpw.equals("")) || (haspic))) || (!c.login_Auth(charId)) || (ChannelServer.getInstance(c.getChannel()) == null) || (c.getWorld() != 0)) {
            c.getSession().close();
            return;
        }
        if (GameConstants.GMS) {
            c.updateMacs(slea.readMapleAsciiString());
            slea.readMapleAsciiString();
        }
        if (slea.available() != 0L) {
            String setpassword = slea.readMapleAsciiString();
            if ((setpassword.length() >= 6) && (setpassword.length() <= 16)) {
                c.setSecondPassword(setpassword);
                c.updateSecondPassword();
            } else {
                c.getSession().write(LoginPacket.secondPwError((byte) 20));
                return;
            }
        } else if ((GameConstants.GMS) && (haspic)) {
            return;
        }
        if (c.getIdleTask() != null) {
            c.getIdleTask().cancel(true);
        }
        String s = c.getSessionIPAddress();
        LoginServer.putLoginAuth(charId, s.substring(s.indexOf('/') + 1, s.length()), c.getTempIP());
        c.updateLoginState(1, s);
        c.getSession().write(MaplePacketCreator.getServerIP(c, Integer.parseInt(ChannelServer.getInstance(c.getChannel()).getIP().split(":")[1]), charId));
    }
}