package handling.login.handler;

import client.MapleClient;
import handling.login.LoginServer;
import tools.packet.LoginPacket;

public class ServerStatusRequestHandler {

    public static void handlePacket(MapleClient c) {
        int numPlayer = LoginServer.getUsersOn();
        int userLimit = LoginServer.getUserLimit();
        if (numPlayer >= userLimit) {
            c.getSession().write(LoginPacket.getServerStatus(2));
        } else if (numPlayer * 2 >= userLimit) {
            c.getSession().write(LoginPacket.getServerStatus(1));
        } else {
            c.getSession().write(LoginPacket.getServerStatus(0));
        }
    }
}
