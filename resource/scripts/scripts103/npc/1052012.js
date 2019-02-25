/*一转飞侠NPC

*/


var status = 0;

function start() {
	cm.sendSimple("#e呵呵是的没错。我现在已经不在教授学员了，做些飞侠应该做的事情，我现在很忙有很多任务需要你帮我去完成。#r完成任务后，我会给你一些【飞侠】非常好的东西，记得在装备栏留20个空位哦。#k \r\n #L0#收集#v4000016#（30个）适合等级0-30级#l \r\n #L1#收集#v4000106#（300个）适合等级30-70级#l \r\n #L2#收集#v4000051#（500个）适合等级70-100级#l \r\n #L3#收集#v4000241#（500个）适合等级100-130级#l \r\n\r\n\r\n 任务提示：\r\n 1、#v4000016#在【射手村】训练场打蜗牛得到 \r\n 2、#v4000106#在【玩具城】可以打玩具熊得到\r\n 3、#v4000051#在【冰封雪域】可以打狼得到\r\n 4、#v4000241#在【神木村】可以打绵羊得到");
}
function action(mode, type, selection) {
	cm.dispose();
	if (selection == 0) {
if(cm.haveItem(4000016,30) == true) {
cm.gainItem(4000016,-30); 
cm.gainItem(1332001,1);
cm.gainItem(1472011,1);
cm.gainItem(1002177,1);
cm.gainItem(1082046,1);
cm.gainItem(1072105,1);
cm.gainItem(1040061,1);
cm.gainItem(1041075,1);
cm.gainItem(1060052,1);
cm.gainItem(1061044,1);
cm.sendOk("哇~!HO，这么快就收集到了，这些是给你的，打开包袱看看喜欢吗");
cm.dispose();
} else {
cm.sendOk("你没有所必需的兑换道具，请收集到了在来吧。"); 
cm.dispose(); }
			} else if (selection == 1) {
if(cm.haveItem(4000106,300) == true) {
cm.gainItem(4000106,-300); 
cm.gainItem(1332018,1);
cm.gainItem(1472027,1);
cm.gainItem(1002281,1);
cm.gainItem(1082095,1);
cm.gainItem(1072162,1);
cm.gainItem(1040107,1);
cm.gainItem(1041101,1);
cm.gainItem(1060087,1);
cm.gainItem(1061101,1);

cm.sendOk("哇~!HO，这么快就收集到了，这些是给你的，打开包袱看看喜欢吗");
cm.dispose();
} else {
cm.sendOk("你没有所必需的兑换道具，请收集到了在来吧。"); 
cm.dispose(); }
			} else if (selection == 2) {
if(cm.haveItem(4000051,500) == true) {
cm.gainItem(4000051,-500); 
cm.gainItem(1332023,1);
cm.gainItem(1472031,1);
cm.gainItem(1002327,1);
cm.gainItem(1082120,1);
cm.gainItem(1072173,1);
cm.gainItem(1040109,1);
cm.gainItem(1060098,1);
cm.gainItem(1061106,1);
cm.sendOk("哇~!HO，这么快就收集到了，这些是给你的，打开包袱看看喜欢吗");
cm.dispose();
} else {
cm.sendOk("你没有所必需的兑换道具，请收集到了在来吧。"); 
cm.dispose(); }
			} else if (selection == 3) {
if(cm.haveItem(4000241,500) == true) {
cm.gainItem(4000241,-500); 
cm.gainItem(1332027,1);
cm.gainItem(1472033,1);
cm.gainItem(1002326,1);
cm.gainItem(1082142,1);
cm.gainItem(1072192,1);
cm.gainItem(1040116,1);
cm.gainItem(1040118,1);
cm.gainItem(1060106,1);
cm.gainItem(1061106,1);
cm.sendOk("哇~!HO，这么快就收集到了，这些是给你的，打开包袱看看喜欢吗");
cm.dispose();
} else {
cm.sendOk("你没有所必需的兑换道具，请收集到了在来吧。"); 
cm.dispose(); }
			}
	}