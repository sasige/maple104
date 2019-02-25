/* ===========================================================
			注释
	脚本类型: 		QUEST
	脚本名字:		送便当
=============================================================
制作时间：2010年8月5日 21:18:57
制作人员：笔芯
=============================================================
*/

var status = -1;

function start(mode, type, selection) {
    status++;
	if (mode != 1) {
	    if(type == 1 && mode == 0)
		    status -= 2;
		else{
			qm.sendNext("好孩子应该听妈妈的话不是吗？");
			qm.dispose();
			return;
		}
	}
	if (status == 0)
		qm.sendAcceptDecline("去农场干活的时候，#b爸爸#k忘了把便当带过去了。你能去#b#m100030300##k给爸爸#b送便当#k吗？");	
	else if (status == 1){
		qm.forceStartQuest();
		qm.sendNext("不要觉得麻烦就不愿意去。你是个好孩子，对吧？再来和我说话吧。");
		if(!qm.haveItem(4032448))
			qm.gainItem(4032448, 1);
	}else if (status == 3)
		qm.sendNextPrev("去农场干活的时候，#b爸爸#k忘了把便当带过去了。你能去#b#m100030300##k给爸爸#b送便当#k吗？");
		qm.dispose();
	else if (status == 4){
		//qm.evanTutorial("UI/tutorial/evan/5/0" , 1);
		
	}
}

function end(mode, type, selection) {

}
