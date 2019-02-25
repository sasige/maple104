/*
*	暴风成长秘药
*/
function start() {
	if(im.getChar().getLevel()<200){
		im.getChar().levelUp();
		im.gainItem(2430218,-1);
		im.dispose();
	}else{
		im.sendOk("对不起,200级以上不能再使用此物品!");
	}
	
}