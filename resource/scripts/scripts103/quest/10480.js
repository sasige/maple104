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
            qm.forceCompleteQuest();
            qm.sendOk("重新上线或更换频道来解除任务提示。");
            qm.dispose();
        }
    }
}
