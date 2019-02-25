var baseid = 541000300;
var dungeonid = 541000301;
var dungeons = 19;

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
		pi.playerMessage(5, "�㲻����ӳ���");
	    }
	} else {
	    for (var i = 0; i < dungeons; i++) {
		if (pi.getPlayerCount(dungeonid + i) == 0) {
		    pi.warp(dungeonid + i);
		    return;
		}
	    }
	}
	pi.playerMessage(5, "�����㲻�ܽ��뵽���棬���Ժ����ԡ��������Ѿ�����ϵͳ���ơ���");
    } else {
	
	pi.warp(baseid, "MD00");
    }
}