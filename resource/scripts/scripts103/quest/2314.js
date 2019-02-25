/* ===========================================================
			注释(cm.sendSimple\cm.itemQuantity(5420008))
	脚本类型: 		QUEST
	所在地图:		灯泡触发
	脚本名字:		探索蘑菇城堡
=============================================================
制作时间：2010年8月16日 14:44:03
制作人员：笔芯
=============================================================
*/

importPackage(Packages.client);

var status = -1;

function start(mode, type, selection) {
    status++;
	if (mode != 1) {
	    if(type == 1 && mode == 0)
		    status -= 2;
		else{
			qm.sendNext("请快点帮助我们拯救蘑菇城堡！");
			qm.dispose();
			return;
		}
	}
	if (status == 0)
		qm.sendYesNo("为了拯救公主，你必须先去看一下蘑菇森林，企鹅国王建立强有力的屏障，禁止任何人进入城堡。");
	if (status == 1)
		qm.sendNext("你向东走，站在蘑菇森林的屏障，请小心一点，那里经常会有可怕的怪物出没。");
	if(status == 2){
		qm.forceStartQuest();
		qm.dispose();
	}
}


	
