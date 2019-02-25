var mmm="#fEffect/CharacterEff/1112905/0/1#";
function start() {

    cm.sendSimple ("欢迎来到#r"+cm.GetSN()+"#k,( ^_^ )\r\n这里是奖励小草！\r\n#L0#恭喜你过关了.#l")
}

function action(mode, type, selection) {
    cm.dispose();

    switch(selection){
        case 0:
        if (!cm.haveItem(4002000,1)) {
        cm.sendOk("抱歉，你没有1个#v4002000v#无法换取坐骑.亲 请您继续收集)");
} else {
	cm.gainItem(4002000,-1);
   cm.teachSkill(80001026,10,10);//扫把技能
                cm.warp(910000000,0);
                cm.sendOk("恭喜你你已经获得了扫把坐骑技能!双击人物,选择坐骑使用");
cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『"+ cm.getChar().getName() +"』"+" : "+"哇~~!!★★★★★已经获得了魔力超级奖励坐骑★★★★★恭喜他吧!!"))
                cm.dispose();
            }
           
    }
}
