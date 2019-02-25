/*    */ package server.maps;
/*    */ 
/*    */ public enum SavedLocationType
/*    */ {
/*  5 */   FREE_MARKET(0), 
/*  6 */   MULUNG_TC(1), 
/*  7 */   WORLDTOUR(2), 
/*  8 */   FLORINA(3), 
/*  9 */   FISHING(4), 
/* 10 */   RICHIE(5), 
/* 11 */   DONGDONGCHIANG(6), 
/* 12 */   EVENT(7), 
/* 13 */   AMORIA(8), 
/* 14 */   CHRISTMAS(9), 
/* 15 */   ARDENTMILL(10), 
/* 16 */   TURNEGG(11), 
/* 17 */   PVP(12), 
/* 18 */   GUILD(13);
/*    */ 
/*    */   private int index;
/*    */ 
/* 22 */   private SavedLocationType(int index) { this.index = index; }
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 26 */     return this.index;
/*    */   }
/*    */ 
/*    */   public static SavedLocationType fromString(String Str) {
/* 30 */     return valueOf(Str);
/*    */   }
/*    */ }

/* Location:           F:\√∞œ’µ∫\MyTools\CrackMeOK\
 * Qualified Name:     server.maps.SavedLocationType
 * JD-Core Version:    0.6.0
 */