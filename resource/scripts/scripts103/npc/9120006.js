// Fitness JQ warper
// MrDk/Useless

var status = 0;  

function start() {  
    status = -1;  
    action(1, 0, 0);  
}  

function action(mode, type, selection) {  
       
    if (mode == -1) {  
        cm.dispose();  
    }  
    else {   
        if (status >= 2 && mode == 0) {   
            cm.sendOk("好吧，下次再见！");   
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
            cm.sendNext("欢迎您的到来！");
        }
        else if (status == 1) {
            cm.sendNext("一些规则，你必须清楚\r\n你的等级不能#r低于#k30\r\n如果你死了, 你就不能复活\r\n一旦死亡，你要等待最新通知才能复活");
        } 
        else if (status == 2) { 
            if (cm.getLevel() >= 30) {
                cm.sendSimple("好吧，现在你已经读过规则了，请选择吧！\r\n#L0#带我到等待室!#l\r\n#L1#算了，还是不去了！");  
            }
        }
        else if (status == 3) { 
            if (selection == 0) {  
                cm.sendOk("哦！之前，我忘了，这里是你的票！你可以使用这张票在彼得罗收到一个特殊的奖励！,");  
                cm.gainItem(5220001, -cm.itemQuantity(5220001));
                cm.gainItem(5220001, 1);  
                cm.warp(10904000, 0); 
                cm.dispose();  
            }  
            else if (selection == 1) {  
                cm.sendOk("好吧，下次再见！");  
                cm.dispose();  
            } 
        }  
    } 
}  