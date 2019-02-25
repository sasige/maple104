function enter(pi) {
	if (pi.getMapId() == 970000001){
if (pi.haveItem(4000019,3)){//如果拥有了三个蜗牛壳
pi.openNpc(9900005);
return false;
}else{
pi.getPlayer().dropMessage("必须收集三个蜗牛壳之后才能进入下一关。");
return false;
}
	}else if (pi.getMapId() == 970000002){
if (pi.haveItem(4000000,3)){//如果拥有了三个蜗牛壳
pi.openNpc(9900005);
return false;
}else{
pi.getPlayer().dropMessage("必须收集三个蓝色蜗牛壳之后才能进入下一关。");
return false;
}		
	}else if (pi.getMapId() == 970000003){
if (pi.haveItem(4000016,3)){//如果拥有了三个蜗牛壳
pi.openNpc(9900005);
return false;
}else{
pi.getPlayer().dropMessage("必须收集三个红色蜗牛壳之后才能进入下一关。");
return false;
}		
	}//getMapId
}