/* Denma the Owner
	Henesys VIP Eye Change.
*/
var status = -1;
var facetype;
var card = 5152057;

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 0) {
	cm.dispose();
	return;
    } else {
	status++;
    }

    if (status == 0) {
	cm.sendNext("嘿~！你好!如果你有#b#t5152057##k，我可以为你进行整形手术。");
    } else if (status == 1) {
	var face = cm.getPlayerStat("FACE");

	if (cm.getPlayerStat("GENDER") == 0) {
	    facetype = [20000,20001,20002,20003,20004,20005,20006,20007,20008,20009,20010,20011,20012,20013,20014,20015,20016,20017,20018,20019,20020,20021,20022,20023,20025,20026,20027,20028];
	} else {
	    facetype = [21000,21001,21002,21003,21004,21005,21006,21007,21008,21009,21010,21011,21012,21013,21014,21015,21016,21017,21018,21019,21020,21021,21022,21023,21025,21026];
	}
	for (var i = 0; i < facetype.length; i++) {
	    facetype[i] = facetype[i] + face % 1000 - (face % 100);
	}
	cm.sendStyle("我可以改变你的脸型,让它比现在看起来漂亮. 你为什么不试着改变它下? 如果你有#b#t5152057##k,我将会帮你改变你的脸型,那么选择一个你想要的新脸型吧!", facetype,5152057);
    } else if (status == 2){
	if (cm.setAvatar(5152057, facetype[selection]) == 1) {
	    cm.sendOk("好了,你的朋友们一定认不出来是你了!");
	} else {
	    cm.sendOk("看起来你并没有我们的高级会员卡,我恐怕不能给你进行整形手术,我很抱歉.请你先购买吧.");
	}
	cm.dispose();
    }
}
