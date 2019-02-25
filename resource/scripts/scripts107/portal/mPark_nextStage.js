function enter(pi) {
    if (pi.getMap().getAllMonstersThreadsafe().size() == 0) {
	pi.warpParty(pi.getMapId() + 100,0); //next
    } else {
	pi.playerMessage(5, "门还没有打开,请清理掉此阶段的怪物.");
    }
}