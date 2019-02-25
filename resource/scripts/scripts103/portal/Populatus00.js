
//非同意禁止转载
importPackage(net.sf.odinms.server.maps);
importPackage(net.sf.odinms.net.channel);
importPackage(net.sf.odinms.tools);

function enter(pi) {
	var papuMap = ChannelServer.getInstance(pi.getPlayer().getClient().getChannel()).getMapFactory().getMap (220080001);
	if (papuMap.getCharacters().isEmpty()&&pi.getBossLog('Populatus00')<2&&pi.getPlayer().getClient().getChannel()  ==2) {
		var mapobjects = papuMap.getMapObjects();
		var iter = mapobjects.iterator();
		while (iter.hasNext()) {
			o = iter.next();
			if (o.getType() == MapleMapObjectType.MONSTER){
				//papuMap.killMonster(o, pi.getPlayer(), false);
				papuMap.removeMapObject(o);
			}
		}
		papuMap.resetReactors();
	}
	else { // someone is inside
		var mapobjects = papuMap.getMapObjects();
		var boss = null;
		var iter = mapobjects.iterator();
		while (iter.hasNext()) {
			o = iter.next();
			if (o.getType() == MapleMapObjectType.MONSTER){
				boss = o;
			}
		}
		if (boss != null) {
			sendMessage(pi,"有人正在挑战 " + boss.getName() + ".");
			return false;
		}
		if (pi.getBossLog('Populatus00')>=2) {
			sendMessage(pi,"每天只能挑战2次");
			return false;
		}
	}
	
	pi.setBossLog('Populatus00');
	return pi.warp(220080001, "sp","st00");
}

function sendMessage(pi,message) {
	pi.getPlayer().getClient().getSession().write(MaplePacketCreator.serverNotice(5, message));
}
