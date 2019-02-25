/* 
法老王冒险岛  用能力值换装备
*/

var status = 0;
var maps = Array(
Array("绚丽彩虹", 1702155, 3000), Array("酒瓶", 1422011, 13000), Array("冻冻鱼", 1442039, 13000), Array("防暴卷轴", 5064000, 1000),
//Array("龙魂之戒",1112734,2000), 
//Array("银河星群",1702330,10000), 
//Array("高级双弩",1522017,5000), 
Array("白色名片戒指30天", 1112100, 3000), Array("工地手套(褐)", 1082149, 3000), Array("冰蓝蝴蝶卡子", 1003222, 5000),
//Array("皇家海军帽",1002995,2000), 
Array("小兔兔遮阳帽", 1002845, 5000),
//Array("稻草编织帽",1003141,5000), 
Array("大象卫衣", 1042208, 5000), Array("黄格绒线上衣", 1042151, 3000),
//Array("圣诞节套服-女孩",1051221,2000), 
Array("石榴石渡鸦面具", 1003422, 12000), Array("落英飞天丝带", 1102368, 12000), Array("精灵精神发带<女>", 1001077, 12000), Array("精灵精神发带<男>", 1000051, 12000),
//Array("白色名片戒指30天",1112100,1000), 
//Array("[派对]黑框眼镜",1022124,300), 
//Array("蓝色旋律翅膀",1102377,15000), 
Array("普塞克翅膀", 1102378, 15000), Array("巨无霸", 3010070, 30000));
var selectedMap = -1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {

    if (mode == -1) {
        cm.dispose();
    } else {
        if (status >= 3 && mode == 0) {
            cm.sendOk("好的,如果你要什么,我会满足你的.#r 请赶快考虑吧!低价只限今天喔 #k~");
            cm.dispose();
            return;
        }
        if (mode == 1) status++;
        else {
            cm.sendOk("好的,如果你要什么,我会满足你的.");
            cm.dispose();
            return;
        }
        if (status == 0) {
            if (cm.getChar().getName() == "vip3123") {
                cm.sendYesNo("你是大笨蛋么?这样都还不懂!!");
                cm.dispose();
            } else {

                cm.sendNext("正在为您打开商店....\r\n注:你有多的剩余属性点才可以在本店兑换物品!\r\n您现在的剩余属性点为:#r"+cm.getChar().getRemainingAp()+"#k");
            }
        } else if (status == 1) {
            var selStr = "你需要什么东西呢?速度点喔!快选吧!说不定什么时候就下架咯,当然也可能会有新东西上架哦.#b";
            for (var i = 0; i < maps.length; i++) {
                selStr += "\r\n#L" + i + "#" + "#d兑换需要#r" + maps[i][2] + "#k#d能力值#k　　#v" + maps[i][1] + "##z" + maps[i][1] + "#";
            }
            selStr += "#k";
            cm.sendSimpleS(selStr, 2);
        } else if (status == 2) {
            selectedMap = selection;
            cm.sendYesNo("你真的想要 #b#i" + maps[selection][1] + "#" + maps[selection][0] + "#k 吗?这会花费你#r" + maps[selection][2] + "#k属性点");

        }

        else if (status == 3) {
            if (cm.getChar().getRemainingAp() < maps[selectedMap][2] + 1500) {
                cm.sendOk("你没有足够的属性点兑换\r\n或者你有但是扣了后你的属性点就不够转身了\r\n所以每个兑换物品的属性点你必须多出1000才能兑换\r\n但是多出的1500不会扣,只是为了保证你有足够属性转身");
                cm.dispose();
            } else {
                cm.getChar().setRemainingAp(cm.getChar().getRemainingAp() - maps[selectedMap][2]);
                //cm.gainItem(maps[selectedMap][1], 1);
                //var statup = new java.util.ArrayList();

                //statup.add(new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.AVAILABLEAP, java.lang.Integer.valueOf(cm.getChar().getRemainingAp())));
                //cm.getChar().getClient().getSession().write(net.sf.odinms.tools.MaplePacketCreator.updatePlayerStats(statup, cm.c.getPlayer()));
		
		var str = cm.getChar().getStat().getStr();
		var dex =cm.getChar().getStat().getDex();
		var _int =cm.getChar().getStat().getInt();
		var luk = cm.getChar().getStat().getLuk();
		cm.resetStats(str,dex,_int,luk);
		cm.gainGachaponItem(maps[selectedMap][1], 1, "使用能力值兑换物品"); //显示喇叭并把装备放进玩家背包
		cm.sendOk("恭喜你成功兑换并获得#z"+maps[selectedMap][1]+"#!");
                cm.dispose();
            }

        }
    }
}