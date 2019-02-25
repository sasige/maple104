importPackage(net.sf.odinms.client);

var status = 0;
var jobName;
var job;
var ico11 = "#fEffect/CharacterEff/1112903/0/0#";

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            cm.sendNext("嗨，我是 #b菇菇博士#k 我可以帮助你快速转职哦~~！");
        } else if (status == 1) {
            
            //骑士团
            if(cm.getJob().getId() >= 1000 && cm.getJob().getId() <= 1510){
                cm.sendNext("哇，你是骑士团的一员，我很高兴为你服务哦！！！");
                status = 160;
                return;
            }
            
            //战神
            if(cm.getJob().getId() >= 2000 && cm.getJob().getId() <= 2215 && (cm.getJob().getId() != 2002 && cm.getJob().getId() != 2300 && cm.getJob().getId() != 2310 && cm.getJob().getId() != 2311 && cm.getJob().getId() != 2312)){//战神屏蔽双弩的职业代码 以免误判 导致转职错误 -  笔芯
                cm.sendNext("哇~~战神战来了！新职业哦~我很高兴为你服务哦！！！");
                status = 163;
                return;
            }
            //恶魔猎手的开始
            if(cm.getJob().getId() >=3001 && cm.getJob().getId() <= 3112) {
                cm.sendNext("哇~~恶魔猎手来了!!新职业哦~我很高兴为你服务哦！！！");
                status = 303;
                return;
            }
			
            //反抗者的开始
            if(cm.getJob().getId() >=3000 && cm.getJob().getId() <=3512 && (cm.getJob().getId() != 3001 && cm.getJob().getId() != 3100 && cm.getJob().getId() != 3110 && cm.getJob().getId() != 3111 && cm.getJob().getId() != 3112)) {//反抗者屏蔽恶魔猎手的职业代码 以免误判 导致转职错误 -  笔芯
                cm.sendNext("哇~~反抗者来了!!新职业哦~我很高兴为你服务哦！！！");
                status = 200;
                return;
            }
			
            //双刀的开始
            /*  if(cm.getJob().getId() == 400 && (cm.getLevel() >= 20 && cm.getLevel() < 30)) {
                cm.sendNext("嗨~我们又见面了,恭喜你达到#r["+cm.getLevel()+"级]#k看起来你想转职为一名飞侠的新职业[见习刀客]?");
                status = 300;
                return;
            }*/
            
            
            //炮手的开始
            if(cm.getJob().getId()>= 501 && cm.getJob().getId()<= 532 && ( 
                cm.getJob().getId() != 510 &&
                cm.getJob().getId() != 511 &&
                cm.getJob().getId() != 508 && //龙的传人
                cm.getJob().getId() != 512 &&
                cm.getJob().getId() != 520 &&
                cm.getJob().getId() != 521 &&
                cm.getJob().getId() != 522)) {
                cm.sendNext("哇~~炮手来了!!新职业哦~我很高兴为你服务哦！！！");
                status = 302;
                return;
            }
            //龙的传人
            if(cm.getJob().getId() == 508 || cm.getJob().getId() == 570 || cm.getJob().getId() == 571 || cm.getJob().getId() == 572 ){
                cm.sendNext("哇，你是#b[龙的传人]#k的一员，我很高兴为你服务哦！！！");
                status = 999;
                return;
            }


            //双弩的开始
            if(cm.getJob().getId() >= 2002 && cm.getJob().getId() <= 2312) {
                cm.sendNext("哇~~双弩精灵来了!!新职业哦~我很高兴为你服务哦！！！");
                status = 301;
                return;
            }

			
            //另一个双刀
            if(cm.getJob().getId() >= 430 && cm.getJob().getId() <= 434) {
                status = 300;
                if(cm.getJob().getId() == 430 && (cm.getLevel() >= 30)) {
                    cm.sendNext("嗨~我们又见面了,恭喜你达到#r[30级]#k看起来你想转职为一名飞侠的新职业[双刀客]?");
                }else if(cm.getJob().getId() == 431 && (cm.getLevel() >= 55)) {
                    cm.sendNext("嗨~我们又见面了,恭喜你达到#r[55级]#k看起来你想转职为一名飞侠的新职业[双刀侠]?");
                }else if(cm.getJob().getId() == 432 && (cm.getLevel() >= 70)) {
                    cm.sendNext("嗨~我们又见面了,恭喜你达到#r[70级]#k看起来你想转职为一名飞侠的新职业[血刀]?");
                }else if(cm.getJob().getId() == 433 && (cm.getLevel() >= 120)) {
                    cm.sendNext("嗨~我们又见面了,恭喜你达到#r[120级]级]#k看起来你想转职为一名飞侠的新职业[暗影双刀]?");
                }else{
                    cm.sendOk("你的条件不成立!!请检查!!");
                    cm.dispose();
                }
                return;
            }
			
			
			
            if (cm.getLevel() < 255 && cm.getJob().equals(net.sf.odinms.client.MapleJob.BEGINNER)) {
                if (cm.getLevel() < 8) {
                    cm.sendNext("对不起，你至少要达到 #b[8级]#k 我才能为你服务！");
                    status = 98;
                } else if (cm.getLevel() < 10) {
                    cm.sendYesNo("我们需要集结魔法师的精神力去封印魔王的力量,#b管理员#k 正在与魔王对抗,我们应该尽快赶过去支援他,因此你必须比其他职业提前进行修炼并领悟魔法的精髓,这是一条艰苦的道路,那么你想成为 #b魔法师#k 吗？");
                    status = 150;
                } else {
                    cm.sendNext("哇~~我又看到一名新手！\r\n恭喜你达到了 #r[10级]#k  那么你想选择的 #b[第一职业]#k 是？");
                    status = 153;
                }
            } else if (cm.getLevel() < 30) {
                cm.sendNext("怎么样？冒险还算顺利吧。有努力就有回报。当然这一切都不是容易的。当你到达 #r[30级]#k 的时候就可以进行#b[第二次转职]#k到时别忘记来找我哦！");
                status = 98;
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.THIEF)) {
                cm.sendSimple("嗨~我们又见面了，恭喜你达到#r[30级]#k 你想转职为一名？\r\n#L0##b刺客#l    #L1##b侠客#l#k  #L12##b见习刀客#l#k");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.WARRIOR)) {
                cm.sendSimple("嗨~我们又见面了，恭喜你达到#r[30级]#k 你想转职为一名？\r\n#L2##b剑客#l    #L3##b骑士#l    #L4##b枪战士#l#k");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.MAGICIAN)) {
                cm.sendSimple("嗨~我们又见面了，恭喜你达到#r[30级]#k 你想转职为一名？\r\n#L5##b冰雷#l    #L6##b火毒#l    #L7##b牧师#l#k");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.BOWMAN)) {
                cm.sendSimple("嗨~我们又见面了，恭喜你达到#r[30级]#k 你想转职为一名？\r\n#L8##b猎人#l    #L9##b弩手#l#k");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.PIRATE)) {
                cm.sendSimple("嗨~我们又见面了，恭喜你达到#r[30级]#k 你想转职为一名？\r\n#L10##b拳手#l   #L11##b枪手#l");

            } else if (cm.getLevel() < 70) {
                cm.sendNext("怎么样？冒险还算顺利吧。有努力就有回报。当然这一切都不是容易的。当你到达 #r[70级]#k 的时候就可以进行#b[第三次转职]#k到时别忘记来找我哦！");
                status = 98;
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.ASSASSIN)) {
                status = 63;
                cm.sendYesNo("恭喜你达到了 #r[70级]#k 你现在就要完成 #b[第三次转职]#k 吗？");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.BANDIT)) {
                status = 66;
                cm.sendYesNo("恭喜你达到了 #r[70级]#k 你现在就要完成 #b[第三次转职]#k 吗？");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.HUNTER)) {
                status = 69;
                cm.sendYesNo("恭喜你达到了 #r[70级]#k 你现在就要完成 #b[第三次转职]#k 吗？");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.CROSSBOWMAN)) {
                status = 72;
                cm.sendYesNo("恭喜你达到了 #r[70级]#k 你现在就要完成 #b[第三次转职]#k 吗？");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.FP_WIZARD)) {
                status = 75;
                cm.sendYesNo("恭喜你达到了 #r[70级]#k 你现在就要完成 #b[第三次转职]#k 吗？");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.IL_WIZARD)) {
                status = 78;
                cm.sendYesNo("恭喜你达到了 #r[70级]#k 你现在就要完成 #b[第三次转职]#k 吗？");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.CLERIC)) {
                status = 81;
                cm.sendYesNo("恭喜你达到了 #r[70级]#k 你现在就要完成 #b[第三次转职]#k 吗？");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.FIGHTER)) {
                status = 84;
                cm.sendYesNo("恭喜你达到了 #r[70级]#k 你现在就要完成 #b[第三次转职]#k 吗？");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.PAGE)) {
                status = 87;
                cm.sendYesNo("恭喜你达到了 #r[70级]#k 你现在就要完成 #b[第三次转职]#k 吗？");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.SPEARMAN)) {
                status = 90;
                cm.sendYesNo("恭喜你达到了 #r[70级]#k 你现在就要完成 #b[第三次转职]#k 吗？");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.BRAWLER)) {
                status = 93;
                cm.sendYesNo("恭喜你达到了 #r[70级]#k 你现在就要完成 #b[第三次转职]#k 吗？");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.GUNSLINGER)) {
                status = 96;
                cm.sendYesNo("恭喜你达到了 #r[70级]#k 你现在就要完成 #b[第三次转职]#k 吗？");
            } else if (cm.getLevel() < 120) {
                cm.sendNext("怎么样？冒险还算顺利吧。有努力就有回报。当然这一切都不是容易的。当你到达 #r[120级]#k 的时候就可以进行#b[第四次转职]#k到时别忘记来找我哦！");
                status = 98;
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.HERMIT)) {
                status = 105;
                cm.sendYesNo("恭喜你达到了 #r[120级]#k 你现在就要完成 #b[第四次转职]#k 吗？");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.CHIEFBANDIT)) {
                status = 108;
                cm.sendYesNo("恭喜你达到了 #r[120级]#k 你现在就要完成 #b[第四次转职]#k 吗？");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.RANGER)) {
                status = 111;
                cm.sendYesNo("恭喜你达到了 #r[120级]#k 你现在就要完成 #b[第四次转职]#k 吗？");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.SNIPER)) {
                status = 114;
                cm.sendYesNo("恭喜你达到了 #r[120级]#k 你现在就要完成 #b[第四次转职]#k 吗？");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.FP_MAGE)) {
                status = 117;
                cm.sendYesNo("恭喜你达到了 #r[120级]#k 你现在就要完成 #b[第四次转职]#k 吗？");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.IL_MAGE)) {
                status = 120;
                cm.sendYesNo("恭喜你达到了 #r[120级]#k 你现在就要完成 #b[第四次转职]#k 吗？");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.PRIEST)) {
                status = 123;
                cm.sendYesNo("恭喜你达到了 #r[120级]#k 你现在就要完成 #b[第四次转职]#k 吗？");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.CRUSADER)) {
                status = 126;
                cm.sendYesNo("恭喜你达到了 #r[120级]#k 你现在就要完成 #b[第四次转职]#k 吗？");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.WHITEKNIGHT)) {
                status = 129;
                cm.sendYesNo("恭喜你达到了 #r[120级]#k 你现在就要完成 #b[第四次转职]#k 吗？");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.DRAGONKNIGHT)) {
                status = 132;
                cm.sendYesNo("恭喜你达到了 #r[120级]#k 你现在就要完成 #b[第四次转职]#k 吗？");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.MARAUDER)) {
                status = 135;
                cm.sendYesNo("恭喜你达到了 #r[120级]#k 你现在就要完成 #b[第四次转职]#k 吗？");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.OUTLAW)) {
                status = 138;
                cm.sendYesNo("恭喜你达到了 #r[120级]#k 你现在就要完成 #b[第四次转职]#k 吗？");
            } else if (cm.getLevel() < 255) {
                cm.sendNext("了不起，你已经完成了所有的转职！\r\n但是你可以 #r[转生]#k ,但需要管理员开启转生功能！");
                status = 98;
            } else if (cm.getLevel() >= 255) {
                cm.sendOk("#d啊哈... 伟大的 #r[#h #]#k ,你已经通过一个漫长而充满挑战的道路,终于成为了风起云涌的人物.但这个世界阴暗的深处,被 #r[管理员]#k #d封印的魔王正蠢蠢欲动,它的残忍无人能及,你需要修炼的更加强大才能拯救所有的居民!"); 
                cm.dispose();
            } else {
                cm.dispose();
            }

        } else if (status == 2) {
            if (selection == 0) {
                jobName = "刺客";
                job = net.sf.odinms.client.MapleJob.ASSASSIN;
            }
            if (selection == 1) {
                jobName = "侠客";
                job = net.sf.odinms.client.MapleJob.BANDIT;
            }
            if (selection == 2) {
                jobName = "剑客";
                job = net.sf.odinms.client.MapleJob.FIGHTER;
            }
            if (selection == 3) {
                jobName = "准骑士";
                job = net.sf.odinms.client.MapleJob.PAGE;
            }
            if (selection == 4) {
                jobName = "枪战士";
                job = net.sf.odinms.client.MapleJob.SPEARMAN;
            }
            if (selection == 5) {
                jobName = "冰雷法师";
                job = net.sf.odinms.client.MapleJob.IL_WIZARD;
            }
            if (selection == 6) {
                jobName = "火毒法师";
                job = net.sf.odinms.client.MapleJob.FP_WIZARD;
            }
            if (selection == 7) {
                jobName = "牧师";
                job = net.sf.odinms.client.MapleJob.CLERIC;
            }
            if (selection == 8) {
                jobName = "猎人";
                job = net.sf.odinms.client.MapleJob.HUNTER;
            }
            if (selection == 9) {
                jobName = "弩手";
                job = net.sf.odinms.client.MapleJob.CROSSBOWMAN;
            }
            if (selection == 10) {
                jobName = "拳手";
                job = net.sf.odinms.client.MapleJob.BRAWLER;
            }
            if (selection == 11) {
                jobName = "火枪手";
                job = net.sf.odinms.client.MapleJob.GUNSLINGER;
            }
            if (selection == 12){
                jobName = "见习刀客";
                job = net.sf.odinms.client.MapleJob.Dual_Blade_1;
            }
            cm.sendYesNo("不错的选择哦，确定要成为一名 #b[" + jobName + "] #k吗？"); 
            
        } else if (status == 3) {
            cm.changeJob(job);
            if (cm.getJob().equals(net.sf.odinms.client.MapleJob.ASSASSIN)) {
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.BANDIT)) {
                cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.FIGHTER)) {
                cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.PAGE)) {
                cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.SPEARMAN)) {
                cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.IL_WIZARD)) {
                cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.FP_WIZARD)) {
                cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.CLERIC)) {
                cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.HUNTER)) {
                cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.CROSSBOWMAN)) {
                cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.BRAWLER)) {
                cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.GUNSLINGER)) {
                cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
            }

            cm.sendOk("转职成功！加油锻炼，当你变的强大的时候记的来找我哦！");
            cm.dispose();

        } else if (status == 61) {
            if (cm.getJob().equals(net.sf.odinms.client.MapleJob.ASSASSIN)) {
                status = 63;
                cm.sendYesNo("#d恭喜你达到了 #r[XXX级]#k #d,你现在就要完成 #r[第XXX次转职]#k 吗？");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.BANDIT)) {
                status = 66;
                cm.sendYesNo("#d恭喜你达到了 #r[XXX级]#k #d,你现在就要完成 #r[第XXX次转职]#k 吗？");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.HUNTER)) {
                status = 69;
                cm.sendYesNo("#d恭喜你达到了 #r[XXX级]#k #d,你现在就要完成 #r[第XXX次转职]#k 吗？");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.CROSSBOWMAN)) {
                status = 72;
                cm.sendYesNo("#d恭喜你达到了 #r[XXX级]#k #d,你现在就要完成 #r[第XXX次转职]#k 吗？");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.FP_WIZARD)) {
                status = 75;
                cm.sendYesNo("#d恭喜你达到了 #r[XXX级]#k #d,你现在就要完成 #r[第XXX次转职]#k 吗？");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.IL_WIZARD)) {
                status = 78;
                cm.sendYesNo("#d恭喜你达到了 #r[XXX级]#k #d,你现在就要完成 #r[第XXX次转职]#k 吗？");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.CLERIC)) {
                status = 81;
                cm.sendYesNo("#d恭喜你达到了 #r[XXX级]#k #d,你现在就要完成 #r[第XXX次转职]#k 吗？");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.FIGHTER)) {
                status = 84;
                cm.sendYesNo("#d恭喜你达到了 #r[XXX级]#k #d,你现在就要完成 #r[第XXX次转职]#k 吗？");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.PAGE)) {
                status = 87;
                cm.sendYesNo("#d恭喜你达到了 #r[XXX级]#k #d,你现在就要完成 #r[第XXX次转职]#k 吗？");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.SPEARMAN)) {
                status = 90;
                cm.sendYesNo("#d恭喜你达到了 #r[XXX级]#k #d,你现在就要完成 #r[第XXX次转职]#k 吗？");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.BRAWLER)) {
                status = 93;
                cm.sendYesNo("#d恭喜你达到了 #r[XXX级]#k #d,你现在就要完成 #r[第XXX次转职]#k 吗？");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.GUNSLINGER)) {
                status = 960;
                cm.sendYesNo("#d恭喜你达到了 #r[XXX级]#k #d,你现在就要完成 #r[第XXX次转职]#k 吗？");
            } else { 
                cm.dispose();
            }

//龙的传人
        }else if(status == 1000){
            var reqlevel = 0;
            if (cm.getJob().getId() == 508){
                reqlevel = 30;
                job = net.sf.odinms.client.MapleJob.龙之传人_2;
            }
            if(cm.getJob().getId() == 570){
                reqlevel = 70;
                job = net.sf.odinms.client.MapleJob.龙之传人_3;
            }
            if(cm.getJob().getId() == 571){
                reqlevel = 120;
                job = net.sf.odinms.client.MapleJob.龙之传人_4;
            }
            
            if (reqlevel != 0){
                if(cm.getLevel()>=reqlevel)
                {
                    cm.changeJob(job);
                    cm.getPlayer().gainAp(5);
                    cm.sendOk("转职成功!加油锻炼,当你变的强大的时候记得来找我哦!");
                    cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！-【龙的传人】.");
                }else{
                    cm.sendOk("你未达到转职条件要求等级："+reqlevel);
                }
            }else {
                cm.sendOk("了不起，你已经完成了所有的转职！\r\n但是你可以 #r[转生]#k ,但需要管理员开启转生功能！");
            }
            cm.dispose();





        } else if (status == 64) {
            cm.changeJob(MapleJob.HERMIT);
            cm.sendOk("转职成功！加油锻炼，当你变的强大的时候记的来找我哦！");
            cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
            cm.dispose();
        } else if (status == 67) {
            cm.changeJob(MapleJob.CHIEFBANDIT);
            cm.sendOk("转职成功！加油锻炼，当你变的强大的时候记的来找我哦！");
            cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
            cm.dispose();
        } else if (status == 70) {
            cm.changeJob(MapleJob.RANGER);
            cm.sendOk("转职成功！加油锻炼，当你变的强大的时候记的来找我哦！");
            cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
            cm.dispose();
        } else if (status == 73) {
            cm.changeJob(MapleJob.SNIPER);
            cm.sendOk("转职成功！加油锻炼，当你变的强大的时候记的来找我哦！");
            cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
            cm.dispose();
        } else if (status == 76) {
            cm.changeJob(MapleJob.FP_MAGE);
            cm.sendOk("转职成功！加油锻炼，当你变的强大的时候记的来找我哦！");
            cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
            cm.dispose();
        } else if (status == 79) {
            cm.changeJob(MapleJob.IL_MAGE);
            cm.sendOk("转职成功！加油锻炼，当你变的强大的时候记的来找我哦！");
            cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
            cm.dispose();
        } else if (status == 82) {
            cm.changeJob(MapleJob.PRIEST);
            cm.sendOk("转职成功！加油锻炼，当你变的强大的时候记的来找我哦！");
            cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
            cm.dispose();
        } else if (status == 85) {
            cm.changeJob(MapleJob.CRUSADER);
            cm.sendOk("转职成功！加油锻炼，当你变的强大的时候记的来找我哦！");
            cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
            cm.dispose();
        } else if (status == 88) {
            cm.changeJob(MapleJob.WHITEKNIGHT);
            cm.sendOk("转职成功！加油锻炼，当你变的强大的时候记的来找我哦！");
            cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
            cm.dispose();
        } else if (status == 91) {
            cm.changeJob(MapleJob.DRAGONKNIGHT);
            cm.sendOk("转职成功！加油锻炼，当你变的强大的时候记的来找我哦！");
            cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
        } else if (status == 94) {
            cm.changeJob(MapleJob.MARAUDER);
            cm.sendOk("转职成功！加油锻炼，当你变的强大的时候记的来找我哦！");
            cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
        } else if (status == 97) {
            cm.changeJob(MapleJob.OUTLAW);
            cm.sendOk("转职成功！加油锻炼，当你变的强大的时候记的来找我哦！");
            cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
            cm.dispose();
        } else if (status == 99) {
            cm.sendOk("天气很好哦~~加油吧！再见！");
            cm.dispose();
        } else if (status == 102) {
            if (cm.getJob().equals(net.sf.odinms.client.MapleJob.HERMIT)) {
                status = 105;
                cm.sendYesNo("#d恭喜你达到了 #r[XXXX级]#k #d,你现在就要完成 #r[第XXXX次转职]#k 吗？");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.CHIEFBANDIT)) {
                status = 108;
                cm.sendYesNo("#d恭喜你达到了 #r[XXXX级]#k #d,你现在就要完成 #r[第XXXX次转职]#k 吗？");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.RANGER)) {
                status = 111;
                cm.sendYesNo("#d恭喜你达到了 #r[XXXX级]#k #d,你现在就要完成 #r[第XXXX次转职]#k 吗？");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.SNIPER)) {
                status = 114;
                cm.sendYesNo("#d恭喜你达到了 #r[XXXX级]#k #d,你现在就要完成 #r[第XXXX次转职]#k 吗？");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.FP_MAGE)) {
                status = 117;
                cm.sendYesNo("#d恭喜你达到了 #r[XXXX级]#k #d,你现在就要完成 #r[第XXXX次转职]#k 吗？");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.IL_MAGE)) {
                status = 120;
                cm.sendYesNo("#d恭喜你达到了 #r[XXXX级]#k #d,你现在就要完成 #r[第XXXX次转职]#k 吗？");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.PRIEST)) {
                status = 123;
                cm.sendYesNo("#d恭喜你达到了 #r[XXXX级]#k #d,你现在就要完成 #r[第XXXX次转职]#k 吗？");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.CRUSADER)) {
                status = 126;
                cm.sendYesNo("#d恭喜你达到了 #r[XXXX级]#k #d,你现在就要完成 #r[第XXXX次转职]#k 吗？");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.WHITEKNIGHT)) {
                status = 129;
                cm.sendYesNo("#d恭喜你达到了 #r[XXXX级]#k #d,你现在就要完成 #r[第XXXX次转职]#k 吗？");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.DRAGONKNIGHT)) {
                status = 132;
                cm.sendYesNo("#d恭喜你达到了 #r[XXXX级]#k #d,你现在就要完成 #r[第XXXX次转职]#k 吗？");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.MARAUDER)) {
                status = 135;
                cm.sendYesNo("#d恭喜你达到了 #r[XXXX级]#k #d,你现在就要完成 #r[第XXXX次转职]#k 吗？");
            } else if (cm.getJob().equals(net.sf.odinms.client.MapleJob.OUTLAW)) {
                status = 137;
                cm.sendYesNo("#d恭喜你达到了 #r[XXXX级]#k #d,你现在就要完成 #r[第XXXX次转职]#k 吗？");
            } else { 
                cm.dispose();
            }


        } else if (status == 106) {
            cm.changeJob(MapleJob.NIGHTLORD);
            cm.sendOk("转职成功！加油锻炼，当你变的强大的时候记的来找我哦！");
            cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
            cm.dispose();
        } else if (status == 109) {
            cm.changeJob(MapleJob.SHADOWER);
            cm.sendOk("转职成功！加油锻炼，当你变的强大的时候记的来找我哦！");
            cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
            cm.dispose();
        } else if (status == 112) {
            cm.changeJob(MapleJob.BOWMASTER);
            cm.sendOk("转职成功！加油锻炼，当你变的强大的时候记的来找我哦！");
            cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
            cm.dispose();
        } else if (status == 115) {
            cm.changeJob(MapleJob.CROSSBOWMASTER);
            cm.sendOk("转职成功！加油锻炼，当你变的强大的时候记的来找我哦！");
            cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
            cm.dispose();
        } else if (status == 118) {
            cm.changeJob(MapleJob.FP_ARCHMAGE);
            cm.sendOk("转职成功！加油锻炼，当你变的强大的时候记的来找我哦！");
            cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
            cm.dispose();
        } else if (status == 121) {
            cm.changeJob(MapleJob.IL_ARCHMAGE);
            cm.sendOk("转职成功！加油锻炼，当你变的强大的时候记的来找我哦！");
            cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
            cm.dispose();
        } else if (status == 124) {
            cm.changeJob(MapleJob.BISHOP);
            cm.sendOk("转职成功！加油锻炼，当你变的强大的时候记的来找我哦！");
            cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
            cm.dispose();
        } else if (status == 127) {
            cm.changeJob(MapleJob.HERO);
            cm.sendOk("转职成功！加油锻炼，当你变的强大的时候记的来找我哦！");
            cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
            cm.dispose();
        } else if (status == 130) {
            cm.changeJob(MapleJob.PALADIN);
            cm.sendOk("转职成功！加油锻炼，当你变的强大的时候记的来找我哦！");
            cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
            cm.dispose();
        } else if (status == 133) {
            cm.changeJob(MapleJob.DARKKNIGHT);
            cm.sendOk("转职成功！加油锻炼，当你变的强大的时候记的来找我哦！");
            cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
            cm.dispose();
        } else if (status == 136) {
            cm.changeJob(MapleJob.BUCCANEER);
            cm.sendOk("转职成功！加油锻炼，当你变的强大的时候记的来找我哦！");
            cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
            cm.dispose();
        } else if (status == 139) {
            cm.changeJob(MapleJob.CORSAIR);
            cm.sendOk("转职成功！加油锻炼，当你变的强大的时候记的来找我哦！");
            cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
            cm.dispose();
        } else if (status == 151) {
            if (cm.c.getPlayer().getInt() >= 4) {
                cm.teachSkill(2001002,15,15); //Magic Guard
                cm.teachSkill(2001003,15,15); //Magic Armor
                cm.teachSkill(2001004,20,20); //Energy Bolt
                cm.teachSkill(2001005,20,20); //Magic Claw
                cm.changeJob(net.sf.odinms.client.MapleJob.MAGICIAN);
                cm.sendOk("转职成功！希望你成为出色的 #b[魔法师]#k ！");
                cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
                cm.dispose();
            } else {
                cm.sendOk("你没有符合最小需求: #b[20 智力]#k ！");
                cm.dispose();
            }
            
        } else if (status == 154) {
            var text = "";
            for(var i = 0; i < 11; i++){
                text += "#fEffect/SetEff/35/effect/10#";
            }
            text +="#d#L0#"+ico11+"战士[英雄·圣骑士·黑骑士·魂骑士·战神]#l\r\n";
            text +="#d#L1#"+ico11+"魔法师[主教·火毒·冰雷·炎术师·龙神·唤灵斗师]#l\r\n";
            text +="#d#L2#"+ico11+"弓箭手[神射手·箭神·风灵使者·豹弩游侠]#l\r\n";
            text +="#d#L3#"+ico11+"飞侠[侠盗·隐士·夜行者·暗影双刀]#l\r\n";
            text +="#d#L4#"+ico11+"海盗[船长·冲锋队长·机械师]#l\r\n\r\n";
            for(var i = 0; i < 4; i++){
                for(var j = 3992008; j < 3992011; j++){
                    text += "#v" + j + "#";
                }
            }
            text +="#r\r\n  "+ico11+"最新职业：\r\n";
            if(cm.getJob().getId() == 2002 || cm.getJob().getId() == 3001){
                text +="#d#L5#"+ico11+"双弩精灵#l #L7#"+ico11+"恶魔猎手#l #L6#"+ico11+"神炮王#l #L8#"+ico11+"龙的传人#l";
            }else{
                text +="#d#L5#"+ico11+"双弩精灵#l #L7#"+ico11+"恶魔猎手#l #L6#"+ico11+"神炮王#l #L8#"+ico11+"龙的传人#l";
            }
            cm.sendSimple(text);
        } else if (status == 155) {
            if (selection == 0) {
                jobName = "战士";
                job = net.sf.odinms.client.MapleJob.WARRIOR;
            }
            if (selection == 1) {
                jobName = "魔法师";
                job = net.sf.odinms.client.MapleJob.MAGICIAN;
            }
            if (selection == 2) {
                jobName = "弓箭手";
                job = net.sf.odinms.client.MapleJob.BOWMAN;
            }
            if (selection == 3) {
                jobName = "飞侠";
                job = net.sf.odinms.client.MapleJob.THIEF;
            }
            if (selection == 4) {
                jobName = "海盗";
                job = net.sf.odinms.client.MapleJob.PIRATE;
            }
            if (selection == 5) {
                jobName = "双弩精灵";
                job = net.sf.odinms.client.MapleJob.DoubleCrossbows;
            }
            if (selection == 6) {
                jobName = "炮手";
                job = net.sf.odinms.client.MapleJob.GODGUNNER;
            }
            if (selection == 7) {
                jobName = "恶魔猎手";
                job = net.sf.odinms.client.MapleJob.DemonHunter;
            }
            if (selection == 8) {
                jobName = "龙的传人";
                job = net.sf.odinms.client.MapleJob.龙之传人_1;
                
            }
            cm.sendYesNo("不错的选择哦，确定要成为一名 #b[" + jobName + "] #k吗？"); 
        } else if (status == 156) {
            cm.changeJob(job);
            cm.sendOk("转职成功！加油锻炼，当你变的强大的时候记的来找我哦！");
            cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
            cm.dispose();
        } else if (status == 161) {
            if(cm.getJob().getId() == 1000 && cm.getLevel()>=10){
                cm.sendSimple("看起来你还是一个初心者,快选择一个适合自己的职业吧!#b\r\n#L0#魂骑士#l #L1#炎术士#l #L2#风灵使者#l #L3#夜行者#l #L4#奇袭者#l#k");
            }else if(parseInt(cm.getJob().getId() / 100) >10 && cm.getLevel()>=30 && cm.getJob().getId()%100 == 0){
                cm.sendYesNo("您真的确定要进行第二次转职了吗？");
            }else if(parseInt(cm.getJob().getId() / 100) >10 && cm.getLevel()>=70 && cm.getJob().getId()%10 == 0){
                cm.sendYesNo("您真的确定要进行第三次转职了吗？");
            }else{
                cm.sendOk("您目前的条件不能使用我的服务哦! 骑士团");
                cm.dispose();
            }
        } else if (status == 162) {
            if(cm.getJob().getId() == 1000 && cm.getLevel()>=10){
                if (selection == 0) {
                    job = net.sf.odinms.client.MapleJob.GHOST_KNIGHT;
                } else if (selection == 1) {
                    job = net.sf.odinms.client.MapleJob.FIRE_KNIGHT;
                } else if (selection == 2) {
                    job = net.sf.odinms.client.MapleJob.WIND_KNIGHT;
                } else if (selection == 3) {
                    job = net.sf.odinms.client.MapleJob.NIGHT_KNIGHT;
                } else if (selection == 4) {
                    job = net.sf.odinms.client.MapleJob.THIEF_KNIGHT;
                }
                cm.changeJob(job);
                cm.gainItem(1142066,1);
                cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职为骑士团职业！");
                cm.sendOk("转职成功！加油锻炼，当你变的强大的时候记的来找我哦！");
            } else if(parseInt(cm.getJob().getId() / 100) >10 && cm.getLevel()>=30 && cm.getJob().getId()%100 == 0){
                cm.changeJob(MapleJob.getById(cm.getJob().getId()+10));
                cm.gainItem(1142067,1);
                cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职为骑士团职业！");
                cm.sendOk("转职成功！加油锻炼，当你变的强大的时候记的来找我哦！");
            } else if(parseInt(cm.getJob().getId() / 100) >10 && cm.getLevel()>=70 && cm.getJob().getId()%10 == 0){
                cm.gainItem(1142068,1);
                cm.getPlayer().gainAp(5);
                cm.changeJob(MapleJob.getById(cm.getJob().getId()+1));
                cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职为骑士团职业！");
                cm.sendOk("转职成功！希望您以后的冒险之路顺利!");
            }
            cm.dispose();
        } else if (status == 164) {
            if(cm.getJob().getId() == 2000 && cm.getLevel() >=10){
                cm.sendYesNo("战神战起来！\r\n看起来你还是一个战童,您确定要进行第一次转职吗？");
            } else if(cm.getJob().getId() == 2100 && cm.getLevel() >=30) {
                cm.sendYesNo("战神战起来！您真的确定要进行第二次转职了吗？");
            } else if(cm.getJob().getId() == 2110 && cm.getLevel() >=70){
                cm.sendYesNo("战神战起来！您真的确定要进行第三次转职了吗？");
            } else if(cm.getJob().getId() == 2111 && cm.getLevel() >=120) {
                cm.sendYesNo("战神战起来！您真的确定要进行第四次转职了吗？");
            } else if(cm.getJob().getId() == 2112 && cm.getLevel() >120) {
                cm.sendOk("你已经完成了所有的转职工作。继续加油吧！！");
            } else {
                cm.sendOk("按照您目前的条件，我还不能为您服务哦！加油吧！");
                cm.dispose();
            }
        } else if (status == 165) {
            if(cm.getJob().getId() == 2000 && cm.getLevel() >=10){
                cm.teachSkill(21000000,10,10);
                cm.teachSkill(21001001,5,5);
                cm.teachSkill(21000002,20,20);
                cm.teachSkill(21001003,20,20);
                cm.teachSkill(21000004,10,10);
                cm.changeJob(MapleJob.Aran_1);
                cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职为战神职业！");
                cm.sendOk("转职成功！加油锻炼，当你变的强大的时候记的来找我哦！");
            } else if(cm.getJob().getId() == 2100 && cm.getLevel() >=30){
                cm.changeJob(MapleJob.Aran_2);
                cm.gainItem(1142130,1);
                cm.gainItem(1442078,1);
                cm.teachSkill(21100000,20,20);
                cm.teachSkill(21100001,20,20);
                cm.teachSkill(21101003,10,10);
                cm.teachSkill(21100002,20,20);
                cm.teachSkill(21100005,20,20);
                cm.teachSkill(21101006,20,20);
                cm.teachSkill(21100007,20,20);
                cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职为战神职业！");
                cm.sendOk("转职成功！加油锻炼，当你变的强大的时候记的来找我哦！");
            } else if(cm.getJob().getId() == 2110 && cm.getLevel() >=70){
                cm.gainItem(1142131,1);
                cm.getPlayer().gainAp(5);
                cm.changeJob(MapleJob.Aran_3);
                cm.teachSkill(21110000,20,20);
                cm.teachSkill(21111001,20,20);
                cm.teachSkill(21110002,20,20);
                cm.teachSkill(21110003,20,20);
                cm.teachSkill(21110006,20,20);
                cm.teachSkill(21110007,20,20);
                cm.teachSkill(21110008,20,20);
                cm.teachSkill(21111009,20,20);
                cm.teachSkill(21110010,20,20);
                cm.teachSkill(21110011,20,20);
                cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职为战神职业！");
                cm.sendOk("转职成功！加油锻炼，当你变的强大的时候记的来找我哦！");
            } else if(cm.getJob().getId() == 2111 && cm.getLevel() >=120){
                cm.gainItem(1142132,1);
                cm.getPlayer().gainAp(5);
                cm.teachSkill(21121000,30,30);
                cm.teachSkill(21120001,30,30);
                cm.teachSkill(21120002,30,30);
                cm.teachSkill(21121003,30,30);
                cm.teachSkill(21120004,30,30);
                cm.teachSkill(21120005,30,30);
                cm.teachSkill(21120006,30,30);
                cm.teachSkill(21120007,30,30);
                cm.teachSkill(21121008,5,5);
                cm.teachSkill(21120009,30,30);
                cm.teachSkill(21120010,30,30);
                cm.teachSkill(21120011,10,10);
                cm.changeJob(MapleJob.Aran_4);
                cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职为战神职业！");
                cm.sendOk("转职成功！希望您以后的冒险之路顺利！");
            }
            cm.dispose();
        //反抗者开始的部分
        }else if (status == 201){
            if(cm.getJob().getId() == 3000 && cm.getLevel()>=10){
                cm.sendSimple("怎么样~~在下面选择一种你所喜欢的职业吧！#b\r\n#L0#战斗法师#l  #L1#豹弩游侠#l  #L2#机械师#l#k");
            }else if(parseInt(cm.getJob().getId() / 100) >10 && cm.getLevel()>=30 && cm.getJob().getId()%100 == 0){
                status = 211;//获取反抗者第二次转职
                cm.sendYesNo("您真的确定要进行第二次转职了吗？");
            }else if(parseInt(cm.getJob().getId() / 100) >10 && cm.getLevel()>=70 && cm.getJob().getId()%10 == 0){
                status = 221;//获取反抗者第三次转职
                cm.sendYesNo("您真的确定要进行第三次转职了吗？");
            }else if(parseInt(cm.getJob().getId() / 100) >10 && cm.getLevel()>=120 && cm.getJob().getId()%10 == 1){
                status = 231;//获取反抗者第三次转职
                cm.sendYesNo("您真的确定要进行第四次转职了吗？");
            }else{
                cm.sendOk("您目前的条件不能使用我的服务哦! 反抗者");
                cm.dispose();
            }
        }else if(status == 202){
            if (selection == 0) {
                jobName = "战斗法师";
                job = net.sf.odinms.client.MapleJob.Battlemage_1;
            }
            if (selection == 1) {
                jobName = "豹弩游侠";
                job = net.sf.odinms.client.MapleJob.wildhunter_1;
            }
            if (selection == 2) {
                jobName = "机械师";
                job = net.sf.odinms.client.MapleJob.mechinic_1;
            }
            cm.sendYesNo("不错的选择哦，确定要成为一名 #b[" + jobName + "] #k吗？"); 
        } else if (status == 203) {
            cm.changeJob(job);
            cm.getPlayer().gainAp(5);
            cm.sendOk("转职成功！加油锻炼，当你变的强大的时候记的来找我哦！");
            cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
            cm.dispose();
        }else if(status == 212){
            cm.changeJob(MapleJob.getById(cm.getJob().getId()+10));
            cm.getPlayer().gainAp(5);
            cm.sendOk("转职成功！加油锻炼，当你变的强大的时候记的来找我哦！");
            cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
            cm.dispose();
        }else if(status == 222){
            cm.changeJob(MapleJob.getById(cm.getJob().getId()+1));
            cm.getPlayer().gainAp(5);
            cm.sendOk("转职成功！加油锻炼，当你变的强大的时候记的来找我哦！");
            cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
            cm.dispose();
        }else if(status == 232){
            cm.changeJob(MapleJob.getById(cm.getJob().getId()+1));
            cm.getPlayer().gainAp(5);
            cm.sendOk("转职成功！加油锻炼，当你变的强大的时候记的来找我哦！");
            cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
            cm.dispose();
        }else if(status == 301){
            if(cm.getJob().getId() == 400 && cm.getLevel() >= 20){//见习双刀
                cm.changeJob(net.sf.odinms.client.MapleJob.Dual_Blade_1);
                cm.getPlayer().gainAp(5);
                cm.sendOk("转职成功!加油锻炼,当你变的强大的时候记得来找我哦!下一次转职为[30]级!");
                cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
                cm.dispose();
            }else if(cm.getJob().getId() == 430 && cm.getLevel() >= 30 && cm.getJob().getId()%100 == 30){//双刀客
                cm.changeJob(MapleJob.getById(cm.getJob().getId()+1));
                cm.getPlayer().gainAp(5);
                cm.sendOk("转职成功!加油锻炼,当你变的强大的时候记得来找我哦!下一次转职为[55]级!");
                cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
                cm.dispose();
            }else if(cm.getJob().getId() == 431 && cm.getLevel() >= 55 && cm.getJob().getId()%100 == 31){//双刀侠
                cm.changeJob(MapleJob.getById(cm.getJob().getId()+1));
                cm.getPlayer().gainAp(5);
                cm.sendOk("转职成功!加油锻炼,当你变的强大的时候记得来找我哦!下一次转职为[70]级!");
                cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
                cm.dispose();
            }else if(cm.getJob().getId() == 432 && cm.getLevel() >= 70 && cm.getJob().getId()%100 == 32){//血刀
                cm.changeJob(MapleJob.getById(cm.getJob().getId()+1));
                cm.getPlayer().gainAp(5);
                cm.sendOk("转职成功!加油锻炼,当你变的强大的时候记得来找我哦!下一次转职为[120]级!");
                cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
                cm.dispose();
            }else if(cm.getJob().getId() == 433 && cm.getLevel() >= 120 && cm.getJob().getId()%100 == 33){//幻影双刀
                cm.changeJob(MapleJob.getById(cm.getJob().getId()+1));
                cm.getPlayer().gainAp(5);
                cm.sendOk("转职成功！你的职业生涯已经完成了!!");
                cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
                cm.dispose();
            }else{
                cm.sendOk("你未达到转职条件!!"+cm.getJob().getId()%100);
                cm.dispose();
            }

        }else if(status == 302){
            var reqlevel = 0;
            if (cm.getJob().getId() == 2002){
                reqlevel = 10;
                job = net.sf.odinms.client.MapleJob.DoubleCrossbows;
            }
            if(cm.getJob().getId() == 2300){
                reqlevel = 30;
                job = net.sf.odinms.client.MapleJob.DoubleCrossbows_2;
            }
            if(cm.getJob().getId() == 2310){
                reqlevel = 70;
                job = net.sf.odinms.client.MapleJob.DoubleCrossbows_3;
            }
            if(cm.getJob().getId() == 2311){
                reqlevel = 120;
                job = net.sf.odinms.client.MapleJob.DoubleCrossbows_4;
            }
            if (reqlevel != 0){
                if(cm.getLevel()>=reqlevel)
                {
                    cm.changeJob(job);
                    cm.getPlayer().gainAp(5);
                    cm.sendOk("转职成功!加油锻炼,当你变的强大的时候记得来找我哦!");
                    cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
                }else{
                    cm.sendOk("你未达到转职条件要求等级："+reqlevel);
                }
            }else {
                cm.sendOk("了不起，你已经完成了所有的转职！\r\n但是你可以 #r[转生]#k ,但需要管理员开启转生功能！");
            }
            cm.dispose();
        }else if(status == 303){
            var reqlevel = 0;
            if(cm.getJob().getId() == 501){
                reqlevel = 30;
                job = net.sf.odinms.client.MapleJob.GODGUNNER_2;
            }
            if(cm.getJob().getId() == 530){
                reqlevel = 70;
                job = net.sf.odinms.client.MapleJob.GODGUNNER_3;
            }
            if(cm.getJob().getId() == 531){
                reqlevel = 120;
                job = net.sf.odinms.client.MapleJob.GODGUNNER_4;
            }
            if (reqlevel != 0){
                if(cm.getLevel()>=reqlevel)
                {
                    cm.changeJob(job);
                    cm.getPlayer().gainAp(5);
                    cm.sendOk("转职成功!加油锻炼,当你变的强大的时候记得来找我哦!");
                    cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
                }else{
                    cm.sendOk("你未达到转职条件要求等级："+reqlevel);
                }
            }else {
                cm.sendOk("了不起，你已经完成了所有的转职！\r\n但是你可以 #r[转生]#k ,但需要管理员开启转生功能！");
            }
            cm.dispose();
            cm.dispose();
        }else if(status == 304){
            var reqlevel = 0;
            if (cm.getJob().getId() == 3001){
                reqlevel = 10;
                job = net.sf.odinms.client.MapleJob.DemonHunter;
            }
            if(cm.getJob().getId() == 3100){
                reqlevel = 30;
                job = net.sf.odinms.client.MapleJob.DemonHunter_2;
            }
            if(cm.getJob().getId() == 3110){
                reqlevel = 70;
                job = net.sf.odinms.client.MapleJob.DemonHunter_3;
            }
            if(cm.getJob().getId() == 3111){
                reqlevel = 120;
                job = net.sf.odinms.client.MapleJob.DemonHunter_4;
            }
            if (reqlevel != 0){
                if(cm.getLevel()>=reqlevel)
                {
                    cm.changeJob(job);
                    cm.getPlayer().gainAp(5);
                    cm.sendOk("转职成功!加油锻炼,当你变的强大的时候记得来找我哦!");
                    cm.serverNotice("[转职系统]: 恭喜 [" + cm.getPlayer() + "] 在NPC：菇菇博士 快速转职成功！");
                }else{
                    cm.sendOk("你未达到转职条件要求等级："+reqlevel);
                }
            }else {
                cm.sendOk("了不起，你已经完成了所有的转职！\r\n但是你可以 #r[转生]#k ,但需要管理员开启转生功能！");
            }
            cm.dispose();
        } else {
            cm.dispose();
        }  

    }
}