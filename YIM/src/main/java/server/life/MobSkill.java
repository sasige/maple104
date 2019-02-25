package server.life;

import client.MapleCharacter;
import client.MapleDisease;
import client.status.MonsterStatus;
import constants.GameConstants;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import scripting.EventInstanceManager;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.MapleMist;
import tools.MaplePacketCreator;

public class MobSkill {

    private int skillId;
    private int skillLevel;
    private int mpCon;
    private int spawnEffect;
    private int hp;
    private int x;
    private int y;
    private long duration;
    private long cooltime;
    private float prop;
    private short limit;
    private List<Integer> toSummon = new ArrayList();
    private Point lt;
    private Point rb;
    private boolean summonOnce;

    public MobSkill(int skillId, int level) {
        skillId = skillId;
        skillLevel = level;
    }

    public void setOnce(boolean o) {
        summonOnce = o;
    }

    public boolean onlyOnce() {
        return summonOnce;
    }

    public void setMpCon(int mpCon) {
        mpCon = mpCon;
    }

    public void addSummons(List<Integer> toSummon) {
        toSummon = toSummon;
    }

    public void setSpawnEffect(int spawnEffect) {
        spawnEffect = spawnEffect;
    }

    public void setHp(int hp) {
        hp = hp;
    }

    public void setX(int x) {
        x = x;
    }

    public void setY(int y) {
        y = y;
    }

    public void setDuration(long duration) {
        duration = duration;
    }

    public void setCoolTime(long cooltime) {
        cooltime = cooltime;
    }

    public void setProp(float prop) {
        prop = prop;
    }

    public void setLtRb(Point lt, Point rb) {
        lt = lt;
        rb = rb;
    }

    public void setLimit(short limit) {
        limit = limit;
    }

    public boolean checkCurrentBuff(MapleCharacter player, MapleMonster monster) {
        boolean stop = false;
        switch (skillId) {
            case 100:
            case 110:
            case 150:
                stop = monster.isBuffed(MonsterStatus.物攻提升);
                break;
            case 101:
            case 111:
            case 151:
                stop = monster.isBuffed(MonsterStatus.魔攻提升);
                break;
            case 102:
            case 112:
            case 152:
                stop = monster.isBuffed(MonsterStatus.物防提升);
                break;
            case 103:
            case 113:
            case 153:
                stop = monster.isBuffed(MonsterStatus.魔防提升);
                break;
            case 140:
            case 141:
            case 142:
            case 143:
            case 144:
            case 145:
                stop = (monster.isBuffed(MonsterStatus.免疫伤害)) || (monster.isBuffed(MonsterStatus.免疫魔攻)) || (monster.isBuffed(MonsterStatus.免疫物攻));
                break;
            case 200:
                stop = player.getMap().getNumMonsters() >= limit;
        }

        stop |= monster.isBuffed(MonsterStatus.魔击无效);
        return stop;
    }

    public void applyEffect(MapleCharacter player, MapleMonster monster, boolean skill) {
        MapleDisease disease = MapleDisease.getBySkill(skillId);
        Map stats = new EnumMap(MonsterStatus.class);
        List reflection = new LinkedList();

        switch (skillId) {
            case 100:
            case 110:
            case 150:
                stats.put(MonsterStatus.物攻提升, Integer.valueOf(x));
                break;
            case 101:
            case 111:
            case 151:
                stats.put(MonsterStatus.魔攻提升, Integer.valueOf(x));
                break;
            case 102:
            case 112:
            case 152:
                stats.put(MonsterStatus.物防提升, Integer.valueOf(x));
                break;
            case 103:
            case 113:
            case 153:
                stats.put(MonsterStatus.魔防提升, Integer.valueOf(x));
                break;
            case 154:
                stats.put(MonsterStatus.命中, Integer.valueOf(x));
                break;
            case 155:
                stats.put(MonsterStatus.回避, Integer.valueOf(x));
                break;
            case 115:
            case 156:
                stats.put(MonsterStatus.速度, Integer.valueOf(x));
                break;
            case 157:
                stats.put(MonsterStatus.封印, Integer.valueOf(x));
                break;
            case 114:
                int hps;
                if ((lt != null) && (rb != null) && (skill) && (monster != null)) {
                    List<MapleMapObject> objects = getObjectsInRange(monster, MapleMapObjectType.MONSTER);
                    hps = getX() / 1000 * (int) (950.0D + 1050.0D * Math.random());
                    for (MapleMapObject mons : objects) {
                        ((MapleMonster) mons).heal(hps, getY(), true);
                    }
                } else {
                    if (monster == null) {
                        break;
                    }
                    monster.heal(getX(), getY(), true);
                }
                break;
            case 105:
                if ((lt != null) && (rb != null) && (skill) && (monster != null)) {
                    List<MapleMapObject> objects = getObjectsInRange(monster, MapleMapObjectType.MONSTER);
                    for (MapleMapObject mons : objects) {
                        if (mons.getObjectId() != monster.getObjectId()) {
                            player.getMap().killMonster((MapleMonster) mons, player, true, false, (byte) 1, 0);
                            monster.heal(getX(), getY(), true);
                            break;
                        }
                    }
                } else {
                    if (monster == null) {
                        break;
                    }
                    monster.heal(getX(), getY(), true);
                }
                break;
            case 127:
                if ((lt != null) && (rb != null) && (skill) && (monster != null) && (player != null)) {
                    for (MapleCharacter character : getPlayersInRange(monster, player)) {
                        character.dispel();
                    }
                } else {
                    if (player == null) {
                        break;
                    }
                    player.dispel();
                }
                break;
            case 129:
                if ((monster == null) || (monster.getMap().getSquadByMap() != null) || ((monster.getEventInstance() != null) && (monster.getEventInstance().getName().indexOf("BossQuest") != -1))) {
                    break;
                }
                BanishInfo info = monster.getStats().getBanishInfo();
                if (info != null) {
                    if ((lt != null) && (rb != null) && (skill) && (player != null)) {
                        for (MapleCharacter chr : getPlayersInRange(monster, player)) {
                            if (!chr.hasBlockedInventory()) {
                                chr.changeMapBanish(info.getMap(), info.getPortal(), info.getMsg());
                            }
                        }
                    } else if ((player != null) && (!player.hasBlockedInventory())) {
                        player.changeMapBanish(info.getMap(), info.getPortal(), info.getMsg());
                    }
                }
                break;
            case 131:
                if (monster == null) {
                    break;
                }
                monster.getMap().spawnMist(new MapleMist(calculateBoundingBox(monster.getTruePosition(), true), monster, this), x * 10, false);
                break;
            case 140:
                stats.put(MonsterStatus.免疫物攻, Integer.valueOf(x));
                break;
            case 141:
                stats.put(MonsterStatus.免疫魔攻, Integer.valueOf(x));
                break;
            case 142:
                stats.put(MonsterStatus.免疫伤害, Integer.valueOf(x));
                break;
            case 143:
                stats.put(MonsterStatus.反射物攻, Integer.valueOf(x));
                stats.put(MonsterStatus.免疫物攻, Integer.valueOf(x));
                reflection.add(Integer.valueOf(x));
                if (monster == null) {
                    break;
                }
                monster.getMap().broadcastMessage(MaplePacketCreator.spouseMessage(10, "[系统提示] 注意 " + monster.getStats().getName() + " 即将开启反射物攻状态。"));
                break;
            case 144:
                stats.put(MonsterStatus.反射魔攻, Integer.valueOf(x));
                stats.put(MonsterStatus.免疫魔攻, Integer.valueOf(x));
                reflection.add(Integer.valueOf(x));
                if (monster == null) {
                    break;
                }
                monster.getMap().broadcastMessage(MaplePacketCreator.spouseMessage(10, "[系统提示] 注意 " + monster.getStats().getName() + " 即将开启反射魔攻状态。"));
                break;
            case 145:
                stats.put(MonsterStatus.反射物攻, Integer.valueOf(x));
                stats.put(MonsterStatus.免疫物攻, Integer.valueOf(x));
                stats.put(MonsterStatus.反射魔攻, Integer.valueOf(x));
                stats.put(MonsterStatus.免疫魔攻, Integer.valueOf(x));
                reflection.add(x);
                reflection.add(x);
                if (monster == null) {
                    break;
                }
                monster.getMap().broadcastMessage(MaplePacketCreator.spouseMessage(10, "[系统提示] 注意 " + monster.getStats().getName() + " 即将开启反射物攻和魔攻状态。"));
                break;
            case 200:
                if (monster == null) {
                    return;
                }
                for (Integer mobId : getSummons()) {
                    MapleMonster toSpawn = null;
                    try {
                        toSpawn = MapleLifeFactory.getMonster(GameConstants.getCustomSpawnID(monster.getId(), mobId));
                    } catch (RuntimeException e) {
                        continue;
                    }


                    if (toSpawn == null) {
                        continue;
                    }
                    toSpawn.setPosition(monster.getTruePosition());
                    int ypos = (int) monster.getTruePosition().getY();
                    int xpos = (int) monster.getTruePosition().getX();
                    switch (mobId) {
                        case 8500003:
                            toSpawn.setFh((int) Math.ceil(Math.random() * 19.0D));
                            ypos = -590;
                            break;
                        case 8500004:
                            xpos = (int) (monster.getTruePosition().getX() + Math.ceil(Math.random() * 1000.0D) - 500.0D);
                            ypos = (int) monster.getTruePosition().getY();
                            break;
                        case 8510100:
                            if (Math.ceil(Math.random() * 5.0D) == 1.0D) {
                                ypos = 78;
                                xpos = (int) (0.0D + Math.ceil(Math.random() * 5.0D)) + (Math.ceil(Math.random() * 2.0D) == 1.0D ? 180 : 0);
                            } else {
                                xpos = (int) (monster.getTruePosition().getX() + Math.ceil(Math.random() * 1000.0D) - 500.0D);
                            }
                            break;
                        case 8820007:
                            break;
                        default:
                            switch (monster.getMap().getId()) {
                                case 220080001:
                                    if (xpos < -890) {
                                        xpos = (int) (-890.0D + Math.ceil(Math.random() * 150.0D));
                                    } else {
                                        if (xpos <= 230) {
                                            break;
                                        }
                                        xpos = (int) (230.0D - Math.ceil(Math.random() * 150.0D));
                                    }
                                    break;
                                case 230040420:
                                    if (xpos < -239) {
                                        xpos = (int) (-239.0D + Math.ceil(Math.random() * 150.0D));
                                    } else {
                                        if (xpos <= 371) {
                                            break;
                                        }
                                        xpos = (int) (371.0D - Math.ceil(Math.random() * 150.0D));
                                    }
                            }

                            monster.getMap().spawnMonsterWithEffect(toSpawn, getSpawnEffect(), monster.getMap().calcPointBelow(new Point(xpos, ypos - 1)));
                    }
                }
                break;
            case 104:
            case 106:
            case 107:
            case 108:
            case 109:
            case 116:
            case 117:
            case 118:
            case 119:
            case 120:
            case 121:
            case 122:
            case 123:
            case 124:
            case 125:
            case 126:
            case 128:
            case 130:
            case 132:
            case 133:
            case 134:
            case 135:
            case 136:
            case 137:
            case 138:
            case 139:
            case 146:
            case 147:
            case 148:
            case 149:
            case 158:
            case 159:
            case 160:
            case 161:
            case 162:
            case 163:
            case 164:
            case 165:
            case 166:
            case 167:
            case 168:
            case 169:
            case 170:
            case 171:
            case 172:
            case 173:
            case 174:
            case 175:
            case 176:
            case 177:
            case 178:
            case 179:
            case 180:
            case 181:
            case 182:
            case 183:
            case 184:
            case 185:
            case 186:
            case 187:
            case 188:
            case 189:
            case 190:
            case 191:
            case 192:
            case 193:
            case 194:
            case 195:
            case 196:
            case 197:
            case 198:
            case 199:
            default:
                if (disease != null) {
                    break;
                }
                System.out.println("未处理的怪物技能 skillid : " + skillId);
        }

        if ((stats.size() > 0) && (monster != null)) {
            if ((lt != null) && (rb != null) && (skill)) {
                for (MapleMapObject mons : getObjectsInRange(monster, MapleMapObjectType.MONSTER)) {
                    ((MapleMonster) mons).applyMonsterBuff(stats, getSkillId(), getDuration(), this, reflection);
                }
            } else {
                monster.applyMonsterBuff(stats, getSkillId(), getDuration(), this, reflection);
            }
        }
        if ((disease != null) && (player != null)) {
            if ((lt != null) && (rb != null) && (skill) && (monster != null)) {
                for (MapleCharacter chr : getPlayersInRange(monster, player)) {
                    chr.giveDebuff(disease, this);
                }
            } else {
                player.giveDebuff(disease, this);
            }
        }
        if (monster != null) {
            monster.setMp(monster.getMp() - getMpCon());
        }
    }

    public int getSkillId() {
        return skillId;
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    public int getMpCon() {
        return mpCon;
    }

    public List<Integer> getSummons() {
        return Collections.unmodifiableList(toSummon);
    }

    public int getSpawnEffect() {
        return spawnEffect;
    }

    public int getHP() {
        return hp;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public long getDuration() {
        return duration;
    }

    public long getCoolTime() {
        return cooltime;
    }

    public Point getLt() {
        return lt;
    }

    public Point getRb() {
        return rb;
    }

    public int getLimit() {
        return limit;
    }

    public boolean makeChanceResult() {
        return (prop >= 1.0D) || (Math.random() < prop);
    }

    private Rectangle calculateBoundingBox(Point posFrom, boolean facingLeft) {
        Point myrb, mylt;
        if (facingLeft) {
            mylt = new Point(lt.x + posFrom.x, lt.y + posFrom.y);
            myrb = new Point(rb.x + posFrom.x, rb.y + posFrom.y);
        } else {
            myrb = new Point(lt.x * -1 + posFrom.x, rb.y + posFrom.y);
            mylt = new Point(rb.x * -1 + posFrom.x, lt.y + posFrom.y);
        }
        Rectangle bounds = new Rectangle(mylt.x, mylt.y, myrb.x - mylt.x, myrb.y - mylt.y);
        return bounds;
    }

    private List<MapleCharacter> getPlayersInRange(MapleMonster monster, MapleCharacter player) {
        Rectangle bounds = calculateBoundingBox(monster.getTruePosition(), monster.isFacingLeft());
        List players = new ArrayList();
        players.add(player);
        return monster.getMap().getPlayersInRectAndInList(bounds, players);
    }

    private List<MapleMapObject> getObjectsInRange(MapleMonster monster, MapleMapObjectType objectType) {
        Rectangle bounds = calculateBoundingBox(monster.getTruePosition(), monster.isFacingLeft());
        List objectTypes = new ArrayList();
        objectTypes.add(objectType);
        return monster.getMap().getMapObjectsInRect(bounds, objectTypes);
    }
}