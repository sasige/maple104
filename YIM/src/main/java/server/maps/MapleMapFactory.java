package server.maps;

import constants.BattleConstants;
import constants.GameConstants;
import database.DatabaseConnection;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import server.MaplePortal;
import server.Randomizer;
import server.life.AbstractLoadedMapleLife;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MapleMonsterStats;
import server.life.MapleNPC;
import server.life.SpawnPoint;
import tools.Pair;
import tools.StringUtil;

public class MapleMapFactory {

    private MapleDataProvider source = MapleDataProviderFactory.getDataProvider(new File(new StringBuilder().append(System.getProperty("wzpath")).append("/Map.wz").toString()));
    private MapleData nameData = MapleDataProviderFactory.getDataProvider(new File(new StringBuilder().append(System.getProperty("wzpath")).append("/String.wz").toString())).getData("Map.img");
    private HashMap<Integer, MapleMap> maps = new HashMap();
    private HashMap<Integer, MapleMap> instanceMap = new HashMap();
    private static Map<Integer, List<AbstractLoadedMapleLife>> customLife = new HashMap();
    private ReentrantLock lock = new ReentrantLock();
    private int channel;

    public MapleMapFactory(int channel) {
        this.channel = channel;
    }

    public MapleMap getMap(int mapid) {
        return getMap(mapid, true, true, true);
    }

    public MapleMap getMap(int mapid, boolean respawns, boolean npcs) {
        return getMap(mapid, respawns, npcs, true);
    }

    public MapleMap getMap(int mapid, boolean respawns, boolean npcs, boolean reactors) {
        Integer omapid = Integer.valueOf(mapid);
        MapleMap map = (MapleMap) this.maps.get(omapid);
        if (map == null) {
            this.lock.lock();
            try {
                map = (MapleMap) this.maps.get(omapid);
                if (map != null) {
                    MapleMap localMapleMap1 = map;
                    return localMapleMap1;
                }
                MapleData mapData = null;
                try {
                    mapData = this.source.getData(getMapName(mapid));
                } catch (Exception e) {
                    this.lock.unlock();
                    return null;
                }
                if (mapData == null) {
                    return null;
                }
                MapleData link = mapData.getChildByPath("info/link");
                if (link != null) {
                    mapData = this.source.getData(getMapName(MapleDataTool.getIntConvert("info/link", mapData)));
                }

                float monsterRate = 0.0F;
                if (respawns) {
                    MapleData mobRate = mapData.getChildByPath("info/mobRate");
                    if (mobRate != null) {
                        monsterRate = ((Float) mobRate.getData()).floatValue();
                    }
                }
                map = new MapleMap(mapid, this.channel, MapleDataTool.getInt("info/returnMap", mapData), monsterRate);

                loadPortals(map, mapData.getChildByPath("portal"));
                map.setTop(MapleDataTool.getInt(mapData.getChildByPath("info/VRTop"), 0));
                map.setLeft(MapleDataTool.getInt(mapData.getChildByPath("info/VRLeft"), 0));
                map.setBottom(MapleDataTool.getInt(mapData.getChildByPath("info/VRBottom"), 0));
                map.setRight(MapleDataTool.getInt(mapData.getChildByPath("info/VRRight"), 0));
                List<MapleFoothold> allFootholds = new LinkedList<MapleFoothold>();
                Point lBound = new Point();
                Point uBound = new Point();

                for (MapleData footRoot : mapData.getChildByPath("foothold")) {
                    for (MapleData footCat : footRoot) {
                        for (MapleData footHold : footCat) {
                            MapleFoothold fh = new MapleFoothold(new Point(MapleDataTool.getInt(footHold.getChildByPath("x1"), 0), MapleDataTool.getInt(footHold.getChildByPath("y1"), 0)), new Point(MapleDataTool.getInt(footHold.getChildByPath("x2"), 0), MapleDataTool.getInt(footHold.getChildByPath("y2"), 0)), Integer.parseInt(footHold.getName()));

                            fh.setPrev((short) MapleDataTool.getInt(footHold.getChildByPath("prev"), 0));
                            fh.setNext((short) MapleDataTool.getInt(footHold.getChildByPath("next"), 0));

                            if (fh.getX1() < lBound.x) {
                                lBound.x = fh.getX1();
                            }
                            if (fh.getX2() > uBound.x) {
                                uBound.x = fh.getX2();
                            }
                            if (fh.getY1() < lBound.y) {
                                lBound.y = fh.getY1();
                            }
                            if (fh.getY2() > uBound.y) {
                                uBound.y = fh.getY2();
                            }
                            allFootholds.add(fh);
                        }
                    }
                }
                MapleFootholdTree fTree = new MapleFootholdTree(lBound, uBound);
                for (MapleFoothold foothold : allFootholds) {
                    fTree.insert(foothold);
                }
                map.setFootholds(fTree);
                if (map.getTop() == 0) {
                    map.setTop(lBound.y);
                }
                if (map.getBottom() == 0) {
                    map.setBottom(uBound.y);
                }
                if (map.getLeft() == 0) {
                    map.setLeft(lBound.x);
                }
                if (map.getRight() == 0) {
                    map.setRight(uBound.x);
                }
                int bossid = -1;
                String msg = null;
                if (mapData.getChildByPath("info/timeMob") != null) {
                    bossid = MapleDataTool.getInt(mapData.getChildByPath("info/timeMob/id"), 0);
                    msg = MapleDataTool.getString(mapData.getChildByPath("info/timeMob/message"), null);
                }
                try {
                    Connection con = DatabaseConnection.getConnection();
                    PreparedStatement ps = con.prepareStatement("SELECT * FROM spawns WHERE mid = ?");
                    ps.setInt(1, omapid.intValue());
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        int sqlid = rs.getInt("idd");
                        int sqlf = rs.getInt("f");
                        boolean sqlhide = false;
                        String sqltype = rs.getString("type");
                        int sqlfh = rs.getInt("fh");
                        int sqlcy = rs.getInt("cy");
                        int sqlrx0 = rs.getInt("rx0");
                        int sqlrx1 = rs.getInt("rx1");
                        int sqlx = rs.getInt("x");
                        int sqly = rs.getInt("y");
                        int sqlmobTime = rs.getInt("mobtime");
                        AbstractLoadedMapleLife sqlmyLife = loadLife(sqlid, sqlf, sqlhide, sqlfh, sqlcy, sqlrx0, sqlrx1, sqlx, sqly, sqltype);
                        if (sqltype.equals("n")) {
                            map.addMapObject(sqlmyLife);
                        } else if (sqltype.equals("m")) {
                            MapleMonster monster = (MapleMonster) sqlmyLife;
                            map.addMonsterSpawn(monster, sqlmobTime, (byte) -1, null);
                        }
                    }
                } catch (SQLException e) {
                    System.out.println("读取SQL刷Npc和刷新怪物出错.");
                }

                List herbRocks = new ArrayList();
                int lowestLevel = 200;
                int highestLevel = 0;

                for (MapleData life : mapData.getChildByPath("life")) {
                    String type = MapleDataTool.getString(life.getChildByPath("type"));
                    String limited = MapleDataTool.getString("limitedname", life, "");
                    if (((npcs) || (!type.equals("n"))) && (!limited.equals("Stage0"))) {
                        AbstractLoadedMapleLife myLife = loadLife(life, MapleDataTool.getString(life.getChildByPath("id")), type);

                        if (((myLife instanceof MapleMonster)) && (!BattleConstants.isBattleMap(mapid)) && (!GameConstants.isNoSpawn(mapid))) {
                            MapleMonster mob = (MapleMonster) myLife;

                            herbRocks.add(map.addMonsterSpawn(mob, MapleDataTool.getInt("mobTime", life, 0), (byte) MapleDataTool.getInt("team", life, -1), mob.getId() == bossid ? msg : null).getPosition());
                            if ((mob.getStats().getLevel() > highestLevel) && (!mob.getStats().isBoss())) {
                                highestLevel = mob.getStats().getLevel();
                            }
                            if ((mob.getStats().getLevel() < lowestLevel) && (!mob.getStats().isBoss())) {
                                lowestLevel = mob.getStats().getLevel();
                            }
                        } else if ((myLife instanceof MapleNPC)) {
                            map.addMapObject(myLife);
                        }
                    }
                }
                addAreaBossSpawn(map);
                map.setCreateMobInterval((short) MapleDataTool.getInt(mapData.getChildByPath("info/createMobInterval"), 9000));
                map.setFixedMob(MapleDataTool.getInt(mapData.getChildByPath("info/fixedMobCapacity"), 0));
                map.setPartyBonusRate(GameConstants.getPartyPlay(mapid, MapleDataTool.getInt(mapData.getChildByPath("info/partyBonusR"), 0)));
                map.loadMonsterRate(true);
                map.setNodes(loadNodes(mapid, mapData));

                if ((reactors) && (mapData.getChildByPath("reactor") != null) && (!BattleConstants.isBattleMap(mapid))) {
                    for (MapleData reactor : mapData.getChildByPath("reactor")) {
                        String id = MapleDataTool.getString(reactor.getChildByPath("id"));
                        if (id != null) {
                            map.spawnReactor(loadReactor(reactor, id, (byte) MapleDataTool.getInt(reactor.getChildByPath("f"), 0)));
                        }
                    }
                }
                map.setFirstUserEnter(MapleDataTool.getString(mapData.getChildByPath("info/onFirstUserEnter"), ""));
                map.setUserEnter(mapid == 180000001 ? "jail" : MapleDataTool.getString(mapData.getChildByPath("info/onUserEnter"), ""));
                if ((reactors) && (herbRocks.size() > 0) && (highestLevel >= 30) && (map.getFirstUserEnter().equals("")) && (map.getUserEnter().equals(""))) {
                    List allowedSpawn = new ArrayList(24);
                    allowedSpawn.add(Integer.valueOf(100011));
                    allowedSpawn.add(Integer.valueOf(200011));
                    if (highestLevel >= 100) {
                        for (int i = 0; i < 10; i++) {
                            for (int x = 0; x < 4; x++) {
                                allowedSpawn.add(Integer.valueOf(100000 + i));
                                allowedSpawn.add(Integer.valueOf(200000 + i));
                            }
                        }
                    } else {
                        for (int i = lowestLevel % 10 > highestLevel % 10 ? 0 : lowestLevel % 10; i < highestLevel % 10; i++) {
                            for (int x = 0; x < 4; x++) {
                                allowedSpawn.add(Integer.valueOf(100000 + i));
                                allowedSpawn.add(Integer.valueOf(200000 + i));
                            }
                        }
                    }
                    int numSpawn = Randomizer.nextInt(allowedSpawn.size()) / 6;
                    for (int i = 0; (i < numSpawn) && (!herbRocks.isEmpty()); i++) {
                        int idd = ((Integer) allowedSpawn.get(Randomizer.nextInt(allowedSpawn.size()))).intValue();
                        int theSpawn = Randomizer.nextInt(herbRocks.size());
                        MapleReactor myReactor = new MapleReactor(MapleReactorFactory.getReactor(idd), idd);
                        myReactor.setPosition((Point) herbRocks.get(theSpawn));
                        myReactor.setDelay(idd % 100 == 11 ? 60000 : 5000);
                        map.spawnReactor(myReactor);
                        herbRocks.remove(theSpawn);
                    }
                }
                try {
                    map.setMapName(MapleDataTool.getString("mapName", this.nameData.getChildByPath(getMapStringName(omapid.intValue())), ""));
                    map.setStreetName(MapleDataTool.getString("streetName", this.nameData.getChildByPath(getMapStringName(omapid.intValue())), ""));
                } catch (Exception e) {
                    map.setMapName("");
                    map.setStreetName("");
                }
                map.setClock(mapData.getChildByPath("clock") != null);
                map.setEverlast(MapleDataTool.getInt(mapData.getChildByPath("info/everlast"), 0) > 0);
                map.setTown(MapleDataTool.getInt(mapData.getChildByPath("info/town"), 0) > 0);
                map.setSoaring(MapleDataTool.getInt(mapData.getChildByPath("info/needSkillForFly"), 0) > 0);
                map.setPersonalShop(MapleDataTool.getInt(mapData.getChildByPath("info/personalShop"), 0) > 0);
                map.setForceMove(MapleDataTool.getInt(mapData.getChildByPath("info/lvForceMove"), 0));
                map.setHPDec(MapleDataTool.getInt(mapData.getChildByPath("info/decHP"), 0));
                map.setHPDecInterval(MapleDataTool.getInt(mapData.getChildByPath("info/decHPInterval"), 10000));
                map.setHPDecProtect(MapleDataTool.getInt(mapData.getChildByPath("info/protectItem"), 0));
                map.setForcedReturnMap(mapid == 0 ? 999999999 : MapleDataTool.getInt(mapData.getChildByPath("info/forcedReturn"), 999999999));
                map.setTimeLimit(MapleDataTool.getInt(mapData.getChildByPath("info/timeLimit"), -1));
                map.setFieldLimit(MapleDataTool.getInt(mapData.getChildByPath("info/fieldLimit"), 0));
                map.setRecoveryRate(MapleDataTool.getFloat(mapData.getChildByPath("info/recovery"), 1.0F));
                map.setFixedMob(MapleDataTool.getInt(mapData.getChildByPath("info/fixedMobCapacity"), 0));
                map.setPartyBonusRate(GameConstants.getPartyPlay(mapid, MapleDataTool.getInt(mapData.getChildByPath("info/partyBonusR"), 0)));
                map.setConsumeItemCoolTime(MapleDataTool.getInt(mapData.getChildByPath("info/consumeItemCoolTime"), 0));
                this.maps.put(omapid, map);
            } finally {
                this.lock.unlock();
            }
        }
        return map;
    }

    public MapleMap getInstanceMap(int instanceid) {
        return (MapleMap) this.instanceMap.get(Integer.valueOf(instanceid));
    }

    public void removeInstanceMap(int instanceid) {
        this.lock.lock();
        try {
            if (isInstanceMapLoaded(instanceid)) {
                getInstanceMap(instanceid).checkStates("");
                this.instanceMap.remove(Integer.valueOf(instanceid));
            }
        } finally {
            this.lock.unlock();
        }
    }

    public void removeMap(int instanceid) {
        this.lock.lock();
        try {
            if (isMapLoaded(instanceid)) {
                getMap(instanceid).checkStates("");
                this.maps.remove(Integer.valueOf(instanceid));
            }
        } finally {
            this.lock.unlock();
        }
    }

    public MapleMap CreateInstanceMap(int mapid, boolean respawns, boolean npcs, boolean reactors, int instanceid) {
        this.lock.lock();
        try {
            if (isInstanceMapLoaded(instanceid)) {
                MapleMap localMapleMap1 = getInstanceMap(instanceid);
                return localMapleMap1;
            }
        } finally {
            this.lock.unlock();
        }
        MapleData mapData = null;
        try {
            mapData = this.source.getData(getMapName(mapid));
        } catch (Exception e) {
            return null;
        }
        if (mapData == null) {
            return null;
        }
        MapleData link = mapData.getChildByPath("info/link");
        if (link != null) {
            mapData = this.source.getData(getMapName(MapleDataTool.getIntConvert("info/link", mapData)));
        }

        float monsterRate = 0.0F;
        if (respawns) {
            MapleData mobRate = mapData.getChildByPath("info/mobRate");
            if (mobRate != null) {
                monsterRate = ((Float) mobRate.getData()).floatValue();
            }
        }
        MapleMap map = new MapleMap(mapid, this.channel, MapleDataTool.getInt("info/returnMap", mapData), monsterRate);
        loadPortals(map, mapData.getChildByPath("portal"));
        map.setTop(MapleDataTool.getInt(mapData.getChildByPath("info/VRTop"), 0));
        map.setLeft(MapleDataTool.getInt(mapData.getChildByPath("info/VRLeft"), 0));
        map.setBottom(MapleDataTool.getInt(mapData.getChildByPath("info/VRBottom"), 0));
        map.setRight(MapleDataTool.getInt(mapData.getChildByPath("info/VRRight"), 0));
        List<MapleFoothold> allFootholds = new LinkedList<MapleFoothold>();
        Point lBound = new Point();
        Point uBound = new Point();
        for (MapleData footRoot : mapData.getChildByPath("foothold")) {
            for (MapleData footCat : footRoot) {
                for (MapleData footHold : footCat) {
                    MapleFoothold fh = new MapleFoothold(new Point(MapleDataTool.getInt(footHold.getChildByPath("x1")), MapleDataTool.getInt(footHold.getChildByPath("y1"))), new Point(MapleDataTool.getInt(footHold.getChildByPath("x2")), MapleDataTool.getInt(footHold.getChildByPath("y2"))), Integer.parseInt(footHold.getName()));

                    fh.setPrev((short) MapleDataTool.getInt(footHold.getChildByPath("prev")));
                    fh.setNext((short) MapleDataTool.getInt(footHold.getChildByPath("next")));

                    if (fh.getX1() < lBound.x) {
                        lBound.x = fh.getX1();
                    }
                    if (fh.getX2() > uBound.x) {
                        uBound.x = fh.getX2();
                    }
                    if (fh.getY1() < lBound.y) {
                        lBound.y = fh.getY1();
                    }
                    if (fh.getY2() > uBound.y) {
                        uBound.y = fh.getY2();
                    }
                    allFootholds.add(fh);
                }
            }
        }
        MapleFootholdTree fTree = new MapleFootholdTree(lBound, uBound);
        for (MapleFoothold fh : allFootholds) {
            fTree.insert(fh);
        }
        map.setFootholds(fTree);
        if (map.getTop() == 0) {
            map.setTop(lBound.y);
        }
        if (map.getBottom() == 0) {
            map.setBottom(uBound.y);
        }
        if (map.getLeft() == 0) {
            map.setLeft(lBound.x);
        }
        if (map.getRight() == 0) {
            map.setRight(uBound.x);
        }
        int bossid = -1;
        String msg = null;
        if (mapData.getChildByPath("info/timeMob") != null) {
            bossid = MapleDataTool.getInt(mapData.getChildByPath("info/timeMob/id"), 0);
            msg = MapleDataTool.getString(mapData.getChildByPath("info/timeMob/message"), null);
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM spawns WHERE mid = ?");
            ps.setInt(1, mapid);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int sqlid = rs.getInt("idd");
                int sqlf = rs.getInt("f");
                boolean sqlhide = false;
                String sqltype = rs.getString("type");
                int sqlfh = rs.getInt("fh");
                int sqlcy = rs.getInt("cy");
                int sqlrx0 = rs.getInt("rx0");
                int sqlrx1 = rs.getInt("rx1");
                int sqlx = rs.getInt("x");
                int sqly = rs.getInt("y");
                int sqlmobTime = rs.getInt("mobtime");
                AbstractLoadedMapleLife sqlmyLife = loadLife(sqlid, sqlf, sqlhide, sqlfh, sqlcy, sqlrx0, sqlrx1, sqlx, sqly, sqltype);
                if (sqltype.equals("n")) {
                    map.addMapObject(sqlmyLife);
                } else if (sqltype.equals("m")) {
                    MapleMonster monster = (MapleMonster) sqlmyLife;
                    map.addMonsterSpawn(monster, sqlmobTime, (byte) -1, null);
                }
            }
        } catch (SQLException e) {
            System.out.println("读取SQL刷Npc和刷新怪物出错.");
        }

        for (MapleData life : mapData.getChildByPath("life")) {
            String type = MapleDataTool.getString(life.getChildByPath("type"));
            String limited = MapleDataTool.getString("limitedname", life, "");
            if (((npcs) || (!type.equals("n"))) && (limited.equals(""))) {
                AbstractLoadedMapleLife myLife = loadLife(life, MapleDataTool.getString(life.getChildByPath("id")), type);

                if (((myLife instanceof MapleMonster)) && (!BattleConstants.isBattleMap(mapid)) && (!GameConstants.isNoSpawn(mapid))) {
                    MapleMonster mob = (MapleMonster) myLife;

                    map.addMonsterSpawn(mob, MapleDataTool.getInt("mobTime", life, 0), (byte) MapleDataTool.getInt("team", life, -1), mob.getId() == bossid ? msg : null);
                } else if ((myLife instanceof MapleNPC)) {
                    map.addMapObject(myLife);
                }
            }
        }
        addAreaBossSpawn(map);
        map.setCreateMobInterval((short) MapleDataTool.getInt(mapData.getChildByPath("info/createMobInterval"), 9000));
        map.setFixedMob(MapleDataTool.getInt(mapData.getChildByPath("info/fixedMobCapacity"), 0));
        map.setPartyBonusRate(GameConstants.getPartyPlay(mapid, MapleDataTool.getInt(mapData.getChildByPath("info/partyBonusR"), 0)));
        map.loadMonsterRate(true);
        map.setNodes(loadNodes(mapid, mapData));

        if ((reactors) && (mapData.getChildByPath("reactor") != null) && (!BattleConstants.isBattleMap(mapid))) {
            for (MapleData reactor : mapData.getChildByPath("reactor")) {
                String id = MapleDataTool.getString(reactor.getChildByPath("id"));
                if (id != null) {
                    map.spawnReactor(loadReactor(reactor, id, (byte) MapleDataTool.getInt(reactor.getChildByPath("f"), 0)));
                }
            }
        }
        try {
            map.setMapName(MapleDataTool.getString("mapName", this.nameData.getChildByPath(getMapStringName(mapid)), ""));
            map.setStreetName(MapleDataTool.getString("streetName", this.nameData.getChildByPath(getMapStringName(mapid)), ""));
        } catch (Exception e) {
            map.setMapName("");
            map.setStreetName("");
        }
        map.setClock(MapleDataTool.getInt(mapData.getChildByPath("info/clock"), 0) > 0);
        map.setEverlast(MapleDataTool.getInt(mapData.getChildByPath("info/everlast"), 0) > 0);
        map.setTown(MapleDataTool.getInt(mapData.getChildByPath("info/town"), 0) > 0);
        map.setSoaring(MapleDataTool.getInt(mapData.getChildByPath("info/needSkillForFly"), 0) > 0);
        map.setForceMove(MapleDataTool.getInt(mapData.getChildByPath("info/lvForceMove"), 0));
        map.setHPDec(MapleDataTool.getInt(mapData.getChildByPath("info/decHP"), 0));
        map.setHPDecInterval(MapleDataTool.getInt(mapData.getChildByPath("info/decHPInterval"), 10000));
        map.setHPDecProtect(MapleDataTool.getInt(mapData.getChildByPath("info/protectItem"), 0));
        map.setForcedReturnMap(MapleDataTool.getInt(mapData.getChildByPath("info/forcedReturn"), 999999999));
        map.setTimeLimit(MapleDataTool.getInt(mapData.getChildByPath("info/timeLimit"), -1));
        map.setFieldLimit(MapleDataTool.getInt(mapData.getChildByPath("info/fieldLimit"), 0));
        map.setFirstUserEnter(MapleDataTool.getString(mapData.getChildByPath("info/onFirstUserEnter"), ""));
        map.setUserEnter(MapleDataTool.getString(mapData.getChildByPath("info/onUserEnter"), ""));
        map.setRecoveryRate(MapleDataTool.getFloat(mapData.getChildByPath("info/recovery"), 1.0F));
        map.setConsumeItemCoolTime(MapleDataTool.getInt(mapData.getChildByPath("info/consumeItemCoolTime"), 0));
        map.setInstanceId(instanceid);
        this.lock.lock();
        try {
            this.instanceMap.put(Integer.valueOf(instanceid), map);
        } finally {
            this.lock.unlock();
        }
        return map;
    }

    public int getLoadedMaps() {
        return this.maps.size();
    }

    public boolean isMapLoaded(int mapId) {
        return this.maps.containsKey(Integer.valueOf(mapId));
    }

    public boolean isInstanceMapLoaded(int instanceid) {
        return this.instanceMap.containsKey(Integer.valueOf(instanceid));
    }

    public void clearLoadedMap() {
        this.lock.lock();
        try {
            this.maps.clear();
        } finally {
            this.lock.unlock();
        }
    }

    public List<MapleMap> getAllLoadedMaps() {
        List ret = new ArrayList();
        this.lock.lock();
        try {
            ret.addAll(this.maps.values());
            ret.addAll(this.instanceMap.values());
        } finally {
            this.lock.unlock();
        }
        return ret;
    }

    public Collection<MapleMap> getAllMaps() {
        return this.maps.values();
    }

    private AbstractLoadedMapleLife loadLife(MapleData life, String id, String type) {
        AbstractLoadedMapleLife myLife = MapleLifeFactory.getLife(Integer.parseInt(id), type);
        if (myLife == null) {
            return null;
        }
        myLife.setCy(MapleDataTool.getInt(life.getChildByPath("cy")));
        MapleData dF = life.getChildByPath("f");
        if (dF != null) {
            myLife.setF(MapleDataTool.getInt(dF));
        }
        myLife.setFh(MapleDataTool.getInt(life.getChildByPath("fh")));
        myLife.setRx0(MapleDataTool.getInt(life.getChildByPath("rx0")));
        myLife.setRx1(MapleDataTool.getInt(life.getChildByPath("rx1")));
        myLife.setPosition(new Point(MapleDataTool.getInt(life.getChildByPath("x")), MapleDataTool.getInt(life.getChildByPath("y"))));

        if ((MapleDataTool.getInt("hide", life, 0) == 1) && ((myLife instanceof MapleNPC))) {
            myLife.setHide(true);
        }

        return myLife;
    }

    private AbstractLoadedMapleLife loadLife(int id, int f, boolean hide, int fh, int cy, int rx0, int rx1, int x, int y, String type) {
        AbstractLoadedMapleLife myLife = MapleLifeFactory.getLife(id, type);
        myLife.setCy(cy);
        myLife.setF(f);
        myLife.setFh(fh);
        myLife.setRx0(rx0);
        myLife.setRx1(rx1);
        myLife.setPosition(new Point(x, y));
        myLife.setHide(hide);
        return myLife;
    }

    private MapleReactor loadReactor(MapleData reactor, String id, byte FacingDirection) {
        MapleReactor myReactor = new MapleReactor(MapleReactorFactory.getReactor(Integer.parseInt(id)), Integer.parseInt(id));
        myReactor.setFacingDirection(FacingDirection);
        myReactor.setPosition(new Point(MapleDataTool.getInt(reactor.getChildByPath("x")), MapleDataTool.getInt(reactor.getChildByPath("y"))));
        myReactor.setDelay(MapleDataTool.getInt(reactor.getChildByPath("reactorTime")) * 1000);
        myReactor.setName(MapleDataTool.getString(reactor.getChildByPath("name"), ""));

        return myReactor;
    }

    private String getMapName(int mapid) {
        String mapName = StringUtil.getLeftPaddedStr(Integer.toString(mapid), '0', 9);
        StringBuilder builder = new StringBuilder("Map/Map");
        builder.append(mapid / 100000000);
        builder.append("/");
        builder.append(mapName);
        builder.append(".img");
        mapName = builder.toString();
        return mapName;
    }

    private String getMapStringName(int mapid) {
        StringBuilder builder = new StringBuilder();
        if (mapid < 100000000) {
            builder.append("maple");
        } else if ((mapid >= 100000000) && (mapid < 200000000)) {
            builder.append("victoria");
        } else if ((mapid >= 200000000) && (mapid < 300000000)) {
            builder.append("ossyria");
        } else if ((mapid >= 300000000) && (mapid < 400000000)) {
            builder.append("3rd");
        } else if ((mapid >= 500000000) && (mapid < 510000000)) {
            builder.append("thai");
        } else if ((mapid >= 555000000) && (mapid < 556000000)) {
            builder.append("SG");
        } else if ((mapid >= 540000000) && (mapid < 600000000)) {
            builder.append("SG");
        } else if ((mapid >= 680100000) && (mapid < 681100000)) {
            builder.append("HalloweenGL");
        } else if ((mapid >= 682000000) && (mapid < 683000000)) {
            builder.append("HalloweenGL");
        } else if ((mapid >= 600000000) && (mapid < 670000000)) {
            builder.append("MasteriaGL");
        } else if ((mapid >= 677000000) && (mapid < 678000000)) {
            builder.append("Episode1GL");
        } else if ((mapid >= 670000000) && (mapid < 682000000)) {
            builder.append(GameConstants.GMS ? "weddingGL" : "GL");
        } else if ((mapid >= 683000000) && (mapid < 684000000)) {
            builder.append("event");
        } else if ((mapid >= 684000000) && (mapid < 685000000)) {
            builder.append("event_5th");
        } else if ((mapid >= 686000000) && (mapid < 687000000)) {
            builder.append("event_6th");
        } else if ((mapid >= 687000000) && (mapid < 688000000)) {
            builder.append("Gacha_GL");
        } else if ((mapid >= 689000000) && (mapid <= 689000010)) {
            builder.append("CTF_GL");
        } else if ((mapid >= 689010000) && (mapid <= 689013089)) {
            builder.append("Pink ZakumGL");
        } else if ((mapid >= 700000000) && (mapid < 700000300)) {
            builder.append("wedding");
        } else if ((mapid == 680000700) || (mapid == 680000800)) {
            builder.append("global");
        } else if ((mapid >= 700000000) && (mapid < 741000000)) {
            builder.append("chinese");
        } else if ((mapid >= 702000000) && (mapid < 742000000)) {
            builder.append("CN");
        } else if ((mapid >= 743000000) && (mapid <= 743000700)) {
            builder.append("taiwan");
        } else if ((mapid >= 800000000) && (mapid < 900000000)) {
            builder.append("jp");
        } else {
            builder.append("etc");
        }
        builder.append("/");
        builder.append(mapid);
        return builder.toString();
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    private void addAreaBossSpawn(MapleMap map) {
        int monsterid = -1;
        int mobtime = -1;
        String msg = null;
        boolean shouldSpawn = true;
        Point pos1 = null;
        Point pos2 = null;
        Point pos3 = null;

        switch (map.getId()) {
            case 104010200:
                mobtime = 1200;
                monsterid = 2220000;
                msg = "天气凉快了就会出现红蜗牛王。";
                pos1 = new Point(189, 2);
                pos2 = new Point(478, 250);
                pos3 = new Point(611, 489);
                break;
            case 102020500:
                mobtime = 1200;
                monsterid = 3220000;
                msg = "Stumpy has appeared with a stumping sound that rings the Stone Mountain.";
                pos1 = new Point(1121, 2130);
                pos2 = new Point(483, 2171);
                pos3 = new Point(1474, 1706);
                break;
            case 100020101:
                mobtime = 1200;
                monsterid = 6130101;
                msg = "什么地方出现了巨大的蘑菇。";
                pos1 = new Point(-311, 201);
                pos2 = new Point(-903, 197);
                pos3 = new Point(-568, 196);
                break;
            case 100020301:
                mobtime = 1200;
                monsterid = 8220007;
                msg = "什么地方出现了巨大的蓝色蘑菇。";
                pos1 = new Point(-188, -657);
                pos2 = new Point(625, -660);
                pos3 = new Point(508, -648);
                break;
            case 100020401:
                mobtime = 1200;
                monsterid = 6300005;
                msg = "什么地方出现了笼罩着阴暗气息的巨大蘑菇。";
                pos1 = new Point(-130, -773);
                pos2 = new Point(504, -760);
                pos3 = new Point(608, -641);
                break;
            case 120030500:
                mobtime = 1200;
                monsterid = 5220001;
                msg = "从沙滩里慢慢的走出了一只巨居蟹。";
                pos1 = new Point(-355, 179);
                pos2 = new Point(-1283, -113);
                pos3 = new Point(-571, -593);
                break;
            case 250010304:
                mobtime = 2100;
                monsterid = 7220000;
                msg = "随着微弱的口哨声，肯德熊出现了。";
                pos1 = new Point(-210, 33);
                pos2 = new Point(-234, 393);
                pos3 = new Point(-654, 33);
                break;
            case 200010300:
                mobtime = 1200;
                monsterid = 8220000;
                msg = "艾利杰出现了。";
                pos1 = new Point(665, 83);
                pos2 = new Point(672, -217);
                pos3 = new Point(-123, -217);
                break;
            case 250010503:
                mobtime = 1800;
                monsterid = 7220002;
                msg = "周边的妖气慢慢浓厚，可以听到诡异的猫叫声。";
                pos1 = new Point(-303, 543);
                pos2 = new Point(227, 543);
                pos3 = new Point(719, 543);
                break;
            case 222010310:
                mobtime = 2700;
                monsterid = 7220001;
                msg = "As the moon light dims,a long fox cry can be heard and the presence of the old fox can be felt.";
                pos1 = new Point(-169, -147);
                pos2 = new Point(-517, 93);
                pos3 = new Point(247, 93);
                break;
            case 103030400:
                mobtime = 1800;
                monsterid = 6220000;
                msg = "从沼泽出现了巨大的多尔。";
                pos1 = new Point(-831, 109);
                pos2 = new Point(1525, -75);
                pos3 = new Point(-511, 107);
                break;
            case 101040300:
                mobtime = 1800;
                monsterid = 5220002;
                msg = "蓝雾慢慢散去，浮士德慢慢的显现了出来。";
                pos1 = new Point(600, -600);
                pos2 = new Point(600, -800);
                pos3 = new Point(600, -300);
                break;
            case 220050100:
                mobtime = 1500;
                monsterid = 5220003;
                msg = "Click clock! Timer has appeared with an irregular clock sound.";
                pos1 = new Point(-467, 1032);
                pos2 = new Point(532, 1032);
                pos3 = new Point(-47, 1032);
                break;
            case 221040301:
                mobtime = 2400;
                monsterid = 6220001;
                msg = "Zeno has appeared with a heavy sound of machinery.";
                pos1 = new Point(-4134, 416);
                pos2 = new Point(-4283, 776);
                pos3 = new Point(-3292, 776);
                break;
            case 240040401:
                mobtime = 7200;
                monsterid = 8220003;
                msg = "Leviathan has appeared with a cold wind from over the gorge.";
                pos1 = new Point(-15, 2481);
                pos2 = new Point(127, 1634);
                pos3 = new Point(159, 1142);
                break;
            case 260010201:
                mobtime = 3600;
                monsterid = 3220001;
                msg = "Deo slowly appeared out of the sand dust.";
                pos1 = new Point(-215, 275);
                pos2 = new Point(298, 275);
                pos3 = new Point(592, 275);
                break;
            case 251010102:
                mobtime = 3600;
                monsterid = 5220004;
                msg = "大王蜈蚣出现了。";
                pos1 = new Point(-41, 124);
                pos2 = new Point(-173, 126);
                pos3 = new Point(79, 118);
                break;
            case 261030000:
                mobtime = 2700;
                monsterid = 8220002;
                msg = "Chimera has appeared out of the darkness of the underground with a glitter in her eyes.";
                pos1 = new Point(-1094, -405);
                pos2 = new Point(-772, -116);
                pos3 = new Point(-108, 181);
                break;
            case 230020100:
                mobtime = 2700;
                monsterid = 4220000;
                msg = "在海草中间，出现了奇怪的蛤蚌。";
                pos1 = new Point(-291, -20);
                pos2 = new Point(-272, -500);
                pos3 = new Point(-462, 640);
                break;
            case 103020320:
                mobtime = 1800;
                monsterid = 5090000;
                msg = "在地铁的阴影中出现了什么东西。";
                pos1 = new Point(79, 174);
                pos2 = new Point(-223, 296);
                pos3 = new Point(80, 275);
                break;
            case 103020420:
                mobtime = 1800;
                monsterid = 5090000;
                msg = "在地铁的阴影中出现了什么东西。";
                pos1 = new Point(2241, 301);
                pos2 = new Point(1990, 301);
                pos3 = new Point(1684, 307);
                break;
            case 261020300:
                mobtime = 2700;
                monsterid = 7090000;
                msg = "自动警备系统出现了。";
                pos1 = new Point(312, 157);
                pos2 = new Point(539, 136);
                pos3 = new Point(760, 141);
                break;
            case 261020401:
                mobtime = 2700;
                monsterid = 8090000;
                msg = "迪特和罗伊出现了。";
                pos1 = new Point(-263, 155);
                pos2 = new Point(-436, 122);
                pos3 = new Point(22, 144);
                break;
            case 250020300:
                mobtime = 2700;
                monsterid = 5090001;
                msg = "仙人玩偶出现了。";
                pos1 = new Point(1208, 27);
                pos2 = new Point(1654, 40);
                pos3 = new Point(927, -502);
                break;
            case 211050000:
                mobtime = 2700;
                monsterid = 6090001;
                msg = "雪山魔女出现了。";
                pos1 = new Point(-233, -431);
                pos2 = new Point(-370, -426);
                pos3 = new Point(-526, -420);
                break;
            case 261010003:
                mobtime = 2700;
                monsterid = 6090004;
                msg = "陆陆猫出现了。";
                pos1 = new Point(-861, 301);
                pos2 = new Point(-703, 301);
                pos3 = new Point(-426, 287);
                break;
            case 222010300:
                mobtime = 2700;
                monsterid = 6090003;
                msg = "书生鬼出现了。";
                pos1 = new Point(1300, -400);
                pos2 = new Point(1100, -100);
                pos3 = new Point(1100, 100);
                break;
            case 251010101:
                mobtime = 2700;
                monsterid = 6090002;
                msg = "青竹武士出现了。";
                pos1 = new Point(-15, -449);
                pos2 = new Point(-114, -442);
                pos3 = new Point(-255, -446);
                break;
            case 211041400:
                mobtime = 2700;
                monsterid = 6090000;
                msg = "黑山老妖出现了。";
                pos1 = new Point(1672, 82);
                pos2 = new Point(2071, 10);
                pos3 = new Point(1417, 57);
                break;
            case 105030500:
                mobtime = 2700;
                monsterid = 8130100;
                msg = "蝙蝠怪出现了。";
                pos1 = new Point(1275, -399);
                pos2 = new Point(1254, -412);
                pos3 = new Point(1058, -427);
                break;
            case 105020400:
                mobtime = 2700;
                monsterid = 8220008;
                msg = "出现了一个奇怪的商店。";
                pos1 = new Point(-163, 82);
                pos2 = new Point(958, 107);
                pos3 = new Point(706, -206);
                break;
            case 211040101:
                mobtime = 3600;
                monsterid = 8220001;
                msg = "驮狼雪人出现了。";
                pos1 = new Point(485, 244);
                pos2 = new Point(-60, 249);
                pos3 = new Point(208, 255);
                break;
            case 209000000:
                mobtime = 300;
                monsterid = 9500317;
                msg = "小雪人出现了。";
                pos1 = new Point(-115, 154);
                pos2 = new Point(-115, 154);
                pos3 = new Point(-115, 154);
                break;
            case 677000001:
                mobtime = 60;
                monsterid = 9400612;
                msg = "牛魔王出现了。";
                pos1 = new Point(99, 60);
                pos2 = new Point(99, 60);
                pos3 = new Point(99, 60);
                break;
            case 677000003:
                mobtime = 60;
                monsterid = 9400610;
                msg = "黑暗独角兽出现了。";
                pos1 = new Point(6, 35);
                pos2 = new Point(6, 35);
                pos3 = new Point(6, 35);
                break;
            case 677000005:
                mobtime = 60;
                monsterid = 9400609;
                msg = "印第安老斑鸠出现了。";
                pos1 = new Point(-277, 78);
                pos2 = new Point(547, 86);
                pos3 = new Point(-347, 80);
                break;
            case 677000007:
                mobtime = 60;
                monsterid = 9400611;
                msg = "雪之猫女出现了。";
                pos1 = new Point(117, 73);
                pos2 = new Point(117, 73);
                pos3 = new Point(117, 73);
                break;
            case 677000009:
                mobtime = 60;
                monsterid = 9400613;
                msg = "沃勒福出现了。";
                pos1 = new Point(85, 66);
                pos2 = new Point(85, 66);
                pos3 = new Point(85, 66);
                break;
            case 931000500:
                mobtime = 108000;
                monsterid = 9304005;
                msg = "剑齿豹出现了。";
                pos1 = new Point(-872, -332);
                pos2 = new Point(409, -572);
                pos3 = new Point(-131, 0);
                shouldSpawn = false;
        }

        if (monsterid > 0) {
            map.addAreaMonsterSpawn(MapleLifeFactory.getMonster(monsterid), pos1, pos2, pos3, mobtime, msg, shouldSpawn);
        }
    }

    private void loadPortals(MapleMap map, MapleData port) {
        if (port == null) {
            return;
        }
        int nextDoorPortal = 128;
        for (MapleData portal : port.getChildren()) {
            MaplePortal myPortal = new MaplePortal(MapleDataTool.getInt(portal.getChildByPath("pt")));
            myPortal.setName(MapleDataTool.getString(portal.getChildByPath("pn")));
            myPortal.setTarget(MapleDataTool.getString(portal.getChildByPath("tn")));
            myPortal.setTargetMapId(MapleDataTool.getInt(portal.getChildByPath("tm")));
            myPortal.setPosition(new Point(MapleDataTool.getInt(portal.getChildByPath("x")), MapleDataTool.getInt(portal.getChildByPath("y"))));
            String script = MapleDataTool.getString("script", portal, null);
            if ((script != null) && (script.equals(""))) {
                script = null;
            }
            myPortal.setScriptName(script);

            if (myPortal.getType() == MaplePortal.DOOR_PORTAL) {
                myPortal.setId(nextDoorPortal);
                nextDoorPortal++;
            } else {
                myPortal.setId(Integer.parseInt(portal.getName()));
            }
            map.addPortal(myPortal);
        }
    }

    private MapleNodes loadNodes(int mapid, MapleData mapData) {
        MapleNodes nodeInfo = new MapleNodes(mapid);
        if (mapData.getChildByPath("nodeInfo") != null) {
            for (MapleData node : mapData.getChildByPath("nodeInfo")) {
                try {
                    if (node.getName().equals("start")) {
                        nodeInfo.setNodeStart(MapleDataTool.getInt(node, 0));
                        continue;
                    }
                    List edges = new ArrayList();
                    if (node.getChildByPath("edge") != null) {
                        for (MapleData edge : node.getChildByPath("edge")) {
                            edges.add(Integer.valueOf(MapleDataTool.getInt(edge, -1)));
                        }
                    }
                    MapleNodes.MapleNodeInfo mni = new MapleNodes.MapleNodeInfo(Integer.parseInt(node.getName()), MapleDataTool.getIntConvert("key", node, 0), MapleDataTool.getIntConvert("x", node, 0), MapleDataTool.getIntConvert("y", node, 0), MapleDataTool.getIntConvert("attr", node, 0), edges);

                    nodeInfo.addNode(mni);
                } catch (NumberFormatException e) {
                }
            }
            nodeInfo.sortNodes();
        }
        for (int i = 1; i <= 7; i++) {
            if ((mapData.getChildByPath(String.valueOf(i)) != null) && (mapData.getChildByPath(new StringBuilder().append(i).append("/obj").toString()) != null)) {
                for (MapleData node : mapData.getChildByPath(new StringBuilder().append(i).append("/obj").toString())) {
                    if ((node.getChildByPath("SN_count") != null) && (node.getChildByPath("speed") != null)) {
                        int sn_count = MapleDataTool.getIntConvert("SN_count", node, 0);
                        String name = MapleDataTool.getString("name", node, "");
                        int speed = MapleDataTool.getIntConvert("speed", node, 0);
                        if ((sn_count <= 0) || (speed <= 0) || (name.equals(""))) {
                            continue;
                        }
                        List SN = new ArrayList();
                        for (int x = 0; x < sn_count; x++) {
                            SN.add(Integer.valueOf(MapleDataTool.getIntConvert(new StringBuilder().append("SN").append(x).toString(), node, 0)));
                        }
                        MapleNodes.MaplePlatform mni = new MapleNodes.MaplePlatform(name, MapleDataTool.getIntConvert("start", node, 2), speed, MapleDataTool.getIntConvert("x1", node, 0), MapleDataTool.getIntConvert("y1", node, 0), MapleDataTool.getIntConvert("x2", node, 0), MapleDataTool.getIntConvert("y2", node, 0), MapleDataTool.getIntConvert("r", node, 0), SN);

                        nodeInfo.addPlatform(mni);
                    } else if (node.getChildByPath("tags") != null) {
                        String name = MapleDataTool.getString("tags", node, "");
                        nodeInfo.addFlag(new Pair(name, Integer.valueOf(name.endsWith("3") ? 1 : 0)));
                    }
                }
            }
        }

        if (mapData.getChildByPath("area") != null) {
            for (MapleData area : mapData.getChildByPath("area")) {
                int x1 = MapleDataTool.getInt(area.getChildByPath("x1"));
                int y1 = MapleDataTool.getInt(area.getChildByPath("y1"));
                int x2 = MapleDataTool.getInt(area.getChildByPath("x2"));
                int y2 = MapleDataTool.getInt(area.getChildByPath("y2"));
                Rectangle mapArea = new Rectangle(x1, y1, x2 - x1, y2 - y1);
                nodeInfo.addMapleArea(mapArea);
            }
        }
        if (mapData.getChildByPath("CaptureTheFlag") != null) {
            MapleData mc = mapData.getChildByPath("CaptureTheFlag");
            for (MapleData area : mc) {
                nodeInfo.addGuardianSpawn(new Point(MapleDataTool.getInt(area.getChildByPath("FlagPositionX")), MapleDataTool.getInt(area.getChildByPath("FlagPositionY"))), area.getName().startsWith("Red") ? 0 : 1);
            }
        }
        if (mapData.getChildByPath("directionInfo") != null) {
            MapleData mc = mapData.getChildByPath("directionInfo");
            for (MapleData area : mc) {
                MapleNodes.DirectionInfo di = new MapleNodes.DirectionInfo(Integer.parseInt(area.getName()), MapleDataTool.getInt("x", area, 0), MapleDataTool.getInt("y", area, 0), MapleDataTool.getInt("forcedInput", area, 0) > 0);
                for (MapleData event : area.getChildByPath("eventQ")) {
                    di.eventQ.add(MapleDataTool.getString(event));
                }
                nodeInfo.addDirection(Integer.parseInt(area.getName()), di);
            }
        }
        if (mapData.getChildByPath("monsterCarnival") != null) {
            MapleData mc = mapData.getChildByPath("monsterCarnival");
            if (mc.getChildByPath("mobGenPos") != null) {
                for (MapleData area : mc.getChildByPath("mobGenPos")) {
                    nodeInfo.addMonsterPoint(MapleDataTool.getInt(area.getChildByPath("x")), MapleDataTool.getInt(area.getChildByPath("y")), MapleDataTool.getInt(area.getChildByPath("fh")), MapleDataTool.getInt(area.getChildByPath("cy")), MapleDataTool.getInt("team", area, -1));
                }

            }

            if (mc.getChildByPath("mob") != null) {
                for (MapleData area : mc.getChildByPath("mob")) {
                    nodeInfo.addMobSpawn(MapleDataTool.getInt(area.getChildByPath("id")), MapleDataTool.getInt(area.getChildByPath("spendCP")));
                }
            }
            if (mc.getChildByPath("guardianGenPos") != null) {
                for (MapleData area : mc.getChildByPath("guardianGenPos")) {
                    nodeInfo.addGuardianSpawn(new Point(MapleDataTool.getInt(area.getChildByPath("x")), MapleDataTool.getInt(area.getChildByPath("y"))), MapleDataTool.getInt("team", area, -1));
                }
            }
            if (mc.getChildByPath("skill") != null) {
                for (MapleData area : mc.getChildByPath("skill")) {
                    nodeInfo.addSkillId(MapleDataTool.getInt(area));
                }
            }
        }
        return nodeInfo;
    }
}