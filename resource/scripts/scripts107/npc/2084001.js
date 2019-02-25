/*
	金利奇
*/
var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (status == 0 && mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
    if (cm.getMapId() == 180000001) {
            cm.sendOk("很遗憾，您因为违反用户守则被禁止游戏活动，如有异议请联系管理员.")
            cm.dispose();
        } 
    else if (status == 0) {
        var selStr = "有什么事吗？\r\n\r\n#b#L0#智能机器人#l  \r\n#L1#特殊物品#l  \r\n#L2#必成卷轴#l  \r\n#L3#职业装备#l  \r\n#L4#专业传送#l";
		cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
        case 0:
			cm.dispose();
			cm.openNpc(2084001,2)
            break;
        case 1:
			cm.dispose();
			cm.openNpc(2084001,3)
            break;
        case 2:
			cm.dispose();
			cm.openNpc(2084001,4)
            break;
        case 3:
			cm.dispose();
			cm.openNpc(2084001,5)
            break;
        case 4:
			cm.dispose();
			cm.openNpc(2084002)
            break;
        }
    }
}