package tools.packet;

import client.MapleCharacter;
import handling.SendPacketOpcode;
import handling.world.World;
import handling.world.family.MapleFamily;
import handling.world.family.MapleFamilyBuff;
import handling.world.family.MapleFamilyCharacter;
import java.util.Iterator;
import java.util.List;
import tools.data.MaplePacketLittleEndianWriter;

public class FamilyPacket {

    public static byte[] getFamilyData() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.FAMILY.getValue());
        MapleFamilyBuff[] entries = MapleFamilyBuff.values();
        mplew.writeInt(entries.length);

        for (MapleFamilyBuff entry : entries) {
            mplew.write(entry.type);
            mplew.writeInt(entry.rep);
            mplew.writeInt(1);
            mplew.writeMapleAsciiString(entry.name);
            mplew.writeMapleAsciiString(entry.desc);
        }
        return mplew.getPacket();
    }

    public static byte[] changeRep(int r, String name) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.REP_INCREASE.getValue());
        mplew.writeInt(r);
        mplew.writeMapleAsciiString(name);
        return mplew.getPacket();
    }

    public static byte[] getFamilyInfo(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.OPEN_FAMILY.getValue());
        mplew.writeInt(chr.getCurrentRep());
        mplew.writeInt(chr.getTotalRep());
        mplew.writeInt(chr.getTotalRep());
        mplew.writeShort(chr.getNoJuniors());
        mplew.writeShort(2);
        mplew.writeShort(chr.getNoJuniors());
        MapleFamily family = World.Family.getFamily(chr.getFamilyId());
        if (family != null) {
            mplew.writeInt(family.getLeaderId());
            mplew.writeMapleAsciiString(family.getLeaderName());
            mplew.writeMapleAsciiString(family.getNotice());
        } else {
            mplew.writeLong(0L);
        }
        List b = chr.usedBuffs();
        mplew.writeInt(b.size());
        for (Iterator i$ = b.iterator(); i$.hasNext();) {
            int ii = ((Integer) i$.next()).intValue();
            mplew.writeInt(ii);
            mplew.writeInt(1);
        }
        return mplew.getPacket();
    }

    public static void addFamilyCharInfo(MapleFamilyCharacter ldr, MaplePacketLittleEndianWriter mplew) {
        mplew.writeInt(ldr.getId());
        mplew.writeInt(ldr.getSeniorId());
        mplew.writeShort(ldr.getJobId());
        mplew.write(ldr.getLevel());
        mplew.write(ldr.isOnline() ? 1 : 0);
        mplew.writeInt(ldr.getCurrentRep());
        mplew.writeInt(ldr.getTotalRep());
        mplew.writeInt(ldr.getTotalRep());
        mplew.writeInt(ldr.getTotalRep());
        mplew.writeLong(Math.max(ldr.getChannel(), 0));
        mplew.writeMapleAsciiString(ldr.getName());
    }

    public static byte[] getFamilyPedigree(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SEND_PEDIGREE.getValue());
        mplew.writeInt(chr.getId());
        MapleFamily family = World.Family.getFamily(chr.getFamilyId());
        int descendants = 2;
        int gens = 0;
        int generations = 0;
        if (family == null) {
            mplew.writeInt(2);
            addFamilyCharInfo(new MapleFamilyCharacter(chr, 0, 0, 0, 0), mplew);
        } else {
            mplew.writeInt(family.getMFC(chr.getId()).getPedigree().size() + 1);
            addFamilyCharInfo(family.getMFC(family.getLeaderId()), mplew);

            if (chr.getSeniorId() > 0) {
                MapleFamilyCharacter senior = family.getMFC(chr.getSeniorId());
                if (senior != null) {
                    if (senior.getSeniorId() > 0) {
                        addFamilyCharInfo(family.getMFC(senior.getSeniorId()), mplew);
                    }
                    addFamilyCharInfo(senior, mplew);
                }
            }
        }
        addFamilyCharInfo(chr.getMFC() == null ? new MapleFamilyCharacter(chr, 0, 0, 0, 0) : chr.getMFC(), mplew);
        if (family != null) {
            if (chr.getSeniorId() > 0) {
                MapleFamilyCharacter senior = family.getMFC(chr.getSeniorId());
                if (senior != null) {
                    if ((senior.getJunior1() > 0) && (senior.getJunior1() != chr.getId())) {
                        addFamilyCharInfo(family.getMFC(senior.getJunior1()), mplew);
                    } else if ((senior.getJunior2() > 0) && (senior.getJunior2() != chr.getId())) {
                        addFamilyCharInfo(family.getMFC(senior.getJunior2()), mplew);
                    }
                }
            }
            if (chr.getJunior1() > 0) {
                MapleFamilyCharacter junior = family.getMFC(chr.getJunior1());
                if (junior != null) {
                    addFamilyCharInfo(junior, mplew);
                }
            }
            if (chr.getJunior2() > 0) {
                MapleFamilyCharacter junior = family.getMFC(chr.getJunior2());
                if (junior != null) {
                    addFamilyCharInfo(junior, mplew);
                }
            }
            if (chr.getJunior1() > 0) {
                MapleFamilyCharacter junior = family.getMFC(chr.getJunior1());
                if (junior != null) {
                    if ((junior.getJunior1() > 0) && (family.getMFC(junior.getJunior1()) != null)) {
                        gens++;
                        addFamilyCharInfo(family.getMFC(junior.getJunior1()), mplew);
                    }
                    if ((junior.getJunior2() > 0) && (family.getMFC(junior.getJunior2()) != null)) {
                        gens++;
                        addFamilyCharInfo(family.getMFC(junior.getJunior2()), mplew);
                    }
                }
            }
            if (chr.getJunior2() > 0) {
                MapleFamilyCharacter junior = family.getMFC(chr.getJunior2());
                if (junior != null) {
                    if ((junior.getJunior1() > 0) && (family.getMFC(junior.getJunior1()) != null)) {
                        gens++;
                        addFamilyCharInfo(family.getMFC(junior.getJunior1()), mplew);
                    }
                    if ((junior.getJunior2() > 0) && (family.getMFC(junior.getJunior2()) != null)) {
                        gens++;
                        addFamilyCharInfo(family.getMFC(junior.getJunior2()), mplew);
                    }
                }
            }
            generations = family.getMemberSize();
        }
        mplew.writeLong(2 + gens);
        mplew.writeInt(gens);
        mplew.writeInt(-1);
        mplew.writeInt(generations);
        if (family != null) {
            if (chr.getJunior1() > 0) {
                MapleFamilyCharacter junior = family.getMFC(chr.getJunior1());
                if (junior != null) {
                    if ((junior.getJunior1() > 0) && (family.getMFC(junior.getJunior1()) != null)) {
                        mplew.writeInt(junior.getJunior1());
                        mplew.writeInt(family.getMFC(junior.getJunior1()).getDescendants());
                    }
                    if ((junior.getJunior2() > 0) && (family.getMFC(junior.getJunior2()) != null)) {
                        mplew.writeInt(junior.getJunior2());
                        mplew.writeInt(family.getMFC(junior.getJunior2()).getDescendants());
                    }
                }
            }
            if (chr.getJunior2() > 0) {
                MapleFamilyCharacter junior = family.getMFC(chr.getJunior2());
                if (junior != null) {
                    if ((junior.getJunior1() > 0) && (family.getMFC(junior.getJunior1()) != null)) {
                        mplew.writeInt(junior.getJunior1());
                        mplew.writeInt(family.getMFC(junior.getJunior1()).getDescendants());
                    }
                    if ((junior.getJunior2() > 0) && (family.getMFC(junior.getJunior2()) != null)) {
                        mplew.writeInt(junior.getJunior2());
                        mplew.writeInt(family.getMFC(junior.getJunior2()).getDescendants());
                    }
                }
            }
        }
        List b = chr.usedBuffs();
        mplew.writeInt(b.size());
        for (Iterator i$ = b.iterator(); i$.hasNext();) {
            int ii = ((Integer) i$.next()).intValue();
            mplew.writeInt(ii);
            mplew.writeInt(1);
        }
        mplew.writeShort(2);
        return mplew.getPacket();
    }

    public static byte[] sendFamilyInvite(int cid, int otherLevel, int otherJob, String inviter) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.FAMILY_INVITE.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(otherLevel);
        mplew.writeInt(otherJob);
        mplew.writeMapleAsciiString(inviter);

        return mplew.getPacket();
    }

    public static byte[] getSeniorMessage(String name) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SENIOR_MESSAGE.getValue());
        mplew.writeMapleAsciiString(name);
        return mplew.getPacket();
    }

    public static byte[] sendFamilyJoinResponse(boolean accepted, String added) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.FAMILY_JUNIOR.getValue());

        mplew.write(accepted ? 1 : 0);
        mplew.writeMapleAsciiString(added);
        return mplew.getPacket();
    }

    public static byte[] familyBuff(int type, int buffnr, int amount, int time) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.FAMILY_BUFF.getValue());
        mplew.write(type);
        if ((type >= 2) && (type <= 4)) {
            mplew.writeInt(buffnr);

            mplew.writeInt(type == 3 ? 0 : amount);
            mplew.writeInt(type == 2 ? 0 : amount);
            mplew.write(0);
            mplew.writeInt(time);
        }
        return mplew.getPacket();
    }

    public static byte[] cancelFamilyBuff() {
        return familyBuff(0, 0, 0, 0);
    }

    public static byte[] familyLoggedIn(boolean online, String name) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.FAMILY_LOGGEDIN.getValue());
        mplew.write(online ? 1 : 0);
        mplew.writeMapleAsciiString(name);
        return mplew.getPacket();
    }

    public static byte[] familySummonRequest(String name, String mapname) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.FAMILY_USE_REQUEST.getValue());
        mplew.writeMapleAsciiString(name);
        mplew.writeMapleAsciiString(mapname);
        return mplew.getPacket();
    }

    public static byte[] sendFamilyMessage(int op) {
        return sendFamilyMessage(op, 0);
    }

    public static byte[] sendFamilyMessage(int op, int id) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.FAMILY_MESSAGE.getValue());
        mplew.writeInt(op);
        mplew.writeInt(id);

        return mplew.getPacket();
    }
}