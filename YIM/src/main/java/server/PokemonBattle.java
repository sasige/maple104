package server;

import client.Battler;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleQuestStatus;
import client.MonsterBook;
import client.PlayerStats;
import client.status.MonsterStatus;
import client.status.MonsterStatusEffect;
import constants.BattleConstants;
import constants.BattleConstants.HoldItem;
import constants.BattleConstants.PokemonAbility;
import constants.BattleConstants.PokemonElement;
import constants.BattleConstants.PokemonItem;
import constants.BattleConstants.PokemonMap;
import constants.BattleConstants.PokemonStat;
import constants.BattleConstants.Turn;
import handling.channel.ChannelServer;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import scripting.EventScriptManager;
import server.Timer.EtcTimer;
import server.events.MapleEvent;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MapleMonsterStats;
import server.life.MapleNPC;
import server.life.MobAttackInfo;
import server.maps.MapleMap;
import server.maps.MapleMapFactory;
import server.movement.AbsoluteLifeMovement;
import server.movement.LifeMovementFragment;
import server.movement.RelativeLifeMovement;
import server.quest.MapleQuest;
import tools.FileoutputUtil;
import tools.MaplePacketCreator;
import tools.StringUtil;
import tools.packet.MobPacket;

public final class PokemonBattle {

    private static int WALK_TIME = 2000;
    private static String wildBGM = "BgmTW/NightField";
    private static String npcBGM = "BgmTW/YoTaipei";
    private static String trainerBGM = "BgmCN/PvP";
    private MapleMap map;
    private int[] characterIds = new int[2];
    private Battler[] battlers = new Battler[2];
    private int instanceid = -1;
    private int mapid;
    private Turn[] turn = new Turn[2];
    private String[] attacks = new String[2];
    private boolean disposed = false;
    private Battler[] switches = new Battler[2];
    private List<Integer> npcTeam;
    private static final String[] messages = {"The ball broke free?!", "It appeared to be caught!", "No! It was so close, too!", "Argh! Almost had it!"};

    public PokemonBattle(MapleCharacter init, int monsterId, PokemonMap mapp) {
        for (Battler b : init.getBattlers()) {
            if (b != null) {
                b.resetStats();
            }
        }
        this.characterIds[0] = init.getId();
        this.characterIds[1] = 0;
        this.instanceid = EventScriptManager.getNewInstanceMapId();
        this.map = init.getClient().getChannelServer().getMapFactory().CreateInstanceMap(init.getMapId(), false, false, false, this.instanceid);
        this.mapid = init.getMapId();
        this.battlers[0] = init.getBattler(0);
        MapleMonsterStats mons = MapleLifeFactory.getMonsterStats(monsterId);
        if (mons == null) {
            dispose(init, true);
            throw new RuntimeException("MONSTER NOT EXIST " + monsterId);
        }
        this.battlers[1] = new Battler(mons);
    }

    public PokemonBattle(MapleCharacter init, List<Integer> monsterId, int npcId, int maxLevel) {
        for (Battler b : init.getBattlers()) {
            if (b != null) {
                b.resetStats();
                if ((maxLevel > 0) && (b.getLevel() > maxLevel)) {
                    b.setTempLevel(maxLevel);
                    b.resetHP();
                }
            }
        }
        this.characterIds[0] = init.getId();
        this.characterIds[1] = 0;
        this.instanceid = EventScriptManager.getNewInstanceMapId();
        this.map = init.getClient().getChannelServer().getMapFactory().CreateInstanceMap(init.getMapId(), false, false, false, this.instanceid);
        this.mapid = init.getMapId();
        this.battlers[0] = init.getBattler(0);
        this.npcTeam = monsterId;
        MapleMonsterStats mons = MapleLifeFactory.getMonsterStats(((Integer) monsterId.get(0)).intValue());
        if (mons == null) {
            dispose(init, true);
            throw new RuntimeException("MONSTER NOT EXIST " + monsterId.get(0));
        }
        this.battlers[1] = new Battler(mons);
        this.npcTeam.remove(0);
        this.map.spawnNpc(npcId, BattleConstants.getNPCPos(init.getMapId()));
    }

    public PokemonBattle(MapleCharacter init, MapleCharacter init2) {
        this.map = init.getMap();
        this.mapid = init.getMapId();
        if (init2.getMap() != this.map) {
            throw new RuntimeException("INIT MAPS WERE NOT EQUAL: " + init.getMapId() + " vs " + init2.getMapId());
        }
        for (Battler b : init.getBattlers()) {
            if (b != null) {
                b.resetStats();
            }
        }
        for (Battler b : init2.getBattlers()) {
            if (b != null) {
                b.resetStats();
            }
        }
        this.characterIds[0] = init.getId();
        this.characterIds[1] = init2.getId();
        this.battlers[0] = init.getBattler(0);
        this.battlers[1] = init2.getBattler(0);
    }

    public int getInstanceId() {
        return this.instanceid;
    }

    public void giveReward(MapleCharacter chr) {
        if (chr.getMapId() == 925020011) {
            int rr = Randomizer.nextInt(100);
            if (rr < 10) {
                int reward = RandomRewards.getPokemonReward();
                if (MapleInventoryManipulator.checkSpace(chr.getClient(), reward, 1, "")) {
                    chr.dropMessage(-6, "You have gained a pokemon item.");
                    MapleInventoryManipulator.addById(chr.getClient(), reward, (short) 1, "Pokemon prize on NORMAL on " + FileoutputUtil.CurrentReadable_Date());
                } else {
                    giveReward(chr);
                }
            } else {
                if (rr < 11) {
                    MapleEvent.givePrize(chr);
                    return;
                }
                if (rr < 45) {
                    int reward = RandomRewards.getDropReward();
                    if (MapleInventoryManipulator.checkSpace(chr.getClient(), reward, 1, "")) {
                        MapleInventoryManipulator.addById(chr.getClient(), reward, (short) 1, "Pokemon prize on NORMAL on " + FileoutputUtil.CurrentReadable_Date());
                    } else {
                        giveReward(chr);
                    }
                } else if (rr < 70) {
                    int reward = RandomRewards.getFishingReward();
                    if (MapleInventoryManipulator.checkSpace(chr.getClient(), reward, 1, "")) {
                        MapleInventoryManipulator.addById(chr.getClient(), reward, (short) 1, "Pokemon prize on NORMAL on " + FileoutputUtil.CurrentReadable_Date());
                    } else {
                        giveReward(chr);
                    }
                } else if (rr < 80) {
                    int reward = RandomRewards.getSilverBoxReward();
                    if (MapleInventoryManipulator.checkSpace(chr.getClient(), reward, 1, "")) {
                        MapleInventoryManipulator.addById(chr.getClient(), reward, (short) 1, "Pokemon prize on NORMAL on " + FileoutputUtil.CurrentReadable_Date());
                    } else {
                        giveReward(chr);
                    }
                } else if (rr < 85) {
                    int reward = RandomRewards.getGoldBoxReward();
                    if (MapleInventoryManipulator.checkSpace(chr.getClient(), reward, 1, "")) {
                        MapleInventoryManipulator.addById(chr.getClient(), reward, (short) 1, "Pokemon prize on NORMAL on " + FileoutputUtil.CurrentReadable_Date());
                    } else {
                        giveReward(chr);
                    }
                } else if (rr < 90) {
                    int reward = RandomRewards.getPeanutReward();
                    if (MapleInventoryManipulator.checkSpace(chr.getClient(), reward, 1, "")) {
                        MapleInventoryManipulator.addById(chr.getClient(), reward, (short) 1, "Pokemon prize on NORMAL on " + FileoutputUtil.CurrentReadable_Date());
                    } else {
                        giveReward(chr);
                    }
                } else {
                    chr.dropMessage(-6, "The trainer ran away, ashamed at their defeat, unable to give a reward.");
                }
            }
        } else if (chr.getMapId() == 925020012) {
            int rr = Randomizer.nextInt(100);
            if (rr < 20) {
                int reward = RandomRewards.getPokemonReward();
                if (MapleInventoryManipulator.checkSpace(chr.getClient(), reward, 1, "")) {
                    chr.dropMessage(-6, "You have gained a pokemon item.");
                    MapleInventoryManipulator.addById(chr.getClient(), reward, (short) 1, "Pokemon prize on HARD on " + FileoutputUtil.CurrentReadable_Date());
                } else {
                    giveReward(chr);
                }
            } else {
                if (rr < 22) {
                    MapleEvent.givePrize(chr);
                    return;
                }
                if (rr < 40) {
                    int reward = RandomRewards.getDropReward();
                    if (MapleInventoryManipulator.checkSpace(chr.getClient(), reward, 1, "")) {
                        MapleInventoryManipulator.addById(chr.getClient(), reward, (short) 1, "Pokemon prize on HARD on " + FileoutputUtil.CurrentReadable_Date());
                    } else {
                        giveReward(chr);
                    }
                } else if (rr < 60) {
                    int reward = RandomRewards.getFishingReward();
                    if (MapleInventoryManipulator.checkSpace(chr.getClient(), reward, 1, "")) {
                        MapleInventoryManipulator.addById(chr.getClient(), reward, (short) 1, "Pokemon prize on HARD on " + FileoutputUtil.CurrentReadable_Date());
                    } else {
                        giveReward(chr);
                    }
                } else if (rr < 75) {
                    int reward = RandomRewards.getSilverBoxReward();
                    if (MapleInventoryManipulator.checkSpace(chr.getClient(), reward, 1, "")) {
                        MapleInventoryManipulator.addById(chr.getClient(), reward, (short) 1, "Pokemon prize on HARD on " + FileoutputUtil.CurrentReadable_Date());
                    } else {
                        giveReward(chr);
                    }
                } else if (rr < 85) {
                    int reward = RandomRewards.getGoldBoxReward();
                    if (MapleInventoryManipulator.checkSpace(chr.getClient(), reward, 1, "")) {
                        MapleInventoryManipulator.addById(chr.getClient(), reward, (short) 1, "Pokemon prize on HARD on " + FileoutputUtil.CurrentReadable_Date());
                    } else {
                        giveReward(chr);
                    }
                } else if (rr < 95) {
                    int reward = RandomRewards.getPeanutReward();
                    if (MapleInventoryManipulator.checkSpace(chr.getClient(), reward, 1, "")) {
                        MapleInventoryManipulator.addById(chr.getClient(), reward, (short) 1, "Pokemon prize on HARD on " + FileoutputUtil.CurrentReadable_Date());
                    } else {
                        giveReward(chr);
                    }
                } else {
                    chr.dropMessage(-6, "The trainer ran away, ashamed at their defeat, unable to give a reward.");
                }
            }
        } else if (chr.getMapId() == 925020013) {
            int rr = Randomizer.nextInt(100);
            if (rr < 40) {
                int reward = RandomRewards.getPokemonReward();
                if (MapleInventoryManipulator.checkSpace(chr.getClient(), reward, 1, "")) {
                    chr.dropMessage(-6, "You have gained an evolution item.");
                    MapleInventoryManipulator.addById(chr.getClient(), reward, (short) 1, "Pokemon prize on HELL on " + FileoutputUtil.CurrentReadable_Date());
                } else {
                    giveReward(chr);
                }
            } else {
                if (rr < 45) {
                    MapleEvent.givePrize(chr);
                    return;
                }
                if (rr < 50) {
                    int reward = RandomRewards.getDropReward();
                    if (MapleInventoryManipulator.checkSpace(chr.getClient(), reward, 1, "")) {
                        MapleInventoryManipulator.addById(chr.getClient(), reward, (short) 1, "Pokemon prize on HELL on " + FileoutputUtil.CurrentReadable_Date());
                    } else {
                        giveReward(chr);
                    }
                } else if (rr < 70) {
                    int reward = RandomRewards.getSilverBoxReward();
                    if (MapleInventoryManipulator.checkSpace(chr.getClient(), reward, 1, "")) {
                        MapleInventoryManipulator.addById(chr.getClient(), reward, (short) 1, "Pokemon prize on HELL on " + FileoutputUtil.CurrentReadable_Date());
                    } else {
                        giveReward(chr);
                    }
                } else if (rr < 85) {
                    int reward = RandomRewards.getGoldBoxReward();
                    if (MapleInventoryManipulator.checkSpace(chr.getClient(), reward, 1, "")) {
                        MapleInventoryManipulator.addById(chr.getClient(), reward, (short) 1, "Pokemon prize on HELL on " + FileoutputUtil.CurrentReadable_Date());
                    } else {
                        giveReward(chr);
                    }
                } else {
                    int reward = RandomRewards.getPeanutReward();
                    if (MapleInventoryManipulator.checkSpace(chr.getClient(), reward, 1, "")) {
                        MapleInventoryManipulator.addById(chr.getClient(), reward, (short) 1, "Pokemon prize on HELL on " + FileoutputUtil.CurrentReadable_Date());
                    } else {
                        giveReward(chr);
                    }
                }
            }
        }
    }

    public void dispose() {
        if (this.map != null) {
            for (int i = 0; i < this.battlers.length; i++) {
                if ((this.battlers[i] != null) && (this.battlers[i].getMonster() != null)) {
                    this.map.killMonster(this.battlers[i].getMonster());
                    this.battlers[i].resetStats();
                }
            }
        }
        this.map = null;
        this.battlers = new Battler[2];
        this.turn = new Turn[2];
        this.attacks = new String[2];
        this.switches = new Battler[2];
        this.characterIds = new int[2];
        this.disposed = true;
        if (this.npcTeam != null) {
            this.npcTeam.clear();
            this.npcTeam = null;
        }
    }

    public void dispose(MapleCharacter chr, boolean dc) {
        boolean npc = this.npcTeam != null;
        dispose();
        chr.setBattle(null);
        for (Battler b : chr.getBattlers()) {
            if (b != null) {
                b.resetStats();
            }
        }
        if (this.instanceid >= 0) {
            if (((!dc) || (npc)) && (chr.getMapId() == this.mapid)) {
                MapleMap mapz = chr.getClient().getChannelServer().getMapFactory().getMap(this.mapid);
                chr.changeMap(mapz, mapz.findClosestPortal(chr.getTruePosition()));
            }
            chr.getClient().getChannelServer().getMapFactory().removeInstanceMap(this.instanceid);
        } else if ((chr.getMapId() == this.mapid) && (!dc)) {
            chr.fakeRelog();
        }
    }

    public MapleMap getMap() {
        return this.map;
    }

    public Battler getBattler(int index) {
        return this.battlers[index];
    }

    public void updateHP(int i) {
        if ((this.battlers[i] != null) && (this.battlers[i].getMonster() != null)) {
            if (this.characterIds[0] > 0) {
                MapleCharacter chrr = this.map.getCharacterById(this.characterIds[0]);
                if (chrr != null) {
                    chrr.getClient().getSession().write(MobPacket.showBossHP(9300184, this.battlers[i].getCurrentHP(), this.battlers[i].calcHP()));
                }
            }
        }
    }

    public MapleMonster spawnMonster(Point pos, boolean facingLeft, boolean animation, Battler b) {
        MapleMonster mons = MapleLifeFactory.getMonster(b.getMonsterId());
        if (mons == null) {
            return mons;
        }
        mons.setFake(true);
        mons.setLinkCID(b.getCharacterId() <= 0 ? 1 : b.getCharacterId());
        if (facingLeft) {
            mons.setStance(4);
        }
        this.map.spawnMonster_Pokemon(mons, pos, animation ? 15 : -2);
        b.setMonster(mons);
        for (int i = 0; i < this.characterIds.length; i++) {
            if (this.characterIds[i] == b.getCharacterId()) {
                updateHP(i);
                break;
            }
        }
        return mons;
    }

    public void initiate(MapleCharacter initiator, PokemonMap mapp) {
        spawnMonster(mapp.pos1, mapp.facingLeft, false, this.battlers[1]);
        initiator.dropMessage(-6, "Wild " + this.battlers[1].getName() + " has appeared! (Level " + this.battlers[1].getLevel() + ")");
        initiator.dropMessage(-3, "Go, " + this.battlers[0].getName() + "! (Level " + this.battlers[0].getLevel() + ")");
        spawnMonster(mapp.pos0, !mapp.facingLeft, true, this.battlers[0]);
        initiator.dropMessage(-6, "What will you do? @use @info @ball @run @battlehelp");
        this.turn[1] = makeRandomTurn();
        initiator.getClient().getSession().write(MaplePacketCreator.musicChange(wildBGM));
        initiator.getMonsterBook().monsterSeen(initiator.getClient(), this.battlers[1].getMonsterId(), this.battlers[1].getOriginalName());
        firstTurn(this.battlers[0], this.battlers[1]);
    }

    public void initiate(MapleCharacter initiator, MapleNPC npc) {
        Point ourPos = BattleConstants.getNPCPos(initiator.getMapId());
        spawnMonster(new Point(ourPos.x - 100, ourPos.y), false, true, this.battlers[1]);
        initiator.dropMessage(-6, "Trainer " + npc.getName() + " wants to battle!");
        initiator.dropMessage(-6, "Go, " + this.battlers[1].getName() + "! (Level " + this.battlers[1].getLevel() + ")");
        initiator.dropMessage(-3, "Go, " + this.battlers[0].getName() + "! (Level " + this.battlers[0].getLevel() + ")");
        spawnMonster(new Point(ourPos.x - 400, ourPos.y), true, true, this.battlers[0]);
        initiator.dropMessage(-6, "What will you do? @use @info @ball @run @battlehelp");
        this.turn[1] = makeRandomTurn();
        initiator.getClient().getSession().write(MaplePacketCreator.musicChange(npcBGM));
        firstTurn(this.battlers[0], this.battlers[1]);
    }

    public void initiate() {
        MapleCharacter initiator = this.map.getCharacterById(this.battlers[0].getCharacterId());
        MapleCharacter initiator2 = this.map.getCharacterById(this.battlers[1].getCharacterId());
        initiator.dropMessage(-6, initiator2.getName() + " wants to battle!");
        initiator2.dropMessage(-6, initiator.getName() + " wants to battle!");
        this.map.broadcastMessage(MaplePacketCreator.getChatText(initiator.getId(), "Go, " + this.battlers[0].getName() + "! (Level " + this.battlers[0].getLevel() + ")", initiator.isGM(), 0));
        this.map.broadcastMessage(MaplePacketCreator.getChatText(initiator2.getId(), "Go, " + this.battlers[1].getName() + "! (Level " + this.battlers[1].getLevel() + ")", initiator2.isGM(), 0));
        spawnMonster(new Point(initiator.getTruePosition().x + (initiator.getTruePosition().x > initiator2.getTruePosition().x ? -100 : 100), initiator.getTruePosition().y), initiator.getTruePosition().x < initiator2.getTruePosition().x, true, this.battlers[0]);
        spawnMonster(new Point(initiator2.getTruePosition().x + (initiator2.getTruePosition().x > initiator.getTruePosition().x ? -100 : 100), initiator2.getTruePosition().y), initiator2.getTruePosition().x < initiator.getTruePosition().x, true, this.battlers[1]);
        initiator.dropMessage(-6, "What will you do? @use @info @ball @run @battlehelp");
        initiator2.dropMessage(-6, "What will you do? @use @info @ball @run @battlehelp");
        initiator.getClient().getSession().write(MaplePacketCreator.musicChange(trainerBGM));
        initiator2.getClient().getSession().write(MaplePacketCreator.musicChange(trainerBGM));
        firstTurn(this.battlers[0], this.battlers[1]);
        firstTurn(this.battlers[1], this.battlers[0]);
    }

    public void forfeit(MapleCharacter forfeiter, boolean dc) {
        if (!dc) {
            if (this.turn[1] != null) {
                if (this.turn[1] != Turn.TRUANT) {
                    forfeiter.dropMessage(-6, "You've already selected an action.");
                    return;
                }
            }
        }
        if (this.instanceid >= 0) {
            if ((!dc) && (this.battlers[0] != null) && (this.battlers[1] != null) && (this.battlers[1].getLevel() > this.battlers[0].getLevel()) && (this.battlers[1].getLevel() > 1) && (Randomizer.nextInt(this.battlers[1].getLevel() - this.battlers[0].getLevel()) / 10 != 0) && (this.battlers[0].getAbility() != PokemonAbility.RunAway)) {
                forfeiter.dropMessage(-6, "Couldn't get away!");
                this.turn[1] = makeRandomTurn();
                makeTurn();
                return;
            }
            dispose(forfeiter, dc);
        } else {
            MapleMap theMap = this.map;
            if (theMap == null) {
                theMap = forfeiter.getMap();
                if (theMap == null) {
                    dispose(forfeiter, dc);
                    return;
                }
            }
            theMap.broadcastMessage(MaplePacketCreator.serverNotice(6, forfeiter.getName() + " has left the match."));
            if (!this.disposed) {
                dispose(forfeiter.getId() == this.characterIds[0] ? theMap.getCharacterById(this.characterIds[1]) : theMap.getCharacterById(this.characterIds[0]), dc);
            }
            dispose(forfeiter, dc);
        }
    }

    public boolean attack(MapleCharacter chr, String name) {
        if (battlers[0] == null || battlers[1] == null || (turn[chr.getId() == characterIds[0] ? 0 : 1] != null && (turn[chr.getId() == characterIds[0] ? 0 : 1] != Turn.TRUANT || attacks[chr.getId() == characterIds[0] ? 0 : 1] != null))) {
            return false;
        }
        attacks[chr.getId() == characterIds[0] ? 0 : 1] = name;
        if (turn[chr.getId() == characterIds[0] ? 0 : 1] == null) {
            turn[chr.getId() == characterIds[0] ? 0 : 1] = Turn.ATTACK;
        }
        if (turn[chr.getId() == characterIds[0] ? 1 : 0] != null) {
            makeTurn();
        }
        return true;
    }

    public boolean switchBattler(MapleCharacter chr, Battler b) {
        try {
            if (b.getCurrentHP() > 0L) {
                if (this.turn[1] == null);
            } else {
                return false;
            }

            if (b != this.battlers[1]) {
                if (this.battlers[0] != null);
            } else {
                return false;
            }

            if (this.battlers[0] != null) {
                if (this.battlers[0].getAbility() == PokemonAbility.ShadowTag) {
                    return false;
                }
            }
            this.turn[(chr.getId() == this.characterIds[0] ? 0 : 1)] = Turn.SWITCH;
            this.switches[(chr.getId() == this.characterIds[0] ? 0 : 1)] = b;
            if (this.turn[0] != null) {
                makeTurn();
            }
            return true;
        } catch (NullPointerException e) {
            FileoutputUtil.outputFileError("log\\Script_Except.log", e);
        }
        return false;
    }

    public boolean useBall(final MapleCharacter user, PokemonItem itemId) {
        if (this.battlers[1] == null) {
            return false;
        }
        boolean toBox = false;
        int highestLevel = 0;
        for (Battler b : user.getBattlers()) {
            if ((b != null) && (b.getLevel() > highestLevel)) {
                highestLevel = b.getLevel();
            }
            if ((b != null) && (b.getMonsterId() == this.battlers[1].getMonsterId())) {
                toBox = true;
            }
        }
        if ((this.turn[1] == Turn.DISABLED) || ((this.turn[0] != null) && (this.turn[0] != Turn.TRUANT)) || (this.battlers[0] == null) || (this.battlers[1] == null) || (highestLevel + 5 < this.battlers[1].getLevel()) || (this.npcTeam != null) || (this.disposed)) {
            return false;
        }
        if (this.turn[0] == Turn.TRUANT) {
            this.turn[0] = null;
        }
        user.dropMessage(-3, "Go, " + StringUtil.makeEnumHumanReadable(itemId.name()) + "!");
        catchEffect(this.battlers[1].getMonster());
        final String mName = this.battlers[1].getOriginalName();
        long rand = Math.round(this.battlers[1].canCatch(itemId.catchChance));
        for (int i = 0; i < messages.length; i++) {
            if (Randomizer.nextInt(256) <= rand) {
                if (i != messages.length - 1) {
                    EtcTimer.getInstance().schedule(new Runnable() {

                        @Override
                        public void run() {
                            if ((user != null) && (!disposed)) {
                                user.getClient().getSession().write(MaplePacketCreator.playSound("Cokeplay/Fall"));
                                catchEffect(battlers[1].getMonster());
                            } else {
                                dispose();
                            }
                        }
                    }, 2000 * i + 2000);
                } else {
                    this.turn[1] = Turn.DISABLED;
                    final boolean toBo = (toBox) || (user.countBattlers() >= 6);
                    EtcTimer.getInstance().schedule(new Runnable() {

                        @Override
                        public void run() {
                            if ((user != null) && (!disposed)) {
                                map.killMonster(battlers[1].getMonster());
                                user.getClient().getSession().write(MaplePacketCreator.playSound("Romio/discovery"));
                                user.dropMessage(-6, "Monster " + battlers[1].getName() + " has been successfully caught!");
                                caughtMonster(user, mName, toBo);
                            } else {
                                dispose();
                            }
                        }
                    }, 2000 * i + 2000);

                    return true;
                }
            } else {
                this.turn[1] = Turn.DISABLED;
                final String msgg = messages[i];
                EtcTimer.getInstance().schedule(new Runnable() {

                    @Override
                    public void run() {
                        if ((user != null) && (!disposed)) {
                            user.dropMessage(-3, msgg);
                            turn[1] = makeRandomTurn();
                            makeTurn();
                        } else {
                            dispose();
                        }
                    }
                }, 2000 * i + 2500);

                return true;
            }
        }
        return true;
    }

    public void catchEffect(MapleMonster mons) {
        this.map.broadcastMessage(MobPacket.killMonster(mons.getObjectId(), 0));
        this.map.broadcastMessage(MobPacket.makeMonsterEffect(mons, 15));
    }

    public Turn makeRandomTurn() {
        if ((this.battlers[1] == null) || (this.battlers[0] == null)) {
            return Turn.DISABLED;
        }
        if ((this.npcTeam == null) && (this.battlers[1].getLevel() < this.battlers[0].getLevel()) && (this.battlers[1].getLevel() >= 1) && (Randomizer.nextInt(this.battlers[0].getLevel() - this.battlers[1].getLevel()) > 10) && (Randomizer.nextInt(50) >= 40)) {
            return Turn.SWITCH;
        }
        return Turn.ATTACK;
    }

    public void firstTurn(Battler ours, Battler theirs) {
        if (ours.getAbility() == PokemonAbility.Forewarn) {
            this.map.broadcastMessage(MaplePacketCreator.serverNotice(6, theirs.getName() + "'s type is " + theirs.getElementString() + "!"));
        } else if (ours.getAbility() == PokemonAbility.Frisk) {
            this.map.broadcastMessage(MaplePacketCreator.serverNotice(6, theirs.getName() + "'s item is " + theirs.getItemString() + "!"));
        } else if (ours.getAbility() == PokemonAbility.Intimidate) {
            theirs.setMod(PokemonStat.ATK, theirs.decreaseMod(theirs.getMod(PokemonStat.ATK)));
            this.map.broadcastMessage(MaplePacketCreator.serverNotice(6, ours.getName() + "'s Intimidate scared the opponent!"));
        }
    }

    public void caughtMonster(MapleCharacter user, String mName, boolean toBox) {
        if (toBox) {
            user.getBoxed().add(this.battlers[1]);
        } else {
            user.getBattlers()[user.countBattlers()] = this.battlers[1];
        }
        this.battlers[1].setCharacterId(user.getId());
        user.changedBattler();
        user.getMonsterBook().monsterCaught(user.getClient(), this.battlers[1].getMonsterId(), mName);
        dispose(user, false);
    }

    public void makeTurn() {
        final int theFirst;
        int[] order;
        if ((this.turn[0] != Turn.SWITCH) && ((this.turn[1] == Turn.SWITCH) || ((this.battlers[1] != null) && (this.battlers[0] != null) && (((this.battlers[1].getItem() != null) && (this.battlers[1].getItem() == HoldItem.Orange_Star) && (Randomizer.nextBoolean())) || ((this.battlers[1].getSpeed() > this.battlers[0].getSpeed()) && ((this.battlers[0].getItem() == null) || (this.battlers[0].getItem() != HoldItem.Orange_Star) || (!Randomizer.nextBoolean()))))))) {
            order = new int[]{1, 0};
            theFirst = 1;
        } else {
            order = new int[]{0, 1};
            theFirst = 0;
        }
        int timeFirst = WALK_TIME / 8;
        for (final int i : order) {
            if (this.turn[i] == null) {
                continue;
            }
            if ((this.battlers[1] != null) && (this.turn[i] != Turn.DISABLED)) {
                if ((this.npcTeam == null) && (i == 1) && (this.turn[i] == Turn.SWITCH) && (this.instanceid >= 0)) {
                    MapleCharacter chr = this.map.getCharacterById(this.characterIds[0]);
                    chr.dropMessage(-6, "The wild monster fled.");
                    dispose(chr, false);
                    return;
                }
                if (this.battlers[i] != null) {
                    this.battlers[i].addMonsterId(this.battlers[1].getMonsterId());
                }
                Timer.EtcTimer.getInstance().schedule(new Runnable() {

                    public void run() {
                        if ((map != null) && (turn[i] != null) && (!disposed)) {
                            if (battlers[1] != null);
                        } else {
                            return;
                        }

                        switch (turn[i]) {
                            case TRUANT:
                                if (battlers[i] == null) {
                                    break;
                                }
                                map.broadcastMessage(MaplePacketCreator.serverNotice(6, battlers[i].getName() + " was loafing around!"));
                                break;
                            case SWITCH:
                                if (battlers[1].getMonster() == null) {
                                    map.broadcastMessage(MaplePacketCreator.serverNotice(5, "The move could not be made due to an error."));
                                    return;
                                }
                                boolean left = battlers[1].getMonster().isFacingLeft();
                                Point pos = new Point(battlers[1].getMonster().getTruePosition().x + (left ? -300 : 300), battlers[1].getMonster().getTruePosition().y - 20);
                                if (battlers[i] != null) {
                                    if (battlers[i].getMonster() == null) {
                                        map.broadcastMessage(MaplePacketCreator.serverNotice(5, "The move could not be made due to an error."));
                                        return;
                                    }
                                    map.broadcastMessage(MaplePacketCreator.getChatText(characterIds[i], "Return, " + battlers[i].getName() + "!", false, 0));
                                    pos = battlers[i].getMonster().getPosition();
                                    left = !battlers[i].getMonster().isFacingLeft();
                                    map.killMonster(battlers[i].getMonster());
                                    battlers[i].wipe();
                                    if (battlers[i].getAbility() == PokemonAbility.NaturalCure) {
                                        battlers[i].wipeStatus();
                                    } else if (battlers[i].getAbility() == PokemonAbility.Regenerator) {
                                        battlers[i].damage((int) (-(battlers[i].calcHP() / 3L)), null, 0, true);
                                    }
                                }
                                battlers[i] = switches[i];
                                if (battlers[i] == null) {
                                    map.broadcastMessage(MaplePacketCreator.serverNotice(5, "The move could not be made due to an error."));
                                    return;
                                }
                                if ((i == 1) && (npcTeam != null)) {
                                    map.broadcastMessage(MaplePacketCreator.serverNotice(5, "Go, " + battlers[i].getName() + "! (Level " + battlers[i].getLevel() + ")"));
                                } else {
                                    map.broadcastMessage(MaplePacketCreator.getChatText(characterIds[i], "Go, " + battlers[i].getName() + "! (Level " + battlers[i].getLevel() + ")", false, 0));
                                }
                                spawnMonster(pos, left, true, battlers[i]);
                                firstTurn(battlers[i], battlers[0]);
                                break;
                            case ATTACK:
                                if ((battlers[i] == null) || (battlers[i].getMonster() == null)) {
                                    map.broadcastMessage(MaplePacketCreator.serverNotice(5, "The move could not be made due to an error."));
                                    return;
                                }
                                if (attacks[i] != null) {
                                    map.broadcastMessage(MaplePacketCreator.getChatText(characterIds[i], battlers[i].getName() + ", use " + attacks[i] + "!", false, 0));
                                }
                                battlers[i].decreaseStatusTurns();
                                boolean st = false;
                                if ((battlers[i].getAbility() == PokemonAbility.ShedSkin) && (Randomizer.nextBoolean())) {
                                    battlers[i].decreaseStatusTurns();
                                }
                                final MonsterStatus s;
                                if (battlers[i].getCurrentStatus() != null) {
                                    if (battlers[i].getAbility() == PokemonAbility.Guts) {
                                        battlers[i].setMod(PokemonStat.ATK, battlers[i].decreaseMod(battlers[i].getMod(PokemonStat.ATK)));
                                    } else if (battlers[i].getAbility() == PokemonAbility.MarvelScale) {
                                        battlers[i].setMod(PokemonStat.DEF, battlers[i].decreaseMod(battlers[i].getMod(PokemonStat.DEF)));
                                    } else if (battlers[i].getAbility() == PokemonAbility.QuickFeet) {
                                        battlers[i].setMod(PokemonStat.SPEED, battlers[i].decreaseMod(battlers[i].getMod(PokemonStat.SPEED)));
                                    }
                                    s = battlers[i].getCurrentStatus().getStati();
                                    switch (s) {
                                        case Ñ£ÔÎ:
                                            map.broadcastMessage(MaplePacketCreator.serverNotice(6, battlers[i].getName() + " is stunned and cannot move!"));
                                            st = true;
                                            break;
                                        case ½á±ù:
                                            map.broadcastMessage(MaplePacketCreator.serverNotice(6, battlers[i].getName() + " is frozen and cannot move!"));
                                            st = true;
                                            break;
                                        case ¹í¿Ì·û:
                                            battlers[i].setMod(PokemonStat.SPATK, battlers[i].increaseMod(battlers[i].getMod(PokemonStat.SPATK)));
                                            if (battlers[i].getAbility() != PokemonAbility.MagicGuard) {
                                                battlers[i].damage((int) battlers[i].calcHP() / 16, map, battlers[0] == null ? 0 : battlers[0].getMonsterId(), true);
                                                updateHP(i);
                                                map.broadcastMessage(MaplePacketCreator.serverNotice(6, battlers[i].getName() + " was hurt " + battlers[i].calcHP() / 16L + " damage by imprint!"));
                                            }
                                            break;
                                        case ¿Ö»Å:
                                            battlers[i].setMod(PokemonStat.ATK, battlers[i].increaseMod(battlers[i].getMod(PokemonStat.ATK)));
                                            if (battlers[i].getAbility() != PokemonAbility.MagicGuard) {
                                                battlers[i].damage((int) battlers[i].calcHP() / 16, map, battlers[0] == null ? 0 : battlers[0].getMonsterId(), true);
                                                updateHP(i);
                                                map.broadcastMessage(MaplePacketCreator.serverNotice(6, battlers[i].getName() + " was hurt " + battlers[i].calcHP() / 16L + " damage by burn!"));
                                            }
                                            break;
                                        case ÐÄÁé¿ØÖÆ:
                                            if (battlers[i].getAbility() == PokemonAbility.EarlyBird) {
                                                battlers[i].decreaseStatusTurns();
                                            } else if (battlers[1].getAbility() == PokemonAbility.BadDreams) {
                                                if (battlers[i].getAbility() != PokemonAbility.MagicGuard) {
                                                    battlers[i].damage((int) battlers[i].calcHP() / 16, map, battlers[0] == null ? 0 : battlers[0].getMonsterId(), true);
                                                    updateHP(i);
                                                    map.broadcastMessage(MaplePacketCreator.serverNotice(6, battlers[i].getName() + " was hurt " + battlers[i].calcHP() / 16L + " damage by the opponent's Bad Dreams!"));
                                                }
                                            }
                                            break;
                                        case ÖÐ¶¾:
                                            if (battlers[i].getAbility() != PokemonAbility.MagicGuard) {
                                                battlers[i].damage((int) (battlers[i].getAbility() == PokemonAbility.PoisonHeal ? -(battlers[i].calcHP() / 16L) : battlers[i].calcHP() / 16L), map, battlers[0] == null ? 0 : battlers[0].getMonsterId(), true);
                                                updateHP(i);
                                                map.broadcastMessage(MaplePacketCreator.serverNotice(6, battlers[i].getName() + " was hurt " + battlers[i].calcHP() / 16L + " damage by poison!"));
                                            }
                                            break;
                                        case Ó°Íø:
                                            if (Randomizer.nextInt(4) != 0) {
                                                break;
                                            }
                                            map.broadcastMessage(MaplePacketCreator.serverNotice(6, battlers[i].getName() + " was paralyzed and could not move!"));
                                            st = true;
                                        case ËÙ¶È:
                                            battlers[i].setMod(PokemonStat.SPEED, battlers[i].increaseMod(battlers[i].getMod(PokemonStat.SPEED)));
                                            break;
                                        case ÌôÐÆ:
                                            map.broadcastMessage(MaplePacketCreator.serverNotice(6, battlers[i].getName() + " is angry and confused!"));
                                            if ((Randomizer.nextBoolean()) && (battlers[i].getAbility() != PokemonAbility.MagicGuard)) {
                                                battlers[i].damage((int) battlers[i].calcHP() / 16, map, battlers[0] == null ? 0 : battlers[0].getMonsterId(), true);
                                                updateHP(i);
                                                map.broadcastMessage(MaplePacketCreator.serverNotice(6, battlers[i].getName() + " was hurt " + battlers[i].calcHP() / 16 + " damage in its rage!"));
                                                st = true;
                                            }
                                            battlers[i].setMod(PokemonStat.ATK, battlers[i].decreaseMod(battlers[i].getMod(PokemonStat.ATK)));
                                            battlers[i].setMod(PokemonStat.SPATK, battlers[i].decreaseMod(battlers[i].getMod(PokemonStat.SPATK)));
                                            battlers[i].setMod(PokemonStat.DEF, battlers[i].increaseMod(battlers[i].getMod(PokemonStat.DEF)));
                                            battlers[i].setMod(PokemonStat.SPDEF, battlers[i].increaseMod(battlers[i].getMod(PokemonStat.SPDEF)));
                                    }
                                } else {
                                    s = null;
                                }
                                if (st) {
                                    break;
                                }
                                if (battlers[i].getAbility() != PokemonAbility.SheerForce);
                                final boolean extraEffects = (battlers[1].getAbility() != PokemonAbility.ShieldDust) && (s != MonsterStatus.Ä§»÷ÎÞÐ§);
                                int critChance = 20;
                                if ((battlers[0].getAbility() != PokemonAbility.Klutz) && (battlers[i].getItem() == HoldItem.Green_Star)) {
                                    critChance /= 2;
                                }
                                if ((battlers[i].getAbility() == PokemonAbility.SuperLuck) || (battlers[i].getAbility() == PokemonAbility.SereneGrace)) {
                                    critChance /= 2;
                                }
                                Point startPos = battlers[i].getMonster().getPosition();
                                Point maskedPos = null;
                                int origStance = battlers[i].getMonster().getStance();
                                final int atk = Randomizer.nextInt(battlers[i].getMonster().getStats().getMobAttacks().size() + 1);
                                MobAttackInfo theAtk = atk > 0 ? battlers[i].getMonster().getStats().getMobAttack(atk - 1) : null;
                                boolean speshul = (theAtk != null) && ((theAtk.magic) || (theAtk.MADamage > theAtk.PADamage));
                                if (((speshul) || (Randomizer.nextInt(5) == 0)) && (s != MonsterStatus.·âÓ¡));
                                final PokemonElement element = (battlers[0].getAbility() != PokemonAbility.Normalize) && (battlers[i].getElementSize() > 0) ? battlers[i].getElements()[Randomizer.nextInt(battlers[i].getElementSize())] : PokemonElement.None;
                                boolean stab = element != PokemonElement.None;
                                final boolean critical = (Randomizer.nextInt(critChance) == 0) && (extraEffects);
                                final boolean flinch = (Randomizer.nextInt(battlers[i].getAbility() == PokemonAbility.SereneGrace ? 25 : 50) == 0) && (extraEffects);
                                PokemonStat decc = null;
                                PokemonStat incc = null;
                                MonsterStatus statt = null;
                                final int levelDec = Randomizer.nextInt(10) == 0 ? 2 : 1;
                                final int levelInc = Randomizer.nextInt(10) == 0 ? 2 : 1;
                                long base = battlers[i].getLevel() + 1;

                                int incDivisor = 1;
                                if ((battlers[0].getAbility() != PokemonAbility.Klutz) && (battlers[i].getItem() == HoldItem.Herb_Pouch)) {
                                    incDivisor *= 2;
                                }
                                if (battlers[i].getAbility() == PokemonAbility.SereneGrace) {
                                    incDivisor *= 2;
                                }
                                if (extraEffects) {
                                    if (battlers[1].getAbility() != PokemonAbility.ClearBody) {
                                        if ((battlers[1].getAbility() != PokemonAbility.BigPecks) && (Randomizer.nextInt(20 / incDivisor) == 0)) {
                                            base = base * 9L / 10L;
                                            decc = (stab) || (speshul) ? PokemonStat.SPDEF : PokemonStat.DEF;
                                            break;
                                        }
                                    }
                                }
                                if (extraEffects) {
                                    if (battlers[1].getAbility() != PokemonAbility.HyperCutter) {
                                        if ((battlers[1].getAbility() != PokemonAbility.ClearBody) && (Randomizer.nextInt(20 / incDivisor) == 0)) {
                                            base = base * 9L / 10L;
                                            decc = (stab) || (speshul) ? PokemonStat.SPATK : PokemonStat.ATK;
                                            break;
                                        }
                                    }
                                }
                                if (extraEffects) {
                                    if ((battlers[1].getAbility() != PokemonAbility.ClearBody) && (Randomizer.nextInt(50 / incDivisor) == 0)) {
                                        base = base * 9L / 10L;
                                        decc = (stab) || (speshul) ? PokemonStat.EVA : PokemonStat.SPEED;
                                    }
                                }
                                int decDivisor = 1;
                                if ((battlers[0].getAbility() != PokemonAbility.Klutz) && (battlers[i].getItem() == HoldItem.Ripped_Note)) {
                                    decDivisor *= 2;
                                }
                                if (battlers[i].getAbility() == PokemonAbility.SereneGrace) {
                                    decDivisor *= 2;
                                }
                                if ((extraEffects) && (Randomizer.nextInt(20 / decDivisor) == 0)) {
                                    base = base * 9L / 10L;
                                    incc = (stab) || (speshul) ? PokemonStat.SPDEF : PokemonStat.DEF;
                                } else if ((extraEffects) && (Randomizer.nextInt(20 / decDivisor) == 0)) {
                                    base = base * 9L / 10L;
                                    incc = (stab) || (speshul) ? PokemonStat.SPATK : PokemonStat.ATK;
                                } else if ((extraEffects) && (Randomizer.nextInt(50 / decDivisor) == 0)) {
                                    base = base * 9L / 10L;
                                    incc = (stab) || (speshul) ? PokemonStat.ACC : PokemonStat.SPEED;
                                }
                                double eez = element.getEffectiveness(battlers[1].getElements());
                                final double ee = (eez <= 0.0D) && (battlers[i].getAbility() == PokemonAbility.Scrappy) ? 1.0D : eez;
                                int statDivisor = 1;
                                if ((battlers[0].getAbility() != PokemonAbility.Klutz) && (battlers[i].getItem() == HoldItem.King_Star)) {
                                    statDivisor *= 2;
                                }
                                if (battlers[i].getAbility() == PokemonAbility.SereneGrace) {
                                    statDivisor *= 2;
                                }
                                if ((extraEffects) && (Randomizer.nextInt(33 / statDivisor) == 0) && (ee >= 1.0D)) {
                                    switch (element) {
                                        case None:
                                        case Normal:
                                        case Immortal:
                                            statt = MonsterStatus.Ñ£ÔÎ;
                                            break;
                                        case Enchanted:
                                            statt = MonsterStatus.¹í¿Ì·û;
                                            break;
                                        case Devil:
                                        case Fire:
                                            statt = MonsterStatus.ÁÒÑæÅçÉä;
                                            break;
                                        case Reptile:
                                        case Mammal:
                                            statt = MonsterStatus.·âÓ¡;
                                            break;
                                        case Ice:
                                            statt = MonsterStatus.½á±ù;
                                            break;
                                        case Lightning:
                                            statt = MonsterStatus.Ó°Íø;
                                            break;
                                        case Fish:
                                            statt = MonsterStatus.ËÙ¶È;
                                            break;
                                        case Plant:
                                        case Spirit:
                                            statt = MonsterStatus.ÖÐ¶¾;
                                            break;
                                        case Holy:
                                            statt = MonsterStatus.ÌôÐÆ;
                                            break;
//                case :
//                  statt = MonsterStatus.Ä§»÷ÎÞÐ§;
//                  break;
                                        case Dark:
                                            statt = MonsterStatus.¿Ö»Å;
                                            break;
                                    }
                                }

                                if ((extraEffects) && (statt == null) && (theAtk != null) && (theAtk.getDiseaseSkill() > 0) && (Randomizer.nextInt(10 / statDivisor) == 0)) {
                                    statt = MonsterStatus.getBySkill_Pokemon(theAtk.getDiseaseSkill());
                                }
                                if ((statt == null) && (battlers[i].getAbility() == PokemonAbility.PoisonTouch) && (Randomizer.nextInt(5) == 0)) {
                                    statt = MonsterStatus.ÖÐ¶¾;
                                }
                                final PokemonStat dec = decc;
                                final PokemonStat inc = incc;
                                final MonsterStatus stat = statt;
                                final long basedamagee;
                                if (ee > 0.0D) {
                                    double stabModifier = (battlers[0].getAbility() != PokemonAbility.Klutz) && (battlers[i].getItem() == HoldItem.Medal) ? 0.3D : 0.0D;
                                    if (battlers[i].getAbility() == PokemonAbility.Adaptability) {
                                        stabModifier += 0.3D;
                                    } else if ((battlers[i].getAbility() == PokemonAbility.Blaze) && (battlers[i].getHPPercent() <= 33) && (element == PokemonElement.Fire)) {
                                        stabModifier += 0.5D;
                                    } else if ((battlers[i].getAbility() == PokemonAbility.Torrent) && (battlers[i].getHPPercent() <= 33) && (element == PokemonElement.Fish)) {
                                        stabModifier += 0.5D;
                                    } else if ((battlers[i].getAbility() == PokemonAbility.Overgrow) && (battlers[i].getHPPercent() <= 33) && (element == PokemonElement.Plant)) {
                                        stabModifier += 0.5D;
                                    }
                                    if (battlers[0].getAbility() == PokemonAbility.Filter) {
                                        stabModifier -= 0.5D;
                                    }
                                    basedamagee = (long) Math.max(1.0D, Math.ceil((((battlers[i].getAbility() == PokemonAbility.Analytic) && (theFirst != i) ? 1.0D : 0.75D) + battlers[i].getLevel() / 200.0D) * (!element.special ? battlers[i].getATK(atk) : battlers[i].getSpATK(atk)) * (base / 5.0D) * (100.0D - (!element.special ? battlers[1].getDEF() : battlers[1].getSpDEF())) / 100.0D + 2.0D) * (stab ? stabModifier + 3.0D : 2.0D) * Math.round(ee * 2.0D * ((battlers[0].getAbility() != PokemonAbility.Klutz) && (battlers[i].getItem() == BattleConstants.HoldItem.Other_World_Key) && (ee >= 2.0D) ? 1.1D : 1.0D)) * (35.0D + Randomizer.nextInt(15)) / 200.0D);
                                } else {
                                    basedamagee = 1;
                                }
                                final byte skillByte = (byte) (atk <= 0 ? -1 : 26 + (atk - 1) * 2 + battlers[i].getMonster().getFacingDirection());
                                final List<LifeMovementFragment> moves = new ArrayList<LifeMovementFragment>();
                                if (theAtk == null) {
                                    AbsoluteLifeMovement alm = new AbsoluteLifeMovement(0, battlers[0].getMonster().getPosition(), WALK_TIME, battlers[i].getMonster().getFacingDirection() + 2);
                                    alm.defaulted();
                                    moves.add(alm);
                                    alm = new AbsoluteLifeMovement(0, startPos, WALK_TIME, (battlers[i].getMonster().getFacingDirection() == 1 ? 0 : 1) + 2);
                                    alm.defaulted();
                                    moves.add(alm);
                                    alm = new AbsoluteLifeMovement(0, startPos, 0, origStance);
                                    alm.defaulted();
                                    moves.add(alm);
                                } else {
                                    if (startPos.x < battlers[0].getMonster().getTruePosition().x) {
                                        if (battlers[0].getMonster().getTruePosition().x - theAtk.getRange() > startPos.x);
                                    } else if (startPos.x <= battlers[0].getMonster().getTruePosition().x) {
                                        break;
                                    }
                                    boolean shouldAdd = battlers[0].getMonster().getTruePosition().x + theAtk.getRange() < startPos.x;
                                    if (shouldAdd) {
                                        List moves2 = new ArrayList();
                                        maskedPos = new Point(battlers[0].getMonster().getTruePosition().x + (startPos.x < battlers[0].getMonster().getTruePosition().x ? -theAtk.getRange() : theAtk.getRange()), battlers[0].getMonster().getTruePosition().y);
                                        AbsoluteLifeMovement alm = new AbsoluteLifeMovement(0, maskedPos, WALK_TIME, battlers[i].getMonster().getFacingDirection() + 2);
                                        alm.defaulted();
                                        moves2.add(alm);
                                        map.broadcastMessage(MobPacket.moveMonster(false, -1, 0, 0, 0, battlers[i].getMonster().getObjectId(), startPos, moves2));
                                    }
                                    RelativeLifeMovement rlm = new RelativeLifeMovement(2, maskedPos == null ? battlers[0].getMonster().getPosition() : new Point(0, 0), 0, battlers[i].getMonster().getFacingDirection() + 2);
                                    moves.add(rlm);
                                    if (shouldAdd) {
                                        AbsoluteLifeMovement alm = new AbsoluteLifeMovement(0, maskedPos, Math.min(theAtk.attackAfter, WALK_TIME - WALK_TIME / 2), battlers[i].getMonster().getFacingDirection() + 2);
                                        alm.defaulted();
                                        moves.add(alm);
                                        alm = new AbsoluteLifeMovement(0, startPos, WALK_TIME - Math.min(theAtk.attackAfter, WALK_TIME - WALK_TIME / 2), (battlers[i].getMonster().getFacingDirection() == 1 ? 0 : 1) + 2);
                                        alm.defaulted();
                                        moves.add(alm);
                                    }
                                    AbsoluteLifeMovement alm = new AbsoluteLifeMovement(0, startPos, 0, origStance);
                                    alm.defaulted();
                                    moves.add(alm);
                                }
                                final Point mPos = maskedPos;
                                if (mPos == null) {
                                    map.broadcastMessage(MobPacket.moveMonster(skillByte > 0, skillByte, 0, 0, 0, battlers[i].getMonster().getObjectId(), startPos, moves));
                                    moves.clear();
                                }
                                Timer.EtcTimer.getInstance().schedule(new Runnable() {

                                    public void run() {
                                        if (disposed || map == null || battlers[i == 1 ? 0 : 1] == null || battlers[i] == null) {
                                            return;
                                        }

                                        if (mPos != null) {
                                            map.broadcastMessage(MobPacket.moveMonster(skillByte > 0, skillByte, 0, 0, 0, battlers[i].getMonster().getObjectId(), mPos, moves));
                                            moves.clear();
                                        }
                                        long basedamage = basedamagee;
                                        if ((battlers[0].getAbility() != PokemonAbility.NoGuard) && (battlers[i].getAbility() != PokemonAbility.NoGuard)) {
                                            if (battlers[i].getACC() < Randomizer.nextInt(1 + battlers[1].getEVA()) * (s == MonsterStatus.¿Ö»Å ? 2 : 1)) {
                                                map.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append(battlers[i].getName()).append("'s attack missed!").toString()));
                                                basedamage = 0;
                                            }
                                        }
                                        if (ee <= 0.0D) {
                                            map.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append(battlers[i].getName()).append("'s attack didn't affect ").append(battlers[0].getName()).append("...").toString()));
                                        } else if (ee < 1.0D) {
                                            if (battlers[0].getAbility() == PokemonAbility.WonderGuard) {
                                                basedamage = 0L;
                                                map.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append(battlers[i].getName()).append("'s attack was negated by Wonder Guard!").toString()));
                                            } else {
                                                if (battlers[0].getAbility() != PokemonAbility.Klutz) {
                                                    if ((battlers[0].getAbility() != PokemonAbility.Unnerve) && (battlers[i].getItem() == HoldItem.Dark_Chocolate)) {
                                                        map.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append(battlers[i].getName()).append("'s item negated the effectiveness.").toString()));
                                                        basedamage *= 2L;
                                                        battlers[i].setItem(0);
                                                    }
                                                }
                                                if (battlers[i].getAbility() == PokemonAbility.TintedLens) {
                                                    map.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append(battlers[i].getName()).append("'s ability negated the effectiveness.").toString()));
                                                    basedamage *= 2L;
                                                } else {
                                                    map.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append(battlers[i].getName()).append("'s attack wasn't very effective.").toString()));
                                                }
                                            }
                                        } else if (ee > 1.0D) {
                                            if ((battlers[i].getAbility() != PokemonAbility.Klutz) && (battlers[i].getAbility() != PokemonAbility.Unnerve)) {
                                                if (battlers[1].getItem() == HoldItem.White_Chocolate) {
                                                    map.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append(battlers[i].getName()).append("'s effectiveness was negated!").toString()));
                                                    basedamage /= 2L;
                                                    battlers[1].setItem(0);
                                                }
                                            }
                                            map.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append(battlers[i].getName()).append("'s attack was super effective!").toString()));
                                        } else if (battlers[0].getAbility() == PokemonAbility.WonderGuard) {
                                            basedamage = 0L;
                                            map.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append(battlers[i].getName()).append("'s attack was negated by Wonder Guard!").toString()));
                                        }
                                        if (atk == 0) {
                                            if (battlers[i].getCurrentStatus() == null) {
                                                if ((battlers[0].getAbility() == PokemonAbility.EffectSpore) && (Randomizer.nextInt(100) < 50)) {
                                                    MonsterStatus stati = null;
                                                    switch (Randomizer.nextInt(3)) {
                                                        case 0:
                                                            stati = MonsterStatus.ÖÐ¶¾;
                                                            break;
                                                        case 1:
                                                            stati = MonsterStatus.Ó°Íø;
                                                            break;
                                                        case 2:
                                                            stati = MonsterStatus.¿Ö»Å;
                                                    }

                                                    MonsterStatusEffect mse = new MonsterStatusEffect(stati, Integer.valueOf(stati == MonsterStatus.ÖÐ¶¾ ? (int) (battlers[i].calcHP() / 16L) : 1), MonsterStatusEffect.genericSkill(stati), null, false);
                                                    battlers[i].setStatus(mse);
                                                    map.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append(battlers[i].getName()).append(" was inflicted with ").append(StringUtil.makeEnumHumanReadable(stati.name())).append(" status!").toString()));

                                                }
                                            }
                                            if (battlers[i].getCurrentStatus() == null) {
                                                if ((battlers[0].getAbility() == PokemonAbility.FlameBody) && (Randomizer.nextInt(100) < 50)) {
                                                    MonsterStatus stati = MonsterStatus.ÁÒÑæÅçÉä;
                                                    MonsterStatusEffect mse = new MonsterStatusEffect(stati, Integer.valueOf((int) (battlers[i].calcHP() / 16L)), MonsterStatusEffect.genericSkill(stati), null, false);
                                                    battlers[i].setStatus(mse);
                                                    map.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append(battlers[i].getName()).append(" was inflicted with ").append(StringUtil.makeEnumHumanReadable(stati.name())).append(" status!").toString()));

                                                }
                                            }
                                            if (battlers[i].getCurrentStatus() == null) {
                                                if ((battlers[0].getAbility() == PokemonAbility.PoisonPoint) && (Randomizer.nextInt(100) < 50)) {
                                                    MonsterStatus stati = MonsterStatus.ÖÐ¶¾;
                                                    MonsterStatusEffect mse = new MonsterStatusEffect(stati, Integer.valueOf((int) (battlers[i].calcHP() / 16L)), MonsterStatusEffect.genericSkill(stati), null, false);
                                                    battlers[i].setStatus(mse);
                                                    map.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append(battlers[i].getName()).append(" was inflicted with ").append(StringUtil.makeEnumHumanReadable(stati.name())).append(" status!").toString()));

                                                }
                                            }
                                            if (battlers[i].getCurrentStatus() == null) {
                                                if ((battlers[0].getAbility() == PokemonAbility.Static) && (Randomizer.nextInt(100) < 50)) {
                                                    MonsterStatus stati = MonsterStatus.Ó°Íø;
                                                    MonsterStatusEffect mse = new MonsterStatusEffect(stati, Integer.valueOf(1), MonsterStatusEffect.genericSkill(stati), null, false);
                                                    battlers[i].setStatus(mse);
                                                    map.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append(battlers[i].getName()).append(" was inflicted with ").append(StringUtil.makeEnumHumanReadable(stati.name())).append(" status!").toString()));

                                                }
                                            }
                                            if ((battlers[0].getAbility() == PokemonAbility.IronBarbs) && (battlers[i].getAbility() != PokemonAbility.MagicGuard)) {
                                                map.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append(battlers[i].getName()).append(" got hurt ").append(battlers[i].calcHP() / 8L).append(" damage from Rough Skin!").toString()));
                                                battlers[i].damage((int) battlers[i].calcHP() / 8, map, 0, false);
                                                updateHP(i);
                                            }
                                        }
                                        if ((basedamage > 1L) && (battlers[i].getAbility() == PokemonAbility.SheerForce) && (Randomizer.nextInt(3) == 0)) {
                                            basedamage = basedamage * 13L / 10L;
                                        }
                                        if (basedamage > 1L) {
                                            if ((battlers[0].getAbility() != PokemonAbility.Klutz) && (battlers[i].getItem() == HoldItem.Mini_Dragon) && (battlers[i].getAbility() != PokemonAbility.MagicGuard)) {
                                                basedamage = basedamage * 13L / 10L;
                                                battlers[i].damage((int) battlers[i].calcHP() / 8, map, battlers[0] == null ? 0 : battlers[0].getMonsterId(), true);
                                                updateHP(i);
                                                map.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append(battlers[i].getName()).append(" was hurt ").append(battlers[i].calcHP() / 8L).append(" damage from its item!").toString()));
                                            }
                                        }
                                        if ((battlers[1].getAbility() != PokemonAbility.BattleArmor) && (basedamage > 1L) && (critical)) {
                                            map.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append(battlers[i].getName()).append("'s attack was a critical hit!").toString()));
                                            basedamage *= (battlers[i].getAbility() == PokemonAbility.Sniper ? 3 : 2);
                                            if (battlers[1].getAbility() == PokemonAbility.AngerPoint) {
                                                map.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append(battlers[i].getName()).append("'s attack triggered the foe's Anger Point!").toString()));
                                                battlers[1].setMod(PokemonStat.ATK, battlers[1].increaseMod(battlers[1].getMod(PokemonStat.ATK)));
                                                battlers[1].setMod(PokemonStat.SPATK, battlers[1].increaseMod(battlers[1].getMod(PokemonStat.SPATK)));
                                            }
                                        }
                                        if ((basedamage > 1L) && (inc != null)) {
                                            map.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append(battlers[i].getName()).append("'s ").append(StringUtil.makeEnumHumanReadable(inc.name())).append(" increased ").append(levelInc == 1 ? "slightly" : "greatly").append("!").toString()));
                                            for (int x = 0; x < levelInc; x++) {
                                                battlers[i].setMod(inc, battlers[i].increaseMod(battlers[i].getMod(inc)));
                                            }
                                        }
                                        if ((basedamage > 1L) && (dec != null)) {
                                            if (battlers[1].getItem() != HoldItem.Kenta_Report) {
                                                map.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append(battlers[1].getName()).append("'s ").append(StringUtil.makeEnumHumanReadable(dec.name())).append(" decreased ").append(levelDec == 1 ? "slightly" : "greatly").append("!").toString()));
                                                for (int x = 0; x < levelDec; x++) {
                                                    battlers[1].setMod(dec, battlers[1].decreaseMod(battlers[1].getMod(dec)));
                                                }
                                                if (battlers[1].getAbility() == PokemonAbility.Defiant) {
                                                    for (int x = 0; x < 2; x++) {
                                                        battlers[1].setMod(PokemonStat.ATK, battlers[1].increaseMod(battlers[i].getMod(PokemonStat.ATK)));
                                                        battlers[1].setMod(PokemonStat.SPATK, battlers[1].increaseMod(battlers[i].getMod(PokemonStat.SPATK)));
                                                    }
                                                }
                                            }
                                        }
                                        if ((basedamage > 1L) && (flinch)) {
                                            map.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append(battlers[i].getName()).append(" absorbed ").append(basedamage / 2L).append(" HP!").toString()));
                                            battlers[i].damage((int) (-(basedamage / 2L)), map, 0, false);
                                            updateHP(i);
                                        } else if ((basedamage > 1L) && (battlers[i].getItem() != null)) {
                                            if ((battlers[0].getAbility() != PokemonAbility.Klutz) && (battlers[i].getItem() == HoldItem.Strange_Slush)) {
                                                int percentHP = (int) Math.min(100L, basedamage * 100L / battlers[0].calcHP());
                                                if ((percentHP > 0) && (battlers[i].calcHP() > 800L)) {
                                                    map.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append(battlers[i].getName()).append(" absorbed ").append(percentHP * battlers[i].calcHP() / 800L).append(" HP!").toString()));
                                                    battlers[i].damage((int) (-(percentHP * battlers[i].calcHP() / 800L)), map, 0, false);
                                                    updateHP(i);
                                                }
                                            }
                                        }
                                        if ((extraEffects) && (basedamage > 1L) && (stat != null)) {
                                            if ((battlers[1].getCurrentStatus() == null) && (battlers[i].getAbility() != PokemonAbility.Klutz)) {
                                                if (battlers[1].getItem() != HoldItem.Pheremone) {
                                                    if ((battlers[i].getAbility() != PokemonAbility.Klutz) && (battlers[i].getAbility() != PokemonAbility.Unnerve)) {
                                                        if (battlers[1].getItem() == HoldItem.Pineapple) {
                                                            map.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append(battlers[1].getName()).append("'s item negated the status!").toString()));
                                                            battlers[1].setItem(0);
                                                        }
                                                    }
                                                    battlers[1].setStatus(new MonsterStatusEffect(stat, Integer.valueOf(1), MonsterStatusEffect.genericSkill(stat), null, false));
                                                    map.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append(battlers[1].getName()).append(" was inflicted with ").append(StringUtil.makeEnumHumanReadable(stat.name())).append(" status!").toString()));
                                                    if ((battlers[1].getAbility() == PokemonAbility.Synchronize) && (battlers[i].getCurrentStatus() == null)) {
                                                        if ((battlers[0].getAbility() != PokemonAbility.Klutz) && (battlers[i].getItem() != HoldItem.Pheremone)) {
                                                            battlers[i].setStatus(new MonsterStatusEffect(stat, Integer.valueOf((stat == MonsterStatus.ÖÐ¶¾) || (stat == MonsterStatus.ÁÒÑæÅçÉä) ? (int) (battlers[i].calcHP() / 16L) : 1), MonsterStatusEffect.genericSkill(stat), null, false));
                                                            map.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append(battlers[i].getName()).append(" was inflicted with ").append(StringUtil.makeEnumHumanReadable(stat.name())).append(" status!").toString()));
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        label5111:
                                        if (basedamage != 0L) {
                                            if (battlers[1] == null) {
                                                return;
                                            }
                                            if (basedamage > 1L) {
                                                final byte skillByte2 = (byte) (14 + battlers[1].getMonster().getFacingDirection());
                                                AbsoluteLifeMovement alm2 = new AbsoluteLifeMovement(0, battlers[1].getMonster().getPosition(), 0, battlers[1].getMonster().getStance());
                                                alm2.defaulted();
                                                moves.add(alm2);
                                                map.broadcastMessage(MobPacket.moveMonster(true, skillByte2, 0, 0, 0, battlers[1].getMonster().getObjectId(), battlers[1].getMonster().getTruePosition(), moves));
                                            }
                                            if (battlers[1].getAbility() != PokemonAbility.DrySkin) {
                                                if (battlers[1].getAbility() != PokemonAbility.WaterAbsorb);
                                            } else if (element == PokemonElement.Fish) {
                                                if ((battlers[1].getAbility() != PokemonAbility.VoltAbsorb) || (element != PokemonElement.Lightning)) {
                                                    if ((battlers[1].getAbility() != PokemonAbility.SapSipper) || (element != PokemonElement.Plant));
                                                } else {
                                                    int percentHP = (int) Math.min(100L, basedamage * 100L / battlers[0].calcHP());
                                                    if (percentHP > 0) {
                                                        if (battlers[0].calcHP() > 800L) {
                                                            map.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append(battlers[1].getName()).append(" absorbed ").append(percentHP * battlers[1].calcHP() / 800L).append(" HP!").toString()));
                                                            battlers[0].damage((int) (-(percentHP * battlers[1].calcHP() / 800L)), map, 0, false);
                                                            updateHP(i == 0 ? 1 : 0);
                                                        }
                                                    }
                                                }
                                            }
                                            if ((battlers[1].getAbility() == PokemonAbility.DrySkin) && (element == PokemonElement.Fire)) {
                                                basedamage *= 2L;
                                            } else {
                                                if (battlers[1].getAbility() != PokemonAbility.ThickFat) {
                                                    if (battlers[1].getAbility() != PokemonAbility.Heatproof);
                                                } else if (element == PokemonElement.Fire) {
                                                    basedamage /= 2L;
                                                }
                                                if ((battlers[1].getAbility() == PokemonAbility.ThickFat) && (element == PokemonElement.Ice)) {
                                                    basedamage /= 2L;
                                                } else {
                                                    if (battlers[1].getAbility() == PokemonAbility.Multiscale) {
                                                        if (battlers[1].getHPPercent() >= 100) {
                                                            basedamage /= 2L;
                                                        }
                                                    }
                                                    if (battlers[1].getAbility() == PokemonAbility.WeakArmor) {
                                                        battlers[1].setMod(PokemonStat.SPEED, battlers[1].increaseMod(battlers[1].getMod(PokemonStat.SPEED)));
                                                        battlers[1].setMod(PokemonStat.DEF, battlers[1].decreaseMod(battlers[1].getMod(PokemonStat.DEF)));
                                                    } else if ((battlers[1].getAbility() == PokemonAbility.MotorDrive) && (element == PokemonElement.Lightning)) {
                                                        battlers[1].setMod(PokemonStat.SPEED, battlers[1].increaseMod(battlers[1].getMod(PokemonStat.SPEED)));
                                                    }
                                                }
                                            }
                                            map.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append(battlers[1].getName()).append(" received ").append(basedamage).append(" damage!").toString()));
                                            battlers[1].damage((int) basedamage, map, battlers[i].getMonsterId(), false);
                                            updateHP(i == 1 ? 0 : 1);
                                        }

                                        label5551:
                                        label6507:
                                        if (battlers[1].getCurrentHP() <= 0L) {
                                            if ((battlers[i] != null) && (battlers[i].getAbility() == PokemonAbility.Moxie)) {
                                                battlers[i].setMod(PokemonStat.ATK, battlers[i].increaseMod(battlers[i].getMod(PokemonStat.ATK)));
                                                battlers[i].setMod(PokemonStat.SPATK, battlers[i].increaseMod(battlers[i].getMod(PokemonStat.SPATK)));
                                            }
                                            MapleCharacter ch = map.getCharacterById(characterIds[i]);
                                            Iterator i$;
                                            if ((ch != null) && (instanceid >= 0)) {
                                                for (Battler b : ch.getBattlers()) {
                                                    if ((b != null) && (b.getItem() == HoldItem.Maha_Charm)) {
                                                        battlers[1].addMonsterId(b.getMonsterId());
                                                    }
                                                }
                                                for (i$ = battlers[1].getDamaged().iterator(); i$.hasNext();) {
                                                    int z = ((Integer) i$.next()).intValue();
                                                    for (Battler b : ch.getBattlers()) {
                                                        if ((b != null) && (b.getMonsterId() == z)) {
                                                            int oLevel = b.getTrueLevel();
                                                            String oName = b.getStats().getName();
                                                            int xx = battlers[1].getExp(npcTeam != null, z);
                                                            b.gainExp(xx, ch);
                                                            ch.dropMessage(-6, new StringBuilder().append(b.getName()).append(" gained ").append(xx).append(" EXP.").toString());
                                                            ch.changedBattler();
                                                            if (b.getTrueLevel() > oLevel) {
                                                                ch.dropMessage(-6, new StringBuilder().append(b.getName()).append(" leveled up to level ").append(b.getTrueLevel()).append("!").toString());
                                                                if (!b.getStats().getName().equals(oName)) {
                                                                    ch.getClient().getSession().write(MaplePacketCreator.playSound("5th_Maple/prize"));
                                                                    ch.dropMessage(-6, new StringBuilder().append(b.getName()).append(" evolved from a ").append(oName).append(" to a ").append(b.getStats().getName()).append("!!!").toString());
                                                                    if (b.getMonster() != null) {
                                                                        Point pos = b.getMonster().getPosition();
                                                                        boolean left = !b.getMonster().isFacingLeft();
                                                                        map.killMonster(b.getMonster());
                                                                        spawnMonster(pos, left, true, b);
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            MapleMonsterStats mons = battlers[1].getStats();
                                            map.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append(battlers[1].getName()).append(" fainted!").toString()));
                                            if (battlers[1].getAbility() == PokemonAbility.Aftermath) {
                                                battlers[i].damage((int) battlers[i].calcHP() / 4, map, 0, true);
                                                updateHP(i);
                                                map.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append(battlers[i].getName()).append(" was hurt ").append(battlers[i].calcHP() / 4L).append(" damage by Aftermath!").toString()));
                                            }
                                            map.killMonster(battlers[1].getMonster());
                                            if (battlers[i] != null) {
                                                battlers[i].removeMonsterId(battlers[1].getMonsterId());
                                            }
                                            battlers[1].wipe();
                                            battlers[(i == 1 ? 0 : 1)] = null;
                                            if ((instanceid >= 0) && (i == 0) && (npcTeam == null)) {
                                                MapleCharacter chr = map.getCharacterById(characterIds[0]);
                                                chr.dropMessage(-6, "The wild monster fainted.");
                                                if (Randomizer.nextInt(5) == 0) {
                                                    chr.modifyCSPoints(1, (int) ((Randomizer.nextInt(chr.getClient().getChannelServer().getCashRate()) + chr.getClient().getChannelServer().getCashRate() + mons.getExp() / 1000 + mons.getHp() / 20000L) * (chr.getStat().cashBuff / 100.0D) * chr.getCashMod()), true);
                                                }
                                                chr.getClient().getSession().write(MaplePacketCreator.playSound("Romio/discovery"));
                                                dispose(chr, false);
                                                return;
                                            }
                                            if ((instanceid >= 0) && (i == 0) && (npcTeam != null)) {
                                                if (npcTeam.size() > 0) {
                                                    turn[i] = Turn.DISABLED;
                                                    turn[(i == 1 ? 0 : 1)] = Turn.SWITCH;
                                                    attacks = new String[2];
                                                    switches = new Battler[2];
                                                    switches[(i == 1 ? 0 : 1)] = new Battler(MapleLifeFactory.getMonsterStats(((Integer) npcTeam.get(0)).intValue()));
                                                    npcTeam.remove(0);
                                                } else {
                                                    MapleCharacter chrr = map.getCharacterById(characterIds[i]);
                                                    map.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append(chrr.getName()).append(" won the match.").toString()));
                                                    giveReward(chrr);
                                                    forfeit(chrr, true);
                                                }
                                                return;
                                            }
                                            MapleCharacter chrr = map.getCharacterById(characterIds[0]);
                                            boolean cont = false;
                                            for (Battler b : chrr.getBattlers()) {
                                                if ((b != null) && (b.getCurrentHP() > 0L)) {
                                                    cont = true;
                                                    break;
                                                }
                                            }
                                            turn[i] = Turn.DISABLED;
                                            turn[(i == 1 ? 0 : 1)] = (cont ? null : Turn.DISABLED);
                                            if (!cont) {
                                                playerWin(i);
                                                map.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append(chrr.getName()).append(" lost the match.").toString()));
                                                forfeit(chrr, true);
                                                return;
                                            }
                                        } else {
                                            if ((battlers[i].getAbility() != PokemonAbility.Klutz) && (battlers[i].getAbility() != PokemonAbility.Unnerve)) {
                                                if (battlers[0].getItem() != null) {
                                                    if (battlers[0].getHPPercent() < (battlers[0].getAbility() == PokemonAbility.Gluttony ? 75 : 50)) {
                                                        boolean usedItem = battlers[0].getAbility() == PokemonAbility.Unburden;
                                                        switch (battlers[0].getItem()) {
                                                            case Red_Candy:
                                                                map.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append(battlers[1].getName()).append("'s attack increased from its item!").toString()));
                                                                battlers[1].setMod(PokemonStat.ATK, battlers[1].increaseMod(battlers[1].getMod(PokemonStat.ATK)));
                                                                battlers[1].setMod(PokemonStat.SPATK, battlers[1].increaseMod(battlers[1].getMod(PokemonStat.SPATK)));
                                                                battlers[0].setItem(0);
                                                                break;
                                                            case Blue_Candy:
                                                                map.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append(battlers[1].getName()).append("'s defense increased from its item!").toString()));
                                                                battlers[1].setMod(PokemonStat.DEF, battlers[1].increaseMod(battlers[1].getMod(PokemonStat.DEF)));
                                                                battlers[1].setMod(PokemonStat.SPDEF, battlers[1].increaseMod(battlers[1].getMod(PokemonStat.SPDEF)));
                                                                battlers[0].setItem(0);
                                                                break;
                                                            case Green_Candy:
                                                                map.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append(battlers[1].getName()).append("'s speed increased from its item!").toString()));
                                                                battlers[1].setMod(PokemonStat.SPEED, battlers[1].increaseMod(battlers[1].getMod(PokemonStat.SPEED)));
                                                                battlers[1].setMod(PokemonStat.EVA, battlers[1].increaseMod(battlers[1].getMod(PokemonStat.EVA)));
                                                                battlers[1].setMod(PokemonStat.ACC, battlers[1].increaseMod(battlers[1].getMod(PokemonStat.ACC)));
                                                                battlers[0].setItem(0);
                                                                break;
                                                            case Strawberry:
                                                                map.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append(battlers[1].getName()).append("'s HP healed from its item!").toString()));
                                                                battlers[0].damage((int) (-(battlers[i].calcHP() / 10L)), map, 0, false);
                                                                updateHP(i == 0 ? 1 : 0);
                                                                battlers[0].setItem(0);
                                                                break;
                                                            default:
                                                                usedItem = false;
                                                        }

                                                        if (usedItem) {
                                                            battlers[i].setMod(PokemonStat.SPEED, battlers[i].increaseMod(battlers[i].getMod(PokemonStat.SPEED)));
                                                        }
                                                    }
                                                }
                                            }
                                            if (battlers[i].getAbility() == PokemonAbility.SpeedBoost) {
                                                battlers[i].setMod(PokemonStat.SPEED, battlers[i].increaseMod(battlers[i].getMod(PokemonStat.SPEED)));
                                            } else if (battlers[i].getAbility() == PokemonAbility.Moody) {
                                                PokemonStat down = PokemonStat.getRandom();
                                                PokemonStat up = PokemonStat.getRandom();
                                                for (int x = 0; x < 2; x++) {
                                                    battlers[i].setMod(up, battlers[i].increaseMod(battlers[i].getMod(up)));
                                                    battlers[i].setMod(down, battlers[i].decreaseMod(battlers[i].getMod(down)));
                                                }
                                                map.broadcastMessage(MaplePacketCreator.serverNotice(6, new StringBuilder().append(battlers[i].getName()).append("'s Moody increased the ").append(StringUtil.makeEnumHumanReadable(up.name())).append(" stat and decreased the ").append(StringUtil.makeEnumHumanReadable(down.name())).append(" stat!").toString()));
                                            }
                                        }
                                    }
                                }, PokemonBattle.WALK_TIME);
                        }
                    }
                }, timeFirst);

                timeFirst += WALK_TIME * 2 + WALK_TIME / 8;
            }
        }
        if (((this.turn[0] == Turn.DISABLED) || (this.turn[1] == Turn.DISABLED) || (this.disposed)) && (this.turn[0] != Turn.SWITCH) && (this.turn[1] != Turn.SWITCH) && (this.npcTeam == null)) {
            return;
        }
        Timer.EtcTimer.getInstance().schedule(new Runnable() {

            public void run() {
                if (((turn[0] == Turn.DISABLED) || (turn[1] == Turn.DISABLED) || (disposed)) && (turn[0] != Turn.SWITCH) && ((turn[1] != Turn.SWITCH) || ((npcTeam != null) && (battlers[1] == null)))) {
                    if ((npcTeam != null) && (turn[1] == Turn.SWITCH)) {
                        makeTurn();
                    }
                    return;
                }
                boolean[] truant = {false, false};
                switches = new Battler[2];
                attacks = new String[2];
                if (!disposed) {
                    for (int i = 0; i < characterIds.length; i++) {
                        updateHP(i);
                        if ((battlers[i] != null) && (battlers[i].getAbility() == PokemonAbility.Truant) && (turn[i] == Turn.ATTACK)) {
                            truant[i] = true;
                        }
                    }
                }
                turn = new Turn[]{null, null};
                for (int i = 0; i < truant.length; i++) {
                    if (truant[i]) {
                        turn[i] = Turn.TRUANT;
                    }
                }
                if ((!disposed) && (instanceid >= 0) && (turn[1] != Turn.TRUANT)) {
                    turn[1] = makeRandomTurn();
                }
            }
        }, timeFirst - WALK_TIME / 8);
    }

    public boolean isTrainerBattle() {
        return this.npcTeam != null;
    }

    public void playerWin(int i) {
        int[] averageLevel = {0, 0};
        int[] numBattlers = {0, 0};
        for (int x = 0; x < 2; x++) {
            MapleCharacter pro = this.map.getCharacterById(this.characterIds[x]);
            if (pro == null) {
                return;
            }
            for (Battler b : pro.getBattlers()) {
                if (b != null) {
                    averageLevel[x] += b.getLevel();
                    numBattlers[x] += 1;
                }
            }
            averageLevel[x] /= numBattlers[x];
        }
        MapleCharacter winner = this.map.getCharacterById(this.characterIds[i]);
        MapleCharacter loser = this.map.getCharacterById(this.characterIds[0]);
        if ((Math.abs(averageLevel[0] - averageLevel[1]) > 20) || (numBattlers[0] != numBattlers[1])) {
            winner.dropMessage(-6, "The battle did not count as a win due to the ease.");
        } else if (!winner.canBattle(loser)) {
            winner.dropMessage(-6, "The battle did not count as a win due to you battling the character in this month.");
        } else {
            winner.hasBattled(loser);
            winner.increaseTotalWins();
            loser.increaseTotalLosses();
            int theWins = winner.getIntNoRecord(122400) + 1;
            int theWins2 = Math.max(0, loser.getIntNoRecord(122400) - 1);
            winner.getQuestNAdd(MapleQuest.getInstance(122400)).setCustomData(String.valueOf(theWins));
            loser.getQuestNAdd(MapleQuest.getInstance(122400)).setCustomData(String.valueOf(theWins2));
            winner.dropMessage(-6, "You have gained a win on your record! Total Wins: " + winner.getTotalWins() + ", Current Wins: " + theWins);
            loser.dropMessage(-6, "You have gained a loss on your record... Total Losses: " + loser.getTotalLosses() + ", Current Wins: " + theWins2);
        }
    }
}