

function start() {
    cm.sendSimple ("#e你想挑战黑暗龙王吗？黑暗龙王是很强大的BOSS。每天每人只能挑战2次。如果您的挑战次数已经到达上限了，那么可以到我这里来。我可以多允许您进去10次。但是您要具备以下条件才行。#k\r\n#g条件一:必须拥有一张黑龙挑战卷.#k#v5220006#\r\n#b条件二:需要两名以上的队伍.#k\r\n#r条件三:队员中有人超过5次的就无法进入了.#k \r\n#L0#好吧，我明白了，请把我们传送进去！ ");
    }

function action(mode, type, selection) {
        cm.dispose();
 
if (selection == 0) {
if (cm.getParty() == null) {
cm.sendOk("抱歉你没有组队，无法进入，挑战黑龙至少需要2个人");
} else if (!cm.isLeader()) {
cm.sendOk("如果你想进入, 那么请 #b你的组队长#k 来告诉我！");
} else if (cm.getPartyOnlineNumber() < 1) {
cm.sendOk("您的组队在线人数不足2人 无法进入");
} else if (cm.isPartyAllHere() == false) {
cm.sendOk("您的组员还没有来齐，请等他们来之后在一起挑战黑龙");
} else if (cm.getPartyBossLog('HEILONG', 5)) {
cm.sendOk("您的组员中有人今天已经挑战过5次黑龙了。");
} else if(!cm.haveItem(4001017,1)) {
cm.sendOk("您没有#v4001017#,无法进入。");
} else if (cm.getPartyOnlineNumber() >= 2 && cm.isPartyAllHere() != false) {
cm.setPartyBossLog('HEILONG');
cm.gainMeso(-20000000);
cm.warpParty(910340200);
} else {
cm.sendOk("挑战黑龙至少要2个人哦！");
}
}
}
