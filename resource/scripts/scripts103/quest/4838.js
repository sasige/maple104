/* ===========================================================
			注释
	脚本类型: 		QUEST
	脚本名字:		彩虹枫叶兑换任务
=============================================================
制作时间：2010年8月9日 15:25:26
制作人员：笔芯
=============================================================
*/

/*
1112401 - 6周年敏捷戒指 - 为庆祝冒险岛6岁生日而特制的戒指.
1112402 - 6周年智力戒指 - 为庆祝冒险岛6岁生日而特制的戒指.
1112403 - 6周年运气戒指 - 为庆祝冒险岛6岁生日而特制的戒指.
1112400 - 6周年力量戒指 - 为庆祝冒险岛6岁生日而特制的戒指.

2041132 - 6周年戒指卷轴 - 可用于6周年戒指上,使戒指的属性变好或变坏.\n成功率:100%

小于号 <

（与NPC版的结合版）

*/

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
				 qm.sendNext("你知道吗？冒险岛六周年啦，只有你有彩虹枫叶，就能向我领取为庆祝冒险岛六岁生日而特制的戒指。")  
			}else if (a == 1){
				 qm.sendSimple("#h #，在这个特别的日子，你是否很高兴呢？#b\r\n\r\n#L0# 我想要领取6周年特制戒指。\r\n#L1# 我想要领取6周年戒指卷轴。")
			}else if (a == 2){
				 var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();
			for(var i = 1;i<=5;i++){
				if(qm.getPlayer().getInventory(net.sf.odinms.client.MapleInventoryType.getByType(i)).isFull()){
					qm.sendOk("您至少应该让所有包裹都空出一格");
					qm.dispose();
					return;
				}
			}
			 if (selection == 0){//我想领取6周年特制戒指
											  if (qm.itemQuantity(4032733) >=10){//彩虹枫叶
											  var rand = Math.floor(Math.random() * 4);
											if (rand == 1) {
											qm.gainItem(1112441, 1); //  6周年敏捷戒指 - 为庆祝冒险岛6岁生日而特制的戒指.
											} else if (rand == 2) {
											qm.gainItem(1112442, 1); //  6周年智力戒指 - 为庆祝冒险岛6岁生日而特制的戒指.
											} else if (rand == 3) {
											qm.gainItem(1112443, 1); //  6周年运气戒指 - 为庆祝冒险岛6岁生日而特制的戒指.
											} else if (rand == 4) {
											qm.gainItem(1112440, 1); //  6周年力量戒指 - 为庆祝冒险岛6岁生日而特制的戒指.
											}
											qm.sendOk("领取成功，可以再跟我说话，领取六周年戒指卷轴。")
											qm.gainItem(4032733,-10)
											qm.dispose();
											  }else{
												  qm.sendOk("你的彩虹枫叶好像不够吧？你可以打猎怪物获得彩虹枫叶。")
												  qm.dispose();
											  }
											}else if (selection == 1){
												if (qm.itemQuantity(4032733) >=10){//彩虹枫叶
										      qm.sendNext("你已经收集好10个彩虹枫叶了呀，好的一张#t2041132#送给你。")
											  qm.gainItem(2041132,1)
											  qm.gainItem(4032733,-10)
												 qm.dispose();
												}else{
											qm.sendOk("你的彩虹枫叶好像不够吧？你可以打猎怪物获得彩虹枫叶。")
											qm.dispose();
												}
											}//selection
											qm.forceCompleteQuest();
		}//a
	}//mode
}//function
