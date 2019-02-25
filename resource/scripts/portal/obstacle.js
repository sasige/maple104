importPackage(net.sf.odinms.server.maps);
importPackage(net.sf.odinms.net.channel);
importPackage(net.sf.odinms.tools);

function enter(pi){
if (pi.haveItem(2430014,1)){
pi.warp(106020400)
return false;
}else if (pi.haveItem(4000507,1)){
pi.warp(106020400)
pi.gainItem(4000507,-1)
return false;
}else{
pi.getPlayer().getClient().getSession().write(MaplePacketCreator.serverNotice(5, "未知的力量阻挡着你的前进。"));
return false;
}
}