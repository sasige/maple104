package handling.channel.handler;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleQuestStatus;
import client.RockPaperScissors;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import handling.SendPacketOpcode;
import handling.world.World.Broadcast;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.log4j.Logger;
import scripting.NPCConversationManager;
import scripting.NPCScriptManager;
import server.*;
import server.life.MapleNPC;
import server.maps.MapScriptMethods;
import server.maps.MapleQuickMove;
import server.quest.MapleQuest;
import tools.MaplePacketCreator;
import tools.Pair;
import tools.data.LittleEndianAccessor;
import tools.data.MaplePacketLittleEndianWriter;

public class NPCHandler {
    
    private static Logger log = Logger.getLogger(NPCHandler.class);
    
    public static void NPCAnimation(LittleEndianAccessor slea, MapleClient c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.NPC_ACTION.getValue());
        int length = (int) slea.available();
        if (length == 10) {
            mplew.writeInt(slea.readInt());
            mplew.writeShort(slea.readShort());
            mplew.writeInt(slea.readInt());
        } else if (length > 6) {
            mplew.write(slea.read(length - 9));
        } else {
            return;
        }
        c.getSession().write(mplew.getPacket());
    }
    
    public static void NPCShop(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        byte bmode = slea.readByte();
        if (chr == null) {
            return;
        }
        switch (bmode) {
            case 0: {//买
                MapleShop shop = chr.getShop();
                if (shop == null) {
                    return;
                }
                short position = slea.readShort();
                int itemId = slea.readInt();
                short quantity = slea.readShort();
                shop.buy(c, itemId, quantity, position);
                break;
            }
            case 1: {//卖
                MapleShop shop = chr.getShop();
                if (shop == null) {
                    return;
                }
                byte slot = (byte) slea.readShort();
                int itemId = slea.readInt();
                short quantity = slea.readShort();
                shop.sell(c, GameConstants.getInventoryType(itemId), slot, quantity);
                break;
            }
            case 2: {//充值
                MapleShop shop = chr.getShop();
                if (shop == null) {
                    return;
                }
                byte slot = (byte) slea.readShort();
                shop.recharge(c, slot);
                break;
            }
            default:
                chr.setConversation(0);
        }
    }
    
    public static void NPCTalk(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        if ((chr == null) || (chr.getMap() == null) || (chr.getBattle() != null)) {
            return;
        }
        MapleNPC npc = chr.getMap().getNPCByOid(slea.readInt());
        if (npc == null) {
            return;
        }
        if (chr.hasBlockedInventory()) {
            chr.dropMessage(-1, "您当前已经和1个NPC对话了. 如果不是请输入 @ea 命令进行解卡。");
            return;
        }
        if (chr.getAntiMacro().inProgress()) {
            chr.dropMessage(5, "被使用测谎仪时无法操作。");
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        
        if (npc.hasShop()) {
            chr.setConversation(1);
            npc.sendShop(c);
        } else {
            NPCScriptManager.getInstance().start(c, npc.getId());
        }
    }
    
    public static void QuestAction(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        byte action = slea.readByte();
        int quest = slea.readUShort();
        
        if (chr == null) {
            return;
        }
        if (chr.getAntiMacro().inProgress()) {
            chr.dropMessage(5, "被使用测谎仪时无法操作。");
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        MapleQuest q = MapleQuest.getInstance(quest);
        switch (action) {
            case 0:
                slea.readInt();
                int itemid = slea.readInt();
                q.RestoreLostItem(chr, itemid);
                break;
            case 1:
                int npc = slea.readInt();
                if (q.hasStartScript()) {
                    break;
                }
                q.start(chr, npc);
                if ((!chr.isAdmin()) || (!ServerProperties.ShowPacket())) {
                    break;
                }
                chr.dropMessage(6, "开始系统任务 NPC: " + npc + " Quest：" + quest);
                break;
            case 2:
                npc = slea.readInt();
                
                slea.readInt();
                if (q.hasEndScript()) {
                    return;
                }
                if (slea.available() >= 4L) {
                    q.complete(chr, npc, Integer.valueOf(slea.readInt()));
                } else {
                    q.complete(chr, npc);
                }
                if ((!chr.isAdmin()) || (!ServerProperties.ShowPacket())) {
                    break;
                }
                chr.dropMessage(6, "完成系统任务 NPC: " + npc + " Quest: " + quest);
                break;
            case 3:
                if (GameConstants.canForfeit(q.getId())) {
                    q.forfeit(chr);
                    if ((!chr.isAdmin()) || (!ServerProperties.ShowPacket())) {
                        break;
                    }
                    chr.dropMessage(6, "放弃系统任务 Quest: " + quest);
                } else {
                    chr.dropMessage(1, "无法放弃这个任务.");
                }
                break;
            case 4:
                npc = slea.readInt();
                if (chr.hasBlockedInventory()) {
                    chr.dropMessage(-1, "您当前已经和1个NPC对话了. 如果不是请输入 @ea 命令进行解卡。");
                    return;
                }
            
                NPCScriptManager.getInstance().startQuest(c, npc, quest);
                if ((!chr.isAdmin()) || (!ServerProperties.ShowPacket())) {
                    break;
                }
                chr.dropMessage(6, "执行脚本任务 NPC：" + npc + " Quest: " + quest);
                break;
            case 5:
                npc = slea.readInt();
                if (chr.hasBlockedInventory()) {
                    chr.dropMessage(-1, "您当前已经和1个NPC对话了. 如果不是请输入 @ea 命令进行解卡。");
                    return;
                }
                
                NPCScriptManager.getInstance().endQuest(c, npc, quest, false);
                c.getSession().write(MaplePacketCreator.showSpecialEffect(13));
                chr.getMap().broadcastMessage(chr, MaplePacketCreator.showSpecialEffect(chr.getId(), 13), false);
                if ((!chr.isAdmin()) || (!ServerProperties.ShowPacket())) {
                    break;
                }
                chr.dropMessage(6, "完成脚本任务 NPC：" + npc + " Quest: " + quest);
                break;
        }
    }
    
    public static void Storage(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) { //仓库处理
        byte mode = slea.readByte();
        if (chr == null) {
            return;
        }
        if (chr.getAntiMacro().inProgress()) {
            chr.dropMessage(5, "被使用测谎仪时无法操作。");
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        MapleStorage storage = chr.getStorage();
        switch (mode) {
            case 4://取出
                byte type = slea.readByte();
                byte slot = storage.getSlot(MapleInventoryType.getByType(type), slea.readByte());
                Item item = storage.takeOut(slot);
                if (item != null) {
                    if (chr.getMap().getId() == 910000000) {
                        if (chr.getMeso() < 1000) {
                            storage.store(item);
                            c.getSession().write(MaplePacketCreator.getStorageError((byte) 11));
                            return;
                        }
                        chr.gainMeso(-1000, false);
                    }
                    
                    if (!MapleInventoryManipulator.checkSpace(c, item.getItemId(), item.getQuantity(), item.getOwner())) {
                        storage.store(item);
                        c.getSession().write(MaplePacketCreator.getStorageError((byte) 10));
                    } else {
                        MapleInventoryManipulator.addFromDrop(c, item, false);
                        storage.sendTakenOut(c, GameConstants.getInventoryType(item.getItemId()));
                    }
                } else {
                    log.info("[作弊] " + chr.getName() + " (等级 " + chr.getLevel() + ") 试图从仓库取出不存在的道具.");
                    Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM Message] 玩家: " + chr.getName() + " (等级 " + chr.getLevel() + ") 试图从仓库取出不存在的道具."));
                    c.getSession().write(MaplePacketCreator.enableActions());
                }
                break;
            case 5://存入
                slot = (byte) slea.readShort();
                int itemId = slea.readInt();
                MapleInventoryType type1 = GameConstants.getInventoryType(itemId);
                short quantity = slea.readShort();
                MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                if (quantity < 1) {
                    AutobanManager.getInstance().autoban(c, "试图存入到仓库的道具数量: " + quantity + " 道具ID: " + itemId);
                    return;
                }
                if (storage.isFull()) {
                    c.getSession().write(MaplePacketCreator.getStorageError((byte) 17));
                    return;
                }
                if (chr.getInventory(type1).getItem((short) slot) == null) {
                    c.getSession().write(MaplePacketCreator.enableActions());
                    return;
                }
                int meso = chr.getMap().getId() == 910000000 ? 500 : 100;
                if (chr.getMeso() < meso) {
                    c.getSession().write(MaplePacketCreator.getStorageError((byte) 16));
                    return;
                }
                item = chr.getInventory(type1).getItem((short) slot).copy();
                if (GameConstants.isPet(item.getItemId())) {
                    c.getSession().write(MaplePacketCreator.enableActions());
                    return;
                }
                short flag = item.getFlag();
                if ((ii.isPickupRestricted(item.getItemId())) && (storage.findById(item.getItemId()) != null)) {
                    c.getSession().write(MaplePacketCreator.enableActions());
                    return;
                }
                if ((item.getItemId() == itemId) && ((item.getQuantity() >= quantity) || (GameConstants.isThrowingStar(itemId)) || (GameConstants.isBullet(itemId)))) {
                    if (ii.isDropRestricted(item.getItemId())) {
                        if (ItemFlag.KARMA_EQ.check(flag)) {
                            item.setFlag((short) (flag - ItemFlag.KARMA_EQ.getValue()));
                        } else if (ItemFlag.KARMA_USE.check(flag)) {
                            item.setFlag((short) (flag - ItemFlag.KARMA_USE.getValue()));
                        } else if (ItemFlag.KARMA_ACC.check(flag)) {
                            item.setFlag((short) (flag - ItemFlag.KARMA_ACC.getValue()));
                        } else if (ItemFlag.KARMA_ACC_USE.check(flag)) {
                            item.setFlag((short) (flag - ItemFlag.KARMA_ACC_USE.getValue()));
                        } else {
                            c.getSession().write(MaplePacketCreator.enableActions());
                            return;
                        }
                    }
                    if ((GameConstants.isThrowingStar(itemId)) || (GameConstants.isBullet(itemId))) {
                        quantity = item.getQuantity();
                    }
                    chr.gainMeso(-meso, false, false);
                    MapleInventoryManipulator.removeFromSlot(c, type1, (short) slot, quantity, false);
                    item.setQuantity(quantity);
                    storage.store(item);
                } else {
                    AutobanManager.getInstance().addPoints(c, 1000, 0L, "试图存入到仓库的道具ID (" + itemId + "/" + item.getItemId() + ") 或者道具数量 (" + quantity + "/" + item.getQuantity() + ") 不正确.");
                    return;
                }
                
                storage.sendStored(c, GameConstants.getInventoryType(itemId));
                break;
            case 6:
                storage.arrange();
                storage.update(c);
                break;
            case 7://存 取金钱
                meso = slea.readInt();
                int storageMesos = storage.getMeso();
                int playerMesos = chr.getMeso();
                if (((meso > 0) && (storageMesos >= meso)) || ((meso < 0) && (playerMesos >= -meso))) {
                    if ((meso < 0) && (storageMesos - meso < 0)) {
                        meso = -(Integer.MAX_VALUE - storageMesos);
                        if (-meso > playerMesos) {
                            return;
                        }
                    } else if ((meso > 0) && (playerMesos + meso < 0)) {
                        meso = Integer.MAX_VALUE - playerMesos;
                        if (meso > storageMesos) {
                            return;
                        }
                    }
                    storage.setMeso(storageMesos - meso);
                    chr.gainMeso(meso, false, false);
                } else {
                    AutobanManager.getInstance().addPoints(c, 1000, 0L, "Trying to store or take out unavailable amount of mesos (" + meso + "/" + storage.getMeso() + "/" + c.getPlayer().getMeso() + ")");
                    return;
                }
                storage.sendMeso(c);
                break;
            case 8://退出
                storage.close();
                chr.setConversation(0);
                break;
            default:
                System.out.println("Unhandled Storage mode : " + mode);
        }
    }
    
    public static void NPCMoreTalk(LittleEndianAccessor slea, MapleClient c) {
        byte lastMsg = slea.readByte();
        byte action = slea.readByte();
        
        if (((lastMsg == 18) && (c.getPlayer().getDirection() >= 0)) || ((lastMsg == 19) && (c.getPlayer().getDirection() == -1) && (action == 1) && (GameConstants.GMS))) {
            MapScriptMethods.startDirectionInfo(c.getPlayer(), lastMsg == 19);
            return;
        }
        NPCConversationManager cm = NPCScriptManager.getInstance().getCM(c);
        if ((cm == null) || (c.getPlayer().getConversation() == 0) || (cm.getLastMsg() != lastMsg)) {
            return;
        }
        cm.setLastMsg((byte) -1);
        if (lastMsg == 3) {
            if (action != 0) {
                cm.setGetText(slea.readMapleAsciiString());
                if (cm.getType() == 0) {
                    NPCScriptManager.getInstance().startQuest(c, action, lastMsg, -1);
                } else if (cm.getType() == 1) {
                    NPCScriptManager.getInstance().endQuest(c, action, lastMsg, -1);
                } else {
                    NPCScriptManager.getInstance().action(c, action, lastMsg, -1);
                }
            } else {
                cm.dispose();
            }
        } else {
            int selection = -1;
            if (slea.available() >= 4L) {
                selection = slea.readInt();
            } else if (slea.available() > 0L) {
                selection = slea.readByte();
            }
            if ((lastMsg == 4) && (selection == -1)) {
                cm.dispose();
                return;
            }
            if ((selection >= -1) && (action != -1)) {
                if (cm.getType() == 0) {
                    NPCScriptManager.getInstance().startQuest(c, action, lastMsg, selection);
                } else if (cm.getType() == 1) {
                    NPCScriptManager.getInstance().endQuest(c, action, lastMsg, selection);
                } else {
                    NPCScriptManager.getInstance().action(c, action, lastMsg, selection);
                }
            } else {
                cm.dispose();
            }
        }
    }
    
    public static void repairAll(MapleClient c) {
        int price = 0;
        
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        Map<Equip, Integer> eqs = new HashMap<Equip, Integer>();
        MapleInventoryType[] types = {MapleInventoryType.EQUIP, MapleInventoryType.EQUIPPED};
        for (MapleInventoryType type : types) {
            for (Item item : c.getPlayer().getInventory(type).newList()) {
                if ((item instanceof Equip)) {
                    Equip eq = (Equip) item;
                    if (eq.getDurability() >= 0) {
                        Map<String, Integer> eqStats = ii.getEquipStats(eq.getItemId());
                        if ((eqStats.containsKey("durability")) && (((Integer) eqStats.get("durability")).intValue() > 0) && (eq.getDurability() < ((Integer) eqStats.get("durability")).intValue())) {
                            double rPercentage = 100.0D - Math.ceil(eq.getDurability() * 1000.0D / (((Integer) eqStats.get("durability")).intValue() * 10.0D));
                            eqs.put(eq, eqStats.get("durability"));
                            price += (int) Math.ceil(rPercentage * ii.getPrice(eq.getItemId()) / (ii.getReqLevel(eq.getItemId()) < 70 ? 100.0D : 1.0D));
                        }
                    }
                }
            }
        }
        if ((eqs.size() <= 0) || (c.getPlayer().getMeso() < price)) {
            return;
        }
        c.getPlayer().gainMeso(-price, true);
        
        for (Entry<Equip, Integer> eqqz : eqs.entrySet()) {
            Equip ez = (Equip) eqqz.getKey();
            ez.setDurability(((Integer) eqqz.getValue()).intValue());
            c.getPlayer().forceReAddItem(ez.copy(), ez.getPosition() < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP);
        }
    }
    
    public static void repair(LittleEndianAccessor slea, MapleClient c) {
        if (slea.available() < 4L) {
            return;
        }
        int position = slea.readInt();
        MapleInventoryType type = position < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP;
        Item item = c.getPlayer().getInventory(type).getItem((short) (byte) position);
        if (item == null) {
            return;
        }
        Equip eq = (Equip) item;
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        Map eqStats = ii.getEquipStats(item.getItemId());
        if ((eq.getDurability() < 0) || (!eqStats.containsKey("durability")) || (((Integer) eqStats.get("durability")).intValue() <= 0) || (eq.getDurability() >= ((Integer) eqStats.get("durability")).intValue())) {
            return;
        }
        double rPercentage = 100.0D - Math.ceil(eq.getDurability() * 1000.0D / (((Integer) eqStats.get("durability")).intValue() * 10.0D));
        
        int price = (int) Math.ceil(rPercentage * ii.getPrice(eq.getItemId()) / (ii.getReqLevel(eq.getItemId()) < 70 ? 100.0D : 1.0D));
        
        if (c.getPlayer().getMeso() < price) {
            return;
        }
        c.getPlayer().gainMeso(-price, false);
        eq.setDurability(((Integer) eqStats.get("durability")).intValue());
        c.getPlayer().forceReAddItem(eq.copy(), type);
    }
    
    public static void MedalQuestAction(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        int questId = slea.readShort();
        int itemId = slea.readInt();
        int price = 100;
        MapleQuest quest = MapleQuest.getInstance(questId);
        MapleQuestStatus stat = chr.getQuest(quest);
        if ((((quest != null ? 1 : 0) & (quest.getMedalItem() > 0 ? 1 : 0)) != 0) && (chr.getQuestStatus(quest.getId()) == 2) && (quest.getMedalItem() == itemId)) {
            if (!chr.haveItem(itemId)) {
                String customData = "count=1";
                if ((stat != null) && (stat.getCustomData() != null)) {
                    if ("count=1".equals(stat.getCustomData())) {
                        price = 10000;
                        customData = "count=2";
                    } else if ("count=2".equals(stat.getCustomData())) {
                        price = 100000;
                        customData = "count=3";
                    } else if ("count=3".equals(stat.getCustomData())) {
                        price = 1000000;
                        customData = "count=4";
                    } else if ("count=4".equals(stat.getCustomData())) {
                        price = 10000000;
                        customData = "count=5";
                    } else {
                        chr.dropMessage(1, "已经无法继续重新领取勋章");
                        c.getSession().write(MaplePacketCreator.enableActions());
                        return;
                    }
                }
                if (chr.getMeso() < price) {
                    chr.dropMessage(1, "本次重新需要金币: " + price + "\r\n请检查金币是否足够");
                    c.getSession().write(MaplePacketCreator.enableActions());
                    return;
                }
                chr.gainMeso(-price, true, true);
                MapleInventoryManipulator.addById(c, itemId, (short) 1, "");
                stat.setCustomData(customData);
                c.getSession().write(MaplePacketCreator.updateInfoQuest(questId, customData));
                c.getSession().write(MaplePacketCreator.updateMedalQuestInfo((byte) 0, itemId));
            } else {
                c.getSession().write(MaplePacketCreator.updateMedalQuestInfo((byte) 3, itemId));
            }
        } else {
            c.getSession().write(MaplePacketCreator.enableActions());
        }
    }
    
    public static void UpdateQuest(LittleEndianAccessor slea, MapleClient c) {
        MapleQuest quest = MapleQuest.getInstance(slea.readShort());
        if (quest != null) {
            c.getPlayer().updateQuest(c.getPlayer().getQuest(quest), true);
        }
    }
    
    public static void UseItemQuest(LittleEndianAccessor slea, MapleClient c) {
        short slot = slea.readShort();
        int itemId = slea.readInt();
        Item item = c.getPlayer().getInventory(MapleInventoryType.ETC).getItem(slot);
        int qid = slea.readInt();
        MapleQuest quest = MapleQuest.getInstance(qid);
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        Pair questItemInfo = null;
        boolean found = false;
        for (Item i : c.getPlayer().getInventory(MapleInventoryType.ETC)) {
            if (i.getItemId() / 10000 == 422) {
                questItemInfo = ii.questItemInfo(i.getItemId());
                if ((questItemInfo != null) && (((Integer) questItemInfo.getLeft()).intValue() == qid) && (questItemInfo.getRight() != null) && (((List) questItemInfo.getRight()).contains(Integer.valueOf(itemId)))) {
                    found = true;
                    break;
                }
            }
        }
        if ((quest != null) && (found) && (item != null) && (item.getQuantity() > 0) && (item.getItemId() == itemId)) {
            int newData = slea.readInt();
            MapleQuestStatus stats = c.getPlayer().getQuestNoAdd(quest);
            if ((stats != null) && (stats.getStatus() == 1)) {
                stats.setCustomData(String.valueOf(newData));
                c.getPlayer().updateQuest(stats, true);
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.ETC, slot, (short) 1, false);
            }
        }
    }
    
    public static void RPSGame(LittleEndianAccessor slea, MapleClient c) {
        if ((slea.available() == 0L) || (c.getPlayer() == null) || (c.getPlayer().getMap() == null) || (!c.getPlayer().getMap().containsNPC(9000019))) {
            if ((c.getPlayer() != null) && (c.getPlayer().getRPS() != null)) {
                c.getPlayer().getRPS().dispose(c);
            }
            return;
        }
        byte mode = slea.readByte();
        switch (mode) {
            case 0:
            case 5:
                if (c.getPlayer().getRPS() != null) {
                    c.getPlayer().getRPS().reward(c);
                }
                if (c.getPlayer().getMeso() >= 1000) {
                    c.getPlayer().setRPS(new RockPaperScissors(c, mode));
                } else {
                    c.getSession().write(MaplePacketCreator.getRPSMode((byte) 8, -1, -1, -1));
                }
                break;
            case 1:
                if ((c.getPlayer().getRPS() != null) && (c.getPlayer().getRPS().answer(c, slea.readByte()))) {
                    break;
                }
                c.getSession().write(MaplePacketCreator.getRPSMode((byte) 13, -1, -1, -1));
                break;
            case 2:
                if ((c.getPlayer().getRPS() != null) && (c.getPlayer().getRPS().timeOut(c))) {
                    break;
                }
                c.getSession().write(MaplePacketCreator.getRPSMode((byte) 13, -1, -1, -1));
                break;
            case 3:
                if ((c.getPlayer().getRPS() != null) && (c.getPlayer().getRPS().nextRound(c))) {
                    break;
                }
                c.getSession().write(MaplePacketCreator.getRPSMode((byte) 13, -1, -1, -1));
                break;
            case 4:
                if (c.getPlayer().getRPS() != null) {
                    c.getPlayer().getRPS().dispose(c);
                } else {
                    c.getSession().write(MaplePacketCreator.getRPSMode((byte) 13, -1, -1, -1));
                }
        }
    }
    
    public static void OpenQuickMoveNpc(LittleEndianAccessor slea, MapleClient c) {
        int npcid = slea.readInt();
        if ((c.getPlayer().hasBlockedInventory()) || (c.getPlayer().isInBlockedMap()) || (c.getPlayer().getLevel() < 10)) {
            c.getPlayer().dropMessage(-1, "您当前已经和1个NPC对话了. 如果不是请输入 @ea 命令进行解卡。");
            return;
        }
        for (MapleQuickMove pn : MapleQuickMove.values()) {
            if (pn.getNpcId() == npcid) {
                NPCScriptManager.getInstance().start(c, npcid);
                break;
            }
        }
    }
}
