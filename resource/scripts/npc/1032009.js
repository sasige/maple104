function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.sendNext("不错的选择。");
        cm.dispose();
    } else {
        if (status == 0 && mode == 0) {
            cm.sendNext("不着急，船马上就开了");
            cm.dispose();
        }
        if (mode == 1) {
            status++;
        } else {
            status--;
        }
        if (status == 0) {
            cm.sendYesNo("你想下船吗？离开飞船后会回到原来的地方！");
        } else if (status == 1) {
            cm.warp(104020110);
            cm.dispose();
        }
    }
}