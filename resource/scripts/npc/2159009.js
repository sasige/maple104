function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.sendNext("不错的选择1。");
        cm.dispose();
    } else {
        if (status == 0 && mode == 0) {
            cm.sendNext("别婆婆妈妈的了，快点。");
            cm.dispose();
        }
        if (mode == 1) {
            status++;
        } else {
            status--;
        }
        if (status == 0) {
            cm.sendYesNo("快点上船.他们马上就要追上来了，你们还想回那个实验室吗？");
        } else if (status == 1) {
            cm.warp(931000030);
            cm.dispose();
        }
    }
}