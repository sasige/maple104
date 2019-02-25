function enter(pi) {
	if (pi.getQuestStatus(31001) == 2){//如果“这里就是克里塞？”任务已经完成
	
	pi.warp(200100010)
	}else{
	pi.playerMessage("外部人员无法进入。");	
	}
}