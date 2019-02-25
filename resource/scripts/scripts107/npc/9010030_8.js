importPackage(java.util);
importPackage(net.sf.odinms.client);
importPackage(net.sf.odinms.server);


var status = 0;
var fstype=0;
var price=500000000; //砸卷价格
var types=new Array("┈━═☆装备栏═━┈","┈━═☆消耗栏═━┈","┈━═☆设置栏═━┈","┈━═☆其他栏═━┈","┈━═☆特殊栏═━┈"); 
var chance3 = (Math.floor(Math.random()*8)+1);
var itemSet120 = new Array(
	1402046,1432047,1002776,
	1082234,1052155,1072355,
	1382057,1002777,1082235,
	1052156,1072356,1452057,
	1002778,1082236,1052157,
	1072357,1332074,1002779,
	1082237,1052158,
	1072358,1492023,1002780,
	1082238,1052159,1072359
	);
var itemSet140 = new Array(
1322096,1003172,1052314,1082295,1072485,1102275,1302152,1442116,1432086,1003173,1052315,1082296,1072486,1102276,1382104,1372084,1522018,1003174,1052316,1082297,1072487,1102277,1452111,1462099,1003176,1052318,1082299,1072489,1102279,1482084,1492085
	);
var rand120 = Math.floor(Math.random() * itemSet120.length);
var rand140 = Math.floor(Math.random() * itemSet140.length);

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
var nextmap = cm.getC().getChannelServer().getMapFactory().getMap(910000007);
var vip3 = nextmap.getCharacters().toArray().length;
var nextmap1 = cm.getC().getChannelServer().getMapFactory().getMap(910000008);
var vip4 = nextmap1.getCharacters().toArray().length;
var nextmap2 = cm.getC().getChannelServer().getMapFactory().getMap(910000009);
var vip5 = nextmap2.getCharacters().toArray().length;
var nextmap3 = cm.getC().getChannelServer().getMapFactory().getMap(910000010);
var vip6 = nextmap3.getCharacters().toArray().length;
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        if (status == 0) {  
			cm.sendSimple("伟大的#b#h ##k　你现在的会员等级为："+cm.getChar().getVip()+"\r\n#r为了方便会员的装备提升,会员每天可以在我这里领取装备#k\r\n#b(随机装备包含该等级里面套装,武器等.)#k#l\r\n你当日领取140装备使用次数为："+cm.getBossLog('会员140装备')+"次#k\r\n#rVIP4#k每日可以领取1次　#rVIP5#k每日可以领取2次\r\n#L2#每日随即领取一样140装备#l");
        } else if (status == 1) {
            if (selection==0){
		if (cm.getChar().getVip() < 3) {
                    cm.sendOk("你不是会员③,无法领取");
                    cm.dispose();
                }else if(cm.getBossLog("hymrlq") >= 1) {
	            cm.sendOk("你好,会员③每日只能领取随机120装备1次");
                    cm.dispose();
		}else{			
			cm.gainItem(itemSet120[rand120],1)
			cm.setBossLog("hymrlq");
			cm.sendOk("你获得了#v" + itemSet120[rand120] + "##z" + itemSet120[rand120] + "#");
			cm.dispose();
		}
            }else if (selection==1){
		if (cm.getChar().getVip() < 4) {
                    cm.sendOk("你不是会员④,无法领取");
                    cm.dispose();
                }else if(cm.getBossLog("hymrlq") >= 2) {
	            cm.sendOk("你好,会员④每日只能领取随机120装备1次");
                    cm.dispose();
		}else{			
			cm.gainItem(itemSet120[rand120],1)
			cm.setBossLog("hymrlq");
			cm.sendOk("你获得了#v" + itemSet120[rand120] + "##z" + itemSet120[rand120] + "#");
			cm.dispose();
		}
            }else if (selection==2){
		if(cm.getBossLog("会员140装备")>0 && cm.getVip()==4){
			cm.sendOk("你今日的领取次数已经使用!");
                	 cm.dispose();
		}else if(cm.getBossLog("会员140装备")>1 && cm.getVip()==5){
			cm.sendOk("你今日的领取次数已经使用!");
                 	 cm.dispose();
		}else{			
			cm.gainItem(itemSet140[rand140],1)
			cm.setBossLog("会员140装备");
			cm.sendOk("你获得了#v" + itemSet140[rand140] + "##z" + itemSet120[rand140] + "#");
			cm.dispose();
		}
            }else if (selection==3){
		if (cm.getChar().getVip() < 6) {
                    cm.sendOk("你不是会员⑥,无法领取");
                    cm.dispose();
                }else if(cm.getBossLog("hymrlq1") >= 2) {
	            cm.sendOk("你好,会员⑥每日只能领取随机140装备2次");
                    cm.dispose();
		}else{			
			cm.gainItem(itemSet140[rand140],1)
			cm.setBossLog("hymrlq1");
			cm.sendOk("你获得了#v" + itemSet140[rand140] + "##z" + itemSet120[rand140] + "#");
			cm.dispose();
		}
		}
       } else if (status == 2) {
			if(fstype==10){
				fstype=11;
				var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
				//var statup = new java.util.ArrayList();
				cm.sendAcceptDecline("这一步很重要,请确认一下你要升级的装备.\r\n你要砸的装备是：#v"+item.getItemId()+"#\r\n#b确认完毕后，点击接受开始升级..");
			}
			if(fstype==21){
				fstype=22;
				var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
				//var statup = new java.util.ArrayList();
				cm.sendAcceptDecline("这一步很重要,请确认一下你要升级的装备.\r\n你要砸的装备是：#v"+item.getItemId()+"#\r\n#b\确认完毕后，点击接受开始升级..");
			}
			if(fstype==31){
				fstype=32;
				var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
				//var statup = new java.util.ArrayList();
				cm.sendNext("请把装装备放在装备窗口的第一格，否则你将不能成功.\r\n请确认一下你要砸的装备是：#v"+item.getItemId()+"#吗？\r\n #rps:如果是那就继续点击下一步程序..");
			}
		}else if(status == 3){
			if(fstype==11){
				if(cm.getChar().getVip() == 5){
					if(cm.getBossLog('vip5sx') < 1){
						var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
						//var statup = new java.util.ArrayList();
						item.setStr((item.getStr()+50));
						item.setDex((item.getDex()+50));
						item.setInt((item.getInt()+50));
						item.setLuk((item.getLuk()+50));
						item.setHp((item.getHp()+50));
						item.setMp((item.getMp()+50));
						item.setMatk((item.getMatk()+50));
						item.setWatk((item.getWatk()+50));
						MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
						MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "admin");
						cm.setBossLog('vip5sx');
		////cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『会员强化』"+" : "+"『"+ cm.getChar().getName() +"』使用了会员⑤强化装备属性,给装备增加了全属性50~")) 
						cm.sendOk("#b恭喜你成功拉!快快看你的包裹吧!#k");
						cm.getChar().saveToDB(true);
						cm.dispose();
					}else{
						cm.sendOk("对不起,你今天已经使用了此功能!");
						cm.dispose();
					}
				}else{
					cm.sendOk("对不起,你不是VIP5,不能使用此功能!");
					cm.dispose();
				}
			}
			if(fstype==22){
				if(cm.getChar().getVip() == 6){
					if(cm.getBossLog('vip6sx') < 1){
						var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
						//var statup = new java.util.ArrayList();
						item.setStr((item.getStr()+100));
						item.setDex((item.getDex()+100));
						item.setInt((item.getInt()+100));
						item.setLuk((item.getLuk()+100));
						item.setHp((item.getHp()+100));
						item.setMp((item.getMp()+100));
						item.setMatk((item.getMatk()+100));
						item.setWatk((item.getWatk()+100));
						MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
						MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "admin");
						cm.setBossLog('vip6sx');
		////cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『会员强化』"+" : "+"『"+ cm.getChar().getName() +"』使用了会员⑥强化装备属性,给装备增加了全属性100~")) 
						cm.sendOk("#b恭喜你成功拉!快快看你的包裹吧!#k");
						cm.getChar().saveToDB(true);
						cm.dispose();
					}else{
						cm.sendOk("对不起,你今天已经使用了此功能!");
						cm.dispose();
					}
				}else{
					cm.sendOk("对不起,你不是VIP6,不能使用此功能!");
					cm.dispose();
				}
			}
			if(fstype==32){
				if(cm.haveItem(4031160,1)){
					var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
					//var statup = new java.util.ArrayList();
					item.setUpgradeSlots((item.getUpgradeSlots() + 1));
					MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
					MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "admin");
					cm.gainItem(4031160,-1);
					cm.sendOk("#b恭喜你成功拉!快快看你的包裹吧!#k");
					cm.dispose();
				}else{
					cm.sendOk("对不起,你没有#v4031160#,在购买VIP的时候就赠送了这个的!");
					cm.dispose();
			}	
		}
        }
    }
}
