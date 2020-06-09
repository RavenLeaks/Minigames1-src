package me.wazup.skywars;

import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Team;

public class PlaceholderAPIHooks extends EZPlaceholderHook {
   private Skywars plugin;

   public PlaceholderAPIHooks(Plugin var1, String var2) {
      super(var1, var2);
      this.plugin = (Skywars)var1;
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
      } else if (var2.equals("cages_count")) {
         return String.valueOf(this.plugin.cages.size());
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
         } else if (var2.equals("arena")) {
            return String.valueOf(var3.arena != null ? var3.arena.name : "");
         } else if (var2.equals("party_leader")) {
            return String.valueOf(var3.party != null ? var3.party.leaderName : "");
         } else if (var2.equals("selected_kit")) {
            return String.valueOf(var3.selectedKit);
         } else if (var2.equals("selected_trail")) {
            return String.valueOf(var3.selectedTrail);
         } else if (var2.equals("selected_cage")) {
            return String.valueOf(var3.selectedCage);
         } else {
            return !var2.equals("teamcolor") ? null : String.valueOf(var3.arena != null && var3.arena.players.containsKey(var1.getName()) && var3.arena.players.get(var1.getName()) != null ? ((Team)var3.arena.players.get(var1.getName())).getPrefix() : "");
         }
      }
   }
}
