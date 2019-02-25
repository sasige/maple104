/*
	Mady By Coffee
	Powered By XXMS
	Warp NPC
*/
var bossmaps = Array( 
                                                                                Array(100020401,0,"#b僵尸蘑菇的巢穴"),
                                                                                Array(100020301,0,"#b蓝蘑菇王的巢穴"),   
										Array(100020101,0,"#b蘑菇王的巢穴"),     
										Array(230040420,0,"#b皮亚奴斯洞穴"), 
										Array(211042400,0,"#b扎昆祭坛入口"), 
										Array(220080001,0,"#b时间塔的本源"), 
										Array(240020402,0,"#b喷火龙栖息地"), 
										Array(240020102,0,"#b格瑞芬的森林"),   
										Array(270050000,0,"#b神的黄昏"),
										Array(551030100,0,"#b阴森世界"),
										Array(541020700,0,"#b克雷塞遗迹"),
										Array(240040700,0,"#b暗黑龙王栖息地"),
										Array(702070400,0,"#b藏经主场")
										);
var monstermaps = Array(
                                                                                Array(806250097,0,"刷白狼脚趾-  刷刷刷               未知"),
                                                                                Array(100010100,0,"射手村   -  梦境小道          3级-10级"),
										Array(101020100,0,"大木林   -  接近鸟的地方      8级-15级"), 
										Array(102030000,0,"勇士部落 -  野猪的领土       15级-20级"), 
										Array(102030400,0,"勇士部落 -  灰烬之地         20级-40级"), 
										Array(551000200,0,"马来西亚 -  大红花路 II      50级-70级"), 
										Array(600020300,0,"内部齿轮装置 - 机械蜘蛛洞穴  70级-80级"), 
										Array(702010000,0,"东方神州   -   山脚          80级-90级"), 
										Array(220060000,0,"玩具城   -   扭曲的时间1     90级-100级"), 
										Array(220060200,0,"玩具城   -   扭曲的时间3    100级-110级"), 
										Array(220060201,0,"玩具城   -   时间异常之地   110级-120级"), 
										Array(240040510,0,"神木村   -   死龙巢穴       要求120级以上")
										); 
var townmaps = Array(
										Array(910000000,0,"#b自由市场"),
                                                                                //Array(809030000,0,"#b豆豆屋-抽奖"),

        Array(100000104,0,"#b射手村美发店"),

        //Array(140000000,0,"#b里恩"),
                                                                                Array(106020000,0,"#b蘑菇城堡"),
                                                                                Array(271010000,0,"#b被破坏的射手村"),
										Array(104000000,0,"#b明珠港"), 
										Array(100000000,0,"#b射手村"), 
										Array(101000000,0,"#b魔法密林"), 
										Array(102000000,0,"#b勇士部落"), 
										Array(103000000,0,"#b废弃都市"), 
										Array(120000000,0,"#b诺特勒斯号码头"),
										Array(105000000,0,"#b林中之城"), 
										Array(200000000,0,"#b天空之城"),
										Array(211000000,0,"#b冰峰雪域"), 
										Array(230000000,0,"#b水下世界"),  
										Array(222000000,0,"#b童话村"), 
										Array(220000000,0,"#b玩具城"),
										Array(701000000,0,"#b东方神州"),
										Array(250000000,0,"#b武陵"), 
										Array(702000000,0,"#b少林寺"), 
										Array(500000000,0,"#b泰国"),
										Array(260000000,0,"#b沙漠之城"), 
										Array(600000000,0,"#b新叶城"), 
										Array(240000000,0,"#b神木村"), 
										Array(261000000,0,"#b马加提亚"), 
										Array(221000000,0,"#b地球防御本部"), 
										Array(251000000,0,"#b百草堂"),
										Array(701000200,0,"#r上海豫园"),
										Array(550000000,0,"#b吉隆大都市"),
										Array(130000000,0,"#b圣地"),  
										Array(801000000,0,"#b昭和村"), 
										Array(540010000,0,"#b新加坡机场"),
										Array(541000000,0,"#b新加坡码头"), 
										Array(270000100,0,"#b时间神殿"), 
										Array(702100000,0,"#b藏经阁"),  
										Array(800000000,0,"#b古代神社") 

							);
var chosenMap = -1;
var monsters = 0;
var towns = 0;
var bosses = 0;

importPackage(net.sf.odinms.client);

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
            if (mode == -1) {
                cm.dispose();
            }
            else {
                if (status >= 3 && mode == 0) {
			cm.sendOk("下次再见!.");
			cm.dispose();
			return;                    
                }
                if (mode == 1) {
			status++;
		}
		else {
			status--;
		}
               if (status == 0) {
                        cm.sendNext("#rHi,我是世界传送员!");                  
                }
               if (status == 1) {
                   cm.sendSimple("#r#fUI/UIWindow.img/QuestIcon/3/0#\r\n#L0#世界传送#l\r\n#L1#抱歉,我走错了#l");
               }
               else if (status == 2) {
                   if (selection == 0) {
                       cm.sendSimple("#r#fUI/UIWindow.img/QuestIcon/3/0#\r\n#L0##b城镇地图#l\r\n#L1#练级地图#l\r\n#L2#BOSS地图#l");
                   }
                   else if (selection == 1) {
                       cm.dispose();
                   }
               }
               else if (status == 3) {
                   if (selection == 0) {
                        var selStr = "选择你的目的地吧.#b";
			for (var i = 0; i < townmaps.length; i++) {
				selStr += "\r\n#L" + i + "#" + townmaps[i][2] + "";
			}
                        cm.sendSimple(selStr);
                        towns = 1;
                   }
                   if (selection == 1) {
                       var selStr = "选择你的目的地吧.#b";
                       for (var i = 0; i < monstermaps.length; i++) {
				selStr += "\r\n#L" + i + "#" + monstermaps[i][2] + "";
                       }
                       cm.sendSimple(selStr);
                       monsters = 1;
                   }
                   if (selection == 2) {
                       var selStr = "选择你的目的地吧.#b";
                       for (var i = 0; i < bossmaps.length; i++) {
				selStr += "\r\n#L" + i + "#" + bossmaps[i][2] + "";
                       }
                       cm.sendSimple(selStr);
                       bosses = 1;
                   }
               }
            else if (status == 4) {
                if (towns == 1) {
                cm.sendYesNo("你确定要去 " + townmaps[selection][2] + "? 价格:#r"+townmaps[selection][1]+"#k金币");
		chosenMap = selection;
                towns = 2;
                }
                else if (monsters == 1) {
                cm.sendYesNo("你确定要去 " + monstermaps[selection][2] + "? 价格:#r"+monstermaps[selection][1]+"#k金币");
                chosenMap = selection;
                monsters = 2;
                }
                else if (bosses == 1) {
                cm.sendYesNo("你确定要去 " + bossmaps[selection][2] + "? 价格:#r"+bossmaps[selection][1]+"#k金币");
                chosenMap = selection;
                bosses = 2;
                }
            }
            else if (status == 5) {
                if (towns == 2) {
                	if(cm.getMeso()>=townmaps[chosenMap][1]){
                		cm.warp(townmaps[chosenMap][0], 0);
                		cm.gainMeso(-townmaps[chosenMap][1]);
				
                	}else{
                		cm.sendOk("你没有足够的金币哦!");
                	}
                    cm.dispose();
                }
                else if (monsters == 2) {
                    if(cm.getMeso()>=monstermaps[chosenMap][1]){
                		cm.warp(monstermaps[chosenMap][0], 0);
                		cm.gainMeso(-monstermaps[chosenMap][1]);
                	}else{
                		cm.sendOk("你没有足够的金币哦!");
                	}
                    cm.dispose();
                }
                else if (bosses == 2) {
                    if(cm.getMeso()>=bossmaps[chosenMap][1]){
                		cm.warp(bossmaps[chosenMap][0], 0);
                		cm.gainMeso(-bossmaps[chosenMap][1]);				
                	}else{
                		cm.sendOk("你没有足够的金币哦!");
                	}
                    cm.dispose();
                }
            }
              
            }
}
