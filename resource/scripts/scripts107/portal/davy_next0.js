function enter(pi) {
    if (pi.getMap().getAllMonstersThreadsafe().size() == 0) {
        pi.warp(925100100, 0); //next
    } else {
        pi.playerMessage(5, "当前地图还存在怪物,请把此地图怪清理掉..");
    }
}