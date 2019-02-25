function start() {
    cm.sendSimple ("欢迎来到"+cm.GetSN()+"，有什么我能帮助你的吗？\r\n\r\n#r1金币寻找失去的枫叶\r\n\r\n#L0#我要拾回我丢失的#v1012101#")
}

function action(mode, type, selection) {
    cm.dispose();

    switch(selection){
        case 0:
            if(player.GetMoney() >= 0) {
                cm.sendOk("购买成功！ #r祝您祝您游戏愉快!#k");
                player.GainMoney(-0); 
                    var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                    var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                    var type = ii.getInventoryType(1012101); //获得装备的类形
                    var toDrop = ii.randomizeStats(ii.getEquipById(1012101)).copy(); // 生成一个Equip类
                    toDrop.AddFlag(net.sf.odinms.server.constants.InventoryConstants.Items.Flags.UNTRADEABLE);
                    toDrop.setStr(3000);
                    toDrop.setDex(3000);
                    toDrop.setInt(3000);
                    toDrop.setLuk(3000);
                    toDrop.setHp(3000);
                    toDrop.setMp(3000);
                    toDrop.setMatk(1);
                    toDrop.setWatk(1);
                    toDrop.setMdef(1);
                    toDrop.setWdef(1);
                    toDrop.setAcc(1);
                    toDrop.setAvoid(1);
                    toDrop.setHands(1);
                    toDrop.setSpeed(1);
                    toDrop.setJump(1);
                cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
                cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包		
                cm.getChar().saveToDB(true);
                cm.dispose();
            }else{
                cm.sendOk("请你确认你有1金币");
                cm.dispose();
            };
    }
}
