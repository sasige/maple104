/*
-- ---------------------
     黑龙王召唤脚本
-------------------------
-------------------------
*/

importPackage(net.sf.odinms.server.maps); 
importPackage(net.sf.odinms.net.channel); 
importPackage(net.sf.odinms.tools); 

function enter(pi) { 
    if (pi.getMapCharCount(240060200) > 0) {
        sendMessage(pi,"有人正在挑战.");
        return false;
    }
    if (pi.getBossLog('hontale')>=5 && pi.getPlayer().getVip()==0) {
        sendMessage(pi,"每天只能挑战5次.会员不限制次数.");
        return false;
    }
    
    pi.getC().getChannelServer().getMapFactory().getMap(240060200).clearMapTimer(); 
    pi.resetMap(240060200);
    pi.setBossLog('hontale'); 
    pi.warp(240060200);  
    sendMessage(pi,"您已进入了暗黑龙王地图，请用普通攻击打碎右上角的紫色水晶"); 
    return true;    
} 
function sendMessage(pi,message)
{ 
    pi.getPlayer().getClient().getSession().write(MaplePacketCreator.serverNotice(5, message)); 
} 
