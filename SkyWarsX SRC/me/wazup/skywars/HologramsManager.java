package me.wazup.skywars;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

class HologramsManager {
   Hologram leaderboardHologram;
   Location stats;
   Location leaderboard;
   private Skywars plugin;

   public HologramsManager(Skywars var1) {
      this.plugin = var1;
   }

   public void setStats(Location var1) {
      this.stats = var1;
      Iterator var3 = this.plugin.getPlayers(this.plugin.players).iterator();

      while(var3.hasNext()) {
         Player var2 = (Player)var3.next();
         PlayerData var4 = (PlayerData)this.plugin.playerData.get(var2.getName());
         if (var4.hologram != null) {
            if (var1 != null) {
               var4.hologram.teleport(var1);
            } else {
               var4.hologram.delete();
               var4.hologram = null;
            }
         } else {
            this.createStats(var2, var4);
         }
      }

   }

   public void createStats(Player var1, PlayerData var2) {
      if (this.stats != null) {
         if (var2.hologram != null) {
            var2.hologram.delete();
         }

         var2.hologram = HologramsAPI.createHologram(this.plugin, this.stats);
         var2.hologram.getVisibilityManager().showTo(var1);
         var2.hologram.getVisibilityManager().setVisibleByDefault(false);
         var2.hologram.appendTextLine("" + ChatColor.GRAY + ChatColor.STRIKETHROUGH + "---" + ChatColor.GREEN + " " + var1.getName() + "'s" + ChatColor.GRAY + " stats " + ChatColor.GRAY + ChatColor.STRIKETHROUGH + "---");
         var2.hologram.appendTextLine(ChatColor.AQUA + "Kills " + ChatColor.GRAY + Enums.SPECIAL_CHARACTER.ARROW + " " + ChatColor.YELLOW + var2.kills);
         var2.hologram.appendTextLine(ChatColor.AQUA + "Coins " + ChatColor.GRAY + Enums.SPECIAL_CHARACTER.ARROW + " " + ChatColor.YELLOW + var2.getCoins(var1));
         var2.hologram.appendTextLine(ChatColor.AQUA + "Deaths " + ChatColor.GRAY + Enums.SPECIAL_CHARACTER.ARROW + " " + ChatColor.YELLOW + var2.deaths);
         var2.hologram.appendTextLine(ChatColor.AQUA + "Wins " + ChatColor.GRAY + Enums.SPECIAL_CHARACTER.ARROW + " " + ChatColor.YELLOW + var2.wins);
         var2.hologram.appendTextLine(ChatColor.AQUA + "Projectiles launched " + ChatColor.GRAY + Enums.SPECIAL_CHARACTER.ARROW + " " + ChatColor.YELLOW + var2.projectiles_launched);
         var2.hologram.appendTextLine(ChatColor.AQUA + "Projectiles hit " + ChatColor.GRAY + Enums.SPECIAL_CHARACTER.ARROW + " " + ChatColor.YELLOW + var2.projectiles_hit);
         var2.hologram.appendTextLine(ChatColor.AQUA + "Exp " + ChatColor.GRAY + Enums.SPECIAL_CHARACTER.ARROW + " " + ChatColor.YELLOW + var2.player_exp);
         var2.hologram.appendTextLine(ChatColor.AQUA + "Rank " + ChatColor.GRAY + Enums.SPECIAL_CHARACTER.ARROW + " " + ChatColor.YELLOW + var2.player_rank);
         var2.hologram.appendTextLine(ChatColor.AQUA + "Blocks placed " + ChatColor.GRAY + Enums.SPECIAL_CHARACTER.ARROW + " " + ChatColor.YELLOW + var2.blocks_placed);
         var2.hologram.appendTextLine(ChatColor.AQUA + "Blocks broken " + ChatColor.GRAY + Enums.SPECIAL_CHARACTER.ARROW + " " + ChatColor.YELLOW + var2.blocks_broken);
         var2.hologram.appendTextLine(ChatColor.AQUA + "Items enchanted " + ChatColor.GRAY + Enums.SPECIAL_CHARACTER.ARROW + " " + ChatColor.YELLOW + var2.items_enchanted);
         var2.hologram.appendTextLine(ChatColor.AQUA + "Items crafted " + ChatColor.GRAY + Enums.SPECIAL_CHARACTER.ARROW + " " + ChatColor.YELLOW + var2.items_crafted);
         var2.hologram.appendTextLine(ChatColor.AQUA + "Fishes caught " + ChatColor.GRAY + Enums.SPECIAL_CHARACTER.ARROW + " " + ChatColor.YELLOW + var2.fishes_caught);
         var2.hologram.appendTextLine("" + ChatColor.GRAY + ChatColor.STRIKETHROUGH + "----------------------");
      }
   }

   public void setLeaderboard(Location var1, boolean var2) {
      this.leaderboard = var1;
      if (var1 == null) {
         if (this.leaderboardHologram != null) {
            this.leaderboardHologram.delete();
            this.leaderboardHologram = null;
         }

      } else if (this.leaderboardHologram != null) {
         this.leaderboardHologram.teleport(var1);
      } else {
         this.leaderboardHologram = HologramsAPI.createHologram(this.plugin, this.leaderboard);
         this.leaderboardHologram.appendTextLine(ChatColor.AQUA + "Loading...");
         if (var2) {
            this.plugin.startLeaderboardUpdater();
         }

      }
   }

   public void updateLeaderboard(HashMap var1) {
      if (this.leaderboardHologram != null) {
         this.leaderboardHologram.delete();
      }

      this.leaderboardHologram = HologramsAPI.createHologram(this.plugin, this.leaderboard);
      Iterator var3 = var1.keySet().iterator();

      while(var3.hasNext()) {
         Enums.Stat var2 = (Enums.Stat)var3.next();
         Entry var4 = (Entry)var1.get(var2);
         this.leaderboardHologram.appendTextLine(ChatColor.GRAY + "#" + ChatColor.LIGHT_PURPLE + "1 " + ChatColor.AQUA + var2.name() + ChatColor.GRAY + " - " + (String)var4.getKey() + " - " + ChatColor.YELLOW + var4.getValue());
      }

      this.leaderboardHologram.appendTextLine("" + ChatColor.GRAY + ChatColor.STRIKETHROUGH + "-------------------------");
   }
}
