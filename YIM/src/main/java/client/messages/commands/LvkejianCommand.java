/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client.messages.commands;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryType;
import client.messages.CommandProcessorUtil;
import constants.GameConstants;
import constants.ServerConstants;
import database.DatabaseConnection;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import handling.world.World;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import tools.StringUtil;

/**
 *
 * @author lvkejian
 */
public class LvkejianCommand {
    
     private static final Logger log = Logger.getLogger(DonatorCommand.class);
    
    
    public static ServerConstants.PlayerGMRank getPlayerLevelRequired() {
        return ServerConstants.PlayerGMRank.LVKEJIAN;
    }
        
    
    
    
    public static class Maplem extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            int itemId = 5121009;
            String ns = "MapleWing";
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "用法: !Maplem <道具ID> <公告信息>");
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
         //   World.Broadcast.startMapEffect(ns, itemId);
            c.getPlayer().Maplem(ns, itemId);
           // MaplePacketCreator.startMapEffect(ns, itemId, true);
            c.getPlayer().dropMessage(6, ns);
            return 1;
        }
    }
    
    public static class xscsdt extends LvkejianCommand.新手出生地图 {
       
   }
    
    public static class 新手出生地图 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length > 1) {
                int rate = Integer.parseInt(splitted[1]);
                if ((splitted.length > 2) && (splitted[2].equalsIgnoreCase("all"))) {
                    for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                        cserv.setfirstMap(rate);
                    }
                } else {
                    c.getChannelServer().setfirstMap(rate);
                }
                String names = c.getPlayer().getVipname();
                World.Broadcast.startMapEffect(" 尊贵的 " + names + " 已经将 新手出生地图ID 调整为  " + splitted[1] + "   人！ ", 5121009);
                c.getPlayer().dropMessage(6, "新手出生地图ID 调整为 : " + rate + " 人.");
            } else {
                c.getPlayer().dropMessage(6, "用法: !新手出生地图/xscsdt <number> [all/频道ID]");
            }
            return 1;
        }
    }
    
    public static class qndjgb extends LvkejianCommand.潜能等级改变 {
       
   }
    
    public static class 潜能等级改变 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length > 1) {
                int rate = Integer.parseInt(splitted[1]);
                if ((splitted.length > 2) && (splitted[2].equalsIgnoreCase("all"))) {
                    for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                        cserv.setStateRate(rate);
                    }
                } else {
                    c.getChannelServer().setStateRate(rate);
                }
                String names = c.getPlayer().getVipname();
                World.Broadcast.startMapEffect(" 尊贵的 " + names + " 已经将 潜能等级改变几率 调整为  " + splitted[1] + "   人！ ", 5121009);
                c.getPlayer().dropMessage(6, "潜能等级改变几率 调整为 : " + rate + " 人.");
            } else {
                c.getPlayer().dropMessage(6, "用法: !潜能等级改变/qndjgb <number> [all/频道ID]");
            }
            return 1;
        }
    }
    
    
    public static class Akyilers extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length > 1) {
                int rate = Integer.parseInt(splitted[1]);
                if ((splitted.length > 2) && (splitted[2].equalsIgnoreCase("all"))) {
                    for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                        cserv.setAkayiles(rate);
                    }
                } else {
                    c.getChannelServer().setAkayiles(rate);
                }
                String names = c.getPlayer().getVipname();
                World.Broadcast.startMapEffect(" 尊贵的 " + names + " 已经将 阿卡伊勒 远征队入场最低人数调整为  " + splitted[1] + "   人！ ", 5121009);
                c.getPlayer().dropMessage(6, "阿卡伊勒 远征队入场最低人数调整为 : " + rate + " 人.");
            } else {
                c.getPlayer().dropMessage(6, "用法: !akayilers <number> [all/频道ID]");
            }
            return 1;
        }
    }
    
    
    public static class Zakunrs extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length > 1) {
                int rate = Integer.parseInt(splitted[1]);
                if ((splitted.length > 2) && (splitted[2].equalsIgnoreCase("all"))) {
                    for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                        cserv.setZak(rate);
                    }
                } else {
                    c.getChannelServer().setZak(rate);
                }
                String names = c.getPlayer().getVipname();
                World.Broadcast.startMapEffect(" 尊贵的 " + names + " 已经将 扎昆 远征队入场最低人数调整为  " + splitted[1] + "   人！ ", 5121009);
                c.getPlayer().dropMessage(6, "扎昆 远征队入场最低人数调整为 : " + rate + " 人.");
            } else {
                c.getPlayer().dropMessage(6, "用法: !zakunrs <number> [all/频道ID]");
            }
            return 1;
        }
    }
    
    
    public static class jZakunrs extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length > 1) {
                int rate = Integer.parseInt(splitted[1]);
                if ((splitted.length > 2) && (splitted[2].equalsIgnoreCase("all"))) {
                    for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                        cserv.setChaoszak(rate);
                    }
                } else {
                    c.getChannelServer().setChaoszak(rate);
                }
                String names = c.getPlayer().getVipname();
                World.Broadcast.startMapEffect(" 尊贵的 " + names + " 已经将 进阶扎昆 远征队入场最低人数调整为  " + splitted[1] + "   人！ ", 5121009);
                c.getPlayer().dropMessage(6, "进阶扎昆 远征队入场最低人数调整为 : " + rate + " 人.");
            } else {
                c.getPlayer().dropMessage(6, "用法: !jzakunrs <number> [all/频道ID]");
            }
            return 1;
        }
    }
    
    
    public static class heilongrs extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length > 1) {
                int rate = Integer.parseInt(splitted[1]);
                if ((splitted.length > 2) && (splitted[2].equalsIgnoreCase("all"))) {
                    for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                        cserv.setHorntail(rate);
                    }
                } else {
                    c.getChannelServer().setHorntail(rate);
                }
                String names = c.getPlayer().getVipname();
                World.Broadcast.startMapEffect(" 尊贵的 " + names + " 已经将 黑龙 远征队入场最低人数调整为  " + splitted[1] + "   人！ ", 5121009);
                c.getPlayer().dropMessage(6, "黑龙 远征队入场最低人数调整为 : " + rate + " 人.");
            } else {
                c.getPlayer().dropMessage(6, "用法: !heilongrs <number> [all/频道ID]");
            }
            return 1;
        }
    }
    
    
    public static class jheilongrs extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length > 1) {
                int rate = Integer.parseInt(splitted[1]);
                if ((splitted.length > 2) && (splitted[2].equalsIgnoreCase("all"))) {
                    for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                        cserv.setChaosht(rate);
                    }
                } else {
                    c.getChannelServer().setChaosht(rate);
                }
                String names = c.getPlayer().getVipname();
                World.Broadcast.startMapEffect(" 尊贵的 " + names + " 已经将 进阶黑龙 远征队入场最低人数调整为  " + splitted[1] + "   人！ ", 5121009);
                c.getPlayer().dropMessage(6, "进阶黑龙 远征队入场最低人数调整为 : " + rate + " 人.");
            } else {
                c.getPlayer().dropMessage(6, "用法: !jheilongrs <number> [all/频道ID]");
            }
            return 1;
        }
    }
    
    
    public static class xilars extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length > 1) {
                int rate = Integer.parseInt(splitted[1]);
                if ((splitted.length > 2) && (splitted[2].equalsIgnoreCase("all"))) {
                    for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                        cserv.setXila(rate);
                    }
                } else {
                    c.getChannelServer().setXila(rate);
                }
                String names = c.getPlayer().getVipname();
                World.Broadcast.startMapEffect(" 尊贵的 " + names + " 已经将 扎昆 远征队入场最低人数调整为  " + splitted[1] + "   人！ ", 5121009);
                c.getPlayer().dropMessage(6, "扎昆 远征队入场最低人数调整为 : " + rate + " 人.");
            } else {
                c.getPlayer().dropMessage(6, "用法: !xilars <number> [all/频道ID]");
            }
            return 1;
        }
    }
    
    
    public static class xinasirs extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length > 1) {
                int rate = Integer.parseInt(splitted[1]);
                if ((splitted.length > 2) && (splitted[2].equalsIgnoreCase("all"))) {
                    for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                        cserv.setCygnus(rate);
                    }
                } else {
                    c.getChannelServer().setCygnus(rate);
                }
                String names = c.getPlayer().getVipname();
                World.Broadcast.startMapEffect(" 尊贵的 " + names + " 已经将 希纳斯 远征队入场最低人数调整为  " + splitted[1] + "   人！ ", 5121009);
                c.getPlayer().dropMessage(6, "希纳斯 远征队入场最低人数调整为 : " + rate + " 人.");
            } else {
                c.getPlayer().dropMessage(6, "用法: !xinasirs <number> [all/频道ID]");
            }
            return 1;
        }
    }
    
    
    public static class shiziwangrs extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length > 1) {
                int rate = Integer.parseInt(splitted[1]);
                if ((splitted.length > 2) && (splitted[2].equalsIgnoreCase("all"))) {
                    for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                        cserv.setVonleon(rate);
                    }
                } else {
                    c.getChannelServer().setVonleon(rate);
                }
                String names = c.getPlayer().getVipname();
                World.Broadcast.startMapEffect(" 尊贵的 " + names + " 已经将 狮子王 远征队入场最低人数调整为  " + splitted[1] + "   人！ ", 5121009);
                c.getPlayer().dropMessage(6, "狮子王 远征队入场最低人数调整为 : " + rate + " 人.");
            } else {
                c.getPlayer().dropMessage(6, "用法: !shiziwangrs <number> [all/频道ID]");
            }
            return 1;
        }
    }
    
    
    public static class pinkebinrs extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length > 1) {
                int rate = Integer.parseInt(splitted[1]);
                if ((splitted.length > 2) && (splitted[2].equalsIgnoreCase("all"))) {
                    for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                        cserv.setPinkbean(rate);
                    }
                } else {
                    c.getChannelServer().setPinkbean(rate);
                }
                String names = c.getPlayer().getVipname();
                World.Broadcast.startMapEffect(" 尊贵的 " + names + " 已经将 品克缤 远征队入场最低人数调整为  " + splitted[1] + "   人！ ", 5121009);
                c.getPlayer().dropMessage(6, "品克缤 远征队入场最低人数调整为 : " + rate + " 人.");
            } else {
                c.getPlayer().dropMessage(6, "用法: !pinkebinrs <number> [all/频道ID]");
            }
            return 1;
        }
    }
    
    
    public static class lvkejian extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length > 1) {
                int rate = Integer.parseInt(splitted[1]);
                if (splitted.length > 2) {
                    for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                        cserv.setLvkejian(rate);
                    }
               // } else {
              //      c.getChannelServer().setLvkejian(rate);
                }
                boolean mms = c.getChannelServer().getLvkejian();
             //   World.Broadcast.startMapEffect(" 尊贵的 " + names + " 已经将 品克缤 远征队入场最低人数调整为  " + splitted[1] + "   人！ ", 5121009);
                c.getPlayer().dropMessage(5, "当前测试模式为 :  " + mms + "  .");
                
            } else {
                c.getPlayer().dropMessage(5, "用法: !lvkejian <大于1为开启 小于1则为关闭>");
            }
            return 1;
        }
    }
    
    public static class PG extends LvkejianCommand.破攻 {
       
    }
    
    
    public static class 破攻 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(5, "请输入数字，数字大于1关闭破攻，等于1开启破攻。");
                return 0;
            }
            
            LoginServer.破攻(Integer.parseInt(splitted[1]));
             
            c.getPlayer().dropMessage(5, "当前 破攻 状态 : " + LoginServer.isMaxDamage());
            return 1;
        }
    }
    
    public static class PGMS extends LvkejianCommand.破攻模式 {
       
    }
    
    public static class 破攻模式 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(5, "请输入数字，破功类型 0-全属性装备不超过1000的,1-全属性装备最大32767的。");
                return 0;
            }
            LoginServer.破攻模式(Integer.parseInt(splitted[1]));
            c.getPlayer().dropMessage(5, "当前  破攻模式 : " + LoginServer.getMaxdamageType());
            return 1;
        }
    }
    
    
        
    
   public static class jyxxs extends AdminCommand.经验信息 {
       
    }
   
   public static class MLS extends AdminCommand.Gmhelp {
       
   }
   
   public static class 取得自己账号密码 extends LvkejianCommand.Getpw {
       
   }
   
   public static class Getpw extends CommandExecute {
       
        @Override
        public int execute(MapleClient c, String[] splitted) {
        MapleClient victimC = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]).getClient();
        c.getPlayer().dropMessage("帐号: " + victimC.getAccountName());
        c.getPlayer().dropMessage("密码: " + victimC.getSecondPassword());
        return 1;
        }
   }
   
   
   
    public static class Items extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            
            ChannelServer cserv = c.getChannelServer();
            
            int itemId;
            int itemIds = 4001126;
            short quantitys;
            short quantity = 1;
            String names;
            int spls = splitted.length;
            
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            
            if ((spls < 2)||(spls > 4)) {
                c.getPlayer().dropMessage(6, "用法: !Items <角色名称(默认为自己)> <道具ID>");
                return 0;
            }

            MapleCharacter playername = cserv.getPlayerStorage().getCharacterByName(splitted[1]);

            if (playername != null) {
                names = StringUtil.joinStringFrom(splitted, 1);
                itemIds = Integer.parseInt(splitted[2]);
                quantity = (short) CommandProcessorUtil.getOptionalIntArg(splitted, 3, 1);

            } else {  //默认为自己时
                names = c.getPlayer().getName();
                itemIds = Integer.parseInt(splitted[1]);
                quantity = (short) CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1);
            }
        
            
            itemId = itemIds;
            quantitys = quantity;
            
            
            
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
            
            
        //    MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
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
                    item = new Item(itemId, (short) 0, !c.getPlayer().isSuperGM() ? 1 : quantitys, (short) 0);
                }
                if (!c.getPlayer().isSuperGM()) {
                    item.setFlag(flag);
                }
                if (!c.getPlayer().isAdmin()) {
                    item.setOwner(c.getPlayer().getName());
                }
                item.setGMLog(c.getPlayer().getName() + " 使用命令 !item");
                
                    if (playername != null) {
                        MapleInventoryManipulator.addbyItem(playername.getClient(), item);
                    } else {
                   // c.getPlayer().dropMessage("[系统]：玩家未找到.");
                    MapleInventoryManipulator.addbyItem(c, item);
                    
                    }
                c.getPlayer().dropMessage(5, "[命令] 管理员 " + c.getPlayer().getName() + " 刷道具: " + item.getItemId() + " 数量: " + item.getQuantity() + " 名称: " + ii.getName(itemId) + "   给角色: " + names);
                LvkejianCommand.log.info("[命令] 管理员 " + c.getPlayer().getName() + " 刷道具: " + item.getItemId() + " 数量: " + item.getQuantity() + " 名称: " + ii.getName(itemId) + "   给角色: " + names);
            }
            return 1;
        }
    }
   
       
}
