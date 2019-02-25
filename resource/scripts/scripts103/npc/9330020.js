/*
	Mady By Coffee
	Powered By XXMS
	Warp NPC
*/
var bossmaps = Array( 	
Array(803000400,0,"#r#e绯红组队任务(需要180级)"),								            //Array(211042300,0,"#r#e进阶扎昆(需要150级)"), 
//Array(240050400,0,"#r#e进阶暗黑龙王(需要160级)"), 
Array(802000820,0,"#n#b欧碧拉(组队BOSS,需要180级)"), 	
Array(240050000,0,"#r#e暗黑龙王/进阶暗黑龙王(需要120级)"), 									
Array(702070400,0,"#n#b武林妖僧(组队BOSS,需要120级)"), 									Array(551030100,0,"#r#e心疤狮王和暴力熊(需要90级)"),
Array(230040410,0,"#n#b皮亚奴斯(需要100级)"),									           Array(220080000,0,"#n#b帕普拉图斯(需要80级)"),
Array(211042300,0,"#r#e扎昆/进阶扎昆(需要50级)"),    
Array(541020700,0,"#n#b克雷塞尔(需要90级)"),
Array(541010060,0,"#n#b幽灵船长(需要80级)"),
Array(211061001,0,"#r#e狮子王 - 班·雷昂(需要120级)"),
Array(270050000,0,"#r#e时间的神殿 - 品克缤(需要120级)"),
Array(271030600,0,"#r#e堕落的女皇 - 希纳斯(需要170级)")		                                               								);

var townmaps = Array(                                       								
Array(700000000,0,"结婚地图"),
Array(140000000,0,"里恩"),
Array(250000000,0,"武陵"), 
Array(500000000,0,"泰国"),
Array(104000000,0,"明珠港"), 
Array(100000000,0,"射手村"), 
Array(222000000,0,"童话村"), 
Array(220000000,0,"玩具城"),
Array(702000000,0,"少林寺"), 
Array(600000000,0,"新叶城"), 
Array(240000000,0,"神木村"), 
Array(251000000,0,"百草堂"),
Array(801000000,0,"昭和村"), 
Array(702100000,0,"藏经阁"),  
Array(101000000,0,"魔法密林"),
Array(102000000,0,"勇士部落"),  
Array(103000000,0,"废弃都市"), 
Array(105000000,0,"林中之城"), 
Array(200000000,0,"天空之城"),
Array(211000000,0,"冰峰雪域"), 
Array(230000000,0,"海底世界"),  
Array(701000000,0,"东方神州"),
Array(260000000,0,"阿里安特"), 
Array(261000000,0,"马加提亚"), 
Array(270000100,0,"时间神殿"), 
Array(106020000,0,"蘑菇城堡"),
Array(800000000,0,"古代神社"),
Array(271000000,0,"未来之门"),
Array(550000000,0,"吉隆大都市"),
Array(541000000,0,"新加坡码头"), 
Array(221000000,0,"地球防御本部"),
Array(120000000,0,"诺特勒斯号码头")

							);
var chosenMap = -1;
var towns = 0;
var bosses = 0;

importPackage(org.client);

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
            if (mode == -1) {
                cm.dispose();
            }
            else {
                if (status >= 0 && mode == 0) {
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
                   if (selection == 0) {
                       cm.sendSimple("#k#fUI/UIWindow.img/QuestIcon/3/0#\r\n#L0#我想传送到城镇地图#l\r\n#L1#我想传送到BOSS地图#l#l");
                   }
                   else if (selection == 1) {
                       cm.dispose();
                   }
               }
               else if (status == 1) {
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
                       for (var i = 0; i < bossmaps.length; i++) {
				selStr += "\r\n#L" + i + "#" + bossmaps[i][2] + "";
                       }
                       cm.sendSimple(selStr);
                       bosses = 1;
                   }
               }
            else if (status == 2) {
                if (towns == 1) {
                cm.sendYesNo("#r你确定要去 " + townmaps[selection][2] + " #r"+townmaps[selection][1]+"");
		chosenMap = selection;
                towns = 2;
                }
                else if (bosses == 1) {
                cm.sendYesNo("#r你确定要去 " + bossmaps[selection][2] + " #r"+bossmaps[selection][1]+"");
                chosenMap = selection;
                bosses = 2;
                }
            }
            else if (status == 3) {
                if (towns == 2) {
                	if(cm.getMeso()>=townmaps[chosenMap][1]){
                		cm.warp(townmaps[chosenMap][0], 0);
                		cm.gainMeso(-townmaps[chosenMap][1]);
				
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
