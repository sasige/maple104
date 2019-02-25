function start() {
if(cm.getChar().getVip() >= 6 && cm.getChar().getMapId() == 922020300) {
	    if(cm.countMonster()>10000){
		 cm.sendSimple ("我讨厌这里有小生物请消灭掉它们再找我谈话,谢谢~! \r\n");
	    }else{
   		 cm.sendSimple ("您好我是会员接待者我可以帮你传送到你想去的地方~!\r\n#r               【绝对免费哟】\r\n#L0#传送到     #b[邪恶的班-雷昂狮王]#l\r\n#r#L1#传送到     #b[未来的主宰-女皇希纳斯]\r\n#r#L2#传送到     #b[会员⑥秘密-刷钱地图]\r\n#r#L3#传送到     #b[会员⑥秘密-刷必成祝福]\r\n#r#L4#传送到     #b[闹钟-帕普拉图斯的座钟]\r\n#r#L5#传送到     #b[春哥菊花无限开副本]\r\n#r#L6#传送到     #b[品克缤-是我宠物副本]\r\n\r\n#l#L101##r#v4000464#我要去市场欺负小盆友#l");
	    }
	} else {
	    cm.sendOk("#r我是专为Vip⑥服务的传送员.\r\n\r\n#b你不是Vip⑥或者你不在Vip⑥地图里\r\n\r\n没事别乱点我,美女很忙,买了Vip⑥再来找偶.谢谢~！")
	}
}
function action(mode, type, selection) {
cm.dispose();
if (selection == 0) {
cm.warp(980000904,0);
cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『"+ cm.getChar().getName() +"』Vip⑥"+" : "+"使用卑鄙的会员权力强行进入[邪恶的班·雷昂狮王]"))
cm.dispose();
}else if (selection == 1){
		 cm.warp(980000903,0);
cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『"+ cm.getChar().getName() +"』Vip⑥"+" : "+"使用卑鄙的会员权力强行进入[未来的主宰-女皇希纳斯]"))
                 cm.dispose();
}else if (selection == 101){
		 cm.warp(910000000,0);
cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『"+ cm.getChar().getName() +"』Vip⑥"+" : "+"使用卑鄙的会员权力强行进入市场大家请注意"))
                 cm.dispose();
}else if (selection == 2){
		 cm.warp(910000020,0);
cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『"+ cm.getChar().getName() +"』Vip⑥"+" : "+"使用卑鄙的会员权力强行进入[会员⑥秘密-刷钱地图]"))
                 cm.dispose();
}else if (selection == 3){
		 cm.warp(910000021,0);
cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『"+ cm.getChar().getName() +"』Vip⑥"+" : "+"使用卑鄙的会员权力强行进入[会员⑥秘密-刷卷地图]"))
                 cm.dispose();
}else if (selection == 4){
		 cm.warp(220080001,0);
                 cm.dispose();
}else if (selection == 5){
		if (cm.haveItem(4001564)) {
                    cm.sendOk("请把你背包里面的:#v4001564#清空.");
                    cm.dispose();
                }else {	
                 cm.gainItem(4001564, 1);
		 cm.warp(910340100,0);
                 cm.dispose();}
}else if (selection == 6){
		if (cm.haveItem(4001394)) {
                    cm.sendOk("请把你背包里面的:#v4001394#清空.");
                    cm.dispose();
                }else {	
                 cm.gainItem(4001394, 1);
		 cm.warp(270050100,0);
cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『"+ cm.getChar().getName() +"』Vip⑥"+" : "+"使用卑鄙的会员权力强行进入[时间宠儿地图]"))
                 cm.dispose();}


} 
}
