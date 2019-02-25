
/* 
*活动脚本 
*/ 
var status = 0; 
  
function start() { 
    status = -1; 
    action(1, 0, 0); 
} 
  
function action(mode, type, selection) { 
    if (mode == -1) { 
        cm.dispose(); 
    } else { 
        if (mode == 0 && status == 0) { 
            cm.dispose(); 
            return; 
        } 
        if (mode == 1) 
            status++; 
        else 
            status--; 
        if (status == 0) { 
  
            cm.sendSimple("#d你好,我是【给你稀有物品】任务NPC,,我需要请选择: \r\n#L1# 【接受任务ON.1】 \r\n#L2# 【接受任务ON.2】 \r\n#L3# 【接受任务ON.3】");  
        } else if (status == 1) { 
            if (selection == 1) { 
                if (cm.haveItem(4000124, 50)) { 
                    cm.gainItem(4000124, -50); 
                    cm.gainItem(4031225, 10); 
                    cm.gainItem(1402037, 1);
                    cm.gainItem(1012070, 1);
                    cm.gainItem(1012071, 1);
                    cm.gainItem(1012072, 1);
                    cm.gainItem(1012073, 1); 
                    cm.sendOk("#r真是太感谢你了,帮我找回了我需要的材料！#r 【东西】送你 去接下一个任务"); 
                    cm.dispose(); 
                } else { 
                    cm.sendOk("#b由于我昨天让【"+cm.GetSN()+"】请客去吃必胜客不小心弄失了我的加工品我现在急需要50个#v4000124#你可以帮我找回来吗？你将获得绝了版的【雪糕】套装【一把】#v1402037#【一】个#v1012070#【一】个 #v1012071#【一】个 #v1012072#【一】个 #v1012073#");  
                    cm.dispose(); 
                } 
            } else if (selection == 2) { 
                if (cm.itemQuantity(4031225) >= 10) { 
                    if ((cm.haveItem(4005000, 1)) && (cm.haveItem(4005001)) && (cm.haveItem(4005002)) && (cm.haveItem(4005003))) { 
                        cm.sendOk("#r哇，这么快就收集好了，好吧我把#v3010111#奖励给你");
                        cm.gainItem(4005000, -10); 
                        cm.gainItem(4005001, -10); 
                        cm.gainItem(4005002, -10); 
                        cm.gainItem(4005003, -10); 
                        cm.gainItem(3010111, 1); 
                        cm.gainItem(4031018, 1); 
                        cm.dispose(); 
                    } else{ 
                        cm.sendOk("#g(*^__^*) 嘻嘻，又是你啊，我需要要重新加工这批东西要很多矿石啊 请您帮我收集这些吧 10个#v4005000#10个#v4005001#10个#v4005002#10个#v4005003# 你将获得绝了版的#v3010111#"); 
                        cm.dispose(); 
                    } 
  
                } else{ 
                    cm.sendOk("#e你没有完成【任务一】"); 
                    cm.dispose(); 
                } 
  
            } else if (status == 3) { 
            } else if (selection == 3 && cm.itemQuantity(4031018) >= 1) { 
                if (cm.getBossLog('renwu') < 1){ 
                    if ((cm.haveItem(1382060, 1)) && (cm.haveItem(1442068)) && (cm.haveItem(1452060)) && (cm.haveItem(1002812))) { 
                        cm.sendOk("#r哎呀 ！哎呀 我的天啊 这样都被你搜集得到 真是太感谢了 这 东西给你 收好了"); 
                        cm.gainItem(1382060, -1); 
                        cm.gainItem(1442068, -1); 
                        cm.gainItem(1452060, -1); 
                        cm.gainItem(1002812, -1); 
                        cm.gainItem(1402014, 1); 
                        cm.gainItem(1142002, 1); 
                        cm.gainItem(1142189, 1); 
                        cm.serverNotice("『活动公告』：恭喜"+ cm.getChar().getName() +"，在乐于助人活动中获取 总共获取了 龙背刃 冻冻鱼 温度计 玩具!"); 
                        cm.dispose(); 
                    } else{ 
                        cm.sendOk("#k【任务狂人】哇，你又来帮助弱小的我了，我现在需要【一】个#v1382060#【一】个#v1442068#【一】个#v1452060#【一】个#v1002812# 如果完成好我这里刚磨练出一把 能上天下海的【温度计】#v1402014#，还要授予你【任务狂人勋章】一枚#v1142002# 和VIP#v1142189#一个"); 
cm.dispose(); 
                    } 
  
                } else { 
                    cm.sendOk("你已经帮过我很多了 去休息一会儿吧"); 
                    mode = 1; 
                    status = -1; 
                } 
            } else{ 
                cm.sendOk("你没有完成【任务二】"); 
                mode = 1; 
                status = -1; 
            } 
        } 
    } 
} 
