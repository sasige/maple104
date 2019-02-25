function enter(pi) {
		pi.getClient().getSession().write(net.sf.odinms.tools.MaplePacketCreator.serverNotice(5, "被一股神秘的力量阻挡着。"));
		//pi.warp(921120500);
		return true;
}