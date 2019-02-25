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
		
            var add = "欢迎来到#r◆◇◆"+cm.GetSN()+"#k,这里是本服任务系统,";

            add += "本服任务多多,不开放垃圾副本,本服所有任务在盛大基础上进行强化,奖励丰富";

            add += "为我们的冒险增加了无限乐趣,赶快邀请你的朋友一起加入吧.\r\n\r\n#r";

            add += "" + sss + "\r\n ";

            add += "#L13#"+xxx+""+ttt+"扎昆跳跳#l#b      #L14#"+ttt+""+xxx+"绯红组队#l#r\r\n ";

            add += "#L20#"+ttt+""+xxx+"征战女皇#l#b      #L11#"+xxx+""+ttt+"收集材料#l#r\r\n ";

            add += "#L2#"+xxx+""+ttt+"我要刷钱#l#b      #L1#"+ttt+""+xxx+"挑战春哥#l#r\r\n ";

            add += "#L6#"+ttt+""+xxx+"武陵道场#l#b      #L12#"+xxx+""+ttt+"狮子王任务#l#r\r\n ";

            add += "#L10#"+xxx+""+ttt+"幽灵船闯关#l#b    #L9#"+ttt+""+xxx+"挑战少林妖僧#l#b\r\n ";

            add += "#L8#"+ttt+""+xxx+"家族副本#l#b      #L7#"+xxx+""+ttt+"地狱大公任务#l#l#b\r\n";

            add += " #L15#"+ttt+""+xxx+"独创黑凤任务#l#b  #L16#"+xxx+""+ttt+"恶魔女侍副本#l#l#b\r\n";

            add += " #L17#"+ttt+""+xxx+"蜗牛爷爷副本#l#b  #L18#"+xxx+""+ttt+"冰骑士组队副本#l#l#l#b\r\n";

            add += " #L21#"+ttt+""+xxx+"御龙传说副本#l#b   #L19#"+xxx+""+ttt+"牛逼的脑子副本#l#l#l#b\r\n";

            add += " #L24#"+ttt+""+xxx+"牛逼的脑子副本#l#b       #L25#"+xxx+""+ttt+"挑战都纳斯#l#l#l#b\r\n";

            add += " #L26#"+ttt+""+xxx+"碧拉姐不是个传说#l#b#L27#"+xxx+""+ttt+"▲▲bigbang▲▲#l#l#l#b\r\n";


            cm.sendSimple (add);    

        } else if (status == 1) {


            if (selection == 0) {
                cm.warp(221024500,1);
                cm.dispose();

            } else if(selection == 1) {
                cm.openNpc(9020000);

            } else if(selection == 2) {
                cm.openNpc(9050002);

            } else if(selection == 3) {
                cm.openNpc(9020000);


            } else if(selection == 4) {
                cm.openNpc(9020000);


            } else if(selection == 5) {
                cm.openNpc(9020000);


            } else if(selection == 6) {
                cm.openNpc(2091006);

            } else if(selection == 7) {
                cm.warp(677000013,0);
                cm.dispose();

            } else if (selection == 20) { 
                cm.openNpc(9050001);

            } else if (selection == 19) { 
                cm.openNpc(1096007);

            } else if(selection == 8) {
                cm.openNpc(2010007);

            } else if(selection == 14) {
                cm.warp(803001200,0);
                cm.dispose();

            } else if(selection == 21) {
                cm.openNpc(1013106);

            } else if(selection == 25) {
                cm.openNpc(2142000);
            } else if(selection == 26) {
                cm.openNpc(9001000);
            } else if(selection == 27) {
                cm.openNpc(2152019);

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
                cm.dispose();

            } else if(selection == 13) {
                cm.warp(280020001,0);
                cm.dispose();

            } else if(selection == 15) {
                cm.openNpc(1012113);
            } else if(selection == 16) {
                cm.openNpc(9050005);
            } else if(selection == 17) {
                cm.openNpc(1092094);
            } else if(selection == 24) {
                cm.openNpc(2159001);
            } else if(selection == 18) {
                cm.openNpc(2060009);
                cm.dispose();









            }					
        }
    }
}

