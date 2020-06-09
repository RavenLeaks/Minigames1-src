package me.wazup.eggwars;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

class CustomScoreboard {
   private Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
   private Objective objective;
   private HashMap scores = new HashMap();
   private boolean showHealth;
   private int originalSize;

   public CustomScoreboard(Eggwars var1, boolean var2, String var3, String... var4) {
      this.objective = this.scoreboard.registerNewObjective("EW", "dummy");
      this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
      this.objective.setDisplayName(this.format(var3));

      for(int var5 = 0; var5 < var4.length; ++var5) {
         while(this.scoreboard.getEntries().contains(var4[var5])) {
            var4[var5] = var4[var5] + " ";
         }

         String var6 = this.format(var4[var5]);
         int var7 = var4.length - var5;
         this.objective.getScore(var6).setScore(var7);
         this.scores.put(var7, var6);
      }

      this.originalSize = this.scores.size();
      this.showHealth = var2;
      if (var2) {
         Objective var8 = this.scoreboard.registerNewObjective("SW_HEALTH", "health");
         var8.setDisplaySlot(DisplaySlot.BELOW_NAME);
         var8.setDisplayName(ChatColor.DARK_RED + Enums.SPECIAL_CHARACTER.HEART.toString());
      }

   }

   public void update(String var1, String var2, boolean var3) {
      Iterator var5 = this.scoreboard.getEntries().iterator();

      while(var5.hasNext()) {
         String var4 = (String)var5.next();
         if (var4.contains(var1)) {
            int var6 = this.objective.getScore(var4).getScore();
            if (var3) {
               --var6;
            }

            if (this.scores.containsKey(var6)) {
               this.scoreboard.resetScores((String)this.scores.get(var6));

               while(this.scoreboard.getEntries().contains(var2)) {
                  var2 = var2 + " ";
               }

               String var7 = this.format(var2);
               this.objective.getScore(var7).setScore(var6);
               this.scores.put(var6, var7);
               break;
            }
         }
      }

   }

   public void update(String var1, int var2, boolean var3) {
      this.update(var1, String.valueOf(var2), var3);
   }

   public void registerLine(int var1, String var2) {
      while(this.scoreboard.getEntries().contains(var2)) {
         var2 = var2 + " ";
      }

      if (this.scores.containsKey(var1)) {
         ArrayList var3 = new ArrayList();
         Iterator var5 = this.scores.keySet().iterator();

         while(var5.hasNext()) {
            int var4 = (Integer)var5.next();
            if (var4 >= var1) {
               var3.add((String)this.scores.get(var4));
            }
         }

         var5 = var3.iterator();

         while(var5.hasNext()) {
            String var8 = (String)var5.next();
            int var6 = this.objective.getScore(var8).getScore() + 1;
            this.objective.getScore(var8).setScore(var6);
            this.scores.put(var6, var8);
         }
      }

      String var7 = this.format(var2);
      this.objective.getScore(var7).setScore(var1);
      this.scores.put(var1, var7);
   }

   public void deleteLine(int var1) {
      if (this.scores.containsKey(var1)) {
         this.scoreboard.resetScores((String)this.scores.get(var1));
         this.scores.remove(var1);
         ArrayList var2 = new ArrayList();
         Iterator var4 = this.scores.keySet().iterator();

         int var3;
         while(var4.hasNext()) {
            var3 = (Integer)var4.next();
            if (var3 > var1) {
               var2.add((String)this.scores.get(var3));
            }
         }

         var4 = var2.iterator();

         while(var4.hasNext()) {
            String var6 = (String)var4.next();
            int var5 = this.objective.getScore(var6).getScore() - 1;
            this.objective.getScore(var6).setScore(var5);
            this.scores.put(var5, var6);
         }

         var3 = 0;
         Iterator var8 = this.scores.keySet().iterator();

         while(var8.hasNext()) {
            int var7 = (Integer)var8.next();
            if (var7 > var3) {
               var3 = var7;
            }
         }

         this.scores.remove(var3);
      }

   }

   public int getCurrentSize() {
      return this.scores.size();
   }

   public int getOriginalSize() {
      return this.originalSize;
   }

   private String format(String var1) {
      return var1.length() > 16 ? (Bukkit.getBukkitVersion().contains("1.7") ? var1.substring(0, 16) : (var1.length() > 32 ? var1.substring(0, 32) : var1)) : var1;
   }

   public Team registerTeam(String var1) {
      return this.scoreboard.registerNewTeam(var1);
   }

   public Set getTeams() {
      return this.scoreboard.getTeams();
   }

   public void setName(String var1) {
      this.objective.setDisplayName(var1);
   }

   public void apply(Player var1) {
      var1.setScoreboard(this.scoreboard);
      if (this.showHealth) {
         var1.setHealth(var1.getHealth() - 1.0E-4D);
      }

   }
}
