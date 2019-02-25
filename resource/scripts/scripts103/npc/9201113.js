/* ===========================================================
			注释 itemQuantity()
	脚本类型: 		NPC
	所在地图:		扎昆的祭台入口
	脚本名字:		扎昆远征队
=============================================================
本脚本源自网上流传，仅为技术交流之用。如侵权。请联系我们，我们将在第一时间删除。
*/

var a = 0;
var selects = -1;

function start() {
	a = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (mode == 1)
			a++;
		else
			a--;
			if (a == -1){
				cm.dispose();
			}else if (a == 0) {
				cm.sendSimple("通往神圣密室的挑战就要开始了！#b\r\n#L0# 进入远征队。\r\n#L1# 查看关卡流程。")
			}else if (a == 1) {
				if (selection == 0){
				var em = cm.getEventManager("RedQuest");
				 if (em == null) {
					cm.sendOk("配置清单为空，请联系管理员。");
					cm.safeDispose();
					return;
	    		 }
				 var squadAvailability = cm.getSquadAvailability("RedQuest");// 得到扎昆远征队人数
				 if (squadAvailability == -1) {
					//a = 1;
					cm.sendYesNo("现在可以不灭的警戒远征队，你想成为远征队队长吗？");
				 } else if (squadAvailability == 1) {
					var type = cm.isSquadLeader("RedQuest");// 得到扎昆远征队队长
					if (type == -1) {
		    		cm.sendOk("已经结束了申请。");
		    		cm.safeDispose();
					} else if (type == 0) {
			var memberType = cm.isSquadMember("RedQuest"); // 得到扎昆远征队成员数据
		    if (memberType == 2) {
			cm.sendOk("在远征队的制裁名单。");
			cm.safeDispose();
		    } else if (memberType == 1) {
			a = 4;
 			cm.sendSimple("你现在想做什么？\r\n#b#L0#查看远征队成员。#l \r\n#b#L1#加入远征队。#l \r\n#b#L2#退出远征队。#l");
		    } else if (memberType == -1) {
			cm.sendOk("远征队员已经达到30名，请稍后再试。");
			cm.safeDispose();
		    } else {
			a = 4;
			 cm.sendSimple("你现在想做什么？\r\n#b#L0#查看远征队成员。#l \r\n#b#L1#加入远征队。#l \r\n#b#L2#退出远征队。#l");
		    }
					} else { // Is leader
		   	 a = 9;
			 cm.sendSimple("你现在想做什么？\r\n#b#L0#查看远征队伍。#l \r\n#b#L1#制裁远征队伍。#l \r\n#r#L3#进入地图。#l");
					}
			   } else {
				cm.sendOk("任务已经已经开始了，你不能进去。");
				cm.safeDispose();
			   }
				}else if (selection == 1){
					cm.sendOk("关卡流程请详见766论坛或者冒险岛论坛。")
					cm.dispose();
				}//selection
			}else if (a == 2){
				if (mode == 1) {
				cm.registerSquad("RedQuest", 5, " 已经成为了远征队队长。如果你想加入远征队，请重新打开对话申请加入远征队。");
				cm.sendOk("你已经成为了远征队队长。接下来的5分钟，请等待队员们的申请。");
	    		} else {
				cm.sendOk("重新和我对话，如果你想申请加入远征队。")
	    		}
	    		cm.safeDispose();
			}else if (a == 5){
				  if (selection == 0) {
		if (!cm.getSquadList("RedQuest", 0)) {
		    cm.sendOk("由于未知的错误，操作失败。");
		    cm.safeDispose();
		} else {
		    cm.dispose();
		}
	    } else if (selection == 1) { // join
		var ba = cm.addMember("RedQuest", true);
		if (ba == 2) {
		    cm.sendOk("远征队员已经达到30名，请稍后再试。");
		    cm.safeDispose();
		} else if (ba == 1) {
		    cm.sendOk("申请加入远征队成功，请等候队长指示。");
		    cm.safeDispose();
		} else {
		    cm.sendOk("你已经参加了远征队，请等候队长指示。");
		    cm.safeDispose();
		}
	    } else {// withdraw
		var baa = cm.addMember("RedQuest", false);
		if (baa == 1) {
		    cm.sendOk("制裁指定的成员成功。");
		    cm.safeDispose();
		} else {
		    cm.sendOk("你没有参加远征队。");
		    cm.safeDispose();
		}
	    }
			}else if (a == 10){
				 if (selection == 0) {
		if (!cm.getSquadList("RedQuest", 0)) {
		    cm.sendOk("由于未知的错误，操作失败。");
		}
		cm.safeDispose();
	    } else if (selection == 1) {
		a = 10;
		if (!cm.getSquadList("RedQuest", 1)) {
		    cm.sendOk("由于未知的错误，操作失败。");
		}
		cm.safeDispose();
	    } else if (selection == 2) {
		a = 11;
		if (!cm.getSquadList("RedQuest", 2)) {
		    cm.sendOk("由于未知的错误，操作失败。");
		}
		cm.safeDispose();
	    } else if (selection == 3) { // get insode
		if (cm.getSquad("RedQuest") != null) {
		    var dd = cm.getEventManager("RedQuest");
		    dd.startInstance(cm.getSquad("RedQuest"), cm.getMap());
			cm.setBossLog("RedQuest")
		    cm.dispose();
		} else {
		    cm.sendOk("由于未知的错误，操作失败。");
		    cm.safeDispose();
		}
	    }
			}else if (a == 11){
				 cm.banMember("RedQuest", selection);
	   			 cm.dispose();
			}else if (a == 12){
				 if (selection != -1) {
				cm.acceptMember("RedQuest", selection);
	   			 }
	    cm.dispose();
	}//a
}
}