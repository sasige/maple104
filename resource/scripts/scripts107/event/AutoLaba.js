var setupTask;

function init() {
    scheduleNew();
}

function scheduleNew() {
    var cal = java.util.Calendar.getInstance();
    cal.set(java.util.Calendar.HOUR, 0);
    cal.set(java.util.Calendar.MINUTE, 50);
    cal.set(java.util.Calendar.SECOND, 0);
    var nextTime = cal.getTimeInMillis();
	
    while (nextTime <= java.lang.System.currentTimeMillis()) {
        nextTime += 1000 * 60;
    }
    		setupTask = em.scheduleAtTimestamp("start", nextTime);
}

function cancelSchedule() {
    setupTask.cancel(true);
}

function start() {
	var cal = java.util.Calendar.getInstance();
	var hour = cal.get(java.util.Calendar.HOUR);
	var min = cal.get(java.util.Calendar.MINUTE);
	var sec = cal.get(java.util.Calendar.SECOND);
	
	scheduleNew();
	if(min == 1){
		//em.startSuperlabaed("带有宝石套装的部队到终结者部落休息，抢夺套装的勇士们可以去市场刘备处进入挑战",5121025);
		em.broadcastServerMsg(5121025,"带有宝石套装的部队到终结者部落休息，抢夺套装的勇士们可以去市场刘备处进入挑战",true);
    	} else if (min == 15) {
		//em.startSuperlabaed("每小时限时副本<击退海盗>在市场刘备处开放了,勇士们快组队去通关吧,奖励丰富哟",5121031);
		em.broadcastServerMsg(5121031,"每小时限时副本<击退海盗>在市场刘备处开放了,勇士们快组队去通关吧,奖励丰富哟",true);
    	} else if (min == 20) {
		//em.startSuperlabaed("每小时副本<限时夺宝>在市场刘备处开放了,谁会先抢到宝藏入场机会呢？",5121031);
		em.broadcastServerMsg(5121031,"每小时副本<限时夺宝>在市场刘备处开放了,谁会先抢到宝藏入场机会呢？",true);
    	} else if (min == 30) {
		//em.startSuperlabaed("答题活动开始了，喜欢答题的玩家可以到市场的诸葛孔明处答题",5121031);
		em.broadcastServerMsg(5121031,"答题活动开始了，喜欢答题的玩家可以到市场的诸葛孔明处答题",true);
    	} else if (min == 31) {
		//em.startSuperlabaed("带有铂金套装的部队到终结者部落休息，抢夺套装的勇士们可以去市场刘备处进入挑战",5121025);
		em.broadcastServerMsg(5121025,"带有铂金套装的部队到终结者部落休息，抢夺套装的勇士们可以去市场刘备处进入挑战",true);
    	} else if (min == 35) {
		//em.startSuperlabaed("每小时副本<挑战粉扎>在市场刘备处开放了,勇士们快组队征服它吧..",5121010);
		em.broadcastServerMsg(5121010,"每小时副本<挑战粉扎>在市场刘备处开放了,勇士们快组队征服它吧..",true);
	}
    
}
