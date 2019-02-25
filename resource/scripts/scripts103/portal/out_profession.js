//匠人村离开传送脚本

function enter(pi) {
    var returnMap = pi.getSavedLocation("Inprofession");
    pi.clearSavedLocation("Inprofession");

    if (returnMap < 0) {
	returnMap = 102000000; // 如果是异常方式进入，则传送到勇士部落。
    }
    var target = pi.getMap(returnMap);
    var portal;
	
    if (portal == null) {
	portal = target.getPortal(0);
    }
    if (pi.getMapId() != target) {
	pi.getPlayer().changeMap(target, portal);
    }
}