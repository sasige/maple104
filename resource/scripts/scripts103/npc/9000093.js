importPackage(net.sf.odinms.tools);
importPackage(net.sf.odinms.client);



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
        if (mode == 0) {      
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
            var SQMap = cm.getC().getChannelServer().getMapFactory().getMap(922010501);
            if (SQMap.getCharacters().size() == 0) {
                cm.sendYesNo("#b欢迎来到运动会副本\r\n#l#r每天只可以进入2次\n\r\n#k[目前已经进入#r" + cm.getChar().getBossLog('ydh') + "#k次]\r\n#r凌晨刷新#k\r\n[进入需要等级达到Lv.100以上,职业不限] ");


            } else { // 有人在里面
                for (var i = 0; i < 5; i++) {
                    if (SQMap.getMonsterById(922010501 + 1) != null) {  //要是有人则换地图
                        cm.getPlayer().dropMessage("已经开始了战斗,您不能进入.");
                        cm.dispose();

                    }

                }

                for (var i = 0; i < 4; i++) {
                    if (SQMap.getMonsterById(922010501 + 1) != null) {
                        cm.getPlayer().dropMessage("战争已经开始了,您不能进入.");
                        cm.dispose();
                    }
                }
                if (SQMap.getMonsterById(922010501) != null) {
                    cm.getPlayer().dropMessage("战争已经开始了,您不能进入.");
                    cm.dispose();
                }
                else
                    cm.sendYesNo("#v3010313##v3010313##v3010313##v3010313##v3010313##v3010313##v3010313##v3010313#\r\n\r\n\n\r\n#l#r每天只可以进入一次\n\r\n #k[目前已经进入#r" + cm.getChar().getBossLog('ydh') + "#k次]\r\n为现在时间是: " + cm.getHour() + "时:" + cm.getMin() + "分:" + cm.getSec() + "秒,每天凌晨会刷新游戏数据.\r\n[进入需要等级达到Lv.100以上,职业不限]\r\n您确定好了\r\n\r\n#b#v3010313##v3010313##v3010313##v3010313##v3010313##v3010313##v3010313##v3010313#\r\n\r\n  #b     \\n ");
            }
       
        } else if (status == 1  && cm.getLevel() >= 100) {
            if (cm.getChar().getBossLog('ydh') < 2)
            {
                cm.gainItem(4032529 ,1);
                cm.warp(922010501);
                cm.getChar().setBossLog('ydh');
cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null, net.sf.odinms.tools.MaplePacketCreator.serverNotice(17,cm.getC().getChannel(),"[运动会副本]" + " : 玩家"+cm.getChar().getName()+"已经进入进入了运动会副本]"+cm.GetSN()+"☆☆☆☆☆☆☆.",true).getBytes());

                cm.dispose();
            }
            else
            {
                cm.sendOk("#r你每天只能允许进入2次 ! 时间未到.");
                mode = 1;
                status = -1;
            }
                
        }
        else{
            cm.sendOk("你的等级不足100级，所以不能进入!");
            mode = 1;
            status = -1;
			
        }
    }
}