//Kippieeej for the base of the script, Mikethemak for editing it for this function.
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
                cm.sendSimple("Hello #h #, 欢迎来到 #r毛毛冒险岛#k\r\n点卷中介商人!从我这里交易将收取中介费200点卷哦！~ \r\n#L1##b使用5000点卷兑换1个 #v4002001##k#l \r\n \r\n#L2##b使用1个 #v4002001#兑换4800点卷#k#l\r\n #L3##b使用1000点卷换2000W游戏币#k#l");
        //cm.dispose();
            } else if (status == 1) {
            if (selection == 1) {
    if (cm.itemQuantity(4002001) >= 50) {
    cm.sendOk(" 你储存了足够的 #v4002001# 了, 你不能在存放更多的 #v4002001# 了");
    cm.dispose();
        }  else if (cm.getChar().getNX() >= 5000) {
                    cm.gainNX(-5000);                
                    cm.gainItem(4002001, 1); 
                    cm.dispose();
                } else {
                    cm.sendOk("You don't have enough #bMesos#k, are you trying to #eScam#k me!?");
                    cm.dispose();
                }                                
            } else if (selection == 2) {
        if (cm.getMeso() >= 2100000000) {
        cm.sendOk("请花掉你身上的钱吧，强行兑换会使你的钱变成负数哦，呵呵");
                cm.dispose();
        } else if (cm.itemQuantity(4002001) >= 1) {
                   cm.gainNX(4800);                
                    cm.gainItem(4002001, -1); 
                    cm.dispose();
                } else {
                    cm.sendOk("你没有 #v4002001#. 不要想轻易在我这骗到任何东西!");
                    cm.dispose();
                }    
            } else if (selection == 3) {
    if (cm.itemQuantity(5200000) >= 50) {
    cm.sendOk(" 你有足够多的 #v5200001# 了, 试着兑换一部分 #v5200001# 再来找我兑换银袋.");
    cm.dispose();
    } else if (cm.getChar().getNX() >= 1000) {
                    cm.gainNX(-1000);                
                     cm.gainMeso(20000000);
                    cm.dispose();
                } else {
                    cm.sendOk("You don't have enough #bMesos#k, are you trying to #eScam#k me!?");
                    cm.dispose();
                  }
        } else if (selection == 4) {
        if (cm.getMeso() >= 1147000000) {
        cm.sendOk("请花掉你身上的钱吧，强行兑换会使你的钱变成负数哦，呵呵");
                cm.dispose();
                } else if (cm.itemQuantity(5200001) >= 1) {
                    cm.gainMeso(950000000);                
                    cm.gainItem(5200001, -1); 
                    cm.dispose();
                } else {
                    cm.sendOk("你根本就没有 #v5200001#. 挣够钱再来换吧我,这可不是免费服务!");
                    cm.dispose();
              }    
            } else if (selection == 5) {
    if (cm.itemQuantity(5200000) >= 50) {
    cm.sendOk(" 你有足够多的 #v5200000# 了, 先兑换一部分 #v5200000# 再来找我商量兑换铜币包的事");
    cm.dispose();
    } else if (cm.getMeso() >= 500000000) {
                    cm.gainMeso(-500000000);                
                    cm.gainItem(5200000, 1); 
                    cm.dispose();
                } else {
                    cm.sendOk("You don't have enough #bMesos#k, are you trying to #eScam#k me!?");
                    cm.dispose();
            }
                } else if (selection == 6) {
        if (cm.getMeso() >= 1647000000) {
        cm.sendOk("请花掉你身上的钱吧，强行兑换会使你的钱变成负数哦，呵呵");
                cm.dispose();
                } else if (cm.itemQuantity(5200000) >= 1) {
                    cm.gainMeso(450000000);                
                    cm.gainItem(5200000, -1); 
                    cm.dispose();
                } else {
                    cm.sendOk("你根本就没有 #v5200000#. 挣够钱再来找我兑换吧!.");
                    cm.dispose();
                    }    
                }
            }
        }
    }
