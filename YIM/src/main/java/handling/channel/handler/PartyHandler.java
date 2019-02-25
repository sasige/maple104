package handling.channel.handler;

import client.MapleCharacter;
import client.MapleClient;
import constants.GameConstants;
import handling.channel.ChannelServer;
import handling.channel.PlayerStorage;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import handling.world.PartyOperation;
import handling.world.World;
import handling.world.World.Find;
import handling.world.World.Party;
import handling.world.World.Sidekick;
import handling.world.exped.ExpeditionType;
import handling.world.exped.MapleExpedition;
import handling.world.exped.PartySearch;
import handling.world.exped.PartySearchType;
import handling.world.sidekick.MapleSidekick;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import scripting.EventInstanceManager;
import server.maps.Event_DojoAgent;
import server.maps.Event_PyramidSubway;
import server.maps.FieldLimitType;
import server.maps.MapleMap;
import server.quest.MapleQuest;
import tools.MaplePacketCreator;
import tools.data.LittleEndianAccessor;
import tools.packet.PartyPacket;

public class PartyHandler {

    public static void DenyPartyRequest(LittleEndianAccessor slea, MapleClient c) {
        int action = slea.readByte();
        int partyid = slea.readInt();
        if ((c.getPlayer().getParty() == null) && (c.getPlayer().getQuestNoAdd(MapleQuest.getInstance(122901)) == null)) {
            MapleParty party = World.Party.getParty(partyid);
            if (party != null) {
                if (party.getExpeditionId() > 0) {
                    c.getPlayer().dropMessage(5, "加入远征队伍的状态下无法进行此操作。");
                    return;
                }
                switch (action) {
                    case 26:
                        break;
                    case 30:
                        MapleCharacter cfrom = c.getChannelServer().getPlayerStorage().getCharacterById(party.getLeader().getId());
                        if (cfrom == null) {
                            break;
                        }
                        cfrom.getClient().getSession().write(PartyPacket.partyStatusMessage(new StringBuilder().append(c.getPlayer().getName()).append("玩家拒绝了组队招待。").toString()));
                        break;
                    case 31:
                        if (party.getMembers().size() < 6) {
                            c.getPlayer().setParty(party);
                            World.Party.updateParty(partyid, PartyOperation.加入队伍, new MaplePartyCharacter(c.getPlayer()));
                            c.getPlayer().receivePartyMemberHP();
                            c.getPlayer().updatePartyMemberHP();
                        } else {
                            c.getSession().write(PartyPacket.partyStatusMessage(17));
                        }
                        break;
                    default:
                        System.out.println(new StringBuilder().append("第二方收到组队邀请处理( ").append(action).append(" ) 未知.").toString());
                }
            } else {
                c.getPlayer().dropMessage(5, "要参加的队伍不存在。");
            }
        } else {
            c.getPlayer().dropMessage(5, "您已经有一个组队，无法加入其他组队!");
        }
    }

    public static void PartyOperation(LittleEndianAccessor slea, MapleClient c) {
        int operation = slea.readByte();
        MapleParty party = c.getPlayer().getParty();
        MaplePartyCharacter partyplayer = new MaplePartyCharacter(c.getPlayer());
        switch (operation) {
            case 1:
                if (party == null) {
                    party = World.Party.createParty(partyplayer);
                    c.getPlayer().setParty(party);
                    c.getSession().write(PartyPacket.partyCreated(party.getId()));
                } else {
                    if (party.getExpeditionId() > 0) {
                        c.getPlayer().dropMessage(5, "加入远征队伍的状态下无法进行此操作。");
                        return;
                    }
                    if ((partyplayer.equals(party.getLeader())) && (party.getMembers().size() == 1)) {
                        c.getSession().write(PartyPacket.partyCreated(party.getId()));
                    } else {
                        c.getPlayer().dropMessage(5, "你已经存在一个队伍中，无法创建！");
                    }
                }
                break;
            case 2:
                if (party == null) {
                    break;
                }
                if (party.getExpeditionId() > 0) {
                    c.getPlayer().dropMessage(5, "加入远征队伍的状态下无法进行此操作。");
                    return;
                }
                if (partyplayer.equals(party.getLeader())) {
                    if (GameConstants.isDojo(c.getPlayer().getMapId())) {
                        Event_DojoAgent.failed(c.getPlayer());
                    }
                    if (c.getPlayer().getPyramidSubway() != null) {
                        c.getPlayer().getPyramidSubway().fail(c.getPlayer());
                    }
                    World.Party.updateParty(party.getId(), PartyOperation.解散队伍, partyplayer);
                    if (c.getPlayer().getEventInstance() != null) {
                        c.getPlayer().getEventInstance().disbandParty();
                    }
                } else {
                    if (GameConstants.isDojo(c.getPlayer().getMapId())) {
                        Event_DojoAgent.failed(c.getPlayer());
                    }
                    if (c.getPlayer().getPyramidSubway() != null) {
                        c.getPlayer().getPyramidSubway().fail(c.getPlayer());
                    }
                    World.Party.updateParty(party.getId(), PartyOperation.离开队伍, partyplayer);
                    if (c.getPlayer().getEventInstance() != null) {
                        c.getPlayer().getEventInstance().leftParty(c.getPlayer());
                    }
                }
                c.getPlayer().setParty(null);
                break;
            case 3:
                int partyid = slea.readInt();
                if (party == null) {
                    party = World.Party.getParty(partyid);
                    if (party != null) {
                        if (party.getExpeditionId() > 0) {
                            c.getPlayer().dropMessage(5, "加入远征队伍的状态下无法进行此操作。");
                            return;
                        }
                        if ((party.getMembers().size() < 6) && (c.getPlayer().getQuestNoAdd(MapleQuest.getInstance(122901)) == null)) {
                            c.getPlayer().setParty(party);
                            World.Party.updateParty(party.getId(), PartyOperation.加入队伍, partyplayer);
                            c.getPlayer().receivePartyMemberHP();
                            c.getPlayer().updatePartyMemberHP();
                        } else {
                            c.getSession().write(PartyPacket.partyStatusMessage(17));
                        }
                    } else {
                        c.getPlayer().dropMessage(5, "要加入的队伍不存在");
                    }
                } else {
                    c.getPlayer().dropMessage(5, "您已经有一个组队，无法加入其他组队!");
                }
                break;
            case 4:
                if (party == null) {
                    party = World.Party.createParty(partyplayer);
                    c.getPlayer().setParty(party);
                    c.getSession().write(PartyPacket.partyCreated(party.getId()));
                }

                String theName = slea.readMapleAsciiString();
                int theCh = World.Find.findChannel(theName);
                if (theCh > 0) {
                    MapleCharacter invited = ChannelServer.getInstance(theCh).getPlayerStorage().getCharacterByName(theName);
                    if ((invited != null) && (invited.getParty() == null) && (invited.getQuestNoAdd(MapleQuest.getInstance(122901)) == null)) {
                        if (party.getExpeditionId() > 0) {
                            c.getPlayer().dropMessage(5, "加入远征队伍的状态下无法进行此操作。");
                            return;
                        }
                        if (party.getMembers().size() < 6) {
                            c.getSession().write(PartyPacket.partyStatusMessage(26, invited.getName()));
                            invited.getClient().getSession().write(PartyPacket.partyInvite(c.getPlayer()));
                        } else {
                            c.getSession().write(PartyPacket.partyStatusMessage(16));
                        }
                    } else {
                        c.getSession().write(PartyPacket.partyStatusMessage(17));
                    }
                } else {
                    c.getPlayer().dropMessage(-9, new StringBuilder().append("在当前服务器找不到..'").append(theName).append("'。").toString());
                }

                break;
            case 5:
                if ((party == null) || (partyplayer == null) || (!partyplayer.equals(party.getLeader()))) {
                    break;
                }
                if (party.getExpeditionId() > 0) {
                    c.getPlayer().dropMessage(5, "加入远征队伍的状态下无法进行此操作。");
                    return;
                }
               final MaplePartyCharacter expelled = party.getMemberById(slea.readInt());
                if (expelled != null) {
                    if ((GameConstants.isDojo(c.getPlayer().getMapId())) && (expelled.isOnline())) {
                        Event_DojoAgent.failed(c.getPlayer());
                    }
                    if ((c.getPlayer().getPyramidSubway() != null) && (expelled.isOnline())) {
                        c.getPlayer().getPyramidSubway().fail(c.getPlayer());
                    }
                    World.Party.updateParty(party.getId(), PartyOperation.驱逐成员, expelled);
                    if (c.getPlayer().getEventInstance() != null) {
                        if (expelled.isOnline()) {
                            c.getPlayer().getEventInstance().disbandParty();
                        }
                    }
                }
                break;
            case 6:
                if (party == null) {
                    break;
                }
                if (party.getExpeditionId() > 0) {
                    c.getPlayer().dropMessage(5, "加入远征队伍的状态下无法进行此操作。");
                    return;
                }
                MaplePartyCharacter newleader = party.getMemberById(slea.readInt());
                if ((newleader != null) && (partyplayer.equals(party.getLeader()))) {
                    World.Party.updateParty(party.getId(), PartyOperation.改变队长, newleader);
                }
                break;
            case 7:
                if (party != null) {
                    if ((c.getPlayer().getEventInstance() != null) || (c.getPlayer().getPyramidSubway() != null) || (party.getExpeditionId() > 0) || (GameConstants.isDojo(c.getPlayer().getMapId()))) {
                        c.getPlayer().dropMessage(5, "加入远征队伍的状态下无法进行此操作。");
                        return;
                    }
                    if (partyplayer.equals(party.getLeader())) {
                        World.Party.updateParty(party.getId(), PartyOperation.解散队伍, partyplayer);
                    } else {
                        World.Party.updateParty(party.getId(), PartyOperation.离开队伍, partyplayer);
                    }
                    c.getPlayer().setParty(null);
                }
                int partyid_ = slea.readInt();
                if (!GameConstants.GMS) {
                    break;
                }
                party = World.Party.getParty(partyid_);
                if ((party == null) || (party.getMembers().size() >= 6)) {
                    break;
                }
                if (party.getExpeditionId() > 0) {
                    c.getPlayer().dropMessage(5, "加入远征队伍的状态下无法进行此操作。");
                    return;
                }
                MapleCharacter cfrom = c.getPlayer().getMap().getCharacterById(party.getLeader().getId());
                if ((cfrom != null) && (cfrom.getQuestNoAdd(MapleQuest.getInstance(GameConstants.PARTY_REQUEST)) == null)) {
                    c.getSession().write(PartyPacket.partyStatusMessage(50, c.getPlayer().getName()));
                    cfrom.getClient().getSession().write(PartyPacket.partyRequestInvite(c.getPlayer()));
                } else {
                    c.getPlayer().dropMessage(5, "Player was not found or player is not accepting party requests.");
                }
                break;
            case 8:
                if (slea.readByte() > 0) {
                    c.getPlayer().getQuestRemove(MapleQuest.getInstance(GameConstants.PARTY_REQUEST));
                } else {
                    c.getPlayer().getQuestNAdd(MapleQuest.getInstance(GameConstants.PARTY_REQUEST));
                }
                break;
            default:
                System.out.println(new StringBuilder().append("组队邀请处理( ").append(operation).append(" ) 未知.").toString());
        }
    }

    public static void AllowPartyInvite(LittleEndianAccessor slea, MapleClient c) {
        if (slea.readByte() > 0) {
            c.getPlayer().getQuestRemove(MapleQuest.getInstance(GameConstants.PARTY_INVITE));
        } else {
            c.getPlayer().getQuestNAdd(MapleQuest.getInstance(GameConstants.PARTY_INVITE));
        }
    }

    public static void DenySidekickRequest(LittleEndianAccessor slea, MapleClient c) {
        int action = slea.readByte();
        int cid = slea.readInt();
        if ((c.getPlayer().getSidekick() == null) && (action == 90)) {
            MapleCharacter party = c.getPlayer().getMap().getCharacterById(cid);
            if (party != null) {
                if ((party.getSidekick() != null) || (!MapleSidekick.checkLevels(c.getPlayer().getLevel(), party.getLevel()))) {
                    return;
                }
                int sid = World.Sidekick.createSidekick(c.getPlayer().getId(), party.getId());
                if (sid <= 0) {
                    c.getPlayer().dropMessage(5, "Please try again.");
                } else {
                    MapleSidekick s = World.Sidekick.getSidekick(sid);
                    c.getPlayer().setSidekick(s);
                    c.getSession().write(PartyPacket.updateSidekick(c.getPlayer(), s, true));
                    party.setSidekick(s);
                    party.getClient().getSession().write(PartyPacket.updateSidekick(party, s, true));
                }
            } else {
                c.getPlayer().dropMessage(5, "The sidekick you are trying to join does not exist");
            }
        }
    }

    public static void SidekickOperation(LittleEndianAccessor slea, MapleClient c) {
        int operation = slea.readByte();
        switch (operation) {
            case 65:
                if (c.getPlayer().getSidekick() != null) {
                    break;
                }
                MapleCharacter other = c.getPlayer().getMap().getCharacterByName(slea.readMapleAsciiString());
                if ((other.getSidekick() == null) && (MapleSidekick.checkLevels(c.getPlayer().getLevel(), other.getLevel()))) {
                    other.getClient().getSession().write(PartyPacket.sidekickInvite(c.getPlayer()));
                    c.getPlayer().dropMessage(1, new StringBuilder().append("You have sent the sidekick invite to ").append(other.getName()).append(".").toString());
                }
                break;
            case 63:
                if (c.getPlayer().getSidekick() == null) {
                    break;
                }
                c.getPlayer().getSidekick().eraseToDB();
        }
    }

    public static void PartySearchStart(LittleEndianAccessor slea, MapleClient c) {
        if ((c.getPlayer().isInBlockedMap()) || (FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit()))) {
            c.getPlayer().dropMessage(5, "You may not do party search here.");
            return;
        }
        if (GameConstants.GMS) {
            c.getSession().write(MaplePacketCreator.showMemberSearch(c.getPlayer().getMap().getCharactersThreadsafe()));
            return;
        }
        if (c.getPlayer().getParty() == null) {
            MapleParty party = World.Party.createParty(new MaplePartyCharacter(c.getPlayer()));
            c.getPlayer().setParty(party);
            c.getSession().write(PartyPacket.partyCreated(party.getId()));
        } else if (c.getPlayer().getParty().getExpeditionId() > 0) {
            c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
            return;
        }

        int min = slea.readInt();
        int max = slea.readInt();
        int members = slea.readInt();
        int jobs = slea.readInt();
        List jobsList = new ArrayList();
        if ((max <= min) || (max - min > 30) || (members > 6) || (min > c.getPlayer().getLevel()) || (max < c.getPlayer().getLevel()) || (jobs == 0)) {
            c.getPlayer().dropMessage(1, "An error occurred.");
            return;
        }

        if ((jobs & 0x1) != 0) {
            c.getPlayer().startPartySearch(jobsList, max, min, members);
            return;
        }
        if ((jobs & 0x2) != 0) {
            jobsList.add(0);
            jobsList.add(1);
            jobsList.add(1000);
            jobsList.add(2000);
            jobsList.add(2001);
            jobsList.add(3000);
        }
        if ((jobs & 0x4) != 0) {
            jobsList.add(2100);
            jobsList.add(2110);
            jobsList.add(2111);
            jobsList.add(2112);
        }
        if ((jobs & 0x8) != 0) {
            jobsList.add(2200);
            jobsList.add(2210);
            jobsList.add(2211);
            jobsList.add(2212);
            jobsList.add(2213);
            jobsList.add(2214);
            jobsList.add(2215);
            jobsList.add(2216);
            jobsList.add(2217);
            jobsList.add(2218);
        }
        if ((jobs & 0x10) != 0) {
            jobsList.add(100);
        }
        if ((jobs & 0x20) != 0) {
            jobsList.add(110);
            jobsList.add(111);
            jobsList.add(112);
        }
        if ((jobs & 0x40) != 0) {
            jobsList.add(120);
            jobsList.add(121);
            jobsList.add(122);
        }
        if ((jobs & 0x80) != 0) {
            jobsList.add(130);
            jobsList.add(131);
            jobsList.add(132);
        }
        if ((jobs & 0x100) != 0) {
            jobsList.add(1100);
            jobsList.add(1110);
            jobsList.add(1111);
            jobsList.add(1112);
        }
        if ((jobs & 0x200) != 0) {
            jobsList.add(200);
        }
        if ((jobs & 0x400) != 0) {
            jobsList.add(210);
            jobsList.add(211);
            jobsList.add(212);
        }
        if ((jobs & 0x800) != 0) {
            jobsList.add(220);
            jobsList.add(221);
            jobsList.add(222);
        }
        if ((jobs & 0x1000) != 0) {
            jobsList.add(230);
            jobsList.add(231);
            jobsList.add(232);
        }
        if ((jobs & 0x2000) != 0) {
            jobsList.add(1200);
            jobsList.add(1210);
            jobsList.add(1211);
            jobsList.add(1212);
        }
        if ((jobs & 0x4000) != 0) {
            jobsList.add(3200);
            jobsList.add(3210);
            jobsList.add(3211);
            jobsList.add(3212);
        }
        if ((jobs & 0x8000) != 0) {
            jobsList.add(500);
            jobsList.add(501);
        }
        if ((jobs & 0x10000) != 0) {
            jobsList.add(510);
            jobsList.add(511);
            jobsList.add(512);
        }
        if ((jobs & 0x20000) != 0) {
            jobsList.add(520);
            jobsList.add(521);
            jobsList.add(522);
        }
        if ((jobs & 0x40000) != 0) {
            jobsList.add(1500);
            jobsList.add(1510);
            jobsList.add(1511);
            jobsList.add(1512);
        }
        if ((jobs & 0x80000) != 0) {
            jobsList.add(3500);
            jobsList.add(3510);
            jobsList.add(3511);
            jobsList.add(3512);
        }
        if ((jobs & 0x100000) != 0) {
            jobsList.add(400);
        }

        if ((jobs & 0x400000) != 0) {
            jobsList.add(410);
            jobsList.add(411);
            jobsList.add(412);
        }
        if ((jobs & 0x800000) != 0) {
            jobsList.add(420);
            jobsList.add(421);
            jobsList.add(422);
        }
        if ((jobs & 0x1000000) != 0) {
            jobsList.add(1400);
            jobsList.add(1410);
            jobsList.add(1411);
            jobsList.add(1412);
        }
        if ((jobs & 0x2000000) != 0) {
            jobsList.add(430);
            jobsList.add(431);
            jobsList.add(432);
            jobsList.add(433);
            jobsList.add(434);
        }
        if ((jobs & 0x4000000) != 0) {
            jobsList.add(300);
        }
        if ((jobs & 0x8000000) != 0) {
            jobsList.add(310);
            jobsList.add(311);
            jobsList.add(312);
        }
        if ((jobs & 0x10000000) != 0) {
            jobsList.add(320);
            jobsList.add(321);
            jobsList.add(322);
        }
        if ((jobs & 0x20000000) != 0) {
            jobsList.add(1300);
            jobsList.add(1310);
            jobsList.add(1311);
            jobsList.add(1312);
        }
        if ((jobs & 0x40000000) != 0) {
            jobsList.add(3300);
            jobsList.add(3310);
            jobsList.add(3311);
            jobsList.add(3312);
        }
        if (jobsList.size() > 0) {
            c.getPlayer().startPartySearch(jobsList, max, min, members);
        } else {
            c.getPlayer().dropMessage(1, "An error occurred.");
        }
    }

    public static void PartySearchStop(LittleEndianAccessor slea, MapleClient c) {
        if (GameConstants.GMS) {
            List parties = new ArrayList();
            for (MapleCharacter chr : c.getPlayer().getMap().getCharactersThreadsafe()) {
                if ((chr.getParty() != null) && (chr.getParty().getId() != c.getPlayer().getParty().getId()) && (!parties.contains(chr.getParty()))) {
                    parties.add(chr.getParty());
                }
            }
            c.getSession().write(MaplePacketCreator.showPartySearch(parties));
        }
    }

    public static void PartyListing(LittleEndianAccessor slea, MapleClient c) {
        int mode = slea.readByte();
        PartySearchType pst;
        switch (mode) {
            case -105:
            case -97:
            case 98:
            case 159:
                pst = PartySearchType.getById(slea.readInt());
                if ((pst == null) || (c.getPlayer().getLevel() > pst.maxLevel) || (c.getPlayer().getLevel() < pst.minLevel)) {
                    return;
                }
                if ((c.getPlayer().getParty() == null) && (World.Party.searchParty(pst).size() < 10)) {
                    MapleParty party = World.Party.createParty(new MaplePartyCharacter(c.getPlayer()), pst.id);
                    c.getPlayer().setParty(party);
                    c.getSession().write(PartyPacket.partyCreated(party.getId()));
                    PartySearch ps = new PartySearch(slea.readMapleAsciiString(), pst.exped ? party.getExpeditionId() : party.getId(), pst);
                    World.Party.addSearch(ps);
                    if (pst.exped) {
                        c.getSession().write(PartyPacket.expeditionStatus(World.Party.getExped(party.getExpeditionId()), true));
                    }
                    c.getSession().write(PartyPacket.partyListingAdded(ps));
                } else {
                    c.getPlayer().dropMessage(1, "Unable to create. Please leave the party.");
                }
                break;
            case 99:
                c.getPlayer().dropMessage(1, "暂时不支持取消.");
                break;
            case -103:
            case -95:
            case 100:
            case 161:
                pst = PartySearchType.getById(slea.readInt());
                if ((pst == null) || (c.getPlayer().getLevel() > pst.maxLevel) || (c.getPlayer().getLevel() < pst.minLevel)) {
                    return;
                }
                c.getSession().write(PartyPacket.getPartyListing(pst));
                break;
            case -102:
            case -94:
            case 101:
            case 162:
                break;
            case -101:
            case -93:
            case 102:
            case 163:
                MapleParty party = c.getPlayer().getParty();
                MaplePartyCharacter partyplayer = new MaplePartyCharacter(c.getPlayer());
                if (party != null) {
                    break;
                }
                int theId = slea.readInt();
                party = World.Party.getParty(theId);
                if (party != null) {
                    PartySearch ps = World.Party.getSearchByParty(party.getId());
                    if ((ps != null) && (c.getPlayer().getLevel() <= ps.getType().maxLevel) && (c.getPlayer().getLevel() >= ps.getType().minLevel) && (party.getMembers().size() < 6)) {
                        c.getPlayer().setParty(party);
                        World.Party.updateParty(party.getId(), PartyOperation.加入队伍, partyplayer);
                        c.getPlayer().receivePartyMemberHP();
                        c.getPlayer().updatePartyMemberHP();
                    } else {
                        c.getSession().write(PartyPacket.partyStatusMessage(17));
                    }
                } else {
                    MapleExpedition exped = World.Party.getExped(theId);
                    if (exped != null) {
                        PartySearch ps = World.Party.getSearchByExped(exped.getId());
                        if ((ps != null) && (c.getPlayer().getLevel() <= ps.getType().maxLevel) && (c.getPlayer().getLevel() >= ps.getType().minLevel) && (exped.getAllMembers() < exped.getType().maxMembers)) {
                            int partyId = exped.getFreeParty();
                            if (partyId < 0) {
                                c.getSession().write(PartyPacket.partyStatusMessage(17));
                            } else if (partyId == 0) {
                                party = World.Party.createPartyAndAdd(partyplayer, exped.getId());
                                c.getPlayer().setParty(party);
                                c.getSession().write(PartyPacket.partyCreated(party.getId()));
                                c.getSession().write(PartyPacket.expeditionStatus(exped, true));
                                World.Party.expedPacket(exped.getId(), PartyPacket.expeditionJoined(c.getPlayer().getName()), null);
                                World.Party.expedPacket(exped.getId(), PartyPacket.expeditionUpdate(exped.getIndex(party.getId()), party), null);
                            } else {
                                c.getPlayer().setParty(World.Party.getParty(partyId));
                                World.Party.updateParty(partyId, PartyOperation.加入队伍, partyplayer);
                                c.getPlayer().receivePartyMemberHP();
                                c.getPlayer().updatePartyMemberHP();
                                c.getSession().write(PartyPacket.expeditionStatus(exped, true));
                                World.Party.expedPacket(exped.getId(), PartyPacket.expeditionJoined(c.getPlayer().getName()), null);
                            }
                        } else {
                            c.getSession().write(PartyPacket.expeditionError(0, c.getPlayer().getName()));
                        }
                    }
                }
                break;
            default:
                if (!c.getPlayer().isGM()) {
                    break;
                }
                System.out.println(new StringBuilder().append("Unknown PartyListing : ").append(mode).append("\n").append(slea).toString());
        }
    }

    public static void Expedition(LittleEndianAccessor slea, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if ((player == null) || (player.getMap() == null)) {
            return;
        }
        int mode = slea.readByte();
        String name;
        MapleParty part;
        int cid;
        Iterator i$;
        int partyIndexTo;
        switch (mode) {
            case 63:
            case 119: {
                ExpeditionType et = ExpeditionType.getById(slea.readInt());
                if ((et != null) && (player.getParty() == null) && (player.getLevel() <= et.maxLevel) && (player.getLevel() >= et.minLevel)) {
                    MapleParty party = World.Party.createParty(new MaplePartyCharacter(player), et.exped);
                    player.setParty(party);
                    c.getSession().write(PartyPacket.partyCreated(party.getId()));
                    c.getSession().write(PartyPacket.expeditionStatus(World.Party.getExped(party.getExpeditionId()), true));
                } else {
                    c.getSession().write(PartyPacket.expeditionError(0, "这个远征模式"));
                }
                break;
            }
            case 64:
            case 120: {
                name = slea.readMapleAsciiString();
                int theCh = World.Find.findChannel(name);
                if (player.isGM()) {
                    player.dropMessage(-11, new StringBuilder().append("远征队伍邀请 邀请玩家: ").append(name).append(" 是否找到玩家: ").append(theCh).toString());
                }
                if (theCh > 0) {
                    MapleCharacter invited = ChannelServer.getInstance(theCh).getPlayerStorage().getCharacterByName(name);
                    MapleParty party = c.getPlayer().getParty();
                    if ((invited != null) && (invited.getParty() == null) && (party != null) && (party.getExpeditionId() > 0)) {
                        MapleExpedition me = World.Party.getExped(party.getExpeditionId());
                        if (player.isGM()) {
                            player.dropMessage(-11, new StringBuilder().append("远征队伍邀请 me :").append(me).append(" 人数 ").append(me.getAllMembers() < me.getType().maxMembers).append(" 邀请玩家 最小: ").append(invited.getLevel() <= me.getType().maxLevel).append(" 最大: ").append(invited.getLevel() >= me.getType().minLevel).toString());
                        }
                        if ((me != null) && (me.getAllMembers() < me.getType().maxMembers) && (invited.getLevel() <= me.getType().maxLevel) && (invited.getLevel() >= me.getType().minLevel)) {
                            if (player.isGM()) {
                                player.dropMessage(-11, "远征队伍邀请 开始邀请");
                            }
                            c.getSession().write(PartyPacket.expeditionError(7, invited.getName()));
                            invited.getClient().getSession().write(PartyPacket.expeditionInvite(player, me.getType().exped));
                        } else {
                            if (player.isGM()) {
                                player.dropMessage(-11, "远征队伍邀请 错误 - 3");
                            }
                            c.getSession().write(PartyPacket.expeditionError(3, invited.getName()));
                        }
                    } else {
                        if (player.isGM()) {
                            player.dropMessage(-11, "远征队伍邀请 错误 - 2");
                        }
                        c.getSession().write(PartyPacket.expeditionError(2, name));
                    }
                } else {
                    if (player.isGM()) {
                        player.dropMessage(-11, "远征队伍邀请 错误 - 0");
                    }
                    c.getSession().write(PartyPacket.expeditionError(0, name));
                }
                break;
            }
            case 65:
            case 121: {
                name = slea.readMapleAsciiString();
                int action = slea.readInt();
                int theChh = World.Find.findChannel(name);
                if (player.isGM()) {
                    player.dropMessage(-11, new StringBuilder().append("接受远征邀请 接受玩家: ").append(name).append(" 是否找到玩家: ").append(theChh).append(" 操作模式: ").append(action).toString());
                }
                if (theChh <= 0) {
                    break;
                }
                MapleCharacter cfrom = ChannelServer.getInstance(theChh).getPlayerStorage().getCharacterByName(name);
                if (player.isGM()) {
                    player.dropMessage(-11, new StringBuilder().append("接受远征邀请 是否找的到cfrom: ").append(cfrom).toString());
                }
                if ((cfrom != null) && (cfrom.getParty() != null) && (cfrom.getParty().getExpeditionId() > 0)) {
                    MapleParty party = cfrom.getParty();
                    MapleExpedition exped = World.Party.getExped(party.getExpeditionId());
                    if (player.isGM()) {
                        player.dropMessage(-11, new StringBuilder().append("接受远征邀请 MapleExpedition: ").append(exped).toString());
                    }
                    if ((exped != null) && (action == 8)) {
                        if ((player.getLevel() <= exped.getType().maxLevel) && (player.getLevel() >= exped.getType().minLevel) && (exped.getAllMembers() < exped.getType().maxMembers)) {
                            int partyId = exped.getFreeParty();
                            if (player.isGM()) {
                                player.dropMessage(-11, new StringBuilder().append("接受远征邀请 开始接受远征 - partyId ").append(partyId).toString());
                            }
                            if (partyId < 0) {
                                c.getSession().write(PartyPacket.partyStatusMessage(17));
                            } else if (partyId == 0) {
                                if (player.isGM()) {
                                    player.dropMessage(-11, "接受远征邀请 开始接受远征 - partyId == 0");
                                }
                                party = World.Party.createPartyAndAdd(new MaplePartyCharacter(player), exped.getId());
                                player.setParty(party);
                                c.getSession().write(PartyPacket.partyCreated(party.getId()));
                                c.getSession().write(PartyPacket.expeditionStatus(exped, true));
                                World.Party.expedPacket(exped.getId(), PartyPacket.expeditionJoined(player.getName()), null);
                                World.Party.expedPacket(exped.getId(), PartyPacket.expeditionUpdate(exped.getIndex(party.getId()), party), null);
                            } else {
                                if (player.isGM()) {
                                    player.dropMessage(-11, "接受远征邀请 开始接受远征 - 开始加入");
                                }
                                player.setParty(World.Party.getParty(partyId));
                                World.Party.updateParty(partyId, PartyOperation.加入队伍, new MaplePartyCharacter(player));
                                player.receivePartyMemberHP();
                                player.updatePartyMemberHP();
                                c.getSession().write(PartyPacket.expeditionStatus(exped, true));
                                World.Party.expedPacket(exped.getId(), PartyPacket.expeditionJoined(player.getName()), null);
                            }
                        } else {
                            if (player.isGM()) {
                                player.dropMessage(-11, "接受远征邀请 开始接受远征 - 错误 - 3");
                            }
                            c.getSession().write(PartyPacket.expeditionError(3, cfrom.getName()));
                        }
                    } else if (action == 9) {
                        if (player.isGM()) {
                            player.dropMessage(-11, "接受远征邀请 开始接受远征 - action == 9");
                        }
                        cfrom.getClient().getSession().write(PartyPacket.partyStatusMessage(23, player.getName()));
                    }
                }
                break;
            }
            case 66:
            case 122: {
                part = player.getParty();
                if (player.isGM()) {
                    player.dropMessage(-11, new StringBuilder().append("离开远征队伍 - 开始操作 part ").append(part).toString());
                }
                if ((part == null) || (part.getExpeditionId() <= 0)) {
                    break;
                }
                MapleExpedition exped = World.Party.getExped(part.getExpeditionId());
                if (exped != null) {
                    if (GameConstants.isDojo(player.getMapId())) {
                        Event_DojoAgent.failed(player);
                    }
                    if (exped.getLeader() == player.getId()) {
                        if (player.isGM()) {
                            player.dropMessage(-11, "离开远征队伍 - 开始操作 - 是远征队长 - 解散远征队伍");
                        }
                        World.Party.disbandExped(exped.getId());
                        if (player.getEventInstance() != null) {
                            player.getEventInstance().disbandParty();
                        }
                    } else if (part.getLeader().getId() == player.getId()) {
                        if (player.isGM()) {
                            player.dropMessage(-11, "离开远征队伍 - 开始操作 - 是小组队长 - 解散远征小组");
                        }
                        World.Party.updateParty(part.getId(), PartyOperation.解散队伍, new MaplePartyCharacter(player));
                        if (player.getEventInstance() != null) {
                            player.getEventInstance().disbandParty();
                        }
                        if (player.isGM()) {
                            player.dropMessage(-5, "离开远征队伍 - 开始操作 - 是小组队长 - expeditionLeft 0x4E");
                        }

                        World.Party.expedPacket(exped.getId(), PartyPacket.expeditionLeft(78, player.getName()), null);
                    } else {
                        if (player.isGM()) {
                            player.dropMessage(-11, "离开远征队伍 - 开始操作 - 是队员 - 离开远征");
                        }
                        World.Party.updateParty(part.getId(), PartyOperation.离开队伍, new MaplePartyCharacter(player));
                        if (player.getEventInstance() != null) {
                            player.getEventInstance().leftParty(player);
                        }
                        if (player.isGM()) {
                            player.dropMessage(-5, "离开远征队伍 - 开始操作 - 是队员 - expeditionLeft 0x4E");
                        }

                        World.Party.expedPacket(exped.getId(), PartyPacket.expeditionLeft(78, player.getName()), null);
                    }
                    if (player.getPyramidSubway() != null) {
                        player.getPyramidSubway().fail(c.getPlayer());
                    }
                    player.setParty(null);
                }
                break;
            }
            case 67:
            case 123: {
                part = player.getParty();
                if (player.isGM()) {
                    player.dropMessage(-11, new StringBuilder().append("远征队伍驱除 - 开始操作 - part - ").append(part).toString());
                }
                if ((part == null) || (part.getExpeditionId() <= 0)) {
                    break;
                }
                MapleExpedition exped = Party.getExped(part.getExpeditionId());
                if ((exped != null) && (exped.getLeader() == player.getId())) {
                    cid = slea.readInt();
                    for (i$ = exped.getParties().iterator(); i$.hasNext();) {
                        int i = ((Integer) i$.next()).intValue();
                        MapleParty par = World.Party.getParty(i);
                        if (par != null) {
                            MaplePartyCharacter expelled = par.getMemberById(cid);
                            if (expelled != null) {
                                if ((expelled.isOnline()) && (GameConstants.isDojo(player.getMapId()))) {
                                    Event_DojoAgent.failed(player);
                                }
                                World.Party.updateParty(i, PartyOperation.驱逐成员, expelled);
                                if ((player.getEventInstance() != null)
                                        && (expelled.isOnline())) {
                                    player.getEventInstance().disbandParty();
                                }

                                if ((player.getPyramidSubway() != null) && (expelled.isOnline())) {
                                    player.getPyramidSubway().fail(player);
                                }
                                World.Party.expedPacket(exped.getId(), PartyPacket.expeditionLeft(80, expelled.getName()), null);
                                break;
                            }
                        }
                    }
                }
                break;
            }
            case 68:
            case 124: {
                part = player.getParty();
                if (player.isGM()) {
                    player.dropMessage(-11, new StringBuilder().append("改变远征队长 - 开始操作 - part - ").append(part).toString());
                }
                if ((part == null) || (part.getExpeditionId() <= 0)) {
                    break;
                }
                MapleExpedition exped = World.Party.getExped(part.getExpeditionId());
                if ((exped != null) && (exped.getLeader() == player.getId())) {
                    MaplePartyCharacter newleader = part.getMemberById(slea.readInt());
                    if (newleader != null) {
                        World.Party.updateParty(part.getId(), PartyOperation.改变队长, newleader);
                        exped.setLeader(newleader.getId());
                        World.Party.expedPacket(exped.getId(), PartyPacket.expeditionLeaderChanged(0), null);
                    }
                }
                break;
            }
            case 69:
            case 125:
                part = player.getParty();
                if (player.isGM()) {
                    player.dropMessage(-11, new StringBuilder().append("改变小组队长 - 开始操作 - part - ").append(part).toString());
                }
                if ((part == null) || (part.getExpeditionId() <= 0)) {
                    break;
                }
                MapleExpedition exped = World.Party.getExped(part.getExpeditionId());
                if ((exped != null) && (exped.getLeader() == player.getId())) {
                    cid = slea.readInt();
                    for (i$ = exped.getParties().iterator(); i$.hasNext();) {
                        int i = ((Integer) i$.next()).intValue();
                        MapleParty par = World.Party.getParty(i);
                        if (par != null) {
                            MaplePartyCharacter newleader = par.getMemberById(cid);
                            if ((newleader != null) && (par.getId() != part.getId())) {
                                World.Party.updateParty(par.getId(), PartyOperation.改变队长, newleader);
                            }
                        }
                    }
                }
                break;
            case 70:
            case 126:
                part = player.getParty();
                if (player.isGM()) {
                    player.dropMessage(-11, new StringBuilder().append("change party of diff player - 开始操作 - part - ").append(part).toString());
                }
                if ((part == null) || (part.getExpeditionId() <= 0)) {
                    break;
                }
                exped = World.Party.getExped(part.getExpeditionId());
                if ((exped != null) && (exped.getLeader() == player.getId())) {
                    partyIndexTo = slea.readInt();
                    if ((partyIndexTo < exped.getType().maxParty) && (partyIndexTo <= exped.getParties().size())) {
                        cid = slea.readInt();
                        for (i$ = exped.getParties().iterator(); i$.hasNext();) {
                            int i = ((Integer) i$.next()).intValue();
                            MapleParty par = World.Party.getParty(i);
                            if (par != null) {
                                MaplePartyCharacter expelled = par.getMemberById(cid);
                                if ((expelled != null) && (expelled.isOnline())) {
                                    MapleCharacter chr = World.getStorage(expelled.getChannel()).getCharacterById(expelled.getId());
                                    if (chr == null) {
                                        break;
                                    }
                                    if (partyIndexTo < exped.getParties().size()) {
                                        MapleParty party = World.Party.getParty(((Integer) exped.getParties().get(partyIndexTo)).intValue());
                                        if ((party == null) || (party.getMembers().size() >= 6)) {
                                            player.dropMessage(5, "Invalid party.");
                                            break;
                                        }
                                    }
                                    if (GameConstants.isDojo(player.getMapId())) {
                                        Event_DojoAgent.failed(player);
                                    }
                                    World.Party.updateParty(i, PartyOperation.驱逐成员, expelled);
                                    if (partyIndexTo < exped.getParties().size()) {
                                        MapleParty party = World.Party.getParty(((Integer) exped.getParties().get(partyIndexTo)).intValue());
                                        if ((party != null) && (party.getMembers().size() < 6)) {
                                            World.Party.updateParty(party.getId(), PartyOperation.加入队伍, expelled);
                                            chr.receivePartyMemberHP();
                                            chr.updatePartyMemberHP();
                                            chr.getClient().getSession().write(PartyPacket.expeditionStatus(exped, true));
                                        }
                                    } else {
                                        MapleParty party = World.Party.createPartyAndAdd(expelled, exped.getId());
                                        chr.setParty(party);
                                        chr.getClient().getSession().write(PartyPacket.partyCreated(party.getId()));
                                        chr.getClient().getSession().write(PartyPacket.expeditionStatus(exped, true));
                                        World.Party.expedPacket(exped.getId(), PartyPacket.expeditionUpdate(exped.getIndex(party.getId()), party), null);
                                    }
                                    if ((player.getEventInstance() != null)
                                            && (expelled.isOnline())) {
                                        player.getEventInstance().disbandParty();
                                    }

                                    if (player.getPyramidSubway() == null) {
                                        break;
                                    }
                                    player.getPyramidSubway().fail(c.getPlayer());
                                    break;
                                }
                            }
                        }
                    }

                }

                break;
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
            case 80:
            case 81:
            case 82:
            case 83:
            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 90:
            case 91:
            case 92:
            case 93:
            case 94:
            case 95:
            case 96:
            case 97:
            case 98:
            case 99:
            case 100:
            case 101:
            case 102:
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
            case 110:
            case 111:
            case 112:
            case 113:
            case 114:
            case 115:
            case 116:
            case 117:
            case 118:
            default:
                System.out.println(new StringBuilder().append("未知的远征队操作 : ").append(mode).append(" ").append(slea).toString());
        }
    }
}