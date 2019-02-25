package handling.login;

import client.MapleClient;
import constants.ServerConstants;
import database.DatabaseConnection;
import handling.channel.ChannelServer;
import handling.login.handler.ServerlistRequestHandler;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;
import server.Timer.PingTimer;
import tools.FileoutputUtil;
import tools.MaplePacketCreator;
import tools.packet.LoginPacket;

public class LoginWorker {

    private static long lastUpdate = 0L;

    public static void registerClient(final MapleClient c) {
        if ((LoginServer.isAdminOnly()) && (!c.isGm()) && (!c.isLocalhost())) {
            c.getSession().write(MaplePacketCreator.serverNotice(1, "当前服务器设置只能管理员进入游戏.\r\n我们目前在修复几个问题.\r\n请稍后再试."));
            c.getSession().write(LoginPacket.getLoginFailed(16));
            return;
        }

        if (System.currentTimeMillis() - lastUpdate > 600000L) {
            lastUpdate = System.currentTimeMillis();
            Map<Integer, Integer> load = ChannelServer.getChannelLoad();
            int usersOn = 0;
            if ((load == null) || (load.size() <= 0)) {
                lastUpdate = 0L;
                c.getSession().write(LoginPacket.getLoginFailed(7));
                return;
            }
            double loadFactor = 1200.0D / (LoginServer.getUserLimit() / load.size());
            for (Entry<Integer, Integer> entry : load.entrySet()) {
                usersOn += entry.getValue();
                load.put(entry.getKey(), Integer.valueOf(Math.min(1200, (int) (entry.getValue() * loadFactor))));
            }
            LoginServer.setLoad(load, usersOn);
            lastUpdate = System.currentTimeMillis();
        }
        if (c.finishLogin() == 0) {
            c.getSession().write(LoginPacket.getAuthSuccessRequest(c));
            ServerlistRequestHandler.handlePacket(c);
            c.setIdleTask(PingTimer.getInstance().schedule(new Runnable() {

                public void run() {
                    c.getSession().close();
                }
            }, 6000000L));
        } else {
            c.getSession().write(LoginPacket.getLoginFailed(7));
            return;
        }
        if (!ServerConstants.isEligible(c.getSessionIPAddress())) {
            try {
                PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("INSERT INTO iplog (accid, ip, time) VALUES (?, ?, ?)");
                ps.setInt(1, c.getAccID());
                ps.setString(2, c.getSession().getRemoteAddress().toString());
                ps.setString(3, FileoutputUtil.CurrentReadable_Time());
                ps.executeUpdate();
                ps.close();
            } catch (SQLException e) {
                FileoutputUtil.outputFileError("log\\Packet_Except.log", e);
            }
        }
    }
}