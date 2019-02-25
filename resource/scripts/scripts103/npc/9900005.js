importPackage(net.sf.odinms.client);
importPackage(net.sf.odinms.tools);
importPackage(net.sf.odinms.server.maps);
importPackage(net.sf.odinms.client.messages);
importPackage(net.sf.odinms.net.channel);
importPackage(net.sf.odinms.server);


var status = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (status == 0 && mode == 0) {
			cm.dispose();
			return;
		} else if (status == 2 && mode == 0) {
			cm.dispose();
		}
		if (mode == 1)
			status++;
		else
			status--;
if (status == 0) {
cm.sendSimple("#r请选择您的职业\r\n#r注意：这里只能学习3-4转的技能，必须3转以后才能学习\r\n#d#L19#骑宠#L20#群宠#L18#双刀\n#L17#战神\r\n\r\n#L13#魂骑士\n#L14#炎术士\n#L16#夜行者\n#L15#风灵使者#b\r\n\r\n#L1#勇士/英雄\n#L2#骑士/圣骑士\n#L3#龙骑士/黑骑士\r\n\r\n#L4#祭司/主教\n#L5#火毒/魔导师\n#L6#冰雷/魔导师\r\n\r\b#b#L7#游侠/箭神\n#L8#射手/神射手\n#L9#隐士/无影人\r\n\r\n#b#L11#大副/船长\n#L10#侠盗/独行客\n#L12#斗士/冲锋队长")
} else if (status == 1) {//选项部分
//=====================勇士、英雄
if (selection == 1){
if (cm.getJob().getId() == 111 || cm.getJob().getId() == 112){
//勇士
cm.teachSkill(1110000,20,20);//魔力恢复
cm.teachSkill(1110001,20,20);//盾防精通
cm.teachSkill(1111002,30,30);//斗气集中
cm.teachSkill(1111003,30,30);//狂乱之剑
cm.teachSkill(1111004,30,20);//狂乱之斧
cm.teachSkill(1111005,30,30);//气绝剑
cm.teachSkill(1111006,30,30);//气绝斧
cm.teachSkill(1111007,20,20);//防御崩坏
cm.teachSkill(1111008,30,30);//虎咆哮
//英雄
cm.teachSkill(1120003,30,30);//进阶斗气
cm.teachSkill(1120004,30,30);//阿基里斯
cm.teachSkill(1120005,30,30);//寒冰掌
cm.teachSkill(1121000,30,30);//冒险岛勇士
cm.teachSkill(1121001,30,30);//磁石
cm.teachSkill(1121002,30,30);//稳如泰山
cm.teachSkill(1121006,30,30);//突进
cm.teachSkill(1121008,30,30);//轻舞飞扬
//cm.teachSkill(1121009,30,30);//英雄之斧
cm.teachSkill(1121010,30,30);//葵花宝典
cm.teachSkill(1121011,5,5);//士的意志
cm.sendOk("已经成功学习技能!");
			cm.dispose();
}else{
			cm.sendOk("当前职业不符合学习要求。")
			cm.dispose();
}


//=====================骑士、圣骑士
}else if (selection == 2){
if (cm.getJob().getId() == 121 || cm.getJob().getId() == 122){
//骑士
cm.teachSkill(1210000,20,20);//魔力恢复
cm.teachSkill(1210001,20,20);//盾防精通
cm.teachSkill(1211002,30,30);//属性攻击
cm.teachSkill(1211003,30,30);//烈焰之剑
cm.teachSkill(1211004,30,30);//烈焰钝器
cm.teachSkill(1211005,30,30);//寒冰之剑
cm.teachSkill(1211006,30,30);//寒冰钝器
cm.teachSkill(1211007,30,30);//雷电之击：剑
cm.teachSkill(1211008,30,30);//雷电之击：钝器
cm.teachSkill(1211009,20,20);//魔击无效
//圣骑士
cm.teachSkill(1220005,30,30);//阿基里斯
cm.teachSkill(1220006,30,30);//寒冰掌
cm.teachSkill(1220010,10,10);//万佛归一破
cm.teachSkill(1221000,30,30);//冒险岛勇士
cm.teachSkill(1221001,30,30);//磁石
cm.teachSkill(1221002,30,30);//稳如泰山
cm.teachSkill(1221003,20,20);//圣灵之剑
cm.teachSkill(1221004,20,20);//圣灵之锤
cm.teachSkill(1221007,30,30);//突进
cm.teachSkill(1221009,30,30);//连环环破
cm.teachSkill(1221011,30,30);//圣域
cm.teachSkill(1221012,5,5);//勇士的意志
cm.sendOk("已经成功学习技能!");
			cm.dispose();
}else{
			cm.sendOk("当前职业不符合学习要求。")
			cm.dispose();
}


//=====================龙骑士、黑骑士
}else if (selection == 3){
if (cm.getJob().getId() == 131 || cm.getJob().getId() == 132){
//龙骑士
cm.teachSkill(1300000,20,20);//精准枪
cm.teachSkill(1300001,20,20);//精准矛
cm.teachSkill(1300002,30,30);//终极枪
cm.teachSkill(1300003,30,30);//终极矛
cm.teachSkill(1301004,20,20);//快速枪
cm.teachSkill(1301005,20,20);//快速矛
cm.teachSkill(1301006,20,20);//极限防御
cm.teachSkill(1301007,30,30);//神圣之火
cm.teachSkill(1310000,20,20);//魔法抗性
cm.teachSkill(1311001,30,30);//枪连击
cm.teachSkill(1311002,30,30);//矛连击
cm.teachSkill(1311003,30,30);//无双枪
cm.teachSkill(1311004,30,30);//无双矛
cm.teachSkill(1311005,30,30);//龙之献祭
cm.teachSkill(1311006,30,30);//龙咆哮
cm.teachSkill(1311007,20,20);//力量崩坏
cm.teachSkill(1311008,20,20);//龙之魂
//黑骑士
cm.teachSkill(1320005,30,30);//阿基里斯
cm.teachSkill(1320006,30,30);//恶龙附身
cm.teachSkill(1320008,25,25);//灵魂治愈
cm.teachSkill(1320009,25,25);//灵魂祝福
cm.teachSkill(1321000,30,30);//冒险岛勇士
cm.teachSkill(1321001,30,30);//磁石
cm.teachSkill(1321002,30,30);//稳如泰山
cm.teachSkill(1321003,30,30);//突进
cm.teachSkill(1321007,10,10);//灵魂助力
cm.teachSkill(1321010,5,5);//勇士的意志
cm.sendOk("已经成功学习技能!");
			cm.dispose();
}else{
			cm.sendOk("当前职业不符合学习要求。")
			cm.dispose();
}
//=====================祭司、主教
}else if (selection == 4){
if (cm.getJob().getId() == 231 || cm.getJob().getId() == 232){
//祭司
cm.teachSkill(2310000,20,20);//魔法抗性
cm.teachSkill(2311001,20,20);//净化
cm.teachSkill(2311002,20,20);//时空门
cm.teachSkill(2311003,30,30);//神圣祈祷
cm.teachSkill(2311004,30,30);//圣光
cm.teachSkill(2311005,30,30);//巫毒术
cm.teachSkill(2311006,30,30);//圣龙召唤
//主教
cm.teachSkill(2321000,30,30);//冒险岛勇士
cm.teachSkill(2321001,30,30);//创世之破
cm.teachSkill(2321002,30,30);//魔法反击
cm.teachSkill(2321003,30,30);//强化圣龙
cm.teachSkill(2321004,30,30);//终极无限
cm.teachSkill(2321005,30,30);//圣灵之盾
cm.teachSkill(2321006,10,10);//复活术
cm.teachSkill(2321007,30,30);//光芒飞箭
cm.teachSkill(2321008,30,30);//圣光普照
cm.teachSkill(2321009,5,5);//勇士的意志
cm.sendOk("已经成功学习技能!");
			cm.dispose();
}else{
			cm.sendOk("当前职业不符合学习要求。")
			cm.dispose();
}


//=====================巫师火毒、魔导师火毒
}else if (selection == 5){
if (cm.getJob().getId() == 211 || cm.getJob().getId() == 212){
//巫师火毒
cm.teachSkill(2110000,20,20);//火毒抗性
cm.teachSkill(2110001,30,30);//魔力激化
cm.teachSkill(2111002,30,30);//末日烈焰
cm.teachSkill(2111003,30,30);//致命毒雾
cm.teachSkill(2111004,20,20);//封印术
cm.teachSkill(2111005,20,20);//魔法狂暴
cm.teachSkill(2111006,30,30);//火毒合击
//魔导师火毒
cm.teachSkill(2121000,30,30);//冒险岛勇士
cm.teachSkill(2121001,30,30);//创世之破
cm.teachSkill(2121002,30,30);//魔法反击
cm.teachSkill(2121003,30,30);//火凤球
cm.teachSkill(2121004,30,30);//终极无限
cm.teachSkill(2121005,30,30);//冰破魔兽
cm.teachSkill(2121006,30,30);//连环爆破
cm.teachSkill(2121007,30,30);//天降落星
cm.teachSkill(2121008,5,5);//勇士的意志
cm.sendOk("已经成功学习技能!");
			cm.dispose();
}else{
			cm.sendOk("当前职业不符合学习要求。")
			cm.dispose();
}
//=====================巫师冰雷、魔导师冰雷
}else if (selection == 6){
if (cm.getJob().getId() == 221 || cm.getJob().getId() == 222){
//巫师冰雷
cm.teachSkill(2210000,20,20);//冰雷抗性
cm.teachSkill(2210001,30,30);//魔力激化
cm.teachSkill(2211002,30,30);//冰咆哮
cm.teachSkill(2211003,30,30);//落雷枪
cm.teachSkill(2211004,20,20);//封印术
cm.teachSkill(2211005,20,20);//魔法狂暴
cm.teachSkill(2211006,30,30);//冰雷合击
//魔导师冰雷
cm.teachSkill(2221000,30,30);//冒险岛勇士
cm.teachSkill(2221001,30,30);//创世之破
cm.teachSkill(2221002,30,30);//魔法反击
cm.teachSkill(2221003,30,30);//冰凤球
cm.teachSkill(2221004,30,30);//终极无限
cm.teachSkill(2221005,30,30);//火魔兽
cm.teachSkill(2221006,30,30);//链环闪电
cm.teachSkill(2221007,30,30);//落霜冰破
cm.teachSkill(2221008,5,5);//勇士的意志
cm.sendOk("已经成功学习技能!");
			cm.dispose();
}else{
			cm.sendOk("当前职业不符合学习要求。")
			cm.dispose();
}


//=====================游侠、箭神
}else if (selection == 7){
if (cm.getJob().getId() == 321 || cm.getJob().getId() == 322){
//游侠
cm.teachSkill(3210000,20,20);//疾风步
cm.teachSkill(3210001,20,20);//贯穿箭
cm.teachSkill(3211002,20,20);//替身术
cm.teachSkill(3211003,30,30);//寒冰箭
cm.teachSkill(3211004,30,30);//升龙弩
cm.teachSkill(3211005,30,30);//金鹰召唤
cm.teachSkill(3211006,30,30);//箭扫射
//箭神
cm.teachSkill(3220004,30,30);//神弩手
cm.teachSkill(3221000,30,30);//冒险岛勇士
cm.teachSkill(3221001,30,30);//穿透箭
cm.teachSkill(3221002,30,30);//火眼晶晶
cm.teachSkill(3221003,30,30);//飞龙冲击波
cm.teachSkill(3221005,30,30);//冰凤凰
cm.teachSkill(3221006,30,30);//刺眼箭
cm.teachSkill(3221007,30,30);//一击要害箭
cm.teachSkill(3221008,5,5);//勇士的意志
cm.sendOk("已经成功学习技能!");
			cm.dispose();
}else{
			cm.sendOk("当前职业不符合学习要求。")
			cm.dispose();
}


//=====================射手、神射手
}else if (selection == 8){
if (cm.getJob().getId() == 311 || cm.getJob().getId() == 312){
//射手
cm.teachSkill(3110000,20,20);//疾风步
cm.teachSkill(3110001,20,20);//贯穿箭
cm.teachSkill(3111002,20,20);//替身术
cm.teachSkill(3111003,30,30);//烈火箭
cm.teachSkill(3111004,30,30);//箭雨
cm.teachSkill(3111005,30,30);//银鹰召唤
cm.teachSkill(3111006,30,30);//箭扫射
//神射手
cm.teachSkill(3120005,30,30);//神箭手
cm.teachSkill(3121000,30,30);//冒险岛勇士
cm.teachSkill(3121002,30,30);//火眼晶晶
cm.teachSkill(3121003,30,30);//飞龙冲击波
cm.teachSkill(3121004,30,30);//暴风箭雨
cm.teachSkill(3121006,30,30);//火凤凰
cm.teachSkill(3121007,30,30);//击退箭
cm.teachSkill(3121008,30,30);//集中精力
cm.teachSkill(3121009,5,5);//勇士的意志
cm.sendOk("已经成功学习技能!");
			cm.dispose();
}else{
			cm.sendOk("当前职业不符合学习要求。")
			cm.dispose();
}


//=====================无影人、隐士
}else if (selection == 9){
if (cm.getJob().getId() == 411 || cm.getJob().getId() == 412){
//无影人
cm.teachSkill(4110000,20,20);//药剂精通
cm.teachSkill(4111001,20,20);//聚财术
cm.teachSkill(4111002,30,30);//影分身
cm.teachSkill(4111003,20,20);//影网术
cm.teachSkill(4111004,30,30);//金钱攻击
cm.teachSkill(4111005,30,30);//多重飞镖
cm.teachSkill(4111006,20,20);//二段跳
//隐士
cm.teachSkill(4120002,30,30);//假动作
cm.teachSkill(4120005,30,30);//武器用毒液
cm.teachSkill(4121000,30,30);//冒险岛勇士
cm.teachSkill(4121003,30,30);//挑衅
cm.teachSkill(4121004,30,30);//忍者伏击
cm.teachSkill(4121006,30,30);//暗器伤人
cm.teachSkill(4121007,30,30);//三连环光击破
cm.teachSkill(4121008,30,30);//忍者冲击
cm.teachSkill(3221008,5,5);//勇士的意志
cm.sendOk("已经成功学习技能!");
			cm.dispose();
}else{
			cm.sendOk("当前职业不符合学习要求。")
			cm.dispose();
}


//=====================侠盗、独行客
}else if (selection == 10){
if (cm.getJob().getId() == 421 || cm.getJob().getId() == 422){
//独行客
cm.teachSkill(4210000,20,20);//盾防精通
cm.teachSkill(4211001,30,30);//转化术
cm.teachSkill(4211002,30,30);//落叶斩
cm.teachSkill(4211003,20,20);//敛财术
cm.teachSkill(4211004,30,30);//分身术
cm.teachSkill(4211005,20,20);//金钱护盾
cm.teachSkill(4211006,30,30);//金钱炸弹
//侠盗
cm.teachSkill(4220002,30,30);//假动作
cm.teachSkill(4220005,30,30);//武器用毒液
cm.teachSkill(4221000,30,30);//冒险岛勇士
cm.teachSkill(4221001,30,30);//暗杀
cm.teachSkill(4221003,30,30);//挑衅
cm.teachSkill(4221004,30,30);//忍者伏击
cm.teachSkill(4221006,30,30);//烟幕弹
cm.teachSkill(4221007,30,30);//一出双击
cm.teachSkill(3221008,5,5);//勇士的意志
cm.sendOk("已经成功学习技能!");
			cm.dispose();
}else{
			cm.sendOk("当前职业不符合学习要求。")
			cm.dispose();
}

/*
//=====================大幅、船长
}else if (selection == 11){
if (cm.getJob().getId() == 521 || cm.getJob().getId() == 522){
//大幅
cm.teachSkill(5210000,20,20);//三连射杀
cm.teachSkill(5211001,30,30);//章鱼炮台
cm.teachSkill(5211002,30,30);//海鸥空袭
cm.teachSkill(5211004,30,30);//烈焰喷射
cm.teachSkill(5211005,30,30);//寒冰喷射
cm.teachSkill(5211006,30,30);//导航
//船长
cm.teachSkill(5220001,30,30);//属性强化
cm.teachSkill(5220002,20,20);//超级章鱼炮台
cm.teachSkill(5220003,30,30);//地毯式空袭
cm.teachSkill(5220004,30,30);//金属风暴
cm.teachSkill(5220006,10,10);//武装
cm.teachSkill(5220007,30,30);//急速射
cm.teachSkill(5220008,30,30);//重量炮击
cm.teachSkill(5220009,20,20);//心灵控制
cm.teachSkill(5220010,5,5);//勇士的意志
cm.sendOk("已经成功学习技能!");
			cm.dispose();
}else{
			cm.sendOk("当前职业不符合学习要求。")
			cm.dispose();
}
*/
}else if (selection == 11){
cm.sendOk("此职业有BUG，暂时关闭！");
			cm.dispose();


//=====================斗士、冲锋队长
}else if (selection == 12){
if (cm.getJob().getId() == 511 || cm.getJob().getId() == 512){
//斗士
cm.teachSkill(5110000,20,20);//迷惑攻击
cm.teachSkill(5110001,40,40);//能量获得
cm.teachSkill(5111002,30,30);//能量爆破
cm.teachSkill(5111004,20,20);//能量耗转
cm.teachSkill(5111005,20,20);//超人变形
cm.teachSkill(5111006,30,30);//碎石乱击
//冲锋队长
cm.teachSkill(5121000,30,30);//冒险岛勇士
cm.teachSkill(5121001,30,30);//潜龙出渊
cm.teachSkill(5121002,30,30);//超能量
cm.teachSkill(5121003,30,30);//超级变身
cm.teachSkill(5121004,30,30);//金手指
cm.teachSkill(5121005,30,30);//索命
cm.teachSkill(5121007,30,30);//光速拳
cm.teachSkill(5121009,20,20);//极速领域
cm.teachSkill(5121010,30,30);//伺机待发
cm.teachSkill(5121008,5,5);//勇士的意志
cm.sendOk("已经成功学习技能!");
			cm.dispose();
}else{
			cm.sendOk("当前职业不符合学习要求。")
			cm.dispose();
}


//=====================魂骑士
}else if (selection == 13){
if (cm.getJob().getId() == 1111){
//魂骑士
cm.teachSkill(11110000,20,20);//魔力恢复
cm.teachSkill(11111001,20,20);//斗气集中
cm.teachSkill(11111002,20,20);//恐慌
cm.teachSkill(11111003,20,20);//昏迷
cm.teachSkill(11111004,30,30);//轻舞飞扬
cm.teachSkill(11110005,20,20);//进阶斗气
cm.teachSkill(11111006,20,20);//灵魂突刺
cm.teachSkill(11111007,20,20);//灵魂属性
cm.sendOk("已经成功学习技能!");
			cm.dispose();
}else{
			cm.sendOk("当前职业不符合学习要求。")
			cm.dispose();
}


//=====================炎术士
}else if (selection == 14){
if (cm.getJob().getId() == 1211){
//炎术士
cm.teachSkill(12110000,20,20);//魔法抗性
cm.teachSkill(12110001,20,20);//魔力激化
cm.teachSkill(12111002,20,20);//封印术
cm.teachSkill(12111003,20,20);//天降落星
cm.teachSkill(12111004,20,20);//火魔兽
cm.teachSkill(12111005,30,30);//火牢术屏障
cm.teachSkill(12111006,30,30);//火风暴
cm.sendOk("已经成功学习技能!");
			cm.dispose();
}else{
			cm.sendOk("当前职业不符合学习要求。")
			cm.dispose();
}


//=====================风灵使者
}else if (selection == 15){
if (cm.getJob().getId() == 1311){
//风灵使者
cm.teachSkill(13111000,20,20);//箭雨
cm.teachSkill(13111001,30,30);//箭扫射
cm.teachSkill(13111002,20,20);//暴风箭雨
cm.teachSkill(13110003,20,20);//神箭手
cm.teachSkill(13111004,20,20);//替身术
cm.teachSkill(13111005,10,10);//信天翁
cm.teachSkill(13111006,20,20);//风灵穿越
cm.teachSkill(13111007,20,20);//疾风扫射
cm.sendOk("已经成功学习技能!");
			cm.dispose();
}else{
			cm.sendOk("当前职业不符合学习要求。")
			cm.dispose();
}


//=====================夜行者
}else if (selection == 16){
if (cm.getJob().getId() == 1411){
//夜行者
cm.teachSkill(14111000,30,30);//影分身
cm.teachSkill(14111001,20,20);//影网术
cm.teachSkill(14111002,30,30);//多重飞镖
cm.teachSkill(14110003,20,20);//药剂精通
cm.teachSkill(14110004,20,20);//武器用毒液
cm.teachSkill(14111005,20,20);//三连环光击破
cm.teachSkill(14111006,30,30);//毒炸弹
cm.sendOk("已经成功学习技能!");
			cm.dispose();
}else{
			cm.sendOk("当前职业不符合学习要求。")
			cm.dispose();
}


}else if (selection == 17){
if (cm.getJob().getId() == 2100 || cm.getJob().getId() == 2110 || cm.getJob().getId() == 2111 || cm.getJob().getId() == 2112){
cm.teachSkill(21000000,10,10);
cm.teachSkill(21001001,15,15);
cm.teachSkill(21000002,20,20);
cm.teachSkill(21001003,20,20);
cm.teachSkill(21100000,20,20);
cm.teachSkill(21100002,30,30);
cm.teachSkill(21101003,20,20);
cm.teachSkill(21100004,20,20);
cm.teachSkill(21100005,20,20);
cm.teachSkill(21110000,20,20);
cm.teachSkill(21111001,20,20);
cm.teachSkill(21110002,20,20);
cm.teachSkill(21110003,30,30);
cm.teachSkill(21110004,30,30);
cm.teachSkill(21111005,20,20);
cm.teachSkill(21110006,20,20);
cm.teachSkill(21121000,30,30);
cm.teachSkill(21120001,30,30);
cm.teachSkill(21120002,30,30);
cm.teachSkill(21121003,20,20);
cm.teachSkill(21120004,30,30);
cm.teachSkill(21120005,30,30);
cm.teachSkill(21120006,30,30);
cm.teachSkill(21120007,30,30);
cm.sendOk("已经成功为你加满战神技能!");
cm.dispose();
}else{
			cm.sendOk("对不起，你不是战神，不能使用此功能。")
			cm.dispose();
}


}else if(selection == 18){
if (cm.getJob().getId() == 430 || cm.getJob().getId() == 431 || cm.getJob().getId() == 432|| cm.getJob().getId() == 433 || cm.getJob().getId() == 434){
cm.teachSkill(4300000,10,20);
cm.teachSkill(4301001,10,10);
cm.teachSkill(4301002,20,20);
cm.teachSkill(4310000,20,20);
cm.teachSkill(4311001,20,20);
cm.teachSkill(4311002,20,20);
cm.teachSkill(4311003,20,20);
cm.teachSkill(4321000,20,20);
cm.teachSkill(4321001,20,20);
cm.teachSkill(4321002,20,20);
cm.teachSkill(4321003,20,20);
cm.teachSkill(4331000,10,10);
cm.teachSkill(4330001,20,20);
cm.teachSkill(4331002,30,30);
cm.teachSkill(4331003,20,20);
cm.teachSkill(4331004,20,20);
cm.teachSkill(4331005,20,20);
cm.teachSkill(4341000,30,30);
cm.teachSkill(4340001,30,30);
cm.teachSkill(4341002,30,30);
cm.teachSkill(4341003,30,30);
cm.teachSkill(4341004,30,30);
cm.teachSkill(4341005,30,30);
cm.teachSkill(4341006,30,30);
cm.teachSkill(4341008,5,5);
cm.sendOk("已经成功为你加满暗影双刀技能!");
cm.dispose();
		}else{
			cm.sendOk("对不起，你不是暗影双刀，不能使用此功能。")
			cm.dispose();
}

}else if(selection == 19){
cm.teachSkill(1004,1,1);
cm.teachSkill(10001004,1,1);
cm.teachSkill(20001004,1,1);
cm.teachSkill(20011004,1,1);
cm.sendOk("已经成功为你添加骑宠技能!");
cm.dispose();

}else if(selection == 20){
cm.teachSkill(1004,1,1);
cm.teachSkill(10001004,1,1);
cm.teachSkill(20001004,1,1);
cm.teachSkill(20011004,1,1);
cm.sendOk("已经成功为你添加群宠技能!");
cm.dispose();

}//selection		
		}
	}
}
