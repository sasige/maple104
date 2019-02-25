var status = 0;
var typede = 0;
var eff = "#fEffect/CharacterEff/1112905/0/1#";
var hwtext = new Array("人长得漂亮不如活得漂亮！", "当裤子失去皮带，才懂得什麽叫做依赖。", "烟不听话，所以我们'抽烟'。", "你发怒一分钟，便失去60秒的幸福。", "当男人遇见女人，从此只有纪念日，没有独立日。", "路见不平一声吼，吼完继续往前走。", "幸福是个比较级，要有东西垫底才感觉得到。", "知识就像内裤，看不见但很重要", "作为失败的典型，你实在是太成功了", "女人喜欢长得坏坏的男人，不是喜欢长坏了的男人", "跌倒了，爬起来再哭", "你若流泪，先湿的是我的心", "让未来到来，让过去过去", "我自横刀向天笑，笑完之后去睡觉", "别跟我谈感情，谈感情伤钱", "孤单是一个人的狂欢，狂欢是一群人的孤单", "姐不是收破烂的，做不到让你随喊随到", "我不是草船，你的贱别往我这发", "你的矮是终身的，我的胖却是暂时的", "別在无聊的时候來找我，不然显得我是多余的", "姐不是电视机，不要老是盯着姐看", "即使你已名花有主、我也要移花接木", "心里只有你一个频道 最可恨的是还没有广告", "给你最大的报复，就是活的比你幸福", "要不是老师说不能乱扔垃圾，不然我早把你扔出去", "没有癞蛤蟆，天鹅也会寂寞", "我是光棍我可耻，我给国家浪费纸", "人生没有如果，只有后果和结果", "你那么有钱 为什么不让鬼来推磨？", "别把人和狗相提并论，狗最起码忠诚", "生活嘛，就是生下来，活下去", "当你披上了婚纱 我也披上了袈裟", "趁着年轻把能干的坏事都干了吧，没几年了", "我人生只会两件事 1 这也不会 2 那也不会", "出租车司机，司机中的战斗机，噢耶! ", "思想有多远，你就给我滚多远!", "人生最大的悲哀是青春不在,青春痘却还在。", "最简单的长寿秘决:保持呼吸，不要断气~", "打死我也不说，你们还没使美人计呢!", "不要和我比懒,我懒得和你比", "我不是个随便的人 我随便起来不是人", "不怕虎一样的敌人，就怕猪一样的队友", "老虎不发威 你当我是HELLO KITTY！", "吃自助最高境界：扶墙进，扶墙出。", "爷爷都是从孙子走过来的……", "夏天就是不好，穷的时候我连西北风都没得喝", "没什么事就不要找我，有事了更不要找我。", "我想早恋，可是已经晚了……", "钱可以解决的问题都不是问题。", "天哪，我的衣服又瘦了！", "不吃饱哪有力气减肥啊？", "连广告也信，读书读傻了吧？", "人怕出名猪怕壮，男怕没钱女怕胖。", "如果有钱也是一种错，我情愿一错再错", "命运负责洗牌，但是玩牌的是我们自己！", "好好活着，因为我们会死很久!", "人又不聪明，还学人家秃顶！", "我总在牛a与牛c之间徘徊。", "不怕被人利用，就怕你没用。", "鄙视我的人这么多，你算老几? ", "秀发去无踪，头屑更出众！", "春色满园关不住，我诱红杏出墙来。", "问世间情为何物？一物降一物", "bmw是别摸我，msn是摸死你", "女为悦己者容,男为悦己者穷！ ", "念了十几年书，还是幼儿园比较好混");

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
            cm.sendNext("伟大的#b#h ##k，我是本服单人副本的NPC，本服所有开放的单人副本都在我这里进行哟")
        } else if (status == 1) {
            if (cm.getMapId() == 910023100 || cm.getMapId() == 910023400) {
                cm.sendSimple("征服BOSS了吗?青年\r\n#L0#我要离开");
            } else {
                var hwchance = Math.floor(Math.random() * hwtext.length);
                cm.sendSimpleS("#r幽默时刻:" + hwtext[hwchance] + "#k\r\n服务器时间:#e" + cm.getHour() + "点" + cm.getMin() + "分" + cm.getSec() + "秒#n  消费币余额:#e" + cm.getHyPay(1) + "#n个 #r#L0#回到市场#k#l\r\n\r\n#r=================限制级别副本========================#k\r\n#L5#" + eff + "#g[简单级]#k挑战蜈蚣王" + eff + "#L6#" + eff + "#g[简单级]#k挑战人马王" + eff + "#l\r\n#L7#" + eff + "#d[普通级]#k挑战幽灵船" + eff + "#L8#" + eff + "#d[普通级]#k挑战天鹰火龙" + eff + "#l\r\n#L9#" + eff + "#r[困难级]#k挑战欧啦啦" + eff + "#L10#" + eff + "#r[困难级]#k挑战御龙魔" + eff + "#l\r\n\r\n#r=================无限制级别副本======================#k\r\n#L1#" + eff + "#r[神级]#b[装备]#k与神共舞" + eff + "#L2#" + eff + "#r[神级]#b[点装]#k斩杀魔帝" + eff + "\r\n#L3#" + eff + "#r[神级]#b[福袋]#k干掉上帝" + eff + "#L4#" + eff + "#r[神级]#b[椅子]#k挑战春哥" + eff + "#l\r\n\r\n#r=================剧情副本=============================#k\r\n#L11#" + eff + "#d[剧情]#k攻打电钻老巢" + eff + "#L12#" + eff + "#d[剧情]#k拯救阿斯旺" + eff + "", 2);
            }
        } else if (status == 2) {
            if (selection == 0) { //存在副本中就退出
                cm.warp(910000000, 0);
                cm.dispose();
            } else if (selection == 11) { //攻打电钻老巢
                cm.sendOk("剧情正在整理中..稍后开放");
                cm.dispose();
            } else if (selection == 12) { //拯救阿斯旺
                cm.sendOk("剧情正在整理中..稍后开放");
                cm.dispose();
            } else if (selection == 1) { //与神共舞
                typede = 1;
                cm.sendNext("嘿,朋友,还有虐待扎，黑龙吗？你OUT了，欢迎来到与神共舞\r\n#r>>>.为什么取这个名字呢~是因为此副本超级BT,一般人是无法战胜的~里面的怪物是法兰肯~你必须独立战胜它,奖励是本服管管才加入的超属性装备,如下图:\r\n#v1432084##v1442113##v1302149##v1462076##v1452058##v1442090##v1432066##v1402073##v1382068##v1382058#一般各属性都有加,攻击最低在160攻以上，昨天有个玩家暴到了300攻的装备，还不快试试~\r\n#r进入条件:\r\n每天第一次进:100个枫叶#v4001126#和2张木妖邮票#v4002002#\r\n#b第二次及以上，需要另加消费币2000,#g[终极会员直接进入]");
            } else if (selection == 2) { //斩杀魔帝
                typede = 2;
                cm.sendNext("传说中，武术高到顶点就不再有正邪之分，他们会成为“地狱世界”的一员。三百年曾经有一位冒险家击败过“地狱世界”的五十名绝顶高手，他的名字就是——魔帝。由于他的力量太过强大，冥界将他的真身封印了起来。而现在的魔帝以回收死人之魂魄，有着蝙蝠外貌的死神。他将要如何面对企图颠覆冥界的阴谋和自己模糊了数百年的记忆呢？#r英雄的战士们~维护岛内和平靠你们了~战胜后还有稀有点装掉落备:\r\n#v1000050##v1050227##v1001076##v1042081##v1102453##v1102390##v1102389##v1102385##v1102386##v1102368##v1102367##v1102358##v1102355#还有更多稀有点装等你发掘...~\r\n#r进入条件:\r\n每天第一次进:100个枫叶#v4001126#和10张木妖邮票#v4002002#\r\n#b第二次及以上，需要另加消费币2000,#g[终极会员直接进入]");
            } else if (selection == 3) { //干掉上帝
                typede = 3;
                cm.sendNext("#b一位冒险家因为一个未知的理由，要干了上帝，结果未知。~\r\n#k上帝万物之始~推动宇宙运动的第一个因~帝有伟大的神性，无所不知，无所不能,上帝给了我们生，却也决定了我们的死，他约定给人类生活以幸福，却取走了人类的自由，人更加恶意的揣测，当世界一片虚无，上帝只是上帝，而上帝创造了人类，上帝才有了他存在的意义,#d本副本为本服终极副本.\r\n#r奖励:每次必暴一个#v3993003#，可在市场最左边的飞天猪NPC处抽终极装备系列~\r\n#r进入条件:\r\n每天第一次进:200个枫叶#v4001126#和10张木妖邮票#v4002002#\r\n#b第二次及以上，需要另加5000消费币,#g[终极会员直接进入]");
            } else if (selection == 4) { //挑战春哥
                typede = 4;
                cm.sendNext("期待已久的春哥终于面世，这次春哥哥照常不负众望，拿出了看家宝物稀有椅子，春哥哥包包里带来了许多的稀有椅子，征服他吧，你将获得不少的椅子，机会不容错过~\r\n#r进入条件:\r\n每天第一次进:200修为点+2张木妖邮票+300个枫叶\r\n#b第二次及以上，需要另加消费币5000,#g[终极会员直接进入]");
            } else if (selection == 5) { //简单蜈蚣
                typede = 5;
                cm.sendNext("蜈蚣是以往冒险岛的王者，在没有4转，没有外界地图的时候，蜈蚣就是王。今天，蜈蚣风范依旧，等待大家将其斩杀！\r\n#b该副本针对#r0-10#b转玩家开放，为简单模式。进入地图后，你有10分钟的时间来杀死#e1只3000W血量蜈蚣#n，杀死蜈蚣人马将会获得[#r冒险币、120装备、修为点#b其中一种]。都是随便出的哦！\r\n\r\n每日可进入2次，2次后需加1000消费币(终极会员直接进入)#r#e必须独孤不组队才能进入#n#b！#b是否想体验一下呢？");
            } else if (selection == 6) { //简单人马
                typede = 6;
                cm.sendNext("人马王是以往冒险岛的王者，在没有4转，没有外界地图的时候，人马就是王。今天，人马风范依旧，等待大家将其斩杀！\r\n#b该副本针对#r0-10#b转玩家开放，为简单模式。进入地图后，你有10分钟的时间来杀死#e1只3000W血量人马#n，杀死蜈蚣将会获得[#r冒险币、120装备、修为点#b其中一种]。都是随便出的哦！\r\n\r\n每日可进入2次，2次后需加1000消费币(终极会员直接进入)#r#e必须独孤不组队才能进入#n#b！#b是否想体验一下呢？");
            } else if (selection == 7) { //普通幽灵船
                typede = 7;
                cm.sendNext("幽灵船的船长-船长等待你的挑战！他拥有超厚的血量，超强的防御，致命的打击，身为新手的你，能否对付他呢？\r\n#b该副本针对#r10-50#b转玩家开放，为普通模式。进入地图后，你有5分钟的时间来杀死#e船长#n，杀死船长将会获得[#r邮票、修为点、130高级武器#b其中一种]。都是随便出的哦！\r\n\r\n玩家每日可以进入2次，2次后需加1000消费币(终极会员直接进入)\r\n#r#e必须独孤不组队才能进入#n#b！#b是否想体验一下呢？");
            } else if (selection == 8) { //普通天鹰
                typede = 8;
                cm.sendNext("天鹰洞的天鹰-天鹰等待你的挑战！他拥有超厚的血量，超强的防御，致命的打击，身为新手的你，能否对付他呢？\r\n#b该副本针对#r10-50#b转玩家开放，为普通模式。进入地图后，你有5分钟的时间来杀死#e天鹰#n，杀死天鹰将会获得[#r邮票、修为点、130高级武器#b其中一种]。都是随便出的哦！\r\n\r\n玩家每日可以进入2次，2次后需加1000消费币(终极会员直接进入)\r\b#r#e必须独孤不组队才能进入#n#b！#b是否想体验一下呢？");
            } else if (selection == 9) { //困难欧拉拉
                typede = 9;
                cm.sendNext("未来世界的欧拉拉-欧拉拉等待你的挑战！他拥有超厚的血量，超强的防御，致命的打击，身为老手的你，能否对付他呢？\r\n#b该副本针对#r50-200#b转玩家开放，为困难模式。进入地图后，你有5分钟的时间来杀死#e欧拉拉#n\r\n-副本奖励：\r\n[#r邮票、修为点、130高级武器、C级星岩石#b其中一种]。都是随便出的哦！\r\n-副本要求：\r\n#r每日每人可进入2次。2次后需加1000消费币(终极会员直接进入)");
            } else if (selection == 10) { //困难御龙魔
                typede = 10;
                cm.sendNext("未来世界的御龙魔-欧拉拉御龙魔等待你的挑战！他拥有超厚的血量，超强的防御，致命的打击，身为老手的你，能否对付他呢？\r\n#b该副本针对#r50-200#b转玩家开放，为困难模式。进入地图后，你有5分钟的时间来杀死#e御龙魔#n\r\n-副本奖励：\r\n[#r邮票、修为点、130高级武器、C级星岩石#b其中一种]。都是随便出的哦！\r\n-副本要求：\r\n#r每日每人可进入2次。2次后需加1000消费币(终极会员直接进入)");
            }
        } else if (status == 3) {
            if (typede == 1) { //与神共舞
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
                    var em = cm.getEventManager("yfysgw");
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
                        cm.worldMessage("【" + cm.getChar().getName() + "】开始挑战神级副本『与神共舞』~我们一起期待光荣归来~");
                        em.startInstance(cm.getParty(), cm.getPlayer().getMap());
                    } else {
                        if (cm.haveItem(4001126, 50) == true && cm.haveItem(4002002, 10) == true) {
                            if (cm.getBossLog("yfysgw") > 0) {
                                if (cm.getHyPay(1) < 2000) {
                                    cm.sendOk("#r对不起，您今天已经挑战过一次了，如需要在次挑战必须另外加2000消费币才可以进入..");
                                    cm.dispose();
                                    return;
                                }
                                cm.addHyPay(2000);
                            } else {
                                //cm.resetBossLog("yfysgw");
                            }
                            cm.gainItem(4001126, -50);
                            cm.gainItem(4002002, -10);
                            cm.setBossLog("yfysgw");
                            cm.worldMessage("【" + cm.getChar().getName() + "】开始挑战神级副本『与神共舞』~我们一起期待光荣归来~");
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
            else if (typede == 2) { //斩杀魔帝
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
                    var em = cm.getEventManager("yfzsmd");
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
                        cm.worldMessage("【" + cm.getChar().getName() + "】开始挑战神级副本『斩杀魔帝』~我们一起期待光荣归来~");
                        em.startInstance(cm.getParty(), cm.getPlayer().getMap());
                    } else {
                        if (cm.haveItem(4001126, 50) == true && cm.haveItem(4002002, 10) == true) {
                            if (cm.getChar().getBossLog("yfzsmd") > 0) {
                                if (cm.getHyPay(1) < 2000) {
                                    cm.sendOk("#r对不起，您今天已经挑战过一次了，如需要在次挑战必须另外加2000消费币才可以进入..");
                                    cm.dispose();
                                    return;
                                }
                                cm.addHyPay(2000);
                            } else {
                                //cm.resetBossLog("yfzsmd");
                            }
                            cm.setBossLog("yfysgw");
                            cm.gainItem(4001126, -50);
                            cm.gainItem(4002002, -10);
                            cm.worldMessage("【" + cm.getChar().getName() + "】开始挑战神级副本『斩杀魔帝』~我们一起期待光荣归来~");
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
                    if (cm.getChar().getVip() >= 5) {
                        cm.worldMessage("【" + cm.getChar().getName() + "】开始挑战神级副本『干掉上帝』~我们一起期待光荣归来~");
                        em.startInstance(cm.getParty(), cm.getPlayer().getMap());
                    } else {
                        if (cm.haveItem(4001126, 50) == true && cm.haveItem(4002002, 10) == true) {
                            if (cm.getChar().getBossLog("yfgdsd") > 0) {
                                if (cm.getHyPay(1) < 5000) {
                                    cm.sendOk("#r对不起，您今天已经挑战过一次了，如需要在次挑战必须另外加5000消费币才可以进入..");
                                    cm.dispose();
                                    return;
                                }
                                cm.addHyPay(5000);
                            } else {
                                //cm.resetBossLog("yfzsmd");
                            }
                            cm.setBossLog("yfgdsd");
                            cm.gainItem(4001126, -50);
                            cm.gainItem(4002002, -10);
                            cm.worldMessage("【" + cm.getChar().getName() + "】开始挑战神级副本『干掉上帝』~我们一起期待光荣归来~");
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
                    if (cm.getChar().getVip() >= 5) {
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
            }
        } else if (status == 4) {
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
