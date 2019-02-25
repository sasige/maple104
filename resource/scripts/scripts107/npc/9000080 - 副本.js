/**
	脚本类型: 		NPC
           制作：                     一纸离人醉丶
           技术指导：              芬芬时尚潮流
 **/

function start() {

    if(cm.countMonster()< 1) {
        cm.sendSimple ("我是僧人达奥,如果你给我一个#v4001433#我就帮你召唤拉瓦那.\r\n请根据自身等级酌情选择BOSS级别.\r\n#L0#初级拉瓦那 - 建议80以上#l\r\n#L1#中级拉瓦那 - 建议100以上#l\r\n#L2#高级拉瓦那 - 建议120以上#l\r\n#L3#终极拉瓦那 - 建议140以上#l\r\n#L4#离开");
    } else {
        cm.sendOk("一次不能出现两只BOSS,请离开后重新进入.\r\n#L4#离开")
    }
}
function action(mode, type, selection) {
    cm.dispose();
    if (selection == 0) {//初级
    if (cm.haveItem(4001433) < 1 ){  
        cm.sendOk("你必须有#v4001433#我才能帮你召唤.")
        cm.dispose();
       }else{	
        cm.gainItem(4001433,-1);
	cm.changeMusic("Bgm10/Eregos");
        cm.spawnMob(9500390, 908, 513);   
        cm.dispose();
        }
} else if (selection == 1) {//中级
    if (cm.haveItem(4001433) < 1 ){  
        cm.sendOk("你必须有#v4001433#我才能帮你召唤.")
        cm.dispose();
}else{	
        cm.gainItem(4001433,-1);
        cm.changeMusic("Bgm10/Eregos");
        cm.spawnMob(9500391, 908, 513);   
        cm.dispose();
        }
} else if (selection == 3) {//高级
    if (cm.haveItem(4001433) < 1 ){  
        cm.sendOk("你必须有#v4001433#我才能帮你召唤.")
        cm.dispose();
}else{	
        cm.gainItem(4001433,-1);
        cm.changeMusic("Bgm10/Eregos");
        cm.spawnMob(9500392, 908, 513);   
        cm.dispose();
        }
} else if (selection == 2) {//终极
    if (cm.haveItem(4001433) < 1 ){  
        cm.sendOk("你必须有#v4001433#我才能帮你召唤.")
        cm.dispose();
}else{	
        cm.gainItem(4001433,-1);
        cm.changeMusic("Bgm10/Eregos");
        cm.spawnMob(8800200, 908, 513);   
        cm.dispose();
        }
} else if (selection == 4) {//离开地图
            cm.warp(252020000, 0);
            cm.dispose();				
        }
    }