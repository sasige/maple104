var typed=0;

var status = 0;
function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (status >= 0 && mode == 0) {
			cm.dispose();
			return;
		}
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
                              cm.sendSimple("            #v3991056##v3991057##v3991058##v3991059#\r\n\r\n#e#b魔力玩家您好，今天是二零一一年八月十五日。中秋节快到了。另外魔力的生日就是今天。在线的玩家都可以到我这里来领取魔力送的红包。神秘礼物。希望大家多多支持魔力冒险岛。魔力冒险岛Gm在此表示感谢。!#k#l\r\n\r\n     #L9##e#r祝福魔力Gm生日快乐#k#l ");
		} else if (status == 1) {
			 if (selection == 0) {
		} else if (selection == 1) {//经验修复
  						var statup = new java.util.ArrayList();
	   					var p = cm.c.getPlayer();
				 	        if(p.getExp() < 0){
							p.setExp(0) 
					                statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.EXP, java.lang.Integer.valueOf(0))); 
				 		        p.getClient().getSession().write (net.sf.odinms.tools.MaplePacketCreator.updatePlayerStats(statup));
			   				cm.sendOk("经验值已修复完成");
							cm.dispose();
						}else{
						        cm.sendOk("您的经验值正常,无需修复!");
							cm.dispose();
						}
						}else if (selection == 9){
							if (cm.getChar().getBossLog('ZQJ') >= 1) {
                                                        cm.sendOk("#e#g此活动以结束.!请期待下次活动.!#k#l");
                                                        cm.dispose();
                                                        }else{
							cm.sendOk("#e#r此活动以结束.!请期待下次活动.!#k#l");
							cm.dispose();
							}
				cm.dispose();

			}
		}
	}
}
