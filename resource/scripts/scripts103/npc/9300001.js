var a = 0;

function start() {
	a = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (mode == 1)
			a++;
		else
			a--;
			if (a == -1){
				cm.sendNext("咦…你现在不想去吗？\r那好吧，欢迎以后再来吧。")
				cm.dispose();
			}else
				if (a == 0) {
					cm.sendYesNo("哦...你想去结婚的红鸾殿吗？\r这宫殿位于天上，去的话要不少钱哦。")
				}else if (a == 1){
					cm.sendNextPrev("嗯…你想去红鸾宫吗？\r好先付#e2000金币#n，我就送你去。给你优惠价啦。")
				}else if (a == 2){
					cm.sendNextPrev("好！我收下#e2000金币#n！\r那我这就送你到#b天上#k~！")
				}else if (a == 3){
					if (cm.getMeso() < 2000 ){
						cm.sendOk("你的金币不够支付此费用。")
						cm.dispose();
					}else{
						cm.warp(700000000);
						cm.gainMeso(-2000)
						cm.dispose();
					}
	}//status
}
}