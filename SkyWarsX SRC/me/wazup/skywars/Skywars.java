package me.wazup.skywars;

import com.google.common.collect.Lists;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;
import javax.imageio.ImageIO;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.ChunkGenerator.BiomeGrid;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Team;

public class Skywars extends JavaPlugin {
   Skywars plugin;
   public static SkywarsAPI api;
   Random r = new Random();
   ChatColor[] colors;
   ArrayList players;
   ArrayList lobbyPlayers;
   ArrayList protectedPlayers;
   ArrayList parties;
   HashMap playerData;
   HashMap selectionMode;
   HashMap editingChests;
   HashMap joinSigns;
   HashMap topSigns;
   HashMap arenas;
   HashMap cages;
   HashMap chests;
   HashMap kits;
   HashMap trails;
   ItemStack wand_itemstack;
   ItemStack chest_tool_itemstack;
   ItemStack pane_itemstack;
   ItemStack save_itemstack;
   ItemStack plus_itemstack;
   ItemStack minus_itemstack;
   ItemStack winnersMap;
   ItemStack quit_itemstack;
   ItemStack play_itemstack;
   ItemStack shop_itemstack;
   ItemStack profile_itemstack;
   ItemStack party_itemstack;
   ItemStack teleporter_itemstack;
   ItemStack autojoin_itemstack;
   ItemStack back_itemstack;
   ItemStack confirm_itemstack;
   ItemStack cancel_itemstack;
   ItemStack vote_itemstack;
   ItemStack inventory_itemstack;
   ItemStack next_itemstack;
   ItemStack previous_itemstack;
   ItemStack stats_itemstack;
   ItemStack achievements_itemstack;
   ItemStack create_itemstack;
   ItemStack join_itemstack;
   ItemStack leave_itemstack;
   ItemStack invite_itemstack;
   Inventory profileInventory;
   Inventory partyMenu;
   Inventory quitInventory;
   Inventory votingOptions;
   SmartInventory shop;
   SmartInventory arenaSelector;
   SmartInventory partySelector;
   SmartInventory playerInviter;
   int[] smartSlots;
   Location lobbyLocation;
   BukkitTask savingTask;
   BukkitTask leaderboard_updater;
   long leaderboard_updater_time;
   BukkitTask scoreboardTitleAnimationTask;
   Sound CLICK;
   Sound NOTE_PLING;
   boolean availableUpdate;
   FilesManager filesManager;
   Config config;
   RollbackManager rollbackManager;
   MySQL mysql;
   RanksManager ranksManager;
   AchievementsManager achievementsManager;
   Broadcaster broadcaster;
   MysteryBox mysteryBox;
   Customization customization;
   HologramsManager hologramsManager;
   Economy vault;
   public static int db280c66-10c4-42d4-b408-ba9cc064247b;
   public static String d18aaf0e-6cd2-4a70-8ad9-1cd39f802dbc;

   public Skywars() {
      this.colors = new ChatColor[]{ChatColor.DARK_AQUA, ChatColor.GOLD, ChatColor.GRAY, ChatColor.BLUE, ChatColor.GREEN, ChatColor.AQUA, ChatColor.RED, ChatColor.LIGHT_PURPLE, ChatColor.YELLOW};
      this.players = new ArrayList();
      this.lobbyPlayers = new ArrayList();
      this.protectedPlayers = new ArrayList();
      this.playerData = new HashMap();
      this.selectionMode = new HashMap();
      this.editingChests = new HashMap();
      this.wand_itemstack = (new ItemStackBuilder(Material.BLAZE_ROD)).setName(ChatColor.AQUA + "Skywars" + ChatColor.LIGHT_PURPLE + " Wand").addLore(ChatColor.YELLOW + "--------------------------", ChatColor.GREEN + "Left click to select the first corner", ChatColor.GREEN + "Right click to select the second corner", ChatColor.YELLOW + "--------------------------").build();
      this.chest_tool_itemstack = (new ItemStackBuilder(Material.BLAZE_ROD)).setName(ChatColor.GREEN + "Chest Tool").addLore(ChatColor.GRAY + "Left click a chest to set its type", " ", ChatColor.GRAY + "Right click a chest to check its type!").build();
      this.pane_itemstack = (new ItemStackBuilder(Material.STAINED_GLASS_PANE)).setDurability(7).setName(ChatColor.DARK_GRAY + " ").build();
      this.save_itemstack = (new ItemStackBuilder(Material.DIAMOND)).setName(ChatColor.AQUA + "Save & Apply").build();
      this.plus_itemstack = (new ItemStackBuilder(Material.STAINED_GLASS_PANE)).setDurability(5).setName(ChatColor.GREEN + "+").build();
      this.minus_itemstack = (new ItemStackBuilder(Material.STAINED_GLASS_PANE)).setDurability(14).setName(ChatColor.RED + "-").build();
      this.smartSlots = new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};
      this.leaderboard_updater_time = 0L;
   }

   public void onEnable() {
      this.plugin = this;
      api = new SkywarsAPI(this);
      Bukkit.getPluginManager().registerEvents(new SkywarsListener(this), this);
      this.rollbackManager = new RollbackManager(this);
      this.broadcaster = new Broadcaster(this);
      this.loadSounds();
      this.mainSetup();
      Iterator var2 = this.getOnlinePlayers().iterator();

      while(var2.hasNext()) {
         Player var1 = (Player)var2.next();
         this.playerData.put(var1.getName(), new PlayerData(this, var1));
      }

      if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
         Bukkit.getConsoleSender().sendMessage(this.customization.prefix + "Found PlaceholderAPI, Hooked: " + (new PlaceholderAPIHooks(this, "skywars")).hook());
      }

      if (Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")) {
         new MVdWPlacholderHook(this);
         Bukkit.getConsoleSender().sendMessage(this.customization.prefix + "Found MVdWPlaceholderAPI, Hooked: true");
      }

      ConsoleCommandSender var3 = Bukkit.getConsoleSender();
      var3.sendMessage(ChatColor.RED + "=======================================");
      var3.sendMessage(this.customization.prefix + ChatColor.GREEN + "Plugin has been enabled (v" + this.getDescription().getVersion() + ")");
      var3.sendMessage(ChatColor.RED + "=======================================");
      Bukkit.getConsoleSender().sendMessage("Cracked By: Calosis § DirectLeaks.net");
   }

   public void onDisable() {
      ArrayList var1 = new ArrayList();
      Iterator var3 = this.arenas.values().iterator();

      while(var3.hasNext()) {
         Arena var2 = (Arena)var3.next();
         var1.addAll(this.getPlayers(var2.spectators));
      }

      var3 = this.getPlayers(this.players).iterator();

      Player var6;
      while(var3.hasNext()) {
         var6 = (Player)var3.next();
         ((PlayerData)this.playerData.get(var6.getName())).restoreData(var6);
         var6.sendMessage((String)this.customization.messages.get("Player-Leave"));
         Iterator var5 = var1.iterator();

         while(var5.hasNext()) {
            Player var4 = (Player)var5.next();
            var6.showPlayer(var4);
         }
      }

      var3 = this.getPlayers(this.playerData.keySet()).iterator();

      while(var3.hasNext()) {
         var6 = (Player)var3.next();
         ((PlayerData)this.playerData.get(var6.getName())).save(var6);
      }

      var3 = this.getPlayers(this.selectionMode.keySet()).iterator();

      while(var3.hasNext()) {
         var6 = (Player)var3.next();
         var6.getInventory().removeItem(new ItemStack[]{this.wand_itemstack});
      }

      if (this.hologramsManager != null && this.hologramsManager.leaderboardHologram != null) {
         this.hologramsManager.leaderboardHologram.delete();
      }

      Bukkit.getConsoleSender().sendMessage(this.customization.prefix + "Plugin has been disabled!");
   }

   public boolean onCommand(final CommandSender var1, Command var2, String var3, final String[] var4) {
      if (var3.equalsIgnoreCase("skywars") || var3.equalsIgnoreCase("sw")) {
         if (var1 instanceof Player) {
            ((Player)var1).playSound(((Player)var1).getLocation(), this.CLICK, 1.0F, 1.0F);
         }

         if (var4.length == 0) {
            ChatColor var18 = this.colors[this.r.nextInt(this.colors.length)];
            var1.sendMessage("" + var18 + ChatColor.STRIKETHROUGH + "-------------" + ChatColor.YELLOW + " Skywars " + ChatColor.GRAY + "[" + this.getDescription().getVersion() + "] " + var18 + ChatColor.STRIKETHROUGH + "-------------");
            var1.sendMessage(var18 + " - " + ChatColor.YELLOW + "/Skywars | sw " + var18 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Shows a list of commands");
            if (!this.config.bungee_mode_enabled) {
               var1.sendMessage(var18 + " - " + ChatColor.YELLOW + "/sw Join " + var18 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Puts you in the game lobby");
            }

            var1.sendMessage(var18 + " - " + ChatColor.YELLOW + "/sw Leave " + var18 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Removes you from the game!");
            var1.sendMessage(var18 + " - " + ChatColor.YELLOW + "/sw Autojoin " + var18 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Puts you in the best available arena!");
            var1.sendMessage(var18 + " - " + ChatColor.YELLOW + "/sw List " + var18 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Shows a list of arenas and other information!");
            var1.sendMessage(var18 + " - " + ChatColor.YELLOW + "/sw " + (var1.hasPermission("skywars.admin") ? ChatColor.GREEN : ChatColor.RED) + "Admin " + var18 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Shows a list of admin commands");
            var1.sendMessage("" + var18 + ChatColor.STRIKETHROUGH + "--------------------------------------");
            return true;
         }

         String var5 = var4[0].toLowerCase();
         ChatColor var19;
         if (var5.equals("admin")) {
            if (!this.checkSender(var1, false, "skywars.admin")) {
               return true;
            }

            var19 = this.colors[this.r.nextInt(this.colors.length)];
            var1.sendMessage("" + var19 + ChatColor.STRIKETHROUGH + "------------" + ChatColor.YELLOW + " Skywars " + ChatColor.RED + "Admin " + var19 + ChatColor.STRIKETHROUGH + "------------");
            var1.sendMessage(var19 + " - " + ChatColor.YELLOW + "/sw Setlobby " + var19 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Sets the lobby location!");
            var1.sendMessage(var19 + " - " + ChatColor.YELLOW + "/sw Wand " + var19 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Gives you a selection wand!");
            var1.sendMessage(var19 + " - " + ChatColor.YELLOW + "/sw Create " + var19 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Creates a new arena!");
            var1.sendMessage(var19 + " - " + ChatColor.YELLOW + "/sw Delete " + var19 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Deletes an existing arena!");
            var1.sendMessage(var19 + " - " + ChatColor.YELLOW + "/sw Addspawn " + var19 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Adds a spawnpoint to an arena!");
            var1.sendMessage(var19 + " - " + ChatColor.YELLOW + "/sw Removespawn " + var19 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Removes a spawnpoint from an arena!");
            var1.sendMessage(var19 + " - " + ChatColor.YELLOW + "/sw Start/Stop " + var19 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Forces an arena to start/stop");
            var1.sendMessage(var19 + " - " + ChatColor.YELLOW + "/sw Setspectators " + var19 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Sets the location that spectators teleport to");
            var1.sendMessage(var19 + " - " + ChatColor.YELLOW + "/sw Coins " + var19 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Modifies a player coins!");
            var1.sendMessage(var19 + " - " + ChatColor.YELLOW + "/sw Setmodifier " + var19 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Changes a player modifier!");
            var1.sendMessage(var19 + " - " + ChatColor.YELLOW + "/sw Holograms " + var19 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Manages holographic features");
            var1.sendMessage(var19 + " - " + ChatColor.YELLOW + "/sw Reset " + var19 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Resets a player stats");
            var1.sendMessage(var19 + " - " + ChatColor.YELLOW + "/sw Edit " + var19 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Allows arena modifications");
            var1.sendMessage(var19 + " - " + ChatColor.YELLOW + "/sw Editmode " + var19 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Allows the user to modify surroundings in bungeemode");
            var1.sendMessage(var19 + " - " + ChatColor.YELLOW + "/sw Reload " + var19 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Reloads the plugin");
            var1.sendMessage(var19 + " - " + ChatColor.YELLOW + "/sw Updateregion " + var19 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Updates an arena region");
            var1.sendMessage(var19 + " - " + ChatColor.YELLOW + "/sw ChestManager " + var19 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Allows to modify chest types");
            var1.sendMessage(var19 + " - " + ChatColor.YELLOW + "/sw WorldManager " + var19 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Shows a list of World management commands");
            var1.sendMessage("" + var19 + ChatColor.STRIKETHROUGH + "-------------------------------------");
            return true;
         }

         final Player var20;
         if (!this.config.bungee_mode_enabled && var5.equals("join")) {
            if (!this.checkSender(var1, true, "")) {
               return true;
            }

            var20 = (Player)var1;
            if (this.lobbyLocation == null) {
               var20.sendMessage((String)this.customization.messages.get("Lobby-Not-Set"));
               return true;
            }

            if (this.players.contains(var20.getName())) {
               var20.sendMessage((String)this.customization.messages.get("Already-In-Game"));
               return true;
            }

            this.join(var20);
            return true;
         }

         final PlayerData var61;
         if (var5.equals("leave")) {
            if (!this.checkSender(var1, true, "")) {
               return true;
            }

            var20 = (Player)var1;
            if (!this.players.contains(var20.getName())) {
               var20.sendMessage((String)this.customization.messages.get("Not-In-Game"));
               return true;
            }

            var61 = (PlayerData)this.playerData.get(var20.getName());
            if (var61.arena != null && !var61.arena.state.AVAILABLE() && !var61.arena.spectators.contains(var20.getName()) && !var61.arena.state.equals(Enums.ArenaState.ENDING)) {
               final Location var76 = var20.getLocation().getBlock().getLocation();
               (new BukkitRunnable() {
                  int seconds;

                  {
                     this.seconds = Skywars.this.config.leaveCountdownSeconds;
                  }

                  public void run() {
                     if (var61.arena != null && Bukkit.getPlayer(var20.getName()) != null) {
                        if (!var20.getLocation().getBlock().getLocation().equals(var76)) {
                           var20.sendMessage((String)Skywars.this.customization.messages.get("Player-Move"));
                           this.cancel();
                        } else if (this.seconds == 0) {
                           Skywars.this.leave(var20, false);
                           this.cancel();
                        } else {
                           var20.sendMessage(((String)Skywars.this.customization.messages.get("Player-Movement-Disabled")).replace("%seconds%", String.valueOf(this.seconds)));
                        }
                     } else {
                        this.cancel();
                     }

                     --this.seconds;
                  }
               }).runTaskTimer(this, 0L, 20L);
            } else {
               this.leave(var20, false);
            }

            return true;
         }

         if (var5.equals("autojoin")) {
            if (!this.checkSender(var1, true, "")) {
               return true;
            }

            var20 = (Player)var1;
            var61 = (PlayerData)this.plugin.playerData.get(var20.getName());
            if (var61.hasCooldown(var20, "AUTOJOIN_COMMAND", 3)) {
               return true;
            }

            if (this.lobbyPlayers.contains(var20.getName()) && var61.arena == null) {
               this.autojoin(var20, var4.length > 1 ? var4[1].toUpperCase() : "");
               return true;
            }

            var20.sendMessage((String)this.customization.messages.get("Not-In-Lobby"));
            return true;
         }

         if (var5.equals("setlobby")) {
            if (!this.checkSender(var1, true, "skywars.setlobby")) {
               return true;
            }

            var20 = (Player)var1;
            this.lobbyLocation = var20.getLocation();
            this.getConfig().set("Lobby", this.getStringFromLocation(this.lobbyLocation, true));
            this.saveConfig();
            var20.sendMessage(this.customization.prefix + "Lobby has been " + ChatColor.GREEN + "successfully" + ChatColor.GRAY + " set at " + this.getReadableLocationString(this.lobbyLocation, true));
            return true;
         }

         if (var5.equals("wand")) {
            if (!this.checkSender(var1, true, "skywars.wand")) {
               return true;
            }

            var20 = (Player)var1;
            if (!this.selectionMode.containsKey(var20.getName())) {
               var20.getInventory().addItem(new ItemStack[]{this.wand_itemstack});
               this.selectionMode.put(var20.getName(), new Location[2]);
               var20.sendMessage(this.customization.prefix + "You have entered the selection mode!");
            } else {
               var20.getInventory().removeItem(new ItemStack[]{this.wand_itemstack});
               this.selectionMode.remove(var20.getName());
               var20.sendMessage(this.customization.prefix + "You have left the selection mode!");
            }

            return true;
         }

         String var26;
         File var33;
         Cuboid var64;
         YamlConfiguration var65;
         if (var5.equals("create")) {
            if (!this.checkSender(var1, true, "skywars.create")) {
               return true;
            }

            var20 = (Player)var1;
            if (var4.length >= 5 && this.checkNumbers(var4[2], var4[3], var4[4]) && Integer.valueOf(var4[2]) >= 1 && Integer.valueOf(var4[3]) >= 2 && Integer.valueOf(var4[4]) >= Integer.valueOf(var4[3])) {
               var26 = var4[1];
               if (this.arenas.containsKey(var26.toLowerCase())) {
                  var20.sendMessage(this.customization.prefix + "There is already an arena with that name!");
                  return true;
               }

               if (this.selectionMode.containsKey(var20.getName()) && ((Location[])this.selectionMode.get(var20.getName()))[0] != null && ((Location[])this.selectionMode.get(var20.getName()))[1] != null) {
                  var64 = new Cuboid(((Location[])this.selectionMode.get(var20.getName()))[0], ((Location[])this.selectionMode.get(var20.getName()))[1]);
                  if (var64.getSize() > this.config.maxArenaSize) {
                     var20.sendMessage(this.customization.prefix + "Your current selection exceeds the max arena size set in the config! " + ChatColor.LIGHT_PURPLE + "(" + var64.getSize() + "/" + this.config.maxArenaSize + ")!");
                     return true;
                  }

                  var20.sendMessage(this.customization.prefix + "Creating the arena " + ChatColor.AQUA + var26 + ChatColor.GRAY + "!");
                  var33 = new File(this.getDataFolder() + "/arenas/" + var26, "settings.yml");
                  var65 = YamlConfiguration.loadConfiguration(var33);
                  var65.set("team-size", Integer.valueOf(var4[2]));
                  var65.set("min-teams", Integer.valueOf(var4[3]));
                  var65.set("max-teams", Integer.valueOf(var4[4]));

                  try {
                     var65.save(var33);
                  } catch (IOException var14) {
                     var14.printStackTrace();
                  }

                  new ArenaSaveBlocksTask(this, var20, var26, var64);
                  return true;
               }

               var20.sendMessage(this.customization.prefix + "You haven't selected the 2 corners yet!");
               return true;
            }

            this.sendCommandUsage(var1, "Create", "<Name> <teamSize> <minTeams> <maxTeams>", "Team size is the amount of players in each team, if you put 1 for example, the arena will be in Solo mode", "Min teams is the minimum amount of teams for the arena to start", "Max teams is the maximum amount of teams the arena can handle");
            return true;
         }

         final String var6;
         final File var25;
         Arena var46;
         if (var5.equals("delete")) {
            if (!this.checkSender(var1, false, "skywars.delete")) {
               return true;
            }

            if (var4.length == 1) {
               this.sendCommandUsage(var1, "Delete", "<Name>");
               return true;
            }

            var6 = var4[1].toLowerCase();
            if (!this.arenas.containsKey(var6)) {
               var1.sendMessage(this.customization.prefix + "Couldn't find an arena with that name!");
               return true;
            }

            var46 = (Arena)this.arenas.get(var6);
            var46.stop(false);
            var25 = new File(this.getDataFolder() + "/arenas/", var46.name);
            this.filesManager.deleteFile(var25);
            this.arenas.remove(var6);
            this.updateArenasInventory();
            var1.sendMessage(this.customization.prefix + "Arena has been deleted!");
            return true;
         }

         if (var5.equals("list")) {
            if (var1 instanceof Player && ((PlayerData)this.playerData.get(var1.getName())).hasCooldown((Player)var1, "LIST_COMMAND", 5)) {
               return true;
            } else {
               var19 = this.colors[this.r.nextInt(this.colors.length)];
               var1.sendMessage("" + var19 + ChatColor.STRIKETHROUGH + "----------------" + ChatColor.YELLOW + " List " + var19 + ChatColor.STRIKETHROUGH + "----------------");
               var1.sendMessage(var19 + " - " + ChatColor.YELLOW + "Players: " + ChatColor.GREEN + this.players.size());
               var1.sendMessage(var19 + " - " + ChatColor.YELLOW + "Next leaderboard update: " + (this.leaderboard_updater == null ? ChatColor.RED + "Task is off" : ChatColor.GREEN + String.valueOf((this.leaderboard_updater_time - System.currentTimeMillis()) / 1000L) + ChatColor.AQUA + "s " + ChatColor.LIGHT_PURPLE + "(" + (this.leaderboard_updater_time - System.currentTimeMillis()) / 60000L + "m)"));
               var1.sendMessage(var19 + " - " + ChatColor.YELLOW + "Bukkit: " + ChatColor.GREEN + Bukkit.getBukkitVersion());
               var1.sendMessage(var19 + " - " + ChatColor.YELLOW + "Loaded arenas: " + ChatColor.GREEN + this.arenas.size());
               Iterator var74 = this.arenas.values().iterator();

               while(var74.hasNext()) {
                  var46 = (Arena)var74.next();
                  var1.sendMessage(var19 + " - " + ChatColor.YELLOW + var46.name + var19 + " | State: " + var46.state + var19 + " | Players: " + ChatColor.LIGHT_PURPLE + (var46.players.size() - var46.spectators.size()) + var19 + " | Spawnpoints: " + ChatColor.AQUA + var46.teams.size() + var19 + " | Chests: " + ChatColor.GREEN + var46.chests.size() + var19 + " | Team size: " + ChatColor.GOLD + var46.teamSize + var19 + " | Enabled: " + (var46.enabled ? ChatColor.GREEN + "true" : ChatColor.RED + "false"));
               }

               var1.sendMessage("" + var19 + ChatColor.STRIKETHROUGH + "-------------------------------------");
               return true;
            }
         }

         int var40;
         Arena var71;
         if (var5.equals("addspawn")) {
            if (!this.checkSender(var1, true, "skywars.addspawn")) {
               return true;
            }

            if (var4.length == 1) {
               this.sendCommandUsage(var1, "Addspawn", "<Name>");
               return true;
            }

            var20 = (Player)var1;
            var26 = var4[1].toLowerCase();
            if (!this.arenas.containsKey(var26)) {
               var20.sendMessage(this.customization.prefix + "Couldn't find an arena with that name!");
               return true;
            }

            var71 = (Arena)this.arenas.get(var26);
            var33 = new File(this.getDataFolder() + "/arenas/" + var71.name, "locations.dat");
            var65 = YamlConfiguration.loadConfiguration(var33);
            var40 = var71.teams.size() + 1;
            String var63 = this.getStringFromLocation(var20.getLocation(), true);
            var65.set("Spawnpoints." + var40, var63);

            try {
               var65.save(var33);
            } catch (IOException var15) {
               var15.printStackTrace();
            }

            var71.registerTeam(String.valueOf(var40), this.getLocationFromString(var63));
            var20.sendMessage(this.customization.prefix + "You have added spawnpoint " + ChatColor.LIGHT_PURPLE + "#" + var40 + ChatColor.GRAY + " to the arena " + ChatColor.AQUA + var71.name + ChatColor.GRAY + " at " + this.getReadableLocationString(var20.getLocation(), true));
            return true;
         }

         int var10;
         final Iterator var47;
         if (var5.equals("removespawn")) {
            if (!this.checkSender(var1, false, "skywars.removespawn")) {
               return true;
            }

            if (var4.length == 1) {
               this.sendCommandUsage(var1, "Removespawn", "<Name>");
               return true;
            }

            var6 = var4[1].toLowerCase();
            if (!this.arenas.containsKey(var6)) {
               var1.sendMessage(this.customization.prefix + "Couldn't find an arena with that name!");
               return true;
            }

            var46 = (Arena)this.arenas.get(var6);
            if (var46.teams.isEmpty()) {
               var1.sendMessage(this.customization.prefix + "That arena doesn't have any spawnpoints!");
               return true;
            }

            var25 = new File(this.getDataFolder() + "/arenas/" + var46.name, "locations.dat");
            YamlConfiguration var77 = YamlConfiguration.loadConfiguration(var25);
            var10 = var46.teams.size();
            var77.set("Spawnpoints." + var10, (Object)null);

            try {
               var77.save(var25);
            } catch (IOException var16) {
               var16.printStackTrace();
            }

            var47 = var46.teams.keySet().iterator();

            while(var47.hasNext()) {
               Team var67 = (Team)var47.next();
               if (var67.getName().equals(String.valueOf(var10))) {
                  var46.teams.remove(var67);
                  break;
               }
            }

            var1.sendMessage(this.customization.prefix + "You have removed the last spawnpoint!");
            return true;
         }

         Player var23;
         if (var5.equals("setspectators")) {
            if (!this.checkSender(var1, true, "skywars.setspectators")) {
               return true;
            }

            if (var4.length < 2) {
               this.sendCommandUsage(var1, "Setspectators", "<Arena>");
               return true;
            }

            var6 = var4[1].toLowerCase();
            if (!this.arenas.containsKey(var6)) {
               var1.sendMessage(this.customization.prefix + "Couldn't find an arena with that name!");
               return true;
            }

            var23 = (Player)var1;
            var71 = (Arena)this.arenas.get(var6);
            var33 = new File(this.getDataFolder() + "/arenas/" + var71.name, "locations.dat");
            var65 = YamlConfiguration.loadConfiguration(var33);
            var65.set("Spectators-Spawnpoint", this.getStringFromLocation(var23.getLocation(), true));

            try {
               var65.save(var33);
            } catch (IOException var17) {
               var17.printStackTrace();
            }

            var71.spectatorsLocation = var23.getLocation().getBlock().getLocation().add(0.5D, 1.0D, 0.5D);
            var23.sendMessage(this.customization.prefix + "You have set the spectators spawnpoint for the arena " + ChatColor.LIGHT_PURPLE + var71.name + ChatColor.GRAY + "!");
            return true;
         }

         if (var5.equals("start")) {
            if (!this.checkSender(var1, false, "skywars.start")) {
               return true;
            }

            if (var4.length == 1) {
               if (!(var1 instanceof Player) || ((PlayerData)this.playerData.get(var1.getName())).arena == null) {
                  this.sendCommandUsage(var1, "Start", "<Name>");
                  return true;
               }

               var6 = ((PlayerData)this.playerData.get(var1.getName())).arena.name.toLowerCase();
            } else {
               var6 = var4[1].toLowerCase();
            }

            if (!this.arenas.containsKey(var6)) {
               var1.sendMessage(this.customization.prefix + "Couldn't find an arena with that name!");
               return true;
            }

            var46 = (Arena)this.arenas.get(var6);
            if (!var46.state.AVAILABLE()) {
               var1.sendMessage(this.customization.prefix + "You may not start the arena in its current state!");
               return true;
            }

            if (var46.getAliveTeams().size() < var46.minTeams) {
               var1.sendMessage(this.customization.prefix + "There must be at least 2 teams for the arena to start!");
               return true;
            }

            var46.start();
            var1.sendMessage(this.customization.prefix + "You have forced the arena " + ChatColor.YELLOW + var46.name + ChatColor.GRAY + " to start!");
            return true;
         }

         if (var5.equals("stop")) {
            if (!this.checkSender(var1, false, "skywars.stop")) {
               return true;
            }

            if (var4.length == 1) {
               if (!(var1 instanceof Player) || ((PlayerData)this.playerData.get(var1.getName())).arena == null) {
                  this.sendCommandUsage(var1, "Start", "<Name>");
                  return true;
               }

               var6 = ((PlayerData)this.playerData.get(var1.getName())).arena.name.toLowerCase();
            } else {
               var6 = var4[1].toLowerCase();
            }

            if (!this.arenas.containsKey(var6)) {
               var1.sendMessage(this.customization.prefix + "Couldn't find an arena with that name!");
               return true;
            }

            var46 = (Arena)this.arenas.get(var6);
            if (!var46.state.equals(Enums.ArenaState.INGAME) && !var46.state.equals(Enums.ArenaState.ENDING)) {
               var1.sendMessage(this.customization.prefix + "You may not stop the arena in its current state!");
               return true;
            }

            var46.stop(true);
            var1.sendMessage(this.customization.prefix + "You have forced the arena " + ChatColor.YELLOW + var46.name + ChatColor.GRAY + " to stop!");
            return true;
         }

         if (var5.equals("coins")) {
            if (!this.checkSender(var1, false, "skywars.coins")) {
               return true;
            }

            if (var4.length >= 4 && ((var6 = var4[1].toLowerCase()).equals("add") || var6.equals("remove") || var6.equals("set")) && this.checkNumbers(var4[3])) {
               var23 = Bukkit.getPlayer(var4[2]);
               final int var70 = Integer.valueOf(var4[3]);
               if (var23 == null) {
                  var1.sendMessage(this.customization.prefix + "Couldn't find that players online, looking up the database...");
                  (new BukkitRunnable() {
                     boolean increment = !var6.equals("set");

                     public void run() {
                        try {
                           boolean var1x = Skywars.api.modifyOfflinePlayerStat(var4[2], Enums.Stat.COINS, var6.equals("remove") ? -var70 : var70, this.increment);
                           if (var1x) {
                              var1.sendMessage(Skywars.this.customization.prefix + ChatColor.YELLOW + var4[2] + ChatColor.GRAY + " coins were updated!");
                           } else {
                              var1.sendMessage(Skywars.this.customization.prefix + "Couldn't find that player in the database!");
                           }
                        } catch (SQLException var2) {
                           var2.printStackTrace();
                        }

                     }
                  }).runTaskAsynchronously(this.plugin);
                  return true;
               }

               PlayerData var75 = (PlayerData)this.playerData.get(var23.getName());
               var10 = var75.getCoins(var23);
               if (var6.equals("add")) {
                  var75.addCoins(var23, var70);
               } else if (var6.equals("remove")) {
                  var75.removeCoins(var23, var70);
               } else if (var6.equals("set")) {
                  var75.setCoins(var23, var70);
               }

               if (var10 != var75.getCoins(var23)) {
                  var1.sendMessage(this.customization.prefix + "You have " + ChatColor.GREEN + "successfully" + ChatColor.GRAY + " changed " + ChatColor.AQUA + var23.getName() + "'s" + ChatColor.GRAY + " coins!");
                  var23.sendMessage((String)this.customization.messages.get("Coins-Update") + (var75.getCoins(var23) - var10 > 0 ? ChatColor.GREEN + " (+" + (var75.getCoins(var23) - var10) + ")!" : ChatColor.RED + " (" + (var75.getCoins(var23) - var10) + ")!"));
                  if (var75.lobbyScoreboard != null) {
                     var75.lobbyScoreboard.update((String)this.customization.scoreboard.get("Coins"), var75.getCoins(var23), true);
                  }

                  var75.save = true;
               } else {
                  var1.sendMessage(this.customization.prefix + "User coins haven't changed!");
               }

               return true;
            }

            this.sendCommandUsage(var1, "Coins", "<Action> <Player> <Amount>", "Action can only be one of the following", "Add, Remove, Set");
            return true;
         }

         int var31;
         if (var5.equals("setmodifier")) {
            if (!this.checkSender(var1, false, "skywars.setmodifier")) {
               return true;
            }

            if (var4.length >= 3 && this.checkNumbers(var4[2])) {
               var20 = Bukkit.getPlayer(var4[1]);
               if (var20 == null) {
                  var1.sendMessage(this.customization.prefix + "Couldn't find a player with that name! looking up the database...");
                  (new BukkitRunnable() {
                     public void run() {
                        try {
                           boolean var1x = Skywars.api.modifyOfflinePlayerStat(var4[1], Enums.Stat.MODIFIER, Integer.valueOf(var4[2]), false);
                           if (var1x) {
                              var1.sendMessage(Skywars.this.customization.prefix + ChatColor.YELLOW + var4[1] + ChatColor.GRAY + " modifier was updated!");
                           } else {
                              var1.sendMessage(Skywars.this.customization.prefix + "Couldn't find that player in the database!");
                           }
                        } catch (SQLException var2) {
                           var2.printStackTrace();
                        }

                     }
                  }).runTaskAsynchronously(this.plugin);
                  return true;
               }

               int var41 = Integer.valueOf(var4[2]);
               PlayerData var69 = (PlayerData)this.playerData.get(var20.getName());
               var31 = var69.modifier;
               if (var41 != var31) {
                  var69.modifier = var41;
                  var1.sendMessage(this.customization.prefix + "You have " + ChatColor.GREEN + "successfully" + ChatColor.GRAY + " changed " + ChatColor.YELLOW + var20.getName() + ChatColor.GRAY + " modifier to " + ChatColor.AQUA + var41 + ChatColor.GRAY + "!");
                  var20.sendMessage((String)this.customization.messages.get("Modifier-Update"));
                  var69.save = true;
               } else {
                  var1.sendMessage(this.customization.prefix + "The player " + ChatColor.YELLOW + var20.getName() + ChatColor.GRAY + " already has this modifier!");
               }

               return true;
            }

            this.sendCommandUsage(var1, "Setmodifier", "<Player> <Modifier>");
            return true;
         }

         Player var8;
         if (var5.equals("holograms")) {
            if (!this.checkSender(var1, true, "skywars.holograms")) {
               return true;
            }

            if (var4.length < 3 || !(var6 = var4[1].toLowerCase()).equals("set") && !var6.equals("remove") || !(var26 = var4[2].toLowerCase()).equals("stats") && !var26.equals("leaderboard")) {
               this.sendCommandUsage(var1, "Holograms", "Set/Remove Stats/Leaderboard");
               return true;
            }

            if (this.hologramsManager == null) {
               var1.sendMessage(this.customization.prefix + "HolographicDisplays doesn't seem to be loaded!");
               return true;
            }

            var8 = (Player)var1;
            if (var6.equals("set")) {
               Location var73 = var8.getLocation();
               if (var26.equals("stats")) {
                  var73.add(0.0D, 4.0D, 0.0D);
                  this.hologramsManager.setStats(var73);
                  this.getConfig().set("Holographic-Stats", this.getStringFromLocation(var73, false));
                  var8.sendMessage(this.customization.prefix + "You have set the holographic stats location!");
               } else {
                  var73.add(0.0D, 3.0D, 0.0D);
                  this.hologramsManager.setLeaderboard(var73, true);
                  this.getConfig().set("Holographic-Leaderboard", this.getStringFromLocation(var73, false));
                  var8.sendMessage(this.customization.prefix + "You have set the holographic leaderboard location!");
               }
            } else if (var26.equals("stats")) {
               this.hologramsManager.setStats((Location)null);
               this.getConfig().set("Holographic-Stats", (Object)null);
               var8.sendMessage(this.customization.prefix + "You have removed the holographic stats location!");
            } else {
               this.hologramsManager.setLeaderboard((Location)null, false);
               this.getConfig().set("Holographic-Leaderboard", (Object)null);
               var8.sendMessage(this.customization.prefix + "You have removed the holographic leaderboard location!");
            }

            this.saveConfig();
            return true;
         }

         if (var5.equals("reset")) {
            if (!this.checkSender(var1, false, "skywars.reset")) {
               return true;
            }

            if (var4.length == 1) {
               this.sendCommandUsage(var1, "Reset", "<Player>");
               return true;
            }

            var20 = Bukkit.getPlayer(var4[1]);
            final long var37 = System.currentTimeMillis();
            var1.sendMessage(this.customization.prefix + "Looking up the database...");
            (new BukkitRunnable() {
               public void run() {
                  boolean var1x = false;
                  if (Skywars.this.config.mysql_enabled) {
                     try {
                        Connection var2 = Skywars.this.mysql.getConnection();
                        PreparedStatement var3 = var2.prepareStatement(Skywars.this.mysql.SELECT);
                        var3.setString(1, var4[1]);
                        ResultSet var4x = var3.executeQuery();
                        if (var4x.next()) {
                           var3 = var2.prepareStatement(Skywars.this.mysql.DELETE_PLAYER);
                           var3.setString(1, var4[1]);
                           var3.execute();
                           var1x = true;
                        }

                        var4x.close();
                        var3.close();
                     } catch (SQLException var5) {
                        var5.printStackTrace();
                     }
                  } else {
                     Iterator var7 = Skywars.api.getPlayersFiles().iterator();

                     while(var7.hasNext()) {
                        File var6 = (File)var7.next();
                        String var8 = Skywars.this.config.useUUID ? YamlConfiguration.loadConfiguration(var6).getString("Name") : var6.getName();
                        if (var8.equalsIgnoreCase(var4[1])) {
                           var6.delete();
                           var1x = true;
                           break;
                        }
                     }
                  }

                  if (!var1x && var20 == null) {
                     var1.sendMessage(Skywars.this.customization.prefix + "Couldn't find that player in the database!");
                  } else {
                     if (var20 != null) {
                        ((PlayerData)Skywars.this.playerData.get(var20.getName())).load(var20);
                        ((PlayerData)Skywars.this.playerData.get(var20.getName())).save = true;
                     }

                     var1.sendMessage(Skywars.this.customization.prefix + "Player stats have been reset! took " + ChatColor.LIGHT_PURPLE + (System.currentTimeMillis() - var37) + "ms!");
                  }

               }
            }).runTaskAsynchronously(this);
            return true;
         }

         if (var5.equals("edit")) {
            if (!this.checkSender(var1, true, "skywars.edit")) {
               return true;
            }

            if (var4.length == 1) {
               this.sendCommandUsage(var1, "Edit", "<Name>");
               return true;
            }

            var20 = (Player)var1;
            var26 = var4[1].toLowerCase();
            if (!this.arenas.containsKey(var26)) {
               var20.sendMessage(this.customization.prefix + "Couldn't find an arena with that name!");
               return true;
            }

            var20.openInventory(((Arena)this.arenas.get(var26)).editor);
            return true;
         }

         if (var5.equals("editmode")) {
            if (!this.checkSender(var1, true, "skywars.editmode")) {
               return true;
            }

            var20 = (Player)var1;
            if (!this.config.bungee_mode_enabled) {
               var20.sendMessage(this.customization.prefix + "You must be in BungeeMode to use this command!");
               return true;
            }

            if (this.players.contains(var20.getName())) {
               var20.sendMessage(this.customization.prefix + "You have " + ChatColor.GREEN + "enabled" + ChatColor.GRAY + " the editing mode!");
               var26 = this.config.bungee_mode_hub;
               this.config.bungee_mode_hub = String.valueOf(this.r.nextInt());
               this.leave(var20, true);
               this.config.bungee_mode_hub = var26;
            } else if (this.plugin.lobbyLocation != null) {
               var20.sendMessage(this.customization.prefix + "You have " + ChatColor.RED + "disabled" + ChatColor.GRAY + " the editing mode!");
               this.join(var20);
            } else {
               var20.sendMessage((String)this.plugin.customization.messages.get("Lobby-Not-Set"));
            }

            return true;
         }

         Iterator var9;
         final long var29;
         if (var5.equals("reload")) {
            if (!this.checkSender(var1, false, "skywars.reload")) {
               return true;
            }

            var1.sendMessage(this.customization.prefix + "Attempting to reload the plugin!");
            long var21 = System.currentTimeMillis();
            var9 = this.getPlayers(this.players).iterator();

            while(var9.hasNext()) {
               var8 = (Player)var9.next();
               this.leave(var8, true);
               var8.sendMessage((String)this.customization.messages.get("Reload-Kick"));
            }

            this.mainSetup();
            var29 = System.currentTimeMillis();
            var1.sendMessage(this.customization.prefix + "The plugin has been " + ChatColor.GREEN + "successfully" + ChatColor.GRAY + " reloaded! took " + ChatColor.LIGHT_PURPLE + (var29 - var21) + "ms!");
            return true;
         }

         if (var5.equals("updateregion")) {
            if (!this.checkSender(var1, true, "skywars.updateregion")) {
               return true;
            }

            if (var4.length == 1) {
               this.sendCommandUsage(var1, "Updateregion", "<Arena>");
               return true;
            }

            var20 = (Player)var1;
            var26 = var4[1];
            if (!this.arenas.containsKey(var26.toLowerCase())) {
               var20.sendMessage(this.customization.prefix + "Couldn't find an arena with that name!");
               return true;
            }

            if (this.selectionMode.containsKey(var20.getName()) && ((Location[])this.selectionMode.get(var20.getName()))[0] != null && ((Location[])this.selectionMode.get(var20.getName()))[1] != null) {
               var64 = new Cuboid(((Location[])this.selectionMode.get(var20.getName()))[0], ((Location[])this.selectionMode.get(var20.getName()))[1]);
               if (var64.getSize() > this.config.maxArenaSize) {
                  var20.sendMessage(this.customization.prefix + "Your current selection exceeds the max arena size set in the config! " + ChatColor.LIGHT_PURPLE + "(" + var64.getSize() + "/" + this.config.maxArenaSize + ")!");
                  return true;
               }

               var20.sendMessage(this.customization.prefix + "Updating the arena region...");
               Arena var72 = (Arena)this.arenas.get(var26.toLowerCase());
               var72.enabled = false;
               var72.stop(false);
               new ArenaSaveBlocksTask(this, var20, var72.name, var64);
               return true;
            }

            var20.sendMessage(this.customization.prefix + "You haven't selected the 2 corners yet!");
            return true;
         }

         ChatColor var22;
         final String var28;
         String var38;
         Iterator var39;
         String var42;
         if (var5.equals("chestmanager") || var5.equals("cm")) {
            if (!this.checkSender(var1, true, "skywars.chestmanager")) {
               return true;
            }

            var20 = (Player)var1;
            if (var4.length == 1) {
               var22 = this.colors[this.r.nextInt(this.colors.length)];
               var20.sendMessage("" + var22 + ChatColor.STRIKETHROUGH + "------------" + ChatColor.YELLOW + " Skywars " + ChatColor.RED + "ChestManager " + var22 + ChatColor.STRIKETHROUGH + "------------");
               var20.sendMessage(var22 + " - " + ChatColor.YELLOW + "/sw ChestManager Create <Category> <Type [Optional]> " + var22 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Creates a new chest category/type");
               var20.sendMessage(var22 + " - " + ChatColor.YELLOW + "/sw ChestManager Delete <Category> <Type [Optional]> " + var22 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Deletes an existing chest category/type");
               var20.sendMessage(var22 + " - " + ChatColor.YELLOW + "/sw ChestManager Edit <Category> <Type> " + var22 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Allows for chest type modification");
               var20.sendMessage(var22 + " - " + ChatColor.YELLOW + "/sw ChestManager Tool <Type> " + var22 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Gives you a wand to change a chest type");
               var20.sendMessage(var22 + " - " + ChatColor.YELLOW + "/sw ChestManager Updateregion <Type> " + var22 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Changes all chests within a region to a new type");
               var20.sendMessage(var22 + " - " + ChatColor.YELLOW + "/sw ChestManager List " + var22 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Shows a list of chest types!");
               var20.sendMessage("" + var22 + ChatColor.STRIKETHROUGH + "--------------------------------------------");
               return true;
            }

            var26 = var4[1].toLowerCase();
            Iterator var43;
            ChestType var48;
            HashMap var50;
            if (var26.equals("create")) {
               if (var4.length == 2) {
                  this.sendCommandUsage(var1, "ChestManager Create", "<Category> <Type [Optional]>");
                  return true;
               }

               if (var4.length == 3) {
                  var28 = var4[2];
                  var43 = this.chests.keySet().iterator();

                  while(var43.hasNext()) {
                     var42 = (String)var43.next();
                     if (var42.equalsIgnoreCase(var28)) {
                        var20.sendMessage(this.customization.prefix + "There is already a category with that name!");
                        return true;
                     }
                  }

                  FileConfiguration var66 = this.filesManager.getConfig("chests.yml");
                  var66.set("Chests." + var28 + ".Default.min-items", 6);
                  var66.set("Chests." + var28 + ".Default.max-items", 9);
                  var66.set("Chests." + var28 + ".Default.items", new ArrayList());
                  this.filesManager.saveConfig("chests.yml");
                  var50 = new HashMap();
                  var48 = new ChestType(this, "Default", new ArrayList(), 6, 9);
                  var50.put("default", var48);
                  this.chests.put(var28, var50);

                  Arena var58;
                  for(Iterator var51 = this.arenas.values().iterator(); var51.hasNext(); var58.votesManager = new VotesManager(this)) {
                     var58 = (Arena)var51.next();
                  }

                  var48.editor.open(var20);
                  this.editingChests.put(var20.getName(), var28);
                  var20.sendMessage(this.customization.prefix + "You have successfully created a new chest category! 'Default' chest type was added to that category automatically");
               } else {
                  var28 = var4[2];
                  HashMap var68 = null;
                  var39 = this.chests.keySet().iterator();

                  while(var39.hasNext()) {
                     var38 = (String)var39.next();
                     if (var38.equalsIgnoreCase(var28)) {
                        var28 = var38;
                        var68 = (HashMap)this.chests.get(var38);
                        break;
                     }
                  }

                  if (var68 == null) {
                     var20.sendMessage(this.customization.prefix + "Could not find a category with that name!");
                     return true;
                  }

                  var38 = var4[3].toLowerCase();
                  if (var68.containsKey(var38)) {
                     var20.sendMessage(this.customization.prefix + "There is already a chest type with that name!");
                     return true;
                  }

                  var48 = new ChestType(this, var4[3], new ArrayList(), 6, 9);
                  var68.put(var38, var48);
                  FileConfiguration var59 = this.filesManager.getConfig("chests.yml");
                  var59.set("Chests." + var28 + "." + var4[3] + ".min-items", 6);
                  var59.set("Chests." + var28 + "." + var4[3] + ".max-items", 9);
                  var59.set("Chests." + var28 + "." + var4[3] + ".items", new ArrayList());
                  this.filesManager.saveConfig("chests.yml");
                  var48.editor.open(var20);
                  this.editingChests.put(var20.getName(), var28);
                  var20.sendMessage(this.customization.prefix + "You have " + ChatColor.GREEN + "successfully" + ChatColor.GRAY + " created a new chest type in the category " + ChatColor.GREEN + var28 + "!");
                  var20.sendMessage(this.customization.prefix + ChatColor.GOLD + "Please note that when creating a new chest type in a specific category such as 'Normal' category, other categories should also have a chest type with the same name otherwise errors will occur");
               }

               return true;
            }

            ChestType var53;
            boolean var55;
            if (var26.equals("delete")) {
               if (var4.length == 2) {
                  this.sendCommandUsage(var1, "ChestManager Delete", "<Category> <Type [Optional]>");
                  return true;
               }

               var28 = var4[2];
               var55 = false;
               var39 = this.chests.keySet().iterator();

               while(var39.hasNext()) {
                  var38 = (String)var39.next();
                  if (var38.equalsIgnoreCase(var28)) {
                     var28 = var38;
                     var55 = true;
                     break;
                  }
               }

               if (!var55) {
                  var20.sendMessage(this.customization.prefix + "Could not find a category with that name!");
                  return true;
               }

               if (var4.length == 3) {
                  if (var28.equalsIgnoreCase("Normal")) {
                     var20.sendMessage(this.customization.prefix + "You can not delete this category!");
                     return true;
                  }

                  FileConfiguration var56 = this.filesManager.getConfig("chests.yml");
                  var56.set("Chests." + var28, (Object)null);
                  this.filesManager.saveConfig("chests.yml");
                  this.chests.remove(var28);

                  Arena var60;
                  for(var47 = this.arenas.values().iterator(); var47.hasNext(); var60.votesManager = new VotesManager(this)) {
                     var60 = (Arena)var47.next();
                  }

                  var20.sendMessage(this.customization.prefix + "You have successfully removed that chest category!");
               } else {
                  var38 = var4[3].toLowerCase();
                  HashMap var62 = (HashMap)this.chests.get(var28);
                  if (!var62.containsKey(var38)) {
                     var20.sendMessage(this.customization.prefix + "Couldn't find a chest type with that name!");
                     return true;
                  }

                  if (var38.equals("default")) {
                     var20.sendMessage(this.customization.prefix + "You cant delete the default chest type!");
                     return true;
                  }

                  var53 = (ChestType)var62.get(var38);
                  this.filesManager.getConfig("chests.yml").set("Chests." + var28 + "." + var53.name, (Object)null);
                  this.filesManager.saveConfig("chests.yml");
                  var62.remove(var38);
                  var20.sendMessage(this.customization.prefix + "You have " + ChatColor.RED + "removed" + ChatColor.GRAY + " the chest type " + ChatColor.AQUA + var53.name + ChatColor.GRAY + " from the " + ChatColor.LIGHT_PURPLE + var28 + ChatColor.GRAY + " category!");
               }

               return true;
            }

            if (var26.equals("edit")) {
               if (var4.length == 2) {
                  this.sendCommandUsage(var1, "ChestManager Edit", "<Category> <Type>");
                  return true;
               }

               var28 = var4[2];
               var55 = false;
               var39 = this.chests.keySet().iterator();

               while(var39.hasNext()) {
                  var38 = (String)var39.next();
                  if (var38.equalsIgnoreCase(var28)) {
                     var28 = var38;
                     var55 = true;
                     break;
                  }
               }

               if (!var55) {
                  var20.sendMessage(this.customization.prefix + "Could not find a category with that name!");
                  return true;
               }

               var50 = (HashMap)this.chests.get(var28);
               String var57 = var4[3].toLowerCase();
               if (!var50.containsKey(var57)) {
                  var20.sendMessage(this.customization.prefix + "Couldn't find a chest type with that name in that category!");
                  return true;
               }

               var53 = (ChestType)var50.get(var57);
               var53.editor.open(var20);
               this.editingChests.put(var20.getName(), var28);
               return true;
            }

            if (var26.equals("tool")) {
               if (var4.length == 2) {
                  this.sendCommandUsage(var1, "ChestManager Edit", "<Type>");
                  return true;
               }

               var28 = var4[2].toLowerCase();
               if (!((HashMap)this.chests.get("Normal")).containsKey(var28)) {
                  var20.sendMessage(this.customization.prefix + "Couldn't find a chest type with that name!");
                  return true;
               }

               ItemStack var52 = (new ItemStackBuilder(this.chest_tool_itemstack.clone())).addLore(" ", ChatColor.YELLOW + "Type: " + ChatColor.GOLD + ((ChestType)((HashMap)this.chests.get("Normal")).get(var28)).name).build();
               var20.getInventory().addItem(new ItemStack[]{var52});
               var20.sendMessage(this.customization.prefix + "You have received the " + var52.getItemMeta().getDisplayName() + ChatColor.GRAY + "!");
               return true;
            }

            if (var26.equals("updateregion")) {
               if (var4.length == 2) {
                  this.sendCommandUsage(var1, "ChestManager Updateregion", "<Type>");
                  return true;
               }

               var28 = var4[2].toLowerCase();
               if (!((HashMap)this.chests.get("Normal")).containsKey(var28)) {
                  var20.sendMessage(this.customization.prefix + "Couldn't find a chest type with that name!");
                  return true;
               }

               if (this.selectionMode.containsKey(var20.getName()) && ((Location[])this.selectionMode.get(var20.getName()))[0] != null && ((Location[])this.selectionMode.get(var20.getName()))[1] != null) {
                  var42 = null;
                  var39 = this.arenas.values().iterator();

                  final Arena var45;
                  while(var39.hasNext()) {
                     var45 = (Arena)var39.next();
                     if (var45.cuboid.contains(((Location[])this.selectionMode.get(var20.getName()))[0])) {
                        var42 = var45.name.toLowerCase();
                        break;
                     }
                  }

                  if (var42 == null) {
                     var20.sendMessage(this.plugin.customization.prefix + "Couldn't find an arena that contains your selection");
                     return true;
                  }

                  var45 = (Arena)this.arenas.get(var42);
                  Cuboid var54 = new Cuboid(((Location[])this.selectionMode.get(var20.getName()))[0], ((Location[])this.selectionMode.get(var20.getName()))[1]);
                  var20.sendMessage(this.customization.prefix + "Scanning " + ChatColor.AQUA + var54.getSize() + ChatColor.GRAY + " block(s)!");
                  var47 = var54.iterator();
                  (new BukkitRunnable() {
                     int current = 0;
                     int detected = 0;

                     public void run() {
                        for(; var47.hasNext() && this.current < Skywars.this.config.rollback_scan_speed; ++this.current) {
                           Block var1 = (Block)var47.next();
                           if (var1.getType().equals(Material.CHEST)) {
                              var45.chests.put(var1.getLocation(), var28);
                              ++this.detected;
                           }
                        }

                        this.current = 0;
                        if (!var47.hasNext()) {
                           this.cancel();
                           File var7 = new File(Skywars.this.plugin.getDataFolder() + "/arenas/" + var45.name, "locations.dat");
                           YamlConfiguration var2 = YamlConfiguration.loadConfiguration(var7);
                           ArrayList var3 = new ArrayList();
                           Iterator var5 = var45.chests.keySet().iterator();

                           while(var5.hasNext()) {
                              Location var4 = (Location)var5.next();
                              var3.add(var4.getBlockX() + ":" + var4.getBlockY() + ":" + var4.getBlockZ() + ":" + var4.getBlock().getTypeId() + ":" + var4.getBlock().getData() + ":" + (String)var45.chests.get(var4));
                           }

                           var2.set("Chests", var3.toString());

                           try {
                              var2.save(var7);
                           } catch (IOException var6) {
                              var6.printStackTrace();
                           }

                           var20.sendMessage(Skywars.this.customization.prefix + ChatColor.GREEN + "Successfully " + ChatColor.GRAY + "updated " + ChatColor.LIGHT_PURPLE + this.detected + ChatColor.GRAY + " chest(s)! task has been completed!");
                        }

                     }
                  }).runTaskTimer(this, 0L, 1L);
                  return true;
               }

               var20.sendMessage(this.customization.prefix + "You haven't selected the 2 corners yet!");
               return true;
            }

            if (!var26.equals("list")) {
               var20.sendMessage(this.customization.prefix + "Unknown command! use /sw ChestManager for a list of commands");
               return true;
            }

            ChatColor var35 = this.colors[this.r.nextInt(this.colors.length)];
            var20.sendMessage("" + var35 + ChatColor.STRIKETHROUGH + "------------" + ChatColor.YELLOW + " Skywars " + ChatColor.RED + "ChestManager " + var35 + ChatColor.STRIKETHROUGH + "------------");
            var43 = this.chests.keySet().iterator();

            while(var43.hasNext()) {
               var42 = (String)var43.next();
               var20.sendMessage(ChatColor.LIGHT_PURPLE + " " + Enums.SPECIAL_CHARACTER.ARROW + " " + var42);
               var47 = ((HashMap)this.chests.get(var42)).values().iterator();

               while(var47.hasNext()) {
                  var48 = (ChestType)var47.next();
                  var20.sendMessage(ChatColor.GRAY + "    " + Enums.SPECIAL_CHARACTER.ARROW + " " + ChatColor.AQUA + var48.name + ChatColor.GRAY + " - Items: " + var35 + var48.items.size() + ChatColor.GRAY + " - Min items: " + var35 + var48.minItems + ChatColor.GRAY + " - Max items: " + var35 + var48.maxItems);
               }
            }

            var20.sendMessage("" + var35 + ChatColor.STRIKETHROUGH + "--------------------------------------------");
            return true;
         }

         if (var5.equals("worldmanager") || var5.equals("wm")) {
            if (!this.checkSender(var1, false, "skywars.worldmanager")) {
               return true;
            }

            if (var4.length == 1) {
               var19 = this.colors[this.r.nextInt(this.colors.length)];
               var1.sendMessage("" + var19 + ChatColor.STRIKETHROUGH + "------------" + ChatColor.YELLOW + " Skywars " + ChatColor.RED + "WorldManager " + var19 + ChatColor.STRIKETHROUGH + "------------");
               var1.sendMessage(var19 + " - " + ChatColor.YELLOW + "/sw WorldManager Create <World> " + var19 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Creates a new world");
               var1.sendMessage(var19 + " - " + ChatColor.YELLOW + "/sw WorldManager Delete <World> " + var19 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Deletes an existing world");
               var1.sendMessage(var19 + " - " + ChatColor.YELLOW + "/sw WorldManager Import <World> " + var19 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Imports a new world!");
               var1.sendMessage(var19 + " - " + ChatColor.YELLOW + "/sw WorldManager Backup <World> " + var19 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Creates a backup of a world");
               var1.sendMessage(var19 + " - " + ChatColor.YELLOW + "/sw WorldManager Restore <World> " + var19 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Restores a world from the backup file");
               var1.sendMessage(var19 + " - " + ChatColor.YELLOW + "/sw WorldManager Tp <World> " + var19 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Teleports you to a world");
               var1.sendMessage(var19 + " - " + ChatColor.YELLOW + "/ew WorldManager Setspawn <World> " + var19 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Sets the spawn of a world");
               var1.sendMessage(var19 + " - " + ChatColor.YELLOW + "/ew WorldManager Backupall " + var19 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Creates a backup of all loaded worlds");
               var1.sendMessage(var19 + " - " + ChatColor.YELLOW + "/sw WorldManager List " + var19 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Lists loaded worlds!");
               var1.sendMessage("" + var19 + ChatColor.STRIKETHROUGH + "--------------------------------------------");
               return true;
            }

            var6 = var4[1].toLowerCase();
            final List var24;
            long var36;
            int var44;
            if (var6.equals("create")) {
               var24 = Arrays.asList("normal", "nether", "the_end", "empty");
               if (var4.length >= 4 && var24.contains(var4[3].toLowerCase())) {
                  var28 = var4[2];
                  var42 = var4[3].toLowerCase();
                  if (Bukkit.getWorld(var28) != null) {
                     var1.sendMessage(this.customization.prefix + "There is a world with that name!");
                     return true;
                  }

                  String[] var49;
                  var44 = (var49 = Bukkit.getWorldContainer().list()).length;

                  for(var40 = 0; var40 < var44; ++var40) {
                     var38 = var49[var40];
                     if (var38.equalsIgnoreCase(var28)) {
                        var1.sendMessage(this.customization.prefix + "Seems like there is an unloaded world with that name! try using /sw WorldManager Import <Name>");
                        return true;
                     }
                  }

                  var1.sendMessage(this.customization.prefix + "Creating a new world!");
                  var36 = System.currentTimeMillis();
                  if (var42.equals("empty")) {
                     Bukkit.createWorld((new WorldCreator(var28)).generator(new ChunkGenerator() {
                        public byte[][] generateBlockSections(World var1, Random var2, int var3, int var4, BiomeGrid var5) {
                           return new byte[var1.getMaxHeight() / 16][];
                        }
                     }));
                  } else {
                     Bukkit.createWorld((new WorldCreator(var28)).environment(Environment.valueOf(var42.toUpperCase())));
                  }

                  var1.sendMessage(this.customization.prefix + "World has been created! took " + ChatColor.LIGHT_PURPLE + (System.currentTimeMillis() - var36) + "ms" + ChatColor.GRAY + " to complete the process! use " + ChatColor.GREEN + "/sw WorldManager Tp " + var28 + ChatColor.GRAY + " if you would like to be teleported to that world!");
                  return true;
               }

               this.sendCommandUsage(var1, "WorldManager Create", "<Name> <Type>", "Type can be normal, nether, the_end, empty");
               return true;
            }

            World var27;
            if (var6.equals("delete")) {
               if (var4.length == 2) {
                  this.sendCommandUsage(var1, "WorldManager Delete", "<Name>");
                  return true;
               }

               var26 = var4[2].toLowerCase();
               var27 = Bukkit.getWorld(var26);
               var33 = null;
               if (var27 != null) {
                  if (!var27.getPlayers().isEmpty()) {
                     var1.sendMessage(this.customization.prefix + "The world contains players. so the world can't be deleted!");
                     var39 = var27.getPlayers().iterator();

                     while(var39.hasNext()) {
                        Player var32 = (Player)var39.next();
                        var1.sendMessage(this.customization.prefix + "- " + var32.getName());
                     }

                     return true;
                  }

                  var33 = var27.getWorldFolder();
                  Bukkit.unloadWorld(var27, false);
               } else {
                  var1.sendMessage(this.customization.prefix + "The world you looking for seems to be unloaded, looking up folders...");
                  File[] var13;
                  var44 = (var13 = Bukkit.getWorldContainer().listFiles()).length;

                  for(var40 = 0; var40 < var44; ++var40) {
                     File var34 = var13[var40];
                     if (var34.getName().equalsIgnoreCase(var4[2])) {
                        var33 = var34;
                        break;
                     }
                  }
               }

               if (var33 != null) {
                  var36 = System.currentTimeMillis();
                  this.filesManager.deleteFile(var33);
                  var1.sendMessage(this.customization.prefix + "World has been deleted! took " + ChatColor.LIGHT_PURPLE + (System.currentTimeMillis() - var36) + "ms!");
               } else {
                  var1.sendMessage(this.customization.prefix + "Couldn't find a world with that name!");
               }

               return true;
            }

            if (var6.equals("import")) {
               if (var4.length == 2) {
                  this.sendCommandUsage(var1, "WorldManager Import", "<Name>");
                  return true;
               }

               var26 = var4[2];
               if (Bukkit.getWorld(var26) != null) {
                  var1.sendMessage(this.customization.prefix + "There is a loaded world with that name!");
                  return true;
               }

               String[] var11;
               var10 = (var11 = Bukkit.getWorldContainer().list()).length;

               for(var31 = 0; var31 < var10; ++var31) {
                  var28 = var11[var31];
                  if (var26.equalsIgnoreCase(var28)) {
                     var1.sendMessage(this.customization.prefix + "Importing the world...");
                     long var12 = System.currentTimeMillis();
                     if (this.config.emptyChunkGenerator) {
                        Bukkit.createWorld((new WorldCreator(var28)).generator(new ChunkGenerator() {
                           public byte[][] generateBlockSections(World var1, Random var2, int var3, int var4, BiomeGrid var5) {
                              return new byte[var1.getMaxHeight() / 16][];
                           }
                        }));
                     } else {
                        Bukkit.createWorld(new WorldCreator(var28));
                     }

                     var1.sendMessage(this.customization.prefix + "World has been imported! check your logs! took " + ChatColor.LIGHT_PURPLE + (System.currentTimeMillis() - var12) + "ms!");
                     return true;
                  }
               }

               var1.sendMessage(this.customization.prefix + "Couldn't find any world with that name!");
               return true;
            }

            if (!var6.equals("backup") && !var6.equals("restore")) {
               if (var6.equals("backupall")) {
                  if (!this.checkSender(var1, false, "skywars.worldmanager")) {
                     return true;
                  }

                  var24 = Bukkit.getWorlds();
                  var1.sendMessage(this.customization.prefix + ChatColor.GREEN + "Creating a backup of all loaded worlds " + ChatColor.GOLD + "(" + var24.size() + ")" + ChatColor.GREEN + "!");
                  var29 = System.currentTimeMillis();
                  (new BukkitRunnable() {
                     int i = 0;
                     boolean busy = false;

                     public void run() {
                        if (this.i >= var24.size()) {
                           var1.sendMessage(Skywars.this.customization.prefix + ChatColor.GREEN + "A backup for " + ChatColor.GOLD + "(" + this.i + ")" + ChatColor.GREEN + " worlds has been created! " + ChatColor.LIGHT_PURPLE + "Took: " + (System.currentTimeMillis() - var29) + "ms");
                           this.cancel();
                        } else {
                           if (!this.busy) {
                              World var1x = (World)var24.get(this.i);
                              this.busy = true;
                              var1.sendMessage(Skywars.this.customization.prefix + "Creating a backup for the world " + ChatColor.AQUA + var1x.getName());
                              var1x.save();
                              Skywars.this.filesManager.copyFile(var1x.getWorldFolder(), new File(Skywars.this.getDataFolder() + "/backup", var1x.getName()));
                              this.busy = false;
                              ++this.i;
                           }

                        }
                     }
                  }).runTaskTimerAsynchronously(this, 0L, 20L);
                  return true;
               }

               if (var6.equals("tp")) {
                  if (!this.checkSender(var1, true, "skywars.worldmanager")) {
                     return true;
                  }

                  if (var4.length == 2) {
                     this.sendCommandUsage(var1, "WorldManager Tp", "<Name>");
                     return true;
                  }

                  var23 = (Player)var1;
                  var28 = var4[2];
                  if (Bukkit.getWorld(var28) == null) {
                     var23.sendMessage(this.customization.prefix + "Couldn't find a world with that name!");
                     return true;
                  }

                  var23.teleport(Bukkit.getWorld(var28).getSpawnLocation());
                  var23.sendMessage(this.customization.prefix + "You have been teleported to " + ChatColor.GREEN + var28 + ChatColor.GRAY + "!");
                  return true;
               }

               if (var6.equals("setspawn")) {
                  if (!this.checkSender(var1, true, "skywars.worldmanager")) {
                     return true;
                  }

                  if (var4.length == 2) {
                     this.sendCommandUsage(var1, "WorldManager Setspawn", "<Name>");
                     return true;
                  }

                  var23 = (Player)var1;
                  var28 = var4[2];
                  World var30 = Bukkit.getWorld(var28);
                  if (var30 == null) {
                     var23.sendMessage(this.customization.prefix + "Couldn't find a world with that name!");
                     return true;
                  }

                  if (!var30.getName().equals(var23.getWorld().getName())) {
                     var23.sendMessage(this.customization.prefix + "You are not in that world to set its spawn!");
                     return true;
                  }

                  var30.setSpawnLocation(var23.getLocation().getBlockX(), var23.getLocation().getBlockY() + 1, var23.getLocation().getBlockZ());
                  var23.sendMessage(this.customization.prefix + "You have set the world spawn location!");
                  return true;
               }

               if (!var6.equals("list")) {
                  var1.sendMessage(this.customization.prefix + "Unknown command! use /sw WorldManager for a list of commands");
                  return true;
               }

               var22 = this.colors[this.r.nextInt(this.colors.length)];
               var1.sendMessage("" + var22 + ChatColor.STRIKETHROUGH + "------------" + ChatColor.YELLOW + " Skywars " + ChatColor.RED + "WorldManager " + var22 + ChatColor.STRIKETHROUGH + "------------");
               var1.sendMessage(this.customization.prefix + "Loaded worlds: " + ChatColor.LIGHT_PURPLE + Bukkit.getWorlds().size());
               var9 = Bukkit.getWorlds().iterator();

               while(var9.hasNext()) {
                  var27 = (World)var9.next();
                  var1.sendMessage(ChatColor.AQUA + "- " + ChatColor.LIGHT_PURPLE + var27.getName() + ChatColor.DARK_AQUA + " -> " + ChatColor.RED + "Environment: " + ChatColor.AQUA + var27.getEnvironment().name() + ", " + ChatColor.YELLOW + "Difficulty: " + ChatColor.AQUA + var27.getDifficulty().name() + ", " + ChatColor.GREEN + "PVP: " + ChatColor.AQUA + var27.getPVP() + ", " + ChatColor.DARK_AQUA + "Players: " + ChatColor.AQUA + var27.getPlayers().size());
               }

               var1.sendMessage("" + var22 + ChatColor.STRIKETHROUGH + "--------------------------------------------");
               return true;
            }

            if (var4.length == 2) {
               this.sendCommandUsage(var1, "WorldManager " + var6.substring(0, 1).toUpperCase() + var6.substring(1, var6.length()), "<Name>");
               return true;
            }

            final World var7 = Bukkit.getWorld(var4[2]);
            if (var7 == null) {
               var1.sendMessage(this.customization.prefix + "Couldn't find a loaded world with that name!");
               return true;
            }

            if (var6.equals("backup")) {
               (new BukkitRunnable() {
                  public void run() {
                     var1.sendMessage(Skywars.this.customization.prefix + "Creating a backup of the world " + ChatColor.GREEN + var7.getName() + ChatColor.GRAY + "!");
                     long var1x = System.currentTimeMillis();
                     var7.save();
                     Skywars.this.filesManager.copyFile(var7.getWorldFolder(), new File(Skywars.this.getDataFolder() + "/backup", var7.getName()));
                     var1.sendMessage(Skywars.this.customization.prefix + "Backup has been " + ChatColor.GREEN + "successfully" + ChatColor.GRAY + " created! took " + ChatColor.LIGHT_PURPLE + (System.currentTimeMillis() - var1x) + "ms!");
                  }
               }).runTaskAsynchronously(this);
               return true;
            }

            if (!var6.equals("restore")) {
               return true;
            }

            if (var7.getPlayers().isEmpty()) {
               var25 = new File(this.getDataFolder() + "/backup", var7.getName());
               if (!var25.exists()) {
                  var1.sendMessage(this.customization.prefix + "Couldn't find a backup for that world!");
                  return true;
               }

               (new BukkitRunnable() {
                  public void run() {
                     var1.sendMessage(Skywars.this.customization.prefix + "Restoring the world " + ChatColor.LIGHT_PURPLE + var7.getName() + ChatColor.GRAY + "!");
                     final long var1x = System.currentTimeMillis();
                     File var3 = var7.getWorldFolder();
                     final Environment var4 = var7.getEnvironment();
                     Bukkit.unloadWorld(var7, false);
                     Skywars.this.filesManager.deleteFile(var3);
                     Skywars.this.filesManager.copyFile(var25, var3);
                     (new BukkitRunnable() {
                        public void run() {
                           Bukkit.createWorld((new WorldCreator(var25.getName())).environment(var4));
                           var1.sendMessage(Skywars.this.customization.prefix + "World " + ChatColor.AQUA + var25.getName() + ChatColor.GRAY + " has been " + ChatColor.GREEN + "successfully" + ChatColor.GRAY + " restored! took " + ChatColor.LIGHT_PURPLE + (System.currentTimeMillis() - var1x) + "ms!");
                           var1.sendMessage(Skywars.this.customization.prefix + "If the world didn't restore correctly, try using the restore command again!");
                        }
                     }).runTask(Skywars.this.plugin);
                  }
               }).runTaskAsynchronously(this);
               return true;
            }

            var1.sendMessage(this.customization.prefix + "The world contains players. so the world can't be restored!");
            var9 = var7.getPlayers().iterator();

            while(var9.hasNext()) {
               var8 = (Player)var9.next();
               var1.sendMessage(this.customization.prefix + "- " + var8.getName());
            }

            return true;
         }

         var1.sendMessage((String)this.customization.messages.get("Unknown-Command"));
      }

      return false;
   }

   private boolean checkSender(CommandSender var1, boolean var2, String var3) {
      if (var2 && !(var1 instanceof Player)) {
         var1.sendMessage(this.customization.prefix + "You must be a player to use this command");
         return false;
      } else if (!var3.isEmpty() && !var1.hasPermission(var3)) {
         var1.sendMessage((String)this.customization.messages.get("No-Permission"));
         return false;
      } else {
         return true;
      }
   }

   private void sendCommandUsage(CommandSender var1, String var2, String var3, String... var4) {
      var1.sendMessage(this.customization.prefix + "Usage: /sw " + ChatColor.GREEN + var2 + ChatColor.GRAY + " " + var3);
      String[] var8 = var4;
      int var7 = var4.length;

      for(int var6 = 0; var6 < var7; ++var6) {
         String var5 = var8[var6];
         var1.sendMessage(this.customization.prefix + ChatColor.AQUA + "- " + ChatColor.GRAY + var5);
      }

   }

   protected List getPlayers(Collection var1) {
      ArrayList var2 = new ArrayList();
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         String var3 = (String)var4.next();
         if (Bukkit.getPlayer(var3) != null) {
            var2.add(Bukkit.getPlayer(var3));
         }
      }

      return var2;
   }

   protected String getStringFromLocation(Location var1, boolean var2) {
      return var1.getWorld().getName() + ", " + ((double)var1.getBlockX() + (var2 ? 0.5D : 0.0D)) + ", " + (var1.getBlockY() + (var2 ? 1 : 0)) + ", " + ((double)var1.getBlockZ() + (var2 ? 0.5D : 0.0D)) + ", " + var1.getYaw() + ", " + var1.getPitch();
   }

   protected Location getLocationFromString(String var1) {
      String[] var2 = var1.split(", ");
      World var3 = Bukkit.getWorld(var2[0]);
      double var4 = Double.valueOf(var2[1]);
      double var6 = Double.valueOf(var2[2]);
      double var8 = Double.valueOf(var2[3]);
      float var10 = Float.valueOf(var2[4]);
      float var11 = Float.valueOf(var2[5]);
      return new Location(var3, var4, var6, var8, var10, var11);
   }

   protected String getReadableLocationString(Location var1, boolean var2) {
      return "" + ChatColor.GREEN + ((double)var1.getBlockX() + (var2 ? 0.5D : 0.0D)) + ChatColor.GRAY + ", " + ChatColor.GREEN + (var1.getBlockY() + (var2 ? 1 : 0)) + ChatColor.GRAY + ", " + ChatColor.GREEN + ((double)var1.getBlockZ() + (var2 ? 0.5D : 0.0D));
   }

   protected boolean checkNumbers(String... var1) {
      try {
         String[] var5 = var1;
         int var4 = var1.length;

         for(int var3 = 0; var3 < var4; ++var3) {
            String var2 = var5[var3];
            Integer.parseInt(var2);
         }

         return true;
      } catch (NumberFormatException var6) {
         return false;
      }
   }

   protected void cageInventory(Inventory var1, boolean var2) {
      int var3;
      if (var2) {
         for(var3 = 0; var3 < var1.getSize(); ++var3) {
            var1.setItem(var3, this.pane_itemstack);
         }

      } else {
         for(var3 = 0; var3 < 9; ++var3) {
            var1.setItem(var3, this.pane_itemstack);
         }

         for(var3 = var1.getSize() - 9; var3 < var1.getSize(); ++var3) {
            var1.setItem(var3, this.pane_itemstack);
         }

         var3 = var1.getSize() / 9 - 2;
         if (var3 >= 1) {
            int var4;
            for(var4 = 9; var4 < 9 * var3 + 1; var4 += 9) {
               var1.setItem(var4, this.pane_itemstack);
            }

            for(var4 = 17; var4 < 9 * (var3 + 1); var4 += 9) {
               var1.setItem(var4, this.pane_itemstack);
            }

         }
      }
   }

   protected int getInventorySize(int var1) {
      return var1 < 10 ? 9 : (var1 < 19 ? 18 : (var1 < 28 ? 27 : (var1 < 37 ? 36 : (var1 < 46 ? 45 : 54))));
   }

   protected boolean compareItem(ItemStack var1, ItemStack var2) {
      return var1 != null && var2 != null && var1.getType().equals(var2.getType()) && var1.getItemMeta().equals(var2.getItemMeta());
   }

   protected void fireWorkEffect(Player var1, boolean var2) {
      if (this.config.fireworksEnabled) {
         final Firework var3 = (Firework)var1.getWorld().spawn(var1.getLocation().add(0.0D, 1.0D, 0.0D), Firework.class);
         FireworkMeta var4 = var3.getFireworkMeta();
         FireworkEffect var5 = FireworkEffect.builder().flicker(this.r.nextBoolean()).withColor(Color.fromBGR(this.r.nextInt(256), this.r.nextInt(256), this.r.nextInt(256))).withFade(Color.fromBGR(this.r.nextInt(256), this.r.nextInt(256), this.r.nextInt(256))).with(Type.values()[this.r.nextInt(Type.values().length)]).trail(this.r.nextBoolean()).build();
         var4.addEffect(var5);
         var3.setFireworkMeta(var4);
         if (var2) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
               public void run() {
                  var3.detonate();
               }
            }, 2L);
         }

      }
   }

   protected ItemStack getSkull(String var1, String var2) {
      ItemStack var3 = new ItemStack(Material.SKULL_ITEM, 1, (short)SkullType.PLAYER.ordinal());
      SkullMeta var4 = (SkullMeta)var3.getItemMeta();
      if (this.config.loadSkinsOnSkulls) {
         var4.setOwner(var1);
      }

      var4.setDisplayName(var2);
      var3.setItemMeta(var4);
      return var3;
   }

   protected String getPercentageString(int var1) {
      return "" + (var1 < 40 ? ChatColor.RED : (var1 < 80 ? ChatColor.YELLOW : ChatColor.GREEN)) + var1 + "%";
   }

   protected void join(final Player var1) {
      this.players.add(var1.getName());
      this.lobbyPlayers.add(var1.getName());
      this.protectedPlayers.add(var1.getName());
      PlayerData var2 = (PlayerData)this.playerData.get(var1.getName());
      var2.saveData(var1);
      var1.teleport(this.lobbyLocation);
      var2.clearPlayer(var1);
      this.updateJoinSigns();
      if (this.config.scoreboardTitleAnimationEnabled && this.scoreboardTitleAnimationTask == null) {
         this.scoreboardTitleAnimationTask = (new BukkitRunnable() {
            int index = 0;

            public void run() {
               String var1 = (String)Skywars.this.config.scoreboardTitleAnimationFrames.get(this.index);
               if (++this.index >= Skywars.this.plugin.config.scoreboardTitleAnimationFrames.size()) {
                  this.index = 0;
               }

               List var2 = (List)Skywars.this.lobbyPlayers.clone();
               Iterator var4 = var2.iterator();

               while(var4.hasNext()) {
                  String var3 = (String)var4.next();
                  PlayerData var5 = (PlayerData)Skywars.this.playerData.get(var3);
                  if (var5.lobbyScoreboard != null) {
                     var5.lobbyScoreboard.setName(var1);
                  }
               }

               var4 = Skywars.this.arenas.values().iterator();

               while(var4.hasNext()) {
                  Arena var6 = (Arena)var4.next();
                  var6.scoreboard.setName(var1);
               }

            }
         }).runTaskTimer(this, (long)this.config.scoreboardTitleAnimationInterval, (long)this.config.scoreboardTitleAnimationInterval);
      }

      if (this.isOneGamePerServer()) {
         (new BukkitRunnable() {
            public void run() {
               ((Arena)Skywars.this.arenas.values().toArray()[0]).join(var1);
            }
         }).runTaskLater(this.plugin, 10L);
      } else {
         var1.sendMessage((String)this.customization.messages.get("Player-Join"));
      }

      ItemStack var3 = this.getSkull(var1.getName(), ChatColor.AQUA + var1.getName());

      int var4;
      for(var4 = 0; var4 < this.playerInviter.getSize(); ++var4) {
         if (this.playerInviter.addItem(var4, var3)) {
            return;
         }
      }

      var4 = this.playerInviter.addInventory(ChatColor.RED + "List #" + (this.playerInviter.getSize() + 1));
      this.playerInviter.setItem(var4, 49, this.back_itemstack);
      this.playerInviter.addItem(var4, var3);
   }

   protected void leave(Player var1, boolean var2) {
      String var3 = var1.getName();
      PlayerData var4 = (PlayerData)this.playerData.get(var3);
      if (this.isOneGamePerServer()) {
         var2 = true;
      }

      if (var4.arena != null) {
         var4.arena.leave(var1);
         if (!var2) {
            return;
         }
      }

      this.players.remove(var3);
      this.lobbyPlayers.remove(var3);
      this.protectedPlayers.remove(var3);
      var4.restoreData(var1);
      if (var4.party != null) {
         var4.party.leave(var1);
      }

      var1.sendMessage((String)this.customization.messages.get("Player-Leave"));
      this.updateJoinSigns();
      if (this.config.bungee_mode_enabled) {
         ByteArrayDataOutput var5 = ByteStreams.newDataOutput();
         var5.writeUTF("Connect");
         var5.writeUTF(this.config.bungee_mode_hub);
         var1.sendPluginMessage(this, "BungeeCord", var5.toByteArray());
      }

      if (this.config.scoreboardTitleAnimationEnabled && this.scoreboardTitleAnimationTask != null && this.players.isEmpty()) {
         this.scoreboardTitleAnimationTask.cancel();
         this.scoreboardTitleAnimationTask = null;
      }

      for(int var11 = 0; var11 < this.playerInviter.getSize(); ++var11) {
         int[] var9;
         int var8 = (var9 = this.smartSlots).length;

         for(int var7 = 0; var7 < var8; ++var7) {
            int var6 = var9[var7];
            ItemStack var10 = this.playerInviter.getItem(var11, var6);
            if (var10 != null && ChatColor.stripColor(var10.getItemMeta().getDisplayName()).equals(var3)) {
               this.playerInviter.removeItem(var11, var6);
               return;
            }
         }
      }

   }

   protected void autojoin(Player var1, String var2) {
      PlayerData var3 = (PlayerData)this.plugin.playerData.get(var1.getName());
      int var4 = 1;
      if (var3.party != null) {
         var4 = var3.party.players.size();
         if (!var3.party.leaderName.equals(var1.getName())) {
            var1.sendMessage((String)this.customization.messages.get("Party-Must-Be-Leader"));
            return;
         }

         Iterator var6 = var3.party.players.iterator();

         while(var6.hasNext()) {
            String var5 = (String)var6.next();
            if (!this.plugin.lobbyPlayers.contains(var5)) {
               var1.sendMessage(((String)this.customization.messages.get("Party-Player-Not-Available")).replace("%member%", var5));
               return;
            }
         }
      }

      ArrayList var9 = new ArrayList();
      Iterator var7 = this.arenas.values().iterator();

      while(true) {
         Arena var10;
         do {
            do {
               do {
                  do {
                     do {
                        do {
                           if (!var7.hasNext()) {
                              if (var9.isEmpty()) {
                                 var1.sendMessage((String)this.customization.messages.get("No-Available-Arena"));
                                 return;
                              }

                              var10 = (Arena)var9.get(0);
                              Iterator var8 = var9.iterator();

                              while(var8.hasNext()) {
                                 Arena var11 = (Arena)var8.next();
                                 if (var11.players.size() > var10.players.size()) {
                                    var10 = var11;
                                 }
                              }

                              var10.join(var1);
                              return;
                           }

                           var10 = (Arena)var7.next();
                        } while(!var10.enabled);
                     } while(!var10.state.AVAILABLE());
                  } while(var10.teams.size() < var10.maxTeams);
               } while(var10.players.size() + var4 > var10.teamSize * var10.maxTeams);
            } while(var2.equals("SOLO") && var10.teamSize != 1);
         } while(var2.equals("TEAM") && var10.teamSize < 2);

         var9.add(var10);
      }
   }

   protected void updateJoinSigns() {
      Iterator var2 = this.joinSigns.keySet().iterator();

      while(true) {
         Location var1;
         do {
            if (!var2.hasNext()) {
               return;
            }

            var1 = (Location)var2.next();
         } while(!var1.getBlock().getType().equals(Material.WALL_SIGN) && !var1.getBlock().getType().equals(Material.SIGN_POST));

         Sign var3 = (Sign)var1.getBlock().getState();
         var3.setLine(3, String.valueOf(this.players.size()));
         var3.update(true);
      }
   }

   protected String randomize(HashMap var1) {
      int var2 = 0;

      int var3;
      for(Iterator var4 = var1.values().iterator(); var4.hasNext(); var2 += var3) {
         var3 = (Integer)var4.next();
      }

      var3 = this.r.nextInt(var2);
      int var7 = 0;
      Iterator var6 = var1.keySet().iterator();

      while(var6.hasNext()) {
         String var5 = (String)var6.next();
         var7 += (Integer)var1.get(var5);
         if (var3 >= var2 - var7) {
            return var5;
         }
      }

      return null;
   }

   protected void delayedSetup() {
      Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
         public void run() {
            Skywars.this.arenas = new HashMap();
            File var1 = new File(Skywars.this.getDataFolder(), "arenas");
            if (var1.isDirectory()) {
               File[] var5;
               int var4 = (var5 = var1.listFiles()).length;

               for(int var3 = 0; var3 < var4; ++var3) {
                  File var2 = var5[var3];
                  if (var2.isDirectory() && (new File(Skywars.this.getDataFolder() + "/arenas/" + var2.getName(), "settings.yml")).exists() && (new File(Skywars.this.getDataFolder() + "/arenas/" + var2.getName(), "locations.dat")).exists() && (new File(Skywars.this.getDataFolder() + "/arenas/" + var2.getName(), "blocks.dat")).exists()) {
                     new Arena(Skywars.this.plugin, var2.getName());
                  }
               }
            }

            Skywars.this.updateArenasInventory();
            Iterator var9 = Skywars.this.arenas.values().iterator();

            while(var9.hasNext()) {
               Arena var7 = (Arena)var9.next();
               if (!var7.state.equals(Enums.ArenaState.DISABLED)) {
                  Skywars.this.rollbackManager.add(var7);
               }
            }

            Skywars.this.joinSigns = new HashMap();
            FileConfiguration var8 = Skywars.this.filesManager.getConfig("signs.yml");
            Location var6;
            String var10;
            Iterator var11;
            int var13;
            if (var8.getConfigurationSection("Signs.Join") != null && !var8.getConfigurationSection("Signs.Join").getKeys(false).isEmpty()) {
               var11 = var8.getConfigurationSection("Signs.Join").getKeys(false).iterator();

               while(var11.hasNext()) {
                  var10 = (String)var11.next();
                  var13 = Integer.valueOf(var10);
                  var6 = Skywars.this.getLocationFromString(var8.getString("Signs.Join." + var10));
                  Skywars.this.joinSigns.put(var6, var13);
               }
            }

            Skywars.this.updateJoinSigns();
            Skywars.this.topSigns = new HashMap();
            if (var8.getConfigurationSection("Signs.Top") != null && !var8.getConfigurationSection("Signs.Top").getKeys(false).isEmpty()) {
               var11 = var8.getConfigurationSection("Signs.Top").getKeys(false).iterator();

               while(var11.hasNext()) {
                  var10 = (String)var11.next();
                  var13 = Integer.valueOf(var10);
                  var6 = Skywars.this.getLocationFromString(var8.getString("Signs.Top." + var10));
                  Skywars.this.topSigns.put(var6, var13);
               }
            }

            if (Skywars.this.hologramsManager != null && Skywars.this.hologramsManager.leaderboardHologram != null) {
               Skywars.this.hologramsManager.leaderboardHologram.delete();
            }

            Skywars.this.hologramsManager = null;
            if (Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
               Skywars.this.hologramsManager = new HologramsManager(Skywars.this.plugin);
               if (Skywars.this.getConfig().contains("Holographic-Stats")) {
                  Skywars.this.hologramsManager.setStats(Skywars.this.getLocationFromString(Skywars.this.getConfig().getString("Holographic-Stats")));
               }

               if (Skywars.this.getConfig().contains("Holographic-Leaderboard")) {
                  Skywars.this.hologramsManager.setLeaderboard(Skywars.this.getLocationFromString(Skywars.this.getConfig().getString("Holographic-Leaderboard")), false);
               }

               Bukkit.getConsoleSender().sendMessage(Skywars.this.customization.prefix + "HolographicDisplays has been detected! HolographicDisplays features are now accessible..");
            }

            Skywars.this.startLeaderboardUpdater();
            if (Skywars.this.getConfig().contains("Lobby")) {
               var10 = Skywars.this.getConfig().getString("Lobby").split(",")[0];
               if (Bukkit.getWorld(var10) == null) {
                  Bukkit.getConsoleSender().sendMessage("The lobby world seems to be unloaded! attempting to import it");
                  Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "sw worldmanager import " + var10);
               }

               Skywars.this.lobbyLocation = Skywars.this.getLocationFromString(Skywars.this.getConfig().getString("Lobby"));
               if (Skywars.this.config.bungee_mode_enabled) {
                  Iterator var14 = Skywars.this.getOnlinePlayers().iterator();

                  while(var14.hasNext()) {
                     Player var12 = (Player)var14.next();
                     Skywars.this.join(var12);
                  }
               }
            }

            Bukkit.getConsoleSender().sendMessage(Skywars.this.customization.prefix + "Loaded " + ChatColor.AQUA + Skywars.this.arenas.size() + ChatColor.GRAY + " arena(s)" + ChatColor.LIGHT_PURPLE + "  and " + ChatColor.AQUA + Skywars.this.joinSigns.size() + ChatColor.GRAY + " join sign(s)" + ChatColor.LIGHT_PURPLE + " and " + ChatColor.AQUA + Skywars.this.cages.size() + ChatColor.GRAY + " cage(s)" + ChatColor.LIGHT_PURPLE + " and " + ChatColor.AQUA + Skywars.this.chests.size() + ChatColor.GRAY + " chest categorie(s)" + ChatColor.LIGHT_PURPLE + " and " + ChatColor.AQUA + Skywars.this.kits.size() + ChatColor.GRAY + " kit(s)" + ChatColor.LIGHT_PURPLE + " and " + ChatColor.AQUA + Skywars.this.trails.size() + ChatColor.GRAY + " trail(s)" + ChatColor.LIGHT_PURPLE + " and " + ChatColor.AQUA + Skywars.this.shop.getSize() + ChatColor.GRAY + " shop(s)" + ChatColor.LIGHT_PURPLE + " and " + ChatColor.AQUA + Skywars.this.achievementsManager.getSize() + ChatColor.GRAY + " achievement(s)" + ChatColor.LIGHT_PURPLE + " and " + ChatColor.AQUA + Skywars.this.broadcaster.messages.size() + ChatColor.GRAY + " broadcaster message(s)");
         }
      }, (long)this.getConfig().getInt("Load-Delay-Ticks"));
   }

   protected void mainSetup() {
      this.filesManager = new FilesManager(this);
      FileConfiguration var1 = this.filesManager.getConfig("customization.yml");
      this.customization = new Customization(var1);
      this.config = new Config(this);
      this.ranksManager = new RanksManager(this);
      this.rollbackManager.reset();
      this.quit_itemstack = this.getItemStack(var1.getString("Items.Quit"), false, true);
      this.play_itemstack = this.getItemStack(var1.getString("Items.Play"), false, true);
      this.shop_itemstack = this.getItemStack(var1.getString("Items.Shop"), false, true);
      this.profile_itemstack = this.getItemStack(var1.getString("Items.Profile"), false, true);
      this.party_itemstack = this.getItemStack(var1.getString("Items.Party"), false, true);
      this.teleporter_itemstack = this.getItemStack(var1.getString("Items.Teleporter"), false, true);
      this.autojoin_itemstack = this.getItemStack(var1.getString("Items.Autojoin"), false, true);
      this.back_itemstack = this.getItemStack(var1.getString("Items.Back"), false, true);
      this.vote_itemstack = this.getItemStack(var1.getString("Items.Vote"), false, true);
      this.inventory_itemstack = this.getItemStack(var1.getString("Items.Inventory"), false, true);
      this.stats_itemstack = this.getItemStack(var1.getString("Items.Stats"), false, true);
      this.achievements_itemstack = this.getItemStack(var1.getString("Items.Achievements"), false, true);
      this.confirm_itemstack = this.getItemStack(var1.getString("Items.Confirm"), false, true);
      this.cancel_itemstack = this.getItemStack(var1.getString("Items.Cancel"), false, true);
      this.next_itemstack = this.getItemStack(var1.getString("Items.Next"), false, true);
      this.previous_itemstack = this.getItemStack(var1.getString("Items.Previous"), false, true);
      this.create_itemstack = this.getItemStack(var1.getString("Items.Create"), false, true);
      this.join_itemstack = this.getItemStack(var1.getString("Items.Join"), false, true);
      this.invite_itemstack = this.getItemStack(var1.getString("Items.Invite"), false, true);
      this.leave_itemstack = this.getItemStack(var1.getString("Items.Leave"), false, true);
      Enums.ArenaState[] var5;
      int var4 = (var5 = Enums.ArenaState.values()).length;

      for(int var3 = 0; var3 < var4; ++var3) {
         Enums.ArenaState var2 = var5[var3];
         var2.value = ChatColor.translateAlternateColorCodes('&', var1.getString("States." + var2.name()));
      }

      this.arenaSelector = new SmartInventory(this.plugin, (String)this.customization.inventories.get("Arenas-Selector"));
      this.arenaSelector.addInventory(ChatColor.RED + "List #1");
      this.arenaSelector.setItem(0, 49, this.autojoin_itemstack);
      this.delayedSetup();
      this.cages = new HashMap();
      FileConfiguration var19 = this.filesManager.getConfig("cages.yml");
      if (var19.getConfigurationSection("Cages") == null || var19.getConfigurationSection("Cages").getKeys(false).size() < 2) {
         this.createDefaultCages();
      }

      Iterator var22 = var19.getConfigurationSection("Cages").getKeys(false).iterator();

      String var7;
      int var9;
      int var12;
      while(var22.hasNext()) {
         String var20 = (String)var22.next();
         String var25 = "Cages." + var20 + ".";
         ItemStack var6 = this.getItemStack(var19.getString(var25 + "item"), false, false);
         var7 = var19.getString(var25 + "permission");
         Enums.Rarity var8 = Enums.Rarity.valueOf(var19.getString(var25 + "rarity").toUpperCase());
         var9 = var19.getInt(var25 + "value");
         String[] var10 = new String[]{"floor", "lower-middle", "middle", "higher-middle", "roof"};
         ItemStack[] var11 = new ItemStack[5];

         for(var12 = 0; var12 < var11.length; ++var12) {
            var11[var12] = this.getItemStack(var19.getString(var25 + var10[var12]), false, false);
         }

         this.cages.put(var20.toLowerCase(), new Cage(this, var20, var6, var7, var8, var9, var11));
      }

      this.chests = new HashMap();
      FileConfiguration var21 = this.filesManager.getConfig("chests.yml");
      Iterator var26 = var21.getConfigurationSection("Chests").getKeys(false).iterator();

      Iterator var32;
      String var39;
      int var41;
      while(var26.hasNext()) {
         String var23 = (String)var26.next();
         HashMap var28 = new HashMap();
         var32 = var21.getConfigurationSection("Chests." + var23).getKeys(false).iterator();

         while(var32.hasNext()) {
            var7 = (String)var32.next();
            ArrayList var35 = new ArrayList();
            Iterator var42 = var21.getStringList("Chests." + var23 + "." + var7 + ".items").iterator();

            while(var42.hasNext()) {
               var39 = (String)var42.next();
               var35.add(this.getItemStack(var39, true, true));
            }

            var41 = var21.getInt("Chests." + var23 + "." + var7 + ".min-items");
            int var44 = var21.getInt("Chests." + var23 + "." + var7 + ".max-items");
            var28.put(var7.toLowerCase(), new ChestType(this, var7, var35, var41, var44));
         }

         this.chests.put(var23, var28);
      }

      this.kits = new HashMap();
      FileConfiguration var24 = this.filesManager.getConfig("kits.yml");
      if (var24.getConfigurationSection("Kits") == null || var24.getConfigurationSection("Kits").getKeys(false).isEmpty()) {
         this.createDefaultKits();
      }

      String[] var27 = new String[]{"boots", "leggings", "chestplate", "helmet"};
      Iterator var31 = var24.getConfigurationSection("Kits").getKeys(false).iterator();

      String var34;
      while(var31.hasNext()) {
         String var29 = (String)var31.next();
         var34 = "Kits." + var29 + ".";
         ItemStack var36 = this.getItemStack(var24.getString(var34 + "item"), false, false);
         var39 = var24.getString(var34 + "permission");
         Enums.Rarity var45 = Enums.Rarity.valueOf(var24.getString(var34 + "rarity").toUpperCase());
         var12 = var24.getInt(var34 + "value");
         ItemStack[] var13 = new ItemStack[4];

         for(int var14 = 0; var14 < var13.length; ++var14) {
            var13[var14] = this.getItemStack(var24.getString(var34 + var27[var14]), false, true);
         }

         ArrayList var56 = new ArrayList();
         Iterator var16 = var24.getStringList(var34 + "items").iterator();

         while(var16.hasNext()) {
            String var15 = (String)var16.next();
            var56.add(this.getItemStack(var15, true, true));
         }

         ArrayList var59 = new ArrayList();
         Iterator var17 = var24.getStringList(var34 + "description").iterator();

         while(var17.hasNext()) {
            String var60 = (String)var17.next();
            var59.add(ChatColor.translateAlternateColorCodes('&', var60));
         }

         this.kits.put(var29.toLowerCase(), new Kit(this, var29, var36, var39, var56, var13, var59, var45, var12));
      }

      this.trails = new HashMap();
      FileConfiguration var30 = this.filesManager.getConfig("trails.yml");
      if (var30.getConfigurationSection("Trails") == null || var30.getConfigurationSection("Trails").getKeys(false).isEmpty()) {
         this.createDefaultTrails();
      }

      var32 = var30.getConfigurationSection("Trails").getKeys(false).iterator();

      String var47;
      while(var32.hasNext()) {
         var7 = (String)var32.next();
         String var38 = "Trails." + var7 + ".";
         ItemStack var43 = this.getItemStack(var30.getString(var38 + "item"), false, false);
         var47 = var30.getString(var38 + "permission");
         Enums.Rarity var48 = Enums.Rarity.valueOf(var30.getString(var38 + "rarity").toUpperCase());
         int var49 = var30.getInt(var38 + "value");
         this.trails.put(var7.toLowerCase(), new Trail(this, var7, var47, var49, var48, var43));
      }

      this.shop = new SmartInventory(this, (String)this.customization.inventories.get("Shop"));
      FileConfiguration var33 = this.filesManager.getConfig("shop.yml");
      if (var33.getConfigurationSection("Shops") == null || var33.getConfigurationSection("Shops").getKeys(false).isEmpty()) {
         this.createDefaultShop();
      }

      Iterator var40 = var33.getConfigurationSection("Shops").getKeys(false).iterator();

      Iterator var50;
      while(var40.hasNext()) {
         var34 = (String)var40.next();
         var41 = this.shop.addInventory(ChatColor.translateAlternateColorCodes('&', var34));
         var50 = var33.getConfigurationSection("Shops." + var34).getKeys(false).iterator();

         while(var50.hasNext()) {
            var47 = (String)var50.next();
            String var52 = var33.getString("Shops." + var34 + "." + var47).toLowerCase();
            ItemStack var58 = null;
            if (this.kits.containsKey(var52)) {
               var58 = ((Kit)this.kits.get(var52)).item;
            } else if (this.cages.containsKey(var52)) {
               var58 = ((Cage)this.cages.get(var52)).item;
            } else if (this.trails.containsKey(var52)) {
               var58 = ((Trail)this.trails.get(var52)).item;
            }

            if (var58 != null) {
               this.shop.setItem(var41, Integer.valueOf(var47), var58);
            }
         }
      }

      this.createDefaultAchievements();
      this.achievementsManager = new AchievementsManager(this);
      this.broadcaster.loadMessages(this.filesManager.getConfig("broadcaster.yml"));
      Material var37 = Material.valueOf(this.getConfig().getString("Mystery-Box.Block-Type").toUpperCase());
      var9 = this.getConfig().getInt("Mystery-Box.Cost");
      HashMap var46 = new HashMap();
      var50 = this.getConfig().getStringList("Mystery-Box.Contents").iterator();

      while(var50.hasNext()) {
         var47 = (String)var50.next();
         var46.put(var47.split(" : ")[0], Integer.valueOf(var47.split(" : ")[1].replace("%", "")));
      }

      HashMap var51 = new HashMap();
      Iterator var53 = this.getConfig().getStringList("Mystery-Box.Rarities").iterator();

      while(var53.hasNext()) {
         String var54 = (String)var53.next();
         var51.put(var54.split(" : ")[0], Integer.valueOf(var54.split(" : ")[1].replace("%", "")));
      }

      this.mysteryBox = new MysteryBox(this, var37, var9, var46, var51);
      Bukkit.getConsoleSender().sendMessage(this.customization.prefix + "Loaded the mystery box settings!");
      this.partyMenu = Bukkit.createInventory((InventoryHolder)null, 9, ChatColor.RED + "Select your option!");
      this.cageInventory(this.partyMenu, true);
      this.partyMenu.setItem(2, this.create_itemstack);
      this.partyMenu.setItem(6, this.join_itemstack);
      this.parties = new ArrayList();

      PlayerData var55;
      for(var53 = this.playerData.values().iterator(); var53.hasNext(); var55.party = null) {
         var55 = (PlayerData)var53.next();
      }

      this.partySelector = new SmartInventory(this, (String)this.customization.inventories.get("Parties"));
      this.partySelector.addInventory(ChatColor.RED + "List #1");
      this.partySelector.setItem(0, 49, this.back_itemstack);
      this.playerInviter = new SmartInventory(this, (String)this.customization.inventories.get("Player-Inviter"));
      this.profileInventory = Bukkit.createInventory((InventoryHolder)null, 27, (String)this.customization.inventories.get("Profile"));
      this.cageInventory(this.profileInventory, true);
      this.profileInventory.setItem(10, this.stats_itemstack);
      this.profileInventory.setItem(13, this.inventory_itemstack);
      this.profileInventory.setItem(16, this.achievements_itemstack);
      this.votingOptions = Bukkit.createInventory((InventoryHolder)null, 9, (String)this.customization.inventories.get("Arena-Settings"));
      this.cageInventory(this.votingOptions, true);
      this.votingOptions.setItem(2, (new ItemStackBuilder(Material.WATCH)).setName(ChatColor.AQUA + "Time").build());
      this.votingOptions.setItem(4, (new ItemStackBuilder(Material.CHEST)).setName(ChatColor.YELLOW + "Chests").build());
      this.votingOptions.setItem(6, (new ItemStackBuilder(Material.APPLE)).setName(ChatColor.RED + "Health").build());
      this.quitInventory = Bukkit.createInventory((InventoryHolder)null, 9, (String)this.customization.inventories.get("Quit-Inventory"));
      this.quitInventory.setItem(2, this.plugin.confirm_itemstack);
      this.quitInventory.setItem(6, this.plugin.cancel_itemstack);
      this.vault = null;
      if (this.getConfig().getBoolean("use-Vault") && Bukkit.getPluginManager().getPlugin("Vault") != null && Bukkit.getServicesManager().getRegistration(Economy.class) != null) {
         this.vault = (Economy)Bukkit.getServicesManager().getRegistration(Economy.class).getProvider();
         Bukkit.getConsoleSender().sendMessage(this.customization.prefix + "The plugin will be using vault economy instead of coins..");
      }

      if (this.config.bungee_mode_enabled && !Bukkit.getMessenger().isOutgoingChannelRegistered(this, "BungeeCord")) {
         Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
      }

      try {
         if (this.mysql != null) {
            this.mysql.close();
            this.mysql = null;
         }

         if (this.config.mysql_enabled) {
            Bukkit.getConsoleSender().sendMessage(this.customization.prefix + "Attempting to connect to the database and creating a table if it doesn't exist!");
            long var57 = System.currentTimeMillis();
            this.mysql = new MySQL(this, this.getConfig().getString("MySQL.table"), this.getConfig().getString("MySQL.host"), this.getConfig().getString("MySQL.port"), this.getConfig().getString("MySQL.database"), this.getConfig().getString("MySQL.username"), this.getConfig().getString("MySQL.password"));
            this.mysql.setupTable();
            Bukkit.getConsoleSender().sendMessage(this.customization.prefix + "Connection has been successfully established! it took " + (System.currentTimeMillis() - var57) + "ms to complete!");
         }
      } catch (SQLException var18) {
         var18.printStackTrace();
      }

      if (this.savingTask != null) {
         this.savingTask.cancel();
      }

      if (this.getConfig().getBoolean("Saving-Task.Enabled")) {
         this.savingTask = (new BukkitRunnable() {
            public void run() {
               final List var1 = Skywars.this.getOnlinePlayers();
               (new BukkitRunnable() {
                  int currentPlayer = 0;

                  public void run() {
                     if (this.currentPlayer >= var1.size()) {
                        this.cancel();
                        Bukkit.getConsoleSender().sendMessage(Skywars.this.customization.prefix + "Players stats have been saved!");
                     } else {
                        Player var1x = (Player)var1.get(this.currentPlayer);
                        if (var1x != null && var1x.isOnline()) {
                           PlayerData var2 = (PlayerData)Skywars.this.playerData.get(var1x.getName());
                           var2.save(var1x);
                        }

                        ++this.currentPlayer;
                     }
                  }
               }).runTaskTimerAsynchronously(Skywars.this.plugin, 0L, 1L);
            }
         }).runTaskTimer(this, (long)(this.getConfig().getInt("Saving-Task.Save-Every-Minutes") * 1200), (long)(this.getConfig().getInt("Saving-Task.Save-Every-Minutes") * 1200));
      } else {
         this.savingTask = null;
      }

      this.winnersMap = null;
      if (this.getConfig().getBoolean("Winners-Map.enabled")) {
         (new BukkitRunnable() {
            public void run() {
               long var1 = System.currentTimeMillis();
               MapView var3 = Bukkit.createMap((World)Bukkit.getWorlds().get(0));
               Iterator var5 = var3.getRenderers().iterator();

               while(var5.hasNext()) {
                  MapRenderer var4 = (MapRenderer)var5.next();
                  var3.removeRenderer(var4);
               }

               try {
                  final Image var7 = Skywars.this.getConfig().getBoolean("Winners-Map.display-image") ? ImageIO.read(new URL(Skywars.this.getConfig().getString("Winners-Map.image-url"))).getScaledInstance(64, 64, 2) : null;
                  var3.addRenderer(new MapRenderer() {
                     public void render(MapView var1, MapCanvas var2, Player var3) {
                        var2.drawText(25, 10, MinecraftFont.Font, "Congratulations");
                        var2.drawText(35, 20, MinecraftFont.Font, "For winning!");
                        if (var7 != null) {
                           var2.drawImage(35, 50, var7);
                        }

                     }
                  });
               } catch (IOException var6) {
                  var6.printStackTrace();
               }

               Skywars.this.winnersMap = new ItemStack(Material.MAP, 1, var3.getId());
               Bukkit.getConsoleSender().sendMessage(Skywars.this.customization.prefix + "Winners map has been loaded! took " + (System.currentTimeMillis() - var1) + "ms");
            }
         }).runTaskAsynchronously(this);
      }

      this.availableUpdate = false;
      this.checkUpdates((Player)null);
   }

   protected void checkUpdates(final Player var1) {
      if (this.getConfig().getBoolean("Check-For-Updates")) {
         (new BukkitRunnable() {
            public void run() {
               try {
                  HttpURLConnection var1x = (HttpURLConnection)(new URL("https://api.spigotmc.org/")).openConnection();
                  var1x.setRequestMethod("GET");
                  String var2 = (new BufferedReader(new InputStreamReader(var1x.getInputStream()))).readLine();
                  Skywars.this.availableUpdate = !var2.equals(Skywars.this.getDescription().getVersion());
                  String var3 = Skywars.this.customization.prefix + (Skywars.this.availableUpdate ? "Found a new available version! " + ChatColor.LIGHT_PURPLE + "download at http://goo.gl/GpTbwN" : "Looks like you have the latest version installed!");
                  if (var1 == null) {
                     Bukkit.getConsoleSender().sendMessage(var3);
                     Iterator var5 = Skywars.this.getOnlinePlayers().iterator();

                     while(var5.hasNext()) {
                        Player var4 = (Player)var5.next();
                        if (var4.hasPermission("skywars.admin")) {
                           var4.sendMessage(var3);
                        }
                     }
                  } else {
                     var1.sendMessage(var3);
                  }
               } catch (IOException var6) {
                  Bukkit.getConsoleSender().sendMessage(Skywars.this.customization.prefix + "Couldn't check for an available update");
                  var6.printStackTrace();
               }

            }
         }).runTaskAsynchronously(this);
      }

   }

   protected void updateArenasInventory() {
      for(int var1 = 0; var1 < this.arenaSelector.getSize(); ++var1) {
         this.arenaSelector.clear(var1);
      }

      HashMap var10 = new HashMap();
      Iterator var3 = this.arenas.values().iterator();

      while(var3.hasNext()) {
         Arena var2 = (Arena)var3.next();
         var10.put(var2, var2.teamSize);
      }

      LinkedList var11 = new LinkedList(var10.entrySet());
      Collections.sort(var11, new Comparator() {
         public int compare(Entry var1, Entry var2) {
            return (Integer)var1.getValue() - (Integer)var2.getValue();
         }
      });
      var3 = var11.iterator();

      for(int var4 = 0; (double)var4 < Math.ceil(Double.valueOf((double)this.arenas.size()) / (double)this.smartSlots.length); ++var4) {
         if (var4 >= this.arenaSelector.getSize()) {
            this.arenaSelector.addInventory(ChatColor.RED + "List #" + (var4 + 1));
            this.arenaSelector.setItem(var4, 49, this.autojoin_itemstack);
         }

         int[] var8;
         int var7 = (var8 = this.smartSlots).length;

         for(int var6 = 0; var6 < var7; ++var6) {
            int var5 = var8[var6];
            if (!var3.hasNext()) {
               break;
            }

            Arena var9 = (Arena)((Entry)var3.next()).getKey();
            var9.id = var4;
            var9.slot = var5;
            var9.updateItem(0);
         }
      }

   }

   protected void updatePartiesInventory() {
      int var1;
      for(var1 = 0; var1 < this.partySelector.getSize(); ++var1) {
         this.partySelector.clear(var1);
      }

      var1 = 0;

      for(int var2 = 0; (double)var2 < Math.ceil(Double.valueOf((double)this.parties.size()) / (double)this.smartSlots.length); ++var2) {
         if (var2 >= this.partySelector.getSize()) {
            this.partySelector.addInventory(ChatColor.RED + "List #" + (var2 + 1));
            this.partySelector.setItem(var2, 49, this.back_itemstack);
         }

         int[] var6;
         int var5 = (var6 = this.smartSlots).length;

         for(int var4 = 0; var4 < var5; ++var4) {
            int var3 = var6[var4];
            if (var1 >= this.parties.size()) {
               break;
            }

            Party var7 = (Party)this.parties.get(var1);
            var7.id = var2;
            var7.slot = var3;
            var7.updateItem();
            ++var1;
         }
      }

   }

   private void createDefaultCages() {
      FileConfiguration var1 = this.filesManager.getConfig("cages.yml");
      String[] var2 = new String[]{"floor", "lower-middle", "middle", "higher-middle", "roof"};
      HashMap var3 = new HashMap();
      Material[] var4 = new Material[]{Material.STONE, Material.GRASS, Material.DIRT, Material.COBBLESTONE, Material.WOOD, Material.LOG, Material.SNOW_BLOCK, Material.SANDSTONE, Material.SPONGE, Material.BRICK, Material.MELON_BLOCK};
      Material[] var8 = var4;
      int var7 = var4.length;

      for(int var6 = 0; var6 < var7; ++var6) {
         Material var5 = var8[var6];
         var3.put(var5.name().substring(0, 1) + var5.name().substring(1, var5.name().length()).toLowerCase().replace("_block", "") + " Cage", var5.name() + ", COMMON, 750, " + var5.name() + ", " + var5.name() + ", AIR, " + var5.name() + ", " + var5.name());
      }

      Material[] var13 = new Material[]{Material.BOOKSHELF, Material.OBSIDIAN, Material.ICE, Material.WORKBENCH, Material.FURNACE, Material.PUMPKIN};
      Material[] var9 = var13;
      int var17 = var13.length;

      for(var7 = 0; var7 < var17; ++var7) {
         Material var14 = var9[var7];
         var3.put(var14.name().substring(0, 1) + var14.name().substring(1, var14.name().length()).toLowerCase().replace("_block", "") + " Cage", var14.name() + ", RARE, 1500, " + var14.name() + ", " + var14.name() + ", AIR, " + var14.name() + ", " + var14.name());
      }

      Material[] var15 = new Material[]{Material.DIAMOND_BLOCK, Material.GLOWSTONE, Material.EMERALD_BLOCK, Material.TNT, Material.REDSTONE_BLOCK};
      Material[] var10 = var15;
      int var19 = var15.length;

      for(var17 = 0; var17 < var19; ++var17) {
         Material var16 = var10[var17];
         var3.put(var16.name().substring(0, 1) + var16.name().substring(1, var16.name().length()).toLowerCase().replace("_block", "") + " Cage", var16.name() + ", LEGENDARY, 3000, " + var16.name() + ", " + var16.name() + ", AIR, " + var16.name() + ", " + var16.name());
      }

      String[] var18 = new String[]{"White", "Orange", "Magenta", "Blue", "Yellow", "Lime", "Pink", "Gray", "", "Cyan", "Purple", "", "Brown", "Green", "Red", "Black"};

      for(var17 = 0; var17 < var18.length; ++var17) {
         String var20 = var18[var17];
         if (!var20.isEmpty()) {
            var3.put(var20 + " Cage", "WOOL:" + var17 + ", COMMON, 750, WOOL:" + var17 + ", WOOL:" + var17 + ", AIR, WOOL:" + var17 + ", WOOL:" + var17);
            var3.put(var20 + "+ Cage", "STAINED_GLASS:" + var17 + ", RARE, 1500, STAINED_GLASS:" + var17 + ", STAINED_GLASS:" + var17 + ", AIR, STAINED_GLASS:" + var17 + ", STAINED_GLASS:" + var17);
         }
      }

      Iterator var21 = var3.keySet().iterator();

      while(var21.hasNext()) {
         String var22 = (String)var21.next();
         String var23 = "Cages." + var22 + ".";
         String[] var11 = ((String)var3.get(var22)).split(", ");
         var1.set(var23 + "item", var11[0]);
         var1.set(var23 + "permission", "none");
         var1.set(var23 + "rarity", var11[1]);
         var1.set(var23 + "value", Integer.valueOf(var11[2]));

         for(int var12 = 0; var12 < var2.length; ++var12) {
            var1.set(var23 + var2[var12], var11[var12 + 3]);
         }
      }

      this.filesManager.saveConfig("cages.yml");
   }

   private void createDefaultKits() {
      FileConfiguration var1 = this.filesManager.getConfig("kits.yml");
      HashMap var2 = new HashMap();
      HashMap var3 = new HashMap();
      HashMap var4 = new HashMap();
      var2.put("Builder", "WOOL, COMMON, 1000, LEATHER_HELMET, AIR, AIR, AIR");
      var3.put("Builder", Arrays.asList("WOOD_SWORD : 1", "WOOL:14 : 64"));
      var4.put("Builder", Arrays.asList("&7Begin with simple building material", "&7to build towards the middle", "&7or build creative traps"));
      var2.put("Tactician", "WOOD_SWORD, COMMON, 1000, AIR, LEATHER_CHESTPLATE, AIR, AIR");
      var3.put("Tactician", Arrays.asList("WOOD_SWORD : 1", "SPONGE : 6", "POTION:8262 : 1"));
      var4.put("Tactician", Arrays.asList("&7Start with a set of items", "&7that will help you eliminate the others"));
      var2.put("Griefer", "MONSTER_EGG, COMMON, 1000, LEATHER_HELMET, LEATHER_CHESTPLATE, AIR, AIR");
      var3.put("Griefer", Arrays.asList("WOOD_SWORD : 1", "MONSTER_EGG:50 : 3 : name:&cCreeper"));
      var4.put("Griefer", Arrays.asList("&7Spawn creepers that will cause", "&7massive damage to the area"));
      var2.put("Miner", "STONE_PICKAXE, COMMON, 1000, IRON_HELMET : enchant:PROTECTION_EXPLOSIONS:2, AIR, AIR, AIR");
      var3.put("Miner", Arrays.asList("STONE_PICKAXE : 1 : enchant:DIG_SPEED:1 : enchant:DURABILITY:3", "COOKED_BEEF : 2", "TNT : 1", "REDSTONE_TORCH_ON : 1"));
      var4.put("Miner", Arrays.asList("&7Your enchanted pickaxe will help", "&7you mine faster than any one", "&7giving you the advantage to get", "&7decent gear and trap others"));
      var2.put("Hunter", "BOW, COMMON, 1000, LEATHER_HELMET, AIR, LEATHER_LEGGINGS, AIR");
      var3.put("Hunter", Arrays.asList("BOW : 1", "ARROW : 8", "COOKED_BEEF : 2"));
      var4.put("Hunter", Arrays.asList("&7Receive a normal bow and have the instant", "&7ability of knocking other players", "&7when the game starts"));
      var2.put("Armorer", "LEATHER_CHESTPLATE, COMMON, 1000, AIR, LEATHER_CHESTPLATE, LEATHER_LEGGINGS, AIR");
      var3.put("Armorer", Arrays.asList("BREAD : 2"));
      var4.put("Armorer", Arrays.asList("&7Receive a leather chest and leggings", "&7that will help you stay alive", "&7in the early stages"));
      var2.put("Trainer", "BONE, COMMON, 1000, AIR, LEATHER_CHESTPLATE, AIR, AIR");
      var3.put("Trainer", Arrays.asList("MONSTER_EGG:95 : 2 : name:&cWolf", "BREAD : 1"));
      var4.put("Trainer", Arrays.asList("&7Tame some wolfs that will protect you"));
      var2.put("Smith", "ANVIL, RARE, 2500, IRON_HELMET, AIR, AIR, AIR");
      var3.put("Smith", Arrays.asList("ANVIL : 1", "EXP_BOTTLE : 6", "ENCHANTED_BOOK : 1 : enchant:PROTECTION_ENVIRONMENTAL:1", "APPLE : 2"));
      var4.put("Smith", Arrays.asList("&7Start with an anvil and some exp bottles", "&7and an enchanted protection book", "&7which will allow you to enchant right", "&7from the begining of the game!"));
      var2.put("Enchanter", "ENCHANTMENT_TABLE, RARE, 2500, IRON_HELMET, AIR, AIR, AIR");
      var3.put("Enchanter", Arrays.asList("ENCHANTMENT_TABLE : 1", "EXP_BOTTLE : 10", "GRILLED_PORK : 1"));
      var4.put("Enchanter", Arrays.asList("&7Receive an enchanting table and", "&7start enchanting to protect your self"));
      var2.put("Cannoneer", "TNT, RARE, 2500, AIR, CHAINMAIL_CHESTPLATE : enchant:PROTECTION_EXPLOSIONS:3, AIR, AIR");
      var3.put("Cannoneer", Arrays.asList("TNT : 16", "REDSTONE : 32", "WATER_BUCKET : 1", "WOOD_PLATE : 8"));
      var4.put("Cannoneer", Arrays.asList("&7Create tnt cannons on the fly", "&7with your starter resources"));
      var2.put("Miner+", "IRON_PICKAXE, RARE, 2500, IRON_HELMET : enchant:PROTECTION_EXPLOSIONS:3, AIR, AIR, AIR");
      var3.put("Miner+", Arrays.asList("IRON_PICKAXE : 1 : enchant:DIG_SPEED:3 : enchant:DURABILITY:3", "TNT : 4", "REDSTONE_TORCH_ON : 4", "COOKED_CHICKEN : 3"));
      var4.put("Miner+", Arrays.asList("&7Your enchanted pickaxe will help", "&7you mine faster than any one", "&7giving you the advantage to get", "&7decent gear and trap others"));
      var2.put("Hunter+", "BOW, RARE, 2500, LEATHER_HELMET, AIR, LEATHER_LEGGINGS, AIR");
      var3.put("Hunter+", Arrays.asList("BOW : 1 : enchant:ARROW_DAMAGE:1", "ARROW : 16", "BREAD : 1"));
      var4.put("Hunter+", Arrays.asList("&7Receive a strong bow and have the instant", "&7ability of knocking other players", "&7when the game starts"));
      var2.put("Armorer+", "LEATHER_CHESTPLATE, RARE, 2500, LEATHER_HELMET, LEATHER_CHESTPLATE, LEATHER_LEGGINGS, LEATHER_BOOTS");
      var3.put("Armorer+", Arrays.asList("WOOD_AXE : 1", "BREAD : 2"));
      var4.put("Armorer+", Arrays.asList("&7Receive a full leather set", "&7that will help you stay alive", "&7in the early stages"));
      var2.put("Lumberjack", "IRON_AXE, RARE, 2500, IRON_HELMET, AIR, AIR, AIR");
      var3.put("Lumberjack", Arrays.asList("IRON_AXE : 1 : enchant:DIG_SPEED:3", "LOG : 16", "BREAD : 1"));
      var4.put("Lumberjack", Arrays.asList("&7Start with an enchanted axe and", "&7lots of wood that will help you", "&7reach the middle"));
      var2.put("Fisherman", "FISHING_ROD, RARE, 2500, LEATHER_HELMET, LEATHER_CHESTPLATE, AIR, AIR");
      var3.put("Fisherman", Arrays.asList("FISHING_ROD : 1 : enchant:KNOCKBACK:1 : enchant:DURABILITY:3", "COOKED_FISH : 10"));
      var4.put("Fisherman", Arrays.asList("&7Start with a knockback fishing rod", "&7and some of your cooked fishes"));
      var2.put("Snowman", "SNOW_BALL, RARE, 2500, AIR, CHAINMAIL_CHESTPLATE, AIR, AIR");
      var3.put("Snowman", Arrays.asList("WOOD_SPADE : 1 : enchant:DIG_SPEED:2", "PUMPKIN : 2", "SNOW_BLOCK : 4"));
      var4.put("Snowman", Arrays.asList("&7Build snowmen that will produce", "&7snow balls for you to farm"));
      var2.put("Necromancer", "MONSTER_EGG:51, RARE, 2500, LEATHER_HELMET, LEATHER_CHESTPLATE, AIR, AIR");
      var3.put("Necromancer", Arrays.asList("MONSTER_EGG:51 : 2 : name:&cSkeleton", "COOKED_BEEF : 3"));
      var4.put("Necromancer", Arrays.asList("&7Spawn skeletons that will", "&7protect you from others"));
      var2.put("Farmer", "DIAMOND_HOE, LEGENDARY, 5000, AIR, CHAINMAIL_CHESTPLATE : enchant:PROTECTION_PROJECTILE:2, AIR, AIR");
      var3.put("Farmer", Arrays.asList("DIAMOND_HOE : 1", "WHEAT : 12", "INK_SACK:15 : 32", "EGG : 16", "MONSTER_EGG:93 : 10 : name:&cChicken", "WATER_BUCKET : 1"));
      var4.put("Farmer", Arrays.asList("&7Start with many items that will", "&7help you produce food to survive and", "&7eggs to protect your self"));
      var2.put("Pyro", "FLINT_AND_STEEL, LEGENDARY, 5000, AIR, LEATHER_CHESTPLATE : enchant:PROTECTION_FIRE:5, AIR, AIR");
      var3.put("Pyro", Arrays.asList("FLINT_AND_STEEL : 1", "LAVA_BUCKET : 2"));
      var4.put("Pyro", Arrays.asList("&7Set everyone on fire and", "&7watch them burn while", "&7you laugh and swim in lava"));
      var2.put("Knight", "IRON_SWORD, LEGENDARY, 5000, AIR, AIR, AIR, AIR");
      var3.put("Knight", Arrays.asList("IRON_SWORD : 1 : enchant:DAMAGE_ALL:1", "BREAD : 1"));
      var4.put("Knight", Arrays.asList("&7Start with a very strong", "&7enchanted iron sword, but unfortunately", "&7you will have no protection"));
      var2.put("Troll", "WEB, LEGENDARY, 5000, AIR, IRON_CHESTPLATE, AIR, AIR");
      var3.put("Troll", Arrays.asList("WEB : 15", "LAVA_BUCKET : 5"));
      var4.put("Troll", Arrays.asList("&7Trap players inside your", "&7webs and drown them in lava"));
      var2.put("Blaze", "MONSTER_EGG:61, LEGENDARY, 5000, AIR, CHAINMAIL_CHESTPLATE, AIR, AIR");
      var3.put("Blaze", Arrays.asList("WOOD_SWORD : 1", "MONSTER_EGG:61 : 2 : name:&cBlaze"));
      var4.put("Blaze", Arrays.asList("&7Spawn blazes that will protect", "&7you and set everyone on fire!"));
      Iterator var6 = var2.keySet().iterator();

      while(var6.hasNext()) {
         String var5 = (String)var6.next();
         String var7 = (String)var2.get(var5);
         List var8 = (List)var3.get(var5);
         List var9 = (List)var4.get(var5);
         String var10 = "Kits." + var5 + ".";
         var1.set(var10 + "item", var7.split(", ")[0]);
         var1.set(var10 + "permission", "none");
         var1.set(var10 + "rarity", var7.split(", ")[1]);
         var1.set(var10 + "value", Integer.valueOf(var7.split(", ")[2]));
         var1.set(var10 + "helmet", var7.split(", ")[3]);
         var1.set(var10 + "chestplate", var7.split(", ")[4]);
         var1.set(var10 + "leggings", var7.split(", ")[5]);
         var1.set(var10 + "boots", var7.split(", ")[6]);
         var1.set(var10 + "items", var8);
         var1.set(var10 + "description", var9);
      }

      this.filesManager.saveConfig("kits.yml");
   }

   protected void createDefaultTrails() {
      FileConfiguration var1 = this.filesManager.getConfig("trails.yml");
      HashMap var2 = new HashMap();
      Material[] var3 = new Material[]{Material.STONE, Material.GRASS, Material.DIRT, Material.COBBLESTONE, Material.WOOD, Material.LOG, Material.SNOW_BLOCK, Material.SANDSTONE, Material.SPONGE, Material.BRICK, Material.MELON_BLOCK};
      Material[] var7 = var3;
      int var6 = var3.length;

      for(int var5 = 0; var5 < var6; ++var5) {
         Material var4 = var7[var5];
         var2.put(var4.name().substring(0, 1) + var4.name().substring(1, var4.name().length()).toLowerCase().replace("_block", "") + " Trail", var4.name() + ", COMMON, 1250");
      }

      Material[] var10 = new Material[]{Material.BOOKSHELF, Material.OBSIDIAN, Material.ICE, Material.WORKBENCH, Material.FURNACE, Material.PUMPKIN};
      Material[] var8 = var10;
      int var14 = var10.length;

      for(var6 = 0; var6 < var14; ++var6) {
         Material var11 = var8[var6];
         var2.put(var11.name().substring(0, 1) + var11.name().substring(1, var11.name().length()).toLowerCase().replace("_block", "") + " Trail", var11.name() + ", RARE, 2000");
      }

      Material[] var12 = new Material[]{Material.DIAMOND_BLOCK, Material.GLOWSTONE, Material.EMERALD_BLOCK, Material.TNT, Material.REDSTONE_BLOCK};
      Material[] var9 = var12;
      int var16 = var12.length;

      for(var14 = 0; var14 < var16; ++var14) {
         Material var13 = var9[var14];
         var2.put(var13.name().substring(0, 1) + var13.name().substring(1, var13.name().length()).toLowerCase().replace("_block", "") + " Trail", var13.name() + ", LEGENDARY, 3000");
      }

      Iterator var17 = var2.keySet().iterator();

      while(var17.hasNext()) {
         String var15 = (String)var17.next();
         String var18 = "Trails." + var15 + ".";
         String[] var19 = ((String)var2.get(var15)).split(", ");
         var1.set(var18 + "item", var19[0]);
         var1.set(var18 + "permission", "none");
         var1.set(var18 + "rarity", var19[1]);
         var1.set(var18 + "value", Integer.valueOf(var19[2]));
      }

      this.filesManager.saveConfig("trails.yml");
   }

   protected void createDefaultShop() {
      FileConfiguration var1 = this.filesManager.getConfig("shop.yml");
      ArrayList var2 = new ArrayList();
      Iterator var4 = this.kits.values().iterator();

      Kit var3;
      while(var4.hasNext()) {
         var3 = (Kit)var4.next();
         if (var3.rarity == Enums.Rarity.COMMON) {
            var2.add(var3);
         }
      }

      var4 = this.kits.values().iterator();

      while(var4.hasNext()) {
         var3 = (Kit)var4.next();
         if (var3.rarity == Enums.Rarity.RARE) {
            var2.add(var3);
         }
      }

      var4 = this.kits.values().iterator();

      while(var4.hasNext()) {
         var3 = (Kit)var4.next();
         if (var3.rarity == Enums.Rarity.LEGENDARY) {
            var2.add(var3);
         }
      }

      int var14 = 0;

      int var6;
      int var7;
      int var8;
      for(int var15 = 1; (double)var15 < Math.ceil(Double.valueOf((double)var2.size()) / (double)this.smartSlots.length) + 1.0D; ++var15) {
         String var5 = "Shops.&cKits #" + var15 + ".";
         int[] var9;
         var8 = (var9 = this.smartSlots).length;

         for(var7 = 0; var7 < var8; ++var7) {
            var6 = var9[var7];
            if (var14 >= var2.size()) {
               break;
            }

            var1.set(var5 + var6, ((Kit)var2.get(var14)).name);
            ++var14;
         }
      }

      ArrayList var17 = new ArrayList();
      Iterator var18 = this.cages.values().iterator();

      Cage var16;
      while(var18.hasNext()) {
         var16 = (Cage)var18.next();
         if (var16.rarity == Enums.Rarity.COMMON) {
            var17.add(var16);
         }
      }

      var18 = this.cages.values().iterator();

      while(var18.hasNext()) {
         var16 = (Cage)var18.next();
         if (var16.rarity == Enums.Rarity.RARE) {
            var17.add(var16);
         }
      }

      var18 = this.cages.values().iterator();

      while(var18.hasNext()) {
         var16 = (Cage)var18.next();
         if (var16.rarity == Enums.Rarity.LEGENDARY) {
            var17.add(var16);
         }
      }

      var17.remove(this.cages.get("default"));
      int var19 = 0;

      int var10;
      for(var6 = 1; (double)var6 < Math.ceil(Double.valueOf((double)var17.size()) / (double)this.smartSlots.length) + 1.0D; ++var6) {
         String var20 = "Shops.&9Cages #" + var6 + ".";
         int[] var11;
         var10 = (var11 = this.smartSlots).length;

         for(int var24 = 0; var24 < var10; ++var24) {
            var8 = var11[var24];
            if (var19 >= var17.size()) {
               break;
            }

            var1.set(var20 + var8, ((Cage)var17.get(var19)).name);
            ++var19;
         }
      }

      ArrayList var21 = new ArrayList();
      Iterator var23 = this.trails.values().iterator();

      Trail var22;
      while(var23.hasNext()) {
         var22 = (Trail)var23.next();
         if (var22.rarity == Enums.Rarity.COMMON) {
            var21.add(var22);
         }
      }

      var23 = this.trails.values().iterator();

      while(var23.hasNext()) {
         var22 = (Trail)var23.next();
         if (var22.rarity == Enums.Rarity.RARE) {
            var21.add(var22);
         }
      }

      var23 = this.trails.values().iterator();

      while(var23.hasNext()) {
         var22 = (Trail)var23.next();
         if (var22.rarity == Enums.Rarity.LEGENDARY) {
            var21.add(var22);
         }
      }

      var7 = 0;

      for(var8 = 1; (double)var8 < Math.ceil(Double.valueOf((double)var21.size()) / (double)this.smartSlots.length) + 1.0D; ++var8) {
         String var25 = "Shops.&eTrails #" + var8 + ".";
         int[] var13;
         int var12 = (var13 = this.smartSlots).length;

         for(int var26 = 0; var26 < var12; ++var26) {
            var10 = var13[var26];
            if (var7 >= var21.size()) {
               break;
            }

            var1.set(var25 + var10, ((Trail)var21.get(var7)).name);
            ++var7;
         }
      }

      this.filesManager.saveConfig("shop.yml");
   }

   protected void createDefaultAchievements() {
      FileConfiguration var1 = this.filesManager.getConfig("achievements.yml");
      Enums.AchievementType[] var5;
      int var4 = (var5 = Enums.AchievementType.values()).length;

      for(int var3 = 0; var3 < var4; ++var3) {
         Enums.AchievementType var2 = var5[var3];
         if (!var1.contains("Achievements." + var2.name().toLowerCase())) {
            int[] var9;
            int var8 = (var9 = var2.levels).length;

            for(int var7 = 0; var7 < var8; ++var7) {
               int var6 = var9[var7];
               String var10 = "Achievements." + var2.name().toLowerCase() + "." + var6 + ".";
               var1.set(var10 + "description", var2.defaultDescription.replace("%x%", String.valueOf(var6)));
               var1.set(var10 + "prize-description", "Earn " + var2.prizeMultiplier * var6 + " coins!");
               var1.set(var10 + "executed-command", "sw coins add %player% " + var2.prizeMultiplier * var6);
            }
         }
      }

      this.filesManager.saveConfig("achievements.yml");
   }

   protected void startLeaderboardUpdater() {
      if (this.leaderboard_updater != null) {
         this.plugin.leaderboard_updater.cancel();
      }

      this.leaderboard_updater = (new BukkitRunnable() {
         public void run() {
            Bukkit.getConsoleSender().sendMessage(Skywars.this.customization.prefix + "Updating leaderboards...");
            HashMap var1 = new HashMap();
            HashMap var2 = new HashMap();
            HashMap var3 = new HashMap();
            Iterator var5 = Skywars.this.topSigns.keySet().iterator();

            while(true) {
               int var8;
               while(var5.hasNext()) {
                  Location var4 = (Location)var5.next();
                  Block var6 = var4.getBlock();
                  if (!var6.getType().equals(Material.WALL_SIGN) && !var6.getType().equals(Material.SIGN_POST)) {
                     Bukkit.getConsoleSender().sendMessage(Skywars.this.customization.prefix + "Your top sign with the id of " + ChatColor.AQUA + "#" + (Integer)Skywars.this.topSigns.get(var4) + ChatColor.GRAY + " doesn't exist at the coordinates " + ChatColor.GREEN + Skywars.this.getReadableLocationString(var4, false));
                  } else {
                     Sign var7 = (Sign)var6.getState();
                     if (var7.getLine(0).startsWith(ChatColor.AQUA + "Top #" + ChatColor.RED) && Enums.Stat.getByName(var7.getLine(1).toUpperCase()) != null && Skywars.this.checkNumbers(ChatColor.stripColor(var7.getLine(0).split("#")[1]))) {
                        var8 = Integer.valueOf(ChatColor.stripColor(var7.getLine(0).split("#")[1]));
                        Enums.Stat var9 = Enums.Stat.valueOf(var7.getLine(1).toUpperCase());
                        var2.put(var4, var8);
                        var3.put(var4, var9);
                        if (!var1.containsKey(var9) || (Integer)var1.get(var9) < var8) {
                           var1.put(var9, var8);
                        }
                     } else {
                        Bukkit.getConsoleSender().sendMessage(Skywars.this.customization.prefix + "Your top sign with the id of " + ChatColor.AQUA + "#" + (Integer)Skywars.this.topSigns.get(var4) + ChatColor.GRAY + " isn't really a top sign");
                     }
                  }
               }

               if (var2.isEmpty()) {
                  Bukkit.getConsoleSender().sendMessage(Skywars.this.customization.prefix + "Couldn't find any valid top sign to update!");
                  if (Skywars.this.hologramsManager == null || Skywars.this.hologramsManager.leaderboard == null) {
                     Bukkit.getConsoleSender().sendMessage(Skywars.this.customization.prefix + "No Holographic leaderboard was set either, cancelling the task!");
                     this.cancel();
                     Skywars.this.leaderboard_updater = null;
                     return;
                  }
               } else {
                  Bukkit.getConsoleSender().sendMessage(Skywars.this.customization.prefix + "Finished loading all the top signs and their requirements! (" + var2.size() + ") sign(s)! loading players stats...");
               }

               try {
                  HashMap var14 = Skywars.api.getAllPlayersData();
                  Iterator var17 = var1.keySet().iterator();

                  int var19;
                  while(var17.hasNext()) {
                     Enums.Stat var15 = (Enums.Stat)var17.next();
                     var19 = (Integer)var1.get(var15);
                     List var20 = Skywars.api.getTopPlayers(var14, var15, var19);
                     Iterator var10 = var3.keySet().iterator();

                     while(var10.hasNext()) {
                        Location var21 = (Location)var10.next();
                        if (((Enums.Stat)var3.get(var21)).equals(var15)) {
                           Sign var11 = (Sign)var21.getBlock().getState();
                           Entry var12 = (Entry)var20.get((Integer)var2.get(var21) - 1);
                           var11.setLine(2, (String)var12.getKey());
                           var11.setLine(3, "(" + var12.getValue() + ")");
                           var11.update();
                           Skywars.this.updateSkull(var11, (String)var12.getKey());
                        }
                     }
                  }

                  if (Skywars.this.hologramsManager != null && Skywars.this.hologramsManager.leaderboard != null) {
                     Bukkit.getConsoleSender().sendMessage(Skywars.this.customization.prefix + "Updating the Holographic leaderboard...");
                     final HashMap var16 = new HashMap();
                     Enums.Stat[] var22;
                     var8 = (var22 = Enums.Stat.values()).length;

                     for(var19 = 0; var19 < var8; ++var19) {
                        Enums.Stat var18 = var22[var19];
                        var16.put(var18, (Entry)Skywars.api.getTopPlayers(var14, var18, 1).get(0));
                     }

                     (new BukkitRunnable() {
                        public void run() {
                           Skywars.this.hologramsManager.updateLeaderboard(var16);
                        }
                     }).runTask(Skywars.this.plugin);
                  }

                  Skywars.this.leaderboard_updater_time = System.currentTimeMillis() + (long)(Skywars.this.getConfig().getInt("Update-Leaderboard-Every-Minutes") * '\uea60');
                  Bukkit.getConsoleSender().sendMessage(Skywars.this.customization.prefix + "Leaderboards were updated!");
               } catch (SQLException var13) {
                  var13.printStackTrace();
               }

               return;
            }
         }
      }).runTaskTimer(this, 0L, (long)(this.getConfig().getInt("Update-Leaderboard-Every-Minutes") * 1200));
   }

   private void updateSkull(Sign var1, String var2) {
      if (var2.contains("NO_PLAYER")) {
         var2 = "MHF_Question";
      }

      Block var3 = null;
      Location[] var4 = new Location[]{var1.getLocation().add(0.0D, 1.0D, 0.0D), var1.getBlock().getRelative(((org.bukkit.material.Sign)var1.getData()).getAttachedFace()).getLocation().add(0.0D, 1.0D, 0.0D)};
      Location[] var8 = var4;
      int var7 = var4.length;

      for(int var6 = 0; var6 < var7; ++var6) {
         Location var5 = var8[var6];
         if (var5.getBlock().getType().equals(Material.SKULL)) {
            var3 = var5.getBlock();
         }
      }

      if (var3 != null) {
         Skull var9 = (Skull)var3.getState();
         var9.setSkullType(SkullType.PLAYER);
         var9.setOwner(var2);
         var9.update();
      }

   }

   protected ItemStack getItemStack(String var1, boolean var2, boolean var3) {
      String[] var4 = var1.split(" : ");
      ItemStackBuilder var5 = new ItemStackBuilder(var4[0].contains(":") ? Material.getMaterial(var4[0].split(":")[0].toUpperCase()) : Material.getMaterial(var4[0].toUpperCase()));
      if (var2) {
         var5.setAmount(Integer.valueOf(var4[1]));
      }

      if (var4[0].contains(":")) {
         var5.setDurability(Integer.valueOf(var4[0].split(":")[1]));
      }

      if (var3) {
         for(int var6 = var2 ? 2 : 1; var6 < var4.length; ++var6) {
            String var7 = var4[var6].split(":")[0].toLowerCase();
            if (var7.equals("name")) {
               var5.setName(ChatColor.translateAlternateColorCodes('&', var4[var6].split(":")[1]));
            } else if (var7.equals("lore")) {
               var5.addLore(ChatColor.translateAlternateColorCodes('&', var4[var6].split(":")[1]));
            } else if (var7.equals("enchant")) {
               var5.addEnchantment(Enchantment.getByName(var4[var6].split(":")[1].toUpperCase()), Integer.valueOf(var4[var6].split(":")[2]));
            }
         }
      }

      return var5.build();
   }

   protected String getItemStackString(ItemStack var1) {
      String var2 = var1.getType().name() + (var1.getDurability() != 0 ? ":" + var1.getDurability() : "") + " : " + var1.getAmount();
      ItemMeta var3 = var1.getItemMeta();
      if (var3.hasDisplayName()) {
         var2 = var2 + " : name:" + var3.getDisplayName();
      }

      String var4;
      Iterator var5;
      if (var3.hasLore()) {
         for(var5 = var1.getItemMeta().getLore().iterator(); var5.hasNext(); var2 = var2 + " : lore:" + var4) {
            var4 = (String)var5.next();
         }
      }

      Enchantment var6;
      if (var3.hasEnchants()) {
         for(var5 = var3.getEnchants().keySet().iterator(); var5.hasNext(); var2 = var2 + " : enchant:" + var6.getName() + ":" + var3.getEnchants().get(var6)) {
            var6 = (Enchantment)var5.next();
         }
      }

      return var2;
   }

   public boolean isOneGamePerServer() {
      return this.config.bungee_mode_enabled && this.arenas.size() == 1;
   }

   protected List getOnlinePlayers() {
      ArrayList var1 = Lists.newArrayList();
      Iterator var3 = Bukkit.getWorlds().iterator();

      while(var3.hasNext()) {
         World var2 = (World)var3.next();
         var1.addAll(var2.getPlayers());
      }

      return Collections.unmodifiableList(var1);
   }

   private void loadSounds() {
      try {
         this.CLICK = Sound.valueOf("CLICK");
         this.NOTE_PLING = Sound.valueOf("NOTE_PLING");
      } catch (IllegalArgumentException var2) {
         this.CLICK = Sound.valueOf("UI_BUTTON_CLICK");
         this.NOTE_PLING = Sound.valueOf("BLOCK_NOTE_PLING");
      }

   }
}
