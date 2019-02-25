var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (status == 0 && mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
    if (status == 0) {
        var selStr = "您好，请您选择您需要的功能:\r\n#b#L0#领取10-49转奖励#l\r\n#L1#领取50-99转奖励#l\r\n#L2#领取100-149转奖励#l\r\n#L3#领取150-199转奖励#l\r\n#L4#领取200转奖励#l\r\n#L5#领取300转奖励#l";
        cm.sendSimpleS(selStr,2);
    } else if (status == 1) {
        switch (selection) {
        case 0:
	if(cm.getBossLog("10转奖励",1) < 1 && (cm.getPlayer().getReborns() > 9 && cm.getPlayer().getReborns() < 50) &&(cm.getSpace(1)>1 && cm.getSpace(3)>1 && cm.getSpace(4)>1)){
		cm.gainMeso(5000000);
		cm.gainNX(1000);
		cm.gainItem(4031454,2);	//圣杯
		cm.gainItem(1082149,1);	//工地手套(褐)
		var ii = Packages.server.MapleItemInformationProvider.getInstance();
            var type = Packages.constants.GameConstants.getInventoryType(1082149);// 物品代码
            var toDrop = ii.randomizeStats(ii.getEquipById(1082149)).copy(); // 物品代码
            toDrop.setStr(30); //装备力量
            toDrop.setDex(30); //装备敏捷
            toDrop.setInt(30); //装备智力
            toDrop.setLuk(30); //装备运气
            toDrop.setHp(30);
            toDrop.setMp(30);
            toDrop.setMatk(30);//物理攻击
            toDrop.setWatk(30);//魔法攻击
            toDrop.setMdef(30);//物理防御
            toDrop.setWdef(30);//魔法防御
            cm.getPlayer().getInventory(type).addItem(toDrop); //将这个装备放入包中
            cm.getC().getSession().write(Packages.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包
			cm.gainItem(3010038,1);//空气沙发
		cm.setBossLog("10转奖励",1);
		cm.sendOk("你成功领取奖励");
		cm.worldMessage(cm.getChar().getName() + "玩家成功领取10-49等转奖励.");
		cm.dispose();
	} else {
		cm.sendOk("你的等级不满足条件.\r\n注:等转奖励只能领取一次.");
		cm.dispose();
	}
        break;
        case 1:
	if(cm.getBossLog("50转奖励",1) < 1 && (cm.getPlayer().getReborns() > 49 && cm.getPlayer().getReborns() < 100)&&(cm.getSpace(1)>1 && cm.getSpace(3)>1 && cm.getSpace(4)>1 && cm.getSpace(5)>1)){
		cm.gainMeso(10000000);
		cm.gainNX(2000);
		cm.gainItem(4031454,5);	//圣杯
		cm.gainItem(1022029,1);	//环形猪鼻子眼镜
		var ii = Packages.server.MapleItemInformationProvider.getInstance();
            var type = Packages.constants.GameConstants.getInventoryType(1022029);// 物品代码
            var toDrop = ii.randomizeStats(ii.getEquipById(1022029)).copy(); // 物品代码
            toDrop.setStr(50); //装备力量
            toDrop.setDex(50); //装备敏捷
            toDrop.setInt(50); //装备智力
            toDrop.setLuk(50); //装备运气
            toDrop.setHp(50);
            toDrop.setMp(50);
            toDrop.setMatk(50);//物理攻击
            toDrop.setWatk(50);//魔法攻击
            toDrop.setMdef(50);//物理防御
            toDrop.setWdef(50);//魔法防御
            cm.getPlayer().getInventory(type).addItem(toDrop); //将这个装备放入包中
            cm.getC().getSession().write(Packages.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包
		cm.gainItem(5074000,10);//白骨高级喇叭
		cm.gainItem(3010001,1);//蓝色木椅
		//cm.gainItem(5040005,10);//超时空券
		cm.setBossLog("50转奖励",1);
		cm.sendOk("你成功领取奖励");
		cm.worldMessage(cm.getChar().getName() + "玩家成功领取49-100等转奖励.");
		cm.dispose();
	} else {
		cm.sendOk("你的等级不满足条件，或没有足够的背包空间\r\n#b(每种栏3个空位以上).\r\n注:等转奖励只能领取一次.");
		cm.dispose();
	}
        break;
        case 2:
	if(cm.getBossLog("100转奖励",1) < 1 && cm.getPlayer().getReborns() > 99 && cm.getPlayer().getReborns() < 150 &&(cm.getSpace(1)>1 && cm.getSpace(3)>1 && cm.getSpace(4)>1 && cm.getSpace(5)>1)){
		cm.gainMeso(100000000);
		cm.gainNX(3000);
		cm.gainItem(4031454,10);//圣杯
		cm.gainItem(1032099,1);	//葡萄耳环
		var ii = Packages.server.MapleItemInformationProvider.getInstance();
            var type = Packages.constants.GameConstants.getInventoryType(1032099);// 物品代码
            var toDrop = ii.randomizeStats(ii.getEquipById(1032099)).copy(); // 物品代码
            toDrop.setStr(100); //装备力量
            toDrop.setDex(100); //装备敏捷
            toDrop.setInt(100); //装备智力
            toDrop.setLuk(100); //装备运气
            toDrop.setHp(100);
            toDrop.setMp(100);
            toDrop.setMatk(100);//物理攻击
            toDrop.setWatk(100);//魔法攻击
            toDrop.setMdef(100);//物理防御
            toDrop.setWdef(100);//魔法防御
            cm.getPlayer().getInventory(type).addItem(toDrop); //将这个装备放入包中
            cm.getC().getSession().write(Packages.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包
		cm.gainItem(3010191,1);//爱情椅子
		cm.gainItem(5062000,5);//神奇魔方
		cm.gainItem(5390002,10);//爱心情景喇叭
		cm.setBossLog("100转奖励",1);
		cm.sendOk("你成功领取奖励");
		cm.worldMessage(cm.getChar().getName() + "玩家成功领取99-150等转奖励.");
		cm.dispose();
	} else {
		cm.sendOk("你的等级不满足条件，或没有足够的背包空间\r\n#b(每种栏3个空位以上).\r\n注:等转奖励只能领取一次.");
		cm.dispose();
	}
            break;
        case 3:
	if(cm.getBossLog("150转奖励",1) < 1 && cm.getPlayer().getReborns() > 149 && cm.getPlayer().getReborns() < 200 &&(cm.getSpace(1)>1 && cm.getSpace(3)>1 && cm.getSpace(4)>1 && cm.getSpace(5)>2)){
		cm.gainMeso(120000000);
		cm.gainNX(5000);
		cm.gainItem(4002002,10);//木妖邮票
		cm.gainItem(4031454,15);//圣杯
		cm.gainItem(1112586,1);	//黑天使的祝福
		var ii = Packages.server.MapleItemInformationProvider.getInstance();
            var type = Packages.constants.GameConstants.getInventoryType(1112586);// 物品代码
            var toDrop = ii.randomizeStats(ii.getEquipById(1112586)).copy(); // 物品代码
            toDrop.setStr(150); //装备力量
            toDrop.setDex(150); //装备敏捷
            toDrop.setInt(150); //装备智力
            toDrop.setLuk(150); //装备运气
            toDrop.setHp(150);
            toDrop.setMp(150);
            toDrop.setMatk(150);//物理攻击
            toDrop.setWatk(150);//魔法攻击
            toDrop.setMdef(150);//物理防御
            toDrop.setWdef(150);//魔法防御
            cm.getPlayer().getInventory(type).addItem(toDrop); //将这个装备放入包中
            cm.getC().getSession().write(Packages.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包
		cm.gainItem(3013000,1);//樱花树下
		cm.gainItem(5062000,8);//神奇魔方
		cm.gainItem(5079001,10);//蛋糕高级喇叭
		cm.gainItem(5390000,10);//爱心情景喇叭
		cm.setBossLog("150转奖励",1);
		cm.sendOk("你成功领取奖励");
		cm.worldMessage(cm.getChar().getName() + "玩家成功领取149-199等转奖励.");
		cm.dispose();
	} else {
		cm.sendOk("你的等级不满足条件，或没有足够的背包空间\r\n#b(每种栏3个空位以上).\r\n注:等转奖励只能领取一次.");
		cm.dispose();
	}
            break;
        case 4:
	if(cm.getBossLog("200转奖励",1) < 1 && cm.getPlayer().getReborns() > 199 && (cm.getSpace(1)>1 && cm.getSpace(3)>1 && cm.getSpace(4)>1 && cm.getSpace(5)>2)){
		cm.gainMeso(150000000);
		cm.gainNX(10000);
		cm.gainItem(4002002,15);//木妖邮票
		cm.gainItem(4031454,20);//圣杯
		cm.gainItem(1102206,1);	//黑色野兽披风
		var ii = Packages.server.MapleItemInformationProvider.getInstance();
            var type = Packages.constants.GameConstants.getInventoryType(1102206);// 物品代码
            var toDrop = ii.randomizeStats(ii.getEquipById(1102206)).copy(); // 物品代码
            toDrop.setStr(200); //装备力量
            toDrop.setDex(200); //装备敏捷
            toDrop.setInt(200); //装备智力
            toDrop.setLuk(200); //装备运气
            toDrop.setHp(200);
            toDrop.setMp(200);
            toDrop.setMatk(200);//物理攻击
            toDrop.setWatk(200);//魔法攻击
            toDrop.setMdef(200);//物理防御
            toDrop.setWdef(200);//魔法防御
            cm.getPlayer().getInventory(type).addItem(toDrop); //将这个装备放入包中
            cm.getC().getSession().write(Packages.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包
		cm.gainItem(3010044,1);//同一红伞下
		cm.gainItem(5062000,10);//神奇魔方
		cm.gainItem(5079002,10);//馅饼高级喇叭
		cm.gainItem(5390005,10);//小老虎情景喇叭
		cm.setBossLog("200转奖励",1);
		cm.sendOk("你成功领取奖励");
		cm.worldMessage(cm.getChar().getName() + "玩家成功领取200等转奖励.");
		cm.dispose();
	} else {
		cm.sendOk("你的等级不满足条件，或没有足够的背包空间\r\n#b(每种栏3个空位以上).#b\r\n注:等转奖励只能领取一次.");
		cm.dispose();
	}
            break;
        case 5:
	if(cm.getBossLog("300转奖励",1) < 1 && cm.getPlayer().getReborns() > 299 && (cm.getSpace(1)>2 && cm.getSpace(3)>1 && cm.getSpace(4)>1 && cm.getSpace(5)>1)){
		cm.gainMeso(200000000);
		cm.gainNX(20000);
		cm.gainItem(4002002,20);//木妖邮票
		cm.gainItem(4031454,20);//圣杯
		cm.gainItem(1072239,1);	//黄色钉鞋
		var ii = Packages.server.MapleItemInformationProvider.getInstance();
            var type = Packages.constants.GameConstants.getInventoryType(1072239);// 物品代码
            var toDrop = ii.randomizeStats(ii.getEquipById(1072239)).copy(); // 物品代码
            toDrop.setStr(300); //装备力量
            toDrop.setDex(300); //装备敏捷
            toDrop.setInt(300); //装备智力
            toDrop.setLuk(300); //装备运气
            toDrop.setHp(300);
            toDrop.setMp(300);
            toDrop.setMatk(300);//物理攻击
            toDrop.setWatk(300);//魔法攻击
            toDrop.setMdef(300);//物理防御
            toDrop.setWdef(300);//魔法防御
            cm.getPlayer().getInventory(type).addItem(toDrop); //将这个装备放入包中
            cm.getC().getSession().write(Packages.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包
		cm.gainItem(3010303,1);//加肥抱抱椅
		cm.gainItem(1000050,1);//薄荷雪水晶
		cm.gainItem(1001076,1);//樱桃雪水晶
		cm.gainItem(5062000,15);//神奇魔方
		//cm.gainItem(5079002,10);//馅饼高级喇叭
		cm.gainItem(5390005,20);//小老虎情景喇叭
		cm.setBossLog("300转奖励",1);
		cm.sendOk("你成功领取奖励");
		cm.worldMessage(cm.getChar().getName() + "玩家成功领取300等转奖励.");
		cm.dispose();
	} else {
		cm.sendOk("你的等级不满足条件，或没有足够的背包空间\r\n#b(每种栏3个空位以上).#b\r\n注:等转奖励只能领取一次.");
		cm.dispose();
	}
            break;        }
    }
}