function enter(pi) {
	if(pi.isQuestActive(22010)){
		pi.warp(100030310);
	} else {
		pi.playerMessage("如果没有什么事的话是不可以离开农场的。");
		return false;
	}
	return true;
}