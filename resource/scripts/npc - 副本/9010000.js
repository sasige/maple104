function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.sendNext("本技能官方上限已改为20,技能书无法使用,请丢弃");
        cm.dispose();
    } else {
        if (status == 0 && mode == 0) {
            cm.sendNext("本技能官方上限已改为20,技能书无法使用,请丢弃");
            cm.dispose();
        }
        if (mode == 1) {
            status++;
        } else {
            status--;
        }
        if (status == 0) {
            cm.sendNext("我一直努力着给大家带来最好的服务，如果你旅行累了，请来找我吧!");
        } else if (status == 1) {
            cm.dispose();
        }
    }
}