package me.wazup.skywars;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import org.bukkit.scoreboard.Team;

public class MVdWPlacholderHook {
   public MVdWPlacholderHook(final Skywars var1) {
      PlaceholderAPI.registerPlaceholder(var1, "skywars_players_count", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return String.valueOf(var1.players.size());
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "skywars_parties_count", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return String.valueOf(var1.parties.size());
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "skywars_lobby_players_count", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return String.valueOf(var1.lobbyPlayers.size());
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "skywars_arenas_count", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return String.valueOf(var1.arenas.size());
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "skywars_kits_count", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return String.valueOf(var1.kits.size());
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "skywars_trails_count", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return String.valueOf(var1.trails.size());
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "skywars_cages_count", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return String.valueOf(var1.cages.size());
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "skywars_kills", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return var1x.isOnline() ? String.valueOf(((PlayerData)var1.playerData.get(var1x.getPlayer().getName())).kills) : "";
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "skywars_deaths", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return var1x.isOnline() ? String.valueOf(((PlayerData)var1.playerData.get(var1x.getPlayer().getName())).deaths) : "";
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "skywars_coins", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return var1x.isOnline() ? String.valueOf(((PlayerData)var1.playerData.get(var1x.getPlayer().getName())).getCoins(var1x.getPlayer())) : "";
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "skywars_wins", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return var1x.isOnline() ? String.valueOf(((PlayerData)var1.playerData.get(var1x.getPlayer().getName())).wins) : "";
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "skywars_modifier", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return var1x.isOnline() ? String.valueOf(((PlayerData)var1.playerData.get(var1x.getPlayer().getName())).modifier) : "";
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "skywars_projectiles_launched", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return var1x.isOnline() ? String.valueOf(((PlayerData)var1.playerData.get(var1x.getPlayer().getName())).projectiles_launched) : "";
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "skywars_projectiles_hit", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return var1x.isOnline() ? String.valueOf(((PlayerData)var1.playerData.get(var1x.getPlayer().getName())).projectiles_hit) : "";
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "skywars_player_exp", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return var1x.isOnline() ? String.valueOf(((PlayerData)var1.playerData.get(var1x.getPlayer().getName())).player_exp) : "";
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "skywars_player_rank", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return var1x.isOnline() ? String.valueOf(((PlayerData)var1.playerData.get(var1x.getPlayer().getName())).player_rank) : "";
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "skywars_blocks_broken", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return var1x.isOnline() ? String.valueOf(((PlayerData)var1.playerData.get(var1x.getPlayer().getName())).blocks_broken) : "";
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "skywars_blocks_placed", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return var1x.isOnline() ? String.valueOf(((PlayerData)var1.playerData.get(var1x.getPlayer().getName())).blocks_placed) : "";
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "skywars_arena", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return var1x.isOnline() ? String.valueOf(((PlayerData)var1.playerData.get(var1x.getPlayer().getName())).arena != null ? ((PlayerData)var1.playerData.get(var1x.getPlayer().getName())).arena.name : "") : "";
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "skywars_party_leader", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return var1x.isOnline() ? String.valueOf(((PlayerData)var1.playerData.get(var1x.getPlayer().getName())).party != null ? ((PlayerData)var1.playerData.get(var1x.getPlayer().getName())).party.leaderName : "") : "";
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "skywars_selected_kit", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return var1x.isOnline() ? String.valueOf(((PlayerData)var1.playerData.get(var1x.getPlayer().getName())).selectedKit) : "";
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "skywars_selected_trail", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return var1x.isOnline() ? String.valueOf(((PlayerData)var1.playerData.get(var1x.getPlayer().getName())).selectedTrail) : "";
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "skywars_selected_cage", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return var1x.isOnline() ? String.valueOf(((PlayerData)var1.playerData.get(var1x.getPlayer().getName())).selectedCage) : "";
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "skywars_teamcolor", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return !var1x.isOnline() ? "" : String.valueOf(((PlayerData)var1.playerData.get(var1x.getPlayer().getName())).arena != null && ((PlayerData)var1.playerData.get(var1x.getPlayer().getName())).arena.players.containsKey(var1x.getPlayer().getName()) && ((PlayerData)var1.playerData.get(var1x.getPlayer().getName())).arena.players.get(var1x.getPlayer().getName()) != null ? ((Team)((PlayerData)var1.playerData.get(var1x.getPlayer().getName())).arena.players.get(var1x.getPlayer().getName())).getPrefix() : "");
         }
      });
   }
}
