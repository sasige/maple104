function enter(pi) {
	if (pi.getEvanIntroState("mo30=o")) {
		pi.blockPortal();
		return false;
	}
	pi.updateEvanIntroState("mo30=o");
	pi.blockPortal();
	pi.showWZEffect("Effect/OnUserEff.img/guideEffect/evanTutorial/evanBalloon30", 1);
	return true;
}