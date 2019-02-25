/* Natalie
	Henesys VIP Hair/Hair Color Change.
*/
var status = -1;
var beauty = 0;
var hair_Colo_new;

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 0) {
	cm.dispose();
	return;
    } else {
	status++;
    }

    if (status == 0) {
	cm.sendSimple("你好，#h0#！我是#p"+cm.getNpc()+"#。 如果你拥有 #b#t5150052##k 或者 #b#t5151036##k 就可以护理头发了。 请选择你想要的#b\r\n#L0#发型  #i5150052##t5150052##l\r\n#L1#染发 #i5151036##t5151036##l");
    } else if (status == 1) {
	if (selection == 0) {
	    var hair = cm.getPlayerStat("HAIR");
	    hair_Colo_new = [];
	    beauty = 1;
	    if (cm.getPlayerStat("GENDER") == 0) {
		hair_Colo_new = [33100,33120,33160,33220,33150,33110,33200,33210,33040,33000,33050,30730,30920,30830,30780,30800];
	    } else {
		hair_Colo_new = [34210,34170,34120,34110,34150,34160,34180,34220,34200,31990,34140,34100,34010,34000,31760,34040,34050,34060,31890,31950,31940,31930,31920,31910,31890,31880,31870,31860,31850,31820,31800];
	    }
	    for (var i = 0; i < hair_Colo_new.length; i++) {
		hair_Colo_new[i] = hair_Colo_new[i] + (hair % 10);
	    }
		/*if (cm.getPlayerStat("HAIR") % 10 != 0){//如果黑黑色的头发
var currenthaircolo = Math.floor((cm.getPlayerStat("HAIR") / 10)) * 10;
cm.setAvatar(-1,currenthaircolo);
}*/
		var currenthaircolo = Math.floor((cm.getPlayerStat("HAIR") / 10)) * 10;
		//cm.setAvatar(-1,30020)
			
	    cm.sendStyle("我可以改变你的发型,让它比现在看起来漂亮。你为什么不试着改变它下? 如果你有#b#t5150052##k,我将会帮你改变你的发型,那么选择一个你想要的新发型吧!.", hair_Colo_new,5150052);

	} else if (selection == 1) {
	    var currenthaircolo = Math.floor((cm.getPlayerStat("HAIR") / 10)) * 10;
	    hair_Colo_new = [];
	    beauty = 2;

	    for (var i = 0; i < 8; i++) {
		hair_Colo_new[i] = currenthaircolo + i;
	    }
	    cm.sendStyle("我可以改变你的发型,让它比现在看起来漂亮。你为什么不试着改变它下? 如果你有#b#t5151036##k,我将会帮你改变你的发型,那么选择一个你想要的新发型吧!", hair_Colo_new,5151036);
	} else if (selection == 2){ 
	var currenthaircolo = Math.floor((cm.getPlayerStat("HAIR") / 10)) * 10;
		cm.sendOk(""+currenthaircolo+"");
		cm.dispose();
	}//selection
    } else if (status == 2){
	if (beauty == 1){
	    if (cm.setAvatar(5150052, hair_Colo_new[selection]) == 1) {
		cm.sendOk("理发好了，怎么样？看看你的新发型吧！");
	    } else {
		cm.sendOk("看起来你没有#b#t5150052##k。。去商城购买一个吧！");
	    }
	} else {
	    if (cm.setAvatar(5151036, hair_Colo_new[selection]) == 1) {
		cm.sendOk("好了,让朋友们赞叹你的新发色吧!");
	    } else {
		cm.sendOk("看起来你没有#b#t5151036##k。。去商城购买一个吧！");
	    }
	}
	cm.dispose();
    }
}
