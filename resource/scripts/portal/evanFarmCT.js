function enter(pi) {
    if (pi.isQuestActive(22010) || pi.getPlayer().getJob() != 2001) {
        pi.warp(100030310);
    } else {
        pi.playerMessage("外面的世界太危险，还是先在这里锻炼更加强大点在出去好了.");
    }
    return true;
}