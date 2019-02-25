function start() {
    cm.sendSimple ("欢迎来到"+cm.GetSN()+"，有什么我能帮助你的吗？\r\n#d#e       ★ 当前账户剩余:#r"+player.GetMoney()+"元宝 #k★#k\r\n元宝比例#r[1rmb=2000元宝]：\r\n#b以下BT装备都是0级全职业,无任何佩戴限制.如没有您中意的装备，请联系客服添加\r\n#e#L0#购买#v1112663# #b全属性32767#k #r300000元宝 #k \r\n\r\n#e#L1#购买#v1112584# #b全属性32767#k #r300000元宝 #k \r\n\r\n#e#L2#购买#v1112683# #b全属性32767#k #r300000元宝 #k \r\n\r\n#e#L3#购买#v1112685# #b全属性32767#k #r300000元宝 #k \r\n\r\n#e#L4#购买#v1050100# #b全属性32767#k #r300000元宝 #k \r\n\r\n#e#L5#购买#v1051098# #b全属性32767#k #r300000元宝 #k \r\n\r\n#e#L6#购买#v1072239# #b全属性32767#k #r300000元宝 #k \r\n\r\n#e#L7#购买#v1032121# #b全属性32767#k #r300000元宝 #k \r\n\r\n")
}

function action(mode, type, selection) {
    cm.dispose();

    switch(selection){
        case 0:
            if(cm.getChar().GetMoney() >= 300000) {
                cm.sendOk("购买成功！ #r祝您祝您游戏愉快!#k");
                player.GainMoney(-300000); 
                var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                var type = ii.getInventoryType(1112663); //获得装备的类形
                var toDrop = ii.randomizeStats(ii.getEquipById(1112663)).copy(); // 生成一个Equip类
                toDrop.setLocked(1);
                toDrop.setStr(32767);
                toDrop.setDex(32767);
                toDrop.setInt(32767);
                toDrop.setLuk(32767);
                toDrop.setHp(32767);
                toDrop.setMp(32767);
                toDrop.setMatk(32767);
                toDrop.setWatk(32767);
                toDrop.setMdef(32767);
                toDrop.setWdef(32767);
                toDrop.setAcc(32767);
                toDrop.setAvoid(32767);
                toDrop.setHands(32767);
                toDrop.setSpeed(32767);
                toDrop.setJump(32767);
                cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
                cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包		
                cm.getChar().saveToDB(true);
                cm.dispose();
            }else{
                cm.sendOk("请确认你有50000元宝的时候在来购买!");
                cm.dispose();
            }
            break;
        case 1:
            if(cm.getChar().GetMoney() >= 300000){
                cm.sendOk("购买成功！ #r祝您游戏愉快!#k");
                player.GainMoney(-300000);
                var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                var type = ii.getInventoryType(1112584); //获得装备的类形
                var toDrop = ii.randomizeStats(ii.getEquipById(1112584)).copy(); // 生成一个Equip类
                toDrop.setLocked(1);
                toDrop.setStr(32767);
                toDrop.setDex(32767);
                toDrop.setInt(32767);
                toDrop.setLuk(32767);
                toDrop.setHp(32767);
                toDrop.setMp(32767);
                toDrop.setMatk(32767);
                toDrop.setWatk(32767);
                toDrop.setMdef(32767);
                toDrop.setWdef(32767);
                toDrop.setAcc(32767);
                toDrop.setAvoid(32767);
                toDrop.setHands(32767);
                toDrop.setSpeed(32767);
                toDrop.setJump(32767);
                cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
                cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包		
                cm.getChar().saveToDB(true);
                cm.dispose();
            }else{
                cm.sendOk("请确认你有50000元宝的时候在来购买!");
                cm.dispose();
            }
            break;
        case 2:
            if(cm.getChar().GetMoney() >= 300000){
                cm.sendOk("购买成功！ #r祝您游戏愉快!#k");
                player.GainMoney(-300000);
                var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                var type = ii.getInventoryType(1112683); //获得装备的类形
                var toDrop = ii.randomizeStats(ii.getEquipById(1112683)).copy(); // 生成一个Equip类
                toDrop.setLocked(1);
                toDrop.setStr(32767);
                toDrop.setDex(32767);
                toDrop.setInt(32767);
                toDrop.setLuk(32767);
                toDrop.setHp(32767);
                toDrop.setMp(32767);
                toDrop.setMatk(32767);
                toDrop.setWatk(32767);
                toDrop.setMdef(32767);
                toDrop.setWdef(32767);
                toDrop.setAcc(32767);
                toDrop.setAvoid(32767);
                toDrop.setHands(32767);
                toDrop.setSpeed(32767);
                toDrop.setJump(32767);
                cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
                cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包		
                cm.getChar().saveToDB(true);
                cm.dispose();
            }else{
                cm.sendOk("请确认你有50000元宝的时候在来购买!");
                cm.dispose();
            }
            break;
        case 3:
            if(cm.getChar().GetMoney() >= 300000){
                cm.sendOk("购买成功！ #r祝您游戏愉快!#k");
                player.GainMoney(-300000);
                var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                var type = ii.getInventoryType(1112685); //获得装备的类形
                var toDrop = ii.randomizeStats(ii.getEquipById(1112685)).copy(); // 生成一个Equip类
                toDrop.setLocked(1);
                toDrop.setStr(32767);
                toDrop.setDex(32767);
                toDrop.setInt(32767);
                toDrop.setLuk(32767);
                toDrop.setHp(32767);
                toDrop.setMp(32767);
                toDrop.setMatk(32767);
                toDrop.setWatk(32767);
                toDrop.setMdef(32767);
                toDrop.setWdef(32767);
                toDrop.setAcc(32767);
                toDrop.setAvoid(32767);
                toDrop.setHands(32767);
                toDrop.setSpeed(32767);
                toDrop.setJump(32767);
                cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
                cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包		
                cm.getChar().saveToDB(true);
                cm.dispose();
            }else{
                cm.sendOk("请确认你有50000元宝的时候在来购买!");
                cm.dispose();
            }
            break;
        case 4:
            if(cm.getChar().GetMoney() >= 300000){
                cm.sendOk("购买成功！ #r祝您游戏愉快!#k");
                player.GainMoney(-300000);
                var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                var type = ii.getInventoryType(1050100); //获得装备的类形
                var toDrop = ii.randomizeStats(ii.getEquipById(1050100)).copy(); // 生成一个Equip类
                toDrop.setLocked(1);
                toDrop.setStr(32767);
                toDrop.setDex(32767);
                toDrop.setInt(32767);
                toDrop.setLuk(32767);
                toDrop.setHp(32767);
                toDrop.setMp(32767);
                toDrop.setMatk(32767);
                toDrop.setWatk(32767);
                toDrop.setMdef(32767);
                toDrop.setWdef(32767);
                toDrop.setAcc(32767);
                toDrop.setAvoid(32767);
                toDrop.setHands(32767);
                toDrop.setSpeed(32767);
                toDrop.setJump(32767);
                cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
                cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包		
                cm.getChar().saveToDB(true);
                cm.dispose();
            }else{
                cm.sendOk("请确认你有50000元宝的时候在来购买!");
                cm.dispose();
            }
            break;
        case 5:
            if(cm.getChar().GetMoney() >= 300000){
                cm.sendOk("购买成功！ #r祝您游戏愉快!#k");
                player.GainMoney(-300000);
                var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                var type = ii.getInventoryType(1051098); //获得装备的类形
                var toDrop = ii.randomizeStats(ii.getEquipById(1051098)).copy(); // 生成一个Equip类
                toDrop.setLocked(1);
                toDrop.setStr(32767);
                toDrop.setDex(32767);
                toDrop.setInt(32767);
                toDrop.setLuk(32767);
                toDrop.setHp(32767);
                toDrop.setMp(32767);
                toDrop.setMatk(32767);
                toDrop.setWatk(32767);
                toDrop.setMdef(32767);
                toDrop.setWdef(32767);
                toDrop.setAcc(32767);
                toDrop.setAvoid(32767);
                toDrop.setHands(32767);
                toDrop.setSpeed(32767);
                toDrop.setJump(32767);
                cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
                cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包		
                cm.getChar().saveToDB(true);
                cm.dispose();
            }else{
                cm.sendOk("请确认你有50000元宝的时候在来购买!");
                cm.dispose();
            }
            break;
        case 6:
            if(cm.getChar().GetMoney() >= 300000){
                cm.sendOk("购买成功！ #r祝您游戏愉快!#k");
                player.GainMoney(-30000);
                var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                var type = ii.getInventoryType(1072239); //获得装备的类形
                var toDrop = ii.randomizeStats(ii.getEquipById(1072239)).copy(); // 生成一个Equip类
                toDrop.setLocked(1);
                toDrop.setStr(32767);
                toDrop.setDex(32767);
                toDrop.setInt(32767);
                toDrop.setLuk(32767);
                toDrop.setHp(32767);
                toDrop.setMp(32767);
                toDrop.setMatk(32767);
                toDrop.setWatk(32767);
                toDrop.setMdef(32767);
                toDrop.setWdef(32767);
                toDrop.setAcc(32767);
                toDrop.setAvoid(32767);
                toDrop.setHands(32767);
                toDrop.setSpeed(32767);
                toDrop.setJump(32767);
                cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
                cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包		
                cm.getChar().saveToDB(true);
                cm.dispose();
            }else{
                cm.sendOk("请确认你有50000元宝的时候在来购买!");
                cm.dispose();
            }
            break;
        case 7:
            if(cm.getChar().GetMoney() >= 300000){
                cm.sendOk("购买成功！ #r祝您游戏愉快!#k");
                player.GainMoney(-300000);
                var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                var type = ii.getInventoryType(1032121); //获得装备的类形
                var toDrop = ii.randomizeStats(ii.getEquipById(1032121)).copy(); // 生成一个Equip类
                toDrop.setLocked(1);
                toDrop.setStr(32767);
                toDrop.setDex(32767);
                toDrop.setInt(32767);
                toDrop.setLuk(32767);
                toDrop.setHp(32767);
                toDrop.setMp(32767);
                toDrop.setMatk(32767);
                toDrop.setWatk(32767);
                toDrop.setMdef(32767);
                toDrop.setWdef(32767);
                toDrop.setAcc(32767);
                toDrop.setAvoid(32767);
                toDrop.setHands(32767);
                toDrop.setSpeed(32767);
                toDrop.setJump(32767);

                cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
                cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包		
                cm.getChar().saveToDB(true);
                cm.dispose();
            }else{
                cm.sendOk("请确认你有50000元宝的时候在来购买!");
                cm.dispose();

            };
    }
}
