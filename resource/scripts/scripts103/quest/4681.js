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
				if (qm.getQuestCustomData() != null) { 
					a = 3;
					qm.sendYesNo("你带来 #b#t5252002##k 了吗？它在一个现金商城的东西可以买到。")
				}else{
				qm.sendNext("你好，冒险家。我是阿西亚，我是卡姆拉的守护者。")
				}
			}else if (a == 1) {
				qm.sendNext("你想拯救未来之城吗？")
			}else if (a == 2) {
				qm.setQuestCustomData("readHistory");
				qm.sendOk("只要你带来 #b#t5252002## 我就可以让你进入。")
			}else if (a == 3) {
				qm.sendYesNo("你带来 #b#t5252002##k 了吗？它在一个现金商城的东西可以买到。")
			}else if (a == 4) {
				if (qm.haveItem(5252002)){
					qm.gainItem(5252002, -1);
					qm.sendOk("我希望你能拯救逆奥之城!")
					qm.forceStartQuest();
	    			qm.dispose();
				}else{
					qm.sendOk("你没有逆奥之流，我不能让你进入逆奥之城里面。")
					qm.dispose();
				}
					
		}//status
	}//mode
}//function