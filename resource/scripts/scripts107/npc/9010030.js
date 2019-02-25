var status = 0;
var eff = "#fEffect/CharacterEff/1112905/0/1#";

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (status == 0 && mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
    if (status == 0) {
        var selStr = "您好，欢迎来到终结者冒险岛,我是各种商城物品贩卖商\r\n您目前的消费币余额:#r" + cm.getHyPay(1) + " #d消费币比例:#g1元=1000消费币\r\n";
	selStr+="#r新年特价活动：9件顶级属性32767套装 现在充值特价只要999元=230000消费币#k\r\n";
        selStr += "#r#L0#" + eff + "#d购买商城物品[魔方,喇叭,冒险岛转蛋卷,防暴卷]#l\r\n";
        selStr += "#b#L1#" + eff + "各类椅子#l";
        selStr += "#L2#" + eff + "炫酷玩具#l";
        selStr += "#L4#" + eff + "消耗/卷轴#l";
        selStr += "#L3#" + eff + "属性武器#l";
        selStr += "#b#L5#" + eff + "属性帽子#l";
        selStr += "#L6#" + eff + "属性鞋子#l";
        selStr += "#L7#" + eff + "属性/衣服#l";
        selStr += "#L8#" + eff + "属性手套#l\r\n";
        selStr += "#L9#" + eff + "属性披风#l";
        selStr += "#L10#" + eff + "属性戒指,勋章,腰带,坠子,脸绘,微章.#l";
        cm.sendSimpleS(selStr, 2);
    } else if (status == 1) {
        switch (selection) {
        case 0:
            //商城物品
            cm.dispose();
            cm.openNpc(9900002, 11);
            break;
        case 1:
            //各类椅子
            cm.dispose();
            cm.openNpc(9900002, 29);
            break;
        case 2:
            //玩具
            cm.dispose();
            cm.openNpc(9900002, 27);
            break;
        case 3:
            //属性武器
            cm.dispose();
            cm.openNpc(9900002, 9);
            break;
        case 4:
            //卷轴
            cm.dispose();
            cm.openNpc(9900002, 4);
            break;
        case 5:
            //属性帽子
            cm.dispose();
            cm.openNpc(9900002, 10);
            break;
        case 6:
            cm.dispose();
            cm.openNpc(9900002, 111);
            break;
        case 7:
            cm.dispose();
            cm.openNpc(9900002, 112);
            break;
        case 8:
            cm.dispose();
            cm.openNpc(9900002, 113);
            break;
        case 9:
            cm.dispose();
            cm.openNpc(9900002, 114);
            break;
        case 10:
            cm.dispose();
            cm.openNpc(9900002, 115);
            break;
        }
    }
}
