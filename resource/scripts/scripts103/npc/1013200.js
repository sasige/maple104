function start() {
    status = -1;
	
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    }
    else {
        if (status >= 0 && mode == 0) {
                
            cm.sendOk("#r别看我只是一只猪#k,#b没有票子我是不会给你强化道具的哈哈..");
            cm.dispose();
            return;                    
        }
        if (mode == 1) {
            status++;
        }
        else {
            status--;
        }
        if (status == 0) {
            cm.sendSimple("您好我是最新开发的装备强化道具小天使 ~!\r\n#r               【换换换】\r\n#L0#[使用40个#v4310027# 换 #v5062000# 神奇魔方1个]\r\n#L1#[使用40个#v4310027# 换 #v5062001# 混沌魔方1个]\r\n#L4#[使用40个#v4310027# 换  #v5062002#  高级神奇魔方1个]\r\n#L2#[使用40个#v4310027# 换  #v2049400#   潜能附加劵1个]\r\n#L3#[使用40个#v4310027# 换  #v2049300#   装备强化卷1个]");
        } else if (status == 1) {
            if (selection == 0) {
                if(cm.haveItem(4310027,40) == true) {
                    cm.gainItem(4310027,-40); 
                    cm.gainItem(5062000,1); //神奇魔方
                    cm.sendOk("恭喜您成功换取#v5062000#  神奇魔方1个。");
cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『"+ cm.getChar().getName() +"』Vip⑥"+" : "+"使用还我漂漂拳,在可怜的小猪那里抢夺了[神奇魔方]"))
                    cm.dispose();
                } else {
                    cm.sendOk("你背包里没有足够的#v4310027#，不能换取哟！"); 
                    cm.dispose();

                }
            } else if (selection == 1) {
                if(cm.haveItem(4310027,40) == true) {
                    cm.gainItem(4310027,-40); 
                    cm.gainItem(5062001,1);//混沌魔方
                    cm.sendOk("恭喜您成功换取#v5062001#  混沌魔方1个。");
cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『"+ cm.getChar().getName() +"』Vip⑥"+" : "+"使用还我漂漂拳,在可怜的小猪那里抢夺了[混沌魔方]"))
                    cm.dispose();
                } else {
                    cm.sendOk("你背包里没有足够的#v4310027#，不能换取哟！"); 
                    cm.dispose(); 
                }
            } else if (selection == 2) {
                if(cm.haveItem(4310027,40) == true) {
                    cm.gainItem(4310027,-40); 
                    cm.gainItem(2049400,1);//[潜能附加劵]
                    cm.sendOk("恭喜您成功换取#v2049400#  [潜能附加劵]1个。");
cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『"+ cm.getChar().getName() +"』Vip⑥"+" : "+"使用还我漂漂拳,在可怜的小猪那里抢夺了[潜能附加劵]"))
                    cm.dispose();
                } else {
                    cm.sendOk("你背包里没有足够的#v4310027#，不能换取哟！"); 
                    cm.dispose();
                
                }
            } else if (selection == 3) {
                if(cm.haveItem(4310027,40) == true) {
                    cm.gainItem(4310027,-40); 
                    cm.gainItem(2049300,1);//[装备强化卷]
                    cm.sendOk("恭喜您成功换取#v2049300#  [装备强化卷]1个。");
cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『"+ cm.getChar().getName() +"』Vip⑥"+" : "+"使用还我漂漂拳,在可怜的小猪那里抢夺了[装备强化卷]"))
                    cm.dispose();
                } else {
                    cm.sendOk("你背包里没有足够的#v4310027#，不能换取哟！"); 
                    cm.dispose();
                }
            } else if (selection == 4) {
                if(cm.haveItem(4310027,40) == true) {
                    cm.gainItem(4310027,-40); 
                    cm.gainItem(5062002,1);//[装备强化卷]
                    cm.sendOk("恭喜您成功换取#v5062002#  [高级神奇的魔方]1个。");
cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『"+ cm.getChar().getName() +"』Vip⑥"+" : "+"使用还我漂漂拳,在可怜的小猪那里抢夺了[高级神奇的魔方]"))
                    cm.dispose();
                } else {
                    cm.sendOk("你背包里没有足够的#v4310027#，不能换取哟！"); 
                    cm.dispose();
                }
            }
        }
    }
}
