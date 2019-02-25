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
			qm.sendNext("第一次来到新叶城吗？？？");
		} else if (status == 1) {
			qm.sendNextPrev("嗯。。我准备了一些问题，你能把他一一回答出来吗？");
		} else if (status == 2) {
			qm.sendNextPrev("好吧，准备好。。。错了没关系，还可以重新来。");
		} else if (status == 3){
			qm.forceStartQuest();
		}
	}
}
