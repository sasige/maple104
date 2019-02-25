var status = 0;
var typede = 0;
var eff = "#fEffect/CharacterEff/1112905/0/1#";
var hwtext = new Array("人长得漂亮不如活得漂亮！", "当裤子失去皮带，才懂得什麽叫做依赖。", "烟不听话，所以我们'抽烟'。", "你发怒一分钟，便失去60秒的幸福。", "当男人遇见女人，从此只有纪念日，没有独立日。", "路见不平一声吼，吼完继续往前走。", "幸福是个比较级，要有东西垫底才感觉得到。", "知识就像内裤，看不见但很重要", "作为失败的典型，你实在是太成功了", "女人喜欢长得坏坏的男人，不是喜欢长坏了的男人", "跌倒了，爬起来再哭", "你若流泪，先湿的是我的心", "让未来到来，让过去过去", "我自横刀向天笑，笑完之后去睡觉", "别跟我谈感情，谈感情伤钱", "孤单是一个人的狂欢，狂欢是一群人的孤单", "姐不是收破烂的，做不到让你随喊随到", "我不是草船，你的贱别往我这发", "你的矮是终身的，我的胖却是暂时的", "別在无聊的时候來找我，不然显得我是多余的", "姐不是电视机，不要老是盯着姐看", "即使你已名花有主、我也要移花接木", "心里只有你一个频道 最可恨的是还没有广告", "给你最大的报复，就是活的比你幸福", "要不是老师说不能乱扔垃圾，不然我早把你扔出去", "没有癞蛤蟆，天鹅也会寂寞", "我是光棍我可耻，我给国家浪费纸", "人生没有如果，只有后果和结果", "你那么有钱 为什么不让鬼来推磨？", "别把人和狗相提并论，狗最起码忠诚", "生活嘛，就是生下来，活下去", "当你披上了婚纱 我也披上了袈裟", "趁着年轻把能干的坏事都干了吧，没几年了", "我人生只会两件事 1 这也不会 2 那也不会", "出租车司机，司机中的战斗机，噢耶! ", "思想有多远，你就给我滚多远!", "人生最大的悲哀是青春不在,青春痘却还在。", "最简单的长寿秘决:保持呼吸，不要断气~", "打死我也不说，你们还没使美人计呢!", "不要和我比懒,我懒得和你比", "我不是个随便的人 我随便起来不是人", "不怕虎一样的敌人，就怕猪一样的队友", "老虎不发威 你当我是HELLO KITTY！", "吃自助最高境界：扶墙进，扶墙出。", "爷爷都是从孙子走过来的……", "夏天就是不好，穷的时候我连西北风都没得喝", "没什么事就不要找我，有事了更不要找我。", "我想早恋，可是已经晚了……", "钱可以解决的问题都不是问题。", "天哪，我的衣服又瘦了！", "不吃饱哪有力气减肥啊？", "连广告也信，读书读傻了吧？", "人怕出名猪怕壮，男怕没钱女怕胖。", "如果有钱也是一种错，我情愿一错再错", "命运负责洗牌，但是玩牌的是我们自己！", "好好活着，因为我们会死很久!", "人又不聪明，还学人家秃顶！", "我总在牛a与牛c之间徘徊。", "不怕被人利用，就怕你没用。", "鄙视我的人这么多，你算老几? ", "秀发去无踪，头屑更出众！", "春色满园关不住，我诱红杏出墙来。", "问世间情为何物？一物降一物", "bmw是别摸我，msn是摸死你", "女为悦己者容,男为悦己者穷！ ", "念了十几年书，还是幼儿园比较好混");
var psrw2 = new Array(2040506, 2040806, 2040807, 2043303, 2043203, 2043103, 2043703, 2043803, 2044003, 2044103, 2044203, 2044303, 2044403, 2044603, 2044703, 2040807, 2044908);
var rand2 = Math.floor(Math.random() * psrw2.length);
var itemtsid = new Array("1099003", "1532014", "1532014", "1532014", "1532014", "1532014", "1402073", "1462076", "1452058", "1302081", "1312037", "1322060", "1332073", "1332074", "1372044", "1382057", "1402046", "1412033", "1422037", "1432047", "1442063", "1452057", "1462050", "1472068", "1482023", "1492023", "1302059", "1312031", "1322052", "1402036", "1412026", "1422028", "1432038", "1442045", "1452044", "1462039", "1472051", "1472052", "1332050", "1302086", "1312038", "1322061", "1332075", "1332076", "1372045", "1382059", "1402047", "1412034", "1422038", "1432049", "1442067", "1452059", "1462051", "1472071", "1482024", "1492025", "1032025", "1032026", "1032027", "1032028", "1032035", "1032040", "1032047", "1002391", "1002419", "1002436", "1002455", "1002773", "1002794", "1522012", "1522056", "1362020");
var randtsid = Math.floor(Math.random() * itemtsid.length);

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1) status++;
        if (status == 0) {
            if (cm.getMapId() == 910023100 || cm.getMapId() == 910023400) {
                cm.sendSimple("征服BOSS了吗?青年\r\n#L0#我要离开");
            } else {
                var text = "";
                if (cm.getChannelServer().getChannel() == 1) {
                    var hwchance = Math.floor(Math.random() * hwtext.length);
                    text += "#r幽默时刻:" + hwtext[hwchance] + "#k\r\n#L1##b1.使用5个黄金枫叶兑换5个神奇魔方#l\r\n#L2#2.使用10个黄金枫叶抽取必成卷(100%成功)\r\n#L3#3.使用20个黄金枫叶抽取骑宠技能(超级拉风)\r\n#L4#4.使用黄金枫叶+彩虹枫叶换取绝版玩具装备\r\n#L5##r5.使用20个黄金枫叶为武器增加属性#l\r\n#L6#6.使用30个黄金枫叶抽取全属性装备#l";
                    cm.sendSimpleS(text,2);
                } else {
                    text += "亲,我只能在1频道为你提供服务哟!";
                    cm.sendOk(text);
                    cm.dispose();
                }

            }
        } else if (status == 1) {
            if (selection == 0) { //存在副本中就退出
                cm.warp(910000000, 0);
                cm.dispose();
            } else if (selection == 11) { //攻打电钻老巢
                cm.sendOk("剧情正在整理中..稍后开放");
                cm.dispose();
            } else if (selection == 12) { //拯救阿斯旺
                cm.sendOk("剧情正在整理中..稍后开放");
                cm.dispose();
            } else if (selection == 13) { //击退海盗
                typede = 13;
                cm.sendNext("我正在等待勇敢的冒险家。请大家用自己的力量和智慧，一起破解难题，击退强大的#r海盗军团#k\r\n\r\n - #e等级#n：70级以上#r（推荐等级：120～200 ）#k\r\n - #e开放时间#n：整点2分-5分\r\n - #e限制时间#n：10分钟\r\n - #e参加人数#n：3～4人\r\n - #e通关获得物品#n：\r\n　#v4031996##v4031995##v4031994v# 蘑菇奖牌#b（随机获得）#k\r\n - #e通关随机物品#n：\r\n　 #b蓝蜗牛邮票,GM必成卷轴,超级药水,外星装备(有孔),圣杯");
            } else if (selection == 14) { //限时夺宝
                typede = 14;
                cm.sendNext("服务器时间:#e" + cm.getHour() + "点" + cm.getMin() + "分" + cm.getSec() + "秒#n\r\n限时夺宝，每个小时只开放一场，每小时开放时间为20分-21分，所以要抓紧时间哟，进入夺宝后你需要在规定的时间内击破金蛋，才能获得奖励，此夺宝无等级物品限制，所以要抓紧时间哟！");
            } else if (selection == 15) { //粉色扎昆
                typede = 15;
                cm.sendNext("我正在等待勇敢的冒险家。请大家用自己的力量和智慧，一起破解难题，击退强大的#r粉色扎昆#k\r\n\r\n - #e等级#n：70级以上#r（推荐等级：120～200 ）#k\r\n - #e开放时间#n：整点35分-55分\r\n - #e限制时间#n：10分钟\r\n - #e参加人数#n：3～4人\r\n - #e通关获得物品#n：\r\n　#v4033220# #z4033220##b（制作图腾的核心）#k\r\n - #e通关随机物品#n：\r\n　 #v3010313##v3010203##v3010024##v1003439#");
            } else if (selection == 3) { //与神共舞
                cm.sendOk("GM还在整理中。。稍后开放");
                cm.dispose();
            } else if (selection == 2) { //斩杀魔帝
                var ii = Packages.server.MapleItemInformationProvider.getInstance();
                if (cm.haveItem(4000313,10)) {
                    cm.gainItem(4000313, -10); 
                    cm.gainItem(psrw2[rand2], +1); 
                    cm.sendOk("换取成功，成功换取到#v" + psrw2[rand2] + "#一张");
                    cm.worldMessage("玩家[" + cm.getPlayer().getName() + "]在剧情副本[魔女塔]处抽到了一张" + ii.getName(psrw2[rand2]) + "~");
                    cm.dispose();
                } else {
                    cm.sendOk("#v4000313##z4000313#不足，要继续努力哟");
                    cm.dispose();
                }
            } else if (selection == 1) { //干掉上帝
                if (cm.haveItem(4000313,5)) {
                    cm.gainItem(4000313, -5); 
                    cm.gainItem(5062000, +5); 
                    cm.sendOk("换取成功，成功换取到#v5062000#x5");
                    cm.dispose();
                } else {
                    cm.sendOk("#v4000313##z4000313#不足，要继续努力哟");
                    cm.dispose();
                }
            } else if (selection == 4) { //挑战春哥
                cm.sendOk("GM还在整理中。。稍后开放");
                cm.dispose();
            } else if (selection == 5) { //简单蜈蚣
                cm.sendOk("GM还在整理中。。稍后开放");
                cm.dispose();
            } else if (selection == 6) { //简单人马
                if (cm.haveItem(4000313, 30)) {
                    cm.gainItem(4000313, -30); 
                    randxlslot = Math.floor(Math.random() * 100) + 100;
                    var ii = Packages.server.MapleItemInformationProvider.getInstance();
                    var type = Packages.constants.GameConstants.getInventoryType(itemtsid[randtsid]); //帽子
                    var toDrop = ii.randomizeStats(ii.getEquipById(itemtsid[randtsid])).copy(); // 生成一个Equip类
                    toDrop.setStr(randxlslot); //装备力量
                    toDrop.setDex(randxlslot); //装备敏捷
                    toDrop.setInt(randxlslot); //装备智力
                    toDrop.setLuk(randxlslot); //装备运气
                    toDrop.setMatk(randxlslot);
                    toDrop.setWatk(randxlslot);
                    cm.getPlayer().getInventory(type).addItem(toDrop); //将这个装备放入包中
                    cm.getC().getSession().write(Packages.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背                      cm.worldMessage("玩家[" + cm.getPlayer().getName() + "]通过了剧情副本[魔女塔]使用黄金枫叶兑换到全属性+" + randxlslot + "的" + ii.getName(itemtsid[randtsid]) + "~");
                    cm.sendOk("恭喜您,获得 #b全属性+" + randxlslot + "#k 的 #r#z" + itemtsid[randtsid] + "#.");
                    cm.dispose();
                } else {
                    cm.sendOk("#v4000313##z4000313#不足，要继续努力哟");
                    cm.dispose();
                }
            } else if (selection == 7) { //普通幽灵船
                typede = 7;
                cm.sendNext("幽灵船的船长-船长等待你的挑战！他拥有超厚的血量，超强的防御，致命的打击，身为新手的你，能否对付他呢？\r\n#b该副本针对#r10-50#b转玩家开放，为普通模式。进入地图后，你有5分钟的时间来杀死#e船长#n，杀死船长将会获得[#r邮票、修为点、130高级武器#b其中一种]。都是随便出的哦！\r\n\r\n玩家每日可以进入2次，2次后需加1000消费币(VIP5会员直接进入)\r\n#r#e必须独孤不组队才能进入#n#b！#b是否想体验一下呢？");
            } else if (selection == 8) { //普通天鹰
                typede = 8;
                cm.sendNext("天鹰洞的天鹰-天鹰等待你的挑战！他拥有超厚的血量，超强的防御，致命的打击，身为新手的你，能否对付他呢？\r\n#b该副本针对#r10-50#b转玩家开放，为普通模式。进入地图后，你有5分钟的时间来杀死#e天鹰#n，杀死天鹰将会获得[#r邮票、修为点、130高级武器#b其中一种]。都是随便出的哦！\r\n\r\n玩家每日可以进入2次，2次后需加1000消费币(VIP5会员直接进入)\r\b#r#e必须独孤不组队才能进入#n#b！#b是否想体验一下呢？");
            } else if (selection == 9) { //困难欧拉拉
                typede = 9;
                cm.sendNext("未来世界的欧拉拉-欧拉拉等待你的挑战！他拥有超厚的血量，超强的防御，致命的打击，身为老手的你，能否对付他呢？\r\n#b该副本针对#r50-200#b转玩家开放，为困难模式。进入地图后，你有5分钟的时间来杀死#e欧拉拉#n\r\n-副本奖励：\r\n[#r邮票、修为点、130高级武器、C级星岩石#b其中一种]。都是随便出的哦！\r\n-副本要求：\r\n#r每日每人可进入2次。2次后需加1000消费币(VIP5会员直接进入)");
            } else if (selection == 10) { //困难御龙魔
                typede = 10;
                cm.sendNext("未来世界的御龙魔-欧拉拉御龙魔等待你的挑战！他拥有超厚的血量，超强的防御，致命的打击，身为老手的你，能否对付他呢？\r\n#b该副本针对#r50-200#b转玩家开放，为困难模式。进入地图后，你有5分钟的时间来杀死#e御龙魔#n\r\n-副本奖励：\r\n[#r邮票、修为点、130高级武器、C级星岩石#b其中一种]。都是随便出的哦！\r\n-副本要求：\r\n#r每日每人可进入2次。2次后需加1000消费币(VIP5会员直接进入)");
            } else if (selection == 25) { //娱乐0
                typede = 25;
                cm.sendNext("神马是娱乐级?那就让我来给你们说说吧!哼哼...就是没任何限制,只要在规定的时间内便可轻松进入!里面有N种怪物供你挑战.那我能获得什么呢?规定#b1分钟#k时间内成功挑战一次能获得#r冒险岛宝石系列#k任意装备一件!你是否想试试呢?");
            } else if (selection == 26) { //娱乐1
                typede = 26;
                cm.sendNext("神马是娱乐级?那就让我来给你们说说吧!哼哼...就是没任何限制,只要在规定的时间内便可轻松进入!里面有N种怪物供你挑战.那我能获得什么呢?规定#b1分钟#k时间内成功挑战一次能获得#r冒险岛铂金系列#k任意装备一件!你是否想试试呢?");
            } else if (selection == 27) { //娱乐2
                typede = 27;
                cm.sendNext("神马是娱乐级?那就让我来给你们说说吧!哼哼...就是没任何限制,只要在规定的时间内便可轻松进入!里面有N种怪物供你挑战.那我能获得什么呢?规定#b1分钟#k时间内成功挑战一次能获得#r传说系列#k任意装备一件!你是否想试试呢?");

            } else if (selection == 35) { //蜗牛王的邮票
                typede = 35;
                cm.sendNext("据说蜗牛爷爷收集了许多的邮票,但是它不愿意给你.那么我们只好去偷啦,不过被它发现了,那么我们只好打败它,才能顺利拿回邮票.现在你有#r60#k秒的时间来打败它并且带着邮票逃走,如果未能打败它,那么你就会损失#r30#k修为点,你敢去尝试下吗?");
            } else if (selection == 36) { //英雄救美
                typede = 36;
                cm.sendNext("美丽的公主被蘑菇大臣抢走了,快去拯救她吧,说不定她会以身相许哟!打败蘑菇大臣,并把#z4001318#交给公主,你会有意想不到的惊喜.如果未能打败它,那么你就会损失#r100#k修为点,你敢去尝试下吗?当然进去的时候还需要支付#r300#k修为点");
            }

        } else if (status == 2) {
            if (typede == 1) { //与神共舞
                if (cm.getParty() == null) { // No Party
                    cm.sendOk("需要先#b开启#k一个组队,而且只能是你一个人~.zzzZZZZZ..");
                    cm.dispose();
                    return;
                } else if (!cm.isLeader()) { // Not Party Leader
                    cm.sendOk("请叫队长跟我说话.");
                    cm.dispose();
                    return;
                } else if (cm.getMap(980041000).getCharactersSize() > 0 || cm.getMap(980041100).getCharactersSize() > 0 || cm.getMap(980041200).getCharactersSize() > 0) { // Not Party Leader
                    cm.sendOk("有人在挑战此副本，请稍等一会");
                    cm.dispose();
                    return;
                } else {
                    var party = cm.getParty().getMembers();
                    if (party.size() > 1) {
                        cm.sendOk("#r对不起,为了彻底的测试你的能力,只能一人前往..");
                        cm.dispose();
                        return;
                    }
                    var dh = cm.getEventManager("WitchTower_EASY");
                    if (dh == null) {
                        cm.sendOk("此副本出错啦,请联系管管修复吧.");
                        cm.dispose();
                        return;
                    }
                    if (cm.getChar().getVip() >= 6) {
                        cm.worldMessage("【" + cm.getChar().getName() + "】开始挑战神级副本『与神共舞』~我们一起期待光荣归来~");
                        em.startInstance(cm.getParty(), cm.getPlayer().getMap());
                    } else {
                        if (cm.getBossLog("mvtjd") < 100) {
                            if (cm.getBossLog("mvtjd") > 2) {
                                if (cm.getXw() < 10 || cm.haveItem(4001126, 20) < true) {
                                    cm.sendOk("#r对不起，您今天已经挑战过2次了，如需要在次挑战必须另外加10修为点加20个#z4001126#才可以进入..");
                                    cm.dispose();
                                    return;
                                }
                            cm.setXw(cm.getXw() - 10);
                            cm.gainItem(4001126, -20);
                            } else {
                                //cm.resetBossLog("yfysgw");
                            }
                            cm.setBossLog("mvtjd");
                            cm.worldMessage("【" + cm.getChar().getName() + "】开始挑战剧情副本[魔女塔<简单>]~我们一起期待光荣归来~");
                        dh.newInstance(cm.getName()).registerPlayer(cm.getPlayer());
                        } else {
                            cm.sendOk("#r已经有人在挑战了，请稍等一会。。。");
                            cm.dispose();
                            return;
                        }
                    }
                }
                cm.dispose();
                return;
            }
            else if (typede == 2) { //斩杀魔帝
                if (cm.getParty() == null) { // No Party
                    cm.sendOk("需要先#b开启#k一个组队,而且只能是你一个人~.zzzZZZZZ..");
                    cm.dispose();
                    return;
                } else if (!cm.isLeader()) { // Not Party Leader
                    cm.sendOk("请叫队长跟我说话.");
                    cm.dispose();
                    return;
                } else if (cm.getMap(980042000).getCharactersSize() > 0 || cm.getMap(980042100).getCharactersSize() > 0 || cm.getMap(980042200).getCharactersSize() > 0) { // Not Party Leader
                    cm.sendOk("有人在挑战此副本，请稍等一会");
                    cm.dispose();
                    return;
                } else {
                    var party = cm.getParty().getMembers();
                    if (party.size() > 1) {
                        cm.sendOk("#r对不起,为了彻底的测试你的能力,只能一人前往..");
                        cm.dispose();
                        return;
                    }
                    var dh = cm.getEventManager("WitchTower_Med");
                    if (dh == null) {
                        cm.sendOk("此副本出错啦,请联系管管修复吧.");
                        cm.dispose();
                        return;
                    }
                    if (cm.getChar().getVip() >= 6) {
                        cm.worldMessage("【" + cm.getChar().getName() + "】开始挑战神级副本『与神共舞』~我们一起期待光荣归来~");
                        em.startInstance(cm.getParty(), cm.getPlayer().getMap());
                    } else {
                        if (cm.getBossLog("mvtpt") < 100) {
                            if (cm.getBossLog("mvtpt") > 2) {
                                if (cm.getXw() < 10 || cm.haveItem(4001126, 50) < true) {
                                 cm.sendOk("#r对不起，您今天已经挑战过2次了，如需要在次挑战必须另外加20修为点加50个#z4001126#才可以进入..");
                                    cm.dispose();
                                    return;
                                }
                            cm.setXw(cm.getXw() - 20);
                            cm.gainItem(4001126, -50);
                            } else {
                                //cm.resetBossLog("yfysgw");
                            }
                            cm.setBossLog("mvtpt");
                            cm.worldMessage("【" + cm.getChar().getName() + "】开始挑战剧情副本[魔女塔<普通>]~我们一起期待光荣归来~");
                        dh.newInstance(cm.getName()).registerPlayer(cm.getPlayer());
                        } else {
                            cm.sendOk("#r已经有人在挑战了，请稍等一会。。。");
                            cm.dispose();
                            return;
                        }
                    }
                }
                cm.dispose();
                return;
            }
            else if (typede == 3) { //干掉上帝
                if (cm.getParty() == null) { // No Party
                    cm.sendOk("需要先#b开启#k一个组队,而且只能是你一个人~.zzzZZZZZ..");
                    cm.dispose();
                    return;
                } else if (!cm.isLeader()) { // Not Party Leader
                    cm.sendOk("请叫队长跟我说话.");
                    cm.dispose();
                    return;
                } else if (cm.getMap(910023100).getCharactersSize() > 0) { // Not Party Leader
                    cm.sendOk("有人在挑战此副本，请稍等一会，或者换其它线尝试一下！..");
                    cm.dispose();
                    return;
                } else {
                    var party = cm.getParty().getMembers();
                    if (party.size() > 1) {
                        cm.sendOk("#r对不起,为了彻底的测试你的能力,只能一人前往..");
                        cm.dispose();
                        return;
                    }
                    var em = cm.getEventManager("yfgdsd");
                    if (em == null) {
                        cm.sendOk("此副本出错啦,请联系管管修复吧.");
                        cm.dispose();
                        return;
                    } else {
                        var prop = em.getProperty("state");
                        if (prop == null) {
                            cm.sendOk("已经有人在挑战了，请稍等一会，或者换其它线尝试一下！..");
                            cm.dispose();
                            return;
                        }
                    }
                    if (cm.getChar().getVip() >= 6) {
                        cm.worldMessage("【" + cm.getChar().getName() + "】开始挑战神级副本『干掉上帝』~我们一起期待光荣归来~");
                        em.startInstance(cm.getParty(), cm.getPlayer().getMap());
                    } else {
                        if (cm.haveItem(4001126, 50) == true && cm.haveItem(4002002, 10) == true) {
                            if (cm.getChar().getBossLog("yfgdsd") > 0) {
                                if (cm.getXw() < 300) {
                                    cm.sendOk("#r对不起，您今天已经挑战过一次了，如需要在次挑战必须另外加300修为点才可以进入..");
                                    cm.dispose();
                                    return;
                                }
                            cm.setXw(cm.getXw() - 300);
                            } else {
                                //cm.resetBossLog("yfzsmd");
                            }
                            cm.setBossLog("yfgdsd");
                            cm.gainItem(4001126, -50);
                            cm.gainItem(4002002, -10);
                            cm.worldMessage("【" + cm.getChar().getName() + "】开始挑战神级副本『干掉上帝』~我们一起期待光荣归来~");
                            em.startInstance(cm.getParty(), cm.getPlayer().getMap());
                        } else {
                            cm.sendOk("#r你好像没有足够的修为点或者枫叶，或者邮票,我不能让你进去..");
                            cm.dispose();
                            return;
                        }
                    }
                }
                cm.dispose();
                return;
            }
            else if (typede == 4) { //挑战春哥
                if (cm.getParty() == null) { // No Party
                    cm.sendOk("需要先#b开启#k一个组队,而且只能是你一个人~.zzzZZZZZ..");
                    cm.dispose();
                    return;
                } else if (!cm.isLeader()) { // Not Party Leader
                    cm.sendOk("请叫队长跟我说话.");
                    cm.dispose();
                    return;
                } else if (cm.getMap(910023100).getCharactersSize() > 0) { // Not Party Leader
                    cm.sendOk("有人在挑战此副本，请稍等一会，或者换其它线尝试一下！..");
                    cm.dispose();
                    return;
                } else {
                    var party = cm.getParty().getMembers();
                    if (party.size() > 1) {
                        cm.sendOk("#r对不起,为了彻底的测试你的能力,只能一人前往..");
                        cm.dispose();
                        return;
                    }
                    var em = cm.getEventManager("yftzcg");
                    if (em == null) {
                        cm.sendOk("此副本出错啦,请联系管管修复吧.");
                        cm.dispose();
                        return;
                    } else {
                        var prop = em.getProperty("state");
                        if (prop == null) {
                            cm.sendOk("已经有人在挑战了，请稍等一会，或者换其它线尝试一下！..");
                            cm.dispose();
                            return;
                        }
                    }
                    if (cm.getChar().getVip() >= 6) {
                        cm.worldMessage("【" + cm.getChar().getName() + "】开始挑战神级副本『挑战春哥』~我们一起期待光荣归来~");
                        em.startInstance(cm.getParty(), cm.getPlayer().getMap());
                    } else {
                        if (cm.haveItem(4001126, 50) == true && cm.haveItem(4002002, 10) == true) {
                            if (cm.getChar().getBossLog("yftzcg") > 0) {
                                if (cm.getHyPay(1) < 5000) {
                                    cm.sendOk("#r对不起，您今天已经挑战过一次了，如需要在次挑战必须另外加5000消费币才可以进入..");
                                    cm.dispose();
                                    return;
                                }
                                cm.addHyPay(5000);
                            } else {
                                //cm.resetBossLog("yftzcg");
                            }
                            cm.setBossLog("yfgdsd");
                            cm.gainItem(4001126, -50);
                            cm.gainItem(4002002, -10);
                            cm.worldMessage("【" + cm.getChar().getName() + "】开始挑战神级副本『挑战春哥』~我们一起期待光荣归来~");
                            em.startInstance(cm.getParty(), cm.getPlayer().getMap());
                        } else {
                            cm.sendOk("#r你好像没有足够的消费币或者枫叶，或者邮票,我不能让你进去..");
                            cm.dispose();
                            return;
                        }
                    }
                }
                cm.dispose();
                return;
            }
            else if (typede == 5) { //简单蜈蚣
                if (cm.getParty() == null) { // No Party
                    cm.sendOk("需要先#b开启#k一个组队,而且只能是你一个人~.zzzZZZZZ..");
                    cm.dispose();
                    return;
                } else if (!cm.isLeader()) { // Not Party Leader
                    cm.sendOk("请叫队长跟我说话.");
                    cm.dispose();
                    return;
                } else if (cm.getMap(980000103).getCharactersSize() > 0) { // Not Party Leader
                    cm.sendOk("有人在挑战此副本，请稍等一会，或者换其它线尝试一下！..");
                    cm.dispose();
                    return;
                } else {
                    var party = cm.getParty().getMembers();
                    if (party.size() > 1) {
                        cm.sendOk("#r对不起,为了彻底的测试你的能力,只能一人前往..");
                        cm.dispose();
                        return;
                    }
                    var em = cm.getEventManager("yfjdwg");
                    if (em == null) {
                        cm.sendOk("此副本出错啦,请联系管管修复吧.");
                        cm.dispose();
                        return;
                    } else {
                        var prop = em.getProperty("state");
                        if (prop == null) {
                            cm.sendOk("已经有人在挑战了，请稍等一会，或者换其它线尝试一下！..");
                            cm.dispose();
                            return;
                        }
                    }
                    if (cm.getChar().getVip() >= 5) {
                        cm.worldMessage("【" + cm.getChar().getName() + "】开始挑战简单级副本『蜈蚣王』~我们一起期待光荣归来~");
                        em.startInstance(cm.getParty(), cm.getPlayer().getMap());
                    } else {
                        if ((cm.getPlayer().getReborns() >= 0 && cm.getPlayer().getReborns() < 10)) {
                            if (cm.getChar().getBossLog("yfjdwg") > 1) {
                                if (cm.getHyPay(1) < 1000) {
                                    cm.sendOk("#r对不起，您今天已经挑战过2次了，如需要在次挑战必须另外加1000消费币才可以进入..");
                                    cm.dispose();
                                    return;
                                }
                                cm.addHyPay(1000);
                            } else {
                                //cm.resetBossLog("yfjdwg");
                            }
                            cm.setBossLog("yfjdwg");
                            cm.worldMessage("【" + cm.getChar().getName() + "】开始挑战简单级副本『蜈蚣王』~我们一起期待光荣归来~");
                            em.startInstance(cm.getParty(), cm.getPlayer().getMap());
                        } else {
                            cm.sendOk("你选择的难度不适合你的等级了..");
                            cm.dispose();
                            return;
                        }
                    }
                }
                cm.dispose();
                return;
            }
            else if (typede == 6) { //简单人马
                if (cm.getParty() == null) { // No Party
                    cm.sendOk("需要先#b开启#k一个组队,而且只能是你一个人~.zzzZZZZZ..");
                    cm.dispose();
                    return;
                } else if (!cm.isLeader()) { // Not Party Leader
                    cm.sendOk("请叫队长跟我说话.");
                    cm.dispose();
                    return;
                } else if (cm.getMap(980000103).getCharactersSize() > 0) { // Not Party Leader
                    cm.sendOk("有人在挑战此副本，请稍等一会，或者换其它线尝试一下！..");
                    cm.dispose();
                    return;
                } else {
                    var party = cm.getParty().getMembers();
                    if (party.size() > 1) {
                        cm.sendOk("#r对不起,为了彻底的测试你的能力,只能一人前往..");
                        cm.dispose();
                        return;
                    }
                    var em = cm.getEventManager("yfjdrmw");
                    if (em == null) {
                        cm.sendOk("此副本出错啦,请联系管管修复吧.");
                        cm.dispose();
                        return;
                    } else {
                        var prop = em.getProperty("state");
                        if (prop == null) {
                            cm.sendOk("已经有人在挑战了，请稍等一会，或者换其它线尝试一下！..");
                            cm.dispose();
                            return;
                        }
                    }
                    if (cm.getChar().getVip() >= 5) {
                        cm.worldMessage("【" + cm.getChar().getName() + "】开始挑战简单级副本『人马王』~我们一起期待光荣归来~");
                        em.startInstance(cm.getParty(), cm.getPlayer().getMap());
                    } else {
                        if ((cm.getPlayer().getReborns() >= 0 && cm.getPlayer().getReborns() < 10)) {
                            if (cm.getChar().getBossLog("yfjdrmw") > 1) {
                                if (cm.getHyPay(1) < 1000) {
                                    cm.sendOk("#r对不起，您今天已经挑战过2次了，如需要在次挑战必须另外加1000消费币才可以进入..");
                                    cm.dispose();
                                    return;
                                }
                                cm.addHyPay(1000);
                            } else {
                                //cm.resetBossLog("yfjdrmw");
                            }
                            cm.setBossLog("yfjdrmw");
                            cm.worldMessage("【" + cm.getChar().getName() + "】开始挑战简单级副本『人马王』~我们一起期待光荣归来~");
                            em.startInstance(cm.getParty(), cm.getPlayer().getMap());
                        } else {
                            cm.sendOk("你选择的难度不适合你的等级了..");
                            cm.dispose();
                            return;
                        }
                    }
                }
                cm.dispose();
                return;
            }
            else if (typede == 7) { //普通幽灵船
                if (cm.getParty() == null) { // No Party
                    cm.sendOk("需要先#b开启#k一个组队,而且只能是你一个人~.zzzZZZZZ..");
                    cm.dispose();
                    return;
                } else if (!cm.isLeader()) { // Not Party Leader
                    cm.sendOk("请叫队长跟我说话.");
                    cm.dispose();
                    return;
                } else if (cm.getMap(980000203).getCharactersSize() > 0) { // Not Party Leader
                    cm.sendOk("有人在挑战此副本，请稍等一会，或者换其它线尝试一下！..");
                    cm.dispose();
                    return;
                } else {
                    var party = cm.getParty().getMembers();
                    if (party.size() > 1) {
                        cm.sendOk("#r对不起,为了彻底的测试你的能力,只能一人前往..");
                        cm.dispose();
                        return;
                    }
                    var em = cm.getEventManager("yfptylc");
                    if (em == null) {
                        cm.sendOk("此副本出错啦,请联系管管修复吧.");
                        cm.dispose();
                        return;
                    } else {
                        var prop = em.getProperty("state");
                        if (prop == null) {
                            cm.sendOk("已经有人在挑战了，请稍等一会，或者换其它线尝试一下！..");
                            cm.dispose();
                            return;
                        }
                    }
                    if (cm.getChar().getVip() >= 5) {
                        cm.worldMessage("【" + cm.getChar().getName() + "】开始挑战普通级副本『幽灵船』~我们一起期待光荣归来~");
                        em.startInstance(cm.getParty(), cm.getPlayer().getMap());
                    } else {
                        if ((cm.getPlayer().getReborns() > 10 && cm.getPlayer().getReborns() < 50)) {
                            if (cm.getChar().getBossLog("yfptylc") > 1) {
                                if (cm.getHyPay(1) < 1000) {
                                    cm.sendOk("#r对不起，您今天已经挑战过2次了，如需要在次挑战必须另外加1000消费币才可以进入..");
                                    cm.dispose();
                                    return;
                                }
                                cm.addHyPay(1000);
                            } else {
                                //cm.resetBossLog("yfptylc");
                            }
                            cm.setBossLog("yfptylc");
                            cm.worldMessage("【" + cm.getChar().getName() + "】开始挑战普通级副本『幽灵船』~我们一起期待光荣归来~");
                            em.startInstance(cm.getParty(), cm.getPlayer().getMap());
                        } else {
                            cm.sendOk("你选择的难度不适合你的等级了..");
                            cm.dispose();
                            return;
                        }
                    }
                }
                cm.dispose();
                return;
            }
            else if (typede == 8) { //普通天鹰
                if (cm.getParty() == null) { // No Party
                    cm.sendOk("需要先#b开启#k一个组队,而且只能是你一个人~.zzzZZZZZ..");
                    cm.dispose();
                    return;
                } else if (!cm.isLeader()) { // Not Party Leader
                    cm.sendOk("请叫队长跟我说话.");
                    cm.dispose();
                    return;
                } else if (cm.getMap(980000203).getCharactersSize() > 0) { // Not Party Leader
                    cm.sendOk("有人在挑战此副本，请稍等一会，或者换其它线尝试一下！..");
                    cm.dispose();
                    return;
                } else {
                    var party = cm.getParty().getMembers();
                    if (party.size() > 1) {
                        cm.sendOk("#r对不起,为了彻底的测试你的能力,只能一人前往..");
                        cm.dispose();
                        return;
                    }
                    var em = cm.getEventManager("yfptty");
                    if (em == null) {
                        cm.sendOk("此副本出错啦,请联系管管修复吧.");
                        cm.dispose();
                        return;
                    } else {
                        var prop = em.getProperty("state");
                        if (prop == null) {
                            cm.sendOk("已经有人在挑战了，请稍等一会，或者换其它线尝试一下！..");
                            cm.dispose();
                            return;
                        }
                    }
                    if (cm.getChar().getVip() >= 5) {
                        cm.worldMessage("【" + cm.getChar().getName() + "】开始挑战普通级副本『天鹰』~我们一起期待光荣归来~");
                        em.startInstance(cm.getParty(), cm.getPlayer().getMap());
                    } else {
                        if ((cm.getPlayer().getReborns() > 10 && cm.getPlayer().getReborns() < 50)) {
                            if (cm.getChar().getBossLog("yfptty") > 1) {
                                if (cm.getHyPay(1) < 1000) {
                                    cm.sendOk("#r对不起，您今天已经挑战过2次了，如需要在次挑战必须另外加1000消费币才可以进入..");
                                    cm.dispose();
                                    return;
                                }
                                cm.addHyPay(1000);
                            } else {
                                //cm.resetBossLog("yfptty");
                            }
                            cm.setBossLog("yfptty");
                            cm.worldMessage("【" + cm.getChar().getName() + "】开始挑战普通级副本『天鹰』~我们一起期待光荣归来~");
                            em.startInstance(cm.getParty(), cm.getPlayer().getMap());
                        } else {
                            cm.sendOk("你选择的难度不适合你的等级了..");
                            cm.dispose();
                            return;
                        }
                    }
                }
                cm.dispose();
                return;
            }
            else if (typede == 9) { //困难呕拉拉
                if (cm.getParty() == null) { // No Party
                    cm.sendOk("需要先#b开启#k一个组队,而且只能是你一个人~.zzzZZZZZ..");
                    cm.dispose();
                    return;
                } else if (!cm.isLeader()) { // Not Party Leader
                    cm.sendOk("请叫队长跟我说话.");
                    cm.dispose();
                    return;
                } else if (cm.getMap(980000303).getCharactersSize() > 0) { // Not Party Leader
                    cm.sendOk("有人在挑战此副本，请稍等一会，或者换其它线尝试一下！..");
                    cm.dispose();
                    return;
                } else {
                    var party = cm.getParty().getMembers();
                    if (party.size() > 1) {
                        cm.sendOk("#r对不起,为了彻底的测试你的能力,只能一人前往..");
                        cm.dispose();
                        return;
                    }
                    var em = cm.getEventManager("yfknoll");
                    if (em == null) {
                        cm.sendOk("此副本出错啦,请联系管管修复吧.");
                        cm.dispose();
                        return;
                    } else {
                        var prop = em.getProperty("state");
                        if (prop == null) {
                            cm.sendOk("已经有人在挑战了，请稍等一会，或者换其它线尝试一下！..");
                            cm.dispose();
                            return;
                        }
                    }
                    if (cm.getChar().getVip() >= 5) {
                        cm.worldMessage("【" + cm.getChar().getName() + "】开始挑战困难级副本『欧拉拉』~我们一起期待光荣归来~");
                        em.startInstance(cm.getParty(), cm.getPlayer().getMap());
                    } else {
                        if ((cm.getPlayer().getReborns() > 50 && cm.getPlayer().getReborns() < 200)) {
                            if (cm.getChar().getBossLog("yfknoll") > 1) {
                                if (cm.getHyPay(1) < 1000) {
                                    cm.sendOk("#r对不起，您今天已经挑战过2次了，如需要在次挑战必须另外加1000消费币才可以进入..");
                                    cm.dispose();
                                    return;
                                }
                                cm.addHyPay(1000);
                            } else {
                                //cm.resetBossLog("yfknoll");
                            }
                            cm.setBossLog("yfknoll");
                            cm.worldMessage("【" + cm.getChar().getName() + "】开始挑战困难级副本『欧拉拉』~我们一起期待光荣归来~");
                            em.startInstance(cm.getParty(), cm.getPlayer().getMap());
                        } else {
                            cm.sendOk("你选择的难度不适合你的等级了..");
                            cm.dispose();
                            return;
                        }
                    }
                }
                cm.dispose();
                return;
            }
            else if (typede == 10) { //困难呕拉拉
                if (cm.getParty() == null) { // No Party
                    cm.sendOk("需要先#b开启#k一个组队,而且只能是你一个人~.zzzZZZZZ..");
                    cm.dispose();
                    return;
                } else if (!cm.isLeader()) { // Not Party Leader
                    cm.sendOk("请叫队长跟我说话.");
                    cm.dispose();
                    return;
                } else if (cm.getMap(980000303).getCharactersSize() > 0) { // Not Party Leader
                    cm.sendOk("有人在挑战此副本，请稍等一会，或者换其它线尝试一下！..");
                    cm.dispose();
                    return;
                } else {
                    var party = cm.getParty().getMembers();
                    if (party.size() > 1) {
                        cm.sendOk("#r对不起,为了彻底的测试你的能力,只能一人前往..");
                        cm.dispose();
                        return;
                    }
                    var em = cm.getEventManager("yfknylm");
                    if (em == null) {
                        cm.sendOk("此副本出错啦,请联系管管修复吧.");
                        cm.dispose();
                        return;
                    } else {
                        var prop = em.getProperty("state");
                        if (prop == null) {
                            cm.sendOk("已经有人在挑战了，请稍等一会，或者换其它线尝试一下！..");
                            cm.dispose();
                            return;
                        }
                    }
                    if (cm.getChar().getVip() >= 5) {
                        cm.worldMessage("【" + cm.getChar().getName() + "】开始挑战困难级副本『御龙魔』~我们一起期待光荣归来~");
                        em.startInstance(cm.getParty(), cm.getPlayer().getMap());
                    } else {
                        if ((cm.getPlayer().getReborns() > 50 && cm.getPlayer().getReborns() < 200)) {
                            if (cm.getChar().getBossLog("yfknylm") > 1) {
                                if (cm.getHyPay(1) < 1000) {
                                    cm.sendOk("#r对不起，您今天已经挑战过2次了，如需要在次挑战必须另外加1000消费币才可以进入..");
                                    cm.dispose();
                                    return;
                                }
                                cm.addHyPay(1000);
                            } else {
                                //cm.resetBossLog("yfknylm");
                            }
                            cm.setBossLog("yfknylm");
                            cm.worldMessage("【" + cm.getChar().getName() + "】开始挑战困难级副本『御龙魔』~我们一起期待光荣归来~");
                            em.startInstance(cm.getParty(), cm.getPlayer().getMap());
                        } else {
                            cm.sendOk("你选择的难度不适合你的等级了..");
                            cm.dispose();
                            return;
                        }
                    }
                }
                cm.dispose();
                return;
            } else if (typede == 13) { //困难呕拉拉
                if (cm.getMin() < 2 || cm.getMin() > 5) {
                    cm.sendOk("击退海盗副本在每个整点2分-5分开放\r\n例如：1点2分-1点5分，开放也会公告通知\r\n服务器时间:#e" + cm.getHour() + "点" + cm.getMin() + "分" + cm.getSec() + "秒#n");
                    cm.dispose();
                } else {
                    cm.warp(251010404);
                    cm.sendOk("请在5分之前组齐3人队伍共同抵抗海盗入侵，点击人参入场\r\n服务器时间:#e" + cm.getHour() + "点" + cm.getMin() + "分" + cm.getSec() + "秒#n");
                    cm.dispose();
                }
            } else if (typede == 14) { //困难呕拉拉
                if (cm.getParty() == null) { // Not Party Leader
                    cm.sendOk("需要先#b开启#k一个组队,而且只能是你一个人~.zzzZZZZZ..");
                    cm.dispose();
                } else if (!cm.isLeader()) { // Not Party Leader
                    cm.sendOk("请叫队长和我说话");
                    cm.dispose();
                } else if (cm.getMap(951000100).getCharactersSize() > 0) { // Not Party Leader
                    cm.sendOk("已有人开始夺宝，下个时段再来吧....");
                    cm.dispose();
                } else if (cm.getPlayer().getClient().getChannel() != 1) { // Not Party Leader
                    cm.sendOk("夺宝只在1频道开放.... ");
                    cm.dispose();
                } else {
                    var party = cm.getParty().getMembers();
                    if (party.size() > 1) {
                        cm.sendOk("对不起,宝物仓库只对1人开放，请单独组队");
                        cm.dispose();
                        return;
                    }
                    var em = cm.getEventManager("yfbeiyong1");
                    if (em == null) {
                        cm.sendOk("此副本出错啦,请联系管管修复吧.");
                        cm.dispose();
                        return;
                    } else {
                        var prop = em.getProperty("state");
                        if (prop == null) {
                            cm.sendOk("已经有人在挑战了，请稍等一会，或者换其它线尝试一下！..");
                            cm.dispose();
                            return;
                        }
                    }
                    if (cm.getMin() == 20) {
                        cm.worldMessage("[限时夺宝]：玩家[" + cm.getChar().getName() + "]进入了宝物仓库，大家祝福他吧。。");
                        em.startInstance(cm.getParty(), cm.getPlayer().getMap());
                        cm.dispose();
                    } else {
                        cm.sendOk("限时夺宝副本在每个整点20分-21分开放\r\n例如：1点20分-1点21分，开放的时候也会公告通知\r\n服务器时间:#e" + cm.getHour() + "点" + cm.getMin() + "分" + cm.getSec() + "秒#n");
                        cm.dispose();
                    }
                }
            } else if (typede == 15) { //困难呕拉拉
                if (cm.getParty() == null) { // Not Party Leader
                    cm.sendOk("需要先#b开启#k一个组队,~.zzzZZZZZ..");
                    cm.dispose();
                } else if (!cm.isLeader()) { // Not Party Leader
                    cm.sendOk("请叫队长和我说话");
                    cm.dispose();
                } else if (cm.getMap(689013000).getCharactersSize() > 0) { // Not Party Leader
                    cm.sendOk("已有人开始夺宝，下个时段再来吧....");
                    cm.dispose();
                } else if (cm.getPlayer().getClient().getChannel() != 1) { // Not Party Leader
                    cm.sendOk("夺宝只在1频道开放.... ");
                    cm.dispose();
                } else {
                    var party = cm.getParty().getMembers();
                    if (party.size() < 3) {
                        cm.sendOk("对不起，粉色扎昆过于强大，必须3人组队才能前行");
                        cm.dispose();
                        return;
                    }
                    var em = cm.getEventManager("PinkZakum");
                    if (em == null) {
                        cm.sendOk("此副本出错啦,请联系管管修复吧.");
                        cm.dispose();
                        return;
                    } else {
                        var prop = em.getProperty("state");
                        if (prop == null) {
                            cm.sendOk("已经有人在挑战了，请稍等一会，或者换其它线尝试一下！..");
                            cm.dispose();
                            return;
                        }
                    }
                    if (cm.getMin() > 35 && cm.getMin() < 58) {
                        cm.worldMessage("[粉色扎昆]：玩家[" + cm.getChar().getName() + "]带领他的队伍去征服粉色扎昆去了~");
                        em.startInstance(cm.getParty(), cm.getPlayer().getMap());
                        cm.dispose();
                    } else {
                        cm.sendOk("挑战粉色扎昆副本在每个整点35分-55分开放\r\n例如：1点35分-1点55分，开放的时候也会公告通知\r\n服务器时间:#e" + cm.getHour() + "点" + cm.getMin() + "分" + cm.getSec() + "秒#n");
                        cm.dispose();
                    }
                }
            } else if (typede == 25) {
                if (cm.getMin() > 30) {
                    cm.sendOk("带有宝石套装的部队还没到...\r\n他们到这里的休息时间为整点的0-30分，那个时候再来吧");
                    cm.dispose();
                    return;
                }
                if (cm.getParty() == null) { // Not Party Leader
                    cm.sendOk("需要先#b开启#k一个组队,~.zzzZZZZZ..");
                    cm.dispose();
                    return;
                }
                    var party = cm.getParty().getMembers();
                    if (party.size() > 1) {
                    cm.sendOk("为了测试你的能力，只能1个人前往zzz.....");
                    cm.dispose();
                    return;
                }
                    if (cm.getMap(980000403).getCharactersSize() > 0) {
                    cm.sendOk("已经有人在挑战了。稍后再来吧....");
                    cm.dispose();
                    return;
                } else if (!cm.isLeader()) { // Not Party Leader
                    cm.sendOk("请叫队长和我说话");
                    cm.dispose();
                }
                var em = cm.getEventManager("yfyl0");
                if (em == null) {
                    cm.sendOk("此副本出错啦,请联系管管修复吧.");
                    cm.dispose();
                    return;
                } else {
                    var prop = em.getProperty("state");
                    if (prop == null) {
                        cm.sendOk("已经有人在挑战了，请稍等一会，或者换其它线尝试一下！..");
                        cm.dispose();
                        return;
                    }
                }
                cm.worldMessage("[娱乐副本]：玩家[" + cm.getChar().getName() + "]进入了抢夺宝石套装副本，大家祝福他吧。。");
                em.startInstance(cm.getParty(), cm.getPlayer().getMap());
                cm.dispose();
            } else if (typede == 26) {
                if (cm.getMin() < 30) {
                    cm.sendOk("带有铂金套装的部队还没到...\r\n他们到这里的休息时间为整点的30-59分，那个时候再来吧");
                    cm.dispose();
                    return;
                }
                if (cm.getParty() == null) { // Not Party Leader
                    cm.sendOk("需要先#b开启#k一个组队,~.zzzZZZZZ..");
                    cm.dispose();
                    return;
                }
                    var party = cm.getParty().getMembers();
                    if (party.size() > 1) {
                    cm.sendOk("为了测试你的能力，只能1个人前往zzz.....");
                    cm.dispose();
                    return;
                }
                    if (cm.getMap(980000403).getCharactersSize() > 0) {
                    cm.sendOk("已经有人在挑战了。稍后再来吧....");
                    cm.dispose();
                    return;
                } else if (!cm.isLeader()) { // Not Party Leader
                    cm.sendOk("请叫队长和我说话");
                    cm.dispose();
                }
                var em = cm.getEventManager("yfyl1");
                if (em == null) {
                    cm.sendOk("此副本出错啦,请联系管管修复吧.");
                    cm.dispose();
                    return;
                } else {
                    var prop = em.getProperty("state");
                    if (prop == null) {
                        cm.sendOk("已经有人在挑战了，请稍等一会，或者换其它线尝试一下！..");
                        cm.dispose();
                        return;
                    }
                }
                cm.worldMessage("[娱乐副本]：玩家[" + cm.getChar().getName() + "]进入了抢夺铂金套装副本，大家祝福他吧。。");
                em.startInstance(cm.getParty(), cm.getPlayer().getMap());
                cm.dispose();
            } else if (typede == 27) {
                if (cm.getMin() < 20 || cm.getMin() > 41) {
                    cm.sendOk("对不起,我只能在每小时的前20~31分钟为你提供服务!");
                    cm.dispose();
                    return;
                }
                if (cm.getParty() == null) { // Not Party Leader
                    cm.sendOk("需要先#b开启#k一个组队,~.zzzZZZZZ..");
                    cm.dispose();
                    return;
                } else if (!cm.isLeader()) { // Not Party Leader
                    cm.sendOk("请叫队长和我说话");
                    cm.dispose();
                }
                var em = cm.getEventManager("yfyl2");
                if (em == null) {
                    cm.sendOk("此副本出错啦,请联系管管修复吧.");
                    cm.dispose();
                    return;
                } else {
                    var prop = em.getProperty("state");
                    if (prop == null) {
                        cm.sendOk("已经有人在挑战了，请稍等一会，或者换其它线尝试一下！..");
                        cm.dispose();
                        return;
                    }
                }
                cm.worldMessage("[娱乐副本]：玩家[" + cm.getChar().getName() + "]进入了娱乐副本2难度，大家祝福他吧。。");
                em.startInstance(cm.getParty(), cm.getPlayer().getMap());
                cm.dispose();

            } else if (typede == 35) {
                if (cm.getMin() < 40 || cm.getMin() > 50) {
                    cm.sendOk("蜗牛爷爷现在还清醒着哟，它一般在整点的40分-50分休息，那个时候再来吧");
                    cm.dispose();
                    return;
                }
                if (cm.getParty() == null) {
                    cm.sendOk("需要先#b开启#k一个组队,~.zzzZZZZZ..");
                    cm.dispose();
                    return;
                }
                if (cm.getXw() < 30) {
                    cm.sendOk("对不起,你修为点不足30点,快在市场泡会吧!");
                    cm.dispose();
                    return;
                }
                    var party = cm.getParty().getMembers();
                    if (party.size() > 1) {
                    cm.sendOk("为了测试你的能力，只能1个人前往zzz.....");
                    cm.dispose();
                    return;
                }
                    if (cm.getMap(980000503).getCharactersSize() > 0) {
                    cm.sendOk("已经有人在挑战了。稍后再来吧....");
                    cm.dispose();
                    return;
                }
                 else if (!cm.isLeader()) { // Not Party Leader
                    cm.sendOk("请叫队长和我说话");
                    cm.dispose();
                    return;
                }
                var em = cm.getEventManager("yfwn");
                if (em == null) {
                    cm.sendOk("此副本出错啦,请联系管管修复吧.");
                    cm.dispose();
                    return;
                } else {
                    var prop = em.getProperty("state");
                    if (prop == null) {
                        cm.sendOk("已经有人在挑战了，请稍等一会，或者换其它线尝试一下！..");
                        cm.dispose();
                        return;
                    }
                }
                cm.worldMessage("[极限副本]：玩家[" + cm.getChar().getName() + "]进入了蜗牛王的邮票副本，大家祝福他吧。。");
                em.startInstance(cm.getParty(), cm.getPlayer().getMap());
                cm.dispose();
            } else if (typede == 36) {
                if (cm.getParty() == null) {
                    cm.sendOk("需要先#b开启#k一个组队,~.zzzZZZZZ..");
                    cm.dispose();
                    return;
                }
                if (cm.getXw() < 400) {
                    cm.sendOk("对不起,你修为点不足400点,快在市场泡会吧!");
                    cm.dispose();
                    return;
                }
                    var party = cm.getParty().getMembers();
                    if (party.size() > 1) {
                    cm.sendOk("为了测试你的能力，只能1个人前往zzz.....");
                    cm.dispose();
                    return;
                }
                    if (cm.getMap(980000503).getCharactersSize() > 0) {
                    cm.sendOk("已经有人在挑战了。稍后再来吧....");
                    cm.dispose();
                    return;
                }
                 else if (!cm.isLeader()) { // Not Party Leader
                    cm.sendOk("请叫队长和我说话");
                    cm.dispose();
                    return;
                }
                var em = cm.getEventManager("yfyxjm");
                if (em == null) {
                    cm.sendOk("此副本出错啦,请联系管管修复吧.");
                    cm.dispose();
                    return;
                } else {
                    var prop = em.getProperty("state");
                    if (prop == null) {
                        cm.sendOk("已经有人在挑战了，请稍等一会，或者换其它线尝试一下！..");
                        cm.dispose();
                        return;
                    }
                }
                cm.worldMessage("[极限副本]：玩家[" + cm.getChar().getName() + "]进入了英雄救美副本，大家祝福他吧。。");
                cm.setXw(cm.getXw() - 300);
                em.startInstance(cm.getParty(), cm.getPlayer().getMap());
                cm.dispose();
            }
        } else if (status == 3) {
            if (typede == 13) {
                if (cm.getMeso() < price || cm.haveItem(4310034, 2) == false) {
                    cm.sendOk("对不起,你没有足够的冒险币,或者是没有足够的#z4310034#");
                    cm.dispose();
                    return;
                }
                var chance = Math.floor(Math.random() * 2);
                if (chance == 1) {
                    var item = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.EQUIP).getItem(1).copy();
                    var statup = new java.util.ArrayList();
                    item.setUpgradeSlots((item.getUpgradeSlots() + 1));
                    Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
                    Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);
                    cm.gainMeso(-price);
                    cm.gainItem(4310034, -2);
                    cm.sendOk("恭喜你成功拉，快快看你的包裹吧！");
                    cm.worldMessage("[武器升级]：恭喜[" + cm.getChar().getName() + "]在市场裂空之鹰处，使用正义币为武器提升了1次砸卷次数");
                    cm.dispose();
                } else {
                    cm.gainMeso(-price / 2);
                    cm.gainItem(4310034, -2);
                    cm.sendOk("真遗憾，升级失败");
                }
                cm.dispose();
                return;
            }
            if (typede == 14) {
                if (cm.getMeso() < price || cm.haveItem(4310003, 1) == false) {
                    cm.sendOk("对不起,你没有足够的冒险币,或者是没有足够的#z4310003#");
                    cm.dispose();
                    return;
                }
                var chance = Math.floor(Math.random() * 2);
                if (chance == 1) {
                    var item = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.EQUIP).getItem(1).copy();
                    var statup = new java.util.ArrayList();
                    item.setUpgradeSlots((item.getUpgradeSlots() + 1));
                    Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
                    Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);
                    cm.gainMeso(-price);
                    cm.gainItem(4310003, -1);
                    cm.sendOk("恭喜你成功拉，快快看你的包裹吧！");
                    cm.worldMessage("[武器升级]：恭喜[" + cm.getChar().getName() + "]在市场裂空之鹰处，使用黄金枫叶为武器提升了1次砸卷次数");
                    cm.dispose();
                } else {
                    cm.gainMeso(-price / 2);
                    cm.gainItem(4310003, -1);
                    cm.sendOk("真遗憾，升级失败");
                }
                cm.dispose();
                return;
            }
            if (typede == 15) {
                if (cm.getMeso() < price || cm.haveItem(4032733, 30) == false) {
                    cm.sendOk("对不起,你没有足够的冒险币,或者是没有足够的#z4032733#");
                    cm.dispose();
                    return;
                }
                var chance1 = Math.floor(Math.random() * 2);
                if (chance1 == 1) {
                    var item = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.EQUIP).getItem(1).copy();
                    var ii = Packages.server.MapleItemInformationProvider.getInstance();
                    var chance = Math.floor(Math.random() * 100);
                    var lvsj = Math.floor(Math.random() * 20) + 10;
                    cm.gainMeso(-price);
                    cm.gainItem(4032733, -30);
                    if (chance <= 5) { //watk
                        item.setWatk(item.getWatk() * 1 + lvsj);
                        Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
                        Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);
                        cm.sendOk("恭喜，成功给装备增加了:#r" + lvsj + "#k攻击.");
                        cm.worldMessage("[装备升级]：恭喜[" + cm.getChar().getName() + "]在市场裂空之鹰处，使用彩虹枫叶提升了武器的攻击");
                    } else if (chance > 5 && chance <= 20) { //matk
                        item.setMatk(item.getMatk() * 1 + lvsj);
                        Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
                        Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);
                        cm.sendOk("恭喜，成功给装备增加了:#r" + lvsj + "#k魔攻.");
                        cm.worldMessage("[装备升级]：恭喜[" + cm.getChar().getName() + "]在市场裂空之鹰处，使用彩虹枫叶提升了武器的魔法攻击");
                    } else if (chance > 20 && chance <= 40) { //str
                        item.setStr(item.getStr() * 1 + lvsj);
                        Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
                        Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);
                        cm.sendOk("恭喜，成功给装备增加了:#r" + lvsj + "#k力量.");
                        cm.worldMessage("[装备升级]：恭喜[" + cm.getChar().getName() + "]在市场裂空之鹰处，使用彩虹枫叶提升了武器的力量");
                    } else if (chance > 40 && chance <= 60) { //dex
                        item.setDex(item.getDex() * 1 + lvsj);
                        Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
                        Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);
                        cm.sendOk("恭喜，成功给装备增加了:#r" + lvsj + "#k敏捷.");
                        cm.worldMessage("[装备升级]：恭喜[" + cm.getChar().getName() + "]在市场裂空之鹰处，使用彩虹枫叶提升了武器的敏捷");
                    } else if (chance > 60 && chance <= 80) { //luk
                        item.setInt(item.getInt() * 1 + lvsj);
                        Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
                        Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);
                        cm.sendOk("恭喜，成功给装备增加了:#r" + lvsj + "#k智力.");
                        cm.worldMessage("[装备升级]：恭喜[" + cm.getChar().getName() + "]在市场裂空之鹰处，使用彩虹枫叶提升了武器的智力");
                    } else if (chance > 80) { //int
                        item.setLuk(item.getLuk() * 1 + lvsj);
                        Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
                        Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);
                        cm.sendOk("恭喜，成功给装备增加了:#r" + lvsj + "#k运气.");
                        cm.worldMessage("[装备升级]：恭喜[" + cm.getChar().getName() + "]在市场裂空之鹰处，使用彩虹枫叶提升了武器的运气");
                    }
                } else {
                    cm.gainMeso(-price / 2);
                    cm.gainItem(4032733, -30);
                    cm.sendOk("真遗憾，升级失败");
                }
                cm.dispose();
                return;
            }

            if (typede == 101) {
                Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, selection, 1, true);
                cm.sendOk("恭喜,此道具已被清除.");
                cm.dispose();
            }
            if (typede == 102) {
                Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.CASH, selection, 1, true);
                cm.sendOk("恭喜,此道具已被清除.");
                cm.dispose();
            }
        }
    }
}
