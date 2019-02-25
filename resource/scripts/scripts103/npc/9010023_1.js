/* ===========================================================
			注释 itemQuantity()
	脚本类型: 		NPC
	所在地图:		
	脚本名字:		
=============================================================
本脚本源自网上流传，仅为技术交流之用。如侵权。请联系我们，我们将在第一时间删除。
*/

var a = 0;
var level = Array(0,201);
var items = Array(4000463,4000463);
var name;

function start() {
	a = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (mode == 1)
			a++;
		else
			a--;
			if (a == -1){
				cm.dispose();
			}else if (a == 0) {
					if (cm.getPlayerStat("LVL") <= 0){
					name = items[0];
					}else if (cm.getPlayerStat("LVL") <= 201){
						name = items[1];
					}else{
						name = null;	
					}
					if (name != null){
					cm.sendGetText("#b请在下面输入购买交易币数量！\r\n#r 目前拥有#k『"+cm.GetMoney()+"』#r点卷。")
					}else{
					cm.sendOk("对不起，无法交换。")	
					cm.dispose();
					}
			}else if (a == 1){
				if (cm.getText() < 1){
					cm.sendOk("大于1 的数字可以输入。")
					cm.dispose();
				}else if (cm.GetMoney() >= cm.getText() * 500){
					player.GainMoney(-cm.getText() * 500);
					cm.gainItem(name,cm.getText() * 1);
					cm.sendOk("购买成功，获得了"+cm.getText() * 1+"交易币。")
					cm.dispose();
				}else{
					cm.sendOk("对不起，你没有足够的点卷购买。");
					cm.dispose();
				}
	}//status
}
}