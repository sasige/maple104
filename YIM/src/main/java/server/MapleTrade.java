package server;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Item;
import client.inventory.ItemFlag;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.messages.CommandProcessor;
import constants.GameConstants;
import constants.ServerConstants.CommandType;
import handling.world.World.Broadcast;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;

import tools.MaplePacketCreator;
import tools.packet.PlayerShopPacket;

public class MapleTrade {

    private MapleTrade partner = null;
    private List<Item> items = new LinkedList();
    private List<Item> exchangeItems;
    private int meso = 0;
    private int exchangeMeso = 0;
    private boolean locked = false;
    private boolean inTrade = false;
    private WeakReference<MapleCharacter> chr;
    private byte tradingslot;
    private static final Logger log = Logger.getLogger(MapleTrade.class);

    public MapleTrade(byte tradingslot, MapleCharacter chr) {
        this.tradingslot = tradingslot;
        this.chr = new WeakReference(chr);
    }

    public void CompleteTrade() {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (this.exchangeItems != null) {
            List<Item> itemz = new LinkedList<Item>(this.exchangeItems);
            for (Item item : itemz) {
                short flag = item.getFlag();
                if (ItemFlag.KARMA_EQ.check(flag)) {
                    item.setFlag((short) (flag - ItemFlag.KARMA_EQ.getValue()));
                } else if (ItemFlag.KARMA_USE.check(flag)) {
                    item.setFlag((short) (flag - ItemFlag.KARMA_USE.getValue()));
                }
                MapleInventoryManipulator.addFromDrop(((MapleCharacter) this.chr.get()).getClient(), item, false);
                log.info("[交易] " + ((MapleCharacter) this.chr.get()).getName() + " 交易获得道具: " + item.getItemId() + " x " + item.getQuantity() + " - " + ii.getName(item.getItemId()));
            }
            this.exchangeItems.clear();
        }
        if (this.exchangeMeso > 0) {
            ((MapleCharacter) this.chr.get()).gainMeso(this.exchangeMeso - GameConstants.getTaxAmount(this.exchangeMeso), false, false);
            log.info("[交易] " + ((MapleCharacter) this.chr.get()).getName() + " 交易获得金币: " + this.exchangeMeso);
        }
        this.exchangeMeso = 0;
        ((MapleCharacter) this.chr.get()).getClient().getSession().write(MaplePacketCreator.TradeMessage(this.tradingslot, (byte) 8));
    }

    public void cancel(MapleClient c, MapleCharacter chr) {
        cancel(c, chr, 0);
    }

    public void cancel(MapleClient c, MapleCharacter chr, int unsuccessful) {
        if (this.items != null) {
            List<Item> itemz = new LinkedList<Item>(this.items);
            for (Item item : itemz) {
                MapleInventoryManipulator.addFromDrop(c, item, false);
            }
            this.items.clear();
        }
        if (this.meso > 0) {
            chr.gainMeso(this.meso, false, false);
        }
        this.meso = 0;
        c.getSession().write(MaplePacketCreator.getTradeCancel(this.tradingslot, unsuccessful));
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void setMeso(int meso) {
        if ((this.locked) || (this.partner == null) || (meso <= 0) || (this.meso + meso <= 0)) {
            return;
        }
        if (((MapleCharacter) this.chr.get()).getMeso() >= meso) {
            ((MapleCharacter) this.chr.get()).gainMeso(-meso, false, false);
            this.meso += meso;
            ((MapleCharacter) this.chr.get()).getClient().getSession().write(MaplePacketCreator.getTradeMesoSet((byte) 0, this.meso));
            if (this.partner != null) {
                this.partner.getChr().getClient().getSession().write(MaplePacketCreator.getTradeMesoSet((byte) 1, this.meso));
            }
        }
    }

    public void addItem(Item item) {
        if ((this.locked) || (this.partner == null)) {
            return;
        }
        this.items.add(item);
        (this.chr.get()).getClient().getSession().write(MaplePacketCreator.getTradeItemAdd((byte) 0, item));
        if (this.partner != null) {
            this.partner.getChr().getClient().getSession().write(MaplePacketCreator.getTradeItemAdd((byte) 1, item));
        }
    }

    public void chat(String message) {
        if (!CommandProcessor.processCommand(((MapleCharacter) this.chr.get()).getClient(), message, CommandType.TRADE)) {
            ((MapleCharacter) this.chr.get()).dropMessage(-2, ((MapleCharacter) this.chr.get()).getName() + " : " + message);
            if (this.partner != null) {
                this.partner.getChr().getClient().getSession().write(PlayerShopPacket.shopChat(((MapleCharacter) this.chr.get()).getName() + " : " + message, 1));
            }
        }
        if (((MapleCharacter) this.chr.get()).getClient().isMonitored()) {
            Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, ((MapleCharacter) this.chr.get()).getName() + " 在交易中对 " + this.partner.getChr().getName() + " 说: " + message));
        } else if ((this.partner != null) && (this.partner.getChr() != null) && (this.partner.getChr().getClient().isMonitored())) {
            Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, ((MapleCharacter) this.chr.get()).getName() + " 在交易中对 " + this.partner.getChr().getName() + " 说: " + message));
        }
    }

    public void chatAuto(String message) {
        (this.chr.get()).dropMessage(-2, message);
        if (this.partner != null) {
            this.partner.getChr().getClient().getSession().write(PlayerShopPacket.shopChat(message, 1));
        }
        if (((MapleCharacter) this.chr.get()).getClient().isMonitored()) {
            Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, ((MapleCharacter) this.chr.get()).getName() + " said in trade [Automated] with " + this.partner.getChr().getName() + " 说: " + message));
        } else if ((this.partner != null) && (this.partner.getChr() != null) && (this.partner.getChr().getClient().isMonitored())) {
            Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, ((MapleCharacter) this.chr.get()).getName() + " said in trade [Automated] with " + this.partner.getChr().getName() + " 说: " + message));
        }
    }

    public MapleTrade getPartner() {
        return this.partner;
    }

    public void setPartner(MapleTrade partner) {
        if (this.locked) {
            return;
        }
        this.partner = partner;
    }

    public MapleCharacter getChr() {
        return (MapleCharacter) this.chr.get();
    }

    public int getNextTargetSlot() {
        if (this.items.size() >= 9) {
            return -1;
        }
        int ret = 1;
        for (Item item : this.items) {
            if (item.getPosition() == ret) {
                ret++;
            }
        }
        return ret;
    }

    public boolean inTrade() {
        return this.inTrade;
    }

    public boolean setItems(MapleClient c, Item item, byte targetSlot, int quantity) {
        int target = getNextTargetSlot();
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if ((this.partner == null) || (target == -1) || (GameConstants.isPet(item.getItemId())) || (isLocked()) || ((GameConstants.getInventoryType(item.getItemId()) == MapleInventoryType.EQUIP) && (quantity != 1))) {
            return false;
        }
        short flag = item.getFlag();
        if ((ItemFlag.UNTRADEABLE.check(flag)) || (ItemFlag.LOCK.check(flag))) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return false;
        }
        if (((ii.isDropRestricted(item.getItemId())) || (ii.isAccountShared(item.getItemId())))
                && (!ItemFlag.KARMA_EQ.check(flag)) && (!ItemFlag.KARMA_USE.check(flag))) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return false;
        }

        Item tradeItem = item.copy();
        if ((GameConstants.isThrowingStar(item.getItemId())) || (GameConstants.isBullet(item.getItemId()))) {
            tradeItem.setQuantity(item.getQuantity());
            MapleInventoryManipulator.removeFromSlot(c, GameConstants.getInventoryType(item.getItemId()), item.getPosition(), item.getQuantity(), true);
        } else {
            tradeItem.setQuantity((short) quantity);
            MapleInventoryManipulator.removeFromSlot(c, GameConstants.getInventoryType(item.getItemId()), item.getPosition(), (short) quantity, true);
        }
        if (targetSlot < 0) {
            targetSlot = (byte) target;
        } else {
            for (Item itemz : this.items) {
                if (itemz.getPosition() == targetSlot) {
                    targetSlot = (byte) target;
                    break;
                }
            }
        }
        tradeItem.setPosition((short) targetSlot);
        addItem(tradeItem);
        return true;
    }

    private int check() {
        if (((MapleCharacter) this.chr.get()).getMeso() + this.exchangeMeso < 0) {
            return 1;
        }
        if (this.exchangeItems != null) {
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            byte eq = 0, use = 0, setup = 0, etc = 0, cash = 0;
            for (Item item : this.exchangeItems) {
                switch (GameConstants.getInventoryType(item.getItemId())) {
                    case EQUIP:
                        eq++;
                        break;
                    case USE:
                        use++;
                        break;
                    case SETUP:
                        setup++;
                        break;
                    case ETC:
                        etc++;
                        break;
                    case CASH:
                        cash++;
                }

                if ((ii.isPickupRestricted(item.getItemId())) && (((MapleCharacter) this.chr.get()).haveItem(item.getItemId(), 1, true, true))) {
                    return 2;
                }
            }
            if ((((MapleCharacter) this.chr.get()).getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < eq) || (((MapleCharacter) this.chr.get()).getInventory(MapleInventoryType.USE).getNumFreeSlot() < use) || (((MapleCharacter) this.chr.get()).getInventory(MapleInventoryType.SETUP).getNumFreeSlot() < setup) || (((MapleCharacter) this.chr.get()).getInventory(MapleInventoryType.ETC).getNumFreeSlot() < etc) || (((MapleCharacter) this.chr.get()).getInventory(MapleInventoryType.CASH).getNumFreeSlot() < cash)) {
                return 1;
            }
        }
        return 0;
    }

    public static void completeTrade(MapleCharacter c) {
        MapleTrade local = c.getTrade();
        MapleTrade partner = local.getPartner();
        if ((partner == null) || (local.locked)) {
            return;
        }
        local.locked = true;
        partner.getChr().getClient().getSession().write(MaplePacketCreator.getTradeConfirmation());
        partner.exchangeItems = new LinkedList(local.items);
        partner.exchangeMeso = local.meso;
        if (partner.isLocked()) {
            int lz = local.check();
            int lz2 = partner.check();
            if ((lz == 0) && (lz2 == 0)) {
                log.info("[交易] -------------------------------------------------------------------------- ");
                local.CompleteTrade();
                partner.CompleteTrade();
                log.info("[交易] " + local.getChr().getName() + " 和 " + partner.getChr().getName() + " 交易完成。");
            } else {
                partner.cancel(partner.getChr().getClient(), partner.getChr(), lz == 0 ? lz2 : lz);
                local.cancel(c.getClient(), c, lz == 0 ? lz2 : lz);
            }
            partner.getChr().setTrade(null);
            c.setTrade(null);
        }
    }

    public static void cancelTrade(MapleTrade Localtrade, MapleClient c, MapleCharacter chr) {
        Localtrade.cancel(c, chr);
        MapleTrade partner = Localtrade.getPartner();
        if ((partner != null) && (partner.getChr() != null)) {
            partner.cancel(partner.getChr().getClient(), partner.getChr());
            partner.getChr().setTrade(null);
        }
        chr.setTrade(null);
    }

    public static void startTrade(MapleCharacter c) {
        if (c.getTrade() == null) {
            c.setTrade(new MapleTrade((byte) 0, c));
            c.getClient().getSession().write(MaplePacketCreator.getTradeStart(c.getClient(), c.getTrade(), (byte) 0));
        } else {
            c.getClient().getSession().write(MaplePacketCreator.serverNotice(5, "不能同时做多件事情."));
        }
    }

    public static void inviteTrade(MapleCharacter c1, MapleCharacter c2) {
        if ((c1 == null) || (c1.getTrade() == null)) {
            return;
        }
        if ((c2 != null) && (c2.getTrade() == null)) {
            c2.setTrade(new MapleTrade((byte) 1, c2));
            c2.getTrade().setPartner(c1.getTrade());
            c1.getTrade().setPartner(c2.getTrade());
            c2.getClient().getSession().write(MaplePacketCreator.getTradeInvite(c1));
        } else {
            c1.getClient().getSession().write(MaplePacketCreator.serverNotice(5, "The other player is already trading with someone else."));
            cancelTrade(c1.getTrade(), c1.getClient(), c1);
        }
    }

    public static void visitTrade(MapleCharacter c1, MapleCharacter c2) {
        if ((c2 != null) && (c1.getTrade() != null) && (c1.getTrade().getPartner() == c2.getTrade()) && (c2.getTrade() != null) && (c2.getTrade().getPartner() == c1.getTrade())) {
            c1.getTrade().inTrade = true;
            c2.getClient().getSession().write(PlayerShopPacket.shopVisitorAdd(c1, 1));
            c1.getClient().getSession().write(MaplePacketCreator.getTradeStart(c1.getClient(), c1.getTrade(), (byte) 1));
        } else {
            c1.getClient().getSession().write(MaplePacketCreator.serverNotice(5, "The other player has already closed the trade"));
        }
    }

    public static void declineTrade(MapleCharacter c) {
        MapleTrade trade = c.getTrade();
        if (trade != null) {
            if (trade.getPartner() != null) {
                MapleCharacter other = trade.getPartner().getChr();
                if ((other != null) && (other.getTrade() != null)) {
                    other.getTrade().cancel(other.getClient(), other);
                    other.setTrade(null);
                    other.dropMessage(5, c.getName() + " 拒绝了你的交易邀请.");
                }
            }
            trade.cancel(c.getClient(), c);
            c.setTrade(null);
        }
    }
}