package me.wazup.eggwars;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

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
   int maxWarnings;
   int blockBehindSigns;
   int leaveCountdownSeconds;
   int partyInvitationLength;
   int samePlayerKillLimitPerArena;
   int maxArenaSize;
   int broadcasterSendEvery;
   boolean hologramsAboveGenerators;
   boolean signsAboveGenerators;
   boolean respawnWithKit;
   boolean voidInstantDeath;
   boolean allowSpectatorsViewInventory;
   boolean loadSkinsOnSkulls;
   boolean disableNaturalMobSpawning;
   boolean fireworksEnabled;
   boolean leashMobs;
   boolean lobbyScoreboard;
   boolean displayRankInChat;
   boolean showHealthOnBowHit;
   boolean clearDroppedItemsOnDeath;
   List droppedItemsOnDeathExceptions;
   boolean spectatorsChat;
   boolean teamChat;
   boolean lobbyChat;
   double selling_value;
   Material coreBlockType;
   int coinsPerEggBreak;
   boolean teleportSoundEnabled;
   boolean buildingSoundEnabled;
   int deathProtectionInSeconds;
   int buildProtectionRange;
   String teamPrefix;
   List broadcastTime;
   List allowedCommands;
   List commandAliases;
   int party_default_slots;
   HashMap party_custom_slots;
   List executed_commands_player_win;
   List executed_commands_arena_countdown;
   List executed_commands_arena_start;
   List executed_commands_arena_end;
   List executed_commands_egg_destroy;
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
   HashMap spawnerData;
   boolean scoreboardTitleAnimationEnabled;
   int scoreboardTitleAnimationInterval;
   List scoreboardTitleAnimationFrames;
   boolean titles_enabled;
   boolean actionbar_enabled;
   int titles_fadeIn;
   int titles_stay;
   int titles_fadeOut;
   HashMap hotbarItems;

   public Config(Eggwars var1) {
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
      this.maxWarnings = var2.getInt("Max-Warnings");
      this.blockBehindSigns = var2.getInt("Block-Behind-Signs");
      this.leaveCountdownSeconds = var2.getInt("Leave-Countdown-Seconds");
      this.partyInvitationLength = var2.getInt("Party-Invitation-Length");
      this.samePlayerKillLimitPerArena = var2.getInt("Same-Player-Kill-Limit-Per-Arena");
      this.maxArenaSize = var2.getInt("Max-Arena-Size");
      this.broadcasterSendEvery = var2.getInt("Broadcaster-Send-Every") * 20;
      this.hologramsAboveGenerators = var2.getBoolean("Holograms-Above-Generators");
      this.signsAboveGenerators = var2.getBoolean("Signs-Above-Generators");
      this.respawnWithKit = var2.getBoolean("Respawn-With-Kit");
      this.voidInstantDeath = var2.getBoolean("Void-Instant-Death");
      this.allowSpectatorsViewInventory = var2.getBoolean("Allow-Spectators-Inventory-View");
      this.loadSkinsOnSkulls = var2.getBoolean("Load-Skins-On-Skulls");
      this.disableNaturalMobSpawning = var2.getBoolean("Disable-Natural-Mob-Spawning");
      this.fireworksEnabled = var2.getBoolean("Fireworks-Enabled");
      this.leashMobs = var2.getBoolean("Leash-Mobs");
      this.lobbyScoreboard = var2.getBoolean("Lobby-Scoreboard");
      this.displayRankInChat = var2.getBoolean("Display-Rank-In-Chat");
      this.showHealthOnBowHit = var2.getBoolean("Show-Health-On-Bow-Hit");
      this.clearDroppedItemsOnDeath = var2.getBoolean("Dropped-Items-On-Death.clear");
      this.droppedItemsOnDeathExceptions = new ArrayList();
      Iterator var4 = var2.getStringList("Dropped-Items-On-Death.exceptions").iterator();

      String var3;
      while(var4.hasNext()) {
         var3 = (String)var4.next();
         this.droppedItemsOnDeathExceptions.add(Material.valueOf(var3.toUpperCase()));
      }

      this.spectatorsChat = var2.getBoolean("Spectators-Chat");
      this.teamChat = var2.getBoolean("Team-Chat");
      this.lobbyChat = var2.getBoolean("Lobby-Chat");
      this.selling_value = var2.getDouble("Selling-Value");
      this.coreBlockType = Material.getMaterial(var2.getString("Core-Block-Type"));
      this.coinsPerEggBreak = var2.getInt("Coins-Per-Egg-Break");
      this.teleportSoundEnabled = var2.getBoolean("Teleport-Sound-Enabled");
      this.buildingSoundEnabled = var2.getBoolean("Building-Sound-Enabled");
      this.deathProtectionInSeconds = var2.getInt("Death-Protection-In-Seconds");
      this.buildProtectionRange = var2.getInt("Build-Protection-Range");
      this.broadcastTime = var2.getIntegerList("Broadcast-Time");
      this.allowedCommands = new ArrayList();
      var4 = var2.getStringList("Allowed-Commands").iterator();

      while(var4.hasNext()) {
         var3 = (String)var4.next();
         this.allowedCommands.add(var3.toLowerCase());
      }

      this.commandAliases = new ArrayList();
      var4 = var2.getStringList("Command-Aliases").iterator();

      while(var4.hasNext()) {
         var3 = (String)var4.next();
         this.commandAliases.add(var3.toLowerCase());
      }

      this.party_default_slots = var2.getInt("Party.Default-Slots");
      this.party_custom_slots = new HashMap();
      var4 = var2.getStringList("Party.Custom-Slots").iterator();

      while(var4.hasNext()) {
         var3 = (String)var4.next();
         this.party_custom_slots.put(Integer.valueOf(var3.split(" : ")[0]), var3.split(" : ")[1]);
      }

      this.executed_commands_player_win = var2.getStringList("Executed-Commands.Player-Win");
      this.executed_commands_arena_countdown = var2.getStringList("Executed-Commands.Arena-Countdown");
      this.executed_commands_arena_start = var2.getStringList("Executed-Commands.Arena-Start");
      this.executed_commands_arena_end = var2.getStringList("Executed-Commands.Arena-End");
      this.executed_commands_egg_destroy = var2.getStringList("Executed-Commands.Egg-Destroy");
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

      int var16;
      while(var4.hasNext()) {
         var3 = (String)var4.next();
         String[] var12 = var3.split(" : ");
         var16 = Integer.valueOf(var12[0]);
         ArrayList var18 = new ArrayList();

         for(int var8 = 1; var8 < var12.length; ++var8) {
            var18.add(var12[var8]);
         }

         this.killstreaks.put(var16, var18);
      }

      this.spawnerData = new HashMap();

      ItemStack var19;
      HashMap var20;
      for(var4 = var2.getConfigurationSection("Generator-Settings").getKeys(false).iterator(); var4.hasNext(); this.spawnerData.put(Material.valueOf(var3.toUpperCase()), new SpawnerData(var16, var19, var20))) {
         var3 = (String)var4.next();
         String var13 = "Generator-Settings." + var3 + ".";
         var16 = var2.getInt(var13 + "Item-Limit");
         var19 = null;
         var20 = null;
         if (var2.getBoolean(var13 + "Upgrades.Enabled")) {
            var19 = var1.getItemStack(var2.getString(var13 + "Upgrades.Fix-Cost"), true, false);
            var20 = new HashMap();
            Iterator var10 = var2.getStringList(var13 + "Upgrades.List").iterator();

            while(var10.hasNext()) {
               String var9 = (String)var10.next();
               int var11 = Integer.valueOf(var9.split(" :: ")[1]);
               if (var11 >= 10 && var11 % 10 == 0 && !var20.values().contains(var11)) {
                  var20.put(var1.getItemStack(var9.split(" :: ")[0], true, false), var11);
               }
            }
         }
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

      boolean var14 = Bukkit.getPluginManager().getPlugin("TitleManager") != null && Bukkit.getPluginManager().getPlugin("TitleManager").isEnabled();
      if (var14) {
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
      Iterator var15 = var2.getConfigurationSection("Hotbar-Items").getKeys(false).iterator();

      while(var15.hasNext()) {
         String var17 = (String)var15.next();
         if (var2.getBoolean("Hotbar-Items." + var17 + ".enabled")) {
            this.hotbarItems.put(var17, var2.getInt("Hotbar-Items." + var17 + ".slot"));
         }
      }

   }
}
