/*一转弓箭手NPC

*/


var status = 0;

function start() {
	cm.sendSimple("#b#e[在破坏的射手村收集]。#r要收集后才可以换挑战都纳斯证明[#v4032906#]#k #b#e[在破坏的射手村收集] \r\n 收集#v4000642#（150个）\n#v4000643#（150个）\n#v4000644#（188个\r\n\r\n#b#L0#我已经收集了点击换取#l\r\n\r\n#b#L1#还没有收集完成点击进入收集地图#l\r\n\r\n#r#L2#我已经所有东西都准备好了进入BOSS地图#l\r\n\r\n");
}
function action(mode, type, selection) {
	cm.dispose();
	if (selection == 0) {
        if (!cm.haveItem(4000642,150)) {
        cm.sendOk("抱歉，你没有150个#v4000642#无法为你换取#e#r(请到破坏的射手村收集)");
        } else if (!cm.haveItem(4000643,150)) {
        cm.sendOk("抱歉，你没有150个#v4000643#无法为你换取#e#r(请到破坏的射手村收集)"); 
        } else if (!cm.haveItem(4000644,188)) {
        cm.sendOk("抱歉，你没有188个#v4000644#无法为你换取#e#r(请到破坏的射手村收集)"); 
} else {
	cm.gainItem(4000642,-150);
	cm.gainItem(4000643,-150);
	cm.gainItem(4000644,-188);
	cm.gainItem(4032906,1);
cm.sendOk("哇~!HO，这么快就收集到了，赶紧去挑战都纳斯吧。他痒痒呢");
cm.dispose();}
}else if (selection == 1) {
    cm.warp(271010000, 0);
    cm.dispose();
} else if (selection == 2) {
        if (!cm.haveItem(4032906,1)) {
        cm.sendOk("抱歉，你没有#v4032906#您无法进入地图#e#r)");
} else {
    cm.warp(802000311, 0);
cm.sendOk("哇~!HO，这么快就收集到了，这些是给你的，打开包袱看看喜欢吗");
cm.dispose(); }
			}
	}