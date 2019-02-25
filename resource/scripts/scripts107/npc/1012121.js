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
        var selStr = "\r\n"; //#L9#狮子王道具兑换#r[New]#k#l\r\n
	//selStr +="#L2#打开杂货店(技能册/挑战物品/宠物食品/等.)#l\r\n";
	selStr +="#L3#打开杂货店(药水/花生/Boss物品/放大镜/潜能卷/灵魂盾)#l\r\n";
	selStr +="#L0#打开双弩武器商店#l";
	selStr +="#L4#打开火炮手武器店#l\r\n";
	selStr +="#L5#打开双刀武器店#l　";
	selStr +="#L6#各职业新手装备武器店(龙传人宝盒)#l\r\n";
	//selStr +="#L8#打开骑宠商店#l　　";
	selStr +="#L10#各职业装备(70-110)(幻影已更新)#l\r\n";
	selStr +="#L11#狂龙战士|爆莉萌天使装备店#l";
	selStr +="#L12#夜光法师装备店#l";
        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
        case 0:
	    cm.openShop(1033001);
            break;
        case 2:
            cm.dispose();
            cm.openShop(1012123);
            break;
        case 3:
            cm.dispose();
            cm.openShop(9090000);
            break;
        case 4:
            cm.dispose();
            cm.openShop(1012124);
            break;
        case 5:
            cm.dispose();
            cm.openShop(1012125);
            break;
        case 6:
            cm.dispose();
		cm.openShop(1011101);
            break;
        case 7:
            cm.dispose();
            break;
        case 8:
            cm.dispose();
            cm.openShop(9010038);
            break;
        case 9:
            cm.dispose();
            cm.openShop(2161010);
            break;
	case 10:
            cm.dispose();
            cm.openNpc(2101018,1);
            break;
	case 11:
            cm.dispose();
            cm.openShop(1012133);
            break;
	case 12:
            cm.dispose();
            cm.openShop(1012132);
            break;
        }
    }
}