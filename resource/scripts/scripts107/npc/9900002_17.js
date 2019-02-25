var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (status == 0 && mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
    if (status == 0) {
        var selStr = "请选择你要控制的转盘（闹闹好礼大赠送）:\r\n\r\n";
        selStr = "#b#L0#我要转300的(累计充值到300可以转)#l\r\n";
        selStr += "#L1#我要转30的(累计充值到30可以转)#l\r\n";
        selStr += "#L2#领取#z1112915#_4项属性+1000(累计充值到300可领取)#l\r\n";
        selStr += "#L3#领取#z1112915#_4项属性+2000(累计充值到600可领取)#l";
        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
        case 0:
            cm.dispose();
            cm.openNpc(9900002, 18);
            break;
        case 1:
            cm.dispose();
            cm.openNpc(9900002, 19);
            break;
        case 2:
            if (cm.delPayReward(0) > 0 && cm.getBossLog("300充值奖励", 1) == 0) {
                var ii = Packages.server.MapleItemInformationProvider.getInstance();
                var type = Packages.constants.GameConstants.getInventoryType(1112915); //圣诞鹿的鼻子
                var toDrop = ii.randomizeStats(ii.getEquipById(1112915)).copy(); // 生成一个Equip类
                toDrop.setStr(1000); //装备力量
                toDrop.setDex(1000); //装备敏捷
                toDrop.setInt(1000); //装备智力
                toDrop.setLuk(1000); //装备运气
                cm.getPlayer().getInventory(type).addItem(toDrop); //将这个装备放入包中
                cm.getC().getSession().write(Packages.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包
                cm.setBossLog("300充值奖励", 1);
                cm.sendOk("恭喜你领取成功!");
                //cm.getC().getChannelServer().broadcastPacket(Packages.tools.MaplePacketCreator.serverNotice(0x10, cm.getC().getChannel(), "『充值公告』" + " : " + "恭喜" + cm.getChar().getName() + ",领取了本服充值300超级大礼."));
                cm.getC().getChannelServer().broadcastPacket(Packages.tools.MaplePacketCreator.serverNotice(0x10, cm.getC().getChannel(), "『充值公告』" + " : " + "恭喜" + cm.getChar().getName() + ",领取了本服充值300超级大礼."));
                cm.getC().getChannelServer().broadcastPacket(Packages.tools.MaplePacketCreator.serverNotice(0x10, cm.getC().getChannel(), "『充值公告』" + " : " + "恭喜" + cm.getChar().getName() + ",领取了本服充值300超级大礼."));
		cm.getChar().saveToDB(false,false);
            } else {
                cm.sendOk("对不起,你累计充值没有达到300或者你已经领取过了!");
            }
            cm.dispose();
            break;
        case 3:
            if (cm.delPayReward(0) > 0 && cm.getBossLog("600充值奖励", 1) == 0) {
                var ii = Packages.server.MapleItemInformationProvider.getInstance();
                var type = Packages.constants.GameConstants.getInventoryType(1112915); //蓝调戒指
                var toDrop = ii.randomizeStats(ii.getEquipById(1112915)).copy(); // 生成一个Equip类
                toDrop.setStr(2000); //装备力量
                toDrop.setDex(2000); //装备敏捷
                toDrop.setInt(2000); //装备智力
                toDrop.setLuk(2000); //装备运气
                cm.getPlayer().getInventory(type).addItem(toDrop); //将这个装备放入包中
                cm.getC().getSession().write(Packages.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包
                cm.setBossLog("600充值奖励", 1);
                cm.sendOk("恭喜你领取成功!");
                cm.getC().getChannelServer().broadcastPacket(Packages.tools.MaplePacketCreator.serverNotice(0x10, cm.getC().getChannel(), "『充值公告』" + " : " + "恭喜" + cm.getChar().getName() + ",领取了本服充值600终极大礼."));
                cm.getC().getChannelServer().broadcastPacket(Packages.tools.MaplePacketCreator.serverNotice(0x10, cm.getC().getChannel(), "『充值公告』" + " : " + "恭喜" + cm.getChar().getName() + ",领取了本服充值600终极大礼."));
                cm.getC().getChannelServer().broadcastPacket(Packages.tools.MaplePacketCreator.serverNotice(0x10, cm.getC().getChannel(), "『充值公告』" + " : " + "恭喜" + cm.getChar().getName() + ",领取了本服充值600终极大礼."));
                cm.getC().getChannelServer().broadcastPacket(Packages.tools.MaplePacketCreator.serverNotice(0x10, cm.getC().getChannel(), "『充值公告』" + " : " + "恭喜" + cm.getChar().getName() + ",领取了本服充值600终极大礼."));
                cm.getC().getChannelServer().broadcastPacket(Packages.tools.MaplePacketCreator.serverNotice(0x10, cm.getC().getChannel(), "『充值公告』" + " : " + "恭喜" + cm.getChar().getName() + ",领取了本服充值600终极大礼."));
		cm.getChar().saveToDB(false,false);
            } else {
                cm.sendOk("对不起,你累计充值没有达到600或者你已经领取过了!");
            }
            cm.dispose();
            break;
        }
    }
}