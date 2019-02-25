/* ===========================================================
			注释(cm.sendSimple\cm.itemQuantity(5420008))
	脚本类型: 		QUEST
	脚本名字:		决战！移动要塞贝尔加莫特（未来东京BOSS第一关）
	完成任务函数：qm.forceCompleteQuest();
	接受任务函数：qm.forceStartQuest();
=============================================================
制作时间：2010年8月7日 11:20:11
制作人员：笔芯
=============================================================
*/
var a = -1;

function start(mode, type, selection) {//接任务
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
				qm.sendOk("暂时没有开放此任务。")
				qm.forceCompleteQuest();
				qm.dispose();
		}//status
	}//mode
}//function
