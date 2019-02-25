//进入珠房 笔芯制作
function enter(pi) {
		var nextmap = pi.getC().getChannelServer().getMapFactory().getMap(910350000);   //珠房
		if(nextmap.playerCount() > 0){  //如果有人
		pi.getPlayer().getClient().getSession().write(net.sf.odinms.tools.MaplePacketCreator.serverNotice(5, "可能有人在珠房。")); 
		return false;	
		}
		nextmap.resetReactors(); 
		nextmap.clearMapTimer();
		pi.warp(910350000);
		nextmap.addMapTimer(60*10,103050100);  //加时间限制
		return true;
}