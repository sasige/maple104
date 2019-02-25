

importPackage(net.sf.odinms.server.maps);
importPackage(net.sf.odinms.net.channel);
importPackage(net.sf.odinms.tools);

function enter(pi) {
	var nextMap = 922010200;
	var eim = pi.getPlayer().getEventInstance();
	var target = eim.getMapInstance(nextMap);
	var targetPortal = target.getPortal("st00");
	// only let people through if the eim is ready
	var avail = eim.getProperty("stage1status");
	if (avail == null) {
		// do nothing; send message to player
		pi.playerMessage("这只是一道很普通的墙壁。");
		return false;
	} else {
		pi.getPlayer().changeMap(target, targetPortal);
		return true;
	}
}