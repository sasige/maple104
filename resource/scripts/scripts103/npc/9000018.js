importPackage(net.sf.odinms.client);
var status = 0;
//·········以下为VIP地图设置,请根据需要设置地图ID
var vip1map = 803001200;
var vip2map = 925020000;
var vip2bj = 910000004;
var vip3map = 925010400;
var vip3bj = 910000005;
var vip4map = 920010000;
var vip4bj = 910000006;


//·········以下为工资（金币）参数,请根据需要自行配置;
var GZ_Player_money = 2000000;
var GZ_V1_money = 50000000;
var GZ_V2_money = 100000000;
var GZ_V3_money = 200000000;
var GZ_V4_money = 300000000;


//·········以下为工资（道具）参数,请根据需要自行配置; 
var GZ_Player_item = Array(1002140,0);   //配置方法 只需更改 Aarray(道具ID,数量);
var GZ_V1_item = Array(2340000,0);       //数量为0 表示不给.
var GZ_V2_item = Array(2340000,5);
var GZ_V3_item = Array(2340000,10);
var GZ_V4_item = Array(2340000,20);


//·········以下为工资（抵用券）参数,请根据需要自行配置;

var GZ_Player_Nx = 10000;
var GZ_V1_Nx = 10000;
var GZ_V2_Nx = 20000;
var GZ_V3_Nx = 30000;
var GZ_V4_Nx = 40000;


//·········以下为工资（宿命豆）参数,请根据需要自行配置;

var GZ_Player_zb = 1000;
var GZ_V1_zb = 1000;
var GZ_V2_zb = 5000;
var GZ_V3_zb = 7000;
var GZ_V4_zb = 10000;


//////////////////////////////////////////////////////////
function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (status >= 0 && mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            cm.sendSimple("你好，这是#r"+cm.GetSN()+"#k各职业等级武器交易中意，希望你能喜欢！\r\n请选择您要购买的类型.\r\n#b★★★★★★★★★★130级高级装备★★★★★★★★★★\r\n#L1#战士系列130级装备#l        #b#L2#法师系列130级装备#l\r\n#b#L3#弓手系列130级武器#l        #b#L4#飞侠系列130级武器#l\r\n#b#L5#海盗系列130级装备#l\r\n\r\n★★★★★★★★★★140级顶级装备★★★★★★★★★★\r\n#L6#战士系列140级装备#l        #L7#法师系列140级装备#l\r\n#L8#弓手系列140级装备#l        #L9#飞侠系列140级装备#l\r\n#b#L10#海盗系列140级装备#l");	
			
        }else if (status == 1) {

            var viplevel = cm.getChar().getVip();

            if(selection == 0){
                if(viplevel < 0){					
                    cm.sendOk("#b您不是幸福会员,无法进入此地图")
                    cm.dispose();
                }else{
                    cm.warp(vip1map,0);
                    cm.sendOk("#b暴绯红系列装备!.");					
                    cm.dispose();
                }

            } else if (selection == 10) { 
                cm.openNpc(9270057);

            } else if (selection == 1) { 
                cm.openNpc(9250016);

            }else if(selection == 2){
                cm.openNpc(9250008);

            } else if (selection == 3) { 
                cm.openNpc(9250009);

            } else if (selection == 7) { 
                cm.openNpc(9120013);
            } else if (selection == 8) { 
                cm.openNpc(9000021);
            } else if (selection == 9) { 
                cm.openNpc(9270056);
            } else if (selection == 4) {
                cm.openNpc(9250011);
            } else if (selection == 11) {
                cm.warp(921133000,0);
                cm.dispose();

            } else if (selection == 5) { 
                cm.openNpc(9250006);

            } else if (selection == 6) { 
                cm.openNpc(9120017);




            }										
        }
    }
}

