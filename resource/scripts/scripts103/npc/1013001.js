var status = 0;

function start() {
status = -1;
action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0)
			cm.sendNexts("你终于来了。拥有契约资格的人……");
		if (status == 1)
			cm.sendNextPrevs("执行契约吧……");
		if (status == 2) {
			cm.lockUI();
			cm.warp(900090101);
			cm.dispose();
		}
	}
}