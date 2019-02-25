var status = 0;
var items = Array(
Array(3010100,10),
Array(3010128,2),
Array(3010139,2),
Array(3010170,2),
Array(3010131,2),
Array(3010172,2),
Array(3010029,1),
Array(3010030,1),
Array(3010031,1),
Array(3010032,1),
Array(3010033,1),
Array(3010046,2),
Array(3010047,2),
Array(3010077,2),
Array(3010093,2),
Array(3010098,2),
Array(3012002,2),
Array(3012003,2),
Array(3012006,2),
Array(3012010,2),
Array(3012011,2),
Array(3010175,2),
Array(3010161,3),
Array(3010177,2),
Array(3010196,3),
Array(3010212,5),
Array(3010221,1),
Array(3010218,3),
Array(3010188,1),
Array(3010188,1),
Array(3010184,3),
Array(3010181,5),
Array(3010180,5),
Array(3010166,2),
Array(3010151,2),
Array(3010140,2),
Array(3010156,3),
Array(3010187,1),
Array(3010053,2),
Array(3010183,2),
Array(3010182,2),
Array(3010168,3),
Array(3010135,2),
Array(3010070,7),
Array(3010071,5),
Array(3010073,4)
);
var selectedItems = -1;
var item = 4031326;
var number = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (status == 0 && mode == 0) {
			cm.dispose();
			return;
		} else if (status == 2 && mode == 0) {
			cm.dispose();
		}
		if (mode == 1)
			status++;
		else
			status--;
                                          if (status == 0) {
cm.sendSimple("欢迎来到099单机冒险岛！如没有您中意的椅子,请联系飘舞管理员添加！#k#b\r\n#L1# 我想用#t4031326#兑换豪华椅子。\r\n#L2# 点击购买一个#t4031326# (10000点)")
										  }else if (status == 1){
											  if (selection == 1){
												  var selStr = "请选择您想兑换的奖品！#b";
												  for (var i = 0; i < items.length; i++) {
						selStr += "\r\n#L" + i + "##z" + items[i][0] + "# ("+items[i][1]+"个#t4031326#)#l";
					}
												cm.sendSimple(selStr);
	}else if (selection == 2){
	if(cm.getChar().getNX() >= 10000) {
            cm.gainNX(-10000); 
	cm.gainItem(4031326,1);
        cm.sendOk("购买成功！"); 
        }else{
        cm.sendOk("对不起！您没有足够的点，不能给你兑换！！"); 
	}
			 cm.dispose()
}
										  }else if (status == 2){
											  selectedItems = selection;
											  number = items[selectedItems][1];
											  itemss = items[selectedItems][0];
cm.sendYesNo("你已经决定好，确定要兑换 #b#z" + items[selectedItems][0] + "##k吗？\r\n那么你将要给我 #b" + number +"#k个#t4031326#。");
										  }else if (status == 3){
											  if (cm.itemQuantity(item) < number) {//如果小于要兑换的
											  cm.sendOk("对不起，你没有足够的#t4031326#");
											cm.dispose();
										} else {
											cm.gainItem(item,-number);
				cm.gainItem(items[selectedItems][0], 1);
				cm.sendOk("兑换成功，你使用了"+number+"个#t4031326#。")
				cm.serverNotice(" [" + cm.getPlayer() + "] 在枫叶募捐箱里使用财神的信物兑换了豪华椅子。")
				cm.dispose();
		}
		}//status
	}//mode
}//function
