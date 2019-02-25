function start() {
if(cm.getChar().getMapId() == 910130101) {
	    if(cm.countMonster()>10000){
		 cm.sendSimple ("我讨厌这里有小生物请消灭掉它们再找我谈话,谢谢~! \r\n");
	    }else{
   		 cm.sendSimple ("      #v4000464#想获得丰厚的奖励吗??!#v4000464#\r\n\r\n        #L0##b我要走捷径#l        #r#L1#自由市场#l\r\n\r\n\r\n   完成副本后可以获得魔力独家推出的魔力扫把坐骑\r\n  \r\n  #v1992006##v1992006##v1992006##v1992006##v1992006##v1992006##v1992006##v1992006##v1992006#");
	    }
	} else {
	    cm.sendOk("...")
	}
}
function action(mode, type, selection) {
cm.dispose();
if (selection == 0	) {
cm.sendSimple ("#r没有捷径可以#k走努力吧~!\r\n#b                            少年");
cm.dispose();}
if (selection == 1	) {
cm.warp(910000000,0);
cm.dispose();}
}