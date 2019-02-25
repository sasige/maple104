function start() {

if(cm.getChar().getVip() >= 1 && cm.getChar().getMapId() == 220080001 && cm.countMonster()<1) {
    cm.sendSimple ("#v4031179##v4031179##v4031179##v4031179##v4031179##v4031179##v4031179##v4031179##v4031179#\r\n#b召唤闹钟王\r\n我来帮你把它拽出来吧,不过得给我钱\r\n10E很公道了吧\r\n请选择你要虐的BOSS.  #l#k\r\n #L110#（市场）操你妹打个BOSS还要钱哥闪了~！#l \r\n#l#k\r\n                 #r10E BOSS对抗#l#b\r\n             #L0#召唤闹钟王BOSS#l#l\r\n\r\n\r\n#v4031179##v4031179##v4031179##v4031179##v4031179##v4031179##v4031179##v4031179##v4031179#");
    } else {
    cm.sendOk("请满足以下条件才可以使用\r\n\r\n#r#e不要再找个地图召唤过多的boss（闹钟王）")
    }
}
function action(mode, type, selection) {
        cm.dispose();
    

    if (selection == 199) {
        cm.warp(910000014, 0);
    } else if (selection == 200) {
        cm.warp(910000000, 0);
    } else if (selection == 201) {
        if (cm.getMeso() >= 50000000){
				cm.gainMeso(-50000000);
				cm.sendOk("购买成功.");
				cm.gainItem(2340000,1);
				cm.dispose();
				}else{
					cm.sendOk("购买失败，你可能没有5000万冒险币.");
     cm.dispose();
    } 
}

if (cm.getMeso() >= 10000000) {

cm.dispose();
        
    if (selection == 202) {
        cm.gainMeso(-10000000);
        //怪物ID, HP, EXP, 数量
        cm.summonMob(8500001, 10000000, 5000000, 1);//闹钟
    } else if (selection == 203) {
        cm.gainMeso(-10000000);
        cm.summonMob(8810026, 100000000, 8000000, 1);//黑龙
    
    } else if (selection == 204) {
        cm.gainMeso(-10000000);
        cm.summonMob(9400205, 100000, 10000, 15);//蓝蘑菇王15个
   
              
        
   
		cm.dispose();
    } 
}




if (cm.getMeso() >= 1000000000) {

cm.dispose();
        
    if (selection == 0) {
        cm.gainMeso(-1000000000);
        //怪物ID, HP, EXP, 数量
        cm.summonMob(8500001,2100000000,2100000000, 1);     
cm.serverNotice("『BOSS系统』：【"+ cm.getChar().getName() +"】已经召唤出帕普拉图斯的座钟,拍卖超级传送进 入");
    } else if (selection == 1) {
        cm.gainMeso(-1000000);
        cm.summonMob(9420547, 150000000, 1150, 1);//蝙蝠魔(觉醒)
cm.serverNotice("『BOSS系统』：【"+ cm.getChar().getName() +"】已经召唤出龌龊的爱巴狮,拍卖超级传送进入");
    } else if (selection == 2) {
        cm.gainMeso(-1000000);
        cm.summonMob(9400572, 75000000, 1150, 1);//蝙蝠魔(卐解)
    } else if (selection == 3) {
        cm.gainMeso(-1000000);
        cm.summonMob(9400536, 75000000, 1150, 1);//蝙蝠魔(咒印)
    } else if (selection == 4) {
        cm.gainMeso(-1000000);
        cm.summonMob(9400120, 18000000, 1150, 1);//男老板 
    } else if (selection == 5) {
        cm.gainMeso(-1000000);
        cm.summonMob(9400121, 75000000, 1150, 1);//女老板 
    } else if (selection == 6) {
        cm.gainMeso(-1000000);
        cm.summonMob(9400112, 400000000, 1150, 1);//保镖A 
    } else if (selection == 7) {
        cm.gainMeso(-1000000);
        cm.summonMob(9400113, 500000000, 1150, 1);//保镖B
    } else if (selection == 8) {
        cm.gainMeso(-1000000);
        cm.summonMob(9400300, 150000000, 1150, 1);//恶僧
    } else if (selection == 9) {
        cm.gainMeso(-1000000);
        cm.summonMob(9400549, 3500000, 1150, 1);//火马
    } else if (selection == 10) {
        cm.gainMeso(-1000000);
        cm.summonMob(8180001, 3700000, 1150, 1);//天鹰
    } else if (selection == 11) {
        cm.gainMeso(-1000000);
        cm.summonMob(8180000, 3700000, 1150, 1);//火焰龙
    } else if (selection == 12) {
        cm.gainMeso(-1000000);
        cm.summonMob(9300012, 18000000, 1150, 1);//阿丽莎乐
    } else if (selection == 13) {
        cm.gainMeso(-1000000);
        cm.summonMob(8220001, 18000000, 1150, 1);//驮狼雪人
    } else if (selection == 108) {
        cm.warp(910500000, 0);	
	} else if (selection == 109) {
        cm.warp(910000001, 0);
    } else if (selection == 110) {
        cm.warp(910000000, 0);
	} else if (selection == 111) {
        cm.warp(220080001, 0);
	} else if (selection == 112) {
        cm.warp(280030000, 0);
	} else if (selection == 113) {
        cm.warp(240060200, 0);
	} else if (selection == 114) {
        cm.openShop (2015);
		cm.dispose();
    } 
}

if (cm.getMeso() >= 5000000) {

cm.dispose();
        
    if (selection == 41) {
        cm.gainMeso(-1000000);
        cm.summonMob(9500167, 18000, 500, 10);//金猪
    } else if (selection == 42) {
        cm.gainMeso(-1000000);
        cm.summonMob(6130207, 16700, 1200, 10);//猿公
    } else if (selection == 43) {
        cm.gainMeso(-1000000);
        cm.summonMob(4230102, 18500, 1500, 10);//大幽灵 
    } else if (selection == 44) {
        cm.gainMeso(-5000000);
        cm.summonMob(9001000, 1800000, 450, 5);//教官
        cm.summonMob(9001001, 1800000, 450, 5);
        cm.summonMob(9001002, 1800000, 450, 5);
        cm.summonMob(9001003, 1800000, 450, 5);
    } else if (selection == 45) {
        cm.gainMeso(-5000000);
        cm.summonMob(100100, 40000, 3000, 4);//绿蜗牛 
    } else if (selection == 46) {
        cm.gainMeso(-5000000);
        cm.summonMob(7130001, 75000, 110, 20);//猎犬
    } else if (selection == 47) {
        cm.gainMeso(-5000000);
        cm.summonMob(8140500, 1500000, 1750, 10);//火焰猎犬
    } else if (selection == 48) {
        cm.gainMeso(-5000000);
        cm.summonMob(7130200, 180000, 1000, 10);//红狼
    } else if (selection == 49) {
        cm.gainMeso(-5000000);
        cm.summonMob(8140000, 500000, 1000, 5);//白狼
    } else if (selection == 50) {
        cm.gainMeso(-5000000);
        cm.summonMob(8140100, 500000, 1000, 8);//企鹅王与黑雪人 
    } else if (selection == 51) {
        cm.gainMeso(-5000000);
        cm.summonMob(8140103, 1800000, 1050, 1);//寒冰半人马
    } else if (selection == 52) {
        cm.gainMeso(-5000000);
        cm.summonMob(8140101, 2000000, 1200, 1);//暗黑半人马
    } else if (selection == 53) {
        cm.gainMeso(-5000000);
        cm.summonMob(8810020, 30000000, 1000, 8);//蓝飞龙 
    } else if (selection == 54) {
        cm.gainMeso(-5000000);
        cm.summonMob(8810021, 30000000, 1000, 8);//黑飞龙
    } else if (selection == 55) {
        cm.gainMeso(-5000000);
        cm.summonMob(8150201, 5000000, 1500, 5);//邪恶双刀蜥蜴
    } else if (selection == 56) {
        cm.gainMeso(-5000000);
        cm.summonMob(9300077, 20000000, 3000, 5);//骷髅龙
    } else if (selection == 57) {
        cm.gainMeso(-5000000);
        cm.summonMob(8150101, 1800000, 1000, 10);//尖鼻鲨鱼 
    } else if (selection == 58) {
        cm.gainMeso(-5000000);
        cm.summonMob(8142100, 1800000, 1000, 10);//致命乌贼怪 
    } else if (selection == 59) {
        cm.gainMeso(-5000000);
        cm.summonMob(8160000, 4000000, 1150, 10);//时间门神 
    } else if (selection == 60) {
        cm.gainMeso(-5000000);
        cm.summonMob(8170000, 5000000, 1150, 10);//黑甲凶灵
    } else if (selection == 61) {
        cm.gainMeso(-5000000);
        cm.summonMob(8141100, 6000000, 1250, 10);//大海贼王
    } else if (selection == 62) {
        cm.gainMeso(-5000000);
        cm.summonMob(8143000, 6000000, 1250, 10);//时之鬼王 
    } else if (selection == 100) {
        cm.gainMeso(-5000000);
        cm.gainItem(4000019,200);//绿色蜗牛壳 
    } else if (selection == 101) {
        cm.gainMeso(-5000000);
        cm.gainItem(4000000,200);//蓝色蜗牛壳  
    } else if (selection == 102) {
        cm.gainMeso(-5000000); 
        cm.gainItem(4000016,200);//红色蜗牛壳 
        
    } 
    
} else {
        cm.sendSimple ("你的钱不够呀.");
        cm.dispose();
        }
        cm.dispose();
    }