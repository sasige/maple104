package client.messages.commands;

import client.*;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryType;
import client.messages.CommandProcessorUtil;
import constants.GameConstants;
import constants.ServerConstants.PlayerGMRank;
import handling.channel.ChannelServer;
import handling.world.World;
import org.apache.log4j.Logger;
import scripting.EventInstanceManager;
import scripting.EventManager;
import server.MapleCarnivalChallenge;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MapleShopFactory;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.life.MapleMonster;
import server.maps.MapleMap;
import tools.MaplePacketCreator;
import tools.StringUtil;

public class GMCommand {

    private static final Logger log = Logger.getLogger(GMCommand.class);

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.GM;
    }

    public static class UnbanIP extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "[用法] !unbanip <IGN>");
                return 0;
            }
            byte ret = MapleClient.unbanIPMacs(splitted[1]);
            if (ret == -2) {
                c.getPlayer().dropMessage(6, "[UnbanIP] 数据库查询出错.");
            } else if (ret == -1) {
                c.getPlayer().dropMessage(6, "[UnbanIP] 输入的角色不存在.");
            } else if (ret == 0) {
                c.getPlayer().dropMessage(6, "[UnbanIP] 不存在具有该字符的IP或Mac!");
            } else if (ret == 1) {
                c.getPlayer().dropMessage(6, "[UnbanIP] IP / Mac  - 其中一个被发现并且没有被禁止。");
            } else if (ret == 2) {
                c.getPlayer().dropMessage(6, "[UnbanIP] IP和Mac都是禁止的。");
            }
            if (ret > 0) {
                return 1;
            }
            return 0;
        }
    }

    public static class UnBan extends CommandExecute {

        protected boolean hellban = false;

        private String getCommand() {
            if (this.hellban) {
                return "UnHellBan";
            }
            return "UnBan";
        }

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "[用法] !" + getCommand() + " <IGN>");
                return 0;
            }
            byte ret;
            if (this.hellban) {
                ret = MapleClient.unHellban(splitted[1]);
            } else {
                ret = MapleClient.unban(splitted[1]);
            }
            if (ret == -2) {
                c.getPlayer().dropMessage(6, "[" + getCommand() + "] 数据库查询出错.");
                return 0;
            }
            if (ret == -1) {
                c.getPlayer().dropMessage(6, "[" + getCommand() + "] 角色不存在.");
                return 0;
            }
            c.getPlayer().dropMessage(6, "[" + getCommand() + "] 已经成功解除封停!");
            GMCommand.log.info("[命令] 管理员 " + c.getPlayer().getName() + " 将玩家 " + splitted[1] + " 解除封号.");

            byte ret_ = MapleClient.unbanIPMacs(splitted[1]);
            if (ret_ == -2) {
                c.getPlayer().dropMessage(6, "[UnbanIP] 数据库查询出错.");
            } else if (ret_ == -1) {
                c.getPlayer().dropMessage(6, "[UnbanIP] 输入的角色不存在.");
            } else if (ret_ == 0) {
                c.getPlayer().dropMessage(6, "[UnbanIP] No IP or Mac with that character exists!");
            } else if (ret_ == 1) {
                c.getPlayer().dropMessage(6, "[UnbanIP] IP/Mac -- one of them was found and unbanned.");
            } else if (ret_ == 2) {
                c.getPlayer().dropMessage(6, "[UnbanIP] Both IP and Macs were unbanned.");
            }
            return ret_ > 0 ? 1 : 0;
        }
    }

    public static class UnB extends GMCommand.UnBan {
    }

    public static class UnHellBan extends GMCommand.UnBan {

        public UnHellBan() {
            this.hellban = true;
        }
    }

    public static class UnHellB extends GMCommand.UnHellBan {
    }

    public static class HellBan extends InternCommand.Ban {

        public HellBan() {
            this.hellban = true;
        }
    }

    public static class HellB extends GMCommand.HellBan {
    }

    public static class TDrops extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().getMap().toggleDrops();
            return 1;
        }
    }

    public static class BanIP extends InternCommand.Ban {

        public BanIP() {
            this.ipBan = true;
        }
    }

    public static class TempBanIP extends InternCommand.TempBan {

        public TempBanIP() {
            this.ipBan = true;
        }
    }

    public static class 我登陆游戏的IP extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().dropMessage(5, "IP: " + c.getSession().getRemoteAddress().toString().split(":")[0]);
            return 1;
        }
    }

    public static class Y extends GMCommand.Yellow {
    }

    public static class Yellow extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            int range = -1;
            if (splitted[1].equals("m")) {
                range = 0;
            } else if (splitted[1].equals("c")) {
                range = 1;
            } else if (splitted[1].equals("w")) {
                range = 2;
            }
            if (range == -1) {
                range = 2;
            }
            byte[] packet = MaplePacketCreator.yellowChat(new StringBuilder().append(splitted[0].equals("!y") ? new StringBuilder().append("[").append(c.getPlayer().getName()).append("] ").toString() : "").append(StringUtil.joinStringFrom(splitted, 2)).toString());
            if (range == 0) {
                c.getPlayer().getMap().broadcastMessage(packet);
            } else if (range == 1) {
                ChannelServer.getInstance(c.getChannel()).broadcastPacket(packet);
            } else if (range == 2) {
                World.Broadcast.broadcastMessage(packet);
            }
            return 1;
        }
    }

    public static class Notice extends CommandExecute {

        protected static int getNoticeType(String typestring) {
            if (typestring.equals("n")) {
                return 0;
            }
            if (typestring.equals("p")) {
                return 1;
            }
            if (typestring.equals("l")) {
                return 2;
            }
            if (typestring.equals("nv")) {
                return 5;
            }
            if (typestring.equals("v")) {
                return 5;
            }
            if (typestring.equals("b")) {
                return 6;
            }
            return -1;
        }

        @Override
        public int execute(MapleClient c, String[] splitted) {
            int joinmod = 1;
            int range = -1;
            if (splitted[1].equals("m")) {
                range = 0;
            } else if (splitted[1].equals("c")) {
                range = 1;
            } else if (splitted[1].equals("w")) {
                range = 2;
            }

            int tfrom = 2;
            if (range == -1) {
                range = 2;
                tfrom = 1;
            }
            int type = getNoticeType(splitted[tfrom]);
            if (type == -1) {
                type = 0;
                joinmod = 0;
            }
            StringBuilder sb = new StringBuilder();
            if (splitted[tfrom].equals("nv")) {
                sb.append("[Notice]");
            } else {
                sb.append("");
            }
            joinmod += tfrom;
            sb.append(StringUtil.joinStringFrom(splitted, joinmod));

            byte[] packet = MaplePacketCreator.serverNotice(type, sb.toString());
            if (range == 0) {
                c.getPlayer().getMap().broadcastMessage(packet);
            } else if (range == 1) {
                ChannelServer.getInstance(c.getChannel()).broadcastPacket(packet);
            } else if (range == 2) {
                World.Broadcast.broadcastMessage(packet);
            }
            return 1;
        }
    }

    public static class 重置NPC extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().getMap().resetNPCs();
            return 1;
        }
    }

    public static class KillMonster extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleMap map = c.getPlayer().getMap();
            int targetId = Integer.parseInt(splitted[1]);
            MapleMonster monster = map.getMonsterByOid(targetId);
            if (monster != null) {
                map.killMonster(monster, c.getPlayer(), false, false, (byte) 1);
            }
            return 1;
        }
    }

    public static class czmob extends GMCommand.重置怪物 {

    }

    public static class 重置怪物 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().getMap().killAllMonsters(false);
            return 1;
        }
    }

    public static class StartInstance extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (c.getPlayer().getEventInstance() != null) {
                c.getPlayer().dropMessage(5, "You are in one");
            } else if (splitted.length > 2) {
                EventManager em = c.getChannelServer().getEventSM().getEventManager(splitted[1]);
                if ((em == null) || (em.getInstance(splitted[2]) == null)) {
                    c.getPlayer().dropMessage(5, "Not exist");
                } else {
                    em.getInstance(splitted[2]).registerPlayer(c.getPlayer());
                }
            } else {
                c.getPlayer().dropMessage(5, "!startinstance [eventmanager] [eventinstance]");
            }
            return 1;
        }
    }

    public static class 谁在这个地图 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            StringBuilder builder = new StringBuilder("当前地图玩家: ").append(c.getPlayer().getMap().getCharactersThreadsafe().size()).append(" 人. ");
            for (MapleCharacter chr : c.getPlayer().getMap().getCharactersThreadsafe()) {
                if (builder.length() > 150) {
                    builder.setLength(builder.length() - 2);
                    c.getPlayer().dropMessage(6, builder.toString());
                    builder = new StringBuilder();
                }
                builder.append(MapleCharacterUtil.makeMapleReadable(chr.getName()));
                builder.append(", ");
            }
            builder.setLength(builder.length() - 2);
            c.getPlayer().dropMessage(6, builder.toString());
            return 1;
        }
    }

    public static class LeaveInstance extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (c.getPlayer().getEventInstance() == null) {
                c.getPlayer().dropMessage(5, "You are not in one");
            } else {
                c.getPlayer().getEventInstance().unregisterPlayer(c.getPlayer());
            }
            return 1;
        }
    }

    //ListInstanceProperty (原名)
    public static class 查看活动脚本 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            EventManager em = c.getChannelServer().getEventSM().getEventManager(splitted[1]);
            if ((em == null) || (em.getInstances().size() <= 0)) {
                c.getPlayer().dropMessage(5, "none");
            } else {
                for (EventInstanceManager eim : em.getInstances()) {
                    c.getPlayer().dropMessage(5, "活动脚本: " + eim.getName() + ", eventManager: " + em.getName() + " iprops: " + eim.getProperty(splitted[2]) + ", eprops: " + em.getProperty(splitted[2]));
                }
            }
            return 0;
        }
    }

    public static class SetInstanceProperty extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            EventManager em = c.getChannelServer().getEventSM().getEventManager(splitted[1]);
            if ((em == null) || (em.getInstances().size() <= 0)) {
                c.getPlayer().dropMessage(5, "none");
            } else {
                em.setProperty(splitted[2], splitted[3]);
                for (EventInstanceManager eim : em.getInstances()) {
                    eim.setProperty(splitted[2], splitted[3]);
                }
            }
            return 1;
        }
    }

    public static class DisposeClones extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().dropMessage(6, c.getPlayer().getCloneSize() + " clones disposed.");
            c.getPlayer().disposeClones();
            return 1;
        }
    }

    public static class CloneMe extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().cloneLook();
            return 1;
        }
    }

    public static class Disease extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 3) {
                c.getPlayer().dropMessage(6, "!disease <type> [charname] <level> where type = SEAL/DARKNESS/WEAKEN/STUN/CURSE/POISON/SLOW/SEDUCE/REVERSE/ZOMBIFY/POTION/SHADOW/BLIND/FREEZE/POTENTIAL");
                return 0;
            }
            int type = 0;
            if (splitted[1].equalsIgnoreCase("SEAL")) {
                type = 120;
            } else if (splitted[1].equalsIgnoreCase("DARKNESS")) {
                type = 121;
            } else if (splitted[1].equalsIgnoreCase("WEAKEN")) {
                type = 122;
            } else if (splitted[1].equalsIgnoreCase("STUN")) {
                type = 123;
            } else if (splitted[1].equalsIgnoreCase("CURSE")) {
                type = 124;
            } else if (splitted[1].equalsIgnoreCase("POISON")) {
                type = 125;
            } else if (splitted[1].equalsIgnoreCase("SLOW")) {
                type = 126;
            } else if (splitted[1].equalsIgnoreCase("SEDUCE")) {
                type = 128;
            } else if (splitted[1].equalsIgnoreCase("REVERSE")) {
                type = 132;
            } else if (splitted[1].equalsIgnoreCase("ZOMBIFY")) {
                type = 133;
            } else if (splitted[1].equalsIgnoreCase("POTION")) {
                type = 134;
            } else if (splitted[1].equalsIgnoreCase("SHADOW")) {
                type = 135;
            } else if (splitted[1].equalsIgnoreCase("BLIND")) {
                type = 136;
            } else if (splitted[1].equalsIgnoreCase("FREEZE")) {
                type = 137;
            } else if (splitted[1].equalsIgnoreCase("POTENTIAL")) {
                type = 138;
            } else {
                c.getPlayer().dropMessage(6, "!disease <type> [charname] <level> where type = SEAL/DARKNESS/WEAKEN/STUN/CURSE/POISON/SLOW/SEDUCE/REVERSE/ZOMBIFY/POTION/SHADOW/BLIND/FREEZE/POTENTIAL");
                return 0;
            }
            if (splitted.length == 4) {
                MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[2]);
                if (victim == null) {
                    c.getPlayer().dropMessage(5, "无法找到角色.");
                    return 0;
                }
                victim.disease(type, CommandProcessorUtil.getOptionalIntArg(splitted, 3, 1));
            } else {
                for (MapleCharacter victim : c.getPlayer().getMap().getCharactersThreadsafe()) {
                    victim.disease(type, CommandProcessorUtil.getOptionalIntArg(splitted, 3, 1));
                }
            }
            return 1;
        }
    }

    public static class 找玩家位置 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim == null) {
                c.getPlayer().dropMessage(5, "没有找到 " + splitted[1] + " 玩家.");
                return 0;
            }
            victim.getMap().broadcastMessage(MaplePacketCreator.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted, 2), victim.isGM(), 0));

            return 1;
        }
    }

    public static class SpeakMega extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            World.Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(3, victim == null ? c.getChannel() : victim.getClient().getChannel(), victim.getName() + " : " + StringUtil.joinStringFrom(splitted, 2), true));
            return 1;
        }
    }

    public static class KillMap extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            for (MapleCharacter map : c.getPlayer().getMap().getCharactersThreadsafe()) {
                if ((map != null) && (!map.isGM())) {
                    map.getStat().setHp(0, map);
                    map.getStat().setMp(0, map);
                    map.updateSingleStat(MapleStat.HP, 0);
                    map.updateSingleStat(MapleStat.MP, 0);
                }
            }
            return 1;
        }
    }

    public static class 锁定道具 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 3) {
                c.getPlayer().dropMessage(6, "需要输入 <角色名字> <道具ID>");
                return 0;
            }
            MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (chr == null) {
                c.getPlayer().dropMessage(6, "输入的角色不存在或者角色不在线或者不在这个频道.");
                return 0;
            }
            int itemid = Integer.parseInt(splitted[2]);
            MapleInventoryType type = GameConstants.getInventoryType(itemid);
            for (Item item : chr.getInventory(type).listById(itemid)) {
                item.setFlag((short) (byte) (item.getFlag() | ItemFlag.LOCK.getValue()));
                chr.getClient().getSession().write(MaplePacketCreator.updateSpecialItemUse(item, type.getType(), item.getPosition(), true, chr));
            }
            if (type == MapleInventoryType.EQUIP) {
                type = MapleInventoryType.EQUIPPED;
                for (Item item : chr.getInventory(type).listById(itemid)) {
                    item.setFlag((short) (byte) (item.getFlag() | ItemFlag.LOCK.getValue()));
                }
            }

            c.getPlayer().dropMessage(6, "已经成功的将ID为: " + splitted[2] + " 的所有道具锁定,执行角色为: " + splitted[1] + ".");
            return 1;
        }
    }

    public static class 删除道具 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 3) {
                c.getPlayer().dropMessage(6, "需要输入 <角色名字> <道具ID>");
                return 0;
            }
            MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (chr == null) {
                c.getPlayer().dropMessage(6, "输入的角色不存在或者角色不在线或者不在这个频道.");
                return 0;
            }
            chr.removeAll(Integer.parseInt(splitted[2]), false, false);
            c.getPlayer().dropMessage(6, "已经成功的将ID为: " + splitted[2] + " 的所有道具从角色: " + splitted[1] + " 的背包中删除.");
            return 1;
        }
    }

    public static class ScheduleEvent extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleEventType type = MapleEventType.getByString(splitted[1]);
            if (type == null) {
                StringBuilder sb = new StringBuilder("Wrong syntax: ");
                for (MapleEventType t : MapleEventType.values()) {
                    sb.append(t.name()).append(",");
                }
                c.getPlayer().dropMessage(5, sb.toString().substring(0, sb.toString().length() - 1));
                return 0;
            }
            String msg = MapleEvent.scheduleEvent(type, c.getChannelServer());
            if (msg.length() > 0) {
                c.getPlayer().dropMessage(5, msg);
                return 0;
            }
            return 1;
        }
    }

    public static class StartEvent extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (c.getChannelServer().getEvent() == c.getPlayer().getMapId()) {
                MapleEvent.setEvent(c.getChannelServer(), false);
                c.getPlayer().dropMessage(5, "开启或关闭活动脚本成功.");
                return 1;
            }
            c.getPlayer().dropMessage(5, "!scheduleevent must've been done first, and you must be in the event map.");
            return 0;
        }
    }

    public static class SetEvent extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleEvent.onStartEvent(c.getPlayer());
            return 1;
        }
    }

    public static class StartAutoEvent extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            EventManager em = c.getChannelServer().getEventSM().getEventManager("AutomatedEvent");
            if (em != null) {
                em.scheduleRandomEvent();
            }
            return 1;
        }
    }

    public static class Hide extends CommandExecute {
        public int execute(MapleClient c, String[] splitted) {
            SkillFactory.getSkill(9001004).getEffect(1).applyTo(c.getPlayer());
            c.getPlayer().dropMessage(6, "管理员隐藏 = 开启 \r\n 解除请输入!unhide");
            return 0;
        }
    }

    public static class 刷级 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().setLevel(Short.parseShort(splitted[1]));
            c.getPlayer().levelUp();
            if (c.getPlayer().getExp() < 0) {
                c.getPlayer().gainExp(-c.getPlayer().getExp(), false, false, true);
            }
            return 1;
        }
    }

    public static class 刷 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            int itemId = Integer.parseInt(splitted[1]);
            short quantity = (short) CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1);
            if (!c.getPlayer().isAdmin()) {
                for (int i : GameConstants.itemBlock) {
                    if (itemId == i) {
                        c.getPlayer().dropMessage(5, "对不起，您当前管理权限无法刷出这个装备.");
                        return 0;
                    }
                }
            }
            if (!c.getPlayer().isSuperGM()) {
                switch (itemId / 10000) {
                    case 202:
                    case 204:
                    case 229:
                    case 251:
                    case 253:
                    case 400:
                    case 401:
                    case 402:
                    case 403:
                    case 413:
                    case 417:
                    case 425:
                    case 431:
                    case 506:
                        c.getPlayer().dropMessage(5, "对不起，您当前管理权限无法刷出这个装备.");
                        return 0;
                }
            }
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            //  if (GameConstants.isPet(itemId)) {
            // c.getPlayer().dropMessage(5, "宠物道具请通过商城购买.");
            //  } else
            if (!ii.itemExists(itemId)) {
                c.getPlayer().dropMessage(5, itemId + " 这个道具不存在.");
            } else {
                short flag = (short) ItemFlag.LOCK.getValue();
                Item item;
                if (GameConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP) {
                    item = ii.randomizeStats((Equip) ii.getEquipById(itemId));
                } else {
                    item = new Item(itemId, (short) 0, !c.getPlayer().isSuperGM() ? 1 : quantity, (short) 0);
                }
                if (!c.getPlayer().isSuperGM()) {
                    item.setFlag(flag);
                }
                if (!c.getPlayer().isAdmin()) {
                    item.setOwner(c.getPlayer().getName());
                }
                item.setGMLog(c.getPlayer().getName() + " 使用命令 !刷");
                MapleInventoryManipulator.addbyItem(c, item);
                GMCommand.log.info("[命令] 管理员 " + c.getPlayer().getName() + " 刷道具: " + item.getItemId() + " 数量: " + item.getQuantity() + " 名称: " + ii.getName(itemId));
            }
            return 1;
        }
    }

    public static class 获得5亿经验 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (c.getPlayer().getLevel() < 200) {
                c.getPlayer().gainExp(500000000, true, false, true);
            }
            return 1;
        }
    }

    public static class Shop extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleShopFactory shop = MapleShopFactory.getInstance();
            int shopId = Integer.parseInt(splitted[1]);
            if (shop.getShop(shopId) != null) {
                shop.getShop(shopId).sendShop(c);
            }
            return 1;
        }
    }

    public static class 转职 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "!转职 <职业ID>");
                return 0;
            }
            int jobId = Integer.parseInt(splitted[1]);
            if ((jobId >= 2003) && (jobId <= 2412) && (!c.getPlayer().isAdmin())) {
                c.getPlayer().dropMessage(5, "暂不开放此职业的转职.");
                return 0;
            }
            if (MapleCarnivalChallenge.getJobNameById(jobId).length() == 0) {
                c.getPlayer().dropMessage(5, "输入的职业id无效.");
                return 0;
            }
            c.getPlayer().changeJob(Integer.parseInt(splitted[1]));
            return 1;
        }
    }

    public static class 技能点 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().setRemainingSp(CommandProcessorUtil.getOptionalIntArg(splitted, 1, 1));
            c.getSession().write(MaplePacketCreator.updateSp(c.getPlayer(), false));
            return 1;
        }
    }

    public static class 无敌模式 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            if (player.isInvincible()) {
                player.setInvincible(false);
                player.dropMessage(6, "无敌模式已关闭.");
            } else {
                player.setInvincible(true);
                player.dropMessage(6, "无敌模式已开启.");
            }
            return 1;
        }
    }

    public static class 人气 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "用法: !人气 <玩家名字> <要加人气的数量>");
                return 0;
            }
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            int fame = 0;
            try {
                fame = Integer.parseInt(splitted[2]);
            } catch (NumberFormatException nfe) {
                c.getPlayer().dropMessage(6, "输入的数字无效...");
                return 0;
            }
            if ((victim != null) && (player.allowedToTarget(victim))) {
                victim.addFame(fame);
                victim.updateSingleStat(MapleStat.人气, victim.getFame());
                GMCommand.log.info("[命令] 管理员 " + player.getName() + " 给玩家 " + victim.getName() + " 加人气 " + fame + " 点.");
            }
            return 1;
        }
    }

    public static class GetSkill extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            Skill skill = SkillFactory.getSkill(Integer.parseInt(splitted[1]));
            byte level = (byte) CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1);
            byte masterlevel = (byte) CommandProcessorUtil.getOptionalIntArg(splitted, 3, 1);

            if (level > skill.getMaxLevel()) {
                level = (byte) skill.getMaxLevel();
            }
            if (masterlevel > skill.getMaxLevel()) {
                masterlevel = (byte) skill.getMaxLevel();
            }
            c.getPlayer().changeSkillLevel(skill, level, masterlevel);
            return 1;
        }
    }

    public static class 查看当前地图信息 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().dropMessage(6, "当前地图信息: ID " + c.getPlayer().getMapId() + " 名字 " + c.getPlayer().getMap().getMapName() + " 当前坐标: X " + c.getPlayer().getPosition().x + " Y " + c.getPlayer().getPosition().y);
            return 1;
        }
    }
}