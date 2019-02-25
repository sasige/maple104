function start() {
    cm.sendSimple ("欢迎来到"+cm.GetSN()+"，有什么我能帮助你的吗？\r\n#d#e       ★ 当前账户剩余:#"+player.GetMoney()+"元宝 #k★#k\r\n元宝比例#r[1rmb=2000元宝]：\r\n#b以下BT装备都是0级全职业,无任何佩戴限制.如没有您中意的装备，请联系客服添加\r\n#e#L0#购买#v1142179# #b全属性3000#k #r35000元宝 #k \r\n\r\n#e#L1#购买#v1022048# #b全属性3000#k #r35000元宝 #k \r\n\r\n#e#L2#购买#v1012057# #b全属性3000#k #r35000元宝 #k \r\n\r\n#e#L3#购买#v1032024# #b全属性3000#k #r35000元宝 #k \r\n\r\n#e#L4#购买#v1002186# #b全属性3000#k #r35000元宝 #k \r\n\r\n#e#L5#购买#v1102039# #b全属性3000#k #r35000元宝 #k \r\n\r\n#e#L6#购买#v1082102# #b全属性3000#k #r35000元宝 #k \r\n\r\n#e#L7#购买#v1072153# #b全属性3000#k #r35000元宝 #k \r\n\r\n")
}

function action(mode, type, selection) {
    cm.dispose();

    switch(selection){
        case 0:
            if(cm.getChar().GetMoney() >= 35000) {
                cm.sendOk("购买成功！ #r祝您祝您游戏愉快!#k");
                player.GainMoney(-35000); 
                var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                var type = ii.getInventoryType(1142179); //获得装备的类形
                var toDrop = ii.randomizeStats(ii.getEquipById(1142179)).copy(); // 生成一个Equip类
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
                cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null,  net.sf.odinms.tools.MaplePacketCreator.getItemMegas(cm.getC().getChannel(),"[贵族通告]" + " : " + "大老板『"+cm.getChar().getName()+"』成功购买全属性3000的王座勋章,购买请找NPC雪天使!",toDrop, true).getBytes());
                cm.dispose();
            }else{
                cm.sendOk("请确认你有50000元宝的时候在来购买!");
                cm.dispose();
            }
            break;
        case 1:
            if(cm.getChar().GetMoney() >= 35000){
                cm.sendOk("购买成功！ #r祝您游戏愉快!#k");
                player.GainMoney(-35000);
                var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                var type = ii.getInventoryType(1022048); //获得装备的类形
                var toDrop = ii.randomizeStats(ii.getEquipById(1022048)).copy(); // 生成一个Equip类
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
                cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null,  net.sf.odinms.tools.MaplePacketCreator.getItemMegas(cm.getC().getChannel(),"[贵族通告]" + " : " + "大老板『"+cm.getChar().getName()+"』成功购买全属性3000的透明眼镜,购买请找NPC雪天使!",toDrop, true).getBytes());
                cm.dispose();
            }else{
                cm.sendOk("请确认你有50000元宝的时候在来购买!");
                cm.dispose();
            }
            break;
        case 2:
            if(cm.getChar().GetMoney() >= 35000){
                cm.sendOk("购买成功！ #r祝您游戏愉快!#k");
                player.GainMoney(-35000);
                var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                var type = ii.getInventoryType(1012057); //获得装备的类形
                var toDrop = ii.randomizeStats(ii.getEquipById(1012057)).copy(); // 生成一个Equip类
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
                cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null,  net.sf.odinms.tools.MaplePacketCreator.getItemMegas(cm.getC().getChannel(),"[贵族通告]" + " : " + "大老板『"+cm.getChar().getName()+"』成功购买全属性3000的透明面具,购买请找NPC雪天使!",toDrop, true).getBytes());
                cm.dispose();
            }else{
                cm.sendOk("请确认你有50000元宝的时候在来购买!");
                cm.dispose();
            }
            break;
        case 3:
            if(cm.getChar().GetMoney() >= 35000){
                cm.sendOk("购买成功！ #r祝您游戏愉快!#k");
                player.GainMoney(-35000);
                var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                var type = ii.getInventoryType(1032024); //获得装备的类形
                var toDrop = ii.randomizeStats(ii.getEquipById(1032024)).copy(); // 生成一个Equip类
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
                cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null,  net.sf.odinms.tools.MaplePacketCreator.getItemMegas(cm.getC().getChannel(),"[贵族通告]" + " : " + "大老板『"+cm.getChar().getName()+"』成功购买全属性3000的透明耳环,购买请找NPC雪天使!",toDrop, true).getBytes());
                cm.dispose();
            }else{
                cm.sendOk("请确认你有50000元宝的时候在来购买!");
                cm.dispose();
            }
            break;
        case 4:
            if(cm.getChar().GetMoney() >= 35000){
                cm.sendOk("购买成功！ #r祝您游戏愉快!#k");
                player.GainMoney(-35000);
                var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                var type = ii.getInventoryType(1002186); //获得装备的类形
                var toDrop = ii.randomizeStats(ii.getEquipById(1002186)).copy(); // 生成一个Equip类
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
                cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null,  net.sf.odinms.tools.MaplePacketCreator.getItemMegas(cm.getC().getChannel(),"[贵族通告]" + " : " + "大老板『"+cm.getChar().getName()+"』成功购买全属性3000的透明帽,购买请找NPC雪天使!",toDrop, true).getBytes());
                cm.dispose();
            }else{
                cm.sendOk("请确认你有50000元宝的时候在来购买!");
                cm.dispose();
            }
            break;
        case 5:
            if(cm.getChar().GetMoney() >= 35000){
                cm.sendOk("购买成功！ #r祝您游戏愉快!#k");
                player.GainMoney(-35000);
                var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                var type = ii.getInventoryType(1102039); //获得装备的类形
                var toDrop = ii.randomizeStats(ii.getEquipById(1102039)).copy(); // 生成一个Equip类
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
                cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null,  net.sf.odinms.tools.MaplePacketCreator.getItemMegas(cm.getC().getChannel(),"[贵族通告]" + " : " + "大老板『"+cm.getChar().getName()+"』成功购买全属性3000的透明披风,购买请找NPC雪天使!",toDrop, true).getBytes());
                cm.dispose();
            }else{
                cm.sendOk("请确认你有50000元宝的时候在来购买!");
                cm.dispose();
            }
            break;
        case 6:
            if(cm.getChar().GetMoney() >= 35000){
                cm.sendOk("购买成功！ #r祝您游戏愉快!#k");
                player.GainMoney(-35000);
                var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                var type = ii.getInventoryType(1082102); //获得装备的类形
                var toDrop = ii.randomizeStats(ii.getEquipById(1082102)).copy(); // 生成一个Equip类
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
                cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null,  net.sf.odinms.tools.MaplePacketCreator.getItemMegas(cm.getC().getChannel(),"[贵族通告]" + " : " + "大老板『"+cm.getChar().getName()+"』成功购买全属性3000的透明手套,购买请找NPC雪天使!",toDrop, true).getBytes());
                cm.dispose();
            }else{
                cm.sendOk("请确认你有50000元宝的时候在来购买!");
                cm.dispose();
            }
            break;
        case 7:
            if(cm.getChar().GetMoney() >= 35000){
                cm.sendOk("购买成功！ #r祝您游戏愉快!#k");
                player.GainMoney(-35000);
                var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                var type = ii.getInventoryType(1072153); //获得装备的类形
                var toDrop = ii.randomizeStats(ii.getEquipById(1072153)).copy(); // 生成一个Equip类
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
                cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null,  net.sf.odinms.tools.MaplePacketCreator.getItemMegas(cm.getC().getChannel(),"[贵族通告]" + " : " + "大老板『"+cm.getChar().getName()+"』成功购买全属性3000的透明鞋子,购买请找NPC雪天使!",toDrop, true).getBytes());
                cm.dispose();
            }else{
                cm.sendOk("请确认你有50000元宝的时候在来购买!");
                cm.dispose();

            };
    }
}
