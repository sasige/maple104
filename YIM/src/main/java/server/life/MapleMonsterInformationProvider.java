package server.life;

import client.inventory.MapleInventoryType;
import constants.GameConstants;
import database.DatabaseConnection;
import java.io.File;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import server.MapleItemInformationProvider;
import server.StructFamiliar;

public class MapleMonsterInformationProvider {

    private static final MapleMonsterInformationProvider instance = new MapleMonsterInformationProvider();
    private final Map<Integer, ArrayList<MonsterDropEntry>> drops = new HashMap();
    private final List<MonsterGlobalDropEntry> globaldrops = new ArrayList();
    private static final MapleDataProvider stringDataWZ = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("wzpath") + "/String.wz"));
    private static final MapleData mobStringData = stringDataWZ.getData("MonsterBook.img");

    public static MapleMonsterInformationProvider getInstance() {
        return instance;
    }

    public List<MonsterGlobalDropEntry> getGlobalDrop() {
        return globaldrops;
    }

    public void load() {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            Connection con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT * FROM drop_data_global WHERE chance > 0");
            rs = ps.executeQuery();

            while (rs.next()) {
                globaldrops.add(new MonsterGlobalDropEntry(rs.getInt("itemid"), rs.getInt("chance"), rs.getInt("continent"), rs.getByte("dropType"), rs.getInt("minimum_quantity"), rs.getInt("maximum_quantity"), rs.getInt("questid")));
            }

            rs.close();
            ps.close();

            ps = con.prepareStatement("SELECT dropperid FROM drop_data");
            List<Integer> mobIds = new ArrayList<Integer>();
            rs = ps.executeQuery();
            while (rs.next()) {
                if (!mobIds.contains(rs.getInt("dropperid"))) {
                    loadDrop(rs.getInt("dropperid"));
                    mobIds.add(rs.getInt("dropperid"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving drop" + e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ignore) {
            }
        }
    }

    public ArrayList<MonsterDropEntry> retrieveDrop(int monsterId) {
        return (ArrayList) drops.get(Integer.valueOf(monsterId));
    }

    private void loadDrop(final int monsterId) {
        final ArrayList<MonsterDropEntry> ret = new ArrayList<MonsterDropEntry>();

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            MapleMonsterStats mons = MapleLifeFactory.getMonsterStats(monsterId);
            if (mons == null) {
                return;
            }
            ps = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM drop_data WHERE dropperid = ?");
            ps.setInt(1, monsterId);
            rs = ps.executeQuery();

            boolean doneMesos = false;
            while (rs.next()) {
                int itemid = rs.getInt("itemid");
                int chance = rs.getInt("chance");
                if (GameConstants.getInventoryType(itemid) == MapleInventoryType.EQUIP) {
                    chance *= 10;
                }
                if (itemid / 10000 == 238) {
                    continue;
                }
                ret.add(new MonsterDropEntry(itemid, chance, rs.getInt("minimum_quantity"), rs.getInt("maximum_quantity"), rs.getInt("questid")));

                if (itemid == 0) {
                    doneMesos = true;
                }
            }
            if (!doneMesos) {
                addMeso(mons, ret);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving drop" + e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ignore) {
                return;
            }
        }
        drops.put(monsterId, ret);
    }

    public void addExtra() {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        for (Entry<Integer, ArrayList<MonsterDropEntry>> e : drops.entrySet()) {
            for (int i = 0; i < e.getValue().size(); i++) {
                if (e.getValue().get(i).itemId != 0 && !ii.itemExists(e.getValue().get(i).itemId)) {
                    e.getValue().remove(i);
                }
            }
            MapleMonsterStats mons = MapleLifeFactory.getMonsterStats(e.getKey());
            Integer item = ii.getItemIdByMob(e.getKey());
            if ((item != null) && (item.intValue() > 0)) {
                if (item.intValue() / 10000 == 238) {
                    continue;
                }
                e.getValue().add(new MonsterDropEntry(item, mons.isBoss() ? 1000000 : 10000, 1, 1, 0));
            }
            StructFamiliar f = ii.getFamiliarByMob(e.getKey());
            if (f != null) {
                if (f.itemid / 10000 == 238) {
                    continue;
                }
                e.getValue().add(new MonsterDropEntry(f.itemid, mons.isBoss() ? 10000 : 100, 1, 1, 0));
            }
        }
        for (Entry<Integer, Integer> i : ii.getMonsterBook().entrySet()) {
            if (!drops.containsKey(i.getKey())) {
                MapleMonsterStats mons = MapleLifeFactory.getMonsterStats(i.getKey());
                ArrayList<MonsterDropEntry> e = new ArrayList<MonsterDropEntry>();
                if (i.getValue() / 10000 == 238) {
                    continue;
                }
                e.add(new MonsterDropEntry( i.getValue(), mons.isBoss() ? 1000000 : 10000, 1, 1, 0));
                StructFamiliar f = ii.getFamiliarByMob(((Integer) i.getKey()).intValue());
                if (f != null) {
                    if (f.itemid / 10000 == 238) {
                        continue;
                    }
                    e.add(new MonsterDropEntry(f.itemid, mons.isBoss() ? 10000 : 100, 1, 1, 0));
                }
                addMeso(mons, e);
                drops.put(i.getKey(), e);
            }
        }
        for (StructFamiliar f : ii.getFamiliars().values()) {
            if (!drops.containsKey(f.mob)) {
                MapleMonsterStats mons = MapleLifeFactory.getMonsterStats(f.mob);
                ArrayList<MonsterDropEntry> e = new ArrayList<MonsterDropEntry>();
                if (f.itemid / 10000 == 238) {
                    continue;
                }
                e.add(new MonsterDropEntry(f.itemid, mons.isBoss() ? 10000 : 100, 1, 1, 0));
                addMeso(mons, e);
                drops.put(f.mob, e);
            }

        }

        for (Entry<Integer, ArrayList<MonsterDropEntry>> e : drops.entrySet()) {
            if ((e.getKey() != 9400408) && (mobStringData.getChildByPath(String.valueOf(e.getKey())) != null)) {
                for (MapleData d : mobStringData.getChildByPath(e.getKey() + "/reward")) {
                    int toAdd = MapleDataTool.getInt(d, 0);
                    if ((toAdd > 0) && (!contains(e.getValue(), toAdd)) && (ii.itemExists(toAdd))) {
                        if ((toAdd / 10000 == 238) || (toAdd / 10000 == 243) || (toAdd / 10000 == 399) || (toAdd == 4001126) || (toAdd == 4001128) || (toAdd == 4001246) || (toAdd == 4001473) || (toAdd == 4001447) || (toAdd == 2022450) || (toAdd == 2022451) || (toAdd == 2022452) || (toAdd == 4032302) || (toAdd == 4032303) || (toAdd == 4032304)) {
                            continue;
                        }

                         e.getValue().add(new MonsterDropEntry(toAdd, chanceLogic(toAdd), 1, 1, 0));
                    }
                }
            }
        }
    }

    public void addMeso(MapleMonsterStats mons, ArrayList<MonsterDropEntry> ret) {
        double divided = mons.getLevel() < 100 ? 10.0D : mons.getLevel() < 10 ? mons.getLevel() : mons.getLevel() / 10.0D;
        int max = (mons.isBoss()) && (!mons.isPartyBonus()) ? mons.getLevel() * mons.getLevel() : mons.getLevel() * (int) Math.ceil(mons.getLevel() / divided);
        for (int i = 0; i < mons.dropsMeso(); i++) {
            if ((mons.getId() >= 9600086) && (mons.getId() <= 9600098)) {
                ret.add(new MonsterDropEntry(0, mons.isPartyBonus() ? 25000 : mons.isBoss() && !mons.isPartyBonus() ? 800000 : 50000, (int) Math.floor(0.66D * max), max, 0));
            } else {
                ret.add(new MonsterDropEntry(0, mons.isPartyBonus() ? 50000 : mons.isBoss() && !mons.isPartyBonus() ? 800000 : 80000, (int) Math.floor(0.66D * max), max, 0));
            }
        }
    }

    public void clearDrops() {
        drops.clear();
        globaldrops.clear();
        load();
        addExtra();
    }

    public boolean contains(ArrayList<MonsterDropEntry> e, int toAdd) {
        for (MonsterDropEntry f : e) {
            if (f.itemId == toAdd) {
                return true;
            }
        }
        return false;
    }

    public int chanceLogic(int itemId) {
        switch (itemId) {
            case 2049301:
            case 2049401:
            case 4280000:
            case 4280001:
            case 4280005:
            case 4280006:
                return 5000;
            case 1002419:
            case 2049300:
            case 2049400:
                return 2000;
            case 1002938:
                return 50;
        }
        if (GameConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP) {
            return 8000;
        }
        if (GameConstants.getInventoryType(itemId) == MapleInventoryType.SETUP || GameConstants.getInventoryType(itemId) == MapleInventoryType.CASH) {
            return 500;
        }
        switch (itemId / 10000) {
            case 204:
                return 1800;
            case 207:
            case 233:
                return 3000;
            case 229:
                return 400;
            case 401:
            case 402:
                return 5000;
            case 403:
                return 4000;
        }
        return 8000;
    }
}