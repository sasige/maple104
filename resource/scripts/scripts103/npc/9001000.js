/*一转弓箭手NPC

*/


var status = 0;

function start() {
	cm.sendSimple("#b#e[挑战碧拉姐姐]。#r要收集400个[#v4001126#]#k #b#e[挑战碧拉姐姐肯定要付出代价] \r\n\r\n\r\n#b#L2#挑战碧拉姐姐#l\r\n\r\n");
}
function action(mode, type, selection) {
	cm.dispose();
	if (selection == 0) {
        if (!cm.haveItem(4000642,150)) {
        cm.sendOk("抱歉，你没有150个#v4000642#无法为你换取#e#r(请到破坏的射手村收集)");
        } else if (!cm.haveItem(4000643,150)) {
        cm.sendOk("抱歉，你没有150个#v4000643#无法为你换取#e#r(请到破坏的射手村收集)"); 
        } else if (!cm.haveItem(4000644,200)) {
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
        if (!cm.haveItem(4001126,400)) {
        cm.sendOk("抱歉，你没有400个#v4001126#您无法进入地图#e#r)");
        }else{
    cm.warp(931020011, 0);
cm.dispose();}
			}
	}