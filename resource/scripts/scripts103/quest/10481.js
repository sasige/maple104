/* ===========================================================
			注释
	脚本类型: 		QUEST
	脚本名字:		冒险岛运营员的祝贺
=============================================================
制作时间：2010年8月9日 14:54:36
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
            qm.sendNext("你已经到达20级了吗？")
        }else if (a == 1){
            qm.sendNext("不敢相信，居然可以这么快成长。")
        }else if (a == 2){
            qm.sendNext("送你一样东西，等你到了50级以后我会送你一样更好的#b龙眼镜#k。\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0# \r\n\r\n#v2040232# #t2040232# 1个")
            qm.gainItem(2040232,1)
            qm.forceCompleteQuest();
            qm.dispose();
        }//status
    }//mode
}//function
