var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    if (cm.getPlayer().getMapId() == 925100700) {
	cm.removeAll(4001117);
	cm.removeAll(4001120);
	cm.removeAll(4001121);
	cm.removeAll(4001122);
	cm.warp(251010404,0);
	cm.dispose();
	return;
    }
    var em = cm.getEventManager("Pirate");
    if (em == null) {
	cm.sendNext("The event isn't started...");
	cm.dispose();
	return;
    }
    if (!cm.isLeader()) {
	cm.sendNext("请队长来和我交谈");
	cm.dispose();
	return;
    }
    switch(cm.getPlayer().getMapId()) {
	case 925100000:
	   cm.sendNext("我们走的是海盗船！好，我们要消灭所有怪物守护它\r\n#r如果要退出副本,请下线重新上#k");
	   cm.dispose();
	   break;
	case 925100100:
	   var emp = em.getProperty("stage2");
	   if (emp == null) {
		em.setProperty("stage2", "0");
		emp = "0";
	   }
	   if (emp.equals("0")) {
		if (cm.haveItem(4001120,20)) {
		    cm.sendNext("不错都搜集到了,下面搜集下一阶段的证明吧");
		    cm.gainItem(4001120,-20);
		    em.setProperty("stage2", "1");
		} else {
	   	    cm.sendNext("我们走的是海盗船！得到的，我们必须使自己的高贵海盗\r\n请搜集#v4001120##z4001120#20个交给我");
		}
	   } else if (emp.equals("1")) {
		if (cm.haveItem(4001121,20)) {
		    cm.sendNext("不错都搜集到了,下面搜集下一阶段的证明吧");
		    cm.gainItem(4001121,-20);
		    em.setProperty("stage2", "2");
		} else {
	   	    cm.sendNext("我们走的是海盗船！得到的，我们必须使自己的高贵海盗\r\n请搜集#v4001122##z4001121#20个交给我");
		}
	   } else if (emp.equals("2")) {
		if (cm.haveItem(4001122,20)) {
		    cm.sendNext("不错，让我们进入下一阶段吧");
		    cm.gainItem(4001122,-20);
		    em.setProperty("stage2", "3");
		} else {
	   	    cm.sendNext("我们走的是海盗船！得到的，我们必须使自己的高贵海盗\r\n请搜集#v4001122##z4001122#20个交给我");
		}
	   } else {
		cm.sendNext("下一阶段门已经打开，快去吧");
	   }
	   cm.dispose();
	   break;
	case 925100200:
	   cm.sendNext("消灭地图上所有的怪,下一阶段的门会自动打开");
	   cm.dispose();
	   break;
	case 925100201:
	   if (cm.getMap().getAllMonstersThreadsafe().size() == 0) {
		cm.sendNext("Excellent.");
		if (em.getProperty("stage2a").equals("0")) {
		    cm.getMap().setReactorState();
		    em.setProperty("stage2a", "1");
		}
	   } else {
	   	cm.sendNext("这些风铃草在躲藏。我们一定要解放他们");
	   }
	   cm.dispose();
	   break;
	case 925100301:
	   if (cm.getMap().getAllMonstersThreadsafe().size() == 0) {
		cm.sendNext("Excellent.");
		if (em.getProperty("stage3a").equals("0")) {
		    cm.getMap().setReactorState();
		    em.setProperty("stage3a", "1");
		}
	   } else {
	   	cm.sendNext("这些风铃草在躲藏。我们一定要解放他们");
	   }
	   cm.dispose();
	   break;
	case 925100202:
	case 925100302:
	   cm.sendNext("这些是船长和克鲁人，将他们一生都奉献给主的海盗。杀了他们，正如你所看到的");
	   cm.dispose();
	   break;
	case 925100400:
	   cm.sendNext("这是该船的动力来源。我们必须密封采用旧金属键关上门");
	   cm.dispose();
	   break;
	case 925100500:
	   if (cm.getMap().getAllMonstersThreadsafe().size() == 0) {
		cm.warpParty(925100600);
	   } else {
	   	cm.sendNext("打败所有的怪物！即使主海盗的奴才");
	   }
	   cm.dispose();
	   break;
    }
}