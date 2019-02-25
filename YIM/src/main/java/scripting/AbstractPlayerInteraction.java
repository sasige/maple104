package scripting;

import client.*;
import client.inventory.*;
import constants.GameConstants;
import handling.channel.ChannelServer;
import handling.channel.MapleGuildRanking;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import handling.world.World;
import handling.world.World.Broadcast;
import handling.world.World.Guild;
import handling.world.guild.MapleGuild;
import java.awt.Point;
import java.util.Calendar;
import java.util.List;
import org.apache.log4j.Logger;
import server.*;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.maps.*;
import server.quest.MapleQuest;
import tools.FileoutputUtil;
import tools.MaplePacketCreator;
import tools.StringUtil;
import tools.packet.GuildPacket;
import tools.packet.PetPacket;
import tools.packet.UIPacket;

public abstract class AbstractPlayerInteraction {

    private static final Logger log = Logger.getLogger(AbstractPlayerInteraction.class);
    protected MapleClient c;
    protected int id;
    protected int id2;
    protected int id3;

    public AbstractPlayerInteraction(MapleClient c, int id, int id2, int id3) {
        this.c = c;
        this.id = id;
        this.id2 = id2;
        this.id3 = id3;
    }

    public MapleClient getClient() {
        return this.c;
    }

    public MapleClient getC() {
        return this.c;
    }

    public MapleCharacter getChar() {
        return this.c.getPlayer();
    }

    public MapleCharacter getPlayer() {
        return this.c.getPlayer();
    }

    public ChannelServer getChannelServer() {
        return this.c.getChannelServer();
    }

    public EventManager getEventManager(String event) {
        return this.c.getChannelServer().getEventSM().getEventManager(event);
    }

    public EventInstanceManager getEventInstance() {
        return this.c.getPlayer().getEventInstance();
    }

    public void warp(int map) {
        MapleMap mapz = getWarpMap(map);
        try {
            this.c.getPlayer().changeMap(mapz, mapz.getPortal(Randomizer.nextInt(mapz.getPortals().size())));
        } catch (Exception e) {
            this.c.getPlayer().changeMap(mapz, mapz.getPortal(0));
        }
    //    this.c.getPlayer().setMaplewingmap();
    }

    public void warp_Instanced(int map) {
        MapleMap mapz = getMap_Instanced(map);
        try {
            this.c.getPlayer().changeMap(mapz, mapz.getPortal(Randomizer.nextInt(mapz.getPortals().size())));
        } catch (Exception e) {
            this.c.getPlayer().changeMap(mapz, mapz.getPortal(0));
        }
      //  this.c.getPlayer().setMaplewingmap();
    }

    public void warp(int map, int portal) {
        MapleMap mapz = getWarpMap(map);
        if ((portal != 0) && (map == this.c.getPlayer().getMapId())) {
            Point portalPos = new Point(this.c.getPlayer().getMap().getPortal(portal).getPosition());
            if (portalPos.distanceSq(getPlayer().getTruePosition()) < 90000.0D) {
                this.c.getSession().write(MaplePacketCreator.instantMapWarp((byte) portal));
                this.c.getPlayer().checkFollow();
                this.c.getPlayer().getMap().movePlayer(this.c.getPlayer(), portalPos);
            } else {
                this.c.getPlayer().changeMap(mapz, mapz.getPortal(portal));
            }
        } else {
            this.c.getPlayer().changeMap(mapz, mapz.getPortal(portal));
        }
   //     this.c.getPlayer().setMaplewingmap();
    }

    public void warpS(int map, int portal) {
        MapleMap mapz = getWarpMap(map);
        this.c.getPlayer().changeMap(mapz, mapz.getPortal(portal));
   //     this.c.getPlayer().setMaplewingmap();
    }

    public void warp(int map, String portal) {
        MapleMap mapz = getWarpMap(map);
        if ((map == 109060000) || (map == 109060002) || (map == 109060004)) {
            portal = mapz.getSnowballPortal();
        }
        if (map == this.c.getPlayer().getMapId()) {
            Point portalPos = new Point(this.c.getPlayer().getMap().getPortal(portal).getPosition());
            if (portalPos.distanceSq(getPlayer().getTruePosition()) < 90000.0D) {
                this.c.getPlayer().checkFollow();
                this.c.getSession().write(MaplePacketCreator.instantMapWarp((byte) this.c.getPlayer().getMap().getPortal(portal).getId()));
                this.c.getPlayer().getMap().movePlayer(this.c.getPlayer(), new Point(this.c.getPlayer().getMap().getPortal(portal).getPosition()));
            } else {
                this.c.getPlayer().changeMap(mapz, mapz.getPortal(portal));
            }
        } else {
            this.c.getPlayer().changeMap(mapz, mapz.getPortal(portal));
        }
      //  this.c.getPlayer().setMaplewingmap();
    }

    public void warpS(int map, String portal) {
        MapleMap mapz = getWarpMap(map);
        if ((map == 109060000) || (map == 109060002) || (map == 109060004)) {
            portal = mapz.getSnowballPortal();
        }
        this.c.getPlayer().changeMap(mapz, mapz.getPortal(portal));
     //   this.c.getPlayer().setMaplewingmap();
    }

    public void warpMap(int mapid, int portal) {
        MapleMap map = getMap(mapid);
        for (MapleCharacter chr : this.c.getPlayer().getMap().getCharactersThreadsafe()) {
            chr.changeMap(map, map.getPortal(portal));
        }
      //  this.c.getPlayer().setMaplewingmap();
    }

    public void playPortalSE() {
        this.c.getSession().write(MaplePacketCreator.showOwnBuffEffect(0, 7, 1, 1));
    }

    private MapleMap getWarpMap(int map) {
        return ChannelServer.getInstance(this.c.getChannel()).getMapFactory().getMap(map);
    }

    public MapleMap getMap() {
        return this.c.getPlayer().getMap();
    }

    public MapleMap getMap(int map) {
        return getWarpMap(map);
    }

    public MapleMap getMap_Instanced(int map) {
        return this.c.getPlayer().getEventInstance() == null ? getMap(map) : this.c.getPlayer().getEventInstance().getMapInstance(map);
    }

    public void spawnMonster(int id, int qty) {
        spawnMob(id, qty, this.c.getPlayer().getTruePosition());
    }

    public void spawnMobOnMap(int id, int qty, int x, int y, int map) {
        for (int i = 0; i < qty; i++) {
            getMap(map).spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(id), new Point(x, y));
        }
    }

    public void spawnMob(int id, int qty, int x, int y) {
        spawnMob(id, qty, new Point(x, y));
    }

    public void spawnMob(int id, int x, int y) {
        spawnMob(id, 1, new Point(x, y));
    }

    private void spawnMob(int id, int qty, Point pos) {
        for (int i = 0; i < qty; i++) {
            this.c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(id), pos);
        }
    }

    public void killMob(int ids) {
        this.c.getPlayer().getMap().killMonster(ids);
    }

    public void killAllMob() {
        this.c.getPlayer().getMap().killAllMonsters(true);
    }

    public void addHP(int delta) {
        this.c.getPlayer().addHP(delta);
    }

    public int getPlayerStat(String type) {
        if (type.equals("LVL")) {
            return this.c.getPlayer().getLevel();
        }
        if (type.equals("STR")) {
            return this.c.getPlayer().getStat().getStr();
        }
        if (type.equals("DEX")) {
            return this.c.getPlayer().getStat().getDex();
        }
        if (type.equals("INT")) {
            return this.c.getPlayer().getStat().getInt();
        }
        if (type.equals("LUK")) {
            return this.c.getPlayer().getStat().getLuk();
        }
        if (type.equals("HP")) {
            return this.c.getPlayer().getStat().getHp();
        }
        if (type.equals("MP")) {
            return this.c.getPlayer().getStat().getMp();
        }
        if (type.equals("MAXHP")) {
            return this.c.getPlayer().getStat().getMaxHp();
        }
        if (type.equals("MAXMP")) {
            return this.c.getPlayer().getStat().getMaxMp();
        }
        if (type.equals("RAP")) {
            return this.c.getPlayer().getRemainingAp();
        }
        if (type.equals("RSP")) {
            return this.c.getPlayer().getRemainingSp();
        }
        if (type.equals("GID")) {
            return this.c.getPlayer().getGuildId();
        }
        if (type.equals("GRANK")) {
            return this.c.getPlayer().getGuildRank();
        }
        if (type.equals("ARANK")) {
            return this.c.getPlayer().getAllianceRank();
        }
        if (type.equals("GM")) {
            return this.c.getPlayer().isGM() ? 1 : 0;
        }
        if (type.equals("ADMIN")) {
            return this.c.getPlayer().isAdmin() ? 1 : 0;
        }
        if (type.equals("GENDER")) {
            return this.c.getPlayer().getGender();
        }
        if (type.equals("FACE")) {
            return this.c.getPlayer().getFace();
        }
        if (type.equals("HAIR")) {
            return this.c.getPlayer().getHair();
        }
        return -1;
    }

    public int getAndroidStat(String type) {
        if (type.equals("HAIR")) {
            return this.c.getPlayer().getAndroid().getHair();
        }
        if (type.equals("FACE")) {
            return this.c.getPlayer().getAndroid().getFace();
        }
        if (type.equals("SKIN")) {
            return this.c.getPlayer().getAndroid().getSkin();
        }
        if (type.equals("GENDER")) {
            return this.c.getPlayer().getAndroid().getGender();
        }
        return -1;
    }

    public String getName() {
        return this.c.getPlayer().getName();
    }

    public boolean haveItem(int itemid) {
        return haveItem(itemid, 1);
    }

    public boolean haveItem(int itemid, int quantity) {
        return haveItem(itemid, quantity, false, true);
    }

    public boolean haveItem(int itemid, int quantity, boolean checkEquipped, boolean greaterOrEquals) {
        return this.c.getPlayer().haveItem(itemid, quantity, checkEquipped, greaterOrEquals);
    }

    public int getItemQuantity(int itemid) {
        return this.c.getPlayer().getItemQuantity(itemid);
    }

    public boolean canHold() {
        for (int i = 1; i <= 5; i++) {
            if (this.c.getPlayer().getInventory(MapleInventoryType.getByType((byte) i)).getNextFreeSlot() <= -1) {
                return false;
            }
        }
        return true;
    }

    public boolean canHoldSlots(int slot) {
        for (int i = 1; i <= 5; i++) {
            if (this.c.getPlayer().getInventory(MapleInventoryType.getByType((byte) i)).isFull(slot)) {
                return false;
            }
        }
        return true;
    }

    public boolean canHold(int itemid) {
        return this.c.getPlayer().getInventory(GameConstants.getInventoryType(itemid)).getNextFreeSlot() > -1;
    }

    public boolean canHold(int itemid, int quantity) {
        return MapleInventoryManipulator.checkSpace(this.c, itemid, quantity, "");
    }

    public MapleQuestStatus getQuestRecord(int id) {
        return this.c.getPlayer().getQuestNAdd(MapleQuest.getInstance(id));
    }

    public MapleQuestStatus getQuestNoRecord(int id) {
        return this.c.getPlayer().getQuestNoAdd(MapleQuest.getInstance(id));
    }

    public byte getQuestStatus(int id) {
        return this.c.getPlayer().getQuestStatus(id);
    }

    public boolean isQuestActive(int id) {
        return getQuestStatus(id) == 1;
    }

    public boolean isQuestFinished(int id) {
        return getQuestStatus(id) == 2;
    }

    public void showQuestMsg(String msg) {
        this.c.getSession().write(MaplePacketCreator.showQuestMsg(msg));
    }

    public void forceStartQuest(int id, String data) {
        MapleQuest.getInstance(id).forceStart(this.c.getPlayer(), 0, data);
    }

    public void forceStartQuest(int id, int data, boolean filler) {
        MapleQuest.getInstance(id).forceStart(this.c.getPlayer(), 0, filler ? String.valueOf(data) : null);
    }

    public void forceStartQuest(int id) {
        MapleQuest.getInstance(id).forceStart(this.c.getPlayer(), 0, null);
    }

    public void forceCompleteQuest(int id) {
        MapleQuest.getInstance(id).forceComplete(getPlayer(), 0);
    }

    public void spawnNpc(int npcId) {
        this.c.getPlayer().getMap().spawnNpc(npcId, this.c.getPlayer().getPosition());
    }

    public void spawnNpc(int npcId, int x, int y) {
        this.c.getPlayer().getMap().spawnNpc(npcId, new Point(x, y));
    }

    public void spawnNpc(int npcId, Point pos) {
        this.c.getPlayer().getMap().spawnNpc(npcId, pos);
    }

    public void removeNpc(int mapid, int npcId) {
        this.c.getChannelServer().getMapFactory().getMap(mapid).removeNpc(npcId);
    }

    public void removeNpc(int npcId) {
        this.c.getPlayer().getMap().removeNpc(npcId);
    }

    public void forceStartReactor(int mapid, int id) {
        MapleMap map = this.c.getChannelServer().getMapFactory().getMap(mapid);

        for (MapleMapObject remo : map.getAllReactorsThreadsafe()) {
            MapleReactor react = (MapleReactor) remo;
            if (react.getReactorId() == id) {
                react.forceStartReactor(this.c);
                break;
            }
        }
    }

    public void destroyReactor(int mapid, int id) {
        MapleMap map = this.c.getChannelServer().getMapFactory().getMap(mapid);

        for (MapleMapObject remo : map.getAllReactorsThreadsafe()) {
            MapleReactor react = (MapleReactor) remo;
            if (react.getReactorId() == id) {
                react.hitReactor(this.c);
                break;
            }
        }
    }

    public void hitReactor(int mapid, int id) {
        MapleMap map = this.c.getChannelServer().getMapFactory().getMap(mapid);

        for (MapleMapObject remo : map.getAllReactorsThreadsafe()) {
            MapleReactor react = (MapleReactor) remo;
            if (react.getReactorId() == id) {
                react.hitReactor(this.c);
                break;
            }
        }
    }

    public int getJob() {
        return this.c.getPlayer().getJob();
    }

    /**
     * 获取当前职业的ID
     *
     * @return
     */
    public int getJobId() {
        return this.c.getPlayer().getJob();
    }

    public String getJobName(int id) {
        return MapleCarnivalChallenge.getJobNameById(id);
    }

    public boolean isBeginnerJob() {
        return ((getJob() == 0) || (getJob() == 1000) || (getJob() == 2000) || (getJob() == 2001) || (getJob() == 2002) || (getJob() == 2003) || (getJob() == 3000) || (getJob() == 3001)) && (getLevel() < 11);
    }

    public int getLevel() {
        return this.c.getPlayer().getLevel();
    }

    public int getFame() {
        return this.c.getPlayer().getFame();
    }

    public void gainFame(int famechange) {
        gainFame(famechange, false);
    }

    public void gainFame(int famechange, boolean show) {
        this.c.getPlayer().gainFame(famechange, show);
    }

    public void getNX(int type) {
        if ((type <= 0) || (type > 2)) {
            type = 2;
        }
        this.c.getPlayer().getCSPoints(type);
    }

    public void gainNX(int amount) {
        this.c.getPlayer().modifyCSPoints(1, amount, true);
    }

    public void gainNX(int type, int amount) {
        if ((type <= 0) || (type > 2)) {
            type = 2;
        }
        this.c.getPlayer().modifyCSPoints(type, amount, true);
    }

    public void gainItemPeriod(int id, short quantity, int period) {
        gainItem(id, quantity, false, period, -1, "");
    }

    public void gainItemPeriod(int id, short quantity, long period, String owner) {
        gainItem(id, quantity, false, period, -1, owner);
    }

    public void gainItem(int id, short quantity) {
        gainItem(id, quantity, false, 0L, -1, "");
    }
    
    public void gainItem(int id, int quantitys) {
        short quantity;
        if (quantitys > 300) {
            quantity = 300;
        } else {
            quantity = (short) quantitys;
        }
        gainItem(id, quantity, false, 0L, -1, "");
    }

    public void gainItem(int id, short quantity, boolean randomStats) {
        gainItem(id, quantity, randomStats, 0L, -1, "");
    }

    public void gainItem(int id, short quantity, boolean randomStats, int slots) {
        gainItem(id, quantity, randomStats, 0L, slots, "");
    }

    public void gainItem(int id, short quantity, long period) {
        gainItem(id, quantity, false, period, -1, "");
    }

    public void gainItem(int id, short quantity, boolean randomStats, long period, int slots) {
        gainItem(id, quantity, randomStats, period, slots, "");
    }

    public void gainItem(int id, short quantity, boolean randomStats, long period, int slots, String owner) {
        gainItem(id, quantity, randomStats, period, slots, owner, this.c);
    }

    public void gainItem(int id, short quantity, boolean randomStats, long period, int slots, String owner, MapleClient cg) {
        if (GameConstants.isLogItem(id)) {
            String itemText = new StringBuilder().append("玩家 ").append(StringUtil.getRightPaddedStr(cg.getPlayer().getName(), ' ', 13)).append(quantity >= 0 ? " 获得道具: " : " 失去道具: ").append(id).append(" 数量: ").append(StringUtil.getRightPaddedStr(String.valueOf(Math.abs(quantity)), ' ', 5)).append(" 道具名字: ").append(getItemName(id)).toString();
            log.info(new StringBuilder().append("[物品] ").append(itemText).toString());
            Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append("[GM Message] ").append(itemText).toString()));
        }
        if (quantity >= 0) {
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            MapleInventoryType type = GameConstants.getInventoryType(id);
            if (!MapleInventoryManipulator.checkSpace(cg, id, quantity, "")) {
                return;
            }
            if ((type.equals(MapleInventoryType.EQUIP)) && (!GameConstants.isThrowingStar(id)) && (!GameConstants.isBullet(id))) {
                Equip item = (Equip) (randomStats ? ii.randomizeStats((Equip) ii.getEquipById(id)) : ii.getEquipById(id));
                if (period > 0L) {
                    item.setExpiration(System.currentTimeMillis() + period * 24L * 60L * 60L * 1000L);
                }
                if (slots > 0) {
                    item.setUpgradeSlots((byte) (item.getUpgradeSlots() + slots));
                }
                if (owner != null) {
                    item.setOwner(owner);
                }
                item.setGMLog(new StringBuilder().append("脚本获得 ").append(this.id).append(" (").append(this.id2).append(") 地图: ").append(cg.getPlayer().getMapId()).append(" 时间: ").append(FileoutputUtil.CurrentReadable_Time()).toString());
                String name = ii.getName(id);
                if ((id / 10000 == 114) && (name != null) && (name.length() > 0)) {
                    String msg = new StringBuilder().append("恭喜您获得勋章 <").append(name).append(">").toString();
                    cg.getPlayer().dropMessage(-1, msg);
                    cg.getPlayer().dropMessage(5, msg);
                }
                MapleInventoryManipulator.addbyItem(cg, item.copy());
            } else {
                MapleInventoryManipulator.addById(cg, id, quantity, owner == null ? "" : owner, null, period, new StringBuilder().append("脚本获得 ").append(this.id).append(" (").append(this.id2).append(") 地图: ").append(cg.getPlayer().getMapId()).append(" 时间: ").append(FileoutputUtil.CurrentReadable_Time()).toString());
            }
        } else {
            MapleInventoryManipulator.removeById(cg, GameConstants.getInventoryType(id), id, -quantity, true, false);
        }
        cg.getSession().write(MaplePacketCreator.getShowItemGain(id, quantity, true));
    }

    public boolean removeItem(int id) {
        if (MapleInventoryManipulator.removeById_Lock(this.c, GameConstants.getInventoryType(id), id)) {
            c.getSession().write(MaplePacketCreator.getShowItemGain(id, (short) -1, true));
            return true;
        }
        return false;
    }

    public void changeMusic(String songName) {
        getPlayer().getMap().broadcastMessage(MaplePacketCreator.musicChange(songName));
    }

    public void worldMessage(String message) {
        worldMessage(6, message);
    }

    public void worldMessage(int type, String message) {
        Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(type, message));
    }

    public void playerMessage(String message) {
        playerMessage(5, message);
    }

    public void mapMessage(String message) {
        mapMessage(5, message);
    }

    public void guildMessage(String message) {
        guildMessage(5, message);
    }

    public void playerMessage(int type, String message) {
        this.c.getPlayer().dropMessage(type, message);
    }

    public void mapMessage(int type, String message) {
        this.c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.serverNotice(type, message));
    }

    public void guildMessage(int type, String message) {
        if (getPlayer().getGuildId() > 0) {
            Guild.guildPacket(getPlayer().getGuildId(), MaplePacketCreator.serverNotice(type, message));
        }
    }

    public void topMessage(String message) {
        this.c.getSession().write(UIPacket.getTopMsg(message));
    }

    public MapleGuild getGuild() {
        return getGuild(getPlayer().getGuildId());
    }

    public MapleGuild getGuild(int guildid) {
        return Guild.getGuild(guildid);
    }

    public MapleParty getParty() {
        return this.c.getPlayer().getParty();
    }

    public int getCurrentPartyId(int mapid) {
        return getMap(mapid).getCurrentPartyId();
    }

    public boolean isLeader() {
        if (getPlayer().getParty() == null) {
            return false;
        }
        return getParty().getLeader().getId() == this.c.getPlayer().getId();
    }

    public boolean isAllPartyMembersAllowedJob(int job) {
        if (this.c.getPlayer().getParty() == null) {
            return false;
        }
        for (MaplePartyCharacter mem : this.c.getPlayer().getParty().getMembers()) {
            if (mem.getJobId() / 100 != job) {
                return false;
            }
        }
        return true;
    }

    public boolean allMembersHere() {
        if (this.c.getPlayer().getParty() == null) {
            return false;
        }
        for (MaplePartyCharacter mem : this.c.getPlayer().getParty().getMembers()) {
            MapleCharacter chr = this.c.getPlayer().getMap().getCharacterById(mem.getId());
            if (chr == null) {
                return false;
            }
        }
        return true;
    }

    public void warpParty(int mapId) {
        if ((getPlayer().getParty() == null) || (getPlayer().getParty().getMembers().size() == 1)) {
            warp(mapId, 0);
            return;
        }
        MapleMap target = getMap(mapId);
        int cMap = getPlayer().getMapId();
        for (MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            MapleCharacter curChar = getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if ((curChar != null) && ((curChar.getMapId() == cMap) || (curChar.getEventInstance() == getPlayer().getEventInstance()))) {
                curChar.changeMap(target, target.getPortal(0));
            }
        }
    }

    public void warpParty(int mapId, int portal) {
        if ((getPlayer().getParty() == null) || (getPlayer().getParty().getMembers().size() == 1)) {
            if (portal < 0) {
                warp(mapId);
            } else {
                warp(mapId, portal);
            }
            return;
        }
        boolean rand = portal < 0;
        MapleMap target = getMap(mapId);
        int cMap = getPlayer().getMapId();
        for (MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            MapleCharacter curChar = getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if ((curChar != null) && ((curChar.getMapId() == cMap) || (curChar.getEventInstance() == getPlayer().getEventInstance()))) {
                if (rand) {
                    try {
                        curChar.changeMap(target, target.getPortal(Randomizer.nextInt(target.getPortals().size())));
                    } catch (Exception e) {
                        curChar.changeMap(target, target.getPortal(0));
                    }
                } else {
                    curChar.changeMap(target, target.getPortal(portal));
                }
            }
        }
    }

    public void warpParty_Instanced(int mapId) {
        if ((getPlayer().getParty() == null) || (getPlayer().getParty().getMembers().size() == 1)) {
            warp_Instanced(mapId);
            return;
        }
        MapleMap target = getMap_Instanced(mapId);

        int cMap = getPlayer().getMapId();
        for (MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            MapleCharacter curChar = getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if ((curChar != null) && ((curChar.getMapId() == cMap) || (curChar.getEventInstance() == getPlayer().getEventInstance()))) {
                curChar.changeMap(target, target.getPortal(0));
            }
        }
    }

    public void gainMeso(int gain) {
        this.c.getPlayer().gainMeso(gain, true, true);
    }

    public void gainExp(int gain) {
        this.c.getPlayer().gainExp(gain, true, true, true);
    }

    public void gainExpR(int gain) {
        this.c.getPlayer().gainExp(gain * this.c.getChannelServer().getExpRate(), true, true, true);
    }

    public void givePartyItems(int id, short quantity, List<MapleCharacter> party) {
        for (MapleCharacter chr : party) {
            if (quantity >= 0) {
                MapleInventoryManipulator.addById(chr.getClient(), id, quantity, new StringBuilder().append("Received from party interaction ").append(id).append(" (").append(this.id2).append(")").toString());
            } else {
                MapleInventoryManipulator.removeById(chr.getClient(), GameConstants.getInventoryType(id), id, -quantity, true, false);
            }
            chr.getClient().getSession().write(MaplePacketCreator.getShowItemGain(id, quantity, true));
        }
    }

    public void addPartyTrait(String t, int e, List<MapleCharacter> party) {
        for (MapleCharacter chr : party) {
            chr.getTrait(MapleTrait.MapleTraitType.valueOf(t)).addExp(e, chr);
        }
    }

    public void addPartyTrait(String t, int e) {
        if ((getPlayer().getParty() == null) || (getPlayer().getParty().getMembers().size() == 1)) {
            addTrait(t, e);
            return;
        }
        int cMap = getPlayer().getMapId();
        for (MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            MapleCharacter curChar = getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if ((curChar != null) && ((curChar.getMapId() == cMap) || (curChar.getEventInstance() == getPlayer().getEventInstance()))) {
                curChar.getTrait(MapleTrait.MapleTraitType.valueOf(t)).addExp(e, curChar);
            }
        }
    }

    public void addTrait(String t, int e) {
        getPlayer().getTrait(MapleTrait.MapleTraitType.valueOf(t)).addExp(e, getPlayer());
    }

    public void givePartyItems(int id, short quantity) {
        givePartyItems(id, quantity, false);
    }

    public void givePartyItems(int id, short quantity, boolean removeAll) {
        if ((getPlayer().getParty() == null) || (getPlayer().getParty().getMembers().size() == 1)) {
            gainItem(id, (short) (removeAll ? -getPlayer().itemQuantity(id) : quantity));
            return;
        }
        int cMap = getPlayer().getMapId();
        for (MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            MapleCharacter curChar = getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if ((curChar != null) && ((curChar.getMapId() == cMap) || (curChar.getEventInstance() == getPlayer().getEventInstance()))) {
                gainItem(id, (short) (removeAll ? -curChar.itemQuantity(id) : quantity), false, 0L, 0, "", curChar.getClient());
            }
        }
    }

    public void givePartyExp_PQ(int maxLevel, double mod, List<MapleCharacter> party) {
        for (MapleCharacter chr : party) {
            int amount = (int) Math.round(GameConstants.getExpNeededForLevel(chr.getLevel() > maxLevel ? maxLevel + (maxLevel - chr.getLevel()) / 10 : chr.getLevel()) / (Math.min(chr.getLevel(), maxLevel) / 5.0D) / (mod * 2.0D));
            chr.gainExp(amount * this.c.getChannelServer().getExpRate(), true, true, true);
        }
    }

    public void gainExp_PQ(int maxLevel, double mod) {
        int amount = (int) Math.round(GameConstants.getExpNeededForLevel(getPlayer().getLevel() > maxLevel ? maxLevel + getPlayer().getLevel() / 10 : getPlayer().getLevel()) / (Math.min(getPlayer().getLevel(), maxLevel) / 10.0D) / mod);
        gainExp(amount * this.c.getChannelServer().getExpRate());
    }

    public void givePartyExp_PQ(int maxLevel, double mod) {
        if ((getPlayer().getParty() == null) || (getPlayer().getParty().getMembers().size() == 1)) {
            int amount = (int) Math.round(GameConstants.getExpNeededForLevel(getPlayer().getLevel() > maxLevel ? maxLevel + getPlayer().getLevel() / 10 : getPlayer().getLevel()) / (Math.min(getPlayer().getLevel(), maxLevel) / 10.0D) / mod);
            gainExp(amount * this.c.getChannelServer().getExpRate());
            return;
        }
        int cMap = getPlayer().getMapId();
        for (MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            MapleCharacter curChar = getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if ((curChar != null) && ((curChar.getMapId() == cMap) || (curChar.getEventInstance() == getPlayer().getEventInstance()))) {
                int amount = (int) Math.round(GameConstants.getExpNeededForLevel(curChar.getLevel() > maxLevel ? maxLevel + curChar.getLevel() / 10 : curChar.getLevel()) / (Math.min(curChar.getLevel(), maxLevel) / 10.0D) / mod);
                curChar.gainExp(amount * this.c.getChannelServer().getExpRate(), true, true, true);
            }
        }
    }

    public void givePartyExp(int amount, List<MapleCharacter> party) {
        for (MapleCharacter chr : party) {
            chr.gainExp(amount * this.c.getChannelServer().getExpRate(), true, true, true);
        }
    }

    public void givePartyExp(int amount) {
        if ((getPlayer().getParty() == null) || (getPlayer().getParty().getMembers().size() == 1)) {
            gainExp(amount * this.c.getChannelServer().getExpRate());
            return;
        }
        int cMap = getPlayer().getMapId();
        for (MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            MapleCharacter curChar = getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if ((curChar != null) && ((curChar.getMapId() == cMap) || (curChar.getEventInstance() == getPlayer().getEventInstance()))) {
                curChar.gainExp(amount * this.c.getChannelServer().getExpRate(), true, true, true);
            }
        }
    }

    public void givePartyNX(int amount, List<MapleCharacter> party) {
        for (MapleCharacter chr : party) {
            chr.modifyCSPoints(1, amount, true);
        }
    }

    public void givePartyNX(int amount) {
        if ((getPlayer().getParty() == null) || (getPlayer().getParty().getMembers().size() == 1)) {
            gainNX(amount);
            return;
        }
        int cMap = getPlayer().getMapId();
        for (MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            MapleCharacter curChar = getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if ((curChar != null) && ((curChar.getMapId() == cMap) || (curChar.getEventInstance() == getPlayer().getEventInstance()))) {
                curChar.modifyCSPoints(1, amount, true);
            }
        }
    }

    public void endPartyQuest(int amount, List<MapleCharacter> party) {
        for (MapleCharacter chr : party) {
            chr.endPartyQuest(amount);
        }
    }

    public void endPartyQuest(int amount) {
        if ((getPlayer().getParty() == null) || (getPlayer().getParty().getMembers().size() == 1)) {
            getPlayer().endPartyQuest(amount);
            return;
        }
        int cMap = getPlayer().getMapId();
        for (MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            MapleCharacter curChar = getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if ((curChar != null) && ((curChar.getMapId() == cMap) || (curChar.getEventInstance() == getPlayer().getEventInstance()))) {
                curChar.endPartyQuest(amount);
            }
        }
    }

    public void removeFromParty(int id, List<MapleCharacter> party) {
        for (MapleCharacter chr : party) {
            int possesed = chr.getInventory(GameConstants.getInventoryType(id)).countById(id);
            if (possesed > 0) {
                MapleInventoryManipulator.removeById(this.c, GameConstants.getInventoryType(id), id, possesed, true, false);
                chr.getClient().getSession().write(MaplePacketCreator.getShowItemGain(id, (short) (-possesed), true));
            }
        }
    }

    public void removeFromParty(int id) {
        givePartyItems(id, (short) 0, true);
    }

    public void useSkill(int skill, int level) {
        if (level <= 0) {
            return;
        }
        SkillFactory.getSkill(skill).getEffect(level).applyTo(this.c.getPlayer());
    }

    public void useItem(int id) {
        MapleItemInformationProvider.getInstance().getItemEffect(id).applyTo(this.c.getPlayer());
        this.c.getSession().write(UIPacket.getStatusMsg(id));
    }

    public void cancelItem(int id) {
        this.c.getPlayer().cancelEffect(MapleItemInformationProvider.getInstance().getItemEffect(id), false, -1L);
    }

    public int getMorphState() {
        return this.c.getPlayer().getMorphState();
    }

    public void removeAll(int id) {
        this.c.getPlayer().removeAll(id);
    }

    public void gainCloseness(int closeness, int index) {
        MaplePet pet = getPlayer().getPet(index);
        if (pet != null) {
            pet.setCloseness(pet.getCloseness() + closeness * getChannelServer().getTraitRate());
            getClient().getSession().write(PetPacket.updatePet(pet, getPlayer().getInventory(MapleInventoryType.CASH).getItem((short) (byte) pet.getInventoryPosition()), true));
        }
    }

    public void gainClosenessAll(int closeness) {
        for (MaplePet pet : getPlayer().getPets()) {
            if ((pet != null) && (pet.getSummoned())) {
                pet.setCloseness(pet.getCloseness() + closeness);
                getClient().getSession().write(PetPacket.updatePet(pet, getPlayer().getInventory(MapleInventoryType.CASH).getItem((short) (byte) pet.getInventoryPosition()), true));
            }
        }
    }

    public void resetMap(int mapid) {
        getMap(mapid).resetFully();
    }

    public void openNpc(int id) {
        getClient().removeClickedNPC();
        NPCScriptManager.getInstance().start(getClient(), id);
    }

    public void openNpc(MapleClient cg, int id) {
        cg.removeClickedNPC();
        NPCScriptManager.getInstance().start(cg, id);
    }

    public void openNpc(int id, int npcMode) {
        getClient().removeClickedNPC();
        NPCScriptManager.getInstance().start(getClient(), id, npcMode);
    }

    /**
     * 获取当前地图的ID
     *
     * @return
     */
    public int getMapId() {
        return this.c.getPlayer().getMap().getId();
    }

    public boolean haveMonster(int mobid) {
        for (MapleMapObject obj : this.c.getPlayer().getMap().getAllMonstersThreadsafe()) {
            MapleMonster mob = (MapleMonster) obj;
            if (mob.getId() == mobid) {
                return true;
            }
        }
        return false;
    }

    public int getChannelNumber() {
        return this.c.getChannel();
    }

    /**
     * 根据地图ID获取该地图的怪物数量
     *
     * @param mapid 地图ID
     * @return
     */
    public int getMonsterCount(int mapid) {
        return this.c.getChannelServer().getMapFactory().getMap(mapid).getNumMonsters();
    }

    public void teachSkill(int id, int level, byte masterlevel) {
        getPlayer().changeSkillLevel(SkillFactory.getSkill(id), level, masterlevel);
    }

    public void teachSkill(int id, int level) {
        Skill skil = SkillFactory.getSkill(id);
        if (getPlayer().getSkillLevel(skil) > level) {
            level = getPlayer().getSkillLevel(skil);
        }
        getPlayer().changeSkillLevel(skil, level, (byte) skil.getMaxLevel());
    }

    /**
     * 根据地图ID获取该地图上的玩家数量
     *
     * @param mapid 地图ID
     * @return
     */
    public int getPlayerCount(int mapid) {
        return this.c.getChannelServer().getMapFactory().getMap(mapid).getCharactersSize();
    }

    public void dojo_getUp() {
        this.c.getSession().write(MaplePacketCreator.updateInfoQuest(1207, "pt=1;min=4;belt=1;tuto=1"));
        this.c.getSession().write(MaplePacketCreator.Mulung_DojoUp2());
        this.c.getSession().write(MaplePacketCreator.instantMapWarp((byte) 6));
    }

    public boolean dojoAgent_NextMap(boolean dojo, boolean fromresting) {
        if (dojo) {
            return Event_DojoAgent.warpNextMap(this.c.getPlayer(), fromresting, this.c.getPlayer().getMap());
        }
        return Event_DojoAgent.warpNextMap_Agent(this.c.getPlayer(), fromresting);
    }

    public boolean dojoAgent_NextMap(boolean dojo, boolean fromresting, int mapid) {
        if (dojo) {
            return Event_DojoAgent.warpNextMap(this.c.getPlayer(), fromresting, getMap(mapid));
        }
        return Event_DojoAgent.warpNextMap_Agent(this.c.getPlayer(), fromresting);
    }

    public int dojo_getPts() {
        return this.c.getPlayer().getIntNoRecord(150100);
    }

    public MapleEvent getEvent(String loc) {
        return this.c.getChannelServer().getEvent(MapleEventType.valueOf(loc));
    }

    public int getSavedLocation(String loc) {
        Integer ret = Integer.valueOf(this.c.getPlayer().getSavedLocation(SavedLocationType.fromString(loc)));
        if ((ret == null) || (ret.intValue() == -1)) {
            return 100000000;
        }
        return ret.intValue();
    }

    public void saveLocation(String loc) {
        this.c.getPlayer().saveLocation(SavedLocationType.fromString(loc));
    }

    public void saveReturnLocation(String loc) {
        this.c.getPlayer().saveLocation(SavedLocationType.fromString(loc), this.c.getPlayer().getMap().getReturnMap().getId());
    }

    public void clearSavedLocation(String loc) {
        this.c.getPlayer().clearSavedLocation(SavedLocationType.fromString(loc));
    }

    public void summonMsg(String msg) {
        if (!this.c.getPlayer().hasSummon()) {
            playerSummonHint(true);
        }
        this.c.getSession().write(UIPacket.summonMessage(msg));
    }

    public void summonMsg(int type) {
        if (!this.c.getPlayer().hasSummon()) {
            playerSummonHint(true);
        }
        this.c.getSession().write(UIPacket.summonMessage(type));
    }

    public void showInstruction(String msg, int width, int height) {
        this.c.getSession().write(MaplePacketCreator.sendHint(msg, width, height));
    }

    public void playerSummonHint(boolean summon) {
        this.c.getPlayer().setHasSummon(summon);
        this.c.getSession().write(UIPacket.summonHelper(summon));
    }

    public String getInfoQuest(int id) {
        return this.c.getPlayer().getInfoQuest(id);
    }

    public void updateInfoQuest(int id, String data) {
        this.c.getPlayer().updateInfoQuest(id, data);
    }

    public boolean getEvanIntroState(String data) {
        return getInfoQuest(22013).equals(data);
    }

    public void updateEvanIntroState(String data) {
        updateInfoQuest(22013, data);
    }

    public void Aran_Start() {
        this.c.getSession().write(UIPacket.Aran_Start());
    }

    public void evanTutorial(String data, int v1) {
        this.c.getSession().write(MaplePacketCreator.getEvanTutorial(data));
    }

    public void AranTutInstructionalBubble(String data) {
        this.c.getSession().write(UIPacket.AranTutInstructionalBalloon(data));
    }

    public void ShowWZEffect(String data) {
        this.c.getSession().write(UIPacket.AranTutInstructionalBalloon(data));
    }

    public void showWZEffect(String data) {
        this.c.getSession().write(UIPacket.ShowWZEffect(data));
    }

    public void EarnTitleMsg(String data) {
        this.c.getSession().write(UIPacket.EarnTitleMsg(data));
    }

    public void EnableUI(short i) {
        this.c.getSession().write(UIPacket.IntroEnableUI(i));
    }

    public void DisableUI(boolean enabled) {
        this.c.getSession().write(UIPacket.IntroDisableUI(enabled));
    }

    public void MovieClipIntroUI(boolean enabled) {
        this.c.getSession().write(UIPacket.IntroDisableUI(enabled));
        this.c.getSession().write(UIPacket.IntroLock(enabled));
    }

    public MapleInventoryType getInvType(int i) {
        return MapleInventoryType.getByType((byte) i);
    }

    public String getItemName(int id) {
        return MapleItemInformationProvider.getInstance().getName(id);
    }

    public void gainPet(int id, String name, int level, int closeness, int fullness, long period, short flags) {
        if ((id > 5000200) || (id < 5000000)) {
            id = 5000000;
        }
        if (level > 30) {
            level = 30;
        }
        if (closeness > 30000) {
            closeness = 30000;
        }
        if (fullness > 100) {
            fullness = 100;
        }
        try {
            MapleInventoryManipulator.addById(this.c, id, (short) 1, "", MaplePet.createPet(id, name, level, closeness, fullness, MapleInventoryIdentifier.getInstance(), id == 5000054 ? (int) period : 0, flags, 0), 45L, new StringBuilder().append("Pet from interaction ").append(id).append(" (").append(this.id2).append(")").append(" on ").append(FileoutputUtil.CurrentReadable_Date()).toString());
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    public void removeSlot(int invType, byte slot, short quantity) {
        MapleInventoryManipulator.removeFromSlot(this.c, getInvType(invType), (short) slot, quantity, true);
    }

    public void gainGP(int gp) {
        if (getPlayer().getGuildId() <= 0) {
            return;
        }
        Guild.gainGP(getPlayer().getGuildId(), gp);
    }

    public int getGP() {
        if (getPlayer().getGuildId() <= 0) {
            return 0;
        }
        return Guild.getGP(getPlayer().getGuildId());
    }

    public void showMapEffect(String path) {
        getClient().getSession().write(UIPacket.MapEff(path));
    }

    public int itemQuantity(int itemid) {
        return getPlayer().itemQuantity(itemid);
    }

    public EventInstanceManager getDisconnected(String event) {
        EventManager em = getEventManager(event);
        if (em == null) {
            return null;
        }
        for (EventInstanceManager eim : em.getInstances()) {
            if ((eim.isDisconnected(this.c.getPlayer())) && (eim.getPlayerCount() > 0)) {
                return eim;
            }
        }
        return null;
    }

    public boolean isAllReactorState(int reactorId, int state) {
        boolean ret = false;
        for (MapleReactor r : getMap().getAllReactorsThreadsafe()) {
            if (r.getReactorId() == reactorId) {
                ret = r.getState() == state;
            }
        }
        return ret;
    }

    public long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public void spawnMonster(int id) {
        spawnMonster(id, 1, getPlayer().getTruePosition());
    }

    public void spawnMonster(int id, int x, int y) {
        spawnMonster(id, 1, new Point(x, y));
    }

    public void spawnMonster(int id, int qty, int x, int y) {
        spawnMonster(id, qty, new Point(x, y));
    }

    public void spawnMonster(int id, int qty, Point pos) {
        for (int i = 0; i < qty; i++) {
            getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(id), pos);
        }
    }

    public void sendNPCText(String text, int npc) {
        getMap().broadcastMessage(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "00 00", (byte) 0));
    }

    public boolean getTempFlag(int flag) {
        return (this.c.getChannelServer().getTempFlag() & flag) == flag;
    }

    public void logPQ(String text) {
    }

    public void outputFileError(Throwable t) {
        FileoutputUtil.outputFileError("log\\Script_Except.log", t);
    }

    public void trembleEffect(int type, int delay) {
        this.c.getSession().write(MaplePacketCreator.trembleEffect(type, delay));
    }

    public int nextInt(int arg0) {
        return Randomizer.nextInt(arg0);
    }

    public MapleQuest getQuest(int arg0) {
        return MapleQuest.getInstance(arg0);
    }

    public void achievement(int a) {
        this.c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.achievementRatio(a));
    }

    public MapleInventory getInventory(int type) {
        return this.c.getPlayer().getInventory(MapleInventoryType.getByType((byte) type));
    }

    public boolean isGMS() {
        return GameConstants.GMS;
    }

    public int randInt(int arg0) {
        return Randomizer.nextInt(arg0);
    }

    public void sendDirectionStatus(int key, int value) {
        this.c.getSession().write(UIPacket.getDirectionInfo(key, value));
        this.c.getSession().write(UIPacket.getDirectionStatus(true));
    }

    public void sendDirectionInfo(String data) {
        this.c.getSession().write(UIPacket.getDirectionInfo(data, 2000, 0, -100, 0));
        this.c.getSession().write(UIPacket.getDirectionInfo(1, 2000));
    }

    public int getProfessions() {
        int ii = 0;

        for (int i = 0; i < 5; i++) {
            int skillId = 92000000 + i * 10000;
            if (this.c.getPlayer().getProfessionLevel(skillId) > 0) {
                ii++;
            }
        }
        return ii;
    }

    /**
     * 设置VIP
     *
     * @param vip 等级
     */
    public void setVip(int vip) {
        setVip(vip, 7);
    }

    /**
     * 设置VIP
     *
     * @param vip 等级
     * @param period 时间
     */
    public void setVip(int vip, long period) {
        this.c.getPlayer().setVip(vip);
        if (period > 0L) {
            this.c.getPlayer().setViptime(period);
        }
    }

    /**
     * 获取当前VIP等级
     *
     * @return
     */
    public int getVip() {
        return this.c.getPlayer().getVip();
    }

    /**
     * 判断是否VIP
     *
     * @return
     */
    public boolean isVip() {
        return this.c.getPlayer().getVip() > 0;
    }

    /**
     * 设置VIP时间
     *
     * @param period
     */
    public void setViptime(long period) {
        if (period != 0L) {
            this.c.getPlayer().setViptime(period);
        }
    }

    /**
     * 设置VIP成长值
     * @param vipczz 
     */
    public void setVipczz(int vipczz) {
        c.getPlayer().setVipczz(vipczz);
    }

    /**
     * 获取VIP的成长值
     * @return 
     */
    public int getVipczz() {
        return c.getPlayer().getVipczz();
    }

    public int getBossLog(String bossid) {
        return this.c.getPlayer().getBossLog(bossid);
    }

    public int getBossLog(String bossid, int type) {
        return this.c.getPlayer().getBossLog(bossid, type);
    }

    public void setBossLog(String bossid) {
        this.c.getPlayer().setBossLog(bossid);
    }

    public void setBossLog(String bossid, int type) {
        this.c.getPlayer().setBossLog(bossid, type);
    }

    public void setBossLog(String bossid, int type, int count) {
        this.c.getPlayer().setBossLog(bossid, type, count);
    }

    public void resetBossLog(String bossid) {
        this.c.getPlayer().resetBossLog(bossid);
    }

    public void resetBossLog(String bossid, int type) {
        this.c.getPlayer().resetBossLog(bossid, type);
    }

    public void getClock(int time) {
        this.c.getSession().write(MaplePacketCreator.getClock(time));
    }
    
    public int getMaplewing(String te) {
        upMaplewing();
        return this.c.getPlayer().getMaplewing(te);
    }
    
    public int getMaplewing(String te, int ids) {
        upMaplewing(ids);
        return this.c.getPlayer().getMaplewing(te, ids);
    }
    
    public int addMaplewing(String te, int tas) {
        upMaplewing();
        return this.c.getPlayer().addMaplewing(te, tas);
    }
    
    public int addMaplewing(String te, int tas, int ids) {
        upMaplewing(ids);
        return this.c.getPlayer().addMaplewing(te, tas, ids);
    }
    
    public void upMaplewing() {
         this.c.getPlayer().upMaplewing();
    }
    
    public void upMaplewing(int ids) {
         this.c.getPlayer().upMaplewing(ids);
    }
    
    public int setMaplewing(String te, int tas) {
        upMaplewing();
        return this.c.getPlayer().setMaplewing(te, tas);
    }
    
    public int setMaplewing(String te, int das, int ids) {
        upMaplewing(ids);
        return this.c.getPlayer().setMaplewing(te, das, ids);
    }
    
    public String getColor() {
        upMaplewing();
        return this.c.getPlayer().getColor();
    }
    
    public String getColor(int ids) {
        upMaplewing(ids);
        return this.c.getPlayer().getColor(ids);
    }
    
    public String getMaplewings(String te) {
        upMaplewing();
        return this.c.getPlayer().getMaplewings(te);
    }
    
    public String getMaplewings(String te,int ids) {
        upMaplewing(ids);
        return this.c.getPlayer().getMaplewings(te, ids);
    }
    
    public String getVipname() {
        return this.c.getPlayer().getVipname();
    }

    /**
     * 打开指定网址
     *
     * @param web 网址
     */
    public void openWeb(String web) {
        this.c.getSession().write(MaplePacketCreator.openWeb(web));
    }

    public boolean isCanPvp() {
        return this.c.getChannelServer().isCanPvp();
    }

    public void showDoJangRank() {
        this.c.getSession().write(MaplePacketCreator.showDoJangRank());
    }
    
    /**
     * 显示家族排行
     */
    public void showlayGuildRanks() {
        this.c.getSession().write(GuildPacket.showGuildRanks(this.id, MapleGuildRanking.getInstance().getRank()));
    }

    public int MarrageChecking() {
        if (getPlayer().getParty() == null) {
            return -1;
        }
        if (getPlayer().getMarriageId() > 0) {
            return 0;
        }
        if (getPlayer().getParty().getMembers().size() != 2) {
            return 1;
        }
        if ((getPlayer().getGender() == 0) && (!getPlayer().haveItem(1050121)) && (!getPlayer().haveItem(1050122)) && (!getPlayer().haveItem(1050113))) {
            return 5;
        }
        if ((getPlayer().getGender() == 1) && (!getPlayer().haveItem(1051129)) && (!getPlayer().haveItem(1051130)) && (!getPlayer().haveItem(1051114))) {
            return 5;
        }
        if (!getPlayer().haveItem(1112001)) {
            return 6;
        }
        for (MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            if (chr.getId() == getPlayer().getId()) {
                continue;
            }
            MapleCharacter curChar = getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if (curChar == null) {
                return 2;
            }
            if (curChar.getMarriageId() > 0) {
                return 3;
            }
            if (curChar.getGender() == getPlayer().getGender()) {
                return 4;
            }
            if ((curChar.getGender() == 0) && (!curChar.haveItem(1050121)) && (!curChar.haveItem(1050122)) && (!curChar.haveItem(1050113))) {
                return 5;
            }
            if ((curChar.getGender() == 1) && (!curChar.haveItem(1051129)) && (!curChar.haveItem(1051130)) && (!curChar.haveItem(1051114))) {
                return 5;
            }
            if (!curChar.haveItem(1112001)) {
                return 6;
            }
        }
        return 9;
    }

    public int getPartyFormID() {
        int curCharID = -1;
        if (getPlayer().getParty() == null) {
            curCharID = -1;
        } else if (getPlayer().getMarriageId() > 0) {
            curCharID = -2;
        } else if (getPlayer().getParty().getMembers().size() != 2) {
            curCharID = -3;
        }
        for (MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            if (chr.getId() == getPlayer().getId()) {
                continue;
            }
            MapleCharacter curChar = getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if (curChar == null) {
                curCharID = -4;
            } else {
                curCharID = chr.getId();
            }
        }
        return curCharID;
    }
    public void setattack(final int attack, final int minmapid, final int maxmapid) {
        c.getPlayer().setattack(1, attack, minmapid, maxmapid);
    }

    /**
     * 获取GM等级
     *
     * @return
     */
    public int getGMLevel() {
        return this.c.getPlayer().getGMLevel();
    }

    public void startLieDetector(boolean isItem) {
        this.c.getPlayer().startLieDetector(isItem);
    }
    
    /**
     * 重修系统
     *
     */
    
    public void Chongxiu(int lv, int str, int dex, int in, int luk) {
        this.c.getPlayer().Chongxiu(lv, str, dex, in, luk);
    }
    
    public void Chongxiu(int jobs, int lv, int str, int dex, int in, int luk, int hp, int mp, int reborn) {
        this.c.getPlayer().Chongxiu(jobs, lv, str, dex, in, luk, reborn);
    }
    
    public void Chongxiu(int lv, int sx) {
        this.c.getPlayer().Chongxiu(lv, sx);
    }
    
    /**
     * 发送世界地图祝福
     *
     */
    
    public void Maplen(String ns, int itemid, int time) {
        World.Broadcast.startMapEffect(ns, itemid, time);
    }
    
    public void Maplen(String ns, int itemid) {
        MaplePacketCreator.startMapEffect(ns, itemid, true);
    }
    
    public int getMaplewingmap(String ns, int ids) {
        return this.c.getPlayer().getMaplewingmap(ns, ids);
    }
    
    public int getMaplewingmap(String ns) {
        return this.c.getPlayer().getMaplewingmap(ns);
    }
    
    public void setMaplewingmap(String ns, int mapids, int ids) {
        this.c.getPlayer().setMaplewingmap(ns, mapids, ids);
    }
    
    public void setMaplewingmap(String ns, int mapids) {
        this.c.getPlayer().setMaplewingmap(ns, mapids);
    }
    
    public void setMaplewingmap(int mapids) {
        this.c.getPlayer().setMaplewingmap(mapids);
    }
    
    public void setMaplewingmap() {
        this.c.getPlayer().setMaplewingmap();
    }
    
    public String getMaplewinggpname(int ids) {
        return this.c.getPlayer().getMaplewinggpname(ids);
    }
    
    public int getMaplewinggpIdByName(String te) {
        return this.c.getPlayer().getMaplewinggpIdByName(te);
    }
    
    public int getMaplewinggp(String te, int ids) {
        return this.c.getPlayer().getMaplewinggp(te, ids);
    }
    
    public void addMaplewinggp(String names, int rate, int point, int lastpoint) {
        this.c.getPlayer().addMaplewinggp(names, rate, point, lastpoint);
        int idss = getMaplewinggpIdByName(names);
        String mds = "[ MapleWing ] : 新增股票 " + names + " 代码为：" + idss + "！具体交易信息为 倍率rate: " + rate + " 点数point: " + point + " 上一点数lastpoint: " + lastpoint + " ";
        this.c.getPlayer().dropMessage(-1, mds);
        this.c.getPlayer().dropMessage(5, mds);
        World.Broadcast.startMapEffect(mds, 5121009);
    }
    
    public void setMaplewinggp(String te, int tas, int ids) {
        int dss =  this.c.getPlayer().setMaplewinggp(te, tas, ids);
      //  int dsa = getMaplewinggp(te, ids);
        String names = getMaplewinggpname(ids);
        String mds = "当前的股票 " + names + " 的交易信息 " + te + " 被Maplewing金融中心设置为 " + tas;
        if (dss == -1) {
            World.Broadcast.startMapEffect(mds, 5121009);
            this.c.getPlayer().dropMessage(5, mds);
            
        } else {
            World.Broadcast.startMapEffect(mds + "  错误！！！请联系Maplewing进行修复！！", 5121009);
            this.c.getPlayer().dropMessage(5, mds + "  错误！！！请联系Maplewing进行修复！！");
        }
    }
    
    public void setMaplewingjr(String te, int tas) {
         int dss = this.c.getPlayer().setMaplewingjr(te, tas);
         String mds = "您的金融投资 " + te + " 被设置为 " + tas;
         String nam = getVipname();
         if (dss == -1) {
            this.c.getPlayer().dropMessage(-1, nam + mds);
            this.c.getPlayer().dropMessage(5, nam +  mds);
        } else {
            this.c.getPlayer().dropMessage(-1, nam + mds+ "错误！！！请联系Maplewing进行修复！！");
            this.c.getPlayer().dropMessage(5, nam + mds + "错误！！！请联系Maplewing进行修复！！");
        }
    }
    
    public void setMaplewingjr(String te, int tas, int ids) {
         int dss = this.c.getPlayer().setMaplewingjr(te, tas, ids);
         String mds = "您的金融投资 " + te + " 被设置为 " + tas;
         String nam = getVipname();
         if (dss == -1) {
            this.c.getPlayer().dropMessage(-1, nam + mds);
            this.c.getPlayer().dropMessage(5, nam + mds);
        } else {
            this.c.getPlayer().dropMessage(-1, nam + mds + "错误！！！请联系Maplewing进行修复！！");
            this.c.getPlayer().dropMessage(5, nam + mds + "错误！！！请联系Maplewing进行修复！！");
        }
         
    }
    
    public int getMaplewingjr(String te) {
        return this.c.getPlayer().getMaplewingjr(te);
    }
    
    public int getMaplewingjr(String te, int ids) {
        return this.c.getPlayer().getMaplewingjr(te, ids);
    }
    
    public void addMaplewingjr(String te, int tas) {
        int dss = this.c.getPlayer().addMaplewingjr(te, tas);
        String mds = "您的金融投资 " + te + " 增加了 " + tas;
        String nam = getVipname();
        if (dss == -1) {
            this.c.getPlayer().dropMessage(-1, mds);
            this.c.getPlayer().dropMessage(5, mds);
        } else {
            this.c.getPlayer().dropMessage(-1, nam + mds + "错误！！！请联系Maplewing进行修复！！");
            this.c.getPlayer().dropMessage(5, nam + mds + "错误！！！请联系Maplewing进行修复！！");
        }
    }
    
    public void addMaplewingjr(String te, int tas, int ids) {
         int dss = this.c.getPlayer().addMaplewingjr(te, tas, ids);
         String mds = "您的金融投资 " + te + " 增加了 " + tas;
         String nam = getVipname();
         if (dss == -1) {
            this.c.getPlayer().dropMessage(-1, nam + mds);
            this.c.getPlayer().dropMessage(5, nam + mds);
        } else {
            this.c.getPlayer().dropMessage(-1, nam + mds + "错误！！！请联系Maplewing进行修复！！");
            this.c.getPlayer().dropMessage(5, nam + mds + "错误！！！请联系Maplewing进行修复！！");
        }
         
    }
    
    public int getMaplewingJS(String te) {
        return this.c.getPlayer().getMaplewingJS(te);
        
    }
    
    public int getMaplewingZJ(int te, int ds) {
        return this.c.getPlayer().getMaplewingZJS(te, ds);
        
    }
    
    public int MaplwingSJTP(String te) {
        
        MapleItemInformationProvider iis = MapleItemInformationProvider.getInstance();
                    
        int reward = RandomRewards.get水晶天平();//随机取得抽奖物品
        if ((c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 2) || (c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 2) || (c.getPlayer().getInventory(MapleInventoryType.SETUP).getNumFreeSlot() < 2) || (c.getPlayer().getInventory(MapleInventoryType.CASH).getNumFreeSlot() < 2) || (c.getPlayer().getInventory(MapleInventoryType.ETC).getNumFreeSlot() < 2)) {
            c.getPlayer().dropMessage(5, "请确保你有足够的背包里的各个栏目有1个空间以上.");
           // return null;
            reward = 3010000;
         } else {
            
            Item items = MapleInventoryManipulator.addbyId_Gachapon(c, reward, (short) 1);
                    if (items == null) {
                        this.c.getPlayer().dropMessage(1, "获取 " + te +" 里的物品失败(所抽取的数据不存在)，请重试一次。");
                        c.getSession().write(MaplePacketCreator.enableActions());
                        reward = 3010000;
                       // return -1;
                    } else {
            
            c.getSession().write(MaplePacketCreator.getShowItemGain(reward, (short) 1, true));//给予抽奖物品
            //发送世界公告
            Broadcast.broadcastMessage(MaplePacketCreator.getGachaponMega(c.getPlayer().getName(), new StringBuilder().append(" : 从").append(te).append("中获得{").append(iis.getName(items.getItemId())).append("}！大家一起恭喜他（她）吧！！！！").toString(), items, (byte)2, c.getChannel()));
                    }
        }
        
        
        return reward;
    }
    
    
    public void MaplwingSJTPS(String te) {
        
        MapleItemInformationProvider iis = MapleItemInformationProvider.getInstance();
                    
        int reward = RandomRewards.get水晶天平();//随机取得抽奖物品
        if ((c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 2) || (c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 2) || (c.getPlayer().getInventory(MapleInventoryType.SETUP).getNumFreeSlot() < 2) || (c.getPlayer().getInventory(MapleInventoryType.CASH).getNumFreeSlot() < 2) || (c.getPlayer().getInventory(MapleInventoryType.ETC).getNumFreeSlot() < 2)) {
            c.getPlayer().dropMessage(5, "请确保你有足够的背包里的各个栏目有1个空间以上.");
           // return null;
         //   reward = 3010000;
         } else {
            
            Item items = MapleInventoryManipulator.addbyId_Gachapon(c, reward, (short) 1);
                    if (items == null) {
                        this.c.getPlayer().dropMessage(1, "获取 " + te +" 里的物品失败(所抽取的数据不存在)，请重试一次。");
                        c.getSession().write(MaplePacketCreator.enableActions());
                     //   reward = 3010000;
                       // return -1;
                    } else {
            
            c.getSession().write(MaplePacketCreator.getShowItemGain(reward, (short) 1, true));//给予抽奖物品
            //发送世界公告
            Broadcast.broadcastMessage(MaplePacketCreator.getGachaponMega(c.getPlayer().getName(), new StringBuilder().append(" : 从").append(te).append("中获得{").append(iis.getName(items.getItemId())).append("}！大家一起恭喜他（她）吧！！！！").toString(), items, (byte)2, c.getChannel()));
                    }
        }
        
        
      //  return reward;
    }
    
    
    public void MaplwingYZ(String te) {
        
        MapleItemInformationProvider iis = MapleItemInformationProvider.getInstance();
                    
        int reward = RandomRewards.get所有椅子();//随机取得抽奖物品
        if ((c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 2) || (c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 2) || (c.getPlayer().getInventory(MapleInventoryType.SETUP).getNumFreeSlot() < 2) || (c.getPlayer().getInventory(MapleInventoryType.CASH).getNumFreeSlot() < 2) || (c.getPlayer().getInventory(MapleInventoryType.ETC).getNumFreeSlot() < 2)) {
            c.getPlayer().dropMessage(5, "请确保你有足够的背包里的各个栏目有1个空间以上.");
           // return null;
         //   reward = 3010000;
         } else {
            
            Item items = MapleInventoryManipulator.addbyId_Gachapon(c, reward, (short) 1);
                    if (items == null) {
                        this.c.getPlayer().dropMessage(1, "获取 " + te +" 里的物品失败(所抽取的数据不存在)，请重试一次。");
                        c.getSession().write(MaplePacketCreator.enableActions());
                     //   reward = 3010000;
                       // return -1;
                    } else {
            
            c.getSession().write(MaplePacketCreator.getShowItemGain(reward, (short) 1, true));//给予抽奖物品
            //发送世界公告
            Broadcast.broadcastMessage(MaplePacketCreator.getGachaponMega(c.getPlayer().getName(), new StringBuilder().append(" : 从").append(te).append("中获得{").append(iis.getName(items.getItemId())).append("}！大家一起恭喜他（她）吧！！！！").toString(), items, (byte)2, c.getChannel()));
                    }
        }
        
        
      //  return reward;
    }
    
    public void gainMaples(int ds) {
        int md1 = (int)(Math.random()*100);
        int md2 = (int)(Math.random()*1000);
        int md3 = (int)(Math.random()*100);
        int md4 = (int)(Math.random()*50);
        int md5 = (int)(Math.random()*10);
        int md6 = (int)(Math.random()*5);
        int md7 = (int)(Math.random()*2);
        
        int md8 = (int)(Math.random()*100);
        int gas = 0;
        
        if (md1 > 99) {
            gas = md2;
        } else if (md1 > 95) {
            gas = md3;
        } else if (md1 > 90) {
            gas = md4;
        } else if (md1 > 20) {
            gas = md5;
        } else if (md1 > 10) {
            gas = md6;
        } else if (md1 >= 0) {
            gas = md7;
        }
        
        if (md8 > 90) {
            addMaplewing("mapley", gas*ds);
        } else if (md8 > 60) {
            addMaplewing("maple", gas*ds);
        } else  if (md8 > 50){
            this.c.getPlayer().modifyCSPoints(1, gas, true);
        } else  if (md8 > 30){
            this.c.getPlayer().modifyCSPoints(2, gas, true);
        } else {
            this.c.getPlayer().gainMeso(gas, true, true);
        }
        
        
        
    }
    
    
    public int getChongxiu() {
        return this.c.getPlayer().getMaplewing("chongxiu");
    }
    
    public void addChongxiu(int i) {
         this.c.getPlayer().addMaplewingjr("chongxiu", i);
    }
    
    public String 取得玩家委托任务名称(String lvs) {
        return this.c.getPlayer().取得玩家委托任务名称(lvs);
    }
    
    public void 增加玩家委托任务信息(String te, int tas, int ids) {
         this.c.getPlayer().增加玩家委托任务信息(te, tas, ids);
    }
    
    public void 增加玩家委托任务信息(String te, int tas) {
          this.c.getPlayer().增加玩家委托任务信息(te, tas);
     }
    
    public int 设置玩家委托任务信息(String te, int das) {
         return this.c.getPlayer().设置玩家委托任务信息(te, das);
     }
     
     
     public int 设置玩家委托任务信息(String te, int das, int ids) {
         return this.c.getPlayer().设置玩家委托任务信息(te, das, ids);
     }
     
     public int 设置委托任务信息(String te, int das, int ids) {
         return this.c.getPlayer().设置委托任务信息(te, das, ids);
     }
     
     public int 取得玩家委托任务信息(String te, int ids) {
         return this.c.getPlayer().取得玩家委托任务信息(te, ids);
     }
     
     public int 取得玩家委托任务信息(String te) {
         return this.c.getPlayer().取得玩家委托任务信息(te);
     }
     
     public String 取得委托玩家名称() {
         return this.c.getPlayer().取得委托玩家名称();
     }
     
     public String 取得委托玩家名称(int ids) {
         return this.c.getPlayer().取得委托玩家名称(ids);
     }
     
     public int 取得委托任务信息(String te, int ids) {
         return this.c.getPlayer().取得委托任务信息(te, ids);
     }
     
     public String 取得委托任务名称(int te, int ids) {
         return this.c.getPlayer().取得委托任务名称(te, ids);
     }
     
     public int 取得玩家可接委托任务数量() {
         return this.c.getPlayer().取得玩家可接委托任务数量();
     }
     
     public int 取得玩家可接委托任务最大数量() {
         return this.c.getPlayer().取得玩家可接委托任务最大数量();
     }
     
     public String 委托信息(int te, int 模式) {
         
         if (te < 1 || te > 9) {
             te =1;
         }
         int wt = 1;
         int kmob = -1;
         
         switch (te) {
             case 1:
                 wt = 取得玩家委托任务信息("renwu1");
                 kmob  = 取得玩家委托任务信息("mob1");
                 break;
             case 2:
                 wt = 取得玩家委托任务信息("renwu2");
                 kmob  = 取得玩家委托任务信息("mob2");
                 break;
             case 3:
                 wt = 取得玩家委托任务信息("renwu3");
                 kmob  = 取得玩家委托任务信息("mob3");
                 break;
             case 4:
                 wt = 取得玩家委托任务信息("renwu4");
                 kmob  = 取得玩家委托任务信息("mob4");
                 break;
             case 5:
                 wt = 取得玩家委托任务信息("renwu5");
                 kmob  = 取得玩家委托任务信息("mob5");
                 break;
             case 6:
                 wt = 取得玩家委托任务信息("renwu6");
                 kmob  = 取得玩家委托任务信息("mob6");
                 break;
             case 7:
                 wt = 取得玩家委托任务信息("renwu7");
                 kmob  = 取得玩家委托任务信息("mob7");
                 break;
             case 8:
                 wt = 取得玩家委托任务信息("renwu8");
                 kmob  = 取得玩家委托任务信息("mob8");
                 break;
             case 9:
                 wt = 取得玩家委托任务信息("renwu9");
                 kmob  = 取得玩家委托任务信息("mob9");
                 break;
         }
         
         String name = 取得委托任务名称(1, wt);
         String neirong = 取得委托任务名称(2, wt);
         int fnpc = 取得委托任务信息("fnpc", wt);
         int npc = 取得委托任务信息("npc", wt);
         int maple = 取得委托任务信息("reward1maple", wt);
         int mapley = 取得委托任务信息("reward2mapley", wt);
         int wmose = 取得委托任务信息("reward3wmose", wt);
         int emose = 取得委托任务信息("reward4emose", wt);
         int nx = 取得委托任务信息("reward5nx", wt);
         int rewarditem = 取得委托任务信息("rewarditem", wt);
         int itemsl = 取得委托任务信息("itemsl", wt);
         int exp = 取得委托任务信息("exp", wt);
         int meso = 取得委托任务信息("meso", wt);
         
         int map  = 取得委托任务信息("map", wt);
         int mob = 取得委托任务信息("mob", wt);
         int nmob = 取得委托任务信息("nmob", wt);
         
         int item = 取得委托任务信息("item", wt);
         int nitem = 取得委托任务信息("nitem", wt);
         int hitem = c.getPlayer().getItemQuantity(item);
         
         StringBuilder ms = new StringBuilder().append("#r┈┈┈━═☆#i4251202##r  委托任务信息  #i4251202# #r☆═━┈┈┈\r\n#r");
         if (wt > 0) {
         if (模式 == 1) {
             ms.append(getVipname()).append("  #d以下是您领取的委托任务 #r#e").append(te).append("#n#d 详细信息：\r\n");
         } else {
             ms.append("#d看看下面的委托任务 #r#e").append(te).append("#n#d 的详细信息吧，得赶紧完成呐！\r\n");
         }
         ms.append("#b委托任务编号：#e#r").append(wt).append("#n  #b委托任务名称： #g☆ #r").append(name).append(" #g☆\r\n");
         ms.append("#b任务发布").append((fnpc < 1000000) ? "玩家": "NPC").append("：#r").append((fnpc < 1000000) ? 取得委托玩家名称(fnpc): "#p" + fnpc + "#").append("#b  委托任务交付NPC：#r").append("#p").append(npc).append("#\r\n");
         ms.append("#g☆#b任务简介#g☆  \r\n#d").append(neirong).append("\r\n");
         ms.append("#b需要物品：  #r").append(((item > 1000000) ? "#i" + item + ":#  #b数量：#r" + nitem + "    #b已拥有数量：#r" + hitem :" 无")).append("\r\n");
         ms.append("#b目标怪物：  #r").append(((mob >= 100000) ? "#o" + mob + ":#  #b数量：#r" + nmob + "    #b已打猎数量：#r" + kmob:" 无")).append("\r\n");
         ms.append("#b执行地点：  #r").append((map > 0 ? "#m" + map + "#":" 任意地点")).append("    #b地点编号：#r").append(map).append("\r\n  ");
         ms.append("#g☆#b任务奖励#g☆  \r\n#d贡献点：").append((maple > 0 ? " #r#e" + maple + "#n": " 无")).append("  #d活跃点：").append((mapley > 0 ? " #e#r" + mapley + "#n": " 无")).append("  #d万级余额：").append((wmose > 0 ? " #e#r" + wmose + "#n": " 无")).append("\r\n#d亿级余额：").append((emose > 0 ? " #e#r" + emose + "#n" : " 无")).append("  #d点卷：").append((nx > 0 ? " #e#r" + nx + "#n" : " 无")).append("  #d奖励物品：").append((rewarditem > 0 ? " #i" + rewarditem + ":# 数量 #e#r" + itemsl + "#n": " 无")).append("\r\n").append("\r\n#d经验：#r#e").append(exp).append("#n  #d金币：#e#r").append(meso).append("#n\r\n");
         if ((hitem >= nitem) && (kmob >= nmob)) {
             ms.append("#d您已经完成了委托任务： #r").append(name).append("  #b赶快去找NPC：#r#p").append(npc).append("# #b领取奖励吧！\r\n");
         }
         
         } else if (wt != -1) {
             ms.append("\r\n#b对不起，你的委托任务 #e#r").append(te).append("#n  #b还未记录有委托任务哦！\r\n赶快去领取多几个任务吧！");
         } else {
             ms.append("\r\n#b对不起，你的委托任务 #e#r").append(te).append("#n  #b还未开放哦！\r\n等您的岛民等级达到 #e#r").append(te).append("#n #b系统将会自动帮您解锁！\r\n目前您的岛民等级为：#e#r").append(getMaplewing("cardlevel")).append("#n#d\r\n如果不行，就躲到委托任务那里看看！领取几个委托任务！");
         }
         
         return ms.toString();
         
     }
     
     public int getHours() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        return hour;
    }

     
     public String 发布系统委托信息s(int 数量) {
         if (数量 > 25) {
             数量 = 25;
         } else if (数量 < 10) {
             数量 = 11;
         }
         int mms = 1;
         if ((getHours() > 12) && (getHours() < 18)) {
             mms = (int)(Math.random()*5);
         } else if ((getHours() >= 18) && (getHours() < 24)) {
             mms = (int)(Math.random()*10);
         } else {
             mms = (int)(Math.random()*3);
         }
         
                 
         int level = getMaplewing("cardlevel");
         StringBuilder ms = new StringBuilder().append("#r┈┈┈━═☆#i4251202##r  系统委托任务  #i4251202# #r☆═━┈┈┈\r\n#r");
         ms.append(getVipname()).append(" 您好！ #d以下是截至 #r").append(FileoutputUtil.CurrentReadable_Time()).append("#d\r\n发布的委托任务信息：\r\n");
     //    ms.append("--------------------系统委托任务--------------------");
         int dds = 1;
         switch (mms) {
             case 2:
                 dds = 20;
                 break;
             case 3:
                 dds = 30;
                 break;
             case 4:
                 dds = 40;
                 break;
             case 5:
                 dds = 50;
                 break;
             case 6:
                 dds = 60;
                 break;
             case 7:
                 dds = 70;
                 break;
             case 8:
                 dds = 80;
                 break;
             case 9:
                 dds = 90;
                 break;    
         }
         
         for (int dd = dds;dd <= (数量 + dds); dd++) {
             if (取得委托任务信息("id", dd) != -2) {
                 if ((取得委托任务信息("maxcishu", dd) > 0) && (level >= 取得委托任务信息("level", dd)) && (取得委托任务信息("hastrue", dd) == 0)) {
                 ms.append("#L").append(dd).append("##g☆#b编号 #e#r").append(dd).append("#n#b#g☆#b  #r").append(取得委托任务名称(1, dd)).append("#l\r\n");
             //    ms.append("#b委托任务内容：#r").append(取得委托任务名称(2, dd)).append("\r\n");

                 }
             }
         }
         /*
         if (数量 > 0) {
             
             dds = 1000;
             for (int dd = dds;dd <= (数量 + 1000); dd++) {
              //   ms.append("------------------玩家发布的委托任务------------------");
                 if ((取得委托任务信息("maxcishu", dd) > 0) && (level >= 取得委托任务信息("level", dd)) && (取得委托任务信息("hastrue", dd) == 0)) {
                     ms.append("#L").append(dd).append("##g☆#b编号 #e#r").append(dd).append("#n#b#g☆#b  #r").append(取得委托任务名称(1, dd)).append("#l\r\n");
              //       ms.append("#b委托任务内容：#r").append(取得委托任务名称(2, dd)).append("\r\n");
                 }
                 
             }
         
         }
         * 
         */
         
         return ms.toString();
     }
     
     
     public String 发布系统委托信息(int 数量) {
         if (数量 > 25) {
             数量 = 25;
         } else if (数量 < 10) {
             数量 = 11;
         }
         int level = getMaplewing("cardlevel");
         StringBuilder ms = new StringBuilder().append("#r┈┈┈━═☆#i4251202##r  系统委托任务  #i4251202# #r☆═━┈┈┈\r\n#r");
         ms.append(getVipname()).append(" 您好！ #d以下是截至 #r").append(FileoutputUtil.CurrentReadable_Time()).append("#d\r\n发布的委托任务信息：\r\n");
     //    ms.append("--------------------系统委托任务--------------------");
         
         for (int dd = 1;dd <= 数量; dd++) {
             if (取得委托任务信息("id", dd) != -2) {
                 if ((取得委托任务信息("maxcishu", dd) > 0) && (level >= 取得委托任务信息("level", dd)) && (取得委托任务信息("hastrue", dd) == 0)) {
                 ms.append("#L").append(dd).append("##g☆#b编号 #e#r").append(dd).append("#n#b#g☆#b  #r").append(取得委托任务名称(1, dd)).append("#l\r\n");
             //    ms.append("#b委托任务内容：#r").append(取得委托任务名称(2, dd)).append("\r\n");

                 }
             }
         }
         return ms.toString();
     }
     
      public String 发布玩家委托信息(int 数量) {
         if (数量 > 25) {
             数量 = 25;
         } else if (数量 < 10) {
             数量 = 11;
         }
         int level = getMaplewing("cardlevel");
         StringBuilder ms = new StringBuilder().append("#r┈┈┈━═☆#i4251202##r  玩家委托任务  #i4251202# #r☆═━┈┈┈\r\n#r");
         ms.append(getVipname()).append(" 您好！ #d以下是截至 #r").append(FileoutputUtil.CurrentReadable_Time()).append("#d\r\n发布的委托任务信息：\r\n");
     //    ms.append("--------------------系统委托任务--------------------");
         
         for (int dd = 1000;dd <= (数量 + 1000); dd++) {
             if (取得委托任务信息("id", dd) != -2) {
                 if ((取得委托任务信息("maxcishu", dd) > 0) && (level >= 取得委托任务信息("level", dd)) && (取得委托任务信息("hastrue", dd) == 0)) {
                 ms.append("#L").append(dd).append("##g☆#b编号 #e#r").append(dd).append("#n#b#g☆#b  #r").append(取得委托任务名称(1, dd)).append("#l\r\n");
             //    ms.append("#b委托任务内容：#r").append(取得委托任务名称(2, dd)).append("\r\n");

                 }
             }
         }
         return ms.toString();
     }
      
      /**
     * 根据职业ID获取职业名
     *
     * @param id 职业ID
     * @returnf委托信息
     */
    public String getJobNameByIds(int id) {
        return MapleCarnivalChallenge.getJobNameByIdNull(id);
    }
    
    public String 领取委托任务(int 编号) {
        int renwu1 = 取得玩家委托任务信息("renwu1");
        int renwu2 = 取得玩家委托任务信息("renwu2");
        int renwu3 = 取得玩家委托任务信息("renwu3");
        int renwu4 = 取得玩家委托任务信息("renwu4");
        int renwu5 = 取得玩家委托任务信息("renwu5");
        int renwu6 = 取得玩家委托任务信息("renwu6");
        int renwu7 = 取得玩家委托任务信息("renwu7");
        int renwu8 = 取得玩家委托任务信息("renwu8");
        int renwu9 = 取得玩家委托任务信息("renwu9");
        
        int mob = 取得委托任务信息("mob", 编号);
    //    int nmob = 取得委托任务信息("nmob", 编号);
        int maxcishu = 取得委托任务信息("maxcishu", 编号);
        
        boolean 成功设置 = false;
        if (renwu1 == 0) {
            设置玩家委托任务信息("renwu1", 编号);
            if (mob > 0) {
            设置玩家委托任务信息("mobid1", mob);
            设置玩家委托任务信息("mob1", 0);
            }
            设置委托任务信息("maxcishu", maxcishu - 1, 编号);
            成功设置 = true;
        } else if (renwu2 == 0) {
            设置玩家委托任务信息("renwu2", 编号);
            if (mob > 0) {
            设置玩家委托任务信息("mobid2", mob);
            设置玩家委托任务信息("mob2", 0);
            }
            设置委托任务信息("maxcishu", maxcishu - 1, 编号);
            成功设置 = true;
        } else if (renwu3 == 0) {
            设置玩家委托任务信息("renwu3", 编号);
            if (mob > 0) {
            设置玩家委托任务信息("mobid3", mob);
            设置玩家委托任务信息("mob3", 0);
            }
            设置委托任务信息("maxcishu", maxcishu - 1, 编号);
            成功设置 = true;
        } else if (renwu4 == 0) {
            设置玩家委托任务信息("renwu4", 编号);
            if (mob > 0) {
            设置玩家委托任务信息("mobid4", mob);
            设置玩家委托任务信息("mob4", 0);
            }
            设置委托任务信息("maxcishu", maxcishu - 1, 编号);
            成功设置 = true;
        } else if (renwu5 == 0) {
            设置玩家委托任务信息("renwu5", 编号);
            if (mob > 0) {
            设置玩家委托任务信息("mobid5", mob);
            设置玩家委托任务信息("mob5", 0);
            }
            设置委托任务信息("maxcishu", maxcishu - 1, 编号);
            成功设置 = true;
        } else if (renwu6 == 0) {
            设置玩家委托任务信息("renwu6", 编号);
            if (mob > 0) {
            设置玩家委托任务信息("mobid6", mob);
            设置玩家委托任务信息("mob6", 0);
            }
            设置委托任务信息("maxcishu", maxcishu - 1, 编号);
            成功设置 = true;
        } else if (renwu7 == 0) {
            设置玩家委托任务信息("renwu7", 编号);
            if (mob > 0) {
            设置玩家委托任务信息("mobid7", mob);
            设置玩家委托任务信息("mob7", 0);
            }
            设置委托任务信息("maxcishu", maxcishu - 1, 编号);
            成功设置 = true;
        } else if (renwu8 == 0) {
            设置玩家委托任务信息("renwu8", 编号);
            if (mob > 0) {
            设置玩家委托任务信息("mobid8", mob);
            设置玩家委托任务信息("mob8", 0);
            }
            设置委托任务信息("maxcishu", maxcishu - 1, 编号);
            成功设置 = true;
        } else if (renwu9 == 0) {
            设置玩家委托任务信息("renwu9", 编号);
            if (mob > 0) {
            设置玩家委托任务信息("mobid9", mob);
            设置玩家委托任务信息("mob9", 0);
            }
            设置委托任务信息("maxcishu", maxcishu - 1, 编号);
            成功设置 = true;
        }
        
        if (成功设置 = true) {
        return 委托详细信息(编号, 2);
        } else {
            return "设置委托任务失败";
        }
        
    }
    
    public boolean 检察玩家是否已经领取所选择委托任务(int 编号) {
        int renwu1 = 取得玩家委托任务信息("renwu1");
        int renwu2 = 取得玩家委托任务信息("renwu2");
        int renwu3 = 取得玩家委托任务信息("renwu3");
        int renwu4 = 取得玩家委托任务信息("renwu4");
        int renwu5 = 取得玩家委托任务信息("renwu5");
        int renwu6 = 取得玩家委托任务信息("renwu6");
        int renwu7 = 取得玩家委托任务信息("renwu7");
        int renwu8 = 取得玩家委托任务信息("renwu8");
        int renwu9 = 取得玩家委托任务信息("renwu9");
        
        if ((renwu1 == 编号) || (renwu2 == 编号) || (renwu3 == 编号) || (renwu4 == 编号) || (renwu5 == 编号) || (renwu6 == 编号) || (renwu7 == 编号) || (renwu8 == 编号) || (renwu9 == 编号)) {
            return true;
        } else {
            return false;
        }
        
    }
    
    public int 检察玩家已领取委托任务编号(int 编号) {
        int renwu1 = 取得玩家委托任务信息("renwu1");
        int renwu2 = 取得玩家委托任务信息("renwu2");
        int renwu3 = 取得玩家委托任务信息("renwu3");
        int renwu4 = 取得玩家委托任务信息("renwu4");
        int renwu5 = 取得玩家委托任务信息("renwu5");
        int renwu6 = 取得玩家委托任务信息("renwu6");
        int renwu7 = 取得玩家委托任务信息("renwu7");
        int renwu8 = 取得玩家委托任务信息("renwu8");
        int renwu9 = 取得玩家委托任务信息("renwu9");
        
        int mm = 0;
        
        if (renwu1 == 编号) {
            mm = 1;
        } else if (renwu2 == 编号) {
            mm = 2;
        } else if (renwu3 == 编号) {
            mm = 3;
        } else if (renwu4 == 编号) {
            mm = 4;
        } else if (renwu5 == 编号) {
            mm = 5;
        } else if (renwu6 == 编号) {
            mm = 6;
        } else if (renwu7 == 编号) {
            mm = 7;
        } else if (renwu8 == 编号) {
            mm = 8;
        } else if (renwu9 == 编号) {
            mm = 9;
        }


        return mm;
        
    }
      
     
     public String 委托详细信息(int 编号, int 模式) {
         StringBuilder ms = new StringBuilder().append("#r┈┈┈━═☆#i4251202##r  委托详细信息  #i4251202# #r☆═━┈┈┈\r\n#r");
         if (模式 == 1) {
         ms.append(getVipname()).append(" 您好！ #d现在时间是 #r").append(FileoutputUtil.CurrentReadable_Time()).append("#k\r\n").append("以下是您选择的委托任务：\r\n");
         } else {
             ms.append(getVipname()).append(" ， #d现在时间是 #r").append(FileoutputUtil.CurrentReadable_Time());
         }
         
         if (取得委托任务信息("id", 编号) != -2) {
                 
         String name = 取得委托任务名称(1, 编号);
         String neirong = 取得委托任务名称(2, 编号);
         int fnpc = 取得委托任务信息("fnpc", 编号);
         int npc = 取得委托任务信息("npc", 编号);
         int maple = 取得委托任务信息("reward1maple", 编号);
         int mapley = 取得委托任务信息("reward2mapley", 编号);
         int wmose = 取得委托任务信息("reward3wmose", 编号);
         int emose = 取得委托任务信息("reward4emose", 编号);
         int nx = 取得委托任务信息("reward5nx", 编号);
         int rewarditem = 取得委托任务信息("rewarditem", 编号);
         int itemsl = 取得委托任务信息("itemsl", 编号);
         int exp = 取得委托任务信息("exp", 编号);
         int meso = 取得委托任务信息("meso", 编号);
         
         int map  = 取得委托任务信息("map", 编号);
         int mob = 取得委托任务信息("mob", 编号);
         int nmob = 取得委托任务信息("nmob", 编号);
         
         int item = 取得委托任务信息("item", 编号);
         int nitem = 取得委托任务信息("nitem", 编号);
         int hitem = c.getPlayer().getItemQuantity(item);
         
         int nlevel = 取得委托任务信息("nlevel", 编号);
         int nid = 取得委托任务信息("nid", 编号);
         int njob = 取得委托任务信息("njob", 编号);
         int nchongxiu = 取得委托任务信息("nchongxiu", 编号);
         int nguild = 取得委托任务信息("nguild", 编号);
         
         int maxcishu = 取得委托任务信息("maxcishu", 编号);
         
         if (模式 != 1) {
             ms.append("#d\r\n您已经成功领取委托任务：").append(" #g☆ #r").append(name).append(" #g☆\r\n");
         }
         
         ms.append("#b委托任务编号：#e#r").append(编号).append("#n  #b委托任务名称： #g☆ #r").append(name).append(" #g☆\r\n");
         ms.append("#b任务发布").append((fnpc < 1000000) ? "玩家": "NPC").append("：#r").append((fnpc < 1000000) ? 取得委托玩家名称(fnpc): "#p" + fnpc + "#").append("#b  委托任务交付NPC：#r").append("#p").append(npc).append("#\r\n");
         ms.append("#g☆#b任务简介#g☆  \r\n#d").append(neirong).append("\r\n");
         ms.append("#b需要物品：  #r").append(((item > 1000000) ? "#i" + item + ":#  #b数量：#r" + nitem + "    #b已拥有数量：#r" + hitem :" 无")).append("\r\n");
         ms.append("#b目标怪物：  #r").append(((mob >= 100000) ? "#o" + mob + ":#  #b数量：#r" + nmob :" 无")).append("\r\n");
         ms.append("#b执行地点：  #r").append((map > 0 ? "#m" + map + "#":" 任意地点")).append("    #b地点编号：#r").append(map).append("\r\n");
         if (模式 == 1) {
             
             int viplv = 取得委托任务信息("level", 编号);
         ms.append("#g☆#b任务要求#g☆  \r\n#d").append("要求等级：#r").append(nlevel).append(" 以上    #d要求重修等级：#r").append(nchongxiu).append(" 以上\r\n");
         ms.append((viplv > 0? "#d要求岛民等级：#r" + viplv + "    #d当前等级：#r" + getMaplewing("cardlevel") + "\r\n":""));
         ms.append((nid > 0? "要求角色编号：#r" + nid + "    #d当前编号：#r" + c.getPlayer().getId() + "\r\n": ""));
         ms.append((njob > 0? "要求职业：#r" + getJobNameByIds(njob) + "    #d当前职业：#r" + getJobNameByIds(c.getPlayer().getJob()) + "\r\n" : ""));
         ms.append((nguild > 0? "要求家族编号：#r" + nguild + "    #d当前家族编号：#r" + c.getPlayer().getGuildId() + "\r\n" : ""));
         ms.append("#g☆#b任务可接次数#g☆  #r还剩  #e#r").append(maxcishu).append("#n  次！\r\n");
         }
         
         ms.append("#g☆#b任务奖励#g☆  \r\n#d贡献点：").append((maple > 0 ? " #r#e" + maple + "#n": " 无")).append("  #d活跃点：").append((mapley > 0 ? " #e#r" + mapley + "#n": " 无")).append("  #d万级余额：").append((wmose > 0 ? " #e#r" + wmose + "#n": " 无")).append("\r\n#d亿级余额：").append((emose > 0 ? " #e#r" + emose + "#n" : " 无")).append("  #d点卷：").append((nx > 0 ? " #e#r" + nx + "#n" : " 无")).append("  #d奖励物品：").append((rewarditem > 0 ? " #i" + rewarditem + ":# 数量 #e#r" + itemsl + "#n": " 无")).append("\r\n#d经验：#r#e").append(exp).append("#n  #d金币：#e#r").append(meso).append("#n\r\n");
         
         ms.append("#b目前您可以领取的委托任务数量为：#e#r").append(取得玩家可接委托任务数量()).append("#n  \r\n").append("#b最大可接委托任务数量为：#e#r").append(取得玩家可接委托任务最大数量()).append("#n \r\n");
         
         if (模式 == 1) {
             
             if (maxcishu > 0) {
                 
                 if (检察玩家是否已经领取所选择委托任务(编号) == false) {

             if (取得玩家可接委托任务数量() > 0) {
                 
                 if ((nid > 0? c.getPlayer().getId() == nid: true) && (c.getPlayer().getLevel() >= nlevel) &&
                         (njob > 0? c.getPlayer().getJob() == njob : true) && (c.getPlayer().getReborns() >= nchongxiu) &&
                         (nguild > 0? c.getPlayer().getGuildId() == nguild : true)) {
                 ms.append("#b#L").append(编号).append("#领取委托任务 #g☆ #r").append(name).append(" #g☆ #l\r\n");
                 } else {
                     ms.append("#b对不起，您不符合该委托任务的要求！\r\n无法领取委托任务 #g☆ #r").append(name).append(" #g☆\r\n");
                 }
                 
             } else {
                 ms.append("#r对不起哦！您已经无法继续领取委托任务！\r\n");
             }
                 } else {
                     ms.append("#r对不起哦！您已经领取过该委托任务委托任务！\r\n").append("#b你可以输入 #e#r@wt ").append(检察玩家已领取委托任务编号(编号)).append("#n  #b查看已领取委托任务 #g☆ #r").append(name).append(" #g☆ #b的详细信息！");
                 }
             
             } else {
                 
                 ms.append("#r对不起哦！该任务已经无法继续领取，请更换其他委托任务吧！当然您也可以等待委托任务的刷新！\r\n");
                 
             }
             
             
             
         }
         
         ms.append("#b#L9998#返回主页面查看其他委托任务#l \r\n#L9999#结束对话#l\r\n");
         
         } else {
              ms.append("#r 该委托任务数据出现错误，编号有问题！该委托任务的编号为：#e#r").append(编号).append("#n \r\n#b请联系 MapleWing 进行修复！");
         }
             
         return ms.toString();
     }
     
     

     public void 发布玩家的委托任务(int ids, String names, String neirong, int mob, int nmob, int item, int nitem, int reward1wmose, int level) {
         addMaplewing("wmose", -reward1wmose);
        // addMaplewing("wmose", -reward1wmose);
         int exps = 0;
         int vips = getMaplewing("cardlevel");
         if (reward1wmose < 100) {
             exps = vips*10000;
         } else if (reward1wmose < 1000) {
             exps = vips*100000;
         } else if (reward1wmose < 10000) {
             exps = vips*1000000;
         } else if (reward1wmose < 100000) {
             exps = vips*10000000;
         } else if (reward1wmose < 1000000) {
             exps = vips*20000000;
         } else if (reward1wmose < 10000000) {
             exps = vips*50000000;
         } else if (reward1wmose < 100000000) {
             exps = vips*80000000;
         } else if (reward1wmose < 1000000000) {
             exps = vips*100000000;
         } else if (reward1wmose < 2100000000) {
             exps = vips*150000000;
         }
         
         this.c.getPlayer().发布玩家的委托任务(ids, names, neirong, mob, nmob, item, nitem, reward1wmose, exps, level);
         
     }
     
     public void 发布玩家的委托任务打猎怪物(int ids, String names, String neirong, int mob, int nmob, int reward1wmose, int level) {
         addMaplewing("wmose", -reward1wmose);
        // addMaplewing("wmose", -reward1wmose);
         int exps = 0;
         int vips = getMaplewing("cardlevel");
         if (reward1wmose < 100) {
             exps = vips*10000;
         } else if (reward1wmose < 1000) {
             exps = vips*100000;
         } else if (reward1wmose < 10000) {
             exps = vips*1000000;
         } else if (reward1wmose < 100000) {
             exps = vips*10000000;
         } else if (reward1wmose < 1000000) {
             exps = vips*20000000;
         } else if (reward1wmose < 10000000) {
             exps = vips*50000000;
         } else if (reward1wmose < 100000000) {
             exps = vips*80000000;
         } else if (reward1wmose < 1000000000) {
             exps = vips*100000000;
         } else if (reward1wmose < 2100000000) {
             exps = vips*150000000;
         }
         
         this.c.getPlayer().发布玩家的委托任务(ids, names, neirong, mob, nmob, 0, 0, reward1wmose, exps, level);
     }
     
     public void 发布玩家的委托任务收集物品(int ids, String names, String neirong, int item, int nitem, int reward1wmose, int level) {
         addMaplewing("wmose", -reward1wmose);
        // addMaplewing("wmose", -reward1wmose);
         int exps = 0;
         int vips = getMaplewing("cardlevel");
         if (reward1wmose < 100) {
             exps = vips*10000;
         } else if (reward1wmose < 1000) {
             exps = vips*100000;
         } else if (reward1wmose < 10000) {
             exps = vips*1000000;
         } else if (reward1wmose < 100000) {
             exps = vips*10000000;
         } else if (reward1wmose < 1000000) {
             exps = vips*20000000;
         } else if (reward1wmose < 10000000) {
             exps = vips*50000000;
         } else if (reward1wmose < 100000000) {
             exps = vips*80000000;
         } else if (reward1wmose < 1000000000) {
             exps = vips*100000000;
         } else if (reward1wmose < 2100000000) {
             exps = vips*150000000;
         }
         
         this.c.getPlayer().发布玩家的委托任务(ids, names, neirong, 0, 0, item, nitem, reward1wmose, exps, level);
     }
     
     public String 取得可领取奖励的委托任务() {
         
         StringBuilder ms = new StringBuilder().append("#r┈┈┈━═☆#i4251202##r  领取委托奖励  #i4251202# #r☆═━┈┈┈\r\n#r");
         ms.append(getVipname()).append("  ，#d我是 #p").append(this.id).append("# 以下是您可以想我提交的委托任务：\r\n");
         int renwu1 = 取得玩家委托任务信息("renwu1");
         int renwu2 = 取得玩家委托任务信息("renwu2");
         int renwu3 = 取得玩家委托任务信息("renwu3");
         int renwu4 = 取得玩家委托任务信息("renwu4");
         int renwu5 = 取得玩家委托任务信息("renwu5");
         int renwu6 = 取得玩家委托任务信息("renwu6");
         int renwu7 = 取得玩家委托任务信息("renwu7");
         int renwu8 = 取得玩家委托任务信息("renwu8");
         int renwu9 = 取得玩家委托任务信息("renwu9");
         
         
         if (renwu1 > 0) {
             int mob1 = 取得玩家委托任务信息("mob1");
             int nmob = 取得委托任务信息("nmob", renwu1);
             int item = 取得委托任务信息("item", renwu1);
             int nitem = 取得委托任务信息("nitem", renwu1);
             int hitem = c.getPlayer().getItemQuantity(item);
             int npcs = 取得委托任务信息("npc", renwu1);
             if ((mob1 >= nmob) && (nitem <= hitem) && (this.id == npcs)) {
                 String names = 取得委托任务名称(1, renwu1);
                 ms.append("#b#L").append(renwu1).append("#").append(names).append("#l\r\n");
             }
         }
         if (renwu2 > 0) {
             int mob1 = 取得玩家委托任务信息("mob2");
             int nmob = 取得委托任务信息("nmob", renwu2);
             int item = 取得委托任务信息("item", renwu2);
             int nitem = 取得委托任务信息("nitem", renwu2);
             int hitem = c.getPlayer().getItemQuantity(item);
             int npcs = 取得委托任务信息("npc", renwu2);
             if ((mob1 >= nmob) && (nitem <= hitem) && (this.id == npcs)) {
                 String names = 取得委托任务名称(1, renwu2);
                 ms.append("#b#L").append(renwu2).append("#").append(names).append("#l\r\n");
             }
         }
         if (renwu3 > 0) {
             int mob1 = 取得玩家委托任务信息("mob3");
             int nmob = 取得委托任务信息("nmob", renwu3);
             int item = 取得委托任务信息("item", renwu3);
             int nitem = 取得委托任务信息("nitem", renwu3);
             int hitem = c.getPlayer().getItemQuantity(item);
             int npcs = 取得委托任务信息("npc", renwu3);
             if ((mob1 >= nmob) && (nitem <= hitem) && (this.id == npcs)) {
                 String names = 取得委托任务名称(1, renwu3);
                 ms.append("#b#L").append(renwu3).append("#").append(names).append("#l\r\n");
             }
         }
         if (renwu4 > 0) {
             int mob1 = 取得玩家委托任务信息("mob4");
             int nmob = 取得委托任务信息("nmob", renwu4);
             int item = 取得委托任务信息("item", renwu4);
             int nitem = 取得委托任务信息("nitem", renwu4);
             int hitem = c.getPlayer().getItemQuantity(item);
             int npcs = 取得委托任务信息("npc", renwu4);
             if ((mob1 >= nmob) && (nitem <= hitem) && (this.id == npcs)) {
                 String names = 取得委托任务名称(1, renwu4);
                 ms.append("#b#L").append(renwu4).append("#").append(names).append("#l\r\n");
             }
         }
         if (renwu5 > 0) {
             int mob1 = 取得玩家委托任务信息("mob5");
             int nmob = 取得委托任务信息("nmob", renwu5);
             int item = 取得委托任务信息("item", renwu5);
             int nitem = 取得委托任务信息("nitem", renwu5);
             int hitem = c.getPlayer().getItemQuantity(item);
             int npcs = 取得委托任务信息("npc", renwu5);
             if ((mob1 >= nmob) && (nitem <= hitem) && (this.id == npcs)) {
                 String names = 取得委托任务名称(1, renwu5);
                 ms.append("#b#L").append(renwu5).append("#").append(names).append("#l\r\n");
             }
         }
         if (renwu6 > 0) {
             int mob1 = 取得玩家委托任务信息("mob6");
             int nmob = 取得委托任务信息("nmob", renwu6);
             int item = 取得委托任务信息("item", renwu6);
             int nitem = 取得委托任务信息("nitem", renwu6);
             int hitem = c.getPlayer().getItemQuantity(item);
             int npcs = 取得委托任务信息("npc", renwu6);
             if ((mob1 >= nmob) && (nitem <= hitem) && (this.id == npcs)) {
                 String names = 取得委托任务名称(1, renwu6);
                 ms.append("#b#L").append(renwu6).append("#").append(names).append("#l\r\n");
             }
         }
         if (renwu7 > 0) {
             int mob1 = 取得玩家委托任务信息("mob7");
             int nmob = 取得委托任务信息("nmob", renwu7);
             int item = 取得委托任务信息("item", renwu7);
             int nitem = 取得委托任务信息("nitem", renwu7);
             int hitem = c.getPlayer().getItemQuantity(item);
             int npcs = 取得委托任务信息("npc", renwu7);
             if ((mob1 >= nmob) && (nitem <= hitem) && (this.id == npcs)) {
                 String names = 取得委托任务名称(1, renwu7);
                 ms.append("#b#L").append(renwu7).append("#").append(names).append("#l\r\n");
             }
         }
         if (renwu8 > 0) {
             int mob1 = 取得玩家委托任务信息("mob8");
             int nmob = 取得委托任务信息("nmob", renwu8);
             int item = 取得委托任务信息("item", renwu8);
             int nitem = 取得委托任务信息("nitem", renwu8);
             int hitem = c.getPlayer().getItemQuantity(item);
             int npcs = 取得委托任务信息("npc", renwu8);
             if ((mob1 >= nmob) && (nitem <= hitem) && (this.id == npcs)) {
                 String names = 取得委托任务名称(1, renwu8);
                 ms.append("#b#L").append(renwu8).append("#").append(names).append("#l\r\n");
             }
         }
         if (renwu9 > 0) {
             int mob1 = 取得玩家委托任务信息("mob9");
             int nmob = 取得委托任务信息("nmob", renwu9);
             int item = 取得委托任务信息("item", renwu9);
             int nitem = 取得委托任务信息("nitem", renwu9);
             int hitem = c.getPlayer().getItemQuantity(item);
             int npcs = 取得委托任务信息("npc", renwu9);
             if ((mob1 >= nmob) && (nitem <= hitem) && (this.id == npcs)) {
                 String names = 取得委托任务名称(1, renwu9);
                 ms.append("#b#L").append(renwu9).append("#").append(names).append("#l\r\n");
             }
         }
         
         if (ms.length() > 1) {
             return ms.toString();
         } else {
             return "#你没有可以领取奖励的委托任务哦！你可以输入@wt 数字0-9查看你已经领取的委托任务完成进度。";
         }
         
     }
     
     public boolean 重置玩家委托任务(int 编号) {
         boolean 重置 = false;
         int renwu1 = 取得玩家委托任务信息("renwu1");
         int renwu2 = 取得玩家委托任务信息("renwu2");
         int renwu3 = 取得玩家委托任务信息("renwu3");
         int renwu4 = 取得玩家委托任务信息("renwu4");
         int renwu5 = 取得玩家委托任务信息("renwu5");
         int renwu6 = 取得玩家委托任务信息("renwu6");
         int renwu7 = 取得玩家委托任务信息("renwu7");
         int renwu8 = 取得玩家委托任务信息("renwu8");
         int renwu9 = 取得玩家委托任务信息("renwu9");
         if (renwu1 == 编号) {
             设置玩家委托任务信息("renwu1", 0);
             重置 = true;
         } else if (renwu2 == 编号) {
             设置玩家委托任务信息("renwu2", 0);
             重置 = true;
         } else if (renwu3 == 编号) {
             设置玩家委托任务信息("renwu3", 0);
             重置 = true;
         } else if (renwu4 == 编号) {
             设置玩家委托任务信息("renwu4", 0);
             重置 = true;
         } else if (renwu5 == 编号) {
             设置玩家委托任务信息("renwu5", 0);
             重置 = true;
         } else if (renwu6 == 编号) {
             设置玩家委托任务信息("renwu6", 0);
             重置 = true;
         } else if (renwu7 == 编号) {
             设置玩家委托任务信息("renwu7", 0);
             重置 = true;
         } else if (renwu8 == 编号) {
             设置玩家委托任务信息("renwu8", 0);
             重置 = true;
         } else if (renwu9 == 编号) {
             设置玩家委托任务信息("renwu9", 0);
             重置 = true;
         }
         
         return 重置;
     }
     
     
     public String 领取系统委托任务奖励(int 编号) {
         
         StringBuilder ms = new StringBuilder().append("#r┈┈┈━═☆#i4251202##r  领取委托奖励  #i4251202# #r☆═━┈┈┈\r\n#r");
         
         String names = 取得委托任务名称(1, 编号);
         
         int npcs = 取得委托任务信息("npc", 编号);
         
         if (this.id == npcs) {
         
         if (重置玩家委托任务(编号) == true) {
         
         ms.append(getVipname()).append(" #b你成功提交委托任务：#g☆ #r").append(names).append(" #g☆\r\n#b获得以下奖励：\r\n");
         
         int maple = 取得委托任务信息("reward1maple", 编号);
         int mapley = 取得委托任务信息("reward2mapley", 编号);
         int wmose = 取得委托任务信息("reward3wmose", 编号);
         int emose = 取得委托任务信息("reward4emose", 编号);
         int nx = 取得委托任务信息("reward5nx", 编号);
         int rewarditem = 取得委托任务信息("rewarditem", 编号);
         int itemsl = 取得委托任务信息("itemsl", 编号);
         int exp = 取得委托任务信息("exp", 编号);
         int meso = 取得委托任务信息("meso", 编号);
         
         if (maple > 0) {
             addMaplewing("maple", maple);
             ms.append("#d贡献点：#r#e").append(maple).append("#n\r\n");
         }
         if (mapley > 0) {
             addMaplewing("mapley", mapley);
             ms.append("#d活跃点：#r#e").append(mapley).append("#n\r\n");
         }
         if (wmose > 0) {
             addMaplewing("wmose", wmose);
             ms.append("#d万级余额：#r#e").append(wmose).append("#n\r\n");
         }
         if (emose > 0) {
             addMaplewing("emose", emose);
             ms.append("#d亿级余额：#r#e").append(emose).append("#n\r\n");
         }
         if (nx > 0) {
             gainNX(1, nx);
             ms.append("#d点卷：#r#e").append(nx).append("#n\r\n");
         }
         if (rewarditem > 1000000) {
            gainItem(rewarditem, itemsl);
            ms.append("#d物品：#r#i").append(rewarditem).append(":#    #d数量：#e#r").append(itemsl).append("#n\r\n");
         }
         if (exp > 0) {
             gainExp(exp);
             ms.append("#d经验：#r#e").append(exp).append("#n\r\n");
         }
         if ((meso > 0) && (c.getPlayer().getMeso() + meso < 2100000000)) {
             gainMeso(meso);
             ms.append("#d金币：#r#e").append(meso).append("#n\r\n");
         }
         
         } else {
             ms.append("#r领取任务奖励失败，原因：重置玩家委托任务 失败！请联系MapleWing 修复！\r\n领取任务编号：#e#r ").append(编号);
         }
         
         } else {
             ms.append("\r\n#b领取奖励失败！原因：所领取奖励找的 NPC 不正确！\r\n您应该是寻找 NPC ： #r#p").append(npcs).append("# #b领取 委托任务： #r").append(names).append("  #b的奖励！");
         }
         
         
         return ms.toString();
     }
     
     
    
}