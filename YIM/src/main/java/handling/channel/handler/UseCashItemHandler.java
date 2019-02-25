package handling.channel.handler;

import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.MapleClient;
import client.MapleQuestStatus;
import client.MapleStat;
import client.MonsterFamiliar;
import client.PlayerStats;
import client.Skill;
import client.SkillFactory;
import client.anticheat.CheatTracker;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.ItemFlag;
import client.inventory.MapleAndroid;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.inventory.MaplePet.PetFlag;
import constants.GameConstants;
import handling.channel.ChannelServer;
import handling.channel.PlayerStorage;
import handling.world.World.Broadcast;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.PrintStream;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MapleShop;
import server.MapleShopFactory;
import server.MapleStatEffect;
import server.MapleStorage;
import server.RandomRewards;
import server.Randomizer;
import server.StructFamiliar;
import server.StructItemOption;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.life.MapleLifeFactory;
import server.maps.FieldLimitType;
import server.maps.MapleFoothold;
import server.maps.MapleFootholdTree;
import server.maps.MapleLove;
import server.maps.MapleMap;
import server.maps.MapleMapFactory;
import server.maps.MapleMist;
import server.maps.MapleTVEffect;
import server.quest.MapleQuest;
import tools.FileoutputUtil;
import tools.MaplePacketCreator;
import tools.data.LittleEndianAccessor;
import tools.packet.MTSCSPacket;
import tools.packet.PetPacket;

public class UseCashItemHandler {

    public static void handlePacket(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        if ((chr == null) || (chr.getMap() == null) || (chr.inPVP())) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        chr.updateTick(slea.readInt());
        chr.setScrolledPosition((short) 0);
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        byte slot = (byte) slea.readShort();
        int itemId = slea.readInt();
        int itemType = itemId / 10000;
        Item toUse = chr.getInventory(MapleInventoryType.CASH).getItem((short) slot);
        if ((toUse == null) || (toUse.getItemId() != itemId) || (toUse.getQuantity() < 1) || (chr.hasBlockedInventory())) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if (chr.isAdmin()) {
            chr.dropMessage(5, new StringBuilder().append("使用商城道具 物品ID: ").append(itemId).append(" 物品类型: ").append(itemType).toString());
        }
        boolean used = false;
        boolean cc = false;
        switch (itemType) {
            case 504: {
                if ((itemId == 5043000) || (itemId == 5043001)) {
                    short questid = slea.readShort();
                    int npcid = slea.readInt();
                    MapleQuest quest = MapleQuest.getInstance(questid);
                    if ((chr.getQuest(quest).getStatus() == 1) && (quest.canComplete(chr, Integer.valueOf(npcid)))) {
                        int mapId = MapleLifeFactory.getNPCLocation(npcid);
                        if (mapId != -1) {
                            MapleMap map = c.getChannelServer().getMapFactory().getMap(mapId);
                            if ((map.containsNPC(npcid)) && (!FieldLimitType.VipRock.check(chr.getMap().getFieldLimit())) && (!FieldLimitType.VipRock.check(map.getFieldLimit())) && (!chr.isInBlockedMap())) {
                                chr.changeMap(map, map.getPortal(0));
                            }
                            used = true;
                        } else {
                            chr.dropMessage(1, "使用道具出现未知的错误.");
                        }
                    }
                } else if (itemId == 5042000) {
                    MapleMap map = c.getChannelServer().getMapFactory().getMap(701000200);
                    chr.changeMap(map, map.getPortal(0));
                    used = true;
                } else if (itemId == 5040005) {
                    chr.dropMessage(5, "无法使用这个道具.");
                } else {
                    used = InventoryHandler.UseTeleRock(slea, c, itemId);
                }
                break;
            }
            case 505:
                if (itemId == 5050000) {
                    Map statupdate = new EnumMap(MapleStat.class);
                    int apto = (int) slea.readLong();
                    int apfrom = (int) slea.readLong();
                    if (chr.isGM()) {
                        chr.dropMessage(5, new StringBuilder().append("洗能力点 apto: ").append(apto).append(" apfrom: ").append(apfrom).toString());
                    }
                    if (apto == apfrom) {
                        break;
                    }
                    int job = chr.getJob();
                    PlayerStats playerst = chr.getStat();
                    used = true;
                    switch (apto) {
                        case 64:
                            if (playerst.getStr() < 999) {
                                break;
                            }
                            used = false;
                            break;
                        case 128:
                            if (playerst.getDex() < 999) {
                                break;
                            }
                            used = false;
                            break;
                        case 256:
                            if (playerst.getInt() < 999) {
                                break;
                            }
                            used = false;
                            break;
                        case 512:
                            if (playerst.getLuk() < 999) {
                                break;
                            }
                            used = false;
                            break;
                        case 2048:
                            if (playerst.getMaxHp() < 99999) {
                                break;
                            }
                            used = false;
                            break;
                        case 8192:
                            if (playerst.getMaxMp() < 99999) {
                                break;
                            }
                            used = false;
                    }

                    switch (apfrom) {
                        case 64:
                            if ((playerst.getStr() > 4) && ((chr.getJob() % 1000 / 100 != 1) || (playerst.getStr() > 35))) {
                                break;
                            }
                            used = false;
                            break;
                        case 128:
                            if ((playerst.getDex() > 4) && ((chr.getJob() % 1000 / 100 != 3) || (playerst.getDex() > 25)) && ((chr.getJob() % 1000 / 100 != 4) || (playerst.getDex() > 25)) && ((chr.getJob() % 1000 / 100 != 5) || (playerst.getDex() > 20))) {
                                break;
                            }
                            used = false;
                            break;
                        case 256:
                            if ((playerst.getInt() > 4) && ((chr.getJob() % 1000 / 100 != 2) || (playerst.getInt() > 20))) {
                                break;
                            }
                            used = false;
                            break;
                        case 512:
                            if (playerst.getLuk() > 4) {
                                break;
                            }
                            used = false;
                            break;
                        case 2048:
                            if ((chr.getHpApUsed() > 0) && (chr.getHpApUsed() < 10000)) {
                                break;
                            }
                            used = false;
                            break;
                        case 8192:
                            if ((chr.getHpApUsed() > 0) && (chr.getHpApUsed() < 10000)) {
                                break;
                            }
                            used = false;
                    }

                    if (used) {
                        switch (apto) {
                            case 64: {
                                int toSet = playerst.getStr() + 1;
                                playerst.setStr((short) toSet, chr);
                                statupdate.put(MapleStat.力量, Integer.valueOf(toSet));
                                break;
                            }
                            case 128: {
                                int toSet = playerst.getDex() + 1;
                                playerst.setDex((short) toSet, chr);
                                statupdate.put(MapleStat.敏捷, Integer.valueOf(toSet));
                                break;
                            }
                            case 256: {
                                int toSet = playerst.getInt() + 1;
                                playerst.setInt((short) toSet, chr);
                                statupdate.put(MapleStat.智力, Integer.valueOf(toSet));
                                break;
                            }
                            case 512: {
                                int toSet = playerst.getLuk() + 1;
                                playerst.setLuk((short) toSet, chr);
                                statupdate.put(MapleStat.运气, Integer.valueOf(toSet));
                                break;
                            }
                            case 2048: {
                                int maxhp = playerst.getMaxHp();
                                if (GameConstants.is新手职业(job)) {
                                    maxhp += Randomizer.rand(4, 8);
                                } else if (((job >= 100) && (job <= 132)) || (GameConstants.is米哈尔(job)) || ((job >= 3200) && (job <= 3212)) || ((job >= 1100) && (job <= 1112)) || ((job >= 3100) && (job <= 3112))) {
                                    maxhp += Randomizer.rand(36, 42);
                                } else if (((job >= 200) && (job <= 232)) || (GameConstants.is龙神(job)) || ((job >= 1200) && (job <= 1212))) {
                                    maxhp += Randomizer.rand(10, 12);
                                } else if (((job >= 300) && (job <= 322)) || ((job >= 400) && (job <= 434)) || ((job >= 1300) && (job <= 1312)) || ((job >= 1400) && (job <= 1412)) || ((job >= 3300) && (job <= 3312)) || ((job >= 2300) && (job <= 2312)) || ((job >= 2400) && (job <= 2412))) {
                                    maxhp += Randomizer.rand(14, 18);
                                } else if (((job >= 510) && (job <= 512)) || ((job >= 580) && (job <= 582)) || ((job >= 1510) && (job <= 1512))) {
                                    maxhp += Randomizer.rand(24, 28);
                                } else if (((job >= 500) && (job <= 532)) || ((job >= 590) && (job <= 592)) || (GameConstants.is龙的传人(job)) || ((job >= 3500) && (job <= 3512)) || (job == 1500)) {
                                    maxhp += Randomizer.rand(16, 20);
                                } else if ((job >= 2000) && (job <= 2112)) {
                                    maxhp += Randomizer.rand(34, 38);
                                } else {
                                    maxhp += Randomizer.rand(16, 20);
                                }
                                maxhp = Math.min(99999, Math.abs(maxhp));
                                chr.setHpApUsed((short) (chr.getHpApUsed() + 1));
                                playerst.setMaxHp(maxhp, chr);
                                statupdate.put(MapleStat.MAXHP, Integer.valueOf(maxhp));
                                break;
                            }
                            case 8192: {
                                int maxmp = playerst.getMaxMp();
                                if (GameConstants.is新手职业(job)) {
                                    maxmp += Randomizer.rand(6, 8);
                                } else {
                                    if ((job >= 3100) && (job <= 3112)) {
                                        break;
                                    }
                                    if (((job >= 100) && (job <= 132)) || ((job >= 1100) && (job <= 1112)) || ((job >= 2000) && (job <= 2112))) {
                                        maxmp += Randomizer.rand(4, 9);
                                    } else if (((job >= 200) && (job <= 232)) || (GameConstants.is龙神(job)) || ((job >= 3200) && (job <= 3212)) || ((job >= 1200) && (job <= 1212))) {
                                        maxmp += Randomizer.rand(32, 36);
                                    } else if (((job >= 300) && (job <= 322)) || ((job >= 400) && (job <= 434)) || ((job >= 500) && (job <= 592)) || ((job >= 3200) && (job <= 3212)) || ((job >= 3500) && (job <= 3512)) || ((job >= 1300) && (job <= 1312)) || ((job >= 1400) && (job <= 1412)) || ((job >= 1500) && (job <= 1512)) || ((job >= 2300) && (job <= 2312)) || ((job >= 2400) && (job <= 2412))) {
                                        maxmp += Randomizer.rand(8, 10);
                                    } else {
                                        maxmp += Randomizer.rand(6, 8);
                                    }
                                }
                                maxmp = Math.min(99999, Math.abs(maxmp));
                                chr.setHpApUsed((short) (chr.getHpApUsed() + 1));
                                playerst.setMaxMp(maxmp, chr);
                                statupdate.put(MapleStat.MAXMP, Integer.valueOf(maxmp));
                            }
                        }

                        switch (apfrom) {
                            case 64: {
                                int toSet = playerst.getStr() - 1;
                                playerst.setStr((short) toSet, chr);
                                statupdate.put(MapleStat.力量, Integer.valueOf(toSet));
                                break;
                            }
                            case 128: {
                                int toSet = playerst.getDex() - 1;
                                playerst.setDex((short) toSet, chr);
                                statupdate.put(MapleStat.敏捷, Integer.valueOf(toSet));
                                break;
                            }
                            case 256: {
                                int toSet = playerst.getInt() - 1;
                                playerst.setInt((short) toSet, chr);
                                statupdate.put(MapleStat.智力, Integer.valueOf(toSet));
                                break;
                            }
                            case 512: {
                                int toSet = playerst.getLuk() - 1;
                                playerst.setLuk((short) toSet, chr);
                                statupdate.put(MapleStat.运气, Integer.valueOf(toSet));
                                break;
                            }
                            case 2048: {
                                int maxhp = playerst.getMaxHp();
                                if (GameConstants.is新手职业(job)) {
                                    maxhp -= 12;
                                } else if (((job >= 200) && (job <= 232)) || ((job >= 1200) && (job <= 1212))) {
                                    maxhp -= 10;
                                } else if (((job >= 300) && (job <= 322)) || ((job >= 400) && (job <= 434)) || ((job >= 1300) && (job <= 1312)) || ((job >= 1400) && (job <= 1412)) || ((job >= 3300) && (job <= 3312)) || ((job >= 3500) && (job <= 3512)) || ((job >= 2300) && (job <= 2312)) || ((job >= 2400) && (job <= 2412))) {
                                    maxhp -= 15;
                                } else if (((job >= 500) && (job <= 592)) || ((job >= 1500) && (job <= 1512))) {
                                    maxhp -= 22;
                                } else if (((job >= 100) && (job <= 132)) || ((job >= 1100) && (job <= 1112)) || ((job >= 3100) && (job <= 3112))) {
                                    maxhp -= 32;
                                } else if (((job >= 2000) && (job <= 2112)) || ((job >= 3200) && (job <= 3212))) {
                                    maxhp -= 40;
                                } else {
                                    maxhp -= 20;
                                }
                                chr.setHpApUsed((short) (chr.getHpApUsed() - 1));
                                playerst.setMaxHp(maxhp, chr);
                                statupdate.put(MapleStat.MAXHP, Integer.valueOf(maxhp));
                                break;
                            }
                            case 8192: {
                                int maxmp = playerst.getMaxMp();
                                if (GameConstants.is新手职业(job)) {
                                    maxmp -= 8;
                                } else {
                                    if ((job >= 3100) && (job <= 3112)) {
                                        break;
                                    }
                                    if (((job >= 100) && (job <= 132)) || (GameConstants.is米哈尔(job)) || ((job >= 1100) && (job <= 1112))) {
                                        maxmp -= 4;
                                    } else if (((job >= 200) && (job <= 232)) || ((job >= 1200) && (job <= 1212))) {
                                        maxmp -= 30;
                                    } else if (((job >= 500) && (job <= 592)) || ((job >= 300) && (job <= 322)) || ((job >= 400) && (job <= 434)) || ((job >= 1300) && (job <= 1312)) || ((job >= 1400) && (job <= 1412)) || ((job >= 1500) && (job <= 1512)) || ((job >= 3300) && (job <= 3312)) || ((job >= 3500) && (job <= 3512)) || ((job >= 2300) && (job <= 2312)) || ((job >= 2400) && (job <= 2412))) {
                                        maxmp -= 10;
                                    } else if ((job >= 2000) && (job <= 2112)) {
                                        maxmp -= 5;
                                    } else {
                                        maxmp -= 20;
                                    }
                                }
                                chr.setHpApUsed((short) (chr.getHpApUsed() - 1));
                                playerst.setMaxMp(maxmp, chr);
                                statupdate.put(MapleStat.MAXMP, Integer.valueOf(maxmp));
                            }
                        }

                        c.getSession().write(MaplePacketCreator.updatePlayerStats(statupdate, true, chr));
                    }
                } else if ((itemId >= 5050005) && (!GameConstants.is龙神(chr.getJob()))) {
                    chr.dropMessage(1, "只有龙神职业才能使用这个道具.");
                } else if ((itemId < 5050005) && (GameConstants.is龙神(chr.getJob()))) {
                    chr.dropMessage(1, "龙神职业无法使用这个道具.");
                } else {
                    int skill1 = slea.readInt();
                    int skill2 = slea.readInt();
                    for (int i : GameConstants.blockedSkills) {
                        if (skill1 == i) {
                            chr.dropMessage(1, "该技能未修复，无法增加此技能.");
                            return;
                        }
                    }
                    Skill skillSPTo = SkillFactory.getSkill(skill1);
                    Skill skillSPFrom = SkillFactory.getSkill(skill2);
                    if ((skillSPTo.isBeginnerSkill()) || (skillSPFrom.isBeginnerSkill())) {
                        chr.dropMessage(1, "You may not add beginner skills.");
                    } else if (GameConstants.getSkillBookForSkill(skill1) != GameConstants.getSkillBookForSkill(skill2)) {
                        chr.dropMessage(1, "You may not add different job skills.");
                    } else if ((chr.getSkillLevel(skillSPTo) + 1 <= skillSPTo.getMaxLevel()) && (chr.getSkillLevel(skillSPFrom) > 0) && (skillSPTo.canBeLearnedBy(chr.getJob()))) {
                        if ((skillSPTo.isFourthJob()) && (chr.getSkillLevel(skillSPTo) + 1 > chr.getMasterLevel(skillSPTo))) {
                            chr.dropMessage(1, "You will exceed the master level.");
                            break;
                        }
                        if (itemId >= 5050005) {
                            if ((GameConstants.getSkillBookForSkill(skill1) != (itemId - 5050005) * 2) && (GameConstants.getSkillBookForSkill(skill1) != (itemId - 5050005) * 2 + 1)) {
                                chr.dropMessage(1, "You may not add this job SP using this reset.");
                                break;
                            }
                        } else {
                            int theJob = GameConstants.getJobNumber(skill2 / 10000);
                            switch (skill2 / 10000) {
                                case 430:
                                    theJob = 1;
                                    break;
                                case 431:
                                case 432:
                                    theJob = 2;
                                    break;
                                case 433:
                                    theJob = 3;
                                    break;
                                case 434:
                                    theJob = 4;
                            }

                            if (theJob != itemId - 5050000) {
                                chr.dropMessage(1, "You may not subtract from this skill. Use the appropriate SP reset.");
                                break;
                            }
                        }
                        chr.changeSkillLevel(skillSPFrom, (byte) (chr.getSkillLevel(skillSPFrom) - 1), chr.getMasterLevel(skillSPFrom));
                        chr.changeSkillLevel(skillSPTo, (byte) (chr.getSkillLevel(skillSPTo) + 1), chr.getMasterLevel(skillSPTo));
                        used = true;
                    }
                }
                break;
            case 506:
                if (itemId == 5060000) {
                    Item item = chr.getInventory(MapleInventoryType.EQUIPPED).getItem(slea.readShort());
                    if ((item != null) && (item.getOwner().equals(""))) {
                        boolean change = true;
                        for (String z : GameConstants.RESERVED) {
                            if (chr.getName().indexOf(z) != -1) {
                                change = false;
                            }
                        }
                        if (change) {
                            item.setOwner(chr.getName());
                            chr.forceReAddItem(item, MapleInventoryType.EQUIPPED);
                            used = true;
                            break;
                        }
                    } else {
                        chr.dropMessage(1, "请将道具直接点在你需要刻名的装备上.");
                        break;
                    }
                } else if ((itemId == 5060001) || (itemId == 5061000) || (itemId == 5061001) || (itemId == 5061002) || (itemId == 5061003)) {
                    MapleInventoryType type = MapleInventoryType.getByType((byte) slea.readInt());
                    Item item = chr.getInventory(type).getItem((short) (byte) slea.readInt());
                    if ((item != null) && (item.getExpiration() == -1L)) {
                        short flag = item.getFlag();
                        flag = (short) (flag | ItemFlag.LOCK.getValue());
                        item.setFlag(flag);
                        long days = 0L;
                        if (itemId == 5061000) {
                            days = 7L;
                        } else if (itemId == 5061001) {
                            days = 30L;
                        } else if (itemId == 5061002) {
                            days = 90L;
                        } else if (itemId == 5061003) {
                            days = 365L;
                        }
                        if (chr.isGM()) {
                            chr.dropMessage(5, new StringBuilder().append("使用封印之锁 物品ID: ").append(itemId).append(" 天数: ").append(days).toString());
                        }
                        if (days > 0L) {
                            item.setExpiration(System.currentTimeMillis() + days * 24L * 60L * 60L * 1000L);
                        }
                        chr.forceReAddItem_Flag(item, type);
                        used = true;
                    } else {
                        chr.dropMessage(1, "使用道具出现错误.");
                    }
                } else if (itemId == 5064000) {
                    short dst = slea.readShort();
                    Item item;
                    if (dst < 0) {
                        item = chr.getInventory(MapleInventoryType.EQUIPPED).getItem(dst);
                    } else {
                        item = null;
                    }
                    if ((item != null) && (item.getType() == 1)) {
                        if (((Equip) item).getEnhance() >= 12) {
                            chr.dropMessage(1, "该道具已无法继续使用防爆卷轴.");
                            break;
                        }
                        short flag = item.getFlag();

                        if (!ItemFlag.防爆卷轴.check(flag)) {
                            flag = (short) (flag | ItemFlag.防爆卷轴.getValue());
                            item.setFlag(flag);

                            chr.forceReAddItem_Flag(item, MapleInventoryType.EQUIPPED);
                            chr.getMap().broadcastMessage(chr, MaplePacketCreator.getScrollEffect(chr.getId(), itemId, item.getItemId()), true);
                            used = true;
                        } else {
                            chr.dropMessage(1, "已经获得了相同效果。");
                            break;
                        }
                    } else {
                        chr.dropMessage(1, "请将卷轴点在你需要保护的装备上.");
                        break;
                    }
                } else if (itemId == 5064100) {
                    short dst = slea.readShort();
                    Item item;
                    if (dst < 0) {
                        item = chr.getInventory(MapleInventoryType.EQUIPPED).getItem(dst);
                    } else {
                        item = null;
                    }
                    if ((item != null) && (item.getType() == 1)) {
                        short flag = item.getFlag();

                        if (!ItemFlag.保护卷轴.check(flag)) {
                            flag = (short) (flag | ItemFlag.保护卷轴.getValue());
                            item.setFlag(flag);

                            chr.forceReAddItem_Flag(item, MapleInventoryType.EQUIPPED);
                            chr.getMap().broadcastMessage(chr, MaplePacketCreator.getScrollEffect(chr.getId(), itemId, item.getItemId()), true);
                            used = true;
                        } else {
                            chr.dropMessage(1, "已经获得了相同效果。");
                            break;
                        }
                    } else {
                        chr.dropMessage(1, "请将卷轴点在你需要保护的装备上.");
                        break;
                    }
                } else if (itemId == 5064300) {
                    short dst = slea.readShort();
                    Item item;
                    if (dst < 0) {
                        item = chr.getInventory(MapleInventoryType.EQUIPPED).getItem(dst);
                    } else {
                        item = null;
                    }
                    if ((item != null) && (item.getType() == 1)) {
                        short flag = item.getFlag();

                        if (!ItemFlag.防护卷轴.check(flag)) {
                            flag = (short) (flag | ItemFlag.防护卷轴.getValue());
                            item.setFlag(flag);

                            chr.forceReAddItem_Flag(item, MapleInventoryType.EQUIPPED);
                            chr.getMap().broadcastMessage(chr, MaplePacketCreator.getScrollEffect(chr.getId(), itemId, item.getItemId()), true);
                            used = true;
                        } else {
                            chr.dropMessage(1, "已经获得了相同效果。");
                            break;
                        }
                    } else {
                        chr.dropMessage(1, "请将卷轴点在你需要保护的装备上.");
                        break;
                    }
                    
                } else if (itemId == 5060003) {//花生机
                    Item item = chr.getInventory(MapleInventoryType.ETC).findById(4170023);
                    if ((item == null) || (item.getQuantity() <= 0)) {
                        return;
                    }
                    if (getIncubatedItems(c, itemId)) {//if里的那句语句为奖励语句 下面这句是消耗需要的物品 4170023 花生
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.ETC, item.getPosition(), (short) 1, false);
                        used = true;
                    }
                    
                } else if (itemId == 5060006) {//水晶八音盒
                    MapleItemInformationProvider iis = MapleItemInformationProvider.getInstance();
                    
                    int reward = RandomRewards.get水晶八音盒();//随机取得抽奖物品
                    String box;
                    String key;
                    int keyIDforRemoval = 4170025;//开启需要的物品
                    box = "水晶八音盒";
                    key = "英雄的钥匙";
                    
                    
                   if ((c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 2) || (c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 2) || (c.getPlayer().getInventory(MapleInventoryType.SETUP).getNumFreeSlot() < 2) || (c.getPlayer().getInventory(MapleInventoryType.CASH).getNumFreeSlot() < 2) || (c.getPlayer().getInventory(MapleInventoryType.ETC).getNumFreeSlot() < 2)) {
                       c.getPlayer().dropMessage(5, "请确保你有足够的背包里的各个栏目有1个空间以上.");
                       return;
                   }
                    
                    Item item = chr.getInventory(MapleInventoryType.ETC).findById(keyIDforRemoval);
                    if ((item == null) || (item.getQuantity() <= 0)) {
                        chr.dropMessage(1, "您的背包里没有 " + key +" 请获取后再继续开启 " + box);
                        c.getSession().write(MaplePacketCreator.enableActions());
                        return;
                    }
                    
                    Item items = MapleInventoryManipulator.addbyId_Gachapon(c, reward, (short) 1);
                    if (items == null) {
                        chr.dropMessage(1, "获取 " + box +" 里的物品失败(所抽取的数据不存在)，请重试一次。");
                        c.getSession().write(MaplePacketCreator.enableActions());
                        return;
                    }
                    
                   // MapleInventoryManipulator.removeById(c, MapleInventoryType.CASH, itemId, 1, true, false);//减少开启的道具
                    MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, keyIDforRemoval, 1, true, false);//减少开启需要的钥匙
                    c.getSession().write(MaplePacketCreator.getShowItemGain(reward, (short) 1, true));//给予抽奖物品
                    //发送世界公告
                    Broadcast.broadcastMessage(MaplePacketCreator.getGachaponMega(c.getPlayer().getName(), new StringBuilder().append(" : 从").append(box).append("中获得{").append(iis.getName(items.getItemId())).append("}！大家一起恭喜他（她）吧！！！！").toString(), items, (byte)2, c.getChannel()));
                    used = true;
                
                } else if (itemId == 5060007) {//水晶天平
                    MapleItemInformationProvider iis = MapleItemInformationProvider.getInstance();
                    
                    int reward = RandomRewards.get水晶天平();//随机取得抽奖物品
                    String box;
                    String key;
                    int keyIDforRemoval = 4170028;//开启需要的物品
                    box = "水晶天平";
                    key = "正义之锤";
                    
                    
                   if ((c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 2) || (c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 2) || (c.getPlayer().getInventory(MapleInventoryType.SETUP).getNumFreeSlot() < 2) || (c.getPlayer().getInventory(MapleInventoryType.CASH).getNumFreeSlot() < 2) || (c.getPlayer().getInventory(MapleInventoryType.ETC).getNumFreeSlot() < 2)) {
                       c.getPlayer().dropMessage(5, "请确保你有足够的背包里的各个栏目有1个空间以上.");
                       return;
                   }
                    
                    Item item = chr.getInventory(MapleInventoryType.ETC).findById(keyIDforRemoval);
                    if ((item == null) || (item.getQuantity() <= 0)) {
                        chr.dropMessage(1, "您的背包里没有 " + key +" 请获取后再继续开启 " + box);
                        c.getSession().write(MaplePacketCreator.enableActions());
                        return;
                    }
                    
                    Item items = MapleInventoryManipulator.addbyId_Gachapon(c, reward, (short) 1);
                    if (items == null) {
                        chr.dropMessage(1, "获取 " + box +" 里的物品失败(所抽取的数据不存在)，请重试一次。");
                        c.getSession().write(MaplePacketCreator.enableActions());
                        return;
                    }
                    
                   // MapleInventoryManipulator.removeById(c, MapleInventoryType.CASH, itemId, 1, true, false);//减少开启的道具
                    MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, keyIDforRemoval, 1, true, false);//减少开启需要的钥匙
                    c.getSession().write(MaplePacketCreator.getShowItemGain(reward, (short) 1, true));//给予抽奖物品
                    //发送世界公告
                    Broadcast.broadcastMessage(MaplePacketCreator.getGachaponMega(c.getPlayer().getName(), new StringBuilder().append(" : 从").append(box).append("中获得{").append(iis.getName(items.getItemId())).append("}！大家一起恭喜他（她）吧！！！！").toString(), items, (byte)2, c.getChannel()));
                    used = true;
                    
                    
                } else if (itemId == 5062000) {
                    if (chr.getLevel() < 50) {
                        chr.dropMessage(1, "使用这个道具需要等级达到50级.");
                    } else {
                        Item item = chr.getInventory(MapleInventoryType.EQUIP).getItem((short) (byte) slea.readInt());
                        if (item != null && chr.getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 1) { 
                            Equip eq = (Equip) item;
                            // int stateRate = chr.getClient().getChannelServer().getStateRate();
                            int stateRate = chr.get潜能数目改变概率();
                            if (eq.getState() >= 17 && eq.getState() != 20) {  
                                //潜能鉴定模式 -- 0
                                // eq.renewPotential(0, chr.isLvkejian() ? 10*stateRate : stateRate);
                                eq.renewPotentials(0, chr.isLvkejian() ? 10*stateRate : stateRate, chr.鉴定概率("B级概率"), chr.鉴定概率("A级概率"), chr.鉴定概率("S级概率"), chr.鉴定概率("SS级概率"), chr.鉴定概率("潜能等级概率2"), chr.鉴定概率("潜能等级概率3"), chr.鉴定概率("潜能等级概率4"), chr.鉴定概率("潜能等级概率5"));
                                c.getSession().write(MaplePacketCreator.scrolledItem(toUse, item, false, true));
                                chr.getMap().broadcastMessage(MaplePacketCreator.魔方光效(false, chr.getId(), true, itemId));
                                chr.forceReAddItem_NoUpdate(item, MapleInventoryType.EQUIP);
                                MapleInventoryManipulator.addById(c, 2430112, (short) 1, new StringBuilder().append("Cube on ").append(FileoutputUtil.CurrentReadable_Date()).toString());
                                used = true;
                            } else {
                                chr.dropMessage(5, "请确认您要重置的道具具有潜能属性.");
                            }
                        } else {
                            chr.getMap().broadcastMessage(MaplePacketCreator.魔方光效(false, chr.getId(), false, itemId));
                        }
                    }

                    
                } else if ((itemId == 5062001) || (itemId == 5062100)) {
                    if (chr.getLevel() < 70) {
                        chr.dropMessage(1, "使用这个道具需要等级达到70级.");
                    } else {
                        Item item = chr.getInventory(MapleInventoryType.EQUIP).getItem((short) (byte) slea.readInt());
                        if ((item != null) && (chr.getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 1)) {
                            Equip eq = (Equip) item;
                            // int stateRate = chr.getClient().getChannelServer().getStateRate();
                            int stateRate = chr.get潜能数目改变概率();
                            if ((eq.getState() >= 17) && (eq.getState() != 20)) {
                                //潜能鉴定模式 -- 1
                                // eq.renewPotential(1, chr.isLvkejian() ? 10*stateRate : stateRate);
                                eq.renewPotentials(1, chr.isLvkejian() ? 10*stateRate : stateRate, chr.鉴定概率("B级概率"), chr.鉴定概率("A级概率"), chr.鉴定概率("S级概率"), chr.鉴定概率("SS级概率"), chr.鉴定概率("潜能等级概率2"), chr.鉴定概率("潜能等级概率3"), chr.鉴定概率("潜能等级概率4"), chr.鉴定概率("潜能等级概率5"));
                                c.getSession().write(MaplePacketCreator.scrolledItem(toUse, item, false, true));
                                chr.getMap().broadcastMessage(MaplePacketCreator.魔方光效(false, chr.getId(), true, itemId));
                                chr.forceReAddItem_NoUpdate(item, MapleInventoryType.EQUIP);
                                MapleInventoryManipulator.addById(c, 2430112, (short) 1, new StringBuilder().append("Cube on ").append(FileoutputUtil.CurrentReadable_Date()).toString());
                                used = true;
                            } else {
                                chr.dropMessage(5, "请确认您要重置的道具具有潜能属性.");
                            }
                        } else {
                            chr.getMap().broadcastMessage(MaplePacketCreator.魔方光效(false, chr.getId(), false, itemId));
                        }
                    }


                } else if (itemId == 5062002) {
                    if (chr.getLevel() < 100) {
                        chr.dropMessage(1, "使用这个道具需要等级达到100级.");
                    } else {
                        Item item = chr.getInventory(MapleInventoryType.EQUIP).getItem((short) (byte) slea.readInt());
                        if ((item != null) && (chr.getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 1)) {
                            Equip eq = (Equip) item;
                            // int stateRate = chr.getClient().getChannelServer().getStateRate();
                            int stateRate = chr.get潜能数目改变概率();
                            if (eq.getState() >= 17) {
                                //潜能鉴定模式 -- 2
                                // eq.renewPotential(2, chr.isLvkejian() ? 10*stateRate : stateRate);
                                eq.renewPotentials(2, chr.isLvkejian() ? 10*stateRate : stateRate, chr.鉴定概率("B级概率"), chr.鉴定概率("A级概率"), chr.鉴定概率("S级概率"), chr.鉴定概率("SS级概率"), chr.鉴定概率("潜能等级概率2"), chr.鉴定概率("潜能等级概率3"), chr.鉴定概率("潜能等级概率4"), chr.鉴定概率("潜能等级概率5"));
                                 c.getSession().write(MaplePacketCreator.scrolledItem(toUse, item, false, true));
                                chr.getMap().broadcastMessage(MaplePacketCreator.魔方光效(false, chr.getId(), true, itemId));
                                chr.forceReAddItem_NoUpdate(item, MapleInventoryType.EQUIP);
                                if (itemId == 5062002) {
                                MapleInventoryManipulator.addById(c, 2430481, (short) 1, new StringBuilder().append("Cube on ").append(FileoutputUtil.CurrentReadable_Date()).toString());
                                } else {
                                    MapleInventoryManipulator.addById(c, 2430759, (short) 1, new StringBuilder().append("Cube on ").append(FileoutputUtil.CurrentReadable_Date()).toString());
                                }
                                used = true;
                            } else {
                                chr.dropMessage(5, "请确认您要重置的道具具有潜能属性.");
                            }
                        } else {
                            chr.getMap().broadcastMessage(MaplePacketCreator.魔方光效(false, chr.getId(), false, itemId));
                        }
                    }
                    

                } else if (itemId == 5062005) {
                    if (chr.getLevel() < 100) {
                        chr.dropMessage(1, "使用这个道具需要等级达到100级.");
                    } else {
                        
                        Item item = chr.getInventory(MapleInventoryType.EQUIP).getItem((short) (byte) slea.readInt());
                        if ((item != null) && (chr.getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 1)) {
                            Equip eq = (Equip) item;
                           // int stateRate = chr.getClient().getChannelServer().getStateRate();
                            int stateRate = chr.get潜能数目改变概率();
                            if (eq.getState() >= 17) {
                                //潜能鉴定模式 -- 3
                               // eq.renewPotential(3, chr.isLvkejian() ? 10*stateRate : stateRate);
                                eq.renewPotentials(3, chr.isLvkejian() ? 10*stateRate : stateRate, chr.鉴定概率("B级概率"), chr.鉴定概率("A级概率"), chr.鉴定概率("S级概率"), chr.鉴定概率("SS级概率"), chr.鉴定概率("潜能等级概率2"), chr.鉴定概率("潜能等级概率3"), chr.鉴定概率("潜能等级概率4"), chr.鉴定概率("潜能等级概率5"));
                                c.getSession().write(MaplePacketCreator.scrolledItem(toUse, item, false, true));
                                chr.getMap().broadcastMessage(MaplePacketCreator.魔方光效(false, chr.getId(), true, itemId));
                                chr.forceReAddItem_NoUpdate(item, MapleInventoryType.EQUIP);
                                if (itemId == 5062002) {
                                MapleInventoryManipulator.addById(c, 2430481, (short) 1, new StringBuilder().append("Cube on ").append(FileoutputUtil.CurrentReadable_Date()).toString());
                                } else {
                                    MapleInventoryManipulator.addById(c, 2430759, (short) 1, new StringBuilder().append("Cube on ").append(FileoutputUtil.CurrentReadable_Date()).toString());
                                }
                                used = true;
                            } else {
                                chr.dropMessage(5, "请确认您要重置的道具具有潜能属性.");
                            }
                        } else {
                            chr.getMap().broadcastMessage(MaplePacketCreator.魔方光效(false, chr.getId(), false, itemId));
                        }
                    }
                    

                } else if (itemId == 5060002) {//孵化器
                    byte inventory2 = (byte) slea.readInt();
                    byte slot2 = (byte) slea.readInt();
                    Item item2 = chr.getInventory(MapleInventoryType.getByType(inventory2)).getItem((short) slot2);
                    if (item2 == null) {
                        return;
                    }
                    chr.dropMessage(1, "暂时无法使用这个道具.");
                } else {
                    chr.dropMessage(1, "暂时无法使用这个道具.");
                }
                break;
            case 507:
                if (chr.isAdmin()) {
                    chr.dropMessage(5, new StringBuilder().append("使用商场喇叭 道具类型: ").append(itemId / 1000 % 10).toString());
                }
                if (chr.getLevel() < 10) {
                    chr.dropMessage(5, "需要等级10级才能使用这个道具.");
                } else if (chr.getMapId() == 180000001) {
                    chr.dropMessage(5, "在这个地方无法使用这个道具.");
                } else if ((!chr.getCheatTracker().canSmega()) && (!chr.isGM())) {
                    chr.dropMessage(5, "你需要等待3秒之后才能使用这个道具.");
                } else if (!c.getChannelServer().getMegaphoneMuteState()) {
                    String medal = "";
                    Item medalItem = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -26);
                    if (medalItem != null) {
                        medal = new StringBuilder().append("<").append(ii.getName(medalItem.getItemId())).append("> ").toString();
                    }
                    int msgType = itemId / 1000 % 10;
                    used = true;
                    switch (msgType) {
                        case 0:
                            chr.getMap().broadcastMessage(MaplePacketCreator.serverNotice(2, new StringBuilder().append(medal).append(chr.getName()).append(" : ").append(slea.readMapleAsciiString()).toString()));
                            break;
                        case 1:
                            c.getChannelServer().broadcastSmegaPacket(MaplePacketCreator.serverNotice(2, new StringBuilder().append(medal).append(chr.getName()).append(" : ").append(slea.readMapleAsciiString()).toString()));
                            break;
                        case 2:
                            Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(3, c.getChannel(), new StringBuilder().append(medal).append(chr.getName()).append(" : ").append(slea.readMapleAsciiString()).toString(), slea.readByte() != 0));
                            break;
                        case 3:
                            Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(17, c.getChannel(), new StringBuilder().append(medal).append(chr.getName()).append(" : ").append(slea.readMapleAsciiString()).toString(), slea.readByte() != 0));
                            break;
                        case 4:
                            Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(18, c.getChannel(), new StringBuilder().append(medal).append(chr.getName()).append(" : ").append(slea.readMapleAsciiString()).toString(), slea.readByte() != 0));
                            break;
                        case 5:
                            int tvType = itemId % 10;
                            boolean megassenger = false;
                            boolean tvEar = false;
                            MapleCharacter victim = null;
                            if (tvType != 1) {
                                if (tvType >= 3) {
                                    megassenger = true;
                                    if (tvType == 3) {
                                        slea.readByte();
                                    }
                                    tvEar = 1 == slea.readByte();
                                } else if (tvType != 2) {
                                    slea.readByte();
                                }
                                if (tvType != 4) {
                                    victim = c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
                                }
                            }
                            List tvMessages = new LinkedList();
                            StringBuilder builder = new StringBuilder();
                            String message = slea.readMapleAsciiString();
                            if (megassenger) {
                                builder.append(" ").append(message);
                            }
                            tvMessages.add(message);

                            if (!MapleTVEffect.isActive()) {
                                if (megassenger) {
                                    String text = builder.toString();
                                    if (text.length() <= 60) {
                                        Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(3, c.getChannel(), new StringBuilder().append(chr.getName()).append(" : ").append(builder.toString()).toString(), tvEar));
                                    }
                                }
                                MapleTVEffect mapleTVEffect = new MapleTVEffect(chr, victim, tvMessages, tvType);
                                mapleTVEffect.stratMapleTV();
                            } else {
                                chr.dropMessage(1, "冒险岛TV正在使用中");
                                used = false;
                            }
                            break;
                        case 6:
                            String djmsg = new StringBuilder().append(medal).append(chr.getName()).append(" : ").append(slea.readMapleAsciiString()).toString();
                            boolean sjEar = slea.readByte() > 0;
                            Item item = null;
                            if (slea.readByte() == 1) {
                                byte invType = (byte) slea.readInt();
                                byte pos = (byte) slea.readInt();
                                item = chr.getInventory(MapleInventoryType.getByType(invType)).getItem((short) pos);
                            }
                            Broadcast.broadcastSmega(MaplePacketCreator.itemMegaphone(djmsg, sjEar, c.getChannel(), item));
                            break;
                        case 7:
                            byte numLines = slea.readByte();
                            if ((numLines < 1) || (numLines > 3)) {
                                return;
                            }
                            List bfMessages = new LinkedList();

                            for (int i = 0; i < numLines; i++) {
                                String bfMsg = slea.readMapleAsciiString();
                                if (bfMsg.length() > 65) {
                                    break;
                                }
                                bfMessages.add(new StringBuilder().append(chr.getName()).append(" : ").append(bfMsg).toString());
                            }
                            boolean bfEar = slea.readByte() > 0;
                            Broadcast.broadcastSmega(MaplePacketCreator.tripleSmega(bfMessages, bfEar, c.getChannel()));
                            break;
                        case 9:
                            Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(itemId == 5079001 ? 15 : 16, c.getChannel(), new StringBuilder().append(medal).append(chr.getName()).append(" : ").append(slea.readMapleAsciiString()).toString(), slea.readByte() != 0));
                        case 8:
                    }
                } else {
                    chr.dropMessage(5, "当前频道禁止使用道具喇叭.");
                }
                break;
            case 508:
                MapleLove love = new MapleLove(chr, chr.getPosition(), chr.getMap().getFootholds().findBelow(chr.getPosition()).getId(), slea.readMapleAsciiString(), itemId);
                chr.getMap().spawnLove(love);
                used = true;
                break;
            case 509: {
                String sendTo = slea.readMapleAsciiString();
                String msg = slea.readMapleAsciiString();
                chr.sendNote(sendTo, msg);
                used = true;
                break;
            }
            case 510:
                chr.getMap().startJukebox(chr.getName(), itemId);
                used = true;
                break;
            case 512: {
                String msg = ii.getMsg(itemId);
                String ourMsg = slea.readMapleAsciiString();
                if (!msg.contains("%s")) {
                    msg = ourMsg;
                } else {
                    msg = msg.replaceFirst("%s", chr.getName());
                    if (!msg.contains("%s")) {
                        msg = ii.getMsg(itemId).replaceFirst("%s", ourMsg);
                    } else {
                        try {
                            msg = msg.replaceFirst("%s", ourMsg);
                        } catch (Exception e) {
                            msg = ii.getMsg(itemId).replaceFirst("%s", ourMsg);
                        }
                    }
                }
                chr.getMap().startMapEffect(msg, itemId);
                int buff = ii.getStateChangeItem(itemId);
                if (buff != 0) {
                    for (MapleCharacter mChar : chr.getMap().getCharactersThreadsafe()) {
                        ii.getItemEffect(buff).applyTo(mChar);
                    }
                }
                used = true;
                break;
            }
            case 515:
                if ((itemId >= 5152100) && (itemId <= 5152107)) {
                    int color = (itemId - 5152100) * 100;

                    if (color >= 0) {
                        changeFace(chr, color);
                        used = true;
                    } else {
                        chr.dropMessage(1, "使用一次性隐形眼镜出现错误.");
                    }
                } else if (itemId == 5155000) {
                    String customData = chr.getElfEar() > 0 ? "sw=0" : "sw=1";
                    String effect = "Effect/BasicEff.img/JobChangedElf";
                    chr.setElfEar(chr.getElfEar() <= 0);
                    chr.updateInfoQuest(7784, customData);
                    c.getSession().write(MaplePacketCreator.showOwnJobChangedElf(effect, 2, 5155000));
                    chr.getMap().broadcastMessage(chr, MaplePacketCreator.showJobChangedElf(chr.getId(), effect, 2, 5155000), false);
                    chr.equipChanged();
                    used = true;
                } else {
                    chr.dropMessage(1, "暂不支持这个道具的使用.");
                }
                break;
            case 517: {
                int uniqueid = (int) slea.readLong();
                MaplePet pet = chr.getPet(0);
                int slo = 0;
                if (pet == null) {
                    chr.dropMessage(1, "你必须把宠物召唤出来才能使用.");
                } else {
                    if (pet.getUniqueId() != uniqueid) {
                        pet = chr.getPet(1);
                        slo = 1;
                        if (pet == null) {
                            break;
                        }
                        if (pet.getUniqueId() != uniqueid) {
                            pet = chr.getPet(2);
                            slo = 2;
                            if ((pet == null)
                                    || (pet.getUniqueId() != uniqueid)) {
                                break;
                            }

                        }

                    }

                    String nName = slea.readMapleAsciiString();
                    for (String z : GameConstants.RESERVED) {
                        if ((pet.getName().indexOf(z) != -1) || (nName.indexOf(z) != -1)) {
                            break;
                        }
                    }
                    if (!MapleCharacterUtil.canChangePetName(nName)) {
                        break;
                    }
                    pet.setName(nName);
                    c.getSession().write(PetPacket.updatePet(pet, chr.getInventory(MapleInventoryType.CASH).getItem((short) (byte) pet.getInventoryPosition()), true));
                    c.getSession().write(MaplePacketCreator.enableActions());
                    chr.getMap().broadcastMessage(MTSCSPacket.changePetName(chr, nName, slo));
                    used = true;
                }
                break;
            }
            case 519: {
                if ((itemId >= 5190000) && (itemId <= 5190010)) {
                    int uniqueid = (int) slea.readLong();
                    MaplePet pet = chr.getPet(0);
                    int slo = 0;
                    if (pet == null) {
                        break;
                    }
                    if (pet.getUniqueId() != uniqueid) {
                        pet = chr.getPet(1);
                        slo = 1;
                        if (pet == null) {
                            break;
                        }
                        if (pet.getUniqueId() != uniqueid) {
                            pet = chr.getPet(2);
                            slo = 2;
                            if ((pet == null)
                                    || (pet.getUniqueId() != uniqueid)) {
                                break;
                            }

                        }

                    }

                    MaplePet.PetFlag petFlag = MaplePet.PetFlag.getByAddId(itemId);
                    if ((petFlag == null) || (petFlag.check(pet.getFlags()))) {
                        break;
                    }
                    pet.setFlags(pet.getFlags() | petFlag.getValue());
                    c.getSession().write(PetPacket.updatePet(pet, chr.getInventory(MapleInventoryType.CASH).getItem((short) (byte) pet.getInventoryPosition()), true));
                    c.getSession().write(MaplePacketCreator.enableActions());
                    c.getSession().write(MTSCSPacket.changePetFlag(uniqueid, true, petFlag.getValue()));
                    used = true;
                } else {
                    if ((itemId < 5191000) || (itemId > 5191004)) {
                        break;
                    }
                    int uniqueid = (int) slea.readLong();
                    MaplePet pet = chr.getPet(0);
                    int slo = 0;
                    if (pet == null) {
                        break;
                    }
                    if (pet.getUniqueId() != uniqueid) {
                        pet = chr.getPet(1);
                        slo = 1;
                        if (pet == null) {
                            break;
                        }
                        if (pet.getUniqueId() != uniqueid) {
                            pet = chr.getPet(2);
                            slo = 2;
                            if ((pet == null)
                                    || (pet.getUniqueId() != uniqueid)) {
                                break;
                            }

                        }

                    }

                    MaplePet.PetFlag petFlag = MaplePet.PetFlag.getByDelId(itemId);
                    if ((petFlag != null) && (petFlag.check(pet.getFlags()))) {
                        pet.setFlags(pet.getFlags() - petFlag.getValue());
                        c.getSession().write(PetPacket.updatePet(pet, chr.getInventory(MapleInventoryType.CASH).getItem((short) (byte) pet.getInventoryPosition()), true));
                        c.getSession().write(MaplePacketCreator.enableActions());
                        c.getSession().write(MTSCSPacket.changePetFlag(uniqueid, false, petFlag.getValue()));
                        used = true;
                    }
                }
                break;
            }
            case 520:
                if ((itemId >= 5200000) && (itemId <= 5200008)) {
                    int mesars = ii.getMeso(itemId);
                    if ((mesars > 0) && (chr.getMeso() < 2147483647 - mesars)) {
                        used = true;
                        if (Math.random() > 0.1D) {
                            int gainmes = Randomizer.nextInt(mesars);
                            chr.gainMeso(gainmes, false);
                            c.getSession().write(MTSCSPacket.sendMesobagSuccess(gainmes));
                        } else {
                            c.getSession().write(MTSCSPacket.sendMesobagFailed());
                        }
                    } else {
                        chr.dropMessage(1, "金币已达到上限无法使用这个道具.");
                    }
                } else {
                    chr.dropMessage(5, "暂时无法使用这个道具.");
                }
                break;
            case 522:
                if (itemId == 5220083) {
                    used = true;
                    for (Entry<Integer, StructFamiliar> f : ii.getFamiliars().entrySet()) {
                        if ((f.getValue().itemid == 2870055) || (f.getValue().itemid == 2871002) || (f.getValue().itemid == 2870235) || (f.getValue().itemid == 2870019)) {
                            MonsterFamiliar mf = chr.getFamiliars().get(f.getKey());
                            if (mf != null) {
                                if (mf.getVitality() >= 3) {
                                    mf.setExpiry(Math.min(System.currentTimeMillis() + 7776000000L, mf.getExpiry() + 2592000000L));
                                } else {
                                    mf.setVitality(mf.getVitality() + 1);
                                    mf.setExpiry(mf.getExpiry() + 2592000000L);
                                }
                            } else {
                                mf = new MonsterFamiliar(chr.getId(), f.getKey(), System.currentTimeMillis() + 2592000000L);
                                chr.getFamiliars().put(f.getKey(), mf);
                            }
                            c.getSession().write(MaplePacketCreator.registerFamiliar(mf));
                        }
                    }
                } else if (itemId == 5220084) {
                    if (chr.getInventory(MapleInventoryType.USE).getNumFreeSlot() < 3) {
                        chr.dropMessage(5, "请确保您有足够的背包空间.");
                    } else {
                        used = true;
                        int[] familiars = new int[3];
                        while (true) {
                            for (int i = 0; i < familiars.length; i++) {
                                if (familiars[i] > 0) {
                                    continue;
                                }
                                for (Entry<Integer, StructFamiliar> f : ii.getFamiliars().entrySet()) {
                                    if ((Randomizer.nextInt(500) == 0) && (((i < 2) && (f.getValue().grade == 0)) || ((i == 2) && (f.getValue().grade != 0)))) {
                                        MapleInventoryManipulator.addById(c, f.getValue().itemid, (short) 1, "Booster Pack");
                                        c.getSession().write(MTSCSPacket.getBoosterFamiliar(chr.getId(), f.getKey(), 0));
                                        familiars[i] = f.getValue().itemid;
                                        break;
                                    }
                                }
                            }
                            if ((familiars[0] > 0) && (familiars[1] > 0) && (familiars[2] > 0)) {
                                break;
                            }
                        }
                        c.getSession().write(MTSCSPacket.getBoosterPack(familiars[0], familiars[1], familiars[2]));
                        c.getSession().write(MTSCSPacket.getBoosterPackClick());
                        c.getSession().write(MTSCSPacket.getBoosterPackReveal());
                    }
                } else {
                    chr.dropMessage(1, "暂时无法使用这个道具.");
                }
                break;
            case 523:
                int itemSearch = slea.readInt();
                List hms = c.getChannelServer().searchMerchant(itemSearch);
                if (hms.size() > 0) {
                    c.getSession().write(MaplePacketCreator.getOwlSearched(itemSearch, hms));
                    used = true;
                } else {
                    chr.dropMessage(1, "没有找到这个道具.");
                }
                break;
            case 524:
                MaplePet pet = chr.getPet(0);
                if (pet == null) {
                    break;
                }
                if (!pet.canConsume(itemId)) {
                    pet = chr.getPet(1);
                    if (pet == null) {
                        break;
                    }
                    if (!pet.canConsume(itemId)) {
                        pet = chr.getPet(2);
                        if ((pet == null)
                                || (!pet.canConsume(itemId))) {
                            break;
                        }

                    }

                }

                byte petindex = chr.getPetIndex(pet);
                pet.setFullness(100);
                if (pet.getCloseness() < 30000) {
                    if (pet.getCloseness() + 100 * c.getChannelServer().getTraitRate() > 30000) {
                        pet.setCloseness(30000);
                    } else {
                        pet.setCloseness(pet.getCloseness() + 100 * c.getChannelServer().getTraitRate());
                    }
                    if (pet.getCloseness() >= GameConstants.getClosenessNeededForLevel(pet.getLevel() + 1)) {
                        pet.setLevel(pet.getLevel() + 1);
                        c.getSession().write(PetPacket.showOwnPetLevelUp(chr.getPetIndex(pet)));
                        chr.getMap().broadcastMessage(PetPacket.showPetLevelUp(chr, petindex));
                    }
                }
                c.getSession().write(PetPacket.updatePet(pet, chr.getInventory(MapleInventoryType.CASH).getItem(pet.getInventoryPosition()), true));
                chr.getMap().broadcastMessage(chr, PetPacket.commandResponse(chr.getId(), (byte) 1, petindex, true, true), true);
                used = true;
                break;
            case 528:
                Rectangle bounds = new Rectangle((int) chr.getPosition().getX(), (int) chr.getPosition().getY(), 1, 1);
                MapleMist mist = new MapleMist(bounds, chr);
                chr.getMap().spawnMist(mist, 10000, true);
                c.getSession().write(MaplePacketCreator.enableActions());
                used = true;
                break;
            case 537:
                for (MapleEventType t : MapleEventType.values()) {
                    MapleEvent e = ChannelServer.getInstance(c.getChannel()).getEvent(t);
                    if (e.isRunning()) {
                        for (int i : e.getType().mapids) {
                            if (chr.getMapId() == i) {
                                chr.dropMessage(5, "当前地图无法使用此道具.");
                                c.getSession().write(MaplePacketCreator.enableActions());
                                return;
                            }
                        }
                    }
                }
                chr.setChalkboard(slea.readMapleAsciiString());
                break;
            case 539:
                if (chr.getLevel() < 10) {
                    chr.dropMessage(5, "需要等级10级才能使用这个道具.");
                } else if (chr.getMapId() == 180000001) {
                    chr.dropMessage(5, "当前地图无法使用这个道具.");
                } else if (!chr.getCheatTracker().canAvatarSmega()) {
                    chr.dropMessage(5, "你需要等待6秒之后才能使用这个道具.");
                } else if (!c.getChannelServer().getMegaphoneMuteState()) {
                    String text = slea.readMapleAsciiString();
                    if (text.length() > 55) {
                        break;
                    }
                    boolean ear = slea.readByte() != 0;
                    Broadcast.broadcastSmega(MaplePacketCreator.getAvatarMega(chr, c.getChannel(), itemId, text, ear));
                    used = true;
                } else {
                    chr.dropMessage(5, "当前频道禁止使用情景喇叭.");
                }
                break;
            case 545:
                if ((itemId == 5450005) || (itemId == 5450001)) {
                    chr.setConversation(4);
                    chr.getStorage().sendStorage(c, 1022005);
                } else {
                    for (int i : GameConstants.blockedMaps) {
                        if (chr.getMapId() == i) {
                            chr.dropMessage(5, "当前地图无法使用此道具.");
                            c.getSession().write(MaplePacketCreator.enableActions());
                            return;
                        }
                    }
                    if (chr.getLevel() < 10) {
                        chr.dropMessage(5, "只有等级达到10级才可以使用此道具.");
                    } else if ((chr.hasBlockedInventory()) || (chr.getMap().getSquadByMap() != null) || (chr.getEventInstance() != null) || (chr.getMap().getEMByMap() != null) || (chr.getMapId() >= 990000000)) {
                        chr.dropMessage(5, "当前地图无法使用此道具.");
                    } else if (((chr.getMapId() >= 680000210) && (chr.getMapId() <= 680000502)) || ((chr.getMapId() / 1000 == 980000) && (chr.getMapId() != 980000000)) || (chr.getMapId() / 100 == 1030008) || (chr.getMapId() / 100 == 922010) || (chr.getMapId() / 10 == 13003000)) {
                        chr.dropMessage(5, "当前地图无法使用此道具.");
                    } else {
                        MapleShopFactory.getInstance().getShop(9090000).sendShop(c);
                    }
                }
                break;
            case 550:
                if (itemId == 5500003) {
                    chr.dropMessage(1, "暂时无法使用这个道具.");
                } else if ((itemId == 5501001) || (itemId == 5501002)) {
                    Skill skil = SkillFactory.getSkill(slea.readInt());
                    if ((skil == null) || (skil.getId() / 10000 != 8000) || (chr.getSkillLevel(skil) <= 0) || (!skil.isTimeLimited()) || (GameConstants.getMountItem(skil.getId(), chr) <= 0)) {
                        break;
                    }
                    long toAdd = (itemId == 5501001 ? 30 : 60) * 24 * 60 * 60 * 1000L;
                    long expire = chr.getSkillExpiry(skil);
                    if ((expire < System.currentTimeMillis()) || (expire + toAdd >= System.currentTimeMillis() + 31536000000L)) {
                        break;
                    }
                    chr.changeSkillLevel(skil, chr.getSkillLevel(skil), chr.getMasterLevel(skil), expire + toAdd);
                    used = true;
                } else if ((itemId >= 5500000) && (itemId <= 5500006)) {
                    Short slots = Short.valueOf(slea.readShort());
                    if (slots.shortValue() == 0) {
                        chr.dropMessage(1, "请该道具点在你需要延长时间的道具上.");
                    } else {
                        Item item = chr.getInventory(MapleInventoryType.EQUIPPED).getItem(slots.shortValue());
                        long days = 0L;
                        if (itemId == 5500000) {
                            days = 1L;
                        } else if (itemId == 5500001) {
                            days = 7L;
                        } else if (itemId == 5500002) {
                            days = 20L;
                        } else if (itemId == 5500004) {
                            days = 30L;
                        } else if (itemId == 5500005) {
                            days = 50L;
                        } else if (itemId == 5500006) {
                            days = 99L;
                        }
                        if ((item != null) && (!GameConstants.isAccessory(item.getItemId())) && (item.getExpiration() > -1L) && (!ii.isCash(item.getItemId())) && (System.currentTimeMillis() + 8640000000L > item.getExpiration() + days * 24L * 60L * 60L * 1000L)) {
                            boolean change = true;
                            for (String z : GameConstants.RESERVED) {
                                if ((chr.getName().indexOf(z) != -1) || (item.getOwner().indexOf(z) != -1)) {
                                    change = false;
                                }
                            }
                            if ((change) && (days > 0L)) {
                                item.setExpiration(item.getExpiration() + days * 24L * 60L * 60L * 1000L);
                                chr.forceReAddItem(item, MapleInventoryType.EQUIPPED);
                                used = true;
                                break;
                            }
                            chr.dropMessage(1, "无法使用在这个道具上.");
                        } else {
                            chr.dropMessage(1, "使用道具出现错误.");
                        }
                    }
                } else {
                    chr.dropMessage(1, "暂时无法使用这个道具.");
                }
                break;
            case 552:
                if (itemId == 5521000) {
                    MapleInventoryType type = MapleInventoryType.getByType((byte) slea.readInt());
                    Item item = chr.getInventory(type).getItem((short) (byte) slea.readInt());
                    if ((item != null) && (!ItemFlag.KARMA_ACC.check(item.getFlag())) && (!ItemFlag.KARMA_ACC_USE.check(item.getFlag()))
                            && (ii.isShareTagEnabled(item.getItemId()))) {
                        short flag = item.getFlag();
                        if (ItemFlag.UNTRADEABLE.check(flag)) {
                            flag = (short) (flag - ItemFlag.UNTRADEABLE.getValue());
                        } else if (type == MapleInventoryType.EQUIP) {
                            flag = (short) (flag | ItemFlag.KARMA_ACC.getValue());
                        } else {
                            flag = (short) (flag | ItemFlag.KARMA_ACC_USE.getValue());
                        }
                        item.setFlag(flag);
                        chr.forceReAddItem_NoUpdate(item, type);
                        c.getSession().write(MaplePacketCreator.updateSpecialItemUse(item, type.getType(), item.getPosition(), true, chr));
                        used = true;
                    }
                } else {
                    if ((itemId != 5520000) && (itemId != 5520001)) {
                        break;
                    }
                    MapleInventoryType type = MapleInventoryType.getByType((byte) slea.readInt());
                    Item item = chr.getInventory(type).getItem((short) (byte) slea.readInt());
                    if ((item != null) && (!ItemFlag.KARMA_EQ.check(item.getFlag())) && (!ItemFlag.KARMA_USE.check(item.getFlag())) && (((itemId == 5520000) && (ii.isKarmaEnabled(item.getItemId()))) || ((itemId == 5520001) && (ii.isPKarmaEnabled(item.getItemId()))))) {
                        short flag = item.getFlag();
                        if (ItemFlag.UNTRADEABLE.check(flag)) {
                            flag = (short) (flag - ItemFlag.UNTRADEABLE.getValue());
                        } else if (type == MapleInventoryType.EQUIP) {
                            flag = (short) (flag | ItemFlag.KARMA_EQ.getValue());
                        } else {
                            flag = (short) (flag | ItemFlag.KARMA_USE.getValue());
                        }
                        item.setFlag(flag);
                        chr.forceReAddItem_NoUpdate(item, type);
                        c.getSession().write(MaplePacketCreator.updateSpecialItemUse(item, type.getType(), item.getPosition(), true, chr));
                        used = true;
                    }
                }

                break;
            case 557: {
                slea.readInt();
                Equip item = (Equip) chr.getInventory(MapleInventoryType.EQUIP).getItem((short) (byte) slea.readInt());
                if (item == null) {
                    break;
                }
                if ((GameConstants.canHammer(item.getItemId())) && (ii.getSlots(item.getItemId()) > 0) && (item.getViciousHammer() < 2)) {
                    item.setViciousHammer((byte) (item.getViciousHammer() + 1));
                    item.setUpgradeSlots((byte) (item.getUpgradeSlots() + 1));
                    chr.forceReAddItem(item, MapleInventoryType.EQUIP);

                    cc = true;
                    used = true;
                } else {
                    chr.dropMessage(5, "无法使用在这个道具上.");

                    cc = true;
                }
                break;
            }
            case 561: {
                slea.readInt();
                short dst = (short) slea.readInt();
                slea.readInt();
                short src = (short) slea.readInt();
                used = InventoryHandler.UseUpgradeScroll(src, dst, (short) 2, c, chr, itemId, true);
                cc = used;
                break;
            }
            case 562: {
                if (!InventoryHandler.UseSkillBook(slot, itemId, c, chr)) {
                    break;
                }
                chr.gainSP(1);
                break;
            }
            case 570: {
                slea.skip(8);
                if (chr.getAndroid() == null) {
                    break;
                }
                String nName = slea.readMapleAsciiString();
                for (String z : GameConstants.RESERVED) {
                    if ((chr.getAndroid().getName().indexOf(z) != -1) || (nName.indexOf(z) != -1)) {
                        break;
                    }
                }
                if (!MapleCharacterUtil.canChangePetName(nName)) {
                    break;
                }
                chr.getAndroid().setName(nName);
                chr.setAndroid(chr.getAndroid());
                used = true;
                break;
            }
            case 575: {
                if ((itemId == 5750000) || (itemId == 5750002)) {
                    if (chr.getLevel() < 10) {
                        chr.dropMessage(1, "使用这个道具需要等级达到10级.");
                    } else {
                        Item item = chr.getInventory(MapleInventoryType.SETUP).getItem((short) (byte) slea.readInt());
                        if ((item != null) && (chr.getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 1) && (chr.getInventory(MapleInventoryType.SETUP).getNumFreeSlot() >= 1)) {
                            int grade = GameConstants.getNebuliteGrade(item.getItemId());
                            if ((grade != -1) && (grade < 4)) {
                                int rank = Randomizer.nextInt(100) < 7 ? grade : grade != 3 ? grade + 1 : Randomizer.nextInt(100) < 2 ? grade + 1 : grade;
                                List pots = new LinkedList(ii.getAllSocketInfo(rank).values());
                                int newId = 0;
                                while (newId == 0) {
                                    StructItemOption pot = (StructItemOption) pots.get(Randomizer.nextInt(pots.size()));
                                    if (pot != null) {
                                        newId = pot.opID;
                                    }
                                }
                                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.SETUP, item.getPosition(), (short) 1, false);
                                MapleInventoryManipulator.addById(c, newId, (short) 1, new StringBuilder().append("Upgraded from alien cube on ").append(FileoutputUtil.CurrentReadable_Date()).toString());
                                MapleInventoryManipulator.addById(c, 2430760, (short) 1, new StringBuilder().append("Alien Cube on ").append(FileoutputUtil.CurrentReadable_Date()).toString());
                                used = true;
                            } else {
                                chr.dropMessage(5, "重置的道具失败.");
                                break;
                            }
                        } else {
                            chr.dropMessage(5, "您的背包空间不足.");
                            break;
                        }
                    }
                } else {
                    if (itemId != 5750001) {
                        break;
                    }
                    if (chr.getLevel() < 10) {
                        chr.dropMessage(1, "使用这个道具需要等级达到10级.");
                    } else {
                        Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((short) (byte) slea.readInt());
                        if (item != null) {
                            Equip eq = (Equip) item;
                            int sockItem = eq.getSocket1();
                            if ((sockItem > 0) && (ii.itemExists(sockItem))) {
                                eq.setSocket1(0);
                                c.getSession().write(MaplePacketCreator.scrolledItem(toUse, item, false, true));
                                c.getPlayer().forceReAddItem_NoUpdate(item, MapleInventoryType.EQUIP);
                                MapleInventoryManipulator.addById(c, sockItem, (short) 1, new StringBuilder().append("摘取星岩: ").append(FileoutputUtil.CurrentReadable_Date()).toString());
                                MapleInventoryManipulator.addById(c, 2430691, (short) 1, new StringBuilder().append("Alien Cube on ").append(FileoutputUtil.CurrentReadable_Date()).toString());
                                used = true;
                            } else {
                                chr.dropMessage(5, "该道具不具有星岩属性.");
                                break;
                            }
                        } else {
                            chr.dropMessage(5, "This item's nebulite cannot be removed.");
                            break;
                        }
                    }
                }
                break;
            }
            case 511:
            case 513:
            case 514:
            case 516:
            case 518:
            case 521:
            case 525:
            case 526:
            case 527:
            case 529:
            case 530:
            case 531:
            case 532:
            case 533:
            case 534:
            case 535:
            case 536:
            case 538:
            case 540:
            case 541:
            case 542:
            case 543:
            case 544:
            case 546:
            case 547:
            case 548:
            case 549:
            case 551:
            case 553:
            case 554:
            case 555:
            case 556:
            case 558:
            case 559:
            case 560:
            case 563:
            case 564:
            case 565:
            case 566:
            case 567:
            case 568:
            case 569:
            case 571:
            case 572:
            case 573:
            case 574:
            default:
                System.out.println(new StringBuilder().append("Unhandled CS item : ").append(itemId).toString());
                System.out.println(slea.toString(true));
        }

        if (used) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.CASH, (short) slot, (short) 1, false, true);
        }
        c.getSession().write(MaplePacketCreator.enableActions());
        if (cc) {
            if ((!chr.isAlive()) || (chr.getEventInstance() != null) || (FieldLimitType.ChannelSwitch.check(chr.getMap().getFieldLimit()))) {
                chr.dropMessage(1, "刷新人物数据失败.");
                return;
            }
            chr.dropMessage(5, "正在刷新人数据.请等待...");
            chr.fakeRelog();
            if (chr.getScrolledPosition() != 0) {
                c.getSession().write(MaplePacketCreator.pamSongUI());
            }
        }
    }

    private static boolean getIncubatedItems(MapleClient c, int itemId) {
        if ((c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 2) || (c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 2) || (c.getPlayer().getInventory(MapleInventoryType.SETUP).getNumFreeSlot() < 2)) {
            c.getPlayer().dropMessage(5, "请确保你有足够的背包空间.");
            return false;
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        int id1 = RandomRewards.getPeanutReward();
        int id2 = RandomRewards.getPeanutReward();
        while (!ii.itemExists(id1)) {
            id1 = RandomRewards.getPeanutReward();
        }
        while (!ii.itemExists(id2)) {
            id2 = RandomRewards.getPeanutReward();
        }
        c.getSession().write(MaplePacketCreator.getPeanutResult(id1, (short) 1, id2, (short) 1, itemId));//给予抽奖物品 和消耗道具
        MapleInventoryManipulator.addById(c, id1, (short) 1, new StringBuilder().append(ii.getName(itemId)).append(" 在 ").append(FileoutputUtil.CurrentReadable_Date()).toString());
        MapleInventoryManipulator.addById(c, id2, (short) 1, new StringBuilder().append(ii.getName(itemId)).append(" 在 ").append(FileoutputUtil.CurrentReadable_Date()).toString());
        
             //   Broadcast.broadcastMessage(MaplePacketCreator.getGachaponMega(c.getPlayer().getName(), new StringBuilder().append(" : 从").append("花生机").append("中获得{").append(ii.getName(id2.getItemId())).append("}！大家一起恭喜他（她）吧！！！！").toString(), item, rareness, c.getChannel()));
        return true;
    }

    private static void changeFace(MapleCharacter player, int color) {
        if (player.getFace() % 1000 < 100) {
            player.setFace(player.getFace() + color);
        } else if ((player.getFace() % 1000 >= 100) && (player.getFace() % 1000 < 200)) {
            player.setFace(player.getFace() - 100 + color);
        } else if ((player.getFace() % 1000 >= 200) && (player.getFace() % 1000 < 300)) {
            player.setFace(player.getFace() - 200 + color);
        } else if ((player.getFace() % 1000 >= 300) && (player.getFace() % 1000 < 400)) {
            player.setFace(player.getFace() - 300 + color);
        } else if ((player.getFace() % 1000 >= 400) && (player.getFace() % 1000 < 500)) {
            player.setFace(player.getFace() - 400 + color);
        } else if ((player.getFace() % 1000 >= 500) && (player.getFace() % 1000 < 600)) {
            player.setFace(player.getFace() - 500 + color);
        } else if ((player.getFace() % 1000 >= 600) && (player.getFace() % 1000 < 700)) {
            player.setFace(player.getFace() - 600 + color);
        } else if ((player.getFace() % 1000 >= 700) && (player.getFace() % 1000 < 800)) {
            player.setFace(player.getFace() - 700 + color);
        }
        player.updateSingleStat(MapleStat.脸型, player.getFace());
        player.equipChanged();
    }
}