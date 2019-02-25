var status = 0;
var item = 
Array(
			//-----椅子-----
			Array(3010009,1000,1), //榻榻凳
			Array(3010012,1000,1), //剑士 宝座
			Array(3010013,1000,1), //悠长假期
			Array(3010014,1000,1), //月亮弯
			Array(3010019,1000,1), //寿司椅
			Array(3010024,1000,1), //玩具粉熊椅
			Array(3010028,800,1), //海盗的俘虏
			Array(3010029,800,1), //蓝环凳
			Array(3010030,800,1), //黑环凳
			Array(3010031,800,1), //红环凳
			Array(3010032,800,1), //黄环凳
			Array(3010033,800,1), //绿环凳
			Array(3010034,1000,1), //悠长假期(红色)
			Array(3010035,1000,1), //悠长假期(蓝色)
			Array(3010036,1000,1), //浪漫秋千
			Array(3010037,1000,1), //猪猪凳
			Array(3010040,1000,1), //蝙蝠椅
			Array(3010041,1000,1), //骷髅王座
			Array(3010043,1000,1), //魔女的飞扫把
			Array(3010044,1000,1), //同一红伞下
			Array(3010045,1000,1), //寒冰椅子
			Array(3010046,1000,1), //红龙椅
			Array(3010047,1000,1), //蓝龙椅
			Array(3010048,1000,1), //圣诞树椅子
			Array(3010049,1000,1), //雪房子
			Array(3010050,1000,1), //公主凳
			Array(3010051,1000,1), //沙漠兔子1靠垫
			Array(3010052,1000,1), //沙漠兔子2靠垫
			Array(3010054,1000,1), //呼噜呼噜床
			Array(3010057,1000,1), //血色玫瑰
			Array(3010058,1000,1), //世界末日
			Array(3010068,1000,1), //露水椅子
			Array(3010073,500,1), //巨无霸品克缤
			Array(3010071,1000,1), //神兽椅
			Array(3010073,500,1), //PB克缤
			Array(3010077,1000,1), //猫头鹰椅子
			Array(3010098,1000,1), //电视宅人
			Array(3010100,1000,1), //财神椅子
			Array(3010106,1000,1), //雪狼战椅
			Array(3010111,1000,1), //虎虎生威
			Array(3012001,1000,1), //篝火
			Array(3012002,1000,1), //浴桶
			Array(3012003,1000,1), //恶魔椅子
			Array(3012006,1000,1), //风吹稻香
			Array(3012010,1000,1), //巧克力蛋糕恋人
			Array(3012011,1000,1), //巧克力火锅
			Array(3010183,1200,1), //胡萝卜椅子
			Array(3010182,1200,1), //雪狼战椅
			Array(3010111,1200,1), //4只兔子椅子
			Array(3010179,1200,1), //猫咪倒茶椅子
			Array(3010174,1200,1), //炼药椅子
			Array(3010171,1200,1), //猫咪抱抱椅子
			Array(3010154,1200,1), //机械师椅子
			Array(3010146,1200,1), //六周年纪念椅子
			Array(3010131,1200,1), //国宝熊猫椅子
			Array(3010075,1200,1), //我为音乐狂椅子
			Array(3010057,1200,1), //血红玫瑰
			Array(3010045,1200,1), //椅子
			Array(3010025,1200,1), //椅子
			Array(3010007,1200,1), //椅子
			Array(3010017,1200,1), //椅子
			Array(3010069,1200,1)//大黄蜂椅子

);

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (mode == 0) {
			cm.dispose();
			return;
		}
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
			if (cm.haveItem(4031692)) {
				cm.sendYesNo("欢迎参加vip抽奖活动。系统会随机抽取物品,是否继续使用盛大智由拉的爱的见证#v4031692#进行抽奖？\r\n#r[100%中奖率,无欺骗]#k奖品如下：\r\n#v3010012##v3010013##v3010014##v3010019##v3010024##v3010028##v3010029##v3010030##v3010031##v3010032##v3010033##v3010034##v3010035##v3010036##v3010037##v3010040##v3010041##v3010043##v3010044##v3010045##v3010046##v3010047##v3010048##v3010049##v3010050##v3010051##v3010052##v3010054##v3010057##v3010058##v3010068##v3010073##v3010071##v3010073##v3010077##v3010098##v3010100##v3010106##v3010111##v3012001##v3012002##v3012003##v3012006##v3012010##v3012011##v3010183##v3010111##v3010179##v3010174##v3010171##v3010154##v3010146##v3010131##v3010075##v3010057##v3010045##v3010025##v3010017##v3010069##v3010007#");
			} else {
				cm.sendOk("欢迎参加vip抽奖活动。系统会随机抽取物品,是否继续使用盛大智由拉的爱的见证#v4031692#进行抽奖？\r\n#r[100%中奖率,无欺骗]#k奖品如下：\r\n#v3010012##v3010013##v3010014##v3010019##v3010024##v3010028##v3010029##v3010030##v3010031##v3010032##v3010033##v3010034##v3010035##v3010036##v3010037##v3010040##v3010041##v3010043##v3010044##v3010045##v3010046##v3010047##v3010048##v3010049##v3010050##v3010051##v3010052##v3010054##v3010057##v3010058##v3010068##v3010073##v3010071##v3010073##v3010077##v3010098##v3010100##v3010106##v3010111##v3012001##v3012002##v3012003##v3012006##v3012010##v3012011##v3010183##v3010111##v3010179##v3010174##v3010171##v3010154##v3010146##v3010131##v3010075##v3010057##v3010045##v3010025##v3010017##v3010069##v3010007#");
				cm.dispose();
			}
		} else if (status == 1){	
			var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();
			for(var i = 1;i<=5;i++){
				if(cm.getPlayer().getInventory(net.sf.odinms.client.MapleInventoryType.getByType(i)).isFull()){
					cm.sendOk("您至少应该让所有包裹都空出一格");
					cm.dispose();
					return;
				}
			}
			var chance = Math.floor(Math.random()*1000);
			var finalitem = Array();
			for(var i = 0 ;i<item.length;i++){
				if(item[i][1] >= chance){
					finalitem.push(item[i]);
				}
			}
			if(finalitem.length != 0){
				var random = new java.util.Random();
				var finalchance = random.nextInt(finalitem.length);
				var itemId = finalitem[finalchance][0];
				var Laba = finalitem[finalchance][2];
			        if(ii.getInventoryType(itemId).getType() == 1){
			        	var toDrop = ii.randomizeStats(ii.getEquipById(itemId)).copy();
				}
				else{
				 	 var toDrop = new net.sf.odinms.client.Equip(itemId,0).copy();
				}
				net.sf.odinms.server.MapleInventoryManipulator.addFromDrop(cm.getC(), toDrop,-1);
				cm.gainItem(4031692,-1);
				cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null, net.sf.odinms.tools.MaplePacketCreator.getItemMega(cm.getC().getChannel(),cm.getPlayer().getName() + " : " + "从[至尊摇奖机]获得！大家一起恭喜他/她吧！！！",toDrop, true).getBytes());
				
				cm.sendOk("非常感谢参加本次系统活动。多多努力。获取更多的礼物吧！");
				cm.dispose();
			} else {							
				cm.gainItem(4031692,-1);
				cm.sendOk("你人品差什么都没得到!哈哈~");
				cm.dispose();
			}
		}
	}
}
