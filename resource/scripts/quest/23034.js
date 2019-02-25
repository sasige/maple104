var status = -1;

function end(mode, type, selection) {
    if (mode == 0) {
	if (status == 0) {
	    qm.sendNext("这是一个很重要的决定.");
	    qm.safeDispose();
	    return;
	}
	status--;
    } else {
	status++;
    }
    if (status == 0) {
	qm.sendYesNo("您已经决定了吗？的决定将是最终的，所以请仔细才决定要做什么...");
    } else if (status == 1) {
	qm.sendNext("我刚刚成型的身体使它完美的豹弩游侠。如果你想变得更强大，使用属性窗口（S）适当提高统计。如果你不确定什么提高 #bAuto#k.");
	if (qm.getJob() == 3310) {
	    qm.changeJob(3311);
	}
	qm.forceCompleteQuest();
    } else if (status == 2) {
	qm.sendNextPrev("现在…我要你去世界展示如何抵抗操作.");
	qm.safeDispose();
    }
}