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
			qm.sendOk("重新上线或更换频道来消除该任务。");
		} else if (status == 1) {
			qm.forceCompleteQuest();
			qm.dispose();
	}
}
}
