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
                
   cm.sendOk("感谢你的光临！");
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
   cm.sendSimple("★★★★★★★★★★★★★★★★★★★★★★★★★★★\r\n           #n#d剩余:#r"+player.GetMoney()+"元宝   #d修为点:#r"+ cm.getPlayer().getDojoPoints() +"点#k\r\n★★★★★★★★★★★★★★★★★★★★★★★★★★★\r\n #r#L0#自由市场#l  #g#L1#元宝充值#l #k #L13#全能转职#l#r #L14#最新副本#l\r\n\r\n #b#L3#超级传送#l  #L4#普通商店#l  #L15#空中加油#l #L5#仓库管理#l \r\n #L6#美容美发#l  #L8#稀有点装#l  #g#L16#会员功能#l#k #b#L9#经验修复#l \r\n #L10#我要转生#l  #L11#购买会员#l  #L12##r技能全满#l #L20##r潮人新品#l\r\n #L22##r卷轴购买#l  #L23##b购买点卷#l  #L24##b我要修仙#l\r\n\r\n#L21##r修为点换购#l")
    } else if (status == 1) {
           if (selection == 0) {
			   if(cm.getChar().getMapId() >=910000000 && cm.getChar().getMapId() <=910000022) {
			   cm.sendOk("你已经在自由市场,无需传送!!");
			   }else{
			   cm.warp(910000000);
			   }
           cm.dispose(); 
    }else if  (selection == 1) {
	   cm.sendTVlink("");
	   cm.sendOk("正在打开网站.请进行充值!!");
	   cm.dispose();
    }else if  (selection == 2) {//转职
	   cm.openNpc(9310057);
    }else if  (selection == 3) {//传送      
           cm.openNpc(9310059);		           
    }else if  (selection == 4) {//装备
	   cm.openShop(223);
	   cm.dispose(); 
    }else if  (selection == 15) {//装备
	   cm.openShop(225); 
	   cm.dispose();
    }else if  (selection == 5) {//仓库
           cm.openNpc(9030100); 
    }else if  (selection == 6) {//美发
	   cm.openNpc(9000083); 
    }else if  (selection == 7) {//说话颜色     
           cm.openNpc(9310074);  	     
    }else if  (selection == 8) {//介绍
           cm.openNpc(9310070);  
    }else if  (selection == 10) {//介绍
           cm.openNpc(2112003); 
    }else if  (selection == 11) {//介绍
           cm.openNpc(9105004);   
    }else if  (selection == 12) {//介绍
           cm.openNpc(9330092);    
    }else if  (selection == 13) {//介绍
           cm.openNpc(9310057);  
    }else if  (selection == 33) {//介绍
           cm.openNpc(9010023);        
    }else if  (selection == 14) {//介绍
           cm.openNpc(1061009);   
    }else if  (selection == 16) {//介绍
           cm.openNpc(2151008); 
    }else if  (selection == 20) {//介绍
           cm.openNpc(2159008); 
    }else if  (selection == 21) {//介绍
           cm.openNpc(9310071); 
    }else if  (selection == 22) {//卷轴
           cm.openNpc(1013104);
    }else if  (selection == 23) {//卷轴
           cm.openNpc(9000041);
    }else if  (selection == 24) {//xiuxian
           cm.openNpc(2084001);
    }else if  (selection == 9) {//经验修复
	   var statup = new java.util.ArrayList();
	   var p = cm.c.getPlayer();
	   if(p.getExp() < 0){
		   p.setExp(0)
		   statup.add (org.tools.Pair(net.sf.odinms.client.MapleStat.EXP, java.lang.Integer.valueOf(0))); 
		   p.getClient().getSession().write(net.sf.odinms.tools.MaplePacketCreator.updatePlayerStats(statup,cm.getChar().getJob().getId()));
		   cm.sendOk("经验值已修复完成");
		   cm.dispose();
	   }else{
		   cm.sendOk("您的经验值正常,无需修复!");
		   cm.dispose();
	   }
    }else if  (selection == 10) {
	cm.getChar().maxAllSkill();
	cm.sendOk("你已经满技能了!!!");
	cm.dispose();
}
}
}
}


