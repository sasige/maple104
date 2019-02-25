/* ===========================================================
			注释(cm.sendSimple\cm.itemQuantity(5420008))
	脚本类型: 		QUEST
	脚本名字:		
	完成任务函数：qm.forceCompleteQuest();
	接受任务函数：qm.forceStartQuest();
=============================================================
本脚本源自网上流传，仅为技术交流之用。如侵权。请联系我们，我们将在第一时间删除。
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
				qm.sendAcceptDecline("现在你的强大了许多，我有一件事情想找你帮忙，你是否愿意听听？");
			}else if (a == 1) {
				qm.sendNext("故事发生在蘑菇王国，具体的事情我也不太清楚。但是好像很紧急。");
			}else if (a == 2) {
				qm.sendNext("我不知道事情的细节，所以想找你帮帮忙，你可能会节省更多的时间帮助蘑菇王国，我送你一封信，请你把它交给门卫。 \r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#v4032375# #t4032375#");
			}else if (a == 3) {
				qm.forceStartQuest();
				qm.gainItem(4032375, 1);
				qm.sendYesNo("如果你现在想去蘑菇城堡的话，我可以送你去。你确定要去吗？");
			}else if (a == 4) {		
				qm.warp(106020000)
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
				qm.sendNext("嗯？这个从转职教官那里得来的信件吗？");
			}else if (a == 1) {
				qm.sendNextPrev("我看看……。");
			}else if (a == 2) {
				qm.sendNextPrev("好吧，既然你有转职教官的推荐信，我想你是一个很棒的人，很抱歉我没有自我介绍，我是包围蘑菇城堡的卫兵，正如你所看到的，这里是我们暂时的藏身之地，我们的情况很糟糕，尽管如此，欢迎你来到蘑菇王国！");
			}else if (a == 3) {
				qm.gainItem(4032375, -1);
		qm.forceCompleteQuest();
		qm.forceStartQuest(2312);
		qm.dispose();
		}//status
	}//mode
}//function