var exitMap;
var instanceId;
var minPlayers = 6;
var mapId=689013000;
var setupTask;

function init() {
	scheduleNew();
	instanceId = 1;
}

function monsterValue(eim, mobId) {
	return 1;
}

function setup() {
	exitMap = em.getChannelServer().getMapFactory().getMap(910000000); // <exit>
	var instanceName = "vipfszk";
	var eim = em.newInstance(instanceName);
	var mf = eim.getMapFactory();
	instanceId++;
	var eventTime = 10 * (1000 * 60);
	em.schedule("timeOut", eventTime);
	eim.startEventTimer(eventTime);
	em.schedule("monsterstart", 8000);
	em.setProperty("entryPossible", "1");
	
	return eim;
}

function scheduleNew() {
}

function playerEntry(eim, player) {
	var map = eim.getMapInstance(mapId);
	player.changeMap(map, map.getPortal(0));
	  
}

function allMonstersDead(eim) {
}

function monsterstart() {
    var iter = em.getInstances().iterator();
	while (iter.hasNext()) {
		var eim = iter.next();
			var mob = net.sf.odinms.server.life.MapleLifeFactory.getMonster(9400900);
	var mob2 = net.sf.odinms.server.life.MapleLifeFactory.getMonster(9400903);
	var mob3 = net.sf.odinms.server.life.MapleLifeFactory.getMonster(9400904);
	var mob4 = net.sf.odinms.server.life.MapleLifeFactory.getMonster(9400905);
	var mob5 = net.sf.odinms.server.life.MapleLifeFactory.getMonster(9400906);
	var mob6 = net.sf.odinms.server.life.MapleLifeFactory.getMonster(9400907);
	var mob7 = net.sf.odinms.server.life.MapleLifeFactory.getMonster(9400908);
	var mob8 = net.sf.odinms.server.life.MapleLifeFactory.getMonster(9400909);
	var mob9 = net.sf.odinms.server.life.MapleLifeFactory.getMonster(9400910);
			var overrideStats = new net.sf.odinms.server.life.MapleMonsterStats(); 
			var map = eim.getMapFactory().getMap(mapId); 
			var xRand = 330-Math.floor(Math.random() * 650);
			var hprand = 20000000;
			overrideStats.setHp(hprand); 
			overrideStats.setExp(20000); 
			overrideStats.setMp(200000); 
			mob.setOverrideStats(overrideStats); 
			mob.setHp(50000000);
			eim.registerMonster(mob);
			
map.spawnMonsterOnGroudBelow(mob, new java.awt.Point(-6, 297));

mob2.setOverrideStats(overrideStats); 
			mob2.setHp(hprand);
			eim.registerMonster(mob2);
			
map.spawnMonsterOnGroudBelow(mob2, new java.awt.Point(-6, 297));

mob3.setOverrideStats(overrideStats); 
			mob3.setHp(hprand);
			eim.registerMonster(mob3);
			
map.spawnMonsterOnGroudBelow(mob3, new java.awt.Point(-6, 297));

mob4.setOverrideStats(overrideStats); 
			mob4.setHp(hprand);
			eim.registerMonster(mob4);
			
map.spawnMonsterOnGroudBelow(mob4, new java.awt.Point(-6, 297));

mob5.setOverrideStats(overrideStats); 
			mob5.setHp(hprand);
			eim.registerMonster(mob5);
			
map.spawnMonsterOnGroudBelow(mob5, new java.awt.Point(-6, 297));

mob6.setOverrideStats(overrideStats); 
			mob6.setHp(hprand);
			eim.registerMonster(mob6);
			
map.spawnMonsterOnGroudBelow(mob6, new java.awt.Point(-6, 297));

mob7.setOverrideStats(overrideStats); 
			mob7.setHp(hprand);
			eim.registerMonster(mob7);
			
map.spawnMonsterOnGroudBelow(mob7, new java.awt.Point(-6, 297));

mob8.setOverrideStats(overrideStats); 
			mob8.setHp(hprand);
			eim.registerMonster(mob8);
			
map.spawnMonsterOnGroudBelow(mob8, new java.awt.Point(-6, 297));

mob9.setOverrideStats(overrideStats); 
			mob9.setHp(hprand);
			eim.registerMonster(mob9);
			
map.spawnMonsterOnGroudBelow(mob9, new java.awt.Point(-6, 297));

 map.broadcastMessage(net.sf.odinms.tools.MaplePacketCreator.serverNotice(5, " 粉色扎昆BOSS已经召唤,请在规定时间内打到它!"));
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
		
		em.setProperty("entryPossible", "0");
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
