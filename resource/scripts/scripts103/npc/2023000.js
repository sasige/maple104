var fromMap = new Array(211000000,220000000,240000000);
var toMap = new Array(211040200,220050300,240030000);
var cost = new Array(45000,25000,55000);
var location;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if(mode == -1) {
		cm.dispose();
	}
	if(mode == 0) {
		cm.sendNext("嗯...想想吧。这是值得出租车的服务！你将永远不会后悔！");
	}
	if(mode == 1) {
		status++;
	}
	if(status == 0) {
		switch(cm.getChar().getMapId()) {
			case fromMap[0]:
				location = 0;
				break;
			case fromMap[1]:
				location = 1;
				break;
			case fromMap[2]:
				location = 2;
				break;
		}
		cm.sendNext("你好！这危险出租车将带你到危险区#m"+cm.getChar().getMapId()+"#的#b#m"+toMap[location]+"##k但是你需要交一些手续费 #b"+cost+" 金币#k 看似昂贵，但是不做我的车，你休想轻松的到达危险区！");
	} else if(status == 1) {
		cm.sendOk("#b你确定要让我送你去#b#m"+toMap[location]+"#吗？");
	} else if(status == 2) {
		if(cm.getMeso() < cost) {
			cm.sendNext("You don't seem to have enough mesos. I am terribly sorry, but I cannot help you unless you pay up. Bring in the mesos by hunting more and come back when you have enough.");
			cm.dispose();
		} else{
			cm.warp(toMap[location]);
			cm.gainMeso(-cost[location]);
			cm.dispose();
		}
	}
}
