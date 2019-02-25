//战士精炼
function enter(pi) {
		var eim = pi.getPlayer().getEventInstance();
		if ((pi.getJob() >= 200 && pi.getJob() <=232) || (pi.getJob() >= 1200 && pi.getJob() <=1212) || (pi.getJob() >= 3200 && pi.getJob() <=3212) ||  (pi.getJob() >= 2001 && pi.getJob() <=2218)){
			pi.warp(803001121,"sp");
		}else{
			pi.playerMessage("只有魔法师职业才能进入。")
		}
}