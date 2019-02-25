importPackage(net.sf.odinms.client);

var selStr;
var typeszq=new Array("1702282","1702280","1702281","1702283","1002957","1002737","1002883","1002907","1002935","1002845","1002849","1002851","1002877","1002986","1002972","1002969","1002962","1002945","1002930","1002886","1002868","1002840","1002825","1002838","1002918","1002996","1002997","1022077","1012135","1012148","1012157","1002931","1002965","1102166","1012146","1012141","1012140","1012139","1032063","1102173","1002744","1002751","1002944","1002968","1002953","1702222","1702199","5010086","1002731","1002920","1002921","1002922","1002928","1002956","1002961","1002971","1002976","1002982","1002995","1002976");
var typesitemzqcost=new Array("50","50","50","50","200","500","1000","250","50","80","40","30","20","50","250","50","50","99","999","39","88","99","19","250","250","250","50","100","1000","1000","1000","100","100","100","1000","800","800","800","300","500","500","50","200","200","2000","100","1000","2000","200","100","100","50","250","250","50","30","200","50","50","200");
var typed=0;

var typesqc=new Array("盘龙七冲枪","金龙轰天锤","炼狱魔龙斧","飞龙巨剑","黑精灵王杖","佘太君龙杖","黄金双牙钩","金龙振翅弓","黄金飞龙弩","逆龙咆哮拳","碧水落龙拳");
var typesitemqc=new Array("1432038","1422028","1412026","1402036","1382036","1372032","1332051","1452044","1462039","1472053","1472052");

var types=new Array("永恒显圣枪","永恒神光戟","永恒蝶翼杖","永恒冰轮杖","永恒断手刃[飞短刀-运]","永恒威震天","永恒狂鲨锯[飞短刀-力]","永恒惊电弓","永恒冥雷弩","永恒大悲赋[拳套]","永恒孔雀翎[海盗刀]","永恒凤凰锍[海盗枪]","永恒飞天刃[副刀]");
var typesitem=new Array("1432047","1442063","1372044","1382057","1332074","1432037","1332073","1452057","1462050","1472068","1482023","1492023","1342011");

var typesqced=new Array("玩具木马","青蛙","鸵鸟","鳄鱼王","玩具坦克","泰国牛","绿毛龟","筋斗云","打豆豆机器人","女女机车","男男机车");
var typesitemqced=new Array("1902004","1902008","1902009","1902019","1902014","1902013","1902011","1902028","1902034","1902038","1902039");
var typesitemqceded=new Array("1912002","1912003","1912004","1912012","1912010","1912009","1912007","1912021","1912027","1912031","1912032");

var typeszy=new Array("3010029","3010030","3010035","3010036","3010037","3010039","3010047","3010050","3010052","3010054","3010055","3010061","3010069","3010093","3010062","3012006","3010058","3012001","3010075");



function start() {
	status = -1;
	
	action(1, 0, 0);
}

function action(mode, type, selection) {
            if (mode == -1) {
                cm.dispose();
            }
            else {
                if (status >= 0 && mode == 0) {
			//cm.sendOk("#e#r讨厌，点你姐姐哪呀~~..#k");
			cm.dispose();
			return;                    
                }
                if (mode == 1) {
			status++;
		}
		else {
			status--;
		}
		        if (status == 0) {
					cm.sendSimple("#r你好，我的朋友，我的ID:"+cm.getNpc()+"\r\n我是本服的属性装备特卖员,有什么需要我效劳的么~~#b\r\n#r#L50#┈═☆我要购买全属性3000装备[所有属性]☆═┈#l\r\n#r#L51#┈═☆我要购买全属性5000装备[所有属性]#n☆═┈#l\r\n#r#L52#┈═☆我要购买全属性10000装备[所有属性]#n☆═┈#l\r\n#r#L53#┈═☆我要购买全属性20000装备[所有属性]#n☆═┈#l\r\n#r#L54#┈═☆我要购买全属性32767装备[所有属性]#n☆═┈#l\r\n#r#L6#┈═☆我要购买全属性32767装备[140级装备全属性]#n☆═#l\r\n#r#L7#┈═☆我要购买全属性32767装备[140级武器全属性]#n☆═#l");
			
			//cm.sendSimple("#r#L1#初级VIP乐园#l #L2#高级VIP探秘#l #g#L3#超级VIP圣殿①#l \r\n\r\n #b#L40#■新装备■#l#L41#■新道具■#l#L441#■金袋子可爱玩具专卖■#l\r\n#L6#1.超级白货店#l #L7#2.所有卷轴#l #L8#3.宠物常用#l#r#L801#4.购买喇叭#l \r\n\r\n #d#e#L9#成为初级VIP#l #L10#超级VIP圣殿②#l #L11#超级VIP圣殿③#l#n  \r\n\r\n#e#b#L12#初级VIP工资#l#k#n #e#g#L13#高级VIP工资#l#k#n #e#r#L14#超级VIP工资#l#k#n \r\n\r\n #e-------┈━═☆各职业装备☆═━┈---------#l#k#n\r\n #e#r#L15#┈━═☆全战士装备#n#k#l #r#e#L16#┈━═☆全法师装备#n#k#l\r\n\r\n #e#b#L17#┈━═☆全飞侠装备#n#k#l #b#e#L18#┈━═☆全弓箭手装备#n#k#l" );
			} else if (status == 1) {
				 if (selection == 441) {
					 typed=441;
						selStr = "#e#b以下的玩具你都见过吗？\r\n#r什么...没见过？？？你Out啦..zZZzx@#@$...\r\n#n#r请选择你要购买的玩具.[2011年1月1日更新以下物品]#b";
						for (var i = 0; i < typeszq.length; i++) {
							selStr += "\r\n#L" + i + "##v" +typeszq[i]+"##r需要["+typesitemzqcost[i]+"个金袋子]#l";
						}
						cm.sendSimple(selStr);	
				}
				if (selection == 401) {
						cm.openShop(56);
						cm.dispose();    
				}
				if (selection == 41) {
						cm.openShop(198509);
						cm.dispose();    
						  
				} 
				if (selection == 6) {
                       cm.openNpc(9310055);
				}
				 if (selection == 7) {
                       cm.openNpc(9310068);
                }
				if (selection == 8) {                      
                       cm.openShop(1333);
                       cm.dispose();
                }
				if (selection == 40) {                      
                       cm.openShop(56);
                       cm.dispose();   
				}
         			 if (selection == 50) {
				              cm.openNpc(9310063);
				}
				if (selection == 51) {
				              cm.openNpc(9310064);
				}
				if (selection == 52) {
				              cm.openNpc(9310065);
				}
				if (selection == 53) {
				              cm.openNpc(9310066);
				}
				if (selection == 54) {
				              cm.openNpc(9310067);  
				}
				if (selection == 55) {
						cm.warp(910000021);
						cm.dispose();    
				}
				if (selection == 56) {
						cm.warp(910000021);
						cm.dispose();    
				}
				if (selection == 57) {
						cm.warp(910000021);
						cm.dispose();    
				}
				if (selection == 58) {
						cm.warp(910000021);
						cm.dispose();    
				}
				if (selection == 801) {
                       cm.sendOk("#b请去市场2楼找圣杯喇叭商人购买！.");
						cm.dispose();   
				}
				if (selection == 1) {
					if(cm.getChar().getVip() == 1) {
						cm.warp(209000000, 0);
						cm.sendOk("#b你的会员等级:#rVIP1-初级会员#k\r\n祝您旅途愉快，自动传送到#rVIP1-初级会员专属地图.");
						cm.dispose();
					}else{
						typed=1;
						cm.sendSimple("尊敬的会员:请选择你要去的会员专属地图#b\r\n#L2#VIP1-VIP2公用地图#r[所有玩家可进入]#l.\r\n#L3#VIP3-黄金VIP3专属地图#r[限制VIP3或者以上玩家进入]#l.\r\n#L4#VIP4-至尊VIP4专属地图#r[限制VIP4或者以上玩家进入]#l.\r\n#L5#VIP5-终极VIP5专属地图#r[限制VIP5或者以上玩家进入]#l.\r\n#L6#VIP6-顶级VIP6专属地图#r[限制VIP6或者以上玩家进入]#l");
					}
				}
				if (selection == 2) {
						typed=2;
						cm.sendSimple("尊敬的会员:欢迎你的光临. 您的会员等级:#r[VIP"+cm.getChar().getVip()+"]#k\r\n<又是清爽的一天,希望我能给您带来好心情..>#b\r\n#L1#我是VIP1-初级会员#r[领取今日工资]#b#l\r\n#L2#我是VIP2-高级会员#r[领取今日工资]#b#l\r\n#L3#我是VIP3-超级会员#r[领取今日工资]#b#l\r\n#L4#我是VIP4-至尊会员#r[领取今日工资]#b#l\r\n#L5#我是VIP5-终极会员#r[领取今日工资]\r\n#L6#我是VIP6-顶级会员#r[领取今日工资]#l\r\n\r\n<每天限领一次哟,领后需要等待24小时后可在领.>");
				}
				if (selection == 3) {
						typed=3;
						cm.sendSimple("尊敬的会员:欢迎你的光临.\r\n<会员装备只能领取一次,领取前请确认包包有两个空格，否则领取失败自己负责>\r\n#r<如果您的会员是升级上来的,本服每日工资领装备领椅子,很丰富哦！>#b\r\n#L3#领取110级装备[可以任选一把][限制VIP3领取]#b#l\r\n#L4#领取120级装备[可以任选一把][限VIP4-VIP6领取]#b#l\r\n#L5#领取椅子一把[限VIP4-VIP6领取]#l\r\n#L6#领取骑宠一只[限VIP5-VIP6领取]#l");
				}
				if (selection == 15) {
                       cm.openShop(1358);
                       cm.dispose();   
				} 
				if (selection == 16) {                      
                       cm.openShop(1359);
                       cm.dispose();   
				}
				if (selection == 17) {
                       cm.openShop(1360);
                       cm.dispose();   
				} 
				if (selection == 18) {                      
                       cm.openShop(1361);
                       cm.dispose();   
				}


			} else if (status == 2) {
				if(typed==441){
						if(cm.haveItem(5200002,typesitemzqcost[selection])==true){
							cm.gainItem(typeszq[selection],1);
							cm.gainItem(5200002,-typesitemzqcost[selection]);
							cm.sendOk("恭喜你，购买成功，快去查看一下包包吧.");
							cm.dispose();
						}else{
							cm.sendOk("对不起，你没有足够的金袋子.");
							cm.dispose();
						}
				}
				if(typed==3){
					if (selection == 6) {//qc
						typed=13;
						selStr = "请选择你要领取的骑宠.\r\n#r<注意：装备栏至少要留两格空格,否则造成领取失败后果自负>#b";
						for (var i = 0; i < typesqced.length; i++) {
							selStr += "\r\n#b#L" + i + "#" +typesqced[i]+"#l";
						}
						cm.sendSimple(selStr);
					}
					if (selection == 5) {//yizi
						typed=12;
						selStr = "请选择你要领取的椅子.\r\n#r<注意：仅限V4-V6领，每日只能领一次。>#b";
						for (var i = 0; i < typeszy.length; i++) {
							selStr += "#b#L" + i + "##v" +typeszy[i]+"##l";
						}
						cm.sendSimple(selStr);
					}
					if (selection == 3) {//110级装备
						typed=10;
						selStr = "请选择你要领取的武器.\r\n#r<注意：装备栏第一格不能放任何装备>\r\n#b";
						for (var i = 0; i < typesqc.length; i++) {
							selStr += "\r\n#b#L" + i + "#" +typesqc[i]+"#l";
						}
						cm.sendSimple(selStr);
					}
					if (selection == 4) {//120级装备
						typed=11;
						selStr = "请选择你要领取的武器.\r\n#r<注意：装备栏第一格不能放任何装备>#b";
						for (var i = 0; i < types.length; i++) {
							selStr += "\r\n#b#L" + i + "#" +types[i]+"#l";
						}
						cm.sendSimple(selStr);
					}
					if (selection == 2) {//超级头盔
						if(cm.getChar().getVip() == 1) {
							cm.sendOk("对不起！领取失败.您是VIP1--初级会员，无法领取，请办理更高的会员.");
							cm.dispose();
						}
						if(cm.getChar().getVip() == 2 ) {
							if (cm.getBossLog('VIPZBTK') < 1){
            var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
            var type = ii.getInventoryType(1082002); //获得装备的类形
            var toDrop = ii.randomizeStats(ii.getEquipById(1082002)).copy(); // 生成一个Equip类
toDrop.setStr(500);
toDrop.setDex(500);
toDrop.setInt(500);
toDrop.setLuk(500);
toDrop.setHp(1);
toDrop.setMp(1);
toDrop.setMatk(1);
toDrop.setWatk(1);
toDrop.setMdef(1);
toDrop.setWdef(1);
toDrop.setAcc(1);
toDrop.setAvoid(1);
toDrop.setHands(1);
toDrop.setSpeed(1);
toDrop.setJump(1);
cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包		
cm.getChar().saveToDB(true);
								cm.setBossLog('VIPZBTK');
								cm.sendOk("恭喜您，尊敬的VIP2-高级会员:\r\n#r您已经成功的领取了#r全属性超级头盔+500#v1082002#");
								cm.dispose();
							}else{
								cm.sendOk("对不起！领取失败.您已经领取过了.");
								cm.dispose();
							}
						}
						if(cm.getChar().getVip() == 3 ) {
							if (cm.getBossLog('VIPZBTK') < 1){
            var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
            var type = ii.getInventoryType(1082002); //获得装备的类形
            var toDrop = ii.randomizeStats(ii.getEquipById(1082002)).copy(); // 生成一个Equip类
toDrop.setStr(1000);
toDrop.setDex(1000);
toDrop.setInt(1000);
toDrop.setLuk(1000);
toDrop.setHp(1);
toDrop.setMp(1);
toDrop.setMatk(1);
toDrop.setWatk(1);
toDrop.setMdef(1);
toDrop.setWdef(1);
toDrop.setAcc(1);
toDrop.setAvoid(1);
toDrop.setHands(1);
toDrop.setSpeed(1);
toDrop.setJump(1);
cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包		
cm.getChar().saveToDB(true);
								cm.setBossLog('VIPZBTK');
								cm.sendOk("恭喜您，尊敬的VIP3-超级会员:\r\n#r您已经成功的领取了#r全属性超级头盔+1000#v1082002#");
								cm.dispose();
							}else{
								cm.sendOk("对不起！领取失败.您已经领取过了.");
								cm.dispose();
							}
						}
						if(cm.getChar().getVip() == 4 ) {
							if (cm.getBossLog('VIPZBTK') < 1){
            var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
            var type = ii.getInventoryType(1082002); //获得装备的类形
            var toDrop = ii.randomizeStats(ii.getEquipById(1082002)).copy(); // 生成一个Equip类
toDrop.setStr(1500);
toDrop.setDex(1500);
toDrop.setInt(1500);
toDrop.setLuk(1500);
toDrop.setHp(1);
toDrop.setMp(1);
toDrop.setMatk(1);
toDrop.setWatk(1);
toDrop.setMdef(1);
toDrop.setWdef(1);
toDrop.setAcc(1);
toDrop.setAvoid(1);
toDrop.setHands(1);
toDrop.setSpeed(1);
toDrop.setJump(1);
cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包		
cm.getChar().saveToDB(true);
								cm.setBossLog('VIPZBTK');
								cm.sendOk("恭喜您，尊敬的VIP4-至尊会员:\r\n#r您已经成功的领取了#r全属性超级头盔+1500#v1082002#");
								cm.dispose();
							}else{
								cm.sendOk("对不起！领取失败.您已经领取过了.");
								cm.dispose();
							}
						}
                                                if(cm.getChar().getVip() == 6 ) {
							if (cm.getBossLog('VIPZBTK') < 1){
            var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
            var type = ii.getInventoryType(1082002); //获得装备的类形
            var toDrop = ii.randomizeStats(ii.getEquipById(1082002)).copy(); // 生成一个Equip类
toDrop.setStr(3000);
toDrop.setDex(3000);
toDrop.setInt(3000);
toDrop.setLuk(3000);
toDrop.setHp(1);
toDrop.setMp(1);
toDrop.setMatk(1);
toDrop.setWatk(1);
toDrop.setMdef(1);
toDrop.setWdef(1);
toDrop.setAcc(1);
toDrop.setAvoid(1);
toDrop.setHands(1);
toDrop.setSpeed(1);
toDrop.setJump(1);
cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包		
cm.getChar().saveToDB(true);
								cm.setBossLog('VIPZBTK');
								cm.sendOk("恭喜您，尊敬的VIP6-至尊会员:\r\n#r您已经成功的领取了#r全属性超级头盔+3000#v1082002#");
								cm.dispose();
							}else{
								cm.sendOk("对不起！领取失败.您已经领取过了.");
								cm.dispose();
							}
						}
                                                if(cm.getChar().getVip() == 7 ) {
							if (cm.getBossLog('VIPZBTK') < 1){
            var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
            var type = ii.getInventoryType(1082002); //获得装备的类形
            var toDrop = ii.randomizeStats(ii.getEquipById(1082002)).copy(); // 生成一个Equip类
toDrop.setStr(5000);
toDrop.setDex(5000);
toDrop.setInt(5000);
toDrop.setLuk(5000);
toDrop.setHp(1);
toDrop.setMp(1);
toDrop.setMatk(1);
toDrop.setWatk(1);
toDrop.setMdef(1);
toDrop.setWdef(1);
toDrop.setAcc(1);
toDrop.setAvoid(1);
toDrop.setHands(1);
toDrop.setSpeed(1);
toDrop.setJump(1);
cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包		
cm.getChar().saveToDB(true);

								cm.setBossLog('VIPZBTK');
								cm.sendOk("恭喜您，尊敬的VIP7-无敌会员:\r\n#r您已经成功的领取了#r全属性超级头盔+4000#v1082002#");
								cm.dispose();
							}else{
								cm.sendOk("对不起！领取失败.您已经领取过了.");
								cm.dispose();
							}
						}
						if(cm.getChar().getVip() == 5 ) {
							if (cm.getBossLog('VIPZBTK') < 1){
            var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
            var type = ii.getInventoryType(1082002); //获得装备的类形
            var toDrop = ii.randomizeStats(ii.getEquipById(1082002)).copy(); // 生成一个Equip类
toDrop.setStr(2000);
toDrop.setDex(2000);
toDrop.setInt(2000);
toDrop.setLuk(2000);
toDrop.setHp(1);
toDrop.setMp(1);
toDrop.setMatk(1);
toDrop.setWatk(1);
toDrop.setMdef(1);
toDrop.setWdef(1);
toDrop.setAcc(1);
toDrop.setAvoid(1);
toDrop.setHands(1);
toDrop.setSpeed(1);
toDrop.setJump(1);
cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包		
cm.getChar().saveToDB(true);
								cm.setBossLog('VIPZBTK');
								cm.sendOk("恭喜您，尊敬的VIP5-终级会员:\r\n#r您已经成功的领取了#r全属性超级头盔+2000>#v1082002#");
								cm.dispose();
							}else{
								cm.sendOk("对不起！领取失败.您已经领取过了.");
								cm.dispose();
							}
						}
						cm.dispose();
					}
					if (selection == 7) {
						if(cm.getChar().getVip() == 1) {
							cm.sendOk("对不起！领取失败.您是VIP1--初级会员，无法领取，请办理更高的会员.");
							cm.dispose();
						}
						if(cm.getChar().getVip() == 2 ) {
							if (cm.getBossLog('VIPZBBF2') < 1){
								cm.gainItem(4031678,1);
								cm.setBossLog('VIPZBBF2');
								cm.sendOk("恭喜您，尊敬的VIP2-高级会员:\r\n#r您已经成功的领取了#r装备保护钥匙1个>#v4031678#");
								cm.dispose();
							}else{
								cm.sendOk("对不起！领取失败.您已经领取过了.");
								cm.dispose();
							}
						}
						if(cm.getChar().getVip() == 3 ) {
							if (cm.getBossLog('VIPZBBF3') < 1){
								cm.gainItem(4031678,2);
								cm.setBossLog('VIPZBBF3');
								cm.sendOk("恭喜您，尊敬的VIP3-超级会员:\r\n#r您已经成功的领取了#r装备保护钥匙2个>#v4031678#");
								cm.dispose();
							}else{
								cm.sendOk("对不起！领取失败.您已经领取过了.");
								cm.dispose();
							}
						}
						if(cm.getChar().getVip() == 4 ) {
							if (cm.getBossLog('VIPZBBF4') < 1){
								cm.gainItem(4031678,3);
								cm.setBossLog('VIPZBBF4');
								cm.sendOk("恭喜您，尊敬的VIP4-至尊会员:\r\n#r您已经成功的领取了#r装备保护钥匙3个>#v4031678#");
								cm.dispose();
							}else{
								cm.sendOk("对不起！领取失败.您已经领取过了.");
								cm.dispose();
							}
						}
                                                if(cm.getChar().getVip() == 5 ) {
							if (cm.getBossLog('VIPZBBF5') < 1){
								cm.gainItem(4031678,4);
								cm.setBossLog('VIPZBBF5');
								cm.sendOk("恭喜您，尊敬的VIP5-超级会员:\r\n#r您已经成功的领取了#r装备保护钥匙4个>#v4031678#");
								cm.dispose();
							}else{
								cm.sendOk("对不起！领取失败.您已经领取过了.");
								cm.dispose();
							}
						}
                                                if(cm.getChar().getVip() == 6 ) {
							if (cm.getBossLog('VIPZBBF6') < 1){
								cm.gainItem(4031678,5);
								cm.setBossLog('VIPZBBF6');
								cm.sendOk("恭喜您，尊敬的VIP6-顶级会员:\r\n#r您已经成功的领取了#r装备保护钥匙5个>#v4031678#");
								cm.dispose();
							}else{
								cm.sendOk("对不起！领取失败.您已经领取过了.");
								cm.dispose();
							}
						}
                                                if(cm.getChar().getVip() == 7 ) {
							if (cm.getBossLog('VIPZBBF7') < 1){
								cm.gainItem(4031678,6);
								cm.setBossLog('VIPZBBF7');
								cm.sendOk("恭喜您，尊敬的VIP7-无敌会员:\r\n#r您已经成功的领取了#r装备保护钥匙6个>#v4031678#");
								cm.dispose();
							}else{
								cm.sendOk("对不起！领取失败.您已经领取过了.");
								cm.dispose();
							}
						}
						cm.dispose();
					}
				}
				if(typed==2){
					if (selection == 1) {
						if(cm.getChar().getVip() == 1) {
							if (cm.getBossLog('vip1ds') >= 1 || cm.getBossLog('vipgz') >= 1) {
								cm.sendOk("对不起！领取失败.可能的原因是:\r\n#r1.您今天已经领取过了。\r\n2.你是今天才加入的新手--初级VIP，为了防止少数人利用此功能刷钱，所以暂时无法领取，请明天在来领取.");
								cm.dispose(); 
							}else{
						                cm.gainMeso(100000000); 
						                cm.gainItem(4031454,1);//beizi
								cm.setBossLog('vipgz');
								cm.sendOk("恭喜您,尊敬的[一星会员]，今天的工资已经领取完毕，在接在历，明天记得来哟！");
								cm.dispose();
							}
						}else{
							cm.sendOk("#b你的会员等级:#rVIP"+cm.getChar().getVip()+"#k\r\n领取失败,只能是VIP1--初级会员领取.");
							cm.dispose();
						}
					}
					if (selection == 2) {
						if(cm.getChar().getVip() == 2) {
							if (cm.getBossLog('vipgz') >= 1) {
								cm.sendOk("对不起！领取失败.可能的原因是:\r\n#r1.您今天已经领取过了,请明天在来领取.");
								cm.dispose(); 
							}else{
						               //cm.gainNX(10000);
						               //cm.setzb(1000);
								cm.setBossLog('vipgz');
						               //cm.getChar().setgodpoint(50);
						               //cm.setjf(5);
						               cm.gainItem(5390000,2);//喇叭
                                                               cm.gainItem(5390001,2);//喇叭
                                                               cm.gainItem(5390002,2);
                                                               cm.gainItem(4002000,1);//喇叭
						               cm.gainItem(4031454,3);//beizi
						               cm.gainMeso(30000000);
								cm.sendOk("恭喜您,尊敬的[二星会员]，今天的工资已经领取完毕，在接在历，明天记得来哟！");
								cm.dispose();
							}
						}else{
							cm.sendOk("#b你的会员等级:#rVIP"+cm.getChar().getVip()+"#k\r\n领取失败,只能是VIP2--高级会员领取.");
							cm.dispose();
						}
					}
					if (selection == 3) {
						if(cm.getChar().getVip() == 3) {
							if (cm.getBossLog('vipgz') >= 1) {
								cm.sendOk("对不起！领取失败.可能的原因是:\r\n#r1.您今天已经领取过了,请明天在来领取.");
								cm.dispose(); 
							}else{
								//cm.gainNX(500);
						                //cm.setzb(3000);
								cm.setBossLog('vipgz');
						                cm.gainItem(5390000,3);//喇叭
                                                                cm.gainItem(5390001,3);//喇叭
                                                                cm.gainItem(5390002,3);
                                                                cm.gainItem(4002000,2);
						                cm.gainItem(4031454,4);//beizi
						                cm.gainMeso(100000000);
								cm.sendOk("恭喜您,尊敬的[三星会员]，今天的工资已经领取完毕，在接在历，明天记得来哟！");
								cm.dispose();
							}
						}else{
							cm.sendOk("#b你的会员等级:#rVIP"+cm.getChar().getVip()+"#k\r\n领取失败,只能是VIP3--黄金会员领取.");
						}
					}
					if (selection == 4) {
						if(cm.getChar().getVip() == 4) {
							if (cm.getBossLog('vipgz') >= 1) {
								cm.sendOk("对不起！领取失败.可能的原因是:\r\n#r1.您今天已经领取过了,请明天在来领取.");
								cm.dispose(); 
							}else{
						                //cm.gainNX(1000);
						                //cm.setzb(8000);
								cm.setBossLog('vipgz');
						                cm.gainItem(5390000,4);//喇叭
                                                                cm.gainItem(5390001,4);//喇叭
                                                                cm.gainItem(5390002,4);//喇叭
                                                                cm.gainItem(4002000,3);
						                cm.gainItem(4031454,5);//beizi
						                cm.gainMeso(200000000);
								cm.sendOk("恭喜您,尊敬的[四星会员]，今天的工资已经领取完毕，在接在历，明天记得来哟！");
								cm.dispose();
							}
						}else{
							cm.sendOk("#b你的会员等级:#rVIP"+cm.getChar().getVip()+"#k\r\n领取失败,只能是VIP4--至尊会员领取.");
							cm.dispose();
						}
					}
					if (selection == 5) {
						if(cm.getChar().getVip() == 5) {
							if (cm.getBossLog('vipgz') >= 1) {
								cm.sendOk("对不起！领取失败.可能的原因是:\r\n#r1.您今天已经领取过了,请明天在来领取.");
								cm.dispose(); 
							}else{
						                //cm.gainNX(1800);
						                //cm.setzb(12000);
								cm.setBossLog('vipgz');
						                //cm.getChar().setgodpoint(150);
						                //cm.setjf(8);
						                cm.gainItem(5390000,6);//喇叭
                                                                cm.gainItem(5390001,6);//喇叭
                                                                cm.gainItem(5390002,6);//喇叭
                                                                cm.gainItem(4002000,6);
						                cm.gainItem(4031454,7);//beizi
						                cm.gainMeso(500000000); 
								cm.sendOk("恭喜您,尊敬的[五星会员]，今天的工资已经领取完毕，在接在历，明天记得来哟！");
								cm.dispose();
							}
						}else{
							cm.sendOk("#b你的会员等级:#rVIP"+cm.getChar().getVip()+"#k\r\n领取失败,只能是VIP5--超级会员领取.");
							cm.dispose();
						}
					}
                                        if (selection == 6) {
						if(cm.getChar().getVip() == 6) {
							if (cm.getBossLog('vipgz') >= 1) {
								cm.sendOk("对不起！领取失败.可能的原因是:\r\n#r1.您今天已经领取过了,请明天在来领取.");
								cm.dispose(); 
							}else{
						                //cm.gainNX(30000);
						                //cm.setzb(15000);
								cm.setBossLog('vipgz');
						                //cm.getChar().setgodpoint(200);
						                //cm.setjf(10);
						                cm.gainItem(5390000,10);//喇叭
                                                                cm.gainItem(5390001,10);//喇叭
                                                                cm.gainItem(5390002,10);//喇叭
						                cm.gainMeso(1500000000);
						                cm.gainItem(4031454,10);//beizi
                                                                cm.gainItem(4002000,10);
								cm.sendOk("恭喜您,尊敬的[六星会员]，今天的工资已经领取完毕，在接在历，明天记得来哟！");
								cm.dispose();
							}
						}else{
							cm.sendOk("#b你的会员等级:#rVIP"+cm.getChar().getVip()+"#k\r\n领取失败,只能是VIP6--顶级会员领取.");
							cm.dispose();
						}
					}
                                        if (selection == 7) {
						if(cm.getChar().getVip() == 7) {
							if (cm.getBossLog('vipgz') >= 1) {
								cm.sendOk("对不起！领取失败.可能的原因是:\r\n#r1.您今天已经领取过了,请明天在来领取.");
								cm.dispose(); 
							}else{
								cm.gainItem(5200002,10); 
								cm.setBossLog('vipgz');
								cm.gainItem(4001129,25);
								cm.gainItem(5390002,10);
								cm.gainItem(5390006,1);
								cm.gainItem(5390005,1);
								cm.gainNX(3000);
								cm.sendOk("恭喜，成功领取25个转生币,10个金袋子,10000点卷，10个情景喇叭，2虎咆哮喇叭，2个新年喇叭，在接在历，明天记得来哟！");
								cm.dispose();
							}
						}else{
							cm.sendOk("#b你的会员等级:#rVIP"+cm.getChar().getVip()+"#k\r\n领取失败,只能是VIP6--终级会员领取.");
							cm.dispose();
						}
					}


				}
				if(typed==1){
					if (selection == 1) {
						cm.warp(209000000, 0);
						cm.sendOk("#b你的会员等级:#rVIP"+cm.getChar().getVip()+"#k\r\n已传送到目的地.祝您旅途愉快.");
						cm.dispose();
					}
					if (selection == 2) {
						if(cm.getChar().getVip() >= 1) {
							cm.warp(209000001, 0);
							cm.sendOk("#b你的会员等级:#rVIP"+cm.getChar().getVip()+"#k\r\n已传送到目的地.祝您旅途愉快.");
							cm.dispose();
						}else{
							cm.sendOk("#b你的会员等级:#rVIP"+cm.getChar().getVip()+"#k\r\n进入失败,此地图限制VIP1或者以上玩家进入.");
							cm.dispose();
						}
					}
					if (selection == 3) {
						if(cm.getChar().getVip() >= 3) {
						cm.warp(920010000, 0);
						cm.sendOk("#b你的会员等级:#rVIP"+cm.getChar().getVip()+"#k\r\n已传送到目的地.祝您旅途愉快.");
						cm.dispose();
						}else{
							cm.sendOk("#b你的会员等级:#rVIP"+cm.getChar().getVip()+"#k\r\n进入失败,此地图限制VIP3或者以上玩家进入.");
							cm.dispose();
						}
					}
					if (selection == 4) {
						if(cm.getChar().getVip() >= 4) {
						cm.warp(920010000, 0);
						cm.sendOk("#b你的会员等级:#rVIP"+cm.getChar().getVip()+"#k\r\n已传送到目的地.祝您旅途愉快.");
						cm.dispose();
						}else{
							cm.sendOk("#b你的会员等级:#rVIP"+cm.getChar().getVip()+"#k\r\n进入失败,此地图限制VIP4或者以上玩家进入.");
							cm.dispose();
						}
					}
                                        if (selection == 5) {
						if(cm.getChar().getVip() >= 5) {
						cm.warp(924010200, 0);
						cm.sendOk("#b你的会员等级:#rVIP"+cm.getChar().getVip()+"#k\r\n已传送到目的地.祝您旅途愉快.");
						cm.dispose();
						}else{
							cm.sendOk("#b你的会员等级:#rVIP"+cm.getChar().getVip()+"#k\r\n进入失败,此地图限制VIP5或者以上玩家进入.");
							cm.dispose();
						}
					}
                                        if (selection == 6) {
						if(cm.getChar().getVip() >= 6) {
						cm.warp(922020300, 0);
						cm.sendOk("#b你的会员等级:#rVIP"+cm.getChar().getVip()+"#k\r\n已传送到目的地.祝您旅途愉快.");
						cm.dispose();
						}else{
							cm.sendOk("#b你的会员等级:#rVIP"+cm.getChar().getVip()+"#k\r\n进入失败,此地图限制VIP6或者以上玩家进入.");
							cm.dispose();
						}
					}
                                        if (selection == 7) {
						if(cm.getChar().getVip() >= 7) {
						cm.warp(922020300, 0);
						cm.sendOk("#b你的会员等级:#rVIP"+cm.getChar().getVip()+"#k\r\n已传送到目的地.祝您旅途愉快.");
						cm.dispose();
						}else{
							cm.sendOk("#b你的会员等级:#rVIP"+cm.getChar().getVip()+"#k\r\n进入失败,此地图限制VIP7或者以上玩家进入.");
							cm.dispose();
						}
					}
				}
	}else if (status == 3) {
		if(typed==13){//QC
				if(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1)!=null){
					cm.sendOk("#r对不起，你的装备窗口第一格有装备.请移开后在进行购买,以防出错.");
					cm.dispose();
					return;
				}
				if(cm.getChar().getVip()>=5 && cm.getBossLog('vip5qc')<1){
						cm.gainItem(typesitemqced[selection],1);
						cm.gainItem(typesitemqceded[selection],1);
						cm.setBossLog('vip5qc');
						cm.sendOk("#e#r恭喜您，尊敬的终极会员.\r\n您已经成功的领取了骑宠.");
						cm.dispose();
					}else{
						cm.sendOk("#e#r领取失败，可能的原因是你不是VIP5以上或者是已经领取过了.");
						cm.dispose();
					}
		}
		if(typed==12){//YI ZI
				if(cm.getChar().getVip()>=4 && cm.getBossLog('vip4YZ')<1){
						cm.gainItem(typeszy[selection],1);
						cm.setBossLog('vip4YZ');
						cm.sendOk("#e#r恭喜您，尊敬的会员.\r\n您已经成功的领取了装备.");
						cm.dispose();
					}else{
						cm.sendOk("#e#r领取失败，可能的原因是你不是VIP4以上或者是已经领取过了.");
						cm.dispose();
					}
		}
		if(typed==11){//120
			if(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1)!=null){
					cm.sendOk("#r对不起，你的装备窗口第一格有装备.请移开后在进行购买,以防出错.");
					cm.dispose();
					return;
				}
				if(cm.getChar().getVip()>=4 && cm.getBossLog('vip4120')<1){
						cm.gainItem(typesitem[selection],1);
						cm.setBossLog('vip4120');
						cm.sendOk("#e#r恭喜您，尊敬的会员.\r\n您已经成功的领取了装备.");
						cm.dispose();
					}else{
						cm.sendOk("#e#r领取失败，可能的原因是你不是VIP5以上或者是已经领取过了.");
						cm.dispose();
					}
		}
		if(typed==10){//110 level
			if(cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1)!=null){
					cm.sendOk("#r对不起，你的装备窗口第一格有装备.请移开后在进行购买,以防出错.");
					cm.dispose();
					return;
				}
				if(cm.getChar().getVip()==3 && cm.getBossLog('vip3ed')<1){
						cm.gainItem(typesitemqc[selection],1);
						cm.setBossLog('vip3110');
						cm.sendOk("#e#r恭喜您，尊敬的会员.\r\n您已经成功的领取了装备.");
						cm.dispose();
					}else{
						cm.sendOk("#e#r领取失败，可能的原因是你不是VIP3以上或者是已经领取过了.");
						cm.dispose();
					}
		}
		cm.dispose();
			}			
	}
}
