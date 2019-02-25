var status = 0;
var typed=0;
var lvlitem=new Array("1002547","1002550","1002551","1002649","1002773","1082163","1082164","1082167","1082168","1082216","1052071","1052072","1052075","1052134","1092060","1072268","1072269","1072272","1072273","1072321","1002776","1002777","1002778","1002779","1002780","1002790","1002791","1002792","1002793","1002794","1082234","1082235","1082236","1082237","1082238","1082239","1082240","1082241","1082242","1082243","1052155","1052157","1052158","1052159","1052160","1052162","1052163","1052164","1092057","1092058","1092059","1072355","1072356","1072357","1072358","1072359","1072361","1072362","1072363","1072364","1072365","1003177","1003178","1003179","1003180","1003181","1102280","1102281","1102282","1102283","1102284","1082300","1082301","1082302","1082303","1082304","1052319","1052320","1052321","1052322","1052323","1072490","1072491","1072492","1072493","1072494","1372039","1372040","1372041","1372042");
var yzxzitem=new Array("3010002","3010003","3010006","3010007","3010008","3010008","3010009","3010028","3010024","3010064","3010151","1142283","1142292","1142293","1142305","1142306","1142307","1142249","1142119","1142120","1142084","1142002","1142001","1142004");
var xpdmitem=new Array("1122058","1012270","1122007","1152062","1182000","1182001","1182002","1182003","1182004","1182005","1112402","1122007");
var csitem=new Array("1532043","1522026","1352007","1003360","1003359","1003349");
var bsitem=new Array("1002762","1002763","1002764","1002765","1002766");

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (mode == 0 && status == 0) {
			cm.dispose();
			return;
		}
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
			cm.sendSimple("怎么样，挑战#r<公园副本>#k了吗?战利品#r<怪物公园纪念币>#k#v4310020#交给我吧，我会给你丰厚的报酬的！#b\r\n#L1#1.使用纪念币抽全属性装备<机率极高><110-130级装备>#l\r\n#L2#2.使用纪念币抽勋章，稀有椅子<机率极高>#l\r\n#L3#3.使用纪念币抽取休彼德蔓的徽章,胡子,护肩#l\r\n#L4#4.使用纪念币购买休彼德蔓的贴身物品,护肩等..#l\r\n#L6#5.新英雄达人,使用纪念币换取火炮手,双弩,恶魔猎手装备#l\r\n#L7#6.使用纪念币换取给力绝版帽！猜猜我是谁~！#l\r\n#L8#8.更多功能,敬请期待..#l");					
		} else if (status == 1) {
			if (selection == 1) {
				typed=1;
				cm.sendNext("如果您有30个纪念币，可以在这里抽取110-130级的全属性装备(除武器)，属性在(150~300)之间,属性高低就需要看你的人品啦！#r注意，有50%的机率一无所有#k ，希望你有好的运气 ，点击下一步开始抽取.");
			} else if (selection == 2) {
				typed=2;
				cm.sendNext("您是收集勋章的达人吗？还是收集椅子的达人？不用花人民币就能轻松得到的机会来了，喜欢收集勋章和椅子的玩家这下有福啦！我这里有全服最全的椅子和勋章，如果您有20个纪念币，可以在这里随机抽取勋章或者椅子，#r注意，有50%的机率一无所有#k ，点击下一步开始抽取.");
			} else if (selection == 3) {
				typed=3;
				cm.sendNext("如果您有30个纪念币，可以在这里抽取休彼德蔓的贴身全属性装备，属性在(1~50)之间,属性高低就需要看你的人品啦！#r注意，有50%的机率一无所有#k ，希望你有好的运气 ，点击下一步开始抽取.");
			} else if (selection == 4) {
				typed=4;
				cm.sendNext("我这里出售的都是全属性+15的休彼德蔓纪念品，如果你需要，就买一个吧，点击下一步购买!");
			} else if (selection == 5) {
				typed=5;
				cm.sendNext("您是挑战达人吗？每个副本最后一关都是超强悍的BOSS统领，如果你的组队能成功击杀完所有怪物，就会给您的组队每位成员增加#r一次挑战成功次数#k！当然了，我已经为英雄们准备了礼物，如果你认为你达到了，请点击下一步,#e每天的挑战成功次数都会全清0,请务必在当天领取哦!");	
			} else if (selection == 6) {
				typed=6;
				cm.sendNext("您是新英雄吗？#b双弩精灵，火炮手#k已经相继开放,#b恶魔猎手#k还会远吗.如果你拥有#r足够的#k纪念币,那就勇敢的点击下一步换取您心爱的装备吧!");
			} else if (selection == 7) {
				typed=7;
				var selStr="#e选一个吧。相信你会喜欢的..#n\r\n";
				for (var i = 0; i < bsitem.length; i++) {//itemyz.length
						selStr += "#b#L" + i + "##z" +bsitem[i]+"# #d需要 #r30#d个纪念币#l\r\n";
					}
				cm.sendSimple(selStr);
			} else if (selection == 8) {
				cm.dispose();
			}
		} else if (status == 2) {
			var ii = Packages.server.MapleItemInformationProvider.getInstance();
			if(typed==1){
				if(cm.haveItem(4310020,30)==false){
					cm.sendOk("真遗憾，你的纪念币还不足30个，抽奖失败！");
					cm.dispose();
					return;
				}
				var chanced = Math.floor(Math.random()*lvlitem.length);
				var chance = Math.floor(Math.random()*2);
				var lvstr = Math.floor(Math.random()*150)+150;
				var lvdex = Math.floor(Math.random()*150)+150;
				var lvint = Math.floor(Math.random()*150)+150;
				var lvluk = Math.floor(Math.random()*150)+150;
				cm.gainItem(4310020,-30);
				if(chance==1){
				       var ii = Packages.server.MapleItemInformationProvider.getInstance();		              
               	                       var type = Packages.constants.GameConstants.getInventoryType(lvlitem[chanced]); //获得装备的类形
                	               var toDrop = ii.randomizeStats(ii.getEquipById(lvlitem[chanced])).copy(); // 生成一个Equip类
                	               toDrop.setStr(lvstr);
                	               toDrop.setDex(lvdex);
                	               toDrop.setInt(lvint);
                	               toDrop.setLuk(lvluk);
                	               cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
                	               cm.getC().getSession().write(Packages.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包
				       cm.sendOk("恭喜，您获得了：#v"+lvlitem[chanced]+"#\r\n力量+"+lvstr+" 敏捷+"+lvdex+" 智力+"+lvint+" 运气+"+lvluk);
				       cm.worldMessage("[公告]恭喜[" + cm.getChar().getName() + "]小童鞋获得★"+ii.getName(lvlitem[chanced])+"★(全属性哦)你还等什么，快去怪物公园刷吧！");
				}else{
					cm.sendOk("真遗憾，此次抽奖，一无所有，继续努力吧！");
				}
				cm.dispose();
				return;
			}
			if(typed==2){
				if(cm.haveItem(4310020,20)==false){
					cm.sendOk("真遗憾，你的纪念币还不足20个，抽奖失败！");
					cm.dispose();
					return;
				}
				var chanced = Math.floor(Math.random()*yzxzitem.length);
				var chance = Math.floor(Math.random()*2);
				cm.gainItem(4310020,-20);
				if(chance==1){
					cm.gainItem(yzxzitem[chanced],1);
					cm.sendOk("恭喜，您获得了：#v"+yzxzitem[chanced]+"#");
					cm.worldMessage("[公告]恭喜[" + cm.getChar().getName() + "]小童鞋获得★"+ii.getName(yzxzitem[chanced])+"★你还等什么，快去怪物公园刷吧！");
				}else{
					cm.sendOk("真遗憾，此次抽奖，一无所有，继续努力吧！");
				}
				cm.dispose();
				return;
			}
			if(typed==3){
				if(cm.haveItem(4310020,30)==false){
					cm.sendOk("真遗憾，你的纪念币还不足30个，抽奖失败！");
					cm.dispose();
					return;
				}
				var chanced = Math.floor(Math.random()*xpdmitem.length);
				var chance = Math.floor(Math.random()*2);
				var lvstr = Math.floor(Math.random()*50)+1;
				var lvdex = Math.floor(Math.random()*50)+1;
				var lvint = Math.floor(Math.random()*50)+1;
				var lvluk = Math.floor(Math.random()*50)+1;
				cm.gainItem(4310020,-30);
				if(chance==1){
					 var ii = Packages.server.MapleItemInformationProvider.getInstance();             
               	                       var type = Packages.constants.GameConstants.getInventoryType(xpdmitem[chanced]); //获得装备的类形
                	               var toDrop = ii.randomizeStats(ii.getEquipById(xpdmitem[chanced])).copy(); // 生成一个Equip类
                	               toDrop.setStr(lvstr);
                	               toDrop.setDex(lvdex);
                	               toDrop.setInt(lvint);
                	               toDrop.setLuk(lvluk);
                	               cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
                	               cm.getC().getSession().write(Packages.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包
					cm.sendOk("恭喜，您获得了：#v"+xpdmitem[chanced]+"#\r\n力量+"+lvstr+" 敏捷+"+lvdex+" 智力+"+lvint+" 运气+"+lvluk);
					cm.worldMessage("[公告]恭喜[" + cm.getChar().getName() + "]小童鞋获得★"+ii.getName(xpdmitem[chanced])+"★你还等什么，快去怪物公园刷吧！");
				}else{
					cm.sendOk("真遗憾，此次抽奖，一无所有，继续努力吧！");
				}
				cm.dispose();
				return;
			}
			if(typed==4){
				typed=40;
				var selStr="看看有没有您喜欢的.\r\n";
				for (var i = 0; i < xpdmitem.length; i++) {//itemyz.length
					selStr += "#b#L" + i + "##z" +xpdmitem[i]+"#<全属性+15> #d需要 #r60#d个纪念币#l\r\n";
			}
				cm.sendSimpleS(selStr,2);
			}
			if(typed==5){
				typed=50;
				var selStr="#e您的<成功>挑战次数:#r"+cm.getChar().gettz()+".#n\r\n";
				for (var i = 0; i < xpdmitem.length; i++) {//itemyz.length
					selStr += "#b#L" + i + "##z" +xpdmitem[i]+"#<全属性+100,攻击+30> #d需要 #r30#d次#l\r\n";
				}
				cm.sendSimpleS(selStr,2);
			}
			if(typed==6){
				typed=60;
				var selStr="#e选一个吧。#n\r\n";
				for (var i = 0; i < csitem.length; i++) {//itemyz.length
					if(i>2){
						selStr += "#b#L" + i + "##z" +csitem[i]+"#<全属性+188,含攻击> #d需要 #r488#d个纪念币#l\r\n";
					}else{
						selStr += "#b#L" + i + "##z" +csitem[i]+"#<全属性+188,含攻击> #d需要 #r108#d个纪念币#l\r\n";
					}
					
				}
				cm.sendSimpleS(selStr,2);
			}
			if(typed==7){
				if(cm.haveItem(4310020,30)==false){
						cm.sendOk("真遗憾，你的纪念币还不足30个，换取失败！");
						cm.dispose();
						return;
				}
				var ii = Packages.server.MapleItemInformationProvider.getInstance();		              
               	                var type = Packages.constants.GameConstants.getInventoryType(bsitem[selection]); //获得装备的类形
                	        var toDrop = ii.randomizeStats(ii.getEquipById(bsitem[selection])).copy(); // 生成一个Equip类
                	        toDrop.setStr(188);
                	        toDrop.setDex(188);
                	        toDrop.setInt(188);
                	        toDrop.setLuk(188);
				toDrop.setMatk(188);
				toDrop.setWatk(188);
                	        cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
                	        cm.getC().getSession().write(Packages.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包
				cm.gainItem(4310020,-30);
				cm.sendOk("恭喜，换取成功，扣除30个纪念币，继续努力，做收集达人！");
				cm.dispose();
				return;
			}
		} else if (status == 3) {
			if(typed==40){
				if(cm.haveItem(4310020,60)==false){
					cm.sendOk("真遗憾，你的纪念币还不足60个，抽奖失败！");
					cm.dispose();
					return;
				}
				cm.gainItem(4310020,-60);
				var ii = Packages.server.MapleItemInformationProvider.getInstance();		              
               	                var type = Packages.constants.GameConstants.getInventoryType(xpdmitem[selection]); //获得装备的类形
                	        var toDrop = ii.randomizeStats(ii.getEquipById(xpdmitem[selection])).copy(); // 生成一个Equip类
                	        toDrop.setStr(15);
                	        toDrop.setDex(15);
                	        toDrop.setInt(15);
                	        toDrop.setLuk(15);
                	        cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
                	        cm.getC().getSession().write(Packages.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包
				cm.sendOk("恭喜，购买成功！");
				cm.dispose();
				return;
			}
			if(typed==50){
				if(cm.getChar().gettz()<30){
					cm.sendOk("真遗憾，你的<成功>挑战次数不足30次！");
					cm.dispose();
					return;
				}
				cm.getChar().settz(-30);
				cm.gainTimeItem(xpdmitem[selection],30*24*60*60*1000,1,100,100,100,100,30,30);
				cm.sendOk("恭喜，换取成功，扣除30次<成功>挑战次数，继续努力，做收集达人！");
				cm.dispose();
				return;
			}
			if(typed==60){
				if(selection>2){
					if(cm.haveItem(4310020,488)==false){
						cm.sendOk("真遗憾，你的纪念币还不足488个，抽奖失败！");
						cm.dispose();
						return;
					}
					cm.gainItem(4310020,-488);
					cm.sendOk("恭喜，换取成功，扣除488个纪念币，继续努力，做收集达人！");
				}else{
					if(cm.haveItem(4310020,108)==false){
						cm.sendOk("真遗憾，你的纪念币还不足108个，抽奖失败！");
						cm.dispose();
						return;
					}
					cm.gainItem(4310020,-188);
					cm.sendOk("恭喜，换取成功，扣除108个纪念币，继续努力，做收集达人！");
				}
				var ii = Packages.server.MapleItemInformationProvider.getInstance();		              
               	                var type = Packages.constants.GameConstants.getInventoryType(csitem[selection]); //获得装备的类形
                	        var toDrop = ii.randomizeStats(ii.getEquipById(csitem[selection])).copy(); // 生成一个Equip类
                	        toDrop.setStr(188);
                	        toDrop.setDex(188);
                	        toDrop.setInt(188);
                	        toDrop.setLuk(188);
				toDrop.setMatk(188);
				toDrop.setWatk(188);
                	        cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
                	        cm.getC().getSession().write(Packages.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包
				cm.dispose();
				return;
			}
			cm.dispose();
		}
	}
}
