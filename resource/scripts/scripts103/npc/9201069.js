var status = 0;
var beauty = 0;
var price = 3000000;
var mface = Array(20000,20001,20002,20003,20004,20005,20006,20007,20008,20009,20010,20011,20012,20013,20014,20015,20016,20017,20018,20019,20020,20021,20022,20023,20025,20026,20027,20028);
var fface = Array(21000,21001,21002,21003,21004,21005,21006,21007,21008,21009,21010,21011,21012,21013,21014,21015,21016,21017,21018,21019,21020,21021,21022,21023,21025,21026);
var facenew = Array();
var card = 5152033;//会员卡的ID。

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
			cm.sendSimple("你好，如果你有#b#t"+card+"##k，你就放心的让我为你进行整形手术吧,我会让你满意的.那么你要做什么？\r\n\#L2##b进行整形手术#k(使用#b#t"+card+"##k)随机#l");
			} else if (status == 1) {
			if (selection == 1) {
				cm.dispose();
			} else if (selection == 2) {
				facenew = Array();
				if (cm.getChar().getGender() == 0) {
					for(var i = 0; i < mface.length; i++) {
						facenew.push(mface[i] + cm.getChar().getFace() % 1000 - (cm.getChar().getFace() % 100));
					}
				}
				if (cm.getChar().getGender() == 1) {
					for(var i = 0; i < fface.length; i++) {
						facenew.push(fface[i] + cm.getChar().getFace() % 1000 - (cm.getChar().getFace() % 100));
					}
				}
				cm.sendYesNo("如果你有#b#t"+card+"##k,那么我将帮你随机改变一种脸型,你确定要改变脸型吗？");
			}
		} else if (status == 2){
			cm.dispose();
			if (cm.haveItem(card) == true){
				cm.gainItem(card, -1);
				cm.setFace(facenew[Math.floor(Math.random() * facenew.length)]);
				cm.sendOk("#e好了,你的朋友们一定认不出你了!");
			} else {
				cm.sendOk("看起来你并没有我们的会员卡,我恐怕不能给你理发,我很抱歉.请你先购买吧.");
			}
		}
	}
}
