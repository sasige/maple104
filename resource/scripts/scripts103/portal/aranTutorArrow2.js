function enter(pi) {
	if (pi.getAranIntroState("arr2=o")) {
		pi.blockPortal();
		return false;
	}
	pi.updateAranIntroState("normal=o;arr0=o;arr1=o;arr2=o;mo1=o;chain=o;mo2=o;mo3=o;mo4=o");
	pi.blockPortal();
	pi.showWZEffect("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow1", 1);
	return true;
}