function start() {
	if (cm.getChar().getMapId() == 211070100){
	    if(cm.countMonster()>0){
		 cm.sendSimple ("#b★★★★★★★★★★★★★★★★★★★★★★★★★★★\r\n★如果你在这里能击败所有BOSS您奖获得本服最丰厚的奖励★★★★★★★★★★★★★★★★★★★★★★★★★★★★\r\n               【需要5亿冒险币加500个#v4000406#】\r\n             #L0#邪恶的班·雷昂狮王#l\r\n\r\n         #L5##r查看奖励#l      #L8##r回到市场#l");
	    }else{
   		 cm.sendSimple ("#b★★★★★★★★★★★★★★★★★★★★★★★★★★★\r\n★如果你在这里能击败所有BOSS您奖获得本服最丰厚的奖励★★★★★★★★★★★★★★★★★★★★★★★★★★★★\r\n               【需要5亿冒险币加500个#v4000406#】\r\n             #L0#邪恶的班·雷昂狮王#l\r\n\r\n         #L5##r查看奖励#l      #L8##r回到市场#l");
	    }
	} else {
	    cm.sendOk("找我什么事，想要启动我的力量吗，你需要足够的条件")
	}
}
function action(mode, type, selection) {
cm.dispose();
if (selection == 0) {
        if (!cm.haveItem(4000406,500)) {
        cm.sendOk("#r抱歉，你没有500个#v4000406#\r\n\r\n到少林打金人");
        } else if(cm.getMeso() <= 500000000) {
         cm.sendOk("抱歉你没有5亿。我不能为您召唤");
        }else{
	cm.gainItem(4000406,-500);
        cm.gainMeso(-500000000);
        cm.summonMob(8840000,2000000000, 40000, 1);    
cm.serverNotice("『BOSS系统』：【"+ cm.getChar().getName() +"】已经召唤出邪恶的班·雷昂狮王,难道他想独霸宝藏吗?"); 
	cm.dispose();}
} else if (selection == 1) {
       if(cm.haveItem(4001126,200) == true) {
        cm.gainItem(4001126,-200);
cm.serverNotice("『地狱大公公告』：【"+ cm.getChar().getName() +"】带领他的队友开始挑战地狱大公终极BOSS【血焰将军 】！");  
        cm.summonMob(9400591, 100000000, 40000, 1);//血焰将军     
        }else{
        cm.sendOk("抱歉你没有200个枫叶。我不能为您召唤"); 
	cm.dispose();}
} else if (selection == 2) {
       if(cm.haveItem(4001126,200) == true) {
        cm.gainItem(4001126,-200);
cm.serverNotice("『会员公告』：【"+ cm.getChar().getName() +"】带领他的队友开始挑战地狱大公终极BOSS【地狱船长  】！"); 
        cm.summonMob(9400589, 100000000, 40000, 1);//地狱船长     
        }else{
        cm.sendOk("抱歉你没有200个枫叶。我不能为您召唤"); 
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
cm.sendOk("#r击败BOSS之后BOSS会掉落的物品有:\r\n\r\n#v1482080##v1482104##v1402091##v1402113##v1452107##v1452131##v1332152##v1492081##v1492103##v1522019##v1522022##v1532019##v1532039##v1472118##v1472143##v1382102##v1382126##v1462094##v1462120##v1332126##v1322135##v1322136##v1372080##v1372102##v1302149##v1442113##v1442138##v1432084##v1432101##v1312095##v1312096##v1302175#  雷昂一系列武器...");      
cm.dispose();
} else if (selection == 6) {
       if(cm.haveItem(4001126,1000) == true) {
        cm.gainItem(4001126,-1000);
cm.serverNotice("『会员公告』：【"+ cm.getChar().getName() +"】带领他的队友开始挑战地狱大公终极BOSS【海之魔女  】！"); 
        cm.summonMob(9400590, 10000000, 40000, 1);//地狱船长     
        }else{
        cm.sendOk("抱歉你没有1000个枫叶。我不能为您召唤"); 
	cm.dispose();}
} else if (selection == 7) {
       if(cm.haveItem(4001126,1000) == true) {
        cm.gainItem(4001126,-1000);
cm.serverNotice("『会员公告』：【"+ cm.getChar().getName() +"】带领他的队友开始挑战地狱大公终极BOSS【暗影杀手  】！"); 
        cm.summonMob(9400593, 100000000, 40000, 1);//地狱船长     
        }else{
        cm.sendOk("抱歉你没有1000个枫叶。我不能为您召唤"); 
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
