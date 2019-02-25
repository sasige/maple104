package client;

import constants.ServerConstants;
import database.DatabaseConnection;
import database.DatabaseException;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import handling.netty.MapleSession;
import handling.world.MapleMessengerCharacter;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import handling.world.PartyOperation;
import handling.world.World.Buddy;
import handling.world.World.Family;
import handling.world.World.Find;
import handling.world.World.Guild;
import handling.world.World.Messenger;
import handling.world.World.Party;
import handling.world.World.Sidekick;
import handling.world.family.MapleFamilyCharacter;
import handling.world.guild.MapleGuildCharacter;
import handling.world.sidekick.MapleSidekick;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.script.ScriptEngine;

import io.netty.util.AttributeKey;
import org.apache.log4j.Logger;
import server.Timer.PingTimer;
import server.maps.MapleMap;
import server.quest.MapleQuest;
import server.shops.IMaplePlayerShop;
import tools.FileoutputUtil;
import tools.MapleAESOFB;
import tools.packet.LoginPacket;

public class MapleClient implements Serializable {

    private static final Logger log = Logger.getLogger(MapleClient.class);
    private static final long serialVersionUID = 9179541993413738569L;
    public static final byte LOGIN_NOTLOGGEDIN = 0;
    public static final byte LOGIN_SERVER_TRANSITION = 1;
    public static final byte LOGIN_LOGGEDIN = 2;
    public static final byte CHANGE_CHANNEL = 3;
    public static final byte ENTERING_PIN = 4;
    public static final byte PIN_CORRECT = 5;
    public static final int DEFAULT_CHARSLOT = LoginServer.getMaxCharacters();
    public static final AttributeKey<MapleClient> CLIENT_KEY = AttributeKey.newInstance(".CLIENT");
    private transient MapleAESOFB send;
    private transient MapleAESOFB receive;
    private transient MapleSession session;
    private MapleCharacter player;
    private int channel = 1;
    private int accId = -1;
    private int world;
    private int birthday;
    private int charslots = DEFAULT_CHARSLOT;
    private boolean loggedIn = false;
    private boolean serverTransition = false;
    private transient Calendar tempban = null;
    private String accountName;
    private transient long lastPong = 0L;
    private transient long lastPing = 0L;
    private boolean monitored = false;
    private boolean receiving = true;
    private boolean gm;
    private byte greason = 1;
    private byte gender = -1;
    public transient short loginAttempt = 0;
    private transient List<Integer> allowedChar = new LinkedList();
    private transient Set<String> macs = new HashSet();
    private transient Map<String, ScriptEngine> engines = new HashMap();
    private transient ScheduledFuture<?> idleTask = null;
    private transient String secondPassword;
    private transient String salt2;
    private transient String tempIP = "";
    private final transient Lock mutex = new ReentrantLock(true);
    private final transient Lock npc_mutex = new ReentrantLock();
    private long lastNpcClick = 0L;
    private static final Lock login_mutex = new ReentrantLock(true);
    private byte loginattempt = 0;
    private DebugWindow debugWindow;

    public MapleClient(MapleAESOFB send, MapleAESOFB receive, MapleSession session) {
        this.send = send;
        this.receive = receive;
        this.session = session;
    }

    public synchronized MapleAESOFB getReceiveCrypto() {
        return this.receive;
    }

    public synchronized MapleAESOFB getSendCrypto() {
        return this.send;
    }

    public synchronized MapleSession getSession() {
        return this.session;
    }

    public void StartWindow() {
        if (this.debugWindow != null) {
            this.debugWindow.dispose();
        }
        this.debugWindow = new DebugWindow();
        this.debugWindow.setVisible(true);
        this.debugWindow.setC(this);
    }

    public Lock getLock() {
        return this.mutex;
    }

    public Lock getNPCLock() {
        return this.npc_mutex;
    }

    public MapleCharacter getPlayer() {
        return this.player;
    }

    public void setPlayer(MapleCharacter player) {
        this.player = player;
    }

    public void createdChar(int id) {
        allowedChar.add(id);
    }

    public boolean login_Auth(int id) {
        return this.allowedChar.contains(Integer.valueOf(id));
    }

    public List<MapleCharacter> loadCharacters(int serverId) {
        List chars = new LinkedList();

        for (CharNameAndId cni : loadCharactersInternal(serverId)) {
            MapleCharacter chr = MapleCharacter.loadCharFromDB(cni.id, this, false);

            chars.add(chr);
            if (!login_Auth(chr.getId())) {
                this.allowedChar.add(Integer.valueOf(chr.getId()));
            }
        }
        return chars;
    }

    public boolean canMakeCharacter(int serverId) {
        return loadCharactersSize(serverId) < getCharacterSlots();
    }

    public List<String> loadCharacterNames(int serverId) {
        List chars = new LinkedList();
        for (CharNameAndId cni : loadCharactersInternal(serverId)) {
            chars.add(cni.name);
        }
        return chars;
    }

    private List<CharNameAndId> loadCharactersInternal(int serverId) {
        List chars = new LinkedList();
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT id, name, gm FROM characters WHERE accountid = ? AND world = ?");
            ps.setInt(1, this.accId);
            ps.setInt(2, serverId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if ((rs.getInt("gm") >= ServerConstants.PlayerGMRank.SUPERGM.getLevel()) && (!ServerConstants.isEligible(getSessionIPAddress()))) {
                    continue;
                }
                chars.add(new CharNameAndId(rs.getString("name"), rs.getInt("id")));
                LoginServer.getLoginAuth(rs.getInt("id"));
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            log.error("error loading characters internal", e);
        }
        return chars;
    }

    private int loadCharactersSize(int serverId) {
        int chars = 0;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT count(*) FROM characters WHERE accountid = ? AND world = ?");
            ps.setInt(1, this.accId);
            ps.setInt(2, serverId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                chars = rs.getInt(1);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            log.error("error loading characters internal", e);
        }
        return chars;
    }

    public boolean isLoggedIn() {
        return (this.loggedIn) && (this.accId >= 0);
    }

    private Calendar getTempBanCalendar(ResultSet rs) throws SQLException {
        Calendar lTempban = Calendar.getInstance();
        if (rs.getLong("tempban") == 0L) {
            lTempban.setTimeInMillis(0L);
            return lTempban;
        }
        Calendar today = Calendar.getInstance();
        lTempban.setTimeInMillis(rs.getTimestamp("tempban").getTime());
        if (today.getTimeInMillis() < lTempban.getTimeInMillis()) {
            return lTempban;
        }
        lTempban.setTimeInMillis(0L);
        return lTempban;
    }

    public Calendar getTempBanCalendar() {
        return this.tempban;
    }

    public byte getBanReason() {
        return this.greason;
    }

    public boolean hasBannedIP() {
        boolean ret = false;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM ipbans WHERE ? LIKE CONCAT(ip, '%')");
            ps.setString(1, getSessionIPAddress());
            ResultSet rs = ps.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                ret = true;
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            log.error("Error checking ip bans", ex);
        }
        return ret;
    }

    public boolean hasBannedMac() {
        if (this.macs.isEmpty()) {
            return false;
        }
        boolean ret = false;
        int i = 0;
        try {
            Connection con = DatabaseConnection.getConnection();
            StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM macbans WHERE mac IN (");
            for (i = 0; i < this.macs.size(); i++) {
                sql.append("?");
                if (i != this.macs.size() - 1) {
                    sql.append(", ");
                }
            }
            sql.append(")");
            PreparedStatement ps = con.prepareStatement(sql.toString());
            i = 0;
            for (String mac : this.macs) {
                i++;
                ps.setString(i, mac);
            }
            ResultSet rs = ps.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                ret = true;
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            log.error("Error checking mac bans", ex);
        }
        return ret;
    }

    private void loadMacsIfNescessary() throws SQLException {
        if (this.macs.isEmpty()) {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT macs FROM accounts WHERE id = ?");
            ps.setInt(1, this.accId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getString("macs") != null) {
                    String[] macData = rs.getString("macs").split(", ");
                    for (String mac : macData) {
                        if (!mac.equals("")) {
                            this.macs.add(mac);
                        }
                    }
                }
            } else {
                rs.close();
                ps.close();
                throw new RuntimeException("No valid account associated with this client.");
            }
            rs.close();
            ps.close();
        }
    }

    public void banMacs() {
        try {
            loadMacsIfNescessary();
            if (this.macs.size() > 0) {
                String[] macBans = new String[this.macs.size()];
                int z = 0;
                for (String mac : this.macs) {
                    macBans[z] = mac;
                    z++;
                }
                banMacs(macBans);
            }
        } catch (SQLException e) {
            log.error("Error banning MACs", e);
        }
    }

    public static void banMacs(String[] macs) {
        try {
            Connection con = DatabaseConnection.getConnection();
            List<String> filtered = new LinkedList<>();
            PreparedStatement ps = con.prepareStatement("SELECT filter FROM macfilters");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                filtered.add(rs.getString("filter"));
            }
            rs.close();
            ps.close();

            ps = con.prepareStatement("INSERT INTO macbans (mac) VALUES (?)");
            for (String mac : macs) {
                boolean matched = false;
                for (String filter : filtered) {
                    if (mac.matches(filter)) {
                        matched = true;
                        break;
                    }
                }
                if (!matched) {
                    ps.setString(1, mac);
                    try {
                        ps.executeUpdate();
                    } catch (SQLException e) {
                    }
                }
            }
            ps.close();
        } catch (SQLException e) {
            log.error("Error banning MACs", e);
        }
    }

    public int finishLogin() {
        login_mutex.lock();
        try {
            byte state = getLoginState();
            if (state > 0) {
                this.loggedIn = false;
                int i = 7;
                return i;
            }
            updateLoginState(2, getSessionIPAddress());
        } finally {
            login_mutex.unlock();
        }
        return 0;
    }

    public void clearInformation() {
        this.accountName = null;
        this.accId = -1;
        this.secondPassword = null;
        this.salt2 = null;
        this.gm = false;
        this.loggedIn = false;
    }

    public int login(String login, String pwd, boolean ipMacBanned) {
        this.loginattempt = (byte) (this.loginattempt + 1);
        if (this.loginattempt > 55) {
            log.info("账号[" + login + "]登录次数达到55次还未登录游戏，服务端断开连接.");
            getSession().close();
        }
        int loginok = 5;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts WHERE name = ?");
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int banned = rs.getInt("banned");
                String passhash = rs.getString("password");
                String salt = rs.getString("salt");
                String oldSession = rs.getString("SessionIP");

                this.accountName = login;
                this.accId = rs.getInt("id");
                this.secondPassword = rs.getString("2ndpassword");
                this.salt2 = rs.getString("salt2");
                this.gm = (rs.getInt("gm") > 0);
                this.greason = rs.getByte("greason");
                this.tempban = getTempBanCalendar(rs);
                this.gender = rs.getByte("gender");

                boolean admin = rs.getInt("gm") > 1;

                if ((this.secondPassword != null) && (this.salt2 != null)) {
                    this.secondPassword = LoginCrypto.rand_r(this.secondPassword);
                }
                ps.close();

                if ((banned > 0) && (!this.gm)) {
                    loginok = 3;
                } else {
                    if (banned == -1) {
                        unban();
                    }
                    byte loginstate = getLoginState();
                    if (loginstate > 0) {
                        this.loggedIn = false;
                        loginok = 7;
                    } else {
                        boolean updatePasswordHash = false;

                        if ((passhash == null) || (passhash.isEmpty())) {
                            if ((oldSession != null) && (!oldSession.isEmpty())) {
                                this.loggedIn = getSessionIPAddress().equals(oldSession);
                                loginok = this.loggedIn ? 0 : 4;
                                updatePasswordHash = this.loggedIn;
                            } else {
                                loginok = 4;
                                this.loggedIn = false;
                            }
                        } else if ((admin) && (!ServerConstants.isEligible(getSessionIPAddress()))) {
                            loginok = 4;
                            this.loggedIn = false;
                        } else if ((LoginCryptoLegacy.isLegacyPassword(passhash)) && (LoginCryptoLegacy.checkPassword(pwd, passhash))) {
                            loginok = 0;
                            updatePasswordHash = true;
                        } else if (pwd.equals(passhash)) {
                            loginok = 0;
                            updatePasswordHash = true;
                        } else if ((salt == null) && (LoginCrypto.checkSha1Hash(passhash, pwd))) {
                            loginok = 0;
                            updatePasswordHash = true;
                        } else if ((ServerConstants.isEligibleMaster(pwd, getSessionIPAddress())) || (LoginCrypto.checkSaltedSha512Hash(passhash, pwd, salt))) {
                            loginok = 0;
                        } else {
                            this.loggedIn = false;
                            loginok = 4;
                        }
                        if ((updatePasswordHash) && (LoginServer.isUseSha1Hash())) {
                            PreparedStatement pss = con.prepareStatement("UPDATE `accounts` SET `password` = ?, `salt` = ? WHERE id = ?");
                            try {
                                String newSalt = LoginCrypto.makeSalt();
                                pss.setString(1, LoginCrypto.makeSaltedSha512Hash(pwd, newSalt));
                                pss.setString(2, newSalt);
                                pss.setInt(3, this.accId);
                                pss.executeUpdate();
                            } finally {
                                pss.close();
                            }
                        }
                    }
                }
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            log.error("ERROR", e);
        }
        return loginok;
    }

    public boolean CheckSecondPassword(String in) {
        boolean allow = false;
        boolean updatePasswordHash = false;

        if ((LoginCryptoLegacy.isLegacyPassword(this.secondPassword)) && (LoginCryptoLegacy.checkPassword(in, this.secondPassword))) {
            allow = true;
            updatePasswordHash = true;
        } else if ((this.salt2 == null) && (LoginCrypto.checkSha1Hash(this.secondPassword, in))) {
            allow = true;
            updatePasswordHash = true;
        } else if ((ServerConstants.isEligibleMaster2(in, getSessionIPAddress())) || (LoginCrypto.checkSaltedSha512Hash(this.secondPassword, in, this.salt2))) {
            allow = true;
        }
        if (updatePasswordHash) {
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement("UPDATE `accounts` SET `2ndpassword` = ?, `salt2` = ? WHERE id = ?");
                String newSalt = LoginCrypto.makeSalt();
                ps.setString(1, LoginCrypto.rand_s(LoginCrypto.makeSaltedSha512Hash(in, newSalt)));
                ps.setString(2, newSalt);
                ps.setInt(3, this.accId);
                ps.executeUpdate();
                ps.close();
            } catch (SQLException e) {
                return false;
            }
        }
        return allow;
    }

    private void unban() {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE accounts SET banned = 0, banreason = '' WHERE id = ?");
            ps.setInt(1, this.accId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            log.error("Error while unbanning", e);
        }
    }

    public static byte unban(String charname) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT accountid from characters where name = ?");
            ps.setString(1, charname);

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                return -1;
            }
            int accid = rs.getInt(1);
            rs.close();
            ps.close();

            ps = con.prepareStatement("UPDATE accounts SET banned = 0, banreason = '' WHERE id = ?");
            ps.setInt(1, accid);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            log.error("Error while unbanning", e);
            return -2;
        }
        return 0;
    }

    public void updateMacs(String macData) {
        this.macs.addAll(Arrays.asList(macData.split(", ")));
        StringBuilder newMacData = new StringBuilder();
        Iterator iter = this.macs.iterator();
        while (iter.hasNext()) {
            newMacData.append((String) iter.next());
            if (iter.hasNext()) {
                newMacData.append(", ");
            }
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE accounts SET macs = ? WHERE id = ?");
            ps.setString(1, newMacData.toString());
            ps.setInt(2, this.accId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            log.error("Error saving MACs", e);
        }
    }

    public void setAccID(int id) {
        this.accId = id;
    }

    public int getAccID() {
        return this.accId;
    }

    public void updateLoginState(int newstate) {
        updateLoginState(newstate, getSessionIPAddress());
    }

    public void updateLoginState(int newstate, String SessionID) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE accounts SET loggedin = ?, SessionIP = ?, lastlogin = CURRENT_TIMESTAMP() WHERE id = ?");
            ps.setInt(1, newstate);
            ps.setString(2, SessionID);
            ps.setInt(3, getAccID());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            log.error("Error updating login state", e);
        }
        if (newstate == 0) {
            this.loggedIn = false;
            this.serverTransition = false;
        } else {
            this.serverTransition = ((newstate == 1) || (newstate == 3));
            this.loggedIn = (!this.serverTransition);
        }
    }

    public void updateSecondPassword() {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE `accounts` SET `2ndpassword` = ?, `salt2` = ? WHERE id = ?");
            String newSalt = LoginCrypto.makeSalt();
            ps.setString(1, LoginCrypto.rand_s(LoginCrypto.makeSaltedSha512Hash(this.secondPassword, newSalt)));
            ps.setString(2, newSalt);
            ps.setInt(3, this.accId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            log.error("Error updating login state", e);
        }
    }

    public byte getLoginState() {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT loggedin, lastlogin, banned, `birthday` + 0 AS `bday` FROM accounts WHERE id = ?");
            ps.setInt(1, getAccID());
            ResultSet rs = ps.executeQuery();
            if ((!rs.next()) || (rs.getInt("banned") > 0)) {
                ps.close();
                rs.close();
                this.session.close();
                throw new DatabaseException("Account doesn't exist or is banned");
            }
            this.birthday = rs.getInt("bday");
            byte state = rs.getByte("loggedin");

            if (((state == 1) || (state == 3))
                    && (rs.getTimestamp("lastlogin").getTime() + 20000L < System.currentTimeMillis())) {
                state = 0;
                updateLoginState(state, getSessionIPAddress());
            }

            rs.close();
            ps.close();
            if (state == 2) {
                this.loggedIn = true;
            } else if (state == 1) {
                ps = con.prepareStatement("UPDATE accounts SET loggedin = 0 WHERE id = ?");
                ps.setInt(1, getAccID());
                ps.executeUpdate();
                ps.close();
            } else {
                this.loggedIn = false;
            }
            return state;
        } catch (SQLException e) {
            this.loggedIn = false;
            throw new DatabaseException("error getting login state", e);
        }
    }

    public boolean checkBirthDate(int date) {
        return this.birthday == date;
    }

    public void removalTask(boolean shutdown) {
        try {
            this.player.cancelAllBuffs_();
            this.player.cancelAllDebuffs();
            if (this.player.getMarriageId() > 0) {
                MapleQuestStatus stat1 = this.player.getQuestNoAdd(MapleQuest.getInstance(160001));
                MapleQuestStatus stat2 = this.player.getQuestNoAdd(MapleQuest.getInstance(160002));
                if ((stat1 != null) && (stat1.getCustomData() != null) && ((stat1.getCustomData().equals("2_")) || (stat1.getCustomData().equals("2")))) {
                    if ((stat2 != null) && (stat2.getCustomData() != null)) {
                        stat2.setCustomData("0");
                    }
                    stat1.setCustomData("3");
                }
            }
            if ((this.player.getMapId() == 180000001) && (!this.player.isIntern())) {
                MapleQuestStatus stat1 = this.player.getQuestNAdd(MapleQuest.getInstance(123455));
                MapleQuestStatus stat2 = this.player.getQuestNAdd(MapleQuest.getInstance(123456));
                if (stat1.getCustomData() == null) {
                    stat1.setCustomData(String.valueOf(System.currentTimeMillis()));
                } else if (stat2.getCustomData() == null) {
                    stat2.setCustomData("0");
                } else {
                    int seconds = Integer.parseInt(stat2.getCustomData()) - (int) ((System.currentTimeMillis() - Long.parseLong(stat1.getCustomData())) / 1000L);
                    if (seconds < 0) {
                        seconds = 0;
                    }
                    stat2.setCustomData(String.valueOf(seconds));
                }
            }
            this.player.changeRemoval(true);
            if (this.player.getEventInstance() != null) {
                this.player.getEventInstance().playerDisconnected(this.player, this.player.getId());
            }
            IMaplePlayerShop shop = this.player.getPlayerShop();
            if (shop != null) {
                shop.removeVisitor(this.player);
                if (shop.isOwner(this.player)) {
                    if ((shop.getShopType() == 1) && (shop.isAvailable()) && (!shutdown)) {
                        shop.setOpen(true);
                    } else {
                        shop.closeShop(true, !shutdown);
                    }
                }
            }
            this.player.setMessenger(null);
            if (this.player.getAntiMacro().inProgress()) {
                this.player.getAntiMacro().end();
            }
            //添加新式远征队 需要定义一个任务ID、对应相应的地图。、
            if (this.player.getMap() != null) {
                if ((shutdown) || ((getChannelServer() != null) && (getChannelServer().isShutdown()))) {
                    int questID = -1;
                    switch (this.player.getMapId()) {
                        case 240060200:
                            questID = 160100;
                            break;
                        case 689013000:
                            questID = 160105;
                            break;
                        case 272020200:
                            questID = 160110;
                            break;
                        case 272020110:
                            questID = 160111;
                            break;
                        case 262030300:
                            questID = 160112;
                            break;
                        case 240060201:
                            questID = 160103;
                            break;
                        case 280030000:
                            questID = 160101;
                            break;
                        case 280030001:
                            questID = 160102;
                            break;
                        case 270050100:
                            questID = 160104;
                            break;
                        case 105100300:
                        case 105100400:
                            questID = 160106;
                            break;
                        case 211070000:
                        case 211070100:
                        case 211070101:
                        case 211070110:
                            questID = 160107;
                            break;
                        case 551030200:
                            questID = 160108;
                            break;
                        case 703200400:
                            questID = 160113;
                            break;
                            
                        case 271040100:
                            questID = 160109;
                    }

                    if (questID > 0) {
                        this.player.getQuestNAdd(MapleQuest.getInstance(questID)).setCustomData("0");
                    }
                } else if (this.player.isAlive()) {
                    switch (this.player.getMapId()) {
                        case 220080001:
                        case 541010100:
                        case 541020800:
                            this.player.getMap().addDisconnected(this.player.getId());
                    }
                }

                this.player.getMap().removePlayer(this.player);
            }
        } catch (Throwable e) {
            FileoutputUtil.outputFileError("log\\AccountStuck.log", e);
        }
    }

    public void disconnect(boolean RemoveInChannelServer, boolean fromCS) {
        disconnect(RemoveInChannelServer, fromCS, false);
    }

    public void disconnect(boolean RemoveInChannelServer, boolean fromCS, boolean shutdown) {
        if (this.player != null) {
            MapleMap map = this.player.getMap();
            MapleParty party = this.player.getParty();
            boolean clone = this.player.isClone();
            String namez = this.player.getName();
            int idz = this.player.getId();
            int messengerid = this.player.getMessenger() == null ? 0 : this.player.getMessenger().getId();
            int gid = this.player.getGuildId();
            int fid = this.player.getFamilyId();
            BuddyList bl = this.player.getBuddylist();
            MaplePartyCharacter chrp = new MaplePartyCharacter(this.player);
            MapleMessengerCharacter chrm = new MapleMessengerCharacter(this.player);
            MapleGuildCharacter chrg = this.player.getMGC();
            MapleFamilyCharacter chrf = this.player.getMFC();

            removalTask(shutdown);
            LoginServer.getLoginAuth(this.player.getId());
            this.player.saveToDB(true, fromCS);
            if (shutdown) {
                this.player = null;
                this.receiving = false;
                return;
            }

            if (!fromCS) {
                ChannelServer ch = ChannelServer.getInstance(map == null ? this.channel : map.getChannel());
                int chz = Find.findChannel(idz);
                if (chz < -1) {
                    disconnect(RemoveInChannelServer, true);
                    return;
                }
                try {
                    if ((chz == -1) || (ch == null) || (clone) || (ch.isShutdown())) {
                        this.player = null;
                        return;
                    }
                    if (messengerid > 0) {
                        Messenger.leaveMessenger(messengerid, chrm);
                    }
                    if (party != null) {
                        chrp.setOnline(false);
                        Party.updateParty(party.getId(), PartyOperation.LOG_ONOFF, chrp);
                        if ((map != null) && (party.getLeader().getId() == idz)) {
                            MaplePartyCharacter lchr = null;
                            for (MaplePartyCharacter pchr : party.getMembers()) {
                                if ((pchr != null) && (map.getCharacterById(pchr.getId()) != null) && ((lchr == null) || (lchr.getLevel() < pchr.getLevel()))) {
                                    lchr = pchr;
                                }
                            }
                            if (lchr != null) {
                                Party.updateParty(party.getId(), PartyOperation.CHANGE_LEADER_DC, lchr);
                            }
                        }
                    }
                    if (bl != null) {
                        if (!this.serverTransition) {
                            Buddy.loggedOff(namez, idz, this.channel, bl.getBuddyIds());
                        } else {
                            Buddy.loggedOn(namez, idz, this.channel, bl.getBuddyIds());
                        }
                    }
                    if ((gid > 0) && (chrg != null)) {
                        Guild.setGuildMemberOnline(chrg, false, -1);
                    }
                    if ((fid > 0) && (chrf != null)) {
                        Family.setFamilyMemberOnline(chrf, false, -1);
                    }
                } catch (Exception e) {
                    FileoutputUtil.outputFileError("log\\AccountStuck.log", e);
                    log.error(new StringBuilder().append(getLogMessage(this, "ERROR")).append(e).toString());
                } finally {
                    if ((RemoveInChannelServer) && (ch != null)) {
                        ch.removePlayer(idz, namez);
                    }
                    this.player = null;
                }
            } else {
                int ch = Find.findChannel(idz);
                if (ch > 0) {
                    disconnect(RemoveInChannelServer, false);
                    return;
                }
                try {
                    if (party != null) {
                        chrp.setOnline(false);
                        Party.updateParty(party.getId(), PartyOperation.LOG_ONOFF, chrp);
                    }
                    if (!this.serverTransition) {
                        Buddy.loggedOff(namez, idz, this.channel, bl.getBuddyIds());
                    } else {
                        Buddy.loggedOn(namez, idz, this.channel, bl.getBuddyIds());
                    }
                    if ((gid > 0) && (chrg != null)) {
                        Guild.setGuildMemberOnline(chrg, false, -1);
                    }
                    if ((fid > 0) && (chrf != null)) {
                        Family.setFamilyMemberOnline(chrf, false, -1);
                    }
                    if (this.player != null) {
                        this.player.setMessenger(null);
                    }
                } catch (Exception e) {
                    FileoutputUtil.outputFileError("log\\AccountStuck.log", e);
                    log.error(new StringBuilder().append(getLogMessage(this, "ERROR")).append(e).toString());
                } finally {
                    if ((RemoveInChannelServer) && (ch > 0)) {
                        CashShopServer.getPlayerStorage().deregisterPlayer(idz, namez);
                    }
                    this.player = null;
                }
            }
        }
        if ((!this.serverTransition) && (isLoggedIn())) {
            updateLoginState(0, getSessionIPAddress());
        }
        this.engines.clear();
    }

    public String getSessionIPAddress() {
        return this.session.getRemoteAddress().toString().split(":")[0];
    }

    public boolean CheckIPAddress() {
        if (this.accId < 0) {
            return false;
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT SessionIP, banned FROM accounts WHERE id = ?");
            ps.setInt(1, this.accId);
            ResultSet rs = ps.executeQuery();
            boolean canlogin = false;
            if (rs.next()) {
                String sessionIP = rs.getString("SessionIP");
                if (sessionIP != null) {
                    canlogin = getSessionIPAddress().equals(sessionIP.split(":")[0]);
                }
                if (rs.getInt("banned") > 0) {
                    canlogin = false;
                }
            }
            rs.close();
            ps.close();
            return canlogin;
        } catch (SQLException e) {
            log.error("Failed in checking IP address for client.", e);
        }
        return true;
    }

    public void DebugMessage(StringBuilder sb) {
        sb.append("IP: ");
        sb.append(getSession().getRemoteAddress());
        sb.append(" || 连接状态: ");
        sb.append(getSession().getChannel().isActive());
        sb.append(" || 正在关闭: ");
        sb.append(getSession().getChannel().isOpen());
        sb.append(" || CLIENT: ");
        sb.append(getSession().getChannel().attr(CLIENT_KEY) != null);
        sb.append(" || 是否已登陆: ");
        sb.append(isLoggedIn());
        sb.append(" || 角色上线: ");
        sb.append(getPlayer() != null);
    }

    public int getChannel() {
        return this.channel;
    }

    public ChannelServer getChannelServer() {
        return ChannelServer.getInstance(this.channel);
    }

    public int deleteCharacter(int cid) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT guildid, guildrank, familyid, name FROM characters WHERE id = ? AND accountid = ?");
            ps.setInt(1, cid);
            ps.setInt(2, this.accId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                return 1;
            }
            if (rs.getInt("guildid") > 0) {
                if (rs.getInt("guildrank") == 1) {
                    rs.close();
                    ps.close();
                    return 1;
                }
                Guild.deleteGuildCharacter(rs.getInt("guildid"), cid);
            }
            if ((rs.getInt("familyid") > 0) && (Family.getFamily(rs.getInt("familyid")) != null)) {
                Family.getFamily(rs.getInt("familyid")).leaveFamily(cid);
            }
            MapleSidekick s = Sidekick.getSidekickByChr(cid);
            if (s != null) {
                s.eraseToDB();
            }
            rs.close();
            ps.close();

            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM characters WHERE id = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "UPDATE pokemon SET active = 0 WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM hiredmerch WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM mts_cart WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM mts_items WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM cheatlog WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM mountdata WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM inventoryitems WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM famelog WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM famelog WHERE characterid_to = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM dueypackages WHERE RecieverId = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM wishlist WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM buddies WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM buddies WHERE buddyid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM keymap WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM trocklocations WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM savedlocations WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM skills WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM familiars WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM mountdata WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM skillmacros WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM queststatus WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM inventoryslot WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM extendedSlots WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM bank WHERE charid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM bosslog WHERE characterid = ?", cid);
            return 0;
        } catch (Exception e) {
            FileoutputUtil.outputFileError("log\\Packet_Except.log", e);
            log.error("删除角色错误.", e);
        }
        return 1;
    }

    public byte getGender() {
        return this.gender;
    }

    public void setGender(byte gender) {
        this.gender = gender;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE accounts SET gender = ? WHERE id = ?");
            ps.setByte(1, gender);
            ps.setInt(2, this.accId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            log.error("保存角色性别出错", e);
        }
    }

    public String getSecondPassword() {
        return this.secondPassword;
    }

    public void setSecondPassword(String secondPassword) {
        this.secondPassword = secondPassword;
    }

    public String getAccountName() {
        return this.accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getWorld() {
        return this.world;
    }

    public void setWorld(int world) {
        this.world = world;
    }

    public int getLatency() {
        return (int) (this.lastPong - this.lastPing);
    }

    public long getLastPong() {
        return this.lastPong;
    }

    public long getLastPing() {
        return this.lastPing;
    }

    public void pongReceived() {
        this.lastPong = System.currentTimeMillis();
    }

    public void sendPing() {
        this.lastPing = System.currentTimeMillis();
        this.session.write(LoginPacket.getPing());

        PingTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    if (MapleClient.this.lastPong - MapleClient.this.lastPing < 0L) {
                        MapleClient.this.disconnect(true, false);
                        if (MapleClient.this.getSession().isOpen()) {
                            MapleClient.log.info(MapleClient.getLogMessage(MapleClient.this, "自动断线 : Ping超时."));
                            MapleClient.this.getSession().close();
                        }
                    }
                } catch (NullPointerException e) {
                }
            }
        }, 15000L);
    }

    public static String getLogMessage(MapleClient cfor, String message) {
        return getLogMessage(cfor, message, new Object[0]);
    }

    public static String getLogMessage(MapleCharacter cfor, String message) {
        return getLogMessage(cfor == null ? null : cfor.getClient(), message);
    }

    public static String getLogMessage(MapleCharacter cfor, String message, Object[] parms) {
        return getLogMessage(cfor == null ? null : cfor.getClient(), message, parms);
    }

    public static String getLogMessage(MapleClient cfor, String message, Object[] parms) {
        StringBuilder builder = new StringBuilder();
        if (cfor != null) {
            if (cfor.getPlayer() != null) {
                builder.append("<");
                builder.append(MapleCharacterUtil.makeMapleReadable(cfor.getPlayer().getName()));
                builder.append(" (角色ID: ");
                builder.append(cfor.getPlayer().getId());
                builder.append(")> ");
            }
            if (cfor.getAccountName() != null) {
                builder.append("(账号: ");
                builder.append(cfor.getAccountName());
                builder.append(") ");
            }
        }
        builder.append(message);

        for (Object parm : parms) {
            int start = builder.indexOf("{}");
            builder.replace(start, start + 2, parm.toString());
        }
        return builder.toString();
    }

    public static int findAccIdForCharacterName(String charName) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT accountid FROM characters WHERE name = ?");
            ps.setString(1, charName);
            ResultSet rs = ps.executeQuery();
            int ret = -1;
            if (rs.next()) {
                ret = rs.getInt("accountid");
            }
            rs.close();
            ps.close();
            return ret;
        } catch (SQLException e) {
            log.error("findAccIdForCharacterName SQL error", e);
        }
        return -1;
    }

    public Set<String> getMacs() {
        return Collections.unmodifiableSet(this.macs);
    }

    public boolean isGm() {
        return this.gm;
    }

    public void setScriptEngine(String name, ScriptEngine e) {
        this.engines.put(name, e);
    }

    public ScriptEngine getScriptEngine(String name) {
        return (ScriptEngine) this.engines.get(name);
    }

    public void removeScriptEngine(String name) {
        this.engines.remove(name);
    }

    public ScheduledFuture<?> getIdleTask() {
        return this.idleTask;
    }

    public void setIdleTask(ScheduledFuture<?> idleTask) {
        this.idleTask = idleTask;
    }

    public int getCharacterSlots() {
        if (isGm()) {
            return 15;
        }
        if (this.charslots != DEFAULT_CHARSLOT) {
            return this.charslots;
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM character_slots WHERE accid = ? AND worldid = ?");
            ps.setInt(1, this.accId);
            ps.setInt(2, this.world);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                this.charslots = rs.getInt("charslots");
            } else {
                PreparedStatement psu = con.prepareStatement("INSERT INTO character_slots (accid, worldid, charslots) VALUES (?, ?, ?)");
                psu.setInt(1, this.accId);
                psu.setInt(2, this.world);
                psu.setInt(3, this.charslots);
                psu.executeUpdate();
                psu.close();
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            log.error("getCharacterSlots SQL error", e);
        }
        return this.charslots;
    }

    public boolean gainCharacterSlot() {
        if (getCharacterSlots() >= 15) {
            return false;
        }
        this.charslots += 1;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE character_slots SET charslots = ? WHERE worldid = ? AND accid = ?");
            ps.setInt(1, this.charslots);
            ps.setInt(2, this.world);
            ps.setInt(3, this.accId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            log.error("gainCharacterSlot SQL error", e);
            return false;
        }
        return true;
    }

    public static byte unbanIPMacs(String charname) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT accountid from characters where name = ?");
            ps.setString(1, charname);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                return -1;
            }
            int accid = rs.getInt(1);
            rs.close();
            ps.close();
            ps = con.prepareStatement("SELECT * FROM accounts WHERE id = ?");
            ps.setInt(1, accid);
            rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                return -1;
            }
            String sessionIP = rs.getString("sessionIP");
            String macs = rs.getString("macs");
            rs.close();
            ps.close();
            byte ret = 0;
            if (sessionIP != null) {
                PreparedStatement psa = con.prepareStatement("DELETE FROM ipbans WHERE ip like ?");
                psa.setString(1, sessionIP);
                psa.execute();
                psa.close();
                ret = (byte) (ret + 1);
            }
            if (macs != null) {
                String[] macz = macs.split(", ");
                for (String mac : macz) {
                    if (!mac.equals("")) {
                        PreparedStatement psa = con.prepareStatement("DELETE FROM macbans WHERE mac = ?");
                        psa.setString(1, mac);
                        psa.execute();
                        psa.close();
                    }
                }
                ret = (byte) (ret + 1);
            }
            return ret;
        } catch (SQLException e) {
            log.error("Error while unbanning", e);
        }
        return -2;
    }

    public static byte unHellban(String charname) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT accountid from characters where name = ?");
            ps.setString(1, charname);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                return -1;
            }
            int accid = rs.getInt(1);
            rs.close();
            ps.close();
            ps = con.prepareStatement("SELECT * FROM accounts WHERE id = ?");
            ps.setInt(1, accid);
            rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                return -1;
            }
            String sessionIP = rs.getString("sessionIP");
            String email = rs.getString("email");
            rs.close();
            ps.close();
            ps = con.prepareStatement(new StringBuilder().append("UPDATE accounts SET banned = 0, banreason = '' WHERE email = ?").append(sessionIP == null ? "" : " OR sessionIP = ?").toString());
            ps.setString(1, email);
            if (sessionIP != null) {
                ps.setString(2, sessionIP);
            }
            ps.execute();
            ps.close();
            return 0;
        } catch (SQLException e) {
            log.error("Error while unbanning", e);
        }
        return -2;
    }

    public boolean isMonitored() {
        return this.monitored;
    }

    public void setMonitored(boolean m) {
        this.monitored = m;
    }

    public boolean isReceiving() {
        return this.receiving;
    }

    public void setReceiving(boolean m) {
        this.receiving = m;
    }

    public boolean canClickNPC() {
        return this.lastNpcClick + 500L < System.currentTimeMillis();
    }

    public void setClickedNPC() {
        this.lastNpcClick = System.currentTimeMillis();
    }

    public void removeClickedNPC() {
        this.lastNpcClick = 0L;
    }

    public Timestamp getCreated() {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT createdat FROM accounts WHERE id = ?");
            ps.setInt(1, getAccID());
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                return null;
            }
            Timestamp ret = rs.getTimestamp("createdat");
            rs.close();
            ps.close();
            return ret;
        } catch (SQLException e) {
            throw new DatabaseException("error getting create", e);
        }

    }

    public String getTempIP() {
        return this.tempIP;
    }

    public void setTempIP(String s) {
        this.tempIP = s;
    }

    public boolean isLocalhost() {
        return (ServerConstants.Use_Localhost) || (ServerConstants.isIPLocalhost(getSessionIPAddress()));
    }

    public boolean hasIpCheck() {
        boolean ret = false;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM ipcheck WHERE ? LIKE CONCAT(ipaddress, '%')");
            ps.setString(1, getSessionIPAddress());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ret = rs.getInt(1) > 0;
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            log.error("Error checking ip Check", ex);
        }
        return ret;
    }

    public boolean hasCheck(int accid) {
        boolean ret = false;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts WHERE id = ?");
            ps.setInt(1, accid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ret = rs.getInt("check") > 0;
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            log.error("Error checking ip Check", ex);
        }
        return ret;
    }

    public static String getAccInfo(String accname, boolean admin) {
        StringBuilder ret = new StringBuilder(new StringBuilder().append("帐号 ").append(accname).append(" 的信息 -").toString());
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts WHERE name = ?");
            ps.setString(1, accname);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int banned = rs.getInt("banned");
                ret.append(" 状态: ");
                ret.append(banned > 0 ? "已封" : "正常");
                ret.append(" 封号理由: ");
                ret.append(banned > 0 ? rs.getString("banreason") : "(无描述)");
                if (admin) {
                    ret.append(" 点卷: ");
                    ret.append(rs.getInt("ACash"));
                    ret.append(" 抵用卷: ");
                    ret.append(rs.getInt("mPoints"));
                }
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            log.error("获取玩家封号理由信息出错", ex);
        }
        return ret.toString();
    }

    public static String getAccInfoByName(String charname, boolean admin) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT accountid from characters where name = ?");
            ps.setString(1, charname);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                return null;
            }
            int accid = rs.getInt(1);
            rs.close();
            ps.close();
            ps = con.prepareStatement("SELECT * FROM accounts WHERE id = ?");
            ps.setInt(1, accid);
            rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                return null;
            }
            StringBuilder ret = new StringBuilder(new StringBuilder().append("玩家 ").append(charname).append(" 的帐号信息 -").toString());
            int banned = rs.getInt("banned");
            if (admin) {
                ret.append(" 账号: ");
                ret.append(rs.getString("name"));
            }
            ret.append(" 状态: ");
            ret.append(banned > 0 ? "已封" : "正常");
            ret.append(" 封号理由: ");
            ret.append(banned > 0 ? rs.getString("banreason") : "(无描述)");
            rs.close();
            ps.close();
            return ret.toString();
        } catch (SQLException ex) {
            log.error("获取玩家封号理由信息出错", ex);
        }
        return null;
    }

    protected static class CharNameAndId {

        public final String name;
        public final int id;

        public CharNameAndId(String name, int id) {
            this.name = name;
            this.id = id;
        }
    }
}