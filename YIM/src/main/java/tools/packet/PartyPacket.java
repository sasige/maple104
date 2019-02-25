package tools.packet;

import client.MapleCharacter;
import client.MapleClient;
import handling.SendPacketOpcode;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import handling.world.PartyOperation;
import handling.world.World.Party;
import handling.world.exped.ExpeditionType;
import handling.world.exped.MapleExpedition;
import handling.world.exped.PartySearch;
import handling.world.exped.PartySearchType;
import handling.world.sidekick.MapleSidekick;
import handling.world.sidekick.MapleSidekickCharacter;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import server.ServerProperties;
import server.maps.MapleMap;
import tools.data.MaplePacketLittleEndianWriter;

public class PartyPacket {

    private static final Logger log = Logger.getLogger(PartyPacket.class);

    public static byte[] partyCreated(int partyid) {//创建组队
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PARTY_OPERATION.getValue());
        mplew.write(0x0C);
        mplew.writeInt(partyid);
        mplew.writeInt(999999999);
        mplew.writeInt(999999999);
        mplew.writeLong(0);
        mplew.write(1);

        return mplew.getPacket();
    }

    public static byte[] partyInvite(MapleCharacter from) {//收到别人的邀请
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PARTY_OPERATION.getValue());
        mplew.write(0x04);
        mplew.writeInt(from.getParty() == null ? 0 : from.getParty().getId());
        mplew.writeMapleAsciiString(from.getName());
        mplew.writeInt(from.getLevel());
        mplew.writeInt(from.getJob());
        mplew.writeInt(0);//104
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] partyRequestInvite(MapleCharacter from) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PARTY_OPERATION.getValue());
        mplew.write(7);
        mplew.writeInt(from.getId());
        mplew.writeMapleAsciiString(from.getName());
        mplew.writeInt(from.getLevel());
        mplew.writeInt(from.getJob());

        return mplew.getPacket();
    }

    public static byte[] partyStatusMessage(int message) {//提示你有一个组队 不能加入其它队伍
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PARTY_OPERATION.getValue());
        mplew.write(message);

        return mplew.getPacket();
    }

    public static byte[] partyStatusMessage(int message, String charname) {//发送组队邀请
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PARTY_OPERATION.getValue());

        mplew.write(message);
        mplew.writeMapleAsciiString(charname);

        return mplew.getPacket();
    }

    public static byte[] partyStatusMessage(String message) {//提示信息 提示别人加入别的组队
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPOUSE_MESSAGE.getValue());
        mplew.writeShort(0x0B);
        mplew.writeMapleAsciiString(message);

        return mplew.getPacket();
    }

    private static void addPartyStatus(int forchannel, MapleParty party, MaplePacketLittleEndianWriter lew, boolean leaving) {
        addPartyStatus(forchannel, party, lew, leaving, false);
    }

    private static void addPartyStatus(int forchannel, MapleParty party, MaplePacketLittleEndianWriter lew, boolean leaving, boolean exped) {//加入组队.循环6次
        List<MaplePartyCharacter> partymembers;
        if (party == null) {
            partymembers = new ArrayList();
        } else {
            partymembers = new ArrayList(party.getMembers());
        }
        while (partymembers.size() < 6) {
            partymembers.add(new MaplePartyCharacter());
        }
        for (MaplePartyCharacter partychar : partymembers) {
            lew.writeInt(partychar.getId());
        }
        for (MaplePartyCharacter partychar : partymembers) {
            lew.writeAsciiString(partychar.getName(), 13);
        }
        for (MaplePartyCharacter partychar : partymembers) {
            lew.writeInt(partychar.getJobId());
        }
        for (MaplePartyCharacter partychar : partymembers) {
            lew.writeInt(0);//104
        }            
        for (MaplePartyCharacter partychar : partymembers) {
            lew.writeInt(partychar.getLevel());
        }    
        for (MaplePartyCharacter partychar : partymembers) {
            if (partychar.isOnline()) {
                lew.writeInt(partychar.getChannel() - 1);
            } else {
                lew.writeInt(-2);
            }
        }
        for (MaplePartyCharacter partychar : partymembers) {
            lew.writeInt(0);
        }
        lew.writeInt(party == null ? 0 : party.getLeader().getId());
        if (exped) {
            return;
        }
        for (MaplePartyCharacter partychar : partymembers) {
            if (partychar.getChannel() == forchannel) {
                lew.writeInt(partychar.getMapid());
            } else {
                lew.writeInt(0);
            }
        }
        for (MaplePartyCharacter partychar : partymembers) {
            if ((partychar.getChannel() == forchannel) && (!leaving)) {
                lew.writeInt(partychar.getDoorTown());
                lew.writeInt(partychar.getDoorTarget());
                lew.writeInt(partychar.getDoorSkill());
                lew.writeInt(partychar.getDoorPosition().x);
                lew.writeInt(partychar.getDoorPosition().y);
            } else {
                lew.writeInt(leaving ? 999999999 : 0);
                lew.writeInt(leaving ? 999999999 : 0);
                lew.writeInt(0);
                lew.writeInt(leaving ? -1 : 0);
                lew.writeInt(leaving ? -1 : 0);
            }
        }
        lew.write(1);
    }

    public static byte[] updateParty(int forChannel, MapleParty party, PartyOperation op, MaplePartyCharacter target) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PARTY_OPERATION.getValue());
        switch (op) {
            case 离开队伍:
            case 驱逐成员:
            case 解散队伍:
                mplew.write(16);
                mplew.writeInt(party.getId());
                mplew.writeInt(target.getId());
                mplew.write(op == PartyOperation.解散队伍 ? 0 : 1);
                if (op == PartyOperation.解散队伍) {
                    mplew.writeInt(target.getId());
                } else {
                    mplew.write(op == PartyOperation.驱逐成员 ? 1 : 0);
                    mplew.writeMapleAsciiString(target.getName());
                    addPartyStatus(forChannel, party, mplew, op == PartyOperation.离开队伍);
                }
                break;
            case 加入队伍:
                mplew.write(0x13);
                mplew.writeInt(party.getId());
                mplew.writeMapleAsciiString(target.getName());
                addPartyStatus(forChannel, party, mplew, false);
                break;
            case 更新队伍:
            case LOG_ONOFF:
                mplew.write(11);
                mplew.writeInt(party.getId());
                addPartyStatus(forChannel, party, mplew, op == PartyOperation.LOG_ONOFF);
                break;
            case 改变队长:
            case CHANGE_LEADER_DC:
                mplew.write(35);
                mplew.writeInt(target.getId());
                mplew.write(op == PartyOperation.CHANGE_LEADER_DC ? 1 : 0);
        }

        return mplew.getPacket();
    }

    public static byte[] partyPortal(int townId, int targetId, int skillId, Point position, boolean animation) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PARTY_OPERATION.getValue());
        mplew.write(45);
        mplew.write(animation ? 0 : 1);
        mplew.writeInt(townId);
        mplew.writeInt(targetId);
        mplew.writeInt(skillId);
        mplew.writePos(position);

        return mplew.getPacket();
    }

    public static byte[] updatePartyMemberHP(int cid, int curhp, int maxhp) {//显示对方HP
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.UPDATE_PARTYMEMBER_HP.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(curhp);
        mplew.writeInt(maxhp);

        return mplew.getPacket();
    }

    public static byte[] getPartyListing(PartySearchType pst) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PARTY_OPERATION.getValue());
        mplew.write(94);
        mplew.writeInt(pst.id);
        List<PartySearch> parties = Party.searchParty(pst);
        mplew.writeInt(parties.size());
        for (PartySearch party : parties) {
            if (pst.exped) {
                MapleExpedition me = Party.getExped(party.getId());
                mplew.writeInt(party.getId());
                mplew.writeAsciiString(party.getName(), 37);

                mplew.writeInt(pst.id);
                mplew.writeInt(0);
                for (int i = 0; i < 5; i++) {
                    if (i < me.getParties().size()) {
                        MapleParty part = Party.getParty(((Integer) me.getParties().get(i)).intValue());
                        if (part != null) {
                            addPartyStatus(-1, part, mplew, false, true);
                        } else {
                            mplew.writeZeroBytes(202);
                        }
                    } else {
                        mplew.writeZeroBytes(202);
                    }
                }
            } else {
                mplew.writeInt(party.getId());
                mplew.writeAsciiString(party.getName(), 37);
                mplew.writeInt(0);
                mplew.writeInt(0);
                addPartyStatus(-1, Party.getParty(party.getId()), mplew, false, true);
            }

        }

        return mplew.getPacket();
    }

    public static byte[] partyListingAdded(PartySearch ps) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PARTY_OPERATION.getValue());
        mplew.write(92);
        mplew.writeInt(ps.getType().id);

        if (ps.getType().exped) {
            MapleExpedition me = Party.getExped(ps.getId());

            mplew.writeInt(ps.getId());
            mplew.writeAsciiString(ps.getName(), 37);
            mplew.writeInt(ps.getType().id);
            mplew.writeInt(0);
            for (int i = 0; i < 5; i++) {
                if (i < me.getParties().size()) {
                    MapleParty party = Party.getParty(((Integer) me.getParties().get(i)).intValue());
                    if (party != null) {
                        addPartyStatus(-1, party, mplew, false, true);
                    } else {
                        mplew.writeZeroBytes(202);
                    }
                } else {
                    mplew.writeZeroBytes(202);
                }
            }
        } else {
            mplew.writeInt(ps.getId());
            mplew.writeAsciiString(ps.getName(), 37);
            mplew.writeInt(0);
            mplew.writeInt(0);
            addPartyStatus(-1, Party.getParty(ps.getId()), mplew, false, true);
        }

        return mplew.getPacket();
    }

    public static byte[] expeditionStatus(MapleExpedition me, boolean created) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.EXPEDITION_OPERATION.getValue());
        mplew.write(created ? 73 : 71);
        mplew.writeInt(me.getType().exped);
        mplew.writeInt(0);
        for (int i = 0; i < 5; i++) {
            if (i < me.getParties().size()) {
                MapleParty party = Party.getParty(me.getParties().get(i));
                if (party != null) {
                    addPartyStatus(-1, party, mplew, false, true);
                } else {
                    mplew.writeZeroBytes(202);
                }
            } else {
                mplew.writeZeroBytes(202);
            }

        }

        return mplew.getPacket();
    }

    public static byte[] expeditionError(int errcode, String name) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.EXPEDITION_OPERATION.getValue());
        mplew.write(87);

        mplew.writeInt(errcode);
        mplew.writeMapleAsciiString(name);

        return mplew.getPacket();
    }

    public static byte[] expeditionJoined(String name) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.EXPEDITION_OPERATION.getValue());
        mplew.write(74);
        mplew.writeMapleAsciiString(name);

        return mplew.getPacket();
    }

    public static byte[] expeditionLeft(int code, String name) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.EXPEDITION_OPERATION.getValue());

        mplew.write(code);
        mplew.writeMapleAsciiString(name);

        return mplew.getPacket();
    }

    public static byte[] expeditionMessage(int code) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.EXPEDITION_OPERATION.getValue());

        mplew.write(code);

        return mplew.getPacket();
    }

    public static byte[] expeditionLeaderChanged(int newLeader) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.EXPEDITION_OPERATION.getValue());
        mplew.write(83);
        mplew.writeInt(newLeader);
        return mplew.getPacket();
    }

    public static byte[] expeditionUpdate(int partyIndex, MapleParty party) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.EXPEDITION_OPERATION.getValue());
        mplew.write(84);
        mplew.writeInt(0);
        mplew.writeInt(partyIndex);
        if (party == null) {
            mplew.writeZeroBytes(202);
        } else {
            addPartyStatus(-1, party, mplew, false, true);
        }
        return mplew.getPacket();
    }

    public static byte[] expeditionInvite(MapleCharacter from, int exped) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.EXPEDITION_OPERATION.getValue());
        mplew.write(86);
        mplew.writeInt(from.getLevel());
        mplew.writeInt(from.getJob());
        mplew.writeMapleAsciiString(from.getName());
        mplew.writeInt(exped);

        return mplew.getPacket();
    }

    public static byte[] sidekickInvite(MapleCharacter from) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SIDEKICK_OPERATION.getValue());
        mplew.write(65);
        mplew.writeInt(from.getId());
        mplew.writeMapleAsciiString(from.getName());
        mplew.writeInt(from.getLevel());
        mplew.writeInt(from.getJob());
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] disbandSidekick(MapleSidekick s) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SIDEKICK_OPERATION.getValue());
        mplew.write(75);
        mplew.writeInt(s.getId());
        mplew.writeInt(s.getCharacter(0).getId());
        mplew.write(0);
        mplew.writeInt(s.getCharacter(1).getId());

        return mplew.getPacket();
    }

    public static byte[] updateSidekick(MapleCharacter first, MapleSidekick s, boolean f) {
        if (ServerProperties.ShowPacket()) {
            log.info("调用: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SIDEKICK_OPERATION.getValue());
        mplew.write(f ? 78 : 70);
        MapleSidekickCharacter second = s.getCharacter(s.getCharacter(0).getId() == first.getId() ? 1 : 0);
        boolean online = first.getMap().getCharacterById(second.getId()) != null;
        mplew.writeInt(s.getId());
        if (f) {
            mplew.writeMapleAsciiString(second.getName());
        }
        List<String> msg = s.getSidekickMsg(online);
        mplew.writeInt(msg.size());
        for (String m : msg) {
            mplew.writeMapleAsciiString(m);
        }
        mplew.writeInt(first.getId());
        mplew.writeInt(second.getId());
        mplew.writeAsciiString(first.getName(), 13);
        mplew.writeAsciiString(second.getName(), 13);
        mplew.writeInt(first.getJob());
        mplew.writeInt(second.getJobId());
        mplew.writeInt(first.getLevel());
        mplew.writeInt(second.getLevel());
        mplew.writeInt(first.getClient().getChannel() - 1);
        mplew.writeInt(online ? first.getClient().getChannel() - 1 : 0);
        mplew.writeLong(0L);
        mplew.writeInt(first.getId());
        if (f) {
            mplew.writeInt(first.getId());
        }
        mplew.writeInt(second.getId());
        if (!f) {
            mplew.writeInt(first.getId());
        }
        mplew.writeInt(first.getMapId());
        mplew.writeInt(online ? first.getMapId() : 999999999);
        mplew.writeInt(1);
        mplew.write(Math.abs(first.getLevel() - second.getLevel()));
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(2147483647);
        mplew.writeInt(1);

        return mplew.getPacket();
    }
}