function start() {
var ico11 = "#fEffect/CharacterEff/1112903/0/0#";
	if (cm.getChar().getMapId() == 200101500){
	    if(cm.countMonster()<0){
		 cm.sendSimple ("您正在挑战BOSS,请选把它消灭了再来找我召唤!!如果您想放弃挑战,我可以把你送回市场! \r\n \r\n    #L8##r回到市场#l");
	    }else{
   		 cm.sendSimple ("#b"+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+"\r\n"+ico11+"#L0##b启动BOSS系统·召唤#l          \r\n"+ico11+"\r\n"+ico11+"#k"+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+""+ico11+"\r\n#r#L1#我要升级#v1002972##l           #L2#我要过关#v1002972##l");
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
cm.serverNotice("『运气财富BOSS』：【"+ cm.getChar().getName() +"】已经召唤运气财富！！爆好多好多东西的哦。哇哈哈");
cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『财富大大』"+" : "+"亲。你来把·喜欢我就来爆我吧！"))
	cm.dispose();}


} if (selection == 2) {
        if (!cm.haveItem(1002972,1)) {
        cm.sendOk("抱歉，你没有1顶#v1002972#无法穿越到下一关"); 
        }else{
	cm.gainItem(1002972,-1);
	cm.gainItem(3994471,1);
	cm.warp(910510100, 0);
cm.sendOk("#r哈哈,您太厉害了,过关了#v3994471#这个道具用于下一关召唤");
	cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『维纳斯女皇』对『"+ cm.getChar().getName() +"』"+" : "+"使用必杀技芭蕉扇,穿越到下一关"))    
	cm.dispose();}

}if (selection == 1) {
        if (!cm.haveItem(1002972,2)) {
        cm.sendOk("#r抱歉，你没有2顶#v1002972#我无法为您合成超级属性王冠");
        } else if (!cm.haveItem(4002000,100)) {
        cm.sendOk("#r抱歉，你没有100张#v4002000#我无法为您合成超级属性王冠"); 
        } else if (!cm.haveItem(1112586,1)) {
        cm.sendOk("#r抱歉，你没有1枚#v1112586#我无法为您合成超级属性王冠");
        }else{
	cm.gainItem(1002972,-1);
                var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                var type = ii.getInventoryType(1002972); //获得装备的类形
                var toDrop = ii.randomizeStats(ii.getEquipById(1002972)).copy(); // 生成一个Equip类
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
                cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null,  net.sf.odinms.tools.MaplePacketCreator.getItemMegas(cm.getC().getChannel(),"[超级奖励]" + " : " + "超级玩家『"+cm.getChar().getName()+"』完美通关幸运副本获得999全属性的欧碧拉法冠",toDrop, true).getBytes());
        cm.warp(910000000);
                cm.dispose();


	cm.dispose();}

}
}