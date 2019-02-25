package server;

import client.MapleCharacter;
import client.inventory.Equip;
import client.inventory.MapleInventoryType;
import database.DatabaseConnection;
import handling.channel.ChannelServer;
import handling.world.World.Broadcast;
import java.io.PrintStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import tools.MaplePacketCreator;

public class MapleDonation {

    private static final Logger log = Logger.getLogger(MapleDonation.class);
    private static final String ALLOWED_NAME = "TooInspired";
    private static final int PRIZE_ID = 1142155;
    private static final int RANKING_NUMBER = 10;
    private static Map<String, Integer> storage = new HashMap();
    private static int winnerId;
    private static int totalCash;

    public static void start() {
        Timer.WorldTimer.getInstance().register(new Runnable() {

            public void run() {
                MapleDonation.updateRank(false);
            }
        }, 1800000L);
    }

    public static void updateRank(boolean deleteDupes) {
        System.out.println("开始更新玩家捐献排名...");
        long startTime = System.currentTimeMillis();
        try {
            loadFromDatabase(deleteDupes);
        } catch (SQLException e) {
            log.error("加载玩家捐献排名出错.",e);
        }
        System.out.println(new StringBuilder().append("更新捐献排名更新完成 耗时: ").append((System.currentTimeMillis() - startTime) / 1000L).append(" 秒..").toString());
    }

    public static void loadFromDatabase(boolean deleteDupes)
            throws SQLException {
        storage.clear();
        totalCash = 0;
        String winnerName = null;
        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT `username`, `quantity` FROM donation ORDER by `quantity` DESC");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            if (winnerName == null) {
                winnerName = rs.getString("name");
            }
            storage.put(rs.getString("name"), Integer.valueOf(rs.getInt("amount")));
            totalCash += rs.getInt("amount");
        }
        rs.close();
        ps.close();
        ps = DatabaseConnection.getConnection().prepareStatement("SELECT `id` FROM characters WHERE `name` = ?");
        ps.setString(1, winnerName);
        rs = ps.executeQuery();
        if (rs.next()) {
            winnerId = rs.getInt("id");
        }
        rs.close();
        ps.close();
        if (deleteDupes) {
            ps = DatabaseConnection.getConnection().prepareStatement("DELETE FROM inventoryitems WHERE itemid = ? AND characterid <> ?");
            ps.setInt(1, 1142155);
            ps.setInt(2, winnerId);
            ps.executeUpdate();
            ps.close();
        }
    }

    public static void saveAll()
            throws SQLException {
        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("TRUNCATE scammed_donations");
        ps.executeUpdate();
        ps.close();
        ps = DatabaseConnection.getConnection().prepareStatement("INSERT INTO scammed_donations (`name`, `amount`) VALUES (?, ?)");
        for (String s : storage.keySet()) {
            ps.setString(1, s);
            ps.setLong(2, ((Integer) storage.get(s)).intValue());
            ps.addBatch();
        }
        ps.executeBatch();
        ps.close();
    }

    public static void addDonation(String user, int amount) {
        if (storage.containsKey(user)) {
            storage.put(user, Integer.valueOf(((Integer) storage.get(user)).intValue() + amount));
        } else {
            storage.put(user, Integer.valueOf(amount));
        }
        totalCash += amount;
    }

    public static String displayRankings() {
        ArrayList collect = new ArrayList(storage.values());
        Set exclude = new HashSet();
        Collections.sort(collect);
        StringBuilder ret = new StringBuilder("Top scores: \r\n");
        int start = collect.size() - 1;
        for (int i = start; i > start - 10; i--) {
            if (i < 0) {
                return ret.toString();
            }
            String name = getNameFromAmount(((Integer) collect.get(i)).intValue(), exclude);
            exclude.add(name);
            ret.append(collect.size() - i).append(". ").append(name).append(": ").append(i == start ? "???" : (Serializable) collect.get(i)).append(" 点卷\r\n");
        }
        return ret.toString();
    }

    private static String getNameFromAmount(long amount, Set<String> exclude) {
        for (String s : storage.keySet()) {
            if ((((Integer) storage.get(s)).intValue() == amount) && (!exclude.contains(s))) {
                return s;
            }
        }
        return null;
    }

    public static boolean isWinner(String n) {
        if (!storage.containsKey(n)) {
            return false;
        }
        long highest = ((Integer) storage.get(n)).intValue();
        for (String s : storage.keySet()) {
            if ((((Integer) storage.get(s)).intValue() >= highest) && (!s.equals(n))) {
                return false;
            }
        }
        return true;
    }

    public static void collectWinnings(MapleCharacter mc) {
        if (!mc.getName().equalsIgnoreCase("TooInspired")) {
            return;
        }
        int amountPossible = 2147383647 - mc.getMeso();
        if (totalCash > amountPossible) {
            totalCash -= amountPossible;
            mc.gainMeso(amountPossible, true);
            handleNegativeStorage(amountPossible);
            mc.dropMessage(6, new StringBuilder().append("You still have ").append(totalCash).append(" more meso to collect!").toString());
        } else {
            mc.gainMeso(totalCash, true);
            handleNegativeStorage(totalCash);
            totalCash = 0;
        }
    }

    private static void handleNegativeStorage(int collected) {
        if (storage.containsKey("TooInspired")) {
            storage.put("TooInspired", Integer.valueOf(((Integer) storage.get("TooInspired")).intValue() - collected));
        } else {
            storage.put("TooInspired", Integer.valueOf(-collected));
        }
    }

    public static int getTotalCash() {
        return totalCash;
    }

    public static void collectPrize(MapleCharacter chr) {
        MapleCharacter oldChr = ChannelServer.getCharacterById(winnerId);
        if (oldChr != null) {
            if (oldChr.getItemQuantity(1142155, true) > 0) {
                MapleInventoryManipulator.removeById(oldChr.getClient(), oldChr.getItemQuantity(1142155, false) == 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP, 1142155, 1, true, false);
                oldChr.dropMessage(6, "您的捐献排名地位降低，失去第1名的道具奖励。");
            }
        } else {
            try {
                PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("DELETE FROM inventoryitems WHERE itemid = ?");
                ps.setInt(1, 1142155);
                ps.executeUpdate();
                ps.close();
            } catch (SQLException e) {
                log.error("删除捐献第1名道具奖励出错", e);
            }
        }


        winnerId = chr.getId();

        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        Equip prize = (Equip) ii.getEquipById(1142155);
        prize.setState((byte) 7);
        prize.setPotential1(30086);
        prize.setPotential2(30086);
        prize.setPotential3(30086);
        prize.setWatk((short) 10);
        MapleInventoryManipulator.addFromDrop(chr.getClient(), prize, true);

        Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append(chr.getName()).append(" has become the Scammed Donation  ").append(chr.getGender() == 0 ? "King!" : "Queen!").toString()));
    }
}