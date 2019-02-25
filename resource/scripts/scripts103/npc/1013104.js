var qian = 20000000;
var status = 0;
var choice;
var scrolls = Array(2049100,2043402,2045202,2045206,2045306,2040016,2040100,2040105,2040200,2040205,2040310,2040412,2040612,2040816,2040915,2040920,2041201,2041206,2043008,2040302,2043107,2043002,2040802,2040820,2040822,2041206,2043008,2043012,2043207,2043302,2043307,2043807,2044002,2044107,2044302,2044402,2044507,2044602,2044702);
/*
* 
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
            var choices = "\r\n以下是你可以选择的购买物品: ";
            for (var i = 0; i < scrolls.length; i++) {
                    choices += "\r\n#L" + i + "##v" + scrolls[i] + "##t" + scrolls[i] + "##l";
            }
            cm.sendSimple("欢迎来到卷轴商店#k ,你想买我们商店的物品么？？请选择吧，每个需要"+qian+" 冒险币.：." + choices);
      } else if (status == 1) {
           cm.sendGetText("请输入你要购买的数量.您当前拥有#b"+cm.getMeso()+"#k金币.");
            choice = selection;
        } else if (status == 2) {
       sm = cm.getText();
      mesos =qian*sm;
             cm.sendYesNo("你确定需要购买#v" + scrolls[choice] + "##t" + scrolls[choice] + "# "+sm+"件吗？这将花费你 "+mesos+" 冒险币！！" +"是否继续?");
            status1 = choice;
        } else if (status == 3) {
            if (cm.getMeso() >= mesos) {
                cm.gainMeso(-mesos);
                cm.gainItem(scrolls[status1], sm);
                cm.sendOk("谢谢你的光顾，你购买的物品已经放入你的背包！.");
                cm.dispose();
            } else {
                    cm.sendOk("抱歉，你没足够的钱！.");
                    cm.dispose();
              
            }
        }
    }
}
