var status = 0;
var choice;
var scrolls = Array(1102238,1102241,1102248,1102245,1102265,1102285,1102286,1102287,1102270,1102273,1102288,1102253,1102298,1102299,1102297,1102310,1102319,1102272,1102323,1102324,1102349,1102325,1102326,1102338,1102350,1102374,1102353,1102357,1102382,1102383);
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
            var choices = "\r\n当前账户剩余:#r"+player.GetMoney()+"元宝 #k★#k\r\n元宝比例#r[1rmb=2000元宝]";
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
