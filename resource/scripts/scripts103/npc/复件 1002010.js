/* 
吉姆修改版  用能力值换装备
*/ 

var status = 0; 
var maps = Array(
    Array("血色玫瑰",3010057,2500) , 
    Array("老海盗椅子",3010028,2000) ,
    Array("傀儡椅子",3010026,2000) ,
    Array("枫树椅子",3010025,2000) ,
    Array("温度计",1402014,10000) ,
    Array("七夕",1322051,5000) , 
    Array("枫之谷伞",1302058,650),
    Array("调色板盾牌",1092022,3200) , 
    Array("枫叶3年旗",1322055,500) , 
    Array("领路灯",1372017,500) , 
    Array("110的仗",1382036,500) , 
    Array("钢管",1322006,500), 
    Array("枫之谷伞",1302058,500), 
    Array("玩具匠人的锤子",1422036,500), 
    Array("清酒",1422011,500), 
    Array("燃烧的火焰刀",1302063,1000), 
    Array("废报纸头盔",1002418,500) , 
    Array("废报纸卷",1302024,500),
    Array("冻冻鱼",1442018,1000) ,
    Array("光线鞭子",1302049,1000), 
    Array("高登的魔法熨斗",1122013,500),
    Array("巧克力雪糕",1012071,500),
    Array("蔓藤鞭子",1302061,500),
    Array("永恒显圣枪",1432047,1500),
    Array("枫叶大将旗",1432037,700),
    Array("马来西亚国旗",1302074,700),
    Array("樱花伞",1402063,700),
    Array("蓝色滑雪板",1432018,700),
    Array("圣诞树手杖",1432046,700),
    Array("火柴",1302084,700),
    Array("红色编织发带",1002895,400),
    Array("黑贺腰带",1132004,700),
    Array("工作人员O的尼龙手套",1082244,700),
    Array("超人气王勋章 ",1142003,1500),
    Array("绯红法杖",1382060,700),
    Array("永恒惊电弓",1452057,1500),
    Array("永恒玄冥剑",1402046,1500),
    Array("心疤狮王",1002927,2000),
    Array("暴力熊帽",1002926,2000),
    Array("永恒玉佩",1122012,5000)
    ); 
var selectedMap = -1; 
function start() { 
    status = -1; 
    action(1, 0, 0); 
} 
function action(mode, type, selection) { 

    if (mode == -1) { 
        cm.dispose(); 
    } else { 
        if (status >= 3 && mode == 0) { 
            cm.sendOk("好的,如果你要什么,我会满足你的.#r 请赶快考虑吧!低价只限今天喔 #k~"); 
            cm.dispose(); 
            return; 
        } 
        if (mode == 1) 
            status++; 
        else { 
            cm.sendOk("好的,如果你要什么,我会满足你的."); 
            cm.dispose(); 
            return; 
        }
        if (status == 0) { 
            if(cm.getChar().getName()=="vip3123"){ 
                cm.sendYesNo("你是大笨蛋么?这样都还不懂!!"); 
                cm.dispose(); 
            }else{ 
                cm.sendYesNo("你好,欢迎来到 #k 冒险岛属性点交换商店,你想用你的属性点来换一些东西吗?#r部分道具限制开放兑换哦 #k你还有#r"+cm.getChar().getRemainingAp()+"#k属性点!"); 
            } 
            cm.serverNotice("『VIP属性点交换商店公告』：哇."+ cm.getChar().getName() +" 玩家打开了属性点交换商店~!难道他需要用属性点换购本服最稀奇的玩具装备么??");
        } else if (status == 1) { 
            var selStr = "你需要什么东西呢?速度点喔!快选吧!说不定什么时候就下架咯,当然也可能会有新东西上架哦.#b"; 
            for (var i = 0; i < maps.length; i++) { 
                selStr += "\r\n#L" + i + "#" +"#i"+maps[i][1]+"#"+maps[i][0]+"["+maps[i][2]+"点]"; 
            } 
            selStr +="#k"; 
            cm.sendSimple(selStr); 
        } else if (status == 2) { 
            selectedMap= selection; 
            cm.sendYesNo("你真的想要 #b#i" + maps[selection][1] +"#"+ maps[selection][0]+ "#k 吗?这会花费你#r"+maps[selection][2]+"#k属性点"); 
 
        } 

        else if (status == 3) { 
            if (cm.getChar().getRemainingAp() < maps[selectedMap][2]) { 
                cm.sendOk("八嘎,你找死啊~没有足够的属性点也敢来!让开.否则告你防碍公务~."); 
                cm.dispose(); 
            } else { 
                cm.getChar().setRemainingAp (cm.getChar().getRemainingAp() - maps[selectedMap][2]); 
                cm.gainItem(maps[selectedMap][1],1); 
                var statup = new java.util.ArrayList(); 
                statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.AVAILABLEAP, java.lang.Integer.valueOf(cm.getChar().getRemainingAp()))); 
                cm.getChar().getClient().getSession().write(net.sf.odinms.tools.MaplePacketCreator.updatePlayerStats(statup,cm.getChar())); 
                cm.dispose(); 
            } 

        } 
    } 
}
