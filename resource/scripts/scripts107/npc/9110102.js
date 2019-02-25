function start() {
    cm.sendSimple ("hi~想买超炫酷的新情景喇叭吗?找我就对了~买的越多越优惠哦~我的ID:"+cm.getNpc()+"\r\n#L1#我要用8000点券购买#k#r10个#v5390004# #b#k\r\n#e#L3##r我要用70000点券购买#k #r100个#v5390004# #b#k\r\n#e#L11#我要用8000点券购买#k #r10个#v5390005# #b#k\r\n#e#L12##r我要用70000点券购买#k #r100个#v5390005# #b#k\r\n#e#L14#我要用8000点券购买#k #r10个#v5390006# #b#k\r\n#e#L15##r我要用75000点券购买#k #r100个#v5390006# #b#k\r\n")
    }

function action(mode, type, selection) {
        cm.dispose();

    switch(selection){
        case 0: 
            if(cm.getChar().getNX() >= 1000){
            cm.sendOk("人民币喇叭已经放在你包里了哦！ #r游戏愉快!#k");
            cm.gainNX(-50000);
            cm.gainItem(5390004, 1);
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了1个人民币新年喇叭，购买请看雪精灵！");
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了1个人民币新年喇叭，购买请看雪精灵！");
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了1个人民币新年喇叭，购买请看雪精灵！");
            cm.dispose();
            }else{
            cm.sendOk("请确认你有1000点券的时候在来兑换喇叭!");
            cm.dispose();
            }
        break;
        case 1: 
            if(cm.getChar().getNX() >= 8000){
            cm.sendOk("人民币喇叭已经放在你包里了哦！ #r游戏愉快!#k");
            cm.gainNX(-80000);
            cm.gainItem(5390004, 10);
            cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null, net.sf.odinms.tools.MaplePacketCreator.serverNotice(3,cm.getC().getChannel(),"[系统公告]" + " : 大老板：" + cm.getPlayer().getName() +" 购买了10个人民币新年喇叭，购买请看雪精灵！",true).getBytes());
            cm.dispose();        
            }
            else{    
            cm.sendOk("请确认你有8000点券的时候在来兑换喇叭!");
                cm.dispose();
            };
        break;
        case 3: 
            if(cm.getChar().getNX() >= 70000){
            cm.sendOk("人民币喇叭已经放在你包里了哦！ #r游戏愉快!#k");
            cm.gainNX(-70000);
            cm.gainItem(5390004, 100);
            cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null, net.sf.odinms.tools.MaplePacketCreator.serverNotice(3,cm.getC().getChannel(),"[系统公告]" + " : 大老板：" + cm.getPlayer().getName() +" 购买了100个人民币新年喇叭，购买请看雪精灵！",true).getBytes());
            cm.dispose();        
            }
            else{    
            cm.sendOk("请确认你有70000点券的时候在来兑换喇叭!");
                cm.dispose();
            };
        break;
        case 10: 
            if(cm.getChar().getNX() >= 1000){
            cm.sendOk("人民币喇叭已经放在你包里了哦！ #r游戏愉快!#k");
            cm.gainNX(-1000);
            cm.gainItem(5390005, 1);
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了1个小老虎情景喇叭，购买请看雪精灵！");
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了1个小老虎情景喇叭，购买请看雪精灵！");
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了1个小老虎情景喇叭，购买请看雪精灵！");
            cm.dispose();
            }else{
            cm.sendOk("请确认你有1000点券的时候在来兑换喇叭!");
            cm.dispose();
            }
        break;
        case 11: 
            if(cm.getChar().getNX() >= 8000){
            cm.sendOk("人民币喇叭已经放在你包里了哦！ #r游戏愉快!#k");
            cm.gainNX(-8000);
            cm.gainItem(5390005, 10);
            cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null, net.sf.odinms.tools.MaplePacketCreator.serverNotice(3,cm.getC().getChannel(),"[系统公告]" + " : 大老板：" + cm.getPlayer().getName() +" 购买了10个小老虎情景喇叭，购买请看雪精灵！",true).getBytes());
            cm.dispose();        
            }
            else{    
            cm.sendOk("请确认你有8000点券的时候在来兑换喇叭!");
                cm.dispose();
            };
        break;
        case 12: 
            if(cm.getChar().getNX() >= 70000){
            cm.sendOk("人民币喇叭已经放在你包里了哦！ #r游戏愉快!#k");
            cm.gainNX(-70000);
            cm.gainItem(5390005, 100);
            cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null, net.sf.odinms.tools.MaplePacketCreator.serverNotice(3,cm.getC().getChannel(),"[系统公告]" + " : 大老板：" + cm.getPlayer().getName() +" 购买了100个小老虎情景喇叭，购买请看雪精灵！",true).getBytes());
            cm.dispose();        
            }
            else{    
            cm.sendOk("请确认你有70000点券的时候在来兑换喇叭!");
                cm.dispose();
            };
        break;
        case 13: 
            if(cm.getChar().getNX() >= 1000){
            cm.sendOk("人民币喇叭已经放在你包里了哦！ #r游戏愉快!#k");
            cm.gainNX(-1000);
            cm.gainItem(5390006, 1);
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了1个咆哮老虎情景喇叭，购买请看雪精灵！");
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了1个咆哮老虎情景喇叭，购买请看雪精灵！");
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了1个咆哮老虎情景喇叭，购买请看雪精灵！");
            cm.dispose();
            }else{
            cm.sendOk("请确认你有1000点券的时候在来兑换喇叭!");
            cm.dispose();
            }
        break;
        case 14: 
            if(cm.getChar().getNX() >= 8000){
            cm.sendOk("人民币喇叭已经放在你包里了哦！ #r游戏愉快!#k");
            cm.gainNX(-8000);
            cm.gainItem(5390006, 10);
            cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null, net.sf.odinms.tools.MaplePacketCreator.serverNotice(3,cm.getC().getChannel(),"[系统公告]" + " : 大老板：" + cm.getPlayer().getName() +" 购买了10个咆哮老虎情景喇叭，购买请看雪精灵！",true).getBytes());
            cm.dispose();        
            }
            else{    
            cm.sendOk("请确认你有8000点券的时候在来兑换喇叭!");
                cm.dispose();
            };
        break;
        case 15: 
            if(cm.getChar().getNX() >= 75000){
            cm.sendOk("人民币喇叭已经放在你包里了哦！ #r游戏愉快!#k");
            cm.gainNX(-75000);
            cm.gainItem(5390006, 100);
            cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null, net.sf.odinms.tools.MaplePacketCreator.serverNotice(3,cm.getC().getChannel(),"[系统公告]" + " : 大老板：" + cm.getPlayer().getName() +" 购买了100个咆哮老虎情景喇叭，购买请看雪精灵！",true).getBytes());
            cm.dispose();        
            }
            else{    
            cm.sendOk("请确认你有75000点券的时候在来兑换喇叭!");
                cm.dispose();
            };
        break;
        case 16: 
            if(cm.getzb() >= 10000){
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了1000点券，购买请看雪精灵！");
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了1000点券，购买请看雪精灵！");
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了1000点券，购买请看雪精灵！");
            cm.sendOk("点券购买成功了哦！ #r游戏愉快!#k");
            cm.setzb(-10000);
            cm.gainNX(1000);
            cm.modifyNX(1000, 0);//显示得点
            
            cm.dispose();
            }else{
            cm.sendOk("请确认你有10000元宝的时候在来购买!");
            cm.dispose();
            }
        break;
        case 17: 
            if(cm.getzb() >= 50000){
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了6000点券，购买请看雪精灵！");
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了6000点券，购买请看雪精灵！");
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了6000点券，购买请看雪精灵！");
            cm.sendOk("点券购买成功了哦！ #r游戏愉快!#k");
            cm.setzb(-50000);
            cm.gainNX(6000);
            cm.modifyNX(6000, 0);//显示得点
            
            cm.dispose();
            }else{
            cm.sendOk("请确认你有50000元宝的时候在来兑换喇叭!");
            cm.dispose();
            }
        break;
        case 20:
            if(cm.haveItem(4002000, 1)) {
            cm.sendOk("您的圣杯已被收回!为了回报你，我给你5E冒险币")
            cm.gainItem(4002000, -1);
            cm.gainMeso(500000000);
            cm.dispose();
            } else {
                cm.sendOk("#e您需要 #b1#k 个 #v4002000#\r\n请检查您的背包中是否有再来领取。")
                cm.dispose();    
            };    
        break;
        case 2:
            if(cm.haveItem(4001126, 100)) {
            cm.sendOk("您的#v4001126#已被收回!为了回报你，我给你50元宝!")
            cm.gainItem(4001126, -100);
            cm.setzb(50); 
            cm.dispose();
            } else {
                cm.sendOk("#e您需要 #b100#k 个 #v4001126#\r\n请检查您的背包中是否有100个再来领取。")
                cm.dispose();    
            };    
        break;
        case 4:
            if(cm.haveItem(4031250, 100)) {
            cm.sendOk("您的#v4031250#已被收回!为了回报你，我给你1000元宝!")
            cm.gainItem(4031250, -100);
            cm.setzb(1000); 
            cm.dispose();
            } else {
                cm.sendOk("#e您需要 #b100#k 个 #v4031250#\r\n请检查您的背包中是否有100个再来领取。")
                cm.dispose();    
            };    
        break;
        case 5:
            if(cm.haveItem(4031454, 1)) {
            cm.sendOk("您的圣杯已被收回!为了回报你，我给你5E游戏币")
            cm.gainItem(4031454, -1);
            cm.gainMeso(500000000);
            cm.dispose();
            } else {
                cm.sendOk("#e您需要 #b1#k 个 #v4031454#\r\n请检查您的背包中是否有再来领取。")
                cm.dispose();    
            };    
        break;
        case 6:
            if(cm.getMeso() >= 500000000){
            cm.sendOk("您的游戏币已被收回!给你一个圣杯!")
            cm.gainItem(4031454, 1);
            cm.gainMeso(-500000000);
            cm.dispose();
            } else {
                cm.sendOk("#e你没有 #b1#k #v5E游戏币#")
                cm.dispose();    
            };
        break;
        case 7:
            if(cm.haveItem(4001126, 5)) {
            cm.sendOk("您的点卡已被收回!为了回报你，我给你1250点NX!")
            cm.gainItem(4001126, -5);
            cm.gainNX(1250);
            cm.modifyNX(1250, 0);//显示得点
            cm.dispose();
            } else {
                cm.sendOk("#e您需要 #b5#k 个 #v4001126#\r\n请检查您的背包中是否有5个再来领取。")
                cm.dispose();    
            };
        break
        case 8:
            if(cm.haveItem(4001126, 10)) {
            cm.sendOk("您的点卡已被收回!为了回报你，我给你2500点NX!")
            cm.gainItem(4001126, -10);
            cm.gainNX(2500);
            cm.modifyNX(2500, 0);//显示得点
            cm.dispose();
            } else {
                cm.sendOk("#e您需要 #b10#k 个 #v4001126#\r\n请检查您的背包中是否有10个再来领取。")
                cm.dispose();    
            };
        break
        case 9:
            if(cm.haveItem(4001126, 25)) {
            cm.sendOk("您的点卡已被收回!为了回报你，我给你6250点NX!")
            cm.gainItem(4001126, -25);
            cm.gainNX(6250);
            cm.modifyNX(6250, 0);//显示得点
            cm.dispose();
            } else {
                cm.sendOk("#e您需要 #b25#k 个 #v4001126#\r\n请检查您的背包中是否有25个再来领取。")
                cm.dispose();    
            };
        }
    }
