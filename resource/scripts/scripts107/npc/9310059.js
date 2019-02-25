var status = 0;
var totAp = 0;
var statup;
var p;
var needMeso = 20000000;
var needLevel = 180;
var count = 20;
var current;
var retap = 0;

function start() {
    p = cm.getChar();
    totAp = p.getRemainingAp() + p.getStat().getStr() + p.getStat().getDex() + p.getStat().getInt() + p.getStat().getLuk(); //总能力点
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
    retap = (cm.getChar().getReborns() + 1) * retap; //实际保留属性点
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) { //ExitChat
        cm.dispose();
    } else if (mode == 0) { //No
        cm.sendOk("好的, 请告诉我你确定需要 #b投胎转世#k.");
        cm.dispose();
    } else { //Regular Talk
        if (mode == 1) status++;
        else status--;
        if (status == 0) {
            var zscx = 20 - cm.getBossLog("转生");
            var text = "啊哈... 伟大的#b#h ##k。你已经通过一个漫长而充满挑战的道路，终于成为了风起云涌的人物。\r\n";
            text += "如果您能给我" + needMeso + "金币和#b1个圣杯#k #v4031454#。 \r\n";
            text += "我可以用我的乾坤大挪移心法，助你转世！\r\n";
            text += "您今天已经转生次数：#d" + cm.getBossLog("转生") + "#k，你今天还可以转：" + zscx + "次\r\n";
            text += "您已经转生次数(你玩这个角色总转生次数!)：#r" + cm.getChar().getReborns() + "#k\r\n";
            text += "您将成为1级的 #b新手#k, 并且同时将您所有的#b技能#k扣除，\r\n";
            text += "传承你的属性将保留#r" + retap + "#k点，你是否想#r转生#k?\r\n\r\n";
            text += "VIP1保留100属性点　VIP2保留150属性点\r\nVIP3保留200属性点　VIP4保留250属性点\r\nVIP5保留300属性点";
            cm.sendYesNo(text);
        } else if (status == 1) {
            if (cm.getChar().getLevel() < needLevel) {
                cm.sendOk("很抱歉，您需要" + needLevel + "级，才可以投胎转世.");
                cm.dispose();
            } else if (cm.haveItem(4031454, 1) == false) {
                cm.sendOk("你没有带来#b圣杯#k ");
                cm.dispose();
            } else if (cm.getMeso() < needMeso) {
                cm.sendOk("你没有" + needMeso + "金币,我不能帮你的忙哦.");
                cm.dispose();
            } else if (cm.getChar().getBossLog("转生") == 20) {
                cm.sendOk("今天您已经无法转身了。");
                cm.dispose();
            } else {
                cm.sendYesNo("#e您做得非常好,由于您是VIP#r" + cm.getVip() + "\r\n#b您转身后将会保留#r" + retap + "#k点属性.并且将变为新手职业!");
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
            cm.gainMeso(-needMeso);
            cm.gainItem(4031454, -1); //圣杯
            cm.gainItem(4310003, 1); //转生证明
            cm.gainNX(2, 1000); //抵用点卷
            cm.clearSkills(); //清理技能
            //cm.unequipEverything(); //脱装备语句，需要的去掉前面的“//”
            p.setRemainingAp(retap-40);
            p.getStat().setStr(14, p);
            p.getStat().setDex(14, p);
            p.getStat().setInt(14, p);
            p.getStat().setLuk(14, p);
            p.setLevel(2);
            p.gainReborns(1); //转身次数记录
            cm.getChar().setBossLog("转生");
            //cm.fakeRelog(); //刷新人物数据
			p.levelUp();
            p.saveToDB(false,false);
            cm.sendOk("#e#b您做得非常好#k, 您已经成功转生了,您现在的属性点情况如下：\r\n" + "   力量: #r" + p.getStat().getStr() + " #k点" + "\r\n   敏捷: #r" + p.getStat().getDex() + " #k点" + "\r\n   智力: #r" + p.getStat().getInt() + " #k点" + "\r\n   运气: #r" + p.getStat().getLuk() + " #k点" + "\r\n   未分配的AP: #r" + p.getRemainingAp() + " #k点");
            cm.getC().getChannelServer().broadcastPacket(Packages.tools.MaplePacketCreator.serverNotice(0x09, cm.getC().getChannel(), "『转生系统』" + " : " + "恭喜" + cm.getChar().getName() + ",转生了" + cm.getChar().getReborns() + "次,他又变强大了,大家羡慕嫉妒恨吧!"));
            cm.dispose();
        }
    }
}