/* ===========================================================
			注释(cm.sendSimple\cm.itemQuantity(5420008))
	脚本类型: 		QUEST
	脚本名字:		邪魔斯的觉醒
	完成任务函数：qm.forceCompleteQuest();
	接受任务函数：qm.forceStartQuest();
=============================================================
制作时间：2010年9月19日 21:45:17
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
				if (mode == 1){
				qm.sendNext("喂，你是#b#h0##k 吧？呵呵呵，一定想知道我为什么叫你来吧？我叫#b邪摩斯#k。虽然我现在被囚禁在这里，但是我有件重要的事情想拜托你，所以才会叫你过来。对于我，你现在一定有很多疑问吧？呵呵呵。")
				}else{
					qm.sendOk("竟然拒绝我的提议……你一定会后悔的。")
					qm.dispose();
				}
			}else if (a == 1) {
				qm.sendYesNo("你也看到了，我不是人类，而是侏儒怪。是的，大家都这么叫我。但是我到底是谁？为什么会被关在这里？为什么我记不起小时候的事情？真让人郁闷……你得帮帮我。你能帮帮我这个不幸的人吗？")
			}else if (a == 2) {
				qm.sendNext("马上到我这里来。不知你知不知道，我就在#b冰峰雪域长老公馆地下#k。");
				qm.forceStartQuest()
				qm.dispose();
		}//status
	}//mode
}//function
