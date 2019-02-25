//战士精炼
function enter(pi) {
		var eim = pi.getPlayer().getEventInstance();
		if ((pi.getJob() >= 100 && pi.getJob() <=132) || (pi.getJob() >= 1100 && pi.getJob() <=1112) || (pi.getJob() >= 2000 && pi.getJob() <=2112)){
			pi.warp(803001110,"sp");
		}else{
			pi.playerMessage("只有战士职业才能进入。")
		}
}