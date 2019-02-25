function start() {
    cm.sendSimple ("#r^^^^^^^^^^^^^^俺系大嘴巴专门卖喇叭^^^^^^^^^^^^\r\n#L0##b5000元宝#k#v5079002##r 50个#b#k#l  #L1##b5000元宝#k#v5079001##r 50个#b#k\r\n#L2##b5000元宝#k#v5076000##r 50个#b#k#l  #L3##b5000元宝#k#v5074000##r 50个#b#k\r\n#L10##b9000元宝#k#v5390000##r 30个#b#k#l#L11##b9000元宝#k#v5390002##r 30个#b#k\r\n#L4##b9000元宝#k#v5390003##r 20个#b#k#L5##b9000元宝#k#v5390004##r 20个#b#k\r\n#L6##b9999元宝#k#v5390005##r 20个#b#k#L7##b9999元宝#k#v5390007##r 20个#b#k\r\n#L8##b9999元宝#k#v5390008##r 20个#b#k#L9##b9999元宝#k#v5390010##r 20个#b#k")
    }

function action(mode, type, selection) {
        cm.dispose();

    switch(selection){

        case 0: 
            if(cm.getChar().GetMoney() >= 5000){
            cm.sendOk("人民币喇叭已经放在你包里了哦！ #r游戏愉快!#k");
            player.GainMoney(-5000);
            cm.gainItem(5079002, 50);
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了50个高级馅饼喇叭，购买请看雪精灵！");
	    cm.dispose();        
            }
            else{    
            cm.sendOk("请确认你有5000元宝的时候在来兑换喇叭!");
                cm.dispose();     };
        break;  
        case 1: 
            if(cm.getChar().GetMoney() >= 5000){
            cm.sendOk("人民币喇叭已经放在你包里了哦！ #r游戏愉快!#k");
            player.GainMoney(-5000);
            cm.gainItem(5079001, 50);
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了50个高级蛋糕喇叭，购买请看雪精灵！");
	    cm.dispose();        
            }
            else{    
            cm.sendOk("请确认你有5000元宝的时候在来兑换喇叭!");
                cm.dispose();     };
        break;  
        case 2: 
            if(cm.getChar().GetMoney() >= 5000){
            cm.sendOk("人民币喇叭已经放在你包里了哦！ #r游戏愉快!#k");
            player.GainMoney(-5000);
            cm.gainItem(5076000, 50);
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了50个道具喇叭，购买请看雪精灵！");
	    cm.dispose();        
            }
            else{    
            cm.sendOk("请确认你有5000元宝的时候在来兑换喇叭!");
                cm.dispose();     };
        break;
        case 3: 
            if(cm.getChar().GetMoney() >= 5000){
            cm.sendOk("人民币喇叭已经放在你包里了哦！ #r游戏愉快!#k");
            player.GainMoney(-5000);
            cm.gainItem(5074000, 50);
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了50个白骨喇叭，购买请看雪精灵！");
	    cm.dispose();        
            }
            else{    
            cm.sendOk("请确认你有5000元宝的时候在来兑换喇叭!");
                cm.dispose();     };
        break;


        case 4: 
            if(cm.getChar().GetMoney() >= 9000){
            cm.sendOk("人民币喇叭已经放在你包里了哦！ #r游戏愉快!#k");
            player.GainMoney(-9000);
            cm.gainItem(5390003, 20);
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了20个新年喇叭1，购买请看雪精灵！");
	    cm.dispose();        
            }
            else{    
            cm.sendOk("请确认你有9000元宝的时候在来兑换喇叭!");
                cm.dispose();     };
        break; 

        case 5: 
            if(cm.getChar().GetMoney() >= 9000){
            cm.sendOk("人民币喇叭已经放在你包里了哦！ #r游戏愉快!#k");
            player.GainMoney(-9000);
            cm.gainItem(5390004, 20);
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了20个新年喇叭2，购买请看雪精灵！");
	    cm.dispose();        
            }
            else{    
            cm.sendOk("请确认你有9000元宝的时候在来兑换喇叭!");
                cm.dispose();     };
        break;   

        case 6: 
            if(cm.getChar().GetMoney() >= 9999){
            cm.sendOk("人民币喇叭已经放在你包里了哦！ #r游戏愉快!#k");
            player.GainMoney(-9999);
            cm.gainItem(5390005, 20);
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了20个小老虎喇叭，购买请看雪精灵！");
	    cm.dispose();        
            }
            else{    
            cm.sendOk("请确认你有9999元宝的时候在来兑换喇叭!");
                cm.dispose();     };
        break; 
  
        case 7: 
            if(cm.getChar().GetMoney() >= 9999){
            cm.sendOk("人民币喇叭已经放在你包里了哦！ #r游戏愉快!#k");
            player.GainMoney(-9999);
            cm.gainItem(5390007, 20);
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了20个球进了喇叭，购买请看雪精灵！");
	    cm.dispose();        
            }
            else{    
            cm.sendOk("请确认你有9999元宝的时候在来兑换喇叭!");
                cm.dispose();     };
        break; 
        case 8: 
            if(cm.getChar().GetMoney() >= 9999){
            cm.sendOk("人民币喇叭已经放在你包里了哦！ #r游戏愉快!#k");
            player.GainMoney(-9999);
            cm.gainItem(5390008, 20);
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了20个世界杯喇叭，购买请看雪精灵！");
	    cm.dispose();        
            }
            else{    
            cm.sendOk("请确认你有9999元宝的时候在来兑换喇叭!");
                cm.dispose();     };
        break;
        case 9: 
            if(cm.getChar().GetMoney() >= 9999){
            cm.sendOk("人民币喇叭已经放在你包里了哦！ #r游戏愉快!#k");
            player.GainMoney(-9999);
            cm.gainItem(5390010, 20);
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了20个鬼出没喇叭，购买请看雪精灵！");
	    cm.dispose();        
            }
            else{    
            cm.sendOk("请确认你有9999元宝的时候在来兑换喇叭!");
                cm.dispose();     };
        break;  

        case 10: 
            if(cm.getChar().GetMoney() >= 9000){
            cm.sendOk("人民币喇叭已经放在你包里了哦！ #r游戏愉快!#k");
            player.GainMoney(-9000);
            cm.gainItem(5390000, 30);
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了30个炽热喇叭，购买请看雪精灵！");
	    cm.dispose();        
            }
            else{    
            cm.sendOk("请确认你有90000元宝的时候在来兑换喇叭!");
                cm.dispose();     };
        break; 

        case 11: 
            if(cm.getChar().GetMoney() >= 9000){
            cm.sendOk("人民币喇叭已经放在你包里了哦！ #r游戏愉快!#k");
            player.GainMoney(-9000);
            cm.gainItem(5390002, 30);
            cm.serverNotice(5,"[冒险岛公告]大老板:"+cm.getChar().getName()+" 购买了30个爱心喇叭，购买请看雪精灵！");
	    cm.dispose();        
            }
            else{    
            cm.sendOk("请确认你有90000元宝的时候在来兑换喇叭!");
                cm.dispose();     };
        break; 
        }
    }
