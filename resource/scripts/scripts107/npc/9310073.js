var status = 0;
var fstype=0;
var price=100000; //砸卷价格
var types=new Array("装备栏","消耗栏","任务栏","杂物栏","现金栏"); 
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
			cm.sendSimple("你好伟大的#b#h ##k，我是本服管理装备的NPC，你通过任务，副本，打怪获得特殊物品，都可以在我这里来提升一下装备哟，#r需要注意一点是，清理的装备是不能找回的，为避免给你带来不必要的麻烦，所以请谨慎使用#k\r\n#L1#<慎重>清空背包垃圾(选择性删除)#l\r\n#L6##b<适合普通玩家>BOSS掉落装备合成星级装备#l\r\n#L0##b<适合普通玩家>#z4310034#增加装备砸卷次数#l\r\n#L2#<适合普通玩家>黄金枫叶增加装备砸卷次数#l\r\n#L5##b<适合普通玩家>#z4000054#增加装备全属性(10~30)#l\r\n#L3##b<适合普通玩家>#z4032733#随即增加装备全属性(10~30)#l\r\n#L4##r<适合人民币玩家>消费币随即增加装备全属性(20~50)#l");
        } else if (status == 1) {
            if (selection==0){//正义币
				fstype=1;
				cm.sendNext("你目前选择的是#r增加装备砸卷次数#k\r\n这项功能目前需要手续费用1E冒险币+2个正义币\r\n#r注意：50%机率成功#k,当然失败会返回你一半的手续费费用\r\n#r提示#k：#b#z4310034##k可通过市场#g会计小姐#k正义之章任务获得");
            }else if (selection==2){//黄金枫叶
				fstype=3;
				cm.sendNext("你目前选择的是#r增加装备砸卷次数#k\r\n这项功能目前需要手续费用1E冒险币+1个黄金枫叶\r\n#r注意：50%机率成功#k,当然失败会返回你一半的手续费费用\r\n#r提示#k：#b黄金枫叶#k可通过#g超级转生#k获得,每转生一次可获得1个黄金枫叶");
            }else if (selection==3){//彩色枫叶
				fstype=4;
				cm.sendNext("你目前选择的是#r随即增加装备全属性(10~20)#k\r\n这项功能目前需要手续费用1E冒险币+50个彩色枫叶\r\n#b注意：50%机率成功#k,当然失败会返回你一半的手续费费用\r\n什么是随即增加全属性呢？就是选择要加的装备后，会随即给这装备加全属性随机一项(10~20)，全凭人品哟\r\n#r提示#k：#b彩色枫叶#k可通过击杀#g怪物#k掉落");
            }else if (selection==4){//消费币
				fstype=5;
				cm.sendNext("你目前选择的是#r随即增加装备全属性(20~50)#k\r\n这项功能目前需要手续费用2000消费币\r\n#b注意,这是100%机率成功哟#k,当然就不用担心失败啦~\r\n什么是随即增加全属性呢？就是选择要加的装备后，会随即给这装备加全属性随机一项(20~50)，全凭人品哟");
            }else if (selection==5){//狼人脚趾
				fstype=6;
				cm.sendNext("你目前选择的是#r随即增加装备全属性(10~20)#k\r\n这项功能目前需要手续费用1E冒险币+100个白狼人脚趾\r\n#b注意：50%机率成功#k,当然失败会返回你一半的手续费费用\r\n什么是随即增加全属性呢？就是选择要加的装备后，会随即给这装备加全属性随机一项(10~20)，全凭人品哟\r\n#r提示#k：#b白狼人脚趾#k可通过击杀#g白狼人#k掉落");
            }else if (selection==1){
				fstype=2;
				cm.sendSimple("目前只受理#r装备栏#k和#r现金栏#k两个栏位的清理工作，主要考虑到部份玩家提出的无法清理点装的问题，没有地方放，#r如果你不想要某个装备，请选择后，在删除~是不是很方便？注意，此处清理为不可恢复性清理，所以在准备清理前一定看清楚了.\r\n#b#L1#我要考虑清理装备栏某一个装备#l\r\n#L2#我要考虑清理现金栏某一个装备#l");
            }else if (selection==6){
				cm.dispose();
				cm.openNpc(9310073, 1);
			}
       } else if (status == 2) {
			if(fstype==1){//正义币
				fstype=13;
				var ii = Packages.server.MapleItemInformationProvider.getInstance();
				var item = cm.getInventory(1).getItem(1);
				var statup = new java.util.ArrayList();
				if(item==null){
				cm.sendOk("对不起,你装备栏第一格没有装备!"); 
				cm.dispose(); 
				}else if(ii.isCash(item.getItemId())==true){
				cm.sendOk("商城物品暂不支持.");
				cm.dispose();
				}else{
					cm.sendNext("请把装装备放在装备窗口的第一格，否则你将不能成功.\r\n请确认一下你要砸的装备是：#v"+item.getItemId()+"##z"+item.getItemId()+"#吗？");
				}
				}				
			if(fstype==2){//清理装备
				if(selection==1){
					var it;
					var texts="#r---------------请选择您要清理的装备----------------#b\r\n";
					var inv = cm.getInventory(1);
					for (var i = 1; i <= 96; i++) {
						it = inv.getItem(i);
						if (it != null) {
							texts+="#L"+i+"#装备图片:#v"+it.getItemId()+"# 装备名称及属性:#g#z"+it.getItemId()+"##l#b\r\n"
						}
					}
					fstype=101;
					cm.sendSimpleS(texts,2);
				}else if(selection==2){
					var it;
					var texts="#r---------------请选择您要清理的装备----------------#b\r\n";
					var inv = cm.getInventory(5);
					for (var i = 1; i <= 96; i++) {
						it = inv.getItem(i);
						if (it != null) {
							texts+="#L"+i+"#装备图片:#v"+it.getItemId()+"# 装备名称及属性:#g#z"+it.getItemId()+"##l#b\r\n"
						}
					}
					fstype=102;
					cm.sendSimpleS(texts,2);
				}
				}
			if(fstype==3){//黄金枫叶
				fstype=14;
				var ii = Packages.server.MapleItemInformationProvider.getInstance();
				var item = cm.getInventory(1).getItem(1);
				var statup = new java.util.ArrayList();
				if(item==null){
				cm.sendOk("对不起,你装备栏第一格没有装备!"); 
				cm.dispose(); 
				}else if(ii.isCash(item.getItemId())==true){
				cm.sendOk("商城物品暂不支持.");
				cm.dispose();
				}else{
					cm.sendNext("请把装装备放在装备窗口的第一格，否则你将不能成功.\r\n请确认一下你要砸的装备是：#v"+item.getItemId()+"##z"+item.getItemId()+"#吗？");
				}
				}
			if(fstype==4){//彩色枫叶
				fstype=15;
				var ii = Packages.server.MapleItemInformationProvider.getInstance();
				var item = cm.getInventory(1).getItem(1);
				var statup = new java.util.ArrayList();
				if(item==null){
				cm.sendOk("对不起,你装备栏第一格没有装备!"); 
				cm.dispose(); 
				}else if(ii.isCash(item.getItemId())==true){
				cm.sendOk("商城物品暂不支持.");
				cm.dispose();
				}else{
					cm.sendNext("请把装装备放在装备窗口的第一格，否则你将不能成功.\r\n请确认一下你要砸的装备是：#v"+item.getItemId()+"##z"+item.getItemId()+"#吗？");
				}
				}
			if(fstype==5){//消费币
				fstype=16;
				var ii = Packages.server.MapleItemInformationProvider.getInstance();
				var item = cm.getInventory(1).getItem(1);
				var statup = new java.util.ArrayList();
				if(item==null){
				cm.sendOk("对不起,你装备栏第一格没有装备!"); 
				cm.dispose(); 
				}else if(ii.isCash(item.getItemId())==true){
				cm.sendOk("商城物品暂不支持.");
				cm.dispose();
				}else{
					cm.sendNext("请把装装备放在装备窗口的第一格，否则你将不能成功.\r\n请确认一下你要砸的装备是：#v"+item.getItemId()+"##z"+item.getItemId()+"#吗？");
				}
				}
			if(fstype==6){//脚趾
				fstype=17;
				var ii = Packages.server.MapleItemInformationProvider.getInstance();
				var item = cm.getInventory(1).getItem(1);
				var statup = new java.util.ArrayList();
				if(item==null){
				cm.sendOk("对不起,你装备栏第一格没有装备!"); 
				cm.dispose(); 
				}else if(ii.isCash(item.getItemId())==true){
				cm.sendOk("商城物品暂不支持.");
				cm.dispose();
				}else{
					cm.sendNext("请把装装备放在装备窗口的第一格，否则你将不能成功.\r\n请确认一下你要砸的装备是：#v"+item.getItemId()+"##z"+item.getItemId()+"#吗？");
				}
				}				
		}else if(status == 3){
			if(fstype==13){
				if(cm.getMeso() < price || cm.haveItem(4310034,0)==false){
					cm.sendOk("对不起,你没有足够的冒险币,或者是没有足够的#z4310034#");
					cm.dispose();
					return;
				}
				var chance = Math.floor(Math.random()*2);
				if(chance==1){
				var item = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.EQUIP).getItem(1).copy();
				var statup = new java.util.ArrayList();
				item.setUpgradeSlots((item.getUpgradeSlots() + 1));
				Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
				Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(),item,false);
				cm.gainMeso(-price);
				cm.gainItem(4310034,0);
				cm.sendOk("恭喜你成功拉，快快看你的包裹吧！");
				cm.worldMessage("[武器升级]：恭喜[" + cm.getChar().getName() + "]在市场Kin处，使用正义币为武器提升了1次砸卷次数");
				cm.dispose();
				}else{
				cm.gainMeso(-price/2);
				cm.gainItem(4310034,0);
				cm.sendOk("真遗憾，升级失败");
				}
				cm.dispose();
				return;
			}
			if(fstype==14){
				if(cm.getMeso() < price || cm.haveItem(4310003,0)==false){
					cm.sendOk("对不起,你没有足够的冒险币,或者是没有足够的#z4310003#");
					cm.dispose();
					return;
				}
				var chance = Math.floor(Math.random()*2);
				if(chance==1){
				var item = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.EQUIP).getItem(1).copy();
				var statup = new java.util.ArrayList();
				item.setUpgradeSlots((item.getUpgradeSlots() + 1));
				Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
				Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(),item,false);
				cm.gainMeso(-price);
				cm.gainItem(4310003,0);
				cm.sendOk("恭喜你成功拉，快快看你的包裹吧！");
				cm.worldMessage("[武器升级]：恭喜[" + cm.getChar().getName() + "]在市场Kin处，使用黄金枫叶为武器提升了1次砸卷次数");
				cm.dispose();
				}else{
				cm.gainMeso(-price/2);
				cm.gainItem(4310003,0);
				cm.sendOk("真遗憾，升级失败");
				}
				cm.dispose();
				return;
			}
			if(fstype==15){
				if(cm.getMeso() < price || cm.haveItem(4032733,0)==false){
					cm.sendOk("对不起,你没有足够的冒险币,或者是没有足够的#z4032733#");
					cm.dispose();
					return;
				}
				var chance1 = Math.floor(Math.random()*2);
				if(chance1==1){
				var item = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.EQUIP).getItem(1).copy();
				var ii = Packages.server.MapleItemInformationProvider.getInstance();
				var chance = Math.floor(Math.random()*100);
				var lvsj = Math.floor(Math.random()*10)+10;
				cm.gainMeso(-price);
				cm.gainItem(4032733,0);
				if(chance<=5){//watk
				item.setWatk(item.getWatk()*1+lvsj);
				Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
				Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(),item,false);
				cm.sendOk("恭喜，成功给装备增加了:#r"+lvsj+"#k攻击.");
				cm.worldMessage("[装备升级]：恭喜[" + cm.getChar().getName() + "]在市场Kin处，使用彩虹枫叶提升了武器的攻击");
				}else if(chance>5 && chance<=20){//matk
				item.setMatk(item.getMatk()*1+lvsj);
				Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
				Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(),item,false);
				cm.sendOk("恭喜，成功给装备增加了:#r"+lvsj+"#k魔攻.");
				cm.worldMessage("[装备升级]：恭喜[" + cm.getChar().getName() + "]在市场Kin处，使用彩虹枫叶提升了武器的魔法攻击");
				}else if(chance>20 && chance<=40){//str
				item.setStr(item.getStr()*1+lvsj);
				Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
				Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(),item,false);
				cm.sendOk("恭喜，成功给装备增加了:#r"+lvsj+"#k力量.");
				cm.worldMessage("[装备升级]：恭喜[" + cm.getChar().getName() + "]在市场Kin处，使用彩虹枫叶提升了武器的力量");
				}else if(chance>40 && chance<=60){//dex
				item.setDex(item.getDex()*1+lvsj);
				Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
				Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(),item,false);
				cm.sendOk("恭喜，成功给装备增加了:#r"+lvsj+"#k敏捷.");
				cm.worldMessage("[装备升级]：恭喜[" + cm.getChar().getName() + "]在市场Kin处，使用彩虹枫叶提升了武器的敏捷");
				}else if(chance>60 && chance<=80){//luk
				item.setInt(item.getInt()*1+lvsj);
				Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
				Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(),item,false);
				cm.sendOk("恭喜，成功给装备增加了:#r"+lvsj+"#k智力.");
				cm.worldMessage("[装备升级]：恭喜[" + cm.getChar().getName() + "]在市场Kin处，使用彩虹枫叶提升了武器的智力");
				}else if(chance>80){//int
				item.setLuk(item.getLuk()*1+lvsj);
				Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
				Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(),item,false);
				cm.sendOk("恭喜，成功给装备增加了:#r"+lvsj+"#k运气.");
				cm.worldMessage("[装备升级]：恭喜[" + cm.getChar().getName() + "]在市场Kin处，使用彩虹枫叶提升了武器的运气");
			}
				}else{
				cm.gainMeso(-price/2);
				cm.gainItem(4032733,0);
				cm.sendOk("真遗憾，升级失败");
				}
				cm.dispose();
				return;
			}
			if(fstype==17){
				if(cm.getMeso() < price || cm.haveItem(4000054,0)==false){
					cm.sendOk("对不起,你没有足够的冒险币,或者是没有足够的#z4000054#");
					cm.dispose();
					return;
				}
				var chance1 = Math.floor(Math.random()*2);
				if(chance1==1){
				var item = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.EQUIP).getItem(1).copy();
				var ii = Packages.server.MapleItemInformationProvider.getInstance();
				var chance = Math.floor(Math.random()*100);
				var lvsj = Math.floor(Math.random()*10)+10;
				cm.gainMeso(-price);
				cm.gainItem(4032733,0);
				if(chance<=5){//watk
				item.setWatk(item.getWatk()*1+lvsj);
				Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
				Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(),item,false);
				cm.sendOk("恭喜，成功给装备增加了:#r"+lvsj+"#k攻击.");
				cm.worldMessage("[装备升级]：恭喜[" + cm.getChar().getName() + "]在市场Kin处，使用白狼人脚趾提升了武器的攻击");
				}else if(chance>5 && chance<=20){//matk
				item.setMatk(item.getMatk()*1+lvsj);
				Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
				Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(),item,false);
				cm.sendOk("恭喜，成功给装备增加了:#r"+lvsj+"#k魔攻.");
				cm.worldMessage("[装备升级]：恭喜[" + cm.getChar().getName() + "]在市场Kin处，使用白狼人脚趾提升了武器的魔法攻击");
				}else if(chance>20 && chance<=40){//str
				item.setStr(item.getStr()*1+lvsj);
				Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
				Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(),item,false);
				cm.sendOk("恭喜，成功给装备增加了:#r"+lvsj+"#k力量.");
				cm.worldMessage("[装备升级]：恭喜[" + cm.getChar().getName() + "]在市场Kin处，使用白狼人脚趾提升了武器的力量");
				}else if(chance>40 && chance<=60){//dex
				item.setDex(item.getDex()*1+lvsj);
				Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
				Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(),item,false);
				cm.sendOk("恭喜，成功给装备增加了:#r"+lvsj+"#k敏捷.");
				cm.worldMessage("[装备升级]：恭喜[" + cm.getChar().getName() + "]在市场Kin处，使用白狼人脚趾提升了武器的敏捷");
				}else if(chance>60 && chance<=80){//luk
				item.setInt(item.getInt()*1+lvsj);
				Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
				Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(),item,false);
				cm.sendOk("恭喜，成功给装备增加了:#r"+lvsj+"#k智力.");
				cm.worldMessage("[装备升级]：恭喜[" + cm.getChar().getName() + "]在市场Kin处，使用白狼人脚趾提升了武器的智力");
				}else if(chance>80){//int
				item.setLuk(item.getLuk()*1+lvsj);
				Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
				Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(),item,false);
				cm.sendOk("恭喜，成功给装备增加了:#r"+lvsj+"#k运气.");
				cm.worldMessage("[装备升级]：恭喜[" + cm.getChar().getName() + "]在市场Kin处，使用白狼人脚趾提升了武器的运气");
			}
				}else{
				cm.gainMeso(-price/2);
				cm.gainItem(4000054,0);
				cm.sendOk("真遗憾，升级失败");
				}
				cm.dispose();
				return;
			}
			if(fstype==16){
				if(cm.getHyPay(1) < 0){
					cm.sendOk("对不起,你没有足够的消费币");
					cm.dispose();
					return;
				}
				var item = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.EQUIP).getItem(1).copy();
				var ii = Packages.server.MapleItemInformationProvider.getInstance();
				var chance = Math.floor(Math.random()*100);
				var lvsj = Math.floor(Math.random()*30)+20;
				cm.addHyPay(0);
				if(chance<=5){//watk
				item.setWatk(item.getWatk()*1+lvsj);
				Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
				Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(),item,false);
				cm.sendOk("恭喜，成功给装备增加了:#r"+lvsj+"#k攻击.");
				cm.worldMessage("[装备升级]：恭喜[" + cm.getChar().getName() + "]在市场Kin处，使用消费币叶提升了武器的攻击");
				}else if(chance>5 && chance<=20){//matk
				item.setMatk(item.getMatk()*1+lvsj);
				Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
				Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(),item,false);
				cm.sendOk("恭喜，成功给装备增加了:#r"+lvsj+"#k魔攻.");
				cm.worldMessage("[装备升级]：恭喜[" + cm.getChar().getName() + "]在市场Kin处，使用消费币叶提升了武器的魔法攻击");
				}else if(chance>20 && chance<=40){//str
				item.setStr(item.getStr()*1+lvsj);
				Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
				Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(),item,false);
				cm.sendOk("恭喜，成功给装备增加了:#r"+lvsj+"#k力量.");
				cm.worldMessage("[装备升级]：恭喜[" + cm.getChar().getName() + "]在市场Kin处，使用消费币叶提升了武器的力量");
				}else if(chance>40 && chance<=60){//dex
				item.setDex(item.getDex()*1+lvsj);
				Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
				Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(),item,false);
				cm.sendOk("恭喜，成功给装备增加了:#r"+lvsj+"#k敏捷.");
				cm.worldMessage("[装备升级]：恭喜[" + cm.getChar().getName() + "]在市场Kin处，使用消费币叶提升了武器的敏捷");
				}else if(chance>60 && chance<=80){//luk
				item.setInt(item.getInt()*1+lvsj);
				Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
				Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(),item,false);
				cm.sendOk("恭喜，成功给装备增加了:#r"+lvsj+"#k智力.");
				cm.worldMessage("[装备升级]：恭喜[" + cm.getChar().getName() + "]在市场Kin处，使用消费币叶提升了武器的智力");
				}else if(chance>80){//int
				item.setLuk(item.getLuk()*1+lvsj);
				Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
				Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(),item,false);
				cm.sendOk("恭喜，成功给装备增加了:#r"+lvsj+"#k运气.");
				cm.worldMessage("[装备升级]：恭喜[" + cm.getChar().getName() + "]在市场Kin处，使用消费币叶提升了武器的运气");
			}
				cm.dispose();
				return;
			}
			if(fstype==101){
				Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, selection, 1, true);
				cm.sendOk("恭喜,此道具已被清除.");
				cm.dispose();
			}
			if(fstype==102){
				Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.CASH, selection, 1, true);
				cm.sendOk("恭喜,此道具已被清除.");
				cm.dispose();
			}
				}
    }
}
