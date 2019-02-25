importPackage(java.util);
importPackage(net.sf.odinms.client);
importPackage(net.sf.odinms.server);


var status = 0;
var fstype=0;
var price=100000000; //砸卷价格
var types=new Array("┈━═☆装备栏^_^","┈━═☆消耗栏^_^","┈━═☆设置栏^_^","┈━═☆杂物栏^_^","┈━═☆现金栏^_^"); 
var chance3 = (Math.floor(Math.random()*15)+1);
var chance30 = (Math.floor(Math.random()*3)+1);


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
            return;s
        }
        if (mode == 1)
            status++;
        if (status == 0) {  
			cm.sendSimple("#r你好，我的朋友，我的ID:"+cm.getNpc()+"\r\n我是本服唯一的装备强化使者,有什么需要我效劳的么~~#b\r\n#L0#┈☆升级装备砸卷次数[使用白狼脚趾][1/10机率成功]#l\r\n#L100#┈☆升级装备砸卷次数[使用3000点券][100%的机率成功]#l\r\n#L1#┈☆清理背包[全部清理谨慎操作]#l\r\n#L4#┈☆清理背包[清理第一格]#l\r\n#L5#┈☆检测背包#l");
        } else if (status == 1) {
            if (selection==0){
				fstype=1;
				cm.sendNext("你目前选择的是#r增加装备砸卷次数#k,这项功能目前需要手续费用1亿冒险币,还有30个狼人脚趾甲,#b注意,还有一定机率失败哦#k\r\n注意：非VIP玩家[含初级会员] 成功机率为1/10\r\n注意：VIP玩家[不含初级会员] 成功机率为1/7");
            }else if (selection==1){
				var a="#r┈━═☆请注意,此清除为不可挽回清除,所以请在清除前把重要东西保存在仓库:#b" 
					for(var i=0;i<types.length;i++){ 
						a+= "\r\n#L" + i + "#" + types[ i ]+""; 
						} 
				cm.sendSimple(a);
                                fstype=2; 
			}else if (selection==100){
				fstype=100;
				cm.sendNext("你目前选择的是#r增加装备砸卷次数#k,由于你是使用元宝升级,所有成功机率为100%成功...\r\n#r注意：请把要升级的装备放在装备栏第一格\r\n#b<点击下一步开始升级>");
			}else if (selection==101){
				if(cm.haveItem(4000054,20)==true){
						cm.gainItem(4000054,-20);
						cm.gainItem(2340000,1);
						cm.dispose();
					}else{
						cm.sendOk("对不起，你没有足够的白狼人脚趾.如果是V3，可以使用冒险币直接购买。");
						cm.dispose();
					}
			}else if (selection==4){
				var String = "请选择下面一个你要清除的栏位，注意，选择后将直接清除该栏位第一格物品。.\r\n\r\n#b";
				cm.sendSimple(String+"#L0#装备栏#l #L1#消耗栏#l #L2#其他栏#l #L3#设置栏#l #L4#特殊栏#l");
				fstype=20;
			}else if (selection==5){
            var equip = cm.getChar().getInventory(MapleInventoryType.EQUIP).getNextFreeSlot();
            var use = cm.getChar().getInventory(MapleInventoryType.USE).getNextFreeSlot();
            var etc = cm.getChar().getInventory(MapleInventoryType.ETC).getNextFreeSlot();
            var setup = cm.getChar().getInventory(MapleInventoryType.SETUP).getNextFreeSlot();
            var cash = cm.getChar().getInventory(MapleInventoryType.CASH).getNextFreeSlot();
            cm.sendOk("#e装备栏共有：#r"+(equip-1)+"#k个物品\r\n消耗栏共有：#r"+(use-1)+"#k个物品\r\n其他栏共有：#r"+(etc-1)+"#k个物品\r\n设置栏共有：#r"+(setup-1)+"#k个物品\r\n特殊栏共有：#r"+(cash-1)+"#k个物品\r\n#b如果返回的是-2，代表你的这个窗口已装满了！");
			}else if (selection==2){
				fstype=55;
				cm.sendAcceptDecline("你目前选择的是#r增加装备物理攻击力#k,这项功能目前需要手续费用一个金袋子(#v5200002#)+44个狼人脚趾甲(#v4000054#)+5个火凤凰的卵#v4001113#,下面介绍一下条件:\r\n#b1.要升级的装备必须放在第一格.\r\n2.升级有一定机率失败,请考虑后在升级.\r\n3.升级成功后,你的装备增加10攻击力,砸卷次数减1.\r\n4.升级失败后装备不消失.\r\n#r注意,如果你是高级VIP等级以上的,成功的机率会相对来说高一些...同时说明一下#v4001113#在各大BOSS身上掉落.");
            }else if (selection==3){
				fstype=66;
				cm.sendAcceptDecline("你目前选择的是#r增加装备魔法攻击力#k,这项功能目前需要手续费用一个金袋子(#v5200002#)+44个狼人脚趾甲(#v4000054#)+5个冰凤凰的卵#v4001114#,下面介绍一下条件:\r\n#b1.要升级的装备必须放在第一格.\r\n2.升级有一定机率失败,请考虑后在升级.\r\n3.升级成功后,你的装备增加10魔法力,砸卷次数减1.\r\n4.升级失败后装备不消失.\r\n#r注意,如果你是高级VIP等级以上的,成功的机率会相对来说高一些...同时说明一下#v4001113#在各大BOSS身上掉落.");
            }
 
       } else if (status == 2) {
			if(fstype==55){
				fstype=56;
				var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
				var statup = new java.util.ArrayList();
				cm.sendAcceptDecline("这一步很重要,请确认一下你要升级的装备.\r\n你要砸的装备是：#v"+item.getItemId()+"#\r\n#b\r\n目前装备攻击力:#r"+item.getWatk()+"   #b可升级次数为:#r"+item.getUpgradeSlots()+"#b 已升级次数:#r"+item.getLevel()+"\r\n#b\r\n如果升级成功,那么装备攻击将变为:#r"+(item.getWatk()+10)+"#b,可升级次数变为:#r"+(item.getUpgradeSlots()-1)+"#d\r\n确认完毕后，点击接受开始升级..");
			}
			if(fstype==100){
				if(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1)==null){
					cm.sendOk("你的装备栏第一格没有装备，不能进行此操作!..");
					cm.dispose();
					return;
				}
                                   if(cm.getChar().getNX() >= 3000) {
					   cm.gainNX(-3000);
						var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
						var statup = new java.util.ArrayList();
						item.setUpgradeSlots((item.getUpgradeSlots() + 1));
                    MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                    MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item);
						cm.sendOk("#b恭喜你成功拉!快快看你的包裹吧!#k");
						cm.dispose();
				}else{
					cm.sendOk("对不起,你没有足够的元宝.");
					cm.dispose();
				}
			}
			if(fstype==20){
                                if (selection==0){
				if(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1)==null){
					cm.sendOk("你的装备栏第一格没有装备，不能进行此操作!..");
					cm.dispose();
					return;
				}
				                fstype=21;
						var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
						var statup = new java.util.ArrayList();
				                cm.sendNext("请把要清除的装备放在装备栏的第一格，否则你将不能成功.\r\n请确认一下你要清除的装备是：#v"+item.getItemId()+"#吗？\r\n #rps:如果是那就继续点击下一步程序..");




                                              
				}else if (selection==1){
				if(cm.getChar().getInventory(MapleInventoryType.USE).getItem(1)==null){
					cm.sendOk("你的消耗栏第一格没有物品，不能进行此操作!..");
					cm.dispose();
					return;
				}

				                fstype=22;
						var item = cm.getChar().getInventory(MapleInventoryType.USE).getItem(1).copy();
						var statup = new java.util.ArrayList();
				                cm.sendNext("请把要清除的物品放在消耗栏的第一格，否则你将不能成功.\r\n请确认一下你要清除的物品是：#v"+item.getItemId()+"#吗？\r\n #rps:如果是那就继续点击下一步程序..");
                                              
				}else if (selection==2){
				if(cm.getChar().getInventory(MapleInventoryType.ETC).getItem(1)==null){
					cm.sendOk("你的其他栏第一格没有物品，不能进行此操作!..");
					cm.dispose();
					return;
				}

				                fstype=23;
						var item = cm.getChar().getInventory(MapleInventoryType.ETC).getItem(1).copy();
						var statup = new java.util.ArrayList();
				                cm.sendNext("请把要清除的物品放在其他栏的第一格，否则你将不能成功.\r\n请确认一下你要清除的物品是：#v"+item.getItemId()+"#吗？\r\n #rps:如果是那就继续点击下一步程序..");
                                              
				}else if (selection==3){
				if(cm.getChar().getInventory(MapleInventoryType.SETUP).getItem(1)==null){
					cm.sendOk("你的设置栏第一格没有物品，不能进行此操作!..");
					cm.dispose();
					return;
				}

				                fstype=24;
						var item = cm.getChar().getInventory(MapleInventoryType.SETUP).getItem(1).copy();
						var statup = new java.util.ArrayList();
				                cm.sendNext("请把要清除的物品放在设置栏的第一格，否则你将不能成功.\r\n请确认一下你要清除的物品是：#v"+item.getItemId()+"#吗？\r\n #rps:如果是那就继续点击下一步程序..");
                                              
				}else if (selection==4){
				if(cm.getChar().getInventory(MapleInventoryType.CASH).getItem(1)==null){
					cm.sendOk("你的特殊栏第一格没有物品，不能进行此操作!..");
					cm.dispose();
					return;
				}

				                fstype=25;
						var item = cm.getChar().getInventory(MapleInventoryType.CASH).getItem(1).copy();
						var statup = new java.util.ArrayList();
				                cm.sendNext("请把要清除的物品放在特殊栏的第一格，否则你将不能成功.\r\n请确认一下你要清除的物品是：#v"+item.getItemId()+"#吗？\r\n #rps:如果是那就继续点击下一步程序..");
                                              
				}
			}
			if(fstype==66){
				fstype=67;
				var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
				var statup = new java.util.ArrayList();
				cm.sendAcceptDecline("这一步很重要,请确认一下你要升级的装备.\r\n你要砸的装备是：#v"+item.getItemId()+"#\r\n#b\r\n目前装备魔法力:#r"+item.getMatk()+"   #b可升级次数为:#r"+item.getUpgradeSlots()+"#b 已升级次数:#r"+item.getLevel()+"\r\n#b\r\n如果升级成功,那么装备攻击将变为:#r"+(item.getMatk()+10)+"#b,可升级次数变为:#r"+(item.getUpgradeSlots()-1)+"#d\r\n确认完毕后，点击接受开始升级..");
			}
			if(fstype==1){
				fstype=3;
				if(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1)==null){
					cm.sendOk("#r对不起，你的第一格没有装备，不能进行砸卷。!.");
					cm.dispose();
					return;
				}
				var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
				//var statup = new java.util.ArrayList();
				cm.sendNext("请把要升级的装备放在装备窗口的第一格，否则你将不能成功.\r\n请确认一下你要砸的装备是：#v"+item.getItemId()+"#吗？\r\n #rps:如果是那就继续点击下一步程序..");
			}
			if (fstype==2){
				cm.deleteItem(selection+1); 
				cm.sendOk("恭喜,已经为你清理完毕!"); 
				cm.dispose(); 
			}
		}else if(status == 3){
			if(fstype==21){//装备物品现金
                                MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
				cm.sendOk("恭喜,已经成功清除此物品!"); 
				cm.dispose(); 
			}
			if(fstype==22){//装备物品现金
                                MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.USE, 1, 9999, true);
				cm.sendOk("恭喜,已经成功清除此物品!"); 
				cm.dispose(); 
			}
			if(fstype==23){//装备物品现金
                                MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.ETC, 1, 9999, true);
				cm.sendOk("恭喜,已经成功清除此物品!"); 
				cm.dispose(); 
			}
			if(fstype==24){//装备物品现金
                                MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.SETUP, 1, 9999, true);
				cm.sendOk("恭喜,已经成功清除此物品!"); 
				cm.dispose(); 
			}
			if(fstype==25){//装备物品现金
                                MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.CASH, 1, 9999, true);
				cm.sendOk("恭喜,已经成功清除此物品!"); 
				cm.dispose(); 
			}
			if(fstype==67){
				if(cm.haveItem(5200002,1) && cm.haveItem(4000054,44) && cm.haveItem(4001114,5)){
						cm.gainItem(5200002,-1);
						cm.gainItem(4000054,-44);
						cm.gainItem(4001114,-5);
					if(chance3!=5){
						cm.sendOk("对不起，很郁闷,升级失败!..");
						cm.dispose();
					}else{
						var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
						var statup = new java.util.ArrayList();
						item.setUpgradeSlots((item.getUpgradeSlots() - 1));
						item.setMatk((item.getMatk() + 10));
						item.setLevel((item.getLevel() + 1));
                    MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                    MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item);
						cm.sendOk("#b恭喜你成功拉!快快看你的包裹吧!#k");
						cm.serverNotice("[NPC][装备升级使者]：恭喜啊，贺喜啊~玩家:"+cm.getChar().getName()+"成功将装备提升一个等级，大家一起祝贺他吧~~");
						cm.dispose();

						}
				}else{
					cm.sendOk("对不起,你没有足够的冒险币,或者是没有足够的白狼人脚趾甲#v4000054#..");
					cm.dispose();
				}
			}
			if(fstype==56){
				if(cm.haveItem(5200002,1) && cm.haveItem(4000054,44) && cm.haveItem(4001113,5)){
						cm.gainItem(5200002,-1);
						cm.gainItem(4000054,-44);
						cm.gainItem(4001113,-5);
					if(chance3!=3){
						cm.sendOk("对不起，很郁闷,升级失败!..");
						cm.dispose();
					}else{
						var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
						var statup = new java.util.ArrayList();
						item.setUpgradeSlots((item.getUpgradeSlots() - 1));
						item.setWatk((item.getWatk() + 10));
						item.setLevel((item.getLevel() + 1));
                    MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                    MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item);
						cm.gainItem(5200002,-1);
						cm.gainItem(4000054,-44);
						cm.gainItem(4001113,-5);
						cm.sendOk("#b恭喜你成功拉!快快看你的包裹吧!#k");
						cm.serverNotice("[NPC][装备升级使者]：恭喜啊，贺喜啊~玩家:"+cm.getChar().getName()+"成功将装备提升一个等级，大家一起祝贺他吧~~");
						cm.dispose();
						}
				}else{
					cm.sendOk("对不起,你没有足够的冒险币,或者是没有足够的白狼人脚趾甲#v4000054#..");
					cm.dispose();
				}
			}
			if(fstype==3){
				if(cm.getMeso()>=price && cm.haveItem(4000054,30)==true){
					if(chance3!=4){
						cm.sendOk("对不起，砸卷失败!..");
						cm.gainMeso(-price);
						cm.gainItem(4000054,-10);
						cm.dispose();
					}else{
						var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
						var statup = new java.util.ArrayList();
						item.setUpgradeSlots((item.getUpgradeSlots() + 1));
                    MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
                    MapleInventoryManipulator.addFromDrop(cm.getChar().getClient(), item);
						cm.gainMeso(-price);
						cm.gainItem(4000054,-10);
						cm.sendOk("#b恭喜你成功拉!快快看你的包裹吧!#k");
						cm.dispose();
						}
				}else{
					cm.sendOk("对不起,你没有足够的冒险币,或者是没有足够的白狼人脚趾甲#v4000054#..");
					cm.dispose();

				}
				}
        }
    }
}