var status = -1;
var prizeIdScroll = Array(2043003, 2043103, 2043203, 2043303, 2043703, 2043803, 2044003, 2044019, 2044103, 2044203, 2044303, 2044503, 2044603, 2044703, 2044815, 2044908); //GM卷
var prizeIdUse = Array(2000005,2000005,2000005,2000005,2000005,2000005,2000005,2000005,2000005,20000052000005,2000005,2000005,2000005); //超级药水
var prizeQtyUse = Array(80, 80, 80, 50, 50, 40, 100);//随即给超级数量
var prizeIdEquip = Array(1302059, 1312031, 1332052, 1332049, 1332050, 1382036, 1402036, 1412026, 1422028, 1432038, 1442045, 1452044, 1462039, 1472051, 1472052, 1482013, 1492013, 1342010); //110装备
var prizeIdEtc = Array(4002002, 4002002); // 木妖邮票
var prizeQtyEtc = Array(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 1, 2, 3, 4, 5, 6, 7);//随即给木妖数量

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1)
	status++;
    else
	status--;
    if (status == 0) {
	cm.sendSimple("#b#L0#领取奖励离开#l");
    } else if (status == 1) {
	if (selection == 0) {
    var itemSetSel = Math.random();
    var itemSet;
    var itemSetQty;
    var hasQty = false;
    var mgb = new Array(4031996,4031996,4031996,4031996,4031996,4031996,4031996,4031996,4031996,4031996,4031996,4031996,4031996,4031996,4031996,4031995,4031995,4031995,4031995,4031995,4031994);
    var randmgb = Math.floor(Math.random() * mgb.length);
    if (itemSetSel < 0.3) {
        itemSet = prizeIdScroll;
    } else if (itemSetSel < 0.6) {
        itemSet = prizeIdEquip;
    } else if (itemSetSel < 0.9) {
        itemSet = prizeIdUse;
        itemSetQty = prizeQtyUse;
        hasQty = true;
    } else {
        itemSet = prizeIdEtc;
        itemSetQty = prizeQtyEtc;
        hasQty = true;
    }
    var sel = Math.floor(Math.random() * itemSet.length);
    var qty = 1;
    if (hasQty) qty = itemSetQty[sel];
		if (!cm.canHold(4001455, 1)) {
			cm.sendOk("请把房间的怪物XX。");
			cm.dispose();
			return;
		}
	    cm.gainItem(itemSet[sel], qty);
	    cm.gainItem(mgb[randmgb],1);
	    cm.getChar().setXw(cm.getChar().getXw() + 5);
	    cm.removeAll(4001117);
	    cm.removeAll(4001120);
	    cm.removeAll(4001121);
	    cm.removeAll(4001122);
	    cm.gainNX(100);
	    cm.addTrait("will", 15);
	    cm.gainExp_PQ(120, 2.0);
	    cm.getPlayer().endPartyQuest(1204);
	    cm.warp(251010404,0);
	} else { //TODO JUMP
		if (cm.haveItem(cm.isGMS() ? 1003267 : 1002573, 1)) {
			cm.sendOk("You have the best hat.");
		} else if (cm.haveItem(1002573, 1)) {
		    if (cm.haveItem(4001455, 20)) {	
				if (cm.canHold(1003267,1)) {
					cm.gainItem(1002573, -1);
					cm.gainItem(4001455, -20);
		    	    cm.gainItem(1003267,1);
					cm.sendOk("我已经给了你的帽子。");
		    	} else {
					cm.sendOk("请把房间的怪物XX。");
		        } 
		    } else {
				cm.sendOk("你需要20个海盗能得到下一个帽子。");
		    }
		} else if (cm.haveItem(1002572, 1)) {
		    if (cm.haveItem(4001455, 20)) {	
				if (cm.canHold(1002573,1)) {
					cm.gainItem(1002572, -1);
					cm.gainItem(4001455, -20);
		    	    cm.gainItem(1002573,1);
					cm.sendOk("我已经给了你的帽子。");
		    	} else {
					cm.sendOk("请把房间的怪物XX。");
		        } 
		    } else {
				cm.sendOk("你需要20个海盗物品能得到下一个帽子。");
		    }
		} else {
		    if (cm.haveItem(4001455, 20)) {	
				if (cm.canHold(1002572,1)) {
					cm.gainItem(4001455, -20);
		    	    cm.gainItem(1002572,1);
					cm.sendOk("我已经给了你的帽子。");
		    	} else {
					cm.sendOk("请把房间的怪物XX。");
		        } 
		    } else {
				cm.sendOk("你需要20个海盗物品能得到下一个帽子。");
		    }
		}
	}
	cm.dispose();
    }
}