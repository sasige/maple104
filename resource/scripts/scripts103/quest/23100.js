/* ===========================================================
			注释(qm.sendSimple\qm.itemQuantity(5420008))
	脚本类型: 		QUEST
	脚本名字:		
	完成任务函数：qm.forceCompleteQuest();
	接受任务函数：qm.forceStartQuest();
=============================================================
制作时间：
制作人员：笔芯
=============================================================
*/
importPackage(net.sf.odinms.client);


var a = -1;

function end(mode, type, selection) {//接任务
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
				qm.sendOk("嘿！你。你想参加训练吗？")
			}else if (a == 1) {
				qm.sendOk("看到旁边的传送口了吗？进入就可以到达训练场了。")
				qm.forceStartQuest(23103);
				qm.forceCompleteQuest();
		}//status
	}//mode
}//function
