//debuger
var status = 0;

//购买VIP价格 有多少个等级就加多少个，不要多不要少
var buyvipprice =new Array(0,25000,50000,80000,120000);

//购买时赠送
//VIP等级 物品ID,数量(非装备可以),力量,敏捷 智力 运气 攻击 魔攻(普通道具属性随便)
var givingitem =new Array(
new Array(1,1032035,1,100,100,100,100,0,0),
new Array(2,1032035,1,200,200,200,200,0,0),
new Array(3,1032035,1,300,300,300,300,0,0),
new Array(4,1032036,1,400,400,400,400,0,0),
new Array(5,1032038,1,500,500,500,500,0,0)
);

//(VIP等级,对应的地图ID,地图名字)
var vipmap =new Array(
new Array(1,910000007,"自由市场7洞"),//new Array(VIP等级,地图ID,地图名称),
new Array(2,910000008,"自由市场8洞"),
new Array(3,910000009,"自由市场9洞"),
new Array(3,910000009,"自由市场9洞"),
new Array(4,910000009,"自由市场9洞"),
new Array(5,910000009,"自由市场9洞"),
new Array(5,910000009,"自由市场9洞")
);

//每日工资,道具等
var dailywageItem=new Array(
new Array(1,4031454,1),//new Array(VIP等级,道具ID,道具数量),
new Array(2,4031454,3),
new Array(3,4031454,6),
new Array(3,4031454,6),
new Array(4,4031454,6),
new Array(5,4031454,6),
new Array(5,4031454,6)
);
//每日 金币 有多少个等级就加多少个，不要多不要少
var dailywageMeso=new Array(1000000,10000000,50000000,50000000,50000000);
//每日 抵用  有多少个等级就加多少个，不要多不要少
var dailywageNX=new Array(1000,10000,50000,50000,50000);


var status1type=-1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (status == 1 && mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        status--;
        cm.dispose();
    }
    if (status == 0) {
		var says="#L1##b购买VIP#l#k\r\n#L2##b领取工资#l#k\r\n#L3##b练级刷钱#l#k\r\n#L4##b造型#l#k";
		cm.sendSimple(says);
	} else if (status == 1) {
		status1type=selection;
		var where = "";
		switch(status1type){
			case 1:
				where+="#b您当前点卷余额为:#r"+cm.getPlayer().getCSPoints(1);
				for (var i = 0; i < buyvipprice.length; i++) {
				where+="\r\n#L"+i+"##b购买#rVIP["+(i+1)+"]#k#b需要点卷:#r"+buyvipprice[i]+"#k#l";
					where+="\r\n\r\n获取土豪礼包:"
					for(var j=0;j<givingitem.length;j++){
						if((i+1)==givingitem[j][0]){
							where+="\r\n#v"+givingitem[j][1]+"##r"+givingitem[j][2]+"#b个 力:#r"+givingitem[j][3]+" #b敏:#r"+givingitem[j][4]+" #b智:#r"+givingitem[j][5]+" #b运:#r"+givingitem[j][6]+" #b物攻:#r"+givingitem[j][7]+" #b魔攻:#r"+givingitem[j][8];
						}
					}
				}
			break;
			case 2:
				where+="#b您的VIP级别为:#r"+cm.getPlayer().getVip();
				where+="\r\n#bVIP每日可以领取的道具为:";
				for(var i=0;i<dailywageItem.length;i++){
					where +="\r\nVIP等级:#r"+dailywageItem[i][0]+"#b可以领取#v"+dailywageItem[i][1]+"##r "+dailywageItem[i][2]+"#b 个。"
				}
				where+="\r\nVIP每日可以领取的金币:";
				for(var i=0;i<dailywageMeso.length;i++){
					where +="\r\nVIP等级:#r"+(i+1)+"#b可以领取#r"+dailywageMeso[i]+"#b 金币。"
				}
				where+="\r\nVIP每日可以领取的抵用券:";
				for(var i=0;i<dailywageNX.length;i++){
					where +="\r\nVIP等级:#r"+(i+1)+"#b可以领取#r"+dailywageNX[i]+"#b 抵用卷。"
				}
			break;
			case 3:
				where+="请选择您要传送的地图";
				for (var i = 0; i < vipmap.length; i++) {
				where += "\r\n#L" + i + "##b"+vipmap[i][2]+" 需要VIP等级大于:"+vipmap[i][0]+"#k#l";
				}
			break;
			case 4:
			cm.sendOk("请自行设置NPC,下面代码//去掉,后面换成美容NPCID");
			//cm.openNpc(9900004);//自己修改美容NPC ID
			cm.dispose();
			break;
			default:
			cm.sendOk("没有该选项");
			cm.dispose();
			break;
		}
        cm.sendSimple(where);

    } else if (status == 2) {		
		var select=selection;
		switch(status1type){
		case 1:
			if(cm.getVip()-1>=select){
				cm.sendOk("你已经是VIP"+cm.getPlayer().getVip()+"了,不需要重复购买"+(select+1)+"");
			}else
			if(cm.getPlayer().getCSPoints(1)>=buyvipprice[select]){
				var cvi=0;				
				for(var i=0;i<givingitem.length;i++){
					if(givingitem[i][0]==cm.getPlayer().getVip()){
						cvi++;
					}					
				}
				if(cm.getPlayer().getSpace(1)>=cvi){
				cm.gainNX(-buyvipprice[select]);
				cm.getPlayer().setVip(select+1);
				for(var i=0;i<givingitem.length;i++){
					if(givingitem[i][0]==(select+1)){
						cm.givingVIPItem(givingitem[i][1],givingitem[i][2],givingitem[i][3],givingitem[i][4],givingitem[i][5],givingitem[i][6],givingitem[i][7],givingitem[i][8]);
					}					
				}
				cm.worldMessage("#b恭喜["+cm.getPlayer().getName()+"] 成为了 VIP "+(select+1)+" 并获得了VIP土豪套装，大家都祝福他");
				cm.sendOk("#b恭喜您成为了VIP"+(select+1)+"");
				}
				else{
				cm.sendOk("#b请确定背包装备栏空间至少空余"+cvi+"个");
				}				
			}
		break;

		case 2:
			if(cm.getPlayer().getVip()<=0){
				cm.sendOk("#b您还不是VIP不能领取");
				break;
			}
			var dcount=cm.getBossLog("vipdayitem");
			var cvi=0;
			var freesolt=99;
				for(var i=0;i<dailywageItem.length;i++){
					if(dailywageItem[i][0]==cm.getVip()){
						var fs=cm.getPlayer().haveSpaceForId(dailywageItem[i][1]);
						if(fs<freesolt){
							freesolt=fs;
						}
						cvi++;
					}
				}
			if(dcount<=0){
				if(freesolt>=cvi){
					for(var i=0;i<dailywageItem.length;i++){
						if(dailywageItem[i][0]==cm.getVip()){
							cm.gainItem(dailywageItem[i][1],dailywageItem[i][2]);
						}
					}
					cm.gainMeso(dailywageMeso[(cm.getVip()-1)]);
					cm.gainNX(2,dailywageNX[(cm.getVip()-1)]);
					cm.setBossLog("vipdayitem");
					var says="#b恭喜你获得了每日VIP礼包:";
					for(var i=0;i<dailywageItem.length;i++){
						if(dailywageItem[i][0]==cm.getVip()){
							says+="\r\n#v"+dailywageItem[i][1]+"##r "+dailywageItem[i][2]+"#b 个。"
						}
					}
					says+="\r\n#b金币: #r"+dailywageMeso[(cm.getVip()-1)]+" #b个"
					says+="\r\n#b抵用券: #r"+dailywageNX[(cm.getVip()-1)]+" #b个"
					cm.sendOk(says);
				}else{
					cm.sendOk("#b请检查您的背包要留出至少 "+cvi+" 个空格位置");
				}				
			}else{
				cm.sendOk("#b今天已经领取过了，请明天再来");
			}
		break;
		case 3:
			if(!cm.isVip()){
				cm.sendOk("您还不是VIP。不能使用该服务");
				break;
			}
			if(vipmap[select][0]<=cm.getVip()){
				cm.warp(vipmap[select][1]);
				cm.playerMessage("使用VIP传送到了:"+vipmap[select][2]);
			}else{
				cm.sendOk("您当前的VIP级别为:"+cm.getVip()+",该地图需要VIP级别为:"+vipmap[select][0]);
			}
		break;
		}
		cm.dispose();		
    } else if (status == 3) {
	
		cm.dispose();
	}
}