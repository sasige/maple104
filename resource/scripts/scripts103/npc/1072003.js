var mmm="#fEffect/CharacterEff/1112905/0/1#";
function start() {

    cm.sendSimple (mmm+"你需要给我一个#v4031579#就可以换到120级各职业的套装哟\r\n#r套装包括[武器,帽子,衣服,裤子,鞋子,手套]\r\n换的时候请注意背包里面留出5个空位置,不然没拿到不负责\r\n#L0##b换取一套[#r战士套装#b]#k#L1##b换取一套[#r魔法师套装#k#b]#k\r\n#r#L2##b换取一套[#r弓箭手套装#b]#k#r#L3##b换取一套[#r飞狭套装#b]#k\r\n#L4##b换取一套[#r海盗套装#b]#k")
    }

function action(mode, type, selection) {
        cm.dispose();

    switch(selection){
        case 0: 
            if(cm.haveItem(4031579, 1)) {
            cm.sendOk("兑换成功");
	    cm.gainItem(1402046,1) ; //武器
	    cm.gainItem(1432047,1) ; //武器
	    cm.gainItem(1002776,1) ; //帽子
	    cm.gainItem(1082234,1) ; //手套
	    cm.gainItem(1052155,1) ; //衣服
	    cm.gainItem(1072355,1) ; //鞋
            cm.gainItem(4031579, -1);
            cm.dispose();
            }else{
            cm.sendOk("你没有一个#v4031579#");
            cm.dispose();
            }
        break;
        case 1: 
            if(cm.haveItem(4031579, 1)) {
            cm.sendOk("兑换成功");
	    cm.gainItem(1382057,1) ; //武器
	    cm.gainItem(1002777,1) ; //帽子
	    cm.gainItem(1082235,1) ; //手套
	    cm.gainItem(1052156,1) ; //衣服
	    cm.gainItem(1072356,1) ; //鞋
            cm.gainItem(4031579, -1);
            cm.dispose();
            }else{
            cm.sendOk("你没有一个#v4031579#");
            cm.dispose();
            }
        break;
        case 2:
            if(cm.haveItem(4031579, 1)) {
            cm.sendOk("兑换成功");
	    cm.gainItem(1452057,1) ; //武器
	    cm.gainItem(1002778,1) ; //帽子
	    cm.gainItem(1082236,1) ; //手套
	    cm.gainItem(1052157,1) ; //衣服
	    cm.gainItem(1072357,1) ; //鞋
            cm.gainItem(4031579, -1);
            cm.dispose();
            }else{
            cm.sendOk("你没有一个#v4031579#");
            cm.dispose();
            }
        break;
        case 3:
            if(cm.haveItem(4031579, 1)) {
            cm.sendOk("兑换成功");
	    cm.gainItem(1332074,1) ; //武器
	    cm.gainItem(1002779,1) ; //帽子
	    cm.gainItem(1082237,1) ; //手套
	    cm.gainItem(1052158,1) ; //衣服
	    cm.gainItem(1072358,1) ; //鞋
            cm.gainItem(4031579, -1);
            cm.dispose();
            }else{
            cm.sendOk("你没有一个#v4031579#");
            cm.dispose();
            }
        break;
        case 4:
            if(cm.haveItem(4031579, 1)) {
            cm.sendOk("兑换成功");
	    cm.gainItem(1492023,1) ; //武器
	    cm.gainItem(1002780,1) ; //帽子
	    cm.gainItem(1082238,1) ; //手套
	    cm.gainItem(1052159,1) ; //衣服
	    cm.gainItem(1072359,1) ; //鞋
            cm.gainItem(4031579, -1);
            cm.dispose();
            }else{
            cm.sendOk("你没有一个#v4031579#");
            cm.dispose();
            }
        break;
        case 5:
            if(cm.haveItem(4001126, 500)) {
            cm.sendOk("兑换成功")
            cm.gainItem(4001126, -500);
            cm.modifyNx(2000);//抵用卷
            cm.dispose();
            } else {
                cm.sendOk("您需要#b500#k个#v4001126#\r\n请检查您的背包中是否有500个再来领取。")
                cm.dispose();    
            };
        break;
        case 6:
            if(cm.haveItem(4001126, 1000)) {
            cm.sendOk("兑换成功")
            cm.gainItem(4001126, -1000);
            cm.gainItem(4002000, +1);
            cm.dispose();
            } else {
                cm.sendOk("您需要#b1000#k个#v4001126#\r\n请检查您的背包中是否有1000个再来领取。")
                cm.dispose();
            };
        break;
        case 7:
            if(cm.haveItem(4001126, 2000)) {
            cm.sendOk("兑换成功")
            cm.gainItem(4001126, -2000);
            cm.gainItem(4031454, +1);
            cm.dispose();
            } else {
                cm.sendOk("#e您需要2000个#v4001126#\r\n请检查您的背包中是否有2000个再来换取。")
                cm.dispose();
            };  
        break;
        case 8:
            if(cm.getMeso() >= 1900000000) {
                       cm.sendOk("请确认你的背包里面少于19E后再来兑换"); 
			 cm.dispose();
                       } else if(cm.haveItem(4031549, 1)) {
                          cm.gainMeso(+100000000);
                          cm.gainItem(4031549, -1);
                          cm.sendOk("兑换成功")
			 cm.dispose();
                    } else { 
                       cm.sendOk("你没有1个#v4031549#"); 
                       cm.dispose();   
				}
        break;
        case 9:
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
