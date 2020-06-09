package me.wazup.skywars;

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

   public CustomScoreboard(Skywars var1, boolean var2, String var3, String... var4) {
      this.objective = this.scoreboard.registerNewObjective("SW", "dummy");
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

   public void update(String var1, int var2, boolean var3) {
      this.update(var1, String.valueOf(var2), var3);
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
