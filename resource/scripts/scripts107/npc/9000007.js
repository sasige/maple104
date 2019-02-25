// White Scroll Maker By Sawyer of LynnStory
//Nana(H) (9201001)
importPackage(net.sf.odinms.client);
var status = 0;



function start() {
	status = -1;
	action(1, 0, 0);
}


function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (status >= 2 && mode == 0) {
			cm.sendOk("Rawr!?");
			cm.dispose();
			return;
		}
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
				cm.sendNext("战士们正在休息....小声点！战士的武器在一旁闪着光芒..#v1402016# #v1402036# .如果你想要的话..请拿来#b 力量水晶20个#k#v4005000#, #b #k#v1402035#, 以及 #b500w冒险B. .....");
			}
		else if (status == 1) {
			if ((cm.haveItem(4005000, 20)) && (cm.haveItem(1402035)) && (cm.getMeso() >= 5000000)) {
			cm.sendYesNo("不错喔，看来你已经收集到所需要的所有材料..现在就给你么?");
			}
			else if (!cm.haveItem(4005000, 20)) {
			cm.sendOk("你所收集的 力量水晶20个#k#v4005000#没有达到我所要求的数量.");
			cm.dispose();
			}
			else if (!cm.haveItem(1402035)) {
			cm.sendOk("你还没有#k#v1402035#");
			cm.dispose();
			}
			else if (!cm.getMeso() <= 5000000) {
			cm.sendOk("很抱歉，你身上的金币不够支付本次制作卷轴的费用.");
			cm.dispose();
			}
			
		}
		else if (status == 2) {
			cm.gainItem(4005000, -20);
			cm.gainItem(1402035, -1);
			cm.gainMeso(-5000000);
			cm.gainItem(1402016, 1);
			cm.gainItem(1402036, 1);			
}
}
}