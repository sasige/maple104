function enter(pi) {
	var eim = pi.getPlayer().getEventInstance();
		if (eim.getProperty("pass5") == null){
			pi.playerMessage("黑暗魔法师的力量越来越强了，赶紧把奇迹的武器献给胜利雕像！");
		}else{
		pi.warp(803001200,"sp")
		}
}