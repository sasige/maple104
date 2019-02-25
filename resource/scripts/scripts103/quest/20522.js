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
				if (qm.getMeso() >= 10000000){
					if (qm.gainItem(4032117,1)){
				qm.forceStartQuest();
				qm.forceCompleteQuest();
				qm.gainMeso(-10000000)
				qm.sendOk("再和我对话，我会给你了解更多。")
				qm.dispose();
					}
				}else{
				qm.sendOk("接受此任务需要1000万，请检查一下你的金币。")	
				}
				qm.dispose();
		}//status
	}//mode
}//function