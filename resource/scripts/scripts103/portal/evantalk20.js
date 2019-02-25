function enter(pi) {
	pi.unlockUI();
	if (pi.getEvanIntroState("dt00=o;dt01=o;mo00=o;mo01=o;mo10=o;mo02=o;mo11=o;mo20=o")) {
		pi.blockPortal();
		return false;
	}
	pi.updateEvanIntroState("dt00=o;dt01=o;mo00=o;mo01=o;mo10=o;mo02=o;mo11=o;mo20=o");
	pi.blockPortal();
	pi.showWZEffect("Effect/OnUserEff.img/guideEffect/evanTutorial/evanBalloon20", 1);
	return true;
}