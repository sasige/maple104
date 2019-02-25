var status = 0;
var beauty = 0;
var hairprice = 3000000;
var haircolorprice = 3000000;
var mhair = Array(30020,30150,30240,30280,30160,30190,30050,30300,30320,30340,30510,30650,30730,30760,30680,30700,30000);
var fhair = Array(31000,31030,31040,31010,31150,31120,31160,31230,31270,31280,31290,31460,31520,31650,31720,31740,31700,31670);
var hairnew = Array();

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
			cm.sendSimple("你好,我是美发店的助手!如果你有#b玩具城美发店普通会员卡#k或#b玩具城染色普通会员卡#k,你就放心的把发型交给我,我会让你满意的.那么你要做什么?请选择吧!\r\n#L1#改变发型(使用#b玩具城美发普通会员卡#k)#l\r\n#L2#改变发色(使用#b玩具城染发普通会员卡#k)#l");								
			} else if (status == 1) {
			if (selection == 0) {
				beauty = 0;
				cm.sendSimple("那么你想购买哪种会员卡呢?\r\n#L0##b玩具城美发店普通会员卡#k,需要#r" + hairprice + "#k金币#l\r\n#L1##b玩具城染发普通会员卡#k,需要#r" + haircolorprice + "#k金币#l");
			} else if (selection == 1) {
				beauty = 1;
				hairnew = Array();
				if (cm.getChar().getGender() == 0) {
					for(var i = 0; i < mhair.length; i++) {
						hairnew.push(mhair[i] + parseInt(cm.getChar().getHair() % 10));
					}
				} 
				if (cm.getChar().getGender() == 1) {
					for(var i = 0; i < fhair.length; i++) {
						hairnew.push(fhair[i] + parseInt(cm.getChar().getHair() % 10));
					}
				}
				cm.sendYesNo("如果你有#b玩具城美发店普通会员卡#k,那么我将帮你随机改变一种发型,你确定要改变发型吗?");
			} else if (selection == 2) {
				beauty = 2;
				haircolor = Array();
				var current = parseInt(cm.getChar().getHair()/10)*10;
				for(var i = 0; i < 8; i++) {
					haircolor.push(current + i);
				}
				cm.sendYesNo("如果你有#b玩具城染发普通会员卡#k,那么我将帮你随机改变一种发色,你确定要改变发色吗?");
			}
		}
		else if (status == 2){
			cm.dispose();
			if (beauty == 1){
				if (cm.haveItem(5150006)){
					cm.gainItem(5150006, -1);
					cm.setHair(hairnew[Math.floor(Math.random() * hairnew.length)]);
					cm.sendOk("好了,让朋友们赞叹你的新发型吧!");
				} else {
					cm.sendOk("看起来你并没有我们的会员卡,我恐怕不能给你理发,我很抱歉.请你先购买吧.");
				}
			}
			if (beauty == 2){
				if (cm.haveItem(5151006) == true){
					cm.gainItem(5151006, -1);
					cm.setHair(haircolor[Math.floor(Math.random() * haircolor.length)]);
					cm.sendOk("好了,让朋友们赞叹你的新发色吧!");
				} else {
					cm.sendOk("看起来你并没有我们的会员卡,我恐怕不能给你染发,我很抱歉.请你先购买吧.");
				}
}
	
}
		}
		}
	