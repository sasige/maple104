function enter(pi) {
	var eim = pi.getPlayer().getEventInstance();
		if (eim.getProperty("pass4") != null){//如果已经打掉了所有的祭台。
			pi.playerMessage("黑暗魔法师的力量越来越接近了，赶快启动所有的祭坛。逃离这里。")
		}else{
			pi.warp(803001100,"sp")	
		}
}