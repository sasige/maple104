
//非同意禁止转载
importPackage(net.sf.odinms.server.maps);
importPackage(net.sf.odinms.net.channel);
importPackage(net.sf.odinms.tools);

function enter(pi) {
    if (pi.getMapCharCount(220080001) > 0) {
        sendMessage(pi,"有人正在挑战.");
        return false;
    }
    if (pi.getBossLog('Populatus00')>=5 && pi.getPlayer().getVip()==0) {
        sendMessage(pi,"每天只能挑战5次.会员不限制次数.");
        return false;
    }
    pi.resetMap(220080001);
    pi.setBossLog('Populatus00');
    pi.warp(220080001,"st00")
    return true;
}

function sendMessage(pi,message) {
    pi.getPlayer().getClient().getSession().write(MaplePacketCreator.serverNotice(5, message));
}
