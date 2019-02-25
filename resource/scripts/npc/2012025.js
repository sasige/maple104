/*
	NPC Name: 		Geras
	Map(s): 		Orbis: Station<To Ariant> (200000151)
	Description: 		Orbis Ticketing Usher
*/
var status = 0;

function start() {
    status = -1;
    geenie = cm.getEventManager("Geenie");
    action(1, 0, 0);
}

function action(mode, type, selection) {
    status++;
    if(mode == 0) {
	cm.sendNext("你还有什么事情再这里没有完成吗？");
	cm.dispose();
	return;
    }
    if (status == 0) {
	if(geenie == null) {
	    cm.sendNext("脚本发生错误，请联系管理员解决。");
	    cm.dispose();
	} else if (geenie.getProperty("entry").equals("true")) {
	    cm.sendYesNo("上去之后，需要飞很久才能到达目的地。如果你在这里有急事要办的话，请先把事情办完，怎么样？");
	} else if(geenie.getProperty("entry").equals("false") && geenie.getProperty("docked").equals("true")) {
	    cm.sendNext("本次航班已经出发，请等待下一次航班。.");
	    cm.dispose();
	} else {
	    cm.sendNext("出发前5分钟开始才可以入场。请稍等一下。不过也别来的太晚。出发前1分钟就会结束出航准备。");
	    cm.dispose();
	}
    } else if(status == 1) {
	cm.warp(200000152, 0);
	cm.dispose();
    }
}