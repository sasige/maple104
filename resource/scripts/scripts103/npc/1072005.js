importPackage(java.util);
importPackage(net.sf.odinms.client);
importPackage(net.sf.odinms.server);


var status = 0;
var fstype=0;
var price=500000000; //砸卷价格
var types=new Array("┈━═☆装备栏═━┈","┈━═☆消耗栏═━┈","┈━═☆设置栏═━┈","┈━═☆其他栏═━┈","┈━═☆特殊栏═━┈"); 
var chance3 = (Math.floor(Math.random()*8)+1);

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
        if (status == 0) {  
			cm.sendSimple("伟大的#b#h ##k,我的编号是："+cm.getNpc()+"\r\n我是本服唯一的装备强化使者,有什么需要我效劳的么~~#e#k#l\r\n#b#L4#【升级装备砸卷次数→勋章版】#k#l");
        } else if (status == 1) {
            if (selection==0){
				fstype=1;
				cm.sendNext("你目前选择的是#r增加装备砸卷次数#k,这项功能目前需要使用#r1个消费点#k,请问你需要此项服务吗?");
            	}else if (selection==1){
				var a="#r┈━═☆请注意,此清除为不可挽回清除,所以请在清除前把重要东西保存在仓库:#b" 
					for(var i=0;i<types.length;i++){ 
						a+= "\r\n#L" + i + "#" + types[ i ]+""; 
						} 
				cm.sendSimple(a);
				fstype=2; 
		}else if (selection==2){
				fstype=21;
				cm.sendAcceptDecline("你目前选择的是#r增加装备物理攻击力#k,这项功能目前需要手续费用#r1个消费点#k,下面介绍一下条件:\r\n#b1.要升级的装备必须放在第一格\r\n2.升级成功后,你的装备+10攻击力(Weapon Attack),砸卷次数-1,装备已升级次数+1");
            	}else if (selection==3){
				fstype=31;
				cm.sendAcceptDecline("你目前选择的是#r增加装备魔法攻击力#k,这项功能目前需要手续费用#r1个消费点#k,下面介绍一下条件:\r\n#b1.要升级的装备必须放在第一格\r\n2.升级成功后,你的装备+10魔法力,砸卷次数减1,装备已升级次数+1");
            	}else if (selection==4){
				fstype=41;
				cm.sendNext("你目前选择的是#r增加装备砸卷次数#k,这项功能目前需要使用#r1个#v4031160##k,请问你需要此项服务吗?");
		}else if (selection==5){//物理防御力
				fstype=51;
				cm.sendAcceptDecline("你目前选择的是#r增加装备物理防御力#k,这项功能目前需要手续费用#r1个消费点#k,下面介绍一下条件:\r\n#b1.要升级的装备必须放在第一格\r\n2.升级成功后,你的装备+20物理防御力,砸卷次数减1,装备已升级次数+1");
		}else if (selection==6){//魔法防御力
				fstype=61;
				cm.sendAcceptDecline("你目前选择的是#r增加装备魔法防御力#k,这项功能目前需要手续费用#r1个消费点#k,下面介绍一下条件:\r\n#b1.要升级的装备必须放在第一格\r\n2.升级成功后,你的装备+20魔法防御力,砸卷次数减1,装备已升级次数+1");
		}else if (selection==7){
				cm.sendSimple("#r#e┈━═☆请选择你想要发超级喇巴的类型↘\r\n#L0##v5120000##l#L1##v5121002##l#L2##v5121001##l#L3##v5121000#l");
				fstype=71;
		}else if (selection==8){
				fstype=81;
				cm.sendNext("你目前选择的是#r增加装备砸卷次数#k,这项功能目前需要使用#r10W点卷#k,请问你需要此项服务吗?");
}
       } else if (status == 2) {
			if(fstype==21){
				fstype=22;
				var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
				var statup = new java.util.ArrayList();
				cm.sendAcceptDecline("这一步很重要,请确认一下你要升级的装备.\r\n你要砸的装备是：#v"+item.getItemId()+"#\r\n#b\r\n目前装备攻击力:#r"+item.getWatk()+"   #b可升级次数为:#r"+item.getUpgradeSlots()+"#b 已升级次数:#r"+item.getLevel()+"\r\n#b\r\n如果升级成功,那么装备攻击将变为:#r"+(item.getWatk()+10)+"#b,可升级次数变为:#r"+(item.getUpgradeSlots()-1)+"#d\r\n确认完毕后，点击接受开始升级..");
			}
			if(fstype==31){
				fstype=32;
				var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
				var statup = new java.util.ArrayList();
				cm.sendAcceptDecline("这一步很重要,请确认一下你要升级的装备.\r\n你要砸的装备是：#v"+item.getItemId()+"#\r\n#b\r\n目前装备魔法力:#r"+item.getMatk()+"   #b可升级次数为:#r"+item.getUpgradeSlots()+"#b 已升级次数:#r"+item.getLevel()+"\r\n#b\r\n如果升级成功,那么装备攻击将变为:#r"+(item.getMatk()+10)+"#b,可升级次数变为:#r"+(item.getUpgradeSlots()-1)+"#d\r\n确认完毕后，点击接受开始升级..");
			}
			if(fstype==1){
				fstype=3;
				var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
				var statup = new java.util.ArrayList();
				cm.sendNext("请把装装备放在装备窗口的第一格，否则你将不能成功.\r\n请确认一下你要砸的装备是：#v"+item.getItemId()+"#吗？\r\n #rps:如果是那就继续点击下一步程序..");
			}
			if(fstype==41){
				fstype=42;
				var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
				var statup = new java.util.ArrayList();
				cm.sendNext("请把装装备放在装备窗口的第一格，否则你将不能成功.\r\n请确认一下你要砸的装备是：#v"+item.getItemId()+"#吗？\r\n #rps:如果是那就继续点击下一步程序..");
			}
			if (fstype==2){
				cm.deleteItem(selection+1); 
				cm.sendOk("恭喜,已经为你清理完毕!"); 
				cm.dispose(); 
			}
			if(fstype==51){
				fstype=52;
				var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
				var statup = new java.util.ArrayList();
				cm.sendNext("请把装装备放在装备窗口的第一格，否则你将不能成功.\r\n请确认一下你要砸的装备是：#v"+item.getItemId()+"#吗？\r\n #rps:如果是那就继续点击下一步程序..");
			}
			if(fstype==61){
				fstype=62;
				var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
				var statup = new java.util.ArrayList();
				cm.sendNext("请把装装备放在装备窗口的第一格，否则你将不能成功.\r\n请确认一下你要砸的装备是：#v"+item.getItemId()+"#吗？\r\n #rps:如果是那就继续点击下一步程序..");
			}
                        if(fstype==71){
				if(selection==0){
				typedd=5120000;
			}else if(selection==1){
				typedd=5121002;
			}else if(selection==2){
				typedd=5121001;
			}else if(selection==3){
				typedd=5121000;
                        }
                        test=72;
			cm.sendGetText("#r淫荡的一天又开始呢!~~~\r\n#b我是本服#r超级喇巴使者#b,此喇巴非一般之喇巴,发送后全服的人都能以祝福的形式显示,比滚动喇巴更振汗,来一句?\r\n手续费嘛，哼哼,1个消费币1次...");
			} 
			if(fstype==81){
				fstype=82;
				var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
				var statup = new java.util.ArrayList();
				cm.sendNext("请把装装备放在装备窗口的第一格，否则你将不能成功.\r\n请确认一下你要砸的装备是：#v"+item.getItemId()+"#吗？\r\n #rps:如果是那就继续点击下一步程序..");
                    }
		}else if(status == 3){
			if(fstype==32){
				if(cm.getbaby() >= 1){
						var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
						var statup = new java.util.ArrayList();
						item.setUpgradeSlots((item.getUpgradeSlots() - 1));
						item.setMatk((item.getMatk() + 10));
						item.setLevel((item.getLevel() + 1));
                    				MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                    				MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "admin");
						//cm.setbaby(-1);
						//cm.getChar().setbabyLog('zjmfgj=1');
						cm.sendOk("#b恭喜你成功拉!快快看你的包裹吧!#k");
						cm.getChar().saveToDB(true);
						cm.dispose();
				}else{
					cm.sendOk("对不起,你没有1点消费点,请充值消费点后使用此功能!");
					cm.dispose();
				}
			}
			if(fstype==22){
				if(cm.getbaby() >= 1){
					var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
						var statup = new java.util.ArrayList();
						item.setUpgradeSlots((item.getUpgradeSlots() - 1));
						item.setWatk((item.getWatk() + 10));
						item.setLevel((item.getLevel() + 1));
                    				MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                    				MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "admin");
						cm.setbaby(-1);
						cm.getChar().setbabyLog('zjwlgj=1');
						cm.sendOk("#b恭喜你成功拉!快快看你的包裹吧!#k");
						cm.getChar().saveToDB(true);
						cm.dispose();
				}else{
					cm.sendOk("对不起,你没有1点消费点,请充值消费点后使用此功能!");
					cm.dispose();
				}
			}
			if(fstype==3){
				if(cm.getbaby() >= 1){
						var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
						var statup = new java.util.ArrayList();
						item.setUpgradeSlots((item.getUpgradeSlots() + 1));
                    				MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                    				MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "admin");
						cm.setbaby(-1);
						cm.getChar().setbabyLog('sjcs=1');
						cm.sendOk("#b恭喜你成功拉!快快看你的包裹吧!#k");
						cm.getChar().saveToDB(true);
						cm.dispose();
				}else{
					cm.sendOk("对不起,你没有1点消费点,请充值消费点后使用此功能!");
					cm.dispose();
				}
	}
			if(fstype==42){
				if(cm.haveItem(4031160,1)){
					var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
					var statup = new java.util.ArrayList();
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
			if(fstype==52){
				if(cm.getbaby() >= 1){
					var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
						var statup = new java.util.ArrayList();
						item.setUpgradeSlots((item.getUpgradeSlots() - 1));
						item.setWdef((item.getWdef() + 15));//物理防御力
						item.setLevel((item.getLevel() + 1));
                    				MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                    				MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "admin");
						cm.setbaby(-1);
						cm.getChar().setbabyLog('zjwlfy=1');
						cm.sendOk("#b恭喜你成功拉!快快看你的包裹吧!#k");
						cm.getChar().saveToDB(true);
						cm.dispose();
				}else{
					cm.sendOk("对不起,你没有1点消费点,请充值消费点后使用此功能!");
					cm.dispose();
				}
			}
			if(fstype==62){
				if(cm.getbaby() >= 1){
					var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
						var statup = new java.util.ArrayList();
						item.setUpgradeSlots((item.getUpgradeSlots() - 1));
						item.setMdef((item.getMdef() + 15));//魔法防御力
						item.setLevel((item.getLevel() + 1));
                    				MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                    				MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "admin");
						cm.setbaby(-1);
						cm.getChar().setbabyLog('zjwlfy=1');
						cm.sendOk("#b恭喜你成功拉!快快看你的包裹吧!#k");
						cm.getChar().saveToDB(true);
						cm.dispose();
				}else{
					cm.sendOk("对不起,你没有1点消费点,请充值消费点后使用此功能!");
					cm.dispose();
				}
			}
			if(fstype==82) {
				if(cm.getChar().getNX() >= 100000){
					var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
					var statup = new java.util.ArrayList();
					item.setUpgradeSlots((item.getUpgradeSlots() + 1));
					MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
					MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item, "admin");
					cm.gainNX(-100000);
					cm.sendOk("#b恭喜你成功拉!快快看你的包裹吧!#k");
					cm.dispose();
				}else{
					cm.sendOk("对不起,你没有#r10W点卷#k!");
					cm.dispose();
				}	
			}
			if (test == 72) {
			if(cm.getbaby()>=1){
				selected = cm.getText();
				cm.superlaba(selected,typedd);
				cm.setbaby(-1);
				cm.getChar().setbabyLog('cjlb=1');
				cm.dispose();
			}else{
				cm.sendOk("对不起,你目前没有足够的消费币.");
				cm.dispose(); 
				}
			}
        }
    }
}
