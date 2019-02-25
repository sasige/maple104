package client.messages.commands;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleStat;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import constants.ServerConstants.PlayerGMRank;
import handling.channel.ChannelServer;
import java.util.Arrays;
import java.util.List;
import scripting.NPCScriptManager;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.RankingWorker;
import server.RankingWorker.RankingInformation;
import server.life.MapleMonster;
import server.life.MapleMonsterInformationProvider;
import server.life.MapleNPC;
import server.maps.*;
import tools.MaplePacketCreator;
import tools.StringUtil;

public class PlayerCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.NORMAL;
    }

    public static class Help extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().dropMessage(-11, "============================================================");
            c.getPlayer().dropMessage(-11, "        " + c.getChannelServer().getServerName() + " 玩家命令");
            c.getPlayer().dropMessage(-11, "============================================================");
            c.getPlayer().dropMessage(-11, "@LL/@MJ/@ZL/@YQ <空格键> <数量>    - 快速加属性点命令(不分大小写).");
            c.getPlayer().dropMessage(-11, "@str, @dex, @int, @luk <空格键> <需要分配的点数>");
            c.getPlayer().dropMessage(-11, "@save       - 在线存档命令.");
            c.getPlayer().dropMessage(-11, "@mob < 查看当前离你最近的怪物信息 >");
            c.getPlayer().dropMessage(-11, "@hg < 移动到自由市场 >");
            c.getPlayer().dropMessage(-11, "@ea < 如果无法和NPC进行对话请输入这个命令 >");
            c.getPlayer().dropMessage(-11, "@fh < 每日可以免费复活5次 >");
            c.getPlayer().dropMessage(-11, "@ranking < 查看游戏中的排名信息 >");
            c.getPlayer().dropMessage(-11, "@穿戴宝盒 < 宝盒在装备拦的位置 > 注意: 此功能只对龙的传人开放");
            c.getPlayer().dropMessage(-11, "@取下宝盒 注意: 此功能只对龙的传人开放");
            c.getPlayer().dropMessage(-11, "@VIP 查看自己的VIP信息和特权、福利等(也可以使用@会员)");
            c.getPlayer().dropMessage(-11, "@QN 查看自己的鉴定潜能数目改变概率(也可以使用@潜能)");
            c.getPlayer().dropMessage(-11, "@wt   查看自己委托任务信息(也可以使用@委托)");
            c.getPlayer().dropMessage(-11, "@pg   查看自己攻击伤害上限(也可以使用@破攻)");

            return 1;
        }
    }
    
    
    public static class 委托 extends PlayerCommand.wt {
       
   }
    
    public static class wt extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            
            MapleItemInformationProvider iis = MapleItemInformationProvider.getInstance();

            if (splitted.length < 2) {
                c.getPlayer().dropMessage(5, "用法：@wt <数字0--9> 查看指定委托任务的信息");
                return 0;
            }
            int s输入 = Integer.parseInt(splitted[1]);
            
           if (s输入 <= 0 || s输入 >= 10) {
                s输入 = 1;
            } 
           switch (s输入) {
               case 0:
                   NPCScriptManager.getInstance().start(c, 9000086, 2200);
                   break;
               case 1:
                   NPCScriptManager.getInstance().start(c, 9000086, 2201);
                   break;
               case 2:
                   NPCScriptManager.getInstance().start(c, 9000086, 2202);
                   break;
               case 3:
                   NPCScriptManager.getInstance().start(c, 9000086, 2203);
                   break;
               case 4:
                   NPCScriptManager.getInstance().start(c, 9000086, 2204);
                   break;
               case 5:
                   NPCScriptManager.getInstance().start(c, 9000086, 2205);
                   break;
               case 6:
                   NPCScriptManager.getInstance().start(c, 9000086, 2206);
                   break;
               case 7:
                   NPCScriptManager.getInstance().start(c, 9000086, 2207);
                   break;
               case 8:
                   NPCScriptManager.getInstance().start(c, 9000086, 2208);
                   break;
               case 9:
                   NPCScriptManager.getInstance().start(c, 9000086, 2209);
                   break;
           }
           return 1;
        }
        
    }
    

    
    
    public static class 破攻 extends PlayerCommand.pg {
       
   }
    
    public static class pg extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            long maxdamage = (999999 + c.getPlayer().getMaplewing("maple") * c.getPlayer().getMaplewing("cardlevel") * (ChannelServer.getpogpngbilv()));
            if ((maxdamage >= 2147483647) || (maxdamage < 0)) {
                maxdamage = 2147483647;
            }
            String mds = "当前您的伤害上限为： " + maxdamage + " ";
            c.getPlayer().dropMessage(-11, "============================================================");
            c.getPlayer().dropMessage(-11, "                  " + c.getChannelServer().getServerName() + "    ");
            c.getPlayer().dropMessage(-11, "============================================================");
            c.getPlayer().dropMessage(-11, "目前您的岛民等级为：" + c.getPlayer().getMaplewing("cardlevel") + "  拥有贡献点: " + c.getPlayer().getMaplewing("maple"));
            c.getPlayer().dropMessage(-11, "当前伤害上限倍率：" + ChannelServer.getpogpngbilv());
            c.getPlayer().dropMessage(-11, "伤害上限计算公式： 基础上限(999999) + 岛民等级 * 贡献点 * " + ChannelServer.getpogpngbilv());
            c.getPlayer().dropMessage(-1, mds);
            c.getPlayer().dropMessage(-11, mds);
            return 1;
        }
    }
    
    
    public static class 潜能 extends PlayerCommand.qn {
       
   }
    
    public static class qn extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            int qns = c.getPlayer().get潜能数目改变概率();
            long maxdamage = (999999 + c.getPlayer().getMaplewing("maple") * c.getPlayer().getMaplewing("cardlevel") * (ChannelServer.getpogpngbilv()));
            if ((maxdamage >= 2147483647) || (maxdamage < 0)) {
                maxdamage = 2147483647;
            }
            String mds = "当前您的伤害上限为： " + maxdamage + " ";
            c.getPlayer().dropMessage(-11, "============================================================");
            c.getPlayer().dropMessage(-11, "                  " + c.getChannelServer().getServerName() + "    ");
            c.getPlayer().dropMessage(-11, "============================================================");
            c.getPlayer().dropMessage(-11, "目前您的岛民等级为：" + c.getPlayer().getMaplewing("cardlevel") + "  拥有贡献点: " + c.getPlayer().getMaplewing("maple") + "  拥有活跃点: " + c.getPlayer().getMaplewing("mapley"));
            c.getPlayer().dropMessage(-11, "当前伤害上限倍率：" + ChannelServer.getpogpngbilv());
            c.getPlayer().dropMessage(-11, "伤害上限计算公式： 基础上限(999999) + 岛民等级 * 贡献点 * " + ChannelServer.getpogpngbilv());
            c.getPlayer().dropMessage(-1, mds);
            c.getPlayer().dropMessage(-11, mds);
            c.getPlayer().dropMessage(-11, "============================================================");
            c.getPlayer().dropMessage(-11, "鉴定装备潜能数目数量改变概率计算公式：  岛民等级 * ( 活跃点 + " + c.getChannelServer().get潜能数目改变基本概率() + " )");
            c.getPlayer().dropMessage(-11, "当前您的鉴定装备潜能数目数量改变概率如下：");
            c.getPlayer().dropMessage(-11, "鉴定出3条潜能概率：" + qns + " %");
            c.getPlayer().dropMessage(-11, "鉴定出4条潜能概率：" + (int) qns/10 + " %");
            c.getPlayer().dropMessage(-11, "鉴定出5条潜能概率：" + (int) qns/100 + " %");
            c.getPlayer().dropMessage(-11, "============================================================");
            c.getPlayer().dropMessage(-11, "第5条潜能鉴定出B级潜能概率：100 %");
            c.getPlayer().dropMessage(-11, "第5条潜能鉴定出A级潜能概率：" + (int) c.getPlayer().get潜能5的潜能等级概率()/30 + " %");
            c.getPlayer().dropMessage(-11, "第5条潜能鉴定出S级潜能概率：" + (int) c.getPlayer().get潜能5的潜能等级概率()/40 + " %");
            c.getPlayer().dropMessage(-11, "第5条潜能鉴定出SS级潜能概率：" + (int) c.getPlayer().get潜能5的潜能等级概率()/50 + " %");
            c.getPlayer().dropMessage(-11, "============================================================");
            c.getPlayer().dropMessage(-11, "第4条潜能鉴定出B级潜能概率：100 %");
            c.getPlayer().dropMessage(-11, "第4条潜能鉴定出A级潜能概率：" + (int) c.getPlayer().get潜能4的潜能等级概率()/10 + " %");
            c.getPlayer().dropMessage(-11, "第4条潜能鉴定出S级潜能概率：" + (int) c.getPlayer().get潜能4的潜能等级概率()/20 + " %");
            c.getPlayer().dropMessage(-11, "第4条潜能鉴定出SS级潜能概率：" + (int) c.getPlayer().get潜能4的潜能等级概率()/30 + " %");
            c.getPlayer().dropMessage(-11, "============================================================");
            c.getPlayer().dropMessage(-11, "第3条潜能鉴定出B级潜能概率：100 %");
            c.getPlayer().dropMessage(-11, "第3条潜能鉴定出A级潜能概率：" + (int) c.getPlayer().get潜能3的潜能等级概率()/5 + " %");
            c.getPlayer().dropMessage(-11, "第3条潜能鉴定出S级潜能概率：" + (int) c.getPlayer().get潜能3的潜能等级概率()/10 + " %");
            c.getPlayer().dropMessage(-11, "第3条潜能鉴定出SS级潜能概率：" + (int) c.getPlayer().get潜能3的潜能等级概率()/20 + " %");
            c.getPlayer().dropMessage(-11, "============================================================");
            c.getPlayer().dropMessage(-11, "第2条潜能鉴定出B级潜能概率：100 %");
            c.getPlayer().dropMessage(-11, "第2条潜能鉴定出A级潜能概率：" + (int) c.getPlayer().get潜能2的潜能等级概率()/2 + " %");
            c.getPlayer().dropMessage(-11, "第2条潜能鉴定出S级潜能概率：" + (int) c.getPlayer().get潜能2的潜能等级概率()/5 + " %");
            c.getPlayer().dropMessage(-11, "第2条潜能鉴定出SS级潜能概率：" + (int) c.getPlayer().get潜能2的潜能等级概率()/10 + " %");
            c.getPlayer().dropMessage(-11, "============================================================");
          //  c.getPlayer().dropMessage(-11, "鉴定出第3、4、5条潜能等级与第1条潜能一样的概率：" + c.getPlayer().get潜能1的潜能等级概率() + " %");
            c.getPlayer().dropMessage(-11, "第1条潜能鉴定出B级潜能概率：" + c.getPlayer().get鉴定出B级潜能概率() + " %");
            c.getPlayer().dropMessage(-11, "第1条潜能鉴定出A级潜能概率：" + c.getPlayer().get鉴定出A级潜能概率() + " %");
            c.getPlayer().dropMessage(-11, "第1条潜能鉴定出S级潜能概率：" + c.getPlayer().get鉴定出S级潜能概率() + " %");
            c.getPlayer().dropMessage(-11, "第1条潜能鉴定出SS级潜能概率：" + c.getPlayer().get鉴定出SS级潜能概率() + " %");
            c.getPlayer().dropMessage(-11, "============================================================");
            c.getPlayer().dropMessage(-11, "神奇魔方 重置过的装备，鉴定出的各潜能概率加成：");
            c.getPlayer().dropMessage(-11, "B级潜能概率 +100%  A级潜能概率 +50%  S级潜能概率 +20%  SS级潜能为 0%");
            c.getPlayer().dropMessage(-11, "高级神奇魔方 重置过的装备，鉴定出的各潜能概率加成：");
            c.getPlayer().dropMessage(-11, "B级潜能概率 +100%  A级潜能概率 +50%  S级潜能概率 +30%  SS级潜能为 0%");
            c.getPlayer().dropMessage(-11, "混沌神奇魔方 重置过的装备，鉴定出的各潜能概率加成：");
            c.getPlayer().dropMessage(-11, "B级潜能概率 +100%  A级潜能概率 +50%  S级潜能概率 +30%  SS级潜能为 0%");
            c.getPlayer().dropMessage(-11, "惊人神奇魔方 重置过的装备，鉴定出的各潜能概率加成：");
            c.getPlayer().dropMessage(-11, "B级潜能概率 +100%  A级潜能概率 +100%  S级潜能概率 +80%  SS级潜能 +10%");
            
            
            
            
            return 1;
        }
    }
    
    
    public static class 会员 extends PlayerCommand.vip {
       
   }
    
    public static class vip extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            long maxdamage = (999999 + c.getPlayer().getMaplewing("maple") * c.getPlayer().getMaplewing("cardlevel") * (ChannelServer.getpogpngbilv()));
            if ((maxdamage >= 2147483647) || (maxdamage < 0)) {
                maxdamage = 2147483647;
            }
            String mds = "当前您的攻击上限为： " + maxdamage + " ";
            c.getPlayer().dropMessage(-11, "============================================================");
            c.getPlayer().dropMessage(-11, "                  " + c.getChannelServer().getServerName() + "    ");
            c.getPlayer().dropMessage(-11, "============================================================");
            c.getPlayer().dropMessage(-11, mds);
            c.getPlayer().dropMessage(-11, "目前您的岛民等级为：" + c.getPlayer().getMaplewing("cardlevel") + "  拥有贡献点: " + c.getPlayer().getMaplewing("maple"));
            c.getPlayer().dropMessage(-11, "打猎额外获得的 网吧特别经验 比率：" + c.getPlayer().getVipExp() + " %");
           // c.getPlayer().dropMessage(-11, "打猎额外获得的 网吧特别金币 比率：" + c.getPlayer().getVipExp() + " %");
            c.getPlayer().dropMessage(-1, mds);
            return 1;
        }
    }
    
    public static class bl extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            NPCScriptManager.getInstance().start(c, 9010000, 1);
            return 1;
        }
    }
    
    public static class cj extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            NPCScriptManager.getInstance().start(c, 9900002, 0);
            return 1;
        }
    }
    
    public static class ns1 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            NPCScriptManager.getInstance().start(c, 2144000, 1);
            return 1;
        }
    }
    
    public static class ns2 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            NPCScriptManager.getInstance().start(c, 2144000, 2);
            return 1;
        }
    }
    
    public static class ns3 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            NPCScriptManager.getInstance().start(c, 2144000, 3);
            return 1;
        }
    }
    
    public static class ns4 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            NPCScriptManager.getInstance().start(c, 2144000, 4);
            return 1;
        }
    }
    
    public static class ns5 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            NPCScriptManager.getInstance().start(c, 2144000, 5);
            return 1;
        }
    }
    
    public static class 取下灵魂盾 extends CommandExecute
  {
    public int execute(MapleClient c, String[] splitted)
    {
      if (GameConstants.is米哈尔(c.getPlayer().getJob())) {
        Item toUse = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short) -10);
        if ((toUse == null) || (c.getPlayer().getInventory(MapleInventoryType.EQUIP).isFull())) {
          c.getPlayer().dropMessage(6, "取下米哈尔盾错误，副武器位置道具信息为空，或者装备栏已满。");
          return 0;
        }
        MapleInventoryManipulator.unequip(c, (byte) -10, c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
        return 1;
      }
      c.getPlayer().dropMessage(6, "此命令只对米哈尔开放。");
      return 0;
    }
  }

  public static class 穿戴灵魂盾 extends CommandExecute
  {
    public int execute(MapleClient c, String[] splitted)
    {
      if (splitted.length < 2) {
        c.getPlayer().dropMessage(6, "用法: @Eqdun [灵魂盾在装备拦的位置]");
        return 0;
      }
      if (c.getPlayer().getLevel() < 10) {
        c.getPlayer().dropMessage(5, "等级达到10级才可以使用此命令.");
        return 0;
      }
      if (GameConstants.is米哈尔(c.getPlayer().getJob())) {
        short src = (short)Integer.parseInt(splitted[1]);
        Item toUse = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(src);
        if ((toUse == null) || (toUse.getQuantity() < 1) || (!GameConstants.is米哈尔盾(toUse.getItemId()))) {
          c.getPlayer().dropMessage(6, "穿戴错误，装备栏的第 " + src + " 个道具的道具信息为空，或者该道具不是米哈尔专用盾。");
          return 0;
        }
        MapleInventoryManipulator.equip(c, src, (short) -10);
        return 1;
      }
      c.getPlayer().dropMessage(6, "此命令只对米哈尔开放。");
      return 0;
    }
  }
  
  public static class 取下精气盾 extends CommandExecute
  {
    public int execute(MapleClient c, String[] splitted)
    {
      if (GameConstants.is恶魔猎手(c.getPlayer().getJob())) {
        Item toUse = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short) -10);
        if ((toUse == null) || (c.getPlayer().getInventory(MapleInventoryType.EQUIP).isFull())) {
          c.getPlayer().dropMessage(6, "取下恶魔猎手盾错误，副武器位置道具信息为空，或者装备栏已满。");
          return 0;
        }
        MapleInventoryManipulator.unequip(c, (byte) -10, c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
        return 1;
      }
      c.getPlayer().dropMessage(6, "此命令只对恶魔猎手开放。");
      return 0;
    }
  }

  public static class 穿戴精气盾 extends CommandExecute
  {
    public int execute(MapleClient c, String[] splitted)
    {
      if (splitted.length < 2) {
        c.getPlayer().dropMessage(6, "用法: @Eqdun [恶魔猎手盾在装备拦的位置]");
        return 0;
      }
      if (c.getPlayer().getLevel() < 10) {
        c.getPlayer().dropMessage(5, "等级达到10级才可以使用此命令.");
        return 0;
      }
      if (GameConstants.is恶魔猎手(c.getPlayer().getJob())) {
        short src = (short)Integer.parseInt(splitted[1]);
        Item toUse = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(src);
        if ((toUse == null) || (toUse.getQuantity() < 1) || (!GameConstants.is恶魔猎手盾(toUse.getItemId()))) {
          c.getPlayer().dropMessage(6, "穿戴错误，装备栏的第 " + src + " 个道具的道具信息为空，或者该道具不是恶魔猎手专用盾。");
          return 0;
        }
        MapleInventoryManipulator.equip(c, src, (short) -10);
        return 1;
      }
      c.getPlayer().dropMessage(6, "此命令只对恶魔猎手开放。");
      return 0;
    }
  }
    

    public static class 取下宝盒 extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            if (GameConstants.is龙的传人(c.getPlayer().getJob())) {
                Item toUse = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short) -10);
                if ((toUse == null) || (c.getPlayer().getInventory(MapleInventoryType.EQUIP).isFull())) {
                    c.getPlayer().dropMessage(6, "取下宝盒错误，副武器位置道具信息为空，或者装备栏已满。");
                    return 0;
                }
                MapleInventoryManipulator.unequip(c, (byte) -10, c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                return 1;
            }
            c.getPlayer().dropMessage(6, "此命令只对龙的传人开放。");
            return 0;
        }
    }

    public static class 穿戴宝盒 extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "用法: @穿戴宝盒 [宝盒在装备拦的位置]");
                return 0;
            }
            if (c.getPlayer().getLevel() < 10) {
                c.getPlayer().dropMessage(5, "等级达到10级才可以使用此命令.");
                return 0;
            }
            if (GameConstants.is龙的传人(c.getPlayer().getJob())) {
                short src = (short) Integer.parseInt(splitted[1]);
                Item toUse = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(src);
                if ((toUse == null) || (toUse.getQuantity() < 1) || (!GameConstants.is龙传宝盒(toUse.getItemId()))) {
                    c.getPlayer().dropMessage(6, "穿戴错误，装备栏的第 " + src + " 个道具的道具信息为空，或者该道具不是宝盒装备。");
                    return 0;
                }
                MapleInventoryManipulator.equip(c, src, (short) -10);
                return 1;
            }
            c.getPlayer().dropMessage(6, "此命令只对龙的传人开放。");
            return 0;
        }
    }

    public static class Ranking extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 4) {
                c.getPlayer().dropMessage(5, "使用 @ranking [职业类型] [起始排名] [结束排名] 例子:[@ranking 所有 1 20]此为显示所有职业排名第1-20的信息");
                StringBuilder builder = new StringBuilder("职业类型: ");
                for (String b : RankingWorker.getJobCommands().keySet()) {
                    builder.append(b);
                    builder.append(" ");
                }
                c.getPlayer().dropMessage(5, builder.toString());
            } else {
                int start = 1;
                int end = 20;
                try {
                    start = Integer.parseInt(splitted[2]);
                    end = Integer.parseInt(splitted[3]);
                } catch (NumberFormatException e) {
                    c.getPlayer().dropMessage(5, "输入的显示排名数字错误.每次只能显示20个角色的信息 例子:[@ranking 所有 1 20");
                }
                if ((end < start) || (end - start > 20)) {
                    c.getPlayer().dropMessage(5, "输入的显示排名数字错误.每次只能显示20个角色的信息 例子:[@ranking 所有 1 20");
                } else {
                    Integer job = RankingWorker.getJobCommand(splitted[1]);
                    if (job == null) {
                        c.getPlayer().dropMessage(5, "输入的职业类型代码不存在.");
                    } else {
                        List<RankingInformation> ranks = RankingWorker.getRankingInfo(job.intValue());
                        if ((ranks == null) || (ranks.size() <= 0)) {
                            c.getPlayer().dropMessage(5, "请稍后在试.");
                        } else {
                            int num = 0;
                            for (RankingInformation rank : ranks) {
                                if ((rank.rank >= start) && (rank.rank <= end)) {
                                    if (num == 0) {
                                        c.getPlayer().dropMessage(6, new StringBuilder().append("当前显示为 ").append(splitted[1]).append(" 的排名 - 开始 ").append(start).append(" 结束 ").append(end).toString());
                                        c.getPlayer().dropMessage(6, "--------------------------------------------");
                                    }
                                    c.getPlayer().dropMessage(6, rank.toString());
                                    num++;
                                }
                            }
                            if (num == 0) {
                                c.getPlayer().dropMessage(5, "排名信息为空.");
                            }
                        }
                    }
                }
            }
            return 1;
        }
    }

    public static class EA extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            c.removeClickedNPC();
            NPCScriptManager.getInstance().dispose(c);
            c.getSession().write(MaplePacketCreator.enableActions());
            return 1;
        }
    }

    public static class HG extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            for (int i : GameConstants.blockedMaps) {
                if (c.getPlayer().getMapId() == i) {
                    c.getPlayer().dropMessage(5, "当前地图禁止使用此命令.");
                    return 0;
                }
            }
            if ((c.getPlayer().getLevel() < 10) && (c.getPlayer().getJob() != 200)) {
                c.getPlayer().dropMessage(5, "等级达到10级才可以使用此命令.");
                return 0;
            }
            if ((c.getPlayer().hasBlockedInventory()) || (c.getPlayer().getMap().getSquadByMap() != null) || (c.getPlayer().getEventInstance() != null) || (c.getPlayer().getMap().getEMByMap() != null) || (c.getPlayer().getMapId() >= 990000000)) {
                c.getPlayer().dropMessage(5, "当前地图禁止使用此命令.");
                return 0;
            }
            if (((c.getPlayer().getMapId() >= 680000210) && (c.getPlayer().getMapId() <= 680000502)) || ((c.getPlayer().getMapId() / 1000 == 980000) && (c.getPlayer().getMapId() != 980000000)) || (c.getPlayer().getMapId() / 100 == 1030008) || (c.getPlayer().getMapId() / 100 == 922010) || (c.getPlayer().getMapId() / 10 == 13003000)) {
                c.getPlayer().dropMessage(5, "当前地图禁止使用此命令.");
                return 0;
            }
            c.getPlayer().saveLocation(SavedLocationType.FREE_MARKET, c.getPlayer().getMap().getReturnMap().getId());
            MapleMap map = c.getChannelServer().getMapFactory().getMap(910000000);
            c.getPlayer().changeMap(map, map.getPortal(0));
            c.getPlayer().setMaplewingmap();
            return 1;
        }
    }

    public static class Mob extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            MapleMonster mob = null;
            for (MapleMapObject monstermo : c.getPlayer().getMap().getMapObjectsInRange(c.getPlayer().getPosition(), 100000.0D, Arrays.asList(new MapleMapObjectType[]{MapleMapObjectType.MONSTER}))) {
                mob = (MapleMonster) monstermo;
                if (mob.isAlive()) {
                    c.getPlayer().dropMessage(6, "怪物: " + mob.toString());
                    break;
                }
            }
            if (mob == null) {
                c.getPlayer().dropMessage(6, "查看失败: 1.没有找到需要查看的怪物信息. 2.你周围没有怪物出现. 3.有些怪物禁止查看.");
            }
            return 1;
        }
    }

    public static class Fh extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            if (c.getPlayer().getLevel() < 70) {
                c.getPlayer().dropMessage(5, "等级达到70级才可以使用这个命令.");
                return 0;
            }
            if (c.getPlayer().isAlive()) {
                c.getPlayer().dropMessage(5, "您都还没有挂掉，怎么能使用这个命令呢。");
                return 0;
            }
            if ((c.getPlayer().getBossLog("原地复活") >= 5) && (c.getPlayer().getCSPoints(2) < 300)) {
                c.getPlayer().dropMessage(5, "您今天的免费复活次数已经用完或者您的抵用卷不足300点。");
                return 0;
            }
            if (c.getPlayer().getBossLog("原地复活") < 5) {
                c.getPlayer().setBossLog("原地复活");
                c.getPlayer().getStat().heal(c.getPlayer());
                c.getPlayer().dispelDebuffs();
                c.getPlayer().dropMessage(5, "恭喜您原地复活成功，您今天还可以免费使用: " + (5 - c.getPlayer().getBossLog("原地复活")) + " 次。");
                return 1;
            }
            if (c.getPlayer().getCSPoints(2) >= 300) {
                c.getPlayer().modifyCSPoints(2, -300);
                c.getPlayer().getStat().heal(c.getPlayer());
                c.getPlayer().dispelDebuffs();
                c.getPlayer().dropMessage(5, "恭喜您原地复活成功，本次复活花费抵用卷 300 点。");
                return 1;
            }
            c.getPlayer().dropMessage(5, "复活失败，您今天的免费复活次数已经用完或者您的抵用卷不足300点。");
            return 0;
        }
    }

    public static class Save extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            if (c.getPlayer().getCheatTracker().canSaveDB()) {
                c.getPlayer().dropMessage(5, "开始保存角色数据...");
                c.getPlayer().saveToDB(false, false);
                c.getPlayer().dropMessage(5, "保存角色数据完成...");
                return 1;
            }
            c.getPlayer().dropMessage(5, "保存角色数据失败，此命令使用的间隔为60秒。上线后第1次输入不保存需要再次输入才保存。");
            return 0;
        }
    }

    public static abstract class DistributeStatCommands extends CommandExecute {

        protected MapleStat stat = null;

        private void setStat(MapleCharacter player, int amount) {
            switch (stat) {
                case 力量:
                    player.getStat().setStr((short) amount, player);
                    player.updateSingleStat(MapleStat.力量, player.getStat().getStr());
                    break;
                case 敏捷:
                    player.getStat().setDex((short) amount, player);
                    player.updateSingleStat(MapleStat.敏捷, player.getStat().getDex());
                    break;
                case 智力:
                    player.getStat().setInt((short) amount, player);
                    player.updateSingleStat(MapleStat.智力, player.getStat().getInt());
                    break;
                case 运气:
                    player.getStat().setLuk((short) amount, player);
                    player.updateSingleStat(MapleStat.运气, player.getStat().getLuk());
            }
        }

        private int getStat(MapleCharacter player) {
            switch (stat) {
                case 力量:
                    return player.getStat().getStr();
                case 敏捷:
                    return player.getStat().getDex();
                case 智力:
                    return player.getStat().getInt();
                case 运气:
                    return player.getStat().getLuk();
            }
            throw new RuntimeException();
        }

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(5, "输入的数字无效.");
                return 0;
            }
            int change = 0;
            try {
                change = Integer.parseInt(splitted[1]);
            } catch (NumberFormatException nfe) {
                c.getPlayer().dropMessage(5, "输入的数字无效.");
                return 0;
            }
            if (change <= 0) {
                c.getPlayer().dropMessage(5, "您必须输入一个大于 0 的数字.");
                return 0;
            }
            if (c.getPlayer().getRemainingAp() < change) {
                c.getPlayer().dropMessage(5, "您的能力点不足.");
                return 0;
            }
            if (getStat(c.getPlayer()) + change > c.getChannelServer().getStatLimit()) {
                c.getPlayer().dropMessage(5, "所要分配的能力点总和不能大于 " + c.getChannelServer().getStatLimit() + " 点.");
                return 0;
            }
            setStat(c.getPlayer(), getStat(c.getPlayer()) + change);
            c.getPlayer().setRemainingAp((short) (c.getPlayer().getRemainingAp() - change));
            c.getPlayer().updateSingleStat(MapleStat.AVAILABLEAP, c.getPlayer().getRemainingAp());
            c.getPlayer().dropMessage(5, "加点成功您的 " + StringUtil.makeEnumHumanReadable(this.stat.name()) + " 提高了 " + change + " 点.");
            return 1;
        }
    }
    
        public static class YQ extends PlayerCommand.DistributeStatCommands {

        public YQ() {
            this.stat = MapleStat.运气;
        }
    }

    public static class ZL extends PlayerCommand.DistributeStatCommands {

        public ZL() {
            this.stat = MapleStat.智力;
        }
    }

    public static class MJ extends PlayerCommand.DistributeStatCommands {

        public MJ() {
            this.stat = MapleStat.敏捷;
        }
    }

    public static class LL extends PlayerCommand.DistributeStatCommands {

        public LL() {
            this.stat = MapleStat.力量;
        }
    }

    public static class LUK extends PlayerCommand.DistributeStatCommands {

        public LUK() {
            this.stat = MapleStat.运气;
        }
    }

    public static class INT extends PlayerCommand.DistributeStatCommands {

        public INT() {
            this.stat = MapleStat.智力;
        }
    }

    public static class DEX extends PlayerCommand.DistributeStatCommands {

        public DEX() {
            this.stat = MapleStat.敏捷;
        }
    }

    public static class STR extends PlayerCommand.DistributeStatCommands {

        public STR() {
            this.stat = MapleStat.力量;
        }
    }
}