var a = -1;

function start(mode, type, selection) {//接任务
	if (mode == -1) {
		qm.dispose();
	} else {
		if (mode == 1)
			a++;
		else
			a--;
			if(a == -1){
				qm.dispose();
			}else if (a == 0) {
				qm.sendNext("你好，骑士团长。现在冒险岛世界面临非常危险的情况。要想防止黑魔法师侵犯这里，需要更多的兵力。为了让士兵们变得更强，我决定和冒险家长老们合力，培养出了比冒险家更强的终极冒险家。")
			}else if (a == 1) {
				qm.sendNext("终极冒险家一出生就是50级，并且拥有特殊的技能。怎么样？你以终极冒险家的面貌获得重生吗？")
			}else if (a == 2) {
				qm.dispose();
		}//status
	}//mode
}//function