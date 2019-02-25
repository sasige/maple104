var status = 0;
var select = 0;
var StringS;
var viplevel = 0;  //设置打开此NPC需要VIP等级

var itemS = Array(
			Array(2340000,1500,"祝福卷"),
			Array(1002702,100000,"传说头巾"),
			Array(1102319,100000,"传说心跳气球"),
			Array(1112668,100000,"传说戒指"),
			Array(1122150,100000,"统治者吊坠"),
			Array(1032129,100000,"希望之数之传说耳环"),
			Array(1122185,100000,"希望之数之传说项链"),
			Array(1132135,100000,"希望之数之传说腰带"),
			Array(1152077,100000,"希望之数之传说肩部"),
			Array(1112135,250000,"水墨花名片戒指"),
			Array(1112238,200000,"水墨花聊天戒指"),
			Array(1112924,100000,"柠檬流行戒指"),
			Array(1142310,100000,"十字旅团[初等兵]"),
			Array(1142311,100000,"十字旅团[中等兵]"),
			Array(1142312,100000,"十字旅团[高等兵]"),
			Array(1142313,100000,"十字旅团[军士长]"),
			Array(1142314,100000,"十字旅团[准尉]"),
			Array(1142315,100000,"十字旅团[少尉]"),
			Array(1142316,100000,"十字旅团[中尉]"),
			Array(1142317,100000,"十字旅团[上尉]"),
			Array(1142318,100000,"十字旅团[少校]"),
			Array(1142319,110000,"十字旅团[中校]"),
			Array(1142320,120000,"十字旅团[上校]"),
			Array(1142321,130000,"十字旅团[团长]"),
			Array(1142179,150000,"王座收藏家勋章"),
			Array(1142174,110000,"冒险岛艺术家勋章"),
			Array(1142073,100000,"求朋友勋章"),
			Array(1142070,100000,"反外挂勋章")
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
				  if(cm.getChar().getVip() >= viplevel){			
					  cm.sendNext("#r您好!我是这里能买到稀有玩具,装备等等哦！");   				
					  //cm.getChar().modifyCSPoints(1,9000000000);
				  }else{
					cm.sendOk("您的VIP等级无权使用此NPC! 请购买VIP后再来!东西超实惠哦");
					cm.dispose();
				  }
		}else if (status == 1) {
				StringS = "请选择您需要购买的道具";
				for (var i = 0; i < itemS.length; i++){
					StringS += "\r\n#L" + i + "#" + itemS[i][2] + "#v" + itemS[i][0] + "#(#r" + itemS[i][1] + "#k点卷)#l"
				}
				cm.getChar().getNX();
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
