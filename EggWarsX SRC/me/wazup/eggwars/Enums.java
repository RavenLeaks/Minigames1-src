package me.wazup.eggwars;

import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.ChatColor;

public class Enums {
   protected static enum AchievementType {
      KILLS("Get %x% kills!", 10, new int[]{10, 30, 50, 100, 150, 200, 500, 1000}),
      WINS("Win %x% game(s)!", 100, new int[]{1, 5, 10, 20, 50, 100}),
      PROJECTILES_LAUNCHED("Launch %x% projectile!", 1, new int[]{50, 100, 500, 1000, 3000}),
      PROJECTILES_HIT("Hit %x% projectile!", 2, new int[]{25, 50, 100, 250, 500, 1000}),
      PLAYER_RANK("Reach rank %x%!", 100, new int[]{5, 10, 15, 20, 25, 30}),
      BLOCKS_PLACED("Place %x% block(s)!", 1, new int[]{50, 200, 400, 1000, 2000}),
      BLOCKS_BROKEN("Break %x% block(s)!", 1, new int[]{50, 200, 400, 1000, 2000}),
      EGGS_BROKEN("Break %x% egg(s)!", 50, new int[]{1, 10, 25, 50, 100});

      String defaultDescription;
      int prizeMultiplier;
      int[] levels;

      private AchievementType(String var3, int var4, int... var5) {
         this.defaultDescription = var3;
         this.prizeMultiplier = var4;
         this.levels = var5;
      }
   }

   protected static enum ArenaState {
      WAITING((String)null, true),
      STARTING((String)null, true),
      INGAME((String)null, false),
      ENDING((String)null, false),
      ROLLBACKING((String)null, false),
      QUEUED((String)null, false),
      DISABLED((String)null, false);

      String value;
      private boolean available;

      private ArenaState(String var3, boolean var4) {
         this.value = var3;
         this.available = var4;
      }

      public String toString() {
         return this.value;
      }

      public boolean AVAILABLE() {
         return this.available;
      }
   }

   protected static enum PartyPrivacy {
      INVITE(ChatColor.RED + "Invite only"),
      PUBLIC(ChatColor.GREEN + "Public");

      String value;

      private PartyPrivacy(String var3) {
         this.value = var3;
      }

      public String toString() {
         return this.value;
      }
   }

   protected static enum Rarity {
      COMMON(ChatColor.GREEN, "Common"),
      RARE(ChatColor.BLUE, "Rare"),
      LEGENDARY(ChatColor.GOLD, "Legendary");

      private String value;
      private ChatColor c;

      private Rarity(ChatColor var3, String var4) {
         this.value = var3 + var4;
         this.c = var3;
      }

      public String toString() {
         return this.value;
      }

      public ChatColor getColor() {
         return this.c;
      }
   }

   protected static enum SPECIAL_CHARACTER {
      ARROW(StringEscapeUtils.unescapeJava("➝")),
      HEART(StringEscapeUtils.unescapeJava("❤")),
      STAR(StringEscapeUtils.unescapeJava("✪")),
      POINTED_STAR(StringEscapeUtils.unescapeJava("✦")),
      CHECK_MARK(StringEscapeUtils.unescapeJava("✔")),
      XMARK(StringEscapeUtils.unescapeJava("✘"));

      private String value;

      private SPECIAL_CHARACTER(String var3) {
         this.value = var3;
      }

      public String toString() {
         return this.value;
      }
   }

   public static enum Stat {
      KILLS(0),
      COINS(1),
      DEATHS(2),
      WINS(3),
      MODIFIER(4),
      PROJECTILES_HIT(6),
      PLAYER_EXP(7),
      BLOCKS_PLACED(8),
      BLOCKS_BROKEN(9),
      EGGS_BROKEN(10);

      int id;

      private Stat(int var3) {
         this.id = var3;
      }

      public static Enums.Stat getByName(String var0) {
         Enums.Stat[] var4;
         int var3 = (var4 = values()).length;

         for(int var2 = 0; var2 < var3; ++var2) {
            Enums.Stat var1 = var4[var2];
            if (var1.name().equals(var0)) {
               return var1;
            }
         }

         return null;
      }
   }
}
