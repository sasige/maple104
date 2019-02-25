function enter(pi) {
	var returnMap = pi..getSavedLocation("WORLDTOUR");
	if (returnMap < 0) {
		returnMap = 910000000;
	}
	var target = pi.getPlayer().getClient().getChannelServer().getMapFactory().getMap(returnMap);
	var portal = target.getPortal("st00");
	if (portal == null) {
		portal = target.getPortal(0);
	}
	pi.clearSavedLocation("WORLDTOUR");
	pi.getPlayer().changeMap(target, portal);
	return true;
}