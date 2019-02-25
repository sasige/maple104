var status = 0;
var slot = Array();
var stats = Array("力量", "敏捷", "智力", "运气", "HP", "MP", "物理攻击", "魔法攻击");
var rand = Math.floor(Math.random() * stats.length);
var selected;
var statsSel;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (status == 0 && mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
    if (status == 0) {
		if(cm.getChar().getVip() == 1){
		cm.sendOk("#rVIP3#k以上才能使用"); 
		cm.dispose();
		}else if(cm.getChar().getVip() == 2){
		cm.sendOk("#rVIP3#k以上才能使用"); 
		cm.dispose();
		}else if(cm.getChar().getVip() == 3){
		text = "#d尊贵的#b#h ##k,#d你目前会员等级为#rVIP3#k\r\n";
		text += "你每天可以提升装备次数为1次,你当前使用提升次数为：" + cm.getBossLog("会员提升装备") +"\r\n";
		text += "你每天可以给装备随机一项属性增加30\r\n";
		text += "#r提示：每个级别的VIP享受提升属性是不一样的哟#k\r\n";
		text += "#L0##d[VIP3]#k开始提升#l\r\n"; 
		cm.sendSimple(text);
		}else if(cm.getChar().getVip() == 4){
		text = "#d尊贵的#b#h ##k,#d你目前会员等级为#rVIP4#k\r\n";
		text += "你每天可以提升装备次数为1次,你当前使用提升次数为：" + cm.getBossLog("会员提升装备") +"\r\n";
		text += "你每天可以给装备随机一项属性增加50\r\n";
		text += "#r提示：每个级别的VIP享受提升属性是不一样的哟#k\r\n";
		text += "#L1##d[VIP4]#k开始提升#l\r\n" 
		cm.sendSimple(text);
		}else if(cm.getChar().getVip() == 5){
		text = "#d尊贵的#b#h ##k,#d你目前会员等级为#rVIP5#k\r\n";
		text += "你每天可以提升装备次数为1次,你当前使用提升次数为：" + cm.getBossLog("会员提升装备") +"\r\n";
		text += "你每天可以给装备随机一项属性增加100\r\n";
		text += "#r提示：每个级别的VIP享受提升属性是不一样的哟#k\r\n";
		text += "#L2##d[VIP5]#k开始提升#l\r\n" 
		cm.sendSimple(text);
		}else{
		cm.sendOk("你还不是会员");
		cm.dispose();
			}
    } else if (status == 1) {
        if (selection == 0) {
	    typed=1;
            var avail = ""
            for (var i = -1; i > -199; i--) {
                if (cm.getInventory( - 1).getItem(i) != null) {
                    avail += "#L" + Math.abs(i) + "##z" + cm.getInventory( - 1).getItem(i).getItemId() + "##l\r\n";
                }
                slot.push(i);
            }
            cm.sendSimple("请选择您需要提升属性的装备：(身上穿戴的装备)\r\n#b" + avail);
        } else if (selection == 1) {
	    typed=2;
            var avail = ""
            for (var i = -1; i > -199; i--) {
                if (cm.getInventory( - 1).getItem(i) != null) {
                    avail += "#L" + Math.abs(i) + "##z" + cm.getInventory( - 1).getItem(i).getItemId() + "##l\r\n";
                }
                slot.push(i);
            }
            cm.sendSimple("请选择您需要提升属性的装备：(身上穿戴的装备)\r\n#b" + avail);
        } else if (selection == 2) {
	    typed=3;
            var avail = ""
            for (var i = -1; i > -199; i--) {
                if (cm.getInventory( - 1).getItem(i) != null) {
                    avail += "#L" + Math.abs(i) + "##z" + cm.getInventory( - 1).getItem(i).getItemId() + "##l\r\n";
                }
                slot.push(i);
            }
            cm.sendSimple("请选择您需要提升属性的装备：(身上穿戴的装备)\r\n#b" + avail);
				}
    } else if (status == 2) {
        if (typed==1) {
	typed=4;
	var ii = Packages.server.MapleItemInformationProvider.getInstance();
        selected = selection - 1;
	if(ii.isCash(cm.getInventory( - 1).getItem(slot[selected]).getItemId())==true){
	cm.sendOk("对不起，选择的装备不能为商场物品：#v" + cm.getInventory( - 1).getItem(slot[selected]).getItemId() + "##z" + cm.getInventory( - 1).getItem(slot[selected]).getItemId() + "#"); 
	cm.dispose();
        } else {
        cm.sendNext("随机提升30属性吗？属性包括力量，敏捷，运气，智力，攻击，魔力，HP上限，MP上限，至于得不得到你满意了，试了就知道了！每天就只有这么一次哦！\r\n#r你选择了提升#b#z" + cm.getInventory( - 1).getItem(slot[selected]).getItemId() + "");
    }
        } else if (typed==2) {
	    typed=5;
	var ii = Packages.server.MapleItemInformationProvider.getInstance();
        selected = selection - 1;
	if(ii.isCash(cm.getInventory( - 1).getItem(slot[selected]).getItemId())==true){
	cm.sendOk("对不起，选择的装备不能为商场物品：#v" + cm.getInventory( - 1).getItem(slot[selected]).getItemId() + "##z" + cm.getInventory( - 1).getItem(slot[selected]).getItemId() + "#"); 
	cm.dispose();
        } else {
        cm.sendNext("随机提升50属性吗？属性包括力量，敏捷，运气，智力，攻击，魔力，HP上限，MP上限，至于得不得到你满意了，试了就知道了！每天就只有这么一次哦！\r\n#r你选择了提升#b#z" + cm.getInventory( - 1).getItem(slot[selected]).getItemId() + "");
    }
        } else if (typed==3) {
	    typed=6;
	var ii = Packages.server.MapleItemInformationProvider.getInstance();
        selected = selection - 1;
	if(ii.isCash(cm.getInventory( - 1).getItem(slot[selected]).getItemId())==true){
	cm.sendOk("对不起，选择的装备不能为商场物品：#v" + cm.getInventory( - 1).getItem(slot[selected]).getItemId() + "##z" + cm.getInventory( - 1).getItem(slot[selected]).getItemId() + "#");
	cm.dispose();
        } else {
        cm.sendNext("随机提升100属性吗？属性包括力量，敏捷，运气，智力，攻击，魔力，HP上限，MP上限，至于得不得到你满意了，试了就知道了！每天就只有这么一次哦！\r\n#r你选择了提升#b#z" + cm.getInventory( - 1).getItem(slot[selected]).getItemId() + "");
    }
				}
    } else if (status == 3) {
        if (typed==4) {
			if(cm.getBossLog("会员提升装备") > 0){
				cm.sendOk("对不起,你今天已经使用了1次此功能!");
				cm.dispose();
			}else{
				var item = cm.getInventory(-1).getItem(slot[selected]);
				var chance = Math.floor(Math.random()*100);
				cm.setBossLog("会员提升装备");
				var index;
				if(chance<=5){
					index=6;
					cm.changeStat(slot[selected], index, item.getWatk()+30);
				}else if(chance>5 && chance<=15){
					index=7;
					cm.changeStat(slot[selected], index, item.getMatk()+30);
				}else if(chance>15 && chance<=30){
					index=0;
					cm.changeStat(slot[selected], index, item.getStr()+30);
				}else if(chance>30 && chance<=45){
					index=1;
					cm.changeStat(slot[selected], index, item.getDex()+30);
				}else if(chance>45 && chance<=60){
					index=3;
					cm.changeStat(slot[selected], index, item.getLuk()+30);
				}else if(chance>60 && chance<=75){
					index=2;
					cm.changeStat(slot[selected], index, item.getInt()+30);
				}else if(chance>75 && chance<=90){
					index=4;
					cm.changeStat(slot[selected], index, item.getHp()+30);
				}else if(chance>90){
					index=5;
					cm.changeStat(slot[selected], index, item.getMp()+30);	
				}
				cm.sendOk("恭喜，成功给装备增加了:#r30点#k"+stats[index]);
				cm.worldMessage("[装备升级]：["+cm.getChar().getName() + "]VIP3玩家使用特权给他武器提升了30属性");
				cm.dispose();
			}
        } else if (typed==5) {
			if(cm.getBossLog("会员提升装备") > 0){
				cm.sendOk("对不起,你今天已经使用了1次此功能!");
				cm.dispose();
			}else{
				var item = cm.getInventory(-1).getItem(slot[selected]);
				var chance = Math.floor(Math.random()*100);
				cm.setBossLog("会员提升装备");
				var index;
				if(chance<=5){
					index=6;
					cm.changeStat(slot[selected], index, item.getWatk()+50);
				}else if(chance>5 && chance<=15){
					index=7;
					cm.changeStat(slot[selected], index, item.getMatk()+50);
				}else if(chance>15 && chance<=30){
					index=0;
					cm.changeStat(slot[selected], index, item.getStr()+50);
				}else if(chance>30 && chance<=45){
					index=1;
					cm.changeStat(slot[selected], index, item.getDex()+50);
				}else if(chance>45 && chance<=60){
					index=3;
					cm.changeStat(slot[selected], index, item.getLuk()+50);
				}else if(chance>60 && chance<=75){
					index=2;
					cm.changeStat(slot[selected], index, item.getInt()+50);
				}else if(chance>75 && chance<=90){
					index=4;
					cm.changeStat(slot[selected], index, item.getHp()+50);
				}else if(chance>90){
					index=5;
					cm.changeStat(slot[selected], index, item.getMp()+50);	
				}
				cm.sendOk("恭喜，成功给装备增加了:#r50点#k"+stats[index]);
				cm.worldMessage("[装备升级]：[" + cm.getChar().getName() + "]VIP4玩家使用特权给他武器提升了50属性");
				cm.dispose();
			}
        } else if (typed==6) {
			if(cm.getBossLog("会员提升装备") > 0){//判定今天是否提升过
				cm.sendOk("对不起,你今天已经使用了1次此功能!");//提示过就进行下一项
				cm.dispose();
			}else{
				var item = cm.getInventory(-1).getItem(slot[selected]);//获取他上一项选取的装备
				var chance = Math.floor(Math.random()*100);//100%几率
				cm.setBossLog("会员提升装备");//写入数据库
				var index;
				if(chance<=5){
					index=6;
					cm.changeStat(slot[selected], index, item.getWatk()+100);
				}else if(chance>5 && chance<=15){
					index=7;
					cm.changeStat(slot[selected], index, item.getMatk()+100);
				}else if(chance>15 && chance<=30){
					index=0;
					cm.changeStat(slot[selected], index, item.getStr()+100);
				}else if(chance>30 && chance<=45){
					index=1;
					cm.changeStat(slot[selected], index, item.getDex()+100);
				}else if(chance>45 && chance<=60){
					index=3;
					cm.changeStat(slot[selected], index, item.getLuk()+100);
				}else if(chance>60 && chance<=75){
					index=2;
					cm.changeStat(slot[selected], index, item.getInt()+100);
				}else if(chance>75 && chance<=90){
					index=4;
					cm.changeStat(slot[selected], index, item.getHp()+100);
				}else if(chance>90){
					index=5;
					cm.changeStat(slot[selected], index, item.getMp()+100);	
				}
				cm.sendOk("恭喜，成功给装备增加了:#r100点#k"+stats[index]);
				cm.worldMessage("[装备升级]：[" + cm.getChar().getName() + "]VIP5玩家使用特权给他武器提升了100属性");
				cm.dispose();
			}
		}
	}
}
