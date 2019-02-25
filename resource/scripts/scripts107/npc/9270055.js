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
   cm.sendSimple("#e您好，欢迎来到#r冒险岛的世界#k，我是BT装备售货员:\r\n#b注意:以下的交易都需要#r#e元宝#n#b,如果你还不知如何获得,请到官方充值吧！\r\n#r注意：bt装备请戴在身上，留在包里会被爆哦，32767不能进行咋卷!\r\n#L1#∝╬═→我要购买全属性的BT帽子#l\r\n#L2#∝╬═→我要购买全属性的BT勋章#l\r\n#L3#∝╬═→我要购买全属性的BT披风#l\r\n#L4#∝╬═→我要购买全属性的BT耳环#l\r\n#L5#∝╬═→我要购买全属性的BT戒指#l\r\n#L6#∝╬═→我要购买全属性的BT项链#l\r\n#L7#∝╬═→我要购买全属性的BT腰带#l\r\n#L8#∝╬═→我要购买全属性的BT褐手#l");
    } else if (status == 1) {
           if (selection == 0) {
      cm.sendOk("#ewww.xf4060.com");
            cm.dispose();
    }else if  (selection == 1) {
           cm.openNpc(1300006);
    }else if  (selection == 2) {
           cm.openNpc(1300007);
    }else if  (selection == 3) {
           cm.openNpc(1300009); 
    }else if  (selection == 4) {
           cm.openNpc(1300010); 
    }else if  (selection == 5) {
           cm.openNpc(1300003); 
    }else if  (selection == 6) {
           cm.openNpc(1200001); 
    }else if  (selection == 7) {
           cm.openNpc(1200005); 
    }else if  (selection == 8) {
           cm.openNpc(9330065); 
    }else if  (selection == 9) {
           cm.openNpc(9330076); 
    }else if  (selection == 10) {
           cm.openNpc(2101018); 
    }else if  (selection == 11) {
           cm.openNpc(9310084); 
          

}
}      
}
}



