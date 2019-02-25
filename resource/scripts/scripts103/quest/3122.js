/* ===========================================================
			注释(cm.sendSimple\cm.itemQuantity(5420008))
	脚本类型: 		QUEST
	脚本名字:		封印的确认
	完成任务函数：qm.forceCompleteQuest();
	接受任务函数：qm.forceStartQuest();
=============================================================
制作时间：2010年9月12日 22:45:59
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
				qm.sendOk("请帮我赶紧确认后回来吧！")
				qm.dispose();
			}else if (a == 0) {
				qm.sendYesNo("喔喔，帮我确认好回来了吗？ ");
			}else if ( a == 1){
				qm.sendNextPrev("呼，好了，邪摩斯。我亲眼确认过了，他被完全封印了起来。",2);
			}else if ( a == 2){
				qm.sendPrev("真的？那就太好了……谢谢你。多亏了你，一切应该都会好起来的。一切……")
				qm.forceCompleteQuest();
				
		}//status
	}//mode
}//function
