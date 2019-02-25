function start() {
	var text ="你好.我是专门统计本服排行信息的,你是否上榜了呢?\r\n";
	text +="#L0#查看终结者#g[人气排行]#l#k\r\n";
	text +="#L1#查看终结者#d[家族排行]#k\r\n";
	text +="#L2#查看终结者#r[转生排行]#k\r\n";
	text +="#L3#查看终结者#r[情侣排行]#k";
	cm.sendSimpleS (text,2);
	    
}
function action(mode, type, selection) {
	cm.dispose();
	if  (selection == 0) {
        	var a = "　　　　　 #fEffect/SetItemInfoEff/1/8#\r\n#e#r#fEffect/ItemEff/1112811/0/0#终结者全服第一人气玩家#fEffect/ItemEff/1112811/0/0##n#k\r\n#fEffect/ItemEff/1112312/0/3#"; 
        	a+=cm.rqMing();        
        	cm.sendOkS(a,2);
        	cm.dispose();
    	}else if  (selection == 1) {
        	var a = "　　　　　 #fEffect/SetItemInfoEff/1/8#\r\n#e#r#fEffect/ItemEff/1112811/0/0#终结者全服第一家族#fEffect/ItemEff/1112811/0/0##n#k\r\n#fEffect/ItemEff/1112312/0/3#"; 
        	a+=cm.guildMing();        
        	cm.sendOkS(a,2);
        	cm.dispose();
    	}else if  (selection == 2) {
        	var a = "　　　　　 #fEffect/SetItemInfoEff/1/8#\r\n#e#r#fEffect/ItemEff/1112811/0/0#终结者全服第一转生玩家#fEffect/ItemEff/1112811/0/0##n#k\r\n#fEffect/ItemEff/1112312/0/3#"; 
        	a+=cm.paiMing();        
        	cm.sendOkS(a,2);
        	cm.dispose();
    	}else if  (selection == 3) {
        	var a = "　　　　　 #fEffect/SetItemInfoEff/1/8#\r\n#e#r#fEffect/ItemEff/1112312/0/3#终结者全服第一恩爱情侣#fEffect/ItemEff/1112312/0/3##n#k\r\n"; 
        	a+=cm.marriageMing();        
        	cm.sendOkS(a,2);
        	cm.dispose();
	}
}
