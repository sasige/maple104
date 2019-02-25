importPackage(net.sf.odinms.server.maps);
importPackage(net.sf.odinms.tools);
importPackage(net.sf.odinms.client);

function enter(pi) {
 var nextMap = 280030000; 
 var gotomap = 211042400;
 var zakumMap = pi.getC().getChannelServer().getMapFactory().getMap(nextMap); 
		
if (pi.getBossLog('ZAKUM') >= 5) {
			pi.playerMessage("你今天已经没有机会再次进入！");
			return false;
		} 

/*
if (pi.getPlayer().getClient().getChannel() == 1) {
nextMap = 240060201;
gotomap = 211042401;
} 
*/
      var mapobjects = zakumMap.getMapObjects(); 
        var boss = null;
        var iter = mapobjects.iterator();
        while (iter.hasNext()) {
            o = iter.next();
            if (pi.isMonster(o))
                boss = o;


}
        if (boss != null && !zakumMap.getCharacters().isEmpty()) {
            pi.playerMessage("里面已经开始了和扎昆的战斗。");
            return false;
        }

if(zakumMap.getCharacters().isEmpty()){
zakumMap.resetReactors(); 
}

  pi.getC().getChannelServer().getMapFactory().getMap(nextMap).clearMapTimer(); 
  pi.getC().getChannelServer().getMapFactory().getMap(nextMap).killAllMonsters(); 
			pi.warp(gotomap, "west00");
			return true;

}