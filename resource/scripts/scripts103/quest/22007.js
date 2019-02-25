/* ===========================================================
			注释
	脚本类型: 		QUEST
	脚本名字:		孵化鸡蛋
=============================================================
制作时间：2010年8月5日 21:40:06
制作人员：笔芯
=============================================================
*/

importPackage(Packages.client);

var status = -1;

function end(mode, type, selection) {
    status++;
	if (mode != 1) {
	    if(type == 1 && mode == 0)
		    status -= 2;
		else{
			qm.sendNext("嗯？奇怪。孵化器没有设置好。重新尝试一下吧。");
		    qm.dispose();
			return;
		}
	}
	if (status == 0)
		qm.sendNext("哦，#t4032451#拿来了吗？快把蛋给我吧。我来帮你把它孵化。");
	if (status == 1)
		qm.sendYesNo("来，拿着。不知道这到底可以用来干什么……\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 360 exp");
	if (status == 2){
		qm.forceCompleteQuest();
		qm.gainExp(360);
		qm.gainItem(4032451, -1);
		//qm.evanTutorial("UI/tutorial/evan/9/0" , 1);
		qm.dispose();
		}
	}
