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
            cm.sendYesNo("#r         ┈═☆欢迎来到"+cm.GetSN()+"☆═┈#l\r\n\r\n#r┈═☆游戏版本：冒险岛Ver.102 -《冒险传说之精灵觉醒》\r\n#r┈═☆游戏帮助：聊天框内 @帮助 即可查看玩家命令#l\r\n#r┈═☆游戏特色：长久稳定，随官方同步更新\r\n#r┈═☆游戏点券：1元即可兑换2000点券，活动时期更高\r\n#r┈═☆新手礼包：10W抵用＋3000全属性枫叶＋会员①#l\r\n\r\n#b~~~~~~~~~~~~~~~#v1032089#```#v1142263#````#v3010025#~~~~~~~~~~~~~~~#l\r\n #e  ━═☆请点击按钮(是) 祝您游戏愉快☆═━#l#k#n");
        } else if (status == 1) {
            if (cm.getChar().getPresent() == 0) {
                cm.gainMeso(20000000);
                cm.getChar().modifyCSPoints(1,33333);
                cm.getChar().SetVip(1);
                cm.warp(910000000);
                if(cm.getChar().getNX()  <= 0) {
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
                    cm.sendOk(".............!");
                    cm.dispose();
                }
                cm.getChar().setPresent(1);
                cm.getChar().saveToDB(true);
                cm.gainItem(1142263, 1);
                cm.gainItem(1032089, 1);
                cm.gainItem(3010025, 1);
                cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『新手驾到』"+" : "+"恭喜"+ cm.getChar().getName() +",慧眼识英雄,选择了"+cm.GetSN()+"热烈欢迎,鼓掌.~!.~!"))
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
