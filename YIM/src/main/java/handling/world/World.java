package handling.world;

import client.BuddyList;
import client.BuddyList.BuddyOperation;
import client.BuddylistEntry;
import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleCoolDownValueHolder;
import client.MapleDiseaseValueHolder;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.inventory.PetDataFactory;
import client.status.MonsterStatusEffect;
import database.DatabaseConnection;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.channel.PlayerStorage;
import handling.world.exped.ExpeditionType;
import handling.world.exped.MapleExpedition;
import handling.world.exped.PartySearch;
import handling.world.exped.PartySearchType;
import handling.world.family.MapleFamily;
import handling.world.family.MapleFamilyCharacter;
import handling.world.guild.MapleBBSThread;
import handling.world.guild.MapleGuild;
import handling.world.guild.MapleGuildAlliance;
import handling.world.guild.MapleGuildCharacter;
import handling.world.sidekick.MapleSidekick;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.log4j.Logger;
import server.Timer.WorldTimer;
import server.life.MapleMonster;
import server.maps.MapleMap;
import server.maps.MapleMapItem;
import server.shops.HiredMerchant;
import tools.CollectionUtil;
import tools.FileoutputUtil;
import tools.MaplePacketCreator;
import tools.packet.BuddyListPacket;
import tools.packet.GuildPacket;
import tools.packet.PartyPacket;
import tools.packet.PetPacket;

public class World {

    private static final Logger log = Logger.getLogger(World.class);
    private static final int CHANNELS_PER_THREAD = 3;

    public static void init() {
        Find.findChannel(0);
        Sidekick.lock.toString();
        Alliance.lock.toString();
        Messenger.getMessenger(0);
        Party.getParty(0);
    }

    public static String getStatus() {
        StringBuilder ret = new StringBuilder();
        int totalUsers = 0;
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            ret.append("频道 ");
            ret.append(cs.getChannel());
            ret.append(": ");
            int channelUsers = cs.getConnectedClients();
            totalUsers += channelUsers;
            ret.append(channelUsers);
            ret.append(" 玩家\n");
        }
        ret.append("总计在线: ");
        ret.append(totalUsers);
        ret.append("\n");
        return ret.toString();
    }

    public static Map<Integer, Integer> getConnected() {
        Map<Integer, Integer> ret = new HashMap<>();
        int total = 0;
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            int curConnected = cs.getConnectedClients();
            ret.put(cs.getChannel(), curConnected);
            total += curConnected;
        }
        ret.put(0, total);
        return ret;
    }

    public static List<CheaterData> getCheaters() {
        List<CheaterData> allCheaters = new ArrayList<>();
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            allCheaters.addAll(cs.getCheaters());
        }
        Collections.sort(allCheaters);
        return CollectionUtil.copyFirst(allCheaters, 20);
    }

    public static List<CheaterData> getReports() {
        List<CheaterData> allCheaters = new ArrayList<>();
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            allCheaters.addAll(cs.getReports());
        }
        Collections.sort(allCheaters);
        return CollectionUtil.copyFirst(allCheaters, 20);
    }

    public static boolean isConnected(String charName) {
        return Find.findChannel(charName) > 0;
    }

    public static void toggleMegaphoneMuteState() {
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            cs.toggleMegaphoneMuteState();
        }
    }

    public static void ChannelChange_Data(CharacterTransfer Data, int characterid, int toChannel) {
        getStorage(toChannel).registerPendingPlayer(Data, characterid);
    }

    public static boolean isCharacterListConnected(List<String> charName) {
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            for (String c : charName) {
                if (cs.getPlayerStorage().getCharacterByName(c) != null) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean hasMerchant(int accountID) {
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            if (cs.containsMerchant(accountID)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasMerchant(int accountID, int characterID) {
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            if (cs.containsMerchant(accountID, characterID)) {
                return true;
            }
        }
        return false;
    }

    public static HiredMerchant getMerchant(int accountID, int characterID) {
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            if (cs.containsMerchant(accountID, characterID)) {
                return cs.getHiredMerchants(accountID, characterID);
            }
        }
        return null;
    }

    public static PlayerStorage getStorage(int channel) {
        if (channel == -20) {
            return CashShopServer.getPlayerStorageMTS();
        }
        if (channel == -10) {//原先值为  -10
            return CashShopServer.getPlayerStorage();
        }
        return ChannelServer.getInstance(channel).getPlayerStorage();
    }

    public static int getPendingCharacterSize() {
        int ret = CashShopServer.getPlayerStorage().pendingCharacterSize() + CashShopServer.getPlayerStorageMTS().pendingCharacterSize();
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            ret += cserv.getPlayerStorage().pendingCharacterSize();
        }
        return ret;
    }

    public static boolean isChannelAvailable(int ch) {
        if ((ChannelServer.getInstance(ch) == null) || (ChannelServer.getInstance(ch).getPlayerStorage() == null)) {
            return false;
        }
        return ChannelServer.getInstance(ch).getPlayerStorage().getConnectedClients() < (ch == 1 ? 600 : 400);
    }

    public static void registerRespawn() {
        Integer[] chs = ChannelServer.getAllInstance().toArray(new Integer[0]);
        for (int i = 0; i < chs.length; i += 3) {
            WorldTimer.getInstance().register(new Respawn(chs, i), 4500L);
        }
    }

    public static void handleMap(MapleMap map, int numTimes, int size, long now) {

        if (map.getItemsSize() > 0) {
            for (MapleMapItem item : map.getAllItemsThreadsafe()) {
                if (item.shouldExpire(now)) {
                    item.expire(map);
                } else if (item.shouldFFA(now)) {
                    item.setDropType((byte) 2);
                }
            }
        }
        Iterator i$;
        if ((map.characterSize() > 0) || (map.getId() == 931000500)) {
            if (map.canSpawn(now)) {
                map.respawn(false, now);
            }
            boolean hurt = map.canHurt(now);
            for (MapleCharacter chr : map.getCharactersThreadsafe()) {
                handleCooldowns(chr, numTimes, hurt, now);
            }
            if (map.getMobsSize() > 0) {
                for (MapleMonster mons : map.getAllMonstersThreadsafe()) {
                    if ((mons.isAlive()) && (mons.shouldKill(now))) {
                        map.killMonster(mons);
                    } else if ((mons.isAlive()) && (mons.shouldDrop(now))) {
                        mons.doDropItem(now);
                    } else if ((mons.isAlive()) && (mons.getStatiSize() > 0)) {
                        for (MonsterStatusEffect mse : mons.getAllBuffs()) {
                            if (mse.shouldCancel(now)) {
                                mons.cancelSingleStatus(mse);
                            }
                        }
                    }
                }
            }
        }

    }

    public static void handleCooldowns(MapleCharacter chr, int numTimes, boolean hurt, long now) {
        if (chr.getCooldownSize() > 0) {
            for (MapleCoolDownValueHolder m : chr.getCooldowns()) {
                if (m.startTime + m.length < now) {
                    int skil = m.skillId;
                    chr.removeCooldown(skil);
                    chr.getClient().getSession().write(MaplePacketCreator.skillCooldown(skil, 0));
                }
            }
        }
        if (chr.isAlive()) {
            if (((chr.getJob() == 131) || (chr.getJob() == 132))
                    && (chr.canBlood(now))) {
                chr.doDragonBlood();
            }

            if (chr.canRecover(now)) {
                chr.doRecovery();
            }
            if (chr.canHPRecover(now)) {
                chr.addHP((int) chr.getStat().getHealHP());
            }
            if (chr.canMPRecover(now)) {
                chr.addMP((int) chr.getStat().getHealMP());
            }
            if (chr.canFairy(now)) {
                chr.doFairy();
            }
            if (chr.canFish(now)) {
                chr.doFish(now);
            }
            if (chr.canDOT(now)) {
                chr.doDOT();
            }
        }
        if (chr.getDiseaseSize() > 0) {
            for (MapleDiseaseValueHolder m : chr.getAllDiseases()) {
                if ((m != null) && (m.startTime + m.length < now)) {
                    chr.dispelDebuff(m.disease);
                }
            }
        }
        if ((numTimes % 7 == 0) && (chr.getMount() != null) && (chr.getMount().canTire(now))) {
            chr.getMount().increaseFatigue();
        }
        if (numTimes % 13 == 0) {
            chr.doFamiliarSchedule(now);
            for (MaplePet pet : chr.getSummonedPets()) {
                if ((pet.getPetItemId() == 5000054) && (pet.getSecondsLeft() > 0)) {
                    pet.setSecondsLeft(pet.getSecondsLeft() - 1);
                    if (pet.getSecondsLeft() <= 0) {
                        chr.unequipPet(pet, true, true);
                        return;
                    }
                }
                int newFullness = pet.getFullness() - PetDataFactory.getHunger(pet.getPetItemId());
                if (newFullness <= 5) {
                    pet.setFullness(15);
                    chr.unequipPet(pet, true, true);
                } else {
                    pet.setFullness(newFullness);
                    chr.getClient().getSession().write(PetPacket.updatePet(pet, chr.getInventory(MapleInventoryType.CASH).getItem(pet.getInventoryPosition()), true));
                }
            }
        }
        if ((hurt) && (chr.isAlive())
                && (chr.getInventory(MapleInventoryType.EQUIPPED).findById(chr.getMap().getHPDecProtect()) == null)) {
            if ((chr.getMapId() == 749040100) && (chr.getInventory(MapleInventoryType.CASH).findById(5451000) == null)) {
                chr.addHP(-chr.getMap().getHPDec());
            } else if (chr.getMapId() != 749040100) {
                chr.addHP(-(chr.getMap().getHPDec() - (chr.getBuffedValue(MapleBuffStat.HP_LOSS_GUARD) == null ? 0 : chr.getBuffedValue(MapleBuffStat.HP_LOSS_GUARD).intValue())));
            }
        }
    }

    public static class Respawn
            implements Runnable {

        private int numTimes = 0;
        private final List<ChannelServer> cservs = new ArrayList(3);

        public Respawn(Integer[] chs, int c) {
            StringBuilder s = new StringBuilder("[Respawn Worker] Registered for channels ");
            for (int i = 1; (i <= 3) && (chs.length >= c + i); i++) {
                this.cservs.add(ChannelServer.getInstance(c + i));
                s.append(c + i).append(" ");
            }
            System.out.println(s.toString());
        }

        public void run() {
            this.numTimes += 1;
            long now = System.currentTimeMillis();
            for (ChannelServer cserv : this.cservs) {
                if (!cserv.hasFinishedShutdown()) {
                    for (MapleMap map : cserv.getMapFactory().getAllLoadedMaps()) {
                        World.handleMap(map, this.numTimes, map.getCharactersSize(), now);
                    }
                }
            }
        }
    }

    public static class Family {

        private static final Map<Integer, MapleFamily> families = new LinkedHashMap();
        private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

        public static void addLoadedFamily(MapleFamily f) {
            if (f.isProper()) {
                families.put(Integer.valueOf(f.getId()), f);
            }
        }

        public static MapleFamily getFamily(int id) {
            MapleFamily ret = null;
            lock.readLock().lock();
            try {
                ret = (MapleFamily) families.get(Integer.valueOf(id));
            } finally {
                lock.readLock().unlock();
            }
            if (ret == null) {
                lock.writeLock().lock();
                try {
                    ret = new MapleFamily(id);
                    if ((ret == null) || (ret.getId() <= 0) || (!ret.isProper())) {
                        return null;
                    }
                    families.put(Integer.valueOf(id), ret);
                } finally {
                    lock.writeLock().unlock();
                }
            }
            return ret;
        }

        public static void memberFamilyUpdate(MapleFamilyCharacter mfc, MapleCharacter mc) {
            MapleFamily f = getFamily(mfc.getFamilyId());
            if (f != null) {
                f.memberLevelJobUpdate(mc);
            }
        }

        public static void setFamilyMemberOnline(MapleFamilyCharacter mfc, boolean bOnline, int channel) {
            MapleFamily f = getFamily(mfc.getFamilyId());
            if (f != null) {
                f.setOnline(mfc.getId(), bOnline, channel);
            }
        }

        public static int setRep(int fid, int cid, int addrep, int oldLevel, String oldName) {
            MapleFamily f = getFamily(fid);
            if (f != null) {
                return f.setRep(cid, addrep, oldLevel, oldName);
            }
            return 0;
        }

        public static void save() {
            System.out.println("正在保存学院数据...");
            lock.writeLock().lock();
            try {
                for (MapleFamily a : families.values()) {
                    a.writeToDB(false);
                }
            } finally {
                lock.writeLock().unlock();
            }
        }

        public static void setFamily(int familyid, int seniorid, int junior1, int junior2, int currentrep, int totalrep, int cid) {
            int ch = World.Find.findChannel(cid);
            if (ch == -1) {
                return;
            }
            MapleCharacter mc = World.getStorage(ch).getCharacterById(cid);
            if (mc == null) {
                return;
            }
            boolean bDifferent = (mc.getFamilyId() != familyid) || (mc.getSeniorId() != seniorid) || (mc.getJunior1() != junior1) || (mc.getJunior2() != junior2);
            mc.setFamily(familyid, seniorid, junior1, junior2);
            mc.setCurrentRep(currentrep);
            mc.setTotalRep(totalrep);
            if (bDifferent) {
                mc.saveFamilyStatus();
            }
        }

        public static void familyPacket(int gid, byte[] message, int cid) {
            MapleFamily f = getFamily(gid);
            if (f != null) {
                f.broadcast(message, -1, f.getMFC(cid).getPedigree());
            }
        }

        public static void disbandFamily(int gid) {
            MapleFamily g = getFamily(gid);
            if (g != null) {
                lock.writeLock().lock();
                try {
                    families.remove(Integer.valueOf(gid));
                } finally {
                    lock.writeLock().unlock();
                }
                g.disbandFamily();
            }
        }
    }

    public static class Alliance {

        private static final Map<Integer, MapleGuildAlliance> alliances = new LinkedHashMap();
        private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

        public static MapleGuildAlliance getAlliance(int allianceid) {
            MapleGuildAlliance ret = null;
            lock.readLock().lock();
            try {
                ret = (MapleGuildAlliance) alliances.get(Integer.valueOf(allianceid));
            } finally {
                lock.readLock().unlock();
            }
            if (ret == null) {
                lock.writeLock().lock();
                try {
                    ret = new MapleGuildAlliance(allianceid);
                    if ((ret == null) || (ret.getId() <= 0)) {
                        return null;
                    }
                    alliances.put(Integer.valueOf(allianceid), ret);
                } finally {
                    lock.writeLock().unlock();
                }
            }
            return ret;
        }

        public static int getAllianceLeader(int allianceid) {
            MapleGuildAlliance mga = getAlliance(allianceid);
            if (mga != null) {
                return mga.getLeaderId();
            }
            return 0;
        }

        public static void updateAllianceRanks(int allianceid, String[] ranks) {
            MapleGuildAlliance mga = getAlliance(allianceid);
            if (mga != null) {
                mga.setRank(ranks);
            }
        }

        public static void updateAllianceNotice(int allianceid, String notice) {
            MapleGuildAlliance mga = getAlliance(allianceid);
            if (mga != null) {
                mga.setNotice(notice);
            }
        }

        public static boolean canInvite(int allianceid) {
            MapleGuildAlliance mga = getAlliance(allianceid);
            if (mga != null) {
                return mga.getCapacity() > mga.getNoGuilds();
            }
            return false;
        }

        public static boolean changeAllianceLeader(int allianceid, int cid) {
            MapleGuildAlliance mga = getAlliance(allianceid);
            if (mga != null) {
                return mga.setLeaderId(cid);
            }
            return false;
        }

        public static boolean changeAllianceLeader(int allianceid, int cid, boolean sameGuild) {
            MapleGuildAlliance mga = getAlliance(allianceid);
            if (mga != null) {
                return mga.setLeaderId(cid, sameGuild);
            }
            return false;
        }

        public static boolean changeAllianceRank(int allianceid, int cid, int change) {
            MapleGuildAlliance mga = getAlliance(allianceid);
            if (mga != null) {
                return mga.changeAllianceRank(cid, change);
            }
            return false;
        }

        public static boolean changeAllianceCapacity(int allianceid) {
            MapleGuildAlliance mga = getAlliance(allianceid);
            if (mga != null) {
                return mga.setCapacity();
            }
            return false;
        }

        public static boolean disbandAlliance(int allianceid) {
            MapleGuildAlliance mga = getAlliance(allianceid);
            if (mga != null) {
                return mga.disband();
            }
            return false;
        }

        public static boolean addGuildToAlliance(int allianceid, int gid) {
            MapleGuildAlliance mga = getAlliance(allianceid);
            if (mga != null) {
                return mga.addGuild(gid);
            }
            return false;
        }

        public static boolean removeGuildFromAlliance(int allianceid, int gid, boolean expelled) {
            MapleGuildAlliance mga = getAlliance(allianceid);
            if (mga != null) {
                return mga.removeGuild(gid, expelled);
            }
            return false;
        }

        public static void sendGuild(int allianceid) {
            MapleGuildAlliance alliance = getAlliance(allianceid);
            if (alliance != null) {
                sendGuild(GuildPacket.getAllianceUpdate(alliance), -1, allianceid);
                sendGuild(GuildPacket.getGuildAlliance(alliance), -1, allianceid);
            }
        }

        public static void sendGuild(byte[] packet, int exceptionId, int allianceid) {
            MapleGuildAlliance alliance = getAlliance(allianceid);
            if (alliance != null) {
                for (int i = 0; i < alliance.getNoGuilds(); i++) {
                    int gid = alliance.getGuildId(i);
                    if ((gid > 0) && (gid != exceptionId)) {
                        World.Guild.guildPacket(gid, packet);
                    }
                }
            }
        }

        public static boolean createAlliance(String alliancename, int cid, int cid2, int gid, int gid2) {
            int allianceid = MapleGuildAlliance.createToDb(cid, alliancename, gid, gid2);
            if (allianceid <= 0) {
                return false;
            }
            MapleGuild g = World.Guild.getGuild(gid);
            MapleGuild g_ = World.Guild.getGuild(gid2);
            g.setAllianceId(allianceid);
            g_.setAllianceId(allianceid);
            g.changeARank(true);
            g_.changeARank(false);

            MapleGuildAlliance alliance = getAlliance(allianceid);

            sendGuild(GuildPacket.createGuildAlliance(alliance), -1, allianceid);
            sendGuild(GuildPacket.getAllianceInfo(alliance), -1, allianceid);
            sendGuild(GuildPacket.getGuildAlliance(alliance), -1, allianceid);
            sendGuild(GuildPacket.changeAlliance(alliance, true), -1, allianceid);
            return true;
        }

        public static void allianceChat(int gid, String name, int cid, String msg) {
            MapleGuild g = World.Guild.getGuild(gid);
            if (g != null) {
                MapleGuildAlliance ga = getAlliance(g.getAllianceId());
                if (ga != null) {
                    for (int i = 0; i < ga.getNoGuilds(); i++) {
                        MapleGuild g_ = World.Guild.getGuild(ga.getGuildId(i));
                        if (g_ != null) {
                            g_.allianceChat(name, cid, msg);
                        }
                    }
                }
            }
        }

        public static void setNewAlliance(int gid, int allianceid) {
            MapleGuildAlliance alliance = getAlliance(allianceid);
            MapleGuild guild = World.Guild.getGuild(gid);
            if ((alliance != null) && (guild != null)) {
                for (int i = 0; i < alliance.getNoGuilds(); i++) {
                    if (gid == alliance.getGuildId(i)) {
                        guild.setAllianceId(allianceid);
                        guild.broadcast(GuildPacket.getAllianceInfo(alliance));
                        guild.broadcast(GuildPacket.getGuildAlliance(alliance));
                        guild.broadcast(GuildPacket.changeAlliance(alliance, true));
                        guild.changeARank();
                        guild.writeToDB(false);
                    } else {
                        MapleGuild g_ = World.Guild.getGuild(alliance.getGuildId(i));
                        if (g_ != null) {
                            g_.broadcast(GuildPacket.addGuildToAlliance(alliance, guild));
                            g_.broadcast(GuildPacket.changeGuildInAlliance(alliance, guild, true));
                        }
                    }
                }
            }
        }

        public static void setOldAlliance(int gid, boolean expelled, int allianceid) {
            MapleGuildAlliance alliance = getAlliance(allianceid);
            MapleGuild g_ = World.Guild.getGuild(gid);
            if (alliance != null) {
                for (int i = 0; i < alliance.getNoGuilds(); i++) {
                    MapleGuild guild = World.Guild.getGuild(alliance.getGuildId(i));
                    if (guild == null) {
                        if (gid != alliance.getGuildId(i)) {
                            alliance.removeGuild(gid, false, true);
                        }

                    } else if ((g_ == null) || (gid == alliance.getGuildId(i))) {
                        guild.changeARank(5);
                        guild.setAllianceId(0);
                        guild.broadcast(GuildPacket.disbandAlliance(allianceid));
                    } else if (g_ != null) {
                        guild.broadcast(MaplePacketCreator.serverNotice(5, "[" + g_.getName() + "] 家族退出家族联盟."));
                        guild.broadcast(GuildPacket.changeGuildInAlliance(alliance, g_, false));
                        guild.broadcast(GuildPacket.removeGuildFromAlliance(alliance, g_, expelled));
                    }
                }
            }
            if (gid == -1) {
                lock.writeLock().lock();
                try {
                    alliances.remove(Integer.valueOf(allianceid));
                } finally {
                    lock.writeLock().unlock();
                }
            }
        }

        public static List<byte[]> getAllianceInfo(int allianceid, boolean start) {
            List ret = new ArrayList();
            MapleGuildAlliance alliance = getAlliance(allianceid);
            if (alliance != null) {
                if (start) {
                    ret.add(GuildPacket.getAllianceInfo(alliance));
                    ret.add(GuildPacket.getGuildAlliance(alliance));
                }
                ret.add(GuildPacket.getAllianceUpdate(alliance));
            }
            return ret;
        }

        public static void save() {
            System.out.println("正在保存家族联盟数据...");
            lock.writeLock().lock();
            try {
                for (MapleGuildAlliance a : alliances.values()) {
                    a.saveToDb();
                }
            } finally {
                lock.writeLock().unlock();
            }
        }

        static {
            Collection<MapleGuildAlliance> allGuilds = MapleGuildAlliance.loadAll();
            for (MapleGuildAlliance g : allGuilds) {
                alliances.put(Integer.valueOf(g.getId()), g);
            }
        }
    }

    public static class Find {

        private static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        private static HashMap<Integer, Integer> idToChannel = new HashMap();
        private static HashMap<String, Integer> nameToChannel = new HashMap();

        public static void register(int id, String name, int channel) {
            lock.writeLock().lock();
            try {
                idToChannel.put(Integer.valueOf(id), Integer.valueOf(channel));
                nameToChannel.put(name.toLowerCase(), Integer.valueOf(channel));
            } finally {
                lock.writeLock().unlock();
            }
            
            if (channel == -10) {//原先值为  -10
                System.out.println("时间：" + FileoutputUtil.CurrentReadable_Time() + "  玩家连接 - 角色ID: " + id + " 名字: " + name + " 进入商城");
            } else {
                System.out.println("时间：" + FileoutputUtil.CurrentReadable_Time() + "  玩家连接 - 角色ID: " + id + " 名字: " + name + " 频道: " + channel);
            }
        }

        public static void forceDeregister(int id) {
            lock.writeLock().lock();
            try {
                idToChannel.remove(Integer.valueOf(id));
            } finally {
                lock.writeLock().unlock();
            }
            System.out.println("时间：" + FileoutputUtil.CurrentReadable_Time() + "  玩家离开 - 角色ID: " + id);
        }

        public static void forceDeregister(String id) {
            lock.writeLock().lock();
            try {
                nameToChannel.remove(id.toLowerCase());
            } finally {
                lock.writeLock().unlock();
            }
            System.out.println("时间：" + FileoutputUtil.CurrentReadable_Time() + "  玩家离开 - 角色ID: " + id);
        }

        public static void forceDeregister(int id, String name) {
            lock.writeLock().lock();
            try {
                idToChannel.remove(Integer.valueOf(id));
                nameToChannel.remove(name.toLowerCase());
            } finally {
                lock.writeLock().unlock();
            }
            System.out.println("时间：" + FileoutputUtil.CurrentReadable_Time() + "  玩家离开 - 角色ID: " + id + " 名字: " + name);
        }

        public static int findChannel(int id) {
            lock.readLock().lock();
            Integer ret;
            try {
                ret = (Integer) idToChannel.get(Integer.valueOf(id));
            } finally {
                lock.readLock().unlock();
            }
            if (ret != null) {// 4 原先值为  -10
                if ((ret.intValue() != -10) && (ret.intValue() != -20) && (ChannelServer.getInstance(ret.intValue()) == null)) {
                    forceDeregister(id);
                    return -1;
                }
                return ret.intValue();
            }
            return -1;
        }

        public static int findChannel(String st) {
            lock.readLock().lock();
            Integer ret;
            try {
                ret = (Integer) nameToChannel.get(st.toLowerCase());
            } finally {
                lock.readLock().unlock();
            }
            if (ret != null) {// 4 原先值为  -10
                if ((ret.intValue() != -10) && (ret.intValue() != -20) && (ChannelServer.getInstance(ret.intValue()) == null)) {
                    forceDeregister(st);
                    return -1;
                }
                return ret.intValue();
            }
            return -1;
        }

        public static CharacterIdChannelPair[] multiBuddyFind(int charIdFrom, int[] characterIds) {
            List foundsChars = new ArrayList(characterIds.length);
            for (int i : characterIds) {
                int channel = findChannel(i);
                if (channel > 0) {
                    foundsChars.add(new CharacterIdChannelPair(i, channel));
                }
            }
            Collections.sort(foundsChars);
            return (CharacterIdChannelPair[]) foundsChars.toArray(new CharacterIdChannelPair[foundsChars.size()]);
        }
    }

    public static class Broadcast {

        public static void broadcastSmega(byte[] message) {
            for (ChannelServer cs : ChannelServer.getAllInstances()) {
                cs.broadcastSmega(message);
            }
        }

        public static void broadcastGMMessage(byte[] message) {
            for (ChannelServer cs : ChannelServer.getAllInstances()) {
                cs.broadcastGMMessage(message);
            }
        }

        public static void broadcastMessage(byte[] message) {
            for (ChannelServer cs : ChannelServer.getAllInstances()) {
                cs.broadcastMessage(message);
            }
        }

        public static void startMapEffect(String msg, int itemId) {
            for (ChannelServer cs : ChannelServer.getAllInstances()) {
                cs.startMapEffect(msg, itemId);
            }
        }

        public static void startMapEffect(String msg, int itemId, int time) {
            for (ChannelServer cs : ChannelServer.getAllInstances()) {
                cs.startMapEffect(msg, itemId, time);
            }
        }

        public static void sendPacket(List<Integer> targetIds, byte[] packet, int exception) {
            for (Iterator i$ = targetIds.iterator(); i$.hasNext();) {
                int i = ((Integer) i$.next()).intValue();
                if (i == exception) {
                    continue;
                }
                int ch = World.Find.findChannel(i);
                if (ch < 0) {
                    continue;
                }
                MapleCharacter c = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterById(i);
                if (c != null) {
                    c.getClient().getSession().write(packet);
                }
            }
        }

        public static void sendPacket(int targetId, byte[] packet) {
            int ch = World.Find.findChannel(targetId);
            if (ch < 0) {
                return;
            }
            MapleCharacter c = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterById(targetId);
            if (c != null) {
                c.getClient().getSession().write(packet);
            }
        }

        public static void sendGuildPacket(int targetIds, byte[] packet, int exception, int guildid) {
            if (targetIds == exception) {
                return;
            }
            int ch = World.Find.findChannel(targetIds);
            if (ch < 0) {
                return;
            }
            MapleCharacter c = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterById(targetIds);
            if ((c != null) && (c.getGuildId() == guildid)) {
                c.getClient().getSession().write(packet);
            }
        }

        public static void sendFamilyPacket(int targetIds, byte[] packet, int exception, int guildid) {
            if (targetIds == exception) {
                return;
            }
            int ch = World.Find.findChannel(targetIds);
            if (ch < 0) {
                return;
            }
            MapleCharacter c = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterById(targetIds);
            if ((c != null) && (c.getFamilyId() == guildid)) {
                c.getClient().getSession().write(packet);
            }
        }
    }

    public static class Guild {

        private static final Map<Integer, MapleGuild> guilds = new LinkedHashMap();
        private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

        public static void addLoadedGuild(MapleGuild f) {
            if (f.isProper()) {
                guilds.put(Integer.valueOf(f.getId()), f);
            }
        }

        public static int createGuild(int leaderId, String name) {
            return MapleGuild.createGuild(leaderId, name);
        }

        public static MapleGuild getGuild(int id) {
            MapleGuild ret = null;
            lock.readLock().lock();
            try {
                ret = (MapleGuild) guilds.get(Integer.valueOf(id));
            } finally {
                lock.readLock().unlock();
            }
            if (ret == null) {
                lock.writeLock().lock();
                try {
                    ret = new MapleGuild(id);
                    if ((ret == null) || (ret.getId() <= 0) || (!ret.isProper())) {
                        return null;
                    }
                    guilds.put(Integer.valueOf(id), ret);
                } finally {
                    lock.writeLock().unlock();
                }
            }
            return ret;
        }

        public static MapleGuild getGuildByName(String guildName) {
            lock.readLock().lock();
            try {
                for (MapleGuild g : guilds.values()) {
                    if (g.getName().equalsIgnoreCase(guildName)) {
                        MapleGuild localMapleGuild1 = g;
                        return localMapleGuild1;
                    }
                }
                return null;
            } finally {
                lock.readLock().unlock();
            }
        }

        public static MapleGuild getGuild(MapleCharacter mc) {
            return getGuild(mc.getGuildId());
        }

        public static void setGuildMemberOnline(MapleGuildCharacter mc, boolean bOnline, int channel) {
            MapleGuild g = getGuild(mc.getGuildId());
            if (g != null) {
                g.setOnline(mc.getId(), bOnline, channel);
            }
        }

        public static void guildPacket(int gid, byte[] message) {
            MapleGuild g = getGuild(gid);
            if (g != null) {
                g.broadcast(message);
            }
        }

        public static int addGuildMember(MapleGuildCharacter mc) {
            MapleGuild g = getGuild(mc.getGuildId());
            if (g != null) {
                return g.addGuildMember(mc);
            }
            return 0;
        }

        public static void leaveGuild(MapleGuildCharacter mc) {
            MapleGuild g = getGuild(mc.getGuildId());
            if (g != null) {
                g.leaveGuild(mc);
            }
        }

        public static void guildChat(int gid, String name, int cid, String msg) {
            MapleGuild g = getGuild(gid);
            if (g != null) {
                g.guildChat(name, cid, msg);
            }
        }

        public static void changeRank(int gid, int cid, int newRank) {
            MapleGuild g = getGuild(gid);
            if (g != null) {
                g.changeRank(cid, newRank);
            }
        }

        public static void expelMember(MapleGuildCharacter initiator, String name, int cid) {
            MapleGuild g = getGuild(initiator.getGuildId());
            if (g != null) {
                g.expelMember(initiator, name, cid);
            }
        }

        public static void setGuildNotice(int gid, String notice) {
            MapleGuild g = getGuild(gid);
            if (g != null) {
                g.setGuildNotice(notice);
            }
        }

        public static void setGuildLeader(int gid, int cid) {
            MapleGuild g = getGuild(gid);
            if (g != null) {
                g.changeGuildLeader(cid);
            }
        }

        public static int getSkillLevel(int gid, int sid) {
            MapleGuild g = getGuild(gid);
            if (g != null) {
                return g.getSkillLevel(sid);
            }
            return 0;
        }

        public static boolean purchaseSkill(int gid, int sid, String name, int cid) {
            MapleGuild g = getGuild(gid);
            if (g != null) {
                return g.purchaseSkill(sid, name, cid);
            }
            return false;
        }

        public static boolean activateSkill(int gid, int sid, String name) {
            MapleGuild g = getGuild(gid);
            if (g != null) {
                return g.activateSkill(sid, name);
            }
            return false;
        }

        public static void memberLevelJobUpdate(MapleGuildCharacter mc) {
            MapleGuild g = getGuild(mc.getGuildId());
            if (g != null) {
                g.memberLevelJobUpdate(mc);
            }
        }

        public static void changeRankTitle(int gid, String[] ranks) {
            MapleGuild g = getGuild(gid);
            if (g != null) {
                g.changeRankTitle(ranks);
            }
        }

        public static void setGuildEmblem(int gid, short bg, byte bgcolor, short logo, byte logocolor) {
            MapleGuild g = getGuild(gid);
            if (g != null) {
                g.setGuildEmblem(bg, bgcolor, logo, logocolor);
            }
        }

        public static void disbandGuild(int gid) {
            MapleGuild g = getGuild(gid);
            lock.writeLock().lock();
            try {
                if (g != null) {
                    g.disbandGuild();
                    guilds.remove(Integer.valueOf(gid));
                }
            } finally {
                lock.writeLock().unlock();
            }
        }

        public static void deleteGuildCharacter(int guildid, int charid) {
            MapleGuild g = getGuild(guildid);
            if (g != null) {
                MapleGuildCharacter mc = g.getMGC(charid);
                if (mc != null) {
                    if (mc.getGuildRank() > 1) {
                        g.leaveGuild(mc);
                    } else {
                        g.disbandGuild();
                    }
                }
            }
        }

        public static boolean increaseGuildCapacity(int gid, boolean b) {
            MapleGuild g = getGuild(gid);
            if (g != null) {
                return g.increaseCapacity(b);
            }
            return false;
        }

        public static void gainGP(int gid, int amount) {
            MapleGuild g = getGuild(gid);
            if (g != null) {
                g.gainGP(amount);
            }
        }

        public static void gainGP(int gid, int amount, int cid) {
            MapleGuild g = getGuild(gid);
            if (g != null) {
                g.gainGP(amount, false, cid);
            }
        }

        public static int getGP(int gid) {
            MapleGuild g = getGuild(gid);
            if (g != null) {
                return g.getGP();
            }
            return 0;
        }

        public static int getInvitedId(int gid) {
            MapleGuild g = getGuild(gid);
            if (g != null) {
                return g.getInvitedId();
            }
            return 0;
        }

        public static void setInvitedId(int gid, int inviteid) {
            MapleGuild g = getGuild(gid);
            if (g != null) {
                g.setInvitedId(inviteid);
            }
        }

        public static int getGuildLeader(int guildName) {
            MapleGuild mga = getGuild(guildName);
            if (mga != null) {
                return mga.getLeaderId();
            }
            return 0;
        }

        public static int getGuildLeader(String guildName) {
            MapleGuild mga = getGuildByName(guildName);
            if (mga != null) {
                return mga.getLeaderId();
            }
            return 0;
        }

        public static void save() {
            System.out.println("正在保存家族数据...");
            lock.writeLock().lock();
            try {
                for (MapleGuild a : guilds.values()) {
                    a.writeToDB(false);
                }
            } finally {
                lock.writeLock().unlock();
            }
        }

        public static List<MapleBBSThread> getBBS(int gid) {
            MapleGuild g = getGuild(gid);
            if (g != null) {
                return g.getBBS();
            }
            return null;
        }

        public static int addBBSThread(int guildid, String title, String text, int icon, boolean bNotice, int posterID) {
            MapleGuild g = getGuild(guildid);
            if (g != null) {
                return g.addBBSThread(title, text, icon, bNotice, posterID);
            }
            return -1;
        }

        public static void editBBSThread(int guildid, int localthreadid, String title, String text, int icon, int posterID, int guildRank) {
            MapleGuild g = getGuild(guildid);
            if (g != null) {
                g.editBBSThread(localthreadid, title, text, icon, posterID, guildRank);
            }
        }

        public static void deleteBBSThread(int guildid, int localthreadid, int posterID, int guildRank) {
            MapleGuild g = getGuild(guildid);
            if (g != null) {
                g.deleteBBSThread(localthreadid, posterID, guildRank);
            }
        }

        public static void addBBSReply(int guildid, int localthreadid, String text, int posterID) {
            MapleGuild g = getGuild(guildid);
            if (g != null) {
                g.addBBSReply(localthreadid, text, posterID);
            }
        }

        public static void deleteBBSReply(int guildid, int localthreadid, int replyid, int posterID, int guildRank) {
            MapleGuild g = getGuild(guildid);
            if (g != null) {
                g.deleteBBSReply(localthreadid, replyid, posterID, guildRank);
            }
        }

        public static void changeEmblem(int gid, int affectedPlayers, MapleGuild mgs) {
            World.Broadcast.sendGuildPacket(affectedPlayers, GuildPacket.guildEmblemChange(gid, (short) mgs.getLogoBG(), (byte) mgs.getLogoBGColor(), (short) mgs.getLogo(), (byte) mgs.getLogoColor()), -1, gid);
            setGuildAndRank(affectedPlayers, -1, -1, -1, -1);
        }

        public static void setGuildAndRank(int cid, int guildid, int rank, int contribution, int alliancerank) {
            int ch = World.Find.findChannel(cid);
            if (ch == -1) {
                return;
            }
            MapleCharacter mc = World.getStorage(ch).getCharacterById(cid);
            if (mc == null) {
                return;
            }
            boolean bDifferentGuild;
            if ((guildid == -1) && (rank == -1)) {
                bDifferentGuild = true;
            } else {
                bDifferentGuild = guildid != mc.getGuildId();
                mc.setGuildId(guildid);
                mc.setGuildRank((byte) rank);
                mc.setGuildContribution(contribution);
                mc.setAllianceRank((byte) alliancerank);
                mc.saveGuildStatus();
            }
            if ((bDifferentGuild) && (ch > 0)) {
                mc.getMap().broadcastMessage(mc, GuildPacket.loadGuildName(mc), false);
                mc.getMap().broadcastMessage(mc, GuildPacket.loadGuildIcon(mc), false);
            }
        }
    }

    public static class Sidekick {

        private static final Map<Integer, MapleSidekick> sides = new LinkedHashMap();
        private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

        public static void addLoadedSidekick(MapleSidekick f) {
            if (f.getId() >= 0) {
                sides.put(Integer.valueOf(f.getId()), f);
            }
        }

        public static int createSidekick(int leaderId, int leaderId2) {
            return MapleSidekick.create(leaderId, leaderId2);
        }

        public static void eraseSidekick(int id) {
            lock.writeLock().lock();
            try {
                MapleSidekick ms = (MapleSidekick) sides.remove(Integer.valueOf(id));
                if (ms != null) {
                    erasePlayer(ms.getCharacter(0).getId());
                    erasePlayer(ms.getCharacter(1).getId());
                }
            } finally {
                lock.writeLock().unlock();
            }
        }

        public static void erasePlayer(int targetId) {
            int ch = World.Find.findChannel(targetId);
            if (ch < 0) {
                return;
            }
            MapleCharacter c = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterById(targetId);
            if (c != null) {
                c.setSidekick(null);
            }
        }

        public static MapleSidekick getSidekick(int id) {
            MapleSidekick ret = null;
            lock.readLock().lock();
            try {
                ret = (MapleSidekick) sides.get(Integer.valueOf(id));
            } finally {
                lock.readLock().unlock();
            }
            if (ret == null) {
                lock.writeLock().lock();
                try {
                    ret = new MapleSidekick(id);
                    if ((ret == null) || (ret.getId() < 0)) {
                        return null;
                    }
                    sides.put(Integer.valueOf(id), ret);
                } finally {
                    lock.writeLock().unlock();
                }
            }
            return ret;
        }

        public static MapleSidekick getSidekickByChr(int id) {
            lock.readLock().lock();
            try {
                for (MapleSidekick r : sides.values()) {
                    if ((r.getCharacter(0).getId() == id) || (r.getCharacter(1).getId() == id)) {
                        MapleSidekick localMapleSidekick1 = r;
                        return localMapleSidekick1;
                    }
                }
            } finally {
                lock.readLock().unlock();
            }
            return null;
        }

        static {
            for (MapleSidekick s : MapleSidekick.loadAll()) {
                addLoadedSidekick(s);
            }
        }
    }

    public static class Messenger {

        private static Map<Integer, MapleMessenger> messengers = new HashMap();
        private static final AtomicInteger runningMessengerId = new AtomicInteger();

        public static MapleMessenger createMessenger(MapleMessengerCharacter chrfor) {
            int messengerid = runningMessengerId.getAndIncrement();
            MapleMessenger messenger = new MapleMessenger(messengerid, chrfor);
            messengers.put(Integer.valueOf(messenger.getId()), messenger);
            return messenger;
        }

        public static void declineChat(String target, String namefrom) {
            int ch = World.Find.findChannel(target);
            if (ch > 0) {
                ChannelServer cs = ChannelServer.getInstance(ch);
                MapleCharacter chr = cs.getPlayerStorage().getCharacterByName(target);
                if (chr != null) {
                    MapleMessenger messenger = chr.getMessenger();
                    if (messenger != null) {
                        chr.getClient().getSession().write(MaplePacketCreator.messengerNote(namefrom, 5, 0));
                    }
                }
            }
        }

        public static MapleMessenger getMessenger(int messengerid) {
            return (MapleMessenger) messengers.get(Integer.valueOf(messengerid));
        }

        public static void leaveMessenger(int messengerid, MapleMessengerCharacter target) {
            MapleMessenger messenger = getMessenger(messengerid);
            if (messenger == null) {
                throw new IllegalArgumentException("No messenger with the specified messengerid exists");
            }
            int position = messenger.getPositionByName(target.getName());
            messenger.removeMember(target);

            for (MapleMessengerCharacter mmc : messenger.getMembers()) {
                if (mmc != null) {
                    int ch = World.Find.findChannel(mmc.getId());
                    if (ch > 0) {
                        MapleCharacter chr = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(mmc.getName());
                        if (chr != null) {
                            chr.getClient().getSession().write(MaplePacketCreator.removeMessengerPlayer(position));
                        }
                    }
                }
            }
        }

        public static void silentLeaveMessenger(int messengerid, MapleMessengerCharacter target) {
            MapleMessenger messenger = getMessenger(messengerid);
            if (messenger == null) {
                throw new IllegalArgumentException("No messenger with the specified messengerid exists");
            }
            messenger.silentRemoveMember(target);
        }

        public static void silentJoinMessenger(int messengerid, MapleMessengerCharacter target) {
            MapleMessenger messenger = getMessenger(messengerid);
            if (messenger == null) {
                throw new IllegalArgumentException("No messenger with the specified messengerid exists");
            }
            messenger.silentAddMember(target);
        }

        public static void updateMessenger(int messengerid, String namefrom, int fromchannel) {
            MapleMessenger messenger = getMessenger(messengerid);
            int position = messenger.getPositionByName(namefrom);

            for (MapleMessengerCharacter messengerchar : messenger.getMembers()) {
                if ((messengerchar != null) && (!messengerchar.getName().equals(namefrom))) {
                    int ch = World.Find.findChannel(messengerchar.getName());
                    if (ch > 0) {
                        MapleCharacter chr = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(messengerchar.getName());
                        if (chr != null) {
                            MapleCharacter from = ChannelServer.getInstance(fromchannel).getPlayerStorage().getCharacterByName(namefrom);
                            chr.getClient().getSession().write(MaplePacketCreator.updateMessengerPlayer(namefrom, from, position, fromchannel - 1));
                        }
                    }
                }
            }
        }

        public static void joinMessenger(int messengerid, MapleMessengerCharacter target, String from, int fromchannel) {
            MapleMessenger messenger = getMessenger(messengerid);
            if (messenger == null) {
                throw new IllegalArgumentException("No messenger with the specified messengerid exists");
            }
            messenger.addMember(target);
            int position = messenger.getPositionByName(target.getName());
            for (MapleMessengerCharacter messengerchar : messenger.getMembers()) {
                if (messengerchar != null) {
                    int mposition = messenger.getPositionByName(messengerchar.getName());
                    int ch = World.Find.findChannel(messengerchar.getName());
                    if (ch > 0) {
                        MapleCharacter chr = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(messengerchar.getName());
                        if (chr != null) {
                            if (!messengerchar.getName().equals(from)) {
                                MapleCharacter fromCh = ChannelServer.getInstance(fromchannel).getPlayerStorage().getCharacterByName(from);
                                if (fromCh != null) {
                                    chr.getClient().getSession().write(MaplePacketCreator.addMessengerPlayer(from, fromCh, position, fromchannel - 1));
                                    fromCh.getClient().getSession().write(MaplePacketCreator.addMessengerPlayer(chr.getName(), chr, mposition, messengerchar.getChannel() - 1));
                                }
                            } else {
                                chr.getClient().getSession().write(MaplePacketCreator.joinMessenger(mposition));
                            }
                        }
                    }
                }
            }
        }

        public static void messengerChat(int messengerid, String chattext, String namefrom) {
            MapleMessenger messenger = getMessenger(messengerid);
            if (messenger == null) {
                throw new IllegalArgumentException("No messenger with the specified messengerid exists");
            }
            for (MapleMessengerCharacter messengerchar : messenger.getMembers()) {
                if ((messengerchar != null) && (!messengerchar.getName().equals(namefrom))) {
                    int ch = World.Find.findChannel(messengerchar.getName());
                    if (ch > 0) {
                        MapleCharacter chr = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(messengerchar.getName());
                        if (chr != null) {
                            chr.getClient().getSession().write(MaplePacketCreator.messengerChat(chattext));
                        }
                    }
                }
            }
        }

        public static void messengerInvite(String sender, int messengerid, String target, int fromchannel, boolean gm) {
            if (World.isConnected(target)) {
                int ch = World.Find.findChannel(target);
                if (ch > 0) {
                    MapleCharacter from = ChannelServer.getInstance(fromchannel).getPlayerStorage().getCharacterByName(sender);
                    MapleCharacter targeter = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(target);
                    if ((targeter != null) && (targeter.getMessenger() == null)) {
                        if ((!targeter.isIntern()) || (gm)) {
                            targeter.getClient().getSession().write(MaplePacketCreator.messengerInvite(sender, messengerid));
                            from.getClient().getSession().write(MaplePacketCreator.messengerNote(target, 4, 1));
                        } else {
                            from.getClient().getSession().write(MaplePacketCreator.messengerNote(target, 4, 0));
                        }
                    } else {
                        from.getClient().getSession().write(MaplePacketCreator.messengerChat(sender + " : " + target + " 正在做别的事情，暂时无法邀请."));
                    }
                }
            }
        }

        static {
            runningMessengerId.set(1);
        }
    }

    public static class Buddy {

        public static void buddyChat(int[] recipientCharacterIds, int cidFrom, String nameFrom, String chattext) {
            for (int characterId : recipientCharacterIds) {
                int ch = World.Find.findChannel(characterId);
                if (ch > 0) {
                    MapleCharacter chr = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterById(characterId);
                    if ((chr != null) && (chr.getBuddylist().containsVisible(cidFrom))) {
                        chr.getClient().getSession().write(MaplePacketCreator.multiChat(nameFrom, chattext, 0));
                        if (chr.getClient().isMonitored()) {
                            World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM Message] " + nameFrom + " said to " + chr.getName() + " (Buddy): " + chattext));
                        }
                    }
                }
            }
        }

        private static void updateBuddies(int characterId, int channel, int[] buddies, boolean offline) {
            for (int buddy : buddies) {
                int ch = World.Find.findChannel(buddy);
                if (ch > 0) {
                    MapleCharacter chr = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterById(buddy);
                    if (chr != null) {
                        BuddylistEntry ble = chr.getBuddylist().get(characterId);
                        if ((ble == null) || (!ble.isVisible())) {
                            continue;
                        }
                        int mcChannel;
                        if (offline) {
                            ble.setChannel(-1);
                            mcChannel = -1;
                        } else {
                            ble.setChannel(channel);
                            mcChannel = channel - 1;
                        }
                        chr.getClient().getSession().write(BuddyListPacket.updateBuddyChannel(ble.getCharacterId(), mcChannel));
                    }
                }
            }
        }

        public static void buddyChanged(int cid, int cidFrom, String name, int channel, BuddyOperation operation, String group) {
            int ch = World.Find.findChannel(cid);
            if (ch > 0) {
                MapleCharacter addChar = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterById(cid);
                if (addChar != null) {
                    BuddyList buddylist = addChar.getBuddylist();
                    switch (operation) {
                        case 添加好友:
                            if (!buddylist.contains(cidFrom)) {
                                break;
                            }
                            buddylist.put(new BuddylistEntry(name, cidFrom, group, channel, true));
                            addChar.getClient().getSession().write(BuddyListPacket.updateBuddyChannel(cidFrom, channel - 1));
                            break;
                        case 删除好友:
                            if (!buddylist.contains(cidFrom)) {
                                break;
                            }
                            buddylist.put(new BuddylistEntry(name, cidFrom, group, -1, buddylist.get(cidFrom).isVisible()));
                            addChar.getClient().getSession().write(BuddyListPacket.updateBuddyChannel(cidFrom, -1));
                    }
                }
            }
        }

        public static BuddyList.BuddyAddResult requestBuddyAdd(String addName, int channelFrom, int cidFrom, String nameFrom, int levelFrom, int jobFrom) {
            int ch = World.Find.findChannel(cidFrom);
            if (ch > 0) {
                MapleCharacter addChar = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(addName);
                if (addChar != null) {
                    BuddyList buddylist = addChar.getBuddylist();
                    if (buddylist.isFull()) {
                        return BuddyList.BuddyAddResult.BUDDYLIST_FULL;
                    }
                    if (!buddylist.contains(cidFrom)) {
                        buddylist.addBuddyRequest(addChar.getClient(), cidFrom, nameFrom, channelFrom, levelFrom, jobFrom);
                    } else if (buddylist.containsVisible(cidFrom)) {
                        return BuddyList.BuddyAddResult.ALREADY_ON_LIST;
                    }
                }
            }

            return BuddyList.BuddyAddResult.OK;
        }

        public static void loggedOn(String name, int characterId, int channel, int[] buddies) {
            updateBuddies(characterId, channel, buddies, false);
        }

        public static void loggedOff(String name, int characterId, int channel, int[] buddies) {
            updateBuddies(characterId, channel, buddies, true);
        }
    }

    public static class Party {

        private static Map<Integer, MapleParty> parties = new HashMap();
        private static Map<Integer, MapleExpedition> expeds = new HashMap();
        private static final Map<PartySearchType, List<PartySearch>> searches = new EnumMap(PartySearchType.class);
        private static final AtomicInteger runningPartyId = new AtomicInteger(1);
        private static final AtomicInteger runningExpedId = new AtomicInteger(1);

        public static void partyChat(int partyid, String chattext, String namefrom) {
            partyChat(partyid, chattext, namefrom, 1);
        }

        public static void expedChat(int expedId, String chattext, String namefrom) {
            MapleExpedition party = getExped(expedId);
            if (party == null) {
                return;
            }
            for (Iterator i$ = party.getParties().iterator(); i$.hasNext();) {
                int i = ((Integer) i$.next()).intValue();
                partyChat(i, chattext, namefrom, 4);
            }
        }

        public static void expedPacket(int expedId, byte[] packet, MaplePartyCharacter exception) {
            MapleExpedition party = getExped(expedId);
            if (party == null) {
                return;
            }
            for (Iterator i$ = party.getParties().iterator(); i$.hasNext();) {
                int i = ((Integer) i$.next()).intValue();
                partyPacket(i, packet, exception);
            }
        }

        public static void partyPacket(int partyid, byte[] packet, MaplePartyCharacter exception) {
            MapleParty party = getParty(partyid);
            if (party == null) {
                return;
            }
            for (MaplePartyCharacter partychar : party.getMembers()) {
                int ch = World.Find.findChannel(partychar.getName());
                if ((ch > 0) && ((exception == null) || (partychar.getId() != exception.getId()))) {
                    MapleCharacter chr = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(partychar.getName());
                    if (chr != null) {
                        chr.getClient().getSession().write(packet);
                    }
                }
            }
        }

        public static void partyChat(int partyid, String chattext, String namefrom, int mode) {
            MapleParty party = getParty(partyid);
            if (party == null) {
                return;
            }
            for (MaplePartyCharacter partychar : party.getMembers()) {
                int ch = World.Find.findChannel(partychar.getName());
                if (ch > 0) {
                    MapleCharacter chr = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(partychar.getName());
                    if ((chr != null) && (!chr.getName().equalsIgnoreCase(namefrom))) {
                        chr.getClient().getSession().write(MaplePacketCreator.multiChat(namefrom, chattext, mode));
                        if (chr.getClient().isMonitored()) {
                            World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM Message] " + namefrom + " said to " + chr.getName() + " (Party): " + chattext));
                        }
                    }
                }
            }
        }

        public static void partyMessage(int partyid, String chattext) {
            MapleParty party = getParty(partyid);
            if (party == null) {
                return;
            }
            for (MaplePartyCharacter partychar : party.getMembers()) {
                int ch = World.Find.findChannel(partychar.getName());
                if (ch > 0) {
                    MapleCharacter chr = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(partychar.getName());
                    if (chr != null) {
                        chr.dropMessage(5, chattext);
                    }
                }
            }
        }

        public static void expedMessage(int expedId, String chattext) {
            MapleExpedition party = getExped(expedId);
            if (party == null) {
                return;
            }
            for (Iterator i$ = party.getParties().iterator(); i$.hasNext();) {
                int i = ((Integer) i$.next()).intValue();
                partyMessage(i, chattext);
            }
        }

        public static void updateParty(int partyid, PartyOperation operation, MaplePartyCharacter target) {
            MapleParty party = getParty(partyid);
            if (party == null) {
                return;
            }

            int oldExped = party.getExpeditionId();
            int oldInd = -1;
            if (oldExped > 0) {
                MapleExpedition exped = getExped(oldExped);
                if (exped != null) {
                    oldInd = exped.getIndex(partyid);
                }
            }
            switch (operation) {
                case 加入队伍:
                    party.addMember(target);
                    if (party.getMembers().size() < 6) {
                        break;
                    }
                    PartySearch toRemove = getSearchByParty(partyid);
                    if (toRemove != null) {
                        removeSearch(toRemove, "该队伍人数已满，无法加入。");
                    } else if (party.getExpeditionId() > 0) {
                        MapleExpedition exped = getExped(party.getExpeditionId());
                        if ((exped != null) && (exped.getAllMembers() >= exped.getType().maxMembers)) {
                            toRemove = getSearchByExped(exped.getId());
                            if (toRemove != null) {
                                removeSearch(toRemove, "该远征队伍人数已满，无法加入。");
                            }
                        }
                    }
                    break;
                case 驱逐成员:
                case 离开队伍:
                    party.removeMember(target);
                    break;
                case 解散队伍:
                    disbandParty(partyid);
                    break;
                case 更新队伍:
                case LOG_ONOFF:
                    party.updateMember(target);
                    break;
                case 改变队长:
                case CHANGE_LEADER_DC:
                    party.setLeader(target);
                    break;
                default:
                    throw new RuntimeException("Unhandeled updateParty operation " + operation.name());
            }
            if ((operation == PartyOperation.离开队伍) || (operation == PartyOperation.驱逐成员)) {
                int chz = World.Find.findChannel(target.getName());
                if (chz > 0) {
                    MapleCharacter chr = World.getStorage(chz).getCharacterByName(target.getName());
                    if (chr != null) {
                        chr.setParty(null);
                        if (oldExped > 0) {
                            if (chr.isGM());
                            chr.getClient().getSession().write(PartyPacket.expeditionMessage(81));
                        }

                        if (chr.isGM());
                        chr.getClient().getSession().write(PartyPacket.updateParty(chr.getClient().getChannel(), party, operation, target));
                    }
                }
                if ((target.getId() == party.getLeader().getId()) && (party.getMembers().size() > 0)) {
                    MaplePartyCharacter lchr = null;
                    for (MaplePartyCharacter pchr : party.getMembers()) {
                        if ((pchr != null) && ((lchr == null) || (lchr.getLevel() < pchr.getLevel()))) {
                            lchr = pchr;
                        }
                    }
                    if (lchr != null) {
                        updateParty(partyid, PartyOperation.CHANGE_LEADER_DC, lchr);
                    }
                }
            }
            if (party.getMembers().size() <= 0) {
                disbandParty(partyid);
            }
            for (MaplePartyCharacter partychar : party.getMembers()) {
                if (partychar == null) {
                    continue;
                }
                int ch = World.Find.findChannel(partychar.getName());
                if (ch > 0) {
                    MapleCharacter chr = World.getStorage(ch).getCharacterByName(partychar.getName());
                    if (chr != null) {
                        if (operation == PartyOperation.解散队伍) {
                            chr.setParty(null);
                            if (oldExped > 0) {
                                if (chr.isGM());
                                chr.getClient().getSession().write(PartyPacket.expeditionMessage(82));
                            }
                        } else {
                            if (chr.isGM());
                            chr.setParty(party);
                        }

                        if (chr.isGM());
                        chr.getClient().getSession().write(PartyPacket.updateParty(chr.getClient().getChannel(), party, operation, target));
                    }
                }
            }
            if (oldExped > 0) {
                expedPacket(oldExped, PartyPacket.expeditionUpdate(oldInd, party), (operation == PartyOperation.LOG_ONOFF) || (operation == PartyOperation.更新队伍) ? target : null);
            }
        }

        public static MapleParty createParty(MaplePartyCharacter chrfor) {
            MapleParty party = new MapleParty(runningPartyId.getAndIncrement(), chrfor);
            parties.put(Integer.valueOf(party.getId()), party);
            return party;
        }

        public static MapleParty createParty(MaplePartyCharacter chrfor, int expedId) {
            ExpeditionType ex = ExpeditionType.getById(expedId);
            MapleParty party = new MapleParty(runningPartyId.getAndIncrement(), chrfor, ex != null ? runningExpedId.getAndIncrement() : -1);
            parties.put(Integer.valueOf(party.getId()), party);
            if (ex != null) {
                MapleExpedition exp = new MapleExpedition(ex, chrfor.getId(), party.getExpeditionId());
                exp.getParties().add(Integer.valueOf(party.getId()));
                expeds.put(Integer.valueOf(party.getExpeditionId()), exp);
            }
            return party;
        }

        public static MapleParty createPartyAndAdd(MaplePartyCharacter chrfor, int expedId) {
            MapleExpedition ex = getExped(expedId);
            if (ex == null) {
                return null;
            }
            MapleParty party = new MapleParty(runningPartyId.getAndIncrement(), chrfor, expedId);
            parties.put(Integer.valueOf(party.getId()), party);
            ex.getParties().add(Integer.valueOf(party.getId()));
            return party;
        }

        public static MapleParty getParty(int partyid) {
            return (MapleParty) parties.get(Integer.valueOf(partyid));
        }

        public static MapleExpedition getExped(int partyid) {
            return (MapleExpedition) expeds.get(Integer.valueOf(partyid));
        }

        public static MapleExpedition disbandExped(int partyid) {
            PartySearch toRemove = getSearchByExped(partyid);
            if (toRemove != null) {
                removeSearch(toRemove, "The Party Listing was removed because the party disbanded.");
            }
            MapleExpedition ret = (MapleExpedition) expeds.remove(Integer.valueOf(partyid));
            Iterator i$;
            if (ret != null) {
                for (i$ = ret.getParties().iterator(); i$.hasNext();) {
                    int p = ((Integer) i$.next()).intValue();
                    MapleParty pp = getParty(p);
                    if (pp != null) {
                        updateParty(p, PartyOperation.解散队伍, pp.getLeader());
                    }
                }
            }
            return ret;
        }

        public static MapleParty disbandParty(int partyid) {
            PartySearch toRemove = getSearchByParty(partyid);
            if (toRemove != null) {
                removeSearch(toRemove, "The Party Listing was removed because the party disbanded.");
            }
            MapleParty ret = (MapleParty) parties.remove(Integer.valueOf(partyid));
            if (ret == null) {
                return null;
            }
            if (ret.getExpeditionId() > 0) {
                MapleExpedition me = getExped(ret.getExpeditionId());
                if (me != null) {
                    int ind = me.getIndex(partyid);
                    if (ind >= 0) {
                        me.getParties().remove(ind);

                        expedPacket(me.getId(), PartyPacket.expeditionUpdate(ind, null), null);
                    }
                }
            }
            ret.disband();
            return ret;
        }

        public static List<PartySearch> searchParty(PartySearchType pst) {
            return (List) searches.get(pst);
        }

        public static void removeSearch(PartySearch ps, String text) {
            List ss = (List) searches.get(ps.getType());
            if (ss.contains(ps)) {
                ss.remove(ps);
                ps.cancelRemoval();
                if (ps.getType().exped) {
                    expedMessage(ps.getId(), text);
                } else {
                    partyMessage(ps.getId(), text);
                }
            }
        }

        public static void addSearch(PartySearch ps) {
            ((List) searches.get(ps.getType())).add(ps);
        }

        public static PartySearch getSearch(MapleParty party) {
            for (List<PartySearch> ps : searches.values()) {
                for (PartySearch p : ps) {
                    if (((p.getId() == party.getId()) && (!p.getType().exped)) || ((p.getId() == party.getExpeditionId()) && (p.getType().exped))) {
                        return p;
                    }
                }
            }
            return null;
        }

        public static PartySearch getSearchByParty(int partyId) {
            for (List<PartySearch> ps : searches.values()) {
                for (PartySearch p : ps) {
                    if ((p.getId() == partyId) && (!p.getType().exped)) {
                        return p;
                    }
                }
            }
            return null;
        }

        public static PartySearch getSearchByExped(int partyId) {
            for (List<PartySearch> ps : searches.values()) {
                for (PartySearch p : ps) {
                    if ((p.getId() == partyId) && (p.getType().exped)) {
                        return p;
                    }
                }
            }
            return null;
        }

        public static boolean partyListed(MapleParty party) {
            return getSearchByParty(party.getId()) != null;
        }

        static {
            try {
                PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE characters SET party = -1, fatigue = 0");
                ps.executeUpdate();
                ps.close();
            } catch (SQLException e) {
                System.out.println("更新角色组队为-1失败...");
            }
            for (PartySearchType pst : PartySearchType.values()) {
                searches.put(pst, new ArrayList());
            }
        }
    }
}