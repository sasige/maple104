/* ===========================================================
			注释(cm.sendSimple\cm.itemQuantity(5420008))
	脚本类型: 		QUEST
	脚本名字:		石化的鼠标
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
            qm.sendNext("#h #,看来你带来的石化的鼠标，什么你不知道他是什么东西？")
        }else if ( a == 1){
            qm.sendNext("这个是过去有一个岛民使用非法程序而遗留下来的鼠标。")
        }else if ( a == 2){
            qm.sendNext("使用非法程序是一种很不好的行为，希望你不要使用非法程序。")
        }else if ( a == 3){
            qm.sendNext("我的一些同伴们抓使用非法程序的岛民抓得很辛苦，不过他们知道，总有一天玩家们会发现直接享受游戏要比使用非法程序有趣得多。")
        }else if ( a == 4){
            qm.sendNext("这样吧，我送你一样东西，然后你去找在自由市场的那位伙伴，听说收集好八大遗物他会送你一样精美的东西。")
            qm.forceCompleteQuest();
            qm.dispose();
        }//status
    }//mode
}//function
