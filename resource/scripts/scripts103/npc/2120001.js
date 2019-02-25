var status = 0;
var choice;
var scrolls = Array(3010171,3010174,3010183,3010299,3010053,3013000,3013002,3010156,3010128,3010140,3010141,3010151,3010180,3010181,3010184,3010189,3010188,3010220,3010218,3010219,3010212,3010196,3010197,3010200,3010201,3010161,3010222,3010226,3010222,3010282,3010283,3010288,3010195,3010280,3010302,3010303,3010304,3010138,3010311);
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
