/*  NPC : Hellin
	Thief 4th job advancement
	Forest of the priest (240010501)
 */





var status = -1;

var newjob;

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 0 && status == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
    if (status == 0) {
       
       newjob = cm.getJob() + 1;


        if (! (cm.getJob() == 411 || cm.getJob() == 421 || cm.getJob() == 433)) {

            cm.sendOk("#r" + cm.getVipname() + " #b���Ѿ�����˵��Ĵ�תְ��\r\n#k������������ʲô����ô��\r\n#rMaplewing  �����������");
            cm.dispose();
            return;

        } else if (cm.getPlayerStat("LVL") < 120) {

            cm.sendOk("#r" + cm.getVipname() + " #b�㻹δ�ﵽ120�����޷����е��Ĵ�תְ��\r\n#d��ﵽ120���Ժ��������ң�");
            cm.dispose();
            return;

        } else {


           cm.sendSimple("#r" + cm.getVipname() + " #k����Ҫ�ٽ�һ������ô?\r\n#b#L0#��Ҫ��Ϊһ��#r " + cm.getJobNameById(newjob) + ".#l\r\n#b#L1#�����ٿ���һ��.#l");

        }


    } else if (status == 1) {
        if (selection == 1) {
            cm.sendOk("#r" + cm.getVipname() + " #k�����׼������,�һ�������е��Ĵ�תְ��.");
            cm.dispose();
            return;
        }
        if (cm.getPlayerStat("RSP") > cm.getPlayerStat("LVL") * 3) { //��һ���̫���SP
            if (cm.getPlayer().getAllSkillLevels() > ((cm.getPlayerStat("LVL") - 9) * 3)) { //��һ���̫���Sp
                cm.sendOk("#r" + cm.getVipname() + "  #k�㻹��δʹ����� SP ����ʹ������������ң�.#n");
                cm.getPlayer().resetSP((cm.getPlayerStat("LVL") - 120) * 3);
            } else {
                cm.sendOk("#r" + cm.getVipname() + "  #k�š����㻹��#b SP #kδʹ�ã���ʹ�������������.");
            }
            cm.dispose();
            return;
        } else {
          cm.changeJob(newjob);
          cm.sendOk("#r" + cm.getVipname() + "  #k ��ϲ���Ѿ������  #d���Ĵ�תְ#k  ��\r\n�������Ѿ���һ�� " + cm.getJobNameById(newjob) + "\r\n#b���Ͱɣ� #r<ʱ��Ů��>����#b  �ڵȴ����㣡\r\n\r\n#e#d���������------��������"); 
          cm.dispose();

}

}

}













/*

var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
    if (status == 0) {
        if (! (cm.getJob() == 411 || cm.getJob() == 421 || cm.getJob() == 433)) {
            cm.sendOk("Why do you want to see me? There is nothing you want to ask me.");
            cm.safeDispose();
            return;
        } else if (cm.getPlayerStat("LVL") < 120) {
            cm.sendOk("You're still weak to go to thief extreme road. If you get stronger, come back to me.");
            cm.safeDispose();
            return;
        } else {
            if (cm.getQuestStatus(6934) == 2 || cm.getJob() == 433) {
                if (cm.getJob() == 411) {
                    cm.sendSimple("You're qualified to be a true thief. \r\nDo you want job advancement?\r\n#b#L0# I want to advance to Night Lord.#l\r\n#b#L1#  Let me think for a while.#l");
                } else if (cm.getJob() == 421) {
                    cm.sendSimple("You're qualified to be a true thief. \r\nDo you want job advancement?\r\n#b#L0# I want to advance to Shadower.#l\r\n#b#L1#  Let me think for a while.#l");
                } else {
                    if (cm.haveItem(4031348) || cm.getQuestStatus(6934) == 2) {
                        cm.sendSimple("You're qualified to be a true thief. \r\nDo you want job advancement?\r\n#b#L0# I want to advance to Dual Master.#l\r\n#b#L1#  Let me think for a while.#l");
                    } else {
                        cm.sendNext("You need the Secret Scroll for 10 million meso.");
                        cm.dispose();
                        return;
                    }
                }
            } else {
                cm.sendOk("You're not ready to make 4th job advancement. When you're ready, talk to me.");
                cm.safeDispose();
                return;
            }
        }
    } else if (status == 1) {
        if (selection == 1) {
            cm.sendOk("You don't have to hesitate.... Whenever you decide, talk to me. If you're ready, I'll let you make the 4th job advancement.");
            cm.safeDispose();
            return;
        }
        if (cm.getPlayerStat("RSP") > (cm.getPlayerStat("LVL") - 120) * 3) { //player have too much SP means they havent assigned to their skills
            if (cm.getPlayer().getAllSkillLevels() > cm.getPlayerStat("LVL") * 3) { //player used too much SP means they have assigned to their skills.. conflict
                cm.sendOk("It appears that you have a great number of SP yet you have used enough SP on your skills already. Your SP has been reset. #ePlease talk to me again to make the job advancement.#n");
                cm.getPlayer().resetSP((cm.getPlayerStat("LVL") - 120) * 3);
            } else {
                cm.sendOk("Hmm...You have too many #bSP#k. You can't make the 4th job advancement with too many SP left.");
            }
            cm.dispose();
            return;
        } else {
            if (cm.getJob() == 411) {
                cm.changeJob(412);
                cm.sendNext("You became the best thief #bNight Lord#k.");
            } else if (cm.getJob() == 421) {
                cm.changeJob(422);
                cm.sendNext("You became the best thief #bShadower#k.");
            } else if (cm.getJob() == 433) {
                if (cm.getQuestStatus(6934) != 2) {
                    cm.gainItem(4031348, -1);
                }
                cm.changeJob(434);
                cm.sendNext("You became the best thief #bDual Master#k.");
            }
        }
    } else if (status == 2) {
        cm.sendNextPrev("Don't forget that it all depends on how much you train.");
    } else if (status == 3) {
        cm.dispose();
    }
}











*/