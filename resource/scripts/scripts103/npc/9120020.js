
var status = 0;  	
var itemS =  	
Array(
	  
                                                
                         Array(5062000,3000,"神奇魔方"),
                                       Array(4001626,600,"怪物币"),
                                         Array(1112135,80000,"水墨花名片戒指"),
			Array(1112238,80000,"水墨花聊天戒指"),   
                                  // Array(5062000,3600,"神奇魔方"), 
          
                         Array(1032129,6000,"希望之树之传说耳环"),  
                                          Array(1122185,6000,"希望之树之传说项链"),  
                     Array(1132135,6000,"希望之树之传说腰带"),  
                     Array(1152077,6000,"希望之树之传说肩部"),  
            Array(5050000,600,"能力值初始化卷"), 
                            
                   Array(4001093,8000,"蓝色钥匙（黑龙任务需要）"), 
                                               Array(4000487,1000,"暗影币（圣诞鹿系列、希纳斯需要）"),  
                                               Array(4021010,40000,"时间之石（品克缤任务需要）"),  
                                             Array(4001374,300,"念力钥匙（绯红骑士团需要）"),
                                             
	                                
                                              	Array(4000313,5000,"还原装备属性道具"),
  				
						Array(1112120,8000,"可乐White名片戒指（稀有点装）"),
						Array(1112119,8000,"可乐Red名片戒指（稀有点装）"),
						Array(1112229,8000,"可乐Red聊天戒指（稀有点装）"),
						Array(1112230,8000,"可乐White聊天戒指（稀有点装）"),
                                  
				         
						Array(1022097,5000,"龙眼镜（稀有）"),
						Array(2040211,2000,"龙眼镜专用卷轴"),
            Array(1012191,5000,"暗影双刀面巾（稀有）"), 
  Array(1152001,3000,"黑虎爪"), 
 Array(1152059,1000,"十字旅团勇士护肩"), 

 

						Array(1102320,100000,"真·比耶莫特披风（稀有披风）"),
Array(1102321,100000,"真·夏其尔披风（稀有披风）"),

						
						Array(1302084,2000,"红火柴"),
						Array(1302128,2000,"蓝火柴"),
			
						Array(1112590,8000,"威力戒指Ⅳ"),
						Array(1402037,20000,"龙背刃"),


Array(3010154,1000,"机械椅子"),


Array(3010012,10000,"剑士 宝座"),
Array(3010004,20000,"黄蓝休闲椅"),
Array(3010069,20000,"大黄风"),
Array(3010005,20000,"红蓝休闲椅"),
Array(3010183,50000,"胡萝卜椅子"),
Array(3010014,50000,"月亮弯"),

Array(3010197,50000,"英雄的椅子-战神"),
Array(3010200,50000,"英雄的椅子-龙神"),
Array(3010201,50000,"英雄的椅子-暗影双刀"),

Array(3010029,50000,"蓝环凳"),
Array(3010030,50000,"黑环凳"),
Array(3010031,50000,"红环凳"),
Array(3010032,50000,"黄环凳"),
Array(3010033,50000,"绿环凳")



								
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
			
				  cm.sendNext("\r\n嘿!小子,来和我做一笔买卖?我不会亏待你的.");   
			
		}else if (status == 1) {
				StringS = "#fUI/UIWindow.img/QuestIcon/3/0#\r\n来看看吧!";
				for (var i = 0; i < itemS.length; i++){
					StringS += "\r\n#L" + i + "#" + itemS[i][2] + "#v" + itemS[i][0] + "#(需要抵用卷" + itemS[i][1] + "#k点)#l"
				}
				cm.sendSimple(StringS);			 								     
		}else if (status == 2){
			if (cm.getPlayer().getCSPoints(1) >= itemS[selection][1]){
				if(cm.getPlayer().getInventory(net.sf.odinms.client.MapleInventoryType.getByType(1)).isFull(2)){
						cm.sendOk("您至少应该让装备栏空出两格");
						cm.dospose();
				}
		
                    cm.getPlayer().modifyCSPoints(1, - itemS[selection][1]);
				cm.gainItem(itemS[selection][0],1);
				cm.sendOk("购买成功!欢迎下次再来!")
				cm.dispose();
			}else{
				cm.sendOk("钱不够啊小子。");
				cm.dispose();
			}
		}
	}
}
