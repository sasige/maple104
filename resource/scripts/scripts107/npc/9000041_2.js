var mmm="#fEffect/CharacterEff/1112905/0/1#";
function start() {

    cm.sendSimpleS("你当前游戏币余额：#r" + cm.getChar().getMeso() + "#k\r\n#L0##r#z5072000#x5　 #d需要 #r5#k #d亿#k#l\r\n#L1##r#z5072000#x12　#d需要 #r10#k #d亿#k#l\r\n#L2##r#z5072000#x30　#d需要 #r20#k #d亿#k#l\r\n#L3##r#z4031454#x1　　　　 #d需要 #r2000#k #d万#k#l\r\n#L4##r#z4031454#x5　　　　 #d需要 #r1#k #d亿#k#l",2)
}

function action(mode, type, selection) {
    cm.dispose();

    switch(selection){
        case 0:
            if(cm.getMeso() >= 500000000){//判定是否有前
                cm.sendOk("购买成功");//NPC提示购买成功
                cm.gainItem(5072000,5);//给玩家喇叭5个
                cm.gainMeso(-500000000);//扣取5E游戏币               
		cm.dispose();//解决NPC假死代码   
            }
            else{    
                cm.sendOk("游戏币不足");//没钱就提示游戏币不足
                cm.dispose();//解决NPC假死代码
            };  
            break;
        case 1:
            if(cm.getMeso() >= 1000000000){
                cm.sendOk("购买成功");
                cm.gainItem(5072000,12);
                cm.gainMeso(-1000000000);               
		cm.dispose();   
            }
            else{    
                cm.sendOk("游戏币不足");
                cm.dispose();
            };  
            break;
        case 2:
            if(cm.getMeso() >= 2000000000){
                cm.sendOk("购买成功");
                cm.gainItem(5072000,30);
                cm.gainMeso(-2000000000);               
		cm.dispose();   
            }
            else{    
                cm.sendOk("游戏币不足");
                cm.dispose();
            };  
            break;
        case 3:
            if(cm.getMeso() >= 20000000){
                cm.sendOk("购买成功");
                cm.gainItem(4031454,1);
                cm.gainMeso(-20000000);               
		cm.dispose();   
            }
            else{    
                cm.sendOk("请确认你有2000W游戏币");
                cm.dispose();
            };  
            break;
        case 4:
            if(cm.getMeso() >= 100000000){
                cm.sendOk("购买成功");
                cm.gainItem(4031454,5);
                cm.gainMeso(-100000000);               
		cm.dispose();   
            }
            else{    
                cm.sendOk("请确认你有1E游戏币");
                cm.dispose();
            };  
            break;
           case 5:
            if(cm.getMeso() >= 1000000000){
                cm.sendOk("购买成功");
                cm.gainItem(5074000,50);
                cm.gainMeso(-1000000000);               
		cm.dispose();   
            }
            else{    
                cm.sendOk("请确认你有10E游戏币");
                cm.dispose();
            };  
            break;

        case 44:
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
        case 55:
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
