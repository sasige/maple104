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
		if(cm.getChar().getVip() == 1){
		text = "#d尊贵的#b#h ##k,#d你目前会员等级为#rVIP1#k\r\n";
		text += "你每天可以重置副本次数为1次,你当前使用重置次数为：" + cm.getBossLog("会员重置") +"\r\n";
		text += "#r提示：每个级别的VIP享受重置的次数是不一样的哟#k\r\n";
		text += "#L0##d[VIP1]#k消除副本次数#l\r\n"; 
		cm.sendSimple(text);
		}else if(cm.getChar().getVip() == 2){
		text = "#d尊贵的#b#h ##k,#d你目前会员等级为#rVIP2#k\r\n";
		text += "你每天可以重置副本次数为2次,你当前使用重置次数为：" + cm.getBossLog("会员重置") +"\r\n";
		text += "#r提示：每个级别的VIP享受重置的次数是不一样的哟#k\r\n";
		text += "#L1##d[VIP2]#k消除副本次数#l\r\n"; 
		cm.sendSimple(text);
		}else if(cm.getChar().getVip() == 3){
		text = "#d尊贵的#b#h ##k,#d你目前会员等级为#rVIP3#k\r\n";
		text += "你每天可以重置副本次数为3次,你当前使用重置次数为：" + cm.getBossLog("会员重置") +"\r\n";
		text += "#r提示：每个级别的VIP享受重置的次数是不一样的哟#k\r\n";
		text += "#L2##d[VIP3]#k消除副本次数#l\r\n"; 
		cm.sendSimple(text);
		}else if(cm.getChar().getVip() == 4){
		text = "#d尊贵的#b#h ##k,#d你目前会员等级为#rVIP4#k\r\n";
		text += "你每天可以重置副本次数为4次,你当前使用重置次数为：" + cm.getBossLog("会员重置") +"\r\n";
		text += "#r提示：每个级别的VIP享受重置的次数是不一样的哟#k\r\n";
		text += "#L3##d[VIP4]#k消除副本次数#l\r\n"; 
		cm.sendSimple(text);
		}else if(cm.getChar().getVip() == 5){
		text = "#d尊贵的#b#h ##k,#d你目前会员等级为#rVIP5#k\r\n";
		text += "你每天可以重置副本次数为5次,你当前使用重置次数为：" + cm.getBossLog("会员重置") +"\r\n";
		text += "#r提示：每个级别的VIP享受重置的次数是不一样的哟#k\r\n";
		text += "#L4##d[VIP5]#k消除副本次数#l\r\n"; 
		cm.sendSimple(text);
		}else{
		cm.sendOk("你还不是会员");
		cm.dispose();
			}
    } else if (status == 1) {
      switch (selection) {
        case 0:
           if (cm.getBossLog("会员重置") > 0) {
                    cm.sendOk("温馨提示：#b\r\n副本重置失败，没有副本重置次数");
		    cm.dispose();
                }else if (cm.getBossLog("普通扎昆") == 0) {
                    cm.sendOk("温馨提示：#b\r\n副本重置失败，普通黑龙还有剩余次数");
                    cm.dispose();
                }else if (cm.getBossLog("进阶扎昆") == 0) {
                    cm.sendOk("温馨提示：#b\r\n副本重置失败，进阶扎昆还有剩余次数");
                    cm.dispose();
                }else if (cm.getBossLog("进阶黑龙") == 0) {
                    cm.sendOk("温馨提示：#b\r\n副本重置失败，进阶黑龙还有剩余次数");
                    cm.dispose();
                }else if (cm.getBossLog("狮子王") == 0) {
                    cm.sendOk("温馨提示：#b\r\n副本重置失败，狮子王还有剩余次数");
                    cm.dispose();
                }else if (cm.getBossLog("品克缤") == 0) {
                    cm.sendOk("温馨提示：#b\r\n副本重置失败，品克缤还有剩余次数");
                    cm.dispose();
                } else {
                    cm.resetBossLog("普通扎昆");
                    cm.resetBossLog("进阶黑龙");
                    cm.resetBossLog("进阶扎昆");
                    cm.resetBossLog("狮子王");
                    cm.resetBossLog("品克缤");
                    cm.setBossLog("会员重置");
		    cm.worldMessage(cm.getChar().getName() + "玩家使用VIP特权重置了所有副本,今日使用次数" + cm.getBossLog("会员重置"));
                    cm.sendOk("温馨提示：#b\r\n副本重置成功，勇士行动起来吧！");
                    cm.dispose();
                }
            	    break;
        case 1:
           if (cm.getBossLog("会员重置") > 1) {
                    cm.sendOk("温馨提示：#b\r\n副本重置失败，没有副本重置次数");
		    cm.dispose();
                }else if (cm.getBossLog("普通扎昆") == 0) {
                    cm.sendOk("温馨提示：#b\r\n副本重置失败，普通扎昆还有剩余次数");
                    cm.dispose();
                }else if (cm.getBossLog("进阶扎昆") == 0) {
                    cm.sendOk("温馨提示：#b\r\n副本重置失败，进阶扎昆还有剩余次数");
                    cm.dispose();
                }else if (cm.getBossLog("进阶黑龙") == 0) {
                    cm.sendOk("温馨提示：#b\r\n副本重置失败，进阶黑龙还有剩余次数");
                    cm.dispose();
                }else if (cm.getBossLog("狮子王") == 0) {
                    cm.sendOk("温馨提示：#b\r\n副本重置失败，狮子王还有剩余次数");
                    cm.dispose();
                }else if (cm.getBossLog("品克缤") == 0) {
                    cm.sendOk("温馨提示：#b\r\n副本重置失败，品克缤还有剩余次数");
                    cm.dispose();
                } else {
                    cm.resetBossLog("普通扎昆");
                    cm.resetBossLog("进阶黑龙");
                    cm.resetBossLog("进阶扎昆");
                    cm.resetBossLog("狮子王");
                    cm.resetBossLog("品克缤");
                    cm.setBossLog("会员重置");
		    cm.worldMessage(cm.getChar().getName() + "玩家使用VIP特权重置了所有副本,今日使用次数" + cm.getBossLog("会员重置"));
                    cm.sendOk("温馨提示：#b\r\n副本重置成功，勇士行动起来吧！");
                    cm.dispose();
                }
            	    break;
        case 2:
           if (cm.getBossLog("会员重置") > 2) {
                    cm.sendOk("温馨提示：#b\r\n副本重置失败，没有副本重置次数");
		    cm.dispose();
                }else if (cm.getBossLog("普通扎昆") == 0) {
                    cm.sendOk("温馨提示：#b\r\n副本重置失败，普通扎昆还有剩余次数");
                    cm.dispose();
                }else if (cm.getBossLog("进阶扎昆") == 0) {
                    cm.sendOk("温馨提示：#b\r\n副本重置失败，进阶扎昆还有剩余次数");
                    cm.dispose();
                }else if (cm.getBossLog("进阶黑龙") == 0) {
                    cm.sendOk("温馨提示：#b\r\n副本重置失败，进阶黑龙还有剩余次数");
                    cm.dispose();
                }else if (cm.getBossLog("狮子王") == 0) {
                    cm.sendOk("温馨提示：#b\r\n副本重置失败，狮子王还有剩余次数");
                    cm.dispose();
                }else if (cm.getBossLog("品克缤") == 0) {
                    cm.sendOk("温馨提示：#b\r\n副本重置失败，品克缤还有剩余次数");
                    cm.dispose();
                } else {
                    cm.resetBossLog("普通扎昆");
                    cm.resetBossLog("进阶黑龙");
                    cm.resetBossLog("进阶扎昆");
                    cm.resetBossLog("狮子王");
                    cm.resetBossLog("品克缤");
                    cm.setBossLog("会员重置");
		    cm.worldMessage(cm.getChar().getName() + "玩家使用VIP特权重置了所有副本,今日使用次数" + cm.getBossLog("会员重置"));
                    cm.sendOk("温馨提示：#b\r\n副本重置成功，勇士行动起来吧！");
                    cm.dispose();
                }
            	    break;
        case 3:
           if (cm.getBossLog("会员重置") > 3) {
                    cm.sendOk("温馨提示：#b\r\n副本重置失败，没有副本重置次数");
		    cm.dispose();
                }else if (cm.getBossLog("普通扎昆") == 0) {
                    cm.sendOk("温馨提示：#b\r\n副本重置失败，普通扎昆还有剩余次数");
                    cm.dispose();
                }else if (cm.getBossLog("进阶扎昆") == 0) {
                    cm.sendOk("温馨提示：#b\r\n副本重置失败，进阶扎昆还有剩余次数");
                    cm.dispose();
                }else if (cm.getBossLog("进阶黑龙") == 0) {
                    cm.sendOk("温馨提示：#b\r\n副本重置失败，进阶黑龙还有剩余次数");
                    cm.dispose();
                }else if (cm.getBossLog("狮子王") == 0) {
                    cm.sendOk("温馨提示：#b\r\n副本重置失败，狮子王还有剩余次数");
                    cm.dispose();
                }else if (cm.getBossLog("品克缤") == 0) {
                    cm.sendOk("温馨提示：#b\r\n副本重置失败，品克缤还有剩余次数");
                    cm.dispose();
                } else {
                    cm.resetBossLog("普通扎昆");
                    cm.resetBossLog("进阶黑龙");
                    cm.resetBossLog("进阶扎昆");
                    cm.resetBossLog("狮子王");
                    cm.resetBossLog("品克缤");
                    cm.setBossLog("会员重置");
		    cm.worldMessage(cm.getChar().getName() + "玩家使用VIP特权重置了所有副本,今日使用次数" + cm.getBossLog("会员重置"));
                    cm.sendOk("温馨提示：#b\r\n副本重置成功，勇士行动起来吧！");
                    cm.dispose();
                }
            	    break;
        case 4:
           if (cm.getBossLog("会员重置") > 4) {
                    cm.sendOk("温馨提示：#b\r\n副本重置失败，没有副本重置次数");
		    cm.dispose();
                }else if (cm.getBossLog("普通扎昆") == 0) {
                    cm.sendOk("温馨提示：#b\r\n副本重置失败，普通扎昆还有剩余次数");
                    cm.dispose();
                }else if (cm.getBossLog("进阶扎昆") == 0) {
                    cm.sendOk("温馨提示：#b\r\n副本重置失败，进阶扎昆还有剩余次数");
                    cm.dispose();
                }else if (cm.getBossLog("进阶黑龙") == 0) {
                    cm.sendOk("温馨提示：#b\r\n副本重置失败，进阶黑龙还有剩余次数");
                    cm.dispose();
                }else if (cm.getBossLog("狮子王") == 0) {
                    cm.sendOk("温馨提示：#b\r\n副本重置失败，狮子王还有剩余次数");
                    cm.dispose();
                }else if (cm.getBossLog("品克缤") == 0) {
                    cm.sendOk("温馨提示：#b\r\n副本重置失败，品克缤还有剩余次数");
                    cm.dispose();
                } else {
                    cm.resetBossLog("普通扎昆");
                    cm.resetBossLog("进阶黑龙");
                    cm.resetBossLog("进阶扎昆");
                    cm.resetBossLog("狮子王");
                    cm.resetBossLog("品克缤");
                    cm.setBossLog("会员重置");
		    cm.worldMessage(cm.getChar().getName() + "玩家使用VIP特权重置了所有副本,今日使用次数" + cm.getBossLog("会员重置"));
                    cm.sendOk("温馨提示：#b\r\n副本重置成功，勇士行动起来吧！");
                    cm.dispose();
                }
            	    break;
        case 5:
           if (cm.getBossLog("会员重置") > 5) {
                    cm.sendOk("温馨提示：#b\r\n副本重置失败，没有副本重置次数");
		    cm.dispose();
                }else if (cm.getBossLog("普通扎昆") == 0) {
                    cm.sendOk("温馨提示：#b\r\n副本重置失败，普通扎昆还有剩余次数");
                    cm.dispose();
                }else if (cm.getBossLog("进阶扎昆") == 0) {
                    cm.sendOk("温馨提示：#b\r\n副本重置失败，进阶扎昆还有剩余次数");
                    cm.dispose();
                }else if (cm.getBossLog("进阶黑龙") == 0) {
                    cm.sendOk("温馨提示：#b\r\n副本重置失败，进阶黑龙还有剩余次数");
                    cm.dispose();
                }else if (cm.getBossLog("狮子王") == 0) {
                    cm.sendOk("温馨提示：#b\r\n副本重置失败，狮子王还有剩余次数");
                    cm.dispose();
                }else if (cm.getBossLog("品克缤") == 0) {
                    cm.sendOk("温馨提示：#b\r\n副本重置失败，品克缤还有剩余次数");
                    cm.dispose();
                } else {
                    cm.resetBossLog("普通扎昆");
                    cm.resetBossLog("进阶黑龙");
                    cm.resetBossLog("进阶扎昆");
                    cm.resetBossLog("狮子王");
                    cm.resetBossLog("品克缤");
                    cm.setBossLog("会员重置");
		    cm.worldMessage(cm.getChar().getName() + "玩家使用VIP特权重置了所有副本,今日使用次数" + cm.getBossLog("会员重置"));
                    cm.sendOk("温馨提示：#b\r\n副本重置成功，勇士行动起来吧！");
                    cm.dispose();
                }
            	    break;
        case 6:
           if (cm.getPlayer().getCSPoints(1)>=30000 && cm.getBossLog("希纳斯") >= 1) {
		    cm.gainNX(-30000);
                    cm.resetBossLog("希纳斯");
                    cm.sendOk("温馨提示：#b\r\n副本重置成功，勇士行动起来吧！");
		    cm.dispose();
                } else {
                    cm.sendOk("温馨提示：#b\r\n副本重置失败，点卷剩余不足(3W)或你还剩余次数.");
                    cm.dispose();
                }
            	    break;
        case 7:
	if( cm.haveItem(4000243,1) && cm.haveItem(4000235,1) && (cm.getBossLog("普通扎昆") > 1 || cm.getBossLog("普通黑龙") > 1 || cm.getBossLog("品克缤") > 1)){
                    cm.resetBossLog("普通扎昆");
                    cm.resetBossLog("普通黑龙");
                    cm.resetBossLog("品克缤");
		    cm.gainItem(4000235,-1);
		    cm.gainItem(4000243,-1);
	    cm.sendOk("重置成功.祝你游戏愉快!");
	    cm.dispose();
} else {
	    cm.sendOk("你没有带来格瑞芬多角-天鹰的角与喷火龙的尾巴\r\n或你的挑战次数还没有使用完哦");
	    cm.dispose();
}
            break;
        }
    }
}