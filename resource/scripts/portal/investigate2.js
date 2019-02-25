//笔芯制作
importPackage(net.sf.odinms.server.maps);
importPackage(net.sf.odinms.net.channel);
importPackage(net.sf.odinms.tools);

function enter(pi) {
if (pi.isQuestFinished(2322)){//如果完成了这个任务
pi.getPlayer().getClient().getSession().write(MaplePacketCreator.serverNotice(5, "听到了某种声音，但是已经向内务大臣禀报过了。。"));
return false;
}else if (!pi.isQuestActive(2322)) {//如果没有接这个任务
pi.getPlayer().getClient().getSession().write(MaplePacketCreator.serverNotice(5, "好像听到了某种声音，内务大程好像知道点什么。。"));
return false;
}else if (!pi.isQuestFinished(2322)){//如果还没有完成任务。
pi.updateQuest(2322, "1");
//pi.startQuest(2322)
pi.getPlayer().getClient().getSession().write(MaplePacketCreator.serverNotice(5, "好像听到了某种声音，赶快回去向内务大臣禀报！"));
return false;
}else{
pi.getPlayer().getClient().getSession().write(MaplePacketCreator.serverNotice(5, "好像听到了某种声音，但是不知道是从什么地方发来的。"));
return false;
}
}
