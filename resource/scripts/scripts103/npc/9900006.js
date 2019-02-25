var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (status >= 0 && mode == 0) {
            cm.sendNext("大家不要开挂喔，和谐嘛!");
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            cm.sendYesNo("#r欢迎来到#b轩辕剑冒险岛#r我是美女新手接待员\r\n#k温柔的点击一下是\r\n#b即可获得精心为您准备的礼物喔#r送初级会员\r\n\r\n#v1003080##v1052246##v1102238##v1012058##v1142263##v1032089##v3010025##v3010073##v1902038#\r\n#k#r\r\n\r\n#v1012058# 全属性3000\r\n#v1142263# 全属性100 #b含攻击力 ");
        } else if (status == 1) {
            if (cm.getChar().getPresent() == 0) {
                cm.gainMeso(20000000);
                cm.getChar().modifyCSPoints(1,300000);
                cm.getChar().SetVip(1);
                cm.warp(910000000);
                if(cm.getChar().getNX()  <= 0) {
                    var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                    var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                    var type = ii.getInventoryType(1012058); //获得装备的类形
                    var toDrop = ii.randomizeStats(ii.getEquipById(1012058)).copy(); // 生成一个Equip类
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
                cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新	
                cm.getChar().saveToDB(true);
                cm.dispose();
                    var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                    var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                    var type = ii.getInventoryType(1142263); //获得装备的类形
                    var toDrop = ii.randomizeStats(ii.getEquipById(1012058)).copy(); // 生成一个Equip类
                    toDrop.AddFlag(net.sf.odinms.server.constants.InventoryConstants.Items.Flags.UNTRADEABLE);
                    toDrop.setStr(100);
                    toDrop.setDex(100);
                    toDrop.setInt(100);
                    toDrop.setLuk(100);
                    toDrop.setHp(100);
                    toDrop.setMp(100);
                    toDrop.setMatk(100);
                    toDrop.setWatk(100);
                    toDrop.setMdef(100);
                    toDrop.setWdef(100);
                    toDrop.setAcc(100);
                    toDrop.setAvoid(100);
                    toDrop.setHands(100);
                    toDrop.setSpeed(100);
                    toDrop.setJump(100);
                cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
                cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新	
                cm.getChar().saveToDB(true);
                cm.dispose();
                }else{
                    cm.sendOk(".............!");
                    cm.dispose();
                }
                cm.getChar().setPresent(1);
                cm.getChar().saveToDB(true);
                cm.gainItem(1032089, 1);
                cm.gainItem(3010025, 1);
                cm.gainItem(3010073, 1);
		cm.teachSkill(80001006,1,1);
		cm.teachSkill(80001021,1,1);
                cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『新手驾到』"+" : "+"恭喜"+ cm.getChar().getName() +",加入了"+cm.GetSN()+"欢迎热烈欢迎~!"))
                cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null, net.sf.odinms.tools.MaplePacketCreator.serverNotice(17,cm.getC().getChannel(),"[vip公告]" + " : 恭喜玩家：" + cm.getPlayer().getName() +" 加入了本服初级会员行列，欢呼吧！！！",true).getBytes());
cm.sendOk("恭喜您，成功加入[初级会员]，祝您游戏愉快！");
                cm.sendOk("恭喜您.领取完毕！展开您的冒险之旅吧！");
                cm.dispose();
            } else {
                cm.warp(910000000);
                cm.sendOk("每个帐号只可以领取#b1次#k。你已经领取过了!");
                cm.dispose();
            }	
        }
    }
}
