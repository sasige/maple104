var exitMap;
var instanceId;
var minPlayers = 6;
var mapId=930000600;
var mapId2=930000200;
var mapId3=930000400;
var mapId4=930000500;
var mapId5=930000100;
function init() {
	instanceId = 1;
}

function monsterValue(eim, mobId) {
	return 1;
}

function setup() {
	exitMap = em.getChannelServer().getMapFactory().getMap(910000000); // <exit>
	var instanceName = "vipdw" ;
	var eim = em.newInstance(instanceName);
	var mf = eim.getMapFactory();
	instanceId++;
	var eventTime = 10 * (1000 * 60);
	em.schedule("timeOut", eventTime);
	em.schedule("monsterstart", 5000);
	em.setProperty("entryPossible", "1");
	eim.startEventTimer(eventTime);
	
	return eim;
}

function playerEntry(eim, player) {

	var map = eim.getMapInstance(mapId);
	player.changeMap(map, map.getPortal(0));


	
}

function monsterstart() {

    var iter = em.getInstances().iterator();
	while (iter.hasNext()) {
		var eim = iter.next();
		var mob = net.sf.odinms.server.life.MapleLifeFactory.getMonster(9300180);
        var overrideStats = new net.sf.odinms.server.life.MapleMonsterStats(); 
        var map = eim.getMapFactory().getMap(mapId); 
        var xRand = Math.floor(Math.random() * 150)+25;
        overrideStats.setHp(100000); 
        overrideStats.setExp(3000); 
        overrideStats.setMp(5000); 
        mob.setOverrideStats(overrideStats); 
        mob.setHp(100000);
        eim.registerMonster(mob);
        map.spawnMonsterOnGroudBelow(mob, new java.awt.Point(373, 150));
        map.broadcastMessage(net.sf.odinms.tools.MaplePacketCreator.serverNotice(5, "挑战 石头人 开始! !"));
	}


}

function playerDead(eim, player) {
}

function playerRevive(eim, player) {
	if (eim.isLeader(player)) { 
		var party = eim.getPlayers();
		for (var i = 0; i < party.size(); i++) {
			playerExit(eim, party.get(i));
		}
		eim.dispose();
	}
	else { //boot dead player
		// If only 2 players are left, uncompletable:
		var party = eim.getPlayers();
		if (party.size() <= minPlayers) {
			for (var i = 0; i < party.size(); i++) {
				playerExit(eim,party.get(i));
			}
			eim.dispose();
		}
		else
			playerExit(eim, player);
	}
}

function playerDisconnected(eim, player) {
	if (eim.isLeader(player)) { //check for party leader
		//boot whole party and end
		var party = eim.getPlayers();
		for (var i = 0; i < party.size(); i++) {
			if (party.get(i).equals(player)) {
				removePlayer(eim, player);
			}			
			else {
				playerExit(eim, party.get(i));
			}
		}
		eim.dispose();
	}
	else { //boot d/ced player
		// If only 2 players are left, uncompletable:
		var party = eim.getPlayers();
		if (party.size() < minPlayers) {
			for (var i = 0; i < party.size(); i++) {
				playerExit(eim,party.get(i));
			}
			eim.dispose();
		}
		else
			playerExit(eim, player);
	}
}

function leftParty(eim, player) {			
	// If only 2 players are left, uncompletable:
	var party = eim.getPlayers();
	if (party.size() <= minPlayers) {
		for (var i = 0; i < party.size(); i++) {
			playerExit(eim,party.get(i));
		}
		eim.dispose();
	}
	else
		playerExit(eim, player);
}

function disbandParty(eim) {
	//boot whole party and end
	var party = eim.getPlayers();
	for (var i = 0; i < party.size(); i++) {
		playerExit(eim, party.get(i));
	}
	eim.dispose();
}

function playerExit(eim, player) {
	em.setProperty("entryPossible", "0");
	eim.unregisterPlayer(player);
	player.changeMap(exitMap, exitMap.getPortal(0));
}

//for offline players
function removePlayer(eim, player) {
	eim.unregisterPlayer(player);
	player.getMap().removePlayer(player);
	player.setMap(exitMap);
}

function clearPQ(eim) {
	//KPQ does nothing special with winners
	var party = eim.getPlayers();
	for (var i = 0; i < party.size(); i++) {
		playerExit(eim, party.get(i));
	}
	eim.dispose();
}

function allMonstersDead(eim) {

}

function cancelSchedule() {
}

function timeOut() {
	var iter = em.getInstances().iterator();
	while (iter.hasNext()) {
		var eim = iter.next();
		if (eim.getPlayerCount() > 0) {
			var pIter = eim.getPlayers().iterator();
			while (pIter.hasNext()) {
				playerExit(eim, pIter.next());
			}
		}
		eim.dispose();
	}
}
