/* ===========================================================
			注释(cm.sendSimple\cm.itemQuantity(5420008))
	脚本类型: 		QUEST
	脚本名字:		盛大11周年运营员的祝贺
	完成任务函数：qm.forceCompleteQuest();
	接受任务函数：qm.forceStartQuest();
=============================================================
制作时间：2010年9月12日 15:42:52
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
				qm.sendYesNo("你好～在冒险岛玩得愉快吗？这次盛大迎来了11周年。我为大家准备了纪念增益。我需要10个#t3991052#、10个#t3991053#、10个#t3991054#和10个#t3991054#！你想获得我的礼物吗？")
			}else if (a == 1) {
				qm.sendOk("请收下礼物。25分钟之后如果你还有纪念字母，我会再过来～");
				qm.forceCompleteQuest();
				qm.giveNPCBuff(qm.getPlayer(),2028021);//盛大11周年纪念增益1
				qm.getPlayer().dropMessage(5, "为了纪念盛大11周年，佳佳施加的增益效果。在20分钟内掉落率提高为1.5倍。");
				qm.dispose();
		}//status
	}//mode
}//function
