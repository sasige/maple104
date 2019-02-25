var mmm="#fEffect/CharacterEff/1112905/0/1#";
function start() {

    cm.sendSimple ("欢迎来到#r"+cm.GetSN()+"#k,( ^_^ )\r\n#r我是可爱的奶牛宝宝\r\n#b给我运动会金币哈哈\r\n#r#L1#1个#v4001318#换取#v4310030##l    \r\n\r\n#b#L2#10个#v4001318#换取#v5000046#(可爱的蘑菇坐骑)")
}

function action(mode, type, selection) {
    cm.dispose();

    switch(selection){

        case 1:
            if(cm.haveItem(4001318, 1)) {
                cm.sendOk("您的1个#v4001318#已被收回!为了回报你，我给你#v4310030#!")
                cm.gainItem(4001318, -1);
                cm.gainItem(4310030, 1);
                        cm.serverNotice(5,"[运动会公告]：恭喜运动健将:"+cm.getChar().getName()+" 成功获得一枚运动会金币");
                cm.dispose();
            } else {
                cm.sendOk("#r等您有一个#v4001318#的时候我可以给您换取一枚#v4310030#")
                cm.dispose();    
            };    
            break;
        case 2:
            if(cm.haveItem(4001318, 10)) {
                cm.sendOk("您的10个#v4001318#已被收回!为了回报你，我给你#v5000046#!")
                cm.gainItem(4001318, -10);
		cm.teachSkill(80001013,1,1);
                cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null, net.sf.odinms.tools.MaplePacketCreator.serverNotice(17,cm.getC().getChannel(),"[运动会公告]" + " : 恭喜运动健将：" + cm.getPlayer().getName() +" 成功收集10个运动会金币换取蘑菇坐骑",true).getBytes());
                cm.dispose();
            } else {
                cm.sendOk("#r等您有10个#v4001318#的时候我可以给您换取椅子#v5000046#")
                cm.dispose();    
            };    
            break;
    }
}
