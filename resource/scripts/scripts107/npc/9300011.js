var status = 0;
var statuss = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (status == 1 && mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        status--;
        cm.dispose();
    }
    if (status == 0) {
        cm.sendSimple("#r107版本已修改转职等级,除龙神,骑士团之外其他职业转职3转等级调整为60级,4转等级调整为100级#k.\r\n你确定要转职?请选择你梦想的职业：\r\n#b#L100#狂龙战士转职(1~4转)#l	#b#L110#爆莉萌天使转职(1~4转)#l\r\n#b#L13#冒险家转职(1~4转)#l	#b#L12#骑士团转职(1~4转)#l\r\n#b#L0#火炮手转职(1~4转)#l	#b#L1#暗影双刀转职(1~5转)#l\r\n#b#L2#双弩转职(1~4转)#l	#b#L3#恶魔转职(1~4转)#l\r\n#b#L4#米哈尔转职(1~4转)#l	#b#L5#幻影神偷转职(1~4转)#l\r\n#r#L6#夜光法师转职(1~4转)#l	#b#L7#幻灵斗师转职(1~4转)#l\r\n#b#L8#弩豹游侠转职(1~4转)#l	#b#L9#机械师转职(1~4转)#l\r\n#b#L10#战神转职(1~4转)#l	#b#L11#龙神转职(1~10转)#l");
    } else if (status == 1) {
        switch (selection) {
        case 0:
            if (cm.getJob() == 0 && cm.getPlayer().getLevel() >= 10) {
                cm.getPlayer().changeJob(501);
                cm.sendOk("系统已经为您转职为火炮手1转.");
            } else if (cm.getJob() == 501 && cm.getPlayer().getLevel() >= 30) {
                cm.getPlayer().changeJob(530);
                cm.sendOk("系统已经为您转职为火炮手2转.");
            } else if (cm.getJob() == 530 && cm.getPlayer().getLevel() >= 60) {
                cm.getPlayer().changeJob(531);
                cm.sendOk("系统已经为您转职为火炮手3转.");
            } else if (cm.getJob() == 531 && cm.getPlayer().getLevel() >= 100) {
                cm.getPlayer().changeJob(532);
                cm.sendOk("系统已经为您转职为火炮手4转.");
            } else {
                cm.sendOk("你的当前职业条件不满足,无法为你提供转职服务!");
            }
            cm.dispose();
            break;
        case 1:
            if (cm.getJob() == 400 && cm.getPlayer().getLevel() >= 20) {
                cm.getPlayer().changeJob(430);
                cm.sendOk("系统已经为您转职为暗影双刀1转.");
            } else if (cm.getJob() == 430 && cm.getPlayer().getLevel() >= 30) {
                cm.getPlayer().changeJob(431);
                cm.sendOk("系统已经为您转职为暗影双刀2转.");
            } else if (cm.getJob() == 431 && cm.getPlayer().getLevel() >= 55) {
                cm.getPlayer().changeJob(432);
                cm.sendOk("系统已经为您转职为暗影双刀3转.");
            } else if (cm.getJob() == 432 && cm.getPlayer().getLevel() >= 60) {
                cm.getPlayer().changeJob(433);
                cm.sendOk("系统已经为您转职为暗影双刀4转.");
            } else if (cm.getJob() == 433 && cm.getPlayer().getLevel() >= 100) {
                cm.getPlayer().changeJob(434);
                cm.sendOk("系统已经为您转职为暗影双刀5转.");
            } else {
                cm.sendOk("你不是飞侠(一转)职业 或你的等级没有达到20.(双刀一转请转飞侠)");
            }
            cm.dispose();
            break;
        case 2:
            if ((cm.getJob() == 0 || cm.getJob() == 2002) && cm.getPlayer().getLevel() >= 10) {
                cm.getPlayer().changeJob(2300);
                cm.sendOk("系统已经为您转职为双弩精灵1转.");
            } else if (cm.getJob() == 2300 && cm.getPlayer().getLevel() >= 30) {
                cm.getPlayer().changeJob(2310);
                cm.sendOk("系统已经为您转职为双弩精灵2转.");
            } else if (cm.getJob() == 2310 && cm.getPlayer().getLevel() >= 60) {
                cm.getPlayer().changeJob(2311);
                cm.sendOk("系统已经为您转职为双弩精灵3转.");
            } else if (cm.getJob() == 2311 && cm.getPlayer().getLevel() >= 100) {
                cm.getPlayer().changeJob(2312);
                cm.sendOk("系统已经为您转职为双弩精灵4转.");
            } else {
                cm.sendOk("你的当前职业条件不满足,无法为你提供转职服务!");
            }
            cm.dispose();
            break;
        case 3:
            if ((cm.getJob() == 0 || cm.getJob() == 3001) && cm.getPlayer().getLevel() >= 10) {
                cm.getPlayer().changeJob(3100);
                cm.sendOk("系统已经为您转职为恶魔猎手1转.");
            } else if (cm.getJob() == 3100 && cm.getPlayer().getLevel() >= 30) {
                cm.getPlayer().changeJob(3110);
                cm.sendOk("系统已经为您转职为恶魔猎手2转.");
            } else if (cm.getJob() == 3110 && cm.getPlayer().getLevel() >= 60) {
                cm.getPlayer().changeJob(3111);
                cm.sendOk("系统已经为您转职为恶魔猎手3转.");
            } else if (cm.getJob() == 3111 && cm.getPlayer().getLevel() >= 100) {
                cm.getPlayer().changeJob(3112);
                cm.sendOk("系统已经为您转职为恶魔猎手4转.");
            } else {
                cm.sendOk("你的当前职业条件不满足,无法为你提供转职服务!");
            }
            cm.dispose();
            break;
        case 4:
            if ((cm.getJob() == 0 || cm.getJob() == 5000) && cm.getPlayer().getLevel() >= 10) {
                cm.getPlayer().changeJob(5100);
				equip(1098000);
                cm.sendOk("系统已经为您转职为米哈尔1转.");
            } else if (cm.getJob() == 5100 && cm.getPlayer().getLevel() >= 30) {
                cm.getPlayer().changeJob(5110);
				equip(1098001);
                cm.sendOk("系统已经为您转职为米哈尔2转.");
            } else if (cm.getJob() == 5110 && cm.getPlayer().getLevel() >= 60) {
                cm.getPlayer().changeJob(5111);
				equip(1098002);
                cm.sendOk("系统已经为您转职为米哈尔3转.");
            } else if (cm.getJob() == 5111 && cm.getPlayer().getLevel() >= 100) {
                cm.getPlayer().changeJob(5112);
				equip(1098003);
                cm.sendOk("系统已经为您转职为米哈尔4转.");
            } else {
                cm.sendOk("你的当前职业条件不满足,无法为你提供转职服务!");
            }
            cm.dispose();
            break;
        case 5:
            if ((cm.getJob() == 0 || cm.getJob() == 2003) && cm.getPlayer().getLevel() >= 10) {
                cm.getPlayer().changeJob(2400);
                cm.sendOk("系统已经为您转职为幻影神偷1转.");
            } else if (cm.getJob() == 2400 && cm.getPlayer().getLevel() >= 30) {
                cm.getPlayer().changeJob(2410);
                cm.sendOk("系统已经为您转职为幻影神偷2转.");
            } else if (cm.getJob() == 2410 && cm.getPlayer().getLevel() >= 60) {
                cm.getPlayer().changeJob(2411);
                cm.sendOk("系统已经为您转职为幻影神偷3转.");
            } else if (cm.getJob() == 2411 && cm.getPlayer().getLevel() >= 100) {
                cm.getPlayer().changeJob(2412);
                cm.sendOk("系统已经为您转职为幻影神偷4转.");
            } else {
                cm.sendOk("你的当前职业条件不满足,无法为你提供转职服务!");
            }
            cm.dispose();
            break;
        case 6:
            if ((cm.getJob() == 0 || cm.getJob() == 2004) && cm.getPlayer().getLevel() >= 10) {
                cm.getPlayer().changeJob(2700);
                cm.sendOk("系统已经为您转职为夜光法师1转.");
            } else if (cm.getJob() == 2700 && cm.getPlayer().getLevel() >= 30) {
                cm.getPlayer().changeJob(2710);
                cm.sendOk("系统已经为您转职为夜光法师2转.");
            } else if (cm.getJob() == 2710 && cm.getPlayer().getLevel() >= 60) {
                cm.getPlayer().changeJob(2711);
                cm.sendOk("系统已经为您转职为夜光法师3转.");
            } else if (cm.getJob() == 2711 && cm.getPlayer().getLevel() >= 100) {
                cm.getPlayer().changeJob(2712);
                cm.sendOk("系统已经为您转职为夜光法师4转.");
            } else {
                cm.sendOk("你的当前职业条件不满足,无法为你提供转职服务!");
            }
            cm.dispose();
            break;
        case 7:
            if ((cm.getJob() == 0 || cm.getJob() == 3000) && cm.getPlayer().getLevel() >= 10) {
                cm.getPlayer().changeJob(3200);
                cm.sendOk("系统已经为您转职为幻灵斗师1转.");
            } else if (cm.getJob() == 3200 && cm.getPlayer().getLevel() >= 30) {
                cm.getPlayer().changeJob(3210);
                cm.sendOk("系统已经为您转职为幻灵斗师2转.");
            } else if (cm.getJob() == 3210 && cm.getPlayer().getLevel() >= 60) {
                cm.getPlayer().changeJob(3211);
                cm.sendOk("系统已经为您转职为幻灵斗师3转.");
            } else if (cm.getJob() == 3211 && cm.getPlayer().getLevel() >= 100) {
                cm.getPlayer().changeJob(3212);
                cm.sendOk("系统已经为您转职为幻灵斗师4转.");
            } else {
                cm.sendOk("你的当前职业条件不满足,无法为你提供转职服务!");
            }
            cm.dispose();
            break;
        case 8:
            if ((cm.getJob() == 0 || cm.getJob() == 3000) && cm.getPlayer().getLevel() >= 10) {
                cm.getPlayer().changeJob(3300);
                cm.teachSkill(80001000, 1, 1);
                cm.sendOk("系统已经为您转职为弩豹游侠1转.");
            } else if (cm.getJob() == 3300 && cm.getPlayer().getLevel() >= 30) {
                cm.getPlayer().changeJob(3310);
                cm.sendOk("系统已经为您转职为弩豹游侠2转.");
            } else if (cm.getJob() == 3310 && cm.getPlayer().getLevel() >= 60) {
                cm.getPlayer().changeJob(3311);
                cm.sendOk("系统已经为您转职为弩豹游侠3转.");
            } else if (cm.getJob() == 3311 && cm.getPlayer().getLevel() >= 100) {
                cm.getPlayer().changeJob(3312);
                cm.sendOk("系统已经为您转职为弩豹游侠4转.");
            } else {
                cm.sendOk("你的当前职业条件不满足,无法为你提供转职服务!");
            }
            cm.dispose();
            break;
        case 9:
            if ((cm.getJob() == 0 || cm.getJob() == 3000) && cm.getPlayer().getLevel() >= 10) {
                cm.getPlayer().changeJob(3500);
                cm.sendOk("系统已经为您转职为机械师1转.");
            } else if (cm.getJob() == 3500 && cm.getPlayer().getLevel() >= 30) {
                cm.getPlayer().changeJob(3510);
                cm.sendOk("系统已经为您转职为机械师2转.");
            } else if (cm.getJob() == 3510 && cm.getPlayer().getLevel() >= 60) {
                cm.getPlayer().changeJob(3511);
                cm.sendOk("系统已经为您转职为机械师3转.");
            } else if (cm.getJob() == 3511 && cm.getPlayer().getLevel() >= 100) {
                cm.getPlayer().changeJob(3512);
                cm.sendOk("系统已经为您转职为机械师4转.");
            } else {
                cm.sendOk("你的当前职业条件不满足,无法为你提供转职服务!");
            }
            cm.dispose();
            break;
        case 10:
            if ((cm.getJob() == 0 || cm.getJob() == 2000) && cm.getPlayer().getLevel() >= 10) {
                cm.getPlayer().changeJob(2100);
                cm.sendOk("系统已经为您转职为战神1转.");
            } else if (cm.getJob() == 2100 && cm.getPlayer().getLevel() >= 30) {
                cm.getPlayer().changeJob(2110);
                cm.sendOk("系统已经为您转职为战神2转.");
            } else if (cm.getJob() == 2110 && cm.getPlayer().getLevel() >= 60) {
                cm.getPlayer().changeJob(2111);
                cm.sendOk("系统已经为您转职为战神3转.");
            } else if (cm.getJob() == 2111 && cm.getPlayer().getLevel() >= 100) {
                cm.getPlayer().changeJob(2112);
                cm.sendOk("系统已经为您转职为战神4转.");
            } else {
                cm.sendOk("你的当前职业条件不满足,无法为你提供转职服务!");
            }
            cm.dispose();
            break;
        case 11:
            if ((cm.getJob() == 0 || cm.getJob() == 2001) && cm.getPlayer().getLevel() >= 10) {
                cm.getPlayer().changeJob(2200);
                cm.sendOk("系统已经为您转职为龙神1转.");
            } else if (cm.getJob() == 2200 && cm.getPlayer().getLevel() >= 20) {
                cm.getPlayer().changeJob(2210);
                cm.sendOk("系统已经为您转职为龙神2转.");
            } else if (cm.getJob() == 2210 && cm.getPlayer().getLevel() >= 30) {
                cm.getPlayer().changeJob(2211);
                cm.sendOk("系统已经为您转职为龙神3转.");
            } else if (cm.getJob() == 2211 && cm.getPlayer().getLevel() >= 40) {
                cm.getPlayer().changeJob(2212);
                cm.sendOk("系统已经为您转职为龙神4转.");
            } else if (cm.getJob() == 2212 && cm.getPlayer().getLevel() >= 60) {
                cm.getPlayer().changeJob(2213);
                cm.sendOk("系统已经为您转职为龙神5转.");
            } else if (cm.getJob() == 2213 && cm.getPlayer().getLevel() >= 80) {
                cm.getPlayer().changeJob(2214);
                cm.sendOk("系统已经为您转职为龙神6转.");
            } else if (cm.getJob() == 2214 && cm.getPlayer().getLevel() >= 100) {
                cm.getPlayer().changeJob(2215);
                cm.sendOk("系统已经为您转职为龙神7转.");
            } else if (cm.getJob() == 2215 && cm.getPlayer().getLevel() >= 120) {
                cm.getPlayer().changeJob(2216);
                cm.sendOk("系统已经为您转职为龙神8转.");
            } else if (cm.getJob() == 2216 && cm.getPlayer().getLevel() >= 140) {
                cm.getPlayer().changeJob(2217);
                cm.sendOk("系统已经为您转职为龙神9转.");
            } else if (cm.getJob() == 2217 && cm.getPlayer().getLevel() >= 160) {
                cm.getPlayer().changeJob(2218);
                cm.sendOk("系统已经为您转职为龙神10转.");
            } else {
                cm.sendOk("你的当前职业条件不满足,无法为你提供转职服务!");
            }
            cm.dispose();
            break;
        case 12:
            cm.sendSimple("骑士团职业转职等级分别为(10,30,70),你需要转职成?\r\n#b#L13#魂骑士转职(1~4转)#l\r\n#b#L14#炎术士转职(1~3转)#l\r\n#b#L15#风灵使者转职(1~3转)#l\r\n#b#L16#夜行者转职(1~3转)#l\r\n#b#L17#奇袭者转职(1~3转)#l");
            break;
        case 13:
            cm.sendSimple("冒险家职业转职等级分别为(10,30,60,100),你需要转职成?\r\n#b#L18#战士转职(1~4转)#l\r\n#b#L19#魔法师转职(1~4转)#l\r\n#b#L20#弓箭手转职(1~4转)#l\r\n#b#L21#飞侠转职(1~4转)#l\r\n#b#L22#海盗转职(1~4转)#l");
            break;
        case 100:
            if ((cm.getJob() == 0 || cm.getJob() == 6000) && cm.getPlayer().getLevel() >= 10) {
                cm.getPlayer().changeJob(6100);
				equip(1352500);
                cm.sendOk("系统已经为您转职为狂龙战士1转.");
            } else if (cm.getJob() == 6100 && cm.getPlayer().getLevel() >= 30) {
                cm.getPlayer().changeJob(6110);
				equip(1352501);
                cm.sendOk("系统已经为您转职为狂龙战士转.");
            } else if (cm.getJob() == 6110 && cm.getPlayer().getLevel() >= 60) {
                cm.getPlayer().changeJob(6111);
				equip(1352502);
                cm.sendOk("系统已经为您转职为狂龙战士3转.");
            } else if (cm.getJob() == 6111 && cm.getPlayer().getLevel() >= 100) {
                cm.getPlayer().changeJob(6112);
				equip(1352503);
                cm.sendOk("系统已经为您转职为狂龙战士4转.");
            } else {
                cm.sendOk("你的当前职业条件不满足,无法为你提供转职服务!");
            }
            cm.dispose();
            break;
        case 110:
            if ((cm.getJob() == 0 || cm.getJob() == 6001) && cm.getPlayer().getLevel() >= 10) {
                cm.getPlayer().changeJob(6500);
				equip(1352601);
                cm.sendOk("系统已经为您转职为爆莉萌天使1转.");
            } else if (cm.getJob() == 6500 && cm.getPlayer().getLevel() >= 30) {
                cm.getPlayer().changeJob(6510);
				equip(1352602);
                cm.sendOk("系统已经为您转职为爆莉萌天使转.");
            } else if (cm.getJob() == 6510 && cm.getPlayer().getLevel() >= 60) {
                cm.getPlayer().changeJob(6511);
				equip(1352603);
                cm.sendOk("系统已经为您转职为爆莉萌天使3转.");
            } else if (cm.getJob() == 6511 && cm.getPlayer().getLevel() >= 100) {
                cm.getPlayer().changeJob(6512);
				equip(1352604);
                cm.sendOk("系统已经为您转职为爆莉萌天使4转.");
            } else {
                cm.sendOk("你的当前职业条件不满足,无法为你提供转职服务!");
            }
            cm.dispose();
            break;
        }
    } else if (status == 2) {
        if (selection == 13) {
            changeJobByKnights(1);
        } else if (selection == 14) {
            changeJobByKnights(2);
        } else if (selection == 15) {
            changeJobByKnights(3);
        } else if (selection == 16) {
            changeJobByKnights(4);
        } else if (selection == 17) {
            changeJobByKnights(5);
        } else if (selection == 18) {
            if (cm.getJob() == 0 && cm.getPlayer().getLevel() >= 10) {
                cm.getPlayer().changeJob(100);
                cm.sendOk("系统已经为您转职为战士1转.");
                cm.dispose();
            } else if (cm.getJob() >= 100 && cm.getPlayer().getLevel() >= 30) {
                cm.sendSimple("#b#L23#剑客转职(2~4转)#l\r\n#b#L24#准骑士转职(2~4转)#l\r\n#b#L25#枪战士转职(2~4转)#l\r\n");
            } else {
                cm.sendOk("你的当前职业条件不满足,无法为你提供转职服务!");
                cm.dispose();
            }
        } else if (selection == 19) {
            if (cm.getJob() == 0 && cm.getPlayer().getLevel() >= 8) {
                cm.getPlayer().changeJob(200);
                cm.sendOk("系统已经为您转职为魔法师1转.");
                cm.dispose();
            } else if (cm.getJob() >= 200 && cm.getPlayer().getLevel() >= 30) {
                cm.sendSimple("#b#L26#火毒法师转职(2~4转)#l\r\n#b#L27#冰雷法师转职(2~4转)#l\r\n#b#L28#牧师法师转职(2~4转)#l\r\n");
            } else {
                cm.sendOk("你的当前职业条件不满足,无法为你提供转职服务!");
                cm.dispose();
            }
        } else if (selection == 20) {
            if (cm.getJob() == 0 && cm.getPlayer().getLevel() >= 10) {
                cm.getPlayer().changeJob(300);
                cm.sendOk("系统已经为您转职为弓箭手1转.");
                cm.dispose();
            } else if (cm.getJob() >= 300 && cm.getPlayer().getLevel() >= 30) {
                cm.sendSimple("#b#L29#猎人转职(2~4转)#l\r\n#b#L30#游侠转职(2~4转)#l");
            } else {
                cm.sendOk("你的当前职业条件不满足,无法为你提供转职服务!");
                cm.dispose();
            }
        } else if (selection == 21) {
            if (cm.getJob() == 0 && cm.getPlayer().getLevel() >= 10) {
                cm.getPlayer().changeJob(400);
                cm.sendOk("系统已经为您转职为飞侠1转.");
                cm.dispose();
            } else if (cm.getJob() >= 400 && cm.getPlayer().getLevel() >= 30) {
                cm.sendSimple("#b#L31#刺客转职(2~4转)#l\r\n#b#L32#侠客转职(2~4转)#l");
            } else {
                cm.sendOk("你的当前职业条件不满足,无法为你提供转职服务!");
                cm.dispose();
            }
        } else if (selection == 22) {
            if (cm.getJob() == 0 && cm.getPlayer().getLevel() >= 10) {
                cm.getPlayer().changeJob(500);
                cm.sendOk("系统已经为您转职为海盗1转.");
                cm.dispose();
            } else if (cm.getJob() >= 500 && cm.getPlayer().getLevel() >= 30) {
                cm.sendSimple("#b#L33#拳手转职(2~4转)#l\r\n#b#L34#火枪手转职(2~4转)#l");
            } else {
                cm.sendOk("你的当前职业条件不满足,无法为你提供转职服务!");
                cm.dispose();
            }
        }
    } else if (status == 3) {
        if (selection == 23) {
            changeJobByAdventurer(1);
        } else if (selection == 24) {
            changeJobByAdventurer(2);
        } else if (selection == 25) {
            changeJobByAdventurer(3);
        } else if (selection == 26) {
            changeJobByAdventurer(4);
        } else if (selection == 27) {
            changeJobByAdventurer(5);
        } else if (selection == 28) {
            changeJobByAdventurer(6);
        } else if (selection == 29) {
            changeJobByAdventurer(7);
        } else if (selection == 30) {
            changeJobByAdventurer(8);
        } else if (selection == 31) {
            changeJobByAdventurer(9);
        } else if (selection == 32) {
            changeJobByAdventurer(10);
        } else if (selection == 33) {
            changeJobByAdventurer(11);
        } else if (selection == 34) {
            changeJobByAdventurer(12);
        }
    }
}

/*
冒险家转职
*/

function changeJobByAdventurer(type) {
    if (type == 1) {
        if (cm.getJob() == 100 && cm.getPlayer().getLevel() >= 30) {
            cm.getPlayer().changeJob(110);
            cm.sendOk("系统已经为您转职为剑客(战士2转).");
        } else if (cm.getJob() == 110 && cm.getPlayer().getLevel() >= 60) {
            cm.getPlayer().changeJob(111);
            cm.sendOk("系统已经为您转职为勇士(战士3转).");
        } else if (cm.getJob() == 111 && cm.getPlayer().getLevel() >= 100) {
            cm.getPlayer().changeJob(112);
            cm.sendOk("系统已经为您转职为英雄(战士4转).");
        } else {
            cm.sendOk("你的当前职业条件不满足,无法为你提供转职服务!");
        }
    } else if (type == 2) {
        if (cm.getJob() == 100 && cm.getPlayer().getLevel() >= 30) {
            cm.getPlayer().changeJob(120);
            cm.sendOk("系统已经为您转职为准骑士(战士2转).");
        } else if (cm.getJob() == 120 && cm.getPlayer().getLevel() >= 60) {
            cm.getPlayer().changeJob(121);
            cm.sendOk("系统已经为您转职为骑士(战士3转).");
        } else if (cm.getJob() == 121 && cm.getPlayer().getLevel() >= 100) {
            cm.getPlayer().changeJob(122);
            cm.sendOk("系统已经为您转职为圣骑士(战士4转).");
        } else {
            cm.sendOk("你的当前职业条件不满足,无法为你提供转职服务!");
        }
    } else if (type == 3) {
        if (cm.getJob() == 100 && cm.getPlayer().getLevel() >= 30) {
            cm.getPlayer().changeJob(130);
            cm.sendOk("系统已经为您转职为枪战士(战士2转).");
        } else if (cm.getJob() == 130 && cm.getPlayer().getLevel() >= 60) {
            cm.getPlayer().changeJob(131);
            cm.sendOk("系统已经为您转职为龙骑士(战士3转).");
        } else if (cm.getJob() == 131 && cm.getPlayer().getLevel() >= 100) {
            cm.getPlayer().changeJob(132);
            cm.sendOk("系统已经为您转职为黑骑士(战士4转).");
        } else {
            cm.sendOk("你的当前职业条件不满足,无法为你提供转职服务!");
        }
    } else if (type == 4) {
        if (cm.getJob() == 200 && cm.getPlayer().getLevel() >= 30) {
            cm.getPlayer().changeJob(210);
            cm.sendOk("系统已经为您转职为火毒法师(魔法师2转).");
        } else if (cm.getJob() == 210 && cm.getPlayer().getLevel() >= 60) {
            cm.getPlayer().changeJob(211);
            cm.sendOk("系统已经为您转职为火毒巫师(魔法师3转).");
        } else if (cm.getJob() == 211 && cm.getPlayer().getLevel() >= 100) {
            cm.getPlayer().changeJob(212);
            cm.sendOk("系统已经为您转职为火毒魔导师(魔法师4转).");
        } else {
            cm.sendOk("你的当前职业条件不满足,无法为你提供转职服务!");
        }
    } else if (type == 5) {
        if (cm.getJob() == 200 && cm.getPlayer().getLevel() >= 30) {
            cm.getPlayer().changeJob(220);
            cm.sendOk("系统已经为您转职为冰雷法师(魔法师2转).");
        } else if (cm.getJob() == 220 && cm.getPlayer().getLevel() >= 60) {
            cm.getPlayer().changeJob(221);
            cm.sendOk("系统已经为您转职为冰雷巫师(魔法师3转).");
        } else if (cm.getJob() == 221 && cm.getPlayer().getLevel() >= 100) {
            cm.getPlayer().changeJob(222);
            cm.sendOk("系统已经为您转职为冰雷魔导师(魔法师4转).");
        } else {
            cm.sendOk("你的当前职业条件不满足,无法为你提供转职服务!");
        }
    } else if (type == 6) {
        if (cm.getJob() == 200 && cm.getPlayer().getLevel() >= 30) {
            cm.getPlayer().changeJob(230);
            cm.sendOk("系统已经为您转职为牧师(魔法师2转).");
        } else if (cm.getJob() == 230 && cm.getPlayer().getLevel() >= 60) {
            cm.getPlayer().changeJob(231);
            cm.sendOk("系统已经为您转职为祭祀(魔法师3转).");
        } else if (cm.getJob() == 231 && cm.getPlayer().getLevel() >= 100) {
            cm.getPlayer().changeJob(232);
            cm.sendOk("系统已经为您转职为主教(魔法师4转).");
        } else {
            cm.sendOk("你的当前职业条件不满足,无法为你提供转职服务!");
        }
    } else if (type == 7) {
        if (cm.getJob() == 300 && cm.getPlayer().getLevel() >= 30) {
            cm.getPlayer().changeJob(310);
            cm.sendOk("系统已经为您转职为猎人(弓箭手2转).");
        } else if (cm.getJob() == 310 && cm.getPlayer().getLevel() >= 60) {
            cm.getPlayer().changeJob(311);
            cm.sendOk("系统已经为您转职为射手(弓箭手3转).");
        } else if (cm.getJob() == 311 && cm.getPlayer().getLevel() >= 100) {
            cm.getPlayer().changeJob(312);
            cm.sendOk("系统已经为您转职为神射手(弓箭手4转).");
        } else {
            cm.sendOk("你的当前职业条件不满足,无法为你提供转职服务!");
        }
    } else if (type == 8) {
        if (cm.getJob() == 300 && cm.getPlayer().getLevel() >= 30) {
            cm.getPlayer().changeJob(320);
            cm.sendOk("系统已经为您转职为弩弓手(弓箭手2转).");
        } else if (cm.getJob() == 320 && cm.getPlayer().getLevel() >= 60) {
            cm.getPlayer().changeJob(321);
            cm.sendOk("系统已经为您转职为游侠(弓箭手3转).");
        } else if (cm.getJob() == 321 && cm.getPlayer().getLevel() >= 100) {
            cm.getPlayer().changeJob(322);
            cm.sendOk("系统已经为您转职为箭神(弓箭手4转).");
        } else {
            cm.sendOk("你的当前职业条件不满足,无法为你提供转职服务!");
        }
    } else if (type == 9) {
        if (cm.getJob() == 400 && cm.getPlayer().getLevel() >= 30) {
            cm.getPlayer().changeJob(410);
            cm.sendOk("系统已经为您转职为刺客(飞侠2转).");
        } else if (cm.getJob() == 410 && cm.getPlayer().getLevel() >= 60) {
            cm.getPlayer().changeJob(411);
            cm.sendOk("系统已经为您转职为无影人(飞侠3转).");
        } else if (cm.getJob() == 411 && cm.getPlayer().getLevel() >= 100) {
            cm.getPlayer().changeJob(412);
            cm.sendOk("系统已经为您转职为隐士(飞侠4转).");
        } else {
            cm.sendOk("你的当前职业条件不满足,无法为你提供转职服务!");
        }
    } else if (type == 10) {
        if (cm.getJob() == 400 && cm.getPlayer().getLevel() >= 30) {
            cm.getPlayer().changeJob(420);
            cm.sendOk("系统已经为您转职为侠客(飞侠2转).");
        } else if (cm.getJob() == 420 && cm.getPlayer().getLevel() >= 60) {
            cm.getPlayer().changeJob(421);
            cm.sendOk("系统已经为您转职为独行客(飞侠3转).");
        } else if (cm.getJob() == 421 && cm.getPlayer().getLevel() >= 100) {
            cm.getPlayer().changeJob(422);
            cm.sendOk("系统已经为您转职为侠盗(飞侠4转).");
        } else {
            cm.sendOk("你的当前职业条件不满足,无法为你提供转职服务!");
        }
    } else if (type == 11) {
        if (cm.getJob() == 500 && cm.getPlayer().getLevel() >= 30) {
            cm.getPlayer().changeJob(510);
            cm.sendOk("系统已经为您转职为拳手(海盗2转).");
        } else if (cm.getJob() == 510 && cm.getPlayer().getLevel() >= 60) {
            cm.getPlayer().changeJob(511);
            cm.sendOk("系统已经为您转职为斗士(海盗3转).");
        } else if (cm.getJob() == 511 && cm.getPlayer().getLevel() >= 100) {
            cm.getPlayer().changeJob(512);
            cm.sendOk("系统已经为您转职为冲锋队长(海盗4转).");
        } else {
            cm.sendOk("你的当前职业条件不满足,无法为你提供转职服务!");
        }
    } else {
        if (cm.getJob() == 500 && cm.getPlayer().getLevel() >= 30) {
            cm.getPlayer().changeJob(520);
            cm.sendOk("系统已经为您转职为火枪手(海盗2转).");
        } else if (cm.getJob() == 520 && cm.getPlayer().getLevel() >= 60) {
            cm.getPlayer().changeJob(521);
            cm.sendOk("系统已经为您转职为大副(海盗3转).");
        } else if (cm.getJob() == 521 && cm.getPlayer().getLevel() >= 100) {
            cm.getPlayer().changeJob(522);
            cm.sendOk("系统已经为您转职为船长(海盗4转).");
        } else {
            cm.sendOk("你的当前职业条件不满足,无法为你提供转职服务!");
        }
    }
    cm.dispose();
}

/*
骑士团转职
*/

function changeJobByKnights(type) {
    if (type == 1) {
        if ((cm.getJob() == 0 || cm.getJob() == 1000) && cm.getPlayer().getLevel() >= 10) {
            cm.getPlayer().changeJob(1100);
            cm.sendOk("系统已经为您转职为魂骑士1转.");
        } else if (cm.getJob() == 1100 && cm.getPlayer().getLevel() >= 30) {
            cm.getPlayer().changeJob(1110);
            cm.sendOk("系统已经为您转职为魂骑士2转.");
        } else if (cm.getJob() == 1110 && cm.getPlayer().getLevel() >= 70) {
            cm.getPlayer().changeJob(1111);
            cm.sendOk("系统已经为您转职为魂骑士3转.");
        } else {
            cm.sendOk("你的当前职业条件不满足,无法为你提供转职服务!");
        }
    } else if (type == 2) {
        if ((cm.getJob() == 0 || cm.getJob() == 1000) && cm.getPlayer().getLevel() >= 10) {
            cm.getPlayer().changeJob(1200);
            cm.sendOk("系统已经为您转职为炎术士1转.");
        } else if (cm.getJob() == 1200 && cm.getPlayer().getLevel() >= 30) {
            cm.getPlayer().changeJob(1210);
            cm.sendOk("系统已经为您转职为炎术士2转.");
        } else if (cm.getJob() == 1210 && cm.getPlayer().getLevel() >= 70) {
            cm.getPlayer().changeJob(1211);
            cm.sendOk("系统已经为您转职为炎术士3转.");
        } else {
            cm.sendOk("你的当前职业条件不满足,无法为你提供转职服务!");
        }
    } else if (type == 2) {
        if ((cm.getJob() == 0 || cm.getJob() == 1000) && cm.getPlayer().getLevel() >= 10) {
            cm.getPlayer().changeJob(1300);
            cm.sendOk("系统已经为您转职为风灵使者1转.");
        } else if (cm.getJob() == 1300 && cm.getPlayer().getLevel() >= 30) {
            cm.getPlayer().changeJob(1310);
            cm.sendOk("系统已经为您转职为风灵使者2转.");
        } else if (cm.getJob() == 1310 && cm.getPlayer().getLevel() >= 70) {
            cm.getPlayer().changeJob(1311);
            cm.sendOk("系统已经为您转职为风灵使者3转.");
        } else {
            cm.sendOk("你的当前职业条件不满足,无法为你提供转职服务!");
        }
    } else if (type == 2) {
        if ((cm.getJob() == 0 || cm.getJob() == 1000) && cm.getPlayer().getLevel() >= 10) {
            cm.getPlayer().changeJob(1400);
            cm.sendOk("系统已经为您转职为夜行者1转.");
        } else if (cm.getJob() == 1400 && cm.getPlayer().getLevel() >= 30) {
            cm.getPlayer().changeJob(1410);
            cm.sendOk("系统已经为您转职为夜行者2转.");
        } else if (cm.getJob() == 1410 && cm.getPlayer().getLevel() >= 70) {
            cm.getPlayer().changeJob(1411);
            cm.sendOk("系统已经为您转职为夜行者3转.");
        } else {
            cm.sendOk("你的当前职业条件不满足,无法为你提供转职服务!");
        }
    } else {
        if ((cm.getJob() == 0 || cm.getJob() == 1000) && cm.getPlayer().getLevel() >= 10) {
            cm.getPlayer().changeJob(1500);
            cm.sendOk("系统已经为您转职为奇袭者1转.");
        } else if (cm.getJob() == 1500 && cm.getPlayer().getLevel() >= 30) {
            cm.getPlayer().changeJob(1510);
            cm.sendOk("系统已经为您转职为奇袭者2转.");
        } else if (cm.getJob() == 1510 && cm.getPlayer().getLevel() >= 70) {
            cm.getPlayer().changeJob(1511);
            cm.sendOk("系统已经为您转职为奇袭者3转.");
        } else {
            cm.sendOk("你的当前职业条件不满足,无法为你提供转职服务!");
        }
    }
    cm.dispose();
}

function equip(itemId){
	var item = cm.getPlayer().getInventory(Packages.client.inventory.MapleInventoryType.EQUIP).findById(itemId);
	if(item==null){
		cm.gainItem(itemId, 1);
	}
	//这里再查找一次 防止第一次查找的时候为null
	var item2 = cm.getPlayer().getInventory(Packages.client.inventory.MapleInventoryType.EQUIP).findById(itemId);
	//var item2 = cm.getPlayer().getInventory(Packages.client.inventory.MapleInventoryType.EQUIPPED).getItem(-10);
	//查找玩家背包有没有这个物品,没有就给玩家
	if(item2 !=null){
		var pos = item2.getPosition();
		Packages.server.MapleInventoryManipulator.equip(cm.getC(),pos,-10);
	}
	//如果已经装备了副手武器或者背包没有满的情况下 取下副手装备到背包
	//if(item2 !=null && cm.getPlayer().getInventory(Packages.client.inventory.MapleInventoryType.EQUIP).isFull() == false){
		//Packages.server.MapleInventoryManipulator.unequip(cm.getC(),-10,cm.getPlayer().getInventory(Packages.client.inventory.MapleInventoryType.EQUIP).getNextFreeSlot());
	//}
	
}