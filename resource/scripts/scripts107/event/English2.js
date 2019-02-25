var mapId = 802000211;
var item = Array(1302059, 1312031, 1332052, 1332049, 1332050, 1382036, 1402036, 1412026, 1422028, 1432038, 1442045, 1452044, 1462039, 1472051, 1472052, 1482013, 1492013, 1342010); //110装备

function init() {
    em.setProperty("state", "0");
    em.setProperty("leader", "true");
}

function setup(eim, leaderid) {
    em.setProperty("state", "1");
    em.setProperty("leader", "true");
    var eim = em.newInstance("Vergamot" + leaderid);

    eim.setProperty("vergamotSummoned", "0");

    var map = eim.setInstanceMap(mapId);
    map.resetFully();

    var mob = em.getMonster(9400276);
    eim.registerMonster(mob);
    map.spawnMonsterOnGroundBelow(mob, new java.awt.Point(156, 258));

    eim.startEventTimer(14400000); // 4 hrs
    return eim;
}

function playerEntry(eim, player) {
    var map = eim.getMapInstance(0);
    player.changeMap(map, map.getPortal(0));
}

function playerRevive(eim, player) {
    return false;
}

function scheduledTimeout(eim) {
    end(eim);
}

function changedMap(eim, player, mapid) {
    if (mapid != 802000211) {
        eim.unregisterPlayer(player);

        if (eim.disposeIfPlayerBelow(0, 0)) {
            em.setProperty("state", "0");
            em.setProperty("leader", "true");
        }
    }
}

function playerDisconnected(eim, player) {
    return 0;
}

function monsterValue(eim, mobId) {
    return 1;
}

function playerExit(eim, player) {
    eim.unregisterPlayer(player);

    if (eim.disposeIfPlayerBelow(0, 0)) {
        em.setProperty("state", "0");
        em.setProperty("leader", "true");
    }
}

function end(eim) {
    eim.disposeIfPlayerBelow(100, 802000212);
    em.setProperty("state", "0");
    em.setProperty("leader", "true");
}

function clearPQ(eim) {
    end(eim);
}

function allMonstersDead(eim) {
    var prop = eim.getProperty("vergamotSummoned");
    if (prop.equals("0")) {
        eim.setProperty("vergamotSummoned", "1");
        var map = eim.getMapInstance(0);
        var mob = em.getMonster(9400276);
        eim.registerMonster(mob);
        map.spawnMonsterOnGroundBelow(mob, new java.awt.Point(156, 258));
        map.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(5, "恭喜,以通关1!"));
    } else if (prop.equals("1")) {
        eim.setProperty("vergamotSummoned", "2");
        var map = eim.getMapInstance(0);
        var mob = em.getMonster(9400276);
        eim.registerMonster(mob);
        map.spawnMonsterOnGroundBelow(mob, new java.awt.Point(156, 258));
        map.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(5, "恭喜,以通关2!"));
    } else if (prop.equals("2")) {
        eim.setProperty("vergamotSummoned", "3");
        var map = eim.getMapInstance(0);
        var mob = em.getMonster(9400276);
        eim.registerMonster(mob);
        map.spawnMonsterOnGroundBelow(mob, new java.awt.Point(156, 258));
        map.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(5, "恭喜,以通关3!"));
    } else if (prop.equals("3")) {
        eim.setProperty("vergamotSummoned", "4");
        var map = eim.getMapInstance(0);
        var mob = em.getMonster(9400276);
        eim.registerMonster(mob);
        map.spawnMonsterOnGroundBelow(mob, new java.awt.Point(156, 258));
        map.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(5, "恭喜,以通关4!"));
    } else if (prop.equals("4")) {
        var iter = em.getInstances().iterator();
        while (iter.hasNext()) {
            var eim = iter.next();
            var pIter = eim.getPlayers().iterator();
            while (pIter.hasNext()) {
                var chr = pIter.next();
                //var winner = eim.getPlayers().get(0);
                var map = eim.getMapFactory().getMap(mapId);
                var randitem = Math.floor(Math.random() * item.length);
                var toDrop = new Packages.client.inventory.Item(4002000, 0, 1);
                for (var i = 0; i < 10; i++) {
                    //map.spawnItemDrop(chr, chr, toDrop, chr.getPosition(), true, false);
					map.spawnAutoDrop(4002000,chr.getPosition());
                }
                //toDrop = new Packages.client.inventory.Item(item[randitem], 0, 1);
                //map.spawnItemDrop(winner, winner, toDrop, winner.getPosition(), true, false);
				map.spawnAutoDrop(item[randitem],chr.getPosition());
                map.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(5, "恭喜,以通完全部关卡!"));
            }
        }
    }
}

function leftParty(eim, player) {}

function disbandParty(eim) {}

function playerDead(eim, player) {}

function cancelSchedule() {}