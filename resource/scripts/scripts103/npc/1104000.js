function start() {
	if (cm.getChar().getMapId() == 240060201){
	    if(cm.countMonster()<0){
		 cm.sendSimple ("您正在挑战BOSS,请选把它消灭了再来找我召唤!!如果您想放弃挑战,我可以把你送回市场! \r\n \r\n    #L8##r回到市场#l");
	    }else{
   		 cm.sendSimple ("#b★★★★★★★★★★★★★★★★★★★★★★★★★★★\r\n★如果你在这里能击败所有BOSS您奖获得本服最丰厚的奖励★★★★★★★★★★★★★★★★★★★★★★★★★★★★\r\n\r\n     #r想杀我?先打败我五个手下吧(他们在市场9洞)\r\n           【#v4001558##v4001559##v4001560##v4001561##v4001562#】\r\n               #b#L0#挑战恶魔女友#l\r\n\r\n      #e#r挑战恶魔女友·\r\n\r\n         #L5##r查看奖励#l      #L8##r回到市场#l");
	    }
	} else {
	    cm.sendOk("找我什么事，想要启动我的力量吗，你需要足够的条件")
	}
}
function action(mode, type, selection) {
cm.dispose();
if (selection == 0) {
        if (!cm.haveItem(4001558,1)) {
        cm.sendOk("抱歉，你没有1张#v4001558#无法为你开启#e#r(市场9洞获取)");
        } else if (!cm.haveItem(4001559,1)) {
        cm.sendOk("抱歉，你没有1张#v4001559#无法为你开启#e#r(市场9洞获取)"); 
        } else if (!cm.haveItem(4001560,1)) {
        cm.sendOk("抱歉，你没有1张#v4001560#无法为你开启#e#r(市场9洞获取)"); 
        } else if (!cm.haveItem(4001561,1)) {
        cm.sendOk("抱歉，你没有1张#v4001561#无法为你开启#e#r(市场9洞获取)"); 
        } else if (!cm.haveItem(4001562,1)) {
        cm.sendOk("抱歉，你没有1张#v4001562#无法为你开启#e#r(市场9洞获取)");  
        }else{
	cm.gainItem(4001558,-1);
	cm.gainItem(4001559,-1);
	cm.gainItem(4001560,-1);
	cm.gainItem(4001561,-1);
	cm.gainItem(4001562,-1);
        cm.summonMob(9001039,100000000, 40000, 1);    
        cm.summonMob(9001042,100000000, 40000, 1);     
        cm.summonMob(9001040,100000000, 40000, 1);    
        cm.summonMob(9001041,150000000, 40000, 1);   
cm.serverNotice("『BOSS系统』：【"+ cm.getChar().getName() +"】已经召唤出挑战恶魔女友,叱喝道我 等你等的好苦~!");
cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『挑战恶魔女友』"+" : "+"无理的地球人,姐姐今天要好好收拾你.~!"))	
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
cm.sendOk("#r击败BOSS之后BOSS会掉落的物品有:\r\n\r\n#v1302059##v1322052##v1332021##v1312031##v1122076##v1702274##v1402091##v1102248##v1003114##v1702208##v1402037##v3010046##v3010047##v3010096##v3010107##v3010108##v3010147##v3010128##v3010200#  好东西...");      
cm.dispose();     

} 
}
