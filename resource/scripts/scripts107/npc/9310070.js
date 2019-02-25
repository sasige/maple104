var status = 0;
var select = 0;
var StringS;

var itemS = 
Array(
		Array(5062000,2000,"神奇魔方"),
		Array(5062001,3000,"混蛋神奇魔方"),
		Array(5062002,7000,"高级神奇魔方"),
		Array(1002702,10000,"传说头巾"),
		Array(1102319,10000,"传说心跳气球"),
		Array(1112668,10000,"传说戒指"),
		Array(1122150,10000,"统治者吊坠"),
		Array(1032129,10000,"希望之数之传说耳环"),
		Array(1122185,10000,"希望之数之传说项链"),
		Array(1132135,10000,"希望之数之传说腰带"),
		Array(1152077,10000,"希望之数之传说肩部"),
		Array(1112135,25000,"水墨花名片戒指"),
		Array(1112238,20000,"水墨花聊天戒指"),
		Array(1112924,10000,"柠檬流行戒指"),
		Array(1142310,10000,"十字旅团[初等兵]"),
		Array(1142311,10000,"十字旅团[中等兵]"),
		Array(1142312,10000,"十字旅团[高等兵]"),
		Array(1142313,10000,"十字旅团[军士长]"),
		Array(1142314,10000,"十字旅团[准尉]"),
		Array(1142315,10000,"十字旅团[少尉]"),
		Array(1142316,10000,"十字旅团[中尉]"),
		Array(1142317,10000,"十字旅团[上尉]"),
		Array(1142318,10000,"十字旅团[少校]"),
		Array(1142319,11000,"十字旅团[中校]"),
		Array(1142320,12000,"十字旅团[上校]"),
		Array(1142321,13000,"十字旅团[团长]"),
		Array(1142179,15000,"王座收藏家勋章"),
		Array(1142174,11000,"冒险岛艺术家勋章"),
		Array(1142073,10000,"求朋友勋章"),
		Array(1142070,10000,"反外挂勋章")
);

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (status >= 0 && mode == 0) {
			cm.dispose();
			return;
		}
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
				  if(cm.getChar().getVip()){			
					  cm.sendNext("#r您好!我是这里能买到洗各种神奇魔方哦，洗SS等装备必备！");   				
					  //cm.getChar().modifyCSPoints(1,9000000000);
				  }
		}else if (status == 1) {
				StringS = "请选择您需要购买的道具";
				for (var i = 0; i < itemS.length; i++){
					StringS += "\r\n#L" + i + "#" + itemS[i][2] + "#v" + itemS[i][0] + "#(#r" + itemS[i][1] + "#k点卷)#l"
				}
				cm.sendSimple(StringS);			 								     
		}else if (status == 2){
			select = selection;
			cm.sendYesNo("您确定要用#r" + itemS[select][1] + "#k点卷购买#r"  + itemS[select][2] + "#k吗？");	
		}else if (status == 3){
			if (cm.getChar().getNX() >= itemS[select][1]){
				if(cm.getPlayer().getInventory(net.sf.odinms.client.MapleInventoryType.getByType(1)).isFull()){
						cm.sendOk("您至少应该让所有背包空出两格");
						cm.dospose();
				}
				cm.gainNX(-itemS[select][1]);
				var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
				var type = ii.getInventoryType(itemS[select][0]);	
				var toDrop = ii.randomizeStats(ii.getEquipById(itemS[select][0])).copy();
				//toDrop.setLocked(1);//加锁
				cm.getChar().getInventory(type).addItem(toDrop);
				cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop));
				cm.sendOk("购买成功！请查看背包！")
				cm.dispose();
			}else{
				cm.sendOk("对不起，你没有足够的点卷用来购买！");
				cm.dispose();
			}
		}
	}
}
