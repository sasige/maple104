var status = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
	if (status >= 0 && mode == 0) {
		cm.sendNext("还有别的事在这里没办完吗？");
		cm.dispose();
		return;
	}
	if (mode == 1)
		status++;
	else
		status--;
	if (status == 0) {
		cm.sendYesNo("您好我是封印扎昆的#b看守者#k如果想挑战扎昆就进去吧\r\n#r进阶扎昆#k3线召唤");
	} else if (status == 1) {
		cm.sendNext("向前进.3线是进阶扎昆。");
		cm.dispose();
		}
	}
}