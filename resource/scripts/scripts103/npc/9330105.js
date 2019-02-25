/*一转弓箭手NPC

*/


var status = 0;

function start() {
	cm.sendSimple("#b#e本关需要#r收集各1个#v3994060##v3994067##v3994065##v3994060##v3994059##v3994072##v 3994065#\r\n\r\n\r\n#b#L2#进入下一关#l\r\n\r\n");
}
function action(mode, type, selection) {
	cm.dispose();
	if (selection == 0) {
        if (!cm.haveItem(3994060,2)) {
        cm.sendOk("抱歉，你没有#v3994060#无法为进入下一个.亲 请您继续收集)");
        } else if (!cm.haveItem(3994067,1)) {
        cm.sendOk("抱歉，你没有#v3994067#无法进入下一个.亲 请您继续收集)"); 
        } else if (!cm.haveItem(3994065,2)) {
        cm.sendOk("抱歉，你没有#v3991006#无法进入下一个.亲 请您继续收集)"); 
        } else if (!cm.haveItem(3994059,1)) {
        cm.sendOk("抱歉，你没有#v3991000#无法进入下一个.亲 请您继续收集)"); 
        } else if (!cm.haveItem(3994072,1)) {
        cm.sendOk("抱歉，你没有#v3991013#无法进入下一个.亲 请您继续收集"); 
} else {
	cm.gainItem(3991001,-2);
	cm.gainItem(3991006,-2);
	cm.gainItem(3991008,-1);
	cm.gainItem(3991000,-1);
	cm.gainItem(3991013,-1);
	cm.gainItem(3994039,1);
        cm.warp(749020900, 0);
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