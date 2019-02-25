/* ===========================================================
			注释(qm.sendSimple\qm.itemQuantity(5420008))
	脚本类型: 		QUEST
	脚本名字:		桃子的邀请 2
	完成任务函数：qm.forceCompleteQuest();
	接受任务函数：qm.forceStartQuest();
=============================================================
制作时间：
制作人员：笔芯
for(var i = 1;i<=5;i++){
				if(qm.getPlayer().getInventory(net.sf.odinms.client.MapleInventoryType.getByType(i)).isFull()){
					qm.startPopMessage(qm.getPlayer().getId(), "背包栏不足，请检查。");
					qm.dispose();
					return;
				}
			}
=============================================================
*/
var a = -1;
// 道具ID,兑换数量，需要物品数量,是否限制时间1为是0为不是，是否是能手册，1为是0为不是
/*
var Items = Array(
Array(2000019,50,100,0,0),//超级药水
Array(2100068,1,300,0,0),//小雪人召唤包
Array(1002788,1,700,0,0),//褐色猫咪耳
Array(1002996,1,900,0,0),//迷你蛋糕帽
Array(1012070,1,1000,1,0),//草莓雪糕，限时
Array(1012161,1,1000,0,0),//发光的鼻子
Array(3010163,1,2000,0,0),//满月椅
Array(3010111,1,3000,0,0),//虎虎生威
Array(1122018,1,3500,0,0),//温暖的围脖
Array(1051151,1,5000,0,0),//樱花粉晚宴裙
Array(3010131,1,6000,0,0),//贪吃熊猫椅
Array(3010132,1,6000,0,0),//撒娇喵咪椅
Array(3010133,1,7000,0,0),//帐篷椅
Array(2290023,1,1000,0,1),//[能手册]恶龙附身
Array(2290041,1,1000,0,1),//[能手册]天降落星
Array(2290049,1,2000,0,1),//[能手册]圣光普照
Array(2290053,1,2000,0,1),//[能手册]火眼晶晶
Array(2290057,1,1500,0,1),//[能手册]神箭手
Array(2290061,1,1500,0,1),//[能手册]暴风箭雨
Array(2290065,1,1500,0,1),//[能手册]集中精力
Array(2290085,1,1500,0,1),//[能手册]三连环光击破
Array(2290125,1,5000,0,1)//[能手册]冒险岛勇士30
);
*/

var Items = Array(
Array(2000019,50,100,0,0),//超级药水
Array(2022245,100,300,0,0),//心跳停止糖
Array(1002788,1,700,0,0),//褐色猫咪耳
Array(1002798,1,900,0,0),//头顶年糕
Array(1012070,1,1000,1,0),//草莓雪糕，限时
Array(1002799,1,1000,0,0),//月桂冠
Array(3010013,1,2000,0,0),//悠长假期
Array(3010025,1,3000,0,0),//枫叶纪念凳
Array(1122018,1,3500,0,0),//温暖的围脖
Array(1051151,1,5000,0,0),//樱花粉晚宴裙
Array(1051152,1,5000,0,1),//[能手册]恶龙附身
Array(1051153,1,5000,0,1)//[能手册]天降落星
);

var seleItem = -1;
var needitem = 4032056;


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
				qm.sendNext("这是进入秘密的活动地图所需的入场券。")
			}else if (a == 1) {
				qm.sendNextPrev("参加的方法很简单！还记得冒险岛各地叫做#b迷你地图#k的地方吗？用我给你的入场券进入那些地图，然后通过传送点可以移动到和平常不同的特殊的迷你地图中。")
			}else if (a == 2) {
				qm.sendNextPrev("啊，地图的风景和以前一样。只不过会发现增加了#b秘密的东西#k。")
			}else if (a == 3) {
				qm.sendNextPrev("#b秘密的东西#k……那就是叫做#b#t4032056##k的水晶球。请你搜集尽可能多的水晶球。我会根据搜集的数量送给你特殊的礼物。")
			}else if (a == 4) {
				qm.sendNextPrev("由于要进行本次活动，所以对迷你地图的设置进行了一下调整，请你参考一下。 1,入场一次只能停留10分钟。过了10分钟之后，自动逐出。 2,每天最多可以进入迷你地图10次。 3,在组队状态下无法入场。 4,在地图内无法使用某些特定技能。")
			}else if (a == 5) {
				qm.sendNextPrev("嗯？搜集#b#t4032056##k可以获得什么？嗯……这次准备的奖励很多噢，我要卖个关子，暂时不告诉你。等你收集够了一定数量，再来找我，就自然知道是什么奖励啦。")
			}else if (a == 6) {
				qm.sendNextPrev("活动有关内容，请查看官方主页的相关活动网页～！")
			}else if (a == 7) {
				qm.sendNextPrev("我们会根据你拥有的#t4032056#的个数而给予最好的礼物，所以在换取礼物之前，请确认一下道具的个数。#t4032056#可以大量持有，希望你能积极参与活动，搜集到可以换取心仪的礼物的足够多的道具。")
			}else if (a == 8) {
				qm.sendNextPrev("好了，你去搜集道具吧～我会在这里等你～")
				qm.forceStartQuest()
				qm.dispose();
		}//status
	}//mode
}//function



function end(mode, type, selection) {//提交任务
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
				var selStr = "你想要哪一个呢？#b";
				for (var i = 0; i < Items.length; i++) {
						if (Items[i][0] == 1012070){
						selStr += "\r\n#L" + i + "##i"+needitem+"# : "+Items[i][2]+" (奖励 : #i"+Items[i][0]+"# - 30天权)#l";
						}else if (Items[i][0] == 2000019){
							selStr += "\r\n#L" + i + "##i"+needitem+"# : "+Items[i][2]+" (奖励 : #i"+Items[i][0]+"# x50)#l";
						}else{
							selStr += "\r\n#L" + i + "##i"+needitem+"# : "+Items[i][2]+" (奖励 : #i"+Items[i][0]+"# )#l";
						}
					}
					qm.sendSimple(selStr);
			}else if (a == 1){
				seleItem = selection;
				if (Items[seleItem][0] == 1012070){
					qm.sendYesNo("用"+Items[seleItem][2]+"个#t"+needitem+"#兑换 #t"+Items[seleItem][0]+"# （30天权） 吗？");
				}else if (Items[seleItem][0] == 2000019){
					qm.sendYesNo("用"+Items[seleItem][2]+"个#t"+needitem+"#兑换 #t"+Items[seleItem][0]+"# x50 吗？");
				}else{
				qm.sendYesNo("用"+Items[seleItem][2]+"个#t"+needitem+"#兑换 #t"+Items[seleItem][0]+"# 吗？");
				}
			}else if (a == 2){
				for(var i = 1;i<=5;i++){
				if(qm.getPlayer().getInventory(net.sf.odinms.client.MapleInventoryType.getByType(i)).isFull()){
					qm.startPopMessage(qm.getPlayer().getId(), "背包栏不足，请检查。");
					qm.dispose();
					return;
				}
			}
				if (qm.itemQuantity(needitem) < Items[seleItem][2]){//如果小于数量
						qm.sendNext("你的#t"+needitem+"#数量不足。")
						qm.dispose();
				}else{
						if (Items[seleItem][0] == 1012070){//如果是草莓雪糕
						qm.gaintimeItem(Items[seleItem][0], 30 * 24 * 60 * 60 * 1000, true)
						}else{
							qm.gainItem(Items[seleItem][0],Items[seleItem][1])
						}
						qm.gainItem(needitem,-Items[seleItem][2]);
						qm.sendOk("兑换成功了，请检查。")
						qm.forceCompleteQuest();
						//qm.serverNotice(0,""+cm.getPlayer.getName()+" 玩家在桃子迷你副本中用"+Items[seleItem][2]+"个枫叶水晶球获得了"+Items[seleItem][1]+"。")
						qm.dispose();
				}
				
		}//status
	}//mode
}//functionion
