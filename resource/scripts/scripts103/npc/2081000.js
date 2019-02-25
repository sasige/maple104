var section;
var temp;
var cost;
var count;
var menu = "";
var itemID = new Array(4000226,4000229,4000236,4000237,4000261,4000231,4000238,4000239,4000241,4000242,4000234,4000232,4000233,4000235,4000243);

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if(mode == -1) {
		cm.dispose();
		return;
	}
	if(mode == 0 && (status == 0 || status == 1 || status == 2)) {
		cm.dispose();
		return;
	} else if(mode == 0) {
		if(section == 0) {
			cm.sendOk("请检查你的背包里是否有这些东西，或者你的金币是否够用，再或者你的背包空位是否够用？");
		} else {
			cm.sendOk("Think about it, and then let me know of your decision.");
		}
		cm.dispose();
		return;
	}
	if(mode == 1) {
		status++;
	}
	if(status == 0) {
		cm.sendSimple("你有什么话要对我说吗？\r\n#L0##b购买魔法种子#k#l\r\n#L1##b为了神木村#k#l");
	} else if(status == 1) {
		section = selection;
		if(section == 0) {
			cm.sendSimple("不像是我们村子的人，找我什么事?\r\n\r\n#L0##b想得到#t4031346#。#k#l");
		} else {
			cm.sendNext("更好的建设村落是村长的职责。所以需要更多更好的道具。你能为了村落捐献出在神木村附近收集到的道具吗？");
		}
	} else if(status == 2) {
		if(section == 0) {
			cm.sendGetNumber("#b#t4031346##k是贵重物品，不能白送给你。付出一定的代价可以吗?一个#b#t4031346##k#b30,000 金币#k\r\n买吗?要几个?",0,0,99);
		} else {
			for(var i=0; i < itemID.length; i++) {
				menu += "\r\n#L"+i+"##b#t"+itemID[i]+"##k#l";
			}
			cm.sendNext("你想捐献出那种道具呢?");
			cm.dispose();
		}
	} else if(status == 3) {
		if(section == 0) {
			if(selection == 0) {
				cm.sendOk("你必须填上您要购买的数量.");
				cm.dispose();
			} else {
				temp = selection;
				cost = temp * 30000;
				cm.sendYesNo("购买#b #t4031346#"+temp+"个#k 需要#b"+cost+"金币#k.购买吗?");
			}
		} else {
			temp = selection;
			if(!cm.haveItem(itemID[temp])) {
				cm.sendNext("你不认为你拥有该物品？");
				cm.dispose();
			} else {
				cm.sendGetNumber("How many #b#t"+itemID[temp]+"#k's would you like to donate?\r\n#b< Owned : #c"+itemID[temp]+"# >#k",0,0,"#c"+itemID[temp]+"#");
			}
		}
	} else if(status == 4) {
		if(section == 0) {
			if(cm.getMeso() < cost || !cm.canHold(4031346)) {
				cm.sendOk("请检查你的背包里是否有这些东西，或者你的金币是否够用，再或者你的背包空位是否够用？");
			} else {
				cm.sendOk("欢迎再来。");
				cm.gainItem(4031346, temp);
				cm.gainMeso(-cost);
			}
			cm.dispose();
		} else {
			count = selection;
			cm.sendYesNo("Are you sure you want to donate #b"+count+" #t"+itemID[temp]+"##k?");
		}
	} else if(status == 5) {
		if(count == 0 || !cm.haveItem(itemID[temp],count)) {
			cm.sendNext("您并未拥有该物品。");
		} else {
			cm.gainItem(itemID[temp],-count);
			cm.sendNext("非常感谢你。");
		}
		cm.dispose();
	}
}
