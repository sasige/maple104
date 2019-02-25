var baseid = 240020500;
var dungeonid = 240020501;
var dungeons = 30;

function enter(pi) {
    if (pi.getMapId() == baseid) {
	if (pi.getParty() != null) {
	    if (pi.isLeader()) {
		for (var i = 0; i < dungeons; i++) {
		    if (pi.getPlayerCount(dungeonid + i) == 0) {
			pi.warpParty(dungeonid + i);
			return;
		    }
		}
	    } else {
		pi.playerMessage(5, "你不是组队长。");
	    }
	} else {
	    for (var i = 0; i < dungeons; i++) {
		if (pi.getPlayerCount(dungeonid + i) == 0) {
		    pi.warp(dungeonid + i);
		    return;
		}
	    }
	}
	pi.playerMessage(5, "现在你不能进入到里面，请稍后再试。（人数已经超过系统限制。）");
    } else {
	
	pi.warp(baseid, "MD00");
    }
}