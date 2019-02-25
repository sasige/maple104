importPackage(java.util);
importPackage(net.sf.odinms.client);
importPackage(net.sf.odinms.server);

var status = 0;
var typed=0;
var slot = Array();
var stats = Array("Strength", "Dexterity", "Intellect", "Luck", "HP", "MP", "Weapon Attack", "Magic Attack", "Weapon Defense", "Magic Defense", "Accuracy", "Avoidability", "Hands", "Speed", "Jump");
var selected;
var statsSel;
var ico11 = "#v4030014#";
var itemjf=new Array("1122019","1122024","1122025","1132000","1132001","1132002","1142000","1142001","1142002","1142003","1142004","1142005","1142006","1142007","1142008","1142014","1142015","1142016","1142017","1142074","1142151","1142173","1142174","1142175","1142176","1142177","1142178","1142179","1142304","1142189","1142124","1142167");
var itemjfcost=new Array("200","800","800","300","400","500","200","800","600","600","600","1500","2000","1800","1800","800","800","800","800","2000","2000","3000","3000","3000","3000","3000","3000","10000","10000","10000","10000","10000");

var itemgp=new Array("2370001","2370000","1122019","1122024","1122025","1132000","1132001","1132002","1142000","1142001","1142002","1142003","1142004","1142005","1142006","1142007","1142008","1142014","1142015","1142016","1142017","1142074","1142151","1142173","1142174","1142175","1142176","1142177","1142178","1142179","1142304","1142189","1142124","1142167");
var itemgpcost=new Array("20","50","500","1500","1500","800","1200","1800","800","2500","2400","2400","2400","3000","3800","3600","3600","2500","2500","2500","2500","5000","5000","6000","6000","7000","7000","7000","8000","30000","30000","30000","30000","25000");
var chanced = Math.floor(Math.random()*2)+1;

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
            //if (cm.getChar().getGMLevel() >= 50 ) { 
            var iid="[活动]";
            //cm.makeitem2(2340000,0,0,0,0,0,0,0,iid,50);
            //}
            if(chanced>=2){
                cm.mapMessage("[NPC]好消息，庆祝节日的到来，现在开放了充值元宝奖励，充得越多，送得越多哟！");
            }
            if (cm.getLevel() >= 1 ) {  
                var text = "";
                text +="   #b您好，欢迎来到#r"+cm.GetSN()+"#k,0_0我是#r[多功能服务员]#k.\r\n\r\n";
                text +="   #L8#"+ico11+"#r[购买会员]#l     #L20#[重置属性点]#v4030015##l \r\n\r\n";
                text +="   #L2#"+ico11+"#r[美容美发]#l     #L100#[购买众喇叭]#v4030015##l\r\n\r\n";
                text +="   #L105##r"+ico11+"[高级装备]#l     #L106#[圣杯必成卷]#v4030015##l\r\n\r\n\r\n";
                text +="         #L107##b"+ico11+"[超级属性装备]#v4030015##l\r\n";
                cm.sendSimple(text);
            } else {
                cm.sendOk("#r对不起，等级底于130级不给你使用这个NPC");
                cm.dispose();
            }
        } else if (status == 1) {
            if (selection >= 1001) {
                cm.dispose();
            }
            if (selection == 21) {
                typed=212;
                var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();
                for(var i = 1;i<=5;i++){
                    if(cm.getPlayer().getInventory(net.sf.odinms.client.MapleInventoryType.getByType(i)).isFull()){
                        cm.sendOk("您至少应该让所有包裹都空出一格");
                        cm.dispose();
                        return;
                    }
                }
                selStr = "能量点获得方法就是站在1线市场泡点，每1分钟会涨1点.20的兵法书直接增加500W经验，50的兵法书直接增加5000W经验.\r\n#r<您目前的能量点为："+cm.getChar().getgodpoint()+" 点>继续努力哟~~#b";
                for (var i = 0; i < itemgp.length; i++) {
                    selStr += "\r\n#b#L" + i + "##v" +itemgp[i]+"# 需要 "+itemgpcost[i]+"点能量，#r[点击图片开始换取]#l";
                }
                cm.sendSimple(selStr);
            }
            if (selection == 103) {
                cm.openNpc(2080000);
            }
            if (selection == 100) {
                cm.openNpc(9110102);
            }
            if (selection == 105) {
                cm.openNpc(9000018);
            }
            if (selection == 106) {
                cm.openNpc(1012122);
            }
            if (selection == 21) {
                cm.openNpc(1012122);
            }
            if (selection == 107) {
                cm.openNpc(9310074);
            }
            if (selection == 61) {
                cm.openNpc(9310074);
            }
            if (selection == 62) {
                cm.openNpc(9330092);
            }
            if (selection == 108) {
                cm.openNpc(1032101);
            }
            if (selection == 104) {
                cm.sendOk("站在市场，就能以每分钟1点的值增加，如果你没什么事的时候，比如去吃饭啦，就可以挂在市场，能量点越多，就能换到更好的玩具和装备，不断在更新中哟，记得去看！");
                cm.dispose();
            }
            if (selection == 20) {
                typed=211;
                cm.sendNext("如果你带来了[10张洗能力点卷轴]#v5050000#,我可以将你的所有能力点全部重置为4，在把你其它的点留给你自己加哟，别忘了，加点可以使用命令加的，这是本服特色，如果你还不知道命令，请在聊天窗口输入#b@帮助#k 查询.#r[如果你是高级vip2或者以上级别的是免费的哟]");
            }
            if (selection == 50) {
                cm.sendOk("未知原因，也被管理员关闭，请等待管理员通知开放！");
                cm.dispose();
            }
            if (selection == 51) {
                cm.openNpc(1061100);
            }
            if (selection == 52) {
                cm.openNpc(2060005);
            }
            if (selection == 53) {
                cm.openNpc(9900000);
            }
            if (selection == 54) {
                cm.openNpc(9000009);
            }
            if (selection == 67021) {
                cm.openNpc(9330092);
            }
            if (selection == 101) {
                typed=202;
                var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();
                for(var i = 1;i<=5;i++){
                    if(cm.getPlayer().getInventory(net.sf.odinms.client.MapleInventoryType.getByType(i)).isFull()){
                        cm.sendOk("您至少应该让所有包裹都空出一格");
                        cm.dispose();
                        return;
                    }
                }
                selStr = "请选择你要换取的物品道具.\r\n#r<您目前的积分为："+cm.getjf()+" 分>#b";
                for (var i = 0; i < itemjf.length; i++) {
                    selStr += "\r\n#b#L" + i + "##v" +itemjf[i]+"# 需要 "+itemjfcost[i]+"点积分，#r[点击图片开始换取]#l";
                }
                cm.sendSimple(selStr);
            }
            if (selection == 102) {
                cm.sendNext("目前积分获得的方式，仅仅是废弃组队任务获得，我们将会开放更多的任务来增加积分，敬请期待！当然了，加入会员后，每天可领取5点积分！");
                cm.dispose();
                return;
            }
            if (selection == 11) {
                typed=20;
                cm.sendNext("嘿，你转了几转了呀，如果你的转生等级为#r5#k的倍数，就可以来我这里抽奖咯，每转仅限抽一次哟，#b打个比方：你现在5转，那你就有一次抽奖机会，你到10转的时候，你又有一次机会,以此类推..\r\n#k怎么样，来试一次么?我这里的抽奖和飞天猪那里不一样，有机率获得#r+属性攻击项链和腰带和椅子#k哟..\r\n#eLet's GO.....您目前转生: #r"+cm.getChar().getzs()+"#k次");
            }
            if (selection == 8) {
                typed=8;
                cm.sendSimple("请选择你要加入的会员类型:\r\n#e#b购买会员之前保证背包有充足位置#n#r\r\n#L1##v4031683#中级会员需要#r#b 2W元宝#r(送300全属性物品 )#l\r\n#L2##v4031684##r高级会员需要#b 5W元宝#r(送500全属性物品 )#l\r\n#L5##v4031685##r超级会员需要#b10W元宝#r(送800全属性物品 )#l\r\n#L7##v4031686##r顶级会员需要#b15W元宝#r(送1500全属性物品)#l\r\n#L9##v4031687##r神级会员需要#b18W元宝#r(送3000全属性物品)#l");
            }
            if (selection == 9) {
                if(cm.getChar().getVip()>0){
                    //cm.openNpc(2133001);
                    cm.warp(180000000);
                    //cm.sendOk("尊敬的会员：您的专属地图已经到达！");
                    cm.dispose();
                    return;
                }else{
                    cm.sendOk("#r对不起，您不是会员！");
                    cm.dispose();
                    return;
                }
            }
            if (selection == 10) {
                typed=10;
                cm.sendSimple("请点击查看各星会员介绍:\r\n#e#r本服的会员经验加成可以和双倍经验卡叠加生效,\r\n比如6星会员自身3倍经验,用了双倍卡就是3×2=6倍\r\n#b#L0#初级功能及待遇[0元]#l\r\n#L1#高级vip2功能及待遇[10元]#l\r\n#L2#黄金vip3功能及待遇[30元]#l\r\n#b#L4#至尊vip4功能及待遇[60元]#l\r\n#L5#超级vip5功能及待遇[100元]#l\r\n#L6#顶级vip6功能及待遇[150元]#l\r\n#L3#查看如何办理#l");
            }
            if (selection == 2) {
                cm.openNpc(9000083);
            }
            if (selection == 5) {
                cm.openNpc(1012112);
            }
            if (selection == 0) {
                cm.dispose();
            }

            if (selection == 1) {
                cm.openNpc(9900000);
            }
        } else if (status == 2) {
            if(typed==8){
                if(selection==0){
                    if(cm.getLevel()>=160 && cm.getChar().getVip()<1){
                        cm.gainItem(1142178,1);
                        cm.getChar().setVip(1);
                        var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();
                        var toDropd = ii.randomizeStats(ii.getEquipById(1142178));
                        cm.gainItem(2340000,1);//祝福卷
                        cm.serverNotice(5,"[vip公告]：恭喜玩家:"+cm.getChar().getName()+" 加入了本服一星会员行列，欢呼吧！！！");
                        cm.serverNotice(5,"[vip公告]：恭喜玩家:"+cm.getChar().getName()+" 加入了本服一星会员行列，欢呼吧！！！");
                        cm.serverNotice(5,"[vip公告]：恭喜玩家:"+cm.getChar().getName()+" 加入了本服一星会员行列，欢呼吧！！！");
                        cm.serverNotice(5,"[vip公告]：恭喜玩家:"+cm.getChar().getName()+" 加入了本服一星会员行列，欢呼吧！！！");
                        cm.serverNotice(5,"[vip公告]：恭喜玩家:"+cm.getChar().getName()+" 加入了本服一星会员行列，欢呼吧！！！");
                        cm.sendOk("恭喜您，成功加入[一星会员]");
                    }else{
                        cm.sendOk("#r对不起，加入[一星会员]失败，需要120级才能加入哦，如果你没有达到160级，是不能给您办理的哟，或者您已经是[一星会员]了！");
                    }
                }else if (selection==1){
                    if(cm.getChar().GetMoney() >= 20000 && cm.getChar().getVip()<2){
                        player.GainMoney(-20000);
                        cm.getChar().SetVip(2);
                        cm.gainItem(3010013,1);
                        cm.gainItem(3010034,1);
                        cm.gainItem(3010035,1);
                        cm.gainItem(3010018,1);
                        cm.gainItem(1032081,1);
                        cm.gainItem(1132037,1);
                        cm.gainItem(1122082,1);
                        cm.gainItem(1112436,1);
                var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                var type = ii.getInventoryType(1112422); //获得装备的类形
                var toDrop = ii.randomizeStats(ii.getEquipById(1112422)).copy(); // 生成一个Equip类
                toDrop.setLocked(1);
                toDrop.setStr(300);
                toDrop.setDex(300);
                toDrop.setInt(300);
                toDrop.setLuk(300);
                toDrop.setHp(300);
                toDrop.setMp(300);
                toDrop.setMatk(300);
                toDrop.setWatk(300);
                toDrop.setMdef(300);
                toDrop.setWdef(300);
                toDrop.setAcc(300);
                toDrop.setAvoid(300);
                toDrop.setHands(300);
                toDrop.setSpeed(300);
                toDrop.setJump(300);
                cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
                cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背
                cm.getChar().saveToDB(true);
                cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null, net.sf.odinms.tools.MaplePacketCreator.serverNotice(17,cm.getC().getChannel(),"[vip公告]" + " : 恭喜玩家：" + cm.getPlayer().getName() +" 加入了本服中级会员行列，欢呼吧！！！",true).getBytes());
cm.sendOk("恭喜您，成功加入[中级会员]，祝您游戏愉快！");
                    }else{
                        cm.sendOk("#r对不起，加入[中级会员]失败，请确认你有足够的元宝，不够是不能给您办理的哟，或者您已经是[中级会员]了！");
                    }
                }else if (selection==2){
                    if(cm.getChar().GetMoney() >= 50000 && cm.getChar().getVip()<3){
                        player.GainMoney(-50000);
                        cm.getChar().SetVip(3);
                        cm.gainItem(3010013,1);
                        cm.gainItem(3010034,1);
                        cm.gainItem(3010035,1);
                        cm.gainItem(3010018,1);
                        cm.gainItem(1032081,1);
                        cm.gainItem(1132037,1);
                        cm.gainItem(1122082,1);
                        cm.gainItem(1112436,1);
                        cm.gainItem(3010061,1);
                        cm.gainItem(3010025,1);
                        cm.gainItem(1092076,1);
                        cm.gainItem(1372075,1);
                        cm.gainItem(1402087,1);
                        cm.gainItem(2340000,25);
						cm.gainItem(5390006,30); //老虎喇叭
						cm.gainItem(2340000,30); //祝福卷
						cm.gainItem(4001374,30); //GM卷30张
						cm.gainItem(4031692,20); //抽奖物品
						cm.gainItem(4031579,2);  //小宝物袋
						cm.gainItem(4031160,30);  //升级勋章
                var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                var type = ii.getInventoryType(1112422); //获得装备的类形
                var toDrop = ii.randomizeStats(ii.getEquipById(1112422)).copy(); // 生成一个Equip类
                toDrop.setLocked(1);
                toDrop.setStr(500);
                toDrop.setDex(500);
                toDrop.setInt(500);
                toDrop.setLuk(500);
                toDrop.setHp(500);
                toDrop.setMp(500);
                toDrop.setMatk(500);
                toDrop.setWatk(500);
                toDrop.setMdef(500);
                toDrop.setWdef(500);
                toDrop.setAcc(500);
                toDrop.setAvoid(500);
                toDrop.setHands(500);
                toDrop.setSpeed(500);
                toDrop.setJump(500);
                cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
                cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背
                cm.getChar().saveToDB(true);
                        cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null, net.sf.odinms.tools.MaplePacketCreator.serverNotice(17,cm.getC().getChannel(),"[vip公告]" + " : 恭喜玩家：" + cm.getPlayer().getName() +" 加入了本服高级会员行列，欢呼吧！！！",true).getBytes());
cm.sendOk("恭喜您，成功加入[高级会员]，祝您游戏愉快！");
                    }else{
                        cm.sendOk("#r对不起，加入[高级会员]失败，请确认你有足够的元宝，不够是不能给您办理的哟，或者您已经是[高会员]了！");
                    }
                }else if (selection==5){
                    if(cm.getChar().GetMoney() >= 100000 && cm.getChar().getVip()<4){
                        player.GainMoney(-100000);
                        cm.getChar().SetVip(4);
                        cm.gainItem(3010013,1);
                        cm.gainItem(3010034,1);
                        cm.gainItem(3010035,1);
                        cm.gainItem(3010018,1);
                        cm.gainItem(1032081,1);
                        cm.gainItem(1132037,1);
                        cm.gainItem(1122082,1);
                        cm.gainItem(1112436,1);
                        cm.gainItem(3010061,1);
                        cm.gainItem(3010025,1);
                        cm.gainItem(1092076,1);
                        cm.gainItem(1372075,1);
                        cm.gainItem(1402087,1);
                        cm.gainItem(3994106,1);
                        cm.gainItem(3010037,1);
                        cm.gainItem(1452106,1);
                        cm.gainItem(1342031,1);
                        cm.gainItem(1382097,1);
                        cm.gainItem(1402088,1);
                        cm.gainItem(1432079,1);
                        cm.gainItem(1452104,1);
                        cm.gainItem(2340000,40);
						cm.gainItem(5390006,50); //老虎喇叭
						cm.gainItem(2340000,50); //祝福卷
						cm.gainItem(4001374,50); //GM卷50张
						cm.gainItem(4031692,30); //抽奖物品
						cm.gainItem(4031579,3);  //小宝物袋
						cm.gainItem(4031160,50);  //升级勋章
                var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                var type = ii.getInventoryType(1112422); //获得装备的类形
                var toDrop = ii.randomizeStats(ii.getEquipById(1112422)).copy(); // 生成一个Equip类
                toDrop.setLocked(1);
                toDrop.setStr(800);
                toDrop.setDex(800);
                toDrop.setInt(800);
                toDrop.setLuk(800);
                toDrop.setHp(800);
                toDrop.setMp(800);
                toDrop.setMatk(800);
                toDrop.setWatk(800);
                toDrop.setMdef(800);
                toDrop.setWdef(800);
                toDrop.setAcc(800);
                toDrop.setAvoid(800);
                toDrop.setHands(800);
                toDrop.setSpeed(800);
                toDrop.setJump(800);
                cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
                cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背
                cm.getChar().saveToDB(true);
                        cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null, net.sf.odinms.tools.MaplePacketCreator.serverNotice(17,cm.getC().getChannel(),"[vip公告]" + " : 恭喜玩家：" + cm.getPlayer().getName() +" 加入了本服超级会员行列，欢呼吧！！！",true).getBytes());
cm.sendOk("恭喜您，成功加入[超级会员]，祝您游戏愉快！");
                    }else{
                        cm.sendOk("#r对不起，加入[超级会员]失败，请确认你有足够的元宝，不够是不能给您办理的哟，或者您已经是[超级会员]了！");
                    }
                }else if (selection==7){
                    if(cm.getChar().GetMoney() >= 150000 && cm.getChar().getVip()<5){
                        player.GainMoney(-150000);
                        cm.getChar().SetVip(5);
						cm.gainItem(3010013,1);
						cm.gainItem(3010034,1);
						cm.gainItem(3010035,1);
						cm.gainItem(3010018,1);
						cm.gainItem(3010061,1);
						cm.gainItem(3010025,1);
						cm.gainItem(3994106,1);
						cm.gainItem(3010037,1);
						cm.gainItem(3010094,1);
						cm.gainItem(3010093,1);
						cm.gainItem(1482109,1);//以下是110级传说
						cm.gainItem(1422078,1);
						cm.gainItem(1412076,1);
						cm.gainItem(1452136,1);
						cm.gainItem(1342046,1);
						cm.gainItem(1372106,1);
						cm.gainItem(1442143,1);
						cm.gainItem(1432106,1);
						cm.gainItem(1312077,1);
						cm.gainItem(1302179,1);
						cm.gainItem(1402118,1);//以上是110级传说
						cm.gainItem(5390006,60); //老虎喇叭
						cm.gainItem(2340000,70); //祝福卷
						cm.gainItem(4001374,70); //GM卷50张
						cm.gainItem(4031692,35); //抽奖物品
						cm.gainItem(4031579,5);  //小宝物袋
						cm.gainItem(4031160,70);  //升级勋章
                        cm.gainItem(2340000,60);
                var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                var type = ii.getInventoryType(1112422); //获得装备的类形
                var toDrop = ii.randomizeStats(ii.getEquipById(1112422)).copy(); // 生成一个Equip类
                toDrop.setLocked(1);
                toDrop.setStr(1500);
                toDrop.setDex(1500);
                toDrop.setInt(1500);
                toDrop.setLuk(1500);
                toDrop.setHp(1500);
                toDrop.setMp(1500);
                toDrop.setMatk(1500);
                toDrop.setWatk(1500);
                toDrop.setMdef(1500);
                toDrop.setWdef(1500);
                toDrop.setAcc(1500);
                toDrop.setAvoid(1500);
                toDrop.setHands(1500);
                toDrop.setSpeed(1500);
                toDrop.setJump(1500);
                cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
                cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背
                cm.getChar().saveToDB(true);
                        cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null, net.sf.odinms.tools.MaplePacketCreator.serverNotice(17,cm.getC().getChannel(),"[vip公告]" + " : 恭喜玩家：" + cm.getPlayer().getName() +" 加入了本服顶级会员行列，欢呼吧！！！",true).getBytes());
			cm.sendOk("恭喜您，成功加入[顶级会员]，祝您游戏愉快！");}else{
                        cm.sendOk("#r对不起，加入[顶级会员]失败，请确认你有足够的元宝，不够是不能给您办理的哟，或者您已经是[顶级会员]了！");
                    }
                }else if (selection==9){
                    if(cm.getChar().GetMoney() >= 180000 && cm.getChar().getVip()<6){
                        player.GainMoney(-180000);
                        cm.getChar().SetVip(6);
                        cm.gainItem(3010013,1);
                        cm.gainItem(3010034,1);
                        cm.gainItem(3010035,1);
                        cm.gainItem(3010018,1);
                        cm.gainItem(3010061,1);
                        cm.gainItem(3010025,1);
                        cm.gainItem(3994106,1);
                        cm.gainItem(3010037,1);
                        cm.gainItem(3010094,1);
                        cm.gainItem(3010093,1);
                        cm.gainItem(3012001,1);
                        cm.gainItem(3012010,1);
                        cm.gainItem(3012003,1);
                        cm.gainItem(1032084,1);
                        cm.gainItem(1132040,1);
                        cm.gainItem(1122085,1);
                        cm.gainItem(1112439,1);
                        cm.gainItem(1302147,1);
                        cm.gainItem(1342033,1);
                        cm.gainItem(1452106,1);
                        cm.gainItem(1442039,1);
                        cm.gainItem(1402014,1);
                        cm.gainItem(1302063,1);
                        cm.gainItem(5062001,30);
                        cm.gainItem(5062002,30);
                        cm.gainItem(5062000,30);
                        cm.gainItem(2340000,100);
						cm.gainItem(1482109,1);//以下是110级传说
						cm.gainItem(1422078,1);
						cm.gainItem(1412076,1);
						cm.gainItem(1452136,1);
						cm.gainItem(1342046,1);
						cm.gainItem(1402118,1);//以上是110级传说
						cm.gainItem(1002702,1);//传说头巾
						cm.gainItem(1102319,1);//传说心跳气球
						cm.gainItem(1112668,1);//传说帽子
						cm.gainItem(5390006,70); //老虎喇叭
						cm.gainItem(2340000,80); //祝福卷
						cm.gainItem(4001374,80); //GM卷50张
						cm.gainItem(4031692,50); //抽奖物品
						cm.gainItem(4031579,6);  //小宝物袋
						cm.gainItem(4031160,80);  //升级勋章
                var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
                var type = ii.getInventoryType(1112422); //获得装备的类形
                var toDrop = ii.randomizeStats(ii.getEquipById(1112422)).copy(); // 生成一个Equip类
                toDrop.setLocked(1);
                toDrop.setStr(3000);
                toDrop.setDex(3000);
                toDrop.setInt(3000);
                toDrop.setLuk(3000);
                toDrop.setHp(3000);
                toDrop.setMp(3000);
                toDrop.setMatk(3000);
                toDrop.setWatk(3000);
                toDrop.setMdef(3000);
                toDrop.setWdef(3000);
                toDrop.setAcc(3000);
                toDrop.setAvoid(3000);
                toDrop.setHands(3000);
                toDrop.setSpeed(3000);
                toDrop.setJump(3000);
                cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
                cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背
                cm.getChar().saveToDB(true);
                        cm.getC().getChannelServer().getWorldInterface().broadcastMessage(null, net.sf.odinms.tools.MaplePacketCreator.serverNotice(17,cm.getC().getChannel(),"[vip公告]" + " : 恭喜玩家：" + cm.getPlayer().getName() +" 加入了本服神级会员行列，欢呼吧！！！",true).getBytes());
cm.sendOk("恭喜您，成功加入[神级会员]，祝您游戏愉快！");
                    }else{
 cm.sendOk("#r对不起，加入[神级会员]失败，请确认你有足够的元宝，不够是不能给您办理的哟，或者您已经是[神级会员]了！");
                    cm.dispose();
                    return;
		}

		  }
          	    }
            if(typed==211){
                if(cm.getChar().getVip()>=2){
                    var statup = new java.util.ArrayList();
                    var p = cm.getChar();
                    var totAp = p.getRemainingAp() + p.getStr() + p.getDex() + p.getInt() + p.getLuk();
                    p.setRemainingAp(totAp-16);
                    p.setStr(4);
                    p.setDex(4);
                    p.setInt(4);
                    p.setLuk(4);
                    statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.STR, java.lang.Integer.valueOf(4)));
                    statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.DEX, java.lang.Integer.valueOf(4)));
                    statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.LUK, java.lang.Integer.valueOf(4)));
                    statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.INT, java.lang.Integer.valueOf(4)));
                    statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.AVAILABLEAP, java.lang.Integer.valueOf(p.getRemainingAp())));
                    p.getClient().getSession().write (net.sf.odinms.tools.MaplePacketCreator.updatePlayerStats(statup,p));
                    cm.sendOk("恭喜您，重置成功，由于你是会员，本次免费！");
                    cm.dispose();
                    return;
                }
                if(cm.haveItem(5050000,10)){
                    cm.gainItem(5050000,-10);
                    var statup = new java.util.ArrayList();
                    var p = cm.getChar();
                    var totAp = p.getRemainingAp() + p.getStr() + p.getDex() + p.getInt() + p.getLuk();
                    p.setRemainingAp(totAp-16);
                    p.setStr(4);
                    p.setDex(4);
                    p.setInt(4);
                    p.setLuk(4);
                    statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.STR, java.lang.Integer.valueOf(4)));
                    statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.DEX, java.lang.Integer.valueOf(4)));
                    statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.LUK, java.lang.Integer.valueOf(4)));
                    statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.INT, java.lang.Integer.valueOf(4)));
                    statup.add (new net.sf.odinms.tools.Pair(net.sf.odinms.client.MapleStat.AVAILABLEAP, java.lang.Integer.valueOf(p.getRemainingAp())));
                    p.getClient().getSession().write (net.sf.odinms.tools.MaplePacketCreator.updatePlayerStats(statup,p));
                    cm.sendOk("恭喜您，重置成功，扣除10张[洗能力点卷轴]！");
                    cm.dispose();
                    return;
                }else{
                    cm.sendOk("#r对不起，您没有足够的[洗能力点卷轴]，请到商城去购买！");
                    cm.dispose();
                    return;
                }
            }
        }
    }
}
