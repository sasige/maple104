//战士精炼
function enter(pi) {
		var eim = pi.getPlayer().getEventInstance();
		if ((pi.getJob() >= 500 && pi.getJob() <=522) || (pi.getJob() >= 1500 && pi.getJob() <=1512) || (pi.getJob() >= 3500 && pi.getJob() <=3512)){
			pi.warp(803001150,"sp");
		}else{
			pi.playerMessage("只有海盗职业才能进入。")
		}
}