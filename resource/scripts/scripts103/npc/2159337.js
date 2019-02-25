function start() {
if(cm.getChar().getVip() >= 6 && cm.getChar().getMapId() == 180000000) {
if (cm.haveItem(3010070)) {
                    cm.sendOk("您好如果您消费到了300即可点击我领取丰厚奖励不过只能领取一次噢\r\n\r\n领取全属性999的#v1112663##v1112135##v1112238#\r\n\r\n椅子:#v3010070#.\r\n\r\n坐骑:#v1902045#狮子坐骑");
                    cm.dispose();
		}else{
   		 cm.sendSimple ("您好您消费到了300 点击我领取丰厚奖励~!\r\n#r               【绝对免费哟】\r\n\r\n#r\r\n\r\n领取全属性999的#v1112663##v1112135##v1112238#\r\n\r\n椅子:#v3010070#.\r\n\r\n坐骑:#v1902045#狮子坐骑\r\n#L0##b[领取奖励道具]#l");
	    }
	} else {
	    cm.sendOk("#r您好如果您消费到了300即可点击我领取丰厚奖励不过只能领取一次噢\r\n\r\n领取全属性999的#v1112663##v1112135##v1112238#\r\n\r\n椅子:#v3010070#.\r\n\r\n坐骑:#v1902045#狮子坐骑")
	}
}
function action(mode, type, selection) {
cm.dispose();
if (selection == 0) {

                var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                var type = ii.getInventoryType(1112135); //获得装备的类形
                var toDrop = ii.randomizeStats(ii.getEquipById(1112135)).copy(); // 生成一个Equip类
                toDrop.setLocked(1);
                toDrop.setStr(999);
                toDrop.setDex(999);
                toDrop.setInt(999);
                toDrop.setLuk(999);
                toDrop.setHp(999);
                toDrop.setMp(999);
                toDrop.setMatk(999);
                toDrop.setWatk(999);
                toDrop.setMdef(999);
                toDrop.setWdef(999);
                toDrop.setAcc(999);
                toDrop.setAvoid(999);
                toDrop.setHands(999);
                toDrop.setSpeed(999);
                toDrop.setJump(999);
                cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
                cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包		
                cm.getChar().saveToDB(true);

                var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                var type = ii.getInventoryType(1112238); //获得装备的类形
                var toDrop = ii.randomizeStats(ii.getEquipById(1112238)).copy(); // 生成一个Equip类
                toDrop.setLocked(1);
                toDrop.setStr(999);
                toDrop.setDex(999);
                toDrop.setInt(999);
                toDrop.setLuk(999);
                toDrop.setHp(999);
                toDrop.setMp(999);
                toDrop.setMatk(999);
                toDrop.setWatk(999);
                toDrop.setMdef(999);
                toDrop.setWdef(999);
                toDrop.setAcc(999);
                toDrop.setAvoid(999);
                toDrop.setHands(999);
                toDrop.setSpeed(999);
                toDrop.setJump(999);
                cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
                cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包		
                cm.getChar().saveToDB(true);
                var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                var type = ii.getInventoryType(1112663); //获得装备的类形
                var toDrop = ii.randomizeStats(ii.getEquipById(1112663)).copy(); // 生成一个Equip类
                toDrop.setLocked(1);
                toDrop.setStr(999);
                toDrop.setDex(999);
                toDrop.setInt(999);
                toDrop.setLuk(999);
                toDrop.setHp(999);
                toDrop.setMp(999);
                toDrop.setMatk(999);
                toDrop.setWatk(999);
                toDrop.setMdef(999);
                toDrop.setWdef(999);
                toDrop.setAcc(999);
                toDrop.setAvoid(999);
                toDrop.setHands(999);
                toDrop.setSpeed(999);
                toDrop.setJump(999);
                cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
                cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包		
                cm.getChar().saveToDB(true);
		cm.gainItem(3010070,1);
		cm.teachSkill(80001010,1,1);
		cm.teachSkill(80001020,1,1);
		cm.warp(910000000);
		cm.sendOk("#r恭喜您成功领取全属性999的#v1112663##v1112135##v1112238#\r\n\r\n椅子:#v3010070#.\r\n\r\n坐骑:#v1902045#狮子坐骑");
                cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null, net.sf.odinms.tools.MaplePacketCreator.serverNotice(17,cm.getC().getChannel(),"[贵族公告]" + " : 恭喜玩家：" + cm.getPlayer().getName() +" 消费300领取丰厚奖励",true).getBytes());

		cm.dispose();



} 
}
