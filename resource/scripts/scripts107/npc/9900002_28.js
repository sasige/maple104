importPackage(java.util);
importPackage(net.sf.odinms.client);
importPackage(net.sf.odinms.server);

var psrw = new Array(3010209, 3010012, 3010040, 3010045,3010077,3010095,3010100,3010116,3012003,3010297,3010061,3010113,3010114,3010173,3010137,3010129,3010130,3010154,3010211,3010157,3010162,3010191,3010206,3010194,3010189,3010188,3010218,3010221,3010212,3010177,3010197,3010201,3010222,3010300,3010311,3010225,3010321,3010365,3010407,3010414,3010428,3010409,3010085);
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
            text = "伟大的#b#h ##k\r\n欢迎来我这里签到,每天抽取一次,你可以在这里有机会获取到各种椅子\r\n";
		text +="#v3010209# #v3010012# #v3010040# #v3010045# #v3010077# #v3010095# #v3010100# #v3010116# #v3012003# #v3010297# #v3010061# #v3010113# #v3010114# #v3010173# #v3010137# #v3010129# #v3010130# #v3010154# #v3010211# #v3010157# #v3010162# #v3010191# #v3010206# #v3010194# #v3010189# #v3010188# #v3010218# #v3010221# #v3010212# #v3010177# #v3010197# #v3010201# #v3010222# #v3010300# #v3010311# #v3010225# #v3010321# #v3010365# #v3010407# #v3010414# #v3010428# #v3010409# #v3010085#";
            text += "\r\n你当前已经连续抽奖#r" + cm.getBossLog("mryz") + "#k天\r\n";
            text += "#b#L0#我要抽奖#k#l\r\n";
            cm.sendSimple(text);
        } else if (status == 1) {
            if (selection == 0) {
                if (cm.getBossLog("mryz") < 1) {
                    cm.setBossLog("mryz");
                    cm.gainItem(psrw[rand], 1); //SDNA随即给
                    cm.sendOk("抽取椅子成功.");
                    cm.dispose();
                } else {
                    cm.sendOk("你今天已经抽取椅子过了,明天再来吧");
                    cm.dispose();
                }
            } else if (selection == 1) {
                if (cm.getBossLogCount("mrqd", 1) == 5) {
                    if (cm.getVip() != 2) {
                        cm.setVip(2, 365);
                        cm.sendOk("恭喜你,成功通过签到获得本服Vip2!");
                        //cm.getC().getChannelServer().broadcastPacket(Packages.tools.MaplePacketCreator.serverNotice(0x10, cm.getC().getChannel(), "『签到系统』" + " : " + "『" + cm.getChar().getName() + "』成功通过签到系统获得本服Vip2~"));
                        cm.dispose();
                    } else {
                        cm.sendOk("对不起,你当前已经是本服Vip2!");
                        cm.dispose();
                    }
                } else if (cm.getBossLogCount("mrqd", 1) == 15) {
                    if (cm.getVip() != 3) {
                        cm.setVip(3, 365);
                        cm.sendOk("恭喜你,成功通过签到获得本服Vip3!");
                        cm.getC().getChannelServer().broadcastPacket(Packages.tools.MaplePacketCreator.serverNotice(0x10, cm.getC().getChannel(), "『签到系统』" + " : " + "『" + cm.getChar().getName() + "』成功通过签到系统获得本服Vip3~"));
                        cm.dispose();
                    } else {
                        cm.sendOk("对不起,你当前已经是本服Vip3!");
                        cm.dispose();
                    }
                } else if (cm.getBossLogCount("mrqd", 1) == 30) {
                    if (cm.getVip() != 4) {
                        cm.setVip(4, 365);
                        cm.sendOk("恭喜你,成功通过签到获得本服Vip3!");
                        cm.getC().getChannelServer().broadcastPacket(Packages.tools.MaplePacketCreator.serverNotice(0x10, cm.getC().getChannel(), "『签到系统』" + " : " + "『" + cm.getChar().getName() + "』成功通过签到系统获得本服Vip4~"));
                        cm.dispose();
                    } else {
                        cm.sendOk("对不起,你当前已经是本服Vip4!");
                        cm.dispose();
                    }
                } else {
                    cm.sendOk("亲,你还没有达到指定的签到次数哟!");
                }
            }
        }
    }
}