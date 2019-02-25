function enter(pi) {

if (pi.getQuestStatus(31102) >= 1) {
	pi.warp(271000000)
}else{
	pi.playerMessage("由于未知的力量，你不能进入。");
}//判断任务
}