function start() {
if(cm.getChar().getMapId() == 910000014) {
	    if(cm.countMonster()>10000){
		 cm.sendSimple ("我讨厌这里有小生物请消灭掉它们再找我谈话,谢谢~! \r\n");
	    }else{
   		     cm.sendSimple ("#r您好我是庆祝五一连环副本npc\r\n#L0#我想换取#v4001547##l#k#L1##b我想换取#v4001548##l#k#L2##r我想换取#v4001549##l\r\n\r\n      #L3##b#v4001550#我想换取#l#L4##r我想换取#v4001551##l\r\n\r\n\r\n                #k#e#L99#我收集好了#l#k\r\n         #L99##v4001547##v4001548##v4001549##v4001550##v4001551##l");
	    }
	} else {
	    cm.sendOk("#r您好活动还没有开始我不发表仍和意见\r\n\r\n#b晚上6点开放\r\n\r\n今天的活动有可能比较复杂大家多多参考\r\n\r\nhttp://mxd.776.com")
	}
}
function action(mode, type, selection) {
if (selection == 0) {
        if (!cm.haveItem(4001016, 3)) {
	cm.sendOk("#r您需要 #b3#k 个 #v4001016#\r\n请检查您的背包中是否有3个再来领取。")
        }else{
                cm.gainItem(4001016, -3);
                cm.gainItem(4001547,1);
                cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null, net.sf.odinms.tools.MaplePacketCreator.serverNotice(17,cm.getC().getChannel(),"[活动公告]" + " : 恭喜玩家：" + cm.getPlayer().getName() +" 成功突破连环副本第一关",true).getBytes());
                cm.dispose();}




}
}