/* ===========================================================
			注释(qm.sendSimple\qm.itemQuantity(5420008))
	脚本类型: 		QUEST
	脚本名字:		
	完成任务函数：qm.forceCompleteQuest();
	接受任务函数：qm.forceStartQuest();
=============================================================
本脚本源自网上流传，仅为技术交流之用。如侵权。请联系我们，我们将在第一时间删除。
*/
/*
        <imgdir name="0001142">
            <string name="name" value="飞行骑乘"/>
            <string name="desc" value="有可以飞行的骑宠的话，可以在天上飞。\n骑着骑宠，同时按方向键上和跳跃键，就可以飞行。"/>
            <string name="h1" value="消耗MP10"/>
        </imgdir>
        <imgdir name="10001142">
            <string name="name" value="飞行骑乘"/>
            <string name="desc" value="有可以飞行的骑宠的话，可以在天上飞。\n骑着骑宠，同时按方向键上和跳跃键，就可以飞行。"/>
            <string name="h1" value="消耗MP10"/>
        </imgdir>
        <imgdir name="20001142">
            <string name="name" value="飞行骑乘"/>
            <string name="desc" value="有可以飞行的骑宠的话，可以在天上飞。\n骑着骑宠，同时按方向键上和跳跃键，就可以飞行。"/>
            <string name="h1" value="消耗MP10"/>
        </imgdir>
        <imgdir name="20011142">
            <string name="name" value="飞行骑乘"/>
            <string name="desc" value="有可以飞行的骑宠的话，可以在天上飞。\n骑着骑宠，同时按方向键上和跳跃键，就可以飞行。"/>
            <string name="h1" value="消耗MP10"/>
        </imgdir>
        <imgdir name="30001142">
            <string name="name" value="飞行骑乘"/>
            <string name="desc" value="有可以飞行的骑宠的话，可以在天上飞。\n骑着骑宠，同时按方向键上和跳跃键，就可以飞行。"/>
            <string name="h1" value="消耗MP10"/>
        </imgdir>
*/
var a = -1;
var skillid;

function end(mode, type, selection) {//接任务
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
				if (qm.getQuestStatus(50722) == 0){
				qm.forceStartQuest();
				qm.dispose();
				}else{
					qm.sendNext("啊，你带来了#b#t4032969##k了？")
				//qm.forceCompleteQuest();
				//qm.dispose();
				}	
			}else if (a == 1){
				qm.sendYesNo("有了这个东西，就可以学习飞行骑乘技能，你想学习吗？")
			}else if (a == 2){
				if(qm.getJob() >= 1000 && qm.getJob() <= 1510){
                skillid = 10001142;//骑士团
				}
				
				if (qm.getJob() >= 2001 && qm.getJob() <= 2218){
					skillid = 20011142;//龙神
				}
				
				if(qm.getJob() >= 2000 && qm.getJob() <= 2112){
					skillid = 20001142;//战神
				}
				
				if(qm.getJob() >= 3000 && qm.getJob() <= 3512){
					skillid = 30001142;//反抗者
				}
				
				if(qm.getJob() <= 522){
					skillid = 1142;//冒险家
				}
				
				if (skillid == null){
				qm.sendOk("系统错误，或者是您的角色暂时不能学习技能，请联系管理员。")
				qm.dispose();
				}else{
				qm.sendOk("好的，你已经学习了这个技能。\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n\r\n#s"+skillid+"# 飞行骑乘技能。")
				qm.gainItem(4032969,-1);
				qm.teachSkill(skillid,1,1);
				qm.forceCompleteQuest();
				qm.dispose();
				}
		}//status
	}//mode
}//function