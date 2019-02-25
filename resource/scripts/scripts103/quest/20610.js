/*
 * Cygnus Skill -
 */

var status = -1;

function start(mode, type, selection) {
    status++;

    if (status == 0) {
	qm.sendAcceptDecline("点接受解除任务。");
    } else if (status == 1) {
	    qm.forceStartQuest();
	qm.dispose();
    }
}

function end(mode, type, selection) {
    qm.dispose();
}
