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
				cm.sendNext("这样都被你找到了...看来我的空间魔法还需加强...哎  这里是我的一些武器..你要么？.#v1372032# #v1382036# .如果你想要的话..请拿来#b 智力水晶20个#k#v4005001#, #b #k#v1382007#, 以及 #b500w冒险B. .....");
			}
		else if (status == 1) {
			if ((cm.haveItem(4005001, 20)) && (cm.haveItem(1382007)) && (cm.getMeso() >= 5000000)) {
			cm.sendYesNo("不错喔，看来你已经收集到所需要的所有材料..现在就给你么?");
			}
			else if (!cm.haveItem(4005001, 20)) {
			cm.sendOk("你所收集的 水晶20个#k#v4005001#没有达到我所要求的数量.");
			cm.dispose();
			}
			else if (!cm.haveItem(1382007)) {
			cm.sendOk("你还没有#k#v1382007#");
			cm.dispose();
			}
			else if (!cm.getMeso() <= 5000000) {
			cm.sendOk("很抱歉，你身上的金币不够支付本次制作卷轴的费用.");
			cm.dispose();
			}
			
		}
		else if (status == 2) {
			cm.gainItem(4005001, -20);
			cm.gainItem(1382007, -1);
			cm.gainMeso(-5000000);
			cm.gainItem(1372032, 1);
			cm.gainItem(1382036, 1);			
}
}
}