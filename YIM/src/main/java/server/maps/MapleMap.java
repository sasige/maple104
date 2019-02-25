package server.maps;

import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import client.MonsterBook;
import client.MonsterFamiliar;
import client.PlayerStats;
import client.Skill;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleAndroid;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.messages.commands.DonatorCommand.item;
import client.status.MonsterStatus;
import client.status.MonsterStatusEffect;
import constants.GameConstants;
import database.DatabaseConnection;
import handling.channel.ChannelServer;
import handling.world.MapleParty;
import handling.world.PartyOperation;
import handling.world.World.Broadcast;
import handling.world.exped.ExpeditionType;
import handling.world.sidekick.MapleSidekick;
import handling.world.sidekick.MapleSidekickCharacter;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.PrintStream;
import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import scripting.EventInstanceManager;
import scripting.EventManager;
import scripting.EventScriptManager;
import server.*;
import server.MapleCarnivalFactory.MCSkill;
import server.MapleSquad.MapleSquadType;
import server.Timer;
import server.Timer.EtcTimer;
import server.Timer.MapTimer;
import server.Timer.WorldTimer;
import server.events.MapleEvent;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MapleMonsterInformationProvider;
import server.life.MapleMonsterStats;
import server.life.MapleNPC;
import server.life.MobSkill;
import server.life.MonsterDropEntry;
import server.life.MonsterGlobalDropEntry;
import server.life.SpawnPoint;
import server.life.SpawnPointAreaBoss;
import server.life.Spawns;
import tools.FileoutputUtil;
import tools.MaplePacketCreator;
import tools.Pair;
import tools.StringUtil;
import tools.Triple;
import tools.packet.AndroidPacket;
import tools.packet.MobPacket;
import tools.packet.PartyPacket;
import tools.packet.PetPacket;
import tools.packet.UIPacket;

public final class MapleMap {

    private final Map<MapleMapObjectType, LinkedHashMap<Integer, MapleMapObject>> mapobjects;
    private Map<Integer, MapleMapObject> mapobjectss = new LinkedHashMap<Integer, MapleMapObject>();
    private final Map<MapleMapObjectType, ReentrantReadWriteLock> mapobjectlocks;
    private final List<MapleCharacter> characters = new ArrayList();
    private final ReentrantReadWriteLock charactersLock = new ReentrantReadWriteLock();
    private int runningOid = 500000;
    private final Lock runningOidLock = new ReentrantLock();
    private final List<Spawns> monsterSpawn = new ArrayList();
    private final AtomicInteger spawnedMonstersOnMap = new AtomicInteger(0);
    private final Map<Integer, MaplePortal> portals = new HashMap();
    private MapleFootholdTree footholds = null;
    private float monsterRate;
    private float recoveryRate;
    private MapleMapEffect mapEffect;
    private byte channel;
    private short decHP = 0;
    private short createMobInterval = 9000;
    private short top = 0;
    private short bottom = 0;
    private short left = 0;
    private short right = 0;
    private int consumeItemCoolTime = 0;
    private int protectItem = 0;
    private int decHPInterval = 10000;
    private int mapid;
    private int returnMapId;
    private int timeLimit;
    private int fieldLimit;
    private int maxRegularSpawn = 0;
    private int fixedMob;
    private int forcedReturnMap = 999999999;
    private int instanceid = -1;
    private int lvForceMove = 0;
    private int lvLimit = 0;
    private int permanentWeather = 0;
    private int partyBonusRate = 0;
    private int dropLife = 180000; //Time in milliseconds drops last before disappearing
    private boolean town;
    private boolean clock;
    private boolean personalShop;
    private boolean everlast = false;
    private boolean dropsDisabled = false;
    private boolean gDropsDisabled = false;
    private boolean soaring = false;
    private boolean squadTimer = false;
    private boolean isSpawns = true;
    private boolean checkStates = true;
    private String mapName;
    private String streetName;
    private String onUserEnter;
    private String onFirstUserEnter;
    private String speedRunLeader = "";
    private List<Integer> dced = new ArrayList();
    private ScheduledFuture<?> squadSchedule;
    private long speedRunStart = 0L;
    private long lastSpawnTime = 0L;
    private long lastHurtTime = 0L;
    private MapleNodes nodes;
    
    private MapleCharacter chrs;
    
    private MapleSquad.MapleSquadType squad;
    private Map<String, Integer> environment = new LinkedHashMap();

    public MapleMap(int mapid, int channel, int returnMapId, float monsterRate) {
        this.mapid = mapid;
        this.channel = (byte) channel;
        this.returnMapId = returnMapId;
        if (this.returnMapId == 999999999) {
            this.returnMapId = mapid;
        }
        if (GameConstants.getPartyPlay(mapid) > 0) {
            this.monsterRate = ((monsterRate - 1.0F) * 2.5F + 1.0F);
        } else {
            this.monsterRate = monsterRate;
        }
        EnumMap objsMap = new EnumMap(MapleMapObjectType.class);
        EnumMap objlockmap = new EnumMap(MapleMapObjectType.class);
        for (MapleMapObjectType type : MapleMapObjectType.values()) {
            objsMap.put(type, new LinkedHashMap());
            objlockmap.put(type, new ReentrantReadWriteLock());
        }
        this.mapobjects = Collections.unmodifiableMap(objsMap);
        this.mapobjectlocks = Collections.unmodifiableMap(objlockmap);
    }

    public void setSpawns(boolean fm) {
        this.isSpawns = fm;
    }

    public boolean getSpawns() {
        return this.isSpawns;
    }

    public void setFixedMob(int fm) {
        this.fixedMob = fm;
    }

    public void setForceMove(int fm) {
        this.lvForceMove = fm;
    }

    public int getForceMove() {
        return this.lvForceMove;
    }

    public void setLevelLimit(int fm) {
        this.lvLimit = fm;
    }

    public int getLevelLimit() {
        return this.lvLimit;
    }

    public void setReturnMapId(int rmi) {
        this.returnMapId = rmi;
    }

    public void setSoaring(boolean b) {
        this.soaring = b;
    }

    public boolean canSoar() {
        return this.soaring;
    }

    public void toggleDrops() {
        this.dropsDisabled = (!this.dropsDisabled);
    }

    public void setDrops(boolean b) {
        this.dropsDisabled = b;
    }

    public void toggleGDrops() {
        this.gDropsDisabled = (!this.gDropsDisabled);
    }

    public int getId() {
        return this.mapid;
    }

    public MapleMap getReturnMap() {
        return ChannelServer.getInstance(this.channel).getMapFactory().getMap(this.returnMapId);
    }

    public int getReturnMapId() {
        return this.returnMapId;
    }

    public int getForcedReturnId() {
        return this.forcedReturnMap;
    }

    public MapleMap getForcedReturnMap() {
        return ChannelServer.getInstance(this.channel).getMapFactory().getMap(this.forcedReturnMap);
    }

    public void setForcedReturnMap(int map) {
        this.forcedReturnMap = map;
    }

    public float getRecoveryRate() {
        return this.recoveryRate;
    }

    public void setRecoveryRate(float recoveryRate) {
        this.recoveryRate = recoveryRate;
    }

    public int getFieldLimit() {
        return this.fieldLimit;
    }

    public void setFieldLimit(int fieldLimit) {
        this.fieldLimit = fieldLimit;
    }

    public void setCreateMobInterval(short createMobInterval) {
        this.createMobInterval = createMobInterval;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String getMapName() {
        return this.mapName;
    }

    public String getStreetName() {
        return this.streetName;
    }

    public void setFirstUserEnter(String onFirstUserEnter) {
        this.onFirstUserEnter = onFirstUserEnter;
    }

    public void setUserEnter(String onUserEnter) {
        this.onUserEnter = onUserEnter;
    }

    public String getFirstUserEnter() {
        return this.onFirstUserEnter;
    }

    public String getUserEnter() {
        return this.onUserEnter;
    }

    public boolean hasClock() {
        return this.clock;
    }

    public void setClock(boolean hasClock) {
        this.clock = hasClock;
    }

    public boolean isTown() {
        return this.town;
    }

    public void setTown(boolean town) {
        this.town = town;
    }

    public boolean allowPersonalShop() {
        return this.personalShop;
    }

    public void setPersonalShop(boolean personalShop) {
        this.personalShop = personalShop;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public void setEverlast(boolean everlast) {
        this.everlast = everlast;
    }

    public boolean getEverlast() {
        return this.everlast;
    }

    public int getHPDec() {
        return this.decHP;
    }

    public void setHPDec(int delta) {
        if ((delta > 0) || (this.mapid == 749040100)) {
            this.lastHurtTime = System.currentTimeMillis();
        }
        this.decHP = (short) delta;
    }

    public int getHPDecInterval() {
        return this.decHPInterval;
    }

    public void setHPDecInterval(int delta) {
        this.decHPInterval = delta;
    }

    public int getHPDecProtect() {
        return this.protectItem;
    }

    public void setHPDecProtect(int delta) {
        this.protectItem = delta;
    }

    public int getCurrentPartyId() {
        this.charactersLock.readLock().lock();
        try {
            Iterator ltr = this.characters.iterator();

            while (ltr.hasNext()) {
                MapleCharacter chr = (MapleCharacter) ltr.next();
                if (chr.getParty() != null) {
                    int i = chr.getParty().getId();
                    return i;
                }
            }
        } finally {
            this.charactersLock.readLock().unlock();
        }
        return -1;
    }

    public void addMapObject(MapleMapObject mapobject) {
        this.runningOidLock.lock();
        int newOid;
        try {
            newOid = ++this.runningOid;
        } finally {
            this.runningOidLock.unlock();
        }

        mapobject.setObjectId(newOid);

        ((ReentrantReadWriteLock) this.mapobjectlocks.get(mapobject.getType())).writeLock().lock();
        try {
            ((LinkedHashMap) this.mapobjects.get(mapobject.getType())).put(Integer.valueOf(newOid), mapobject);
        } finally {
            ((ReentrantReadWriteLock) this.mapobjectlocks.get(mapobject.getType())).writeLock().unlock();
        }
    }

    private void spawnAndAddRangedMapObject(MapleMapObject mapobject, DelayedPacketCreation packetbakery) {
        addMapObject(mapobject);

        this.charactersLock.readLock().lock();
        try {
            Iterator itr = this.characters.iterator();

            while (itr.hasNext()) {
                MapleCharacter chr = (MapleCharacter) itr.next();
                if ((!chr.isClone()) && ((mapobject.getType() == MapleMapObjectType.MIST) || (chr.getTruePosition().distanceSq(mapobject.getTruePosition()) <= GameConstants.maxViewRangeSq()))) {
                    packetbakery.sendPackets(chr.getClient());
                    chr.addVisibleMapObject(mapobject);
                }
            }
        } finally {
            this.charactersLock.readLock().unlock();
        }
    }

    public void removeMapObject(MapleMapObject obj) {
        ((ReentrantReadWriteLock) this.mapobjectlocks.get(obj.getType())).writeLock().lock();
        try {
            ((LinkedHashMap) this.mapobjects.get(obj.getType())).remove(Integer.valueOf(obj.getObjectId()));
        } finally {
            ((ReentrantReadWriteLock) this.mapobjectlocks.get(obj.getType())).writeLock().unlock();
        }
    }

    public Point calcPointBelow(Point initial) {
        MapleFoothold fh = this.footholds.findBelow(initial);
        if (fh == null) {
            return null;
        }
        int dropY = fh.getY1();
        if ((!fh.isWall()) && (fh.getY1() != fh.getY2())) {
            double s1 = Math.abs(fh.getY2() - fh.getY1());
            double s2 = Math.abs(fh.getX2() - fh.getX1());
            if (fh.getY2() < fh.getY1()) {
                dropY = fh.getY1() - (int) (Math.cos(Math.atan(s2 / s1)) * (Math.abs(initial.x - fh.getX1()) / Math.cos(Math.atan(s1 / s2))));
            } else {
                dropY = fh.getY1() + (int) (Math.cos(Math.atan(s2 / s1)) * (Math.abs(initial.x - fh.getX1()) / Math.cos(Math.atan(s1 / s2))));
            }
        }
        return new Point(initial.x, dropY);
    }

    public Point calcDropPos(Point initial, Point fallback) {
        Point ret = calcPointBelow(new Point(initial.x, initial.y - 50));
        if (ret == null) {
            return fallback;
        }
        return ret;
    }

    private void dropFromMonster(MapleCharacter chr, MapleMonster mob, boolean instanced) {
        if ((mob == null) || (chr == null) || (ChannelServer.getInstance(this.channel) == null) || (this.dropsDisabled) || (mob.dropsDisabled()) || (chr.getPyramidSubway() != null)) {
            return;
        }

        if ((!instanced) && (((LinkedHashMap) this.mapobjects.get(MapleMapObjectType.ITEM)).size() >= 250)) {
            removeDrops();
        }

        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        byte droptype = (byte) (chr.getParty() != null ? 1 : mob.getStats().isFfaLoot() ? 2 : mob.getStats().isExplosiveReward() ? 3 : 0);
        int mobpos = mob.getTruePosition().x;
        int cmServerrate = ChannelServer.getInstance(this.channel).getMesoRate();
        int chServerrate = ChannelServer.getInstance(this.channel).getDropRate();
        int caServerrate = ChannelServer.getInstance(this.channel).getCashRate();

        byte d = 1;
        Point pos = new Point(0, mob.getTruePosition().y);
        double showdown = 100.0D;
        MonsterStatusEffect mse = mob.getBuff(MonsterStatus.挑衅);
        if (mse != null) {
            showdown += mse.getX().intValue();
        }

        MapleMonsterInformationProvider mi = MapleMonsterInformationProvider.getInstance();
        List derp = mi.retrieveDrop(mob.getId());
        if (derp == null) {
            return;
        }
        List<MonsterDropEntry> dropEntry = new ArrayList<MonsterDropEntry>(derp);
        Collections.shuffle(dropEntry);

        boolean mesoDropped = false;
        for (MonsterDropEntry de : dropEntry) {
            if (de.itemId == mob.getStolen()) {
                continue;
            }
            if (Randomizer.nextInt(999999) < (int) (de.chance * chServerrate * chr.getDropMod() * (chr.getStat().dropBuff / 100.0D) * (showdown / 100.0D))) {
                if (((mesoDropped) && (droptype != 3) && (de.itemId == 0))
                        || (de.itemId / 10000 == 238)) {
                    continue;
                }
                if (droptype == 3) {
                    pos.x = (mobpos + (d % 2 == 0 ? 40 * (d + 1) / 2 : -(40 * (d / 2))));
                } else {
                    pos.x = (mobpos + (d % 2 == 0 ? 25 * (d + 1) / 2 : -(25 * (d / 2))));
                }
                if (de.itemId == 0) {
                    int mesos = Randomizer.nextInt(1 + Math.abs(de.Maximum - de.Minimum)) + de.Minimum;
                    if (mesos > 0) {
                        
                     //   if (chrs.getMaplewing("jinbi") == 0) {
                        spawnMobMesoDrop((int) (mesos * (chr.getStat().mesoBuff / 100.0D) * chr.getDropMod() * cmServerrate), calcDropPos(pos, mob.getTruePosition()), mob, chr, false, droptype);
                        mesoDropped = true;
                      //  } else {
                        //    chrs.gainMeso((int) (mesos * (chr.getStat().mesoBuff / 100.0D) * chr.getDropMod() * cmServerrate), true, true);
                        
                      //  }
                        
                    }
                } else {
                    Item idrop;
                    if (GameConstants.getInventoryType(de.itemId) == MapleInventoryType.EQUIP) {
                        idrop = ii.randomizeStats((Equip) ii.getEquipById(de.itemId));
                    } else {
                        int range = Math.abs(de.Maximum - de.Minimum);
                        idrop = new Item(de.itemId, (short) 0, (short) (de.Maximum != 1 ? Randomizer.nextInt(range <= 0 ? 1 : range) + de.Minimum : 1), (short) 0);
                    }
                    idrop.setGMLog(new StringBuilder().append("怪物掉落: ").append(mob.getId()).append(" 地图: ").append(this.mapid).append(" 时间: ").append(FileoutputUtil.CurrentReadable_Date()).toString());
                    if (GameConstants.isNoticeItem(de.itemId) || GameConstants.神奇魔方(de.itemId)) {
                        broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append("<公告信息> 恭喜 ").append(chr.getName()).append(" 在 ").append(chr.getMap().getMapName()).append(" 杀死 ").append(mob.getStats().getName()).append(" 掉落道具 ").append(ii.getName(de.itemId)).toString()));
                    }
                    spawnMobDrop(idrop, calcDropPos(pos, mob.getTruePosition()), mob, chr, droptype, de.questid);
                }
                d = (byte) (d + 1);
            }
        }
        List<MonsterGlobalDropEntry> globalEntry = new ArrayList<MonsterGlobalDropEntry>(mi.getGlobalDrop());
        Collections.shuffle(globalEntry);
        int cashz = ((mob.getStats().isBoss()) && (mob.getStats().getHPDisplayType() == 0) ? 20 : 1) * caServerrate;
        int cashModifier = (int) (mob.getStats().isBoss() ? mob.getStats().isPartyBonus() ? mob.getMobExp() / 1000 : 0 : mob.getMobExp() / 1000 + mob.getMobMaxHp() / 20000L);

        for (MonsterGlobalDropEntry de : globalEntry) {
            if ((Randomizer.nextInt(999999) < de.chance) && ((de.continent < 0) || ((de.continent < 10) && (this.mapid / 100000000 == de.continent)) || ((de.continent < 100) && (this.mapid / 10000000 == de.continent)) || ((de.continent < 1000) && (this.mapid / 1000000 == de.continent)))) {
                if ((de.itemId == 0) && (caServerrate != 0)) {
                    int giveCash = (int) ((Randomizer.nextInt(cashz) + cashz + cashModifier) * (chr.getStat().cashBuff / 100.0D) * chr.getCashMod());
                    if (giveCash > 0) {
                        chr.modifyCSPoints(2, giveCash, true);
                    }
                } else if (!this.gDropsDisabled) {
                    if (droptype == 3) {
                        pos.x = (mobpos + (d % 2 == 0 ? 40 * (d + 1) / 2 : -(40 * (d / 2))));
                    } else {
                        pos.x = (mobpos + (d % 2 == 0 ? 25 * (d + 1) / 2 : -(25 * (d / 2))));
                    }
                    Item idrop;
                    if (GameConstants.getInventoryType(de.itemId) == MapleInventoryType.EQUIP) {
                        idrop = ii.randomizeStats((Equip) ii.getEquipById(de.itemId));
                    } else {
                        idrop = new Item(de.itemId, (short) 0, (short) (de.Maximum != 1 ? Randomizer.nextInt(de.Maximum - de.Minimum) + de.Minimum : 1), (short) 0);
                    }
                    idrop.setGMLog(new StringBuilder().append("怪物掉落: ").append(mob.getId()).append(" 地图: ").append(this.mapid).append(" (Global) 时间: ").append(FileoutputUtil.CurrentReadable_Date()).toString());
                    spawnMobDrop(idrop, calcDropPos(pos, mob.getTruePosition()), mob, chr, de.onlySelf ? 0 : droptype, de.questid);
                    d = (byte) (d + 1);
                }
            }
        }
    }

    public void removeMonster(MapleMonster monster) {
        if (monster == null) {
            return;
        }
        this.spawnedMonstersOnMap.decrementAndGet();
        broadcastMessage(MobPacket.killMonster(monster.getObjectId(), 0));
        removeMapObject(monster);
        monster.killed();
    }

    public void killMonster(MapleMonster monster) {
        if (monster == null) {
            return;
        }
        this.spawnedMonstersOnMap.decrementAndGet();
        monster.setHp(0L);
        if (monster.getLinkCID() <= 0) {
            monster.spawnRevives(this);
        }
        broadcastMessage(MobPacket.killMonster(monster.getObjectId(), monster.getStats().getSelfD() < 0 ? 1 : monster.getStats().getSelfD()));
        removeMapObject(monster);
        monster.killed();
    }

    public void killMonster(MapleMonster monster, MapleCharacter chr, boolean withDrops, boolean second, byte animation) {
        killMonster(monster, chr, withDrops, second, animation, 0);
    }

    public final void killMonster(final MapleMonster monster, final MapleCharacter chr, boolean withDrops, boolean second, byte animation, int lastSkill) {
        if (((monster.getId() == 8810122) || (monster.getId() == 8810018)) && (!second)) {
            MapTimer.getInstance().schedule(new Runnable() {

                public void run() {
                    killMonster(monster, chr, true, true, (byte) 1);
                    killAllMonsters(true);
                }
            }, 3000L);

            return;
        }
        if (monster.getId() == 8820014) {
            killMonster(8820000);
        } else if (monster.getId() == 9300166) {
            animation = 4;
        }
        this.spawnedMonstersOnMap.decrementAndGet();
        removeMapObject(monster);
        monster.killed();
        MapleSquad sqd = getSquadByMap();
        boolean instanced = (sqd != null) || (monster.getEventInstance() != null) || (getEMByMap() != null);
        int dropOwner = monster.killBy(chr, lastSkill);
        if (animation >= 0) {
            broadcastMessage(MobPacket.killMonster(monster.getObjectId(), animation));
        }

        if (monster.getBuffToGive() > -1) {
            int buffid = monster.getBuffToGive();
            MapleStatEffect buff = MapleItemInformationProvider.getInstance().getItemEffect(buffid);

            this.charactersLock.readLock().lock();
            try {
                for (MapleCharacter mc : this.characters) {
                    if (mc.isAlive()) {
                        buff.applyTo(mc);

                        switch (monster.getId()) {
                            case 8810018:
                            case 8810122:
                            case 8820001:
                                mc.getClient().getSession().write(MaplePacketCreator.showOwnBuffEffect(buffid, 13, mc.getLevel(), 1));
                                broadcastMessage(mc, MaplePacketCreator.showBuffeffect(mc.getId(), buffid, 13, mc.getLevel(), 1), false);
                        }
                    }
                }
            } finally {
                this.charactersLock.readLock().unlock();
            }
        }
        int mobid = monster.getId();
        ExpeditionType type = null;
        if ((mobid == 8810018) && (this.mapid == 240060200)) {
            Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, "经过无数次的挑战，终于击破了暗黑龙王的远征队！你们才是龙之林的真正英雄~"));
            this.charactersLock.readLock().lock();
            try {
                for (MapleCharacter c : this.characters) {
                    c.finishAchievement(16);
                }
            } finally {
                this.charactersLock.readLock().unlock();
            }

            if (this.speedRunStart > 0L) {
                type = ExpeditionType.Horntail;
            }
            doShrine(true);
        } else if ((mobid == 8810122) && (this.mapid == 240060201)) {
            Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, "经过无数次的挑战，终于击破了进阶暗黑龙王的远征队！你们才是龙之林的真正英雄~"));
            this.charactersLock.readLock().lock();
            try {
                for (MapleCharacter c : this.characters) {
                    c.finishAchievement(24);
                }
            } finally {
                this.charactersLock.readLock().unlock();
            }

            if (this.speedRunStart > 0L) {
                type = ExpeditionType.ChaosHT;
            }
            doShrine(true);
        } else if ((mobid == 9400266) && (this.mapid == 802000111)) {
            doShrine(true);
        } else if ((mobid == 9400265) && (this.mapid == 802000211)) {
            doShrine(true);
        } else if ((mobid == 9400270) && (this.mapid == 802000411)) {
            doShrine(true);
        } else if ((mobid == 9400273) && (this.mapid == 802000611)) {
            doShrine(true);
        } else if ((mobid == 9400294) && (this.mapid == 802000711)) {
            doShrine(true);
        } else if ((mobid == 9400296) && (this.mapid == 802000803)) {
            doShrine(true);
        } else if ((mobid == 9400289) && (this.mapid == 802000821)) {
            doShrine(true);
        } else if ((mobid == 8830000) && (this.mapid == 105100300)) {
            if (this.speedRunStart > 0L) {
                type = ExpeditionType.Normal_Balrog;
            }
        } else if (((mobid == 9420544) || (mobid == 9420549)) && (this.mapid == 551030200) && (monster.getEventInstance() != null) && (monster.getEventInstance().getName().contains(getEMByMap().getName()))) {
            doShrine(getAllReactor().isEmpty());
        } else if ((mobid == 8820001) && (this.mapid == 270050100)) {
            Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, "经过无数次的挑战，终于击破了时间的宠儿－品克缤的远征队！你们才是时间神殿的真正英雄~"));
            this.charactersLock.readLock().lock();
            try {
                for (MapleCharacter c : this.characters) {
                    c.finishAchievement(17);
                }
            } finally {
                this.charactersLock.readLock().unlock();
            }
            if (this.speedRunStart > 0L) {
                type = ExpeditionType.Pink_Bean;
            }
            doShrine(true);
        } else if (((mobid == 8850011) && (this.mapid == 271040200)) || ((mobid == 8850012) && (this.mapid == 271040100))) {
            Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, "被黑魔法师黑化的希纳斯女皇终于被打倒! 混沌世界得以净化!"));
            this.charactersLock.readLock().lock();
            try {
                for (MapleCharacter c : this.characters) {
                    c.finishAchievement(39);
                }
            } finally {
                this.charactersLock.readLock().unlock();
            }
            if (this.speedRunStart > 0L) {
                type = ExpeditionType.Cygnus;
            }
            doShrine(true);
        } else if ((mobid == 8840000) && (this.mapid == 211070100)) {
            this.charactersLock.readLock().lock();
            try {
                for (MapleCharacter c : this.characters) {
                    c.finishAchievement(38);
                }
            } finally {
                this.charactersLock.readLock().unlock();
            }
            if (this.speedRunStart > 0L) {
                type = ExpeditionType.Von_Leon;
            }
            doShrine(true);
            //终极挑战
        } else if (((mobid == 8850012) || (mobid == 8850011)) && (this.mapid == 703200400)) {
            this.charactersLock.readLock().lock();
            try {
                for (MapleCharacter c : this.characters) {
                    c.finishAchievement(38);
                }
            } finally {
                this.charactersLock.readLock().unlock();
            }
            if (this.speedRunStart > 0L) {
                type = ExpeditionType.AllBoss;
            }
            doShrine(true);
            
            //阿卡伊勒
        } else if (((mobid == 8860001) || (mobid == 8860010)) && (this.mapid == 272020200)) {
            this.charactersLock.readLock().lock();
            try {
                for (MapleCharacter c : this.characters) {
                    c.finishAchievement(38);
                }
            } finally {
                this.charactersLock.readLock().unlock();
            }
            if (this.speedRunStart > 0L) {
                type = ExpeditionType.Akayile;
            }
            doShrine(true);
            
        } else if ((mobid == 8800002) && (this.mapid == 280030000)) {
            this.charactersLock.readLock().lock();
            try {
                for (MapleCharacter c : this.characters) {
                    c.finishAchievement(15);
                }
            } finally {
                this.charactersLock.readLock().unlock();
            }

            if (this.speedRunStart > 0L) {
                type = ExpeditionType.Zakum;
            }
            doShrine(true);
        } else if ((mobid == 8800102) && (this.mapid == 280030001)) {
            this.charactersLock.readLock().lock();
            try {
                for (MapleCharacter c : this.characters) {
                    c.finishAchievement(23);
                }
            } finally {
                this.charactersLock.readLock().unlock();
            }

            if (this.speedRunStart > 0L) {
                type = ExpeditionType.Chaos_Zakum;
            }
            doShrine(true);
        } else if ((mobid >= 8800003) && (mobid <= 8800010)) {
            boolean makeZakReal = true;
            Collection<MapleMonster> monsters = getAllMonstersThreadsafe();

            for (MapleMonster mons : monsters) {
                if ((mons.getId() >= 8800003) && (mons.getId() <= 8800010)) {
                    makeZakReal = false;
                    break;
                }
            }
            if (makeZakReal) {
                for (Object object : monsters) {
                    MapleMonster mons = (MapleMonster) object;
                    if (mons.getId() == 8800000) {
                        Point pos = mons.getTruePosition();
                        killAllMonsters(true);
                        spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8800000), pos);
                        break;
                    }
                }
            }
        } else if ((mobid >= 8800103) && (mobid <= 8800110)) {
            boolean makeZakReal = true;
            Collection<MapleMonster> monsters = getAllMonstersThreadsafe();

            for (MapleMonster mons : monsters) {
                if ((mons.getId() >= 8800103) && (mons.getId() <= 8800110)) {
                    makeZakReal = false;
                    break;
                }
            }
            if (makeZakReal) {
                for (MapleMonster mons : monsters) {
                    if (mons.getId() == 8800100) {
                        Point pos = mons.getTruePosition();
                        killAllMonsters(true);
                        spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8800100), pos);
                        break;
                    }
                }
            }
        } else if ((mobid >= 9400903) && (mobid <= 9400910)) {
            boolean makeZakReal = true;
            Collection<MapleMonster> monsters = getAllMonstersThreadsafe();

            for (MapleMonster mons : monsters) {
                if ((mons.getId() >= 9400903) && (mons.getId() <= 9400910)) {
                    makeZakReal = false;
                    break;
                }
            }
            if (makeZakReal) {
                for (MapleMonster mons : monsters) {
                    if (mons.getId() == 9400900) {
                        Point pos = mons.getTruePosition();
                        killAllMonsters(true);
                        spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9400900), pos);
                        break;
                    }
                }
            }
        } else if (mobid == 8820008) {
            for (MapleMapObject mmo : getAllMonstersThreadsafe()) {
                MapleMonster mons = (MapleMonster) mmo;
                if (mons.getLinkOid() != monster.getObjectId()) {
                    killMonster(mons, chr, false, false, animation);
                }
            }
        } else if ((mobid >= 8820010) && (mobid <= 8820014)) {
            for (MapleMapObject mmo : getAllMonstersThreadsafe()) {
                MapleMonster mons = (MapleMonster) mmo;
                if ((mons.getId() != 8820000) && (mons.getId() != 8820001) && (mons.getObjectId() != monster.getObjectId()) && (mons.isAlive()) && (mons.getLinkOid() == monster.getObjectId())) {
                    killMonster(mons, chr, false, false, animation);
                }
            }
        } else if ((mobid / 100000 == 98) && (chr.getMapId() / 10000000 == 95) && (getAllMonstersThreadsafe().isEmpty())) {
            switch (chr.getMapId() % 1000 / 100) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                    chr.getClient().getSession().write(UIPacket.MapEff("monsterPark/clear"));
                    break;
                case 5:
                    if (chr.getMapId() / 1000000 == 952) {
                        chr.getClient().getSession().write(UIPacket.MapEff("monsterPark/clearF"));
                    } else {
                        chr.getClient().getSession().write(UIPacket.MapEff("monsterPark/clear"));
                    }
                    break;
                case 6:
                    chr.getClient().getSession().write(UIPacket.MapEff("monsterPark/clearF"));
            }
        }
        //杀死BOSS奖励贡献点
        if (GameConstants.isBOSS(mobid)) {
        chr.杀死BOSS给奖励("maple", 1, mobid);
        }
        //委托任务 消灭指定怪物
        if (Boolean.parseBoolean(ServerProperties.getProperty("world.委托任务"))) {
            
        int rw1 = chr.取得玩家委托任务信息("renwu1");
        int mob1 = chr.取得玩家委托任务信息("mobid1");
        boolean nmob1 = chr.取得玩家委托任务信息("mob1") < chr.取得委托任务信息("nmob", rw1);
        
        if ((mob1 == mobid) && (mob1 > 0) && (nmob1 == true) && ((this.mapid == chr.取得委托任务信息("map", rw1)) || (chr.取得委托任务信息("map", rw1) == 0))) {
            chr.增加玩家委托任务信息("mob1", 1);
        } else {
            if (nmob1 == false && rw1 > 0 && mob1 > 0) {
                chr.dropMessage(-11, "委托任务1需要打猎的怪物数量已经完成！");
            }
            int rw2 = chr.取得玩家委托任务信息("renwu2");
            int mob2 = chr.取得玩家委托任务信息("mobid2");
            boolean nmob2 = chr.取得玩家委托任务信息("mob2") < chr.取得委托任务信息("nmob", rw2);
            if ((mob2 == mobid) && (mob2 > 0) && (nmob2 == true) && ((this.mapid == chr.取得委托任务信息("map", rw2)) || (chr.取得委托任务信息("map", rw2) == 0))) {
                chr.增加玩家委托任务信息("mob2", 1);
            } else {
                if (nmob2 == false && rw2 > 0 && mob2 > 0) {
                   chr.dropMessage(-11, "委托任务2需要打猎的怪物数量已经完成！");
                }
                int rw3 = chr.取得玩家委托任务信息("renwu3");
                int mob3 = chr.取得玩家委托任务信息("mobid3");
                boolean nmob3 = chr.取得玩家委托任务信息("mob3") < chr.取得委托任务信息("nmob", rw3);
                if ((mob3 == mobid) && (mob3 > 0) && (nmob3 == true) && ((this.mapid == chr.取得委托任务信息("map", rw3)) || (chr.取得委托任务信息("map", rw3) == 0))) {
                    chr.增加玩家委托任务信息("mob3", 1);
                } else {
                    if (nmob3 == false && rw3 > 0 && mob3 > 0) {
                       chr.dropMessage(-11, "委托任务3需要打猎的怪物数量已经完成！");
                    }
                    int rw4 = chr.取得玩家委托任务信息("renwu4");
                    int mob4 = chr.取得玩家委托任务信息("mobid4");
                    boolean nmob4 = chr.取得玩家委托任务信息("mob4") < chr.取得委托任务信息("nmob", rw4);
                    if ((mob4 == mobid) && (mob4 > 0) && (nmob4 == true) && ((this.mapid == chr.取得委托任务信息("map", rw4)) || (chr.取得委托任务信息("map", rw4) == 0))) {
                        chr.增加玩家委托任务信息("mob4", 1);
                    } else {
                        if (nmob4 == false && rw4 > 0 && mob4 > 0) {
                           chr.dropMessage(-11, "委托任务4需要打猎的怪物数量已经完成！");
                        }
                        int rw5 = chr.取得玩家委托任务信息("renwu5");
                        int mob5 = chr.取得玩家委托任务信息("mobid5");
                        boolean nmob5 = chr.取得玩家委托任务信息("mob5") < chr.取得委托任务信息("nmob", rw5);
                        if ((mob5 == mobid) && (mob5 > 0) && (nmob5 == true) && ((this.mapid == chr.取得委托任务信息("map", rw5)) || (chr.取得委托任务信息("map", rw5) == 0))) {
                             chr.增加玩家委托任务信息("mob5", 1);
                        } else {
                            if (nmob5 == false && rw5 > 0 && mob5 > 0) {
                               chr.dropMessage(-11, "委托任务5需要打猎的怪物数量已经完成！");
                            }
                            int rw6 = chr.取得玩家委托任务信息("renwu6");
                            int mob6 = chr.取得玩家委托任务信息("mobid6");
                            boolean nmob6 = chr.取得玩家委托任务信息("mob6") < chr.取得委托任务信息("nmob", rw6);
                            if ((mob6 == mobid) && (mob6 > 0) && (nmob6 == true) && ((this.mapid == chr.取得委托任务信息("map", rw6)) || (chr.取得委托任务信息("map", rw6) == 0))) {
                                chr.增加玩家委托任务信息("mob6", 1);
                            } else {
                                if (nmob6 == false && rw6 > 0 && mob6 > 0) {
                                    chr.dropMessage(-11, "委托任务6需要打猎的怪物数量已经完成！");
                                }
                                int rw7 = chr.取得玩家委托任务信息("renwu7");
                                int mob7 = chr.取得玩家委托任务信息("mobid7");
                                boolean nmob7 = chr.取得玩家委托任务信息("mob7") < chr.取得委托任务信息("nmob", rw7);
                                if ((mob7 == mobid) && (mob7 > 0) && (nmob7 == true) && ((this.mapid == chr.取得委托任务信息("map", rw7)) || (chr.取得委托任务信息("map", rw7) == 0))) {
                                     chr.增加玩家委托任务信息("mob7", 1);
                                } else {
                                    if (nmob7 == false && rw7 > 0 && mob7 > 0) {
                                       chr.dropMessage(-11, "委托任务7需要打猎的怪物数量已经完成！");
                                    }
                                    int rw8 = chr.取得玩家委托任务信息("renwu8");
                                    int mob8 = chr.取得玩家委托任务信息("mobid8");
                                    boolean nmob8 = chr.取得玩家委托任务信息("mob8") < chr.取得委托任务信息("nmob", rw8);
                                    if ((mob8 == mobid) && (mob8 > 0) && (nmob8 == true) && ((this.mapid == chr.取得委托任务信息("map", rw8)) || (chr.取得委托任务信息("map", rw8) == 0))) {
                                         chr.增加玩家委托任务信息("mob8", 1);
                                    } else {
                                        if (nmob8 == false && rw8 > 0 && mob8 > 0) {
                                           chr.dropMessage(-11, "委托任务8需要打猎的怪物数量已经完成！");
                                        }
                                        int rw9 = chr.取得玩家委托任务信息("renwu9");
                                        int mob9 = chr.取得玩家委托任务信息("mobid9");
                                        boolean nmob9 = chr.取得玩家委托任务信息("mob9") < chr.取得委托任务信息("nmob", rw9);
                                        if ((mob9 == mobid) && (mob9 > 0) && (nmob9 == true) && ((this.mapid == chr.取得委托任务信息("map", rw9)) || (chr.取得委托任务信息("map", rw9) == 0))) {
                                            chr.增加玩家委托任务信息("mob9", 1);
                                        } else {
                                            if (nmob9 == false && rw9 > 0 && mob9 > 0) {
                                               chr.dropMessage(-11, "委托任务9需要打猎的怪物数量已经完成！");
                                            }
                                        }//mob9
                                        
                                    }//mob8
                                    
                                }//mob7
                                
                            }//mob6
                            
                        }//mob5
                        
                    }//mob4
                    
                }//mob3

            }//mob2
            
        }//mob1
        
        
        }

        
        if ((type != null)
                && (this.speedRunStart > 0L) && (this.speedRunLeader.length() > 0)) {
            String name = "";
            if (type.name().equals("Normal_Balrog")) {
                name = "蝙蝠怪";
            } else if (type.name().equals("Zakum")) {
                name = "扎昆";
            } else if (type.name().equals("Horntail")) {
                name = "暗黑龙王";
            } else if (type.name().equals("Pink_Bean")) {
                name = "时间的宠儿－品克缤";
            } else if (type.name().equals("Chaos_Zakum")) {
                name = "进阶扎昆";
            } else if (type.name().equals("ChaosHT")) {
                name = "进阶暗黑龙王";
            } else if (type.name().equals("Von_Leon")) {
                name = "班·雷昂";
            } else if (type.name().equals("Cygnus")) {
                name = "希纳斯女皇";
            }
            long endTime = System.currentTimeMillis();
            String time = StringUtil.getReadableMillis(this.speedRunStart, endTime);
            broadcastMessage(MaplePacketCreator.serverNotice(5, new StringBuilder().append(this.speedRunLeader).append("带领的远征队，耗时: ").append(time).append(" 击败了 ").append(name).append("!").toString()));
            getRankAndAdd(this.speedRunLeader, time, type, endTime - this.speedRunStart, sqd == null ? null : sqd.getMembers());
            endSpeedRun();
        }

        if ((withDrops) && (dropOwner != 1)) {
            MapleCharacter drop = null;
            if (dropOwner <= 0) {
                drop = chr;
            } else {
                drop = getCharacterById(dropOwner);
                if (drop == null) {
                    drop = chr;
                }
            }
            dropFromMonster(drop, monster, instanced);
        }
    }

    public List<MapleReactor> getAllReactor() {
        return getAllReactorsThreadsafe();
    }

    public List<MapleReactor> getAllReactorsThreadsafe() {
        ArrayList ret = new ArrayList();
        ((ReentrantReadWriteLock) this.mapobjectlocks.get(MapleMapObjectType.REACTOR)).readLock().lock();
        try {
            for (MapleMapObject mmo : mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                ret.add((MapleReactor) mmo);
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
        return ret;
    }

    public List<MapleSummon> getAllSummonsThreadsafe() {
        ArrayList ret = new ArrayList();
        ((ReentrantReadWriteLock) this.mapobjectlocks.get(MapleMapObjectType.SUMMON)).readLock().lock();
        try {
            for (MapleMapObject mmo : mapobjects.get(MapleMapObjectType.SUMMON).values()) {
                if ((mmo instanceof MapleSummon)) {
                    ret.add((MapleSummon) mmo);
                }
            }
        } finally {
            ((ReentrantReadWriteLock) this.mapobjectlocks.get(MapleMapObjectType.SUMMON)).readLock().unlock();
        }
        return ret;
    }

    public List<MapleMapObject> getAllDoor() {
        return getAllDoorsThreadsafe();
    }

    public List<MapleMapObject> getAllDoorsThreadsafe() {
        ArrayList ret = new ArrayList();
        ((ReentrantReadWriteLock) this.mapobjectlocks.get(MapleMapObjectType.DOOR)).readLock().lock();
        try {
            for (MapleMapObject mmo : mapobjects.get(MapleMapObjectType.DOOR).values()) {
                if ((mmo instanceof MapleDoor)) {
                    ret.add(mmo);
                }
            }
        } finally {
            ((ReentrantReadWriteLock) this.mapobjectlocks.get(MapleMapObjectType.DOOR)).readLock().unlock();
        }
        return ret;
    }

    public List<MapleMapObject> getAllMechDoorsThreadsafe() {
        ArrayList ret = new ArrayList();
        ((ReentrantReadWriteLock) this.mapobjectlocks.get(MapleMapObjectType.DOOR)).readLock().lock();
        try {
            for (MapleMapObject mmo : mapobjects.get(MapleMapObjectType.DOOR).values()) {
                if ((mmo instanceof MechDoor)) {
                    ret.add(mmo);
                }
            }
        } finally {
            ((ReentrantReadWriteLock) this.mapobjectlocks.get(MapleMapObjectType.DOOR)).readLock().unlock();
        }
        return ret;
    }

    public List<MapleMapObject> getAllMerchant() {
        return getAllHiredMerchantsThreadsafe();
    }

    public List<MapleMapObject> getAllHiredMerchantsThreadsafe() {
        ArrayList ret = new ArrayList();
        mapobjectlocks.get(MapleMapObjectType.HIRED_MERCHANT).readLock().lock();
        try {
            for (MapleMapObject mmo : mapobjects.get(MapleMapObjectType.HIRED_MERCHANT).values()) {
                ret.add(mmo);
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.HIRED_MERCHANT).readLock().unlock();
        }
        return ret;
    }

    public List<MapleMonster> getAllMonster() {
        return getAllMonstersThreadsafe();
    }

    public List<MapleMonster> getAllMonstersThreadsafe() {
        ArrayList ret = new ArrayList();
        mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().lock();
        try {
            for (MapleMapObject mmo : mapobjects.get(MapleMapObjectType.MONSTER).values()) {
                ret.add((MapleMonster) mmo);
            }
        } finally {
            ((ReentrantReadWriteLock) this.mapobjectlocks.get(MapleMapObjectType.MONSTER)).readLock().unlock();
        }
        return ret;
    }

    public List<Integer> getAllUniqueMonsters() {
        ArrayList ret = new ArrayList();
        ((ReentrantReadWriteLock) this.mapobjectlocks.get(MapleMapObjectType.MONSTER)).readLock().lock();
        try {
            for (MapleMapObject mmo : mapobjects.get(MapleMapObjectType.MONSTER).values()) {
                int theId = ((MapleMonster) mmo).getId();
                if (!ret.contains(Integer.valueOf(theId))) {
                    ret.add(Integer.valueOf(theId));
                }
            }
        } finally {
            ((ReentrantReadWriteLock) this.mapobjectlocks.get(MapleMapObjectType.MONSTER)).readLock().unlock();
        }
        return ret;
    }

    public void killAllMonsters(boolean animate) {
        for (MapleMapObject monstermo : getAllMonstersThreadsafe()) {
            MapleMonster monster = (MapleMonster) monstermo;
            this.spawnedMonstersOnMap.decrementAndGet();
            monster.setHp(0L);
            broadcastMessage(MobPacket.killMonster(monster.getObjectId(), animate ? 1 : 0));
            removeMapObject(monster);
            monster.killed();
        }
    }

    public void killMonster(int monsId) {
        for (MapleMapObject mmo : getAllMonstersThreadsafe()) {
            if (((MapleMonster) mmo).getId() == monsId) {
                this.spawnedMonstersOnMap.decrementAndGet();
                removeMapObject(mmo);
                broadcastMessage(MobPacket.killMonster(mmo.getObjectId(), 1));
                ((MapleMonster) mmo).killed();
                break;
            }
        }
    }

    private String MapDebug_Log() {
        StringBuilder sb = new StringBuilder("Defeat time : ");
        sb.append(FileoutputUtil.CurrentReadable_Time());
        sb.append(" | Mapid : ").append(this.mapid);
        this.charactersLock.readLock().lock();
        try {
            sb.append(" Users [").append(this.characters.size()).append("] | ");
            for (MapleCharacter mc : this.characters) {
                sb.append(mc.getName()).append(", ");
            }
        } finally {
            this.charactersLock.readLock().unlock();
        }
        return sb.toString();
    }

    public void limitReactor(int rid, int num) {
        List<MapleReactor> toDestroy = new ArrayList<MapleReactor>();
        Map contained = new LinkedHashMap();
        ((ReentrantReadWriteLock) this.mapobjectlocks.get(MapleMapObjectType.REACTOR)).readLock().lock();
        try {
            for (MapleMapObject obj : mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                MapleReactor mr = (MapleReactor) obj;
                if (contained.containsKey(Integer.valueOf(mr.getReactorId()))) {
                    if (((Integer) contained.get(Integer.valueOf(mr.getReactorId()))).intValue() >= num) {
                        toDestroy.add(mr);
                    } else {
                        contained.put(Integer.valueOf(mr.getReactorId()), Integer.valueOf(((Integer) contained.get(Integer.valueOf(mr.getReactorId()))).intValue() + 1));
                    }
                } else {
                    contained.put(Integer.valueOf(mr.getReactorId()), Integer.valueOf(1));
                }
            }
        } finally {
            ((ReentrantReadWriteLock) this.mapobjectlocks.get(MapleMapObjectType.REACTOR)).readLock().unlock();
        }
        for (MapleReactor mr : toDestroy) {
            destroyReactor(mr.getObjectId());
        }
    }

    public void destroyReactors(int first, int last) {
        List<MapleReactor> toDestroy = new ArrayList<MapleReactor>();
        ((ReentrantReadWriteLock) this.mapobjectlocks.get(MapleMapObjectType.REACTOR)).readLock().lock();
        try {
            for (MapleMapObject obj : mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                MapleReactor mr = (MapleReactor) obj;
                if ((mr.getReactorId() >= first) && (mr.getReactorId() <= last)) {
                    toDestroy.add(mr);
                }
            }
        } finally {
            ((ReentrantReadWriteLock) this.mapobjectlocks.get(MapleMapObjectType.REACTOR)).readLock().unlock();
        }
        for (MapleReactor mr : toDestroy) {
            destroyReactor(mr.getObjectId());
        }
    }

    public void destroyReactor(int oid) {
        final MapleReactor reactor = getReactorByOid(oid);
        if (reactor == null) {
            return;
        }
        broadcastMessage(MaplePacketCreator.destroyReactor(reactor));
        reactor.setAlive(false);
        removeMapObject(reactor);
        reactor.setTimerActive(false);

        if (reactor.getDelay() > 0) {
            MapTimer.getInstance().schedule(new Runnable() {

                public void run() {
                    MapleMap.this.respawnReactor(reactor);
                }
            }, reactor.getDelay());
        }
    }

    public void reloadReactors() {
        List<MapleReactor> toSpawn = new ArrayList();
        ((ReentrantReadWriteLock) this.mapobjectlocks.get(MapleMapObjectType.REACTOR)).readLock().lock();
        try {
            for (MapleMapObject obj : mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                MapleReactor reactor = (MapleReactor) obj;
                broadcastMessage(MaplePacketCreator.destroyReactor(reactor));
                reactor.setAlive(false);
                reactor.setTimerActive(false);
                toSpawn.add(reactor);
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
        for (MapleReactor r : toSpawn) {
            removeMapObject(r);
            if (!r.isCustom()) {
                respawnReactor(r);
            }
        }
    }

    public void resetReactors() {
        setReactorState((byte) 0);
    }

    public void setReactorState() {
        setReactorState((byte) 1);
    }

    public void setReactorState(final byte state) {
        mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject obj : mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                ((MapleReactor) obj).forceHitReactor(state);
            }
        } finally {
            ((ReentrantReadWriteLock) this.mapobjectlocks.get(MapleMapObjectType.REACTOR)).readLock().unlock();
        }
    }

    public void setReactorDelay(final int state) {
        mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject obj : mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                ((MapleReactor) obj).setDelay(state);
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
    }

    public void shuffleReactors() {
        shuffleReactors(0, 9999999);
    }

    public final void shuffleReactors(int first, int last) {
        List<Point> points = new ArrayList<Point>();
        mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject obj : mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                MapleReactor mr = (MapleReactor) obj;
                if (mr.getReactorId() >= first && mr.getReactorId() <= last) {
                    points.add(mr.getPosition());
                }
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
        Collections.shuffle(points);
        mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject obj : mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                MapleReactor mr = (MapleReactor) obj;
                if (mr.getReactorId() >= first && mr.getReactorId() <= last) {
                    mr.setPosition(points.remove(points.size() - 1));
                }
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
    }

    public void updateMonsterController(MapleMonster monster) {
        if ((!monster.isAlive()) || (monster.getLinkCID() > 0) || (monster.getStats().isEscort())) {
            return;
        }
        if (monster.getController() != null) {
            if ((monster.getController().getMap() != this) || (monster.getController().getTruePosition().distanceSq(monster.getTruePosition()) > monster.getRange())) {
                monster.getController().stopControllingMonster(monster);
            } else {
                return;
            }
        }
        int mincontrolled = -1;
        MapleCharacter newController = null;

        this.charactersLock.readLock().lock();
        try {
            Iterator ltr = this.characters.iterator();

            while (ltr.hasNext()) {
                MapleCharacter chr = (MapleCharacter) ltr.next();
                if ((!chr.isHidden()) && (!chr.isClone()) && ((chr.getControlledSize() < mincontrolled) || (mincontrolled == -1)) && (chr.getTruePosition().distanceSq(monster.getTruePosition()) <= monster.getRange())) {
                    mincontrolled = chr.getControlledSize();
                    newController = chr;
                }
            }
        } finally {
            this.charactersLock.readLock().unlock();
        }
        if (newController != null) {
            if (monster.isFirstAttack()) {
                newController.controlMonster(monster, true);
                monster.setControllerHasAggro(true);
            } else {
                newController.controlMonster(monster, false);
            }
        }
    }

    public final MapleMapObject getMapObject(int oid, MapleMapObjectType type) {
        mapobjectlocks.get(type).readLock().lock();
        try {
            return mapobjects.get(type).get(oid);
        } finally {
            mapobjectlocks.get(type).readLock().unlock();
        }
    }

    public final boolean containsNPC(int npcid) {
        mapobjectlocks.get(MapleMapObjectType.NPC).readLock().lock();
        try {
            Iterator<MapleMapObject> itr = mapobjects.get(MapleMapObjectType.NPC).values().iterator();
            while (itr.hasNext()) {
                MapleNPC n = (MapleNPC) itr.next();
                if (n.getId() == npcid) {
                    return true;
                }
            }
            return false;
        } finally {
            mapobjectlocks.get(MapleMapObjectType.NPC).readLock().unlock();
        }
    }

    public MapleNPC getNPCById(int id) {
        mapobjectlocks.get(MapleMapObjectType.NPC).readLock().lock();
        try {
            Iterator<MapleMapObject> itr = mapobjects.get(MapleMapObjectType.NPC).values().iterator();
            while (itr.hasNext()) {
                MapleNPC n = (MapleNPC) itr.next();
                if (n.getId() == id) {
                    return n;
                }
            }
            return null;
        } finally {
            mapobjectlocks.get(MapleMapObjectType.NPC).readLock().unlock();
        }
    }

    public MapleMonster getMonsterById(int id) {
        mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().lock();
        try {
            MapleMonster ret = null;
            Iterator<MapleMapObject> itr = mapobjects.get(MapleMapObjectType.MONSTER).values().iterator();
            while (itr.hasNext()) {
                MapleMonster n = (MapleMonster) itr.next();
                if (n.getId() == id) {
                    ret = n;
                    break;
                }
            }
            return ret;
        } finally {
            mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().unlock();
        }
    }

    public int countMonsterById(int id) {
        mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().lock();
        try {
            int ret = 0;
            Iterator<MapleMapObject> itr = mapobjects.get(MapleMapObjectType.MONSTER).values().iterator();
            while (itr.hasNext()) {
                MapleMonster n = (MapleMonster) itr.next();
                if (n.getId() == id) {
                    ret++;
                }
            }
            return ret;
        } finally {
            mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().unlock();
        }
    }

    public MapleReactor getReactorById(int id) {
        mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            MapleReactor ret = null;
            Iterator<MapleMapObject> itr = mapobjects.get(MapleMapObjectType.REACTOR).values().iterator();
            while (itr.hasNext()) {
                MapleReactor n = (MapleReactor) itr.next();
                if (n.getReactorId() == id) {
                    ret = n;
                    break;
                }
            }
            return ret;
        } finally {
            mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
    }

    public MapleMonster getMonsterByOid(int oid) {
        MapleMapObject mmo = getMapObject(oid, MapleMapObjectType.MONSTER);
        if (mmo == null) {
            return null;
        }
        return (MapleMonster) mmo;
    }

    public MapleNPC getNPCByOid(int oid) {
        MapleMapObject mmo = getMapObject(oid, MapleMapObjectType.NPC);
        if (mmo == null) {
            return null;
        }
        return (MapleNPC) mmo;
    }

    public MapleReactor getReactorByOid(int oid) {
        MapleMapObject mmo = getMapObject(oid, MapleMapObjectType.REACTOR);
        if (mmo == null) {
            return null;
        }
        return (MapleReactor) mmo;
    }

    public MonsterFamiliar getFamiliarByOid(int oid) {
        MapleMapObject mmo = getMapObject(oid, MapleMapObjectType.FAMILIAR);
        if (mmo == null) {
            return null;
        }
        return (MonsterFamiliar) mmo;
    }

    public final MapleReactor getReactorByName(final String name) {
        mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject obj : mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                MapleReactor mr = ((MapleReactor) obj);
                if (mr.getName().equalsIgnoreCase(name)) {
                    return mr;
                }
            }
            return null;
        } finally {
            mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
    }

    public void spawnNpc(int id, Point pos) {
        MapleNPC npc = MapleLifeFactory.getNPC(id);
        npc.setPosition(pos);
        npc.setCy(pos.y);
        npc.setRx0(pos.x + 50);
        npc.setRx1(pos.x - 50);
        npc.setFh(getFootholds().findBelow(pos).getId());
        npc.setCustom(true);
        addMapObject(npc);
        broadcastMessage(MaplePacketCreator.spawnNPC(npc, true));
    }

    public void removeNpc(int npcid) {
        ((ReentrantReadWriteLock) this.mapobjectlocks.get(MapleMapObjectType.NPC)).writeLock().lock();
        try {
            Iterator itr = ((LinkedHashMap) this.mapobjects.get(MapleMapObjectType.NPC)).values().iterator();
            while (itr.hasNext()) {
                MapleNPC npc = (MapleNPC) itr.next();
                if ((npc.isCustom()) && ((npcid == -1) || (npc.getId() == npcid))) {
                    broadcastMessage(MaplePacketCreator.removeNPCController(npc.getObjectId()));
                    broadcastMessage(MaplePacketCreator.removeNPC(npc.getObjectId()));
                    itr.remove();
                }
            }
        } finally {
            ((ReentrantReadWriteLock) this.mapobjectlocks.get(MapleMapObjectType.NPC)).writeLock().unlock();
        }
    }

    public void hideNpc(int npcid) {
        ((ReentrantReadWriteLock) this.mapobjectlocks.get(MapleMapObjectType.NPC)).readLock().lock();
        try {
            Iterator itr = ((LinkedHashMap) this.mapobjects.get(MapleMapObjectType.NPC)).values().iterator();
            while (itr.hasNext()) {
                MapleNPC npc = (MapleNPC) itr.next();
                if ((npcid == -1) || (npc.getId() == npcid)) {
                    broadcastMessage(MaplePacketCreator.removeNPCController(npc.getObjectId()));
                    broadcastMessage(MaplePacketCreator.removeNPC(npc.getObjectId()));
                }
            }
        } finally {
            ((ReentrantReadWriteLock) this.mapobjectlocks.get(MapleMapObjectType.NPC)).readLock().unlock();
        }
    }

    public void spawnReactorOnGroundBelow(MapleReactor mob, Point pos) {
        mob.setPosition(pos);
        mob.setCustom(true);
        spawnReactor(mob);
    }

    public void spawnMonster_sSack(MapleMonster mob, Point pos, int spawnType) {
        mob.setPosition(calcPointBelow(new Point(pos.x, pos.y - 1)));
        spawnMonster(mob, spawnType);
    }

    public void spawnMonster_Pokemon(MapleMonster mob, Point pos, int spawnType) {
        Point spos = calcPointBelow(new Point(pos.x, pos.y - 1));
        mob.setPosition(spos);
        spawnMonster(mob, spawnType, true);
    }

    public void spawnMonsterOnGroundBelow(MapleMonster mob, Point pos) {
        spawnMonster_sSack(mob, pos, -2);
    }

    public int spawnMonsterWithEffectBelow(MapleMonster mob, Point pos, int effect) {
        Point spos = calcPointBelow(new Point(pos.x, pos.y - 1));
        return spawnMonsterWithEffect(mob, effect, spos);
    }

    public void spawnZakum(int x, int y) {
        Point pos = new Point(x, y);
        MapleMonster mainb = MapleLifeFactory.getMonster(8800000);
        Point spos = calcPointBelow(new Point(pos.x, pos.y - 1));
        mainb.setPosition(spos);
        mainb.setFake(true);

        spawnFakeMonster(mainb);
        int[] zakpart = {8800003, 8800004, 8800005, 8800006, 8800007, 8800008, 8800009, 8800010};
        for (int i : zakpart) {
            MapleMonster part = MapleLifeFactory.getMonster(i);
            part.setPosition(spos);
            spawnMonster(part, -2);
        }
        if (this.squadSchedule != null) {
            cancelSquadSchedule(false);
        }
    }

    public void spawnChaosZakum(int x, int y) {
        Point pos = new Point(x, y);
        MapleMonster mainb = MapleLifeFactory.getMonster(8800100);
        Point spos = calcPointBelow(new Point(pos.x, pos.y - 1));
        mainb.setPosition(spos);
        mainb.setFake(true);

        spawnFakeMonster(mainb);
        int[] zakpart = {8800103, 8800104, 8800105, 8800106, 8800107, 8800108, 8800109, 8800110};
        for (int i : zakpart) {
            MapleMonster part = MapleLifeFactory.getMonster(i);
            part.setPosition(spos);
            spawnMonster(part, -2);
        }
        if (this.squadSchedule != null) {
            cancelSquadSchedule(false);
        }
    }

    public void spawnPinkZakum(int x, int y) {
        Point pos = new Point(x, y);
        MapleMonster mainb = MapleLifeFactory.getMonster(9400900);
        Point spos = calcPointBelow(new Point(pos.x, pos.y - 1));
        mainb.setPosition(spos);
        mainb.setFake(true);

        spawnFakeMonster(mainb);
        int[] zakpart = {9400903, 9400904, 9400905, 9400906, 9400907, 9400908, 9400909, 9400910};
        for (int i : zakpart) {
            MapleMonster part = MapleLifeFactory.getMonster(i);
            part.setPosition(spos);
            spawnMonster(part, -2);
        }
        if (this.squadSchedule != null) {
            cancelSquadSchedule(false);
        }
    }
    
    public void spawnAkayile(int x, int y) {
        Point pos = new Point(x, y);
        MapleMonster mainb = MapleLifeFactory.getMonster(8860010);
        Point spos = calcPointBelow(new Point(pos.x, pos.y - 1));
        mainb.setPosition(spos);
        mainb.setFake(true);
        spawnFakeMonster(mainb);
        if (this.squadSchedule != null) {
            cancelSquadSchedule(false);
        }
    }

    public void spawnFakeMonsterOnGroundBelow(MapleMonster mob, Point pos) {
        Point spos = calcPointBelow(new Point(pos.x, pos.y - 1));
        spos.y -= 1;
        mob.setPosition(spos);
        spawnFakeMonster(mob);
    }

    private void checkRemoveAfter(MapleMonster monster) {
        int ra = monster.getStats().getRemoveAfter();
        if ((ra > 0) && (monster.getLinkCID() <= 0)) {
            monster.registerKill(ra * 1000);
        }
    }

    public void spawnRevives(final MapleMonster monster, final int oid) {
        monster.setMap(this);
        checkRemoveAfter(monster);
        monster.setLinkOid(oid);
        spawnAndAddRangedMapObject(monster, new DelayedPacketCreation() {

            public void sendPackets(MapleClient c) {
                c.getSession().write(MobPacket.spawnMonster(monster, monster.getStats().getSummonType() <= 1 ? -3 : monster.getStats().getSummonType(), oid));
            }
        });
        updateMonsterController(monster);
        this.spawnedMonstersOnMap.incrementAndGet();
    }

    public void spawnMonster(MapleMonster monster, int spawnType) {
        spawnMonster(monster, spawnType, false);
    }

    public void spawnMonster(final MapleMonster monster, final int spawnType, final boolean overwrite) {
        monster.setMap(this);
        checkRemoveAfter(monster);
        spawnAndAddRangedMapObject(monster, new DelayedPacketCreation() {

            public void sendPackets(MapleClient c) {
                c.getSession().write(MobPacket.spawnMonster(monster, (monster.getStats().getSummonType() <= 1) || (monster.getStats().getSummonType() == 27) || (overwrite) ? spawnType : monster.getStats().getSummonType(), 0));
            }
        });
        updateMonsterController(monster);
        this.spawnedMonstersOnMap.incrementAndGet();
    }

    public int spawnMonsterWithEffect(final MapleMonster monster, final int effect, Point pos) {
        try {
            monster.setMap(this);
            monster.setPosition(pos);
            spawnAndAddRangedMapObject(monster, new DelayedPacketCreation() {

                public void sendPackets(MapleClient c) {
                    c.getSession().write(MobPacket.spawnMonster(monster, effect, 0));
                }
            });
            updateMonsterController(monster);
            this.spawnedMonstersOnMap.incrementAndGet();
            return monster.getObjectId();
        } catch (Exception e) {
        }
        return -1;
    }

    public void spawnFakeMonster(final MapleMonster monster) {
        monster.setMap(this);
        monster.setFake(true);
        spawnAndAddRangedMapObject(monster, new DelayedPacketCreation() {

            public void sendPackets(MapleClient c) {
                c.getSession().write(MobPacket.spawnMonster(monster, -4, 0));
            }
        });
        updateMonsterController(monster);
        this.spawnedMonstersOnMap.incrementAndGet();
    }

    public void spawnReactor(final MapleReactor reactor) {
        reactor.setMap(this);
        spawnAndAddRangedMapObject(reactor, new DelayedPacketCreation() {

            public void sendPackets(MapleClient c) {
                c.getSession().write(MaplePacketCreator.spawnReactor(reactor));
            }
        });
    }

    private void respawnReactor(MapleReactor reactor) {
        reactor.setState((byte) 0);
        reactor.setAlive(true);
        spawnReactor(reactor);
    }

    public void spawnDoor(final MapleDoor door) {
        spawnAndAddRangedMapObject(door, new DelayedPacketCreation() {

            public void sendPackets(MapleClient c) {
                door.sendSpawnData(c);
                c.getSession().write(MaplePacketCreator.enableActions());
            }
        });
    }

    public void spawnMechDoor(final MechDoor door) {
        spawnAndAddRangedMapObject(door, new DelayedPacketCreation() {

            public void sendPackets(MapleClient c) {
                c.getSession().write(MaplePacketCreator.spawnMechDoor(door, true));
                c.getSession().write(MaplePacketCreator.enableActions());
            }
        });
    }

    public void spawnSummon(final MapleSummon summon) {
        summon.updateMap(this);
        spawnAndAddRangedMapObject(summon, new DelayedPacketCreation() {

            public void sendPackets(MapleClient c) {
                if ((summon != null) && (c.getPlayer() != null) && ((!summon.isChangedMap()) || (summon.getOwnerId() == c.getPlayer().getId()))) {
                    c.getSession().write(MaplePacketCreator.spawnSummon(summon, true));
                }
            }
        });
    }

    public void spawnFamiliar(final MonsterFamiliar familiar) {
        spawnAndAddRangedMapObject(familiar, new DelayedPacketCreation() {

            public void sendPackets(MapleClient c) {
                if ((familiar != null) && (c.getPlayer() != null)) {
                    c.getSession().write(MaplePacketCreator.spawnFamiliar(familiar, true));
                }
            }
        });
    }

    public void spawnExtractor(final MapleExtractor ex) {
        spawnAndAddRangedMapObject(ex, new DelayedPacketCreation() {

            public void sendPackets(MapleClient c) {
                ex.sendSpawnData(c);
            }
        });
    }

    public void spawnLove(final MapleLove love) {
        spawnAndAddRangedMapObject(love, new DelayedPacketCreation() {

            public void sendPackets(MapleClient c) {
                love.sendSpawnData(c);
            }
        });
        MapTimer tMan = MapTimer.getInstance();
        tMan.schedule(new Runnable() {

            public void run() {
                MapleMap.this.broadcastMessage(MaplePacketCreator.removeLove(love.getObjectId(), love.getItemId()));
                MapleMap.this.removeMapObject(love);
            }
        }, 3600000L);
    }

    public void spawnMist(final MapleMist mist, final int duration, boolean fake) {
        spawnAndAddRangedMapObject(mist, new DelayedPacketCreation() {

            public void sendPackets(MapleClient c) {
                mist.sendSpawnData(c);
            }
        });
        MapTimer tMan = MapTimer.getInstance();
        final ScheduledFuture poisonSchedule;
        switch (mist.isPoisonMist()) {
            case 1:
                final MapleCharacter owner = getCharacterById(mist.getOwnerId());
                final boolean pvp = owner.inPVP();
                poisonSchedule = tMan.register(new Runnable() {

                    public void run() {
                        for (MapleMapObject mo : MapleMap.this.getMapObjectsInRect(mist.getBox(), Collections.singletonList(pvp ? MapleMapObjectType.PLAYER : MapleMapObjectType.MONSTER))) {
                            if ((pvp) && (mist.makeChanceResult()) && (!((MapleCharacter) mo).hasDOT()) && (((MapleCharacter) mo).getId() != mist.getOwnerId())) {
                                ((MapleCharacter) mo).setDOT(mist.getSource().getDOT(), mist.getSourceSkill().getId(), mist.getSkillLevel());
                            } else if ((!pvp) && (mist.makeChanceResult()) && (!((MapleMonster) mo).isBuffed(MonsterStatus.中毒))) {
                                ((MapleMonster) mo).applyStatus(owner, new MonsterStatusEffect(MonsterStatus.中毒, Integer.valueOf(1), mist.getSourceSkill().getId(), null, false), true, duration, true, mist.getSource());
                            }
                        }
                    }
                }, 2000L, 2500L);

                break;
            case 4:
                poisonSchedule = tMan.register(new Runnable() {

                    public void run() {
                        for (MapleMapObject mo : MapleMap.this.getMapObjectsInRect(mist.getBox(), Collections.singletonList(MapleMapObjectType.PLAYER))) {
                            if (mist.makeChanceResult()) {
                                MapleCharacter chr = (MapleCharacter) mo;
                                chr.addMP((int) (mist.getSource().getX() * (chr.getStat().getMaxMp() / 100.0D)));
                            }
                        }
                    }
                }, 2000L, 2500L);

                break;
            default:
                poisonSchedule = null;
        }

        mist.setPoisonSchedule(poisonSchedule);
        mist.setSchedule(tMan.schedule(new Runnable() {

            public void run() {
                MapleMap.this.broadcastMessage(MaplePacketCreator.removeMist(mist.getObjectId(), false));
                MapleMap.this.removeMapObject(mist);
                if (poisonSchedule != null) {
                    poisonSchedule.cancel(false);
                }
            }
        }, duration));
    }

    public void disappearingItemDrop(final MapleMapObject dropper, MapleCharacter owner, Item item, Point pos) {
        final Point droppos = calcDropPos(pos, pos);
        final MapleMapItem drop = new MapleMapItem(item, droppos, dropper, owner, (byte) 1, false);
        broadcastMessage(MaplePacketCreator.dropItemFromMapObject(drop, dropper.getTruePosition(), droppos, (byte) 3), drop.getTruePosition());
    }

    public void spawnMesoDrop(final int meso, final Point position, final MapleMapObject dropper, MapleCharacter owner, boolean playerDrop, byte droptype) {
        final Point droppos = calcDropPos(position, position);
        final MapleMapItem mdrop = new MapleMapItem(meso, droppos, dropper, owner, droptype, playerDrop);

        spawnAndAddRangedMapObject(mdrop, new DelayedPacketCreation() {

            public void sendPackets(MapleClient c) {
                c.getSession().write(MaplePacketCreator.dropItemFromMapObject(mdrop, dropper.getTruePosition(), droppos, (byte) 1));
            }
        });
        if (!this.everlast) {
            mdrop.registerExpire(120000L);
            if ((droptype == 0) || (droptype == 1)) {
                mdrop.registerFFA(30000L);
            }
        }
    }
//怪物掉落金币
    public final void spawnMobMesoDrop(int meso, final Point position, final MapleMapObject dropper, final MapleCharacter owner, boolean playerDrop, byte droptype) {
        final MapleMapItem mdrop = new MapleMapItem(meso, position, dropper, owner, droptype, playerDrop);

       // final int mose = meso; 
     //   if (chrs.getMaplewing("jinbi") == 0) {
            
        spawnAndAddRangedMapObject(mdrop, new DelayedPacketCreation() {

            public void sendPackets(MapleClient c) {
                c.getSession().write(MaplePacketCreator.dropItemFromMapObject(mdrop, dropper.getTruePosition(), position, (byte) 1));
            }
        });
        mdrop.registerExpire(120000L);
        if ((droptype == 0) || (droptype == 1)) {
            mdrop.registerFFA(30000L);
        }
        
     //   } else {
     //      chrs.gainMeso(mose, true, true);
    //    } 
    }
//怪物掉落物品
    public final void spawnMobDrop(final Item idrop, final Point dropPos, final MapleMonster mob, MapleCharacter chr, byte droptype, final int questid) {
        final MapleMapItem mdrop = new MapleMapItem(idrop, dropPos, mob, chr, droptype, false, questid);

        spawnAndAddRangedMapObject(mdrop, new DelayedPacketCreation() {

            public void sendPackets(MapleClient c) {
 
                if ((c != null) && (c.getPlayer() != null) && ((questid <= 0) || (c.getPlayer().getQuestStatus(questid) == 1)) && ((idrop.getItemId() / 10000 != 238) || (c.getPlayer().getMonsterBook().getLevelByCard(idrop.getItemId()) >= 2)) && (mob != null) && (dropPos != null)) {
                    c.getSession().write(MaplePacketCreator.dropItemFromMapObject(mdrop, mob.getTruePosition(), dropPos, (byte) 1));
                  //  if (theItems < 200) {//怪物枫叶等物品的概率
                   //     MapleMap.this.spawnAutoDrop(itemids, dropPos);
                  //  }
                }
            }
        });
        int theItems = (int)(Math.random()*100);
        int itemids = GameConstants.所有怪物掉落[Randomizer.nextInt(GameConstants.所有怪物掉落.length)];
        
        
        int 纪念币 = GameConstants.所有纪念币[Randomizer.nextInt(GameConstants.所有纪念币.length)];
        
        if ((theItems < 20) && (itemids > 0)) {//怪物枫叶等物品的概率
           MapleMap.this.spawnAutoDrop(itemids, dropPos);
         } else if ((theItems > 90) && (纪念币 > 0)) {
            MapleMap.this.spawnAutoDrop(纪念币, dropPos);
        }
        
        mdrop.registerExpire(120000L);
        if ((droptype == 0) || (droptype == 1)) {
            mdrop.registerFFA(30000L);
        }
        activateItemReactors(mdrop, chr.getClient());
    }

    public final void spawnRandDrop() {
        if ((this.mapid != 910000000) || (this.channel != 1)) {
            return;
        }

        ((ReentrantReadWriteLock) this.mapobjectlocks.get(MapleMapObjectType.ITEM)).readLock().lock();
        try {
            for (MapleMapObject o : mapobjects.get(MapleMapObjectType.ITEM).values()) {
                if (((MapleMapItem) o).isRandDrop()) {
                    return;
                }
            }
        } finally {
            ((ReentrantReadWriteLock) this.mapobjectlocks.get(MapleMapObjectType.ITEM)).readLock().unlock();
        }
        MapTimer.getInstance().schedule(new Runnable() {

            public void run() {
                Point pos = new Point(Randomizer.nextInt(800) + 531, -806);
                int theItem = Randomizer.nextInt(1000);
                int itemid = 0;
                if (theItem < 950) {
                    itemid = GameConstants.normalDrops[Randomizer.nextInt(GameConstants.normalDrops.length)];
                } else if (theItem < 990) {
                    itemid = GameConstants.rareDrops[Randomizer.nextInt(GameConstants.rareDrops.length)];
                } else {
                    itemid = GameConstants.superDrops[Randomizer.nextInt(GameConstants.superDrops.length)];
                }
                MapleMap.this.spawnAutoDrop(itemid, pos);
            }
        }, 20000L);
    }

    public final void spawnAutoDrop(final int itemid, final Point pos) {
        Item idrop = null;
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (GameConstants.getInventoryType(itemid) == MapleInventoryType.EQUIP) {
            idrop = ii.randomizeStats((Equip) ii.getEquipById(itemid));
        } else {
            idrop = new Item(itemid, (byte) 0, (short) 1, (byte) 0);
        }
        idrop.setGMLog(new StringBuilder().append("自动掉落 ").append(itemid).append(" 地图 ").append(this.mapid).toString());
        final MapleMapItem mdrop = new MapleMapItem(pos, idrop);
        spawnAndAddRangedMapObject(mdrop, new DelayedPacketCreation() {

            public void sendPackets(MapleClient c) {
                c.getSession().write(MaplePacketCreator.dropItemFromMapObject(mdrop, pos, pos, (byte) 1));
            }
        });
        broadcastMessage(MaplePacketCreator.dropItemFromMapObject(mdrop, pos, pos, (byte) 0));
        if (itemid / 10000 != 291) {
            mdrop.registerExpire(120000L);
        }
    }
    
    
    public final void Drops(int maps) {

        ((ReentrantReadWriteLock) this.mapobjectlocks.get(MapleMapObjectType.ITEM)).readLock().lock();
        try {
            for (MapleMapObject o : mapobjects.get(MapleMapObjectType.ITEM).values()) {
                if (((MapleMapItem) o).isRandDrop()) {
                    return;
                }
            }
        } finally {
            ((ReentrantReadWriteLock) this.mapobjectlocks.get(MapleMapObjectType.ITEM)).readLock().unlock();
        }
        MapTimer.getInstance().schedule(new Runnable() {

            public void run() {
                
                int mdx = -375 + (int)(Math.random()*1380);
             //   int mdy = 1264 - (int)(Math.random()*100);
               // int pox = chrs.getPosition().x;
             //   int poy = chrs.getPosition().y;
             //   Point pos = new Point(pox + Randomizer.nextInt(150), poy + Randomizer.nextInt(150));
                Point pos = new Point(mdx, 1264);
                
                int theItem = Randomizer.nextInt(1000);
                int itemid = 0;
                
                if (theItem < 950) {
                    itemid = GameConstants.normalDrops[Randomizer.nextInt(GameConstants.normalDrops.length)];
                } else if (theItem < 990) {
                    itemid = GameConstants.rareDrops[Randomizer.nextInt(GameConstants.rareDrops.length)];
                } else {
                    itemid = GameConstants.superDrops[Randomizer.nextInt(GameConstants.superDrops.length)];
                }
                
                
                
                MapleMap.this.spawnAutoDrop(itemid, pos);
            }
        }, 20000L);
    }

    public final void spawnItemDrop(final MapleMapObject dropper, final MapleCharacter owner, Item item, Point pos, boolean ffaDrop, boolean playerDrop) {
        final Point droppos = calcDropPos(pos, pos);
        final MapleMapItem drop = new MapleMapItem(item, droppos, dropper, owner, (byte) 2, playerDrop);

        spawnAndAddRangedMapObject(drop, new DelayedPacketCreation() {

            public void sendPackets(MapleClient c) {
                c.getSession().write(MaplePacketCreator.dropItemFromMapObject(drop, dropper.getTruePosition(), droppos, (byte) 1));
            }
        });
        broadcastMessage(MaplePacketCreator.dropItemFromMapObject(drop, dropper.getTruePosition(), droppos, (byte) 0));

        if (!this.everlast) {
            drop.registerExpire(120000L);
            activateItemReactors(drop, owner.getClient());
        }
    }
    
    public MapleMapObject getMapObject(int oid) {
        return mapobjectss.get(oid);
    }
    
    
    private class ExpireMapItemJob implements Runnable {

        private MapleMapItem mapitem;

        public ExpireMapItemJob(MapleMapItem mapitem) {
            this.mapitem = mapitem;
        }

        @Override
        public void run() {
            if (mapitem != null && mapitem == getMapObject(mapitem.getObjectId())) {
                synchronized (mapitem) {
                    if (mapitem.isPickedUp()) {
                        return;
                    }
                    MapleMap.this.broadcastMessage(MaplePacketCreator.removeItemFromMap(mapitem.getObjectId(), 0, 0),
                            mapitem.getPosition());
                    MapleMap.this.removeMapObject(mapitem);
                    mapitem.setPickedUp(true);
                }
            }
        }
    }
    
    
    public void spawnItemDropdir(final MapleMapObject dropper, final MapleCharacter owner, final  Item item, final boolean ffaDrop, boolean playerDrop) {
        
        Timer tMan = WorldTimer.getInstance();
	int posx = (int)(Math.floor(Math.random() * 1274D) - 366D);
	int posy = (int)(32D - Math.floor(Math.random() * 450D));
	Point pos1 = new Point(posx, posy);
	final Point droppos = calcDropPos(pos1, pos1);
       // final Point droppos = calcDropPos(pos, pos);
        final MapleMapItem drop = new MapleMapItem(item, droppos, dropper, owner, (byte) 2, playerDrop);

        spawnAndAddRangedMapObject(drop, new DelayedPacketCreation() {

            public void sendPackets(MapleClient c) {
                c.getSession().write(MaplePacketCreator.dropItemFromMapObject(drop, dropper.getTruePosition(), droppos, (byte) 1));
            }
        });
        broadcastMessage(MaplePacketCreator.dropItemFromMapObject(drop, dropper.getTruePosition(), droppos, (byte) 0));
       // broadcastMessage(MaplePacketCreator.dropItemFromMapObject(item.getItemId(), drop.getObjectId(), 0, ffaDrop ? 0: owner.getId(), dropper.getPosition(), droppos, (byte) 0), drop.getPosition());
        
        if (playerDrop) {
            tMan.schedule(new ExpireMapItemJob(drop), dropLife);
        }

      //  if (!this.everlast) {
        //    drop.registerExpire(120000L);
            activateItemReactors(drop, owner.getClient());
       // }
    }

    private void activateItemReactors(MapleMapItem drop, MapleClient c) {
        Item item = drop.getItem();

        mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject o : mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                MapleReactor react = (MapleReactor) o;

                if ((react.getReactorType() == 100)
                        && (item.getItemId() == GameConstants.getCustomReactItem(react.getReactorId(), ((Integer) react.getReactItem().getLeft()).intValue())) && (((Integer) react.getReactItem().getRight()).intValue() == item.getQuantity())
                        && (react.getArea().contains(drop.getTruePosition()))
                        && (!react.isTimerActive())) {
                    MapTimer.getInstance().schedule(new ActivateItemReactor(drop, react, c), 5000L);
                    react.setTimerActive(true);
                    break;
                }
            }

        } finally {
            ((ReentrantReadWriteLock) this.mapobjectlocks.get(MapleMapObjectType.REACTOR)).readLock().unlock();
        }
    }

    public int getItemsSize() {
        return ((LinkedHashMap) this.mapobjects.get(MapleMapObjectType.ITEM)).size();
    }

    public int getExtractorSize() {
        return ((LinkedHashMap) this.mapobjects.get(MapleMapObjectType.EXTRACTOR)).size();
    }

    public int getMobsSize() {
        return ((LinkedHashMap) this.mapobjects.get(MapleMapObjectType.MONSTER)).size();
    }

    public List<MapleMapItem> getAllItems() {
        return getAllItemsThreadsafe();
    }

    public List<MapleMapItem> getAllItemsThreadsafe() {
        ArrayList<MapleMapItem> ret = new ArrayList<MapleMapItem>();
        mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().lock();
        try {
            for (MapleMapObject mmo : mapobjects.get(MapleMapObjectType.ITEM).values()) {
                ret.add((MapleMapItem) mmo);
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().unlock();
        }
        return ret;
    }

    public Point getPointOfItem(int itemid) {
        this.mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().lock();
        try {
            for (MapleMapObject mmo : mapobjects.get(MapleMapObjectType.ITEM).values()) {
                MapleMapItem mm = (MapleMapItem) mmo;
                if ((mm.getItem() != null) && (mm.getItem().getItemId() == itemid)) {
                    Point localPoint = mm.getPosition();
                    return localPoint;
                }
            }
        } finally {
            this.mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().unlock();
        }
        return null;
    }

    public List<MapleMist> getAllMistsThreadsafe() {
        ArrayList<MapleMist> ret = new ArrayList<MapleMist>();
        ((ReentrantReadWriteLock) this.mapobjectlocks.get(MapleMapObjectType.MIST)).readLock().lock();
        try {
            for (MapleMapObject mmo : mapobjects.get(MapleMapObjectType.MIST).values()) {
                ret.add((MapleMist) mmo);
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.MIST).readLock().unlock();
        }
        return ret;
    }

    public void returnEverLastItem(MapleCharacter chr) {
        for (MapleMapObject o : getAllItemsThreadsafe()) {
            MapleMapItem item = (MapleMapItem) o;
            if (item.getOwner() == chr.getId()) {
                item.setPickedUp(true);
                broadcastMessage(MaplePacketCreator.removeItemFromMap(item.getObjectId(), 2, chr.getId()), item.getTruePosition());
                if (item.getMeso() > 0) {
                    chr.gainMeso(item.getMeso(), false);
                } else {
                    MapleInventoryManipulator.addFromDrop(chr.getClient(), item.getItem(), false);
                }
                removeMapObject(item);
            }
        }
        spawnRandDrop();
    }

    public void talkMonster(String msg, int itemId, int objectid) {
        if (itemId > 0) {
            startMapEffect(msg, itemId, false);
        }
        broadcastMessage(MobPacket.talkMonster(objectid, itemId, msg));
        broadcastMessage(MobPacket.removeTalkMonster(objectid));
    }

    public void startMapEffect(String msg, int itemId) {
        startMapEffect(msg, itemId, false);
    }

    public final void startMapEffect(String msg, int itemId, boolean jukebox) {
        if (this.mapEffect != null) {
            return;
        }
        this.mapEffect = new MapleMapEffect(msg, itemId);
        this.mapEffect.setJukebox(jukebox);
        broadcastMessage(this.mapEffect.makeStartData());
        MapTimer.getInstance().schedule(new Runnable() {

            public void run() {
                if (MapleMap.this.mapEffect != null) {
                    broadcastMessage(mapEffect.makeDestroyData());
                    mapEffect = null;
                }
            }
        }, jukebox ? 300000L : 15000L);
    }

    public final void startMapEffect(String msg, int itemId, int time) {
        if (this.mapEffect != null) {
            return;
        }
        if (time <= 0) {
            time = 5;
        }
        this.mapEffect = new MapleMapEffect(msg, itemId);
        this.mapEffect.setJukebox(false);
        broadcastMessage(this.mapEffect.makeStartData());
        MapTimer.getInstance().schedule(new Runnable() {

            public void run() {
                if (mapEffect != null) {
                    broadcastMessage(MapleMap.this.mapEffect.makeDestroyData());
                    mapEffect = null;
                }
            }
        }, time * 1000);
    }

    public final void startExtendedMapEffect(final String msg, final int itemId) {
        broadcastMessage(MaplePacketCreator.startMapEffect(msg, itemId, true));
        MapTimer.getInstance().schedule(new Runnable() {

            public void run() {
                MapleMap.this.broadcastMessage(MaplePacketCreator.removeMapEffect());
                MapleMap.this.broadcastMessage(MaplePacketCreator.startMapEffect(msg, itemId, false));
            }
        }, 60000L);
    }

    public void startSimpleMapEffect(String msg, int itemId) {
        broadcastMessage(MaplePacketCreator.startMapEffect(msg, itemId, true));
    }

    public void startJukebox(String msg, int itemId) {
        startMapEffect(msg, itemId, true);
    }

    public void addPlayer(MapleCharacter chr) {
        ((ReentrantReadWriteLock) this.mapobjectlocks.get(MapleMapObjectType.PLAYER)).writeLock().lock();
        try {
            ((LinkedHashMap) this.mapobjects.get(MapleMapObjectType.PLAYER)).put(Integer.valueOf(chr.getObjectId()), chr);
        } finally {
            ((ReentrantReadWriteLock) this.mapobjectlocks.get(MapleMapObjectType.PLAYER)).writeLock().unlock();
        }
        this.charactersLock.writeLock().lock();
        try {
            this.characters.add(chr);
        } finally {
            this.charactersLock.writeLock().unlock();
        }
        chr.setChangeTime();
        if ((GameConstants.isTeamMap(this.mapid)) && (!chr.inPVP())) {
            chr.setTeam(getAndSwitchTeam() ? 0 : 1);
        }
        byte[] packet = MaplePacketCreator.spawnPlayerMapobject(chr);
        if (!chr.isHidden()) {
            broadcastMessage(chr, packet, false);
            if ((chr.isIntern()) && (this.speedRunStart > 0L)) {
                endSpeedRun();
                broadcastMessage(MaplePacketCreator.serverNotice(5, "The speed run has ended."));
            }
        } else {
            broadcastGMMessage(chr, packet, false);
        }
        if (!chr.isClone()) {
            if ((!this.onFirstUserEnter.equals(""))
                    && (getCharactersSize() == 1)) {
                MapScriptMethods.startScript_FirstUser(chr.getClient(), this.onFirstUserEnter);
            }

            sendObjectPlacement(chr);

            chr.getClient().getSession().write(packet);

            if (!this.onUserEnter.equals("")) {
                MapScriptMethods.startScript_User(chr.getClient(), this.onUserEnter);
            }
            GameConstants.achievementRatio(chr.getClient());

            if ((GameConstants.isTeamMap(this.mapid)) && (!chr.inPVP())) {
                chr.getClient().getSession().write(MaplePacketCreator.showEquipEffect(chr.getTeam()));
            }
            switch (this.mapid) {
                case 809000101:
                case 809000201:
                    chr.getClient().getSession().write(MaplePacketCreator.showEquipEffect());
                    break;
                case 689000000:
                case 689000010:
                    chr.getClient().getSession().write(MaplePacketCreator.getCaptureFlags(this));
            }
        }

        for (MaplePet pet : chr.getPets()) {
            if (pet.getSummoned()) {
                pet.setPos(chr.getTruePosition());
                chr.getClient().getSession().write(PetPacket.updatePet(pet, chr.getInventory(MapleInventoryType.CASH).getItem((short) (byte) pet.getInventoryPosition()), true));
                broadcastMessage(chr, PetPacket.showPet(chr, pet, false, false), false);
            }
        }
        if (chr.getSummonedFamiliar() != null) {
            chr.spawnFamiliar(chr.getSummonedFamiliar());
        }
       if (chr.getAndroid() != null) {//安卓召唤
           // chr.getAndroid().setPos(chr.getTruePosition());
            chr.getAndroid().setPos(chr.getPosition());
            broadcastMessage(AndroidPacket.spawnAndroid(chr, chr.getAndroid()));
        }
        if ((chr.getParty() != null) && (!chr.isClone())) {
            chr.silentPartyUpdate();
            chr.getClient().getSession().write(PartyPacket.updateParty(chr.getClient().getChannel(), chr.getParty(), PartyOperation.更新队伍, null));
            chr.updatePartyMemberHP();
            chr.receivePartyMemberHP();
        }
        if ((!chr.isInBlockedMap()) && (chr.getLevel() > 10)) {
            chr.getClient().getSession().write(MaplePacketCreator.showQuickMove(chr));
        }
        if (!chr.isClone()) {
            List<MapleSummon> ss = chr.getSummonsReadLock();
            try {
                for (MapleSummon summon : ss) {
                    summon.setPosition(chr.getTruePosition());
                    chr.addVisibleMapObject(summon);
                    spawnSummon(summon);
                }
            } finally {
                chr.unlockSummonsReadLock();
            }
        }
        if (this.mapEffect != null) {
            this.mapEffect.sendStartData(chr.getClient());
        }
        if ((this.timeLimit > 0) && (getForcedReturnMap() != null) && (!chr.isClone())) {
            chr.startMapTimeLimitTask(this.timeLimit, getForcedReturnMap());
        }
        if ((chr.getBuffedValue(MapleBuffStat.骑兽技能) != null) && (!GameConstants.is反抗者(chr.getJob()))
                && (FieldLimitType.Mount.check(this.fieldLimit))) {
            chr.cancelEffectFromBuffStat(MapleBuffStat.骑兽技能);
        }

        if (!chr.isClone()) {
            if (chr.getSidekick() != null) {
                MapleCharacter side = getCharacterById(chr.getSidekick().getCharacter(chr.getSidekick().getCharacter(0).getId() == chr.getId() ? 1 : 0).getId());
                if (side != null) {
                    chr.getSidekick().applyBuff(side);
                    chr.getSidekick().applyBuff(chr);
                }
            }
            if ((chr.getEventInstance() != null) && (chr.getEventInstance().isTimerStarted()) && (!chr.isClone())) {
                if (chr.inPVP()) {
                    chr.getClient().getSession().write(MaplePacketCreator.getPVPClock(Integer.parseInt(chr.getEventInstance().getProperty("type")), (int) (chr.getEventInstance().getTimeLeft() / 1000L)));
                } else {
                    chr.getClient().getSession().write(MaplePacketCreator.getClock((int) (chr.getEventInstance().getTimeLeft() / 1000L)));
                }
            }
            if (hasClock()) {
                Calendar cal = Calendar.getInstance();
                chr.getClient().getSession().write(MaplePacketCreator.getClockTime(cal.get(11), cal.get(12), cal.get(13)));
            }
            if ((chr.getCarnivalParty() != null) && (chr.getEventInstance() != null)) {
                chr.getEventInstance().onMapLoad(chr);
            }
            MapleEvent.mapLoad(chr, this.channel);
            if ((getSquadBegin() != null) && (getSquadBegin().getTimeLeft() > 0L) && (getSquadBegin().getStatus() == 1)) {
                chr.getClient().getSession().write(MaplePacketCreator.getClock((int) (getSquadBegin().getTimeLeft() / 1000L)));
            }
            //远征队系类任务使用 申请远征队任务时剩下的设定时间 时间结束 不退场。
            if ((this.mapid / 1000 != 105100) && (this.mapid / 100 != 8020003) && (this.mapid / 100 != 8020008) && (this.mapid != 271040100) && (this.mapid != 703200400) && (this.mapid != 272020200)) {
                MapleSquad sqd = getSquadByMap();
                EventManager em = getEMByMap();
                if ((!this.squadTimer) && (sqd != null) && (chr.getName().equals(sqd.getLeaderName())) && (em != null) && (em.getProperty("leader") != null) && (em.getProperty("leader").equals("true")) && (this.checkStates)) {
                    doShrine(false);
                    this.squadTimer = true;
                }
            }
            if ((getNumMonsters() > 0) && ((this.mapid == 280030001) || (this.mapid == 240060201) || (this.mapid == 280030000) || (this.mapid == 240060200) || (this.mapid == 220080001) || (this.mapid == 541020800) || (this.mapid == 541010100))) {
                String music = "Bgm09/TimeAttack";
                switch (this.mapid) {
                    case 240060200:
                    case 240060201:
                        music = "Bgm14/HonTale";
                        break;
                    case 280030000:
                    case 280030001:
                        music = "Bgm06/FinalFight";
                }

                chr.getClient().getSession().write(MaplePacketCreator.musicChange(music));
            }

            for (WeakReference chrz : chr.getClones()) {
                if (chrz.get() != null) {
                    ((MapleCharacter) chrz.get()).setPosition(chr.getTruePosition());
                    ((MapleCharacter) chrz.get()).setMap(this);
                    addPlayer((MapleCharacter) chrz.get());
                }
            }
            if ((this.mapid == 914000000) || (this.mapid == 927000000)) {
                chr.getClient().getSession().write(MaplePacketCreator.temporaryStats_Aran());
            } else if ((this.mapid == 105100300) && (chr.getLevel() >= 91)) {
                chr.getClient().getSession().write(MaplePacketCreator.temporaryStats_Balrog(chr));
            } else if ((this.mapid == 140090000) || (this.mapid == 105100301) || (this.mapid == 105100401) || (this.mapid == 105100100)) {
                chr.getClient().getSession().write(MaplePacketCreator.temporaryStats_Reset());
            }
        }
        if ((GameConstants.is龙神(chr.getJob())) && (chr.getJob() >= 2200)) {
            if (chr.getDragon() == null) {
                chr.makeDragon();
            } else {
                chr.getDragon().setPosition(chr.getPosition());
            }
            if (chr.getDragon() != null) {
                broadcastMessage(MaplePacketCreator.spawnDragon(chr.getDragon()));
            }
        }
        if (((this.mapid == 10000) && (chr.getJob() == 0)) || ((this.mapid == 130030000) && (chr.getJob() == 1000)) || ((this.mapid == 914000000) && (chr.getJob() == 2000)) || ((this.mapid == 900010000) && (chr.getJob() == 2001)) || ((this.mapid == 931000000) && (chr.getJob() == 3000))) {
            chr.getClient().getSession().write(MaplePacketCreator.startMapEffect(new StringBuilder().append("欢迎来到 ").append(chr.getClient().getChannelServer().getServerName()).append("!").toString(), 5122000, true));
            chr.dropMessage(1, new StringBuilder().append("欢迎来到 ").append(chr.getClient().getChannelServer().getServerName()).append(", ").append(chr.getName()).append(" ！\r\n使用 @help 可以查看你当前能使用的命令\r\n祝你玩的愉快！").toString());
            chr.dropMessage(5, "使用 @help 可以查看你当前能使用的命令 祝你玩的愉快！");
            if (chr.getMaplewingJS("tmeso") > 10000) {
                int tmeso = chr.getMaplewingJS("tmeso");
                int jmeso = chr.getMaplewingJS("jmeso");
                String names = chr.getVipname()+ " 您的金币财富总额达到：" + tmeso + " 万金币！从现在起系统会定时自动扣除您" + jmeso + "万金币！";
                chr.dropMessage(-1, names);
                chr.dropMessage(-11, names);
                
            }
        }
        if (this.permanentWeather > 0) {
            chr.getClient().getSession().write(MaplePacketCreator.startMapEffect("", this.permanentWeather, false));
        }
        if (getPlatforms().size() > 0) {
            chr.getClient().getSession().write(MaplePacketCreator.getMovingPlatforms(this));
        }
        if (this.environment.size() > 0) {
            chr.getClient().getSession().write(MaplePacketCreator.getUpdateEnvironment(this));
        }
        if ((this.partyBonusRate <= 0)
                || (isTown())) {
            chr.cancelEffectFromBuffStat(MapleBuffStat.地雷);
        }
        if (!canSoar()) {
            chr.cancelEffectFromBuffStat(MapleBuffStat.飞行骑乘);
        }
        if ((chr.getJob() < 3200) || (chr.getJob() > 3212)) {
            chr.cancelEffectFromBuffStat(MapleBuffStat.幻灵灵气);
        }
        chr.getClient().getSession().write(MaplePacketCreator.显示免费时空(chr.getBossLog("超时空卷"), (int) Math.ceil(chr.getCSPoints(2) / 200)));
    }

    public int getNumItems() {
        ((ReentrantReadWriteLock) this.mapobjectlocks.get(MapleMapObjectType.ITEM)).readLock().lock();
        try {
            int i = ((LinkedHashMap) this.mapobjects.get(MapleMapObjectType.ITEM)).size();
            return i;
        } finally {
            mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().unlock();
        }
    }

    public int getNumMonsters() {
        ((ReentrantReadWriteLock) this.mapobjectlocks.get(MapleMapObjectType.MONSTER)).readLock().lock();
        try {
            int i = ((LinkedHashMap) this.mapobjects.get(MapleMapObjectType.MONSTER)).size();
            return i;
        } finally {
            mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().unlock();
        }
    }

    public void doShrine(final boolean spawned) {
        if (this.squadSchedule != null) {
            cancelSquadSchedule(true);
        }
        MapleSquad sqd = getSquadByMap();
        if (sqd == null) {
            return;
        }
        final int mode = (this.mapid == 240060200) || (this.mapid == 240060201) ? 3 : this.mapid == 280030001 ? 2 : this.mapid == 280030000 ? 1 : 0;

        EventManager em = getEMByMap();
        if ((sqd != null) && (em != null) && (getCharactersSize() > 0)) {
            final String leaderName = sqd.getLeaderName();
            final String state = em.getProperty("state");

            MapleMap returnMapa = getForcedReturnMap();
            if ((returnMapa == null) || (returnMapa.getId() == this.mapid)) {
                returnMapa = getReturnMap();
            }
            if (((mode == 1) || (mode == 2)
                    || (mode != 3))
                    || (spawned)) {
                broadcastMessage(MaplePacketCreator.getClock(300));
            }
            final MapleMap returnMapz = returnMapa;
            Runnable run;
            if (!spawned) {
                final List<MapleMonster> monsterz = getAllMonstersThreadsafe();
                final List<Integer> monsteridz = new ArrayList<Integer>();
                for (MapleMapObject m : monsterz) {
                    monsteridz.add(Integer.valueOf(m.getObjectId()));
                }
                run = new Runnable() {

                    public void run() {
                        MapleSquad sqnow = MapleMap.this.getSquadByMap();
                        if ((MapleMap.this.getCharactersSize() > 0) && (MapleMap.this.getNumMonsters() == monsterz.size()) && (sqnow != null) && (sqnow.getStatus() == 2) && (sqnow.getLeaderName().equals(leaderName)) && (MapleMap.this.getEMByMap().getProperty("state").equals(state))) {
                            boolean passed = monsterz.isEmpty();
                            for (MapleMapObject m : MapleMap.this.getAllMonstersThreadsafe()) {
                                for (int i : monsteridz) {
                                    if (m.getObjectId() == i) {
                                        passed = true;
                                        break;
                                    }
                                }
                                if (passed) {
                                    break;
                                }
                            }
                            if (passed) {
                                byte[] packet;
                                if ((mode == 1) || (mode == 2)) {
                                    packet = MaplePacketCreator.showChaosZakumShrine(spawned, 0);
                                } else {
                                    packet = MaplePacketCreator.showHorntailShrine(spawned, 0);
                                }
                                for (MapleCharacter chr : MapleMap.this.getCharactersThreadsafe()) {
                                    chr.changeMap(returnMapz, returnMapz.getPortal(0));
                                }
                                MapleMap.this.checkStates("");
                                MapleMap.this.resetFully();
                            }
                        }
                    }
                };
            } else {
                run = new Runnable() {

                    public void run() {
                        MapleSquad sqnow = MapleMap.this.getSquadByMap();

                        if ((MapleMap.this.getCharactersSize() > 0) && (sqnow != null) && (sqnow.getStatus() == 2) && (sqnow.getLeaderName().equals(leaderName)) && (MapleMap.this.getEMByMap().getProperty("state").equals(state))) {
                            byte[] packet;
                            if ((mode == 1) || (mode == 2)) {
                                packet = MaplePacketCreator.showChaosZakumShrine(spawned, 0);
                            } else {
                                packet = MaplePacketCreator.showHorntailShrine(spawned, 0);
                            }
                            for (MapleCharacter chr : MapleMap.this.getCharactersThreadsafe()) {
                                chr.changeMap(returnMapz, returnMapz.getPortal(0));
                            }
                            MapleMap.this.checkStates("");
                            MapleMap.this.resetFully();
                        }
                    }
                };
            }
            this.squadSchedule = MapTimer.getInstance().schedule(run, 300000L);
        }
    }

    public MapleSquad getSquadByMap() {
        MapleSquad.MapleSquadType zz = null;
        switch (this.mapid) {
            case 105100300:
            case 105100400:
                zz = MapleSquad.MapleSquadType.bossbalrog;
                break;
            case 280030000:
                zz = MapleSquad.MapleSquadType.zak;
                break;
            case 280030001:
                zz = MapleSquad.MapleSquadType.chaoszak;
                break;
            case 240060200:
                zz = MapleSquad.MapleSquadType.horntail;
                break;
            case 240060201:
                zz = MapleSquad.MapleSquadType.chaosht;
                break;
            case 270050100:
                zz = MapleSquad.MapleSquadType.pinkbean;
                break;
            case 802000111:
                zz = MapleSquad.MapleSquadType.nmm_squad;
                break;
            case 802000211:
                zz = MapleSquad.MapleSquadType.vergamot;
                break;
            case 802000311:
                zz = MapleSquad.MapleSquadType.tokyo_2095;
                break;
            case 802000411:
                zz = MapleSquad.MapleSquadType.dunas;
                break;
            case 802000611:
                zz = MapleSquad.MapleSquadType.nibergen_squad;
                break;
            case 802000711:
                zz = MapleSquad.MapleSquadType.dunas2;
                break;
            case 272020200:
                zz = MapleSquad.MapleSquadType.akayile;
                break;
            case 262030300:
                zz = MapleSquad.MapleSquadType.xila;
                break;
            case 802000801:
            case 802000802:
            case 802000803:
                zz = MapleSquad.MapleSquadType.core_blaze;
                break;
            case 802000821:
            case 802000823:
                zz = MapleSquad.MapleSquadType.aufheben;
                break;
            case 211070100:
            case 211070101:
            case 211070110:
                zz = MapleSquad.MapleSquadType.vonleon;
                break;
            case 551030200:
                zz = MapleSquad.MapleSquadType.scartar;
                break;
            case 271040100:
                zz = MapleSquad.MapleSquadType.cygnus;
                break;
            case 689013000:
                zz = MapleSquad.MapleSquadType.pinkzak;
                break;
            case 703200400:
                zz = MapleSquad.MapleSquadType.allboss;
                break;
            default:
                return null;
        }
        return ChannelServer.getInstance(this.channel).getMapleSquad(zz);
    }

    public MapleSquad getSquadBegin() {
        if (this.squad != null) {
            return ChannelServer.getInstance(this.channel).getMapleSquad(this.squad);
        }
        return null;
    }
//远征队 副本名称要对应！
    public EventManager getEMByMap() {
        String em = null;
        switch (this.mapid) {
            case 105100400:
                em = "BossBalrog_EASY";
                break;
            case 105100300:
                em = "BossBalrog_NORMAL";
                break;
            case 280030000:
                em = "ZakumBattle";
                break;
            case 240060200:
                em = "HorntailBattle";
                break;
            case 280030001:
                em = "ChaosZakum";
                break;
            case 240060201:
                em = "ChaosHorntail";
                break;
            case 270050100:
                em = "PinkBeanBattle";
                break;
            case 802000111:
                em = "NamelessMagicMonster";
                break;
            case 802000211:
                em = "Vergamot";
                break;
            case 802000311:
                em = "2095_tokyo";
                break;
            case 802000411:
                em = "Dunas";
                break;
            case 802000611:
                em = "Nibergen";
                break;
            case 802000711:
                em = "Dunas2";
                break;
            case 272020200:
                em = "Akayile";
                break;
            case 262030300:
                em = "Xila";
                break;
            case 802000801:
            case 802000802:
            case 802000803:
                em = "CoreBlaze";
                break;
            case 802000821:
            case 802000823:
                em = "Aufhaven";
                break;
            case 211070100:
            case 211070101:
            case 211070110:
                em = "VonLeonBattle";
                break;
            case 551030200:
                em = "ScarTarBattle";
                break;
            case 271040100:
                em = "CygnusBattle";
                break;
            case 689013000:
                em = "PinkZakum";
                break;
            case 703200400:
                em = "0AllBoss";
                break;
            default:
                return null;
        }
        return ChannelServer.getInstance(this.channel).getEventSM().getEventManager(em);
    }

    public void removePlayer(MapleCharacter chr) {
        if (this.everlast) {
            returnEverLastItem(chr);
        }

        this.charactersLock.writeLock().lock();
        try {
            this.characters.remove(chr);
        } finally {
            this.charactersLock.writeLock().unlock();
        }
        removeMapObject(chr);
        chr.checkFollow();
        chr.removeExtractor();
        chr.cancelEffectFromBuffStat(MapleBuffStat.SIDEKICK_PASSIVE);
        if (chr.getSidekick() != null) {
            MapleCharacter side = getCharacterById(chr.getSidekick().getCharacter(chr.getSidekick().getCharacter(0).getId() == chr.getId() ? 1 : 0).getId());
            if (side != null) {
                side.cancelEffectFromBuffStat(MapleBuffStat.SIDEKICK_PASSIVE);
            }
        }
        broadcastMessage(MaplePacketCreator.removePlayerFromMap(chr.getId()));

        if (chr.getSummonedFamiliar() != null) {
            chr.removeVisibleFamiliar();
        }
        List<MapleSummon> toCancel = new ArrayList();
        List<MapleSummon> ss = chr.getSummonsReadLock();
        try {
            for (MapleSummon summon : ss) {
                broadcastMessage(MaplePacketCreator.removeSummon(summon, true));
                removeMapObject(summon);
                if ((summon.getMovementType() == SummonMovementType.STATIONARY) || (summon.getMovementType() == SummonMovementType.CIRCLE_STATIONARY) || (summon.getMovementType() == SummonMovementType.WALK_STATIONARY)) {
                    toCancel.add(summon);
                } else {
                    summon.setChangedMap(true);
                }
            }
        } finally {
            chr.unlockSummonsReadLock();
        }
        for (MapleSummon summon : toCancel) {
            chr.removeSummon(summon);
            chr.dispelSkill(summon.getSkill());
        }
        if (!chr.isClone()) {
            checkStates(chr.getName());
            if (this.mapid == 109020001) {
                chr.canTalk(true);
            }
            for (WeakReference chrz : chr.getClones()) {
                if (chrz.get() != null) {
                    removePlayer((MapleCharacter) chrz.get());
                }
            }
            chr.leaveMap(this);
        }
    }

    public void broadcastMessage(byte[] packet) {
        broadcastMessage(null, packet, (1.0D / 0.0D), null);
    }

    public void broadcastMessage(MapleCharacter source, byte[] packet, boolean repeatToSource) {
        broadcastMessage(repeatToSource ? null : source, packet, (1.0D / 0.0D), source.getTruePosition());
    }

    public void broadcastMessage(byte[] packet, Point rangedFrom) {
        broadcastMessage(null, packet, GameConstants.maxViewRangeSq(), rangedFrom);
    }

    public void broadcastMessage(MapleCharacter source, byte[] packet, Point rangedFrom) {
        broadcastMessage(source, packet, GameConstants.maxViewRangeSq(), rangedFrom);
    }

    public void broadcastMessage(MapleCharacter source, byte[] packet, double rangeSq, Point rangedFrom) {
        this.charactersLock.readLock().lock();
        try {
            for (MapleCharacter chr : this.characters) {
                if (chr != source) {
                    if (rangeSq < (1.0D / 0.0D)) {
                        if (rangedFrom.distanceSq(chr.getTruePosition()) <= rangeSq) {
                            chr.getClient().getSession().write(packet);
                        }
                    } else {
                        chr.getClient().getSession().write(packet);
                    }
                }
            }
        } finally {
            this.charactersLock.readLock().unlock();
        }
    }

    private void sendObjectPlacement(MapleCharacter c) {
        if ((c == null) || (c.isClone())) {
            return;
        }
        for (MapleMapObject o : getMapObjectsInRange(c.getTruePosition(), c.getRange(), GameConstants.rangedMapobjectTypes)) {
            if ((o.getType() == MapleMapObjectType.REACTOR)
                    && (!((MapleReactor) o).isAlive())) {
                continue;
            }
            o.sendSpawnData(c.getClient());
            c.addVisibleMapObject(o);
        }
    }

    public List<MaplePortal> getPortalsInRange(Point from, double rangeSq) {
        List ret = new ArrayList();
        for (MaplePortal type : this.portals.values()) {
            if ((from.distanceSq(type.getPosition()) <= rangeSq) && (type.getTargetMapId() != this.mapid) && (type.getTargetMapId() != 999999999)) {
                ret.add(type);
            }
        }
        return ret;
    }

    public List<MapleMapObject> getMapObjectsInRange(Point from, double rangeSq) {
        List ret = new ArrayList();
        for (MapleMapObjectType type : MapleMapObjectType.values()) {
            ((ReentrantReadWriteLock) this.mapobjectlocks.get(type)).readLock().lock();
            try {
                Iterator itr = ((LinkedHashMap) this.mapobjects.get(type)).values().iterator();
                while (itr.hasNext()) {
                    MapleMapObject mmo = (MapleMapObject) itr.next();
                    if (from.distanceSq(mmo.getTruePosition()) <= rangeSq) {
                        ret.add(mmo);
                    }
                }
            } finally {
                ((ReentrantReadWriteLock) this.mapobjectlocks.get(type)).readLock().unlock();
            }
        }
        return ret;
    }

    public List<MapleMapObject> getItemsInRange(Point from, double rangeSq) {
        return getMapObjectsInRange(from, rangeSq, Arrays.asList(new MapleMapObjectType[]{MapleMapObjectType.ITEM}));
    }

    public List<MapleMapObject> getMapObjectsInRange(Point from, double rangeSq, List<MapleMapObjectType> MapObject_types) {
        List ret = new ArrayList();
        for (MapleMapObjectType type : MapObject_types) {
            ((ReentrantReadWriteLock) this.mapobjectlocks.get(type)).readLock().lock();
            try {
                Iterator itr = ((LinkedHashMap) this.mapobjects.get(type)).values().iterator();
                while (itr.hasNext()) {
                    MapleMapObject mmo = (MapleMapObject) itr.next();
                    if (from.distanceSq(mmo.getTruePosition()) <= rangeSq) {
                        ret.add(mmo);
                    }
                }
            } finally {
                ((ReentrantReadWriteLock) this.mapobjectlocks.get(type)).readLock().unlock();
            }
        }
        return ret;
    }

    public List<MapleMapObject> getMapObjectsInRect(Rectangle box, List<MapleMapObjectType> MapObject_types) {
        List ret = new ArrayList();
        for (MapleMapObjectType type : MapObject_types) {
            ((ReentrantReadWriteLock) this.mapobjectlocks.get(type)).readLock().lock();
            try {
                Iterator itr = ((LinkedHashMap) this.mapobjects.get(type)).values().iterator();
                while (itr.hasNext()) {
                    MapleMapObject mmo = (MapleMapObject) itr.next();
                    if (box.contains(mmo.getTruePosition())) {
                        ret.add(mmo);
                    }
                }
            } finally {
                ((ReentrantReadWriteLock) this.mapobjectlocks.get(type)).readLock().unlock();
            }
        }
        return ret;
    }

    public List<MapleCharacter> getCharactersIntersect(Rectangle box) {
        List ret = new ArrayList();
        this.charactersLock.readLock().lock();
        try {
            for (MapleCharacter chr : this.characters) {
                if (chr.getBounds().intersects(box)) {
                    ret.add(chr);
                }
            }
        } finally {
            this.charactersLock.readLock().unlock();
        }
        return ret;
    }

    public List<MapleCharacter> getPlayersInRectAndInList(Rectangle box, List<MapleCharacter> chrList) {
        List character = new LinkedList();

        this.charactersLock.readLock().lock();
        try {
            Iterator ltr = this.characters.iterator();

            while (ltr.hasNext()) {
                MapleCharacter a = (MapleCharacter) ltr.next();
                if ((chrList.contains(a)) && (box.contains(a.getTruePosition()))) {
                    character.add(a);
                }
            }
        } finally {
            this.charactersLock.readLock().unlock();
        }
        return character;
    }

    public void addPortal(MaplePortal myPortal) {
        this.portals.put(Integer.valueOf(myPortal.getId()), myPortal);
    }

    public MaplePortal getPortal(String portalname) {
        for (MaplePortal port : this.portals.values()) {
            if (port.getName().equals(portalname)) {
                return port;
            }
        }
        return null;
    }

    public MaplePortal getPortal(int portalid) {
        return (MaplePortal) this.portals.get(Integer.valueOf(portalid));
    }

    public void resetPortals() {
        for (MaplePortal port : this.portals.values()) {
            port.setPortalState(true);
        }
    }

    public void setFootholds(MapleFootholdTree footholds) {
        this.footholds = footholds;
    }

    public MapleFootholdTree getFootholds() {
        return this.footholds;
    }

    public int getNumSpawnPoints() {
        return this.monsterSpawn.size();
    }

    public void loadMonsterRate(boolean first) {
        int spawnSize = this.monsterSpawn.size();
        if ((spawnSize >= 20) || (this.partyBonusRate > 0)) {
            this.maxRegularSpawn = Math.round(spawnSize / this.monsterRate);
        } else {
            this.maxRegularSpawn = (int) Math.ceil(spawnSize * this.monsterRate);
        }
        if (this.fixedMob > 0) {
            this.maxRegularSpawn = this.fixedMob;
        } else if (this.maxRegularSpawn <= 2) {
            this.maxRegularSpawn = 2;
        } else if (this.maxRegularSpawn > spawnSize) {
            this.maxRegularSpawn = Math.max(10, spawnSize);
        }

        Collection newSpawn = new LinkedList();
        Collection newBossSpawn = new LinkedList();
        for (Spawns s : this.monsterSpawn) {
            if (s.getCarnivalTeam() >= 2) {
                continue;
            }
            if (s.getMonster().isBoss()) {
                newBossSpawn.add(s);
            } else {
                newSpawn.add(s);
            }
        }
        this.monsterSpawn.clear();
        this.monsterSpawn.addAll(newBossSpawn);
        this.monsterSpawn.addAll(newSpawn);

        if ((first) && (spawnSize > 0)) {
            this.lastSpawnTime = System.currentTimeMillis();
            if (GameConstants.isForceRespawn(this.mapid)) {
                this.createMobInterval = 15000;
            }
        }
    }

    public SpawnPoint addMonsterSpawn(MapleMonster monster, int mobTime, byte carnivalTeam, String msg) {
        Point newpos = calcPointBelow(monster.getPosition());
        newpos.y -= 1;
        SpawnPoint sp = new SpawnPoint(monster, newpos, mobTime, carnivalTeam, msg);
        if (carnivalTeam > -1) {
            this.monsterSpawn.add(0, sp);
        } else {
            this.monsterSpawn.add(sp);
        }
        return sp;
    }

    public void addAreaMonsterSpawn(MapleMonster monster, Point pos1, Point pos2, Point pos3, int mobTime, String msg, boolean shouldSpawn) {
        pos1 = calcPointBelow(pos1);
        pos2 = calcPointBelow(pos2);
        pos3 = calcPointBelow(pos3);
        if (pos1 != null) {
            pos1.y -= 1;
        }
        if (pos2 != null) {
            pos2.y -= 1;
        }
        if (pos3 != null) {
            pos3.y -= 1;
        }
        if ((pos1 == null) && (pos2 == null) && (pos3 == null)) {
            System.out.println(new StringBuilder().append("WARNING: mapid ").append(this.mapid).append(", monster ").append(monster.getId()).append(" could not be spawned.").toString());
            return;
        }
        if (pos1 != null) {
            if (pos2 == null) {
                pos2 = new Point(pos1);
            }
            if (pos3 == null) {
                pos3 = new Point(pos1);
            }
        } else if (pos2 != null) {
            if (pos1 == null) {
                pos1 = new Point(pos2);
            }
            if (pos3 == null) {
                pos3 = new Point(pos2);
            }
        } else if (pos3 != null) {
            if (pos1 == null) {
                pos1 = new Point(pos3);
            }
            if (pos2 == null) {
                pos2 = new Point(pos3);
            }
        }
        this.monsterSpawn.add(new SpawnPointAreaBoss(monster, pos1, pos2, pos3, mobTime, msg, shouldSpawn));
    }

    public List<MapleCharacter> getCharacters() {
        return getCharactersThreadsafe();
    }

    public List<MapleCharacter> getCharactersThreadsafe() {
        List chars = new ArrayList();

        this.charactersLock.readLock().lock();
        try {
            for (MapleCharacter mc : this.characters) {
                chars.add(mc);
            }
        } finally {
            this.charactersLock.readLock().unlock();
        }
        return chars;
    }

    public MapleCharacter getCharacterByName(String id) {
        this.charactersLock.readLock().lock();
        try {
            for (MapleCharacter mc : this.characters) {
                if (mc.getName().equalsIgnoreCase(id)) {
                    MapleCharacter localMapleCharacter1 = mc;
                    return localMapleCharacter1;
                }
            }
        } finally {
            this.charactersLock.readLock().unlock();
        }
        return null;
    }

    public MapleCharacter getCharacterById_InMap(int id) {
        return getCharacterById(id);
    }

    public MapleCharacter getCharacterById(int id) {
        this.charactersLock.readLock().lock();
        try {
            for (MapleCharacter mc : this.characters) {
                if (mc.getId() == id) {
                    MapleCharacter localMapleCharacter1 = mc;
                    return localMapleCharacter1;
                }
            }
        } finally {
            this.charactersLock.readLock().unlock();
        }
        return null;
    }

    public void updateMapObjectVisibility(MapleCharacter chr, MapleMapObject mo) {
        if ((chr == null) || (chr.isClone())) {
            return;
        }
        if (!chr.isMapObjectVisible(mo)) {
            if ((mo.getType() == MapleMapObjectType.MIST) || (mo.getType() == MapleMapObjectType.EXTRACTOR) || (mo.getType() == MapleMapObjectType.SUMMON) || (mo.getType() == MapleMapObjectType.FAMILIAR) || ((mo instanceof MechDoor)) || (mo.getTruePosition().distanceSq(chr.getTruePosition()) <= mo.getRange())) {
                chr.addVisibleMapObject(mo);
                mo.sendSpawnData(chr.getClient());
            }
        } else if ((!(mo instanceof MechDoor)) && (mo.getType() != MapleMapObjectType.MIST) && (mo.getType() != MapleMapObjectType.EXTRACTOR) && (mo.getType() != MapleMapObjectType.SUMMON) && (mo.getType() != MapleMapObjectType.FAMILIAR) && (mo.getTruePosition().distanceSq(chr.getTruePosition()) > mo.getRange())) {
            chr.removeVisibleMapObject(mo);
            mo.sendDestroyData(chr.getClient());
        } else if ((mo.getType() == MapleMapObjectType.MONSTER)
                && (chr.getTruePosition().distanceSq(mo.getTruePosition()) <= GameConstants.maxViewRangeSq_Half())) {
            updateMonsterController((MapleMonster) mo);
        }
    }

    public void moveMonster(MapleMonster monster, Point reportedPos) {
        monster.setPosition(reportedPos);

        this.charactersLock.readLock().lock();
        try {
            for (MapleCharacter mc : this.characters) {
                updateMapObjectVisibility(mc, monster);
            }
        } finally {
            this.charactersLock.readLock().unlock();
        }
    }

    public void movePlayer(MapleCharacter player, Point newPosition) {
        Collection visibleObjects;
        player.setPosition(newPosition);
        if (!player.isClone()) {
            try {
                visibleObjects = player.getAndWriteLockVisibleMapObjects();
                ArrayList copy = new ArrayList(visibleObjects);
                Iterator itr = copy.iterator();
                while (itr.hasNext()) {
                    MapleMapObject mo = (MapleMapObject) itr.next();
                    if ((mo != null) && (getMapObject(mo.getObjectId(), mo.getType()) == mo)) {
                        updateMapObjectVisibility(player, mo);
                    } else if (mo != null) {
                        visibleObjects.remove(mo);
                    }
                }
                for (MapleMapObject mo : getMapObjectsInRange(player.getTruePosition(), player.getRange())) {
                    if ((mo != null) && (!visibleObjects.contains(mo))) {
                        mo.sendSpawnData(player.getClient());
                        visibleObjects.add(mo);
                    }
                }
            } finally {

                player.unlockWriteVisibleMapObjects();
            }
        }
    }

    public MaplePortal findClosestSpawnpoint(Point from) {
        MaplePortal closest = getPortal(0);
        double shortestDistance = (1.0D / 0.0D);
        for (MaplePortal portal : this.portals.values()) {
            double distance = portal.getPosition().distanceSq(from);
            if ((portal.getType() >= 0) && (portal.getType() <= 2) && (distance < shortestDistance) && (portal.getTargetMapId() == 999999999)) {
                closest = portal;
                shortestDistance = distance;
            }
        }
        return closest;
    }

    public MaplePortal findClosestPortal(Point from) {
        MaplePortal closest = getPortal(0);
        double shortestDistance = (1.0D / 0.0D);
        for (MaplePortal portal : this.portals.values()) {
            double distance = portal.getPosition().distanceSq(from);
            if (distance < shortestDistance) {
                closest = portal;
                shortestDistance = distance;
            }
        }
        return closest;
    }

    public String spawnDebug() {
        StringBuilder sb = new StringBuilder("Mobs in map : ");
        sb.append(getMobsSize());
        sb.append(" spawnedMonstersOnMap: ");
        sb.append(this.spawnedMonstersOnMap);
        sb.append(" spawnpoints: ");
        sb.append(this.monsterSpawn.size());
        sb.append(" maxRegularSpawn: ");
        sb.append(this.maxRegularSpawn);
        sb.append(" actual monsters: ");
        sb.append(getNumMonsters());
        sb.append(" monster rate: ");
        sb.append(this.monsterRate);
        sb.append(" fixed: ");
        sb.append(this.fixedMob);

        return sb.toString();
    }

    public int characterSize() {
        return this.characters.size();
    }

    public int getMapObjectSize() {
        return this.mapobjects.size() + getCharactersSize() - this.characters.size();
    }

    public int getCharactersSize() {
        int ret = 0;
        this.charactersLock.readLock().lock();
        try {
            Iterator ltr = this.characters.iterator();

            while (ltr.hasNext()) {
                MapleCharacter chr = (MapleCharacter) ltr.next();
                if (!chr.isClone()) {
                    ret++;
                }
            }
        } finally {
            this.charactersLock.readLock().unlock();
        }
        return ret;
    }

    public Collection<MaplePortal> getPortals() {
        return Collections.unmodifiableCollection(this.portals.values());
    }

    public int getSpawnedMonstersOnMap() {
        return this.spawnedMonstersOnMap.get();
    }

    public void respawn(boolean force) {
        respawn(force, System.currentTimeMillis());
    }

    public void respawn(boolean force, long now) {
        this.lastSpawnTime = now;
        int numShouldSpawn, spawned;
        if (force) {
            numShouldSpawn = this.monsterSpawn.size() - this.spawnedMonstersOnMap.get();

            if (numShouldSpawn > 0) {
                spawned = 0;

                for (Spawns spawnPoint : this.monsterSpawn) {
                    spawnPoint.spawnMonster(this);
                    spawned++;
                    if (spawned >= numShouldSpawn) {
                        break;
                    }
                }
            }
        } else {
            numShouldSpawn = (GameConstants.isForceRespawn(this.mapid) ? this.monsterSpawn.size() : this.maxRegularSpawn) - this.spawnedMonstersOnMap.get();
            if (numShouldSpawn > 0) {
                spawned = 0;

                List<Spawns> randomSpawn = new ArrayList<Spawns>(monsterSpawn);
                Collections.shuffle(randomSpawn);

                for (Spawns spawnPoint : randomSpawn) {
                    if ((!this.isSpawns) && (spawnPoint.getMobTime() > 0)) {
                        continue;
                    }
                    if ((spawnPoint.shouldSpawn(this.lastSpawnTime)) || (GameConstants.isForceRespawn(this.mapid)) || ((this.monsterSpawn.size() < 10) && (this.maxRegularSpawn > this.monsterSpawn.size()) && (this.partyBonusRate > 0))) {
                        spawnPoint.spawnMonster(this);
                        spawned++;
                    }
                    if (spawned >= numShouldSpawn) {
                        break;
                    }
                }
            }
        }
    }

    public String getSnowballPortal() {
        int[] teamss = new int[2];
        this.charactersLock.readLock().lock();
        try {
            for (MapleCharacter chr : this.characters) {
                if (chr.getTruePosition().y > -80) {
                    teamss[0] += 1;
                } else {
                    teamss[1] += 1;
                }
            }
        } finally {
            this.charactersLock.readLock().unlock();
        }
        if (teamss[0] > teamss[1]) {
            return "st01";
        }
        return "st00";
    }

    public boolean isDisconnected(int id) {
        return this.dced.contains(Integer.valueOf(id));
    }

    public void addDisconnected(int id) {
        this.dced.add(Integer.valueOf(id));
    }

    public void resetDisconnected() {
        this.dced.clear();
    }

    public void startSpeedRun() {
        MapleSquad squads = getSquadByMap();
        if (squads != null) {
            this.charactersLock.readLock().lock();
            try {
                for (MapleCharacter chr : this.characters) {
                    if ((chr.getName().equals(squads.getLeaderName())) && (!chr.isIntern())) {
                        startSpeedRun(chr.getName());
                        return;
                    }
                }
            } finally {
                this.charactersLock.readLock().unlock();
            }
        }
    }

    public void startSpeedRun(String leader) {
        this.speedRunStart = System.currentTimeMillis();
        this.speedRunLeader = leader;
    }

    public void endSpeedRun() {
        this.speedRunStart = 0L;
        this.speedRunLeader = "";
    }

    public void getRankAndAdd(String leader, String time, ExpeditionType type, long timz, Collection<String> squad) {
        try {
            long lastTime = SpeedRunner.getSpeedRunData(type) == null ? 0L : ((Long) SpeedRunner.getSpeedRunData(type).right).longValue();

            StringBuilder rett = new StringBuilder();
            if (squad != null) {
                for (String chr : squad) {
                    rett.append(chr);
                    rett.append(",");
                }
            }
            String z = rett.toString();
            if (squad != null) {
                z = z.substring(0, z.length() - 1);
            }
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO speedruns(`type`, `leader`, `timestring`, `time`, `members`) VALUES (?,?,?,?,?)");
            ps.setString(1, type.name());
            ps.setString(2, leader);
            ps.setString(3, time);
            ps.setLong(4, timz);
            ps.setString(5, z);
            ps.executeUpdate();
            ps.close();

            if (lastTime == 0L) {
                SpeedRunner.addSpeedRunData(type, SpeedRunner.addSpeedRunData(new StringBuilder(SpeedRunner.getPreamble(type)), new HashMap(), z, leader, 1, time), timz);
            } else {
                SpeedRunner.removeSpeedRunData(type);
                SpeedRunner.loadSpeedRunData(type);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getSpeedRunStart() {
        return this.speedRunStart;
    }

    public final void disconnectAll() {
        for (MapleCharacter chr : getCharactersThreadsafe()) {
            if (!chr.isGM()) {
                chr.getClient().disconnect(true, false);
                chr.getClient().getSession().close();
            }
        }
    }

    public List<MapleNPC> getAllNPCs() {
        return getAllNPCsThreadsafe();
    }

    public List<MapleNPC> getAllNPCsThreadsafe() {
        ArrayList<MapleNPC> ret = new ArrayList<MapleNPC>();
        mapobjectlocks.get(MapleMapObjectType.NPC).readLock().lock();
        try {
            for (MapleMapObject mmo : mapobjects.get(MapleMapObjectType.NPC).values()) {
                ret.add((MapleNPC) mmo);
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.NPC).readLock().unlock();
        }
        return ret;
    }

    public final void resetNPCs() {
        removeNpc(-1);
    }

    public final void resetPQ(int level) {
        resetFully();
        for (MapleMonster mons : getAllMonstersThreadsafe()) {
            mons.changeLevel(level, true);
        }
        resetSpawnLevel(level);
    }

    public final void resetSpawnLevel(int level) {
        for (Spawns spawn : this.monsterSpawn) {
            if ((spawn instanceof SpawnPoint)) {
                ((SpawnPoint) spawn).setLevel(level);
            }
        }
    }

    public final void resetFully() {
        resetFully(true);
    }

    public final void resetFully(boolean respawn) {
        killAllMonsters(false);
        reloadReactors();
        removeDrops();
        resetNPCs();
        resetSpawns();
        resetDisconnected();
        endSpeedRun();
        cancelSquadSchedule(true);
        resetPortals();
        this.environment.clear();
        if (respawn) {
            respawn(true);
        }
    }

    public final void cancelSquadSchedule(boolean interrupt) {
        this.squadTimer = false;
        this.checkStates = true;
        if (this.squadSchedule != null) {
            this.squadSchedule.cancel(interrupt);
            this.squadSchedule = null;
        }
    }

    public final void removeDrops() {
        List<MapleMapItem> items = getAllItemsThreadsafe();
        for (MapleMapItem i : items) {
            i.expire(this);
        }
    }

    public final void resetAllSpawnPoint(int mobid, int mobTime) {
        Collection<Spawns> sss = new LinkedList(this.monsterSpawn);
        resetFully();
        this.monsterSpawn.clear();
        for (Spawns s : sss) {
            MapleMonster newMons = MapleLifeFactory.getMonster(mobid);
            newMons.setF(s.getF());
            newMons.setFh(s.getFh());
            newMons.setPosition(s.getPosition());
            addMonsterSpawn(newMons, mobTime, (byte) -1, null);
        }
        loadMonsterRate(true);
    }

    public final void resetSpawns() {
        boolean changed = false;
        Iterator sss = this.monsterSpawn.iterator();
        while (sss.hasNext()) {
            if (((Spawns) sss.next()).getCarnivalId() > -1) {
                sss.remove();
                changed = true;
            }
        }
        setSpawns(true);
        if (changed) {
            loadMonsterRate(true);
        }
    }

    public final boolean makeCarnivalSpawn(int team, MapleMonster newMons, int num) {
        MapleNodes.MonsterPoint ret = null;
        for (MapleNodes.MonsterPoint mp : this.nodes.getMonsterPoints()) {
            if ((mp.team == team) || (mp.team == -1)) {
                Point newpos = calcPointBelow(new Point(mp.x, mp.y));
                newpos.y -= 1;
                boolean found = false;
                for (Spawns s : this.monsterSpawn) {
                    if ((s.getCarnivalId() > -1) && ((mp.team == -1) || (s.getCarnivalTeam() == mp.team)) && (s.getPosition().x == newpos.x) && (s.getPosition().y == newpos.y)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    ret = mp;
                    break;
                }
            }
        }
        if (ret != null) {
            newMons.setCy(ret.cy);
            newMons.setF(0);
            newMons.setFh(ret.fh);
            newMons.setRx0(ret.x + 50);
            newMons.setRx1(ret.x - 50);
            newMons.setPosition(new Point(ret.x, ret.y));
            newMons.setHide(false);
            SpawnPoint sp = addMonsterSpawn(newMons, 1, (byte) team, null);
            sp.setCarnival(num);
        }
        return ret != null;
    }

    public final boolean makeCarnivalReactor(int team, int num) {
        MapleReactor old = getReactorByName(new StringBuilder().append(team).append("").append(num).toString());
        if ((old != null) && (old.getState() < 5)) {
            return false;
        }
        Point guardz = null;
        List<MapleReactor> react = getAllReactorsThreadsafe();
        for (Pair guard : this.nodes.getGuardians()) {
            if ((((Integer) guard.right).intValue() == team) || (((Integer) guard.right).intValue() == -1)) {
                boolean found = false;
                for (MapleReactor r : react) {
                    if ((r.getTruePosition().x == ((Point) guard.left).x) && (r.getTruePosition().y == ((Point) guard.left).y) && (r.getState() < 5)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    guardz = (Point) guard.left;
                    break;
                }
            }
        }
        MapleCarnivalFactory.MCSkill skil;
        if (guardz != null) {
            MapleReactor my = new MapleReactor(MapleReactorFactory.getReactor(9980000 + team), 9980000 + team);
            my.setState((byte) 1);
            my.setName(new StringBuilder().append(team).append("").append(num).toString());

            spawnReactorOnGroundBelow(my, guardz);
            skil = MapleCarnivalFactory.getInstance().getGuardian(num);
            for (MapleMonster mons : getAllMonstersThreadsafe()) {
                if (mons.getCarnivalTeam() == team) {
                    skil.getSkill().applyEffect(null, mons, false);
                }
            }
        }
        return guardz != null;
    }

    public final void blockAllPortal() {
        for (MaplePortal p : this.portals.values()) {
            p.setPortalState(false);
        }
    }

    public boolean getAndSwitchTeam() {
        return getCharactersSize() % 2 != 0;
    }

    public void setSquad(MapleSquad.MapleSquadType s) {
        this.squad = s;
    }

    public int getChannel() {
        return this.channel;
    }

    public int getConsumeItemCoolTime() {
        return this.consumeItemCoolTime;
    }

    public void setConsumeItemCoolTime(int ciit) {
        this.consumeItemCoolTime = ciit;
    }

    public void setPermanentWeather(int pw) {
        this.permanentWeather = pw;
    }

    public int getPermanentWeather() {
        return this.permanentWeather;
    }

    public void checkStates(String chr) {
        if (!this.checkStates) {
            return;
        }
        MapleSquad sqd = getSquadByMap();
        EventManager em = getEMByMap();
        int size = getCharactersSize();
        if ((sqd != null) && (sqd.getStatus() == 2)) {
            sqd.removeMember(chr);
            if (em != null) {
                if (sqd.getLeaderName().equalsIgnoreCase(chr)) {
                    em.setProperty("leader", "false");
                }
                if ((chr.equals("")) || (size == 0)) {
                    em.setProperty("state", "0");
                    em.setProperty("leader", "true");
                    cancelSquadSchedule(!chr.equals(""));
                    sqd.clear();
                    sqd.copy();
                }
            }
        }
        if ((em != null) && (em.getProperty("state") != null) && ((sqd == null) || (sqd.getStatus() == 2)) && (size == 0)) {
            em.setProperty("state", "0");
            if (em.getProperty("leader") != null) {
                em.setProperty("leader", "true");
            }
        }
        if ((this.speedRunStart > 0L) && (size == 0)) {
            endSpeedRun();
        }
    }

    public void setCheckStates(boolean b) {
        this.checkStates = b;
    }

    public void setNodes(MapleNodes mn) {
        this.nodes = mn;
    }

    public List<MapleNodes.MaplePlatform> getPlatforms() {
        return this.nodes.getPlatforms();
    }

    public Collection<MapleNodes.MapleNodeInfo> getNodes() {
        return this.nodes.getNodes();
    }

    public MapleNodes.MapleNodeInfo getNode(int index) {
        return this.nodes.getNode(index);
    }

    public boolean isLastNode(int index) {
        return this.nodes.isLastNode(index);
    }

    public List<Rectangle> getAreas() {
        return this.nodes.getAreas();
    }

    public Rectangle getArea(int index) {
        return this.nodes.getArea(index);
    }

    public void changeEnvironment(String ms, int type) {
        broadcastMessage(MaplePacketCreator.environmentChange(ms, type));
    }

    public void toggleEnvironment(String ms) {
        if (this.environment.containsKey(ms)) {
            moveEnvironment(ms, ((Integer) this.environment.get(ms)).intValue() == 1 ? 2 : 1);
        } else {
            moveEnvironment(ms, 1);
        }
    }

    public void moveEnvironment(String ms, int type) {
        broadcastMessage(MaplePacketCreator.environmentMove(ms, type));
        this.environment.put(ms, Integer.valueOf(type));
    }

    public Map<String, Integer> getEnvironment() {
        return this.environment;
    }

    public int getNumPlayersInArea(int index) {
        return getNumPlayersInRect(getArea(index));
    }

    public int getNumPlayersInRect(Rectangle rect) {
        int ret = 0;
        this.charactersLock.readLock().lock();
        try {
            Iterator ltr = this.characters.iterator();

            while (ltr.hasNext()) {
                if (rect.contains(((MapleCharacter) ltr.next()).getTruePosition())) {
                    ret++;
                }
            }
        } finally {
            this.charactersLock.readLock().unlock();
        }
        return ret;
    }

    public int getNumPlayersItemsInArea(int index) {
        return getNumPlayersItemsInRect(getArea(index));
    }

    public final int getNumPlayersItemsInRect(final Rectangle rect) {
        int ret = getNumPlayersInRect(rect);

        mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().lock();
        try {
            for (MapleMapObject mmo : mapobjects.get(MapleMapObjectType.ITEM).values()) {
                if (rect.contains(mmo.getTruePosition())) {
                    ret++;
                }
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().unlock();
        }
        return ret;
    }

    public void broadcastGMMessage(MapleCharacter source, byte[] packet, boolean repeatToSource) {
        broadcastGMMessage(repeatToSource ? null : source, packet);
    }

    private void broadcastGMMessage(MapleCharacter source, byte[] packet) {
        this.charactersLock.readLock().lock();
        try {
            if (source == null) {
                for (MapleCharacter chr : this.characters) {
                    if (chr.isStaff()) {
                        chr.getClient().getSession().write(packet);
                    }
                }
            } else {
                for (MapleCharacter chr : this.characters) {
                    if ((chr != source) && (chr.getGMLevel() >= source.getGMLevel())) {
                        chr.getClient().getSession().write(packet);
                    }
                }
            }
        } finally {
            this.charactersLock.readLock().unlock();
        }
    }

    public List<Pair<Integer, Integer>> getMobsToSpawn() {
        return this.nodes.getMobsToSpawn();
    }

    public List<Integer> getSkillIds() {
        return this.nodes.getSkillIds();
    }

    public boolean canSpawn(long now) {
        return (this.lastSpawnTime > 0L) && (this.lastSpawnTime + this.createMobInterval < now);
    }

    public boolean canHurt(long now) {
        if ((this.lastHurtTime > 0L) && (this.lastHurtTime + this.decHPInterval < now)) {
            this.lastHurtTime = now;
            return true;
        }
        return false;
    }

    public void resetShammos(final MapleClient c) {
        killAllMonsters(true);
        broadcastMessage(MaplePacketCreator.serverNotice(5, "A player has moved too far from Shammos. Shammos is going back to the start."));
        EtcTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (c.getPlayer() != null) {
                    c.getPlayer().changeMap(MapleMap.this, MapleMap.this.getPortal(0));
                    if (MapleMap.this.getCharactersThreadsafe().size() > 1) {
                        MapScriptMethods.startScript_FirstUser(c, "shammos_Fenter");
                    }
                }
            }
        }, 500);
    }

    public int getInstanceId() {
        return this.instanceid;
    }

    public void setInstanceId(int ii) {
        this.instanceid = ii;
    }

    public int getPartyBonusRate() {
        return this.partyBonusRate;
    }

    public void setPartyBonusRate(int ii) {
        this.partyBonusRate = ii;
    }

    public short getTop() {
        return this.top;
    }

    public short getBottom() {
        return this.bottom;
    }

    public short getLeft() {
        return this.left;
    }

    public short getRight() {
        return this.right;
    }

    public void setTop(int ii) {
        this.top = (short) ii;
    }

    public void setBottom(int ii) {
        this.bottom = (short) ii;
    }

    public void setLeft(int ii) {
        this.left = (short) ii;
    }

    public void setRight(int ii) {
        this.right = (short) ii;
    }

    public List<Pair<Point, Integer>> getGuardians() {
        return this.nodes.getGuardians();
    }

    public MapleNodes.DirectionInfo getDirectionInfo(int i) {
        return this.nodes.getDirection(i);
    }

    public void AutoNx(int jsNx) {
        if (this.mapid != 910000000) {
            return;
        }
        for (MapleCharacter chr : this.characters) {
            int givNx = chr.getLevel() / 10 + jsNx;
            chr.modifyCSPoints(2, givNx);
            chr.dropMessage(5, new StringBuilder().append("[系统奖励] 在线时间奖励获得 [").append(givNx).append("] 点抵用券.").toString());
        }
    }

    public void AutoGain(int jsexp) {
        if (this.mapid != 910000000) {
            return;
        }
        for (MapleCharacter chr : this.characters) {
            if (chr.getLevel() >= 200) {
                return;
            }
            int givExp = jsexp * chr.getLevel() + chr.getClient().getChannelServer().getExpRate();
            givExp *= 3;
            chr.gainExp(givExp, true, false, true);
            chr.dropMessage(5, new StringBuilder().append("[系统奖励] 在线时间奖励获得 [").append(givExp).append("] 点经验.").toString());
        }
    }
    
    public void MaplewingGXs(int ds) {
        
        for (MapleCharacter chr : this.characters) {
            chr.addMaplewing("maple", ds);
            chr.upMaplewing();
            String names = chr.getVipname()+ " 您在 Maplewing 的 贡献点 增加了 " + ds + " . 赶快去贡献专区看看吧！";
            chr.dropMessage(-1, names);
            chr.dropMessage(5, names);
        }
        
    }
    
    public void MaplewingHYs(int ds) {
        
        for (MapleCharacter chr : this.characters) {
            chr.addMaplewing("mapley", ds);
            chr.upMaplewing();
            String names = chr.getVipname()+ " 您在 Maplewing 的 活跃点 增加了 " + ds + " . 赶快去领取在线福利吧！";
            chr.dropMessage(-1, names);
            chr.dropMessage(5, names);
        }
        
    }


    public boolean isMarketMap() {
        return (this.mapid >= 910000000) && (this.mapid <= 910000022);
    }

    public boolean isBossMap() {
        switch (this.mapid) {
            case 105100300:
            case 105100400:
            case 211070100:
            case 211070101:
            case 211070110:
            case 220080001:
            case 240040700:
            case 240060200:
            case 240060201:
            case 270050100:
            case 271040100:
            case 703200400:
            case 271040200:
            case 280030000:
            case 280030001:
            case 300030310:
            case 551030200:
            case 802000111:
            case 802000211:
            case 802000311:
            case 802000411:
            case 802000611:
            case 802000711:
            case 802000801:
            case 802000802:
            case 802000803:
            case 802000821:
            case 802000823:
                return true;
        }
        return false;
    }

    private static abstract interface DelayedPacketCreation {

        public abstract void sendPackets(MapleClient paramMapleClient);
    }

    private class ActivateItemReactor
            implements Runnable {

        private MapleMapItem mapitem;
        private MapleReactor reactor;
        private MapleClient c;

        public ActivateItemReactor(MapleMapItem mapitem, MapleReactor reactor, MapleClient c) {
            this.mapitem = mapitem;
            this.reactor = reactor;
            this.c = c;
        }

        @Override
        public void run() {
            if ((this.mapitem != null) && (this.mapitem == MapleMap.this.getMapObject(this.mapitem.getObjectId(), this.mapitem.getType())) && (!this.mapitem.isPickedUp())) {
                this.mapitem.expire(MapleMap.this);
                this.reactor.hitReactor(this.c);
                this.reactor.setTimerActive(false);

                if (this.reactor.getDelay() > 0) {
                    MapTimer.getInstance().schedule(new Runnable() {

                        @Override
                        public void run() {
                            reactor.forceHitReactor((byte) 0);
                        }
                    }, this.reactor.getDelay());
                }

            } else {
                this.reactor.setTimerActive(false);
            }
        }
    }
}