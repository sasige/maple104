function enter(pi) {
	if (pi.getEvanIntroState("dt00=o;mo00=o;mo01=o;mo10=o;mo02=o")) {
		pi.blockPortal();
		return false;
	}
	pi.updateEvanIntroState("dt00=o;mo00=o;mo01=o;mo10=o;mo02=o");
	pi.blockPortal();
	pi.showWZEffect("Effect/OnUserEff.img/guideEffect/evanTutorial/evanBalloon10", 1);
	return true;
}