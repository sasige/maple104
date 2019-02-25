/* ===========================================================
			注释
	脚本类型: 		QUEST
	脚本名字:		一口甘草
=============================================================
制作时间：2010年8月5日 22:27:00
制作人员：笔芯
=============================================================
*/
var status = -1;

function end(mode, type, selection) {//接任务
	if (mode == -1) {
		qm.dispose();
	} else {
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
			qm.sendYesNo("可能和蜥蜴一样，可以喂它吃#b#t4032452##k。这里有很多#b草垛#k，你可以喂给它试试。如果不吃的话，就再看看吃不吃别的。")
		}else if (status == 1){
			//qm.evanTutorial("UI/tutorial/evan/12/0", -1);
			qm.dispose();
			
		}//status
	}//mode
}//function