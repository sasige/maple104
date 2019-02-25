function enter(pi) {
	if (pi.getPlayer().getMapId()==200000300) {
		pi.warp(200000301);
		return true;
	} else if (pi.getPlayer().getMapId()==200000301) {
		pi.warp(200000300);
		return true;
	}
}