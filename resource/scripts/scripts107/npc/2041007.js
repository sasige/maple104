var status = 0;
var beauty = 0;
var hairprice = 1000000;
var haircolorprice = 1000000;
var mhair = Array(30020,30030,30150,30240,30280,30160,30190,30050,30300,30320,30340,30510,30650,30730,30760,30680,30700,30000);
var fhair = Array(31000,31030,31040,31010,31150,31120,31160,31230,31270,31280,31290,31460,31520,31650,31720,31740,31700,31670);
var hairnew = Array();

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
			cm.sendSimple("你好,我是玩具城美发店的店长#p2041007#!如果你有#b玩具城美发店高级会员卡#k或#b玩具城染色高级会员卡#k,你就放心的把发型交给我,我会让你满意的.那么你要做什么?请选择吧!.\r\n#L1#改变发型(使用#b玩具城美发高级会员卡#k)#l\r\n#L2#改变发色(使用#b玩具城染色高级会员卡#k)#l");						
		} else if (status == 1) {
			 if (selection == 1) {
				beauty = 1;
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
				cm.sendStyle("我的手艺是拜师的来。如果你有#b玩具城美发店高级会员卡#k就可以让你换一个发型,请选择喜欢的发型吧.", hairnew,5150035);
			} else if (selection == 2) {
				beauty = 2;
				haircolor = Array();
				var current = parseInt(cm.getChar().getHair()/10)*10;
				for(var i = 0; i < 8; i++) {
					haircolor.push(current + i);
				}
				cm.sendStyle("我可以改变你的发色,让它比现在看起来漂亮. 你为什么不试着改变它下? 如果你有#b玩具城染色高级会员卡#k,我将会帮你改变你的发色,那么选择一个你想要的新发色吧!", haircolor,5151030);			}
		} else if (status == 2){
			if (beauty == 1){
					if (cm.haveItem(5150007)){
						cm.gainItem(5150007, -1);
						cm.setHair(hairnew[selection]);
						cm.sendOk("好了,让朋友们赞叹你的新发型吧!");
					} else {
						cm.sendOk("看起来你并没有我们的高级会员卡,我恐怕不能给你染发,我很抱歉.请你先购买吧.");
					}
					cm.dispose();	
				
			}
			if (beauty == 2){
				if (cm.haveItem(5151007) == true){
					cm.gainItem(5151007, -1);
					cm.setHair(haircolor[selection]);
					cm.sendOk("好了,让朋友们赞叹你的新发色吧!");
					cm.dispose();
				} else {
					cm.sendOk("看起来你并没有我们的高级会员卡,我恐怕不能给你染发,我很抱歉.请你先购买吧.");
					cm.dispose();
				}
				
			}
		}
	}
}
