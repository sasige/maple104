importPackage(net.sf.cherry.client);

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
                
			cm.sendOk("#e好的,如果你想好了要做什么,我会很乐意为你服务的..");
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
cm.sendSimple("#e#b如果你有一枚7周年纪念#v4310002#,你可以用一枚7周年纪念币,和我换取以下的任何一枚GM卷,砸卷成功率100%\r\n#r潜能和强化卷轴不是100%成功#k\r\n\r\n\r\n\r\n#L30##v2043404#刀攻击卷(非必成)\r\n#L29##v2049401#潜能附加卷轴\r\n#L28##v2049301#装备强化卷轴\r\n#L1##v2040006#头盔防御诅咒卷轴\r\n#L2##v2040303#耳环智力诅咒卷轴\r\n#L3##v2040403#上衣防御诅咒卷轴\r\n#L4##v2040506#全身铠甲敏捷诅咒卷轴\r\n#L5##v2040507#全身铠甲防御诅咒卷轴\r\n#L6##v2040603#裤/裙防御诅咒卷轴\r\n#L7##v2040709#鞋子敏捷诅咒卷轴\r\n#L8##v2040710#鞋子跳跃诅咒卷轴\r\n#L9##v2040806#手套敏捷诅咒卷轴\r\n#L10##v2040903#盾牌防御诅咒卷轴\r\n#L11##v2041024#披风魔防诅咒卷轴\r\n#L12##v2041025#披风防御诅咒卷轴\r\n#L13##v2041200#暗黑龙王石\r\n#L14##v2043003#单手剑攻击诅咒卷轴\r\n#L15##v2043103#单手斧攻击诅咒卷轴\r\n#L16##v2043203#单手钝器攻击诅咒卷轴\r\n#L17##v2043303#短剑攻击诅咒卷轴\r\n#L18##v2043703#短杖魔力诅咒卷轴\r\n#L19##v2043803#长杖魔力诅咒卷轴\r\n#L20##v2044003#双手剑攻击诅咒卷轴\r\n#L21##v2044103#双手斧攻击诅咒卷轴\r\n#L22##v2044203#双手钝器攻击诅咒卷轴\r\n#L23##v2044303#枪攻击诅咒卷轴\r\n#L24##v2044403#矛攻击诅咒卷轴\r\n#L25##v2044503#弓攻击诅咒卷轴\r\n#L26##v2044603#弩攻击诅咒卷轴\r\n#L27##v2044703#拳套攻击诅咒卷轴");
}else if (status == 1) {
  if(cm.getPlayer().getInventory(net.sf.cherry.client.MapleInventoryType.getByType(2)).isFull()){
							cm.sendOk("请保证消耗栏位有1个空格接受兑换物品.");
							cm.dispose();
}else   
if (selection == 1) {
if (cm.haveItem(4310002) == false){
cm.sendOk("抱歉,您没有带来大吉,所以我不能和你交换"); cm.dispose();
}else{
cm.gainItem(4310002,-1);
cm.gainItem(2040006,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 2) {
if (cm.haveItem(4310002) == false){
cm.sendOk("抱歉,您没有带来大吉,所以我不能和你交换"); cm.dispose();
}else{
cm.gainItem(4310002,-1);
cm.gainItem(2040303,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 3) {
if (cm.haveItem(4310002) == false){
cm.sendOk("抱歉,您没有带来大吉,所以我不能和你交换"); cm.dispose();
}else{
cm.gainItem(4310002,-1);
cm.gainItem(2040403,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 4) {
if (cm.haveItem(4310002) == false){
cm.sendOk("抱歉,您没有带来大吉,所以我不能和你交换"); cm.dispose();
}else{
cm.gainItem(4310002,-1);
cm.gainItem(2040506,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 5) {
if (cm.haveItem(4310002) == false){
cm.sendOk("抱歉,您没有带来大吉,所以我不能和你交换"); cm.dispose();
}else{
cm.gainItem(4310002,-1);
cm.gainItem(2040507,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 6) {
if (cm.haveItem(4310002) == false){
cm.sendOk("抱歉,您没有带来大吉,所以我不能和你交换"); cm.dispose();
}else{
cm.gainItem(4310002,-1);
cm.gainItem(2040603,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 7) {
if (cm.haveItem(4310002) == false){
cm.sendOk("抱歉,您没有带来大吉,所以我不能和你交换"); cm.dispose();
}else{
cm.gainItem(4310002,-1);
cm.gainItem(2040709,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 8) {
if (cm.haveItem(4310002) == false){
cm.sendOk("抱歉,您没有带来大吉,所以我不能和你交换"); cm.dispose();
}else{
cm.gainItem(4310002,-1);
cm.gainItem(2040710,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 9) {
if (cm.haveItem(4310002) == false){
cm.sendOk("抱歉,您没有带来大吉,所以我不能和你交换"); cm.dispose();
}else{
cm.gainItem(4310002,-1);
cm.gainItem(2040806,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 10) {
if (cm.haveItem(4310002) == false){
cm.sendOk("抱歉,您没有带来大吉,所以我不能和你交换"); cm.dispose();
}else{
cm.gainItem(4310002,-1);
cm.gainItem(2040903,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 11) {
if (cm.haveItem(4310002) == false){
cm.sendOk("抱歉,您没有带来大吉,所以我不能和你交换"); cm.dispose();
}else{
cm.gainItem(4310002,-1);
cm.gainItem(2041024,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 12) {
if (cm.haveItem(4310002) == false){
cm.sendOk("抱歉,您没有带来大吉,所以我不能和你交换"); cm.dispose();
}else{
cm.gainItem(4310002,-1);
cm.gainItem(2041025,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 13) {
if (cm.haveItem(4310002) == false){
cm.sendOk("抱歉,您没有带来大吉,所以我不能和你交换"); cm.dispose();
}else{
cm.gainItem(4310002,-1);
cm.gainItem(2041200,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 14) {
if (cm.haveItem(4310002) == false){
cm.sendOk("抱歉,您没有带来大吉,所以我不能和你交换"); cm.dispose();
}else{
cm.gainItem(4310002,-1);
cm.gainItem(2043003,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 15) {
if (cm.haveItem(4310002) == false){
cm.sendOk("抱歉,您没有带来大吉,所以我不能和你交换"); cm.dispose();
}else{
cm.gainItem(4310002,-1);
cm.gainItem(2043103,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 16) {
if (cm.haveItem(4310002) == false){
cm.sendOk("抱歉,您没有带来大吉,所以我不能和你交换"); cm.dispose();
}else{
cm.gainItem(4310002,-1);
cm.gainItem(2043203,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 17) {
if (cm.haveItem(4310002) == false){
cm.sendOk("抱歉,您没有带来大吉,所以我不能和你交换"); cm.dispose();
}else{
cm.gainItem(4310002,-1);
cm.gainItem(2043303,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 18) {
if (cm.haveItem(4310002) == false){
cm.sendOk("抱歉,您没有带来大吉,所以我不能和你交换"); cm.dispose();
}else{
cm.gainItem(4310002,-1);
cm.gainItem(2043703,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 19) {
if (cm.haveItem(4310002) == false){
cm.sendOk("抱歉,您没有带来大吉,所以我不能和你交换"); cm.dispose();
}else{
cm.gainItem(4310002,-1);
cm.gainItem(2043803,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 20) {
if (cm.haveItem(4310002) == false){
cm.sendOk("抱歉,您没有带来大吉,所以我不能和你交换"); cm.dispose();
}else{
cm.gainItem(4310002,-1);
cm.gainItem(2044003,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 21) {
if (cm.haveItem(4310002) == false){
cm.sendOk("抱歉,您没有带来大吉,所以我不能和你交换"); cm.dispose();
}else{
cm.gainItem(4310002,-1);
cm.gainItem(2044103,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 22) {
if (cm.haveItem(4310002) == false){
cm.sendOk("抱歉,您没有带来大吉,所以我不能和你交换"); cm.dispose();
}else{
cm.gainItem(4310002,-1);
cm.gainItem(2044203,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 23) {
if (cm.haveItem(4310002) == false){
cm.sendOk("抱歉,您没有带来大吉,所以我不能和你交换"); cm.dispose();
}else{
cm.gainItem(4310002,-1);
cm.gainItem(2044303,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 24) {
if (cm.haveItem(4310002) == false){
cm.sendOk("抱歉,您没有带来大吉,所以我不能和你交换"); cm.dispose();
}else{
cm.gainItem(4310002,-1);
cm.gainItem(2044403,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 25) {
if (cm.haveItem(4310002) == false){
cm.sendOk("抱歉,您没有带来大吉,所以我不能和你交换"); cm.dispose();
}else{
cm.gainItem(4310002,-1);
cm.gainItem(2044503,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 26) {
if (cm.haveItem(4310002) == false){
cm.sendOk("抱歉,您没有带来大吉,所以我不能和你交换"); cm.dispose();
}else{
cm.gainItem(4310002,-1);
cm.gainItem(2044603,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 27) {
if (cm.haveItem(4310002) == false){
cm.sendOk("抱歉,您没有带来大吉,所以我不能和你交换"); cm.dispose();
}else{
cm.gainItem(4310002,-1);
cm.gainItem(2044703,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 28) {
if (cm.haveItem(4310002) == false){
cm.sendOk("抱歉,您没有带来大吉,所以我不能和你交换"); cm.dispose();
}else{
cm.gainItem(4310002,-1);
cm.gainItem(2049301,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 29) {
if (cm.haveItem(4310002) == false){
cm.sendOk("抱歉,您没有带来大吉,所以我不能和你交换"); cm.dispose();
}else{
cm.gainItem(4310002,-1);
cm.gainItem(2049401,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 30) {
if (cm.haveItem(4310002) == false){
cm.sendOk("抱歉,您没有带来大吉,所以我不能和你交换"); cm.dispose();
}else{
cm.gainItem(4310002,-1);
cm.gainItem(2043404,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}
}
}
}