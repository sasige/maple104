/*
This is autoExp;
You can stand on this map to get Exp;
the map ID is 910000000;209000000;209000001
made by aexr
*/

var setupTask;

function init() {
    scheduleNew();
}

function scheduleNew() {
    var cal = java.util.Calendar.getInstance();
    cal.set(java.util.Calendar.SECOND, 5);
    var nextTime = cal.getTimeInMillis();
    while (nextTime <= java.lang.System.currentTimeMillis()) {
        nextTime += 1000 * 60 *45; // Every 1 minute
    }
    setupTask = em.scheduleAtTimestamp("start", nextTime);
}

function cancelSchedule() {
    setupTask.cancel(true);
}

function start() {
    scheduleNew();
    em.autoaddFame(1);
    var iter = em.getInstances().iterator();
    while (iter.hasNext()) {
    var eim = iter.next();
    }
}