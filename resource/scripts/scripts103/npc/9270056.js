var status = 0;
var choice;
var scrolls = Array(3010209,3010096,3010044,3010046,3010036,3010068,3010057,3010058,3010069,3010073,3010075,3010077,3010085,3010092,3010094,3010096,3010098,3010099,3010106,3010110,3010111,3010112,3010116,3010117,3010123,3010124,3010125,3010127,3012002,3010146,3010136);
/*
* 魔力冒险岛
*/
function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1)
        cm.dispose();
    else {
        if (status == 0 && mode == 0) {
            cm.dispose();
            return;
        } else if (status >= 1 && mode == 0) {
            cm.sendOk("好吧，欢迎下次继续光临！.");
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;

        if (status == 0) {
            var choices = "\r\n ★ 当前账户剩余:#r"+player.GetMoney()+"元宝 #k★#k\r\n元宝比例#r[1rmb=2000元宝]： 100版椅子 ";
            for (var i = 0; i < scrolls.length; i++) {
                choices += "\r\n#L" + i + "##v" + scrolls[i] + "##t" + scrolls[i] + "##l";
            }
            cm.sendSimple("#b欢迎来到"+cm.GetSN()+" ,你想买我们商店的物品么？？请选择吧，每个需要10000元宝：." + choices);
        } else if (status == 1) {
            cm.sendYesNo("#b你确定需要购买这个物品么？这将花费你10000元宝！！#k" +"\r\n#v" + scrolls[selection] + "##t" + scrolls[selection] + "#");
            choice = selection;
        } else if (status == 2) {
            if(cm.getChar().GetMoney() >= 10000) {
                player.GainMoney(-10000); 
                cm.gainItem(scrolls[choice], 1);
                cm.sendOk("谢谢你的光顾，你购买的物品已经放入你的背包！.");
                cm.dispose();
            } else {
                cm.sendOk("抱歉，你没足够的钱！.");
                cm.dispose();
            }
        }
    }
}
