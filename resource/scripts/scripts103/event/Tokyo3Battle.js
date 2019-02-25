function init() {
}

function setup(eim, leaderid) {
    var eim = em.newInstance("Tokyo3Battle" + leaderid);
    
    eim.setProperty("Tokyo3Summoned", "0");
    eim.createInstanceMap(802000821);
	eim.startEventTimer(1000*60*60*4); // 4 hour
    eim.schedule("scheduledTimeout", 1000*60*60*4); // 4 hour

    return eim;
}

function playerEntry(eim, player) {
    var map = eim.getMapInstance(0);
    player.changeMap(map, map.getPortal(0));
	player.openNpc(9900000)
}

function playerRevive(eim, player) {
    return false;
}

function scheduledTimeout(eim) {
    eim.disposeIfPlayerBelow(100, 802000822);
}

function changedMap(eim, player, mapid) {
    if (mapid != 802000821) {
	eim.unregisterPlayer(player);

	eim.disposeIfPlayerBelow(0, 0);
    }
}

function playerDisconnected(eim, player) {
    return 0;
}

function scheduledTimeout(eim) {
    end(eim);
}

function monsterKilled(eim, mobId) {
    return 1;
}

function playerExit(eim, player) {
    eim.unregisterPlayer(player);

    eim.disposeIfPlayerBelow(0, 0);
}

function end(eim) {
    eim.disposeIfPlayerBelow(100, 802000822);

    em.setProperty("Tokyo3Summoned", "0");
}

function clearPQ(eim) {
    end(eim);
}

function allMonstersDead(eim) {
}

function leftParty (eim, player) {}
function disbandParty (eim) {}
function playerDead(eim, player) {}
function cancelSchedule() {}