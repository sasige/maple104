var status = -1;

function start(mode, type, selection) {
    if (mode == -1) {
        qm.dispose();
    } else {
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            qm.sendYesNo("你知道吗？战神复活的事情？那么请几天后再来找我吧。");
        } else if (status == 1) {
            qm.forceCompleteQuest();
            qm.dispose();
        }
    }
}
