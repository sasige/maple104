/*
	脚本类型: 		传送NPC
           修改：                     一纸离人醉丶
           技术指导：              芬芬时尚潮流
*/
var bossmaps = Array( 
Array(104010200,0,"#d蜗牛霸主BOSS-蜗牛王#e#r【无等级限制】#n"), 
Array(100020101,10,"#d蘑菇森林BOSS-蘑菇王#e#r【需要10以上】#n"), 
Array(100020301,10,"#d蘑菇森林BOSS-蓝蘑菇#e#r【需要10以上】#n"), 
Array(100020401,10,"#d蘑菇森林BOSS-僵尸菇#e#r【需要10以上】#n"), 
Array(102020500,10,"#d旋风地带BOSS-树妖王#e#r【需要10以上】#n"), 
Array(120030500,10,"#d黄金海滩BOSS-巨居蟹#e#r【需要10以上】#n"), 
Array(103030400,10,"#d沼泽地带BOSS-鳄鱼王#e#r【需要10以上】#n"), 
Array(101040300,10,"#d诅咒森林BOSS-浮士德#e#r【需要10以上】#n"), 
Array(105020400,30,"#d龙族洞穴BOSS-黑轮王#e#r【需要30以上】#n"), 
Array(222010310,30,"#d月岭妖狐BOSS-九尾狐#e#r【需要30以上】#n"), 
Array(800010100,50,"#d天皇殿堂BOSS-蓝蘑菇#e#r【需要50以上】#n"), 
Array(200010300,50,"#d天空楼梯BOSS-艾利杰#e#r【需要50以上】#n"), 
Array(925100500,50,"#d海盗船长BOSS-老海盗#e#r【需要50以上】#n"), 
Array(105030500,50,"#d禁忌祭坛BOSS-蝙蝠怪#e#r【需要50以上】#n"), 
Array(250010304,50,"#d武陵霸主BOSS-肯德熊#e#r【需要50以上】#n"), 
Array(260010201,50,"#d阿里安特BOSS-仙人掌#e#r【需要50以上】#n"), 
Array(220050100,50,"#d时间漩涡BOSS-猫头鹰#e#r【需要50以上】#n"), 
Array(252020000,80,"#d黄金寺院BOSS-拉瓦那#e#r【需要80以上】#n"),
Array(221040301,80,"#d哥雷草原BOSS-外星司令#e#r【需要80以上】#n"), 
Array(250010503,80,"#d妖怪森林BOSS-妖怪禅师#e#r【需要80以上】#n"), 
Array(541010060,80,"#d七号船舱BOSS-幽灵船长#e#r【需要80以上】#n"), 
Array(211040101,100,"#d雪原狼人BOSS-驮狼雪人#e#r【需要100以上】#n"), 
Array(300030310,100,"#d蝴蝶森林BOSS-蝴蝶女王#e#r【需要100以上】尚未修复#n"), 
Array(240040401,100,"#d蓝龙峡谷BOSS-蓝龙#e#r【需要120以上】#n"), 
Array(270010500,120,"#d追忆之路BOSS-多多#e#r【需要120以上】#n"), 
Array(270030500,120,"#d忘却之路BOSS-雷卡#e#r【需要120以上】#n"), 
Array(270020500,120,"#d后悔之路BOSS-独角#e#r【需要120以上】#n"), 
Array(240020402,120,"#d火龙栖地BOSS-火龙#e#r【需要120以上】#n"), 
Array(240020101,120,"#d格瑞森林BOSS-龙鹰#e#r【需要120以上】#n"),   
Array(802000101,120,"#d逆奥之城BOSS-未来#e#r【需要120以上】#n"), 
Array(230040410,120,"#d幽深洞穴BOSS-皮亚奴斯#e#r【需要120以上】#n"), 
Array(211042300,120,"#d血祭之魔BOSS-扎昆祭坛#e#r【需要120以上】#n"), 
Array(220080000,120,"#d时间塔的本源BOSS-闹钟#e#r【需要120以上】#n"), 
Array(240020402,120,"#d喷火龙栖息地BOSS-火龙#e#r【需要120以上】#n"), 
Array(240020101,120,"#d格瑞芬的森林BOSS-龙鹰#e#r【需要120以上】#n"),   
Array(551030100,120,"#d阴森世界BOSS-心疤狮#e#r【需要120以上】#n"),
Array(541020700,120,"#d雷塞遗迹BOSS-树精王#e#r【需要120以上】#n"),
Array(240040700,120,"#d远古霸主BOSS-暗黑龙王#e#r【需要120以上】#n"),
Array(702070400,120,"#d藏经主场BOSS-少林妖僧#e#r【需要120以上】尚未修复#n"),
Array(802000801,120,"#d逆奥之城BOSS-皇家护卫#e#r【需要120以上】尚未修复#n"),
Array(271000000,120,"#d超爽之门BOSS-神仙居所#e#r【需要120以上】尚未修复#n")
										);
var townmaps = Array(
Array(50000,0,"彩虹岛"),
Array(914040000,0,"里恩"),
Array(130000000,0,"圣地"),
Array(105000000,0,"林中之城"), 
Array(300000000,0,"艾琳森林"), 
Array(252000000,0,"黄金寺院"), 
Array(104000000,0,"明珠港"), 
Array(100000000,0,"射手村"), 
Array(310000000,0,"埃德尔斯坦"), 
Array(271010000,0,"破坏的射手村"), 
Array(106020000,0,"蘑菇城"), 
Array(101000000,0,"魔法密林"), 
Array(102000000,0,"勇士部落"), 
Array(103000000,0,"废弃都市"), 
Array(103040000,0,"废都广场"), 
Array(120000000,0,"诺特勒斯"),
Array(682000000,0,"闹鬼宅邸"),
Array(110000000,0,"黄金海岸"),
Array(200000301,0,"家族中心"),
Array(200000000,0,"天空之城"),
Array(211000000,0,"冰峰雪域"), 
Array(230000000,0,"水下世界"),  
Array(222000000,0,"童话村"), 
Array(220000000,0,"玩具城"),
Array(802000101,0,"逆奥之城"),
Array(600000000,0,"马莱尼西亚"),
Array(103040000,0,"101大道"), 
Array(701000000,0,"东方神州"),
Array(250000000,0,"武陵"), 
Array(702000000,0,"少林寺"), 
Array(500000000,0,"泰国"),
Array(260000000,0,"阿里安特"), 
Array(600000000,0,"新叶城"), 
Array(240000000,0,"神木村"), 
Array(261000000,0,"马加提亚"), 
Array(221000000,0,"地球防御本部"), 
Array(251000000,0,"百草堂"),
Array(701000200,0,"上海豫园"),
Array(550000000,0,"吉隆大都市"),  
Array(801000000,0,"昭和村"), 
Array(540000000,0,"新加坡机场"),
Array(270000100,0,"时间神殿"),  
Array(800000000,0,"古代神社") 

							);
var monstermaps = Array(
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
                if (status >= 0 && mode == 0) {
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
                       cm.sendSimple("#b#fUI/UIWindow.img/QuestIcon/3/0#\r\n#L0#城镇地图#l#L1#练级地图#l#L2#BOSS地图#l\r\n#L3#幸福村 圣诞狂欢#l#L4#摄影棚 截图必备#l\r\n#L5#匠人街 专业技术村庄#l");
                   }
                   else if (selection == 1) {
                       cm.dispose();
                   }
               }
               else if (status == 1) {
                   if (selection == 0) {
                        var selStr = "选择你的目的地吧,如果卡地图请输入@fm.#b";
			for (var i = 0; i < townmaps.length; i++) {
				selStr += "\r\n#L" + i + "#" + townmaps[i][2] + "";
			}
                        cm.sendSimple(selStr);
                        towns = 1;
                   }
		   if (selection == 1) {
                       var selStr = "选择你的目的地吧,如果卡地图请输入@fm.#b";
                       for (var i = 0; i < monstermaps.length; i++) {
				selStr += "\r\n#L" + i + "#" + monstermaps[i][2] + "";
                       }
                       cm.sendSimple(selStr);
                       monsters = 1;
                   }
                   if (selection == 2) {
                       var selStr = "";
                       for (var i = 0; i < bossmaps.length; i++) {
				selStr += "#L" + i + "#" + bossmaps[i][2] + "\r\n";
                       }
                       cm.sendSimple(selStr);
                       bosses = 1;
                   }
		   if (selection == 3) {
			cm.warp(209000000);
			cm.dispose();
		   }
		   if (selection == 4) {
			cm.warp(970000100);
			cm.dispose();
		   }
		   if (selection == 5) {
			cm.warp(910001000);
			cm.dispose();
		   }

               }
            else if (status == 2) {
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
                cm.sendYesNo("你确定要去 " + bossmaps[selection][2] + "?");
                chosenMap = selection;
                bosses = 2;
                }
            }
            else if (status == 3) {
                if (towns == 2) {
                	if(cm.getMeso()>=townmaps[chosenMap][1]){
                		cm.warp(townmaps[chosenMap][0], 0);
                	//	cm.gainMeso(-townmaps[chosenMap][1]);
				
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
                    if(cm.getLevel()>=bossmaps[chosenMap][1]){
                		cm.warp(bossmaps[chosenMap][0], 0);				
                	}else{
                		cm.sendOk("你的等级不够!");
                	}
                    cm.dispose();
                }
            }
              
            }
}


