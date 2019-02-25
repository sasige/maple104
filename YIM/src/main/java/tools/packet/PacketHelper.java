package tools.packet;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleCoolDownValueHolder;
import client.MapleQuestStatus;
import client.MapleTrait.MapleTraitType;
import client.Skill;
import client.SkillEntry;
import client.SkillFactory;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.inventory.MapleRing;
import constants.GameConstants;
import database.DatabaseConnection;
import handling.Buffstat;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SimpleTimeZone;
import server.MapleCarnivalChallenge;
import server.MapleItemInformationProvider;
import server.MapleShop;
import server.MapleShopItem;
import server.ServerProperties;
import server.movement.LifeMovementFragment;
import server.quest.MapleQuest;
import server.shops.AbstractPlayerStore;
import server.shops.IMaplePlayerShop;
import tools.BitTools;
import tools.DateUtil;
import tools.FileoutputUtil;
import tools.HexTool;
import tools.Pair;
import tools.StringUtil;
import tools.Triple;
import tools.data.MaplePacketLittleEndianWriter;

public class PacketHelper {

    public static long FT_UT_OFFSET = 116444592000000000L;
    public static long MAX_TIME = 150842304000000000L;
    public static long ZERO_TIME = 94354848000000000L;//00 40 E0 FD 3B 37 4F 01
    public static long PERMANENT = 150841440000000000L;

    public static long getKoreanTimestamp(long realTimestamp) {
        return getTime(realTimestamp);
    }

    public static long getTime(long realTimestamp) {
        if (realTimestamp == -1) {
            return MAX_TIME;
        }
        if (realTimestamp == -2) {
            return ZERO_TIME;
        }
        if (realTimestamp == -3) {
            return PERMANENT;
        }
        return realTimestamp * 10000 + FT_UT_OFFSET;
    }

    public static long getFileTimestamp(long timeStampinMillis, boolean roundToMinutes) {
        if (SimpleTimeZone.getDefault().inDaylightTime(new Date())) {
            timeStampinMillis -= 3600000;
        }
        long time;
        if (roundToMinutes) {
            time = timeStampinMillis / 1000 / 60 * 600000000L;
        } else {
            time = timeStampinMillis * 10000;
        }
        return time + FT_UT_OFFSET;
    }

    public static void addQuestInfo(final MaplePacketLittleEndianWriter mplew, final MapleCharacter chr) {
        List<MapleQuestStatus> started = chr.getStartedQuests();
        mplew.write(1);
        mplew.writeShort(started.size());
        for (MapleQuestStatus q : started) {
            mplew.writeShort(q.getQuest().getId());
            if (q.hasMobKills()) {
                StringBuilder sb = new StringBuilder();
                for (int kills : q.getMobKills().values()) {
                    sb.append(StringUtil.getLeftPaddedStr(String.valueOf(kills), '0', 3));
                }
                mplew.writeMapleAsciiString(sb.toString());
            } else {
                mplew.writeMapleAsciiString(q.getCustomData() == null ? "" : q.getCustomData());
            }
        }

        mplew.write(1);
        List<MapleQuestStatus> completed = chr.getCompletedQuests();
        mplew.writeShort(completed.size());
        for (MapleQuestStatus q : completed) {
            mplew.writeShort(q.getQuest().getId());
            mplew.writeLong(getTime(q.getCompletionTime()));
        }
    }

    public static void addSkillInfo(final MaplePacketLittleEndianWriter mplew, final MapleCharacter chr) {
        final Map<Skill, SkillEntry> skills = chr.getSkills();
        mplew.write(1);
        mplew.writeShort(skills.size());
        for (Entry<Skill, SkillEntry> skill : skills.entrySet()) {
            mplew.writeInt(skill.getKey().getId());
            if (skill.getKey().getId() == 80000000 || skill.getKey().getId() == 80000001 || skill.getKey().getId() == 80001040) {
                mplew.writeInt(skill.getValue().teachId);
            } else if (skill.getKey().getId() == 110 || skill.getKey().getId() == 20021110 || skill.getKey().getId() == 30010112) {
                mplew.writeInt(skill.getValue().teachId > 0 ? skill.getValue().teachId : skill.getValue().skillevel);
            } else {
                mplew.writeInt(skill.getValue().skillevel);
            }
            addExpirationTime(mplew, skill.getValue().expiration);
            if (skill.getKey().isFourthJob()) {
                mplew.writeInt(skill.getValue().masterlevel);
            }
            if ((skill.getValue().skillevel >= 5) && (chr.isGM()) && (ServerProperties.ShowPacket())) {
                String job = new StringBuilder().append("addSkillInfo\\").append(MapleCarnivalChallenge.getJobNameById(chr.getJob())).append(".txt").toString();
                FileoutputUtil.log(job, new StringBuilder().append("玩家技能: ").append(((Skill) skill.getKey()).getId()).append(" 名字: ").append(SkillFactory.getSkillName(((Skill) skill.getKey()).getId())).append(" 技能等级: ").append(((SkillEntry) skill.getValue()).skillevel).append("/").append(((SkillEntry) skill.getValue()).masterlevel).append(" 是否写最大等级: ").append(((Skill) skill.getKey()).isFourthJob()).toString(), true);
            }
        }
    }

    public static void addCoolDownInfo(final MaplePacketLittleEndianWriter mplew, final MapleCharacter chr) {
        final List<MapleCoolDownValueHolder> cd = chr.getCooldowns();
        mplew.writeShort(cd.size());
        for (final MapleCoolDownValueHolder cooling : cd) {
            mplew.writeInt(cooling.skillId);
            int timeLeft = (int) (cooling.length + cooling.startTime - System.currentTimeMillis());
            mplew.writeInt(timeLeft / 1000);
        }
    }

    public static void addRocksInfo(final MaplePacketLittleEndianWriter mplew, final MapleCharacter chr) {
        int[] mapz = chr.getRegRocks();
        for (int i = 0; i < 5; i++) {
            mplew.writeInt(mapz[i]);
        }
        int[] map = chr.getRocks();
        for (int i = 0; i < 10; i++) {
            mplew.writeInt(map[i]);
        }
        int[] maps = chr.getHyperRocks();
        for (int i = 0; i < 13; i++) {
            mplew.writeInt(maps[i]);
        }
    }

    public static void addRingInfo(final MaplePacketLittleEndianWriter mplew, final MapleCharacter chr) {
        mplew.writeShort(0);

        Triple<List<MapleRing>, List<MapleRing>, List<MapleRing>> aRing = chr.getRings(true);
        List<MapleRing> cRing = aRing.getLeft();
        mplew.writeShort(cRing.size());
        for (MapleRing ring : cRing) {
            mplew.writeInt(ring.getPartnerChrId());
            mplew.writeAsciiString(ring.getPartnerName(), 13);
            mplew.writeLong(ring.getRingId());
            mplew.writeLong(ring.getPartnerRingId());
        }
        List<MapleRing> fRing = aRing.getMid();
        mplew.writeShort(fRing.size());
        for (MapleRing ring : fRing) {
            mplew.writeInt(ring.getPartnerChrId());
            mplew.writeAsciiString(ring.getPartnerName(), 13);
            mplew.writeLong(ring.getRingId());
            mplew.writeLong(ring.getPartnerRingId());
            mplew.writeInt(ring.getItemId());
        }
        List<MapleRing> mRing = aRing.getRight();
        mplew.writeShort(mRing.size());
        int marriageId = 30000;
        for (MapleRing ring : mRing) {
            mplew.writeInt(marriageId);
            mplew.writeInt(chr.getId());
            mplew.writeInt(ring.getPartnerChrId());
            mplew.writeShort(3);
            mplew.writeInt(ring.getItemId());
            mplew.writeInt(ring.getItemId());
            mplew.writeAsciiString(chr.getName(), 13);
            mplew.writeAsciiString(ring.getPartnerName(), 13);
        }
    }

    public static void addInventoryInfo(final MaplePacketLittleEndianWriter mplew, final MapleCharacter chr) {
        mplew.writeInt(chr.getMeso());
        mplew.writeInt(chr.getId());
        mplew.writeInt(chr.getBeans());
        mplew.writeInt(chr.getCSPoints(2));
        mplew.writeInt(0);
        mplew.write(chr.getInventory(MapleInventoryType.EQUIP).getSlotLimit());
        mplew.write(chr.getInventory(MapleInventoryType.USE).getSlotLimit());
        mplew.write(chr.getInventory(MapleInventoryType.SETUP).getSlotLimit());
        mplew.write(chr.getInventory(MapleInventoryType.ETC).getSlotLimit());
        mplew.write(chr.getInventory(MapleInventoryType.CASH).getSlotLimit());

        final MapleQuestStatus stat = chr.getQuestNoAdd(MapleQuest.getInstance(122700));
        if ((stat != null) && (stat.getCustomData() != null) && (Long.parseLong(stat.getCustomData()) > System.currentTimeMillis())) {
            mplew.writeLong(getTime(Long.parseLong(stat.getCustomData())));
        } else {
            mplew.writeLong(getTime(-2));
        }

        MapleInventory iv = chr.getInventory(MapleInventoryType.EQUIPPED);
        List<Item> equipped = iv.newList();
        Collections.sort(equipped);
        for (Item item : equipped) {
            if ((item.getPosition() < 0) && (item.getPosition() > -100)) {
                addItemInfo(mplew, item, false, false, false, false, chr);
            }
        }
        mplew.writeShort(0);
        for (Item item : equipped) {
            if ((item.getPosition() <= -100) && (item.getPosition() > -1000)) {
                addItemInfo(mplew, item, false, false, false, false, chr);
            }
        }
        mplew.writeShort(0);
        iv = chr.getInventory(MapleInventoryType.EQUIP);
        for (Item item : iv.list()) {
            addItemInfo(mplew, item, false, false, false, false, chr);
        }
        mplew.writeShort(0);
        for (Item item : equipped) {
            if ((item.getPosition() <= -1000) && (item.getPosition() > -1100)) {
                addItemInfo(mplew, item, false, false, false, false, chr);
            }
        }
        mplew.writeShort(0);
        for (Item item : equipped) {
            if ((item.getPosition() <= -1100) && (item.getPosition() > -1200)) {
                addItemInfo(mplew, item, false, false, false, false, chr);
            }
        }
        mplew.writeShort(0);
        for (Item item : equipped) {
            if ((item.getPosition() <= -1200) && (item.getPosition() > -1300)) {
                addItemInfo(mplew, item, false, false, false, false, chr);
            }
        }
        mplew.writeShort(0);
        for (Item item : equipped) {
            if ((item.getPosition() <= -5000) && (item.getPosition() > -5003)) {
                addItemInfo(mplew, item, false, false, false, false, chr);
            }
        }
        mplew.writeShort(0);
        iv = chr.getInventory(MapleInventoryType.USE);
        for (Item item : iv.list()) {
            addItemInfo(mplew, item, false, false, false, false, chr);
        }
        mplew.write(0);
        iv = chr.getInventory(MapleInventoryType.SETUP);
        for (Item item : iv.list()) {
            addItemInfo(mplew, item, false, false, false, false, chr);
        }
        mplew.write(0);
        iv = chr.getInventory(MapleInventoryType.ETC);
        for (Item item : iv.list()) {
            if (item.getPosition() < 100) {
                addItemInfo(mplew, item, false, false, false, false, chr);
            }
        }
        mplew.write(0);
        iv = chr.getInventory(MapleInventoryType.CASH);
        for (Item item : iv.list()) {
            addItemInfo(mplew, item, false, false, false, false, chr);
        }
        mplew.write(0);
        for (int i = 0; i < chr.getExtendedSlots().size(); i++) {
            mplew.writeInt(i);
            mplew.writeInt(chr.getExtendedSlot(i));
            for (Item item : chr.getInventory(MapleInventoryType.ETC).list()) {
                // if ((item.getPosition() > i * 100 + 100) && (item.getPosition() < i * 100 + 200)) {
                if (item.getPosition() > (i * 100 + 100) && item.getPosition() < (i * 100 + 200)) {
                    addItemInfo(mplew, item, false, false, false, true, chr);
                }
            }
            mplew.writeInt(-1);
        }
        mplew.writeInt(-1);
        mplew.writeZeroBytes(5);
        mplew.writeInt(0);
    }

    public static void addCharStats(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        mplew.writeInt(chr.getId());
        mplew.writeAsciiString(chr.getName(), 13);
        mplew.write(chr.getGender());
        mplew.write(chr.getSkinColor());
        mplew.writeInt(chr.getFace());
        mplew.writeInt(chr.getHair());

        mplew.write(chr.getLevel());
        mplew.writeShort(chr.getJob());
        chr.getStat().connectData(mplew);
        mplew.writeShort(chr.getRemainingAp());
        if ((GameConstants.is龙神(chr.getJob())) || (GameConstants.is反抗者(chr.getJob())) || (GameConstants.is双弩精灵(chr.getJob())) || (GameConstants.is恶魔猎手(chr.getJob())) || (GameConstants.is幻影(chr.getJob())) || (GameConstants.is米哈尔(chr.getJob()))) {
            int size = chr.getRemainingSpSize();
            mplew.write(size);
            for (int i = 0; i < chr.getRemainingSps().length; i++) {
                if (chr.getRemainingSp(i) > 0) {
                    mplew.write(i + 1);
                    mplew.write(chr.getRemainingSp(i));
                }
            }
        } else {
            mplew.writeShort(chr.getRemainingSp());
        }
        mplew.writeInt(chr.getExp());
        mplew.writeInt(chr.getFame());
        mplew.writeInt(chr.getGachExp());
        mplew.writeLong(DateUtil.getFileTimestamp(System.currentTimeMillis()));
        mplew.writeInt(chr.getMapId());
        mplew.write(chr.getInitialSpawnpoint());
        mplew.writeShort(chr.getSubcategory());
        if (GameConstants.is恶魔猎手(chr.getJob())) {
            mplew.writeInt(chr.getDecorate());
        }
        mplew.write(chr.getFatigue());
        mplew.writeInt(DateUtil.getTime());

        for (MapleTraitType t : MapleTraitType.values()) {//特征点数的总数
            mplew.writeInt(chr.getTrait(t).getTotalExp());
        }
        //mplew.writeZeroBytes(12);
        for (MapleTraitType t : MapleTraitType.values()) {  //今天的特征点数
            mplew.writeShort(0);
        }
        mplew.write(0);  //104
        mplew.writeLong(getTime(-2));//1900-1-1 0:00:00  00 40 E0 FD 3B 37 4F 01

        mplew.writeInt(chr.getStat().pvpExp);
        mplew.write(chr.getStat().pvpRank);
        mplew.writeInt(chr.getBattlePoints());
        mplew.write(6);
        mplew.writeInt(0);
        mplew.write(0);
        mplew.write(HexTool.getByteArrayFromHexString("3B 37 4F 01 00 40 E0 FD"));//1900-1-1 0:00:00  00 40 E0 FD 3B 37 4F 01  倒装时间封包
        for (int i = 0; i < 7; i++) {//104
            mplew.writeLong(0);
        }
        mplew.writeShort(0);//104
        mplew.write(0);  //104
        mplew.write(HexTool.getByteArrayFromHexString("4C 80 CD 01 B0 FB D5 87"));//2012-8-22 9:57:24  B0 FB D5 87 4C 80 CD 01  倒装时间封包

    }

    public static void addCharLook(MaplePacketLittleEndianWriter mplew, MapleCharacter chr, boolean mega) {
        addCharLook(mplew, chr, mega, true);
    }

    public static void addCharLook(MaplePacketLittleEndianWriter mplew, MapleCharacter chr, boolean mega, boolean channelserver) {
        mplew.write(chr.getGender());
        mplew.write(chr.getSkinColor());
        mplew.writeInt(chr.getFace());
        mplew.writeInt(chr.getJob());
        mplew.write(mega ? 0 : 1);
        mplew.writeInt(chr.getHair());

        final Map<Byte, Integer> myEquip = new LinkedHashMap<Byte, Integer>();
        final Map<Byte, Integer> maskedEquip = new LinkedHashMap<Byte, Integer>();
        final Map<Byte, Integer> totemEquip = new LinkedHashMap<Byte, Integer>();
        MapleInventory equip = chr.getInventory(MapleInventoryType.EQUIPPED);

        for (Item item : equip.newList()) {
            if (item.getPosition() < -128) {
                continue;
            }
            byte pos = (byte) (item.getPosition() * -1);
            if ((pos < 100) && (myEquip.get(pos) == null)) {
                myEquip.put(pos, item.getItemId());
            } else if (((pos > 100) || (pos == -128)) && (pos != 111)) {
                pos = (byte) (pos == -128 ? 28 : pos - 100);
                if (myEquip.get(pos) != null) {
                    maskedEquip.put(pos, myEquip.get(pos));
                }
                myEquip.put(pos, item.getItemId());
            } else if (myEquip.get(pos) != null) {
                maskedEquip.put(pos, item.getItemId());
            }
        }

        for (Entry<Byte, Integer> entry : myEquip.entrySet()) {
            mplew.write(entry.getKey());
            mplew.writeInt(entry.getValue());
        }
        mplew.write(0xFF);

        for (Entry<Byte, Integer> entry : maskedEquip.entrySet()) {
            mplew.write(entry.getKey());
            mplew.writeInt(entry.getValue());
        }
        mplew.write(0xFF);

        for (Entry<Byte, Integer> entry : totemEquip.entrySet()) {
            mplew.write(entry.getKey());
            mplew.writeInt(entry.getValue());
        }
        mplew.write(0xFF);

        Item cWeapon = equip.getItem((short) -111);
        mplew.writeInt(cWeapon != null ? cWeapon.getItemId() : 0);
        mplew.write(chr.getElfEar());

        for (int i = 0; i < 3; i++) {
            if (channelserver) {
                mplew.writeInt(chr.getPet(i) != null ? chr.getPet(i).getPetItemId() : 0);
            } else {
                mplew.writeInt(0);
            }
        }
        if (GameConstants.is恶魔猎手(chr.getJob())) {
            mplew.writeInt(chr.getDecorate());
        }
    }

    public static void addExpirationTime(MaplePacketLittleEndianWriter mplew, long time) {
        mplew.writeLong(getTime(time));
    }

    public static void addItemInfo(MaplePacketLittleEndianWriter mplew, Item item, boolean zeroPosition, boolean leaveOut) {
        addItemInfo(mplew, item, zeroPosition, leaveOut, false, false, null);
    }

    public static void addItemInfo(MaplePacketLittleEndianWriter mplew, Item item, boolean zeroPosition, boolean leaveOut, boolean trade) {
        addItemInfo(mplew, item, zeroPosition, leaveOut, trade, false, null);
    }

    public static void addItemInfo(MaplePacketLittleEndianWriter mplew, Item item, boolean zeroPosition, boolean leaveOut, boolean trade, boolean bagSlot, MapleCharacter chr) {
        short pos = item.getPosition();
        if (zeroPosition) {
            if (!leaveOut) {
                mplew.write(0);
            }
        } else {
            if (pos <= -1) {
                pos = (short) (pos * -1);
                if ((pos > 100) && (pos < 1000)) {
                    pos = (short) (pos - 100);
                }
            }
            if (bagSlot) {
                mplew.writeInt(pos % 100 - 1);
            } else if ((!trade) && (item.getType() == 1)) {
                mplew.writeShort(pos);
            } else {
                mplew.write(pos);
            }
        }
        mplew.write(item.getPet() != null ? 3 : item.getType());
        mplew.writeInt(item.getItemId());
        boolean hasUniqueId = (item.getUniqueId() > 0) && (!GameConstants.is结婚戒指(item.getItemId())) && (item.getItemId() / 10000 != 166);

        mplew.write(hasUniqueId ? 1 : 0);
        if (hasUniqueId) {
            mplew.writeLong(item.getUniqueId());
        }
        if (item.getPet() != null) {
            addPetItemInfo(mplew, item, item.getPet(), true);
        } else {
            addExpirationTime(mplew, item.getExpiration());
            mplew.writeInt(chr == null ? -1 : chr.getExtendedSlots().indexOf(Integer.valueOf(item.getItemId())));
            if (item.getType() == 1) {
                Equip equip = (Equip) item;
                mplew.write(equip.getUpgradeSlots());
                mplew.write(equip.getLevel());

                mplew.writeShort(equip.getStr());
                mplew.writeShort(equip.getDex());
                mplew.writeShort(equip.getInt());
                mplew.writeShort(equip.getLuk());

                mplew.writeShort(equip.getHp());
                mplew.writeShort(equip.getMp());

                mplew.writeShort(equip.getWatk());
                mplew.writeShort(equip.getMatk());
                mplew.writeShort(equip.getWdef());
                mplew.writeShort(equip.getMdef());

                mplew.writeShort(equip.getAcc());
                mplew.writeShort(equip.getAvoid());
                mplew.writeShort(equip.getHands());
                mplew.writeShort(equip.getSpeed());
                mplew.writeShort(equip.getJump());

                mplew.writeMapleAsciiString(equip.getOwner());
                mplew.writeShort(equip.getFlag());
                mplew.writeShort(0);
                mplew.write(equip.getIncSkill() > 0 ? 1 : 0);
                mplew.write(Math.max(equip.getBaseLevel(), equip.getEquipLevel()));
                mplew.writeInt(equip.getExpPercentage() * 100000);//装备经验百分比
                mplew.writeInt(equip.getDurability());//耐久力
                mplew.writeInt(equip.getViciousHammer());
                mplew.writeShort(equip.getPVPDamage());
                mplew.write(equip.getState());
                mplew.write(equip.getEnhance());
                mplew.writeShort(equip.getPotential1() <= 0 ? 0 : equip.getPotential1());//潜能1
                mplew.writeShort(equip.getPotential2() <= 0 ? 0 : equip.getPotential2());//潜能2
                mplew.writeShort(equip.getPotential3() <= 0 ? 0 : equip.getPotential3());//潜能3
                mplew.writeShort(equip.getPotential4() <= 0 ? 0 : equip.getPotential4());//潜能4
                mplew.writeShort(equip.getPotential5() <= 0 ? 0 : equip.getPotential5());//潜能5

                mplew.writeShort(equip.getSocketState());
                mplew.writeShort(equip.getSocket1() % 10000);
                mplew.writeShort(equip.getSocket2() % 10000);
                mplew.writeShort(equip.getSocket3() % 10000);

                if (!hasUniqueId) {
                    mplew.writeLong(equip.getInventoryId() <= 0 ? -1 : equip.getInventoryId());
                }
                mplew.writeLong(getTime(-2));//00 40 E0 FD 3B 37 4F 01
                mplew.writeInt(-1);
                mplew.writeLong(0);
                mplew.writeLong(getTime(-2));
                mplew.writeLong(0);
                mplew.writeLong(0);
            } else {
                mplew.writeShort(item.getQuantity());
                mplew.writeMapleAsciiString(item.getOwner());
                mplew.writeShort(item.getFlag());
                if ((GameConstants.isThrowingStar(item.getItemId())) || (GameConstants.isBullet(item.getItemId())) || (item.getItemId() / 10000 == 287)) {
                    mplew.writeLong(item.getInventoryId() <= 0 ? -1 : item.getInventoryId());
                }
            }
        }
    }

    public static void serializeMovementList(final MaplePacketLittleEndianWriter lew, final List<LifeMovementFragment> moves) {
        lew.write(moves.size());
        for (LifeMovementFragment move : moves) {
            move.serialize(lew);
        }
    }

    public static void addAnnounceBox(final MaplePacketLittleEndianWriter mplew, final MapleCharacter chr) {
        if ((chr.getPlayerShop() != null) && (chr.getPlayerShop().isOwner(chr)) && (chr.getPlayerShop().getShopType() != 1) && (chr.getPlayerShop().isAvailable())) {
            addInteraction(mplew, chr.getPlayerShop());
        } else {
            mplew.write(0);
        }
    }

    public static void addInteraction(final MaplePacketLittleEndianWriter mplew, final IMaplePlayerShop shop) {
        mplew.write(shop.getGameType());
        mplew.writeInt(((AbstractPlayerStore) shop).getObjectId());
        mplew.writeMapleAsciiString(shop.getDescription());
        if (shop.getShopType() != 1) {
            mplew.write(shop.getPassword().length() > 0 ? 1 : 0);
        }
        mplew.write(shop.getItemId() - 5030000);
        mplew.write(shop.getSize());
        mplew.write(shop.getMaxSize());
        if (shop.getShopType() != 1) {
            mplew.write(shop.isOpen() ? 0 : 1);
        }
    }

    public static void addCharacterInfo(final MaplePacketLittleEndianWriter mplew, final MapleCharacter chr) {
        mplew.writeLong(-1);
        mplew.write(new byte[7]);
        addCharStats(mplew, chr);
        mplew.write(chr.getBuddylist().getCapacity());//最大好友

        if (chr.getBlessOfFairyOrigin() != null) {
            mplew.write(1);
            mplew.writeMapleAsciiString(chr.getBlessOfFairyOrigin());
        } else {
            mplew.write(0);
        }

        if (chr.getBlessOfEmpressOrigin() != null) {
            mplew.write(1);
            mplew.writeMapleAsciiString(chr.getBlessOfEmpressOrigin());
        } else {
            mplew.write(0);
        }

        MapleQuestStatus ultExplorer = chr.getQuestNoAdd(MapleQuest.getInstance(111111));
        if ((ultExplorer != null) && (ultExplorer.getCustomData() != null)) {
            mplew.write(1);
            mplew.writeMapleAsciiString(ultExplorer.getCustomData());
        } else {
            mplew.write(0);
        }

        addInventoryInfo(mplew, chr);
        addSkillInfo(mplew, chr);
        addCoolDownInfo(mplew, chr);
        addQuestInfo(mplew, chr);
        addRingInfo(mplew, chr);
        addRocksInfo(mplew, chr);

        chr.QuestInfoPacket(mplew);
        if ((chr.getJob() >= 3300) && (chr.getJob() <= 3312)) {
            addJaguarInfo(mplew, chr);
        }
        mplew.writeInt(0);//+
        if(chr.getJob()>=2400 && chr.getJob()<=2412){
            mplew.writeInt(chr.getphantomskills().get(10));
            mplew.writeInt(chr.getphantomskills().get(11));
            mplew.writeInt(chr.getphantomskills().get(12));
            mplew.writeInt(chr.getphantomskills().get(13));

            mplew.writeInt(chr.getphantomskills().get(20));
            mplew.writeInt(chr.getphantomskills().get(21));
            mplew.writeInt(chr.getphantomskills().get(22));
            mplew.writeInt(chr.getphantomskills().get(23));

            mplew.writeInt(chr.getphantomskills().get(30));
            mplew.writeInt(chr.getphantomskills().get(31));
            mplew.writeInt(chr.getphantomskills().get(32));
            mplew.writeInt(chr.getphantomskills().get(40));
            mplew.writeInt(chr.getphantomskills().get(41));

            mplew.writeInt(chr.getphantomskills().get(1));
            mplew.writeInt(chr.getphantomskills().get(2));
            mplew.writeInt(chr.getphantomskills().get(3));
            mplew.writeInt(chr.getphantomskills().get(4));
        }else {
        //mplew.writeZeroBytes(74);
            mplew.writeZeroBytes(68);//+
        }
        mplew.writeShort(0);//+
        mplew.writeLong(1);
        mplew.write(HexTool.getByteArrayFromHexString("6C BC 18 06"));
        mplew.writeZeroBytes(68);
        mplew.write(HexTool.getByteArrayFromHexString("A0 6D B0 40 0B 81 CD 01"));
        mplew.writeInt(0);
        mplew.write(1);
    }
 

    public static void addMonsterBookInfo(final MaplePacketLittleEndianWriter mplew, final MapleCharacter chr) {
        mplew.writeInt(0);
        if (chr.getMonsterBook().getSetScore() > 0) {
            chr.getMonsterBook().writeFinished(mplew);
        } else {
            chr.getMonsterBook().writeUnfinished(mplew);
        }
        mplew.writeInt(chr.getMonsterBook().getSet());
        mplew.writeZeroBytes(9);
    }
    
    public static void addCharacterphantomskills(final MaplePacketLittleEndianWriter mplew, final MapleCharacter chr) {
        //03 00 ,15 00 01 ,16 00 01 ,1A 00 01 ,02 00 00 00 ,00 00 ,00 00 ,
        //area : 03 00 ,24 C8 ,0A 00 53 74 61 67 65 4B 65 79 3D 30 ,DF 59 ,0F 00 76 65 6C 30 30 3D 32 3B 76 65 6C 30 31 3D 33 54 46 ,06 00 75 73 65 3D 2D 31
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("select * from phantomskills where characterid=?");
            ps.setInt(1, chr.getId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                mplew.writeInt(rs.getInt("skill1_0"));
                mplew.writeInt(rs.getInt("skill1_1"));
                mplew.writeInt(rs.getInt("skill1_2"));
                mplew.writeInt(rs.getInt("skill1_3"));

                mplew.writeInt(rs.getInt("skill2_0"));
                mplew.writeInt(rs.getInt("skill2_1"));
                mplew.writeInt(rs.getInt("skill2_2"));
                mplew.writeInt(rs.getInt("skill2_3"));

                mplew.writeInt(rs.getInt("skill3_0"));
                mplew.writeInt(rs.getInt("skill3_1"));
                mplew.writeInt(rs.getInt("skill3_2"));

                mplew.writeInt(rs.getInt("skill4_0"));
                mplew.writeInt(rs.getInt("skill4_1"));

                mplew.writeInt(rs.getInt("skill1"));
                mplew.writeInt(rs.getInt("skill2"));
                mplew.writeInt(rs.getInt("skill3"));
                mplew.writeInt(rs.getInt("skill4"));
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {}
    }    

    public static void addPetItemInfo(final MaplePacketLittleEndianWriter mplew, Item item, MaplePet pet, boolean active) {
        if (item == null) {
            mplew.writeLong(getKoreanTimestamp((long) (System.currentTimeMillis() * 1.5)));
        } else {
            addExpirationTime(mplew, item.getExpiration() <= System.currentTimeMillis() ? -1 : item.getExpiration());
        }
        mplew.writeInt(-1);
        mplew.writeAsciiString(pet.getName(), 13);
        mplew.write(pet.getLevel());
        mplew.writeShort(pet.getCloseness());
        mplew.write(pet.getFullness());
        if (item == null) {
            mplew.writeLong(getKoreanTimestamp((long) (System.currentTimeMillis() * 1.5)));
        } else {
            addExpirationTime(mplew, item.getExpiration() <= System.currentTimeMillis() ? -1 : item.getExpiration());
        }
        mplew.writeShort(0);
        mplew.writeShort(pet.getFlags());
        mplew.writeInt((pet.getPetItemId() == 5000054) && (pet.getSecondsLeft() > 0) ? pet.getSecondsLeft() : 0);
        mplew.writeShort(0);
        mplew.write(active ? pet.getSummoned() ? pet.getSummonedValue() : 0 : 0);//显示装备栏上宠物的位置
        mplew.writeInt(active ? pet.getBuffSkill() : 0);
    }

    public static void addShopInfo(MaplePacketLittleEndianWriter mplew, MapleShop shop, MapleClient c) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

        mplew.writeInt(DateUtil.getTime());
        mplew.write(shop.getRanks().size() > 0 ? 1 : 0);
        if (shop.getRanks().size() > 0) {
            mplew.write(shop.getRanks().size());
            for (Pair<Integer, String> s : shop.getRanks()) {
                mplew.writeInt(s.left);
                mplew.writeMapleAsciiString(s.right);
            }
        }

        mplew.writeShort(shop.getItems().size() + c.getPlayer().getRebuy().size());
        for (MapleShopItem item : shop.getItems()) {
            addShopItemInfo(mplew, item, shop, ii, null);
        }
        for (Item i : c.getPlayer().getRebuy()) {
            addShopItemInfo(mplew, new MapleShopItem(i.getItemId(), (int) ii.getPrice(i.getItemId()), i.getQuantity()), shop, ii, i);
        }
    }

    public static void addShopItemInfo(MaplePacketLittleEndianWriter mplew, MapleShopItem item, MapleShop shop, MapleItemInformationProvider ii, Item i) {
        mplew.writeInt(item.getItemId());
        mplew.writeInt(item.getPrice());
        mplew.write(0);
        mplew.writeInt(item.getReqItem());
        mplew.writeInt(item.getReqItemQ());
        mplew.writeLong(0);
        mplew.writeLong(1440 * item.getPeriod());

        mplew.writeInt(item.getRank());
        mplew.writeInt(0);//104
        mplew.writeMapleAsciiString("1900010100");
        mplew.writeMapleAsciiString("2079010100");
        mplew.write(item.getState() > 0 ? 1 : 0);
        mplew.writeInt(item.getPeriod() > 0 ? 1 : 0);
        if ((!GameConstants.isThrowingStar(item.getItemId())) && (!GameConstants.isBullet(item.getItemId()))) {
            mplew.writeShort(1);
            mplew.writeShort(item.getBuyable());
        } else {
            mplew.writeZeroBytes(6);
            mplew.writeShort(BitTools.doubleToShortBits(ii.getPrice(item.getItemId())));
            mplew.writeShort(ii.getSlotMax(item.getItemId()));
        }
        mplew.write(i == null ? 0 : 1);
        if (i != null) {
            addItemInfo(mplew, i, true, true);
        }
        if (shop.getRanks().size() > 0) {
            mplew.write(item.getRank() >= 0 ? 1 : 0);
            if (item.getRank() >= 0) {
                mplew.write(item.getRank());
            }
        }
        //00 00 00 00 00 01en 00 2C 01 00
        mplew.writeLong(0);
        mplew.writeLong(0);
        for (int x = 0; x < 4; x++) {
            mplew.writeLong(9410165 + x);
        }
    }

    public static void addJaguarInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        mplew.write(chr.getIntNoRecord(111112));
        mplew.writeZeroBytes(20);
    }

    public static <E extends Buffstat> void writeSingleMask(MaplePacketLittleEndianWriter mplew, E statup) {
        for (int i = 8; i >= 1; i--) {
            mplew.writeInt(i == statup.getPosition() ? statup.getValue() : 0);
        }
    }

    public static <E extends Buffstat> void writeMask(MaplePacketLittleEndianWriter mplew, Collection<E> statups) {
        int[] mask = new int[8];
        for (Buffstat statup : statups) {
            mask[(statup.getPosition() - 1)] |= statup.getValue();
        }
        for (int i = mask.length; i >= 1; i--) {
            mplew.writeInt(mask[(i - 1)]);
        }
    }

    public static <E extends Buffstat> void writeBuffMask(MaplePacketLittleEndianWriter mplew, Collection<Pair<E, Integer>> statups) {
        int[] mask = new int[8];
        for (Pair<E, Integer> statup : statups) {
            mask[((statup.left).getPosition() - 1)] |= ((Buffstat) statup.left).getValue();
        }
        for (int i = mask.length; i >= 1; i--) {
            mplew.writeInt(mask[(i - 1)]);
        }
    }

    public static <E extends Buffstat> void writeBuffMask(MaplePacketLittleEndianWriter mplew, Map<E, Integer> statups) {
        int[] mask = new int[8];
        for (Buffstat statup : statups.keySet()) {
            mask[(statup.getPosition() - 1)] |= statup.getValue();
        }
        for (int i = mask.length; i >= 1; i--) {
            mplew.writeInt(mask[(i - 1)]);
        }
    }
}