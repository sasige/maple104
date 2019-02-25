var status = 0;
var choice;
var scrolls = Array(1003176,1052318,1082299,1072489,1102279,1482084,1492085,1532018);
/*
* 099单机冒险岛
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
            var choices = "\r\n注意：此类为#r海盗140级系列装备#k,#b如果你不是#r海盗#k,#b请另选\r\n以下是你可以选择的购买物品:  ";
            for (var i = 0; i < scrolls.length; i++) {
                    choices += "\r\n#L" + i + "##v" + scrolls[i] + "##t" + scrolls[i] + "##l";
            }
            cm.sendSimple("#b欢迎来到099单机冒险岛 ,你想买我们商店的物品么？？请选择吧，每个需要30000点券：." + choices);
        } else if (status == 1) {
            cm.sendYesNo("#b你确定需要购买这个物品么？这将花费你30000点券！！#k" +"\r\n#v" + scrolls[selection] + "##t" + scrolls[selection] + "#");
            choice = selection;
        } else if (status == 2) {
	if(cm.getChar().getNX() >= 30000) {
            cm.gainNX(-30000); 
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
