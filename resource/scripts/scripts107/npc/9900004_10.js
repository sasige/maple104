var status = 0;

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
        var selStr = "亲爱的#r#h ##k您好， 这里能给你提供每日福利哟#k!\r\n\r\n#L1#签到新手必看\r\n#L2#开始每日签到#l\r\n";
        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
        case 0:
            
        case 1:
            cm.sendOk("#r特殊签到#k会累计每天签到的天数，也代表着你对终结者的支持，说不定哪天就换到终结者东西了哟\r\n#b普通签到#k每天签到可获得1个字母和1-5个月光铜钱，集齐#v3991052##v3991053##v3991054##v3991055#可抽取一次必成卷轴\r\n10个月光铜钱还能换取500消费币\r\n#g情侣签到#k每天签到就可以获得10点恩爱值，不仅可以上情侣恩爱排行，还能在会计小姐处换取专属称号");
            cm.dispose();
            break;
        case 2:
            cm.dispose();
            cm.openNpc(9900002, 25);
            break;
        case 3:
            cm.dispose();
            cm.openNpc(1012121);
            break;
        case 4:
            cm.dispose();
            cm.warp(749050400);
            break;
        case 5:
            cm.dispose();
            cm.openNpc(9900002, 2);
            break;
        case 6:
            cm.dispose();
            cm.openNpc(9030100);
            break;
        case 7:
            cm.dispose();
            cm.openNpc(9900002, 8);
            break;
        case 8:
            cm.dispose();
            cm.openNpc(9270035);
            break;
        case 9:
            cm.dispose();
            cm.openShop(2060003);
            break;
        }
    }
}
