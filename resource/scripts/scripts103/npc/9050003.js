function start() {	
	if (cm.getChar().getMapId() == 922010501){
	    if(cm.countMonster()<0){
		 cm.sendSimple ("您正在挑战BOSS,请选把它消灭了再来找我召唤!!如果您想放弃挑战,我可以把你送回市场! \r\n \r\n    #L8##r回到市场#l");
	    }else{
   		 cm.sendSimple ("#b您好,我是运动会召唤boss的小猪猪\r\n\r\n                         #L0##r#v3994471# 召唤运动会boss #v3994471##l  \r\n#k       \r\n\r\n#b请保证您其他栏和设置栏有 #b5#k #b个空格再过关\r\n\r\n#v3994246#   #v3994247#   #v3994448#   #v4001318#   #v4031954#\r\n\r\n                        #L2#   #v1002972# 我要过关 #v1002972##l");
	    }
	} else {
	    cm.sendOk("找我什么事，想要启动我的力量吗，你需要足够的条件")
	}
}
function action(mode, type, selection) {
cm.dispose();
if (selection == 0) {
        if (!cm.haveItem(4032529,1)) {
        cm.sendOk("抱歉，你没有1张#v4032529#无法为你开启#e#r");
        } else if (!cm.haveItem(4032529,1)) {
        }else{
	cm.gainItem(4032529,-1);
        cm.summonMob(9400288,2000000000, 400, 3);     
	cm.dispose();}


} if (selection == 2) {
        if (!cm.haveItem(1002972,3)) {
        cm.sendOk("#r抱歉，你没有3顶#v1002972#无法过关.换取运动金币"); 
        }else{
	cm.gainItem(1002972,-3);
	cm.gainItem(3994246,1);
	cm.gainItem(3994247,1);
	cm.gainItem(3994448,1);
	cm.gainItem(4001318,1);
	cm.gainItem(4031954,1);
    	cm.warp(922010506,0); 
	cm.sendOk("#b哈哈,您太厉害了,过关了\r\n\r\n#r以下五个道具分别找到他们的主人会奖励您丰厚道具\r\n\r\n#v3994246#   #v3994247#   #v3994448#   #v4001318#   #v4031954#\r\n\r\n#b(如果您领取由遗漏说明您背包满了");
	cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『运动会副本』"+" : "+"『"+ cm.getChar().getName() +"』玩家被[梅西] 踢进奖励关卡"))    
	cm.dispose();}


}
}