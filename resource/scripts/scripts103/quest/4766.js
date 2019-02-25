var status = -1;

function start(mode, type, selection) {
    if (mode == -1) {
        qm.dispose();
    } else {
        if (mode == 1)
            status++;
        else
            status--;
        if(status == -1){
            qm.sendOk("怎么了？不想要奖励吗？没关系，想好再来。")
            qm.dispose();
        }else if (status == 0) {
                           
            qm.sendAcceptDecline("恭喜你达到40级，感谢你对"+qm.GetSN()+"的支持！\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n\r\n#b#i2430112#  #t2430112#2个\r\n\r\n金币:150000");
        } else if (status == 1) {
            if (qm.gainItem(2430112, 2)){
                qm.gainMeso(150000);
                qm.forceCompleteQuest();
                qm.dispose();
            }
        }
    }
}
