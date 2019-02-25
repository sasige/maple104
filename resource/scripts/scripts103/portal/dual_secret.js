function enter(pi) {
pi.getPlayer().getClient().getSession().write(net.sf.odinms.tools.MaplePacketCreator.serverNotice(5, "四周黑暗的地方，还是不要进去的好。")); 
return false;
}