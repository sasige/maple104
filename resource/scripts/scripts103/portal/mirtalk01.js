function enter(pi) {
	if (pi.getEvanIntroState("dt00=o;dt01=o;mo00=o;mo01=o;mo10=o;mo02=o")) {
		pi.blockPortal();
		return false;
	}
	pi.updateEvanIntroState("dt00=o;dt01=o;mo00=o;mo01=o;mo10=o;mo02=o");
	pi.blockPortal();
	pi.showMapEffect("evan/dragonTalk01");
	return true;
}