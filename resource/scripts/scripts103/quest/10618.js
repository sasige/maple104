/* ===========================================================
			注释(cm.sendSimple\cm.itemQuantity(5420008))
	脚本类型: 		QUEST
	脚本名字:		
	完成任务函数：qm.forceCompleteQuest();
	接受任务函数：qm.forceStartQuest();
=============================================================
制作时间：2011年4月13日 18:18:56
制作人员：
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
				qm.sendNext("暗影双刀达到了90级！\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n\r\n#i3800008# 猫头鹰图标 1个。\r\n\r\n#i1012191# #t1012191# 1个。")
			}else if (a == 1) {
				qm.sendOk("领取成功了。")
				qm.gainItem(1012191,1)
				qm.gainItem(3800008,1)
				qm.forceCompleteQuest();
	    			qm.safeDispose();
		}//status
	}//mode
}//function
