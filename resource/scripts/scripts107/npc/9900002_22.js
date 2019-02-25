/* Joyce
	Event NPC
*/

var status = -1;
var maps = Array(
100000202,//射手跳跳
220000006,//玩具城跳跳
106020000,//蘑菇城堡
910001000, //隐藏地图 - 专业技术村庄&lt;匠人街>
230000000, //水下世界 - 水下世界
260000000, //火焰之路 - 阿里安特
101000000, //魔法密林 - 魔法密林
211000000, //神秘岛 - 冰峰雪域
120030000, //黄金海滩 - 海边瓜棚
130000200, //女皇之路 - 圣地岔路
100000000, //射手村 - 射手村
103000000, //废弃都市 - 废弃都市
222000000, //时间静止之湖 - 童话村
240000000, //神木村 - 神木村
104000000, //明珠港 - 明珠港
220000000, //玩具城 - 玩具城
802000101, //逆奥之城 - 卡姆那 （内部）
120000000, //诺特勒斯 - 诺特勒斯码头
221000000, //时间静止之湖 - 地球防御本部
200000000, //神秘岛 - 天空之城
102000000, //勇士部落 - 勇士部落
300000000, //艾琳森林 - 阿尔泰营地
801000000, //昭和村 - 昭和村
540000000, //新加坡 - 中心商务区
541000000, //新加坡 - 驳船码头城
250000000, //武陵 - 武陵
251000000, //百草堂 - 百草堂
551000000, //马来西亚 - 甘榜村
550000000, //马来西亚 - 吉隆大都市 
261000000, //莎翁小镇 - 玛加提亚
541020000, //新加坡 - 乌鲁城入口
270000000, //时间神殿 - 三个门
682000000, //隐藏地图 - 闹鬼宅邸外部
140000000, //冰雪之岛 - 里恩
970010000, //隐藏地图 - 枫树山丘
103040000, //废都广场 - 废都广场大厅
555000000, //M我 - 白色圣诞山丘
310000000, //黑色之翼领地 - 埃德尔斯坦
200100000, //天空中的克里塞 - 克里塞入口
211060000, //狮子王之城 - 沉寂原野
310040300, //干路 - 岩石路
701000000);//上海外滩

var monstermaps = Array(
										Array(50000,0,"彩虹岛	-	大蘑菇        	1级-10级"),
                                                                                Array(100010100,0,"射手村	-	梦境小道          3级-10级"),
										Array(101020100,0,"大木林	-  	接近鸟的地方      8级-15级"), 
										Array(102030000,0,"勇士部落 	-  	野猪的领土        15级-20级"), 
										Array(102030400,0,"勇士部落 	-  	灰烬之地          20级-40级"), 
										Array(551000200,0,"马来西亚 	-  	大红花路 II       50级-70级"), 
										Array(600020300,0,"内部齿轮装置 - 	机械蜘蛛洞穴  	  70级-80级"), 
										Array(702010000,0,"东方神州   	-   	山脚              80级-90级"), 
										Array(220060000,0,"玩具城   	-       扭曲的时间1       90级-100级"), 
										Array(541010010,0,"新加坡 	-       幽灵船 2     	  90级-100级"), 
										Array(220060200,0,"玩具城   	-       扭曲的时间3       100级-110级"), 
										Array(220060201,0,"玩具城   	-   	时间异常之地      110级-120级"), 
										Array(240040510,0,"神木村   	-   	死龙巢穴       	  >120级以上"),
										Array(240040510,0,"冰峰雪域   	-   	城墙下1      	  >150级以上")
										); 

var bossmaps = Array( 
100020401,//僵尸蘑菇的巢穴
100020301,//蓝蘑菇王的巢穴
100020101,//蘑菇王的巢穴  
230040420,//皮亚奴斯洞穴
220080000,//时间塔的本源
240020402,//喷火龙栖息地
240020102,//格瑞芬的森林   
551030100,//阴森世界
240040700,//暗黑龙王栖息地
702070400);//藏经主场

var selectedMap = -1;
var selectedArea = -1;

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status >= 2 || status == 0) {
            cm.dispose();
            return;
        }
        status--;
    }

    if (status == 0) {
        cm.sendSimple("您好 #r#h ##k 请选择您要传送的项目:\r\n#b#L0#城镇传送#l\r\n#L1#练级传送#l\r\n#L4#BOSS传送#l#k\r\n#L5#美洲豹栖息地(抓豹子)#l");//\r\n#L3#网吧地图#l
    } else if (status == 1) {
	var selStr = "请选择您的目的地: #b";
    	if (selection == 0) {
            	for (var i = 0; i < maps.length; i++) {
                	selStr += "\r\n#L" + i + "##m" + maps[i] + "# #l";
            	}
        } else if (selection == 2) {
            	cm.dispose();
            	cm.openNpc(9010022);
            	return;
        } else if (selection == 3) {
            	cm.dispose();
            	cm.openNpc(9070007);
            	return;
	} else if (selection == 4) {
		for (var i = 0; i < bossmaps.length; i++) {
                	selStr += "\r\n#L" + i + "##m" + bossmaps[i] + "# #l";
            	}
	} else if (selection == 5) {
		if(cm.getJob() >= 3300 && cm.getJob() <= 3312){
			cm.teachSkill(24000003, 1, 1);
			cm.warp(931000500,0);
			cm.dispose();
			return;
		}else {
			cm.sendOk("你需要抓豹子?");
			cm.dispose();
		}
		
		
        } else {
                       for (var i = 0; i < monstermaps.length; i++) {
				selStr += "\r\n#L" + i + "#" + monstermaps[i][2] + "";
                       }
	} 
        selectedArea = selection;
        cm.sendSimple(selStr);
    } else if (status == 2) {
        //cm.sendYesNo("看来这里的事情都已经处理完了啊。您真的要移动到 #m" + (selectedArea == 0 ? maps[selection] : monstermaps[selection]) + "# 吗？");
	cm.sendYesNo("看来这里的事情都已经处理完了啊。您真的要移动吗？");
        selectedMap = selection;
    } else if (status == 3) {
        if (selectedMap >= 0) {
		if(selectedArea == 0){
			cm.warp(maps[selectedMap],0);
		}else if(selectedArea == 4){
			cm.warp(bossmaps[selectedMap],0);
		}else{
			cm.warp(monstermaps[selectedMap][0],0);
		}
            //cm.warp(selectedArea == 0 ? maps[selectedMap] : monstermaps[selectedMap], 0);
        }
        cm.dispose();
    } else if (status == 6) {
        
    }
}