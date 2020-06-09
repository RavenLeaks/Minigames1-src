package me.wazup.kitbattle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class CustomScoreboard {
   private Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
   private Objective objective;
   private HashMap orginalLines = new HashMap();
   private HashMap placeholders = new HashMap();
   private HashMap multiplePlaceholders = new HashMap();
   private boolean showHealth;

   public CustomScoreboard(Kitbattle var1, boolean var2, String var3, String... var4) {
      if (var1.config.ScoreboardEnabled) {
         this.objective = this.scoreboard.registerNewObjective("KB", "dummy");
         this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
         this.objective.setDisplayName(this.format(var3));

         for(int var5 = 0; var5 < var4.length; ++var5) {
            while(this.scoreboard.getEntries().contains(var4[var5])) {
               var4[var5] = var4[var5] + " ";
            }

            String var6 = var4[var5];
            String var7 = this.format(var4[var5]);
            int var8 = var4.length - var5;
            this.objective.getScore(var7).setScore(var8);
            this.orginalLines.put(var8, var6);
            int var9 = this.getCharacterCount(var6, '%');
            if (var9 > 0 && var9 % 2 == 0) {
               if (var9 / 2 > 1) {
                  ArrayList var10 = new ArrayList();

                  for(int var11 = 1; var11 < var9; var11 += 2) {
                     var10.add(var6.split("%")[var11]);
                  }

                  this.multiplePlaceholders.put(var8, var10);
               }

               for(int var13 = 1; var13 < var9; var13 += 2) {
                  this.placeholders.put(var6.split("%")[var13], var8);
               }
            }
         }
      }

      this.showHealth = var2;
      if (var2) {
         Objective var12 = this.scoreboard.registerNewObjective("KB_HEALTH", "health");
         var12.setDisplaySlot(DisplaySlot.BELOW_NAME);
         var12.setDisplayName(ChatColor.DARK_RED + var1.character_heart);
      }

   }

   public void updatePlaceholder(Player var1, Kitbattle var2, String var3, String var4) {
      if (this.placeholders.containsKey(var3)) {
         int var5 = (Integer)this.placeholders.get(var3);
         Iterator var7 = this.scoreboard.getEntries().iterator();

         while(var7.hasNext()) {
            String var6 = (String)var7.next();
            if (this.objective.getScore(var6).getScore() == var5) {
               this.scoreboard.resetScores(var6);
               String var8 = (String)this.orginalLines.get(var5);
               if (this.multiplePlaceholders.containsKey(var5)) {
                  List var9 = (List)this.multiplePlaceholders.get(var5);

                  for(int var10 = 0; var10 < var9.size(); ++var10) {
                     var8 = var8.replace("%" + (String)var9.get(var10) + "%", var2.getPlaceholderValue(var1, (PlayerData)var2.playerData.get(var1.getName()), (String)var9.get(var10)));
                  }
               } else {
                  var8 = var8.replace("%" + var3 + "%", var4);
               }

               while(this.scoreboard.getEntries().contains(var8)) {
                  var8 = var8 + " ";
               }

               this.objective.getScore(this.format(var8)).setScore(var5);
               break;
            }
         }

      }
   }

   public void updatePlaceholder(Player var1, Kitbattle var2, String var3, int var4) {
      this.updatePlaceholder(var1, var2, var3, String.valueOf(var4));
   }

   private int getCharacterCount(String var1, char var2) {
      int var3 = 0;
      char[] var7;
      int var6 = (var7 = var1.toCharArray()).length;

      for(int var5 = 0; var5 < var6; ++var5) {
         char var4 = var7[var5];
         if (var4 == var2) {
            ++var3;
         }
      }

      return var3;
   }

   public void replace(String var1, String var2, boolean var3) {
      Iterator var5 = this.scoreboard.getEntries().iterator();

      while(var5.hasNext()) {
         String var4 = (String)var5.next();
         if (var4.contains(var1)) {
            int var6 = this.objective.getScore(var4).getScore();
            if (var3) {
               --var6;
            }

            this.scoreboard.resetScores(this.getEntry(var6));

            while(this.scoreboard.getEntries().contains(var2)) {
               var2 = var2 + " ";
            }

            String var7 = this.format(var2);
            this.objective.getScore(var7).setScore(var6);
            break;
         }
      }

   }

   public void replace(String var1, int var2, boolean var3) {
      this.replace(var1, String.valueOf(var2), var3);
   }

   public void registerLine(int var1, String var2) {
      while(this.scoreboard.getEntries().contains(var2)) {
         var2 = var2 + " ";
      }

      if (this.getEntry(var1) != null) {
         ArrayList var3 = new ArrayList();
         Iterator var5 = this.scoreboard.getEntries().iterator();

         String var4;
         while(var5.hasNext()) {
            var4 = (String)var5.next();
            if (this.objective.getScore(var4).getScore() >= var1) {
               var3.add(var4);
            }
         }

         var5 = var3.iterator();

         while(var5.hasNext()) {
            var4 = (String)var5.next();
            this.objective.getScore(var4).setScore(this.objective.getScore(var4).getScore() + 1);
         }
      }

      this.objective.getScore(this.format(var2)).setScore(var1);
   }

   public void deleteLine(int var1) {
      String var2 = this.getEntry(var1);
      if (var2 != null) {
         this.scoreboard.resetScores(var2);
         ArrayList var3 = new ArrayList();
         Iterator var5 = this.scoreboard.getEntries().iterator();

         String var4;
         while(var5.hasNext()) {
            var4 = (String)var5.next();
            if (this.objective.getScore(var4).getScore() > var1) {
               var3.add(var4);
            }
         }

         var5 = var3.iterator();

         while(var5.hasNext()) {
            var4 = (String)var5.next();
            this.objective.getScore(var4).setScore(this.objective.getScore(var4).getScore() - 1);
         }

      }
   }

   private String getEntry(int var1) {
      Iterator var3 = this.scoreboard.getEntries().iterator();

      while(var3.hasNext()) {
         String var2 = (String)var3.next();
         if (this.objective.getScore(var2).getScore() == var1) {
            return var2;
         }
      }

      return null;
   }

   private String format(String var1) {
      return var1.length() > 16 ? (Bukkit.getBukkitVersion().contains("1.7") ? var1.substring(0, 16) : (var1.length() > 32 ? var1.substring(0, 32) : var1)) : var1;
   }

   public void setName(String var1) {
      this.objective.setDisplayName(var1);
   }

   public Team registerTeam(String var1) {
      return this.scoreboard.registerNewTeam(var1);
   }

   public Set getTeams() {
      return this.scoreboard.getTeams();
   }

   public void apply(Player var1) {
      var1.setScoreboard(this.scoreboard);
      if (this.showHealth && !var1.isDead()) {
         var1.setHealth(var1.getHealth() - 1.0E-4D);
      }

   }
}
