var psrw = new Array(3010209, 3010012, 3010040, 3010045, 3010077, 3010095, 3010100, 3010116, 3012003, 3010297, 3010061, 3010113, 3010114, 3010173, 3010137, 3010129, 3010130, 3010154, 3010211, 3010157, 3010162, 3010191, 3010206, 3010194, 3010189, 3010188, 3010218, 3010221, 3010212, 3010177, 3010197, 3010201, 3010222, 3010300, 3010311, 3010225, 3010321, 3010365, 3010407, 3010414, 3010428, 3010409, 3010085);
var rand = Math.floor(Math.random() * psrw.length);
var psrw1 = new Array(2, 4, 2, 4, 3, 2, 2, 5, 4, 2, 3, 6, 3, 7, 2, 4, 2);
var rand1 = Math.floor(Math.random() * psrw1.length);
var psrw2 = new Array(2040506, 2040806, 2040807, 2043303, 2043203, 2043103, 2043703, 2043803, 2044003, 2044103, 2044203, 2044303, 2044403, 2044603, 2044703, 2040807, 2044908);
var rand2 = Math.floor(Math.random() * psrw2.length);
var status = 0;
var fstype = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1) status++;
        if (status == 0) {
            var text = "";
            text = "伟大的#b#h ##k\r\n我的英雄,看来是你拯救了我,虽然你以为我会以身相许,但是你错了,因为我已经有心上人了.不过做为你帮助我抢回玉玺的回报,我可以送你些礼物!\r\n";
            text += "#v3010209# #v3010012# #v3010040# #v3010045# #v3010077# #v3010095# #v3010100# #v3010116# #v3012003# #v3010297# #v3010061# #v3010113# #v3010114# #v3010173# #v3010137# #v3010129# #v3010130# #v3010154# #v3010211# #v3010157# #v3010162# #v3010191# #v3010206# #v3010194# #v3010189# #v3010188# #v3010218# #v3010221# #v3010212# #v3010177# #v3010197# #v3010201# #v3010222# #v3010300# #v3010311# #v3010225# #v3010321# #v3010365# #v3010407# #v3010414# #v3010428# #v3010409# #v3010085#";
            text += "\r\n#b#L0#哎,这么漂亮的公主,浪费了!算了,还是拿了东西走人吧!#k#l\r\n";
            cm.sendSimple(text);
        } else if (status == 1) {
            if (selection == 0) {
                if (cm.haveItem(4001318)) {
		var chance = Math.floor(Math.random()*2);
		if(chance==1){
                cm.gainItem(4002002, 3);
		cm.removeAll(4001318);
                cm.warp(910000000, "st00");
		cm.sendOk("恭喜你获得了3张#v4002002#");
		cm.worldMessage("[英雄就美]：玩家["+cm.getChar().getName()+"]拯救了公主了，公主并未对他动心拉，给予了邮票奖励");
		cm.dispose();
		}else{
                cm.gainItem(psrw[rand], 1);
		cm.removeAll(4001318);
                cm.warp(910000000, "st00");
		cm.sendOk("恭喜你获得了#v" + psrw[rand] + "#");
		cm.worldMessage("[英雄就美]：玩家["+cm.getChar().getName()+"]拯救了公主了，公主对他动心拉，并给予的稀有椅子奖励");
		cm.dispose();
		};
                } else {
                    cm.sendOk("你好像没有拿回#z4001318#.");
                    cm.dispose();
                }
            }
        }
    }
}