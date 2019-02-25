/* ===========================================================
			注释(qm.sendSimple\qm.itemQuantity(5420008))
	脚本类型: 		QUEST
	脚本名字:		天上的岛——克里塞
	完成任务函数：qm.forceCompleteQuest();
	接受任务函数：qm.forceStartQuest();
=============================================================
本脚本源自网上流传，仅为技术交流之用。如侵权。请联系我们，我们将在第一时间删除。
*/
var a = -1;

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
				qm.sendNext("怎么现在才来？你不知道我等了多久。")
			}else if (a == 1) {
				qm.sendNextPrev("在天空之城的上空，有一个名叫克里塞的空中之岛。那里生活着体型巨大，但是性格善良的巨人族。\r\n但是从不久前开始，克里塞开始变得越来越远，联络也中断了。\r\n一定是发生了什么事……要是可以的话，我真想马上过去看看。但是你也知道，我无法离开这里。")
			}else if (a == 2) {
					if (mode == 1){
				qm.sendNextPrev("你能帮我去确认一下克里塞到底发生了什么事情吗？\r\n我把你送到克里塞去。回来之后，一定要告诉我发生了什么事情。")
					}else{
				qm.sendNext("你还没做好准备吗？如果你改变了主意，可以再来找我。")	
					}
			}else if (a == 3) { 
			qm.forceCompleteQuest();
				qm.sendYesNo("做好出发的准备了吗？\r\n这将会是一段很长的旅程，你最好做好充分的准备。我马上送你过去。")
			}else if (a == 4) {
				qm.sendNext("很好。我马上送你过去。这将是一段艰苦的旅程，你一定要做好心理准备。")
			}else if (a == 5){
				//qm.YellowMessage("按跳跃键，就可以飞到空中，到达克里塞了。")
				qm.warp(200100001);
				qm.dispose();
		}//status
	}//mode
}//function