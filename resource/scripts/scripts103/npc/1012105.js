/* Ms. Tan 
	Henesys Skin Change.
*/
var status = 0;
var skin = Array(0, 1, 2, 3, 4);

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 0 && status == 0) {
	cm.dispose();
	return;
    } else {
	status++;
    }

    if (status == 0) {
	cm.sendNext("哇，你好！欢迎来到#m"+cm.getMapId()+"#! 如果你拥有#b#t5153015##k，我就可以帮你换一个新肤色哦~");
    } else if (status == 1) {
	cm.sendStyle("用我们特殊开发的机器可查看护肤后的效果噢，想换成什么样的皮肤呢？请选择～", skin,5153015);
    } else if (status == 2){
	if (cm.setAvatar(5153015, skin[selection]) == 1) {
	    cm.sendOk("完成了,让朋友们赞叹你的新肤色吧!");
	} else {
	    cm.sendOk("看起来你并没有我们的会员卡,我恐怕不能给你护肤,我很抱歉.请你先购买吧.");
	}
	cm.dispose();
    }
}
