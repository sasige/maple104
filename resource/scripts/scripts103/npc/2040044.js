function start() {
	if (cm.getChar().getMapId() == 910510000){
	    if(cm.countMonster()>0){
		 cm.sendSimple ("您正在挑战BOSS,请选把它消灭了再来找我召唤!!如果您想放弃挑战,我可以把你送回市场!<地图不能有怪物> \r\n \r\n    #L8##r回到市场#l");
	    }else{
   		 cm.sendSimple ("#r★★★★★★★★★★★★★★★★★★★★★★★★★★★\r\n★★★★★★★★#b我可以帮你召唤凤舞三兄弟#r★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★\r\n\r\n            #r不过你要给我一张蜗牛的邮票\r\n          【#v4002000##v4002000##v4002000##v4002000##v4002000#】\r\n                  #b掉玩具几率很大哟\r\n                  #b#L0#凤舞三兄弟#l\r\n\r\n     #L5##r查看奖励#l                 #L8##r回到市场#l");
	    }
	} else {
	    cm.sendOk("找我什么事，想要启动我的力量吗，你需要足够的条件")
	}
}
function action(mode, type, selection) {
cm.dispose();
if (selection == 0) {
        if (!cm.haveItem(4002000,1)) {
        cm.sendOk("抱歉，你没有1张#v4002000#无法为你开启#e#r\r\n\r\n(打蜗牛爷爷副本获得,或者10E冒险币换取)");
        }else{
	cm.gainItem(4002000,-1);
        cm.summonMob(9300089,2100000000, 40000, 1);  
        cm.summonMob(9300090,2100000000, 40000, 1);
        cm.summonMob(9400599,2100000000, 40000, 1);   
cm.serverNotice("『BOSS系统』：【"+ cm.getChar().getName() +"】已经召唤出凤舞三兄弟-一海量玩具等着你");
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
cm.sendOk("#e击败BOSS之后BOSS会掉落的物品有:#e#r\r\n\r\n火:#v1302063##v1302084##v1302087##v1372014##v1372035##v1342006#\r\n冰:#v1302080##v1432094##v1302128##v1702136##v1702211##v1702113#\r\n暗:#v1702321##v1702240##v3010085##v3010092##v1332077##v1302026#\r\n.");      
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
