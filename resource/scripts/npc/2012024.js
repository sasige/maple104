/*
	Egnet - Before Takeoff To Ariant(200000152)
*/

var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	cm.sendOk("旅途还很漫长...");
	cm.safeDispose();
	return;
    }
    if (status == 0) {
	cm.sendYesNo("你确定要离开这里吗？");
    } else if (status == 1) {
	cm.warp(200000151);
	cm.dispose();
    }
}