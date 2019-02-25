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
    if (cm.getMapId() == 180000001) {
        cm.sendOk("很遗憾，您因为违反用户守则被禁止游戏活动，如有异议请联系管理员.")
        cm.dispose();
    }
    else if (status == 0) {
	var selStr = "#e玩家名字:[#b"+cm.getChar().getName()+"#k]  修为点:#r"+cm.getXw()+"#k  会员等级:#r"+cm.getVip()+"#k\r\n消费币数量:#g"+cm.getHyPay(1)+"#k\r\n";
        selStr += "#L0#传送到市场#l #L2#查看怪物掉物#l  #L3#每日签到#l\r\n\r\n";
        selStr += "#L1#点满技能#l 　#L11#终结者新手介绍#l　#L4#快速转职#l\r\n\r\n";
        selStr += "#L5#美容美发#l　 #L6#属性重置#l　　　#L7#仓库管理#l\r\n\r\n";
        selStr += "#L8#超级转生#l　 #L9#万能传送#l　　　#L10#游戏商店#l\r\n\r\n";
	selStr += "#L11##r会员购买#k#l　 #L12##r属性装备#k#l　　　#L13##r给力副本#k#l\r\n\r\n";
	selStr += "#L14##b充值奖励#k#l   #L15##b修炼成仙#k#l\r\n\r\n";
        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
        case 0:
            if (cm.getPlayer().getMapId() >= 910000000 && cm.getPlayer().getMapId() <= 910000022) {
                cm.sendOk("您已经在市场了，还想做什么？");
            } else {
                cm.saveReturnLocation("FREE_MARKET");
                cm.warp(910000000, "st00");
            }
            cm.dispose();
            break;
        case 1://满技能
            cm.dispose();
            cm.openNpc(9900002, 23);
            break;
        case 2://查看怪物
            cm.dispose();
            cm.openNpc(9010000, 1);
            break;

        case 3://每日签到
            cm.dispose();
            cm.openNpc(9900004, 10);
            break;
        case 4://快速专职
            cm.dispose();
            cm.openNpc(9300011);
            break;
        case 5://美容美发
            cm.dispose();
            cm.openNpc(9900002, 24);
            break;
        case 6://属性重置
            cm.dispose();
            cm.openNpc(9900002, 26);
            break;
        case 7://仓库管理
            cm.dispose();
            cm.openNpc(9030100);
            break;
        case 8://超级转生
            cm.dispose();
            cm.openNpc(9310059);
            break;
        case 9://万能传送
            cm.dispose();
            cm.openNpc(9900002, 22);
            break;
        case 10://游戏商店
            cm.dispose();
            cm.openNpc(1012121);
            break;
	case 11:
		cm.dispose();
            cm.openNpc(9070002);
            break;
	case 12:
		cm.dispose();
            cm.openNpc(9010030);
            break;
	case 13:
		cm.dispose();
            cm.openNpc(9070005);
            break;
	case 15:
		cm.dispose();
            cm.openNpc(3000010);
            break;
	case 14:
		cm.dispose();
            cm.openNpc(9900002, 17);
            break;

        }
    }
}
