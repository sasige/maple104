importPackage(net.sf.odinms.server.life);
importPackage(net.sf.odinms.tools);
importPackage(java.awt);

var monsters = Array(9300184, 9300185, 9300186, 9300187, 9300188, 0, 9300189, 9300190, 9300191, 9300192, 9300193, 0, 9300194, 9300195, 9300196, 9300197, 9300198, 0, 9300199, 9300200, 9300201, 9300202, 9300203, 0, 9300204, 9300205, 9300206, 9300207, 9300208, 0, 9300209, 9300210, 9300211, 9300212, 9300213, 0, 9300214, 9300215);

function start(ms) {
    ms.getPlayer().resetEnteredScript();
    var temp = (ms.getPlayer().getMapId() - 925000000) / 100;
    var stage = (temp - (Math.floor(temp / 100) * 100)) | 0;
    
    ms.getClient().getSession().write(MaplePacketCreator.getClock(360)); //6 minutes.
    
    if (!isRestingSpot(ms.getPlayer().getMap().getId())) {
        ms.getClient().getSession().write(MaplePacketCreator.playSound("Dojang/start"));
        ms.getClient().getSession().write(MaplePacketCreator.showEffect("dojang/start/stage"));
        ms.getClient().getSession().write(MaplePacketCreator.showEffect("dojang/start/number/" + stage));
        ms.getClient().getSession().write(MaplePacketCreator.getEnergy(300));
    }
    
    if (monsters[stage - 1] != 0) {
        var mob = MapleLifeFactory.getMonster(monsters[stage - 1]);
        if (mob != null) {
            mob.setBoss(false);
            ms.getPlayer().getMap().spawnMonsterWithEffect(mob, 15, new Point(140, 0));
        }
    }
}

function isRestingSpot(id) {
    var shortid = id / 100;

    switch (shortid) {
    case 9250206:
    case 9250212:
    case 9250218:
    case 9250224:
    case 9250230:
    case 9250236:
        return true;
    }
    return false;
}