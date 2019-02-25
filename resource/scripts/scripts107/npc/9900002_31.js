function start() {
	var text ="\r\n";
	text +="#L0#查看终结者#g人气排行#l#k\r\n";
	text +="#L1#查看终结者#d[家族排行]#k\r\n";
	text +="#L2#查看终结者#r[转生排行]#k";
	cm.sendSimpleS (text,2);
	    
}
function action(mode, type, selection) {
	cm.dispose();
	if  (selection == 0) {
        	var a = "#v4032386#"; 
        	a+=cm.rqMing();        
        	cm.sendOk(a);
        	cm.dispose();
    	}else if  (selection == 1) {
        	var a = "#v4032386#"; 
        	a+=cm.guildMing();        
        	cm.sendOk(a);
        	cm.dispose();
    	}else if  (selection == 2) {
        	var a = "#v4032386#"; 
        	a+=cm.paiMing();        
        	cm.sendOk(a);
        	cm.dispose();
	}
}
