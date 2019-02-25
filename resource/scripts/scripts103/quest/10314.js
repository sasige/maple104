/* ===========================================================
			注释(cm.sendSimple\cm.itemQuantity(5420008))
	脚本类型: 		QUEST
	脚本名字:		发现了闪闪发光的腕表！
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
            qm.sendNext("什么？分析遗物的时候，发现了#b闪闪发光的腕表#k？")
        }else if ( a == 1){
            qm.sendNext("这个东西可是很珍贵的，听说以前有以为冒险家在探险的时候掉了，他还哭哭啼啼的要我帮他找回来呢。")
        }else if ( a == 2){
            qm.sendNext("我得赶紧联系他，把这个东西给他。")
        }else if ( a == 3){
            qm.sendNext("对了，你去找找其它的遗物，听说还有许多遗物散落在冒险世界。")
        }else if ( a == 4){
            qm.forceCompleteQuest();
            qm.dispose();
        }//status
    }//mode
}//function
