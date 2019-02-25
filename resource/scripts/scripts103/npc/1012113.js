function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        //cm.warp(240040700);
        cm.dispose();
    } 
    else {
        if (mode == 0) {
            //cm.warp(240040700);
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {			
            cm.sendNext(""+cm.GetSN()+"带给你无限的快乐~!,#l#k\r\n#r挑战凤舞三重天么可能获得#e#r\r\n\r\n火:#v1302063##v1302084##v1302087##v1372014##v1372035##v1342006#\r\n冰:#v1302080##v1432094##v1302128##v1702136##v1702211##v1702113#\r\n暗:#v1702321##v1702240##v3010085##v3010092##v1332077##v1302026#\r\n\r\n想的话就点下一项吧~!`");											
        }else if (status == 1) {
            cm.sendYesNo("#e#b确定要去挑战凤舞三兄弟么?");
        }else if (status == 2) {
            cm.warp(910510000);			
            cm.dispose();
        }
    }
}

