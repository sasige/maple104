//皇家理发券
var status = -1;
var beauty = 0;
var hair_Colo_new;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 0 && status == 0) {
	cm.dispose();
	return;
    }
    if (mode == 1)
	status++;
    else
	status--;
    if (status == 0) {
	cm.sendYesNo("我是#p"+cm.getNpc()+"#。如果你拥有 #b#t5150040##k  ，就可以随机抽取一次限量版的皇家发型哦，你想使用吗？");
    } else if (status == 1) {
	    var hair = cm.getPlayerStat("HAIR");
	    hair_Colo_new = [];
	    beauty = 1;

	    if (cm.getPlayerStat("GENDER") == 0) {
		hair_Colo_new = [36000,33990,33100,33120,33160,33220,33150,33110,33200,33210,33040,33000,33050,30730,30920,30830,30780,30800,33370,33390,33350,32120,33250,33260,33280,33290,33300,33310,33320,31380,33380,33470,33480,33440,33520];
	    } else {
		hair_Colo_new = [34890,34882,34900,34910,34210,34170,34120,34110,34150,34160,34180,34220,34200,31990,34140,34100,34010,34000,31760,34040,34050,34060,31890,31950,31940,31930,31920,31910,31890,31880,31870,31860,31850,31820,31800,31900,34360,31360,32130,34200,34340,34260,34230,34240,30380,31380,33380,34350,34430,32160,32150,34490,31240];
	    }
	    for (var i = 0; i < hair_Colo_new.length; i++) {
		hair_Colo_new[i] = hair_Colo_new[i] + (hair % 10);
	    }
		
		
		 if (cm.setRandomAvatar(5150040, hair_Colo_new) == 1) {
		cm.sendOk("好了,让朋友们赞叹你的新发型吧!");
	    } else {
		cm.sendOk("看起来你并没有我们的会员卡,我恐怕不能给你理发,我很抱歉.请你先购买吧.");
	    }
           cm.dispose();
    }
}
