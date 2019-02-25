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
        var selStr = "您好，请您选择您需要的功能:\r\n#b#L0#领取10-49级等级奖励#l\r\n#L1#领取50-99级等级奖励#l\r\n#L2#领取100-149级等级奖励#l\r\n#L3#领取150-199级等级奖励#l\r\n#L4#领取200级等级奖励#l";
        cm.sendSimpleS(selStr,2);
    } else if (status == 1) {
        switch (selection) {
        case 0:
	if(cm.getBossLog("10级奖励",1) < 1 && (cm.getPlayer().getLevel() > 9 && cm.getPlayer().getLevel() < 50)){
		cm.gainMeso(2000000);
		cm.gainNX(2,1000);
		cm.setBossLog("10级奖励",1);
		cm.sendOk("你成功领取奖励");
		cm.worldMessage(cm.getChar().getName() + "玩家成功领取10-49等级奖励.");
		cm.dispose();
	} else {
		cm.sendOk("你的等级不满足条件.\r\n注:等级奖励只能领取一次.");
		cm.dispose();
	}
        break;
        case 1:
	if(cm.getBossLog("50级奖励",1) < 1 && (cm.getPlayer().getLevel() > 49 && cm.getPlayer().getLevel() < 100) && cm.getSpace(5)>2){
		cm.gainMeso(2000000);
		cm.gainNX(2,2000);
		cm.gainItem(5074000,5);
		cm.gainItem(5040005,10);//超时空券
		cm.setBossLog("50级奖励",1);
		cm.sendOk("你成功领取奖励");
		cm.worldMessage(cm.getChar().getName() + "玩家成功领取49-100等级奖励.");
		cm.dispose();
	} else {
		cm.sendOk("你的等级不满足条件，或没有足够的背包空间\r\n#b(特殊栏3个空位以上).\r\n注:等级奖励只能领取一次.");
		cm.dispose();
	}
        break;
        case 2:
	if(cm.getBossLog("100级奖励",1) < 1 && (cm.getPlayer().getLevel() > 99 && cm.getPlayer().getLevel() < 150) && cm.getSpace(5)>1){
		cm.gainMeso(2000000);
		cm.gainNX(2,3000);
		cm.gainItem(5074000,10);//白骨高级喇叭
		cm.setBossLog("100级奖励",1);
		cm.sendOk("你成功领取奖励");
		cm.worldMessage(cm.getChar().getName() + "玩家成功领取99-150等级奖励.");
		cm.dispose();
	} else {
		cm.sendOk("你的等级不满足条件，或没有足够的背包空间\r\n#b(特殊栏2个空位以上).\r\n注:等级奖励只能领取一次.");
		cm.dispose();
	}
            break;
        case 3:
	if(cm.getBossLog("150级奖励",1) < 1 && (cm.getPlayer().getLevel() > 149 && cm.getPlayer().getLevel() < 200) && (cm.getSpace(5)>2 && cm.getSpace(3)>1)){
		cm.gainMeso(2000000);
		cm.gainNX(2,4000);
		cm.gainItem(5064000,5,7);//白骨高级喇叭
		cm.gainItem(3010155,1);//暗影双刀的猫头鹰椅子
		cm.gainItem(5390002,10);//爱心情景喇叭
		cm.setBossLog("150级奖励",1);
		cm.sendOk("你成功领取奖励");
		cm.worldMessage(cm.getChar().getName() + "玩家成功领取149-199等级奖励.");
		cm.dispose();
	} else {
		cm.sendOk("你的等级不满足条件，或没有足够的背包空间\r\n#b(特殊栏3个空位以上,设置栏1个空位以上).\r\n注:等级奖励只能领取一次.");
		cm.dispose();
	}
            break;
        case 4:
	if(cm.getBossLog("200级奖励",1) < 1 && cm.getPlayer().getLevel() > 199 && cm.getSpace(5)>2){
		cm.gainMeso(2000000);
		cm.gainNX(2,5000);
		//cm.gainItem(5062002,15,7);
		cm.gainItem(5390002,10);
		cm.setBossLog("200级奖励",1);
		cm.sendOk("你成功领取奖励");
		cm.worldMessage(cm.getChar().getName() + "玩家成功领取200等级奖励.");
		cm.dispose();
	} else {
		cm.sendOk("你的等级不满足条件，或没有足够的背包空间\r\n#b(特殊栏3个空位以上).#b\r\n注:等级奖励只能领取一次.");
		cm.dispose();
	}
            break;
        }
    }
}