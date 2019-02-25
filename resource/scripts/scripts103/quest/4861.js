/* ===========================================================
			注释(cm.sendSimple\cm.itemQuantity(5420008))
	脚本类型: 		QUEST
	脚本名字:		回归玩家活动
	完成任务函数：qm.forceCompleteQuest();
	接受任务函数：qm.forceStartQuest();
=============================================================
制作时间：2010年12月30日 13:33:44
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
				qm.sendNext("为纪念服务器开启，现在可以领取奖励哦！")
			}else if (a == 1) {
				qm.gainItem(2430182,1)
				qm.sendOk("领取成功了。")
				qm.forceCompleteQuest();
				qm.dispose();
		}//status
	}//mode
}//function
