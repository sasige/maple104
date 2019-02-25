package client.messages.commands;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleStat;
import client.SkillFactory;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryType;
import client.messages.CommandProcessorUtil;
import constants.GameConstants;
import constants.ServerConstants;
import database.DatabaseConnection;
import handling.channel.ChannelServer;
import handling.world.World;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.Logger;
import scripting.NPCScriptManager;
import server.MapleCarnivalChallenge;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MaplePortal;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MapleNPC;
import server.life.PlayerNPC;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import tools.MaplePacketCreator;
import tools.StringUtil;


public class DonatorCommand {
        private static final Logger log = Logger.getLogger(DonatorCommand.class);

    public static ServerConstants.PlayerGMRank getPlayerLevelRequired() {
        return ServerConstants.PlayerGMRank.DONATOR;
    }
    
    public static class Maplen extends CommandExecute {
        @Override
        public int execute(MapleClient c, String[] splitted) {
            int itemId = 5120008;
            String maples = "MapleWing";
            int spls = splitted.length;
            int times = 30;
            if (spls < 2) {
                c.getPlayer().dropMessage(6, "用法: !Maplen <道具ID/cs/m c w /公告信息> <公告信息/道具ID/cs 道具ID> <公告信息>");
                return 0;
            }
            
            int range = 0;
            switch (splitted[1]) {
                case "m":
                    range = 1;
                    break;
                case "c":
                    range = 2;
                    break;
                case "w":
                    range = 3;
                    break;
                case "s":
                    range = 4;
                    break;
            }
            
            int nus = 1;
            int bes = 2;
            if (range > 0) {
                nus = 2;
                bes = 3;
            } else if(range == 4) {
                nus = 3;
                bes = 4;
            }
            if (spls == 2) {
                maples = StringUtil.joinStringFrom(splitted, 1);
            } else if ((spls == 3)&&(range > 0)) {
                maples = StringUtil.joinStringFrom(splitted, 2);
            } else if (spls >= 3) {
                switch (splitted[nus]) {
                    case "cs":
                        itemId = Integer.parseInt(splitted[bes]);
                        if (spls == 5) {
                            maples = StringUtil.joinStringFrom(splitted, 4);
                        }
                        break;
                    case "mg":
                        itemId = 5121009;
                        maples = StringUtil.joinStringFrom(splitted, bes);
                        break;
                    case "yh":
                        itemId = 5120001;
                        maples = StringUtil.joinStringFrom(splitted, bes);
                        break;
                    case "fy":
                        itemId = 5120008;
                        maples = StringUtil.joinStringFrom(splitted, bes);
                        break;
                    default:
                        itemId = Integer.parseInt(splitted[nus]);
                        maples = StringUtil.joinStringFrom(splitted, bes);
                        break;
                }
                
            }
            
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            if (!ii.itemExists(itemId)) {
                c.getPlayer().dropMessage(5, itemId + " 这个道具不存在.");
                return 0;
            }
            if (!ii.isFloatCashItem(itemId)) {
                c.getPlayer().dropMessage(5, itemId + " 不具有漂浮公告的效果.");
                return 0;
            }
            if (range == 2) {
                ChannelServer.getInstance(c.getChannel()).startMapEffect(maples, itemId, times);
            } else if (range == 3){
                MaplePacketCreator.startMapEffect(maples, itemId, true);
               // World.Broadcast.startMapEffect(maples, itemId, times);
            } else if (range == 4) {
                MaplePacketCreator.startMapEffect(maples, itemId, true);
            } else {
                c.getPlayer().getMap().startMapEffect(maples, itemId, times);
            }
            return 1;
        }
    }
    
    public static class Ces extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            int itemId = 5121009;
            String ns = "MapleWing";
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "用法: !ces <道具ID> <公告信息>");
                return 0;
            }
            if (splitted.length == 2){
                itemId = Integer.parseInt(splitted[1]);
            } else if (splitted.length == 3) {
                itemId = Integer.parseInt(splitted[1]);
                ns = StringUtil.joinStringFrom(splitted, 2);
            }
           // int itemId = Integer.parseInt(splitted[1]);
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            if (!ii.isFloatCashItem(itemId)) {
                c.getPlayer().dropMessage(5, itemId + " 不具有漂浮公告的效果.");
                return 0;
            }
            World.Broadcast.startMapEffect(ns, itemId);
           // MaplePacketCreator.startMapEffect(ns, itemId, true);
            c.getPlayer().dropMessage(6, "当前测试类型ID"+ itemId);
            return 1;
        }
    }
    
    public static class item extends CommandExecute {

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
                item.setGMLog(c.getPlayer().getName() + " 使用命令 !item");
                MapleInventoryManipulator.addbyItem(c, item);
                DonatorCommand.log.info("[命令] 管理员 " + c.getPlayer().getName() + " 刷道具: " + item.getItemId() + " 数量: " + item.getQuantity() + " 名称: " + ii.getName(itemId));
            }
            return 1;
        }
    }
    
    public static class level extends CommandExecute {

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
    
     public static class lv extends CommandExecute {

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
     
     public static class onpc extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
        if (splitted.length < 2) {
            c.getPlayer().dropMessage(1, "用法： !onpc <npcid> <型号(默认为0)>");
                return 0;
            }
            int npcid = Integer.parseInt(splitted[1]);
            int moder = 0;
            if (splitted.length == 3) {
                moder = Integer.parseInt(splitted[2]);
            }
            MapleNPC npc = MapleLifeFactory.getNPC(npcid);
            if (npc != null && !npc.getName().equalsIgnoreCase("MISSINGNO")) {
               NPCScriptManager.getInstance().start(c, npcid , moder);
            } else {
            c.getPlayer().dropMessage(5, "未找到NPC ID为" + npcid + "型号为" + moder);
           }
            return 1;
        }
    }
     
    public static class job extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "!job <职业ID>");
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

    public static class sp extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().setRemainingSp(CommandProcessorUtil.getOptionalIntArg(splitted, 1, 1));
            c.getSession().write(MaplePacketCreator.updateSp(c.getPlayer(), false));
            return 1;
        }
    }
    public static class mapwz extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().dropMessage(6, "当前地图信息: ID " + c.getPlayer().getMapId() + " 名字 " + c.getPlayer().getMap().getMapName() + " 当前坐标: X " + c.getPlayer().getPosition().x + " Y " + c.getPlayer().getPosition().y);
            return 1;
        }
    }
    public static class skill extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "用法: !技能 <技能ID> <技能等级>");
                return 0;
            }
            SkillFactory.getSkill(Integer.parseInt(splitted[1])).getEffect(Integer.parseInt(splitted[2])).applyTo(c.getPlayer());
            return 1;
        }
    }
    public static class pwjnpc extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            try {
                c.getPlayer().dropMessage(6, "创立玩家NPC...");
                MapleCharacter chhr = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
                if (chhr == null) {
                    c.getPlayer().dropMessage(6, splitted[1] + " 不在线");
                    return 0;
                }
                PlayerNPC npc = new PlayerNPC(chhr, Integer.parseInt(splitted[2]), c.getPlayer().getMap(), c.getPlayer());
                npc.addToServer();
                c.getPlayer().dropMessage(6, "做玩家NPC 成功！");
            } catch (Exception e) {
                c.getPlayer().dropMessage(6, "NPC失败... : " + e.getMessage());
            }
            return 1;
        }
    }
    public static class pmob extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "用法: !制作永久怪物 <怪物ID> <刷新时间 默认按秒计算>");
                return 0;
            }
            int mobid = Integer.parseInt(splitted[1]);
            int mobTime = Integer.parseInt(splitted[2]);
            if (splitted[2] == null) {
                mobTime = 1;
            }
            MapleMonster mob = MapleLifeFactory.getMonster(mobid);
            if (mob != null) {
                int xpos = c.getPlayer().getPosition().x;
                int ypos = c.getPlayer().getPosition().y;
                int fh = c.getPlayer().getMap().getFootholds().findBelow(c.getPlayer().getPosition()).getId();
                mob.setPosition(c.getPlayer().getPosition());
                mob.setCy(ypos);
                mob.setRx0(xpos + 50);
                mob.setRx1(xpos - 50);
                mob.setFh(fh);
                try {
                    PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("INSERT INTO spawns ( idd, f, fh, cy, rx0, rx1, type, x, y, mid, mobtime ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )");
                    ps.setInt(1, mobid);
                    ps.setInt(2, 0);
                    ps.setInt(3, fh);
                    ps.setInt(4, ypos);
                    ps.setInt(5, xpos + 50);
                    ps.setInt(6, xpos - 50);
                    ps.setString(7, "m");
                    ps.setInt(8, xpos);
                    ps.setInt(9, ypos);
                    ps.setInt(10, c.getPlayer().getMapId());
                    ps.setInt(11, mobTime);
                    ps.executeUpdate();
                } catch (SQLException e) {
                    c.getPlayer().dropMessage(6, "保存怪物信息到数据库中出现错误.");
                }
                c.getPlayer().getMap().addMonsterSpawn(mob, mobTime, (byte) -1, null);
            } else {
                c.getPlayer().dropMessage(6, "你应该输入一个正确的 怪物-Id.");
                return 0;
            }
            return 1;
        }
    }
    public static class KillAllmob extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            MapleMap map = c.getPlayer().getMap();
            double range = (1.0D / 0.0D);
            if (splitted.length > 1) {
                int irange = Integer.parseInt(splitted[1]);
                if (splitted.length <= 2) {
                    range = irange * irange;
                } else {
                    map = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted[2]));
                }
            }
            if (map == null) {
                c.getPlayer().dropMessage(6, "输入的地图不存在.");
                return 0;
            }

            for (MapleMapObject monstermo : map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(new MapleMapObjectType[]{MapleMapObjectType.MONSTER}))) {
                MapleMonster mob = (MapleMonster) monstermo;
                if ((!mob.getStats().isBoss()) || (mob.getStats().isPartyBonus()) || (c.getPlayer().isGM())) {
                    map.killMonster(mob, c.getPlayer(), false, false, (byte) 1);
                }
            }
            return 1;
        }
    }
    public static class map extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            ;
            try {
                MapleMap target = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted[1]));
                if (target == null) {
                    c.getPlayer().dropMessage(6, "输入的地图不存在.");
                    return 0;
                }
                MapleMap from = c.getPlayer().getMap();
                for (MapleCharacter chr : from.getCharactersThreadsafe()) {
                    chr.changeMap(target, target.getPortal(0));
                }
            } catch (Exception e) {

                c.getPlayer().dropMessage(5, "错误: " + e.getMessage());
                return 0;
            }
            return 1;
        }
    }
    public static class gomap extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if ((victim != null) && (c.getPlayer().getGMLevel() >= victim.getGMLevel()) && (!victim.inPVP()) && (!c.getPlayer().inPVP())) {
                if (splitted.length == 2) {
                    c.getPlayer().changeMap(victim.getMap(), victim.getMap().findClosestSpawnpoint(victim.getTruePosition()));
                } else {
                    MapleMap target = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(Integer.parseInt(splitted[2]));
                    if (target == null) {
                        c.getPlayer().dropMessage(6, "输入的地图不存在.");
                        return 0;
                    }
                    MaplePortal targetPortal = null;
                    if (splitted.length > 3) {
                        try {
                            targetPortal = target.getPortal(Integer.parseInt(splitted[3]));
                        } catch (IndexOutOfBoundsException e) {
                            c.getPlayer().dropMessage(5, "Invalid portal selected.");
                        } catch (NumberFormatException a) {
                        }
                    }
                    if (targetPortal == null) {
                        targetPortal = target.getPortal(0);
                    }
                    victim.changeMap(target, targetPortal);
                }
            } else {
                try {
                    victim = c.getPlayer();
                    int ch = World.Find.findChannel(splitted[1]);
                    if (ch < 0) {
                        MapleMap target = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted[1]));
                        if (target == null) {
                            c.getPlayer().dropMessage(6, "输入的地图不存在.");
                            return 0;
                        }
                        MaplePortal targetPortal = null;
                        if (splitted.length > 2) {
                            try {
                                targetPortal = target.getPortal(Integer.parseInt(splitted[2]));
                            } catch (IndexOutOfBoundsException e) {
                                c.getPlayer().dropMessage(5, "Invalid portal selected.");
                            } catch (NumberFormatException a) {
                            }
                        }
                        if (targetPortal == null) {
                            targetPortal = target.getPortal(0);
                        }
                        c.getPlayer().changeMap(target, targetPortal);
                    } else {
                        victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(splitted[1]);
                        c.getPlayer().dropMessage(6, "正在切换频道，请等待...");
                        if (victim.getMapId() != c.getPlayer().getMapId()) {
                            MapleMap mapp = c.getChannelServer().getMapFactory().getMap(victim.getMapId());
                            c.getPlayer().changeMap(mapp, mapp.findClosestPortal(victim.getTruePosition()));
                        }
                        c.getPlayer().changeChannel(ch);
                    }
                } catch (Exception e) {
                    c.getPlayer().dropMessage(6, "出现错误: " + e.getMessage());
                    return 0;
                }
            }

            return 1;
        }
    }
    public static class zaixianrs extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            Map connected = World.getConnected();
            StringBuilder conStr = new StringBuilder("连接数量: ");
            boolean first = true;
            for (Iterator i$ = connected.keySet().iterator(); i$.hasNext();) {
                int i = ((Integer) i$.next()).intValue();
                if (!first) {
                    conStr.append(", ");
                } else {
                    first = false;
                }
                if (i == 0) {
                    conStr.append("总计: ");
                    conStr.append(connected.get(Integer.valueOf(i)));
                } else {
                    conStr.append("频道");
                    conStr.append(i);
                    conStr.append(": ");
                    conStr.append(connected.get(Integer.valueOf(i)));
                }
            }
            c.getPlayer().dropMessage(6, conStr.toString());
            return 1;
        }
    }
    public static class killplayer extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "用法: !杀死玩家 <list player names>");
                return 0;
            }
            MapleCharacter victim = null;
            for (int i = 1; i < splitted.length; i++) {
                try {
                    victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[i]);
                } catch (Exception e) {
                    c.getPlayer().dropMessage(6, "没有找到名字为: " + splitted[i] + " 的玩家.");
                }
                if ((player.allowedToTarget(victim)) && (player.getGMLevel() >= victim.getGMLevel())) {
                    victim.getStat().setHp(0, victim);
                    victim.getStat().setMp(0, victim);
                    victim.updateSingleStat(MapleStat.HP, 0);
                    victim.updateSingleStat(MapleStat.MP, 0);
                }
            }
            return 1;
        }
    }
    public static class mapid extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().dropMessage(5, "当前地图ID: " + c.getPlayer().getMap().getId());
            return 1;
        }
    }
    public static class pingdaozaixian extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().dropMessage(6, "频道在线: " + Integer.parseInt(splitted[1]) + ":");
            c.getPlayer().dropMessage(6, ChannelServer.getInstance(Integer.parseInt(splitted[1])).getPlayerStorage().getOnlinePlayers(true));
            return 1;
        }
    }
    public static class jsxx extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            StringBuilder builder = new StringBuilder();
            MapleCharacter other = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (other == null) {
                builder.append("输入的角色不存在...");
                c.getPlayer().dropMessage(6, builder.toString());
                return 0;
            }
            if (other.getClient().getLastPing() <= 0L) {
                other.getClient().sendPing();
            }
            builder.append(MapleClient.getLogMessage(other, ""));
            builder.append(" 坐标 ").append(other.getPosition().x);
            builder.append(" /").append(other.getPosition().y);

            builder.append(" || 血 : ");
            builder.append(other.getStat().getHp());
            builder.append(" /");
            builder.append(other.getStat().getCurrentMaxHp());

            builder.append(" || 蓝 : ");
            builder.append(other.getStat().getMp());
            builder.append(" /");
            builder.append(other.getStat().getCurrentMaxMp(other.getJob()));

            builder.append(" || BattleshipHP : ");
            builder.append(other.currentBattleshipHP());

            builder.append(" || 物理攻击 : ");
            builder.append(other.getStat().getTotalWatk());
            builder.append(" || 魔法攻击 : ");
            builder.append(other.getStat().getTotalMagic());
            builder.append(" || 最大攻击 : ");
            builder.append(other.getStat().getCurrentMaxBaseDamage());
            builder.append(" || 伤害% : ");
            builder.append(other.getStat().dam_r);
            builder.append(" || BOSS伤害% : ");
            builder.append(other.getStat().bossdam_r);
            builder.append(" || 爆击几率 : ");
            builder.append(other.getStat().passive_sharpeye_rate());
            builder.append(" || 暴击伤害 : ");
            builder.append(other.getStat().passive_sharpeye_percent());

            builder.append(" || 力量 : ");
            builder.append(other.getStat().getStr());
            builder.append(" || 敏捷 : ");
            builder.append(other.getStat().getDex());
            builder.append(" || 智力 : ");
            builder.append(other.getStat().getInt());
            builder.append(" || 运气 : ");
            builder.append(other.getStat().getLuk());

            builder.append(" || 全部力量 : ");
            builder.append(other.getStat().getTotalStr());
            builder.append(" || 全部敏捷 : ");
            builder.append(other.getStat().getTotalDex());
            builder.append(" || 全部智力 : ");
            builder.append(other.getStat().getTotalInt());
            builder.append(" || 全部智力 : ");
            builder.append(other.getStat().getTotalLuk());

            builder.append(" || 经验 : ");
            builder.append(other.getExp());
            builder.append(" || 金币 : ");
            builder.append(other.getMeso());

            builder.append(" || 是否组队 : ");
            builder.append(other.getParty() == null ? -1 : other.getParty().getId());

            builder.append(" || 是否交易: ");
            builder.append(other.getTrade() != null);
            builder.append(" || Latency: ");
            builder.append(other.getClient().getLatency());
            builder.append(" || PING: ");
            builder.append(other.getClient().getLastPing());
            builder.append(" || PONG: ");
            builder.append(other.getClient().getLastPong());
            builder.append(" || remoteAddress: ");

            other.getClient().DebugMessage(builder);

            c.getPlayer().dropMessage(6, builder.toString());
            return 1;
        }
    }
    
    public static class gotomap extends CommandExecute {

        private static final HashMap<String, Integer> gotomaps = new HashMap();

        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "用法: !gotomap <mapname>");
            } else if (gotomaps.containsKey(splitted[1])) {
                MapleMap target = c.getChannelServer().getMapFactory().getMap(((Integer) gotomaps.get(splitted[1])).intValue());
                if (target == null) {
                    c.getPlayer().dropMessage(6, "输入的地图不存在.");
                    return 0;
                }
                MaplePortal targetPortal = target.getPortal(0);
                c.getPlayer().changeMap(target, targetPortal);
            } else if (splitted[1].equals("查看")) {
                c.getPlayer().dropMessage(6, "!goto <地图名字>. 该命令可以传送的所有地图名字为:");
                StringBuilder sb = new StringBuilder();
                for (String s : gotomaps.keySet()) {
                    sb.append(s).append(", ");
                }
                c.getPlayer().dropMessage(6, sb.substring(0, sb.length() - 2));
            } else {
                c.getPlayer().dropMessage(6, "Invalid command syntax - Use !goto <location>. For a list of locations, use !goto locations.");
            }

            return 1;
        }

        static {
            gotomaps.put("gmmap", Integer.valueOf(180000000));
            gotomaps.put("southperry", Integer.valueOf(2000000));
            gotomaps.put("amherst", Integer.valueOf(1010000));
            gotomaps.put("henesys", Integer.valueOf(100000000));
            gotomaps.put("ellinia", Integer.valueOf(101000000));
            gotomaps.put("perion", Integer.valueOf(102000000));
            gotomaps.put("kerning", Integer.valueOf(103000000));
            gotomaps.put("harbor", Integer.valueOf(104000000));
            gotomaps.put("sleepywood", Integer.valueOf(105000000));
            gotomaps.put("florina", Integer.valueOf(120000300));
            gotomaps.put("orbis", Integer.valueOf(200000000));
            gotomaps.put("happyville", Integer.valueOf(209000000));
            gotomaps.put("elnath", Integer.valueOf(211000000));
            gotomaps.put("ludibrium", Integer.valueOf(220000000));
            gotomaps.put("aquaroad", Integer.valueOf(230000000));
            gotomaps.put("leafre", Integer.valueOf(240000000));
            gotomaps.put("mulung", Integer.valueOf(250000000));
            gotomaps.put("herbtown", Integer.valueOf(251000000));
            gotomaps.put("omegasector", Integer.valueOf(221000000));
            gotomaps.put("koreanfolktown", Integer.valueOf(222000000));
            gotomaps.put("newleafcity", Integer.valueOf(600000000));
            gotomaps.put("sharenian", Integer.valueOf(990000000));
            gotomaps.put("pianus", Integer.valueOf(230040420));
            gotomaps.put("horntail", Integer.valueOf(240060200));
            gotomaps.put("chorntail", Integer.valueOf(240060201));
            gotomaps.put("griffey", Integer.valueOf(240020101));
            gotomaps.put("manon", Integer.valueOf(240020401));
            gotomaps.put("zakum", Integer.valueOf(280030000));
            gotomaps.put("czakum", Integer.valueOf(280030001));
            gotomaps.put("papulatus", Integer.valueOf(220080001));
            gotomaps.put("showatown", Integer.valueOf(801000000));
            gotomaps.put("zipangu", Integer.valueOf(800000000));
            gotomaps.put("ariant", Integer.valueOf(260000100));
            gotomaps.put("nautilus", Integer.valueOf(120000000));
            gotomaps.put("boatquay", Integer.valueOf(541000000));
            gotomaps.put("malaysia", Integer.valueOf(550000000));
            gotomaps.put("erev", Integer.valueOf(130000000));
            gotomaps.put("ellin", Integer.valueOf(300000000));
            gotomaps.put("kampung", Integer.valueOf(551000000));
            gotomaps.put("singapore", Integer.valueOf(540000000));
            gotomaps.put("amoria", Integer.valueOf(680000000));
            gotomaps.put("timetemple", Integer.valueOf(270000000));
            gotomaps.put("pinkbean", Integer.valueOf(270050100));
            gotomaps.put("fm", Integer.valueOf(910000000));
            gotomaps.put("freemarket", Integer.valueOf(910000000));
            gotomaps.put("oxquiz", Integer.valueOf(109020001));
            gotomaps.put("ola", Integer.valueOf(109030101));
            gotomaps.put("fitness", Integer.valueOf(109040000));
            gotomaps.put("snowball", Integer.valueOf(109060000));
            gotomaps.put("golden", Integer.valueOf(950100000));
            gotomaps.put("phantom", Integer.valueOf(610010000));
            gotomaps.put("cwk", Integer.valueOf(610030000));
            gotomaps.put("rien", Integer.valueOf(140000000));
            gotomaps.put("edel", Integer.valueOf(310000000));
            gotomaps.put("ardent", Integer.valueOf(910001000));
            gotomaps.put("craft", Integer.valueOf(910001000));
            gotomaps.put("pvp", Integer.valueOf(960000000));
            gotomaps.put("future", Integer.valueOf(271000000));
        }
    }

    
}
