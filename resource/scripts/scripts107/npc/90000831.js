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
                
			cm.sendOk("好的,如果你想好了要做什么,我会很乐意的为你服务的..");
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
			cm.sendSimple("#v3994102##v3994103##v3994104##v3994105#\r\n\r\n\r\n迎来到099单机冒险岛！,如果您有我需要的物品。我可以给你兑换点卷或者经验，或者药水等等好东西哦，怎么样？想要和我换么？\r\n\r\n#L0##v3994115# [使用100个#v4000001#换5万经验]#l\r\n#L1##v3994116# [使用100个#v4000082#换20万经验]#l\r\n#L2##v3994117# [使用1个#v4001083#换一个#v4001210#]#l\r\n#L3##v3994117# [使用1个#v4001084#换一个#v4001210#]#l\r\n#L4##v3994117# [使用1个#v4001085#换一个#v4001210#]#l\r\n#L5##v3994118# [使用100个#v4001210#换1000点卷]#l");
			} else if (status == 1) {
			if (selection == 0) {
			 if(cm.haveItem(4000001) == true) {
                   	   cm.gainItem(4000001,-100); 
                   	   cm.gainExp(50000);//兑换5万经验
                   	   cm.sendOk("恭喜您成功换取5万经验，请再接再厉。");
               		   cm.dispose();
                   	   } else {
		   	   cm.sendOk("你背包里没有足够的#v4000001#，不能换取哟！赶紧去打吧"); 
		   	   cm.dispose(); }
			} else if (selection == 1) {
			 if(cm.haveItem(4000082) == true) {
                   	   cm.gainItem(4000082,-100); 
                   	   cm.gainExp(200000);//兑换20万经验
                   	   cm.sendOk("你成功换取啦20万经验,请再接再厉。");
               		   cm.dispose();
                   	   } else {
		   	   cm.sendOk("你背包里没有足够的#v4000082#，不能换取哟！赶紧去打吧"); 
		   	   cm.dispose(); }
			} else if (selection == 2) {
			 if(cm.haveItem(4001083) == true) {
                   	   cm.gainItem(4001083,-1); 
                   	   cm.gainItem(4001210,+1);//兑换星缘币
                   	   cm.sendOk("你成功换取啦1个星缘纪念币");
               		   cm.dispose();
                   	   } else {
		   	   cm.sendOk("你背包里没有#v4001083#，不能换取哟！"); 
		   	   cm.dispose(); }
			} else if (selection == 3) {
			 if(cm.haveItem(4001084) == true) {
                   	   cm.gainItem(4001084,-1); 
                   	   cm.gainItem(4001210,+1);//兑换星缘币
                   	   cm.sendOk("你成功换取啦1个星缘纪念币");
               		   cm.dispose();
                   	   } else {
		   	   cm.sendOk("你背包里没有#v4001084#，不能换取哟！"); 
		   	   cm.dispose(); }
			} else if (selection == 4) {
			 if(cm.haveItem(4001085) == true) {
                   	   cm.gainItem(4001085,-200); 
                   	   cm.gainItem(4001210,+1);//兑换星缘币
                   	   cm.sendOk("你成功换取啦1个星缘纪念币");
               		   cm.dispose();
                   	   } else {
		   	   cm.sendOk("你背包里没有#v4001085#，不能换取哟！"); 
		   	   cm.dispose(); }
			} else if (selection == 5) {
			 if(cm.haveItem(4001210,100) == true) {
                   	   cm.gainItem(4001210,-100); 
                   	   cm.getChar().modifyCSPoints(2,1000);//兑换点卷
                   	   cm.sendOk("你成功换取啦1000点卷。");
               		   cm.dispose();
                   	   } else {
		   	   cm.sendOk("你背包里没有足够的#v4001210#，不能换取哟！"); 
		   	   cm.dispose(); }
			}
		}
	}
}
