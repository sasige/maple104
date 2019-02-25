var status = -1;

function start() {
    action(1,0,0);
}

function action(mode, type, selection) {
    if (mode != 1) {
        cm.dispose();
        return;
    }
    status++;
    if (status == 0)
        cm.sendYesNo("Do you want to move to the Free Market?")
    else if (status == 1) {
        cm.warp(910000000);
        cm.saveReturnLocation("FREE_MARKET");
        cm.dispose();
    }
}