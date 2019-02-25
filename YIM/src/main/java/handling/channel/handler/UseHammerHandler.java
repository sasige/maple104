package handling.channel.handler;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import constants.GameConstants;

import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.maps.FieldLimitType;
import server.maps.MapleMap;
import tools.MaplePacketCreator;
import tools.data.LittleEndianAccessor;

public class UseHammerHandler {

    public static void handlePacket(LittleEndianAccessor slea, MapleClient c) {
        slea.readInt();
        int hammerSlot = slea.readInt();
        int hammerItemid = slea.readInt();
        slea.readInt();
        int equipSlot = slea.readInt();
        Item useItem = c.getPlayer().getInventory(MapleInventoryType.USE).getItem((short) (byte) hammerSlot);
        Equip toItem = (Equip) c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((short) (byte) equipSlot);

        if ((useItem == null) || (useItem.getQuantity() <= 0) || (useItem.getItemId() != hammerItemid) || (c.getPlayer().hasBlockedInventory())) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (toItem != null) {
            if ((GameConstants.canHammer(toItem.getItemId())) && (ii.getSlots(toItem.getItemId()) > 0) && (toItem.getViciousHammer() < 2)) {
                toItem.setViciousHammer((byte) (toItem.getViciousHammer() + 1));
                toItem.setUpgradeSlots((byte) (toItem.getUpgradeSlots() + 1));
                c.getPlayer().forceReAddItem(toItem, MapleInventoryType.EQUIP);
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, (short) (byte) hammerSlot, (short) 1, false, true);
            } else {
                c.getPlayer().dropMessage(5, "该道具无法使用在此物品上。");
            }
        }
        if ((!c.getPlayer().isAlive()) || (c.getPlayer().getEventInstance() != null) || (FieldLimitType.ChannelSwitch.check(c.getPlayer().getMap().getFieldLimit()))) {
            c.getPlayer().dropMessage(1, "刷新人物数据失败.");
            return;
        }
        c.getPlayer().dropMessage(5, "正在刷新人数据.请等待...");
        c.getPlayer().fakeRelog();
        if (c.getPlayer().getScrolledPosition() != 0) {
            c.getSession().write(MaplePacketCreator.pamSongUI());
        }
    }
}