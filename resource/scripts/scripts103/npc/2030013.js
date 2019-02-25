importPackage(net.sf.odinms.tools);
importPackage(net.sf.odinms.client);

/* Adobis
Zakum entrance
*/
var status = 0;
var price = 5000000;
var map = Array(240010501);

function start() {
    status = -1;
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
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            cm.sendSimple("勇士, #h #. 欢迎来到 #g扎昆的祭台入口#k.进阶在3线召唤. #d每天只能进入扎昆的祭台5次.#k#r今天你已经进入"+cm.getBossLog('ZAKUM')+"次#k.#k#l 你想要有火眼吗?\r\n#L0##b购买 火眼(5,000,000 冒险币)#l#k\r\n\#L1##r进入扎昆祭台.#l");
        } else if (status == 1) {
            if (selection == 0) {
                if(cm.getMeso() >= 5000000) {
                    cm.gainMeso(-5000000);
                    cm.gainItem(4001017, 1);
                } else {
                    cm.sendOk("请确认您的包袱里有足够的金币.");
                }
                cm.dispose();
            } else if (status == 2) {
            } else if (selection == 1 && cm.getLevel() >= 100) {
                if (cm.getBossLog('ZAKUM') < 5 && cm.getC().getChannel() > 1 && cm.getC().getChannel() < 4)
                {
                    if(cm.getC().getChannel() == 3){
                        cm.warp(280030001, 0);
                    }else{
                        cm.warp(280030000, 0);
                    }
                    cm.setBossLog('ZAKUM');
                    cm.dispose();
                }
                else
                {
                    cm.sendOk("你没有满足以下条件\r\n你每天只能进入扎昆祭坛5次.\r\n扎昆BOSS在能在2-3频道被召唤.进阶在3线召唤.");
                    mode = 1;
                    status = -1;
                }
            }
            else{
                cm.sendOk("你必须达到100级以上才能挑战 #m280030000#.");
                mode = 1;
                status = -1;
            }
        }
    }
}