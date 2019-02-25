var status = 0;
var totAp = 0;
var statup;
var p;
var needMeso = 20000000;
var reborns1 = 200;
var reborns2 = 300;
var count = 20;
var current;
var retap = 0;
var gainReborns=0;

function start() {
    p = cm.getChar();
    if (p.getVip() <= 1) {
        retap = 100;
    } else if (p.getVip() == 2) {
        retap = 150;
    } else if (cm.getVip() == 3) {
        retap = 200;
    } else if (cm.getVip() == 4) {
        retap = 250;
    } else if (cm.getVip() == 5) {
        retap = 300;
    }
	//实际保留属性点
    if (p.getFs() == 0) {
        retap = (p.getReborns() - 200) * retap;
		gainReborns = p.getReborns()-200;
    }
    if (p.getFs() == 1) {
        retap = (p.getReborns() - 300) * retap;
		gainReborns = p.getReborns()-300;
    }
	
	
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) { //ExitChat
        cm.dispose();
    } else if (mode == 0) { //No
        cm.sendOk("那么等你需要的时候再来找我吧!");
        cm.dispose();
    } else { //Regular Talk
        if (mode == 1) status++;
        else status--;
        if (status == 0) {
            var text = "看来你将开始踏入修真旅程,这将使你的战斗力得到巨大的提升.\r\n";
            text += "#b如果你是第一次修真,那么你需要转生次数达到200转,并且等级为200级,修真成功,将扣除你当前转生次数和根据你VIP的等级到200转时获得的所有属性点,你将获得[仙]称号.当前攻击力会翻一倍.#k\r\n";
            text += "#b如果你是第二次修真,那么你需要转生次数达到300转,并且等级为200级,并且已成功获得[仙]称号,修真成功,将扣除你当前转生次数和根据你VIP的等级到300转时获得的所有属性点,你将获得[神]称号.当前攻击力会翻二倍.#k\r\n";
            text += "#r凡称号一览表:<10[筑基] <20[旋照] <30[开光] <20[旋照] <40[融合] <50[心动] <60[灵寂] <70[结丹] <80[元婴] <90[出窍] <120[分神] <150[合体] <190[渡劫] >190[出窍]\r\n\r\n";
            text += "仙称号一览表: <50[鬼仙] <150[人仙] <250[地仙] <290[天仙] >290[神仙]\r\n\r\n";
            text += "神称号一览表: <100[主神] <200[圣者] <300[神王] <390[神帝] >390[神尊]#k\r\n";
            cm.sendYesNo(text);
        } else if (status == 1) {
			if(p.getLevel() < 200){
				cm.sendOk("很抱歉，您等级需要200级.");
                cm.dispose();
			}
            if (p.getFs() == 0 && p.getReborns() < reborns1) {
                cm.sendOk("很抱歉，您需要" + reborns1 + "转,才可以进行修仙.");
                cm.dispose();
            } else if (p.getFs() == 1 && p.getReborns() < reborns2 ) {
                cm.sendOk("很抱歉，您需要" + reborns2 + "转,才可以进行修神.");
                cm.dispose();
            } else {
                cm.sendYesNo("#e您做得非常好,由于您是VIP#r" + cm.getVip() + "\r\n#b您修真后将会保留#r" + retap + "#k点属性.并且将变为新手职业!");
            }
        } else if (status == 2) {
            //var ii = server.MapleItemInformationProvider().getInstance();
            //var toDrop = ii.randomizeStats(ii.getEquipById(4001129));
            var item = cm.getPlayer().getInventory(Packages.client.inventory.MapleInventoryType.EQUIPPED).getItem(-10);
            if (item != null) {
                if (cm.getPlayer().getInventory(Packages.client.inventory.MapleInventoryType.EQUIP).isFull() == false) {
                    Packages.server.MapleInventoryManipulator.unequip(cm.getC(), -10, cm.getPlayer().getInventory(Packages.client.inventory.MapleInventoryType.EQUIP).getNextFreeSlot());
                } else {
                    cm.sendOk("你因为带有副手装备,但是装备栏没有空位,无法为你提供转生服务!");
                    cm.dispose();
                    return;
                }
            }
            cm.changeJob(0);
            cm.clearSkills(); //清理技能
            //cm.unequipEverything(); //脱装备语句，需要的去掉前面的“//”
            p.setRemainingAp(retap);
            p.getStat().setStr(4, p);
            p.getStat().setDex(4, p);
            p.getStat().setInt(4, p);
            p.getStat().setLuk(4, p);
            p.setLevel(1);
	    p.setReborns(gainReborns);
            p.gainFs(1); //飞升次数+1
            cm.getChar().setBossLog("飞升");
            cm.fakeRelog(); //刷新人物数据
            p.saveToDB(false, false);
            p.levelUp();
            cm.sendOk("#e#b您做得非常好#k, 您已经成功修真了,您现在的属性点情况如下：\r\n" + "   力量: #r" + p.getStat().getStr() + " #k点" + "\r\n   敏捷: #r" + p.getStat().getDex() + " #k点" + "\r\n   智力: #r" + p.getStat().getInt() + " #k点" + "\r\n   运气: #r" + p.getStat().getLuk() + " #k点" + "\r\n   未分配的AP: #r" + p.getRemainingAp() + " #k点");
            cm.getC().getChannelServer().broadcastPacket(Packages.tools.MaplePacketCreator.serverNotice(0x09, cm.getC().getChannel(), "『修真系统』" + " : " + "恭喜" + p.getName() + ",修真了" + p.getFs() + "次,他又变强大了,大家羡慕嫉妒恨吧!"));
            cm.dispose();
        }
    }
}