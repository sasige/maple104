 package client.inventory;
 
import java.util.Arrays;
import java.util.List;

 /**
  * 
  * @author mary
  */
 
 public enum EquipAdditions {
   elemboost("elemVol", "elemVol", true), 
   mobcategory("category", "damage"), 
   critical("prob", "damage"), 
   boss("prob", "damage"), 
   mobdie("hpIncOnMobDie", "mpIncOnMobDie"), 
   hpmpchange("hpChangerPerTime", "mpChangerPerTime"), 
   skill("id", "level");
 
   private final String i1;
   private final String i2;
   private final boolean element;
 
   private EquipAdditions(String i1, String i2) { this.i1 = i1;
     this.i2 = i2;
     this.element = false; }
 
   private EquipAdditions(String i1, String i2, boolean element) {
     this.i1 = i1;
     this.i2 = i2;
     this.element = element;
   }
 
   public String getValue1() {
     return this.i1;
   }
 
   public String getValue2() {
     return this.i2;
   }
 
   public boolean isElement() {
     return this.element;
   }
 
   public static EquipAdditions fromString(String str) {
     for (EquipAdditions s : values()) {
       if (s.name().equalsIgnoreCase(str)) {
         return s;
       }
     }
     return null;
   }
 
   public static enum RingSet {
     Source_Ring(new Integer[] { 
         Integer.valueOf(1112435),// 一代不速之客戒指
         Integer.valueOf(1112436),// 二代不速之客戒指
         Integer.valueOf(1112437),// 三代不速之客戒指 
         Integer.valueOf(1112438),// 末代不速之客戒指 
         Integer.valueOf(1112439) }),// 至尊不速之客戒指 
     
     Angelic_Ring(new Integer[] { 
         Integer.valueOf(1112585), //天使的祝福
         Integer.valueOf(1112586), //黑天使的祝福
         Integer.valueOf(1112594), //雪花天使的祝福
         Integer.valueOf(1112663) }), //白天使的祝福
     
     Job_Ring(new Integer[] { 
         Integer.valueOf(1112427),//冒险家残酷之戒
         Integer.valueOf(1112428),//冒险家暴击之戒 
         Integer.valueOf(1112429),//冒险家魔法之戒 
         Integer.valueOf(1112405),//利琳的戒指 
         Integer.valueOf(1112445),//反抗者戒指 
         Integer.valueOf(1112591),//反抗者勒克斯戒指 
         Integer.valueOf(1112592) }),//利琳的灵气戒指 
     
     Evolving_Ring(new Integer[] { 
         Integer.valueOf(1112499), 
         Integer.valueOf(1112500), 
         Integer.valueOf(1112501), 
         Integer.valueOf(1112502), 
         Integer.valueOf(1112503), 
         Integer.valueOf(1112504), 
         Integer.valueOf(1112505), 
         Integer.valueOf(1112506), 
         Integer.valueOf(1112507), 
         Integer.valueOf(1112508), 
         Integer.valueOf(1112509), 
         Integer.valueOf(1112510), 
         Integer.valueOf(1112511), 
         Integer.valueOf(1112512), 
         Integer.valueOf(1112513), 
         Integer.valueOf(1112514), 
         Integer.valueOf(1112515), 
         Integer.valueOf(1112516), 
         Integer.valueOf(1112517), 
         Integer.valueOf(1112518), 
         Integer.valueOf(1112519), 
         Integer.valueOf(1112520), 
         Integer.valueOf(1112521), 
         Integer.valueOf(1112522), 
         Integer.valueOf(1112523), 
         Integer.valueOf(1112524), 
         Integer.valueOf(1112525), 
         Integer.valueOf(1112526), 
         Integer.valueOf(1112527), 
         Integer.valueOf(1112528), 
         Integer.valueOf(1112529), 
         Integer.valueOf(1112530), 
         Integer.valueOf(1112531), 
         Integer.valueOf(1112532), 
         Integer.valueOf(1112533) }), 
     
     Evolving_Ring_II(new Integer[] { 
         Integer.valueOf(1112614), 
         Integer.valueOf(1112615), 
         Integer.valueOf(1112616), 
         Integer.valueOf(1112617), 
         Integer.valueOf(1112618), 
         Integer.valueOf(1112619), 
         Integer.valueOf(1112620), 
         Integer.valueOf(1112621), 
         Integer.valueOf(1112622), 
         Integer.valueOf(1112623), 
         Integer.valueOf(1112624), 
         Integer.valueOf(1112625), 
         Integer.valueOf(1112626), 
         Integer.valueOf(1112627), 
         Integer.valueOf(1112628), 
         Integer.valueOf(1112629), 
         Integer.valueOf(1112630), 
         Integer.valueOf(1112631), 
         Integer.valueOf(1112632), 
         Integer.valueOf(1112633), 
         Integer.valueOf(1112634), 
         Integer.valueOf(1112635), 
         Integer.valueOf(1112636), 
         Integer.valueOf(1112637), 
         Integer.valueOf(1112638), 
         Integer.valueOf(1112639), 
         Integer.valueOf(1112640), 
         Integer.valueOf(1112641), 
         Integer.valueOf(1112642), 
         Integer.valueOf(1112643), 
         Integer.valueOf(1112644), 
         Integer.valueOf(1112645), 
         Integer.valueOf(1112646), 
         Integer.valueOf(1112647), 
         Integer.valueOf(1112648) });
 
     public List<Integer> id;
 
     private RingSet(Integer[] ids) { this.id = Arrays.asList(ids);
     }
   }
 }