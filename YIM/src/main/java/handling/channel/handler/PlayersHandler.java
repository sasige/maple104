package handling.channel.handler;

import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleDisease;
import client.MapleLieDetector;
import client.MapleQuestStatus;
import client.MapleStat;
import client.MonsterBook;
import client.PlayerStats;
import client.Skill;
import client.SkillFactory;
import client.anticheat.CheatTracker;
import client.anticheat.CheatingOffense;
import client.anticheat.ReportType;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.MapleRing;
import client.status.MonsterStatus;
import constants.GameConstants;
import handling.channel.ChannelServer;
import handling.channel.PlayerStorage;
import handling.world.World.Broadcast;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import scripting.EventInstanceManager;
import scripting.EventManager;
import scripting.EventScriptManager;
import scripting.ReactorScriptManager;
import server.CashShop;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import server.Randomizer;
import server.events.MapleCoconut;
import server.events.MapleCoconut.MapleCoconuts;
import server.events.MapleEventType;
import server.maps.FieldLimitType;
import server.maps.MapleDoor;
import server.maps.MapleMap;
import server.maps.MapleMapFactory;
import server.maps.MapleMapObject;
import server.maps.MapleMist;
import server.maps.MapleReactor;
import server.maps.MechDoor;
import server.quest.MapleQuest;
import tools.AttackPair;
import tools.FileoutputUtil;
import tools.MaplePacketCreator;
import tools.Pair;
import tools.Triple;
import tools.data.LittleEndianAccessor;
import tools.packet.UIPacket;

public class PlayersHandler {

    public static void Note(LittleEndianAccessor slea, MapleCharacter chr) {
        byte type = slea.readByte();
        switch (type) {
            case 0:
                String name = slea.readMapleAsciiString();
                String msg = slea.readMapleAsciiString();
                boolean fame = slea.readByte() > 0;
                slea.readInt();
                Item itemz = chr.getCashInventory().findByCashId((int) slea.readLong());
                if ((itemz == null) || (!itemz.getGiftFrom().equalsIgnoreCase(name)) || (!chr.getCashInventory().canSendNote(itemz.getUniqueId()))) {
                    return;
                }
                try {
                    chr.sendNote(name, msg, fame ? 1 : 0);
                    chr.getCashInventory().sendedNote(itemz.getUniqueId());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            case 1:
                int num = slea.readShort();
                int giveFame = slea.readByte();
                for (int i = 0; i < num; i++) {
                    int id = slea.readInt();
                    chr.deleteNote(id, giveFame);
                }
                break;
            default:
                System.out.println(new StringBuilder().append("Unhandled note action, ").append(type).append("").toString());
        }
    }

    public static void GiveFame(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        int who = slea.readInt();
        int mode = slea.readByte();

        int famechange = mode == 0 ? -1 : 1;
        MapleCharacter target = chr.getMap().getCharacterById(who);

        if ((target == null) || (target == chr)) {
            chr.getCheatTracker().registerOffense(CheatingOffense.FAMING_SELF, "不能对自身操作.");
            return;
        }
        if (chr.getLevel() < 15) {
            chr.getCheatTracker().registerOffense(CheatingOffense.FAMING_UNDER_15, "等级小于15级.");
            return;
        }
        switch (chr.canGiveFame(target)) {
            case OK:
                if (Math.abs(target.getFame() + famechange) <= 99999) {
                    target.addFame(famechange);
                    target.updateSingleStat(MapleStat.人气, target.getFame());
                }
                if (!chr.isGM()) {
                    chr.hasGivenFame(target);
                }
                c.getSession().write(MaplePacketCreator.giveFameResponse(mode, target.getName(), target.getFame()));
                target.getClient().getSession().write(MaplePacketCreator.receiveFame(mode, chr.getName()));
                break;
            case NOT_TODAY:
                c.getSession().write(MaplePacketCreator.giveFameErrorResponse(3));
                break;
            case NOT_THIS_MONTH:
                c.getSession().write(MaplePacketCreator.giveFameErrorResponse(4));
        }
    }

    public static void UseDoor(LittleEndianAccessor slea, MapleCharacter chr) {
        int oid = slea.readInt();
        boolean mode = slea.readByte() == 0;

        for (MapleMapObject obj : chr.getMap().getAllDoorsThreadsafe()) {
            MapleDoor door = (MapleDoor) obj;
            if (door.getOwnerId() == oid) {
                door.warp(chr, mode);
                break;
            }
        }
    }

    public static void UseMechDoor(LittleEndianAccessor slea, MapleCharacter chr) {
        int oid = slea.readInt();
        Point pos = slea.readPos();
        int mode = slea.readByte();
        chr.getClient().getSession().write(MaplePacketCreator.enableActions());
        for (MapleMapObject obj : chr.getMap().getAllMechDoorsThreadsafe()) {
            MechDoor door = (MechDoor) obj;
            if ((door.getOwnerId() == oid) && (door.getId() == mode)) {
                chr.checkFollow();
                chr.getMap().movePlayer(chr, pos);
                break;
            }
        }
    }

    public static void TransformPlayer(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        chr.updateTick(slea.readInt());
        byte slot = (byte) slea.readShort();
        int itemId = slea.readInt();
        String target = slea.readMapleAsciiString();
        Item toUse = c.getPlayer().getInventory(MapleInventoryType.USE).getItem((short) slot);
        if ((toUse == null) || (toUse.getQuantity() < 1) || (toUse.getItemId() != itemId)) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        switch (itemId) {
            case 2212000:
                MapleCharacter search_chr = chr.getMap().getCharacterByName(target);
                if (search_chr == null) {
                    break;
                }
                MapleItemInformationProvider.getInstance().getItemEffect(2210023).applyTo(search_chr);
                search_chr.dropMessage(6, new StringBuilder().append(chr.getName()).append(" has played a prank on you!").toString());
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, (short) slot, (short) 1, false);
        }
    }

    public static void HitReactor(LittleEndianAccessor slea, MapleClient c) {
        int oid = slea.readInt();
        int charPos = slea.readInt();
        short stance = slea.readShort();
        MapleReactor reactor = c.getPlayer().getMap().getReactorByOid(oid);

        if ((reactor == null) || (!reactor.isAlive())) {
            return;
        }
        reactor.hitReactor(charPos, stance, c);
    }

    public static void TouchReactor(LittleEndianAccessor slea, MapleClient c) {
        int oid = slea.readInt();
        boolean touched = (slea.available() == 0L) || (slea.readByte() > 0);
        MapleReactor reactor = c.getPlayer().getMap().getReactorByOid(oid);
        if ((!touched) || (reactor == null) || (!reactor.isAlive()) || (reactor.getTouch() == 0)) {
            return;
        }
        if (c.getPlayer().isAdmin()) {
            c.getPlayer().dropMessage(5, new StringBuilder().append("反应堆信息 - oid: ").append(oid).append(" Touch: ").append(reactor.getTouch()).append(" isTimerActive: ").append(reactor.isTimerActive()).append(" ReactorType: ").append(reactor.getReactorType()).toString());
        }
        if (reactor.getTouch() == 2) {
            ReactorScriptManager.getInstance().act(c, reactor);
        } else if ((reactor.getTouch() == 1) && (!reactor.isTimerActive())) {
            if (reactor.getReactorType() == 100) {
                int itemid = GameConstants.getCustomReactItem(reactor.getReactorId(), ((Integer) reactor.getReactItem().getLeft()).intValue());
                if (c.getPlayer().haveItem(itemid, ((Integer) reactor.getReactItem().getRight()).intValue())) {
                    if (reactor.getArea().contains(c.getPlayer().getTruePosition())) {
                        MapleInventoryManipulator.removeById(c, GameConstants.getInventoryType(itemid), itemid, ((Integer) reactor.getReactItem().getRight()).intValue(), true, false);
                        reactor.hitReactor(c);
                    } else {
                        c.getPlayer().dropMessage(5, "距离太远。请靠近后重新尝试。");
                    }
                } else {
                    c.getPlayer().dropMessage(5, "You don't have the item required.");
                }
            } else {
                reactor.hitReactor(c);
            }
        }
    }

    public static void hitCoconut(LittleEndianAccessor slea, MapleClient c) {
        int id = slea.readShort();
        String co = "coconut";
        MapleCoconut map = (MapleCoconut) c.getChannelServer().getEvent(MapleEventType.Coconut);
        if ((map == null) || (!map.isRunning())) {
            map = (MapleCoconut) c.getChannelServer().getEvent(MapleEventType.CokePlay);
            co = "coke cap";
            if ((map == null) || (!map.isRunning())) {
                return;
            }
        }

        MapleCoconut.MapleCoconuts nut = map.getCoconut(id);
        if ((nut == null) || (!nut.isHittable())) {
            return;
        }
        if (System.currentTimeMillis() < nut.getHitTime()) {
            return;
        }

        if ((nut.getHits() > 2) && (Math.random() < 0.4D) && (!nut.isStopped())) {
            nut.setHittable(false);
            if ((Math.random() < 0.01D) && (map.getStopped() > 0)) {
                nut.setStopped(true);
                map.stopCoconut();
                c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.hitCoconut(false, id, 1));
                return;
            }
            nut.resetHits();

            if ((Math.random() < 0.05D) && (map.getBombings() > 0)) {
                c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.hitCoconut(false, id, 2));
                map.bombCoconut();
            } else if (map.getFalling() > 0) {
                c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.hitCoconut(false, id, 3));
                map.fallCoconut();
                if (c.getPlayer().getTeam() == 0) {
                    map.addMapleScore();
                } else {
                    map.addStoryScore();
                }

                c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.coconutScore(map.getCoconutScore()));
            }
        } else {
            nut.hit();
            c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.hitCoconut(false, id, 1));
        }
    }

    public static void FollowRequest(LittleEndianAccessor slea, MapleClient c) {
        MapleCharacter tt = c.getPlayer().getMap().getCharacterById(slea.readInt());
        if (slea.readByte() > 0) {
            tt = c.getPlayer().getMap().getCharacterById(c.getPlayer().getFollowId());
            if ((tt != null) && (tt.getFollowId() == c.getPlayer().getId())) {
                tt.setFollowOn(true);
                c.getPlayer().setFollowOn(true);
            } else {
                c.getPlayer().checkFollow();
            }
            return;
        }
        if (slea.readByte() > 0) {
            tt = c.getPlayer().getMap().getCharacterById(c.getPlayer().getFollowId());
            if ((tt != null) && (tt.getFollowId() == c.getPlayer().getId()) && (c.getPlayer().isFollowOn())) {
                c.getPlayer().checkFollow();
            }
            return;
        }
        if ((tt != null) && (tt.getPosition().distanceSq(c.getPlayer().getPosition()) < 10000.0D) && (tt.getFollowId() == 0) && (c.getPlayer().getFollowId() == 0) && (tt.getId() != c.getPlayer().getId())) {
            tt.setFollowId(c.getPlayer().getId());
            tt.setFollowOn(false);
            tt.setFollowInitiator(false);
            c.getPlayer().setFollowOn(false);
            c.getPlayer().setFollowInitiator(false);
            tt.getClient().getSession().write(MaplePacketCreator.followRequest(c.getPlayer().getId()));
        } else {
            c.getSession().write(MaplePacketCreator.serverNotice(1, "距离太远。"));
        }
    }

    public static void FollowReply(LittleEndianAccessor slea, MapleClient c) {
        if ((c.getPlayer().getFollowId() > 0) && (c.getPlayer().getFollowId() == slea.readInt())) {
            MapleCharacter tt = c.getPlayer().getMap().getCharacterById(c.getPlayer().getFollowId());
            if ((tt != null) && (tt.getPosition().distanceSq(c.getPlayer().getPosition()) < 10000.0D) && (tt.getFollowId() == 0) && (tt.getId() != c.getPlayer().getId())) {
                boolean accepted = slea.readByte() > 0;
                if (accepted) {
                    tt.setFollowId(c.getPlayer().getId());
                    tt.setFollowOn(true);
                    tt.setFollowInitiator(false);
                    c.getPlayer().setFollowOn(true);
                    c.getPlayer().setFollowInitiator(true);
                    c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.followEffect(tt.getId(), c.getPlayer().getId(), null));
                } else {
                    c.getPlayer().setFollowId(0);
                    tt.setFollowId(0);
                    tt.getClient().getSession().write(MaplePacketCreator.getFollowMsg(5));
                }
            } else {
                if (tt != null) {
                    tt.setFollowId(0);
                    c.getPlayer().setFollowId(0);
                }
                c.getSession().write(MaplePacketCreator.serverNotice(1, "距离太远."));
            }
        } else {
            c.getPlayer().setFollowId(0);
        }
    }

    public static void DoRing(MapleClient c, String name, int itemid) {
        int newItemId = itemid == 2240003 ? 1112809 : itemid == 2240002 ? 1112807 : itemid == 2240001 ? 1112806 : itemid == 2240000 ? 1112803 : 1112300 + (itemid - 2240004);
        MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterByName(name);
        int errcode = 0;
        if (c.getPlayer().getMarriageId() > 0) {
            errcode = 23;
        } else if (chr == null) {
            errcode = 18;
        } else if (chr.getMapId() != c.getPlayer().getMapId()) {
            errcode = 19;
        } else if ((!c.getPlayer().haveItem(itemid, 1)) || (itemid < 2240000) || (itemid > 2240015)) {
            errcode = 13;
        } else if ((chr.getMarriageId() > 0) || (chr.getMarriageItemId() > 0)) {
            errcode = 24;
        } else if (!MapleInventoryManipulator.checkSpace(c, newItemId, 1, "")) {
            errcode = 20;
        } else if (!MapleInventoryManipulator.checkSpace(chr.getClient(), newItemId, 1, "")) {
            errcode = 21;
        }
        if (errcode > 0) {
            c.getSession().write(MaplePacketCreator.sendEngagement((byte) errcode, 0, null, null));
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        c.getPlayer().setMarriageItemId(itemid);
        chr.getClient().getSession().write(MaplePacketCreator.sendEngagementRequest(c.getPlayer().getName(), c.getPlayer().getId()));
    }

    public static void RingAction(LittleEndianAccessor slea, MapleClient c) {
        byte mode = slea.readByte();
        if (mode == 0) {
            DoRing(c, slea.readMapleAsciiString(), slea.readInt());
        } else if (mode == 1) {
            c.getPlayer().setMarriageItemId(0);
        } else if (mode == 2) {
            boolean accepted = slea.readByte() > 0;
            String name = slea.readMapleAsciiString();
            int id = slea.readInt();
            MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterByName(name);
            if ((c.getPlayer().getMarriageId() > 0) || (chr == null) || (chr.getId() != id) || (chr.getMarriageItemId() <= 0) || (!chr.haveItem(chr.getMarriageItemId(), 1)) || (chr.getMarriageId() > 0) || (!chr.isAlive()) || (chr.getEventInstance() != null) || (!c.getPlayer().isAlive()) || (c.getPlayer().getEventInstance() != null)) {
                c.getSession().write(MaplePacketCreator.sendEngagement((byte) 29, 0, null, null));
                c.getSession().write(MaplePacketCreator.enableActions());
                return;
            }
            if (accepted) {
                int itemid = chr.getMarriageItemId();
                int newItemId = itemid == 2240003 ? 1112809 : itemid == 2240002 ? 1112807 : itemid == 2240001 ? 1112806 : itemid == 2240000 ? 1112803 : 1112300 + (itemid - 2240004);
                if ((!MapleInventoryManipulator.checkSpace(c, newItemId, 1, "")) || (!MapleInventoryManipulator.checkSpace(chr.getClient(), newItemId, 1, ""))) {
                    c.getSession().write(MaplePacketCreator.sendEngagement((byte) 21, 0, null, null));
                    c.getSession().write(MaplePacketCreator.enableActions());
                    return;
                }
                try {
                    int[] ringID = MapleRing.makeRing(newItemId, c.getPlayer(), chr);
                    Equip eq = (Equip) MapleItemInformationProvider.getInstance().getEquipById(newItemId, ringID[1]);
                    MapleRing ring = MapleRing.loadFromDb(ringID[1]);
                    if (ring != null) {
                        eq.setRing(ring);
                    }
                    MapleInventoryManipulator.addbyItem(c, eq);
                    eq = (Equip) MapleItemInformationProvider.getInstance().getEquipById(newItemId, ringID[0]);
                    ring = MapleRing.loadFromDb(ringID[0]);
                    if (ring != null) {
                        eq.setRing(ring);
                    }
                    MapleInventoryManipulator.addbyItem(chr.getClient(), eq);
                    MapleInventoryManipulator.removeById(chr.getClient(), MapleInventoryType.USE, chr.getMarriageItemId(), 1, false, false);
                    chr.getClient().getSession().write(MaplePacketCreator.sendEngagement((byte) 16, newItemId, chr, c.getPlayer()));
                    chr.setMarriageId(c.getPlayer().getId());
                    c.getPlayer().setMarriageId(chr.getId());
                    chr.fakeRelog();
                    c.getPlayer().fakeRelog();
                } catch (Exception e) {
                    FileoutputUtil.outputFileError("log\\Packet_Except.log", e);
                }
            } else {
                chr.getClient().getSession().write(MaplePacketCreator.sendEngagement((byte) 30, 0, null, null));
            }
            c.getSession().write(MaplePacketCreator.enableActions());
            chr.setMarriageItemId(0);
        } else if (mode == 3) {
            int itemId = slea.readInt();
            MapleInventoryType type = GameConstants.getInventoryType(itemId);
            Item item = c.getPlayer().getInventory(type).findById(itemId);
            if ((item != null) && (type == MapleInventoryType.ETC) && (itemId / 10000 == 421)) {
                MapleInventoryManipulator.drop(c, type, item.getPosition(), item.getQuantity());
            }
        }
    }

    public static void Solomon(LittleEndianAccessor slea, MapleClient c) {
        c.getSession().write(MaplePacketCreator.enableActions());
        c.getPlayer().updateTick(slea.readInt());
        Item item = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slea.readShort());
        if ((item == null) || (item.getItemId() != slea.readInt()) || (item.getQuantity() <= 0) || (c.getPlayer().getGachExp() > 0) || (c.getPlayer().getLevel() > 50) || (MapleItemInformationProvider.getInstance().getItemEffect(item.getItemId()).getEXP() <= 0)) {
            return;
        }
        c.getPlayer().setGachExp(c.getPlayer().getGachExp() + MapleItemInformationProvider.getInstance().getItemEffect(item.getItemId()).getEXP());
        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, item.getPosition(), (short) 1, false);
        c.getPlayer().updateSingleStat(MapleStat.GACHAPONEXP, c.getPlayer().getGachExp());
    }

    public static void GachExp(LittleEndianAccessor slea, MapleClient c) {
        c.getSession().write(MaplePacketCreator.enableActions());
        c.getPlayer().updateTick(slea.readInt());
        if (c.getPlayer().getGachExp() <= 0) {
            return;
        }
        c.getPlayer().gainExp(c.getPlayer().getGachExp() * GameConstants.getExpRate_Quest(c.getPlayer().getLevel()), true, true, false);
        c.getPlayer().setGachExp(0);
        c.getPlayer().updateSingleStat(MapleStat.GACHAPONEXP, 0);
    }

    public static void Report(LittleEndianAccessor slea, MapleClient c) {
        ReportType type = ReportType.getById(slea.readByte());
        MapleCharacter other = c.getPlayer().getMap().getCharacterByName(slea.readMapleAsciiString());

        if ((other == null) || (type == null) || (other.isIntern())) {
            c.getPlayer().dropMessage(1, "举报错误.");
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        MapleQuestStatus stat = c.getPlayer().getQuestNAdd(MapleQuest.getInstance(123457));
        if (stat.getCustomData() == null) {
            stat.setCustomData("0");
        }
        long currentTime = System.currentTimeMillis();
        long theTime = Long.parseLong(stat.getCustomData());
        if ((theTime + 7200000L > currentTime) && (!c.getPlayer().isIntern())) {
            c.getPlayer().dropMessage(5, "每2小时才能举报1次.");
        } else {
            stat.setCustomData(String.valueOf(currentTime));
            other.addReport(type);

            c.getPlayer().dropMessage(1, "举报完成.");
        }
        c.getSession().write(MaplePacketCreator.enableActions());
    }

    public static void MonsterBookInfoRequest(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        if ((c.getPlayer() == null) || (c.getPlayer().getMap() == null)) {
            return;
        }
        slea.readInt();
        MapleCharacter player = c.getPlayer().getMap().getCharacterById(slea.readInt());
        c.getSession().write(MaplePacketCreator.enableActions());
        if ((player != null) && (!player.isClone()) && ((!player.isGM()) || (c.getPlayer().isGM()))) {
            c.getSession().write(MaplePacketCreator.getMonsterBookInfo(player));
        }
    }

    public static void ChangeSet(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        if ((c.getPlayer() == null) || (c.getPlayer().getMap() == null)) {
            return;
        }
        int set = slea.readInt();
        if (chr.getMonsterBook().changeSet(set)) {
            chr.getMonsterBook().applyBook(chr, false);
            chr.getQuestNAdd(MapleQuest.getInstance(122800)).setCustomData(String.valueOf(set));
            c.getSession().write(MaplePacketCreator.changeCardSet(set));
        }
    }

    public static void EnterPVP(LittleEndianAccessor slea, MapleClient c) {
        if ((c.getPlayer() == null) || (c.getPlayer().getMap() == null) || (c.getPlayer().getMapId() != 960000000)) {
            c.getSession().write(MaplePacketCreator.pvpBlocked(1));
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if (c.getPlayer().getParty() != null) {
            c.getSession().write(MaplePacketCreator.pvpBlocked(9));
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        c.getPlayer().updateTick(slea.readInt());
        slea.skip(1);
        int type = slea.readByte();
        int lvl = slea.readByte();
        int playerCount = 0;
        boolean passed = false;
        switch (lvl) {
            case 0:
                passed = (c.getPlayer().getLevel() >= 30) && (c.getPlayer().getLevel() < 70);
                break;
            case 1:
                passed = c.getPlayer().getLevel() >= 70;
                break;
            case 2:
                passed = c.getPlayer().getLevel() >= 120;
                break;
            case 3:
                passed = c.getPlayer().getLevel() >= 180;
        }

        EventManager em = c.getChannelServer().getEventSM().getEventManager("PVP");
        if ((!passed) || (em == null)) {
            c.getSession().write(MaplePacketCreator.pvpBlocked(1));
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        List maps = new ArrayList();
        switch (type) {
            case 0:
                maps.add(Integer.valueOf(960010100));
                maps.add(Integer.valueOf(960010101));
                maps.add(Integer.valueOf(960010102));
                break;
            case 1:
                maps.add(Integer.valueOf(960020100));
                maps.add(Integer.valueOf(960020101));
                maps.add(Integer.valueOf(960020102));
                maps.add(Integer.valueOf(960020103));
                break;
            case 2:
                maps.add(Integer.valueOf(960030100));
                break;
            case 3:
                maps.add(Integer.valueOf(689000000));
                maps.add(Integer.valueOf(689000010));
                break;
            default:
                passed = false;
        }

        if (!passed) {
            c.getSession().write(MaplePacketCreator.pvpBlocked(1));
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        c.getPlayer().getStat().heal(c.getPlayer());
        c.getPlayer().cancelAllBuffs();
        c.getPlayer().dispelDebuffs();
        c.getPlayer().changeRemoval();
        c.getPlayer().clearAllCooldowns();
        c.getPlayer().unequipAllPets();
        StringBuilder key = new StringBuilder().append(lvl).append(" ").append(type).append(" ");

        for (Iterator i$ = maps.iterator(); i$.hasNext();) {
            int i = ((Integer) i$.next()).intValue();
            EventInstanceManager eim = em.getInstance(new StringBuilder("PVP").append(key.toString()).append(i).toString().replace(" ", "").replace(" ", ""));
            if ((eim != null) && ((eim.getProperty("started").equals("0")) || (eim.getPlayerCount() < 10))) {
                eim.registerPlayer(c.getPlayer());
                return;
            }
        }

        em.startInstance_Solo(key.append(maps.get(Randomizer.nextInt(maps.size()))).toString(), c.getPlayer());
    }

    public static void RespawnPVP(LittleEndianAccessor slea, MapleClient c) {
        if ((c.getPlayer() == null) || (c.getPlayer().getMap() == null) || (!c.getPlayer().inPVP()) || (c.getPlayer().isAlive())) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        int type = Integer.parseInt(c.getPlayer().getEventInstance().getProperty("type"));
        c.getPlayer().getStat().heal_noUpdate(c.getPlayer());
        c.getPlayer().updateSingleStat(MapleStat.MP, c.getPlayer().getStat().getMp());
        c.getPlayer().getEventInstance().schedule("broadcastType", 500L);
        c.getPlayer().getEventInstance().schedule("updateScoreboard", 500L);
        c.getPlayer().changeMap(c.getPlayer().getMap(), c.getPlayer().getMap().getPortal(c.getPlayer().getTeam() == 0 ? 2 : type == 3 ? 1 : c.getPlayer().getTeam() == 0 ? 3 : type == 0 ? 0 : 3));
        c.getSession().write(MaplePacketCreator.getPVPScore(Integer.parseInt(c.getPlayer().getEventInstance().getProperty(String.valueOf(c.getPlayer().getId()))), false));
    }

    public static void LeavePVP(LittleEndianAccessor slea, MapleClient c) {
        if ((c.getPlayer() == null) || (c.getPlayer().getMap() == null) || (!c.getPlayer().inPVP())) {
            c.getSession().write(MaplePacketCreator.pvpBlocked(6));
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        int x = Integer.parseInt(c.getPlayer().getEventInstance().getProperty(String.valueOf(c.getPlayer().getId())));
        int lv = Integer.parseInt(c.getPlayer().getEventInstance().getProperty("lvl"));
        if ((lv < 2) && (c.getPlayer().getLevel() >= 120)) {
            x /= 2;
        }
        c.getPlayer().setTotalBattleExp(c.getPlayer().getTotalBattleExp() + x / 10 * 3 / 2);
        c.getPlayer().setBattlePoints(c.getPlayer().getBattlePoints() + x / 10 * 3 / 2);
        c.getPlayer().cancelAllBuffs();
        c.getPlayer().changeRemoval();
        c.getPlayer().dispelDebuffs();
        c.getPlayer().clearAllCooldowns();
        c.getPlayer().updateTick(slea.readInt());
        c.getSession().write(UIPacket.clearMidMsg());
        c.getPlayer().changeMap(c.getChannelServer().getMapFactory().getMap(960000000));
        c.getPlayer().getStat().recalcLocalStats(c.getPlayer());
        c.getPlayer().getStat().heal(c.getPlayer());
    }

    public static void AttackPVP(LittleEndianAccessor slea, MapleClient c) {
        MapleCharacter chr = c.getPlayer();
        int trueSkill = slea.readInt();
        int skillid = trueSkill;
        if ((chr == null) || (chr.isHidden()) || (!chr.isAlive()) || (chr.hasBlockedInventory()) || (chr.getMap() == null) || (!chr.inPVP()) || (!chr.getEventInstance().getProperty("started").equals("1")) || (skillid >= 90000000)) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        int lvl = Integer.parseInt(chr.getEventInstance().getProperty("lvl"));
        int type = Integer.parseInt(chr.getEventInstance().getProperty("type"));
        int ice = Integer.parseInt(chr.getEventInstance().getProperty("ice"));
        int ourScore = Integer.parseInt(chr.getEventInstance().getProperty(String.valueOf(chr.getId())));
        int addedScore = 0;
        int skillLevel = 0;
        int trueSkillLevel = 0;
        int animation = -1;
        int attackCount = 1;
        int mobCount = 1;
        int fakeMastery = chr.getStat().passive_mastery();
        int ignoreDEF = chr.getStat().ignoreTargetDEF;
        int critRate = chr.getStat().passive_sharpeye_rate();
        int skillDamage = 100;
        boolean magic = false;
        boolean move = false;
        boolean pull = false;
        boolean push = false;

        double maxdamage = chr.getStat().getCurrentMaxBasePVPDamage();
        MapleStatEffect effect = null;
        chr.checkFollow();
        Rectangle box = null;

        Item weapon = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -11);
        Item shield = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -10);
        boolean katara = (shield != null) && (shield.getItemId() / 10000 == 134);
        boolean aran = (weapon != null) && (weapon.getItemId() / 10000 == 144) && (GameConstants.is战神(chr.getJob()));
        slea.skip(1);
        int chargeTime = 0;
        if (GameConstants.isMagicChargeSkill(skillid)) {
            chargeTime = slea.readInt();
        } else {
            slea.skip(4);
        }
        boolean facingLeft = slea.readByte() > 0;
        if (skillid > 0) {
            if ((skillid == 3211006) && (chr.getTotalSkillLevel(3220010) > 0)) {
                skillid = 3220010;
            }
            Skill skil = SkillFactory.getSkill(skillid);
            if ((skil == null) || (skil.isPVPDisabled())) {
                c.getSession().write(MaplePacketCreator.enableActions());
                return;
            }
            magic = skil.isMagic();
            move = skil.isMovement();
            push = skil.isPush();
            pull = skil.isPull();
            if (chr.getTotalSkillLevel(GameConstants.getLinkedAranSkill(skillid)) <= 0) {
                if ((!GameConstants.isIceKnightSkill(skillid)) && (chr.getTotalSkillLevel(GameConstants.getLinkedAranSkill(skillid)) <= 0)) {
                    c.getSession().close();
                    return;
                }
                if ((GameConstants.isIceKnightSkill(skillid)) && (chr.getBuffSource(MapleBuffStat.变身术) % 10000 != 1105)) {
                    return;
                }
            }
            animation = skil.getAnimation();
            if ((animation == -1) && (!skil.isMagic())) {
                String after = weapon == null ? "barehands" : katara ? "katara" : aran ? "aran" : MapleItemInformationProvider.getInstance().getAfterImage(weapon.getItemId());
                if (after != null) {
                    List p = MapleItemInformationProvider.getInstance().getAfterImage(after);
                    if (p != null) {
                        while (animation == -1) {
                            Triple ep = (Triple) p.get(Randomizer.nextInt(p.size()));
                            if ((!((String) ep.left).contains("stab")) || ((((String) ep.left).contains("stab")) && (weapon != null) && (weapon.getItemId() / 10000 == 144))) {
                                continue;
                            }
                            if (SkillFactory.getDelay((String) ep.left) != null) {
                                animation = SkillFactory.getDelay((String) ep.left).intValue();
                            }
                        }
                    }
                }
            } else if ((animation == -1) && (skil.isMagic())) {
                animation = SkillFactory.getDelay(Randomizer.nextBoolean() ? "dash" : "dash2").intValue();
            }
            if (skil.isMagic()) {
                fakeMastery = 0;
            }
            skillLevel = chr.getTotalSkillLevel(GameConstants.getLinkedAranSkill(skillid));
            trueSkillLevel = chr.getTotalSkillLevel(GameConstants.getLinkedAranSkill(trueSkill));
            effect = skil.getPVPEffect(skillLevel);
            ignoreDEF += effect.getIgnoreMob();
            critRate += effect.getCr();

            skillDamage = effect.getDamage() + chr.getStat().getDamageIncrease(skillid);
            box = effect.calculateBoundingBox(chr.getTruePosition(), facingLeft, chr.getStat().defRange);
            attackCount = Math.max(effect.getBulletCount(), effect.getAttackCount());
            mobCount = Math.max(1, effect.getMobCount());
            if ((effect.getCooldown() > 0) && (!chr.isGM())) {
                if (chr.skillisCooling(skillid)) {
                    c.getSession().write(MaplePacketCreator.enableActions());
                    return;
                }
                if (((skillid != 35111004) && (skillid != 35121013)) || (chr.getBuffSource(MapleBuffStat.金属机甲) != skillid)) {
                    c.getSession().write(MaplePacketCreator.skillCooldown(skillid, effect.getCooldown()));
                    chr.addCooldown(skillid, System.currentTimeMillis(), effect.getCooldown() * 1000);
                }
            }
            switch (chr.getJob()) {
                case 111:
                case 112:
                case 1111:
                case 1112:
                    if (PlayerHandler.isFinisher(skillid) <= 0) {
                        break;
                    }
                    if ((chr.getBuffedValue(MapleBuffStat.斗气集中) == null) || (chr.getBuffedValue(MapleBuffStat.斗气集中).intValue() <= 2)) {
                        return;
                    }
                    skillDamage *= (chr.getBuffedValue(MapleBuffStat.斗气集中).intValue() - 1) / 2;
                    chr.handleOrbconsume(PlayerHandler.isFinisher(skillid));
            }
        } else {
            attackCount = katara ? 2 : 1;
            Point lt = null;
            Point rb = null;
            String after = weapon == null ? "barehands" : katara ? "katara" : aran ? "aran" : MapleItemInformationProvider.getInstance().getAfterImage(weapon.getItemId());
            if (after != null) {
                List p = MapleItemInformationProvider.getInstance().getAfterImage(after);
                if (p != null) {
                    while (animation == -1) {
                        Triple ep = (Triple) p.get(Randomizer.nextInt(p.size()));
                        if ((!((String) ep.left).contains("stab")) || ((((String) ep.left).contains("stab")) && (weapon != null) && (weapon.getItemId() / 10000 == 147))) {
                            continue;
                        }
                        if (SkillFactory.getDelay((String) ep.left) != null) {
                            animation = SkillFactory.getDelay((String) ep.left).intValue();
                            lt = (Point) ep.mid;
                            rb = (Point) ep.right;
                        }
                    }
                }
            }
            box = MapleStatEffect.calculateBoundingBox(chr.getTruePosition(), facingLeft, lt, rb, chr.getStat().defRange);
        }
        chr.getCheatTracker().checkPVPAttack(skillid);
        MapleStatEffect shad = chr.getStatForBuff(MapleBuffStat.影分身);
        int originalAttackCount = attackCount;
        attackCount *= (shad != null ? 2 : 1);

        slea.skip(4);
        int speed = slea.readByte();
        int slot = slea.readShort();
        int csstar = slea.readShort();
        int visProjectile = 0;
        if ((chr.getJob() >= 3500) && (chr.getJob() <= 3512)) {
            visProjectile = 2333000;
        } else if (GameConstants.is火炮手(chr.getJob())) {
            visProjectile = 2333001;
        } else if ((!GameConstants.is双弩精灵(chr.getJob())) && (chr.getBuffedValue(MapleBuffStat.无形箭弩) == null) && (slot > 0)) {
            Item ipp = chr.getInventory(MapleInventoryType.USE).getItem((short) slot);
            if (ipp == null) {
                return;
            }
            if (csstar > 0) {
                ipp = chr.getInventory(MapleInventoryType.CASH).getItem((short) csstar);
                if (ipp == null) {
                    return;
                }
            }
            visProjectile = ipp.getItemId();
        }
        maxdamage *= skillDamage / 100.0D;
        maxdamage *= chr.getStat().dam_r / 100.0D;
        List ourAttacks = new ArrayList(mobCount);
        boolean area = inArea(chr);
        boolean didAttack = false;
        boolean killed = false;
        if (!area) {
            for (MapleCharacter attacked : chr.getMap().getCharactersIntersect(box)) {
                if ((attacked.getId() != chr.getId()) && (attacked.isAlive()) && (!attacked.isHidden()) && ((type == 0) || (attacked.getTeam() != chr.getTeam()))) {
                    double rawDamage = maxdamage / Math.max(1.0D, (magic ? attacked.getStat().mdef : attacked.getStat().wdef) * Math.max(1.0D, 100.0D - ignoreDEF) / 100.0D * (type == 3 ? 0.2D : 0.5D));
                    if ((attacked.getBuffedValue(MapleBuffStat.INVINCIBILITY) != null) || (inArea(attacked))) {
                        rawDamage = 0.0D;
                    }
                    rawDamage *= attacked.getStat().mesoGuard / 100.0D;
                    rawDamage += rawDamage * chr.getDamageIncrease(attacked.getId()) / 100.0D;
                    rawDamage = ((Double) attacked.modifyDamageTaken(rawDamage, attacked).left).doubleValue();
                    double min = rawDamage * chr.getStat().trueMastery / 100.0D;
                    List attacks = new ArrayList(attackCount);
                    int totalMPLoss = 0;
                    int totalHPLoss = 0;
                    for (int i = 0; i < attackCount; i++) {
                        boolean critical_ = false;
                        int mploss = 0;
                        double ourDamage = Randomizer.nextInt((int) Math.abs(Math.round(rawDamage - min)) + 2) + min;
                        if ((attacked.getStat().dodgeChance > 0) && (Randomizer.nextInt(100) < attacked.getStat().dodgeChance)) {
                            ourDamage = 0.0D;
                        } else if ((attacked.hasDisease(MapleDisease.DARKNESS)) && (Randomizer.nextInt(100) < 50)) {
                            ourDamage = 0.0D;
                        } else if ((attacked.getJob() == 122) && (attacked.getTotalSkillLevel(1220006) > 0) && (attacked.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -10) != null)) {
                            MapleStatEffect eff = SkillFactory.getSkill(1220006).getEffect(attacked.getTotalSkillLevel(1220006));
                            if (eff.makeChanceResult()) {
                                ourDamage = 0.0D;
                            }
                        } else if ((attacked.getJob() == 412) && (attacked.getTotalSkillLevel(4120002) > 0)) {
                            MapleStatEffect eff = SkillFactory.getSkill(4120002).getEffect(attacked.getTotalSkillLevel(4120002));
                            if (eff.makeChanceResult()) {
                                ourDamage = 0.0D;
                            }
                        } else if ((attacked.getJob() == 422) && (attacked.getTotalSkillLevel(4220002) > 0)) {
                            MapleStatEffect eff = SkillFactory.getSkill(4220002).getEffect(attacked.getTotalSkillLevel(4220002));
                            if (eff.makeChanceResult()) {
                                ourDamage = 0.0D;
                            }
                        } else if ((shad != null) && (i >= originalAttackCount)) {
                            ourDamage *= shad.getX() / 100.0D;
                        }
                        if ((ourDamage > 0.0D) && (skillid != 4211006) && (skillid != 3211003) && ((skillid == 4221001) || (skillid == 3221007) || (skillid == 23121003) || (skillid == 4331005) || (skillid == 4331006) || (skillid == 21120005) || (Randomizer.nextInt(100) < critRate))) {
                            ourDamage *= (100.0D + (Randomizer.nextInt(Math.max(2, chr.getStat().passive_sharpeye_percent() - chr.getStat().passive_sharpeye_min_percent())) + chr.getStat().passive_sharpeye_min_percent())) / 100.0D;
                            critical_ = true;
                        }
                        if (attacked.getBuffedValue(MapleBuffStat.魔法盾) != null) {
                            mploss = (int) Math.min(attacked.getStat().getMp(), ourDamage * attacked.getBuffedValue(MapleBuffStat.魔法盾).doubleValue() / 100.0D);
                        }
                        ourDamage -= mploss;
                        if (attacked.getBuffedValue(MapleBuffStat.终极无限) != null) {
                            mploss = 0;
                        }
                        attacks.add(new Pair(Integer.valueOf((int) Math.floor(ourDamage)), Boolean.valueOf(critical_)));

                        totalHPLoss = (int) (totalHPLoss + Math.floor(ourDamage));
                        totalMPLoss += mploss;
                    }
                    addedScore += Math.min(attacked.getStat().getHp() / 100, totalHPLoss / 100 + totalMPLoss / 100);
                    attacked.addMPHP(-totalHPLoss, -totalMPLoss);
                    ourAttacks.add(new AttackPair(attacked.getId(), attacked.getPosition(), attacks));
                    chr.onAttack(attacked.getStat().getCurrentMaxHp(), attacked.getStat().getCurrentMaxMp(attacked.getJob()), skillid, attacked.getObjectId(), totalHPLoss);
                    attacked.getCheatTracker().setAttacksWithoutHit(false);
                    if (totalHPLoss > 0) {
                        didAttack = true;
                    }
                    if (attacked.getStat().getHPPercent() <= 20) {
                        SkillFactory.getSkill(PlayerStats.getSkillByJob(93, attacked.getJob())).getEffect(1).applyTo(attacked);
                    }
                    if (effect != null) {
                        if ((effect.getMonsterStati().size() > 0) && (effect.makeChanceResult())) {
                            for (Map.Entry z : effect.getMonsterStati().entrySet()) {
                                MapleDisease d = MonsterStatus.getLinkedDisease((MonsterStatus) z.getKey());
                                if (d != null) {
                                    attacked.giveDebuff(d, ((Integer) z.getValue()).intValue(), effect.getDuration(), d.getDisease(), 1);
                                }
                            }
                        }
                        effect.handleExtraPVP(chr, attacked);
                    }
                    if ((chr.getJob() == 121) || (chr.getJob() == 122) || (chr.getJob() == 2110) || (chr.getJob() == 2111) || (chr.getJob() == 2112)) {
                        if ((chr.getBuffSource(MapleBuffStat.属性攻击) == 1211006) || (chr.getBuffSource(MapleBuffStat.属性攻击) == 21101006)) {
                            MapleStatEffect eff = chr.getStatForBuff(MapleBuffStat.属性攻击);
                            if (eff.makeChanceResult()) {
                                attacked.giveDebuff(MapleDisease.FREEZE, 1, eff.getDuration(), MapleDisease.FREEZE.getDisease(), 1);
                            }
                        }
                    } else if (chr.getBuffedValue(MapleBuffStat.幻影步) != null) {
                        MapleStatEffect eff = chr.getStatForBuff(MapleBuffStat.幻影步);

                        if ((eff != null) && (eff.makeChanceResult())) {
                            attacked.giveDebuff(MapleDisease.SLOW, 100 - Math.abs(eff.getX()), eff.getDuration(), MapleDisease.SLOW.getDisease(), 1);
                        }
                    } else if (chr.getBuffedValue(MapleBuffStat.缓速术) != null) {
                        MapleStatEffect eff = chr.getStatForBuff(MapleBuffStat.缓速术);
                        if ((eff != null) && (eff.makeChanceResult())) {
                            attacked.giveDebuff(MapleDisease.SLOW, 100 - Math.abs(eff.getX()), eff.getDuration(), MapleDisease.SLOW.getDisease(), 1);
                        }
                  //  } else if ((chr.getJob() == 412) || (chr.getJob() == 422) || (chr.getJob() == 434) || (chr.getJob() == 1411) || (chr.getJob() == 1412)) {
                      } else if ((chr.getJob() == 411) || (chr.getJob() == 421) || (chr.getJob() == 432) || (chr.getJob() == 1411) || (chr.getJob() == 1412)) { 
                        int[] skills = {4110011, 4210010, 4320005, 14110004};
                        for (int i : skills) {
                            Skill skill = SkillFactory.getSkill(i);
                            if (chr.getTotalSkillLevel(skill) > 0) {
                                MapleStatEffect venomEffect = skill.getEffect(chr.getTotalSkillLevel(skill));
                                if (!venomEffect.makeChanceResult()) {
                                    break;
                                }
                                attacked.giveDebuff(MapleDisease.POISON, 1, venomEffect.getDuration(), MapleDisease.POISON.getDisease(), 1);
                                break;
                            }
                        }

                    }

                    if (chr.getJob() / 100 % 10 == 2) {
                        int[] skills = {2000007, 12000006, 22000002, 32000012};
                        for (int i : skills) {
                            Skill skill = SkillFactory.getSkill(i);
                            if (chr.getTotalSkillLevel(skill) > 0) {
                                MapleStatEffect venomEffect = skill.getEffect(chr.getTotalSkillLevel(skill));
                                if (!venomEffect.makeChanceResult()) {
                                    break;
                                }
                                venomEffect.applyTo(attacked);
                                break;
                            }
                        }

                    }

                    if (ice == attacked.getId()) {
                        chr.getClient().getSession().write(MaplePacketCreator.getPVPIceHPBar(attacked.getStat().getHp(), attacked.getStat().getCurrentMaxHp()));
                    } else {
                        chr.getClient().getSession().write(MaplePacketCreator.getPVPHPBar(attacked.getId(), attacked.getStat().getHp(), attacked.getStat().getCurrentMaxHp()));
                    }
                    if (!attacked.isAlive()) {
                        addedScore += 5;
                        killed = true;
                    }
                    if (ourAttacks.size() >= mobCount) {
                        break;
                    }
                }
            }
        } else if (type == 3) {
            if ((Integer.parseInt(chr.getEventInstance().getProperty("redflag")) == chr.getId()) && (chr.getMap().getArea(1).contains(chr.getTruePosition()))) {
                chr.getEventInstance().setProperty("redflag", "0");
                chr.getEventInstance().setProperty("blue", String.valueOf(Integer.parseInt(chr.getEventInstance().getProperty("blue")) + 1));
                chr.getEventInstance().broadcastPlayerMsg(-7, "Blue Team has scored a point!");
                chr.getMap().spawnAutoDrop(2910000, (Point) ((Pair) chr.getMap().getGuardians().get(0)).left);
                chr.getEventInstance().broadcastPacket(MaplePacketCreator.getCapturePosition(chr.getMap()));
                chr.getEventInstance().broadcastPacket(MaplePacketCreator.resetCapture());
                chr.getEventInstance().schedule("updateScoreboard", 1000L);
            } else if ((Integer.parseInt(chr.getEventInstance().getProperty("blueflag")) == chr.getId()) && (chr.getMap().getArea(0).contains(chr.getTruePosition()))) {
                chr.getEventInstance().setProperty("blueflag", "0");
                chr.getEventInstance().setProperty("red", String.valueOf(Integer.parseInt(chr.getEventInstance().getProperty("red")) + 1));
                chr.getEventInstance().broadcastPlayerMsg(-7, "Red Team has scored a point!");
                chr.getMap().spawnAutoDrop(2910001, (Point) ((Pair) chr.getMap().getGuardians().get(1)).left);
                chr.getEventInstance().broadcastPacket(MaplePacketCreator.getCapturePosition(chr.getMap()));
                chr.getEventInstance().broadcastPacket(MaplePacketCreator.resetCapture());
                chr.getEventInstance().schedule("updateScoreboard", 1000L);
            }
        }
        if (chr.getEventInstance() == null) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if ((killed) || (addedScore > 0)) {
            chr.getEventInstance().addPVPScore(chr, addedScore);
            chr.getClient().getSession().write(MaplePacketCreator.getPVPScore(ourScore + addedScore, killed));
        }
        if (didAttack) {
            chr.afterAttack(ourAttacks.size(), attackCount, skillid);
            PlayerHandler.AranCombo(c, chr, ourAttacks.size() * attackCount);
            if ((skillid > 0) && ((ourAttacks.size() > 0) || (skillid != 4341002)) && (!GameConstants.isNoDelaySkill(skillid))) {
                effect.applyTo(chr, chr.getTruePosition());
            } else {
                c.getSession().write(MaplePacketCreator.enableActions());
            }
        } else {
            move = false;
            pull = false;
            push = false;
            c.getSession().write(MaplePacketCreator.enableActions());
        }
        chr.getMap().broadcastMessage(MaplePacketCreator.pvpAttack(chr.getId(), chr.getLevel(), trueSkill, trueSkillLevel, speed, fakeMastery, visProjectile, attackCount, chargeTime, animation, facingLeft ? 1 : 0, chr.getStat().defRange, skillid, skillLevel, move, push, pull, ourAttacks));
        if ((addedScore > 0) && (GameConstants.getAttackDelay(skillid, SkillFactory.getSkill(skillid)) >= 100)) {
            CheatTracker tracker = chr.getCheatTracker();
            tracker.setAttacksWithoutHit(true);
            if (tracker.getAttacksWithoutHit() >= 50) {
                tracker.registerOffense(CheatingOffense.ATTACK_WITHOUT_GETTING_HIT, "无敌自动封号.");
            }
        }
    }

    public static boolean inArea(MapleCharacter chr) {
        for (Rectangle rect : chr.getMap().getAreas()) {
            if (rect.contains(chr.getTruePosition())) {
                return true;
            }
        }
        for (MapleMist mist : chr.getMap().getAllMistsThreadsafe()) {
            if ((mist.getOwnerId() == chr.getId()) && (mist.isPoisonMist() == 2) && (mist.getBox().contains(chr.getTruePosition()))) {
                return true;
            }
        }
        return false;
    }

    public static void LieDetector(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr, boolean isItem) {
        if ((chr == null) || (chr.getMap() == null)) {
            return;
        }
        String target = slea.readMapleAsciiString();
        byte slot = 0;
        if (isItem) {
            if (!chr.getCheatTracker().canLieDetector()) {
                chr.dropMessage(1, "您已经使用过一次，暂时还无法使用测谎仪道具.");
                c.getSession().write(MaplePacketCreator.enableActions());
                return;
            }
            slot = (byte) slea.readShort();
            int itemId = slea.readInt();
            Item toUse = chr.getInventory(MapleInventoryType.USE).getItem((short) slot);
            if ((toUse == null) || (toUse.getQuantity() <= 0) || (toUse.getItemId() != itemId) || (itemId != 2190000)) {
                c.getSession().write(MaplePacketCreator.enableActions());
                return;
            }
        } else if (!chr.isIntern()) {
            c.getSession().close();
            return;
        }
        if (((FieldLimitType.PotionUse.check(chr.getMap().getFieldLimit())) && (isItem)) || (chr.getMap().getReturnMapId() == chr.getMapId())) {
            chr.dropMessage(5, "当前地图无法使用测谎仪.");
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        MapleCharacter search_chr = chr.getMap().getCharacterByName(target);
        if ((search_chr == null) || (search_chr.getId() == chr.getId()) || ((search_chr.isIntern()) && (!chr.isIntern()))) {
            chr.dropMessage(1, "未找到角色.");
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if ((search_chr.getEventInstance() != null) || (search_chr.getMapId() == 180000001)) {
            chr.dropMessage(5, "当前地图无法使用测谎仪.");
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if (search_chr.getAntiMacro().inProgress()) {
            c.getSession().write(MaplePacketCreator.LieDetectorResponse((byte) 3));
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if (((search_chr.getAntiMacro().isPassed()) && (isItem)) || (search_chr.getAntiMacro().getAttempt() == 2)) {
            c.getSession().write(MaplePacketCreator.LieDetectorResponse((byte) 2));
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if (!search_chr.getAntiMacro().startLieDetector(chr.getName(), isItem, false)) {
            chr.dropMessage(5, "使用测谎仪失败.");
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if (isItem) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, (short) slot, (short) 1, false);
        }
        search_chr.dropMessage(5, new StringBuilder().append(chr.getName()).append(" 对你使用了测谎仪.").toString());
    }

    public static void LieDetectorResponse(LittleEndianAccessor slea, MapleClient c) {
        if ((c.getPlayer() == null) || (c.getPlayer().getMap() == null)) {
            return;
        }
        String answer = slea.readMapleAsciiString();
        MapleLieDetector ld = c.getPlayer().getAntiMacro();
        if ((!ld.inProgress()) || ((ld.isPassed()) && (ld.getLastType() == 0)) || (ld.getAnswer() == null) || (answer.length() <= 0)) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if (answer.equalsIgnoreCase(ld.getAnswer())) {
            MapleCharacter search_chr = c.getPlayer().getMap().getCharacterByName(ld.getTester());
            if ((search_chr != null) && (search_chr.getId() != c.getPlayer().getId())) {
                search_chr.dropMessage(5, new StringBuilder().append(c.getPlayer().getName()).append(" 通过了测谎仪的检测.").toString());
            }
            c.getSession().write(MaplePacketCreator.LieDetectorResponse((byte) 10, (byte) 1));
            c.getPlayer().gainMeso(5000, true);
            ld.end();
            Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append("[GM Message] 玩家: ").append(c.getPlayer().getName()).append(" (等级 ").append(c.getPlayer().getLevel()).append(") 通过了测谎仪检测。").toString()));
        } else if (ld.getAttempt() < 3) {
            ld.startLieDetector(ld.getTester(), ld.getLastType() == 0, true);
        } else {
            MapleCharacter search_chr = c.getPlayer().getMap().getCharacterByName(ld.getTester());
            if ((search_chr != null) && (search_chr.getId() != c.getPlayer().getId())) {
                search_chr.dropMessage(5, new StringBuilder().append(c.getPlayer().getName()).append(" 没用通过测谎仪的检测，恭喜你获得7000的金币.").toString());
                search_chr.gainMeso(7000, true);
            }
            ld.end();
            c.getPlayer().getClient().getSession().write(MaplePacketCreator.LieDetectorResponse((byte) 8, (byte) 4));
            MapleMap map = c.getChannelServer().getMapFactory().getMap(180000001);
            c.getPlayer().getQuestNAdd(MapleQuest.getInstance(123456)).setCustomData(String.valueOf(1800));
            c.getPlayer().changeMap(map, map.getPortal(0));
            Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append("[GM Message] 玩家: ").append(c.getPlayer().getName()).append(" (等级 ").append(c.getPlayer().getLevel()).append(") 未通过测谎仪检测，系统将其监禁30分钟！").toString()));
        }
    }

    public static void LieDetectorRefresh(LittleEndianAccessor slea, MapleClient c) {
        if ((c.getPlayer() == null) || (c.getPlayer().getMap() == null)) {
            return;
        }
        MapleLieDetector ld = c.getPlayer().getAntiMacro();
        if (ld.getAttempt() < 3) {
            ld.startLieDetector(ld.getTester(), ld.getLastType() == 0, true);
        } else {
            ld.end();
            c.getPlayer().getClient().getSession().write(MaplePacketCreator.LieDetectorResponse((byte) 8, (byte) 4));
            MapleMap map = c.getChannelServer().getMapFactory().getMap(180000001);
            c.getPlayer().getQuestNAdd(MapleQuest.getInstance(123456)).setCustomData(String.valueOf(1800));
            c.getPlayer().changeMap(map, map.getPortal(0));
            Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append("[GM Message] 玩家: ").append(c.getPlayer().getName()).append(" (等级 ").append(c.getPlayer().getLevel()).append(") 未通过测谎仪检测，系统将其监禁30分钟！").toString()));
        }
    }
}
