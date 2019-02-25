/**
	NPC Name: 		Han the Broker
	Map(s): 		Magatia
	Description: 	Quest - 蒙特鸠协会会长的考试
*/

var status = -1;
var oreArray;

function start(mode, type, selection) {
}

function end(mode, type, selection) {
    if (mode == -1) {
	qm.dispose();
    } else {
	oreArray = getOreArray();
	if (status == -1) {
	    if (oreArray.length > 0) {
		status++;
		qm.sendSimple("噢，看起来你想做一个交易，你想加入蒙特鸠吗？嗯……。我不理解你为什么要加入到蒙特鸠，你要给我什么回报？\r\n" + getOreString(oreArray));
	    } else {
		qm.sendOk("这个是什么东西？你没有感觉矿石和你在一起的感觉吗？没有矿石，你就不能进入到蒙特鸠。");
		qm.dispose();
	    }
	} else if (status == 0) {
	    qm.gainItem(oreArray[selection], -2); // Take 2 ores
	    qm.sendNext("请等一段时间，我去买一些东西来帮助你考试。");
	    qm.forceCompleteQuest();
	    qm.dispose();
	} else {
	    qm.dispose();
	}
    }
}

function getOreArray() {
    var ores = new Array();
    var y = 0;
    for (var x = 4020000; x <= 4020008; x++) {
	if (qm.haveItem(x, 2)) {
	    ores[y] = x;
	    y++;
	}
    }
    return ores;
}

function getOreString(ids) { // Parameter 'ids' is just the array of getOreArray()
    var thestring = "#b";
    var extra;
    for (x = 0; x < ids.length; x++) {
	extra = "#L" + x + "##t" + ids[x] + "##l\r\n";
	thestring += extra;
    }
    thestring += "#k";
    return thestring;
}
