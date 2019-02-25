/*
 *
 *
 */

importPackage(net.sf.odinms.server.maps); 
importPackage(net.sf.odinms.net.channel); 
importPackage(net.sf.odinms.tools);
importPackage(net.sf.odinms.server.life);
importPackage(java.awt);

var status = 0;

function start() 
	{
	status = -1;
	action(1, 0, 0);
	}

function action(mode, type, selection)
{
	var nextmap = cm.getC().getChannelServer().getMapFactory().getMap(702060000);
	if (mode == -1)
	{
		cm.dispose();
	}
	else if (mode == 0)
	{
		cm.sendOk("好的如果要挑战#b御龙终结者#k随时来找我.");
		cm.dispose();
	} 
	else 
	{
	if (mode == 1)
	status++;
	else
	status--;
		
	if (status == 0)
	{	if (cm.getC().getChannel() != 2){
			cm.sendOk("   御龙终结者只能在#r2#k频道进行!");
			cm.dispose();
      		}else{
			cm.sendYesNo("你是否要挑战#b御龙终结者#k呢?");
		}
	}
	else if (status == 1) 
	{ 	
		var party = cm.getPlayer().getParty();		
		if (party == null || party.getLeader().getId() != cm.getPlayer().getId()) {
                    cm.sendOk("你不是队长。请你们队长来说话吧！");
                    cm.dispose();
                }else if(cm.getBossLog("shaoling") >= 3) {
	            cm.sendOk("您好,系统限定每天只能挑战三次,如果强行进入,会被系统弹回来的!");
                    cm.dispose();
		}else if(party.getMembers().size() <3) {
	            cm.sendOk("需要 3 人以上的组队才能进入！!");
                    cm.dispose();
		}else{			
			nextmap.resetReactors();
	    		nextmap.killAllMonsters();
			nextmap.clearMapTimer();			
			nextmap.setOnUserEnter("shaoling");
			cm.gainItem(4031466,1);
			cm.gainItem(4001402,1);
			cm.warpParty(674030200);
cm.serverNotice("『御龙终结者副本』：【"+ cm.getChar().getName() +"】居然带领他的队友挑战御龙终结者！");
			cm.dispose();
		}
	}
}
}
