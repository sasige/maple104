var status = 0;
var choice;
var itemxh=new Array("4002002","2330007","2070024","2070023","2070019","2049301","2049300","2049401","2049400","2340000","2040304","2040506","2040710","2040806","2043003","2043103","2043203","2043303","2043703","2043803","2044003","2044019","2044103","2044203","2044303","2044403","2044503","2044603","2044703","2044815","2044908");
var itemxhcost=new Array("0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0");

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
            for (var i = 0; i < itemxh.length; i++) {
                choices += "\r\n#L" + i + "##v" + itemxh[i] + "##z" + itemxh[i] + "#　#d需要#r" + itemxhcost[i] + "#d消费币#k#l";
            }
            cm.sendSimpleS("欢迎来到精品玩具店,你想买我们商店的物品么?请选择吧：." + choices,2);
        } else if (status == 1) {
                cm.sendGetNumber("你选择的商品为#v" + itemxh[selection] + "#售价为：" + itemxhcost[selection] + "消费币/个\r\n请输入你购买的数量",1,1,cm.getHyPay(1));
		choice = selection;
        } else if (status == 2) {
            fee = selection;
            money = fee*itemxhcost[choice];
            if (fee < 0) {
            cm.sendOk("你输入的数负数!");
            cm.dispose();
            } else if (cm.getHyPay(1) < money) {
            cm.sendOk("兑换失败，你没有" + money + "个消费币");
            cm.dispose();
            } else {
	    cm.addHyPay(money);
            cm.gainItem(itemxh[choice], fee); //圣杯
            cm.sendOk("恭喜，购买成功。");
            cm.dispose();
                }
        }
    }
}