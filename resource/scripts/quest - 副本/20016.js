/*
	NPC Name: 		Nineheart
	Description: 		Quest - Do you know the black Magician?
*/

var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 8) {
            qm.sendNext("噢，你还有什么问题吗？如果你要跟我对话，请重新打开界面。");
            qm.safeDispose();
            return;
        }
        status--;
    }
    if (status == 0) {
        qm.sendNext("你好, #h0#.欢迎 #p1101000# 骑士。我的名字是 #p1101002# 和我目前作为年轻的皇后的战术。我们最好了解我们会再见到彼此。哈哈！");
    } else if (status == 1) {
        qm.sendNextPrev("我敢肯定你有很多问题，因为一切都发生得太快了。我会解释这一切，一个接一个，从你所在的地方到你在这里要做的.");
    } else if (status == 2) {
        qm.sendNextPrev("这个岛叫做节前。由于皇后的魔术，这个岛通常彩车周围像天空中的巡逻船在枫树世界。现在，然而，我们停在这里是有原因的.");
    } else if (status == 3) {
        qm.sendNextPrev("年轻的皇后是统治者的枫树世界。什么？这是你第一次听说过她？啊，是的。嗯，她是枫树世界统治者的但是她不想控制它。她看着远方来确保一切都好。嗯，至少这是她平常的作用.");
    } else if (status == 4) {
        qm.sendNextPrev("但这不是现在的情况。我们已经发现的迹象都在预示着枫树世界的黑魔法师的复兴。我们不能有黑法师来恐吓枫树世界为他过去!");
    } else if (status == 5) {
        qm.sendNextPrev("但那是很久以前的事，今天的人们没有意识到多么吓人的黑法师。我们都成为了我们今天所享有的世界和平的枫树，忘记了如何混乱和可怕的枫树世界曾经是。如果我们不做些什么，黑法师将再次统治枫树世界!");
    } else if (status == 6) {
        qm.sendNextPrev("这就是为什么年轻的皇后决定自己来处理这件事。她是形成一个骑士勇敢的玩家打败黑魔法师一劳永逸。你知道你需要做的，对吗？我确信你有一个想法，因为你，你自己，签署了一个骑士.");
    } else if (status == 7) {
        qm.sendNextPrev("我们越来越强，所以我们可以打败黑魔法师如果他复活。我们的主要目标是阻止他破坏枫树世界，你将发挥突出的作用.");
    } else if (status == 8) {
        qm.askAcceptDecline("我已经回答了你的问题，你还有什么疑问吗? \r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0# \r\n#fUI/UIWindow.img/QuestIcon/8/0# 380 exp");
    } else if (status == 9) {
        if (qm.getQuestStatus(20016) == 0) {
            qm.gainExp(380);
            qm.forceCompleteQuest();
        }
        qm.sendNext("我很高兴你清楚自己目前的处境，但你知道的，在你的等级，你甚至没有足够坚强去面对黑魔法师的奴才，别说黑法师自己。甚至不是他的奴才的奴才，事实上。你将如何在你的等级保护枫树世界?");
    } else if (status == 10) {
        qm.sendNextPrev("虽然你已经被接受为骑士，你不能被视为一个骑士。你是不是一个正式的骑士，因为即使你不是一个骑士训练。如果你呆在你目前的水平，你就没有比# p1101000 #骑士的勤杂工.");
    } else if (status == 11) {
        qm.sendNextPrev("但是没有人开始一天一个强大的骑士。皇后不想有人强。她想要一个有勇气她可能会发展成一个强大的骑士通过严格的训练。所以，你首先要成为一个骑士训练。我们来谈谈你的任务，当你达到这一点.");
    } else if (status == 12) {
        qm.sendPrev("以门左边达到训练的森林。在那里，你会发现# p1102000 #，训练的老师，他将教你如何变得更强。我不想看到你漫无目的游走直到你到达LV。10，你听见了吗?");
        qm.safeDispose();
    }
}

function end(mode, type, selection) {
}