package server.achievement;

import java.util.LinkedHashMap;
import java.util.Map;

public class MapleAchievements {

    private Map<Integer, MapleAchievement> achievements = new LinkedHashMap();
    private static MapleAchievements instance = new MapleAchievements();

    protected MapleAchievements() {
        this.achievements.put(Integer.valueOf(1), new MapleAchievement("首次通过传送门", 100, 0, 0, true));
        this.achievements.put(Integer.valueOf(2), new MapleAchievement("首次等级达到30级", 300, 0, 300000, true));
        this.achievements.put(Integer.valueOf(3), new MapleAchievement("首次等级达到70级", 500, 0, 500000, true));
        this.achievements.put(Integer.valueOf(4), new MapleAchievement("首次等级达到120级", 1000, 0, 1000000, true));
        this.achievements.put(Integer.valueOf(5), new MapleAchievement("首次等级达到200级", 2000, 0, 2000000, true));
        this.achievements.put(Integer.valueOf(7), new MapleAchievement("首次人气达到50点", 1000, 0, 0, true));
        this.achievements.put(Integer.valueOf(9), new MapleAchievement("首次穿戴重生装备", 300, 0, 0, true));
        this.achievements.put(Integer.valueOf(10), new MapleAchievement("首次穿戴永恒装备", 500, 0, 0, true));
        this.achievements.put(Integer.valueOf(11), new MapleAchievement("说喜欢我们的游戏", 200, 0, 0, true));
        this.achievements.put(Integer.valueOf(12), new MapleAchievement("首次击败BOSS女老板", 500, 0, 0, true));
        this.achievements.put(Integer.valueOf(13), new MapleAchievement("首次击败帕普拉图斯", 500, 0, 0, true));
        this.achievements.put(Integer.valueOf(14), new MapleAchievement("首次击败皮亚奴斯", 500, 0, 0, true));
        this.achievements.put(Integer.valueOf(15), new MapleAchievement("首次击败扎昆", 1000, 0, 0, true));
        this.achievements.put(Integer.valueOf(16), new MapleAchievement("首次击败暗黑龙王", 2000, 0, 0, true));
        this.achievements.put(Integer.valueOf(17), new MapleAchievement("首次击败时间的宠儿－品克缤", 3000, 0, 0, true));
        this.achievements.put(Integer.valueOf(18), new MapleAchievement("首次杀死1个BOSS", 200, 0, 0, true));
        this.achievements.put(Integer.valueOf(19), new MapleAchievement("首次完成活动任务 'OX Quiz'", 600, 0, 0, true));
        this.achievements.put(Integer.valueOf(20), new MapleAchievement("首次完成活动任务 'MapleFitness'", 600, 0, 0, true));
        this.achievements.put(Integer.valueOf(21), new MapleAchievement("首次完成活动任务 'Ola Ola'", 600, 0, 0, true));
        this.achievements.put(Integer.valueOf(22), new MapleAchievement("defeating BossQuest HELL mode", 600, 0, 0, true));
        this.achievements.put(Integer.valueOf(23), new MapleAchievement("首次击败进阶扎昆", 3000, 0, 0, true));
        this.achievements.put(Integer.valueOf(24), new MapleAchievement("首次击败进阶暗黑龙王", 3000, 0, 0, true));
        this.achievements.put(Integer.valueOf(25), new MapleAchievement("首次完成活动任务 'Survival Challenge'", 600, 0, 0, true));
        this.achievements.put(Integer.valueOf(26), new MapleAchievement("首次攻击超过 10000 点", 200, 0, 0, true));
        this.achievements.put(Integer.valueOf(27), new MapleAchievement("首次攻击超过 50000 点", 300, 0, 0, true));
        this.achievements.put(Integer.valueOf(28), new MapleAchievement("首次攻击超过 100000 点", 400, 0, 0, true));
        this.achievements.put(Integer.valueOf(29), new MapleAchievement("首次攻击超过 500000 点", 500, 0, 0, true));
        this.achievements.put(Integer.valueOf(30), new MapleAchievement("首次攻击达到 999999 点", 1000, 0, 0, true));
        this.achievements.put(Integer.valueOf(31), new MapleAchievement("首次拥有 1 000 000 金币", 200, 0, 0, true));
        this.achievements.put(Integer.valueOf(32), new MapleAchievement("首次拥有 10 000 000 金币", 400, 0, 0, true));
        this.achievements.put(Integer.valueOf(33), new MapleAchievement("首次拥有 100 000 000 金币", 600, 0, 0, true));
        this.achievements.put(Integer.valueOf(34), new MapleAchievement("首次拥有 1 000 000 000 金币", 800, 0, 0, true));
        this.achievements.put(Integer.valueOf(35), new MapleAchievement("首次成功创建家族", 800, 0, 0, true));
        this.achievements.put(Integer.valueOf(36), new MapleAchievement("首次成功创建学院", 600, 0, 0, true));
        this.achievements.put(Integer.valueOf(37), new MapleAchievement("首次完成1个组队任务", 600, 0, 0, true));
        this.achievements.put(Integer.valueOf(38), new MapleAchievement("首次击败班·雷昂", 2500, 0, 0, true));
        this.achievements.put(Integer.valueOf(39), new MapleAchievement("首次击败希纳斯", 3000, 0, 0, true));
        this.achievements.put(Integer.valueOf(40), new MapleAchievement("首次穿戴130级装备", 800, 0, 0, true));
        this.achievements.put(Integer.valueOf(41), new MapleAchievement("首次穿戴140级装备", 1200, 0, 0, true));
        this.achievements.put(Integer.valueOf(42), new MapleAchievement("首次砸卷成功", 300, 0, 0, true));
        this.achievements.put(Integer.valueOf(43), new MapleAchievement("首次鉴定装备", 300, 0, 0, true));
        this.achievements.put(Integer.valueOf(44), new MapleAchievement("首次加星成功", 300, 0, 0, true));
        this.achievements.put(Integer.valueOf(45), new MapleAchievement("首次装备达到10星", 3000, 0, 0, true));
        this.achievements.put(Integer.valueOf(46), new MapleAchievement("首次领袖达到60级", 800, 0, 0, true));
        this.achievements.put(Integer.valueOf(47), new MapleAchievement("首次洞察达到60级", 800, 0, 0, true));
        this.achievements.put(Integer.valueOf(48), new MapleAchievement("首次意志达到60级", 800, 0, 0, true));
        this.achievements.put(Integer.valueOf(49), new MapleAchievement("首次手技达到60级", 800, 0, 0, true));
        this.achievements.put(Integer.valueOf(50), new MapleAchievement("首次感性达到60级", 800, 0, 0, true));
        this.achievements.put(Integer.valueOf(51), new MapleAchievement("首次魅力达到60级", 800, 0, 0, true));
        this.achievements.put(Integer.valueOf(52), new MapleAchievement("首次鉴定出 A 级装备", 300, 0, 300000, true));
        this.achievements.put(Integer.valueOf(53), new MapleAchievement("首次鉴定出 S 级装备", 600, 0, 600000, true));
        this.achievements.put(Integer.valueOf(54), new MapleAchievement("首次鉴定出 SS 级装备", 1200, 0, 1200000, true));
    }

    public static MapleAchievements getInstance() {
        return instance;
    }

    public MapleAchievement getById(int id) {
        return (MapleAchievement) this.achievements.get(Integer.valueOf(id));
    }

    public Integer getByMapleAchievement(MapleAchievement ma) {
        for (Map.Entry achievement : this.achievements.entrySet()) {
            if (achievement.getValue() == ma) {
                return (Integer) achievement.getKey();
            }
        }
        return null;
    }
}