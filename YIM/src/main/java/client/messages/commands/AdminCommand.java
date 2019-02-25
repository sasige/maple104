package client.messages.commands;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import constants.ServerConstants.PlayerGMRank;
import database.DatabaseConnection;
import handling.RecvPacketOpcode;
import handling.SendPacketOpcode;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import handling.world.World;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import scripting.NPCScriptManager;
import scripting.PortalScriptManager;
import scripting.ReactorScriptManager;
import server.*;
import server.life.MapleMonsterInformationProvider;
import tools.MaplePacketCreator;
import tools.packet.UIPacket;
import tools.performance.CPUSampler;

public class AdminCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.ADMIN;
    }
    
    public static class Gmhelp extends CommandExecute {
 
        @Override
        public int execute(MapleClient c, String[] splitted) {
            
            int sls = splitted.length;
            if (sls == 1) {
                c.getPlayer().dropMessage(5, "用法!GMhelp       <1/2/3/4/5/任意字符(查看常用的命令)> ");
            }
            
            if (sls >= 2) {
                
            c.getPlayer().dropMessage(5, "============================================================");
            c.getPlayer().dropMessage(5, "       " + c.getChannelServer().getServerName() + " 管理员 命令");
            c.getPlayer().dropMessage(5, "============================================================");
            
                switch (splitted[1]) {
                    case "1":
            //DonatorCommand  中的命令
            c.getPlayer().dropMessage(5, "!lv/level       <等级>");
            c.getPlayer().dropMessage(5, "!job            <职业代码>");
            c.getPlayer().dropMessage(5, "!Maplen         <道具ID/cs/m c w /公告信息> <公告信息/道具ID/cs 道具ID> <公告信息>");
            c.getPlayer().dropMessage(5, "!item           < 物品代码 >");
            c.getPlayer().dropMessage(5, "!onpc           <npcid> <型号(默认为0)>");
            c.getPlayer().dropMessage(5, "!sp/ap          <点数>(设置SP或AP)");
            c.getPlayer().dropMessage(5, "!mapwz/mapid    (查看当前所在地图 坐标信息/当前地图ID )");
            c.getPlayer().dropMessage(5, "!skill/技能     <技能ID> <技能等级>");
            c.getPlayer().dropMessage(5, "!pwjnpc         <玩家名字><NPC代码>( 创建玩家NPC 或用!制作永久NPC)");
            c.getPlayer().dropMessage(5, "!pmob           < MOB代码 >( 创建永久MOB 或用!制作永久怪物 )");
            c.getPlayer().dropMessage(5, "!KillAllmob     < 杀死当前地图所有怪物 >");
            c.getPlayer().dropMessage(5, "!map            < 地图代码 > (传送到指定地图)");
            c.getPlayer().dropMessage(5, "!gomap          <频道(默认为当前频道)> < 地图代码 >");
            c.getPlayer().dropMessage(5, "!zaixianrs      ( 查看当前服务器在线人数 或用!在线人数 )");
            c.getPlayer().dropMessage(5, "!killplayer     <玩家名字>");
            c.getPlayer().dropMessage(5, "!pingdaozaixian ( 查看当前 频道 在线人数 )");
            c.getPlayer().dropMessage(5, "!jsxx/角色信息   < 玩家名字 >( 查看玩家的详细信息 )");
            c.getPlayer().dropMessage(5, "!gotomap       < 地图名字 /查看(查看可以传送的地图名字) >");
                        break;
                    case "2":
            //SuperDonatorCommand 中的命令
            c.getPlayer().dropMessage(5, "待加入。。。");
                        break;
                    case "3":
            //InternCommand 中的命令
            c.getPlayer().dropMessage(5, "!测谎仪          <角色名字>");
            c.getPlayer().dropMessage(5, "!查看账号        <玩家账号>");
            c.getPlayer().dropMessage(5, "!查看封号        <角色名字>");
            c.getPlayer().dropMessage(5, "!KillAll        <地图代码> ( 杀死指定地图所有怪物 )");
            c.getPlayer().dropMessage(5, "!Find           <NPC/MOB/ITEM/MAP/SKILL/QUEST 代码> (查找相应代码信息)");
            c.getPlayer().dropMessage(5, "!Say            <消息内容> （发送GM信息） ");
            c.getPlayer().dropMessage(5, "!ListAllSquads  ( 查看服务器所有 远征队 的详细信息 )");
            c.getPlayer().dropMessage(5, "!监禁           [玩家名字] [多少分钟, 0 = forever]");
            c.getPlayer().dropMessage(5, "!MyNPCPos       ( 查看自己所在详细坐标 )");
            c.getPlayer().dropMessage(5, "!LookReactor     <查看反应堆信息>");
            c.getPlayer().dropMessage(5, "!查看NPC信息     <查看NPC信息>");
            c.getPlayer().dropMessage(5, "!RemoveDrops    <清除当前地图的所有掉落物品>");
            c.getPlayer().dropMessage(5, "!检测作弊        ( 检测服务器作弊 )");
            c.getPlayer().dropMessage(5, "!PermWeather    <效果ID 5120000> (禁止当前地图指定效果)");
            c.getPlayer().dropMessage(5, "!Song            <歌曲名称> （修改当前地图背景音乐） ");
            c.getPlayer().dropMessage(5, "!检查玩家物品信息 <玩家名字> <道具ID>");
            c.getPlayer().dropMessage(5, "!在线           （查看在线总人数）");
            c.getPlayer().dropMessage(5, "!ClearInv       [all/eqp/eq/u/s/e/c] ");
            c.getPlayer().dropMessage(5, "!隐身模式        ( 开启隐身模式 )");
                        break;
                    case "4":
            //GMCommand 中的命令
            c.getPlayer().dropMessage(5, "!UnBan          < 角色ING > (解封玩家)");
            c.getPlayer().dropMessage(5, "!Yellow         <m/c/w(发送消息范围)> < 消息内容 >");
            c.getPlayer().dropMessage(5, "!Notice         <n/p/l/nv/v/b(发送消息类型)> < 消息内容 >");
            c.getPlayer().dropMessage(5, "!重置NPC        <重置地图上所有NPC(删除用!npc或NPC脚本所创建的NPC)>");
            c.getPlayer().dropMessage(5, "!KillMonster    < 怪物ID > (杀死当前地图的指定怪物)");
            c.getPlayer().dropMessage(5, "!/czmob重置怪物  ( 重置当前地图的怪物 )");
            c.getPlayer().dropMessage(5, "!谁在这个地图    ( 查看当前地图具体玩家 )");
            c.getPlayer().dropMessage(5, "!查看活动脚本    < 脚本名称 > ");
            c.getPlayer().dropMessage(5, "!disease        <type> [charname] <level> where type = SEAL/DARKNESS/WEAKEN/STUN/CURSE/POISON/SLOW/SEDUCE/REVERSE/ZOMBIFY/POTION/SHADOW/BLIND/FREEZE/POTENTIAL");
            c.getPlayer().dropMessage(5, "!找玩家位置      < 玩家名字 >");
            c.getPlayer().dropMessage(5, "!锁定道具        <角色名字> <道具ID>");
            c.getPlayer().dropMessage(5, "!删除道具        <角色名字> <道具ID>");
            c.getPlayer().dropMessage(5, "!StartEvent     ( 开启或关闭活动脚本 )");
            c.getPlayer().dropMessage(5, "!Shop           <商店ID> < 打开指定ID商店 > ");
            c.getPlayer().dropMessage(5, "!无敌模式        ( 开启或关闭无敌模式 )");
            c.getPlayer().dropMessage(5, "!人气            <玩家名字> <要加人气的数量>");
            c.getPlayer().dropMessage(5, "!GetSkill        <技能ID> <技能等级><最大等级(默认为1)>");
                        break;
                    case "5":
            //SuperGMCommand  中的命令
            c.getPlayer().dropMessage(5, "!漂浮公告        <道具ID> <公告信息>");
            c.getPlayer().dropMessage(5, "!ItemSize       <查看当前服务器加载物品数量>");
            c.getPlayer().dropMessage(5, "!BuffItemEX     <道具ID> (或用！BuffItem)");
            c.getPlayer().dropMessage(5, "!sendallnote    <消息内容> ");
            c.getPlayer().dropMessage(5, "!ResetMap       (重置地图)");
            c.getPlayer().dropMessage(5, "!StartQuest     <任务代码> （开始执行某任务） ");
            c.getPlayer().dropMessage(5, "!ResetQuest     <任务代码> （重置某任务）");
            c.getPlayer().dropMessage(5, "!ServerMessage  < 消息内容 > ( 修改服务器消息 )");
            c.getPlayer().dropMessage(5, "!MakeOfflineP   < NPC代码 >( 在当前坐标设置NPC )");
            c.getPlayer().dropMessage(5, "![npc/MakeNpc   < NPC代码 >( 在当前坐标设置永久NPC )");//
            c.getPlayer().dropMessage(5, "!NPC            < NPC代码 > ( 与指定NPC对话 )");
            c.getPlayer().dropMessage(5, "!监视玩家        <监视某玩家的动态>");
            c.getPlayer().dropMessage(5, "!Marry          <玩家名字> <结婚戒指ID>");
            c.getPlayer().dropMessage(5, "!Drop           <物品ID> (在当前坐标掉落指定物品)");//
                        break;
                    default:
            //Admin  中的命令
            c.getPlayer().dropMessage(5, "!启用幻影        <0/1><数字大于0开启幻影，等于0关闭幻影> ");
            c.getPlayer().dropMessage(5, "!Shopitem       <shopid> <itemid> <price> （为指定商店添加物品）");
            c.getPlayer().dropMessage(5, "!查看股价        (查看当前股价)");
            c.getPlayer().dropMessage(5, "!降低股价        <数量> ");
            c.getPlayer().dropMessage(5, "!增加股价        <数量>");
            c.getPlayer().dropMessage(5, "!封包调试        (进行封包调试） ");
            c.getPlayer().dropMessage(5, "!czhd           ( 重载 活动）");
            c.getPlayer().dropMessage(5, "!czsd           ( 重载 商店 贩卖物品 )");
            c.getPlayer().dropMessage(5, "!czcs           ( 重载 传送脚本 )");
            c.getPlayer().dropMessage(5, "!czbl           ( 重载 爆率 )");//
            c.getPlayer().dropMessage(5, "!czbt           ( 重载 包头 )");
            c.getPlayer().dropMessage(5, "!GainVP         <数量>");
            c.getPlayer().dropMessage(5, "!GainP          <数量> <获得点数>");
            c.getPlayer().dropMessage(5, "!dyjuan/刷抵用卷 <数量> (获得抵用点卷)");//
            c.getPlayer().dropMessage(5, "!dianjuan/栓点卷 <数量> <获得 点卷>");
            c.getPlayer().dropMessage(5, "!mxb/刷钱        <数量> (获得  金币)");//
            c.getPlayer().dropMessage(5, "!StopProfiling  < 新文件名 >( 保存 服务端检测 信息 TXT）");
            c.getPlayer().dropMessage(5, "!StartProfiling ( 开启 服务端检测 )");
            c.getPlayer().dropMessage(5, "!ShutdownTime   < 数量(单位：分钟) > （定时关闭服务器）");
            c.getPlayer().dropMessage(5, "!ckbl/查看爆率   ( 查看 爆率 )");//
            c.getPlayer().dropMessage(5, "!DCAll          < m/c/w >( 断开 指定位置的玩家连接 )");
            c.getPlayer().dropMessage(5, "!ckjy/经验信息   <查看经验信息 或用 !jyxx>");
            c.getPlayer().dropMessage(5, "!sbjy/双倍经验   < 1为关闭活动经验，2为开启活动经验 ><开启双倍经验和金币活动>");
            c.getPlayer().dropMessage(5, "!jbbl/金币倍率   <数量> (设置 服务器 金币倍率 或用!MoseRate )");//
            c.getPlayer().dropMessage(5, "!jybl/经验倍率   <数量> <设置 服务器 经验倍率 或用!ExpRate>");
            c.getPlayer().dropMessage(5, "!wpbl/物品倍率   <数量> <设置 服务器 物品倍率 或用!DropRate>");
            c.getPlayer().dropMessage(5, "!给所有人点卷    <数量> (或用 !gsyrdj )");//
            c.getPlayer().dropMessage(5, "!给所有人冒险币  <数量> (或用 !gsyrmxb )");//
            //
            c.getPlayer().dropMessage(5, "!Drop           <物品ID> (在当前坐标掉落指定物品)");//
            c.getPlayer().dropMessage(5, "!/czmob重置怪物  ( 重置当前地图的怪物 )");
            c.getPlayer().dropMessage(5, "![npc/MakeNpc   < NPC代码 >( 在当前坐标设置永久NPC )");//
            c.getPlayer().dropMessage(5, "!pmob           < MOB代码 >( 创建永久MOB 或用!制作永久怪物 )");
            
            c.getPlayer().dropMessage(5, "!lv/level       <等级>");
            c.getPlayer().dropMessage(5, "!job            <职业代码>");
            c.getPlayer().dropMessage(5, "!Maplen         <道具ID/cs/m c w /公告信息> <公告信息/道具ID/cs 道具ID> <公告信息>");
            c.getPlayer().dropMessage(5, "!item           < 物品代码 >");
            c.getPlayer().dropMessage(5, "!onpc           <npcid> <型号(默认为0)>");
            c.getPlayer().dropMessage(5, "!sp/ap          <点数>(设置SP或AP)");
            c.getPlayer().dropMessage(5, "!mapwz/mapid    (查看当前所在地图 坐标信息/当前地图ID )");
            c.getPlayer().dropMessage(5, "!skill/技能     <技能ID> <技能等级>");
            c.getPlayer().dropMessage(5, "!pwjnpc         <玩家名字><NPC代码>( 创建玩家NPC 或用!制作永久NPC)");
            c.getPlayer().dropMessage(5, "!pmob           < MOB代码 >( 创建永久MOB 或用!制作永久怪物 )");
            c.getPlayer().dropMessage(5, "!KillAllmob     < 杀死当前地图所有怪物 >");
            c.getPlayer().dropMessage(5, "!map            < 地图代码 > (传送到指定地图)");
            c.getPlayer().dropMessage(5, "!gomap          <频道(默认为当前频道)> < 地图代码 >");
            c.getPlayer().dropMessage(5, "!zaixianrs      ( 查看当前服务器在线人数 或用!在线人数 )");
            c.getPlayer().dropMessage(5, "!killplayer     <玩家名字>");
            c.getPlayer().dropMessage(5, "!pingdaozaixian ( 查看当前 频道 在线人数 )");
            c.getPlayer().dropMessage(5, "!jsxx/角色信息   < 玩家名字 >( 查看玩家的详细信息 )");
                        break;
                }
            }
            
            return 1;
        }
    }
    
    public static class itemc extends LvkejianCommand.Items {
    }

    public static class 启用幻影 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(5, "请输入数字，数字大于0开启幻影，等于0关闭幻影。");
                return 0;
            }
            LoginServer.启用幻影(Integer.parseInt(splitted[1]));
            c.getPlayer().dropMessage(5, "当前开启状态: " + LoginServer.is开启幻影());
            return 1;
        }
    }
    
    public static class Shopitem extends CommandExecute {
        @Override
        public int execute(MapleClient c, String[] splitted) {
            int getshopitemid = c.getPlayer().getMapleGetShopitems("shopitemid");
            int shopitemid = getshopitemid - 1;
            int shopid = 0;
            int itemid = 0;
            int price = 1;
            int reqitem = 0;
            int reqitemq = 0;
            int period = 0;
            int state = 0;
            int rank = 0;
            int position = 0;
            int spls = splitted.length;
            if (spls < 3) {
                c.getPlayer().dropMessage("!shopitem <shopid> <itemid> <price> ");
                return 0;
                
            } else {
                try {
                    Connection con = DatabaseConnection.getConnection();
                    if (spls == 3) {
                        position = c.getPlayer().getMapleShopitems("position", shopitemid) + 1;
                        shopitemid = getshopitemid;
                        shopid = Integer.parseInt(splitted[1]);
                        itemid = Integer.parseInt(splitted[2]);
                       
                    } else if (spls == 4) {
                        position = c.getPlayer().getMapleShopitems("position", shopitemid) + 1;
                        shopitemid = getshopitemid;
                        shopid = Integer.parseInt(splitted[1]);
                        itemid = Integer.parseInt(splitted[2]);
                        price = Integer.parseInt(splitted[3]);
                        
                    } else if (spls == 5) {
                        position = c.getPlayer().getMapleShopitems("position", shopitemid) + 1;
                        shopitemid = getshopitemid;
                        shopid = Integer.parseInt(splitted[1]);
                        itemid = Integer.parseInt(splitted[2]);
                        price = 0;
                        reqitem = Integer.parseInt(splitted[3]);
                        reqitemq = Integer.parseInt(splitted[4]);
                        
                    } else if (spls == 11) {
                         shopitemid = Integer.parseInt(splitted[1]);
                         shopid = Integer.parseInt(splitted[2]);
                         itemid = Integer.parseInt(splitted[3]);
                         price = Integer.parseInt(splitted[4]);
                         reqitem = Integer.parseInt(splitted[5]);
                         reqitemq = Integer.parseInt(splitted[6]);
                         period = Integer.parseInt(splitted[7]);
                         state = Integer.parseInt(splitted[8]);
                         rank = Integer.parseInt(splitted[9]);
                         position = Integer.parseInt(splitted[10]);
                         
                    } else {
                        c.getPlayer().dropMessage("!shopitem <shopid> <itemid> <price>");
                        return 0;
                    }
                    MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                    if (!ii.itemExists(itemid)) {
                        c.getPlayer().dropMessage(5, itemid + " 这个道具不存在.");
                        return 0;
                      } 
                    
                        PreparedStatement psu3 = con.prepareStatement("INSERT INTO shopitems (shopitemid, shopid, itemid, price, reqitem, reqitemq, period, state, rank, position) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                        psu3.setInt(1, shopitemid);
                        psu3.setInt(2, shopid);
                        psu3.setInt(3, itemid);
                        psu3.setInt(4, price);
                        psu3.setInt(5, reqitem);
                        psu3.setInt(6, reqitemq);
                        psu3.setInt(7, period);
                        psu3.setInt(8, state);
                        psu3.setInt(9, rank);
                        psu3.setInt(10, position);
                        psu3.executeUpdate();
                        psu3.close();
                        MapleShopFactory.getInstance().clear();
                        c.getPlayer().addMapleShopitems(shopitemid, shopid, itemid, position);
                        c.getPlayer().addMapleGetShopitems("shopitemid", 1);
                        c.getPlayer().dropMessage("商店 ID :"+ shopid +"  添加物品 ID :"+ itemid +" 价格为 :"+ price +" 金币.购买需要物品ID： "+ reqitem +" 数量："+ reqitemq +" 序号:"+ position +" 完成!");
                        
                } catch (SQLException e) {
                    c.getPlayer().dropMessage("添加商店物品出错 请检查 shopitems.");
                    return 0;
                }
            }
            return 1;
                }
    }

    public static class 查看股价 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().dropMessage(5, "当前的股价为: " + ChannelServer.getInstance(1).getSharePrice());
            return 1;
        }
    }

    public static class 降低股价 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(5, "请输入数量.");
                return 0;
            }
            int share = Integer.parseInt(splitted[1]);
            ChannelServer.getInstance(1).decreaseShare(share);
            c.getPlayer().dropMessage(5, "股价降低: " + share + " 当前的股价为: " + ChannelServer.getInstance(1).getSharePrice());
            return 1;
        }
    }

    public static class 增加股价 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(5, "请输入数量.");
                return 0;
            }
            int share = Integer.parseInt(splitted[1]);
            ChannelServer.getInstance(1).increaseShare(share);
            c.getPlayer().dropMessage(5, "股价提高: " + share + " 当前的股价为: " + ChannelServer.getInstance(1).getSharePrice());
            return 1;
        }
    }

    public static class 封包调试 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.StartWindow();
            return 1;
        }
    }

    public static class czhd extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            for (ChannelServer instance : ChannelServer.getAllInstances()) {
                instance.reloadEvents();
            }
            c.getPlayer().dropMessage(5, "重新加载活动脚本完成.");
            return 1;
        }
    }

    public static class czsd extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleShopFactory.getInstance().clear();
            c.getPlayer().dropMessage(5, "重新加载商店贩卖道具完成.");
            return 1;
        }
    }

    public static class czcs extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            PortalScriptManager.getInstance().clearScripts();
            c.getPlayer().dropMessage(5, "重新加载传送点脚本完成.");
            return 1;
        }
    }

    public static class czbl extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleMonsterInformationProvider.getInstance().clearDrops();
            ReactorScriptManager.getInstance().clearDrops();
            c.getPlayer().dropMessage(5, "重新加载爆率完成.");
            return 1;
        }
    }

    public static class czbt extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            SendPacketOpcode.reloadValues();
            RecvPacketOpcode.reloadValues();
            c.getPlayer().dropMessage(5, "重新获取包头完成.");
            return 1;
        }
    }

    public static class GainVP extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(5, "请输入数量.");
                return 0;
            }
            c.getPlayer().setVPoints(c.getPlayer().getVPoints() + Integer.parseInt(splitted[1]));
            return 1;
        }
    }

    public static class GainP extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(5, "请输入数量.");
                return 0;
            }
            c.getPlayer().setPoints(c.getPlayer().getPoints() + Integer.parseInt(splitted[1]));
            return 1;
        }
    }
    
    public static class dyjuan extends AdminCommand.刷抵用卷 {
    }

    public static class 刷抵用卷 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(5, "请输入数量.");
                return 0;
            }
            c.getPlayer().modifyCSPoints(2, Integer.parseInt(splitted[1]), true);
            return 1;
        }
    }

    public static class dianjuan extends AdminCommand.刷点卷 {
    }
    
    public static class 刷点卷 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(5, "请输入数量.");
                return 0;
            }
            c.getPlayer().modifyCSPoints(1, Integer.parseInt(splitted[1]), true);
            return 1;
        }
    }
    
    public static class mbx extends AdminCommand.刷钱 {
    }

    public static class 刷钱 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().gainMeso(2147483647 - c.getPlayer().getMeso(), true);
            return 1;
        }
    }

    public static class Subcategory extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().setSubcategory(Byte.parseByte(splitted[1]));
            return 1;
        }
    }

    public static class StopProfiling extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            CPUSampler sampler = CPUSampler.getInstance();
            try {
                String filename = "odinprofile.txt";
                if (splitted.length > 1) {
                    filename = splitted[1];
                }
                File file = new File(filename);
                if (file.exists()) {
                    c.getPlayer().dropMessage(6, "输入的文件名字已经存在，请重新输入1个新的文件名。");
                    return 0;
                }
                sampler.stop();
                FileWriter fw = new FileWriter(file);
                sampler.save(fw, 1, 10);
                fw.close();
            } catch (IOException e) {
                System.err.println("保存文件出错." + e);
            }
            sampler.reset();
            c.getPlayer().dropMessage(6, "已经停止服务端性能监测.");
            return 1;
        }
    }

    public static class StartProfiling extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            CPUSampler sampler = CPUSampler.getInstance();
            sampler.addIncluded("client");
            sampler.addIncluded("constants");
            sampler.addIncluded("database");
            sampler.addIncluded("handling");
            sampler.addIncluded("provider");
            sampler.addIncluded("scripting");
            sampler.addIncluded("server");
            sampler.addIncluded("tools");
            sampler.start();
            c.getPlayer().dropMessage(6, "已经开启服务端性能监测.");
            return 1;
        }
    }

    public static class ShutdownTime extends AdminCommand.Shutdown {

        private static ScheduledFuture<?> ts = null;
        private int minutesLeft = 0;

        @Override
        public int execute(MapleClient c, String[] splitted) {
            this.minutesLeft = Integer.parseInt(splitted[1]);
            c.getPlayer().dropMessage(6, "游戏将在 " + this.minutesLeft + " 分钟之后关闭...");
            if ((ts == null) && ((t == null) || (!t.isAlive()))) {
                t = new Thread(ShutdownServer.getInstance());
                ts = Timer.EventTimer.getInstance().register(new Runnable() {

                    @Override
                    public void run() {
                        if (AdminCommand.ShutdownTime.this.minutesLeft == 0) {
                            ShutdownServer.getInstance().shutdown();
                            AdminCommand.Shutdown.t.start();
                            AdminCommand.ShutdownTime.ts.cancel(false);
                            return;
                        }
                        World.Broadcast.broadcastMessage(UIPacket.clearMidMsg());
                        World.Broadcast.broadcastMessage(UIPacket.getMidMsg("游戏将于 " + AdminCommand.ShutdownTime.this.minutesLeft + " 分钟之后关闭维护.请玩家安全下线.", true, 0));
                        World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(0, " 游戏将于 " + AdminCommand.ShutdownTime.this.minutesLeft + " 分钟之后关闭维护.请玩家安全下线."));
                        //AdminCommand.ShutdownTime.access$010(AdminCommand.ShutdownTime.this);
                    }
                }, 60000);
            } else {
                c.getPlayer().dropMessage(6, "已经使用过一次这个命令，暂时无法使用.");
            }
            return 1;
        }
    }

    public static class Shutdown extends CommandExecute {

        protected static Thread t = null;

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().dropMessage(6, "游戏即将关闭...");
            if ((t == null) || (!t.isAlive())) {
                t = new Thread(ShutdownServer.getInstance());
                ShutdownServer.getInstance().shutdown();
                t.start();
            } else {
                c.getPlayer().dropMessage(6, "已经使用过一次这个命令，暂时无法使用.");
            }
            return 1;
        }
    }
    
    public static class ckbl extends AdminCommand.查看爆率 {
    }

    public static class 查看爆率 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            NPCScriptManager.getInstance().start(c, 9010000, 1);
            return 1;
        }
    }

    public static class DCAll extends CommandExecute {

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
                range = 1;
            }
            if (range == 0) {
                c.getPlayer().getMap().disconnectAll();
                c.getPlayer().dropMessage(5, "已成功断开当前地图所有玩家的连接.");
            } else if (range == 1) {
                c.getChannelServer().getPlayerStorage().disconnectAll(true);
                c.getPlayer().dropMessage(5, "已成功断开当前频道所有玩家的连接.");
            } else if (range == 2) {
                for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                    cserv.getPlayerStorage().disconnectAll(true);
                }
                c.getPlayer().dropMessage(5, "已成功断开当前游戏所有玩家的连接.");
            }
            return 1;
        }
    }
    
    public static class jyxx extends AdminCommand.经验信息 {
    }
    
    public static class cajy extends AdminCommand.经验信息 {
    }

    public static class 经验信息 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().dropMessage(5, "当前游戏设置信息:");
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                StringBuilder rateStr = new StringBuilder("频道 ");
                rateStr.append(cserv.getChannel());
                rateStr.append(" 经验: ");
                rateStr.append(cserv.getExpRate());
                rateStr.append(" 金币: ");
                rateStr.append(cserv.getMesoRate());
                rateStr.append(" 爆率: ");
                rateStr.append(cserv.getDropRate());
                rateStr.append(" 活动: ");
                rateStr.append(cserv.getDoubleExp());
                c.getPlayer().dropMessage(5, rateStr.toString());
            }
            return 1;
        }
    }
    
    public static class sbjy extends AdminCommand.双倍经验 {
    }

    public static class 双倍经验 extends CommandExecute {

        private int change = 0;

        @Override
        public int execute(MapleClient c, String[] splitted) {
            
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "用法: !双倍经验 <1或2（1为关闭2为开启）> ");
                return 0;
            }
            
            
            int jbss;
            int jyss;
            int blss;
            int bls;
            int jbs;
            int jys;
            this.change = Integer.parseInt(splitted[1]);
            if ((this.change == 1) || (this.change == 2)) {
                jys = c.getChannelServer().getExpRate();
                jbs = c.getChannelServer().getMesoRate();
                bls = c.getChannelServer().getDropRate();
                c.getPlayer().dropMessage(5, "以前 - 经验: " + c.getChannelServer().getExpRate() + " 金币: " + c.getChannelServer().getMesoRate() + " 物品爆率: " + c.getChannelServer().getDropRate());
                for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                    cserv.setDoubleExp(this.change);
                }
                jyss = c.getChannelServer().getExpRate();
                jbss = c.getChannelServer().getMesoRate();
                blss = c.getChannelServer().getDropRate();
                c.getPlayer().dropMessage(5, "现在 - 经验: " + c.getChannelServer().getExpRate() + " 金币: " + c.getChannelServer().getMesoRate() + " 物品爆率: " + c.getChannelServer().getDropRate());
                
                World.Broadcast.startMapEffect("系统倍率调整：经验倍率从" + jys + "倍调整为" + jyss + "倍  金币倍率从 " + jbs + "倍调整为" + jbss + "倍  物品爆率从 " + bls + "倍调整为" + blss + "倍！ ", 5121009);
                
                return 1;
            }
            c.getPlayer().dropMessage(5, "输入的数字无效，1为关闭活动经验，2为开启活动经验。当前输入为: " + this.change);
            return 0;
        }
    }
    
    public static class wpbl extends AdminCommand.DropRate {
    }
    
    public static class 物品倍率 extends AdminCommand.DropRate {
    }
    
        public static class DropRate extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length > 1) {
                int rate = Integer.parseInt(splitted[1]);
                if ((splitted.length > 2) && (splitted[2].equalsIgnoreCase("all"))) {
                    for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                        cserv.setDropRate(rate);
                    }
                } else {
                    c.getChannelServer().setDropRate(rate);
                }
                String names = c.getPlayer().getVipname();
                World.Broadcast.startMapEffect(" 尊贵的 " + names + " 已经将经物品爆率调整为  " + splitted[1] + "  倍！ ", 5121009);
                c.getPlayer().dropMessage(6, "物品爆率已经修改为: " + rate + "倍.");
            } else {
                c.getPlayer().dropMessage(6, "用法: !mesorate <number> [all]");
            }
            return 1;
        }
    }
    
    public static class jbbl extends AdminCommand.MesoRate {
    }
    
    public static class 金币倍率 extends AdminCommand.MesoRate {
    }

    public static class MesoRate extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length > 1) {
                int rate = Integer.parseInt(splitted[1]);
                if ((splitted.length > 2) && (splitted[2].equalsIgnoreCase("all"))) {
                    for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                        cserv.setMesoRate(rate);
                    }
                } else {
                    c.getChannelServer().setMesoRate(rate);
                }
                String names = c.getPlayer().getVipname();
                World.Broadcast.startMapEffect(" 尊贵的 " + names + " 已经将经金币率调整为  " + splitted[1] + "  倍！ ", 5121009);
                c.getPlayer().dropMessage(6, "金币爆率已经修改为: " + rate + "倍.");
            } else {
                c.getPlayer().dropMessage(6, "用法: !mesorate <number> [all]");
            }
            return 1;
        }
    }
    
    public static class jybl extends AdminCommand.ExpRate {
    }
    
    public static class 经验倍率 extends AdminCommand.ExpRate {
    }

    public static class ExpRate extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length > 1) {
                int rate = Integer.parseInt(splitted[1]);
                if ((splitted.length > 2) && (splitted[2].equalsIgnoreCase("all"))) {
                    for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                        cserv.setExpRate(rate);
                    }
                } else {
                    c.getChannelServer().setExpRate(rate);
                }
                String names = c.getPlayer().getVipname();
                World.Broadcast.startMapEffect(" 尊贵的 " + names + " 已经将经验倍率调整为  " + splitted[1] + "  倍！ ", 5121009);
                c.getPlayer().dropMessage(6, "经验倍率已经修改为: " + rate + "倍.");
            } else {
                c.getPlayer().dropMessage(6, "用法: !exprate <number> [all]");
            }
            return 1;
        }
    }
    
    public static class gsyrdj extends AdminCommand.给所有人点卷 {
    }

    public static class 给所有人点卷 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "用法: !给所有人点卷 [点卷类型1-2] [点卷数量]");
                return 0;
            }
            int type = Integer.parseInt(splitted[1]);
            int quantity = Integer.parseInt(splitted[2]);
            if ((type <= 0) || (type > 2)) {
                type = 2;
            }
            if (quantity > 9000) {
                quantity = 9000;
            }
            int ret = 0;
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (MapleCharacter mch : cserv.getPlayerStorage().getAllCharacters()) {
                    mch.modifyCSPoints(type, quantity, false);
                    mch.dropMessage(-11, new StringBuilder().append("[系统提示] 恭喜您获得管理员赠送给您的").append(type == 1 ? "点券 " : " 抵用券 ").append(quantity).append(" 点.").toString());
                    mch.dropMessage(-1, new StringBuilder().append("[系统提示] 恭喜您获得管理员赠送给您的").append(type == 1 ? "点券 " : " 抵用券 ").append(quantity).append(" 点.").toString());
                    ret++;
                }
            }
            String names = c.getPlayer().getVipname();
            World.Broadcast.startMapEffect(new StringBuilder().append("尊贵的  ").append(names).append("  赠送给各位岛民每人 ").append(type == 1 ? "点券 " : " 抵用券 ").append(quantity).append(" 点.").toString(), 5121009);
            c.getPlayer().dropMessage(6, new StringBuilder().append("命令使用成功，当前共有: ").append(ret).append(" 个玩家获得: ").append(quantity).append(" 点的").append(type == 1 ? "点券 " : " 抵用券 ").append(" 总计: ").append(ret * quantity).toString());
            return 1;
        }
    }
    
    public static class gsyrmxb extends AdminCommand.给所有人冒险币 {
    }

    public static class 给所有人冒险币 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (MapleCharacter mch : cserv.getPlayerStorage().getAllCharacters()) {
                    mch.gainMeso(Integer.parseInt(splitted[1]), true);
                    mch.dropMessage(-11, new StringBuilder().append("[系统提示] 恭喜您获得管理员赠送给您的  ").append(splitted[1]).append("  金币.").toString());
                    mch.dropMessage(-1, new StringBuilder().append("[系统提示] 恭喜您获得管理员赠送给您的  ").append(splitted[1]).append("  金币.").toString());
                    
                }
            }
            String names = c.getPlayer().getVipname();
            World.Broadcast.startMapEffect(" 尊贵的 " + names + " 赠送各位岛民每人  " + splitted[1] + "  金币 ", 5121009);
            return 1;
        }
    }

    public static class StripEveryone extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleInventory equip;
            ChannelServer cs = c.getChannelServer();
            for (MapleCharacter mchr : cs.getPlayerStorage().getAllCharacters()) {
                if (mchr.isGM()) {
                    continue;
                }
                MapleInventory equipped = mchr.getInventory(MapleInventoryType.EQUIPPED);
                equip = mchr.getInventory(MapleInventoryType.EQUIP);
                List<Short> ids = new ArrayList();
                for (Item item : equipped.newList()) {
                    ids.add(Short.valueOf(item.getPosition()));
                }
                for (short id : ids) {
                    MapleInventoryManipulator.unequip(mchr.getClient(), id, equip.getNextFreeSlot());
                }
            }

            return 1;
        }
    }
}