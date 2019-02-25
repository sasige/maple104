package handling.channel;

import client.MapleCharacter;
import database.DatabaseConnection;
import handling.MapleServerHandler;
import handling.login.LoginServer;

import handling.netty.NettyServerConnection;
import handling.world.CheaterData;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;
import scripting.EventScriptManager;
import server.MapleSquad;
import server.ServerProperties;
import server.events.MapleCoconut;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.events.MapleFitness;
import server.events.MapleOla;
import server.events.MapleOxQuiz;
import server.events.MapleSnowball;
import server.events.MapleSurvival;
import server.life.PlayerNPC;
import server.maps.*;
import server.shops.HiredMerchant;
import server.shops.HiredMerchantSave;
import tools.ConcurrentEnumMap;
import tools.MaplePacketCreator;

public class ChannelServer {

    public static long serverStartTime;
    private int expRate;
    private int mesoRate;
    private int dropRate;
    private int cashRate;
    private int traitRate;
    private int stateRate;
    private int stateLines;
    private int statLimit;
    private short port;
    private static final short DEFAULT_PORT = 7575;
    private int channel;
    private int running_MerchantID = 0;
    private int flags = 0;
    private int doubleExp = 1;
    private int sharePrice = 0;

    private int 潜能数目改变基本概率 = 1;

    private String serverMessage;
    private String ip;
    private String serverName;


    private static int bossbalrog;//109dc
    private static int chaoszak;//109dc
    private static int pinkzak;//109dc
    private static int zak;//109dc
    private static int horntail;//109dc
    private static int chaosht;//109dc
    private static int pinkbean;//109dc
    private static int vonleon;//109dc
    private static int cygnus;//109dc

    private static int akayile;
    private static int xila;
    private static int baojun;
    private static int nmm_squad;
    private static int vergamot;
    private static int dunas;
    private static int nibergen_squad;
    private static int dunas2;
    private static int core_blaze;
    private static int aufheben;
    private static int cwkpq;
    private static int tokyo_2095;
    private static int scartar;
    private static int allboss;

    private int firstMap;

    private static int pogongbilv;

    private boolean shutdown = false;
    private boolean finishedShutdown = false;
    private boolean MegaphoneMuteState = false;
    private boolean adminOnly = false;
    private boolean canPvp = false;
    private boolean lvkejian = false;

    private boolean 贡献排名 = false;
    private boolean 委托任务 = true;

    private static boolean 鉴定出全属性20 = false;
    private static boolean 鉴定出BOSS30 = false;
    private static boolean 鉴定出顶级属性 = false;

    private PlayerStorage players;
    private NettyServerConnection acceptor;
    private final MapleMapFactory mapFactory;
    private MapleCharacter cns;
    private EventScriptManager eventSM;
    private AramiaFireWorks works = new AramiaFireWorks();
    private static Map<Integer, ChannelServer> instances = new HashMap();
    private Map<MapleSquad.MapleSquadType, MapleSquad> mapleSquads = new ConcurrentEnumMap(MapleSquad.MapleSquadType.class);
    private Map<Integer, HiredMerchant> merchants = new HashMap();
    private List<PlayerNPC> playerNPCs = new LinkedList();
    private ReentrantReadWriteLock merchLock = null;
    private ReentrantReadWriteLock.ReadLock mcReadLock = null;
    private ReentrantReadWriteLock.WriteLock mcWriteLock = null;
    private int eventmap = -1;
    private final Map<MapleEventType, MapleEvent> events = new EnumMap(MapleEventType.class);
    private String ShopPack;
    Connection shareCon;
    private static final Logger log = Logger.getLogger(ChannelServer.class);

    private ChannelServer(int channel) {
        this.channel = channel;
        this.mapFactory = new MapleMapFactory(channel);

        this.merchLock = new ReentrantReadWriteLock(true);
        this.mcReadLock = this.merchLock.readLock();
        this.mcWriteLock = this.merchLock.writeLock();
    }

    public static Set<Integer> getAllInstance() {
        return new HashSet(instances.keySet());
    }

    public void loadEvents() {
        if (events.size() != 0) {
            return;
        }
        events.put(MapleEventType.CokePlay, new MapleCoconut(channel, MapleEventType.CokePlay));
        events.put(MapleEventType.Coconut, new MapleCoconut(channel, MapleEventType.Coconut));
        events.put(MapleEventType.Fitness, new MapleFitness(channel, MapleEventType.Fitness));
        events.put(MapleEventType.OlaOla, new MapleOla(channel, MapleEventType.OlaOla));
        events.put(MapleEventType.OxQuiz, new MapleOxQuiz(channel, MapleEventType.OxQuiz));
        events.put(MapleEventType.Snowball, new MapleSnowball(channel, MapleEventType.Snowball));
        events.put(MapleEventType.Survival, new MapleSurvival(channel, MapleEventType.Survival));
    }

    public void run_startup_configurations() {
        setChannel(this.channel);
        try {
            expRate = Integer.parseInt(ServerProperties.getProperty("world.exp", "10"));
            mesoRate = Integer.parseInt(ServerProperties.getProperty("world.meso", "10"));
            dropRate = Integer.parseInt(ServerProperties.getProperty("world.drop", "3"));
            cashRate = Integer.parseInt(ServerProperties.getProperty("world.cash", "1"));
            traitRate = Integer.parseInt(ServerProperties.getProperty("world.trait", "1"));
            stateRate = Integer.parseInt(ServerProperties.getProperty("world.state", "4"));
            stateLines = Integer.parseInt(ServerProperties.getProperty("world.stateLines", "3"));
            statLimit = Integer.parseInt(ServerProperties.getProperty("world.statLimit", "999"));
            serverMessage = ServerProperties.getProperty("world.serverMessage");
            serverName = ServerProperties.getProperty("login.serverName");
            flags = Integer.parseInt(ServerProperties.getProperty("world.flags", "0"));
            adminOnly = Boolean.parseBoolean(ServerProperties.getProperty("world.admin", "false"));
            lvkejian = Boolean.parseBoolean(ServerProperties.getProperty("world.lvkejian", "false"));
            canPvp = Boolean.parseBoolean(ServerProperties.getProperty("world.canPvp", "false"));
            eventSM = new EventScriptManager(this, ServerProperties.getProperty("channel.events").split(","));
            port = Short.parseShort(ServerProperties.getProperty("channel.port" + channel, String.valueOf(DEFAULT_PORT + this.channel)));

            贡献排名 = Boolean.parseBoolean(ServerProperties.getProperty("world.贡献排名", "false"));
            委托任务 = Boolean.parseBoolean(ServerProperties.getProperty("world.委托任务", "false"));

            潜能数目改变基本概率 = Integer.parseInt(ServerProperties.getProperty("world.潜能数目改变基本概率", "1"));

            鉴定出全属性20 = Boolean.parseBoolean(ServerProperties.getProperty("world.鉴定出全属性20", "false"));
            鉴定出BOSS30 = Boolean.parseBoolean(ServerProperties.getProperty("world.鉴定出BOSS30", "false"));
            鉴定出顶级属性 = Boolean.parseBoolean(ServerProperties.getProperty("world.鉴定出顶级属性", "false"));

            bossbalrog = Integer.parseInt(ServerProperties.getProperty("bossbalrog", "1"));
            chaoszak = Integer.parseInt(ServerProperties.getProperty("chaoszak", "1"));
            pinkzak = Integer.parseInt(ServerProperties.getProperty("pinkzak", "1"));
            zak = Integer.parseInt(ServerProperties.getProperty("zak", "1"));
            horntail = Integer.parseInt(ServerProperties.getProperty("horntail", "1"));
            chaosht = Integer.parseInt(ServerProperties.getProperty("chaosht", "1"));
            pinkbean = Integer.parseInt(ServerProperties.getProperty("pinkbean", "1"));
            vonleon = Integer.parseInt(ServerProperties.getProperty("vonleon", "1"));
            cygnus = Integer.parseInt(ServerProperties.getProperty("cygnus", "1"));

            akayile = Integer.parseInt(ServerProperties.getProperty("akayile", "1"));
            xila = Integer.parseInt(ServerProperties.getProperty("xila", "1"));
            baojun = Integer.parseInt(ServerProperties.getProperty("baojun", "1"));
            nmm_squad = Integer.parseInt(ServerProperties.getProperty("nmm_squad", "1"));
            vergamot = Integer.parseInt(ServerProperties.getProperty("vergamot", "1"));
            dunas = Integer.parseInt(ServerProperties.getProperty("dunas", "1"));

            nibergen_squad = Integer.parseInt(ServerProperties.getProperty("nibergen_squad", "1"));
            dunas2 = Integer.parseInt(ServerProperties.getProperty("dunas2", "1"));
            core_blaze = Integer.parseInt(ServerProperties.getProperty("core_blaze", "1"));
            aufheben = Integer.parseInt(ServerProperties.getProperty("aufheben", "1"));
            cwkpq = Integer.parseInt(ServerProperties.getProperty("cwkpq", "1"));
            tokyo_2095 = Integer.parseInt(ServerProperties.getProperty("tokyo_2095", "1"));
            scartar = Integer.parseInt(ServerProperties.getProperty("scartar", "1"));

            allboss = Integer.parseInt(ServerProperties.getProperty("allboss", "1"));

            firstMap = Integer.parseInt(ServerProperties.getProperty("world.firstMap", "50000"));
            pogongbilv = Integer.parseInt(ServerProperties.getProperty("world.pogongbilv", "1000"));


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ip = (ServerProperties.getProperty("channel.interface") + ":" + this.port);

        this.acceptor = new NettyServerConnection(this.port, 0, this.channel, false);
        try {
            this.acceptor.run();
            log.info("频道: " + this.channel + " 监听端口: " + this.port);
        } catch (Exception e) {
            log.error("绑定端口: " + this.port + " 失败 (ch: " + getChannel() + ")" + e);
            acceptor.close();
        }

       /* IoBuffer.setUseDirectBuffer(false);
        IoBuffer.setAllocator(new SimpleBufferAllocator());

        acceptor = new NioSocketAcceptor();
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MapleCodecFactory()));
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 30);
        players = new PlayerStorage(this.channel);
        getShopPack();
        loadEvents();
        //loadShare();//股票
        try {
            acceptor.setHandler(new MapleServerHandler(this.channel, false));
            acceptor.bind(new InetSocketAddress(this.port));
            ((SocketSessionConfig) acceptor.getSessionConfig()).setTcpNoDelay(true);

            eventSM.init();
        } catch (IOException e) {
            System.out.println("绑定端口: " + this.port + " 失败 (ch: " + getChannel() + ")" + e);
        }*/
    }

    public void shutdown() {
        if (this.finishedShutdown) {
            return;
        }
        broadcastPacket(MaplePacketCreator.serverNotice(0, "游戏即将关闭维护..."));

        this.shutdown = true;
        System.out.println("频道 " + this.channel + " 正在清理活动脚本...");

        this.eventSM.cancel();

        System.out.println("频道 " + this.channel + " 正在保存所有角色数据...");

        getPlayerStorage().disconnectAll();

        System.out.println("频道 " + this.channel + " 解除绑定端口...");

        this.acceptor.close();
        this.acceptor = null;

        instances.remove(this.channel);
        setFinishShutdown();
    }

    public void unbind() {
        this.acceptor.close();
    }

    public boolean hasFinishedShutdown() {
        return this.finishedShutdown;
    }

    public MapleMapFactory getMapFactory() {
        return this.mapFactory;
    }

    public static ChannelServer newInstance(int channel) {
        return new ChannelServer(channel);
    }

    public static ChannelServer getInstance(int channel) {
        return (ChannelServer) instances.get(Integer.valueOf(channel));
    }

    public void addPlayer(MapleCharacter chr) {
        getPlayerStorage().registerPlayer(chr);
    }

    public PlayerStorage getPlayerStorage() {
        if (this.players == null) {
            this.players = new PlayerStorage(this.channel);
        }
        return this.players;
    }

    public void removePlayer(MapleCharacter chr) {
        getPlayerStorage().deregisterPlayer(chr);
    }

    public void removePlayer(int idz, String namez) {
        getPlayerStorage().deregisterPlayer(idz, namez);
    }

    public String getServerMessage() {
        return this.serverMessage;
    }

    public void setServerMessage(String newMessage) {
        this.serverMessage = newMessage;
        broadcastPacket(MaplePacketCreator.serverMessage(this.serverMessage));
    }

    public void broadcastPacket(byte[] data) {
        getPlayerStorage().broadcastPacket(data);
    }

    public void broadcastSmegaPacket(byte[] data) {
        getPlayerStorage().broadcastSmegaPacket(data);
    }

    public void broadcastGMPacket(byte[] data) {
        getPlayerStorage().broadcastGMPacket(data);
    }

    public int getChannel() {
        return this.channel;
    }

    public void setChannel(int channel) {
        instances.put(Integer.valueOf(channel), this);
        LoginServer.addChannel(channel);
    }

    public static ArrayList<ChannelServer> getAllInstances() {
        return new ArrayList(instances.values());
    }

    public String getIP() {
        return this.ip;
    }

    public boolean isShutdown() {
        return this.shutdown;
    }

    public int getLoadedMaps() {
        return this.mapFactory.getLoadedMaps();
    }

    public EventScriptManager getEventSM() {
        return this.eventSM;
    }

    public void reloadEvents() {
        this.eventSM.cancel();
        this.eventSM = new EventScriptManager(this, ServerProperties.getProperty("channel.events").split(","));
        this.eventSM.init();
    }

    public int getExpRate() {
        return this.expRate * getDoubleExp();
    }

    public void setExpRate(int expRate) {
        this.expRate = expRate;
    }

    public int getCashRate() {
        return this.cashRate;
    }

    public void setCashRate(int cashRate) {
        this.cashRate = cashRate;
    }

    public int getMesoRate() {
        return this.mesoRate * getDoubleExp();
    }

    public void setMesoRate(int mesoRate) {
        this.mesoRate = mesoRate;
    }

    public int getDropRate() {
        return this.dropRate * getDoubleExp();
    }

    public void setDropRate(int dropRate) {
        this.dropRate = dropRate;
    }

    public int getDoubleExp() {
        if ((this.doubleExp < 0) || (this.doubleExp > 2)) {
            return 1;
        }
        return this.doubleExp;
    }

    public void setDoubleExp(int doubleExp) {
        if ((doubleExp < 0) || (doubleExp > 2)) {
            this.doubleExp = 1;
        } else {
            this.doubleExp = doubleExp;
        }
    }

    public int getStatLimit() {
        return this.statLimit;
    }

    public void setStatLimit(int limit) {
        this.statLimit = limit;
    }

    public static void startChannel_Main() {
        serverStartTime = System.currentTimeMillis();
        int ch = Integer.parseInt(ServerProperties.getProperty("channel.count", "0"));
        if (ch > 10) {
            ch = 10;
        }
        for (int i = 0; i < ch; i++) {
            newInstance(i + 1).run_startup_configurations();
        }
    }

    public Map<MapleSquad.MapleSquadType, MapleSquad> getAllSquads() {
        return Collections.unmodifiableMap(this.mapleSquads);
    }

    public MapleSquad getMapleSquad(String type) {
        return getMapleSquad(MapleSquad.MapleSquadType.valueOf(type.toLowerCase()));
    }

    public MapleSquad getMapleSquad(MapleSquad.MapleSquadType type) {
        return (MapleSquad) this.mapleSquads.get(type);
    }

    public boolean addMapleSquad(MapleSquad squad, String type) {
        MapleSquad.MapleSquadType types = MapleSquad.MapleSquadType.valueOf(type.toLowerCase());
        if ((types != null) && (!this.mapleSquads.containsKey(types))) {
            this.mapleSquads.put(types, squad);
            squad.scheduleRemoval();
            return true;
        }
        return false;
    }

    public boolean removeMapleSquad(MapleSquad.MapleSquadType types) {
        if ((types != null) && (this.mapleSquads.containsKey(types))) {
            this.mapleSquads.remove(types);
            return true;
        }
        return false;
    }

    public int closeAllMerchant() {
        int ret = 0;
        this.mcWriteLock.lock();
        try {
            Iterator merchants_ = this.merchants.entrySet().iterator();
            while (merchants_.hasNext()) {
                HiredMerchant hm = (HiredMerchant) ((Map.Entry) merchants_.next()).getValue();
                HiredMerchantSave.QueueShopForSave(hm);
                hm.getMap().removeMapObject(hm);
                merchants_.remove();
                ret++;
            }
        } finally {
            this.mcWriteLock.unlock();
        }

        for (int i = 910000001; i <= 910000022; i++) {
            for (MapleMapObject mmo : this.mapFactory.getMap(i).getAllHiredMerchantsThreadsafe()) {
                HiredMerchantSave.QueueShopForSave((HiredMerchant) mmo);
                ret++;
            }
        }
        return ret;
    }

    public void closeAllMerchants() {
        int ret = 0;
        long Start = System.currentTimeMillis();
        this.mcWriteLock.lock();
        try {
            Iterator hmit = this.merchants.entrySet().iterator();
            while (hmit.hasNext()) {
                ((HiredMerchant) ((Map.Entry) hmit.next()).getValue()).closeShop(true, false);
                hmit.remove();
                ret++;
            }
        } catch (Exception e) {
            System.out.println("关闭雇佣商店出现错误..." + e);
        } finally {
            this.mcWriteLock.unlock();
        }
        System.out.println("频道 " + this.channel + " 共保存雇佣商店: " + ret + " | 耗时: " + (System.currentTimeMillis() - Start) + " 毫秒.");
    }

    public int addMerchant(final HiredMerchant hMerchant) {
        mcWriteLock.lock();
        try {
            running_MerchantID++;
            merchants.put(this.running_MerchantID, hMerchant);
            return running_MerchantID;
        } finally {
            this.mcWriteLock.unlock();
        }
    }

    public void removeMerchant(HiredMerchant hMerchant) {
        this.mcWriteLock.lock();
        try {
            this.merchants.remove(Integer.valueOf(hMerchant.getStoreId()));
        } finally {
            this.mcWriteLock.unlock();
        }
    }

    public boolean containsMerchant(int accid) {
        boolean contains = false;
        this.mcReadLock.lock();
        try {
            Iterator itr = this.merchants.values().iterator();
            while (itr.hasNext()) {
                HiredMerchant hm = (HiredMerchant) itr.next();
                if (hm.getOwnerAccId() == accid) {
                    contains = true;
                    break;
                }
            }
        } finally {
            this.mcReadLock.unlock();
        }
        return contains;
    }

    public boolean containsMerchant(int accid, int cid) {
        boolean contains = false;
        this.mcReadLock.lock();
        try {
            Iterator itr = this.merchants.values().iterator();
            while (itr.hasNext()) {
                HiredMerchant hm = (HiredMerchant) itr.next();
                if ((hm.getOwnerAccId() == accid) && (hm.getOwnerId() == cid)) {
                    contains = true;
                    break;
                }
            }
        } finally {
            this.mcReadLock.unlock();
        }
        return contains;
    }

    public List<HiredMerchant> searchMerchant(int itemSearch) {
        List list = new LinkedList();
        this.mcReadLock.lock();
        try {
            Iterator itr = this.merchants.values().iterator();
            while (itr.hasNext()) {
                HiredMerchant hm = (HiredMerchant) itr.next();
                if (hm.searchItem(itemSearch).size() > 0) {
                    list.add(hm);
                }
            }
        } finally {
            this.mcReadLock.unlock();
        }
        return list;
    }

    public HiredMerchant getHiredMerchants(int accid, int cid) {
        this.mcReadLock.lock();
        try {
            Iterator itr = this.merchants.values().iterator();
            while (itr.hasNext()) {
                HiredMerchant hm = (HiredMerchant) itr.next();
                if ((hm.getOwnerAccId() == accid) && (hm.getOwnerId() == cid)) {
                    HiredMerchant localHiredMerchant1 = hm;
                    return localHiredMerchant1;
                }
            }
        } finally {
            this.mcReadLock.unlock();
        }
        return null;
    }

    public void toggleMegaphoneMuteState() {
        this.MegaphoneMuteState = (!this.MegaphoneMuteState);
    }

    public boolean getMegaphoneMuteState() {
        return this.MegaphoneMuteState;
    }

    public int getEvent() {
        return this.eventmap;
    }

    public void setEvent(int ze) {
        this.eventmap = ze;
    }

    public MapleEvent getEvent(MapleEventType t) {
        return (MapleEvent) this.events.get(t);
    }

    public Collection<PlayerNPC> getAllPlayerNPC() {
        return this.playerNPCs;
    }

    public void addPlayerNPC(PlayerNPC npc) {
        if (this.playerNPCs.contains(npc)) {
            return;
        }
        this.playerNPCs.add(npc);
        getMapFactory().getMap(npc.getMapId()).addMapObject(npc);
    }

    public void removePlayerNPC(PlayerNPC npc) {
        if (this.playerNPCs.contains(npc)) {
            this.playerNPCs.remove(npc);
            getMapFactory().getMap(npc.getMapId()).removeMapObject(npc);
        }
    }

    public String getServerName() {
        return this.serverName;
    }

    public void setServerName(String sn) {
        this.serverName = sn;
    }

    public String getTrueServerName() {
        return this.serverName.substring(0, this.serverName.length() - 3);
    }

    public int getPort() {
        return this.port;
    }

    public static Set<Integer> getChannelServer() {
        return new HashSet(instances.keySet());
    }

    public void setShutdown() {
        this.shutdown = true;
        System.out.println("频道 " + this.channel + " 正在关闭和保存雇佣商店数据信息...");
    }

    public void setFinishShutdown() {
        this.finishedShutdown = true;
        System.out.println("频道 " + this.channel + " 已关闭完成.");
    }

    public boolean isAdminOnly() {
        return this.adminOnly;
    }

    public static int getChannelCount() {
        return instances.size();
    }

    public int getTempFlag() {
        return this.flags;
    }

    public static Map<Integer, Integer> getChannelLoad() {
        Map ret = new HashMap();
        for (ChannelServer cs : instances.values()) {
            ret.put(Integer.valueOf(cs.getChannel()), Integer.valueOf(cs.getConnectedClients()));
        }
        return ret;
    }

    public int getConnectedClients() {
        return getPlayerStorage().getConnectedClients();
    }

    public List<CheaterData> getCheaters() {
        List cheaters = getPlayerStorage().getCheaters();
        Collections.sort(cheaters);
        return cheaters;
    }

    public List<CheaterData> getReports() {
        List cheaters = getPlayerStorage().getReports();
        Collections.sort(cheaters);
        return cheaters;
    }

    public void broadcastMessage(byte[] message) {
        broadcastPacket(message);
    }

    public void broadcastSmega(byte[] message) {
        broadcastSmegaPacket(message);
    }

    public void broadcastGMMessage(byte[] message) {
        broadcastGMPacket(message);
    }

    public void startMapEffect(String msg, int itemId) {
        startMapEffect(msg, itemId, 10);
    }

    public void startMapEffect(String msg, int itemId, int time) {
        for (MapleMap load : getMapFactory().getAllMaps()) {
            if (load.getCharactersSize() > 0) {
                load.startMapEffect(msg, itemId, time);
            }
        }
    }

    public AramiaFireWorks getFireWorks() {
        return this.works;
    }

    public int getTraitRate() {
        return this.traitRate;
    }

    public String getShopPack() {
        if (ShopPack != null) {
            return ShopPack;
        }
        Properties dbProp = new Properties();
        try {
            FileInputStream is = new FileInputStream(System.getProperty("resourcePath") + File.separator + "CashPack.txt");
            dbProp.load(is);
            is.close();
        } catch (IOException ex) {
            log.error("无法加载 CashPack.txt 的商城信息数据文件.");
        }
        ShopPack = dbProp.getProperty("pack");
        return ShopPack;
    }

    public void saveAll() {
        int nos = 0;
        for (MapleCharacter chr : this.players.getAllCharacters()) {
            nos++;
            chr.saveToDB(false, false);
        }
        System.out.println("[自动保存] 已经将频道 " + this.channel + " 的 " + nos + " 个玩家的数据自动保存到数据中.");
    }

    public void AutoGain(int jsexp) {
        this.mapFactory.getMap(910000000).AutoGain(jsexp);
    }

    public void AutoNx(int jsNx) {
        this.mapFactory.getMap(910000000).AutoNx(jsNx);
    }

    public void AutoPaoDian() {
        for (MapleCharacter chr : this.players.getAllCharacters()) {
            chr.setBossLog("在线泡点");
        }
    }


    public void AutoNxw(int jsNx) {

        for (MapleCharacter chr : this.players.getAllCharacters()) {
            int givNx = chr.getLevel() / 10 + jsNx;
            chr.modifyCSPoints(2, givNx);
            chr.dropMessage(-11, new StringBuilder().append("[ MapleWing ]： 在线时间奖励获得 [ ").append(givNx).append(" ] 点抵用券.").toString());
        }
    }

    public void AutoNxm(int jsNx) {
        int maps = 749050500;
        for (int ii = 0; ii <= 2; ii++) {
            for (MapleCharacter chr : this.players.getAllCharacters()) {
                int givNx = chr.getLevel() / 10 + jsNx;
                chr.modifyCSPoints(1, givNx);
                chr.dropMessage(-11, new StringBuilder().append("[ MapleWing ]： 在线时间奖励获得 [ ").append(givNx).append(" ] 点券.").toString());
            }
            maps++;
        }
    }

    //掉落物品
    public void Drops(int maps) {
        this.mapFactory.getMap(maps).Drops(maps);
    }

    public void RemoveDrops() {
        this.cns.getMap().removeDrops();
    }

    public void MaplewingGXs(int jsNx) {
        int maps = 749050500;
        for (int ii = 0; ii <= 2; ii++) {

            this.mapFactory.getMap(maps).MaplewingGXs(jsNx);
            maps++;
        }
    }

    public void MaplewingHYs(int jsNx) {
        int maps = 749050500;
        for (int ii = 0; ii <= 2; ii++) {
            this.mapFactory.getMap(maps).MaplewingHYs(jsNx);
            maps++;
        }
    }

    public void MaplewingGP(String te, int ds, int ids) {
        for (MapleCharacter chr : this.players.getAllCharacters()) {
            chr.setMaplewinggp(te, ds, ids);
        }
    }


    public void MaplewingGX(int ds) {

        for (MapleCharacter chr : this.players.getAllCharacters()) {
            chr.addMaplewing("maple", ds);
            chr.upMaplewing();
            String names = chr.getVipname() + " 您在 Maplewing 的 贡献点 增加了 " + ds + " . 赶快去贡献专区看看吧！";
            chr.dropMessage(-1, names);
            chr.dropMessage(-11, names);
        }

    }

    public void MaplewingHY(int ds) {

        for (MapleCharacter chr : this.players.getAllCharacters()) {
            chr.addMaplewing("mapley", ds);
            chr.upMaplewing();
            String names = chr.getVipname() + " 您在 Maplewing 的 活跃点 增加了 " + ds + " . 赶快去领取在线福利吧！";
            chr.dropMessage(-1, names);
            if (ds >= 10) {
                chr.dropMessage(-11, names);
            }
        }

    }

    public void MaplewingHaveMoseTime(int ds) {

        for (MapleCharacter chr : this.players.getAllCharacters()) {
            int times = chr.getMaplewing("havetime");
            if (times >= 1) {
                chr.addMaplewing("havetime", -ds);
                chr.upMaplewing();
                if (times <= 5) {
                    String names = chr.getVipname() + " 您在 MaplewingBank 存款 时间 仅剩 " + times + "  分钟. 赶快去金融专区查看你的收入吧！！";
                    chr.dropMessage(-1, names);
                    chr.dropMessage(-11, names);
                }
            }
        }

    }

    public void MaplewingJS(int ds) {

        for (MapleCharacter chr : this.players.getAllCharacters()) {
            int wmose = chr.getMaplewing("wmose");
            int emose = chr.getMaplewing("emose");
            int savemose = chr.getMaplewing("savemose");


            int lvs = chr.getLevel();
            int meso = chr.getMeso();
            int tmeso = chr.getMaplewingJS("tmeso");

            int jmeso = chr.getMaplewingJS("jmeso");

            jmeso *= ds;

            int gmeso = 0;
            int gwmose = 0;
            int gemose = 0;
            int gsavemose = 0;

            if ((tmeso > 10000) && (jmeso > 0)) {


                if ((emose > 0) && (wmose < jmeso)) {

                    if (emose * 10000 + wmose > 2100000000) {
                        gemose = (int) (2100000000 - (wmose - wmose % 10000)) / 10000;
                    } else {
                        gemose = emose;
                    }

                    chr.addMaplewing("emose", -gemose);
                    chr.addMaplewing("wmose", gemose * 10000);
                }
                int nwmose = chr.getMaplewing("wmose");
                if (nwmose > jmeso) {
                    gwmose = jmeso;
                    gmeso = 0;
                    gsavemose = 0;
                } else {
                    gwmose = nwmose;
                }

                if ((nwmose < jmeso) && (savemose > jmeso - nwmose)) {
                    gsavemose = jmeso - nwmose;
                    gmeso = 0;
                } else {
                    if (savemose < jmeso - nwmose) {
                        gsavemose = savemose;
                    } else {
                        gsavemose = 0;
                    }
                }

                if ((nwmose + savemose < jmeso) && (meso > (jmeso - (nwmose + savemose)) * 10000)) {
                    gmeso = jmeso * 10000;
                }

                chr.addMaplewing("wmose", -gwmose);
                chr.addMaplewing("savemose", -gsavemose);
                chr.gainMeso(-gmeso, true);

                nwmose = chr.getMaplewing("wmose");
                emose = chr.getMaplewing("emose");
                savemose = chr.getMaplewing("savemose");
                meso = chr.getMeso();

                int tmesos = nwmose + emose * 10000 + savemose + (int) meso / 10000;

                String names = chr.getVipname() + " 系统自动扣除您 " + jmeso + " 万金币财富！目前您的金币财富总额为 " + tmesos + "万金币！";
                chr.dropMessage(-1, names);
                chr.dropMessage(-11, names);

            } else {
                chr.addMaplewing("wmose", 100 * ds);
                String names = chr.getVipname() + " 您金币财富低于1E金币财富！系统发放： " + (100 * ds) + "万成长基金到您的MapleBank万级账目！";
                chr.dropMessage(-1, names);
                chr.dropMessage(-11, names);

            }


        }
    }

    public void MaplewingGainMose(int ds) {

        for (MapleCharacter chr : this.players.getAllCharacters()) {
            int times = chr.getMaplewing("havetime");
            int savemose = chr.getMaplewing("savemose");
            int gmosse = chr.getMaplewing("gainmoses");

            int gas = chr.getMaplewingZJS(0, ds);

            if (times >= ds) {
                chr.addMaplewing("havetime", -ds);
                chr.upMaplewing();


                if (gmosse <= 2000000000) {
                    chr.addMaplewing("gainmoses", gas);
                    chr.upMaplewing();

                    int nowtimes = chr.getMaplewing("havetime");
                    int mosse = chr.getMaplewing("gainmoses");

                    String namess = chr.getVipname() + " 您购买的MaplewingBank债券返利时间还剩" + nowtimes + " .本次返利" + gas + " 金币！！";
                    chr.dropMessage(-1, namess);
                    chr.dropMessage(-11, namess);

                    if ((times <= 5) && (times > 0)) {
                        String names = chr.getVipname() + " 您购买的MaplewingBank债券返利时间仅剩" + nowtimes + " .目前已经返利" + mosse + " 金币，赶快去金融专区查看你的收入吧！！";
                        chr.dropMessage(-1, names);
                        chr.dropMessage(-11, names);
                    }
                }

            } else {
                String names = chr.getVipname() + " MaplewingBank债券信息已经刷新！快去金融储蓄购买债券或获得您的利息吧！";
                chr.dropMessage(-1, names);
                chr.dropMessage(-11, names);

            }

        }

    }


    public boolean getLvkejian() {
        return lvkejian;
    }

    public void setLvkejian(int mds) {
        if (mds > 1) {
            lvkejian = true;
        } else {
            lvkejian = false;
        }

    }

    public static boolean get鉴定出BOSS30() {
        return 鉴定出BOSS30;
    }

    public static boolean get鉴定出顶级属性() {
        return 鉴定出顶级属性;
    }

    public static boolean get鉴定出全属性20() {
        return 鉴定出全属性20;
    }

    public static void set鉴定出全属性20(int mds) {
        if (mds > 1) {
            鉴定出全属性20 = true;
        } else {
            鉴定出全属性20 = false;
        }

    }


    public static int getBossbalrog() {
        return bossbalrog;
    }

    public void setBossbalrog(int bossbalrogs) {
        bossbalrog = bossbalrogs;
    }

    public static int getChaoszak() {
        return chaoszak;
    }

    public void setChaoszak(int chaoszaks) {
        chaoszak = chaoszaks;
    }

    public static int getPinkzak() {
        return pinkzak;
    }

    public void setPinkzak(int pinkzaks) {
        pinkzak = pinkzaks;
    }

    public static int getHorntail() {
        return horntail;
    }

    public void setHorntail(int horntails) {
        horntail = horntails;
    }

    public static int getChaosht() {
        return chaosht;
    }

    public void setChaosht(int chaoshts) {
        chaosht = chaoshts;
    }

    public static int getPinkbean() {
        return pinkbean;
    }

    public void setPinkbean(int pinkbeans) {
        pinkbean = pinkbeans;
    }

    public static int getZak() {
        return zak;
    }

    public void setZak(int zaks) {
        zak = zaks;
    }

    public static int getVonleon() {
        return vonleon;
    }

    public void setVonleon(int vonleons) {
        vonleon = vonleons;
    }

    public static int getCygnus() {
        return cygnus;
    }

    public void setCygnus(int cygnuss) {
        cygnus = cygnuss;
    }


    public static int getAkayile() {
        return akayile;
    }

    public void setAkayiles(int Akayiles) {
        akayile = Akayiles;
    }

    public static int getXila() {
        return xila;
    }

    public void setXila(int xilas) {
        xila = xilas;
    }

    public static int getBaojun() {
        return baojun;
    }

    public void setBaojun(int baojuns) {
        baojun = baojuns;
    }


    public static int getAllboss() {
        return allboss;
    }

    public static void setAllboss(int vonleons) {
        allboss = vonleons;
    }


    public static int getNmm_squad() {
        return nmm_squad;
    }

    public void setNmm_squad(int nmm_squads) {
        nmm_squad = nmm_squads;
    }

    public static int getVergamot() {
        return vergamot;
    }

    public void getVergamot(int cygnuss) {
        vergamot = cygnuss;
    }

    public static int getDunas() {
        return dunas;
    }

    public void setDunas(int cygnuss) {
        dunas = cygnuss;
    }


    public static int getNibergen_squad() {
        return nibergen_squad;
    }

    public void setNibergen_squad(int cygnuss) {
        nibergen_squad = cygnuss;
    }

    public static int getDunas2() {
        return dunas2;
    }

    public void setDunas2(int cygnuss) {
        dunas2 = cygnuss;
    }

    public static int getCore_blaze() {
        return core_blaze;
    }

    public void setCore_blaze(int cygnuss) {
        core_blaze = cygnuss;
    }

    public static int getAufheben() {
        return aufheben;
    }

    public void setAufheben(int cygnuss) {
        aufheben = cygnuss;
    }

    public static int getCwkpq() {
        return cwkpq;
    }

    public void setCwkpq(int cygnuss) {
        cwkpq = cygnuss;
    }

    public static int getTokyo_2095() {
        return tokyo_2095;
    }

    public void setTokyo_2095(int cygnuss) {
        tokyo_2095 = cygnuss;
    }

    public static int getScartar() {
        return scartar;
    }

    public void setScartar(int cygnuss) {
        scartar = cygnuss;
    }

    public int getfirstMap() {
        return firstMap;
    }

    public void setfirstMap(int cygnuss) {
        firstMap = cygnuss;
    }

    public static int getpogpngbilv() {
        return pogongbilv;
    }

    public static void setpogongbilv(int cygnuss) {
        pogongbilv = cygnuss;
    }


    public boolean isCanPvp() {
        return this.canPvp;
    }

    //取得 潜能改变和概率
    public int getStateRate() {
        return this.stateRate;
    }

    public void setStateRate(int stateRate) {
        this.stateRate = stateRate;
    }

    //取得潜能数目的改变数目
    public int getStateLines() {
        if ((this.stateLines < 0) || (this.stateLines > 5)) {
            this.stateLines = 3;
        }
        return this.stateLines;
    }

    //取得潜能数目的改变数目
    public int get潜能数目改变基本概率() {
        if ((this.潜能数目改变基本概率 < 0) || (this.潜能数目改变基本概率 > 100)) {
            this.stateLines = 1;
        }
        return this.潜能数目改变基本概率;
    }

    public void set潜能数目改变基本概率(int stateRate) {
        this.潜能数目改变基本概率 = stateRate;
    }

    public void setStateLines(int lines) {
        if ((this.stateLines < 0) || (this.stateLines > 5)) {
            this.stateLines = 3;
        } else {
            this.stateLines = lines;
        }
    }

    public static MapleCharacter getCharacterById(int id) {
        for (ChannelServer cserv_ : getAllInstances()) {
            MapleCharacter ret = cserv_.getPlayerStorage().getCharacterById(id);
            if (ret != null) {
                return ret;
            }
        }
        return null;
    }

    public static MapleCharacter getCharacterByName(String name) {
        for (ChannelServer cserv_ : getAllInstances()) {
            MapleCharacter ret = cserv_.getPlayerStorage().getCharacterByName(name);
            if (ret != null) {
                return ret;
            }
        }
        return null;
    }

    public int getSharePrice() {
        return this.sharePrice;
    }

    public void loadShare() {
        if (this.channel != 1 || this.finishedShutdown) {
            return;
        }
        this.shareCon = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = this.shareCon.prepareStatement("SELECT * FROM shares WHERE channelid = ?");
            ps.setInt(1, 1);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                this.sharePrice = rs.getInt("currentprice");
            } else {
                throw new RuntimeException("[EXCEPTION] 无法加载股票数据.");
            }
            log.info("目前的股票价格: " + this.sharePrice);
            rs.close();
            ps.close();
        } catch (SQLException e) {
            log.error("ERROR Load Shares", e);
        }
    }

    public void increaseShare(int share) {
        if ((this.channel != 1) || (this.finishedShutdown)) {
            return;
        }
        this.sharePrice += share;
        try {
            PreparedStatement ps = this.shareCon.prepareStatement("UPDATE shares SET currentprice = ? WHERE channelid = 1");
            ps.setInt(1, this.sharePrice);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            log.error("ERROR Increase Shares", e);
        }
    }

    public void decreaseShare(int share) {
        if ((this.channel != 1) || (this.finishedShutdown)) {
            return;
        }
        this.sharePrice -= share;
        if (this.sharePrice < 0) {
            this.sharePrice = 0;
        }
        try {
            PreparedStatement ps = this.shareCon.prepareStatement("UPDATE shares SET currentprice = ? WHERE channelid = 1");
            ps.setInt(1, this.sharePrice);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            log.error("ERROR Decrease Shares", e);
        }
    }

    public void saveShares() {
        if ((this.channel != 1) || (this.finishedShutdown)) {
            return;
        }
        try {
            PreparedStatement ps = this.shareCon.prepareStatement("UPDATE shares SET currentprice = ? WHERE channelid = 1");
            ps.setInt(1, this.sharePrice);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            log.error("ERROR Save Shares", e);
        }
    }
}