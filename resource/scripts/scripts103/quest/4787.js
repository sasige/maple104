/* ===========================================================
			注释(cm.sendSimple\cm.itemQuantity(5420008))
	脚本类型: 		QUEST
	脚本名字:		假面绅士的邀请
	完成任务函数：qm.forceCompleteQuest();
	接受任务函数：qm.forceStartQuest();
=============================================================
制作时间：2010年9月12日 22:25:44
制作人员：笔芯
=============================================================
*/
var a = -1;

function end(mode, type, selection) {//quesd end
	if (mode == -1) {
		qm.dispose();
	} else {
		if (mode == 1)
			a++;
		else
			a--;
			if(a == -1){
				qm.dispose();
			}else if (a == 0) {
				qm.sendNext("啊，你来了。来，这是寄你的东西。好像是邀请你参加什么舞会的请帖……嗯，#r假面绅士的舞会#k的话，一定会很有意思。如果你感兴趣的话，就#b双击道具#k试试。")
			}else if ( a == 1){
				qm.sendNext("看来你是没有放请帖的地方了？先确认一下消耗窗口中有没有空格。")
				qm.dispose();
		}//status
	}//mode
}//function
