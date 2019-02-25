function enter(pi) {
	if(pi.isQuestActive(22008)){
		pi.warp(100030103);
	} else {
		pi.playerMessage("如果没什么事的话是不可以进入后院的。");
		return false;
        } 
	return true;
}  