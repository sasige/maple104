function enter(pi) {
    if (pi.getMap().getReactorByName("sMob1").getState() >= 1 && pi.getMap().getReactorByName("sMob2").getState() >= 1 && pi.getMap().getReactorByName("sMob3").getState() >= 1 && pi.getMap().getReactorByName("sMob4").getState() >= 1) {
        if (pi.isLeader()) {
            pi.warpParty(925100500); //next
        } else {
            pi.playerMessage(5, "队长必须在这");
        }
    } else {
        pi.playerMessage(5, "门还没有打开,请把此地图的门都关上");
    }
}