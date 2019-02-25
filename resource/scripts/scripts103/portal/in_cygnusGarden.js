function enter(pi) {

if(cm.haveItem(4032923)){
	pi.warp(271030410)
	pi.gainItem(4032923,-1)
}else{
	pi.playerMessage("没有梦之钥匙，不能进入。");	
}
}