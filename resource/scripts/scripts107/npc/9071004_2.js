var maps = Array(952000000, 952010000, 952020000, 952030000, 952040000);
var mapNames = Array("石人寺院<5%掉落纪念币>", "废都广场<8%掉落纪念币>", "魔女雪原<10%掉落纪念币>", "寂静之海<12%掉落纪念币>", "黑暗神殿<15%掉落纪念币>");
var minLevel = Array(2, 11, 31, 61, 81);
var maxLevel = Array(10, 30, 60, 80, 100);

function start() {
    var selStr = "你想进入哪个地方?你当前转生次数为：" + cm.getChar().getReborns() + "\r\n#r（2转以上100转以下的玩家可以使用）\r\n#b";
    for (var i = 0; i < maps.length; i++) {
        selStr += "#L" + i + "#" + mapNames[i] + "（" + minLevel[i] + "转~" + maxLevel[i] + "转）#l\r\n";
    }
    cm.sendSimple(selStr);
}

function action(mode, type, selection) {
    if (mode == 1 && selection >= 0 && selection < maps.length) {
        if (cm.getParty() == null || !cm.isLeader()) {
            cm.sendOk("你想要进入的地区是组队游戏区域。可以通过#b队长#k入场。");
        } else {
            var party = cm.getParty().getMembers().iterator();
            var next = true;
            while (party.hasNext()) {
                var cPlayer = party.next();
                if (cPlayer.getReborns() < minLevel[selection] || cPlayer.getReborns() > maxLevel[selection] || cPlayer.getMapid() != cm.getMapId() || cm.haveItem(4001513) > 1) {
                    next = false;
                }
            }
            if (!next) {
                cm.sendOk("请确定你组员都在该地图,并且都在转数范围内\r\n或者队长没有#r#z4001514##k,无法带队进入\r\n#r#z4001514##k在左边的梅里换取");
            } else {
                var em = cm.getEventManager("MonsterPark");
                if (em == null || em.getInstance("MonsterPark" + maps[selection]) != null) {
                    cm.playerMessage("MonsterPark em " + em);
                    cm.sendOk("怪物公园里面已经有人了.");
                } else {
                    cm.gainItem(4001513,-1);
                    em.startInstance_Party("" + maps[selection], cm.getPlayer());
                }
            }
        }
    }
    cm.dispose();
}