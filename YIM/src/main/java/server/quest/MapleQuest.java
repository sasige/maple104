package server.quest;

import client.MapleCharacter;
import client.MapleQuestStatus;
import constants.GameConstants;
import database.DatabaseConnectionWZ;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import scripting.NPCScriptManager;
import server.ServerProperties;
import tools.MaplePacketCreator;
import tools.Pair;

public class MapleQuest
        implements Serializable {

    private static final long serialVersionUID = 9179541993413738569L;
    private static Map<Integer, MapleQuest> quests = new LinkedHashMap();
    protected int id;
    protected List<MapleQuestRequirement> startReqs = new LinkedList();
    protected List<MapleQuestRequirement> completeReqs = new LinkedList();
    protected List<MapleQuestAction> startActs = new LinkedList();
    protected List<MapleQuestAction> completeActs = new LinkedList();
    protected Map<String, List<Pair<String, Pair<String, Integer>>>> partyQuestInfo = new LinkedHashMap();
    protected Map<Integer, Integer> relevantMobs = new LinkedHashMap();
    private boolean autoStart = false;
    private boolean autoPreComplete = false;
    private boolean repeatable = false;
    private boolean customend = false;
    private boolean blocked = false;
    private boolean autoAccept = false;
    private boolean autoComplete = false;
    private boolean scriptedStart = false;
    private int viewMedalItem = 0;
    private int selectedSkillID = 0;
    protected String name = "";

    public static enum MedalQuest {

        新手冒险家(29005, 29015, 15, new int[]{100000000, 100020400, 100040000, 101000000, 101020300, 101040300, 102000000, 102020500, 102030400, 102040200, 103000000, 103020200, 103030400, 103040000, 104000000, 104020000, 106020100, 120000000, 120020400, 120030000}),
        冰峰雪域山脉探险家(29006, 29012, 50, new int[]{200000000, 200010100, 200010300, 200080000, 200080100, 211000000, 211030000, 211040300, 211041200, 211041800}),
        时间静止之湖探险家(29007, 29012, 40, new int[]{222000000, 222010400, 222020000, 220000000, 220020300, 220040200, 221020701, 221000000, 221030600, 221040400}),
        海底探险家(29008, 29012, 40, new int[]{230000000, 230010400, 230010200, 230010201, 230020000, 230020201, 230030100, 230040000, 230040200, 230040400}),
        武陵探险家(29009, 29012, 50, new int[]{251000000, 251010200, 251010402, 251010500, 250010500, 250010504, 250000000, 250010300, 250010304, 250020300}),
        尼哈沙漠探险家(29010, 29012, 70, new int[]{261030000, 261020401, 261020000, 261010100, 261000000, 260020700, 260020300, 260000000, 260010600, 260010300}),
        神木村探险家(29011, 29012, 70, new int[]{240000000, 240010200, 240010800, 240020401, 240020101, 240030000, 240040400, 240040511, 240040521, 240050000}),
        林中之城探险家(29014, 29015, 50, new int[]{105000000, 105010100, 105020100, 105020300, 105020400, 105030000, 105030100, 105030200, 105030300, 105030500});
        public int questid;
        public int level;
        public int lquestid;
        public int[] maps;

        private MedalQuest(int questid, int lquestid, int level, int[] maps) {
            this.questid = questid;
            this.level = level;
            this.lquestid = lquestid;
            this.maps = maps;
        }
    }

    protected MapleQuest(int id) {
        this.id = id;
    }

    private static MapleQuest loadQuest(ResultSet rs, PreparedStatement psr, PreparedStatement psa, PreparedStatement pss, PreparedStatement psq, PreparedStatement psi, PreparedStatement psp) throws SQLException {
        MapleQuest ret = new MapleQuest(rs.getInt("questid"));
        ret.name = rs.getString("name");
        ret.autoStart = (rs.getInt("autoStart") > 0);
        ret.autoPreComplete = (rs.getInt("autoPreComplete") > 0);
        ret.autoAccept = (rs.getInt("autoAccept") > 0);
        ret.autoComplete = (rs.getInt("autoComplete") > 0);
        ret.viewMedalItem = rs.getInt("viewMedalItem");
        ret.selectedSkillID = rs.getInt("selectedSkillID");
        ret.blocked = (rs.getInt("blocked") > 0);

        psr.setInt(1, ret.id);
        ResultSet rse = psr.executeQuery();
        while (rse.next()) {
            MapleQuestRequirementType type = MapleQuestRequirementType.getByWZName(rse.getString("name"));
            MapleQuestRequirement req = new MapleQuestRequirement(ret, type, rse);
            if (type.equals(MapleQuestRequirementType.interval)) {
                ret.repeatable = true;
            } else if (type.equals(MapleQuestRequirementType.normalAutoStart)) {
                ret.repeatable = true;
                ret.autoStart = true;
            } else if (type.equals(MapleQuestRequirementType.startscript)) {
                ret.scriptedStart = true;
            } else if (type.equals(MapleQuestRequirementType.endscript)) {
                ret.customend = true;
            } else if (type.equals(MapleQuestRequirementType.mob)) {
                for (Pair<Integer, Integer> mob : req.getDataStore()) {
                    ret.relevantMobs.put(mob.left, mob.right);
                }
            }
            if (rse.getInt("type") == 0) {
                ret.startReqs.add(req);
            } else {
                ret.completeReqs.add(req);
            }
        }
        rse.close();

        psa.setInt(1, ret.id);
        rse = psa.executeQuery();
        while (rse.next()) {
            MapleQuestActionType ty = MapleQuestActionType.getByWZName(rse.getString("name"));
            if (rse.getInt("type") == 0) {
                if ((ty == MapleQuestActionType.item) && (ret.id == 7103)) {
                    continue;
                }
                ret.startActs.add(new MapleQuestAction(ty, rse, ret, pss, psq, psi));
            } else {
                if ((ty == MapleQuestActionType.item) && (ret.id == 7102)) {
                    continue;
                }
                ret.completeActs.add(new MapleQuestAction(ty, rse, ret, pss, psq, psi));
            }
        }
        rse.close();

        psp.setInt(1, ret.id);
        rse = psp.executeQuery();
        while (rse.next()) {
            if (!ret.partyQuestInfo.containsKey(rse.getString("rank"))) {
                ret.partyQuestInfo.put(rse.getString("rank"), new ArrayList());
            }
            ((List) ret.partyQuestInfo.get(rse.getString("rank"))).add(new Pair(rse.getString("mode"), new Pair(rse.getString("property"), Integer.valueOf(rse.getInt("value")))));
        }
        rse.close();
        return ret;
    }

    public List<Pair<String, Pair<String, Integer>>> getInfoByRank(String rank) {
        return (List) this.partyQuestInfo.get(rank);
    }

    public boolean isPartyQuest() {
        return this.partyQuestInfo.size() > 0;
    }

    public int getSkillID() {
        return this.selectedSkillID;
    }

    public String getName() {
        return this.name;
    }

    public List<MapleQuestAction> getCompleteActs() {
        return this.completeActs;
    }

    public static void initQuests() {
        try {
            Connection con = DatabaseConnectionWZ.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_questdata");
            PreparedStatement psr = con.prepareStatement("SELECT * FROM wz_questreqdata WHERE questid = ?");
            PreparedStatement psa = con.prepareStatement("SELECT * FROM wz_questactdata WHERE questid = ?");
            PreparedStatement pss = con.prepareStatement("SELECT * FROM wz_questactskilldata WHERE uniqueid = ?");
            PreparedStatement psq = con.prepareStatement("SELECT * FROM wz_questactquestdata WHERE uniqueid = ?");
            PreparedStatement psi = con.prepareStatement("SELECT * FROM wz_questactitemdata WHERE uniqueid = ?");
            PreparedStatement psp = con.prepareStatement("SELECT * FROM wz_questpartydata WHERE questid = ?");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                quests.put(Integer.valueOf(rs.getInt("questid")), loadQuest(rs, psr, psa, pss, psq, psi, psp));
            }
            rs.close();
            ps.close();
            psr.close();
            psa.close();
            pss.close();
            psq.close();
            psi.close();
            psp.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("共加载 " + quests.size() + " 个任务信息.");
    }

    public static MapleQuest getInstance(int id) {
        MapleQuest ret = (MapleQuest) quests.get(Integer.valueOf(id));
        if (ret == null) {
            ret = new MapleQuest(id);
            quests.put(Integer.valueOf(id), ret);
        }
        return ret;
    }

    public static Collection<MapleQuest> getAllInstances() {
        return quests.values();
    }

    public boolean canStart(MapleCharacter chr, Integer npcid) {
        if ((chr.getQuest(this).getStatus() != 0) && ((chr.getQuest(this).getStatus() != 2) || (!this.repeatable))) {
            if ((chr.isAdmin()) && (ServerProperties.ShowPacket()));
            return false;
        }
        if ((this.blocked) && (!chr.isGM())) {
            if ((chr.isAdmin()) && (ServerProperties.ShowPacket()));
            return false;
        }

        for (MapleQuestRequirement r : this.startReqs) {
            if ((r.getType() == MapleQuestRequirementType.dayByDay) && (npcid != null)) {
                forceComplete(chr, npcid.intValue());
                return false;
            }
            if (!r.check(chr, npcid)) {
                if ((chr.isAdmin()) && (ServerProperties.ShowPacket()));
                return false;
            }
        }
        return true;
    }

    public boolean canComplete(MapleCharacter chr, Integer npcid) {
        if (chr.getQuest(this).getStatus() != 1) {
            return false;
        }
        if ((this.blocked) && (!chr.isGM())) {
            return false;
        }
        if ((this.autoComplete) && (npcid != null) && (this.viewMedalItem <= 0)) {
            forceComplete(chr, npcid.intValue());
            return false;
        }
        for (MapleQuestRequirement r : this.completeReqs) {
            if (!r.check(chr, npcid)) {
                return false;
            }
        }
        return true;
    }

    public void RestoreLostItem(MapleCharacter chr, int itemid) {
        if ((this.blocked) && (!chr.isGM())) {
            return;
        }
        for (MapleQuestAction a : this.startActs) {
            if (a.RestoreLostItem(chr, itemid)) {
                break;
            }
        }
    }

    public void start(MapleCharacter chr, int npc) {
        if (((!chr.isAdmin()) || (!ServerProperties.ShowPacket())) || (((this.autoStart) || (checkNPCOnMap(chr, npc))) && (canStart(chr, Integer.valueOf(npc))))) {
            for (MapleQuestAction a : this.startActs) {
                if (!a.checkEnd(chr, null)) {
                    return;
                }
            }
            for (MapleQuestAction a : this.startActs) {
                a.runStart(chr, null);
            }
            if (!this.customend) {
                forceStart(chr, npc, null);
            } else {
                NPCScriptManager.getInstance().endQuest(chr.getClient(), npc, getId(), true);
            }
        }
    }

    public void complete(MapleCharacter chr, int npc) {
        complete(chr, npc, null);
    }

    public void complete(MapleCharacter chr, int npc, Integer selection) {
        if ((chr.getMap() != null) && ((this.autoPreComplete) || (checkNPCOnMap(chr, npc))) && (canComplete(chr, Integer.valueOf(npc)))) {
            for (MapleQuestAction a : this.completeActs) {
                if (!a.checkEnd(chr, selection)) {
                    return;
                }
            }
            forceComplete(chr, npc);
            for (MapleQuestAction a : this.completeActs) {
                a.runEnd(chr, selection);
            }

            chr.getClient().getSession().write(MaplePacketCreator.showSpecialEffect(13));
            chr.getMap().broadcastMessage(chr, MaplePacketCreator.showSpecialEffect(chr.getId(), 13), false);
        }
    }

    public void forfeit(MapleCharacter chr) {
        if (chr.getQuest(this).getStatus() != 1) {
            return;
        }
        MapleQuestStatus oldStatus = chr.getQuest(this);
        MapleQuestStatus newStatus = new MapleQuestStatus(this, 0);
        newStatus.setForfeited(oldStatus.getForfeited() + 1);
        newStatus.setCompletionTime(oldStatus.getCompletionTime());
        chr.updateQuest(newStatus);
    }

    public void forceStart(MapleCharacter chr, int npc, String customData) {
        MapleQuestStatus newStatus = new MapleQuestStatus(this, (byte) 1, npc);
        newStatus.setForfeited(chr.getQuest(this).getForfeited());
        newStatus.setCompletionTime(chr.getQuest(this).getCompletionTime());
        newStatus.setCustomData(customData);
        chr.updateQuest(newStatus);
    }

    public void forceComplete(MapleCharacter chr, int npc) {
        MapleQuestStatus newStatus = new MapleQuestStatus(this, (byte) 2, npc);
        newStatus.setForfeited(chr.getQuest(this).getForfeited());
        chr.updateQuest(newStatus);
    }

    public int getId() {
        return this.id;
    }

    public Map<Integer, Integer> getRelevantMobs() {
        return this.relevantMobs;
    }

    private boolean checkNPCOnMap(MapleCharacter player, int npcid) {
        return ((GameConstants.is龙神(player.getJob())) && (npcid == 1013000)) || ((GameConstants.is恶魔猎手(player.getJob())) && (npcid == 0)) || ((GameConstants.is双弩精灵(player.getJob())) && (npcid == 0)) || (npcid == 2151009) || (npcid == 9010000) || ((npcid >= 2161000) && (npcid <= 2161011)) || (npcid == 9000040) || (npcid == 9000066) || (npcid == 0) || ((player.getMap() != null) && (player.getMap().containsNPC(npcid)));
    }

    public int getMedalItem() {
        return this.viewMedalItem;
    }

    public boolean isBlocked() {
        return this.blocked;
    }

    public boolean hasStartScript() {
        return this.scriptedStart;
    }

    public boolean hasEndScript() {
        return this.customend;
    }
}