function start() {
    if (!cm.haveItem(4000660,10)){
        cm.sendOk("#r[提示]#k：合成#v4032923##b#z4032923#需要10个#v4000660##b#z4000660#请收集！");
        cm.dispose();
    }else if (!cm.haveItem(4000661,10)){
        cm.sendOk("#r[提示]#k：合成#v4032923##b#z4032923#需要10个#v4000661##b#z4000661#请收集！");
        cm.dispose();
       }else if (!cm.haveItem(4000662,10)){
        cm.sendOk("#r[提示]#k：合成#v4032923##b#z4032923#需要10个#v4000662##b#z4000662#请收集！");
        cm.dispose();
        }else if (!cm.haveItem(4000663,10)){
        cm.sendOk("#r[提示]#k：合成#v4032923##b#z4032923#需要10个#v4000663##b#z4000663#请收集！");
        cm.dispose();
        }else if (!cm.haveItem(2430200,1)){
        cm.sendOk("#r[提示]#k：合成#v4032923##b#z4032923#需要1个#v2430200##b#z2430200#请收集！");
        cm.dispose();
		}else if (cm.haveItem(4032923)){
        cm.sendOk("#r[提示]#k：你已经有钥匙，无法在合成。");
        cm.dispose();
    }else{
		cm.gainItem(4000660,-10);
		cm.gainItem(4000661,-10);
		cm.gainItem(4000662,-10);
		cm.gainItem(4000663,-10);
		cm.gainItem(2430200,-1);
        cm.gainItem(4032923,1);
        cm.sendOk("#r[提示]#k：恭喜你获得#r#v4032923##z4032923##k钥匙。");
        cm.dispose();
    }
}