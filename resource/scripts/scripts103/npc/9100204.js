var myDate = new Date(); // 实例化一个Date类的变量。。 
var status = 0;
function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (mode == 0 && status == 0) {
			
			cm.dispose();
			return;
		}
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
	   var text = "#r#fUI/UIWindow.img/QuestIcon/3/0#\r\n"
        text += "这个礼包每个帐号只能领取一次!\r\n\r\n"; 


 text += "#L1##e我要领取新手礼包②#n#l\r\n\r\n"; 
  text += "#L2##e我要领取新手礼包③#n#l\r\n\r\n"; 
  text += "#L4##e我要领取新手礼包④#n#l\r\n\r\n"; 
            
                       

			cm.sendSimple(text);
			
		} else if (status == 1) {

			if (selection == 3){

if (cm.getChar().getPresent() == 0 &&cm.getChar().getLevel() >=1 ) {

if (cm.getChar().getGender() == 0 ) {

var text = " #fUI/UIWindow.img/QuestIcon/3/0#\r\n新手礼包#v5150040#X①,#v1142263#X①,#v1003192#X①,#v1040154#X①,#v1060145#X①,#v1072516#X①\r\n\r\n"

	    text += "#L0#我要领取新手礼包\r\n\r\n"; 

cm.sendSimple(text);
}else if (cm.getChar().getGender() == 1 ) {

var text = "#fUI/UIWindow.img/QuestIcon/3/0#\r\n新手礼包#v5150040#X①,#v1142263#X①,#v1003193#X①,#v1041156#X①,#v1061166#X①,#v1072516#X①\r\n\r\n"

	   text += "#L1#我要领取新手礼包\r\n\r\n"; 
cm.sendSimple(text);

}


} else {
			cm.sendOk("#e你已经领取过了!");
			cm.dispose();
		       }
	  }else if (selection == 4){


if (cm.getChar().getPresent() == 3 &&cm.getChar().getLevel() >=30 ) {
  cm.gainItem(1032080, 1);

  cm.gainItem(1132036, 1);
  cm.gainItem(1122081, 1);
  cm.gainItem(1112435, 1);


 cm.getChar().setPresent(4);
	cm.getChar().saveToDB(true);

    cm.getPlayer().startMapEffect("领取成功!祝你游戏愉快!", 5120004);
cm.dispose();


}else{ 
  cm.getPlayer().dropMessage(1, "你已经领取过了,或者没有领取上一个礼包!或者没有达到30级!");
cm.dispose();
}


}else if (selection == 1){

if (cm.getChar().getPresent() == 1 &&cm.getChar().getLevel() >=1 ) {
  cm.gainItem(1142265, 1);


 cm.getChar().setPresent(2);
	cm.getChar().saveToDB(true);

   
    cm.getPlayer().startMapEffect("领取到了2000抵用点卷,祝你游戏愉快!", 5120004);
cm.getPlayer().modifyCSPoints(1,+2000);
cm.dispose();


}else{ 
  cm.getPlayer().dropMessage(1, "你已经领取过了,或者没有领取上一个礼包!");
cm.dispose();
}

}else if (selection == 2){

if (cm.getChar().getPresent() == 2 &&cm.getChar().getLevel() >=1 ) {
  cm.gainItem(2040807, 7);
  cm.gainItem(1082149, 1);

 cm.getChar().setPresent(3);
	cm.getChar().saveToDB(true);

    cm.getPlayer().startMapEffect("领取成功!祝你游戏愉快!", 5120004);
cm.dispose();


}else{ 
  cm.getPlayer().dropMessage(1, "你已经领取过了,或者没有领取上一个礼包!");
cm.dispose();
}

}		
}else if (status == 2) {
 if(selection == 0){


 cm.gainItem(5150040, 1);

 cm.gainItem(1142263, 1);
 cm.gainItem(1003192, 1);
 cm.gainItem(1040154, 1);
 cm.gainItem(1060145, 1);

 cm.gainItem(1072516, 1);
cm.getChar().setPresent(1);
	cm.getChar().saveToDB(true);

    cm.getPlayer().startMapEffect("领取成功,祝你游戏愉快!", 5120004);
cm.dispose();




}else if(selection == 1){


 cm.gainItem(5150040, 1);

 cm.gainItem(1142263, 1);
 cm.gainItem(1003193, 1);
 cm.gainItem(1041156, 1);
 cm.gainItem(1061166, 1);
 cm.gainItem(1072516, 1);

cm.getChar().setPresent(1);
	cm.getChar().saveToDB(true);

    cm.getPlayer().startMapEffect("领取成功,祝你游戏愉快!", 5120004);
cm.dispose();





}else if(selection == 2){


if (cm.itemQuantity(4000019) >= 100) {
   cm.gainItem(4000019, -100); 
cm.getChar().SetShuoHua(3);
cm.sendOk("恭喜你设置成功!!");	
cm.dispose();
}else{ 
  cm.getPlayer().dropMessage(1, "材料不足!");
cm.dispose();
}

}else if(selection == 3){
cm.getChar().SetShuoHua(0);
cm.sendOk("恭喜你取消成功!!");	
cm.dispose();
}
else if(selection == 4){
if (cm.itemQuantity(4000019) >= 500) {
cm.gainItem(4000019, -500); 
cm.getChar().SetSD(1);
cm.sendOk("恭喜你开启成功!!如果你打怪会掉线请关闭此功能，当然操作好就不会掉线");	
cm.dispose();
}else{ 
  cm.getPlayer().dropMessage(1, "材料不足!");
cm.dispose();
}
}else if(selection == 5){
cm.getChar().SetSD(0);
cm.sendOk("恭喜你取消成功!!打怪会掉线的玩家必须取消哦，当然操作好就不会掉线");	
cm.dispose();
}
}



	}
}	