var Message = new Array(
"游戏内，所有的东西都是要靠自己的努力得到。GM不会给任何玩家任何东西。本服唯一GMqq1989509365", 
"为了庆祝本服新开。充值大给力。各种充值方式都有10-20%的优惠。而且24小时内充值满200元送100满300元送300。满500元送600元满999送1300元消费币", 
"请勿使用任何非法程序：变速齿轮,吸怪,无敌,虚假MISS,飞天,修改WZ,快速过图,修改怪物状态,挂机等外挂,被发现则封号封IP！", 
"发现游戏错误地方(BUG)或游戏漏洞时.请第一时间提交给在线管理.如发现BUG不提交，利用游戏BUG非法获得其物品财产将处于封号处理。对于提交重大BUG的玩家，我们将会给予点券奖励！", 
"使用 @help 命令，可以查看你当前能使用的命令列表。", 
"如果无法和NPC进行对话，请使用 @ea 命令。", 
"在一些常去的城市地图可以点右下角拍卖，选择里面的万能传送，匠人街等等地图。", 
"玩家可以到专业技术地图学习各种生活技能。",
"如果玩家卡在了地图，可以使用@FM回到市场",
"本服新开 给力充值大活动现在办理本服Vip5只需要150元还赠送50000消费币。还送全属性+8888给力装备和其他超值礼包.");

var setupTask;

function init() {
    scheduleNew();
}

function scheduleNew() {
    setupTask = em.schedule("start", 900000);
}

function cancelSchedule() {
    setupTask.cancel(false);
}

function start() {
    scheduleNew();
    em.broadcastServerMsg("[公告事项] " + Message[Math.floor(Math.random() * Message.length)]);
}