var status = 0;
var fstype=0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        if (status == 0) {
            cm.sendNext("伟大的#b#h ##k，我是本服唯一发布副本和任务的NPC，你要进行副本和任务的挑战吗？奖励当然是非常丰富的");
            
        } else if (status == 1) {  
			cm.sendSimple("你好伟大的#b#h ##k，我是本服发布任务和副本的NPC，本服的副本和任务都在我这里进行哟，当然平时的活动和新加的副本也是这里啦，准备好了吗？\r\n#r==============开放中的任务和副本===================#k\r\n#L4##r[主题活动]#k国庆7天乐#l\r\n#L0##r[每日任务]#k正义之章#l\r\n#L1##r[每日任务]#k每日打工#l\r\n#L2##b[副本挑战]#k击退海盗#l　　#L12##d查看任务介绍#k\r\n#L3##b[副本挑战]#k怪物公园#l\r\n\r\n#g==============近期开放的任务和副本=================#k\r\n#b#L5##b[副本挑战]#k玩具城组队#l\r\n#L6##b[副本挑战]#k拯救海底世界#l");
        } else if (status == 2) {
            if (selection==0){//正义之章
				cm.dispose();
        			cm.openNpc(9010041,1);
            }else if (selection==1){//每日刷钱
				cm.dispose();
        			cm.openNpc(9010041,2);
            }else if (selection==2){//击退海盗的奖励
        			cm.warp(251010404);
        			cm.dispose();
            }else if (selection==3){//怪物公园
        			cm.warp(951000000);
        			cm.dispose();
            }else if (selection==4){//国庆7天
        			fstype = 4;
        			cm.sendSimple("#r活动.1#k：国庆节到了，终结者怎会没有活动呢，现在每天可签到，连续签到5天就可以获得国庆的转属的礼包，礼包含有祝福卷轴，魔方，椅子等哟\r\n#r活动.2#k：定点搜集物品任务活动，有几率获得(黄金罗盘,可到宝物岛1分钟抢夺金猪梦盒子)(金梦猪盒子可以开到GM卷,有孔装备)\r\n#r活动.3#k：国庆7天开放冲值大回馈哟冲100出比例还送20,200还送40,300还送80\r\n#r活动.4#k：国庆每天晚上20：00还有GM的在线活动哟\r\n你在国庆7天乐活动已签到天数为：#r" + cm.getBossLog("qddq", 1) + "#k\r\n#L3##b开始定点搜集物品活动#k\r\n#L1##b我要签到#k#l#L2##r我要领取国庆礼包#k#l");
            }else if (selection==5){//玩具组队
        			cm.sendOk("暂时未开放");
        			cm.dispose();
            }else if (selection==6){//拯救海底
        			cm.sendOk("暂时未开放");
        			cm.dispose();
           }else if (selection==12){//击退海盗的奖励
        			cm.sendOk("我正在等待勇敢的冒险家。请大家用自己的力量和智慧，一起破解难题，击退强大的#r海盗军团！\r\n\r\n - #e等级#n：70级以上#r（推荐等级：120～200 ）#k\r\n - #e限制时间#n：10分钟\r\n - #e参加人数#n：3～4人\r\n - #e通关获得物品#n：\r\n　#v4031996##v4031995##v4031994v# 蘑菇奖牌#b（随机获得）#k\r\n - #e通关随机物品#n：\r\n　 #b蓝蜗牛邮票,GM必成卷轴,超级药水,外星装备(有孔),圣杯");
        			cm.dispose();
			}
       } else if (status == 3) {
			if(fstype==1){//正义币
				fstype=13;
				var ii = Packages.server.MapleItemInformationProvider.getInstance();
				var item = cm.getInventory(1).getItem(1);
				var statup = new java.util.ArrayList();
				if(item==null){
				cm.sendOk("对不起,你装备栏第一格没有装备!"); 
				cm.dispose(); 
				}else if(ii.isCash(item.getItemId())==true){
				cm.sendOk("商城物品暂不支持.");
				cm.dispose();
				}else{
					cm.sendNext("请把装装备放在装备窗口的第一格，否则你将不能成功.\r\n请确认一下你要砸的装备是：#v"+item.getItemId()+"##z"+item.getItemId()+"#吗？");
				}
				}				
			if(fstype==2){//清理装备
				if(selection==1){
					var it;
					var texts="#r---------------请选择您要清理的装备----------------#b\r\n";
					var inv = cm.getInventory(1);
					for (var i = 1; i <= 96; i++) {
						it = inv.getItem(i);
						if (it != null) {
							texts+="#L"+i+"#装备图片:#v"+it.getItemId()+"# 装备名称及属性:#g#z"+it.getItemId()+"##l#b\r\n"
						}
					}
					fstype=101;
					cm.sendSimpleS(texts,2);
				}else if(selection==2){
					var it;
					var texts="#r---------------请选择您要清理的装备----------------#b\r\n";
					var inv = cm.getInventory(5);
					for (var i = 1; i <= 96; i++) {
						it = inv.getItem(i);
						if (it != null) {
							texts+="#L"+i+"#装备图片:#v"+it.getItemId()+"# 装备名称及属性:#g#z"+it.getItemId()+"##l#b\r\n"
						}
					}
					fstype=102;
					cm.sendSimpleS(texts,2);
				}
				}
			if(fstype==3){//黄金枫叶
				fstype=14;
				var ii = Packages.server.MapleItemInformationProvider.getInstance();
				var item = cm.getInventory(1).getItem(1);
				var statup = new java.util.ArrayList();
				if(item==null){
				cm.sendOk("对不起,你装备栏第一格没有装备!"); 
				cm.dispose(); 
				}else if(ii.isCash(item.getItemId())==true){
				cm.sendOk("商城物品暂不支持.");
				cm.dispose();
				}else{
					cm.sendNext("请把装装备放在装备窗口的第一格，否则你将不能成功.\r\n请确认一下你要砸的装备是：#v"+item.getItemId()+"##z"+item.getItemId()+"#吗？");
				}
				}
			if(fstype==4){//国庆活动
			if (selection == 1) {
            		var count = cm.getBossLog("qddq", 1);
            		if (cm.getBossLog("qddq") == 0) {
            		cm.setBossLog("qddq",1,count+1);
            		cm.sendOk("签到成功.");
			cm.worldMessage("[国庆7天乐]：玩家["+cm.getChar().getName()+"]完成了今天的国庆签到,又想国庆礼包前进了一步");
            		cm.dispose();
            		 } else {
            		cm.sendOk("你已经签到过了,明天再来吧");
            		cm.dispose();
            		}
            		} else if (selection == 2) {
            		if (cm.getBossLog("qddq", 1) >= 5 && cm.getBossLog("qdlb", 1) == 0) {
            		cm.setBossLog("qdlb",1);
                        cm.gainItem(5062002, 10); //高级神奇魔方
                        cm.gainItem(5062001, 10); //混沌神奇魔方
                        cm.gainItem(2340000, 10); //祝福卷轴
			cm.gainItem(2430008,+1);
            		cm.sendOk("领取成功.");
			cm.worldMessage("[国庆7天乐]：玩家["+cm.getChar().getName()+"]领取了国庆节的礼物包");
            		cm.dispose();
            		 } else {
            		cm.sendOk("你已经领取过了,或者你签到未到6天");
            		cm.dispose();
            		}
            		} else if (selection == 4) {
			cm.sendOk("#e#r10月5号<活动1>#k#n\r\n入场时间：20：30，开始时间：20：30\r\n#b<第一论晋级赛射手跳跳>#k\r\n玩家全部站到起点,GM叫开始后,玩家开始跳,晋级8位玩家然后进行第二轮比赛\r\n#b<第二论36数字游戏>#k\r\n进入场地后,玩家请听GM指挥,GM会叫全部玩家所有站在一个点上面,然后GM随机抽出5位玩家分别分为1-2-3-4-5号,并排站好,然后GM说开始,1号玩家就可以开始数数字了,1号玩家只能数1,或者2,当以后玩家数后,2号玩家接着1号的数,如果1号数的1,那么2号玩家可以在1号数的数字的基础上+1或者+2,比如数2,或者3,依次类推,数到5号玩家后,1号接着5号的数,谁最后能说到36这个数字,便淘汰\r\n晋级到二轮活动的玩家都能获得消费币1W\r\n晋级的玩家可以玩第二轮的36\r\n第一个活动第一名：有孔全属性武器200武器(+S级星岩一个)+1万消费币+上网站名人榜\r\n第一个活动第二名：稀有椅子一把+1万消费币\r\n第一个活动第三名：一般椅子一把+1万消费币");
        			cm.dispose();
            		} else if (selection == 8) {
			cm.sendOk("#e#r10月5号<活动2>#k#n\r\n入场时间：20：40，开始时间：20：40\r\n#b<谁是卧底>#k\r\nGM会交易每个人,告诉他们的一个词语,在参与玩家中会有一人的词语不一样,然后每个人从GM排列号数来形容这个词语#r(最好不要形容得太容易,既要让本轮卧底不太懂是什么词语,又要让对友们明白你的意思)#k全部形容完后,大家根据玩家的号数投票,如果投出去的不是卧底,游戏继续,是卧底的话,卧底淘汰,卧底只要能存活到最后还剩3名玩家就晋级");
        			cm.dispose();
            		} else if (selection == 6) {
			cm.sendOk("#e#r10月2号<活动2>#k#n\r\n问答时间：20：12，结束时间：20：20\r\n#b<全能问答>#k\r\nGM每次问答的时候会给出下一题的提示,比如下一题是数学题,下面说说提示的方面\r\n#b数学题#k就是一些加减乘除,所以准备好你的计算器哟\r\n#b歪答题#k一些有意思的答题,娱乐性答案\r\n#b音乐题#kGM可能回说出一句歌词,必须回答出名字,或者GM会说关键字,玩家必须说出一句带关键字的歌句\r\n#b终结者知识#k一些关于终结者冒险岛的常识\r\n答题必须在#r市场答,直接在聊天窗输入答案,以公屏的文字决定快慢,也可以使用喇叭答题#k\r\n答对一道的玩家将能获得一个黄金罗盘(GM卷,有孔装备)#k");
			cm.dispose();
            		} else if (selection == 7) {
			cm.sendOk("#e#r10月3号<活动2>#k#n\r\n开始时间：21：50\r\nGM会进行5轮的躲猫猫活动,GM藏好会,会喇叭提示大家GM所在的城市,当然提示的4个城市只有1个是对的哟,找到的玩家可以获得黄金罗盘一个");
			cm.dispose();
            }else if (selection==5){//怪物公园
        			//cm.warp(749080100);
			cm.sendOk("入口已经关闭");
        			cm.dispose();
            		} else if (selection == 3) { //搜集物品
			if((cm.getHour() != 15 || cm.getHour() != 19)){
			cm.sendOk("当前服务器时间:"+cm.getHour()+"点"+cm.getMin()+"分\r\n时间还没到哦.只能下午15点和晚上19点提交任务");
			cm.dispose();
			} else if (cm.haveItem(4000004,200) == false  || cm.haveItem(4000268,200) == false || cm.haveItem(4000273,200) == false || cm.haveItem(4000188,200) == false ) {
			cm.sendOk("材料不足,你需要搜集#z4000004#x200个,#z4000268#x200个,#z4000273#x200个,#z4000188#x200个\r\n注意：提交任务只能在15点和19点,所以请抓紧时间");
			cm.dispose();
			}else{
			cm.gainItem(4000004,-200);
			cm.gainItem(4000268,-200);
			cm.gainItem(4000273,-200);
			cm.gainItem(4000188,-200);
			cm.gainItem(2430008,+1);
			cm.sendOk("恭喜任务完成,获得了一个#r#z2430008##k,快去宝物岛拿好东西吧");
			cm.worldMessage("[国庆7天乐]：玩家["+cm.getChar().getName()+"]完成了国庆7天乐的任务,获得了一个黄金罗盘");
			cm.dispose();
				}
			}
			}
		}else if(status == 4){
			if(fstype==13){
				if(cm.getMeso() < price || cm.haveItem(4310034,2)==false){
					cm.sendOk("对不起,你没有足够的冒险币,或者是没有足够的#z4310034#");
					cm.dispose();
					return;
				}
				var chance = Math.floor(Math.random()*2);
				if(chance==1){
				var item = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.EQUIP).getItem(1).copy();
				var statup = new java.util.ArrayList();
				item.setUpgradeSlots((item.getUpgradeSlots() + 1));
				Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
				Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(),item,false);
				cm.gainMeso(-price);
				cm.gainItem(4310034,-2);
				cm.sendOk("恭喜你成功拉，快快看你的包裹吧！");
				cm.worldMessage("[武器升级]：恭喜[" + cm.getChar().getName() + "]在市场裂空之鹰处，使用正义币为武器提升了1次砸卷次数");
				cm.dispose();
				}else{
				cm.gainMeso(-price/2);
				cm.gainItem(4310034,-2);
				cm.sendOk("真遗憾，升级失败");
				}
				cm.dispose();
				return;
			}
			if(fstype==14){
				if(cm.getMeso() < price || cm.haveItem(4310003,1)==false){
					cm.sendOk("对不起,你没有足够的冒险币,或者是没有足够的#z4310003#");
					cm.dispose();
					return;
				}
				var chance = Math.floor(Math.random()*2);
				if(chance==1){
				var item = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.EQUIP).getItem(1).copy();
				var statup = new java.util.ArrayList();
				item.setUpgradeSlots((item.getUpgradeSlots() + 1));
				Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
				Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(),item,false);
				cm.gainMeso(-price);
				cm.gainItem(4310003,-1);
				cm.sendOk("恭喜你成功拉，快快看你的包裹吧！");
				cm.worldMessage("[武器升级]：恭喜[" + cm.getChar().getName() + "]在市场裂空之鹰处，使用黄金枫叶为武器提升了1次砸卷次数");
				cm.dispose();
				}else{
				cm.gainMeso(-price/2);
				cm.gainItem(4310003,-1);
				cm.sendOk("真遗憾，升级失败");
				}
				cm.dispose();
				return;
			}
			if(fstype==15){
				if(cm.getMeso() < price || cm.haveItem(4032733,30)==false){
					cm.sendOk("对不起,你没有足够的冒险币,或者是没有足够的#z4032733#");
					cm.dispose();
					return;
				}
				var chance1 = Math.floor(Math.random()*2);
				if(chance1==1){
				var item = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.EQUIP).getItem(1).copy();
				var ii = Packages.server.MapleItemInformationProvider.getInstance();
				var chance = Math.floor(Math.random()*100);
				var lvsj = Math.floor(Math.random()*20)+10;
				cm.gainMeso(-price);
				cm.gainItem(4032733,-30);
				if(chance<=5){//watk
				item.setWatk(item.getWatk()*1+lvsj);
				Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
				Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(),item,false);
				cm.sendOk("恭喜，成功给装备增加了:#r"+lvsj+"#k攻击.");
				cm.worldMessage("[装备升级]：恭喜[" + cm.getChar().getName() + "]在市场裂空之鹰处，使用彩虹枫叶提升了武器的攻击");
				}else if(chance>5 && chance<=20){//matk
				item.setMatk(item.getMatk()*1+lvsj);
				Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
				Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(),item,false);
				cm.sendOk("恭喜，成功给装备增加了:#r"+lvsj+"#k魔攻.");
				cm.worldMessage("[装备升级]：恭喜[" + cm.getChar().getName() + "]在市场裂空之鹰处，使用彩虹枫叶提升了武器的魔法攻击");
				}else if(chance>20 && chance<=40){//str
				item.setStr(item.getStr()*1+lvsj);
				Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
				Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(),item,false);
				cm.sendOk("恭喜，成功给装备增加了:#r"+lvsj+"#k力量.");
				cm.worldMessage("[装备升级]：恭喜[" + cm.getChar().getName() + "]在市场裂空之鹰处，使用彩虹枫叶提升了武器的力量");
				}else if(chance>40 && chance<=60){//dex
				item.setDex(item.getDex()*1+lvsj);
				Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
				Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(),item,false);
				cm.sendOk("恭喜，成功给装备增加了:#r"+lvsj+"#k敏捷.");
				cm.worldMessage("[装备升级]：恭喜[" + cm.getChar().getName() + "]在市场裂空之鹰处，使用彩虹枫叶提升了武器的敏捷");
				}else if(chance>60 && chance<=80){//luk
				item.setInt(item.getInt()*1+lvsj);
				Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
				Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(),item,false);
				cm.sendOk("恭喜，成功给装备增加了:#r"+lvsj+"#k智力.");
				cm.worldMessage("[装备升级]：恭喜[" + cm.getChar().getName() + "]在市场裂空之鹰处，使用彩虹枫叶提升了武器的智力");
				}else if(chance>80){//int
				item.setLuk(item.getLuk()*1+lvsj);
				Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
				Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(),item,false);
				cm.sendOk("恭喜，成功给装备增加了:#r"+lvsj+"#k运气.");
				cm.worldMessage("[装备升级]：恭喜[" + cm.getChar().getName() + "]在市场裂空之鹰处，使用彩虹枫叶提升了武器的运气");
			}
				}else{
				cm.gainMeso(-price/2);
				cm.gainItem(4032733,-30);
				cm.sendOk("真遗憾，升级失败");
				}
				cm.dispose();
				return;
			}
			if(fstype==101){
				Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, selection, 1, true);
				cm.sendOk("恭喜,此道具已被清除.");
				cm.dispose();
			}
			if(fstype==102){
				Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.CASH, selection, 1, true);
				cm.sendOk("恭喜,此道具已被清除.");
				cm.dispose();
			}
				}
    }
}
