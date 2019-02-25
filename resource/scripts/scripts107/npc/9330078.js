

var minPlayers = 1;
var maxPlayers = 6;
var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);

}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0 && status == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            if (cm.haveItem(4032923)){
                cm.sendSimple("你已经有钥匙了，不能重复获取：\r\n\r\n#L6#希纳斯的庭院#l ");
            }else{ 
                cm.sendSimple("你想到精灵之地去吗？没有#v4032923##b#z4032923##k是进不去的，那里面有许多的精灵，据说在那里可以得到希纳斯的庭院的钥匙？为了和平，请继续努力。#b\r\n\r\n#L1#灵魂之地#l \r\n#L2#火焰之地#l \r\n#L3#风暴之地#l \r\n#L4#黑暗之地#l \r\n#L5#闪电之地#l \r\n#r#L7#查看帮助#l#k");
            }
        } else if (status == 1) {
            if (selection == 1) {
                cm.warp(271030201,0);
                cm.dispose();
            }else if (selection == 2) {
                cm.warp(271030202,0);
                cm.dispose();      
            }else if (selection == 3) {
                cm.warp(271030203,0);
                cm.dispose(); 
            }else if (selection == 4) {
                cm.warp(271030204,0);
                cm.dispose(); 
            }else if (selection == 5) {
                cm.warp(271030205,0);
                cm.dispose(); 
            }else if (selection == 6) {
                cm.warp(271040000,0);
                cm.dispose();
		   }else if (selection == 7) {
			   cm.sendOk("请分别进入以上5个地图收集以下足够的道具\r\n\r\n#v4000660# x10个#v4000661# x10个 #v4000662# x10个#v4000663# x10个 #v2430200# x1个\r\n收集满以后，使用在闪电之地收集到的#v2430200##r双击打开，自动会合成钥匙#v4032923#");
			   cm.dispose();
            }
        }
    }
}