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
				cm.sendNext("基德 ！又是基德！留下的武器！.#v1472052# #v1332052# .如果你想要的话..请拿来#b 运气水晶20个#k#v4005003#, #b #k#v1472031#, 以及 #b500万冒险B. .....");
			}
		else if (status == 1) {
			if ((cm.haveItem(4005003, 20)) && (cm.haveItem(1472031)) && (cm.getMeso() >= 5000000)) {
			cm.sendYesNo("不错喔，看来你已经收集到所需要的所有材料..现在就给你么?");
			}
			else if (!cm.haveItem(4005003, 20)) {
			cm.sendOk("你所收集的 水晶20个#k#v4005003#没有达到我所要求的数量.");
			cm.dispose();
			}
			else if (!cm.haveItem(1402035)) {
			cm.sendOk("你还没有#k#v1472031#");
			cm.dispose();
			}
			else if (!cm.getMeso() <= 5000000) {
			cm.sendOk("很抱歉，你身上的金币不够支付本次制作卷轴的费用.");
			cm.dispose();
			}
			
		}
		else if (status == 2) {
			cm.gainItem(4005003, -20);
			cm.gainItem(1472031, -1);
			cm.gainMeso(-5000000);
			cm.gainItem(1472052, 1);
			cm.gainItem(1332052, 1);			
}
}
}