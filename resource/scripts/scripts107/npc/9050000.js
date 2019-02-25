importPackage(java.util);
importPackage(net.sf.odinms.client.inventory);
importPackage(net.sf.odinms.tools);

var itemzb=new Array("1482085","1342035","1342036","1402096","1422067","1412066","1312066","1322097","1302153","1442117","1432087","1492086","1332131","1462100","1452112","1382105","1472123","1482084","1402095","1302152","1442116","1432086","1452111","1462099","1492085","1332130","1382104","1472122");
//黄金枫叶装备
var itemfy=new Array("2041133","2043103","2043400");
var itemfyd=new Array("1012057","1022048","1032024","1012104","1022079","1002186","1102039","1082102","1092064","1092067","1072153","2041037","2041035","2041039","2041041","2041059","2041060","2041061","2041062","2041102","2041204","2041209","2041313","2041317","2041319","2041502","1302050","1312006","1322009","1332029","1372024","1382021","1402012","1412017","1422026");
var itemboss=new Array("1302054","1302059","1312030","1312031","1322045","1322052","1332051","1332052","1332050","1332049","1372010","1372032","1402035","1402036","1442044","1442045","1432030","1432038","1472063","1472053","1472051","1472052","1472054","2070011","1422027","1422028","1462015","1462016","1462017","1462039","1382035","1382036","1452019","1452020","1452021","1452044","1412021","1412026","1092036","1092037","1092038","1092060","1092042","1041122","1041123","1041124","1032030","1060109","1060110","1060111","1061121","1061122","1061123","1072273","1072268","1072272","1072269","1492012","1492013","1482012","1482013","1082135","1082136","1082137","1082138","1082139","1082140","1082141","1082158","1082151","1082152","1082153","1082154","1082159","1082160","1082213","1082216","1082168","1072268","1082167","1082163","1102048","1050102","1050103","1050104","1050105","1051090","1051091","1051092","1051093","1051105","1051106","1051107","1050106","1050107","1050108","1050096","1050097","1050098","1050099","1050106","1050107","1050108","1002776","1002777","1002778","1002779","1002780","1082234","1082235","1082236","1082237","1082238","1082239","1082240","1082241","1082242","1082243","1052155","1052156","1052157","1052158","1052159","1052160","1052161","1052162","1052163","1052164","1092057","1092058","1092059");
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
			cm.sendSimple("冒险岛转蛋机中有各类#b装备、卷轴或稀有新奇的道具#k噢！使用通过游戏获得的不同道具可以在我这里抽取到不同奖励的物品，确定要开始试试运气吗？#b\r\n#L3#使用黄金枫叶奖#l\r\n#L4#使用#z3993003#抽奖#l\r\n#L1#使用#z4001126#抽取黄金枫叶装备,祝福卷,必成卷轴等#l\r\n#L7#[挑战]BOSS终结者(集齐BOSS物品抽终极装备)#l");
		}else if (status == 1){
			if(selection==1){
				typed=1;
				cm.sendSimpleS("如果您有我100个枫叶#v4001126#，就可以在我这里抽奖，#b主打[新]黄金枫叶装备，还能抽到祝福卷轴，必成卷轴，刀攻击卷，心跳糖，枫叶装备专用[七周年]潜能力卷轴，[七周年]神秘魔方，#k>给枫叶装备附加潜能,快快试一下吧!#r\r\n黄金会员，中枫叶装备机率将提升1.2倍\r\n白金会员，中枫叶装备机率将提升1.4倍\r\n至尊会员，中枫叶装备机率将提升1.6倍.\r\n#b#L1#直接使用100张枫叶抽奖#l\r\n#L2#使用100张枫叶#r(+1000消费币)#b(提升中奖2倍机率)#l",2);
			}
			if(selection==7){
				typed=7;
				cm.sendNext("我需要以下的物品:\r\n#v4001085# x 1(皮亚努斯BOSS掉落)\r\n#v4001084# x 1(闹钟BOSS掉落)\r\n#v4001083# x 1(扎昆BOSS掉落)\r\n#r如果你收集齐了以上道具,请点击下一步抽奖,我将随机给您100~120级装备,不管你想不想要，我都会收取以上任务道具！\r\n#g#ePs:如果您是至尊会员,获得装备机率将大大提升.");
			}
			if(selection==3){
				typed=3;
				cm.sendSimpleS("朋友，如果你有五张黄金枫叶#v4310003#，在我这里可以抽奖哦~别说我没告诉你~我这里全是#b好宝贝#k..有人问黄金枫叶的出处，据传转身一次就会赠送你一张哟，抓紧去刷吧~#b\r\n#L1#开始抽奖[需要5张黄金枫叶#v4310003#]#l",2);
			}
			if(selection==4){
				typed=4;
				cm.sendSimpleS("朋友，如果你有一个红色福袋，在我这里可以抽奖哦~别说我没告诉你~#r100%中奖#k,我这里全是#b终极武器（130级-140级装备[天然攻击均在300攻以上，上不封顶，就看你的实力+运气了，总之：有此装备在>没任何意外！]）#k..有人问红色福袋的出处，据传只有#b挑战春哥#k副本才有出哦~抓紧去刷吧~#b#e\r\n#L1#开始抽奖[需要1个红色福袋]#l",2);
			}
			if(selection==5){
				cm.openNpc(9050002);
			}
		}else if (status == 2){
			if(typed==1){
				var ii = Packages.server.MapleItemInformationProvider.getInstance();
				var chance1 = Math.floor(Math.random()*100+1);
				var chance2 =0;
				if(cm.haveItem(4001126,100)==true){
					cm.gainItem(4001126,-100);
				}else{
					cm.sendOk("需要100个枫叶才能抽奖 .");
					cm.dispose();
					return;
				}
				if(selection==1){
					if(chance1>=1){
						chance2 = Math.floor(Math.random()*itemfy.length);
						cm.gainItem(itemfy[chance2],1);
						cm.worldMessage("【"+ cm.getChar().getName() +"】在市场[飞天猪]处使用枫叶抽奖获得["+ii.getName(itemfy[chance2])+"]大家一起恭喜他.");
						cm.sendOk("恭喜您，获得：#r "+ii.getName(itemfy[chance2])+" #k .");
						cm.dispose();
						return;
					}else{
						chance2 = Math.floor(Math.random()*itemfyd.length);
						cm.gainItem(itemfyd[chance2],1);
						cm.sendOk("恭喜您，获得：#r "+ii.getName(itemfyd[chance2])+" #k .");
						cm.dispose();
						return;
					}
				}else{
					if(cm.getHyPay(1) >= 1000){
						cm.addHyPay(1000);
					}else{
						cm.sendOk("对不起，你没有1000消费币 .");
						cm.dispose();
						return;
					}
					if(chance1>=20 && chance1<=35){
						chance2 = Math.floor(Math.random()*itemfy.length);
						cm.gainItem(itemfy[chance2],1);
						cm.worldMessage("【"+ cm.getChar().getName() +"】在市场[飞天猪]处使用枫叶抽奖获得["+ii.getName(itemfy[chance2])+"]大家一起恭喜他.");
						cm.sendOk("恭喜您，获得：#r "+ii.getName(itemfy[chance2])+" #k .");
						cm.dispose();
						return;
					}else{
						chance2 = Math.floor(Math.random()*itemfyd.length);
						cm.gainItem(itemfyd[chance2],1);
						cm.sendOk("恭喜您，获得：#r "+ii.getName(itemfyd[chance2])+" #k .");
						cm.dispose();
						return;
					}
				}
				cm.dispose();
				return;
			}
			if(typed==7){
				if(cm.haveItem(4001085,1)==true && cm.haveItem(4001084,1)==true && cm.haveItem(4001083,1)==true){
					cm.gainItem(4001085,-1);
					cm.gainItem(4001084,-1);
					cm.gainItem(4001083,-1);
				}else{
					cm.sendOk("您没有收集到我需要的物品,我需要:#b\r\n#v4001085# x 1(皮亚努斯BOSS掉落)\r\n#v4001084# x 1(闹钟BOSS掉落)\r\n#v4001083# x 1(扎昆BOSS掉落)");
					cm.dispose();
					return;
				}
				var chance = Math.floor(Math.random()*itemboss.length);
				var ii = Packages.server.MapleItemInformationProvider.getInstance();
				if(itemboss[chance]>0){
					cm.gainItem(itemboss[chance],1);
					cm.worldMessage("<BOSS抽奖>"+cm.getChar().getName()+" : "+"在[枫叶邮箱]处使用BOSS物品抽奖获得["+ii.getName(itemboss[chance])+"]大家一起恭喜他.");
					cm.sendOk("恭喜，获得物品：#v"+itemboss[chance]+"#");
				}else{
					cm.sendOk("很遗憾，什么都没有得到！");
				}
				cm.dispose();
				return;
			}
			if(typed==3){
				if(selection==1){
					if(cm.haveItem(4310003,5)==true){
						var randx=Math.floor((Math.random()*26))+1;
						if(randx==1){
							cm.gainItem(2340000,5);
							cm.gainItem(4310003,-5);
							cm.worldMessage("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得5张祝福卷轴~");//另一种黄色
							cm.sendOk("#e恭喜您获得了5张祝福卷轴~~~~~");
						}
						if(randx==2){
							cm.gainItem(1402037,1);
							cm.gainItem(4310003,-5);
							cm.worldMessage("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得龙背刃~");//另一种黄色
							cm.sendOk("#e恭喜您获得了龙背刃~~~~~");
						}
						if(randx==3){
							cm.gainItem(1092022,1);
							cm.gainItem(4310003,-5);
							cm.worldMessage("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得调色板~");//另一种黄色
							cm.sendOk("#e恭喜您获得了玩具:调色板~~~~~");
						}
						if(randx==4){
							cm.gainItem(1702289,1);
							cm.gainItem(4310003,-5);
							cm.worldMessage("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得皇家海军旗帜~");
							cm.sendOk("#e恭喜您获得了玩具:皇家海军旗帜~~~~~");
						}
						if(randx==5){
							cm.gainItem(1702288,1);
							cm.gainItem(4310003,-5);
							cm.worldMessage("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得豹弩游侠弩~");
							cm.sendOk("#e恭喜您获得了玩具:豹弩游侠弩~~~~~");
						}
						if(randx==6){
							cm.gainItem(1702287,1);
							cm.gainItem(4310003,-5);
							cm.worldMessage("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得唤灵斗师长杖~");
							cm.sendOk("#e恭喜您获得了玩具:唤灵斗师长杖~~~~~");
						}
						if(randx==7){
							cm.gainItem(1702275,1);
							cm.gainItem(4310003,-5);
							cm.worldMessage("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得皇家彩虹伞~");
							cm.sendOk("#e恭喜您获得了玩具:皇家彩虹伞~~~~~");
						}
						if(randx==8){
							cm.gainItem(1702191,1);
							cm.gainItem(4310003,-5);
							cm.worldMessage("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得彩虹剑~");
							cm.sendOk("#e恭喜您获得了玩具:彩虹剑~~~~~");
						}
						if(randx==9){
				var ii = Packages.server.MapleItemInformationProvider.getInstance();
					var type = Packages.constants.GameConstants.getInventoryType(1462076);
					var toDrop = ii.randomizeStats(ii.getEquipById(1462076)).copy(); // 生成一个Equip类
					toDrop.setStr(15); //装备力量
					toDrop.setDex(25); //装备敏捷
					toDrop.setInt(33); //装备智力
					toDrop.setLuk(55); //装备运气
					toDrop.setWatk(129);
					cm.getPlayer().getInventory(type).addItem(toDrop); //将这个装备放入包中
            cm.getC().getSession().write(Packages.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包
							cm.gainItem(4310003,-5);
							cm.worldMessage("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得超属性装备地狱之弩~");
							cm.sendOk("#e恭喜您获得了超属性装备:地狱之弩~~~~~");
						}
						if(randx==10){
				var ii = Packages.server.MapleItemInformationProvider.getInstance();
					var type = Packages.constants.GameConstants.getInventoryType(1452058);
					var toDrop = ii.randomizeStats(ii.getEquipById(1452058)).copy(); // 生成一个Equip类
					toDrop.setStr(18); //装备力量
					toDrop.setDex(25); //装备敏捷
					toDrop.setInt(33); //装备智力
					toDrop.setLuk(15); //装备运气
					toDrop.setWatk(125);
					cm.getPlayer().getInventory(type).addItem(toDrop); //将这个装备放入包中
            cm.getC().getSession().write(Packages.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包
							cm.gainItem(4310003,-5);
							cm.worldMessage("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得超属性装备水精灵弓~");
							cm.sendOk("#e恭喜您获得了超属性装备:水精灵弓~~~~~");
						}
						if(randx==11){
				var ii = Packages.server.MapleItemInformationProvider.getInstance();
					var type = Packages.constants.GameConstants.getInventoryType(1402073);
					var toDrop = ii.randomizeStats(ii.getEquipById(1402073)).copy(); // 生成一个Equip类
					toDrop.setStr(10); //装备力量
					toDrop.setDex(20); //装备敏捷
					toDrop.setInt(23); //装备智力
					toDrop.setLuk(15); //装备运气
					toDrop.setWatk(115);
					cm.getPlayer().getInventory(type).addItem(toDrop); //将这个装备放入包中
            cm.getC().getSession().write(Packages.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包
							cm.gainItem(4310003,-5);
							cm.worldMessage("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得超属性装备阿斯卡隆圣剑~");
							cm.sendOk("#e恭喜您获得了超属性装备:阿斯卡隆圣剑~~~~~");}
						if(randx==12){
							cm.gainItem(2022176,28);
							cm.gainItem(4310003,-5);
							cm.sendOk("#e恭喜您获得了超级药水~~~~~");}
						if(randx==13){
							cm.gainItem(2044703,1);
							cm.gainItem(4310003,-5);
							cm.worldMessage("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得拳套攻击必成卷~");
							cm.sendOk("#e恭喜您获得了拳套攻击必成卷~~~~~");}
						if(randx==14){
							cm.gainItem(2044603,1);
							cm.gainItem(4310003,-5);
							cm.worldMessage("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得弩攻击必成卷~");
							cm.sendOk("#e恭喜您获得了弩攻击必成卷~~~~~");}
						if(randx==15){
							cm.gainItem(2044503,1);
							cm.gainItem(4310003,-5);
							cm.worldMessage("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得弓攻击必成卷~");
							cm.sendOk("#e恭喜您获得了弓攻击必成卷~~~~~");}
						if(randx==16){
							cm.gainItem(2044403,1);
							cm.gainItem(4310003,-5);
							cm.worldMessage("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得矛攻击必成卷~");
							cm.sendOk("#e恭喜您获得了矛攻击必成卷~~~~~");}
						if(randx==17){
							cm.gainItem(2044303,1);
							cm.gainItem(4310003,-5);
							cm.worldMessage("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得枪攻击必成卷~");
							cm.sendOk("#e恭喜您获得了枪攻击必成卷~~~~~");}
						if(randx==18){
							cm.gainItem(2044203,1);
							cm.gainItem(4310003,-5);
							cm.worldMessage("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得双手钝器攻击必成卷~");
							cm.sendOk("#e恭喜您获得了双手钝器攻击必成卷~~~~~");}
						if(randx==19){
							cm.gainItem(2044103,1);
							cm.gainItem(4310003,-5);
							cm.worldMessage("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得双手斧攻击必成卷~");
							cm.sendOk("#e恭喜您获得了双手斧攻击必成卷~~~~~");}
						if(randx==20){
							cm.gainItem(2044003,1);
							cm.gainItem(4310003,-5);
							cm.worldMessage("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得双手剑攻击必成卷~");
							cm.sendOk("#e恭喜您获得了双手剑攻击必成卷~~~~~");}
						if(randx==21){
							cm.gainItem(2043803,1);
							cm.gainItem(4310003,-5);
							cm.worldMessage("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得长杖魔力必成卷~");
							cm.sendOk("#e恭喜您获得了长杖魔力必成卷~~~~~");}
						if(randx==22){
							cm.gainItem(2043703,1);
							cm.gainItem(4310003,-5);
							cm.worldMessage("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得短杖攻击必成卷~");
							cm.sendOk("#e恭喜您获得了短杖攻击必成卷~~~~~");}
						if(randx==23){
							cm.gainItem(2043303,1);
							cm.gainItem(4310003,-5);
							cm.worldMessage("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得短剑攻击必成卷~");
							cm.sendOk("#e恭喜您获得了短剑攻击必成卷~~~~~");
							}
						if(randx==24){
							cm.gainItem(2043203,1);
							cm.gainItem(4310003,-5);
							cm.worldMessage("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得单手钝器攻击必成卷~");
							cm.sendOk("#e恭喜您获得了单手钝器攻击必成卷~~~~~");
							}
						if(randx==25){
							cm.gainItem(2043103,1);
							cm.gainItem(4310003,-5);
							cm.worldMessage("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得单手斧攻击必成卷~");
							cm.sendOk("#e恭喜您获得了单手斧攻击必成卷~~~~~");
							}
						if(randx==26){
							cm.gainItem(2043003,1);
							cm.gainItem(4310003,-5);
							cm.worldMessage("【"+ cm.getChar().getName() +"】在市场飞天猪处使用黄金枫叶获得单手剑攻击必成卷~");
							cm.sendOk("#e恭喜您获得了单手剑攻击必成卷~~~~~");
							}
						if(randx==27){
							cm.gainItem(2041024,1);
							cm.gainItem(4310003,-5);
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
				var ii = Packages.server.MapleItemInformationProvider.getInstance();
					var chancestr = Math.floor(Math.random()*20+130);
					var chancedex = Math.floor(Math.random()*20+130); 
					var chanceint = Math.floor(Math.random()*20+130);
					var chanceluk = Math.floor(Math.random()*20+130);
					var chancewatk = Math.floor(Math.random()*30+140);
					var chancematk = Math.floor(Math.random()*30+140);
					var chancezb = Math.floor(Math.random()*itemzb.length);
					var type = Packages.constants.GameConstants.getInventoryType(itemzb[chancezb]);
					var toDrop = ii.randomizeStats(ii.getEquipById(itemzb[chancezb])).copy(); // 生成一个Equip类
					toDrop.setStr(chancestr); //装备力量
					toDrop.setDex(chancedex); //装备敏捷
					toDrop.setInt(chanceint); //装备智力
					toDrop.setLuk(chanceluk); //装备运气
					toDrop.setWatk(chancewatk);
					toDrop.setMatk(chancematk);;
					cm.getPlayer().getInventory(type).addItem(toDrop); //将这个装备放入包中
            cm.getC().getSession().write(Packages.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包
					cm.sendOk("恭喜，您此次抽到:#b#i"+itemzb[chancezb]+"#");
					cm.worldMessage("【"+ cm.getChar().getName() +"】在市场飞天猪处使用红色福袋抽奖获得全属性<"+ii.getName(itemzb[chancezb])+">~");//另一种黄色
				}
				cm.dispose();
				return;
			}
		}else if (status == 3){
		}
	}
}
