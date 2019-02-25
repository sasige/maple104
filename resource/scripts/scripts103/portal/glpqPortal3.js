function enter(pi) {
	var eim = pi.getPlayer().getEventInstance();
		if (eim.getProperty("pass3") == null){//如果还没有打掉所有的祭台。
			pi.playerMessage("黑暗魔法师的力量越来越接近了，赶快启动所有的祭坛。逃离这里。")
		}else if (eim.getProperty("pass31") == null){//如果大调了所有的祭台，但是还没有从上面的传送口中得到记录
			pi.playerMessage("黑暗魔法师的力量越来越接近了，赶快到上面去。");
		}else if (eim.getProperty("pass32") == null){//如果从传送口中得到了记录，但是还没有点击NPC赋值
			pi.playerMessage("由于黑暗魔法师的妨碍，无法继续前进。杰克好像有什么话要说……。")
		}else if (eim.getIntVar("var_int") >= eim.getPlayers().size()){//如果从NPC哪里得到了赋值，且所有玩家已经从传送口中得到了记录。
			pi.warp(803001100,"sp")
		}else{//
			pi.playerMessage("此地图还有其它玩家没有从上面的传送口通过。")
		}
}