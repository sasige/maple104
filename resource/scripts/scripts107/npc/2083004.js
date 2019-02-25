function start() {
    if (cm.getPlayer().getClient().getChannel() == 1,2,3) {
        cm.dispose();
        cm.openNpc(2083004, 2);
    } else if (cm.getPlayer().getClient().getChannel() == 1,2,3 ||cm.getPlayer().getClient().getChannel() == 4 ) {
        cm.dispose();
        cm.openNpc(2083004, 1);
    }
	cm.dispose();
}