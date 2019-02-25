function start() {
	if (cm.getChar().getMapId() == 910510100){
	    if(cm.countMonster()<0){
		 cm.sendSimple ("您正在挑战BOSS,请选把它消灭了再来找我召唤!!如果您想放弃挑战,我可以把你送回市场! \r\n \r\n    #L8##r回到市场#l");
	    }else{
   		 cm.sendSimple ("#b★★★★★★★★★★★★★★★★★★★★★★★★★★★\r\n★★★★★★★#r您好请确保背包有足够位置#k#b★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★\r\n              #r看看我有什么奖品给您吧#b\r\n#L12#[使用#v4000422##v4000423##v4000424##v4000425#兑换#v3010070#]\r\n#L11#[使用#v4000422##v4000423##v4000424##v4000425#升级#v1002972#]\r\n#L0##k#r[使用#v4000422##v4000423##v4000424##v4000425# 换 #v3010279#]\r\n#L1#[使用#v4000422##v4000423##v4000424##v4000425# 换 #v3010290#]\r\n#L2#[使用#v4000422##v4000423##v4000424##v4000425# 换 #v3010286#]\r\n#L3#[使用#v4000422##v4000423##v4000424##v4000425# 换  #v1112135#]\r\n#L4#[使用#v4000422##v4000423##v4000424##v4000425# 换  #v1112238#]\r\n#L5#[使用#v4000422##v4000423##v4000424##v4000425# 换 #v1112663#]\r\n#L6#[使用#v4000422##v4000423##v4000424##v4000425# 换 #v3010135#]\r\n#L7#[使用#v4000422##v4000423##v4000424##v4000425# 换 #v1702342#]\r\n#L8#[使用#v4000422##v4000423##v4000424##v4000425# 换 #v1702334#]\r\n#L9#[使用#v4000422##v4000423##v4000424##v4000425# 换 #v1702346#]\r\n#L10#[使用#v4000422##v4000423##v4000424##v4000425# 换 #v1322102#]");
	    }
	} else {
	    cm.sendOk("找我什么事，想要启动我的力量吗，你需要足够的条件")
	}
}
function action(mode, type, selection) {
cm.dispose();
if (selection == 0) {
        if (!cm.haveItem(4000422,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000422#无法为你开启#e#r");
        } else if (!cm.haveItem(4000423,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000423#无法为你开启#e#r"); 
        } else if (!cm.haveItem(4000424,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000424#无法为你开启#e#r"); 
        } else if (!cm.haveItem(4000425,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000425#无法为你开启#e#r");  
        }else{
	cm.gainItem(4000422,-1);
	cm.gainItem(4000423,-1);
	cm.gainItem(4000424,-1);
	cm.gainItem(4000425,-1);
	cm.gainItem(3010279,1);
        cm.warp(910000000);
cm.sendOk("#r哈哈,您太厉害了,您简直就是冒险王,神秘道具奖励给您了");
cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『"+ cm.getChar().getName() +"』神人"+" : "+"完成了,神秘副本,获得了超级神秘道具"))
cm.dispose(); }


}if (selection == 1) {
        if (!cm.haveItem(4000422,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000422#无法为你开启#e#r");
        } else if (!cm.haveItem(4000423,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000423#无法为你开启#e#r"); 
        } else if (!cm.haveItem(4000424,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000424#无法为你开启#e#r"); 
        } else if (!cm.haveItem(4000425,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000425#无法为你开启#e#r");  
        }else{
	cm.gainItem(4000422,-1);
	cm.gainItem(4000423,-1);
	cm.gainItem(4000424,-1);
	cm.gainItem(4000425,-1);
	cm.gainItem(3010290,1);
        cm.warp(910000000);
cm.sendOk("#r哈哈,您太厉害了,您简直就是冒险王,神秘道具奖励给您了");
cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『"+ cm.getChar().getName() +"』神人"+" : "+"完成了,神秘副本,获得了超级神秘道具"))
cm.dispose(); }


}if (selection == 2) {
        if (!cm.haveItem(4000422,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000422#无法为你开启#e#r");
        } else if (!cm.haveItem(4000423,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000423#无法为你开启#e#r"); 
        } else if (!cm.haveItem(4000424,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000424#无法为你开启#e#r"); 
        } else if (!cm.haveItem(4000425,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000425#无法为你开启#e#r");  
        }else{
	cm.gainItem(4000422,-1);
	cm.gainItem(4000423,-1);
	cm.gainItem(4000424,-1);
	cm.gainItem(4000425,-1);
	cm.gainItem(3010286,1);
        cm.warp(910000000);
cm.sendOk("#r哈哈,您太厉害了,您简直就是冒险王,神秘道具奖励给您了");
cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『"+ cm.getChar().getName() +"』神人"+" : "+"完成了,神秘副本,获得了超级神秘道具"))
cm.dispose(); }

}if (selection == 3) {
        if (!cm.haveItem(4000422,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000422#无法为你开启#e#r");
        } else if (!cm.haveItem(4000423,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000423#无法为你开启#e#r"); 
        } else if (!cm.haveItem(4000424,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000424#无法为你开启#e#r"); 
        } else if (!cm.haveItem(4000425,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000425#无法为你开启#e#r");  
        }else{
	cm.gainItem(4000422,-1);
	cm.gainItem(4000423,-1);
	cm.gainItem(4000424,-1);
	cm.gainItem(4000425,-1);
	cm.gainItem(1112135,1);
        cm.warp(910000000);
cm.sendOk("#r哈哈,您太厉害了,您简直就是冒险王,神秘道具奖励给您了");
cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『"+ cm.getChar().getName() +"』神人"+" : "+"完成了,神秘副本,获得了超级神秘道具"))
cm.dispose(); }

}if (selection == 4) {
        if (!cm.haveItem(4000422,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000422#无法为你开启#e#r");
        } else if (!cm.haveItem(4000423,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000423#无法为你开启#e#r"); 
        } else if (!cm.haveItem(4000424,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000424#无法为你开启#e#r"); 
        } else if (!cm.haveItem(4000425,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000425#无法为你开启#e#r");  
        }else{
	cm.gainItem(4000422,-1);
	cm.gainItem(4000423,-1);
	cm.gainItem(4000424,-1);
	cm.gainItem(4000425,-1);
	cm.gainItem(1112238,1);
        cm.warp(910000000);
cm.sendOk("#r哈哈,您太厉害了,您简直就是冒险王,神秘道具奖励给您了");
cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『"+ cm.getChar().getName() +"』神人"+" : "+"完成了,神秘副本,获得了超级神秘道具"))
cm.dispose(); }

}if (selection == 5) {
        if (!cm.haveItem(4000422,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000422#无法为你开启#e#r");
        } else if (!cm.haveItem(4000423,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000423#无法为你开启#e#r"); 
        } else if (!cm.haveItem(4000424,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000424#无法为你开启#e#r"); 
        } else if (!cm.haveItem(4000425,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000425#无法为你开启#e#r");  
        }else{
	cm.gainItem(4000422,-1);
	cm.gainItem(4000423,-1);
	cm.gainItem(4000424,-1);
	cm.gainItem(4000425,-1);
	cm.gainItem(1112663,1);
        cm.warp(910000000);
cm.sendOk("#r哈哈,您太厉害了,您简直就是冒险王,神秘道具奖励给您了");
cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『"+ cm.getChar().getName() +"』神人"+" : "+"完成了,神秘副本,获得了超级神秘道具"))
cm.dispose(); }


}if (selection == 6) {
        if (!cm.haveItem(4000422,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000422#无法为你开启#e#r");
        } else if (!cm.haveItem(4000423,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000423#无法为你开启#e#r"); 
        } else if (!cm.haveItem(4000424,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000424#无法为你开启#e#r"); 
        } else if (!cm.haveItem(4000425,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000425#无法为你开启#e#r");  
        }else{
	cm.gainItem(4000422,-1);
	cm.gainItem(4000423,-1);
	cm.gainItem(4000424,-1);
	cm.gainItem(4000425,-1);
	cm.gainItem(3010135,1);
        cm.warp(910000000);
cm.sendOk("#r哈哈,您太厉害了,您简直就是冒险王,神秘道具奖励给您了");
cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『"+ cm.getChar().getName() +"』神人"+" : "+"完成了,神秘副本,获得了超级神秘道具"))
cm.dispose(); }


}if (selection == 7) {
        if (!cm.haveItem(4000422,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000422#无法为你开启#e#r");
        } else if (!cm.haveItem(4000423,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000423#无法为你开启#e#r"); 
        } else if (!cm.haveItem(4000424,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000424#无法为你开启#e#r"); 
        } else if (!cm.haveItem(4000425,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000425#无法为你开启#e#r");  
        }else{
	cm.gainItem(4000422,-1);
	cm.gainItem(4000423,-1);
	cm.gainItem(4000424,-1);
	cm.gainItem(4000425,-1);
	cm.gainItem(1702342,1);
        cm.warp(910000000);
cm.sendOk("#r哈哈,您太厉害了,您简直就是冒险王,神秘道具奖励给您了");
cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『"+ cm.getChar().getName() +"』神人"+" : "+"完成了,神秘副本,获得了超级神秘道具"))
cm.dispose(); }


}if (selection == 8) {
        if (!cm.haveItem(4000422,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000422#无法为你开启#e#r");
        } else if (!cm.haveItem(4000423,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000423#无法为你开启#e#r"); 
        } else if (!cm.haveItem(4000424,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000424#无法为你开启#e#r"); 
        } else if (!cm.haveItem(4000425,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000425#无法为你开启#e#r");  
        }else{
	cm.gainItem(4000422,-1);
	cm.gainItem(4000423,-1);
	cm.gainItem(4000424,-1);
	cm.gainItem(4000425,-1);
	cm.gainItem(1702334,1);
        cm.warp(910000000);
cm.sendOk("#r哈哈,您太厉害了,您简直就是冒险王,神秘道具奖励给您了");
cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『"+ cm.getChar().getName() +"』神人"+" : "+"完成了,神秘副本,获得了超级神秘道具"))
cm.dispose(); }


}if (selection == 9) {
        if (!cm.haveItem(4000422,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000422#无法为你开启#e#r");
        } else if (!cm.haveItem(4000423,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000423#无法为你开启#e#r"); 
        } else if (!cm.haveItem(4000424,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000424#无法为你开启#e#r"); 
        } else if (!cm.haveItem(4000425,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000425#无法为你开启#e#r");  
        }else{
	cm.gainItem(4000422,-1);
	cm.gainItem(4000423,-1);
	cm.gainItem(4000424,-1);
	cm.gainItem(4000425,-1);
	cm.gainItem(1702346,1);
        cm.warp(910000000);
cm.sendOk("#r哈哈,您太厉害了,您简直就是冒险王,神秘道具奖励给您了");
cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『"+ cm.getChar().getName() +"』神人"+" : "+"完成了,神秘副本,获得了超级神秘道具"))
cm.dispose(); }


}if (selection == 10) {
        if (!cm.haveItem(4000422,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000422#无法为你开启#e#r");
        } else if (!cm.haveItem(4000423,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000423#无法为你开启#e#r"); 
        } else if (!cm.haveItem(4000424,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000424#无法为你开启#e#r"); 
        } else if (!cm.haveItem(4000425,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000425#无法为你开启#e#r");  
        }else{
	cm.gainItem(4000422,-1);
	cm.gainItem(4000423,-1);
	cm.gainItem(4000424,-1);
	cm.gainItem(4000425,-1);
	cm.gainItem(1322102,1);
        cm.warp(910000000);
cm.sendOk("#r哈哈,您太厉害了,您简直就是冒险王,神秘道具奖励给您了");
cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『"+ cm.getChar().getName() +"』神人"+" : "+"完成了,神秘副本,获得了超级神秘道具"))
cm.dispose(); }


}if (selection == 11) {
        if (!cm.haveItem(1002972,3)) {
        cm.sendOk("#r抱歉，你没有3顶#v1002972#我无法为您合成超级属性王冠");
        } else if (!cm.haveItem(4002000,100)) {
        cm.sendOk("#r抱歉，你没有100张#v4002000#我无法为您合成超级属性王冠"); 
        } else if (!cm.haveItem(1112586,2)) {
        cm.sendOk("#r抱歉，你没有2枚#v1112586#我无法为您合成超级属性王冠");
        }else{
	cm.gainItem(1002972,-3);
	cm.gainItem(4002000,-100);
	cm.gainItem(1112586,-2);
                var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                var type = ii.getInventoryType(1002972); //获得装备的类形
                var toDrop = ii.randomizeStats(ii.getEquipById(1002972)).copy(); // 生成一个Equip类
                toDrop.setLocked(1);
                toDrop.setStr(3000);
                toDrop.setDex(3000);
                toDrop.setInt(3000);
                toDrop.setLuk(3000);
                toDrop.setHp(3000);
                toDrop.setMp(3000);
                toDrop.setMatk(3000);
                toDrop.setWatk(3000);
                toDrop.setMdef(3000);
                toDrop.setWdef(3000);
                toDrop.setAcc(3000);
                toDrop.setAvoid(3000);
                toDrop.setHands(3000);
                toDrop.setSpeed(3000);
                toDrop.setJump(3000);
                cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
                cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包		
                cm.getChar().saveToDB(true);
                cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null,  net.sf.odinms.tools.MaplePacketCreator.getItemMegas(cm.getC().getChannel(),"[超级奖励]" + " : " + "超级玩家『"+cm.getChar().getName()+"』完美通关幸运副本获得3000全属性的欧碧拉法冠",toDrop, true).getBytes());
        cm.warp(910000000);
cm.dispose(); }

}if (selection == 12) {
        if (!cm.haveItem(4000422,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000422#无法为你开启#e#r");
        } else if (!cm.haveItem(4000423,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000423#无法为你开启#e#r"); 
        } else if (!cm.haveItem(4000424,1)) {
        cm.sendOk("#r抱歉，你没有1张#v4000424#无法为你开启#e#r"); 
        } else if (!cm.haveItem(4000425,1)) {
        cm.sendOk("#r抱歉，你没有20张#v4000425#无法为你开启#e#r");  
	} else if (!cm.haveItem(1002972,24)) {
        cm.sendOk("#r抱歉，你没有24顶#v1002972#我无法为您合成超级巨无霸椅子");
        } else if (!cm.haveItem(4002000,100)) {
        cm.sendOk("#r抱歉，你没有100张#v4002000#我无法为您合成超级巨无霸椅子"); 
        }else{
	cm.gainItem(4002000,-100);
	cm.gainItem(1002972,-24);
	cm.gainItem(4000422,-1);
	cm.gainItem(4000423,-1);
	cm.gainItem(4000424,-1);
	cm.gainItem(4000425,-1);
	cm.gainItem(3010070,1);
        cm.warp(910000000);
cm.sendOk("#r哈哈,您太厉害了,您简直就是冒险王,神秘道具奖励给您了");
cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『"+ cm.getChar().getName() +"』神人"+" : "+"完成了,神秘副本,获得了巨无霸椅子"))
cm.dispose(); }


}
}