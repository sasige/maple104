
var status = 0;  	
var itemS =  	
Array(
					Array(5062002,15,"高级神奇魔方"),
					Array(1003274,10000,"#r派对帽子"),
					Array(1042223,10000,"#r派对衣服"),
					Array(1062140,10000,"#r派对裤子"),
					Array(1322106,10000,"#r可疑的包包"),
					Array(1402110,10000,"#r派对吉他"),
					Array(3010313,5000,"粉色扎昆椅子"),
					Array(5062000,12,"神奇魔方"),
					Array(5062001,10,"混沌神奇魔方"),
					Array(5064000,20,"超级防爆卷"),
					Array(1402037,1000,"龙背刃"),
					Array(1402014,1000,"温度计"),
					Array(3010070,20000,"巨无霸品克缤"),
					Array(1002894,800,"粉色编织发带"),		
					Array(1002895,800,"红色编织发带"),
					Array(1002896,800,"紫色编织发带"),
					Array(1002897,800,"橙色编织发带"),
					Array(1002898,800,"绿色编织发带"),
					Array(1002899,800,"黄色编织发带"),
					Array(1002900,800,"蓝色编织发带"),
					Array(1002901,1200,"银色编织发带"),
					Array(1002902,1400,"黑色编织发带"),
					Array(1142000,100,"诚实的冒险家勋章"),
					Array(1142001,200,"组队任务狂人勋章"),
					Array(1142002,130,"任务狂人勋章"),
					Array(1142003,130,"超人气王勋章"),
					Array(1142004,100,"勤奋冒险家勋章"),
					Array(1142006,180,"冒险岛偶像明星勋章"),
					Array(1122029,1500,"冒险之心(战士)#r30#k攻"),							Array(1122030,1500,"冒险之心(法师)#r30#k魔"),						        Array(1122031,1500,"冒险之心(弓手)#r30#k攻"),							Array(1122032,1500,"冒险之心(飞侠)#r30#k攻"),
					Array(1122033,1500,"冒险之心(海盗)#r25#k攻"),
					Array(1122034,3000,"冒险之心(战士)#r50#k攻"),
					Array(1122035,3000,"冒险之心(法师)#r50#k魔"),
					Array(1122036,3000,"冒险之心(弓手)#r50#k攻"),
					Array(1122037,3000,"冒险之心(飞侠)#r50#k攻"),
					Array(1122038,3000,"冒险之心(海盗)#r45#k攻")				
);
var StringS;
function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (status >= 0 && mode == 0) {
			cm.dispose();
			return;
		}
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
			
				  cm.sendNext("#rHi,我是特殊道具换取NPC，在我这您只需用\r\n#b挂市场修为点\r\n#r就可以换取一些特殊道具哦!!\r\n#v5062002##v1003274##v1042223##v1062140##v1322106##v1402110##v3010313##v5062000##v5062001##v5064000##v1402037##v1402014##v3010070##v1002894##v1002895##v1002896##v1002897##v1002898##v1002899##v1002900##v1002901##v1002902##v1142000##v1142001##v1142002##v1142003##v1142004##v1142006##v1122029##v1122030##v1122031##v1122032##v1122033##v1122034##v1122035##v1122036##v1122037##v1122038#");   
			
		}else if (status == 1) {
				StringS = "您好!您当前的市场修为点为#r " + cm.getPlayer().getDojoPoints() + " #k分请选择您需要兑换的道具";
				for (var i = 0; i < itemS.length; i++){
					StringS += "\r\n#L" + i + "#" + itemS[i][2] + "#v" + itemS[i][0] + "#(需要市场修为点#r" + itemS[i][1] + "#k分)#l"
				}
				cm.sendSimple(StringS);			 								     
		}else if (status == 2){
			if (cm.getPlayer().getDojoPoints() >= itemS[selection][1]){
				if(cm.getPlayer().getInventory(net.sf.odinms.client.MapleInventoryType.getByType(1)).isFull(2)){
						cm.sendOk("您至少应该让装备栏空出两格");
						cm.dospose();
				}
				cm.getPlayer().setDojoPoints(cm.getPlayer().getDojoPoints() - itemS[selection][1]);
				if (itemS[selection][0] == 1902007){
					cm.gainItem(1912005,1);
				}
				cm.gainItem(itemS[selection][0],1);
				cm.sendOk("兑换成功！请查看背包！")
				cm.dispose();
			}else{
				cm.sendOk("对不起，你没有足够的市场修为点用来换取！");
				cm.dispose();
			}
		}
	}
}
