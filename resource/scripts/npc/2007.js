var job;
function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (cm.getJob() == 2000) {
        cm.warp(914000000);
//      cm.sendNext("战神");
        cm.dispose();
    } else {
        if (cm.getJob() == 1000) {
            cm.warp(130030000);
//          cm.sendNext("骑士团");
            cm.dispose();
        }
        if (cm.getJob() == 3000) {
            cm.warp(931000000);
//          cm.sendNext("反抗者");
            cm.dispose();
        }
        if (cm.getJob() == 2001) {
            cm.warp(900010200);
// 100030100   900090000      cm.sendNext("龙神");
            cm.dispose();
        }
        if (cm.getJob() == 508) {
            cm.sendNext("龙的传人");
            cm.dispose();
        }
        if (cm.getJob() == 2002) {
            cm.sendNext("双弩精灵");
            cm.dispose();
        }
        if (cm.getJob() == 3001) {
            cm.sendNext("恶魔猎手");
            cm.dispose();
        }
        if (cm.getJob() == 2003) {
            cm.sendNext("幻影");
            cm.dispose();
        }
        if (cm.getJob() == 5000) {
            cm.sendNext("米哈尔");
            cm.dispose();
        }
        if (cm.getJob() == 3001) {
            cm.sendNext("恶魔猎手");
            cm.dispose();
        }
        if (cm.getJob() == 2004) {
            cm.sendNext("夜光法师");
            cm.dispose();
        } 
        if (cm.getJob() == 6500) {
            cm.sendNext("爆莉萌天使");
            cm.dispose();
        }
        if (cm.getJob() == 6000) {
            cm.sendNext("狂龙战士");
            cm.dispose();
        }else {
            status--;
        }
        if (cm.getJob() == 0) {
            cm.dispose();
        } else if (cm.getJob() == 2) {
            cm.warp(104000000);
            cm.dispose();
        }
    }
}
