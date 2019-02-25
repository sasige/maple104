importPackage(net.sf.odinms.tools);
var status = 0;
var typed=0;
var mmm = 0;
var lockitems=0;
var hwtext=new Array("人长得漂亮不如活得漂亮！","当裤子失去皮带，才懂得什麽叫做依赖。","烟不听话，所以我们'抽烟'。","你发怒一分钟，便失去60秒的幸福。","当男人遇见女人，从此只有纪念日，没有独立日。","路见不平一声吼，吼完继续往前走。","幸福是个比较级，要有东西垫底才感觉得到。","知识就像内裤，看不见但很重要","作为失败的典型，你实在是太成功了","女人喜欢长得坏坏的男人，不是喜欢长坏了的男人","跌倒了，爬起来再哭","你若流泪，先湿的是我的心","让未来到来，让过去过去","我自横刀向天笑，笑完之后去睡觉","别跟我谈感情，谈感情伤钱","孤单是一个人的狂欢，狂欢是一群人的孤单","姐不是收破烂的，做不到让你随喊随到","我不是草船，你的贱别往我这发","你的矮是终身的，我的胖却是暂时的","別在无聊的时候來找我，不然显得我是多余的","姐不是电视机，不要老是盯着姐看","即使你已名花有主、我也要移花接木","心里只有你一个频道 最可恨的是还没有广告","给你最大的报复，就是活的比你幸福","要不是老师说不能乱扔垃圾，不然我早把你扔出去","没有癞蛤蟆，天鹅也会寂寞","我是光棍我可耻，我给国家浪费纸","人生没有如果，只有后果和结果","你那么有钱 为什么不让鬼来推磨？","别把人和狗相提并论，狗最起码忠诚","生活嘛，就是生下来，活下去","当你披上了婚纱 我也披上了袈裟","趁着年轻把能干的坏事都干了吧，没几年了","我人生只会两件事 1 这也不会 2 那也不会","出租车司机，司机中的战斗机，噢耶! ","思想有多远，你就给我滚多远!","人生最大的悲哀是青春不在,青春痘却还在。","最简单的长寿秘决:保持呼吸，不要断气~","打死我也不说，你们还没使美人计呢!","不要和我比懒,我懒得和你比","我不是个随便的人 我随便起来不是人","不怕虎一样的敌人，就怕猪一样的队友","老虎不发威 你当我是HELLO KITTY！","吃自助最高境界：扶墙进，扶墙出。","爷爷都是从孙子走过来的……","夏天就是不好，穷的时候我连西北风都没得喝","没什么事就不要找我，有事了更不要找我。","我想早恋，可是已经晚了……","钱可以解决的问题都不是问题。","天哪，我的衣服又瘦了！","不吃饱哪有力气减肥啊？","连广告也信，读书读傻了吧？","人怕出名猪怕壮，男怕没钱女怕胖。","如果有钱也是一种错，我情愿一错再错","命运负责洗牌，但是玩牌的是我们自己！","好好活着，因为我们会死很久!","人又不聪明，还学人家秃顶！","我总在牛a与牛c之间徘徊。","不怕被人利用，就怕你没用。","鄙视我的人这么多，你算老几? ","秀发去无踪，头屑更出众！","春色满园关不住，我诱红杏出墙来。","问世间情为何物？一物降一物","bmw是别摸我，msn是摸死你","女为悦己者容,男为悦己者穷！ ","念了十几年书，还是幼儿园比较好混");


function start() {
	var hwchance= Math.floor(Math.random()*hwtext.length);
	cm.sendSimple("#r幽默时刻:"+hwtext[hwchance]+"#k\r\n服务器时间:#e"+cm.getHour()+"点"+cm.getMin()+"分"+cm.getSec()+"秒#n  元宝余额:#e"+cm.getzb()+"#n个\r\n#e#b▄︻┻┳═一以下都是本服精心制作的特色副本.#k\r\n#L5#惊险夺宝#L6#独闯天关#l#L7#虚度年华#l#L8#限时秒怪#l\r\n#L11##r与神共舞#b#l#L14##r斩杀魔帝#b#l#L15#干掉上帝#l#L10#我是坏蛋#l\r\n#L24##b征战冰骑士#b#l#L25##g征战魔幻神龙#b#l#L26##r征战狮子王[BT]#l#n\r\n#b#L22##v4002002# x1 #k换#r 1亿冒险币#l#L23#1亿冒险币#k 换#b#v4002002# x1#l\r\n#b#L18##v4002001# x1 换#r1000万冒险币#l#L19##v4002000# x1 换#r100万冒险币#l",2);
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
			if(selection>=800){
				cm.dispose();
				return;
			}
            if(selection==10){
				typed=10;
				cm.sendNext("#e#bHey,Boy~~Come on~~~~\r\n#k如果你给我#r10亿冒险币#k,我能让全服的人都知道你是坏蛋~赶试下吗~.#r仅限于2频道市场.",2);
			}
			if(selection==11){//与神共舞
				typed=11;
                cm.sendNext("嘿,朋友,还有虐待扎，黑龙吗？你OUT了，欢迎来到与神共舞\r\n#r>>>.为什么取这个名字呢~是因为此副本超级BT,一般人是无法战胜的~里面的怪物是品克缤魔王~你必须独立战胜它,奖励是本服管管才加入的超属性装备,如下图:\r\n#v1432084##v1442113##v1302149##v1462076##v1452058##v1442090##v1432066##v1402073##v1382068##v1382058#一般各属性都有加,攻击最低在160攻以上，昨天有个玩家暴到了300攻的装备，还不快试试~\r\n#r进入条件:\r\n每天第一次进:50个枫叶#v4001126#和1张木妖邮票#v4002002#\r\n#b第二次及以上，需要另加元宝1万,#g[终极会员直接进入]");
			}
			if(selection==14){//斩杀魔帝
				typed=140;
                cm.sendNext("传说中，武术高到顶点就不再有正邪之分，他们会成为“地狱世界”的一员。三百年曾经有一位冒险家击败过“地狱世界”的五十名绝顶高手，他的名字就是——魔帝。由于他的力量太过强大，冥界将他的真身封印了起来。而现在的魔帝以回收死人之魂魄，有着蝙蝠外貌的死神。他将要如何面对企图颠覆冥界的阴谋和自己模糊了数百年的记忆呢？#r英雄的战士们~维护岛内和平靠你们了~战胜后还有机率掉出超属性黄金枫叶装备:\r\n#v1482073##v1402085##v1422057##v1412055##v1452100##v1302142##v1492073##v1472111##v1382093##v1432075##v1312056##v1462085##v1332114#一般攻击天然在180G以上，昨天有个玩家暴到了300攻的装备，信不信由你~\r\n#r进入条件:\r\n每天第一次进:100个枫叶#v4001126#和2张木妖邮票#v4002002#\r\n#b第二次及以上，需要另加元宝1万,#g[终极会员直接进入]");
            }
			if(selection==15){//干掉上帝
				typed=15;
                cm.sendNextPrev("#b一位冒险家因为一个未知的理由，要干了上帝，结果未知。~\r\n#k上帝万物之始~推动宇宙运动的第一个因~帝有伟大的神性，无所不知，无所不能,上帝给了我们生，却也决定了我们的死，他约定给人类生活以幸福，却取走了人类的自由，人更加恶意的揣测，当世界一片虚无，上帝只是上帝，而上帝创造了人类，上帝才有了他存在的意义,#d本副本为本服终极副本,必须要2人组队进入.\r\n#r奖励:每次必暴一个#v3993003#，可在市场最左边的飞天猪NPC处抽终极装备系列~\r\n#r进入条件:\r\n每天第一次进:200个枫叶#v4001126#和2张木妖邮票#v4002002#\r\n#b第二次及以上，需要另加元宝2万,#g[终极会员直接进入]");	
			}
			if(selection==5){
                typed=5;
                cm.sendNext("我先讲一下财富夺宝规则:\r\n#r1.每次进去的时间限制为1分钟\r\n2.每刷新一个物品,1秒后消失.\r\n3.物品为随机刷新随机掉落在地上.请先做好一切准备.\r\n\r\n#b每次进入需要花费#b[2万元宝]");
			}
			if(selection==6){
				if(cm.getChar().getBossLog('dctgfb')>5 && cm.getChar().getVip()<3){
					cm.sendOk("每天最多进入5次，#r至尊会员和白金会员每天可以进入10次！.");
                    cm.dispose();
                    return;
				}
				if(cm.getChar().getBossLog('dctgfb')>10 && cm.getChar().getVip()>=3){
					cm.sendOk("#r至尊会员和白金会员每天可以进入10次！.");
                    cm.dispose();
                    return;
				}
				if (cm.getParty() == null) { // No Party
                    cm.sendOk("嘿,无知的家伙,想要闯天关?\r\n需要先#b开启#k一个组队,里面的怪物是#r相当给力#k的哟.当然了,我也不介意,你单挑.如果你有实力的话zzzZZZZZ..");
                    cm.dispose();
                    return;
                } else if (!cm.isLeader()) { // Not Party Leader
                    cm.sendOk("请叫队长跟我说话.");
                    cm.dispose();
                    return;
                } else {
                    var party = cm.getParty().getMembers();
                    if (party.size() > 1) {
					cm.sendOk("#r对不起,只能你一个人挑战..");
                    cm.dispose();
                    return;
                    }
                    var em = cm.getEventManager("vip4");
					if (em == null) {
						cm.sendOk("此副本出错啦,请联系管管修复吧.");
						cm.dispose();
						return;
					} else {
                        if(em.getProperty("entryPossible")!=null){
							if(em.getProperty("entryPossible").equals("0")==false){
								cm.sendOk("#r已经有人在挑战了，请稍等一会，或者换其它线尝试一下！..");
								cm.dispose();
								return;
							}
						}
						cm.getChar().setBossLog('dctgfb');
						cm.itemlaba("【"+ cm.getChar().getName() +"】非常凶悍的拿着苍蝇排去『独闯天关』了");
                        em.startInstance(cm.getParty(),cm.getPlayer().getMap());
					}
					cm.dispose();
					return;
                }
			}
			if(selection==7){
                typed=7;
                cm.sendNext("嘿,朋友,欢迎来到虚度年华世界:\r\n#r>>>.进入年华副本后,将每秒扣掉您200元宝,如果元宝用完,将会被传回现实世界.里面的怪物刷新为秒刷新,很大机率暴出必成卷,祝福卷,等稀有玩具.每次刷新必暴物品..");
			}
			if(selection==8){
                typed=8;
                cm.sendNext("嘿,朋友,如果你能在5分钟内杀死555只怪,请点击下一步向我挑战.我将会给你准备丰厚的奖励.保证让你爽....\r\n#r记住:每天只能挑战最多3次,开始挑战后不能下线,换频道，否则视为自动放弃任务.每次送的东西都是随机的哟.");
			}
			if(selection==16){
				if(cm.haveItem(4002000,1)==true){
					cm.gainItem(4002000,-1);
					cm.gainMeso(1000000);
					cm.sendOk("恭喜，使用 #b #v4002000# x1#k,获得100万冒险币.");
				}else{
					cm.sendOk("你好像没有#b #v4002000# x1");
				}
				cm.dispose();
			}
			if(selection==17){
				if(cm.getMeso()>=1000000){
					cm.gainItem(4002000,1);
					cm.gainMeso(-1000000);
					cm.sendOk("恭喜，使用 #b100万冒险币. #k,获得 #b#v4002000# x1");
				}else{
					cm.sendOk("你好像没有5亿冒险币.");
				}
				cm.dispose();
			}
			if(selection==18){
				if(cm.haveItem(4002001,1)==true){
					cm.gainItem(4002001,-1);
					cm.gainMeso(10000000);
					cm.sendOk("恭喜，使用 #b #v4002001# x1#k,获得1000万冒险币.");
				}else{
					cm.sendOk("你好像没有#b #v4002001# x1");
				}
				cm.dispose();
			}
			if(selection==19){
				if(cm.haveItem(4002000,1)==true){
					cm.gainItem(4002000,-1);
					cm.gainMeso(1000000);
					cm.sendOk("恭喜，使用 #b #v4002000# x1#k,获得100万冒险币.");
				}else{
					cm.sendOk("你好像没有#b #v4002000# x1");
				}
				cm.dispose();
			}
			if(selection==20){
				if(cm.haveItem(4002001,10)==true){
					cm.gainItem(4002001,-10);
					cm.gainItem(4002002,1);
					cm.sendOk("恭喜，使用 #b #v4002001# x10#k,获得#v4002002# x1.");
				}else{
					cm.sendOk("你好像没有#b #v4002001# x10");
				}
				cm.dispose();
			}
			if(selection==21){
				if(cm.haveItem(4002002,1)==true){
					cm.gainItem(4002002,-1);
					cm.gainItem(4002001,10);
					cm.sendOk("恭喜，使用 #b #v4002002# x1#k,获得#v4002001# x10.");
				}else{
					cm.sendOk("你好像没有#b #v4002002# x1");
				}
				cm.dispose();
			}
			if(selection==22){
				if(cm.haveItem(4002002,1)==true){
					cm.gainItem(4002002,-1);
					cm.gainMeso(100000000);
					cm.sendOk("恭喜，使用 #b #v4002002# x1#k,获得1亿冒险币.");
				}else{
					cm.sendOk("你好像没有#b #v4002002# x1");
				}
				cm.dispose();
			}
			if(selection==23){
				if(cm.getMeso()>=100000000){
					cm.gainItem(4002002,1);
					cm.gainMeso(-100000000);
					cm.sendOk("恭喜，使用 #b1亿万冒险币. #k,获得 #b#v4002002# x1");
				}else{
					cm.sendOk("你好像没有1亿冒险币.");
				}
				cm.dispose();
			}
			if(selection==24){
				typed=24;
                cm.sendNext("沉睡中的冰骑士终于被唤醒了，这次带来了满包包的#r至尊系列装备#k，全属性得到了大大的提升，属性基本上都是在#e200+以上#n，是居家旅行之必备装甲,快来试试吧~\r\n#r进入条件:\r\n每天第一次进:100活跃值+1张木妖邮票+100个枫叶\r\n#b第二次及以上，需要另加元宝1万,#g[终极会员直接进入]");
            }
			if(selection==25){
				typed=25;
                cm.sendNext("期待已久的神龙终于面世，这次神龙照常不负众望，拿出了看家宝物-(#r140级旭日系列装备#k)，属性基本上都是在#e100+以上#n，机会不容错过~\r\n#r进入条件:\r\n每天第一次进:200活跃值+2张木妖邮票+300个枫叶\r\n#b第二次及以上，需要另加元宝2万,#g[终极会员直接进入]");
            }
			if(selection==26){
				typed=26;
                cm.sendNext("狮子王，冒险岛世界的领袖，藐视一切的神，能挑战过它，才是真正的英雄 王上宝物-(#r130级狮子王系列装备#k)，属性基本上都是在#e200+以上#n，机会不容错过~\r\n#r进入条件:\r\n每天第一次进:200活跃值+5张木妖邮票+300个枫叶\r\n#b第二次及以上，需要另加元宝3万,#g[终极会员直接进入]");
            }
		}else if(status == 2){
            if(typed==13){
				typed=14;
				var texts="请选择你要锁定的当前装备：\r\n#L0#----------------------------------------------#l\r\n";
				var inv = cm.getPlayer().getInventory(net.sf.odinms.client.MapleInventoryType.EQUIP);
					for (var i = 1; i <= 96; i++) {
						it = inv.getItem(i);
						if (it != null) {
							texts+="#L"+i+"##v"+it.getItemId()+"##l  ";
						}
					}
					cm.sendSimple(texts,2);
			}
			if(typed==10){
				if(cm.getC().getChannel()==2){
					if(cm.getChar().getMap().mobCount()>5){
						cm.sendOk("天啦，这么多怪了，为了服务器稳定，还是低调点好！！");
						cm.dispose();
						return;
					}
					if(cm.getMeso()>=1000000000){
						var randxsss=Math.floor((Math.random()*9))+1;
						if(randxsss==5){
							cm.summonMobAtPosition(8820001, 1, 1070, 1);
						}else if(randxsss==8){//班·雷昂
							cm.summonMobAtPosition(9500412, 1, 1070, 1);
						}else{
							cm.summonMobAtPosition(8180000, 10, 1070, 4);
						}
						cm.gainMeso(-1000000000);
						cm.itemlaba("【"+ cm.getChar().getName() +"】承认自己是超级坏蛋~~2线市场有好戏看喽~~.~");
						cm.itemlaba("【"+ cm.getChar().getName() +"】承认自己是超级坏蛋~~2线市场有好戏看喽~~.~");
						cm.itemlaba("【"+ cm.getChar().getName() +"】承认自己是超级坏蛋~~2线市场有好戏看喽~~.~");
						cm.dispose();
						return;
					}else{
						cm.sendOk("你还不够坏~如果你有10亿冒险币,我成全你~让你彻底的坏一次~~");
						cm.dispose();
						return;
					}
				}else{
					cm.sendOk("我知道你是坏蛋,但是为了维护社会和平,请在二线使用此功能~~");
					cm.dispose();
					return;
				}
			}
            if(typed==9){
                if(cm.getzb()>=5000){
					if(cm.getChar().getGMLevel()<1){
								cm.superlabad("["+cm.getChar().getName()+"]吼道:"+cm.getText(),5121020);
					}else{
						cm.superlabad("[公告]:"+cm.getText(),5121020);
					}
                    cm.setzb(-5000);
                }else{
                    cm.sendOk("对不起,没有足够的元宝.");
                }
                cm.dispose();
                return;
            }
			if(typed==8){
                if(cm.getChar().getdamagemobstart()==0){
                    if(cm.getChar().getBossLog('tzfb01')>=5){
                        cm.sendOk("#e您今天已经挑战满5次，明天在来吧！！");
                        cm.dispose();
                        return;
                    }
                    if(cm.getChar().getBossLog('tzfb01')==0){
                        cm.getChar().delBossLog('tzfb01');
                    }
					cm.itemlaba("【"+ cm.getChar().getName() +"】手拿冲锋枪接受了『限时秒怪』副本.让我们一起期待吧~");
                    cm.sendOk("#r提醒:您已经#b成功#r开启了挑战.请在五分钟后向我报告战果.");
                    cm.getChar().setlongtemptime(cm.getnowtonow(5));
                    cm.getChar().settimemob(-cm.getChar().gettimemob());
                    cm.getChar().setdamagemobstart(1);
                    cm.getChar().setBossLog('tzfb01');
                    cm.dispose();
                }else{
                    if(cm.getnow()>=cm.getChar().getlongtemptime()){
                        if(cm.getChar().gettimemob()>=555){
                        cm.sendOk("恭喜,你在短短的#e5分钟内,秒杀了"+cm.getChar().gettimemob()+"只怪.#n\r\n你的光荣战绩管管和所有玩家都在看着,你是全服的骄傲.还忘继续努力,为本服创造更大的荣誉.#b我的小小心意请您笑纳>>>");
                        cm.getChar().setdamagemobstart(0);
                        tzlinjiang();
                        }else{
                            cm.sendOk("很遗憾,你在短短的#e5分钟内,秒杀了"+cm.getChar().gettimemob()+"只怪.#n\r\n并没在达到我们的要求555只.希望你继续努力..争取下一次的胜利.");
                            cm.getChar().setdamagemobstart(0);
                            cm.dispose();
                        }
                    }else{
                        cm.sendOk("嘿,时间还没到,抓紧杀怪吧..");
                        cm.dispose();
                    }
                }
			}
			if(typed==15){//vipsd
				if (cm.getParty() == null) { // No Party
                    cm.sendOk("需要先#b开启#k一个组队,而且只能是你一个人~.zzzZZZZZ..");
                    cm.dispose();
                    return;
                } else if (!cm.isLeader()) { // Not Party Leader
                    cm.sendOk("请叫队长跟我说话.");
                    cm.dispose();
                    return;
                } else {
                    var party = cm.getParty().getMembers();
                    if (party.size() != 2) {
					cm.sendOk("#r对不起,为了彻底的测试你们的能力,必须2人组队前往..");
                    cm.dispose();
                    return;
                    }
                    var em = cm.getEventManager("vipsd");
					if (em == null) {
						cm.sendOk("此副本出错啦,请联系管管修复吧.");
						cm.dispose();
						return;
					} else {
						if(cm.getChar().getVip()>=5){
							cm.itemlaba("【"+ cm.getChar().getName() +"】开始挑战神级副本『干掉上帝』~我们一起期待光荣归来~");
								em.startInstance(cm.getParty(),cm.getPlayer().getMap());
						}else{
							if(cm.haveItem(4001126,200)==true && cm.haveItem(4002002,2)==true){
								if(cm.getChar().getBossLog('vipsd')>0){
									if(cm.getzb()<20000){
										cm.sendOk("#r对不起，您今天已经挑战过一次了，如需要在次挑战必须另外加2万元宝才可以进入..");
										cm.dispose();
										return;
									}
									cm.setzb(-20000);
								}else{
									cm.getChar().delBossLog('vipsd');
								}
								cm.gainItem(4001126,-200);
								cm.gainItem(4002002,-2);
								cm.itemlaba("【"+ cm.getChar().getName() +"】开始挑战神级副本『干掉上帝』~我们一起期待光荣归来~");
								em.startInstance(cm.getParty(),cm.getPlayer().getMap());
							}else{
							cm.sendOk("#r你好像没有足够的枫叶，或者邮票,我不能让你进去..");
							cm.dispose();
							return;
							}
						}
                        
					}
					cm.dispose();
					return;
                }	
			}
			if(typed==140){//斩杀魔帝
				if (cm.getParty() == null) { // No Party
                    cm.sendOk("需要先#b开启#k一个组队,而且只能是你一个人~.zzzZZZZZ..");
                    cm.dispose();
                    return;
                } else if (!cm.isLeader()) { // Not Party Leader
                    cm.sendOk("请叫队长跟我说话.");
                    cm.dispose();
                    return;
                } else {
                    var party = cm.getParty().getMembers();
                    if (party.size() > 1) {
					cm.sendOk("#r对不起,为了彻底的测试你的能力,只能一人前往..");
                    cm.dispose();
                    return;
                    }
                    var em = cm.getEventManager("vipmd");
					if (em == null) {
						cm.sendOk("此副本出错啦,请联系管管修复吧.");
						cm.dispose();
						return;
					} else {
						if(em.getProperty("entryPossible")!=null){
							if(em.getProperty("entryPossible").equals("1")==true){
								cm.sendOk("#r已经有人在挑战了，请稍等一会，或者换其它线尝试一下！..");
								cm.dispose();
								return;
							}
						}
						if(cm.getChar().getVip()>=5){
							cm.itemlaba("【"+ cm.getChar().getName() +"】开始挑战神级副本『斩杀魔帝』~我们一起期待光荣归来~");
							em.startInstance(cm.getParty(),cm.getPlayer().getMap());
						}else{
							if(cm.haveItem(4001126,100)==true  && cm.haveItem(4002002,2)==true){
								if(cm.getChar().getBossLog('vipmd')>0){
									if(cm.getzb()<10000){
										cm.sendOk("#r对不起，您今天已经挑战过一次了，如需要在次挑战必须另外加1万元宝才可以进入..");
										cm.dispose();
										return;
									}
									cm.setzb(-10000);
								}else{
									cm.getChar().delBossLog('vipmd');
								}
								cm.gainItem(4001126,-100);
								cm.gainItem(4002002,-2);
								cm.itemlaba("【"+ cm.getChar().getName() +"】开始挑战神级副本『斩杀魔帝』~我们一起期待光荣归来~");
								em.startInstance(cm.getParty(),cm.getPlayer().getMap());
							}else{
								cm.sendOk("#r你好像没有足够的枫叶，或者邮票,我不能让你进去..");
								cm.dispose();
								return;
							}
						}
					}
					cm.dispose();
					return;
                }	
			}
			if(typed==11){//与神共舞
				if (cm.getParty() == null) { // No Party
                    cm.sendOk("需要先#b开启#k一个组队,而且只能是你一个人~.zzzZZZZZ..");
                    cm.dispose();
                    return;
                } else if (!cm.isLeader()) { // Not Party Leader
                    cm.sendOk("请叫队长跟我说话.");
                    cm.dispose();
                    return;
                } else {
                    var party = cm.getParty().getMembers();
                    if (party.size() > 1) {
					cm.sendOk("#r对不起,为了彻底的测试你的能力,只能一人前往..");
                    cm.dispose();
                    return;
                    }
                    var em = cm.getEventManager("vippb");
					if (em == null) {
						cm.sendOk("此副本出错啦,请联系管管修复吧.");
						cm.dispose();
						return;
					} else {
                        if(em.getProperty("entryPossible")!=null){
							if(em.getProperty("entryPossible").equals("1")==true){
								cm.sendOk("#r已经有人在挑战了，请稍等一会，或者换其它线尝试一下！..");
								cm.dispose();
								return;
							}
						}
						if(cm.getChar().getVip()>=5){
							cm.itemlaba("【"+ cm.getChar().getName() +"】开始挑战神级副本『与神共舞』~我们一起期待光荣归来~");
							em.startInstance(cm.getParty(),cm.getPlayer().getMap());
						}else{
							if(cm.haveItem(4001126,50)==true && cm.haveItem(4002002,1)==true){
								if(cm.getChar().getBossLog('vippb')>0){
									if(cm.getzb()<10000){
										cm.sendOk("#r对不起，您今天已经挑战过一次了，如需要在次挑战必须另外加1万元宝才可以进入..");
										cm.dispose();
										return;
									}
									cm.setzb(-10000);
								}else{
									cm.getChar().delBossLog('vippb');
								}
								cm.gainItem(4001126,-50);
								cm.gainItem(4002002,-1);
								cm.itemlaba("【"+ cm.getChar().getName() +"】开始挑战神级副本『与神共舞』~我们一起期待光荣归来~");
								em.startInstance(cm.getParty(),cm.getPlayer().getMap());
							}else{
								cm.sendOk("#r你好像没有足够的元宝或者枫叶，或者邮票,我不能让你进去..");
								cm.dispose();
								return;
							}
						}
					}
					cm.dispose();
					return;
                }
			}
			if(typed==5){
				if (cm.getParty() == null) { // No Party
                    cm.sendOk("嘿,朋友,想去夺宝藏?\r\n需要先#b开启#k一个组队,里面的宝物是#r相当给力#k的哟.当然了,这些宝物会在一秒后消失,要抓紧多捡点哟.zzzZZZZZ..");
                    cm.dispose();
                    return;
                } else if (!cm.isLeader()) { // Not Party Leader
                    cm.sendOk("请叫队长跟我说话.");
                    cm.dispose();
                    return;
                } else {
                    var party = cm.getParty().getMembers();
                    if (party.size() > 1) {
					cm.sendOk("#r对不起,为了游戏平衡.只能你一个人进去夺宝..");
                    cm.dispose();
                    return;
                    }
                    var em = cm.getEventManager("vip5");//980000304
					if (em == null) {
						cm.sendOk("此副本出错啦,请联系管管修复吧.");
						cm.dispose();
						return;
					} else {
						if(em.getProperty("entryPossible")!=null){
							if(em.getProperty("entryPossible").equals("1")==true){
								cm.sendOk("#r已经有人在挑战了，请稍等一会，或者换其它线尝试一下！..");
								cm.dispose();
								return;
							}
						}
                        if(cm.getzb()>=20000){
                        cm.setzb(-20000);
						cm.itemlaba("【"+ cm.getChar().getName() +"】带着空背包进入了『财富夺宝』捡玩具宝贝啦.让我们一起期待光荣归来~");
                        em.startInstance(cm.getParty(),cm.getPlayer().getMap());
                        }else{
                        cm.sendOk("#r你好像没有足够的元宝,我不能让你进去..");
						cm.dispose();
						return;
                        }
					}
					cm.dispose();
					return;
                }
			}
			if(typed==7){
				if (cm.getParty() == null) { // No Party
                    cm.sendOk("想虚度年华.浮云青春?\r\n需要先#b开启#k一个组队,里面的宝物虽然#r很浮云#k的说.还请大爷大姐手下留情...zzzZZZZZ..");
                    cm.dispose();
                    return;
                } else if (!cm.isLeader()) { // Not Party Leader
                    cm.sendOk("请叫队长跟我说话.");
                    cm.dispose();
                    return;
                } else {
                    var party = cm.getParty().getMembers();
                    if (party.size() > 1) {
					cm.sendOk("#r对不起,为了游戏平衡.只能你一个人进去夺宝..");
                    cm.dispose();
                    return;
                    }
                    var em = cm.getEventManager("vip6mob");//980000403
					if (em == null) {
						cm.sendOk("此副本出错啦,请联系管管修复吧.");
						cm.dispose();
						return;
					} else {
                        if(cm.getzb()<200){
                            cm.sendOk("sorry.你的元宝小于200,太少啦,进去一秒不到就出来了.所以我决定还是不让你去了..");
                            cm.dispose();
                            return;
                        }
						if(em.getProperty("entryPossible")!=null){
							if(em.getProperty("entryPossible").equals("1")==true){
								cm.sendOk("#r已经有人在挑战了，请稍等一会，或者换其它线尝试一下！..");
								cm.dispose();
								return;
							}
						}
						cm.itemlaba("【"+ cm.getChar().getName() +"】带着空背包进入了『虚度年华』副本.大吼道:浮云哥(姐)来耶~");
                        em.startInstance(cm.getParty(),cm.getPlayer().getMap());
					}
					cm.dispose();
					return;
                }
			}
			if(typed==24){//冰骑士
				if (cm.getParty() == null) { // No Party
                    cm.sendOk("需要先#b开启#k一个组队,而且只能是你一个人~.zzzZZZZZ..");
                    cm.dispose();
                    return;
                } else if (!cm.isLeader()) { // Not Party Leader
                    cm.sendOk("请叫队长跟我说话.");
                    cm.dispose();
                    return;
                } else {
                    var party = cm.getParty().getMembers();
                    if (party.size() > 1) {
					cm.sendOk("#r对不起,为了彻底的测试你的能力,只能一人前往..");
                    cm.dispose();
                    return;
                    }
                    var em = cm.getEventManager("vipbqs");
					if (em == null) {
						cm.sendOk("此副本出错啦,请联系管管修复吧.");
						cm.dispose();
						return;
					} else {
						if(em.getProperty("entryPossible")!=null){
							if(em.getProperty("entryPossible").equals("1")==true){
								cm.sendOk("#r已经有人在挑战了，请稍等一会，或者换其它线尝试一下！..");
								cm.dispose();
								return;
							}
						}
						if(cm.getChar().getVip()>=5){
							cm.itemlaba("【"+ cm.getChar().getName() +"】开始挑战神级副本『征战冰骑士』我们一起期待光荣归来~");
							em.startInstance(cm.getParty(),cm.getPlayer().getMap());
						}else{
							if(cm.haveItem(4001126,100)==true && cm.haveItem(4002002,1)==true && cm.getChar().gethyd()>=100){
								if(cm.getChar().getBossLog('vipbqs')>0){
									if(cm.getzb()<10000){
										cm.sendOk("#r对不起，您今天已经挑战过一次了，如需要在次挑战必须另外加1万元宝才可以进入..");
										cm.dispose();
										return;
									}
									cm.setzb(-10000);
								}else{
									cm.getChar().delBossLog('vipbqs');
								}
								cm.gainItem(4001126,-100);
								cm.gainItem(4002002,-1);
								cm.getChar().sethyd(-100);
								cm.getChar().setBossLog('vipbqs');
								cm.itemlaba("【"+ cm.getChar().getName() +"】开始挑战神级副本『征战冰骑士』我们一起期待光荣归来~");
								em.startInstance(cm.getParty(),cm.getPlayer().getMap());
							}else{
								cm.sendOk("#r你好像没有足够的活跃点或者枫叶，或者邮票,我不能让你进去..");
							}
						}
                        
					}
					cm.dispose();
					return;
                }	
			}
			if(typed==25){//冰骑士
				if (cm.getParty() == null) { // No Party
                    cm.sendOk("需要先#b开启#k一个组队,而且只能是你一个人~.zzzZZZZZ..");
                    cm.dispose();
                    return;
                } else if (!cm.isLeader()) { // Not Party Leader
                    cm.sendOk("请叫队长跟我说话.");
                    cm.dispose();
                    return;
                } else {
                    var party = cm.getParty().getMembers();
                    if (party.size() > 1) {
					cm.sendOk("#r对不起,为了彻底的测试你的能力,只能一人前往..");
                    cm.dispose();
                    return;
                    }
                    var em = cm.getEventManager("vipsl");
					if (em == null) {
						cm.sendOk("此副本出错啦,请联系管管修复吧.");
						cm.dispose();
						return;
					} else {
						if(em.getProperty("entryPossible")!=null){
							if(em.getProperty("entryPossible").equals("1")==true){
								cm.sendOk("#r已经有人在挑战了，请稍等一会，或者换其它线尝试一下！..");
								cm.dispose();
								return;
							}
						}
						if(cm.getChar().getVip()>=5){
							cm.itemlaba("【"+ cm.getChar().getName() +"】开始挑战神级副本『征战魔幻神龙』我们一起期待光荣归来~");
								em.startInstance(cm.getParty(),cm.getPlayer().getMap());
						}else{
							if(cm.haveItem(4001126,300)==true && cm.haveItem(4002002,2)==true && cm.getChar().gethyd()>=200){
								if(cm.getChar().getBossLog('vipsl')>0){
									if(cm.getzb()<20000){
										cm.sendOk("#r对不起，您今天已经挑战过一次了，如需要在次挑战必须另外加2万元宝才可以进入..");
										cm.dispose();
										return;
									}
									cm.setzb(-20000);
								}else{
									cm.getChar().delBossLog('vipsl');
								}
								cm.gainItem(4001126,-300);
								cm.gainItem(4002002,-2);
								cm.getChar().sethyd(-200);
								cm.getChar().setBossLog('vipsl');
								cm.itemlaba("【"+ cm.getChar().getName() +"】开始挑战神级副本『征战魔幻神龙』我们一起期待光荣归来~");
								em.startInstance(cm.getParty(),cm.getPlayer().getMap());
							}else{
								cm.sendOk("#r你好像没有足够的枫叶或者活跃点，或者木妖邮票,我不能让你进去..");
							}
						}
					}
					cm.dispose();
					return;
                }	
			}
			if(typed==26){//冰骑士
				if (cm.getParty() == null) { // No Party
                    cm.sendOk("需要先#b开启#k一个组队,而且只能是你一个人~.zzzZZZZZ..");
                    cm.dispose();
                    return;
                } else if (!cm.isLeader()) { // Not Party Leader
                    cm.sendOk("请叫队长跟我说话.");
                    cm.dispose();
                    return;
                } else {
                    var party = cm.getParty().getMembers();
                    if (party.size() > 1) {
					cm.sendOk("#r对不起,为了彻底的测试你的能力,只能一人前往..");
                    cm.dispose();
                    return;
                    }
                    var em = cm.getEventManager("vipszw");
					if (em == null) {
						cm.sendOk("此副本出错啦,请联系管管修复吧.");
						cm.dispose();
						return;
					} else {
						if(em.getProperty("entryPossible")!=null){
							if(em.getProperty("entryPossible").equals("1")==true){
								cm.sendOk("#r已经有人在挑战了，请稍等一会，或者换其它线尝试一下！..");
								cm.dispose();
								return;
							}
						}
						if(cm.getChar().getVip()>=5){
							cm.itemlaba("【"+ cm.getChar().getName() +"】开始挑战神级副本『征战狮子王』我们一起期待光荣归来~");
							em.startInstance(cm.getParty(),cm.getPlayer().getMap());
						}else{
							if(cm.haveItem(4001126,300)==true && cm.haveItem(4002002,5)==true && cm.getChar().gethyd()>=200){
								if(cm.getChar().getBossLog('vipszw')>0){
									if(cm.getzb()<30000){
										cm.sendOk("#r对不起，您今天已经挑战过一次了，如需要在次挑战必须另外加3万元宝才可以进入..");
										cm.dispose();
										return;
									}
									cm.setzb(-30000);
								}else{
									cm.getChar().delBossLog('vipszw');
								}
								cm.gainItem(4001126,-300);
								cm.gainItem(4002002,-5);
								cm.getChar().sethyd(-200);
								cm.getChar().setBossLog('vipszw');
								cm.itemlaba("【"+ cm.getChar().getName() +"】开始挑战神级副本『征战狮子王』我们一起期待光荣归来~");
								em.startInstance(cm.getParty(),cm.getPlayer().getMap());
							}else{
								cm.sendOk("#r你好像没有足够的枫叶或者活跃点，或者木妖邮票,我不能让你进去..");
							}
						}
					}
					cm.dispose();
					return;
                }	
			}
		}else if(status == 3){
		}else if(status == 4){	
		}
    }
}

function tzlinjiang(){
    var randx=Math.floor((Math.random()*9))+1;
    if(randx==1){
        cm.gainItem(4031454,2);
        cm.serverNotice("『挑战活动』：【"+ cm.getChar().getName() +"】成功完成了限时秒怪副本.");
        cm.dispose();
    }else if(randx==2){
        cm.gainItem(4001126,200);
        cm.serverNotice("『挑战活动』：【"+ cm.getChar().getName() +"】成功完成了限时秒怪副本.");
        cm.dispose();
    }else if(randx==3){
        cm.gainItem(5390003,20);
        cm.serverNotice("『挑战活动』：【"+ cm.getChar().getName() +"】成功完成了限时秒怪副本.");
        cm.dispose();
    }else if(randx==4){
        cm.gainItem(5390004,20);
        cm.serverNotice("『挑战活动』：【"+ cm.getChar().getName() +"】成功完成了限时秒怪副本.");
        cm.dispose();
    }else if(randx==5){
        cm.gainItem(5010073,1);
        cm.serverNotice("『挑战活动』：【"+ cm.getChar().getName() +"】成功完成了限时秒怪副本.获得了[人气美女]道具.");
        cm.dispose();
    }else if(randx==6){
        cm.gainItem(4004000,20);
        cm.serverNotice("『挑战活动』：【"+ cm.getChar().getName() +"】成功完成了限时秒怪副本.");
        cm.dispose();
    }else if(randx==7){
        cm.gainItem(4004001,20);
        cm.serverNotice("『挑战活动』：【"+ cm.getChar().getName() +"】成功完成了限时秒怪副本.");
        cm.dispose();
    }else if(randx==8){
        cm.gainItem(4004002,20);
        cm.serverNotice("『挑战活动』：【"+ cm.getChar().getName() +"】成功完成了限时秒怪副本.");
        cm.dispose();    
    }else if(randx==10){
        cm.gainItem(4004003,20);
        cm.serverNotice("『挑战活动』：【"+ cm.getChar().getName() +"】成功完成了限时秒怪副本.");
        cm.dispose();  
    }else if(randx==9){
        cm.gainItem(5010074,1);
        cm.serverNotice("『挑战活动』：【"+ cm.getChar().getName() +"】成功完成了限时秒怪副本.获得了[人气帅哥]道具.");
        cm.dispose();
    }
    cm.dispose();
}

