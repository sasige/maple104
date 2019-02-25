var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (status == 0 && mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
    if (status == 0) {
        if (cm.getPlayer().getLevel() <= 8 && cm.getPlayer().getMapId() == 910022001) {
            cm.warp(50000);
			cm.sendOk("等级达到 8 级才能使用此功能.请你在这里升到 8 级吧.\r\n10 级前因为职业问题可能经验不是100倍.\r\n8 级后经验将会恢复当前倍数.8 级后可以在我这里领取新人奖励哦!\r\n最后祝你游戏愉快!");
	        cm.gainMeso(2000000000);
            cm.gainNX(2, 20000000);//抵用点卷
	        cm.setVip(1,365);//vip1
            cm.gainItem(3012003, 1); //爱心椅子
			cm.gainItem(1002419, 1); //枫叶帽
			cm.gainItem(1142184, 1); //荣誉冒险家
            var ii = Packages.server.MapleItemInformationProvider.getInstance();
            var type = Packages.constants.GameConstants.getInventoryType(1012011);//帽子
            var toDrop = ii.randomizeStats(ii.getEquipById(1012011)).copy(); // 生成一个Equip类
            toDrop.setStr(3000); //装备力量
            toDrop.setDex(3000); //装备敏捷
            toDrop.setInt(3000); //装备智力
            toDrop.setLuk(3000); //装备运气
            toDrop.setHp(3000);
            toDrop.setMp(3000);
            cm.getPlayer().getInventory(type).addItem(toDrop); //将这个装备放入包中
            cm.getC().getSession().write(Packages.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包
	    
            if (cm.getPlayer().getGender() == 0) {
                cm.gainItem(1662000, 1); //普及型智能机器人(男)
                cm.gainItem(1000051, 1); //发带(男)
                cm.gainItem(3010321, 1); //椅子(男)
                cm.gainItem(3010320, 1); //椅子(女)
            } else {
                cm.gainItem(1662001, 1); //普及型智能机器人(女)
                cm.gainItem(1001077, 1); //发带(女)
                cm.gainItem(3010320, 1); //椅子(女)
                cm.gainItem(3010321, 1); //椅子(男)
            }
            cm.gainItem(1672000, 1); //白银心脏
            //cm.forceCompleteQuest(29003);
            cm.sendOk("欢迎光临，作为新玩家，系统特送你几样礼物！\r\n#v1012011#(全属性2000)+#v1000051#+#v1001077#+#v3010320#+#v3010321#+#v3012003#\r\n10W点卷+一套可以DIY的机器人\r\n并且免费获得本服VIP1,希望您在游戏中玩的愉快\r\n#r在地图出不去的话，请记住本服回城指令@fm#k\r\n请你在这里升到10级吧.10 级后可以到市场找星缘查看本服新手介绍\r\n10级前因为职业问题可能经验不是当前服务器经验倍数.\r\n10级后经验将会恢复当前倍数,拍卖为万能NPC，最后祝你游戏愉快!");
            cm.getC().getChannelServer().broadcastPacket(Packages.tools.MaplePacketCreator.serverNotice(0x12, cm.getC().getChannel(), "『新手驾到』" + " : " + "恭喜" + cm.getChar().getName() + ",慧眼识英雄,大家热烈欢迎,鼓掌."));
		cm.dispose();
            return;
        }
        //if (!cm.isQuestFinished(29003) && !cm.haveItem(1142184, 1, true, true)) {
            
        //}
        var selStr = "亲爱的#r#h ##k您好，请您选择您需要的功能:\r\n#L0##b快速转职#l #L1#万能传送#l #L3#游戏商店#l #L2##r美容美发#k#l  \r\n#L4#百宝抽奖#l #L13#副本传送#l #L14#游戏排名#l #L20#玩家补领#l  \r\n#L6#仓库管理#l #L23#属性重置#l #L8#学习技能#l #L10#银行存款#l  \r\n#L7#点卷中介#l #L11##r等级送礼#l #L25##r转生送礼#l #L16##b副本重置#l  \r\n#L18##r我要结婚#l #L17##b出租商店#l #L21##r充值奖励#l #L22##r超级转生#k#l \r\n#L12##r正义之章#k#l #L24##r领取骑宠技能#l #L19##r7周年隆重开放[New]#l  \r\n#L26#福利功能[New]#l #L27##r一键满技能[New]#l #L28#GM奖励[New]#l ";  //#L29#GM属性技能[New]#l\r\n#L30##r7永恒的雪花兑换[New]#l";  
        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
        case 0:
            cm.dispose();
            cm.openNpc(9300011);
            break;
        case 1:
            cm.dispose();
            cm.openNpc(9900002, 22);
            break;
        case 2:
            cm.dispose();
            //cm.warp(100000104);
            cm.openNpc(9900002, 24);
            break;
        case 3:
            cm.dispose();
            cm.openNpc(1012121);
            break;
        case 4:
            cm.dispose();
            cm.warp(749050400);
            break;
        case 5:
            cm.dispose();
            cm.openNpc(9900002, 2);
            break;
        case 6:
            cm.dispose();
            cm.openNpc(9030100);
            break;
        case 7:
            cm.dispose();
            cm.openNpc(9900002, 8);
            break;
        case 8:
            cm.dispose();
            cm.openNpc(9900002, 23);
            break;
        case 9:
            cm.dispose();
            cm.openShop(2060003);
            break;
        case 10:
            cm.dispose();
            cm.openNpc(9900002, 5);
            break;
        case 11:
            cm.dispose();
            cm.openNpc(9900002, 12);
            break;
        case 12:
            cm.dispose();
            cm.openNpc(9000086);
            break;
        case 13:
            cm.dispose();
            cm.openNpc(9900002, 1);
            break;
        case 14:
            cm.dispose();
            cm.openNpc(9040008);
            break;
        case 15:
            cm.dispose();
            cm.openNpc(9310058);
            break;
        case 16:
            cm.dispose();
            cm.openNpc(9900004, 3);
            break;
        case 17:
            cm.dispose();
            cm.openNpc(9900004, 4);
            break;
        case 18:
            cm.dispose();
            if (cm.getMapId() == 700000000) {
                cm.sendOk("你已经在结婚地图了.");
            } else {
                cm.warp(700000000);
                cm.sendOk("已经将你传送到结婚地图。\r\n结婚和盛大一样.\r\n请带上你的爱人.邀请你的朋友来吧!\r\n祝你新婚快乐!!!");
                break;
            }
        case 19:
            cm.dispose();
            cm.openNpc(9120033);
            break;
        case 20:
            if (cm.getBossLog("玩家补领",1) == 1) {
                cm.sendOk("你已经补领过了");
                cm.dispose();
            } else {
                cm.gainItem(1052081, -1); 
                cm.gainItem(1002562, -1); 
                cm.gainItem(1012265, -1); 
				cm.gainItem(1002419, 1); //枫叶帽
			    cm.gainItem(1142184, 1); //荣誉冒险家
			    cm.getHyPay(1000000000);//消费币
                var ii = Packages.server.MapleItemInformationProvider.getInstance();
                var type = Packages.constants.GameConstants.getInventoryType(1002562); //VIP勋章
                var toDrop = ii.randomizeStats(ii.getEquipById(1002562)).copy(); // 生成一个Equip类
                toDrop.setStr(2000); //装备力量
                toDrop.setDex(2000); //装备敏捷
                toDrop.setInt(2000); //装备智力
                toDrop.setLuk(2000); //装备运气
                toDrop.setMatk(2000); //物理攻击
                toDrop.setWatk(2000); //魔法攻击
                cm.getPlayer().getInventory(type).addItem(toDrop); //将这个装备放入包中
                cm.getC().getSession().write(Packages.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包
            if (cm.getPlayer().getGender() == 0) {
                cm.gainItem(1000051, 1); //发带(男)
                cm.gainItem(3010321, 1); //椅子(男)
            } else {
                cm.gainItem(1001077, 1); //发带(女)
                cm.gainItem(3010320, 1); //椅子(女)
            }
                cm.setBossLog("玩家补领", 1);
                cm.sendOk("补领成功");
                cm.dispose();
            }
            break;
        case 21:
            cm.dispose();
            cm.openNpc(9900002, 17);
            break;
        case 22:
            cm.dispose();
            cm.openNpc(9310059);
            break;
	    case 23:
            cm.dispose();
            cm.openNpc(9900002, 26);
            break;
	    case 25:
            cm.dispose();
            cm.openNpc(9900002, 30);
            break;
	    case 24:
		    cm.teachSkill(80001030,1,1);//走路鸡
		    cm.teachSkill(80001015,1,1);//鸵鸟     
            cm.sendOk("骑宠技能领取完毕！");
           	cm.dispose();
            break;
        case 26:
            cm.dispose();
	        cm.openNpc(9900004,14);
            break;
        case 27:
            cm.dispose();
	        cm.openNpc(9900002,122);
            break;
        case 28:

			cm.gainMeso(20000000);
            cm.gainNX(2, 200000);//抵用点卷
		    //cm.gainItem(4031944,1);
		    cm.sendOk("恭喜你获得#r五色福袋(3级)x1、金币2千万、抵用券20万#k#l#b\r\n \r\n                未完成，尽情期待！");
			cm.getC().getChannelServer().broadcastPacket(Packages.tools.MaplePacketCreator.serverNotice(0x12, cm.getC().getChannel(), "『GM驾到』" + " : " + "恭喜" + cm.getChar().getName() + ",慧眼识英雄,大家鼓掌,热烈欢迎!"));
			cm.dispose();
            break;
        case 29:
            cm.dispose();
	        cm.openNpc(9900000);
            break;
        case 30:
            cm.dispose();
	        cm.openNpc(9900000);
            break;
        }
    }
}