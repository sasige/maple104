package tools;

import client.*;
import client.inventory.*;
import constants.GameConstants;
import constants.ServerConstants;
import handling.SendPacketOpcode;
import handling.login.LoginServer;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import handling.world.World;
import handling.world.guild.MapleGuild;
import handling.world.guild.MapleGuildAlliance;
import java.awt.Point;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;
import org.apache.log4j.Logger;
import server.*;
import server.events.MapleSnowball;
import server.life.MapleMonster;
import server.life.MapleNPC;
import server.life.PlayerNPC;
import server.maps.MapleNodes.MapleNodeInfo;
import server.maps.MapleNodes.MaplePlatform;
import server.maps.*;
import server.movement.LifeMovementFragment;
import server.quest.MapleQuest;
import server.shops.HiredMerchant;
import server.shops.MaplePlayerShopItem;
import tools.data.MaplePacketLittleEndianWriter;
import tools.packet.PacketHelper;
import tools.packet.PetPacket;



public class MaplePacketCreator {

    private static final Logger log = Logger.getLogger(MaplePacketCreator.class);
    public static final Map<MapleStat, Integer> EMPTY_STATUPDATE = new EnumMap(MapleStat.class);
    public static int DEFAULT_BUFFMASK = 0;

    public static byte[] getWzCheck(String WzCheckPack) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.WZ_CHECK.getValue());
        mplew.write(HexTool.getByteArrayFromHexString(WzCheckPack));
        return mplew.getPacket();
    }

    public static byte[] getServerIP(MapleClient c, int port, int clientId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SERVER_IP.getValue());
        mplew.writeShort(0);
        if (c.getTempIP().length() > 0) {
            for (String s : c.getTempIP().split(",")) {
                mplew.write(Integer.parseInt(s));
            }
        } else {
            mplew.write(ServerConstants.Gateway_IP);
        }
        mplew.writeShort(port);
        mplew.writeInt(clientId);
        mplew.write(1);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] getChannelChange(MapleClient c, int port) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CHANGE_CHANNEL.getValue());
        mplew.write(1);
        if (c.getTempIP().length() > 0) {
            for (String s : c.getTempIP().split(",")) {
                mplew.write(Integer.parseInt(s));
            }
        } else {
            mplew.write(ServerConstants.Gateway_IP);
        }
        mplew.writeShort(port);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] getCharInfo(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.WARP_TO_MAP.getValue());
        mplew.writeShort(1);
        mplew.writeInt(1);
        mplew.writeInt(1);
        mplew.writeLong(chr.getClient().getChannel() - 1);
        mplew.writeShort(0);
        mplew.writeInt(1);
        mplew.write(new byte[]{0, 1, 0, 0});
        for (int i = 0; i < 3; i++) {
            mplew.writeInt(Randomizer.nextInt());
        }

        PacketHelper.addCharacterInfo(mplew, chr);
        mplew.writeInt(0);//104
        mplew.write(HexTool.getByteArrayFromHexString("DC E2 77 02 0D 4D 76 00"));//104

        mplew.writeLong(4);//长度
        for (int x = 0; x < 4; x++) {//104
            mplew.writeLong(9410165 + x);
        }
        //mplew.writeZeroBytes(20);
        mplew.writeLong(0);//104
        mplew.writeLong(0);//104
        mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
        mplew.writeInt(100);
        mplew.writeShort(0);
        mplew.write(1);

        return mplew.getPacket();
    }

    public static byte[] enableActions() {
        return updatePlayerStats(EMPTY_STATUPDATE, true, null);
    }

    public static byte[] updatePlayerStats(Map<MapleStat, Integer> stats, MapleCharacter chr) {
        return updatePlayerStats(stats, false, chr);
    }

    public static byte[] updatePlayerStats(Map<MapleStat, Integer> mystats, boolean itemReaction, MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.UPDATE_STATS.getValue());
        mplew.write(itemReaction ? 1 : 0);
        long updateMask = 0L;
        for (MapleStat statupdate : mystats.keySet()) {
            updateMask |= statupdate.getValue();
        }
        mplew.writeLong(updateMask);

        for (Map.Entry statupdate : mystats.entrySet()) {
            Long value = Long.valueOf(((MapleStat) statupdate.getKey()).getValue());

            if (value.longValue() >= 1L) {
                if (value.longValue() == MapleStat.皮肤.getValue()) {
                    mplew.writeShort(((Integer) statupdate.getValue()).shortValue());
                } else if (value.longValue() <= MapleStat.发型.getValue()) {
                    mplew.writeInt(((Integer) statupdate.getValue()).intValue());
                } else if ((value.longValue() < MapleStat.职业.getValue()) || (value.longValue() == MapleStat.疲劳.getValue()) || (value.longValue() == MapleStat.ICE_GAGE.getValue())) {
                    mplew.write(((Integer) statupdate.getValue()).byteValue());
                } else if (value.longValue() == MapleStat.AVAILABLESP.getValue()) {
                    if ((GameConstants.is龙神(chr.getJob())) || (GameConstants.is反抗者(chr.getJob())) || (GameConstants.is双弩精灵(chr.getJob())) || (GameConstants.is幻影(chr.getJob())) || (GameConstants.is米哈尔(chr.getJob()))) {
                        mplew.write(chr.getRemainingSpSize());
                        for (int i = 0; i < chr.getRemainingSps().length; i++) {
                            if (chr.getRemainingSp(i) > 0) {
                                mplew.write(i + 1);
                                mplew.write(chr.getRemainingSp(i));
                            }
                        }
                    } else {
                        mplew.writeShort(((Integer) statupdate.getValue()).shortValue());
                    }
                } else if ((value.longValue() >= MapleStat.HP.getValue()) && (value.longValue() <= MapleStat.MAXMP.getValue())) {
                    mplew.writeInt(((Integer) statupdate.getValue()).intValue());
                } else if (value.longValue() < MapleStat.经验.getValue()) {
                    mplew.writeShort(((Integer) statupdate.getValue()).shortValue());
                } else if (value.longValue() == MapleStat.TRAIT_LIMIT.getValue()) {
                    mplew.writeInt(((Integer) statupdate.getValue()).intValue());
                    mplew.writeInt(((Integer) statupdate.getValue()).intValue());
                    mplew.writeInt(((Integer) statupdate.getValue()).intValue());
                } else if (value.longValue() == MapleStat.宠物.getValue()) {
                    mplew.writeLong(((Integer) statupdate.getValue()).intValue());
                    mplew.writeLong(((Integer) statupdate.getValue()).intValue());
                    mplew.writeLong(((Integer) statupdate.getValue()).intValue());
                } else {
                    mplew.writeInt(((Integer) statupdate.getValue()).intValue());
                }
            }
        }
        if ((updateMask == 0L) && (!itemReaction)) {
            mplew.write(1);
        }
        mplew.write(0);
        mplew.write(0);
        return mplew.getPacket();
    }
    
    public static byte[] updateLevel(MapleCharacter chr, boolean itemReaction) {
        return updateLevel(chr, itemReaction, false); 
    }
    
    private static byte[] updateLevel(MapleCharacter chr, boolean itemReaction, boolean b) {
        return updateLevel(chr, itemReaction, false); 
    }

    public static byte[] updateSp(MapleCharacter chr, boolean itemReaction) {
        return updateSp(chr, itemReaction, false);
    }

    public static byte[] updateSp(MapleCharacter chr, boolean itemReaction, boolean overrideJob) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.UPDATE_STATS.getValue());
        mplew.write(itemReaction ? 1 : 0);
        mplew.writeLong(32768L);
        if ((overrideJob) || (GameConstants.is龙神(chr.getJob())) || (GameConstants.is反抗者(chr.getJob())) || (GameConstants.is双弩精灵(chr.getJob())) || (GameConstants.is幻影(chr.getJob())) || (GameConstants.is米哈尔(chr.getJob()))) {
            mplew.write(chr.getRemainingSpSize());
            for (int i = 0; i < chr.getRemainingSps().length; i++) {
                if (chr.getRemainingSp(i) > 0) {
                    mplew.write(i + 1);
                    mplew.write(chr.getRemainingSp(i));
                }
            }
        } else {
            mplew.writeShort(chr.getRemainingSp());
        }
        mplew.writeShort(0);
        return mplew.getPacket();
    }

    public static byte[] getWarpToMap(MapleMap to, int spawnPoint, MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.WARP_TO_MAP.getValue());

        mplew.writeShort(1);
        mplew.writeInt(1);
        mplew.writeInt(1);
        mplew.writeLong(chr.getClient().getChannel() - 1);
        mplew.writeShort(0);
        mplew.writeLong(0L);
        mplew.write(0);
        mplew.writeInt(to.getId());
        mplew.write(spawnPoint);
        mplew.writeInt(chr.getStat().getHp());
        mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
        mplew.writeInt(100);
        mplew.writeShort(0);
        mplew.write(GameConstants.is反抗者(chr.getJob()) ? 0 : 1);

        return mplew.getPacket();
    }

    public static byte[] instantMapWarp(byte portal) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CURRENT_MAP_WARP.getValue());
        mplew.write(0);
        mplew.write(portal);

        return mplew.getPacket();
    }

    public static byte[] spawnPortal(int townId, int targetId, int skillId, Point pos) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPAWN_PORTAL.getValue());
        mplew.writeInt(townId);
        mplew.writeInt(targetId);
        if ((townId != 999999999) && (targetId != 999999999)) {
            mplew.writeInt(skillId);
            mplew.writePos(pos);
        }

        return mplew.getPacket();
    }

    public static byte[] spawnDoor(int oid, Point pos, boolean animation) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPAWN_DOOR.getValue());
        mplew.write(animation ? 0 : 1);
        mplew.writeInt(oid);
        mplew.writePos(pos);

        return mplew.getPacket();
    }

    public static byte[] removeDoor(int oid, boolean animation) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.REMOVE_DOOR.getValue());
        mplew.write(animation ? 0 : 1);
        mplew.writeInt(oid);

        return mplew.getPacket();
    }

    public static byte[] spawnSummon(MapleSummon summon, boolean animated) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPAWN_SUMMON.getValue());
        mplew.writeInt(summon.getOwnerId());
        mplew.writeInt(summon.getObjectId());
        mplew.writeInt(summon.getSkill());
        mplew.write(summon.getOwnerLevel() - 1);
        mplew.write(summon.getSkillLevel());
        mplew.writePos(summon.getPosition());
        mplew.write((summon.getSkill() == 32111006) || (summon.getSkill() == 33101005) ? 5 : 4);
        mplew.writeShort(0);
        mplew.write(summon.getMovementType().getValue());
        mplew.write(summon.getSummonType());
        mplew.write(animated ? 1 : 0);
        mplew.write(1);
        MapleCharacter chr = summon.getOwner();
        mplew.write((summon.getSkill() == 4341006) && (chr != null) ? 1 : 0);
        if ((summon.getSkill() == 4341006) && (chr != null)) {
            PacketHelper.addCharLook(mplew, chr, true);
        }
        if (summon.getSkill() == 35111002) {
            mplew.write(0);
        }

        return mplew.getPacket();
    }

    public static byte[] removeSummon(int ownerId, int objId) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.REMOVE_SUMMON.getValue());
        mplew.writeInt(ownerId);
        mplew.writeInt(objId);
        mplew.write(10);
        return mplew.getPacket();
    }

    public static byte[] removeSummon(MapleSummon summon, boolean animated) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.REMOVE_SUMMON.getValue());
        mplew.writeInt(summon.getOwnerId());
        mplew.writeInt(summon.getObjectId());
        if (animated) {
            switch (summon.getSkill()) {
                case 33101008:
                case 35111001:
                case 35111002:
                case 35111005:
                case 35111009:
                case 35111010:
                case 35111011:
                case 35121009:
                case 35121010:
                case 35121011:
                    mplew.write(5);
                    break;
                case 35121003:
                    mplew.write(10);
                    break;
                case 3111005:
                case 3211005:
                    mplew.write(12);
                    break;
                default:
                    mplew.write(4);
                    break;
            }
        } else {
            switch (summon.getSkill()) {
                case 3111005:
                case 3211005:
                    mplew.write(0);
                    break;
                default:
                    mplew.write(1);
            }
        }

        return mplew.getPacket();
    }

    public static byte[] serverBlocked(int type) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SERVER_BLOCKED.getValue());
        mplew.write(type);

        return mplew.getPacket();
    }

    public static byte[] pvpBlocked(int type) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_BLOCKED.getValue());
        mplew.write(type);

        return mplew.getPacket();
    }

    public static byte[] serverMessage(String message) {
        return serverMessage(4, 0, message, false);
    }

    public static byte[] serverNotice(int type, String message) {
        return serverMessage(type, 0, message, false);
    }

    public static byte[] serverNotice(int type, int channel, String message) {
        return serverMessage(type, channel, message, false);
    }

    public static byte[] serverNotice(int type, int channel, String message, boolean smegaEar) {
        return serverMessage(type, channel, message, smegaEar);
    }

    private static byte[] serverMessage(int type, int channel, String message, boolean megaEar) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SERVERMESSAGE.getValue());
        mplew.write(type);
        if (type == 4) {
            mplew.write(1);
        }
        mplew.writeMapleAsciiString(message);
        switch (type) {
            case 3:
            case 9:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
                mplew.write(channel - 1);
                mplew.write(megaEar ? 1 : 0);
                break;
            case 6:
            case 24:
                mplew.writeInt((channel >= 1000000) && (channel < 6000000) ? channel : 0);
            case 4:
            case 5:
            case 7:
            case 8:
            case 10:
            case 11:
            case 12:
            case 13:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
        }
        return mplew.getPacket();
    }

    public static byte[] getGachaponMega(String name, String message, Item item, byte rareness, int channel) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SERVERMESSAGE.getValue());
        mplew.write(rareness == 2 ? 25 : 25);
        mplew.writeMapleAsciiString(new StringBuilder().append(name).append(message).toString());
        mplew.writeInt(item.getItemId());
        mplew.writeInt(channel - 1);
        mplew.writeInt(0);
        mplew.write(1);
        PacketHelper.addItemInfo(mplew, item, true, true);

        return mplew.getPacket();
    }

    public static byte[] getGachaponMega(String message, int itemId) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SERVERMESSAGE.getValue());
        mplew.write(11);
        mplew.writeMapleAsciiString(message);
        mplew.writeInt(itemId);

        return mplew.getPacket();
    }

    public static byte[] getAniMsg(int questID, int time) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SERVERMESSAGE.getValue());
        mplew.write(14);
        mplew.writeShort(questID);
        mplew.writeInt(time);

        return mplew.getPacket();
    }

    public static byte[] tripleSmega(List<String> message, boolean ear, int channel) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SERVERMESSAGE.getValue());
        mplew.write(10);
        if (message.get(0) != null) {
            mplew.writeMapleAsciiString((String) message.get(0));
        }
        mplew.write(message.size());
        for (int i = 1; i < message.size(); i++) {
            if (message.get(i) != null) {
                mplew.writeMapleAsciiString((String) message.get(i));
            }
        }
        mplew.write(channel - 1);
        mplew.write(ear ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] getAvatarMega(MapleCharacter chr, int channel, int itemId, String message, boolean ear) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.AVATAR_MEGA.getValue());
        mplew.writeInt(itemId);
        mplew.writeMapleAsciiString(chr.getName());
        mplew.writeMapleAsciiString(message);
        mplew.writeInt(channel - 1);
        mplew.write(ear ? 1 : 0);
        PacketHelper.addCharLook(mplew, chr, true);

        return mplew.getPacket();
    }

    public static byte[] itemMegaphone(String msg, boolean whisper, int channel, Item item) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SERVERMESSAGE.getValue());
        mplew.write(8);
        mplew.writeMapleAsciiString(msg);
        mplew.write(channel - 1);
        mplew.write(whisper ? 1 : 0);
        if (item == null) {
            mplew.write(0);
        } else {
            PacketHelper.addItemInfo(mplew, item, false, false, true);
        }
        return mplew.getPacket();
    }

    public static byte[] echoMegaphone(String name, String message) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ECHO_MESSAGE.getValue());
        mplew.write(0);
        mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
        mplew.writeMapleAsciiString(name);
        mplew.writeMapleAsciiString(message);

        return mplew.getPacket();
    }

    public static byte[] spawnNPC(MapleNPC life, boolean show) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPAWN_NPC.getValue());
        mplew.writeInt(life.getObjectId());
        mplew.writeInt(life.getId());
        mplew.writeShort(life.getPosition().x);
        mplew.writeShort(life.getCy());
        mplew.write(life.getF() == 1 ? 0 : 1);
        mplew.writeShort(life.getFh());
        mplew.writeShort(life.getRx0());
        mplew.writeShort(life.getRx1());
        mplew.write(show ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] removeNPC(int objectid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.REMOVE_NPC.getValue());
        mplew.writeInt(objectid);

        return mplew.getPacket();
    }

    public static byte[] removeNPCController(int objectid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPAWN_NPC_REQUEST_CONTROLLER.getValue());
        mplew.write(0);
        mplew.writeInt(objectid);

        return mplew.getPacket();
    }

    public static byte[] spawnNPCRequestController(MapleNPC life, boolean MiniMap) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPAWN_NPC_REQUEST_CONTROLLER.getValue());
        mplew.write(1);
        mplew.writeInt(life.getObjectId());
        mplew.writeInt(life.getId());
        mplew.writeShort(life.getPosition().x);
        mplew.writeShort(life.getCy());
        mplew.write(life.getF() == 1 ? 0 : 1);
        mplew.writeShort(life.getFh());
        mplew.writeShort(life.getRx0());
        mplew.writeShort(life.getRx1());
        mplew.write(MiniMap ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] spawnPlayerNPC(PlayerNPC npc) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_NPC.getValue());
        mplew.write(npc.getF() == 1 ? 0 : 1);
        mplew.writeInt(npc.getId());
        mplew.writeMapleAsciiString(npc.getName());
        mplew.write(npc.getGender());
        mplew.write(npc.getSkin());
        mplew.writeInt(npc.getFace());
        mplew.writeInt(0);
        mplew.write(0);
        mplew.writeInt(npc.getHair());
        Map<Byte, Integer> equip = npc.getEquips();
        Map<Byte, Integer> myEquip = new LinkedHashMap();
        Map<Byte, Integer> maskedEquip = new LinkedHashMap();
        for (Entry<Byte, Integer> position : equip.entrySet()) {
            byte pos = (byte) (((Byte) position.getKey()).byteValue() * -1);
            if ((pos < 100) && (myEquip.get(Byte.valueOf(pos)) == null)) {
                myEquip.put(Byte.valueOf(pos), position.getValue());
            } else if ((pos > 100) && (pos != 111)) {
                pos = (byte) (pos - 100);
                if (myEquip.get(Byte.valueOf(pos)) != null) {
                    maskedEquip.put(Byte.valueOf(pos), myEquip.get(Byte.valueOf(pos)));
                }
                myEquip.put(Byte.valueOf(pos), position.getValue());
            } else if (myEquip.get(Byte.valueOf(pos)) != null) {
                maskedEquip.put(Byte.valueOf(pos), position.getValue());
            }
        }
        for (Entry<Byte, Integer> entry : myEquip.entrySet()) {
            mplew.write(((Byte) entry.getKey()).byteValue());
            mplew.writeInt(((Integer) entry.getValue()).intValue());
        }
        mplew.write(255);
        for (Entry<Byte, Integer> entry : maskedEquip.entrySet()) {
            mplew.write(((Byte) entry.getKey()).byteValue());
            mplew.writeInt(((Integer) entry.getValue()).intValue());
        }
        mplew.write(255);
        Integer cWeapon = (Integer) equip.get(((byte) -111));
        if (cWeapon != null) {
            mplew.writeInt(cWeapon.intValue());
        } else {
            mplew.writeInt(0);
        }
        for (int i = 0; i < 3; i++) {
            mplew.writeInt(npc.getPet(i));
        }

        return mplew.getPacket();
    }

    public static byte[] getChatText(int cidfrom, String text, boolean whiteBG, int show) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CHATTEXT.getValue());
        mplew.writeInt(cidfrom);
        mplew.write(whiteBG ? 1 : 0);
        mplew.writeMapleAsciiString(text);
        mplew.write(show);

        return mplew.getPacket();
    }

    public static byte[] GameMaster_Func(int value) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GM_EFFECT.getValue());
        mplew.write(value);
        mplew.writeZeroBytes(17);

        return mplew.getPacket();
    }

    public static byte[] AranCombo(int value) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ARAN_COMBO.getValue());
        mplew.writeInt(value);

        return mplew.getPacket();
    }

    public static byte[] rechargeCombo(int value) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ARAN_COMBO_RECHARGE.getValue());
        mplew.writeInt(value);

        return mplew.getPacket();
    }

    public static byte[] getPacketFromHexString(String hex) {
        return HexTool.getByteArrayFromHexString(hex);
    }

    public static byte[] GainEXP_Monster(int gain, boolean white, int 组队经验, int 精灵祝福经验, int 道具佩戴经验, int 召回戒指经验, int Sidekick_Bonus_EXP, int 网吧特别经验, int 结婚奖励经验) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(3);
        mplew.write(white ? 1 : 0);
        mplew.writeInt(gain);
        mplew.write(0);

        mplew.writeInt(0);
        mplew.writeShort(0);
        mplew.writeInt(结婚奖励经验);
        mplew.writeInt(召回戒指经验);
        mplew.write(0);
        mplew.writeInt(组队经验);
        mplew.writeInt(道具佩戴经验);
        mplew.writeInt(网吧特别经验);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(精灵祝福经验);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] GainEXP_Others(int gain, boolean inChat, boolean white) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(3);
        mplew.write(white ? 1 : 0);
        mplew.writeInt(gain);
        mplew.write(inChat ? 1 : 0);
        mplew.writeZeroBytes(63);
        mplew.writeZeroBytes(8);
        if (inChat) {
            mplew.write(0);
        }

        return mplew.getPacket();
    }

    public static byte[] getShowFameGain(int gain) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(5);
        mplew.writeInt(gain);

        return mplew.getPacket();
    }

    public static byte[] showMesoGain(int gain, boolean inChat) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        if (!inChat) {
            mplew.write(0);
            mplew.write(1);
            mplew.write(0);
            mplew.writeInt(gain);
            mplew.writeShort(0);
        } else {
            mplew.write(6);
            mplew.writeInt(gain);
            mplew.writeInt(-1);
        }

        return mplew.getPacket();
    }

    public static byte[] getShowItemGain(int itemId, short quantity) {
        return getShowItemGain(itemId, quantity, false);
    }

    public static byte[] getShowItemGain(int itemId, short quantity, boolean inChat) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        if (inChat) {
            mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
            mplew.write(5);
            mplew.write(1);
            mplew.writeInt(itemId);
            mplew.writeInt(quantity);
        } else {
            mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
            mplew.writeShort(0);
            mplew.writeInt(itemId);
            mplew.writeInt(quantity);
        }
        return mplew.getPacket();
    }

    public static byte[] getShowItemGain(Map<Integer, Integer> showItems) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
        mplew.write(5);
        mplew.write(showItems.size());
        for (Map.Entry items : showItems.entrySet()) {
            mplew.writeInt(((Integer) items.getKey()).intValue());
            mplew.writeInt(((Integer) items.getValue()).intValue());
        }
        return mplew.getPacket();
    }

    public static byte[] getShowItemGain(List<Pair<Integer, Integer>> showItems) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
        mplew.write(5);
        mplew.write(showItems.size());
        for (Pair items : showItems) {
            mplew.writeInt(((Integer) items.left).intValue());
            mplew.writeInt(((Integer) items.right).intValue());
        }
        return mplew.getPacket();
    }

    public static byte[] showRewardItemAnimation(int itemId, String effect) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
        mplew.write(17);
        mplew.writeInt(itemId);
        mplew.write((effect != null) && (effect.length() > 0) ? 1 : 0);
        if ((effect != null) && (effect.length() > 0)) {
            mplew.writeMapleAsciiString(effect);
        }

        return mplew.getPacket();
    }

    public static byte[] showRewardItemAnimation(int itemId, String effect, int from_playerid) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
        mplew.writeInt(from_playerid);
        mplew.write(17);
        mplew.writeInt(itemId);
        mplew.write((effect != null) && (effect.length() > 0) ? 1 : 0);
        if ((effect != null) && (effect.length() > 0)) {
            mplew.writeMapleAsciiString(effect);
        }

        return mplew.getPacket();
    }

    public static byte[] dropItemFromMapObject(MapleMapItem drop, Point dropfrom, Point dropto, byte mod) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.DROP_ITEM_FROM_MAPOBJECT.getValue());
        mplew.write(mod);
        mplew.writeInt(drop.getObjectId());
        mplew.write(drop.getMeso() > 0 ? 1 : 0);
        mplew.writeInt(drop.getItemId());
        mplew.writeInt(drop.getOwner());
        mplew.write(drop.getDropType());
        mplew.writePos(dropto);
        mplew.writeInt(0);
        if (mod != 2) {
            mplew.writePos(dropfrom);
            mplew.writeShort(0);
        }
        if (drop.getMeso() == 0) {
            PacketHelper.addExpirationTime(mplew, drop.getItem().getExpiration());
        }
        mplew.writeShort(drop.isPlayerDrop() ? 0 : 1);

        return mplew.getPacket();
    }

    public static byte[] spawnPlayerMapobject(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPAWN_PLAYER.getValue());
        mplew.writeInt(chr.getId());
        mplew.write(chr.getLevel());
        mplew.writeMapleAsciiString(chr.getName());
        MapleQuestStatus ultExplorer = chr.getQuestNoAdd(MapleQuest.getInstance(111111));
        if ((ultExplorer != null) && (ultExplorer.getCustomData() != null)) {
            mplew.writeMapleAsciiString(ultExplorer.getCustomData());
        } else {
            mplew.writeMapleAsciiString("");
        }
        
        /*
        if (chr.getGuildId() <= 0) {
            mplew.writeInt(0);
            mplew.writeInt(0);
        } else {
            MapleGuild gs = World.Guild.getGuild(chr.getGuildId());
            if (gs != null) {
                mplew.writeMapleAsciiString(gs.getName());
                mplew.writeShort(gs.getLogoBG());
                mplew.write(gs.getLogoBGColor());
                mplew.writeShort(gs.getLogo());
                mplew.write(gs.getLogoColor());
            } else {
                mplew.writeInt(0);
                mplew.writeInt(0);
            }
        }
        * 
        */
        
        
    String gender = "";
    String reborns = "";

    if (chr.getGender() == 0) {
      gender = "[♂]";
    } else {
      gender = "[♀]";
    }

    reborns = new StringBuilder().append(" <重修").append(chr.getReborns()).append("重天> ").toString();

    if (chr.getGuildId() <= 0) {
      if (LoginServer.isMaxDamage()) {
        //mplew.writeMapleAsciiString(new StringBuilder().append(gender).append(chr.getVipStr()).append(reborns).append(chr.getFsStr()).toString());
        mplew.writeMapleAsciiString(new StringBuilder().append(" ").append(chr.getVipname(2)).append("").append(reborns).toString());
        mplew.write(new byte[6]);
      } else {
        mplew.writeInt(0);
        mplew.writeInt(0);
      }
    } else {
      MapleGuild gs = World.Guild.getGuild(chr.getGuildId());
      if (gs != null) {
        if (LoginServer.isMaxDamage()) {
        //  mplew.writeMapleAsciiString(new StringBuilder().append(" ").append(gender).append("[ ").append(chr.getVipname()).append(" ] ").append(gs.getName()).append(reborns).append(chr.getFsStr()).toString());
          mplew.writeMapleAsciiString(new StringBuilder().append("").append(" ").append(chr.getVipname(2)).append("").append(gs.getName()).append(reborns).toString());
        } else {
          mplew.writeMapleAsciiString(gs.getName());
        }
        mplew.writeShort(gs.getLogoBG());
        mplew.write(gs.getLogoBGColor());
        mplew.writeShort(gs.getLogo());
        mplew.write(gs.getLogoColor());
      } else {
        mplew.writeInt(0);
        mplew.writeInt(0);
      }
      
   }
        
        
        
        List<Pair<Integer, Integer>> buffvalue = new ArrayList();
        int[] mask = new int[GameConstants.MAX_BUFFSTAT];
        mask[0] |= -16777216;
        mask[1] |= 163840;

        if ((chr.getBuffedValue(MapleBuffStat.隐身术) != null) || (chr.isHidden())) {
            if (ServerProperties.ShowPacket()) {
                System.err.println("出现: 隐身术");
            }
            mask[mask.length - 1] |= MapleBuffStat.隐身术.getValue();
        }
        if (chr.getBuffedValue(MapleBuffStat.无形箭弩) != null) {
            if (ServerProperties.ShowPacket()) {
                System.err.println("出现: 无形箭弩");
            }
            mask[mask.length - 1] |= MapleBuffStat.无形箭弩.getValue();
        }
        if (chr.getBuffedValue(MapleBuffStat.斗气集中) != null) {
            if (ServerProperties.ShowPacket()) {
                System.err.println("出现: 斗气集中");
            }
            mask[mask.length - 1] |= MapleBuffStat.斗气集中.getValue();
            buffvalue.add(new Pair(chr.getBuffedValue(MapleBuffStat.斗气集中), 1));
        }
        if (chr.getBuffedValue(MapleBuffStat.属性攻击) != null) {
            if (ServerProperties.ShowPacket()) {
                System.err.println("出现: 属性攻击");
            }
            mask[mask.length - 1] |= MapleBuffStat.属性攻击.getValue();
            buffvalue.add(new Pair(chr.getBuffedValue(MapleBuffStat.属性攻击), 2));
            buffvalue.add(new Pair(chr.getBuffSource(MapleBuffStat.属性攻击), 3));
        }
        if (chr.getBuffedValue(MapleBuffStat.影分身) != null) {
            if (ServerProperties.ShowPacket()) {
                System.err.println("出现: 影分身");
            }
            mask[mask.length - 1] |= MapleBuffStat.影分身.getValue();
            buffvalue.add(new Pair(chr.getBuffedValue(MapleBuffStat.影分身), 2));
            buffvalue.add(new Pair(chr.getBuffSource(MapleBuffStat.影分身), 3));
        }

        if (chr.getBuffedValue(MapleBuffStat.变身术) != null) {
            if (ServerProperties.ShowPacket()) {
                System.err.println("出现: 变身术");
            }
            mask[mask.length - 2] |= MapleBuffStat.变身术.getValue();
            buffvalue.add(new Pair(chr.getStatForBuff(MapleBuffStat.变身术).getMorph(chr), 2));
            buffvalue.add(new Pair(chr.getBuffSource(MapleBuffStat.变身术), 3));
        }
        if (chr.getBuffedValue(MapleBuffStat.金刚霸体) != null) {
            if (ServerProperties.ShowPacket()) {
                System.err.println("出现: 金刚霸体");
            }
            mask[mask.length - 2] |= MapleBuffStat.金刚霸体.getValue();
        }
        if (chr.getBuffedValue(MapleBuffStat.狂暴战魂) != null) {
            if (ServerProperties.ShowPacket()) {
                System.err.println("出现: 狂暴战魂");
            }
            mask[mask.length - 2] |= MapleBuffStat.狂暴战魂.getValue();
        }

        if ((chr.getBuffedValue(MapleBuffStat.风影漫步) != null)
                && (ServerProperties.ShowPacket())) {
            System.err.println("出现: 风影漫步");
        }

        if (chr.getBuffedValue(MapleBuffStat.PYRAMID_PQ) != null) {
            if (ServerProperties.ShowPacket()) {
                System.err.println("出现: PYRAMID_PQ");
            }
            mask[mask.length - 3] |= MapleBuffStat.PYRAMID_PQ.getValue();
            buffvalue.add(new Pair(chr.getBuffedValue(MapleBuffStat.PYRAMID_PQ), 2));
            buffvalue.add(new Pair(chr.getTrueBuffSource(MapleBuffStat.PYRAMID_PQ), 3));
        }
        if (chr.getBuffedValue(MapleBuffStat.飞行骑乘) != null) {
            if (ServerProperties.ShowPacket()) {
                System.err.println("出现: 飞行骑乘");
            }
            mask[mask.length - 3] |= MapleBuffStat.飞行骑乘.getValue();
            buffvalue.add(new Pair(chr.getBuffedValue(MapleBuffStat.飞行骑乘), 2));
        }

        if (chr.getBuffedValue(MapleBuffStat.潜入) != null) {
            if (ServerProperties.ShowPacket()) {
                System.err.println("出现: 潜入");
            }
            mask[mask.length - 4] |= MapleBuffStat.潜入.getValue();
        }
        if (chr.getBuffedValue(MapleBuffStat.金属机甲) != null) {
            if (ServerProperties.ShowPacket()) {
                System.err.println("出现: 金属机甲");
            }
            mask[mask.length - 4] |= MapleBuffStat.金属机甲.getValue();
            buffvalue.add(new Pair(chr.getBuffedValue(MapleBuffStat.金属机甲), 2));
            buffvalue.add(new Pair(chr.getTrueBuffSource(MapleBuffStat.金属机甲), 3));
        }
        if (chr.getBuffedValue(MapleBuffStat.黑暗灵气) != null) {
            if (ServerProperties.ShowPacket()) {
                System.err.println("出现: 黑暗灵气");
            }
            mask[mask.length - 4] |= MapleBuffStat.黑暗灵气.getValue();
            buffvalue.add(new Pair(chr.getBuffedValue(MapleBuffStat.黑暗灵气), 2));
            buffvalue.add(new Pair(chr.getTrueBuffSource(MapleBuffStat.黑暗灵气), 3));
        }
        if (chr.getBuffedValue(MapleBuffStat.蓝色灵气) != null) {
            if (ServerProperties.ShowPacket()) {
                System.err.println("出现: 蓝色灵气");
            }
            mask[mask.length - 4] |= MapleBuffStat.蓝色灵气.getValue();
            buffvalue.add(new Pair(chr.getBuffedValue(MapleBuffStat.蓝色灵气), 2));
            buffvalue.add(new Pair(chr.getTrueBuffSource(MapleBuffStat.蓝色灵气), 3));
        }
        if (chr.getBuffedValue(MapleBuffStat.黄色灵气) != null) {
            if (ServerProperties.ShowPacket()) {
                System.err.println("出现: 黄色灵气");
            }
            mask[mask.length - 4] |= MapleBuffStat.黄色灵气.getValue();
            buffvalue.add(new Pair(chr.getBuffedValue(MapleBuffStat.黄色灵气), 2));
            buffvalue.add(new Pair(chr.getTrueBuffSource(MapleBuffStat.黄色灵气), 3));
        }
        if (chr.getBuffedValue(MapleBuffStat.祝福护甲) != null) {
            if (ServerProperties.ShowPacket()) {
                System.err.println("出现: 祝福护甲");
            }
            mask[mask.length - 4] |= MapleBuffStat.祝福护甲.getValue();
        }
        if (chr.getBuffedValue(MapleBuffStat.GIANT_POTION) != null) {
            if (ServerProperties.ShowPacket()) {
                System.err.println("出现: GIANT_POTION");
            }
            mask[mask.length - 4] |= MapleBuffStat.GIANT_POTION.getValue();
            buffvalue.add(new Pair(chr.getBuffedValue(MapleBuffStat.GIANT_POTION).intValue(), 2));
            buffvalue.add(new Pair(chr.getTrueBuffSource(MapleBuffStat.GIANT_POTION), 3));
        }

        if (chr.getBuffedValue(MapleBuffStat.双弩水盾) != null) {
            if (ServerProperties.ShowPacket()) {
                System.err.println("出现: 双弩水盾");
            }
            mask[mask.length - 1] |= MapleBuffStat.双弩水盾.getValue();
        }
        if (chr.getBuffedValue(MapleBuffStat.精神注入) != null) {
            if (ServerProperties.ShowPacket()) {
                System.err.println("出现: 精神注入");
            }
        }

        if (chr.getBuffedValue(MapleBuffStat.精神连接) != null) {
            if (ServerProperties.ShowPacket()) {
                System.err.println("出现: 精神连接");
            }
        }

        if (chr.getBuffedValue(MapleBuffStat.FAMILIAR_SHADOW) != null) {
            if (ServerProperties.ShowPacket()) {
                System.err.println("出现: FAMILIAR_SHADOW");
            }
            mask[mask.length - 2] |= MapleBuffStat.FAMILIAR_SHADOW.getValue();
            buffvalue.add(new Pair(chr.getBuffedValue(MapleBuffStat.FAMILIAR_SHADOW), 3));
            buffvalue.add(new Pair(chr.getStatForBuff(MapleBuffStat.FAMILIAR_SHADOW).getCharColor(), 3));
        }
        for (int i = 0; i < mask.length; i++) {
            if (ServerProperties.ShowPacket()) {
                System.err.println(new StringBuilder().append("出现: mask [").append(i).append("] ").append(mask[i]).toString());
            }
            mplew.writeInt(mask[i]);
        }

        for (Pair<Integer, Integer> i : buffvalue) {
            if (i.right == 1) {
                if (ServerProperties.ShowPacket()) {
                    System.err.println(new StringBuilder().append("出现: i.right == 1").append(i.left).toString());
                }
                mplew.write(i.left);
            } else if (i.right == 2) {
                if (ServerProperties.ShowPacket()) {
                    System.err.println(new StringBuilder().append("出现: i.right == 2").append(i.left).toString());
                }
                mplew.writeShort(i.left);
            } else if (i.right == 3) {
                if (ServerProperties.ShowPacket()) {
                    System.err.println(new StringBuilder().append("出现: i.right == 3 ").append(i.left).toString());
                }
                mplew.writeInt(i.left);
            }
        }
        mplew.writeInt(-1);
        int CHAR_MAGIC_SPAWN = Randomizer.nextInt();

        mplew.writeShort(0);

        mplew.writeShort(0);
        mplew.writeLong(0);
        mplew.write(1);
        mplew.writeInt(CHAR_MAGIC_SPAWN);

        mplew.writeShort(0);
        mplew.writeLong(0);
        mplew.write(1);
        mplew.writeInt(CHAR_MAGIC_SPAWN);

        mplew.writeShort(0);
        mplew.writeLong(0);
        mplew.write(1);
        mplew.writeInt(CHAR_MAGIC_SPAWN);

        mplew.writeShort(0);
        int buffSrc = chr.getBuffSource(MapleBuffStat.骑兽技能);
        if (ServerProperties.ShowPacket()) {
            System.err.println(new StringBuilder().append("buffSrc: ").append(buffSrc).toString());
        }
        if (buffSrc > 0) {
            Item c_mount = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -123);
            Item mount = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -18);
            if ((GameConstants.getMountItem(buffSrc, chr) == 0) && (c_mount != null) && (chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -124) != null)) {
                mplew.writeInt(c_mount.getItemId());
            } else if ((GameConstants.getMountItem(buffSrc, chr) == 0) && (mount != null) && (chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -19) != null)) {
                mplew.writeInt(mount.getItemId());
            } else {
                mplew.writeInt(GameConstants.getMountItem(buffSrc, chr));
            }
            mplew.writeInt(buffSrc);
        } else {
            mplew.writeLong(0);
        }
        mplew.write(1);
        mplew.writeInt(CHAR_MAGIC_SPAWN);

        mplew.writeLong(0);
        mplew.write(1);
        mplew.writeInt(CHAR_MAGIC_SPAWN);

        mplew.write(1);
        mplew.writeInt(Randomizer.nextInt());
        mplew.writeShort(0);
        mplew.writeLong(0);
        mplew.write(1);
        mplew.writeInt(CHAR_MAGIC_SPAWN);

        mplew.writeLong(0);
        mplew.writeLong(0);
        mplew.write(1);
        mplew.writeInt(CHAR_MAGIC_SPAWN);

        mplew.writeShort(0);
        mplew.writeLong(0);
        mplew.write(1);
        mplew.writeInt(CHAR_MAGIC_SPAWN);

        mplew.writeInt(chr.getJob());
        PacketHelper.addCharLook(mplew, chr, true);

        mplew.writeZeroBytes(20);
        mplew.writeInt(Math.min(250, chr.getInventory(MapleInventoryType.CASH).countById(5110000)));
        mplew.writeInt(0);
        mplew.writeInt(chr.getItemEffect());
        mplew.writeInt(chr.getTitleEffect());
        mplew.writeInt(0);
        mplew.writeLong(0);
        mplew.writeInt(-1);
        mplew.write(0);
        mplew.writeInt(GameConstants.getInventoryType(chr.getChair()) == MapleInventoryType.SETUP ? chr.getChair() : 0);
        mplew.writeInt(0);
        mplew.writePos(chr.getTruePosition());
        mplew.write(chr.getStance());
        mplew.writeShort(0);

        for (MaplePet pet : chr.getPets()) {
            if (pet.getSummoned()) {
                PetPacket.addPetInfo(mplew, chr, pet, true);
            }
        }
        mplew.write(0);

        mplew.writeInt(chr.getMount() != null ? chr.getMount().getLevel() : 1);
        mplew.writeInt(chr.getMount() != null ? chr.getMount().getExp() : 0);
        mplew.writeInt(chr.getMount() != null ? chr.getMount().getFatigue() : 0);

        PacketHelper.addAnnounceBox(mplew, chr);

        mplew.write((chr.getChalkboard() != null) && (chr.getChalkboard().length() > 0) ? 1 : 0);
        if ((chr.getChalkboard() != null) && (chr.getChalkboard().length() > 0)) {
            mplew.writeMapleAsciiString(chr.getChalkboard());
        }

        Triple rings = chr.getRings(false);
        addRingInfo(mplew, (List) rings.getLeft());
        addRingInfo(mplew, (List) rings.getMid());
        addMRingInfo(mplew, (List) rings.getRight(), chr);
        mplew.writeShort(0);
        mplew.writeInt(0);
        boolean pvp = chr.inPVP();
        if (pvp) {
            mplew.write(Integer.parseInt(chr.getEventInstance().getProperty("type")));
        }
        if (chr.getCarnivalParty() != null) {
            mplew.write(chr.getCarnivalParty().getTeam());
        } else if (GameConstants.isTeamMap(chr.getMapId())) {
            mplew.write(chr.getTeam() + (pvp ? 1 : 0));
        }
        return mplew.getPacket();
    }

    public static byte[] removePlayerFromMap(int cid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.REMOVE_PLAYER_FROM_MAP.getValue());
        mplew.writeInt(cid);

        return mplew.getPacket();
    }

    public static byte[] facialExpression(MapleCharacter from, int expression) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.FACIAL_EXPRESSION.getValue());
        mplew.writeInt(from.getId());
        mplew.writeInt(expression);
        mplew.writeInt(-1);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] movePlayer(int cid, List<LifeMovementFragment> moves, Point startPos) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MOVE_PLAYER.getValue());
        mplew.writeInt(cid);
        mplew.writePos(startPos);
        mplew.writeInt(0);
        PacketHelper.serializeMovementList(mplew, moves);

        return mplew.getPacket();
    }

    public static byte[] moveSummon(int cid, int oid, Point startPos, List<LifeMovementFragment> moves) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MOVE_SUMMON.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(oid);
        mplew.writePos(startPos);
        mplew.writeInt(0);
        PacketHelper.serializeMovementList(mplew, moves);

        return mplew.getPacket();
    }

    public static byte[] summonAttack(int cid, int summonSkillId, byte animation, List<Pair<Integer, Integer>> allDamage, int level, boolean darkFlare) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SUMMON_ATTACK.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(summonSkillId);
        mplew.write(level - 1);
        mplew.write(animation);
        mplew.write(allDamage.size());

        for (Pair<Integer,Integer> attackEntry : allDamage) {
            mplew.writeInt(attackEntry.left);
            mplew.write(7);
            mplew.writeInt(attackEntry.right);
        }
        mplew.write(darkFlare ? 1 : 0);
        return mplew.getPacket();
    }

    public static byte[] closeRangeAttack(int cid, int tbyte, int skill, int level, int display, byte speed, List<AttackPair> damage, boolean energy, int lvl, byte mastery, byte unk, int charge) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(energy ? SendPacketOpcode.ENERGY_ATTACK.getValue() : SendPacketOpcode.CLOSE_RANGE_ATTACK.getValue());
        mplew.writeInt(cid);
        mplew.write(tbyte);
        mplew.write(lvl);
        if (skill > 0) {
            mplew.write(level);
            mplew.writeInt(skill);
        } else {
            mplew.write(0);
        }
        mplew.write(unk);
        mplew.writeShort(display);
        mplew.write(speed);
        mplew.write(mastery);
        mplew.writeInt(0);

        if (skill == 4211006) {
            for (AttackPair oned : damage) {
                if (oned.attack != null) {
                    mplew.writeInt(oned.objectid);
                    mplew.write(7);
                    mplew.write(oned.attack.size());
                    for (Pair eachd : oned.attack) {
                        mplew.writeInt(((Integer) eachd.left).intValue());
                    }
                }
            }
        } else {
            for (AttackPair oned : damage) {
                if (oned.attack != null) {
                    mplew.writeInt(oned.objectid);
                    mplew.write(7);
                    for (Pair eachd : oned.attack) {
                        if (((Boolean) eachd.right).booleanValue()) {
                            mplew.writeInt(((Integer) eachd.left).intValue() + -2147483648);
                        } else {
                            mplew.writeInt(((Integer) eachd.left).intValue());
                        }
                    }
                }

            }

        }

        return mplew.getPacket();
    }

    public static byte[] strafeAttack(int cid, byte tbyte, int skill, int level, int display, byte speed, int itemid, List<AttackPair> damage, Point pos, int lvl, byte mastery, byte unk, int ultLevel) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.RANGED_ATTACK.getValue());
        mplew.writeInt(cid);
        mplew.write(tbyte);
        mplew.write(lvl);
        if (skill > 0) {
            mplew.write(level);
            mplew.writeInt(skill);
        } else {
            mplew.write(0);
        }
        mplew.write(ultLevel);
        if (ultLevel > 0) {
            mplew.writeInt(3220010);
        }
        mplew.write(0);
        mplew.writeShort(display);
        mplew.write(speed);
        mplew.write(mastery);
        mplew.writeInt(itemid);

        for (AttackPair oned : damage) {
            if (oned.attack != null) {
                mplew.writeInt(oned.objectid);
                mplew.write(7);
                for (Pair eachd : oned.attack) {
                    if (((Boolean) eachd.right).booleanValue()) {
                        mplew.writeInt(((Integer) eachd.left).intValue() + -2147483648);
                    } else {
                        mplew.writeInt(((Integer) eachd.left).intValue());
                    }
                }
            }
        }
        mplew.writePos(pos);

        return mplew.getPacket();
    }

    public static byte[] rangedAttack(int cid, byte tbyte, int skill, int level, int display, byte speed, int itemid, List<AttackPair> damage, Point pos, int lvl, byte mastery, byte unk) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.RANGED_ATTACK.getValue());
        mplew.writeInt(cid);
        mplew.write(tbyte);
        mplew.write(lvl);
        if (skill > 0) {
            mplew.write(level);
            mplew.writeInt(skill);
        } else {
            mplew.write(0);
        }
        mplew.write(unk);
        mplew.writeShort(display);
        mplew.write(speed);
        mplew.write(mastery);
        mplew.writeInt(itemid);

        for (AttackPair oned : damage) {
            if (oned.attack != null) {
                mplew.writeInt(oned.objectid);
                mplew.write(7);
                for (Pair eachd : oned.attack) {
                    if (((Boolean) eachd.right).booleanValue()) {
                        mplew.writeInt(((Integer) eachd.left).intValue() + -2147483648);
                    } else {
                        mplew.writeInt(((Integer) eachd.left).intValue());
                    }
                }
            }
        }
        mplew.writePos(pos);

        return mplew.getPacket();
    }

    public static byte[] magicAttack(int cid, int tbyte, int skill, int level, int display, byte speed, List<AttackPair> damage, int charge, int lvl, byte unk) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MAGIC_ATTACK.getValue());
        mplew.writeInt(cid);
        mplew.write(tbyte);
        mplew.write(lvl);
        mplew.write(level);
        mplew.writeInt(skill);

        mplew.write(unk);
        mplew.writeShort(display);
        mplew.write(speed);
        mplew.write(0);
        mplew.writeInt(0);

        for (AttackPair oned : damage) {
            if (oned.attack != null) {
                mplew.writeInt(oned.objectid);
                mplew.write(7);
                for (Pair eachd : oned.attack) {
                    if (((Boolean) eachd.right).booleanValue()) {
                        mplew.writeInt(((Integer) eachd.left).intValue() + -2147483648);
                    } else {
                        mplew.writeInt(((Integer) eachd.left).intValue());
                    }
                }
            }
        }
        if (charge > 0) {
            mplew.writeInt(charge);
        }
        return mplew.getPacket();
    }

    public static byte[] getNPCShop(int sid, MapleShop shop, MapleClient c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.OPEN_NPC_SHOP.getValue());
        mplew.writeInt(0);
        mplew.writeInt(sid);
        PacketHelper.addShopInfo(mplew, shop, c);
        return mplew.getPacket();
    }

    public static byte[] confirmShopTransaction(byte code, MapleShop shop, MapleClient c, int indexBought) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CONFIRM_SHOP_TRANSACTION.getValue());

        mplew.write(code);
        if (code == 4) {
            mplew.writeInt(0);
            mplew.writeInt(shop.getNpcId());
            PacketHelper.addShopInfo(mplew, shop, c);
        } else {
            mplew.write(indexBought >= 0 ? 1 : 0);
            if (indexBought >= 0) {
                mplew.writeInt(indexBought);
            }
        }
        return mplew.getPacket();
    }

    public static byte[] addInventorySlot(MapleInventoryType type, Item item) {
        return addInventorySlot(type, item, false);
    }

    public static byte[] addInventorySlot(MapleInventoryType type, Item item, boolean fromDrop) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
        mplew.write(fromDrop ? 1 : 0);
        mplew.writeShort(1);
        mplew.write((item.getPosition() > 100) && (type == MapleInventoryType.ETC) ? 9 : 0);
        mplew.write(type.getType());
        mplew.write(item.getPosition());
        PacketHelper.addItemInfo(mplew, item, true, false);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] updateInventorySlot(MapleInventoryType type, Item item, boolean fromDrop) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
        mplew.write(fromDrop ? 1 : 0);
        mplew.writeShort(1);
        mplew.write((item.getPosition() > 100) && (type == MapleInventoryType.ETC) ? 6 : 1);
        mplew.write(type.getType());
        mplew.writeShort(item.getPosition());
        mplew.writeShort(item.getQuantity());
        return mplew.getPacket();
    }

    public static byte[] moveInventoryItem(MapleInventoryType type, short src, short dst, boolean bag, boolean bothBag) {
        return moveInventoryItem(type, src, dst, (short) -1, bag, bothBag);
    }

    public static byte[] moveInventoryItem(MapleInventoryType type, short src, short dst, short equipIndicator, boolean bag, boolean bothBag) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("01 01 00"));
        mplew.write(bag ? 5 : bothBag ? 8 : 2);
        mplew.write(type.getType());
        mplew.writeShort(src);
        mplew.writeShort(dst);
        if (equipIndicator != -1) {
            mplew.write(equipIndicator);
        }
        if (bag) {
            mplew.write(0);
        }
        return mplew.getPacket();
    }

    public static byte[] moveAndMergeInventoryItem(MapleInventoryType type, short src, short dst, short total, boolean bag, boolean switchSrcDst, boolean bothBag) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("01 02 00"));
        mplew.write((bag) && ((switchSrcDst) || (bothBag)) ? 7 : 3);
        mplew.write(type.getType());
        mplew.writeShort(src);
        mplew.write((bag) && ((!switchSrcDst) || (bothBag)) ? 6 : 1);
        mplew.write(type.getType());
        mplew.writeShort(dst);
        mplew.writeShort(total);

        return mplew.getPacket();
    }

    public static byte[] moveAndMergeWithRestInventoryItem(MapleInventoryType type, short src, short dst, short srcQ, short dstQ, boolean bag, boolean switchSrcDst, boolean bothBag) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("01 02 00"));
        mplew.write((bag) && ((switchSrcDst) || (bothBag)) ? 6 : 1);
        mplew.write(type.getType());
        mplew.writeShort(src);
        mplew.writeShort(srcQ);
        mplew.write((bag) && ((!switchSrcDst) || (bothBag)) ? 6 : 1);
        mplew.write(type.getType());
        mplew.writeShort(dst);
        mplew.writeShort(dstQ);

        return mplew.getPacket();
    }

    public static byte[] clearInventoryItem(MapleInventoryType type, short slot, boolean fromDrop) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
        mplew.write(fromDrop ? 1 : 0);
        mplew.writeShort(1);
        mplew.write((slot > 100) && (type == MapleInventoryType.ETC) ? 7 : 3);
        mplew.write(type.getType());
        mplew.writeShort(slot);

        return mplew.getPacket();
    }

    public static byte[] updateSpecialItemUse(Item item, byte invType, MapleCharacter chr) {
        return updateSpecialItemUse(item, invType, item.getPosition(), false, chr);
    }

    public static byte[] updateSpecialItemUse(Item item, byte invType, short pos, boolean theShort, MapleCharacter chr) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
        mplew.write(0);
        mplew.writeShort(2);

        mplew.write((invType == MapleInventoryType.ETC.getType()) && (pos > 100) ? 7 : 3);
        mplew.write(invType);
        mplew.writeShort(pos);
        mplew.write(0);
        mplew.write(invType);
        if ((item.getType() == 1) || (theShort)) {
            mplew.writeShort(pos);
        } else {
            mplew.write(pos);
        }
        PacketHelper.addItemInfo(mplew, item, true, true, false, false, chr);
        if (pos < 0) {
            mplew.write(2);
        }

        return mplew.getPacket();
    }

    public static byte[] updateSpecialItemUse_(Item item, byte invType, MapleCharacter chr) {
        return updateSpecialItemUse_(item, invType, item.getPosition(), chr);
    }

    public static byte[] updateSpecialItemUse_(Item item, byte invType, short pos, MapleCharacter chr) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
        mplew.write(0);
        mplew.writeShort(1);
        mplew.write(0);
        mplew.write(invType);
        if (item.getType() == 1) {
            mplew.writeShort(pos);
        } else {
            mplew.write(pos);
        }
        PacketHelper.addItemInfo(mplew, item, true, true, false, false, chr);
        if (pos < 0) {
            mplew.write(1);
        }

        return mplew.getPacket();
    }

    public static byte[] scrolledItem(Item scroll, Item item, boolean destroyed, boolean potential) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
        mplew.write(1);
        mplew.writeShort(destroyed ? 2 : 3);
        mplew.write(scroll.getQuantity() > 0 ? 1 : 3);
        mplew.write(GameConstants.getInventoryType(scroll.getItemId()).getType());
        mplew.writeShort(scroll.getPosition());
        if (scroll.getQuantity() > 0) {
            mplew.writeShort(scroll.getQuantity());
        }
        mplew.write(3);
        if (!destroyed) {
            mplew.write(MapleInventoryType.EQUIP.getType());
            mplew.writeShort(item.getPosition());
            mplew.write(0);
        }
        mplew.write(MapleInventoryType.EQUIP.getType());
        mplew.writeShort(item.getPosition());
        if (!destroyed) {
            PacketHelper.addItemInfo(mplew, item, true, true);
        }
        if (!potential) {
            mplew.write(7);
        }

        return mplew.getPacket();
    }

    public static byte[] moveAndUpgradeItem(MapleInventoryType type, Item item, short oldpos, short newpos, MapleCharacter chr) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
        mplew.write(1);
        mplew.writeShort(3);
        mplew.write((type == MapleInventoryType.ETC) && (newpos > 100) ? 7 : 3);
        mplew.write(type.getType());
        mplew.writeShort(oldpos);
        mplew.write(0);
        mplew.write(1);
        mplew.writeShort(oldpos);
        PacketHelper.addItemInfo(mplew, item, true, true, false, false, chr);
        mplew.write(2);
        mplew.write(type.getType());
        mplew.writeShort(oldpos);
        mplew.writeShort(newpos);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static byte[] getScrollEffect(int chrId, int scroll, int toScroll) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_SCROLL_EFFECT.getValue());
        mplew.writeInt(chrId);
        mplew.writeShort(1);
        mplew.writeInt(scroll);
        mplew.writeInt(toScroll);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] getScrollEffect(int chrId, Equip.ScrollResult scrollSuccess, boolean legendarySpirit, boolean whiteScroll, int scroll, int toScroll) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_SCROLL_EFFECT.getValue());
        mplew.writeInt(chrId);
        if (scrollSuccess == Equip.ScrollResult.成功) {
            mplew.writeShort(1);
        } else if (scrollSuccess == Equip.ScrollResult.消失) {
            mplew.writeShort(2);
        } else {
            mplew.writeShort(0);
        }

        mplew.writeInt(scroll);
        mplew.writeInt(toScroll);
        mplew.write(legendarySpirit ? 1 : 0);
        if (whiteScroll) {
            mplew.write(whiteScroll ? 1 : 0);
        }
        return mplew.getPacket();
    }

    public static byte[] getPotentialEffect(int chrId, int itemid) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_MAGNIFYING_EFFECT.getValue());
        mplew.writeInt(chrId);
        mplew.write(1);
        mplew.writeInt(itemid);
        return mplew.getPacket();
    }

    public static byte[] 放大镜效果(int chr, short pos) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_MAGNIFYING_EFFECT.getValue());
        mplew.writeInt(chr);
        mplew.writeShort(pos);

        return mplew.getPacket();
    }

    public static byte[] 魔方光效(boolean fireworks, int chr, boolean success, int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_POTENTIAL_RESET.getValue());
        mplew.writeInt(chr);
        mplew.write(success ? 1 : 0);
        mplew.writeInt(itemid);

        return mplew.getPacket();
    }

    public static byte[] showNebuliteEffect(int chr, boolean success) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_NEBULITE_EFFECT.getValue());
        mplew.writeInt(chr);
        mplew.write(success ? 1 : 0);
        mplew.writeMapleAsciiString(success ? "镶嵌星岩成功." : "镶嵌星岩失败.");

        return mplew.getPacket();
    }

    public static byte[] useNebuliteFusion(int cid, int itemId, boolean success) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_FUSION_EFFECT.getValue());
        mplew.writeInt(cid);
        mplew.write(success ? 1 : 0);
        mplew.writeInt(itemId);

        return mplew.getPacket();
    }

    public static byte[] ItemMaker_Success() {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
        mplew.write(20);
        mplew.writeZeroBytes(4);

        return mplew.getPacket();
    }

    public static byte[] ItemMaker_Success_3rdParty(int from_playerid) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
        mplew.writeInt(from_playerid);
        mplew.write(20);
        mplew.writeZeroBytes(4);

        return mplew.getPacket();
    }

    public static byte[] explodeDrop(int oid) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.REMOVE_ITEM_FROM_MAP.getValue());
        mplew.write(4);
        mplew.writeInt(oid);
        mplew.writeShort(655);

        return mplew.getPacket();
    }

    public static byte[] removeItemFromMap(int oid, int animation, int cid) {
        return removeItemFromMap(oid, animation, cid, 0);
    }

    public static byte[] removeItemFromMap(int oid, int animation, int cid, int slot) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.REMOVE_ITEM_FROM_MAP.getValue());
        mplew.write(animation);
        mplew.writeInt(oid);
        if (animation >= 2) {
            mplew.writeInt(cid);
            if (animation == 5) {
                mplew.writeInt(slot);
            }
        }
        return mplew.getPacket();
    }

    public static byte[] updateCharLook(MapleCharacter chr) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.UPDATE_CHAR_LOOK.getValue());
        mplew.writeInt(chr.getId());
        mplew.write(1);
        PacketHelper.addCharLook(mplew, chr, false);
        Triple rings = chr.getRings(false);
        addRingInfo(mplew, (List) rings.getLeft());
        addRingInfo(mplew, (List) rings.getMid());
        addMRingInfo(mplew, (List) rings.getRight(), chr);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static void addRingInfo(MaplePacketLittleEndianWriter mplew, List<MapleRing> rings) {
        mplew.write(rings.size());
        for (MapleRing ring : rings) {
            mplew.writeInt(1);
            mplew.writeLong(ring.getRingId());
            mplew.writeLong(ring.getPartnerRingId());
            mplew.writeInt(ring.getItemId());
        }
    }

    public static void addMRingInfo(MaplePacketLittleEndianWriter mplew, List<MapleRing> rings, MapleCharacter chr) {
        mplew.write(rings.size());
        for (MapleRing ring : rings) {
            mplew.writeInt(1);
            mplew.writeInt(chr.getId());
            mplew.writeInt(ring.getPartnerChrId());
            mplew.writeInt(ring.getItemId());
        }
    }

    public static byte[] dropInventoryItem(MapleInventoryType type, short src) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("01 01 00 03"));
        mplew.write(type.getType());
        mplew.writeShort(src);
        if (src < 0) {
            mplew.write(1);
        }
        return mplew.getPacket();
    }

    public static byte[] dropInventoryItemUpdate(MapleInventoryType type, Item item) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("01 01 00 01"));
        mplew.write(type.getType());
        mplew.writeShort(item.getPosition());
        mplew.writeShort(item.getQuantity());

        return mplew.getPacket();
    }

    public static byte[] damagePlayer(int cid, int type, int damage, int monsteridfrom, byte direction, int skillid, int pDMG, boolean pPhysical, int pID, byte pType, Point pPos, byte offset, int offset_d, int fake) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.DAMAGE_PLAYER.getValue());
        mplew.writeInt(cid);
        mplew.write(type);
        mplew.writeInt(damage);
        mplew.write(0);
        if (type >= -1) {
            mplew.writeInt(monsteridfrom);
            mplew.write(direction);
            mplew.writeInt(skillid);
            mplew.writeInt(pDMG);
            mplew.write(0);
            if (pDMG > 0) {
                mplew.write(pPhysical ? 1 : 0);
                mplew.writeInt(pID);
                mplew.write(pType);
                mplew.writePos(pPos);
            }
            mplew.write(offset);
            if (offset == 1) {
                mplew.writeInt(offset_d);
            }
        }
        mplew.writeInt(damage);
        if ((damage <= 0) || (fake > 0)) {
            mplew.writeInt(fake);
        }
        return mplew.getPacket();
    }

    public static byte[] updateQuest(MapleQuestStatus quest) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(1);
        mplew.writeShort(quest.getQuest().getId());
        mplew.write(quest.getStatus());
        switch (quest.getStatus()) {
            case 0:
                mplew.writeZeroBytes(10);
                break;
            case 1:
                mplew.writeMapleAsciiString(quest.getCustomData() != null ? quest.getCustomData() : "");
                break;
            case 2:
                mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
        }

        return mplew.getPacket();
    }

    public static byte[] updateInfoQuest(int quest, String data) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(12);
        mplew.writeShort(quest);
        mplew.writeMapleAsciiString(data);

        return mplew.getPacket();
    }

    public static byte[] updateQuestInfo(MapleCharacter c, int quest, int npc, byte progress) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.UPDATE_QUEST_INFO.getValue());
        mplew.write(progress);
        mplew.writeShort(quest);
        mplew.writeInt(npc);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] updateQuestFinish(int quest, int npc, int nextquest) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.UPDATE_QUEST_INFO.getValue());
        mplew.write(10);
        mplew.writeShort(quest);
        mplew.writeInt(npc);
        mplew.writeShort(nextquest);
        return mplew.getPacket();
    }

    public static byte[] updateMedalQuestInfo(byte op, int itemId) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.UPDATE_MEDAL_QUEST.getValue());

        mplew.write(op);
        mplew.writeInt(itemId);

        return mplew.getPacket();
    }

    public static byte[] charInfo(MapleCharacter chr, boolean isSelf) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CHAR_INFO.getValue());
        mplew.writeInt(chr.getId());//玩家ID
        mplew.write(chr.getLevel());//等级
        mplew.writeShort(chr.getJob());//职业ID
        mplew.writeShort(0);//104
        mplew.write(chr.getStat().pvpRank);//PK等级
        mplew.writeInt(chr.getFame());//人气
        mplew.write(chr.getMarriageId() > 0 ? 1 : 0);//是否结婚

        List<Integer> prof = chr.getProfessions();
        mplew.write(prof.size());//专业技术 数量
        for (int i : prof) {
            mplew.writeShort(i);//专业技术 ID
        }

        if (chr.getGuildId() <= 0) {//家族
            mplew.writeMapleAsciiString("-");
            mplew.writeMapleAsciiString("");
        } else {
            MapleGuild gs = World.Guild.getGuild(chr.getGuildId());
            if (gs != null) {
                mplew.writeMapleAsciiString(gs.getName());
                if (gs.getAllianceId() > 0) {
                    MapleGuildAlliance allianceName = World.Alliance.getAlliance(gs.getAllianceId());
                    if (allianceName != null) {
                        mplew.writeMapleAsciiString(allianceName.getName());
                    } else {
                        mplew.writeMapleAsciiString("");
                    }
                } else {
                    mplew.writeMapleAsciiString("");
                }
            } else {
                mplew.writeMapleAsciiString("-");
                mplew.writeMapleAsciiString("");
            }
        }

        mplew.write(-1);//没宠物的时候发送-1 这个是宠物格子.
       // mplew.write(isSelf ? 1 : 0);//查看自己的个人信息还是其他玩家的
        mplew.write(0);
        
        mplew.write(chr.getPet(0) != null ? 1 : 0);//是否有宠物
        for (MaplePet pet : chr.getPets()) {
            if (pet.getSummoned()) {
                mplew.write(1);
                mplew.writeInt(chr.getPetIndex(pet));
                mplew.writeInt(pet.getPetItemId());
                mplew.writeMapleAsciiString(pet.getName());
                mplew.write(pet.getLevel());
                mplew.writeShort(pet.getCloseness());
                mplew.write(pet.getFullness());
                mplew.writeShort(pet.getFlags());
                Item inv = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) (byte) (chr.getPetIndex(pet) == 1 ? -130 : chr.getPetIndex(pet) == 0 ? -114 : -138));
                mplew.writeInt(inv == null ? 0 : inv.getItemId());
            }
        }
        mplew.write(0);//载入宠物完毕

        //载入坐骑
        if ((chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -18) != null) && (chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -19) != null)) {
            int itemid = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -18).getItemId();
            MapleMount mount = chr.getMount();
            boolean canwear = MapleItemInformationProvider.getInstance().getReqLevel(itemid) <= chr.getLevel();
            mplew.write(canwear ? 1 : 0);
            if (canwear) {
                mplew.writeInt(mount.getLevel());
                mplew.writeInt(mount.getExp());
                mplew.writeInt(mount.getFatigue());
            }
        } else {
            mplew.write(0);//载入坐骑完毕
        }

        int wishlistSize = chr.getWishlistSize();
        mplew.write(wishlistSize);
        if (wishlistSize > 0) {//购物车?
            int[] wishlist = chr.getWishlist();
            for (int x = 0; x < wishlistSize; x++) {
                mplew.writeInt(wishlist[x]);
            }

        }

        Item medal = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -26);//勋章
        mplew.writeInt(medal == null ? 0 : medal.getItemId());//正在佩戴的勋章

        List<Pair<Integer, Long>> medalQuests = chr.getCompletedMedals();
        mplew.writeShort(medalQuests.size());//显示人物下面的勋章 数量
        for (Pair<Integer, Long> x : medalQuests) {
            mplew.writeShort(x.left.intValue());//任务ID
            mplew.writeLong(x.right.longValue());//任务完成时间
        }

        for (MapleTrait.MapleTraitType t : MapleTrait.MapleTraitType.values()) {//我的倾向
            mplew.write(chr.getTrait(t).getLevel());//不同倾向的等级
        }

        List<Integer> chairs = new ArrayList();
        for (Item i : chr.getInventory(MapleInventoryType.SETUP).newList()) { //椅子
            if ((i.getItemId() / 10000 == 301) && (!chairs.contains(i.getItemId()))) {
                chairs.add(Integer.valueOf(i.getItemId()));
            }
        }
        mplew.writeInt(chairs.size());//椅子 数量
        for (int i : chairs) {
            mplew.writeInt(i);//椅子 ID
        }

        List<Integer> medals = new ArrayList();
        for (Item i : chr.getInventory(MapleInventoryType.EQUIP).list()) { //勋章
            if ((i.getItemId() >= 1142000) && (i.getItemId() < 1152000)) {
                medals.add(i.getItemId());
            }
        }
        mplew.writeInt(medals.size());// 显示道具下面那个勋章 数量
        for (int i : medals) {
            mplew.writeInt(i);//勋章 ID
        }

        return mplew.getPacket();
    }

    public static byte[] giveDice(int buffid, int skillid, int duration, List<Pair<MapleBuffStat, Integer>> statups) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GIVE_BUFF.getValue());

        PacketHelper.writeBuffMask(mplew, statups);

        mplew.writeShort(Math.max(buffid / 100, Math.max(buffid / 10, buffid % 10)));

        mplew.writeInt(skillid);
        mplew.writeInt(duration);
        mplew.writeShort(0);
        mplew.write(0);

        mplew.writeInt(GameConstants.getDiceStat(buffid, 3));
        mplew.writeInt(GameConstants.getDiceStat(buffid, 3));
        mplew.writeInt(GameConstants.getDiceStat(buffid, 4));
        mplew.writeZeroBytes(20);
        mplew.writeInt(GameConstants.getDiceStat(buffid, 2));
        mplew.writeZeroBytes(12);
        mplew.writeInt(GameConstants.getDiceStat(buffid, 5));
        mplew.writeZeroBytes(16);
        mplew.writeInt(GameConstants.getDiceStat(buffid, 6));
        mplew.writeZeroBytes(16);
        mplew.writeShort(1000);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] giveMount(int buffid, int skillid, List<Pair<MapleBuffStat, Integer>> statups) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GIVE_BUFF.getValue());
        PacketHelper.writeBuffMask(mplew, statups);
        mplew.writeShort(0);
        mplew.write(0);
        mplew.writeInt(buffid);
        mplew.writeInt(skillid);
        mplew.writeInt(0);
        mplew.writeShort(0);
        mplew.writeShort(0);
        mplew.write(7);

        return mplew.getPacket();
    }

    public static byte[] giveArcane(Map<Integer, Integer> statups, int duration) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GIVE_BUFF.getValue());
        PacketHelper.writeSingleMask(mplew, MapleBuffStat.神秘瞄准术);

        mplew.writeShort(0);
        mplew.write(0);
        mplew.writeInt(statups.size());
        for (Map.Entry stat : statups.entrySet()) {
            mplew.writeInt(((Integer) stat.getKey()).intValue());
            mplew.writeLong(((Integer) stat.getValue()).intValue());
            mplew.writeInt(duration);
        }
        mplew.writeShort(0);
        mplew.writeShort(0);
        mplew.write(1);
        mplew.write(0);
        mplew.write(1);
        return mplew.getPacket();
    }

    public static byte[] givePirate(List<Pair<MapleBuffStat, Integer>> statups, int duration, int skillid) {
        boolean infusion = (skillid == 5821009) || (skillid == 15111005) || (skillid % 10000 == 8006);
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GIVE_BUFF.getValue());
        PacketHelper.writeBuffMask(mplew, statups);
        mplew.writeShort(0);
        mplew.write(0);
        for (Pair stat : statups) {
            mplew.writeInt(((Integer) stat.getRight()).intValue());
            mplew.writeLong(skillid);
            mplew.writeZeroBytes(infusion ? 6 : 1);
            mplew.writeShort(duration);
        }
        mplew.writeShort(0);
        mplew.writeShort(0);
        mplew.write(1);

        mplew.write(1);
        return mplew.getPacket();
    }

    public static byte[] giveForeignPirate(List<Pair<MapleBuffStat, Integer>> statups, int duration, int cid, int skillid) {
        boolean infusion = (skillid == 5821009) || (skillid == 15111005);
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GIVE_FOREIGN_BUFF.getValue());
        mplew.writeInt(cid);
        PacketHelper.writeBuffMask(mplew, statups);
        mplew.writeShort(0);
        mplew.write(0);
        for (Pair stat : statups) {
            mplew.writeInt(((Integer) stat.getRight()).intValue());
            mplew.writeLong(skillid);
            mplew.writeZeroBytes(infusion ? 6 : 1);
            mplew.writeShort(duration);
        }
        mplew.writeShort(0);
        mplew.writeShort(0);
        mplew.write(1);
        mplew.write(1);
        return mplew.getPacket();
    }

    public static byte[] giveHoming(int skillid, int mobid, int x) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GIVE_BUFF.getValue());
        PacketHelper.writeSingleMask(mplew, MapleBuffStat.导航辅助);
        mplew.writeShort(0);
        mplew.write(0);
        mplew.writeInt(1);
        mplew.writeLong(skillid);
        mplew.write(0);
        mplew.writeLong(mobid);
        mplew.writeShort(0);
        mplew.writeShort(0);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static byte[] giveEnergyChargeTest(int bar, int bufflength) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GIVE_BUFF.getValue());
        PacketHelper.writeSingleMask(mplew, MapleBuffStat.能量获得);
        mplew.writeShort(0);
        mplew.write(0);
        mplew.writeInt(Math.min(bar, 10000));
        mplew.writeLong(0L);
        mplew.writeShort(0);
        mplew.writeInt(bar >= 10000 ? bufflength : 0);
        mplew.write(4);
        return mplew.getPacket();
    }

    public static byte[] giveEnergyChargeTest(int cid, int bar, int bufflength) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GIVE_FOREIGN_BUFF.getValue());
        mplew.writeInt(cid);
        PacketHelper.writeSingleMask(mplew, MapleBuffStat.能量获得);
        mplew.writeShort(0);
        mplew.write(0);
        mplew.writeInt(Math.min(bar, 10000));
        mplew.writeLong(0L);
        mplew.writeShort(0);
        mplew.writeInt(bar >= 10000 ? bufflength : 0);
        return mplew.getPacket();
    }

    public static byte[] give火焰咆哮(int buffid, int bufflength, List<Pair<MapleBuffStat, Integer>> statups, int equippedSummon) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GIVE_BUFF.getValue());
        PacketHelper.writeBuffMask(mplew, statups);
        mplew.writeZeroBytes(3);
        mplew.writeInt(equippedSummon == 0 ? 1 : 2);
        if (equippedSummon > 0) {
            if ((equippedSummon == 1085) || (equippedSummon == 1090)) {
                mplew.writeInt(-2022746);
                mplew.writeLong(5L);
            } else if (equippedSummon == 1087) {
                mplew.writeInt(-2022747);
                mplew.writeLong(10L);
            } else if (equippedSummon == 1179) {
                mplew.writeInt(-2022823);
                mplew.writeLong(12L);
            }
            mplew.writeInt(600000);
        }
        for (Pair stat : statups) {
            mplew.writeInt(buffid);
            mplew.writeLong(((Integer) stat.getRight()).longValue());
            mplew.writeInt(bufflength);
        }
        mplew.writeShort(1000);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static byte[] giveBuff(int buffid, int bufflength, List<Pair<MapleBuffStat, Integer>> statups, MapleStatEffect effect) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        boolean is机甲 = (buffid == 35001002) || (buffid == 35120000);
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GIVE_BUFF.getValue());

        PacketHelper.writeBuffMask(mplew, statups);
        boolean stacked = false;
        for (Pair<MapleBuffStat, Integer> stat : statups) {
            if (stat.getLeft().canStack()) {
                if (!stacked) {
                    mplew.writeZeroBytes(3);
                    stacked = true;
                }
                if (stat.getLeft() == MapleBuffStat.骑兽技能 && is机甲) {
                    mplew.writeInt(1932016);
                    mplew.writeInt(buffid);
                    mplew.write(0);
                } else {
                    mplew.writeInt(1);
                    mplew.writeInt(buffid);
                    mplew.writeLong(stat.getRight().longValue());
                    if (ServerProperties.ShowPacket()) {
                        log.info(new StringBuilder().append("技能ID: ").append(buffid).append(" LongStat: ").append(((Integer) stat.getRight()).longValue()).append(" 持续时间: ").append(bufflength).append(" 转换: ").append(bufflength / 1000).append("秒").toString());
                    }
                }
            } else {
                if (is机甲 || buffid == 23101003 || buffid == 3120006 || buffid == 3220005) {
                    mplew.writeInt(stat.getRight().intValue());
                } else {
                    mplew.writeShort(stat.getRight().intValue());
                }
                mplew.writeInt(buffid);
                if (ServerProperties.ShowPacket()) {
                    log.info(new StringBuilder().append("技能ID: ").append(buffid).append(" ShortStat: ").append(((Integer) stat.getRight()).intValue()).append(" 持续时间: ").append(bufflength).append(" 转换: ").append(bufflength / 1000).append("秒").toString());
                }
            }
            mplew.writeInt(bufflength);
        }
        mplew.writeShort(0);
        if (effect != null && effect.is祝福护甲()) {
            mplew.writeInt(effect.getEnhancedWatk());
        } else if (effect != null && effect.getCharColor() > 0) {
            mplew.writeInt(effect.getCharColor());
        } else if (effect != null && effect.isInflation()) {
            mplew.writeInt(effect.getInflation());
        }
        mplew.write(0);
        if (buffid == 32111005) {
            mplew.writeInt(0);
        } else if (is机甲) {
            mplew.writeShort(0);
        }
        int a = giveBuffa(buffid);
        if (a > 0) {
            mplew.write(a);
        }
        int b = giveBuffb(buffid);
        mplew.writeShort(b > 0 ? b : 0);
        int c = giveBuffc(buffid);
        if (c > 0);
        mplew.write(0);

        if (effect != null && !effect.is影分身()) {
            mplew.write(4);
        }
        return mplew.getPacket();
    }

    public static int giveBuffa(int buffid) {
        int a = 0;
        switch (buffid) {
            case 32001003://战法 黑暗灵气
            case 32101003://战法 黄色灵气
            case 32111012://战法 蓝色灵气    
            case 32110000://战法 进阶蓝色灵气
            case 32120000://战法 进阶黑色灵气
            case 32120001://战法 进阶黄色灵气
            case 33101006:
                a = 1;
                break;
            case 31101003:// 恶魔猎手 黑暗复仇
            case 31111004:// 恶魔猎手 黑暗忍耐
                a = 4;
        }

        return a;
    }

    public static int giveBuffb(int buffid) {
        int b = 0;
        switch (buffid) {
            case 2311009://主教 神圣魔法盾
                b = 300;
                break;
            case 1111002:// 英雄 斗气集中
            case 3211000:// 弩手 集中精力
            case 3221002:// 弩手 火眼晶晶
            case 3221006:// 弩手 幻影步
            case 4201011:// 侠盗 金钱护盾
            case 4211003:// 侠盗 敛财术
            case 33111004:// 豹弩 致盲
                b = 600;
                break;
            case 21101006:// 战神 冰雪矛
                b = 720;
                break;
            case 1121000:// 英雄 冒险岛勇士
            case 1221000:// 圣骑 冒险岛勇士
            case 1321000:// 黑骑 冒险岛勇士
            case 2121000:// 火毒 冒险岛勇士
            case 2221000:// 冰雷 冒险岛勇士
            case 2321000:// 主教 冒险岛勇士
            case 3121000:// 弓手 冒险岛勇士
            case 3221000:// 弩手 冒险岛勇士
            case 4121000:// 隐士 冒险岛勇士
            case 4221000:// 侠盗 冒险岛勇士
            case 4341000:// 双刀 冒险岛勇士
            case 5321005:// 火炮 冒险岛勇士
            case 5721000:// 传人 冒险岛勇士
            case 5721009:// 传人 海盗气魄 
            case 5821000:// 拳手 冒险岛勇士
            case 5921000:// 枪手 冒险岛勇士
            case 21121000:// 战神 冒险岛勇士
            case 22171000:// 龙神 冒险岛勇士
            case 23111004:// 双弩 火焰咆哮
            case 23111005:// 双弩 水盾
            case 23121005:// 双弩 冒险岛勇士
            case 24121008:// 幻影 冒险岛勇士
            case 31121004:// 恶魔 冒险岛勇士
            case 32121007:// 战法 冒险岛勇士
            case 33121007:// 豹子 冒险岛勇士
            case 35121007:// 机械 冒险岛勇士
          //  case 51121005://米哈尔 冒险岛勇士
                b = 1000;
        }

        return b;
    }

    public static int giveBuffc(int buffid) {
        int c = 0;
        switch (buffid) {
            case 8000:
            case 4001003:
            case 4001005:
            case 5801007:
            case 5811005:
            case 5821003:
            case 10008000:
            case 13111005:
            case 14001003:
            case 14001007:
            case 20008000:
            case 20018000:
            case 20028000:
            case 22141002:
            case 30008000:
            case 30018000:
            case 33111007:
            case 35001002:
            case 35120000:
            case 35121010:
                c = 6;
                break;
            case 1121000:
            case 1221000:
            case 1321000:
            case 2121000:
            case 2221000:
            case 2321000:
            case 3121000:
            case 3221000:
            case 4121000:
            case 4221000:
            case 4341000:
            case 5321005:
            case 5821000:
            case 5921000:
            case 21121000:
            case 22171000:
            case 23121005:
            case 31121004:
            case 32121007:
            case 33121007:
            case 35121007:
           // case 51121005:
                c = 7;
                break;
            case 1002:
            case 10001002:
            case 20001002:
            case 20011002:
            case 32121003:
                c = 8;
                break;
            case 32101003:
            case 32120001:
                c = 9;
                break;
            case 20021001:
            case 30001001:
            case 30011001:
                c = 10;
                break;
            case 5301003:
                c = 15;
                break;
            case 1026:
            case 10001026:
            case 20001026:
            case 20011026:
            case 20021026:
            case 30001026:
            case 30011026:
                c = 17;
        }

        return c;
    }
    
    
  public static int giveBuffd(int buffid) {
    int d = 0;
    switch (buffid) {
    case 1111002:
    case 1121000:
    case 1221000:
    case 1321000:
    case 2121000:
    case 2221000:
    case 2321000:
    case 3121000:
    case 3221000:
    case 4121000:
    case 4221000:
    case 4341000:
    case 5121000:
    case 5221000:
    case 5221006:
    case 5321005:
    case 5721000:
    case 5821000:
    case 5921000:
    case 5921006:
    case 11111001:
    case 21121000:
    case 22171000:
    case 23121005:
    case 24121008:
    case 27121009:
    case 31121004:
    case 32121007:
    case 33121007:
    case 35121007:
    case 51121005:
      return 768;
    }
    return d;
  }
  
  
  public static int giveBuffe(int buffid) {
    int e = 0;
    switch (buffid) {
    case 4001005:
    case 4001006:
    case 4101004:
    case 4201003:
    case 4301003:
    case 4311001:
    case 9001001:
    case 14001007:
    case 14101003:
      return 1000;
    }
    return e;
  }

    public static byte[] giveDebuff(MapleDisease statups, int x, int skillid, int level, int duration) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GIVE_BUFF.getValue());

        PacketHelper.writeSingleMask(mplew, statups);

        mplew.writeShort(x);
        mplew.writeShort(skillid);
        mplew.writeShort(level);
        mplew.writeInt(duration);
        mplew.writeShort(0);
        mplew.writeShort(0);
        mplew.writeShort(1);
        mplew.write(1);

        return mplew.getPacket();
    }

    public static byte[] giveForeignDebuff(int cid, MapleDisease statups, int skillid, int level, int x) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GIVE_FOREIGN_BUFF.getValue());
        mplew.writeInt(cid);

        PacketHelper.writeSingleMask(mplew, statups);
        if (skillid == 125) {
            mplew.writeShort(0);
            mplew.write(0);
        }
        mplew.writeShort(x);
        mplew.writeShort(skillid);
        mplew.writeShort(level);
        mplew.writeShort(0);
        mplew.writeShort(0);
        mplew.write(1);
        mplew.write(1);
        return mplew.getPacket();
    }

    public static byte[] cancelForeignDebuff(int cid, MapleDisease mask) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CANCEL_FOREIGN_BUFF.getValue());
        mplew.writeInt(cid);

        PacketHelper.writeSingleMask(mplew, mask);
        mplew.write(3);
        mplew.write(1);
        return mplew.getPacket();
    }

    public static byte[] showMonsterRiding(int cid, List<Pair<MapleBuffStat, Integer>> statups, int itemId, int skillId) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GIVE_FOREIGN_BUFF.getValue());
        mplew.writeInt(cid);

        PacketHelper.writeBuffMask(mplew, statups);

        mplew.writeShort(0);
        mplew.write(0);
        mplew.writeInt(itemId);
        mplew.writeInt(skillId);
        mplew.writeInt(0);
        mplew.writeShort(0);
        mplew.write(1);
        mplew.write(4);

        return mplew.getPacket();
    }

    public static byte[] giveForeignBuff(int cid, List<Pair<MapleBuffStat, Integer>> statups, MapleStatEffect effect) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GIVE_FOREIGN_BUFF.getValue());
        mplew.writeInt(cid);

        PacketHelper.writeBuffMask(mplew, statups);
        for (Pair statup : statups) {
            if ((statup.getLeft() == MapleBuffStat.影分身) || (statup.getLeft() == MapleBuffStat.金属机甲) || (statup.getLeft() == MapleBuffStat.黑暗灵气) || (statup.getLeft() == MapleBuffStat.黄色灵气) || (statup.getLeft() == MapleBuffStat.蓝色灵气) || (statup.getLeft() == MapleBuffStat.GIANT_POTION) || (statup.getLeft() == MapleBuffStat.精神连接) || (statup.getLeft() == MapleBuffStat.PYRAMID_PQ) || (statup.getLeft() == MapleBuffStat.属性攻击) || (statup.getLeft() == MapleBuffStat.精神注入) || (statup.getLeft() == MapleBuffStat.变身术)) {
                mplew.writeShort(((Integer) statup.getRight()).shortValue());
                mplew.writeInt(effect.isSkill() ? effect.getSourceId() : -effect.getSourceId());
            } else if (statup.getLeft() == MapleBuffStat.FAMILIAR_SHADOW) {
                mplew.writeInt(((Integer) statup.getRight()).intValue());
                mplew.writeInt(effect.getCharColor());
            } else {
                mplew.writeShort(((Integer) statup.getRight()).shortValue());
            }
        }
        mplew.writeShort(0);
        mplew.writeShort(0);
        mplew.write(1);
        mplew.write(1);

        return mplew.getPacket();
    }

    public static byte[] cancelForeignBuff(int cid, List<MapleBuffStat> statups) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CANCEL_FOREIGN_BUFF.getValue());
        mplew.writeInt(cid);
        PacketHelper.writeMask(mplew, statups);
        mplew.write(3);
        mplew.write(1);

        return mplew.getPacket();
    }

    public static byte[] cancelBuff(List<MapleBuffStat> statups) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CANCEL_BUFF.getValue());

        PacketHelper.writeMask(mplew, statups);
        for (MapleBuffStat z : statups) {
            if (z.canStack()) {
                mplew.writeInt(0);
            }
        }
        mplew.write(3);
        mplew.write(0);
        mplew.write(1);

        return mplew.getPacket();
    }

    public static int cancelBuffa(int buffid) {
        int a = 0;
        switch (buffid) {
            case 4001005:
            case 14001007:
                a = 8;
                break;
            case 1121000:
            case 1221000:
            case 1321000:
            case 2121000:
            case 2221000:
            case 2321000:
            case 3121000:
            case 3221000:
            case 4121000:
            case 4221000:
            case 4341000:
            case 5321005:
            case 5821000:
            case 5921000:
            case 21121000:
            case 22171000:
            case 23121005:
            case 31121004:
            case 32121007:
            case 33121007:
            case 35121007:
            case 51121005:
                a = 10;
                break;
            case 32121003:
                a = 12;
                break;
            case 32101003:
            case 32120001:
                a = 18;
        }

        return a;
    }

    public static byte[] cancelHoming() {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CANCEL_BUFF.getValue());

        PacketHelper.writeSingleMask(mplew, MapleBuffStat.导航辅助);

        return mplew.getPacket();
    }

    public static byte[] cancelDebuff(MapleDisease mask) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CANCEL_BUFF.getValue());

        PacketHelper.writeSingleMask(mplew, mask);
        mplew.write(3);
        mplew.write(0);
        mplew.write(1);

        return mplew.getPacket();
    }

    public static byte[] updateMount(MapleCharacter chr, boolean levelup) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.UPDATE_MOUNT.getValue());
        mplew.writeInt(chr.getId());
        mplew.writeInt(chr.getMount().getLevel());
        mplew.writeInt(chr.getMount().getExp());
        mplew.writeInt(chr.getMount().getFatigue());
        mplew.write(levelup ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] mountInfo(MapleCharacter chr) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.UPDATE_MOUNT.getValue());
        mplew.writeInt(chr.getId());
        mplew.write(1);
        mplew.writeInt(chr.getMount().getLevel());
        mplew.writeInt(chr.getMount().getExp());
        mplew.writeInt(chr.getMount().getFatigue());

        return mplew.getPacket();
    }

    public static byte[] getTradeInvite(MapleCharacter c) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(11);
        mplew.write(3);
        mplew.writeMapleAsciiString(c.getName());
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] getTradeMesoSet(byte number, int meso) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(1);
        mplew.write(number);
        mplew.writeInt(meso);

        return mplew.getPacket();
    }

    public static byte[] getTradeItemAdd(byte number, Item item) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(0);
        mplew.write(number);
        PacketHelper.addItemInfo(mplew, item, false, false, true);

        return mplew.getPacket();
    }

    public static byte[] getTradeStart(MapleClient c, MapleTrade trade, byte number) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(10);
        mplew.write(3);
        mplew.write(2);
        mplew.write(number);
        if (number == 1) {
            mplew.write(0);
            PacketHelper.addCharLook(mplew, trade.getPartner().getChr(), false);
            mplew.writeMapleAsciiString(trade.getPartner().getChr().getName());
            mplew.writeShort(trade.getPartner().getChr().getJob());
        }
        mplew.write(number);
        PacketHelper.addCharLook(mplew, c.getPlayer(), false);
        mplew.writeMapleAsciiString(c.getPlayer().getName());
        mplew.writeShort(c.getPlayer().getJob());
        mplew.write(255);

        return mplew.getPacket();
    }

    public static byte[] getTradeConfirmation() {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(2);

        return mplew.getPacket();
    }

    public static byte[] TradeMessage(byte UserSlot, byte message) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(18);
        mplew.write(UserSlot);
        mplew.write(message);

        return mplew.getPacket();
    }

    public static byte[] getTradeCancel(byte UserSlot, int unsuccessful) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(18);
        mplew.write(UserSlot);
        mplew.write(9);

        return mplew.getPacket();
    }

    public static byte[] getNPCTalk(int npc, byte msgType, String talk, String endBytes, byte type) {
        return getNPCTalk(npc, msgType, talk, endBytes, type, npc);
    }

    public static byte[] getNPCTalk(int npc, byte msgType, String talk, String endBytes, byte type, int diffNPC) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
        mplew.write(4);
        mplew.writeInt(npc);
        mplew.write(msgType);
        mplew.write(type);
        if ((type & 0x4) != 0) {
            mplew.writeInt(diffNPC);
        }
        mplew.writeMapleAsciiString(talk);
        mplew.write(HexTool.getByteArrayFromHexString(endBytes));

        return mplew.getPacket();
    }

    public static byte[] getMapSelection(int npcid, String sel) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
        mplew.write(4);
        mplew.writeInt(npcid);
        mplew.writeShort(16);
        mplew.writeInt(npcid == 2083006 ? 1 : npcid == 9010000 ? 3 : 0);
        mplew.writeInt(npcid == 9010022 ? 1 : 0);
        mplew.writeMapleAsciiString(sel);

        return mplew.getPacket();
    }

    public static byte[] getNPCTalkStyle(int npc, String talk, int[] styles, int card, boolean android) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
        mplew.write(4);
        mplew.writeInt(npc);
        mplew.writeShort(android ? 10 : 9);
        mplew.writeMapleAsciiString(talk);
        mplew.write(styles.length);
        for (int i = 0; i < styles.length; i++) {
            mplew.writeInt(styles[i]);
        }
        mplew.writeInt(card);

        return mplew.getPacket();
    }

    public static byte[] getNPCTalkNum(int npc, String talk, int def, int min, int max) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
        mplew.write(4);
        mplew.writeInt(npc);
        mplew.writeShort(4);
        mplew.writeMapleAsciiString(talk);
        mplew.writeInt(def);
        mplew.writeInt(min);
        mplew.writeInt(max);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] getNPCTalkText(int npc, String talk) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
        mplew.write(4);
        mplew.writeInt(npc);
        mplew.writeShort(3);
        mplew.writeMapleAsciiString(talk);
        mplew.writeInt(0);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] getAndroidTalkStyle(int npc, String talk, int[] args) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
        mplew.write(4);
        mplew.writeInt(npc);
        mplew.writeShort(10);
        mplew.writeMapleAsciiString(talk);
        mplew.write(args.length);
        for (int i = 0; i < args.length; i++) {
            mplew.writeInt(args[i]);
        }
        return mplew.getPacket();
    }

    public static byte[] showForeignEffect(int cid, int effect) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
        mplew.writeInt(cid);

        mplew.write(effect);

        return mplew.getPacket();
    }

    public static byte[] showBuffeffect(int cid, int skillid, int effectid, int playerLevel, int skillLevel) {
        return showBuffeffect(cid, skillid, effectid, playerLevel, skillLevel, (byte) 3);
    }

    public static byte[] showBuffeffect(int cid, int skillid, int effectid, int playerLevel, int skillLevel, byte direction) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
        mplew.writeInt(cid);
        mplew.write(effectid);
        mplew.writeInt(skillid);
        mplew.write(playerLevel - 1);
        mplew.write(skillLevel);
        if (direction != 3) {
            mplew.write(direction);
        }
        return mplew.getPacket();
    }

    public static byte[] showOwnBuffEffect(int skillid, int effectid, int playerLevel, int skillLevel) {
        return showOwnBuffEffect(skillid, effectid, playerLevel, skillLevel, (byte) 3);
    }

    public static byte[] showOwnBuffEffect(int skillid, int effectid, int playerLevel, int skillLevel, byte direction) {
        if (ServerProperties.ShowPacket());
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        //天使祝福
        mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
        mplew.write(effectid);
        mplew.writeInt(skillid);
        mplew.write(playerLevel - 1);
        mplew.write(skillLevel);
        if (direction != 3) {
            mplew.write(direction);
        }

        return mplew.getPacket();
    }

    public static byte[] showOwnDiceEffect(int skillid, int effectid, int effectid2, int level) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
        mplew.write(3);
        mplew.writeInt(effectid);
        mplew.writeInt(effectid2);
        mplew.writeInt(skillid);
        mplew.write(level);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] showDiceEffect(int cid, int skillid, int effectid, int effectid2, int level) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
        mplew.writeInt(cid);
        mplew.write(3);
        mplew.writeInt(effectid);
        mplew.writeInt(effectid2);
        mplew.writeInt(skillid);
        mplew.write(level);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] showItemLevelupEffect() {
        return showSpecialEffect(19);
    }

    public static byte[] showForeignItemLevelupEffect(int cid) {
        return showSpecialEffect(cid, 19);
    }

    public static byte[] showSpecialEffect(int effect) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
        mplew.write(effect);

        return mplew.getPacket();
    }

    public static byte[] showSpecialEffect(int cid, int effect) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
        mplew.writeInt(cid);
        mplew.write(effect);

        return mplew.getPacket();
    }

    public static byte[] updateSkill(int skillid, int level, int masterlevel, long expiration) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        boolean isProfession = (skillid == 92000000) || (skillid == 92010000) || (skillid == 92020000) || (skillid == 92030000) || (skillid == 92040000);
        mplew.writeShort(SendPacketOpcode.UPDATE_SKILLS.getValue());
        mplew.writeShort(isProfession ? 0 : 1);
        mplew.writeShort(1);
        mplew.writeInt(skillid);
        mplew.writeInt(level);
        mplew.writeInt(masterlevel);
        PacketHelper.addExpirationTime(mplew, expiration);
        mplew.write(isProfession ? 4 : 3);

        return mplew.getPacket();
    }

    public static byte[] updateQuestMobKills(MapleQuestStatus status) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(1);
        mplew.writeShort(status.getQuest().getId());
        mplew.write(1);

        StringBuilder sb = new StringBuilder();
        for (Iterator i$ = status.getMobKills().values().iterator(); i$.hasNext();) {
            int kills = ((Integer) i$.next()).intValue();
            sb.append(StringUtil.getLeftPaddedStr(String.valueOf(kills), '0', 3));
        }
        mplew.writeMapleAsciiString(sb.toString());

        return mplew.getPacket();
    }

    public static byte[] getShowQuestCompletion(int id) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_QUEST_COMPLETION.getValue());
        mplew.writeShort(id);

        return mplew.getPacket();
    }

    public static byte[] getKeymap(MapleKeyLayout layout) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.KEYMAP.getValue());

        layout.writeData(mplew);

        return mplew.getPacket();
    }

    public static byte[] petAutoHP(int itemId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PET_AUTO_HP.getValue());
        mplew.writeInt(itemId);

        return mplew.getPacket();
    }

    public static byte[] petAutoMP(int itemId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PET_AUTO_MP.getValue());
        mplew.writeInt(itemId);

        return mplew.getPacket();
    }

    public static byte[] petAutoBuff(int skillId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PET_AUTO_BUFF.getValue());
        mplew.writeInt(skillId);

        return mplew.getPacket();
    }

    public static byte[] getWhisper(String sender, int channel, String text) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.WHISPER.getValue());
        mplew.write(18);
        mplew.writeMapleAsciiString(sender);
        mplew.writeShort(channel - 1);
        mplew.writeMapleAsciiString(text);

        return mplew.getPacket();
    }

    public static byte[] getWhisperReply(String target, byte reply) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.WHISPER.getValue());
        mplew.write(10);
        mplew.writeMapleAsciiString(target);
        mplew.write(reply);

        return mplew.getPacket();
    }

    public static byte[] getFindReplyWithMap(String target, int mapid, boolean buddy) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.WHISPER.getValue());
        mplew.write(buddy ? 72 : 9);
        mplew.writeMapleAsciiString(target);
        mplew.write(1);
        mplew.writeInt(mapid);
        mplew.writeZeroBytes(8);

        return mplew.getPacket();
    }

    public static byte[] getFindReply(String target, int channel, boolean buddy) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.WHISPER.getValue());
        mplew.write(buddy ? 72 : 9);
        mplew.writeMapleAsciiString(target);
        mplew.write(3);
        mplew.writeInt(channel - 1);

        return mplew.getPacket();
    }

    public static byte[] getInventoryFull() {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
        mplew.write(1);
        mplew.writeShort(0);

        return mplew.getPacket();
    }

    public static byte[] getInventoryStatus() {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
        mplew.write(0);
        mplew.writeShort(0);

        return mplew.getPacket();
    }

    public static byte[] getShowInventoryFull() {
        return getShowInventoryStatus(255);
    }

    public static byte[] showItemUnavailable() {
        return getShowInventoryStatus(254);
    }

    public static byte[] getShowInventoryStatus(int mode) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(0);
        mplew.write(mode);

        return mplew.getPacket();
    }

    public static byte[] openFishingStorage(int npcId, byte slots) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.FISHING_STORE.getValue());
        mplew.write(33);
        mplew.writeLong(-1L);
        mplew.write(slots);
        mplew.writeLong(0L);
        mplew.writeInt(npcId);
        return mplew.getPacket();
    }

    public static byte[] getStorage(int npcId, byte slots, Collection<Item> items, int meso) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.OPEN_STORAGE.getValue());
        mplew.write(22);
        mplew.writeInt(npcId);
        mplew.write(slots);
        mplew.writeLong(126L);
        mplew.writeInt(meso);

        mplew.write(items.size());
        for (Item item : items) {
            PacketHelper.addItemInfo(mplew, item, true, true);
        }
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] getStorageError(byte op) {//仓库取出物品
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.OPEN_STORAGE.getValue());
        mplew.write(op);

        return mplew.getPacket();
    }

    public static byte[] mesoStorage(byte slots, int meso) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.OPEN_STORAGE.getValue());

        mplew.write(19);
        mplew.write(slots);
        mplew.writeLong(2L);
        mplew.writeInt(meso);

        return mplew.getPacket();
    }

    public static byte[] arrangeStorage(byte slots, Collection<Item> items, boolean changed) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.OPEN_STORAGE.getValue());
        mplew.write(15);
        mplew.write(slots);
        mplew.writeLong(124L);

        mplew.write(items.size());
        for (Item item : items) {
            PacketHelper.addItemInfo(mplew, item, true, true);
        }
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static byte[] storeStorage(byte slots, MapleInventoryType type, Collection<Item> items) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.OPEN_STORAGE.getValue());
        mplew.write(13);
        mplew.write(slots);
        mplew.writeLong(type.getBitfieldEncoding());
        mplew.write(items.size());
        for (Item item : items) {
            PacketHelper.addItemInfo(mplew, item, true, true);
        }
        return mplew.getPacket();
    }

    public static byte[] takeOutStorage(byte slots, MapleInventoryType type, Collection<Item> items) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.OPEN_STORAGE.getValue());
        mplew.write(9);
        mplew.write(slots);
        mplew.writeLong(type.getBitfieldEncoding());
        mplew.write(items.size());
        for (Item item : items) {
            PacketHelper.addItemInfo(mplew, item, true, true);
        }
        return mplew.getPacket();
    }

    public static byte[] fairyPendantMessage(int position, int percent) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.FAIRY_PEND_MSG.getValue());
        mplew.writeInt(position);
        mplew.writeInt(0);
        mplew.writeInt(percent);

        return mplew.getPacket();
    }

    public static byte[] giveFameResponse(int mode, String charname, int newfame) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.FAME_RESPONSE.getValue());
        mplew.write(0);
        mplew.writeMapleAsciiString(charname);
        mplew.write(mode);
        mplew.writeInt(newfame);
        mplew.writeShort(0);

        return mplew.getPacket();
    }

    public static byte[] giveFameErrorResponse(int status) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.FAME_RESPONSE.getValue());
        mplew.write(status);

        return mplew.getPacket();
    }

    public static byte[] receiveFame(int mode, String charnameFrom) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.FAME_RESPONSE.getValue());
        mplew.write(5);
        mplew.writeMapleAsciiString(charnameFrom);
        mplew.write(mode);

        return mplew.getPacket();
    }

    public static byte[] multiChat(String name, String chattext, int mode) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MULTICHAT.getValue());
        mplew.write(mode);
        mplew.writeMapleAsciiString(name);
        mplew.writeMapleAsciiString(chattext);

        return mplew.getPacket();
    }

    public static byte[] getClock(int time) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CLOCK.getValue());
        mplew.write(2);
        mplew.writeInt(time);

        return mplew.getPacket();
    }

    public static byte[] getClockTime(int hour, int min, int sec) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CLOCK.getValue());
        mplew.write(1);
        mplew.write(hour);
        mplew.write(min);
        mplew.write(sec);

        return mplew.getPacket();
    }

    public static byte[] spawnMist(MapleMist mist) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPAWN_MIST.getValue());
        mplew.writeInt(mist.getObjectId());
        mplew.writeInt(mist.isMobMist() ? 0 : mist.isPoisonMist());
        mplew.writeInt(mist.getOwnerId());
        if (mist.getMobSkill() == null) {
            mplew.writeInt(mist.getSourceSkill().getId());
        } else {
            mplew.writeInt(mist.getMobSkill().getSkillId());
        }
        mplew.write(mist.getSkillLevel());
        mplew.writeShort(mist.getSkillDelay());
        mplew.writeRect(mist.getBox());
        mplew.writeLong(0L);
        return mplew.getPacket();
    }

    public static byte[] removeMist(int oid, boolean eruption) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.REMOVE_MIST.getValue());
        mplew.writeInt(oid);
        mplew.write(eruption ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] spawnLove(int oid, int itemid, String name, String msg, Point pos, int ft) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPAWN_LOVE.getValue());
        mplew.writeInt(oid);
        mplew.writeInt(itemid);
        mplew.writeMapleAsciiString(msg);
        mplew.writeMapleAsciiString(name);
        mplew.writeShort(pos.x);
        mplew.writeShort(pos.y + ft);

        return mplew.getPacket();
    }

    public static byte[] removeLove(int oid, int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.REMOVE_LOVE.getValue());
        mplew.writeInt(oid);
        mplew.writeInt(itemid);

        return mplew.getPacket();
    }

    public static byte[] damageSummon(int cid, int summonSkillId, int damage, int unkByte, int monsterIdFrom) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.DAMAGE_SUMMON.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(summonSkillId);
        mplew.write(unkByte);
        mplew.writeInt(damage);
        mplew.writeInt(monsterIdFrom);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] itemEffect(int characterid, int itemid) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_ITEM_EFFECT.getValue());
        mplew.writeInt(characterid);
        mplew.writeInt(itemid);

        return mplew.getPacket();
    }

    public static byte[] showTitleEffect(int characterid, int itemid) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_TITLE_EFFECT.getValue());
        mplew.writeInt(characterid);
        mplew.writeInt(itemid);

        return mplew.getPacket();
    }

    public static byte[] showChair(int characterid, int itemid) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_CHAIR.getValue());
        mplew.writeInt(characterid);
        mplew.writeInt(itemid);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] cancelChair(int id) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CANCEL_CHAIR.getValue());
        if (id == -1) {
            mplew.write(0);
        } else {
            mplew.write(1);
            mplew.writeShort(id);
        }
        return mplew.getPacket();
    }

    public static byte[] spawnReactor(MapleReactor reactor) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }

        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.REACTOR_SPAWN.getValue());
        mplew.writeInt(reactor.getObjectId());
        mplew.writeInt(reactor.getReactorId());
        mplew.write(reactor.getState());
        mplew.writePos(reactor.getTruePosition());
        mplew.write(reactor.getFacingDirection());
        mplew.writeMapleAsciiString(reactor.getName());

        return mplew.getPacket();
    }

    public static byte[] triggerReactor(MapleReactor reactor, int stance) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.REACTOR_HIT.getValue());
        mplew.writeInt(reactor.getObjectId());
        mplew.write(reactor.getState());
        mplew.writePos(reactor.getTruePosition());
        mplew.writeInt(stance);
        return mplew.getPacket();
    }

    public static byte[] destroyReactor(MapleReactor reactor) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.REACTOR_DESTROY.getValue());
        mplew.writeInt(reactor.getObjectId());
        mplew.write(reactor.getState());
        mplew.writePos(reactor.getPosition());

        return mplew.getPacket();
    }

    public static byte[] musicChange(String song) {
        return environmentChange(song, 6);
    }

    public static byte[] showEffect(String effect) {
        return environmentChange(effect, 3);
    }

    public static byte[] playSound(String sound) {
        return environmentChange(sound, 4);
    }

    public static byte[] environmentChange(String env, int mode) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.BOSS_ENV.getValue());
        mplew.write(mode);
        mplew.writeMapleAsciiString(env);

        return mplew.getPacket();
    }

    public static byte[] environmentMove(String env, int mode) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MOVE_ENV.getValue());
        mplew.writeMapleAsciiString(env);
        mplew.writeInt(mode);

        return mplew.getPacket();
    }

    public static byte[] startMapEffect(String msg, int itemid, boolean active) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MAP_EFFECT.getValue());
        mplew.write(active ? 0 : 1);
        mplew.writeInt(itemid);
        if (active) {
            mplew.writeMapleAsciiString(msg);
        }
        return mplew.getPacket();
    }

    public static byte[] removeMapEffect() {
        return startMapEffect(null, 0, false);
    }

    public static byte[] skillEffect(MapleCharacter from, int skillId, byte level, byte flags, byte speed, byte unk) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SKILL_EFFECT.getValue());
        mplew.writeInt(from.getId());
        mplew.writeInt(skillId);
        mplew.write(level);
        mplew.write(flags);
        mplew.write(speed);
        mplew.write(unk);

        return mplew.getPacket();
    }

    public static byte[] skillCancel(MapleCharacter from, int skillId) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CANCEL_SKILL_EFFECT.getValue());
        mplew.writeInt(from.getId());
        mplew.writeInt(skillId);

        return mplew.getPacket();
    }

    public static byte[] showMagnet(int mobid, byte success, int skillId) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_MAGNET.getValue());
        mplew.writeInt(mobid);
        mplew.write(success);
        if (skillId == 30001061) {
            mplew.write(1);
        }

        return mplew.getPacket();
    }

    public static byte[] sendHint(String hint, int width, int height) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        if (width < 1) {
            width = hint.length() * 10;
            if (width < 40) {
                width = 40;
            }
        }
        if (height < 5) {
            height = 5;
        }
        mplew.writeShort(SendPacketOpcode.PLAYER_HINT.getValue());
        mplew.writeMapleAsciiString(hint);
        mplew.writeShort(width);
        mplew.writeShort(height);
        mplew.write(1);
        

        return mplew.getPacket();
    }

    public static byte[] messengerInvite(String from, int messengerid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(3);
        mplew.writeMapleAsciiString(from);
        mplew.write(0);
        mplew.writeInt(messengerid);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] addMessengerPlayer(String from, MapleCharacter chr, int position, int channel) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(0);
        mplew.write(position);
        PacketHelper.addCharLook(mplew, chr, true);
        mplew.writeMapleAsciiString(from);
        mplew.writeShort(channel);

        return mplew.getPacket();
    }

    public static byte[] removeMessengerPlayer(int position) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(2);
        mplew.write(position);

        return mplew.getPacket();
    }

    public static byte[] updateMessengerPlayer(String from, MapleCharacter chr, int position, int channel) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(7);
        mplew.write(position);
        PacketHelper.addCharLook(mplew, chr, true);
        mplew.writeMapleAsciiString(from);
        mplew.writeShort(channel);

        return mplew.getPacket();
    }

    public static byte[] joinMessenger(int position) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(1);
        mplew.write(position);

        return mplew.getPacket();
    }

    public static byte[] messengerChat(String text) {//接受别人的招待邀请
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(6);
        mplew.writeMapleAsciiString(text);

        return mplew.getPacket();
    }

    public static byte[] messengerNote(String text, int mode, int mode2) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(mode);
        mplew.writeMapleAsciiString(text);
        mplew.write(mode2);

        return mplew.getPacket();
    }

    public static byte[] getFindReplyWithCS(String target, boolean buddy) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.WHISPER.getValue());
        mplew.write(buddy ? 72 : 9);
        mplew.writeMapleAsciiString(target);
        mplew.write(2);
        mplew.writeInt(-1);

        return mplew.getPacket();
    }

    public static byte[] getFindReplyWithMTS(String target, boolean buddy) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.WHISPER.getValue());
        mplew.write(buddy ? 72 : 9);
        mplew.writeMapleAsciiString(target);
        mplew.write(0);
        mplew.writeInt(-1);

        return mplew.getPacket();
    }

    public static byte[] showEquipEffect() {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_EQUIP_EFFECT.getValue());

        return mplew.getPacket();
    }

    public static byte[] showEquipEffect(int team) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_EQUIP_EFFECT.getValue());
        mplew.writeShort(team);
        return mplew.getPacket();
    }

    public static byte[] summonSkill(int cid, int summonSkillId, int newStance) {
        if (ServerProperties.ShowPacket());
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SUMMON_SKILL.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(summonSkillId);
        mplew.write(newStance);

        return mplew.getPacket();
    }

    public static byte[] skillCooldown(int sid, int time) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.COOLDOWN.getValue());
        mplew.writeInt(sid);
        mplew.writeInt(time);

        return mplew.getPacket();
    }

    public static byte[] useSkillBook(MapleCharacter chr, int skillid, int maxlevel, boolean canuse, boolean success) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.USE_SKILL_BOOK.getValue());
        mplew.write(0);
        mplew.writeInt(chr.getId());
        mplew.write(1);
        mplew.writeInt(skillid);
        mplew.writeInt(maxlevel);
        mplew.write(canuse ? 1 : 0);
        mplew.write(success ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] getMacros(SkillMacro[] macros) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SKILL_MACRO.getValue());
        int count = 0;
        for (int i = 0; i < 5; i++) {
            if (macros[i] != null) {
                count++;
            }
        }
        mplew.write(count);
        for (int i = 0; i < 5; i++) {
            SkillMacro macro = macros[i];
            if (macro != null) {
                mplew.writeMapleAsciiString(macro.getName());
                mplew.write(macro.getShout());
                mplew.writeInt(macro.getSkill1());
                mplew.writeInt(macro.getSkill2());
                mplew.writeInt(macro.getSkill3());
            }
        }
        return mplew.getPacket();
    }

    public static byte[] updateAriantPQRanking(String name, int score, boolean empty) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ARIANT_PQ_START.getValue());
        mplew.write(empty ? 0 : 1);
        if (!empty) {
            mplew.writeMapleAsciiString(name);
            mplew.writeInt(score);
        }
        return mplew.getPacket();
    }

    public static byte[] catchMonster(int mobid, int itemid, byte success) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CATCH_MONSTER.getValue());
        mplew.writeInt(mobid);
        mplew.writeInt(itemid);
        mplew.write(success);

        return mplew.getPacket();
    }

    public static byte[] catchMob(int mobid, int itemid, byte success) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CATCH_MOB.getValue());
        mplew.write(success);
        mplew.writeInt(itemid);
        mplew.writeInt(mobid);

        return mplew.getPacket();
    }

    public static byte[] showAriantScoreBoard() {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ARIANT_SCOREBOARD.getValue());

        return mplew.getPacket();
    }

    public static byte[] boatPacket(int effect) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.BOAT_EFFECT.getValue());
        mplew.writeShort(effect);

        return mplew.getPacket();
    }

    public static byte[] boatEffect(int effect) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.BOAT_EFF.getValue());
        mplew.writeShort(effect);

        return mplew.getPacket();
    }

    public static byte[] removeItemFromDuey(boolean remove, int Package) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.DUEY.getValue());
        mplew.write(24);
        mplew.writeInt(Package);
        mplew.write(remove ? 3 : 4);

        return mplew.getPacket();
    }

    public static byte[] sendDuey(byte operation, List<MapleDueyActions> packages) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.DUEY.getValue());
        mplew.write(operation);

        switch (operation) {
            case 9:
                mplew.write(1);

                break;
            case 10:
                mplew.write(0);
                mplew.write(packages.size());

                for (MapleDueyActions dp : packages) {
                    mplew.writeInt(dp.getPackageId());
                    mplew.writeAsciiString(dp.getSender(), 13);
                    mplew.writeInt(dp.getMesos());
                    mplew.writeLong(PacketHelper.getTime(dp.getSentTime()));
                    mplew.writeZeroBytes(205);

                    if (dp.getItem() != null) {
                        mplew.write(1);
                        PacketHelper.addItemInfo(mplew, dp.getItem(), true, true);
                    } else {
                        mplew.write(0);
                    }
                }

                mplew.write(0);
        }

        return mplew.getPacket();
    }

    public static byte[] enableTV() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ENABLE_TV.getValue());
        mplew.writeInt(0);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] removeTV() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.REMOVE_TV.getValue());

        return mplew.getPacket();
    }

    public static byte[] sendTV(MapleCharacter chr, List<String> messages, int type, MapleCharacter partner, int delay) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.START_TV.getValue());
        mplew.write(partner != null ? 2 : 1);
        mplew.write(type);
        PacketHelper.addCharLook(mplew, chr, false);
        mplew.writeMapleAsciiString(chr.getName());

        if (partner != null) {
            mplew.writeMapleAsciiString(partner.getName());
        } else {
            mplew.writeShort(0);
        }
        for (int i = 0; i < messages.size(); i++) {
            if ((i == 4) && (((String) messages.get(4)).length() > 15)) {
                mplew.writeMapleAsciiString(((String) messages.get(4)).substring(0, 15));
            } else {
                mplew.writeMapleAsciiString((String) messages.get(i));
            }
        }
        mplew.writeInt(delay);
        if (partner != null) {
            PacketHelper.addCharLook(mplew, partner, false);
        }
        return mplew.getPacket();
    }

    public static byte[] Mulung_DojoUp2() {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
        mplew.write(11);

        return mplew.getPacket();
    }

    public static byte[] showQuestMsg(String msg) {
        return serverNotice(5, msg);
    }

    public static byte[] Mulung_Pts(int recv, int total) {
        return showQuestMsg(new StringBuilder().append("获得了 ").append(recv).append(" 点修炼点数。总修炼点数为 ").append(total).append(" 点。").toString());
    }

    public static byte[] showOXQuiz(int questionSet, int questionId, boolean askQuestion) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.OX_QUIZ.getValue());
        mplew.write(askQuestion ? 1 : 0);
        mplew.write(questionSet);
        mplew.writeShort(questionId);
        return mplew.getPacket();
    }

    public static byte[] leftKnockBack() {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.LEFT_KNOCK_BACK.getValue());
        return mplew.getPacket();
    }

    public static byte[] rollSnowball(int type, MapleSnowball.MapleSnowballs ball1, MapleSnowball.MapleSnowballs ball2) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.ROLL_SNOWBALL.getValue());
        mplew.write(type);
        mplew.writeInt(ball1 == null ? 0 : ball1.getSnowmanHP() / 75);
        mplew.writeInt(ball2 == null ? 0 : ball2.getSnowmanHP() / 75);
        mplew.writeShort(ball1 == null ? 0 : ball1.getPosition());
        mplew.write(0);
        mplew.writeShort(ball2 == null ? 0 : ball2.getPosition());
        mplew.writeZeroBytes(11);
        return mplew.getPacket();
    }

    public static byte[] enterSnowBall() {
        return rollSnowball(0, null, null);
    }

    public static byte[] hitSnowBall(int team, int damage, int distance, int delay) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.HIT_SNOWBALL.getValue());
        mplew.write(team);
        mplew.writeShort(damage);
        mplew.write(distance);
        mplew.write(delay);
        return mplew.getPacket();
    }

    public static byte[] snowballMessage(int team, int message) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SNOWBALL_MESSAGE.getValue());
        mplew.write(team);
        mplew.writeInt(message);
        return mplew.getPacket();
    }

    public static byte[] finishedSort(int type) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }

        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.FINISH_SORT.getValue());
        mplew.write(1);
        mplew.write(type);
        return mplew.getPacket();
    }

    public static byte[] coconutScore(int[] coconutscore) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.COCONUT_SCORE.getValue());
        mplew.writeShort(coconutscore[0]);
        mplew.writeShort(coconutscore[1]);
        return mplew.getPacket();
    }

    public static byte[] hitCoconut(boolean spawn, int id, int type) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.HIT_COCONUT.getValue());
        if (spawn) {
            mplew.write(0);
            mplew.writeInt(128);
        } else {
            mplew.writeInt(id);
            mplew.write(type);
        }
        return mplew.getPacket();
    }

    public static byte[] finishedGather(int type) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }

        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.FINISH_GATHER.getValue());
        mplew.write(1);
        mplew.write(type);
        return mplew.getPacket();
    }

    public static byte[] yellowChat(String msg) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPOUSE_MESSAGE.getValue());
        mplew.writeShort(7);
        mplew.writeMapleAsciiString(msg);
        return mplew.getPacket();
    }

    public static byte[] getPeanutResult(int itemId, short quantity, int itemId2, short quantity2, int ourItem) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PIGMI_REWARD.getValue());
        mplew.writeInt(itemId);
        mplew.writeShort(quantity);
        mplew.writeInt(ourItem);
        mplew.writeInt(itemId2);
        mplew.writeInt(quantity2);

        return mplew.getPacket();
    }

    public static byte[] sendLevelup(boolean family, int level, String name) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.LEVEL_UPDATE.getValue());
        mplew.write(family ? 1 : 2);
        mplew.writeInt(level);
        mplew.writeMapleAsciiString(name);

        return mplew.getPacket();
    }

    public static byte[] sendMarriage(boolean family, String name) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MARRIAGE_UPDATE.getValue());
        mplew.write(family ? 1 : 0);
        mplew.writeMapleAsciiString(name);

        return mplew.getPacket();
    }

    public static byte[] sendJobup(boolean family, int jobid, String name) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.JOB_UPDATE.getValue());
        mplew.write(family ? 1 : 0);
        mplew.writeInt(jobid);
        mplew.writeMapleAsciiString(new StringBuilder().append((GameConstants.GMS) && (!family) ? "> " : "").append(name).toString());

        return mplew.getPacket();
    }

    public static byte[] showHorntailShrine(boolean spawned, int time) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.HORNTAIL_SHRINE.getValue());
        mplew.write(spawned ? 1 : 0);
        mplew.writeInt(time);
        return mplew.getPacket();
    }

    public static byte[] showChaosZakumShrine(boolean spawned, int time) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CHAOS_ZAKUM_SHRINE.getValue());
        mplew.write(spawned ? 1 : 0);
        mplew.writeInt(time);
        return mplew.getPacket();
    }

    public static byte[] showChaosHorntailShrine(boolean spawned, int time) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CHAOS_HORNTAIL_SHRINE.getValue());
        mplew.write(spawned ? 1 : 0);
        mplew.writeInt(time);
        return mplew.getPacket();
    }

    public static byte[] stopClock() {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.STOP_CLOCK.getValue());

        return mplew.getPacket();
    }

    public static byte[] spawnDragon(MapleDragon d) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.DRAGON_SPAWN.getValue());
        mplew.writeInt(d.getOwner());
        mplew.writeInt(d.getPosition().x);
        mplew.writeInt(d.getPosition().y);
        mplew.write(d.getStance());
        mplew.writeShort(0);
        mplew.writeShort(d.getJobId());
        return mplew.getPacket();
    }

    public static byte[] removeDragon(int chrid) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.DRAGON_REMOVE.getValue());
        mplew.writeInt(chrid);
        return mplew.getPacket();
    }

    public static byte[] moveDragon(MapleDragon d, Point startPos, List<LifeMovementFragment> moves) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.DRAGON_MOVE.getValue());
        mplew.writeInt(d.getOwner());
        mplew.writePos(startPos);
        mplew.writeInt(0);

        PacketHelper.serializeMovementList(mplew, moves);

        return mplew.getPacket();
    }

    public static byte[] temporaryStats_Aran() {
        Map stats = new EnumMap(MapleStat.Temp.class);
        stats.put(MapleStat.Temp.力量, Integer.valueOf(999));
        stats.put(MapleStat.Temp.敏捷, Integer.valueOf(999));
        stats.put(MapleStat.Temp.智力, Integer.valueOf(999));
        stats.put(MapleStat.Temp.运气, Integer.valueOf(999));
        stats.put(MapleStat.Temp.物攻, Integer.valueOf(255));
        stats.put(MapleStat.Temp.命中, Integer.valueOf(999));
        stats.put(MapleStat.Temp.回避, Integer.valueOf(999));
        stats.put(MapleStat.Temp.速度, Integer.valueOf(140));
        stats.put(MapleStat.Temp.跳跃, Integer.valueOf(120));
        return temporaryStats(stats);
    }

    public static byte[] temporaryStats_Balrog(MapleCharacter chr) {
        Map stats = new EnumMap(MapleStat.Temp.class);
        int offset = 1 + (chr.getLevel() - 90) / 20;

        stats.put(MapleStat.Temp.力量, Integer.valueOf(chr.getStat().getTotalStr() / offset));
        stats.put(MapleStat.Temp.敏捷, Integer.valueOf(chr.getStat().getTotalDex() / offset));
        stats.put(MapleStat.Temp.智力, Integer.valueOf(chr.getStat().getTotalInt() / offset));
        stats.put(MapleStat.Temp.运气, Integer.valueOf(chr.getStat().getTotalLuk() / offset));
        stats.put(MapleStat.Temp.物攻, Integer.valueOf(chr.getStat().getTotalWatk() / offset));
        stats.put(MapleStat.Temp.物防, Integer.valueOf(chr.getStat().getTotalMagic() / offset));
        return temporaryStats(stats);
    }

    public static byte[] temporaryStats(Map<MapleStat.Temp, Integer> mystats) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.TEMP_STATS.getValue());

        int updateMask = 0;
        for (MapleStat.Temp statupdate : mystats.keySet()) {
            updateMask |= statupdate.getValue();
        }
        mplew.writeInt(updateMask);

        for (Map.Entry statupdate : mystats.entrySet()) {
            Integer value = Integer.valueOf(((MapleStat.Temp) statupdate.getKey()).getValue());

            if (value.intValue() >= 1) {
                if (value.intValue() <= 512) {
                    mplew.writeShort(((Integer) statupdate.getValue()).shortValue());
                } else {
                    mplew.write(((Integer) statupdate.getValue()).byteValue());
                }
            }
        }
        return mplew.getPacket();
    }

    public static byte[] temporaryStats_Reset() {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.TEMP_STATS_RESET.getValue());
        return mplew.getPacket();
    }

    public static byte[] showHpHealed(int cid, int amount) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
        mplew.writeInt(cid);
        mplew.write(12);
        mplew.writeInt(amount);
        return mplew.getPacket();
    }

    public static byte[] showOwnHpHealed(int amount) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
        mplew.write(12);
        mplew.writeInt(amount);
        return mplew.getPacket();
    }

    public static byte[] sendPartyWindow(int npc) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.REPAIR_WINDOW.getValue());

        mplew.writeInt(21);
        mplew.writeInt(npc);
        return mplew.getPacket();
    }

    public static byte[] sendRepairWindow(int npc) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.REPAIR_WINDOW.getValue());

        mplew.writeInt(33);
        mplew.writeInt(npc);
        return mplew.getPacket();
    }

    public static byte[] sendProfessionWindow(int npc) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.REPAIR_WINDOW.getValue());

        mplew.writeInt(42);
        mplew.writeInt(npc);
        return mplew.getPacket();
    }

    public static byte[] sendPVPWindow(int npc) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PVP_WINDOW.getValue());
        mplew.writeInt(50);
        if (npc > 0) {
            mplew.writeInt(npc);
        }
        return mplew.getPacket();
    }

    public static byte[] sendEventWindow(int npc) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PVP_WINDOW.getValue());
        mplew.writeInt(55);
        if (npc > 0) {
            mplew.writeInt(npc);
        }

        return mplew.getPacket();
    }

    public static byte[] sendPVPMaps() {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PVP_INFO.getValue());
        mplew.write(1);
        mplew.writeInt(0);
        for (int i = 0; i < 3; i++) {
            mplew.writeInt(1);
        }
        mplew.writeLong(0L);
        for (int i = 0; i < 3; i++) {
            mplew.writeInt(1);
        }
        mplew.writeLong(0L);
        for (int i = 0; i < 4; i++) {
            mplew.writeInt(1);
        }
        for (int i = 0; i < 10; i++) {
            mplew.writeInt(1);
        }
        mplew.writeInt(14);
        mplew.writeShort(100);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static byte[] sendPyramidUpdate(int amount) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PYRAMID_UPDATE.getValue());
        mplew.writeInt(amount);
        return mplew.getPacket();
    }

    public static byte[] sendPyramidResult(byte rank, int amount) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PYRAMID_RESULT.getValue());
        mplew.write(rank);
        mplew.writeInt(amount);
        return mplew.getPacket();
    }

    public static byte[] sendPyramidEnergy(String type, String amount) {
        return sendString(1, type, amount);
    }

    public static byte[] sendString(int type, String object, String amount) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        switch (type) {
            case 1:
                mplew.writeShort(SendPacketOpcode.ENERGY.getValue());
                break;
            case 2:
                mplew.writeShort(SendPacketOpcode.GHOST_POINT.getValue());
                break;
            case 3:
                mplew.writeShort(SendPacketOpcode.GHOST_STATUS.getValue());
        }

        mplew.writeMapleAsciiString(object);
        mplew.writeMapleAsciiString(amount);
        return mplew.getPacket();
    }

    public static byte[] sendGhostPoint(String type, String amount) {
        return sendString(2, type, amount);
    }

    public static byte[] sendGhostStatus(String type, String amount) {
        return sendString(3, type, amount);
    }

    public static byte[] MulungEnergy(int energy) {
        return sendPyramidEnergy("energy", String.valueOf(energy));
    }

    public static byte[] getPollQuestion() {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GAME_POLL_QUESTION.getValue());
        mplew.writeInt(1);
        mplew.writeInt(14);
        mplew.writeMapleAsciiString(ServerConstants.Poll_Question);
        mplew.writeInt(ServerConstants.Poll_Answers.length);
        for (byte i = 0; i < ServerConstants.Poll_Answers.length; i = (byte) (i + 1)) {
            mplew.writeMapleAsciiString(ServerConstants.Poll_Answers[i]);
        }

        return mplew.getPacket();
    }

    public static byte[] getPollReply(String message) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GAME_POLL_REPLY.getValue());
        mplew.writeMapleAsciiString(message);

        return mplew.getPacket();
    }

    public static byte[] getEvanTutorial(String data) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());

        mplew.write(8);
        mplew.writeInt(0);
        mplew.write(1);
        mplew.write(1);
        mplew.write(1);
        mplew.writeMapleAsciiString(data);

        return mplew.getPacket();
    }

    public static byte[] showEventInstructions() {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GMEVENT_INSTRUCTIONS.getValue());
        mplew.write(0);
        return mplew.getPacket();
    }

    public static byte[] getOwlOpen() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.OWL_OF_MINERVA.getValue());
        mplew.write(9);
        mplew.write(GameConstants.owlItems.length);
        for (int i : GameConstants.owlItems) {
            mplew.writeInt(i);
        }
        return mplew.getPacket();
    }

    public static byte[] getOwlSearched(int itemSearch, List<HiredMerchant> hms) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.OWL_OF_MINERVA.getValue());
        mplew.write(8);
        mplew.writeInt(0);
        mplew.writeInt(itemSearch);
        int size = 0;
        HiredMerchant hm1;
        for (HiredMerchant hm : hms) {
            size += hm.searchItem(itemSearch).size();
        }
        mplew.writeInt(size);
        for (Iterator i = hms.iterator(); i.hasNext();) {
            HiredMerchant hm = (HiredMerchant) i.next();
            List<MaplePlayerShopItem> items = hm.searchItem(itemSearch);
            for (MaplePlayerShopItem item : items) {
                mplew.writeMapleAsciiString(hm.getOwnerName());
                mplew.writeInt(hm.getMap().getId());
                mplew.writeMapleAsciiString(hm.getDescription());
                mplew.writeInt(item.item.getQuantity());
                mplew.writeInt(item.bundles);
                mplew.writeInt(item.price);
                switch (1) {
                    case 0:
                        mplew.writeInt(hm.getOwnerId());
                        break;
                    case 1:
                        mplew.writeInt(hm.getStoreId());
                        break;
                    default:
                        mplew.writeInt(hm.getObjectId());
                }

                mplew.write(hm.getFreeSlot() == -1 ? 1 : 0);
                mplew.write(GameConstants.getInventoryType(itemSearch).getType());
                if (GameConstants.getInventoryType(itemSearch) == MapleInventoryType.EQUIP) {
                    PacketHelper.addItemInfo(mplew, item.item, true, true);
                }
            }
        }
        return mplew.getPacket();
    }

    public static byte[] getRPSMode(byte mode, int mesos, int selection, int answer) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.RPS_GAME.getValue());
        mplew.write(mode);
        switch (mode) {
            case 6:
                if (mesos == -1) {
                    break;
                }
                mplew.writeInt(mesos);
                break;
            case 8:
                mplew.writeInt(9000019);
                break;
            case 11:
                mplew.write(selection);
                mplew.write(answer);
        }

        return mplew.getPacket();
    }

    public static byte[] getSlotUpdate(byte invType, byte newSlots) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.UPDATE_INVENTORY_SLOT.getValue());
        mplew.write(invType);
        mplew.write(newSlots);
        return mplew.getPacket();
    }

    public static byte[] followRequest(int chrid) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.FOLLOW_REQUEST.getValue());
        mplew.writeInt(chrid);
        return mplew.getPacket();
    }

    public static byte[] followEffect(int initiator, int replier, Point toMap) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.FOLLOW_EFFECT.getValue());
        mplew.writeInt(initiator);
        mplew.writeInt(replier);
        if (replier == 0) {
            mplew.write(toMap == null ? 0 : 1);
            if (toMap != null) {
                mplew.writeInt(toMap.x);
                mplew.writeInt(toMap.y);
            }
        }
        return mplew.getPacket();
    }

    public static byte[] getFollowMsg(int opcode) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.FOLLOW_MSG.getValue());

        mplew.writeLong(opcode);
        return mplew.getPacket();
    }

    public static byte[] moveFollow(Point otherStart, Point myStart, Point otherEnd, List<LifeMovementFragment> moves) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.FOLLOW_MOVE.getValue());
        mplew.writePos(otherStart);
        mplew.writePos(myStart);
        PacketHelper.serializeMovementList(mplew, moves);
        mplew.write(17);
        for (int i = 0; i < 8; i++) {
            mplew.write(0);
        }
        mplew.write(0);
        mplew.writePos(otherEnd);
        mplew.writePos(otherStart);

        return mplew.getPacket();
    }

    public static byte[] getFollowMessage(String msg) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPOUSE_MESSAGE.getValue());
        mplew.writeShort(11);
        mplew.writeMapleAsciiString(msg);
        return mplew.getPacket();
    }

    public static byte[] getNodeProperties(MapleMonster objectid, MapleMap map) {
        if (objectid.getNodePacket() != null) {
            return objectid.getNodePacket();
        }
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MONSTER_PROPERTIES.getValue());
        mplew.writeInt(objectid.getObjectId());
        mplew.writeInt(map.getNodes().size());
        mplew.writeInt(objectid.getPosition().x);
        mplew.writeInt(objectid.getPosition().y);
        for (MapleNodeInfo mni : map.getNodes()) {
            mplew.writeInt(mni.x);
            mplew.writeInt(mni.y);
            mplew.writeInt(mni.attr);
            if (mni.attr == 2) {
                mplew.writeInt(500);
            }
        }
        mplew.writeZeroBytes(6);
        objectid.setNodePacket(mplew.getPacket());
        return objectid.getNodePacket();
    }

    public static byte[] getMovingPlatforms(MapleMap map) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MOVE_PLATFORM.getValue());
        mplew.writeInt(map.getPlatforms().size());
        for (MaplePlatform mp : map.getPlatforms()) {
            mplew.writeMapleAsciiString(mp.name);
            mplew.writeInt(mp.start);
            mplew.writeInt(mp.SN.size());
            for (int x = 0; x < mp.SN.size(); x++) {
                mplew.writeInt(((Integer) mp.SN.get(x)).intValue());
            }
            mplew.writeInt(mp.speed);
            mplew.writeInt(mp.x1);
            mplew.writeInt(mp.x2);
            mplew.writeInt(mp.y1);
            mplew.writeInt(mp.y2);
            mplew.writeInt(mp.x1);
            mplew.writeInt(mp.y1);
            mplew.writeShort(mp.r);
        }
        return mplew.getPacket();
    }

    public static byte[] getUpdateEnvironment(MapleMap map) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.UPDATE_ENV.getValue());
        mplew.writeInt(map.getEnvironment().size());
        for (Map.Entry mp : map.getEnvironment().entrySet()) {
            mplew.writeMapleAsciiString((String) mp.getKey());
            mplew.writeInt(((Integer) mp.getValue()).intValue());
        }
        return mplew.getPacket();
    }

    public static byte[] sendEngagementRequest(String name, int cid) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.ENGAGE_REQUEST.getValue());
        mplew.write(0);
        mplew.writeMapleAsciiString(name);
        mplew.writeInt(cid);
        return mplew.getPacket();
    }

    public static byte[] trembleEffect(int type, int delay) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.BOSS_ENV.getValue());
        mplew.write(1);
        mplew.write(type);
        mplew.writeInt(delay);
        return mplew.getPacket();
    }

    public static byte[] sendEngagement(byte msg, int item, MapleCharacter male, MapleCharacter female) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ENGAGE_RESULT.getValue());
        mplew.write(msg);
        switch (msg) {
            case 11:
                mplew.writeInt(0);
                mplew.writeInt(male.getId());
                mplew.writeInt(female.getId());
                mplew.writeShort(1);
                mplew.writeInt(item);
                mplew.writeInt(item);
                mplew.writeAsciiString(male.getName(), 13);
                mplew.writeAsciiString(female.getName(), 13);
        }

        return mplew.getPacket();
    }

    public static byte[] updateJaguar(MapleCharacter from) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.UPDATE_JAGUAR.getValue());
        PacketHelper.addJaguarInfo(mplew, from);

        return mplew.getPacket();
    }

    public static byte[] teslaTriangle(int cid, int sum1, int sum2, int sum3) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.TESLA_TRIANGLE.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(sum1);
        mplew.writeInt(sum2);
        mplew.writeInt(sum3);
        return mplew.getPacket();
    }

    public static byte[] mechPortal(Point pos) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MECH_PORTAL.getValue());
        mplew.writePos(pos);
        return mplew.getPacket();
    }

    public static byte[] spawnMechDoor(MechDoor md, boolean animated) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MECH_DOOR_SPAWN.getValue());
        mplew.write(animated ? 0 : 1);
        mplew.writeInt(md.getOwnerId());
        mplew.writePos(md.getTruePosition());
        mplew.write(md.getId());
        mplew.writeInt(md.getPartyId());
        return mplew.getPacket();
    }

    public static byte[] removeMechDoor(MechDoor md, boolean animated) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MECH_DOOR_REMOVE.getValue());
        mplew.write(animated ? 0 : 1);
        mplew.writeInt(md.getOwnerId());
        mplew.write(md.getId());
        return mplew.getPacket();
    }

    public static byte[] useSPReset(int cid) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SP_RESET.getValue());
        mplew.write(1);
        mplew.writeInt(cid);
        mplew.write(1);

        return mplew.getPacket();
    }

    public static byte[] useAPReset(int cid) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.AP_RESET.getValue());
        mplew.write(1);
        mplew.writeInt(cid);
        mplew.write(1);

        return mplew.getPacket();
    }

    public static byte[] playerDamaged(int cid, int dmg) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PLAYER_DAMAGED.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(dmg);

        return mplew.getPacket();
    }

    public static byte[] pamsSongEffect(int cid) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PAMS_SONG.getValue());
        mplew.writeInt(cid);

        return mplew.getPacket();
    }

    public static byte[] pamsSongUI() {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PAMS_SONG.getValue());
        mplew.writeShort(0);

        return mplew.getPacket();
    }

    public static byte[] englishQuizMsg(String msg) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ENGLISH_QUIZ.getValue());
        mplew.writeInt(20);
        mplew.writeMapleAsciiString(msg);

        return mplew.getPacket();
    }

    public static byte[] report(int err) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.REPORT.getValue());
        mplew.write(err);
        if ((GameConstants.GMS) && (err == 2)) {
            mplew.write(0);
            mplew.writeInt(1);
        }
        return mplew.getPacket();
    }

    public static byte[] sendLieDetector(byte[] image, int attempt) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.LIE_DETECTOR.getValue());
        mplew.write(7);
        mplew.write(3);
        mplew.write(1);
        mplew.write(attempt - 1);
        if (image == null) {
            mplew.writeInt(0);
            return mplew.getPacket();
        }
        mplew.writeInt(image.length);
        mplew.write(image);

        return mplew.getPacket();
    }

    public static byte[] LieDetectorResponse(byte msg) {
        return LieDetectorResponse(msg, (byte) 0);
    }

    public static byte[] LieDetectorResponse(byte msg, byte msg2) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.LIE_DETECTOR.getValue());

        mplew.write(msg);
        mplew.write(msg2);

        return mplew.getPacket();
    }

    public static byte[] enableReport() {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
        mplew.writeShort(SendPacketOpcode.ENABLE_REPORT.getValue());
        mplew.write(1);
        return mplew.getPacket();
    }

    public static byte[] reportResponse(byte mode, int remainingReports) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.REPORT_RESPONSE.getValue());
        mplew.writeShort(mode);
        if (mode == 2) {
            mplew.write(1);
            mplew.writeInt(remainingReports);
        }
        return mplew.getPacket();
    }

    public static byte[] ultimateExplorer() {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ULTIMATE_EXPLORER.getValue());

        return mplew.getPacket();
    }

    public static byte[] GMPoliceMessage() {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GM_POLICE.getValue());
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static byte[] pamSongUI() {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PAM_SONG.getValue());

        return mplew.getPacket();
    }

    public static byte[] dragonBlink(int portalId) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.DRAGON_BLINK.getValue());
        mplew.write(portalId);
        return mplew.getPacket();
    }

    public static byte[] showTraitGain(MapleTrait.MapleTraitType trait, int amount) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(16);
        mplew.writeLong(trait.getStat().getValue());
        mplew.writeInt(amount);
        return mplew.getPacket();
    }

    public static byte[] showTraitMaxed(MapleTrait.MapleTraitType trait) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(GameConstants.GMS ? 17 : 19);
        if (GameConstants.GMS) {
            mplew.writeLong(trait.getStat().getValue());
        } else {
            mplew.writeInt((int) trait.getStat().getValue());
        }
        return mplew.getPacket();
    }

    public static byte[] harvestMessage(int oid, int msg) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.HARVEST_MESSAGE.getValue());
        mplew.writeInt(oid);
        mplew.writeInt(msg);
        return mplew.getPacket();
    }

    public static byte[] showHarvesting(int cid, int tool) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_HARVEST.getValue());
        mplew.writeInt(cid);
        if (tool > 0) {
            mplew.write(1);
            mplew.writeInt(tool);
        } else {
            mplew.write(0);
        }
        return mplew.getPacket();
    }

    public static byte[] harvestResult(int cid, boolean success) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.HARVESTED.getValue());
        mplew.writeInt(cid);
        mplew.write(success ? 1 : 0);
        return mplew.getPacket();
    }

    public static byte[] makeExtractor(int cid, String cname, Point pos, int timeLeft, int itemId, int fee) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPAWN_EXTRACTOR.getValue());
        mplew.writeInt(cid);
        mplew.writeMapleAsciiString(cname);
        mplew.writeInt(pos.x);
        mplew.writeInt(pos.y);
        mplew.writeShort(timeLeft);
        mplew.writeInt(itemId);
        mplew.writeInt(fee);
        return mplew.getPacket();
    }

    public static byte[] removeExtractor(int cid) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.REMOVE_EXTRACTOR.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(1);
        return mplew.getPacket();
    }

    public static byte[] spouseMessage(String msg, boolean white) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPOUSE_MESSAGE.getValue());

        mplew.writeShort(white ? 10 : 6);
        mplew.writeMapleAsciiString(msg);
        return mplew.getPacket();
    }

    public static byte[] spouseMessage(int op, String msg) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPOUSE_MESSAGE.getValue());

        mplew.writeShort(op);
        mplew.writeMapleAsciiString(msg);
        return mplew.getPacket();
    }

    public static byte[] openBag(int index, int itemId, boolean firstTime) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.OPEN_BAG.getValue());
        mplew.writeInt(index);
        mplew.writeInt(itemId);
        mplew.writeShort(firstTime ? 1 : 0);
        return mplew.getPacket();
    }

    public static byte[] showOwnCraftingEffect(String effect, int time, int mode) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
        mplew.write(36);
        mplew.writeMapleAsciiString(effect);
        mplew.write(1);
        mplew.writeInt(time);
        mplew.writeInt(mode);

        return mplew.getPacket();
    }

    public static byte[] showCraftingEffect(int cid, String effect, int time, int mode) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
        mplew.writeInt(cid);
        mplew.write(36);
        mplew.writeMapleAsciiString(effect);
        mplew.write(1);
        mplew.writeInt(time);
        mplew.writeInt(mode);

        return mplew.getPacket();
    }

    public static byte[] craftMake(int cid, int something, int time) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CRAFT_EFFECT.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(something);
        mplew.writeInt(time);
        return mplew.getPacket();
    }

    public static byte[] craftFinished(int cid, int craftID, int ranking, int itemId, int quantity, int exp) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CRAFT_COMPLETE.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(craftID);
        mplew.writeInt(ranking);
        if (ranking != 23) {
            mplew.writeInt(itemId);
            mplew.writeInt(quantity);
        }
        mplew.writeInt(exp);
        return mplew.getPacket();
    }

    public static byte[] craftMessage(String msg) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CRAFT_MESSAGE.getValue());
        mplew.writeMapleAsciiString(msg);
        mplew.write(1);
        return mplew.getPacket();
    }

    public static byte[] shopDiscount(int percent) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOP_DISCOUNT.getValue());
        mplew.write(percent);
        return mplew.getPacket();
    }

    public static byte[] changeCardSet(int set) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CARD_SET.getValue());
        mplew.writeInt(set);
        return mplew.getPacket();
    }

    public static byte[] getCard(int itemid, int level) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GET_CARD.getValue());
        mplew.write(itemid > 0 ? 1 : 0);
        if (itemid > 0) {
            mplew.writeInt(itemid);
            mplew.writeInt(level);
        }
        return mplew.getPacket();
    }

    public static byte[] upgradeBook(Item book, MapleCharacter chr) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.BOOK_STATS.getValue());
        mplew.writeInt(book.getPosition());
        PacketHelper.addItemInfo(mplew, book, true, true, false, false, chr);
        return mplew.getPacket();
    }

    public static byte[] pendantSlot(boolean p) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PENDANT_SLOT.getValue());
        mplew.write(p ? 1 : 0);
        return mplew.getPacket();
    }

    public static byte[] getMonsterBookInfo(MapleCharacter chr) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.BOOK_INFO.getValue());
        mplew.writeInt(chr.getId());
        mplew.writeInt(chr.getLevel());
        chr.getMonsterBook().writeCharInfoPacket(mplew);
        return mplew.getPacket();
    }

    public static byte[] getBuffBar(long millis) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.BUFF_BAR.getValue());
        mplew.writeLong(millis);
        return mplew.getPacket();
    }

    public static byte[] setNPCScriptable(List<Pair<Integer, String>> npcs) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.NPC_SCRIPTABLE.getValue());
        mplew.write(npcs.size());
        for (Pair s : npcs) {
            mplew.writeInt(((Integer) s.left).intValue());
            mplew.writeMapleAsciiString((String) s.right);
            mplew.writeInt(0);
            mplew.writeInt(2147483647);
        }
        return mplew.getPacket();
    }

    public static byte[] showMidMsg(String s, int l) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MID_MSG.getValue());
        mplew.write(l);
        mplew.writeMapleAsciiString(s);
        mplew.write(s.length() > 0 ? 0 : 1);
        return mplew.getPacket();
    }

    public static byte[] showMemberSearch(List<MapleCharacter> chr) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MEMBER_SEARCH.getValue());
        mplew.write(chr.size());
        for (MapleCharacter c : chr) {
            mplew.writeInt(c.getId());
            mplew.writeMapleAsciiString(c.getName());
            mplew.writeShort(c.getJob());
            mplew.write(c.getLevel());
        }
        return mplew.getPacket();
    }

    public static byte[] showPartySearch(List<MapleParty> chr) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PARTY_SEARCH.getValue());
        mplew.write(chr.size());
        for (MapleParty c : chr) {
            mplew.writeInt(c.getId());
            mplew.writeMapleAsciiString(c.getLeader().getName());
            mplew.write(c.getLeader().getLevel());
            mplew.write(c.getLeader().isOnline() ? 1 : 0);
            mplew.write(c.getMembers().size());
            for (MaplePartyCharacter ch : c.getMembers()) {
                mplew.writeInt(ch.getId());
                mplew.writeMapleAsciiString(ch.getName());
                mplew.writeShort(ch.getJobId());
                mplew.write(ch.getLevel());
                mplew.write(ch.isOnline() ? 1 : 0);
            }
        }
        return mplew.getPacket();
    }

    public static byte[] showBackgroundEffect(String eff, int value) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.VISITOR.getValue());
        mplew.writeMapleAsciiString(eff);
        mplew.write(value);
        return mplew.getPacket();
    }

    public static byte[] updateGender(MapleCharacter chr) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.UPDATE_GENDER.getValue());
        mplew.write(chr.getGender());
        return mplew.getPacket();
    }

    public static byte[] registerFamiliar(MonsterFamiliar mf) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.REGISTER_FAMILIAR.getValue());
        mplew.writeLong(mf.getId());
        mf.writeRegisterPacket(mplew, false);
        mplew.writeShort(mf.getVitality() >= 3 ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] touchFamiliar(int cid, byte unk, int objectid, int type, int delay, int damage) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.TOUCH_FAMILIAR.getValue());
        mplew.writeInt(cid);
        mplew.write(0);
        mplew.write(unk);
        mplew.writeInt(objectid);
        mplew.writeInt(type);
        mplew.writeInt(delay);
        mplew.writeInt(damage);
        return mplew.getPacket();
    }

    public static byte[] familiarAttack(int cid, byte unk, List<Triple<Integer, Integer, List<Integer>>> attackPair) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.ATTACK_FAMILIAR.getValue());
        mplew.writeInt(cid);
        mplew.write(0);
        mplew.write(unk);
        mplew.write(attackPair.size());
        for (Triple s : attackPair) {
            mplew.writeInt(((Integer) s.left).intValue());
            mplew.write(((Integer) s.mid).intValue());
            mplew.write(((List) s.right).size());
            for (Iterator i = ((List) s.right).iterator(); i.hasNext();) {
                int damage = ((Integer) i.next()).intValue();
                mplew.writeInt(damage);
            }
        }
        return mplew.getPacket();
    }

    public static byte[] updateFamiliar(MonsterFamiliar mf) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.UPDATE_FAMILIAR.getValue());
        mplew.writeInt(mf.getCharacterId());
        mplew.writeInt(mf.getFamiliar());
        mplew.writeInt(mf.getFatigue());
        mplew.writeLong(PacketHelper.getTime(mf.getVitality() >= 3 ? System.currentTimeMillis() : -2L));
        return mplew.getPacket();
    }

    public static byte[] removeFamiliar(int cid) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SPAWN_FAMILIAR.getValue());
        mplew.writeInt(cid);
        mplew.writeShort(0);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static byte[] spawnFamiliar(MonsterFamiliar mf, boolean spawn) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SPAWN_FAMILIAR.getValue());
        mplew.writeInt(mf.getCharacterId());
        mplew.writeShort(spawn ? 1 : 0);
        mplew.write(0);
        if (spawn) {
            mplew.writeInt(mf.getFamiliar());
            mplew.writeInt(mf.getFatigue());
            mplew.writeInt(mf.getVitality() * 300);
            mplew.writeMapleAsciiString(mf.getName());
            mplew.writePos(mf.getTruePosition());
            mplew.write(mf.getStance());
            mplew.writeShort(mf.getFh());
        }
        return mplew.getPacket();
    }

    public static byte[] moveFamiliar(int cid, Point startPos, List<LifeMovementFragment> moves) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MOVE_FAMILIAR.getValue());
        mplew.writeInt(cid);
        mplew.write(0);
        mplew.writePos(startPos);
        mplew.writeInt(0);

        PacketHelper.serializeMovementList(mplew, moves);

        return mplew.getPacket();
    }

    public static byte[] achievementRatio(int amount) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ACHIEVEMENT_RATIO.getValue());
        mplew.writeInt(amount);

        return mplew.getPacket();
    }

    public static byte[] createUltimate(int amount) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CREATE_ULTIMATE.getValue());
        mplew.writeInt(amount);

        return mplew.getPacket();
    }

    public static byte[] professionInfo(String skil, int level1, int level2, int chance) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PROFESSION_INFO.getValue());
        mplew.writeMapleAsciiString(skil);
        mplew.writeInt(level1);
        mplew.writeInt(level2);
        mplew.write(1);
        mplew.writeInt((skil.startsWith("9200")) || (skil.startsWith("9201")) ? 100 : chance);

        return mplew.getPacket();
    }

    public static byte[] quickSlot(String skil) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.QUICK_SLOT.getValue());
        mplew.write(skil == null ? 0 : 1);
        if (skil != null) {
            for (int i = 0; i < skil.length(); i++) {
                mplew.writeAsciiString(skil.substring(i, i + 1));
                mplew.writeZeroBytes(3);
            }
        }

        return mplew.getPacket();
    }

    public static byte[] getFamiliarInfo(MapleCharacter chr) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.FAMILIAR_INFO.getValue());
        mplew.writeInt(chr.getFamiliars().size());
        for (MonsterFamiliar mf : chr.getFamiliars().values()) {
            mf.writeRegisterPacket(mplew, true);
        }
        List<Pair<Integer, Long>> size = new ArrayList();
        for (Item i : chr.getInventory(MapleInventoryType.USE).list()) {
            if (i.getItemId() / 10000 == 287) {
                StructFamiliar f = MapleItemInformationProvider.getInstance().getFamiliarByItem(i.getItemId());
                if (f != null) {
                    size.add(new Pair(Integer.valueOf(f.familiar), Long.valueOf(i.getInventoryId())));
                }
            }
        }
        mplew.writeInt(size.size());
        for (Pair<Integer, Long> s : size) {
            mplew.writeInt(chr.getId());
            mplew.writeInt((s.left).intValue());
            mplew.writeLong((s.right).longValue());
            mplew.write(0);
        }
        size.clear();
        return mplew.getPacket();
    }

    public static byte[] updateImp(MapleImp imp, int mask, int index, boolean login) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ITEM_POT.getValue());
        mplew.write(login ? 0 : 1);
        mplew.writeInt(index + 1);
        mplew.writeInt(mask);
        if ((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) {
            Pair i = MapleItemInformationProvider.getInstance().getPot(imp.getItemId());
            if (i == null) {
                return enableActions();
            }
            mplew.writeInt(((Integer) i.left).intValue());
            mplew.write(imp.getLevel());
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.STATE.getValue()) != 0)) {
            mplew.write(imp.getState());
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.FULLNESS.getValue()) != 0)) {
            mplew.writeInt(imp.getFullness());
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.CLOSENESS.getValue()) != 0)) {
            mplew.writeInt(imp.getCloseness());
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.CLOSENESS_LEFT.getValue()) != 0)) {
            mplew.writeInt(1);
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.MINUTES_LEFT.getValue()) != 0)) {
            mplew.writeInt(0);
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.LEVEL.getValue()) != 0)) {
            mplew.write(1);
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.FULLNESS_2.getValue()) != 0)) {
            mplew.writeInt(imp.getFullness());
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.UPDATE_TIME.getValue()) != 0)) {
            mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.CREATE_TIME.getValue()) != 0)) {
            mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.AWAKE_TIME.getValue()) != 0)) {
            mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.SLEEP_TIME.getValue()) != 0)) {
            mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.MAX_CLOSENESS.getValue()) != 0)) {
            mplew.writeInt(100);
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.MAX_DELAY.getValue()) != 0)) {
            mplew.writeInt(1000);
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.MAX_FULLNESS.getValue()) != 0)) {
            mplew.writeInt(1000);
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.MAX_ALIVE.getValue()) != 0)) {
            mplew.writeInt(1);
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.MAX_MINUTES.getValue()) != 0)) {
            mplew.writeInt(10);
        }
        mplew.write(0);
        return mplew.getPacket();
    }

    public static byte[] spawnFlags(List<Pair<String, Integer>> flags) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.LOGIN_WELCOME.getValue());
        mplew.write(flags == null ? 0 : flags.size());
        if (flags != null) {
            for (Pair f : flags) {
                mplew.writeMapleAsciiString((String) f.left);
                mplew.write(((Integer) f.right).intValue());
            }
        }

        return mplew.getPacket();
    }

    public static byte[] getPVPScoreboard(List<Pair<Integer, MapleCharacter>> flags, int type) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_SCOREBOARD.getValue());
        mplew.writeShort(flags.size());
        for (Pair f : flags) {
            mplew.writeInt(((MapleCharacter) f.right).getId());
            mplew.writeMapleAsciiString(((MapleCharacter) f.right).getName());
            mplew.writeInt(((Integer) f.left).intValue());
            mplew.write(type == 0 ? 0 : ((MapleCharacter) f.right).getTeam() + 1);
        }

        return mplew.getPacket();
    }

    public static byte[] getPVPResult(List<Pair<Integer, MapleCharacter>> flags, int exp, int winningTeam, int playerTeam) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_RESULT.getValue());
        mplew.writeInt(flags.size());
        for (Pair f : flags) {
            mplew.writeInt(((MapleCharacter) f.right).getId());
            mplew.writeMapleAsciiString(((MapleCharacter) f.right).getName());
            mplew.writeInt(((Integer) f.left).intValue());
            mplew.writeShort(((MapleCharacter) f.right).getTeam() + 1);
            if (GameConstants.GMS) {
                mplew.writeInt(0);
            }
        }
        mplew.writeZeroBytes(24);
        mplew.writeInt(exp);
        mplew.write(0);
        if (GameConstants.GMS) {
            mplew.writeShort(100);
            mplew.writeInt(0);
        }
        mplew.write(winningTeam);
        mplew.write(playerTeam);

        return mplew.getPacket();
    }

    public static byte[] showStatusMessage(String info, String data) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(22);
        mplew.writeMapleAsciiString(info);
        mplew.writeMapleAsciiString(data);

        return mplew.getPacket();
    }

    public static byte[] showOwnChampionEffect() {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
        mplew.write(32);
        mplew.writeInt(30000);

        return mplew.getPacket();
    }

    public static byte[] showChampionEffect(int from_playerid) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
        mplew.writeInt(from_playerid);
        mplew.write(32);
        mplew.writeInt(30000);

        return mplew.getPacket();
    }

    public static byte[] enablePVP(boolean enabled) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_ENABLED.getValue());
        mplew.write(enabled ? 1 : 2);

        return mplew.getPacket();
    }

    public static byte[] getPVPMode(int mode) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_MODE.getValue());
        mplew.write(mode);

        return mplew.getPacket();
    }

    public static byte[] getPVPType(int type, List<Pair<Integer, String>> players1, int team, boolean enabled, int lvl) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_TYPE.getValue());
        mplew.write(type);
        mplew.write(lvl);
        mplew.write(enabled ? 1 : 0);
        if (type > 0) {
            mplew.write(team);
            mplew.writeInt(players1.size());
            for (Pair pl : players1) {
                mplew.writeInt(((Integer) pl.left).intValue());
                mplew.writeMapleAsciiString((String) pl.right);
                mplew.writeShort(2660);
            }
        }

        return mplew.getPacket();
    }

    public static byte[] getPVPTeam(List<Pair<Integer, String>> players) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_TEAM.getValue());
        mplew.writeInt(players.size());
        for (Pair pl : players) {
            mplew.writeInt(((Integer) pl.left).intValue());
            mplew.writeMapleAsciiString((String) pl.right);
            mplew.writeShort(2660);
        }

        return mplew.getPacket();
    }

    public static byte[] getPVPScore(int score, boolean kill) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_SCORE.getValue());
        mplew.writeInt(score);
        mplew.write(kill ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] getPVPIceGage(int score) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_ICEGAGE.getValue());
        mplew.writeInt(score);

        return mplew.getPacket();
    }

    public static byte[] getPVPKilled(String lastWords) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_KILLED.getValue());
        mplew.writeMapleAsciiString(lastWords);

        return mplew.getPacket();
    }

    public static byte[] getPVPPoints(int p1, int p2) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_POINTS.getValue());

        mplew.writeInt(p1);
        mplew.writeInt(p2);

        return mplew.getPacket();
    }

    public static byte[] getPVPHPBar(int cid, int hp, int maxHp) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_HP.getValue());

        mplew.writeInt(cid);
        mplew.writeInt(hp);
        mplew.writeInt(maxHp);

        return mplew.getPacket();
    }

    public static byte[] getPVPIceHPBar(int hp, int maxHp) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_ICEKNIGHT.getValue());

        mplew.writeInt(hp);
        mplew.writeInt(maxHp);

        return mplew.getPacket();
    }

    public static byte[] getPVPMist(int cid, int mistSkill, int mistLevel, int damage) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_MIST.getValue());

        mplew.writeInt(cid);
        mplew.writeInt(mistSkill);
        mplew.write(mistLevel);
        mplew.writeInt(damage);
        mplew.write(8);
        mplew.writeInt(1000);

        return mplew.getPacket();
    }

    public static byte[] getCaptureFlags(MapleMap map) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CAPTURE_FLAGS.getValue());
        mplew.writeRect(map.getArea(0));
        mplew.writeInt(((Point) ((Pair) map.getGuardians().get(0)).left).x);
        mplew.writeInt(((Point) ((Pair) map.getGuardians().get(0)).left).y);
        mplew.writeRect(map.getArea(1));
        mplew.writeInt(((Point) ((Pair) map.getGuardians().get(1)).left).x);
        mplew.writeInt(((Point) ((Pair) map.getGuardians().get(1)).left).y);
        return mplew.getPacket();
    }

    public static byte[] getCapturePosition(MapleMap map) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        Point p1 = map.getPointOfItem(2910000);
        Point p2 = map.getPointOfItem(2910001);
        mplew.writeShort(SendPacketOpcode.CAPTURE_POSITION.getValue());
        mplew.write(p1 == null ? 0 : 1);
        if (p1 != null) {
            mplew.writeInt(p1.x);
            mplew.writeInt(p1.y);
        }
        mplew.write(p2 == null ? 0 : 1);
        if (p2 != null) {
            mplew.writeInt(p2.x);
            mplew.writeInt(p2.y);
        }

        return mplew.getPacket();
    }

    public static byte[] resetCapture() {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CAPTURE_RESET.getValue());

        return mplew.getPacket();
    }

    public static byte[] pvpAttack(int cid, int playerLevel, int skill, int skillLevel, int speed, int mastery, int projectile, int attackCount, int chargeTime, int stance, int direction, int range, int linkSkill, int linkSkillLevel, boolean movementSkill, boolean pushTarget, boolean pullTarget, List<AttackPair> attack) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_ATTACK.getValue());
        mplew.writeInt(cid);
        mplew.write(playerLevel);
        mplew.writeInt(skill);
        mplew.write(skillLevel);
        mplew.writeInt(linkSkill != skill ? linkSkill : 0);
        mplew.write(linkSkillLevel != skillLevel ? linkSkillLevel : 0);
        mplew.write(direction);
        mplew.write(movementSkill ? 1 : 0);
        mplew.write(pushTarget ? 1 : 0);
        mplew.write(pullTarget ? 1 : 0);
        mplew.write(0);
        mplew.writeShort(stance);
        mplew.write(speed);
        mplew.write(mastery);
        mplew.writeInt(projectile);
        mplew.writeInt(chargeTime);
        mplew.writeInt(range);
        mplew.writeShort(attack.size());
        if (GameConstants.GMS) {
            mplew.writeInt(0);
        }
        mplew.write(attackCount);
        mplew.write(0);
        for (AttackPair p : attack) {
            mplew.writeInt(p.objectid);
            if (GameConstants.GMS) {
                mplew.writeInt(0);
            }
            mplew.writePos(p.point);
            mplew.writeZeroBytes(5);
            for (Pair atk : p.attack) {
                mplew.writeInt(((Integer) atk.left).intValue());
                if (GameConstants.GMS) {
                    mplew.writeInt(0);
                }
                mplew.write(((Boolean) atk.right).booleanValue() ? 1 : 0);
                mplew.writeShort(0);
            }
        }

        return mplew.getPacket();
    }

    public static byte[] pvpSummonAttack(int cid, int playerLevel, int oid, int animation, Point pos, List<AttackPair> attack) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_SUMMON.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(oid);
        mplew.write(playerLevel);
        mplew.write(animation);
        mplew.writePos(pos);
        mplew.writeInt(0);
        mplew.write(attack.size());
        for (AttackPair p : attack) {
            mplew.writeInt(p.objectid);
            mplew.writePos(p.point);
            mplew.writeShort(p.attack.size());
            for (Pair atk : p.attack) {
                mplew.writeInt(((Integer) atk.left).intValue());
            }
        }

        return mplew.getPacket();
    }

    public static byte[] pvpCool(int cid, List<Integer> attack) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_COOL.getValue());
        mplew.writeInt(cid);
        mplew.write(attack.size());
        for (Iterator i$ = attack.iterator(); i$.hasNext();) {
            int b = ((Integer) i$.next()).intValue();
            mplew.writeInt(b);
        }
        return mplew.getPacket();
    }

    public static byte[] getPVPClock(int type, int time) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CLOCK.getValue());
        mplew.write(3);
        mplew.write(type);
        mplew.writeInt(time);

        return mplew.getPacket();
    }

    public static byte[] getPVPTransform(int type) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_TRANSFORM.getValue());
        mplew.write(type);

        return mplew.getPacket();
    }

    public static byte[] changeTeam(int cid, int type) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.LOAD_TEAM.getValue());
        mplew.writeInt(cid);
        mplew.write(type);

        return mplew.getPacket();
    }

    public static byte[] getQuickMove(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.QUICK_MOVE.getValue());
        if (!MapleQuickMove.isQuickMoveMap(chr.getMapId())) {
            mplew.write(0);
            return mplew.getPacket();
        }
        MapleQuickMove[] maps = MapleQuickMove.values();
        mplew.write(maps.length);
        for (MapleQuickMove map : maps) {
            mplew.writeMapleAsciiString(map.name);
            mplew.writeInt(map.npcid);
            mplew.writeInt(map.type);
            mplew.writeInt(map.level);
            mplew.writeMapleAsciiString(map.desc);
        }

        return mplew.getPacket();
    }

    public static byte[] showQuickMove(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.QUICK_MOVE.getValue());
        List<MapleQuickMove> QuickMove = MapleQuickMove.showQuickMove(chr.getMapId());
        mplew.write(QuickMove.size());
        for (MapleQuickMove map : QuickMove) {
            mplew.writeMapleAsciiString(map.name);
            mplew.writeInt(map.npcid);
            mplew.writeInt(map.type);
            mplew.writeInt(map.level);
            mplew.writeMapleAsciiString(map.desc);
        }
        return mplew.getPacket();
    }

    public static byte[] showForce(int oid, int forceCount, int forceColor) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GAIN_FORCE.getValue());
        mplew.write(1);
        mplew.writeInt(oid);
        mplew.writeInt(0);
        mplew.write(1);
        mplew.writeInt(forceCount);

        mplew.writeInt(forceColor);
        mplew.writeInt(37);
        mplew.writeInt(6);
        mplew.writeInt(46);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] showCarte(int carte) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_CARTE.getValue());
        mplew.write(carte);

        return mplew.getPacket();
    }

    public static byte[] showCarte(MapleCharacter chr, int oid, int skillId, int forceCount) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        //1.恶魔精气吸收效果 2.幻影卡片封包效果
        mplew.writeShort(SendPacketOpcode.GAIN_FORCE.getValue());
        mplew.write(0);
        mplew.writeInt(chr.getId());
        mplew.writeInt(1);
        mplew.writeInt(oid);
        mplew.writeInt(skillId);
        mplew.write(1);
        mplew.writeInt(forceCount);
        mplew.writeInt(1);
        mplew.writeInt(28);
        mplew.writeInt(8);
        mplew.writeInt(6);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] showQuestMessage(String msg) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(11);
        mplew.writeMapleAsciiString(msg);

        return mplew.getPacket();
    }

    public static byte[] sendloginSuccess() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.LOGIN_SUCC.getValue());

        return mplew.getPacket();
    }

    public static byte[] showCharCash(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CHAR_CASH.getValue());
        mplew.writeInt(chr.getId());
        mplew.writeInt(chr.getCSPoints(2));

        return mplew.getPacket();
    }

    public static byte[] UNK() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(182);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static byte[] UNK1() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(211);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static byte[] GainEXP_Monster(String testmsg) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(3);
        mplew.write(1);
        mplew.writeInt(1000);
        mplew.write(0);

        mplew.writeInt(0);
        mplew.writeShort(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.write(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] teachMessage(int skillId, int toChrId, String toChrName) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.TEACH_MESSAGE.getValue());
        mplew.writeInt(0);
        mplew.writeInt(skillId);
        mplew.writeInt(toChrId);
        mplew.writeMapleAsciiString(toChrName);

        return mplew.getPacket();
    }
//道场排名
    public static byte[] showDoJangRank() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.DOJANG_RANK.getValue());
        mplew.writeInt(1);
        mplew.writeShort(1);
        mplew.writeMapleAsciiString("MapleWing");
        mplew.writeLong(60L);

        return mplew.getPacket();
    }

    public static byte[] openWeb(String web) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.OPEN_WEB.getValue());
        mplew.writeMapleAsciiString(web);

        return mplew.getPacket();
    }

    public static byte[] sendPolice(String text) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MAPLE_ADMIN.getValue());
        mplew.writeMapleAsciiString(text);

        return mplew.getPacket();
    }

    public static byte[] 显示免费时空(int mf, int cs) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.DAY_OF_SHIKONG.getValue());
        mplew.writeInt(30 - mf);
        mplew.writeInt(cs);
        return mplew.getPacket();
    }

    public static byte[] 时空移动错误() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.DAY_OF_SHIKONG1.getValue());
        mplew.write(0);
        return mplew.getPacket();
    }

    public static byte[] showOwnJobChangedElf(String effect, int time, int itemId) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
        mplew.write(36);
        mplew.writeMapleAsciiString(effect);
        mplew.write(1);
        mplew.writeInt(0);
        mplew.writeInt(time);
        mplew.writeInt(itemId);

        return mplew.getPacket();
    }

    public static byte[] showJobChangedElf(int cid, String effect, int time, int mode) {
        if (ServerProperties.ShowPacket()) {
            log.info(new StringBuilder().append("调用: ").append(new java.lang.Throwable().getStackTrace()[0]).toString());
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
        mplew.writeInt(cid);
        mplew.write(36);
        mplew.writeMapleAsciiString(effect);
        mplew.write(1);
        mplew.writeInt(0);
        mplew.writeInt(time);
        mplew.writeInt(mode);

        return mplew.getPacket();
    }

    public static byte[] testPacket(String testmsg) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(HexTool.getByteArrayFromHexString(testmsg));
        return mplew.getPacket();
    }

    public static byte[] testPacket(byte[] testmsg) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(testmsg);
        return mplew.getPacket();
    }

    public static byte[] testPacket(String op, String text) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(HexTool.getByteArrayFromHexString(op));
        mplew.writeMapleAsciiString(text);
        return mplew.getPacket();
    }

    static {
        DEFAULT_BUFFMASK |= MapleBuffStat.能量获得.getValue();
        DEFAULT_BUFFMASK |= MapleBuffStat.疾驰速度.getValue();
        DEFAULT_BUFFMASK |= MapleBuffStat.疾驰跳跃.getValue();
        DEFAULT_BUFFMASK |= MapleBuffStat.骑兽技能.getValue();
        DEFAULT_BUFFMASK |= MapleBuffStat.极速领域.getValue();
        DEFAULT_BUFFMASK |= MapleBuffStat.导航辅助.getValue();
        DEFAULT_BUFFMASK |= MapleBuffStat.DEFAULT_BUFFSTAT.getValue();
    }
    /**
     * 幻影偷技能系列
     */
    public static byte[] sendphantomunequip(int skillid) {//印技树 删除技能
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PHANTOM_EQUIP_SEND.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("01 00"));
        mplew.writeInt(skillid);
        return mplew.getPacket();
    }
    
    public static byte[] sendphantomequip(int chrid,int skillid) {//印技树 装备技能
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PHANTOM_EQUIP_SEND.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("01 01"));//??
        mplew.writeInt(chrid);
        mplew.writeInt(skillid);
        return mplew.getPacket();
    }
    
        public static byte[] sendphantomview(int skillid,int skilllvl,int type,int xtype) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PHANTOM_VIEW_SEND.getValue());
        mplew.writeShort(1); //?
        mplew.writeInt(type);
        mplew.writeInt(xtype);
        mplew.writeInt(skillid);//??
        mplew.writeInt(skilllvl);
        mplew.writeInt(0);
        return mplew.getPacket();
    }
        
    public static byte[] sendphantomdrop(int chrid,int type,int xtype) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PHANTOM_VIEW_SEND.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("01 03"));//??
        mplew.writeInt(type);
        mplew.writeInt(xtype);
        return mplew.getPacket();
    }
    
    public static byte[] sendphantomskill(int chrid,int jobid,List<Integer> skills) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PHANTOM_SKILL_SEND.getValue());
        mplew.write(1); //?
        mplew.writeInt(chrid);
        mplew.writeInt(4);//??
        mplew.writeInt(jobid);
        mplew.writeInt(skills.size());
        for (int b : skills) {
            mplew.writeInt(b);
        }
        return mplew.getPacket();
    }
/*
public static byte[] ZreHylvl(int npcid, ResultSet rs) throws SQLException  { 

  MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();    
  mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue()); 
  mplew.write(0x49); 
  mplew.writeInt(npcid); 
  if (!rs.last()) 
  { 
   mplew.writeInt(0); 
   return mplew.getPacket(); 
  } 
   
  mplew.writeInt(rs.getRow()); 
   
  rs.beforeFirst(); 
  while (rs.next()) 
  { 
   mplew.writeMapleAsciiString(rs.getString("name")); 
   mplew.writeInt(rs.getInt("level")); 
   mplew.writeInt(rs.getInt("str")); 
   mplew.writeInt(rs.getInt("dex")); 
   mplew.writeInt(rs.getInt("int")); 
   mplew.writeInt(rs.getInt("luk")); 
  } 
   
  return mplew.getPacket(); 
 } 
public static byte[] ZreHyfame(int npcid, ResultSet rs) throws SQLException 
 { 
  MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(); 
   
  mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue()); 
  mplew.write(0x49); 
  mplew.writeInt(npcid); 
  if (!rs.last()) 
  { 
   mplew.writeInt(0); 
   return mplew.getPacket(); 
  } 
   
  mplew.writeInt(rs.getRow()); 
   
  rs.beforeFirst(); 
  while (rs.next()) 
  { 
   mplew.writeMapleAsciiString(rs.getString("name")); 
   mplew.writeInt(rs.getInt("fame")); 
   mplew.writeInt(rs.getInt("str")); 
   mplew.writeInt(rs.getInt("dex")); 
   mplew.writeInt(rs.getInt("int")); 
   mplew.writeInt(rs.getInt("luk")); 
  } 
   
  return mplew.getPacket(); 
 } 

public static byte[] ZreHymeso(int npcid, ResultSet rs) throws SQLException 
 { 
  MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(); 
   
  mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue()); 
  mplew.write(0x49); 
  mplew.writeInt(npcid); 
  if (!rs.last())  
  { 
   mplew.writeInt(0); 
   return mplew.getPacket(); 
  } 
   
  mplew.writeInt(rs.getRow()); 
  rs.beforeFirst(); 
  while (rs.next()) 
  { 
   mplew.writeMapleAsciiString(rs.getString("name")); 
   mplew.writeInt(rs.getInt("meso")); 
   mplew.writeInt(rs.getInt("str")); 
   mplew.writeInt(rs.getInt("dex")); 
   mplew.writeInt(rs.getInt("int")); 
   mplew.writeInt(rs.getInt("luk")); 
  } 
   
  return mplew.getPacket(); 
 }

public static byte[] ZreHyzs(int npcid, ResultSet rs) throws SQLException 
 { 
  MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(); 
   
  mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue()); 
  mplew.write(0x49); 
  mplew.writeInt(npcid); 
  if (!rs.last()) 
  { 
   mplew.writeInt(0); 
   return mplew.getPacket(); 
  } 
   
  mplew.writeInt(rs.getRow()); 
   
  rs.beforeFirst(); 
  while (rs.next()) 
  { 
   mplew.writeMapleAsciiString(rs.getString("name")); 
   mplew.writeInt(rs.getInt("reborns")); 
   mplew.writeInt(rs.getInt("str")); 
   mplew.writeInt(rs.getInt("dex")); 
   mplew.writeInt(rs.getInt("int")); 
   mplew.writeInt(rs.getInt("luk")); 
  } 
   
  return mplew.getPacket(); 
 } 
public static byte[] ZreHypvpkills(int npcid, ResultSet rs) throws SQLException 
 { 
  MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(); 
   
  mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue()); 
  mplew.write(0x49); 
  mplew.writeInt(npcid); 
  if (!rs.last()) 
  { 
   mplew.writeInt(0); 
   return mplew.getPacket(); 
  } 
   
  mplew.writeInt(rs.getRow()); 
   
  rs.beforeFirst(); 
  while (rs.next()) 
  { 
   mplew.writeMapleAsciiString(rs.getString("name")); 
   mplew.writeInt(rs.getInt("pvpkills")); 
   mplew.writeInt(rs.getInt("str")); 
   mplew.writeInt(rs.getInt("dex")); 
   mplew.writeInt(rs.getInt("int")); 
   mplew.writeInt(rs.getInt("luk")); 
  } 
   
  return mplew.getPacket(); 
 } 

public static byte[] ZreHypvpdeaths(int npcid, ResultSet rs) throws SQLException 
 { 
  MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(); 
   
  mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue()); 
  mplew.write(0x49); 
  mplew.writeInt(npcid); 
  if (!rs.last())  
  { 
   mplew.writeInt(0); 
   return mplew.getPacket(); 
  } 
   
  mplew.writeInt(rs.getRow()); 
  rs.beforeFirst(); 
  while (rs.next()) 
  { 
   mplew.writeMapleAsciiString(rs.getString("name")); 
   mplew.writeInt(rs.getInt("pvpdeaths")); 
   mplew.writeInt(rs.getInt("str")); 
   mplew.writeInt(rs.getInt("dex")); 
   mplew.writeInt(rs.getInt("int")); 
   mplew.writeInt(rs.getInt("luk")); 
  } 
   
  return mplew.getPacket(); 
 }
 * 
 */

}