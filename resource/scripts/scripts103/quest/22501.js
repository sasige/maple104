/* ===========================================================
			注释
	脚本类型: 		QUEST
	脚本名字:		
=============================================================
制作时间：
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
			qm.sendNext("喂，主人。我向你展示了我的能力，现在你也快点向我展示你的能力吧。搜集食物的能力！嗯？为了让我可以发挥力量，你必须照顾好我。")
		}else if (status == 1){
			qm.sendNextPrev("哦……虽然不知道是怎么回事，但我好像必须照顾你才行。好吃的东西？你想吃什么？我不知道龙吃什么东西。",2)
		}else if (status == 2){
			qm.sendNextPrev("我刚出生还没几分钟，怎么可能知道那些事情。我只知道我是龙，你是我的主人，主人必须好好照顾我！")
			}else if (status == 3){
			qm.sendYesNo("看来其他东西得和主人一起慢慢学习才行了～但是比起学习知识，填饱肚子更加重要……主人，快去帮我搜集好吃的！")
			}else if (status == 4){
				qm.forceStartQuest();
qm.PlayerToNpc("#b#b(幼龙 #p1013000#好像肚子很饿。应该给它吃什么呢……不知道该给龙吃什么才好。爸爸说不定会知道。去问问他吧。)")
		}//status
	}//mode
}//function



function end(mode, type, selection) {//完成任务
	if (mode == -1) {
		qm.dispose();
	} else {
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
			
		}//status
	}//mode
}//function
