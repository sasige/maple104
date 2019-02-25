/* ===========================================================
			注释(cm.sendSimple\cm.itemQuantity(5420008))
	脚本类型: 		QUEST
	脚本名字:		星愿的问答
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
		qm.sendOk("回答错误，再想想。")	
				qm.dispose();
	} else {
		if (mode == 1)
			a++;
		else
			a--;
			if(a == -1){
				qm.dispose();
			}else if (a == 0) {
				qm.sendYesNo("请用O（是），X（不是）回答下一个问题。#b\r\nNPC比克·爱德华王子收皇家美发券才能帮助设计发型？#k")
			}else if (a == 1) {
				qm.sendOk("哦～真了不起！回答正确～")
				qm.forceCompleteQuest();
				qm.dispose();
		}//status
	}//mode
}//function
