package scripting;

import client.MapleCharacter;
import client.MapleClient;
import handling.channel.ChannelServer;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import handling.world.World;
import java.awt.Point;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import javax.script.Invocable;
import javax.script.ScriptException;
import server.MapleItemInformationProvider;
import server.MapleSquad;
import server.Randomizer;
import server.Timer.EventTimer;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.OverrideMonsterStats;
import server.maps.*;
import tools.FileoutputUtil;
import tools.MaplePacketCreator;

public class EventManager {

    private static int[] eventChannel = new int[2];
    private Invocable iv;
    private int channel;
    private Map<String, EventInstanceManager> instances = new WeakHashMap();
    private Properties props = new Properties();
    private String name;
    private MapleCharacter chr;
    protected MapleClient c;

    public EventManager(ChannelServer cserv, Invocable iv, String name) {
        this.iv = iv;
        this.channel = cserv.getChannel();
        this.name = name;
    }

    public void cancel() {
        try {
            this.iv.invokeFunction("cancelSchedule", (Object) null);
        } catch (Exception ex) {
            System.out.println(new StringBuilder().append("Event name : ").append(this.name).append(", method Name : cancelSchedule:\n").append(ex).toString());
            FileoutputUtil.log("log\\Script_Except.log", new StringBuilder().append("Event name : ").append(this.name).append(", method Name : cancelSchedule:\n").append(ex).toString());
        }
    }

    public ScheduledFuture<?> schedule(final String methodName, long delay) {
        return EventTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                try {
                    EventManager.this.iv.invokeFunction(methodName, (Object) null);
                } catch (Exception ex) {
                    System.out.println("Event name : " + EventManager.this.name + ", method Name : " + methodName + ":\n" + ex);
                    FileoutputUtil.log("log\\Script_Except.log", "Event name : " + EventManager.this.name + ", method Name : " + methodName + ":\n" + ex);
                }
            }
        }, delay);
    }
    
        public int getHour() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        return hour;
    }

    public ScheduledFuture<?> schedule(final String methodName, long delay, final EventInstanceManager eim) {
        return EventTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                try {
                    iv.invokeFunction(methodName, eim);
                } catch (Exception ex) {
                    System.out.println("Event name : " + EventManager.this.name + ", method Name : " + methodName + ":\n" + ex);
                    FileoutputUtil.log("log\\Script_Except.log", "Event name : " + EventManager.this.name + ", method Name : " + methodName + ":\n" + ex);
                }
            }
        }, delay);
    }

    public ScheduledFuture<?> scheduleAtTimestamp(final String methodName, long timestamp) {
        return EventTimer.getInstance().scheduleAtTimestamp(new Runnable() {

            @Override
            public void run() {
                try {
                    EventManager.this.iv.invokeFunction(methodName, (Object) null);
                } catch (ScriptException ex) {
                    System.out.println("Event name : " + EventManager.this.name + ", method Name : " + methodName + ":\n" + ex);
                } catch (NoSuchMethodException ex) {
                    System.out.println("Event name : " + EventManager.this.name + ", method Name : " + methodName + ":\n" + ex);
                }
            }
        }, timestamp);
    }

    public int getChannel() {
        return this.channel;
    }

    public ChannelServer getChannelServer() {
        return ChannelServer.getInstance(this.channel);
    }

    public EventInstanceManager getInstance(String name) {
        return (EventInstanceManager) this.instances.get(name);
    }

    public Collection<EventInstanceManager> getInstances() {
        return Collections.unmodifiableCollection(this.instances.values());
    }

    public EventInstanceManager newInstance(String name) {
        EventInstanceManager ret = new EventInstanceManager(this, name, this.channel);
        this.instances.put(name, ret);
        return ret;
    }

    public void disposeInstance(String name) {
        this.instances.remove(name);
        if ((getProperty("state") != null) && (this.instances.isEmpty())) {
            setProperty("state", "0");
        }
        if ((getProperty("leader") != null) && (this.instances.isEmpty()) && (getProperty("leader").equals("false"))) {
            setProperty("leader", "true");
        }
        if (this.name.equals("CWKPQ")) {
            MapleSquad squad = ChannelServer.getInstance(this.channel).getMapleSquad("CWKPQ");
            if (squad != null) {
                squad.clear();
                squad.copy();
            }
        }
    }

    public Invocable getIv() {
        return this.iv;
    }

    public void setProperty(String key, String value) {
        this.props.setProperty(key, value);
    }

    public String getProperty(String key) {
        return this.props.getProperty(key);
    }

    public final Properties getProperties() {
        return this.props;
    }

    public String getName() {
        return this.name;
    }

    public void startInstance() {
        try {
            this.iv.invokeFunction("setup", new Object[]{(Object) null});
        } catch (Exception ex) {
            FileoutputUtil.log("log\\Script_Except.log", new StringBuilder().append("Event name : ").append(this.name).append(", method Name : setup:\n").append(ex).toString());
        }
    }

    public void startInstance_Solo(String mapid, MapleCharacter chr) {
        try {
            EventInstanceManager eim = (EventInstanceManager) this.iv.invokeFunction("setup", mapid);
            eim.registerPlayer(chr);
        } catch (Exception ex) {
            FileoutputUtil.log("log\\Script_Except.log", new StringBuilder().append("Event name : ").append(this.name).append(", method Name : setup:\n").append(ex).toString());
        }
    }

    public void startInstance(String mapid, MapleCharacter chr) {
        try {
            EventInstanceManager eim = (EventInstanceManager) this.iv.invokeFunction("setup", mapid);
            eim.registerCarnivalParty(chr, chr.getMap(), (byte) 0);
        } catch (Exception ex) {
            FileoutputUtil.log("log\\Script_Except.log", new StringBuilder().append("Event name : ").append(this.name).append(", method Name : setup:\n").append(ex).toString());
        }
    }

    public void startInstance_Party(String mapid, MapleCharacter chr) {
        try {
            EventInstanceManager eim = (EventInstanceManager) this.iv.invokeFunction("setup", mapid);
            eim.registerParty(chr.getParty(), chr.getMap());
        } catch (Exception ex) {
            FileoutputUtil.log("log\\Script_Except.log", new StringBuilder().append("Event name : ").append(this.name).append(", method Name : setup:\n").append(ex).toString());
        }
    }

    public void startInstance(MapleCharacter character, String leader) {
        try {
            EventInstanceManager eim = (EventInstanceManager) this.iv.invokeFunction("setup", (Object) null);
            eim.registerPlayer(character);
            eim.setProperty("leader", leader);
            eim.setProperty("guildid", String.valueOf(character.getGuildId()));
            setProperty("guildid", String.valueOf(character.getGuildId()));
        } catch (Exception ex) {
            System.out.println(new StringBuilder().append("Event name : ").append(this.name).append(", method Name : setup-Guild:\n").append(ex).toString());
            FileoutputUtil.log("log\\Script_Except.log", new StringBuilder().append("Event name : ").append(this.name).append(", method Name : setup-Guild:\n").append(ex).toString());
        }
    }

    public void startInstance_CharID(MapleCharacter character) {
        try {
            EventInstanceManager eim = (EventInstanceManager) iv.invokeFunction("setup", character.getId());
            eim.registerPlayer(character);
        } catch (Exception ex) {
            System.out.println(new StringBuilder().append("Event name : ").append(this.name).append(", method Name : setup-CharID:\n").append(ex).toString());
            FileoutputUtil.log("log\\Script_Except.log", new StringBuilder().append("Event name : ").append(this.name).append(", method Name : setup-CharID:\n").append(ex).toString());
        }
    }

    public void startInstance_CharMapID(MapleCharacter character) {
        try {
            EventInstanceManager eim = (EventInstanceManager) iv.invokeFunction("setup", character.getId(), character.getMapId());
            eim.registerPlayer(character);
        } catch (Exception ex) {
            System.out.println(new StringBuilder().append("Event name : ").append(this.name).append(", method Name : setup-CharID:\n").append(ex).toString());
            FileoutputUtil.log("log\\Script_Except.log", new StringBuilder().append("Event name : ").append(this.name).append(", method Name : setup-CharID:\n").append(ex).toString());
        }
    }

    public void startInstance(MapleCharacter character) {
        try {
            EventInstanceManager eim = (EventInstanceManager) this.iv.invokeFunction("setup", (Object) null);
            eim.registerPlayer(character);
        } catch (Exception ex) {
            System.out.println(new StringBuilder().append("Event name : ").append(this.name).append(", method Name : setup-character:\n").append(ex).toString());
            FileoutputUtil.log("log\\Script_Except.log", new StringBuilder().append("Event name : ").append(this.name).append(", method Name : setup-character:\n").append(ex).toString());
        }
    }

    public void startInstance(MapleParty party, MapleMap map) {
        startInstance(party, map, 255);
    }

    public void startInstance(MapleParty party, MapleMap map, int maxLevel) {
        try {
            int averageLevel = 0;
            int size = 0;
            for (MaplePartyCharacter mpc : party.getMembers()) {
                if ((mpc.isOnline()) && (mpc.getMapid() == map.getId()) && (mpc.getChannel() == map.getChannel())) {
                    averageLevel += mpc.getLevel();
                    size++;
                }
            }
            if (size <= 0) {
                return;
            }
            averageLevel /= size;
            EventInstanceManager eim = (EventInstanceManager) iv.invokeFunction("setup", Math.min(maxLevel, averageLevel), party.getId());
            eim.registerParty(party, map);
        } catch (ScriptException ex) {
            System.out.println(new StringBuilder().append("Event name : ").append(this.name).append(", method Name : setup-partyid:\n").append(ex).toString());
            FileoutputUtil.log("log\\Script_Except.log", new StringBuilder().append("Event name : ").append(this.name).append(", method Name : setup-partyid:\n").append(ex).toString());
        } catch (Exception ex) {
            startInstance_NoID(party, map, ex);
        }
    }

    public void startInstance_NoID(MapleParty party, MapleMap map) {
        startInstance_NoID(party, map, null);
    }

    public void startInstance_NoID(MapleParty party, MapleMap map, Exception old) {
        try {
            EventInstanceManager eim = (EventInstanceManager) iv.invokeFunction("setup", (Object) null);
            eim.registerParty(party, map);
        } catch (Exception ex) {
            System.out.println(new StringBuilder().append("Event name : ").append(this.name).append(", method Name : setup-party:\n").append(ex).toString());
            FileoutputUtil.log("log\\Script_Except.log", new StringBuilder().append("Event name : ").append(this.name).append(", method Name : setup-party:\n").append(ex).append("\n").append(old == null ? "no old exception" : old).toString());
        }
    }

    public void startInstance(EventInstanceManager eim, String leader) {
        try {
            this.iv.invokeFunction("setup", new Object[]{eim});
            eim.setProperty("leader", leader);
        } catch (Exception ex) {
            System.out.println(new StringBuilder().append("Event name : ").append(this.name).append(", method Name : setup-leader:\n").append(ex).toString());
            FileoutputUtil.log("log\\Script_Except.log", new StringBuilder().append("Event name : ").append(this.name).append(", method Name : setup-leader:\n").append(ex).toString());
        }
    }

    public void startInstance(MapleSquad squad, MapleMap map) {
        startInstance(squad, map, -1);
    }

    public void startInstance(MapleSquad squad, MapleMap map, int questID) {
        if (squad.getStatus() == 0) {
            return;
        }
        if (!squad.getLeader().isGM()) {
            int mapid = map.getId();
            int chrSize = 0;
            for (String chr : squad.getMembers()) {
                MapleCharacter player = squad.getChar(chr);
                if ((player != null) && (player.getMapId() == mapid)) {
                    chrSize++;
                }
            }
            if (chrSize < squad.getType().i) {
                squad.getLeader().dropMessage(5, new StringBuilder().append("远征队中人员少于 ").append(squad.getType().i).append(" 人，无法开始远征任务。注意必须队伍中的角色在线且在同一地图。当前人数: ").append(chrSize).toString());
                return;
            }
            if ((this.name.equals("CWKPQ")) && (squad.getJobs().size() < 5)) {
                squad.getLeader().dropMessage(5, "远征队中成员职业的类型小于5种，无法开始远征任务。");
                return;
            }
        }
        try {
            EventInstanceManager eim = (EventInstanceManager) (EventInstanceManager) this.iv.invokeFunction("setup", new Object[]{squad.getLeaderName()});
            eim.registerSquad(squad, map, questID);
        } catch (Exception ex) {
            System.out.println(new StringBuilder().append("Event name : ").append(this.name).append(", method Name : setup-squad:\n").append(ex).toString());
            FileoutputUtil.log("log\\Script_Except.log", new StringBuilder().append("Event name : ").append(this.name).append(", method Name : setup-squad:\n").append(ex).toString());
        }
    }

    public void startInstance(MapleSquad squad, MapleMap map, String bossid) {
        if (squad.getStatus() == 0) {
            return;
        }
        if (!squad.getLeader().isGM()) {
            int mapid = map.getId();
            int chrSize = 0;
            for (String chr : squad.getMembers()) {
                MapleCharacter player = squad.getChar(chr);
                if ((player != null) && (player.getMapId() == mapid)) {
                    chrSize++;
                }
            }
            //限制进入副本的最低人数！
            if (chrSize < squad.getType().i) {
                squad.getLeader().dropMessage(5, new StringBuilder().append("远征队中人员少于 ").append(squad.getType().i).append(" 人，无法开始远征任务。注意必须队伍中的角色在线且在同一地图。当前人数: ").append(chrSize).toString());
               return;
            }
            if ((this.name.equals("CWKPQ")) && (squad.getJobs().size() < 5)) {
                squad.getLeader().dropMessage(5, "远征队中成员职业的类型小于5种，无法开始远征任务。");
                return;
            }
        }
        try {
            EventInstanceManager eim = (EventInstanceManager) (EventInstanceManager) this.iv.invokeFunction("setup", squad.getLeaderName());
            eim.registerSquad(squad, map, Integer.parseInt(bossid));
        } catch (Exception ex) {
            System.out.println(new StringBuilder().append("Event name : ").append(this.name).append(", method Name : setup-squad:\n").append(ex).toString());
            FileoutputUtil.log("log\\Script_Except.log", new StringBuilder().append("Event name : ").append(this.name).append(", method Name : setup-squad:\n").append(ex).toString());
        }
    }

    public void warpAllPlayer(int from, int to) {
        MapleMap tomap = getMapFactory().getMap(to);
        MapleMap frommap = getMapFactory().getMap(from);
        List<MapleCharacter> list = frommap.getCharactersThreadsafe();
        if ((tomap != null) && (frommap != null) && (list != null) && (frommap.getCharactersSize() > 0)) {
            for (MapleMapObject mmo : list) {
                ((MapleCharacter) mmo).changeMap(tomap, tomap.getPortal(0));
            }
        }
    }

    public MapleMapFactory getMapFactory() {
        return getChannelServer().getMapFactory();
    }

    public OverrideMonsterStats newMonsterStats() {
        return new OverrideMonsterStats();
    }

    public List<MapleCharacter> newCharList() {
        return new ArrayList();
    }

    public MapleMonster getMonster(int id) {
        return MapleLifeFactory.getMonster(id);
    }

    public MapleReactor getReactor(int id) {
        return new MapleReactor(MapleReactorFactory.getReactor(id), id);
    }

    public void broadcastShip(int mapid, int effect) {
        getMapFactory().getMap(mapid).broadcastMessage(MaplePacketCreator.boatPacket(effect));
    }

    public void broadcastYellowMsg(String msg) {
        getChannelServer().broadcastPacket(MaplePacketCreator.yellowChat(msg));
    }

    public void broadcastServerMsg(String msg) {
        getChannelServer().broadcastPacket(MaplePacketCreator.serverNotice(6, msg));
    }

    public void broadcastServerMsg(int type, String msg, boolean weather) {
        if (!weather) {
            getChannelServer().broadcastPacket(MaplePacketCreator.serverNotice(type, msg));
        } else {
            for (MapleMap load : getMapFactory().getAllMaps()) {
                if (load.getCharactersSize() > 0) {
                    load.startMapEffect(msg, type);
                }
            }
        }
    }

    public boolean scheduleRandomEvent() {
        boolean omg = false;
        for (int i = 0; i < eventChannel.length; i++) {
            omg |= scheduleRandomEventInChannel(eventChannel[i]);
        }
        return omg;
    }

    public boolean scheduleRandomEventInChannel(int chz) {
        final ChannelServer cs = ChannelServer.getInstance(chz);
        if ((cs == null) || (cs.getEvent() > -1)) {
            return false;
        }
        MapleEventType t = null;
        while (t == null) {
            for (MapleEventType x : MapleEventType.values()) {
                if ((Randomizer.nextInt(MapleEventType.values().length) == 0) && (x != MapleEventType.OxQuiz)) {
                    t = x;
                    break;
                }
            }
        }
        final String msg = MapleEvent.scheduleEvent(t, cs);
        if (msg.length() > 0) {
            broadcastYellowMsg(msg);
            return false;
        }
        EventTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (cs.getEvent() >= 0) {
                    MapleEvent.setEvent(cs, true);
                }
            }
        }, 180000);

        return true;
    }
    
            public void zidonglaba(String Text)
        {
            if (Text.isEmpty())
		{
			chr.dropMessage("[注意]文字过长，不能发送，最长为20个字！");
			return;
		}
		for (Iterator n$ = ChannelServer.getAllInstances().iterator(); n$.hasNext();)
		{
			ChannelServer cservs = (ChannelServer)n$.next();
			Iterator i$ = cservs.getPlayerStorage().getAllCharacters().iterator();
			while (i$.hasNext())
			{
				MapleCharacter players = (MapleCharacter)i$.next();
                                if (getHour() >= 0 || getHour() >= 5 || getHour() >= 10 || getHour() >=15 || getHour() >= 20)
                                    Text = "Maplewings活动多多精彩多多.Maplewings祝大家游戏愉快!";
                                        players.getClient().getSession().write(MaplePacketCreator.startMapEffect((new StringBuilder()).append("Maplewings管理员").append(":").append(Text).toString(), 5120000, true));
                                if (getHour() >= 1 || getHour() >= 6 || getHour() >= 11 || getHour() >=16 || getHour() >= 21)
                                    Text = "让我们一起拒绝外挂,一起携手打造美好的Maple环境!";
                                        players.getClient().getSession().write(MaplePacketCreator.startMapEffect((new StringBuilder()).append("Maplewings管理员").append(":").append(Text).toString(), 5120001, true));
                                if (getHour() >= 2 || getHour() >= 7 || getHour() >= 12 || getHour() >= 17 || getHour() >= 22)
                                    Text = "祝大家能够在Maplewings里玩的开心,好友成群!";
                                        players.getClient().getSession().write(MaplePacketCreator.startMapEffect((new StringBuilder()).append("Maplewings管理员").append(":").append(Text).toString(), 5120008, true));
                                if (getHour() >= 3 || getHour() >= 8 || getHour() >= 13 || getHour() >= 18 || getHour() >= 23)
                                    Text = "Maplewings有你们的支持,我们会做得更好!";
                                        players.getClient().getSession().write(MaplePacketCreator.startMapEffect((new StringBuilder()).append("Maplewings管理员").append(":").append(Text).toString(), 5121009, true));
                                if (getHour() >= 4 || getHour() >= 9 || getHour() >= 14 || getHour() >= 19 || getHour() >= 24)
                                    Text = "欢迎来到Maplewings ,如有什么疑问或发现BUG请和GM联系!";
                                        players.getClient().getSession().write(MaplePacketCreator.startMapEffect((new StringBuilder()).append("Maplewings管理员").append(":").append(Text).toString(), 5121009, true));
			}
		}

	}

    public void setWorldEvent() {
        for (int i = 0; i < eventChannel.length; i++) {
            eventChannel[i] = (Randomizer.nextInt(ChannelServer.getAllInstances().size() - 4) + 2 + i);
        }
    }

    public void DoubleRateEvent(boolean start) {
        getChannelServer().setDoubleExp(start ? 2 : 1);
    }
    
    public void removeNpcs(int npcId) {
        this.c.getPlayer().getMap().removeNpc(npcId);
    }

    
}