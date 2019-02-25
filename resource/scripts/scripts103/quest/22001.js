/* ===========================================================
			注释
	脚本类型: 		QUEST
	脚本名字:		喂猎犬
=============================================================
制作时间：2010年8月5日 21:12:58
制作人员：笔芯
=============================================================
*/

importPackage(Packages.client);

var status = -1;

function start(mode, type, selection) {
    status++;
	if (mode != 1) {
	    if(type == 1 && mode == 0)
		    status -= 2;
		else{
			qm.sendNext("你，不愿意去吗？你想看到哥哥我被狗咬吗？快重新和我说话，接受任务！");
			qm.dispose();
			return;
		}
	}
	if (status == 0)
		qm.sendNext("一大早就来开玩笑，哈哈哈。别乱说了，快去给#p1013102#喂饭吧。");
	else if (status == 1)
		qm.sendNextS("#b嗯？那不是#p1013101#的事情吗？",2);
	else if (status == 2)
qm.sendYesNo("你这家伙！快去喂呀！！ #p1013102#有多讨厌我，你也知道。哥哥我去的话，它一定会咬我的。猎犬喜欢你，你去给它送饭。");
	else if (status == 3){
		qm.gainItem(4032447,1);
		qm.sendNext("你快到#b左边#k去，给#b#p1013102##k喂饲料。那个家伙好像肚子饿了，从刚才开始就一直在叫。");
		qm.forceStartQuest();
   }else if (status == 4){
		qm.sendPrev("给#p1013102#喂完食之后，赶快回来。");
		qm.dispose();
	}
}

function end(mode, type, selection) {
    status++;
	if (mode != 1) {
	    if(type == 1 && mode == 0)
		    status -= 2;
		else{
		    qm.dispose();
			return;
		}
	}
	if (status == 0)
		qm.sendNext("#b（把饲料放进 猎犬 的饭盆里。）#k");
	if (status == 1)
		qm.sendOk("#b（猎犬 很乖。 尤塔 真是个胆小鬼。）#k\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 35 经验值");
	if (status == 2){
		qm.forceCompleteQuest();
		qm.gainItem(4032447, -1);
		qm.gainExp(35);
		qm.sendOk("#b( 猎犬 好像把饭都吃光了。回去告诉 尤塔 吧。)#k");
		qm.dispose();
		}
	}
