importPackage(java.util);
importPackage(net.sf.odinms.client.inventory);
importPackage(org.server);
importPackage(net.sf.odinms.tools);

var itemzb=new Array("1482085","1342035","1342036","1402096","1422067","1412066","1312066","1322097","1302153","1442117","1432087","1492086","1332131","1462100","1452112","1382105","1472123","1482084","1402095","1302152","1442116","1432086","1452111","1462099","1492085","1332130","1382104","1472122");
var status = 0;
var typed=0;


function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (mode == 0 && status >= 0) {
			cm.dispose();
			return;
		}
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
			cm.sendSimple("系统会随机抽取物品，你有可能获得意想不到的惊喜！#b\r\n#L3#使用1个#v3993003#抽奖#l");
		}else if (status == 1){
			if(selection==3){
				typed=3;
				cm.sendSimple("朋友，如果你有一张黄金枫叶#v3993003#，在我这里可以抽奖哦~别说我没告诉你~我这里全是#b好宝贝#k..有人问黄金枫叶的出处，据传只有#b金蛋#k才有出哦~市场每半个小时就有一个，抓紧去刷吧~#b\r\n#L1#开始抽奖[需要1张黄金枫叶#v3993003#]#l",2);
			}else if(selection==4){
				typed=4;
				cm.sendSimple("朋友，如果你有一个红色福袋#v3993003#，在我这里可以抽奖哦~别说我没告诉你~#r100%中奖#k,我这里全是#b终极武器（130级-140级装备[天然攻击均在300攻以上，上不封顶，就看你的实力+运气了，总之：有此装备在>没任何意外！]）#k..有人问红色福袋的出处，据传只有#b干掉上帝#k副本才有出哦~抓紧去刷吧~#b#e\r\n#L1#开始抽奖[需要1个红色福袋#v3993003#]#l",2);
			}else{
				cm.dispose();
			}
		}else if (status == 2){
			if(typed==3){
				if(selection==1){
					if(cm.haveItem(3993003,1)==true){
						var randx=Math.floor((Math.random()*26))+1;
						if(randx==1){
							cm.gainItem(2340000,5);
							cm.gainItem(3993003,-1);
							//cm.itemlaba("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得5张祝福卷轴~");//另一种黄色
							cm.sendOk("#e恭喜您获得了5张祝福卷轴~~~~~");
						}
						if(randx==2){
							cm.gainItem(1402037,1);
							cm.gainItem(3993003,-1);
							//cm.itemlaba("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得龙背刃~");//另一种黄色
							cm.sendOk("#e恭喜您获得了龙背刃~~~~~");
						}
						if(randx==3){
							cm.gainItem(1092022,1);
							cm.gainItem(3993003,-1);
							cm.itemlaba("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得调色板~");//另一种黄色
							cm.sendOk("#e恭喜您获得了玩具:调色板~~~~~");
						}
						if(randx==4){
							cm.gainItem(1702289,1);
							cm.gainItem(3993003,-1);
							//cm.itemlaba("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得皇家海军旗帜~");
							cm.sendOk("#e恭喜您获得了玩具:皇家海军旗帜~~~~~");
						}
						if(randx==5){
							cm.gainItem(1702288,1);
							cm.gainItem(3993003,-1);
							//cm.itemlaba("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得豹弩游侠弩~");
							cm.sendOk("#e恭喜您获得了玩具:豹弩游侠弩~~~~~");
						}
						if(randx==6){
							cm.gainItem(1702287,1);
							cm.gainItem(3993003,-1);
							//cm.itemlaba("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得唤灵斗师长杖~");
							cm.sendOk("#e恭喜您获得了玩具:唤灵斗师长杖~~~~~");
						}
						if(randx==7){
							cm.gainItem(1702275,1);
							cm.gainItem(3993003,-1);
							cm.itemlaba("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得皇家彩虹伞~");
							cm.sendOk("#e恭喜您获得了玩具:皇家彩虹伞~~~~~");
						}
						if(randx==8){
							cm.gainItem(1702191,1);
							cm.gainItem(3993003,-1);
							//cm.itemlaba("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得彩虹剑~");
							cm.sendOk("#e恭喜您获得了玩具:彩虹剑~~~~~");
						}
						if(randx==9){
							cm.makeitem(1462076,15,25,33,55,129,0,7,"");
							cm.gainItem(3993003,-1);
							//cm.itemlaba("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得超属性装备地狱之弩~");
							cm.sendOk("#e恭喜您获得了超属性装备:地狱之弩~~~~~");
						}
						if(randx==10){
							cm.makeitem(1452058,18,25,33,15,125,0,7,"");
							cm.gainItem(3993003,-1);
							//cm.itemlaba("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得超属性装备水精灵弓~");
							cm.sendOk("#e恭喜您获得了超属性装备:水精灵弓~~~~~");
						}
						if(randx==11){
							cm.makeitem(1402073,10,20,23,15,115,0,7,"");
							cm.gainItem(3993003,-1);
							//cm.itemlaba("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得超属性装备阿斯卡隆圣剑~");
							cm.sendOk("#e恭喜您获得了超属性装备:阿斯卡隆圣剑~~~~~");}
						if(randx==12){
							cm.gainItem(2022176,28);
							cm.gainItem(3993003,-1);
							cm.sendOk("#e恭喜您获得了超级药水~~~~~");}
						if(randx==13){
							cm.gainItem(2044703,1);
							cm.gainItem(3993003,-1);
							//cm.itemlaba("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得拳套攻击必成卷~");
							cm.sendOk("#e恭喜您获得了拳套攻击必成卷~~~~~");}
						if(randx==14){
							cm.gainItem(2044603,1);
							cm.gainItem(3993003,-1);
							//cm.itemlaba("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得弩攻击必成卷~");
							cm.sendOk("#e恭喜您获得了弩攻击必成卷~~~~~");}
						if(randx==15){
							cm.gainItem(2044503,1);
							cm.gainItem(3993003,-1);
							cm.itemlaba("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得弓攻击必成卷~");
							cm.sendOk("#e恭喜您获得了弓攻击必成卷~~~~~");}
						if(randx==16){
							cm.gainItem(2044403,1);
							cm.gainItem(3993003,-1);
							//cm.itemlaba("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得矛攻击必成卷~");
							cm.sendOk("#e恭喜您获得了矛攻击必成卷~~~~~");}
						if(randx==17){
							cm.gainItem(2044303,1);
							cm.gainItem(3993003,-1);
							//cm.itemlaba("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得枪攻击必成卷~");
							cm.sendOk("#e恭喜您获得了枪攻击必成卷~~~~~");}
						if(randx==18){
							cm.gainItem(2044203,1);
							cm.gainItem(3993003,-1);
							//cm.itemlaba("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得双手钝器攻击必成卷~");
							cm.sendOk("#e恭喜您获得了双手钝器攻击必成卷~~~~~");}
						if(randx==19){
							cm.gainItem(2044103,1);
							cm.gainItem(3993003,-1);
							//cm.itemlaba("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得双手斧攻击必成卷~");
							cm.sendOk("#e恭喜您获得了双手斧攻击必成卷~~~~~");}
						if(randx==20){
							cm.gainItem(2044003,1);
							cm.gainItem(3993003,-1);
							//cm.itemlaba("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得双手剑攻击必成卷~");
							cm.sendOk("#e恭喜您获得了双手剑攻击必成卷~~~~~");}
						if(randx==21){
							cm.gainItem(2043803,1);
							cm.gainItem(3993003,-1);
							//cm.itemlaba("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得长杖魔力必成卷~");
							cm.sendOk("#e恭喜您获得了长杖魔力必成卷~~~~~");}
						if(randx==22){
							cm.gainItem(2043703,1);
							cm.gainItem(3993003,-1);
							//cm.itemlaba("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得短杖攻击必成卷~");
							cm.sendOk("#e恭喜您获得了短杖攻击必成卷~~~~~");}
						if(randx==23){
							cm.gainItem(2043303,1);
							cm.gainItem(3993003,-1);
							//cm.itemlaba("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得短剑攻击必成卷~");
							cm.sendOk("#e恭喜您获得了短剑攻击必成卷~~~~~");
							}
						if(randx==24){
							cm.gainItem(2043203,1);
							cm.gainItem(3993003,-1);
							//cm.itemlaba("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得单手钝器攻击必成卷~");
							cm.sendOk("#e恭喜您获得了单手钝器攻击必成卷~~~~~");
							}
						if(randx==25){
							cm.gainItem(2043103,1);
							cm.gainItem(3993003,-1);
							//cm.itemlaba("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得单手斧攻击必成卷~");
							cm.sendOk("#e恭喜您获得了单手斧攻击必成卷~~~~~");
							}
						if(randx==26){
							cm.gainItem(2043003,1);
							cm.gainItem(3993003,-1);
							//cm.itemlaba("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得单手剑攻击必成卷~");
							cm.sendOk("#e恭喜您获得了单手剑攻击必成卷~~~~~");
							}
						if(randx==27){
							cm.gainItem(2041024,1);
							cm.gainItem(3993003,-1);
							cm.sendOk("#e恭喜您获得了披风魔防必成卷~~~~~");
						}
					}else{
						cm.sendOk("对不起~物品不足,请检查一下~~");
					}
				}
			cm.dispose();
			return;
			}
			if(typed==4){
				if(selection==1){
				if(cm.haveItem(3993003,1)==false){
					cm.sendOk("对不起，你没有一个#v3993003#");
					cm.dispose();
					return;
				}else{
					cm.gainItem(3993003,-1);
				}
				var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();
					var chancestr = Math.floor(Math.random()*20+130);
					var chancedex = Math.floor(Math.random()*20+130); 
					var chanceint = Math.floor(Math.random()*20+130);
					var chanceluk = Math.floor(Math.random()*20+130);
					var chancewatk = Math.floor(Math.random()*30+140);
					var chancematk = Math.floor(Math.random()*30+140);
					var chancezb = Math.floor(Math.random()*itemzb.length);
					cm.makeitem(itemzb[chancezb],chancestr,chancedex,chanceint,chanceluk,chancewatk,chancematk,5,"");
					cm.sendOk("恭喜，您此次抽到:#b#i"+itemzb[chancezb]+"#");
					//cm.itemlaba("【"+ cm.getChar().getName() +"】在市场飞天猪处使用红色福袋抽奖获得全属性<"+ii.getName(itemzb[chancezb])+">~");//另一种黄色
				}
				cm.dispose();
				return;
			}
		}else if (status == 3){
		}
	}
}
