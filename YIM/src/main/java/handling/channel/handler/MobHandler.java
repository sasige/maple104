package handling.channel.handler;

import client.*;
import client.anticheat.CheatingOffense;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.status.MonsterStatus;
import client.status.MonsterStatusEffect;
import constants.GameConstants;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.Randomizer;
import server.StructFamiliar;
import server.life.*;
import server.maps.MapleMap;
import server.maps.MapleNodes.MapleNodeInfo;
import server.movement.AbsoluteLifeMovement;
import server.movement.LifeMovement;
import server.movement.LifeMovementFragment;
import tools.FileoutputUtil;
import tools.MaplePacketCreator;
import tools.Pair;
import tools.Triple;
import tools.data.LittleEndianAccessor;
import tools.packet.MobPacket;

public class MobHandler {

    public static void MoveMonster(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        if ((chr == null) || (chr.getMap() == null)) {
            return;
        }
        int oid = slea.readInt();
        MapleMonster monster = chr.getMap().getMonsterByOid(oid);
        if (monster == null) {
            return;
        }
        if (monster.getLinkCID() > 0) {
            return;
        }
        slea.skip(1);
        short moveid = slea.readShort();
        boolean useSkill = (slea.readByte() & 0xFF) > 0;
        byte action = slea.readByte();
        int skillId = slea.readByte() & 0xFF;
        int skillLevel = slea.readByte();
        short delay = slea.readShort();

        int realskill = 0;
        int level = 0;
        if (useSkill) {
            byte size = monster.getNoSkills();
            boolean used = false;
            if (size > 0) {
                Pair skillToUse = (Pair) monster.getSkills().get((byte) Randomizer.nextInt(size));
                realskill = ((Integer) skillToUse.getLeft()).intValue();
                level = ((Integer) skillToUse.getRight()).intValue();

                MobSkill mobSkill = MobSkillFactory.getMobSkill(realskill, level);
                if ((mobSkill != null) && (!mobSkill.checkCurrentBuff(chr, monster))) {
                    long now = System.currentTimeMillis();
                    long ls = monster.getLastSkillUsed(realskill);
                    if ((ls == 0L) || ((now - ls > mobSkill.getCoolTime()) && (!mobSkill.onlyOnce()))) {
                        monster.setLastSkillUsed(realskill, now, mobSkill.getCoolTime());
                        int reqHp = (int) ((float) monster.getHp() / (float) monster.getMobMaxHp() * 100.0F);
                        if (reqHp <= mobSkill.getHP()) {
                            used = true;
                            mobSkill.applyEffect(chr, monster, true);
                        }
                    }
                }
            }
            if (!used) {
                realskill = 0;
                level = 0;
            }

        }

        byte aa = slea.readByte();
        if (aa > 0) {
            for (int i = 0; i < aa; i++) {
                slea.skip(4);
            }
        }
        byte gg = slea.readByte();

        if (gg > 0) {
            for (int i = 0; i < gg; i++) {
                slea.skip(2);
            }
        }
        slea.skip(1);
        boolean skipped = (slea.readInt() != 0) && (gg > 0);
        slea.skip(8);
        slea.skip(5);
        slea.skip(4);
        slea.skip(4);
        if ((monster.getId() == 9300281) && (skipped)) {
            if (slea.readByte() > 10) {
                slea.skip(8);
            } else {
                slea.seek(slea.getPosition() - 1L);
            }
        }
        Point startPos = monster.getPosition();
        List<LifeMovementFragment> res;
        try {
            res = MovementParse.parseMovement(slea, 2);
        } catch (ArrayIndexOutOfBoundsException e) {
            FileoutputUtil.outputFileError("log\\Movement.log", e);
            FileoutputUtil.log("log\\Movement.log", "怪物ID " + monster.getId() + ", AIOBE Type2:\r\n" + slea.toString(true));
            return;
        }
        if ((res != null) && (chr != null) && (res.size() > 0)) {
            MapleMap map = chr.getMap();
            for (LifeMovementFragment move : res) {
                if ((move instanceof AbsoluteLifeMovement)) {
                    Point endPos = ((LifeMovement) move).getPosition();
                    if ((endPos.x < map.getLeft() - 250) || (endPos.y < map.getTop() - 250) || (endPos.x > map.getRight() + 250) || (endPos.y > map.getBottom() + 250)) {
                        chr.getCheatTracker().checkMoveMonster(endPos);
                        return;
                    }
                }
            }
            slea.skip(1);
            slea.skip(4);
            slea.skip(4);
            c.getSession().write(MobPacket.moveMonsterResponse(monster.getObjectId(), moveid, monster.getMp(), monster.isControllerHasAggro(), realskill, level));
            if ((slea.available() < 9L) || (slea.available() > 30L)) {
                FileoutputUtil.log("log\\Packet_Except.log", "slea.available != 30 (movement parsing error)\n" + slea.toString(true));

                return;
            }
            MovementParse.updatePosition(res, monster, -1);
            Point endPos = monster.getTruePosition();
            map.moveMonster(monster, endPos);
            map.broadcastMessage(chr, MobPacket.moveMonster(useSkill, action, skillId, skillLevel, delay, monster.getObjectId(), startPos, res), endPos);
            chr.getCheatTracker().checkMoveMonster(endPos);
        }
    }

    public static void FriendlyDamage(LittleEndianAccessor slea, MapleCharacter chr) {
        MapleMap map = chr.getMap();
        if (map == null) {
            return;
        }
        MapleMonster mobfrom = map.getMonsterByOid(slea.readInt());
        slea.skip(4);
        MapleMonster mobto = map.getMonsterByOid(slea.readInt());

        if ((mobfrom != null) && (mobto != null) && (mobto.getStats().isFriendly())) {
            int damage = mobto.getStats().getLevel() * Randomizer.nextInt(mobto.getStats().getLevel()) / 2;
            mobto.damage(chr, damage, true);
            checkShammos(chr, mobto, map);
        }
    }

    public static void MobBomb(LittleEndianAccessor slea, MapleCharacter chr) {
        MapleMap map = chr.getMap();
        if (map == null) {
            return;
        }
        MapleMonster mobfrom = map.getMonsterByOid(slea.readInt());
        slea.skip(4);
        slea.readInt();

        if ((mobfrom != null) && (mobfrom.getBuff(MonsterStatus.怪物炸弹) != null));
    }

    public static void checkShammos(MapleCharacter chr, MapleMonster mobto, MapleMap map) {
        MapleMap mapp;
        if ((!mobto.isAlive()) && (mobto.getStats().isEscort())) {
            for (MapleCharacter chrz : map.getCharactersThreadsafe()) {
                if ((chrz.getParty() != null) && (chrz.getParty().getLeader().getId() == chrz.getId())) {
                    if (!chrz.haveItem(2022698)) {
                        break;
                    }
                    MapleInventoryManipulator.removeById(chrz.getClient(), MapleInventoryType.USE, 2022698, 1, false, true);
                    mobto.heal((int) mobto.getMobMaxHp(), mobto.getMobMaxMp(), true);
                    return;
                }

            }

            map.broadcastMessage(MaplePacketCreator.serverNotice(6, "Your party has failed to protect the monster."));
            mapp = chr.getMap().getForcedReturnMap();
            for (MapleCharacter chrz : map.getCharactersThreadsafe()) {
                chrz.changeMap(mapp, mapp.getPortal(0));
            }
        } else if ((mobto.getStats().isEscort()) && (mobto.getEventInstance() != null)) {
            mobto.getEventInstance().setProperty("HP", String.valueOf(mobto.getHp()));
        }
    }

    public static void MonsterBomb(int oid, MapleCharacter chr) {
        MapleMonster monster = chr.getMap().getMonsterByOid(oid);

        if ((monster == null) || (!chr.isAlive()) || (chr.isHidden()) || (monster.getLinkCID() > 0)) {
            return;
        }
        byte selfd = monster.getStats().getSelfD();
        if (selfd != -1) {
            chr.getMap().killMonster(monster, chr, false, false, selfd);
        }
    }

    public static void AutoAggro(int monsteroid, MapleCharacter chr) {
        if ((chr == null) || (chr.getMap() == null) || (chr.isHidden())) {
            return;
        }
        MapleMonster monster = chr.getMap().getMonsterByOid(monsteroid);

        if ((monster != null) && (chr.getTruePosition().distanceSq(monster.getTruePosition()) < 200000.0D) && (monster.getLinkCID() <= 0)) {
            if (monster.getController() != null) {
                if (chr.getMap().getCharacterById(monster.getController().getId()) == null) {
                    monster.switchController(chr, true);
                } else {
                    monster.switchController(monster.getController(), true);
                }
            } else {
                monster.switchController(chr, true);
            }
        }
    }

    public static void HypnotizeDmg(LittleEndianAccessor slea, MapleCharacter chr) {
        MapleMonster mob_from = chr.getMap().getMonsterByOid(slea.readInt());
        slea.skip(4);
        int to = slea.readInt();
        slea.skip(1);
        int damage = slea.readInt();

        MapleMonster mob_to = chr.getMap().getMonsterByOid(to);

        if ((mob_from != null) && (mob_to != null) && (mob_to.getStats().isFriendly())) {
            if (damage > 30000) {
                return;
            }
            mob_to.damage(chr, damage, true);
            checkShammos(chr, mob_to, chr.getMap());
        }
    }

    public static void DisplayNode(LittleEndianAccessor slea, MapleCharacter chr) {
        MapleMonster mob_from = chr.getMap().getMonsterByOid(slea.readInt());
        if (mob_from != null) {
            chr.getClient().getSession().write(MaplePacketCreator.getNodeProperties(mob_from, chr.getMap()));
        }
    }

    public static void MobNode(LittleEndianAccessor slea, MapleCharacter chr) {
        MapleMonster mob_from = chr.getMap().getMonsterByOid(slea.readInt());
        int newNode = slea.readInt();
        int nodeSize = chr.getMap().getNodes().size();
        if ((mob_from != null) && (nodeSize > 0)) {
            MapleNodeInfo mni = chr.getMap().getNode(newNode);
            if (mni == null) {
                return;
            }
            if (mni.attr == 2) {
                switch (chr.getMapId() / 100) {
                    case 9211200:
                    case 9211201:
                    case 9211202:
                    case 9211203:
                    case 9211204:
                        chr.getMap().talkMonster("Please escort me carefully.", 5120035, mob_from.getObjectId());
                        break;
                    case 9320001:
                    case 9320002:
                    case 9320003:
                        chr.getMap().talkMonster("Please escort me carefully.", 5120051, mob_from.getObjectId());
                }
            }

            mob_from.setLastNode(newNode);
            if (chr.getMap().isLastNode(newNode)) {
                switch (chr.getMapId() / 100) {
                    case 9211200:
                    case 9211201:
                    case 9211202:
                    case 9211203:
                    case 9211204:
                    case 9320001:
                    case 9320002:
                    case 9320003:
                        chr.getMap().broadcastMessage(MaplePacketCreator.serverNotice(5, "进入下一个阶段。"));
                        chr.getMap().removeMonster(mob_from);
                }
            }
        }
    }

    public static void RenameFamiliar(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        MonsterFamiliar mf = (MonsterFamiliar) c.getPlayer().getFamiliars().get(Integer.valueOf(slea.readInt()));
        String newName = slea.readMapleAsciiString();
        if ((mf != null) && (mf.getName().equals(mf.getOriginalName())) && (MapleCharacterUtil.isEligibleCharName(newName, false))) {
            mf.setName(newName);
        } else {
            chr.dropMessage(1, "Name was not eligible.");
        }
        c.getSession().write(MaplePacketCreator.enableActions());
    }

    public static void SpawnFamiliar(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        c.getPlayer().updateTick(slea.readInt());
        int mId = slea.readInt();
        c.getSession().write(MaplePacketCreator.enableActions());
        c.getPlayer().removeFamiliar();
        if ((c.getPlayer().getFamiliars().containsKey(Integer.valueOf(mId))) && (slea.readByte() > 0)) {
            MonsterFamiliar mf = (MonsterFamiliar) c.getPlayer().getFamiliars().get(Integer.valueOf(mId));
            if (mf.getFatigue() > 0) {
                c.getPlayer().dropMessage(1, "Please wait " + mf.getFatigue() + " seconds to summon it.");
            } else {
                c.getPlayer().spawnFamiliar(mf);
            }
        }
    }

    public static void MoveFamiliar(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        slea.skip(13);
        List res = MovementParse.parseMovement(slea, 6);
        if ((chr != null) && (chr.getSummonedFamiliar() != null) && (res.size() > 0)) {
            Point pos = chr.getSummonedFamiliar().getPosition();
            MovementParse.updatePosition(res, chr.getSummonedFamiliar(), 0);
            chr.getSummonedFamiliar().updatePosition(res);
            if (!chr.isHidden()) {
                chr.getMap().broadcastMessage(chr, MaplePacketCreator.moveFamiliar(chr.getId(), pos, res), chr.getTruePosition());
            }
        }
    }

    public static void AttackFamiliar(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        if (chr.getSummonedFamiliar() == null) {
            return;
        }
        slea.skip(6);
        int skillid = slea.readInt();
        SkillFactory.FamiliarEntry f = SkillFactory.getFamiliar(skillid);
        if (f == null) {
            return;
        }
        byte unk = slea.readByte();
        byte size = slea.readByte();
        List<Triple<Integer, Integer, List<Integer>>> attackPair = new ArrayList<Triple<Integer, Integer, List<Integer>>>(size);
        for (int i = 0; i < size; i++) {
            int oid = slea.readInt();
            int type = slea.readInt();
            slea.skip(10);
            byte si = slea.readByte();
            List<Integer> attack = new ArrayList<Integer>(si);
            for (int x = 0; x < si; x++) {
                attack.add(slea.readInt());
            }
            attackPair.add(new Triple(oid, type, attack));
        }
        if ((attackPair.isEmpty()) || (!chr.getCheatTracker().checkFamiliarAttack(chr)) || (attackPair.size() > f.targetCount)) {
            return;
        }
        MapleMonsterStats oStats = chr.getSummonedFamiliar().getOriginalStats();
        chr.getMap().broadcastMessage(chr, MaplePacketCreator.familiarAttack(chr.getId(), unk, attackPair), chr.getTruePosition());
        for (Triple<Integer, Integer, List<Integer>> attack : attackPair) {
            MapleMonster mons = chr.getMap().getMonsterByOid(attack.left);
            if ((mons == null) || (!mons.isAlive()) || (mons.getStats().isFriendly()) || (mons.getLinkCID() > 0) || (attack.right.size() > f.attackCount)) {
                continue;
            }
            if ((chr.getTruePosition().distanceSq(mons.getTruePosition()) > 640000.0D) || (chr.getSummonedFamiliar().getTruePosition().distanceSq(mons.getTruePosition()) > GameConstants.getAttackRange(f.lt, f.rb))) {
                chr.getCheatTracker().registerOffense(CheatingOffense.ATTACK_FARAWAY_MONSTER_SUMMON);
            }
            for (int damage : attack.right) {
                if (damage <= oStats.getPhysicalAttack() * 4) {
                    mons.damage(chr, damage, true);
                }
            }
            if ((f.makeChanceResult()) && (mons.isAlive())) {
                for (MonsterStatus s : f.status) {
                    mons.applyStatus(chr, new MonsterStatusEffect(s, Integer.valueOf(f.speed), MonsterStatusEffect.genericSkill(s), null, false), false, f.time * 1000, false, null);
                }
                if (f.knockback) {
                    mons.switchController(chr, true);
                }
            }
        }
        chr.getSummonedFamiliar().addFatigue(chr, attackPair.size());
    }

    public static void TouchFamiliar(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        if (chr.getSummonedFamiliar() == null) {
            return;
        }
        slea.skip(6);
        byte unk = slea.readByte();

        MapleMonster target = chr.getMap().getMonsterByOid(slea.readInt());
        if (target == null) {
            return;
        }
        int type = slea.readInt();
        slea.skip(4);
        int damage = slea.readInt();
        int maxDamage = chr.getSummonedFamiliar().getOriginalStats().getPhysicalAttack() * 5;
        if (damage < maxDamage) {
            damage = maxDamage;
        }
        if ((!target.getStats().isFriendly()) && (chr.getCheatTracker().checkFamiliarAttack(chr))) {
            chr.getMap().broadcastMessage(chr, MaplePacketCreator.touchFamiliar(chr.getId(), unk, target.getObjectId(), type, 600, damage), chr.getTruePosition());
            target.damage(chr, damage, true);
            chr.getSummonedFamiliar().addFatigue(chr);
        }
    }

    public static void UseFamiliar(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        if ((chr == null) || (!chr.isAlive()) || (chr.getMap() == null) || (chr.hasBlockedInventory())) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        c.getPlayer().updateTick(slea.readInt());
        short slot = slea.readShort();
        int itemId = slea.readInt();
        Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);

        c.getSession().write(MaplePacketCreator.enableActions());
        if ((toUse == null) || (toUse.getQuantity() < 1) || (toUse.getItemId() != itemId) || (itemId / 10000 != 287)) {
            return;
        }
        StructFamiliar f = MapleItemInformationProvider.getInstance().getFamiliarByItem(itemId);
        if (MapleLifeFactory.getMonsterStats(f.mob).getLevel() <= c.getPlayer().getLevel()) {
            MonsterFamiliar mf = (MonsterFamiliar) c.getPlayer().getFamiliars().get(Integer.valueOf(f.familiar));
            if (mf != null) {
                if (mf.getVitality() >= 3) {
                    mf.setExpiry(Math.min(System.currentTimeMillis() + 7776000000L, mf.getExpiry() + 2592000000L));
                } else {
                    mf.setVitality(mf.getVitality() + 1);
                    mf.setExpiry(mf.getExpiry() + 2592000000L);
                }
            } else {
                mf = new MonsterFamiliar(c.getPlayer().getId(), f.familiar, System.currentTimeMillis() + 2592000000L);
                c.getPlayer().getFamiliars().put(Integer.valueOf(f.familiar), mf);
            }
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false, false);
            c.getSession().write(MaplePacketCreator.registerFamiliar(mf));
        }
    }
}