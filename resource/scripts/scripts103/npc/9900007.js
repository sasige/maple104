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
                       cm.sendYesNo("#r欢迎来到#b轩辕剑冒险岛#r我是美女新手接待员\r\n#k温柔的点击一下是\r\n#b即可获得精心为您准备的礼物喔#r送初级会员\r\n\r\n#v1003080##v1052246##v1102238##v1012058##v1142263##v1032089##v3010025##v3010073##v1902038#\r\n#k#r\r\n\r\n#v1002930# 全属性100\r\n#v1142263# 全属性100 #b含攻击力 ");
        } else if (status == 1) {
            if (cm.getChar().getPresent() == 0) {
                cm.gainMeso(20000000);
                cm.getChar().modifyCSPoints(1,2000);
                cm.getChar().SetVip(1);
                cm.warp(910000000);
                if(cm.getChar().getNX()  <= 0) {
                    var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                	                
                var type = ii.getInventoryType(1002930); //获得装备的类形
                var toDrop = ii.randomizeStats(ii.getEquipById(1002930)).copy(); // 生成一个Equip类
                toDrop.setLocked(1);
                toDrop.setStr(100);
                toDrop.setDex(100);
                toDrop.setInt(100);
                toDrop.setLuk(100);
                toDrop.setHp(100);
                toDrop.setMp(100);
                toDrop.setMatk(3);
                toDrop.setWatk(3);
                toDrop.setMdef(3);
                toDrop.setWdef(3);
                toDrop.setAcc(3);
                toDrop.setAvoid(3);
                toDrop.setHands(3);
                toDrop.setSpeed(3);
                toDrop.setJump(3);
                cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
                cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背
                cm.getChar().saveToDB(true);
                cm.dispose();
                }else{
                    cm.sendOk("只能领取一次噢");
                    cm.dispose();             
	        }
                cm.getChar().setPresent(1);
                cm.getChar().saveToDB(true);
                var type = ii.getInventoryType(1142263); //获得装备的类形
                var toDrop = ii.randomizeStats(ii.getEquipById(1142263)).copy(); // 生成一个Equip类
                toDrop.setLocked(1);
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
                cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背
                cm.getChar().saveToDB(true);
                cm.dispose();
                cm.gainItem(1032089, 1);
                cm.gainItem(3010025, 1);
                cm.gainItem(3010073, 1);
                cm.gainItem(1052246, 1);
                cm.gainItem(1003080, 1);
                cm.gainItem(1102238, 1);
		cm.teachSkill(80001006,1,1);
		cm.teachSkill(80001021,1,1);
                cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『新手驾到』"+" : "+"恭喜"+ cm.getChar().getName() +",慧眼识英雄,选择了"+cm.GetSN()+"热烈欢迎,鼓掌.~!.~!"))
                cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null, net.sf.odinms.tools.MaplePacketCreator.serverNotice(17,cm.getC().getChannel(),"[vip公告]" + " : 恭喜玩家：" + cm.getPlayer().getName() +" 加入了本服初级会员行列，欢呼吧！！！",true).getBytes());
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
