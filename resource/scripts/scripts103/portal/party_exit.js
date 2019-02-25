function enter(pi) {
    var returnMap = pi.getSavedLocation("TURNEGG");
    pi.clearSavedLocation("TURNEGG");

    if (returnMap < 0) {
	returnMap = 103000000;
    }
    var target = pi.getMap(returnMap);
    var portal = target.getPortal(0);

    if (pi.getMapId() != target) {
	pi.getPlayer().changeMap(target, portal);
    }
}