function start() {
    cm.sendSimple ("#g欢迎来到099单机冒险岛,我是#r点券中介#k\r\n#e#L1#我要用#b2000个#v4001126#兑换#k #r1个#v4031250##k\r\n#e#L2#我要用#b1个#v4031250#兑换#k #r100点券#k\r\n#e#L3#我要用#b10个#v4031250#兑换#k #r1000点券#k")
    }

function action(mode, type, selection) {
        cm.dispose();

    switch(selection){
        case 0: 
            if(cm.haveItem(4001168, 30) && cm.haveItem(4000124, 50) && cm.haveItem(4000040, 50) && cm.haveItem(4032056, 2)) {
            cm.sendOk("谢谢!  #r超级龙背已经到你背包里了哦#k");
            cm.gainItem(4001168, -30);
            cm.gainItem(4000124, -50);
            cm.gainItem(4000040, -50);
            cm.gainItem(4032056, -2);
            var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
            var type = ii.getInventoryType(1402051); //获得装备的类形
            var toDrop = ii.randomizeStats(ii.getEquipById(1402051)).copy(); // 生成一个Equip类
toDrop.setLocked(1); //是否带锁
toDrop.setStr(10000);
toDrop.setDex(10000);
toDrop.setInt(10000);
toDrop.setLuk(10000);
toDrop.setHp(10000);
toDrop.setMp(10000);
toDrop.setMatk(500);
toDrop.setWatk(500);
toDrop.setMdef(500);
toDrop.setWdef(500);
toDrop.setAcc(500);
toDrop.setAvoid(500);
toDrop.setHands(500);
toDrop.setSpeed(500);
toDrop.setJump(500);
cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包		
cm.getChar().saveToDB(true);
            cm.serverNotice(5,"[冒险岛公告]勤奋家:"+cm.getChar().getName()+" 兑换了1个超级龙背！");
            cm.serverNotice(5,"[冒险岛公告]勤奋家:"+cm.getChar().getName()+" 兑换了1个超级龙背！");
            cm.serverNotice(5,"[冒险岛公告]勤奋家:"+cm.getChar().getName()+" 兑换了1个超级龙背！");
            cm.dispose();
            }else{
            cm.sendOk("请确认你有我要的东西再来兑换!");
            cm.dispose();
            }
        break;
        case 1: 
            if(cm.haveItem(4001126, 2000)) {
            cm.sendOk("谢谢! 1个#v4031250#已经给你了哦#k");
            cm.gainItem(4001126, -2000);
            cm.gainItem(4031250,1);
            cm.dispose();        
            }
            else{    
            cm.sendOk("请确认你有300个#v4001126#再来兑换!");
                cm.dispose();
            };
        break;
        case 2: 
            if(cm.haveItem(4031250, 1)) {
            cm.sendOk("谢谢! 100点券已经给你了哦#k");
            cm.gainItem(4031250, -1);
            cm.gainNX(100);
            cm.modifyNX(100, 0);//显示得点
            cm.dispose();        
            }
            else{    
            cm.sendOk("请确认你有1个#v4031250#再来兑换!");
                cm.dispose();
            };
        break;
        case 3: 
            if(cm.haveItem(4031250, 10)) {
            cm.sendOk("谢谢! 1000点券已经给你了哦#k");
            cm.gainItem(4031250, -10);
            cm.gainNX(1000);
            cm.modifyNX(1000, 0);//显示得点
            cm.dispose();        
            }
            else{    
            cm.sendOk("请确认你有10个#v4031250#再来兑换!");
                cm.dispose();
            };
        break;
        case 4:
            if(cm.haveItem(2370000, 1)) {
            cm.sendOk("您的#v4000378#已被收回!为了回报你，我给你5000经验")
            cm.gainItem(2370000, -1);
            cm.gainExp(50000000); 
            cm.dispose();
            } else {
                cm.sendOk("#e您需要 #b1#k 个 #v2370000#\r\n请检查您的背包中是否有1个再来领取。")
                cm.dispose();     
            };    
        break;
        case 5:
            if(cm.haveItem(4000038, 100)) {
            cm.sendOk("您的#v4000038#已被收回!为了回报你，我给你骑士团战车")
            cm.gainItem(4000038, -100);
            cm.gainItem(1902031,1);
            cm.gainItem(1912024,1);
            cm.dispose();
            } else {
                cm.sendOk("#e您需要 #b100#k 个 #v4000038#\r\n请检查您的背包中是否有100个再来领取。")
                cm.dispose();    
            };    
        break;
        case 6:
            if(cm.haveItem(2370001, 1)) {
            cm.sendOk("您的#v2370001#已被收回!为了回报你，我给你2500经验")
            cm.gainItem(2370001, -1);
            cm.gainExp(25000000); 
            cm.dispose();
            } else {
                cm.sendOk("#e您需要 #b1#k 个 #v2370001#\r\n请检查您的背包中是否有1个再来领取。")
                cm.dispose();      
            };
        break;
        case 7:
            if(cm.haveItem(2370001, 1)) {
            cm.sendOk("您的#v2370001#已被收回!为了回报你，我给你2500经验")
            cm.gainItem(2370001, -1);
            cm.gainExp(25000000); 
            cm.dispose();
            } else {
                cm.sendOk("#e您需要 #b1#k 个 #v2370001#\r\n请检查您的背包中是否有1个再来领取。")
                cm.dispose();    
            };
        break
        case 8:
            if(cm.haveItem(2370001, 1)) {
            cm.sendOk("您的#v2370001#已被收回!为了回报你，我给你2500经验")
            cm.gainItem(2370001, -1);
            cm.gainExp(25000000); 
            cm.dispose();
            } else {
                cm.sendOk("#e您需要 #b1#k 个 #v2370001#\r\n请检查您的背包中是否有1个再来领取。")
                cm.dispose();    
            };
        break
        case 9:
            if(cm.haveItem(2370001, 1)) {
            cm.sendOk("您的#v2370001#已被收回!为了回报你，我给你2500经验")
            cm.gainItem(2370001, -1);
            cm.gainExp(25000000); 
            cm.dispose();
            } else {
                cm.sendOk("#e您需要 #b1#k 个 #v2370001#\r\n请检查您的背包中是否有1个再来领取。")
                cm.dispose();  
            };
        }
    }
