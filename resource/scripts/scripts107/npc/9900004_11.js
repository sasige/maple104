var status = 0;
//添加的获取物品最好不要一次获得超过两个背包空格。
var duiitems=new Array(
new Array(4000000,1,4000001,1),//new Array(兑换物品,兑换数量,需要物品,需要数量),
new Array(4000001,2,4000000,2),
new Array(4000000,3,4000001,3)
);


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
		var where = "选择你想要兑换的物品吧";
			for (var i = 0; i < duiitems.length; i++) {
				where += "\r\n#L" + i + "##b兑换#v"+duiitems[i][0]+"# #r" + duiitems[i][1] + "#b 个 需要 #v"+duiitems[i][2]+"##r " + duiitems[i][3] + "#b个 #k#l";
			}
        cm.sendSimple(where);
    } else if (status == 1) {
		var is=selection;

		var newitem=duiitems[is][0];
		var newcount=duiitems[is][1];
		var needitemid=duiitems[is][2];
		var needcount=duiitems[is][3];

		if(cm.haveSpaceForId(newitem)){
			if(cm.haveItem(needitemid,needcount)){
			cm.gainItem(needitemid,-needcount);
			cm.gainItem(newitem,newcount);
			cm.sendOk("祝贺你兑换成功");		
			}else{
				cm.sendOk("你的物品数量不够\r\n需要#r"+needcount+"#k个#v"+needitemid+"#...");
			}
		}else{
			cm.sendOk("#b必须留出多余的空间才能完成兑换，请确定您的背包空间是否充足能够装下新获得的物品#k");
		}
		
		cm.dispose();
    }
}