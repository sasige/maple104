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
	qm.sendYesNo("您已经决定了吗？的决定将是最终的，所以请仔细才决定要做什么。你确定你想成为一个幻灵斗师?");
    } else if (status == 1) {
	qm.sendNext("我刚刚成型的身体使它完美的幻灵斗师。如果你想变得更强大，使用属性窗口（S）适当提高统计。如果你不确定什么提高，只需点击 #bAuto#k.");
	if (qm.getJob() == 3000) {
	    qm.gainItem(1382100,1);
	    qm.expandInventory(1, 4);
	    qm.expandInventory(2, 4);
	    qm.expandInventory(4, 4);
	    qm.changeJob(3200);
	}
	qm.forceCompleteQuest();
    } else if (status == 2) {
	qm.sendNextPrev("我也扩大了你的库存槽数为你的设备等。库存。明智地使用这些插槽和装满电阻进行所需物品。");
    } else if (status == 3) {
	qm.sendNextPrev("现在…我要你去世界展示如何抵抗操作.");
	qm.safeDispose();
    }
}