/* ===========================================================
			注释(cm.sendSimple\cm.itemQuantity(5420008))
	脚本类型: 		QUEST
	脚本名字:		桃子的邀请 1
	完成任务函数：qm.forceCompleteQuest();
	接受任务函数：qm.forceStartQuest();
=============================================================
制作时间：2010年9月18日 16:40:09
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
				qm.sendNext("你好～今天天气很不错哦～")
			}else if (a == 1) {
				if (mode == 1){
				qm.sendNextPrev("我是特别活动的向导，名叫#p9105002#。")
				}else{
					qm.sendNext("哦～很高兴见到你。我会为你准备活动入场券。请你稍等一下再和我说话。..")
					qm.dispose();
				}
			}else if (a == 2) {
				qm.sendYesNo("怎么样？有时间的话，你愿意参加一下活动吗？")
			}else if (a == 3) {
				qm.sendNext("哦～很高兴见到你。我会为你准备活动入场券。请你稍等一下再和我说话。..")
			}else if (a == 4) {
				qm.forceStartQuest();
				qm.dispose();
		}//status
	}//mode
}//function


function end(mode, type, selection) {//完成任务
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
		}//status
	}//mode
}//function
