var status = 0;
var selectedType = -1;
var selectedItem = -1;

function start() {
status = -1;
action(1, 0, 0);
}

function action(mode, type, selection) {
if (mode == -1 || mode == 0) {
cm.dispose();
} else {
status++;
var Msize = cm.getParty().getMembers().size();
var Pleader = cm.getParty().getLeader();
if (status == 0) {
cm.sendOk("Msize = " + Msize + "\r\nLeader = " + Pleader + "\r\nEnjoy");
cm.dispose();
}
}
}