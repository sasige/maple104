package scripting;

import client.*;
import client.inventory.*;
import constants.BattleConstants;
import constants.BattleConstants.PokedexEntry;
import constants.GameConstants;
import database.DatabaseConnection;
import handling.channel.ChannelServer;
import handling.channel.MapleGuildRanking;
import handling.channel.handler.PlayersHandler;
import handling.login.LoginInformationProvider;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import handling.world.World.Alliance;
import handling.world.World.Broadcast;
import handling.world.World.Family;
import handling.world.World.Find;
import handling.world.World.Guild;
import handling.world.exped.ExpeditionType;
import handling.world.guild.MapleGuild;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;
import javax.script.Invocable;
import org.apache.log4j.Logger;
import server.*;
import server.Timer.CloneTimer;
import server.life.MapleLifeFactory;
import server.life.MapleMonsterInformationProvider;
import server.life.MonsterDropEntry;
import server.maps.Event_DojoAgent;
import server.maps.Event_PyramidSubway;
import server.maps.FieldLimitType;
import server.maps.MapleMap;
import server.quest.MapleQuest;
import tools.FileoutputUtil;
import tools.MaplePacketCreator;
import tools.StringUtil;
import tools.Triple;
import tools.packet.FamilyPacket;
import tools.packet.GuildPacket;
import tools.packet.PlayerShopPacket;

public class NPCConversationManager extends AbstractPlayerInteraction {

    private static final Logger _log = Logger.getLogger(NPCConversationManager.class);
    private String getText;
    private byte type;
    private byte lastMsg = -1;
    private int npcMode = 0;
    public boolean pendingDisposal = false;
    private Invocable iv;
    private MapleCharacter chr;
    private int npc;

    public NPCConversationManager(MapleClient c, int npc, int questid, int itemid, byte type, int npcMode, Invocable iv) {
        super(c, npc, questid, itemid);
        this.type = type;
        this.npcMode = npcMode;
        this.iv = iv;
    }
    /*
    public void ZreHylvl1() { 
        MapleGuild.ZreHylvl(getClient(), npc); 
    } 
     
    public void ZreHyfame1() { 
        MapleGuild.ZreHyfame(getClient(), npc); 
        
    } public void ZreHymeso1() { 
        MapleGuild.ZreHymeso(getClient(), npc); 
    } 
    
     public void ZreHyzs1() { 
        MapleGuild.ZreHyzs(getClient(), npc); 
    } 
     
    public void ZreHypvpkills1() { 
        MapleGuild.ZreHypvpkills(getClient(), npc); 
        
    } public void ZreHypvpdeaths1() { 
        MapleGuild.ZreHypvpdeaths(getClient(), npc); 
    } 
    * 
    */


    public Invocable getIv() {
        return this.iv;
    }
    
    public int getVip1() {
        return getPlayer().getVip();
    }

    public int getNpc() {
        return this.id;
    }

    public int getQuest() {
        return this.id2;
    }

    public int getItem() {
        return this.id3;
    }

    public byte getType() {
        return this.type;
    }

    public int getNpcMode() {
        return this.npcMode;
    }

    public void safeDispose() {
        this.pendingDisposal = true;
    }

    public void dispose() {
        NPCScriptManager.getInstance().dispose(this.c);
    }

    public void askMapSelection(String sel) {
        if (this.lastMsg > -1) {
            return;
        }
        this.c.getSession().write(MaplePacketCreator.getMapSelection(this.id, sel));
        this.lastMsg = 16;
    }

    public void sendNext(String text) {
        sendNext(text, this.id);
    }

    public void sendNext(String text, int id) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            sendSimple(text);
            return;
        }
        this.c.getSession().write(MaplePacketCreator.getNPCTalk(id, (byte) 0, text, "00 01", (byte) 0));
        this.lastMsg = 0;
    }

    public void sendPlayerToNpc(String text) {
        sendNextS(text, (byte) 3, this.id);
    }

    public void sendNextNoESC(String text) {
        sendNextS(text, (byte) 1, this.id);
    }

    public void sendNextNoESC(String text, int id) {
        sendNextS(text, (byte) 1, id);
    }

    public void sendNextS(String text, byte type) {
        sendNextS(text, type, this.id);
    }

    public void sendNextS(String text, byte type, int idd) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            sendSimpleS(text, type);
            return;
        }
        this.c.getSession().write(MaplePacketCreator.getNPCTalk(this.id, (byte) 0, text, "00 01", type, idd));
        this.lastMsg = 0;
    }

    public void sendPrev(String text) {
        sendPrev(text, this.id);
    }

    public void sendPrev(String text, int id) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            sendSimple(text);
            return;
        }
        this.c.getSession().write(MaplePacketCreator.getNPCTalk(id, (byte) 0, text, "01 00", (byte) 0));
        this.lastMsg = 0;
    }

    public void sendPrevS(String text, byte type) {
        sendPrevS(text, type, this.id);
    }

    public void sendPrevS(String text, byte type, int idd) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            sendSimpleS(text, type);
            return;
        }
        this.c.getSession().write(MaplePacketCreator.getNPCTalk(this.id, (byte) 0, text, "01 00", type, idd));
        this.lastMsg = 0;
    }

    public void sendNextPrev(String text) {
        sendNextPrev(text, this.id);
    }

    public void sendNextPrev(String text, int id) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            sendSimple(text);
            return;
        }
        this.c.getSession().write(MaplePacketCreator.getNPCTalk(id, (byte) 0, text, "01 01", (byte) 0));
        this.lastMsg = 0;
    }

    public void PlayerToNpc(String text) {
        sendNextPrevS(text, (byte) 3);
    }

    public void sendNextPrevS(String text) {
        sendNextPrevS(text, (byte) 3);
    }

    public void sendNextPrevS(String text, byte type) {
        sendNextPrevS(text, type, this.id);
    }

    public void sendNextPrevS(String text, byte type, int idd) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            sendSimpleS(text, type);
            return;
        }
        this.c.getSession().write(MaplePacketCreator.getNPCTalk(this.id, (byte) 0, text, "01 01", type, idd));
        this.lastMsg = 0;
    }

    public void sendOk(String text) {
        sendOk(text, this.id);
    }

    public void sendOk(String text, int id) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            sendSimple(text);
            return;
        }
        this.c.getSession().write(MaplePacketCreator.getNPCTalk(id, (byte) 0, text, "00 00", (byte) 0));
        this.lastMsg = 0;
    }

    public void sendOkS(String text, byte type) {
        sendOkS(text, type, this.id);
    }

    public void sendOkS(String text, byte type, int idd) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            sendSimpleS(text, type);
            return;
        }
        this.c.getSession().write(MaplePacketCreator.getNPCTalk(this.id, (byte) 0, text, "00 00", type, idd));
        this.lastMsg = 0;
    }

    public void sendYesNo(String text) {
        sendYesNo(text, this.id);
    }

    public void sendYesNo(String text, int id) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            sendSimple(text);
            return;
        }
        this.c.getSession().write(MaplePacketCreator.getNPCTalk(id, (byte) 2, text, "", (byte) 0));
        this.lastMsg = 2;
    }

    public void sendYesNoS(String text, byte type) {
        sendYesNoS(text, type, this.id);
    }

    public void sendYesNoS(String text, byte type, int idd) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            sendSimpleS(text, type);
            return;
        }
        this.c.getSession().write(MaplePacketCreator.getNPCTalk(this.id, (byte) 2, text, "", type, idd));
        this.lastMsg = 2;
    }

    public void sendAcceptDecline(String text) {
        askAcceptDecline(text);
    }

    public void sendAcceptDeclineNoESC(String text) {
        askAcceptDeclineNoESC(text);
    }

    public void askAcceptDecline(String text) {
        askAcceptDecline(text, this.id);
    }

    public void askAcceptDecline(String text, int id) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            sendSimple(text);
            return;
        }
        this.lastMsg = 14;
        this.c.getSession().write(MaplePacketCreator.getNPCTalk(id, lastMsg, text, "", (byte) 0));
    }

    public void askAcceptDeclineNoESC(String text) {
        askAcceptDeclineNoESC(text, this.id);
    }

    public void askAcceptDeclineNoESC(String text, int id) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            sendSimple(text);
            return;
        }
        this.lastMsg = 14;
        this.c.getSession().write(MaplePacketCreator.getNPCTalk(id, lastMsg, text, "", (byte) 1));
    }

    public void askAvatar(String text, int[] styles, int card) {
        if (this.lastMsg > -1) {
            return;
        }
        this.c.getSession().write(MaplePacketCreator.getNPCTalkStyle(this.id, text, styles, card, false));
        this.lastMsg = 9;
    }

    public void sendSimple(String text) {
        sendSimple(text, this.id);
    }

    public void sendSimple(String text, int id) {
        if (this.lastMsg > -1) {
            return;
        }
        if (!text.contains("#L")) {
            sendNext(text);
            return;
        }
        this.c.getSession().write(MaplePacketCreator.getNPCTalk(id, (byte) 5, text, "", (byte) 0));
        this.lastMsg = 5;
    }

    public void sendSimpleS(String text, byte type) {
        sendSimpleS(text, type, this.id);
    }

    public void sendSimpleS(String text, byte type, int idd) {
        if (this.lastMsg > -1) {
            return;
        }
        if (!text.contains("#L")) {
            sendNextS(text, type);
            return;
        }
        this.c.getSession().write(MaplePacketCreator.getNPCTalk(this.id, (byte) 5, text, "", type, idd));
        this.lastMsg = 5;
    }

    public void sendStyle(String text, int[] styles, int card) {
        if (this.lastMsg > -1) {
            return;
        }
        this.c.getSession().write(MaplePacketCreator.getNPCTalkStyle(this.id, text, styles, card, false));
        this.lastMsg = 9;
    }

    public void sendAStyle(String text, int[] styles, int card) {
        if (this.lastMsg > -1) {
            return;
        }
        this.c.getSession().write(MaplePacketCreator.getNPCTalkStyle(this.id, text, styles, card, true));
        this.lastMsg = 10;
    }

    public void sendGetNumber(String text, int def, int min, int max) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            sendSimple(text);
            return;
        }
        this.c.getSession().write(MaplePacketCreator.getNPCTalkNum(this.id, text, def, min, max));
        this.lastMsg = 4;
    }

    public void sendGetText(String text) {
        sendGetText(text, this.id);
    }

    public void sendGetText(String text, int id) {
        if (this.lastMsg > -1) {
            return;
        }
        if (text.contains("#L")) {
            sendSimple(text);
            return;
        }
        this.c.getSession().write(MaplePacketCreator.getNPCTalkText(id, text));
        this.lastMsg = 3;
    }

    public void setGetText(String text) {
        this.getText = text;
    }

    public String getText() {
        return this.getText;
    }

    public void setHair(int hair) {
        getPlayer().setHair(hair);
        getPlayer().updateSingleStat(MapleStat.发型, hair);
        getPlayer().equipChanged();
    }

    public void setFace(int face) {
        getPlayer().setFace(face);
        getPlayer().updateSingleStat(MapleStat.脸型, face);
        getPlayer().equipChanged();
    }

    public void setSkin(int color) {
        getPlayer().setSkinColor((byte) color);
        getPlayer().updateSingleStat(MapleStat.皮肤, color);
        getPlayer().equipChanged();
    }

    public void setAndroidHair(int hair) {
        this.c.getPlayer().getAndroid().setHair(hair);
        this.c.getPlayer().getAndroid().saveToDb();
        this.c.getPlayer().setAndroid(this.c.getPlayer().getAndroid());
    }

    public void setAndroidFace(int face) {
        this.c.getPlayer().getAndroid().setFace(face);
        this.c.getPlayer().getAndroid().saveToDb();
        this.c.getPlayer().setAndroid(this.c.getPlayer().getAndroid());
    }

    public void setAndroidSkin(int skin) {
        this.c.getPlayer().getAndroid().setSkin(skin);
        this.c.getPlayer().getAndroid().saveToDb();
        this.c.getPlayer().setAndroid(this.c.getPlayer().getAndroid());
    }
    //设置角色等级
    public void setLV(short level) {
        this.c.getPlayer().setLevel(level);
        this.c.getPlayer().levelUp();
            if (c.getPlayer().getExp() < 0) {
                c.getPlayer().gainExp(-c.getPlayer().getExp(), false, false, true);
            }
    }
    //设置角色最大HP
    public void setMaxhp(int maxhp) {
        this.c.getPlayer().setMaxhp(maxhp);
    }
    //设置角色最大MP
    public void setMaxmp(int maxmp) {
        this.c.getPlayer().setMaxmp(maxmp);
    }

    public int setRandomAvatarA(int ticket, int[] args_all) {
        if (!haveItem(ticket)) {
            return -1;
        }
        gainItem(ticket, (short) -1);
        int args = args_all[Randomizer.nextInt(args_all.length)];
        if (args < 100) {
            this.c.getPlayer().getAndroid().setSkin(args);
        } else if (args < 30000) {
            this.c.getPlayer().getAndroid().setFace(args);
        } else {
            this.c.getPlayer().getAndroid().setHair(args);
        }
        this.c.getPlayer().getAndroid().saveToDb();
        this.c.getPlayer().setAndroid(this.c.getPlayer().getAndroid());
        return 1;
    }

    public int setAvatarA(int ticket, int args) {
        if (!haveItem(ticket)) {
            return -1;
        }
        gainItem(ticket, (short) -1);
        if (args < 100) {
            this.c.getPlayer().getAndroid().setSkin(args);
        } else if (args < 30000) {
            this.c.getPlayer().getAndroid().setFace(args);
        } else {
            this.c.getPlayer().getAndroid().setHair(args);
        }
        this.c.getPlayer().getAndroid().saveToDb();
        this.c.getPlayer().setAndroid(this.c.getPlayer().getAndroid());
        return 1;
    }

    public int setRandomAvatar(int ticket, int[] args_all) {
        if (!haveItem(ticket)) {
            return -1;
        }
        gainItem(ticket, (short) -1);
        int args = args_all[Randomizer.nextInt(args_all.length)];
        if (args < 100) {
            this.c.getPlayer().setSkinColor((byte) args);
            this.c.getPlayer().updateSingleStat(MapleStat.皮肤, args);
        } else if (args < 30000) {
            this.c.getPlayer().setFace(args);
            this.c.getPlayer().updateSingleStat(MapleStat.脸型, args);
        } else {
            this.c.getPlayer().setHair(args);
            this.c.getPlayer().updateSingleStat(MapleStat.发型, args);
        }
        this.c.getPlayer().equipChanged();
        return 1;
    }

    public int setAvatar(int ticket, int args) {
        if (!haveItem(ticket)) {
            return -1;
        }
        gainItem(ticket, (short) -1);
        if (args < 100) {
            this.c.getPlayer().setSkinColor((byte) args);
            this.c.getPlayer().updateSingleStat(MapleStat.皮肤, args);
        } else if (args < 30000) {
            this.c.getPlayer().setFace(args);
            this.c.getPlayer().updateSingleStat(MapleStat.脸型, args);
        } else {
            this.c.getPlayer().setHair(args);
            this.c.getPlayer().updateSingleStat(MapleStat.发型, args);
        }
        this.c.getPlayer().equipChanged();
        return 1;
    }
    
    
    public int setAvatar(int args) {
        if (args < 100) {
            this.c.getPlayer().setSkinColor((byte) args);
            this.c.getPlayer().updateSingleStat(MapleStat.皮肤, args);
        } else if (args < 30000) {
            this.c.getPlayer().setFace(args);
            this.c.getPlayer().updateSingleStat(MapleStat.脸型, args);
        } else {
            this.c.getPlayer().setHair(args);
            this.c.getPlayer().updateSingleStat(MapleStat.发型, args);
        }
        this.c.getPlayer().equipChanged();
        return 1;
    }
    

    public void sendStorage() {
        this.c.getPlayer().setConversation(4);
        this.c.getPlayer().getStorage().sendStorage(this.c, this.id);
    }

    public void openShop(int id) {
        MapleShopFactory.getInstance().getShop(id).sendShop(this.c);
    }

    public void openShopNPC(int id) {
        MapleShopFactory.getInstance().getShop(id).sendShop(this.c, this.id);
    }

    public int gainGachaponItem(int id, int quantity) {
        return gainGachaponItem(id, quantity, new StringBuilder().append(this.c.getPlayer().getMap().getStreetName()).append(" - ").append(this.c.getPlayer().getMap().getMapName()).toString());
    }

    public int gainGachaponItem(int id, int quantity, String msg) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        try {
            if (!ii.itemExists(id)) {
                return -1;
            }
            Item item = MapleInventoryManipulator.addbyId_Gachapon(this.c, id, (short) quantity);
            if (item == null) {
                return -1;
            }
            byte rareness = GameConstants.gachaponRareItem(item.getItemId());
            if (rareness > 0) {
                Broadcast.broadcastMessage(MaplePacketCreator.getGachaponMega(this.c.getPlayer().getName(), new StringBuilder().append(" : 从").append(msg).append("中获得{").append(ii.getName(item.getItemId())).append("}！大家一起恭喜他（她）吧！！！！").toString(), item, rareness, this.c.getChannel()));
            }
            return item.getItemId();
        } catch (Exception e) {
            _log.error("gainGachaponItem 错误", e);
        }
        return -1;
    }
    
    
    public int gainBaiBX(int id, int quantity, String msg) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        try {
            if (!ii.itemExists(id)) {
                return -1;
            }
            Item item = MapleInventoryManipulator.addbyId_Gachapon(this.c, id, (short) quantity);
            if (item == null) {
                return -1;
            }
            byte rareness = GameConstants.gachaponRareItem(item.getItemId());
            if (rareness > 0) {
                Broadcast.broadcastMessage(MaplePacketCreator.getGachaponMega(this.c.getPlayer().getName(), new StringBuilder().append(" : 从 ").append(msg).append(" 中获得{").append(ii.getName(item.getItemId())).append("}！大家一起恭喜他（她）吧！！！！").toString(), item, rareness, this.c.getChannel()));
            }
            return item.getItemId();
        } catch (Exception e) {
            _log.error("gainBaiBX 百宝抽奖 错误", e);
        }
        return -1;
    }
    
    

    /**
     * 更改职业
     *
     * @param job 职业ID
     */
    public void changeJob(int job) {
        this.c.getPlayer().changeJob(job);
    }

    public boolean isValidJob(int id) {
        return MapleCarnivalChallenge.getJobNameByIdNull(id) != null;
    }

    /**
     * 根据职业ID获取职业名
     *
     * @param id 职业ID
     * @return
     */
    public String getJobNameById(int id) {
        return MapleCarnivalChallenge.getJobNameByIdNull(id);
    }

    public void startQuest(int idd) {
        MapleQuest.getInstance(idd).start(getPlayer(), this.id);
    }

    public void completeQuest(int idd) {
        MapleQuest.getInstance(idd).complete(getPlayer(), this.id);
    }

    public void forfeitQuest(int idd) {
        MapleQuest.getInstance(idd).forfeit(getPlayer());
    }

    public void forceStartQuest() {
        MapleQuest.getInstance(this.id2).forceStart(getPlayer(), getNpc(), null);
    }

    @Override
    public void forceStartQuest(int idd) {
        MapleQuest.getInstance(idd).forceStart(getPlayer(), getNpc(), null);
    }

    public void forceStartQuest(String customData) {
        MapleQuest.getInstance(this.id2).forceStart(getPlayer(), getNpc(), customData);
    }

    public void forceCompleteQuest() {
        MapleQuest.getInstance(this.id2).forceComplete(getPlayer(), getNpc());
    }

    @Override
    public void forceCompleteQuest(int idd) {
        MapleQuest.getInstance(idd).forceComplete(getPlayer(), getNpc());
    }

    public String getQuestCustomData() {
        return this.c.getPlayer().getQuestNAdd(MapleQuest.getInstance(this.id2)).getCustomData();
    }

    public void setQuestCustomData(String customData) {
        getPlayer().getQuestNAdd(MapleQuest.getInstance(this.id2)).setCustomData(customData);
    }
    

    /**
     * 获得金币
     *
     * @return
     */
    public int getMeso() {
        return getPlayer().getMeso();
    }
    
        public void gainVip(int amount) {
        getPlayer().gainVip(amount);
        getPlayer().getClient().getSession().write(MaplePacketCreator.serverNotice(6, "你的VIP等级提升了：" + amount));
        getPlayer().getClient().getSession().write(MaplePacketCreator.serverNotice(6, "你现在的VIP等级为：" + getPlayer().getVip()));
    }

    public void setVip1(int amount) {
        getPlayer().setVip(amount);
        getPlayer().getClient().getSession().write(MaplePacketCreator.serverNotice(6, "你现在的VIP等级为：" + getPlayer().getVip()));
    }
     
    //public void gainChongxiu(int amount) {
    //    getPlayer().gainChongxiu(amount);
   // }

    /**
     * 获得属性点
     *
     * @param amount 属性点数量
     */
    public void gainAp(int amount) {
        this.c.getPlayer().gainAp((short) amount);
    }

    public void expandInventory(byte type, int amt) {
        this.c.getPlayer().expandInventory(type, amt);
    }

    public void unequipEverything() {
        MapleInventory equipped = getPlayer().getInventory(MapleInventoryType.EQUIPPED);
        MapleInventory equip = getPlayer().getInventory(MapleInventoryType.EQUIP);
        List itemIds = new LinkedList();
        for (Item item : equipped.newList()) {
            itemIds.add(Short.valueOf(item.getPosition()));
        }
        for (Iterator i$ = itemIds.iterator(); i$.hasNext();) {
            short ids = ((Short) i$.next()).shortValue();
            MapleInventoryManipulator.unequip(getC(), ids, equip.getNextFreeSlot());
        }
    }

    /**
     * 清理技能
     */
    public void clearSkills() {
        Map<Skill, SkillEntry> skills = new HashMap(getPlayer().getSkills());
        for (Entry<Skill, SkillEntry> skill : skills.entrySet()) {
            getPlayer().changeSkillLevel((Skill) skill.getKey(), 0, (byte) 0);
        }
        skills.clear();
    }

    /**
     * 根据技能ID获取技能等级
     *
     * @param skillid 技能ID
     * @return
     */
    public boolean hasSkill(int skillid) {
        Skill theSkill = SkillFactory.getSkill(skillid);
        if (theSkill != null) {
            return this.c.getPlayer().getSkillLevel(theSkill) > 0;
        }
        return false;
    }

    public void showEffect(boolean broadcast, String effect) {
        if (broadcast) {
            this.c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.showEffect(effect));
        } else {
            this.c.getSession().write(MaplePacketCreator.showEffect(effect));
        }
    }

    /**
     * 播放歌曲
     *
     * @param broadcast
     * @param sound
     */
    public void playSound(boolean broadcast, String sound) {
        if (broadcast) {
            this.c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.playSound(sound));
        } else {
            this.c.getSession().write(MaplePacketCreator.playSound(sound));
        }
    }

    public void environmentChange(boolean broadcast, String env) {
        if (broadcast) {
            this.c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.environmentChange(env, 2));
        } else {
            this.c.getSession().write(MaplePacketCreator.environmentChange(env, 2));
        }
    }

    public void updateBuddyCapacity(int capacity) {
        this.c.getPlayer().setBuddyCapacity((byte) capacity);
    }

    public int getBuddyCapacity() {
        return this.c.getPlayer().getBuddyCapacity();
    }

    public int partyMembersInMap() {
        int inMap = 0;
        if (getPlayer().getParty() == null) {
            return inMap;
        }
        for (MapleCharacter char2 : getPlayer().getMap().getCharactersThreadsafe()) {
            if ((char2.getParty() != null) && (char2.getParty().getId() == getPlayer().getParty().getId())) {
                inMap++;
            }
        }
        return inMap;
    }

    public List<MapleCharacter> getPartyMembers() {
        if (getPlayer().getParty() == null) {
            return null;
        }
        List<MapleCharacter> chars = new LinkedList<MapleCharacter>(); // creates an empty array full of shit..
        for (MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            for (ChannelServer channel : ChannelServer.getAllInstances()) {
                MapleCharacter ch = channel.getPlayerStorage().getCharacterById(chr.getId());
                if (ch != null) { // double check <3
                    chars.add(ch);
                }
            }
        }
        return chars;
    }

    public void warpPartyWithExp(int mapId, int exp) {
        if (getPlayer().getParty() == null) {
            warp(mapId, 0);
            gainExp(exp);
            return;
        }
        MapleMap target = getMap(mapId);
        for (MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            MapleCharacter curChar = this.c.getChannelServer().getPlayerStorage().getCharacterByName(chr.getName());
            if (((curChar.getEventInstance() == null) && (getPlayer().getEventInstance() == null)) || (curChar.getEventInstance() == getPlayer().getEventInstance())) {
                curChar.changeMap(target, target.getPortal(0));
                curChar.gainExp(exp, true, false, true);
            }
        }
    }

    public void warpPartyWithExpMeso(int mapId, int exp, int meso) {
        if (getPlayer().getParty() == null) {
            warp(mapId, 0);
            gainExp(exp);
            gainMeso(meso);
            return;
        }
        MapleMap target = getMap(mapId);
        for (MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            MapleCharacter curChar = this.c.getChannelServer().getPlayerStorage().getCharacterByName(chr.getName());
            if (((curChar.getEventInstance() == null) && (getPlayer().getEventInstance() == null)) || (curChar.getEventInstance() == getPlayer().getEventInstance())) {
                curChar.changeMap(target, target.getPortal(0));
                curChar.gainExp(exp, true, false, true);
                curChar.gainMeso(meso, true);
            }
        }
    }

    public MapleSquad getSquad(String type) {
        return this.c.getChannelServer().getMapleSquad(type);
    }

    public int getSquadAvailability(String type) {
        MapleSquad squad = this.c.getChannelServer().getMapleSquad(type);
        if (squad == null) {
            return -1;
        }
        return squad.getStatus();
    }

    public boolean registerSquad(String type, int minutes, String startText) {
        if (this.c.getChannelServer().getMapleSquad(type) == null) {
            MapleSquad squad = new MapleSquad(this.c.getChannel(), type, this.c.getPlayer(), minutes * 60 * 1000, startText);
            boolean ret = this.c.getChannelServer().addMapleSquad(squad, type);
            if (ret) {
                MapleMap map = this.c.getPlayer().getMap();
                map.broadcastMessage(MaplePacketCreator.getClock(minutes * 60));
                map.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append(this.c.getPlayer().getName()).append(startText).toString()));
            } else {
                squad.clear();
            }
            return ret;
        }
        return false;
    }

    public boolean getSquadList(String type, byte type_) {
        try {
            MapleSquad squad = this.c.getChannelServer().getMapleSquad(type);
            if (squad == null) {
                return false;
            }
            if ((type_ == 0) || (type_ == 3)) {
                sendNext(squad.getSquadMemberString(type_));
            } else if (type_ == 1) {
                sendSimple(squad.getSquadMemberString(type_));
            } else if (type_ == 2) {
                if (squad.getBannedMemberSize() > 0) {
                    sendSimple(squad.getSquadMemberString(type_));
                } else {
                    sendNext(squad.getSquadMemberString(type_));
                }
            }
            return true;
        } catch (NullPointerException ex) {
            FileoutputUtil.outputFileError("log\\Script_Except.log", ex);
        }
        return false;
    }

    public byte isSquadLeader(String type) {
        MapleSquad squad = this.c.getChannelServer().getMapleSquad(type);
        if (squad == null) {
            return -1;
        }
        if ((squad.getLeader() != null) && (squad.getLeader().getId() == this.c.getPlayer().getId())) {
            return 1;
        }
        return 0;
    }

    public boolean reAdd(String eim, String squad) {
        EventInstanceManager eimz = getDisconnected(eim);
        MapleSquad squadz = getSquad(squad);
        if ((eimz != null) && (squadz != null)) {
            squadz.reAddMember(getPlayer());
            eimz.registerPlayer(getPlayer());
            return true;
        }
        return false;
    }

    public void banMember(String type, int pos) {
        MapleSquad squad = this.c.getChannelServer().getMapleSquad(type);
        if (squad != null) {
            squad.banMember(pos);
        }
    }

    public void acceptMember(String type, int pos) {
        MapleSquad squad = this.c.getChannelServer().getMapleSquad(type);
        if (squad != null) {
            squad.acceptMember(pos);
        }
    }

    public int addMember(String type, boolean join) {
        try {
            MapleSquad squad = this.c.getChannelServer().getMapleSquad(type);
            if (squad != null) {
                return squad.addMember(this.c.getPlayer(), join);
            }
            return -1;
        } catch (NullPointerException ex) {
            FileoutputUtil.outputFileError("log\\Script_Except.log", ex);
        }
        return -1;
    }

    public byte isSquadMember(String type) {
        MapleSquad squad = this.c.getChannelServer().getMapleSquad(type);
        if (squad == null) {
            return -1;
        }
        if (squad.containsMember(this.c.getPlayer())) {
            return 1;
        }
        if (squad.isBanned(this.c.getPlayer())) {
            return 2;
        }
        return 0;
    }

    public void resetReactors() {
        getPlayer().getMap().resetReactors();
    }

    public void genericGuildMessage(int code) {
        this.c.getSession().write(GuildPacket.genericGuildMessage((byte) code));
    }

    public void disbandGuild() {
        int gid = this.c.getPlayer().getGuildId();
        if ((gid <= 0) || (this.c.getPlayer().getGuildRank() != 1)) {
            return;
        }
        Guild.disbandGuild(gid);
    }

    public void increaseGuildCapacity(boolean trueMax) {
        if ((this.c.getPlayer().getMeso() < 500000) && (!trueMax)) {
            this.c.getSession().write(MaplePacketCreator.serverNotice(1, "金币不足."));
            return;
        }
        int gid = this.c.getPlayer().getGuildId();
        if (gid <= 0) {
            return;
        }
        if (Guild.increaseGuildCapacity(gid, trueMax)) {
            if (!trueMax) {
                this.c.getPlayer().gainMeso(-500000, true, true);
            } else {
                gainGP(-25000);
            }
        } else if (!trueMax) {
            sendNext("请检查家族成员是否到达上限. (最大人数: 100)");
        } else {
            sendNext("Please check if your guild capacity is full, if you have the GP needed or if subtracting GP would decrease a guild level. (Limit: 200)");
        }
    }

    /**
     * 显示家族排行
     */
    public void displayGuildRanks() {
        this.c.getSession().write(GuildPacket.showGuildRanks(this.id, MapleGuildRanking.getInstance().getRank()));
    }

    public boolean removePlayerFromInstance() {
        if (this.c.getPlayer().getEventInstance() != null) {
            this.c.getPlayer().getEventInstance().removePlayer(this.c.getPlayer());
            return true;
        }
        return false;
    }

    public boolean isPlayerInstance() {
        return this.c.getPlayer().getEventInstance() != null;
    }

    public void changeStat(byte slot, int type, int amount) {
        Equip sel = (Equip) this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short) slot);
        switch (type) {
            case 0:
                sel.setStr((short) amount);
                break;
            case 1:
                sel.setDex((short) amount);
                break;
            case 2:
                sel.setInt((short) amount);
                break;
            case 3:
                sel.setLuk((short) amount);
                break;
            case 4:
                sel.setHp((short) amount);
                break;
            case 5:
                sel.setMp((short) amount);
                break;
            case 6:
                sel.setWatk((short) amount);
                break;
            case 7:
                sel.setMatk((short) amount);
                break;
            case 8:
                sel.setWdef((short) amount);
                break;
            case 9:
                sel.setMdef((short) amount);
                break;
            case 10:
                sel.setAcc((short) amount);
                break;
            case 11:
                sel.setAvoid((short) amount);
                break;
            case 12:
                sel.setHands((short) amount);
                break;
            case 13:
                sel.setSpeed((short) amount);
                break;
            case 14:
                sel.setJump((short) amount);
                break;
            case 15:
                sel.setUpgradeSlots((byte) amount);
                break;
            case 16:
                sel.setViciousHammer((byte) amount);
                break;
            case 17:
                sel.setLevel((byte) amount);
                break;
            case 18:
                sel.setState((byte) amount);
                break;
            case 19:
                sel.setEnhance((byte) amount);
                break;
            case 20:
                sel.setPotential1(amount);
                break;
            case 21:
                sel.setPotential2(amount);
                break;
            case 22:
                sel.setPotential3(amount);
                break;
            case 23:
                sel.setOwner(getText());
                break;
        }

        this.c.getPlayer().equipChanged();
        fakeRelog();
    }

    public void changePotentialStat(byte slot, int type, int amount) {
        Equip sel = (Equip) this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short) slot);
        switch (type) {
            case 0:
                if (amount == 0) {
                    sel.setPotential1(-5);
                } else if (amount == 1) {
                    sel.setPotential1(-6);
                } else if (amount == 2) {
                    sel.setPotential1(-7);
                } else {
                    if (amount != 3) {
                        break;
                    }
                    sel.setPotential1(-8);
                }
                break;
            case 1:
                sel.setPotential1(amount);
                sel.setStateMsg(3);
                break;
            case 2:
                sel.setPotential2(amount);
                sel.setStateMsg(3);
                break;
            case 3:
                sel.setPotential3(amount);
                sel.setStateMsg(3);
                break;
        }

        this.c.getPlayer().equipChanged();
        fakeRelog();
    }

    public void openDuey() {
        this.c.getPlayer().setConversation(2);
        this.c.getSession().write(MaplePacketCreator.sendDuey((byte) 9, null));
    }

    public void openMerchantItemStore() {
        this.c.getPlayer().setConversation(3);
        this.c.getSession().write(PlayerShopPacket.merchItemStore((byte) 37));
    }

    public void sendPVPWindow() {
        this.c.getSession().write(MaplePacketCreator.sendPVPWindow(0));
        this.c.getSession().write(MaplePacketCreator.sendPVPMaps());
    }

    public void sendPartyWindow() {
        this.c.getSession().write(MaplePacketCreator.sendPartyWindow(this.id));
    }

    public void sendPartyWindow(int id) {
        this.c.getSession().write(MaplePacketCreator.sendPartyWindow(id));
    }

    public void sendRepairWindow() {
        this.c.getSession().write(MaplePacketCreator.sendRepairWindow(this.id));
    }

    public void sendProfessionWindow() {
        this.c.getSession().write(MaplePacketCreator.sendProfessionWindow(0));
    }

    public void sendEventWindow() {
        this.c.getSession().write(MaplePacketCreator.sendEventWindow(0));
    }

    public int getDojoPoints() {
        return dojo_getPts();
    }

    public int getDojoRecord() {
        return this.c.getPlayer().getIntNoRecord(150101);
    }

    public void setDojoRecord(boolean reset) {
        if (reset) {
            this.c.getPlayer().getQuestNAdd(MapleQuest.getInstance(150101)).setCustomData("0");
            this.c.getPlayer().getQuestNAdd(MapleQuest.getInstance(150100)).setCustomData("0");
        } else {
            this.c.getPlayer().getQuestNAdd(MapleQuest.getInstance(150101)).setCustomData(String.valueOf(this.c.getPlayer().getIntRecord(150101) + 1));
        }
    }


    public boolean start_DojoAgent(boolean dojo, boolean party) {
        if (dojo) {
            return Event_DojoAgent.warpStartDojo(this.c.getPlayer(), party);
        }
        return Event_DojoAgent.warpStartAgent(this.c.getPlayer(), party);
    }

    public boolean start_PyramidSubway(int pyramid) {
        if (pyramid >= 0) {
            return Event_PyramidSubway.warpStartPyramid(this.c.getPlayer(), pyramid);
        }
        return Event_PyramidSubway.warpStartSubway(this.c.getPlayer());
    }

    public boolean bonus_PyramidSubway(int pyramid) {
        if (pyramid >= 0) {
            return Event_PyramidSubway.warpBonusPyramid(this.c.getPlayer(), pyramid);
        }
        return Event_PyramidSubway.warpBonusSubway(this.c.getPlayer());
    }

    public short getKegs() {
        return this.c.getChannelServer().getFireWorks().getKegsPercentage();
    }

    public void giveKegs(int kegs) {
        this.c.getChannelServer().getFireWorks().giveKegs(this.c.getPlayer(), kegs);
    }

    public short getSunshines() {
        return this.c.getChannelServer().getFireWorks().getSunsPercentage();
    }

    public void addSunshines(int kegs) {
        this.c.getChannelServer().getFireWorks().giveSuns(this.c.getPlayer(), kegs);
    }

    public short getDecorations() {
        return this.c.getChannelServer().getFireWorks().getDecsPercentage();
    }

    public void addDecorations(int kegs) {
        try {
            this.c.getChannelServer().getFireWorks().giveDecs(this.c.getPlayer(), kegs);
        } catch (Exception e) {
            _log.error("addDecorations 错误", e);
        }
    }

    public MapleCarnivalParty getCarnivalParty() {
        return this.c.getPlayer().getCarnivalParty();
    }

    public MapleCarnivalChallenge getNextCarnivalRequest() {
        return this.c.getPlayer().getNextCarnivalRequest();
    }

    public MapleCarnivalChallenge getCarnivalChallenge(MapleCharacter chr) {
        return new MapleCarnivalChallenge(chr);
    }

    /**
     * 角色所有属性最大化
     */
    public void maxStats() {
        Map statup = new EnumMap(MapleStat.class);
        this.c.getPlayer().getStat().str = 32767;
        this.c.getPlayer().getStat().dex = 32767;
        this.c.getPlayer().getStat().int_ = 32767;
        this.c.getPlayer().getStat().luk = 32767;

        this.c.getPlayer().getStat().maxhp = 99999;
        this.c.getPlayer().getStat().maxmp = 99999;
        this.c.getPlayer().getStat().setHp(99999, this.c.getPlayer());
        this.c.getPlayer().getStat().setMp(99999, this.c.getPlayer());

        statup.put(MapleStat.力量, 32767);
        statup.put(MapleStat.敏捷, 32767);
        statup.put(MapleStat.运气, 32767);
        statup.put(MapleStat.智力, 32767);
        statup.put(MapleStat.HP, 99999);
        statup.put(MapleStat.MAXHP, 99999);
        statup.put(MapleStat.MP, 99999);
        statup.put(MapleStat.MAXMP, 99999);
        this.c.getPlayer().getStat().recalcLocalStats(this.c.getPlayer());
        this.c.getSession().write(MaplePacketCreator.updatePlayerStats(statup, this.c.getPlayer()));
    }
    
    public void setLevel(short level) {
        c.getPlayer().setLevel(level);
            c.getPlayer().levelUp();
            if (c.getPlayer().getExp() < 0) {
                c.getPlayer().gainExp(-c.getPlayer().getExp(), false, false, true);
            }
    }
    
    /**
     * 修改设置重修角色属性
     */
    
    public void Chongxiusx(int str, int dex, int int_, int luk) {
        Map statup = new EnumMap(MapleStat.class);

       // this.c.getPlayer().getStat().level = (short) level;
        this.c.getPlayer().getStat().str = (short) str;
        this.c.getPlayer().getStat().dex = (short) dex;
        this.c.getPlayer().getStat().int_ = (short) int_;
        this.c.getPlayer().getStat().luk = (short) luk;

       // this.c.getPlayer().getStat().maxhp = (int) Maxhp;
       // this.c.getPlayer().getStat().maxmp = (int) Maxmp;
       // this.c.getPlayer().getStat().setLevel(level, this.c.getPlayer());
        //this.c.getPlayer().getStat().setMaxHp(Maxhp, this.c.getPlayer());
        //this.c.getPlayer().getStat().setMaxMp(Maxmp, this.c.getPlayer());
       // this.c.getPlayer().getStat().setHp(HP, this.c.getPlayer());
       // this.c.getPlayer().getStat().setMp(MP, this.c.getPlayer());

       // statup.put(MapleStat.等级, level);
        statup.put(MapleStat.力量, str);
        statup.put(MapleStat.敏捷, dex);
        statup.put(MapleStat.运气, luk);
        statup.put(MapleStat.智力, int_);
       // statup.put(MapleStat.HP, HP);
       // statup.put(MapleStat.MAXHP, Maxhp);
       // statup.put(MapleStat.MP, MP);
       // statup.put(MapleStat.MAXMP, Maxmp);
        this.c.getPlayer().getStat().recalcLocalStats(this.c.getPlayer());
        //this.c.getPlayer().resetChongxiusx1(level, str, dex, int_, luk, Maxhp, HP, Maxmp, MP);
        this.c.getSession().write(MaplePacketCreator.updatePlayerStats(statup, this.c.getPlayer()));
    }
    

    
    /**
     * 修改设置重修角色属性2
     */
    
    public void Chongxiusx2(int level, int str, int dex, int int_, int luk, int Maxhp, int HP, int Maxmp, int MP) {
        Map statup = new EnumMap(MapleStat.class);

        this.c.getPlayer().getStat().level = (short) level;
        this.c.getPlayer().getStat().str = (short) str;
        this.c.getPlayer().getStat().dex = (short) dex;
        this.c.getPlayer().getStat().int_ = (short) int_;
        this.c.getPlayer().getStat().luk = (short) luk;

        this.c.getPlayer().getStat().maxhp = (int) Maxhp;
        this.c.getPlayer().getStat().maxmp = (int) Maxmp;
        this.c.getPlayer().getStat().setLevel(level, this.c.getPlayer());
        this.c.getPlayer().getStat().setMaxHp(Maxhp, this.c.getPlayer());
        this.c.getPlayer().getStat().setMaxMp(Maxmp, this.c.getPlayer());
        this.c.getPlayer().getStat().setHp(HP, this.c.getPlayer());
        this.c.getPlayer().getStat().setMp(MP, this.c.getPlayer());

        statup.put(MapleStat.等级, level);
        statup.put(MapleStat.力量, str);
        statup.put(MapleStat.敏捷, dex);
        statup.put(MapleStat.运气, luk);
        statup.put(MapleStat.智力, int_);
        statup.put(MapleStat.HP, HP);
        statup.put(MapleStat.MAXHP, Maxhp);
        statup.put(MapleStat.MP, MP);
        statup.put(MapleStat.MAXMP, Maxmp);
        this.c.getPlayer().getStat().recalcLocalStats(this.c.getPlayer());
        this.c.getPlayer().resetChongxiusx1(level, str, dex, int_, luk, Maxhp, HP, Maxmp, MP);
        this.c.getSession().write(MaplePacketCreator.updatePlayerStats(statup, this.c.getPlayer()));
    }
    

    
        
     /**
     * 修改设置重修角色属性  会保留重置后AP
     */

    public void resetChongxiusx1(int level, int str, int dex, int z, int luk, int maxhp, int HP, int maxmp, int MP) {
        this.c.getPlayer().resetChongxiusx1(level, str, dex, z, luk, maxhp, HP, maxmp, MP);
    }

    public Triple<String, Map<Integer, String>, Long> getSpeedRun(String typ) {
        ExpeditionType types = ExpeditionType.valueOf(typ);
        if (SpeedRunner.getSpeedRunData(types) != null) {
            return SpeedRunner.getSpeedRunData(types);
        }
        return new Triple("", new HashMap(), Long.valueOf(0L));
    }

    public boolean getSR(Triple<String, Map<Integer, String>, Long> ma, int sel) {
        if ((((Map) ma.mid).get(Integer.valueOf(sel)) == null) || (((String) ((Map) ma.mid).get(Integer.valueOf(sel))).length() <= 0)) {
            dispose();
            return false;
        }
        sendOk((String) ((Map) ma.mid).get(Integer.valueOf(sel)));
        return true;
    }

    public Equip getEquip(int itemid) {
        return (Equip) MapleItemInformationProvider.getInstance().getEquipById(itemid);
    }

    public void setExpiration(Object statsSel, long expire) {
        if ((statsSel instanceof Equip)) {
            ((Equip) statsSel).setExpiration(System.currentTimeMillis() + expire * 24L * 60L * 60L * 1000L);
        }
    }

    public void setLock(Object statsSel) {
        if ((statsSel instanceof Equip)) {
            Equip eq = (Equip) statsSel;
            if (eq.getExpiration() == -1L) {
                eq.setFlag((short) (byte) (eq.getFlag() | ItemFlag.LOCK.getValue()));
            } else {
                eq.setFlag((short) (byte) (eq.getFlag() | ItemFlag.UNTRADEABLE.getValue()));
            }
        }
    }

    public boolean addFromDrop(Object statsSel) {
        if ((statsSel instanceof Item)) {
            Item it = (Item) statsSel;
            return (MapleInventoryManipulator.checkSpace(getClient(), it.getItemId(), it.getQuantity(), it.getOwner())) && (MapleInventoryManipulator.addFromDrop(getClient(), it, false));
        }
        return false;
    }

    public boolean replaceItem(int slot, int invType, Object statsSel, int offset, String type) {
        return replaceItem(slot, invType, statsSel, offset, type, false);
    }

    public boolean replaceItem(int slot, int invType, Object statsSel, int offset, String type, boolean takeSlot) {
        MapleInventoryType inv = MapleInventoryType.getByType((byte) invType);
        if (inv == null) {
            return false;
        }
        Item item = getPlayer().getInventory(inv).getItem((short) (byte) slot);
        if ((item == null) || ((statsSel instanceof Item))) {
            item = (Item) statsSel;
        }
        if (offset > 0) {
            if (inv != MapleInventoryType.EQUIP) {
                return false;
            }
            Equip eq = (Equip) item;
            if (takeSlot) {
                if (eq.getUpgradeSlots() < 1) {
                    return false;
                }
                eq.setUpgradeSlots((byte) (eq.getUpgradeSlots() - 1));

                if (eq.getExpiration() == -1L) {
                    eq.setFlag((short) (byte) (eq.getFlag() | ItemFlag.LOCK.getValue()));
                } else {
                    eq.setFlag((short) (byte) (eq.getFlag() | ItemFlag.UNTRADEABLE.getValue()));
                }
            }
            if (type.equalsIgnoreCase("Slots")) {
                eq.setUpgradeSlots((byte) (eq.getUpgradeSlots() + offset));
                eq.setViciousHammer((byte) (eq.getViciousHammer() + offset));
            } else if (type.equalsIgnoreCase("Level")) {
                eq.setLevel((byte) (eq.getLevel() + offset));
            } else if (type.equalsIgnoreCase("Hammer")) {
                eq.setViciousHammer((byte) (eq.getViciousHammer() + offset));
            } else if (type.equalsIgnoreCase("STR")) {
                eq.setStr((short) (eq.getStr() + offset));
            } else if (type.equalsIgnoreCase("DEX")) {
                eq.setDex((short) (eq.getDex() + offset));
            } else if (type.equalsIgnoreCase("INT")) {
                eq.setInt((short) (eq.getInt() + offset));
            } else if (type.equalsIgnoreCase("LUK")) {
                eq.setLuk((short) (eq.getLuk() + offset));
            } else if (type.equalsIgnoreCase("HP")) {
                eq.setHp((short) (eq.getHp() + offset));
            } else if (type.equalsIgnoreCase("MP")) {
                eq.setMp((short) (eq.getMp() + offset));
            } else if (type.equalsIgnoreCase("WATK")) {
                eq.setWatk((short) (eq.getWatk() + offset));
            } else if (type.equalsIgnoreCase("MATK")) {
                eq.setMatk((short) (eq.getMatk() + offset));
            } else if (type.equalsIgnoreCase("WDEF")) {
                eq.setWdef((short) (eq.getWdef() + offset));
            } else if (type.equalsIgnoreCase("MDEF")) {
                eq.setMdef((short) (eq.getMdef() + offset));
            } else if (type.equalsIgnoreCase("ACC")) {
                eq.setAcc((short) (eq.getAcc() + offset));
            } else if (type.equalsIgnoreCase("Avoid")) {
                eq.setAvoid((short) (eq.getAvoid() + offset));
            } else if (type.equalsIgnoreCase("Hands")) {
                eq.setHands((short) (eq.getHands() + offset));
            } else if (type.equalsIgnoreCase("Speed")) {
                eq.setSpeed((short) (eq.getSpeed() + offset));
            } else if (type.equalsIgnoreCase("Jump")) {
                eq.setJump((short) (eq.getJump() + offset));
            } else if (type.equalsIgnoreCase("ItemEXP")) {
                eq.setItemEXP(eq.getItemEXP() + offset);
            } else if (type.equalsIgnoreCase("Expiration")) {
                eq.setExpiration(eq.getExpiration() + offset);
            } else if (type.equalsIgnoreCase("Flag")) {
                eq.setFlag((short) (byte) (eq.getFlag() + offset));
            }
            item = eq.copy();
        }
        MapleInventoryManipulator.removeFromSlot(getClient(), inv, (short) slot, item.getQuantity(), false);
        return MapleInventoryManipulator.addFromDrop(getClient(), item, false);
    }

    public boolean replaceItem(int slot, int invType, Object statsSel, int upgradeSlots) {
        return replaceItem(slot, invType, statsSel, upgradeSlots, "Slots");
    }

    public boolean isCash(int itemId) {
        return MapleItemInformationProvider.getInstance().isCash(itemId);
    }

    public int getTotalStat(int itemId) {
        return MapleItemInformationProvider.getInstance().getTotalStat((Equip) MapleItemInformationProvider.getInstance().getEquipById(itemId));
    }

    public int getReqLevel(int itemId) {
        return MapleItemInformationProvider.getInstance().getReqLevel(itemId);
    }

    public MapleStatEffect getEffect(int buff) {
        return MapleItemInformationProvider.getInstance().getItemEffect(buff);
    }

    public void buffGuild(int buff, int duration, String msg) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        MapleStatEffect mse;
        if ((ii.getItemEffect(buff) != null) && (getPlayer().getGuildId() > 0)) {
            mse = ii.getItemEffect(buff);
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
                    if (chr.getGuildId() == getPlayer().getGuildId()) {
                        mse.applyTo(chr, chr, true, null, duration);
                        chr.dropMessage(5, new StringBuilder().append("Your guild has gotten a ").append(msg).append(" buff.").toString());
                    }
                }
            }
        }
    }

    public boolean createAlliance(String alliancename) {
        MapleParty pt = this.c.getPlayer().getParty();
        MapleCharacter otherChar = this.c.getChannelServer().getPlayerStorage().getCharacterById(pt.getMemberByIndex(1).getId());
        if ((otherChar == null) || (otherChar.getId() == this.c.getPlayer().getId())) {
            return false;
        }
        try {
            return Alliance.createAlliance(alliancename, this.c.getPlayer().getId(), otherChar.getId(), this.c.getPlayer().getGuildId(), otherChar.getGuildId());
        } catch (Exception re) {
            _log.error("createAlliance 错误", re);
        }
        return false;
    }

    public boolean addCapacityToAlliance() {
        try {
            MapleGuild gs = Guild.getGuild(this.c.getPlayer().getGuildId());
            if ((gs != null) && (this.c.getPlayer().getGuildRank() == 1) && (this.c.getPlayer().getAllianceRank() == 1)
                    && (Alliance.getAllianceLeader(gs.getAllianceId()) == this.c.getPlayer().getId()) && (Alliance.changeAllianceCapacity(gs.getAllianceId()))) {
                gainMeso(-10000000);
                return true;
            }
        } catch (Exception re) {
            _log.error("addCapacityToAlliance 错误", re);
        }
        return false;
    }

    public boolean disbandAlliance() {
        try {
            MapleGuild gs = Guild.getGuild(this.c.getPlayer().getGuildId());
            if ((gs != null) && (this.c.getPlayer().getGuildRank() == 1) && (this.c.getPlayer().getAllianceRank() == 1)
                    && (Alliance.getAllianceLeader(gs.getAllianceId()) == this.c.getPlayer().getId()) && (Alliance.disbandAlliance(gs.getAllianceId()))) {
                return true;
            }
        } catch (Exception re) {
            _log.error("disbandAlliance 错误", re);
        }
        return false;
    }

    public byte getLastMsg() {
        return this.lastMsg;
    }

    public void setLastMsg(byte last) {
        this.lastMsg = last;
    }
    

    /**
     * 技能全满
     */
    public void maxAllSkills() {
        for (Skill skil : SkillFactory.getAllSkills()) {
            if ((GameConstants.isApplicableSkill(skil.getId())) && (skil.getId() < 90000000)) {
                teachSkill(skil.getId(), (byte) skil.getMaxLevel(), (byte) skil.getMaxLevel());
            }
        }
    }

    /**
     * 根据职业满技能
     */
    public void maxSkillsByJob() {
        for (Skill skil : SkillFactory.getAllSkills()) {
            if ((GameConstants.isApplicableSkill(skil.getId())) && (skil.canBeLearnedBy(getPlayer().getJob()))) {
                teachSkill(skil.getId(), (byte) skil.getMaxLevel(), (byte) skil.getMaxLevel());
                if ((skil.getMaxLevel() >= 5) && (ServerProperties.ShowPacket()) && (getPlayer().isGM())) {
                    String job = new StringBuilder().append("Skill\\").append(MapleCarnivalChallenge.getJobNameById(getPlayer().getJob())).append(".txt").toString();
                    String txt = new StringBuilder().append("public static final int ").append(skil.getName()).append(" = ").append(skil.getId()).append("; //技能最大等级").append(skil.getMaxLevel()).toString();
                    FileoutputUtil.log(job, txt, true);
                }
            }
        }
    }
    
     /**
     * 修改设置角色属性
     */

    public void resetStats(int str, int dex, int z, int luk) {
        this.c.getPlayer().resetStats(str, dex, z, luk);
    }


    public boolean dropItem(int slot, int invType, int quantity) {
        MapleInventoryType inv = MapleInventoryType.getByType((byte) invType);
        if (inv == null) {
            return false;
        }
        return MapleInventoryManipulator.drop(this.c, inv, (short) slot, (short) quantity, true);
    }

    public List<Integer> getAllPotentialInfo() {
        List list = new ArrayList(MapleItemInformationProvider.getInstance().getAllPotentialInfo().keySet());
        Collections.sort(list);
        return list;
    }

    public final List<Integer> getAllPotentialInfoSearch(String content) {
        List<Integer> list = new ArrayList<>();
        for (Entry<Integer, List<StructItemOption>> i : MapleItemInformationProvider.getInstance().getAllPotentialInfo().entrySet()) {
            for (StructItemOption ii : i.getValue()) {
                if (ii.toString().contains(content)) {
                    list.add(i.getKey());
                }
            }
        }
        Collections.sort(list);
        return list;
    }

    /**
     * 获取潜能信息
     * @param id
     * @return 
     */
    public String getPotentialInfo(int id) {
        List<StructItemOption> potInfo = MapleItemInformationProvider.getInstance().getPotentialInfo(id);
        StringBuilder builder = new StringBuilder("#b#e以下是潜能ID为 ");
        builder.append(id);
        builder.append(" 的信息#n#k\r\n\r\n");
        int minLevel = 1;
        int maxLevel = 10;
        for (StructItemOption item : potInfo) {
            builder.append("#e等级范围 ");
            builder.append(minLevel);
            builder.append("~");
            builder.append(maxLevel);
            builder.append(": #n");
            builder.append(item.toString());
            minLevel += 10;
            maxLevel += 10;
            builder.append("\r\n");
        }
        return builder.toString();
    }

    public void sendRPS() {
        this.c.getSession().write(MaplePacketCreator.getRPSMode((byte) 8, -1, -1, -1));
    }

    public void setQuestRecord(Object ch, int questid, String data) {
        ((MapleCharacter) ch).getQuestNAdd(MapleQuest.getInstance(questid)).setCustomData(data);
    }

    public final void doWeddingEffect(Object ch) {
        final MapleCharacter chr = (MapleCharacter) ch;
        final MapleCharacter player = getPlayer();
        Broadcast.broadcastMessage(MaplePacketCreator.yellowChat(new StringBuilder().append(player.getName()).append(", 你愿意娶 ").append(chr.getName()).append(" 为妻吗？无论她将来是富有还是贫穷、或无论她将来身体健康或不适，你都愿意和她永远在一起吗？").toString()));
        CloneTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if ((chr == null) || (player == null)) {
                    NPCConversationManager.this.warpMap(700000000, 0);
                } else {
                    Broadcast.broadcastMessage(MaplePacketCreator.yellowChat(chr.getName() + ", 你愿意嫁给 " + player.getName() + " 吗？无论他将来是富有还是贫穷、或无论他将来身体健康或不适，你都愿意和他永远在一起吗？"));
                }
            }
        }, 10000L);

        CloneTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if ((chr == null) || (player == null)) {
                    if (player != null) {
                        NPCConversationManager.this.setQuestRecord(player, 160001, "3");
                        NPCConversationManager.this.setQuestRecord(player, 160002, "0");
                    } else if (chr != null) {
                        NPCConversationManager.this.setQuestRecord(chr, 160001, "3");
                        NPCConversationManager.this.setQuestRecord(chr, 160002, "0");
                    }
                    NPCConversationManager.this.warpMap(700000000, 0);
                } else {
                    NPCConversationManager.this.setQuestRecord(player, 160001, "2");
                    NPCConversationManager.this.setQuestRecord(chr, 160001, "2");
                    chr.setMarriageId(player.getId());
                    player.setMarriageId(chr.getId());
                    NPCConversationManager.this.sendNPCText("好，我以圣灵、圣父、圣子的名义宣布：" + player.getName() + " 和 " + chr.getName() + "结为夫妻。 希望你们在 " + chr.getClient().getChannelServer().getServerName() + " 游戏中玩的愉快!", 9201002);
                    chr.getMap().startExtendedMapEffect("现在，新郎可以亲吻新娘了。 " + player.getName() + "!", 5120006);
                    if (chr.getGuildId() > 0) {
                        Guild.guildPacket(chr.getGuildId(), MaplePacketCreator.sendMarriage(false, chr.getName()));
                    }
                    if (chr.getFamilyId() > 0) {
                        Family.familyPacket(chr.getFamilyId(), MaplePacketCreator.sendMarriage(true, chr.getName()), chr.getId());
                    }
                    if (player.getGuildId() > 0) {
                        Guild.guildPacket(player.getGuildId(), MaplePacketCreator.sendMarriage(false, player.getName()));
                    }
                    if (player.getFamilyId() > 0) {
                        Family.familyPacket(player.getFamilyId(), MaplePacketCreator.sendMarriage(true, chr.getName()), player.getId());
                    }
                }
            }
        }, 20000L);
    }

    public void putKey(int key, int type, int action) {
        getPlayer().changeKeybinding(key, (byte) type, action);
        getClient().getSession().write(MaplePacketCreator.getKeymap(getPlayer().getKeyLayout()));
    }

    public void logDonator(String log, int previous_points) {
        StringBuilder logg = new StringBuilder();
        logg.append(MapleCharacterUtil.makeMapleReadable(getPlayer().getName()));
        logg.append(" [角色ID: ").append(getPlayer().getId()).append("] ");
        logg.append(" [账号: ").append(MapleCharacterUtil.makeMapleReadable(getClient().getAccountName())).append("] ");
        logg.append(log);
        logg.append(" [以前: ").append(previous_points).append("] [现在: ").append(getPlayer().getPoints()).append("]");
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO donorlog VALUES(DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?)");
            ps.setString(1, MapleCharacterUtil.makeMapleReadable(getClient().getAccountName()));
            ps.setInt(2, getClient().getAccID());
            ps.setString(3, MapleCharacterUtil.makeMapleReadable(getPlayer().getName()));
            ps.setInt(4, getPlayer().getId());
            ps.setString(5, log);
            ps.setString(6, FileoutputUtil.CurrentReadable_Time());
            ps.setInt(7, previous_points);
            ps.setInt(8, getPlayer().getPoints());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            _log.error("logDonator 错误", e);
        }
        FileoutputUtil.log("log\\Donator.log", logg.toString());
    }

    public void doRing(String name, int itemid) {
        PlayersHandler.DoRing(getClient(), name, itemid);
    }

    public int getNaturalStats(int itemid, String it) {
        Map eqStats = MapleItemInformationProvider.getInstance().getEquipStats(itemid);
        if ((eqStats != null) && (eqStats.containsKey(it))) {
            return ((Integer) eqStats.get(it)).intValue();
        }
        return 0;
    }

    public boolean isEligibleName(String t) {
        return (MapleCharacterUtil.canCreateChar(t, getPlayer().isGM())) && ((!LoginInformationProvider.getInstance().isForbiddenName(t)) || (getPlayer().isGM()));
    }

    /**
     * 查询怪物掉落
     *
     * @param mobId
     * @return
     */
    public String checkDrop(int mobId) {
        List ranks = MapleMonsterInformationProvider.getInstance().retrieveDrop(mobId);
        if ((ranks != null) && (ranks.size() > 0)) {
            int num = 0;
            int itemId = 0;
            int ch = 0;

            StringBuilder name = new StringBuilder();
            for (int i = 0; i < ranks.size(); i++) {
                MonsterDropEntry de = (MonsterDropEntry) ranks.get(i);
                if ((de.chance > 0) && ((de.questid <= 0) || ((de.questid > 0) && (MapleQuest.getInstance(de.questid).getName().length() > 0)))) {
                    itemId = de.itemId;
                    if (num == 0) {
                        name.append("当前怪物 #o").append(mobId).append("# 的爆率为:\r\n");
                        name.append("--------------------------------------\r\n");
                    }
                    String namez = new StringBuilder().append("#z").append(itemId).append("#").toString();
                    if (itemId == 0) {
                        itemId = 4031041;
                        namez = new StringBuilder().append(de.Minimum * getClient().getChannelServer().getMesoRate()).append(" - ").append(de.Maximum * getClient().getChannelServer().getMesoRate()).append(" 的金币").toString();
                    }
                    ch = de.chance * getClient().getChannelServer().getDropRate();
                    if (getPlayer().isAdmin()) {
                        name.append(num + 1).append(") #v").append(itemId).append("#").append(namez).append(" - ").append(Integer.valueOf(ch >= 999999 ? 1000000 : ch).doubleValue() / 10000.0D).append("%的爆率. ").append((de.questid > 0) && (MapleQuest.getInstance(de.questid).getName().length() > 0) ? new StringBuilder().append("需要接受任务: ").append(MapleQuest.getInstance(de.questid).getName()).toString() : "").append("\r\n");
                    } else {
                        name.append(num + 1).append(") #v").append(itemId).append("#").append(namez).append((de.questid > 0) && (MapleQuest.getInstance(de.questid).getName().length() > 0) ? new StringBuilder().append("需要接受任务: ").append(MapleQuest.getInstance(de.questid).getName()).toString() : "").append("\r\n");
                    }
                    num++;
                }
            }
            if (name.length() > 0) {
                return name.toString();
            }
        }
        return "没有找到这个怪物的爆率数据。";
    }

    public List<BattleConstants.PokedexEntry> getAllPokedex() {
        return BattleConstants.getAllPokedex();
    }

    public String getLeftPadded(String in, char padchar, int length) {
        return StringUtil.getLeftPaddedStr(in, padchar, length);
    }

    public void preparePokemonBattle(List<Integer> npcTeam, int restrictedLevel) {
        int theId = MapleLifeFactory.getRandomNPC();
        PokemonBattle wild = new PokemonBattle(getPlayer(), npcTeam, theId, restrictedLevel);
        getPlayer().changeMap(wild.getMap(), wild.getMap().getPortal(0));
        getPlayer().setBattle(wild);
        wild.initiate(getPlayer(), MapleLifeFactory.getNPC(theId));
    }

    public List<Integer> makeTeam(int lowRange, int highRange, int neededLevel, int restrictedLevel) {
        List ret = new ArrayList();
        int averageLevel = 0;
        int numBattlers = 0;
        for (Battler b : getPlayer().getBattlers()) {
            if (b != null) {
                if (b.getLevel() > averageLevel) {
                    averageLevel = b.getLevel();
                }
                numBattlers++;
            }
        }
        boolean hell = lowRange == highRange;
        if ((numBattlers < 3) || (averageLevel < neededLevel)) {
            return null;
        }
        if (averageLevel > restrictedLevel) {
            averageLevel = restrictedLevel;
        }
        List<PokedexEntry> pokeEntries = new ArrayList(getAllPokedex());
        Collections.shuffle(pokeEntries);
        while (ret.size() < numBattlers) {
            for (PokedexEntry d : pokeEntries) {
                if (((d.dummyBattler.getStats().isBoss()) && (hell)) || ((!d.dummyBattler.getStats().isBoss()) && (!hell))) {
                    if (!hell) {
                        if ((d.dummyBattler.getLevel() <= averageLevel + highRange) && (d.dummyBattler.getLevel() >= averageLevel + lowRange) && (Randomizer.nextInt(numBattlers) == 0)) {
                            ret.add(Integer.valueOf(d.id));
                            if (ret.size() >= numBattlers) {
                                break;
                            }
                        }
                    } else if ((d.dummyBattler.getFamily().type != BattleConstants.MobExp.EASY) && (d.dummyBattler.getLevel() >= neededLevel) && (d.dummyBattler.getLevel() <= averageLevel) && (Randomizer.nextInt(numBattlers) == 0)) {
                        ret.add(Integer.valueOf(d.id));
                        if (ret.size() >= numBattlers) {
                            break;
                        }
                    }
                }
            }
        }
        return ret;
    }

    public BattleConstants.HoldItem[] getAllHoldItems() {
        return BattleConstants.HoldItem.values();
    }

    public void handleDivorce() {
        if (getPlayer().getMarriageId() <= 0) {
            sendNext("你还没结婚，怎么能离婚呢？");
            return;
        }
        int chz = Find.findChannel(getPlayer().getMarriageId());
        if (chz == -1) {
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement("UPDATE queststatus SET customData = ? WHERE characterid = ? AND (quest = ? OR quest = ?)");
                ps.setString(1, "0");
                ps.setInt(2, getPlayer().getMarriageId());
                ps.setInt(3, 160001);
                ps.setInt(4, 160002);
                ps.executeUpdate();
                ps.close();
                ps = con.prepareStatement("UPDATE characters SET marriageid = ? WHERE id = ?");
                ps.setInt(1, 0);
                ps.setInt(2, getPlayer().getMarriageId());
                ps.executeUpdate();
                ps.close();
            } catch (SQLException e) {
                outputFileError(e);
                return;
            }
            setQuestRecord(getPlayer(), 160001, "0");
            setQuestRecord(getPlayer(), 160002, "0");
            getPlayer().setMarriageId(0);
            sendNext("离婚成功...");
            return;
        }
        if (chz < -1) {
            sendNext("请确保你的伴侣是在线的.");
            return;
        }
        MapleCharacter cPlayer = ChannelServer.getInstance(chz).getPlayerStorage().getCharacterById(getPlayer().getMarriageId());
        if (cPlayer != null) {
            cPlayer.dropMessage(1, "你的伴侣和你离婚了.");
            cPlayer.setMarriageId(0);
            setQuestRecord(cPlayer, 160001, "0");
            setQuestRecord(getPlayer(), 160001, "0");
            setQuestRecord(cPlayer, 160002, "0");
            setQuestRecord(getPlayer(), 160002, "0");
            getPlayer().setMarriageId(0);
            sendNext("离婚成功...");
        } else {
            sendNext("出现了未知的错误...");
        }
    }

    public String getReadableMillis(long startMillis, long endMillis) {
        return StringUtil.getReadableMillis(startMillis, endMillis);
    }

    public void sendUltimateExplorer() {
        getClient().getSession().write(MaplePacketCreator.ultimateExplorer());
    }

    public String getRankingInformation(int job) {
        StringBuilder sb = new StringBuilder();
        for (RankingWorker.RankingInformation pi : RankingWorker.getRankingInfo(job)) {
            sb.append(pi.toString());
        }
        return sb.toString();
    }

    public String getPokemonRanking() {
        StringBuilder sb = new StringBuilder();
        for (RankingWorker.PokemonInformation pi : RankingWorker.getPokemonInfo()) {
            sb.append(pi.toString());
        }
        return sb.toString();
    }

    public String getPokemonRanking_Caught() {
        StringBuilder sb = new StringBuilder();
        for (RankingWorker.PokedexInformation pi : RankingWorker.getPokemonCaught()) {
            sb.append(pi.toString());
        }
        return sb.toString();
    }

    public String getPokemonRanking_Ratio() {
        StringBuilder sb = new StringBuilder();
        for (RankingWorker.PokebattleInformation pi : RankingWorker.getPokemonRatio()) {
            sb.append(pi.toString());
        }
        return sb.toString();
    }

    public void sendPendant(boolean b) {
        this.c.getSession().write(MaplePacketCreator.pendantSlot(b));
    }

    public Triple<Integer, Integer, Integer> getCompensation() {
        Triple ret = null;
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM compensationlog_confirmed WHERE chrname LIKE ?");
            ps.setString(1, getPlayer().getName());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ret = new Triple(Integer.valueOf(rs.getInt("value")), Integer.valueOf(rs.getInt("taken")), Integer.valueOf(rs.getInt("donor")));
            }
            rs.close();
            ps.close();
            return ret;
        } catch (SQLException e) {
            FileoutputUtil.outputFileError("log\\Script_Except.log", e);
        }
        return ret;
    }

    public boolean deleteCompensation(int taken) {
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE compensationlog_confirmed SET taken = ? WHERE chrname LIKE ?");
            ps.setInt(1, taken);
            ps.setString(2, getPlayer().getName());
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException e) {
            FileoutputUtil.outputFileError("log\\Script_Except.log", e);
        }
        return false;
    }

    public void testPacket(String testmsg) {
        this.c.getSession().write(MaplePacketCreator.testPacket(testmsg));
    }

    public void testPacket(int op) {
        this.c.getSession().write(FamilyPacket.sendFamilyMessage(op));
    }

    public void testPacket(String op, String msg) {
        this.c.getSession().write(MaplePacketCreator.testPacket(op, msg));
    }

    public short getSpace(byte type) {
        return getPlayer().getSpace(type);
    }

    public boolean haveSpace(int type) {
        return getPlayer().haveSpace(type);
    }

    public boolean haveSpaceForId(int itemid) {
        return getPlayer().haveSpaceForId(itemid);
    }

    public int gainGachaponItem(int id, int quantity, String msg, int rareness) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        try {
            if (!ii.itemExists(id)) {
                return -1;
            }
            Item item = MapleInventoryManipulator.addbyId_Gachapon(this.c, id, (short) quantity);
            if (item == null) {
                return -1;
            }
            if ((rareness == 1) || (rareness == 2)) {
                Broadcast.broadcastMessage(MaplePacketCreator.getGachaponMega(this.c.getPlayer().getName(), new StringBuilder().append(" : 从").append(msg).append("中获得{").append(ii.getName(item.getItemId())).append("}！大家一起恭喜他（她）吧！！！！").toString(), item, (byte) rareness, this.c.getChannel()));
            }
            return item.getItemId();
        } catch (Exception e) {
            _log.error("gainGachaponItem 错误", e);
        }
        return -1;
    }

    public int getMoney() {
        int money = 0;
        try {
            int cid = getPlayer().getId();
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("select * from bank where charid=?");
            ps.setInt(1, cid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                money = rs.getInt("money");
            } else {
                PreparedStatement psu = con.prepareStatement("insert into bank (charid, money) VALUES (?, ?)");
                psu.setInt(1, cid);
                psu.setInt(2, 0);
                psu.executeUpdate();
                psu.close();
            }
            ps.close();
            rs.close();
        } catch (SQLException ex) {
            _log.error("银行存款获取信息发生错误", ex);
        }
        return money;
    }

    public int addMoney(int money, int type) {
        try {
            int cid = getPlayer().getId();
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("select * from bank where charid=?");
            ps.setInt(1, cid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if ((type == 1)
                        && (money > rs.getInt("money"))) {
                    return -1;
                }

                ps = con.prepareStatement(new StringBuilder().append("UPDATE bank SET money =money+ ").append(money).append(" WHERE charid = ").append(cid).append("").toString());
                return ps.executeUpdate();
            }
            ps.close();
            rs.close();
        } catch (SQLException ex) {
            _log.error("银行存款添加数量发生错误", ex);
        }
        return 0;
    }
    
        public int getMaplewing() {
        int Maplewing = 0;
        try {
            int cid = getPlayer().getId();
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("select * from bank where charid=?");
            ps.setInt(1, cid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Maplewing = rs.getInt("Maplewing");
            } else {
                PreparedStatement psu = con.prepareStatement("insert into bank (charid, Maplewing) VALUES (?, ?)");
                psu.setInt(1, cid);
                psu.setInt(2, 0);
                psu.executeUpdate();
                psu.close();
            }
            ps.close();
            rs.close();
        } catch (SQLException ex) {
            _log.error("MapleWing系统获取信息发生错误", ex);
        }
        return Maplewing;
    }

    public int addMaplewing1(int Maplewing, int type) {
        try {
            int cid = getPlayer().getId();
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("select * from bank where charid=?");
            ps.setInt(1, cid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if ((type == 1)
                        && (Maplewing > rs.getInt("Maplewing"))) {
                    return -1;
                }

                ps = con.prepareStatement(new StringBuilder().append("UPDATE bank SET Maplewing =Maplewing+ ").append(Maplewing).append(" WHERE charid = ").append(cid).append("").toString());
                return ps.executeUpdate();
            }
            ps.close();
            rs.close();
        } catch (SQLException ex) {
            _log.error("MapleWing系统添加数量发生错误", ex);
        }
        return 0;
    }
    
    public int getMaplewingrw1() {
        int Maplewingrw = 0;
        try {
            int cid = getPlayer().getId();
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("select * from bank where charid=?");
            ps.setInt(1, cid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Maplewingrw = rs.getInt("Maplewingrw");
            } else {
                PreparedStatement psu = con.prepareStatement("insert into bank (charid, Maplewingrw) VALUES (?, ?)");
                psu.setInt(1, cid);
                psu.setInt(2, 0);
                psu.executeUpdate();
                psu.close();
            }
            ps.close();
            rs.close();
        } catch (SQLException ex) {
            _log.error("MapleWing系统获取信息发生错误", ex);
        }
        return Maplewingrw;
    }

    public int addMaplewingrw1(int Maplewingrw, int type) {
        try {
            int cid = getPlayer().getId();
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("select * from bank where charid=?");
            ps.setInt(1, cid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if ((type == 1)
                        && (Maplewingrw > rs.getInt("Maplewingrw"))) {
                    return -1;
                }

                ps = con.prepareStatement(new StringBuilder().append("UPDATE bank SET Maplewingrw =Maplewingrw+ ").append(Maplewingrw).append(" WHERE charid = ").append(cid).append("").toString());
                return ps.executeUpdate();
            }
            ps.close();
            rs.close();
        } catch (SQLException ex) {
            _log.error("MapleWing系统添加数量发生错误", ex);
        }
        return 0;
    }
    
    public int getMaplewingcx() {
        int Maplewingcx = 0;
        try {
            int cid = getPlayer().getId();
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("select * from bank where charid=?");
            ps.setInt(1, cid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Maplewingcx = rs.getInt("Maplewingcx");
            } else {
                PreparedStatement psu = con.prepareStatement("insert into bank (charid, Maplewingcx) VALUES (?, ?)");
                psu.setInt(1, cid);
                psu.setInt(2, 0);
                psu.executeUpdate();
                psu.close();
            }
            ps.close();
            rs.close();
        } catch (SQLException ex) {
            _log.error("MapleWing系统获取信息发生错误", ex);
        }
        return Maplewingcx;
    }

    public int addMaplewingcx(int Maplewingcx, int type) {
        try {
            int cid = getPlayer().getId();
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("select * from bank where charid=?");
            ps.setInt(1, cid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if ((type == 1)
                        && (Maplewingcx > rs.getInt("Maplewingcx"))) {
                    return -1;
                }

                ps = con.prepareStatement(new StringBuilder().append("UPDATE bank SET Maplewingcx =Maplewingcx+ ").append(Maplewingcx).append(" WHERE charid = ").append(cid).append("").toString());
                return ps.executeUpdate();
            }
            ps.close();
            rs.close();
        } catch (SQLException ex) {
            _log.error("MapleWing系统添加数量发生错误", ex);
        }
        return 0;
    }
    
    public int addBank() { 
            int r = 0; 
        try { 
            Connection con = DatabaseConnection.getConnection(); 
            PreparedStatement ps = con.prepareStatement("insert into bank (charid,money,Maplewing,Maplewingrw,Maplewingcx) values (?,0,0,0,0)"); 
            ps.setInt(1, getPlayer().getId());            
            r=ps.executeUpdate(); 
            ps.close(); 
            
        } catch (SQLException ex) { 
          r=0; 
        } 
            
            return r; 
        } 
    
    
    public int getMaplewingid() {
        int id = 0;
        try {
            int cid = getPlayer().getId();
            Connection con = DatabaseConnection.getConnection();
            ResultSet rs;
            try (PreparedStatement ps = con.prepareStatement("select * from bank where charid=?")) {
                ps.setInt(1, cid);
                rs = ps.executeQuery();
                if (rs.next()) {
                    id = rs.getInt("id");
                } else {
                    try (PreparedStatement psu = con.prepareStatement("insert into bank (charid, id) VALUES (?, ?)")) {
                        psu.setInt(1, cid);
                        psu.setInt(2, 0);
                        psu.executeUpdate();
                    }
                }
            }
            rs.close();
        } catch (SQLException ex) {
            _log.error("MapleWing系统获取信息发生错误", ex);
        }
        return id;
    }
    
    public int getHyPay(int type) {
        return getPlayer().getHyPay(type);
    }

    public int addHyPay(int hypay) {
        return getPlayer().addHyPay(hypay);
    }

    public int delPayReward(int pay) {
        return getPlayer().delPayReward(pay);
    }

    public void fakeRelog() {
        if ((!this.c.getPlayer().isAlive()) || (this.c.getPlayer().getEventInstance() != null) || (FieldLimitType.ChannelSwitch.check(this.c.getPlayer().getMap().getFieldLimit()))) {
            this.c.getPlayer().dropMessage(1, "刷新人物数据失败.");
            return;
        }
        this.c.getPlayer().dropMessage(5, "正在刷新人数据.请等待...");
        this.c.getPlayer().fakeRelog();
    }

    public MapleCharacter getCharByName(String name) {
        try {
            return this.c.getChannelServer().getPlayerStorage().getCharacterByName(name);
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 装备栏列表
     *
     * @param c
     * @return
     */
    public String EquipList(MapleClient c) {
        StringBuilder str = new StringBuilder();
        MapleInventory equip = c.getPlayer().getInventory(MapleInventoryType.EQUIP);
        List<String> stra = new LinkedList();
        for (Item item : equip.list()) {
            stra.add(new StringBuilder().append("#L").append(item.getPosition()).append("##v").append(item.getItemId()).append("##l").toString());
        }
        for (String strb : stra) {
            str.append(strb);
        }
        return str.toString();
    }

    /**
     * 消耗栏列表
     *
     * @param c
     * @return
     */
    public String UseList(MapleClient c) {
        StringBuilder str = new StringBuilder();
        MapleInventory use = c.getPlayer().getInventory(MapleInventoryType.USE);
        List<String> stra = new LinkedList();
        for (Item item : use.list()) {
            stra.add(new StringBuilder().append("#L").append(item.getPosition()).append("##v").append(item.getItemId()).append("##l").toString());
        }
        for (String strb : stra) {
            str.append(strb);
        }
        return str.toString();
    }

    /**
     * 现金栏列表
     *
     * @param c
     * @return
     */
    public String CashList(MapleClient c) {
        StringBuilder str = new StringBuilder();
        MapleInventory cash = c.getPlayer().getInventory(MapleInventoryType.CASH);
        List<String> stra = new LinkedList();
        for (Item item : cash.list()) {
            stra.add(new StringBuilder().append("#L").append(item.getPosition()).append("##v").append(item.getItemId()).append("##l").toString());
        }
        for (String strb : stra) {
            str.append(strb);
        }
        return str.toString();
    }

    /**
     * 其他栏列表
     *
     * @param c
     * @return
     */
    public String EtcList(MapleClient c) {
        StringBuilder str = new StringBuilder();
        MapleInventory etc = c.getPlayer().getInventory(MapleInventoryType.ETC);
        List<String> stra = new LinkedList();
        for (Item item : etc.list()) {
            stra.add(new StringBuilder().append("#L").append(item.getPosition()).append("##v").append(item.getItemId()).append("##l").toString());
        }
        for (String strb : stra) {
            str.append(strb);
        }
        return str.toString();
    }

    /**
     * 设置栏列表
     *
     * @param c
     * @return
     */
    public String SetupList(MapleClient c) {
        StringBuilder str = new StringBuilder();
        MapleInventory setup = c.getPlayer().getInventory(MapleInventoryType.SETUP);
        List<String> stra = new LinkedList<String>();
        for (Item item : setup.list()) {
            stra.add(new StringBuilder().append("#L").append(item.getPosition()).append("##v").append(item.getItemId()).append("##l").toString());
        }
        for (String strb : stra) {
            str.append(strb);
        }
        return str.toString();
    }

    /**
     * 根据指定的物品
     *
     * @param itemId 物品ID
     */
    public void deleteAll(int itemId) {
        MapleInventoryManipulator.removeAllById(getClient(), itemId, true);
    }

    public int getCurrentSharesPrice() {
        return this.c.getChannelServer().getSharePrice();
    }

    public int getDollars() {
        return getPlayer().getDollars();
    }

    public int getShareLots() {
        return getPlayer().getShareLots();
    }

    public void addDollars(int n) {
        getPlayer().addDollars(n);
    }

    public void addShareLots(int n) {
        getPlayer().addShareLots(n);
    }
    
            public int getHour() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        return hour;
    }
    
    public int getMin() {
        Calendar cal = Calendar.getInstance();
        int min = cal.get(Calendar.MINUTE);
        return min;
    }
    
    public int getSec() {
        Calendar cal = Calendar.getInstance();
        int sec = cal.get(Calendar.SECOND);
        return sec;
    }
    
    public void Maplewingx(String Text, int typedd) {
		for (Iterator n$ = ChannelServer.getAllInstances().iterator(); n$.hasNext();)
		{
			ChannelServer cservs = (ChannelServer)n$.next();
			Iterator i$ = cservs.getPlayerStorage().getAllCharacters().iterator();
			while (i$.hasNext())
			{
				MapleCharacter players = (MapleCharacter)i$.next();
				if (players.getGMLevel() < 1000000)
					players.getClient().getSession().write(MaplePacketCreator.startMapEffect((new StringBuilder()).append("  ").append(Text).toString(), typedd, true));
			}
		}

	}


    public void superlaba(String Text, int typedd) {
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
				if (players.getGMLevel() < 10000000)
					players.getClient().getSession().write(MaplePacketCreator.startMapEffect((new StringBuilder()).append("玩家 ").append(c.getPlayer().getName()).append(" 的消息:").append(Text).toString(), typedd, true));
			}
		}

	}
        
        public void deleteItem(int inventorytype) { 
        try { 
            Connection con = DatabaseConnection.getConnection(); 
            PreparedStatement ps = con.prepareStatement("Select * from inventoryitems where characterid=? and inventorytype=?"); 
            ps.setInt(1, getPlayer().getId()); 
            ps.setInt(2, inventorytype); 
            ResultSet re = ps.executeQuery(); 
            MapleInventoryType type = null; 
            switch (inventorytype) { 
                case 1: 
                    type=MapleInventoryType.EQUIP; 
                    break; 
                case 2: 
                    type=MapleInventoryType.USE; 
                    break; 
                case 3: 
                    type=MapleInventoryType.SETUP; 
                    break; 
                case 4: 
                    type=MapleInventoryType.ETC; 
                    break; 
                case 5: 
                    type=MapleInventoryType.CASH; 
                    break; 
            } 
            while (re.next()) { 
            MapleInventoryManipulator.removeById(getC(),type, re.getInt("itemid"),1,true, true); 
            } 
            re.close(); 
            ps.close(); 
            
        } catch (SQLException ex) { 
            // Logger.getLogger(NPCConversationManager.class.getName()).log(Level.SEVERE, null, ex); 
        } 
    }

    public int useNebuliteGachapon() {
        try {
            if ((this.c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 1) || (this.c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 1) || (this.c.getPlayer().getInventory(MapleInventoryType.SETUP).getNumFreeSlot() < 1) || (this.c.getPlayer().getInventory(MapleInventoryType.ETC).getNumFreeSlot() < 1) || (this.c.getPlayer().getInventory(MapleInventoryType.CASH).getNumFreeSlot() < 1)) {
                return -1;
            }
            int grade = 0;
            int chance = Randomizer.nextInt(100);
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            if (chance < 1) {
                grade = 3;
            } else if (chance < 5) {
                grade = 2;
            } else if (chance < 35) {
                grade = 1;
            } else {
                grade = Randomizer.nextInt(100) < 25 ? 5 : 0;
            }
            int newId = 0;
            if (grade == 5) {
                newId = 4420000;
            } else {
                List pots = new LinkedList(ii.getAllSocketInfo(grade).values());
                while (newId == 0) {
                    StructItemOption pot = (StructItemOption) pots.get(Randomizer.nextInt(pots.size()));
                    if (pot != null) {
                        newId = pot.opID;
                    }
                }
            }
            Item item = MapleInventoryManipulator.addbyId_Gachapon(this.c, newId, (short) 1);
            if (item == null) {
                return -1;
            }
            if ((grade >= 2) && (grade != 5)) {
                Broadcast.broadcastMessage(MaplePacketCreator.getGachaponMega(this.c.getPlayer().getName(), new StringBuilder().append(" : 从星岩中获得{").append(ii.getName(item.getItemId())).append("}！大家一起恭喜他（她）吧！！！！").toString(), item, (byte) 2, this.c.getChannel()));
            }
            this.c.getSession().write(MaplePacketCreator.getShowItemGain(newId, (short) 1, true));
            return item.getItemId();
        } catch (Exception e) {
            System.out.println(new StringBuilder().append("[Error] Failed to use Nebulite Gachapon. ").append(e).toString());
        }
        return -1;
    }
}