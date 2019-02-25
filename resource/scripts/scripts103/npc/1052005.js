/* Dr. Feeble
	Henesys Random Eye Change.
*/
var status = 0;
var beauty = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 0 && status == 0) {
	cm.dispose();
	return;
    }
    if (mode == 1)
	status++;
    else
	status--;
    if (status == 0) {
	cm.sendNext("你好，如果你有#b#t5152056##k，你就放心的让我为你进行整形手术吧,我会让你满意的.那么你要做什么？");
    } else if (status == 1) {
	cm.sendYesNo("真的要使用 #b#t5152056##k吗？");
    } else if (status == 2){
	var face = cm.getPlayerStat("FACE");
	var facetype;

	if (cm.getPlayerStat("GENDER") == 0) {
	    facetype = [20000,20001,20002,20003,20004,20005,20006,20007,20008,20009,20010,20011,20012,20013,20014,20015,20016,20017,20018,20019,20020,20021,20022,20023,20025,20026,20027,20028];
	} else {
	    facetype = [21000,21001,21002,21003,21004,21005,21006,21007,21008,21009,21010,21011,21012,21013,21014,21015,21016,21017,21018,21019,21020,21021,21022,21023,21025,21026];
	}
	for (var i = 0; i < facetype.length; i++) {
	    facetype[i] = facetype[i] + face % 1000 - (face % 100);
	}

	if (cm.setRandomAvatar(5152056, facetype) == 1) {
	    cm.sendOk("#e好了,你的朋友们一定认不出你了!");
	} else {
	    cm.sendOk("看起来你并没有我们的会员卡,我恐怕不能给你理发,我很抱歉.请你先购买吧.");
	}
	cm.dispose();
    }
}
