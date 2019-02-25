/* ===========================================================
			注释
	脚本类型: 		QUEST
	脚本名字:		召唤幼龙
=============================================================
制作时间：2010年8月5日 21:40:06
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
			qm.sendNext("什么？你是不相信我吗？哦哦哦～我发火了！");
			qm.dispose();
			return;
		}
	}
	if (status == 0)
		qm.sendNext("我终于醒来了！呵～这就是世界上的空气！哦，那个就是太阳！那是树！那是草！那是花！真了不起！比我在蛋里想想的更加漂亮！还有……嗯？你是我的主人吗？怎么好像和我期待的有点不一样？");
	if (status == 1)
		qm.sendNextPrev("#b哇啊啊啊啊啊啊！你会说话？！",2);
	if (status == 2)
		qm.sendNextPrev("……我的主人真是个奇怪的人。不过已经签订了契约，我也不能选择其他主人了。今后还请多多关照。");
	if (status == 3)
		qm.sendNextPrev("#b嗯？这是什么意思？今后请多多关照……？契约？那是什么？",2);
	if(status == 4)
		qm.sendNextPrev("你在说什么啊……就是你把我从蛋中唤醒的契约啊。你是我的主人，当然应该照顾我，让我变成强大的龙。不是吗？");
	if (status == 5)
		qm.sendNextPrev("#b嗯？龙？！你是龙吗？你在说什么啊？我完全听不懂！契约到底是什么？主人又是怎么回事？",2);
	if (status == 6)
		qm.sendNextPrev("嗯？你在说什么啊？你不是和我签订了将龙和人的灵魂合二为一的契约吗？所以你就是我的主人。你连这都不知道，就签了契约？但是现在已经晚了，契约已经无法解开了。");
	if (status == 7)
		qm.sendNextPrev("#b啊？等，等等！虽然我不是很明白，不过听你这么说……我必须得无条件地照顾你吗？",2);
	if (status == 8)
		qm.sendNextPrev("那当然！……嗯？干嘛？那副委屈的表情？你不想成为我的主人吗？");
	if (status == 9)
		qm.sendNextPrev("#b不，不是不愿意，而是不知道怎么养宠物。",2);
	if (status == 10)
		qm.sendNextPrev("宠，宠物～？！你把我当作宠物？！你把我当成什么了？再怎么说，我也是世界上最强的生命体——玛瑙龙！");
	if (status == 11)
		qm.sendNextPrev("#b……#b(再怎么看，也只是条小蜥蜴而已。)#k",2);
	if (status == 12)
		qm.sendAcceptDecline("干嘛？那种眼神？你是觉得我像条小蜥蜴吗！哎呀，受不了啦！我来证明我的力量给你看！好了，你做好准备了吗？");
	if (status == 13){
		qm.forceStartQuest();
		qm.sendNext("快向我下达攻击#r#o1210100##k的命令！那样的话，我就去攻击#o1210100#给你看看！那样你就会看到龙的能力！好的，突击！");
	}if (status == 14)
		qm.sendNextPrev("不，等等！在这之前，你分配好AP（属性点）了吗？我会受到主人的#b智力和运气#k的影响！如果你想看到我真正的能力，就先把AP分配好，#b装备好魔法师装备#k，然后向我下达命令！");
	if (status == 15){
		//qm.evanTutorial("UI/tutorial/evan/11/0", -1);
		qm.dispose();
	}
}

function end(mode, type, selection) {
    /*status++;
	if (mode != 1) {
	    if(type == 1 && mode == 0)
		    status -= 2;
		else{
		    qm.dispose();
			return;
		}
	}
	if(status == 0)
		qm.sendOk("Ha! What do you think of that?! My skills are amazing, right? You can use them as much as you want. That's what it means to be in a pact with me. Isn't it amazing?");
	if(status == 1){
		qm.forceCompleteQuest();
		qm.gainExp(1270);
		qm.gainSp();
		qm.sendOk("Ohhh... I'm so hungry. I used my energy too soon after being born...");
		qm.dispose();
	}*/
}
