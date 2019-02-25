var status = 0;
var choice;
var scrolls = Array(1002888,1002890,1002846,1003133,1002839,1050152,1051180,1050119,1051131,1003249,1003250,1003251,1003252,1003253,1003254,1003255,1003256,1062074,1102391,1000044,1001064,1003222,1003141,1042208,1042151,1042104,1042193,1042198,1052224,1052275,1003109,1052296,1002501,1003009,1003013,1003077,1003147,1003192,1003193,1003194,1003220,1112100,1112209,1112118,1112119,1112120,1112228,1112229,1112230,1112916,1112135,1112238,1112926,1112925,1102245,1102233,1102377,1102378,1092108,1702309,1702342,1702302,1702303,1702305,1702284,1702285,1702266,1702261);
var scrolls1 = Array(250,250,200,250,250,150,150,100,100,200,200,200,200,200,200,200,200,150,100,150,150,150,250,200,100,100,150,150,150,150,150,200,100,150,150,150,150,150,150,150,150,200,200,200,200,200,200,200,200,200,300,300,200,200,200,200,250,250,100,250,250,200,200,200,200,200,200,200);
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
            var choices = "";
            for (var i = 0; i < scrolls.length; i++) {
                choices += "\r\n#L" + i + "##v" + scrolls[i] + "##z" + scrolls[i] + "#　#d需要#r" + scrolls1[i] + "张#d#z4002002##k#l";
            }
            cm.sendSimpleS(choices,2);
        } else if (status == 1) {
            cm.sendYesNo("#b你确定需要购买这个物品么？这将花费你" + scrolls1[selection] + "张#z4002002#！！#k" +"\r\n#v" + scrolls[selection] + "##t" + scrolls[selection] + "#");
            choice = selection;
        } else if (status == 2) {
            if(cm.haveItem(4002002,scrolls1[choice])) {
                cm.gainItem(4002002,-scrolls1[choice]);//木妖
                cm.gainItem(scrolls[choice], 1);
                cm.sendOk("谢谢你的光顾，你购买的物品已经放入你的背包！.");
                cm.dispose();
            } else {
                cm.sendOk("抱歉，你没足够的#z4002002#.");
                cm.dispose();
            }
        }
    }
}
