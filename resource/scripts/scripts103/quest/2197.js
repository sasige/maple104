/* ===========================================================
			注释(cm.sendSimple\cm.itemQuantity(5420008))
	脚本类型: 		QUEST
	脚本名字:		
	完成任务函数：qm.forceCompleteQuest();
	接受任务函数：qm.forceStartQuest();
=============================================================
制作时间：
制作人员：笔芯
=============================================================
*/
var a = -1;

function end(mode, type, selection) {//接任务
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
				qm.sendAcceptDecline("嗯？#h0#。很高兴你能来。想了解一下怪物手册的秘密吗？")
			}else if (a == 1) {  
				qm.sendNextPrev("怪物手册是怪物们所收集的一种怪物卡，收集好怪物卡之后他会自动收集到手册。")
			}else if (a == 2) {
				qm.sendNextPrev("让我看看你拥有几张怪物卡。")
			}else if (a == 3) {
				qm.sendAcceptDecline("看来你没有收集到怪物卡，我送你几张回城卷轴，这样的话你随时可以来找我。")
			}else if (a == 4) {
				if (qm.canHold(2030001)) {
				qm.gainItem(2030001,1)
				qm.forceCompleteQuest()
				qm.sendNextPrev("请收好我送给你的东西，收集到怪物卡的时候可以随时来找我。")
				qm.dispose();
				}else{
				qm.sendNextPrev("嗯？看来你的背包不足。请检查一下再跟我对话吧。")
				qm.dispose();
				}
		}//status
	}//mode
}//function
