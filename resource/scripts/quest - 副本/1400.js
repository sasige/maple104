/* Dawnveil
	The 5 paths 
	Mai
    Made by Daenerys
*/
var status = -1;
var sel = 0;

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 0) {
	    qm.safeDispose();
	    return;
	}
	status--;
    }
    if (status == 0) {
        qm.sendAcceptDecline("嗯,为了你的水准取得良好进展。你决定你想选择什么职业呢?你可能是一个战士的力量和高防御,魔术师有许多法术,从远处射的箭的弓箭手,还有一个飞侠,使用快速、卑鄙的袭击,或与各种浮华的海盗链技能……!");
		qm.startQuest(1400);
	} else if (status == 1) {
        qm.sendSimple("如果你去维多利亚岛,你可以提前到您所选择的职业要合适的职业指导。但是在那之前,让我知道哪一个你感兴趣的,我将发送# bthem # k一封推荐信。这将使你更容易推进!那么,你会选择什么职业?\r\n#b#L0#I 我想成为一名伟大的战士啊!#l\r\n#b#L1#I 我想成为一个神秘的魔术师!#l\r\n#b#L2#I 我想成为一名神射手!#l\r\n#b#L3#I 我想成为一个卑鄙的小偷!#l\r\n#b#L4#I 我想成为一个虚张声势的海盗!#l");
    } else if (status == 2) {
        sel = selection;
	if (selection == 0) {
        qm.sendNext("一个战士,是吗?男孩,你将得到真强!他们可以把成吨的伤害,和菜足够了。好吧,我将发送我的建议与炎# # bDances k,战士教练工作
.");
        } else if (selection == 1) {
		qm.sendNext("你想成为一个魔术师?他们肯定是神秘的!他们的魔法是超级强大,有各种各样的效果。不受到…魔术师并不知道他们的耐力!好吧,我把我的建议# bGrendel真的老# k,魔术师教练工作
.");
        } else if (selection == 2) {
		qm.sendNext("你想成为一个弓箭手?我希望你有非常好的目标!伟大的灵巧,他们没有问题避免攻击和发射了大量的他们自己的。好吧,我把我的建议# bAthena皮尔斯# k,鲍曼的工作指导
.");
        } else if (selection == 3) {
		qm.sendNext("将是一个小偷,是吗?他们如此快速和卑鄙,敌人不看到他们到来,直到为时已晚。真是太酷了!好吧,我将把我的推荐信主# # bDark k,小偷教练工作
.");
        } else if (selection == 4) {
		qm.sendNext("一个海盗吗?Yarr !无论是在枪战或白刃战的争吵,海盗与风格!我觉得你的挑战。好吧,我把我的建议# bKyrin # k,海盗教练工作
.");
        }
    } else if (status == 3) {
	    if (sel == 0) {
		qm.sendNextPrev("他将联系当你到达Lv。10。成为一个伟大的战士
!");
		qm.forceStartQuest(1401);
	    qm.forceCompleteQuest(1400);
		qm.dispose();
	    } else if (sel == 1) {
		qm.sendNext("你知道魔术师有他们的工作比其他工作进步,对吗?格伦德尔真的老会联系你一旦你达到Lv。8。成为一个神奇的魔术师
!");
		qm.forceStartQuest(1402);
		qm.forceCompleteQuest(1400);
		qm.dispose();
		} else if (sel == 2) {
		qm.sendNext("她会联系你一旦你达到# bLv。10 # k。我希望你成为一个宏伟的弓箭手
!");
		qm.forceStartQuest(1403);
		qm.forceCompleteQuest(1400);
		qm.dispose();
		} else if (sel == 3) {
		qm.sendNext("如果你起床# bLv。10 # k,他会联系你。成为一个伟大的小偷,明白了
?");
		qm.forceStartQuest(1404);
		qm.forceCompleteQuest(1400);
		qm.dispose();
		} else if (sel == 4) {
		qm.sendNext("她会联系你一旦你达到# bLv。10 # k。成为一个熟练的海盗
!");
		qm.forceStartQuest(1405);
		qm.forceCompleteQuest(1400);
		qm.dispose();
	   }
	    qm.dispose();
    }
}