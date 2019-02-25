package server;

import constants.BattleConstants.HoldItem;
import constants.BattleConstants.PItem;
import constants.BattleConstants.PokemonItem;
import constants.GameConstants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomRewards {

    private static List<Integer> compiledGold = null;
    private static List<Integer> compiledSilver = null;
    private static List<Integer> compiledFishing = null;
    private static List<Integer> compiledPeanut = null;
    private static List<Integer> compiledEvent = null;
    private static List<Integer> compiledEventC = null;
    private static List<Integer> compiledEventB = null;
    private static List<Integer> compiledEventA = null;
    private static List<Integer> compiledPokemon = null;
    private static List<Integer> compiledDrops = null;
    private static List<Integer> compiledDropsB = null;
    private static List<Integer> compiledDropsA = null;
    private static List<Integer> tenPercent = null;
    private static List<Integer> 水晶八音盒 = null;
    private static List<Integer> 所有椅子 = null;
    private static List<Integer> 水晶天平 = null;
 //   private static List<Integer> 所有纪念币s = null;

    static {
        List returnArray = new ArrayList();

        processRewards(returnArray, GameConstants.水晶八音盒s);

        水晶八音盒 = returnArray;

        returnArray = new ArrayList();
        
        processRewards(returnArray, GameConstants.所有椅子s);

        所有椅子 = returnArray;

        returnArray = new ArrayList();
        
        processRewards(returnArray, GameConstants.水晶天平s);

        水晶天平 = returnArray;

        returnArray = new ArrayList();
        
       // processRewards(returnArray, GameConstants.所有纪念币);

     //   所有纪念币s = returnArray;

       // returnArray = new ArrayList();

        processRewards(returnArray, GameConstants.goldrewards);

        compiledGold = returnArray;

        returnArray = new ArrayList();

        processRewards(returnArray, GameConstants.silverrewards);

        compiledSilver = returnArray;

        returnArray = new ArrayList();

        processRewards(returnArray, GameConstants.fishingReward);

        compiledFishing = returnArray;

        returnArray = new ArrayList();

        processRewards(returnArray, GameConstants.eventCommonReward);

        compiledEventC = returnArray;

        returnArray = new ArrayList();

        processRewards(returnArray, GameConstants.eventUncommonReward);

        compiledEventB = returnArray;

        returnArray = new ArrayList();

        processRewards(returnArray, GameConstants.eventRareReward);
        processRewardsSimple(returnArray, GameConstants.tenPercent);
        processRewardsSimple(returnArray, GameConstants.tenPercent);

        compiledEventA = returnArray;

        returnArray = new ArrayList();

        processRewards(returnArray, GameConstants.eventSuperReward);

        compiledEvent = returnArray;

        returnArray = new ArrayList();

        processRewards(returnArray, GameConstants.peanuts);

        compiledPeanut = returnArray;

        returnArray = new ArrayList();

        processPokemon(returnArray, PokemonItem.values());
        processPokemon(returnArray, HoldItem.values());

        compiledPokemon = returnArray;

        returnArray = new ArrayList();

        processRewardsSimple(returnArray, GameConstants.normalDrops);

        compiledDrops = returnArray;

        returnArray = new ArrayList();

        processRewardsSimple(returnArray, GameConstants.rareDrops);

        compiledDropsB = returnArray;

        returnArray = new ArrayList();

        processRewardsSimple(returnArray, GameConstants.superDrops);

        compiledDropsA = returnArray;

        returnArray = new ArrayList();

        processRewardsSimple(returnArray, GameConstants.tenPercent);

        tenPercent = returnArray;
    }

    private static void processRewards(List<Integer> returnArray, int[] list) {
        int lastitem = 0;
        for (int i = 0; i < list.length; i++) {
            if (i % 2 == 0) {
                lastitem = list[i];
            } else {
                for (int j = 0; j < list[i]; j++) {
                    returnArray.add(Integer.valueOf(lastitem));
                }
            }
        }
        Collections.shuffle(returnArray);
    }

    private static void processRewardsSimple(List<Integer> returnArray, int[] list) {
        for (int i = 0; i < list.length; i++) {
            returnArray.add(Integer.valueOf(list[i]));
        }
        Collections.shuffle(returnArray);
    }

    private static void processPokemon(List<Integer> returnArray, PItem[] list) {
        for (int i = 0; i < list.length; i++) {
            PItem lastitem = list[i];
            for (int j = 0; j < lastitem.getItemChance(); j++) {
                returnArray.add(Integer.valueOf(lastitem.getId()));
            }
        }
        Collections.shuffle(returnArray);
    }
    
    public static int get水晶八音盒() {
        return ((Integer) 水晶八音盒.get(Randomizer.nextInt(水晶八音盒.size()))).intValue();
    }
    
    public static int get所有椅子() {
        return ((Integer) 所有椅子.get(Randomizer.nextInt(所有椅子.size()))).intValue();
    }
    
    public static int get水晶天平() {
        return ((Integer) 水晶天平.get(Randomizer.nextInt(水晶天平.size()))).intValue();
    }
    /*
    public static int get所有纪念币() {
        return ((Integer) 所有纪念币s.get(Randomizer.nextInt(所有纪念币s.size()))).intValue();
    }
    * 
    */
    

    public static int getGoldBoxReward() {
        return ((Integer) compiledGold.get(Randomizer.nextInt(compiledGold.size()))).intValue();
    }

    public static int getSilverBoxReward() {
        return ((Integer) compiledSilver.get(Randomizer.nextInt(compiledSilver.size()))).intValue();
    }

    public static int getFishingReward() {
        return ((Integer) compiledFishing.get(Randomizer.nextInt(compiledFishing.size()))).intValue();
    }

    public static int getPeanutReward() {
        return ((Integer) compiledPeanut.get(Randomizer.nextInt(compiledPeanut.size()))).intValue();
    }

    public static int getPokemonReward() {
        return ((Integer) compiledPokemon.get(Randomizer.nextInt(compiledPokemon.size()))).intValue();
    }

    public static int getEventReward() {
        int chance = Randomizer.nextInt(101);
        if (chance < 66) {
            return ((Integer) compiledEventC.get(Randomizer.nextInt(compiledEventC.size()))).intValue();
        }
        if (chance < 86) {
            return ((Integer) compiledEventB.get(Randomizer.nextInt(compiledEventB.size()))).intValue();
        }
        if (chance < 96) {
            return ((Integer) compiledEventA.get(Randomizer.nextInt(compiledEventA.size()))).intValue();
        }
        return ((Integer) compiledEvent.get(Randomizer.nextInt(compiledEvent.size()))).intValue();
    }

    public static int getDropReward() {
        int chance = Randomizer.nextInt(101);
        if (chance < 76) {
            return ((Integer) compiledDrops.get(Randomizer.nextInt(compiledDrops.size()))).intValue();
        }
        if (chance < 96) {
            return ((Integer) compiledDropsB.get(Randomizer.nextInt(compiledDropsB.size()))).intValue();
        }
        return ((Integer) compiledDropsA.get(Randomizer.nextInt(compiledDropsA.size()))).intValue();
    }

    public static List<Integer> getTenPercent() {
        return tenPercent;
    }

    static void load() {
    }
}