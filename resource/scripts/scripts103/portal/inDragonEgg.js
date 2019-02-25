function enter(pi) {
	if(pi.isQuestActive(22005)){
		pi.warp(900020100);
	} else{
		pi.playerMessage("Cannot enter the Lush Forest without a reason.");
		return false;
        }
	return true;
}  