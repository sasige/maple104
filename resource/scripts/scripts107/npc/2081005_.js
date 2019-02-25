/*
	NPC 名字: 		可罗宾-守门人
	所在地图: 		生命之穴洞口
	脚本名字: 		暗黑龙王远征队
*/

var morph;
var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
    if (status == 0) {
        morph = cm.getMorphState();
        if (morph == 2210003 || cm.isQuestFinished(7301)) {
            cm.sendNext("哦，我的兄弟！不要担心人类的入侵.我会保护你.请进.");
        } else {
            var hp = cm.getPlayerStat("HP");
            if (hp > 500) {
                cm.addHP( - 500);
            } else if (hp > 1 && hp <= 500) {
                cm.addHP( - (hp - 1));
            }
            cm.sendNext("这是远远不够的，人类！没有人可以超越这一点.离开这里！");
        }
    } else if (status == 1) {
        if (morph == 2210003 || cm.isQuestFinished(7301)) {
            cm.cancelItem(2210003);
            cm.warp(240050000, 0);
            if (cm.haveItem(4031454)) { // Paladin
                cm.gainItem(4031454, -1);
                cm.gainItem(4031455, 1);
            }
            if (cm.getQuestStatus(6169) == 1) { // Temporary
                cm.gainItem(4031461, 1);
            }
        } else {
            cm.warp(240040600, "st00");
        }
        cm.dispose();
    }
}