package handling.login.handler;

import client.MapleClient;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import tools.MaplePacketCreator;
import tools.data.LittleEndianAccessor;

public class CharSelectedHandler {

    private static boolean loginFailCount(MapleClient c) {
        c.loginAttempt = (short) (c.loginAttempt + 1);

        return c.loginAttempt > 5;
    }

    public static void handlePacket(LittleEndianAccessor slea, MapleClient c) {
        int charId = slea.readInt();
        if ((!c.isLoggedIn()) || (loginFailCount(c)) || (!c.login_Auth(charId))) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if ((ChannelServer.getInstance(c.getChannel()) == null) || (c.getWorld() != 0)) {
            c.getSession().close();
            return;
        }
        if (c.getIdleTask() != null) {
            c.getIdleTask().cancel(true);
        }

        String ip = c.getSessionIPAddress();
        LoginServer.putLoginAuth(charId, ip.substring(ip.indexOf('/') + 1, ip.length()), c.getTempIP());
        c.updateLoginState(1, ip);
        c.getSession().write(MaplePacketCreator.getServerIP(c, Integer.parseInt(ChannelServer.getInstance(c.getChannel()).getIP().split(":")[1]), charId));
    }
}
