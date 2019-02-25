package client;

import client.MapleTrait.MapleTraitType;
import client.anticheat.CheatTracker;
import client.anticheat.ReportType;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.ItemFlag;
import client.inventory.ItemLoader;
import client.inventory.MapleAndroid;
import client.inventory.MapleImp;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryIdentifier;
import client.inventory.MapleInventoryType;
import client.inventory.MapleMount;
import client.inventory.MaplePet;
import client.inventory.MapleRing;
import client.status.MonsterStatus;
import client.status.MonsterStatusEffect;
import constants.BattleConstants.PokemonNature;
import constants.BattleConstants.PokemonStat;
import constants.GameConstants;
import constants.ServerConstants.PlayerGMRank;
import database.DatabaseConnection;
import database.DatabaseException;
import handling.channel.ChannelServer;
import handling.login.LoginInformationProvider.JobType;
import handling.login.LoginServer;
import handling.world.CharacterTransfer;
import handling.world.MapleMessenger;
import handling.world.MapleMessengerCharacter;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import handling.world.PartyOperation;
import handling.world.PlayerBuffStorage;
import handling.world.PlayerBuffValueHolder;
import handling.world.World;
import handling.world.World.Broadcast;
import handling.world.family.MapleFamily;
import handling.world.family.MapleFamilyBuff;
import handling.world.family.MapleFamilyCharacter;
import handling.world.guild.MapleGuild;
import handling.world.guild.MapleGuildCharacter;
import handling.world.sidekick.MapleSidekick;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;
import scripting.EventInstanceManager;
import scripting.NPCScriptManager;
import server.CashShop;
import server.MapleCarnivalChallenge;
import server.MapleCarnivalParty;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MaplePortal;
import server.MapleShop;
import server.MapleStatEffect;
import server.MapleStatEffect.CancelEffectAction;
import server.MapleStorage;
import server.MapleTrade;
import server.PokemonBattle;
import server.RandomRewards;
import server.Randomizer;
import server.ServerProperties;
import server.Timer.BuffTimer;
import server.Timer.MapTimer;
import server.Timer.WorldTimer;
import server.achievement.MapleAchievements;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MapleMonsterStats;
import server.life.MobSkill;
import server.life.MobSkillFactory;
import server.life.PlayerNPC;
import server.maps.AnimatedMapleMapObject;
import server.maps.Event_PyramidSubway;
import server.maps.FieldLimitType;
import server.maps.MapleDoor;
import server.maps.MapleDragon;
import server.maps.MapleExtractor;
import server.maps.MapleFoothold;
import server.maps.MapleMap;
import server.maps.MapleMapFactory;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.MapleSummon;
import server.maps.MechDoor;
import server.maps.SavedLocationType;
import server.movement.LifeMovementFragment;
import server.quest.MapleQuest;
import server.shops.IMaplePlayerShop;
import tools.ConcurrentEnumMap;
import tools.FileoutputUtil;
import tools.MaplePacketCreator;
import tools.Pair;
import tools.StringUtil;
import tools.Triple;
import tools.data.MaplePacketLittleEndianWriter;
import tools.packet.AndroidPacket;
import tools.packet.BuddyListPacket;
import tools.packet.MTSCSPacket;
import tools.packet.MobPacket;
import tools.packet.MonsterCarnivalPacket;
import tools.packet.PartyPacket;
import tools.packet.PetPacket;
import tools.packet.PlayerShopPacket;
import tools.packet.UIPacket;

public class MapleCharacter extends AnimatedMapleMapObject implements Serializable {

    private static final long serialVersionUID = 845748950829L;
    private String name;
    private String chalktext;
    private String BlessOfFairy_Origin;
    private String BlessOfEmpress_Origin;
    private String teleportname;
    private long lastCombo;
    private long lastfametime;
    private long keydown_skill;
    private long nextConsume;
    private long pqStartTime;
    private long lastDragonBloodTime;
    private long lastBerserkTime;
    private long lastRecoveryTime;
    private long lastSummonTime;
    private long mapChangeTime;
    private long lastFishingTime;
    private long lastFairyTime;
    private long lastHPTime;
    private long lastMPTime;
    private long lastFamiliarEffectTime;
    private long lastDOTTime;
    private byte gmLevel;
    private byte gender;
    private byte initialSpawnPoint;
    private byte skinColor;
    private byte guildrank = 5;
    private byte allianceRank = 5;
    private byte world;
    private byte fairyExp;
    private byte numClones;
    private byte subcategory;
    private short level;
    private short mulung_energy;
    private short combo;
    private short availableCP;
    private short fatigue;
    private short totalCP;
    private short hpApUsed;
    private short job;
    private short remainingAp;
    private short scrolledPosition;
    private int accountid;
    private int id;
    private int meso;
    private int exp;
    private int hair;
    private int face;
    private int mapid;
    private int fame;
    private int pvpExp;
    private int pvpPoints;
    private int totalWins;
    private int totalLosses;
    private int guildid = 0;
    private int fallcounter;
    private int maplepoints;
    private int acash;
    private int chair;
    private int itemEffect;
    private int points;
    private int vpoints;
    private int rank = 1;
    private int rankMove = 0;
    private int jobRank = 1;
    private int jobRankMove = 0;
    private int marriageId;
    private int marriageItemId;
    private int dotHP;
    private int currentrep;
    private int totalrep;
    private int coconutteam;
    private int followid;
    private int battleshipHP;
    private int gachexp;
    private int challenge;
    private int guildContribution = 0;
    private Point old;
    private MonsterFamiliar summonedFamiliar;
    private int[] wishlist;
    private int[] rocks;
    private int[] savedLocations;
    private int[] regrocks;
    private int[] hyperrocks;
    private int[] remainingSp = new int[10];
    private int[] remainingLevel = new int[10];
    private transient AtomicInteger inst;
    private transient AtomicInteger insd;
    private transient List<LifeMovementFragment> lastres;
    private List<Integer> lastmonthfameids;
    private List<Integer> lastmonthbattleids;
    private List<Integer> extendedSlots;
    private List<Integer> excluded;
    private Map<Integer, Integer> phantomskill;
    private List<MapleDoor> doors;
    private List<MechDoor> mechDoors;
    private List<MaplePet> pets;
    private List<Item> rebuy;
    private MapleImp[] imps;
    private transient WeakReference<MapleCharacter>[] clones;
    private transient Set<MapleMonster> controlled;
    private transient Set<MapleMapObject> visibleMapObjects;
    private transient ReentrantReadWriteLock visibleMapObjectsLock;
    private transient ReentrantReadWriteLock summonsLock;
    private transient ReentrantReadWriteLock controlledLock;
    private transient MapleAndroid android;
    private Map<MapleQuest, MapleQuestStatus> quests;
    private Map<Integer, String> questinfo;
    private Map<Skill, SkillEntry> skills;
    private transient Map<MapleBuffStat, MapleBuffStatValueHolder> effects;
    private transient List<MapleSummon> summons;
    private transient Map<Integer, MapleCoolDownValueHolder> coolDowns;
    private transient Map<MapleDisease, MapleDiseaseValueHolder> diseases;
    private Map<ReportType, Integer> reports;
    private CashShop cs;
    private transient Deque<MapleCarnivalChallenge> pendingCarnivalRequests;
    private transient MapleCarnivalParty carnivalParty;
    private BuddyList buddylist;
    private MonsterBook monsterbook;
    private transient CheatTracker anticheat;
    private transient MapleLieDetector antiMacro;
    private MapleClient client;
    private transient MapleParty party;
    private PlayerStats stats;
    private transient MapleMap map;
    private transient MapleShop shop;
    private transient MapleDragon dragon;
    private transient MapleExtractor extractor;
    private transient RockPaperScissors rps;
    private MapleSidekick sidekick;
    private Map<Integer, MonsterFamiliar> familiars;
    private MapleStorage storage;
    private transient MapleTrade trade;
    private MapleMount mount;
    private List<Integer> finishedAchievements;
    private MapleMessenger messenger;
    private byte[] petStore;
    private transient IMaplePlayerShop playerShop;
    private boolean invincible;
    private boolean canTalk;
    private boolean clone;
    private boolean followinitiator;
    private boolean followon;
    private boolean smega;
    private boolean hasSummon;
    private MapleGuildCharacter mgc;
    private MapleFamilyCharacter mfc;
    private transient EventInstanceManager eventInstance;
    private MapleInventory[] inventory;
    private SkillMacro[] skillMacros = new SkillMacro[5];
    private EnumMap<MapleTrait.MapleTraitType, MapleTrait> traits;
    private Battler[] battlers = new Battler[6];
    private List<Battler> boxed;
    private MapleKeyLayout keylayout;
    private transient ScheduledFuture<?> mapTimeLimitTask;
    private transient Event_PyramidSubway pyramidSubway = null;
    private transient List<Integer> pendingExpiration = null;
    private transient List<Integer> pendingSkills = null;
    private transient Map<Integer, Integer> linkMobs;
    private transient PokemonBattle battle;
    private boolean changed_wishlist;
    private boolean changed_trocklocations;
    private boolean changed_skillmacros;
    private boolean changed_achievements;
    private boolean changed_savedlocations;
    private boolean changed_pokemon;
    private boolean changed_questinfo;
    private boolean changed_skills;
    private boolean changed_reports;
    private boolean changed_extendedSlots;
    private static final Logger log = Logger.getLogger(MapleCharacter.class);
    private int forcecounter = 0;
    private int decorate;
    private int elfEar;
    private int vip, vipczz;
    private Timestamp viptime;
    private int titleEffect;
    private boolean isbanned = false;
    private int beans;
    private int warning;
    private int dollars;
    private int shareLots;
    private int reborns;
    private int apstorage;
    private int carte = 0;
    private Map<Integer, attactset> attackd;
    // private int maxhp;
    // private int maxmp;

    public static enum FameStatus {

        OK, NOT_TODAY, NOT_THIS_MONTH;
    }

    private MapleCharacter(boolean ChannelServer) {
        setStance(0);
        setPosition(new Point(0, 0));
        this.inventory = new MapleInventory[MapleInventoryType.values().length];
        for (MapleInventoryType type : MapleInventoryType.values()) {
            this.inventory[type.ordinal()] = new MapleInventory(type);
        }
        this.quests = new LinkedHashMap<>();
        this.skills = new LinkedHashMap<>();
        this.stats = new PlayerStats();
        for (int i = 0; i < this.remainingSp.length; i++) {
            this.remainingSp[i] = 0;
        }
        this.traits = new EnumMap(MapleTraitType.class);
        for (MapleTraitType t : MapleTraitType.values()) {
            this.traits.put(t, new MapleTrait(t));
        }
        if (ChannelServer) {
            this.changed_reports = false;
            this.changed_skills = false;
            this.changed_achievements = false;
            this.changed_wishlist = false;
            this.changed_trocklocations = false;
            this.changed_skillmacros = false;
            this.changed_savedlocations = false;
            this.changed_pokemon = false;
            this.changed_extendedSlots = false;
            this.changed_questinfo = false;
            this.scrolledPosition = 0;
            this.lastCombo = 0L;
            this.mulung_energy = 0;
            this.combo = 0;
            this.forcecounter = 0;
            this.carte = 0;
            this.keydown_skill = 0L;
            this.nextConsume = 0L;
            this.pqStartTime = 0L;
            this.fairyExp = 0;
            this.mapChangeTime = 0L;
            this.lastRecoveryTime = 0L;
            this.lastDragonBloodTime = 0L;
            this.lastBerserkTime = 0L;
            this.lastFishingTime = 0L;
            this.lastFairyTime = 0L;
            this.lastHPTime = 0L;
            this.lastMPTime = 0L;
            this.lastFamiliarEffectTime = 0L;
            this.old = new Point(0, 0);
            this.coconutteam = 0;
            this.followid = 0;
            this.battleshipHP = 0;
            this.marriageItemId = 0;
            this.fallcounter = 0;
            this.challenge = 0;
            this.dotHP = 0;
            this.lastSummonTime = 0L;
            this.hasSummon = false;
            this.invincible = false;
            this.canTalk = true;
            this.clone = false;
            this.followinitiator = false;
            this.followon = false;
            this.rebuy = new ArrayList<>();
            this.linkMobs = new HashMap<>();
            this.finishedAchievements = new ArrayList<>();
            this.reports = new EnumMap<>(ReportType.class);
            this.teleportname = "";
            this.smega = true;
            this.petStore = new byte[3];
            for (int i = 0; i < this.petStore.length; i++) {
                this.petStore[i] = -1;
            }
            this.wishlist = new int[10];
            this.rocks = new int[10];
            this.regrocks = new int[5];
            this.hyperrocks = new int[13];
            this.imps = new MapleImp[3];
            this.clones = new WeakReference[5];
            for (int i = 0; i < this.clones.length; i++) {
                this.clones[i] = new WeakReference(null);
            }
            this.boxed = new ArrayList<Battler>();
            this.familiars = new LinkedHashMap<Integer, MonsterFamiliar>();
            this.extendedSlots = new ArrayList<Integer>();
            this.excluded = new ArrayList<Integer>();
            this.effects = new ConcurrentEnumMap<MapleBuffStat, MapleBuffStatValueHolder>(MapleBuffStat.class);
            this.coolDowns = new LinkedHashMap<Integer, MapleCoolDownValueHolder>();
            this.diseases = new ConcurrentEnumMap<MapleDisease, MapleDiseaseValueHolder>(MapleDisease.class);
            this.inst = new AtomicInteger(0);
            this.insd = new AtomicInteger(-1);
            this.keylayout = new MapleKeyLayout();
            this.doors = new ArrayList();
            this.mechDoors = new ArrayList();
            this.controlled = new LinkedHashSet();
            this.controlledLock = new ReentrantReadWriteLock();
            this.summons = new LinkedList();
            this.summonsLock = new ReentrantReadWriteLock();
            this.visibleMapObjects = new LinkedHashSet();
            this.visibleMapObjectsLock = new ReentrantReadWriteLock();
            this.pendingCarnivalRequests = new LinkedList();

            this.savedLocations = new int[SavedLocationType.values().length];
            for (int i = 0; i < SavedLocationType.values().length; i++) {
                this.savedLocations[i] = -1;
            }
            this.questinfo = new LinkedHashMap();
            this.pets = new ArrayList();
            this.phantomskill = new LinkedHashMap<>();
            this.attackd = new LinkedHashMap<>();
        }
    }

    public static MapleCharacter getDefault(MapleClient client, JobType type) {
        MapleCharacter ret = new MapleCharacter(false);
        ret.client = client;
        ret.map = null;
        ret.exp = 0;
        ret.gmLevel = 0;
        ret.job = (type.type == 8 ? 0 : (short) type.id);
        ret.meso = 0;
        ret.level = 1;
        ret.remainingAp = 0;
        ret.fame = 0;
        ret.accountid = client.getAccID();
        ret.buddylist = new BuddyList((byte) 20);

        ret.stats.str = 12;
        ret.stats.dex = 5;
        ret.stats.int_ = 4;
        ret.stats.luk = 4;
        ret.stats.maxhp = 50;
        ret.stats.hp = 50;
        ret.stats.maxmp = 50;
        ret.stats.mp = 50;
        ret.gachexp = 0;
        try {
            Connection con = DatabaseConnection.getConnection();

            PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts WHERE id = ?");
            ps.setInt(1, ret.accountid);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                ret.client.setAccountName(rs.getString("name"));
                ret.acash = rs.getInt("ACash");
                ret.maplepoints = rs.getInt("mPoints");
                ret.points = rs.getInt("points");
                ret.vpoints = rs.getInt("vpoints");
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println(new StringBuilder().append("Error getting character default").append(e).toString());
        }
        return ret;
    }

    public static MapleCharacter ReconstructChr(final CharacterTransfer ct, MapleClient client, boolean isChannel) {
        MapleCharacter ret = new MapleCharacter(true);
        ret.client = client;
        if (!isChannel) {
            ret.client.setChannel(ct.channel);
        }
        ret.id = ct.characterid;
        ret.name = ct.name;
        ret.level = ct.level;
        ret.fame = ct.fame;

        ret.stats.str = ct.str;
        ret.stats.dex = ct.dex;
        ret.stats.int_ = ct.int_;
        ret.stats.luk = ct.luk;
        ret.stats.maxhp = ct.maxhp;
        ret.stats.maxmp = ct.maxmp;
        ret.stats.hp = ct.hp;
        ret.stats.mp = ct.mp;

        ret.chalktext = ct.chalkboard;
        ret.gmLevel = ct.gmLevel;
        //等级经验限制>= 200
        ret.exp = (((ret.level >= 252) || ((GameConstants.is骑士团(ret.job)) && (ret.level >= 120) && (!ret.isIntern()))) ? 0 : ct.exp);
        ret.hpApUsed = ct.hpApUsed;
        ret.remainingSp = ct.remainingSp;
        ret.remainingAp = ct.remainingAp;
        ret.meso = ct.meso;
        ret.skinColor = ct.skinColor;
        ret.gender = ct.gender;
        ret.job = ct.job;
        ret.hair = ct.hair;
        ret.face = ct.face;
        ret.accountid = ct.accountid;
        ret.totalWins = ct.totalWins;
        ret.totalLosses = ct.totalLosses;
        client.setAccID(ct.accountid);
        ret.mapid = ct.mapid;
        ret.initialSpawnPoint = ct.initialSpawnPoint;
        ret.world = ct.world;
        ret.guildid = ct.guildid;
        ret.guildrank = ct.guildrank;
        ret.guildContribution = ct.guildContribution;
        ret.allianceRank = ct.alliancerank;
        ret.points = ct.points;
        ret.vpoints = ct.vpoints;
        ret.fairyExp = ct.fairyExp;
        ret.marriageId = ct.marriageId;
        ret.currentrep = ct.currentrep;
        ret.totalrep = ct.totalrep;
        ret.gachexp = ct.gachexp;
        ret.pvpExp = ct.pvpExp;
        ret.pvpPoints = ct.pvpPoints;
        ret.phantomskill = ct.phantomskill;
        ret.decorate = ct.decorate;
        ret.elfEar = ct.elfEar;
        ret.beans = ct.beans;
        ret.warning = ct.warning;
        ret.dollars = ct.dollars;
        ret.shareLots = ct.shareLots;
        ret.vip = ct.vip;
        ret.vipczz = ct.vipczz;
        ret.viptime = ct.viptime;
        ret.makeMFC(ct.familyid, ct.seniorid, ct.junior1, ct.junior2);
        if (ret.guildid > 0) {
            ret.mgc = new MapleGuildCharacter(ret);
        }
        ret.fatigue = ct.fatigue;
        ret.buddylist = new BuddyList(ct.buddysize);
        ret.subcategory = ct.subcategory;

        if (ct.sidekick > 0) {
            ret.sidekick = World.Sidekick.getSidekick(ct.sidekick);
        }

        if (isChannel) {
            MapleMapFactory mapFactory = ChannelServer.getInstance(client.getChannel()).getMapFactory();
            ret.map = mapFactory.getMap(ret.mapid);
            if (ret.map == null) {
                ret.map = mapFactory.getMap(100000000);
            } else if ((ret.map.getForcedReturnId() != 999999999) && (ret.map.getForcedReturnMap() != null)) {
                ret.map = ret.map.getForcedReturnMap();
            }

            MaplePortal portal = ret.map.getPortal(ret.initialSpawnPoint);
            if (portal == null) {
                portal = ret.map.getPortal(0);
                ret.initialSpawnPoint = 0;
            }
            ret.setPosition(portal.getPosition());

            int messengerid = ct.messengerid;
            if (messengerid > 0) {
                ret.messenger = World.Messenger.getMessenger(messengerid);
            }
        } else {
            ret.messenger = null;
        }
        int partyid = ct.partyid;
        if (partyid >= 0) {
            MapleParty party = World.Party.getParty(partyid);
            if ((party != null) && (party.getMemberById(ret.id) != null)) {
                ret.party = party;
            }

        }

        for (final Entry<Integer, Object> qs : ct.Quest.entrySet()) {
            MapleQuestStatus queststatus_from = (MapleQuestStatus) qs.getValue();
            queststatus_from.setQuest(((Integer) qs.getKey()).intValue());
            ret.quests.put(queststatus_from.getQuest(), queststatus_from);
        }
        for (final Entry<Integer, SkillEntry> qs : ct.Skills.entrySet()) {
            ret.skills.put(SkillFactory.getSkill(qs.getKey()), qs.getValue());
        }
        for (Integer zz : ct.finishedAchievements) {
            ret.finishedAchievements.add(zz);
        }
        for (Object zz : ct.boxed) {
            Battler zzz = (Battler) zz;
            zzz.setStats();
            ret.boxed.add(zzz);
        }
        for (Entry<MapleTraitType, Integer> t : ct.traits.entrySet()) {
            ret.traits.get(t.getKey()).setExp(t.getValue());
        }
        for (Entry<Byte, Integer> qs : ct.reports.entrySet()) {
            ret.reports.put(ReportType.getById(qs.getKey().byteValue()), qs.getValue());
        }
        ret.monsterbook = new MonsterBook(ct.mbook, ret);
        ret.inventory = (MapleInventory[]) (MapleInventory[]) ct.inventorys;
        ret.BlessOfFairy_Origin = ct.BlessOfFairy;
        ret.BlessOfEmpress_Origin = ct.BlessOfEmpress;
        ret.skillMacros = (SkillMacro[]) ct.skillmacro;
        ret.battlers = (Battler[]) ct.battlers;
        for (Battler b : ret.battlers) {
            if (b != null) {
                b.setStats();
            }
        }
        ret.petStore = ct.petStore;
        ret.keylayout = new MapleKeyLayout(ct.keymap);
        ret.questinfo = ct.InfoQuest;
        ret.familiars = ct.familiars;
        ret.savedLocations = ct.savedlocation;
        ret.wishlist = ct.wishlist;
        ret.rocks = ct.rocks;
        ret.regrocks = ct.regrocks;
        ret.hyperrocks = ct.hyperrocks;
        ret.buddylist.loadFromTransfer(ct.buddies);

        ret.keydown_skill = 0L;
        ret.lastfametime = ct.lastfametime;
        ret.lastmonthfameids = ct.famedcharacters;
        ret.lastmonthbattleids = ct.battledaccs;
        ret.extendedSlots = ct.extendedSlots;
        ret.excluded = ct.excluded;
        ret.storage = ((MapleStorage) ct.storage);
        ret.cs = ((CashShop) ct.cs);
        client.setAccountName(ct.accountname);
        ret.acash = ct.ACash;
        ret.maplepoints = ct.MaplePoints;
        ret.numClones = ct.clonez;
        ret.imps = ct.imps;
        ret.anticheat = ((CheatTracker) ct.anticheat);
        ret.anticheat.start(ret);
        ret.antiMacro = ((MapleLieDetector) ct.antiMacro);
        ret.rebuy = ct.rebuy;
        ret.mount = new MapleMount(ret, ct.mount_itemid, PlayerStats.getSkillByJob(1004, ret.job), ct.mount_Fatigue, ct.mount_level, ct.mount_exp);
        ret.expirationTask(false, false);
        ret.stats.recalcLocalStats(true, ret);
        client.setTempIP(ct.tempIP);

        return ret;
    }

    public static MapleCharacter loadCharFromDB(int charid, MapleClient client, boolean channelserver) {
        MapleCharacter ret = new MapleCharacter(channelserver);
        ret.client = client;
        ret.id = charid;

        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = null;
        PreparedStatement pse = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement("SELECT * FROM characters WHERE id = ?");
            ps.setInt(1, charid);
            rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                throw new RuntimeException("加载角色失败原因(角色没有找到).");
            }
            ret.name = rs.getString("name");
            ret.level = rs.getShort("level");
            ret.fame = rs.getInt("fame");

            ret.stats.str = rs.getShort("str");
            ret.stats.dex = rs.getShort("dex");
            ret.stats.int_ = rs.getShort("int");
            ret.stats.luk = rs.getShort("luk");
            ret.stats.maxhp = rs.getInt("maxhp");
            ret.stats.maxmp = rs.getInt("maxmp");
            ret.stats.hp = rs.getInt("hp");
            ret.stats.mp = rs.getInt("mp");
            ret.job = rs.getShort("job");
            ret.gmLevel = rs.getByte("gm");
            //等级经验限制>= 200
            ret.exp = (((ret.level >= 252) || ((GameConstants.is骑士团(ret.job)) && (ret.level >= 120) && (!ret.isIntern()))) ? 0 : rs.getInt("exp"));
            ret.hpApUsed = rs.getShort("hpApUsed");
            String[] sp = rs.getString("sp").split(",");
            for (int i = 0; i < ret.remainingSp.length; i++) {
                ret.remainingSp[i] = Integer.parseInt(sp[i]);
            }
            ret.remainingAp = rs.getShort("ap");
            ret.meso = rs.getInt("meso");
            ret.skinColor = rs.getByte("skincolor");
            ret.gender = rs.getByte("gender");

            ret.hair = rs.getInt("hair");
            ret.face = rs.getInt("face");
            ret.accountid = rs.getInt("accountid");
            client.setAccID(ret.accountid);
            ret.mapid = rs.getInt("map");
            ret.initialSpawnPoint = rs.getByte("spawnpoint");
            ret.world = rs.getByte("world");
            ret.guildid = rs.getInt("guildid");
            ret.guildrank = rs.getByte("guildrank");
            ret.allianceRank = rs.getByte("allianceRank");
            ret.guildContribution = rs.getInt("guildContribution");
            ret.totalWins = rs.getInt("totalWins");
            ret.totalLosses = rs.getInt("totalLosses");
            ret.currentrep = rs.getInt("currentrep");
            ret.totalrep = rs.getInt("totalrep");
            ret.makeMFC(rs.getInt("familyid"), rs.getInt("seniorid"), rs.getInt("junior1"), rs.getInt("junior2"));
            if (ret.guildid > 0) {
                ret.mgc = new MapleGuildCharacter(ret);
            }
            ret.gachexp = rs.getInt("gachexp");
            ret.buddylist = new BuddyList(rs.getByte("buddyCapacity"));
            ret.subcategory = rs.getByte("subcategory");
            ret.mount = new MapleMount(ret, 0, PlayerStats.getSkillByJob(1004, ret.job), (byte) 0, (byte) 1, 0);

            ret.rank = rs.getInt("rank");
            ret.rankMove = rs.getInt("rankMove");
            ret.jobRank = rs.getInt("jobRank");
            ret.jobRankMove = rs.getInt("jobRankMove");

            ret.marriageId = rs.getInt("marriageId");

            ret.fatigue = rs.getShort("fatigue");

            ret.pvpExp = rs.getInt("pvpExp");
            ret.pvpPoints = rs.getInt("pvpPoints");

            for (MapleTrait t : ret.traits.values()) {
                t.setExp(rs.getInt(t.getType().name()));
            }

            ret.decorate = rs.getInt("decorate");

            ret.elfEar = rs.getInt("elfEar");

            ret.beans = rs.getInt("beans");

            ret.warning = rs.getInt("warning");

            ret.dollars = rs.getInt("dollars");
            ret.shareLots = rs.getInt("sharelots");

            ret.reborns = rs.getInt("reborns");
            ret.apstorage = rs.getInt("apstorage");

            ret.vip = rs.getInt("vip");
            ret.vipczz = rs.getInt("vipczz");
            Timestamp expiration = rs.getTimestamp("viptime");
            ret.viptime = (expiration == null ? null : expiration);
            if (channelserver) {
                ret.anticheat = new CheatTracker(ret);

                ret.antiMacro = new MapleLieDetector(ret);

                MapleMapFactory mapFactory = ChannelServer.getInstance(client.getChannel()).getMapFactory();
                ret.map = mapFactory.getMap(ret.mapid);
                if (ret.map == null) {
                    ret.map = mapFactory.getMap(100000000);
                }

                MaplePortal portal = ret.map.getPortal(ret.initialSpawnPoint);
                if (portal == null) {
                    portal = ret.map.getPortal(0);
                    ret.initialSpawnPoint = 0;
                }
                ret.setPosition(portal.getPosition());

                int partyid = rs.getInt("party");
                if (partyid >= 0) {
                    MapleParty party = World.Party.getParty(partyid);
                    if ((party != null) && (party.getMemberById(ret.id) != null)) {
                        ret.party = party;
                    }
                }

                String[] pets = rs.getString("pets").split(",");
                for (int i = 0; i < ret.petStore.length; i++) {
                    ret.petStore[i] = Byte.parseByte(pets[i]);
                }
                rs.close();
                ps.close();

                ps = con.prepareStatement("SELECT * FROM achievements WHERE accountid = ?");
                ps.setInt(1, ret.accountid);
                rs = ps.executeQuery();
                while (rs.next()) {
                    ret.finishedAchievements.add(Integer.valueOf(rs.getInt("achievementid")));
                }
                ps.close();
                rs.close();
                ps = con.prepareStatement("SELECT * FROM reports WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                while (rs.next()) {
                    if (ReportType.getById(rs.getByte("type")) != null) {
                        ret.reports.put(ReportType.getById(rs.getByte("type")), Integer.valueOf(rs.getInt("count")));
                    }
                }
            }
            rs.close();
            ps.close();
            ps = con.prepareStatement("SELECT * FROM queststatus WHERE characterid = ?");
            ps.setInt(1, charid);
            rs = ps.executeQuery();
            pse = con.prepareStatement("SELECT * FROM queststatusmobs WHERE queststatusid = ?");
            while (rs.next()) {
                int id = rs.getInt("quest");
                MapleQuest q = MapleQuest.getInstance(id);
                byte stat = rs.getByte("status");
                if (((stat == 1) || (stat == 2)) && (((channelserver) && ((q == null) || (q.isBlocked()))) || ((stat == 1) && (channelserver) && (!q.canStart(ret, null))))) {
                    continue;
                }
                MapleQuestStatus status = new MapleQuestStatus(q, stat);
                long cTime = rs.getLong("time");
                if (cTime > -1L) {
                    status.setCompletionTime(cTime * 1000L);
                }
                status.setForfeited(rs.getInt("forfeited"));
                status.setCustomData(rs.getString("customData"));
                ret.quests.put(q, status);
                pse.setInt(1, rs.getInt("queststatusid"));
                ResultSet rsMobs = pse.executeQuery();
                while (rsMobs.next()) {
                    status.setMobKills(rsMobs.getInt("mob"), rsMobs.getInt("count"));
                }
                rsMobs.close();
            }
            rs.close();
            ps.close();
            pse.close();
            if (channelserver) {
                ret.monsterbook = MonsterBook.loadCards(ret.accountid, ret);

                ps = con.prepareStatement("SELECT * FROM inventoryslot where characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                if (!rs.next()) {
                    rs.close();
                    ps.close();
                    throw new RuntimeException("No Inventory slot column found in SQL. [inventoryslot]");
                }
                ret.getInventory(MapleInventoryType.EQUIP).setSlotLimit(rs.getByte("equip"));
                ret.getInventory(MapleInventoryType.USE).setSlotLimit(rs.getByte("use"));
                ret.getInventory(MapleInventoryType.SETUP).setSlotLimit(rs.getByte("setup"));
                ret.getInventory(MapleInventoryType.ETC).setSlotLimit(rs.getByte("etc"));
                ret.getInventory(MapleInventoryType.CASH).setSlotLimit(rs.getByte("cash"));

                ps.close();
                rs.close();
                for (Pair mit : ItemLoader.装备道具.loadItems(false, charid).values()) {
                    ret.getInventory((MapleInventoryType) mit.getRight()).addFromDB((Item) mit.getLeft());
                    if (((Item) mit.getLeft()).getPet() != null) {
                        ret.pets.add(((Item) mit.getLeft()).getPet());
                    }
                }

                ps = con.prepareStatement("SELECT * FROM accounts WHERE id = ?");
                ps.setInt(1, ret.accountid);
                rs = ps.executeQuery();
                if (rs.next()) {
                    ret.getClient().setAccountName(rs.getString("name"));
                    ret.acash = rs.getInt("ACash");
                    ret.maplepoints = rs.getInt("mPoints");
                    ret.points = rs.getInt("points");
                    ret.vpoints = rs.getInt("vpoints");

                    if (rs.getTimestamp("lastlogon") != null) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(rs.getTimestamp("lastlogon").getTime());
                        cal.get(Calendar.DAY_OF_WEEK);
                        Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                    }
                    if (rs.getInt("banned") > 0) {
                        rs.close();
                        ps.close();
                        ret.getClient().getSession().close();
                        throw new RuntimeException("Loading a banned character");
                    }
                    rs.close();
                    ps.close();
                    ps = con.prepareStatement("UPDATE accounts SET lastlogon = CURRENT_TIMESTAMP() WHERE id = ?");
                    ps.setInt(1, ret.accountid);
                    ps.executeUpdate();
                } else {
                    rs.close();
                }
                ps.close();
                ps = con.prepareStatement("SELECT * FROM questinfo WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                while (rs.next()) {
                    ret.questinfo.put(Integer.valueOf(rs.getInt("quest")), rs.getString("customData"));
                }
                rs.close();
                ps.close();
                ps = con.prepareStatement("SELECT skillid, skilllevel, masterlevel, expiration, teachId FROM skills WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                Skill skil;

                while (rs.next()) {
                    int skid = rs.getInt("skillid");
                    //Skill skil = SkillFactory.getSkill(skid);
                    skil = SkillFactory.getSkill(skid);
                    int skl = rs.getInt("skilllevel");
                    byte msl = rs.getByte("masterlevel");
                    int teachId = rs.getInt("teachId");
                    if (skil != null && GameConstants.isApplicableSkill(skid)) {
                        if (skl > skil.getMaxLevel() && skid < 92000000) {
                            if (!skil.isBeginnerSkill() && skil.canBeLearnedBy(ret.job) && !skil.isSpecialSkill()) {
                                ret.remainingSp[GameConstants.getSkillBookForSkill(skid)] += skl - skil.getMaxLevel();
                            }
                            skl = (byte) skil.getMaxLevel();
                        }
                        if (msl > skil.getMaxLevel()) {
                            msl = (byte) skil.getMaxLevel();
                        }
                        ret.skills.put(skil, new SkillEntry(skl, msl, rs.getLong("expiration"), teachId));
                    } else if ((skil == null)
                            && (!GameConstants.is新手职业(skid / 10000)) && (skid / 10000 != 900) && (skid / 10000 != 800) && (skid / 10000 != 9000)) {
                        ret.remainingSp[GameConstants.getSkillBookForSkill(skid)] += skl;
                    }
                }

                rs.close();
                ps.close();

                ps = con.prepareStatement("SELECT * FROM phantomskills WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                if (rs.next()) {
                    int skid = rs.getInt("skill1");
                    if (skid != 0) {
                        skil = SkillFactory.getSkill(skid);
                        ret.skills.put(skil, new SkillEntry(skil.getMaxLevel(), (byte) 0, -1L, -1));
                    }
                    skid = rs.getInt("skill2");
                    if (skid != 0) {
                        skil = SkillFactory.getSkill(skid);
                        ret.skills.put(skil, new SkillEntry(skil.getMaxLevel(), (byte) 0, -1L, -1));
                    }
                    skid = rs.getInt("skill3");
                    if (skid != 0) {
                        skil = SkillFactory.getSkill(skid);
                        ret.skills.put(skil, new SkillEntry(skil.getMaxLevel(), (byte) 0, -1L, -1));
                    }
                    skid = rs.getInt("skill4");
                    if (skid != 0) {
                        skil = SkillFactory.getSkill(skid);
                        ret.skills.put(skil, new SkillEntry(skil.getMaxLevel(), (byte) 0, -1L, -1));
                    }
                    skid = rs.getInt("skill1_0");
                    if (skid != 0) {
                        skil = SkillFactory.getSkill(skid);
                        ret.skills.put(skil, new SkillEntry(skil.getMaxLevel(), (byte) 0, -1L, -1));
                    }
                    skid = rs.getInt("skill1_1");
                    if (skid != 0) {
                        skil = SkillFactory.getSkill(skid);
                        ret.skills.put(skil, new SkillEntry(skil.getMaxLevel(), (byte) 0, -1L, -1));
                    }
                    skid = rs.getInt("skill1_2");
                    if (skid != 0) {
                        skil = SkillFactory.getSkill(skid);
                        ret.skills.put(skil, new SkillEntry(skil.getMaxLevel(), (byte) 0, -1L, -1));
                    }
                    skid = rs.getInt("skill1_3");
                    if (skid != 0) {
                        skil = SkillFactory.getSkill(skid);
                        ret.skills.put(skil, new SkillEntry(skil.getMaxLevel(), (byte) 0, -1L, -1));
                    }

                    skid = rs.getInt("skill2_0");
                    if (skid != 0) {
                        skil = SkillFactory.getSkill(skid);
                        ret.skills.put(skil, new SkillEntry(skil.getMaxLevel(), (byte) 0, -1L, -1));
                    }
                    skid = rs.getInt("skill2_1");
                    if (skid != 0) {
                        skil = SkillFactory.getSkill(skid);
                        ret.skills.put(skil, new SkillEntry(skil.getMaxLevel(), (byte) 0, -1L, -1));
                    }
                    skid = rs.getInt("skill2_2");
                    if (skid != 0) {
                        skil = SkillFactory.getSkill(skid);
                        ret.skills.put(skil, new SkillEntry(skil.getMaxLevel(), (byte) 0, -1L, -1));
                    }
                    skid = rs.getInt("skill2_3");
                    if (skid != 0) {
                        skil = SkillFactory.getSkill(skid);
                        ret.skills.put(skil, new SkillEntry(skil.getMaxLevel(), (byte) 0, -1L, -1));
                    }

                    skid = rs.getInt("skill3_0");
                    if (skid != 0) {
                        skil = SkillFactory.getSkill(skid);
                        ret.skills.put(skil, new SkillEntry(skil.getMaxLevel(), (byte) 0, -1L, -1));
                    }
                    skid = rs.getInt("skill3_1");
                    if (skid != 0) {
                        skil = SkillFactory.getSkill(skid);
                        ret.skills.put(skil, new SkillEntry(skil.getMaxLevel(), (byte) 0, -1L, -1));
                    }
                    skid = rs.getInt("skill3_2");
                    if (skid != 0) {
                        skil = SkillFactory.getSkill(skid);
                        ret.skills.put(skil, new SkillEntry(skil.getMaxLevel(), (byte) 0, -1L, -1));
                    }

                    skid = rs.getInt("skill4_0");
                    if (skid != 0) {
                        skil = SkillFactory.getSkill(skid);
                        ret.skills.put(skil, new SkillEntry(skil.getMaxLevel(), (byte) 0, -1L, -1));
                    }
                    skid = rs.getInt("skill4_1");
                    if (skid != 0) {
                        skil = SkillFactory.getSkill(skid);
                        ret.skills.put(skil, new SkillEntry(skil.getMaxLevel(), (byte) 0, -1L, -1));
                    }
                    ret.phantomskill.put(1, rs.getInt("skill1"));
                    ret.phantomskill.put(2, rs.getInt("skill2"));
                    ret.phantomskill.put(3, rs.getInt("skill3"));
                    ret.phantomskill.put(4, rs.getInt("skill4"));

                    ret.phantomskill.put(10, rs.getInt("skill1_0"));
                    ret.phantomskill.put(11, rs.getInt("skill1_1"));
                    ret.phantomskill.put(12, rs.getInt("skill1_2"));
                    ret.phantomskill.put(13, rs.getInt("skill1_3"));

                    ret.phantomskill.put(20, rs.getInt("skill2_0"));
                    ret.phantomskill.put(21, rs.getInt("skill2_1"));
                    ret.phantomskill.put(22, rs.getInt("skill2_2"));
                    ret.phantomskill.put(23, rs.getInt("skill2_3"));

                    ret.phantomskill.put(30, rs.getInt("skill3_0"));
                    ret.phantomskill.put(31, rs.getInt("skill3_1"));
                    ret.phantomskill.put(32, rs.getInt("skill3_2"));

                    ret.phantomskill.put(40, rs.getInt("skill4_0"));
                    ret.phantomskill.put(41, rs.getInt("skill4_1"));
                } else {
                    ret.phantomskill.put(1, 0);
                    ret.phantomskill.put(2, 0);
                    ret.phantomskill.put(3, 0);
                    ret.phantomskill.put(4, 0);

                    ret.phantomskill.put(10, 0);
                    ret.phantomskill.put(11, 0);
                    ret.phantomskill.put(12, 0);
                    ret.phantomskill.put(13, 0);

                    ret.phantomskill.put(20, 0);
                    ret.phantomskill.put(21, 0);
                    ret.phantomskill.put(22, 0);
                    ret.phantomskill.put(23, 0);

                    ret.phantomskill.put(30, 0);
                    ret.phantomskill.put(31, 0);
                    ret.phantomskill.put(32, 0);

                    ret.phantomskill.put(40, 0);
                    ret.phantomskill.put(41, 0);
                }
                rs.close();
                ps.close();

                ret.expirationTask(false, true);
                ps = con.prepareStatement("SELECT * FROM characters WHERE accountid = ? ORDER BY level DESC");
                ps.setInt(1, ret.accountid);
                rs = ps.executeQuery();
                int maxlevel_ = 0;
                int maxlevel_2 = 0;
                while (rs.next()) {
                    if (rs.getInt("id") != charid) {
                        if (GameConstants.is骑士团(rs.getShort("job"))) {
                            int maxlevel = rs.getShort("level") / 5;
                            if (maxlevel > 24) {
                                maxlevel = 24;
                            }
                            if ((maxlevel > maxlevel_2) || (maxlevel_2 == 0)) {
                                maxlevel_2 = maxlevel;
                                ret.BlessOfEmpress_Origin = rs.getString("name");
                            }
                        }
                        int maxlevel = rs.getShort("level") / 10;
                        if (maxlevel > 20) {
                            maxlevel = 20;
                        }
                        if ((maxlevel > maxlevel_) || (maxlevel_ == 0)) {
                            maxlevel_ = maxlevel;
                            ret.BlessOfFairy_Origin = rs.getString("name");
                        }

                    }

                }

                if (ret.BlessOfFairy_Origin == null) {
                    ret.BlessOfFairy_Origin = ret.name;
                }
                ret.skills.put(SkillFactory.getSkill(GameConstants.getBOF_ForJob(ret.job)), new SkillEntry(maxlevel_, (byte) 0, -1L, 0));
                if (SkillFactory.getSkill(GameConstants.getEmpress_ForJob(ret.job)) != null) {
                    if (ret.BlessOfEmpress_Origin == null) {
                        ret.BlessOfEmpress_Origin = ret.BlessOfFairy_Origin;
                    }
                    ret.skills.put(SkillFactory.getSkill(GameConstants.getEmpress_ForJob(ret.job)), new SkillEntry(maxlevel_2, (byte) 0, -1L, 0));
                }
                ps.close();
                rs.close();
                ps = con.prepareStatement("SELECT * FROM skillmacros WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();

                while (rs.next()) {
                    int position = rs.getInt("position");
                    SkillMacro macro = new SkillMacro(rs.getInt("skill1"), rs.getInt("skill2"), rs.getInt("skill3"), rs.getString("name"), rs.getInt("shout"), position);
                    ret.skillMacros[position] = macro;
                }
                rs.close();
                ps.close();
                ps = con.prepareStatement("SELECT * FROM familiars WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                while (rs.next()) {
                    if (rs.getLong("expiry") <= System.currentTimeMillis()) {
                        continue;
                    }
                    ret.familiars.put(Integer.valueOf(rs.getInt("familiar")), new MonsterFamiliar(charid, rs.getInt("id"), rs.getInt("familiar"), rs.getLong("expiry"), rs.getString("name"), rs.getInt("fatigue"), rs.getByte("vitality")));
                }
                rs.close();
                ps.close();

                ps = con.prepareStatement("SELECT * FROM pokemon WHERE characterid = ? OR (accountid = ? AND active = 0)");
                ps.setInt(1, charid);
                ps.setInt(2, ret.accountid);
                rs = ps.executeQuery();
                int position = 0;
                while (rs.next()) {
                    Battler b = new Battler(rs.getInt("level"), rs.getInt("exp"), charid, rs.getInt("monsterid"), rs.getString("name"), PokemonNature.values()[rs.getInt("nature")], rs.getInt("itemid"), rs.getByte("gender"), rs.getByte("hpiv"), rs.getByte("atkiv"), rs.getByte("defiv"), rs.getByte("spatkiv"), rs.getByte("spdefiv"), rs.getByte("speediv"), rs.getByte("evaiv"), rs.getByte("acciv"), rs.getByte("ability"));
                    if (b.getFamily() == null) {
                        continue;
                    }
                    if ((rs.getInt("active") > 0) && (position < 6) && (rs.getInt("characterid") == charid)) {
                        ret.battlers[position] = b;
                        position++;
                    } else {
                        ret.boxed.add(b);
                    }
                }
                rs.close();
                ps.close();
                ps = con.prepareStatement("SELECT `key`,`type`,`action` FROM keymap WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                Map<Integer, Pair<Byte, Integer>> keyb = ret.keylayout.Layout();
                while (rs.next()) {
                    keyb.put(rs.getInt("key"), new Pair<Byte, Integer>(rs.getByte("type"), rs.getInt("action")));
                }
                rs.close();
                ps.close();
                ret.keylayout.unchanged();
                ps = con.prepareStatement("SELECT `locationtype`,`map` FROM savedlocations WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                while (rs.next()) {
                    ret.savedLocations[rs.getInt("locationtype")] = rs.getInt("map");
                }
                rs.close();
                ps.close();
                ps = con.prepareStatement("SELECT `characterid_to`,`when` FROM famelog WHERE characterid = ? AND DATEDIFF(NOW(),`when`) < 30");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                ret.lastfametime = 0L;
                ret.lastmonthfameids = new ArrayList(31);
                while (rs.next()) {
                    ret.lastfametime = Math.max(ret.lastfametime, rs.getTimestamp("when").getTime());
                    ret.lastmonthfameids.add(rs.getInt("characterid_to"));
                }
                rs.close();
                ps.close();
                ps = con.prepareStatement("SELECT `accid_to`,`when` FROM battlelog WHERE accid = ? AND DATEDIFF(NOW(),`when`) < 30");
                ps.setInt(1, ret.accountid);
                rs = ps.executeQuery();
                ret.lastmonthbattleids = new ArrayList();
                while (rs.next()) {
                    ret.lastmonthbattleids.add(Integer.valueOf(rs.getInt("accid_to")));
                }
                rs.close();
                ps.close();
                ps = con.prepareStatement("SELECT `itemId` FROM extendedSlots WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                while (rs.next()) {
                    ret.extendedSlots.add(Integer.valueOf(rs.getInt("itemId")));
                }
                rs.close();
                ps.close();
                ret.buddylist.loadFromDb(charid);

                ret.storage = MapleStorage.loadStorage(ret.accountid);

                ret.cs = new CashShop(ret.accountid, charid, ret.getJob());

                ps = con.prepareStatement("SELECT sn FROM wishlist WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                int i = 0;
                while (rs.next()) {
                    ret.wishlist[i] = rs.getInt("sn");
                    i++;
                }
                while (i < 10) {
                    ret.wishlist[i] = 0;
                    i++;
                }
                rs.close();
                ps.close();
                ps = con.prepareStatement("SELECT mapid,vip FROM trocklocations WHERE characterid = ? LIMIT 28");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                int r = 0;
                int reg = 0;
                int hyper = 0;
                while (rs.next()) {
                    if (rs.getInt("vip") == 0) {
                        ret.regrocks[reg] = rs.getInt("mapid");
                        reg++;
                        continue;
                    }
                    if (rs.getInt("vip") == 1) {
                        ret.rocks[r] = rs.getInt("mapid");
                        r++;
                        continue;
                    }
                    if (rs.getInt("vip") == 2) {
                        ret.hyperrocks[hyper] = rs.getInt("mapid");
                        hyper++;
                    }
                }
                while (reg < 5) {
                    ret.regrocks[reg] = 999999999;
                    reg++;
                }
                while (r < 10) {
                    ret.rocks[r] = 999999999;
                    r++;
                }
                while (hyper < 13) {
                    ret.hyperrocks[hyper] = 999999999;
                    hyper++;
                }
                rs.close();
                ps.close();
                ps = con.prepareStatement("SELECT * FROM imps WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                r = 0;
                while (rs.next()) {
                    ret.imps[r] = new MapleImp(rs.getInt("itemid"));
                    ret.imps[r].setLevel(rs.getByte("level"));
                    ret.imps[r].setState(rs.getByte("state"));
                    ret.imps[r].setCloseness(rs.getShort("closeness"));
                    ret.imps[r].setFullness(rs.getShort("fullness"));
                    r++;
                }
                rs.close();
                ps.close();
                ps = con.prepareStatement("SELECT * FROM mountdata WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                if (!rs.next()) {
                    throw new RuntimeException("No mount data found on SQL column");
                }

                Item mount = ret.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -18);
                ret.mount = new MapleMount(ret, mount != null ? mount.getItemId() : 0, 80001000, rs.getByte("Fatigue"), rs.getByte("Level"), rs.getInt("Exp"));
                ps.close();
                rs.close();

                ret.stats.recalcLocalStats(true, ret);
            } else {
                for (Pair mit : ItemLoader.装备道具.loadItems(true, charid).values()) {
                    ret.getInventory((MapleInventoryType) mit.getRight()).addFromDB((Item) mit.getLeft());
                }
                ret.stats.recalcPVPRank(ret);
            }
        } catch (SQLException ess) {
            ess.printStackTrace();
            System.out.println("加载角色数据信息出错...");
            FileoutputUtil.outputFileError("log\\Packet_Except.log", ess);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ignore) {
            }
        }
        return ret;
    }

    public static void saveNewCharToDB(MapleCharacter chr, JobType type, short db) {
        Connection con = DatabaseConnection.getConnection();

        PreparedStatement ps = null;
        PreparedStatement pse = null;
        ResultSet rs = null;
        try {
            con.setTransactionIsolation(1);
            con.setAutoCommit(false);

            ps = con.prepareStatement("INSERT INTO characters (level, str, dex, luk, `int`, hp, mp, maxhp, maxmp, sp, ap, skincolor, gender, job, hair, face, map, meso, party, buddyCapacity, pets, decorate, subcategory, accountid, name, world) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 1);
            ps.setInt(1, chr.level);
            PlayerStats stat = chr.stats;
            ps.setShort(2, stat.getStr());
            ps.setShort(3, stat.getDex());
            ps.setShort(4, stat.getInt());
            ps.setShort(5, stat.getLuk());
            ps.setInt(6, stat.getHp());
            ps.setInt(7, stat.getMp());
            ps.setInt(8, stat.getMaxHp());
            ps.setInt(9, stat.getMaxMp());
            StringBuilder sps = new StringBuilder();
            for (int i = 0; i < chr.remainingSp.length; i++) {
                sps.append(chr.remainingSp[i]);
                sps.append(",");
            }
            String sp = sps.toString();
            ps.setString(10, sp.substring(0, sp.length() - 1));
            ps.setShort(11, chr.remainingAp);
            ps.setByte(12, chr.skinColor);
            ps.setByte(13, chr.gender);
            ps.setShort(14, chr.job);
            ps.setInt(15, chr.hair);
            ps.setInt(16, chr.face);
            if (db < 0 || db > 10) {
                db = 0;
            }
            ps.setInt(17, type.map);
            ps.setInt(18, chr.meso);
            ps.setInt(19, -1);
            ps.setByte(20, chr.buddylist.getCapacity());
            ps.setString(21, "-1,-1,-1");
            ps.setInt(22, db);
            ps.setInt(23, db);
            ps.setInt(24, chr.getAccountID());
            ps.setString(25, chr.name);
            ps.setByte(26, chr.world);
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                chr.id = rs.getInt(1);
            } else {
                ps.close();
                rs.close();
                throw new DatabaseException("生成新角色到数据库出错...");
            }
            ps.close();
            rs.close();
            ps = con.prepareStatement("INSERT INTO queststatus (`queststatusid`, `characterid`, `quest`, `status`, `time`, `forfeited`, `customData`) VALUES (DEFAULT, ?, ?, ?, ?, ?, ?)", 1);
            pse = con.prepareStatement("INSERT INTO queststatusmobs VALUES (DEFAULT, ?, ?, ?)");
            ps.setInt(1, chr.id);
            for (MapleQuestStatus q : chr.quests.values()) {
                ps.setInt(2, q.getQuest().getId());
                ps.setInt(3, q.getStatus());
                ps.setInt(4, (int) (q.getCompletionTime() / 1000));
                ps.setInt(5, q.getForfeited());
                ps.setString(6, q.getCustomData());
                ps.execute();
                rs = ps.getGeneratedKeys();
                if (q.hasMobKills()) {
                    rs.next();
                    for (int mob : q.getMobKills().keySet()) {
                        pse.setInt(1, rs.getInt(1));
                        pse.setInt(2, mob);
                        pse.setInt(3, q.getMobKills(mob));
                        pse.execute();
                    }
                }
                rs.close();
            }
            ps.close();
            pse.close();

            ps = con.prepareStatement("INSERT INTO skills (characterid, skillid, skilllevel, masterlevel, expiration, teachId) VALUES (?, ?, ?, ?, ?, ?)");
            ps.setInt(1, chr.id);

            for (final Entry<Skill, SkillEntry> skill : chr.skills.entrySet()) {
                if (GameConstants.isApplicableSkill(skill.getKey().getId())) {
                    ps.setInt(2, (skill.getKey()).getId());
                    ps.setInt(3, skill.getValue().skillevel);
                    ps.setByte(4, skill.getValue().masterlevel);
                    ps.setLong(5, skill.getValue().expiration);
                    ps.setInt(6, skill.getValue().teachId);
                    ps.execute();
                }
            }
            ps.close();

            ps = con.prepareStatement("INSERT INTO inventoryslot (characterid, `equip`, `use`, `setup`, `etc`, `cash`) VALUES (?, ?, ?, ?, ?, ?)");
            ps.setInt(1, chr.id);
            ps.setByte(2, (byte) 32);
            ps.setByte(3, (byte) 32);
            ps.setByte(4, (byte) 32);
            ps.setByte(5, (byte) 32);
            ps.setByte(6, (byte) 60);
            ps.execute();
            ps.close();

            ps = con.prepareStatement("INSERT INTO mountdata (characterid, `Level`, `Exp`, `Fatigue`) VALUES (?, ?, ?, ?)");
            ps.setInt(1, chr.id);
            ps.setByte(2, (byte) 1);
            ps.setInt(3, 0);
            ps.setByte(4, (byte) 0);
            ps.execute();
            ps.close();

            int[] array1 = {2, 3, 4, 5, 6, 7, 16, 17, 18, 19, 20, 21, 23, 24, 25, 26, 27, 29, 31, 33, 34, 35, 37, 38, 39, 40, 41, 43, 44, 45, 46, 47, 48, 50, 56, 57, 59, 60, 61, 62, 63, 64, 65};
            int[] array2 = {4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 4, 4, 4, 4, 5, 5, 6, 6, 6, 6, 6, 6, 6};
            int[] array3 = {10, 12, 13, 18, 23, 28, 8, 5, 0, 4, 27, 30, 1, 24, 19, 14, 15, 52, 2, 25, 17, 11, 3, 20, 26, 16, 22, 9, 50, 51, 6, 31, 29, 7, 53, 54, 100, 101, 102, 103, 104, 105, 106};

            ps = con.prepareStatement("INSERT INTO keymap (characterid, `key`, `type`, `action`) VALUES (?, ?, ?, ?)");
            ps.setInt(1, chr.id);
            for (int i = 0; i < array1.length; i++) {
                ps.setInt(2, array1[i]);
                ps.setInt(3, array2[i]);
                ps.setInt(4, array3[i]);
                ps.execute();
            }
            ps.close();

            List<Pair<Item, MapleInventoryType>> itemsWithType = new ArrayList<Pair<Item, MapleInventoryType>>();
            for (MapleInventory iv : chr.inventory) {
                for (Item item : iv.list()) {
                    itemsWithType.add(new Pair<Item, MapleInventoryType>(item, iv.getType()));
                }
            }
            ItemLoader.装备道具.saveItems(itemsWithType, chr.id);
            con.commit();
        } catch (Exception e) {
            e.printStackTrace();
            FileoutputUtil.outputFileError("log\\Packet_Except.log", e);
            System.err.println("[charsave] Error saving character data");
            try {
                con.rollback();
            } catch (SQLException ex) {
                FileoutputUtil.outputFileError("log\\Packet_Except.log", ex);
                System.err.println("[charsave] Error Rolling Back");
            }
        } finally {
            try {
                if (pse != null) {
                    pse.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                con.setAutoCommit(true);
                con.setTransactionIsolation(4);
            } catch (SQLException e) {
                FileoutputUtil.outputFileError("log\\Packet_Except.log", e);
                System.err.println("[charsave] Error going back to autocommit mode");
            }
        }
    }

    public void saveToDB(boolean dc, boolean fromcs) {
        if (isClone()) {
            return;
        }
        Connection con = DatabaseConnection.getConnection();

        PreparedStatement ps = null;
        PreparedStatement pse = null;
        ResultSet rs = null;
        try {
            con.setTransactionIsolation(1);
            con.setAutoCommit(false);

            ps = con.prepareStatement("UPDATE characters SET level = ?, fame = ?, str = ?, dex = ?, luk = ?, `int` = ?, exp = ?, hp = ?, mp = ?, maxhp = ?, maxmp = ?, sp = ?, ap = ?, gm = ?, skincolor = ?, gender = ?, job = ?, hair = ?, face = ?, map = ?, meso = ?, hpApUsed = ?, spawnpoint = ?, party = ?, buddyCapacity = ?, pets = ?, subcategory = ?, marriageId = ?, currentrep = ?, totalrep = ?, gachexp = ?, fatigue = ?, charm = ?, charisma = ?, craft = ?, insight = ?, sense = ?, will = ?, totalwins = ?, totallosses = ?, pvpExp = ?, pvpPoints = ?, decorate = ?, elfEar = ?, beans = ?, warning = ?, dollars = ?, sharelots = ?, reborns = ?, apstorage = ?, vip = ?,vipczz=?, viptime = ?, name = ? WHERE id = ?", 1);
            ps.setInt(1, this.level);
            ps.setInt(2, this.fame);
            ps.setShort(3, this.stats.getStr());
            ps.setShort(4, this.stats.getDex());
            ps.setShort(5, this.stats.getLuk());
            ps.setShort(6, this.stats.getInt());
            //等级经验限制>= 200
            ps.setInt(7, ((this.level >= 252) || ((GameConstants.is骑士团(this.job)) && (this.level >= 120) && (!isIntern()))) ? 0 : this.exp);
            ps.setInt(8, this.stats.getHp() < 1 ? 50 : this.stats.getHp());
            ps.setInt(9, this.stats.getMp());
            ps.setInt(10, this.stats.getMaxHp());
            ps.setInt(11, this.stats.getMaxMp());
            StringBuilder sps = new StringBuilder();
            for (int i = 0; i < this.remainingSp.length; i++) {
                sps.append(this.remainingSp[i]);
                sps.append(",");
            }
            String sp = sps.toString();
            ps.setString(12, sp.substring(0, sp.length() - 1));
            ps.setShort(13, this.remainingAp);
            ps.setByte(14, this.gmLevel);
            ps.setByte(15, this.skinColor);
            ps.setByte(16, this.gender);
            ps.setShort(17, this.job);
            ps.setInt(18, this.hair);
            ps.setInt(19, this.face);
            if ((!fromcs) && (this.map != null)) {
                if (this.map.getId() == 180000001) {
                    ps.setInt(20, 180000001);
                } else if ((this.map.getForcedReturnId() != 999999999) && (this.map.getForcedReturnMap() != null)) {
                    ps.setInt(20, this.map.getForcedReturnId());
                } else {
                    ps.setInt(20, this.stats.getHp() < 1 ? this.map.getReturnMapId() : this.map.getId());
                }
            } else {
                ps.setInt(20, this.mapid);
            }
            ps.setInt(21, this.meso);
            ps.setShort(22, this.hpApUsed);
            if (this.map == null) {
                ps.setByte(23, (byte) 0);
            } else {
                MaplePortal closest = this.map.findClosestSpawnpoint(getTruePosition());
                ps.setByte(23, (byte) (closest != null ? closest.getId() : 0));
            }
            ps.setInt(24, this.party == null ? -1 : this.party.getId());
            ps.setShort(25, (short) this.buddylist.getCapacity());
            StringBuilder petz = new StringBuilder();
            int petLength = 0;
            for (MaplePet pet : this.pets) {
                if (pet.getSummoned()) {
                    pet.saveToDb();
                    petz.append(pet.getInventoryPosition());
                    petz.append(",");
                    petLength++;
                }
            }
            while (petLength < 3) {
                petz.append("-1,");
                petLength++;
            }
            String petstring = petz.toString();
            ps.setString(26, petstring.substring(0, petstring.length() - 1));
            ps.setByte(27, this.subcategory);
            ps.setInt(28, this.marriageId);
            ps.setInt(29, this.currentrep);
            ps.setInt(30, this.totalrep);
            ps.setInt(31, this.gachexp);
            ps.setShort(32, this.fatigue);
            ps.setInt(33, ((MapleTrait) this.traits.get(MapleTrait.MapleTraitType.charm)).getTotalExp());
            ps.setInt(34, ((MapleTrait) this.traits.get(MapleTrait.MapleTraitType.charisma)).getTotalExp());
            ps.setInt(35, ((MapleTrait) this.traits.get(MapleTrait.MapleTraitType.craft)).getTotalExp());
            ps.setInt(36, ((MapleTrait) this.traits.get(MapleTrait.MapleTraitType.insight)).getTotalExp());
            ps.setInt(37, ((MapleTrait) this.traits.get(MapleTrait.MapleTraitType.sense)).getTotalExp());
            ps.setInt(38, ((MapleTrait) this.traits.get(MapleTrait.MapleTraitType.will)).getTotalExp());
            ps.setInt(39, this.totalWins);
            ps.setInt(40, this.totalLosses);
            ps.setInt(41, this.pvpExp);
            ps.setInt(42, this.pvpPoints);

            ps.setInt(43, this.decorate);

            ps.setInt(44, this.elfEar);

            ps.setInt(45, this.beans);

            ps.setInt(46, this.warning);

            ps.setInt(47, this.dollars);
            ps.setInt(48, this.shareLots);

            ps.setInt(49, this.reborns);
            ps.setInt(50, this.apstorage);

            ps.setInt(51, this.vip);
            ps.setInt(52, this.vipczz);
            ps.setTimestamp(53, getViptime() == null ? null : getViptime());

            ps.setString(54, this.name);
            ps.setInt(55, this.id);
            if (ps.executeUpdate() < 1) {
                ps.close();
                throw new DatabaseException(new StringBuilder().append("Character not in database (").append(this.id).append(")").toString());
            }
            ps.close();
            if (this.changed_skillmacros) {
                deleteWhereCharacterId(con, "DELETE FROM skillmacros WHERE characterid = ?");
                for (int i = 0; i < 5; i++) {
                    SkillMacro macro = this.skillMacros[i];
                    if (macro != null) {
                        ps = con.prepareStatement("INSERT INTO skillmacros (characterid, skill1, skill2, skill3, name, shout, position) VALUES (?, ?, ?, ?, ?, ?, ?)");
                        ps.setInt(1, this.id);
                        ps.setInt(2, macro.getSkill1());
                        ps.setInt(3, macro.getSkill2());
                        ps.setInt(4, macro.getSkill3());
                        ps.setString(5, macro.getName());
                        ps.setInt(6, macro.getShout());
                        ps.setInt(7, i);
                        ps.execute();
                        ps.close();
                    }
                }
            }
            if (this.changed_pokemon) {
                ps = con.prepareStatement("DELETE FROM pokemon WHERE characterid = ? OR (accountid = ? AND active = 0)");
                ps.setInt(1, this.id);
                ps.setInt(2, this.accountid);
                ps.execute();
                ps.close();
                ps = con.prepareStatement("INSERT INTO pokemon (characterid, level, exp, monsterid, name, nature, active, accountid, itemid, gender, hpiv, atkiv, defiv, spatkiv, spdefiv, speediv, evaiv, acciv, ability) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                for (int i = 0; i < this.battlers.length; i++) {
                    Battler macro = this.battlers[i];
                    if (macro != null) {
                        ps.setInt(1, this.id);
                        ps.setInt(2, macro.getLevel());
                        ps.setInt(3, macro.getExp());
                        ps.setInt(4, macro.getMonsterId());
                        ps.setString(5, macro.getName());
                        ps.setInt(6, macro.getNature().ordinal());
                        ps.setInt(7, 1);
                        ps.setInt(8, this.accountid);
                        ps.setInt(9, macro.getItem() == null ? 0 : macro.getItem().id);
                        ps.setByte(10, macro.getGender());
                        ps.setByte(11, macro.getIV(PokemonStat.HP));
                        ps.setByte(12, macro.getIV(PokemonStat.ATK));
                        ps.setByte(13, macro.getIV(PokemonStat.DEF));
                        ps.setByte(14, macro.getIV(PokemonStat.SPATK));
                        ps.setByte(15, macro.getIV(PokemonStat.SPDEF));
                        ps.setByte(16, macro.getIV(PokemonStat.SPEED));
                        ps.setByte(17, macro.getIV(PokemonStat.EVA));
                        ps.setByte(18, macro.getIV(PokemonStat.ACC));
                        ps.setByte(19, macro.getAbilityIndex());
                        ps.execute();
                    }
                }
                for (Battler macro : this.boxed) {
                    ps.setInt(1, this.id);
                    ps.setInt(2, macro.getLevel());
                    ps.setInt(3, macro.getExp());
                    ps.setInt(4, macro.getMonsterId());
                    ps.setString(5, macro.getName());
                    ps.setInt(6, macro.getNature().ordinal());
                    ps.setInt(7, 0);
                    ps.setInt(8, this.accountid);
                    ps.setInt(9, macro.getItem() == null ? 0 : macro.getItem().id);
                    ps.setByte(10, macro.getGender());
                    ps.setByte(11, macro.getIV(PokemonStat.HP));
                    ps.setByte(12, macro.getIV(PokemonStat.ATK));
                    ps.setByte(13, macro.getIV(PokemonStat.DEF));
                    ps.setByte(14, macro.getIV(PokemonStat.SPATK));
                    ps.setByte(15, macro.getIV(PokemonStat.SPDEF));
                    ps.setByte(16, macro.getIV(PokemonStat.SPEED));
                    ps.setByte(17, macro.getIV(PokemonStat.EVA));
                    ps.setByte(18, macro.getIV(PokemonStat.ACC));
                    ps.setByte(19, macro.getAbilityIndex());
                    ps.execute();
                }
                ps.close();
            }

            deleteWhereCharacterId(con, "DELETE FROM inventoryslot WHERE characterid = ?");
            ps = con.prepareStatement("INSERT INTO inventoryslot (characterid, `equip`, `use`, `setup`, `etc`, `cash`) VALUES (?, ?, ?, ?, ?, ?)");
            ps.setInt(1, this.id);
            ps.setByte(2, getInventory(MapleInventoryType.EQUIP).getSlotLimit());
            ps.setByte(3, getInventory(MapleInventoryType.USE).getSlotLimit());
            ps.setByte(4, getInventory(MapleInventoryType.SETUP).getSlotLimit());
            ps.setByte(5, getInventory(MapleInventoryType.ETC).getSlotLimit());
            ps.setByte(6, getInventory(MapleInventoryType.CASH).getSlotLimit());
            ps.execute();
            ps.close();

            List<Pair<Item, MapleInventoryType>> itemsWithType = new ArrayList<Pair<Item, MapleInventoryType>>();
            for (final MapleInventory iv : this.inventory) {
                for (final Item item : iv.list()) {
                    itemsWithType.add(new Pair<Item, MapleInventoryType>(item, iv.getType()));
                }
            }
            ItemLoader.装备道具.saveItems(itemsWithType, this.id);

            if (this.changed_questinfo) {
                deleteWhereCharacterId(con, "DELETE FROM questinfo WHERE characterid = ?");
                ps = con.prepareStatement("INSERT INTO questinfo (`characterid`, `quest`, `customData`) VALUES (?, ?, ?)");
                ps.setInt(1, this.id);
                for (Map.Entry q : this.questinfo.entrySet()) {
                    ps.setInt(2, ((Integer) q.getKey()).intValue());
                    ps.setString(3, (String) q.getValue());
                    ps.execute();
                }
                ps.close();
            }

            deleteWhereCharacterId(con, "DELETE FROM queststatus WHERE characterid = ?");
            ps = con.prepareStatement("INSERT INTO queststatus (`queststatusid`, `characterid`, `quest`, `status`, `time`, `forfeited`, `customData`) VALUES (DEFAULT, ?, ?, ?, ?, ?, ?)", 1);
            pse = con.prepareStatement("INSERT INTO queststatusmobs VALUES (DEFAULT, ?, ?, ?)");
            ps.setInt(1, this.id);
            for (MapleQuestStatus q : this.quests.values()) {
                ps.setInt(2, q.getQuest().getId());
                ps.setInt(3, q.getStatus());
                ps.setInt(4, (int) (q.getCompletionTime() / 1000L));
                ps.setInt(5, q.getForfeited());
                ps.setString(6, q.getCustomData());
                ps.execute();
                rs = ps.getGeneratedKeys();
                Iterator i$;
                if (q.hasMobKills()) {
                    rs.next();
                    for (i$ = q.getMobKills().keySet().iterator(); i$.hasNext(); ) {
                        int mob = ((Integer) i$.next()).intValue();
                        pse.setInt(1, rs.getInt(1));
                        pse.setInt(2, mob);
                        pse.setInt(3, q.getMobKills(mob));
                        pse.execute();
                    }
                }
                rs.close();
            }
            ps.close();
            pse.close();

            if (this.changed_skills) {
                deleteWhereCharacterId(con, "DELETE FROM skills WHERE characterid = ?");
                ps = con.prepareStatement("INSERT INTO skills (characterid, skillid, skilllevel, masterlevel, expiration, teachId) VALUES (?, ?, ?, ?, ?, ?)");
                ps.setInt(1, this.id);

                for (Map.Entry skill : this.skills.entrySet()) {
                    if (GameConstants.isApplicableSkill(((Skill) skill.getKey()).getId())) {
                        ps.setInt(2, ((Skill) skill.getKey()).getId());
                        ps.setInt(3, ((SkillEntry) skill.getValue()).skillevel);
                        ps.setByte(4, ((SkillEntry) skill.getValue()).masterlevel);
                        ps.setLong(5, ((SkillEntry) skill.getValue()).expiration);
                        ps.setInt(6, ((SkillEntry) skill.getValue()).teachId);
                        ps.execute();
                    }
                }
                ps.close();
            }

            deleteWhereCharacterId(con, "DELETE FROM phantomskills WHERE characterid = ?");
            ps = con.prepareStatement("INSERT INTO phantomskills (characterid, skill1, skill1_0, skill1_1, skill1_2, skill1_3, skill2, skill2_0, skill2_1, skill2_2, skill2_3, skill3, skill3_0, skill3_1, skill3_2, skill4, skill4_0, skill4_1) VALUES (?, ?, ?, ?, ?,?, ?, ?, ?, ?,?, ?, ?, ?, ?,?, ?, ?)");
            ps.setInt(1, id);
            if (phantomskill == null) {
                ps.setInt(2, 0);
                ps.setInt(3, 0);
                ps.setInt(4, 0);
                ps.setInt(5, 0);
                ps.setInt(6, 0);
                ps.setInt(7, 0);
                ps.setInt(8, 0);
                ps.setInt(9, 0);
                ps.setInt(10, 0);
                ps.setInt(11, 0);
                ps.setInt(12, 0);
                ps.setInt(13, 0);
                ps.setInt(14, 0);
                ps.setInt(15, 0);
                ps.setInt(16, 0);
                ps.setInt(17, 0);
                ps.setInt(18, 0);
            } else {
                ps.setInt(2, phantomskill.get(1));
                ps.setInt(3, phantomskill.get(10));
                ps.setInt(4, phantomskill.get(11));
                ps.setInt(5, phantomskill.get(12));
                ps.setInt(6, phantomskill.get(13));
                ps.setInt(7, phantomskill.get(2));
                ps.setInt(8, phantomskill.get(20));
                ps.setInt(9, phantomskill.get(21));
                ps.setInt(10, phantomskill.get(22));
                ps.setInt(11, phantomskill.get(23));
                ps.setInt(12, phantomskill.get(3));
                ps.setInt(13, phantomskill.get(30));
                ps.setInt(14, phantomskill.get(31));
                ps.setInt(15, phantomskill.get(32));
                ps.setInt(16, phantomskill.get(4));
                ps.setInt(17, phantomskill.get(40));
                ps.setInt(18, phantomskill.get(41));
            }
            ps.execute();
            ps.close();

            List<MapleCoolDownValueHolder> cd = getCooldowns();
            if ((dc) && (cd.size() > 0)) {
                ps = con.prepareStatement("INSERT INTO skills_cooldowns (charid, SkillID, StartTime, length) VALUES (?, ?, ?, ?)");
                ps.setInt(1, getId());
                for (MapleCoolDownValueHolder cooling : cd) {
                    ps.setInt(2, cooling.skillId);
                    ps.setLong(3, cooling.startTime);
                    ps.setLong(4, cooling.length);
                    ps.execute();
                }
                ps.close();
            }

            if (this.changed_savedlocations) {
                deleteWhereCharacterId(con, "DELETE FROM savedlocations WHERE characterid = ?");
                ps = con.prepareStatement("INSERT INTO savedlocations (characterid, `locationtype`, `map`) VALUES (?, ?, ?)");
                ps.setInt(1, this.id);
                for (SavedLocationType savedLocationType : SavedLocationType.values()) {
                    if (this.savedLocations[savedLocationType.getValue()] != -1) {
                        ps.setInt(2, savedLocationType.getValue());
                        ps.setInt(3, this.savedLocations[savedLocationType.getValue()]);
                        ps.execute();
                    }
                }
                ps.close();
            }

            if (this.changed_achievements) {
                ps = con.prepareStatement("DELETE FROM achievements WHERE accountid = ?");
                ps.setInt(1, this.accountid);
                ps.executeUpdate();
                ps.close();
                ps = con.prepareStatement("INSERT INTO achievements(charid, achievementid, accountid) VALUES(?, ?, ?)");
                for (Integer achid : this.finishedAchievements) {
                    ps.setInt(1, this.id);
                    ps.setInt(2, achid.intValue());
                    ps.setInt(3, this.accountid);
                    ps.execute();
                }
                ps.close();
            }

            if (this.changed_reports) {
                deleteWhereCharacterId(con, "DELETE FROM reports WHERE characterid = ?");
                ps = con.prepareStatement("INSERT INTO reports VALUES(DEFAULT, ?, ?, ?)");
                for (Entry<ReportType, Integer> achid : reports.entrySet()) {
                    ps.setInt(1, this.id);
                    ps.setByte(2, achid.getKey().i);
                    ps.setInt(3, achid.getValue());
                    ps.execute();
                }
                ps.close();
            }

            if (buddylist.changed()) {
                deleteWhereCharacterId(con, "DELETE FROM buddies WHERE characterid = ?");
                ps = con.prepareStatement("INSERT INTO buddies (characterid, `buddyid`, `pending`) VALUES (?, ?, ?)");
                ps.setInt(1, id);
                for (BuddylistEntry entry : buddylist.getBuddies()) {
                    ps.setInt(2, entry.getCharacterId());
                    ps.setInt(3, entry.isVisible() ? 0 : 1);
                    ps.execute();
                }
                ps.close();
                buddylist.setChanged(false);
            }

            ps = con.prepareStatement("UPDATE accounts SET `ACash` = ?, `mPoints` = ?, `points` = ?, `vpoints` = ? WHERE id = ?");
            ps.setInt(1, acash);
            ps.setInt(2, maplepoints);
            ps.setInt(3, points);
            ps.setInt(4, vpoints);
            ps.setInt(5, client.getAccID());
            ps.executeUpdate();
            ps.close();

            if (storage != null) {
                storage.saveToDB();
            }
            if (cs != null) {
                cs.save();
            }
            PlayerNPC.updateByCharId(this);
            this.keylayout.saveKeys(this.id);
            this.mount.saveMount(this.id);
            this.monsterbook.saveCards(this.accountid);

            deleteWhereCharacterId(con, "DELETE FROM familiars WHERE characterid = ?");
            ps = con.prepareStatement("INSERT INTO familiars (characterid, expiry, name, fatigue, vitality, familiar) VALUES (?, ?, ?, ?, ?, ?)");
            ps.setInt(1, this.id);
            for (MonsterFamiliar f : this.familiars.values()) {
                ps.setLong(2, f.getExpiry());
                ps.setString(3, f.getName());
                ps.setInt(4, f.getFatigue());
                ps.setByte(5, f.getVitality());
                ps.setInt(6, f.getFamiliar());
                ps.executeUpdate();
            }
            ps.close();

            deleteWhereCharacterId(con, "DELETE FROM imps WHERE characterid = ?");
            ps = con.prepareStatement("INSERT INTO imps (characterid, itemid, closeness, fullness, state, level) VALUES (?, ?, ?, ?, ?, ?)");
            ps.setInt(1, this.id);
            for (int i = 0; i < this.imps.length; i++) {
                if (this.imps[i] != null) {
                    ps.setInt(2, this.imps[i].getItemId());
                    ps.setShort(3, this.imps[i].getCloseness());
                    ps.setShort(4, this.imps[i].getFullness());
                    ps.setByte(5, this.imps[i].getState());
                    ps.setByte(6, this.imps[i].getLevel());
                    ps.executeUpdate();
                }
            }
            ps.close();
            if (this.changed_wishlist) {
                deleteWhereCharacterId(con, "DELETE FROM wishlist WHERE characterid = ?");
                for (int i = 0; i < getWishlistSize(); i++) {
                    ps = con.prepareStatement("INSERT INTO wishlist(characterid, sn) VALUES(?, ?) ");
                    ps.setInt(1, getId());
                    ps.setInt(2, this.wishlist[i]);
                    ps.execute();
                    ps.close();
                }
            }
            if (this.changed_trocklocations) {
                deleteWhereCharacterId(con, "DELETE FROM trocklocations WHERE characterid = ?");
                for (int i = 0; i < this.regrocks.length; i++) {
                    if (this.regrocks[i] != 999999999) {
                        ps = con.prepareStatement("INSERT INTO trocklocations(characterid, mapid, vip) VALUES (?, ?, 0)");
                        ps.setInt(1, getId());
                        ps.setInt(2, this.regrocks[i]);
                        ps.execute();
                        ps.close();
                    }
                }
                for (int i = 0; i < this.rocks.length; i++) {
                    if (this.rocks[i] != 999999999) {
                        ps = con.prepareStatement("INSERT INTO trocklocations(characterid, mapid, vip) VALUES (?, ?, 1)");
                        ps.setInt(1, getId());
                        ps.setInt(2, this.rocks[i]);
                        ps.execute();
                        ps.close();
                    }
                }
                for (int i = 0; i < this.hyperrocks.length; i++) {
                    if (this.hyperrocks[i] != 999999999) {
                        ps = con.prepareStatement("INSERT INTO trocklocations(characterid, mapid, vip) VALUES (?, ?, 2)");
                        ps.setInt(1, getId());
                        ps.setInt(2, this.hyperrocks[i]);
                        ps.execute();
                        ps.close();
                    }
                }
            }
            Iterator i$;
            if (this.changed_extendedSlots) {
                deleteWhereCharacterId(con, "DELETE FROM extendedSlots WHERE characterid = ?");
                for (i$ = this.extendedSlots.iterator(); i$.hasNext(); ) {
                    int i = ((Integer) i$.next()).intValue();
                    if (getInventory(MapleInventoryType.ETC).findById(i) != null) {
                        ps = con.prepareStatement("INSERT INTO extendedSlots(characterid, itemId) VALUES(?, ?) ");
                        ps.setInt(1, getId());
                        ps.setInt(2, i);
                        ps.execute();
                        ps.close();
                    }
                }
            }
            this.changed_wishlist = false;
            this.changed_trocklocations = false;
            this.changed_skillmacros = false;
            this.changed_savedlocations = false;
            this.changed_pokemon = false;
            this.changed_questinfo = false;
            this.changed_achievements = false;
            this.changed_extendedSlots = false;
            this.changed_skills = false;
            this.changed_reports = false;
            con.commit();
        } catch (Exception e) {
            FileoutputUtil.outputFileError("log\\Packet_Except.log", e);
            log.error(new StringBuilder().append(MapleClient.getLogMessage(this, "[charsave] Error saving character data")).append(e).toString());
            try {
                con.rollback();
            } catch (SQLException ex) {
                FileoutputUtil.outputFileError("log\\Packet_Except.log", ex);
                log.error(new StringBuilder().append(MapleClient.getLogMessage(this, "[charsave] Error Rolling Back")).append(e).toString());
            }
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (pse != null) {
                    pse.close();
                }
                if (rs != null) {
                    rs.close();
                }
                con.setAutoCommit(true);
                con.setTransactionIsolation(4);
            } catch (SQLException e) {
                FileoutputUtil.outputFileError("log\\Packet_Except.log", e);
                log.error(new StringBuilder().append(MapleClient.getLogMessage(this, "[charsave] Error going back to autocommit mode")).append(e).toString());
            }
        }
    }

    private void deleteWhereCharacterId(Connection con, String sql) throws SQLException {
        deleteWhereCharacterId(con, sql, this.id);
    }

    public static void deleteWhereCharacterId(Connection con, String sql, int id) throws SQLException {
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        ps.close();
    }

    public static void deleteWhereCharacterId_NoLock(Connection con, String sql, int id) throws SQLException {
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, id);
        ps.execute();
        ps.close();
    }

    public PlayerStats getStat() {
        return this.stats;
    }

    public void QuestInfoPacket(MaplePacketLittleEndianWriter mplew) {
        mplew.writeShort(this.questinfo.size());
        for (Map.Entry q : this.questinfo.entrySet()) {
            mplew.writeShort(((Integer) q.getKey()).intValue());
            mplew.writeMapleAsciiString(q.getValue() == null ? "" : (String) q.getValue());
        }
    }

    public void updateInfoQuest(int questid, String data) {
        this.questinfo.put(Integer.valueOf(questid), data);
        this.changed_questinfo = true;
        this.client.getSession().write(MaplePacketCreator.updateInfoQuest(questid, data));
    }

    public String getInfoQuest(int questid) {
        if (this.questinfo.containsKey(Integer.valueOf(questid))) {
            return (String) this.questinfo.get(Integer.valueOf(questid));
        }
        return "";
    }

    public int getNumQuest() {
        int i = 0;
        for (MapleQuestStatus q : this.quests.values()) {
            if ((q.getStatus() == 2) && (!q.isCustom())) {
                i++;
            }
        }
        return i;
    }

    public byte getQuestStatus(int quest) {
        MapleQuest qq = MapleQuest.getInstance(quest);
        if (getQuestNoAdd(qq) == null) {
            return 0;
        }
        return getQuestNoAdd(qq).getStatus();
    }

    public MapleQuestStatus getQuest(MapleQuest quest) {
        if (!this.quests.containsKey(quest)) {
            return new MapleQuestStatus(quest, 0);
        }
        return (MapleQuestStatus) this.quests.get(quest);
    }

    public void setQuestAdd(MapleQuest quest, byte status, String customData) {
        if (!this.quests.containsKey(quest)) {
            MapleQuestStatus stat = new MapleQuestStatus(quest, status);
            stat.setCustomData(customData);
            this.quests.put(quest, stat);
        }
    }

    public MapleQuestStatus getQuestNAdd(MapleQuest quest) {
        if (!this.quests.containsKey(quest)) {
            MapleQuestStatus status = new MapleQuestStatus(quest, 0);
            this.quests.put(quest, status);
            return status;
        }
        return (MapleQuestStatus) this.quests.get(quest);
    }

    public MapleQuestStatus getQuestNoAdd(MapleQuest quest) {
        return (MapleQuestStatus) this.quests.get(quest);
    }

    public MapleQuestStatus getQuestRemove(MapleQuest quest) {
        return (MapleQuestStatus) this.quests.remove(quest);
    }

    public void updateQuest(MapleQuestStatus quest) {
        updateQuest(quest, false);
    }

    public void updateQuest(MapleQuestStatus quest, boolean update) {
        this.quests.put(quest.getQuest(), quest);
        if (!quest.isCustom()) {
            this.client.getSession().write(MaplePacketCreator.updateQuest(quest));
            if ((quest.getStatus() == 1) && (!update)) {
                this.client.getSession().write(MaplePacketCreator.updateQuestInfo(this, quest.getQuest().getId(), quest.getNpc(), (byte) 10));
            }
        }
    }

    public Map<Integer, String> getInfoQuest_Map() {
        return this.questinfo;
    }

    public Map<MapleQuest, MapleQuestStatus> getQuest_Map() {
        return this.quests;
    }

    public Integer getBuffedValue(MapleBuffStat effect) {
        MapleBuffStatValueHolder mbsvh = (MapleBuffStatValueHolder) this.effects.get(effect);
        return mbsvh == null ? null : Integer.valueOf(mbsvh.value);
    }

    public Integer getBuffedSkill_X(MapleBuffStat effect) {
        MapleBuffStatValueHolder mbsvh = (MapleBuffStatValueHolder) this.effects.get(effect);
        if (mbsvh == null) {
            return null;
        }
        return Integer.valueOf(mbsvh.effect.getX());
    }

    public Integer getBuffedSkill_Y(MapleBuffStat effect) {
        MapleBuffStatValueHolder mbsvh = (MapleBuffStatValueHolder) this.effects.get(effect);
        if (mbsvh == null) {
            return null;
        }
        return Integer.valueOf(mbsvh.effect.getY());
    }

    public boolean isBuffFrom(MapleBuffStat stat, Skill skill) {
        MapleBuffStatValueHolder mbsvh = (MapleBuffStatValueHolder) this.effects.get(stat);
        if ((mbsvh == null) || (mbsvh.effect == null)) {
            return false;
        }
        return (mbsvh.effect.isSkill()) && (mbsvh.effect.getSourceId() == skill.getId());
    }

    public int getBuffSource(MapleBuffStat stat) {
        MapleBuffStatValueHolder mbsvh = (MapleBuffStatValueHolder) this.effects.get(stat);
        return mbsvh == null ? -1 : mbsvh.effect.getSourceId();
    }

    public int getTrueBuffSource(MapleBuffStat stat) {
        MapleBuffStatValueHolder mbsvh = (MapleBuffStatValueHolder) this.effects.get(stat);
        return mbsvh.effect.isSkill() ? mbsvh.effect.getSourceId() : mbsvh == null ? -1 : -mbsvh.effect.getSourceId();
    }

    public void setBuffedValue(MapleBuffStat effect, int value) {
        MapleBuffStatValueHolder mbsvh = (MapleBuffStatValueHolder) this.effects.get(effect);
        if (mbsvh == null) {
            return;
        }
        mbsvh.value = value;
    }

    public void setSchedule(MapleBuffStat effect, ScheduledFuture<?> sched) {
        MapleBuffStatValueHolder mbsvh = (MapleBuffStatValueHolder) this.effects.get(effect);
        if (mbsvh == null) {
            return;
        }
        mbsvh.schedule.cancel(false);
        mbsvh.schedule = sched;
    }

    public Long getBuffedStarttime(MapleBuffStat effect) {
        MapleBuffStatValueHolder mbsvh = (MapleBuffStatValueHolder) this.effects.get(effect);
        return mbsvh == null ? null : Long.valueOf(mbsvh.startTime);
    }

    public MapleStatEffect getStatForBuff(MapleBuffStat effect) {
        MapleBuffStatValueHolder mbsvh = (MapleBuffStatValueHolder) this.effects.get(effect);
        return mbsvh == null ? null : mbsvh.effect;
    }

    public void doDragonBlood() {
        MapleStatEffect bloodEffect = getStatForBuff(MapleBuffStat.龙之力);
        if (bloodEffect == null) {
            this.lastDragonBloodTime = 0L;
            return;
        }
        prepareDragonBlood();
        if (this.stats.getHp() - bloodEffect.getStr() <= 1) {
            cancelBuffStats(new MapleBuffStat[]{MapleBuffStat.龙之力});
        } else {
            addHP(-bloodEffect.getStr());
            this.client.getSession().write(MaplePacketCreator.showOwnBuffEffect(bloodEffect.getSourceId(), 7, getLevel(), bloodEffect.getLevel()));
            this.map.broadcastMessage(this, MaplePacketCreator.showBuffeffect(getId(), bloodEffect.getSourceId(), 7, getLevel(), bloodEffect.getLevel()), false);
        }
    }

    public boolean canBlood(long now) {
        return (this.lastDragonBloodTime > 0L) && (this.lastDragonBloodTime + 4000L < now);
    }

    private void prepareDragonBlood() {
        this.lastDragonBloodTime = System.currentTimeMillis();
    }

    public void doRecovery() {
        MapleStatEffect bloodEffect = getStatForBuff(MapleBuffStat.RECOVERY);
        if (bloodEffect == null) {
            bloodEffect = getStatForBuff(MapleBuffStat.金属机甲);
            if (bloodEffect == null) {
                this.lastRecoveryTime = 0L;
            } else if (bloodEffect.getSourceId() == 35121005) {
                prepareRecovery();
                if (this.stats.getMp() < bloodEffect.getU()) {
                    cancelEffectFromBuffStat(MapleBuffStat.骑兽技能);
                    cancelEffectFromBuffStat(MapleBuffStat.金属机甲);
                } else {
                    addMP(-bloodEffect.getU());
                }
            }
        } else {
            prepareRecovery();
            if (this.stats.getHp() >= this.stats.getCurrentMaxHp()) {
                cancelEffectFromBuffStat(MapleBuffStat.RECOVERY);
            } else {
                healHP(bloodEffect.getX());
            }
        }
    }

    public boolean canRecover(long now) {
        return (this.lastRecoveryTime > 0L) && (this.lastRecoveryTime + 5000L < now);
    }

    private void prepareRecovery() {
        this.lastRecoveryTime = System.currentTimeMillis();
    }

    public void startMapTimeLimitTask(int time, final MapleMap to) {
        if (time <= 0) {
            time = 1;
        }
        this.client.getSession().write(MaplePacketCreator.getClock(time));
        final MapleMap ourMap = getMap();
        time *= 1000;
        this.mapTimeLimitTask = MapTimer.getInstance().register(new Runnable() {
            public void run() {
                if (ourMap.getId() == 180000001) {
                    MapleCharacter.this.getQuestNAdd(MapleQuest.getInstance(123455)).setCustomData(String.valueOf(System.currentTimeMillis()));
                    MapleCharacter.this.getQuestNAdd(MapleQuest.getInstance(123456)).setCustomData("0");
                }
                MapleCharacter.this.changeMap(to, to.getPortal(0));
            }
        }, time, time);
    }

    public boolean canDOT(long now) {
        return (this.lastDOTTime > 0L) && (this.lastDOTTime + 8000L < now);
    }

    public boolean hasDOT() {
        return this.dotHP > 0;
    }

    public void doDOT() {
        addHP(-(this.dotHP * 4));
        this.dotHP = 0;
        this.lastDOTTime = 0L;
    }

    public void setDOT(int d, int source, int sourceLevel) {
        this.dotHP = d;
        addHP(-(this.dotHP * 4));
        this.map.broadcastMessage(MaplePacketCreator.getPVPMist(this.id, source, sourceLevel, d));
        this.lastDOTTime = System.currentTimeMillis();
    }

    public void startFishingTask() {
        cancelFishingTask();
        this.lastFishingTime = System.currentTimeMillis();
    }

    public boolean canFish(long now) {
        return (this.lastFishingTime > 0L) && (this.lastFishingTime + GameConstants.getFishingTime(this.stats.canFishVIP, isGM()) < now);
    }

    public void doFish(long now) {
        this.lastFishingTime = now;
        boolean expMulti = haveItem(2300001, 1, false, true);
        if ((this.client == null) || (this.client.getPlayer() == null) || (!this.client.isReceiving()) || ((!expMulti) && (!haveItem(2300000, 1, false, true))) || (!GameConstants.isFishingMap(getMapId())) || (!this.stats.canFish) || (this.chair <= 0)) {
            cancelFishingTask();
            return;
        }
        MapleInventoryManipulator.removeById(this.client, MapleInventoryType.USE, expMulti ? 2300001 : 2300000, 1, false, false);
        boolean passed = false;
        while (!passed) {
            int randval = RandomRewards.getFishingReward();
            switch (randval) {
                case 0:
                    int money = Randomizer.rand(expMulti ? 15 : 10, expMulti ? 75000 : 50000);
                    gainMeso(money, true);
                    passed = true;
                    break;
                case 1:
                    int experi = Math.min(Randomizer.nextInt(Math.abs(getNeededExp() / 200) + 1), 500000);
                    gainExp(expMulti ? experi * 3 / 2 : experi, true, false, true);
                    passed = true;
                    break;
                default:
                    if (!MapleItemInformationProvider.getInstance().itemExists(randval)) {
                        break;
                    }
                    MapleInventoryManipulator.addById(this.client, randval, (short) 1, new StringBuilder().append("钓鱼 时间 ").append(FileoutputUtil.CurrentReadable_Date()).toString());
                    passed = true;
            }

        }

        this.map.broadcastMessage(UIPacket.fishingCaught(this.id));
    }

    public void cancelMapTimeLimitTask() {
        if (this.mapTimeLimitTask != null) {
            this.mapTimeLimitTask.cancel(false);
            this.mapTimeLimitTask = null;
        }
    }

    public int getNeededExp() {
        return GameConstants.getExpNeededForLevel(this.level);
    }

    public void cancelFishingTask() {
        this.lastFishingTime = 0L;
    }

    public void registerEffect(MapleStatEffect effect, long starttime, ScheduledFuture<?> schedule, int from) {
        registerEffect(effect, starttime, schedule, effect.getStatups(), false, effect.getDuration(), from);
    }

    public void registerEffect(MapleStatEffect effect, long starttime, ScheduledFuture<?> schedule, List<Pair<MapleBuffStat, Integer>> statups, boolean silent, int localDuration, int cid) {
        if (effect.is隐藏术()) {
            this.map.broadcastMessage(this, MaplePacketCreator.removePlayerFromMap(getId()), false);
        } else if (effect.is龙之力()) {
            prepareDragonBlood();
        } else if (effect.is团队治疗()) {
            prepareRecovery();
        } else if (effect.is黑暗力量()) {
            checkBerserk();
        } else if (effect.is骑兽技能_()) {
            getMount().startSchedule();
        }
        int clonez = 0;
        for (Pair<MapleBuffStat, Integer> statup : statups) {
            if (statup.getLeft() == MapleBuffStat.ILLUSION) {
                clonez = ((Integer) statup.getRight()).intValue();
            }
            int value = ((Integer) statup.getRight()).intValue();
            if (statup.getLeft() == MapleBuffStat.骑兽技能) {
                if ((effect.getSourceId() == 5921006) && (this.battleshipHP <= 0)) {
                    this.battleshipHP = maxBattleshipHP(effect.getSourceId());
                }
                removeFamiliar();
            }
            this.effects.put(statup.getLeft(), new MapleBuffStatValueHolder(effect, starttime, schedule, value, localDuration, cid));
        }
        if (clonez > 0) {
            int cloneSize = Math.max(getNumClones(), getCloneSize());
            if (clonez > cloneSize) {
                for (int i = 0; i < clonez - cloneSize; i++) {
                    cloneLook();
                }
            }
        }
        if (!silent) {
            this.stats.recalcLocalStats(this);
        }
    }

    public List<MapleBuffStat> getBuffStats(final MapleStatEffect effect, final long startTime) {
        final List<MapleBuffStat> bstats = new ArrayList<MapleBuffStat>();
        final Map<MapleBuffStat, MapleBuffStatValueHolder> allBuffs = new EnumMap<MapleBuffStat, MapleBuffStatValueHolder>(effects);
        for (Entry<MapleBuffStat, MapleBuffStatValueHolder> stateffect : allBuffs.entrySet()) {
            MapleBuffStatValueHolder mbsvh = (MapleBuffStatValueHolder) stateffect.getValue();
            if ((mbsvh.effect.sameSource(effect)) && ((startTime == -1L) || (startTime == mbsvh.startTime) || (((MapleBuffStat) stateffect.getKey()).canStack()))) {
                bstats.add(stateffect.getKey());
            }
        }
        return bstats;
    }

    private boolean deregisterBuffStats(List<MapleBuffStat> stats) {
        boolean clonez = false;
        List<MapleBuffStatValueHolder> effectsToCancel = new ArrayList(stats.size());
        for (MapleBuffStat stat : stats) {
            MapleBuffStatValueHolder mbsvh = (MapleBuffStatValueHolder) this.effects.remove(stat);
            if (mbsvh != null) {
                boolean addMbsvh = true;
                for (MapleBuffStatValueHolder contained : effectsToCancel) {
                    if ((mbsvh.startTime == contained.startTime) && (contained.effect == mbsvh.effect)) {
                        addMbsvh = false;
                    }
                }
                if (addMbsvh) {
                    effectsToCancel.add(mbsvh);
                }
                if ((stat == MapleBuffStat.召唤兽) || (stat == MapleBuffStat.替身术) || (stat == MapleBuffStat.幻灵重生) || (stat == MapleBuffStat.灵魂助力) || (stat == MapleBuffStat.DAMAGE_BUFF) || (stat == MapleBuffStat.地雷) || (stat == MapleBuffStat.ANGEL_ATK)) {
                    int summonId = mbsvh.effect.getSourceId();
                    List<MapleSummon> toRemove = new ArrayList<MapleSummon>();
                    this.visibleMapObjectsLock.writeLock().lock();
                    this.summonsLock.writeLock().lock();
                    try {
                        for (MapleSummon summon : this.summons) {
                            if ((summon.getSkill() == summonId) || ((stat == MapleBuffStat.地雷) && (summonId == 33101008)) || ((summonId == 35121009) && (summon.getSkill() == 35121011)) || (((summonId != 86) && (summonId != 88) && (summonId != 91)) || ((summon.getSkill() == summonId + 999) || (((summonId == 1085) || (summonId == 1087) || (summonId == 1090)) && (summon.getSkill() == summonId - 999))))) {
                                this.map.broadcastMessage(MaplePacketCreator.removeSummon(summon, true));
                                this.map.removeMapObject(summon);
                                this.visibleMapObjects.remove(summon);
                                toRemove.add(summon);
                            }
                        }
                        for (MapleSummon s : toRemove) {
                            this.summons.remove(s);
                        }
                    } finally {
                        this.summonsLock.writeLock().unlock();
                        this.visibleMapObjectsLock.writeLock().unlock();
                    }
                    if ((summonId == 3111005) || (summonId == 3211005)) {
                        cancelEffectFromBuffStat(MapleBuffStat.精神连接);
                    }
                } else if (stat == MapleBuffStat.龙之力) {
                    this.lastDragonBloodTime = 0L;
                } else if ((stat == MapleBuffStat.RECOVERY) || (mbsvh.effect.getSourceId() == 35121005)) {
                    this.lastRecoveryTime = 0L;
                } else if ((stat == MapleBuffStat.导航辅助) || (stat == MapleBuffStat.神秘瞄准术)) {
                    this.linkMobs.clear();
                } else if (stat == MapleBuffStat.ILLUSION) {
                    disposeClones();
                    clonez = true;
                }
            }
        }
        for (MapleBuffStatValueHolder cancelEffectCancelTasks : effectsToCancel) {
            if ((getBuffStats(cancelEffectCancelTasks.effect, cancelEffectCancelTasks.startTime).isEmpty())
                    && (cancelEffectCancelTasks.schedule != null)) {
                cancelEffectCancelTasks.schedule.cancel(false);
            }
        }

        return clonez;
    }

    public void cancelEffect(MapleStatEffect effect, boolean overwrite, long startTime) {
        if (effect == null) {
            if (ServerProperties.ShowPacket()) {
                log.info("取消技能BUFF: cancelEffect effect == null - 1");
            }
            return;
        }
        cancelEffect(effect, overwrite, startTime, effect.getStatups());
    }

    public void cancelEffect(MapleStatEffect effect, boolean overwrite, long startTime, List<Pair<MapleBuffStat, Integer>> statups) {
        if (effect == null) {
            if (ServerProperties.ShowPacket()) {
                log.info("取消技能BUFF: cancelEffect effect == null - 2");
            }
            return;
        }
        List<MapleBuffStat> buffstats;
        if (!overwrite) {
            buffstats = getBuffStats(effect, startTime);
        } else {
            buffstats = new ArrayList<MapleBuffStat>(statups.size());
            for (Pair<MapleBuffStat, Integer> statup : statups) {
                buffstats.add(statup.getLeft());
            }
        }
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("取消技能BUFF: - buffstats.size() ").append(buffstats.size()).toString());
        }
        if (buffstats.size() <= 0) {
            if (effect.is黑暗灵气()) {
                cancelEffectFromBuffStat(MapleBuffStat.黑暗灵气);
            } else if (effect.is黄色灵气()) {
                cancelEffectFromBuffStat(MapleBuffStat.黄色灵气);
            } else if (effect.is蓝色灵气()) {
                cancelEffectFromBuffStat(MapleBuffStat.蓝色灵气);
            }
            return;
        }
        if (ServerProperties.ShowPacket()) {
            log.info("开始取消技能BUFF: - 1");
        }
        if ((effect.is终极无限()) && (getBuffedValue(MapleBuffStat.终极无限) != null)) {
            int duration = Math.max(effect.getDuration(), effect.alchemistModifyVal(this, effect.getDuration(), false));
            long start = getBuffedStarttime(MapleBuffStat.终极无限).longValue();
            duration += (int) (start - System.currentTimeMillis());
            if (duration > 0) {
                int neworbcount = getBuffedValue(MapleBuffStat.终极无限).intValue() + effect.getDamage();
                List stat = Collections.singletonList(new Pair(MapleBuffStat.终极无限, Integer.valueOf(neworbcount)));
                setBuffedValue(MapleBuffStat.终极无限, neworbcount);
                this.client.getSession().write(MaplePacketCreator.giveBuff(effect.getSourceId(), duration, stat, effect));
                addHP((int) (effect.getHpR() * this.stats.getCurrentMaxHp()));
                addMP((int) (effect.getMpR() * this.stats.getCurrentMaxMp(getJob())));
                setSchedule(MapleBuffStat.终极无限, BuffTimer.getInstance().schedule(new CancelEffectAction(this, effect, start, stat), effect.alchemistModifyVal(this, 4000, false)));
                return;
            }
        }
        boolean clonez = deregisterBuffStats(buffstats);
        if (effect.is时空门()) {
            if (ServerProperties.ShowPacket()) {
                log.info("开始取消技能BUFF: - 时空门");
            }
            if (!getDoors().isEmpty()) {
                removeDoor();
                silentPartyUpdate();
            }
        } else if (effect.is机械传送门()) {
            if (ServerProperties.ShowPacket()) {
                log.info("开始取消技能BUFF: - 机械传送门");
            }
            if (!getMechDoors().isEmpty()) {
                removeMechDoor();
            }
        } else if (effect.is骑兽技能_()) {
            if (ServerProperties.ShowPacket()) {
                log.info("开始取消技能BUFF: - 骑兽技能_");
            }
            getMount().cancelSchedule();
        } else if (effect.is机械骑兽()) {
            if (ServerProperties.ShowPacket()) {
                log.info("开始取消技能BUFF: - 机械骑兽");
            }
            cancelEffectFromBuffStat(MapleBuffStat.金属机甲);
        } else if (effect.is矛连击强化()) {
            this.combo = 0;
        }

        cancelPlayerBuffs(buffstats, overwrite);
        if ((!overwrite)
                && (effect.is隐藏术()) && (this.client.getChannelServer().getPlayerStorage().getCharacterById(getId()) != null)) {
            this.map.broadcastMessage(this, MaplePacketCreator.spawnPlayerMapobject(this), false);
            for (MaplePet pet : this.pets) {
                if (pet.getSummoned()) {
                    this.map.broadcastMessage(this, PetPacket.showPet(this, pet, false, false), false);
                }
            }
            for (WeakReference chr : this.clones) {
                if (chr.get() != null) {
                    this.map.broadcastMessage((MapleCharacter) chr.get(), MaplePacketCreator.spawnPlayerMapobject((MapleCharacter) chr.get()), false);
                }
            }
        }

        if ((effect.getSourceId() == 35121013) && (!overwrite)) {
            if (ServerProperties.ShowPacket()) {
                log.info("开始技能BUFF: - 机械师.金属机甲_重机枪_4转");
            }
            SkillFactory.getSkill(35121005).getEffect(getTotalSkillLevel(35121005)).applyTo(this);
        }
        if (!clonez) {
            for (WeakReference chr : this.clones) {
                if (chr.get() != null) {
                    ((MapleCharacter) chr.get()).cancelEffect(effect, overwrite, startTime);
                }
            }
        }
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("BUFF状态已经取消. Effect: ").append(effect.getSourceId()).toString());
        }
    }

    public void cancelBuffStats(MapleBuffStat[] stat) {
        List buffStatList = Arrays.asList(stat);
        deregisterBuffStats(buffStatList);
        cancelPlayerBuffs(buffStatList, false);
    }

    public void cancelEffectFromBuffStat(MapleBuffStat stat) {
        if (this.effects.get(stat) != null) {
            cancelEffect(((MapleBuffStatValueHolder) this.effects.get(stat)).effect, false, -1L);
        }
    }

    public void cancelEffectFromBuffStat(MapleBuffStat stat, int from) {
        if ((this.effects.get(stat) != null) && (((MapleBuffStatValueHolder) this.effects.get(stat)).cid == from)) {
            cancelEffect(((MapleBuffStatValueHolder) this.effects.get(stat)).effect, false, -1L);
        }
    }

    private void cancelPlayerBuffs(List<MapleBuffStat> buffstats, boolean overwrite) {
        boolean write = (this.client != null) && (this.client.getChannelServer() != null) && (this.client.getChannelServer().getPlayerStorage().getCharacterById(getId()) != null);
        if (buffstats.contains(MapleBuffStat.导航辅助)) {
            this.client.getSession().write(MaplePacketCreator.cancelHoming());
        } else {
            if (overwrite) {
                List z = new ArrayList();
                for (MapleBuffStat s : buffstats) {
                    if (s.canStack()) {
                        z.add(s);
                    }
                }
                if (z.size() > 0) {
                    buffstats = z;
                } else {
                    return;
                }
            } else if (write) {
                this.stats.recalcLocalStats(this);
            }
            this.client.getSession().write(MaplePacketCreator.cancelBuff(buffstats));
            this.map.broadcastMessage(this, MaplePacketCreator.cancelForeignBuff(getId(), buffstats), false);
        }
    }

    public void dispel() {
        if (!isHidden()) {
            LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<MapleBuffStatValueHolder>(this.effects.values());
            for (MapleBuffStatValueHolder mbsvh : allBuffs) {
                if ((mbsvh.effect.isSkill()) && (mbsvh.schedule != null) && (!mbsvh.effect.isMorph()) && (!mbsvh.effect.isGmBuff()) && (!mbsvh.effect.is骑兽技能()) && (!mbsvh.effect.isMechChange()) && (!mbsvh.effect.is能量获得()) && (!mbsvh.effect.is矛连击强化())) {
                    cancelEffect(mbsvh.effect, false, mbsvh.startTime);
                }
            }
        }
    }

    public void dispelSkill(int skillid) {
        LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<MapleBuffStatValueHolder>(this.effects.values());
        for (MapleBuffStatValueHolder mbsvh : allBuffs) {
            if ((mbsvh.effect.isSkill()) && (mbsvh.effect.getSourceId() == skillid)) {
                cancelEffect(mbsvh.effect, false, mbsvh.startTime);
                break;
            }
        }
    }

    public void dispelSummons() {
        LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<MapleBuffStatValueHolder>(this.effects.values());
        for (MapleBuffStatValueHolder mbsvh : allBuffs) {
            if (mbsvh.effect.getSummonMovementType() != null) {
                cancelEffect(mbsvh.effect, false, mbsvh.startTime);
            }
        }
    }

    public void dispelBuff(int skillid) {
        LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<MapleBuffStatValueHolder>(this.effects.values());
        for (MapleBuffStatValueHolder mbsvh : allBuffs) {
            if (mbsvh.effect.getSourceId() == skillid) {
                cancelEffect(mbsvh.effect, false, mbsvh.startTime);
                break;
            }
        }
    }

    public void cancelAllBuffs_() {
        this.effects.clear();
    }

    public void cancelAllBuffs() {
        LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<MapleBuffStatValueHolder>(this.effects.values());
        for (MapleBuffStatValueHolder mbsvh : allBuffs) {
            cancelEffect(mbsvh.effect, false, mbsvh.startTime);
        }
    }

    public void cancelMorphs() {
        LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<MapleBuffStatValueHolder>(this.effects.values());
        for (MapleBuffStatValueHolder mbsvh : allBuffs) {
            switch (mbsvh.effect.getSourceId()) {
                case 5811005:
                case 5821003:
                case 13111005:
                    return;
            }
            if (mbsvh.effect.isMorph()) {
                cancelEffect(mbsvh.effect, false, mbsvh.startTime);
                continue;
            }
        }
    }

    public int getMorphState() {
        LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<MapleBuffStatValueHolder>(this.effects.values());
        for (MapleBuffStatValueHolder mbsvh : allBuffs) {
            if (mbsvh.effect.isMorph()) {
                return mbsvh.effect.getSourceId();
            }
        }
        return -1;
    }

    public void silentGiveBuffs(List<PlayerBuffValueHolder> buffs) {
        if (buffs == null) {
            return;
        }
        for (PlayerBuffValueHolder mbsvh : buffs) {
            mbsvh.effect.silentApplyBuff(this, mbsvh.startTime, mbsvh.localDuration, mbsvh.statup, mbsvh.cid);
        }
    }

    public List<PlayerBuffValueHolder> getAllBuffs() {
        List<PlayerBuffValueHolder> ret = new ArrayList<PlayerBuffValueHolder>();
        Map<Pair<Integer, Byte>, Integer> alreadyDone = new HashMap<Pair<Integer, Byte>, Integer>();
        LinkedList<Entry<MapleBuffStat, MapleBuffStatValueHolder>> allBuffs = new LinkedList<Entry<MapleBuffStat, MapleBuffStatValueHolder>>(this.effects.entrySet());
        for (Entry<MapleBuffStat, MapleBuffStatValueHolder> mbsvh : allBuffs) {
            Pair<Integer, Byte> key = new Pair<Integer, Byte>(mbsvh.getValue().effect.getSourceId(), mbsvh.getValue().effect.getLevel());
            if (alreadyDone.containsKey(key)) {
                ret.get(alreadyDone.get(key)).statup.add(new Pair(mbsvh.getKey(), mbsvh.getValue().value));
            } else {
                alreadyDone.put(key, ret.size());
                ArrayList list = new ArrayList();
                list.add(new Pair(mbsvh.getKey(), mbsvh.getValue().value));
                ret.add(new PlayerBuffValueHolder(mbsvh.getValue().startTime, mbsvh.getValue().effect, list, mbsvh.getValue().localDuration, mbsvh.getValue().cid));
            }
        }
        return ret;
    }

    public void cancelMagicDoor() {
        LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<MapleBuffStatValueHolder>(this.effects.values());
        for (MapleBuffStatValueHolder mbsvh : allBuffs) {
            if (mbsvh.effect.is时空门()) {
                cancelEffect(mbsvh.effect, false, mbsvh.startTime);
                break;
            }
        }
    }

    public int getSkillLevel(int skillid) {
        return getSkillLevel(SkillFactory.getSkill(skillid));
    }

    public int getTotalSkillLevel(int skillid) {
        return getTotalSkillLevel(SkillFactory.getSkill(skillid));
    }

    public void handleEnergyCharge(int skillid, int targets) {
        Skill echskill = SkillFactory.getSkill(skillid);
        int skilllevel = getTotalSkillLevel(echskill);
        if (skilllevel > 0) {
            MapleStatEffect echeff = echskill.getEffect(skilllevel);
            if (targets > 0) {
                if (getBuffedValue(MapleBuffStat.能量获得) == null) {
                    echeff.applyEnergyBuff(this, true);
                } else {
                    Integer energyLevel = getBuffedValue(MapleBuffStat.能量获得);

                    if (energyLevel.intValue() < 10000) {
                        energyLevel = Integer.valueOf(energyLevel.intValue() + echeff.getX() * targets);
                        this.client.getSession().write(MaplePacketCreator.showOwnBuffEffect(skillid, 2, getLevel(), skilllevel));
                        this.map.broadcastMessage(this, MaplePacketCreator.showBuffeffect(this.id, skillid, 2, getLevel(), skilllevel), false);
                        if (energyLevel.intValue() >= 10000) {
                            energyLevel = Integer.valueOf(10000);
                        }
                        this.client.getSession().write(MaplePacketCreator.giveEnergyChargeTest(energyLevel.intValue(), echeff.getDuration() / 1000));
                        setBuffedValue(MapleBuffStat.能量获得, Integer.valueOf(energyLevel.intValue()).intValue());
                    } else if (energyLevel.intValue() == 10000) {
                        echeff.applyEnergyBuff(this, false);
                        setBuffedValue(MapleBuffStat.能量获得, Integer.valueOf(10001).intValue());
                    }
                }
            }
        }
    }

    public void handleBattleshipHP(int damage) {
        if (damage < 0) {
            MapleStatEffect effect = getStatForBuff(MapleBuffStat.骑兽技能);
            if ((effect != null) && (effect.getSourceId() == 5921006)) {
                this.battleshipHP += damage;
                this.client.getSession().write(MaplePacketCreator.skillCooldown(5221999, this.battleshipHP / 10));
                if (this.battleshipHP <= 0) {
                    this.battleshipHP = 0;
                    this.client.getSession().write(MaplePacketCreator.skillCooldown(5921006, effect.getCooldown()));
                    addCooldown(5921006, System.currentTimeMillis(), effect.getCooldown() * 1000);
                    cancelEffectFromBuffStat(MapleBuffStat.骑兽技能);
                }
            }
        }
    }

    public void handleOrbgain() {
        int orbcount = getBuffedValue(MapleBuffStat.斗气集中).intValue();
        Skill combos;
        Skill advcombo;
        switch (getJob()) {
            case 1110:
            case 1111:
            case 1112:
                combos = SkillFactory.getSkill(11111001);
                advcombo = SkillFactory.getSkill(11110005);
                break;
            default:
                combos = SkillFactory.getSkill(1111002);
                advcombo = SkillFactory.getSkill(1120003);
        }

        MapleStatEffect ceffect = null;
        int advComboSkillLevel = getTotalSkillLevel(advcombo);
        if (advComboSkillLevel > 0) {
            ceffect = advcombo.getEffect(advComboSkillLevel);
        } else if (getSkillLevel(combos) > 0) {
            ceffect = combos.getEffect(getTotalSkillLevel(combos));
        } else {
            return;
        }
        if (orbcount < ceffect.getX() + 1) {
            int neworbcount = orbcount + 1;
            if ((advComboSkillLevel > 0) && (ceffect.makeChanceResult())
                    && (neworbcount < ceffect.getX() + 1)) {
                neworbcount++;
            }

            List stat = Collections.singletonList(new Pair(MapleBuffStat.斗气集中, Integer.valueOf(neworbcount)));
            setBuffedValue(MapleBuffStat.斗气集中, neworbcount);
            int duration = ceffect.getDuration();
            duration += (int) (getBuffedStarttime(MapleBuffStat.斗气集中).longValue() - System.currentTimeMillis());
            this.client.getSession().write(MaplePacketCreator.giveBuff(combos.getId(), duration, stat, ceffect));
            this.map.broadcastMessage(this, MaplePacketCreator.giveForeignBuff(getId(), stat, ceffect), false);
        }
    }

    public void handleOrbconsume(int howmany) {
        Skill combos;
        switch (getJob()) {
            case 1110:
            case 1111:
            case 1112:
                combos = SkillFactory.getSkill(11111001);
                break;
            default:
                combos = SkillFactory.getSkill(1111002);
        }

        if (getSkillLevel(combos) <= 0) {
            return;
        }
        MapleStatEffect ceffect = getStatForBuff(MapleBuffStat.斗气集中);
        if (ceffect == null) {
            return;
        }
        List stat = Collections.singletonList(new Pair(MapleBuffStat.斗气集中, Integer.valueOf(Math.max(1, getBuffedValue(MapleBuffStat.斗气集中).intValue() - howmany))));
        setBuffedValue(MapleBuffStat.斗气集中, Math.max(1, getBuffedValue(MapleBuffStat.斗气集中).intValue() - howmany));
        int duration = ceffect.getDuration();
        duration += (int) (getBuffedStarttime(MapleBuffStat.斗气集中).longValue() - System.currentTimeMillis());
        this.client.getSession().write(MaplePacketCreator.giveBuff(combos.getId(), duration, stat, ceffect));
        this.map.broadcastMessage(this, MaplePacketCreator.giveForeignBuff(getId(), stat, ceffect), false);
    }

    public void silentEnforceMaxHpMp() {
        this.stats.setMp(this.stats.getMp(), this);
        this.stats.setHp(this.stats.getHp(), true, this);
    }

    public void enforceMaxHpMp() {
        Map statups = new EnumMap(MapleStat.class);
        if (this.stats.getMp() > this.stats.getCurrentMaxMp(getJob())) {
            this.stats.setMp(this.stats.getMp(), this);
            statups.put(MapleStat.MP, Integer.valueOf(this.stats.getMp()));
        }
        if (this.stats.getHp() > this.stats.getCurrentMaxHp()) {
            this.stats.setHp(this.stats.getHp(), this);
            statups.put(MapleStat.HP, Integer.valueOf(this.stats.getHp()));
        }
        if (statups.size() > 0) {
            this.client.getSession().write(MaplePacketCreator.updatePlayerStats(statups, this));
        }
    }

    public MapleMap getMap() {
        return this.map;
    }

    public MonsterBook getMonsterBook() {
        return this.monsterbook;
    }

    public void setMap(MapleMap newmap) {
        this.map = newmap;
    }

    public void setMap(int PmapId) {
        this.mapid = PmapId;
    }

    public int getMapId() {
        if (this.map != null) {
            return this.map.getId();
        }
        return this.mapid;
    }

    public byte getInitialSpawnpoint() {
        return this.initialSpawnPoint;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getBlessOfFairyOrigin() {
        return this.BlessOfFairy_Origin;
    }

    public String getBlessOfEmpressOrigin() {
        return this.BlessOfEmpress_Origin;
    }

    public short getLevel() {
        return this.level;
    }

    public int getFame() {
        return this.fame;
    }

    public int getFallCounter() {
        return this.fallcounter;
    }

    public MapleClient getClient() {
        return this.client;
    }

    public void setClient(MapleClient client) {
        this.client = client;
    }

    public int getExp() {
        return this.exp;
    }

    public short getRemainingAp() {
        return this.remainingAp;
    }

    public int getRemainingSp() {
        return this.remainingSp[GameConstants.getSkillBook(this.job)];
    }

    public int getRemainingSp(int skillbook) {
        return this.remainingSp[skillbook];
    }

    public int[] getRemainingSps() {
        return this.remainingSp;
    }

    public int getRemainingSpSize() {
        int ret = 0;
        for (int i = 0; i < this.remainingSp.length; i++) {
            if (this.remainingSp[i] > 0) {
                ret++;
            }
        }
        return ret;
    }

    public short getHpApUsed() {
        return this.hpApUsed;
    }

    public boolean isHidden() {
        return getBuffSource(MapleBuffStat.隐身术) / 1000000 == 9;
    }

    public void setHpApUsed(short hpApUsed) {
        this.hpApUsed = hpApUsed;
    }

    public byte getSkinColor() {
        return this.skinColor;
    }

    public void setSkinColor(byte skinColor) {
        this.skinColor = skinColor;
    }

    public short getJob() {
        return this.job;
    }

    public byte getGender() {
        return this.gender;
    }

    public int getHair() {
        return this.hair;
    }

    public int getFace() {
        return this.face;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public void setHair(int hair) {
        this.hair = hair;
    }

    public void setFace(int face) {
        this.face = face;
    }

    public void setFame(int fame) {
        this.fame = fame;
    }

    public void setFallCounter(int fallcounter) {
        this.fallcounter = fallcounter;
    }

    public Point getOldPosition() {
        return this.old;
    }

    public void setOldPosition(Point x) {
        this.old = x;
    }

    public void setRemainingAp(short remainingAp) {
        this.remainingAp = remainingAp;
    }

    public void setRemainingSp(int remainingSp) {
        this.remainingSp[GameConstants.getSkillBook(this.job)] = remainingSp;
    }

    public void setRemainingSp(int remainingSp, int skillbook) {
        this.remainingSp[skillbook] = remainingSp;
    }

    public void setGender(byte gender) {
        this.gender = gender;
    }

    public void setInvincible(boolean invinc) {
        this.invincible = invinc;
    }

    public boolean isInvincible() {
        return this.invincible;
    }

    public CheatTracker getCheatTracker() {
        return this.anticheat;
    }

    public MapleLieDetector getAntiMacro() {
        return this.antiMacro;
    }

    public BuddyList getBuddylist() {
        return this.buddylist;
    }

    public void addFame(int famechange) {
        this.fame += famechange;
        getTrait(MapleTrait.MapleTraitType.charm).addLocalExp(famechange);
        if (this.fame >= 50) {
            finishAchievement(7);
        }
    }

    public void updateFame() {
        updateSingleStat(MapleStat.人气, this.fame);
    }

    public void gainFame(int famechange, boolean show) {
        this.fame += famechange;
        updateSingleStat(MapleStat.人气, this.fame);
        if ((show) && (famechange != 0)) {
            this.client.getSession().write(MaplePacketCreator.getShowFameGain(famechange));
        }
    }

    public void changeMapBanish(int mapid, String portal, String msg) {
        dropMessage(5, msg);
        MapleMap maps = this.client.getChannelServer().getMapFactory().getMap(mapid);
        changeMap(maps, maps.getPortal(portal));
    }

    public void changeMap(MapleMap to, Point pos) {
        changeMapInternal(to, pos, MaplePacketCreator.getWarpToMap(to, 128, this), null);
    }

    public void changeMap(MapleMap to) {
        changeMapInternal(to, to.getPortal(0).getPosition(), MaplePacketCreator.getWarpToMap(to, 0, this), to.getPortal(0));
    }

    public void changeMap(MapleMap to, MaplePortal pto) {
        changeMapInternal(to, pto.getPosition(), MaplePacketCreator.getWarpToMap(to, pto.getId(), this), null);
    }

    public void changeMapPortal(MapleMap to, MaplePortal pto) {
        changeMapInternal(to, pto.getPosition(), MaplePacketCreator.getWarpToMap(to, pto.getId(), this), pto);
    }

    private void changeMapInternal(MapleMap to, Point pos, byte[] warpPacket, MaplePortal pto) {
        if (to == null) {
            return;
        }
        if (getAntiMacro().inProgress()) {
            dropMessage(5, "被使用测谎仪时无法操作。");
            return;
        }
        int nowmapid = this.map.getId();
        if (this.eventInstance != null) {
            this.eventInstance.changedMap(this, to.getId());
        }
        boolean pyramid = this.pyramidSubway != null;
        if (this.map.getId() == nowmapid) {
            this.client.getSession().write(warpPacket);
            boolean shouldChange = (!isClone()) && (this.client.getChannelServer().getPlayerStorage().getCharacterById(getId()) != null);
            boolean shouldState = this.map.getId() == to.getId();
            if ((shouldChange) && (shouldState)) {
                to.setCheckStates(false);
            }
            this.map.removePlayer(this);
            if (shouldChange) {
                this.map = to;
                setPosition(pos);
                to.addPlayer(this);
                this.stats.relocHeal(this);
                if (shouldState) {
                    to.setCheckStates(true);
                }
            }
        }
        if ((pyramid) && (this.pyramidSubway != null)) {
            this.pyramidSubway.onChangeMap(this, to.getId());
        }
    }

    public void cancelChallenge() {
        if ((this.challenge != 0) && (this.client.getChannelServer() != null)) {
            MapleCharacter chr = this.client.getChannelServer().getPlayerStorage().getCharacterById(this.challenge);
            if (chr != null) {
                chr.dropMessage(6, new StringBuilder().append(getName()).append(" 拒绝了你的请求.").toString());
                chr.setChallenge(0);
            }
            dropMessage(6, "Denied the challenge.");
            this.challenge = 0;
        }
    }

    public void leaveMap(MapleMap map) {
        this.controlledLock.writeLock().lock();
        this.visibleMapObjectsLock.writeLock().lock();
        try {
            for (MapleMonster mons : this.controlled) {
                if (mons != null) {
                    mons.setController(null);
                    mons.setControllerHasAggro(false);
                    map.updateMonsterController(mons);
                }
            }
            this.controlled.clear();
            this.visibleMapObjects.clear();
        } finally {
            this.controlledLock.writeLock().unlock();
            this.visibleMapObjectsLock.writeLock().unlock();
        }
        if (this.chair != 0) {
            this.chair = 0;
        }
        clearLinkMid();
        cancelFishingTask();
        cancelChallenge();
        if (getBattle() != null) {
            getBattle().forfeit(this, true);
        }
        if (!getMechDoors().isEmpty()) {
            removeMechDoor();
        }
        cancelMapTimeLimitTask();
        if (getTrade() != null) {
            MapleTrade.cancelTrade(getTrade(), this.client, this);
        }
    }

    public void changeJob(int newJob) {
        try {
            cancelEffectFromBuffStat(MapleBuffStat.影分身);
            this.job = (short) newJob;
            updateSingleStat(MapleStat.职业, newJob);
            if (!GameConstants.is新手职业(newJob)) {
                if ((GameConstants.is龙神(newJob)) || (GameConstants.is反抗者(newJob)) || (GameConstants.is双弩精灵(newJob)) || (GameConstants.is幻影(newJob)) || (GameConstants.is米哈尔(newJob))) {
                    int changeSp = (newJob == 2200) || (newJob == 2210) || (newJob == 2211) || (newJob == 2213) ? 3 : 5;
                    if ((GameConstants.is反抗者(this.job)) && (newJob != 3100) && (newJob != 3200) && (newJob != 3300) && (newJob != 3500)) {
                        changeSp = 3;
                    }
                    if (GameConstants.is幻影(this.job)) {
                        return;
                    }
                    this.remainingSp[GameConstants.getSkillBook(newJob)] += changeSp;
                    this.client.getSession().write(UIPacket.getSPMsg((byte) changeSp, (short) newJob));
                } else {
                    this.remainingSp[GameConstants.getSkillBook(newJob)] += 1;
                    if (newJob % 10 >= 2) {
                        this.remainingSp[GameConstants.getSkillBook(newJob)] += 2;
                    }
                }
                if ((newJob % 10 >= 1) && (this.level >= 70)) {
                    this.remainingAp = (short) (this.remainingAp + 5);
                    updateSingleStat(MapleStat.AVAILABLEAP, this.remainingAp);

                    Skill skil = SkillFactory.getSkill(PlayerStats.getSkillByJob(1007, getJob()));
                    if ((skil != null) && (getSkillLevel(skil) <= 0)) {
                        dropMessage(-1, "恭喜你获得锻造技能。");
                        changeSkillLevel(skil, skil.getMaxLevel(), (byte) skil.getMaxLevel());
                    }
                }
                if (!isGM()) {
                    resetStatsByJob(true);
                    if (!GameConstants.is龙神(newJob)) {
                        if (getLevel() > (newJob == 200 ? 8 : 10)) {
                            if ((newJob % 100 == 0) && (newJob % 1000 / 100 > 0)) {
                                this.remainingSp[GameConstants.getSkillBook(newJob)] += 3 * (getLevel() - (newJob == 200 ? 8 : 10));
                            }
                        }
                    } else if (newJob == 2200) {
                        MapleQuest.getInstance(22100).forceStart(this, 0, null);
                        MapleQuest.getInstance(22100).forceComplete(this, 0);
                        expandInventory((byte) 1, 4);
                        expandInventory((byte) 2, 4);
                        expandInventory((byte) 3, 4);
                        expandInventory((byte) 4, 4);
                        this.client.getSession().write(MaplePacketCreator.getEvanTutorial("UI/tutorial/evan/14/0"));
                        dropMessage(5, "孵化器里的蛋中孵化出了幼龙，获得了可以提升龙的技能的3点SP，幼龙好像想说话。点击幼龙，和它说话吧！");
                    }
                }
                this.client.getSession().write(MaplePacketCreator.updateSp(this, false, false));
            }

            int maxhp = this.stats.getMaxHp();
            int maxmp = this.stats.getMaxMp();

            switch (this.job) {
                case 100:
                case 1100:
                case 2100:
                case 3200:
                    maxhp += Randomizer.rand(200, 250);
                    break;
                case 3100:
                    maxhp += Randomizer.rand(200, 250);
                    break;
                case 3110:
                    maxhp += Randomizer.rand(300, 350);
                    break;
                case 200:
                case 2200:
                case 2210:
                    maxmp += Randomizer.rand(100, 150);
                    break;
                case 300:
                case 400:
                case 500:
                case 501:
                case 509:
                case 2300:
                case 3300:
                case 3500:
                    maxhp += Randomizer.rand(100, 150);
                    maxmp += Randomizer.rand(25, 50);
                    break;
                case 110:
                case 120:
                case 130:
                case 1110:
                case 2110:
                case 3210:
                case 5110:
                    maxhp += Randomizer.rand(300, 350);
                    break;
                case 210:
                case 220:
                case 230:
                    maxmp += Randomizer.rand(400, 450);
                    break;
                case 310:
                case 320:
                case 410:
                case 420:
                case 430:
                case 510:
                case 520:
                case 530:
                case 570:
                case 580:
                case 590:
                case 1310:
                case 1410:
                case 2310:
                case 2410:
                case 3310:
                case 3510:
                    maxhp += Randomizer.rand(200, 250);
                    maxhp += Randomizer.rand(150, 200);
                    break;
                case 800:
                case 900:
                    maxhp += 99999;
                    maxmp += 99999;
            }

            if (maxhp >= 99999) {
                maxhp = 99999;
            }
            if (maxmp >= 99999) {
                maxmp = 99999;
            }
            if (GameConstants.is恶魔猎手(this.job)) {
                maxmp = GameConstants.getMPByJob(this.job);
            }
            this.stats.setInfo(maxhp, maxmp, maxhp, maxmp);
            Map statup = new EnumMap(MapleStat.class);
            statup.put(MapleStat.MAXHP, Integer.valueOf(maxhp));
            statup.put(MapleStat.MAXMP, Integer.valueOf(maxmp));
            statup.put(MapleStat.HP, Integer.valueOf(maxhp));
            statup.put(MapleStat.MP, Integer.valueOf(maxmp));
            this.stats.recalcLocalStats(this);
            this.client.getSession().write(MaplePacketCreator.updatePlayerStats(statup, this));
            this.map.broadcastMessage(this, MaplePacketCreator.showForeignEffect(getId(), 12), false);
            this.map.broadcastMessage(this, MaplePacketCreator.updateCharLook(this), false);
            silentPartyUpdate();
            guildUpdate();
            familyUpdate();
            sidekickUpdate();
            if (this.dragon != null) {
                this.map.broadcastMessage(MaplePacketCreator.removeDragon(this.id));
                this.dragon = null;
            }
            baseSkills();
            if ((newJob >= 2200) && (newJob <= 2218)) {
                if (getBuffedValue(MapleBuffStat.骑兽技能) != null) {
                    cancelBuffStats(new MapleBuffStat[]{MapleBuffStat.骑兽技能});
                }
                makeDragon();
            }
        } catch (Exception e) {
            FileoutputUtil.outputFileError("log\\Script_Except.log", e);
        }
    }

    public void baseSkills() {
        Iterator i$;
        if (GameConstants.getJobNumber(this.job) >= 3) {
            List baseSkills = SkillFactory.getSkillsByJob(this.job);
            if (baseSkills != null) {
                for (i$ = baseSkills.iterator(); i$.hasNext(); ) {
                    int i = ((Integer) i$.next()).intValue();
                    Skill skil = SkillFactory.getSkill(i);
                    if ((skil != null) && (!skil.isInvisible()) && (skil.isFourthJob()) && (getSkillLevel(skil) <= 0) && (getMasterLevel(skil) <= 0) && (skil.getMasterLevel() > 0)) {
                        changeSkillLevel(skil, 0, (byte) skil.getMasterLevel());
                    } else if ((skil != null) && (skil.getName() != null) && (skil.getName().contains("冒险岛勇士")) && (getSkillLevel(skil) <= 0) && (getMasterLevel(skil) <= 0)) {
                        changeSkillLevel(skil, 0, (byte) 10);
                    }

                }

            }

        }

        if ((GameConstants.is恶魔猎手(this.job)) && (this.level >= 120)
                && (haveItem(1099001))) {
            removeItem(1099001);
            MapleInventoryManipulator.addById(this.client, 1099004, (short) 1, new StringBuilder().append("系统赠送 时间: ").append(FileoutputUtil.CurrentReadable_Date()).toString());
            if (!isGM()) {
                World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append("[系统公告] 恭喜恶魔猎手职业玩家 ").append(getName()).append(" 等级达到 ").append(this.level).append(" 级，系统赠送他(她)120级盾牌-极限军团盾。").toString()));
            }

        }

        if ((this.job >= 3300) && (this.job <= 3312)) {
            Skill skil = SkillFactory.getSkill(30001061);
            if ((skil != null) && (getSkillLevel(skil) <= 0)) {
                changeSkillLevel(skil, skil.getMaxLevel(), (byte) skil.getMaxLevel());
            }
        }
        if ((GameConstants.is火炮手(this.job)) && (this.level >= 10)) {
            Skill skil = SkillFactory.getSkill(110);
            if ((skil != null) && (getSkillLevel(skil) <= 0)) {
                changeSkillLevel(skil, 1, (byte) 1);
            }
        }
        if ((GameConstants.is恶魔猎手(this.job)) && (this.level >= 10)) {
            int[] ss = {30010110, 30010111, 30010112, 30011159, 30010185, 30010183, 30010184, 30010186};
            for (int i : ss) {
                Skill skil = SkillFactory.getSkill(i);
                if ((skil != null) && (getSkillLevel(skil) <= 0)) {
                    changeSkillLevel(skil, 1, (byte) 1);
                }
            }
        }
        if ((GameConstants.is双弩精灵(this.job)) && (this.level >= 10)) {
            int[] ss = {20021110, 20020111, 20020112, 20020109};
            for (int i : ss) {
                Skill skil = SkillFactory.getSkill(i);
                if ((skil != null) && (getSkillLevel(skil) <= 0)) {
                    changeSkillLevel(skil, 1, (byte) 1);
                }
            }
        }
        if ((GameConstants.is龙的传人(this.job)) && (this.level >= 10)) {
            Skill skil = SkillFactory.getSkill(228);
            if ((skil != null) && (getSkillLevel(skil) <= 0)) {
                changeSkillLevel(SkillFactory.getSkill(228), 1, (byte) 1);
            }
        }
        if ((GameConstants.is幻影(this.job)) && (this.level >= 10)) {
            int[] ss = {20031203, 20030204, 20031205, 20030206, 20031207, 20031208, 20031209};
            for (int i : ss) {
                Skill skil = SkillFactory.getSkill(i);
                if ((skil != null) && (getSkillLevel(skil) <= 0)) {
                    changeSkillLevel(skil, 1, (byte) 1);
                }
            }
        }
    }

    public void makeDragon() {
        this.dragon = new MapleDragon(this);
        this.map.broadcastMessage(MaplePacketCreator.spawnDragon(this.dragon));
    }

    public MapleDragon getDragon() {
        return this.dragon;
    }

    public void gainAp(short ap) {
        this.remainingAp = (short) (this.remainingAp + ap);
        updateSingleStat(MapleStat.AVAILABLEAP, this.remainingAp);
    }

    public void gainSP(int sp) {
        this.remainingSp[GameConstants.getSkillBook(this.job)] += sp;
        this.client.getSession().write(MaplePacketCreator.updateSp(this, false));
        this.client.getSession().write(UIPacket.getSPMsg((byte) sp, this.job));
    }

    public void gainSP(int sp, int skillbook) {
        this.remainingSp[skillbook] += sp;
        this.client.getSession().write(MaplePacketCreator.updateSp(this, false));
        this.client.getSession().write(UIPacket.getSPMsg((byte) sp, (short) 0));
    }

    public void resetSP(int sp) {
        for (int i = 0; i < this.remainingSp.length; i++) {
            this.remainingSp[i] = sp;
        }
        this.client.getSession().write(MaplePacketCreator.updateSp(this, false));
    }

    public void resetLevel(int level) {
        for (int i = 0; i < this.remainingLevel.length; i++) {
            this.remainingLevel[i] = level;
        }
        this.client.getSession().write(MaplePacketCreator.updateLevel(this, false));
    }

    public void resetAPSP() {
        resetSP(0);
        gainAp((short) (-this.remainingAp));
    }

    public List<Integer> getProfessions() {
        List prof = new ArrayList();
        for (int i = 9200; i <= 9204; i++) {
            if (getProfessionLevel(i * 10000) > 0) {
                prof.add(i);
            }
        }
        return prof;
    }

    public byte getProfessionLevel(int id) {
        int ret = getSkillLevel(id);
        if (ret <= 0) {
            return 0;
        }
        return (byte) (ret >>> 24 & 0xFF);
    }

    public short getProfessionExp(int id) {
        int ret = getSkillLevel(id);
        if (ret <= 0) {
            return 0;
        }
        return (short) (ret & 0xFFFF);
    }

    public boolean addProfessionExp(int id, int expGain) {
        int ret = getProfessionLevel(id);
        if ((ret <= 0) || (ret >= 10)) {
            return false;
        }
        int newExp = getProfessionExp(id) + expGain;
        if (newExp >= GameConstants.getProfessionEXP(ret)) {
            changeProfessionLevelExp(id, ret + 1, newExp - GameConstants.getProfessionEXP(ret));
            int traitGain = (int) Math.pow(2.0D, ret + 1);
            switch (id) {
                case 92000000:
                    ((MapleTrait) this.traits.get(MapleTrait.MapleTraitType.sense)).addExp(traitGain, this);
                    break;
                case 92010000:
                    ((MapleTrait) this.traits.get(MapleTrait.MapleTraitType.will)).addExp(traitGain, this);
                    break;
                case 92020000:
                case 92030000:
                case 92040000:
                    ((MapleTrait) this.traits.get(MapleTrait.MapleTraitType.craft)).addExp(traitGain, this);
            }

            return true;
        }
        changeProfessionLevelExp(id, ret, newExp);
        return false;
    }

    public void changeProfessionLevelExp(int id, int level, int exp) {
        changeSkillLevel(SkillFactory.getSkill(id), ((level & 0xFF) << 24) + (exp & 0xFFFF), (byte) 10);
    }

    public void changeSkillLevel(Skill skill, int newLevel, byte newMasterlevel) {
        if (skill == null) {
            return;
        }
        changeSkillLevel(skill, newLevel, newMasterlevel, skill.isTimeLimited() ? System.currentTimeMillis() + 2592000000L : -1L);
    }

    public void changeSkillLevel(Skill skill, int newLevel, byte newMasterlevel, long expiration) {
        if ((skill == null) || ((!GameConstants.isApplicableSkill(skill.getId())) && (!GameConstants.isApplicableSkill_(skill.getId())))) {
            return;
        }
        this.client.getSession().write(MaplePacketCreator.updateSkill(skill.getId(), newLevel, newMasterlevel, expiration));
        if ((newLevel == 0) && (newMasterlevel == 0)) {
            if (this.skills.containsKey(skill)) {
                this.skills.remove(skill);
            } else {
                return;
            }
        } else {
            this.skills.put(skill, new SkillEntry(newLevel, newMasterlevel, expiration, 0));
        }
        this.changed_skills = true;
        if (GameConstants.isRecoveryIncSkill(skill.getId())) {
            this.stats.relocHeal(this);
        }
        if (skill.getId() < 80000000) {
            this.stats.recalcLocalStats(this);
        }
    }

    public void changeSkillLevel_Skip(Skill skill, int newLevel, byte newMasterlevel) {
        changeSkillLevel_Skip(skill, newLevel, newMasterlevel, false);
    }

    public void changeSkillLevel_Skip(Skill skill, int newLevel, byte newMasterlevel, boolean write) {
        if (skill == null) {
            return;
        }
        if (write) {
            this.client.getSession().write(MaplePacketCreator.updateSkill(skill.getId(), newLevel, newMasterlevel, -1L));
        }
        if ((newLevel == 0) && (newMasterlevel == 0)) {
            if (this.skills.containsKey(skill)) {
                this.skills.remove(skill);
            } else {
                return;
            }
        } else {
            this.skills.put(skill, new SkillEntry(newLevel, newMasterlevel, -1L, 0));
        }
    }

    public void changeTeachSkill(int skillId, int toChrId) {
        Skill skill = SkillFactory.getSkill(skillId);
        if (skill == null) {
            return;
        }
        this.client.getSession().write(MaplePacketCreator.updateSkill(skillId, toChrId, 1, -1L));
        this.skills.put(skill, new SkillEntry(1, (byte) 1, -1L, toChrId));
        this.changed_skills = true;
    }

    public void playerDead() {
        MapleStatEffect statss = getStatForBuff(MapleBuffStat.灵魂之石);
        if (statss != null) {
            dropMessage(5, "由于灵魂之石的效果发动，本次死亡经验您的经验值不会减少。");
            getStat().setHp(getStat().getMaxHp() / 100 * statss.getX(), this);
            setStance(0);

            cancelEffectFromBuffStat(MapleBuffStat.灵魂之石);
            return;
        }
        if (getEventInstance() != null) {
            getEventInstance().playerKilled(this);
        }
        cancelEffectFromBuffStat(MapleBuffStat.影分身);
        cancelEffectFromBuffStat(MapleBuffStat.变身术);
        cancelEffectFromBuffStat(MapleBuffStat.飞行骑乘);
        cancelEffectFromBuffStat(MapleBuffStat.骑兽技能);
        cancelEffectFromBuffStat(MapleBuffStat.金属机甲);
        cancelEffectFromBuffStat(MapleBuffStat.RECOVERY);
        cancelEffectFromBuffStat(MapleBuffStat.HP_BOOST);
        cancelEffectFromBuffStat(MapleBuffStat.MP_BOOST);
        cancelEffectFromBuffStat(MapleBuffStat.增强_MAXHP);
        cancelEffectFromBuffStat(MapleBuffStat.增强_MAXMP);
        cancelEffectFromBuffStat(MapleBuffStat.MAXHP);
        cancelEffectFromBuffStat(MapleBuffStat.MAXMP);
        cancelEffectFromBuffStat(MapleBuffStat.精神连接);
        dispelSummons();
        checkFollow();
        this.dotHP = 0;
        this.lastDOTTime = 0L;
        if ((!GameConstants.is新手职业(this.job)) && (!inPVP())) {
            int charms = getItemQuantity(5130000, false);
            if (charms > 0) {
                MapleInventoryManipulator.removeById(this.client, MapleInventoryType.CASH, 5130000, 1, true, false);
                charms--;
                if (charms > 0xFF) {
                    charms = 0xFF;
                }
                this.client.getSession().write(MTSCSPacket.useCharm((byte) charms, (byte) 0));
            } else {
                float diepercentage = 0.0F;
                int expforlevel = getNeededExp();
                if ((this.map.isTown()) || (FieldLimitType.RegularExpLoss.check(this.map.getFieldLimit()))) {
                    diepercentage = 0.01F;
                } else {
                    diepercentage = 0.1F - ((MapleTrait) this.traits.get(MapleTrait.MapleTraitType.charisma)).getLevel() / 20 / 100.0F;
                }
                int v10 = (int) (this.exp - (long) (expforlevel * diepercentage));
                if (v10 < 0) {
                    v10 = 0;
                }
                this.exp = v10;
            }
            updateSingleStat(MapleStat.经验, this.exp);
        }
        if (!this.stats.checkEquipDurabilitys(this, -100)) {
            dropMessage(5, "An item has run out of durability but has no inventory room to go to.");
        }
        if (this.pyramidSubway != null) {
            this.stats.setHp(50, this);
            this.pyramidSubway.fail(this);
        }
    }

    public void updatePartyMemberHP() {
        int channel;
        if ((this.party != null) && (this.client.getChannelServer() != null)) {
            channel = this.client.getChannel();
            for (MaplePartyCharacter partychar : this.party.getMembers()) {
                if ((partychar != null) && (partychar.getMapid() == getMapId()) && (partychar.getChannel() == channel)) {
                    MapleCharacter other = this.client.getChannelServer().getPlayerStorage().getCharacterByName(partychar.getName());
                    if (other != null) {
                        other.getClient().getSession().write(PartyPacket.updatePartyMemberHP(getId(), this.stats.getHp(), this.stats.getCurrentMaxHp()));
                    }
                }
            }
        }
    }

    public void receivePartyMemberHP() {
        if (this.party == null) {
            return;
        }
        int channel = this.client.getChannel();
        for (MaplePartyCharacter partychar : this.party.getMembers()) {
            if ((partychar != null) && (partychar.getMapid() == getMapId()) && (partychar.getChannel() == channel)) {
                MapleCharacter other = this.client.getChannelServer().getPlayerStorage().getCharacterByName(partychar.getName());
                if (other != null) {
                    this.client.getSession().write(PartyPacket.updatePartyMemberHP(other.getId(), other.getStat().getHp(), other.getStat().getCurrentMaxHp()));
                }
            }
        }
    }

    public void healHP(int delta) {
        addHP(delta);
        this.client.getSession().write(MaplePacketCreator.showOwnHpHealed(delta));
        getMap().broadcastMessage(this, MaplePacketCreator.showHpHealed(getId(), delta), false);
    }

    public void healMP(int delta) {
        addMP(delta);
        this.client.getSession().write(MaplePacketCreator.showOwnHpHealed(delta));
        getMap().broadcastMessage(this, MaplePacketCreator.showHpHealed(getId(), delta), false);
    }

    public void addHP(int delta) {
        if (this.stats.setHp(this.stats.getHp() + delta, this)) {
            updateSingleStat(MapleStat.HP, this.stats.getHp());
        }
    }

    public void addMP(int delta) {
        addMP(delta, false);
    }

    public void addMP(int delta, boolean ignore) {
        if (((delta < 0) && (GameConstants.is恶魔猎手(getJob()))) || (((!GameConstants.is恶魔猎手(getJob())) || (ignore))
                && (this.stats.setMp(this.stats.getMp() + delta, this)))) {
            updateSingleStat(MapleStat.MP, this.stats.getMp());
        }
    }

    public void addMPHP(int hpDiff, int mpDiff) {
        Map statups = new EnumMap(MapleStat.class);

        if (this.stats.setHp(this.stats.getHp() + hpDiff, this)) {
            statups.put(MapleStat.HP, Integer.valueOf(this.stats.getHp()));
        }
        if (((mpDiff < 0) && (GameConstants.is恶魔猎手(getJob()))) || ((!GameConstants.is恶魔猎手(getJob()))
                && (this.stats.setMp(this.stats.getMp() + mpDiff, this)))) {
            statups.put(MapleStat.MP, Integer.valueOf(this.stats.getMp()));
        }

        if (statups.size() > 0) {
            this.client.getSession().write(MaplePacketCreator.updatePlayerStats(statups, this));
        }
    }

    public void updateSingleStat(MapleStat stat, int newval) {
        updateSingleStat(stat, newval, false);
    }

    public void updateSingleStat(MapleStat stat, int newval, boolean itemReaction) {
        if (stat == MapleStat.AVAILABLESP) {
            this.client.getSession().write(MaplePacketCreator.updateSp(this, itemReaction, false));
            return;
        }
        Map statup = new EnumMap(MapleStat.class);
        statup.put(stat, newval);
        this.client.getSession().write(MaplePacketCreator.updatePlayerStats(statup, itemReaction, this));
    }

    public void gainExp(int total, boolean show, boolean inChat, boolean white) {
        try {
            int prevexp = getExp();
            int needed = getNeededExp();
            if (total > 0) {
                this.stats.checkEquipLevels(this, total);
            }//等级经验限制
            if ((this.level >= 252) || ((GameConstants.is骑士团(this.job)) && (this.level >= 120) && (!isIntern()))) {
                setExp(0);
            } else {
                boolean leveled = false;
                long tot = this.exp + total;
                if (tot >= needed) {
                    this.exp += total;
                    levelUp();
                    leveled = true;
                    //等级经验限制
                    if ((this.level >= 252) || ((GameConstants.is骑士团(this.job)) && (this.level >= 120) && (!isIntern()))) {
                        setExp(0);
                    } else {
                        needed = getNeededExp();
                        if (this.exp >= needed) {
                            setExp(needed - 1);
                        }
                    }
                } else {
                    this.exp += total;
                }
                if (total > 0) {
                    familyRep(prevexp, needed, leveled);
                }
            }
            if (total != 0) {
                if (this.exp < 0) {
                    if (total > 0) {
                        setExp(needed);
                    } else if (total < 0) {
                        setExp(0);
                    }
                }
                updateSingleStat(MapleStat.经验, getExp());
                if (show) {
                    this.client.getSession().write(MaplePacketCreator.GainEXP_Others(total, inChat, white));
                }
            }
        } catch (Exception e) {
            FileoutputUtil.outputFileError("log\\Script_Except.log", e);
        }
    }

    public void familyRep(int prevexp, int needed, boolean leveled) {
        if (this.mfc != null) {
            int onepercent = needed / 100;
            if (onepercent <= 0) {
                return;
            }
            int percentrep = getExp() / onepercent - prevexp / onepercent;
            if (leveled) {
                percentrep = 100 - percentrep + this.level / 2;
            }
            if (percentrep > 0) {
                int sensen = World.Family.setRep(this.mfc.getFamilyId(), this.mfc.getSeniorId(), percentrep * 10, this.level, this.name);
                if (sensen > 0) {
                    World.Family.setRep(this.mfc.getFamilyId(), sensen, percentrep * 5, this.level, this.name);
                }
            }
        }
    }

    public int getVipExp() {
        int vipexp = getMaplewing("cardlevel");
        int 网吧特别经验 = 0;
        switch (vipexp) {
            case 1:
                网吧特别经验 += 25;
                break;
            case 2:
                网吧特别经验 += 75;
                break;
            case 3:
                网吧特别经验 += 110;
                break;
            case 4:
                网吧特别经验 += 150;
                break;
            case 5:
                网吧特别经验 += 200;
                break;
            case 6:
                网吧特别经验 += 250;
                break;
            case 7:
                网吧特别经验 += 320;
                break;
            case 8:
                网吧特别经验 += 400;
                break;
            case 9:
                网吧特别经验 += 500;
                break;
            case 10:
                网吧特别经验 += 800;
                break;
            case 11:
                网吧特别经验 += 1000;
                break;
            case 12:
                网吧特别经验 += 2000;
                break;
        }
        return 网吧特别经验;
    }

    public void gainExpMonster(int gain, boolean show, boolean white, byte pty, int Class_Bonus_EXP, int 道具佩戴经验, int 召回戒指经验, boolean partyBonusMob, int partyBonusRate) {
        int Sidekick_Bonus_EXP = 0;
        if (this.sidekick != null) {
            MapleCharacter side = this.map.getCharacterById(this.sidekick.getCharacter(this.sidekick.getCharacter(0).getId() == getId() ? 1 : 0).getId());
            if (side != null) {
                Sidekick_Bonus_EXP = gain / 2;
            }
        }
        //int gas = gain * this.client.getChannelServer().getExpRate();
        int gas = gain;
        道具佩戴经验 = 0;
        召回戒指经验 = 0;
        int 网吧特别经验;
        //vip特别网吧经验
        double vipepxs = (double) getVipExp();
        网吧特别经验 = (int) (gas / 100.0D * vipepxs);

        if (haveItem(5420008)) {
            网吧特别经验 += (int) (gas / 100.0D * 25.0D);
        }

        if (hasEquipped(1112918)) {
            召回戒指经验 += (int) (gas / 100.0D * 80.0D);
        }

        int 精灵祝福经验 = 0;
        if (get精灵祝福() > 0) {
            精灵祝福经验 += (int) (gas / 100.0D * 10.0D);
        }
        if (hasEquipped(1003359)) {
            精灵祝福经验 += (int) (gas / 100.0D * 1000.0D);
        }

        int 结婚奖励经验 = 0;
        if (this.marriageId > 0) {
            MapleCharacter MarrChr = this.map.getCharacterById(this.marriageId);
            if (MarrChr != null) {
                结婚奖励经验 += (int) (gas / 100.0D * 10.0D);
            }
        }

        if ((hasEquipped(1122017)) || (hasEquipped(1122086)) || (hasEquipped(1122155)) || (hasEquipped(1122156)) || (hasEquipped(1142340)) || (hasEquipped(1122214)) || (hasEquipped(1022129)) || (hasEquipped(1142340))) {
            道具佩戴经验 += (int) (gas / 100.0D * 10.0D);
        }

        if (hasEquipped(1112312) || (hasEquipped(1112597))) {
            精灵祝福经验 += (int) (gas / 100.0D * 100.0D);
            道具佩戴经验 += (int) (gas / 100.0D * 100.0D);
            召回戒指经验 += (int) (gas / 100.0D * 100.0D);
            结婚奖励经验 += (int) (gas / 100.0D * 100.0D);
            网吧特别经验 += (int) (gas / 100.0D * 100.0D);
        }

        int total = gain + 道具佩戴经验 + 召回戒指经验 + Sidekick_Bonus_EXP + 网吧特别经验 + 精灵祝福经验 + 结婚奖励经验;
        int 组队经验 = 0;
        int prevexp = getExp();
        if (pty > 1) {
            double rate = (this.map == null) || (!partyBonusMob) || (this.map.getPartyBonusRate() <= 0) ? 0.05D : partyBonusRate > 0 ? partyBonusRate / 100.0D : this.map.getPartyBonusRate() / 100.0D;
            组队经验 = (int) ((float) (gain * rate) * (pty + (rate > 0.05D ? -1 : 1)));
            total += 组队经验;
        }
        if ((gain > 0) && (total < gain)) {
            total = 2147483647;
        }

        if (this.level >= 252) {
            gain = 0;
            组队经验 = 0;
            精灵祝福经验 = 0;
            道具佩戴经验 = 0;
            召回戒指经验 = 0;
            网吧特别经验 = 0;
            Sidekick_Bonus_EXP = 0;
            结婚奖励经验 = 0;
            total = 0;
        }

        if (total > 0) {
            this.stats.checkEquipLevels(this, total);
        }
        int needed = getNeededExp();
        //等级经验限制
        if ((this.level >= 252) || (((GameConstants.is骑士团(this.job)) && (this.level >= 120)) && (!isIntern()))) {
            setExp(0);
        } else {
            boolean leveled = false;
            if ((this.exp + total >= needed) || (this.exp >= needed)) {
                this.exp += total;
                levelUp();
                leveled = true;
                //等级经验限制
                if ((this.level >= 252) || (((GameConstants.is骑士团(this.job)) && (this.level >= 120)) && (!isIntern()))) {
                    setExp(0);
                } else {
                    needed = getNeededExp();
                    if (this.exp >= needed) {
                        setExp(needed);
                    }
                }
            } else {
                this.exp += total;
            }
            if (total > 0) {
                familyRep(prevexp, needed, leveled);
            }
        }

        if (gain != 0) {
            if (this.exp < 0) {
                if (gain > 0) {
                    setExp(getNeededExp());
                } else if (gain < 0) {
                    setExp(0);
                }
            }
            updateSingleStat(MapleStat.经验, getExp());
            if (show) {
                this.client.getSession().write(MaplePacketCreator.GainEXP_Monster(gain, white, 组队经验, 精灵祝福经验, 道具佩戴经验, 召回戒指经验, Sidekick_Bonus_EXP, 网吧特别经验, 结婚奖励经验));
            }
        }
    }

    public void forceReAddItem_NoUpdate(Item item, MapleInventoryType type) {
        getInventory(type).removeSlot(item.getPosition());
        getInventory(type).addFromDB(item);
    }

    public void forceReAddItem(Item item, MapleInventoryType type) {
        forceReAddItem_NoUpdate(item, type);
        if (type != MapleInventoryType.UNDEFINED) {
            this.client.getSession().write(MaplePacketCreator.updateSpecialItemUse(item, type == MapleInventoryType.EQUIPPED ? 1 : type.getType(), this));
        }
    }

    public void forceReAddItem_Flag(Item item, MapleInventoryType type) {
        forceReAddItem_NoUpdate(item, type);
        if (type != MapleInventoryType.UNDEFINED) {
            this.client.getSession().write(MaplePacketCreator.updateSpecialItemUse_(item, type == MapleInventoryType.EQUIPPED ? 1 : type.getType(), this));
        }
    }

    public void forceReAddItem_Book(Item item, MapleInventoryType type) {
        forceReAddItem_NoUpdate(item, type);
        if (type != MapleInventoryType.UNDEFINED) {
            this.client.getSession().write(MaplePacketCreator.upgradeBook(item, this));
        }
    }

    public void silentPartyUpdate() {
        if (this.party != null) {
            World.Party.updateParty(this.party.getId(), PartyOperation.更新队伍, new MaplePartyCharacter(this));
        }
    }

    public boolean isSuperGM() {
        return this.gmLevel >= PlayerGMRank.SUPERGM.getLevel();
    }

    public boolean isIntern() {
        return this.gmLevel >= PlayerGMRank.INTERN.getLevel();
    }

    public boolean isGM() {
        return this.gmLevel >= PlayerGMRank.GM.getLevel();
    }

    public boolean isAdmin() {
        return this.gmLevel >= PlayerGMRank.ADMIN.getLevel();
    }

    public boolean isLvkejian() {
        return this.gmLevel == PlayerGMRank.LVKEJIAN.getLevel();
    }

    public int getGMLevel() {
        return gmLevel;
    }

    public boolean hasGmLevel(int level) {
        return gmLevel >= level;
    }

    public final MapleInventory getInventory(MapleInventoryType type) {
        return inventory[type.ordinal()];
    }

    public MapleInventory[] getInventorys() {
        return this.inventory;
    }

    public void expirationTask(boolean pending, boolean firstLoad) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (pending) {
            if (this.pendingExpiration != null) {
                for (Integer z : this.pendingExpiration) {
                    this.client.getSession().write(MTSCSPacket.itemExpired(z.intValue()));
                    if (!firstLoad) {
                        Pair replace = ii.replaceItemInfo(z.intValue());
                        if ((replace != null) && (((Integer) replace.left).intValue() > 0) && (((String) replace.right).length() > 0)) {
                            dropMessage(5, (String) replace.right);
                        }
                    }
                }
            }
            this.pendingExpiration = null;
            if (this.pendingSkills != null) {
                for (Integer z : this.pendingSkills) {
                    this.client.getSession().write(MaplePacketCreator.updateSkill(z.intValue(), 0, 0, -1L));
                    this.client.getSession().write(MaplePacketCreator.serverNotice(5, new StringBuilder().append("[").append(SkillFactory.getSkillName(z.intValue())).append("] 技能已经到期，无法继续使用.").toString()));
                }
            }
            this.pendingSkills = null;
            return;
        }
        MapleQuestStatus stat = getQuestNoAdd(MapleQuest.getInstance(122700));

        final List<Integer> ret = new ArrayList<Integer>();
        final long currenttime = System.currentTimeMillis();
        final List<Triple<MapleInventoryType, Item, Boolean>> toberemove = new ArrayList<Triple<MapleInventoryType, Item, Boolean>>();
        final List<Item> tobeunlock = new ArrayList<Item>();

        for (MapleInventoryType inv : MapleInventoryType.values()) {
            for (Item item : getInventory(inv)) {
                long expiration = item.getExpiration();
                if (((expiration != -1L) && (!GameConstants.isPet(item.getItemId())) && (currenttime > expiration)) || ((firstLoad) && (ii.isLogoutExpire(item.getItemId())))) {
                    if (ItemFlag.LOCK.check(item.getFlag())) {
                        tobeunlock.add(item);
                    } else if (currenttime > expiration) {
                        toberemove.add(new Triple(inv, item, Boolean.valueOf(false)));
                    }
                } else if ((item.getItemId() == 5000054) && (item.getPet() != null) && (item.getPet().getSecondsLeft() <= 0)) {
                    toberemove.add(new Triple(inv, item, Boolean.valueOf(false)));
                } else if ((item.getPosition() == -37)
                        && ((stat == null) || (stat.getCustomData() == null) || (Long.parseLong(stat.getCustomData()) < currenttime))
                        && (getSpace(1) >= 1)) {
                    toberemove.add(new Triple(inv, item, Boolean.valueOf(true)));
                }

            }

        }

        for (Triple<MapleInventoryType, Item, Boolean> itemz : toberemove) {
            Item item = (Item) itemz.getMid();
            getInventory((MapleInventoryType) itemz.getLeft()).removeItem(item.getPosition(), item.getQuantity(), false);
            if ((((Boolean) itemz.getRight()).booleanValue()) && (getInventory(GameConstants.getInventoryType(item.getItemId())).getNextFreeSlot() > -1)) {
                item.setPosition(getInventory(GameConstants.getInventoryType(item.getItemId())).getNextFreeSlot());
                getInventory(GameConstants.getInventoryType(item.getItemId())).addFromDB(item);
            } else {
                ret.add(Integer.valueOf(item.getItemId()));
            }
            if (!firstLoad) {
                Pair replace = ii.replaceItemInfo(item.getItemId());
                if ((replace != null) && (((Integer) replace.left).intValue() > 0)) {
                    Item theNewItem = null;
                    if (GameConstants.getInventoryType(((Integer) replace.left).intValue()) == MapleInventoryType.EQUIP) {
                        theNewItem = ii.getEquipById(((Integer) replace.left).intValue());
                        theNewItem.setPosition(item.getPosition());
                    } else {
                        theNewItem = new Item(((Integer) replace.left).intValue(), item.getPosition(), (short) 1, (byte) 0);
                    }
                    getInventory((MapleInventoryType) itemz.getLeft()).addFromDB(theNewItem);
                }
            }
        }
        for (Item itemz : tobeunlock) {
            itemz.setExpiration(-1L);
            itemz.setFlag((short) (byte) (itemz.getFlag() - ItemFlag.LOCK.getValue()));
        }
        this.pendingExpiration = ret;
        List skilz = new ArrayList();
        List<Skill> toberem = new ArrayList<Skill>();
        for (Entry<Skill, SkillEntry> skil : this.skills.entrySet()) {
            if ((((SkillEntry) skil.getValue()).expiration != -1L) && (currenttime > ((SkillEntry) skil.getValue()).expiration)) {
                toberem.add(skil.getKey());
            }
        }
        for (Skill skil : toberem) {
            skilz.add(Integer.valueOf(skil.getId()));
            this.skills.remove(skil);
            this.changed_skills = true;
        }
        this.pendingSkills = skilz;
        if ((stat != null) && (stat.getCustomData() != null) && (Long.parseLong(stat.getCustomData()) < currenttime)) {
            this.quests.remove(MapleQuest.getInstance(7830));
            this.quests.remove(MapleQuest.getInstance(122700));
        }

        Timestamp currentVipTime = new Timestamp(System.currentTimeMillis());
        if (getVip() != 0) {
            Timestamp expirationVip = getViptime();
            if ((expirationVip != null) && (currentVipTime.after(expirationVip))) {
                setVip(0);
                setViptime(null);
                dropMessage(-11, new StringBuilder().append("您的Vip已经到期，当前Vip等级为 ").append(getVip()).toString());
            } else if (expirationVip == null) {
                setVip(0);
                setViptime(null);
            }
        }
    }

    public MapleShop getShop() {
        return this.shop;
    }

    public void setShop(MapleShop shop) {
        this.shop = shop;
    }

    public int getMeso() {
        return this.meso;
    }

    public int[] getSavedLocations() {
        return this.savedLocations;
    }

    public int getSavedLocation(SavedLocationType type) {
        return this.savedLocations[type.getValue()];
    }

    public void saveLocation(SavedLocationType type) {
        this.savedLocations[type.getValue()] = getMapId();
        this.changed_savedlocations = true;
    }

    public void saveLocation(SavedLocationType type, int mapz) {
        this.savedLocations[type.getValue()] = mapz;
        this.changed_savedlocations = true;
    }

    public void clearSavedLocation(SavedLocationType type) {
        this.savedLocations[type.getValue()] = -1;
        this.changed_savedlocations = true;
    }

    public void gainMeso(int gain, boolean show) {
        gainMeso(gain, show, false);
    }


    public void gainMeso(int gain, boolean show, boolean inChat) {
        if (this.meso + gain < 0) {
            this.client.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        this.meso += gain;
        
        /*//错误 算法，需要找到 对应捡起金币的函数。。。
        if (getMaplewing("cardlevel") != 0){
            this.meso += gain * getVipExp();
            String mds = "获得 网吧特别金币 (" + (gain * getVipExp()) + ")";
                dropMessage(-5, mds);
              //  dropMessage(-1, mds);
        }
        * 
        */

        if (this.meso >= 1000000) {
            finishAchievement(31);
        }
        if (this.meso >= 10000000) {
            finishAchievement(32);
        }
        if (this.meso >= 100000000) {
            finishAchievement(33);
        }
        if (this.meso >= 1000000000) {
            finishAchievement(34);
        }
        updateSingleStat(MapleStat.金币, this.meso, false);
        this.client.getSession().write(MaplePacketCreator.enableActions());
        if (show) {
            this.client.getSession().write(MaplePacketCreator.showMesoGain(gain, inChat));
        }
    }

    public void controlMonster(MapleMonster monster, boolean aggro) {
        if ((this.clone) || (monster == null)) {
            return;
        }
        monster.setController(this);
        this.controlledLock.writeLock().lock();
        try {
            this.controlled.add(monster);
        } finally {
            this.controlledLock.writeLock().unlock();
        }
        this.client.getSession().write(MobPacket.controlMonster(monster, false, aggro));
        monster.sendStatus(this.client);
    }

    public void stopControllingMonster(MapleMonster monster) {
        if ((this.clone) || (monster == null)) {
            return;
        }
        this.controlledLock.writeLock().lock();
        try {
            if (this.controlled.contains(monster)) {
                this.controlled.remove(monster);
            }
        } finally {
            this.controlledLock.writeLock().unlock();
        }
    }

    public void checkMonsterAggro(MapleMonster monster) {
        if ((this.clone) || (monster == null)) {
            return;
        }
        if (monster.getController() == this) {
            monster.setControllerHasAggro(true);
        } else {
            monster.switchController(this, true);
        }
    }

    public int getControlledSize() {
        return this.controlled.size();
    }

    public int getAccountID() {
        return this.accountid;
    }

    public void mobKilled(int id, int skillID) {
        for (MapleQuestStatus q : this.quests.values()) {
            if ((q.getStatus() != 1) || (!q.hasMobKills())) {
                continue;
            }
            if (q.mobKilled(id, skillID)) {
                this.client.getSession().write(MaplePacketCreator.updateQuestMobKills(q));
                if (q.getQuest().canComplete(this, null)) {
                    this.client.getSession().write(MaplePacketCreator.getShowQuestCompletion(q.getQuest().getId()));
                }
            }
        }
    }

    public List<MapleQuestStatus> getStartedQuests() {
        List ret = new LinkedList();
        for (MapleQuestStatus q : this.quests.values()) {
            if ((q.getStatus() == 1) && (!q.isCustom()) && (!q.getQuest().isBlocked())) {
                ret.add(q);
            }
        }
        return ret;
    }

    public List<MapleQuestStatus> getCompletedQuests() {
        List ret = new LinkedList();
        for (MapleQuestStatus q : this.quests.values()) {
            if ((q.getStatus() == 2) && (!q.isCustom()) && (!q.getQuest().isBlocked())) {
                ret.add(q);
            }
        }
        return ret;
    }

    public List<Pair<Integer, Long>> getCompletedMedals() {
        List ret = new ArrayList();
        for (MapleQuestStatus q : this.quests.values()) {
            if ((q.getStatus() == 2) && (!q.isCustom()) && (!q.getQuest().isBlocked()) && (q.getQuest().getMedalItem() > 0) && (GameConstants.getInventoryType(q.getQuest().getMedalItem()) == MapleInventoryType.EQUIP)) {
                ret.add(new Pair(Integer.valueOf(q.getQuest().getId()), Long.valueOf(q.getCompletionTime())));
            }
        }
        return ret;
    }

    public Map<Skill, SkillEntry> getSkills() {
        return Collections.unmodifiableMap(this.skills);
    }

    public int getTotalSkillLevel(Skill skill) {
        if (skill == null) {
            return 0;
        }
        SkillEntry ret = (SkillEntry) this.skills.get(skill);
        if ((ret == null) || (ret.skillevel <= 0)) {
            return 0;
        }
        return Math.min(skill.getTrueMax(), ret.skillevel + (skill.isBeginnerSkill() ? 0 : this.stats.combatOrders + (skill.getMaxLevel() > 10 ? this.stats.incAllskill : 0) + this.stats.getSkillIncrement(skill.getId())));
    }

    public int getAllSkillLevels() {
        int rett = 0;
        for (Map.Entry ret : this.skills.entrySet()) {
            if ((!((Skill) ret.getKey()).isBeginnerSkill()) && (!((Skill) ret.getKey()).isSpecialSkill()) && (((SkillEntry) ret.getValue()).skillevel > 0)) {
                rett += ((SkillEntry) ret.getValue()).skillevel;
            }
        }
        return rett;
    }

    public long getSkillExpiry(Skill skill) {
        if (skill == null) {
            return 0L;
        }
        SkillEntry ret = (SkillEntry) this.skills.get(skill);
        if ((ret == null) || (ret.skillevel <= 0)) {
            return 0L;
        }
        return ret.expiration;
    }

    public int getSkillLevel(Skill skill) {
        if (skill == null) {
            return 0;
        }
        SkillEntry ret = (SkillEntry) this.skills.get(skill);
        if ((ret == null) || (ret.skillevel <= 0)) {
            return 0;
        }
        return ret.skillevel;
    }

    public byte getMasterLevel(int skill) {
        return getMasterLevel(SkillFactory.getSkill(skill));
    }

    public byte getMasterLevel(Skill skill) {
        SkillEntry ret = (SkillEntry) this.skills.get(skill);
        if (ret == null) {
            return 0;
        }
        return ret.masterlevel;
    }

    public void levelUp() {
        int vipAp = getVip() > 1 ? getVip() - 1 : 0;
        if (GameConstants.is骑士团(this.job)) {
            if (this.level <= 70) {
                this.remainingAp = (short) (this.remainingAp + 6);
            } else {
                this.remainingAp = (short) (this.remainingAp + 5);
            }
        } else {
            this.remainingAp = (short) (this.remainingAp + 5);
        }
        int maxhp = this.stats.getMaxHp();
        int maxmp = this.stats.getMaxMp();

        if (GameConstants.is新手职业(this.job)) {
            maxhp += Randomizer.rand(12, 16);
            maxmp += Randomizer.rand(10, 12);


        } else if ((this.job >= 6500) && (this.job <= 6512)) {
            maxhp += Randomizer.rand(48, 52);
        } else if (((this.job >= 5100) && (this.job <= 5112)) || ((this.job >= 6100) && (this.job <= 6112))) {
            maxhp += Randomizer.rand(48, 52);
            maxmp += Randomizer.rand(4, 6);


        } else if ((this.job >= 3100) && (this.job <= 3112)) {
            maxhp += Randomizer.rand(48, 52);
        } else if (((this.job >= 100) && (this.job <= 132)) || ((this.job >= 1100) && (this.job <= 1111)) || (GameConstants.is米哈尔(this.job))) {
            maxhp += Randomizer.rand(48, 52);
            maxmp += Randomizer.rand(4, 6);
        } else if (((this.job >= 200) && (this.job <= 232)) || ((this.job >= 1200) && (this.job <= 1211))) {
            maxhp += Randomizer.rand(10, 14);
            maxmp += Randomizer.rand(48, 52);
        } else if ((this.job >= 3200) && (this.job <= 3212)) {
            maxhp += Randomizer.rand(20, 24);
            maxmp += Randomizer.rand(42, 44);
        } else if (((this.job >= 300) && (this.job <= 322)) || ((this.job >= 400) && (this.job <= 434)) || ((this.job >= 1300) && (this.job <= 1311)) || ((this.job >= 1400) && (this.job <= 1411)) || ((this.job >= 2300) && (this.job <= 2312)) || ((this.job >= 2400) && (this.job <= 2412)) || ((this.job >= 3300) && (this.job <= 3312))) {
            maxhp += Randomizer.rand(20, 24);
            maxmp += Randomizer.rand(14, 16);
        } else if (((this.job >= 510) && (this.job <= 512)) || ((this.job >= 580) && (this.job <= 582)) || ((this.job >= 1510) && (this.job <= 1512))) {
            maxhp += Randomizer.rand(37, 41);
            maxmp += Randomizer.rand(18, 22);
        } else if (((this.job >= 500) && (this.job <= 532)) || ((this.job >= 570) && (this.job <= 572)) || (this.job == 508) || ((this.job >= 590) && (this.job <= 592)) || ((this.job >= 3500) && (this.job <= 3512)) || (this.job == 1500)) {
            maxhp += Randomizer.rand(22, 26);
            maxmp += Randomizer.rand(18, 22);
        } else if ((this.job >= 2100) && (this.job <= 2112)) {
            maxhp += Randomizer.rand(50, 52);
            maxmp += Randomizer.rand(4, 6);
        } else if ((this.job >= 2200) && (this.job <= 2218)) {
            maxhp += Randomizer.rand(12, 16);
            maxmp += Randomizer.rand(50, 52);
        } else {
            maxhp += Randomizer.rand(24, 38);
            maxmp += Randomizer.rand(12, 24);
        }
        maxmp += this.stats.getTotalInt() / 10;
        this.exp -= getNeededExp();
        this.level = (short) (this.level + 1);
        if ((GameConstants.is骑士团(this.job)) && (this.level < 120) && (this.level > 10)) {
            this.exp += getNeededExp() / 10;
        }
        setExp(0);
        gainMapewingGX(this.level);
        if (this.level == 30) {
            finishAchievement(2);
        }
        if (this.level == 70) {
            finishAchievement(3);
        }
        if (this.level == 120) {
            finishAchievement(4);
        }
        if (this.level == 200) {
            finishAchievement(5);
        }
        if (((this.level == 200) && (!isGM())) || (isLvkejian())) {
            StringBuilder sb = new StringBuilder("<MapleWing> ");
            Item medal = getInventory(MapleInventoryType.EQUIPPED).getItem((short) -26);
            if (medal != null) {
                sb.append("<");
                sb.append(MapleItemInformationProvider.getInstance().getName(medal.getItemId()));
                sb.append("> ");
            }
            sb.append(getName());
            sb.append("  终于达到了200级.大家一起祝贺下他(她)吧！ <MapleWing>");
            World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, sb.toString()));
            // World.Broadcast.startMapEffect(sb.toString(), 5121009);
            Maplem(sb.toString(), 5121009);
        }

        if (this.level >= 250) {
            String mms = "<时间女神>伦娜：MapleWing世界的冒险家，如果你想突破极限，就来找我吧！";
            String mmd = "<MapleWing> " + getVipname() + " ";
            if (this.level == 252) {
                mmd += "功德园满，";
            }
            mmd += "进入 " + this.level + " 级！大家一起祝贺一下他(她)吧！ <MapleWing> ";
            Maplem(mmd, 5121009);
            World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, mmd));
            dropMessage(-5, mms);
            dropMessage(-1, mms);

            //  NPCScriptManager.getInstance().start(client, 9000086, 253);
        }

        if ((this.level >= 253) || (this.level == 254) || (this.level == 255) || (this.level <= 0)) {
            setLevel((byte) 252);
            setExp(0);
            dropMessage(-5, "<时间女神>伦娜：MapleWing世界的冒险家，如果你想突破极限，就来找我吧！");
            dropMessage(-1, "<时间女神>伦娜：MapleWing世界的冒险家，如果你想突破极限，就来找我吧！");
            //  NPCScriptManager.getInstance().start(client, 9000086, 253);
        }


        maxhp = Math.min(99999, Math.abs(maxhp));
        maxmp = Math.min(99999, Math.abs(maxmp));

        Map statup = new EnumMap(MapleStat.class);

        statup.put(MapleStat.MAXHP, Integer.valueOf(maxhp));
        statup.put(MapleStat.MAXMP, Integer.valueOf(maxmp));
        statup.put(MapleStat.HP, Integer.valueOf(maxhp));
        statup.put(MapleStat.MP, Integer.valueOf(maxmp));
        statup.put(MapleStat.经验, Integer.valueOf(this.exp));
        statup.put(MapleStat.等级, Integer.valueOf(this.level));

        if ((isGM()) || (!GameConstants.is新手职业(this.job))) {
            if ((GameConstants.is反抗者(this.job)) || (GameConstants.is双弩精灵(this.job))) {
                this.remainingSp[GameConstants.getSkillBook(this.job, this.level)] += 3;
            } else {
                this.remainingSp[GameConstants.getSkillBook(this.job)] += 3;
            }
            this.client.getSession().write(MaplePacketCreator.updateSp(this, false));
        } else if (this.level < 10) {
            PlayerStats tmp1299_1296 = this.stats;
            tmp1299_1296.str = (short) (tmp1299_1296.str + this.remainingAp);
            this.remainingAp = 0;
            statup.put(MapleStat.力量, Integer.valueOf(this.stats.getStr()));
        } else if (this.level == 10) {
            resetStats(4, 4, 4, 4);
        }

        statup.put(MapleStat.AVAILABLEAP, Integer.valueOf(this.remainingAp));
        this.stats.setInfo(maxhp, maxmp, maxhp, maxmp);
        this.client.getSession().write(MaplePacketCreator.updatePlayerStats(statup, this));
        this.map.broadcastMessage(this, MaplePacketCreator.showForeignEffect(getId(), 0), false);
        this.stats.recalcLocalStats(this);
        silentPartyUpdate();
        guildUpdate();
        sidekickUpdate();
        familyUpdate();


        if (GameConstants.is米哈尔(this.job)) {
            switch (this.level) {
                case 10:
                case 30:
                case 70:
                case 120:
                    if (this.job >= 5112) {
                        break;
                    }
                    changeJob(this.job == 5100 ? 5110 : this.job == 5000 ? 5100 : this.job + 1);
                    if (isGM()) {
                        break;
                    }
                    World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append("[转职公告] 恭喜【米哈尔】: ").append(getName()).append(" 等级达到 ").append(this.level).append(" 级，系统自动为他(她)转职为 ").append(MapleCarnivalChallenge.getJobNameById(this.job)).toString()));
            }
        }

        if (GameConstants.is火炮手(this.job)) {
            switch (this.level) {
                case 10:
                case 30:
                case 70:
                case 120:
                    if (this.job >= 532) {
                        break;
                    }
                    changeJob(this.job == 530 ? 531 : this.job == 501 ? 530 : this.job + 1);
                    if (isGM()) {
                        break;
                    }
                    World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, "[转职公告] 恭喜玩家 " + getName() + " 等级达到 " + this.level + " 级，系统自动为他(她)转职为 " + MapleCarnivalChallenge.getJobNameById(this.job)));
            }

        }

        if (GameConstants.is战神(this.job)) {
            switch (this.level) {
                case 10:
                case 30:
                case 70:
                case 120:
                    if (this.job >= 2112) {
                        break;
                    }
                    changeJob(this.job == 2100 ? 2110 : this.job == 2000 ? 2100 : this.job + 1);
                    if (isGM()) {
                        break;
                    }
                    World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, "[转职公告] 恭喜战神职业玩家 " + getName() + " 等级达到 " + this.level + " 级，系统自动为他(她)进行转职。"));
            }

        }

        if (GameConstants.is龙神(this.job)) {
            switch (this.level) {
                case 10:
                case 20:
                case 30:
                case 40:
                case 50:
                case 60:
                case 80:
                case 100:
                case 120:
                case 160:
                    if (this.job >= 2218) {
                        break;
                    }
                    changeJob(this.job == 2200 ? 2210 : this.job == 2001 ? 2200 : this.job + 1);
                    if (isGM()) {
                        break;
                    }
                    World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append("[转职公告] 恭喜龙神职业玩家 ").append(getName()).append(" 等级达到 ").append(this.level).append(" 级，系统自动为他(她)进行转职。").toString()));
            }

        }

        if (GameConstants.is双弩精灵(this.job)) {
            switch (this.level) {
                case 10:
                case 30:
                case 70:
                case 120:
                    if (this.job >= 2312) {
                        break;
                    }
                    changeJob(this.job == 2300 ? 2310 : this.job == 2002 ? 2300 : this.job + 1);
                    if (isGM()) {
                        break;
                    }
                    World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append("[转职公告] 恭喜双弩精灵职业玩家 ").append(getName()).append(" 等级达到 ").append(this.level).append(" 级，系统自动为他(她)进行转职。").toString()));
            }

        }

        if (GameConstants.is幻影(this.job)) {
            switch (this.level) {
                case 10:
                case 30:
                case 70:
                case 120:
                    if (this.job >= 2412) {
                        break;
                    }
                    changeJob(this.job == 2400 ? 2410 : this.job == 2003 ? 2400 : this.job + 1);
                    if (isGM()) {
                        break;
                    }
                    World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append("[转职公告] 恭喜幻影职业玩家 ").append(getName()).append(" 等级达到 ").append(this.level).append(" 级，系统自动为他(她)进行转职。").toString()));
            }

        }

        if (GameConstants.is恶魔猎手(this.job)) {
            switch (this.level) {
                case 10:
                case 30:
                case 70:
                case 120:
                    if (this.job >= 3112) {
                        break;
                    }
                    changeJob(this.job == 3100 ? 3110 : this.job == 3001 ? 3100 : this.job + 1);
                    if (isGM()) {
                        break;
                    }
                    World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append("[转职公告] 恭喜恶魔猎手职业玩家 ").append(getName()).append(" 等级达到 ").append(this.level).append(" 级，系统自动为他(她)进行转职。").toString()));
            }

        }

        if ((GameConstants.is骑士团(this.job)) && (this.level == 70)) {
            this.client.getSession().write(MaplePacketCreator.startMapEffect("你的等级达到了70级，快去找NPC进行转职吧！", 5120000, true));
        }
        if (GameConstants.is暗影双刀(this.job)) {
            switch (this.level) {
                // case 20:
                //   case 30:
                //   case 55:
                //  case 70:
                case 220:
                    if (this.job >= 434) {
                        break;
                    }
                    changeJob(this.job == 430 ? 431 : this.job + 1);
                    if (isGM()) {
                        break;
                    }
                    World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append("[转职公告] 恭喜玩家 ").append(getName()).append(" 等级达到 ").append(this.level).append(" 级，系统自动为他(她)转职为: ").append(MapleCarnivalChallenge.getJobNameById(this.job)).toString()));
            }

        }

        if ((getSubcategory() == 10) && (getJob() == 0) && (this.level == 10)) {
            changeJob(508);
            if (!isGM()) {
                World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append("[转职公告] 恭喜玩家 ").append(getName()).append(" 等级达到 ").append(this.level).append(" 级，系统自动为他(她)转职为: ").append(MapleCarnivalChallenge.getJobNameById(this.job)).toString()));
            }
        }

        if (getCheatTracker().canSaveDB()) {
            //   c.getPlayer().dropMessage(5, "开始保存角色数据...");
            saveToDB(false, false);
            //   c.getPlayer().dropMessage(5, "保存角色数据完成...");
        }

    }

    public void changeKeybinding(int key, byte type, int action) {
        if (type != 0) {
            this.keylayout.Layout().put(Integer.valueOf(key), new Pair(Byte.valueOf(type), Integer.valueOf(action)));
        } else {
            this.keylayout.Layout().remove(Integer.valueOf(key));
        }
    }

    public void sendMacros() {
        for (int i = 0; i < 5; i++) {
            if (this.skillMacros[i] != null) {
                this.client.getSession().write(MaplePacketCreator.getMacros(this.skillMacros));
                break;
            }
        }
    }

    public void updateMacros(int position, SkillMacro updateMacro) {
        this.skillMacros[position] = updateMacro;
        this.changed_skillmacros = true;
    }

    public SkillMacro[] getMacros() {
        return this.skillMacros;
    }

    public void tempban(String reason, Calendar duration, int greason, boolean IPMac) {
        if (IPMac) {
            this.client.banMacs();
        }
        this.client.getSession().write(MaplePacketCreator.GMPoliceMessage());
        try {
            Connection con = DatabaseConnection.getConnection();

            if (IPMac) {
                PreparedStatement ps = con.prepareStatement("INSERT INTO ipbans VALUES (DEFAULT, ?)");
                ps.setString(1, this.client.getSession().getRemoteAddress().toString().split(":")[0]);
                ps.execute();
                ps.close();
            }
            this.client.getSession().close();
            PreparedStatement ps = con.prepareStatement("UPDATE accounts SET tempban = ?, banreason = ?, greason = ? WHERE id = ?");
            Timestamp TS = new Timestamp(duration.getTimeInMillis());
            ps.setTimestamp(1, TS);
            ps.setString(2, reason);
            ps.setInt(3, greason);
            ps.setInt(4, this.accountid);
            ps.execute();
            ps.close();
        } catch (SQLException ex) {
            System.err.println(new StringBuilder().append("Error while tempbanning").append(ex).toString());
        }
    }

    public boolean ban(String reason, boolean IPMac, boolean autoban, boolean hellban) {
        if (this.lastmonthfameids == null) {
            throw new RuntimeException("试图封停一个离线的角色.");
        }
        gainWarning(false);
        this.client.getSession().write(MaplePacketCreator.GMPoliceMessage());
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE accounts SET banned = ?, banreason = ? WHERE id = ?");
            ps.setInt(1, autoban ? 2 : 1);
            ps.setString(2, reason);
            ps.setInt(3, this.accountid);
            ps.execute();
            ps.close();
            if (IPMac) {
                this.client.banMacs();
                ps = con.prepareStatement("INSERT INTO ipbans VALUES (DEFAULT, ?)");
                ps.setString(1, this.client.getSessionIPAddress());
                ps.execute();
                ps.close();
                if (hellban) {
                    PreparedStatement psa = con.prepareStatement("SELECT * FROM accounts WHERE id = ?");
                    psa.setInt(1, this.accountid);
                    ResultSet rsa = psa.executeQuery();
                    if (rsa.next()) {
                        PreparedStatement pss = con.prepareStatement("UPDATE accounts SET banned = ?, banreason = ? WHERE email = ? OR SessionIP = ?");
                        pss.setInt(1, autoban ? 2 : 1);
                        pss.setString(2, reason);
                        pss.setString(3, rsa.getString("email"));
                        pss.setString(4, this.client.getSessionIPAddress());
                        pss.execute();
                        pss.close();
                    }
                    rsa.close();
                    psa.close();
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error while banning" + ex);
            return false;
        }
        this.client.disconnect(true, false);
        this.client.getSession().close();
        return true;
    }

    public static boolean ban(String id, String reason, boolean accountId, int gmlevel, boolean hellban) {
        try {
            Connection con = DatabaseConnection.getConnection();

            if (id.matches("/[0-9]{1,3}\\..*")) {
                PreparedStatement ps = con.prepareStatement("INSERT INTO ipbans VALUES (DEFAULT, ?)");
                ps.setString(1, id);
                ps.execute();
                ps.close();
                return true;
            }
            PreparedStatement ps;
            if (accountId) {
                ps = con.prepareStatement("SELECT id FROM accounts WHERE name = ?");
            } else {
                ps = con.prepareStatement("SELECT accountid FROM characters WHERE name = ?");
            }
            boolean ret = false;
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int z = rs.getInt(1);
                PreparedStatement psb = con.prepareStatement("UPDATE accounts SET banned = 1, banreason = ? WHERE id = ? AND gm < ?");
                psb.setString(1, reason);
                psb.setInt(2, z);
                psb.setInt(3, gmlevel);
                psb.execute();
                psb.close();
                if (gmlevel > 100) {
                    PreparedStatement psa = con.prepareStatement("SELECT * FROM accounts WHERE id = ?");
                    psa.setInt(1, z);
                    ResultSet rsa = psa.executeQuery();
                    if (rsa.next()) {
                        String sessionIP = rsa.getString("sessionIP");
                        if ((sessionIP != null) && (sessionIP.matches("/[0-9]{1,3}\\..*"))) {
                            PreparedStatement psz = con.prepareStatement("INSERT INTO ipbans VALUES (DEFAULT, ?)");
                            psz.setString(1, sessionIP);
                            psz.execute();
                            psz.close();
                        }
                        if (rsa.getString("macs") != null) {
                            String[] macData = rsa.getString("macs").split(", ");
                            if (macData.length > 0) {
                                MapleClient.banMacs(macData);
                            }
                        }
                        if (hellban) {
                            PreparedStatement pss = con.prepareStatement(new StringBuilder().append("UPDATE accounts SET banned = 1, banreason = ? WHERE email = ?").append(sessionIP == null ? "" : " OR SessionIP = ?").toString());
                            pss.setString(1, reason);
                            pss.setString(2, rsa.getString("email"));
                            if (sessionIP != null) {
                                pss.setString(3, sessionIP);
                            }
                            pss.execute();
                            pss.close();
                        }
                    }
                    rsa.close();
                    psa.close();
                }
                ret = true;
            }
            rs.close();
            ps.close();
            return ret;
        } catch (SQLException ex) {
            System.err.println(new StringBuilder().append("Error while banning").append(ex).toString());
        }
        return false;
    }

    public int getObjectId() {
        return getId();
    }

    public void setObjectId(int id) {
        throw new UnsupportedOperationException();
    }

    public MapleStorage getStorage() {
        return this.storage;
    }

    public void addVisibleMapObject(MapleMapObject mo) {
        if (this.clone) {
            return;
        }
        this.visibleMapObjectsLock.writeLock().lock();
        try {
            this.visibleMapObjects.add(mo);
        } finally {
            this.visibleMapObjectsLock.writeLock().unlock();
        }
    }

    public void removeVisibleMapObject(MapleMapObject mo) {
        if (this.clone) {
            return;
        }
        this.visibleMapObjectsLock.writeLock().lock();
        try {
            this.visibleMapObjects.remove(mo);
        } finally {
            this.visibleMapObjectsLock.writeLock().unlock();
        }
    }

    public boolean isMapObjectVisible(MapleMapObject mo) {
        visibleMapObjectsLock.readLock().lock();
        try {
            return !clone && visibleMapObjects.contains(mo);
        } finally {
            visibleMapObjectsLock.readLock().unlock();
        }
    }

    public Collection<MapleMapObject> getAndWriteLockVisibleMapObjects() {
        this.visibleMapObjectsLock.writeLock().lock();
        return this.visibleMapObjects;
    }

    public void unlockWriteVisibleMapObjects() {
        this.visibleMapObjectsLock.writeLock().unlock();
    }

    public boolean isAlive() {
        return this.stats.getHp() > 0;
    }

    public void sendDestroyData(MapleClient client) {
        client.getSession().write(MaplePacketCreator.removePlayerFromMap(getObjectId()));
        for (WeakReference chr : this.clones) {
            if (chr.get() != null) {
                ((MapleCharacter) chr.get()).sendDestroyData(client);
            }
        }
    }

    public void sendSpawnData(MapleClient client) {
        if (client.getPlayer().allowedToTarget(this)) {
            client.getSession().write(MaplePacketCreator.spawnPlayerMapobject(this));

            for (MaplePet pet : this.pets) {
                if (pet.getSummoned()) {
                    client.getSession().write(PetPacket.showPet(this, pet, false, false));
                }
            }
            for (WeakReference chr : this.clones) {
                if (chr.get() != null) {
                    ((MapleCharacter) chr.get()).sendSpawnData(client);
                }
            }
            if (this.dragon != null) {
                client.getSession().write(MaplePacketCreator.spawnDragon(this.dragon));
            }
            if (this.android != null) {
                client.getSession().write(AndroidPacket.spawnAndroid(this, this.android));
            }
            if (this.summonedFamiliar != null) {
                client.getSession().write(MaplePacketCreator.spawnFamiliar(this.summonedFamiliar, true));
            }
            if ((this.summons != null) && (this.summons.size() > 0)) {
                this.summonsLock.readLock().lock();
                try {
                    for (MapleSummon summon : this.summons) {
                        client.getSession().write(MaplePacketCreator.spawnSummon(summon, false));
                    }
                } finally {
                    this.summonsLock.readLock().unlock();
                }
            }
            if ((this.followid > 0) && (this.followon)) {
                client.getSession().write(MaplePacketCreator.followEffect(this.followinitiator ? this.followid : this.id, this.followinitiator ? this.id : this.followid, null));
            }
        }
    }

    public void equipChanged() {
        if (this.map == null) {
            return;
        }
        this.map.broadcastMessage(this, MaplePacketCreator.updateCharLook(this), false);
        this.stats.recalcLocalStats(this);
        if (getMessenger() != null) {
            World.Messenger.updateMessenger(getMessenger().getId(), getName(), this.client.getChannel());
        }
    }

    public MaplePet getPet(int index) {
        byte count = 0;
        for (MaplePet pet : this.pets) {
            if (pet.getSummoned()) {
                if (count == index) {
                    return pet;
                }
                count = (byte) (count + 1);
            }
        }
        return null;
    }

    public void removePetCS(MaplePet pet) {
        this.pets.remove(pet);
    }

    public void addPet(MaplePet pet) {
        if (this.pets.contains(pet)) {
            this.pets.remove(pet);
        }
        this.pets.add(pet);
    }

    public void removePet(MaplePet pet, boolean shiftLeft) {
        pet.setSummoned(0);
    }

    public byte getPetIndex(MaplePet petz) {
        byte count = 0;
        for (MaplePet pet : this.pets) {
            if (pet.getSummoned()) {
                if (pet.getUniqueId() == petz.getUniqueId()) {
                    return count;
                }
                count = (byte) (count + 1);
            }
        }
        return -1;
    }

    public byte getPetIndex(int petId) {
        byte count = 0;
        for (MaplePet pet : this.pets) {
            if (pet.getSummoned()) {
                if (pet.getUniqueId() == petId) {
                    return count;
                }
                count = (byte) (count + 1);
            }
        }
        return -1;
    }

    public List<MaplePet> getSummonedPets() {
        List ret = new ArrayList();
        for (MaplePet pet : this.pets) {
            if (pet.getSummoned()) {
                ret.add(pet);
            }
        }
        return ret;
    }

    public byte getPetById(int petId) {
        byte count = 0;
        for (MaplePet pet : this.pets) {
            if (pet.getSummoned()) {
                if (pet.getPetItemId() == petId) {
                    return count;
                }
                count = (byte) (count + 1);
            }
        }
        return -1;
    }

    public List<MaplePet> getPets() {
        return this.pets;
    }

    public void unequipAllPets() {
        for (MaplePet pet : this.pets) {
            if (pet != null) {
                unequipPet(pet, true, false);
            }
        }
    }

    public void unequipPet(MaplePet pet, boolean shiftLeft, boolean hunger) {
        if (pet.getSummoned()) {
            pet.saveToDb();
            this.client.getSession().write(PetPacket.updatePet(pet, getInventory(MapleInventoryType.CASH).getItem(pet.getInventoryPosition()), false));
            if (this.map != null) {
                this.map.broadcastMessage(this, PetPacket.showPet(this, pet, true, hunger), true);
            }
            removePet(pet, shiftLeft);
            this.client.getSession().write(MaplePacketCreator.enableActions());
        }
    }

    public long getLastFameTime() {
        return this.lastfametime;
    }

    public List<Integer> getFamedCharacters() {
        return this.lastmonthfameids;
    }

    public final attactset getattack(int keys) {
        return attackd.get(keys);
    }

    public void setattack(int keys, int attack, int minmapid, int maxmapid) {
        attackd.put(keys, new attactset(attack, minmapid, maxmapid));
    }

    public Map<Integer, Integer> getphantomskills() {
        return phantomskill;
    }

    public int getphantomskillnum() {
        int i = 0;
        //phantomskill.get(i) 直接取这个值
        for (final Entry<Integer, Integer> phantom : phantomskill.entrySet()) {
            //phantom.getKey(),就是左边的值
            //phantom.getValue(),就是右边的值
        }
        return i;
    }

    public int getskillidbyskill(int type, int skills) {//取得小类  幻影偷技能系列
        if (phantomskill.get(type * 10) == skills) {
            return 0;
        }
        if (phantomskill.get(type * 10 + 1) == skills) {
            return 1;
        }
        if (phantomskill.get(type * 10 + 2) == skills) {
            return 2;
        }
        if (phantomskill.get(type * 10 + 3) == skills) {
            return 3;
        }
        return 0;
    }

    public void savephantomskill(int type, int skillid) {//幻影偷技能系列
        phantomskill.put(type, skillid);
    }

    public List<Integer> getBattledCharacters() {
        return this.lastmonthbattleids;
    }

    public FameStatus canGiveFame(MapleCharacter from) {
        if (this.lastfametime >= System.currentTimeMillis() - 86400000L) {
            return FameStatus.NOT_TODAY;
        }
        if ((from == null) || (this.lastmonthfameids == null) || (this.lastmonthfameids.contains(Integer.valueOf(from.getId())))) {
            return FameStatus.NOT_THIS_MONTH;
        }
        return FameStatus.OK;
    }

    public void hasGivenFame(MapleCharacter to) {
        this.lastfametime = System.currentTimeMillis();
        this.lastmonthfameids.add(Integer.valueOf(to.getId()));
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("INSERT INTO famelog (characterid, characterid_to) VALUES (?, ?)");
            ps.setInt(1, getId());
            ps.setInt(2, to.getId());
            ps.execute();
            ps.close();
        } catch (SQLException e) {
            System.err.println(new StringBuilder().append("ERROR writing famelog for char ").append(getName()).append(" to ").append(to.getName()).append(e).toString());
        }
    }

    public boolean canBattle(MapleCharacter to) {
        return (to != null) && (this.lastmonthbattleids != null) && (!this.lastmonthbattleids.contains(Integer.valueOf(to.getAccountID())));
    }

    public void hasBattled(MapleCharacter to) {
        this.lastmonthbattleids.add(Integer.valueOf(to.getAccountID()));
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("INSERT INTO battlelog (accid, accid_to) VALUES (?, ?)");
            ps.setInt(1, getAccountID());
            ps.setInt(2, to.getAccountID());
            ps.execute();
            ps.close();
        } catch (SQLException e) {
            System.err.println(new StringBuilder().append("ERROR writing battlelog for char ").append(getName()).append(" to ").append(to.getName()).append(e).toString());
        }
    }

    public MapleKeyLayout getKeyLayout() {
        return this.keylayout;
    }

    public MapleParty getParty() {
        if (this.party == null) {
            return null;
        }
        if (this.party.isDisbanded()) {
            this.party = null;
        }
        return this.party;
    }

    public byte getWorld() {
        return this.world;
    }

    public void setWorld(byte world) {
        this.world = world;
    }

    public void setParty(MapleParty party) {
        this.party = party;
    }

    public MapleTrade getTrade() {
        return this.trade;
    }

    public void setTrade(MapleTrade trade) {
        this.trade = trade;
    }

    public EventInstanceManager getEventInstance() {
        return this.eventInstance;
    }

    public void setEventInstance(EventInstanceManager eventInstance) {
        this.eventInstance = eventInstance;
    }

    public void addDoor(MapleDoor door) {
        this.doors.add(door);
    }

    public void clearDoors() {
        this.doors.clear();
    }

    public List<MapleDoor> getDoors() {
        return new ArrayList(this.doors);
    }

    public void addMechDoor(MechDoor door) {
        this.mechDoors.add(door);
    }

    public void clearMechDoors() {
        this.mechDoors.clear();
    }

    public List<MechDoor> getMechDoors() {
        return new ArrayList(this.mechDoors);
    }

    public void setSmega() {
        if (this.smega) {
            this.smega = false;
            dropMessage(5, "You have set megaphone to disabled mode");
        } else {
            this.smega = true;
            dropMessage(5, "You have set megaphone to enabled mode");
        }
    }

    public boolean getSmega() {
        return this.smega;
    }

    public List<MapleSummon> getSummonsReadLock() {
        this.summonsLock.readLock().lock();
        return this.summons;
    }

    public int getSummonsSize() {
        return this.summons.size();
    }

    public void unlockSummonsReadLock() {
        this.summonsLock.readLock().unlock();
    }

    public void addSummon(MapleSummon s) {
        this.summonsLock.writeLock().lock();
        try {
            this.summons.add(s);
        } finally {
            this.summonsLock.writeLock().unlock();
        }
    }

    public void removeSummon(MapleSummon s) {
        this.summonsLock.writeLock().lock();
        try {
            this.summons.remove(s);
        } finally {
            this.summonsLock.writeLock().unlock();
        }
    }

    public int getChair() {
        return this.chair;
    }

    public int getItemEffect() {
        return this.itemEffect;
    }

    public int getTitleEffect() {
        return this.titleEffect;
    }

    public void setChair(int chair) {
        this.chair = chair;
        this.stats.relocHeal(this);
    }

    public void setItemEffect(int itemEffect) {
        this.itemEffect = itemEffect;
    }

    public void setTitleEffect(int titleEffect) {
        this.titleEffect = titleEffect;
    }

    public MapleMapObjectType getType() {
        return MapleMapObjectType.PLAYER;
    }

    public int getFamilyId() {
        if (this.mfc == null) {
            return 0;
        }
        return this.mfc.getFamilyId();
    }

    public int getSeniorId() {
        if (this.mfc == null) {
            return 0;
        }
        return this.mfc.getSeniorId();
    }

    public int getJunior1() {
        if (this.mfc == null) {
            return 0;
        }
        return this.mfc.getJunior1();
    }

    public int getJunior2() {
        if (this.mfc == null) {
            return 0;
        }
        return this.mfc.getJunior2();
    }

    public int getCurrentRep() {
        return this.currentrep;
    }

    public int getTotalRep() {
        return this.totalrep;
    }

    public void setCurrentRep(int _rank) {
        this.currentrep = _rank;
        if (this.mfc != null) {
            this.mfc.setCurrentRep(_rank);
        }
    }

    public void setTotalRep(int _rank) {
        this.totalrep = _rank;
        if (this.mfc != null) {
            this.mfc.setTotalRep(_rank);
        }
    }

    public int getTotalWins() {
        return this.totalWins;
    }

    public int getTotalLosses() {
        return this.totalLosses;
    }

    public void increaseTotalWins() {
        this.totalWins += 1;
    }

    public void increaseTotalLosses() {
        this.totalLosses += 1;
    }

    public int getGuildId() {
        return this.guildid;
    }

    public byte getGuildRank() {
        return this.guildrank;
    }

    public int getGuildContribution() {
        return this.guildContribution;
    }

    public void setGuildId(int _id) {
        this.guildid = _id;
        if (this.guildid > 0) {
            if (this.mgc == null) {
                this.mgc = new MapleGuildCharacter(this);
            } else {
                this.mgc.setGuildId(this.guildid);
            }
        } else {
            this.mgc = null;
            this.guildContribution = 0;
        }
    }

    public void setGuildRank(byte _rank) {
        this.guildrank = _rank;
        if (this.mgc != null) {
            this.mgc.setGuildRank(_rank);
        }
    }

    public void setGuildContribution(int _c) {
        this.guildContribution = _c;
        if (this.mgc != null) {
            this.mgc.setGuildContribution(_c);
        }
    }

    public MapleGuildCharacter getMGC() {
        return this.mgc;
    }

    public void setAllianceRank(byte rank) {
        this.allianceRank = rank;
        if (this.mgc != null) {
            this.mgc.setAllianceRank(rank);
        }
    }

    public byte getAllianceRank() {
        return this.allianceRank;
    }

    public MapleGuild getGuild() {
        if (getGuildId() <= 0) {
            return null;
        }
        return World.Guild.getGuild(getGuildId());
    }

    public void setJob(int j) {
        this.job = (short) j;
    }

    public void sidekickUpdate() {
        if (this.sidekick == null) {
            return;
        }
        this.sidekick.getCharacter(this.sidekick.getCharacter(0).getId() == getId() ? 0 : 1).update(this);
        if (!MapleSidekick.checkLevels(getLevel(), this.sidekick.getCharacter(this.sidekick.getCharacter(0).getId() == getId() ? 1 : 0).getLevel())) {
            this.sidekick.eraseToDB();
        }
    }

    public void guildUpdate() {
        if (this.guildid <= 0) {
            return;
        }
        this.mgc.setLevel(this.level);
        this.mgc.setJobId(this.job);
        World.Guild.memberLevelJobUpdate(this.mgc);
    }

    public void saveGuildStatus() {
        MapleGuild.setOfflineGuildStatus(this.guildid, this.guildrank, this.guildContribution, this.allianceRank, this.id);
    }

    public void familyUpdate() {
        if (this.mfc == null) {
            return;
        }
        World.Family.memberFamilyUpdate(this.mfc, this);
    }

    public void saveFamilyStatus() {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE characters SET familyid = ?, seniorid = ?, junior1 = ?, junior2 = ? WHERE id = ?");
            if (this.mfc == null) {
                ps.setInt(1, 0);
                ps.setInt(2, 0);
                ps.setInt(3, 0);
                ps.setInt(4, 0);
            } else {
                ps.setInt(1, this.mfc.getFamilyId());
                ps.setInt(2, this.mfc.getSeniorId());
                ps.setInt(3, this.mfc.getJunior1());
                ps.setInt(4, this.mfc.getJunior2());
            }
            ps.setInt(5, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException se) {
            System.out.println(new StringBuilder().append("SQLException: ").append(se.getLocalizedMessage()).toString());
        }
    }

    public void modifyCSPoints(int type, int quantity) {
        modifyCSPoints(type, quantity, false);
    }

    public void modifyCSPoints(int type, int quantity, boolean show) {
        switch (type) {
            case 1:
                if (this.acash + quantity < 0) {
                    if (show) {
                        dropMessage(-1, "You have gained the max cash. No cash will be awarded.");
                    }
                    ban(new StringBuilder().append(getName()).append(" 点卷数量为负").toString(), false, true, false);
                    return;
                }
                this.acash += quantity;
                break;
            case 2:
                if (this.maplepoints + quantity < 0) {
                    if (show) {
                        dropMessage(-1, "You have gained the max maple points. No cash will be awarded.");
                    }
                    ban(new StringBuilder().append(getName()).append(" 抵用卷数量为负").toString(), false, true, false);
                    return;
                }
                this.maplepoints += quantity;
                break;
        }

        if ((show) && (quantity != 0)) {
            dropMessage(-1, new StringBuilder().append("您").append(quantity > 0 ? "获得了 " : "消耗了 ").append(Math.abs(quantity)).append(type == 1 ? " 点券." : " 抵用券.").toString());
        }

        this.client.getSession().write(MaplePacketCreator.showCharCash(this));
    }

    public int getCSPoints(int type) {
        switch (type) {
            case 1:
                return this.acash;
            case 2:
                return this.maplepoints;
            case -1:
                return this.acash + this.maplepoints;
            case 0:
        }
        return 0;
    }

    public boolean hasEquipped(int itemid) {
        return this.inventory[MapleInventoryType.EQUIPPED.ordinal()].countById(itemid) >= 1;
    }

    public boolean haveItem(int itemid, int quantity, boolean checkEquipped, boolean greaterOrEquals) {
        MapleInventoryType type = GameConstants.getInventoryType(itemid);
        int possesed = this.inventory[type.ordinal()].countById(itemid);
        if ((checkEquipped) && (type == MapleInventoryType.EQUIP)) {
            possesed += this.inventory[MapleInventoryType.EQUIPPED.ordinal()].countById(itemid);
        }
        if (greaterOrEquals) {
            return possesed >= quantity;
        }
        return possesed == quantity;
    }

    public boolean haveItem(int itemid, int quantity) {
        return haveItem(itemid, quantity, true, true);
    }

    public boolean haveItem(int itemid) {
        return haveItem(itemid, 1, true, true);
    }

    public int getItemQuantity(int itemid) {
        MapleInventoryType type = GameConstants.getInventoryType(itemid);
        return getInventory(type).countById(itemid);
    }

    public int getItemQuantity(int itemid, boolean checkEquipped) {
        int possesed = this.inventory[GameConstants.getInventoryType(itemid).ordinal()].countById(itemid);
        if (checkEquipped) {
            possesed += this.inventory[MapleInventoryType.EQUIPPED.ordinal()].countById(itemid);
        }
        return possesed;
    }

    public int getEquipId(byte slot) {
        MapleInventory equip = getInventory(MapleInventoryType.EQUIP);
        return equip.getItem((short) slot).getItemId();
    }

    public int getUseId(byte slot) {
        MapleInventory use = getInventory(MapleInventoryType.USE);
        return use.getItem((short) slot).getItemId();
    }

    public int getSetupId(byte slot) {
        MapleInventory setup = getInventory(MapleInventoryType.SETUP);
        return setup.getItem((short) slot).getItemId();
    }

    public int getCashId(byte slot) {
        MapleInventory cash = getInventory(MapleInventoryType.CASH);
        return cash.getItem((short) slot).getItemId();
    }

    public int getEtcId(byte slot) {
        MapleInventory etc = getInventory(MapleInventoryType.ETC);
        return etc.getItem((short) slot).getItemId();
    }

    public byte getBuddyCapacity() {
        return this.buddylist.getCapacity();
    }

    public void setBuddyCapacity(byte capacity) {
        this.buddylist.setCapacity(capacity);
        this.client.getSession().write(BuddyListPacket.updateBuddyCapacity(capacity));
    }

    public MapleMessenger getMessenger() {
        return this.messenger;
    }

    public void setMessenger(MapleMessenger messenger) {
        this.messenger = messenger;
    }

    public void addCooldown(int skillId, long startTime, long length) {
        this.coolDowns.put(Integer.valueOf(skillId), new MapleCoolDownValueHolder(skillId, startTime, length));
    }

    public void removeCooldown(int skillId) {
        if (this.coolDowns.containsKey(Integer.valueOf(skillId))) {
            this.coolDowns.remove(Integer.valueOf(skillId));
        }
    }

    public boolean skillisCooling(int skillId) {
        return this.coolDowns.containsKey(Integer.valueOf(skillId));
    }

    public void giveCoolDowns(int skillid, long starttime, long length) {
        addCooldown(skillid, starttime, length);
    }

    public void giveCoolDowns(List<MapleCoolDownValueHolder> cooldowns) {
        if (cooldowns != null) {
            for (MapleCoolDownValueHolder cooldown : cooldowns) {
                this.coolDowns.put(Integer.valueOf(cooldown.skillId), cooldown);
            }
        } else {
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement("SELECT SkillID,StartTime,length FROM skills_cooldowns WHERE charid = ?");
                ps.setInt(1, getId());
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    if (rs.getLong("length") + rs.getLong("StartTime") - System.currentTimeMillis() <= 0L) {
                        continue;
                    }
                    giveCoolDowns(rs.getInt("SkillID"), rs.getLong("StartTime"), rs.getLong("length"));
                }
                ps.close();
                rs.close();
                deleteWhereCharacterId(con, "DELETE FROM skills_cooldowns WHERE charid = ?");
            } catch (SQLException e) {
                System.err.println("Error while retriving cooldown from SQL storage");
            }
        }
    }

    public int getCooldownSize() {
        return this.coolDowns.size();
    }

    public int getDiseaseSize() {
        return this.diseases.size();
    }

    public List<MapleCoolDownValueHolder> getCooldowns() {
        List ret = new ArrayList();
        for (MapleCoolDownValueHolder mc : this.coolDowns.values()) {
            if (mc != null) {
                ret.add(mc);
            }
        }
        return ret;
    }

    public List<MapleDiseaseValueHolder> getAllDiseases() {
        return new ArrayList(this.diseases.values());
    }

    public boolean hasDisease(MapleDisease dis) {
        return this.diseases.containsKey(dis);
    }

    public void giveDebuff(MapleDisease disease, MobSkill skill) {
        giveDebuff(disease, skill.getX(), skill.getDuration(), skill.getSkillId(), skill.getSkillLevel());
    }

    public void giveDebuff(MapleDisease disease, int x, long duration, int skillid, int level) {
        if ((this.map != null) && (!hasDisease(disease))) {
            if ((disease != MapleDisease.SEDUCE) && (disease != MapleDisease.STUN) && (disease != MapleDisease.FLAG)
                    && (getBuffedValue(MapleBuffStat.进阶祝福) != null)) {
                return;
            }

            int mC = getBuffSource(MapleBuffStat.金属机甲);
            if ((mC > 0) && (mC != 35121005)) {
                return;
            }
            if ((this.stats.ASR > 0) && (Randomizer.nextInt(100) < this.stats.ASR)) {
                return;
            }
            this.diseases.put(disease, new MapleDiseaseValueHolder(disease, System.currentTimeMillis(), duration - this.stats.decreaseDebuff));
            this.client.getSession().write(MaplePacketCreator.giveDebuff(disease, x, skillid, level, (int) duration));
            this.map.broadcastMessage(this, MaplePacketCreator.giveForeignDebuff(this.id, disease, skillid, level, x), false);
            if ((x > 0) && (disease == MapleDisease.POISON)) {
                addHP((int) (-(x * ((duration - this.stats.decreaseDebuff) / 1000L))));
            }
        }
    }

    public void giveSilentDebuff(List<MapleDiseaseValueHolder> ld) {
        if (ld != null) {
            for (MapleDiseaseValueHolder disease : ld) {
                this.diseases.put(disease.disease, disease);
            }
        }
    }

    public void dispelDebuff(MapleDisease debuff) {
        if (hasDisease(debuff)) {
            this.client.getSession().write(MaplePacketCreator.cancelDebuff(debuff));
            this.map.broadcastMessage(this, MaplePacketCreator.cancelForeignDebuff(this.id, debuff), false);
            this.diseases.remove(debuff);
        }
    }

    public void dispelDebuffs() {
        List<MapleDisease> diseasess = new ArrayList<MapleDisease>(this.diseases.keySet());
        for (MapleDisease d : diseasess) {
            dispelDebuff(d);
        }
    }

    public void cancelAllDebuffs() {
        this.diseases.clear();
    }

    public void setLevel(short level) {
        this.level = (short) (level - 1);
    }

    public void setMaxhp(int maxhp) {
        this.stats.maxhp = (int) maxhp;
    }

    public void setMaxmp(int maxmp) {
        this.stats.maxmp = (int) maxmp;
    }

    public void setMaxhp1(int maxhp) {
        this.stats.maxhp = (int) (maxhp - 1);
    }

    public void setMaxmp1(int maxmp) {
        this.stats.maxmp = (int) (maxmp - 1);
    }

    public int getskillidbytypedel(int type, int skillid) {
        int rets = -1;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM phantomskills WHERE characterid=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getInt("skill" + type + "_0") == skillid) {
                    rs.close();
                    ps.close();
                    return 0;
                }
                if (rs.getInt("skill" + type + "_1") == skillid) {
                    rs.close();
                    ps.close();
                    return 1;
                }
                if (type < 4) {
                    if (rs.getInt("skill" + type + "_2") == skillid) {
                        rs.close();
                        ps.close();
                        return 2;
                    }
                    if (type < 3) {
                        if (rs.getInt("skill" + type + "_3") == skillid) {
                            rs.close();
                            ps.close();
                            return 3;
                        }
                    }
                }
            }
        } catch (SQLException e) {
        }
        return rets;
    }

    public void sendNote(String to, String msg) {
        sendNote(to, msg, 0);
    }

    public void sendNote(String to, String msg, int fame) {
        MapleCharacterUtil.sendNote(to, getName(), msg, fame);
    }

    public void showNote() {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM notes WHERE `to`=?", 1005, 1008);
            ps.setString(1, getName());
            ResultSet rs = ps.executeQuery();
            rs.last();
            int count = rs.getRow();
            rs.first();
            this.client.getSession().write(MTSCSPacket.showNotes(rs, count));
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println(new StringBuilder().append("Unable to show note").append(e).toString());
        }
    }

    public void deleteNote(int id, int fame) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT gift FROM notes WHERE `id`=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if ((rs.next())
                    && (rs.getInt("gift") == fame) && (fame > 0)) {
                addFame(fame);
                updateSingleStat(MapleStat.人气, getFame());
                this.client.getSession().write(MaplePacketCreator.getShowFameGain(fame));
            }

            rs.close();
            ps.close();
            ps = con.prepareStatement("DELETE FROM notes WHERE `id`=?");
            ps.setInt(1, id);
            ps.execute();
            ps.close();
        } catch (SQLException e) {
            System.err.println(new StringBuilder().append("Unable to delete note").append(e).toString());
        }
    }

    public int getMulungEnergy() {
        return this.mulung_energy;
    }

    public void mulung_EnergyModify(boolean inc) {
        if (inc) {
            if (this.mulung_energy + 100 > 10000) {
                this.mulung_energy = 10000;
            } else {
                this.mulung_energy = (short) (this.mulung_energy + 100);
            }
        } else {
            this.mulung_energy = 0;
        }
        this.client.getSession().write(MaplePacketCreator.MulungEnergy(this.mulung_energy));
    }

    public void writeMulungEnergy() {
        this.client.getSession().write(MaplePacketCreator.MulungEnergy(this.mulung_energy));
    }

    public void writeEnergy(String type, String inc) {
        this.client.getSession().write(MaplePacketCreator.sendPyramidEnergy(type, inc));
    }

    public void writeStatus(String type, String inc) {
        this.client.getSession().write(MaplePacketCreator.sendGhostStatus(type, inc));
    }

    public void writePoint(String type, String inc) {
        this.client.getSession().write(MaplePacketCreator.sendGhostPoint(type, inc));
    }

    public short getCombo() {
        return this.combo;
    }

    public void setCombo(short combo) {
        this.combo = combo;
    }

    public long getLastCombo() {
        return this.lastCombo;
    }

    public void setLastCombo(long combo) {
        this.lastCombo = combo;
    }

    public long getKeyDownSkill_Time() {
        return this.keydown_skill;
    }

    public void setKeyDownSkill_Time(long keydown_skill) {
        this.keydown_skill = keydown_skill;
    }

    public void checkBerserk() {
        if ((this.job != 132) || (this.lastBerserkTime < 0L) || (this.lastBerserkTime + 10000L > System.currentTimeMillis())) {
            return;
        }
        Skill BerserkX = SkillFactory.getSkill(1320006);
        int skilllevel = getTotalSkillLevel(BerserkX);
        if ((skilllevel >= 1) && (this.map != null)) {
            this.lastBerserkTime = System.currentTimeMillis();
            MapleStatEffect ampStat = BerserkX.getEffect(skilllevel);
            this.stats.Berserk = (this.stats.getHp() * 100 / this.stats.getCurrentMaxHp() >= ampStat.getX());
            this.client.getSession().write(MaplePacketCreator.showOwnBuffEffect(1320006, 1, getLevel(), skilllevel, (byte) (this.stats.Berserk ? 1 : 0)));
            this.map.broadcastMessage(this, MaplePacketCreator.showBuffeffect(getId(), 1320006, 1, getLevel(), skilllevel, (byte) (this.stats.Berserk ? 1 : 0)), false);
        } else {
            this.lastBerserkTime = -1L;
        }
    }

    public void setChalkboard(String text) {
        this.chalktext = text;
        if (this.map != null) {
            this.map.broadcastMessage(MTSCSPacket.useChalkboard(getId(), text));
        }
    }

    public String getChalkboard() {
        return this.chalktext;
    }

    public MapleMount getMount() {
        return this.mount;
    }

    public int[] getWishlist() {
        return this.wishlist;
    }

    public void clearWishlist() {
        for (int i = 0; i < 10; i++) {
            this.wishlist[i] = 0;
        }
        this.changed_wishlist = true;
    }

    public int getWishlistSize() {
        int ret = 0;
        for (int i = 0; i < 10; i++) {
            if (this.wishlist[i] > 0) {
                ret++;
            }
        }
        return ret;
    }

    public void setWishlist(int[] wl) {
        this.wishlist = wl;
        this.changed_wishlist = true;
    }

    public int[] getRocks() {
        return this.rocks;
    }

    public int getRockSize() {
        int ret = 0;
        for (int i = 0; i < 10; i++) {
            if (this.rocks[i] != 999999999) {
                ret++;
            }
        }
        return ret;
    }

    public void deleteFromRocks(int map) {
        for (int i = 0; i < 10; i++) {
            if (this.rocks[i] == map) {
                this.rocks[i] = 999999999;
                this.changed_trocklocations = true;
                break;
            }
        }
    }

    public void addRockMap() {
        if (getRockSize() >= 10) {
            return;
        }
        this.rocks[getRockSize()] = getMapId();
        this.changed_trocklocations = true;
    }

    public boolean isRockMap(int id) {
        for (int i = 0; i < 10; i++) {
            if (this.rocks[i] == id) {
                return true;
            }
        }
        return false;
    }

    public int[] getRegRocks() {
        return this.regrocks;
    }

    public int getRegRockSize() {
        int ret = 0;
        for (int i = 0; i < 5; i++) {
            if (this.regrocks[i] != 999999999) {
                ret++;
            }
        }
        return ret;
    }

    public void deleteFromRegRocks(int map) {
        for (int i = 0; i < 5; i++) {
            if (this.regrocks[i] == map) {
                this.regrocks[i] = 999999999;
                this.changed_trocklocations = true;
                break;
            }
        }
    }

    public void addRegRockMap() {
        if (getRegRockSize() >= 5) {
            return;
        }
        this.regrocks[getRegRockSize()] = getMapId();
        this.changed_trocklocations = true;
    }

    public boolean isRegRockMap(int id) {
        for (int i = 0; i < 5; i++) {
            if (this.regrocks[i] == id) {
                return true;
            }
        }
        return false;
    }

    public int[] getHyperRocks() {
        return this.hyperrocks;
    }

    public int getHyperRockSize() {
        int ret = 0;
        for (int i = 0; i < 13; i++) {
            if (this.hyperrocks[i] != 999999999) {
                ret++;
            }
        }
        return ret;
    }

    public void deleteFromHyperRocks(int map) {
        for (int i = 0; i < 13; i++) {
            if (this.hyperrocks[i] == map) {
                this.hyperrocks[i] = 999999999;
                this.changed_trocklocations = true;
                break;
            }
        }
    }

    public void addHyperRockMap() {
        if (getRegRockSize() >= 13) {
            return;
        }
        this.hyperrocks[getHyperRockSize()] = getMapId();
        this.changed_trocklocations = true;
    }

    public boolean isHyperRockMap(int id) {
        for (int i = 0; i < 13; i++) {
            if (this.hyperrocks[i] == id) {
                return true;
            }
        }
        return false;
    }

    public List<LifeMovementFragment> getLastRes() {
        return this.lastres;
    }

    public void setLastRes(List<LifeMovementFragment> lastres) {
        this.lastres = lastres;
    }

    public void dropMessage(int type, String message) {
        if (type == -1) {
            this.client.getSession().write(UIPacket.getTopMsg(message));
        } else if (type == -2) {
            this.client.getSession().write(PlayerShopPacket.shopChat(message, 0));
        } else if (type == -3) {
            this.client.getSession().write(MaplePacketCreator.getChatText(getId(), message, isSuperGM(), 0));
        } else if (type == -4) {
            this.client.getSession().write(MaplePacketCreator.getChatText(getId(), message, isSuperGM(), 1));
        } else if (type == -5) {
            this.client.getSession().write(MaplePacketCreator.spouseMessage(message, false));
        } else if (type == -6) {
            this.client.getSession().write(MaplePacketCreator.spouseMessage(message, true));
        } else if (type == -7) {
            this.client.getSession().write(UIPacket.getMidMsg(message, false, 0));
        } else if (type == -8) {
            this.client.getSession().write(UIPacket.getMidMsg(message, true, 0));
        } else if (type == -9) {
            this.client.getSession().write(MaplePacketCreator.showQuestMessage(message));
        } else if (type == -10) {
            this.client.getSession().write(MaplePacketCreator.getFollowMessage(message));
        } else if (type == -11) {
            this.client.getSession().write(MaplePacketCreator.yellowChat(message));
        } else {
            this.client.getSession().write(MaplePacketCreator.serverNotice(type, message));
        }
    }

    public IMaplePlayerShop getPlayerShop() {
        return this.playerShop;
    }

    public void setPlayerShop(IMaplePlayerShop playerShop) {
        this.playerShop = playerShop;
    }

    public int getConversation() {
        return this.inst.get();
    }

    public void setConversation(int inst) {
        this.inst.set(inst);
    }

    public int getDirection() {
        return this.insd.get();
    }

    public void setDirection(int inst) {
        this.insd.set(inst);
    }

    public MapleCarnivalParty getCarnivalParty() {
        return this.carnivalParty;
    }

    public void setCarnivalParty(MapleCarnivalParty party) {
        this.carnivalParty = party;
    }

    public void addCP(int ammount) {
        this.totalCP = (short) (this.totalCP + ammount);
        this.availableCP = (short) (this.availableCP + ammount);
    }

    public void useCP(int ammount) {
        this.availableCP = (short) (this.availableCP - ammount);
    }

    public int getAvailableCP() {
        return this.availableCP;
    }

    public int getTotalCP() {
        return this.totalCP;
    }

    public void resetCP() {
        this.totalCP = 0;
        this.availableCP = 0;
    }

    public void addCarnivalRequest(MapleCarnivalChallenge request) {
        this.pendingCarnivalRequests.add(request);
    }

    public final MapleCarnivalChallenge getNextCarnivalRequest() {
        return (MapleCarnivalChallenge) this.pendingCarnivalRequests.pollLast();
    }

    public void clearCarnivalRequests() {
        this.pendingCarnivalRequests = new LinkedList();
    }

    public void startMonsterCarnival(int enemyavailable, int enemytotal) {
        this.client.getSession().write(MonsterCarnivalPacket.startMonsterCarnival(this, enemyavailable, enemytotal));
    }

    public void CPUpdate(boolean party, int available, int total, int team) {
        this.client.getSession().write(MonsterCarnivalPacket.CPUpdate(party, available, total, team));
    }

    public void playerDiedCPQ(String name, int lostCP, int team) {
        this.client.getSession().write(MonsterCarnivalPacket.playerDiedMessage(name, lostCP, team));
    }

    public void setAchievementFinished(int id) {
        if (!this.finishedAchievements.contains(Integer.valueOf(id))) {
            this.finishedAchievements.add(Integer.valueOf(id));
            this.changed_achievements = true;
        }
    }

    public boolean achievementFinished(int achievementid) {
        return this.finishedAchievements.contains(Integer.valueOf(achievementid));
    }

    //成就系统 鉴定出装备等级
    public void finishAchievement(int id) {
        if ((!achievementFinished(id))
                && (isAlive()) && (!isClone())) {
            MapleAchievements.getInstance().getById(id).finishAchievement(this);
        }
    }

    public List<Integer> getFinishedAchievements() {
        return this.finishedAchievements;
    }

    public boolean getCanTalk() {
        return this.canTalk;
    }

    public void canTalk(boolean talk) {
        this.canTalk = talk;
    }

    public double getEXPMod() {
        return hasEXPCard();
    }

    public double hasEXPCard() {
        int[] expCards = {5210000, 5210001, 5210002, 5210003, 5210004, 5210005, 5210006, 5211047, 5211060, 5211000, 5211001, 5211002, 5211063, 5211064, 5211065, 5211066, 5211069, 5211070};

        MapleInventory iv = getInventory(MapleInventoryType.CASH);
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        double canuse = 1.0D;
        int[] arr$ = expCards;
        int len$ = arr$.length;
        for (int i$ = 0; i$ < len$; i$++) {
            Integer ids = Integer.valueOf(arr$[i$]);
            if ((iv.countById(ids.intValue()) <= 0)
                    || (!ii.isExpOrDropCardTime(ids.intValue()))) {
                continue;
            }
            switch (ids.intValue()) {
                case 5210000:
                case 5210001:
                case 5210002:
                case 5210003:
                case 5210004:
                case 5210005:
                case 5210006:
                case 5211000:
                case 5211001:
                case 5211002:
                case 5211047:
                    canuse = 2.0D;
                    break;
                case 5211060:
                    canuse = 3.0D;
                    break;
                case 5211063:
                case 5211064:
                case 5211065:
                case 5211066:
                case 5211069:
                case 5211070:
                    canuse = 1.5D;
            }

        }

        return canuse;
    }

    public int getDropMod() {
        return hasDropCard();
    }

    public int hasDropCard() {
        int[] dropCards = {5360000, 5360014, 5360015, 5360016};
        MapleInventory iv = getInventory(MapleInventoryType.CASH);
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        int[] arr$ = dropCards;
        int len$ = arr$.length;
        for (int i$ = 0; i$ < len$; i$++) {
            Integer id3 = Integer.valueOf(arr$[i$]);
            if ((iv.countById(id3.intValue()) > 0)
                    && (ii.isExpOrDropCardTime(id3.intValue()))) {
                return 2;
            }
        }

        return 1;
    }

    public int getCashMod() {
        return this.stats.cashMod;
    }

    public void setPoints(int p) {
        this.points = p;
        if (this.points >= 1) {
            finishAchievement(1);
        }
    }

    public int getPoints() {
        return this.points;
    }

    public void setVPoints(int p) {
        this.vpoints = p;
    }

    public int getVPoints() {
        return this.vpoints;
    }

    public CashShop getCashInventory() {
        return this.cs;
    }

    public void removeItem(int id, int quantity) {
        MapleInventoryManipulator.removeById(this.client, GameConstants.getInventoryType(id), id, quantity, true, false);
        this.client.getSession().write(MaplePacketCreator.getShowItemGain(id, (short) quantity, true));
    }

    public void removeAll(int id) {
        removeAll(id, true, false);
    }

    public void removeAll(int itemId, boolean show, boolean checkEquipped) {
        MapleInventoryType type = GameConstants.getInventoryType(itemId);
        int possessed = getInventory(type).countById(itemId);
        if (possessed > 0) {
            MapleInventoryManipulator.removeById(getClient(), type, itemId, possessed, true, false);
            if (show) {
                getClient().getSession().write(MaplePacketCreator.getShowItemGain(itemId, (short) (-possessed), true));
            }
        }
        if ((checkEquipped) && (type == MapleInventoryType.EQUIP)) {
            type = MapleInventoryType.EQUIPPED;
            possessed = getInventory(type).countById(itemId);
            if (possessed > 0) {
                MapleInventoryManipulator.removeById(getClient(), type, itemId, possessed, true, false);
                if (show) {
                    getClient().getSession().write(MaplePacketCreator.getShowItemGain(itemId, (short) (-possessed), true));
                }
                equipChanged();
            }
        }
    }

    public void removeItem(int itemId) {
        MapleInventoryType type = GameConstants.getInventoryType(itemId);
        if (type == MapleInventoryType.EQUIP) {
            type = MapleInventoryType.EQUIPPED;
            int possessed = getInventory(type).countById(itemId);
            if (possessed > 0) {
                MapleInventoryManipulator.removeById(getClient(), type, itemId, possessed, true, false);
                getClient().getSession().write(MaplePacketCreator.getShowItemGain(itemId, (short) (-possessed), true));
            }
        }
    }

    public Triple<List<MapleRing>, List<MapleRing>, List<MapleRing>> getRings(boolean equip) {
        MapleInventory iv = getInventory(MapleInventoryType.EQUIPPED);
        List<Item> equipped = iv.newList();
        Collections.sort(equipped);
        List crings = new ArrayList();
        List frings = new ArrayList();
        List mrings = new ArrayList();

        for (Item ite : equipped) {
            Equip item = (Equip) ite;
            if (item.getRing() != null) {
                MapleRing ring = item.getRing();
                ring.setEquipped(true);
                if (GameConstants.isEffectRing(item.getItemId())) {
                    if (equip) {
                        if (GameConstants.is恋人戒指(item.getItemId())) {
                            crings.add(ring);
                        } else if (GameConstants.is好友戒指(item.getItemId())) {
                            frings.add(ring);
                        } else if (GameConstants.is结婚戒指(item.getItemId())) {
                            mrings.add(ring);
                        }
                    } else if ((crings.isEmpty()) && (GameConstants.is恋人戒指(item.getItemId()))) {
                        crings.add(ring);
                    } else if ((frings.isEmpty()) && (GameConstants.is好友戒指(item.getItemId()))) {
                        frings.add(ring);
                    } else if ((mrings.isEmpty()) && (GameConstants.is结婚戒指(item.getItemId()))) {
                        mrings.add(ring);
                    }
                }
            }

        }

        if (equip) {
            iv = getInventory(MapleInventoryType.EQUIP);
            for (Item ite : iv.list()) {
                Equip item = (Equip) ite;
                if ((item.getRing() != null) && (GameConstants.is恋人戒指(item.getItemId()))) {
                    MapleRing ring = item.getRing();
                    ring.setEquipped(false);
                    if (GameConstants.is好友戒指(item.getItemId())) {
                        frings.add(ring);
                    } else if (GameConstants.is恋人戒指(item.getItemId())) {
                        crings.add(ring);
                    } else if (GameConstants.is结婚戒指(item.getItemId())) {
                        mrings.add(ring);
                    }
                }
            }
        }
        Collections.sort(frings, new MapleRing.RingComparator());
        Collections.sort(crings, new MapleRing.RingComparator());
        Collections.sort(mrings, new MapleRing.RingComparator());
        return new Triple(crings, frings, mrings);
    }

    public int getFH() {
        MapleFoothold fh = getMap().getFootholds().findBelow(getTruePosition());
        if (fh != null) {
            return fh.getId();
        }
        return 0;
    }

    public void startFairySchedule(boolean exp) {
        startFairySchedule(exp, false);
    }

    public void startFairySchedule(boolean exp, boolean equipped) {
        cancelFairySchedule((exp) || (this.stats.equippedFairy == 0));
        if (this.fairyExp <= 0) {
            this.fairyExp = (byte) this.stats.equippedFairy;
        }
        if ((equipped) && (this.fairyExp < this.stats.equippedFairy * 3) && (this.stats.equippedFairy > 0)) {
            dropMessage(5, new StringBuilder().append("您装备了精灵吊坠在1小时后经验获取将增加到 ").append(this.fairyExp + this.stats.equippedFairy).append(" %.").toString());
        }
        this.lastFairyTime = System.currentTimeMillis();
    }

    public boolean canFairy(long now) {
        return (this.lastFairyTime > 0) && (this.lastFairyTime + 3600000L < now);
    }

    public boolean canHP(long now) {
        if (this.lastHPTime + 5000L < now) {
            this.lastHPTime = now;
            return true;
        }
        return false;
    }

    public boolean canMP(long now) {
        if (this.lastMPTime + 5000L < now) {
            this.lastMPTime = now;
            return true;
        }
        return false;
    }

    public boolean canHPRecover(long now) {
        if ((this.stats.hpRecoverTime > 0) && (this.lastHPTime + this.stats.hpRecoverTime < now)) {
            this.lastHPTime = now;
            return true;
        }
        return false;
    }

    public boolean canMPRecover(long now) {
        if ((this.stats.mpRecoverTime > 0) && (this.lastMPTime + this.stats.mpRecoverTime < now)) {
            this.lastMPTime = now;
            return true;
        }
        return false;
    }

    public void cancelFairySchedule(boolean exp) {
        this.lastFairyTime = 0L;
        if (exp) {
            this.fairyExp = 0;
        }
    }

    public void doFairy() {
        if ((this.fairyExp < this.stats.equippedFairy * 3) && (this.stats.equippedFairy > 0)) {
            this.fairyExp = (byte) (this.fairyExp + this.stats.equippedFairy);
            dropMessage(5, new StringBuilder().append("精灵吊坠经验获取增加到 ").append(this.fairyExp).append(" %.").toString());
        }
        if (getGuildId() > 0) {
            World.Guild.gainGP(getGuildId(), 20, this.id);
            this.client.getSession().write(UIPacket.getGPContribution(20));
        }
        ((MapleTrait) this.traits.get(MapleTrait.MapleTraitType.will)).addExp(5, this);
        startFairySchedule(false, true);
    }

    public byte getFairyExp() {
        return this.fairyExp;
    }

    public int getTeam() {
        return this.coconutteam;
    }

    public void setTeam(int v) {
        this.coconutteam = v;
    }

    public void spawnPet(byte slot) {
        spawnPet(slot, false, true);
    }

    public void spawnPet(byte slot, boolean lead) {
        spawnPet(slot, lead, true);
    }

    public void spawnPet(byte slot, boolean lead, boolean broadcast) {
        Item item = getInventory(MapleInventoryType.CASH).getItem((short) slot);
        if ((item == null) || (item.getItemId() > 5000300) || (item.getItemId() < 5000000)) {
            this.client.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        switch (item.getItemId()) {
            case 5000028:
            case 5000047: {
                final MaplePet pet = MaplePet.createPet(item.getItemId() + 1, MapleInventoryIdentifier.getInstance());
                if (pet == null) {
                    break;
                }
                MapleInventoryManipulator.addById(this.client, item.getItemId() + 1, (short) 1, item.getOwner(), pet, 45L, new StringBuilder().append("双击宠物获得: ").append(item.getItemId()).append(" 时间: ").append(FileoutputUtil.CurrentReadable_Date()).toString());
                MapleInventoryManipulator.removeFromSlot(this.client, MapleInventoryType.CASH, (short) slot, (short) 1, false);
                break;
            }
            default: {
                final MaplePet pet = item.getPet();
                if ((pet == null) || ((item.getItemId() == 5000054) && (pet.getSecondsLeft() <= 0)) || ((item.getExpiration() != -1L) && (item.getExpiration() <= System.currentTimeMillis()))) {
                    break;
                }
                if (pet.getSummoned()) {
                    unequipPet(pet, true, false);
                } else {
                    int leadid = 8;
                    if (GameConstants.is骑士团(getJob())) {
                        leadid = 10000018;
                    } else if (GameConstants.is战神(getJob())) {
                        leadid = 20000024;
                    } else if (GameConstants.is龙神(getJob())) {
                        leadid = 20011024;
                    }
                    if ((getSkillLevel(SkillFactory.getSkill(leadid)) == 0) && (getPet(0) != null)) {//判断是否有群宠技能
                        unequipPet(getPet(0), false, false);
                    } else if ((!lead) || (getSkillLevel(SkillFactory.getSkill(leadid)) <= 0)) ;

                    Point pos = getPosition();
                    pet.setPos(pos);
                    try {
                        pet.setFh(getMap().getFootholds().findBelow(pos).getId());
                    } catch (NullPointerException e) {
                        pet.setFh(0);
                    }
                    pet.setStance(0);
                    pet.setSummoned(1);
                    if (getSkillLevel(pet.getBuffSkill()) == 0) {
                        pet.setBuffSkill(0);
                    }
                    addPet(pet);
                    pet.setSummoned(getPetIndex(pet) + 1);
                    if ((broadcast) && (getMap() != null)) {
                        getMap().broadcastMessage(this, PetPacket.showPet(this, pet, false, false), true);
                        this.client.getSession().write(PetPacket.updatePet(pet, getInventory(MapleInventoryType.CASH).getItem(pet.getInventoryPosition()), true));
                        this.client.getSession().write(PetPacket.loadExceptionList(this, pet.getUniqueId(), (byte) (pet.getSummonedValue() - 1)));
                    }
                }
                break;
            }
        }

        this.client.getSession().write(MaplePacketCreator.enableActions());
    }

    public void clearLinkMid() {
        this.linkMobs.clear();
        cancelEffectFromBuffStat(MapleBuffStat.导航辅助);
        cancelEffectFromBuffStat(MapleBuffStat.神秘瞄准术);
    }

    public int getFirstLinkMid() {
        Iterator i$ = this.linkMobs.keySet().iterator();
        if (i$.hasNext()) {
            Integer lm = (Integer) i$.next();
            return lm.intValue();
        }
        return 0;
    }

    public Map<Integer, Integer> getAllLinkMid() {
        return this.linkMobs;
    }

    public void setLinkMid(int lm, int x) {
        this.linkMobs.put(Integer.valueOf(lm), Integer.valueOf(x));
    }

    public int getDamageIncrease(int lm) {
        if (this.linkMobs.containsKey(Integer.valueOf(lm))) {
            return ((Integer) this.linkMobs.get(Integer.valueOf(lm))).intValue();
        }
        return 0;
    }

    public boolean isClone() {
        return this.clone;
    }

    public void setClone(boolean c) {
        this.clone = c;
    }

    public WeakReference<MapleCharacter>[] getClones() {
        return this.clones;
    }

    public MapleCharacter cloneLooks() {
        MapleClient c = new MapleClient(null, null, null);
        int minus = getId() + Randomizer.nextInt(2147483647 - getId());
        MapleCharacter ret = new MapleCharacter(true);
        ret.id = minus;
        ret.client = c;
        ret.exp = 0;
        ret.meso = 0;
        ret.remainingAp = 0;
        ret.fame = 0;
        ret.accountid = this.client.getAccID();
        ret.anticheat = this.anticheat;
        ret.name = this.name;
        ret.level = this.level;
        ret.fame = this.fame;
        ret.job = this.job;
        ret.hair = this.hair;
        ret.face = this.face;
        ret.skinColor = this.skinColor;
        ret.monsterbook = this.monsterbook;
        ret.mount = this.mount;
        ret.gmLevel = this.gmLevel;
        ret.gender = this.gender;
        ret.mapid = this.map.getId();
        ret.map = this.map;
        ret.setStance(getStance());
        ret.chair = this.chair;
        ret.itemEffect = this.itemEffect;
        ret.titleEffect = this.titleEffect;
        ret.guildid = this.guildid;
        ret.currentrep = this.currentrep;
        ret.totalrep = this.totalrep;
        ret.stats = this.stats;
        ret.effects.putAll(this.effects);
        ret.dispelSummons();
        ret.guildrank = this.guildrank;
        ret.guildContribution = this.guildContribution;
        ret.allianceRank = this.allianceRank;
        ret.setPosition(getTruePosition());
        for (Item equip : getInventory(MapleInventoryType.EQUIPPED).newList()) {
            ret.getInventory(MapleInventoryType.EQUIPPED).addFromDB(equip.copy());
        }
        ret.skillMacros = this.skillMacros;
        ret.keylayout = this.keylayout;
        ret.questinfo = this.questinfo;
        ret.savedLocations = this.savedLocations;
        ret.wishlist = this.wishlist;
        ret.buddylist = this.buddylist;
        ret.keydown_skill = 0L;
        ret.lastmonthfameids = this.lastmonthfameids;
        ret.lastfametime = this.lastfametime;
        ret.storage = this.storage;
        ret.cs = this.cs;
        ret.client.setAccountName(this.client.getAccountName());
        ret.acash = this.acash;
        ret.maplepoints = this.maplepoints;
        ret.clone = true;
        ret.client.setChannel(this.client.getChannel());
        while ((this.map.getCharacterById(ret.id) != null) || (this.client.getChannelServer().getPlayerStorage().getCharacterById(ret.id) != null)) {
            ret.id += 1;
        }
        ret.client.setPlayer(ret);
        return ret;
    }

    public void cloneLook() {
        if ((this.clone) || (inPVP())) {
            return;
        }
        for (int i = 0; i < this.clones.length; i++) {
            if (this.clones[i].get() == null) {
                MapleCharacter newp = cloneLooks();
                this.map.addPlayer(newp);
                this.map.broadcastMessage(MaplePacketCreator.updateCharLook(newp));
                this.map.movePlayer(newp, getTruePosition());
                this.clones[i] = new WeakReference(newp);
                return;
            }
        }
    }

    public void disposeClones() {
        this.numClones = 0;
        for (int i = 0; i < this.clones.length; i++) {
            if (this.clones[i].get() != null) {
                this.map.removePlayer((MapleCharacter) this.clones[i].get());
                if (((MapleCharacter) this.clones[i].get()).getClient() != null) {
                    ((MapleCharacter) this.clones[i].get()).getClient().setPlayer(null);
                    ((MapleCharacter) this.clones[i].get()).client = null;
                }
                this.clones[i] = new WeakReference(null);
                this.numClones = (byte) (this.numClones + 1);
            }
        }
    }

    public int getCloneSize() {
        int z = 0;
        for (int i = 0; i < this.clones.length; i++) {
            if (this.clones[i].get() != null) {
                z++;
            }
        }
        return z;
    }

    public void spawnClones() {
        if (!isGM()) {
            this.numClones = (byte) (this.stats.hasClone ? 1 : 0);
        }
        for (int i = 0; i < this.numClones; i++) {
            cloneLook();
        }
        this.numClones = 0;
    }

    public byte getNumClones() {
        return this.numClones;
    }

    public void setDragon(MapleDragon d) {
        this.dragon = d;
    }

    public MapleExtractor getExtractor() {
        return this.extractor;
    }

    public void setExtractor(MapleExtractor me) {
        removeExtractor();
        this.extractor = me;
    }

    public void removeExtractor() {
        if (this.extractor != null) {
            this.map.broadcastMessage(MaplePacketCreator.removeExtractor(this.id));
            this.map.removeMapObject(this.extractor);
            this.extractor = null;
        }
    }

    public void spawnSavedPets() {
        for (int i = 0; i < this.petStore.length; i++) {
            if (this.petStore[i] > -1) {
                spawnPet(this.petStore[i], false, false);
            }
        }

        this.petStore = new byte[]{-1, -1, -1};
    }

    public byte[] getPetStores() {
        return this.petStore;
    }

    public void resetChongxiusx1(int level, int str, int dex, int int_, int luk, int maxhp, int HP, int maxmp, int MP) {
        Map stat = new EnumMap(MapleStat.class);
        int total = this.stats.getStr() + this.stats.getDex() + this.stats.getLuk() + this.stats.getInt() + getRemainingAp();

        total -= level;
        this.stats.level = (short) level;

        total -= str;
        this.stats.str = (short) str;

        total -= dex;
        this.stats.dex = (short) dex;

        total -= int_;
        this.stats.int_ = (short) int_;

        total -= luk;
        this.stats.luk = (short) luk;

        total -= maxhp;
        this.stats.maxhp = (int) maxhp;

        total -= HP;
        this.stats.hp = (int) HP;

        total -= maxmp;
        this.stats.maxmp = (int) maxmp;

        total -= MP;
        this.stats.mp = (int) MP;

        setRemainingAp((short) total);
        this.stats.recalcLocalStats(this);
        stat.put(MapleStat.等级, level);
        stat.put(MapleStat.力量, str);
        stat.put(MapleStat.敏捷, dex);
        stat.put(MapleStat.智力, int_);
        stat.put(MapleStat.运气, luk);
        stat.put(MapleStat.MAXHP, maxhp);
        stat.put(MapleStat.HP, HP);
        stat.put(MapleStat.MAXMP, maxmp);
        stat.put(MapleStat.MP, MP);
        stat.put(MapleStat.AVAILABLEAP, total);
        this.client.getSession().write(MaplePacketCreator.updatePlayerStats(stat, false, this));
    }

    public void resetStats(int str, int dex, int int_, int luk) {
        Map stat = new EnumMap(MapleStat.class);
        int total = this.stats.getStr() + this.stats.getDex() + this.stats.getLuk() + this.stats.getInt() + getRemainingAp();

        total -= str;
        this.stats.str = (short) str;

        total -= dex;
        this.stats.dex = (short) dex;

        total -= int_;
        this.stats.int_ = (short) int_;

        total -= luk;
        this.stats.luk = (short) luk;

        setRemainingAp((short) total);
        this.stats.recalcLocalStats(this);
        stat.put(MapleStat.力量, str);
        stat.put(MapleStat.敏捷, dex);
        stat.put(MapleStat.智力, int_);
        stat.put(MapleStat.运气, luk);
        stat.put(MapleStat.AVAILABLEAP, total);
        this.client.getSession().write(MaplePacketCreator.updatePlayerStats(stat, false, this));
    }

    public Event_PyramidSubway getPyramidSubway() {
        return this.pyramidSubway;
    }

    public void setPyramidSubway(Event_PyramidSubway ps) {
        this.pyramidSubway = ps;
    }

    public byte getSubcategory() {
        if ((this.job >= 430) && (this.job <= 434)) {
            return 1;
        }
        if (GameConstants.is火炮手(this.job)) {
            return 2;
        }
        if (GameConstants.is龙的传人(this.job)) {
            return 10;
        }
        return this.subcategory;
    }

    public void setSubcategory(int z) {
        this.subcategory = (byte) z;
    }

    public int itemQuantity(int itemid) {
        return getInventory(GameConstants.getInventoryType(itemid)).countById(itemid);
    }

    public void setRPS(RockPaperScissors rps) {
        this.rps = rps;
    }

    public RockPaperScissors getRPS() {
        return this.rps;
    }

    public long getNextConsume() {
        return this.nextConsume;
    }

    public void setNextConsume(long nc) {
        this.nextConsume = nc;
    }

    public int getRank() {
        return this.rank;
    }

    public int getRankMove() {
        return this.rankMove;
    }

    public int getJobRank() {
        return this.jobRank;
    }

    public int getJobRankMove() {
        return this.jobRankMove;
    }

    public void changeChannel(int channel) {
        ChannelServer toch = ChannelServer.getInstance(channel);
        if ((channel == this.client.getChannel()) || (toch == null) || (toch.isShutdown())) {
            this.client.getSession().write(MaplePacketCreator.serverBlocked(1));
            return;
        }
        changeRemoval();
        ChannelServer ch = ChannelServer.getInstance(this.client.getChannel());
        if (getMessenger() != null) {
            World.Messenger.silentLeaveMessenger(getMessenger().getId(), new MapleMessengerCharacter(this));
        }
        PlayerBuffStorage.addBuffsToStorage(getId(), getAllBuffs());
        PlayerBuffStorage.addCooldownsToStorage(getId(), getCooldowns());
        PlayerBuffStorage.addDiseaseToStorage(getId(), getAllDiseases());
        World.ChannelChange_Data(new CharacterTransfer(this), getId(), channel);
        ch.removePlayer(this);
        this.client.updateLoginState(3, this.client.getSessionIPAddress());
        String s = this.client.getSessionIPAddress();
        LoginServer.addIPAuth(s.substring(s.indexOf(47) + 1, s.length()));
        this.client.getSession().write(MaplePacketCreator.getChannelChange(this.client, Integer.parseInt(toch.getIP().split(":")[1])));
        saveToDB(false, false);
        getMap().removePlayer(this);
        this.client.setPlayer(null);
        this.client.setReceiving(false);
    }

    public void expandInventory(byte type, int amount) {
        MapleInventory inv = getInventory(MapleInventoryType.getByType(type));
        inv.addSlot((byte) amount);
        this.client.getSession().write(MaplePacketCreator.getSlotUpdate(type, inv.getSlotLimit()));
    }

    public boolean allowedToTarget(MapleCharacter other) {
        return (other != null) && ((!other.isHidden()) || (getGMLevel() >= other.getGMLevel()));
    }

    public int getFollowId() {
        return this.followid;
    }

    public void setFollowId(int fi) {
        this.followid = fi;
        if (fi == 0) {
            this.followinitiator = false;
            this.followon = false;
        }
    }

    public void setFollowInitiator(boolean fi) {
        this.followinitiator = fi;
    }

    public void setFollowOn(boolean fi) {
        this.followon = fi;
    }

    public boolean isFollowOn() {
        return this.followon;
    }

    public boolean isFollowInitiator() {
        return this.followinitiator;
    }

    public void checkFollow() {
        if (this.followid <= 0) {
            return;
        }
        if (this.followon) {
            this.map.broadcastMessage(MaplePacketCreator.followEffect(this.id, 0, null));
            this.map.broadcastMessage(MaplePacketCreator.followEffect(this.followid, 0, null));
        }
        MapleCharacter tt = this.map.getCharacterById(this.followid);
        this.client.getSession().write(MaplePacketCreator.getFollowMessage("已停止跟随。"));
        if (tt != null) {
            tt.setFollowId(0);
            tt.getClient().getSession().write(MaplePacketCreator.getFollowMessage("已停止跟随。"));
        }
        setFollowId(0);
    }

    public int getMarriageId() {
        return this.marriageId;
    }

    public void setMarriageId(int mi) {
        this.marriageId = mi;
    }

    public int getMarriageItemId() {
        return this.marriageItemId;
    }

    public void setMarriageItemId(int mi) {
        this.marriageItemId = mi;
    }

    public boolean isStaff() {
        return this.gmLevel >= PlayerGMRank.INTERN.getLevel();
    }

    public boolean isDonator() {
        return this.gmLevel >= PlayerGMRank.DONATOR.getLevel();
    }

    public boolean startPartyQuest(int questid) {
        boolean ret = false;
        MapleQuest q = MapleQuest.getInstance(questid);
        if ((q == null) || (!q.isPartyQuest())) {
            return false;
        }
        if ((!this.quests.containsKey(q)) || (!this.questinfo.containsKey(Integer.valueOf(questid)))) {
            MapleQuestStatus status = getQuestNAdd(q);
            status.setStatus((byte) 1);
            updateQuest(status);
            switch (questid) {
                case 1300:
                case 1301:
                case 1302:
                    updateInfoQuest(questid, "min=0;sec=0;date=0000-00-00;have=0;rank=F;try=0;cmp=0;CR=0;VR=0;gvup=0;vic=0;lose=0;draw=0");
                    break;
                case 1303:
                    updateInfoQuest(questid, "min=0;sec=0;date=0000-00-00;have=0;have1=0;rank=F;try=0;cmp=0;CR=0;VR=0;vic=0;lose=0");
                    break;
                case 1204:
                    updateInfoQuest(questid, "min=0;sec=0;date=0000-00-00;have0=0;have1=0;have2=0;have3=0;rank=F;try=0;cmp=0;CR=0;VR=0");
                    break;
                case 1206:
                    updateInfoQuest(questid, "min=0;sec=0;date=0000-00-00;have0=0;have1=0;rank=F;try=0;cmp=0;CR=0;VR=0");
                    break;
                default:
                    updateInfoQuest(questid, "min=0;sec=0;date=0000-00-00;have=0;rank=F;try=0;cmp=0;CR=0;VR=0");
            }

            ret = true;
        }
        return ret;
    }

    public String getOneInfo(int questid, String key) {
        if ((!this.questinfo.containsKey(Integer.valueOf(questid))) || (key == null) || (MapleQuest.getInstance(questid) == null) || (!MapleQuest.getInstance(questid).isPartyQuest())) {
            return null;
        }
        String[] split = ((String) this.questinfo.get(Integer.valueOf(questid))).split(";");
        for (String x : split) {
            String[] split2 = x.split("=");
            if ((split2.length == 2) && (split2[0].equals(key))) {
                return split2[1];
            }
        }
        return null;
    }

    public void updateOneInfo(int questid, String key, String value) {
        if ((!this.questinfo.containsKey(Integer.valueOf(questid))) || (key == null) || (value == null) || (MapleQuest.getInstance(questid) == null) || (!MapleQuest.getInstance(questid).isPartyQuest())) {
            return;
        }
        String[] split = ((String) this.questinfo.get(Integer.valueOf(questid))).split(";");
        boolean changed = false;
        StringBuilder newQuest = new StringBuilder();
        for (String x : split) {
            String[] split2 = x.split("=");
            if (split2.length != 2) {
                continue;
            }
            if (split2[0].equals(key)) {
                newQuest.append(key).append("=").append(value);
            } else {
                newQuest.append(x);
            }
            newQuest.append(";");
            changed = true;
        }
        updateInfoQuest(questid, changed ? newQuest.toString().substring(0, newQuest.toString().length() - 1) : newQuest.toString());
    }

    public void recalcPartyQuestRank(int questid) {
        if ((MapleQuest.getInstance(questid) == null) || (!MapleQuest.getInstance(questid).isPartyQuest())) {
            return;
        }
        if (!startPartyQuest(questid)) {
            String oldRank = getOneInfo(questid, "rank");
            if ((oldRank == null) || (oldRank.equals("S"))) {
                return;
            }
            String newRank = null;
            if (oldRank.equals("A")) {
                newRank = "S";
            } else if (oldRank.equals("B")) {
                newRank = "A";
            } else if (oldRank.equals("C")) {
                newRank = "B";
            } else if (oldRank.equals("D")) {
                newRank = "C";
            } else if (oldRank.equals("F")) {
                newRank = "D";
            } else {
                return;
            }
            List<Pair<String, Pair<String, Integer>>> questInfo = MapleQuest.getInstance(questid).getInfoByRank(newRank);
            if (questInfo == null) {
                return;
            }
            for (Pair<String, Pair<String, Integer>> q : questInfo) {
                boolean found = false;
                String val = getOneInfo(questid, (String) ((Pair) q.right).left);
                if (val == null) {
                    return;
                }
                int vall = 0;
                try {
                    vall = Integer.parseInt(val);
                } catch (NumberFormatException e) {
                    return;
                }
                if (((String) q.left).equals("less")) {
                    found = vall < ((Integer) ((Pair) q.right).right).intValue();
                } else if (((String) q.left).equals("more")) {
                    found = vall > ((Integer) ((Pair) q.right).right).intValue();
                } else if (((String) q.left).equals("equal")) {
                    found = vall == ((Integer) ((Pair) q.right).right).intValue();
                }
                if (!found) {
                    return;
                }
            }
            updateOneInfo(questid, "rank", newRank);
        }
    }

    public void tryPartyQuest(int questid) {
        if ((MapleQuest.getInstance(questid) == null) || (!MapleQuest.getInstance(questid).isPartyQuest())) {
            return;
        }
        try {
            startPartyQuest(questid);
            this.pqStartTime = System.currentTimeMillis();
            updateOneInfo(questid, "try", String.valueOf(Integer.parseInt(getOneInfo(questid, "try")) + 1));
        } catch (Exception e) {
            System.out.println("tryPartyQuest error");
        }
    }

    public void endPartyQuest(int questid) {
        if ((MapleQuest.getInstance(questid) == null) || (!MapleQuest.getInstance(questid).isPartyQuest())) {
            return;
        }
        try {
            startPartyQuest(questid);
            if (this.pqStartTime > 0L) {
                long changeTime = System.currentTimeMillis() - this.pqStartTime;
                int mins = (int) (changeTime / 1000L / 60L);
                int secs = (int) (changeTime / 1000L % 60L);
                int mins2 = Integer.parseInt(getOneInfo(questid, "min"));
                if ((mins2 <= 0) || (mins < mins2)) {
                    updateOneInfo(questid, "min", String.valueOf(mins));
                    updateOneInfo(questid, "sec", String.valueOf(secs));
                    updateOneInfo(questid, "date", FileoutputUtil.CurrentReadable_Date());
                }
                int newCmp = Integer.parseInt(getOneInfo(questid, "cmp")) + 1;
                updateOneInfo(questid, "cmp", String.valueOf(newCmp));
                updateOneInfo(questid, "CR", String.valueOf((int) Math.ceil(newCmp * 100.0D / Integer.parseInt(getOneInfo(questid, "try")))));
                recalcPartyQuestRank(questid);
                this.pqStartTime = 0L;
            }
        } catch (Exception e) {
            System.out.println("endPartyQuest error");
        }
    }

    public void havePartyQuest(int itemId) {
        int questid = 0;
        int index = -1;
        switch (itemId) {
            case 1002798:
                questid = 1200;
                break;
            case 1072369:
                questid = 1201;
                break;
            case 1022073:
                questid = 1202;
                break;
            case 1082232:
                questid = 1203;
                break;
            case 1002571:
            case 1002572:
            case 1002573:
            case 1002574:
                questid = 1204;
                index = itemId - 1002571;
                break;
            case 1102226:
                questid = 1303;
                break;
            case 1102227:
                questid = 1303;
                index = 0;
                break;
            case 1122010:
                questid = 1205;
                break;
            case 1032060:
            case 1032061:
                questid = 1206;
                index = itemId - 1032060;
                break;
            case 3010018:
                questid = 1300;
                break;
            case 1122007:
                questid = 1301;
                break;
            case 1122058:
                questid = 1302;
                break;
            default:
                return;
        }
        if ((MapleQuest.getInstance(questid) == null) || (!MapleQuest.getInstance(questid).isPartyQuest())) {
            return;
        }
        startPartyQuest(questid);
        updateOneInfo(questid, new StringBuilder().append("have").append(index == -1 ? "" : Integer.valueOf(index)).toString(), "1");
    }

    public void resetStatsByJob(boolean beginnerJob) {
        int baseJob = beginnerJob ? this.job % 1000 : this.job % 1000 / 100 * 100;
        boolean UA = getQuestNoAdd(MapleQuest.getInstance(111111)) != null;
        if (baseJob == 100) {
            resetStats(UA ? 4 : 35, 4, 4, 4);
        } else if (baseJob == 200) {
            resetStats(4, 4, UA ? 4 : 20, 4);
        } else if ((baseJob == 300) || (baseJob == 400)) {
            resetStats(4, UA ? 4 : 25, 4, 4);
        } else if (baseJob == 500) {
            resetStats(4, UA ? 4 : 20, 4, 4);
        } else if (baseJob == 0) {
            resetStats(4, 4, 4, 4);
        }
    }

    public boolean hasSummon() {
        return this.hasSummon;
    }

    public void setHasSummon(boolean summ) {
        this.hasSummon = summ;
    }

    public void removeDoor() {
        MapleDoor door = (MapleDoor) getDoors().iterator().next();
        for (MapleCharacter chr : door.getTarget().getCharactersThreadsafe()) {
            door.sendDestroyData(chr.getClient());
        }
        for (MapleCharacter chr : door.getTown().getCharactersThreadsafe()) {
            door.sendDestroyData(chr.getClient());
        }
        for (MapleDoor destroyDoor : getDoors()) {
            door.getTarget().removeMapObject(destroyDoor);
            door.getTown().removeMapObject(destroyDoor);
        }
        clearDoors();
    }

    public void removeMechDoor() {
        for (MechDoor destroyDoor : getMechDoors()) {
            for (MapleCharacter chr : getMap().getCharactersThreadsafe()) {
                destroyDoor.sendDestroyData(chr.getClient());
            }
            getMap().removeMapObject(destroyDoor);
        }
        clearMechDoors();
    }

    public void changeRemoval() {
        changeRemoval(false);
    }

    public void changeRemoval(boolean dc) {
        if ((getCheatTracker() != null) && (dc)) {
            getCheatTracker().dispose();
        }
        removeFamiliar();
        dispelSummons();
        if (!dc) {
            cancelEffectFromBuffStat(MapleBuffStat.飞行骑乘);
            cancelEffectFromBuffStat(MapleBuffStat.骑兽技能);
            cancelEffectFromBuffStat(MapleBuffStat.金属机甲);
            cancelEffectFromBuffStat(MapleBuffStat.RECOVERY);
            cancelEffectFromBuffStat(MapleBuffStat.精神连接);
        }
        if (getPyramidSubway() != null) {
            getPyramidSubway().dispose(this);
        }
        if ((this.playerShop != null) && (!dc)) {
            this.playerShop.removeVisitor(this);
            if (this.playerShop.isOwner(this)) {
                this.playerShop.setOpen(true);
            }
        }
        if (!getDoors().isEmpty()) {
            removeDoor();
        }
        if (!getMechDoors().isEmpty()) {
            removeMechDoor();
        }
        disposeClones();
        NPCScriptManager.getInstance().dispose(this.client);
        cancelFairySchedule(false);
    }

    public void updateTick(int newTick) {
        this.anticheat.updateTick(newTick);
    }

    public boolean canUseFamilyBuff(MapleFamilyBuff buff) {
        MapleQuestStatus stat = getQuestNoAdd(MapleQuest.getInstance(buff.questID));
        if (stat == null) {
            return true;
        }
        if (stat.getCustomData() == null) {
            stat.setCustomData("0");
        }
        return Long.parseLong(stat.getCustomData()) + 86400000L < System.currentTimeMillis();
    }

    public void useFamilyBuff(MapleFamilyBuff buff) {
        MapleQuestStatus stat = getQuestNAdd(MapleQuest.getInstance(buff.questID));
        stat.setCustomData(String.valueOf(System.currentTimeMillis()));
    }

    public List<Integer> usedBuffs() {
        List used = new ArrayList();
        MapleFamilyBuff[] z = MapleFamilyBuff.values();
        for (int i = 0; i < z.length; i++) {
            if (!canUseFamilyBuff(z[i])) {
                used.add(Integer.valueOf(i));
            }
        }
        return used;
    }

    public String getTeleportName() {
        return this.teleportname;
    }

    public void setTeleportName(String tname) {
        this.teleportname = tname;
    }

    public int getNoJuniors() {
        if (this.mfc == null) {
            return 0;
        }
        return this.mfc.getNoJuniors();
    }

    public MapleFamilyCharacter getMFC() {
        return this.mfc;
    }

    public void makeMFC(int familyid, int seniorid, int junior1, int junior2) {
        if (familyid > 0) {
            MapleFamily f = World.Family.getFamily(familyid);
            if (f == null) {
                this.mfc = null;
            } else {
                this.mfc = f.getMFC(this.id);
                if (this.mfc == null) {
                    this.mfc = f.addFamilyMemberInfo(this, seniorid, junior1, junior2);
                }
                if (this.mfc.getSeniorId() != seniorid) {
                    this.mfc.setSeniorId(seniorid);
                }
                if (this.mfc.getJunior1() != junior1) {
                    this.mfc.setJunior1(junior1);
                }
                if (this.mfc.getJunior2() != junior2) {
                    this.mfc.setJunior2(junior2);
                }
            }
        } else {
            this.mfc = null;
        }
    }

    public void setFamily(int newf, int news, int newj1, int newj2) {
        if ((this.mfc == null) || (newf != this.mfc.getFamilyId()) || (news != this.mfc.getSeniorId()) || (newj1 != this.mfc.getJunior1()) || (newj2 != this.mfc.getJunior2())) {
            makeMFC(newf, news, newj1, newj2);
        }
    }

    public int maxBattleshipHP(int skillid) {
        return getTotalSkillLevel(skillid) * 5000 + (getLevel() - 120) * 3000;
    }

    public int currentBattleshipHP() {
        return this.battleshipHP;
    }

    public void setBattleshipHP(int v) {
        this.battleshipHP = v;
    }

    public void decreaseBattleshipHP() {
        this.battleshipHP -= 1;
    }

    public int getGachExp() {
        return this.gachexp;
    }

    public void setGachExp(int ge) {
        this.gachexp = ge;
    }

    public boolean isInBlockedMap() {
        if ((!isAlive()) || (getPyramidSubway() != null) || (getMap().getSquadByMap() != null) || (getEventInstance() != null) || (getMap().getEMByMap() != null)) {
            return true;
        }
        if (((getMapId() >= 680000210) && (getMapId() <= 680000502)) || ((getMapId() / 10000 == 92502) && (getMapId() >= 925020100)) || (getMapId() / 10000 == 92503) || (getMapId() == 180000001)) {
            return true;
        }
        for (int i : GameConstants.blockedMaps) {
            if (getMapId() == i) {
                return true;
            }
        }
        return false;
    }

    public boolean isInTownMap() {
        if ((hasBlockedInventory()) || (!getMap().isTown()) || (FieldLimitType.VipRock.check(getMap().getFieldLimit())) || (getEventInstance() != null)) {
            return false;
        }
        for (int i : GameConstants.blockedMaps) {
            if (getMapId() == i) {
                return false;
            }
        }
        return true;
    }

    public boolean hasBlockedInventory() {
        return (!isAlive()) || (getTrade() != null) || (getConversation() > 0) || (getDirection() >= 0) || (getPlayerShop() != null) || (getBattle() != null) || (this.map == null);
    }

    public void startPartySearch(List<Integer> jobs, int maxLevel, int minLevel, int membersNeeded) {
        for (MapleCharacter chr : this.map.getCharacters()) {
            if ((chr.getId() != this.id) && (chr.getParty() == null) && (chr.getLevel() >= minLevel) && (chr.getLevel() <= maxLevel) && ((jobs.isEmpty()) || (jobs.contains(Integer.valueOf(chr.getJob())))) && ((isGM()) || (!chr.isGM()))) {
                if ((this.party == null) || (this.party.getMembers().size() >= 6) || (this.party.getMembers().size() >= membersNeeded)) {
                    break;
                }
                chr.setParty(this.party);
                World.Party.updateParty(this.party.getId(), PartyOperation.加入队伍, new MaplePartyCharacter(chr));
                chr.receivePartyMemberHP();
                chr.updatePartyMemberHP();
            }
        }
    }

    public Battler getBattler(int pos) {
        return this.battlers[pos];
    }

    public Battler[] getBattlers() {
        return this.battlers;
    }

    public List<Battler> getBoxed() {
        return this.boxed;
    }

    public PokemonBattle getBattle() {
        return this.battle;
    }

    public void setBattle(PokemonBattle b) {
        this.battle = b;
    }

    public int countBattlers() {
        int ret = 0;
        for (int i = 0; i < this.battlers.length; i++) {
            if (this.battlers[i] != null) {
                ret++;
            }
        }
        return ret;
    }

    public void changedBattler() {
        this.changed_pokemon = true;
    }

    public void makeBattler(int index, int monsterId) {
        MapleMonsterStats mons = MapleLifeFactory.getMonsterStats(monsterId);
        this.battlers[index] = new Battler(mons);
        this.battlers[index].setCharacterId(this.id);
        this.changed_pokemon = true;
        getMonsterBook().monsterCaught(this.client, monsterId, mons.getName());
    }

    public boolean removeBattler(int ind) {
        if (countBattlers() <= 1) {
            return false;
        }
        if (ind == this.battlers.length) {
            this.battlers[ind] = null;
        } else {
            for (int i = ind; i < this.battlers.length; i++) {
                this.battlers[i] = (i + 1 == this.battlers.length ? null : this.battlers[(i + 1)]);
            }
        }
        this.changed_pokemon = true;
        return true;
    }

    public int getChallenge() {
        return this.challenge;
    }

    public void setChallenge(int c) {
        this.challenge = c;
    }

    public short getFatigue() {
        return this.fatigue;
    }

    public void setFatigue(int j) {
        this.fatigue = (short) Math.max(0, j);
        updateSingleStat(MapleStat.疲劳, this.fatigue);
    }

    public void fakeRelog() {
        this.client.getSession().write(MaplePacketCreator.getCharInfo(this));
        MapleMap mapp = getMap();
        mapp.setCheckStates(false);
        mapp.removePlayer(this);
        mapp.addPlayer(this);
        mapp.setCheckStates(true);
        if (GameConstants.GMS) {
            this.client.getSession().write(MaplePacketCreator.getFamiliarInfo(this));
        }
        this.client.getSession().write(MaplePacketCreator.serverNotice(5, "刷新人数据完成..."));
    }

    public boolean canSummon() {
        return canSummon(5000);
    }

    public boolean canSummon(int g) {
        if (this.lastSummonTime + g < System.currentTimeMillis()) {
            this.lastSummonTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    public int getIntNoRecord(int questID) {
        MapleQuestStatus stat = getQuestNoAdd(MapleQuest.getInstance(questID));
        if ((stat == null) || (stat.getCustomData() == null)) {
            return 0;
        }
        return Integer.parseInt(stat.getCustomData());
    }

    public int getIntRecord(int questID) {
        MapleQuestStatus stat = getQuestNAdd(MapleQuest.getInstance(questID));
        if (stat.getCustomData() == null) {
            stat.setCustomData("0");
        }
        return Integer.parseInt(stat.getCustomData());
    }

    public void updatePetAuto() {
        this.client.getSession().write(MaplePacketCreator.petAutoHP(getIntRecord(122221)));
        this.client.getSession().write(MaplePacketCreator.petAutoMP(getIntRecord(122223)));
        this.client.getSession().write(MaplePacketCreator.petAutoBuff(getIntRecord(122224)));
    }

    public void sendEnglishQuiz(String msg) {
        this.client.getSession().write(MaplePacketCreator.englishQuizMsg(msg));
    }

    public void setChangeTime() {
        this.mapChangeTime = System.currentTimeMillis();
    }

    public long getChangeTime() {
        return this.mapChangeTime;
    }

    public Map<ReportType, Integer> getReports() {
        return this.reports;
    }

    public void addReport(ReportType type) {
        Integer value = (Integer) this.reports.get(type);
        this.reports.put(type, Integer.valueOf(value == null ? 1 : value.intValue() + 1));
        this.changed_reports = true;
    }

    public void clearReports(ReportType type) {
        this.reports.remove(type);
        this.changed_reports = true;
    }

    public void clearReports() {
        this.reports.clear();
        this.changed_reports = true;
    }

    public int getReportPoints() {
        int ret = 0;
        for (Integer entry : this.reports.values()) {
            ret += entry.intValue();
        }
        return ret;
    }

    public String getReportSummary() {
        StringBuilder ret = new StringBuilder();
        final List<Pair<ReportType, Integer>> offenseList = new ArrayList<Pair<ReportType, Integer>>();
        for (final Entry<ReportType, Integer> entry : reports.entrySet()) {
            offenseList.add(new Pair<ReportType, Integer>(entry.getKey(), entry.getValue()));
        }
        Collections.sort(offenseList, new Comparator<Pair<ReportType, Integer>>() {
            public int compare(Pair<ReportType, Integer> o1, Pair<ReportType, Integer> o2) {
                int thisVal = ((Integer) o1.getRight()).intValue();
                int anotherVal = ((Integer) o2.getRight()).intValue();
                return thisVal == anotherVal ? 0 : thisVal < anotherVal ? 1 : -1;
            }
        });
        for (int x = 0; x < offenseList.size(); x++) {
            ret.append(StringUtil.makeEnumHumanReadable(((ReportType) ((Pair) offenseList.get(x)).left).name()));
            ret.append(": ");
            ret.append(((Pair) offenseList.get(x)).right);
            ret.append(" ");
        }
        return ret.toString();
    }

    public short getScrolledPosition() {
        return this.scrolledPosition;
    }

    public void setScrolledPosition(short s) {
        this.scrolledPosition = s;
    }

    public MapleTrait getTrait(MapleTrait.MapleTraitType t) {
        return (MapleTrait) this.traits.get(t);
    }

    public void forceCompleteQuest(int id) {
        MapleQuest.getInstance(id).forceComplete(this, 9270035);
    }

    public List<Integer> getExtendedSlots() {
        return this.extendedSlots;
    }

    public int getExtendedSlot(int index) {
        if ((this.extendedSlots.size() <= index) || (index < 0)) {
            return -1;
        }
        return ((Integer) this.extendedSlots.get(index)).intValue();
    }

    public void changedExtended() {
        this.changed_extendedSlots = true;
    }

    public MapleAndroid getAndroid() {
        return this.android;
    }

    public void removeAndroid() {
        if (this.map != null) {
            this.map.broadcastMessage(AndroidPacket.deactivateAndroid(this.id));
        }
        this.android = null;
    }

    public void updateAndroid(int size, int itemId) {
        if (this.map != null) {
            this.map.broadcastMessage(AndroidPacket.updateAndroidLook(getId(), size, itemId));
        }
    }

    public boolean checkHearts() {
        return getInventory(MapleInventoryType.EQUIPPED).getItem((short) -35) != null;
    }

    public void setAndroid(MapleAndroid a) {
        if (checkHearts()) {
            this.android = a;
            if ((this.map != null) && (a != null)) {
                this.map.broadcastMessage(AndroidPacket.spawnAndroid(this, a));
                this.map.broadcastMessage(AndroidPacket.showAndroidEmotion(getId(), Randomizer.nextInt(17) + 1));
            }
        }
    }

    public void setSidekick(MapleSidekick s) {
        this.sidekick = s;
    }

    public MapleSidekick getSidekick() {
        return this.sidekick;
    }

    public List<Item> getRebuy() {
        return this.rebuy;
    }

    public Item findById(int itemId, int pos) {
        Item posItem = (Item) getRebuy().get(pos);

        if (posItem.getItemId() == itemId) {
            return posItem;
        }
        return null;
    }

    public Map<Integer, MonsterFamiliar> getFamiliars() {
        return this.familiars;
    }

    public MonsterFamiliar getSummonedFamiliar() {
        return this.summonedFamiliar;
    }

    public void removeFamiliar() {
        if ((this.summonedFamiliar != null) && (this.map != null)) {
            removeVisibleFamiliar();
        }
        this.summonedFamiliar = null;
    }

    public void removeVisibleFamiliar() {
        getMap().removeMapObject(this.summonedFamiliar);
        removeVisibleMapObject(this.summonedFamiliar);
        getMap().broadcastMessage(MaplePacketCreator.removeFamiliar(getId()));
        this.anticheat.resetFamiliarAttack();
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        cancelEffect(ii.getItemEffect(ii.getFamiliar(this.summonedFamiliar.getFamiliar()).passive), false, System.currentTimeMillis());
    }

    public void spawnFamiliar(MonsterFamiliar mf) {
        this.summonedFamiliar = mf;
        mf.setStance(0);
        mf.setPosition(getPosition());
        mf.setFh(getFH());
        addVisibleMapObject(mf);
        getMap().spawnFamiliar(mf);
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        MapleStatEffect eff = ii.getItemEffect(ii.getFamiliar(this.summonedFamiliar.getFamiliar()).passive);
        if ((eff != null) && (eff.getInterval() <= 0) && (eff.makeChanceResult())) {
            eff.applyTo(this);
        }
        this.lastFamiliarEffectTime = System.currentTimeMillis();
    }

    public boolean canFamiliarEffect(long now, MapleStatEffect eff) {
        return (this.lastFamiliarEffectTime > 0L) && (this.lastFamiliarEffectTime + eff.getInterval() < now);
    }

    public void doFamiliarSchedule(long now) {
        for (MonsterFamiliar mf : this.familiars.values()) {
            if ((this.summonedFamiliar != null) && (this.summonedFamiliar.getId() == mf.getId())) {
                mf.addFatigue(this, 5);
                MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                MapleStatEffect eff = ii.getItemEffect(ii.getFamiliar(this.summonedFamiliar.getFamiliar()).passive);
                if ((eff != null) && (eff.getInterval() > 0) && (canFamiliarEffect(now, eff)) && (eff.makeChanceResult())) {
                    eff.applyTo(this);
                }
            } else if (mf.getFatigue() > 0) {
                mf.setFatigue(Math.max(0, mf.getFatigue() - 5));
            }
        }
    }

    public MapleImp[] getImps() {
        return this.imps;
    }

    public void sendImp() {
        for (int i = 0; i < this.imps.length; i++) {
            if (this.imps[i] != null) {
                this.client.getSession().write(MaplePacketCreator.updateImp(this.imps[i], MapleImp.ImpFlag.SUMMONED.getValue(), i, true));
            }
        }
    }

    public int getBattlePoints() {
        return this.pvpPoints;
    }

    public int getTotalBattleExp() {
        return this.pvpExp;
    }

    public void setVip1(int amount) {
        if (amount > 3) {
            this.vip = 4;
        } else if (amount <= -4) {
            this.vip = -4;
        } else {
            this.vip = amount;

        }
    }

    public void gainVip(int amount) {
        this.vip += amount;
    }

    public void setBattlePoints(int p) {
        if (p != this.pvpPoints) {
            this.client.getSession().write(UIPacket.getBPMsg(p - this.pvpPoints));
            updateSingleStat(MapleStat.BATTLE_POINTS, p);
        }
        this.pvpPoints = p;
    }

    public void setTotalBattleExp(int p) {
        int previous = this.pvpExp;
        this.pvpExp = p;
        if (p != previous) {
            this.stats.recalcPVPRank(this);
            updateSingleStat(MapleStat.BATTLE_EXP, this.stats.pvpExp);
            updateSingleStat(MapleStat.BATTLE_RANK, this.stats.pvpRank);
        }
    }

    public void changeTeam(int newTeam) {
        this.coconutteam = newTeam;
        if (!inPVP()) {
            this.client.getSession().write(MaplePacketCreator.showEquipEffect(newTeam));
        }
    }

    public void disease(int type, int level) {
        if (MapleDisease.getBySkill(type) == null) {
            return;
        }
        this.chair = 0;
        this.client.getSession().write(MaplePacketCreator.cancelChair(-1));
        this.map.broadcastMessage(this, MaplePacketCreator.showChair(this.id, 0), false);
        giveDebuff(MapleDisease.getBySkill(type), MobSkillFactory.getMobSkill(type, level));
    }

    public boolean inPVP() {
        return (this.eventInstance != null) && (this.eventInstance.getName().startsWith("PVP")) && (this.client.getChannelServer().isCanPvp());
    }

    public void clearAllCooldowns() {
        for (MapleCoolDownValueHolder m : getCooldowns()) {
            int skil = m.skillId;
            removeCooldown(skil);
            this.client.getSession().write(MaplePacketCreator.skillCooldown(skil, 0));
        }
    }

    public Pair<Double, Boolean> modifyDamageTaken(double damage, MapleMapObject attacke) {
        Pair ret = new Pair(Double.valueOf(damage), Boolean.valueOf(false));
        if (damage < 0.0D) {
            return ret;
        }
        Integer div = getBuffedValue(MapleBuffStat.祝福护甲);
        Integer div2 = getBuffedValue(MapleBuffStat.神圣魔法盾);
        if (div2 != null) {
            if (div2.intValue() <= 0) {
                cancelEffectFromBuffStat(MapleBuffStat.神圣魔法盾);
            } else {
                setBuffedValue(MapleBuffStat.神圣魔法盾, div2.intValue() - 1);
                damage = 0.0D;
            }
        } else if (div != null) {
            if (div.intValue() <= 0) {
                cancelEffectFromBuffStat(MapleBuffStat.祝福护甲);
            } else {
                setBuffedValue(MapleBuffStat.祝福护甲, div.intValue() - 1);
                damage = 0.0D;
            }
        }
        MapleStatEffect barrier = getStatForBuff(MapleBuffStat.战神之盾);
        if (barrier != null) {
            damage = barrier.getX() / 1000.0D * damage;
        }
        barrier = getStatForBuff(MapleBuffStat.魔法屏障);
        if (barrier != null) {
            damage -= barrier.getX() / 100.0D * damage;
        }
        barrier = getStatForBuff(MapleBuffStat.双弩水盾);
        if (barrier != null) {
            if (ServerProperties.ShowPacket()) {
                log.info(new StringBuilder().append("双弩水盾 - 受到伤害: ").append(damage).toString());
            }
            damage -= barrier.getX() / 100.0D * damage;
            if (ServerProperties.ShowPacket()) {
                log.info(new StringBuilder().append("双弩水盾 - 吸收伤害: ").append(damage).append(" BUFFX: ").append(barrier.getX()).append(" 比率: ").append(barrier.getX() / 100.0D).toString());
            }
        }
        List attack = ((attacke instanceof MapleMonster)) || (attacke == null) ? null : new ArrayList();
        if (damage > 0.0D) {
            if ((getJob() == 122) && (!skillisCooling(1220013))) {
                Skill divine = SkillFactory.getSkill(1220013);
                if (getTotalSkillLevel(divine) > 0) {
                    MapleStatEffect divineShield = divine.getEffect(getTotalSkillLevel(divine));
                    if (divineShield.makeChanceResult()) {
                        divineShield.applyTo(this);
                        this.client.getSession().write(MaplePacketCreator.skillCooldown(1220013, divineShield.getCooldown()));
                        addCooldown(1220013, System.currentTimeMillis(), divineShield.getCooldown() * 1000);
                    }
                }
            } else if (getJob() == 3112) {
                Skill divine = SkillFactory.getSkill(31120009);
                if (getTotalSkillLevel(divine) > 0) {
                    MapleStatEffect eff = divine.getEffect(getTotalSkillLevel(divine));
                    damage = eff.getX() / 1000.0D * damage;
                }
            } else if (getJob() == 2112) {
                Skill achilles = SkillFactory.getSkill(21120004);
                if (getTotalSkillLevel(achilles) > 0) {
                    MapleStatEffect multiplier = achilles.getEffect(getTotalSkillLevel(achilles));
                    damage = multiplier.getX() / 1000.0D * damage;
                }
            } else if ((getBuffedValue(MapleBuffStat.卫星防护_PROC) != null) && (getBuffedValue(MapleBuffStat.卫星防护_吸收) != null) && (getBuffedValue(MapleBuffStat.替身术) != null)) {
                double buff = getBuffedValue(MapleBuffStat.卫星防护_PROC).doubleValue();
                double buffz = getBuffedValue(MapleBuffStat.卫星防护_吸收).doubleValue();
                if ((int) (buff / 100.0D * getStat().getMaxHp()) <= damage) {
                    damage -= buffz / 100.0D * damage;
                    cancelEffectFromBuffStat(MapleBuffStat.替身术);
                }
            } else if ((getJob() == 433) || (getJob() == 434)) {
                Skill divine = SkillFactory.getSkill(4330001);
                if ((getTotalSkillLevel(divine) > 0) && (getBuffedValue(MapleBuffStat.隐身术) == null) && (!skillisCooling(divine.getId()))) {
                    MapleStatEffect divineShield = divine.getEffect(getTotalSkillLevel(divine));
                    if (Randomizer.nextInt(100) < divineShield.getX()) {
                        divineShield.applyTo(this);
                    }
                }
            } else if (((getJob() == 582) || (getJob() == 592) || (getJob() == 572)) && (getBuffedValue(MapleBuffStat.反制攻击) == null)) {
                Skill divine = SkillFactory.getSkill(getJob() == 592 ? 5920012 : getJob() == 582 ? 5820011 : 5720012);
                if ((getTotalSkillLevel(divine) > 0) && (!skillisCooling(divine.getId()))) {
                    MapleStatEffect divineShield = divine.getEffect(getTotalSkillLevel(divine));
                    if (divineShield.makeChanceResult()) {
                        divineShield.applyTo(this);
                        this.client.getSession().write(MaplePacketCreator.skillCooldown(divine.getId(), divineShield.getX()));
                        addCooldown(divine.getId(), System.currentTimeMillis(), divineShield.getX() * 1000);
                    }
                }
            } else if ((getJob() == 312) && (attacke != null)) {
                Skill divine = SkillFactory.getSkill(3120010);
                if (getTotalSkillLevel(divine) > 0) {
                    MapleStatEffect divineShield = divine.getEffect(getTotalSkillLevel(divine));
                    if (divineShield.makeChanceResult()) {
                        int i;
                        if ((attacke instanceof MapleMonster)) {
                            Rectangle bounds = divineShield.calculateBoundingBox(getTruePosition(), isFacingLeft());
                            List<MapleMapObject> affected = getMap().getMapObjectsInRect(bounds, Arrays.asList(new MapleMapObjectType[]{attacke.getType()}));
                            i = 0;
                            for (MapleMapObject mo : affected) {
                                MapleMonster mons = (MapleMonster) mo;
                                if ((mons.getStats().isFriendly()) || (mons.isFake())) {
                                    continue;
                                }
                                mons.applyStatus(this, new MonsterStatusEffect(MonsterStatus.眩晕, Integer.valueOf(1), divineShield.getSourceId(), null, false), false, divineShield.getDuration(), true, divineShield);
                                int theDmg = (int) (divineShield.getDamage() * getStat().getCurrentMaxBaseDamage() / 100.0D);
                                mons.damage(this, theDmg, true);
                                getMap().broadcastMessage(MobPacket.damageMonster(mons.getObjectId(), theDmg));
                                i++;
                                if (i >= divineShield.getMobCount()) {
                                    break;
                                }
                            }
                        } else {
                            MapleCharacter chr = (MapleCharacter) attacke;
                            chr.addHP(-divineShield.getDamage());
                            attack.add(Integer.valueOf(divineShield.getDamage()));
                        }
                    }
                }
            } else if (((getJob() == 531) || (getJob() == 532)) && (attacke != null)) {
                Skill divine = SkillFactory.getSkill(5310009);
                if (getTotalSkillLevel(divine) > 0) {
                    MapleStatEffect divineShield = divine.getEffect(getTotalSkillLevel(divine));
                    if (divineShield.makeChanceResult()) {
                        if ((attacke instanceof MapleMonster)) {
                            MapleMonster attacker = (MapleMonster) attacke;
                            int theDmg = (int) (divineShield.getDamage() * getStat().getCurrentMaxBaseDamage() / 100.0D);
                            attacker.damage(this, theDmg, true);
                            getMap().broadcastMessage(MobPacket.damageMonster(attacker.getObjectId(), theDmg));
                        } else {
                            MapleCharacter attacker = (MapleCharacter) attacke;
                            attacker.addHP(-divineShield.getDamage());
                            attack.add(Integer.valueOf(divineShield.getDamage()));
                        }
                    }
                }
            } else if ((getJob() == 132) && (attacke != null)) {
                Skill divine = SkillFactory.getSkill(1320011);
                if ((getTotalSkillLevel(divine) > 0) && (!skillisCooling(divine.getId())) && (getBuffSource(MapleBuffStat.灵魂助力) == 1321007)) {
                    MapleStatEffect divineShield = divine.getEffect(getTotalSkillLevel(divine));
                    if (divineShield.makeChanceResult()) {
                        this.client.getSession().write(MaplePacketCreator.skillCooldown(divine.getId(), divineShield.getCooldown()));
                        addCooldown(divine.getId(), System.currentTimeMillis(), divineShield.getCooldown() * 1000);
                        if ((attacke instanceof MapleMonster)) {
                            MapleMonster attacker = (MapleMonster) attacke;
                            int theDmg = (int) (divineShield.getDamage() * getStat().getCurrentMaxBaseDamage() / 100.0D);
                            attacker.damage(this, theDmg, true);
                            getMap().broadcastMessage(MobPacket.damageMonster(attacker.getObjectId(), theDmg));
                        } else {
                            MapleCharacter attacker = (MapleCharacter) attacke;
                            attacker.addHP(-divineShield.getDamage());
                            attack.add(Integer.valueOf(divineShield.getDamage()));
                        }
                    }
                }
            }
            if (attacke != null) {
                int damr = (Randomizer.nextInt(100) < getStat().DAMreflect_rate ? getStat().DAMreflect : 0) + (getBuffedValue(MapleBuffStat.伤害反击) != null ? getBuffedValue(MapleBuffStat.伤害反击).intValue() : 0);
                if (damr > 0) {
                    long bouncedamage = (long) (damage * damr / 100.0D);
                    if ((attacke instanceof MapleMonster)) {
                        MapleMonster attacker = (MapleMonster) attacke;
                        bouncedamage = Math.min(bouncedamage, attacker.getMobMaxHp() / 10L);
                        attacker.damage(this, bouncedamage, true);
                        getMap().broadcastMessage(this, MobPacket.damageMonster(attacker.getObjectId(), bouncedamage), getTruePosition());
                        if (getBuffSource(MapleBuffStat.伤害反击) == 31101003) {
                            MapleStatEffect eff = getStatForBuff(MapleBuffStat.伤害反击);
                            attacker.applyStatus(this, new MonsterStatusEffect(MonsterStatus.眩晕, Integer.valueOf(1), eff.getSourceId(), null, false), false, 5000L, true, eff);
                        }
                    } else {
                        MapleCharacter attacker = (MapleCharacter) attacke;
                        bouncedamage = Math.min(bouncedamage, attacker.getStat().getCurrentMaxHp() / 10);
                        attacker.addHP(-(int) bouncedamage);
                        log.info(new StringBuilder().append("减少Hp: ").append((int) bouncedamage).toString());
                        attack.add(Integer.valueOf((int) bouncedamage));
                        if (getBuffSource(MapleBuffStat.伤害反击) == 31101003) {
                            attacker.disease(MapleDisease.STUN.getDisease(), 1);
                        }
                    }
                    ret.right = Boolean.valueOf(true);
                }
                if (((getJob() == 411) || (getJob() == 412) || (getJob() == 421) || (getJob() == 422) || (getJob() == 1412)) && (getBuffedValue(MapleBuffStat.召唤兽) != null) && (attacke != null)) {
                    List<MapleSummon> ss = getSummonsReadLock();
                    try {
                        for (MapleSummon sum : ss) {
                            if ((sum.getTruePosition().distanceSq(getTruePosition()) < 400000.0D) && ((sum.getSkill() == 4111007) || (sum.getSkill() == 4211007) || (sum.getSkill() == 14111010))) {
                                List allDamage = new ArrayList();
                                if ((attacke instanceof MapleMonster)) {
                                    MapleMonster attacker = (MapleMonster) attacke;
                                    int theDmg = (int) (SkillFactory.getSkill(sum.getSkill()).getEffect(sum.getSkillLevel()).getX() * damage / 100.0D);
                                    allDamage.add(new Pair(Integer.valueOf(attacker.getObjectId()), Integer.valueOf(theDmg)));
                                    getMap().broadcastMessage(MaplePacketCreator.summonAttack(sum.getOwnerId(), sum.getObjectId(), (byte) -124, allDamage, getLevel(), true));
                                    attacker.damage(this, theDmg, true);
                                    checkMonsterAggro(attacker);
                                    if (!attacker.isAlive()) {
                                        getClient().getSession().write(MobPacket.killMonster(attacker.getObjectId(), 1));
                                    }
                                } else {
                                    MapleCharacter chr = (MapleCharacter) attacke;
                                    int dmg = SkillFactory.getSkill(sum.getSkill()).getEffect(sum.getSkillLevel()).getX();
                                    chr.addHP(-dmg);
                                    attack.add(Integer.valueOf(dmg));
                                }
                            }
                        }
                    } finally {
                        unlockSummonsReadLock();
                    }
                }
            }
        }
        if ((attack != null) && (attack.size() > 0) && (attacke != null)) {
            getMap().broadcastMessage(MaplePacketCreator.pvpCool(attacke.getObjectId(), attack));
        }
        ret.left = Double.valueOf(damage);
        return ret;
    }

    public void onAttack(long maxhp, int maxmp, int skillid, int oid, int totDamage) {
        if ((this.stats.hpRecoverProp > 0)
                && (Randomizer.nextInt(100) <= this.stats.hpRecoverProp)) {
            if (this.stats.hpRecover > 0) {
                healHP(this.stats.hpRecover);
            }
            if (this.stats.hpRecoverPercent > 0) {
                addHP((int) Math.min(maxhp, Math.min((int) (totDamage * this.stats.hpRecoverPercent / 100.0D), this.stats.getMaxHp() / 2)));
            }
        }

        if ((this.stats.mpRecoverProp > 0) && (!GameConstants.is恶魔猎手(getJob()))
                && (Randomizer.nextInt(100) <= this.stats.mpRecoverProp)
                && (this.stats.mpRecover > 0)) {
            healMP(this.stats.mpRecover);
        }

        if (((getJob() == 2410) || (getJob() == 2411) || (getJob() == 2412)) && (totDamage > 0) && ((skillid != 24120002) || (skillid != 24100003))) {
            handleCarteGain(oid);
        }

        if (getBuffedValue(MapleBuffStat.连环吸血) != null) {
            addHP((int) Math.min(maxhp, Math.min((int) (totDamage * getStatForBuff(MapleBuffStat.连环吸血).getX() / 100.0D), this.stats.getMaxHp() / 2)));
        }
        if (getBuffSource(MapleBuffStat.连环吸血) == 23101003) {
            addMP(Math.min(maxmp, Math.min((int) (totDamage * getStatForBuff(MapleBuffStat.连环吸血).getX() / 100.0D), this.stats.getMaxMp() / 2)));
        }
        if ((getBuffedValue(MapleBuffStat.幻灵重生) != null) && (getBuffedValue(MapleBuffStat.召唤兽) == null) && (getSummonsSize() < 4) && (canSummon())) {
            MapleStatEffect eff = getStatForBuff(MapleBuffStat.幻灵重生);
            if (eff.makeChanceResult()) {
                eff.applyTo(this, this, false, null, eff.getDuration());
            }
        }
        if ((getJob() == 212) || (getJob() == 222) || (getJob() == 232)) {
            int[] skillIds = {2120010, 2220010, 2320011};
            for (int i : skillIds) {
                Skill skill = SkillFactory.getSkill(i);
                if (getTotalSkillLevel(skill) > 0) {
                    MapleStatEffect venomEffect = skill.getEffect(getTotalSkillLevel(skill));
                    if (ServerProperties.ShowPacket()) {
                        log.info(new StringBuilder().append("神秘瞄准术: ").append(skill.getId()).append(" - ").append(skill.getName()).append(" getAllLinkMid ").append(getAllLinkMid().size()).append(" Y ").append(venomEffect.getY()).toString());
                    }
                    if ((!venomEffect.makeChanceResult()) || (getAllLinkMid().size() >= venomEffect.getY())) {
                        break;
                    }
                    setLinkMid(oid, venomEffect.getX());
                    venomEffect.applyTo(this);
                    if (!ServerProperties.ShowPacket()) {
                        break;
                    }
                    log.info("神秘瞄准术: 开始加BUFF");
                    break;
                }

            }

        }

        if (skillid > 0) {
            Skill skil = SkillFactory.getSkill(skillid);
            MapleStatEffect effect = skil.getEffect(getTotalSkillLevel(skil));
            switch (skillid) {
                case 1078:
                case 11078:
                case 3111008:
                case 5811004:
                case 14101006:
                case 31111003:
                case 33111006:
                    addHP((int) Math.min(maxhp, Math.min((int) (totDamage * effect.getX() / 100.0D), this.stats.getMaxHp() / 2)));
                    break;
                case 5721003:
                case 5911006:
                case 5920011:
                case 22151002:
                    setLinkMid(oid, effect.getX());
                    break;
                case 33101005:
                    clearLinkMid();
            }
        }
    }

    public void afterAttack(int mobCount, int attackCount, int skillid) {
        switch (getJob()) {
            case 581:
            case 582:
                handleEnergyCharge(5810001, mobCount * attackCount);
                break;
            case 1510:
            case 1511:
            case 1512:
                handleEnergyCharge(15100004, mobCount * attackCount);
                break;
            case 111:
            case 112:
            case 1111:
            case 1112:
                if (((skillid != 1111008 ? 1 : 0) & (getBuffedValue(MapleBuffStat.斗气集中) != null ? 1 : 0)) == 0) {
                    break;
                }
                handleOrbgain();
        }

        if (getBuffedValue(MapleBuffStat.死亡猫头鹰) != null) {
            if (currentBattleshipHP() > 0) {
                decreaseBattleshipHP();
            }
            if (currentBattleshipHP() <= 0) {
                cancelEffectFromBuffStat(MapleBuffStat.死亡猫头鹰);
            }
        }
        if (!isIntern()) {
            cancelEffectFromBuffStat(MapleBuffStat.风影漫步);
            cancelEffectFromBuffStat(MapleBuffStat.潜入);
            MapleStatEffect ds = getStatForBuff(MapleBuffStat.隐身术);
            if ((ds != null) && ((ds.getSourceId() != 4330001) || (!ds.makeChanceResult()))) {
                cancelEffectFromBuffStat(MapleBuffStat.隐身术);
            }
        }
    }

    public void applyIceGage(int x) {
        updateSingleStat(MapleStat.ICE_GAGE, x);
    }

    public Rectangle getBounds() {
        return new Rectangle(getTruePosition().x - 25, getTruePosition().y - 75, 50, 75);
    }

    public boolean getCygnusBless() {
        int jobid = getJob();

        return ((getSkillLevel(12) > 0) && (jobid >= 0) && (jobid < 1000)) || ((getSkillLevel(10000012) > 0) && (jobid >= 1000) && (jobid < 2000)) || ((getSkillLevel(20000012) > 0) && ((jobid == 2000) || ((jobid >= 2100) && (jobid <= 2112)))) || ((getSkillLevel(20010012) > 0) && ((jobid == 2001) || ((jobid >= 2200) && (jobid <= 2218)))) || ((getSkillLevel(20020012) > 0) && ((jobid == 2002) || ((jobid >= 2300) && (jobid <= 2312)))) || ((getSkillLevel(20030012) > 0) && ((jobid == 2003) || ((jobid >= 2400) && (jobid <= 2412)))) || ((getSkillLevel(30000012) > 0) && ((jobid == 3000) || ((jobid >= 3200) && (jobid <= 3512)))) || ((getSkillLevel(30010012) > 0) && ((jobid == 3001) || ((jobid >= 3100) && (jobid <= 3112))) || ((getSkillLevel(50000012) > 0) && (GameConstants.is米哈尔(jobid))));
    }

    public byte get精灵祝福() {
        int jobid = getJob();
        if (((getSkillLevel(20021110) > 0) && ((jobid == 2002) || ((jobid >= 2300) && (jobid <= 2312)))) || (getSkillLevel(80001040) > 0)) {
            return 10;
        }
        return 0;
    }

    public void handleForceGain(int oid, int skillid) {
        handleForceGain(oid, skillid, 0);
    }

    public void handleForceGain(int oid, int skillid, int extraForce) {
        if ((!GameConstants.isForceIncrease(skillid)) && (extraForce <= 0)) {
            return;
        }
        int forceGain = 1;
        if ((getLevel() >= 30) && (getLevel() < 70)) {
            forceGain = 2;
        } else if ((getLevel() >= 70) && (getLevel() < 120)) {
            forceGain = 3;
        } else if (getLevel() >= 120) {
            forceGain = 4;
        }
        this.forcecounter += 1;
        addMP(extraForce > 0 ? extraForce : forceGain, true);
        getClient().getSession().write(MaplePacketCreator.showForce(oid, this.forcecounter, forceGain));
        if ((this.stats.mpRecoverProp > 0) && (extraForce <= 0)
                && (Randomizer.nextInt(100) <= this.stats.mpRecoverProp)) {
            this.forcecounter += 1;
            addMP(this.stats.mpRecover, true);
            getClient().getSession().write(MaplePacketCreator.showForce(oid, this.forcecounter, this.stats.mpRecoverProp));
        }
    }

    public void gainForce(int oid, int forceColor, int skillid) {
        int maxFuryLevel = getSkillLevel(SkillFactory.getSkill(31110009));
        this.forcecounter += 1;
        if (Randomizer.nextInt(100) >= 60) {
            addMP(forceColor);
        }
        getClient().getSession().write(MaplePacketCreator.showForce(oid, this.forcecounter, forceColor));
        if ((maxFuryLevel > 0) && ((skillid == 31000004) || (skillid == 31001006) || (skillid == 31001007) || (skillid == 31001008))) {
            this.forcecounter += 1;
            int rand = Randomizer.nextInt(100);
            if (rand >= 40) {
                addMP(forceColor);
                getClient().getSession().write(MaplePacketCreator.showForce(oid, this.forcecounter, 2));
            } else {
                getClient().getSession().write(MaplePacketCreator.showForce(oid, this.forcecounter, 3));
            }
        }
    }

    public int getCarte() {
        return this.carte;
    }

    public int getCarteByJob() {
        switch (getJob()) {
            case 2410:
                return 20;
            case 2411:
                return 30;
            case 2412:
                return 40;
        }
        return 0;
    }

    public void setCarte(int carte) {
        this.carte = carte;
    }

    public void handleCarteGain(int oid) {
        int[] skillIds = {24120002, 24100003};
        for (int i : skillIds) {
            Skill skill = SkillFactory.getSkill(i);
            if (getSkillLevel(skill) > 0) {
                MapleStatEffect effect = skill.getEffect(getSkillLevel(skill));
                if ((!effect.makeChanceResult()) || (Randomizer.nextInt(100) > 50)) {
                    break;
                }
                this.forcecounter += 1;
                getClient().getSession().write(MaplePacketCreator.showCarte(this, oid, skill.getId(), this.forcecounter));
                if (getCarte() >= getCarteByJob()) {
                    break;
                }
                this.carte += 1;
                getClient().getSession().write(MaplePacketCreator.showCarte(this.carte));
                break;
            }
        }
    }

    public void setDecorate(int id) {
        if ((id >= 1012276) && (id <= 1012280)) {
            this.decorate = id;
        } else {
            this.decorate = 0;
        }
    }

    public int getDecorate() {
        return this.decorate;
    }

    public void setElfEar(boolean show) {
        this.elfEar = (show ? 1 : 0);
    }

    public int getElfEar() {
        return this.elfEar;
    }

    public void setVip(int vip) {
        if (vip >= 10) {
            this.vip = 10;
        } else if (vip < 0) {
            this.vip = 0;
        } else {
            this.vip = vip;
        }
    }

    public int getVip() {
        return this.vip;
    }

    public boolean isVip() {
        return this.vip > 0;
    }

    public int getVipczz() {
        return vipczz;
    }

    public void setVipczz(int vipczz) {
        this.vipczz = vipczz;
    }

    public Timestamp getViptime() {
        if (getVip() == 0) {
            return null;
        }
        return this.viptime;
    }

    public void setViptime(long period) {
        if (period != 0L) {
            Timestamp expiration = new Timestamp(System.currentTimeMillis() + period * 24 * 60L * 60L * 1000L);
            setViptime(expiration);
        } else {
            setViptime(null);
        }
    }

    public void setViptime(Timestamp expire) {
        this.viptime = expire;
    }

    public int getBossLog(String boss) {
        return getBossLog(boss, 0);
    }

    public int getBossLog(String boss, int type) {
        try {
            int count = 0;

            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM bosslog WHERE characterid = ? AND bossid = ?");
            ps.setInt(1, this.id);
            ps.setString(2, boss);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt("count");
                Timestamp bossTime = rs.getTimestamp("time");
                rs.close();
                ps.close();
                if (type == 0) {
                    if (bossTime != null) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(bossTime.getTime());
                        if ((cal.get(5) + 1 <= Calendar.getInstance().get(5)) || (cal.get(2) + 1 <= Calendar.getInstance().get(2))) {
                            count = 0;
                            ps = con.prepareStatement("UPDATE bosslog SET count = 0  WHERE characterid = ? AND bossid = ?");
                            ps.setInt(1, this.id);
                            ps.setString(2, boss);
                            ps.executeUpdate();
                        }
                    }
                    rs.close();
                    ps.close();
                    ps = con.prepareStatement("UPDATE bosslog SET time = CURRENT_TIMESTAMP() WHERE characterid = ? AND bossid = ?");
                    ps.setInt(1, this.id);
                    ps.setString(2, boss);
                    ps.executeUpdate();
                }
            } else {
                PreparedStatement psu = con.prepareStatement("INSERT INTO bosslog (characterid, bossid, count, type) VALUES (?, ?, ?, ?)");
                psu.setInt(1, this.id);
                psu.setString(2, boss);
                psu.setInt(3, 0);
                psu.setInt(4, type);
                psu.executeUpdate();
                psu.close();
            }
            rs.close();
            ps.close();
            return count;
        } catch (Exception Ex) {
            log.error("Error while read bosslog.", Ex);
        }
        return -1;
    }

    public void dropMessage(String message) {
        dropMessage(6, message);
    }


    public void setBossLog(String boss) {
        setBossLog(boss, 0);
    }

    public void setBossLog(String boss, int type) {
        setBossLog(boss, type, 1);
    }

    public void setBossLog(String boss, int type, int count) {
        int bossCount = getBossLog(boss, type);
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE bosslog SET count = ?, type = ?, time = CURRENT_TIMESTAMP() WHERE characterid = ? AND bossid = ?");
            ps.setInt(1, bossCount + count);
            ps.setInt(2, type);
            ps.setInt(3, this.id);
            ps.setString(4, boss);
            ps.executeUpdate();
            ps.close();
        } catch (Exception Ex) {
            log.error("Error while set bosslog.", Ex);
        }
    }

    public void resetBossLog(String boss) {
        resetBossLog(boss, 0);
    }

    public void resetBossLog(String boss, int type) {
        try {
            Connection con = DatabaseConnection.getConnection();

            PreparedStatement ps = con.prepareStatement("UPDATE bosslog SET count = ?, type = ?, time = CURRENT_TIMESTAMP() WHERE characterid = ? AND bossid = ?");
            ps.setInt(1, 0);
            ps.setInt(2, type);
            ps.setInt(3, this.id);
            ps.setString(4, boss);
            ps.executeUpdate();
            ps.close();
        } catch (Exception Ex) {
            log.error("Error while reset bosslog.", Ex);
        }
    }

    public void updateCash() {
        this.client.getSession().write(MaplePacketCreator.showCharCash(this));
    }

    public short getSpace(int type) {
        return getInventory(MapleInventoryType.getByType((byte) type)).getNumFreeSlot();
    }

    public boolean haveSpace(int type) {
        short slot = getInventory(MapleInventoryType.getByType((byte) type)).getNextFreeSlot();
        return slot != -1;
    }

    public boolean haveSpaceForId(int itemid) {
        short slot = getInventory(GameConstants.getInventoryType(itemid)).getNextFreeSlot();
        return slot != -1;
    }

    public int getMerchantMeso() {
        int mesos = 0;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * from hiredmerch where characterid = ?");
            ps.setInt(1, this.id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                mesos = rs.getInt("Mesos");
            }
            rs.close();
            ps.close();
        } catch (SQLException se) {
            log.error("获取雇佣商店金币发生错误", se);
        }
        return mesos;
    }

    public List<Integer> getExcluded() {
        return this.excluded;
    }

    public void addExcluded(int itemId) {
        if (this.excluded.size() < 10) {
            this.excluded.add(Integer.valueOf(itemId));
        }
    }

    public int getHyPay(int type) {
        int pay = 0;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("select * from hypay where accname = ?");
            ps.setString(1, getClient().getAccountName());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (type == 1) {
                    pay = rs.getInt("pay");
                } else if (type == 2) {
                    pay = rs.getInt("payUsed");
                } else if (type == 3) {
                    pay = rs.getInt("pay") + rs.getInt("payUsed");
                } else if (type == 4) {
                    pay = rs.getInt("payReward");
                } else {
                    pay = 0;
                }
            } else {
                PreparedStatement psu = con.prepareStatement("insert into hypay (accname, pay, payUsed, payReward) VALUES (?, ?, ?, ?)");
                psu.setString(1, getClient().getAccountName());
                psu.setInt(2, 0);
                psu.setInt(3, 0);
                psu.setInt(4, 0);
                psu.executeUpdate();
                psu.close();
            }
            ps.close();
            rs.close();
        } catch (SQLException ex) {
            log.error("获取充值信息发生错误", ex);
        }
        return pay;
    }

    public int addHyPay(int hypay) {
        int pay = getHyPay(1);
        int payUsed = getHyPay(2);
        int payReward = getHyPay(4);
        if (hypay > pay) {
            return -1;
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE hypay SET pay = ? ,payUsed = ? ,payReward = ? where accname = ?");
            ps.setInt(1, pay - hypay);
            ps.setInt(2, payUsed + hypay);
            ps.setInt(3, payReward + hypay);
            ps.setString(4, getClient().getAccountName());
            ps.executeUpdate();
            ps.close();
            return 1;
        } catch (SQLException ex) {
            log.error("加减充值信息发生错误", ex);
        }
        return -1;
    }

    public int delPayReward(int pay) {
        int payReward = getHyPay(4);
        if (pay <= 0) {
            return -1;
        }
        if (pay > payReward) {
            return -1;
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE hypay SET payReward = ? where accname = ?");
            ps.setInt(1, payReward - pay);
            ps.setString(2, getClient().getAccountName());
            ps.executeUpdate();
            ps.close();
            return 1;
        } catch (SQLException ex) {
            log.error("加减消费奖励信息发生错误", ex);
        }
        return -1;
    }

    public int getMapleGetShopitems(String te) {
        int tp = -2;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("select * from maplegetshopitems where shopid = ?");
            ps.setInt(1, 0);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                switch (te) {
                    case "shopid":
                        tp = rs.getInt("shopid");
                        break;
                    case "shopitemid":
                        tp = rs.getInt("shopitemid");
                        break;
                    default:
                        tp = -1;
                        break;
                }
            } else {
                PreparedStatement psu = con.prepareStatement("insert into maplegetshopitems (shopitemid, shopid) VALUES (?, ?)");
                psu.setInt(1, 0);
                psu.setInt(2, 0);
                psu.executeUpdate();
                psu.close();
                ps.close();
                rs.close();
            }
        } catch (SQLException ex) {
            log.error("获取MapleGetShopitems信息发生错误", ex);
        }
        return tp;

    }

    public int getMapleShopitems(String te, int shopitemid) {
        int tp = 0;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("select * from mapleshopitems where shopitemid = ?");
            ps.setInt(1, shopitemid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                switch (te) {
                    case "shopid":
                        tp = rs.getInt("shopid");
                        break;
                    case "shopitemid":
                        tp = rs.getInt("shopitemid");
                        break;
                    case "itemid":
                        tp = rs.getInt("itemid");
                        break;
                    case "position":
                        tp = rs.getInt("position");
                        break;
                    default:
                        tp = 0;
                        break;
                }
            } else {
                PreparedStatement psu = con.prepareStatement("insert into mapleshopitems (shopitemid, shopid, itemid, position) VALUES (?, ?, ?, ?)");
                psu.setInt(1, shopitemid);
                psu.setInt(2, 0);
                psu.setInt(3, 0);
                psu.setInt(4, 0);
                psu.executeUpdate();
                psu.close();
                ps.close();
                rs.close();
            }
        } catch (SQLException ex) {
            log.error("获取MapleShopitems信息发生错误", ex);
        }
        return tp;
    }

    public int getMaplewingjr(String te) {
        return getMaplewingjr(te, this.id);
    }


    public int getMaplewingjr(String te, int ids) {
        int tp = 0;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("select * from maplewingjr where id = ?");
            ps.setInt(1, ids);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                switch (te) {
                    case "gpid1":
                        tp = rs.getInt("gpid1");
                        break;
                    case "gpid2":
                        tp = rs.getInt("gpid1");
                        break;
                    case "gpid3":
                        tp = rs.getInt("gpid1");
                        break;
                    default:
                        tp = 0;
                        break;
                }
            } else {
                PreparedStatement psu = con.prepareStatement("insert into maplewingjr (id, playername, gpid1, gpid2, gpid3) VALUES (?, ?, ?, ?, ?)");
                psu.setInt(1, ids);
                psu.setString(2, this.name);
                psu.setInt(3, 0);
                psu.setInt(4, 0);
                psu.setInt(5, 0);
                psu.executeUpdate();
                psu.close();
                ps.close();
                rs.close();
            }
        } catch (SQLException ex) {
            log.error("获取maplewingjr信息发生错误", ex);
        }
        return tp;
    }

    public int getMaplewinggp(String te, int ids) {
        int tp = 0;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("select * from maplewinggp where id = ?");
            ps.setInt(1, ids);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                switch (te) {
                    case "rate":
                        tp = rs.getInt("rate");
                        break;
                    case "point":
                        tp = rs.getInt("point");
                        break;
                    case "lastpoint":
                        tp = rs.getInt("lastpoint");
                        break;
                    case "id":
                        tp = rs.getInt("id");
                        break;
                    default:
                        tp = 0;
                        break;
                }
            } else {
                PreparedStatement psu = con.prepareStatement("insert into maplewinggp (id, name, rate, point, lastpoint) VALUES (?, ?, ?, ?, ?)");
                psu.setInt(1, ids);
                psu.setString(2, this.name);
                psu.setInt(3, 0);
                psu.setInt(4, 0);
                psu.setInt(5, 0);
                psu.executeUpdate();
                psu.close();
                ps.close();
                rs.close();
            }
        } catch (SQLException ex) {
            log.error("获取maplewinggp信息发生错误", ex);
        }
        return tp;
    }

    public int getMaplewinggpIdByName(String te) {
        int tp = 0;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("select * from maplewinggp where name = ?");
            ps.setString(1, te);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {

                tp = rs.getInt("id");

            } else {
                tp = 0;
                ps.close();
                rs.close();
            }
        } catch (SQLException ex) {
            log.error("获取maplewinggpIdByname信息发生错误", ex);
        }
        return tp;


    }

    public String getMaplewinggpname(int ids) {

        String tp = "错误tp";
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("select * from maplewinggp where id = ?");
            ps.setInt(1, ids);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {

                tp = rs.getString("name");

            } else {
                tp = "错误";
                ps.close();
                rs.close();
            }
        } catch (SQLException ex) {
            log.error("获取maplewinggps信息发生错误", ex);
        }
        return tp;


    }


    public String getMaplewings(String te) {
        return getMaplewings(te, this.id);
    }

    public String getMaplewings(String te, int ids) {
        String tp = null;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("select * from maplewing where cardid = ?");
            //  ps.setInt(1, this.id);
            ps.setInt(1, ids);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                switch (te) {
                    case "cardid":
                        tp = rs.getString("cardid");
                        break;
                    case "cardname":
                        tp = rs.getString("cardname");
                        break;
                    case "cardcolor":
                        tp = rs.getString("cardcolor");
                        break;
                    default:
                        tp = "错误";
                        break;
                }
            } else {
                if (ids == this.id) {
                    PreparedStatement psu = con.prepareStatement("insert into maplewing (cardid, cardname, cardcolor, cardlevel, cardmima, wmose, emose, maple, mapley, maplez, mapleb, savemose, savetime, havetime, gainmoses) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                    psu.setInt(1, this.id);
                    psu.setString(2, this.name);
                    psu.setString(3, "灰卡");
                    psu.setInt(4, 0);
                    psu.setInt(5, 0);
                    psu.setInt(6, 0);
                    psu.setInt(7, 0);
                    psu.setInt(8, 0);
                    psu.setInt(9, 0);
                    psu.setInt(10, 0);
                    psu.setInt(11, 0);
                    psu.setInt(12, 0);
                    psu.setInt(13, 0);
                    psu.setInt(14, 0);
                    psu.setInt(15, 0);
                    psu.executeUpdate();
                    psu.close();
                } else {
                    dropMessage(5, "所输入的账户ID不存在.");
                    return tp;
                }
                ps.close();
                rs.close();
            }
        } catch (SQLException ex) {
            log.error("获取Maplewing信息发生错误", ex);
            return tp;
        }
        return tp;

    }

    public int getMaplewing(String te) {
        return getMaplewing(te, this.id);
    }

    public int getMaplewing(String te, int ids) {
        int tp = -1;

        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("select * from maplewing where cardid = ?");
            //  ps.setInt(1, this.id);
            ps.setInt(1, ids);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                switch (te) {
                    case "cardid":
                        tp = rs.getInt("cardid");
                        break;
                    case "cardlevel":
                        tp = rs.getInt("cardlevel");
                        break;
                    case "cardmima":
                        tp = rs.getInt("cardmima");
                        break;
                    case "wmose":
                        tp = rs.getInt("wmose");
                        break;
                    case "emose":
                        tp = rs.getInt("emose");
                        break;
                    case "maple":
                        tp = rs.getInt("maple");
                        break;
                    case "mapley":
                        tp = rs.getInt("mapley");
                        break;
                    case "maplez":
                        tp = rs.getInt("maplez");
                        break;
                    case "mapleb":
                        tp = rs.getInt("mapleb");
                        break;
                    case "savemose":
                        tp = rs.getInt("savemose");
                        break;
                    case "savetime":
                        tp = rs.getInt("savetime");
                        break;
                    case "havetime":
                        tp = rs.getInt("havetime");
                        break;
                    case "gainmoses":
                        tp = rs.getInt("gainmoses");
                        break;
                    case "chongxiu":
                        tp = rs.getInt("chongxiu");
                        break;
                    case "jinbi":
                        tp = rs.getInt("jinbi");
                        break;
                    default:
                        tp = 0;
                        break;
                }
            } else {
                if (ids == this.id) {
                    PreparedStatement psu = con.prepareStatement("insert into maplewing (cardid, cardname, cardcolor, cardlevel, cardmima, wmose, emose, maple, mapley, maplez, mapleb, savemose, savetime, havetime, gainmoses, chongxiu, jinbi) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                    psu.setInt(1, this.id);
                    psu.setString(2, this.name);
                    psu.setString(3, "灰卡");
                    psu.setInt(4, 0);
                    psu.setInt(5, 0);
                    psu.setInt(6, 0);
                    psu.setInt(7, 0);
                    psu.setInt(8, 0);
                    psu.setInt(9, 0);
                    psu.setInt(10, 0);
                    psu.setInt(11, 0);
                    psu.setInt(12, 0);
                    psu.setInt(13, 0);
                    psu.setInt(14, 0);
                    psu.setInt(15, 0);
                    psu.setInt(16, 0);
                    psu.setInt(17, 0);
                    psu.executeUpdate();
                    psu.close();
                } else {
                    dropMessage(5, "所输入的账户ID不存在.");
                    return tp;
                }
                ps.close();
                rs.close();
            }
        } catch (SQLException ex) {
            log.error("获取Maplewing信息发生错误", ex);
            return tp;
        }
        return tp;
    }

    public int getMaplewingmap(String te) {
        return getMaplewingmap(te, this.id);
    }

    public int getMaplewingmap(String te, int ids) {
        int mds = 910000000;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("select * from maplewingmap where charid = ?");
            ps.setInt(1, ids);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                switch (te) {
                    case "nowmap":
                        mds = rs.getInt("nowmap");
                        break;
                    case "formermap1":
                        mds = rs.getInt("formermap1");
                        break;
                    case "formermap2":
                        mds = rs.getInt("formermap2");
                        break;
                    default:
                        mds = 0;
                        break;
                }
            } else {
                if (ids == this.id) {
                    PreparedStatement psu = con.prepareStatement("insert into maplewingmap (charid, charname, nowmap, formermap1, formermap2) VALUES (?, ?, ?, ?, ?)");
                    psu.setInt(1, this.id);
                    psu.setString(2, this.name);
                    psu.setInt(3, mds);
                    psu.setInt(4, mds);
                    psu.setInt(5, mds);
                    psu.executeUpdate();
                    psu.close();
                } else {
                    dropMessage(5, "所输入的账户ID不存在.");
                    return mds;
                }
                ps.close();
                rs.close();
            }

        } catch (SQLException ex) {
            log.error("获取Maplewing信息发生错误", ex);
            return mds;
        }
        return mds;
    }

    public void addMapleShopitems(int shopitemid, int shopid, int itemid, int positions) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps3 = con.prepareStatement("INSERT INTO mapleshopitems (shopitemid, shopid, itemid, position) VALUES (?, ?, ?, ?)");
            ps3.setInt(1, shopitemid);
            ps3.setInt(2, shopid);
            ps3.setInt(3, itemid);
            ps3.setInt(4, positions);
            ps3.executeUpdate();
            ps3.close();
        } catch (SQLException ex) {
            log.error("加减MapleShopitems信息发生错误，请检查MapleCharacter里的addMapleShopitems.", ex);
        }
    }

    public void addMapleGetShopitems(String te, int tas) {
        int ads = getMapleGetShopitems(te);
        int bes = ads + tas;
        if (bes < ads) {
            bes = ads;
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps3 = con.prepareStatement("UPDATE maplegetshopitems SET shopitemid = ? where shopid = ?");
            ps3.setInt(1, bes);
            ps3.setInt(2, 0);
            ps3.executeUpdate();
            ps3.close();
        } catch (SQLException ex) {
            log.error("加减MapleGetShopitems信息发生错误，请检查MapleCharacter里的addMapleGetShopitems.", ex);
        }
    }

    public int addMaplewing(String te, int tas) {
        return addMaplewing(te, tas, this.id);
    }

    public int addMaplewing(String te, int tas, int ids) {
        int tp = -1;

        int das = getMaplewing(te, ids);
        int ads = das + tas;
        if (ads < 2147483640) {
            try {
                Connection con = DatabaseConnection.getConnection();

                switch (te) {
                    case "cardname":
                        PreparedStatement ps1 = con.prepareStatement("UPDATE maplewing SET cardname = ? where cardid = ?");
                        ps1.setString(1, this.name);
                        ps1.setInt(2, ids);
                        ps1.executeUpdate();
                        ps1.close();
                        break;
                    case "cardcolor":
                        PreparedStatement ps2 = con.prepareStatement("UPDATE maplewing SET cardcolor = ? where cardid = ?");
                        ps2.setString(1, "灰卡");
                        ps2.setInt(2, ids);
                        ps2.executeUpdate();
                        ps2.close();
                        break;
                    case "cardlevel":
                        PreparedStatement ps3 = con.prepareStatement("UPDATE maplewing SET cardlevel = ? where cardid = ?");
                        ps3.setInt(1, das + tas);
                        ps3.setInt(2, ids);
                        ps3.executeUpdate();
                        ps3.close();
                        break;
                    case "cardmima":
                        PreparedStatement ps4 = con.prepareStatement("UPDATE maplewing SET cardmima = ? where cardid = ?");
                        ps4.setInt(1, das + tas);
                        ps4.setInt(2, ids);
                        ps4.executeUpdate();
                        ps4.close();
                        break;
                    case "wmose":
                        PreparedStatement ps5 = con.prepareStatement("UPDATE maplewing SET wmose = ? where cardid = ?");
                        ps5.setInt(1, das + tas);
                        ps5.setInt(2, ids);
                        ps5.executeUpdate();
                        ps5.close();
                        break;
                    case "emose":
                        PreparedStatement ps6 = con.prepareStatement("UPDATE maplewing SET emose = ? where cardid = ?");
                        ps6.setInt(1, das + tas);
                        ps6.setInt(2, ids);
                        ps6.executeUpdate();
                        ps6.close();
                        break;
                    case "maple":
                        PreparedStatement ps7 = con.prepareStatement("UPDATE maplewing SET maple = ? where cardid = ?");
                        ps7.setInt(1, das + tas);
                        ps7.setInt(2, ids);
                        ps7.executeUpdate();
                        ps7.close();
                        break;
                    case "mapley":
                        PreparedStatement ps8 = con.prepareStatement("UPDATE maplewing SET mapley = ? where cardid = ?");
                        ps8.setInt(1, das + tas);
                        ps8.setInt(2, ids);
                        ps8.executeUpdate();
                        ps8.close();
                        break;
                    case "maplez":
                        PreparedStatement ps9 = con.prepareStatement("UPDATE maplewing SET maplez = ? where cardid = ?");
                        ps9.setInt(1, das + tas);
                        ps9.setInt(2, ids);
                        ps9.executeUpdate();
                        ps9.close();
                        break;
                    case "mapleb":
                        PreparedStatement ps10 = con.prepareStatement("UPDATE maplewing SET mapleb = ? where cardid = ?");
                        ps10.setInt(1, das + tas);
                        ps10.setInt(2, ids);
                        ps10.executeUpdate();
                        ps10.close();
                        break;
                    case "savemose":
                        PreparedStatement ps11 = con.prepareStatement("UPDATE maplewing SET savemose = ? where cardid = ?");
                        ps11.setInt(1, das + tas);
                        ps11.setInt(2, ids);
                        ps11.executeUpdate();
                        ps11.close();
                        break;
                    case "savetime":
                        PreparedStatement ps12 = con.prepareStatement("UPDATE maplewing SET savetime = ? where cardid = ?");
                        ps12.setInt(1, das + tas);
                        ps12.setInt(2, ids);
                        ps12.executeUpdate();
                        ps12.close();
                        break;
                    case "havetime":
                        PreparedStatement ps13 = con.prepareStatement("UPDATE maplewing SET havetime = ? where cardid = ?");
                        ps13.setInt(1, das + tas);
                        ps13.setInt(2, ids);
                        ps13.executeUpdate();
                        ps13.close();
                        break;
                    case "gainmoses":
                        PreparedStatement ps14 = con.prepareStatement("UPDATE maplewing SET gainmoses = ? where cardid = ?");
                        ps14.setInt(1, das + tas);
                        ps14.setInt(2, ids);
                        ps14.executeUpdate();
                        ps14.close();
                        break;
                    case "chongxiu":
                        PreparedStatement ps15 = con.prepareStatement("UPDATE maplewing SET chongxiu = ? where cardid = ?");
                        ps15.setInt(1, das + tas);
                        ps15.setInt(2, ids);
                        ps15.executeUpdate();
                        ps15.close();
                        break;
                    case "jinbi":
                        PreparedStatement ps16 = con.prepareStatement("UPDATE maplewing SET jinbi = ? where cardid = ?");
                        ps16.setInt(1, das + tas);
                        ps16.setInt(2, ids);
                        ps16.executeUpdate();
                        ps16.close();
                        break;
                    default:
                        dropMessage(5, "选择类型错误，请联系Maplewing修复.");
                        tp = 0;
                        break;
                }
                if ((das > 0) && (ids == this.id)) {
                    String mds = "获得 " + getMaplewingName(te) + " (" + tas + ")";
                    dropMessage(-5, mds);
                    dropMessage(-1, mds);
                }

            } catch (SQLException ex) {
                log.error("加减Maplewing信息发生错误，请检查MapleCharacter里的addMaplewing.", ex);
                return tp;
            }
        } else {
            dropMessage(5, "所选择存入类型的余额和存入量的总和超出2147483640 无法再继续存入.请更换存入类型.");
            return tp;
        }

        if (getCheatTracker().canSaveDB()) {
            //   c.getPlayer().dropMessage(5, "开始保存角色数据...");
            saveToDB(false, false);
            //   c.getPlayer().dropMessage(5, "保存角色数据完成...");
        }

        return tp;
    }

    public void addMaplewinggp(String names, int rates, int point, int lastpoint) {
        int dsa = getMaplewinggp("id", 1);
        int idss = 0;
        for (int ids = 0; idss < dsa; ids++) {
            dsa = getMaplewinggp("id", dsa);
            idss++;

        }


        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("select * from maplewinggp where id = ?");
            //  ps.setInt(1, this.id);
            ps.setInt(1, idss);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {

                // } else {

                PreparedStatement psu = con.prepareStatement("insert into maplewinggp (id, name, rate, point, lastpointa) VALUES (?, ?, ?, ?, ?)");
                psu.setInt(1, idss);
                psu.setString(2, names);
                psu.setInt(3, rates);
                psu.setInt(4, point);
                psu.setInt(5, lastpoint);
                psu.executeUpdate();
                psu.close();

                ps.close();
                rs.close();
            }
        } catch (SQLException ex) {
            log.error("新增 股票 进入 Maplewinggp 发生错误 请检察源码 ", ex);

        }


    }


    public int addMaplewingjr(String te, int tas) {
        return addMaplewingjr(te, tas, this.id);
    }


    public int addMaplewingjr(String te, int tas, int ids) {
        int tp = -1;

        int das = getMaplewingjr(te, ids);
        int ads = das + tas;
        if (ads < 2147483640) {
            try {
                Connection con = DatabaseConnection.getConnection();

                switch (te) {
                    case "gpid1":
                        PreparedStatement ps1 = con.prepareStatement("UPDATE maplewingjr SET gpid1 = ? where id = ?");
                        ps1.setInt(1, das + tas);
                        ps1.setInt(2, ids);
                        ps1.executeUpdate();
                        ps1.close();
                        break;
                    case "gpid2":
                        PreparedStatement ps2 = con.prepareStatement("UPDATE maplewingjr SET gpid2 = ? where id = ?");
                        ps2.setInt(1, das + tas);
                        ps2.setInt(2, ids);
                        ps2.executeUpdate();
                        ps2.close();
                        break;
                    case "gpid3":
                        PreparedStatement ps3 = con.prepareStatement("UPDATE maplewingjr SET gpid3 = ? where id = ?");
                        ps3.setInt(1, das + tas);
                        ps3.setInt(2, ids);
                        ps3.executeUpdate();
                        ps3.close();
                        break;
                    default:
                        dropMessage(5, "选择类型" + te + "错误，请联系Maplewing修复.");
                        tp = 0;
                        break;
                }

            } catch (SQLException ex) {
                log.error("加减Maplewingjr信息发生错误，请检查MapleCharacter里的addMaplewingjr.", ex);
                return tp;
            }
        } else {
            dropMessage(5, "所选择存入类型的余额和存入量的总和超出2147483640 无法再继续存入.请更换存入类型.");
            return tp;
        }
        return tp;
    }


    public void upMaplewing() {
        upMaplewing(this.id);
    }

    public void upMaplewing(int ids) {
        int maple = getMaplewing("maple", ids);
        int emose = getMaplewing("emose", ids);
        int cardlv;
        int cardid = getMaplewing("cardid", ids);
        String color;

        if (cardid == 1) {
            cardlv = 11;
            color = "心卡";
        } else if (emose >= 2100000 || maple >= 2100000000) {
            cardlv = 10;
            color = "金卡";
        } else if (emose >= 100000 || maple >= 1000000000) {
            cardlv = 9;
            color = "红卡";
        } else if (emose >= 10000 || maple >= 100000000) {
            cardlv = 8;
            color = "黑卡";
        } else if (emose >= 1000 || maple >= 10000000) {
            cardlv = 7;
            color = "紫卡";
        } else if (emose >= 100 || maple >= 1000000) {
            cardlv = 6;
            color = "蓝卡";
        } else if (emose >= 10 || maple >= 100000) {
            cardlv = 5;
            color = "青卡";
        } else if (emose >= 1 || maple >= 10000) {
            cardlv = 4;
            color = "绿卡";
        } else if (maple >= 1000) {
            cardlv = 3;
            color = "黄卡";
        } else if (maple >= 100) {
            cardlv = 2;
            color = "橙卡";
        } else if (maple >= 10) {
            cardlv = 1;
            color = "白卡";
        } else {
            cardlv = 0;
            color = "灰卡";
        }

        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps1 = con.prepareStatement("UPDATE maplewing SET cardlevel = ? where cardid = ?");
            // ps1.setString(1, color);
            ps1.setInt(1, cardlv);
            ps1.setInt(2, ids);
            ps1.executeUpdate();
            ps1.close();
            PreparedStatement ps2 = con.prepareStatement("UPDATE maplewing SET cardcolor = ? where cardid = ?");
            ps2.setString(1, color);
            ps2.setInt(2, ids);
            ps2.executeUpdate();
            ps2.close();
        } catch (SQLException ex) {
            log.error("刷新Maplewing信息发生错误，请检查MapleCharacter里的upMaplewing.", ex);
        }
        if (this.id == 1) {
            dropMessage(5, "刷新MapleWing成功！");
        }
    }

    public int setMaplewing(String te, int tas) {
        return setMaplewing(te, tas, this.id);
    }

    public int setMaplewing(String te, int das, int ids) {
        int tp = -1;

        if (das < 2147483640) {
            try {
                Connection con = DatabaseConnection.getConnection();

                switch (te) {
                    case "cardname":
                        PreparedStatement ps1 = con.prepareStatement("UPDATE maplewing SET cardname = ? where cardid = ?");
                        ps1.setString(1, this.name);
                        ps1.setInt(2, ids);
                        ps1.executeUpdate();
                        ps1.close();
                        break;
                    case "cardcolor":
                        PreparedStatement ps2 = con.prepareStatement("UPDATE maplewing SET cardcolor = ? where cardid = ?");
                        ps2.setString(1, "灰卡");
                        ps2.setInt(2, ids);
                        ps2.executeUpdate();
                        ps2.close();
                        break;
                    case "cardlevel":
                        PreparedStatement ps3 = con.prepareStatement("UPDATE maplewing SET cardlevel = ? where cardid = ?");
                        ps3.setInt(1, das);
                        ps3.setInt(2, ids);
                        ps3.executeUpdate();
                        ps3.close();
                        break;
                    case "cardmima":
                        PreparedStatement ps4 = con.prepareStatement("UPDATE maplewing SET cardmima = ? where cardid = ?");
                        ps4.setInt(1, das);
                        ps4.setInt(2, ids);
                        ps4.executeUpdate();
                        ps4.close();
                        break;
                    case "wmose":
                        PreparedStatement ps5 = con.prepareStatement("UPDATE maplewing SET wmose = ? where cardid = ?");
                        ps5.setInt(1, das);
                        ps5.setInt(2, ids);
                        ps5.executeUpdate();
                        ps5.close();
                        break;
                    case "emose":
                        PreparedStatement ps6 = con.prepareStatement("UPDATE maplewing SET emose = ? where cardid = ?");
                        ps6.setInt(1, das);
                        ps6.setInt(2, ids);
                        ps6.executeUpdate();
                        ps6.close();
                        break;
                    case "maple":
                        PreparedStatement ps7 = con.prepareStatement("UPDATE maplewing SET maple = ? where cardid = ?");
                        ps7.setInt(1, das);
                        ps7.setInt(2, ids);
                        ps7.executeUpdate();
                        ps7.close();
                        break;
                    case "mapley":
                        PreparedStatement ps8 = con.prepareStatement("UPDATE maplewing SET mapley = ? where cardid = ?");
                        ps8.setInt(1, das);
                        ps8.setInt(2, ids);
                        ps8.executeUpdate();
                        ps8.close();
                        break;
                    case "maplez":
                        PreparedStatement ps9 = con.prepareStatement("UPDATE maplewing SET maplez = ? where cardid = ?");
                        ps9.setInt(1, das);
                        ps9.setInt(2, ids);
                        ps9.executeUpdate();
                        ps9.close();
                        break;
                    case "mapleb":
                        PreparedStatement ps10 = con.prepareStatement("UPDATE maplewing SET mapleb = ? where cardid = ?");
                        ps10.setInt(1, das);
                        ps10.setInt(2, ids);
                        ps10.executeUpdate();
                        ps10.close();
                        break;
                    case "savemose":
                        PreparedStatement ps11 = con.prepareStatement("UPDATE maplewing SET savemose = ? where cardid = ?");
                        ps11.setInt(1, das);
                        ps11.setInt(2, ids);
                        ps11.executeUpdate();
                        ps11.close();
                        break;
                    case "savetime":
                        PreparedStatement ps12 = con.prepareStatement("UPDATE maplewing SET savetime = ? where cardid = ?");
                        ps12.setInt(1, das);
                        ps12.setInt(2, ids);
                        ps12.executeUpdate();
                        ps12.close();
                        break;
                    case "havetime":
                        PreparedStatement ps13 = con.prepareStatement("UPDATE maplewing SET havetime = ? where cardid = ?");
                        ps13.setInt(1, das);
                        ps13.setInt(2, ids);
                        ps13.executeUpdate();
                        ps13.close();
                        break;
                    case "gainmoses":
                        PreparedStatement ps14 = con.prepareStatement("UPDATE maplewing SET gainmoses = ? where cardid = ?");
                        ps14.setInt(1, das);
                        ps14.setInt(2, ids);
                        ps14.executeUpdate();
                        ps14.close();
                        break;
                    case "chongxiu":
                        PreparedStatement ps15 = con.prepareStatement("UPDATE maplewing SET chongxiu = ? where cardid = ?");
                        ps15.setInt(1, das);
                        ps15.setInt(2, ids);
                        ps15.executeUpdate();
                        ps15.close();
                        break;
                    case "jinbi":
                        PreparedStatement ps16 = con.prepareStatement("UPDATE maplewing SET jinbi = ? where cardid = ?");
                        ps16.setInt(1, das);
                        ps16.setInt(2, ids);
                        ps16.executeUpdate();
                        ps16.close();
                        break;
                    default:
                        dropMessage(5, "setMaplewing选择类型错误，请联系Maplewing修复.");
                        tp = 0;
                        break;
                }
                if (das > 0) {
                    String mds = "您的 " + getMaplewingName(te) + " 值被设置为 (" + das + ")";
                    dropMessage(-5, mds);
                    dropMessage(-1, mds);
                }

            } catch (SQLException ex) {
                log.error("设置Maplewing信息发生错误，请检查MapleCharacter里的setMaplewing.", ex);
                return tp;
            }
        } else {
            dropMessage(5, "所选择设置类型的值的总和超出2147483640 无法设置.请更设置类型.");
            return tp;
        }

        if (getCheatTracker().canSaveDB()) {
            //   c.getPlayer().dropMessage(5, "开始保存角色数据...");
            saveToDB(false, false);
            //   c.getPlayer().dropMessage(5, "保存角色数据完成...");
        }

        return tp;
    }


    public int setMaplewinggp(String te, int das, int ids) {
        int tp = -1;
        int inds = getMaplewinggp("point", ids);
        if (das < 2147483640) {
            try {
                Connection con = DatabaseConnection.getConnection();

                switch (te) {
                    case "rate":
                        PreparedStatement ps1 = con.prepareStatement("UPDATE maplewinggp SET rate = ? where id = ?");
                        ps1.setInt(1, das);
                        ps1.setInt(2, ids);
                        ps1.executeUpdate();
                        ps1.close();
                        break;
                    case "point":
                        PreparedStatement ps2 = con.prepareStatement("UPDATE maplewinggp SET point = ? where id = ?");
                        ps2.setInt(1, das);
                        ps2.setInt(2, ids);
                        ps2.executeUpdate();
                        ps2.close();
                        break;
                    case "lastpoint":
                        PreparedStatement ps3 = con.prepareStatement("UPDATE maplewinggp SET lastpoint = ? where id = ?");
                        ps3.setInt(1, das);
                        ps3.setInt(2, ids);
                        ps3.executeUpdate();
                        ps3.close();
                        break;
                    default:
                        dropMessage(5, "setMaplewinggp选择类型" + te + "错误，请联系Maplewing修复.");
                        tp = 0;
                        break;
                }

            } catch (SQLException ex) {
                log.error("设置Maplewinggp信息发生错误，请检查MapleCharacter里的setMaplewinggp.", ex);
                return tp;
            }
        } else {
            dropMessage(5, "所选择设置类型的值的总和超出2147483640 无法设置.请更设置类型.");
            return tp;
        }
        return tp;
    }

    public int setMaplewingjr(String te, int tas) {
        return setMaplewingjr(te, tas, this.id);
    }


    public int setMaplewingjr(String te, int das, int ids) {
        int tp = -1;

        if (das < 2147483640) {
            try {
                Connection con = DatabaseConnection.getConnection();

                switch (te) {
                    case "gpid1":
                        PreparedStatement ps1 = con.prepareStatement("UPDATE maplewingjr SET gpid1 = ? where id = ?");
                        ps1.setInt(1, das);
                        ps1.setInt(2, ids);
                        ps1.executeUpdate();
                        ps1.close();
                        break;
                    case "gpid2":
                        PreparedStatement ps2 = con.prepareStatement("UPDATE maplewingjr SET gpid2 = ? where id = ?");
                        ps2.setInt(1, das);
                        ps2.setInt(2, ids);
                        ps2.executeUpdate();
                        ps2.close();
                        break;
                    case "gpid3":
                        PreparedStatement ps3 = con.prepareStatement("UPDATE maplewingjr SET gpid3 = ? where id = ?");
                        ps3.setInt(1, das);
                        ps3.setInt(2, ids);
                        ps3.executeUpdate();
                        ps3.close();
                        break;
                    default:
                        dropMessage(5, "setMaplewingjr选择类型" + te + "错误，请联系Maplewing修复.");
                        tp = 0;
                        break;
                }

            } catch (SQLException ex) {
                log.error("设置Maplewingjr信息发生错误，请检查MapleCharacter里的setMaplewingjr.", ex);
                return tp;
            }
        } else {
            dropMessage(5, "所选择设置类型的值的总和超出2147483640 无法设置.请更设置类型.");
            return tp;
        }
        return tp;
    }


    public void setMaplewingmap() {
        setMaplewingmap(getMapId());
    }

    public void setMaplewingmap(int mapids) {
        setMaplewingmap("nowmap", mapids, this.id);
    }

    public void setMaplewingmap(String te, int mapids) {
        setMaplewingmap(te, mapids, this.id);
    }

    public void setMaplewingmap(String te, int mapids, int ids) {
        int fmap = getMaplewingmap(te, ids);
        int fmaps = 0;//getMaplewingmap("formermap1", ids)
        int maps = getMapId();
        switch (maps) {
            case 150000000:
            case 150000001:
            case 150010000:
            case 150010100:
            case 150010200:
            case 150010300:
            case 150010400:
            case 150010410:
            case 150010411:
            case 150010412:
                fmaps = 150000000;
                break;
            case 910150001:
            case 910150002:
            case 910150003:
            case 910150004:
            case 910150100:
            case 910150220:
            case 910150230:
            case 101050020:
            case 101050000:
            case 101050010:
            case 101050030:
            case 101050031:
            case 101050032:
            case 101050033:
            case 101050100:
            case 101050200:
            case 101050300:
            case 101050400:
                fmaps = 101050000;
                break;
            default:
                break;
        }

        try {
            Connection con = DatabaseConnection.getConnection();

            switch (te) {
                case "nowmap":
                    if (fmaps > 0) {
                        PreparedStatement ps = con.prepareStatement("UPDATE maplewingmap SET nowmap = ?, formermap1 = ?, formermap2 = ? where charid = ?");
                        ps.setInt(1, mapids);
                        ps.setInt(2, fmap);
                        ps.setInt(3, fmaps);
                        ps.setInt(4, ids);
                        ps.executeUpdate();
                        ps.close();
                    } else {
                        PreparedStatement ps = con.prepareStatement("UPDATE maplewingmap SET nowmap = ?, formermap1 = ? where charid = ?");
                        ps.setInt(1, mapids);
                        ps.setInt(2, fmap);
                        ps.setInt(3, ids);
                        ps.executeUpdate();
                        ps.close();
                    }
                    break;
                case "formermap1":
                    PreparedStatement ps1 = con.prepareStatement("UPDATE maplewingmap SET formermap1 = ?, formermap2 = ? where charid = ?");
                    ps1.setInt(1, mapids);
                    ps1.setInt(2, fmap);
                    ps1.setInt(3, ids);
                    ps1.executeUpdate();
                    ps1.close();
                    break;
                case "formermap2":
                    PreparedStatement ps2 = con.prepareStatement("UPDATE maplewingmap SET formermap2 = ? where charid = ?");
                    ps2.setInt(1, fmap);
                    ps2.setInt(2, ids);
                    ps2.executeUpdate();
                    ps2.close();
                    break;
                default:
                    break;
            }

        } catch (SQLException ex) {
            log.error("设置Maplewing信息发生错误，请检查MapleCharacter里的setMaplewingmap.", ex);
        }

    }

    public String getColor() {
        return getColor(this.id);
    }

    public String getColor(int ids) {
        int cardlevel = getMaplewing("cardlevel", ids);
        String color;
        switch (cardlevel) {
            case 11:
                color = "心卡";
                break;
            case 10:
                color = "金卡";
                break;
            case 9:
                color = "红卡";
                break;
            case 8:
                color = "黑卡";
                break;
            case 7:
                color = "紫卡";
                break;
            case 6:
                color = "蓝卡";
                break;
            case 5:
                color = "青卡";
                break;
            case 4:
                color = "绿卡";
                break;
            case 3:
                color = "黄卡";
                break;
            case 2:
                color = "橙卡";
                break;
            case 1:
                color = "白卡";
                break;
            default:
                color = "灰卡";
                break;

        }
        return color;
    }

    public String getVipname(int ass) {
        return getVipname(this.id, ass);
    }

    public String getVipname() {
        return getVipname(this.id, 1);
    }

    public String getVipname(int mds, int ass) {
        int viplv = getMaplewing("cardlevel", mds);
        if (this.id == 1) {
            viplv = 11;
        }
        String color;
        switch (viplv) {
            case 11:
                color = "恋心VIP";
                break;
            case 10:
                color = "创世VIP";
                break;
            case 9:
                color = "神话VIP";
                break;
            case 8:
                color = "至尊VIP";
                break;
            case 7:
                color = "传说VIP";
                break;
            case 6:
                color = "英雄VIP";
                break;
            case 5:
                color = "终极VIP";
                break;
            case 4:
                color = "超级VIP";
                break;
            case 3:
                color = "高级VIP";
                break;
            case 2:
                color = "中级VIP";
                break;
            case 1:
                color = "初级VIP";
                break;
            default:
                color = "冒险家";
                break;

        }
        StringBuilder sb = new StringBuilder();
        switch (ass) {
            case 1:
                sb.append("<").append(color).append(">").append(" ").append(this.name);
                break;
            case 2:
                sb.append("<").append(color).append(">").append(" ");
                break;
            default:
                sb.append(color).append(" ");
                break;
        }

        return sb.toString();
    }


    public void autoban(String reason, int greason) {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(1), cal.get(2), cal.get(5) + 3, cal.get(11), cal.get(12));
        Timestamp TS = new Timestamp(cal.getTimeInMillis());
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE accounts SET banreason = ?, tempban = ?, greason = ? WHERE id = ?");
            ps.setString(1, reason);
            ps.setTimestamp(2, TS);
            ps.setInt(3, greason);
            ps.setInt(4, this.accountid);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            log.error("Error while autoban" + e);
        }
    }

    public boolean isBanned() {
        return this.isbanned;
    }

    public void sendPolice(int greason, String reason, int duration) {
        this.isbanned = true;
        WorldTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                MapleCharacter.this.client.disconnect(true, false);
            }
        }, duration);
    }

    public void sendPolice(String text) {
        this.client.getSession().write(MaplePacketCreator.sendPolice(text));
        this.isbanned = true;
        WorldTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                MapleCharacter.this.client.disconnect(true, false);
                if (MapleCharacter.this.client.getSession().isOpen()) {
                    MapleCharacter.this.client.getSession().close();
                }
            }
        }, 6000L);
    }

    public void startIpCheck() {
        if (!this.client.hasIpCheck()) {
            log.info(new StringBuilder().append("[作弊] 检测到玩家 ").append(getName()).append(" 登录器关闭，系统对其进行断开连接处理。").toString());
            sendPolice("检测到登录器关闭，游戏即将断开。");
        }
    }

    public void startCheck() {
        if (!this.client.hasCheck(getAccountID())) {
            log.info(new StringBuilder().append("[作弊] 检测到玩家 ").append(getName()).append(" 登录器关闭，系统对其进行断开连接处理。").toString());
            sendPolice("检测到登录器关闭，游戏即将断开。");
        }
    }

    public Timestamp getChrCreated() {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT createdate FROM characters WHERE id = ?");
            ps.setInt(1, getId());
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                return null;
            }
            Timestamp ret = rs.getTimestamp("createdate");
            rs.close();
            ps.close();
            return ret;
        } catch (SQLException e) {
            throw new DatabaseException("获取角色创建日期出错", e);
        }

    }

    public boolean isInJailMap() {
        return (getMapId() == 180000001) && (getGMLevel() == 0);
    }

    public int getWarning() {
        return warning;
    }

    public void setWarning(int warning) {
        this.warning = warning;
    }

    public void gainWarning(boolean warningEnabled) {
        this.warning += 1;
        Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append("[GM Message] 截至目前玩家: ").append(getName()).append(" (等级 ").append(getLevel()).append(") 该用户已被警告: ").append(this.warning).append(" 次！").toString()));
        if (warningEnabled == true) {
            if (this.warning == 1) {
                dropMessage(5, "这是你的第一次警告！请注意在游戏中勿使用非法程序！");
            } else if (this.warning == 2) {
                dropMessage(5, new StringBuilder().append("警告现在是第 ").append(this.warning).append(" 次。如果你再得到一次警告就会封号处理！").toString());
            } else if (this.warning >= 3) {
                ban(new StringBuilder().append(getName()).append(" 由于警告次数超过: ").append(this.warning).append(" 次，系统对其封号处理！").toString(), false, true, false);
                Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(0, new StringBuilder().append(" 玩家 ").append(getName()).append(" (等级 ").append(getLevel()).append(") 由于警告次数过多，系统对其封号处理！").toString()));
            }
        }
    }

    public int getBeans() {
        return this.beans;
    }

    public void gainBeans(int i, boolean show) {
        this.beans += i;
        if ((show) && (i != 0)) {
            dropMessage(-1, new StringBuilder().append("您").append(i > 0 ? "获得了 " : "消耗了 ").append(Math.abs(i)).append(" 个豆豆.").toString());
        }
    }

    public void setBeans(int i) {
        this.beans = i;
    }

    public int teachSkill(int skillId, int toChrId) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("DELETE FROM skills WHERE skillid = ? AND teachId = ?");
            ps.setInt(1, skillId);
            ps.setInt(2, this.id);
            ps.executeUpdate();
            ps.close();
            ps = con.prepareStatement("SELECT * FROM skills WHERE skillid = ? AND characterid = ?");
            ps.setInt(1, skillId);
            ps.setInt(2, toChrId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                PreparedStatement psskills = con.prepareStatement("INSERT INTO skills (characterid, skillid, skilllevel, masterlevel, expiration, teachId) VALUES (?, ?, ?, ?, ?, ?)");
                psskills.setInt(1, toChrId);
                psskills.setInt(2, skillId);
                psskills.setInt(3, 1);
                psskills.setByte(4, (byte) 1);
                psskills.setLong(5, -1);
                psskills.setInt(6, this.id);
                psskills.executeUpdate();
                psskills.close();
                return 1;
            }
            rs.close();
            ps.close();
            return -1;
        } catch (Exception Ex) {
            log.error("Error while read bosslog.", Ex);
        }
        return -1;
    }

    public void startLieDetector(boolean isItem) {
        if (!getAntiMacro().inProgress()) {
            getAntiMacro().startLieDetector(getName(), isItem, false);
        }
    }

    public int getDollars() {
        return this.dollars;
    }

    public int getShareLots() {
        return this.shareLots;
    }

    public void addDollars(int n) {
        this.dollars += n;
    }

    public void addShareLots(int n) {
        this.shareLots += n;
    }

    public int getReborns1() {
        return this.reborns;
    }

    public void gainReborns1(int i) {
        reborns += i;
    }

    public int getReborns() {
        return getMaplewing("chongxiu");
    }

    public void gainReborns(int i) {
        addMaplewing("chongxiu", i);
    }

    public int getChongxiu() {
        return getMaplewing("chongxiu");
    }

    public void gainChongxiu(int i) {
        addMaplewing("chongxiu", i);
    }

    public int getAPS() {
        return this.apstorage;
    }

    public void gainAPS(int aps) {
        this.apstorage += aps;
    }

    /**
     * 转身随机获得属性 和 APS 有关
     */
    public void doReborn() {
        Map stat = new EnumMap(MapleStat.class);
        this.reborns += 1;//转身次数加1
        setLevel((short) 1);
        setExp(0);
        setRemainingAp((short) 0);

        int oriStats = this.stats.getStr() + this.stats.getDex() + this.stats.getLuk() + this.stats.getInt();

        int str = Randomizer.rand(25, this.stats.getStr());
        int dex = Randomizer.rand(25, this.stats.getDex());
        int int_ = Randomizer.rand(25, this.stats.getInt());
        int luk = Randomizer.rand(25, this.stats.getLuk());

        int afterStats = str + dex + int_ + luk;

        int MAS = oriStats - afterStats + getRemainingAp();
        this.client.getPlayer().gainAPS(MAS);

        this.stats.recalcLocalStats(this);
        this.stats.setStr((short) str, this.client.getPlayer());
        this.stats.setDex((short) dex, this.client.getPlayer());
        this.stats.setInt((short) int_, this.client.getPlayer());
        this.stats.setLuk((short) luk, this.client.getPlayer());
        stat.put(MapleStat.力量, str);
        stat.put(MapleStat.敏捷, dex);
        stat.put(MapleStat.智力, int_);
        stat.put(MapleStat.运气, luk);
        stat.put(MapleStat.AVAILABLEAP, 0);
        updateSingleStat(MapleStat.等级, 1);
        updateSingleStat(MapleStat.职业, 0);
        updateSingleStat(MapleStat.经验, 0);
        this.client.getSession().write(MaplePacketCreator.updatePlayerStats(stat, false, this));
    }

    /**
     * 刷新人物属性
     */
    public void updatePlayerStats() {
        Map stat = new EnumMap(MapleStat.class);
        stat.put(MapleStat.力量, stats.getStr());
        stat.put(MapleStat.敏捷, stats.getDex());
        stat.put(MapleStat.智力, stats.getInt());
        stat.put(MapleStat.运气, stats.getLuk());
        stat.put(MapleStat.AVAILABLEAP, getRemainingAp());
        stat.put(MapleStat.等级, getLevel());
        stat.put(MapleStat.职业, getJob());
        stat.put(MapleStat.经验, getExp());
        client.getSession().write(MaplePacketCreator.updatePlayerStats(stat, this));
    }

    /**
     * 转身1
     */
    public void doReborn1() {
        Map stat = new EnumMap(MapleStat.class);
        this.reborns += 1;//转身次数加1
        setLevel((short) 1);
        setExp(0);

        int job = this.stats.getJob();
        this.stats.recalcLocalStats(this);
        stat.put(MapleStat.等级, 1);
        updateSingleStat(MapleStat.等级, 1);
        updateSingleStat(MapleStat.职业, job);
        updateSingleStat(MapleStat.经验, 0);
        this.client.getSession().write(MaplePacketCreator.updatePlayerStats(stat, false, this));
    }

    public void setMaxhp2(int maxhp) {
        Map stat = new EnumMap(MapleStat.class);
        this.stats.maxhp = ((int) maxhp);
        this.stats.hp = ((int) maxhp);

        stat.put(MapleStat.MAXHP, maxhp);
        stat.put(MapleStat.HP, maxhp);
        updateSingleStat(MapleStat.MAXHP, maxhp);
        updateSingleStat(MapleStat.HP, maxhp);
        this.client.getSession().write(MaplePacketCreator.updatePlayerStats(stat, false, this));
    }

    public void setMaxmp2(int maxmp) {
        Map stat = new EnumMap(MapleStat.class);
        this.stats.maxmp = ((int) maxmp);
        this.stats.mp = ((int) maxmp);

        stat.put(MapleStat.MAXMP, maxmp);
        stat.put(MapleStat.MP, maxmp);
        updateSingleStat(MapleStat.MAXMP, maxmp);
        updateSingleStat(MapleStat.MP, maxmp);
        this.client.getSession().write(MaplePacketCreator.updatePlayerStats(stat, false, this));
    }

    public void setCXHPMP(int a) {
        Map stat = new EnumMap(MapleStat.class);
        this.stats.maxmp = ((int) a);
        this.stats.mp = ((int) a);
        this.stats.maxhp = ((int) a);
        this.stats.hp = ((int) a);

        stat.put(MapleStat.MAXMP, a);
        stat.put(MapleStat.MP, a);
        stat.put(MapleStat.MAXHP, a);
        stat.put(MapleStat.HP, a);
        updateSingleStat(MapleStat.MAXMP, a);
        updateSingleStat(MapleStat.MP, a);
        updateSingleStat(MapleStat.MAXHP, a);
        updateSingleStat(MapleStat.HP, a);
        this.client.getSession().write(MaplePacketCreator.updatePlayerStats(stat, false, this));
    }

    public void Chongxiu(int jobs, int lv, int str, int dex, int in, int luk, int reborn) {
        Map stat = new EnumMap(MapleStat.class);
        this.reborns += reborn;//转身次数加reborn
        //  this.stats.maxmp = ((int) mp);
        //  this.stats.mp = ((int) mp);
        //  this.stats.maxhp = ((int) hp);
        //   this.stats.hp = ((int) hp);
        //  this.stats.str = ((short) str);
        // this.stats.dex = ((short) dex);
        //  this.stats.int_ = ((short) in);
        // this.stats.luk = ((short) luk);

        this.stats.recalcLocalStats(this);
        this.stats.setStr((short) str, this.client.getPlayer());
        this.stats.setDex((short) dex, this.client.getPlayer());
        this.stats.setInt((short) in, this.client.getPlayer());
        this.stats.setLuk((short) luk, this.client.getPlayer());

        setRemainingAp((short) 0);
        setLevel((short) lv);
        setExp(0);

        //  stat.put(MapleStat.MAXMP, mp);
        //   stat.put(MapleStat.MP, mp);
        //   stat.put(MapleStat.MAXHP, hp);
        //  stat.put(MapleStat.HP, hp);
        stat.put(MapleStat.力量, str);
        stat.put(MapleStat.敏捷, dex);
        stat.put(MapleStat.智力, in);
        stat.put(MapleStat.运气, luk);
        stat.put(MapleStat.AVAILABLEAP, getRemainingAp());
        //   updateSingleStat(MapleStat.MAXMP, mp);
        //   updateSingleStat(MapleStat.MP, mp);
        //    updateSingleStat(MapleStat.MAXHP, hp);
        //   updateSingleStat(MapleStat.HP, hp);
        updateSingleStat(MapleStat.等级, lv);
        updateSingleStat(MapleStat.职业, jobs);
        updateSingleStat(MapleStat.经验, 0);
        this.client.getSession().write(MaplePacketCreator.updatePlayerStats(stat, false, this));
    }

    public void Chongxiu(int lv, int sx) {
        Chongxiu(0, lv, sx, sx, sx, sx, 1);
    }

    public void Chongxiu(int lv, int str, int dex, int in, int luk) {
        Chongxiu(0, lv, str, dex, in, luk, 1);
    }


    public int getMaplewingJS(String te) {

        int dsd = 0;


        int wmose = getMaplewing("wmose");
        int emose = getMaplewing("emose");
        int savemose = getMaplewing("savemose");

        int lvs = getLevel();
        int meso = getMeso();
        int tmeso = wmose + emose * 10000 + savemose + (int) meso / 10000;
        int lvjmeso;

        int jmeso;

        if (lvs >= 250) {
            lvjmeso = 500;
        } else if (lvs >= 240) {
            lvjmeso = 450;
        } else if (lvs >= 230) {
            lvjmeso = 400;
        } else if (lvs >= 230) {
            lvjmeso = 350;
        } else if (lvs >= 220) {
            lvjmeso = 300;
        } else if (lvs >= 210) {
            lvjmeso = 250;
        } else if (lvs >= 200) {
            lvjmeso = 200;
        } else if (lvs >= 190) {
            lvjmeso = 150;
        } else if (lvs >= 180) {
            lvjmeso = 130;
        } else if (lvs >= 170) {
            lvjmeso = 100;
        } else if (lvs >= 160) {
            lvjmeso = 80;
        } else if (lvs >= 150) {
            lvjmeso = 50;
        } else if (lvs >= 140) {
            lvjmeso = 30;
        } else if (lvs >= 130) {
            lvjmeso = 20;
        } else if (lvs >= 120) {
            lvjmeso = 10;
        } else if (lvs >= 110) {
            lvjmeso = 5;
        } else if (lvs >= 100) {
            lvjmeso = 1;
        } else {
            lvjmeso = 0;
        }


        if (tmeso > 100000000) {
            jmeso = 100000 - lvjmeso;
        } else if (tmeso > 10000000) {
            jmeso = 50000 - lvjmeso;
        } else if (tmeso > 1000000) {
            jmeso = 30000 - lvjmeso;
        } else if (tmeso > 500000) {
            jmeso = 5000 - lvjmeso;
        } else if (tmeso > 400000) {
            jmeso = 4000 - lvjmeso;
        } else if (tmeso > 300000) {
            jmeso = 2000 - lvjmeso;
        } else if (tmeso > 200000) {
            jmeso = 1000 - lvjmeso;
        } else if (tmeso > 150000) {
            jmeso = 500 - lvjmeso;
        } else if (tmeso > 100000) {
            jmeso = 300 - lvjmeso;
        } else if (tmeso > 90000) {
            jmeso = 200 - lvjmeso;
        } else if (tmeso > 80000) {
            jmeso = 150 - lvjmeso;
        } else if (tmeso > 70000) {
            jmeso = 130 - lvjmeso;
        } else if (tmeso > 60000) {
            jmeso = 120 - lvjmeso;
        } else if (tmeso > 50000) {
            jmeso = 100 - lvjmeso;
        } else if (tmeso > 40000) {
            jmeso = 80 - lvjmeso;
        } else if (tmeso > 30000) {
            jmeso = 50 - lvjmeso;
        } else if (tmeso > 20000) {
            jmeso = 30 - lvjmeso;
        } else if (tmeso > 10000) {
            jmeso = 10 - lvjmeso;
        } else {
            jmeso = 0;
        }
        if (jmeso < 0) {
            jmeso = 0;
        }


        switch (te) {
            case "tmeso":
                dsd = tmeso;
                break;
            case "jmeso":
                dsd = jmeso;
                break;
            default:
                dsd = 0;
                break;
        }

        return dsd;

    }

    public int getMaplewingZJS(int te, int ds) {

        int savemose = getMaplewing("savemose");
        int gas = ds * savemose * 20;
        int lvs = getLevel();

        if (te == 0) {

            if (savemose >= 1000000000) {
                lvs *= 25000;
                gas += gas * 10;
            } else if (savemose >= 100000000) {
                lvs *= 15000;
                gas += gas * 5 + lvs;
            } else if (savemose >= 10000000) {
                lvs *= 10000;
                gas += gas * 2 + lvs;
            } else if (savemose >= 1000000) {
                lvs *= 5000;
                gas += gas * 2 + lvs;
            } else if (savemose >= 10000) {
                lvs *= 1000;
                gas += gas + 7000 + lvs;
            } else if (savemose >= 1000) {
                lvs *= 500;
                gas += gas + 3000 + lvs;
            } else if (savemose >= 100) {
                lvs *= 100;
                gas += gas + 1000 + lvs;
            } else if (savemose >= 10) {
                lvs *= 50;
                gas += gas + 500 + lvs;
            } else {
                lvs *= 10;
                gas += gas + 100 + lvs;
            }
        } else {

            gas = te * ds * 20;

            if (te >= 1000000000) {
                lvs *= 25000;
                gas += gas * 10;
            } else if (te >= 100000000) {
                lvs *= 15000;
                gas += gas * 5 + lvs;
            } else if (te >= 10000000) {
                lvs *= 10000;
                gas += gas * 2 + lvs;
            } else if (te >= 1000000) {
                lvs *= 5000;
                gas += gas * 2 + lvs;
            } else if (te >= 10000) {
                lvs *= 1000;
                gas += gas + 7000 + lvs;
            } else if (te >= 1000) {
                lvs *= 500;
                gas += gas + 3000 + lvs;
            } else if (te >= 100) {
                lvs *= 100;
                gas += gas + 1000 + lvs;
            } else if (te >= 10) {
                lvs *= 50;
                gas += gas + 500 + lvs;
            } else {
                lvs *= 10;
                gas += gas + 100 + lvs;
            }

        }
        if ((savemose == 0) && (te == 0)) {
            gas = 0;
        }

        return gas;
    }

    public void gainMapewingGX(int lvs) {
        //int lvs = getLevel();
        int gas;
        int gagx;
        int gahy;

        gahy = (lvs - lvs % 20) / 20;

        if (lvs <= 10) {
            gagx = 1;
        } else if (lvs <= 20) {
            gagx = 2;
        } else if (lvs <= 30) {
            gagx = 3;
        } else if (lvs <= 40) {
            gagx = 4;
        } else if (lvs <= 50) {
            gagx = 5;
        } else if (lvs <= 60) {
            gagx = 6;
        } else if (lvs <= 70) {
            gagx = 7;
        } else if (lvs <= 80) {
            gagx = 8;
        } else if (lvs <= 90) {
            gagx = 9;
        } else if (lvs <= 100) {
            gagx = 10;
        } else if (lvs <= 110) {
            gagx = 11;
        } else if (lvs <= 120) {
            gagx = 12;
        } else if (lvs <= 130) {
            gagx = 13;
        } else if (lvs <= 140) {
            gagx = 14;
        } else if (lvs <= 150) {
            gagx = 15;
        } else if (lvs <= 160) {
            gagx = 16;
        } else if (lvs <= 170) {
            gagx = 17;
        } else if (lvs <= 180) {
            gagx = 18;
        } else if (lvs <= 190) {
            gagx = 19;
        } else if (lvs <= 200) {
            gagx = 20;
        } else if (lvs <= 210) {
            gagx = 21;
        } else if (lvs <= 220) {
            gagx = 22;
        } else if (lvs <= 230) {
            gagx = 23;
        } else if (lvs <= 240) {
            gagx = 24;
        } else if (lvs <= 250) {
            gagx = 25;
        } else {
            gagx = 30;
        }

        addMaplewing("maple", gagx);
        addMaplewing("mapley", gahy);


    }


    public String getMaplewingName(String lvs) {

        String names = null;
        switch (lvs) {
            case "cardid":
                names = "账户ID";
                break;
            case "cardname":
                names = this.name;
                break;
            case "cardcolor":
                names = "卡号类别";
                break;
            case "cardlevel":
                names = "卡号等级";
                break;
            case "cardmima":
                names = "密码";
                break;
            case "wmose":
                names = "万级余额";
                break;
            case "emose":
                names = "亿级余额";
                break;
            case "maple":
                names = "贡献点";
                break;
            case "maplez":
                names = "枫叶储量";
                break;
            case "mapley":
                names = "活跃点";
                break;
            case "mapleb":
                names = "枫币储量";
                break;
            case "savemose":
                names = "债券";
                break;
            case "savetime":
                names = "债券约定时间";
                break;
            case "havetime":
                names = "债券剩余时间";
                break;
            case "gainmoses":
                names = "利息";
                break;
            case "chongxiu":
                names = "枫翼重修重天";
                break;

        }
        return names;
    }


    public void Maplem(String Text, int typedd) {
        for (Iterator n$ = ChannelServer.getAllInstances().iterator(); n$.hasNext(); ) {
            ChannelServer cservs = (ChannelServer) n$.next();
            Iterator i$ = cservs.getPlayerStorage().getAllCharacters().iterator();
            while (i$.hasNext()) {

                MapleCharacter players = (MapleCharacter) i$.next();
                //if (players.getGMLevel() < 10000000)
                players.getClient().getSession().write(MaplePacketCreator.startMapEffect((new StringBuilder()).append(Text).toString(), typedd, true));
            }
        }

    }


    public int get潜能数目改变概率() {

        return (int) getMaplewing("cardlevel") * (client.getChannelServer().get潜能数目改变基本概率() + getMaplewing("mapley") / 1000);
    }


    public int get鉴定出B级潜能概率() {
        int ras = (int) getMaplewing("cardlevel") * (client.getChannelServer().get潜能数目改变基本概率() + getMaplewing("mapley") * 10);
        if (ras >= 100) {
            ras = 100;
        } else if (ras < 0) {
            ras = 0;
        }
        return ras;
    }

    public int get鉴定出A级潜能概率() {
        int ras = (int) getMaplewing("cardlevel") * (client.getChannelServer().get潜能数目改变基本概率() + getMaplewing("mapley") / 10);
        if (ras >= 100) {
            ras = 100;
        } else if (ras < 0) {
            ras = 0;
        }
        return ras;
    }

    public int get鉴定出S级潜能概率() {
        int ras = (int) getMaplewing("cardlevel") * (client.getChannelServer().get潜能数目改变基本概率() + getMaplewing("mapley") / 100);
        if (ras >= 100) {
            ras = 100;
        } else if (ras < 0) {
            ras = 0;
        }
        return ras;
    }

    public int get鉴定出SS级潜能概率() {
        int ras = (int) getMaplewing("cardlevel") + (client.getChannelServer().get潜能数目改变基本概率() / 10 + getMaplewing("mapley") / 10000);
        if (ras >= 100) {
            ras = 100;
        } else if (ras < 0) {
            ras = 0;
        }
        return ras;
    }

    public int get潜能1的潜能等级概率() {
        int ras = (int) getMaplewing("cardlevel") + (client.getChannelServer().get潜能数目改变基本概率() + getMaplewing("mapley") / 10000);
        if (ras >= 100) {
            ras = 100;
        } else if (ras < 0) {
            ras = 0;
        }
        return ras;
    }

    public int get潜能2的潜能等级概率() {
        int ras = (int) getMaplewing("cardlevel") * (client.getChannelServer().get潜能数目改变基本概率() + getMaplewing("mapley") / 10000) - 10;
        if (ras >= 100) {
            ras = 100;
        } else if (ras < 0) {
            ras = 0;
        }
        return ras;
    }

    public int get潜能3的潜能等级概率() {
        int ras = (int) getMaplewing("cardlevel") * (client.getChannelServer().get潜能数目改变基本概率() + getMaplewing("mapley") / 10000) - 30;
        if (ras >= 100) {
            ras = 100;
        } else if (ras < 0) {
            ras = 0;
        }
        return ras;
    }

    public int get潜能4的潜能等级概率() {
        int ras = (int) getMaplewing("cardlevel") + (client.getChannelServer().get潜能数目改变基本概率() + getMaplewing("mapley") / 10000) - 60;
        if (ras >= 100) {
            ras = 100;
        } else if (ras < 0) {
            ras = 0;
        }
        return ras;
    }

    public int get潜能5的潜能等级概率() {
        int ras = (int) getMaplewing("cardlevel") + (client.getChannelServer().get潜能数目改变基本概率() + getMaplewing("mapley") / 10000) - 90;
        if (ras >= 100) {
            ras = 100;
        } else if (ras < 0) {
            ras = 0;
        }
        return ras;
    }

    public int 鉴定概率(String md) {
        int rate = 0;


        int B级概率 = get鉴定出B级潜能概率();
        int A级概率 = get鉴定出A级潜能概率();
        int S级概率 = get鉴定出S级潜能概率();
        int SS级概率 = get鉴定出SS级潜能概率();

        int 潜能等级概率1 = get潜能1的潜能等级概率();
        int 潜能等级概率2 = get潜能2的潜能等级概率();
        int 潜能等级概率3 = get潜能3的潜能等级概率();
        int 潜能等级概率4 = get潜能4的潜能等级概率();
        int 潜能等级概率5 = get潜能5的潜能等级概率();
        switch (md) {
            case "B级概率":
                rate = B级概率;
                break;
            case "A级概率":
                rate = A级概率;
                break;
            case "S级概率":
                rate = S级概率;
                break;
            case "SS级概率":
                rate = SS级概率;
                break;
            case "潜能等级概率1":
                rate = 潜能等级概率1;
                break;
            case "潜能等级概率2":
                rate = 潜能等级概率2;
                break;
            case "潜能等级概率3":
                rate = 潜能等级概率3;
                break;
            case "潜能等级概率4":
                rate = 潜能等级概率4;
                break;
            case "潜能等级概率5":
                rate = 潜能等级概率5;
                break;

        }

        return rate;

    }


    public void 杀死BOSS给奖励(String md, int rate, int mob) {
        int ds = 0;
        switch (mob) {
            //扎昆
            case 8800000:
            case 8800001:
            case 8800002:
            case 9400900:
            case 9400901:
            case 9600102:
            case 9600103:
            case 9600104:
                ds = 111;
                break;
            case 8800100:
            case 8800101:
            case 8800102:
                ds = 333;
                break;
            //黑龙
            case 8810000:
            case 8810001:
            case 8810002:
            case 8810003:
            case 8810004:
            case 8810005:
            case 8810006:
            case 8810007:
            case 8810008:
            case 8810009:
                ds = 100;
                break;
            case 8810100:
            case 8810101:
            case 8810102:
            case 8810103:
            case 8810104:
            case 8810105:
            case 8810106:
            case 8810107:
            case 8810108:
            case 8810109:
                ds = 200;
                break;
            //闹钟
            case 8500001:
            case 8500002:
            case 9300214:
            case 9500180:
            case 9500181:
            case 9500331:
            case 9500362:
            case 9305133:
            case 9305233:
            case 9305333:
            case 9300513:
                ds = 33;
                break;
            //希纳斯
            case 8850011:
            case 8850012:
            case 9001053:
            case 9600135:
                ds = 1111;
                break;
            //班·雷昂
            case 8840000:
            case 8840006:
            case 8840010:
            case 9500408:
            case 9500412:
                ds = 100;
                break;
            //阿卡伊勒
            case 9300303:
            case 9300304:
            case 8860000:
            case 9600136:
                ds = 222;
                break;
            //骑士团
            case 8850000:
            case 8850001:
            case 8850002:
            case 8850003:
            case 8850004:
                ds = 111;
                break;
            //神的黄昏 雕像
            case 8820003:
            case 8820004:
            case 8820005:
            case 8820006:
            case 8820002:
                ds = 333;
                break;
            //时间的宠儿－品克缤
            case 8820000:
                ds = 1000;
                break;
            case 2220000://红蜗牛王
            case 6130101://蘑菇王
            case 6300005://僵尸蘑菇王
            case 8220007://蓝蘑菇王
                ds = 5;
                break;
        }
        addMaplewing(md, ds);

    }

    public String 取得委托任务名称(int ms, int ids) {
        String mm = "该委托任务名称不存在 ID：" + ids;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("select * from maplewingwt where id = ?");
            //  ps.setInt(1, this.id);
            ps.setInt(1, ids);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                if (ms == 1) {
                    mm = rs.getString("name");
                } else {
                    mm = rs.getString("neirong");
                }
            }
        } catch (SQLException ex) {
            log.error("取得委托任务名称 wt信息发生错误", ex);

        }
        return mm;
    }


    public int 取得委托任务信息(String te, int ids) {
        int tp = -2;

        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("select * from maplewingwt where id = ?");
            //  ps.setInt(1, this.id);
            ps.setInt(1, ids);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                switch (te) {
                    case "id":
                        tp = rs.getInt("id");
                        break;
                    case "fnpc":
                        tp = rs.getInt("fnpc");
                        break;
                    case "nid":
                        tp = rs.getInt("nid");
                        break;
                    case "nlevel":
                        tp = rs.getInt("nlevel");
                        break;
                    case "njob":
                        tp = rs.getInt("njob");
                        break;
                    case "nchongxiu":
                        tp = rs.getInt("nchongxiu");
                        break;
                    case "nguild":
                        tp = rs.getInt("nguild");
                        break;
                    case "mob":
                        tp = rs.getInt("mob");
                        break;
                    case "nmob":
                        tp = rs.getInt("nmob");
                        break;
                    case "item":
                        tp = rs.getInt("item");
                        break;
                    case "nitem":
                        tp = rs.getInt("nitem");
                        break;
                    case "npc":
                        tp = rs.getInt("npc");
                        break;
                    case "map":
                        tp = rs.getInt("map");
                        break;
                    case "reward1maple":
                        tp = rs.getInt("reward1maple");
                        break;
                    case "reward2mapley":
                        tp = rs.getInt("reward2mapley");
                        break;
                    case "reward3wmose":
                        tp = rs.getInt("reward3wmose");
                        break;

                    case "reward4emose":
                        tp = rs.getInt("reward4emose");
                        break;
                    case "reward5nx":
                        tp = rs.getInt("reward5nx");
                        break;

                    case "rewarditem":
                        tp = rs.getInt("rewarditem");
                        break;

                    case "itemsl":
                        tp = rs.getInt("itemsl");
                        break;
                    case "maxcishu":
                        tp = rs.getInt("maxcishu");
                        break;
                    case "exp":
                        tp = rs.getInt("exp");
                        break;
                    case "meso":
                        tp = rs.getInt("meso");
                        break;
                    case "level":
                        tp = rs.getInt("level");
                        break;
                    case "hastrue":
                        tp = rs.getInt("hastrue");
                        break;


                    default:
                        tp = -5;
                        break;
                }

            }
        } catch (SQLException ex) {
            tp = -9;
            // log.error("获取Maplewing wt信息发生错误", ex);
            return tp;//-2
        }
        return tp;
    }

    public int 取得玩家委托任务信息(String te) {
        return 取得玩家委托任务信息(te, this.id);
    }

    public String 取得委托玩家名称() {
        return 取得委托玩家名称(this.id);
    }

    public String 取得委托玩家名称(int ids) {
        String mms = "未知";
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("select * from maplewingplayerwt where id = ?");
            //  ps.setInt(1, this.id);
            ps.setInt(1, ids);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                mms = rs.getString("name");
            }

        } catch (SQLException ex) {
            log.error("获取Maplewing wt信息发生错误", ex);
        }

        return mms;

    }

    public int 取得玩家委托任务信息(String te, int ids) {
        int tp = -2;

        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("select * from maplewingplayerwt where id = ?");
            //  ps.setInt(1, this.id);
            ps.setInt(1, ids);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                switch (te) {
                    case "id":
                        tp = rs.getInt("id");
                        break;
                    case "renwu1":
                        tp = rs.getInt("renwu1");
                        break;
                    case "mobid1":
                        tp = rs.getInt("mobid1");
                        break;
                    case "mob1":
                        tp = rs.getInt("mob1");
                        break;
                    case "renwu2":
                        tp = rs.getInt("renwu2");
                        break;
                    case "mobid2":
                        tp = rs.getInt("mobid2");
                        break;
                    case "mob2":
                        tp = rs.getInt("mob2");
                        break;
                    case "renwu3":
                        tp = rs.getInt("renwu3");
                        break;
                    case "mobid3":
                        tp = rs.getInt("mobid3");
                        break;
                    case "mob3":
                        tp = rs.getInt("mob3");
                        break;
                    case "renwu4":
                        tp = rs.getInt("renwu4");
                        break;
                    case "mobid4":
                        tp = rs.getInt("mobid4");
                        break;
                    case "mob4":
                        tp = rs.getInt("mob4");
                        break;
                    case "renwu5":
                        tp = rs.getInt("renwu5");
                        break;

                    case "mobid5":
                        tp = rs.getInt("mobid5");
                        break;
                    case "mob5":
                        tp = rs.getInt("mob5");
                        break;

                    case "renwu6":
                        tp = rs.getInt("renwu6");
                        break;
                    case "mobid6":
                        tp = rs.getInt("mobid6");
                        break;
                    case "mob6":
                        tp = rs.getInt("mob6");
                        break;
                    case "renwu7":
                        tp = rs.getInt("renwu7");
                        break;
                    case "mobid7":
                        tp = rs.getInt("mobid7");
                        break;
                    case "mob7":
                        tp = rs.getInt("mob7");
                        break;
                    case "renwu8":
                        tp = rs.getInt("renwu8");
                        break;
                    case "mobid8":
                        tp = rs.getInt("mobid8");
                        break;
                    case "mob8":
                        tp = rs.getInt("mob8");
                        break;
                    case "renwu9":
                        tp = rs.getInt("renwu9");
                        break;

                    case "mobid9":
                        tp = rs.getInt("mobid9");
                        break;
                    case "mob9":
                        tp = rs.getInt("mob9");
                        break;

                    default:
                        tp = -5;
                        break;
                }
            } else {
                if (ids == this.id) {
                    PreparedStatement psu = con.prepareStatement("insert into maplewingplayerwt (id, name, renwu1, mobid1, mob1, renwu2, mobid2, mob2, renwu3, mobid3, mob3, renwu4, mobid4, mob4, renwu5, mobid5, mob5, renwu6, mobid6, mob6, renwu7, mobid7, mob7, renwu8, mobid8, mob8, renwu9, mobid9, mob9) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                    psu.setInt(1, this.id);
                    psu.setString(2, this.name);
                    psu.setInt(3, 0);//1
                    psu.setInt(4, 0);
                    psu.setInt(5, 0);
                    psu.setInt(6, 0);//2
                    psu.setInt(7, 0);
                    psu.setInt(8, 0);
                    psu.setInt(9, 0);//3
                    psu.setInt(10, 0);
                    psu.setInt(11, 0);
                    psu.setInt(12, -1);//4
                    psu.setInt(13, 0);
                    psu.setInt(14, 0);
                    psu.setInt(15, -1);//5
                    psu.setInt(16, 0);
                    psu.setInt(17, 0);
                    psu.setInt(18, -1);//6
                    psu.setInt(19, 0);
                    psu.setInt(20, 0);
                    psu.setInt(21, -1);//7
                    psu.setInt(22, 0);
                    psu.setInt(23, 0);
                    psu.setInt(24, -1);//8
                    psu.setInt(25, 0);
                    psu.setInt(26, 0);
                    psu.setInt(27, -1);//9
                    psu.setInt(28, 0);
                    psu.setInt(29, 0);
                    psu.executeUpdate();
                    psu.close();
                } else {
                    dropMessage(5, "所输入的账户ID不存在.");
                    return tp;
                }
                ps.close();
                rs.close();
            }


        } catch (SQLException ex) {
            log.error("获取Maplewing wt信息发生错误", ex);
            return tp;//-2
        }
        return tp;
    }

    public void 设置委托任务名称(String das, int ids) {

        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps1 = con.prepareStatement("UPDATE maplewingwt SET name = ? where id = ?");
            ps1.setString(1, das);
            ps1.setInt(2, ids);
            ps1.executeUpdate();
            ps1.close();

        } catch (SQLException ex) {
            log.error(" 设置委托任务名称 发生错误，请检查MapleCharacter里的  设置委托任务信息.", ex);
        }


    }

    public int 设置委托任务信息(String te, int das, int ids) {
        int tp = -1;

        if (das < 2147483640) {
            try {
                Connection con = DatabaseConnection.getConnection();

                switch (te) {
                    case "nid":
                        PreparedStatement ps1 = con.prepareStatement("UPDATE maplewingwt SET nid = ? where id = ?");
                        ps1.setInt(1, das);
                        ps1.setInt(2, ids);
                        ps1.executeUpdate();
                        ps1.close();
                        break;
                    case "fnpc":
                        PreparedStatement ps2 = con.prepareStatement("UPDATE maplewingwt SET fnpc = ? where id = ?");
                        ps2.setInt(1, das);
                        ps2.setInt(2, ids);
                        ps2.executeUpdate();
                        ps2.close();
                        break;
                    case "nlevel":
                        PreparedStatement ps3 = con.prepareStatement("UPDATE maplewingwt SET nlevel = ? where id = ?");
                        ps3.setInt(1, das);
                        ps3.setInt(2, ids);
                        ps3.executeUpdate();
                        ps3.close();
                        break;
                    case "njob":
                        PreparedStatement ps4 = con.prepareStatement("UPDATE maplewingwt SET njob = ? where id = ?");
                        ps4.setInt(1, das);
                        ps4.setInt(2, ids);
                        ps4.executeUpdate();
                        ps4.close();
                        break;
                    case "nchongxiu":
                        PreparedStatement ps5 = con.prepareStatement("UPDATE maplewingwt SET nchongxiu = ? where id = ?");
                        ps5.setInt(1, das);
                        ps5.setInt(2, ids);
                        ps5.executeUpdate();
                        ps5.close();
                        break;
                    case "nguild":
                        PreparedStatement ps6 = con.prepareStatement("UPDATE maplewingwt SET nguild = ? where id = ?");
                        ps6.setInt(1, das);
                        ps6.setInt(2, ids);
                        ps6.executeUpdate();
                        ps6.close();
                        break;
                    case "mob":
                        PreparedStatement ps7 = con.prepareStatement("UPDATE maplewingwt SET mob = ? where id = ?");
                        ps7.setInt(1, das);
                        ps7.setInt(2, ids);
                        ps7.executeUpdate();
                        ps7.close();
                        break;
                    case "nmob":
                        PreparedStatement ps8 = con.prepareStatement("UPDATE maplewingwt SET nmob = ? where id = ?");
                        ps8.setInt(1, das);
                        ps8.setInt(2, ids);
                        ps8.executeUpdate();
                        ps8.close();
                        break;
                    case "item":
                        PreparedStatement ps9 = con.prepareStatement("UPDATE maplewingwt SET item = ? where id = ?");
                        ps9.setInt(1, das);
                        ps9.setInt(2, ids);
                        ps9.executeUpdate();
                        ps9.close();
                        break;
                    case "npc":
                        PreparedStatement ps10 = con.prepareStatement("UPDATE maplewingwt SET npc = ? where id = ?");
                        ps10.setInt(1, das);
                        ps10.setInt(2, ids);
                        ps10.executeUpdate();
                        ps10.close();
                        break;
                    case "map":
                        PreparedStatement ps11 = con.prepareStatement("UPDATE maplewingwt SET map = ? where id = ?");
                        ps11.setInt(1, das);
                        ps11.setInt(2, ids);
                        ps11.executeUpdate();
                        ps11.close();
                        break;
                    case "reward1maple":
                        PreparedStatement ps12 = con.prepareStatement("UPDATE maplewingwt SET reward1maple = ? where id = ?");
                        ps12.setInt(1, das);
                        ps12.setInt(2, ids);
                        ps12.executeUpdate();
                        ps12.close();
                        break;
                    case "reward2mapley":
                        PreparedStatement ps13 = con.prepareStatement("UPDATE maplewingwt SET reward2mapley = ? where id = ?");
                        ps13.setInt(1, das);
                        ps13.setInt(2, ids);
                        ps13.executeUpdate();
                        ps13.close();
                        break;
                    case "reward3wmose":
                        PreparedStatement ps14 = con.prepareStatement("UPDATE maplewingwt SET reward3wmose = ? where id = ?");
                        ps14.setInt(1, das);
                        ps14.setInt(2, ids);
                        ps14.executeUpdate();
                        ps14.close();
                        break;
                    case "reward4emose":
                        PreparedStatement ps15 = con.prepareStatement("UPDATE maplewingwt SET reward4emose = ? where id = ?");
                        ps15.setInt(1, das);
                        ps15.setInt(2, ids);
                        ps15.executeUpdate();
                        ps15.close();
                        break;
                    case "reward5nx":
                        PreparedStatement ps16 = con.prepareStatement("UPDATE maplewingwt SET reward5nx = ? where id = ?");
                        ps16.setInt(1, das);
                        ps16.setInt(2, ids);
                        ps16.executeUpdate();
                        ps16.close();
                        break;
                    case "rewarditem":
                        PreparedStatement ps17 = con.prepareStatement("UPDATE maplewingwt SET rewarditem = ? where id = ?");
                        ps17.setInt(1, das);
                        ps17.setInt(2, ids);
                        ps17.executeUpdate();
                        ps17.close();
                        break;
                    case "itemsl":
                        PreparedStatement ps18 = con.prepareStatement("UPDATE maplewingwt SET itemsl = ? where id = ?");
                        ps18.setInt(1, das);
                        ps18.setInt(2, ids);
                        ps18.executeUpdate();
                        ps18.close();
                        break;
                    case "maxcishu":
                        PreparedStatement ps19 = con.prepareStatement("UPDATE maplewingwt SET maxcishu = ? where id = ?");
                        ps19.setInt(1, das);
                        ps19.setInt(2, ids);
                        ps19.executeUpdate();
                        ps19.close();
                        break;
                    case "exp":
                        PreparedStatement ps20 = con.prepareStatement("UPDATE maplewingwt SET exp = ? where id = ?");
                        ps20.setInt(1, das);
                        ps20.setInt(2, ids);
                        ps20.executeUpdate();
                        ps20.close();
                        break;
                    case "meso":
                        PreparedStatement ps21 = con.prepareStatement("UPDATE maplewingwt SET meso = ? where id = ?");
                        ps21.setInt(1, das);
                        ps21.setInt(2, ids);
                        ps21.executeUpdate();
                        ps21.close();
                        break;
                    case "level":
                        PreparedStatement ps22 = con.prepareStatement("UPDATE maplewingwt SET level = ? where id = ?");
                        ps22.setInt(1, das);
                        ps22.setInt(2, ids);
                        ps22.executeUpdate();
                        ps22.close();
                        break;
                    case "hastrue":
                        PreparedStatement ps23 = con.prepareStatement("UPDATE maplewingwt SET hastrue = ? where id = ?");
                        ps23.setInt(1, das);
                        ps23.setInt(2, ids);
                        ps23.executeUpdate();
                        ps23.close();
                        break;
                    default:
                        dropMessage(5, "setMaplewing选择类型错误，maplewingwt .");
                        tp = 0;
                        break;
                }

            } catch (SQLException ex) {
                log.error(" 设置委托任务信息 发生错误，请检查MapleCharacter里的  设置委托任务信息.", ex);
                return tp;
            }
        } else {
            dropMessage(5, "所选择设置类型的值的总和超出2147483640 无法设置.请更设置类型.");
            return tp;
        }

        if (getCheatTracker().canSaveDB()) {
            //   c.getPlayer().dropMessage(5, "开始保存角色数据...");
            saveToDB(false, false);
            //   c.getPlayer().dropMessage(5, "保存角色数据完成...");
        }

        return tp;
    }

    public int 设置玩家委托任务信息(String te, int das) {
        return 设置玩家委托任务信息(te, das, this.id);
    }


    public int 设置玩家委托任务信息(String te, int das, int ids) {
        int tp = -1;

        if (das < 2147483640) {
            try {
                Connection con = DatabaseConnection.getConnection();

                switch (te) {
                    case "nid":
                        PreparedStatement ps1 = con.prepareStatement("UPDATE maplewingplayerwt SET nid = ? where id = ?");
                        ps1.setInt(1, das);
                        ps1.setInt(2, ids);
                        ps1.executeUpdate();
                        ps1.close();
                        break;
                    case "name":
                        PreparedStatement ps2 = con.prepareStatement("UPDATE maplewingplayerwt SET name = ? where id = ?");
                        ps2.setString(1, this.name);
                        ps2.setInt(2, ids);
                        ps2.executeUpdate();
                        ps2.close();
                        break;
                    case "renwu1":
                        PreparedStatement ps3 = con.prepareStatement("UPDATE maplewingplayerwt SET renwu1 = ? where id = ?");
                        ps3.setInt(1, das);
                        ps3.setInt(2, ids);
                        ps3.executeUpdate();
                        ps3.close();
                        break;
                    case "mobid1":
                        PreparedStatement ps4 = con.prepareStatement("UPDATE maplewingplayerwt SET mobid1 = ? where id = ?");
                        ps4.setInt(1, das);
                        ps4.setInt(2, ids);
                        ps4.executeUpdate();
                        ps4.close();
                        break;
                    case "mob1":
                        PreparedStatement ps5 = con.prepareStatement("UPDATE maplewingplayerwt SET mob1 = ? where id = ?");
                        ps5.setInt(1, das);
                        ps5.setInt(2, ids);
                        ps5.executeUpdate();
                        ps5.close();
                        break;
                    case "renwu2":
                        PreparedStatement ps6 = con.prepareStatement("UPDATE maplewingplayerwt SET renwu2 = ? where id = ?");
                        ps6.setInt(1, das);
                        ps6.setInt(2, ids);
                        ps6.executeUpdate();
                        ps6.close();
                        break;
                    case "mobid2":
                        PreparedStatement ps7 = con.prepareStatement("UPDATE maplewingplayerwt SET mobid2 = ? where id = ?");
                        ps7.setInt(1, das);
                        ps7.setInt(2, ids);
                        ps7.executeUpdate();
                        ps7.close();
                        break;
                    case "mob2":
                        PreparedStatement ps8 = con.prepareStatement("UPDATE maplewingplayerwt SET mob2 = ? where id = ?");
                        ps8.setInt(1, das);
                        ps8.setInt(2, ids);
                        ps8.executeUpdate();
                        ps8.close();
                        break;
                    case "renwu3":
                        PreparedStatement ps9 = con.prepareStatement("UPDATE maplewingplayerwt SET renwu3 = ? where id = ?");
                        ps9.setInt(1, das);
                        ps9.setInt(2, ids);
                        ps9.executeUpdate();
                        ps9.close();
                        break;
                    case "mobid3":
                        PreparedStatement ps10 = con.prepareStatement("UPDATE maplewingplayerwt SET mobid3 = ? where id = ?");
                        ps10.setInt(1, das);
                        ps10.setInt(2, ids);
                        ps10.executeUpdate();
                        ps10.close();
                        break;
                    case "mob3":
                        PreparedStatement ps11 = con.prepareStatement("UPDATE maplewingplayerwt SET mob3 = ? where id = ?");
                        ps11.setInt(1, das);
                        ps11.setInt(2, ids);
                        ps11.executeUpdate();
                        ps11.close();
                        break;
                    case "renwu4":
                        PreparedStatement ps12 = con.prepareStatement("UPDATE maplewingplayerwt SET renwu4 = ? where id = ?");
                        ps12.setInt(1, das);
                        ps12.setInt(2, ids);
                        ps12.executeUpdate();
                        ps12.close();
                        break;
                    case "mobid4":
                        PreparedStatement ps13 = con.prepareStatement("UPDATE maplewingplayerwt SET mobid4 = ? where id = ?");
                        ps13.setInt(1, das);
                        ps13.setInt(2, ids);
                        ps13.executeUpdate();
                        ps13.close();
                        break;
                    case "mob4":
                        PreparedStatement ps14 = con.prepareStatement("UPDATE maplewingplayerwt SET mob4 = ? where id = ?");
                        ps14.setInt(1, das);
                        ps14.setInt(2, ids);
                        ps14.executeUpdate();
                        ps14.close();
                        break;
                    case "renwu5":
                        PreparedStatement ps15 = con.prepareStatement("UPDATE maplewingplayerwt SET renwu5 = ? where id = ?");
                        ps15.setInt(1, das);
                        ps15.setInt(2, ids);
                        ps15.executeUpdate();
                        ps15.close();
                        break;
                    case "mobid5":
                        PreparedStatement ps16 = con.prepareStatement("UPDATE maplewingplayerwt SET mobid5 = ? where id = ?");
                        ps16.setInt(1, das);
                        ps16.setInt(2, ids);
                        ps16.executeUpdate();
                        ps16.close();
                        break;
                    case "mob5":
                        PreparedStatement ps17 = con.prepareStatement("UPDATE maplewingplayerwt SET mob5 = ? where id = ?");
                        ps17.setInt(1, das);
                        ps17.setInt(2, ids);
                        ps17.executeUpdate();
                        ps17.close();
                        break;
                    case "renwu6":
                        PreparedStatement ps18 = con.prepareStatement("UPDATE maplewingplayerwt SET renwu6 = ? where id = ?");
                        ps18.setInt(1, das);
                        ps18.setInt(2, ids);
                        ps18.executeUpdate();
                        ps18.close();
                        break;
                    case "mobid6":
                        PreparedStatement ps19 = con.prepareStatement("UPDATE maplewingplayerwt SET mobid6 = ? where id = ?");
                        ps19.setInt(1, das);
                        ps19.setInt(2, ids);
                        ps19.executeUpdate();
                        ps19.close();
                        break;
                    case "mob6":
                        PreparedStatement ps20 = con.prepareStatement("UPDATE maplewingplayerwt SET mob6 = ? where id = ?");
                        ps20.setInt(1, das);
                        ps20.setInt(2, ids);
                        ps20.executeUpdate();
                        ps20.close();
                        break;
                    case "renwu7":
                        PreparedStatement ps21 = con.prepareStatement("UPDATE maplewingplayerwt SET renwu7 = ? where id = ?");
                        ps21.setInt(1, das);
                        ps21.setInt(2, ids);
                        ps21.executeUpdate();
                        ps21.close();
                        break;
                    case "mobid7":
                        PreparedStatement ps22 = con.prepareStatement("UPDATE maplewingplayerwt SET mobid7 = ? where id = ?");
                        ps22.setInt(1, das);
                        ps22.setInt(2, ids);
                        ps22.executeUpdate();
                        ps22.close();
                        break;
                    case "mob7":
                        PreparedStatement ps23 = con.prepareStatement("UPDATE maplewingplayerwt SET mob7 = ? where id = ?");
                        ps23.setInt(1, das);
                        ps23.setInt(2, ids);
                        ps23.executeUpdate();
                        ps23.close();
                        break;
                    case "renwu8":
                        PreparedStatement ps24 = con.prepareStatement("UPDATE maplewingplayerwt SET renwu8 = ? where id = ?");
                        ps24.setInt(1, das);
                        ps24.setInt(2, ids);
                        ps24.executeUpdate();
                        ps24.close();
                        break;
                    case "mobid8":
                        PreparedStatement ps25 = con.prepareStatement("UPDATE maplewingplayerwt SET mobid8 = ? where id = ?");
                        ps25.setInt(1, das);
                        ps25.setInt(2, ids);
                        ps25.executeUpdate();
                        ps25.close();
                        break;
                    case "mob8":
                        PreparedStatement ps26 = con.prepareStatement("UPDATE maplewingplayerwt SET mob8 = ? where id = ?");
                        ps26.setInt(1, das);
                        ps26.setInt(2, ids);
                        ps26.executeUpdate();
                        ps26.close();
                        break;
                    case "renwu9":
                        PreparedStatement ps27 = con.prepareStatement("UPDATE maplewingplayerwt SET renwu9 = ? where id = ?");
                        ps27.setInt(1, das);
                        ps27.setInt(2, ids);
                        ps27.executeUpdate();
                        ps27.close();
                        break;
                    case "mobid9":
                        PreparedStatement ps28 = con.prepareStatement("UPDATE maplewingplayerwt SET mobid9 = ? where id = ?");
                        ps28.setInt(1, das);
                        ps28.setInt(2, ids);
                        ps28.executeUpdate();
                        ps28.close();
                        break;
                    case "mob9":
                        PreparedStatement ps29 = con.prepareStatement("UPDATE maplewingplayerwt SET mob9 = ? where id = ?");
                        ps29.setInt(1, das);
                        ps29.setInt(2, ids);
                        ps29.executeUpdate();
                        ps29.close();
                        break;

                    default:
                        dropMessage(5, "设置玩家委托任务信息 选择类型错误，maplewingplayerwt .");
                        tp = 0;
                        break;
                }
                if (das > 0) {
                    String mds = "设置 " + 取得玩家委托任务名称(te) + " " + das + " ";
                    dropMessage(-5, mds);
                    dropMessage(-1, mds);
                }

            } catch (SQLException ex) {
                log.error(" 设置玩家委托任务信息 发生错误，请检查MapleCharacter里的  设置玩家委托任务信息.", ex);
                return tp;
            }
        } else {
            dropMessage(5, "所选择设置类型的值的总和超出2147483640 无法设置.请更设置类型.");
            return tp;
        }

        if (getCheatTracker().canSaveDB()) {
            //   c.getPlayer().dropMessage(5, "开始保存角色数据...");
            saveToDB(false, false);
            //   c.getPlayer().dropMessage(5, "保存角色数据完成...");
        }

        return tp;
    }


    public void 增加玩家委托任务信息(String te, int tas) {
        增加玩家委托任务信息(te, tas, this.id);
    }

    public void 增加玩家委托任务信息(String te, int tas, int ids) {
        //   int tp = -1;
        int mobss = 0;
        int das = 取得玩家委托任务信息(te, ids);
        int ads = das + tas;
        if (ads < 2147483640) {
            try {
                Connection con = DatabaseConnection.getConnection();

                switch (te) {
                    case "nid":
                        PreparedStatement ps1 = con.prepareStatement("UPDATE maplewingplayerwt SET nid = ? where id = ?");
                        ps1.setInt(1, ads);
                        ps1.setInt(2, ids);
                        ps1.executeUpdate();
                        ps1.close();
                        break;
                    case "name":
                        PreparedStatement ps2 = con.prepareStatement("UPDATE maplewingplayerwt SET name = ? where id = ?");
                        ps2.setString(1, this.name);
                        ps2.setInt(2, ids);
                        ps2.executeUpdate();
                        ps2.close();
                        break;
                    case "renwu1":
                        PreparedStatement ps3 = con.prepareStatement("UPDATE maplewingplayerwt SET renwu1 = ? where id = ?");
                        ps3.setInt(1, ads);
                        ps3.setInt(2, ids);
                        ps3.executeUpdate();
                        ps3.close();
                        break;
                    case "mobid1":
                        PreparedStatement ps4 = con.prepareStatement("UPDATE maplewingplayerwt SET mobid1 = ? where id = ?");
                        ps4.setInt(1, ads);
                        ps4.setInt(2, ids);
                        ps4.executeUpdate();
                        ps4.close();
                        break;
                    case "mob1":
                        PreparedStatement ps5 = con.prepareStatement("UPDATE maplewingplayerwt SET mob1 = ? where id = ?");
                        ps5.setInt(1, ads);
                        ps5.setInt(2, ids);
                        ps5.executeUpdate();
                        ps5.close();
                        mobss = 1;
                        break;
                    case "renwu2":
                        PreparedStatement ps6 = con.prepareStatement("UPDATE maplewingplayerwt SET renwu2 = ? where id = ?");
                        ps6.setInt(1, ads);
                        ps6.setInt(2, ids);
                        ps6.executeUpdate();
                        ps6.close();
                        break;
                    case "mobid2":
                        PreparedStatement ps7 = con.prepareStatement("UPDATE maplewingplayerwt SET mobid2 = ? where id = ?");
                        ps7.setInt(1, ads);
                        ps7.setInt(2, ids);
                        ps7.executeUpdate();
                        ps7.close();
                        break;
                    case "mob2":
                        PreparedStatement ps8 = con.prepareStatement("UPDATE maplewingplayerwt SET mob2 = ? where id = ?");
                        ps8.setInt(1, ads);
                        ps8.setInt(2, ids);
                        ps8.executeUpdate();
                        ps8.close();
                        mobss = 1;
                        break;
                    case "renwu3":
                        PreparedStatement ps9 = con.prepareStatement("UPDATE maplewingplayerwt SET renwu3 = ? where id = ?");
                        ps9.setInt(1, ads);
                        ps9.setInt(2, ids);
                        ps9.executeUpdate();
                        ps9.close();
                        break;
                    case "mobid3":
                        PreparedStatement ps10 = con.prepareStatement("UPDATE maplewingplayerwt SET mobid3 = ? where id = ?");
                        ps10.setInt(1, ads);
                        ps10.setInt(2, ids);
                        ps10.executeUpdate();
                        ps10.close();
                        break;
                    case "mob3":
                        PreparedStatement ps11 = con.prepareStatement("UPDATE maplewingplayerwt SET mob3 = ? where id = ?");
                        ps11.setInt(1, ads);
                        ps11.setInt(2, ids);
                        ps11.executeUpdate();
                        ps11.close();
                        mobss = 1;
                        break;
                    case "renwu4":
                        PreparedStatement ps12 = con.prepareStatement("UPDATE maplewingplayerwt SET renwu4 = ? where id = ?");
                        ps12.setInt(1, ads);
                        ps12.setInt(2, ids);
                        ps12.executeUpdate();
                        ps12.close();
                        break;
                    case "mobid4":
                        PreparedStatement ps13 = con.prepareStatement("UPDATE maplewingplayerwt SET mobid4 = ? where id = ?");
                        ps13.setInt(1, ads);
                        ps13.setInt(2, ids);
                        ps13.executeUpdate();
                        ps13.close();
                        break;
                    case "mob4":
                        PreparedStatement ps14 = con.prepareStatement("UPDATE maplewingplayerwt SET mob4 = ? where id = ?");
                        ps14.setInt(1, ads);
                        ps14.setInt(2, ids);
                        ps14.executeUpdate();
                        ps14.close();
                        mobss = 1;
                        break;
                    case "renwu5":
                        PreparedStatement ps15 = con.prepareStatement("UPDATE maplewingplayerwt SET renwu5 = ? where id = ?");
                        ps15.setInt(1, ads);
                        ps15.setInt(2, ids);
                        ps15.executeUpdate();
                        ps15.close();
                        break;
                    case "mobid5":
                        PreparedStatement ps16 = con.prepareStatement("UPDATE maplewingplayerwt SET mobid5 = ? where id = ?");
                        ps16.setInt(1, ads);
                        ps16.setInt(2, ids);
                        ps16.executeUpdate();
                        ps16.close();
                        break;
                    case "mob5":
                        PreparedStatement ps17 = con.prepareStatement("UPDATE maplewingplayerwt SET mob5 = ? where id = ?");
                        ps17.setInt(1, ads);
                        ps17.setInt(2, ids);
                        ps17.executeUpdate();
                        ps17.close();
                        mobss = 1;
                        break;
                    case "renwu6":
                        PreparedStatement ps18 = con.prepareStatement("UPDATE maplewingplayerwt SET renwu6 = ? where id = ?");
                        ps18.setInt(1, ads);
                        ps18.setInt(2, ids);
                        ps18.executeUpdate();
                        ps18.close();
                        break;
                    case "mobid6":
                        PreparedStatement ps19 = con.prepareStatement("UPDATE maplewingplayerwt SET mobid6 = ? where id = ?");
                        ps19.setInt(1, ads);
                        ps19.setInt(2, ids);
                        ps19.executeUpdate();
                        ps19.close();
                        break;
                    case "mob6":
                        PreparedStatement ps20 = con.prepareStatement("UPDATE maplewingplayerwt SET mob6 = ? where id = ?");
                        ps20.setInt(1, ads);
                        ps20.setInt(2, ids);
                        ps20.executeUpdate();
                        ps20.close();
                        mobss = 1;
                        break;
                    case "renwu7":
                        PreparedStatement ps21 = con.prepareStatement("UPDATE maplewingplayerwt SET renwu7 = ? where id = ?");
                        ps21.setInt(1, ads);
                        ps21.setInt(2, ids);
                        ps21.executeUpdate();
                        ps21.close();
                        break;
                    case "mobid7":
                        PreparedStatement ps22 = con.prepareStatement("UPDATE maplewingplayerwt SET mobid7 = ? where id = ?");
                        ps22.setInt(1, ads);
                        ps22.setInt(2, ids);
                        ps22.executeUpdate();
                        ps22.close();
                        break;
                    case "mob7":
                        PreparedStatement ps23 = con.prepareStatement("UPDATE maplewingplayerwt SET mob7 = ? where id = ?");
                        ps23.setInt(1, ads);
                        ps23.setInt(2, ids);
                        ps23.executeUpdate();
                        ps23.close();
                        mobss = 1;
                        break;
                    case "renwu8":
                        PreparedStatement ps24 = con.prepareStatement("UPDATE maplewingplayerwt SET renwu8 = ? where id = ?");
                        ps24.setInt(1, ads);
                        ps24.setInt(2, ids);
                        ps24.executeUpdate();
                        ps24.close();
                        break;
                    case "mobid8":
                        PreparedStatement ps25 = con.prepareStatement("UPDATE maplewingplayerwt SET mobid8 = ? where id = ?");
                        ps25.setInt(1, ads);
                        ps25.setInt(2, ids);
                        ps25.executeUpdate();
                        ps25.close();
                        break;
                    case "mob8":
                        PreparedStatement ps26 = con.prepareStatement("UPDATE maplewingplayerwt SET mob8 = ? where id = ?");
                        ps26.setInt(1, ads);
                        ps26.setInt(2, ids);
                        ps26.executeUpdate();
                        ps26.close();
                        mobss = 1;
                        break;
                    case "renwu9":
                        PreparedStatement ps27 = con.prepareStatement("UPDATE maplewingplayerwt SET renwu9 = ? where id = ?");
                        ps27.setInt(1, ads);
                        ps27.setInt(2, ids);
                        ps27.executeUpdate();
                        ps27.close();
                        break;
                    case "mobid9":
                        PreparedStatement ps28 = con.prepareStatement("UPDATE maplewingplayerwt SET mobid9 = ? where id = ?");
                        ps28.setInt(1, ads);
                        ps28.setInt(2, ids);
                        ps28.executeUpdate();
                        ps28.close();
                        break;
                    case "mob9":
                        PreparedStatement ps29 = con.prepareStatement("UPDATE maplewingplayerwt SET mob9 = ? where id = ?");
                        ps29.setInt(1, ads);
                        ps29.setInt(2, ids);
                        ps29.executeUpdate();
                        ps29.close();
                        mobss = 1;
                        break;

                    default:
                        dropMessage(5, " 增加玩家委托任务信息 选择类型错误，请联系Maplewing修复.");
                        //       tp = 0;
                        break;
                }
                if ((das >= 0) && (ids == this.id)) {
                    String mds = " " + 取得玩家委托任务名称(te) + " " + tas + " ";
                    if (mobss == 1) {
                        mds = " " + 取得玩家委托任务名称(te) + " " + 取得玩家委托任务信息(te) + " ";
                    }
                    dropMessage(-11, mds);
                    dropMessage(-1, mds);
                }

            } catch (SQLException ex) {
                log.error("增加玩家委托任务信息  信息发生错误，请检查MapleCharacter里的  增加玩家委托任务信息.", ex);
                //    return tp;
            }
        } else {
            dropMessage(5, " 增加玩家委托任务信息 所选择存入类型的余额和存入量的总和超出2147483640 无法再继续存入.请更换存入类型.");
            //   return tp;
        }

        if (getCheatTracker().canSaveDB()) {
            //   c.getPlayer().dropMessage(5, "开始保存角色数据...");
            saveToDB(false, false);
            //   c.getPlayer().dropMessage(5, "保存角色数据完成...");
        }

        // return tp;
    }


    public String 取得玩家委托任务名称(String lvs) {

        String names = null;
        switch (lvs) {
            case "id":
                names = "代号ID";
                break;
            case "name":
                names = this.name;
                break;
            case "renwu1":
                names = "委托任务1";
                break;
            case "mobid1":
                names = "消灭委托1任务怪物";
                break;
            case "mob1":
                names = "消灭委托1任务怪物数量";
                break;
            case "renwu2":
                names = "委托任务2";
                break;
            case "mobid2":
                names = "消灭委托2任务怪物";
                break;
            case "mob2":
                names = "消灭委托2任务怪物数量";
                break;
            case "renwu3":
                names = "委托任务3";
                break;
            case "mobid3":
                names = "消灭委托3任务怪物";
                break;
            case "mob3":
                names = "消灭委托3任务怪物数量";
                break;
            case "renwu4":
                names = "委托任务4";
                break;
            case "mobid4":
                names = "消灭委托4任务怪物";
                break;
            case "mob4":
                names = "消灭委托4任务怪物数量";
                break;
            case "renwu5":
                names = "委托任务4";
                break;
            case "mobid5":
                names = "消灭委托5任务怪物";
                break;
            case "mob5":
                names = "消灭委托5任务怪物数量";
                break;
            case "renwu6":
                names = "委托任务6";
                break;
            case "mobid6":
                names = "消灭委托6任务怪物";
                break;
            case "mob6":
                names = "消灭委托6任务怪物数量";
                break;
            case "renwu7":
                names = "委托任务7";
                break;
            case "mobid7":
                names = "消灭委托7任务怪物";
                break;
            case "mob7":
                names = "消灭委托7任务怪物数量";
                break;
            case "renwu8":
                names = "委托任务8";
                break;
            case "mobid8":
                names = "消灭委托8任务怪物";
                break;
            case "mob8":
                names = "消灭委托8任务怪物数量";
                break;
            case "renwu9":
                names = "委托任务9";
                break;
            case "mobid9":
                names = "消灭委托9任务怪物";
                break;
            case "mob9":
                names = "消灭委托9任务怪物数量";
                break;

        }
        return names;
    }


    public void 发布玩家的委托任务(int ids, String names, String neirong, int mob, int nmob, int item, int nitem, int reward1wmose, int exps, int level) {


        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("select * from maplewingwt where id = ?");
            ps.setInt(1, ids);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {

                PreparedStatement psu = con.prepareStatement("insert into maplewingwt (id, name, neirong, fnpc, nid, nlevel, njob, nchongxiu, nguild, mob, nmob, item, nitem, npc, map, reward1maple, reward2mapley, reward3wmose, reward4emose, reward5nx, rewarditem, itemsl, meso, exp, maxcishu, level, hastrue) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                psu.setInt(1, ids + 1000);
                psu.setString(2, names);
                psu.setString(3, neirong);
                psu.setInt(4, this.id);
                psu.setInt(5, 0);//nid
                psu.setInt(6, 0);//nlevel
                psu.setInt(7, 0);//njob
                psu.setInt(8, 0);//nchongxiu
                psu.setInt(9, 0);//nguild
                psu.setInt(10, mob);//mob
                psu.setInt(11, nmob);//nmob
                psu.setInt(12, 0);//item
                psu.setInt(13, 0);//nitem
                psu.setInt(14, 90000086);//npc
                psu.setInt(15, 0);//map
                psu.setInt(16, reward1wmose);//reward1
                psu.setInt(17, 0);//reward2
                psu.setInt(18, 0);//reward3
                psu.setInt(19, 0);//reward4
                psu.setInt(20, 0);//reward5
                psu.setInt(21, 0);//rewarditem
                psu.setInt(22, 0);//itemsl
                psu.setInt(23, 0);//meso
                psu.setInt(24, exps);//exp
                psu.setInt(25, 0);//maxcishu
                psu.setInt(26, level);//level
                psu.setInt(27, 0);//true == 0 false = 1 
                psu.executeUpdate();
                psu.close();

                ps.close();
                rs.close();
            }
        } catch (SQLException ex) {
            log.error("设置新委托任务 信息发生错误", ex);
        }
    }

    public int 取得玩家可接委托任务数量() {

        int viplv = getMaplewing("cardlevel");
        int renwu1 = 取得玩家委托任务信息("renwu1");
        int renwu2 = 取得玩家委托任务信息("renwu2");
        int renwu3 = 取得玩家委托任务信息("renwu3");
        int renwu4 = 取得玩家委托任务信息("renwu4");
        int renwu5 = 取得玩家委托任务信息("renwu5");
        int renwu6 = 取得玩家委托任务信息("renwu6");
        int renwu7 = 取得玩家委托任务信息("renwu7");
        int renwu8 = 取得玩家委托任务信息("renwu8");
        int renwu9 = 取得玩家委托任务信息("renwu9");

        if ((viplv >= 4) && (renwu4 == -1)) {
            设置玩家委托任务信息("renwu4", 0);
        }
        if ((viplv >= 5) && (renwu5 == -1)) {
            设置玩家委托任务信息("renwu5", 0);
        }
        if ((viplv >= 6) && (renwu6 == -1)) {
            设置玩家委托任务信息("renwu6", 0);
        }
        if ((viplv >= 7) && (renwu7 == -1)) {
            设置玩家委托任务信息("renwu7", 0);
        }
        if ((viplv >= 8) && (renwu8 == -1)) {
            设置玩家委托任务信息("renwu8", 0);
        }
        if ((viplv >= 9) && (renwu9 == -1)) {
            设置玩家委托任务信息("renwu9", 0);
        }

        int 数量 = 0;

        if (renwu1 == 0) {
            数量++;
        }
        if (renwu2 == 0) {
            数量++;
        }
        if (renwu3 == 0) {
            数量++;
        }
        if (renwu4 == 0) {
            数量++;
        }
        if (renwu5 == 0) {
            数量++;
        }
        if (renwu6 == 0) {
            数量++;
        }
        if (renwu7 == 0) {
            数量++;
        }
        if (renwu8 == 0) {
            数量++;
        }
        if (renwu9 == 0) {
            数量++;
        }

        return 数量;

    }

    public int 取得玩家可接委托任务最大数量() {

        int 总数量数量 = 3;
        int viplv = getMaplewing("cardlevel");
        if (viplv <= 9) {
            总数量数量 = viplv;
        } else {
            总数量数量 = 9;
        }

        return 总数量数量;

    }


}