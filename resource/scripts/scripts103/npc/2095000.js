var mmm="#fEffect/CharacterEff/1112905/0/1#";
function start() {

    cm.sendSimple ("欢迎来到#r"+cm.GetSN()+"#k,( ^_^ )\r\n这里是坐骑任务！\r\n\r\n 金币:#r" + cm.getMeso() + "#k.\r\n\r\n#r#L0##fEffect/CharacterEff/1112905/0/1#如果要换老虎只是个传说#v1902045v#坐骑技能就需#k\r\n #r#fEffect/CharacterEff/1112905/0/1#要200个#v4000170v#和200个#v4000394v#和200个#v4000171v#和50张#v4002000v#\r\n\r\n\r\n\#r#L1\##fEffect/CharacterEff/1112905/0/1#如果要换筋斗云#v1902028v#坐骑技能就需\r\n#r#L1\##fEffect/CharacterEff/1112905/0/1#要300个#v4000059v#和300个#v4000060v#和300个#v4000061v#和50张#v4002000v#\r\n\r\n\r\n\#r#L2\##fEffect/CharacterEff/1112905/0/1#如果要换花蘑菇坐骑技能就需\r\n#r#L2##fEffect/CharacterEff/1112905/0/1#要300个#v4000001v#和300个#v4000017v#和300个#v4000009v#和50张#v4002000v\r\n\r\n\r\n#l★★★★★★★★★★★★★★★★★★★★★★★★★★★\r\n#k#b#e   #v3994101v#下面的任务需要会员5级以上才可以做#v3994101v#\r\n★★★★★★★★★★★★★★★★★★★★★★★★\r\n\r\n\r\n#r#L3\##fEffect/CharacterEff/1112905/0/1#如果要换蝙蝠魔#v1902035v#坐骑技能就需\r\n#r#L3##fEffect/CharacterEff/1112905/0/1#要300个#v4000082v#和300个#v4000194v#和300个#v4000232v#和50张#v4002000v\r\n\r\n\r\n\r\n\r\n\r\n#L4\##fEffect/CharacterEff/1112905/0/1#如果要换圣诞雪橇坐骑技能就需\r\n#r#L4##fEffect/CharacterEff/1112905/0/1#要300#v4000048v#和300个#v4000055v#和300个#v4000049#和50张#v4002000#")
}

function action(mode, type, selection) {
    cm.dispose();

    switch(selection){
        case 0:
        if (!cm.haveItem(4000170,200)) {
        cm.sendOk("抱歉，你没有200个#v4000170v#无法换取坐骑.亲 请您继续收集)");
        } else if (!cm.haveItem(4000394,200)) {
        cm.sendOk("抱歉，你没有200个#v4000394v#无法换取坐骑.亲 请您继续收集)"); 
        } else if (!cm.haveItem(4000171,200)) {
        cm.sendOk("抱歉，你没有200个#4000171v#无法换取坐骑.亲 请您继续收集)"); 
        } else if (!cm.haveItem(4002000,50)) {
        cm.sendOk("抱歉，你没有50个#v4002000v#无法换取坐骑.亲 请您继续收集)");  
} else {
	cm.gainItem(4000170,-200);
	cm.gainItem(4000171,-200);
	cm.gainItem(4000394,-200);
	cm.gainItem(4002000,-50);
   cm.teachSkill(80001010,10,10);//老虎坐骑技能
                cm.sendOk("恭喜你你已经获得了老虎坐骑技能!");
cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『"+ cm.getChar().getName() +"』"+" : "+"哇~~!!★★★★★已经获得了虎哥只是个传说★★★★★恭喜他吧!!"))
                cm.dispose();
            }
            break;
        case 1:
        if (!cm.haveItem(4000059,300)) {
        cm.sendOk("抱歉，你没有300个#v4000059v#无法换取坐骑.亲 请您继续收集)");
        } else if (!cm.haveItem(4000060,300)) {
        cm.sendOk("抱歉，你没有300个#v4000060v#无法换取坐骑.亲 请您继续收集)"); 
        } else if (!cm.haveItem(4000061,300)) {
        cm.sendOk("抱歉，你没有300个#4000061v#无法换取坐骑.亲 请您继续收集)"); 
        } else if (!cm.haveItem(4002000,50)) {
        cm.sendOk("抱歉，你没有50个#v4002000v#无法换取坐骑.亲 请您继续收集)");  
} else {
	cm.gainItem(4000059,-300);
	cm.gainItem(4000060,-300);
	cm.gainItem(4000061,-300);
	cm.gainItem(4002000,-50);
   cm.teachSkill(80001007,10,10);//筋斗云
                cm.sendOk("恭喜你你已经获得了筋斗云坐骑技能!");
cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『"+ cm.getChar().getName() +"』"+" : "+"哇~~!!★★★★★已经获得了悟空的筋斗云★★★★★恭喜他吧!!"))

                cm.dispose();
            };
            break;
        case 2:
        if (!cm.haveItem(4000009,300)) {
        cm.sendOk("抱歉，你没有300个#v4000009v#无法换取坐骑.亲 请您继续收集)");
        } else if (!cm.haveItem(4000017,300)) {
        cm.sendOk("抱歉，你没有300个#v4000017v#无法换取坐骑.亲 请您继续收集)"); 
        } else if (!cm.haveItem(4000001,300)) {
        cm.sendOk("抱歉，你没有300个#4000001v#无法换取坐骑.亲 请您继续收集)"); 
        } else if (!cm.haveItem(4002000,50)) {
        cm.sendOk("抱歉，你没有50个#v4002000v#无法换取坐骑.亲 请您继续收集)");  
} else {
	cm.gainItem(4000017,-300);
	cm.gainItem(4000001,-300);
	cm.gainItem(4000009,-300);
	cm.gainItem(4002000,-50);
   cm.teachSkill(80001013,10,10);//花蘑菇
                cm.sendOk("恭喜你你已经获得了花蘑菇坐骑技能!");
cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『"+ cm.getChar().getName() +"』"+" : "+"哇~~!!★★★★★已经获得了花蘑菇坐骑★★★★★恭喜他吧!!"))
                cm.dispose();
            };  
            break;
        case 3:
         if(cm.getChar().getVip() < 5) {
cm.sendOk("你不是黄金VIP5，所以换不到...")
        } else if (!cm.haveItem(4000082,300)) {
        cm.sendOk("抱歉，你没有300个#v4000082v#无法换取坐骑.亲 请您继续收集)");
        } else if (!cm.haveItem(4000194,300)) {
        cm.sendOk("抱歉，你没有300个#v4000194v#无法换取坐骑.亲 请您继续收集)"); 
        } else if (!cm.haveItem(4000232,300)) {
        cm.sendOk("抱歉，你没有300个#4000232v#无法换取坐骑.亲 请您继续收集)"); 
        } else if (!cm.haveItem(4002000,50)) {
        cm.sendOk("抱歉，你没有50个#v4002000v#无法换取坐骑.亲 请您继续收集)");  
} else {
	cm.gainItem(4000082,-300);
	cm.gainItem(4000194,-300);
	cm.gainItem(4000232,-300);
	cm.gainItem(4002000,-50);
   cm.teachSkill(80001011,10,10);//蝙蝠
                cm.sendOk("恭喜你你已经获得了蝙蝠魔先生坐骑技能!");
cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『"+ cm.getChar().getName() +"』"+" : "+"哇~~!!★★★★★已经获得了蝙蝠魔先生★★★★★恭喜他吧!!"))
                cm.dispose();
            };   
            break;
           case 4:
         if(cm.getChar().getVip() < 5) {
cm.sendOk("你不是黄金VIP5，所以换不到...")
         } else if (!cm.haveItem(4000048,300)) {
        cm.sendOk("抱歉，你没有300个#v4000048v#无法换取坐骑.亲 请您继续收集)");
        } else if (!cm.haveItem(4000049,300)) {
        cm.sendOk("抱歉，你没有300个#v4000049v#无法换取坐骑.亲 请您继续收集)"); 
        } else if (!cm.haveItem(4000055,300)) {
        cm.sendOk("抱歉，你没有300个#4000055v#无法换取坐骑.亲 请您继续收集)"); 
        } else if (!cm.haveItem(4002000,50)) {
        cm.sendOk("抱歉，你没有50个#v4002000v#无法换取坐骑.亲 请您继续收集)");  
} else {
	cm.gainItem(4000048,-300);
	cm.gainItem(4000049,-300);
	cm.gainItem(4000055,-300);
	cm.gainItem(4002000,-50);
   cm.teachSkill(80001022,10,10);//圣诞雪橇
                cm.sendOk("恭喜你你已经获得了圣诞雪橇坐骑技能!");
cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『"+ cm.getChar().getName() +"』"+" : "+"哇~~!!★★★★★已经获得了圣诞雪橇★★★★★恭喜他吧!!"))
                cm.dispose();
            };   
            break;
           case 89:
            if(player.GetMoney() >= 100){
                cm.sendOk("谢谢! 1000 点卷已添加到您的帐户! 享受吧! #r赶快点拍卖购买你合适的【点装】吧!#k");
                player.GainMoney(-100);              
		cm.gainNX(1000);                
		cm.dispose();   
            }
            else{    
                cm.sendOk("请确认你有100元宝的时候在在来兑换");
                cm.dispose();
            };   
            break;

        case 4:
            if(cm.haveItem(4002000, 5)) {
                cm.sendOk("您的5个#v4002000#已被收回!为了回报你，我给你1000点卷!")
                cm.gainItem(4002000, -5);
                cm.gainNX(1000);
                cm.dispose();
            } else {
                cm.sendOk("#e您需要 #b5#k 个 #v4002000#\r\n请检查您的背包中是否有5个再来领取。")
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
