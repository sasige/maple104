var status = 0;
var choice;
var scrolls = Array(
Array("雪狼战椅", 3010106, 0), 
Array("浴桶", 3012002, 0), 
Array("呼噜呼噜床", 3010054, 0), 
Array("财神椅子", 3010100, 0), 
Array("暖暖桌", 3010021, 0), 
Array("奶黄包", 3010055, 0), 
Array("风吹稻香", 3010085, 0), 
Array("海盗的俘虏", 3010028, 0), 
Array("摇滚之魂椅子", 3010116, 0), 
Array("猫头鹰椅子", 3010077, 0), 
Array("世界末日", 3010058, 0), 
Array("骷髅宝座", 3010041, 0), 
Array("帐篷", 3010133, 0), 
Array("电视宅人", 3010098, 0), 
Array("我爱巧克力火锅", 3012011, 0), 
Array("巧克力蛋糕恋人", 3012010, 0),
Array("鬼娃娃椅子", 3010085, 0), 
Array("漂漂猪椅子", 3010094, 0), 
Array("北极熊椅子", 3010099, 0), 
Array("圣诞树椅子", 3010048, 0), 
Array("虎虎生威椅子", 3010111, 0), 
Array("魔法书椅子", 3010117, 0), 
Array("暖炉椅", 3010292, 0), 
Array("雪糕丸子椅", 3010055, 0), 
Array("七夕椅子", 3010144, 0), 
Array("龙神椅子", 3010137, 0), 
Array("兔子椅子", 3010186, 0), 
Array("古老录音机椅子", 3010205, 0), 
Array("黑猫椅子", 3010208, 0), 
Array("雪夜椅子", 3010170, 0), 
Array("小幼龙椅子", 3010299, 0), 
Array("兔子纪念版椅子", 3010053, 0), 
Array("蛋糕椅子", 3010141, 0), 
Array("HP椅子", 3010180, 0), 
Array("MP椅子", 3010181, 0), 
Array("无价之宝椅子", 3010195, 0), 
Array("水果椅子", 3010280, 0), 
Array("老奶奶读童话椅子", 3010320, 0), 
Array("购物小推车椅子", 3010361, 0), 
Array("熟睡的鸭子椅", 3010415, 0), 
Array("幻影卡牌椅", 3010401, 0),
Array("情书柜子", 3010112, 0),
Array("糖果音符椅子", 3010118, 0),
Array("都纳斯喷气椅子", 3010124, 0),
Array("喧闹好友椅子", 3010207, 0),
Array("星空椅子", 3010172, 0),
Array("胡萝卜椅子", 3010183, 0),
Array("水族馆椅子", 3010142, 0),
Array("我爱蛋糕椅子", 3010220, 0),
Array("绵羊酋长椅子", 3010219, 0),
Array("月光仙子椅子", 3010226, 0),
Array("动物之家椅子", 3010281, 0),
Array("珍珠蚌椅子", 3010288, 0),
Array("生如夏花椅子", 3010306, 0),
Array("鬼节南瓜椅子", 3010279, 0),
Array("与克里姆享受下午茶", 3010354, 0),
Array("美容椅子", 3010357, 0),
Array("水晶花园椅", 3010400, 0),
Array("希拉的梳妆椅", 3010404, 0),
Array("外形钻机椅子", 3010355, 0),
Array("粉色扎昆椅子", 3010313, 0),
//Array("艾丽莎的双膝", 3010410, 0),

Array("龙龙蛋壳椅", 3010107, 0),
Array("露水椅子", 3010068, 0),
Array("舒适大白熊椅子", 3010110, 0),
//Array("周年庆水晶枫叶椅", 3010145, 0),
Array("大熊猫椅子", 3010078, 0),
Array("肥猫猫椅子", 3010079, 0),
Array("独角兽椅子", 3010135, 0),
Array("诺勒特斯椅子", 3010286, 0),
//Array("噢我的女皇", 3010318, 0),
//Array("埃欧雷的小音乐会", 3010403, 0),
Array("彩虹椅子", 3010362, 0)
//Array("兔子乘风椅", 3010453, 0),
//Array("爱心云朵椅", 3010454, 0)
);

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) cm.dispose();
    else {
        if (status == 0 && mode == 0) {
            cm.dispose();
            return;
        } else if (status >= 1 && mode == 0) {
            cm.sendOk("好吧，欢迎下次继续光临！.");
            cm.dispose();
            return;
        }
        if (mode == 1) status++;
        else status--;

        if (status == 0) {
            choices = "\r\n消费币余额：#r" + cm.getHyPay(1) + "#k个 (#r买后不支持退货哟亲#k)";
            for (var i = 0; i < scrolls.length; i++) {
                choices += "\r\n#L" + i + "##v" + scrolls[i][1] + "##z" + scrolls[i][1] + "#　#d需要#r" + scrolls[i][2] + "#d消费币#k#l";
            }
            cm.sendSimpleS("欢迎来到精品玩具店,你想买我们商店的物品么?请选择吧：." + choices,2);
        } else if (status == 1) {
            cm.sendYesNo("你确定需要购买#v" + scrolls[selection][1] + "##t" + scrolls[selection][1] + "#?");
		choice = selection;
        } else if (status == 2) {
            var money = scrolls[choice][2];
            if (cm.getHyPay(1) < money) {
                cm.sendOk("抱歉，你没足够的消费币！");
                cm.dispose();
            } else {
                cm.addHyPay(money);
                cm.gainItem(scrolls[choice][1], 1);
                cm.sendOk("购买成功.");
                cm.dispose();
            }
        }
    }
}