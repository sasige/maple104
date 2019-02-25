package server;

import constants.GameConstants;
import database.DatabaseConnection;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import server.CashItemInfo.CashModInfo;

public class CashItemFactory {

    private static CashItemFactory instance = new CashItemFactory();
    private static int[] bestItems = {GameConstants.GMS ? 10003055 : 10002819, GameConstants.GMS ? 10003090 : 50100010, GameConstants.GMS ? 10103464 : 50200001, GameConstants.GMS ? 10002960 : 10002147, GameConstants.GMS ? 10103363 : 60000073};
    private Map<Integer, CashItemInfo> itemStats = new HashMap<Integer, CashItemInfo>();
    private Map<Integer, List<Integer>> itemPackage = new HashMap<Integer, List<Integer>>();
    private Map<Integer, CashModInfo> itemMods = new HashMap<Integer, CashModInfo>();
    private MapleDataProvider data = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("wzpath") + "/Etc.wz"));
    private MapleData commodities = this.data.getData("Commodity.img");
    private Map<Integer, Integer> idLookup = new HashMap();

    public static CashItemFactory getInstance() {
        return instance;
    }

    public void initialize() {
        final List<MapleData> cccc = commodities.getChildren();
        for (MapleData field : cccc) {
            final int SN = MapleDataTool.getIntConvert("SN", field, 0);
            final int itemId = MapleDataTool.getIntConvert("ItemId", field, 0);
            final int count = MapleDataTool.getIntConvert("Count", field, 1);
            final int price = MapleDataTool.getIntConvert("Price", field, 0);
            final int period = MapleDataTool.getIntConvert("Period", field, 0);
            final int gender = MapleDataTool.getIntConvert("Gender", field, 2);
            final boolean onSale = MapleDataTool.getIntConvert("OnSale", field, 0) > 0;

            CashItemInfo stats = new CashItemInfo(itemId, count, price, SN, period, gender, onSale);
            if (SN > 0) {
                itemStats.put(SN, stats);
                idLookup.put(itemId, SN);
            }
        }
        final MapleData b = data.getData("CashPackage.img");
        for (MapleData c : b.getChildren()) {
            if (c.getChildByPath("SN") == null) {
                continue;
            }
            final List<Integer> packageItems = new ArrayList<Integer>();
            for (MapleData d : c.getChildByPath("SN").getChildren()) {
                packageItems.add(MapleDataTool.getIntConvert(d));
            }
            itemPackage.put(Integer.parseInt(c.getName()), packageItems);
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM cashshop_modified_items");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                CashModInfo ret = new CashModInfo(
                        rs.getInt("serial"), rs.getInt("discount_price"), rs.getInt("mark"), 
                        rs.getInt("showup") > 0, rs.getInt("itemid"), rs.getInt("priority"), 
                        rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), 
                        rs.getInt("count"), rs.getInt("meso"), rs.getInt("unk_1"), rs.getInt("unk_2"), 
                        rs.getInt("unk_3"), rs.getInt("extra_flags"));
                itemMods.put(ret.sn, ret);
                if (ret.showUp) {
                    final CashItemInfo cc = itemStats.get(ret.sn);
                    if (cc != null) {
                        ret.toCItem(cc);
                    }
                }
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CashItemInfo getSimpleItem(int sn) {
        return itemStats.get(sn);
    }

    public CashItemInfo getItem(int sn) {
        final CashItemInfo stats = itemStats.get(sn);
        final CashModInfo z = getModInfo(sn);

        if ((z != null) && (z.showUp)) {
            return z.toCItem(stats);
        }
        if (stats == null || !stats.onSale()) {
            return null;
        }

        return stats;
    }

    public List<Integer> getPackageItems(int itemId) {
        return itemPackage.get(itemId);
    }

    public CashItemInfo.CashModInfo getModInfo(int sn) {
        return itemMods.get(sn);
    }

    public Collection<CashItemInfo.CashModInfo> getAllModInfo() {
        return itemMods.values();
    }

    public int[] getBestItems() {
        return bestItems;
    }

    public int getSnFromId(int itemId) {
        return idLookup.get(itemId);
    }
}