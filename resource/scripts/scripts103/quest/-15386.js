/* ===========================================================
			注释(qm.sendSimple\qm.itemQuantity(5420008))
	脚本类型: 		QUEST
	脚本名字:		
	完成任务函数：qm.forceCompleteQuest();
	接受任务函数：qm.forceStartQuest();
=============================================================
制作时间：
制作人员：笔芯
=============================================================
*/
var a = -1;

function start(mode, type, selection) {//接任务
    if (mode == -1) {
        qm.dispose();
    } else {
        if (mode == 1)
            a++;
        else
            a--;
        if(a == -1){
            qm.dispose();
        }else if (a == 0) {
            if (qm.MissionStatus(qm.getPlayer().getId(),101108000, 0, 4)){//查看接了没
                qm.MissionDelete(qm.getPlayer().getId(), 101108000)
            }
            qm.sendYesNo("目前冒险岛正在进行升级送奖励活动.每次升级都能拿到礼物噢,赶紧去挑战吧.")
        }else if (a == 1) {
            qm.updateQuest(-15386, "0");
            //qm.forceStartQuest(-15386);
            qm.MissionMake(qm.getPlayer().getId(), qm.getLevel(), 0, 0, 0, 0)
            qm.sendOk("每次升级都能拿到饼干.搜集后以后说不定会有什么好事情发生噢...")
            qm.dispose();
        }//status
    }//mode
}//function



function end(mode, type, selection) {//完成
    if (mode == -1) {
        qm.dispose();
    } else {
        if (mode == 1)
            a++;
        else
            a--;
        if(a == -1){
            qm.dispose();
        }else if (a == 0) {
            if (qm.MissionStatus(qm.getPlayer().getId(),qm.getLevel(), 0, 4)){//查看接了没
                qm.sendOk("哎呀?还没升1级啊? 加油升级噢!")
                qm.dispose();
            }else{
                qm.sendNext("哇…升了一级。")
            }
        }else if (a == 1){
            if (!qm.MissionStatus(qm.getPlayer().getId(),101108000, 0, 4)){//查看接了没
                qm.MissionMake(qm.getPlayer().getId(), 101108000, 0, 0, 0, 0)
            }
            if (!qm.MissionStatus(qm.getPlayer().getId(),101108000, 0, 0)){//查看接了没
                qm.sendOk("每次升级只能领取一次，请换频道或者重新登陆再次接受任务。")
                qm.dispose();
            }else{
                if (qm.getLevel() >= 30){
                    qm.gainItem(2430084,10)
                }else if (qm.getLevel() >= 50){
                    qm.gainItem(2430085,10)
                }else if (qm.getLevel() >= 70){
                    qm.gainItem(2430086,10)
                }else if (qm.getLevel() >= 80){
                    qm.gainItem(2430087,10)
                }else{
                    qm.gainItem(2430088,5)
                }
                qm.sendOk("送给你幸运饼干，收集到一定数量之后可以跟我兑换奖品。")
                qm.MissionFinish(qm.getPlayer().getId(), 101108000)
                qm.forceCompleteQuest();
                qm.dispose();
            }
        }//status
    }//mode
}//function
