importPackage(net.sf.odinms.tools);
importPackage(net.sf.odinms.client);

/* Adobis
feiqi entrance
*/
var status = 1;
var price = 5000000;
var map = Array(910340100);

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
            cm.sendSimple("勇士, #h #. 你想跟你的朋友一起参加组队挑战任务吗.挑战完成后会有你意想不到的奖励，据说管理员添加了很多商城买不到的点装和玩具哟，跟朋友一起去试试吧 \r\n#g进入冰骑士组队副本#k.\r\n #d每天只能进冰骑士组队副本⑤次.#k#r今天你已经进入"+cm.getBossLog('feiqi')+"次#k.#k#l\r\n#L1##r进入冰骑士组队副本.#l");
        } else if (status == 1) {
            if (selection == 0) {
                if(cm.getMeso() >= 5000000) {
                    cm.gainMeso(-5000000);
                    cm.gainItem(4001564, -1000);
                    cm.gainItem(4001564, 1);
                } else {
                    cm.sendOk("请确认您的包袱里有足够的金币.");
                }
                cm.dispose();
            } else if (status == 2) {
            } else if (cm.getLevel() >= 100) {
                if(cm.getParty() == null){
                    cm.sendOk("请先建立组队。");
                    cm.dispose();
                    return;
                }
                if(!cm.isLeader()){
                    cm.sendOk("请让队长跟我说话。");
                    cm.dispose();
                    return;
                }
                if(!cm.getParty().AllMembersAtMap(cm.getChar())){
                    cm.sendOk("你的队员不在这儿。");
                    cm.dispose();
                    return;
                }
                var map = cm.getC().getChannelServer().getMapFactory().getMap(910340100);
                if(map.getCharacters().size()>0){
                    cm.sendOk("里面已经有人了。");
                    cm.dispose();
                    return;
                }
                if ((cm.getParty().AllMembersNoBossLog('feiqi',10,false) && cm.getC().getChannel() > 1 && cm.getC().getChannel() < 9)
                    )
                    { 
                    map.resetReactors();
                    map.killAllMonsters();
                    cm.getParty().WarpToMap(map,0);
                    cm.getParty().AllMembersSetBossLog('feiqi');
                    cm.gainItem(4001564, 1);
                    cm.dispose();
                }
                else
                {
                    cm.sendOk("你的队员没有满足以下条件\r\n你的队员每天5次.VIP会员不限制次数.\r\n在能在2-3频道进入.");
                    mode = 1;
                    status = -1;
                }
            } else{
                cm.sendOk("你必须达到100级以上才能挑战 #m910340100#.");
                mode = 1;
                status = -1;
            }
        }
    }
}