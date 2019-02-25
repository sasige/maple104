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
var items;

function start(mode, type, selection) {//接任务
	if (mode == -1) {
		qm.safeDispose();
	} else {
		if (mode == 1)
			a++;
		else
			a--;
			if(a == -1){
				qm.safeDispose();
			}else if (a == 0) {
				qm.sendNext("冒险岛进入了混沌时代。真正的勇士必须在混沌中刻苦磨练，让自己绽放出光彩。#b#h0##k，我可以给你一个机会，让你去证明自己是不是真正的勇士。如果你能证明自己，我就送你一件和勇士相配的礼物。这是证明自己的机会，你想挑战一下吗？")
			}else if (a == 1) {
				// -- 龙神部分 --
				if (qm.getJob() >= 2001 && qm.getJob() <= 2218){
					items = 1022117;
				}
				
				// -- 冒险家部分 --
				if(qm.getJob() <= 522){
					items = 0;
				}
				
				// -- 反抗者部分 --
				if(qm.getJob() >= 3000 && qm.getJob() <= 3512){
					items = 1112591;
				}
				
				// -- 战神部分 -- 
            	if(qm.getJob() >= 2000 && qm.getJob() <= 2112){
					items = 1112592;
				}
				
				// -- 骑士团部分 -- 
				if(qm.getJob() >= 1000 && qm.getJob() <= 1510){
					items = 1032095;
				}
				
				if (items == 0){
					qm.sendNext("#b在2011年8月10日#k之前达到#b130#k级，就可以获得稀有的物品。希望你能挑战成功。\r\n\r\n#i1112427# #z1112427#\r\n\r\n#i1112428##z1112428#\r\n\r\n#i1112429##z1112429#\r\n\r\n四个其中一个。")
					qm.forceStartQuest();
				qm.safeDispose();
				}else if (items == null){
					qm.sendNext("未知的职业（"+qm.getJob()+"），请联系管理员。")
					qm.dispose();
				}else{
				qm.sendNext("#b在2011年8月10日#k之前达到#b130#k级，就可以获得稀有的物品。希望你能挑战成功。\r\n#i"+items+"##z"+items+"#")
				qm.MissionMake(qm.getPlayer().getId(), qm.getPlayerStat("LVL"), 0, 0, 0, 0)//给予人物等级的高级任务
				qm.forceStartQuest();
				qm.safeDispose();
				}
				
		}//status
	}//mode
}//function


function end(mode, type, selection) {//完成任务
	if (mode == -1) {
		qm.safeDispose();
	} else {
		if (mode == 1)
			a++;
		else
			a--;
			if(a == -1){
				qm.safeDispose();
			}else if (a == 0) {
				qm.sendNext("这么快就挑战成功了吗？让我来看看。")
			}else if (a == 1) {
				if (qm.MissionStatus(qm.getPlayer().getId(),qm.getPlayerStat("LVL"), 0, 4) && qm.getPlayerStat("LVL") >= 130){//查看如果等级不同，那就是升级了，然后再判断一下等级
				// -- 龙神部分 --
				if (qm.getJob() >= 2001 && qm.getJob() <= 2218){
					items = 1022117;
				}
				
				// -- 冒险家部分 --
				if(qm.getJob() <= 522){
					items = 0;
				}
				
				// -- 反抗者部分 --
				if(qm.getJob() >= 3000 && qm.getJob() <= 3512){
					items = 1112591;
				}
				
				// -- 战神部分 -- 
            	if(qm.getJob() >= 2000 && qm.getJob() <= 2112){
					items = 1112592;
				}
				
				// -- 骑士团部分 -- 
				if(qm.getJob() >= 1000 && qm.getJob() <= 1510){
					items = 1032095;
				}
				if (items == 0){
				qm.sendSimple("你想要哪一种奖励？#b\r\n#L0# #z1112427#\r\n#L1# #z1112428#\r\n#L2# #z1112429#")	
				}else{
					a = 2;
				qm.sendNext("恭喜你获得了#z"+items+"#一个，点击下一项领取。")
				}
				}else{
				qm.sendOk("你好像还没有达到目标等级。#b#h0##k，你的目标等级是#b130#k级。希望你继续努力。")	
				qm.dispose();
				}
			}else if (a == 2){
				if (qm.getSpace(1)  >= 1){//判断武器栏是不是大于1
				if (selection == 0){
					qm.gainItem(1112427,1)
				}else if (selection == 1){
					qm.gainitem(1112428,1)
				}else if (selection == 2){
					qm.gainItem(1112429,1)
				}
				qm.forceCompleteQuest()
				qm.sendOk("送给了你作为勇士的礼物，好好收下~")
				qm.dispose();
				}else{
				qm.sendOk("对不起，你的装备栏已满，请腾出一格。")
				qm.dispose();
				}
			}else if (a == 3){
				if (qm.getSpace(1)  >= 1){//判断武器栏是不是大于1
				qm.gainItem(items,1);
				qm.forceCompleteQuest();
				qm.sendOk("送给了你作为勇士的礼物，好好收下~")
				qm.dispose();
				}else{
				qm.sendOk("对不起，你的装备栏已满，请腾出一格。")
				qm.dispose();	
				}
		}//status
	}//mode
}//function