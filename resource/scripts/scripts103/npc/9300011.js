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
                
            cm.sendOk("#r吼吼,我是财神爷,财神大门朝南开,有钱没钱,别进来 吼吼吼\r\n\r\n#v5440000##v5440000##v5440000##v5440000##v5440000##v5440000##v5440000##v5440000##v5440000##v5440000#");
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
            cm.sendSimple("您好我是财神爷,可以帮您换取最犀利的属性装备噢\r\n#r                  【换换换】\r\n\r\n\r\n#L3#[使用10个#v4002000# 换 2张#v5064000# 超级防爆卷]#l\r\n\r\n#L0#[使用10个#v4002000# 换 #v5010083# 神秘的特效]#l\r\n\r\n#L1#[使用10个#v4002000# 换 #v1102353# 自由的翅膀]#l\r\n\r\n#L2#[使用10个#v4002000# 换 #v1702334# 水晶幻想曲]#l\r\n\r\n\r\n#r等待开发.....");
        } else if (status == 1) {
            if (selection == 0) {
                if(cm.haveItem(4002000,10) == true) {
                    cm.gainItem(4002000,-10); 
                    cm.gainItem(5010083,1); //
                    cm.sendOk("恭喜您成功换取#v5010083# 。");
cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『"+ cm.getChar().getName() +"』"+" : "+"在财神爷那里换取神秘道具"))
                    cm.dispose();
                } else {
                    cm.sendOk("你背包里没有足够的#v4002000#，不能换取哟！"); 
                    cm.dispose();

                }
            } else if (selection == 1) {
                if(cm.haveItem(4002000,10) == true) {
                    cm.gainItem(4002000,-10); 
                    cm.gainItem(1102353,1);//
                    cm.sendOk("恭喜您成功换取#v1102353#。");
cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『"+ cm.getChar().getName() +"』"+" : "+"在财神爷那里换取神秘道具(自由的翅膀)"))
                    cm.dispose();
                } else {
                    cm.sendOk("你背包里没有足够的#v4002000#，不能换取哟！"); 
                    cm.dispose(); 

                }
            } else if (selection == 2) {
                if(cm.haveItem(4002000,10) == true) {
                    cm.gainItem(4002000,-10); 
                    cm.gainItem(1702334,1);//混沌魔方
                    cm.sendOk("恭喜您成功换取#v1702334#。");
cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『"+ cm.getChar().getName() +"』"+" : "+"在财神爷那里换取神秘道具(水晶幻想曲)"))
                    cm.dispose();
                } else {
                    cm.sendOk("你背包里没有足够的#v4002000#，不能换取哟！"); 
                    cm.dispose(); 

                }

            } else if (selection == 3) {
                if(cm.haveItem(4002000,10) == true) {
                    cm.gainItem(4002000,-10); 
                    cm.gainItem(5064000,2);//防爆
                    cm.sendOk("恭喜您成功换取2个#v5064000#。");
cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『"+ cm.getChar().getName() +"』"+" : "+"在财神爷那里换取神秘道具(超级防爆卷)"))
                    cm.dispose();
                } else {
                    cm.sendOk("你背包里没有足够的#v4002000#，不能换取哟！"); 
                    cm.dispose(); }

            } else if (selection == 4) {
                if(cm.haveItem(4002000,50) == true) {
                    cm.gainItem(4002000,-50); 
                    cm.gainItem(5062002,4);//防爆
                    cm.sendOk("恭喜您成功换取4个#v5062002#。");
		    cm.dispose();
                } else {
                    cm.sendOk("你背包里没有足够的#v4002000#，不能换取哟！"); 
                    cm.dispose(); 

                }
            }
        }
    }
}
