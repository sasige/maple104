//debuger
var status = 0;
//添加的获取物品最好不要一次获得超过两个背包空格。
var rate=500;

var scroll_15=new Array(
new Array(2040301,500),//new Array(兑换物品,几率[越大越容易获得至少有一个大于等于rate]),
new Array(2040301,100),
new Array(2040301,50)
);
var scroll_65=new Array(
new Array(2040301,500),//new Array(兑换物品,几率[越大越容易获得至少有一个大于等于rate]),
new Array(2040301,100),
new Array(2040301,50)
);
var scroll_30=new Array(
new Array(2040301,500),//new Array(兑换物品,几率[越大越容易获得至少有一个大于等于rate]),
new Array(2040301,100),
new Array(2040301,50)
);
var scroll_70=new Array(
new Array(2040301,500),//new Array(兑换物品,几率[越大越容易获得至少有一个大于等于rate]),
new Array(2040301,100),
new Array(2040301,50)
);
var scroll_other=new Array(
new Array(2040301,500),//new Array(兑换物品,几率[越大越容易获得至少有一个大于等于rate]),
new Array(2040301,100),
new Array(2040301,50)
);


var allScroll=new Array(
new Array(4031997,100,"15%卷轴",scroll_15,1),//(需要的道具,需要的数量,"兑换名",兑换随机集合,兑换个数)
new Array(4031994,20,"65%卷轴",scroll_65,2),
new Array(4031995,40,"30%卷轴",scroll_30,3),
new Array(4031996,50,"70%卷轴",scroll_70,4),
new Array(4031997,30,"任意卷轴",scroll_other,1)
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
			for (var i = 0; i < allScroll.length; i++) {
				where += "\r\n#L" + i + "##b我要使用#v"+allScroll[i][0]+"# #r" + allScroll[i][1] + " #b个 兑换随机#k "+allScroll[i][2]+"#r " + allScroll[i][4] + " #b个 #k#l";
			}
        cm.sendSimple(where);
    } else if (status == 1) {
		var is=selection;		
		var needitemid=allScroll[is][0];
		var needcount=allScroll[is][1];
		var scrolls=allScroll[is][3];
		var newcount=allScroll[is][4];
		var chance = Math.floor(Math.random()*rate);
		var finalitem = Array();
		for(var i = 0 ;i<scrolls.length;i++){
				if(scrolls[i][1] >= chance){
					finalitem.push(scrolls[i]);
				}
			}
		if(finalitem.length != 0){
			var random = new java.util.Random();
				var finalchance = random.nextInt(finalitem.length);
				var itemId = finalitem[finalchance][0];
				var quantity = newcount;
				var itemChance = finalitem[finalchance][1];
			if(cm.haveSpaceForId(itemId)){
				if(cm.haveItem(needitemid,needcount)){
					cm.gainItem(needitemid,-needcount);
					cm.gainItem(itemId,newcount);
					cm.sendOk("祝贺你兑换成功");		
				}else{
					cm.sendOk("你的物品数量不够\r\n需要#r"+needcount+"#k个#v"+needitemid+"#...");
				}
			}else{
				cm.sendOk("#b必须留出多余的空间才能完成兑换，请确定您的背包空间是否充足能够装下新获得的物品#k");
			}
		}else{
			cm.sendOk("哎呀什么运气，什么都没得到……希望你下次再来哦！");
		}
		
		
		cm.dispose();
    }
}