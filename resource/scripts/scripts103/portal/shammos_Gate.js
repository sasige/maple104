importPackage(net.sf.odinms.server.maps);
importPackage(net.sf.odinms.net.channel);
importPackage(net.sf.odinms.tools);
/* ===========================================================
			注释(cm.sendSimple\cm.itemQuantity())
	脚本类型: 		PORTALS
	所在地图:		冰封谷
	脚本名字:		冰封谷入场
=============================================================
制作时间：
制作人员：笔芯
=============================================================
for(var i = 1;i<=5;i++){
				if(cm.getPlayer().getInventory(net.sf.odinms.client.MapleInventoryType.getByType(i)).isFull()){
					cm.startPopMessage(cm.getPlayer().getId(), "您必须让自己的背包腾出一格。");
					cm.dispose();
					return;
				}
			}
pi.isQuestActive(id)//判断是否接了
pi.isQuestFinished(id)//是否完成
pi.getPlayer().getClient().getSession().write(MaplePacketCreator.serverNotice(5, "text"));
return
*/
function enter(pi) {
	if(pi.isQuestActive(3122)){//查看是否接了“封印的确认”任务.
		pi.warp(921120705);
		pi.getPlayer().startMapEffect("继续往前走，到达万年冰河洞穴……那里是莱格斯封印的地方！", 5120035);
		return true;
	}else{
		pi.getPlayer().getClient().getSession().write(MaplePacketCreator.serverNotice(5, "神秘力量阻挡着你的前进。"));
		return false;
	}
}