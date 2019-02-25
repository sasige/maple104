var mmm="#fEffect/CharacterEff/1112905/0/1#";
function start() {

    cm.sendSimple ("欢迎来到#r"+cm.GetSN()+"#k,( ^_^ )\r\n#r低价换点卷,童叟无欺！\r\n\r\n \r\n\r\n#r#L88##fEffect/CharacterEff/1112905/0/1#我有10000 元宝 #r换100000 点卷#k\r\n#r#L89##fEffect/CharacterEff/1112905/0/1#我有100000元宝 #r换1000000 点卷#k\r\n\r\n\r\n")
}

function action(mode, type, selection) {
    cm.dispose();

    switch(selection){
        case 0:
            if (cm.getPlayer().getNX() >= 10000 ) {
                cm.sendOk("谢谢! 10亿金币已添加到您的帐户! 享受吧! #r赶快去挥霍金币吧!#k");
                cm.gainNX(-10000);
                cm.gainMeso(500000000);
                cm.modifyMeso(500000000, 0);//显示得点
                cm.dispose();
            }else{
                cm.sendOk("请确认你有10000点卷的时候在来兑换金币!");
                cm.dispose();
            }
            break;
        case 1:
            if(cm.getMeso() >= 10000000) {
                cm.sendOk("谢谢! 500 点卷已添加到您的帐户! 享受吧! #r赶快点拍卖购买你合适的【点装】吧!#k");
                cm.gainMeso(-10000000);                
		cm.gainNX(500);                
		cm.dispose();   
            }
            else{    
                cm.sendOk("请确认你有一千万的时候在来兑换元宝!");
                cm.dispose();
            };
            break;
        case 2:
            if(cm.getMeso() >= 20000000) {
                cm.sendOk("谢谢! 1000 点卷已添加到您的帐户! 享受吧! #r赶快点拍卖购买你合适的【点装】吧!#k");
                cm.gainMeso(-20000000);                
		cm.gainNX(1000);                
		cm.dispose();   
            }
            else{    
                cm.sendOk("请确认你有二千万的时候在来兑换元宝!");
                cm.dispose();
            };  
            break;
        case 3:
            if(cm.getMeso() >= 200000000) {
                cm.sendOk("谢谢! 10000 点卷已添加到您的帐户! 享受吧! #r赶快点拍卖购买你合适的【点装】吧!#k");
                cm.gainMeso(-200000000);                
		cm.gainNX(10000);                
		cm.dispose();   
            }
            else{    
                cm.sendOk("请确认你有一千万的时候在来兑换元宝!");
                cm.dispose();
            };   
            break;
           case 88:
            if(player.GetMoney() >= 10000){
                cm.sendOk("谢谢! 100000 点卷已添加到您的帐户! 享受吧! #r赶快点拍卖购买你合适的【点装】吧!#k");
                player.GainMoney(-10000);              
		cm.gainNX(100000);                
		cm.dispose();   
            }
            else{    
                cm.sendOk("请确认你有100元宝的时候在在来兑换");
                cm.dispose();
            };   
            break;
           case 89:
            if(player.GetMoney() >= 100000){
                cm.sendOk("谢谢! 1000000 点卷已添加到您的帐户! 享受吧! #r赶快点拍卖购买你合适的【点装】吧!#k");
                player.GainMoney(-100000);              
		cm.gainNX(1000000);                
		cm.dispose();   
            }
            else{    
                cm.sendOk("请确认你有10元宝的时候在在来兑换");
                cm.dispose();
            };   
            break;

        case 4:
            if(cm.haveItem(4002000, 10)) {
                cm.sendOk("您的1个#v4002000#已被收回!为了回报你，我给你1000点卷!")
                cm.gainItem(4002000, -10);
                cm.gainNX(500);
                cm.modifyNX(500, 0);//显示得点
                cm.dispose();
            } else {
                cm.sendOk("#e您需要 #b400#k 个 #v4001126#\r\n请检查您的背包中是否有400个再来领取。")
                cm.dispose();    
            };    
            break;
        case 5:
            if(cm.haveItem(4001126, 600)) {
                cm.sendOk("您的600个#v4001126#已被收回!为了回报你，我给你1500点卷!")
                cm.gainItem(4001126, -600);
                cm.gainNX(1500);
                cm.modifyNX(1500, 0);
                cm.dispose();
            } else {
                cm.sendOk("#e您需要 #b600#k 个 #v4001126#\r\n请检查您的背包中是否有600个再来领取。")
                cm.dispose();    
            };
            break;
        case 6:
            if(cm.haveItem(4001126, 800)) {
                cm.sendOk("您的800个#v4001126#已被收回!为了回报你，我给你一个圣杯!")
                cm.gainItem(4001126, -800);
                cm.gainItem(4031454,1);
                cm.modifyItem(4031454, 1);
                cm.dispose();
            } else {
                cm.sendOk("#e您需要 #b800#k 个 #v4001126#\r\n请检查您的背包中是否有800个再来领取。")
                cm.dispose();
            };
            break;
        case 7:
            if(cm.haveItem(4001129, 1)) {
                cm.sendOk("您的1个#v4001129#已被收回!为了回报你，我给你1个#v4031454#!")
                cm.gainItem(4001129, -1);
                cm.gainItem(4031454,1);
                cm.modifyItem(4031454, 1);
                cm.dispose();
            } else {
                cm.sendOk("#e您需要 #b1#k 个 #v4001129#\r\n请检查您的背包中是否有1个再来领取。")
                cm.dispose();
            };
            break;
        case 8:
            if(cm.haveItem(4000171, 0)) {
                cm.sendOk("您的500个#v4000171#已被收回!为了回报你，我给你2个金币包!")
                cm.gainItem(4000171, -0);
                var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                var type = ii.getInventoryType(1002140); //获得装备的类形
                //var temptime = new java.sql.Timestamp(java.lang.System.currentTimeMillis() + 10 * 24 * 60 * 60 * 100); //时间
                var toDrop = ii.randomizeStats(ii.getEquipById(1002140)).copy(); // 生成一个Equip类
                toDrop.setLocked(1);
                var temptime = new java.sql.Timestamp(java.lang.System.currentTimeMillis() + 10 * 24 * 60 * 60 * 0); //时间
                toDrop.setExpiration(temptime); //给装备时间
                cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
                cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包
                cm.dispose();
            } else {
                cm.sendOk("#e您需要 #b500#k 个 #v4000171#\r\n请检查您的背包中是否有500个再来换取。")
                cm.dispose();
            };  
    }
}
