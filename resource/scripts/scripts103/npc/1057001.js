importPackage(net.sf.odinms.client);
var status = 0;
var totAp = 0;
var newAp;
var newStr;
var newDex;
var newInt;
var newLuk;
var Strings = Array("","","","","");
var aplist;
var apnamelist = Array(1,2,3,4);//用来排序的数组
var statup;
var p;
var kou = 30000;
var kou2 = 30000;   //转身后需要扣掉的能力点
var needMeso = 2000000000;
var needReborns = 200;
var count = 1;
var current;
function start() {
    statup = new java.util.ArrayList();
    p = cm.c.getPlayer();
    totAp = p.getRemainingAp() + p.getStr() + p.getDex() + p.getInt() + p.getLuk();  //总能力点
    newStr =  p.getStr();
    newDex =  p.getDex();
    newInt =  p.getInt();
    newLuk =  p.getLuk();
    aplist= Array(p.getStr(), p.getDex(), p.getInt(), p.getLuk());
    current = p.getBossLog("转生");
    if(p.getVip() < 2){
        kou = 30000;
        kou2 = 30000;
    }else if(p.getVip() == 2){
        kou = 30000;
        kou2 = 30000;
    }else if(p.getVip() == 3){
        kou = 30000;
        kou2 = 30000;
    }else if(p.getVip() == 4){
        kou = 30000;
        kou2 = 30000;
    }else if (p.getVip() == 5){
        kou = 30000;
        kou2 = 30000;
    }else if (p.getVip() >= 6){
        kou = 30000;
        kou2 = 30000;
    }
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {//ExitChat
        cm.dispose();
    }else if (mode == 0){//No
        cm.sendOk("好的, 请告诉我你确定需要 #b飞升#k.");
        cm.dispose();
    }else{            //Regular Talk
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            cm.getPlayer().saveToDB(true);
            cm.sendYesNo("啊哈... 伟大的#b#h ##k。你已经通过一个漫长而充满挑战的道路，终于成为了风起云涌的人物。\r\n如果您能给我"+needMeso+"金币和#b1个圣杯#k #v4031454#。 \r\n我可以用我的乾坤大挪移心法，助你转世！\r\n每日可转次数："+count+"。您今日已转次数："+current+"。\r\n"+"您已经转生次数(你玩这个角色总转生次数!)："+p.GetReborns()+"\r\n您将成为1级的 #b新手#k, 并且同时将您所有的#b技能#k扣除，\r\n但你能传承你的属性总和扣除#r" + kou2 + "#k点后剩余的点数，你是否想#r转生#k呢?");
        }else if (status == 1) {
            if(cm.getChar().getReborns() < needReborns){
                cm.sendOk("很抱歉，您需要" + needReborns + "级，才可以投胎转世.");
                cm.dispose();
            }else if (totAp < (kou + 16)){
                cm.sendOk("您的属性点不足3W无法修炼飞升!");
                cm.dispose();
            }else if (cm.haveItem(4031454,1) == false){
                cm.sendOk("你没有带来#b圣杯#k ");
                cm.dispose();
            }else if (cm.getMeso() < needMeso) {
                cm.sendOk("你没有"+needMeso+"金币,我不能帮你的忙哦.");
                cm.dispose();
            }else if (count <= current) {
                cm.sendOk("今天您已经无法转身了。");
                cm.dispose();
            }else{
                var temp;
                for (var j = 0; j < 3; j++){   //有名的冒气泡排顺法。主要用于排列数组apnamelist里的数据。实现从大到小排列能力值。
                    for (var i = 0; i < 3 - j; i++){
                        if(aplist[i] < aplist[i+1]){
                            temp = aplist[i];
                            aplist[i] = aplist[i+1];
                            aplist[i+1] = temp;
                            temp = apnamelist[i];
                            apnamelist[i] = apnamelist[i+1];
                            apnamelist[i+1] = temp;
                        }
                    }
                }
                if(p.getRemainingAp() >= kou){
                    newAp = p.getRemainingAp() - kou;
                    Strings[0] = " AP值将扣去 #r" + kou + " #k点";
                    kou = 0;
                }else{
                    newAp =0;
                    kou = kou - p.getRemainingAp();
                    if (p.getRemainingAp() > 0){
                        Strings[0] = " AP值将扣去 #r" + p.getRemainingAp() + " #k点";
                    }
                }
                for(x = 0; x < 4; x++){
                    if(kou > 0){
                        if(apnamelist[x] == 1){
                            if(p.getStr() - 4 >= kou){
                                newStr = p.getStr() - kou;
                                Strings[1] = " 力量将扣去 #r" + kou + "#k 点";
                                kou = 0;
                            }else{
                                newStr = 4;
                                kou = kou - (p.getStr() - 4);
                                Strings[1] = " 力量将扣去 #r" + (p.getStr() - 4) + "#k 点";
                            }
                        }else if(apnamelist[x] == 2){
                            if(p.getDex() - 4 >= kou){
                                newDex = p.getDex() - kou;
                                Strings[2] = " 敏捷将扣去 #r" + kou + "#k 点";
                                kou = 0;
                            }else{
                                newDex = 4;
                                kou = kou - (p.getDex() - 4);
                                Strings[2] = " 敏捷将扣去 #r" + (p.getDex() - 4) + "#k 点";
                            }
                        }else if(apnamelist[x] == 3){
                            if(p.getInt() - 4 >= kou){
                                newInt = p.getInt() - kou;
                                Strings[3] = " 智力将扣去 #r" + kou + "#k 点";
                                kou = 0;
                            }else{
                                newInt = 4;
                                kou = kou - (p.getInt() - 4);
                                Strings[3] = " 智力将扣去 #r" + (p.getInt() - 4) + "#k 点";
                            }
                        }else if(apnamelist[x] == 4){
                            if(p.getLuk() - 4 >= kou){
                                newLuk = p.getLuk() - kou;
                                Strings[4] = " 运气将扣去 #r" + kou + "#k 点";
                                kou = 0;
                            }else{
                                newInt = 4;
                                kou = kou - (p.getLuk() - 4);
                                Strings[4] = " 运气将扣去 #r" + (p.getLuk() - 4) + "#k 点";
                            }
                        }
                        if (kou < 1) break;
                    }
                }
                var St = "";
                for(s = 0; s < 5; s++){
                    if(Strings[s] != "") St = St + Strings[s] + "\r\n";
                }
                cm.sendOk("#e#b您做得非常好,由于您是VIP#r" + cm.getChar().getVip() + "\r\n#b您转身后能力值会扣除#r" + kou2 + "#b点!扣除详细情况如下!#k\r\n\r\n" + St + "#n");
            }
        }else if (status == 2){
            cm.sendSimple("恭喜你修炼有成. 你想投胎成为什么职业呢?#b\r\n#L0#新手#l\r\n#L1#骑士团#l\r\n#L2#战神#l\r\n#L3#反抗者#l\r\n#L4#龙神#l\r\n#L5#双弩精灵#l\r\n#L6#恶魔猎手#l#k");
        }else if (status == 3){
            var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();
            var toDrop = ii.randomizeStats(ii.getEquipById(4001129));
            if (selection == 0) {
                cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null, net.sf.odinms.tools.MaplePacketCreator.getItemMegas(cm.getC().getChannel(),cm.getPlayer().getName() + " : " + "成功进行了一次转世轮回变为新手，大家祝贺它吧！！！",toDrop, true).getBytes());
                cm.changeJob(net.sf.odinms.client.MapleJob.BEGINNER);
            }
            if (selection == 1) {
                cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null, net.sf.odinms.tools.MaplePacketCreator.getItemMegas(cm.getC().getChannel(),cm.getPlayer().getName() + " : " + "成功进行了一次转世轮回变为初心者，大家祝贺它吧！！！",toDrop, true).getBytes());
                cm.changeJob(net.sf.odinms.client.MapleJob.KNIGHT);
            }
            if (selection == 2) {
                cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null, net.sf.odinms.tools.MaplePacketCreator.getItemMegas(cm.getC().getChannel(),cm.getPlayer().getName() + " : " + "成功进行了一次转世轮回变为战神，大家祝贺它吧！！！",toDrop, true).getBytes()); 
                cm.changeJob(net.sf.odinms.client.MapleJob.Aran);
            }
            if (selection == 3) {
                cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null, net.sf.odinms.tools.MaplePacketCreator.getItemMegas(cm.getC().getChannel(),cm.getPlayer().getName() + " : " + "成功进行了一次转世轮回变为反抗者，大家祝贺它吧！！！",toDrop, true).getBytes()); 
                cm.changeJob(net.sf.odinms.client.MapleJob.Resistance);
            }
            if (selection == 4) {
                cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null, net.sf.odinms.tools.MaplePacketCreator.getItemMegas(cm.getC().getChannel(),cm.getPlayer().getName() + " : " + "成功进行了一次转世轮回变为龙神，大家祝贺它吧！！！",toDrop, true).getBytes()); 
                cm.changeJob(net.sf.odinms.client.MapleJob.Evan);
            }
            if (selection == 5) {
                cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null, net.sf.odinms.tools.MaplePacketCreator.getItemMegas(cm.getC().getChannel(),cm.getPlayer().getName() + " : " + "成功进行了一次转世轮回变为双弩精灵，大家祝贺它吧！！！",toDrop, true).getBytes()); 
                cm.changeJob(net.sf.odinms.client.MapleJob.精灵的基础);
            }
            if (selection == 6) {
                cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null, net.sf.odinms.tools.MaplePacketCreator.getItemMegas(cm.getC().getChannel(),cm.getPlayer().getName() + " : " + "成功进行了一次转世轮回变为恶魔猎手，大家祝贺它吧！！！",toDrop, true).getBytes()); 
                cm.changeJob(net.sf.odinms.client.MapleJob.恶魔猎手的基础);
            }
            cm.gainMeso(-needMeso);
            cm.gainItem(4031454,-1);
            cm.getChar().ClearAllSkills();
            //cm.unequipEverything(); //脱装备语句，需要的去掉前面的“//”
            cm.sendNext("#e#b您做得非常好#k, 您已经成功转生了,您现在的属性点情况如下：\r\n" + "   力量: #r" + newStr + " #k点" + "\r\n   敏捷: #r" + newDex + " #k点" + "\r\n   智力: #r" + newInt + " #k点" + "\r\n   运气: #r" + newLuk + " #k点" + "\r\n   未分配的AP: #r" + newAp + " #k点");
            p.setRemainingAp(newAp);
            p.setStr(newStr);
            p.setDex(newDex);
            p.setInt(newInt);
            p.setLuk(newLuk);
            p.setReborns(1);
            statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.STR, java.lang.Integer.valueOf(newStr)));
            statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.DEX, java.lang.Integer.valueOf(newDex)));
            statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.LUK, java.lang.Integer.valueOf(newLuk)));
            statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.INT, java.lang.Integer.valueOf(newInt)));
            statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.Reborns, java.lang.Integer.valueOf(1)));
            statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.EXP, java.lang.Integer.valueOf(1)));
            statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.AVAILABLEAP, java.lang.Integer.valueOf(newAp)));
            p.getClient().getSession().write (net.sf.odinms.tools.MaplePacketCreator.updatePlayerStats(statup,p));
            cm.getChar().doReborns(); //转身次数记录
            p.setBossLog("转生");
            cm.getPlayer().saveToDB(true);  //保存
            cm.dispose();
        }
    }
}
 
    
