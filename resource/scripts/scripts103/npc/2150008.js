var status = 0;
var choice;
var scrolls = Array(1702208,1302037,1302061,1302063,1302080,1302084,1302085,1302087,1302128,1302129,1322051,1332021,1332032,1332053,1372017,1372031,1402037,1402049,1402063,1422011,1432039,1432046,1442026,1442065,1442088,1472063,1702342,1702337,1702335,1702330,1702346,1702341,1702340,1702324,1322102,1412056,1402110,1702310,1702329,1702316,1702309);
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
