function enter(pi) {
	var eim = pi.getPlayer().getEventInstance();
	var pro;
	if(pi.getMapId() == 910340100){
		pro = "100stageclear";
	}else if(pi.getMapId() == 910340200){
		pro = "200stageclear";
	}else if(pi.getMapId() == 910340300){
		pro = "300stageclear";
	}else if(pi.getMapId() == 910340400){
		pro = "400stageclear";
	}else if(pi.getMapId() == 910340500){
		pro = "500stageclear";
	}	
	if (eim.getProperty(pro) != null) {
		if (pi.getMapId() == 910340300 || pi.getMapId() == 910340400){
		pi.warp(pi.getMapId()+100, "st00");
		pi.openNpc(9900000);
		}else{
			pi.warp(pi.getMapId()+100, "st00");
		}
	} else {
		pi.playerMessage("现在还不能进入。");
		return false;	
	}
}