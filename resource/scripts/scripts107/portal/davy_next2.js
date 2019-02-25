function enter(pi) {
    if (pi.getMap().getAllMonstersThreadsafe().size() == 0) {
        pi.warp(925100300, 0); //next
    } else {
        pi.playerMessage(5, "门还是关闭,请清理掉地图的怪");
    }
}