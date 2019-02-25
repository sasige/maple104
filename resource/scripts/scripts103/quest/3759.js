/* ===========================================================
			注释(cm.sendSimple\cm.itemQuantity(5420008))
	脚本类型: 		QUEST
	脚本名字:		向着天空 2
	完成任务函数：  qm.forceCompleteQuest();
	接受任务函数：  qm.forceStartQuest();
=============================================================
制作时间：2010年9月1日 15:09:57
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
				qm.sendNext("哦，终于搜集到啦。请稍等一下。我马上为你制作秘药。..");
			}else if (a == 1) {
				qm.sendNextPrev("好的，准备好了吗？准备好了的话，我马上为你制作秘药，并洒在你的身上。那样的话，你就能在天空中飞行了。")
			}else if (a == 2) {
				if(cm.getJob().getId() >= 1000 && cm.getJob().getId() <= 1510){//如果是骑士团
				cm.teachSkill(10001026,1,1);
				return;
				}
				if(cm.getJob().getId() >= 2000){//如果是战神
				cm.teachSkill(20001026,1,1);
                return;
            	}
				if(cm.getJob().getId() <= 600){//如果是冒险家
				cm.teachSkill(0001026,1,1);
                return;
            	}
				if(cm.getJob().getId() >= 2200){//如果是龙神
				cm.teachSkill(20011026,1,1);
                return;
            	}
				qm.sendNextPrev("好了，飞翔技能已经准备好了。有一点需要注意，只有在有龙族气息的地方才能使用飞翔技能。从天渡开始，应该就可以飞行了。")
			}else if (a == 3) {
				qm.sendNextPrev("此外，使用飞翔技能时，#b会持续消耗魔力#k，因此必须留意魔力的控制。不小心从天上掉下来的话，可能会受到#b比平时高很多的坠落伤害#k。搞不好，可能连命都丢了。祝你好运。")
				qm.dispose();
		}//status
	}//mode
}//function
