function start() {
	if (cm.getChar().getMapId() == 541010010){
	    if(cm.countMonster()<0){
		 cm.sendSimple ("您正在挑战BOSS,请选把它消灭了再来找我召唤!!如果您想放弃挑战,我可以把你送回市场! \r\n \r\n    #L8##r回到市场#l");
	    }else{
   		 cm.sendSimple ("#b★★★★★★★★★★★★★★★★★★★★★★★★★★★\r\n★如果你在这里能击败所有BOSS您奖获得本服最丰厚的奖励★★你可要小心哦，一个会比一个厉害的。                \r\n★★★★★★★★★★★★★★★★★★★★★★★★★★★\r\n             【#v4000382#需要300个#v4000382#】\r\n               #L0#挑战幽灵船长#l\r\n\r\n         #L5##r查看奖励#l      #L8##r回到市场#l");
	    }
	} else {
	    cm.sendOk("找我什么事，想要启动我的力量吗，你需要足够的条件")
	}
}
function action(mode, type, selection) {
cm.dispose();
if (selection == 0) {
       if(cm.haveItem(4000382,300) == true) {
        cm.gainItem(4000382,-300);
        cm.summonMob(9420513,1000000000, 400000000, 1);//chuanzhang     
cm.serverNotice("『幽灵船长公告』：【"+ cm.getChar().getName() +"】带领他的大队人马直攻幽灵船长老巢,血誓杀个片甲不留~!"); 
        }else{
        cm.sendOk("抱歉你没有300个蓝色精华。我不能为您召唤"); 
	cm.dispose();}
} else if (selection == 1) {
       if(cm.haveItem(4000382,200) == true) {
        cm.gainItem(4000382,-200);
cm.serverNotice("『地狱大公公告』：【"+ cm.getChar().getName() +"】带领他的队友开始挑战地狱大公终极BOSS【血焰将军 】！");  
        cm.summonMob(9400591, 100000000, 40000, 1);//血焰将军     
        }else{
        cm.sendOk("抱歉你没有200个蓝色精华。我不能为您召唤"); 
	cm.dispose();}
} else if (selection == 2) {
       if(cm.haveItem(4000382,200) == true) {
        cm.gainItem(4000382,-200);
cm.serverNotice("『会员公告』：【"+ cm.getChar().getName() +"】带领他的队友开始挑战地狱大公终极BOSS【地狱船长  】！"); 
        cm.summonMob(9400589, 100000000, 40000, 1);//地狱船长     
        }else{
        cm.sendOk("抱歉你没有300个蓝色精华。我不能为您召唤"); 
	cm.dispose();}
} else if (selection == 3) { 
        if (!cm.haveItem(4001009,1)) {
        cm.sendOk("抱歉，你没有1张#v4001009#无法为你开启");
        } else if (!cm.haveItem(4001010,1)) {
        cm.sendOk("抱歉，你没有1张#v4001010#无法为你开启"); 
        } else if (!cm.haveItem(4001011,1)) {
        cm.sendOk("抱歉，你没有1张#v4001011#无法为你开启"); 
        } else if (!cm.haveItem(4001012,1)) {
        cm.sendOk("抱歉，你没有1张#v4001012#无法为你开启"); 
        } else if (!cm.haveItem(4001013,1)) {
        cm.sendOk("抱歉，你没有1张#v4001013#无法为你开启"); 
        }else{
	cm.gainItem(4001009,-1);
	cm.gainItem(4001010,-1);
	cm.gainItem(4001011,-1);
	cm.gainItem(4001012,-1);
	cm.gainItem(4001013,-1);
	cm.gainItem(4021010,1);
	cm.dispose();
}
} else if (selection == 4) {
        if (!cm.haveItem(4021010,1)) {
        cm.sendOk("抱歉，你没有200个#v4021010#无法为你开启");
cm.dispose();
    } else {
cm.warp(803001400, 0);
cm.dispose();
}
}else if (selection == 8) {
    cm.warp(910000000, 0);
    cm.dispose();
}else if (selection == 5) {
cm.sendOk("#r击败BOSS之后BOSS会掉落的物品有:\r\n\r\n#v1402017##v1002995##v1052209##v1402116##v5150040##v1052275##v1003151##v1003109##v1072448#  ");      
cm.dispose();
} else if (selection == 6) {
       if(cm.haveItem(4000382,1000) == true) {
        cm.gainItem(4000382,-1000);
cm.serverNotice("『会员公告』：【"+ cm.getChar().getName() +"】带领他的队友开始挑战地狱大公终极BOSS【海之魔女  】！"); 
        cm.summonMob(9400590, 10000000, 40000, 1);//地狱船长     
        }else{
        cm.sendOk("抱歉你没有1000个蓝色精华。我不能为您召唤"); 
	cm.dispose();}
} else if (selection == 7) {
       if(cm.haveItem(4000382,1000) == true) {
        cm.gainItem(4000382,-1000);
cm.serverNotice("『会员公告』：【"+ cm.getChar().getName() +"】带领他的队友开始挑战地狱大公终极BOSS【暗影杀手  】！"); 
        cm.summonMob(9400593, 100000000, 40000, 1);//地狱船长     
        }else{
        cm.sendOk("抱歉你没有1000个蓝色精华。我不能为您召唤"); 
	cm.dispose();}
} else if (selection == 8) {
	if(cm.getMeso() <= 50000000) {
        cm.sendOk("抱歉你没有5000万。我不能为您召唤"); 
        }else{ 
        cm.gainMeso(-50000000);
        cm.summonMob(9400300, 100000000, 175000000, 1);//恶僧
	cm.dispose(); } 
} else if (selection == 9) {
	if(cm.getMeso() <= 50000000) {
        cm.sendOk("抱歉你没有5000万。我不能为您召唤"); 
        }else{ 
        cm.gainMeso(-50000000);
        cm.summonMob(9400549, 1, 200300000, 1);//火马
	cm.dispose(); } 
} 
}
