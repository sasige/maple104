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
   cm.sendSimple("★★★★★★★★★★★★★★★★★★★★★★★★★★★\r\n           #n#d剩余:#r"+player.GetMoney()+"元宝   #d闯关积分:#r"+ cm.getPlayer().getDojoPoints() +"点#k\r\n★★★★★★★★★★★★★★★★★★★★★★★★★★★\r\n#r  #L1##v4001558#椅子贩卖①#l#r #L2##v4001559#椅子贩卖② \r\n #L3##v4001560#椅子贩卖③#l  #L4##v4001561#玩具武器☆#l  \r\n\r\n   #L5#稀有披风#l #L6#稀有帽子#l #L7#商城物品#l")
    } else if (status == 1) {
           if (selection == 0) {
			   if(cm.getChar().getMapId() >=910000000 && cm.getChar().getMapId() <=910000022) {
			   cm.sendOk("你已经在自由市场,无需传送!!");
			   }else{
			   cm.warp(910000000);
			   }
           cm.dispose(); 
    }else if  (selection == 1) {
           cm.openNpc(2121020); 
    }else if  (selection == 2) {//介绍
           cm.openNpc(9270056); 
    }else if  (selection == 3) {//介绍
           cm.openNpc(2120001); 
    }else if  (selection == 4) {//介绍
           cm.openNpc(2150008); 
    }else if  (selection == 5) {//介绍
           cm.openNpc(2150010); 
    }else if  (selection == 6) {//介绍
           cm.openNpc(2150009); 
    }else if  (selection == 7) {//介绍
           cm.openNpc(1033233); 
    }else if  (selection == 8) {//介绍
           cm.openNpc(9000020); 
}
}
}
}


