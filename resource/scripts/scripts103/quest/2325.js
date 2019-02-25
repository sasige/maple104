/* 
笔芯制作
非同意内禁止转载
脚本类型：QUEST
脚本内容：詹姆斯的行踪(1)
cm.sendSimple

*/
var status = -1;

function end(mode, type, selection) {//任务开始
	if (mode == -1) {
		qm.sendOk("不想去调查看看吗。。")
		qm.dispose();
	} else {
		if (mode == 1)
			status++;
		else
			status--;
if (status == -1){
qm.sendOk("怎么了？不想听听怎么回事吗？")
qm.dispose();
}else{
		if (status == 0) {
		qm.sendSimple(" 这里太恐怖了，你能帮助我吗？ #b\r\n\r\n#L0# 你需要什么帮助？")
		}else if (status == 1){//有什么我可以帮助的吗
		if (selection == 0){
		qm.sendYesNo("前几天，我被企鹅国的人追杀，躲在了这里。他们很快就发现我了。怎么办。。。你能帮助我吗？")
		}
}else if (status == 2){
		qm.forceStartQuest();
		qm.forceCompleteQuest();
		qm.dispose();
			
		}//status
	}//mode
}//function
}



