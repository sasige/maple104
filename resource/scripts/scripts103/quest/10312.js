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
            qm.sendNext("你找到遗物了啊？唠叨唠叨……")
        }else if ( a == 1){
            qm.sendNext("咦…… 这不就是我的眼睛吗？")
        }else if ( a == 2){
            qm.sendNext("我想起来了，这是上次我被飞船绑架时候遗失的眼睛，谢谢你帮我找了回来。（虽然我买了新的。）")
        }else if ( a == 3){
            qm.sendNext("为了报答你，我送给你一样东西，你再去收集遗物，听说可以得到一样很好的东西。")
        }else if ( a == 4){
            qm.forceCompleteQuest();
            qm.dispose();
        }//status
    }//mode
}//function
