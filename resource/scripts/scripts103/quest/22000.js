/* ===========================================================
			注释
	脚本类型: 		QUEST
	脚本名字:		奇怪的梦
=============================================================
制作时间：2010年8月5日 21:12:58
制作人员：笔芯
=============================================================
*/

importPackage(Packages.client);

var status = -1;

function start(mode, type, selection) {
   if (mode == -1) {
		qm.dispose();
	} else {
		if (mode == 1)
			status++;
		else
			status--;
	if (status == 0){
		qm.sendNext("醒了吗，小不点？");
	}else if (status == 1){
		qm.sendNextPrevS("#b嗯……妈妈也醒了吗？",2);
	}else if (status == 2){
		qm.sendNextPrev("嗯……但是你怎么好像没睡着呢？昨天晚上打了一夜的雷。所以才没睡好吗？");
	}else if (status == 3) {
		qm.sendNextPrevS("#b不，不是因为那个，是因为做了一个奇怪的梦。",2);
	}else if (status == 4){
		qm.sendNextPrev("奇怪的梦？梦见了什么呢？");
	}else if (status == 5){
		qm.sendNextPrevS("#b嗯……#k",2);
	}else if (status == 6){
		qm.sendNextPrevS("#b(说明梦见在迷雾中遇到龙的事情。)",2);
	}else if (status == 7){
		qm.sendYesNo("呵呵呵呵，龙？怎么会梦到这个呢？没被吃掉，真是太好了。你做了个有趣的梦，去告诉#p1013101#吧。他一定会很高兴的。");
	}else if (status == 8){
		qm.forceStartQuest();
		qm.sendNext("#b#p1013101##k去#b#m100030102##k给猎犬喂饭了。从家里出去就能见到他了。");
   }else if (status == 9){
		qm.showWZEffect(true,"UI/tutorial/evan/1/0");
		qm.dispose();
	}
}
}

function end(mode, type, selection) {
    status++;
	if (mode != 1) {
	    if(type == 1 && mode == 0)
		    status -= 2;
		else{
		    qm.dispose();
			return;
		}
	}
	if (status == 0){
		qm.sendNext("哦，起来啦，小不点？大清早的，怎么这么大的黑眼圈啊？晚上没睡好吗？什么？做了奇怪的梦？什么梦啊？嗯？梦见遇到了龙？");
	}else if (status == 1){
		qm.sendNextPrev("哈哈哈哈～龙？不得了。居然梦到了龙！但是梦里有狗吗？哈哈哈哈～\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 20 exp");
	}else if (status == 2){
		qm.gainExp(20);
		//qm.evanTutorial(true,"UI/tutorial/evan/2/0");
		qm.forceCompleteQuest();
		qm.dispose();	
		}
	}
