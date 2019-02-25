function start() {
	if (cm.getChar().getMapId() == 910340100){
	    if(cm.countMonster()>0){
		 cm.sendSimple ("如果你想进入下一关每人必须要50个#v4032605# \r\n \r\n   #b#L0#进入下一关#l");
	    }else{
   		 cm.sendSimple ("#b#v4032605##v4032605##v4032605##v4032605##v4032605##v4032605##v4032605##v4032605##v4032605##v4032605##v4032605##v4032605##v4032605##v4032605# #v4032605##v4032605##v4032605# #v4032605##v4032605##v4032605##v4032605#\r\n如果你想进入下一关每人必须要50个\r\n\r\n     #r如果你想进入下一关每人必须要50个\r\n                   #b#L0#进入下一关#l\r\n\r\n       #L8##r回到市场#l");
	    }
	} else {
	    cm.sendOk("找我什么事，想要启动我的力量吗，你需要足够的条件")
	}
}
function action(mode, type, selection) {
cm.dispose();
if (selection == 0) {
        if (!cm.haveItem(4032605,50)) {
        cm.sendOk("抱歉，你没有50张#v4032605#请继续收集#e#r(帅哥。请继续)");
        } else if (!cm.haveItem(4032605,50)) {
	cm.gainItem(4032605,-50);
        }else{
	cm.gainItem(4032605,-50);
        cm.warp(910340200,0); 
    cm.dispose();
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
        cm.sendOk("抱歉，你没有1张#v4001009#无法为你开启#e#r(市场9洞获取)");
        } else if (!cm.haveItem(4001010,1)) {
        cm.sendOk("抱歉，你没有1张#v4001010#无法为你开启#e#r(市场9洞获取)"); 
        } else if (!cm.haveItem(4001011,1)) {
        cm.sendOk("抱歉，你没有1张#v4001011#无法为你开启#e#r(市场9洞获取)"); 
        } else if (!cm.haveItem(4001012,1)) {
        cm.sendOk("抱歉，你没有1张#v4001012#无法为你开启#e#r(市场9洞获取)"); 
        } else if (!cm.haveItem(4001013,1)) {
        cm.sendOk("抱歉，你没有1张#v4001013#无法为你开启#e#r(市场9洞获取)"); 
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
        cm.sendOk("抱歉，你没有200个#v4021010#无法为你开启#e#r(市场9洞获取)");
cm.dispose();
    } else {
cm.warp(803001400, 0);
cm.dispose();
}
}else if (selection == 8) {
    cm.warp(910000000, 0);
    cm.dispose();
}else if (selection == 5) {
cm.sendOk("#r击败BOSS之后BOSS会掉落的物品有:\r\n\r\n#v4032605##v4031454##v4032605##v4032164#");      
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
