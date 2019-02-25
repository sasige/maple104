package server.life;

import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleDisease;
import client.MapleTrait;
import client.MapleTrait.MapleTraitType;
import client.PlayerStats;
import client.Skill;
import client.SkillFactory;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.status.MonsterStatus;
import client.status.MonsterStatusEffect;
import constants.GameConstants;
import handling.channel.ChannelServer;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import java.awt.Point;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import scripting.EventInstanceManager;
import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import server.Randomizer;
import server.Timer.EtcTimer;
import server.maps.Event_PyramidSubway;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import tools.ConcurrentEnumMap;
import tools.MaplePacketCreator;
import tools.Pair;
import tools.packet.MobPacket;

public class MapleMonster extends AbstractLoadedMapleLife {

    private MapleMonsterStats stats;
    private ChangeableStats ostats = null;
    private long hp;
    private long nextKill = 0L;
    private long lastDropTime = 0L;
    private int mp;
    private byte carnivalTeam = -1;
    private MapleMap map;
    private WeakReference<MapleMonster> sponge = new WeakReference(null);
    private int linkoid = 0;
    private int lastNode = -1;
    private int highestDamageChar = 0;
    private int linkCID = 0;
    private WeakReference<MapleCharacter> controller = new WeakReference(null);
    private boolean fake = false;
    private boolean dropsDisabled = false;
    private boolean controllerHasAggro = false;
    private final Collection<AttackerEntry> attackers = new LinkedList();
    private EventInstanceManager eventInstance;
    private MonsterListener listener = null;
    private byte[] reflectpack = null;
    private byte[] nodepack = null;
    private final ConcurrentEnumMap<MonsterStatus, MonsterStatusEffect> stati = new ConcurrentEnumMap(MonsterStatus.class);
    private final LinkedList<MonsterStatusEffect> poisons = new LinkedList();
    private final ReentrantReadWriteLock poisonsLock = new ReentrantReadWriteLock();
    private Map<Integer, Long> usedSkills;
    private int stolen = -1;
    private boolean shouldDropItem = false;
    private boolean killed = false;

    public MapleMonster(int id, MapleMonsterStats stats) {
        super(id);
        initWithStats(stats);
    }

    public MapleMonster(MapleMonster monster) {
        super(monster);
        initWithStats(monster.stats);
    }

    private void initWithStats(MapleMonsterStats stats) {
        setStance(5);
        this.stats = stats;
        this.hp = stats.getHp();
        this.mp = stats.getMp();

        if (stats.getNoSkills() > 0) {
            this.usedSkills = new HashMap();
        }
    }

    public ArrayList<AttackerEntry> getAttackers() {
        if ((this.attackers == null) || (this.attackers.size() <= 0)) {
            return new ArrayList();
        }
        ArrayList ret = new ArrayList();
        for (AttackerEntry e : this.attackers) {
            if (e != null) {
                ret.add(e);
            }
        }
        return ret;
    }

    public MapleMonsterStats getStats() {
        return this.stats;
    }

    public void disableDrops() {
        this.dropsDisabled = true;
    }

    public boolean dropsDisabled() {
        return this.dropsDisabled;
    }

    public void setSponge(MapleMonster mob) {
        this.sponge = new WeakReference(mob);
        if (this.linkoid <= 0) {
            this.linkoid = mob.getObjectId();
        }
    }

    public void setMap(MapleMap map) {
        this.map = map;
        startDropItemSchedule();
    }

    public long getHp() {
        return this.hp;
    }

    public void setHp(long hp) {
        this.hp = hp;
    }

    public ChangeableStats getChangedStats() {
        return this.ostats;
    }

    public long getMobMaxHp() {
        if (this.ostats != null) {
            return this.ostats.hp;
        }
        return this.stats.getHp();
    }

    public int getMp() {
        return this.mp;
    }

    public void setMp(int mp) {
        if (mp < 0) {
            mp = 0;
        }
        this.mp = mp;
    }

    public int getMobMaxMp() {
        if (this.ostats != null) {
            return this.ostats.mp;
        }
        return this.stats.getMp();
    }

    public int getMobExp() {
        if (this.ostats != null) {
            return this.ostats.exp;
        }
        return this.stats.getExp();
    }

    public void setOverrideStats(OverrideMonsterStats ostats) {
        this.ostats = new ChangeableStats(this.stats, ostats);
        this.hp = ostats.getHp();
        this.mp = ostats.getMp();
    }

    public void changeLevel(int newLevel) {
        changeLevel(newLevel, true);
    }

    public void changeLevel(int newLevel, boolean pqMob) {
        if (!this.stats.isChangeable()) {
            return;
        }
        this.ostats = new ChangeableStats(this.stats, newLevel, pqMob);
        this.hp = this.ostats.getHp();
        this.mp = this.ostats.getMp();
    }

    public MapleMonster getSponge() {
        return (MapleMonster) this.sponge.get();
    }

    public void damage(MapleCharacter from, long damage, boolean updateAttackTime) {
        damage(from, damage, updateAttackTime, 0);
    }

    public void damage(MapleCharacter from, long damage, boolean updateAttackTime, int lastSkill) {
        if ((from == null) || (damage <= 0L) || (!isAlive())) {
            return;
        }
        AttackerEntry attacker = null;
        if (from.getParty() != null) {
            attacker = new PartyAttackerEntry(from.getParty().getId());
        } else {
            attacker = new SingleAttackerEntry(from);
        }
        boolean replaced = false;
        for (AttackerEntry aentry : getAttackers()) {
            if ((aentry != null) && (aentry.equals(attacker))) {
                attacker = aentry;
                replaced = true;
                break;
            }
        }
        if (!replaced) {
            this.attackers.add(attacker);
        }
        long rDamage = Math.max(0L, Math.min(damage, this.hp));
        attacker.addDamage(from, rDamage, updateAttackTime);

        if (this.stats.getSelfD() != -1) {
            this.hp -= rDamage;
            if (this.hp > 0L) {
                if (this.hp < this.stats.getSelfDHp()) {
                    this.map.killMonster(this, from, false, false, this.stats.getSelfD(), lastSkill);
                } else {
                    for (AttackerEntry mattacker : getAttackers()) {
                        for (AttackingMapleCharacter cattacker : mattacker.getAttackers()) {
                            if ((cattacker.getAttacker().getMap() == from.getMap())
                                    && (cattacker.getLastAttackTime() >= System.currentTimeMillis() - 4000L)) {
                                cattacker.getAttacker().getClient().getSession().write(MobPacket.showMonsterHP(getObjectId(), getHPPercent()));
                            }
                        }
                    }
                }
            } else {
                this.map.killMonster(this, from, true, false, (byte) 1, lastSkill);
            }
        } else {
            if ((this.sponge.get() != null)
                    && (((MapleMonster) this.sponge.get()).hp > 0L)) {
                ((MapleMonster) this.sponge.get()).hp -= rDamage;
                if (((MapleMonster) this.sponge.get()).hp <= 0L) {
                    this.map.broadcastMessage(MobPacket.showBossHP(((MapleMonster) this.sponge.get()).getId(), -1L, ((MapleMonster) this.sponge.get()).getMobMaxHp()));
                    this.map.killMonster((MapleMonster) this.sponge.get(), from, true, false, (byte) 1, lastSkill);
                } else {
                    this.map.broadcastMessage(MobPacket.showBossHP((MapleMonster) this.sponge.get()));
                }
            }

            if (this.hp > 0L) {
                this.hp -= rDamage;
                if (this.eventInstance != null) {
                    this.eventInstance.monsterDamaged(from, this, (int) rDamage);
                } else {
                    EventInstanceManager em = from.getEventInstance();
                    if (em != null) {
                        em.monsterDamaged(from, this, (int) rDamage);
                    }
                }
                if ((this.sponge.get() == null) && (this.hp > 0L)) {
                    switch (this.stats.getHPDisplayType()) {
                        case 0:
                            this.map.broadcastMessage(MobPacket.showBossHP(this), getTruePosition());
                            break;
                        case 1:
                            this.map.broadcastMessage(from, MobPacket.damageFriendlyMob(this, damage, true), false);
                            break;
                        case 2:
                            this.map.broadcastMessage(MobPacket.showMonsterHP(getObjectId(), getHPPercent()));
                            from.mulung_EnergyModify(true);
                            break;
                        case 3:
                            for (AttackerEntry mattacker : getAttackers()) {
                                if (mattacker != null) {
                                    for (AttackingMapleCharacter cattacker : mattacker.getAttackers()) {
                                        if ((cattacker != null) && (cattacker.getAttacker().getMap() == from.getMap())
                                                && (cattacker.getLastAttackTime() >= System.currentTimeMillis() - 4000L)) {
                                            cattacker.getAttacker().getClient().getSession().write(MobPacket.showMonsterHP(getObjectId(), getHPPercent()));
                                        }
                                    }
                                }
                            }
                    }

                }

                if (this.hp <= 0L) {
                    if (this.stats.getHPDisplayType() == 0) {
                        this.map.broadcastMessage(MobPacket.showBossHP(getId(), -1L, getMobMaxHp()), getTruePosition());
                    }
                    this.map.killMonster(this, from, true, false, (byte) 1, lastSkill);
                }
            }
        }
        startDropItemSchedule();
    }

    public int getHPPercent() {
        return (int) Math.ceil(this.hp * 100.0D / getMobMaxHp());
    }

    public void heal(int hp, int mp, boolean broadcast) {
        long TotalHP = getHp() + hp;
        int TotalMP = getMp() + mp;
        if (TotalHP >= getMobMaxHp()) {
            setHp(getMobMaxHp());
        } else {
            setHp(TotalHP);
        }
        if (TotalMP >= getMp()) {
            setMp(getMp());
        } else {
            setMp(TotalMP);
        }
        if (broadcast) {
            this.map.broadcastMessage(MobPacket.healMonster(getObjectId(), hp));
        } else if (this.sponge.get() != null) {
            ((MapleMonster) this.sponge.get()).hp += hp;
        }
    }

    public void killed() {
        if (this.listener != null) {
            this.listener.monsterKilled();
        }
        this.listener = null;
    }

    private void giveExpToCharacter(MapleCharacter attacker, int exp, boolean highestDamage, int numExpSharers, byte pty, byte Class_Bonus_EXP_PERCENT, byte Premium_Bonus_EXP_PERCENT, int lastskillID) {
        if (highestDamage) {
            if (this.eventInstance != null) {
                this.eventInstance.monsterKilled(attacker, this);
            } else {
                EventInstanceManager em = attacker.getEventInstance();
                if (em != null) {
                    em.monsterKilled(attacker, this);
                }
            }
            this.highestDamageChar = attacker.getId();
        }
        if (exp > 0) {
            MonsterStatusEffect ms = (MonsterStatusEffect) this.stati.get(MonsterStatus.挑衅);
            if (ms != null) {
                exp += (int) (exp * (ms.getX().intValue() / 100.0D));
            }
            Integer holySymbol = attacker.getBuffedValue(MapleBuffStat.神圣祈祷);
            if (holySymbol != null) {
                exp = (int) (exp * (1.0D + holySymbol.doubleValue() / 100.0D));
            }
            if (attacker.hasDisease(MapleDisease.CURSE)) {
                exp /= 2;
            }
            exp = (int) Math.min(2147483647.0D, exp * attacker.getEXPMod() * attacker.getStat().expBuff / 100.0D * (attacker.getLevel() < 10 ? GameConstants.getExpRate_Below10(attacker.getJob()) : GameConstants.getExpRate(attacker.getJob(), ChannelServer.getInstance(this.map.getChannel()).getExpRate())));

            int Class_Bonus_EXP = 0;
            if (Class_Bonus_EXP_PERCENT > 0) {
                Class_Bonus_EXP = (int) (exp / 100.0D * Class_Bonus_EXP_PERCENT);
            }
            int Premium_Bonus_EXP = 0;
            if (Premium_Bonus_EXP_PERCENT > 0) {
                Premium_Bonus_EXP = (int) (exp / 100.0D * Premium_Bonus_EXP_PERCENT);
            }
            int Equipment_Bonus_EXP = (int) (exp / 100.0D * attacker.getStat().equipmentBonusExp);
            if ((attacker.getStat().equippedFairy > 0) && (attacker.getFairyExp() > 0)) {
                Equipment_Bonus_EXP += (int) (exp / 100.0D * attacker.getFairyExp());
            }
            attacker.getTrait(MapleTrait.MapleTraitType.charisma).addExp(this.stats.getCharismaEXP(), attacker);
            attacker.gainExpMonster(exp, true, highestDamage, pty, Class_Bonus_EXP, Equipment_Bonus_EXP, Premium_Bonus_EXP, this.stats.isPartyBonus(), this.stats.getPartyBonusRate());
        }
        attacker.mobKilled(getId(), lastskillID);
    }

    public int killBy(MapleCharacter killer, int lastSkill) {
        if (this.killed) {
            return 1;
        }
        this.killed = true;
        int totalBaseExp = getMobExp();
        AttackerEntry highest = null;
        long highdamage = 0L;
        List<AttackerEntry> list = getAttackers();
        for (AttackerEntry attackEntry : list) {
            if ((attackEntry != null) && (attackEntry.getDamage() > highdamage)) {
                highest = attackEntry;
                highdamage = attackEntry.getDamage();
            }
        }

        for (AttackerEntry attackEntry : list) {
            if (attackEntry != null) {
                int baseExp = (int) Math.ceil(totalBaseExp * (attackEntry.getDamage() / getMobMaxHp()));
                attackEntry.killedMob(getMap(), baseExp, attackEntry == highest, lastSkill);
            }
        }
        MapleCharacter controll = (MapleCharacter) this.controller.get();
        if (controll != null) {
            controll.getClient().getSession().write(MobPacket.stopControllingMonster(getObjectId()));
            controll.stopControllingMonster(this);
        }
        int achievement = 0;
        switch (getId()) {
            case 9400121:
                achievement = 12;
                break;
            case 8500002:
                achievement = 13;
                break;
            case 8510000:
            case 8520000:
                achievement = 14;
                break;
        }

        if (achievement != 0) {
            if ((killer != null) && (killer.getParty() != null)) {
                for (MaplePartyCharacter pChar : killer.getParty().getMembers()) {
                    MapleCharacter mpc = killer.getMap().getCharacterById(pChar.getId());
                    if (mpc != null) {
                        mpc.finishAchievement(achievement);
                    }
                }
            } else if (killer != null) {
                killer.finishAchievement(achievement);
            }
        }
        if ((killer != null) && (this.stats.isBoss())) {
            killer.finishAchievement(18);
        }
        spawnRevives(getMap());
        if (this.eventInstance != null) {
            this.eventInstance.unregisterMonster(this);
            this.eventInstance = null;
        }
        if ((killer != null) && (killer.getPyramidSubway() != null)) {
            killer.getPyramidSubway().onKill(killer);
        }
        this.hp = 0L;
        MapleMonster oldSponge = getSponge();
        this.sponge = new WeakReference(null);
        if ((oldSponge != null) && (oldSponge.isAlive())) {
            boolean set = true;
            for (MapleMapObject mon : this.map.getAllMonstersThreadsafe()) {
                MapleMonster mons = (MapleMonster) mon;
                if ((mons.isAlive()) && (mons.getObjectId() != oldSponge.getObjectId()) && (mons.getStats().getLevel() > 1) && (mons.getObjectId() != getObjectId()) && ((mons.getSponge() == oldSponge) || (mons.getLinkOid() == oldSponge.getObjectId()))) {
                    set = false;
                    break;
                }
            }
            if (set) {
                this.map.killMonster(oldSponge, killer, true, false, (byte) 1);
            }
        }

        this.reflectpack = null;
        this.nodepack = null;
        if (this.stati.size() > 0) {
            List<MonsterStatus> statuses = new LinkedList<MonsterStatus>(stati.keySet());
            for (MonsterStatus ms : statuses) {
                cancelStatus(ms);
            }
            statuses.clear();
        }
        if (this.poisons.size() > 0) {
            List<MonsterStatusEffect> ps = new LinkedList<MonsterStatusEffect>();
            this.poisonsLock.readLock().lock();
            try {
                ps.addAll(this.poisons);
            } finally {
                this.poisonsLock.readLock().unlock();
            }
            for (MonsterStatusEffect p : ps) {
                cancelSingleStatus(p);
            }
            ps.clear();
        }

        cancelDropItem();
        int v1 = this.highestDamageChar;
        this.highestDamageChar = 0;
        return v1;
    }

    public void spawnRevives(MapleMap map) {
        final List<Integer> toSpawn = this.stats.getRevives();

        if ((toSpawn == null) || (getLinkCID() > 0)) {
            return;
        }
        MapleMonster spongy = null;

        switch (getId()) {
            case 6160003:
            case 8820002:
            case 8820003:
            case 8820004:
            case 8820005:
            case 8820006:
            case 8840000:
            case 8850011:
                break;
            case 8810118:
            case 8810119:
            case 8810120:
            case 8810121:
                for (final int i : toSpawn) {
                    MapleMonster mob = MapleLifeFactory.getMonster(i);
                    mob.setPosition(getTruePosition());
                    if (this.eventInstance != null) {
                        this.eventInstance.registerMonster(mob);
                    }
                    if (dropsDisabled()) {
                        mob.disableDrops();
                    }
                    switch (mob.getId()) {
                        case 8810119:
                        case 8810120:
                        case 8810121:
                        case 8810122:
                            spongy = mob;
                    }
                }

                if ((spongy == null) || (map.getMonsterById(spongy.getId()) != null)) {
                    break;
                }
                map.spawnMonster(spongy, -2);
                for (MapleMapObject mon : map.getAllMonstersThreadsafe()) {
                    MapleMonster mons = (MapleMonster) mon;
                    if ((mons.getObjectId() != spongy.getObjectId()) && ((mons.getSponge() == this) || (mons.getLinkOid() == getObjectId()))) {
                        mons.setSponge(spongy);
                    }
                }
                break;
            case 8810026:
            case 8810130:
            case 8820008:
            case 8820009:
            case 8820010:
            case 8820011:
            case 8820012:
            case 8820013:
                List<MapleMonster> mobs = new ArrayList<MapleMonster>();
                for (final int i : toSpawn) {
                    MapleMonster mob = MapleLifeFactory.getMonster(i);
                    mob.setPosition(getTruePosition());
                    if (this.eventInstance != null) {
                        this.eventInstance.registerMonster(mob);
                    }
                    if (dropsDisabled()) {
                        mob.disableDrops();
                    }
                    switch (mob.getId()) {
                        case 8810018:
                        case 8810118:
                        case 8820009:
                        case 8820010:
                        case 8820011:
                        case 8820012:
                        case 8820013:
                        case 8820014:
                            spongy = mob;
                            break;
                        default:
                            mobs.add(mob);
                    }
                }

                if ((spongy == null) || (map.getMonsterById(spongy.getId()) != null)) {
                    break;
                }
                map.spawnMonster(spongy, -2);
                for (MapleMonster i : mobs) {
                    map.spawnMonster(i, -2);
                    i.setSponge(spongy);
                }
                break;
            case 8820014:
                for (final int i : toSpawn) {
                    MapleMonster mob = MapleLifeFactory.getMonster(i);
                    if (this.eventInstance != null) {
                        this.eventInstance.registerMonster(mob);
                    }
                    mob.setPosition(getTruePosition());
                    if (dropsDisabled()) {
                        mob.disableDrops();
                    }
                    map.spawnMonster(mob, -2);
                }
                break;
            default:
                for (final int i : toSpawn) {
                    MapleMonster mob = MapleLifeFactory.getMonster(i);
                    if (this.eventInstance != null) {
                        this.eventInstance.registerMonster(mob);
                    }
                    mob.setPosition(getTruePosition());
                    if (dropsDisabled()) {
                        mob.disableDrops();
                    }
                    map.spawnRevives(mob, getObjectId());
                    if (mob.getId() == 9300216) {
                        map.broadcastMessage(MaplePacketCreator.environmentChange("Dojang/clear", 4));
                        map.broadcastMessage(MaplePacketCreator.environmentChange("dojang/end/clear", 3));
                    }
                }
        }
    }

    public boolean isAlive() {
        return this.hp > 0L;
    }

    public void setCarnivalTeam(byte team) {
        this.carnivalTeam = team;
    }

    public byte getCarnivalTeam() {
        return this.carnivalTeam;
    }

    public MapleCharacter getController() {
        return (MapleCharacter) this.controller.get();
    }

    public void setController(MapleCharacter controller) {
        this.controller = new WeakReference(controller);
    }

    public void switchController(MapleCharacter newController, boolean immediateAggro) {
        MapleCharacter controllers = getController();
        if (controllers == newController) {
            return;
        }
        if (controllers != null) {
            controllers.stopControllingMonster(this);
            controllers.getClient().getSession().write(MobPacket.stopControllingMonster(getObjectId()));
            sendStatus(controllers.getClient());
        }
        newController.controlMonster(this, immediateAggro);
        setController(newController);
        if (immediateAggro) {
            setControllerHasAggro(true);
        }
    }

    public void addListener(MonsterListener listener) {
        this.listener = listener;
    }

    public boolean isControllerHasAggro() {
        return this.controllerHasAggro;
    }

    public void setControllerHasAggro(boolean controllerHasAggro) {
        this.controllerHasAggro = controllerHasAggro;
    }

    public void sendStatus(MapleClient client) {
        if (this.reflectpack != null) {
            client.getSession().write(this.reflectpack);
        }
        if (this.poisons.size() > 0) {
            this.poisonsLock.readLock().lock();
            try {
                client.getSession().write(MobPacket.applyMonsterStatus(this, this.poisons));
            } finally {
                this.poisonsLock.readLock().unlock();
            }
        }
    }

    public void sendSpawnData(MapleClient client) {
        if (!isAlive()) {
            return;
        }
        client.getSession().write(MobPacket.spawnMonster(this, (this.fake) && (this.linkCID <= 0) ? -4 : -1, 0));
        sendStatus(client);
        if ((this.map != null) && (!this.stats.isEscort()) && (client.getPlayer() != null) && (client.getPlayer().getTruePosition().distanceSq(getTruePosition()) <= GameConstants.maxViewRangeSq_Half())) {
            this.map.updateMonsterController(this);
        }
    }

    public void sendDestroyData(MapleClient client) {
        if ((this.stats.isEscort()) && (getEventInstance() != null) && (this.lastNode >= 0)) {
            this.map.resetShammos(client);
        } else {
            client.getSession().write(MobPacket.killMonster(getObjectId(), 0));
            if ((getController() != null) && (client.getPlayer() != null) && (client.getPlayer().getId() == getController().getId())) {
                client.getPlayer().stopControllingMonster(this);
            }
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(this.stats.getName());
        sb.append("(");
        sb.append(getId());
        sb.append(") 等级:");
        sb.append(this.stats.getLevel());
        sb.append(" 坐标(X:");
        sb.append(getTruePosition().x);
        sb.append("/Y:");
        sb.append(getTruePosition().y);
        sb.append(") 信息: ");
        sb.append(getHp());
        sb.append("/");
        sb.append(getMobMaxHp());
        sb.append("Hp, ");
        sb.append(getMp());
        sb.append("/");
        sb.append(getMobMaxMp());
        sb.append("Mp, oid: ");
        sb.append(getObjectId());
        sb.append("||仇恨目标: ");
        MapleCharacter chr = (MapleCharacter) this.controller.get();
        sb.append(chr != null ? chr.getName() : "无");
        return sb.toString();
    }

    public MapleMapObjectType getType() {
        return MapleMapObjectType.MONSTER;
    }

    public EventInstanceManager getEventInstance() {
        return this.eventInstance;
    }

    public void setEventInstance(EventInstanceManager eventInstance) {
        this.eventInstance = eventInstance;
    }

    public int getStatusSourceID(MonsterStatus status) {
        if ((status == MonsterStatus.中毒) || (status == MonsterStatus.烈焰喷射)) {
            this.poisonsLock.readLock().lock();
            try {
                for (MonsterStatusEffect ps : this.poisons) {
                    if (ps != null) {
                        int i = ps.getSkill();
                        return i;
                    }
                }
            } finally {
                poisonsLock.readLock().unlock();
            }
        }
        MonsterStatusEffect effect = (MonsterStatusEffect) this.stati.get(status);
        if (effect != null) {
            return effect.getSkill();
        }
        return -1;
    }

    public ElementalEffectiveness getEffectiveness(Element e) {
        if ((this.stati.size() > 0) && (this.stati.containsKey(MonsterStatus.巫毒))) {
            return ElementalEffectiveness.NORMAL;
        }
        return this.stats.getEffectiveness(e);
    }

    public void applyStatus(MapleCharacter from, MonsterStatusEffect status, boolean poison, long duration, boolean checkboss, MapleStatEffect eff) {
        if ((!isAlive()) || (getLinkCID() > 0)) {
            return;
        }
        Skill skilz = SkillFactory.getSkill(status.getSkill());
        if (skilz != null) {
            switch (stats.getEffectiveness(skilz.getElement())) {
                case IMMUNE:
                case STRONG:
                    return;
                case NORMAL:
                case WEAK:
                    break;
                default:
                    return;
            }
        }

        int statusSkill = status.getSkill();
        switch (statusSkill) {
            case 2111006:
                switch (stats.getEffectiveness(Element.POISON)) {
                    case IMMUNE:
                    case STRONG:
                        return;
                }
                break;
            case 2211006:
                switch (stats.getEffectiveness(Element.ICE)) {
                    case IMMUNE:
                    case STRONG:
                        return;
                }
                break;
            case 4110011:
            case 4210010:
            case 4320005:
            case 14110004:
                switch (stats.getEffectiveness(Element.POISON)) {
                    case IMMUNE:
                    case STRONG:
                        return;
                }

        }

        if (duration >= 2000000000L) {
            duration = 5000L;
        }
        MonsterStatus stat = status.getStati();
        if ((this.stats.isNoDoom()) && (stat == MonsterStatus.巫毒)) {
            return;
        }
        if (this.stats.isBoss()) {
            if ((stat == MonsterStatus.眩晕) || (stat == MonsterStatus.速度)) {
                return;
            }
            if ((checkboss) && (stat != MonsterStatus.忍者伏击) && (stat != MonsterStatus.物攻) && (stat != MonsterStatus.中毒) && (stat != MonsterStatus.烈焰喷射) && (stat != MonsterStatus.恐慌) && (stat != MonsterStatus.魔击无效)) {
                return;
            }

            if ((getId() == 8850011) && (stat == MonsterStatus.魔击无效)) {
                return;
            }
        }
        if (((this.stats.isFriendly()) || (isFake())) && ((stat == MonsterStatus.眩晕) || (stat == MonsterStatus.速度) || (stat == MonsterStatus.中毒) || (stat == MonsterStatus.烈焰喷射))) {
            return;
        }

        if (((stat == MonsterStatus.烈焰喷射) || (stat == MonsterStatus.中毒)) && (eff == null)) {
            return;
        }
        if (this.stati.containsKey(stat)) {
            cancelStatus(stat);
        }
        if ((stat == MonsterStatus.中毒) || (stat == MonsterStatus.烈焰喷射)) {
            this.poisonsLock.readLock().lock();
            try {
                for (MonsterStatusEffect mse : this.poisons) {
                    if ((mse != null) && ((mse.getSkill() == eff.getSourceId()) || (mse.getSkill() == GameConstants.getLinkedAranSkill(eff.getSourceId())) || (GameConstants.getLinkedAranSkill(mse.getSkill()) == eff.getSourceId()))) {
                        return;
                    }
                }
            } finally {
                this.poisonsLock.readLock().unlock();
            }
        }
        if ((poison) && (getHp() > 1L) && (eff != null)) {
            duration = Math.max(duration, eff.getDOTTime() * 1000);
        }
        duration += from.getStat().dotTime * 1000;
        long aniTime = duration;
        if (skilz != null) {
            aniTime += skilz.getAnimationTime();
        }
        status.setCancelTask(aniTime);
        if ((poison) && (getHp() > 1L)) {
            status.setValue(status.getStati(), Integer.valueOf((int) ((eff.getDOT() + from.getStat().dot + from.getStat().getDamageIncrease(eff.getSourceId())) * from.getStat().getCurrentMaxBaseDamage() / 100.0D)));
            int dam = Integer.valueOf((int) (aniTime / 1000L * status.getX().intValue() / 2L)).intValue();
            status.setPoisonSchedule(dam, from);
            if (dam > 0) {
                if (dam >= this.hp) {
                    dam = (int) (this.hp - 1L);
                }
                damage(from, dam, false);
            }
        } else if ((statusSkill == 4111003) || (statusSkill == 14111001)) {
            status.setValue(status.getStati(), Integer.valueOf((int) (getMobMaxHp() / 50.0D + 0.999D)));
            status.setPoisonSchedule(Integer.valueOf(status.getX().intValue()).intValue(), from);
        } else if (statusSkill == 4341003) {
            status.setPoisonSchedule(Integer.valueOf((int) (eff.getDamage() * from.getStat().getCurrentMaxBaseDamage() / 100.0D)).intValue(), from);
        }

        MapleCharacter con = getController();
        if ((stat == MonsterStatus.中毒) || (stat == MonsterStatus.烈焰喷射)) {
            this.poisonsLock.writeLock().lock();
            try {
                this.poisons.add(status);
                if (con != null) {
                    this.map.broadcastMessage(con, MobPacket.applyMonsterStatus(this, this.poisons), getTruePosition());
                    con.getClient().getSession().write(MobPacket.applyMonsterStatus(this, this.poisons));
                } else {
                    this.map.broadcastMessage(MobPacket.applyMonsterStatus(this, this.poisons), getTruePosition());
                }
            } finally {
                this.poisonsLock.writeLock().unlock();
            }
        } else {
            this.stati.put(stat, status);
            if (con != null) {
                this.map.broadcastMessage(con, MobPacket.applyMonsterStatus(this, status), getTruePosition());
                con.getClient().getSession().write(MobPacket.applyMonsterStatus(this, status));
            } else {
                this.map.broadcastMessage(MobPacket.applyMonsterStatus(this, status), getTruePosition());
            }
        }
    }

    public void applyStatus(MonsterStatusEffect status) {
        if (this.stati.containsKey(status.getStati())) {
            cancelStatus(status.getStati());
        }
        this.stati.put(status.getStati(), status);
        this.map.broadcastMessage(MobPacket.applyMonsterStatus(this, status), getTruePosition());
    }

    public void dispelSkill(MobSkill skillId) {
        List<MonsterStatus> toCancel = new ArrayList();
        for (Entry<MonsterStatus, MonsterStatusEffect> effects : this.stati.entrySet()) {
            MonsterStatusEffect mse = (MonsterStatusEffect) effects.getValue();
            if ((mse.getMobSkill() != null) && (mse.getMobSkill().getSkillId() == skillId.getSkillId())) {
                toCancel.add(effects.getKey());
            }
        }
        for (MonsterStatus stat : toCancel) {
            cancelStatus(stat);
        }
    }

    public void applyMonsterBuff(Map<MonsterStatus, Integer> effect, int skillId, long duration, MobSkill skill, List<Integer> reflection) {
        for (Entry<MonsterStatus, Integer> z : effect.entrySet()) {
            if (this.stati.containsKey(z.getKey())) {
                cancelStatus((MonsterStatus) z.getKey());
            }
            MonsterStatusEffect effectz = new MonsterStatusEffect((MonsterStatus) z.getKey(), (Integer) z.getValue(), 0, skill, true, reflection.size() > 0);
            effectz.setCancelTask(duration);
            this.stati.put(z.getKey(), effectz);
        }
        MapleCharacter con = getController();
        if (reflection.size() > 0) {
            this.reflectpack = MobPacket.applyMonsterStatus(getObjectId(), effect, reflection, skill);
            if (con != null) {
                this.map.broadcastMessage(con, this.reflectpack, getTruePosition());
                con.getClient().getSession().write(this.reflectpack);
            } else {
                this.map.broadcastMessage(this.reflectpack, getTruePosition());
            }
        } else {
            for (Map.Entry z : effect.entrySet()) {
                if (con != null) {
                    this.map.broadcastMessage(con, MobPacket.applyMonsterStatus(getObjectId(), (MonsterStatus) z.getKey(), ((Integer) z.getValue()).intValue(), skill), getTruePosition());
                    con.getClient().getSession().write(MobPacket.applyMonsterStatus(getObjectId(), (MonsterStatus) z.getKey(), ((Integer) z.getValue()).intValue(), skill));
                } else {
                    this.map.broadcastMessage(MobPacket.applyMonsterStatus(getObjectId(), (MonsterStatus) z.getKey(), ((Integer) z.getValue()).intValue(), skill), getTruePosition());
                }
            }
        }
    }

    public void setTempEffectiveness(final Element e, final long milli) {
        this.stats.setEffectiveness(e, ElementalEffectiveness.WEAK);
        EtcTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                stats.removeEffectiveness(e);
            }
        }, milli);
    }

    public boolean isBuffed(MonsterStatus status) {
        if ((status == MonsterStatus.中毒) || (status == MonsterStatus.烈焰喷射)) {
            return (this.poisons.size() > 0) || (this.stati.containsKey(status));
        }
        return this.stati.containsKey(status);
    }

    public MonsterStatusEffect getBuff(MonsterStatus status) {
        return (MonsterStatusEffect) this.stati.get(status);
    }

    public int getStatiSize() {
        return this.stati.size() + (this.poisons.size() > 0 ? 1 : 0);
    }

    public ArrayList<MonsterStatusEffect> getAllBuffs() {
        ArrayList ret = new ArrayList();
        for (MonsterStatusEffect e : this.stati.values()) {
            ret.add(e);
        }
        this.poisonsLock.readLock().lock();
        try {
            for (MonsterStatusEffect e : this.poisons) {
                ret.add(e);
            }
        } finally {
            this.poisonsLock.readLock().unlock();
        }
        return ret;
    }

    public void setFake(boolean fake) {
        this.fake = fake;
    }

    public boolean isFake() {
        return this.fake;
    }

    public MapleMap getMap() {
        return this.map;
    }

    public List<Pair<Integer, Integer>> getSkills() {
        return this.stats.getSkills();
    }

    public boolean hasSkill(int skillId, int level) {
        return this.stats.hasSkill(skillId, level);
    }

    public long getLastSkillUsed(int skillId) {
        if (this.usedSkills.containsKey(Integer.valueOf(skillId))) {
            return ((Long) this.usedSkills.get(Integer.valueOf(skillId))).longValue();
        }
        return 0L;
    }

    public void setLastSkillUsed(int skillId, long now, long cooltime) {
        switch (skillId) {
            case 140:
                this.usedSkills.put(Integer.valueOf(skillId), Long.valueOf(now + cooltime * 2L));
                this.usedSkills.put(Integer.valueOf(141), Long.valueOf(now));
                break;
            case 141:
                this.usedSkills.put(Integer.valueOf(skillId), Long.valueOf(now + cooltime * 2L));
                this.usedSkills.put(Integer.valueOf(140), Long.valueOf(now + cooltime));
                break;
            default:
                this.usedSkills.put(Integer.valueOf(skillId), Long.valueOf(now + cooltime));
        }
    }

    public byte getNoSkills() {
        return this.stats.getNoSkills();
    }

    public boolean isFirstAttack() {
        return this.stats.isFirstAttack();
    }

    public int getBuffToGive() {
        return this.stats.getBuffToGive();
    }

    public void doPoison(MonsterStatusEffect status, WeakReference<MapleCharacter> weakChr) {
        if (((status.getStati() == MonsterStatus.烈焰喷射) || (status.getStati() == MonsterStatus.中毒)) && (this.poisons.size() <= 0)) {
            return;
        }
        if ((status.getStati() != MonsterStatus.烈焰喷射) && (status.getStati() != MonsterStatus.中毒) && (!this.stati.containsKey(status.getStati()))) {
            return;
        }
        if (weakChr == null) {
            return;
        }
        long damage = status.getPoisonSchedule();
        boolean shadowWeb = (status.getSkill() == 4111003) || (status.getSkill() == 14111001);
        MapleCharacter chr = (MapleCharacter) weakChr.get();
        boolean cancel = (damage <= 0L) || (chr == null) || (chr.getMapId() != this.map.getId());
        if (damage >= this.hp) {
            damage = this.hp - 1L;
            cancel = (!shadowWeb) || (cancel);
        }
        if (!cancel) {
            damage(chr, damage, false);
            if (shadowWeb) {
                this.map.broadcastMessage(MobPacket.damageMonster(getObjectId(), damage), getTruePosition());
            }
        }
    }

    public int getLinkOid() {
        return this.linkoid;
    }

    public void setLinkOid(int lo) {
        this.linkoid = lo;
    }

    public ConcurrentEnumMap<MonsterStatus, MonsterStatusEffect> getStati() {
        return this.stati;
    }

    public void addEmpty() {
        for (MonsterStatus stat : MonsterStatus.values()) {
            if (stat.isEmpty()) {
                this.stati.put(stat, new MonsterStatusEffect(stat, Integer.valueOf(0), 0, null, false));
            }
        }
    }

    public int getStolen() {
        return this.stolen;
    }

    public void setStolen(int s) {
        this.stolen = s;
    }

    public void handleSteal(MapleCharacter chr) {
        double showdown = 100.0D;
        MonsterStatusEffect mse = getBuff(MonsterStatus.挑衅);
        if (mse != null) {
            showdown += mse.getX().intValue();
        }
        Skill steal = SkillFactory.getSkill(4201004);
        int level = chr.getTotalSkillLevel(steal);
        int chServerrate = ChannelServer.getInstance(chr.getClient().getChannel()).getDropRate();
        if ((level > 0) && (!getStats().isBoss()) && (this.stolen == -1) && (steal.getEffect(level).makeChanceResult())) {
            MapleMonsterInformationProvider mi = MapleMonsterInformationProvider.getInstance();
            List de = mi.retrieveDrop(getId());
            if (de == null) {
                this.stolen = 0;
                return;
            }
            List<MonsterDropEntry> dropEntry = new ArrayList<MonsterDropEntry>(de);
            Collections.shuffle(dropEntry);

            for (MonsterDropEntry d : dropEntry) {
                if ((d.itemId > 0) && (d.questid == 0) && (d.itemId / 10000 != 238) && (Randomizer.nextInt(999999) < (int) (10 * d.chance * chServerrate * chr.getDropMod() * (chr.getStat().dropBuff / 100.0D) * (showdown / 100.0D)))) {
                    Item idrop;
                    if (GameConstants.getInventoryType(d.itemId) == MapleInventoryType.EQUIP) {
                        Equip eq = (Equip) MapleItemInformationProvider.getInstance().getEquipById(d.itemId);
                        idrop = MapleItemInformationProvider.getInstance().randomizeStats(eq);
                    } else {
                        idrop = new Item(d.itemId, (byte) 0, (short) (d.Maximum != 1 ? Randomizer.nextInt(d.Maximum - d.Minimum) + d.Minimum : 1), (byte) 0);
                    }
                    this.stolen = d.itemId;
                    map.spawnMobDrop(idrop, this.map.calcDropPos(getPosition(), getTruePosition()), this, chr, (byte) 0, (short) 0);
                    break;
                }
            }
        } else {
            this.stolen = 0;
        }
    }

    public void setLastNode(int lastNode) {
        this.lastNode = lastNode;
    }

    public int getLastNode() {
        return this.lastNode;
    }

    public void cancelStatus(MonsterStatus stat) {
        if ((stat == MonsterStatus.空白BUFF) || (stat == MonsterStatus.召唤怪物)) {
            return;
        }
        MonsterStatusEffect mse = (MonsterStatusEffect) this.stati.get(stat);
        if ((mse == null) || (!isAlive())) {
            return;
        }
        if (mse.isReflect()) {
            this.reflectpack = null;
        }
        mse.cancelPoisonSchedule(this);
        MapleCharacter con = getController();
        if (con != null) {
            this.map.broadcastMessage(con, MobPacket.cancelMonsterStatus(getObjectId(), stat), getTruePosition());
            con.getClient().getSession().write(MobPacket.cancelMonsterStatus(getObjectId(), stat));
        } else {
            this.map.broadcastMessage(MobPacket.cancelMonsterStatus(getObjectId(), stat), getTruePosition());
        }
        this.stati.remove(stat);
    }

    public void cancelSingleStatus(MonsterStatusEffect stat) {
        if ((stat == null) || (stat.getStati() == MonsterStatus.空白BUFF) || (stat.getStati() == MonsterStatus.召唤怪物) || (!isAlive())) {
            return;
        }
        if ((stat.getStati() != MonsterStatus.中毒) && (stat.getStati() != MonsterStatus.烈焰喷射)) {
            cancelStatus(stat.getStati());
            return;
        }
        this.poisonsLock.writeLock().lock();
        try {
            if (!this.poisons.contains(stat)) {
                return;
            }
            this.poisons.remove(stat);
            if (stat.isReflect()) {
                this.reflectpack = null;
            }
            stat.cancelPoisonSchedule(this);
            MapleCharacter con = getController();
            if (con != null) {
                this.map.broadcastMessage(con, MobPacket.cancelPoison(getObjectId(), stat), getTruePosition());
                con.getClient().getSession().write(MobPacket.cancelPoison(getObjectId(), stat));
            } else {
                this.map.broadcastMessage(MobPacket.cancelPoison(getObjectId(), stat), getTruePosition());
            }
        } finally {
            this.poisonsLock.writeLock().unlock();
        }
    }

    public void cancelDropItem() {
        this.lastDropTime = 0L;
    }

    public void startDropItemSchedule() {
        cancelDropItem();
        if ((this.stats.getDropItemPeriod() <= 0) || (!isAlive())) {
            return;
        }
        this.shouldDropItem = false;
        this.lastDropTime = System.currentTimeMillis();
    }

    public boolean shouldDrop(long now) {
        return (this.lastDropTime > 0L) && (this.lastDropTime + this.stats.getDropItemPeriod() * 1000 < now);
    }

    public void doDropItem(long now) {
        int itemId;
        switch (getId()) {
            case 9300061:
                itemId = 4001101;
                break;
            default:
                cancelDropItem();
                return;
        }
        if ((isAlive()) && (this.map != null)) {
            if (this.shouldDropItem) {
                this.map.spawnAutoDrop(itemId, getTruePosition());
            } else {
                this.shouldDropItem = true;
            }
        }
        this.lastDropTime = now;
    }

    public byte[] getNodePacket() {
        return this.nodepack;
    }

    public void setNodePacket(byte[] np) {
        this.nodepack = np;
    }

    public void registerKill(long next) {
        this.nextKill = (System.currentTimeMillis() + next);
    }

    public boolean shouldKill(long now) {
        return (this.nextKill > 0L) && (now > this.nextKill);
    }

    public int getLinkCID() {
        return this.linkCID;
    }

    public void setLinkCID(int lc) {
        this.linkCID = lc;
        if (lc > 0) {
            this.stati.put(MonsterStatus.心灵控制, new MonsterStatusEffect(MonsterStatus.心灵控制, Integer.valueOf(60000), 30001062, null, false));
        }
    }

    private class PartyAttackerEntry
            implements MapleMonster.AttackerEntry {

        private long totDamage = 0L;
        private final Map<Integer, MapleMonster.OnePartyAttacker> attackers = new HashMap(6);
        private int partyid;

        public PartyAttackerEntry(int partyid) {
            this.partyid = partyid;
        }

        public List<MapleMonster.AttackingMapleCharacter> getAttackers() {
            List ret = new ArrayList(this.attackers.size());
            for (Map.Entry entry : this.attackers.entrySet()) {
                MapleCharacter chr = MapleMonster.this.map.getCharacterById(((Integer) entry.getKey()).intValue());
                if (chr != null) {
                    ret.add(new MapleMonster.AttackingMapleCharacter(chr, ((MapleMonster.OnePartyAttacker) entry.getValue()).lastAttackTime));
                }
            }
            return ret;
        }

        private Map<MapleCharacter, MapleMonster.OnePartyAttacker> resolveAttackers() {
            Map ret = new HashMap(this.attackers.size());
            for (Map.Entry aentry : this.attackers.entrySet()) {
                MapleCharacter chr = MapleMonster.this.map.getCharacterById(((Integer) aentry.getKey()).intValue());
                if (chr != null) {
                    ret.put(chr, aentry.getValue());
                }
            }
            return ret;
        }

        public boolean contains(MapleCharacter chr) {
            return this.attackers.containsKey(Integer.valueOf(chr.getId()));
        }

        public long getDamage() {
            return this.totDamage;
        }

        public void addDamage(MapleCharacter from, long damage, boolean updateAttackTime) {
            MapleMonster.OnePartyAttacker oldPartyAttacker = (MapleMonster.OnePartyAttacker) this.attackers.get(Integer.valueOf(from.getId()));
            if (oldPartyAttacker != null) {
                oldPartyAttacker.damage += damage;
                oldPartyAttacker.lastKnownParty = from.getParty();
                if (updateAttackTime) {
                    oldPartyAttacker.lastAttackTime = System.currentTimeMillis();
                }

            } else {
                MapleMonster.OnePartyAttacker onePartyAttacker = new MapleMonster.OnePartyAttacker(from.getParty(), damage);
                this.attackers.put(Integer.valueOf(from.getId()), onePartyAttacker);
                if (!updateAttackTime) {
                    onePartyAttacker.lastAttackTime = 0L;
                }
            }
            this.totDamage += damage;
        }

        @Override
        public void killedMob(MapleMap map, int baseExp, boolean mostDamage, int lastSkill) {
            MapleCharacter highest = null;
            long highestDamage = 0L;
            int iexp = 0;
            double addedPartyLevel;
            byte added_partyinc, 职业奖励经验, 召回戒指经验;
            List<MapleCharacter> expApplicable;
            double innerBaseExp;
            ExpMap expmap;
            Map<MapleCharacter, ExpMap> expMap = new HashMap<MapleCharacter, ExpMap>(6);

            for (final Entry<MapleCharacter, OnePartyAttacker> attacker : resolveAttackers().entrySet()) {
                MapleParty party = ((MapleMonster.OnePartyAttacker) attacker.getValue()).lastKnownParty;
                addedPartyLevel = 0.0D;
                added_partyinc = 0;
                职业奖励经验 = 0;
                召回戒指经验 = 0;
                expApplicable = new ArrayList();
                for (MaplePartyCharacter partychar : party.getMembers()) {
                    if ((((MapleCharacter) attacker.getKey()).getLevel() - partychar.getLevel() <= 5) || (MapleMonster.this.stats.getLevel() - partychar.getLevel() <= 5)) {
                        MapleCharacter pchr = map.getCharacterById(partychar.getId());
                        if ((pchr != null) && (pchr.isAlive())) {
                            expApplicable.add(pchr);
                            addedPartyLevel += pchr.getLevel();

                            职业奖励经验 = (byte) (职业奖励经验 + pchr.get精灵祝福());
                            if ((pchr.getStat().equippedWelcomeBackRing) && (召回戒指经验 == 0)) {
                                召回戒指经验 = 80;
                            }
                            if ((pchr.getStat().hasPartyBonus) && (added_partyinc < 4) && (map.getPartyBonusRate() <= 0)) {
                                added_partyinc = (byte) (added_partyinc + 1);
                            }
                        }
                    }
                }
                long iDamage = ((MapleMonster.OnePartyAttacker) attacker.getValue()).damage;
                if (iDamage > highestDamage) {
                    highest = (MapleCharacter) attacker.getKey();
                    highestDamage = iDamage;
                }
                innerBaseExp = baseExp * (iDamage / this.totDamage);
                if (expApplicable.size() <= 1) {
                    职业奖励经验 = 0;
                }

                for (MapleCharacter expReceiver : expApplicable) {
                    iexp = expMap.get(expReceiver) == null ? 0 : ((MapleMonster.ExpMap) expMap.get(expReceiver)).exp;
                    double levelMod = expReceiver.getLevel() / addedPartyLevel * 0.4D;
                    iexp += (int) Math.round(((((MapleCharacter) attacker.getKey()).getId() == expReceiver.getId() ? 0.6D : 0.0D) + levelMod) * innerBaseExp);
                    expMap.put(expReceiver, new MapleMonster.ExpMap(iexp, (byte) (expApplicable.size() + added_partyinc), 职业奖励经验, 召回戒指经验));
                }
            }

            for (Entry<MapleCharacter, ExpMap> expReceiver : expMap.entrySet()) {
                expmap = expReceiver.getValue();
                MapleMonster.this.giveExpToCharacter((MapleCharacter) expReceiver.getKey(), expmap.exp, expReceiver.getKey() == highest, expMap.size(), expmap.ptysize, expmap.职业奖励经验, expmap.召回戒指经验, lastSkill);
            }
        }

        @Override
        public int hashCode() {
            int prime = 31;
            int result = 1;
            result = prime * result + this.partyid;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            PartyAttackerEntry other = (PartyAttackerEntry) obj;

            return this.partyid == other.partyid;
        }
    }

    private static final class OnePartyAttacker {

        public MapleParty lastKnownParty;
        public long damage;
        public long lastAttackTime;

        public OnePartyAttacker(MapleParty lastKnownParty, long damage) {
            this.lastKnownParty = lastKnownParty;
            this.damage = damage;
            this.lastAttackTime = System.currentTimeMillis();
        }
    }

    private static final class ExpMap {

        public final int exp;
        public final byte ptysize;
        public final byte 职业奖励经验;
        public final byte 召回戒指经验;

        public ExpMap(int exp, byte ptysize, byte 职业奖励经验, byte 召回戒指经验) {
            this.exp = exp;
            this.ptysize = ptysize;
            this.职业奖励经验 = 职业奖励经验;
            this.召回戒指经验 = 召回戒指经验;
        }
    }

    private final class SingleAttackerEntry
            implements MapleMonster.AttackerEntry {

        private long damage = 0L;
        private int chrid;
        private long lastAttackTime;

        public SingleAttackerEntry(MapleCharacter from) {
            this.chrid = from.getId();
        }

        @Override
        public void addDamage(MapleCharacter from, long damage, boolean updateAttackTime) {
            if (this.chrid == from.getId()) {
                this.damage += damage;
                if (updateAttackTime) {
                    this.lastAttackTime = System.currentTimeMillis();
                }
            }
        }

        @Override
        public List<MapleMonster.AttackingMapleCharacter> getAttackers() {
            MapleCharacter chr = MapleMonster.this.map.getCharacterById(this.chrid);
            if (chr != null) {
                return Collections.singletonList(new MapleMonster.AttackingMapleCharacter(chr, this.lastAttackTime));
            }
            return Collections.emptyList();
        }

        @Override
        public boolean contains(MapleCharacter chr) {
            return this.chrid == chr.getId();
        }

        public long getDamage() {
            return this.damage;
        }

        public void killedMob(MapleMap map, int baseExp, boolean mostDamage, int lastSkill) {
            MapleCharacter chr = map.getCharacterById(this.chrid);
            if ((chr != null) && (chr.isAlive())) {
                giveExpToCharacter(chr, baseExp, mostDamage, 1, (byte) 0, (byte) 0, (byte) 0, lastSkill);
            }
        }

        public int hashCode() {
            return this.chrid;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            SingleAttackerEntry other = (SingleAttackerEntry) obj;
            return this.chrid == other.chrid;
        }
    }

    private static abstract interface AttackerEntry {

        public abstract List<MapleMonster.AttackingMapleCharacter> getAttackers();

        public abstract void addDamage(MapleCharacter paramMapleCharacter, long paramLong, boolean paramBoolean);

        public abstract long getDamage();

        public abstract boolean contains(MapleCharacter paramMapleCharacter);

        public abstract void killedMob(MapleMap paramMapleMap, int paramInt1, boolean paramBoolean, int paramInt2);
    }

    private static class AttackingMapleCharacter {

        private MapleCharacter attacker;
        private long lastAttackTime;

        public AttackingMapleCharacter(MapleCharacter attacker, long lastAttackTime) {
            this.attacker = attacker;
            this.lastAttackTime = lastAttackTime;
        }

        public long getLastAttackTime() {
            return this.lastAttackTime;
        }

        public void setLastAttackTime(long lastAttackTime) {
            this.lastAttackTime = lastAttackTime;
        }

        public MapleCharacter getAttacker() {
            return this.attacker;
        }
    }
}