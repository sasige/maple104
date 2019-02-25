function start() {
if(cm.getChar().getMapId() == 9100016) {
	    if(cm.countMonster()>=1){
		 cm.sendSimple ("今天晚上8点活动你准备好了吗?只能召唤一只噢~! \r\n");
	    }else{
   		 cm.sendSimple ("今天晚上8点活动你准备好了吗?!\r\n#r9分以后我将会变成一只死猫#v4000464#\r\n\r\n\r\n       #L0##b我要挑战#l        #r#L1#自由市场#l\r\n\r\n\r\n爆率如下\r\n\r\n#v1002544##v1002699##v1002839##v1003391##v1003439##v1003509##v1003510##v1003516##v1042231##v1042235##v1052448##v1052449##v1112136##v1112930##v1112928##v1102391##v1092108##v3010313##v3010361##v3010362#");
	    }
	} else {
	    cm.sendOk("活动结束")
	}
}
function action(mode, type, selection) {
cm.dispose();
if (selection == 0	) {
cm.summonMob(9400900,2000000000, 40000, 1); 
cm.dispose();}
if (selection == 1	) {
cm.warp(910000000,0);
cm.dispose();}
}
