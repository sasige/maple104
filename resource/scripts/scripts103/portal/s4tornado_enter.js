function enter(pi) {
    if (pi.getJob() == 412) {
	if ((pi.haveItem(4001110) && pi.getQuestStatus(6230)) == 0 || pi.getQuestStatus(6230) == 1 || (pi.getQuestStatus(6230) == 2 && pi.getQuestStatus(6231) == 0)) {
	    
	    pi.warp(922020200, 0);
	}
    }
}