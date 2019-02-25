function enter(pi) {
	var eim = pi.getPlayer().getEventInstance();
		if (eim.getProperty("pass2") == null){
			pi.playerMessage("要想继续前进，必须启动所有的祭坛。");
		}else{
		pi.warp(803000900,"sp")
		}
}