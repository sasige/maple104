function enter(pi) {
    if (pi.getMap().getAllMonstersThreadsafe().size() == 0) {
        pi.warp(925100400, 0); //next
    } else {
        pi.playerMessage(5, "门是关闭的,请清理掉地图的怪");
    }
}