function enter(pi) {
	if (pi.getEvanIntroState("mo00=o")) {
		pi.blockPortal();
		return false;
	}
	pi.updateEvanIntroState("mo00=o");
	pi.blockPortal();
        pi.showWZEffect("Effect/OnUserEff.img/guideEffect/evanTutorial/evanBalloon00", 1);
	return true;
}