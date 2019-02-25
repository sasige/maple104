var status = 0;
var choice;
var itemwq=new Array("1082295","1082296","1082297","1082298","1082299");
var itemwqsx=new Array("500","1000","5000","18888","32767");
var itemzbyb=new Array("0","0","0","0","0");


function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) cm.dispose();
    else {
        if (status == 0 && mode == 0) {
            cm.dispose();
            return;
        } else if (status >= 1 && mode == 0) {
            cm.sendOk("好吧，欢迎下次继续光临！.");
            cm.dispose();
            return;
        }
        if (mode == 1) status++;
        else status--;

        if (status == 0) {
		var selStr="#e#r看看有没有您喜欢的.#n#k\r\n";
		for (var i = 0; i < itemwq.length; i++) {//itemyz.length
		selStr += "#b#L" + i + "##v" +itemwq[i]+"#  #t" +itemwq[i]+"#   #d点击图片选择购买的属性#l\r\n";
		}
		cm.sendSimpleS(selStr,2);
        } else if (status == 1) {
		buyitem=itemwq[selection];
		var selStr="您选择要购买的装备是:#v"+buyitem+"#.请选择装备的属性:\r\n";
		for (var i = 0; i < itemwqsx.length; i++) {
		selStr += "#b#L" + i + "##r全属性(+" +itemwqsx[i]+")  #d需要 #r"+itemzbyb[i]+"#d消费币#l\r\n";
		}
		cm.sendSimple(selStr+"\r\n#e#b注意:以上装备都加攻击和魔力\r\n#r活动：如果拥有五色福袋3级#v4031944#一个在减4000消费币.");
        } else if (status == 2) {
		var needyb=itemzbyb[selection];
		var checkit=1;
		if(cm.haveItem(4031944,1)==true){
		needyb-=4000;
		checkit=2;
		}
		if(cm.getHyPay(1)>=needyb){
		cm.addHyPay(needyb);
		cm.setBossLog("sxst"+cm.getPlayer().getId());
		var ii = Packages.server.MapleItemInformationProvider.getInstance();
		var type = Packages.constants.GameConstants.getInventoryType(buyitem);//物品
		var toDrop = ii.randomizeStats(ii.getEquipById(buyitem)).copy(); // 生成一个Equip类
		toDrop.setStr(itemwqsx[selection]); //装备力量
		toDrop.setDex(itemwqsx[selection]); //装备敏捷
 		toDrop.setInt(itemwqsx[selection]); //装备智力
		toDrop.setLuk(itemwqsx[selection]); //装备运气
		toDrop.setWatk(itemwqsx[selection]);
		toDrop.setMatk(itemwqsx[selection]);
		cm.getPlayer().getInventory(type).addItem(toDrop); //将这个装备放入包中
		cm.getC().getSession().write(Packages.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包
		if(checkit==2){
		cm.gainItem(4031944,-1);
		cm.sendOk("恭喜,购买成功.扣除消费币:"+needyb+".\r\n由于你有五色福袋3级#v4031944#，节省了4000消费币.\r\n目前的消费币余额:#g"+cm.getHyPay(1)+"点#k.\r\n获得:#b#i"+buyitem+"#(全属性+"+itemwqsx[selection]+") x 1");
		}else{
		cm.sendOk("恭喜,购买成功.扣除消费币:"+needyb+".\r\n目前的消费币余额:#g"+cm.getHyPay(1)+"点#k.\r\n获得:#b#i"+buyitem+"#(全属性+"+itemwqsx[selection]+") x 1");
		}
		}else{
		cm.sendOk("对不起.你没有足够的消费币.\r\n购买#b#i"+buyitem+"#(+"+itemwqsx[selection]+") x 1 需要:"+itemzbyb[selection]+" 消费币.");
		}
		cm.dispose();
		return;
        }
    }
}
