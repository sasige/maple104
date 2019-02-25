var status = 0;
var choice;
var itemsc=new Array("5220040","5062000","5062001","5062002","5110000","5072000","2049117","5073000","5074000","5076000","5077000","5390000","5390001","5390002","5390003","5390004","5390005","5390007","5390008","5390006");
var itemsccost=new Array("0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0");

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
            choices = "\r\n消费币余额：#r" + cm.getHyPay(1) + "#k消费币 (#r买后不支持退货哟亲#k)";
            for (var i = 0; i < itemsc.length; i++) {
                choices += "\r\n#L" + i + "##v" + itemsc[i] + "##z" + itemsc[i] + "#　#d需要#r" + itemsccost[i] + "#d消费币#k#l";
            }
            cm.sendSimpleS("欢迎来到精品玩具店,你想买我们商店的物品么?请选择吧：." + choices,2);
        } else if (status == 1) {
                cm.sendGetNumber("你选择的商品为#v" + itemsc[selection] + "#售价为：" + itemsccost[selection] + "消费币/个\r\n请输入你购买的数量",1,1,cm.getHyPay(1));
		choice = selection;
        } else if (status == 2) {
            fee = selection;
            money = fee*itemsccost[choice];
            if (fee < 0) {
            cm.sendOk("你输入的数负数!");
            cm.dispose();
            } else if (cm.getHyPay(1) < money) {
            cm.sendOk("兑换失败，你没有" + money + "个消费币");
            cm.dispose();
            } else {
	    cm.addHyPay(money);
            cm.gainItem(itemsc[choice], fee); //圣杯
            cm.sendOk("恭喜，购买成功。");
            cm.dispose();
                }
        }
    }
}