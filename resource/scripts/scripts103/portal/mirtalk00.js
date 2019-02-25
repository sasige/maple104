function enter(pi) {
	if (pi.getEvanIntroState("dt00=o;mo00=o")) {
		pi.blockPortal();
		return false;
	}
	pi.updateEvanIntroState("dt00=o;mo00=o");
	pi.blockPortal();
	pi.showMapEffect("evan/dragonTalk00");
	return true;
}