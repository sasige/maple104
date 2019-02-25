var status = -1;

function start(mode, type, selection) {
	if (mode == -1) {
		qm.dispose();
	} else {
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
			qm.sendOk("  任务暂时完成处理！");
			qm.completeQuest();
			qm.dispose();
		}
	}
}