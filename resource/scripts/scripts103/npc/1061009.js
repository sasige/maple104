importPackage(net.sf.odinms.client);
var ttt ="#fUI/UIWindow.img/Quest/icon9/0#";
var xxx ="#fUI/UIWindow.img/Quest/icon8/0#";
var sss ="#fUI/UIWindow.img/QuestIcon/3/0#";


var status = 0;


function start() {
    status = -1;
    action(1, 0, 0);
}
function action(mode, type, selection) {
    if (mode == -1) {
        cm.sendOk("#b好的,下次再见.");
        cm.dispose();
    } else {
        if (status >= 0 && mode == 0) {
            cm.sendOk("#b好的,下次再见.");
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;





        if (status == 0) {
		
            var add = "欢迎来到#r◆◇◆"+cm.GetSN()+"#k,这里是本服任务系统,\r\n\r\n#r";

	    add += "#g#L100#运动会副本#l\r\n\r\n";

            add += "#r#L99#坐骑跳跳#l  #L98#超级搜集#l  #L13#扎昆跳跳#l  #L14#绯红组队#l\r\n";

            add += "#L20##b征战女皇#l  #L11#收集材料#l  #L2#我要刷钱#l  #L1#挑战春哥#l\r\n";

            add += "#L6##r坐骑任务#l  #L12#狮王任务#l  #L15#黑凤任务#l  #L16#恶魔女侍#l\r\n";

            add += "#L17##b蜗牛爷爷#l  #L18#龙背椅子#l  #L21#御龙传说#l  #L29#粉色扎昆#l\r\n";

            add += "#L24##r超级脑子#l  #L23#你猜副本#l  #L26#碧拉传说#l  #L25#战都纳斯#l\r\n";

	    add += "#L10##b幽灵闯关#l  #L9#少林妖僧#l  #L8#家族副本#l  #L7#地狱大公#l\r\n";




            cm.sendSimple (add);    

        } else if (status == 1) {


            if (selection == 0) {
                cm.warp(221024500,1);
                cm.dispose();

            } else if(selection == 1) {
                cm.openNpc(9020000);
            } else if(selection == 99) {
                cm.warp(910130101,0);
                cm.dispose();
            } else if(selection == 98) {
                cm.openNpc(9209103);

            } else if(selection == 2) {
                cm.openNpc(9050002);

            } else if(selection == 3) {
                cm.openNpc(9020000);

            } else if(selection == 29) {
                cm.warp(689010000,0);
                cm.dispose();

            } else if(selection == 4) {
                cm.openNpc(9020000);


            } else if(selection == 5) {
                cm.openNpc(9020000);


            } else if(selection == 6) {
                    cm.openNpc(2095000);


            } else if(selection == 7) {
                cm.warp(677000013,0);
                cm.dispose();

            } else if (selection == 20) { 
                cm.openNpc(9050001);

            } else if(selection == 8) {
                cm.openNpc(2010007);

            } else if(selection == 14) {
                cm.warp(803001200,0);
                cm.dispose();

            } else if(selection == 21) {
                cm.openNpc(1013106);

            } else if(selection == 23) {
		if (cm.haveItem(4032532)||cm.haveItem(4032529)) {
                    cm.sendOk("请把你背包里面的:#v4032532##v4032529#清空.");
                    cm.dispose();
                }else {	
                    cm.openNpc(2159001);}
            } else if(selection == 24) {
                cm.openNpc(1096007);
            } else if(selection == 9) {
                cm.warp(702070400,0);
                cm.dispose();

            } else if(selection == 10) {
                if (cm.getHour() < 16) {
                    cm.sendOk("现在幽灵船的大门还没有打开.");
                    cm.dispose();
                }else if (cm.haveItem(4000382) ||cm.haveItem(4000379) ||cm.haveItem(4000383)) {
                    cm.sendOk("请把你背包里面的:#v4000382##v4000379##v4000383#清空.");
                    cm.dispose();
                }else {	
                    cm.warp(541010010,0);	
                    cm.sendOk("但愿你能打败幽灵船长,请先展现下你的实力吧~收集300个#v4000382#.请注意时间!!!超过24点,任务将视为放弃.!");
                    cm.dispose();
                }
            } else if(selection == 11) {
                cm.openNpc(9300010);

            } else if(selection == 12) {
                cm.warp(211070100,0);
		cm.sendOk("狮王就在前面准备好了吗?");
                cm.dispose();

            } else if(selection == 13) {
                cm.warp(280020001,0);
                cm.dispose();

            } else if(selection == 15) {
                cm.openNpc(1012113);
            } else if(selection == 16) {
                cm.openNpc(9050005);
            } else if(selection == 17) {
		if (cm.haveItem(4161035)) {
                    cm.sendOk("请把你背包里面的:#v4161035#清空.");
                    cm.dispose();
                }else {	
                    cm.openNpc(1092094);}
            } else if(selection == 25) {
                cm.openNpc(2142000);
            } else if(selection == 26) {
                cm.openNpc(9001000);
            } else if(selection == 18) {
                cm.openNpc(9310086);
                cm.dispose();
            } else if(selection == 100) {//运动会副本
                if (cm.getHour() < 12) {
                    cm.sendOk("#r中午12点以后开发本副本\r\n\r\n#b耐心等待,(*^__^*) 嘻嘻……");
                    cm.dispose();
                }else if (cm.haveItem(3994246) ||cm.haveItem(3994247) ||cm.haveItem(3994448) ||cm.haveItem(4001318) ||cm.haveItem(4031954)) {
                    cm.sendOk("请把你背包里面的:#v3994246#,#v3994247#,#v3994448#,#v4001318#,#v4031954#清空.");
                    cm.dispose();
                }else {	
                    cm.warp(910023504,0);	
                    cm.sendOk("#e加油吧.朋友,\r\n#r不抛弃不放弃才能获得更好的道具奖励");
                    cm.dispose();
                }

            } else if(selection == 27) {
                    cm.sendOk("副本暂时没开封");
                    cm.dispose();





            }					
        }
    }
}



