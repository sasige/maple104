function enter(pi) {
	var nextMap;
	var avail;
	var eim = pi.getPlayer().getEventInstance();
	if (eim == null){
		pi.warp(922010000)
		pi.playerMessage("异常方式进入，现在已经传送到外面。");
	}else{
	if (pi.getMapId() == 922010100){
	nextMap = 922010200;
	avail = eim.getProperty("stage1status");
	}else if (pi.getMapId() == 922010200){
	nextMap = 922010300;
	avail = eim.getProperty("stage2status");
	}else if (pi.getMapId() == 922010300){
	nextMap = 922010400;
	avail = eim.getProperty("stage3status");
	}else if (pi.getMapId() == 922010400){
	nextMap = 922010500;
	avail = eim.getProperty("stage4status");
	}else if (pi.getMapId() == 922010500){
	nextMap = 922010600;
	avail = eim.getProperty("stage5status");
	}else if (pi.getMapId() == 922010600){
	nextMap = 922010700;
	avail = eim.getProperty("stage5status");
	
	}else if (pi.getMapId() == 922010700){
	nextMap = 922010800;
	avail = eim.getProperty("stage7status");
	}else if (pi.getMapId() == 922010800){
	nextMap = 922010900;
	avail = eim.getProperty("8stageclear");	
	}
	
	if (avail == null) {
		pi.playerMessage("现在不能进入里面。");
		return false;
	} else {
		if (pi.getMapId() == 922010700){
			if(eim.getProperty("s8start") == null){
			eim.setProperty("s8start", new java.util.Date().getTime());
			}
		}
		if (pi.getMapId() == 922010800){
		if(eim.getProperty("s9start") == null)
			eim.setProperty("s9start", new java.util.Date().getTime());	
		}
		pi.warp(nextMap,"st00")
		return true;
	}
	}
}