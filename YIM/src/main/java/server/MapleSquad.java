package server;

import client.MapleCharacter;
import client.MapleClient;
import handling.channel.ChannelServer;
import handling.channel.PlayerStorage;
import handling.world.World.Find;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;
import server.maps.MapleMap;
import server.maps.MapleMapFactory;
import tools.MaplePacketCreator;
import tools.Pair;

public class MapleSquad {

    private WeakReference<MapleCharacter> leader;
    private String leaderName;
    private String toSay;
    private Map<String, String> members = new LinkedHashMap();
    private Map<String, String> bannedMembers = new LinkedHashMap();
    private int ch;
    private long startTime;
    private int expiration;
    private int beginMapId;
    private MapleSquadType type;
    private byte status = 0;
    private ScheduledFuture<?> removal;

    public MapleSquad(int ch, String type, MapleCharacter leader, int expiration, String toSay) {
        this.leader = new WeakReference(leader);
        this.members.put(leader.getName(), MapleCarnivalChallenge.getJobBasicNameById(leader.getJob()));
        this.leaderName = leader.getName();
        this.ch = ch;
        this.toSay = toSay;
        this.type = MapleSquadType.valueOf(type.toLowerCase());
        this.status = 1;
        this.beginMapId = leader.getMapId();
        leader.getMap().setSquad(this.type);
        if (this.type.queue.get(Integer.valueOf(ch)) == null) {
            this.type.queue.put(Integer.valueOf(ch), new ArrayList());
            this.type.queuedPlayers.put(Integer.valueOf(ch), new ArrayList());
        }
        this.startTime = System.currentTimeMillis();
        this.expiration = expiration;
    }

    public void copy() {
        while ((((ArrayList) this.type.queue.get(Integer.valueOf(this.ch))).size() > 0) && (ChannelServer.getInstance(this.ch).getMapleSquad(this.type) == null)) {
            int index = 0;
            long lowest = 0L;
            for (int i = 0; i < ((ArrayList) this.type.queue.get(Integer.valueOf(this.ch))).size(); i++) {
                if ((lowest == 0L) || (((Long) ((Pair) ((ArrayList) this.type.queue.get(Integer.valueOf(this.ch))).get(i)).right).longValue() < lowest)) {
                    index = i;
                    lowest = ((Long) ((Pair) ((ArrayList) this.type.queue.get(Integer.valueOf(this.ch))).get(i)).right).longValue();
                }
            }
            String nextPlayerId = (String) ((Pair) ((ArrayList) this.type.queue.get(Integer.valueOf(this.ch))).remove(index)).left;
            int theirCh = Find.findChannel(nextPlayerId);
            if (theirCh > 0) {
                MapleCharacter lead = ChannelServer.getInstance(theirCh).getPlayerStorage().getCharacterByName(nextPlayerId);
                if ((lead != null) && (lead.getMapId() == this.beginMapId) && (lead.getClient().getChannel() == this.ch)) {
                    MapleSquad squad = new MapleSquad(this.ch, this.type.name(), lead, this.expiration, this.toSay);
                    if (ChannelServer.getInstance(this.ch).addMapleSquad(squad, this.type.name())) {
                        getBeginMap().broadcastMessage(MaplePacketCreator.getClock(this.expiration / 1000));
                        getBeginMap().broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append(nextPlayerId).append(this.toSay).toString()));
                        ((ArrayList) this.type.queuedPlayers.get(Integer.valueOf(this.ch))).add(new Pair(nextPlayerId, "Success"));
                        break;
                    }
                    squad.clear();
                    ((ArrayList) this.type.queuedPlayers.get(Integer.valueOf(this.ch))).add(new Pair(nextPlayerId, "Skipped"));

                    break;
                }
                if (lead != null) {
                    lead.dropMessage(6, "Your squad has been skipped due to you not being in the right channel and map.");
                }
                getBeginMap().broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append(nextPlayerId).append("'s squad has been skipped due to the player not being in the right channel and map.").toString()));
                ((ArrayList) this.type.queuedPlayers.get(Integer.valueOf(this.ch))).add(new Pair(nextPlayerId, "Not in map"));
            } else {
                getBeginMap().broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append(nextPlayerId).append("'s squad has been skipped due to the player not being online.").toString()));
                ((ArrayList) this.type.queuedPlayers.get(Integer.valueOf(this.ch))).add(new Pair(nextPlayerId, "Not online"));
            }
        }
    }

    public MapleMap getBeginMap() {
        return ChannelServer.getInstance(this.ch).getMapFactory().getMap(this.beginMapId);
    }

    public void clear() {
        if (this.removal != null) {
            getBeginMap().broadcastMessage(MaplePacketCreator.stopClock());
            this.removal.cancel(false);
            this.removal = null;
        }
        this.members.clear();
        this.bannedMembers.clear();
        this.leader = null;
        ChannelServer.getInstance(this.ch).removeMapleSquad(this.type);
        this.status = 0;
    }

    public MapleCharacter getChar(String name) {
        return ChannelServer.getInstance(this.ch).getPlayerStorage().getCharacterByName(name);
    }

    public long getTimeLeft() {
        return this.expiration - (System.currentTimeMillis() - this.startTime);
    }

    public void scheduleRemoval() {
        this.removal = Timer.EtcTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if ((MapleSquad.this.status != 0) && (MapleSquad.this.leader != null) && ((MapleSquad.this.getLeader() == null) || (MapleSquad.this.status == 1))) {
                    MapleSquad.this.clear();
                    MapleSquad.this.copy();
                }
            }
        }, expiration);
    }

    public String getLeaderName() {
        return this.leaderName;
    }

    public List<Pair<String, Long>> getAllNextPlayer() {
        return (List) this.type.queue.get(Integer.valueOf(this.ch));
    }

    public String getNextPlayer() {
        StringBuilder sb = new StringBuilder("\n等待的队员 : ");
        sb.append("#b").append(type.queue.get(ch).size()).append(" #k\r\n ").append("参加者名单 : \n\r ");
        int i = 0;
        for (Pair<String, Long> chr : type.queue.get(this.ch)) {
            i++;
            sb.append(i).append(" : ").append((String) chr.left);
            sb.append(" \n\r ");
        }
        sb.append("你是愿意留在这里排队,还是回去了呢?");
        return sb.toString();
    }

    public void setNextPlayer(String i) {
        Pair toRemove = null;
        for (Pair<String, Long> s : type.queue.get(this.ch)) {
            if (((String) s.left).equals(i)) {
                toRemove = s;
                break;
            }
        }
        if (toRemove != null) {
            ((ArrayList) this.type.queue.get(Integer.valueOf(this.ch))).remove(toRemove);
            return;
        }
        for (ArrayList<Pair<String, Long>> v : this.type.queue.values()) {
            for (Pair<String, Long> s : v) {
                if (s.left.equals(i)) {
                    return;
                }
            }
        }
        ((this.type.queue.get(ch))).add(new Pair(i, System.currentTimeMillis()));
    }

    public MapleCharacter getLeader() {
        if ((this.leader == null) || (this.leader.get() == null)) {
            if ((this.members.size() > 0) && (getChar(this.leaderName) != null)) {
                this.leader = new WeakReference(getChar(this.leaderName));
            } else {
                if (this.status != 0) {
                    clear();
                }
                return null;
            }
        }
        return (MapleCharacter) this.leader.get();
    }

    public boolean containsMember(MapleCharacter member) {
        for (String mmbr : this.members.keySet()) {
            if (mmbr.equalsIgnoreCase(member.getName())) {
                return true;
            }
        }
        return false;
    }

    public List<String> getMembers() {
        return new LinkedList(this.members.keySet());
    }

    public List<String> getBannedMembers() {
        return new LinkedList(this.bannedMembers.keySet());
    }

    public int getSquadSize() {
        return this.members.size();
    }

    public boolean isBanned(MapleCharacter member) {
        return this.bannedMembers.containsKey(member.getName());
    }

    public int addMember(MapleCharacter member, boolean join) {
        if (getLeader() == null) {
            return -1;
        }
        String job = MapleCarnivalChallenge.getJobBasicNameById(member.getJob());
        if (join) {
            if ((!containsMember(member)) && (!getAllNextPlayer().contains(member.getName()))) {
                if (this.members.size() <= 30) {
                    this.members.put(member.getName(), job);
                    getLeader().dropMessage(5, new StringBuilder().append(member.getName()).append(" (").append(job).append(") 加入了远征队.").toString());
                    return 1;
                }
                return 2;
            }
            return -1;
        }
        if (containsMember(member)) {
            this.members.remove(member.getName());
            getLeader().dropMessage(5, new StringBuilder().append(member.getName()).append(" (").append(job).append(") 离开了远征队.").toString());
            return 1;
        }
        return -1;
    }

    public void acceptMember(int pos) {
        if ((pos < 0) || (pos >= this.bannedMembers.size())) {
            return;
        }
        List membersAsList = getBannedMembers();
        String toadd = (String) membersAsList.get(pos);
        if ((toadd != null) && (getChar(toadd) != null)) {
            this.members.put(toadd, this.bannedMembers.get(toadd));
            this.bannedMembers.remove(toadd);
            getChar(toadd).dropMessage(5, new StringBuilder().append(getLeaderName()).append(" 将你列为远征队队员.").toString());
        }
    }

    public void reAddMember(MapleCharacter chr) {
        removeMember(chr);
        this.members.put(chr.getName(), MapleCarnivalChallenge.getJobBasicNameById(chr.getJob()));
    }

    public void removeMember(MapleCharacter chr) {
        if (this.members.containsKey(chr.getName())) {
            this.members.remove(chr.getName());
        }
    }

    public void removeMember(String chr) {
        if (this.members.containsKey(chr)) {
            this.members.remove(chr);
        }
    }

    public void banMember(int pos) {
        if ((pos <= 0) || (pos >= this.members.size())) {
            return;
        }
        List membersAsList = getMembers();
        String toban = (String) membersAsList.get(pos);
        if ((toban != null) && (getChar(toban) != null)) {
            this.bannedMembers.put(toban, this.members.get(toban));
            this.members.remove(toban);
            getChar(toban).dropMessage(5, new StringBuilder().append(getLeaderName()).append(" 将你请出远征队，目前无法加入远征队.").toString());
        }
    }

    public void setStatus(byte status) {
        this.status = status;
        if ((status == 2) && (this.removal != null)) {
            this.removal.cancel(false);
            this.removal = null;
        }
    }

    public int getStatus() {
        return this.status;
    }

    public int getBannedMemberSize() {
        return this.bannedMembers.size();
    }

    public String getSquadMemberString(byte type) {
        switch (type) {
            case 0: {
                StringBuilder sb = new StringBuilder("总共 : ");
                sb.append("#b").append(this.members.size()).append(" #k ").append("个远征队成员 : \n\r ");
                int i = 0;
                for (Map.Entry chr : this.members.entrySet()) {
                    i++;
                    sb.append(i).append(" : ").append((String) chr.getKey()).append(" (").append((String) chr.getValue()).append(") ");
                    if (i == 1) {
                        sb.append("(远征队队长)");
                    }
                    sb.append(" \n\r ");
                }
                while (i < 30) {
                    i++;
                    sb.append(i).append(" : ").append(" \n\r ");
                }
                return sb.toString();
            }
            case 1: {
                StringBuilder sb = new StringBuilder("总共 : ");
                sb.append("#b").append(this.members.size()).append(" #n ").append("个远征队成员 : \n\r ");
                int i = 0;
                int selection = 0;
                for (Map.Entry chr : this.members.entrySet()) {
                    i++;
                    sb.append("#b#L").append(selection).append("#");
                    selection++;
                    sb.append(i).append(" : ").append((String) chr.getKey()).append(" (").append((String) chr.getValue()).append(") ");
                    if (i == 1) {
                        sb.append("(远征队队长)");
                    }
                    sb.append("#l").append(" \n\r ");
                }
                while (i < 30) {
                    i++;
                    sb.append(i).append(" : ").append(" \n\r ");
                }
                return sb.toString();
            }
            case 2: {
                StringBuilder sb = new StringBuilder("总共 : ");
                sb.append("#b").append(this.members.size()).append(" #n ").append("个远征队成员 : \n\r ");
                int i = 0;
                int selection = 0;
                for (Map.Entry chr : this.bannedMembers.entrySet()) {
                    i++;
                    sb.append("#b#L").append(selection).append("#");
                    selection++;
                    sb.append(i).append(" : ").append((String) chr.getKey()).append(" (").append((String) chr.getValue()).append(") ");
                    sb.append("#l").append(" \n\r ");
                }
                while (i < 30) {
                    i++;
                    sb.append(i).append(" : ").append(" \n\r ");
                }
                return sb.toString();
            }
            case 3: {
                StringBuilder sb = new StringBuilder("Jobs : ");
                Map<String, Integer> jobs = getJobs();
                for (Entry<String, Integer> chr : jobs.entrySet()) {
                    sb.append("\r\n").append((String) chr.getKey()).append(" : ").append(chr.getValue());
                }
                return sb.toString();
            }
        }
        return null;
    }

    public MapleSquadType getType() {
        return this.type;
    }

    public Map<String, Integer> getJobs() {
        Map jobs = new LinkedHashMap();
        for (Map.Entry chr : this.members.entrySet()) {
            if (jobs.containsKey(chr.getValue())) {
                jobs.put(chr.getValue(), Integer.valueOf(((Integer) jobs.get(chr.getValue())).intValue() + 1));
            } else {
                jobs.put(chr.getValue(), Integer.valueOf(1));
            }
        }
        return jobs;
    }

//进入副本的人数
    public static enum MapleSquadType {

        
        
        bossbalrog(ChannelServer.getBossbalrog()), 
        chaoszak(ChannelServer.getChaoszak()), 
        pinkzak(ChannelServer.getPinkzak()), 
        horntail(ChannelServer.getHorntail()), 
        chaosht(ChannelServer.getChaosht()), 
        pinkbean(ChannelServer.getPinkbean()), 

        zak(ChannelServer.getZak()), 
        
        
        vonleon(ChannelServer.getVonleon()), 
        
        akayile(ChannelServer.getAkayile()), 
        xila(ChannelServer.getXila()), 
        baojun(ChannelServer.getBaojun()), 
        nmm_squad(ChannelServer.getNmm_squad()), 
        vergamot(ChannelServer.getVergamot()), 
        dunas(ChannelServer.getDunas()), 
        
        nibergen_squad(ChannelServer.getNibergen_squad()), 
        dunas2(ChannelServer.getDunas2()), 
        core_blaze(ChannelServer.getCore_blaze()), 
        aufheben(ChannelServer.getAufheben()), 
        cwkpq(ChannelServer.getCwkpq()), 
        tokyo_2095(ChannelServer.getTokyo_2095()), 
        scartar(ChannelServer.getScartar()), 
        allboss(ChannelServer.getAllboss()), 
        

        cygnus(ChannelServer.getCygnus());
        
      //  bossbalrog(2),
       // zak(2),
      //  chaoszak(3),
      //  pinkzak(3),
      //  horntail(2),
      //  akayile(2),
    //    xila(2),
      //  chaosht(3),
     //   pinkbean(3),
     //   nmm_squad(2),
    //    vergamot(2),
    //    dunas(2),
     //   nibergen_squad(2),
    //    dunas2(2),
      //  core_blaze(2),
    //    aufheben(2),
     //   cwkpq(10),
      //  tokyo_2095(2),
     //   vonleon(3),
      //  scartar(2);
       // cygnus(3);
        public int i;
        public HashMap<Integer, ArrayList<Pair<String, String>>> queuedPlayers = new HashMap();
        public HashMap<Integer, ArrayList<Pair<String, Long>>> queue = new HashMap();

        private MapleSquadType(int i) {
            this.i = i;
        }
    }
}