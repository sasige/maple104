var status = 0;

var hwtext=new Array("人长得漂亮不如活得漂亮！","当裤子失去皮带，才懂得什麽叫做依赖。","烟不听话，所以我们'抽烟'。","你发怒一分钟，便失去60秒的幸福。","当男人遇见女人，从此只有纪念日，没有独立日。","路见不平一声吼，吼完继续往前走。","幸福是个比较级，要有东西垫底才感觉得到。","知识就像内裤，看不见但很重要","作为失败的典型，你实在是太成功了","女人喜欢长得坏坏的男人，不是喜欢长坏了的男人","跌倒了，爬起来再哭","你若流泪，先湿的是我的心","让未来到来，让过去过去","我自横刀向天笑，笑完之后去睡觉","别跟我谈感情，谈感情伤钱","孤单是一个人的狂欢，狂欢是一群人的孤单","姐不是收破烂的，做不到让你随喊随到","我不是草船，你的贱别往我这发","你的矮是终身的，我的胖却是暂时的","別在无聊的时候來找我，不然显得我是多余的","姐不是电视机，不要老是盯着姐看","即使你已名花有主、我也要移花接木","心里只有你一个频道 最可恨的是还没有广告","给你最大的报复，就是活的比你幸福","要不是老师说不能乱扔垃圾，不然我早把你扔出去","没有癞蛤蟆，天鹅也会寂寞","我是光棍我可耻，我给国家浪费纸","人生没有如果，只有后果和结果","你那么有钱 为什么不让鬼来推磨？","别把人和狗相提并论，狗最起码忠诚","生活嘛，就是生下来，活下去","当你披上了婚纱 我也披上了袈裟","趁着年轻把能干的坏事都干了吧，没几年了","我人生只会两件事 1 这也不会 2 那也不会","出租车司机，司机中的战斗机，噢耶! ","思想有多远，你就给我滚多远!","人生最大的悲哀是青春不在,青春痘却还在。","最简单的长寿秘决:保持呼吸，不要断气~","打死我也不说，你们还没使美人计呢!","不要和我比懒,我懒得和你比","我不是个随便的人 我随便起来不是人","不怕虎一样的敌人，就怕猪一样的队友","老虎不发威 你当我是HELLO KITTY！","吃自助最高境界：扶墙进，扶墙出。","爷爷都是从孙子走过来的……","夏天就是不好，穷的时候我连西北风都没得喝","没什么事就不要找我，有事了更不要找我。","我想早恋，可是已经晚了……","钱可以解决的问题都不是问题。","天哪，我的衣服又瘦了！","不吃饱哪有力气减肥啊？","连广告也信，读书读傻了吧？","人怕出名猪怕壮，男怕没钱女怕胖。","如果有钱也是一种错，我情愿一错再错","命运负责洗牌，但是玩牌的是我们自己！","好好活着，因为我们会死很久!","人又不聪明，还学人家秃顶！","我总在牛a与牛c之间徘徊。","不怕被人利用，就怕你没用。","鄙视我的人这么多，你算老几? ","秀发去无踪，头屑更出众！","春色满园关不住，我诱红杏出墙来。","问世间情为何物？一物降一物","bmw是别摸我，msn是摸死你","女为悦己者容,男为悦己者穷！ ","念了十几年书，还是幼儿园比较好混");

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (status == 0 && mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
    if (status == 0) {
	var hwchance= Math.floor(Math.random()*hwtext.length);
        var selStr = "#r幽默时刻:"+hwtext[hwchance]+"#k\r\n";
        selStr += "#L0##fEffect/CharacterEff/1112905/0/1##d快速转职#l#L1##fEffect/CharacterEff/1112905/0/1#万能传送#l#L3##fEffect/CharacterEff/1112905/0/1#游戏商店#l#L2##fEffect/CharacterEff/1112905/0/1#美容美发#l\r\n";
        selStr += "#L4##fEffect/CharacterEff/1112905/0/1#百宝抽奖#l#L14##fEffect/CharacterEff/1112905/0/1#游戏排名#l#L28##fEffect/CharacterEff/1112905/0/1#消费币购买点卷#l\r\n";
        selStr += "#L6##fEffect/CharacterEff/1112905/0/1#仓库管理#l#L23##fEffect/CharacterEff/1112905/0/1#属性重置#l#L8##fEffect/CharacterEff/1112905/0/1#学习技能#l#L10##fEffect/CharacterEff/1112905/0/1#银行存款#l\r\n";//#L7#点卷中介#l
        selStr += "#L11##r#fEffect/CharacterEff/1112905/0/1#等级送礼#l#L25##r#fEffect/CharacterEff/1112905/0/1#转生送礼#l#L26##r#fEffect/CharacterEff/1112905/0/1#充值消费#l#L27##r#fEffect/CharacterEff/1112905/0/1#本服官网#l\r\n";//#L17##b出租商店#l
        selStr += "#L21##r#fEffect/CharacterEff/1112905/0/1#充值奖励#l#L22##r#fEffect/CharacterEff/1112905/0/1#超级转生#k#l#L24##r#fEffect/CharacterEff/1112905/0/1#领取骑宠技能#l\r\n";
        selStr += "#L18##b#fEffect/CharacterEff/1112905/0/1##e本服特色结婚，离婚(闪婚神马都是浮云~~)#n#l\r\n";
        selStr += "#L13##b#fEffect/CharacterEff/1112905/0/1##e远征队BOSS挑战，(组队才好玩，无兄弟不游戏~)#n#l";
        //selStr += "#L20##r7永恒的雪花兑换[New]  #L19##r7周年隆重开放[New]";
        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
        case 0:
            cm.dispose();
            cm.openNpc(9300011);
            break;
        case 1:
            cm.dispose();
            cm.openNpc(9900002, 22);
            break;
        case 2:
            cm.dispose();
            //cm.warp(100000104);
            cm.openNpc(9900002, 24);
            break;
        case 3:
            cm.dispose();
            cm.openNpc(1012121);
            break;
        case 4:
            cm.dispose();
            cm.warp(749050400);
            break;
        case 5:
            cm.dispose();
            cm.openNpc(9900002, 2);
            break;
        case 6:
            cm.dispose();
            cm.openNpc(9030100);
            break;
        case 7:
            cm.dispose();
            cm.openNpc(9900002, 8);
            break;
        case 8:
            cm.dispose();
            cm.openNpc(9900002, 23);
            break;
        case 9:
            cm.dispose();
            cm.openShop(2060003);
            break;
        case 10:
            cm.dispose();
            cm.openNpc(9900002, 5);
            break;
        case 11:
            cm.dispose();
            cm.openNpc(9900002, 12);
            break;
        case 12:
            cm.dispose();
            cm.openNpc(9000086);
            break;
        case 13:
            cm.dispose();
            cm.openNpc(9900002, 1);
            break;
        case 14:
            cm.dispose();
            cm.openNpc(9040008);
            break;
        case 15:
            cm.dispose();
            cm.openNpc(9310058);
            break;
        case 16:
            cm.dispose();
            cm.openNpc(9900004, 3);
            break;
        case 17:
            cm.dispose();
            cm.openNpc(9900004, 4);
            break;
        case 18:
            cm.dispose();
            if (cm.getMapId() == 700000000) {
                cm.sendOk("你已经在结婚地图了.");
            } else {
                cm.warp(700000000);
                cm.sendOk("已经将你传送到结婚地图。\r\n结婚和盛大一样.\r\n请带上你的爱人.邀请你的朋友来吧!\r\n祝你新婚快乐!!!");
                break;
            }
        case 19:
            cm.dispose();
            cm.openNpc(9120033);
            break;
        case 20:
            if (cm.getChar().getId() > 1100) {
                cm.sendOk("不好意思你不是老玩家不能补领");
                cm.dispose();
            } else if (cm.getBossLog("玩家补领",1) == 1) {
                cm.sendOk("你已经补领过了");
                cm.dispose();
            } else if (!cm.haveItem(1052081)) {
                cm.sendOk("请把#v1052170#放在背包");
                cm.dispose();
            } else if (!cm.haveItem(1002562)) {
                cm.sendOk("请把#v1002562#放在背包");
                cm.dispose();
            } else if (!cm.haveItem(1012265)) {
                cm.sendOk("请把#v1002562#放在背包");
                cm.dispose();
            } else {
                cm.gainItem(1052081, -1); 
                cm.gainItem(1002562, -1); 
                cm.gainItem(1012265, -1); 
                var ii = Packages.server.MapleItemInformationProvider.getInstance();
                var type = Packages.constants.GameConstants.getInventoryType(1002562); //VIP勋章
                var toDrop = ii.randomizeStats(ii.getEquipById(1002562)).copy(); // 生成一个Equip类
                toDrop.setStr(2000); //装备力量
                toDrop.setDex(2000); //装备敏捷
                toDrop.setInt(2000); //装备智力
                toDrop.setLuk(2000); //装备运气
                toDrop.setMatk(2000); //物理攻击
                toDrop.setWatk(2000); //魔法攻击
                cm.getPlayer().getInventory(type).addItem(toDrop); //将这个装备放入包中
                cm.getC().getSession().write(Packages.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包
            if (cm.getPlayer().getGender() == 0) {
                cm.gainItem(1000051, 1); //发带(男)
                cm.gainItem(3010321, 1); //椅子(男)
            } else {
                cm.gainItem(1001077, 1); //发带(女)
                cm.gainItem(3010320, 1); //椅子(女)
            }
                cm.setBossLog("玩家补领", 1);
                cm.sendOk("补领成功");
                cm.dispose();
            }
            break;
        case 21:
            cm.dispose();
            cm.openNpc(9900002, 17);
            break;
        case 22:
            cm.dispose();
            cm.openNpc(9900002, 20);
            break;
	case 23:
            cm.dispose();
            cm.openNpc(9900002, 26);
            break;
	case 25:
            cm.dispose();
            cm.openNpc(9900002, 30);
            break;
	case 28:
            cm.dispose();
            cm.openNpc(9900002, 7);
            break;
        case 26:
            cm.dispose();
            cm.openWeb("http://www.libaopay.com/buy/?wid=17265");
            break;
        case 27:
            cm.dispose();
            cm.openWeb("http://www.baidu.com/");
            break;
	case 24:
		cm.teachSkill(80001030,1,1);//走路鸡
		cm.teachSkill(80001015,1,1);//鸵鸟     
            	cm.sendOk("骑宠技能领取完毕！");
           	cm.dispose();
        }
    }
}