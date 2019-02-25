
var status;

var minPlayers = 1;
var maxPlayers = 6;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1)
        status++;
    else {
        cm.dispose();
        return;
    }
	
    if (status == 0) {
        if (cm.getPlayer().getClient().getChannel() != 3){
            cm.sendOk("希纳斯只能在第3频道召唤！");
            cm.dispose();
        }else if (cm.getPlayer().getParty() == null) {
            cm.sendOk("#e<组队任务：冒险骑士团远征任务>#n\r\n你想和你的队员们一起努力，打败堕落的希纳斯吗？\r\n那么请开启组队，组上3~6个队员在来找我说话吧。");
            cm.dispose();
        } else if (!cm.isLeader()) {
            cm.sendOk("想挑战堕落的希纳斯的话，请叫你的队长来和我谈话！");
            cm.dispose();
        } else {
            cm.sendYesNo("#d你确定你们已经准备好进去对抗堕落的希纳斯了吗？准备好了的话我这就送你们进去！");
        }
    }else if (status == 1) { 
        var party = cm.getParty().getMembers();
        var inMap = cm.partyMembersInMap();
        if (inMap < minPlayers || inMap > maxPlayers) {
            cm.sendOk("队伍人数没有达到要求的最低 "+minPlayers+"人, 最多 "+maxPlayers+"人！");
            cm.dispose();
        } else {
            var em = cm.getEventManager("xinasi");
            var map = net.sf.odinms.net.channel.ChannelServer.getInstance(cm.getC().getChannel()).getMapFactory().getMap(271040100);
            var playercount = map.getCharacters().toArray().length;
            if (playercount < 1)
            {
                em.setProperty("xinasiOpen" , "true");
            }
            if (em == null) {
                cm.sendOk("本任务暂未开放.");
                cm.dispose();
            } else if (em.getProperty("xinasiOpen").equals("true")) {
                em.startInstance(cm.getParty(), cm.getPlayer().getMap());
                em.setProperty("xinasiOpen" , "false");
                cm.serverNotice("[副本公告]：勇敢的玩家 ["+ cm.getChar().getName() +"] 带领着远征军团挑战希纳斯，为它们加油吧！");
                cm.dispose();
            } else {
                cm.sendNext("已经有队伍正在对抗希纳斯大怪物，请稍后在尝试！");
                cm.dispose();
            }
        }
    }
}