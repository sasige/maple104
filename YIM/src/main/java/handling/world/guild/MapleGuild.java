package handling.world.guild;

import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.MapleClient;
import client.SkillFactory;
import constants.GameConstants;
import database.DatabaseConnection;
import handling.world.World.Alliance;
import handling.world.World.Broadcast;
import handling.world.World.Guild;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.log4j.Logger;
import server.MapleStatEffect;
import tools.MaplePacketCreator;
import tools.data.MaplePacketLittleEndianWriter;
import tools.packet.GuildPacket;
import tools.packet.UIPacket;

public class MapleGuild
        implements Serializable {

    public static final long serialVersionUID = 6322150443228168192L;
    private final List<MapleGuildCharacter> members = new CopyOnWriteArrayList();
    private final Map<Integer, MapleGuildSkill> guildSkills = new HashMap();
    private final String[] rankTitles = new String[5];
    private String name;
    private String notice;
    private int id;
    private int gp;
    private int logo;
    private int logoColor;
    private int leader;
    private int capacity;
    private int logoBG;
    private int logoBGColor;
    private int signature;
    private int level;
    private boolean bDirty = true;
    private boolean proper = true;
    private int allianceid = 0;
    private int invitedid = 0;
    private final Map<Integer, MapleBBSThread> bbs = new HashMap();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private boolean init = false;
    private boolean changed = false;
    private boolean changed_skills = false;
    private static final Logger log = Logger.getLogger(MapleGuild.class);

    public MapleGuild(int guildid) {
        this(guildid, null);
    }

    public MapleGuild(int guildid, Map<Integer, Map<Integer, MapleBBSThread.MapleBBSReply>> replies) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM guilds WHERE guildid = ?");
            ps.setInt(1, guildid);
            ResultSet rs = ps.executeQuery();
            if (!rs.first()) {
                rs.close();
                ps.close();
                this.id = -1;
                return;
            }
            this.id = guildid;
            this.name = rs.getString("name");
            this.gp = rs.getInt("GP");
            this.logo = rs.getInt("logo");
            this.logoColor = rs.getInt("logoColor");
            this.logoBG = rs.getInt("logoBG");
            this.logoBGColor = rs.getInt("logoBGColor");
            this.capacity = rs.getInt("capacity");
            this.rankTitles[0] = rs.getString("rank1title");
            this.rankTitles[1] = rs.getString("rank2title");
            this.rankTitles[2] = rs.getString("rank3title");
            this.rankTitles[3] = rs.getString("rank4title");
            this.rankTitles[4] = rs.getString("rank5title");
            this.leader = rs.getInt("leader");
            this.notice = rs.getString("notice");
            this.signature = rs.getInt("signature");
            this.allianceid = rs.getInt("alliance");
            rs.close();
            ps.close();

            MapleGuildAlliance alliance = Alliance.getAlliance(this.allianceid);
            if (alliance == null) {
                this.allianceid = 0;
            }

            ps = con.prepareStatement("SELECT id, name, level, job, guildrank, guildContribution, alliancerank FROM characters WHERE guildid = ? ORDER BY guildrank ASC, name ASC", 1008);
            ps.setInt(1, guildid);
            rs = ps.executeQuery();
            if (!rs.first()) {
                System.err.println(new StringBuilder().append("家族ID: ").append(this.id).append(" 没用成员，系统自动解散该家族。").toString());
                rs.close();
                ps.close();
                writeToDB(true);
                this.proper = false;
                return;
            }
            boolean leaderCheck = false;
            byte gFix = 0;
            byte aFix = 0;
            do {
                int cid = rs.getInt("id");
                byte gRank = rs.getByte("guildrank");
                byte aRank = rs.getByte("alliancerank");

                if (cid == this.leader) {
                    leaderCheck = true;
                    if (gRank != 1) {
                        gRank = 1;
                        gFix = 1;
                    }
                    if (alliance != null) {
                        if ((alliance.getLeaderId() == cid) && (aRank != 1)) {
                            aRank = 1;
                            aFix = 1;
                        } else if ((alliance.getLeaderId() != cid) && (aRank != 2)) {
                            aRank = 2;
                            aFix = 2;
                        }
                    }
                } else {
                    if (gRank == 1) {
                        gRank = 2;
                        gFix = 2;
                    }
                    if (aRank < 3) {
                        aRank = 3;
                        aFix = 3;
                    }
                }
                this.members.add(new MapleGuildCharacter(cid, rs.getShort("level"), rs.getString("name"), (byte) -1, rs.getInt("job"), gRank, rs.getInt("guildContribution"), aRank, guildid, false));
            } while (rs.next());
            rs.close();
            ps.close();

            if (!leaderCheck) {
                System.err.println(new StringBuilder().append("族长[ ").append(this.leader).append(" ]没有在家族ID为 ").append(this.id).append(" 的家族中，系统自动解散这个家族。").toString());
                writeToDB(true);
                this.proper = false;
                return;
            }

            if (gFix > 0) {
                ps = con.prepareStatement("UPDATE characters SET guildrank = ? WHERE id = ?");
                ps.setByte(1, gFix);
                ps.setInt(2, this.leader);
                ps.executeUpdate();
                ps.close();
            }

            if (aFix > 0) {
                ps = con.prepareStatement("UPDATE characters SET alliancerank = ? WHERE id = ?");
                ps.setByte(1, aFix);
                ps.setInt(2, this.leader);
                ps.executeUpdate();
                ps.close();
            }

            ps = con.prepareStatement("SELECT * FROM bbs_threads WHERE guildid = ? ORDER BY localthreadid DESC");
            ps.setInt(1, guildid);
            rs = ps.executeQuery();
            while (rs.next()) {
                int tID = rs.getInt("localthreadid");
                MapleBBSThread thread = new MapleBBSThread(tID, rs.getString("name"), rs.getString("startpost"), rs.getLong("timestamp"), guildid, rs.getInt("postercid"), rs.getInt("icon"));
                if ((replies != null) && (replies.containsKey(Integer.valueOf(rs.getInt("threadid"))))) {
                    thread.replies.putAll((Map) replies.get(Integer.valueOf(rs.getInt("threadid"))));
                }
                this.bbs.put(Integer.valueOf(tID), thread);
            }
            rs.close();
            ps.close();

            ps = con.prepareStatement("SELECT * FROM guildskills WHERE guildid = ?");
            ps.setInt(1, guildid);
            rs = ps.executeQuery();
            while (rs.next()) {
                int sid = rs.getInt("skillid");
                if (sid < 91000000) {
                    rs.close();
                    ps.close();
                    System.err.println(new StringBuilder().append("非家族技能ID: ").append(sid).append(" 在家族ID为 ").append(this.id).append(" 的家族中，系统自动解散该家族。").toString());
                    writeToDB(true);
                    this.proper = false;
                    return;
                }
                this.guildSkills.put(Integer.valueOf(sid), new MapleGuildSkill(sid, rs.getInt("level"), rs.getLong("timestamp"), rs.getString("purchaser"), ""));
            }
            rs.close();
            ps.close();
            this.level = calculateLevel();
        } catch (SQLException se) {
            log.error(new StringBuilder().append("[MapleGuild] 从数据库中加载家族信息出错.").append(se).toString());
        }
    }

    public boolean isProper() {
        return this.proper;
    }
    
    /*
    public static void ZreHylvl(MapleClient c, int npcid) { 
  try 
  { 
   Connection con = DatabaseConnection.getConnection(); 
   PreparedStatement ps = con.prepareStatement( 
     "SELECT `name`, `level`, `str`, `dex`, " + 
     "`int`, `luk` FROM characters ORDER BY `level` DESC LIMIT 100"); 
    
   ResultSet rs = ps.executeQuery(); 
   c.getSession().write(MaplePacketCreator.ZreHylvl(npcid, rs)); 
    
   ps.close(); 
   rs.close(); 
  } 
  catch (Exception e) {log.error("failed to display guild ranks.", e);} 
 } 
    
    public static void ZreHyfame(MapleClient c, int npcid) 
 { 
  try 
  { 
   Connection con = DatabaseConnection.getConnection(); 
   PreparedStatement ps = con.prepareStatement( 
     "SELECT `name`, `fame`, `str`, `dex`, " + 
     "`int`, `luk` FROM characters ORDER BY `fame` DESC LIMIT 100"); 
    
   ResultSet rs = ps.executeQuery(); 
   c.getSession().write(MaplePacketCreator.ZreHyfame(npcid, rs)); 
    
   ps.close(); 
   rs.close(); 
  } 
  catch (Exception e) {log.error("failed to display guild ranks.", e);} 
 } 
    public static void ZreHymeso(MapleClient c, int npcid) 
 { 
  try 
  { 
   Connection con = DatabaseConnection.getConnection(); 
   PreparedStatement ps = con.prepareStatement( 
     "SELECT `name`, `meso`, `str`, `dex`, " + 
     "`int`, `luk` FROM characters ORDER BY `meso` DESC LIMIT 100"); 
    
   ResultSet rs = ps.executeQuery(); 
   c.getSession().write(MaplePacketCreator.ZreHymeso(npcid, rs)); 
    
   ps.close(); 
   rs.close(); 
  } 
  catch (Exception e) {log.error("failed to display guild ranks.", e);} 
 }
    
    public static void ZreHyzs(MapleClient c, int npcid) 
 { 
  try 
  { 
   Connection con = DatabaseConnection.getConnection(); 
   PreparedStatement ps = con.prepareStatement( 
     "SELECT `name`, `reborns`, `str`, `dex`, " + 
     "`int`, `luk` FROM characters ORDER BY `reborns` DESC LIMIT 10"); 
    
   ResultSet rs = ps.executeQuery(); 
   c.getSession().write(MaplePacketCreator.ZreHyzs(npcid, rs)); 
    
   ps.close(); 
   rs.close(); 
  } 
  catch (Exception e) {log.error("failed to display guild ranks.", e);} 
 } 
    
    public static void ZreHypvpkills(MapleClient c, int npcid) 
 { 
  try 
  { 
   Connection con = DatabaseConnection.getConnection(); 
   PreparedStatement ps = con.prepareStatement( 
     "SELECT `name`, `pvpkills`, `str`, `dex`, " + 
     "`int`, `luk` FROM characters ORDER BY `pvpkills` DESC LIMIT 10"); 
    
   ResultSet rs = ps.executeQuery(); 
   c.getSession().write(MaplePacketCreator.ZreHypvpkills(npcid, rs)); 
    
   ps.close(); 
   rs.close(); 
  } 
  catch (Exception e) {log.error("failed to display guild ranks.", e);} 
 } 
    public static void ZreHypvpdeaths(MapleClient c, int npcid) 
 { 
  try 
  { 
   Connection con = DatabaseConnection.getConnection(); 
   PreparedStatement ps = con.prepareStatement( 
     "SELECT `name`, `pvpdeaths`, `str`, `dex`, " + 
     "`int`, `luk` FROM characters ORDER BY `pvpdeaths` DESC LIMIT 20"); 
    
   ResultSet rs = ps.executeQuery(); 
   c.getSession().write(MaplePacketCreator.ZreHypvpdeaths(npcid, rs)); 
    
   ps.close(); 
   rs.close(); 
  } 
  catch (Exception e) {log.error("failed to display guild ranks.", e);} 
 } 
 * 
 */



    public static void loadAll() {
        Map replies = new LinkedHashMap();
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM bbs_replies");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int tID = rs.getInt("threadid");
                Map reply = (Map) replies.get(Integer.valueOf(tID));
                if (reply == null) {
                    reply = new HashMap();
                    replies.put(Integer.valueOf(tID), reply);
                }
                reply.put(Integer.valueOf(reply.size()), new MapleBBSThread.MapleBBSReply(reply.size(), rs.getInt("postercid"), rs.getString("content"), rs.getLong("timestamp")));
            }
            rs.close();
            ps.close();
            ps = con.prepareStatement("SELECT guildid FROM guilds");
            rs = ps.executeQuery();
            while (rs.next()) {
                Guild.addLoadedGuild(new MapleGuild(rs.getInt("guildid"), replies));
            }
            rs.close();
            ps.close();
        } catch (SQLException se) {
            log.error(new StringBuilder().append("[MapleGuild] 从数据库中加载家族信息出错.").append(se).toString());
        }
    }

    public static void loadAll(Object toNotify) {
        Map replies = new LinkedHashMap();
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM bbs_replies");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int tID = rs.getInt("threadid");
                Map reply = (Map) replies.get(Integer.valueOf(tID));
                if (reply == null) {
                    reply = new HashMap();
                    replies.put(Integer.valueOf(tID), reply);
                }
                reply.put(Integer.valueOf(reply.size()), new MapleBBSThread.MapleBBSReply(reply.size(), rs.getInt("postercid"), rs.getString("content"), rs.getLong("timestamp")));
            }
            rs.close();
            ps.close();
            boolean cont = false;
            ps = con.prepareStatement("SELECT guildid FROM guilds");
            rs = ps.executeQuery();
            while (rs.next()) {
                GuildLoad.QueueGuildForLoad(rs.getInt("guildid"), replies);
                cont = true;
            }
            rs.close();
            ps.close();
            if (!cont) {
                return;
            }
        } catch (SQLException se) {
            log.error(new StringBuilder().append("[MapleGuild] 从数据库中加载家族信息出错.").append(se).toString());
        }
        AtomicInteger FinishedThreads = new AtomicInteger(0);
        GuildLoad.Execute(toNotify);
        synchronized (toNotify) {
            try {
                toNotify.wait();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        while (FinishedThreads.incrementAndGet() != 6) {
            synchronized (toNotify) {
                try {
                    toNotify.wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public final void writeToDB(boolean bDisband) {
        try {
            Connection con = DatabaseConnection.getConnection();
            if (!bDisband) {
                StringBuilder buf = new StringBuilder("UPDATE guilds SET GP = ?, logo = ?, logoColor = ?, logoBG = ?, logoBGColor = ?, ");
                for (int i = 1; i < 6; i++) {
                    buf.append("rank").append(i).append("title = ?, ");
                }
                buf.append("capacity = ?, notice = ?, alliance = ?, leader = ? WHERE guildid = ?");

                PreparedStatement ps = con.prepareStatement(buf.toString());
                ps.setInt(1, this.gp);
                ps.setInt(2, this.logo);
                ps.setInt(3, this.logoColor);
                ps.setInt(4, this.logoBG);
                ps.setInt(5, this.logoBGColor);
                ps.setString(6, this.rankTitles[0]);
                ps.setString(7, this.rankTitles[1]);
                ps.setString(8, this.rankTitles[2]);
                ps.setString(9, this.rankTitles[3]);
                ps.setString(10, this.rankTitles[4]);
                ps.setInt(11, this.capacity);
                ps.setString(12, this.notice);
                ps.setInt(13, this.allianceid);
                ps.setInt(14, this.leader);
                ps.setInt(15, this.id);
                ps.executeUpdate();
                ps.close();

                if (this.changed) {
                    ps = con.prepareStatement("DELETE FROM bbs_threads WHERE guildid = ?");
                    ps.setInt(1, this.id);
                    ps.execute();
                    ps.close();

                    ps = con.prepareStatement("DELETE FROM bbs_replies WHERE guildid = ?");
                    ps.setInt(1, this.id);
                    ps.execute();
                    ps.close();

                    PreparedStatement pse = con.prepareStatement("INSERT INTO bbs_replies (`threadid`, `postercid`, `timestamp`, `content`, `guildid`) VALUES (?, ?, ?, ?, ?)");
                    ps = con.prepareStatement("INSERT INTO bbs_threads(`postercid`, `name`, `timestamp`, `icon`, `startpost`, `guildid`, `localthreadid`) VALUES(?, ?, ?, ?, ?, ?, ?)", 1);
                    ps.setInt(6, this.id);
                    for (MapleBBSThread bb : this.bbs.values()) {
                        ps.setInt(1, bb.ownerID);
                        ps.setString(2, bb.name);
                        ps.setLong(3, bb.timestamp);
                        ps.setInt(4, bb.icon);
                        ps.setString(5, bb.text);
                        ps.setInt(7, bb.localthreadID);
                        ps.execute();
                        ResultSet rs = ps.getGeneratedKeys();
                        if (!rs.next()) {
                            rs.close();
                            continue;
                        }
                        int ourId = rs.getInt(1);
                        rs.close();
                        pse.setInt(5, this.id);
                        for (MapleBBSThread.MapleBBSReply r : bb.replies.values()) {
                            pse.setInt(1, ourId);
                            pse.setInt(2, r.ownerID);
                            pse.setLong(3, r.timestamp);
                            pse.setString(4, r.content);
                            pse.addBatch();
                        }
                    }

                    pse.executeBatch();
                    pse.close();
                    ps.close();
                }
                if (this.changed_skills) {
                    ps = con.prepareStatement("DELETE FROM guildskills WHERE guildid = ?");
                    ps.setInt(1, this.id);
                    ps.execute();
                    ps.close();

                    ps = con.prepareStatement("INSERT INTO guildskills(`guildid`, `skillid`, `level`, `timestamp`, `purchaser`) VALUES(?, ?, ?, ?, ?)");
                    ps.setInt(1, this.id);
                    for (MapleGuildSkill i : this.guildSkills.values()) {
                        ps.setInt(2, i.skillID);
                        ps.setByte(3, (byte) i.level);
                        ps.setLong(4, i.timestamp);
                        ps.setString(5, i.purchaser);
                        ps.execute();
                    }
                    ps.close();
                }
                this.changed_skills = false;
                this.changed = false;
            } else {
                PreparedStatement ps = con.prepareStatement("DELETE FROM bbs_threads WHERE guildid = ?");
                ps.setInt(1, this.id);
                ps.execute();
                ps.close();

                ps = con.prepareStatement("DELETE FROM bbs_replies WHERE guildid = ?");
                ps.setInt(1, this.id);
                ps.execute();
                ps.close();

                ps = con.prepareStatement("DELETE FROM guildskills WHERE guildid = ?");
                ps.setInt(1, this.id);
                ps.execute();
                ps.close();

                ps = con.prepareStatement("DELETE FROM guilds WHERE guildid = ?");
                ps.setInt(1, this.id);
                ps.executeUpdate();
                ps.close();

                if (this.allianceid > 0) {
                    MapleGuildAlliance alliance = Alliance.getAlliance(this.allianceid);
                    if (alliance != null) {
                        alliance.removeGuild(this.id, false);
                    }
                }

                broadcast(GuildPacket.guildDisband(this.id));
            }
        } catch (SQLException se) {
            log.error(new StringBuilder().append("[MapleGuild] 保存家族信息出错.").append(se).toString());
        }
    }

    public int getId() {
        return this.id;
    }

    public int getLeaderId() {
        return this.leader;
    }

    public MapleCharacter getLeader(MapleClient c) {
        return c.getChannelServer().getPlayerStorage().getCharacterById(this.leader);
    }

    public int getGP() {
        return this.gp;
    }

    public int getLogo() {
        return this.logo;
    }

    public void setLogo(int l) {
        this.logo = l;
    }

    public int getLogoColor() {
        return this.logoColor;
    }

    public void setLogoColor(int c) {
        this.logoColor = c;
    }

    public int getLogoBG() {
        return this.logoBG;
    }

    public void setLogoBG(int bg) {
        this.logoBG = bg;
    }

    public int getLogoBGColor() {
        return this.logoBGColor;
    }

    public void setLogoBGColor(int c) {
        this.logoBGColor = c;
    }

    public String getNotice() {
        if (this.notice == null) {
            return "";
        }
        return this.notice;
    }

    public String getName() {
        return this.name;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public int getSignature() {
        return this.signature;
    }

    public void broadcast(byte[] packet) {
        broadcast(packet, -1, BCOp.NONE);
    }

    public void broadcast(byte[] packet, int exception) {
        broadcast(packet, exception, BCOp.NONE);
    }

    public void broadcast(byte[] packet, int exceptionId, BCOp bcop) {
        this.lock.writeLock().lock();
        try {
            buildNotifications();
        } finally {
            this.lock.writeLock().unlock();
        }

        this.lock.readLock().lock();
        try {
            for (MapleGuildCharacter mgc : this.members) {
                if (bcop == BCOp.DISBAND) {
                    if (mgc.isOnline()) {
                        Guild.setGuildAndRank(mgc.getId(), 0, 5, 0, 5);
                    } else {
                        setOfflineGuildStatus(0, (byte) 5, 0, (byte) 5, mgc.getId());
                    }
                } else if ((mgc.isOnline()) && (mgc.getId() != exceptionId)) {
                    if (bcop == BCOp.EMBELMCHANGE) {
                        Guild.changeEmblem(this.id, mgc.getId(), this);
                    } else {
                        Broadcast.sendGuildPacket(mgc.getId(), packet, exceptionId, this.id);
                    }
                }
            }
        } finally {
            this.lock.readLock().unlock();
        }
    }

    private void buildNotifications() {
        if (!this.bDirty) {
            return;
        }
        List mem = new LinkedList();
        Iterator toRemove = this.members.iterator();
        while (toRemove.hasNext()) {
            MapleGuildCharacter mgc = (MapleGuildCharacter) toRemove.next();
            if (!mgc.isOnline()) {
                continue;
            }
            if ((mem.contains(Integer.valueOf(mgc.getId()))) || (mgc.getGuildId() != this.id)) {
                this.members.remove(mgc);
                continue;
            }
            mem.add(Integer.valueOf(mgc.getId()));
        }

        this.bDirty = false;
    }

    public void setOnline(int cid, boolean online, int channel) {
        boolean bBroadcast = true;
        for (MapleGuildCharacter mgc : this.members) {
            if ((mgc.getGuildId() == this.id) && (mgc.getId() == cid)) {
                if (mgc.isOnline() == online) {
                    bBroadcast = false;
                }
                mgc.setOnline(online);
                mgc.setChannel((byte) channel);
                break;
            }
        }
        if (bBroadcast) {
            broadcast(GuildPacket.guildMemberOnline(this.id, cid, online), cid);
            if (this.allianceid > 0) {
                Alliance.sendGuild(GuildPacket.allianceMemberOnline(this.allianceid, this.id, cid, online), this.id, this.allianceid);
            }
        }
        this.bDirty = true;
        this.init = true;
    }

    public void guildChat(String name, int cid, String msg) {
        broadcast(MaplePacketCreator.multiChat(name, msg, 2), cid);
    }

    public void allianceChat(String name, int cid, String msg) {
        broadcast(MaplePacketCreator.multiChat(name, msg, 3), cid);
    }

    public String getRankTitle(int rank) {
        return this.rankTitles[(rank - 1)];
    }

    public int getAllianceId() {
        return this.allianceid;
    }

    public int getInvitedId() {
        return this.invitedid;
    }

    public void setInvitedId(int iid) {
        this.invitedid = iid;
    }

    public void setAllianceId(int a) {
        this.allianceid = a;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE guilds SET alliance = ? WHERE guildid = ?");
            ps.setInt(1, a);
            ps.setInt(2, this.id);
            ps.execute();
            ps.close();
        } catch (SQLException e) {
            log.error(new StringBuilder().append("[MapleGuild] 保存家族联盟信息出错.").append(e).toString());
        }
    }

    public static int createGuild(int leaderId, String name) {
        if (name.length() > 12) {
            return 0;
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT guildid FROM guilds WHERE name = ?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if (rs.first()) {
                rs.close();
                ps.close();
                return 0;
            }
            ps.close();
            rs.close();

            ps = con.prepareStatement("INSERT INTO guilds (`leader`, `name`, `signature`, `alliance`) VALUES (?, ?, ?, 0)", 1);
            ps.setInt(1, leaderId);
            ps.setString(2, name);
            ps.setInt(3, (int) (System.currentTimeMillis() / 1000L));
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            int ret = 0;
            if (rs.next()) {
                ret = rs.getInt(1);
            }
            rs.close();
            ps.close();
            return ret;
        } catch (SQLException se) {
            log.error(new StringBuilder().append("[MapleGuild] 创建家族信息出错.").append(se).toString());
        }
        return 0;
    }

    public int addGuildMember(MapleGuildCharacter mgc) {
        this.lock.writeLock().lock();
        try {
            if (this.members.size() >= this.capacity) {
                int i = 0;
                return i;
            }
            for (int i = this.members.size() - 1; i >= 0; i--) {
                if ((((MapleGuildCharacter) this.members.get(i)).getGuildRank() < 5) || (((MapleGuildCharacter) this.members.get(i)).getName().compareTo(mgc.getName()) < 0)) {
                    this.members.add(i + 1, mgc);
                    this.bDirty = true;
                    break;
                }
            }
        } finally {
            this.lock.writeLock().unlock();
        }
        gainGP(500, true, mgc.getId());
        broadcast(GuildPacket.newGuildMember(mgc));
        if (this.allianceid > 0) {
            Alliance.sendGuild(this.allianceid);
        }
        return 1;
    }

    public void leaveGuild(MapleGuildCharacter mgc) {
        this.lock.writeLock().lock();
        try {
            Iterator itr = this.members.iterator();
            while (itr.hasNext()) {
                MapleGuildCharacter mgcc = (MapleGuildCharacter) itr.next();
                if (mgcc.getId() == mgc.getId()) {
                    broadcast(GuildPacket.memberLeft(mgcc, true));
                    this.bDirty = true;
                    gainGP(mgcc.getGuildContribution() > 0 ? -mgcc.getGuildContribution() : -50);
                    this.members.remove(mgcc);
                    if (mgc.isOnline()) {
                        Guild.setGuildAndRank(mgcc.getId(), 0, 5, 0, 5);
                        break;
                    }
                    setOfflineGuildStatus(0, (byte) 5, 0, (byte) 5, mgcc.getId());

                    break;
                }
            }
        } finally {
            this.lock.writeLock().unlock();
        }
        if ((this.bDirty) && (this.allianceid > 0)) {
            Alliance.sendGuild(this.allianceid);
        }
    }

    public void expelMember(MapleGuildCharacter initiator, String name, int cid) {
        this.lock.writeLock().lock();
        try {
            Iterator itr = this.members.iterator();
            while (itr.hasNext()) {
                MapleGuildCharacter mgc = (MapleGuildCharacter) itr.next();
                if ((mgc.getId() == cid) && (initiator.getGuildRank() < mgc.getGuildRank())) {
                    broadcast(GuildPacket.memberLeft(mgc, true));
                    this.bDirty = true;
                    gainGP(mgc.getGuildContribution() > 0 ? -mgc.getGuildContribution() : -50);
                    if (mgc.isOnline()) {
                        Guild.setGuildAndRank(cid, 0, 5, 0, 5);
                    } else {
                        MapleCharacterUtil.sendNote(mgc.getName(), initiator.getName(), "被家族除名了。", 0);
                        setOfflineGuildStatus(0, (byte) 5, 0, (byte) 5, cid);
                    }
                    this.members.remove(mgc);
                    break;
                }
            }
        } finally {
            this.lock.writeLock().unlock();
        }
        if ((this.bDirty) && (this.allianceid > 0)) {
            Alliance.sendGuild(this.allianceid);
        }
    }

    public void changeARank() {
        changeARank(false);
    }

    public void changeARank(boolean leader) {
        if (this.allianceid <= 0) {
            return;
        }
        for (MapleGuildCharacter mgc : this.members) {
            byte newRank = 3;
            if (this.leader == mgc.getId()) {
                newRank = (byte) (leader ? 1 : 2);
            }
            if (mgc.isOnline()) {
                Guild.setGuildAndRank(mgc.getId(), this.id, mgc.getGuildRank(), mgc.getGuildContribution(), newRank);
            } else {
                setOfflineGuildStatus(this.id, mgc.getGuildRank(), mgc.getGuildContribution(), newRank, mgc.getId());
            }
            mgc.setAllianceRank(newRank);
        }
        Alliance.sendGuild(this.allianceid);
    }

    public void changeARank(int newRank) {
        if (this.allianceid <= 0) {
            return;
        }
        for (MapleGuildCharacter mgc : this.members) {
            if (mgc.isOnline()) {
                Guild.setGuildAndRank(mgc.getId(), this.id, mgc.getGuildRank(), mgc.getGuildContribution(), newRank);
            } else {
                setOfflineGuildStatus(this.id, mgc.getGuildRank(), mgc.getGuildContribution(), (byte) newRank, mgc.getId());
            }
            mgc.setAllianceRank((byte) newRank);
        }
        Alliance.sendGuild(this.allianceid);
    }

    public boolean changeARank(int cid, int newRank) {
        if (this.allianceid <= 0) {
            return false;
        }
        for (MapleGuildCharacter mgc : this.members) {
            if (cid == mgc.getId()) {
                if (mgc.isOnline()) {
                    Guild.setGuildAndRank(cid, this.id, mgc.getGuildRank(), mgc.getGuildContribution(), newRank);
                } else {
                    setOfflineGuildStatus(this.id, mgc.getGuildRank(), mgc.getGuildContribution(), (byte) newRank, cid);
                }
                mgc.setAllianceRank((byte) newRank);
                Alliance.sendGuild(this.allianceid);
                return true;
            }
        }
        return false;
    }

    public void changeGuildLeader(int cid) {
        if ((changeRank(cid, 1)) && (changeRank(this.leader, 2))) {
            if (this.allianceid > 0) {
                int aRank = getMGC(this.leader).getAllianceRank();
                if (aRank == 1) {
                    Alliance.changeAllianceLeader(this.allianceid, cid, true);
                } else {
                    changeARank(cid, aRank);
                }
                changeARank(this.leader, 3);
            }
            broadcast(GuildPacket.guildLeaderChanged(this.id, this.leader, cid, this.allianceid));
            this.leader = cid;
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement("UPDATE guilds SET leader = ? WHERE guildid = ?");
                ps.setInt(1, cid);
                ps.setInt(2, this.id);
                ps.execute();
                ps.close();
            } catch (SQLException e) {
                log.error(new StringBuilder().append("[MapleGuild] Saving leaderid ERROR.").append(e).toString());
            }
        }
    }

    public boolean changeRank(int cid, int newRank) {
        for (MapleGuildCharacter mgc : this.members) {
            if (cid == mgc.getId()) {
                if (mgc.isOnline()) {
                    Guild.setGuildAndRank(cid, this.id, newRank, mgc.getGuildContribution(), mgc.getAllianceRank());
                } else {
                    setOfflineGuildStatus(this.id, (byte) newRank, mgc.getGuildContribution(), mgc.getAllianceRank(), cid);
                }
                mgc.setGuildRank((byte) newRank);
                broadcast(GuildPacket.changeRank(mgc));
                return true;
            }
        }

        return false;
    }

    public void setGuildNotice(String notice) {
        this.notice = notice;
        broadcast(GuildPacket.guildNotice(this.id, notice));
    }

    public void memberLevelJobUpdate(MapleGuildCharacter mgc) {
        for (MapleGuildCharacter member : this.members) {
            if (member.getId() == mgc.getId()) {
                int old_level = member.getLevel();
                int old_job = member.getJobId();
                member.setJobId(mgc.getJobId());
                member.setLevel((short) mgc.getLevel());
                if (mgc.getLevel() > old_level) {
                    gainGP((mgc.getLevel() - old_level) * mgc.getLevel(), false, mgc.getId());
                }

                if (old_level != mgc.getLevel()) {
                    broadcast(MaplePacketCreator.sendLevelup(false, mgc.getLevel(), mgc.getName()), mgc.getId());
                }
                if (old_job != mgc.getJobId()) {
                    broadcast(MaplePacketCreator.sendJobup(false, mgc.getJobId(), mgc.getName()), mgc.getId());
                }
                broadcast(GuildPacket.guildMemberLevelJobUpdate(mgc));
                if (this.allianceid <= 0) {
                    break;
                }
                Alliance.sendGuild(GuildPacket.updateAlliance(mgc, this.allianceid), this.id, this.allianceid);
                break;
            }
        }
    }

    public void changeRankTitle(String[] ranks) {
        for (int i = 0; i < 5; i++) {
            this.rankTitles[i] = ranks[i];
        }
        broadcast(GuildPacket.rankTitleChange(this.id, ranks));
    }

    public void disbandGuild() {
        writeToDB(true);
        broadcast(null, -1, BCOp.DISBAND);
    }

    public void setGuildEmblem(short bg, byte bgcolor, short logo, byte logocolor) {
        this.logoBG = bg;
        this.logoBGColor = bgcolor;
        this.logo = logo;
        this.logoColor = logocolor;
        broadcast(null, -1, BCOp.EMBELMCHANGE);
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE guilds SET logo = ?, logoColor = ?, logoBG = ?, logoBGColor = ? WHERE guildid = ?");
            ps.setInt(1, logo);
            ps.setInt(2, this.logoColor);
            ps.setInt(3, this.logoBG);
            ps.setInt(4, this.logoBGColor);
            ps.setInt(5, this.id);
            ps.execute();
            ps.close();
        } catch (SQLException e) {
            log.error(new StringBuilder().append("[MapleGuild] Saving guild logo / BG colo ERROR.").append(e).toString());
        }
    }

    public MapleGuildCharacter getMGC(int cid) {
        for (MapleGuildCharacter mgc : this.members) {
            if (mgc.getId() == cid) {
                return mgc;
            }
        }
        return null;
    }

    public boolean increaseCapacity(boolean trueMax) {
        if (this.capacity < (trueMax ? 200 : 100)) {
            if (this.capacity + 5 <= (trueMax ? 200 : 100));
        } else {
            return false;
        }

        if ((trueMax) && (this.gp < 25000)) {
            return false;
        }
        if ((trueMax) && (this.gp - 25000 < GameConstants.getGuildExpNeededForLevel(getLevel() - 1))) {
            return false;
        }
        this.capacity += 5;
        broadcast(GuildPacket.guildCapacityChange(this.id, this.capacity));
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE guilds SET capacity = ? WHERE guildid = ?");
            ps.setInt(1, this.capacity);
            ps.setInt(2, this.id);
            ps.execute();
            ps.close();
        } catch (SQLException e) {
            log.error(new StringBuilder().append("[MapleGuild] Saving guild capacity ERROR.").append(e).toString());
        }
        return true;
    }

    public void gainGP(int amount) {
        gainGP(amount, true, -1);
    }

    public void gainGP(int amount, boolean broadcast) {
        gainGP(amount, broadcast, -1);
    }

    public void gainGP(int amount, boolean broadcast, int cid) {
        if (amount == 0) {
            return;
        }
        if (amount + this.gp < 0) {
            amount = -this.gp;
        }
        if ((cid > 0) && (amount > 0)) {
            MapleGuildCharacter mg = getMGC(cid);
            if (mg != null) {
                mg.setGuildContribution(mg.getGuildContribution() + amount);
                if (mg.isOnline()) {
                    Guild.setGuildAndRank(cid, this.id, mg.getGuildRank(), mg.getGuildContribution(), mg.getAllianceRank());
                } else {
                    setOfflineGuildStatus(this.id, mg.getGuildRank(), mg.getGuildContribution(), mg.getAllianceRank(), cid);
                }
                broadcast(GuildPacket.guildContribution(this.id, cid, mg.getGuildContribution()));
            }
        }
        this.gp += amount;
        this.level = calculateLevel();
        broadcast(GuildPacket.updateGP(this.id, this.gp, this.level));
        if (broadcast) {
            broadcast(UIPacket.getGPMsg(amount));
        }
    }

    public Collection<MapleGuildSkill> getSkills() {
        return this.guildSkills.values();
    }

    public int getSkillLevel(int sid) {
        if (!this.guildSkills.containsKey(Integer.valueOf(sid))) {
            return 0;
        }
        return ((MapleGuildSkill) this.guildSkills.get(Integer.valueOf(sid))).level;
    }

    public boolean activateSkill(int skill, String name) {
        if (!this.guildSkills.containsKey(Integer.valueOf(skill))) {
            return false;
        }
        MapleGuildSkill ourSkill = (MapleGuildSkill) this.guildSkills.get(Integer.valueOf(skill));
        MapleStatEffect skillid = SkillFactory.getSkill(skill).getEffect(ourSkill.level);
        if ((ourSkill.timestamp > System.currentTimeMillis()) || (skillid.getPeriod() <= 0)) {
            return false;
        }
        ourSkill.timestamp = (System.currentTimeMillis() + skillid.getPeriod() * 60000L);
        ourSkill.activator = name;
        broadcast(GuildPacket.guildSkillPurchased(this.id, skill, ourSkill.level, ourSkill.timestamp, ourSkill.purchaser, name));
        return true;
    }

    public boolean purchaseSkill(int skill, String name, int cid) {
        MapleStatEffect skillid = SkillFactory.getSkill(skill).getEffect(getSkillLevel(skill) + 1);
        if ((skillid.getReqGuildLevel() > getLevel()) || (skillid.getLevel() <= getSkillLevel(skill))) {
            return false;
        }
        MapleGuildSkill ourSkill = (MapleGuildSkill) this.guildSkills.get(Integer.valueOf(skill));
        if (ourSkill == null) {
            ourSkill = new MapleGuildSkill(skill, skillid.getLevel(), 0L, name, name);
            this.guildSkills.put(Integer.valueOf(skill), ourSkill);
        } else {
            ourSkill.level = skillid.getLevel();
            ourSkill.purchaser = name;
            ourSkill.activator = name;
        }
        if (skillid.getPeriod() <= 0) {
            ourSkill.timestamp = -1L;
        } else {
            ourSkill.timestamp = (System.currentTimeMillis() + skillid.getPeriod() * 60000L);
        }
        this.changed_skills = true;
        gainGP(1000, true, cid);
        broadcast(GuildPacket.guildSkillPurchased(this.id, skill, ourSkill.level, ourSkill.timestamp, name, name));
        return true;
    }

    public int getLevel() {
        return this.level;
    }

    public final int calculateLevel() {
        for (int i = 1; i < 10; i++) {
            if (this.gp < GameConstants.getGuildExpNeededForLevel(i)) {
                return i;
            }
        }
        return 10;
    }

    public void addMemberData(MaplePacketLittleEndianWriter mplew) {
        mplew.write(this.members.size());

        for (MapleGuildCharacter mgc : this.members) {
            mplew.writeInt(mgc.getId());
        }
        for (MapleGuildCharacter mgc : this.members) {
            mplew.writeAsciiString(mgc.getName(), 13);
            mplew.writeInt(mgc.getJobId());
            mplew.writeInt(mgc.getLevel());
            mplew.writeInt(mgc.getGuildRank());
            mplew.writeInt(mgc.isOnline() ? 1 : 0);
            mplew.writeInt(mgc.getAllianceRank());
            mplew.writeInt(mgc.getGuildContribution());
        }
    }

    public static MapleGuildResponse sendInvite(MapleClient c, String targetName) {
        MapleCharacter mc = c.getChannelServer().getPlayerStorage().getCharacterByName(targetName);
        if (mc == null) {
            return MapleGuildResponse.NOT_IN_CHANNEL;
        }
        if (mc.getGuildId() > 0) {
            return MapleGuildResponse.ALREADY_IN_GUILD;
        }
        mc.getClient().getSession().write(GuildPacket.guildInvite(c.getPlayer().getGuildId(), c.getPlayer().getName(), c.getPlayer().getLevel(), c.getPlayer().getJob()));
        return null;
    }

    public Collection<MapleGuildCharacter> getMembers() {
        return Collections.unmodifiableCollection(this.members);
    }

    public boolean isInit() {
        return this.init;
    }

    public List<MapleBBSThread> getBBS() {
        List ret = new ArrayList(this.bbs.values());
        Collections.sort(ret, new MapleBBSThread.ThreadComparator());
        return ret;
    }

    public int addBBSThread(String title, String text, int icon, boolean bNotice, int posterID) {
        int add = this.bbs.get(Integer.valueOf(0)) == null ? 1 : 0;
        this.changed = true;
        int ret = bNotice ? 0 : Math.max(1, this.bbs.size() + add);
        this.bbs.put(Integer.valueOf(ret), new MapleBBSThread(ret, title, text, System.currentTimeMillis(), this.id, posterID, icon));
        return ret;
    }

    public void editBBSThread(int localthreadid, String title, String text, int icon, int posterID, int guildRank) {
        MapleBBSThread thread = (MapleBBSThread) this.bbs.get(Integer.valueOf(localthreadid));
        if ((thread != null) && ((thread.ownerID == posterID) || (guildRank <= 2))) {
            this.changed = true;
            this.bbs.put(Integer.valueOf(localthreadid), new MapleBBSThread(localthreadid, title, text, System.currentTimeMillis(), this.id, thread.ownerID, icon));
        }
    }

    public void deleteBBSThread(int localthreadid, int posterID, int guildRank) {
        MapleBBSThread thread = (MapleBBSThread) this.bbs.get(Integer.valueOf(localthreadid));
        if ((thread != null) && ((thread.ownerID == posterID) || (guildRank <= 2))) {
            this.changed = true;
            this.bbs.remove(Integer.valueOf(localthreadid));
        }
    }

    public void addBBSReply(int localthreadid, String text, int posterID) {
        MapleBBSThread thread = (MapleBBSThread) this.bbs.get(Integer.valueOf(localthreadid));
        if (thread != null) {
            this.changed = true;
            thread.replies.put(Integer.valueOf(thread.replies.size()), new MapleBBSThread.MapleBBSReply(thread.replies.size(), posterID, text, System.currentTimeMillis()));
        }
    }

    public void deleteBBSReply(int localthreadid, int replyid, int posterID, int guildRank) {
        MapleBBSThread thread = (MapleBBSThread) this.bbs.get(Integer.valueOf(localthreadid));
        if (thread != null) {
            MapleBBSThread.MapleBBSReply reply = (MapleBBSThread.MapleBBSReply) thread.replies.get(Integer.valueOf(replyid));
            if ((reply != null) && ((reply.ownerID == posterID) || (guildRank <= 2))) {
                this.changed = true;
                thread.replies.remove(Integer.valueOf(replyid));
            }
        }
    }

    public boolean hasSkill(int id) {
        return this.guildSkills.containsKey(Integer.valueOf(id));
    }

    public static void setOfflineGuildStatus(int guildid, byte guildrank, int contribution, byte alliancerank, int cid) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE characters SET guildid = ?, guildrank = ?, guildContribution = ?, alliancerank = ? WHERE id = ?");
            ps.setInt(1, guildid);
            ps.setInt(2, guildrank);
            ps.setInt(3, contribution);
            ps.setInt(4, alliancerank);
            ps.setInt(5, cid);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException se) {
            System.out.println(new StringBuilder().append("SQLException: ").append(se.getLocalizedMessage()).toString());
        }
    }

    private static enum BCOp {

        NONE, DISBAND, EMBELMCHANGE;
    }
}
