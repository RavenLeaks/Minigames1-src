package me.wazup.skywars;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

class Config {
   boolean emptyChunkGenerator;
   boolean useUUID;
   boolean mysql_enabled;
   int startingCoins;
   int coinsPerKill;
   int coinsPerWin;
   int minExpPerKill;
   int maxExpPerKill;
   int celebrationLength;
   int blockBehindSigns;
   int maxWarnings;
   int partyInvitationLength;
   int maxArenaSize;
   int leaveCountdownSeconds;
   int broadcasterSendEvery;
   boolean lobbyChat;
   boolean inGameChat;
   boolean spectatorsChat;
   boolean lobbyScoreboardEnabled;
   boolean allowSpectatorsViewInventory;
   boolean loadSkinsOnSkulls;
   boolean disableNaturalMobSpawning;
   boolean fireworksEnabled;
   boolean allowSpectatorJoin;
   boolean leashMobs;
   boolean teleportMobs;
   boolean voidInstantKill;
   boolean displayRankInChat;
   boolean showHealthOnBowHit;
   double selling_value;
   List broadcastTime;
   List allowedCommands;
   int party_default_slots;
   HashMap party_custom_slots;
   int chestChecks_slotOverwrite;
   int chestChecks_itemDuplicate;
   List executed_commands_player_win;
   List executed_commands_arena_start;
   List executed_commands_arena_countdown;
   boolean bungee_mode_enabled;
   String bungee_mode_hub;
   int rollback_scan_speed;
   int rollback_repair_speed;
   int rollback_queue_size;
   int rollback_send_status_update_every;
   int first_rank_exp;
   double rank_modifier;
   int max_rank;
   HashMap rankCommands;
   HashMap killstreaks;
   ChatColor defaultTeamColor;
   boolean teamColorRandomizerEnabled;
   ChatColor[] teamColorRandomizerColors;
   boolean scoreboardTitleAnimationEnabled;
   int scoreboardTitleAnimationInterval;
   List scoreboardTitleAnimationFrames;
   boolean titles_enabled;
   boolean actionbar_enabled;
   int titles_fadeIn;
   int titles_stay;
   int titles_fadeOut;
   HashMap hotbarItems;

   public Config(Skywars var1) {
      FileConfiguration var2 = var1.getConfig();
      this.emptyChunkGenerator = var2.getBoolean("Empty-Chunk-Generator");
      this.useUUID = var2.getBoolean("use-UUID");
      this.mysql_enabled = var2.getBoolean("MySQL.enabled");
      this.startingCoins = var2.getInt("String-Coins");
      this.coinsPerKill = var2.getInt("Coins-Per-Kill");
      this.coinsPerWin = var2.getInt("Coins-Per-Win");
      this.minExpPerKill = var2.getInt("Min-Exp-Per-Kill");
      this.maxExpPerKill = var2.getInt("Max-Exp-Per-Kill");
      this.celebrationLength = var2.getInt("Celebration-Length");
      this.blockBehindSigns = var2.getInt("Block-Behind-Signs");
      this.maxWarnings = var2.getInt("Max-Warnings");
      this.partyInvitationLength = var2.getInt("Party-Invitation-Length");
      this.maxArenaSize = var2.getInt("Max-Arena-Size");
      this.leaveCountdownSeconds = var2.getInt("Leave-Countdown-Seconds");
      this.broadcasterSendEvery = var2.getInt("Broadcaster-Send-Every") * 20;
      this.lobbyChat = var2.getBoolean("Lobby-Chat");
      this.inGameChat = var2.getBoolean("In-Game-Chat");
      this.spectatorsChat = var2.getBoolean("Spectators-Chat");
      this.lobbyScoreboardEnabled = var2.getBoolean("Lobby-Scoreboard-Enabled");
      this.allowSpectatorsViewInventory = var2.getBoolean("Allow-Spectators-Inventory-View");
      this.loadSkinsOnSkulls = var2.getBoolean("Load-Skins-On-Skulls");
      this.disableNaturalMobSpawning = var2.getBoolean("Disable-Natural-Mob-Spawning");
      this.fireworksEnabled = var2.getBoolean("Fireworks-Enabled");
      this.allowSpectatorJoin = var2.getBoolean("Allow-Spectator-Join");
      this.leashMobs = var2.getBoolean("Leash-Mobs");
      this.teleportMobs = var2.getBoolean("Teleport-Mobs");
      this.voidInstantKill = var2.getBoolean("Void-Instant-Kill");
      this.displayRankInChat = var2.getBoolean("Display-Rank-In-Chat");
      this.showHealthOnBowHit = var2.getBoolean("Show-Health-On-Bow-Hit");
      this.selling_value = var2.getDouble("Selling-Value");
      this.broadcastTime = var2.getIntegerList("Broadcast-Time");
      this.allowedCommands = new ArrayList();
      Iterator var4 = var2.getStringList("Allowed-Commands").iterator();

      String var3;
      while(var4.hasNext()) {
         var3 = (String)var4.next();
         this.allowedCommands.add(var3.toLowerCase());
      }

      this.party_default_slots = var2.getInt("Party.Default-Slots");
      this.party_custom_slots = new HashMap();
      var4 = var2.getStringList("Party.Custom-Slots").iterator();

      while(var4.hasNext()) {
         var3 = (String)var4.next();
         this.party_custom_slots.put(Integer.valueOf(var3.split(" : ")[0]), var3.split(" : ")[1]);
      }

      this.chestChecks_slotOverwrite = Math.min(Math.max(1, var2.getInt("Chest-Checks.Slot-Overwrite")), 10);
      this.chestChecks_itemDuplicate = Math.min(Math.max(1, var2.getInt("Chest-Checks.Item-Duplicate")), 10);
      this.executed_commands_player_win = var2.getStringList("Executed-Commands.Player-Win");
      this.executed_commands_arena_start = var2.getStringList("Executed-Commands.Arena-Start");
      this.executed_commands_arena_countdown = var2.getStringList("Executed-Commands.Arena-Countdown");
      this.bungee_mode_enabled = var2.getBoolean("Bungee-Mode.enabled");
      this.bungee_mode_hub = var2.getString("Bungee-Mode.hub");
      this.rollback_scan_speed = var2.getInt("Rollback.Scan-Speed");
      this.rollback_repair_speed = var2.getInt("Rollback.Repair-Speed");
      this.rollback_queue_size = var2.getInt("Rollback.Queue-Size");
      this.rollback_send_status_update_every = var2.getInt("Rollback.Send-Status-Update-Every");
      this.first_rank_exp = var2.getInt("Ranks.First-Rank-Exp");
      this.rank_modifier = var2.getDouble("Ranks.Rank-Modifier");
      this.max_rank = var2.getInt("Ranks.Max-Rank");
      this.rankCommands = new HashMap();
      var4 = var2.getStringList("Ranks.Commands").iterator();

      while(var4.hasNext()) {
         var3 = (String)var4.next();
         int var5 = Integer.valueOf(var3.split(" : ")[0]);
         ArrayList var6 = new ArrayList();

         for(int var7 = 1; var7 < var3.split(" : ").length; ++var7) {
            var6.add(var3.split(" : ")[var7]);
         }

         this.rankCommands.put(var5, var6);
      }

      this.killstreaks = new HashMap();
      var4 = var2.getStringList("Killstreaks").iterator();

      while(var4.hasNext()) {
         var3 = (String)var4.next();
         String[] var10 = var3.split(" : ");
         int var14 = Integer.valueOf(var10[0]);
         ArrayList var15 = new ArrayList();

         for(int var8 = 1; var8 < var10.length; ++var8) {
            var15.add(var10[var8]);
         }

         this.killstreaks.put(var14, var15);
      }

      this.defaultTeamColor = ChatColor.getByChar(var2.getString("Team-Coloring.default").replace("&", ""));
      this.teamColorRandomizerEnabled = var2.getBoolean("Team-Coloring.randomizer-enabled");
      String var11;
      Iterator var13;
      if (this.teamColorRandomizerEnabled) {
         this.teamColorRandomizerColors = new ChatColor[var2.getStringList("Team-Coloring.randomizer-colors").size()];
         int var9 = 0;

         for(var13 = var2.getStringList("Team-Coloring.randomizer-colors").iterator(); var13.hasNext(); ++var9) {
            var11 = (String)var13.next();
            this.teamColorRandomizerColors[var9] = ChatColor.getByChar(var11.replace("&", ""));
         }
      } else {
         this.teamColorRandomizerColors = new ChatColor[0];
      }

      this.scoreboardTitleAnimationEnabled = var2.getBoolean("Scoreboard-Title-Animation.enabled");
      this.scoreboardTitleAnimationInterval = var2.getInt("Scoreboard-Title-Animation.interval");
      this.scoreboardTitleAnimationFrames = new ArrayList();
      var4 = var2.getStringList("Scoreboard-Title-Animation.frames").iterator();

      while(var4.hasNext()) {
         var3 = (String)var4.next();
         this.scoreboardTitleAnimationFrames.add(ChatColor.translateAlternateColorCodes('&', var3));
      }

      if (this.scoreboardTitleAnimationFrames.size() < 2) {
         this.scoreboardTitleAnimationEnabled = false;
         Bukkit.getConsoleSender().sendMessage(var1.customization.prefix + "Lobby scoreboard animation was disabled because there are not enough amount of frames!");
      } else if (this.scoreboardTitleAnimationEnabled) {
         Bukkit.getConsoleSender().sendMessage(var1.customization.prefix + "Scoreboard title animation has been enabled and it contains " + ChatColor.AQUA + this.scoreboardTitleAnimationFrames.size() + ChatColor.GRAY + " frame(s)!");
      }

      boolean var12 = Bukkit.getPluginManager().getPlugin("TitleManager") != null && Bukkit.getPluginManager().getPlugin("TitleManager").isEnabled();
      if (var12) {
         Bukkit.getConsoleSender().sendMessage(var1.customization.prefix + "TitleManager has been detected! TitleManager features are now accessible..");
         this.titles_enabled = var2.getBoolean("Titles.enabled");
         this.titles_fadeIn = var2.getInt("Titles.fadeIn");
         this.titles_stay = var2.getInt("Titles.stay");
         this.titles_fadeOut = var2.getInt("Titles.fadeOut");
         this.actionbar_enabled = var2.getBoolean("Action-Bar-Enabled");
      } else {
         this.titles_enabled = false;
         this.actionbar_enabled = false;
      }

      this.hotbarItems = new HashMap();
      var13 = var2.getConfigurationSection("Hotbar-Items").getKeys(false).iterator();

      while(var13.hasNext()) {
         var11 = (String)var13.next();
         if (var2.getBoolean("Hotbar-Items." + var11 + ".enabled")) {
            this.hotbarItems.put(var11, var2.getInt("Hotbar-Items." + var11 + ".slot"));
         }
      }

   }
}
