importPackage(java.util);
importPackage(net.sf.odinms.client);
importPackage(net.sf.odinms.server);
importPackage(net.sf.odinms.client.inventory);

var status = 0;
var typed=0;
var selStr;
var xold;
var xnew;
var itemtype=0;
var onesel=0;

var items1=new Array("1452111","1462099","1402095","1422066","1412065","1312065","1322096","1302152","1442116","1432086","1382104","1372084","1332130","1472122","1482084","1492085","1532018","1342036");
var items2=new Array("1302081","1312037","1322060","1332073","1332074","1372044","1382057","1402046","1412033","1422037","1432047","1442063","1452057","1462050","1472068","1482023","1492023","1302086","1312038","1322061","1332075","1332076","1372045","1382059","1402047","1412034","1422038","1432049","1442067","1452059","1462051","1472071","1482024","1492025");
var items3=new Array("1482085","1402096","1422067","1412066","1452112","1342035","1332131","1492086","1522017","1532017","1472123","1382105","1312066","1462100","1322097","1372085","1302153","1442117","1432087");
var items4=new Array("1402073","1462076","1452058","1112584","1112584","1122104","1132085","1032093","1012239");



function start() {
    cm.sendNext("#r想得到更高攻击的装备吗？挑战最高攻击极限,你只要给我两个相同的物品，就可以合成更高级的武器，举例说明一下：\r\n--------------------------------------------------#k\r\n如果你有#b两把普通的110双手剑#k，那么交给我后，我将给你一把名字为：[#r ① #k]的110级双手剑，攻击肯定要#b比普通的高#k哟\r\n--------------------------------------------------\r\n如果你有两把名字为：[ #r①#k ]的110级双手剑，可以继续找我合成名字为：[ #r②#k ]的110级双手剑.\r\n--------------------------------------------------\r\n#k你大概明白合成的方法了吧？\r\n 不明白也没关系，点击下一步我们正式的合成一把看，合成上面没有的装备可以联系客服QQ提供装备名字添加~#r注意，这个只能合成天然的装备，加过属性的装备合成的话属性会丢失，所以请注意!!!");
}

function action(mode, type, selection) {
    if (mode == -1)
        cm.dispose();
    else {
        if (mode == 0) {
            cm.dispose();
        } else
            status++;
        if (status == 1){
			cm.sendSimple("目前只开放了120-140级武器的合成：\r\n#r注意，这个只能合成天然的装备，加过属性的装备合成的话属性会丢失，所以请注意!!!\r\n#b\r\n#L0#>>>>普通装备合成①星装备#l\r\n#L1#>>>>①星装备合成②星装备#l\r\n#L2#>>>>②星装备合成③星装备#l\r\n#L3#>>>>③星装备合成④星装备#l\r\n#L4#>>>>④星装备合成⑤星装备#l\r\n#L5#>>>>⑤星装备合成⑥星装备#l\r\n#L6#>>>>⑥星装备合成⑦星装备#l\r\n#L7#>>>>⑦星装备合成⑧星装备#l\r\n#L8#>>>>⑧星装备合成终极神器#l");
			
		}else if (status == 2){
			if(selection==0){
				xold=".";
				xnew="①";
			}else if(selection==1){
				xold="①";
				xnew="②";
			}else if(selection==2){
				xold="②";
				xnew="③";
			}else if(selection==3){
				xold="③";
				xnew="④";
			}else if(selection==4){
				xold="④";
				xnew="⑤";
			}else if(selection==5){
				xold="⑤";
				xnew="⑥";
			}else if(selection==6){
				xold="⑥";
				xnew="⑦";
			}else if(selection==7){
				xold="⑦";
				xnew="⑧";
			}else if(selection==8){
				xold="⑧";
				xnew="[终极神器]";
			}
			cm.sendSimple("请选择你要合成的武器类别:\r\n#r注意，这个只能合成天然的装备，加过属性的装备合成的话属性会丢失，所以请注意!!!\r\n#b\r\n#L1#140级装备#l #L2#120级装备#l#L3#130级装备#l #L4#其它装备#l");
		}else if (status == 3){
			selStr = " 请选择你要合成的武器.每合成一次随机加10至30攻击.\r\n";
			itemtype=selection;
			if(selection==1){
				for (var i = 0; i < items1.length; i++) {
					selStr += "\r\n#b#L" + i + "#合成[#d#z" +items1[i]+"##b]"+xnew+" 需要2把 #d#z"+items1[i]+"##b"+xold+"，#r[开始合成]#l";
				}
			}
			if(selection==2){
				for (var i = 0; i < items2.length; i++) {
					selStr += "\r\n#b#L" + i + "#合成[#d#z" +items2[i]+"##b]"+xnew+" 需要2把 #d#z"+items2[i]+"##b"+xold+"，#r[开始合成]#l";
				}
			}
			if(selection==3){
				for (var i = 0; i < items3.length; i++) {
					selStr += "\r\n#b#L" + i + "#合成[#d#z" +items3[i]+"##b]"+xnew+" 需要2把 #d#z"+items3[i]+"##b"+xold+"，#r[开始合成]#l";
				}
			}
			if(selection==4){
				for (var i = 0; i < items4.length; i++) {
					selStr += "\r\n#b#L" + i + "#合成[#d#z" +items4[i]+"##b]"+xnew+" 需要2把 #d#z"+items4[i]+"##b"+xold+"，#r[开始合成]#l";
				}
			}
			cm.sendSimple(selStr);
		}else if (status == 4){
			if(itemtype==1){
				onesel=items1[selection];//选择的物品ID
			}
			if(itemtype==2){
				onesel=items2[selection];//选择的物品ID
			}
			if(itemtype==3){
				onesel=items3[selection];//选择的物品ID
			}
			if(itemtype==4){
				onesel=items4[selection];//选择的物品ID
			}
			var inv = cm.getInventory(1);
			var it;
			var itemids;
			var checkitem=0;
			var itemsrc=0;
			var itemsrc2=0;
			for (var i = 0; i <= 96; i++) {
				it = inv.getItem(i);
				if (it != null) {
					itemids = it.getItemId();
					if(itemids==onesel){//检查是否等于这个物品
						if(xold.equals(".")==true){
							if(it.getOwner().length()>0){
							}else{
								checkitem+=1;
								if(checkitem==1){
									itemsrc=i;
								}
								if(checkitem==2){
									itemsrc2=i;
									break;//跳出FOR
								}
							}
						}else if(it.getOwner().length()>0){//检查是否有加个星的
							if(it.getOwner().substring(0, 1).equals(xold)==true){//检查是否符号加星的条件
								checkitem+=1;
								if(checkitem==1){
									itemsrc=i;
								}
								if(checkitem==2){
									itemsrc2=i;
									break;//跳出FOR
								}
							}
						}
					}
				}
			}
			if(checkitem==2){//检测到物品，开始合成程序，
				var itemd = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.EQUIP).getItem(itemsrc).copy();
				itemd.setOwner(xnew);
				var hwchancess= Math.floor(Math.random()*10+5);
				if(itemd.getMatk()!=0){
					itemd.setMatk(itemd.getMatk()*1+hwchancess);
				}
				if(itemd.getWatk()!=0){
					itemd.setWatk(itemd.getWatk()*1+hwchancess);
				}
				var ii = Packages.server.MapleItemInformationProvider.getInstance();
				cm.worldMessage("【"+ cm.getChar().getName() +"】通过市场NPC[魔鬼入口]合成出超级神器~大家恭喜它~");
				Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, itemsrc, 1, true);
				Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, itemsrc2, 1, true);
				Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(),itemd,false);
				cm.sendOk("恭喜，合成成功.\r\n扣除两个#r"+xold+"#v"+onesel+"##k.在给你一个新的#r"+xnew+"#v"+onesel+"#.");
			}else{
				cm.sendOk("对不起，你没有两个"+xold+"#v"+onesel+"#.所以合成"+xnew+"#v"+onesel+"#失败.");
			}
			cm.dispose();
			return;
		}//end stats
    }
}