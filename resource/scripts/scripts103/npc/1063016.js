function start() {
var ico11 = "#fEffect/CharacterEff/1112903/0/0#";
	if (cm.getChar().getMapId() == 200101500){
	    if(cm.countMonster()<0){
		 cm.sendSimple ("#");
	    }else{
   		 cm.sendSimple ("");
	    }
	} else {
	    cm.sendOk("#rÍùÇ°×ß\r\n¡ú¡ú¡ú¡ú¡ú¡ú¡ú¡ú¡ú¡ú¡ú¡ú¡ú¡ú\r\n#b¡ú¡ú¡ú¡ú¡ú¡ú¡ú¡ú¡ú¡ú¡ú¡ú¡ú¡ú\r\n#k¡ú¡ú¡ú¡ú¡ú¡ú¡ú¡ú¡ú¡ú¡ú¡ú¡ú¡ú")
	}
}
function action(mode, type, selection) {
cm.dispose();
if (selection == 0) {
        if (!cm.haveItem(4032529,1)) {
        cm.sendOk("#");
	cm.dispose();}


}
}