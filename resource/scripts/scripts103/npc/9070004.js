var mmm="#fEffect/CharacterEff/1112905/0/1#";
function start() {

    cm.sendSimple ("#v4032877##v4032863##v4032869##v4032879##v4032883##v4032877##v4032863##v4032869##v4032879##v4032883#\r\n"+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+"\r\n               #b您好我就是传说中的飞仙使者\r\n\r\n           #b飞仙可以获得更牛的称号和更牛的破功\r\n\r\n                #k费用昂贵你考虑清楚了吗?\r\n                    #r#L0#我要飞仙#l\r\n\r\n\r\n"+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+""+mmm+"\r\n#v4032877##v4032863##v4032869##v4032879##v4032883##v4032877##v4032863##v4032869##v4032879##v4032883#")
}

function action(mode, type, selection) {
    switch(selection){
        case 0:
            if(player.GetReborns() >= 200 && player.getDojoPoints() >= 1000 && player.getStr() >= 30000 && player.getfs()<11 ) {
                player.SetReborns(player.GetReborns() - 200);
 		player.setfs(player.getfs()+1);
		player.setBossLog("p_fs");
		player.setStr(player.getStr()-30000);
 		player.setDojoPoints(player.getDojoPoints()-1000);
		player.updateSingleStat(net.sf.odinms.client.MapleStat.STR,player.getStr());
                player.saveToDB(true);
		cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『位列仙班公告』"+" : "+"有潜质的"+ cm.getChar().getName() +"成功克服了千难万险飞仙一次(破功增强)!!"))
		cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『位列仙班公告』"+" : "+"有潜质的"+ cm.getChar().getName() +"成功克服了千难万险飞仙一次(破功增强)!!"))
		cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『位列仙班公告』"+" : "+"有潜质的"+ cm.getChar().getName() +"成功克服了千难万险飞仙一次(破功增强)!!"))
		cm.sendOk("#r恭喜您.飞仙成功万人敬仰!");
                cm.dispose();
            }else{
		cm.warp(931000610);
                cm.sendOk("老湿\r\n#r等您转身次数达到200转再来找我\r\n30000点力量属性\r\n飞仙不高于10次\r\n#b一天只可以飞仙一次\r\n#r少年你触动的天神给踹到监狱了下次别乱点它了~!!!");
                cm.dispose();
            }

    }
}
