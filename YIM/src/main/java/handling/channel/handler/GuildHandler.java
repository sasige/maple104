package handling.channel.handler;

import client.MapleCharacter;
import client.MapleClient;
import client.Skill;
import client.SkillFactory;
import handling.world.World.Alliance;
import handling.world.World.Guild;
import handling.world.guild.MapleGuild;
import handling.world.guild.MapleGuildResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import server.MapleStatEffect;
import tools.Pair;
import tools.data.LittleEndianAccessor;
import tools.packet.GuildPacket;

public class GuildHandler {

    private static final Map<String, Pair<Integer, Long>> invited = new HashMap();
    private static long nextPruneTime = System.currentTimeMillis() + 300000L;

    public static void DenyGuildRequest(String from, MapleClient c) {
        MapleCharacter cfrom = c.getChannelServer().getPlayerStorage().getCharacterByName(from);
        if ((cfrom != null) && (invited.remove(c.getPlayer().getName().toLowerCase()) != null)) {
            cfrom.getClient().getSession().write(GuildPacket.denyGuildInvitation(c.getPlayer().getName()));
        }
    }

    private static boolean isGuildNameAcceptable(String name) {
        return (name.getBytes().length >= 3) && (name.getBytes().length <= 12);
    }

    private static void respawnPlayer(MapleCharacter mc) {
        if (mc.getMap() == null) {
            return;
        }
        mc.getMap().broadcastMessage(GuildPacket.loadGuildName(mc));
        mc.getMap().broadcastMessage(GuildPacket.loadGuildIcon(mc));
    }

    public static void Guild(LittleEndianAccessor slea, MapleClient c) {
        long currentTime = System.currentTimeMillis();
        if (currentTime >= nextPruneTime) {
            Iterator itr = invited.entrySet().iterator();

            while (itr.hasNext()) {
                Map.Entry inv = (Map.Entry) itr.next();
                if (currentTime >= ((Long) ((Pair) inv.getValue()).right).longValue()) {
                    itr.remove();
                }
            }
            nextPruneTime += 300000L;
        }
        MapleCharacter chr = c.getPlayer();
        byte mode = slea.readByte();
        int guildId;
        String name;
        int cid;
        Skill skilli;
        int eff;
        switch (mode) {
            case 2:
                if ((chr.getGuildId() > 0) || (chr.getMapId() != 200000301)) {
                    chr.dropMessage(1, "不能创建家族\r\n已经有家族或没在家族中心");
                    return;
                }
                if (chr.getMeso() < 500000) {
                    chr.dropMessage(1, "你没有足够的金币创建一个家族。");
                    return;
                }
                String guildName = slea.readMapleAsciiString();
                if (!isGuildNameAcceptable(guildName)) {
                    chr.dropMessage(1, "你不能使用这个名字。");
                    return;
                }
                guildId = Guild.createGuild(chr.getId(), guildName);
                if (guildId == 0) {
                    chr.dropMessage(1, "创建家族出错\r\n请重试一次.");
                    return;
                }
                chr.gainMeso(-500000, true, true);
                chr.setGuildId(guildId);
                chr.setGuildRank((byte) 1);
                chr.saveGuildStatus();
                chr.finishAchievement(35);
                Guild.setGuildMemberOnline(chr.getMGC(), true, c.getChannel());
                c.getSession().write(GuildPacket.showGuildInfo(chr));
                Guild.gainGP(chr.getGuildId(), 500, chr.getId());
                chr.dropMessage(1, "恭喜你成功创建家族.");
                respawnPlayer(chr);
                break;
            case 5:
                if ((chr.getGuildId() <= 0) || (chr.getGuildRank() > 2)) {
                    return;
                }
                name = slea.readMapleAsciiString().toLowerCase();
                if (invited.containsKey(name)) {
                    chr.dropMessage(5, "玩家 " + name + " 已经在邀请的列表，请稍后在试。");
                    return;
                }
                MapleGuildResponse mgr = MapleGuild.sendInvite(c, name);
                if (mgr != null) {
                    c.getSession().write(mgr.getPacket());
                } else {
                    invited.put(name, new Pair(Integer.valueOf(chr.getGuildId()), Long.valueOf(currentTime + 1200000L)));
                }
                break;
            case 6:
                if (chr.getGuildId() > 0) {
                    return;
                }
                guildId = slea.readInt();
                cid = slea.readInt();
                if (cid != chr.getId()) {
                    return;
                }
                name = chr.getName().toLowerCase();
                Pair gid = (Pair) invited.remove(name);
                if ((gid == null) || (guildId != ((Integer) gid.left).intValue())) {
                    break;
                }
                chr.setGuildId(guildId);
                chr.setGuildRank((byte) 5);
                int s = Guild.addGuildMember(chr.getMGC());
                if (s == 0) {
                    chr.dropMessage(1, "尝试加入的家族成员数已到达最高限制。");
                    chr.setGuildId(0);
                    return;
                }
                c.getSession().write(GuildPacket.showGuildInfo(chr));
                MapleGuild gs = Guild.getGuild(guildId);
                for (byte[] pack : Alliance.getAllianceInfo(gs.getAllianceId(), true)) {
                    if (pack != null) {
                        c.getSession().write(pack);
                    }
                }
                chr.saveGuildStatus();
                respawnPlayer(c.getPlayer());
                break;
            case 7:
                cid = slea.readInt();
                name = slea.readMapleAsciiString();
                if ((cid != chr.getId()) || (!name.equals(chr.getName())) || (chr.getGuildId() <= 0)) {
                    return;
                }
                Guild.leaveGuild(chr.getMGC());
                c.getSession().write(GuildPacket.showGuildInfo(null));
                break;
            case 8:
                cid = slea.readInt();
                name = slea.readMapleAsciiString();
                if ((chr.getGuildRank() > 2) || (chr.getGuildId() <= 0)) {
                    return;
                }
                Guild.expelMember(chr.getMGC(), name, cid);
                break;
            case 14:
                if ((chr.getGuildId() <= 0) || (chr.getGuildRank() != 1)) {
                    return;
                }
                String[] ranks = new String[5];
                for (int i = 0; i < 5; i++) {
                    ranks[i] = slea.readMapleAsciiString();
                }
                Guild.changeRankTitle(chr.getGuildId(), ranks);
                break;
            case 15:
                cid = slea.readInt();
                byte newRank = slea.readByte();
                if ((newRank <= 1) || (newRank > 5) || (chr.getGuildRank() > 2) || ((newRank <= 2) && (chr.getGuildRank() != 1)) || (chr.getGuildId() <= 0)) {
                    return;
                }
                Guild.changeRank(chr.getGuildId(), cid, newRank);
                break;
            case 16:
                if ((chr.getGuildId() <= 0) || (chr.getGuildRank() != 1) || (chr.getMapId() != 200000301)) {
                    return;
                }
                if (chr.getMeso() < 1500000) {
                    chr.dropMessage(1, "金币不足。");
                    return;
                }
                short bg = slea.readShort();
                byte bgcolor = slea.readByte();
                short logo = slea.readShort();
                byte logocolor = slea.readByte();
                Guild.setGuildEmblem(chr.getGuildId(), bg, bgcolor, logo, logocolor);
                chr.gainMeso(-1500000, true, true);
                respawnPlayer(c.getPlayer());
                break;
            case 17:
                String notice = slea.readMapleAsciiString();
                if ((notice.length() > 100) || (chr.getGuildId() <= 0) || (chr.getGuildRank() > 2)) {
                    return;
                }
                Guild.setGuildNotice(chr.getGuildId(), notice);
                break;
            case 29:
                skilli = SkillFactory.getSkill(slea.readInt());
                if ((chr.getGuildId() <= 0) || (skilli == null) || (skilli.getId() < 91000000)) {
                    return;
                }
                eff = Guild.getSkillLevel(chr.getGuildId(), skilli.getId()) + 1;
                if (eff > skilli.getMaxLevel()) {
                    return;
                }
                MapleStatEffect skillid = skilli.getEffect(eff);
                if ((skillid.getReqGuildLevel() <= 0) || (chr.getMeso() < skillid.getPrice())) {
                    return;
                }
                if (!Guild.purchaseSkill(chr.getGuildId(), skillid.getSourceId(), chr.getName(), chr.getId())) {
                    break;
                }
                chr.gainMeso(-skillid.getPrice(), true);
                break;
            case 30:
                skilli = SkillFactory.getSkill(slea.readInt());
                if ((c.getPlayer().getGuildId() <= 0) || (skilli == null)) {
                    return;
                }
                eff = Guild.getSkillLevel(chr.getGuildId(), skilli.getId());
                if (eff <= 0) {
                    return;
                }
                MapleStatEffect skillii = skilli.getEffect(eff);
                if ((skillii.getReqGuildLevel() < 0) || (chr.getMeso() < skillii.getExtendPrice())) {
                    return;
                }
                if (!Guild.activateSkill(chr.getGuildId(), skillii.getSourceId(), chr.getName())) {
                    break;
                }
                chr.gainMeso(-skillii.getExtendPrice(), true);
                break;
            case 31:
                cid = slea.readInt();
                if ((chr.getGuildId() <= 0) || (chr.getGuildRank() > 1)) {
                    return;
                }
                Guild.setGuildLeader(chr.getGuildId(), cid);
                break;
            case 3:
            case 4:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            default:
                System.out.println("未知家族操作类型: " + mode + " " + slea.toString());
        }
    }
}