function start() {
    cm.sendSimple ("欢迎来到"+cm.GetSN()+"，有什么我能帮助你的吗？\r\n#d#e       ★ 当前账户剩余:"+player.GetMoney()+"元宝 #k★#k\r\n元宝比例#r[1rmb=2000元宝]：\r\n#b以下BT装备都是0级全职业,无任何佩戴限制.如没有您中意的装备，请联系客服添加\r\n#e#L0#购买#v1002513# #b全属性32767#k #r300000元宝 #k \r\n\r\n#e#L1#购买#v1082149# #b全属性32767#k #r300000元宝 #k \r\n\r\n#e#L2#购买#v1012239# #b全属性32767#k #r300000元宝 #k \r\n\r\n#e#L3#购买#v1032093# #b全属性32767#k #r300000元宝 #k \r\n\r\n#e#L4#购买#v1132085# #b全属性32767#k #r300000元宝 #k \r\n\r\n#e#L5#购买#v1122104# #b全属性32767#k #r300000元宝 #k \r\n\r\n#e#L6#购买#v1102382# #b全属性32767#k #r300000元宝 #k \r\n\r\n#e#L7#购买#v1072005# #b全属性32767#k #r300000元宝 #k\r\n\r\n \r\n\r\n")
}

function action(mode, type, selection) {
    cm.dispose();

    switch(selection){
        case 0:
            if(player.GetMoney() >= 300000) {
                cm.sendOk("购买成功！ #r祝您祝您游戏愉快!#k");
                player.GainMoney(-300000); 
                var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                var type = ii.getInventoryType(1002513); //获得装备的类形
                var toDrop = ii.randomizeStats(ii.getEquipById(1002513)).copy(); // 生成一个Equip类
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
                cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null,  net.sf.odinms.tools.MaplePacketCreator.getItemMegas(cm.getC().getChannel(),"[贵族通告]" + " : " + "大老板『"+cm.getChar().getName()+"』成功购买全属性32767的渡鸦之魂短刀,购买请找NPC雪天使!",toDrop, true).getBytes());
                cm.dispose();
            }else{
                cm.sendOk("请确认你有30W元宝的时候在来购买!");
                cm.dispose();
            }
            break;
        case 10:
            if(player.GetMoney() >= 300000) {
                cm.sendOk("购买成功！ #r祝您祝您游戏愉快!#k");
                player.GainMoney(-300000); 
                var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                var type = ii.getInventoryType(1522018); //获得装备的类形
                var toDrop = ii.randomizeStats(ii.getEquipById(1522018)).copy(); // 生成一个Equip类
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
                cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null,  net.sf.odinms.tools.MaplePacketCreator.getItemMegas(cm.getC().getChannel(),"[贵族通告]" + " : " + "大老板『"+cm.getChar().getName()+"』成功购买全属性32767的龙翼双弩枪,购买请找NPC雪天使!",toDrop, true).getBytes());
                cm.dispose();
            }else{
                cm.sendOk("请确认你有30W元宝的时候在来购买!");
                cm.dispose();
            }
            break;
        case 14:
            if(player.GetMoney() >= 300000) {
                cm.sendOk("购买成功！ #r祝您祝您游戏愉快!#k");
                player.GainMoney(-300000); 
                var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                var type = ii.getInventoryType(1342033); //获得装备的类形
                var toDrop = ii.randomizeStats(ii.getEquipById(1342033)).copy(); // 生成一个Equip类
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
                cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null,  net.sf.odinms.tools.MaplePacketCreator.getItemMegas(cm.getC().getChannel(),"[贵族通告]" + " : " + "大老板『"+cm.getChar().getName()+"』成功购买全属性32767的龙翼双弩枪,购买请找NPC雪天使!",toDrop, true).getBytes());
                cm.dispose();
            }else{
                cm.sendOk("请确认你有30W元宝的时候在来购买!");
                cm.dispose();
            }
            break;
        case 11:
            if(player.GetMoney() >= 300000) {
                cm.sendOk("购买成功！ #r祝您祝您游戏愉快!#k");
                player.GainMoney(-300000); 
                var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                var type = ii.getInventoryType(1322096); //获得装备的类形
                var toDrop = ii.randomizeStats(ii.getEquipById(1322096)).copy(); // 生成一个Equip类
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
                cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null,  net.sf.odinms.tools.MaplePacketCreator.getItemMegas(cm.getC().getChannel(),"[贵族通告]" + " : " + "大老板『"+cm.getChar().getName()+"』成功购买全属性32767的狮心雷钉,购买请找NPC雪天使!",toDrop, true).getBytes());
                cm.dispose();
            }else{
                cm.sendOk("请确认你有30W元宝的时候在来购买!");
                cm.dispose();
            }
            break;
        case 12:
            if(player.GetMoney() >= 300000) {
                cm.sendOk("购买成功！ #r祝您祝您游戏愉快!#k");
                player.GainMoney(-300000); 
                var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                var type = ii.getInventoryType(1402095); //获得装备的类形
                var toDrop = ii.randomizeStats(ii.getEquipById(1402095)).copy(); // 生成一个Equip类
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
                cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null,  net.sf.odinms.tools.MaplePacketCreator.getItemMegas(cm.getC().getChannel(),"[贵族通告]" + " : " + "大老板『"+cm.getChar().getName()+"』成功购买全属性32767的狮心弯刀,购买请找NPC雪天使!",toDrop, true).getBytes());
                cm.dispose();
            }else{
                cm.sendOk("请确认你有30W元宝的时候在来购买!");
                cm.dispose();
            }
            break;
        case 13:
            if(player.GetMoney() >= 300000) {
                cm.sendOk("购买成功！ #r祝您祝您游戏愉快!#k");
                player.GainMoney(-300000); 
                var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                var type = ii.getInventoryType(1432086); //获得装备的类形
                var toDrop = ii.randomizeStats(ii.getEquipById(1432086)).copy(); // 生成一个Equip类
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
                cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null,  net.sf.odinms.tools.MaplePacketCreator.getItemMegas(cm.getC().getChannel(),"[贵族通告]" + " : " + "大老板『"+cm.getChar().getName()+"』成功购买全属性32767的狮心长枪,购买请找NPC雪天使!",toDrop, true).getBytes());
                cm.dispose();
            }else{
                cm.sendOk("请确认你有30W元宝的时候在来购买!");
                cm.dispose();
            }
            break;
        case 1:
            if(player.GetMoney() >= 300000){
                cm.sendOk("购买成功！ #r祝您游戏愉快!#k");
                player.GainMoney(-300000);
                var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                var type = ii.getInventoryType(1082149); //获得装备的类形
                var toDrop = ii.randomizeStats(ii.getEquipById(1082149)).copy(); // 生成一个Equip类
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
                cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null,  net.sf.odinms.tools.MaplePacketCreator.getItemMegas(cm.getC().getChannel(),"[贵族通告]" + " : " + "大老板『"+cm.getChar().getName()+"』成功购买全属性32767的渡鸦拳套,购买请找NPC雪天使!",toDrop, true).getBytes());
                cm.dispose();
            }else{
                cm.sendOk("请确认你有30W元宝的时候在来购买!");
                cm.dispose();
            }
            break;
        case 2:
            if(player.GetMoney() >= 300000){
                cm.sendOk("购买成功！ #r祝您游戏愉快!#k");
                player.GainMoney(-300000);
                var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                var type = ii.getInventoryType(1012239); //获得装备的类形
                var toDrop = ii.randomizeStats(ii.getEquipById(1012239)).copy(); // 生成一个Equip类
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
                cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null,  net.sf.odinms.tools.MaplePacketCreator.getItemMegas(cm.getC().getChannel(),"[贵族通告]" + " : " + "大老板『"+cm.getChar().getName()+"』成功购买全属性32767的龙尾长杖,购买请找NPC雪天使!",toDrop, true).getBytes());
                cm.dispose();
            }else{
                cm.sendOk("请确认你有30W元宝的时候在来购买!");
                cm.dispose();
            }
            break;
        case 3:
            if(player.GetMoney() >= 300000){
                cm.sendOk("购买成功！ #r祝您游戏愉快!#k");
                player.GainMoney(-300000);
                var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                var type = ii.getInventoryType(1032093); //获得装备的类形
                var toDrop = ii.randomizeStats(ii.getEquipById(1032093)).copy(); // 生成一个Equip类
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
                cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null,  net.sf.odinms.tools.MaplePacketCreator.getItemMegas(cm.getC().getChannel(),"[贵族通告]" + " : " + "大老板『"+cm.getChar().getName()+"』成功购买全属性32767的龙尾短杖,购买请找NPC雪天使!",toDrop, true).getBytes());
                cm.dispose();
            }else{
                cm.sendOk("请确认你有30W元宝的时候在来购买!");
                cm.dispose();
            }
            break;
        case 4:
            if(player.GetMoney() >= 300000){
                cm.sendOk("购买成功！ #r祝您游戏愉快!#k");
                player.GainMoney(-300000);
                var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                var type = ii.getInventoryType(1132085); //获得装备的类形
                var toDrop = ii.randomizeStats(ii.getEquipById(1132085)).copy(); // 生成一个Equip类
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
                cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null,  net.sf.odinms.tools.MaplePacketCreator.getItemMegas(cm.getC().getChannel(),"[贵族通告]" + " : " + "大老板『"+cm.getChar().getName()+"』成功购买全属性32767的爪子,购买请找NPC雪天使!",toDrop, true).getBytes());
                cm.dispose();
            }else{
                cm.sendOk("请确认你有30W元宝的时候在来购买!");
                cm.dispose();
            }
            break;
        case 5:
            if(player.GetMoney() >= 300000){
                cm.sendOk("购买成功！ #r祝您游戏愉快!#k");
                player.GainMoney(-300000);
                var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                var type = ii.getInventoryType(1122104); //获得装备的类形
                var toDrop = ii.randomizeStats(ii.getEquipById(1122104)).copy(); // 生成一个Equip类
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
                cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null,  net.sf.odinms.tools.MaplePacketCreator.getItemMegas(cm.getC().getChannel(),"[贵族通告]" + " : " + "大老板『"+cm.getChar().getName()+"』成功购买全属性32767的鲨齿手枪,购买请找NPC雪天使!",toDrop, true).getBytes());
                cm.dispose();
            }else{
                cm.sendOk("请确认你有30W元宝的时候在来购买!");
                cm.dispose();
            }
            break;
        case 6:
            if(player.GetMoney() >= 300000){
                cm.sendOk("购买成功！ #r祝您游戏愉快!#k");
                player.GainMoney(-30000);
                var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                var type = ii.getInventoryType(1102382); //获得装备的类形
                var toDrop = ii.randomizeStats(ii.getEquipById(1102382)).copy(); // 生成一个Equip类
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
                cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null,  net.sf.odinms.tools.MaplePacketCreator.getItemMegas(cm.getC().getChannel(),"[贵族通告]" + " : " + "大老板『"+cm.getChar().getName()+"』成功购买全属性32767的鲨鱼炮,购买请找NPC雪天使!",toDrop, true).getBytes());
                cm.dispose();
            }else{
                cm.sendOk("请确认你有30W元宝的时候在来购买!");
                cm.dispose();
            }
            break;
        case 7:
            if(player.GetMoney() >= 300000){
                cm.sendOk("购买成功！ #r祝您游戏愉快!#k");
                player.GainMoney(-300000);
                var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                var type = ii.getInventoryType(1072005); //获得装备的类形
                var toDrop = ii.randomizeStats(ii.getEquipById(1072005)).copy(); // 生成一个Equip类
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
                cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null,  net.sf.odinms.tools.MaplePacketCreator.getItemMegas(cm.getC().getChannel(),"[贵族通告]" + " : " + "大老板『"+cm.getChar().getName()+"』成功购买全属性32767的鹰翼弓,购买请找NPC雪天使!",toDrop, true).getBytes());
                cm.dispose();
            }else{
                cm.sendOk("请确认你有30W元宝的时候在来购买!");
                cm.dispose();

            };
    }
}
