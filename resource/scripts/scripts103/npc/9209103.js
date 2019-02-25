/*一转弓箭手NPC

*/


var status = 0;

function start() {
	cm.sendSimple("      \r\n             #b[周末活动]需要收集\r\n\r\n\r\n  我也不知道要收集些神马(难道直接领取奖励?)\n\r\n              #r#L0#领取奖励#l\r\n\r\n         (花姑娘交出来的私密噶)\r\n\r\n           #v1702330##v3010045#,#v1522070##v1932045#");
}
function action(mode, type, selection) {
	cm.dispose();
	if (selection == 0) {
        if (!cm.haveItem(4001016,1)) {
        cm.sendOk("抱歉，你没有#v4001016#无法回收#e#r(请继续收集)");
        } else if (!cm.haveItem(4031107,1)) {
        cm.sendOk("抱歉，你没有#v4031107#无法回收#e#r(请继续收集)"); 
        } else if (!cm.haveItem(4000333,1)) {
        cm.sendOk("抱歉，你没有#v4000333#无法回收#e#r(请继续收集)"); 
        } else if (!cm.haveItem(4021007,1)) {
        cm.sendOk("抱歉，你没有#v4021007#无法回收#e#r(请继续收集)"); 
        } else if (!cm.haveItem(4031062,1)) {
        cm.sendOk("抱歉，你没有#v4031062#无法回收#e#r(请继续收集)"); 
} else {
	cm.gainItem(4001016,-1);
	cm.gainItem(4031107,-1);
	cm.gainItem(4000333,-1);
	cm.gainItem(4021007,-1);
	cm.gainItem(4031062,-1);
	cm.gainItem(3010045,1);
	cm.gainItem(1702330,1);
	cm.gainItem(1522070,1);
        cm.teachSkill(80001054,10,10);//老虎坐骑技能
 	                
cm.sendOk("回收成功请查收#v3010045#,#v1522070##v1932045##v1702330#");
 cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null, net.sf.odinms.tools.MaplePacketCreator.serverNotice(17,cm.getC().getChannel(),"[活动公告]" + " : 恭喜玩家：" + cm.getPlayer().getName() +" 完成收集副本获得稀有物品",true).getBytes());
 cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null, net.sf.odinms.tools.MaplePacketCreator.serverNotice(17,cm.getC().getChannel(),"[活动公告]" + " : 恭喜玩家：" + cm.getPlayer().getName() +" 完成收集副本获得稀有物品",true).getBytes());
 cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null, net.sf.odinms.tools.MaplePacketCreator.serverNotice(17,cm.getC().getChannel(),"[活动公告]" + " : 恭喜玩家：" + cm.getPlayer().getName() +" 完成收集副本获得稀有物品",true).getBytes());
 cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null, net.sf.odinms.tools.MaplePacketCreator.serverNotice(17,cm.getC().getChannel(),"[活动公告]" + " : 恭喜玩家：" + cm.getPlayer().getName() +" 完成收集副本获得稀有物品",true).getBytes());	
cm.dispose();}
			}
	}