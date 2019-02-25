package handling.login.handler;

import client.MapleClient;
import handling.channel.ChannelServer;
import handling.world.World;
import java.util.List;
import tools.data.LittleEndianAccessor;
import tools.packet.LoginPacket;

public class CharlistRequestHandler {

    public static void handlePacket(LittleEndianAccessor slea, MapleClient c) {
        if (!c.isLoggedIn()) {
            c.getSession().close();
            return;
        }
        int server = slea.readByte();
        int channel = slea.readByte() + 1;
        if ((!World.isChannelAvailable(channel)) || (server != 0)) {
            c.getSession().write(LoginPacket.getLoginFailed(10));
            return;
        }

        System.out.println("客户地址: " + c.getSession().getRemoteAddress().toString().split(":")[0] + " 连接到世界服务器: " + server + " 频道: " + channel);

        List chars = c.loadCharacters(server);
        if ((chars != null) && (ChannelServer.getInstance(channel) != null)) {
            c.setWorld(server);
            c.setChannel(channel);
            c.getSession().write(LoginPacket.EventCheck());
            c.getSession().write(LoginPacket.getCharList(c.getSecondPassword(), chars, c.getCharacterSlots()));
        } else {
            c.getSession().close();
        }
    }
}
