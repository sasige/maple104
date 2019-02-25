
var status = 0;  	
var itemS =  	
Array(
	  
                                                         

 
                                   
                                
                           

 




  Array(5211047,5000,"#b双倍经验卡#k"),

  Array(1032110,30000,"#b烈日耳环#k"),
  Array(1132104,30000,"#b烈日腰带#k"),
  Array(1012283,30000,"#b烈日脸饰#k"),
  Array(1122149,30000,"#b烈日吊坠#k"),


Array(1482103,50000," "), 
Array(1402112,50000," "),
Array(1422074,50000," "),        
Array(1412072,50000," "),       
Array(1452130,50000," "),      
Array(1342041,50000," "),       
Array(1332150,50000," "),       
Array(1332151,50000," "),
Array(1492102,50000," "),
Array(1522021,50000," "),      
Array(1532038,50000," "),      
Array(1472142,50000," "),      
Array(1382125,50000," "),       
Array(1462119,50000," "),      
Array(1322108,50000," "),      
Array(1372101,50000," "),      
Array(1442137,50000," "),
Array(1432100,50000," "),   
Array(1312073,50000," "),       
Array(1302174,50000," "),

 Array(1432037,20000,"枫叶大将旗"),  
                                    Array(1003222,50000,"冰蓝蝴蝶卡子"),  
                           Array(1002888,20000,"丝带发箍（红色）"),  
                              Array(1002890,20000,"丝带发箍（蓝色）"),  
                                                 Array(1002846,20000,"贝雷帽"),  
                              Array(1402014,60000,"温度计"),  
                     Array(1112135,5000,"水墨花名片戒指"),
			Array(1112238,5000,"水墨花聊天戒指"),   
                 	Array(3010085,20000,"鬼娃娃椅子"),   
            	Array(3010110,20000,"舒适大白熊椅子"),   
                Array(3010124,20000,"都纳斯喷气椅子"),  
           Array(3010128,20000,"黑龙王座"),   
                   Array(3010142,20000,"水族馆椅子"),  
 
     Array(3010168,20000,"友谊万岁椅子"),  

 
 Array(3010164,20000,"满月椅(永久)"),  
   Array(3010009,40000,"榻榻凳"),  
 Array(3010058,40000,"世界末日"),  
 Array(3010071,40000,"神兽椅"),  
 Array(3010095,40000,"石头人座椅"),  
 Array(3010096,40000,"恐龙化石宝座"),  
 Array(3010118,40000,"糖果音符椅子"),  
 Array(3010119,40000,"羊羊椅子"),  
 Array(3010127,40000,"扎昆宝座"),  
Array(3010195,200000,"无价之宝椅子（稀有椅子）"),
 Array(3010070,600000,"巨无霸品克缤"), 
 Array(1142249,20000,"我是幸运儿勋章"), 



    Array(1112915,10000,"蓝调戒指"),  
     Array(1112495,30000,"老公老婆戒指LV50"),  
                      Array(1112664,10000,"守护者的不朽戒指 攻击"),   
  Array(1112665,10000,"狂战士的不朽戒指 攻击"),
  Array(1112666,10000,"霸王的永恒戒指 魔力"),
  Array(1112667,10000,"神使的永恒戒指 魔力"),
  Array(1122076,10000,"进阶黑暗龙王项链"),
  Array(1012174,100000,"恐怖鬼娃的伤口"),

  Array(1122122,100000,"真·觉醒冒险之心 战士"),
  Array(1122123,100000,"真·觉醒冒险之心 魔法师"),
  Array(1122124,100000,"真·觉醒冒险之心 弓箭手"),
  Array(1122125,100000,"真·觉醒冒险之心 飞侠"),
  Array(1122126,100000,"真·觉醒冒险之心 海盗"),



  Array(1142178,100000,"#b初级勋章 [点击购买] #k"),

  Array(1142304,200000,"#b中级勋章 [点击购买] #k"),

  Array(1142008,300000,"#b高级勋章 [点击购买] #k")




 
				
						

								
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
			
				  cm.sendNext("\r\n#b你好,我是冒险岛点卷商人！#k?.");   
			
		}else if (status == 1) {
				StringS = "#fUI/UIWindow.img/QuestIcon/3/0#\r\n来看看吧!";
				for (var i = 0; i < itemS.length; i++){
					StringS += "\r\n#L" + i + "#" + itemS[i][2] + "#v" + itemS[i][0] + "#(需要点卷" + itemS[i][1] + "#k点)#l"
				}
				cm.sendSimple(StringS);			 								     
		}else if (status == 2){
			if (cm.getChar().getNX()  >= itemS[selection][1]){
				if(cm.getPlayer().getInventory(net.sf.odinms.client.MapleInventoryType.getByType(1)).isFull(2)){
						cm.sendOk("您至少应该让装备栏空出两格");
						cm.dospose();
				}
		
                    cm.gainNX(- itemS[selection][1]);
				cm.gainItem(itemS[selection][0],1);
				cm.sendOk("购买成功!欢迎下次再来!")
				cm.dispose();
			}else{
				cm.sendOk("点卷不足,请充值.");
				cm.dispose();
			}
		}
	}
}
