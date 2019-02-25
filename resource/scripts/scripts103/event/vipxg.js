var exitMap;
var instanceId;
var minPlayers = 6;
var mapId=686000202;

function init() {
	instanceId = 1;
}

function monsterValue(eim, mobId) {
	return 1;
}

function setup() {
	exitMap = em.getChannelServer().getMapFactory().getMap(910000000); // <exit>
	var instanceName = "vipxg" ;
	var eim = em.newInstance(instanceName);
	var mf = eim.getMapFactory();
	instanceId++;
	var eventTime = 10 * (1000 * 60);
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
			var mob = net.sf.odinms.server.life.MapleLifeFactory.getMonster(100007);
			var overrideStats = new net.sf.odinms.server.life.MapleMonsterStats(); 
			var map = eim.getMapFactory().getMap(mapId); 
			var xRand = -386-Math.floor(Math.random() * 650);
			var hprand = 10000;
			overrideStats.setHp(hprand); 
			overrideStats.setExp(100); 
			overrideStats.setMp(1000); 
			mob.setOverrideStats(overrideStats); 
			mob.setHp(hprand);
			eim.registerMonster(mob);
			
map.spawnMonsterOnGroudBelow(mob, new java.awt.Point(-386, -182));


 map.broadcastMessage(net.sf.odinms.tools.MaplePacketCreator.serverNotice(5, " 第一关开始,请在规定时间内消灭他们!"));
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
		var mob2 = net.sf.odinms.server.life.MapleLifeFactory.getMonster(9302011);

var mob3 = net.sf.odinms.server.life.MapleLifeFactory.getMonster(9300060);
var mob4 = net.sf.odinms.server.life.MapleLifeFactory.getMonster(9500340);
var mob5 = net.sf.odinms.server.life.MapleLifeFactory.getMonster(9500341);
        var overrideStats = new net.sf.odinms.server.life.MapleMonsterStats(); 
        var map = eim.getMapFactory().getMap(mapId); 
        var xRand = Math.floor(Math.random() * 150)+25;
        if(em.getProperty("entryPossible").equals("1")==true){
            em.setProperty("entryPossible", "2");
            overrideStats.setHp(20000); 
            overrideStats.setExp(100); 
            overrideStats.setMp(500); 
            mob2.setOverrideStats(overrideStats); 
            mob2.setHp(20000);
 eim.registerMonster(mob2);
        map.spawnMonsterOnGroudBelow(mob2, new java.awt.Point(-386, -151));

			var eims = eim.getPlayers().iterator();
            while (eims.hasNext()) {
                var chrs=eims.next();
				//chrs.sethyd(2);
				//chrs.dropMessage(5,"恭喜,以通关!");
			}
        }else if(em.getProperty("entryPossible").equals("2")==true){
            em.setProperty("entryPossible", "3");
            overrideStats.setHp(20000); 
            overrideStats.setExp(100); 
            overrideStats.setMp(500); 
            mob3.setOverrideStats(overrideStats); 
            mob3.setHp(20000);
 eim.registerMonster(mob3);
        map.spawnMonsterOnGroudBelow(mob3, new java.awt.Point(-386, -151));
 
			var eims = eim.getPlayers().iterator();
            while (eims.hasNext()) {
                var chrs=eims.next();
				//chrs.sethyd(3);
				//chrs.dropMessage(5,"恭喜,以通关！");
			}
        }else if(em.getProperty("entryPossible").equals("3")==true){
            em.setProperty("entryPossible", "4");
            overrideStats.setHp(50000); 
            overrideStats.setExp(1000); 
            overrideStats.setMp(2000); 
            mob4.setOverrideStats(overrideStats); 
            mob4.setHp(50000);
 eim.registerMonster(mob4);
        map.spawnMonsterOnGroudBelow(mob4, new java.awt.Point(-386, -152));
 
			var eims = eim.getPlayers().iterator();
            while (eims.hasNext()) {
                var chrs=eims.next();
				//chrs.sethyd(4);
				//chrs.dropMessage(5,"恭喜,以通关!");
			}
        }else if(em.getProperty("entryPossible").equals("4")==true){
            em.setProperty("entryPossible", "5");
            overrideStats.setHp(60000); 
            overrideStats.setExp(1000); 
            overrideStats.setMp(2000); 
            mob4.setOverrideStats(overrideStats); 
            mob4.setHp(60000);
 eim.registerMonster(mob4);
        map.spawnMonsterOnGroudBelow(mob4, new java.awt.Point(-386, -152));
 
			var eims = eim.getPlayers().iterator();
            while (eims.hasNext()) {
                var chrs=eims.next();
				//chrs.sethyd(4);
				//chrs.dropMessage(5,"恭喜,以通关!");
			}
        }else if(em.getProperty("entryPossible").equals("5")==true){
            em.setProperty("entryPossible", "6");
            overrideStats.setHp(80000); 
            overrideStats.setExp(1000); 
            overrideStats.setMp(2000); 
            mob4.setOverrideStats(overrideStats); 
            mob4.setHp(80000);
 eim.registerMonster(mob4);
        map.spawnMonsterOnGroudBelow(mob4, new java.awt.Point(-386, -152));
 
			var eims = eim.getPlayers().iterator();
            while (eims.hasNext()) {
                var chrs=eims.next();
				//chrs.sethyd(4);
				//chrs.dropMessage(5,"恭喜,以通关!");
			}
        }else if(em.getProperty("entryPossible").equals("6")==true){
            em.setProperty("entryPossible", "7");
            overrideStats.setHp(100000); 
            overrideStats.setExp(1000); 
            overrideStats.setMp(2000); 
            mob4.setOverrideStats(overrideStats); 
            mob4.setHp(100000);
 eim.registerMonster(mob4);
        map.spawnMonsterOnGroudBelow(mob4, new java.awt.Point(-386, -152));
 
			var eims = eim.getPlayers().iterator();
            while (eims.hasNext()) {
                var chrs=eims.next();
				//chrs.sethyd(4);
				//chrs.dropMessage(5,"恭喜,以通关!");
			}
        }else if(em.getProperty("entryPossible").equals("7")==true){
            em.setProperty("entryPossible", "8");
            overrideStats.setHp(2000000); 
            overrideStats.setExp(20000); 
            overrideStats.setMp(50000); 
            mob5.setOverrideStats(overrideStats); 
            mob5.setHp(2000000);
 eim.registerMonster(mob5);
        map.spawnMonsterOnGroudBelow(mob5, new java.awt.Point(-386, -151));
			var eims = eim.getPlayers().iterator();
            while (eims.hasNext()) {
                var chrs=eims.next();
				//chrs.sethyd(5);
				//chrs.dropMessage(5,"恭喜,以通关!");
			}
       
     
        }else if(em.getProperty("entryPossible").equals("8")==true){
            var iter = em.getInstances().iterator();
            while (iter.hasNext()) {
                var eim = iter.next();
                var pIter = eim.getPlayers().iterator();
                while (pIter.hasNext()) {
                var chr=pIter.next();
                    chr.startMapEffect("[你已经通过啦!得到BOSS的战利品了吗?]", 5121008);
					//chr.sethyd(20);
					
               
                }
            }
            
            return;
        }
       
        map.broadcastMessage(net.sf.odinms.tools.MaplePacketCreator.serverNotice(5, "第"+em.getProperty("entryPossible")+"关开始,请在规定时间内消灭他们!"));
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
