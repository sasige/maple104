function start() {
var ico11 = "#fEffect/CharacterEff/1112903/0/0#";
	if (cm.getChar().getMapId() == 910510100){
	    if(cm.countMonster()<0){
		 cm.sendSimple ("...");
	    }else{
   		 cm.sendSimple ("\r\n                 副本介绍\r\n            #r最后一关搜集4个道具\r\n           #v4000422##v4000423##v4000424##v4000425#\r\n         即可换一个稀有玩具或者椅子\r\n\r\n       #L1##r召唤#l#L2##r召唤#l#L3##r召唤#l#L4##r召唤#l\r\n       #L5##r召唤#l#L16##r召唤#l#L6##r召唤#l#L7##r召唤#l\r\n       #L8##r召唤#l#L9##r召唤#l#L10##r召唤#l#L11##r召唤#l\r\n       #L12##r召唤#l#L13##r召唤#l#L14##r召唤#l#L15##r召唤#l");
	    }
	} else {
	    cm.sendOk("找我什么事，想要启动我的力量吗，你需要足够的条件")
	}
}
function action(mode, type, selection) {
cm.dispose();
if (selection == 0) {
        if (!cm.haveItem(4032532,1)) {
        cm.sendOk("#");
        } else if (!cm.haveItem(4032532,1)) {
        }else{
	cm.dispose();}



} if (selection == 1) {
        if (!cm.haveItem(3994471,1)) {
        cm.sendOk("抱歉，你没有1张#v3994471#无法为你开启#e#r(上一关,会送1个您)"); 
        }else{
	cm.gainItem(3994471,-1);
        cm.summonMob(9410080,45000, 40000, 1);     
cm.serverNotice("『幸运系统』：【"+ cm.getChar().getName() +"】已经召唤出蓝色幸运boss");
	cm.dispose();}



} if (selection == 2) {
        if (!cm.haveItem(3994471,1)) {
        cm.sendOk("抱歉，你没有1张#v3994471#无法为你开启#e#r(上一关,会送1个您)"); 
        }else{
	cm.gainItem(3994471,-1);
        cm.summonMob(9500386,2000000000, 40000, 1);     
cm.serverNotice("『幸运系统』：【"+ cm.getChar().getName() +"】已经召唤出白色幸运boss");
	cm.dispose();}


} if (selection == 3) {
        if (!cm.haveItem(3994471,1)) {
        cm.sendOk("抱歉，你没有1张#v3994471#无法为你开启#e#r(上一关,会送1个您)"); 
        }else{
	cm.gainItem(3994471,-1);
        cm.summonMob(9500386,2000000000, 40000, 1);     
cm.serverNotice("『幸运系统』：【"+ cm.getChar().getName() +"】已经召唤出白色幸运boss");
	cm.dispose();}


} if (selection == 4) {
        if (!cm.haveItem(3994471,1)) {
        cm.sendOk("抱歉，你没有1张#v3994471#无法为你开启#e#r(上一关,会送1个您)"); 
        }else{
	cm.gainItem(3994471,-1);
        cm.summonMob(9410080,45000, 40000, 1);     
cm.serverNotice("『幸运系统』：【"+ cm.getChar().getName() +"】已经召唤出蓝色幸运boss");
	cm.dispose();}


} if (selection == 5) {
        if (!cm.haveItem(3994471,1)) {
        cm.sendOk("抱歉，你没有1张#v3994471#无法为你开启#e#r(上一关,会送1个您)"); 
        }else{
	cm.gainItem(3994471,-1);
        cm.summonMob(9500386,2000000000, 40000, 1);     
cm.serverNotice("『幸运系统』：【"+ cm.getChar().getName() +"】已经召唤出白色幸运boss");
	cm.dispose();}


} if (selection == 6) {
        if (!cm.haveItem(3994471,1)) {
        cm.sendOk("抱歉，你没有1张#v3994471#无法为你开启#e#r(上一关,会送1个您)"); 
        }else{
	cm.gainItem(3994471,-1);
        cm.summonMob(9500386,2000000000, 40000, 1);     
cm.serverNotice("『幸运系统』：【"+ cm.getChar().getName() +"】已经召唤出白色幸运boss");
	cm.dispose();}

} if (selection == 7) {
        if (!cm.haveItem(3994471,1)) {
        cm.sendOk("抱歉，你没有1张#v3994471#无法为你开启#e#r(上一关,会送1个您)"); 
        }else{
	cm.gainItem(3994471,-1);
        cm.summonMob(5250007,2000000000, 40000, 1);     
cm.serverNotice("『幸运系统』：【"+ cm.getChar().getName() +"】已经召唤出紫色幸运boss");
	cm.dispose();}

} if (selection == 8) {
        if (!cm.haveItem(3994471,1)) {
        cm.sendOk("抱歉，你没有1张#v3994471#无法为你开启#e#r(上一关,会送1个您)"); 
        }else{
	cm.gainItem(3994471,-1);
        cm.summonMob(9400754,2000000000, 40000, 1);     
cm.serverNotice("『幸运系统』：【"+ cm.getChar().getName() +"】已经召唤出红色幸运boss");
	cm.dispose();}



} if (selection == 9) {
        if (!cm.haveItem(3994471,1)) {
        cm.sendOk("抱歉，你没有1张#v3994471#无法为你开启#e#r(上一关,会送1个您)"); 
        }else{
	cm.gainItem(3994471,-1);
        cm.summonMob(9500386,2000000000, 40000, 1);     
cm.serverNotice("『幸运系统』：【"+ cm.getChar().getName() +"】已经召唤出白色幸运boss");
	cm.dispose();}


} if (selection == 10) {
        if (!cm.haveItem(3994471,1)) {
        cm.sendOk("抱歉，你没有1张#v3994471#无法为你开启#e#r(上一关,会送1个您)"); 
        }else{
	cm.gainItem(3994471,-1);
        cm.summonMob(9500386,2000000000, 40000, 1);     
cm.serverNotice("『幸运系统』：【"+ cm.getChar().getName() +"】已经召唤出白色幸运boss");
	cm.dispose();}

} if (selection == 11) {
        if (!cm.haveItem(3994471,1)) {
        cm.sendOk("抱歉，你没有1张#v3994471#无法为你开启#e#r(上一关,会送1个您)"); 
        }else{
	cm.gainItem(3994471,-1);
        cm.summonMob(5250007,2000000000, 40000, 1);     
cm.serverNotice("『幸运系统』：【"+ cm.getChar().getName() +"】已经召唤出紫色幸运boss");
	cm.dispose();}

} if (selection == 12) {
        if (!cm.haveItem(3994471,1)) {
        cm.sendOk("抱歉，你没有1张#v3994471#无法为你开启#e#r(上一关,会送1个您)"); 
        }else{
	cm.gainItem(3994471,-1);
        cm.summonMob(9400754,2000000000, 40000, 1);     
cm.serverNotice("『幸运系统』：【"+ cm.getChar().getName() +"】已经召唤出红色幸运boss");
	cm.dispose();}


} if (selection == 13) {
        if (!cm.haveItem(3994471,1)) {
        cm.sendOk("抱歉，你没有1张#v3994471#无法为你开启#e#r(上一关,会送1个您)"); 
        }else{
	cm.gainItem(3994471,-1);
        cm.summonMob(9500386,2000000000, 40000, 1);     
cm.serverNotice("『幸运系统』：【"+ cm.getChar().getName() +"】已经召唤出白色幸运boss");
	cm.dispose();}


} if (selection == 14) {
        if (!cm.haveItem(3994471,1)) {
        cm.sendOk("抱歉，你没有1张#v3994471#无法为你开启#e#r(上一关,会送1个您)"); 
        }else{
	cm.gainItem(3994471,-1);
        cm.summonMob(9500386,2000000000, 40000, 1);     
cm.serverNotice("『幸运系统』：【"+ cm.getChar().getName() +"】已经召唤出白色幸运boss");
	cm.dispose();}

} if (selection == 15) {
        if (!cm.haveItem(3994471,1)) {
        cm.sendOk("抱歉，你没有1张#v3994471#无法为你开启#e#r(上一关,会送1个您)"); 
        }else{
	cm.gainItem(3994471,-1);
        cm.summonMob(5250007,2000000000, 40000, 1);     
cm.serverNotice("『幸运系统』：【"+ cm.getChar().getName() +"】已经召唤出紫色幸运boss");
	cm.dispose();}


} if (selection == 16) {
        if (!cm.haveItem(3994471,1)) {
        cm.sendOk("抱歉，你没有1张#v3994471#无法为你开启#e#r(上一关,会送1个您)"); 
        }else{
	cm.gainItem(3994471,-1);
        cm.summonMob(9500386,2000000000, 40000, 1);     
cm.serverNotice("『幸运系统』：【"+ cm.getChar().getName() +"】已经召唤出白色幸运boss");
	cm.dispose();}


}
}