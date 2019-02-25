package handling.channel.handler;

import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.MapleClient;
import handling.world.World;
import handling.world.family.MapleFamily;
import handling.world.family.MapleFamilyBuff;
import handling.world.family.MapleFamilyCharacter;
import java.util.List;
import server.maps.FieldLimitType;
import tools.MaplePacketCreator;
import tools.data.LittleEndianAccessor;
import tools.packet.FamilyPacket;

public class FamilyHandler {

    public static void RequestFamily(LittleEndianAccessor slea, MapleClient c) {
        MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
        if (chr != null) {
            c.getSession().write(FamilyPacket.getFamilyPedigree(chr));
        }
    }

    public static void OpenFamily(LittleEndianAccessor slea, MapleClient c) {
        c.getSession().write(FamilyPacket.getFamilyInfo(c.getPlayer()));
    }

    public static void UseFamily(LittleEndianAccessor slea, MapleClient c) {
        int type = slea.readInt();
        if (MapleFamilyBuff.values().length <= type) {
            return;
        }
        MapleFamilyBuff entry = MapleFamilyBuff.values()[type];
        boolean success = (c.getPlayer().getFamilyId() > 0) && (c.getPlayer().canUseFamilyBuff(entry)) && (c.getPlayer().getCurrentRep() > entry.rep);
        if (!success) {
            return;
        }
        MapleCharacter victim = null;
        switch (entry) {
            case 瞬移:
                victim = c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
                if ((FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit())) || (c.getPlayer().isInBlockedMap())) {
                    c.getPlayer().dropMessage(5, "Summons failed. Your current location or state does not allow a summons.");
                    success = false;
                } else if ((victim == null) || ((victim.isGM()) && (!c.getPlayer().isGM()))) {
                    c.getPlayer().dropMessage(1, "Invalid name or you are not on the same channel.");
                    success = false;
                } else if ((victim.getFamilyId() == c.getPlayer().getFamilyId()) && (!FieldLimitType.VipRock.check(victim.getMap().getFieldLimit())) && (victim.getId() != c.getPlayer().getId()) && (!victim.isInBlockedMap())) {
                    c.getPlayer().changeMap(victim.getMap(), victim.getMap().getPortal(0));
                } else {
                    c.getPlayer().dropMessage(5, "Summons failed. Your current location or state does not allow a summons.");
                    success = false;
                }
                break;
            case 召唤:
                victim = c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
                if ((FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit())) || (c.getPlayer().isInBlockedMap())) {
                    c.getPlayer().dropMessage(5, "Summons failed. Your current location or state does not allow a summons.");
                } else if ((victim == null) || ((victim.isGM()) && (!c.getPlayer().isGM()))) {
                    c.getPlayer().dropMessage(1, "Invalid name or you are not on the same channel.");
                } else if (victim.getTeleportName().length() > 0) {
                    c.getPlayer().dropMessage(1, "Another character has requested to summon this character. Please try again later.");
                } else if ((victim.getFamilyId() == c.getPlayer().getFamilyId()) && (!FieldLimitType.VipRock.check(victim.getMap().getFieldLimit())) && (victim.getId() != c.getPlayer().getId()) && (!victim.isInBlockedMap())) {
                    victim.getClient().getSession().write(FamilyPacket.familySummonRequest(c.getPlayer().getName(), c.getPlayer().getMap().getMapName()));
                    victim.setTeleportName(c.getPlayer().getName());
                } else {
                    c.getPlayer().dropMessage(5, "Summons failed. Your current location or state does not allow a summons.");
                }
                return;
            case 爆率15分钟:
            case 经验15分钟:
            case 爆率30分钟:
            case 经验30分钟:
                entry.applyTo(c.getPlayer());
                break;
            case 团结:
                MapleFamily fam = World.Family.getFamily(c.getPlayer().getFamilyId());
                List<MapleFamilyCharacter> chrs = fam.getMFC(c.getPlayer().getId()).getOnlineJuniors(fam);
                if (chrs.size() < 7) {
                    success = false;
                } else {
                    for (MapleFamilyCharacter chrz : chrs) {
                        int chr = World.Find.findChannel(chrz.getId());
                        if (chr == -1) {
                            continue;
                        }
                        MapleCharacter chrr = World.getStorage(chr).getCharacterById(chrz.getId());
                        entry.applyTo(chrr);
                    }

                }

        }

        if (success) {
            c.getPlayer().setCurrentRep(c.getPlayer().getCurrentRep() - entry.rep);
            c.getSession().write(FamilyPacket.changeRep(-entry.rep, c.getPlayer().getName()));
            c.getPlayer().useFamilyBuff(entry);
        } else {
            c.getPlayer().dropMessage(5, "发生未知错误。");
        }
    }

    public static void FamilyOperation(LittleEndianAccessor slea, MapleClient c) {
        if (c.getPlayer() == null) {
            return;
        }
        MapleCharacter addChr = c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
        if (addChr == null) {
            c.getSession().write(FamilyPacket.sendFamilyMessage(65));
        } else if ((addChr.getFamilyId() == c.getPlayer().getFamilyId()) && (addChr.getFamilyId() > 0)) {
            c.getSession().write(FamilyPacket.sendFamilyMessage(66));
        } else if (addChr.getMapId() != c.getPlayer().getMapId()) {
            c.getSession().write(FamilyPacket.sendFamilyMessage(69));
        } else if (addChr.getSeniorId() != 0) {
            c.getSession().write(FamilyPacket.sendFamilyMessage(70));
        } else if (addChr.getLevel() >= c.getPlayer().getLevel()) {
            c.getSession().write(FamilyPacket.sendFamilyMessage(71));
        } else if (addChr.getLevel() < c.getPlayer().getLevel() - 20) {
            c.getSession().write(FamilyPacket.sendFamilyMessage(72));
        } else if (addChr.getLevel() < 10) {
            c.getPlayer().dropMessage(1, "被邀请的角色等级必须大于10级.");
        } else if ((c.getPlayer().getJunior1() > 0) && (c.getPlayer().getJunior2() > 0)) {
            c.getPlayer().dropMessage(1, "你已经有2位同学，无法继续邀请.");
        } else if ((c.getPlayer().isGM()) || (!addChr.isGM())) {
            addChr.getClient().getSession().write(FamilyPacket.sendFamilyInvite(c.getPlayer().getId(), c.getPlayer().getLevel(), c.getPlayer().getJob(), c.getPlayer().getName()));
        }
        c.getSession().write(MaplePacketCreator.enableActions());
    }

    public static void FamilyPrecept(LittleEndianAccessor slea, MapleClient c) {
        MapleFamily fam = World.Family.getFamily(c.getPlayer().getFamilyId());
        if ((fam == null) || (fam.getLeaderId() != c.getPlayer().getId())) {
            return;
        }
        fam.setNotice(slea.readMapleAsciiString());
    }

    public static void FamilySummon(LittleEndianAccessor slea, MapleClient c) {
        MapleFamilyBuff cost = MapleFamilyBuff.召唤;
        MapleCharacter tt = c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
        if ((c.getPlayer().getFamilyId() > 0) && (tt != null) && (tt.getFamilyId() == c.getPlayer().getFamilyId()) && (!FieldLimitType.VipRock.check(tt.getMap().getFieldLimit())) && (!FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit())) && (tt.canUseFamilyBuff(cost)) && (c.getPlayer().getTeleportName().equals(tt.getName())) && (tt.getCurrentRep() > cost.rep) && (!c.getPlayer().isInBlockedMap()) && (!tt.isInBlockedMap())) {
            boolean accepted = slea.readByte() > 0;
            if (accepted) {
                c.getPlayer().changeMap(tt.getMap(), tt.getMap().getPortal(0));
                tt.setCurrentRep(tt.getCurrentRep() - cost.rep);
                tt.getClient().getSession().write(FamilyPacket.changeRep(-cost.rep, tt.getName()));
                tt.useFamilyBuff(cost);
            } else {
                tt.dropMessage(5, "召唤玩家失败，您当前的位置或状态不容许召唤学院同学。");
            }
        } else {
            c.getPlayer().dropMessage(5, "召唤玩家失败，您当前的位置或状态不容许召唤学院同学。");
        }
        c.getPlayer().setTeleportName("");
    }

    public static void DeleteJunior(LittleEndianAccessor slea, MapleClient c) {
        int juniorid = slea.readInt();
        if ((c.getPlayer().getFamilyId() <= 0) || (juniorid <= 0) || ((c.getPlayer().getJunior1() != juniorid) && (c.getPlayer().getJunior2() != juniorid))) {
            return;
        }

        MapleFamily fam = World.Family.getFamily(c.getPlayer().getFamilyId());
        MapleFamilyCharacter other = fam.getMFC(juniorid);
        if (other == null) {
            return;
        }
        MapleFamilyCharacter oth = c.getPlayer().getMFC();
        boolean junior2 = oth.getJunior2() == juniorid;
        if (junior2) {
            oth.setJunior2(0);
        } else {
            oth.setJunior1(0);
        }
        c.getPlayer().saveFamilyStatus();
        other.setSeniorId(0);

        MapleFamily.setOfflineFamilyStatus(other.getFamilyId(), other.getSeniorId(), other.getJunior1(), other.getJunior2(), other.getCurrentRep(), other.getTotalRep(), other.getId());

        MapleCharacterUtil.sendNote(other.getName(), c.getPlayer().getName(), c.getPlayer().getName() + "宣布诀别，冒险学院关系已断绝。", 0);
        MapleCharacter receiver = c.getChannelServer().getPlayerStorage().getCharacterByName(other.getName());
        if (receiver != null) {
            receiver.showNote();
        }
        if (!fam.splitFamily(juniorid, other)) {
            if (!junior2) {
                fam.resetDescendants();
            }
            fam.resetPedigree();
        }
        c.getSession().write(FamilyPacket.sendFamilyMessage(1, other.getId()));

        c.getSession().write(MaplePacketCreator.enableActions());
    }

    public static void DeleteSenior(LittleEndianAccessor slea, MapleClient c) {
        if ((c.getPlayer().getFamilyId() <= 0) || (c.getPlayer().getSeniorId() <= 0)) {
            return;
        }

        MapleFamily fam = World.Family.getFamily(c.getPlayer().getFamilyId());
        MapleFamilyCharacter mgc = fam.getMFC(c.getPlayer().getSeniorId());
        MapleFamilyCharacter mgc_ = c.getPlayer().getMFC();
        mgc_.setSeniorId(0);
        boolean junior2 = mgc.getJunior2() == c.getPlayer().getId();
        if (junior2) {
            mgc.setJunior2(0);
        } else {
            mgc.setJunior1(0);
        }

        MapleFamily.setOfflineFamilyStatus(mgc.getFamilyId(), mgc.getSeniorId(), mgc.getJunior1(), mgc.getJunior2(), mgc.getCurrentRep(), mgc.getTotalRep(), mgc.getId());

        c.getPlayer().saveFamilyStatus();
        MapleCharacterUtil.sendNote(mgc.getName(), c.getPlayer().getName(), c.getPlayer().getName() + "宣布诀别，冒险学院关系已断绝。", 0);
        MapleCharacter receiver = c.getChannelServer().getPlayerStorage().getCharacterByName(mgc.getName());
        if (receiver != null) {
            receiver.showNote();
        }
        if (!fam.splitFamily(c.getPlayer().getId(), mgc_)) {
            if (!junior2) {
                fam.resetDescendants();
            }
            fam.resetPedigree();
        }
        c.getSession().write(FamilyPacket.sendFamilyMessage(1, mgc.getId()));

        c.getSession().write(MaplePacketCreator.enableActions());
    }

    public static void AcceptFamily(LittleEndianAccessor slea, MapleClient c) {
        MapleCharacter inviter = c.getPlayer().getMap().getCharacterById(slea.readInt());
        if ((inviter != null) && (c.getPlayer().getSeniorId() == 0) && ((c.getPlayer().isGM()) || (!inviter.isHidden())) && (inviter.getLevel() - 20 <= c.getPlayer().getLevel()) && (inviter.getLevel() >= 10) && (inviter.getName().equals(slea.readMapleAsciiString())) && (inviter.getNoJuniors() < 2) && (c.getPlayer().getLevel() >= 10)) {
            boolean accepted = slea.readByte() > 0;
            inviter.getClient().getSession().write(FamilyPacket.sendFamilyJoinResponse(accepted, c.getPlayer().getName()));
            if (accepted) {
                c.getSession().write(FamilyPacket.getSeniorMessage(inviter.getName()));
                int old = c.getPlayer().getMFC() == null ? 0 : c.getPlayer().getMFC().getFamilyId();
                int oldj1 = c.getPlayer().getMFC() == null ? 0 : c.getPlayer().getMFC().getJunior1();
                int oldj2 = c.getPlayer().getMFC() == null ? 0 : c.getPlayer().getMFC().getJunior2();
                if ((inviter.getFamilyId() > 0) && (World.Family.getFamily(inviter.getFamilyId()) != null)) {
                    MapleFamily fam = World.Family.getFamily(inviter.getFamilyId());

                    c.getPlayer().setFamily(old <= 0 ? inviter.getFamilyId() : old, inviter.getId(), oldj1 <= 0 ? 0 : oldj1, oldj2 <= 0 ? 0 : oldj2);
                    MapleFamilyCharacter mf = inviter.getMFC();
                    if (mf.getJunior1() > 0) {
                        mf.setJunior2(c.getPlayer().getId());
                    } else {
                        mf.setJunior1(c.getPlayer().getId());
                    }
                    inviter.saveFamilyStatus();
                    if ((old > 0) && (World.Family.getFamily(old) != null)) {
                        MapleFamily.mergeFamily(fam, World.Family.getFamily(old));
                    } else {
                        c.getPlayer().setFamily(inviter.getFamilyId(), inviter.getId(), oldj1 <= 0 ? 0 : oldj1, oldj2 <= 0 ? 0 : oldj2);
                        fam.setOnline(c.getPlayer().getId(), true, c.getChannel());
                        c.getPlayer().saveFamilyStatus();
                    }
                    if (fam != null) {
                        if ((inviter.getNoJuniors() == 1) || (old > 0)) {
                            fam.resetDescendants();
                        }
                        fam.resetPedigree();
                    }
                } else {
                    int id = MapleFamily.createFamily(inviter.getId());
                    if (id > 0) {
                        MapleFamily.setOfflineFamilyStatus(id, 0, c.getPlayer().getId(), 0, inviter.getCurrentRep(), inviter.getTotalRep(), inviter.getId());
                        MapleFamily.setOfflineFamilyStatus(id, inviter.getId(), oldj1 <= 0 ? 0 : oldj1, oldj2 <= 0 ? 0 : oldj2, c.getPlayer().getCurrentRep(), c.getPlayer().getTotalRep(), c.getPlayer().getId());
                        inviter.setFamily(id, 0, c.getPlayer().getId(), 0);
                        inviter.finishAchievement(36);
                        c.getPlayer().setFamily(id, inviter.getId(), oldj1 <= 0 ? 0 : oldj1, oldj2 <= 0 ? 0 : oldj2);
                        MapleFamily fam = World.Family.getFamily(id);
                        fam.setOnline(inviter.getId(), true, inviter.getClient().getChannel());
                        if ((old > 0) && (World.Family.getFamily(old) != null)) {
                            MapleFamily.mergeFamily(fam, World.Family.getFamily(old));
                        } else {
                            fam.setOnline(c.getPlayer().getId(), true, c.getChannel());
                        }
                        fam.resetDescendants();
                        fam.resetPedigree();
                    }
                }
                c.getSession().write(FamilyPacket.getFamilyInfo(c.getPlayer()));
            }
        }
    }
}