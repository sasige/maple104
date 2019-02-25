package handling.cashshop.handler;

import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.MapleClient;
import constants.GameConstants;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import server.CashItemFactory;
import server.CashItemInfo;
import server.MapleInventoryManipulator;
import tools.FileoutputUtil;
import tools.Triple;
import tools.data.LittleEndianAccessor;
import tools.packet.MTSCSPacket;

public class CouponCodeHandler {

    public static void handlePacket(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        String toPlayer = slea.readMapleAsciiString();
        if (toPlayer.length() >= 0) {
            c.getSession().write(MTSCSPacket.商城错误提示(22));
            c.getSession().write(MTSCSPacket.刷新点卷信息(chr));
            return;
        }
        String code = slea.readMapleAsciiString();
        if (code.length() <= 0) {
            c.getSession().write(MTSCSPacket.刷新点卷信息(chr));
            return;
        }
        Triple info = null;
        try {
            info = MapleCharacterUtil.getNXCodeInfo(code);
        } catch (SQLException e) {
            System.out.print("错误 getNXCodeInfo" + e);
        }
        if ((info != null) && (((Boolean) info.left).booleanValue())) {
            int type = ((Integer) info.mid).intValue();
            int item = ((Integer) info.right).intValue();
            try {
                MapleCharacterUtil.setNXCodeUsed(chr.getName(), code);
            } catch (SQLException e) {
                System.out.print("错误 setNXCodeUsed" + e);
            }

            Map itemz = new HashMap();
            int maplePoints = 0;
            int mesos = 0;
            switch (type) {
                case 1:
                case 2:
                    c.getPlayer().modifyCSPoints(type, item, false);
                    maplePoints = item;
                    break;
                case 3:
                    CashItemInfo itez = CashItemFactory.getInstance().getItem(item);
                    if (itez == null) {
                        c.getSession().write(MTSCSPacket.商城错误提示(0));
                        return;
                    }
                    byte slot = MapleInventoryManipulator.addId(c, itez.getId(), (short) 1, "", "Cash shop: coupon code on " + FileoutputUtil.CurrentReadable_Date());
                    if (slot <= -1) {
                        c.getSession().write(MTSCSPacket.商城错误提示(0));
                        return;
                    }
                    itemz.put(Integer.valueOf(item), chr.getInventory(GameConstants.getInventoryType(item)).getItem((short) slot));

                    break;
                case 4:
                    chr.gainMeso(item, false);
                    mesos = item;
            }

            c.getSession().write(MTSCSPacket.showCouponRedeemedItem(itemz, mesos, maplePoints, c));
        } else {
            c.getSession().write(MTSCSPacket.商城错误提示(info == null ? 14 : 16));
        }
    }
}