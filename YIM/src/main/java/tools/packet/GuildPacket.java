package tools.packet;

import client.MapleCharacter;
import handling.SendPacketOpcode;
import handling.channel.MapleGuildRanking.GuildRankingInfo;
import handling.login.LoginServer;
import handling.world.World.Guild;
import handling.world.guild.MapleBBSThread;
import handling.world.guild.MapleBBSThread.MapleBBSReply;
import handling.world.guild.MapleGuild;
import handling.world.guild.MapleGuildAlliance;
import handling.world.guild.MapleGuildCharacter;
import handling.world.guild.MapleGuildSkill;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import server.ServerProperties;
import tools.MaplePacketCreator;
import tools.data.MaplePacketLittleEndianWriter;

public class GuildPacket {

    private static final Logger log = Logger.getLogger(GuildPacket.class);

    public static byte[] showGuildInfo(MapleCharacter c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(32);
        
        String reborns;
        
        reborns = new StringBuilder().append(" <重修").append(c.getReborns()).append("重天> ").toString();

        if ((c == null) || (c.getMGC() == null)) {
            mplew.write(0);
            return mplew.getPacket();
        }
        MapleGuild g = Guild.getGuild(c.getGuildId());
        if (g == null) {
            
          //  if (LoginServer.isMaxDamage()) {
        //  mplew.writeMapleAsciiString(new StringBuilder().append(" ").append(gender).append("[ ").append(chr.getVipname()).append(" ] ").append(gs.getName()).append(reborns).append(chr.getFsStr()).toString());
           //   mplew.writeMapleAsciiString(new StringBuilder().append(" ").append(c.getVipname(2)).append("").append(reborns).toString());
           //      } else {
            
            mplew.write(0);
            
        //    }
            
            return mplew.getPacket();
            
        }
        mplew.write(1);
        
        
    mplew.writeInt(g.getId());
    String gender;
   // String reborns;

    if (c.getGender() == 0) {
      gender = "[♂]";
    } else {
      gender = "[♀]";
    }

  //  reborns = new StringBuilder().append(" <重修").append(c.getReborns()).append("重天> ").toString();

        if (LoginServer.isMaxDamage()) {
        //  mplew.writeMapleAsciiString(new StringBuilder().append(" ").append(gender).append("[ ").append(chr.getVipname()).append(" ] ").append(gs.getName()).append(reborns).append(chr.getFsStr()).toString());
          mplew.writeMapleAsciiString(new StringBuilder().append("").append(" ").append(c.getVipname(2)).append("").append(g.getName()).append(reborns).toString());
        } else {
          mplew.writeMapleAsciiString(g.getName());
        }

    for (int i = 1; i <= 5; i++) {
      mplew.writeMapleAsciiString(g.getRankTitle(i));
    }
    g.addMemberData(mplew);
    mplew.writeInt(g.getCapacity());
    mplew.writeShort(g.getLogoBG());
    mplew.write(g.getLogoBGColor());
    mplew.writeShort(g.getLogo());
    mplew.write(g.getLogoColor());
    mplew.writeMapleAsciiString(g.getNotice());
    mplew.writeInt(g.getGP());
    mplew.writeInt(g.getGP());
    mplew.writeInt(g.getAllianceId() > 0 ? g.getAllianceId() : 0);
    mplew.write(g.getLevel());
    mplew.writeShort(0);
    mplew.writeShort(g.getSkills().size());
    for (MapleGuildSkill i : g.getSkills()) {
      mplew.writeInt(i.skillID);
      mplew.writeShort(i.level);
      mplew.writeLong(PacketHelper.getTime(i.timestamp));
      mplew.writeMapleAsciiString(i.purchaser);
      mplew.writeMapleAsciiString(i.activator);
    }
        
       // getGuildInfo(mplew, g);

        return mplew.getPacket();
    }

    private static void getGuildInfo(MaplePacketLittleEndianWriter mplew, MapleGuild guild) {
        mplew.writeInt(guild.getId());
        mplew.writeMapleAsciiString(guild.getName());
        for (int i = 1; i <= 5; i++) {
            mplew.writeMapleAsciiString(guild.getRankTitle(i));
        }
        guild.addMemberData(mplew);
        mplew.writeInt(guild.getCapacity());
        mplew.writeShort(guild.getLogoBG());
        mplew.write(guild.getLogoBGColor());
        mplew.writeShort(guild.getLogo());
        mplew.write(guild.getLogoColor());
        mplew.writeMapleAsciiString(guild.getNotice());
        mplew.writeInt(guild.getGP());
        mplew.writeInt(guild.getGP());
        mplew.writeInt(guild.getAllianceId() > 0 ? guild.getAllianceId() : 0);
        mplew.write(guild.getLevel());
        mplew.writeShort(0);
        mplew.writeShort(guild.getSkills().size());
        for (MapleGuildSkill i : guild.getSkills()) {
            mplew.writeInt(i.skillID);
            mplew.writeShort(i.level);
            mplew.writeLong(PacketHelper.getTime(i.timestamp));
            mplew.writeMapleAsciiString(i.purchaser);
            mplew.writeMapleAsciiString(i.activator);
        }
    }

    public static byte[] guildSkillPurchased(int gid, int sid, int level, long expiration, String purchase, String activate) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(85);
        mplew.writeInt(gid);
        mplew.writeInt(sid);
        mplew.writeShort(level);
        mplew.writeLong(PacketHelper.getTime(expiration));
        mplew.writeMapleAsciiString(purchase);
        mplew.writeMapleAsciiString(activate);

        return mplew.getPacket();
    }

    public static byte[] guildLeaderChanged(int gid, int oldLeader, int newLeader, int allianceId) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(89);
        mplew.writeInt(gid);

        mplew.writeInt(oldLeader);
        mplew.writeInt(newLeader);
        mplew.write(1);
        mplew.writeInt(allianceId);

        return mplew.getPacket();
    }

    public static byte[] guildMemberOnline(int gid, int cid, boolean bOnline) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(67);
        mplew.writeInt(gid);
        mplew.writeInt(cid);
        mplew.write(bOnline ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] guildContribution(int gid, int cid, int c) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(72);
        mplew.writeInt(gid);
        mplew.writeInt(cid);
        mplew.writeInt(c);

        return mplew.getPacket();
    }

    public static byte[] guildInvite(int gid, String charName, int levelFrom, int jobFrom) {//收到别人的家族邀请
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(5);
        mplew.writeInt(gid);
        mplew.writeMapleAsciiString(charName);
        mplew.writeInt(levelFrom);
        mplew.writeInt(jobFrom);
        mplew.writeInt(0);//104

        return mplew.getPacket();
    }

    public static byte[] denyGuildInvitation(String charname) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(61);
        mplew.writeMapleAsciiString(charname);

        return mplew.getPacket();
    }

    public static byte[] genericGuildMessage(byte code) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(code);

        return mplew.getPacket();
    }

    public static byte[] newGuildMember(MapleGuildCharacter mgc) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(45);
        mplew.writeInt(mgc.getGuildId());
        mplew.writeInt(mgc.getId());
        mplew.writeAsciiString(mgc.getName(), 13);
        mplew.writeInt(mgc.getJobId());
        mplew.writeInt(mgc.getLevel());
        mplew.writeInt(mgc.getGuildRank());
        mplew.writeInt(mgc.isOnline() ? 1 : 0);
        mplew.writeInt(mgc.getAllianceRank());
        mplew.writeInt(mgc.getGuildContribution());

        return mplew.getPacket();
    }

    public static byte[] memberLeft(MapleGuildCharacter mgc, boolean bExpelled) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(bExpelled ? 53 : 51);

        mplew.writeInt(mgc.getGuildId());
        mplew.writeInt(mgc.getId());
        mplew.writeMapleAsciiString(mgc.getName());

        return mplew.getPacket();
    }

    public static byte[] changeRank(MapleGuildCharacter mgc) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(70);
        mplew.writeInt(mgc.getGuildId());
        mplew.writeInt(mgc.getId());
        mplew.write(mgc.getGuildRank());

        return mplew.getPacket();
    }

    public static byte[] guildNotice(int gid, String notice) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(75);
        mplew.writeInt(gid);
        mplew.writeMapleAsciiString(notice);

        return mplew.getPacket();
    }

    public static byte[] guildMemberLevelJobUpdate(MapleGuildCharacter mgc) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(66);
        mplew.writeInt(mgc.getGuildId());
        mplew.writeInt(mgc.getId());
        mplew.writeInt(mgc.getLevel());
        mplew.writeInt(mgc.getJobId());

        return mplew.getPacket();
    }

    public static byte[] rankTitleChange(int gid, String[] ranks) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(68);
        mplew.writeInt(gid);

        for (String r : ranks) {
            mplew.writeMapleAsciiString(r);
        }
        return mplew.getPacket();
    }

    public static byte[] guildDisband(int gid) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(56);
        mplew.writeInt(gid);
        mplew.write(1);

        return mplew.getPacket();
    }

    public static byte[] guildEmblemChange(int gid, short bg, byte bgcolor, short logo, byte logocolor) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(73);
        mplew.writeInt(gid);
        mplew.writeShort(bg);
        mplew.write(bgcolor);
        mplew.writeShort(logo);
        mplew.write(logocolor);

        return mplew.getPacket();
    }

    public static byte[] guildCapacityChange(int gid, int capacity) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(64);
        mplew.writeInt(gid);
        mplew.write(capacity);

        return mplew.getPacket();
    }

    public static byte[] removeGuildFromAlliance(MapleGuildAlliance alliance, MapleGuild expelledGuild, boolean expelled) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(16);
        addAllianceInfo(mplew, alliance);
        getGuildInfo(mplew, expelledGuild);
        mplew.write(expelled ? 1 : 0);
        return mplew.getPacket();
    }

    public static byte[] changeAlliance(MapleGuildAlliance alliance, boolean in) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(1);
        mplew.write(in ? 1 : 0);
        mplew.writeInt(in ? alliance.getId() : 0);
        int noGuilds = alliance.getNoGuilds();
        MapleGuild[] g = new MapleGuild[noGuilds];
        for (int i = 0; i < noGuilds; i++) {
            g[i] = Guild.getGuild(alliance.getGuildId(i));
            if (g[i] == null) {
                return MaplePacketCreator.enableActions();
            }
        }
        mplew.write(noGuilds);
        for (int i = 0; i < noGuilds; i++) {
            mplew.writeInt(g[i].getId());

            Collection<MapleGuildCharacter> members = g[i].getMembers();
            mplew.writeInt(members.size());
            for (MapleGuildCharacter mgc : members) {
                mplew.writeInt(mgc.getId());
                mplew.write(in ? mgc.getAllianceRank() : 0);
            }
        }
        return mplew.getPacket();
    }

    public static byte[] changeAllianceLeader(int allianceid, int newLeader, int oldLeader) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(2);
        mplew.writeInt(allianceid);
        mplew.writeInt(oldLeader);
        mplew.writeInt(newLeader);
        return mplew.getPacket();
    }

    public static byte[] updateAllianceLeader(int allianceid, int newLeader, int oldLeader) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(25);
        mplew.writeInt(allianceid);
        mplew.writeInt(oldLeader);
        mplew.writeInt(newLeader);
        return mplew.getPacket();
    }

    public static byte[] sendAllianceInvite(String allianceName, MapleCharacter inviter) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(3);
        mplew.writeInt(inviter.getGuildId());
        mplew.writeMapleAsciiString(inviter.getName());

        mplew.writeMapleAsciiString(allianceName);
        return mplew.getPacket();
    }

    public static byte[] changeGuildInAlliance(MapleGuildAlliance alliance, MapleGuild guild, boolean add) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(4);
        mplew.writeInt(add ? alliance.getId() : 0);
        mplew.writeInt(guild.getId());
        Collection<MapleGuildCharacter> members = guild.getMembers();
        mplew.writeInt(members.size());
        for (MapleGuildCharacter mgc : members) {
            mplew.writeInt(mgc.getId());
            mplew.write(add ? mgc.getAllianceRank() : 0);
        }
        return mplew.getPacket();
    }

    public static byte[] changeAllianceRank(int allianceid, MapleGuildCharacter player) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(5);
        mplew.writeInt(allianceid);
        mplew.writeInt(player.getId());
        mplew.writeInt(player.getAllianceRank());
        return mplew.getPacket();
    }

    public static byte[] createGuildAlliance(MapleGuildAlliance alliance) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(15);
        addAllianceInfo(mplew, alliance);
        int noGuilds = alliance.getNoGuilds();
        MapleGuild[] g = new MapleGuild[noGuilds];
        for (int i = 0; i < alliance.getNoGuilds(); i++) {
            g[i] = Guild.getGuild(alliance.getGuildId(i));
            if (g[i] == null) {
                return MaplePacketCreator.enableActions();
            }
        }
        for (MapleGuild gg : g) {
            getGuildInfo(mplew, gg);
        }
        return mplew.getPacket();
    }

    public static byte[] getAllianceInfo(MapleGuildAlliance alliance) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(12);
        mplew.write(alliance == null ? 0 : 1);
        if (alliance != null) {
            addAllianceInfo(mplew, alliance);
        }
        return mplew.getPacket();
    }

    public static byte[] getAllianceUpdate(MapleGuildAlliance alliance) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(23);
        addAllianceInfo(mplew, alliance);
        return mplew.getPacket();
    }

    public static byte[] getGuildAlliance(MapleGuildAlliance alliance) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(13);
        if (alliance == null) {
            mplew.writeInt(0);
            return mplew.getPacket();
        }
        int noGuilds = alliance.getNoGuilds();
        MapleGuild[] g = new MapleGuild[noGuilds];
        for (int i = 0; i < alliance.getNoGuilds(); i++) {
            g[i] = Guild.getGuild(alliance.getGuildId(i));
            if (g[i] == null) {
                return MaplePacketCreator.enableActions();
            }
        }
        mplew.writeInt(noGuilds);
        for (MapleGuild gg : g) {
            getGuildInfo(mplew, gg);
        }
        return mplew.getPacket();
    }

    public static byte[] addGuildToAlliance(MapleGuildAlliance alliance, MapleGuild newGuild) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(18);
        addAllianceInfo(mplew, alliance);
        mplew.writeInt(newGuild.getId());
        getGuildInfo(mplew, newGuild);
        mplew.write(0);
        return mplew.getPacket();
    }

    private static void addAllianceInfo(MaplePacketLittleEndianWriter mplew, MapleGuildAlliance alliance) {
        mplew.writeInt(alliance.getId());
        mplew.writeMapleAsciiString(alliance.getName());
        for (int i = 1; i <= 5; i++) {
            mplew.writeMapleAsciiString(alliance.getRank(i));
        }
        mplew.write(alliance.getNoGuilds());
        for (int i = 0; i < alliance.getNoGuilds(); i++) {
            mplew.writeInt(alliance.getGuildId(i));
        }
        mplew.writeInt(alliance.getCapacity());
        mplew.writeMapleAsciiString(alliance.getNotice());
    }

    public static byte[] allianceMemberOnline(int alliance, int gid, int id, boolean online) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(14);
        mplew.writeInt(alliance);
        mplew.writeInt(gid);
        mplew.writeInt(id);
        mplew.write(online ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] updateAlliance(MapleGuildCharacter mgc, int allianceid) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(24);
        mplew.writeInt(allianceid);
        mplew.writeInt(mgc.getGuildId());
        mplew.writeInt(mgc.getId());
        mplew.writeInt(mgc.getLevel());
        mplew.writeInt(mgc.getJobId());

        return mplew.getPacket();
    }

    public static byte[] updateAllianceRank(int allianceid, MapleGuildCharacter mgc) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(27);
        mplew.writeInt(allianceid);
        mplew.writeInt(mgc.getId());
        mplew.writeInt(mgc.getAllianceRank());

        return mplew.getPacket();
    }

    public static byte[] disbandAlliance(int alliance) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
        mplew.write(29);
        mplew.writeInt(alliance);

        return mplew.getPacket();
    }

    public static byte[] BBSThreadList(List<MapleBBSThread> bbs, int start) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.BBS_OPERATION.getValue());
        mplew.write(6);

        if (bbs == null) {
            mplew.write(0);
            mplew.writeLong(0L);
            return mplew.getPacket();
        }
        int threadCount = bbs.size();
        MapleBBSThread notice = null;
        for (MapleBBSThread b : bbs) {
            if (b.isNotice()) {
                notice = b;
                break;
            }
        }
        mplew.write(notice == null ? 0 : 1);
        if (notice != null) {
            addThread(mplew, notice);
        }
        if (threadCount < start) {
            start = 0;
        }

        mplew.writeInt(threadCount);
        int pages = Math.min(10, threadCount - start);
        mplew.writeInt(pages);

        for (int i = 0; i < pages; i++) {
            addThread(mplew, (MapleBBSThread) bbs.get(start + i));
        }
        return mplew.getPacket();
    }

    private static void addThread(MaplePacketLittleEndianWriter mplew, MapleBBSThread rs) {
        mplew.writeInt(rs.localthreadID);
        mplew.writeInt(rs.ownerID);
        mplew.writeMapleAsciiString(rs.name);
        mplew.writeLong(PacketHelper.getKoreanTimestamp(rs.timestamp));
        mplew.writeInt(rs.icon);
        mplew.writeInt(rs.getReplyCount());
    }

    public static byte[] showThread(MapleBBSThread thread) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.BBS_OPERATION.getValue());
        mplew.write(7);

        mplew.writeInt(thread.localthreadID);
        mplew.writeInt(thread.ownerID);
        mplew.writeLong(PacketHelper.getKoreanTimestamp(thread.timestamp));
        mplew.writeMapleAsciiString(thread.name);
        mplew.writeMapleAsciiString(thread.text);
        mplew.writeInt(thread.icon);
        mplew.writeInt(thread.getReplyCount());
        for (MapleBBSThread.MapleBBSReply reply : thread.replies.values()) {
            mplew.writeInt(reply.replyid);
            mplew.writeInt(reply.ownerID);
            mplew.writeLong(PacketHelper.getKoreanTimestamp(reply.timestamp));
            mplew.writeMapleAsciiString(reply.content);
        }
        return mplew.getPacket();
    }

    public static byte[] showGuildRanks(int npcid, List<GuildRankingInfo> all) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(80);
        mplew.writeInt(npcid);

        mplew.writeInt(all.size());

        for (GuildRankingInfo info : all) {
            mplew.writeMapleAsciiString(info.getName());
            mplew.writeInt(info.getGP());
            mplew.writeInt(info.getGP());
            mplew.writeInt(info.getLogo());
            mplew.writeInt(info.getLogoColor());
            mplew.writeInt(info.getLogoBg());
            mplew.writeInt(info.getLogoBgColor());
        }

        return mplew.getPacket();
    }

    public static byte[] updateGP(int gid, int GP, int glevel) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
        mplew.write(79);
        mplew.writeInt(gid);
        mplew.writeInt(GP);
        mplew.writeInt(glevel);

        return mplew.getPacket();
    }

    public static byte[] loadGuildName(MapleCharacter chr) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.LOAD_GUILD_NAME.getValue());
        mplew.writeInt(chr.getId());
        if (chr.getGuildId() <= 0) {
            mplew.writeShort(0);
        } else {
            MapleGuild gs = Guild.getGuild(chr.getGuildId());
            if (gs != null) {
                mplew.writeMapleAsciiString(gs.getName());
            } else {
                mplew.writeShort(0);
            }
        }
        return mplew.getPacket();
    }

    public static byte[] loadGuildIcon(MapleCharacter chr) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.LOAD_GUILD_ICON.getValue());
        mplew.writeInt(chr.getId());
        if (chr.getGuildId() <= 0) {
            mplew.writeZeroBytes(6);
        } else {
            MapleGuild gs = Guild.getGuild(chr.getGuildId());
            if (gs != null) {
                mplew.writeShort(gs.getLogoBG());
                mplew.write(gs.getLogoBGColor());
                mplew.writeShort(gs.getLogo());
                mplew.write(gs.getLogoColor());
            } else {
                mplew.writeZeroBytes(6);
            }
        }
        return mplew.getPacket();
    }
}