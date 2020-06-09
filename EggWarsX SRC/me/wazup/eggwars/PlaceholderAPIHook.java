package me.wazup.eggwars;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Team;

public class PlaceholderAPIHook extends PlaceholderExpansion {
   private Eggwars plugin;

   public PlaceholderAPIHook(Plugin var1) {
      this.plugin = (Eggwars)var1;
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
      } else if (var2.equals("parties_count")) {
         return String.valueOf(this.plugin.parties.size());
      } else if (var2.equals("lobby_players_count")) {
         return String.valueOf(this.plugin.lobbyPlayers.size());
      } else if (var2.equals("arenas_count")) {
         return String.valueOf(this.plugin.arenas.size());
      } else if (var2.equals("kits_count")) {
         return String.valueOf(this.plugin.kits.size());
      } else if (var2.equals("trails_count")) {
         return String.valueOf(this.plugin.trails.size());
      } else if (var1 == null) {
         return "";
      } else {
         PlayerData var3 = (PlayerData)this.plugin.playerData.get(var1.getName());
         if (var2.equals("kills")) {
            return String.valueOf(var3.kills);
         } else if (var2.equals("deaths")) {
            return String.valueOf(var3.deaths);
         } else if (var2.equals("coins")) {
            return String.valueOf(var3.getCoins(var1));
         } else if (var2.equals("wins")) {
            return String.valueOf(var3.wins);
         } else if (var2.equals("modifier")) {
            return String.valueOf(var3.modifier);
         } else if (var2.equals("projectiles_launched")) {
            return String.valueOf(var3.projectiles_launched);
         } else if (var2.equals("projectiles_hit")) {
            return String.valueOf(var3.projectiles_hit);
         } else if (var2.equals("player_exp")) {
            return String.valueOf(var3.player_exp);
         } else if (var2.equals("player_rank")) {
            return String.valueOf(var3.player_rank);
         } else if (var2.equals("blocks_broken")) {
            return String.valueOf(var3.blocks_broken);
         } else if (var2.equals("blocks_placed")) {
            return String.valueOf(var3.blocks_placed);
         } else if (var2.equals("eggs_broken")) {
            return String.valueOf(var3.eggs_broken);
         } else if (var2.equals("arena")) {
            return String.valueOf(var3.arena != null ? var3.arena.name : "");
         } else if (var2.equals("party_leader")) {
            return String.valueOf(var3.party != null ? var3.party.leaderName : "");
         } else if (var2.equals("selected_kit")) {
            return String.valueOf(var3.selectedKit);
         } else if (var2.equals("selected_trail")) {
            return String.valueOf(var3.selectedTrail);
         } else {
            return !var2.equals("team_colorcode") ? null : String.valueOf(var3.arena != null && var3.arena.players.get(var1.getName()) != null ? "&" + ChatColor.valueOf(((Team)var3.arena.players.get(var1.getName())).getName()).getChar() : "");
         }
      }
   }

   public String getAuthor() {
      return this.plugin.getDescription().getAuthors().toString();
   }

   public String getIdentifier() {
      return "eggwars";
   }

   public String getVersion() {
      return this.plugin.getDescription().getVersion();
   }
}
