/*
	NPC Name: 		Nineheart
	Description: 		Quest - Do you know the black Magician?
*/

var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 8) {
            qm.sendNext("噢，你还有什么问题吗？如果你要跟我对话，请重新打开界面。");
            qm.safeDispose();
            return;
        }
        status--;
    }
    if (status == 0) {
        qm.askAcceptDecline("你好， #h0#。 欢迎来到 圣地。 我的名字是 #p1101002# 。 \r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0# \r\n#fUI/UIWindow.img/QuestIcon/8/0# 380 exp");
    } else if (status == 1) {
        if (qm.getQuestStatus(20016) == 0) {
            qm.gainExp(380);
            qm.forceCompleteQuest();
        }
        qm.safeDispose();
    }
}

function end(mode, type, selection) {
}
