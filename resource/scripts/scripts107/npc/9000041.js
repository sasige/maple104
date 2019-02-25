importPackage(net.sf.odinms.client);
importPackage(net.sf.odinms.tools);
importPackage(net.sf.odinms.server);

var status = 0;
var fee = 0;
var chance = Math.floor(Math.random() * 4 + 1);
var typed = 0;

var compchoice;
var playerchoice;
var Frock = "#fUI/UIWindow.img/RpsGame/Frock#";
var Fpaper = "#fUI/UIWindow.img/RpsGame/Fpaper#";
var Fscissor = "#fUI/UIWindow.img/RpsGame/Fscissor#";
var rock = "#fUI/UIWindow.img/RpsGame/rock#";
var paper = "#fUI/UIWindow.img/RpsGame/paper#";
var scissor = "#fUI/UIWindow.img/RpsGame/scissor#";
var win = "#fUI/UIWindow.img/RpsGame/win#";
var lose = "#fUI/UIWindow.img/RpsGame/lose#";
var draw = "#fUI/UIWindow.img/RpsGame/draw#";
var spacing = "                                   ";
var beta = "#fUI/UIWindow.img/BetaEdition/BetaEdition#\r\n";
var winmatch = false;
var losematch = false
var drawmatch = false;
var cost = 100000; //需要多少钱玩，输多少.. 或者自己改。。
var winmesos = 100000000; //赢钱，或者自己改
var items = new Array(2000005); //以此类推，这些东西是你想给玩家赢后的随机奖励，我写的这些是我乱添加的，你们要自己改掉，我不知道有没有这些东西..
var selectedType = -1;
var selectedItem = -1;
var map = 910000000;
var textedd = new Array("快乐游戏祝你找到真心朋友", "祝你在游戏内幸福快乐", "快乐游戏祝你找到真心朋友");

var types = new Array("圣杯", "祝福卷轴", "耳环智力诅咒卷轴", "全身铠甲敏捷卷轴", "鞋子敏捷诅咒卷轴", "手套敏捷诅咒卷轴", "手套攻击诅咒卷轴", "单手剑攻击诅咒卷轴", "短剑攻击诅咒卷轴", "短杖魔力诅咒卷轴", "长杖魔力诅咒卷轴", "双手剑攻击诅咒卷轴", "双手斧攻击诅咒卷轴", "双手钝器攻击诅咒卷轴", "枪攻击诅咒卷轴", "矛攻击诅咒卷轴", "弓攻击诅咒卷轴", "弩攻击诅咒卷轴", "拳套攻击诅咒卷轴");
var typesitem = new Array("4031454", "2340000", "2040303", "2040506", "2040709", "2040806", "2040807", "2043003", "2043303", "2043703", "2043803", "2044003", "2044103", "2044203", "2044303", "2044403", "2044503", "2044603", "2044703");
var typesitemcost = new Array("100", "541", "2000", "1400", "999", "888", "2000", "888", "1000", "888", "1200", "1250", "777", "730", "1300", "1210", "2600", "1200", "700");


var typeszq = new Array("1302063", "1302049", "1332021", "1302036", "1302037", "1302058", "1302065", "1302067", "1302071", "1302080", "1302001", "1312012", "1322006", "1322051", "1322053", "1372015", "1372017", "1372031", "1432009", "1432013", "1432045", "1432046", "1442046", "1402014", "5010062");
var typesitemzqcost = new Array("4999", "19999", "29999", "6999", "3888", "3888", "9288", "9999", "3666", "14150", "2999", "2999", "999", "19999", "8888", "999", "11199", "19999", "9999", "4999", "999", "8299", "4999", "29999", "29999");

var typeslb = new Array("5390000", "5390001", "5390002", "5072000");
var typesitemlbcost = new Array("200000", "200000", "200000", "2000000");


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
            var text = "#L3##b石头剪刀布[练级累了,轻松一刻]\r\n";
            //text += "#L8#自残术[为情受挫,活不下去了,求解脱]\r\n";
            //text +="#L9##b变性术[一秒改变性别,hold住整个岛]#k#l\r\n";
            text += "#L24##b游戏币购买物品[喇叭,圣杯]#l\r\n";
            //text +="#L29##r使用市场修为换取星岩箱子#b[New]#k#l\r\n";
            text += "#L26##r使用蘑菇奖牌换取合成星岩石#b[New]#k#l\r\n";
            text += "#L22##b使用木妖邮票换取最新点装[商场没有的]#l\r\n";
            text += "#L100##b使用枫叶换取GM卷,圣杯[平民玩家也BT]#r[Hot]#k#l\r\n";
            //text +="#L12##b使用2张木妖邮票换取圣杯[转身必须品]\r\n";
            //text += "#L25##b使用能力值兑换物品[防暴,魔方,椅子,稀有点装]#l\r\n";
            text += "#d#L6#[换取]木妖邮票换1000万#l#L21##d[换取]1000万换木妖邮票#l\r\n";
            text += "#d#L27#[换取]枫叶换对应游戏币#l#L28##d[换取]脚趾换对应游戏币#l\r\n";
            //text +="#L10##d[换取]15E换蜗牛邮票#l#L11#[换取]蜗牛邮票换15E#l\r\n";
            //text +="#L13#[换取]100脚趾换1亿#l #L14#[换取]100枫叶换10亿#k";
            cm.sendSimple(text);
        } else if (status == 1) {
            if (selection == 6) {
                typed = 28;
                cm.sendGetText("请输入你要购兑换的数量#z4002002#=1000W游戏币");
            } else if (selection == 21) {
                typed = 27;
                cm.sendGetText("请输入你要购兑换的数量1000W游戏币=#z4002002#x1");
            } else if (selection == 27) {
                typed = 29;
                cm.sendGetText("请输入你要购兑换游戏币的数量1个#z4001126#=20W游戏币");
            } else if (selection == 28) {
                typed = 30;
                cm.sendGetText("请输入你要购兑换游戏币的数量1个#z4000054#=10W游戏币");
            } else if (selection == 29) {
                typed = 31;
                cm.sendGetText("请输入你要购兑换星岩箱子的数量1个#z2430692#=市场修为200点");
            } else if (selection == 22) {
                cm.dispose();
                cm.openNpc(9000041, 1);
            } else if (selection == 24) {
                cm.dispose();
                cm.openNpc(9000041, 2);
            } else if (selection == 25) {
                cm.dispose();
                cm.openNpc(9000041, 3);
            } else if (selection == 26) {
                cm.dispose();
                cm.openNpc(9000041, 4);
            } else if (selection == 100) {
                typed = 101;
                selStr = "#r\r\n以下的卷都是100%的成功机率，被大家熟称为GM卷轴，如果你带来了足够的枫叶就可以找我换取#b";
                for (var i = 0; i < typesitem.length; i++) {
                    selStr += "\r\n#L" + i + "##z" + typesitem[i] + "##d需要#r[" + typesitemcost[i] + "个]#d枫叶#b";
                }
                cm.sendSimpleS(selStr, 2);
            } else if (selection == 7) {
                if (cm.getMeso() >= 15000000) {
                    cm.sendOk("恭喜，兑换成功。");
                    cm.gainItem(4002000, 1);
                    cm.gainMeso(-15000000);
                    cm.dispose();
                } else {
                    cm.sendOk("兑换失败，你没有足够的金币。");
                    cm.dispose();
                }
            } else if (selection == 8) {
                //var statup = new java.util.ArrayList();
                var p = cm.getChar();
                p.getStat().setHp(0,p);
                p.getStat().setMp(0,p);
                //statup.add(new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.HP, java.lang.Integer.valueOf(0)));
                //statup.add(new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.MP, java.lang.Integer.valueOf(0)));
                //p.getClient().getSession().write(net.sf.odinms.tools.MaplePacketCreator.updatePlayerStats(statup, p));
		var str = p.getStat().getStr();
		var dex =p.getStat().getDex();
		var _int =p.getStat().getInt();
		var luk = p.getStat().getLuk();
		cm.resetStats(str,dex,_int,luk);
                cm.worldMessage("『爱情宣言』：可怜的" + cm.getChar().getName() + "，情场失意，在市场惨烈自杀，大家安慰安慰它吧。");
                cm.dispose();
            } else if (selection == 9) {
                if (cm.haveItem(4002000, 10) == true) {
                    cm.changeSex();
                    cm.sendOk("#b恭喜你，手术非常的成功，扣除10张邮票\r\n#r做人妖的感觉是不是很郁闷！现在你可以正儿八经的说自己是真正的男人或者是女人了.全服公告我就不发了，你自个乐去啦.");
                    cm.gainItem(4002000, -10);
                    cm.dispose();
                } else {
                    cm.sendOk("对不起你没有10张蜗牛邮票.目前手续费需要蜗牛张邮票.");
                    cm.dispose();
                }
            } else if (selection == 10) {
                if (cm.getMeso() >= 150000000) {
                    cm.gainItem(4002000, 1); //蜗牛邮票
                    cm.gainMeso(-15000000);
                    cm.sendOk("恭喜，兑换成功。");
                    cm.dispose();
                } else {
                    cm.sendOk("兑换失败，你没有足够的金币。");
                    cm.dispose();
                }
            } else if (selection == 11) {
                if (cm.getMeso() <= 6000000) {
                    if (cm.haveItem(4002000, 1)) {
                        cm.sendOk("恭喜，兑换成功。");
                        cm.gainItem(4002000, -1); //木妖邮票
                        cm.gainMeso(15000000);
                        cm.dispose();
                    } else {
                        cm.sendOk("兑换失败，你没有1张蜗牛邮票。");
                        cm.dispose();
                    }
                } else {
                    cm.sendOk("对不起,钱最多只能带21E,你身上的钱超过了6E!");
                    cm.dispose();
                }
            } else if (selection == 13) {
                if (cm.haveItem(4000054, 100)) {
                    cm.sendOk("恭喜，兑换成功。");
                    cm.gainItem(4000054, -100); //脚趾
                    cm.gainMeso(10000000);
                    cm.dispose();
                } else {
                    cm.sendOk("兑换失败，你没有100个狼人脚趾");
                    cm.dispose();
                }
            } else if (selection == 14) {
                if (cm.haveItem(4001126, 100)) {
                    cm.sendOk("恭喜，兑换成功。");
                    cm.gainItem(4001126, -100); //木妖邮票
                    cm.gainMeso(10000000);
                    cm.dispose();
                } else {
                    cm.sendOk("兑换失败，你没有100个枫叶");
                    cm.dispose();
                }
            } else if (selection == 12) {
                typed = 5;
                cm.sendGetText("请输入你要购兑换圣杯的数量!2张#v4002002#换一个#v4031454#");
            } else if (selection == 1) {


            } else if (selection == 2) {
                typed = 2;
                cm.sendGetText("你想下注多少金额呢?如果赢了的话可以获得双倍的收益,哈哈快输入金额吧!");

            } else if (selection == 3) {
                if (cm.getMeso() >= 10000000) {
                    typed = 1;
                    cm.sendSimple("注意，胜利将获得1E奖励，失败则失去1E...\r\n" + "#L0##fUI/UIWindow.img/RpsGame/Frock##l" + "#L1##fUI/UIWindow.img/RpsGame/Fpaper##l" + "#L2##fUI/UIWindow.img/RpsGame/Fscissor##l");
                } else {
                    cm.sendOk("对不起,你没有1E游戏币!");
                    cm.dispose();
                }
            } else if (selection == 5) {
                typed = 5;
                var selStr = "你想要挑战你的队友吗？你够强大吗?我是#e#r1对1#k#nPk系统管理员.我可以提供给你单独的一个房间跟你的朋友较量.怎么样?\r\n#e#bPs:(7-12)为1对1PK房间#k#n";
                var pvproom = new Array("\r\n" + cm.getPvpRoom(map + 07, 07), cm.getPvpRoom(map + 08, 08) + "\r\n", cm.getPvpRoom(map + 09, 09), cm.getPvpRoom(map + 10, 10) + "\r\n", cm.getPvpRoom(map + 11, 11), cm.getPvpRoom(map + 12, 12));
                for (var i = 0; i < pvproom.length; i++) {
                    selStr += "" + pvproom[i] + "";
                }
                cm.sendSimple(selStr);
            }
        } else if (status == 2) {
            if (typed == 1) {
                if (selection == 0) {
                    playerchoice = "rock";
                } else if (selection == 1) {
                    playerchoice = "paper";
                } else if (selection == 2) {
                    playerchoice = "scissor";
                }
                var random = Math.floor(Math.random() * 4);
                if (random <= 1) {
                    compchoice = "rock";
                } else if (random <= 2) {
                    compchoice = "paper";
                } else if (random <= 4) {
                    compchoice = "scissor";
                }
                typed = 2;
                cm.sendNext("这次的结果是..."); //这里可以改
            } else if (typed == 44) {
                if (cm.haveItem(4000082, typesitemzqcost[selection]) == true) {
                    cm.gainItem(typeszq[selection], 1);
                    cm.gainItem(4000082, -typesitemzqcost[selection]);
                    cm.worldMessage("[赌博NPC]：恭喜~玩家:" + cm.getChar().getName() + "成功使用金牙换购了一个特殊玩具~");
                    cm.sendOk("恭喜你，换取成功，快去查看一下包包吧.");
                    cm.dispose();
                } else {
                    cm.sendOk("对不起，你没有足够的金牙#v4000082#.");
                    cm.dispose();
                }
            } else if (typed == 45) {
                if (cm.haveItem(5200002, typesitemlbcost[selection]) == true) {
                    cm.gainItem(typeslb[selection], 3);
                    cm.gainItem(5200002, -typesitemlbcost[selection]);
                    cm.sendOk("恭喜你，购买成功，快去查看一下包包吧.");
                    cm.dispose();
                } else {
                    cm.sendOk("对不起，你没有足够的金袋子.");
                    cm.dispose();
                }
            } else if (typed == 101) {
                if (cm.haveItem(4001126, typesitemcost[selection]) == true) {
                    cm.gainItem(typesitem[selection], 1);
                    cm.gainItem(4001126, -typesitemcost[selection]);
                    cm.sendOk("恭喜你，换取成功，快去查看一下包包吧.");
                    cm.dispose();
                } else {
                    cm.sendOk("对不起，你没有足够的枫叶.");
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
            } else if (typed == 73) {
                typed = 74;
                cm.sendSimple("请选择你要创建的戒指类型：\r\n #b#L0#四叶挚友戒指#l\r\n#L1#雏菊挚友戒指#l \r\n#L2#闪星挚友戒指#l");
            } else if (typed == 27) {
                typed = 28;
                fee = cm.getText();
                fee1 = 10000000 * fee;
                cm.sendYesNo("你确定要兑换 #r" + fee + "#k 张木妖邮票吗?\r\n这需要花费你#r" + fee1 + "游戏币");
            } else if (typed == 28) {
                typed = 29;
                fee = cm.getText();
                fee1 = 10000000 * fee;
                cm.sendYesNo("你确定要兑换 #r" + fee1 + "#k 游戏币吗?这需要花费你#r" + fee + "木妖邮票");
            } else if (typed == 29) {
                typed = 30;
                fee = cm.getText();
                fee1 = 200000 * fee;
                cm.sendYesNo("你确定要兑换 #r" + fee1 + "#k 游戏币吗?\r\n这需要花费你#r" + fee + "枫叶");
            } else if (typed == 30) {
                typed = 31;
                fee = cm.getText();
                fee1 = 100000 * fee;
                cm.sendYesNo("你确定要兑换 #r" + fee1 + "#k 游戏币吗?\r\n这需要花费你#r" + fee + "脚趾");
            } else if (typed == 31) {
                typed = 32;
                fee = cm.getText();
                fee1 = 200 * fee;
                cm.sendYesNo("你确定要兑换 #r" + fee + "#k 星岩箱子吗?\r\n这需要花费你#r" + fee1 + "市场修为");
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
                    if (cm.getMeso() >= 50000000) {
                        cm.sendOk("恭喜恭喜，换取成功！");
                        cm.gainItem(4001129, 1);
                        cm.gainMeso(-50000000);
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
            if (typed == 74) {
                if (selection == 0) {
                    if (cm.haveItem(5200002) == true) {
                        typed = 75;
                        cm.sendGetText("请输入对方的名字，如果你打不出对方的名字，那么你可以右键点人物，然后点击名字储存，你就把对好的名字复制好了，在到这里按ctrl+v就粘贴好了.\r\n#r提醒：名字输入错了，可能钱，财，人全部空.后果自已负责。");
                    } else {
                        cm.sendOk("对不起你没有金袋子.目前手续费需要一个.");
                        cm.dispose();
                    }
                } else if (selection == 1) {
                    if (cm.haveItem(5200002) == true) {
                        typed = 76;
                        cm.sendGetText("请输入对方的名字，如果你打不出对方的名字，那么你可以右键点人物，然后点击名字储存，你就把对好的名字复制好了，在到这里按ctrl+v就粘贴好了.\r\n#r提醒：名字输入错了，可能钱，财，人全部空.后果自已负责。");
                    } else {
                        cm.sendOk("对不起你没有金袋子.目前手续费需要一个.");
                        cm.dispose();
                    }
                } else if (selection == 2) {
                    if (cm.haveItem(5200002) == true) {
                        typed = 77;
                        cm.sendGetText("请输入对方的名字，如果你打不出对方的名字，那么你可以右键点人物，然后点击名字储存，你就把对好的名字复制好了，在到这里按ctrl+v就粘贴好了.\r\n#r提醒：名字输入错了，可能钱，财，人全部空.后果自已负责。");
                    } else {
                        cm.sendOk("对不起你没有金袋子.目前手续费需要一个.");
                        cm.dispose();
                    }
                }
            } else if (typed == 28) {
                if (cm.getMeso() < fee1) {
                    cm.sendOk("抱歉，你没足够的钱！.");
                    cm.dispose();
                } else if (fee <= 0) {
                    cm.sendOk("亏你想得出来，居然输入负数，一边去!");
                    cm.dispose();
                } else {
                    cm.gainMeso(-fee1);
                    cm.gainItem(4002002, fee);
                    cm.sendOk("兑换成功.");
                    cm.dispose();
                }
            } else if (typed == 29) {
                meso1 = cm.getChar().getMeso();
                if (fee <= 0 || fee > 210) {
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
            } else if (typed == 30) {
                meso1 = cm.getChar().getMeso();
                if (fee <= 0 || fee > 10500) {
                    cm.sendOk("亏你想得出来，不要乱数");
                    cm.dispose();
                } else if (cm.haveItem(4001126, fee) < true || (meso1 + fee1) > 2147483647) {
                    cm.sendOk("抱歉，你没足够的#z4001126#！\r\n或者你的背包已经装不下这么多钱了");
                    cm.dispose();
                } else {
                    cm.gainMeso(+fee1);
                    cm.gainItem(4001126, -fee);
                    cm.sendOk("兑换成功.");
                    cm.dispose();
                }
            } else if (typed == 31) {
                meso1 = cm.getChar().getMeso();
                if (fee <= 0 || fee > 21000) {
                    cm.sendOk("亏你想得出来，不要乱数");
                    cm.dispose();
                } else if (cm.haveItem(4000054, fee) < true || (meso1 + fee1) > 2147483647) {
                    cm.sendOk("抱歉，你没足够的#z4000054#！\r\n或者你的背包已经装不下这么多钱了");
                    cm.dispose();
                } else {
                    cm.gainMeso(+fee1);
                    cm.gainItem(4000054, -fee);
                    cm.sendOk("兑换成功.");
                    cm.dispose();
                }
            } else if (typed == 32) {
                meso1 = cm.getChar().getMeso();
                if (fee <= 0 || fee > 100) {
                    cm.sendOk("一次最多兑换100个");
                    cm.dispose();
                } else if (cm.getChar().getXw() > fee1 || cm.getSpace(2) > 1) {
                    cm.sendOk("抱歉，你没足够的市场修为\r\n或者你的背包没空格");
                    cm.dispose();
                } else {
                    cm.gainItem(2430692, +fee);
                    cm.getPlayer().setXw(cm.getPlayer().getXw() - fee1);
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
                    cm.worldMessage("『娱乐公告』：哇.可怜的" + cm.getChar().getName() + "，在石头剪刀布活动中失败了，大家一起为他祈祷吧");
                } else if (playerchoice == "rock" && compchoice == "scissor") {
                    cm.sendOk(Frock + spacing + scissor + win);
                    winmatch = true;
                    cm.gainMeso(winmesos);
                    cm.worldMessage("『娱乐公告』：恭喜" + cm.getChar().getName() + "，在石头剪刀布活动中胜利了！");
                    //cm.gainItem(items[Math.floor(Math.random() * items.length)],1);
                } else if (playerchoice == "paper" && compchoice == "rock") {
                    cm.sendOk(Fpaper + spacing + rock + win);
                    winmatch = true;
                    cm.gainMeso(winmesos);
                    //cm.gainItem(items[Math.floor(Math.random() * items.length)],1);
                    cm.worldMessage("『娱乐公告』：恭喜" + cm.getChar().getName() + "，在石头剪刀布活动中胜利了！");
                } else if (playerchoice == "paper" && compchoice == "paper") {
                    cm.sendOk(Fpaper + spacing + paper + draw);
                    drawmatch = true;
                    cm.gainMeso(-cost);
                } else if (playerchoice == "paper" && compchoice == "scissor") {
                    cm.sendOk(Fpaper + spacing + scissor + lose);
                    losematch = true;
                    cm.gainMeso(-cost);
                    cm.worldMessage("『娱乐公告』：哇.可怜的" + cm.getChar().getName() + "，在石头剪刀布活动中失败了，大家一起为他祈祷吧");
                } else if (playerchoice == "scissor" && compchoice == "rock") {
                    cm.sendOk(Fscissor + spacing + rock + lose);
                    losematch = true;
                    cm.gainMeso(-cost);
                    cm.worldMessage("『娱乐公告』：哇.可怜的" + cm.getChar().getName() + "，在石头剪刀布活动中失败了，大家一起为他祈祷吧");
                } else if (playerchoice == "scissor" && compchoice == "paper") {
                    cm.sendOk(Fscissor + spacing + paper + win);
                    winmatch = true;
                    cm.gainMeso(winmesos);
                    cm.worldMessage("『娱乐公告』：恭喜" + cm.getChar().getName() + "，在石头剪刀布活动中胜利了！");
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
                    cm.worldMessage("『赌场公告』：哇.可怜的" + cm.getChar().getName() + "，在赌场输个精光，大家一起为他祈祷吧！");
                    cm.dispose();
                }
                else if (chance == 2) {
                    cm.gainMeso(-fee);
                    cm.sendNext("哦··你的运气不怎么好哦··哈哈 再来一把嘛!");
                    cm.worldMessage("『赌场公告』：哇.可怜的" + cm.getChar().getName() + "，在赌场输个精光，大家一起为他祈祷吧！");
                    cm.dispose();
                }
                else if (chance == 3) {
                    cm.gainMeso(-fee);
                    cm.sendNext("哦··你的运气不怎么好哦··哈哈 再来一把嘛!");
                    cm.worldMessage("『赌场公告』：哇.可怜的" + cm.getChar().getName() + "，在赌场输个精光，大家一起为他祈祷吧！");
                    cm.dispose();
                }
                else if (chance >= 4) {
                    cm.gainMeso(fee * 1);
                    cm.sendNext("#r哈哈，恭喜你#k! 你赢了! 看来你手气不错再来一把哈!");
                    cm.worldMessage("『赌场公告』：恭喜" + cm.getChar().getName() + "，在赌场赢得大量金币，大家一起为他祝贺吧！");
                    cm.dispose();
                }
            }



        } else if (status == 4) {
            if (typed == 75) {
                cm.gainItem(5200002, -1);
                cm.makeRing(cm.getText(), 1112800);
                cm.sendOk("恭喜，创建成功!");
                cm.dispose();
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