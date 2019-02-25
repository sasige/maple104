//笔芯制作
var status = -1;

function start(mode, type, selection) {//接任务
	if (mode == -1) {
		qm.dispose();
	} else {
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
			qm.sendNext("让我看看你的人气度。。。。");
		} else if (status == 1) {
			qm.sendNextPrev("嗯。。只要你的人气度达到10000点，我就可以给你这个勋章。");
		} else if (status == 2) {
			qm.forceStartQuest();
			qm.dispose();
		} else if (status == 3){
			qm.dispose();
		}
	}
}
function end(mode, type, selection) {//完成任务时候的判读啊
if (mode == -1) {
		qm.dispose();
	} else {
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
			qm.sendNext("让我看看你的人气度。。。。");
		} else if (status == 1) {
			if(qm.getfame() == 10000){
			qm.sendNext("嗯。。真不敢相信你的人气度居然达到了最高值！")
			}else{
			qm.sendNext("嗯。。好像你的人气度还没有达到我想要的呀。。。。")
			qm.dispose();
			}
		} else if (status == 2) {
			qm.sendNext("好!遵守约定，我给你一样东西。。。。")
		} else if (status == 3){
			qm.dispose();
		}
	}
}
