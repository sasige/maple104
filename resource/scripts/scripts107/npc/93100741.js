function start() {
    cm.sendSimple ("欢迎来到099单机冒险岛，有什么我能帮助你的吗？\r\n#d#e       ★ 当前账户剩余:#r"+cm.getChar().getCSPoints(0)+"点券 #k★#k\r\n点券比例#r[1rmb=1000点券]：\r\n#b以下BT装备都是0级全职业,无任何佩戴限制.如没有您中意的装备，请联系客服添加\r\n#e#L0#购买#v1142179# #b全属性999#k #r40000点券 #k \r\n\r\n#e#L1#购买#v1022048# #b全属性999#k #r40000点券 #k \r\n\r\n#e#L2#购买#v1012057# #b全属性999#k #r40000点券 #k \r\n\r\n#e#L3#购买#v1032024# #b全属性999#k #r40000点券 #k \r\n\r\n#e#L4#购买#v1002186# #b全属性999#k #r40000点券 #k \r\n\r\n#e#L5#购买#v1102039# #b全属性999#k #r40000点券 #k \r\n\r\n#e#L6#购买#v1082102# #b全属性999#k #r40000点券 #k \r\n\r\n#e#L7#购买#v1072153# #b全属性999#k #r40000点券 #k \r\n\r\n")
    }

function action(mode, type, selection) {
        cm.dispose();

    switch(selection){
        case 0: 
	if(cm.getChar().getNX() >= 40000) {
            cm.sendOk("购买成功！ #r祝您祝您游戏愉快!#k");
            cm.gainNX(-40000); 
            var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
            var type = ii.getInventoryType(1142179); //获得装备的类形
            var toDrop = ii.randomizeStats(ii.getEquipById(1142179)).copy(); // 生成一个Equip类
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
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了1个全属性999的王座收藏家绵羊型寻找，购买请找NPC雪天使！");
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了1个全属性999的王座收藏家绵羊型寻找，购买请找NPC雪天使！");
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了1个全属性999的王座收藏家绵羊型寻找，购买请找NPC雪天使！");
            cm.dispose();
            }else{
            cm.sendOk("请确认你有50000点券的时候在来购买!");
            cm.dispose();
            }
        break;
        case 1: 
            if(cm.getChar().getNX() >= 40000){
            cm.sendOk("购买成功！ #r祝您游戏愉快!#k");
            cm.gainNX(-40000);
            var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
            var type = ii.getInventoryType(1022048); //获得装备的类形
            var toDrop = ii.randomizeStats(ii.getEquipById(1022048)).copy(); // 生成一个Equip类
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
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了1个全属性999的透明眼镜，购买请找NPC雪天使！");
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了1个全属性999的透明眼镜，购买请找NPC雪天使！");
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了1个全属性999的透明眼镜，购买请找NPC雪天使！");
            cm.dispose();
            }else{
            cm.sendOk("请确认你有50000点券的时候在来购买!");
            cm.dispose();
            }
        break;
        case 2: 
            if(cm.getChar().getNX() >= 40000){
            cm.sendOk("购买成功！ #r祝您游戏愉快!#k");
            cm.gainNX(-40000);
            var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
            var type = ii.getInventoryType(1012057); //获得装备的类形
            var toDrop = ii.randomizeStats(ii.getEquipById(1012057)).copy(); // 生成一个Equip类
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
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了1个全属性999的透明面具，购买请找NPC雪天使！");
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了1个全属性999的透明面具，购买请找NPC雪天使！");
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了1个全属性999的透明面具，购买请找NPC雪天使！");
            cm.dispose();
            }else{
            cm.sendOk("请确认你有50000点券的时候在来购买!");
            cm.dispose();
            }
        break;
        case 3: 
            if(cm.getChar().getNX() >= 40000){
            cm.sendOk("购买成功！ #r祝您游戏愉快!#k");
            cm.gainNX(-40000);
            var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
            var type = ii.getInventoryType(1032024); //获得装备的类形
            var toDrop = ii.randomizeStats(ii.getEquipById(1032024)).copy(); // 生成一个Equip类
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
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了1个全属性999的透明耳环，购买请找NPC雪天使！");
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了1个全属性999的透明耳环，购买请找NPC雪天使！");
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了1个全属性999的透明耳环，购买请找NPC雪天使！");
            cm.dispose();
            }else{
            cm.sendOk("请确认你有50000点券的时候在来购买!");
            cm.dispose();
            }
        break;
        case 4: 
            if(cm.getChar().getNX() >= 40000){
            cm.sendOk("购买成功！ #r祝您游戏愉快!#k");
            cm.gainNX(-40000);
            var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
            var type = ii.getInventoryType(1002186); //获得装备的类形
            var toDrop = ii.randomizeStats(ii.getEquipById(1002186)).copy(); // 生成一个Equip类
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
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了1个全属性999的透明帽，购买请找NPC雪天使！");
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了1个全属性999的透明帽，购买请找NPC雪天使！");
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了1个全属性999的透明帽，购买请找NPC雪天使！");
            cm.dispose();
            }else{
            cm.sendOk("请确认你有50000点券的时候在来购买!");
            cm.dispose();
            }
        break;
        case 5: 
            if(cm.getChar().getNX() >= 40000){
            cm.sendOk("购买成功！ #r祝您游戏愉快!#k");
            cm.gainNX(-40000);
            var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
            var type = ii.getInventoryType(1102039); //获得装备的类形
            var toDrop = ii.randomizeStats(ii.getEquipById(1102039)).copy(); // 生成一个Equip类
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
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了1个全属性999的透明披风，购买请找NPC雪天使！");
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了1个全属性999的透明披风，购买请找NPC雪天使！");
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了1个全属性999的透明披风，购买请找NPC雪天使！");
            cm.dispose();
            }else{
            cm.sendOk("请确认你有50000点券的时候在来购买!");
            cm.dispose();
            }
        break;
        case 6: 
            if(cm.getChar().getNX() >= 40000){
            cm.sendOk("购买成功！ #r祝您游戏愉快!#k");
            cm.gainNX(-40000);
            var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
            var type = ii.getInventoryType(1082102); //获得装备的类形
            var toDrop = ii.randomizeStats(ii.getEquipById(1082102)).copy(); // 生成一个Equip类
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
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了1个全属性999的透明手套，购买请找NPC雪天使！");
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了1个全属性999的透明手套，购买请找NPC雪天使！");
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了1个全属性999的透明手套，购买请找NPC雪天使！");
            cm.dispose();
            }else{
            cm.sendOk("请确认你有50000点券的时候在来购买!");
            cm.dispose();
            }
        break;
        case 7: 
            if(cm.getChar().getNX() >= 40000){
            cm.sendOk("购买成功！ #r祝您游戏愉快!#k");
            cm.gainNX(-40000);
            var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
            var type = ii.getInventoryType(1072153); //获得装备的类形
            var toDrop = ii.randomizeStats(ii.getEquipById(1072153)).copy(); // 生成一个Equip类
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
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了1个全属性999的透明鞋，购买请找NPC雪天使！");
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了1个全属性999的透明鞋，购买请找NPC雪天使！");
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了1个全属性999的透明鞋，购买请找NPC雪天使！");
            cm.dispose();
            }else{
            cm.sendOk("请确认你有50000点券的时候在来购买!");
            cm.dispose();

            };
        }
    }
