/*
		射手村整形NPC 加入换眼晴颜色
*/
var status = 0;
var beauty = 0;
var price = 5000000;
var mhair = Array(33810,33960,36020,33620,33630,33660,33670,33680,33690,33970,33980,36000,33990,34720,34620,34610,34590,33630,33620,33610,33600,33720,33710,34760,34750,33750,34560,34670,34650,34640,33650,33640,33430,33410,33400,33550,31900,33520,33510,33500,33440,33450,34450,33470,34480,34470,34400,34380,33390,33380,33350,33340,34270,33250,34260,34250,34220,34330,34310,34210,34200,33220,31990,33154,33270,33180,34110,33120,34130,34040,33000,31760,31490,34710,34890,34882,34900,34910,34791,34800,34860);
var hairnew = Array();
var mface = Array(21038,21050,20040,20038,21034,21033,21045,21044,21043,21042,21041,20036,20035,20047,20046,20045,20044,20055,21031,21030,20032,21028,20231,20033,20030,21139);
var facenew = Array();
var skin = Array(1, 2, 3, 4, 9, 10);
var mmhair = Array(31000, 31010, 31020, 31030, 31040, 31050, 31060, 31070, 31080, 31090, 31100, 31110, 31120, 31130, 31140, 31150, 31160, 31170, 31180, 31190, 31200, 31210, 31220, 31230, 31240, 31250, 31260, 31270, 31280, 31290, 31300, 31310, 31320, 31330, 31340, 31350, 31410, 31420, 31430, 31440, 31450, 31460, 31470, 31480, 31490, 31510, 31520, 31530, 31540, 31550, 31560, 31610, 31620, 31630, 31640, 31650, 31670, 31680, 31690, 31700, 31710, 31720, 31730, 31740, 30760, 30940, 30990, 31200, 31750, 31760, 31770, 31790, 31800, 31810, 31820, 31830, 31850, 31860, 31870, 31880, 31890, 31910, 31920, 31930, 31940, 31950, 33030, 34000, 34010, 34040, 34050, 34060, 33230, 33250, 34120);


var mfhair = Array(30000, 30020, 30040, 30050, 30060, 30110, 30120, 30130, 30140, 30150, 30160, 30170, 30180, 30190, 30200, 30210, 30220, 30230, 30240, 30250, 30260, 30270, 30280, 30290, 30300, 30310, 30320, 30330, 30340, 30350, 30360, 30370, 30400, 30410, 30420, 30430, 30440, 30450, 30460, 30470, 30480, 30490, 30510, 30520, 30530, 30540, 30550, 30560, 30600, 30610, 30620, 30630, 30640, 30650, 30660, 30700, 30710, 30720, 30760, 31870, 30670, 30680, 30730, 30740, 30750, 30770, 30780, 30790, 30800, 30810, 30820, 30830, 30840, 30850, 30860, 30870, 30880, 30890, 30900, 30910, 30920, 30930, 30950, 31400, 31780, 31840, 33000, 33040, 33050, 33230, 33250, 34120);

var fhair = Array(34000, 34010, 34040, 34050, 34060, 31910, 31920, 31930, 31940, 31950, 31800, 31810, 31820, 31830, 31840, 31850, 31860, 31870, 31880, 31890, 31740, 31700, 31710, 31720, 31730, 31750, 31760, 31770, 31780, 31790, 31110, 31120, 31130, 31140, 31150, 31300, 31310, 31320, 31330, 31340, 31350, 31160, 31170, 31180, 31190, 31050, 31610, 31620, 31630, 31640, 31650, 31660, 31670, 31680, 31690, 31100, 31510, 31520, 31530, 31540, 31550, 31560, 31400, 31410, 31420, 31430, 31440, 31450, 31460, 31470, 31480, 31490, 31030, 31080, 31000, 31010, 31020, 31040, 31060, 31090, 31070, 31200, 31210, 31220, 31230, 31240, 31250, 31260, 31270, 31280, 31290);
var hairnew = Array();

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection){
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
			cm.sendSimple("#r         #v4000004##v4000004##v4000004##v4000004##v4000004##v4000004##v4000004##v4000004#\r\n          超级无敌美容处#k,#bV6独享独显尊贵\r\n#b      #L4##v1112683#·更换肤色·#l#L3#·更换发型·#v1112683##l\r\n      #L2##v1112675#·发型颜色·#l#L0#·眼睛整容·#v1112675##l\r\n             #L1##v1112663##r·眼晴换色·#v1112663##l\r\n\r\n     #b更改#r头发#b或#r眼睛#b之前请#g改成黑色#r掉线后果自负");
		} else if (status == 1) {
		if (selection == 0) {
				facenew = Array();
				if (cm.getChar().getGender() == 0) {
					for(var i = 0; i < mface.length; i++) {
						facenew.push(mface[i] + cm.getChar().getFace() % 1000 - (cm.getChar().getFace() % 100));
					}
				}
				if (cm.getChar().getGender() == 1) {
					for(var i = 0; i < fface.length; i++) {
						facenew.push(fface[i] + cm.getChar().getFace() % 1000 - (cm.getChar().getFace() % 100));
					}
				}
				cm.sendStyle("我可以改变你的脸型,让它比现在看起来漂亮. 你为什么不试着改变它下? 如果你是#b初级VIP1会员#k,我将会帮你改变你的脸型,那么选择一个你想要的新脸型吧!", facenew,5152001);
			}else if(selection == 1){
				beauty = 1;
				if (cm.getChar().getGender() == 0) {
					var current = cm.getChar().getFace() % 100 + 20000;
				}else{
					var current = cm.getChar().getFace() % 100 + 21000;
				}
				colors = Array();
				colors = Array(current, current + 100, current + 200, current + 300, current + 400, current + 500, current + 600, current + 700, current + 800);
				cm.sendStyle("请选择你喜欢的颜色.", colors,5152001);
                      } else if (selection == 2) {
				beauty = 2;
				haircolor = Array();
				var current = parseInt(cm.getChar().getHair()/10)*10;
				for(var i = 0; i < 8; i++) {
					haircolor.push(current + i);
				}
				cm.sendStyle("我可以改变你的发色,让它比现在看起来漂亮. 你为什么不试着改变它下? 如果你有#b初级VIP1会员#k,我将会帮你改变你的发色,那么选择一个你想要的新发色吧!", haircolor,5151001);
			
			} else if (selection == 3) {
                                beauty = 3;
				facenew = Array();
				hairnew = Array();
				if (cm.getChar().getGender() == 0) {
					for(var i = 0; i < mhair.length; i++) {
						hairnew.push(mhair[i] + parseInt(cm.getChar().getHair() % 10));
					}
				} 
				if (cm.getChar().getGender() == 1) {
					for(var i = 0; i < fhair.length; i++) {
						hairnew.push(fhair[i] + parseInt(cm.getChar().getHair() % 10));
					}
				}
				cm.sendStyle("我可以改变你的发型,让它比现在看起来漂亮.你为什么不试着改变它下? 如果你有#b初级VIP1会员#k,我将会帮你改变你的发型,那么选择一个你想要的新发型吧!", hairnew,5150001);
			} else if (selection == 4) {
                                beauty = 4;
				cm.sendStyle("用我们特殊开发的机器可查看护肤后的效果噢,想换成什么样的皮肤呢？请选择～~", skin, 5153000);
			} else if (selection == 5) {
				beauty = 5;
				hairnew = Array();
				if (cm.getChar().getGender() == 0) {
					for(var i = 0; i < mmhair.length; i++) {
						hairnew.push(mmhair[i] + parseInt(cm.getChar().getHair() % 10));
					}
				} 
				if (cm.getChar().getGender() == 1) {
					for(var i = 0; i < mfhair.length; i++) {
						hairnew.push(mfhair[i] + parseInt(cm.getChar().getHair() % 10));
					}
				}
				cm.sendStyle("我可以改变你的发型,让它比现在看起来漂亮。你为什么不试着改变它下? 如果你有#b初级VIP1会员#k,我将会帮你改变你的发型,那么选择一个你想要的新发型吧!", hairnew,5150001);			
    }
		}else if (status == 2){			
			cm.dispose();
			if(beauty == 0){
				  cm.setFace(facenew[selection]);
				  cm.sendOk("好了,你的朋友们一定认不出来是你了!");
cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『"+ cm.getChar().getName() +"』Vip⑥"+" : "+"使用还我漂漂拳,在至尊vip6地图(免费)选择了绝版皇家脸型"))
			  
      }else if(beauty ==1 ){
				  cm.setFace(colors[selection]);
				  cm.sendOk("好了,你的朋友们一定认不出来是你了!");
cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『"+ cm.getChar().getName() +"』Vip⑥"+" : "+"使用还我漂漂拳,在至尊vip6地图(免费)更改了个性眼睛颜色"))			    
				
      }else if(beauty ==2 ){
				
					cm.setHair(haircolor[selection]);
					cm.sendOk("好了,你的朋友们一定认不出来是你了!");
cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『"+ cm.getChar().getName() +"』Vip⑥"+" : "+"使用还我漂漂拳,在至尊vip6地图(免费)更改了个性发色"))
   			
      }else if(beauty ==3 ){
				
					cm.setHair(hairnew[selection]);
					cm.sendOk("好了,你的朋友们一定认不出来是你了!");
cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『"+ cm.getChar().getName() +"』Vip⑥"+" : "+"使用还我漂漂拳,在至尊vip6地图(免费)选择了绝版皇家发型"))
   			
      }else if(beauty ==4 ){
				
					cm.setSkin(skin[selection]);
					cm.sendOk("好了,你的朋友们一定认不出来是你了!");
cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『"+ cm.getChar().getName() +"』Vip⑥"+" : "+"使用还我漂漂拳,在至尊vip6地图(免费)更改个性皮肤"))
   			
     }else if(beauty ==5 ){
				
					cm.setHair(hairnew[selection]);
					cm.sendOk("好了,你的朋友们一定认不出来是你了!");
cm.getC().getChannelServer().broadcastPacket(net.sf.odinms.tools.MaplePacketCreator.serverNotice(0x10,cm.getC().getChannel(),"『"+ cm.getChar().getName() +"』Vip⑥"+" : "+"使用还我漂漂拳,在至尊vip6地图(免费)更改造型"))

			}
		}
	}
}
