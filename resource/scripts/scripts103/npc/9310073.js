var status = 0;
var select = 0;
var StringS;
var viplevel = 1;  //设置打开此NPC需要VIP等级

var itemS = Array(
			Array(1402037,200000,"龙背刃"),
			Array(1402014,200000,"温度计"),
			Array(3010085,500000,"鬼娃娃椅子"),
			Array(1102245,200000,"太阳披风"),
			Array(1002890,200000,"蓝色丝带"),
			Array(1002888,200000,"红色丝带"),
			Array(1042104,100000,"小绿叶T桖"),
			Array(1061148,100000,"巨星粉色短裙"),
			Array(1302063,200000,"然烧的火焰刀"),
			Array(1442018,100000,"冻冻鱼"),
			//Array(1912036,10000000,"狮子鞍子"),
			//Array(1302130,10000000,"光效单手剑"),
			//Array(1312044,10000000,"光效斧头"),
			//Array(1322070,10000000,"光效钝器"),
			//Array(1432058,10000000,"霸王枪"),
			//Array(1902042,10000000,"超级机器人骑宠"),
			//Array(1912035,10000000,"超级机器人鞍子"),
			//Array(1142190,10000000,"至尊VIP勋章"),
			//Array(1032098,10000000,"至尊VIP特效"),
			//Array(1902044,10000000,"跑车骑宠"),
			//Array(1912037,10000000,"跑车鞍子"),
			//Array(1902046,10000000,"田园跑车骑宠"),
			//Array(1912039,10000000,"田园跑车鞍子"),
			//Array(1902047,10000000,"潜水艇骑宠"),
			//Array(1912040,10000000,"潜水艇鞍子"),
			//Array(1432059,10000000,"发光枪"),
			//Array(1442084,10000000,"发光矛"),
			//Array(1402066,10000000,"龙纹双手剑"),
			//Array(1302131,10000000,"龙剑"),
			//Array(1442083,10000000,"雪之矛"),
			//Array(1302131,10000000,"龙剑"),
			//Array(1372054,10000000,"发光短杖"),
			//Array(1452086,10000000,"光效长弓"),
			Array(1122012,100000,"永恒玉佩"),
			Array(1102172,100000,"永恒不灭披风"),
			Array(1302081,100000,"永恒破甲剑"),
			Array(1312037,100000,"永恒断蚺斧"),
			Array(1322060,100000,"永恒惊破天"),
			Array(1402046,100000,"永恒玄冥剑"),
			Array(1422037,100000,"永恒威震天"),
			Array(1432047,100000,"永恒显圣枪"),
			Array(1442063,100000,"永恒神光戟"),
			Array(1372044,100000,"永恒蝶翼杖"),
			Array(1382057,100000,"永恒冰轮杖"),
			Array(1452057,100000,"永恒惊电弓"),
			Array(1462050,100000,"永恒冥雷弩"),
			Array(1332073,100000,"永恒狂鲨锯"),
			Array(1332074,100000,"永恒断首刃"),
			Array(1472068,100000,"永恒大悲赋"),
			Array(1482023,100000,"永恒孔雀翎"),
			Array(1492023,100000,"永恒凤凰铳"),
			//Array(1472111,2500,"黄金拳套"),
			//Array(1482073,2500,"黄金拳甲"),
			//Array(1492073,2500,"黄金手枪"),
			Array(1332021,130000,"乌龙茶"),
			Array(1322051,200000,"七夕"),
			Array(1402013,110000,"白日剑"),
			Array(1402029,140000,"鬼刺狼牙棒"),
			Array(1402044,280000,"南瓜灯笼"),
			Array(1402063,300000,"樱花伞"),
			Array(1422011,120000,"酒瓶"),
			Array(1432013,100200,"南瓜枪"),
			Array(1082149,100200,"工地手套(褐)"),
			Array(1082148,100200,"工地手套(紫)"),
			Array(1082147,100200,"工地手套(蓝)"),
			Array(1082146,100200,"工地手套(红)"),
			Array(1082145,100200,"工地手套(黄)"),
			Array(1072238,100200,"紫色钉鞋"),
			Array(1072239,100200,"黄色钉鞋"),
			Array(1302024,110200,"废报纸卷"),
			Array(1302027,100200,"绿雨伞"),
			Array(1302087,200000,"火炬"),
			Array(1302025,300000,"红雨伞"),
			Array(1302084,200000,"火柴(红)"),
			Array(1302128,120000,"火柴"),
			Array(1302085,140000,"叉子"),
			Array(1302080,300000,"七彩霓虹灯泡"),
			Array(1302058,100000,"冒险岛伞"),
			Array(1302049,105000,"光线鞭子"),
			Array(1302029,105000,"褐雨伞"),
			Array(1302061,100500,"蔓藤鞭子"),
			Array(1302035,160000,"枫叶旗"),
			Array(1322006,101000,"钢管"),
			Array(1322027,101000,"米伽勒的平底锅"),
			Array(1332032,180000,"圣诞树"),
			Array(1332030,120000,"团扇"),
			Array(1332053,200000,"野外烧烤串"),
			Array(1372017,200000,"领路灯"),
			Array(1372031,300000,"圣贤短杖"),
			Array(1432039,110000,"钓鱼竿"),
			Array(1422036,100000,"玩具匠人的锤子"),
			Array(1092039,100000,"枯叶盾"),
			Array(1102043,100000,"浪人披风(褐)"),
			Array(1102042,100000,"浪人披风(紫)"),
			Array(1102041,100000,"浪人披风(粉)"),
			Array(1102040,100000,"浪人披风(黄)"),
			Array(1051172,100000,"黄色沐浴巾")


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
					  cm.sendNext("#r您好!我是VIP专用玩具NPC,VIP才能购买.");   				
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
