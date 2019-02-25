var status = 0;
var fee = 0;
var chance = Math.floor(Math.random() * 4 + 1);
var typed = 0;
var vipsdwp = new Array(
1432086, 1442116, 1322096, 1422066);
var vipsdsl = new Array(
1, 1, 1, 1);
var vipsdjq = new Array(
1, 1, 1, 1);
var vipwj = new Array(
1402029, 1302063, 1112238, 1442039, 1112135, 1112919, 1702305, 1702302, 1372017, 1302087, 1142321, 1302024, 1402014, 1322027, 1402044, 1402063, 1412056, 1332032, 1332053);
var randwj = Math.floor(Math.random() * vipwj.length);
var vipyizi = new Array(
3010055, 3010107, 3010115, 3010109, 3010129, 3010205, 3010170, 3010172, 3010162, 3010183, 3010299, 3010156, 3010140, 3010212, 3010161, 3010280, 3010306, 3010300, 3010301);
var randyizi = Math.floor(Math.random() * vipyizi.length);

function start() {
    status = -1;

    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) { //ExitChat
        cm.dispose();

    } else if (mode == 0) { //No
        //cm.sendOk("好的,如果你想好了要做什么,我会很乐意的为你服务的..");
        cm.dispose();

    } else { //Regular Talk
        if (mode == 1) status++;
        else status--;

        if (status == 0) {

            var text = "你好,我是本服的VIP办理员,你当前的Vip等级为#r" + cm.getVip() + "#k\r\n目前的成长值:#r" + cm.getVipczz() + "#k 消费币余额：#r" + cm.getHyPay(1) + "#k\r\n";
            text += "#L1##b我要办理会员(会员购买和升级)#r(VIP2免费中)\r\n";
            text += "#L2##b会员介绍(会员送的物品和详细功能查询)\r\n";
            text += "#L3#会员工资(根据VIP级别判定)\r\n";
            text += "#L4#会员功能使用(根据VIP级别显示使用功能)\r\n";
            text += "#L5#会员礼包领取(购买VIP获得的赠送物品领取)\r\n";
            text += "#L6#会员练级传送(根据VIP级别自动传送地图)\r\n";
            //text += "#L8#会员专属商店(每天VIP限量购买物品<稀有>)\r\n";
            cm.sendSimple(text);
        } else if (status == 1) {
            if (selection == 1) {
                typed = 1;
                cm.sendSimple("升级会员每次只需要补差价，现在只需要转生5次就可以免费获得VIP2了，心动了吧？\r\n#L1##r我要办理会员#k");
            } else if (selection == 2) {
                getVipInfo();
            } else if (selection == 4) { //会员功能
                typed = 4;
                    if (cm.getChar().getVip() == 2) {
                    var hysj2 = 500 - cm.getChar().getVipczz();
                    var hygj2 = hysj2 / 10;
                    text = "#d尊贵的#b#h ##k,#d你目前会员等级为#rVIP2#k\r\n";
                    //text += "#d你的会员到期时间为：#r" + cm.getChar().getViptime() + "#k\r\n";
                    //text += "#d预计下次会员升级还需#r" + hygj2 + "#k#d天\r\n";
                    text += "#d每个级别的VIP享受功能不同,以下是您能使用的功能#k\r\n";
                    text += "#L1##b[VIP2]#k消除副本次数#l\r\n";
                    //text += "#L11##b[VIP2]#k会员成长值商店#l\r\n";
                    //text += "#L2##b[VIP2]#k会员每日打工任务#l\r\n";
                    text += "#L3##b[VIP2]#k更换皇家发型<106版本>#l\r\n";
                    text += "#L4##b[VIP2]#k刷新正义之章<随机刷新>#l\r\n";
                    cm.sendSimple(text);
                } else if (cm.getChar().getVip() == 3) {
                    var hysj3 = 800 - cm.getChar().getVipczz();
                    var hygj3 = hysj3 / 10;
                    text = "#d尊贵的#b#h ##k,#d你目前会员等级为#rVIP3#k\r\n";
                    //text += "#d你的会员到期时间为：#r" + cm.getChar().getViptime() + "#k\r\n";
                    //text += "#d预计下次会员升级还需#r" + hygj3 + "#k#d天\r\n";
                    text += "#d每个级别的VIP享受功能不同,以下是您能使用的功能#k\r\n";
                    text += "#L1##d[VIP2]#k消除副本次数#l\r\n";
                    //text += "#L11##b[VIP2]#k会员成长值商店#l\r\n";
                    //text += "#L2##d[VIP2]#k会员每日打工任务#l\r\n";
                    text += "#L3##b[VIP2]#k更换皇家发型<106版本>#l\r\n";
                    text += "#L4##b[VIP2]#k刷新正义之章<随机刷新>#l\r\n";
                    text += "#L5##g[VIP3]#k给武器增加全属性<每天1次>#l\r\n";
                    text += "#L6##g[VIP3]#k会员大转盘[玩具,卷轴,金币]<每天1次>#l\r\n";
                    text += "#L10##g[VIP3]#k使用黄金钥匙兑换GM必成卷轴#l\r\n";
                    cm.sendSimple(text);
                } else if (cm.getChar().getVip() == 4) {
                    var hysj4 = 1000 - cm.getChar().getVipczz();
                    var hygj4 = hysj4 / 10;
                    text = "#d尊贵的#b#h ##k,#d你目前会员等级为#rVIP4#k\r\n";
                    //text += "#d你的会员到期时间为：#r" + cm.getChar().getViptime() + "#k\r\n";
                    //text += "#d预计下次会员升级还需#r" + hygj4 + "#k#d天\r\n";
                    text += "#d每个级别的VIP享受功能不同,以下是您能使用的功能#k\r\n";
                    text += "#L1##d[VIP2]#k消除副本次数#l\r\n";
                    //text += "#L11##b[VIP2]#k会员成长值商店#l\r\n";
                    //text += "#L2##d[VIP2]#k会员每日打工任务#l\r\n";
                    text += "#L3##b[VIP2]#k更换皇家发型<随机更换>#l\r\n";
                    text += "#L4##b[VIP2]#k刷新正义之章<随机刷新>#l\r\n";
                    text += "#L5##g[VIP3]#k给武器增加全属性<每天1次>#l\r\n";
                    text += "#L6##g[VIP3]#k会员大转盘[玩具,卷轴,金币]<每天2次>#l\r\n";
                    text += "#L10##g[VIP3]#k使用黄金钥匙兑换GM必成卷轴#l\r\n";
                    text += "#L7##r[VIP4]#k更换皇家发型<自主选择>#l\r\n";
                    text += "#L8##r[VIP4]#k领取140装备一把<随机领取>#l\r\n";
                    cm.sendSimple(text);
                } else if (cm.getChar().getVip() == 5) {
                    var hysj5 = 1000 - cm.getChar().getVipczz();
                    var hygj5 = hysj4 / 10;
                    text = "#d尊贵的#b#h ##k,#d你目前会员等级为#rVIP5#k\r\n";
                    //text += "#d你的会员到期时间为：#r" + cm.getChar().getViptime() + "#k\r\n";
                    text += "#d每个级别的VIP享受功能不同,以下是您能使用的功能#k\r\n";
                    text += "#L1##d[VIP2]#k消除副本次数#l\r\n";
                    //text += "#L11##b[VIP2]#k会员成长值商店#l\r\n";
                    //text += "#L2##d[VIP2]#k会员每日打工任务#l\r\n";
                    text += "#L3##b[VIP2]#k更换皇家发型<随机更换>#l\r\n";
                    text += "#L4##b[VIP2]#k刷新正义之章<随机刷新>#l\r\n";
                    text += "#L5##g[VIP3]#k给武器增加全属性<每天1次>#l\r\n";
                    text += "#L6##g[VIP3]#k会员大转盘[玩具,卷轴,金币]<每天3次>#l\r\n";
                    text += "#L10##g[VIP3]#k使用黄金钥匙兑换GM必成卷轴#l\r\n";
                    text += "#L7##r[VIP4]#k更换皇家发型<自主选择>#l\r\n";
                    text += "#L8##r[VIP4]#k领取140装备一把<随机领取>#l\r\n";
                    text += "#L9##r[VIP5]#k增加转身次数上限#l\r\n";
                    //text += "#L10##r[VIP5]#k专属刷邮票地图<每日限进3次>#l\r\n";
                    cm.sendSimple(text);
                } else {
                    cm.sendOk("只有VIP2以上才能使用");
                    cm.dispose();
                }
            } else if (selection == 3) { //工资
                if (cm.getChar().getVip() == 0) {
                    cm.sendOk("对不起.您不是会员");
                    cm.dispose();
                } else if (cm.getChar().getVip() == 1) {
                    if (cm.getChar().getBossLog("VIP工资") == 0) {
                        cm.gainItem(4031454, 2); //2转身证明
                        cm.gainMeso(20000000); //1000W游戏币
                        cm.getChar().setBossLog("VIP工资");
                        cm.worldMessage("[系统公告]恭喜玩家:[" + cm.getChar().getName() + "]领取了会员1工资.");
                        cm.sendOk("恭喜.领取成功,工资如下：\r\n#z4031454#x2\r\n游戏币200,000,00");
                        cm.dispose();
                    } else {
                        cm.sendOk("对不起.您今天已经领取过了..");
                        cm.dispose();
                    }
                } else if (cm.getChar().getVip() == 2) {
                    if (cm.getChar().getBossLog("VIP工资") == 0) {
                        cm.gainItem(4031454, 4); //4转身证明
                        cm.gainNX(5000); //5000点卷
                        cm.gainMeso(100000000); //5000W游戏币
                        cm.getChar().setBossLog("VIP工资");
                        cm.worldMessage("[系统公告]恭喜玩家:[" + cm.getChar().getName() + "]领取了会员2工资.");
                        cm.sendOk("恭喜.领取成功,工资如下：\r\n#z4031454#x4\r\n点卷5,000\r\n游戏币1000,000,00");
                        cm.dispose();
                    } else {
                        cm.sendOk("对不起.您今天已经领取过了..");
                        cm.dispose();
                    }
                } else if (cm.getChar().getVip() == 3) {
                    if (cm.getChar().getBossLog("VIP工资") == 0) {
                        cm.gainItem(5390000, 1); //喇叭1
                        cm.gainItem(5390001, 1); //喇叭2
                        cm.gainItem(5390002, 1); //喇叭3
                        cm.gainItem(5390013, 2); //白银喇叭
                        cm.gainItem(4031454, 6); //转身证明
                        cm.gainNX(10000); //10000点卷
                        cm.gainMeso(300000000); //3E游戏币
                        cm.getChar().setBossLog("VIP工资");
                        cm.worldMessage("[系统公告]恭喜玩家:[" + cm.getChar().getName() + "]领取了会员3工资.");
                        cm.sendOk("恭喜.领取成功,工资如下：\r\n#z5390000#x1\r\n#z5390001#x1\r\n#z5390002#x1\r\n#z5390013#x2\r\n#z4031454#x6\r\n点卷10,000\r\n游戏币300,000,000");
                        cm.dispose();
                    } else {
                        cm.sendOk("对不起.您今天已经领取过了..");
                        cm.dispose();
                    }
                } else if (cm.getChar().getVip() == 4) {
                    if (cm.getChar().getBossLog("VIP工资") == 0) {
                        cm.gainItem(5390000, 2); //喇叭1
                        cm.gainItem(5390001, 2); //喇叭2
                        cm.gainItem(5390002, 2); //喇叭3
                        cm.gainItem(5390012, 2); //黄金
                        cm.gainItem(4031454, 10); //转身证明
                        cm.gainNX(20000); //40000点卷
                        cm.gainMeso(400000000); //4E游戏币
                        cm.getChar().setBossLog("VIP工资");
                        cm.worldMessage("[系统公告]恭喜玩家:[" + cm.getChar().getName() + "]领取了会员4工资.");
                        cm.sendOk("恭喜.领取成功,工资如下：\r\n#z5390000#x2\r\n#z5390001#x2\r\n#z5390002#x2\r\n#z5390012#x2\r\n#z4031454#x10\r\n点卷20,000\r\n游戏币400,000,000");
                        cm.dispose();
                    } else {
                        cm.sendOk("对不起.您今天已经领取过了..");
                        cm.dispose();
                    }
                } else if (cm.getChar().getVip() == 5) {
                    if (cm.getChar().getBossLog("VIP工资") == 0) {
                        cm.gainItem(5390000, 4); //喇叭1
                        cm.gainItem(5390001, 4); //喇叭2
                        cm.gainItem(5390002, 4); //喇叭3
                        cm.gainItem(5390011, 2); //钻石
                        cm.gainItem(4031454, 15); //转身证明
                        cm.gainNX(40000); //40000点卷
                        cm.gainMeso(1000000000); //10E游戏币
                        cm.getChar().setBossLog("VIP工资");
                        cm.worldMessage("[系统公告]恭喜玩家:[" + cm.getChar().getName() + "]领取了会员5工资.");
                        cm.sendOk("恭喜.领取成功,工资如下：\r\n#z5390000#x4\r\n#z5390001#x4\r\n#z5390002#x4\r\n#z5390011#x2\r\n#z4031454#x4\r\n点卷20,000\r\n游戏币1,000,000,000");
			cm.dispose();
                    } else {
                        cm.sendOk("对不起.您今天已经领取过了..");
                        cm.dispose();
                    }
                } else if (cm.getChar().getVip() >= 6) {
                    if (cm.getChar().getBossLog("VIP工资") == 0) {
                        cm.gainItem(5074000, 30); //喇叭
                        cm.gainItem(5390006, 5); //老虎喇叭
                        cm.gainNX(10000);
                        cm.gainMeso(500000000);
                        cm.getChar().setBossLog("VIP工资");
                        cm.worldMessage("[系统公告]恭喜玩家:[" + cm.getChar().getName() + "]领取了会员工资.");
                        cm.sendOk("恭喜.领取成功..")
                        cm.dispose();
                    } else {
                        cm.sendOk("对不起.您今天已经领取过了..");
                        cm.dispose();
                    }
                } else {
                    cm.sendOk("对不起.您不是VIP..");
                    cm.dispose();
                }
            } else if (selection == 5) { //会员装备领取
                typed = 2;
                if (cm.getChar().getVip() < 2) {
                    cm.sendOk("对不起.您的会员级别不能领取礼包");
                    cm.dispose();
                } else if (cm.getChar().getVip() >= 2) {
                    text = "#d尊贵的#b#h ##k,#d你目前会员等级为#rVIP" + cm.getChar().getVip() + "#k\r\n";
                    text += "#d每个级别的VIP礼包物品不同#k\r\n#r领取时请注意自己的背包至少要留出10~15个空格.#k\r\n";
                    text += "#d每个等级的礼包需要依次领取.#k\r\n";
                    text += "#L1##b[VIP2]#k领取礼包#l\r\n";
                    text += "#L2##g[VIP3]#k领取礼包#l\r\n";
                    text += "#L3##r[VIP4]#k领取礼包#l\r\n";
                    text += "#L4##r[VIP5]#k领取礼包#l\r\n";
                    text += "#L5##r[VIP4]#k领取企鹅王巨无霸#l\r\n";
                    cm.sendSimple(text);
                }
            } else if (selection == 6) {
                if (cm.getChar().getVip() == 2) {
                    cm.warp(910000007);
			cm.dispose();
                } else if (cm.getChar().getVip() == 3) {
                    cm.warp(910000008);
			cm.dispose();
                } else if (cm.getChar().getVip() == 4) {
                    cm.warp(910000009);
			cm.dispose();
                } else if (cm.getChar().getVip() == 5) {
                    cm.warp(910000010);
			cm.dispose();
                } else {
                    cm.sendOk("对不起,没有适合你Vip等级的专用练级地图.");
			cm.dispose();
                }
            } else if (selection == 7) {
                cm.sendOk("管理员正在添加中...");
                cm.dispose();
            } else if (selection == 8) {
                typed = 8;
                cm.sendSimple("#r会员专属商店#k就是每天不定时随机刷新几种稀有物品在专属商店抢购,当然都是由游戏币购买\r\n每种物品都是限量出售,所以#rVIP#k时常关注专属商店,会有很大的收获哟\r\n#r提示：#k不同级别的VIP专属商店,销售的数量不一样\r\n#b专属商店出售物品#r(GM必成卷,稀有椅子,稀有药水)#k\r\n#L1##r>>打开会员专属商店#k");
            }
        } else if (status == 2) {
            if (typed == 1) {
                typed = 1;
                var text = "#b你当前的Vip等级为#r" + cm.getVip() + "#k　#b消费币余额：#r" + cm.getHyPay(1) + "#k\r\n";
                text += "#L1##d购买VIP2-现价#r10000#d消费币\r\n"
                text += "#L8##r[需要5次转身]#d购买VIP2-现价#r0#d消费币\r\n"; 
                text += "#L2#购买VIP3-现价#r50000#d消费币\r\n";
                text += "#L3#购买VIP4-现价#r100000#d消费币\r\n";
		text += "#L4#购买VIP5-现价#r150000#d消费币\r\n";
                text += "#L5#VIP2升级VIP3-现价#r50000#d消费币\r\n";
                text += "#L6#VIP3升级VIP4-现价#r50000#d消费币\r\n";
                text += "#L7#VIP4升级VIP5-现价#r50000#d消费币#k\r\n";
                cm.sendSimpleS(text,2);
            } else if (typed == 4) {
                typed = 4;
                if (selection == 1) { //消除副本
                    cm.dispose();
                    cm.openNpc(9010030, 1);
                } else if (selection == 2) { //每日打工
                    cm.dispose();
                    cm.openNpc(9010030, 2);
                } else if (selection == 3) { //随机皇家发型
                    cm.dispose();
                    cm.openNpc(1012117);
                } else if (selection == 4) { //刷新正义之章	
                    cm.dispose();
                    cm.openNpc(9010030, 4);
                } else if (selection == 5) { //武器增加属性	
                    cm.dispose();
                    cm.openNpc(9010030, 5);
                } else if (selection == 6) { //大转盘	
                    cm.dispose();
                    cm.openNpc(9010030, 6);
                } else if (selection == 7) { //VIP4自定义	
                    cm.dispose();
                    cm.openNpc(9010030, 7);
                } else if (selection == 8) { //VIP4领取装备	
                    cm.dispose();
                    cm.openNpc(9010030, 8);
                } else if (selection == 11) { //VIP4领取装备	
                            cm.sendOk("商品还在添加中");
                            cm.dispose();
                } else if (selection == 9) { //增加转生次数	
                    if (cm.getChar().getVip() >= 5 && cm.getBossLog("转生") == 20) {
                        if (cm.getBossLog("转生清零") < 1) {
                            cm.setBossLog("转生清零");
                            cm.resetBossLog("转生");
                            cm.sendOk("#b恭喜,你又可以多转20次了!");
                            cm.dispose();
                        } else {
                            cm.sendOk("#b您可能没有1个#z4310034#或者你24小时内今天已经使用过此功能了,现在无法使用此功能.");
                            cm.dispose();
                        }
                    } else {
                        cm.sendOk("您的会员等级无法使用此功能,或者你的转生次数还没用完");
                        cm.dispose();
                    }
                } else if (selection == 10) { 
                    cm.dispose();
                    cm.openShop(2809);
                } else if (selection == 11) {
                    cm.dispose();
                    cm.openNpc(9010030, 11);
                }
            } else if (typed == 2) { //VIP礼包
                var ii = Packages.server.MapleItemInformationProvider.getInstance();
                if (selection == 0) {

                } else if (selection == 1) { //VIP2礼包
                    if (cm.getChar().getVip() >= 2) {
                        if (cm.getBossLog("VIP2礼包", 1) == 0 && (cm.getSpace(1) > 5 && cm.getSpace(2) > 5 && cm.getSpace(4) > 5)) {
                            cm.gainItem(2340000, 10); //祝福卷轴
                            cm.gainItem(5064000, 10); //防爆卷轴
                            cm.gainItem(5062000, 5); //神奇魔方5个
                            cm.gainItem(vipyizi[randyizi],1); //椅子
                            var type = Packages.constants.GameConstants.getInventoryType(1112673); //VIP勋章
                            var toDrop = ii.randomizeStats(ii.getEquipById(1112673)).copy(); // 生成一个Equip类
                            toDrop.setStr(100); //装备力量
                            toDrop.setDex(100); //装备敏捷
                            toDrop.setInt(100); //装备智力
                            toDrop.setLuk(100); //装备运气
                            toDrop.setMatk(100); //物理攻击
                            toDrop.setWatk(100); //魔法攻击
                            cm.getPlayer().getInventory(type).addItem(toDrop); //将这个装备放入包中
                            cm.getC().getSession().write(Packages.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包
                            cm.setBossLog("VIP2礼包", 1);
                            cm.sendOk("恭喜,领取#rVIP2#k礼包成功,VIP2随机获得椅子为#z" + vipyizi[randyizi] + "#");
                            cm.dispose();
                        } else {
                            cm.sendOk("领取失败，失败的原因可能是：\r\n已经领取过，或者栏位不足，需要背包所有窗口在5个位置以上");
                            cm.dispose();
                        }
                    } else {
                        cm.sendOk("您的会员等级无法使用此功能");
                        cm.dispose();
                    }
                } else if (selection == 2) { //VIP3礼包
                    if (cm.getChar().getVip() >= 3) {
                        if (!cm.haveItem(1112673)) {
                            cm.sendOk("你没有VIP2的#z1112673#,或者没放在装备栏里面.\r\n请先领取VIP2礼包");
                            cm.dispose();
                            return;
                        }
                        if (cm.getBossLog("VIP3礼包", 1) == 0 && (cm.getSpace(1) > 10 && cm.getSpace(2) > 10 && cm.getSpace(4) > 10)) {	
                            cm.gainItem(2340000, 20); //祝福卷轴
                            cm.gainItem(5064000, 20); //防爆卷轴
                            cm.gainItem(5062000, 15); //神奇魔方3个
                            cm.gainItem(vipyizi[randyizi],1); //椅子
                            cm.gainItem(3700071, 1); //白银VIP称号
                            cm.gainItem(3010509, 1); //白银VIP椅子
                            cm.gainItem(1003698, 1); //白银VIP皇冠
                            cm.gainItem(3010410, 1); //椅子1
                            cm.gainItem(3010409, 1); //椅子2
                            cm.gainItem(3010075, 1); //椅子3
                            cm.gainItem(3010073, 1); //椅子4
                            cm.gainItem(3010069, 1); //椅子5
                            cm.gainItem(4031217, 10); //自选GM
                            cm.gainItem(1112140, 1); //白金名片戒指
                            cm.gainItem(1112247, 1); //白金聊天戒指
                            var type = Packages.constants.GameConstants.getInventoryType(1112787); //VIP勋章
                            var toDrop = ii.randomizeStats(ii.getEquipById(1112787)).copy(); // 生成一个Equip类
                            toDrop.setStr(500); //装备力量
                            toDrop.setDex(500); //装备敏捷
                            toDrop.setInt(500); //装备智力
                            toDrop.setLuk(500); //装备运气
                            toDrop.setMatk(500); //物理攻击
                            toDrop.setWatk(500); //魔法攻击
                            cm.getPlayer().getInventory(type).addItem(toDrop); //将这个装备放入包中
                            cm.getC().getSession().write(Packages.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包
                            cm.setBossLog("VIP3礼包", 1);
                            cm.sendOk("恭喜,领取#rVIP3#k礼包成功,VIP3随机获得椅子为#z" + vipyizi[randyizi] + "#");
                            cm.dispose();
                        } else {
                            cm.sendOk("领取失败，失败的原因可能是：\r\n已经领取过，或者栏位不足，需要背包所有窗口在10个位置以上");
                            cm.dispose();
                        }
                    } else {
                        cm.sendOk("您的会员等级无法使用此功能");
                        cm.dispose();
                    }
                } else if (selection == 3) { //VIP4礼包
                    if (cm.getChar().getVip() >= 4) {
                        if (!cm.haveItem(1112787)) {
                            cm.sendOk("你没有VIP3的#z1112787#,或者没放在装备栏里面.\r\n请先领取VIP3礼包");
                            cm.dispose();
                            return;
                        }
                        if (cm.getBossLog("VIP4礼包", 1) == 0 && (cm.getSpace(1) > 12 && cm.getSpace(2) > 12 && cm.getSpace(4) > 12)) {
                            cm.gainItem(1112787, -1); //VIP戒指
                            cm.gainItem(2340000, 20); //祝福卷轴
                            cm.gainItem(5062000, 10); //神奇魔方2个
                            cm.gainItem(5064000, 20); //防爆卷轴
                            cm.gainItem(vipwj[randwj],1); //玩具
                            cm.gainItem(3700070, 1); //黄金VIP称号
                            cm.gainItem(3010508, 1); //黄金VIP椅子
                            cm.gainItem(1003697, 1); //黄金VIP皇冠
                            cm.gainItem(1112139, 1); //黄金名片戒指
                            cm.gainItem(1112246, 1); //黄金聊天戒指
                            //cm.gainItem(3010410, 1); //椅子1
                            //cm.gainItem(3010409, 1); //椅子2
                            //cm.gainItem(3010075, 1); //椅子3
                            //cm.gainItem(3010073, 1); //椅子4
                            //cm.gainItem(3010069, 1); //椅子5
                            cm.gainItem(3010031, 1); //椅子6
                            cm.gainItem(3010032, 1); //椅子7
                            cm.gainItem(3010033, 1); //椅子8
                            cm.gainItem(4031217, 10); //自选GM
                            var type = Packages.constants.GameConstants.getInventoryType(1112786); //VIP勋章
                            var toDrop = ii.randomizeStats(ii.getEquipById(1112786)).copy(); // 生成一个Equip类
                            toDrop.setStr(3000); //装备力量
                            toDrop.setDex(3000); //装备敏捷
                            toDrop.setInt(3000); //装备智力
                            toDrop.setLuk(3000); //装备运气
                            toDrop.setMatk(3000); //物理攻击
                            toDrop.setWatk(3000); //魔法攻击
                            cm.getPlayer().getInventory(type).addItem(toDrop); //将这个装备放入包中
                            cm.getC().getSession().write(Packages.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包
                            cm.setBossLog("VIP4礼包", 1);
                            cm.sendOk("恭喜,领取#rVIP4#k礼包成功,VIP4随机获得玩具为#z" + vipwj[randwj] + "#");
                            cm.dispose();
                        } else {
                            cm.sendOk("领取失败，失败的原因可能是：\r\n已经领取过，或者栏位不足，需要背包所有窗口在12个位置以上");
                            cm.dispose();
                        }
                    } else {
                        cm.sendOk("您的会员等级无法使用此功能");
                        cm.dispose();
                    }
                } else if (selection == 4) { //VIP5礼包
                    if (cm.getChar().getVip() >= 5) {
                        if (!cm.haveItem(1112786)) {
                            cm.sendOk("你没有VIP4的#z1112786#,或者没放在装备栏里面.\r\n请先领取VIP4礼包");
                            cm.dispose();
                            return;
                        }
                        if (cm.getBossLog("VIP5礼包", 1) == 0 && (cm.getSpace(1) > 15 && cm.getSpace(2) > 15 && cm.getSpace(4) > 15)) {
                            cm.gainItem(1112786, -1); //VIP勋章
                            cm.gainItem(5062000, 10); //神奇魔方
                            cm.gainItem(5062002, 10); //高级神奇魔方
                            cm.gainItem(5064000, 50); //防爆卷轴
                            cm.gainItem(1112138, 1); //钻石名片戒指
                            cm.gainItem(1112245, 1); //钻石聊天戒指
                            cm.gainItem(2340000, 50); //祝福卷轴
                            cm.gainItem(3700069, 1); //钻石VIP称号
                            cm.gainItem(3010507, 1); //钻石VIP椅子
                            cm.gainItem(1003696, 1); //钻石VIP皇冠
                            //cm.gainItem(3010410, 1); //椅子1
                            //cm.gainItem(3010409, 1); //椅子2
                            //cm.gainItem(3010075, 1); //椅子3
                            //cm.gainItem(3010073, 1); //椅子4
                            //cm.gainItem(3010069, 1); //椅子5
                            //cm.gainItem(3010031, 1); //椅子6
                            //cm.gainItem(3010032, 1); //椅子7
                            //cm.gainItem(3010033, 1); //椅子8
                            cm.gainItem(3010030, 1); //椅子9
                            cm.gainItem(3010029, 1); //椅子10
                            cm.gainItem(4031217, 20); //自选GM
                            cm.gainItem(vipwj[randwj],1); //玩具
                            var type = Packages.constants.GameConstants.getInventoryType(1112785); //VIP勋章
                            var toDrop = ii.randomizeStats(ii.getEquipById(1112785)).copy(); // 生成一个Equip类
                            toDrop.setStr(8888); //装备力量
                            toDrop.setDex(8888); //装备敏捷
                            toDrop.setInt(8888); //装备智力
                            toDrop.setLuk(8888); //装备运气
                            toDrop.setMatk(8888); //物理攻击
                            toDrop.setWatk(8888); //魔法攻击
                            cm.getPlayer().getInventory(type).addItem(toDrop); //将这个装备放入包中
                            cm.getC().getSession().write(Packages.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包
                            cm.setBossLog("VIP5礼包", 1);
                            cm.sendOk("恭喜,领取#rVIP5#k礼包成功,VIP5随机获得玩具为#z" + vipwj[randwj] + "#");
                            cm.dispose();
                        } else {
                            cm.sendOk("领取失败，失败的原因可能是：\r\n已经领取过，或者栏位不足，需要背包所有窗口在15个位置以上");
                            cm.dispose();
                        }
                    } else {
                        cm.sendOk("您的会员等级无法使用此功能");
                        cm.dispose();
                    }
                } else if (selection == 5) { //VIP4巨无霸
                    if (cm.getChar().getVip() >= 4) {
                        if (cm.getBossLog("VIP4巨无霸", 1) == 0 && (cm.getSpace(1) > 5 && cm.getSpace(2) > 5 && cm.getSpace(2) > 5)) {
                            cm.gainItem(3010417, +1); //VIP勋章
                            cm.setBossLog("VIP4巨无霸", 1);
                            cm.sendOk("恭喜,领取#rVIP4#k#z3010417#");
                            cm.dispose();
                        } else {
                            cm.sendOk("领取失败，失败的原因可能是：\r\n已经领取过，或者栏位不足，需要背包所有窗口在5个位置以上");
                            cm.dispose();
                        }
                    } else {
                        cm.sendOk("您的会员等级无法使用此功能");
                        cm.dispose();
                    }
                }
            } else if (selection == 2) { //每日打工
                cm.dispose();
                cm.openNpc(9010030, 2);
            } else if (selection == 3) { //随机皇家发型
                cm.dispose();
                cm.openNpc(1012117);
            } else if (selection == 4) { //刷新正义之章	
                cm.dispose();
                cm.openNpc(9010030, 4);
            } else if (selection == 5) { //武器增加属性	
                cm.dispose();
                cm.openNpc(9010030, 5);
            } else if (typed == 101) {
                if (cm.haveItem(4000054, typesitemcost[selection]) == true) {
                    cm.gainItem(typesitem[selection], 1);
                    cm.gainItem(4000054, -typesitemcost[selection]);
                    cm.sendOk("恭喜你，换取成功，快去查看一下包包吧.");
                    cm.dispose();
                } else {
                    cm.sendOk("对不起，你没有足够的脚趾.");
                    cm.dispose();
                }
            } else if (typed == 166) {
                cm.delRing(cm.getText());
            } else if (typed == 2) {
                fee = cm.getText();
                cm.sendYesNo("你确定要下注 #r" + fee + "#k 冒险币吗?请先检查你有没有那么多钱哦!");
            } else if (typed == 5) {
                fee = cm.getText();
                if (fee < 0) {
                    cm.sendOk("你输入的数负数!");
                    cm.dispose();
                } else if (!cm.haveItem(4002002, fee * 2)) {
                    cm.sendOk("兑换失败，你没有" + fee * 2 + "张木妖邮票。");
                    cm.dispose();
                } else {
                    cm.gainItem(4002002, -fee * 2); //木妖邮票
                    cm.gainItem(4031454, fee); //圣杯
                    cm.sendOk("恭喜，兑换成功。");
                    cm.dispose();
                }
            } else if (typed == 8) {
                typed = 8;
		/*var selStr= "以下为当时段出售物品,每样物品限购一件\r\n";
		selStr += "#L1##v1432086#　#d需要#r 1 #d金币　限量出售#r 5 #d把　已出售#r "+cm.getBossLogCount("922商品1",2)+" #d把#k\r\n";
		selStr += "#L2##v1432086#　#d需要#r 1 #d金币　限量出售#r 5 #d把　已出售#r "+cm.getBossLogCount("922商品2",2)+" #d把#k\r\n";
		selStr += "#L3##v1432086#　#d需要#r 1 #d金币　限量出售#r 5 #d把　已出售#r "+cm.getBossLogCount("922商品3",2)+" #d把#k\r\n";
		selStr += "#L4##v1432086#　#d需要#r 1 #d金币　限量出售#r 5 #d把　已出售#r "+cm.getBossLogCount("922商品4",2)+" #d把#k\r\n";
		cm.sendSimpleS(selStr,2);*/
                cm.sendOk("商店暂时关闭中。。");
                cm.dispose();
            } else if (typed == 1) {
                typed = 2;
                cm.sendGetText("请输入你要购买的天数,1消费币/1天(最低10天)");
            } else if (typed == 28) {
                typed = 29;
                fee = cm.getText();
                fee1 = 100000000 * fee;
                cm.sendYesNo("你确定要兑换 #r" + fee1 + "#k 游戏币吗?这需要花费你#r" + fee + "木妖邮票");
            } else if (typed == 26) {
                if (selection == 0) {
                    if (player.GetMoney() < 5000) {
                        cm.sendOk("抱歉你没有5000点卷无法购买纪念币\r\n您可以直接点击登录器上面的购买秋爽币，然后进行充值，注意充值时候人物必须下线，成功后找我旁边的大刀NPC购买点卷！");
                        cm.dispose();
                    } else {
                        player.GainMoney(-5000);
                        cm.gainItem(4001129, 1);
                        cm.sendOk("啊哦，纪念币啊，是纪念币！恭喜领取成功，扣除5000点卷.");
                        cm.dispose();
                    }
                } else if (selection == 3) {
                    if (cm.getMeso() >= 500000000) {
                        cm.sendOk("恭喜恭喜，换取成功！");
                        cm.gainItem(4001129, 1);
                        cm.gainMeso(-500000000);
                        cm.dispose();
                    } else {
                        cm.sendNext("对不起，你没有5E游戏币");
                        cm.dispose();
                    }
                } else if (selection == 1) {
                    if (cm.haveItem(4000054, 30) == true) {
                        cm.sendOk("恭喜恭喜，换取成功！");

                        cm.gainItem(4000054, -30);
                        cm.gainItem(4001129, 1);
                        cm.dispose();
                    } else {
                        typed = 230;
                        cm.sendNext("对不起，你没有足够的狼人脚趾,此物品的样子像#v4000054#.\r\n#r点击下一步传送到狼人地图挑战狼人!");
                        //cm.dispose();
                    }
                } else if (selection == 2) {
                    cm.warp(211040800, 0);
                    cm.dispose();
                }
            }
        } else if (status == 3) {
            if (typed == 1) {
                var p = cm.getChar();
                var totAp = p.getRemainingAp();
                if (selection == 1) { //V2
                    if (cm.getVip() >= 2) {
                        cm.sendOk("抱歉，你的会员等级为Vip" + cm.getVip() + ",已经不能再购买了!");
                        cm.dispose();
                    } else if (cm.getHyPay(1) < 10000) {
                        cm.sendOk("抱歉，你没足够的消费币！.");
                        cm.dispose();
                    } else {
                        cm.setVip(2, 365);
                        cm.addHyPay(10000);
                        p.setRemainingAp(totAp + 200);
                        p.levelUp(); //使用这个来刷新当前角色的状态
                        cm.sendOk("恭喜你成功购买VIP2");
                        cm.getC().getChannelServer().broadcastPacket(Packages.tools.MaplePacketCreator.serverNotice(0x09, cm.getC().getChannel(), "『Vip』" + " : " + "恭喜" + cm.getChar().getName() + "成为本服Vip2玩家,大家一起祝贺吧!"));
                        cm.dispose();
                    }
                } else if (selection == 2) { //V3
                    if (cm.getVip() >= 3) {
                        cm.sendOk("抱歉，你的会员等级为Vip" + cm.getVip() + ",已经不能再购买了!");
                        cm.dispose();

                    } else if (cm.getHyPay(1) < 50000) {
                        cm.sendOk("抱歉，你没足够的消费币！.");
                        cm.dispose();
                    } else {
                        cm.setVip(3, 365);
                        cm.addHyPay(50000);
                        p.setRemainingAp(totAp + 400);
                        p.levelUp(); //使用这个来刷新当前角色的状态
                        cm.sendOk("恭喜你成功购买VIP3");
                        cm.getC().getChannelServer().broadcastPacket(Packages.tools.MaplePacketCreator.serverNotice(0x09, cm.getC().getChannel(), "『Vip』" + " : " + "恭喜" + cm.getChar().getName() + "成为本服Vip3玩家,大家一起祝贺吧!"));
                        cm.dispose();
                    }
                } else if (selection == 3) { //V4
                    if (cm.getVip() >= 4) {
                        cm.sendOk("抱歉，你的会员等级为Vip" + cm.getVip() + ",已经不能再购买了!");
                        cm.dispose();
                    } else if (cm.getHyPay(1) < 100000) {
                        cm.sendOk("抱歉，你没足够的消费币！.");
                        cm.dispose();
                    } else {
                        cm.setVip(4, 365);
                        cm.addHyPay(100000);
                        p.setRemainingAp(totAp + 600);
                        p.levelUp(); //使用这个来刷新当前角色的状态
                        cm.sendOk("恭喜你成功购买VIP4");
                        cm.getC().getChannelServer().broadcastPacket(Packages.tools.MaplePacketCreator.serverNotice(0x09, cm.getC().getChannel(), "『Vip』" + " : " + "恭喜" + cm.getChar().getName() + "成为本服Vip4玩家,大家一起祝贺吧!"));
                        cm.dispose();
                    }
                } else if (selection == 4) { //V5
                    if (cm.getVip() >= 5) {
                        cm.sendOk("抱歉，你的会员等级为Vip" + cm.getVip() + ",已经不能再购买了!");
                        cm.dispose();
                    } else if (cm.getHyPay(1) < 150000) {
                        cm.sendOk("抱歉，你没足够的消费币！.");
                        cm.dispose();
                    } else {
                        cm.setVip(5, 365);
                        cm.addHyPay(150000);
                        p.setRemainingAp(totAp + 800);
                        p.levelUp(); //使用这个来刷新当前角色的状态
                        cm.sendOk("恭喜你成功购买VIP5");
                        cm.getC().getChannelServer().broadcastPacket(Packages.tools.MaplePacketCreator.serverNotice(0x09, cm.getC().getChannel(), "『Vip』" + " : " + "恭喜" + cm.getChar().getName() + "成为本服Vip5玩家,大家一起祝贺吧!"));
                        cm.dispose();
                    }
                } else if (selection == 5) { //V2~3
                    if (cm.getVip() != 2) {
                        cm.sendOk("抱歉，你现在不是Vip2.");
                        cm.dispose();
                    } else if (cm.getHyPay(1) < 50000) {
                        cm.sendOk("抱歉，你没足够的消费币！.");
                        cm.dispose();
                    } else {
                        cm.setVip(3, 365);
                        cm.addHyPay(50000);
                        p.setRemainingAp(totAp + 200);
                        p.levelUp(); //使用这个来刷新当前角色的状态
                        cm.sendOk("恭喜你成功升级为VIP3");
                        cm.getC().getChannelServer().broadcastPacket(Packages.tools.MaplePacketCreator.serverNotice(0x09, cm.getC().getChannel(), "『Vip』" + " : " + "恭喜" + cm.getChar().getName() + "升级成为本服Vip3玩家,大家一起祝贺吧!"));
                        cm.dispose();
                    }
                } else if (selection == 6) { //V3~4
                    if (cm.getVip() != 3) {
                        cm.sendOk("抱歉，你现在不是Vip3.");
                        cm.dispose();
                    } else if (cm.getHyPay(1) < 50000) {
                        cm.sendOk("抱歉，你没足够的消费币！.");
                        cm.dispose();
                    } else {
                        cm.setVip(4, 365);
                        cm.addHyPay(50000);
                        p.setRemainingAp(totAp + 400);
                        p.levelUp(); //使用这个来刷新当前角色的状态
                        cm.sendOk("恭喜你成功升级为VIP4");
                        cm.getC().getChannelServer().broadcastPacket(Packages.tools.MaplePacketCreator.serverNotice(0x09, cm.getC().getChannel(), "『Vip』" + " : " + "恭喜" + cm.getChar().getName() + "升级成为本服Vip4玩家,大家一起祝贺吧!"));
                        cm.dispose();
                    }
                } else if (selection == 7) { //V4~5
                    if (cm.getVip() != 4) {
                        cm.sendOk("抱歉，你现在不是Vip4.");
                        cm.dispose();
                    } else if (cm.getHyPay(1) < 50000) {
                        cm.sendOk("抱歉，你没足够的消费币！.");
                        cm.dispose();
                    } else {
                        cm.setVip(5, 365);
                        cm.addHyPay(50000);
                        p.setRemainingAp(totAp + 600);
                        p.levelUp(); //使用这个来刷新当前角色的状态
                        cm.sendOk("恭喜你成功升级购买VIP5");
                        cm.getC().getChannelServer().broadcastPacket(Packages.tools.MaplePacketCreator.serverNotice(0x09, cm.getC().getChannel(), "『Vip』" + " : " + "恭喜" + cm.getChar().getName() + "升级成为本服Vip5玩家,大家一起祝贺吧!"));
                        cm.dispose();
                    }
                } else if (selection == 8) { //V4~5
                    if (cm.getVip() >= 2) {
                        cm.sendOk("抱歉，你的会员等级为Vip" + cm.getVip() + ",已经不能再购买了!");
                        cm.dispose();
                    } else if (cm.getPlayer().getReborns() < 5) {
                        cm.sendOk("抱歉，你还没有达到5次转生，继续努力！.");
                        cm.dispose();
                    } else {
                        cm.setVip(2, 365);
                        p.setRemainingAp(totAp + 200);
                        p.levelUp(); //使用这个来刷新当前角色的状态
                        cm.sendOk("恭喜你成功购买VIP2");
                        cm.getC().getChannelServer().broadcastPacket(Packages.tools.MaplePacketCreator.serverNotice(0x09, cm.getC().getChannel(), "『Vip』" + " : " + "恭喜" + cm.getChar().getName() + "成为本服Vip2玩家,大家一起祝贺吧!"));
                        cm.dispose();
                    }
                }
            } else if (typed == 8) {
                if (selection == 1) { //第一种商品
                    var count = cm.getBossLog("922商品1",1);
                    if (cm.getBossLogCount("922商品1",1) >= 1) {//第一个商品判定购买过没
                        cm.sendOk("抱歉，你已购买过此商品了,已经不能再购买了!");
                        cm.dispose();
                    } else if (cm.getBossLogCount("922商品1",2) >= 5) {//第一个商品判定是否有货
                        cm.sendOk("抱歉，此商品以售完！.");
                        cm.dispose();
                    } else if (cm.getMeso() < 1) {//第一个商品判定是否够金币
                        cm.sendOk("抱歉，你没有足够的金币");
                        cm.dispose();
                    } else {
                        cm.gainMeso(-1);
                        cm.gainItem(1432086, 1);
                        cm.setBossLog("922商品1",1,count+1);
                        cm.sendOk("恭喜你成功淘到了一个#v1432086#");
                        cm.worldMessage("[VIP专属商店]：恭喜玩家[" + cm.getChar().getName() + "]在市场史蒂夫处淘到了<狮心长枪-lv.140>");
                        cm.dispose();
                    }
                } else if (selection == 2) { //第二种商品
                    var count = cm.getBossLog("922商品2",1);
                    if (cm.getBossLogCount("922商品2",1) >= 1) {//第二个商品判定购买过没
                        cm.sendOk("抱歉，你已购买过此商品了,已经不能再购买了!");
                        cm.dispose();
                    } else if (cm.getBossLogCount("922商品2",2) >= 5) {//第二个商品判定是否有货
                        cm.sendOk("抱歉，此商品以售完！.");
                        cm.dispose();
                    } else if (cm.getMeso() < 1) {//第二个商品判定是否够金币
                        cm.sendOk("抱歉，你没有足够的金币");
                        cm.dispose();
                    } else {
                        cm.gainMeso(-1);
                        cm.gainItem(1432086, 1);
                        cm.setBossLog("922商品2",1,count+1);
                        cm.sendOk("恭喜你成功淘到了一个#v1432086#");
                        cm.worldMessage("[VIP专属商店]：恭喜玩家[" + cm.getChar().getName() + "]在市场史蒂夫处淘到了<狮心长枪-lv.140>");
                        cm.dispose();
                    }
                } else if (selection == 3) { //第三种商品
                    var count = cm.getBossLog("922商品3",1);
                    if (cm.getBossLogCount("922商品3",1) >= 1) {//第三个商品判定购买过没
                        cm.sendOk("抱歉，你已购买过此商品了,已经不能再购买了!");
                        cm.dispose();
                    } else if (cm.getBossLogCount("922商品3",2) >= 5) {//第三个商品判定是否有货
                        cm.sendOk("抱歉，此商品以售完！.");
                        cm.dispose();
                    } else if (cm.getMeso() < 1) {//第三个商品判定是否够金币
                        cm.sendOk("抱歉，你没有足够的金币");
                        cm.dispose();
                    } else {
                        cm.gainMeso(-1);
                        cm.gainItem(1432086, 1);
                        cm.setBossLog("922商品3",1,count+1);
                        cm.sendOk("恭喜你成功淘到了一个#v1432086#");
                        cm.worldMessage("[VIP专属商店]：恭喜玩家[" + cm.getChar().getName() + "]在市场史蒂夫处淘到了<狮心长枪-lv.140>");
                        cm.dispose();
                    }
                } else if (selection == 4) { //第四种商品
                    var count = cm.getBossLog("922商品4",1);
                    if (cm.getBossLogCount("922商品4",1) >= 1) {//第四个商品判定购买过没
                        cm.sendOk("抱歉，你已购买过此商品了,已经不能再购买了!");
                        cm.dispose();
                    } else if (cm.getBossLogCount("922商品4",2) >= 5) {//第四个商品判定是否有货
                        cm.sendOk("抱歉，此商品以售完！.");
                        cm.dispose();
                    } else if (cm.getMeso() < 1) {//第四个商品判定是否够金币
                        cm.sendOk("抱歉，你没有足够的金币");
                        cm.dispose();
                    } else {
                        cm.gainMeso(-1);
                        cm.gainItem(1432086, 1);
                        cm.setBossLog("922商品4",1,count+1);
                        cm.sendOk("恭喜你成功淘到了一个#v1432086#");
                        cm.worldMessage("[VIP专属商店]：恭喜玩家[" + cm.getChar().getName() + "]在市场史蒂夫处淘到了<狮心长枪-lv.140>");
                        cm.dispose();
                    }
                }
            } else if (typed == 29) {
                meso1 = cm.getChar().getMeso();
                if (fee <= 0 || fee > 21) {
                    cm.sendOk("亏你想得出来，不要乱数");
                    cm.dispose();
                } else if (cm.haveItem(4002002, fee) < true || (meso1 + fee1) > 2147483647) {
                    cm.sendOk("抱歉，你没足够的蜗牛邮票！\r\n或者你的背包已经装不下这么多钱了");
                    cm.dispose();
                } else {
                    cm.gainMeso(+fee1);
                    cm.gainItem(4002002, -fee);
                    cm.sendOk("兑换成功.");
                    cm.dispose();
                }
            } else if (typed == 230) {
                cm.warp(211040800, 0);
                cm.dispose();
            } else if (typed == 2) {
                if (playerchoice == "rock" && compchoice == "rock") {
                    cm.sendOk(Frock + spacing + rock + draw);
                    drawmatch = true;
                    cm.gainMeso(-cost);
                } else if (playerchoice == "rock" && compchoice == "paper") {
                    cm.sendOk(Frock + spacing + paper + lose);
                    losematch = true;
                    cm.gainMeso(-cost);
                    cm.serverNotice("『娱乐公告』：哇.可怜的" + cm.getChar().getName() + "，在石头剪刀布活动中失败了，大家一起为他祈祷吧");
                } else if (playerchoice == "rock" && compchoice == "scissor") {
                    cm.sendOk(Frock + spacing + scissor + win);
                    winmatch = true;
                    cm.gainMeso(winmesos);
                    cm.serverNotice("『娱乐公告』：恭喜" + cm.getChar().getName() + "，在石头剪刀布活动中胜利了！");
                    //cm.gainItem(items[Math.floor(Math.random() * items.length)],1);
                } else if (playerchoice == "paper" && compchoice == "rock") {
                    cm.sendOk(Fpaper + spacing + rock + win);
                    winmatch = true;
                    cm.gainMeso(winmesos);
                    //cm.gainItem(items[Math.floor(Math.random() * items.length)],1);
                    cm.serverNotice("『娱乐公告』：恭喜" + cm.getChar().getName() + "，在石头剪刀布活动中胜利了！");
                } else if (playerchoice == "paper" && compchoice == "paper") {
                    cm.sendOk(Fpaper + spacing + paper + draw);
                    drawmatch = true;
                    cm.gainMeso(-cost);
                } else if (playerchoice == "paper" && compchoice == "scissor") {
                    cm.sendOk(Fpaper + spacing + scissor + lose);
                    losematch = true;
                    cm.gainMeso(-cost);
                    cm.serverNotice("『娱乐公告』：哇.可怜的" + cm.getChar().getName() + "，在石头剪刀布活动中失败了，大家一起为他祈祷吧");
                } else if (playerchoice == "scissor" && compchoice == "rock") {
                    cm.sendOk(Fscissor + spacing + rock + lose);
                    losematch = true;
                    cm.gainMeso(-cost);
                    cm.serverNotice("『娱乐公告』：哇.可怜的" + cm.getChar().getName() + "，在石头剪刀布活动中失败了，大家一起为他祈祷吧");
                } else if (playerchoice == "scissor" && compchoice == "paper") {
                    cm.sendOk(Fscissor + spacing + paper + win);
                    winmatch = true;
                    cm.gainMeso(winmesos);
                    cm.serverNotice("『娱乐公告』：恭喜" + cm.getChar().getName() + "，在石头剪刀布活动中胜利了！");
                    //cm.gainItem(items[Math.floor(Math.random() * items.length)],1);
                } else if (playerchoice == "scissor" && compchoice == "scissor") {
                    cm.sendOk(Fscissor + spacing + scissor + draw);
                    drawmatch = true;
                    cm.gainMeso(-cost);
                } else {
                    cm.sendOk("出错啦...");
                }
            } else {
                if (cm.getMeso() < fee) {
                    cm.sendOk("哦呵，不好意思你没那么多钱了，去赚点钱再来吧，这可不是免费的!");
                    cm.dispose();
                } else if (cm.getMeso() >= 1050000000) {
                    cm.sendOk("对不起,你的金币大于10.5亿,所以不能参于此次下注!");
                    cm.dispose();
                } else if (cm.getText() < 0) {
                    cm.sendOk("亏你想得出来，居然输入负数，一边去!");
                    cm.dispose();
                } else if (chance <= 1) {
                    cm.gainMeso(-fee);
                    cm.sendNext("哦··你的运气不怎么好哦··哈哈 再来一把嘛!");
                    cm.serverNotice("『赌场公告』：哇.可怜的" + cm.getChar().getName() + "，在赌场输个精光，大家一起为他祈祷吧！");
                    cm.dispose();
                }
                else if (chance == 2) {
                    cm.gainMeso(-fee);
                    cm.sendNext("哦··你的运气不怎么好哦··哈哈 再来一把嘛!");
                    cm.serverNotice("『赌场公告』：哇.可怜的" + cm.getChar().getName() + "，在赌场输个精光，大家一起为他祈祷吧！");
                    cm.dispose();
                }
                else if (chance == 3) {
                    cm.gainMeso(-fee);
                    cm.sendNext("哦··你的运气不怎么好哦··哈哈 再来一把嘛!");
                    cm.serverNotice("『赌场公告』：哇.可怜的" + cm.getChar().getName() + "，在赌场输个精光，大家一起为他祈祷吧！");
                    cm.dispose();
                }
                else if (chance >= 4) {
                    cm.gainMeso(fee * 1);
                    cm.sendNext("#r哈哈，恭喜你#k! 你赢了! 看来你手气不错再来一把哈!");
                    cm.serverNotice("『赌场公告』：恭喜" + cm.getChar().getName() + "，在赌场赢得大量金币，大家一起为他祝贺吧！");
                    cm.dispose();
                }
            }
        } else if (status == 4) {
            if (typed == 1) {

            } else if (typed == 76) {
                cm.gainItem(5200002, -1);
                cm.makeRing(cm.getText(), 1112801);
                cm.sendOk("恭喜，创建成功!");
                cm.dispose();
            } else if (typed == 77) {
                cm.gainItem(5200002, -1);
                cm.makeRing(cm.getText(), 1112802);
                cm.sendOk("恭喜，创建成功!");
                cm.dispose();
            } else {
                cm.dispose();
            }
        }

    }
}

function getVipInfo() {
    var text = "\r\n";

    text += "#r#e会员等级1#n#k-免费\r\n";
    text += "#b会员功能：#r1.#d免费换所有发型(除皇家外)\r\n";
    text += "#b会员工资：#d1000W冒险币+2个圣杯(转生道具)\r\n";
    text += "#g=================================================#k\r\n";

    text += "#r#e会员等级2#n#k-5次转生可领取\r\n";
    text += "#b会员功能：#d#r1.#k会员独有个性上线提示,聊天字体\r\n　　　　　#r2.#k随即更换皇家发型#r(106版本)#k\r\n　　　　　#r3.#k专用练级地图\r\n　　　　　#r4.#k市场双倍泡点修为\r\n"
    text += "#b会员工资：#d5000点券+1E冒险币+4个圣杯(转生道具)#k\r\n";
    text += "#b会员礼包：#d#v2340000##z2340000#x10#v5064000##z5064000#x10\r\n#v2900019#(随即椅子)x1#v1112673#(全属性100)x1#v5062000##z5062000#x5\r\n\r\n";
    text += "#g=================================================#k\r\n";

    text += "#r#e会员3(白银)#n#k-30000消费币\r\n";
    text += "#b会员功能：#d#r1.#k会员独有个性上线提示,聊天字体\r\n　　　　　#r2.#k随即更换皇家发型#r(106版本)#k\r\n　　　　　#r3.#k每天给武器增加全属性30\r\n　　　　　#r4.#k每天可摇VIP抽奖机1次\r\n　　　　　#r4.#k专用练级地图\r\n　　　　　#r5.#k市场三倍泡点修为\r\n";
    text += "#b会员工资：#d1w点券+3E冒险币+6个圣杯(转生道具)\r\n　　　　　3种情景喇叭各1个-白银VIP专属喇叭2个\r\n";
    text += "#b会员礼包：#d#v2340000##z2340000#x30#v5064000##z5064000#x30\r\n#v4031217#(自选GM卷)x10#v2900019#(随即椅子)x2#v1112673#(属性100)x1\r\n#v5062000##z5062000#x20#v1112787#(属性500)x1#v3700071##z3700071#x1\r\n#v3010509##z3010509#x1#v1003698##z1003698#x1\r\n#v1112140##z1112140##v1112247##z1112247#\r\n#v3010410##v3010409##v3010075##v3010073##v3010069#\r\n";
    text += "#g=================================================#k\r\n";

    text += "#r#e会员4(黄金)#n#k-60000消费币\r\n";
    text += "#b会员功能：#d#r1.#k会员独有个性上线提示,聊天字体\r\n　　　　　#r2.#k自主更换皇家发型#r(106版本)#k\r\n　　　　　#r3.#k每天给武器增加全属性50\r\n　　　　　#r4.#k每天可摇VIP抽奖机2次\r\n　　　　　#r5.#k每天随即领取140装备1样(全属性100-200)\r\n　　　　　#r6.#k市场四倍泡点修为\r\n　　　　　#r7.#k专用练级地图\r\n";
    text += "#b会员工资：#d2W点券+4E冒险币+10个圣杯(转生道具)\r\n 　　　　 3种情景喇叭各2个-黄金VIP专属喇叭2个\r\n";
    text += "#b会员礼包：：#d#v2340000##z2340000#x50#v5064000##z5064000#x50\r\n#v4031217#(自选GM卷)x20#v2900019#(随即椅子)x2#v1112673#(属性100)x1\r\n#v5062000##z5062000#x30#v1112786#(属性800)x1#v3700070##z3700070#x1\r\n#v3010508##z3010508#x1#v1003697##z1003697#x1\r\n#v1112139##z1112139##v1112246##z1112246#\r\n#v3010410##v3010409##v3010075##v3010073##v3010069##v3010031##v3010032##v3010033#\r\n";
    text += "#g=================================================#k\r\n";

    text += "#r#e会员5(钻石)#n#k-120000消费币\r\n";
    text += "#b会员功能：#d#r1.#k会员独有个性上线提示,聊天字体\r\n　　　　　#r2.#k自主更换皇家发型#r(106版本)#k\r\n　　　　　#r3.#k每天给武器增加全属性100\r\n　　　　　#r4.#k每天可摇VIP抽奖机3次\r\n　　　　　#r5.#k每天随即领取140装备2样(全属性100-200)\r\n　　　　　#r6.#k市场五倍泡点修为\r\n　　　　　#r7.#k专用练级地图\r\n　　　　　#r8.#k每天可以提升转身上限20次\r\n";
    text += "#b会员工资：#d4W点券+10E冒险币+15个圣杯(转生道具)\r\n 　　　　 3种情景喇叭各4个-钻石VIP专属喇叭2个\r\n";
    text += "#b会员礼包：：#d#v2340000##z2340000#x100#v5064000##z5064000#x100\r\n#v4031217#(自选GM卷)x40#v2900019#(随即椅子)x2#v1112673#(属性100)x1\r\n#v5062000##z5062000#x30#v1112785#(属性1000)x1#v3700069##z3700069#x1\r\n#v3010507##z3010507#x1#v1003696##z1003696#x1\r\n#v1112138##z1112138##v1112245##z1112245#\r\n#v3010410##v3010409##v3010075##v3010073##v3010069##v3010030##v3010029##v3010031##v3010032##v3010033#\r\n";
    cm.sendOkS(text,3);
    cm.dispose();
}
