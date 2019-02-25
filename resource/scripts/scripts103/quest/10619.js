/*
脚本类型：Quest
脚本ID：10619
脚本名字：暗影双刀 - 每日两倍增益
脚本所在地图：X
脚本制作者：笔芯
*/
var status = -1;

function end(mode, type, selection) {//接任务
    if (mode == -1) {
        qm.dispose();
    } else {
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            if (qm.haveItem(3994193)){
                qm.sendSimple("你把耀眼的羽毛带来啦。你想获得什么增益呢？\r\n#b#L0# #i2022694# #t2022694##l\r\n#L1# #i2450018# #t2450018##l")
            }else{
                qm.sendOk("#r错误#k：请和管理员联系。")
                qm.dispose();
            }
        } else if (status == 1) {
            if(selection == 0){
                qm.sendOk("你获得了经验值2倍增益。在接下去的30分钟内，打猎时获得的经验值提高为2倍。退出游戏或角色死亡时，增益效果消失。敬请注意。")
                //(我要自动使用这个道具的。。没函数)qm.gainItem(2450018,1)//暗影双刀-经验值2倍！
                qm.dispose();
            }else{
                qm.sendOk("你获得了怪物暴率2倍增益。在接下去的30分钟内，打猎时获得的经验值提高为2倍。退出游戏或角色死亡时，增益效果消失。敬请注意。")
                //(我要自动使用这个道具的。。没函数)qm.gainItem(2022694,1)//暗影双刀-掉落率2倍！
                qm.dispose();
            }//selection
        }//status
    }//mode
}//function