package constants;

import client.Battler;
import java.awt.Point;
import java.util.*;
import server.life.Element;
import server.life.MapleLifeFactory;
import server.life.MapleMonsterStats;
import tools.Pair;

public class BattleConstants {

    private static List<PokedexEntry> pokedexEntries = new ArrayList<PokedexEntry>();
    private static List<Integer> gmMobs = new ArrayList<Integer>();
    private static EnumMap<PokemonMap, LinkedList<Pair<Integer, Integer>>> mapsToMobs = new EnumMap<PokemonMap, LinkedList<Pair<Integer, Integer>>>(PokemonMap.class);

    public static Point getNPCPos(int mapid) {
        switch (mapid) {
            case 925020010:
            case 925020011:
            case 925020012:
            case 925020013:
            case 925020014:
                return new Point(252, 2);
            case 980010100:
            case 980010200:
            case 980010300:
                return new Point(142, 91);
            case 980010020:
                return new Point(190, 205);
            case 980010000:
                return new Point(447, 89);
        }
        return null;
    }

    public static boolean isBattleMap(int mapid) {
        return getMap(mapid) != null;
    }

    public static PokemonMap getMap(int mapid) {
        for (PokemonMap map : PokemonMap.values()) {
            if (map.id == mapid) {
                return map;
            }
        }
        return null;
    }

    public static void init() {
        Map<Integer, List<Pair<Integer, Integer>>> mobsToMaps = new HashMap<Integer, List<Pair<Integer, Integer>>>();
        for (PokemonMap map : PokemonMap.values()) {
            LinkedList<Integer> set_check = new LinkedList<Integer>();
            LinkedList<Pair<Integer, Integer>> set = new LinkedList<Pair<Integer, Integer>>();
            for (PokemonMob mob : PokemonMob.values()) {
                for (int i = 0; i < mob.evolutions.size(); i++) {
                    final int id = mob.evolutions.get(i);
                    final MapleMonsterStats mons = MapleLifeFactory.getMonsterStats(id);
                    if (mons == null) {
                        System.out.println("WARNING: monster " + id + " does not exist.");
                    } else if (((id == 6400007) || (!mons.isBoss())) && (mons.getLevel() >= map.minLevel) && (mons.getLevel() <= map.maxLevel) && (!set_check.contains(Integer.valueOf(id))) && (canAdd(id))) {
                        set.add(new Pair<Integer, Integer>(id, i + 1));
                        set_check.add(id);
                        List mtm = (List) mobsToMaps.get(Integer.valueOf(id));
                        if (mtm == null) {
                            mtm = new ArrayList<Pair<Integer, Integer>>();
                            mobsToMaps.put(id, mtm);
                        }
                        mtm.add(new Pair<Integer, Integer>(map.id, i + 1));
                    }
                }
            }
            set_check.clear();
            mapsToMobs.put(map, set);
        }
        LinkedHashMap<Integer, PokedexEntry> pokedex = new LinkedHashMap<Integer, PokedexEntry>();

        int pokedexNum = 1;
        for (PokemonMob mob : PokemonMob.values()) {
            for (int i = 0; i < mob.evolutions.size(); i++) {
                int id = ((Integer) mob.evolutions.get(i)).intValue();
                if (!pokedex.containsKey(Integer.valueOf(id))) {
                    PokedexEntry pe = new PokedexEntry(id, pokedexNum);
                    List<Pair<Integer, Integer>> mtm = mobsToMaps.get(id);
                    if (mtm != null) {
                        pe.maps = new ArrayList();
                        for (Pair mt : mtm) {
                            pe.maps.add(new Pair(mt.left, Integer.valueOf((int) Math.round(1.0D / ((LinkedList) mapsToMobs.get(getMap(((Integer) mt.left).intValue()))).size() / ((Integer) mt.right).intValue() * 10000.0D))));
                        }
                    } else {
                        pe.maps = null;
                    }
                    pe.dummyBattler = new Battler(MapleLifeFactory.getMonsterStats(id));
                    pe.dummyBattler.resetNature();
                    pe.dummyBattler.clearIV();
                    if ((pe.dummyBattler.getStats().isBoss()) && (mob.type == MobExp.EASY)) {
                        gmMobs.add(Integer.valueOf(id));
                    } else {
                        if (i != 0) {
                            MapleMonsterStats mm = MapleLifeFactory.getMonsterStats(mob.evolutions.get(i - 1));
                            if (mm != null) {
                                if ((mob.evoItem != null) && (i == mob.evolutions.size() - 1)) {
                                    pe.pre.put(mob.evolutions.get(i - 1), mob.evoItem.id);
                                } else {
                                    pe.pre.put(mob.evolutions.get(i - 1), pe.dummyBattler.getLevel());
                                }
                            }
                        }

                        if (i != mob.evolutions.size() - 1) {
                            MapleMonsterStats mm = MapleLifeFactory.getMonsterStats(((Integer) mob.evolutions.get(i + 1)).intValue());
                            if (mm != null) {
                                if ((mob.evoItem != null) && (i == mob.evolutions.size() - 2)) {
                                    pe.evo.put(mob.evolutions.get(i + 1), mob.evoItem.id);
                                } else {
                                    pe.evo.put(mob.evolutions.get(i + 1), (int) mm.getLevel());
                                }
                            }
                        }
                        pokedex.put(id, pe);
                    }
                } else {
                    PokedexEntry pe = (PokedexEntry) pokedex.get(id);
                    if ((i != 0) && (!pe.pre.containsKey(mob.evolutions.get(i - 1)))) {
                        MapleMonsterStats mm = MapleLifeFactory.getMonsterStats(mob.evolutions.get(i - 1));
                        if (mm != null) {
                            if ((mob.evoItem != null) && (i == mob.evolutions.size() - 1)) {
                                pe.pre.put(mob.evolutions.get(i - 1), mob.evoItem.id);
                            } else {
                                pe.pre.put(mob.evolutions.get(i - 1), pe.dummyBattler.getLevel());
                            }
                        }
                    }
                    if ((i != mob.evolutions.size() - 1) && (!pe.evo.containsKey(mob.evolutions.get(i + 1)))) {
                        MapleMonsterStats mm = MapleLifeFactory.getMonsterStats(mob.evolutions.get(i + 1));
                        if (mm != null) {
                            if ((mob.evoItem != null) && (i == mob.evolutions.size() - 2)) {
                                pe.evo.put(mob.evolutions.get(i + 1), mob.evoItem.id);
                            } else {
                                pe.evo.put(mob.evolutions.get(i + 1), (int) mm.getLevel());
                            }
                        }
                    }
                }
            }
        }
        pokedexEntries.addAll(pokedex.values());
        mobsToMaps.clear();
        pokedex.clear();
    }

    public static boolean isGMMob(int idd) {
        return gmMobs.contains(idd);
    }

    public static boolean canAdd(int id) {
        switch (id) {
            case 5100001:
            case 5130106:
                return false;
        }
        return true;
    }

    public static LinkedList<Pair<Integer, Integer>> getMobs(PokemonMap mapp) {
        return mapsToMobs.get(mapp);
    }

    public static List<PokedexEntry> getAllPokedex() {
        return pokedexEntries;
    }

    public static byte getGender(PokemonMob mob) {
        switch (mob) {
            case Bunny:
            case Maverick_Y:
            case Maverick_B:
            case Maverick_V:
            case Maverick_S:
            case Guard:
            case Lord:
            case Roid:
            //case Frankenroid:
            //case Roi:
            //case Mutae:
            //case Rumo:
            case Robot:
            case Robo:
            case Block:
            case Block_Golem:
            case CD:
            case Mannequin:
            case Ninja:
            case Training_Robot:
            case Veil:
            case Veil_2:
            case Veil_3:
            case Veil_4:
            case Veil_5:
            case Unveil:
            case Cake:
            case Egg:
            case Accessory:
            case Clown:
            case Fire_Sentinel:
            case Ice_Sentinel:
            //case Warrior:
            //case Mage:
            //case Mage_1:
            //case Bowman:
            //case Rogue:
            case Rogue_1:
            //case Pirate:
            case Jar:
            case Vehicle:
            //case Dummy_Strong:
            //case Dummy:
            case Keeper:
            case Keeper_2:
                return 0;
            case Dragon:
            case Human_M:
            case Boss_M:
            case Viking:
            case Bird:
            case Black_Bird:
            case Red_Bird:
            case Blue_Bird:
            case Manon:
            case Griffey:
            case Mask:
            case Crow:
                return 1;
            case Kyrin_1:
            case Kyrin_2:
            case Human_F:
            case Witch:
            case Monkey:
            case White_Monkey:
            case Fairy:
            case Doll_V:
            case Doll_H:
            case Road_Auf:
            case Road_Dunas:
            case Cygnus_Boss:
                return 2;
        }
        return -1;
    }

    public static long getPokemonCustomHP(int mobId, long def) {
        switch (mobId) {
            case 8840000:
            case 9400112:
            case 9400113:
                return def / 200L;
            case 8300006:
            case 8300007:
            case 8850000:
            case 8850001:
            case 8850002:
            case 8850003:
            case 8850004:
            case 9400589:
            case 9400590:
            case 9400591:
            case 9400592:
            case 9400593:
            case 9420543:
            case 9420548:
                return def / 100L;
            case 9300158:
            case 9300159:
            case 9400300:
            case 9420544:
            case 9420549:
                return def / 50L;
            case 9400293:
                return def / 40L;
            case 8840003:
            case 8840004:
            case 9300215:
            case 9400121:
            case 9400405:
                return def / 20L;
            case 8210000:
            case 8210001:
            case 8210002:
            case 8210003:
            case 8210004:
            case 8210005:
            case 8210010:
            case 8210011:
            case 8210012:
            case 8600000:
            case 8600001:
            case 8600002:
            case 8600003:
            case 8600004:
            case 8600005:
            case 8600006:
            case 8610000:
            case 8610001:
            case 8610002:
            case 8610003:
            case 8610004:
            case 8610005:
            case 8610006:
            case 8610007:
            case 8610008:
            case 8610009:
            case 8610010:
            case 8610011:
            case 8610012:
            case 8610013:
            case 8610014:
            case 8840001:
            case 8840002:
            case 8840005:
            case 9001010:
            case 9400014:
                return def / 10L;
            case 6090000:
                return 1350000L;
            case 8220004:
                return 2350000L;
            case 8220005:
                return 3200000L;
            case 8220006:
                return 4100000L;
        }
        return def;
    }

    public static enum PokemonAbility {

        Adaptability("Powers moves of the same type"),
        Aftermath("Damages the foe when KOed"),
        Analytic("Powers moves when moving last"),
        AngerPoint("Raises Attack/Sp.Attack when taking a critical hit"),
        BadDreams("Hurts a foe if they are in darkness"),
        BattleArmor("Protects from critical attacks"),
        BigPecks("Protects Defense from lowering"),
        Blaze("Powers up fire type moves in a pinch"),
        ClearBody("Prevents stats from lowering"),
        Compoundeyes("Accuracy is increased"),
        Contrary("Inverts stat modifiers"),
        Defeatist("Halves Attack/Sp.Attack when below 50% HP"),
        Defiant("Raises Attack two stages when any stat is lowered"),
        DrySkin("Fire type moves are more effective, Fish type moves heal HP"),
        EarlyBird("Awakens quickly from darkness"),
        EffectSpore("Contact may paralyze, poison, or cause darkness"),
        Filter("Powers down super effective moves"),
        FlameBody("Contact may burn"),
        FlareBoost("Increases Sp.Attack to 1.5x when burned."),
        Forewarn("Tells of the opponent's type"),
        Frisk("Tells of the opponent's held item"),
        Gluttony("Uses one time items earlier"),
        Guts("Boosts Attack if there is a status problem"),
        Heatproof("Halves fire type moves effect"),
        HugePower("Doubled Attack stat"),
        Hustle("Doubled Attack stat, with lower accuracy"),
        HyperCutter("Prevents Attack from being lowered"),
        Illuminate("Raises likelihood of finding wild pokemon"),
        Immunity("Prevents poison"),
        Insomnia("Prevents darkness"),
        Intimidate("Lowers opponent's Attack"),
        IronBarbs("Damages opponent 1/8 HP on contact"),
        Klutz("Opponent can't use any held items"),
        Limber("Prevents paralysis"),
        LiquidOoze("Hurts foes when they try to absorb HP"),
        MagicGuard("Only hurt by attacks"),
        MagmaArmor("Prevents freezing"),
        MarvelScale("Boosts Defense when there is status"),
        Moody("Raises random stat two stages, lowers another"),
        MotorDrive("Raises Speed when hit by electricity"),
        Moxie("Raises Attack when KOing a monster"),
        Multiscale("When full HP, halves damage taken"),
        NaturalCure("All status healed when switching out"),
        NoGuard("Ensures hit"),
        Normalize("All moves become Normal type"),
        Overgrow("Powers up Plant in a pinch"),
        OwnTempo("Prevents Showdown status"),
        Pickpocket("Steals opponent's held item on contact"),
        PoisonHeal("Heals HP when poisoned"),
        PoisonPoint("Poisons foe on contact"),
        PoisonTouch("Poisons foe on attack, 20% chance"),
        PurePower("Raises power of Physical moves"),
        QuickFeet("Raises Speed in status problem"),
        Regenerator("Heals 1/3 HP when switching"),
        RunAway("Ensures escape from wild monsters"),
        SapSipper("Absorbs Plant moves"),
        Scrappy("All immunities do not apply"),
        SereneGrace("Boosts added effects"),
        ShadowTag("Prevents escape"),
        ShedSkin("Has a greater chance to heal status problems"),
        SheerForce("Increases power, but prevents extra effects"),
        ShieldDust("Blocks extra effects"),
        Sniper("Increased power of critical hits"),
        SpeedBoost("Increases Speed every turn"),
        Stall("Moves last"),
        Static("Paralysis on contact"),
        Stench("Lower chance of meeting wild monsters"),
        SuperLuck("Increased critical hit rate"),
        Synchronize("Opponent receives same status"),
        TangledFeet("Raises evasion when confused"),
        ThickFat("Resists Fire and Ice moves"),
        TintedLens("Powers up not very effective moves"),
        Torrent("Powers up Fish type moves in a pinch"),
        ToxicBoost("Attack 1.5x when poisoned"),
        Truant("Does nothing every second turn"),
        Unaware("Ignores stat changes by the foe"),
        Unburden("Raises Speed if any held item is used"),
        Unnerve("Prevents opposition from using one time use items"),
        VoltAbsorb("Heals HP when hit by electricity"),
        WaterAbsorb("Heals HP when hit by water"),
        WaterVeil("Prevents burning"),
        WeakArmor("Raises Speed, lowers Defense when hit"),
        WonderGuard("Only super effective moves hit");
        public String desc;

        private PokemonAbility(String desc) {
            this.desc = desc;
        }
    }

    public static enum PokemonElement {

        None(0, false, new int[0], new int[0], new int[0]),
        Fire(-1, true, new int[0], new int[]{-1, -5, -9, -10, -12}, new int[]{-2, -6, -8, -11, -14}),
        Ice(-2, true, new int[]{-12}, new int[]{-1, -2, -4, -9, -10}, new int[]{-7, -8, -13, -14}),
        Lightning(-3, true, new int[0], new int[]{-2, -5, -8, -11, -13}, new int[]{-6, -7, -9, -14}),
        Poison(-4, true, new int[]{-11, -14}, new int[]{-1, -2, -4, -6, -12}, new int[]{-3, -5, -7, -8, -9, -10}),
        Holy(-5, true, new int[]{-4}, new int[]{-5, -11}, new int[]{-6, -12, -13}),
        Dark(-6, true, new int[]{-5}, new int[]{-6, -9}, new int[]{-3, -7, -11}),
        Mammal(-7, false, new int[0], new int[]{-4, -10, -11}, new int[]{-8, -9, -12}),
        Plant(-8, false, new int[0], new int[]{-1, -4, -7, -8}, new int[]{-5, -6, -9, -14}),
        Fish(-9, false, new int[0], new int[]{-7, -9, -10}, new int[]{-1, -2, -8}),
        Reptile(-10, false, new int[0], new int[]{-4, -6, -7, -11, -13}, new int[]{-1, -3, -9}),
        Spirit(-11, true, new int[]{-15}, new int[]{-1, -2, -3, -5, -6, -7}, new int[]{-4, -11, -12}),
        Devil(-12, true, new int[0], new int[]{-8, -9, -10, -12, -14}, new int[]{-1, -2, -4, -5, -6}),
        Immortal(-13, false, new int[]{-13}, new int[]{-2, -7, -9, -10}, new int[]{-1, -4, -11, -12, -15}),
        Enchanted(-14, false, new int[0], new int[]{-2, -3, -5, -6, -8, -14}, new int[]{-4, -7, -13, -15}),
        Normal(-15, false, new int[]{-11}, new int[]{-13}, new int[0]);
        public int trueId;
        public boolean special;
        public Set<Integer> immune = new HashSet();
        public Set<Integer> notEffective = new HashSet();
        public Set<Integer> superEffective = new HashSet();

        private PokemonElement(int trueId, boolean special, int[] immune, int[] notEffective, int[] superEffective) {
            this.special = special;
            this.trueId = trueId;
            for (int e : immune) {
                this.immune.add(Integer.valueOf(e));
            }
            for (int e : notEffective) {
                this.notEffective.add(Integer.valueOf(e));
            }
            for (int e : superEffective) {
                this.superEffective.add(Integer.valueOf(e));
            }
        }

        public static PokemonElement getFromElement(Element c) {
            switch (c) {
                case FIRE:
                    return Fire;
                case ICE:
                    return Ice;
                case LIGHTING:
                    return Lightning;
                case POISON:
                    return Poison;
                case HOLY:
                    return Holy;
                case DARKNESS:
                    return Dark;
            }
            return None;
        }

        public static PokemonElement getById(int c) {
            switch (c) {
                case 1:
                    return Mammal;
                case 2:
                    return Plant;
                case 3:
                    return Fish;
                case 4:
                    return Reptile;
                case 5:
                    return Spirit;
                case 6:
                    return Devil;
                case 7:
                    return Immortal;
                case 8:
                    return Enchanted;
            }
            return None;
        }

        public double getEffectiveness(PokemonElement[] stats) {
            double ret = 1.0D;
            for (int i = 0; i < stats.length; i++) {
                if (this.immune.contains(Integer.valueOf(stats[i].trueId))) {
                    return 0.0D;
                }
                if (this.notEffective.contains(Integer.valueOf(stats[i].trueId))) {
                    ret /= 2.0D;
                } else if (this.superEffective.contains(Integer.valueOf(stats[i].trueId))) {
                    ret *= 2.0D;
                }
            }
            return ret;
        }
    }

    public static enum PokemonNature {

        Bashful(PokemonStat.NONE, PokemonStat.NONE),
        Docile(PokemonStat.NONE, PokemonStat.NONE),
        Hardy(PokemonStat.NONE, PokemonStat.NONE),
        Quirky(PokemonStat.NONE, PokemonStat.NONE),
        Serious(PokemonStat.NONE, PokemonStat.NONE),
        Lonely(PokemonStat.ATK, PokemonStat.DEF),
        Adamant(PokemonStat.ATK, PokemonStat.SPATK),
        Naughty(PokemonStat.ATK, PokemonStat.SPDEF),
        Brave(PokemonStat.ATK, PokemonStat.SPEED),
        Bold(PokemonStat.DEF, PokemonStat.ATK),
        Impish(PokemonStat.DEF, PokemonStat.SPATK),
        Lax(PokemonStat.DEF, PokemonStat.SPDEF),
        Relaxed(PokemonStat.DEF, PokemonStat.SPEED),
        Modest(PokemonStat.SPATK, PokemonStat.ATK),
        Mild(PokemonStat.SPATK, PokemonStat.DEF),
        Rash(PokemonStat.SPATK, PokemonStat.SPDEF),
        Quiet(PokemonStat.SPATK, PokemonStat.SPEED),
        Calm(PokemonStat.SPDEF, PokemonStat.ATK),
        Gentle(PokemonStat.SPDEF, PokemonStat.DEF),
        Careful(PokemonStat.SPDEF, PokemonStat.SPATK),
        Sassy(PokemonStat.SPDEF, PokemonStat.SPEED),
        Timid(PokemonStat.SPEED, PokemonStat.ATK),
        Hasty(PokemonStat.SPEED, PokemonStat.DEF),
        Jolly(PokemonStat.SPEED, PokemonStat.SPATK),
        Naive(PokemonStat.SPEED, PokemonStat.SPDEF);
        public PokemonStat inc;
        public PokemonStat dec;

        private PokemonNature(PokemonStat inc, PokemonStat dec) {
            this.inc = inc;
            this.dec = dec;
        }

        public static PokemonNature randomNature() {
            return values()[server.Randomizer.nextInt(values().length)];
        }
    }

    public static enum PokemonStat {

        ATK, DEF, SPATK, SPDEF, SPEED, EVA, ACC, HP, NONE;

        public static PokemonStat getRandom() {
            return values()[server.Randomizer.nextInt(values().length - 1)];
        }
    }

    public static enum Turn {

        ATTACK, SWITCH, DISABLED, TRUANT;
    }

    public static enum PokemonItem
            implements PItem {

        Basic_Ball(3992017, 1.0D),
        Great_Ball(3992018, 1.5D),
        Ultra_Ball(3992019, 2.0D),
        Yellow_Crystal(4001268, 2.0D),
        Blue_Crystal(4001269, 2.0D),
        Saint_Stone(4020012, 1.0D),
        Ice_Pick(4310007, 2.0D),
        Bright_Feather(3994193, 1.0D),
        Gold_Pick(4310001, 0.5D),
        Coin(4310002, 1.0D),
        Corrupted(4001237, 3.0D),
        More_Corrupted(4001238, 2.0D),
        Most_Corrupted(4001239, 1.0D),
        Corrupted_Item(4001240, 2.0D),
        Ancient_Relic(4001302, 2.0D),
        Heart_of_Heart(4001244, 2.0D),
        Phoenix_Egg(4001113, 1.0D),
        Freezer_Egg(4001114, 1.0D),
        Black_Hole(4001190, 0.5D),
        Maple_Marble(4031456, 0.5D),
        Rainbow_Leaf(4032733, 0.5D),
        Intelligence_Document(4001192, 2.0D),
        Dragon_Heart(4031449, 1.0D),
        Griffey_Wind(4031457, 1.0D),
        Deathly_Fear(4031448, 0.5D),
        Summoning_Frame(4031451, 1.0D),
        Magical_Array(4031453, 0.5D),
        Life_Root(4031461, 1.0D),
        Black_Tornado(4031458, 1.0D),
        Perfect_Pitch(4310000, 1.0D),
        Cold_Heart(4031460, 1.0D),
        Ventilation(4031462, 2.0D),
        Old_Glove(4031465, 1.0D),
        Pocket_Watch(4001393, 1.0D),
        Melted_Chocolate(3994199, 1.0D),
        Whirlwind(4031459, 1.0D);
        public int id;
        public double catchChance;

        private PokemonItem(int id, double chance) {
            this.id = id;
            this.catchChance = chance;
        }

        public int getId() {
            return this.id;
        }

        public int getItemChance() {
            return (int) (this.catchChance * 2.0D);
        }

        public static boolean isPokemonItem(int itemId) {
            return getPokemonItem(itemId) != null;
        }

        public static PItem getPokemonItem(int itemId) {
            for (PokemonItem i : values()) {
                if (i.id == itemId) {
                    return i;
                }
            }
            return HoldItem.getPokemonItem(itemId);
        }
    }

    public static enum HoldItem
            implements PItem {

        Green_Star(3992010, 0.5D, "Scope Lens - increases critical rate"),
        Orange_Star(3992012, 0.5D, "Quick Claw - sometimes goes first"),
        King_Star(3992025, 0.5D, "King Star - increases status rate"),
        Strange_Slush(3992011, 0.5D, "Shell Bell - absorbs HP"),
        Maha_Charm(3994185, 0.5D, "EXP Share - any monsters holding this will share EXP"),
        Question_Mark(3800088, 2.0D, "Everstone - prevents your monster from evolution"),
        Mini_Dragon(3994187, 0.5D, "Life Orb - more damage, but you get hurt"),
        Pheremone(4031507, 0.5D, "Black Herb - cannot have any status"),
        Kenta_Report(4031509, 0.5D, "White Herb - cannot have stats lowered"),
        Other_World_Key(4031409, 0.5D, "Expert Key - super effective attacks do more damage"),
        Ripped_Note(4031252, 0.5D, "Ripped Note - higher chance of increasing your own stats"),
        Herb_Pouch(4031555, 0.5D, "Herb Pouch - higher chance of decreasing enemy stats"),
        Sea_Dust(4031251, 0.5D, "Brightpowder - increases evasion rate"),
        Medal(4031160, 0.5D, "Medal - increases damage of attacks of the same type"),
        Dark_Chocolate(4031110, 3.0D, "Dark Chocolate - a not very effective attack to the opponent is negated, one time use"),
        White_Chocolate(4031109, 3.0D, "White Chocolate - a super effective attack from the opponent is negated, one time use"),
        Red_Candy(4032444, 4.0D, "Red Candy - when under 50% HP, upgrades attack, one time use"),
        Blue_Candy(4032445, 4.0D, "Blue Candy - when under 50% HP, upgrades defense, one time use"),
        Green_Candy(4032446, 4.0D, "Green Candy - when under 50% HP, upgrades speed, one time use"),
        Strawberry(4140102, 4.0D, "Heal Berry - heals 10% HP when under 50% HP, one time use"),
        Pineapple(4140101, 4.0D, "Cure Berry - heals status, one time use");
        public int id;
        public String customName;
        public double catchChance;

        private HoldItem(int id, double chance, String customName) {
            this.id = id;
            this.catchChance = chance;
            this.customName = customName;
        }

        public static HoldItem getPokemonItem(int itemId) {
            for (HoldItem i : values()) {
                if (i.id == itemId) {
                    return i;
                }
            }
            return null;
        }

        public int getId() {
            return this.id;
        }

        public int getItemChance() {
            return (int) (this.catchChance * 2.0D);
        }
    }

    public static abstract interface PItem {

        public abstract int getItemChance();

        public abstract int getId();
    }

    public static enum PokemonMob {

        Snail(PokemonItem.Perfect_Pitch, MobExp.FAST, PokemonAbility.QuickFeet, PokemonAbility.Unburden, new Integer[]{100100, 100101, 130101, 9500144, 4250000, 8600000}),
        Snail_2(PokemonItem.Perfect_Pitch, MobExp.FAST, PokemonAbility.QuickFeet, PokemonAbility.Unburden, new Integer[]{Integer.valueOf(100100), Integer.valueOf(100101), Integer.valueOf(130101), Integer.valueOf(2220000), Integer.valueOf(4250000), Integer.valueOf(8600000)}),
        Muru(null, MobExp.EASY, PokemonAbility.ShedSkin, PokemonAbility.ShieldDust, new Integer[]{Integer.valueOf(100130), Integer.valueOf(100131), Integer.valueOf(100132), Integer.valueOf(100133), Integer.valueOf(100134)}),
        Ti(null, MobExp.EASY, PokemonAbility.Filter, PokemonAbility.TintedLens, new Integer[]{Integer.valueOf(100120), Integer.valueOf(100121), Integer.valueOf(100122), Integer.valueOf(100123), Integer.valueOf(9001011), Integer.valueOf(8600004), Integer.valueOf(8600005), Integer.valueOf(8600006)}),
        Flower(null, MobExp.FAST, PokemonAbility.PurePower, PokemonAbility.SheerForce, new Integer[]{Integer.valueOf(150000), Integer.valueOf(150001), Integer.valueOf(150002), Integer.valueOf(9300174), Integer.valueOf(9300179)}),
        Spore(null, MobExp.ERRATIC, PokemonAbility.EffectSpore, PokemonAbility.Immunity, new Integer[]{Integer.valueOf(120100), Integer.valueOf(9300386), Integer.valueOf(3300000)}),
        Sage_Cat(PokemonItem.Bright_Feather, MobExp.SLOW, PokemonAbility.RunAway, PokemonAbility.Normalize, new Integer[]{Integer.valueOf(9400636), Integer.valueOf(6130209), Integer.valueOf(7220002)}),
        Mushroom(PokemonItem.Perfect_Pitch, MobExp.FAST, PokemonAbility.EffectSpore, PokemonAbility.Overgrow, new Integer[]{Integer.valueOf(1210102), Integer.valueOf(1110100), Integer.valueOf(2110200), Integer.valueOf(2220100), Integer.valueOf(2230101), Integer.valueOf(9500152), Integer.valueOf(9400539), Integer.valueOf(9400550), Integer.valueOf(3300001), Integer.valueOf(5250000), Integer.valueOf(8600001)}),
        Mushmom(PokemonItem.Melted_Chocolate, MobExp.FAST, PokemonAbility.EffectSpore, PokemonAbility.Overgrow, new Integer[]{Integer.valueOf(1210102), Integer.valueOf(1110100), Integer.valueOf(2110200), Integer.valueOf(2220100), Integer.valueOf(6130100), Integer.valueOf(8220007), Integer.valueOf(6300005), Integer.valueOf(9300191), Integer.valueOf(9300196), Integer.valueOf(9300209)}),
        Boogie(null, MobExp.STANDARD, PokemonAbility.OwnTempo, PokemonAbility.Limber, new Integer[]{Integer.valueOf(3230300), Integer.valueOf(9400005), Integer.valueOf(9400006), Integer.valueOf(9400007), Integer.valueOf(9400008), Integer.valueOf(6130104)}),
        Chaos_Boogie(null, MobExp.STANDARD, PokemonAbility.OwnTempo, PokemonAbility.Limber, new Integer[]{Integer.valueOf(3230300), Integer.valueOf(9400005), Integer.valueOf(9400006), Integer.valueOf(9400007), Integer.valueOf(9400008), Integer.valueOf(8800111)}),
        Pig(PokemonItem.Perfect_Pitch, MobExp.STANDARD, PokemonAbility.Gluttony, PokemonAbility.Scrappy, new Integer[]{Integer.valueOf(1210100), Integer.valueOf(1210101), Integer.valueOf(1210104), Integer.valueOf(4230103), Integer.valueOf(3300002), Integer.valueOf(9302011), Integer.valueOf(9500143), Integer.valueOf(8600003)}),
        Boar(PokemonItem.Perfect_Pitch, MobExp.STANDARD, PokemonAbility.Gluttony, PokemonAbility.Scrappy, new Integer[]{Integer.valueOf(1210100), Integer.valueOf(1210101), Integer.valueOf(1210104), Integer.valueOf(2230102), Integer.valueOf(3210100), Integer.valueOf(9400516), Integer.valueOf(4230400), Integer.valueOf(5250002)}),
        Boss_Slime(PokemonItem.Melted_Chocolate, MobExp.STANDARD, PokemonAbility.Illuminate, PokemonAbility.MagicGuard, new Integer[]{Integer.valueOf(9400737), Integer.valueOf(210100), Integer.valueOf(1210103), Integer.valueOf(9500151), Integer.valueOf(9400538), Integer.valueOf(9300027), Integer.valueOf(9400521), Integer.valueOf(9300187)}),
        Slime(PokemonItem.Perfect_Pitch, MobExp.STANDARD, PokemonAbility.Illuminate, PokemonAbility.MagicGuard, new Integer[]{Integer.valueOf(9400737), Integer.valueOf(210100), Integer.valueOf(1210103), Integer.valueOf(9500151), Integer.valueOf(9400538), Integer.valueOf(9300027), Integer.valueOf(9400521), Integer.valueOf(9400203), Integer.valueOf(9420528), Integer.valueOf(9400204), Integer.valueOf(3110300), Integer.valueOf(7120105), Integer.valueOf(8600002)}),
        Fox(null, MobExp.ERRATIC, PokemonAbility.SpeedBoost, PokemonAbility.Unnerve, new Integer[]{Integer.valueOf(9300385), Integer.valueOf(9400002), Integer.valueOf(9400004), Integer.valueOf(5100004), Integer.valueOf(7220001)}),
        Mask(null, MobExp.EASY, PokemonAbility.ShadowTag, PokemonAbility.Insomnia, new Integer[]{Integer.valueOf(9400706), Integer.valueOf(2230110), Integer.valueOf(2230111)}),
        MV(PokemonItem.Deathly_Fear, MobExp.EASY, PokemonAbility.ShadowTag, PokemonAbility.Insomnia, new Integer[]{Integer.valueOf(9400706), Integer.valueOf(9400746)}),
        Stump(null, MobExp.SLOW, PokemonAbility.SapSipper, PokemonAbility.SapSipper, new Integer[]{Integer.valueOf(130100), Integer.valueOf(1110101), Integer.valueOf(1130100), Integer.valueOf(2130100), Integer.valueOf(1140100), Integer.valueOf(3220000), Integer.valueOf(9300172), Integer.valueOf(9420527), Integer.valueOf(9420523), Integer.valueOf(9420514), Integer.valueOf(9420519)}),
        Frog(null, MobExp.EASY, PokemonAbility.Immunity, PokemonAbility.PoisonHeal, new Integer[]{Integer.valueOf(9400634), Integer.valueOf(9420001), Integer.valueOf(9420000)}),
        Cake(null, MobExp.EASY, PokemonAbility.NaturalCure, PokemonAbility.Synchronize, new Integer[]{Integer.valueOf(9400506), Integer.valueOf(9400570), Integer.valueOf(9400507)}),
        Cake_2(null, MobExp.EASY, PokemonAbility.NaturalCure, PokemonAbility.Synchronize, new Integer[]{Integer.valueOf(9400512), Integer.valueOf(9400570), Integer.valueOf(9400513)}),
        Training_Robot(null, MobExp.SLOW, PokemonAbility.Heatproof, PokemonAbility.Stall, new Integer[]{Integer.valueOf(9300409), Integer.valueOf(9300410), Integer.valueOf(9300411), Integer.valueOf(9300412), Integer.valueOf(9300413)}),
        Veil(null, MobExp.FAST, PokemonAbility.FlareBoost, PokemonAbility.FlameBody, new Integer[]{Integer.valueOf(9300083), Integer.valueOf(5100002), Integer.valueOf(6130201), Integer.valueOf(9400577), Integer.valueOf(8610000)}),
        Veil_2(null, MobExp.FAST, PokemonAbility.FlareBoost, PokemonAbility.FlameBody, new Integer[]{Integer.valueOf(9300083), Integer.valueOf(5100002), Integer.valueOf(6130201), Integer.valueOf(9400577), Integer.valueOf(8610001)}),
        Veil_3(null, MobExp.FAST, PokemonAbility.FlareBoost, PokemonAbility.FlameBody, new Integer[]{Integer.valueOf(9300083), Integer.valueOf(5100002), Integer.valueOf(6130201), Integer.valueOf(9400577), Integer.valueOf(8610002)}),
        Veil_4(null, MobExp.FAST, PokemonAbility.FlareBoost, PokemonAbility.FlameBody, new Integer[]{Integer.valueOf(9300083), Integer.valueOf(5100002), Integer.valueOf(6130201), Integer.valueOf(9400577), Integer.valueOf(8610003)}),
        Veil_5(null, MobExp.FAST, PokemonAbility.FlareBoost, PokemonAbility.FlameBody, new Integer[]{Integer.valueOf(9300083), Integer.valueOf(5100002), Integer.valueOf(6130201), Integer.valueOf(9400577), Integer.valueOf(8610004)}),
        Unveil(null, MobExp.FAST, PokemonAbility.FlareBoost, PokemonAbility.FlameBody, new Integer[]{Integer.valueOf(9300083), Integer.valueOf(5100002), Integer.valueOf(6130202)}),
        Flying(null, MobExp.EASY, PokemonAbility.SuperLuck, PokemonAbility.Sniper, new Integer[]{Integer.valueOf(2300100), Integer.valueOf(9400595), Integer.valueOf(9300084), Integer.valueOf(9300025), Integer.valueOf(4230107), Integer.valueOf(8840001)}),
        Alien(null, MobExp.STANDARD, PokemonAbility.Klutz, PokemonAbility.Multiscale, new Integer[]{Integer.valueOf(1120100), Integer.valueOf(9001005), Integer.valueOf(3230302), Integer.valueOf(3230103), Integer.valueOf(4230120), Integer.valueOf(4230121), Integer.valueOf(4230122), Integer.valueOf(5120100)}),
        Wraith(null, MobExp.STANDARD, PokemonAbility.Forewarn, PokemonAbility.Frisk, new Integer[]{Integer.valueOf(3230101), Integer.valueOf(4230102), Integer.valueOf(5090000), Integer.valueOf(9400556), Integer.valueOf(9400003), Integer.valueOf(5120506), Integer.valueOf(9400580)}),
        Wraith_Ghost(null, MobExp.STANDARD, PokemonAbility.Forewarn, PokemonAbility.Frisk, new Integer[]{Integer.valueOf(3230101), Integer.valueOf(4230102), Integer.valueOf(5090000), Integer.valueOf(9400556), Integer.valueOf(9400003), Integer.valueOf(5120506), Integer.valueOf(6110301)}),
        Wraith_Boss(PokemonItem.Saint_Stone, MobExp.STANDARD, PokemonAbility.Forewarn, PokemonAbility.Frisk, new Integer[]{Integer.valueOf(3230101), Integer.valueOf(4230102), Integer.valueOf(5090000), Integer.valueOf(9400556), Integer.valueOf(9400003), Integer.valueOf(6090003)}),
        Fairy(null, MobExp.STANDARD, PokemonAbility.SereneGrace, PokemonAbility.Illuminate, new Integer[]{Integer.valueOf(3000001), Integer.valueOf(3000007), Integer.valueOf(9400526), Integer.valueOf(9400517)}),
        Patrol(null, MobExp.STANDARD, PokemonAbility.MotorDrive, PokemonAbility.Regenerator, new Integer[]{Integer.valueOf(1150000), Integer.valueOf(2150003)}),
        Bird(null, MobExp.FAST, PokemonAbility.Defiant, PokemonAbility.Compoundeyes, new Integer[]{Integer.valueOf(9420005), Integer.valueOf(9600001), Integer.valueOf(9600002), Integer.valueOf(9400000), Integer.valueOf(3230307), Integer.valueOf(3230308), Integer.valueOf(3100102), Integer.valueOf(9400574), Integer.valueOf(8300000), Integer.valueOf(8140002), Integer.valueOf(9400599), Integer.valueOf(8210004)}),
        Black_Bird(PokemonItem.Saint_Stone, MobExp.FAST, PokemonAbility.Defiant, PokemonAbility.Compoundeyes, new Integer[]{Integer.valueOf(9420005), Integer.valueOf(9600001), Integer.valueOf(9600002), Integer.valueOf(9400000), Integer.valueOf(9400544), Integer.valueOf(3230308), Integer.valueOf(3100102), Integer.valueOf(9400574), Integer.valueOf(8300001), Integer.valueOf(8140001), Integer.valueOf(9400014)}),
        Red_Bird(PokemonItem.Phoenix_Egg, MobExp.FAST, PokemonAbility.Defiant, PokemonAbility.Compoundeyes, new Integer[]{Integer.valueOf(9420005), Integer.valueOf(9600001), Integer.valueOf(9600002), Integer.valueOf(9400000), Integer.valueOf(3230307), Integer.valueOf(3230308), Integer.valueOf(3100102), Integer.valueOf(9400574), Integer.valueOf(8300000), Integer.valueOf(8140002), Integer.valueOf(9400599), Integer.valueOf(9300089)}),
        Blue_Bird(PokemonItem.Freezer_Egg, MobExp.FAST, PokemonAbility.Defiant, PokemonAbility.Compoundeyes, new Integer[]{Integer.valueOf(9420005), Integer.valueOf(9600001), Integer.valueOf(9600002), Integer.valueOf(9400000), Integer.valueOf(9400544), Integer.valueOf(3230308), Integer.valueOf(3100102), Integer.valueOf(9400574), Integer.valueOf(8300001), Integer.valueOf(8140001), Integer.valueOf(9400599), Integer.valueOf(9300090)}),
        Dragon(PokemonItem.Dragon_Heart, MobExp.FAST, PokemonAbility.Defiant, PokemonAbility.Compoundeyes, new Integer[]{Integer.valueOf(9420005), Integer.valueOf(9600001), Integer.valueOf(9600002), Integer.valueOf(9400000), Integer.valueOf(9400544), Integer.valueOf(3230308), Integer.valueOf(3100102), Integer.valueOf(9400574), Integer.valueOf(8300006), Integer.valueOf(8300007)}),
        Manon(PokemonItem.Dragon_Heart, MobExp.FAST, PokemonAbility.Defiant, PokemonAbility.Compoundeyes, new Integer[]{Integer.valueOf(9420005), Integer.valueOf(9600001), Integer.valueOf(9600002), Integer.valueOf(9400000), Integer.valueOf(9400544), Integer.valueOf(3230308), Integer.valueOf(3100102), Integer.valueOf(9400574), Integer.valueOf(9300291)}),
        Griffey(PokemonItem.Griffey_Wind, MobExp.FAST, PokemonAbility.Defiant, PokemonAbility.Compoundeyes, new Integer[]{Integer.valueOf(9420005), Integer.valueOf(9600001), Integer.valueOf(9600002), Integer.valueOf(9400000), Integer.valueOf(3230307), Integer.valueOf(3230308), Integer.valueOf(3100102), Integer.valueOf(9400574), Integer.valueOf(9300292)}),
        Crow(PokemonItem.Intelligence_Document, MobExp.FAST, PokemonAbility.Defiant, PokemonAbility.Compoundeyes, new Integer[]{Integer.valueOf(9420005), Integer.valueOf(9600001), Integer.valueOf(9600002), Integer.valueOf(9400000), Integer.valueOf(3230307), Integer.valueOf(3230308), Integer.valueOf(3100102), Integer.valueOf(9001013)}),
        Monkey(null, MobExp.FAST, PokemonAbility.EarlyBird, PokemonAbility.Contrary, new Integer[]{Integer.valueOf(9500383), Integer.valueOf(9500384), Integer.valueOf(6130207)}),
        White_Monkey(null, MobExp.FAST, PokemonAbility.EarlyBird, PokemonAbility.Contrary, new Integer[]{Integer.valueOf(9500383), Integer.valueOf(9500385), Integer.valueOf(9500386), Integer.valueOf(6130207)}),
        Sign(null, MobExp.FAST, PokemonAbility.Aftermath, PokemonAbility.AngerPoint, new Integer[]{Integer.valueOf(1150001), Integer.valueOf(9420500), Integer.valueOf(3150000), Integer.valueOf(9420503)}),
        Eye(null, MobExp.STANDARD, PokemonAbility.Adaptability, PokemonAbility.Analytic, new Integer[]{Integer.valueOf(2230100), Integer.valueOf(3230100), Integer.valueOf(4230100), Integer.valueOf(2230113), Integer.valueOf(9400515), Integer.valueOf(6230100), Integer.valueOf(8200000)}),
        Eye_Drunk(null, MobExp.STANDARD, PokemonAbility.Adaptability, PokemonAbility.Analytic, new Integer[]{Integer.valueOf(2230100), Integer.valueOf(3230100), Integer.valueOf(2230113), Integer.valueOf(9400515), Integer.valueOf(6230100), Integer.valueOf(8200000)}),
        Dark_Snake(null, MobExp.STANDARD, PokemonAbility.Intimidate, PokemonAbility.PoisonTouch, new Integer[]{Integer.valueOf(1150002), Integer.valueOf(2130103), Integer.valueOf(9400633)}),
        Red_Snake(PokemonItem.Yellow_Crystal, MobExp.STANDARD, PokemonAbility.Intimidate, PokemonAbility.PoisonTouch, new Integer[]{Integer.valueOf(1150002), Integer.valueOf(2130103), Integer.valueOf(9420002), Integer.valueOf(2100105), Integer.valueOf(4230504), Integer.valueOf(9420516), Integer.valueOf(5220004)}),
        Black_Slime(null, MobExp.STANDARD, PokemonAbility.ClearBody, PokemonAbility.Stench, new Integer[]{Integer.valueOf(9420502), Integer.valueOf(9420506), Integer.valueOf(9420501), Integer.valueOf(9420529), Integer.valueOf(9420530), Integer.valueOf(9420533), Integer.valueOf(9420534), Integer.valueOf(9420508), Integer.valueOf(9420515), Integer.valueOf(9420517)}),
        Golem(null, MobExp.SLOW, PokemonAbility.ClearBody, PokemonAbility.BigPecks, new Integer[]{Integer.valueOf(5130101), Integer.valueOf(5130102), Integer.valueOf(5150000), Integer.valueOf(9500149), Integer.valueOf(9500150), Integer.valueOf(9300416), Integer.valueOf(9300024), Integer.valueOf(9300287), Integer.valueOf(8840005), Integer.valueOf(8210005), Integer.valueOf(8190002)}),
        Poison_Golem(null, MobExp.SLOW, PokemonAbility.ClearBody, PokemonAbility.BigPecks, new Integer[]{Integer.valueOf(5130101), Integer.valueOf(5130102), Integer.valueOf(5150000), Integer.valueOf(9300180), Integer.valueOf(9300181), Integer.valueOf(9300182)}),
        Dual_Blade(null, MobExp.ERRATIC, PokemonAbility.SpeedBoost, PokemonAbility.QuickFeet, new Integer[]{Integer.valueOf(9001015), Integer.valueOf(9001016), Integer.valueOf(9001017), Integer.valueOf(9001018)}),
        Egg(null, MobExp.EASY, PokemonAbility.MarvelScale, PokemonAbility.Scrappy, new Integer[]{Integer.valueOf(9400511), Integer.valueOf(9400510)}),
        Monster(PokemonItem.Perfect_Pitch, MobExp.FAST, PokemonAbility.NoGuard, PokemonAbility.Hustle, new Integer[]{Integer.valueOf(2150000), Integer.valueOf(2230114), Integer.valueOf(9300173), Integer.valueOf(5250001)}),
        Crocodile(null, MobExp.FAST, PokemonAbility.WaterVeil, PokemonAbility.WaterAbsorb, new Integer[]{Integer.valueOf(3110100), Integer.valueOf(5130103), Integer.valueOf(6220000), Integer.valueOf(6130204), Integer.valueOf(8210000), Integer.valueOf(8840002)}),
        Accessory(null, MobExp.FAST, PokemonAbility.Contrary, PokemonAbility.Contrary, new Integer[]{Integer.valueOf(2150001), Integer.valueOf(2150002)}),
        Wolf(null, MobExp.STANDARD, PokemonAbility.HugePower, PokemonAbility.HyperCutter, new Integer[]{Integer.valueOf(9410000), Integer.valueOf(9410001), Integer.valueOf(9410002), Integer.valueOf(5130104), Integer.valueOf(5140000), Integer.valueOf(9500132), Integer.valueOf(8140000), Integer.valueOf(9300354)}),
        Ninja(null, MobExp.FAST, PokemonAbility.QuickFeet, PokemonAbility.SpeedBoost, new Integer[]{Integer.valueOf(9400400), Integer.valueOf(9400404), Integer.valueOf(9400401), Integer.valueOf(9400406), Integer.valueOf(9400402), Integer.valueOf(9400403), Integer.valueOf(9400405)}),
        Lizard(null, MobExp.FAST, PokemonAbility.DrySkin, PokemonAbility.Moxie, new Integer[]{Integer.valueOf(9420004), Integer.valueOf(9420003)}),
        Sheep(null, MobExp.EASY, PokemonAbility.Intimidate, PokemonAbility.Moody, new Integer[]{Integer.valueOf(9600003), Integer.valueOf(9600008)}),
        Lupin_Clown(null, MobExp.STANDARD, PokemonAbility.Guts, PokemonAbility.BigPecks, new Integer[]{Integer.valueOf(3210800), Integer.valueOf(4230101), Integer.valueOf(9302011), Integer.valueOf(9410003), Integer.valueOf(9410004)}),
        Skeleton(null, MobExp.STANDARD, PokemonAbility.BadDreams, PokemonAbility.WeakArmor, new Integer[]{Integer.valueOf(5150001), Integer.valueOf(4230125), Integer.valueOf(4230126), Integer.valueOf(6230602), Integer.valueOf(7130103), Integer.valueOf(8190003), Integer.valueOf(8190004)}),
        Clown(null, MobExp.ERRATIC, PokemonAbility.BadDreams, PokemonAbility.WeakArmor, new Integer[]{Integer.valueOf(9400558), Integer.valueOf(9400640)}),
        Bunny(null, MobExp.EASY, PokemonAbility.QuickFeet, PokemonAbility.SpeedBoost, new Integer[]{Integer.valueOf(9300414), Integer.valueOf(9400649), Integer.valueOf(3230400), Integer.valueOf(4230300), Integer.valueOf(5160000), Integer.valueOf(5160001), Integer.valueOf(2100100), Integer.valueOf(2100101), Integer.valueOf(9300392)}),
        Raco(null, MobExp.FAST, PokemonAbility.Moody, PokemonAbility.Moxie, new Integer[]{Integer.valueOf(9400001), Integer.valueOf(7150000), Integer.valueOf(7150003), Integer.valueOf(8105000)}),
        Goat(null, MobExp.EASY, PokemonAbility.Intimidate, PokemonAbility.Moody, new Integer[]{Integer.valueOf(9600004), Integer.valueOf(9600005)}),
        Witch(PokemonItem.Ice_Pick, MobExp.FAST, PokemonAbility.Intimidate, PokemonAbility.IronBarbs, new Integer[]{Integer.valueOf(5300100), Integer.valueOf(6090001)}),
        Red_Tea(null, MobExp.EASY, PokemonAbility.WaterAbsorb, PokemonAbility.WaterVeil, new Integer[]{Integer.valueOf(3400000), Integer.valueOf(3400001), Integer.valueOf(3400002), Integer.valueOf(9410005)}),
        Yellow_Tea(null, MobExp.EASY, PokemonAbility.WaterAbsorb, PokemonAbility.WaterVeil, new Integer[]{Integer.valueOf(3400000), Integer.valueOf(3400001), Integer.valueOf(3400002), Integer.valueOf(9410006)}),
        Green_Tea(null, MobExp.EASY, PokemonAbility.WaterAbsorb, PokemonAbility.WaterVeil, new Integer[]{Integer.valueOf(3400000), Integer.valueOf(3400001), Integer.valueOf(3400002), Integer.valueOf(9410007)}),
        Pepe(null, MobExp.ERRATIC, PokemonAbility.ThickFat, PokemonAbility.ThickFat, new Integer[]{Integer.valueOf(3300003), Integer.valueOf(3300004), Integer.valueOf(5400000), Integer.valueOf(6130103), Integer.valueOf(6230100), Integer.valueOf(3210450)}),
        Muncher(null, MobExp.FAST, PokemonAbility.BattleArmor, PokemonAbility.MagmaArmor, new Integer[]{Integer.valueOf(3150001), Integer.valueOf(3150002), Integer.valueOf(8105005)}),
        Cow(null, MobExp.EASY, PokemonAbility.Moxie, PokemonAbility.Moody, new Integer[]{Integer.valueOf(9600006), Integer.valueOf(9600007)}),
        Fire_Sentinel(null, MobExp.FAST, PokemonAbility.MagicGuard, PokemonAbility.MagicGuard, new Integer[]{Integer.valueOf(5200000), Integer.valueOf(3000000), Integer.valueOf(5200002)}),
        Ice_Sentinel(null, MobExp.FAST, PokemonAbility.MagicGuard, PokemonAbility.MagicGuard, new Integer[]{Integer.valueOf(5200000), Integer.valueOf(3000000), Integer.valueOf(5200001)}),
        Cellion(PokemonItem.Most_Corrupted, MobExp.FAST, PokemonAbility.TintedLens, PokemonAbility.Filter, new Integer[]{Integer.valueOf(9400509), Integer.valueOf(3210200), Integer.valueOf(5120001), Integer.valueOf(6230401), Integer.valueOf(7130000), Integer.valueOf(9500315)}),
        Lioner(PokemonItem.Most_Corrupted, MobExp.FAST, PokemonAbility.TintedLens, PokemonAbility.Filter, new Integer[]{Integer.valueOf(3210201), Integer.valueOf(5120002), Integer.valueOf(6230401), Integer.valueOf(7130000), Integer.valueOf(9500315)}),
        Grupin(PokemonItem.Most_Corrupted, MobExp.FAST, PokemonAbility.TintedLens, PokemonAbility.Filter, new Integer[]{Integer.valueOf(3210202), Integer.valueOf(5120003), Integer.valueOf(6230401), Integer.valueOf(7130000), Integer.valueOf(9500315)}),
        Elephant(null, MobExp.FAST, PokemonAbility.Static, PokemonAbility.Blaze, new Integer[]{Integer.valueOf(9400542), Integer.valueOf(9400543), Integer.valueOf(6160001), Integer.valueOf(6160002)}),
        Chronos(PokemonItem.More_Corrupted, MobExp.ERRATIC, PokemonAbility.BadDreams, PokemonAbility.Defiant, new Integer[]{Integer.valueOf(9300015), Integer.valueOf(9300016), Integer.valueOf(9300017), Integer.valueOf(9300192)}),
        Pixie(PokemonItem.Corrupted_Item, MobExp.EASY, PokemonAbility.MagicGuard, PokemonAbility.SereneGrace, new Integer[]{Integer.valueOf(3230200), Integer.valueOf(4230106), Integer.valueOf(5120000), Integer.valueOf(9300038), Integer.valueOf(9300039)}),
        Walrus(null, MobExp.FAST, PokemonAbility.ThickFat, PokemonAbility.VoltAbsorb, new Integer[]{Integer.valueOf(9500145), Integer.valueOf(9500146), Integer.valueOf(3230405), Integer.valueOf(4230124), Integer.valueOf(4230123)}),
        Mannequin(null, MobExp.EASY, PokemonAbility.MarvelScale, PokemonAbility.Unnerve, new Integer[]{Integer.valueOf(4300006), Integer.valueOf(4300007), Integer.valueOf(4300008)}),
        Tortie(null, MobExp.SLOW, PokemonAbility.Stall, PokemonAbility.OwnTempo, new Integer[]{Integer.valueOf(4130101), Integer.valueOf(9500148)}),
        Leprechaun(PokemonItem.Magical_Array, MobExp.ERRATIC, PokemonAbility.Defeatist, PokemonAbility.TangledFeet, new Integer[]{Integer.valueOf(9400583), Integer.valueOf(9400575)}),
        Horse(PokemonItem.Life_Root, MobExp.ERRATIC, PokemonAbility.Defeatist, PokemonAbility.TangledFeet, new Integer[]{Integer.valueOf(9400563), Integer.valueOf(3230305), Integer.valueOf(9400549)}),
        Spider(null, MobExp.FAST, PokemonAbility.ToxicBoost, PokemonAbility.TangledFeet, new Integer[]{Integer.valueOf(9400540), Integer.valueOf(7150001), Integer.valueOf(9400545)}),
        Leatty(null, MobExp.EASY, PokemonAbility.Multiscale, PokemonAbility.Heatproof, new Integer[]{Integer.valueOf(5300000), Integer.valueOf(5300001)}),
        Night(null, MobExp.SLOW, PokemonAbility.LiquidOoze, PokemonAbility.QuickFeet, new Integer[]{Integer.valueOf(9400011), Integer.valueOf(9400013)}),
        Night_Boss(PokemonItem.Bright_Feather, MobExp.SLOW, PokemonAbility.LiquidOoze, PokemonAbility.QuickFeet, new Integer[]{Integer.valueOf(9400011), Integer.valueOf(6090002)}),
        CD(PokemonItem.Deathly_Fear, MobExp.ERRATIC, PokemonAbility.Stall, PokemonAbility.Defeatist, new Integer[]{Integer.valueOf(4300009), Integer.valueOf(4300010), Integer.valueOf(4300011), Integer.valueOf(4300012), Integer.valueOf(4300013)}),
        Goblin(null, MobExp.FAST, PokemonAbility.Analytic, PokemonAbility.Adaptability, new Integer[]{Integer.valueOf(9500387), Integer.valueOf(9500388), Integer.valueOf(9400012), Integer.valueOf(9500389)}),
        Yellow_Goblin(null, MobExp.FAST, PokemonAbility.Analytic, PokemonAbility.Adaptability, new Integer[]{Integer.valueOf(9500387), Integer.valueOf(9500388), Integer.valueOf(9400012), Integer.valueOf(7130400)}),
        Blue_Goblin(null, MobExp.FAST, PokemonAbility.Analytic, PokemonAbility.Adaptability, new Integer[]{Integer.valueOf(9500387), Integer.valueOf(9500388), Integer.valueOf(9400012), Integer.valueOf(7130401)}),
        Green_Goblin(null, MobExp.FAST, PokemonAbility.Analytic, PokemonAbility.Adaptability, new Integer[]{Integer.valueOf(9500387), Integer.valueOf(9500388), Integer.valueOf(9400012), Integer.valueOf(7130402)}),
        Ratz(null, MobExp.FAST, PokemonAbility.Normalize, PokemonAbility.Limber, new Integer[]{Integer.valueOf(3110102), Integer.valueOf(3210205)}),
        Retz(null, MobExp.FAST, PokemonAbility.Normalize, PokemonAbility.Limber, new Integer[]{Integer.valueOf(3110102), Integer.valueOf(3210208)}),
        Human_M(PokemonItem.Gold_Pick, MobExp.FAST, PokemonAbility.Normalize, PokemonAbility.ShadowTag, new Integer[]{Integer.valueOf(9400100), Integer.valueOf(9400101), Integer.valueOf(9400102), Integer.valueOf(9400110), Integer.valueOf(9400111), Integer.valueOf(9400120), Integer.valueOf(9400112)}),
        Boss_M(PokemonItem.Gold_Pick, MobExp.FAST, PokemonAbility.Normalize, PokemonAbility.ShadowTag, new Integer[]{Integer.valueOf(9400112), Integer.valueOf(9400113), Integer.valueOf(9400300)}),
        Human_F(PokemonItem.Gold_Pick, MobExp.FAST, PokemonAbility.Normalize, PokemonAbility.ShadowTag, new Integer[]{Integer.valueOf(9400103), Integer.valueOf(9400121)}),
        Rogue_1(PokemonItem.Coin, MobExp.FAST, PokemonAbility.Normalize, PokemonAbility.ShadowTag, new Integer[]{Integer.valueOf(9400100), Integer.valueOf(9400101), Integer.valueOf(9400102), Integer.valueOf(9400110), Integer.valueOf(9400111), Integer.valueOf(9001003), Integer.valueOf(8610016)}),
        Kyrin_1(PokemonItem.Coin, MobExp.FAST, PokemonAbility.Normalize, PokemonAbility.ShadowTag, new Integer[]{Integer.valueOf(9001004), Integer.valueOf(9300158)}),
        Kyrin_2(PokemonItem.Coin, MobExp.FAST, PokemonAbility.Normalize, PokemonAbility.ShadowTag, new Integer[]{Integer.valueOf(9001004), Integer.valueOf(9300159)}),
        Yeti_Weak(null, MobExp.STANDARD, PokemonAbility.ThickFat, PokemonAbility.Unburden, new Integer[]{Integer.valueOf(5100000), Integer.valueOf(5100001), Integer.valueOf(9300258), Integer.valueOf(6300001), Integer.valueOf(7130102)}),
        Yeti_Strong(PokemonItem.Blue_Crystal, MobExp.STANDARD, PokemonAbility.ThickFat, PokemonAbility.Unburden, new Integer[]{Integer.valueOf(5100000), Integer.valueOf(5100001), Integer.valueOf(9300258), Integer.valueOf(6300001), Integer.valueOf(8220001)}),
        Dark_Yeti_Weak(null, MobExp.STANDARD, PokemonAbility.ThickFat, PokemonAbility.Unburden, new Integer[]{Integer.valueOf(5100000), Integer.valueOf(5130106), Integer.valueOf(9500128), Integer.valueOf(6400001), Integer.valueOf(8140100)}),
        Camera(PokemonItem.Summoning_Frame, MobExp.STANDARD, PokemonAbility.MotorDrive, PokemonAbility.Static, new Integer[]{Integer.valueOf(9400546), Integer.valueOf(6150000), Integer.valueOf(7150004), Integer.valueOf(7090000)}),
        Jar(null, MobExp.FAST, PokemonAbility.ShieldDust, PokemonAbility.ClearBody, new Integer[]{Integer.valueOf(9001022), Integer.valueOf(4230506)}),
        Balrog(PokemonItem.Most_Corrupted, MobExp.IMPOSSIBLE, PokemonAbility.Intimidate, PokemonAbility.Intimidate, new Integer[]{Integer.valueOf(6400007), Integer.valueOf(8130100), Integer.valueOf(8150000), Integer.valueOf(9400514)}),
        Vehicle(null, MobExp.FAST, PokemonAbility.MotorDrive, PokemonAbility.Regenerator, new Integer[]{Integer.valueOf(9420507), Integer.valueOf(9420504), Integer.valueOf(9420505), Integer.valueOf(9420518)}),
        Teddy(null, MobExp.FAST, PokemonAbility.Synchronize, PokemonAbility.Insomnia, new Integer[]{Integer.valueOf(3000005), Integer.valueOf(3110101), Integer.valueOf(3210203), Integer.valueOf(9001028), Integer.valueOf(6230500), Integer.valueOf(7130010), Integer.valueOf(7130300)}),
        Drake(null, MobExp.STANDARD, PokemonAbility.Adaptability, PokemonAbility.Aftermath, new Integer[]{Integer.valueOf(4130100), Integer.valueOf(5130100), Integer.valueOf(6130100), Integer.valueOf(6230600), Integer.valueOf(6230601)}),
        Doll_V(null, MobExp.FAST, PokemonAbility.Contrary, PokemonAbility.LiquidOoze, new Integer[]{Integer.valueOf(9400559), Integer.valueOf(9400561)}),
        Doll_H(null, MobExp.FAST, PokemonAbility.Contrary, PokemonAbility.NoGuard, new Integer[]{Integer.valueOf(9400560), Integer.valueOf(9400562)}),
        Bellflower(null, MobExp.STANDARD, PokemonAbility.Overgrow, PokemonAbility.SapSipper, new Integer[]{Integer.valueOf(9001023), Integer.valueOf(5120502)}),
        Sea(null, MobExp.ERRATIC, PokemonAbility.Torrent, PokemonAbility.WaterAbsorb, new Integer[]{Integer.valueOf(2230105), Integer.valueOf(2230106)}),
        Plane(null, MobExp.FAST, PokemonAbility.MotorDrive, PokemonAbility.Stall, new Integer[]{Integer.valueOf(3230303), Integer.valueOf(3210206), Integer.valueOf(3230304)}),
        Fish(null, MobExp.FAST, PokemonAbility.WaterVeil, PokemonAbility.Torrent, new Integer[]{Integer.valueOf(2230107), Integer.valueOf(2230109), Integer.valueOf(2230200), Integer.valueOf(3000006), Integer.valueOf(3230104), Integer.valueOf(4230200), Integer.valueOf(7130020), Integer.valueOf(8140600), Integer.valueOf(9300099), Integer.valueOf(8150101)}),
        Fish_Poison(null, MobExp.FAST, PokemonAbility.WaterVeil, PokemonAbility.Torrent, new Integer[]{Integer.valueOf(2230107), Integer.valueOf(2230109), Integer.valueOf(2230200), Integer.valueOf(3000006), Integer.valueOf(3230104), Integer.valueOf(4230201), Integer.valueOf(7130020), Integer.valueOf(8140600), Integer.valueOf(8150100), Integer.valueOf(8150101)}),
        Scorpion(null, MobExp.STANDARD, PokemonAbility.ToxicBoost, PokemonAbility.PoisonTouch, new Integer[]{Integer.valueOf(5160002), Integer.valueOf(5160003), Integer.valueOf(2110301)}),
        Tiger(null, MobExp.EASY, PokemonAbility.Intimidate, PokemonAbility.HyperCutter, new Integer[]{Integer.valueOf(5100003), Integer.valueOf(5100005)}),
        Coketump(PokemonItem.Coin, MobExp.ERRATIC, PokemonAbility.Regenerator, PokemonAbility.NaturalCure, new Integer[]{Integer.valueOf(9500154), Integer.valueOf(9500153), Integer.valueOf(8220009)}),
        Scarlion(PokemonItem.Maple_Marble, MobExp.STANDARD, PokemonAbility.Stall, PokemonAbility.PurePower, new Integer[]{Integer.valueOf(9420531), Integer.valueOf(9420535), Integer.valueOf(9420538), Integer.valueOf(9420548)}),
        Targa(PokemonItem.Maple_Marble, MobExp.STANDARD, PokemonAbility.Stall, PokemonAbility.PurePower, new Integer[]{Integer.valueOf(9420532), Integer.valueOf(9420536), Integer.valueOf(9420539), Integer.valueOf(9420540), Integer.valueOf(9420543)}),
        Xerxes(PokemonItem.Ancient_Relic, MobExp.SLOW, PokemonAbility.Stall, PokemonAbility.Immunity, new Integer[]{Integer.valueOf(5160005), Integer.valueOf(5160006), Integer.valueOf(5160004), Integer.valueOf(6160000), Integer.valueOf(6160003)}),
        Robo(null, MobExp.FAST, PokemonAbility.Stall, PokemonAbility.MotorDrive, new Integer[]{Integer.valueOf(4230111), Integer.valueOf(4230112)}),
        Block_Golem(PokemonItem.Corrupted, MobExp.SLOW, PokemonAbility.PurePower, PokemonAbility.ShieldDust, new Integer[]{Integer.valueOf(4230109), Integer.valueOf(4230110), Integer.valueOf(4130103)}),
        Block(PokemonItem.More_Corrupted, MobExp.SLOW, PokemonAbility.PurePower, PokemonAbility.ShieldDust, new Integer[]{Integer.valueOf(4230109), Integer.valueOf(4230110), Integer.valueOf(9300390)}),
        Tauro(null, MobExp.EASY, PokemonAbility.VoltAbsorb, PokemonAbility.Static, new Integer[]{Integer.valueOf(7130100), Integer.valueOf(7130101)}),
        Gray(PokemonItem.Ancient_Relic, MobExp.FAST, PokemonAbility.RunAway, PokemonAbility.Adaptability, new Integer[]{Integer.valueOf(4230116), Integer.valueOf(4230117), Integer.valueOf(4230118), Integer.valueOf(4240000), Integer.valueOf(6220001)}),
        Zakum(null, MobExp.FAST, PokemonAbility.AngerPoint, PokemonAbility.ClearBody, new Integer[]{Integer.valueOf(6300004), Integer.valueOf(6400003), Integer.valueOf(6230101), Integer.valueOf(6300003), Integer.valueOf(6400004)}),
        Chaos_Zakum(null, MobExp.SLOW, PokemonAbility.AngerPoint, PokemonAbility.ClearBody, new Integer[]{Integer.valueOf(8800116), Integer.valueOf(8800114), Integer.valueOf(8800112), Integer.valueOf(8800113), Integer.valueOf(8800115)}),
        Robot(PokemonItem.Ventilation, MobExp.STANDARD, PokemonAbility.MotorDrive, PokemonAbility.Regenerator, new Integer[]{Integer.valueOf(6150000), Integer.valueOf(7150004), Integer.valueOf(8105001), Integer.valueOf(8105002), Integer.valueOf(9001035)}),
        Tick(PokemonItem.Pocket_Watch, MobExp.ERRATIC, PokemonAbility.Insomnia, PokemonAbility.EarlyBird, new Integer[]{Integer.valueOf(3210207), Integer.valueOf(4230113), Integer.valueOf(5220003)}),
        Crew(null, MobExp.FAST, PokemonAbility.SheerForce, PokemonAbility.Scrappy, new Integer[]{Integer.valueOf(9001030), Integer.valueOf(6130208), Integer.valueOf(7130104)}),
        Crew_Angry(PokemonItem.Summoning_Frame, MobExp.FAST, PokemonAbility.SheerForce, PokemonAbility.Scrappy, new Integer[]{Integer.valueOf(9001030), Integer.valueOf(9300105)}),
        Cactus(PokemonItem.Heart_of_Heart, MobExp.FAST, PokemonAbility.SapSipper, PokemonAbility.EffectSpore, new Integer[]{Integer.valueOf(2100102), Integer.valueOf(2100103), Integer.valueOf(2100104), Integer.valueOf(3220001)}),
        Keeper(PokemonItem.Rainbow_Leaf, MobExp.SLOW, PokemonAbility.Intimidate, PokemonAbility.Forewarn, new Integer[]{Integer.valueOf(9400576), Integer.valueOf(9400581), Integer.valueOf(9400578), Integer.valueOf(9400579), Integer.valueOf(9400582), Integer.valueOf(9400596)}),
        Keeper_2(PokemonItem.Rainbow_Leaf, MobExp.SLOW, PokemonAbility.Intimidate, PokemonAbility.Forewarn, new Integer[]{Integer.valueOf(9400576), Integer.valueOf(9400581), Integer.valueOf(9400578), Integer.valueOf(9400579), Integer.valueOf(9400582), Integer.valueOf(9400597)}),
        Ani(PokemonItem.Deathly_Fear, MobExp.SLOW, PokemonAbility.Intimidate, PokemonAbility.Gluttony, new Integer[]{Integer.valueOf(8210006), Integer.valueOf(8210007), Integer.valueOf(8210010)}),
        Boom(null, MobExp.FAST, PokemonAbility.ShadowTag, PokemonAbility.ShedSkin, new Integer[]{Integer.valueOf(8500003), Integer.valueOf(8510100), Integer.valueOf(8500004)}),
        Plead(null, MobExp.STANDARD, PokemonAbility.Gluttony, PokemonAbility.Guts, new Integer[]{Integer.valueOf(2100106), Integer.valueOf(2100108), Integer.valueOf(2100107)}),
        Buffoon(null, MobExp.ERRATIC, PokemonAbility.Truant, PokemonAbility.HugePower, new Integer[]{Integer.valueOf(6300100), Integer.valueOf(6400100)}),
        Papulatus_Clock(PokemonItem.Pocket_Watch, MobExp.ERRATIC, PokemonAbility.Insomnia, PokemonAbility.EarlyBird, new Integer[]{Integer.valueOf(5220003), Integer.valueOf(9500180)}),
        Papulatus(PokemonItem.Pocket_Watch, MobExp.ERRATIC, PokemonAbility.Insomnia, PokemonAbility.EarlyBird, new Integer[]{Integer.valueOf(5220003), Integer.valueOf(9500181)}),
        Porky(null, MobExp.FAST, PokemonAbility.Normalize, PokemonAbility.Gluttony, new Integer[]{Integer.valueOf(4230500), Integer.valueOf(4230501), Integer.valueOf(4230502)}),
        Sand(null, MobExp.ERRATIC, PokemonAbility.SheerForce, PokemonAbility.Intimidate, new Integer[]{Integer.valueOf(2110300), Integer.valueOf(3100101), Integer.valueOf(4230600)}),
        Dark_Sand(null, MobExp.ERRATIC, PokemonAbility.SheerForce, PokemonAbility.Intimidate, new Integer[]{Integer.valueOf(2110300), Integer.valueOf(3110101), Integer.valueOf(4230600)}),
        Hoblin_1(PokemonItem.Magical_Array, MobExp.ERRATIC, PokemonAbility.Intimidate, PokemonAbility.QuickFeet, new Integer[]{Integer.valueOf(9300276), Integer.valueOf(9300279), Integer.valueOf(9300280), Integer.valueOf(9300281)}),
        Hoblin_2(PokemonItem.Magical_Array, MobExp.ERRATIC, PokemonAbility.Intimidate, PokemonAbility.QuickFeet, new Integer[]{Integer.valueOf(9300277), Integer.valueOf(9300279), Integer.valueOf(9300280), Integer.valueOf(9300281)}),
        Hoblin_3(PokemonItem.Magical_Array, MobExp.ERRATIC, PokemonAbility.Intimidate, PokemonAbility.QuickFeet, new Integer[]{Integer.valueOf(9300278), Integer.valueOf(9300279), Integer.valueOf(9300280), Integer.valueOf(9300281)}),
        Roid(null, MobExp.STANDARD, PokemonAbility.Regenerator, PokemonAbility.Immunity, new Integer[]{Integer.valueOf(5110301), Integer.valueOf(5110302), Integer.valueOf(7110300), Integer.valueOf(8105003), Integer.valueOf(8105004), Integer.valueOf(9001034)}),
        Reindeer(null, MobExp.STANDARD, PokemonAbility.Gluttony, PokemonAbility.Guts, new Integer[]{Integer.valueOf(5120505), Integer.valueOf(8210001), Integer.valueOf(8210002)}),
        Ghost(PokemonItem.Old_Glove, MobExp.FAST, PokemonAbility.Frisk, PokemonAbility.NoGuard, new Integer[]{Integer.valueOf(9420509), Integer.valueOf(9420511), Integer.valueOf(9420510), Integer.valueOf(9420512), Integer.valueOf(9420513)}),
        Buffy(null, MobExp.FAST, PokemonAbility.Truant, PokemonAbility.Moody, new Integer[]{Integer.valueOf(6130200), Integer.valueOf(6230300)}),
        Homun(null, MobExp.STANDARD, PokemonAbility.Scrappy, PokemonAbility.Filter, new Integer[]{Integer.valueOf(6110300), Integer.valueOf(7110301), Integer.valueOf(8110300)}),
        Rash(null, MobExp.FAST, PokemonAbility.BattleArmor, PokemonAbility.EarlyBird, new Integer[]{Integer.valueOf(7130500), Integer.valueOf(7130501)}),
        Beetle(null, MobExp.FAST, PokemonAbility.BigPecks, PokemonAbility.BattleArmor, new Integer[]{Integer.valueOf(7130002), Integer.valueOf(7130003)}),
        Scarlion_Boss(PokemonItem.Maple_Marble, MobExp.STANDARD, PokemonAbility.Stall, PokemonAbility.PurePower, new Integer[]{Integer.valueOf(9420548), Integer.valueOf(9420549)}),
        Targa_Boss(PokemonItem.Maple_Marble, MobExp.STANDARD, PokemonAbility.Stall, PokemonAbility.PurePower, new Integer[]{Integer.valueOf(9420543), Integer.valueOf(9420544)}),
        Cornian(null, MobExp.FRUSTRATING, PokemonAbility.HyperCutter, PokemonAbility.Defiant, new Integer[]{Integer.valueOf(9500374), Integer.valueOf(8150200), Integer.valueOf(8150201)}),
        Hobi(null, MobExp.STANDARD, PokemonAbility.OwnTempo, PokemonAbility.Limber, new Integer[]{Integer.valueOf(7130600), Integer.valueOf(7130601), Integer.valueOf(7130004)}),
        Gatekeeper_Nex(null, MobExp.IMPOSSIBLE, PokemonAbility.Intimidate, PokemonAbility.MagicGuard, new Integer[]{Integer.valueOf(7120100), Integer.valueOf(7120101), Integer.valueOf(7120102), Integer.valueOf(8120100), Integer.valueOf(8120101), Integer.valueOf(8140510)}),
        Wyvern(null, MobExp.SLOW, PokemonAbility.Intimidate, PokemonAbility.Defeatist, new Integer[]{Integer.valueOf(8300002), Integer.valueOf(8150301), Integer.valueOf(8150302)}),
        Klock(null, MobExp.STANDARD, PokemonAbility.EarlyBird, PokemonAbility.Insomnia, new Integer[]{Integer.valueOf(8140100), Integer.valueOf(8140200)}),
        Viking(null, MobExp.SLOW, PokemonAbility.SuperLuck, PokemonAbility.Sniper, new Integer[]{Integer.valueOf(7140000), Integer.valueOf(7160000), Integer.valueOf(8141000), Integer.valueOf(8141100)}),
        Lord(PokemonItem.Black_Tornado, MobExp.FRUSTRATING, PokemonAbility.NaturalCure, PokemonAbility.Regenerator, new Integer[]{Integer.valueOf(7120106), Integer.valueOf(7120107), Integer.valueOf(8120102), Integer.valueOf(8120103), Integer.valueOf(8220012)}),
        Birk(null, MobExp.STANDARD, PokemonAbility.Synchronize, PokemonAbility.TintedLens, new Integer[]{Integer.valueOf(8140110), Integer.valueOf(8140111)}),
        Road_Auf(PokemonItem.Cold_Heart, MobExp.IMPOSSIBLE, PokemonAbility.Immunity, PokemonAbility.MarvelScale, new Integer[]{Integer.valueOf(7120109), Integer.valueOf(8220011)}),
        Road_Dunas(PokemonItem.Whirlwind, MobExp.IMPOSSIBLE, PokemonAbility.Immunity, PokemonAbility.MarvelScale, new Integer[]{Integer.valueOf(7120109), Integer.valueOf(8220010)}),
        PhantomGatekeeper(null, MobExp.FRUSTRATING, PokemonAbility.ShedSkin, PokemonAbility.ShadowTag, new Integer[]{Integer.valueOf(8142000), Integer.valueOf(8143000), Integer.valueOf(8160000)}),
        PhantomThanatos(null, MobExp.FRUSTRATING, PokemonAbility.ShedSkin, PokemonAbility.ShadowTag, new Integer[]{Integer.valueOf(8142000), Integer.valueOf(8143000), Integer.valueOf(8170000)}),
        Cerebes(null, MobExp.FRUSTRATING, PokemonAbility.RunAway, PokemonAbility.FlameBody, new Integer[]{Integer.valueOf(4230108), Integer.valueOf(7130001), Integer.valueOf(8140500)}),
        Guard(PokemonItem.Corrupted_Item, MobExp.IMPOSSIBLE, PokemonAbility.MagmaArmor, PokemonAbility.BattleArmor, new Integer[]{Integer.valueOf(8140511), Integer.valueOf(8140512)}),
        Ani_Strong(PokemonItem.Deathly_Fear, MobExp.IMPOSSIBLE, PokemonAbility.Intimidate, PokemonAbility.Gluttony, new Integer[]{Integer.valueOf(8840003), Integer.valueOf(8840004), Integer.valueOf(8210012)}),
        Ani_Weak(PokemonItem.Deathly_Fear, MobExp.IMPOSSIBLE, PokemonAbility.Intimidate, PokemonAbility.Stall, new Integer[]{Integer.valueOf(8840003), Integer.valueOf(8840004), Integer.valueOf(8210011)}),
        Von_Strong(PokemonItem.Deathly_Fear, MobExp.IMPOSSIBLE, PokemonAbility.Intimidate, PokemonAbility.Stall, new Integer[]{Integer.valueOf(8840003), Integer.valueOf(8840004), Integer.valueOf(8840000)}),
        Turtle(null, MobExp.FRUSTRATING, PokemonAbility.Stall, PokemonAbility.Analytic, new Integer[]{Integer.valueOf(8140700), Integer.valueOf(8140701)}),
        Ton(null, MobExp.FRUSTRATING, PokemonAbility.Stall, PokemonAbility.Analytic, new Integer[]{Integer.valueOf(8140702), Integer.valueOf(8140703)}),
        Maverick_Y(null, MobExp.FRUSTRATING, PokemonAbility.MotorDrive, PokemonAbility.MotorDrive, new Integer[]{Integer.valueOf(8120104), Integer.valueOf(8120105), Integer.valueOf(8120106)}),
        Maverick_B(null, MobExp.FRUSTRATING, PokemonAbility.MotorDrive, PokemonAbility.MotorDrive, new Integer[]{Integer.valueOf(8120104), Integer.valueOf(8120105), Integer.valueOf(8120106)}),
        Maverick_V(null, MobExp.FRUSTRATING, PokemonAbility.MotorDrive, PokemonAbility.MotorDrive, new Integer[]{Integer.valueOf(8120104), Integer.valueOf(8120105), Integer.valueOf(8120106)}),
        Maverick_S(null, MobExp.FRUSTRATING, PokemonAbility.MotorDrive, PokemonAbility.MotorDrive, new Integer[]{Integer.valueOf(8120104), Integer.valueOf(8120105), Integer.valueOf(8120106)}),
        Monk(null, MobExp.FRUSTRATING, PokemonAbility.SereneGrace, PokemonAbility.MagicGuard, new Integer[]{Integer.valueOf(8200001), Integer.valueOf(8200002), Integer.valueOf(8200005), Integer.valueOf(8200006), Integer.valueOf(8200009), Integer.valueOf(8200010)}),
        Dodo(PokemonItem.Maple_Marble, MobExp.IMPOSSIBLE, PokemonAbility.Truant, PokemonAbility.Stall, new Integer[]{Integer.valueOf(8200003), Integer.valueOf(8200004), Integer.valueOf(8220004)}),
        Lillinof(PokemonItem.Rainbow_Leaf, MobExp.IMPOSSIBLE, PokemonAbility.Defeatist, PokemonAbility.Stall, new Integer[]{Integer.valueOf(8200007), Integer.valueOf(8200008), Integer.valueOf(8220005)}),
        Raika(PokemonItem.Black_Hole, MobExp.IMPOSSIBLE, PokemonAbility.Defeatist, PokemonAbility.Stall, new Integer[]{Integer.valueOf(8200011), Integer.valueOf(8200012), Integer.valueOf(8220006)}),
        Guardian(null, MobExp.IMPOSSIBLE, PokemonAbility.Defeatist, PokemonAbility.Truant, new Integer[]{Integer.valueOf(8200003), Integer.valueOf(8200004), Integer.valueOf(8200007), Integer.valueOf(8200008), Integer.valueOf(8200011), Integer.valueOf(8200012)}),
        Cygnus_Boss(null, MobExp.EASY, PokemonAbility.WonderGuard, PokemonAbility.WonderGuard, new Integer[]{Integer.valueOf(8850011)}),
        Pink_Bean(null, MobExp.EASY, PokemonAbility.WonderGuard, PokemonAbility.WonderGuard, new Integer[]{Integer.valueOf(8820001)});
        public PokemonItem evoItem;
        public MobExp type;
        public PokemonAbility ability1;
        public PokemonAbility ability2;
        public List<Integer> evolutions;

        private PokemonMob(PokemonItem evoItem, MobExp type, PokemonAbility ability1, PokemonAbility ability2, Integer[] evo) {
            this.type = type;
            this.ability1 = ability1;
            this.ability2 = ability2;
            this.evoItem = evoItem;
            this.evolutions = Arrays.asList(evo);
        }
    }

    public static class PokedexEntry {

        public int id;
        public int num;
        public Battler dummyBattler;
        public Map<Integer, Integer> pre = new LinkedHashMap();
        public Map<Integer, Integer> evo = new LinkedHashMap();
        public List<Pair<Integer, Integer>> maps;

        public PokedexEntry(int id, int num) {
            this.id = id;
            this.num = num;
        }

        public List<Map.Entry<Integer, Integer>> getPre() {
            return new ArrayList(this.pre.entrySet());
        }

        public List<Map.Entry<Integer, Integer>> getEvo() {
            return new ArrayList(this.evo.entrySet());
        }
    }

    public static enum MobExp {

        EASY(0.07000000000000001D),
        ERRATIC(0.1D),
        FAST(0.13D),
        STANDARD(0.16D),
        SLOW(0.19D),
        FRUSTRATING(0.22D),
        IMPOSSIBLE(0.25D);
        public double value;

        private MobExp(double value) {
            this.value = value;
        }
    }

    public static enum Evolution {

        NONE(0),
        LEVEL(1),
        STONE(2);
        public int value;

        private Evolution(int value) {
            this.value = value;
        }
    }

    public static enum PokemonMap {

        MAP1(190000000, 1, 4, 2, new Point(20, 35), new Point(320, 35)),
        MAP2(190000001, 1, 4, 0, new Point(-220, 215), new Point(80, 215)),
        MAP3(190000002, 1, 4, 3, new Point(-400, 215), new Point(-100, 215)),
        MAP4(191000000, 6, 4, 7, new Point(130, 278), new Point(430, 278)),
        MAP5(191000001, 6, 4, 1, new Point(-90, -15), new Point(210, -30)),
        MAP6(192000000, 11, 4, 4, new Point(1100, 2205), new Point(1400, 2205)),
        MAP7(192000001, 11, 4, 4, new Point(1100, 2205), new Point(1400, 2205)),
        MAP8(195000000, 16, 4, 3, new Point(1500, 1294), new Point(1800, 1294)),
        MAP9(195010000, 16, 4, 2, new Point(300, 1659), new Point(0, 1659), true),
        MAP10(195020000, 16, 4, 1, new Point(70, -31), new Point(370, -31)),
        MAP11(195030000, 16, 4, 1, new Point(-200, 160), new Point(100, 160)),
        MAP12(196000000, 21, 4, 5, new Point(-700, -26), new Point(-400, -26)),
        MAP13(196010000, 21, 4, 1, new Point(100, 454), new Point(400, 454)),
        MAP14(197000000, 26, 4, 0, new Point(250, 132), new Point(550, 132)),
        MAP15(197010000, 26, 4, 2, new Point(-600, -78), new Point(-300, -78)),
        MAP16(600010000, 31, 4, 5, new Point(-950, -1307), new Point(-650, -1307)),
        MAP17(880000000, 36, 4, 4, new Point(-1300, 215), new Point(-1000, 215)),
        MAP18(881000000, 41, 4, 3, new Point(1500, -173), new Point(1800, -173)),
        MAP19(809020000, 46, 4, 0, new Point(400, 338), new Point(700, 338)),
        MAP21(922220000, 51, 4, 0, new Point(0, 153), new Point(300, 153)),
        MAP22(924000100, 56, 4, 0, new Point(-400, 422), new Point(-100, 422)),
        MAP23(925010300, 61, 4, 0, new Point(-533, 333), new Point(-233, 333)),
        MAP24(950000100, 66, 4, 0, new Point(1300, 275), new Point(1600, 275)),
        MAP25(970020001, 71, 4, 0, new Point(300, 155), new Point(600, 155)),
        MAP26(970020002, 76, 4, 0, new Point(-300, 543), new Point(0, 543)),
        MAP27(970020003, 81, 4, 0, new Point(-50, 181), new Point(250, 181)),
        MAP28(970020004, 86, 4, 0, new Point(-500, -18), new Point(-200, -18)),
        MAP29(910300000, 91, 4, 0, new Point(-100, -2131), new Point(200, -2131)),
        MAP32(910210000, 96, 4, 0, new Point(700, 165), new Point(1000, 165)),
        MAP33(910500000, 101, 9, 2, new Point(0, 130), new Point(300, 130)),
        MAP34(910500100, 111, 9, 0, new Point(-700, -200), new Point(-400, -200)),
        MAP35(910500200, 121, 9, 1, new Point(-100, -1130), new Point(200, -1130)),
        MAP36(922020100, 131, 19, 2, new Point(-1000, 42), new Point(-700, 42));
        public int id;
        public int minLevel;
        public int maxLevel;
        public int portalId;
        public boolean facingLeft;
        public Point pos0;
        public Point pos1;

        private PokemonMap(int id, int minLevel, int offset, int portalId, Point pos0, Point pos1) {
            this.id = id;
            this.minLevel = minLevel;
            this.portalId = portalId;
            this.maxLevel = (minLevel + offset);
            this.pos0 = pos0;
            this.pos1 = pos1;
            this.facingLeft = false;
        }

        private PokemonMap(int id, int minLevel, int offset, int portalId, Point pos0, Point pos1, boolean facingLeft) {
            this.id = id;
            this.minLevel = minLevel;
            this.portalId = portalId;
            this.maxLevel = (minLevel + offset);
            this.pos0 = pos0;
            this.pos1 = pos1;
            this.facingLeft = facingLeft;
        }
    }
}
