function start() {
	
	var rand = Math.floor(Math.random() * 44);
	var item;
	var num;
	var name;
	
	if (rand <3){
		item = 2043003;
		num = 1;
		name = "龙眼镜";
	}else if (rand < 1){
		item = 2044019
		num = 1;
		name = "VIP勋章"
	}else if (rand == 2){
		item = 2044019
		num = 1;
		name = "金魂披风"
	}else if (rand == 3){
		item = 2043103
		num = 1;
		name = "旭日吊坠"
	}else if (rand == 4){
		item = 2043203
		num = 1;
		name = "暗影双刀面巾"
	}else if (rand == 5){
		item = 2043303
		num = 1;
		name = "圣诞鹿变身帽"
	}else if (rand == 11){
		item = 2043703
		num = 1;
		name = "老公老婆戒指LV49"
	}else if (rand == 12){
		item = 2043803
		num = 1;
		name = "暗影双刀秘密卷轴"
	}else if (rand == 6){
		item = 2044003
		num = 1;
		name = "觉醒的冒险之心(战士)"
	}else if (rand == 7){
		item = 2044203
		num = 1;
		name = "觉醒的冒险之心(魔法师)"
	}else if (rand == 8){
		item = 2044303
		num = 1;
		name = "觉醒的冒险之心(弓箭手)"
	}else if (rand == 9){
		item = 2044403
		num = 1;
		name = "觉醒的冒险之心(飞侠)"
	}else if (rand == 10){
		item = 2044503
		num = 1;
		name = "觉醒的冒险之心(海盗)"
	}else if (rand == 11){
		item = 2044603
		num = 1;
		name = "龙眼镜专用特殊卷轴"
	}else if (rand == 12){
		item = 2044815
		num = 1;
		name = "龙眼镜专用特殊卷轴"
	}else if (rand == 13){
		item = 2044908
		num = 1;
		name = "龙眼镜专用特殊卷轴"
	}else if (rand == 14){
		item = 2340000
		num = 1;
		name = "龙眼镜专用特殊卷轴"
	}else if (rand == 15){
		item = 1032142
		num = 1;
		name = "龙眼镜专用特殊卷轴"
	}else if (rand == 16){
		item = 1122197
		num = 1;
		name = "龙眼镜专用特殊卷轴"
	}else if (rand == 17){
		item = 1132152
		num = 1;
		name = "龙眼镜专用特殊卷轴"
	}else if (rand == 18){
		item = 1002961
		num = 1;
		name = "龙眼镜专用特殊卷轴"
	}else if (rand == 19){
		item = 1003540
		num = 1;
		name = "龙眼镜专用特殊卷轴"
	}else if (rand == 20){
		item = 1082432
		num = 1;
		name = "龙眼镜专用特殊卷轴"
	}else if (rand == 21){
		item = 4002002
		num = 5;
		name = "龙眼镜专用特殊卷轴"
	}else if (rand == 22){
		item = 4002002
		num = 10;
		name = "龙眼镜专用特殊卷轴"
	}else{
		item = 4002002
		num = 5;
		name = "龙眼镜专用特殊卷轴"
	}//rand
	var ii = Packages.server.MapleItemInformationProvider.getInstance();	
	im.gainItem(item,+num); //随机这个道具
	im.gainItem(2430483,-1); //减少1个使用的这个道具
	im.sendOk("获取了 #v"+item+"# "+num+"个")
	im.worldMessage("[打工系统]：恭喜玩家<" + im.getChar().getName() + ">打开金猪梦箱子获得了"+ii.getName(item)+"" );
	im.dispose(); 
}