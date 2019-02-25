var status = 0;
var select;

function start() {
    status = -1;
    action(1, 0, 0);
} 

function action(mode, type, selection) {
    if (mode == 1)
        status++;
    else if (mode == -1)
        status--;
    else {
        cm.dispose();
        return;
    }
    if (status == 0) {
        //cm.openShop(109);
        cm.dispose();
    }
}