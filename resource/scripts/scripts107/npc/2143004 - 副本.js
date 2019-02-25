/*NPC制作：Nemo*/
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
        var selStr = "#b#L0#希纳斯#l  #L1#离开#l";
        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
        case 0:
            if (cm.getPlayer().getLevel() > 180) {
                cm.warp(271040100);
            } else {
		cm.sendOk("你的等级小于180,不能进入 请练级");
            }
            cm.dispose();
            break;
        case 1:
            if (cm.getPlayer().getLevel() > 1) {
                cm.warp(910000000);
            } else {
		cm.sendOk("你的等级小于5,不能进入 请练级");
            }
            cm.dispose();
            break;
         }
    }
}