importPackage(net.sf.odinms.client);

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
cm.sendSimple("你需要给我一个#v4001374##k\r\n#L28#祝福卷轴#b[1个2张]#k#r[Hot]#k\r\n#L2##b[耳环智力GM卷轴]#L4#[全身铠甲敏捷GM卷轴]\r\n#L7#[鞋子敏捷GM卷轴]#L8#[鞋子跳跃GM卷轴]\r\n#L9#[手套敏捷GM卷轴]#L11#[披风魔防GM卷轴]\r\n#L13#[暗黑龙王石]#L14#[单手剑攻击GM卷轴]\r\n#L15#[单手斧攻击GM卷轴]#L16#[单手钝器攻击GM卷轴]\r\n#L17#[短剑攻击GM卷轴]n#L18#[短杖魔力GM卷轴]\r\n#L19#[长杖魔力GM卷轴]#L20#[双手剑攻击GM卷轴]\r\n#L21#[双手斧攻击GM卷轴]#L22#[双手钝器攻击GM卷轴]\r\n#L23#[枪攻击GM卷轴]#L24#[矛攻击GM卷轴]\r\n#L25#[弓攻击GM卷轴]#L26#[弩攻击GM卷轴]\r\n#L27#[拳套攻击GM卷轴]#L29#[手套攻击GM卷轴]\r\n#L30#[短枪攻击GM卷轴]");
}else if (status == 1) {
if (selection == 1) {
if (cm.haveItem(4001374) == false){
cm.sendOk("抱歉你没有我要的东西,不能兑换"); cm.dispose();
}else{
	cm.gainItem(4001374,-1);
cm.gainItem(2040006,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 2) {
if (cm.haveItem(4001374) == false){
cm.sendOk("抱歉你没有我要的东西,不能兑换"); cm.dispose();
}else{
	cm.gainItem(4001374,-1);
cm.gainItem(2040303,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 3) {
if (cm.haveItem(4001374) == false){
cm.sendOk("抱歉你没有我要的东西,不能兑换"); cm.dispose();
}else{
	cm.gainItem(4001374,-1);
cm.gainItem(2040403,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 4) {
if (cm.haveItem(4001374) == false){
cm.sendOk("抱歉你没有我要的东西,不能兑换"); cm.dispose();
}else{
	cm.gainItem(4001374,-1);
cm.gainItem(2040506,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 5) {
if (cm.haveItem(4001374) == false){
cm.sendOk("抱歉你没有我要的东西,不能兑换"); cm.dispose();
}else{
	cm.gainItem(4001374,-1);
cm.gainItem(2040507,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 6) {
if (cm.haveItem(4001374) == false){
cm.sendOk("抱歉你没有我要的东西,不能兑换"); cm.dispose();
}else{
	cm.gainItem(4001374,-1);
cm.gainItem(2040603,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 7) {
if (cm.haveItem(4001374) == false){
cm.sendOk("抱歉你没有我要的东西,不能兑换"); cm.dispose();
}else{
	cm.gainItem(4001374,-1);
cm.gainItem(2040709,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 8) {
if (cm.haveItem(4001374) == false){
cm.sendOk("抱歉你没有我要的东西,不能兑换"); cm.dispose();
}else{
	cm.gainItem(4001374,-1);
cm.gainItem(2040710,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 9) {
if (cm.haveItem(4001374) == false){
cm.sendOk("抱歉你没有我要的东西,不能兑换"); cm.dispose();
}else{
	cm.gainItem(4001374,-1);
cm.gainItem(2040806,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 10) {
if (cm.haveItem(4001374) == false){
cm.sendOk("抱歉你没有我要的东西,不能兑换"); cm.dispose();
}else{
	cm.gainItem(4001374,-1);
cm.gainItem(2040903,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 11) {
if (cm.haveItem(4001374) == false){
cm.sendOk("抱歉你没有我要的东西,不能兑换"); cm.dispose();
}else{
	cm.gainItem(4001374,-1);
cm.gainItem(2041024,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 12) {
if (cm.haveItem(4001374) == false){
cm.sendOk("抱歉你没有我要的东西,不能兑换"); cm.dispose();
}else{
	cm.gainItem(4001374,-1);
cm.gainItem(2041025,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 13) {
if (cm.haveItem(4001374) == false){
cm.sendOk("抱歉你没有我要的东西,不能兑换"); cm.dispose();
}else{
	cm.gainItem(4001374,-1);
cm.gainItem(2041200,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 14) {
if (cm.haveItem(4001374) == false){
cm.sendOk("抱歉你没有我要的东西,不能兑换"); cm.dispose();
}else{
	cm.gainItem(4001374,-1);
cm.gainItem(2043003,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 15) {
if (cm.haveItem(4001374) == false){
cm.sendOk("抱歉你没有我要的东西,不能兑换"); cm.dispose();
}else{
	cm.gainItem(4001374,-1);
cm.gainItem(2043103,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 16) {
if (cm.haveItem(4001374) == false){
cm.sendOk("抱歉你没有我要的东西,不能兑换"); cm.dispose();
}else{
	cm.gainItem(4001374,-1);
cm.gainItem(2043203,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 17) {
if (cm.haveItem(4001374) == false){
cm.sendOk("抱歉你没有我要的东西,不能兑换"); cm.dispose();
}else{
	cm.gainItem(4001374,-1);
cm.gainItem(2043303,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 18) {
if (cm.haveItem(4001374) == false){
cm.sendOk("抱歉你没有我要的东西,不能兑换"); cm.dispose();
}else{
	cm.gainItem(4001374,-1);
cm.gainItem(2043703,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 19) {
if (cm.haveItem(4001374) == false){
cm.sendOk("抱歉你没有我要的东西,不能兑换"); cm.dispose();
}else{
	cm.gainItem(4001374,-1);
cm.gainItem(2043803,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 20) {
if (cm.haveItem(4001374) == false){
cm.sendOk("抱歉你没有我要的东西,不能兑换"); cm.dispose();
}else{
	cm.gainItem(4001374,-1);
cm.gainItem(2044003,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 21) {
if (cm.haveItem(4001374) == false){
cm.sendOk("抱歉你没有我要的东西,不能兑换"); cm.dispose();
}else{
	cm.gainItem(4001374,-1);
cm.gainItem(2044103,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 22) {
if (cm.haveItem(4001374) == false){
cm.sendOk("抱歉你没有我要的东西,不能兑换"); cm.dispose();
}else{
	cm.gainItem(4001374,-1);
cm.gainItem(2044203,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 23) {
if (cm.haveItem(4001374) == false){
cm.sendOk("抱歉你没有我要的东西,不能兑换"); cm.dispose();
}else{
	cm.gainItem(4001374,-1);
cm.gainItem(2044303,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 24) {
if (cm.haveItem(4001374) == false){
cm.sendOk("抱歉你没有我要的东西,不能兑换"); cm.dispose();
}else{
	cm.gainItem(4001374,-1);
cm.gainItem(2044403,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 25) {
if (cm.haveItem(4001374) == false){
cm.sendOk("抱歉你没有我要的东西,不能兑换"); cm.dispose();
}else{
	cm.gainItem(4001374,-1);
cm.gainItem(2044503,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 26) {
if (cm.haveItem(4001374) == false){
cm.sendOk("抱歉你没有我要的东西,不能兑换"); cm.dispose();
}else{
	cm.gainItem(4001374,-1);
cm.gainItem(2044603,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 27) {
if (cm.haveItem(4001374) == false){
cm.sendOk("抱歉你没有我要的东西,不能兑换"); cm.dispose();
}else{
	cm.gainItem(4001374,-1);
cm.gainItem(2044703,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 28) {
if (cm.haveItem(4001374) == false){
cm.sendOk("抱歉你没有我要的东西,不能兑换"); cm.dispose();
}else{
	cm.gainItem(4001374,-1);
cm.gainItem(2340000,2);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 29) {
if (cm.haveItem(4001374) == false){
cm.sendOk("抱歉你没有我要的东西,不能兑换"); cm.dispose();
}else{
	cm.gainItem(4001374,-1);
cm.gainItem(2040807,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}else if (selection == 30) {
if (cm.haveItem(4001374) == false){
cm.sendOk("抱歉你没有我要的东西,不能兑换"); cm.dispose();
}else{
	cm.gainItem(4001374,-1);
cm.gainItem(2044908,1);
cm.sendOk("哈哈,换取成功,快去砸极品装备吧");
cm.dispose(); }
}
}
}
}