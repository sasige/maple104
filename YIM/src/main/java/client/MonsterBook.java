package client;

import client.inventory.Equip;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import database.DatabaseConnection;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;
import server.MapleItemInformationProvider;
import server.quest.MapleQuest;
import tools.Pair;
import tools.Triple;
import tools.data.MaplePacketLittleEndianWriter;

public final class MonsterBook  implements Serializable {

    private static final long serialVersionUID = 7179541993413738569L;
    private boolean changed = false;
    private int currentSet = -1;
    private int level = 0;
    private int setScore;
    private int finishedSets;
    private Map<Integer, Integer> cards;
    private List<Integer> cardItems = new ArrayList<>();
    private Map<Integer, Pair<Integer, Boolean>> sets = new HashMap<>();

    public MonsterBook(Map<Integer, Integer> cards, MapleCharacter chr) {
        this.cards = cards;
        calculateItem();
        calculateScore();

        MapleQuestStatus stat = chr.getQuestNoAdd(MapleQuest.getInstance(122800));
        if ((stat != null) && (stat.getCustomData() != null)) {
            this.currentSet = Integer.parseInt(stat.getCustomData());
            if ((!this.sets.containsKey(this.currentSet)) || (!((Boolean) ((Pair) this.sets.get(this.currentSet)).right).booleanValue())) {
                this.currentSet = -1;
            }
        }
        applyBook(chr, true);
    }

    public void applyBook(MapleCharacter chr, boolean first_login) {
        if (GameConstants.GMS) {
            Equip item = (Equip) chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -55);
            if (item == null) {
                item = (Equip) MapleItemInformationProvider.getInstance().getEquipById(1172000);
                item.setPosition((short) -55);
            }
            modifyBook(item);
            if (first_login) {
                chr.getInventory(MapleInventoryType.EQUIPPED).addFromDB(item);
            } else {
                chr.forceReAddItem_Book(item, MapleInventoryType.EQUIPPED);
                chr.equipChanged();
            }
        }
    }

    public byte calculateScore() {
        byte returnval = 0;
        this.sets.clear();
        int oldLevel = this.level;
        int oldSetScore = this.setScore;
        this.setScore = 0;
        this.finishedSets = 0;
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        for (int i : cardItems) {
            final Integer x = ii.getSetId(i);
            if ((x != null) && (x.intValue() > 0)) {
                final Triple<Integer, List<Integer>, List<Integer>> set = ii.getMonsterBookInfo(x.intValue());
                if (set != null) {
                    if (!this.sets.containsKey(x)) {
                        this.sets.put(x, new Pair<>(1, Boolean.FALSE));
                    } else {
                        sets.get(x).left++;
                    }
                    if (sets.get(x).left == set.mid.size()) {
                        (sets.get(x)).right = Boolean.TRUE;
                        setScore += set.left;
                        if (currentSet == -1) {
                            currentSet = x.intValue();
                            returnval = 2;
                        }
                        this.finishedSets++;
                    }
                }
            }
        }
        this.level = 10;
        for (byte i = 0; i < 10; i = (byte) (i + 1)) {
            if (GameConstants.getSetExpNeededForLevel(i) > this.setScore) {
                this.level = i;
                break;
            }
        }
        if (this.level > oldLevel) {
            returnval = 2;
        } else if (this.setScore > oldSetScore) {
            returnval = 1;
        }
        return returnval;
    }

    public void writeCharInfoPacket(MaplePacketLittleEndianWriter mplew) {
        List<Integer> cardSize = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            cardSize.add(0);
        }
        for (int x : this.cardItems) {
            cardSize.set(0, cardSize.get(0) + 1);
            cardSize.set(x / 1000 % 10 + 1, cardSize.get(x / 1000 % 10 + 1) + 1);
        }
        for (int i : cardSize) {
            mplew.writeInt(i);
        }
        mplew.writeInt(this.setScore);
        mplew.writeInt(this.currentSet);
        mplew.writeInt(this.finishedSets);
    }

    public void writeFinished(MaplePacketLittleEndianWriter mplew) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        mplew.write(1);
        mplew.writeShort(this.cardItems.size());
        final List<Integer> mbList = new ArrayList<>(ii.getMonsterBookList());
        Collections.sort(mbList);
        final int fullCards = mbList.size() / 8 + (mbList.size() % 8 > 0 ? 1 : 0);
        mplew.writeShort(fullCards);

        for (int i = 0; i < fullCards; i++) {
            int currentMask = 1;
            int maskToWrite = 0;
            for (int y = i * 8; (y < i * 8 + 8) && (mbList.size() > y); y++) {
                if (this.cardItems.contains(mbList.get(y))) {
                    maskToWrite |= currentMask;
                }
                currentMask *= 2;
            }
            mplew.write(maskToWrite);
        }

        final int fullSize = this.cardItems.size() / 2 + (this.cardItems.size() % 2 > 0 ? 1 : 0);
        mplew.writeShort(fullSize);
        for (int i = 0; i < fullSize; i++) {
            mplew.write(i == this.cardItems.size() / 2 ? 1 : 17);
        }
    }

    public void writeUnfinished(MaplePacketLittleEndianWriter mplew) {
        mplew.write(0);
        mplew.writeShort(this.cardItems.size());
        for (int i:cardItems) {
            mplew.writeShort(i % 10000);
            mplew.write(1);
        }
    }

    public void calculateItem() {
        this.cardItems.clear();
        for (Entry<Integer, Integer> s : this.cards.entrySet()) {
            addCardItem(s.getKey(), s.getValue());
        }
    }

    public void addCardItem(int key, int value) {
        if (value >= 2) {
            Integer x = MapleItemInformationProvider.getInstance().getItemIdByMob(key);
            if ((x != null) && (x.intValue() > 0)) {
                this.cardItems.add(x.intValue());
            }
        }
    }

    public void modifyBook(Equip eq) {
        eq.setStr((short) this.level);
        eq.setDex((short) this.level);
        eq.setInt((short) this.level);
        eq.setLuk((short) this.level);
        eq.setPotential1(0);
        eq.setPotential2(0);
        eq.setPotential3(0);
        if (this.currentSet > -1) {
            Triple<Integer, List<Integer>, List<Integer>> set = MapleItemInformationProvider.getInstance().getMonsterBookInfo(this.currentSet);
            if (set != null) {
                for (int i = 0; i < (set.right).size(); i++) {
                    if (i == 0) {
                        eq.setPotential1(set.right.get(i).intValue());
                        
                    } else if (i == 1) {
                        eq.setPotential2(set.right.get(i).intValue());
                    } else if (i == 2) {
                        eq.setPotential3(set.right.get(i).intValue());
                        break;
                    }
                }
            } else {
                this.currentSet = -1;
            }
        }
    }

    public int getSetScore() {
        return this.setScore;
    }

    public int getLevel() {
        return this.level;
    }

    public int getSet() {
        return this.currentSet;
    }

    public boolean changeSet(int c) {
        if ((this.sets.containsKey(c)) && (this.sets.get(c)).right) {
            this.currentSet = c;
            return true;
        }
        return false;
    }

    public void changed() {
        this.changed = true;
    }

    public Map<Integer, Integer> getCards() {
        return this.cards;
    }

    public int getSeen() {
        return this.cards.size();
    }

    public int getCaught() {
        int ret = 0;
        for (int i : cards.values()) {
            if (i >= 2) {
                ret++;
            }
        }
        return ret;
    }

    public int getLevelByCard(int cardid) {
        return this.cards.get(cardid) == null ? 0 : (this.cards.get(cardid));
    }

    public static MonsterBook loadCards(int charid, MapleCharacter chr) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM monsterbook WHERE charid = ? ORDER BY cardid ASC");
        ps.setInt(1, charid);
        ResultSet rs = ps.executeQuery();
        Map cards = new LinkedHashMap();

        while (rs.next()) {
            cards.put(rs.getInt("cardid"), rs.getInt("level"));
        }
        rs.close();
        ps.close();
        return new MonsterBook(cards, chr);
    }

    public void saveCards(int charid) throws SQLException {
        if (!this.changed) {
            return;
        }
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("DELETE FROM monsterbook WHERE charid = ?");
        ps.setInt(1, charid);
        ps.execute();
        ps.close();
        this.changed = false;
        if (this.cards.isEmpty()) {
            return;
        }

        boolean first = true;
        StringBuilder query = new StringBuilder();

        for (Map.Entry all : this.cards.entrySet()) {
            if (first) {
                first = false;
                query.append("INSERT INTO monsterbook VALUES (DEFAULT,");
            } else {
                query.append(",(DEFAULT,");
            }
            query.append(charid);
            query.append(",");
            query.append(all.getKey());
            query.append(",");
            query.append(all.getValue());
            query.append(")");
        }
        ps = con.prepareStatement(query.toString());
        ps.execute();
        ps.close();
    }

    public boolean monsterCaught(MapleClient c, int cardid, String cardname) {
        if ((!this.cards.containsKey(cardid)) || (this.cards.get(cardid) < 2)) {
            this.changed = true;
            c.getPlayer().dropMessage(-6, new StringBuilder().append("Book entry updated - ").append(cardname).toString());

            this.cards.put(cardid, 2);
            if (GameConstants.GMS) {
                if (c.getPlayer().getQuestStatus(50195) != 1) {
                    MapleQuest.getInstance(50195).forceStart(c.getPlayer(), 9010000, "1");
                }
                if (c.getPlayer().getQuestStatus(50196) != 1) {
                    MapleQuest.getInstance(50196).forceStart(c.getPlayer(), 9010000, "1");
                }
                addCardItem(cardid, 2);
                byte rr = calculateScore();
                if (rr > 0) {
                    if (c.getPlayer().getQuestStatus(50197) != 1) {
                        MapleQuest.getInstance(50197).forceStart(c.getPlayer(), 9010000, "1");
                    }

                    if (rr > 1) {
                        applyBook(c.getPlayer(), false);
                    }
                }
            }
            return true;
        }
        return false;
    }

    public void monsterSeen(MapleClient c, int cardid, String cardname) {
        if (this.cards.containsKey(cardid)) {
            return;
        }
        this.changed = true;

        c.getPlayer().dropMessage(-6, new StringBuilder().append("New book entry - ").append(cardname).toString());
        this.cards.put(cardid, 1);
    }
}