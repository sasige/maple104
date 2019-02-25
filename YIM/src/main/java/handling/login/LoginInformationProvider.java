package handling.login;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import server.ServerProperties;
import tools.Triple;

public class LoginInformationProvider {

    private final static LoginInformationProvider instance = new LoginInformationProvider();
    protected final List<String> ForbiddenName = new ArrayList<String>();
    protected final List<String> Curse = new ArrayList<String>();
    protected final Map<Triple<Integer, Integer, Integer>, List<Integer>> makeCharInfo = new HashMap<Triple<Integer, Integer, Integer>, List<Integer>>();

    public static LoginInformationProvider getInstance() {
        return instance;
    }

    protected LoginInformationProvider() {
       final String WZpath = System.getProperty("wzpath");
       final MapleDataProvider prov = MapleDataProviderFactory.getDataProvider(new File(WZpath + "/Etc.wz"));
        MapleData nameData = prov.getData("ForbiddenName.img");
        for (final MapleData data : nameData.getChildren()) {
            ForbiddenName.add(MapleDataTool.getString(data));
        }
        nameData = prov.getData("Curse.img");
        for (final MapleData data : nameData.getChildren()) {
            Curse.add(MapleDataTool.getString(data).split(",")[0]);
            ForbiddenName.add(MapleDataTool.getString(data).split(",")[0]);
        }
        final MapleData infoData = prov.getData("MakeCharInfo.img");

        for (MapleData dat : infoData) {
            int type;
            int val = 0;
            try {
                type = JobType.getById(Integer.parseInt(dat.getName())).type;

                for (MapleData d : dat) {
                    if (d.getName().equals("male")) {
                        val = 0;
                    } else if (d.getName().equals("female")) {
                        val = 1;
                    }

                    for (MapleData da : d) {
                        final Triple<Integer, Integer, Integer> key = new Triple<Integer, Integer, Integer>(val, Integer.parseInt(da.getName()), type);
                        List<Integer> our = (List) makeCharInfo.get(key);
                        if (our == null) {
                            our = new ArrayList<Integer>();
                            makeCharInfo.put(key, our);
                        }
                        for (MapleData dd : da) {
                            if (!dd.getName().equals("name")) {
                                our.add(MapleDataTool.getInt(dd, -1));
                            }
                        }
                    }
                }
            } catch (NumberFormatException e) {
            }
        }
        final MapleData uA = infoData.getChildByPath("UltimateAdventurer");
        for (MapleData dat : uA) {
            final Triple<Integer, Integer, Integer> key = new Triple<Integer, Integer, Integer>(-1, Integer.parseInt(dat.getName()), JobType.终极冒险家.type);
            List<Integer> our = makeCharInfo.get(key);
            if (our == null) {
                our = new ArrayList<Integer>();
                makeCharInfo.put(key, our);
            }
            for (MapleData d : dat) {
                our.add(MapleDataTool.getInt(d, -1));
            }
        }

    }

    public final boolean isForbiddenName(final String in) {
        for (final String name : this.ForbiddenName) {
            if (in.toLowerCase().contains(name.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public final boolean isCurseMsg(final String in) {
        for (final String name : this.Curse) {
            if (in.toLowerCase().contains(name.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public final boolean isEligibleItem(final int gender, final int val, final int job, final int item) {
        if (item < 0) {
            return false;
        }
        final Triple<Integer, Integer, Integer> key = new Triple<Integer, Integer, Integer>(gender, val, job);
        final List<Integer> our = makeCharInfo.get(key);
        if (our == null) {
            return false;
        }
        return our.contains(item);
    }

    public static enum JobType {

        反抗者(0, "Resistance", 3000, 931000000),
        冒险家(1, "", 0, 0),
        骑士团(2, "Premium", 1000, 913040000),
        战神(3, "Orient", 2000, 914000000),
        龙神(4, "Evan", 2001, 900090000),
        双弩精灵(5, "", 2002, 910150000),
        恶魔猎手(6, "", 3001, 931050310),
        幻影(7, "", 2003, 910000000),
        龙的传人(8, "", 508, 910000000),
        米哈尔(9, "", 5000, 910000000), 
        夜光法师(10, "Luminous", 2004, 910000000), 
        狂龙战士(11, "Kaiser", 6000, 910000000), 
        爆莉萌天使(12, "Angelic", 6500, 910000000), 
        终极冒险家(-1, "Ultimate", 0, 130000000);
        public int type, id, map;
        public String job;
        private boolean 自由市场 = true;

        private JobType(int type, String job, int id, int map) {
            this.type = type;
            this.job = job;
            this.id = id;
            
            
            //新手出生地图设置
            
            this.map = (ServerProperties.getProperty("world.firstMap") != null ? Integer.parseInt(ServerProperties.getProperty("world.firstMap")) : 910000000);
            //this.map = (this.自由市场 ? 910000000 : map);
            
        }

        public static JobType getByJob(String g) {
            for (JobType e : values()) {
                if ((e.job.length() > 0) && (g.startsWith(e.job))) {
                    return e;
                }
            }
            return 冒险家;
        }

        public static JobType getByType(int g) {
            for (JobType e : values()) {
                if (e.type == g) {
                    return e;
                }
            }
            return 冒险家;
        }

        public static JobType getById(int g) {
            for (JobType e : values()) {
                if (e.id == g) {
                    return e;
                }
            }
            return 冒险家;
        }
    }
}