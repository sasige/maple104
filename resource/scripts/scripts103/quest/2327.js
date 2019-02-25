/* 
笔芯制作
非同意内禁止转载
脚本类型：QUEST
脚本内容：詹姆斯的行踪(2)
cm.sendSimple

*/
var status = -1;

function start(mode, type, selection) {//任务开始
	if (mode == -1) {
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
		qm.sendSimple("#h #，外面太可怕了，我实在不敢出去。。#b\r\n\r\n#L0# 有什么我可以帮助的吗？")
		}else if (status == 1){//有什么我可以帮助的吗
		if (selection == 0){
		qm.sendNext("外面太可怕了。。你能再帮我一件事情吗？")
		}
}else if (status == 2){
qm.forceStartQuest();
qm.dispose();
}else if (status == 3){
qm.dispose();
			
		}//status
	}//mode
}//function
}


function end(mode, type, selection) {//任务开始
	if (mode == -1) {
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
		qm.sendNext("#h #，怎么了？是转职教官叫你来的吗？好吧，他好像有给你一张转职官的推荐书吧？",8)
		}else if (status == 1){//有什么我可以帮助的吗
		qm.sendNext("#b（摸摸口袋，把转职官的推荐书交给警卫队长。。）",2)	
}else if (status == 2){
		qm.sendNext("到底发生什么事情了？",2)
}else if (status == 3){
		qm.sendNext("蘑菇城堡的国王生病了，身体一天比一天差，而冰封岛的国王乘人之危，试图把蘑菇城堡给侵略了。。而且还要把蘑菇城的公主嫁给企鹅王国的王子。。",8)
}else if (status == 4){
		qm.sendNext("接下来发生什么事情了？",2)
}else if (status == 5){
		qm.sendNext("公主整天以泪洗面，蘑菇城堡的国王身体一天不如一天，眼看企鹅王国就要称霸蘑菇城了。冒险家，你能帮助我吗？",8);
}else if (status == 6){
		qm.sendNext("可以，我一定会保护好蘑菇城堡，不让企鹅王国的国王得逞！",2)
}else if (status == 7){
		qm.forceCompleteQuest();
		qm.dispose();
		}//status
	}//mode
}//function
}

