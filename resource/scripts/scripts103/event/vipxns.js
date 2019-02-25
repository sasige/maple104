var exitMap;
var instanceId;
var minPlayers = 6;
var mapId=271040100;

function init() {
	instanceId = 1;
}

function monsterValue(eim, mobId) {
	return 1;
}

function setup() {
	exitMap = em.getChannelServer().getMapFactory().getMap(271040110); // <exit>
	var instanceName = "vipxns" ;
	var eim = em.newInstance(instanceName);
	var mf = eim.getMapFactory();
	instanceId++;
	var eventTime = 60 * (1000 * 60);
	em.schedule("timeOut", eventTime);
	em.schedule("monsterstart", 6000);
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
		var mob = net.sf.odinms.server.life.MapleLifeFactory.getMonster(8850005);
var mob2 = net.sf.odinms.server.life.MapleLifeFactory.getMonster(8850006);
var mob3 = net.sf.odinms.server.life.MapleLifeFactory.getMonster(8850007);
var mob4 = net.sf.odinms.server.life.MapleLifeFactory.getMonster(8850008);
var mob5 = net.sf.odinms.server.life.MapleLifeFactory.getMonster(8850009);
        var overrideStats = new net.sf.odinms.server.life.MapleMonsterStats(); 
        var map = eim.getMapFactory().getMap(mapId); 
        var xRand = Math.floor(Math.random() * 150)+25;
        overrideStats.setHp(1000000000); 
        overrideStats.setExp(50000); 
        overrideStats.setMp(50000000); 
        mob.setOverrideStats(overrideStats); 
        mob.setHp(1000000000);
        eim.registerMonster(mob);
        map.spawnMonsterOnGroudBelow(mob, new java.awt.Point(377, 115));


        var overrideStats = new net.sf.odinms.server.life.MapleMonsterStats(); 
        var map = eim.getMapFactory().getMap(mapId); 
        var xRand = Math.floor(Math.random() * 150)+25;
        overrideStats.setHp(1000000000); 
        overrideStats.setExp(50000); 
        overrideStats.setMp(50000000); 
        mob2.setOverrideStats(overrideStats); 
        mob2.setHp(1000000000);
        eim.registerMonster(mob2);
        map.spawnMonsterOnGroudBelow(mob2, new java.awt.Point(377, 115));

        var overrideStats = new net.sf.odinms.server.life.MapleMonsterStats(); 
        var map = eim.getMapFactory().getMap(mapId); 
        var xRand = Math.floor(Math.random() * 150)+25;
        overrideStats.setHp(1000000000); 
        overrideStats.setExp(50000); 
        overrideStats.setMp(50000000); 
        mob3.setOverrideStats(overrideStats); 
        mob3.setHp(1000000000);
        eim.registerMonster(mob3);
        map.spawnMonsterOnGroudBelow(mob3, new java.awt.Point(377, 115));

        var overrideStats = new net.sf.odinms.server.life.MapleMonsterStats(); 
        var map = eim.getMapFactory().getMap(mapId); 
        var xRand = Math.floor(Math.random() * 150)+25;
        overrideStats.setHp(1000000000); 
        overrideStats.setExp(50000); 
        overrideStats.setMp(50000000); 
        mob4.setOverrideStats(overrideStats); 
        mob4.setHp(1000000000);
        eim.registerMonster(mob4);
        map.spawnMonsterOnGroudBelow(mob4, new java.awt.Point(377, 115));

        var overrideStats = new net.sf.odinms.server.life.MapleMonsterStats(); 
        var map = eim.getMapFactory().getMap(mapId); 
        var xRand = Math.floor(Math.random() * 150)+25;
        overrideStats.setHp(1000000000); 
        overrideStats.setExp(50000); 
        overrideStats.setMp(50000000); 
        mob5.setOverrideStats(overrideStats); 
        mob5.setHp(1000000000);
        eim.registerMonster(mob5);
        map.spawnMonsterOnGroudBelow(mob5, new java.awt.Point(377, 115));
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
    var iter = em.getInstances().iterator();
	while (iter.hasNext()) {
		var eim = iter.next();
		var mob22 = net.sf.odinms.server.life.MapleLifeFactory.getMonster(8850011);


        var overrideStats = new net.sf.odinms.server.life.MapleMonsterStats(); 
        var map = eim.getMapFactory().getMap(mapId); 
        var xRand = Math.floor(Math.random() * 150)+25;
        if(em.getProperty("entryPossible").equals("1")==true){
            em.setProperty("entryPossible", "2");
            overrideStats.setHp(2100000000); 
            overrideStats.setExp(6000000); 
            overrideStats.setMp(500000000); 
            mob22.setOverrideStats(overrideStats); 
            mob22.setHp(2100000000);
 eim.registerMonster(mob22);
        map.spawnMonsterOnGroudBelow(mob22, new java.awt.Point(377, 115));
			var eims = eim.getPlayers().iterator();
            while (eims.hasNext()) {
                var chrs=eims.next();
				//chrs.sethyd(2);
				//chrs.dropMessage(5,"恭喜,以通关!");
			}
       } else if(em.getProperty("entryPossible").equals("2")==true){
            var iter = em.getInstances().iterator();
            while (iter.hasNext()) {
                var eim = iter.next();
                var pIter = eim.getPlayers().iterator();
                while (pIter.hasNext()) {
                var chr=pIter.next();
                    //chr.startMapEffect("[恭喜您成功通关]", 5121008);
					//chr.sethyd(20);
					//chr.dropMessage(5,"恭喜,以通关!");
                em.getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0,"玩家 "+chr.getName()+" 通过了希纳斯骑士团."));
             
                }
            }
            
            return;
        }
       
        map.broadcastMessage(net.sf.odinms.tools.MaplePacketCreator.serverNotice(5, "希纳斯已经被唤醒!"));
	}
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
