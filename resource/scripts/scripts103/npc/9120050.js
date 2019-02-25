/* ===========================================================
			注释 itemQuantity()
	脚本类型: 		NPC
	所在地图:		扎昆的祭台入口
	脚本名字:		扎昆远征队
=============================================================
本脚本源自网上流传，仅为技术交流之用。如侵权。请联系我们，我们将在第一时间删除。
*/

var a = 0;

var books = Array(
Array(2290126,5),
Array(2290127,5),
Array(2290130,5),
Array(2290131,5),
Array(2290132,5),
Array(2290133,5),
Array(2290134,5),
Array(2290135,5),
Array(2290136,5),
Array(2290137,5),
Array(2290196,5),
Array(2290144,5),
Array(2290145,5),
Array(2290018,5),
Array(2290019,5),
Array(2290020,5),
Array(2290021,5),
Array(2290022,5),
Array(2290023,5),
Array(2290204,5),
Array(2290205,5),
Array(2290230,5),
Array(2290231,5),
Array(2290236,5),
Array(2290026,5),
Array(2290027,5),
Array(2290052,5),
Array(2290053,5),
Array(2290056,5),
Array(2290057,5),
Array(2290058,5),
Array(2290059,5),
Array(2290060,5),
Array(2290061,5),
Array(2290092,5),
Array(2290092,5),
Array(2290076,5),
Array(2290077,5),
Array(2290078,5),
Array(2290079,5),
Array(2290048,5),
Array(2290049,5)
);//技能书 暗影双刀 (龙卷风 20 双刀风暴 20)
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
				if (cm.getMapId() == 802000821){
					a = 12;
					cm.sendYesNo("你想离开这里吗？")
				}else{
				cm.sendSimple("挑战欧碧拉就要开始了！#b\r\n#L0# 立即开始挑战 欧碧拉！\r\n#L1# 使用欧碧拉的时沙兑换能手册。")
				}
			}else if (a == 1) {
				if (selection == 0){
				var em = cm.getEventManager("Tokyo3Battle");
				 if (em == null) {
					cm.sendOk("配置清单为空，请联系管理员。");
					cm.safeDispose();
					return;
	    		 }
				 var squadAvailability = cm.getSquadAvailability("TOKYO3");// 得到扎昆远征队人数
				 if (squadAvailability == -1) {
					//a = 1;
					cm.sendYesNo("现在可以申请欧碧拉的远征队，你想成为远征队队长吗？");
				 } else if (squadAvailability == 1) {
					var type = cm.isSquadLeader("TOKYO3");// 得到扎昆远征队队长
					if (type == -1) {
		    		cm.sendOk("已经结束了申请。");
		    		cm.safeDispose();
					} else if (type == 0) {
			var memberType = cm.isSquadMember("TOKYO3"); // 得到扎昆远征队成员数据
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
					var Text = "请选择你要兑换的能手册"
					 var selStr = "你想做什么？#b";
			for (var i = 0; i < books.length; i++) {
						selStr += "\r\n#L" + i + "##z" + books[i][0] + "# 欧碧拉的时沙 "+books[i][1]+"个。)#l";
			}
			a = 13;
			cm.sendSimple(selStr);
				}//selection
			}else if (a == 2){
				if (mode == 1) {
				cm.registerSquad("TOKYO3", 5, " 已经成为了远征队队长。如果你想加入远征队，请重新打开对话申请加入远征队。");
				cm.sendOk("你已经成为了远征队队长。接下来的5分钟，请等待队员们的申请。");
	    		} else {
				cm.sendOk("重新和我对话，如果你想申请加入远征队。")
	    		}
	    		cm.safeDispose();
			}else if (a == 5){
				  if (selection == 0) {
		if (!cm.getSquadList("TOKYO3", 0)) {
		    cm.sendOk("由于未知的错误，操作失败。");
		    cm.safeDispose();
		} else {
		    cm.dispose();
		}
	    } else if (selection == 1) { // join
		var ba = cm.addMember("TOKYO3", true);
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
		var baa = cm.addMember("TOKYO3", false);
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
		if (!cm.getSquadList("TOKYO3", 0)) {
		    cm.sendOk("由于未知的错误，操作失败。");
		}
		cm.safeDispose();
	    } else if (selection == 1) {
		a = 10;
		if (!cm.getSquadList("TOKYO3", 1)) {
		    cm.sendOk("由于未知的错误，操作失败。");
		}
		cm.safeDispose();
	    } else if (selection == 2) {
		a = 11;
		if (!cm.getSquadList("TOKYO3", 2)) {
		    cm.sendOk("由于未知的错误，操作失败。");
		}
		cm.safeDispose();
	    } else if (selection == 3) { // get insode
		if (cm.getSquad("TOKYO3") != null) {
			if (cm.getSquad("TOKYO3").getSquadSize() < 1){
					cm.sendOk("远征队需要3人以上才能进入。")
					cm.dispose();
			}else{
		    var dd = cm.getEventManager("Tokyo3Battle");
		    dd.startInstance(cm.getSquad("TOKYO3"), cm.getMap());
			cm.setBossLog("TOKYO3")
		    cm.dispose();
			}
		} else {
		    cm.sendOk("由于未知的错误，操作失败。");
		    cm.safeDispose();
		}
	    }
			}else if (a == 11){
				 cm.banMember("TOKYO3", selection);
	   			 cm.dispose();
			}else if (a == 12){
				 if (selection != -1) {
				cm.acceptMember("TOKYO3", selection);
	   			 }
	    cm.dispose();
			}else if (a == 13){
				cm.warp(802000822)
				cm.dispose();
			}else if (a == 14){
				selects = selection;
				if (cm.haveItem(4032518,books[selects][1])){
					if(cm.gainItem(books[selects][0],1)){
					cm.gainItem(4032518,-books[selects][1])
					cm.sendOk("兑换成功了。")
					}
				}else{
					cm.sendOk("你没有足够 欧碧拉的时沙，不能兑换能手册。")
				}
				cm.dispose();
	}//a
}
}
