/* ===========================================================
			注释
	脚本类型: 		QUEST
	脚本名字:		一口肉
=============================================================
制作时间：2010年8月5日 22:31:47
制作人员：笔芯
=============================================================
*/
var status = -1;

function end(mode, type, selection) {//接任务
	if (mode == -1) {
		qm.dispose();
	} else {
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
			qm.sendNext("除了这个，没别的吃的了吗？我不喜欢吃草，我需要营养更高的东西，主人！")
		}else if (status == 1){
			qm.sendNext("#b嗯……不喜欢吃吗？龙说不定会喜欢吃肉食。#t4032453#之类的东西怎么样呢？",2)
		}else if (status == 2){
			qm.sendOk("我不知道#t4032453#是什么东西～但是只要是好吃的东西，不管什么都可以。快给我去找点吃的～除了草！")
		}else if (status == 3){
			qm.sendYesNo("#b#b(那就喂#p1013000#吃#t4032453#吧。只要去农场上捕捉几只#o1210100#就行了。10只应该就够了吧？)")
		}else if (status == 4){
			qm.forceStartQuest();
		    qm.dispose();
		}//status
	}//mode
}//function
