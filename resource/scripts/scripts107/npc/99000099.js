var status = 0;
function start() {
    status = -1;
    action(1, 0, 0);
}
function action(mode, type, selection) {
    if (status == 0 && mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        status--;
    } 
    if (status == 0) {
        var selStr = "Dear#r#h ##k您好，现在吗\r\n#L1##r未来之门#l \r\n#L2##r时间领主#l \r\n#r还是算了#l";
        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
	case 1:
        cm.warp(271000000);
	  	cm.dispose();
	break;
	case 2:
        cm.warp(270010000);
		cm.dispose();
	break;
		}
	}
}