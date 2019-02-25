/* 
	NPC Name: 		Divine Bird
	Map(s): 		Erev
	Description: 		Buff
*/

function start() {
    //cm.useItem(2022458); //神兽的祝福 - 1小时内物理攻击力增加5，魔力增加10，物理防御力增加100，魔法防御力增加100，移动速度增加10。
    //cm.sendOk("Don't stop training. Every ounce of your energy is required to protect the world of Maple....");
    cm.dispose();
}

importPackage(net.sf.odinms.client);

var status = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == 0) {
		cm.dispose();
	} else {
		if (mode == 1)
			status++;
		else
			status--;

		if (status == 0) {
			cm.sendAcceptDecline("为了冒险岛世界……你做好成为女皇的骑士的准备了吗？如果你已经准备好了，我会让你成为冒险骑士团的骑士。");
		}
		if (status == 1) {
			cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addNewCharQst());
			cm.dispose();
		}
	}
}