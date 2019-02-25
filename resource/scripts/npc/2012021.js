/* 
	NPC Name: 		Ramini
	Map(s): 		Orbis: Cabin<To Leafre> (200000131)
	Description: 		Orbis Ticketing Usher
*/
var status = 0;

function start() {
    status = -1;
    flight = cm.getEventManager("Flight");
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
	if(flight == null) {
	    cm.sendNext("The boats are currently down.");
	    cm.dispose();
	} else if(flight.getProperty("entry").equals("true")) {
	    cm.sendYesNo("非常好，船上还有足够的位置，请准备好登船，我们将进入漫长的旅行，你是不是想登船？");
	} else if(flight.getProperty("entry").equals("false") && flight.getProperty("docked").equals("true")) {
	    cm.sendNext("本次航班已经出发，请耐心等待下一次航班。");
	    cm.dispose();
	} else {
	    cm.sendNext("出发前1分钟就停止让客人登船了，请注意站台时间。不过也不要来的太晚了！");
	    cm.dispose();
	}
    } else if(status == 1) {
	cm.warp(200000132, 0);
	cm.dispose();
    }
}