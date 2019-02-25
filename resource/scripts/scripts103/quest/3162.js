/* ===========================================================
			注释(qm.sendSimple\qm.itemQuantity(5420008))
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
				qm.sendNext("你知道狮子王之城的看守阿尼吗？\r\n\r\n据说他经常劫持村民，强迫他们劳动……我们不能这样眼睁睁的看着。\r\n\r\n我收到情报说，看守阿尼会在上午10点到下午10点之间每个整点到城外巡查。\r\n\r\n这件事必须秘密地进行，因此在人数上有一定的限制。大概15人左右就差不多了。")
			}else if (a == 1){
				qm.sendAcceptDecline("怎么样？\r\n\r\n愿意和我一起去除掉阿尼吗？\r\n\r\n#r回来的时候，大家会移动到最近的村庄。希望你能注意。")
			}else if (a == 2){
				if (qm.isPlayerInstance()){
					qm.sendOk("你现在正在执行组队副本，不能进入接受到邀请函。")
					qm.dispose();
				}else{
					var nextmap = qm.getMap(921130000);	
					var nextpe = qm.getMap(921130000).getCharactersSize();
					if (nextpe >= 15){
						qm.sendOk("对不起，挑战的人数已经超过15人。请1小时后或者明天再来。")
					}else{
						var em = qm.getEventManager("LionIn");
						if (em == null) {
							qm.sendOk("暂时还没有开放此副本。")
							qm.dispose();
						}else{
							em.newInstance("LionIn").registerPlayer(qm.getChar());	
						}
					}
					qm.dispose();
				}
		}//status
	}//mode
}//function