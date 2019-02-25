/* ===========================================================
			注释
	脚本类型: 		QUEST
	脚本名字:		历史干预，隐形之手
=============================================================
制作时间：2010年8月7日 10:11:58
制作人员：笔芯
=============================================================
*/
var status = -1;

function start(mode, type, selection) {//接任务
	if (mode == -1) {
		qm.dispose();
	} else {
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
			qm.sendOk("ddddd")
			qm.dispose();
		}
		}//status
	}//mode
}//function