package me.wazup.kitbattle;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;

public class MVdWPlacholderHook {
   public MVdWPlacholderHook(final Kitbattle var1) {
      PlaceholderAPI.registerPlaceholder(var1, "kitbattle_players_count", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return String.valueOf(var1.players.size());
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "kitbattle_maps_count", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return String.valueOf(var1.playingMaps.size() + var1.tournamentMaps.size() + var1.challengeMaps.size());
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "kitbattle_challengers_count", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return String.valueOf(var1.challengesManager != null ? var1.challengesManager.players.size() : 0);
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "kitbattle_tournament_participants_count", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return String.valueOf(var1.tournamentsManager != null ? var1.tournamentsManager.getSize() : 0);
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "kitbattle_kits_count", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return String.valueOf(var1.Kits.size());
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "kitbattle_ranks_count", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return String.valueOf(var1.Ranks.size());
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "kitbattle_coins", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return var1x.isOnline() ? String.valueOf(((PlayerData)var1.playerData.get(var1x.getPlayer().getName())).coins) : "";
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "kitbattle_kills", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return var1x.isOnline() ? String.valueOf(((PlayerData)var1.playerData.get(var1x.getPlayer().getName())).getKills()) : "";
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "kitbattle_killstreak", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return var1x.isOnline() ? String.valueOf(((PlayerData)var1.playerData.get(var1x.getPlayer().getName())).getKillstreak()) : "";
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "kitbattle_deaths", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return var1x.isOnline() ? String.valueOf(((PlayerData)var1.playerData.get(var1x.getPlayer().getName())).getDeaths()) : "";
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "kitbattle_player_exp", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return var1x.isOnline() ? String.valueOf(((PlayerData)var1.playerData.get(var1x.getPlayer().getName())).getExp()) : "";
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "kitbattle_player_rank", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return var1x.isOnline() ? String.valueOf(((PlayerData)var1.playerData.get(var1x.getPlayer().getName())).getRank().getName()) : "";
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "kitbattle_player_next_rank", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return var1x.isOnline() ? String.valueOf(((PlayerData)var1.playerData.get(var1x.getPlayer().getName())).getNextRank() != null ? ((PlayerData)var1.playerData.get(var1x.getPlayer().getName())).getNextRank().getName() : "None") : "";
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "kitbattle_player_next_rank_exp", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return var1x.isOnline() ? String.valueOf(((PlayerData)var1.playerData.get(var1x.getPlayer().getName())).getNextRank() != null ? ((PlayerData)var1.playerData.get(var1x.getPlayer().getName())).getNextRank().getRequiredExp() : "0") : "";
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "kitbattle_player_next_rank_exp_difference", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return var1x.isOnline() ? String.valueOf(((PlayerData)var1.playerData.get(var1x.getPlayer().getName())).getNextRank() != null ? ((PlayerData)var1.playerData.get(var1x.getPlayer().getName())).getNextRank().getRequiredExp() - ((PlayerData)var1.playerData.get(var1x.getPlayer().getName())).getExp() : "0") : "";
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "kitbattle_map", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return var1x.isOnline() ? String.valueOf(((PlayerData)var1.playerData.get(var1x.getPlayer().getName())).getMap() != null ? ((PlayerData)var1.playerData.get(var1x.getPlayer().getName())).getMap().name : "None") : "";
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "kitbattle_selected_kit", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return var1x.isOnline() ? String.valueOf(((PlayerData)var1.playerData.get(var1x.getPlayer().getName())).getKit() != null ? ((PlayerData)var1.playerData.get(var1x.getPlayer().getName())).getKit().getName() : "None") : "";
         }
      });
   }
}
