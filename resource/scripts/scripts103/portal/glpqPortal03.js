//战士精炼
function enter(pi) {
		var eim = pi.getPlayer().getEventInstance();
		if ((pi.getJob() >= 400 && pi.getJob() <=434) || (pi.getJob() >= 1400 && pi.getJob() <=1412)){
			pi.warp(803001130,"sp");
		}else{
			pi.playerMessage("只有飞侠职业才能进入。")
		}
}