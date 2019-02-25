//进入珠房 笔芯制作
function enter(pi) {
                var nextmap = pi.getC().getChannelServer().getMapFactory().getMap(910350000);
		pi.warp(910350000);
		nextmap.addMapTimer(1*1,103050100);  //加时间限制
		return true;
}