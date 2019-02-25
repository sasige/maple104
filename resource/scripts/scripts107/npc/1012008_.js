var status = -1;
var renwu = "";
var wanjia = "";

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1 || mode == 0) {
        cm.dispose();
    } else {
        if (mode == 1) status++;
        else status--;
        if (status == 0) {
            cm.sendSimple("这里是冒险岛赌博中心，请选择你需要的！\r\n#b#L1#使用点卷赌博！#l \r\n#b#L2#使用修为赌博！#l \r\n#b#L3#使用冒险币赌博！\r\n#b#L4#使用消费币赌博#l");
        } else if (status == 1) {
            if (selection == 1) {
		cm.dispose();
                cm.openNpc(1012008,1);
            } else if (selection == 2) {
                cm.dispose();
            	cm.openNpc(1012008, 2);
            } else if (selection == 3) {
		cm.dispose();
                cm.openNpc(1012008,3);
            } else if (selection == 4) {
		cm.dispose();
                cm.openNpc(1012008,4);
            }
        }
    }
}
