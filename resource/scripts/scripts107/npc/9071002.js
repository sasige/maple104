var status = -1;
function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            cm.dispose();
        }
        status--;
    }
    if (status == 0) {
        cm.sendSimple("你好～天气不错吧？要想使用怪物公园，当然应该来找我。我能为你做什么呢？\r\n#L3##r购买入场券#l\r\n#L5#纪念币兑换#l\r\n#b#L0#领取#t4001514##l\r\n#L4#什么是怪物公园？#l#k");
    } else if (status == 1) {
        if (selection == 0) {
                if (cm.getChar().getVip() == 0) {
                    cm.sendOk("对不起.您不是会员\r\n你可以免费领取#rVIP1#k后再来领取入场卷\r\n#rVIP1#k在市场史蒂夫处领取");
                    cm.dispose();
                } else if (cm.getChar().getVip() == 1) {
                    if (cm.getChar().getBossLog("斑纹工资") == 0) {
                        cm.gainItem(4001514, 2); //斑纹2张
                        cm.getChar().setBossLog("斑纹工资");
                        cm.worldMessage("[怪物公园]：玩家[" + cm.getChar().getName() + "]在梅里处领取了VIP1的2张斑纹入场卷");
                        cm.sendOk("恭喜.领取到了2张#r#t4001514##k\r\n#rVIP1#k每日领取2张\r\n#rVIP2#k每日领取4张\r\n#rVIP3#k每日领取6张\r\n#rVIP4#k每日领取10张\r\n#rVIP5#k每日无限领取");
                        cm.dispose();
                    } else {
                        cm.sendOk("对不起.您今天已经领取过了..");
                        cm.dispose();
                    }
                } else if (cm.getChar().getVip() == 2) {
                    if (cm.getChar().getBossLog("斑纹工资") == 0) {
                        cm.gainItem(4001514, 4); //斑纹4张
                        cm.getChar().setBossLog("斑纹工资");
                        cm.worldMessage("[怪物公园]：玩家[" + cm.getChar().getName() + "]在梅里处领取了VIP2的4张斑纹入场卷");
                        cm.sendOk("恭喜.领取到了4张#r#t4001514##k\r\n#rVIP1#k每日领取2张\r\n#rVIP2#k每日领取4张\r\n#rVIP3#k每日领取6张\r\n#rVIP4#k每日领取10张\r\n#rVIP5#k每日无限领取");
                        cm.dispose();
                    } else {
                        cm.sendOk("对不起.您今天已经领取过了..");
                        cm.dispose();
                    }
                } else if (cm.getChar().getVip() == 3) {
                    if (cm.getChar().getBossLog("斑纹工资") == 0) {
                        cm.gainItem(4001514, 6); //斑纹6张
                        cm.getChar().setBossLog("斑纹工资");
                        cm.worldMessage("[怪物公园]：玩家[" + cm.getChar().getName() + "]在梅里处领取了VIP3的6张斑纹入场卷");
                        cm.sendOk("恭喜.领取到了6张#r#t4001514##k\r\n#rVIP1#k每日领取2张\r\n#rVIP2#k每日领取4张\r\n#rVIP3#k每日领取6张\r\n#rVIP4#k每日领取10张\r\n#rVIP5#k每日无限领取");
                        cm.dispose();
                    } else {
                        cm.sendOk("对不起.您今天已经领取过了..");
                        cm.dispose();
                    }
                } else if (cm.getChar().getVip() == 4) {
                    if (cm.getChar().getBossLog("斑纹工资") == 0) {
                        cm.gainItem(4001514, 10); //斑纹10张
                        cm.getChar().setBossLog("斑纹工资");
                        cm.worldMessage("[怪物公园]：玩家[" + cm.getChar().getName() + "]在梅里处领取了VIP4的10张斑纹入场卷");
                        cm.sendOk("恭喜.领取到了10张#r#t4001514##k\r\n#rVIP1#k每日领取2张\r\n#rVIP2#k每日领取4张\r\n#rVIP3#k每日领取6张\r\n#rVIP4#k每日领取10张\r\n#rVIP5#k每日无限领取");
                        cm.dispose();
                    } else {
                        cm.sendOk("对不起.您今天已经领取过了..");
                        cm.dispose();
                    }
                } else if (cm.getChar().getVip() == 5) {
                    if (cm.haveItem(4001514) == 0) {
                        cm.gainItem(4001514, 1); //斑纹1张
                        cm.worldMessage("[怪物公园]：VIP5玩家[" + cm.getChar().getName() + "]在梅里处领取了斑纹入场卷");
                        cm.sendOk("恭喜.领取到了1张#r#t4001513##k\r\n#rVIP1#k每日领取2张\r\n#rVIP2#k每日领取4张\r\n#rVIP3#k每日领取6张\r\n#rVIP4#k每日领取10张\r\n#rVIP5#k每日无限领取");
                        cm.dispose();
                    } else {
                        cm.sendOk("对不起.您身上还有没用完的斑纹入场卷,请使用完后再来领取");
                        cm.dispose();
                    }
                } else if (cm.getChar().getVip() >= 6) {
                    if (cm.haveItem(4001515) == 0) {
                        cm.gainItem(5074000, 30); //喇叭
                        cm.gainItem(5390006, 5); //老虎喇叭
                        cm.gainNX(10000);
                        cm.gainMeso(500000000);
                        cm.getChar().setBossLog("VIP工资");
                        cm.worldMessage("[系统公告]恭喜玩家:[" + cm.getChar().getName() + "]在市场史蒂夫处领取了会员工资.");
                        cm.sendOk("恭喜.领取成功..")
                        cm.dispose();
                    } else {
                        cm.sendOk("对不起.您今天已经领取过了..");
                        cm.dispose();
                    }
                } else {
                    cm.sendOk("对不起.您不是会员\r\n你可以免费领取#rVIP1#k后再来领取入场卷\r\n#rVIP1#k在市场史蒂夫处领取");
                    cm.dispose();
                }
        } else if (selection == 1) {
            status = -1;
	if(cm.haveItem(4001515) >= 10){
	cm.gainItem(4001515,-10);
	cm.gainItem(4001514,1);
            cm.sendOk("恭喜你交换成功.");
	    cm.dispose();
	}else{
            cm.sendNext("怎么回事？没有啊。要想交换入场券，需要#b10个入场券碎片#k。");
}
        } else if (selection == 2) {
            status = -1;
	if(cm.haveItem(4001521) >= 10){
	cm.gainItem(4001521,-10);
	cm.gainItem(4001522,1);
            cm.sendOk("恭喜你交换成功.");
	    cm.dispose();
	}else{
            cm.sendNext("怎么回事？没有啊。要想交换入场券，需要#b10个入场券碎片#k。");
}
        } else if (selection == 3) {
            cm.sendSimple("嗯～本来不能这样的，因为我最近心情很好，所以才会破例卖给你。对了，这件事一定要对休彼德蔓保密！\r\n#b#L0##t4001514#1000消费币#l\r\n#L1##t4001514#消耗1个#z4031994##l\r\n#L2##t4001514#5E游戏币");
        } else if (selection == 4) {
            cm.sendOk("哈哈，这么快就传出去了吗？我为到怪物公园来玩的人准备了特别的东西。你想看一看吗？\r\n#b纪念币抽全属性装备<机率极高><110-130级装备>\r\n纪念币抽勋章，稀有椅子<机率极高>\r\n纪念币抽取休彼德蔓的徽章,胡子,亲笔签名#k\r\n怎么样？很想得到吧？呵呵呵，我在怪物公园的怪物身上放了特殊的纪念品。打猎怪物，可以发现#b#t4310020##k,收集就能交换到我准备的特殊的东西\r\n#r每次通关成功也会有一枚#b#t4310020##r哟#k");
	    cm.dispose();
        } else if (selection == 5) {
	    cm.dispose();
            cm.openNpc(9071002,1);
        }
    } else if (status == 2) {
        if (selection == 0) {
	if(cm.getHyPay(1) >= 1000){
	cm.gainItem(4001514,1);
        cm.addHyPay(1000);
        cm.sendOk("恭喜你购买成功");
	cm.dispose();
	} else {
        cm.sendOk("消费币余额不足,你也可以选择其他方式购买入场卷");
	cm.dispose();
}
        } else if (selection == 1) {
	if(cm.haveItem(4031994) >= 1){
	cm.gainItem(4031994,-1);
	cm.gainItem(4001514,1);
        cm.sendOk("恭喜你购买成功");
	cm.dispose();
	} else {
        cm.sendOk("你没有#z4031994#,你也可以选择其他方式购买入场卷");
	cm.dispose();
}
        } else if (selection == 2) {
	if(cm.getMeso() >= 500000000){
	cm.gainItem(4001514,1);
	cm.gainMeso(-500000000);
        cm.sendOk("恭喜你购买成功");
	cm.dispose();
	} else {
        cm.sendOk("游戏币不足,你也可以选择其他方式购买入场卷");
	cm.dispose();
        }
        cm.dispose();
    }
}}