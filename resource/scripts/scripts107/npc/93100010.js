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
			cm.sendSimple("#v3994102##v3994103##v3994104##v3994105#\r\n\r\n\r\n迎来到099单机冒险岛！,如果您有我需要的物品。我可以给你兑换点卷或者经验，或者药水等等好东西哦，怎么样？想要和我换么？\r\n\r\n#L0##v3994115# [使用100个#v4032056#换#v2000005#  超级药水100个]#l\r\n#L1##v3994115# [使用100个#v4032056#换#v2290153#  [能手册]双刀风暴 20 一个]#l\r\n#L2##v3994115# [使用300个#v4032056#换#v2022141#  镇魂包子100个]#l\r\n#L3##v3994115# [使用700个#v4032056#换#v1012084#  小白鼠面装一个]#l\r\n#L4##v3994115# [使用700个#v4032056#换#v1012267#  甜瓜雪糕一个]#l\r\n#L5##v3994115# [使用700个#v4032056#换#v2290155#  [能手册]镜像分身 30 一个]#l\r\n#L6##v3994115# [使用700个#v4032056#换#v2290154#  [能手册]龙卷风 20 一个]#l\r\n#L7##v3994116# [使用1000个#v4032056#换#v1012161#  发光的鼻子一个]#l\r\n#L8##v3994116# [使用1000个#v4032056#换#v1012056#  狗狗鼻一个]#l\r\n#L9##v3994116# [使用2000个#v4032056#换#v3010107#  龙龙的蛋壳椅一个]#l\r\n#L10##v3994116# [使用2500个#v4032056#换#v3010003#  红色时尚转椅一个]#l\r\n#L11##v3994116# [使用2500个#v4032056#换#v1122018#  温暖的围脖一个]#l\r\n#L12##v3994117# [使用4000个#v4032056#换#v1122002#  红色蝴蝶结一个]#l\r\n#L13##v3994117# [使用4500个#v4032056#换#v3010077#  猫头鹰椅子一个]#l\r\n#L14##v3994117# [使用5200个#v4032056#换#v3010094#  漂漂猪椅子一个]#l\r\n#L15##v3994117# [使用7500个#v4032056#换#v3010095#  石头人座椅一个]#l\r\n#L16##v3994117# [使用7500个#v4032056#换#v3010127#  扎昆宝座一个]#l\r\n#L17##v3994118# [使用9000个#v4032056#换#v1032084#  至尊不速之客耳环一个]#l\r\n#L18##v3994118# [使用9000个#v4032056#换#v1142304#  2011年兔年勋章一个]#l\r\n#L19##v3994118# [使用10000个#v4032056#换#v1142178#  冒险岛形象大使勋章一个]#l\r\n#L20##v3994118# [使用10000个#v4032056#换#v3010144#  七夕椅子一个]#l\r\n#L21##v3994118# [使用12000个#v4032056#换#v3010128#  黑龙王座一个]#l\r\n#L22##v3994118# [使用12500个#v4032056#换#v3010183#  胡萝卜椅子一个]#l\r\n#L23##v3994118# [使用12500个#v4032056#换#v3010155#  暗影双刀的猫头鹰椅子一个]#l\r\n");
			} else if (status == 1) {
			if (selection == 0) {
			 if(cm.haveItem(4032056) == true) {
                   	   cm.gainItem(4032056,-100); 
                   	   cm.gainItem(2000005,+100); //超级药水
                   	   cm.sendOk("恭喜您成功换取超级药水100个。");
               		   cm.dispose();
                   	   } else {
		   	   cm.sendOk("你背包里没有足够的#v4032056#，不能换取哟！"); 
		   	   cm.dispose(); }
			} else if (selection == 1) {
			 if(cm.haveItem(4032056) == true) {
                   	   cm.gainItem(4032056,-100); 
                   	   cm.gainItem(2290153,+1);//双刀风暴书
                   	   cm.sendOk("你成功换取啦。");
               		   cm.dispose();
                   	   } else {
		   	   cm.sendOk("你背包里没有足够的#v4032056#，不能换取哟！"); 
		   	   cm.dispose(); }
			} else if (selection == 2) {
			 if(cm.haveItem(4032056) == true) {
                   	   cm.gainItem(4032056,-300); 
                   	   cm.gainItem(2022141,+100);//镇魂包子
                   	   cm.sendOk("你成功换取啦100个镇魂包子");
               		   cm.dispose();
                   	   } else {
		   	   cm.sendOk("你背包里没有#v4032056#，不能换取哟！"); 
		   	   cm.dispose(); }
			} else if (selection == 3) {
			 if(cm.haveItem(4032056) == true) {
                   	   cm.gainItem(4032056,-700); 
                   	   cm.gainItem(1012084,+1);//小白鼠面具
                   	   cm.sendOk("你成功换取啦小白鼠面具.");
               		   cm.dispose();
                   	   } else {
		   	   cm.sendOk("你背包里没有#v4032056#，不能换取哟！"); 
		   	   cm.dispose(); }
			} else if (selection == 4) {
			 if(cm.haveItem(4032056) == true) {
                   	   cm.gainItem(4032056,-700); 
                   	   cm.gainItem(1012267,+1);//甜瓜雪糕
                   	   cm.sendOk("你成功换取啦甜瓜雪糕.");
               		   cm.dispose();
                   	   } else {
		   	   cm.sendOk("你背包里没有#v4032056#，不能换取哟！"); 
		   	   cm.dispose(); }
			} else if (selection == 5) {
			 if(cm.haveItem(4032056) == true) {
                   	   cm.gainItem(4032056,-700); 
                   	   cm.gainItem(2290155,+1);//镜像分身
                   	   cm.sendOk("你成功换取啦。");
               		   cm.dispose();
                   	   } else {
		   	   cm.sendOk("你背包里没有足够的#v4032056#，不能换取哟！"); 
		   	   cm.dispose(); }
			   } else if (selection == 6) {
			 if(cm.haveItem(4032056) == true) {
                   	   cm.gainItem(4032056,-700); 
                   	   cm.gainItem(2290154,+1);//龙卷风
                   	   cm.sendOk("你成功换取啦。");
               		   cm.dispose();
                   	   } else {
		   	   cm.sendOk("你背包里没有足够的#v4032056#，不能换取哟！"); 
		   	   cm.dispose(); }
			   } else if (selection == 7) {
			 if(cm.haveItem(4032056) == true) {
                   	   cm.gainItem(4032056,-1000); 
                   	   cm.gainItem(1012161,+1);//发光鼻子
                   	   cm.sendOk("你成功换取啦发光鼻子。");
               		   cm.dispose();
                   	   } else {
		   	   cm.sendOk("你背包里没有足够的#v4032056#，不能换取哟！"); 
		   	   cm.dispose(); }
			   } else if (selection == 8) {
			 if(cm.haveItem(4032056) == true) {
                   	   cm.gainItem(4032056,-1000); 
                   	   cm.gainItem(1012056,+1);//狗狗鼻
                   	   cm.sendOk("你成功换取啦狗狗鼻。");
               		   cm.dispose();
                   	   } else {
		   	   cm.sendOk("你背包里没有足够的#v4032056#，不能换取哟！"); 
		   	   cm.dispose(); }
			   } else if (selection == 9) {
			 if(cm.haveItem(4032056) == true) {
                   	   cm.gainItem(4032056,-2000); 
                   	   cm.gainItem(3010107,+1);//龙龙的蛋壳椅子
                   	   cm.sendOk("你成功换取啦龙龙的蛋壳椅子。");
               		   cm.dispose();
                   	   } else {
		   	   cm.sendOk("你背包里没有足够的#v4032056#，不能换取哟！"); 
		   	   cm.dispose(); }
			   } else if (selection == 10) {
			 if(cm.haveItem(4032056) == true) {
                   	   cm.gainItem(4032056,-2500); 
                   	   cm.gainItem(3010003,+1);//红色时尚转椅
                   	   cm.sendOk("你成功换取啦红色时尚转椅椅子。");
               		   cm.dispose();
                   	   } else {
		   	   cm.sendOk("你背包里没有足够的#v4032056#，不能换取哟！"); 
		   	   cm.dispose(); }
			   } else if (selection == 11) {
			 if(cm.haveItem(4032056) == true) {
                   	   cm.gainItem(4032056,-2500); 
                   	   cm.gainItem(1122018,+1);//温暖的围脖
                   	   cm.sendOk("你成功换取啦温暖的围脖。");
               		   cm.dispose();
                   	   } else {
		   	   cm.sendOk("你背包里没有足够的#v4032056#，不能换取哟！"); 
		   	   cm.dispose(); }
			   } else if (selection == 12) {
			 if(cm.haveItem(4032056) == true) {
                   	   cm.gainItem(4032056,-4000); 
                   	   cm.gainItem(1122002,+1);//红色蝴蝶结
                   	   cm.sendOk("你成功换取啦红色蝴蝶结。");
               		   cm.dispose();
                   	   } else {
		   	   cm.sendOk("你背包里没有足够的#v4032056#，不能换取哟！"); 
		   	   cm.dispose(); }
			   } else if (selection == 13) {
			 if(cm.haveItem(4032056) == true) {
                   	   cm.gainItem(4032056,-4500); 
                   	   cm.gainItem(3010077,+1);//猫头鹰椅子
                   	   cm.sendOk("你成功换取啦猫头鹰椅子。");
               		   cm.dispose();
                   	   } else {
		   	   cm.sendOk("你背包里没有足够的#v4032056#，不能换取哟！"); 
		   	   cm.dispose(); }
			   } else if (selection == 14) {
			 if(cm.haveItem(4032056) == true) {
                   	   cm.gainItem(4032056,-5200); 
                   	   cm.gainItem(3010094,+1);//漂漂猪椅子
                   	   cm.sendOk("你成功换取啦漂漂猪椅子。");
               		   cm.dispose();
                   	   } else {
		   	   cm.sendOk("你背包里没有足够的#v4032056#，不能换取哟！"); 
		   	   cm.dispose(); }
			   } else if (selection == 15) {
			 if(cm.haveItem(4032056) == true) {
                   	   cm.gainItem(4032056,-7500); 
                   	   cm.gainItem(3010095,+1);//石头人宝座
                   	   cm.sendOk("你成功换取啦石头人宝座椅子。");
               		   cm.dispose();
                   	   } else {
		   	   cm.sendOk("你背包里没有足够的#v4032056#，不能换取哟！"); 
		   	   cm.dispose(); }
			   } else if (selection == 16) {
			 if(cm.haveItem(4032056) == true) {
                   	   cm.gainItem(4032056,-7500); 
                   	   cm.gainItem(3010127,+1);//扎昆宝座
                   	   cm.sendOk("你成功换取啦扎昆宝座椅子。");
               		   cm.dispose();
                   	   } else {
		   	   cm.sendOk("你背包里没有足够的#v4032056#，不能换取哟！"); 
		   	   cm.dispose(); }
			   } else if (selection == 17) {
			 if(cm.haveItem(4032056) == true) {
                   	   cm.gainItem(4032056,-9000); 
                   	   cm.gainItem(1032084,+1);//至尊不速之客耳环
                   	   cm.sendOk("你成功换取啦至尊不速之客耳环。");
               		   cm.dispose();
                   	   } else {
		   	   cm.sendOk("你背包里没有足够的#v4032056#，不能换取哟！"); 
		   	   cm.dispose(); }
			   } else if (selection == 18) {
			 if(cm.haveItem(4032056) == true) {
                   	   cm.gainItem(4032056,-9000); 
                   	   cm.gainItem(1142304,+1);//2011兔年勋章
                   	   cm.sendOk("你成功换取啦2011兔年勋章。");
               		   cm.dispose();
                   	   } else {
		   	   cm.sendOk("你背包里没有足够的#v4032056#，不能换取哟！"); 
		   	   cm.dispose(); }
			   } else if (selection == 19) {
			 if(cm.haveItem(4032056) == true) {
                   	   cm.gainItem(4032056,-10000); 
                   	   cm.gainItem(1142178,+1);//冒险岛形象大使勋章
                   	   cm.sendOk("你成功换取啦冒险岛形象大使勋章。");
               		   cm.dispose();
                   	   } else {
		   	   cm.sendOk("你背包里没有足够的#v4032056#，不能换取哟！"); 
		   	   cm.dispose(); }
			   } else if (selection == 20) {
			 if(cm.haveItem(4032056) == true) {
                   	   cm.gainItem(4032056,-10000); 
                   	   cm.gainItem(3010144,+1);//七夕椅子
                   	   cm.sendOk("你成功换取啦七夕椅子。");
               		   cm.dispose();
                   	   } else {
		   	   cm.sendOk("你背包里没有足够的#v4032056#，不能换取哟！"); 
		   	   cm.dispose(); }
			   } else if (selection == 21) {
			 if(cm.haveItem(4032056) == true) {
                   	   cm.gainItem(4032056,-12000); 
                   	   cm.gainItem(3010128,+1);//黑龙王座
                   	   cm.sendOk("你成功换取啦黑龙王座椅子。");
               		   cm.dispose();
                   	   } else {
		   	   cm.sendOk("你背包里没有足够的#v4032056#，不能换取哟！"); 
		   	   cm.dispose(); }
			   } else if (selection == 22) {
			 if(cm.haveItem(4032056) == true) {
                   	   cm.gainItem(4032056,-12500); 
                   	   cm.gainItem(3010183,+1);//胡萝卜椅子
                   	   cm.sendOk("你成功换取啦胡萝卜椅子。");
               		   cm.dispose();
                   	   } else {
		   	   cm.sendOk("你背包里没有足够的#v4032056#，不能换取哟！"); 
		   	   cm.dispose(); }
			   } else if (selection == 23) {
			 if(cm.haveItem(4032056) == true) {
                   	   cm.gainItem(4032056,-12500); 
                   	   cm.gainItem(3010155,+1);//暗影双刀的猫头鹰椅子
                   	   cm.sendOk("你成功换暗影双刀的猫头鹰椅子。");
               		   cm.dispose();
                   	   } else {
		   	   cm.sendOk("你背包里没有足够的#v4032056#，不能换取哟！"); 
		   	   cm.dispose(); }
			}
		}
	}
}
