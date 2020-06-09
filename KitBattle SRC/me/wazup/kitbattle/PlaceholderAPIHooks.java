package me.wazup.kitbattle;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PlaceholderAPIHooks extends PlaceholderExpansion {
   private Kitbattle plugin;

   public PlaceholderAPIHooks(Plugin var1) {
      this.plugin = (Kitbattle)var1;
   }

   public boolean persist() {
      return true;
   }

   public boolean canRegister() {
      return true;
   }

   public String onPlaceholderRequest(Player var1, String var2) {
      if (var2.equals("players_count")) {
         return String.valueOf(this.plugin.players.size());
      } else if (var2.equals("maps_count")) {
         return String.valueOf(this.plugin.playingMaps.size() + this.plugin.tournamentMaps.size() + this.plugin.challengeMaps.size());
      } else if (var2.equals("challengers_count")) {
         return String.valueOf(this.plugin.challengesManager != null ? this.plugin.challengesManager.players.size() : 0);
      } else if (var2.equals("tournament_participants_count")) {
         return String.valueOf(this.plugin.tournamentsManager != null ? this.plugin.tournamentsManager.getSize() : 0);
      } else if (var2.equals("kits_count")) {
         return String.valueOf(this.plugin.Kits.size());
      } else if (var2.equals("ranks_count")) {
         return String.valueOf(this.plugin.Ranks.size());
      } else if (var1 == null) {
         return "";
      } else {
         PlayerData var3 = (PlayerData)this.plugin.playerData.get(var1.getName());
         return this.plugin.getPlaceholderValue(var1, var3, var2);
      }
   }

   public String getAuthor() {
      return this.plugin.getDescription().getAuthors().toString();
   }

   public String getIdentifier() {
      return "kitbattle";
   }

   public String getVersion() {
      return this.plugin.getDescription().getVersion();
   }
}
