package handling.login.handler;

import client.MapleClient;
import handling.login.LoginServer;

import tools.packet.LoginPacket;

public class ServerlistRequestHandler {

    public static void handlePacket(MapleClient c) {
        c.getSession().write(LoginPacket.getLoginWelcome());
        c.getSession().write(LoginPacket.getServerList(0, LoginServer.getLoad()));
        c.getSession().write(LoginPacket.getEndOfServerList());
    }
}
