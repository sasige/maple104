importPackage(net.sf.odinms.server.maps);
importPackage(net.sf.odinms.net.channel);
importPackage(net.sf.odinms.tools);

function enter(pi) {
	pi.getPlayer().getClient().getSession().write(MaplePacketCreator.serverNotice(5, "迷失在外太空，被未知的力量传送了出来。"));
	pi.warp(199000000);
	return true;
}