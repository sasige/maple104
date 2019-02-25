/* ===========================================================
			注释(cm.sendSimple\cm.itemQuantity(5420008))
	脚本类型: 		QUEST
	脚本名字:		发现了佳佳丢失的眼镜！
	完成任务函数：qm.forceCompleteQuest();
	接受任务函数：qm.forceStartQuest();
=============================================================
制作时间：2010年8月22日 13:39:14
制作人员：笔芯
=============================================================
*/
var a = -1;

function end(mode, type, selection) {//任务完成
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
            qm.sendNext("什么？听说你在遗物分析中发现了#b破碎的水晶球#k？")
        }else if ( a == 1){
            qm.sendNext("水晶球要是破了的话就没有威力了。")
        }else if ( a == 2){
            qm.sendNext("虽然管理员上次有给我一颗新的水晶球，这个我就不要了。")
        }else if ( a == 3){
            qm.sendNext("算了，为了报答你，我送你一样东西吧，毕竟我对旧的东西有所怀念。")
        }else if ( a == 4){
            qm.forceCompleteQuest();
            qm.dispose();
        }//status
    }//mode
}//function
