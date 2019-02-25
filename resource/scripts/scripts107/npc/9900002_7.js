var status = -1;
var selectedpay = 0;
var acash = 3;

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0 && status == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        } else {
            if (status == 2) {
                cm.sendNext("如果您需要充值金额兑换成点卷的话，那么请下次来找我！");
                cm.dispose();
            }
            status--;
        }
        if (status == 0) {
            cm.sendSimple("亲爱的#b#h ##k您好，我是终结者冒险岛玩家点卷充值兑换员。现在的充值比例为#r1RMB:1000消费币#k的\r\n您的消费信息为:\r\n消费币余额:#r " + cm.getHyPay(1) + " #k\r\n已消费金额:#r " + cm.getHyPay(2)/1000 + " #k元\r\n累计充值总金额:#r " + cm.getHyPay(3)/1000 + " #k元\r\n#b#L0#兑换点卷(1000消费币:3000点卷)#l\r\n#b#L1#充值消费币(1RMB:1000消费币)#l");
        } else if (status == 1) {
		if (selection==0){
            		if (cm.getHyPay(1) == 0) {
                		cm.sendNext("您没有可兑换的点卷。");
                		cm.dispose();
            		} else {
                		cm.sendGetNumber("请输入您要兑换的点卷:\r\n游戏点卷的兑换比例为 1000消费币:3000点卷\r\n", 1000, 1000, cm.getHyPay(1));
            		}
		}else if (selection==1){
	    		cm.openWeb("http://www.libaopay.com/buy/?wid=12366");
			cm.dispose();
		}
        } else if (status == 2) {
            selectedpay = selection;
            if (cm.getHyPay(1) < selectedpay) {
                cm.sendNext("您消费币不够。");
                cm.dispose();
            } else {
                cm.sendYesNo("您是否要将#r " + selectedpay + " #k消费币兑换成#b " + selectedpay * acash + " #k的点卷。");
            }
        } else if (status == 3) {
            if (cm.getHyPay(1) < selectedpay) {
                cm.sendNext("您充值金额不够。");
            } else if (cm.addHyPay(selectedpay) > 0) {
                cm.gainNX(selectedpay * acash);
                cm.sendOk("恭喜您成功兑换#b " + selectedpay * acash + " #k的点卷");
            } else {
                cm.sendOk("兑换点卷出现错误，请反馈给管理员！");
            }
            cm.dispose();
        } else {
            cm.dispose();
        }
    }
}
