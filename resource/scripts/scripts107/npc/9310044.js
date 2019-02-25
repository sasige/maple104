/**
 *芬芬时尚潮流 为客户weinan9988制作
 **/

function start() {

    if(cm.countMonster()< 1) {
        cm.sendSimple ("#b亲爱的#r#h ##k#b您好，我可以为你召唤BOSS 请选择:\r\n#L0#扎昆#l\r\n#L1#离开地图出去#l\r\n\r\n        制作人：芬芬时尚潮流 ");
    } else {
        cm.sendOk("一次不能出现两只BOSS,请离开后重新进入.")
    }
}
function action(mode, type, selection) {
    cm.dispose();
    if (selection == 0) {
    if (cm.getPlayer().getMeso() < 1){  
        cm.sendOk("你的冒险币不能少于1")
        cm.dispose();
       }else{	
        cm.gainItem(4001433,-1);
        //怪物ID, X坐标，Y坐标
	cm.changeMusic("Bgm10/Eregos");
        cm.spawnMob(8800200, -202, 593);   
        cm.dispose();
        }
} else if (selection == 1) {//离开地图
            cm.warp(910000000, 0);
            cm.dispose();
			
        }
    }