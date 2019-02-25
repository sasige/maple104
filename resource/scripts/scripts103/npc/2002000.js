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
            cm.sendSimple("#v3994102##v3994103##v3994104##v3994105#\r\n\r\n\r\n迎来到"+cm.GetSN()+"！,如果您有我需要的物品。我可以给你兑换点卷或者经验，或者药水等等好东西哦，怎么样？想要和我换么？\r\n\r\n#L0##v3994115# [使用100个#v4000001#换10万金币]#l\r\n#L1##v3994116# [使用100个#v4000082#换2000万经验]#l\r\n#L2##v3994117# [使用1个#v4001083#换一个#v4001210#]#l\r\n#L3##v3994117# [使用1个#v4001084#换一个#v4001210#]#l\r\n#L4##v3994117# [使用1个#v4001085#换一个#v4001210#]#l\r\n#L5##v3994118# [使用5个#v4001210#换500点卷]#l\r\n#L6##v3994118# [使用1个#v4001210#换#v1302024#]#l\r\n#L7##v3994118# [使用1个#v4001210#换#v2028010#]#l\r\n#L8##v3994118# [使用10个#v2028010#换#v2049300#]#l\r\n#L9##v3994118# [使用10个#v4001210#换#v2028063#]#l\r\n#L10##v3994118# [使用1个#v2028063#换#v3010183#]#l");
        } else if (status == 1) {
            if (selection == 0) {
                if(cm.haveItem(4000001) == true) {
                    cm.gainItem(4000001,-100); 
                    cm.gainMeso(100000);//兑换10万金币
                    cm.sendOk("恭喜您成功换取5万经验，请再接再厉。");
                    cm.dispose();
                } else {
                    cm.sendOk("你背包里没有足够的#v4000001#，不能换取哟！赶紧去打吧"); 
                    cm.dispose();
                }
            } else if (selection == 1) {
                if(cm.haveItem(4000082) == true) {
                    cm.gainItem(4000082,-100); 
                    cm.gainExp(20000000);//兑换2000万经验
                    cm.sendOk("你成功换取啦20万经验,请再接再厉。");
                    cm.dispose();
                } else {
                    cm.sendOk("你背包里没有足够的#v4000082#，不能换取哟！赶紧去打吧"); 
                    cm.dispose();
                }
            } else if (selection == 2) {
                if(cm.haveItem(4001083) == true) {
                    cm.gainItem(4001083,-1); 
                    cm.gainItem(4001210,+1);//兑换星缘币
                    cm.sendOk("你成功换取啦1个星缘纪念币");
                    cm.dispose();
                } else {
                    cm.sendOk("你背包里没有#v4001083#，不能换取哟！"); 
                    cm.dispose();
                }
            } else if (selection == 3) {
                if(cm.haveItem(4001084) == true) {
                    cm.gainItem(4001084,-1); 
                    cm.gainItem(4001210,+1);//兑换星缘币
                    cm.sendOk("你成功换取啦1个星缘纪念币");
                    cm.dispose();
                } else {
                    cm.sendOk("你背包里没有#v4001084#，不能换取哟！"); 
                    cm.dispose();
                }
            } else if (selection == 4) {
                if(cm.haveItem(4001085) == true) {
                    cm.gainItem(4001085,-1); 
                    cm.gainItem(4001210,+1);//兑换星缘币
                    cm.sendOk("你成功换取啦1个星缘纪念币");
                    cm.dispose();
                } else {
                    cm.sendOk("你背包里没有#v4001085#，不能换取哟！"); 
                    cm.dispose();
                }
            } else if (selection == 5) {
                if(cm.haveItem(4001210) == true) {
                    cm.gainItem(4001210,-5); 
                    cm.getChar().modifyCSPoints(1,500);//兑换点卷
                    cm.sendOk("你成功换取啦500点卷。");
                    cm.dispose();
                } else {
                    cm.sendOk("你背包里没有足够的#v4001210#，不能换取哟！"); 
                    cm.dispose();
                }
            } else if (selection == 6) {
                if(cm.haveItem(4001210) == true) {
                    cm.gainItem(4001210,-1); 
                    cm.gainItem(1302024,+1);//兑换废弃报纸
                    cm.sendOk("你成功换取啦废弃报纸。");
                    cm.dispose();
                } else {
                    cm.sendOk("你背包里没有足够的#v4001210#，不能换取哟！"); 
                    cm.dispose();
                }
            } else if (selection == 7) {
                if(cm.haveItem(4001210) == true) {
                    cm.gainItem(4001210,-1); 
                    cm.gainItem(2028010,+1);//高级潜能兑换卷
                    cm.sendOk("你成功换取啦。");
                    cm.dispose();
                } else {
                    cm.sendOk("你背包里没有足够的#v4001210#，不能换取哟！"); 
                    cm.dispose();
                }
            } else if (selection == 8) {
                if(cm.haveItem(2028010) == true) {
                    cm.gainItem(2028010,-10); 
                    cm.gainItem(2049300,+1);//高级潜能兑换卷
                    cm.sendOk("你成功换取啦。");
                    cm.dispose();
                } else {
                    cm.sendOk("你背包里没有足够的#v2028010#，不能换取哟！"); 
                    cm.dispose();
                }
            } else if (selection == 9) {
                if(cm.haveItem(4001210) == true) {
                    cm.gainItem(4001210,-10); 
                    cm.gainItem(2028063,+1);//胡萝卜椅子兑换卷
                    cm.sendOk("你成功换取啦。");
                    cm.dispose();
                } else {
                    cm.sendOk("你背包里没有足够的#v4001210#，不能换取哟！"); 
                    cm.dispose();
                }
            } else if (selection == 10) {
                if(cm.haveItem(2028063) == true) {
                    cm.gainItem(2028063,-1); 
                    cm.gainItem(3010183,+1);//胡萝卜椅子兑换
                    cm.sendOk("你成功换取啦胡萝卜椅子。");
                    cm.dispose();
                } else {
                    cm.sendOk("你背包里没有足够的#v2028063#，不能换取哟！"); 
                    cm.dispose();
                }
            }
        }
    }
}
