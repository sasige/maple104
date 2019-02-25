function enter(pi) {
    if (pi.getMap().getAllMonstersThreadsafe().size() == 0) {
	pi.givePartyItems(4310020, 1);
	pi.warpParty(951000000,0); //next
    } else {
	pi.playerMessage(5, "门还没有打开,请清理掉最后阶段的怪物.");
    }
}