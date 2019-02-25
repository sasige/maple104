package handling.cashshop.handler;

import client.MapleCharacter;
import client.MapleClient;
import handling.cashshop.CashShopServer;
import handling.login.LoginServer;
import handling.world.CharacterTransfer;
import handling.world.World;
import org.apache.log4j.Logger;
import server.MTSCart;
import server.MTSStorage;
import tools.MaplePacketCreator;
import tools.data.LittleEndianAccessor;
import tools.packet.MTSCSPacket;

public class CashShopOperation {

    private static final Logger log = Logger.getLogger(CashShopOperation.class);

    public static void LeaveCS(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        CashShopServer.getPlayerStorageMTS().deregisterPlayer(chr);
        CashShopServer.getPlayerStorage().deregisterPlayer(chr);
        c.updateLoginState(1, c.getSessionIPAddress());
        try {
            World.ChannelChange_Data(new CharacterTransfer(chr), chr.getId(), c.getChannel());
            c.getSession().write(MaplePacketCreator.getChannelChange(c, Integer.parseInt(handling.channel.ChannelServer.getInstance(c.getChannel()).getIP().split(":")[1])));
        } finally {
            String s = c.getSessionIPAddress();
            LoginServer.addIPAuth(s.substring(s.indexOf('/') + 1, s.length()));
            chr.saveToDB(false, true);
            c.setPlayer(null);
            c.setReceiving(false);
            c.getSession().close();
        }
    }

    public static void EnterCS(int playerid, MapleClient c) {
        CharacterTransfer transfer = CashShopServer.getPlayerStorage().getPendingCharacter(playerid);
        boolean mts = false;
        if (transfer == null) {
            transfer = CashShopServer.getPlayerStorageMTS().getPendingCharacter(playerid);
            mts = true;
            if (transfer == null) {
                c.getSession().close();
                log.info("商城检测连接 - 1 transfer == null");
                return;
            }
        }
        MapleCharacter chr = MapleCharacter.ReconstructChr(transfer, c, false);

        c.setPlayer(chr);
        c.setAccID(chr.getAccountID());

        if (!c.CheckIPAddress()) {
            c.getSession().close();
            log.info(new StringBuilder().append("商城检测连接 - 2 ").append(!c.CheckIPAddress()).toString());
            return;
        }

        int state = c.getLoginState();
        boolean allowLogin = false;
        if (((state == 1) || (state == 3))
                && (!World.isCharacterListConnected(c.loadCharacterNames(c.getWorld())))) {
            allowLogin = true;
        }

        if (!allowLogin) {
            c.setPlayer(null);
            c.getSession().close();
            log.info(new StringBuilder().append("商城检测连接 - 3 ").append(!allowLogin).toString());
            return;
        }
        c.updateLoginState(2, c.getSessionIPAddress());
        if (mts) {
            CashShopServer.getPlayerStorageMTS().registerPlayer(chr);
            c.getSession().write(MTSCSPacket.startMTS(chr));
            MTSCart cart = MTSStorage.getInstance().getCart(c.getPlayer().getId());
            cart.refreshCurrentView();
            MTSOperation.MTSUpdate(cart, c);
        } else {
            CashShopServer.getPlayerStorage().registerPlayer(chr);
            c.getSession().write(MTSCSPacket.warpchartoCS(c));
            c.getSession().write(MTSCSPacket.warpCS(c));
            c.getSession().write(MTSCSPacket.enableCSUse());
            c.getSession().write(MTSCSPacket.热点推荐(c.getPlayer()));
            c.getSession().write(MTSCSPacket.每日特卖());
            c.getSession().write(MTSCSPacket.商城未知封包2());
            c.getSession().write(MTSCSPacket.商城道具栏信息(c));
            c.getSession().write(MTSCSPacket.商城未知封包1());
            c.getSession().write(MTSCSPacket.商城礼物信息(c));
            c.getSession().write(MTSCSPacket.商城购物车(c.getPlayer(), false));
            c.getSession().write(MTSCSPacket.刷新点卷信息(c.getPlayer()));
            c.getSession().write(MTSCSPacket.商城道具栏信息(c));
            c.getPlayer().getCashInventory().checkExpire(c);
        }
    }

    public static void CSUpdate(MapleClient c) {
        c.getSession().write(MTSCSPacket.刷新点卷信息(c.getPlayer()));
    }

    public static void doCSPackets(MapleClient c) {
        c.getSession().write(MTSCSPacket.商城道具栏信息(c));
        c.getSession().write(MTSCSPacket.刷新点卷信息(c.getPlayer()));
        c.getSession().write(MTSCSPacket.enableCSUse());
        c.getPlayer().getCashInventory().checkExpire(c);
    }
}
