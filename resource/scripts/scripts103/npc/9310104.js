var status = 0;
var select = 0;
var StringS;
var viplevel = 6;  //设置打开此NPC需要VIP等级

var itemS = Array(
			Array(1332021,10000,"神秘的礼物"),
			Array(1082145,10000,"神秘的礼物"),
			Array(1072238,10000,"神秘的礼物"),
			Array(1072239,10000,"神秘的礼物"),
			Array(1302063,10000,"神秘的礼物"),
			Array(1702334,10000,"神秘的礼物"),
			Array(1702330,10000,"神秘的礼物"),
			Array(1492120,10000,"神秘的礼物"),
			Array(1462137,10000,"神秘的礼物"),
			Array(1442155,10000,"神秘的礼物"),
			Array(1382143,10000,"神秘的礼物"),
			Array(1442018,10000,"神秘的礼物"),
			Array(1122012,10000,"神秘的礼物"),
			Array(1102172,10000,"神秘的礼物"),
			Array(3010289,10000,"神秘的礼物"),
			Array(1302029,10000,"神秘的礼物"),
			Array(1302061,10000,"神秘的礼物"),
			Array(1302035,10000,"神秘的礼物"),
			Array(1322006,10000,"神秘的礼物"),
			Array(1322027,10000,"神秘的礼物"),
			Array(1332032,10000,"神秘的礼物"),
			Array(1332030,10000,"神秘的礼物"),
			Array(1332053,10000,"神秘的礼物"),
			Array(1372017,10000,"神秘的礼物"),
			Array(1372031,10000,"神秘的礼物"),
			Array(3010279,10000,"神秘的礼物"),
			Array(3010302,10000,"神秘的礼物"),
			Array(1402014,10000,"神秘的礼物"),
			Array(1432039,10000,"神秘的礼物"),
			Array(1422036,10000,"神秘的礼物"),
			Array(3010081,10000,"神秘的礼物"),
			Array(3010123,10000,"神秘的礼物"),
			Array(1112683,10000,"神秘的礼物"),
			Array(1302081,10000,"神秘的礼物"),
			Array(1312037,10000,"神秘的礼物"),
			Array(1322060,10000,"神秘的礼物"),
			Array(1402046,10000,"神秘的礼物"),
			Array(1402037,10000,"神秘的礼物"),
			Array(1402014,10000,"神秘的礼物"),
			Array(1492023,10000,"神秘的礼物"),
			Array(1302024,10000,"神秘的礼物"),
			Array(1322051,10000,"神秘的礼物"),
			Array(1402013,10000,"神秘的礼物"),
			Array(1402029,10000,"神秘的礼物"),
			Array(3010305,10000,"神秘的礼物"),
			Array(3010183,10000,"神秘的礼物"),
			Array(3013002,10000,"神秘的礼物"),
			Array(1402044,10000,"神秘的礼物"),
			Array(1402063,10000,"神秘的礼物"),
			Array(1422011,10000,"神秘的礼物"),
			Array(1432013,10000,"神秘的礼物"),
			Array(1082149,10000,"神秘的礼物"),
			Array(1082148,10000,"神秘的礼物"),
			Array(1082147,10000,"神秘的礼物"),
			Array(1702335,10000,"神秘的礼物"),
			Array(1372118,10000,"神秘的礼物"),
			Array(1322102,10000,"神秘的礼物"),
			Array(1442117,10000,"神秘的礼物"),
			Array(1082146,10000,"神秘的礼物"),
			Array(1302027,10000,"神秘的礼物"),
			Array(1302087,10000,"神秘的礼物"),
			Array(1302025,10000,"神秘的礼物"),
			Array(1302084,10000,"神秘的礼物"),
			Array(1702346,10000,"神秘的礼物"),
			Array(1412056,10000,"神秘的礼物"),
			Array(1432102,10000,"神秘的礼物"),
			Array(1302128,10000,"神秘的礼物"),
			Array(1302085,10000,"神秘的礼物"),
			Array(1302080,10000,"神秘的礼物"),
			Array(3010357,10000,"神秘的礼物"),
			Array(1003227,10000,"神秘的礼物"),
			Array(1422037,10000,"神秘的礼物"),
			Array(1432047,10000,"神秘的礼物"),
			Array(1442063,10000,"神秘的礼物"),
			Array(1372044,10000,"神秘的礼物"),
			Array(3010300,10000,"神秘的礼物"),
			Array(3010290,10000,"神秘的礼物"),
			Array(1382057,10000,"神秘的礼物"),
			Array(1452057,10000,"神秘的礼物"),
			Array(1462050,10000,"神秘的礼物"),
			Array(1332073,10000,"神秘的礼物"),
			Array(1332074,10000,"神秘的礼物"),
			Array(1472068,10000,"神秘的礼物"),
			Array(1482023,10000,"神秘的礼物"),
			Array(1003151,10000,"神秘的礼物"),
			Array(1702337,10000,"神秘的礼物"),
			Array(1702342,10000,"神秘的礼物"),
			Array(3010356,10000,"神秘的礼物"),
			Array(1302058,10000,"神秘的礼物"),
			Array(1302049,10000,"神秘的礼物"),
			Array(3010320,10000,"神秘的礼物"),
			Array(1092039,10000,"神秘的礼物"),
			Array(3010053,10000,"神秘的礼物"),
			Array(3013000,10000,"神秘的礼物"),
			Array(1000043,10000,"神秘的礼物"),
			Array(1000042,10000,"神秘的礼物"),
			Array(1000041,10000,"神秘的礼物"),
			Array(1000040,10000,"神秘的礼物"),
			Array(1051172,10000,"神秘的礼物")


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
					  cm.sendNext("\r\n#r您好!我是Vip6刮刮乐超级福利NPC.\r\n#n#d剩余:#r"+player.GetMoney()+"元宝 \r\n#d10000元宝一次的疯狂刮奖,道具奖励无所能及\r\n\r\n#v1051172##v1000040##v1000042##v1000043##v1302080##v1402037##v1112663##v1112683##v3010226#r\n#v4032682##v4032682##v4032682##v4032682##v4032682##v4032682##v4032682##v4032682##v4032682##v4032682#\r\n#v4032682##v4032682##v4032682##v4032682##v4032682##v4032682##v4032682##v4032682##v4032682##v4032682#\r\n\r\n                            #b更多丰厚奖励等您来开启...");   				
					  //cm.getChar().modifyCSPoints(1,9000000000);
				  }else{
					cm.sendOk("您等是vip6,再来vip6地图来找我吧..");
					cm.dispose();
				  }
		}else if (status == 1) {
				StringS = "请选择您需要购买的道具";
				for (var i = 0; i < itemS.length; i++){
					StringS += "\r\n#L" + i + "#" + itemS[i][2] + "#v4032682#(#r" + itemS[i][1] + "#k元宝)#l"
				}
				cm.getChar().GetMoney();
				cm.sendSimple(StringS);			 								     
		}else if (status == 2){
			select = selection;
			cm.sendYesNo("您确定要用#r" + itemS[select][1] + "#k元宝购买#r"  + itemS[select][2] + "#k吗？");	
		}else if (status == 3){
			if (cm.getChar().GetMoney() >= itemS[select][1]){
				if(cm.getPlayer().getInventory(net.sf.odinms.client.MapleInventoryType.getByType(1)).isFull()){
						cm.sendOk("您至少应该让所有背包空出两格");
						cm.dospose();
				}
				player.GainMoney(-itemS[select][1]);
				var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
				var type = ii.getInventoryType(itemS[select][0]);	
				var toDrop = ii.randomizeStats(ii.getEquipById(itemS[select][0])).copy();
				//toDrop.setLocked(1);//加锁
				cm.getChar().getInventory(type).addItem(toDrop);
				cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop));
				cm.sendOk("购买成功！请查看背包！")
cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『"+ cm.getChar().getName() +"』Vip⑥"+" : "+"在至尊vip6地图使用超级刮奖NPC获得<超级奖励>"))
				cm.dispose();
			}else{
				cm.sendOk("对不起，你没有足够的元宝用来购买！");
				cm.dispose();
			}
		}
	}
}
