package handling.channel.handler;

import client.*;
import database.DatabaseConnection;
import handling.channel.ChannelServer;
import handling.world.World;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import tools.data.LittleEndianAccessor;
import tools.packet.BuddyListPacket;

public class BuddyListHandler {

    private static CharacterIdNameBuddyCapacity getCharacterIdAndNameFromDatabase(String name, String group)
            throws SQLException {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE name LIKE ?");
        ps.setString(1, name);
        ResultSet rs = ps.executeQuery();
        CharacterIdNameBuddyCapacity ret = null;
        if ((rs.next())
                && (rs.getInt("gm") < 3)) {
            ret = new CharacterIdNameBuddyCapacity(rs.getInt("id"), rs.getString("name"), group, rs.getInt("buddyCapacity"));
        }

        rs.close();
        ps.close();
        return ret;
    }

    public static void BuddyOperation(LittleEndianAccessor slea, MapleClient c) {
        int mode = slea.readByte();
        BuddyList buddylist = c.getPlayer().getBuddylist();
        if (mode == 1) {
            String addName = slea.readMapleAsciiString();
            String groupName = slea.readMapleAsciiString();
            BuddylistEntry ble = buddylist.get(addName);
            if ((addName.getBytes().length > 13) || (groupName.getBytes().length > 16)) {
                return;
            }
            if ((ble != null) && ((ble.getGroup().equals(groupName)) || (!ble.isVisible()))) {
                c.getSession().write(BuddyListPacket.buddylistMessage((byte) 11));
            } else if ((ble != null) && (ble.isVisible())) {
                ble.setGroup(groupName);
                c.getSession().write(BuddyListPacket.updateBuddylist(buddylist.getBuddies(), 10));
            } else if (buddylist.isFull()) {
                c.getSession().write(BuddyListPacket.buddylistMessage((byte) 11));
            } else {
                try {
                    CharacterIdNameBuddyCapacity charWithId = null;
                    int channel = World.Find.findChannel(addName);
                    MapleCharacter otherChar = null;
                    if (channel > 0) {
                        otherChar = ChannelServer.getInstance(channel).getPlayerStorage().getCharacterByName(addName);
                        if (otherChar == null) {
                            charWithId = getCharacterIdAndNameFromDatabase(addName, groupName);
                        } else if ((!otherChar.isIntern()) || (c.getPlayer().isIntern())) {
                            charWithId = new CharacterIdNameBuddyCapacity(otherChar.getId(), otherChar.getName(), groupName, otherChar.getBuddylist().getCapacity());
                        }
                    } else {
                        charWithId = getCharacterIdAndNameFromDatabase(addName, groupName);
                    }
                    if (charWithId != null) {
                        BuddyList.BuddyAddResult buddyAddResult = null;
                        if (channel > 0) {
                            buddyAddResult = World.Buddy.requestBuddyAdd(addName, c.getChannel(), c.getPlayer().getId(), c.getPlayer().getName(), c.getPlayer().getLevel(), c.getPlayer().getJob());
                        } else {
                            Connection con = DatabaseConnection.getConnection();
                            PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) as buddyCount FROM buddies WHERE characterid = ? AND pending = 0");
                            ps.setInt(1, charWithId.getId());
                            ResultSet rs = ps.executeQuery();
                            if (!rs.next()) {
                                ps.close();
                                rs.close();
                                throw new RuntimeException("Result set expected");
                            }
                            int count = rs.getInt("buddyCount");
                            if (count >= charWithId.getBuddyCapacity()) {
                                buddyAddResult = BuddyList.BuddyAddResult.BUDDYLIST_FULL;
                            }

                            rs.close();
                            ps.close();

                            ps = con.prepareStatement("SELECT pending FROM buddies WHERE characterid = ? AND buddyid = ?");
                            ps.setInt(1, charWithId.getId());
                            ps.setInt(2, c.getPlayer().getId());
                            rs = ps.executeQuery();
                            if (rs.next()) {
                                buddyAddResult = BuddyList.BuddyAddResult.ALREADY_ON_LIST;
                            }
                            rs.close();
                            ps.close();
                        }
                        if (buddyAddResult == BuddyList.BuddyAddResult.BUDDYLIST_FULL) {
                            c.getSession().write(BuddyListPacket.buddylistMessage((byte) 12));
                        } else {
                            int displayChannel = -1;
                            int otherCid = charWithId.getId();
                            if ((buddyAddResult == BuddyList.BuddyAddResult.ALREADY_ON_LIST) && (channel > 0)) {
                                displayChannel = channel;
                                notifyRemoteChannel(c, channel, otherCid, groupName, BuddyList.BuddyOperation.添加好友);
                            } else if (buddyAddResult != BuddyList.BuddyAddResult.ALREADY_ON_LIST) {
                                Connection con = DatabaseConnection.getConnection();
                                PreparedStatement ps = con.prepareStatement("INSERT INTO buddies (`characterid`, `buddyid`, `groupname`, `pending`) VALUES (?, ?, ?, 1)");
                                ps.setInt(1, charWithId.getId());
                                ps.setInt(2, c.getPlayer().getId());
                                ps.setString(3, groupName);
                                ps.executeUpdate();
                                ps.close();
                            }
                            buddylist.put(new BuddylistEntry(charWithId.getName(), otherCid, groupName, displayChannel, true));
                            c.getSession().write(BuddyListPacket.updateBuddylist(buddylist.getBuddies(), 10));
                        }
                    } else {
                        c.getSession().write(BuddyListPacket.buddylistMessage((byte) 15));
                    }
                } catch (SQLException e) {
                    System.err.println("SQL THROW" + e);
                }
            }
        } else if (mode == 2) {
            int otherCid = slea.readInt();
            BuddylistEntry ble = buddylist.get(otherCid);
            if ((!buddylist.isFull()) && (ble != null) && (!ble.isVisible())) {
                int channel = World.Find.findChannel(otherCid);
                buddylist.put(new BuddylistEntry(ble.getName(), otherCid, "未指定群组", channel, true));
                c.getSession().write(BuddyListPacket.updateBuddylist(buddylist.getBuddies(), 10));
                notifyRemoteChannel(c, channel, otherCid, "未指定群组", BuddyList.BuddyOperation.添加好友);
            } else {
                c.getSession().write(BuddyListPacket.buddylistMessage((byte) 11));
            }
        } else if (mode == 3) {
            int otherCid = slea.readInt();
            BuddylistEntry blz = buddylist.get(otherCid);
            if ((blz != null) && (blz.isVisible())) {
                notifyRemoteChannel(c, World.Find.findChannel(otherCid), otherCid, blz.getGroup(), BuddyList.BuddyOperation.删除好友);
            }
            buddylist.remove(otherCid);
            c.getSession().write(BuddyListPacket.updateBuddylist(buddylist.getBuddies(), 18));
        }
    }

    private static void notifyRemoteChannel(MapleClient c, int remoteChannel, int otherCid, String group, BuddyList.BuddyOperation operation) {
        MapleCharacter player = c.getPlayer();
        if (remoteChannel > 0) {
            World.Buddy.buddyChanged(otherCid, player.getId(), player.getName(), c.getChannel(), operation, group);
        }
    }

    private static final class CharacterIdNameBuddyCapacity extends CharacterNameAndId {

        private int buddyCapacity;

        public CharacterIdNameBuddyCapacity(int id, String name, String group, int buddyCapacity) {
            super(id, name, group);
            this.buddyCapacity = buddyCapacity;
        }

        public int getBuddyCapacity() {
            return this.buddyCapacity;
        }
    }
}
