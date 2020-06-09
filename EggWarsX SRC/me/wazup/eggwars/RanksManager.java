package me.wazup.eggwars;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

class RanksManager {
   HashMap ranks = new HashMap();
   private Eggwars plugin;

   public RanksManager(Eggwars var1) {
      this.plugin = var1;
      int var2 = var1.config.first_rank_exp;
      this.ranks.put(1, var2);

      for(int var3 = 2; var3 < var1.config.max_rank + 1; ++var3) {
         int var4 = (int)((double)var2 * (var3 < 9 ? var1.config.rank_modifier + 0.2D : var1.config.rank_modifier));
         this.ranks.put(var3, var4);
         var2 = var4;
      }

   }

   public int getNextRankExp(PlayerData var1) {
      return var1.player_rank < this.plugin.config.max_rank ? (Integer)this.ranks.get(var1.player_rank + 1) - var1.player_exp : 0;
   }

   public void addExp(Player var1, int var2) {
      PlayerData var3 = (PlayerData)this.plugin.playerData.get(var1.getName());
      var3.player_exp += var2;
      if (this.getNextRankExp(var3) < 1 && var3.player_rank < this.plugin.config.max_rank) {
         ++var3.player_rank;
         var1.sendMessage(((String)this.plugin.customization.messages.get("Player-Rank-Up")).replace("%rank%", String.valueOf(var3.player_rank)));
         this.plugin.achievementsManager.checkPlayer(var1, Enums.AchievementType.PLAYER_RANK, var3.player_rank);
         if (this.plugin.config.rankCommands.containsKey(var3.player_rank)) {
            Iterator var5 = ((List)this.plugin.config.rankCommands.get(var3.player_rank)).iterator();

            while(var5.hasNext()) {
               String var4 = (String)var5.next();
               Bukkit.dispatchCommand(Bukkit.getConsoleSender(), var4.replace("%player%", var1.getName()));
            }
         }
      }

   }

   public int getRank(PlayerData var1) {
      int var2 = 0;
      Iterator var4 = this.ranks.keySet().iterator();

      while(var4.hasNext()) {
         int var3 = (Integer)var4.next();
         if (var1.player_exp >= (Integer)this.ranks.get(var3)) {
            var2 = var3;
         }
      }

      return var2;
   }
}
