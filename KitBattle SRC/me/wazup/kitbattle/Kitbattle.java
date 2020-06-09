package me.wazup.kitbattle;

import com.google.common.collect.Lists;
import io.puharesource.mc.titlemanager.api.TitleObject;
import io.puharesource.mc.titlemanager.api.TitleObject.TitleType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;
import java.util.logging.Logger;
import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Kitbattle extends JavaPlugin {
   public Kitbattle plugin;
   public static KitbattleAPI kitbattleApi;
   Logger logger = Logger.getLogger("Minecraft");
   List commands = Arrays.asList("admin", "list", "wand", "create", "delete", "enable", "disable", "addspawn", "removespawn", "addspawncuboid", "removespawncuboid", "join", "leave", "selectkit", "gui", "reset", "coins", "exp", "kit", "kitunlocker", "spectate", "spawn", "reload", "info", "editmode", "vote", "stats");
   String kb;
   Random random;
   ChatColor[] colors;
   HashMap playingMaps;
   HashMap tournamentMaps;
   HashMap challengeMaps;
   HashMap playerData;
   HashMap selectionMode;
   HashMap Kits;
   HashMap Ranks;
   HashMap topSigns;
   ArrayList players;
   ArrayList editmode;
   ArrayList spectating;
   ItemStack KitSelector;
   ItemStack ShopOpener;
   ItemStack ChallengesItem;
   ItemStack ProfileItem;
   ItemStack KitUnlocker;
   ItemStack TrailsOpener;
   ItemStack wand_itemstack;
   ItemStack pane_itemstack;
   ItemStack yellow_pane_itemstack;
   ItemStack confirm_itemstack;
   ItemStack cancel_itemstack;
   ItemStack upgrades_itemstack;
   ItemStack next_itemstack;
   ItemStack previous_itemstack;
   ItemStack back_itemstack;
   int[] smartSlots;
   SmartInventory shop;
   Inventory profileInventory;
   Inventory trailsInventory;
   BungeeMode bungeeMode;
   TournamentManager tournamentsManager;
   ChallengesManager challengesManager;
   KBListener listen;
   AbilityListener abilityListener;
   Config config;
   FileManager fileManager;
   AchievementsManager achievementsManager;
   Messages msgs;
   public Economy econ;
   MySQL mysql;
   BukkitTask scoreboardTitleAnimationTask;
   BukkitTask savingTask;
   BukkitTask Updater;
   int totalUpdates;
   long nextUpdate;
   Sound WITHER_SHOOT;
   Sound CLICK;
   Sound WITHER_SPAWN;
   Sound ANVIL_LAND;
   Sound ITEM_PICKUP;
   Sound NOTE_PLING;
   Sound PISTON_EXTEND;
   Sound BLAZE_DEATH;
   Sound ENDERMAN_DEATH;
   Sound ENDERMAN_SCREAM;
   Sound IRONGOLEM_DEATH;
   String character_heart;
   String character_radioactive;
   String character_right_arrow;
   String character_left_arrow;
   boolean availableUpdate;
   static Method setDamageMethod;

   public Kitbattle() {
      this.kb = ChatColor.DARK_AQUA + "[" + ChatColor.AQUA + "KitBattle" + ChatColor.DARK_AQUA + "] " + ChatColor.GRAY;
      this.random = new Random();
      this.colors = new ChatColor[]{ChatColor.DARK_AQUA, ChatColor.GOLD, ChatColor.GRAY, ChatColor.BLUE, ChatColor.GREEN, ChatColor.AQUA, ChatColor.RED, ChatColor.LIGHT_PURPLE, ChatColor.YELLOW};
      this.playingMaps = new HashMap();
      this.tournamentMaps = new HashMap();
      this.challengeMaps = new HashMap();
      this.playerData = new HashMap();
      this.selectionMode = new HashMap();
      this.Kits = new HashMap();
      this.Ranks = new HashMap();
      this.players = new ArrayList();
      this.editmode = new ArrayList();
      this.spectating = new ArrayList();
      this.smartSlots = new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};
      this.econ = null;
      this.totalUpdates = 0;
   }

   public void onEnable() {
      loadConfig0();
      this.plugin = this;
      kitbattleApi = new KitbattleAPI(this);
      this.config = new Config(this);
      this.msgs = new Messages();
      this.checkMCVersion();
      this.character_heart = StringEscapeUtils.unescapeJava("❤");
      this.character_radioactive = StringEscapeUtils.unescapeJava("☢");
      this.character_right_arrow = StringEscapeUtils.unescapeJava("⤇");
      this.character_left_arrow = StringEscapeUtils.unescapeJava("⤆");
      this.listen = new KBListener(this, this.kb);
      this.abilityListener = new AbilityListener(this, this.kb);
      Bukkit.getPluginManager().registerEvents(this.listen, this);
      Bukkit.getPluginManager().registerEvents(this.abilityListener, this);
      this.loadSounds();
      this.wand_itemstack = (new ItemStackBuilder(Material.BLAZE_ROD)).setName(ChatColor.AQUA + "KitBattle" + ChatColor.LIGHT_PURPLE + " Wand").addLore(ChatColor.YELLOW + "--------------------------", ChatColor.GREEN + "Left click to select the first corner", ChatColor.GREEN + "Right click to select the second corner", ChatColor.YELLOW + "--------------------------").build();
      this.pane_itemstack = (new ItemStackBuilder(Material.STAINED_GLASS_PANE)).setDurability(7).setName(" ").build();
      this.yellow_pane_itemstack = (new ItemStackBuilder(Material.STAINED_GLASS)).setDurability(4).setName(" ").build();
      this.confirm_itemstack = (new ItemStackBuilder(Material.STAINED_GLASS)).setDurability(5).setName(ChatColor.GREEN + "Confirm").build();
      this.cancel_itemstack = (new ItemStackBuilder(Material.STAINED_GLASS)).setDurability(14).setName(ChatColor.RED + "Cancel").build();
      this.next_itemstack = (new ItemStackBuilder(Material.STAINED_GLASS)).setDurability(5).setName(ChatColor.GREEN + "Next page " + this.character_right_arrow).build();
      this.previous_itemstack = (new ItemStackBuilder(Material.STAINED_GLASS)).setDurability(14).setName(ChatColor.RED + this.character_left_arrow + " Previous page").build();
      this.back_itemstack = (new ItemStackBuilder(Material.ARROW)).setName(ChatColor.GREEN + "Back").build();
      this.upgrades_itemstack = (new ItemStackBuilder(Material.NETHER_STAR)).setName(ChatColor.GOLD + this.character_radioactive + " Upgrades " + this.character_radioactive).build();
      if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
         Bukkit.getConsoleSender().sendMessage(this.kb + "Found PlaceholderAPI, Hooked: " + (new PlaceholderAPIHooks(this)).register());
      }

      if (Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")) {
         new MVdWPlacholderHook(this);
         Bukkit.getConsoleSender().sendMessage(this.kb + "Found MVdWPlaceholderAPI, Hooked: true");
      }

      this.setupAll();
      Iterator var2 = this.getOnlinePlayers().iterator();

      while(var2.hasNext()) {
         Player var1 = (Player)var2.next();
         this.playerData.put(var1.getName(), new PlayerData(var1, this));
      }

      this.logger.info("[KitBattle] has been enabled!");
   }

   public void onDisable() {
      Iterator var2 = this.getPlayers(this.players).iterator();

      Player var1;
      while(var2.hasNext()) {
         var1 = (Player)var2.next();
         var1.sendMessage(this.kb + this.msgs.pluginReloadKickMessage);
         ((PlayerData)this.playerData.get(var1.getName())).restoreData(var1);
      }

      var2 = this.getPlayers(this.selectionMode.keySet()).iterator();

      while(var2.hasNext()) {
         var1 = (Player)var2.next();
         var1.getInventory().removeItem(new ItemStack[]{this.wand_itemstack});
      }

      var2 = this.abilityListener.toRemove.iterator();

      while(var2.hasNext()) {
         Entity var3 = (Entity)var2.next();
         var3.remove();
      }

      var2 = this.getOnlinePlayers().iterator();

      while(var2.hasNext()) {
         var1 = (Player)var2.next();
         ((PlayerData)this.playerData.get(var1.getName())).saveStatsIntoFile(var1, true);
      }

      var2 = this.abilityListener.toRollback.iterator();

      while(var2.hasNext()) {
         BlockState var4 = (BlockState)var2.next();
         this.abilityListener.Rollback(var4);
      }

      this.logger.info("[KitBattle] has been disabled!");
   }

   public boolean onCommand(final CommandSender var1, Command var2, String var3, final String[] var4) {
      if (var3.equalsIgnoreCase("kb") || var3.equalsIgnoreCase("kitbattle")) {
         if (var4.length == 0) {
            var1.sendMessage("" + ChatColor.DARK_AQUA + ChatColor.STRIKETHROUGH + " ----------" + ChatColor.AQUA + " KitBattle " + ChatColor.DARK_AQUA + ChatColor.STRIKETHROUGH + "----------");
            var1.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/kb" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Main command");
            var1.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/kb " + ChatColor.GREEN + "List" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Shows a list of maps");
            if (!this.config.bungeeMode) {
               var1.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/kb " + ChatColor.GREEN + "Join" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Join the selected map");
               var1.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/kb " + ChatColor.GREEN + "Leave" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Leave your current map");
            } else {
               var1.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/kb " + ChatColor.GREEN + "Vote" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Vote for the next map");
            }

            var1.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/kb " + ChatColor.GREEN + "Stats" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Shows a player stats");
            var1.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/kb " + ChatColor.GREEN + "Gui" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Opens up a gui");
            var1.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/kb " + ChatColor.GREEN + "Selectkit" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Selects the targeted kit");
            var1.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/kb " + ChatColor.GREEN + "Spectate" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Puts you in the spectator mode");
            var1.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/kb " + ChatColor.GREEN + "Spawn" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Teleport back to spawn!");
            var1.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/kb " + ChatColor.GREEN + "Info" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Shows some information");
            var1.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/kb " + ChatColor.RED + "Admin" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Shows the admin commands");
            var1.sendMessage("" + ChatColor.DARK_AQUA + ChatColor.STRIKETHROUGH + " ----------------------------");
         } else if (var4[0].equalsIgnoreCase("admin")) {
            if (var1.hasPermission("kitbattle.admin")) {
               var1.sendMessage("" + ChatColor.DARK_AQUA + ChatColor.STRIKETHROUGH + " ----------" + ChatColor.AQUA + " KitBattle Admin " + ChatColor.DARK_AQUA + ChatColor.STRIKETHROUGH + "----------");
               var1.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/kb " + ChatColor.GREEN + "Wand" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Gives you the selection wand!");
               var1.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/kb " + ChatColor.GREEN + "Create" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Create a new map!");
               var1.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/kb " + ChatColor.GREEN + "Delete" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Delete a map!");
               var1.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/kb " + ChatColor.GREEN + "Addspawn / Removespawn" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Add/Remove spawnpoints");
               var1.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/kb " + ChatColor.GREEN + "Enable / Disable" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Enables/Disables maps");
               var1.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/kb " + ChatColor.GREEN + "Addspawncuboid / Removespawncuboid" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Add/Remove a spawn region");
               var1.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/kb " + ChatColor.GREEN + "Reset" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Resets a player data");
               var1.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/kb " + ChatColor.GREEN + "Coins / Exp" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Modify a player coins or exp");
               var1.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/kb " + ChatColor.GREEN + "Kit" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Modify the kits");
               var1.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/kb " + ChatColor.GREEN + "KitUnlocker" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Give a player Kitunlockers");
               if (!this.config.AllowBuilding) {
                  var1.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/kb " + ChatColor.GREEN + "Editmode" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Allows you to break/place blocks while ingame");
               }

               var1.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/kb " + ChatColor.GREEN + "Reload" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Reload the config files");
               var1.sendMessage("" + ChatColor.DARK_AQUA + ChatColor.STRIKETHROUGH + " ---------------------------------");
            } else {
               var1.sendMessage(this.kb + this.msgs.nopermission);
            }
         } else if (!this.commands.contains(var4[0].toLowerCase())) {
            var1.sendMessage(this.kb + this.msgs.UnknownCommand);
         } else {
            final Player var33;
            String var37;
            final PlayingMap var65;
            if (var4[0].equalsIgnoreCase("join")) {
               if (var1 instanceof Player) {
                  var33 = (Player)var1;
                  if (this.config.bungeeMode || this.isInTournament(var33) || this.isInChallenge(var33)) {
                     var33.sendMessage(this.kb + this.msgs.CommandDisabled);
                     return true;
                  }

                  if (var4.length == 1) {
                     var33.sendMessage(this.kb + "Usage: /kb " + ChatColor.GREEN + "Join" + ChatColor.GRAY + " <Map>");
                     return true;
                  }

                  var37 = var4[1].toLowerCase();
                  if (!this.playingMaps.containsKey(var37)) {
                     var33.sendMessage(this.kb + this.msgs.UnknownMap);
                     return true;
                  }

                  var65 = (PlayingMap)this.playingMaps.get(var37);
                  if (!var65.enabled) {
                     var33.sendMessage(this.kb + this.msgs.MapDisabled);
                     return true;
                  }

                  Location var79 = var65.getSpawnpoint();
                  if (var79 == null) {
                     var33.sendMessage(this.kb + this.msgs.NoAvailableSpawnpoint);
                     return true;
                  }

                  if (this.players.contains(var33.getName())) {
                     if (((PlayerData)this.plugin.playerData.get(var33.getName())).getMap() != null && ((PlayerData)this.plugin.playerData.get(var33.getName())).getMap().name.equals(var65.name)) {
                        var33.sendMessage(this.kb + this.msgs.PlayerAlreadyInMap);
                        return true;
                     }

                     if (((PlayerData)this.playerData.get(var33.getName())).hasCooldown(var33, "Join-Another-Map")) {
                        return true;
                     }

                     ((PlayerData)this.playerData.get(var33.getName())).setCooldown(var33, "Join-Another-Map", 10, false);
                     this.resetPlayerToMap(var33, var65, true);
                  } else {
                     this.join(var33, var65, 0);
                  }

                  var33.sendMessage(this.kb + this.msgs.PlayerJoinMap.replace("%map%", var65.name));
               } else {
                  var1.sendMessage(this.kb + this.msgs.noconsole);
               }
            } else if (var4[0].equalsIgnoreCase("leave")) {
               if (var1 instanceof Player) {
                  var33 = (Player)var1;
                  if (this.config.bungeeMode || this.isInTournament(var33) || this.isInChallenge(var33)) {
                     var33.sendMessage(this.kb + this.msgs.CommandDisabled);
                     return true;
                  }

                  if (this.players.contains(var33.getName())) {
                     this.leave(var33);
                  } else {
                     var33.sendMessage(this.kb + this.msgs.NotInAGame);
                  }
               } else {
                  var1.sendMessage(this.kb + this.msgs.noconsole);
               }
            } else if (var4[0].equalsIgnoreCase("vote")) {
               if (var1 instanceof Player) {
                  var33 = (Player)var1;
                  if (!this.config.bungeeMode || !this.bungeeMode.isShufflerRunning()) {
                     var33.sendMessage(this.kb + this.msgs.CommandDisabled);
                     return true;
                  }

                  var33.openInventory(this.bungeeMode.voteInventory);
               } else {
                  var1.sendMessage(this.kb + this.msgs.noconsole);
               }
            } else if (var4[0].equalsIgnoreCase("stats")) {
               if (var4.length == 1) {
                  if (!(var1 instanceof Player)) {
                     var1.sendMessage(this.kb + "Usage: /kb " + ChatColor.GREEN + "Stats" + ChatColor.GRAY + " <Player>");
                     return true;
                  }

                  var33 = (Player)var1;
               } else {
                  var33 = Bukkit.getPlayer(var4[1]);
               }

               if (var33 == null) {
                  var1.sendMessage(this.kb + this.msgs.PlayerNotFound);
                  return true;
               }

               ((PlayerData)this.playerData.get(var33.getName())).sendStats(var1, var33);
            } else {
               final PlayerData var36;
               Kit var40;
               if (var4[0].equalsIgnoreCase("selectkit")) {
                  if (var1 instanceof Player) {
                     var33 = (Player)var1;
                     if (var4.length == 1) {
                        var33.sendMessage(this.kb + "Usage: /kb " + ChatColor.GREEN + "Selectkit" + ChatColor.GRAY + " <Kit>");
                        return true;
                     }

                     if (this.players.contains(var33.getName())) {
                        var36 = (PlayerData)this.playerData.get(var33.getName());
                        if (var36.getKit() == null) {
                           var40 = (Kit)this.Kits.get(var4[1].toLowerCase());
                           if (var40 != null) {
                              if (var36.kitsInventory.getAllContents().contains(var40.getLogo())) {
                                 if (!var40.isEnabled()) {
                                    var33.sendMessage(this.kb + this.plugin.msgs.KitDisabled);
                                    return true;
                                 }

                                 if (var40.requirePermission && !var33.hasPermission(var40.permission)) {
                                    var33.sendMessage(this.kb + this.plugin.msgs.NoPermissionForKit);
                                    return true;
                                 }

                                 ((PlayerData)this.plugin.playerData.get(var33.getName())).setKit(var33, var40);
                                 var40.giveItems(var33);
                                 if (this.plugin.config.titles_enabled) {
                                    (new TitleObject(this.plugin.msgs.PlayerSelectKit.replace("%kit%", var40.getName()), TitleType.TITLE)).setFadeIn(this.plugin.config.titles_fadeIn).setStay(this.plugin.config.titles_stay).setFadeOut(this.plugin.config.titles_fadeOut).send(var33);
                                 } else {
                                    var33.sendMessage(this.kb + this.plugin.msgs.PlayerSelectKit.replace("%kit%", var40.getName()));
                                 }

                                 Bukkit.getPluginManager().callEvent(new PlayerSelectKitEvent(var33, var40));
                              } else {
                                 var33.sendMessage(this.kb + this.msgs.KitSelectDeny);
                              }
                           } else {
                              var33.sendMessage(this.kb + this.msgs.UnknownKit);
                           }
                        } else {
                           var33.sendMessage(this.kb + this.msgs.AlreadySelectedKit);
                        }
                     } else {
                        var33.sendMessage(this.kb + this.msgs.NotInAGame);
                     }
                  } else {
                     var1.sendMessage(this.kb + this.msgs.noconsole);
                  }
               } else {
                  String var47;
                  if (var4[0].equalsIgnoreCase("gui")) {
                     if (var1 instanceof Player) {
                        var33 = (Player)var1;
                        var37 = "Usage: /kb " + ChatColor.GREEN + "Gui" + ChatColor.GRAY + " Shop/Kits/Profile/Trails";
                        if (var4.length == 1) {
                           var33.sendMessage(this.kb + var37);
                           return true;
                        }

                        if (this.players.contains(var33.getName())) {
                           PlayerData var75 = (PlayerData)this.playerData.get(var33.getName());
                           var47 = var4[1].toLowerCase();
                           if (var47.equals("shop")) {
                              this.shop.open(var33);
                           } else if (var47.equals("kits")) {
                              if (var75.getKit() == null) {
                                 ((PlayerData)this.plugin.playerData.get(var33.getName())).kitsInventory.open(var33);
                              } else {
                                 var33.sendMessage(this.kb + this.msgs.CommandDisabled);
                              }
                           } else if (var47.equals("profile")) {
                              var33.openInventory(this.profileInventory);
                           } else if (var47.equals("trails")) {
                              var33.openInventory(this.trailsInventory);
                           } else {
                              var33.sendMessage(this.kb + var37);
                           }
                        } else {
                           var33.sendMessage(this.kb + this.msgs.NotInAGame);
                        }
                     } else {
                        var1.sendMessage(this.kb + this.msgs.noconsole);
                     }
                  } else {
                     final String var5;
                     final String var38;
                     PlayingMap var44;
                     Map var53;
                     if (var4[0].equalsIgnoreCase("list")) {
                        var1.sendMessage("" + ChatColor.AQUA + ChatColor.STRIKETHROUGH + "--------------------------------------");
                        var5 = this.playingMaps.isEmpty() ? ChatColor.RED + "None" : "";

                        for(Iterator var68 = this.playingMaps.values().iterator(); var68.hasNext(); var5 = var5 + (var5.isEmpty() ? "" : ", ") + (var44.isAvailable() ? ChatColor.GREEN + var44.name + " (" + var44.players.size() + ")" : ChatColor.RED + var44.name)) {
                           var44 = (PlayingMap)var68.next();
                        }

                        var1.sendMessage(this.kb + "Maps: " + var5);
                        var37 = this.tournamentMaps.isEmpty() ? ChatColor.RED + "None" : "";

                        for(Iterator var72 = this.tournamentMaps.values().iterator(); var72.hasNext(); var37 = var37 + (var37.isEmpty() ? "" : ", ") + (var53.enabled ? (this.tournamentsManager.map != null && this.tournamentsManager.map.name.equals(var53.name) ? ChatColor.RED + var53.name + " (" + this.tournamentsManager.getSize() + ")" : ChatColor.GREEN + var53.name) : ChatColor.RED + var53.name)) {
                           var53 = (Map)var72.next();
                        }

                        var1.sendMessage(this.kb + "Tournament maps: " + var37);
                        var38 = this.challengeMaps.isEmpty() ? ChatColor.RED + "None" : "";

                        ChallengeMap var76;
                        for(Iterator var84 = this.challengeMaps.values().iterator(); var84.hasNext(); var38 = var38 + (var38.isEmpty() ? "" : ", ") + (var76.enabled ? (var76.isAvailable() ? ChatColor.GREEN + var76.name : ChatColor.RED + var76.name + " (" + var76.players.size() + ")") : ChatColor.RED + var76.name)) {
                           var76 = (ChallengeMap)var84.next();
                        }

                        var1.sendMessage(this.kb + "Challenge maps: " + var38);
                        var1.sendMessage("" + ChatColor.AQUA + ChatColor.STRIKETHROUGH + "--------------------------------------");
                     } else {
                        long var35;
                        if (var4[0].equalsIgnoreCase("reload")) {
                           if (var1.hasPermission("kitbattle.reload")) {
                              var35 = System.currentTimeMillis();
                              this.setupAll();
                              var1.sendMessage(this.kb + "The plugin has been reloaded! took " + ChatColor.LIGHT_PURPLE + (System.currentTimeMillis() - var35) + "ms");
                           } else {
                              var1.sendMessage(this.kb + this.msgs.nopermission);
                           }
                        } else {
                           int var8;
                           if (var4[0].equalsIgnoreCase("spectate")) {
                              if (var1 instanceof Player) {
                                 var33 = (Player)var1;
                                 if (var33.hasPermission("kitbattle.spectate")) {
                                    if (this.isInTournament(var33) || this.isInChallenge(var33)) {
                                       var33.sendMessage(this.kb + this.msgs.CommandDisabled);
                                       return true;
                                    }

                                    if (GameMode.values().length < 4) {
                                       var33.sendMessage(this.kb + this.msgs.SpectatorModeNotSupported);
                                       return true;
                                    }

                                    if (!this.players.contains(var33.getName())) {
                                       var33.sendMessage(this.kb + this.msgs.NotInAGame);
                                       return true;
                                    }

                                    var36 = (PlayerData)this.playerData.get(var33.getName());
                                    var65 = var36.getMap();
                                    if (!this.spectating.contains(var33.getName())) {
                                       var8 = var33.hasPermission("kitbattle.spectate.bypass") ? 0 : this.config.spectateCountdownSeconds;
                                       final Location var82 = var33.getLocation().getBlock().getLocation();
                                       if (var36.hasCooldown(var33, "Spectate")) {
                                          return true;
                                       }

                                       var36.setCooldown(var33, "Spectate", var8, false);
                                       (new BukkitRunnable(var8) {
                                          int seconds;

                                          {
                                             this.seconds = var2;
                                          }

                                          public void run() {
                                             if (Bukkit.getPlayer(var33.getName()) == null) {
                                                this.cancel();
                                             } else if (var33.getLocation().getBlock().getLocation().equals(var82) && var36.getMap().name.equals(var65.name)) {
                                                if (this.seconds == 0) {
                                                   Kitbattle.this.spectating.add(var33.getName());
                                                   var33.setGameMode(GameMode.valueOf("SPECTATOR"));
                                                   var33.sendMessage(Kitbattle.this.kb + Kitbattle.this.msgs.SpectatorModeEnable);
                                                   this.cancel();
                                                } else {
                                                   var33.sendMessage(Kitbattle.this.kb + Kitbattle.this.msgs.MovementNotAllowed.replace("%seconds%", String.valueOf(this.seconds)));
                                                   --this.seconds;
                                                }
                                             } else {
                                                var33.sendMessage(Kitbattle.this.kb + Kitbattle.this.msgs.MovementOccur);
                                                this.cancel();
                                             }
                                          }
                                       }).runTaskTimer(this, 0L, 20L);
                                    } else {
                                       this.spectating.remove(var33.getName());
                                       var33.setGameMode(GameMode.valueOf("SURVIVAL"));
                                       var33.teleport(var65.getSpawnpoint());
                                       var33.sendMessage(this.kb + this.msgs.SpectatorModeDisable);
                                    }
                                 } else {
                                    var33.sendMessage(this.kb + this.msgs.nopermission);
                                 }
                              } else {
                                 var1.sendMessage(this.kb + this.msgs.noconsole);
                              }
                           } else {
                              int var46;
                              if (var4[0].equalsIgnoreCase("spawn")) {
                                 if (!(var1 instanceof Player)) {
                                    var1.sendMessage(this.kb + this.msgs.noconsole);
                                    return true;
                                 }

                                 var33 = (Player)var1;
                                 if (!var33.hasPermission("kitbattle.spawn")) {
                                    var33.sendMessage(this.kb + this.msgs.nopermission);
                                    return true;
                                 }

                                 if (this.isInTournament(var33) || this.isInChallenge(var33)) {
                                    var33.sendMessage(this.kb + this.msgs.CommandDisabled);
                                    return true;
                                 }

                                 var36 = (PlayerData)this.playerData.get(var33.getName());
                                 if (!this.players.contains(var33.getName())) {
                                    var33.sendMessage(this.kb + this.msgs.NotInAGame);
                                    return true;
                                 }

                                 final Location var60 = var33.getLocation().getBlock().getLocation();
                                 final PlayingMap var69 = var36.getMap();
                                 var46 = var33.hasPermission("kitbattle.spawn.bypass") ? 0 : this.config.spawnTeleportCountdownSeconds;
                                 if (var36.hasCooldown(var33, "Spawn")) {
                                    return true;
                                 }

                                 var36.setCooldown(var33, "Spawn", var46, false);
                                 (new BukkitRunnable(var46) {
                                    int seconds;

                                    {
                                       this.seconds = var2;
                                    }

                                    public void run() {
                                       if (Bukkit.getPlayer(var33.getName()) == null) {
                                          this.cancel();
                                       } else if (var33.getLocation().getBlock().getLocation().equals(var60) && var36.getMap().name.equals(var69.name)) {
                                          if (this.seconds != 0) {
                                             var33.sendMessage(Kitbattle.this.kb + Kitbattle.this.msgs.MovementNotAllowed.replace("%seconds%", String.valueOf(this.seconds)));
                                             --this.seconds;
                                          } else {
                                             var33.teleport(var69.getSpawnpoint());
                                             PlayerData var1 = (PlayerData)Kitbattle.this.playerData.get(var33.getName());
                                             if (var1.getKit() != null) {
                                                String var2 = var1.getKit().getName();
                                                var1.setKit(var33, (Kit)null);
                                                var33.getInventory().clear();
                                                var33.getInventory().setArmorContents((ItemStack[])null);
                                                Iterator var4 = var33.getActivePotionEffects().iterator();

                                                while(var4.hasNext()) {
                                                   PotionEffect var3 = (PotionEffect)var4.next();
                                                   var33.removePotionEffect(var3.getType());
                                                }

                                                var33.setFireTicks(0);
                                                var33.setHealth(var33.getMaxHealth());
                                                if (var33.getVehicle() != null) {
                                                   Entity var5 = var33.getVehicle();
                                                   if (Kitbattle.this.abilityListener.toRemove.contains(var5)) {
                                                      Kitbattle.this.abilityListener.toRemove.remove(var5);
                                                      var5.remove();
                                                   }
                                                }

                                                var33.setAllowFlight(false);
                                                var33.setFlying(false);
                                                Kitbattle.this.giveDefaultItems(var33);
                                                var33.sendMessage(Kitbattle.this.kb + Kitbattle.this.msgs.PlayerResetKit.replace("%kit%", var2));
                                             }

                                             this.cancel();
                                          }
                                       } else {
                                          var33.sendMessage(Kitbattle.this.kb + Kitbattle.this.msgs.MovementOccur);
                                          this.cancel();
                                       }
                                    }
                                 }).runTaskTimer(this, 0L, 20L);
                              } else {
                                 FileConfiguration var7;
                                 if (var4[0].equalsIgnoreCase("create")) {
                                    if (var1 instanceof Player) {
                                       var33 = (Player)var1;
                                       if (!var33.hasPermission("kitbattle.create")) {
                                          var33.sendMessage(this.kb + this.msgs.nopermission);
                                       } else {
                                          if (var4.length >= 3 && (var4[2].equalsIgnoreCase("Normal") || var4[2].equalsIgnoreCase("Tournament") || var4[2].equalsIgnoreCase("Challenge"))) {
                                             if (!var4[2].equalsIgnoreCase("Challenge") || var4.length >= 4 && var4[3].length() == 3 && var4[3].toLowerCase().charAt(1) == 'v' && this.isNumber(String.valueOf(var4[3].charAt(0))) && this.isNumber(String.valueOf(var4[3].charAt(2))) && Integer.valueOf(String.valueOf(var4[3].charAt(2))) == Integer.valueOf(String.valueOf(var4[3].charAt(0)))) {
                                                var37 = var4[1].toLowerCase();
                                                if (!this.playingMaps.containsKey(var37) && !this.tournamentMaps.containsKey(var37) && !this.challengeMaps.containsKey(var37)) {
                                                   var7 = this.fileManager.getConfig("maps.yml");
                                                   var7.set("Maps." + var4[1] + ".Type", var4[2]);
                                                   if (var4[2].equalsIgnoreCase("Challenge")) {
                                                      var7.set("Maps." + var4[1] + ".Players-Per-Team", Integer.valueOf(String.valueOf(var4[3].charAt(2))));
                                                   }

                                                   var7.set("Maps." + var4[1] + ".Enabled", true);
                                                   this.fileManager.saveConfig("maps.yml");
                                                   if (var4[2].equalsIgnoreCase("Normal")) {
                                                      this.playingMaps.put(var37, new PlayingMap(this, var4[1], new ArrayList(), new ArrayList(), true, new HashMap()));
                                                      if (this.bungeeMode != null) {
                                                         this.bungeeMode.updateMap();
                                                      }
                                                   } else if (var4[2].equalsIgnoreCase("Tournament")) {
                                                      this.tournamentMaps.put(var37, new TournamentMap(this, var4[1], new ArrayList(), true));
                                                      if (this.tournamentsManager == null) {
                                                         this.tournamentsManager = new TournamentManager(this);
                                                      }
                                                   } else if (var4[2].equalsIgnoreCase("Challenge")) {
                                                      this.challengeMaps.put(var37, new ChallengeMap(this, var4[1], new ArrayList(), Integer.valueOf(String.valueOf(var4[3].charAt(2))), true));
                                                      if (this.challengesManager == null) {
                                                         this.challengesManager = new ChallengesManager(this);
                                                      }
                                                   }

                                                   var33.sendMessage(this.kb + "The map " + ChatColor.AQUA + var4[1] + ChatColor.GRAY + " has been created successfully!");
                                                   var33.performCommand("kb addspawn " + var37);
                                                   return true;
                                                }

                                                var33.sendMessage(this.kb + "There is already a map with that name!");
                                                return true;
                                             }

                                             var33.sendMessage(this.kb + "Usage: /kb " + ChatColor.GREEN + "Create" + ChatColor.GRAY + " <Map> Challenge 1v1/2v2/3v3/XvX");
                                             return true;
                                          }

                                          var33.sendMessage(this.kb + "Usage: /kb " + ChatColor.GREEN + "Create" + ChatColor.GRAY + " <Map> <Normal/Tournament/Challenge>");
                                          return true;
                                       }
                                    } else {
                                       var1.sendMessage(this.kb + this.msgs.noconsole);
                                    }
                                 } else {
                                    Map var6;
                                    if (var4[0].equalsIgnoreCase("delete")) {
                                       if (var1.hasPermission("kitbattle.delete")) {
                                          if (var4.length == 1) {
                                             var1.sendMessage(this.kb + "Usage: /kb " + ChatColor.GREEN + "Delete" + ChatColor.GRAY + " <Map>");
                                             return true;
                                          }

                                          var5 = var4[1].toLowerCase();
                                          if (!this.playingMaps.containsKey(var5) && !this.tournamentMaps.containsKey(var5) && !this.challengeMaps.containsKey(var5)) {
                                             var1.sendMessage(this.kb + this.msgs.UnknownMap);
                                             return true;
                                          }

                                          var6 = this.playingMaps.containsKey(var5) ? (Map)this.playingMaps.get(var5) : (this.tournamentMaps.containsKey(var5) ? (Map)this.tournamentMaps.get(var5) : (Map)this.challengeMaps.get(var5));
                                          this.fileManager.getConfig("maps.yml").set("Maps." + var6.name, (Object)null);
                                          this.fileManager.saveConfig("maps.yml");
                                          if (this.playingMaps.containsKey(var5)) {
                                             ((PlayingMap)this.playingMaps.get(var5)).removePlayers();
                                             this.playingMaps.remove(var5);
                                          } else if (this.tournamentMaps.containsKey(var5)) {
                                             this.tournamentMaps.remove(var5);
                                             if (this.tournamentsManager != null && this.tournamentsManager.map.name.toLowerCase().equals(var5)) {
                                                if (this.tournamentMaps.isEmpty()) {
                                                   this.tournamentsManager.clearQueue();
                                                }

                                                this.tournamentsManager.stop();
                                             }
                                          } else {
                                             ChallengeMap var57 = (ChallengeMap)this.challengeMaps.get(var5);
                                             this.challengeMaps.remove(var5);
                                             if (var57.isRunning()) {
                                                var57.stop();
                                             }

                                             if (this.challengeMaps.isEmpty()) {
                                                this.challengesManager = null;
                                             }
                                          }

                                          var1.sendMessage(this.kb + "The map " + ChatColor.AQUA + var6.name + ChatColor.GRAY + " was deleted successfully!");
                                       } else {
                                          var1.sendMessage(this.kb + this.msgs.nopermission);
                                       }
                                    } else {
                                       ChallengeMap var9;
                                       Iterator var10;
                                       Iterator var11;
                                       if (!var4[0].equalsIgnoreCase("enable") && !var4[0].equalsIgnoreCase("disable")) {
                                          FileConfiguration var59;
                                          if (var4[0].equalsIgnoreCase("addspawn")) {
                                             if (var1 instanceof Player) {
                                                var33 = (Player)var1;
                                                if (var33.hasPermission("kitbattle.addspawn")) {
                                                   if (var4.length == 1) {
                                                      var33.sendMessage(this.kb + "Usage: /kb " + ChatColor.GREEN + "Addspawn" + ChatColor.GRAY + " <Map>");
                                                      return true;
                                                   }

                                                   var37 = var4[1].toLowerCase();
                                                   if (!this.playingMaps.containsKey(var37) && !this.tournamentMaps.containsKey(var37) && !this.challengeMaps.containsKey(var37)) {
                                                      var33.sendMessage(this.kb + this.msgs.UnknownMap);
                                                      return true;
                                                   }

                                                   var53 = this.playingMaps.containsKey(var37) ? (Map)this.playingMaps.get(var37) : (this.tournamentMaps.containsKey(var37) ? (Map)this.tournamentMaps.get(var37) : (Map)this.challengeMaps.get(var37));
                                                   var59 = this.fileManager.getConfig("maps.yml");
                                                   var46 = var59.getConfigurationSection("Maps." + var53.name + ".Spawnpoints") != null && !var59.getConfigurationSection("Maps." + var53.name + ".Spawnpoints").getKeys(false).isEmpty() ? var59.getConfigurationSection("Maps." + var53.name + ".Spawnpoints").getKeys(false).size() + 1 : 1;
                                                   String var81 = this.getStringFromLocation(var33.getLocation(), true);
                                                   var59.set("Maps." + var53.name + ".Spawnpoints." + var46, var81);
                                                   this.fileManager.saveConfig("maps.yml");
                                                   var53.spawnpoints.add(this.getLocationFromString(var81));
                                                   if (this.playingMaps.containsKey(var37) && this.config.bungeeMode && var53.enabled) {
                                                      if (this.bungeeMode == null) {
                                                         this.bungeeMode = new BungeeMode(this);
                                                      } else if (var53.spawnpoints.size() == 1) {
                                                         this.bungeeMode.updateMap();
                                                      }
                                                   } else if (this.plugin.tournamentMaps.containsKey(var37) && this.tournamentsManager == null && var53.isAvailable()) {
                                                      this.tournamentsManager = new TournamentManager(this);
                                                   } else if (this.plugin.challengeMaps.containsKey(var37) && this.challengesManager == null && ((ChallengeMap)var53).isAvailable()) {
                                                      this.challengesManager = new ChallengesManager(this);
                                                   }

                                                   var33.sendMessage(this.kb + "You have added a new spawnpoint with the id of " + ChatColor.AQUA + "#" + var46 + ChatColor.GRAY + " to the map " + ChatColor.AQUA + var53.name + " at " + this.getReadableLocationString(var33.getLocation(), true));
                                                } else {
                                                   var33.sendMessage(this.kb + this.msgs.nopermission);
                                                }
                                             } else {
                                                var1.sendMessage(this.kb + this.msgs.noconsole);
                                             }
                                          } else {
                                             final int var54;
                                             int var67;
                                             if (var4[0].equalsIgnoreCase("removespawn")) {
                                                if (var1.hasPermission("kitbattle.removespawn")) {
                                                   if (var4.length == 1) {
                                                      var1.sendMessage(this.kb + "Usage: /kb " + ChatColor.GREEN + "Removespawn" + ChatColor.GRAY + " <Map>");
                                                      return true;
                                                   }

                                                   var5 = var4[1].toLowerCase();
                                                   if (!this.playingMaps.containsKey(var5) && !this.tournamentMaps.containsKey(var5) && !this.challengeMaps.containsKey(var5)) {
                                                      var1.sendMessage(this.kb + this.msgs.UnknownMap);
                                                      return true;
                                                   }

                                                   var6 = this.playingMaps.containsKey(var5) ? (Map)this.playingMaps.get(var5) : (this.tournamentMaps.containsKey(var5) ? (Map)this.tournamentMaps.get(var5) : (Map)this.challengeMaps.get(var5));
                                                   var7 = this.fileManager.getConfig("maps.yml");
                                                   if (var6.spawnpoints.isEmpty() || var7.getConfigurationSection("Maps." + var6.name + ".Spawnpoints") == null || var7.getConfigurationSection("Maps." + var6.name + ".Spawnpoints").getKeys(false).isEmpty()) {
                                                      var1.sendMessage(this.kb + "The map does not have any spawnpoints!");
                                                      return true;
                                                   }

                                                   var8 = var6.spawnpoints.size();
                                                   var7.set("Maps." + var6.name + ".Spawnpoints." + var8, (Object)null);
                                                   this.fileManager.saveConfig("maps.yml");
                                                   var6.spawnpoints.remove(var6.spawnpoints.get(var8 - 1));
                                                   if (this.playingMaps.containsKey(var5) && var6.spawnpoints.isEmpty()) {
                                                      ((PlayingMap)var6).removePlayers();
                                                   } else if (this.tournamentMaps.containsKey(var5) && var6.spawnpoints.isEmpty() && this.tournamentsManager != null && !this.tournamentsManager.isStarting() && (!this.tournamentsManager.isRunning() || this.tournamentsManager.map.name.equals(var6.name))) {
                                                      this.tournamentsManager.stop();
                                                   } else if (this.challengeMaps.containsKey(var5) && this.challengesManager != null) {
                                                      var9 = (ChallengeMap)var6;
                                                      if (!var9.isAvailable()) {
                                                         if (((ChallengeMap)var6).isRunning()) {
                                                            ((ChallengeMap)var6).stop();
                                                         }

                                                         var54 = 0;
                                                         Iterator var88 = this.challengeMaps.values().iterator();

                                                         while(var88.hasNext()) {
                                                            ChallengeMap var91 = (ChallengeMap)var88.next();
                                                            if (var91.isAvailable()) {
                                                               ++var54;
                                                            }
                                                         }

                                                         if (var54 == 0) {
                                                            this.challengesManager = null;
                                                         } else {
                                                            var67 = 0;
                                                            Iterator var95 = this.challengeMaps.values().iterator();

                                                            while(var95.hasNext()) {
                                                               ChallengeMap var92 = (ChallengeMap)var95.next();
                                                               if (var92.playersPerTeam == ((ChallengeMap)var6).playersPerTeam && var9.isAvailable()) {
                                                                  ++var67;
                                                               }
                                                            }

                                                            if (var67 == 0) {
                                                               this.challengesManager.queues.remove(((ChallengeMap)var6).playersPerTeam);
                                                            }
                                                         }
                                                      }
                                                   }

                                                   var1.sendMessage(this.kb + "You have removed the last spawnpoint that was added! " + ChatColor.AQUA + "(#" + var8 + ")");
                                                } else {
                                                   var1.sendMessage(this.kb + this.msgs.nopermission);
                                                }
                                             } else if (var4[0].equalsIgnoreCase("reset")) {
                                                if (var1.hasPermission("kitbattle.reset")) {
                                                   if (var4.length == 1) {
                                                      var1.sendMessage(this.kb + "Usage: /kb " + ChatColor.GREEN + "Reset" + ChatColor.GRAY + " <Player>");
                                                      return true;
                                                   }

                                                   var33 = Bukkit.getPlayer(var4[1]);
                                                   if (var33 != null) {
                                                      var36 = (PlayerData)this.playerData.get(var33.getName());
                                                      var36.joined = true;
                                                      var36.resetPlayer(var33);
                                                      if (this.players.contains(var33.getName())) {
                                                         var36.createScoreboard(var33);
                                                         if (var36.getKit() == null) {
                                                            var33.getInventory().clear();
                                                            this.giveDefaultItems(var33);
                                                         }
                                                      }

                                                      var1.sendMessage(this.kb + "The player " + ChatColor.AQUA + var4[1] + ChatColor.GRAY + " data has been " + ChatColor.GREEN + "successfully " + ChatColor.GRAY + "cleared!");
                                                   } else {
                                                      final long var48 = System.currentTimeMillis();
                                                      final String[] var61 = new String[2];
                                                      this.fileManager.executeDatabaseUpdate(var1, var4[1], new BukkitRunnable() {
                                                         public void run() {
                                                            long var1x;
                                                            if (var61[1] == null) {
                                                               try {
                                                                  Kitbattle.this.mysql.getConnection().prepareStatement("delete from " + Kitbattle.this.config.tableprefix + " WHERE player_name= '" + var61[0] + "';").executeUpdate();
                                                                  var1x = System.currentTimeMillis() - var48;
                                                                  var1.sendMessage(Kitbattle.this.msgs.prefix + "The player " + ChatColor.YELLOW + var61[0] + ChatColor.GOLD + " data has been erased! took " + ChatColor.LIGHT_PURPLE + var1x + "ms " + ChatColor.GOLD + "(" + ChatColor.AQUA + var1x / 1000L + "s" + ChatColor.GOLD + ") to erase the player data");
                                                               } catch (SQLException var3) {
                                                                  var3.printStackTrace();
                                                               }
                                                            } else {
                                                               Kitbattle.this.fileManager.getConfig("players.yml").set("Players." + var61[0], (Object)null);
                                                               Kitbattle.this.fileManager.saveConfig("players.yml");
                                                               var1x = System.currentTimeMillis() - var48;
                                                               var1.sendMessage(Kitbattle.this.msgs.prefix + "The player " + ChatColor.YELLOW + var61[1] + ChatColor.GOLD + " data has been erased! took " + ChatColor.LIGHT_PURPLE + var1x + "ms " + ChatColor.GOLD + "(" + ChatColor.AQUA + var1x / 1000L + "s" + ChatColor.GOLD + ") to erase the player data");
                                                            }

                                                         }
                                                      }, var61);
                                                   }
                                                } else {
                                                   var1.sendMessage(this.kb + this.msgs.nopermission);
                                                }
                                             } else {
                                                int var12;
                                                if (!var4[0].equalsIgnoreCase("coins") && !var4[0].equalsIgnoreCase("exp")) {
                                                   if (var4[0].equalsIgnoreCase("wand")) {
                                                      if (var1 instanceof Player) {
                                                         var33 = (Player)var1;
                                                         if (var33.hasPermission("kitbattle.wand")) {
                                                            if (!this.selectionMode.containsKey(var33.getName())) {
                                                               var33.getInventory().addItem(new ItemStack[]{this.wand_itemstack});
                                                               this.selectionMode.put(var33.getName(), new Location[2]);
                                                               var33.sendMessage(this.kb + "You have entered the selection mode!");
                                                            } else {
                                                               var33.getInventory().removeItem(new ItemStack[]{this.wand_itemstack});
                                                               this.selectionMode.remove(var33.getName());
                                                               var33.sendMessage(this.kb + "You have left the selection mode!");
                                                            }
                                                         } else {
                                                            var33.sendMessage(this.kb + this.msgs.nopermission);
                                                         }
                                                      } else {
                                                         var1.sendMessage(this.kb + this.msgs.noconsole);
                                                      }
                                                   } else if (var4[0].equalsIgnoreCase("addspawncuboid")) {
                                                      if (var1 instanceof Player) {
                                                         var33 = (Player)var1;
                                                         if (var33.hasPermission("kitbattle.addspawncuboid")) {
                                                            if (var4.length == 1) {
                                                               var33.sendMessage(this.kb + "Usage: /kb " + ChatColor.GREEN + "Addspawncuboid " + ChatColor.GRAY + "<Map>");
                                                               return true;
                                                            }

                                                            if (!this.playingMaps.containsKey(var4[1].toLowerCase())) {
                                                               var33.sendMessage(this.kb + this.msgs.UnknownMap);
                                                               return true;
                                                            }

                                                            if (!this.selectionMode.containsKey(var33.getName()) || ((Location[])this.selectionMode.get(var33.getName()))[0] == null || ((Location[])this.selectionMode.get(var33.getName()))[1] == null) {
                                                               var33.sendMessage(this.kb + "You haven't selected the 2 corners yet!");
                                                               return true;
                                                            }

                                                            var44 = (PlayingMap)this.playingMaps.get(var4[1].toLowerCase());
                                                            Cuboid var50 = new Cuboid(((Location[])this.selectionMode.get(var33.getName()))[0], ((Location[])this.selectionMode.get(var33.getName()))[1]);
                                                            var59 = this.fileManager.getConfig("maps.yml");
                                                            var46 = var59.getConfigurationSection("Maps." + var44.name + ".Spawn-Cuboids") != null && !var59.getConfigurationSection("Maps." + var44.name + ".Spawn-Cuboids").getKeys(false).isEmpty() ? var59.getConfigurationSection("Maps." + var44.name + ".Spawn-Cuboids").getKeys(false).size() + 1 : 1;
                                                            var59.set("Maps." + var44.name + ".Spawn-Cuboids." + var46, var50.toString());
                                                            this.fileManager.saveConfig("maps.yml");
                                                            var44.spawnCuboids.add(var50);
                                                            var33.sendMessage(this.kb + "You have added a new cuboid with the id of " + ChatColor.AQUA + "#" + var46 + ChatColor.GRAY + " to the map " + ChatColor.AQUA + var44.name + "!");
                                                         } else {
                                                            var33.sendMessage(this.kb + this.msgs.nopermission);
                                                         }
                                                      } else {
                                                         var1.sendMessage(this.kb + this.msgs.noconsole);
                                                      }
                                                   } else if (var4[0].equalsIgnoreCase("removespawncuboid")) {
                                                      if (var1 instanceof Player) {
                                                         var33 = (Player)var1;
                                                         if (var33.hasPermission("kitbattle.removespawncuboid")) {
                                                            if (var4.length == 1) {
                                                               var33.sendMessage(this.kb + "Usage: /kb " + ChatColor.GREEN + "Removespawncuboid " + ChatColor.GRAY + "<Map>");
                                                               return true;
                                                            }

                                                            if (!this.playingMaps.containsKey(var4[1].toLowerCase())) {
                                                               var33.sendMessage(this.kb + this.msgs.UnknownMap);
                                                               return true;
                                                            }

                                                            var44 = (PlayingMap)this.playingMaps.get(var4[1].toLowerCase());
                                                            var7 = this.fileManager.getConfig("maps.yml");
                                                            if (var44.spawnCuboids.isEmpty() || var7.getConfigurationSection("Maps." + var44.name + ".Spawn-Cuboids") == null || var7.getConfigurationSection("Maps." + var44.name + ".Spawn-Cuboids").getKeys(false).isEmpty()) {
                                                               var1.sendMessage(this.kb + "The map does not have any spawn cuboids!");
                                                               return true;
                                                            }

                                                            var8 = var44.spawnCuboids.size();
                                                            var7.set("Maps." + var44.name + ".Spawn-Cuboids." + var8, (Object)null);
                                                            this.fileManager.saveConfig("maps.yml");
                                                            var44.spawnCuboids.remove(var44.spawnCuboids.get(var8 - 1));
                                                            var1.sendMessage(this.kb + "You have removed the last spawn cuboid that was added! " + ChatColor.AQUA + "(#" + var8 + ")");
                                                         } else {
                                                            var33.sendMessage(this.kb + this.msgs.nopermission);
                                                         }
                                                      } else {
                                                         var1.sendMessage(this.kb + this.msgs.noconsole);
                                                      }
                                                   } else if (var4[0].equalsIgnoreCase("kit")) {
                                                      if (!var1.hasPermission("kitbattle.kit")) {
                                                         var1.sendMessage(this.kb + this.msgs.nopermission);
                                                      } else {
                                                         if (var4.length == 1 || !var4[1].equalsIgnoreCase("Give") && !var4[1].equalsIgnoreCase("Create") && !var4[1].equalsIgnoreCase("Delete") && !var4[1].equalsIgnoreCase("Rename") && !var4[1].equalsIgnoreCase("Enable") && !var4[1].equalsIgnoreCase("Disable")) {
                                                            var1.sendMessage(this.kb + "Usage: /kb " + ChatColor.GREEN + "Kit " + ChatColor.GRAY + "Give/Create/Delete/Rename/Enable/Disable");
                                                            return true;
                                                         }

                                                         var5 = var4[1].toLowerCase();
                                                         Player var41;
                                                         if (var5.equals("give")) {
                                                            if (var4.length < 4) {
                                                               var1.sendMessage(this.kb + "Usage: /kb " + ChatColor.GREEN + "Kit " + ChatColor.GRAY + "Give <Player> <Kit>");
                                                               return true;
                                                            }

                                                            var41 = Bukkit.getPlayer(var4[2]);
                                                            if (var41 == null) {
                                                               var1.sendMessage(this.kb + this.msgs.PlayerNotFound);
                                                               return true;
                                                            }

                                                            var40 = (Kit)this.Kits.get(var4[3].toLowerCase());
                                                            if (var40 == null) {
                                                               var1.sendMessage(this.kb + "Couldn't find a kit with that name!");
                                                               return true;
                                                            }

                                                            PlayerData var55 = (PlayerData)this.plugin.playerData.get(var41.getName());
                                                            Entry var73 = var55.kitsInventory.getEmptySlot();
                                                            var55.kitsInventory.setItem((Integer)var73.getKey(), (Integer)var73.getValue(), var40.getLogo());
                                                            var1.sendMessage(this.kb + "The kit " + ChatColor.AQUA + var40.getName() + ChatColor.GRAY + " has been given to the player " + ChatColor.LIGHT_PURPLE + var41.getName());
                                                         } else if (var5.equals("create")) {
                                                            if (var1 instanceof Player) {
                                                               var41 = (Player)var1;
                                                               if (var4.length < 5 || !this.isMaterial(var4[3].split(":")[0].toUpperCase()) || !this.isNumber(var4[4])) {
                                                                  var41.sendMessage(this.kb + "Usage: /kb " + ChatColor.GREEN + "Kit " + ChatColor.GRAY + "Create <Name> <Logo " + ChatColor.LIGHT_PURPLE + ChatColor.UNDERLINE + "(MATERIAL ENUM) " + ChatColor.GRAY + "> <Price>");
                                                                  return true;
                                                               }

                                                               var38 = var4[2];
                                                               if (this.Kits.containsKey(var38.toLowerCase())) {
                                                                  var41.sendMessage(this.kb + "There is already a kit with that name!");
                                                                  return true;
                                                               }

                                                               var8 = Integer.valueOf(var4[4]);
                                                               ItemStackBuilder var71 = var4[3].contains(":") ? (new ItemStackBuilder(Material.valueOf(var4[3].split(":")[0].toUpperCase()))).setDurability(Integer.valueOf(var4[3].split(":")[1])).setName(ChatColor.LIGHT_PURPLE + var38) : (new ItemStackBuilder(Material.valueOf(var4[3].toUpperCase()))).setName(ChatColor.GREEN + var38);
                                                               var71.addLore(ChatColor.AQUA + "There is no description for this kit!");
                                                               ItemStack[] var77 = var41.getInventory().getContents();
                                                               ItemStack[] var89 = var41.getInventory().getArmorContents();
                                                               Collection var85 = var41.getActivePotionEffects();
                                                               Kit var93 = new Kit(this.plugin, var38, var71.build(), var77, var89, var8, var85, new ArrayList(), true, false);
                                                               this.Kits.put(var38.toLowerCase(), var93);
                                                               FileConfiguration var97 = this.fileManager.getConfig("kits.yml");
                                                               var97.set("Kits." + var38 + ".Enabled", true);
                                                               var97.set("Kits." + var38 + ".Require-Permission", false);
                                                               var97.set("Kits." + var38 + ".Item", var4[3].toUpperCase());
                                                               var97.set("Kits." + var38 + ".Price", var8);
                                                               var97.set("Kits." + var38 + ".Armor.Helmet", this.fileManager.transformItemStackToString(var41.getInventory().getHelmet()));
                                                               var97.set("Kits." + var38 + ".Armor.Chestplate", this.fileManager.transformItemStackToString(var41.getInventory().getChestplate()));
                                                               var97.set("Kits." + var38 + ".Armor.Leggings", this.fileManager.transformItemStackToString(var41.getInventory().getLeggings()));
                                                               var97.set("Kits." + var38 + ".Armor.Boots", this.fileManager.transformItemStackToString(var41.getInventory().getBoots()));
                                                               ArrayList var98 = new ArrayList();
                                                               ItemStack[] var105 = var77;
                                                               int var103 = var77.length;

                                                               for(int var101 = 0; var101 < var103; ++var101) {
                                                                  ItemStack var99 = var105[var101];
                                                                  var98.add(this.fileManager.transformItemStackToString(var99));
                                                               }

                                                               var97.set("Kits." + var38 + ".Items", var98);
                                                               ArrayList var100 = new ArrayList();
                                                               Iterator var104 = var85.iterator();

                                                               PotionEffect var102;
                                                               while(var104.hasNext()) {
                                                                  var102 = (PotionEffect)var104.next();
                                                                  var100.add(var102.getType().getName() + " : " + var102.getDuration() / 20 + " : " + var102.getAmplifier());
                                                               }

                                                               var97.set("Kits." + var38 + ".Abilities", Arrays.asList());
                                                               var97.set("Kits." + var38 + ".Potion-Effects", var100);
                                                               var97.set("Kits." + var38 + ".Description", Arrays.asList("&bThere is no description for this kit!"));
                                                               this.fileManager.saveConfig("kits.yml");
                                                               var41.getInventory().clear();
                                                               var41.getInventory().setArmorContents((ItemStack[])null);
                                                               var104 = var41.getActivePotionEffects().iterator();

                                                               while(var104.hasNext()) {
                                                                  var102 = (PotionEffect)var104.next();
                                                                  var41.removePotionEffect(var102.getType());
                                                               }

                                                               var41.sendMessage(this.kb + "The " + ChatColor.GREEN + var38 + ChatColor.GRAY + " kit has been created!");
                                                            } else {
                                                               var1.sendMessage(this.kb + this.msgs.noconsole);
                                                            }
                                                         } else {
                                                            int var13;
                                                            ItemStack var45;
                                                            int var80;
                                                            if (var5.equals("delete")) {
                                                               if (var4.length == 2) {
                                                                  var1.sendMessage(this.kb + "Usage: /kb " + ChatColor.GREEN + "Delete" + ChatColor.GRAY + " <Kit>");
                                                                  return true;
                                                               }

                                                               var37 = var4[2].toLowerCase();
                                                               if (!this.Kits.containsKey(var37)) {
                                                                  var1.sendMessage(this.kb + "Couldn't find a kit with that name!");
                                                                  return true;
                                                               }

                                                               var38 = ((Kit)this.Kits.get(var37)).getName();
                                                               var45 = ((Kit)this.Kits.get(var37)).getLogo();
                                                               ItemStack var52 = ((Kit)this.Kits.get(var37)).getShopLogo();
                                                               this.Kits.remove(var37);
                                                               this.fileManager.getConfig("kits.yml").set("Kits." + var38, (Object)null);
                                                               this.fileManager.saveConfig("kits.yml");

                                                               for(var54 = 0; var54 < this.shop.getSize(); ++var54) {
                                                                  int[] var14;
                                                                  var13 = (var14 = this.smartSlots).length;

                                                                  for(var12 = 0; var12 < var13; ++var12) {
                                                                     var67 = var14[var12];
                                                                     ItemStack var15 = this.shop.getItem(var54, var67);
                                                                     if (var15 != null && var15.equals(var52)) {
                                                                        this.shop.removeItem(var54, var67);
                                                                     }
                                                                  }
                                                               }

                                                               var11 = this.playerData.values().iterator();

                                                               while(var11.hasNext()) {
                                                                  PlayerData var63 = (PlayerData)var11.next();

                                                                  for(var12 = 0; var12 < var63.kitsInventory.getSize(); ++var12) {
                                                                     int[] var16;
                                                                     int var86 = (var16 = this.smartSlots).length;

                                                                     for(var80 = 0; var80 < var86; ++var80) {
                                                                        var13 = var16[var80];
                                                                        if (var63.kitsInventory.getItem(var12, var13) != null && var63.kitsInventory.getItem(var12, var13).equals(var45)) {
                                                                           var63.kitsInventory.removeItem(var12, var13);
                                                                        }
                                                                     }
                                                                  }
                                                               }

                                                               var1.sendMessage(this.kb + "The kit " + ChatColor.GREEN + var38 + ChatColor.GRAY + " has been deleted!");
                                                            } else if (var5.equals("rename")) {
                                                               if (this.config.useMySQL) {
                                                                  var1.sendMessage(this.kb + "This command is coming soon for MySql users!");
                                                                  return true;
                                                               }

                                                               if (var4.length < 4) {
                                                                  var1.sendMessage(this.kb + "Usage: /kb " + ChatColor.GREEN + "Kit" + ChatColor.GRAY + " rename <Kit> <New Name>");
                                                                  return true;
                                                               }

                                                               var37 = var4[2].toLowerCase();
                                                               if (!this.Kits.containsKey(var37)) {
                                                                  var1.sendMessage(this.kb + "Couldn't find a kit with that name!");
                                                                  return true;
                                                               }

                                                               if (this.Kits.containsKey(var4[3].toLowerCase())) {
                                                                  var1.sendMessage(this.kb + "There is already a kit with that name!");
                                                                  return true;
                                                               }

                                                               var40 = (Kit)this.Kits.get(var37);
                                                               var47 = "Kits." + var40.getName() + ".";
                                                               FileConfiguration var56 = this.fileManager.getConfig("kits.yml");
                                                               boolean var66 = var56.getBoolean(var47 + "Enabled");
                                                               boolean var70 = var56.getBoolean(var47 + "Require-Permission");
                                                               String var74 = var56.getString(var47 + "Item");
                                                               var13 = var56.getInt(var47 + "Price");
                                                               String var83 = var56.getString(var47 + "Armor.Helmet");
                                                               String var90 = var56.getString(var47 + "Armor.Chestplate");
                                                               String var96 = var56.getString(var47 + "Armor.Leggings");
                                                               String var17 = var56.getString(var47 + "Armor.Boots");
                                                               List var18 = var56.getStringList(var47 + "Items");
                                                               List var19 = var56.getStringList(var47 + "Potion-Effects");
                                                               List var20 = var56.getStringList(var47 + "Abilities");
                                                               List var21 = var56.getStringList(var47 + "Description");
                                                               var56.set("Kits." + var40.getName(), (Object)null);
                                                               var56.set("Kits." + var4[3] + ".Enabled", var66);
                                                               var56.set("Kits." + var4[3] + ".Require-Permission", var70);
                                                               var56.set("Kits." + var4[3] + ".Item", var74);
                                                               var56.set("Kits." + var4[3] + ".Price", var13);
                                                               var56.set("Kits." + var4[3] + ".Armor.Helmet", var83);
                                                               var56.set("Kits." + var4[3] + ".Armor.Chestplate", var90);
                                                               var56.set("Kits." + var4[3] + ".Armor.Leggings", var96);
                                                               var56.set("Kits." + var4[3] + ".Armor.Boots", var17);
                                                               var56.set("Kits." + var4[3] + ".Items", var18);
                                                               var56.set("Kits." + var4[3] + ".Abilities", var20);
                                                               var56.set("Kits." + var4[3] + ".Potion-Effects", var19);
                                                               var56.set("Kits." + var4[3] + ".Description", var21);
                                                               this.fileManager.saveConfig("kits.yml");
                                                               ItemStack var22 = var40.getLogo().clone();
                                                               ItemStack var23 = var40.getShopLogo().clone();
                                                               this.Kits.remove(var37);
                                                               var40.setName(var4[3]);
                                                               (new ItemStackBuilder(var40.getLogo())).setName(ChatColor.GREEN + var4[3]).build();
                                                               (new ItemStackBuilder(var40.getShopLogo())).setName(ChatColor.LIGHT_PURPLE + var4[3]).build();
                                                               this.Kits.put(var4[3].toLowerCase(), var40);
                                                               ItemStack var24 = var40.getLogo();
                                                               ItemStack var25 = var40.getShopLogo();

                                                               int var28;
                                                               int var29;
                                                               for(int var26 = 0; var26 < this.shop.getSize(); ++var26) {
                                                                  int[] var30;
                                                                  var29 = (var30 = this.smartSlots).length;

                                                                  for(var28 = 0; var28 < var29; ++var28) {
                                                                     int var27 = var30[var28];
                                                                     ItemStack var31 = this.shop.getItem(var26, var27);
                                                                     if (var31 != null && var31.equals(var23)) {
                                                                        this.shop.setItem(var26, var27, var25);
                                                                     }
                                                                  }
                                                               }

                                                               Iterator var108 = this.playerData.values().iterator();

                                                               while(var108.hasNext()) {
                                                                  PlayerData var106 = (PlayerData)var108.next();

                                                                  for(var28 = 0; var28 < var106.kitsInventory.getSize(); ++var28) {
                                                                     int[] var32;
                                                                     int var110 = (var32 = this.smartSlots).length;

                                                                     for(int var109 = 0; var109 < var110; ++var109) {
                                                                        var29 = var32[var109];
                                                                        if (var106.kitsInventory.getItem(var28, var29) != null && var106.kitsInventory.getItem(var28, var29).equals(var22)) {
                                                                           var106.kitsInventory.setItem(var28, var29, var24);
                                                                        }
                                                                     }
                                                                  }
                                                               }

                                                               final String var107 = ChatColor.stripColor(var22.getItemMeta().getDisplayName());
                                                               var1.sendMessage(this.kb + "The kit " + ChatColor.LIGHT_PURPLE + var107 + ChatColor.GRAY + " has been renamed to " + ChatColor.GREEN + var4[3] + ChatColor.GRAY + "!");
                                                               (new BukkitRunnable() {
                                                                  FileConfiguration playersFile;
                                                                  Iterator iterator;
                                                                  int updated;

                                                                  {
                                                                     this.playersFile = Kitbattle.this.fileManager.getConfig("players.yml");
                                                                     this.iterator = this.playersFile.getConfigurationSection("Players").getKeys(false).iterator();
                                                                     this.updated = 0;
                                                                  }

                                                                  public void run() {
                                                                     int var1x = 0;

                                                                     while(var1x < 50 && this.iterator.hasNext()) {
                                                                        ++var1x;
                                                                        String var2 = (String)this.iterator.next();
                                                                        if (this.playersFile.getStringList("Players." + var2 + ".Kits").contains(var107)) {
                                                                           List var3 = this.playersFile.getStringList("Players." + var2 + ".Kits");
                                                                           var3.remove(var107);
                                                                           var3.add(var4[3]);
                                                                           this.playersFile.set("Players." + var2 + ".Kits", var3);
                                                                           ++this.updated;
                                                                        }
                                                                     }

                                                                     if (!this.iterator.hasNext()) {
                                                                        Kitbattle.this.fileManager.saveConfig("players.yml");
                                                                        var1.sendMessage(Kitbattle.this.kb + "Updated the owned kits in the file for " + ChatColor.LIGHT_PURPLE + this.updated + ChatColor.GRAY + " players!");
                                                                        this.cancel();
                                                                     }

                                                                  }
                                                               }).runTaskTimerAsynchronously(this, 1L, 2L);
                                                            } else {
                                                               Kit var39;
                                                               ItemStack var42;
                                                               PlayerData var64;
                                                               int[] var78;
                                                               ItemStack var87;
                                                               int[] var94;
                                                               if (var5.equals("enable")) {
                                                                  if (var4.length < 3) {
                                                                     var1.sendMessage(this.kb + "Usage: /kb " + ChatColor.GREEN + "Kit" + ChatColor.GRAY + " enable <Kit>");
                                                                     return true;
                                                                  }

                                                                  var39 = (Kit)this.Kits.get(var4[2].toLowerCase());
                                                                  if (var39 == null) {
                                                                     var1.sendMessage(this.kb + "Couldn't find a kit with that name!");
                                                                     return true;
                                                                  }

                                                                  if (var39.enabled) {
                                                                     var1.sendMessage(this.kb + "This kit is already enabled!");
                                                                     return true;
                                                                  }

                                                                  var39.enabled = true;
                                                                  this.fileManager.getConfig("kits.yml").set("Kits." + var39.getName() + ".Enabled", true);
                                                                  this.fileManager.saveConfig("kits.yml");
                                                                  var42 = var39.getLogo().clone();
                                                                  var45 = var39.getShopLogo().clone();
                                                                  var39.generateLogos();

                                                                  for(var46 = 0; var46 < this.shop.getSize(); ++var46) {
                                                                     var12 = (var78 = this.smartSlots).length;

                                                                     for(var67 = 0; var67 < var12; ++var67) {
                                                                        var54 = var78[var67];
                                                                        var87 = this.shop.getItem(var46, var54);
                                                                        if (var87 != null && var87.equals(var45)) {
                                                                           this.shop.setItem(var46, var54, var39.getShopLogo());
                                                                        }
                                                                     }
                                                                  }

                                                                  var10 = this.playerData.values().iterator();

                                                                  while(var10.hasNext()) {
                                                                     var64 = (PlayerData)var10.next();

                                                                     for(var67 = 0; var67 < var64.kitsInventory.getSize(); ++var67) {
                                                                        var80 = (var94 = this.smartSlots).length;

                                                                        for(var13 = 0; var13 < var80; ++var13) {
                                                                           var12 = var94[var13];
                                                                           if (var64.kitsInventory.getItem(var67, var12) != null && var64.kitsInventory.getItem(var67, var12).equals(var42)) {
                                                                              var64.kitsInventory.setItem(var67, var12, var39.getLogo());
                                                                           }
                                                                        }
                                                                     }
                                                                  }

                                                                  var1.sendMessage(this.kb + "You have " + ChatColor.GREEN + "enabled" + ChatColor.GRAY + " the kit " + ChatColor.LIGHT_PURPLE + var39.getName());
                                                               } else if (var5.equals("disable")) {
                                                                  if (var4.length < 3) {
                                                                     var1.sendMessage(this.kb + "Usage: /kb " + ChatColor.GREEN + "Kit" + ChatColor.GRAY + " disable <Kit>");
                                                                     return true;
                                                                  }

                                                                  var39 = (Kit)this.Kits.get(var4[2].toLowerCase());
                                                                  if (var39 == null) {
                                                                     var1.sendMessage(this.kb + "Couldn't find a kit with that name!");
                                                                     return true;
                                                                  }

                                                                  if (!var39.enabled) {
                                                                     var1.sendMessage(this.kb + "This kit is already disabled!");
                                                                     return true;
                                                                  }

                                                                  var39.enabled = false;
                                                                  this.fileManager.getConfig("kits.yml").set("Kits." + var39.getName() + ".Enabled", false);
                                                                  this.fileManager.saveConfig("kits.yml");
                                                                  var42 = var39.getLogo().clone();
                                                                  var45 = var39.getShopLogo().clone();
                                                                  var39.generateLogos();

                                                                  for(var46 = 0; var46 < this.shop.getSize(); ++var46) {
                                                                     var12 = (var78 = this.smartSlots).length;

                                                                     for(var67 = 0; var67 < var12; ++var67) {
                                                                        var54 = var78[var67];
                                                                        var87 = this.shop.getItem(var46, var54);
                                                                        if (var87 != null && var87.equals(var45)) {
                                                                           this.shop.setItem(var46, var54, var39.getShopLogo());
                                                                        }
                                                                     }
                                                                  }

                                                                  var10 = this.playerData.values().iterator();

                                                                  while(var10.hasNext()) {
                                                                     var64 = (PlayerData)var10.next();

                                                                     for(var67 = 0; var67 < var64.kitsInventory.getSize(); ++var67) {
                                                                        var80 = (var94 = this.smartSlots).length;

                                                                        for(var13 = 0; var13 < var80; ++var13) {
                                                                           var12 = var94[var13];
                                                                           if (var64.kitsInventory.getItem(var67, var12) != null && var64.kitsInventory.getItem(var67, var12).equals(var42)) {
                                                                              var64.kitsInventory.setItem(var67, var12, var39.getLogo());
                                                                           }
                                                                        }
                                                                     }
                                                                  }

                                                                  var1.sendMessage(this.kb + "You have " + ChatColor.RED + "disabled" + ChatColor.GRAY + " the kit " + ChatColor.LIGHT_PURPLE + var39.getName());
                                                               }
                                                            }
                                                         }
                                                      }
                                                   } else if (var4[0].equalsIgnoreCase("kitunlocker")) {
                                                      if (var1.hasPermission("kitbattle.kitunlocker")) {
                                                         if (var4.length < 4 || !this.isNumber(var4[3]) || !var4[1].equalsIgnoreCase("give")) {
                                                            var1.sendMessage(this.kb + "Usage: /kb " + ChatColor.GREEN + "Kitunlocker" + ChatColor.GRAY + " give <Player> <Amount>");
                                                            return true;
                                                         }

                                                         var33 = Bukkit.getPlayer(var4[2]);
                                                         if (var33 == null) {
                                                            var1.sendMessage(this.kb + "Couldn't find that player!");
                                                            return true;
                                                         }

                                                         var36 = (PlayerData)this.playerData.get(var33.getName());
                                                         var36.kitUnlockers += Integer.valueOf(var4[3]);
                                                         var36.joined = true;
                                                         if (this.players.contains(var33.getName()) && var36.getKit() == null) {
                                                            var33.getInventory().clear();
                                                            this.giveDefaultItems(var33);
                                                         }

                                                         var33.sendMessage(this.kb + this.msgs.PlayerReceiveKitUnlocker.replace("%amount%", var4[3]));
                                                         var1.sendMessage(this.kb + ChatColor.GREEN + var33.getName() + ChatColor.GRAY + " has received " + ChatColor.LIGHT_PURPLE + var4[3] + ChatColor.GRAY + " Kitunlockers successfully");
                                                      } else {
                                                         var1.sendMessage(this.kb + this.msgs.nopermission);
                                                      }
                                                   } else if (var4[0].equalsIgnoreCase("editmode")) {
                                                      if (var1 instanceof Player) {
                                                         var33 = (Player)var1;
                                                         if (this.config.AllowBuilding) {
                                                            var33.sendMessage(this.kb + this.msgs.CommandDisabled);
                                                            return true;
                                                         }

                                                         if (var33.hasPermission("kitbattle.editmode")) {
                                                            if (this.editmode.contains(var33.getName())) {
                                                               this.editmode.remove(var33.getName());
                                                               var33.sendMessage(this.kb + "You can no longer build");
                                                            } else {
                                                               this.editmode.add(var33.getName());
                                                               var33.sendMessage(this.kb + "You can now build");
                                                            }
                                                         } else {
                                                            var33.sendMessage(this.kb + this.msgs.nopermission);
                                                         }
                                                      } else {
                                                         var1.sendMessage(this.msgs.noconsole);
                                                      }
                                                   } else if (var4[0].equalsIgnoreCase("info")) {
                                                      var1.sendMessage("" + ChatColor.DARK_AQUA + ChatColor.STRIKETHROUGH + " ----------" + ChatColor.AQUA + " Information " + ChatColor.DARK_AQUA + ChatColor.STRIKETHROUGH + "----------");
                                                      var1.sendMessage(ChatColor.AQUA + "Version: " + ChatColor.GREEN + this.getDescription().getVersion());
                                                      var1.sendMessage(ChatColor.AQUA + "BungeeMode: " + ChatColor.GREEN + (this.bungeeMode != null));
                                                      var1.sendMessage(ChatColor.AQUA + "Tournaments: " + ChatColor.GREEN + (this.tournamentsManager != null));
                                                      var1.sendMessage(ChatColor.AQUA + "Vault: " + ChatColor.GREEN + (this.econ != null));
                                                      var1.sendMessage(ChatColor.AQUA + "UUID: " + ChatColor.GREEN + this.config.UUID);
                                                      var1.sendMessage(ChatColor.AQUA + "MySQL: " + ChatColor.GREEN + this.config.useMySQL);
                                                      var1.sendMessage(ChatColor.AQUA + "Scoreboard: " + ChatColor.GREEN + this.config.ScoreboardEnabled);
                                                      var1.sendMessage(ChatColor.AQUA + "Updater Running: " + ChatColor.GREEN + (this.Updater != null));
                                                      var35 = this.nextUpdate == 0L ? 0L : (this.nextUpdate - System.currentTimeMillis()) / 1000L;
                                                      var1.sendMessage(ChatColor.AQUA + "Next Update: " + ChatColor.GREEN + var35 + "s " + ChatColor.AQUA + "(" + ChatColor.LIGHT_PURPLE + var35 / 60L + "m" + ChatColor.AQUA + ")");
                                                      var1.sendMessage(ChatColor.AQUA + "Total Updates: " + ChatColor.GREEN + this.totalUpdates);
                                                      var1.sendMessage("" + ChatColor.DARK_AQUA + ChatColor.STRIKETHROUGH + " ---------------------------------");
                                                   }
                                                } else {
                                                   var5 = var4[0].toLowerCase();
                                                   char[] var34 = var5.trim().toCharArray();
                                                   var34[0] = Character.toUpperCase(var34[0]);
                                                   var38 = new String(var34);
                                                   if (!var1.hasPermission("kitbattle." + var5)) {
                                                      var1.sendMessage(this.kb + this.msgs.nopermission);
                                                   } else {
                                                      if (var4.length < 4 || !var4[1].equalsIgnoreCase("add") && !var4[1].equalsIgnoreCase("set") && !var4[1].equalsIgnoreCase("remove") || !this.isNumber(var4[3])) {
                                                         var1.sendMessage(this.kb + "Usage: /kb " + ChatColor.GREEN + var38 + ChatColor.GRAY + " Add/Set/Remove <Player> <Amount>");
                                                         return true;
                                                      }

                                                      Player var43 = Bukkit.getPlayer(var4[2]);
                                                      final String var49 = var4[1].toLowerCase();
                                                      var54 = Integer.valueOf(var4[3]);
                                                      if (var43 != null) {
                                                         PlayerData var58 = (PlayerData)this.playerData.get(var43.getName());
                                                         if (var49.equals("add")) {
                                                            if (var5.equals("coins")) {
                                                               var58.addCoins(var43, var54);
                                                            } else {
                                                               var58.addExp(var43, var54);
                                                            }
                                                         } else if (var49.equals("set")) {
                                                            if (var5.equals("coins")) {
                                                               var58.setCoins(var43, var54);
                                                            } else {
                                                               var58.setExp(var43, var54);
                                                            }
                                                         } else if (var49.equals("remove")) {
                                                            if (var5.equals("coins")) {
                                                               var58.removeCoins(var43, var54);
                                                            } else {
                                                               var58.setExp(var43, var58.getExp() - var54);
                                                            }
                                                         }

                                                         var12 = var5.equals("coins") ? var58.getCoins(var43) : var58.getExp();
                                                         var1.sendMessage(this.kb + "You have modified the player " + ChatColor.AQUA + var43.getName() + ChatColor.GRAY + " " + var5 + " to (" + ChatColor.GREEN + var12 + ChatColor.GRAY + ")");
                                                         var43.sendMessage(this.kb + this.msgs.PlayerStatModificationThroughCommand.replace("%amount%", String.valueOf(var12)).replace("%stat%", var5));
                                                         if (this.players.contains(var43.getName())) {
                                                            var58.createScoreboard(var43);
                                                         }
                                                      } else {
                                                         final String[] var62 = new String[2];
                                                         this.fileManager.executeDatabaseUpdate(var1, var4[2], new BukkitRunnable() {
                                                            public void run() {
                                                               if (var62[1] == null) {
                                                                  try {
                                                                     ResultSet var1x = Kitbattle.this.mysql.getConnection().createStatement().executeQuery("SELECT " + var38 + " FROM " + Kitbattle.this.config.tableprefix + " WHERE player_name = '" + var62[0] + "';");
                                                                     var1x.next();
                                                                     int var2 = var1x.getInt(var38);
                                                                     var1x.close();
                                                                     if (var49.equals("add")) {
                                                                        var2 += var54;
                                                                     } else if (var49.equals("remove")) {
                                                                        var2 -= var54;
                                                                     } else if (var49.equals("set")) {
                                                                        var2 = var54;
                                                                     }

                                                                     Kitbattle.this.mysql.getConnection().prepareStatement("UPDATE " + Kitbattle.this.config.tableprefix + " SET " + var38 + "=" + var2 + " WHERE player_name= '" + var62[0] + "';").executeUpdate();
                                                                     var1.sendMessage(Kitbattle.this.msgs.prefix + "Player " + ChatColor.AQUA + var62[0] + ChatColor.GOLD + var5 + " has been updated!");
                                                                  } catch (SQLException var3) {
                                                                     var3.printStackTrace();
                                                                  }
                                                               } else {
                                                                  FileConfiguration var4 = Kitbattle.this.fileManager.getConfig("players.yml");
                                                                  if (var49.equalsIgnoreCase("add")) {
                                                                     var4.set("Players." + var62[0] + "." + var38, var4.getInt("Players." + var62[0] + "." + var38) + var54);
                                                                  } else if (var49.equalsIgnoreCase("remove")) {
                                                                     var4.set("Players." + var62[0] + "." + var38, var4.getInt("Players." + var62[0] + "." + var38) - var54);
                                                                  } else if (var49.equalsIgnoreCase("set")) {
                                                                     var4.set("Players." + var62[0] + "." + var38, var54);
                                                                  }

                                                                  Kitbattle.this.fileManager.saveConfig("players.yml");
                                                                  var1.sendMessage(Kitbattle.this.msgs.prefix + "Player " + ChatColor.AQUA + var62[1] + ChatColor.GOLD + " " + var5 + " has been updated!");
                                                               }

                                                            }
                                                         }, var62);
                                                      }
                                                   }
                                                }
                                             }
                                          }
                                       } else if ((!var4[0].equalsIgnoreCase("enable") || !var1.hasPermission("kitbattle.enable")) && (!var4[0].equalsIgnoreCase("disable") || !var1.hasPermission("kitbattle.disable"))) {
                                          var1.sendMessage(this.kb + this.msgs.nopermission);
                                       } else {
                                          if (var4.length == 1) {
                                             var1.sendMessage(this.kb + "Usage: /kb " + ChatColor.GREEN + (var4[0].equalsIgnoreCase("enable") ? "Enable" : "Disable") + ChatColor.GRAY + " <Map>");
                                             return true;
                                          }

                                          var5 = var4[1].toLowerCase();
                                          if (!this.playingMaps.containsKey(var5) && !this.tournamentMaps.containsKey(var5) && !this.challengeMaps.containsKey(var5)) {
                                             var1.sendMessage(this.kb + this.msgs.UnknownMap);
                                             return true;
                                          }

                                          var6 = this.playingMaps.containsKey(var5) ? (Map)this.playingMaps.get(var5) : (this.tournamentMaps.containsKey(var5) ? (Map)this.tournamentMaps.get(var5) : (Map)this.challengeMaps.get(var5));
                                          if (var4[0].equalsIgnoreCase("enable") && var6.enabled || var4[0].equalsIgnoreCase("disable") && !var6.enabled) {
                                             var1.sendMessage(this.kb + "The map is already in that state!");
                                             return true;
                                          }

                                          var7 = this.fileManager.getConfig("maps.yml");
                                          var7.set("Maps." + var6.name + ".Enabled", !var6.enabled);
                                          this.fileManager.saveConfig("maps.yml");
                                          var6.enabled = !var6.enabled;
                                          if (var6.enabled) {
                                             if (this.playingMaps.containsKey(var5)) {
                                                if (this.config.bungeeMode) {
                                                   if (this.bungeeMode != null) {
                                                      this.bungeeMode.updateMap();
                                                   } else {
                                                      this.bungeeMode = new BungeeMode(this);
                                                   }
                                                }
                                             } else if (this.tournamentMaps.containsKey(var5)) {
                                                if (this.tournamentsManager == null && var6.isAvailable()) {
                                                   this.tournamentsManager = new TournamentManager(this);
                                                } else if (this.challengeMaps.containsKey(var5) && this.challengesManager == null && ((ChallengeMap)var6).isAvailable()) {
                                                   this.challengesManager = new ChallengesManager(this);
                                                }
                                             }
                                          } else if (this.playingMaps.containsKey(var5)) {
                                             ((PlayingMap)var6).removePlayers();
                                          } else if (this.tournamentMaps.containsKey(var5) && this.tournamentsManager != null && this.tournamentsManager.isRunning() && this.tournamentsManager.map.name.equals(var6.name)) {
                                             this.tournamentsManager.stop();
                                          } else if (this.challengeMaps.containsKey(var5)) {
                                             if (((ChallengeMap)var6).isRunning()) {
                                                ((ChallengeMap)var6).stop();
                                             }

                                             var8 = 0;
                                             var10 = this.challengeMaps.values().iterator();

                                             while(var10.hasNext()) {
                                                var9 = (ChallengeMap)var10.next();
                                                if (var9.isAvailable()) {
                                                   ++var8;
                                                }
                                             }

                                             if (var8 == 0) {
                                                this.challengesManager = null;
                                             } else {
                                                var46 = 0;
                                                var11 = this.challengeMaps.values().iterator();

                                                while(var11.hasNext()) {
                                                   ChallengeMap var51 = (ChallengeMap)var11.next();
                                                   if (var51.playersPerTeam == ((ChallengeMap)var6).playersPerTeam && var51.isAvailable()) {
                                                      ++var46;
                                                   }
                                                }

                                                if (var46 == 0) {
                                                   this.challengesManager.queues.remove(((ChallengeMap)var6).playersPerTeam);
                                                }
                                             }
                                          }

                                          var1.sendMessage(this.kb + "You have updated the map state!");
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
         }
      }

      return true;
   }

   public void setupAll() {
      this.fileManager = new FileManager(this);
      this.fileManager.setupKits();
      this.msgs.loadMessages(this.fileManager.getConfig("messages.yml"));
      this.config.loadConfig();
      this.config.loadAbilities();
      this.config.loadSignPrefixes();
      this.Kits.clear();
      this.fileManager.loadKits();
      this.fileManager.setupRanks();
      this.Ranks.clear();
      this.fileManager.loadRanks();
      this.kb = this.msgs.prefix;
      this.listen.kb = this.kb;
      this.abilityListener.kb = this.kb;
      this.createDefaultAchievements();
      this.achievementsManager = new AchievementsManager(this);
      this.econ = null;
      if (this.getConfig().getBoolean("use-Vault")) {
         this.setupEcon();
         if (this.econ != null) {
            this.logger.info("[KitBattle] Found vault! The option to use vault is enabled, due to that the plugin will use vault instead of coins");
         } else {
            this.logger.info("[KitBattle] The option to use vault is enabled, but Vault doesn't seem to be installed! due to that the plugin will continue using coins system");
         }
      }

      if (this.mysql != null) {
         try {
            this.mysql.getConnection().close();
            this.mysql = null;
         } catch (SQLException var11) {
            var11.printStackTrace();
         }
      }

      if (this.config.useMySQL) {
         try {
            this.mysql = new MySQL(this.config.tableprefix, this.config.mysqlhost, this.config.mysqlport, this.config.mysqldatabase, this.config.mysqlusername, this.config.mysqlpassword);
            this.mysql.setupTable();
         } catch (SQLException var10) {
            var10.printStackTrace();
         }
      }

      this.KitSelector = (new ItemStackBuilder(Material.valueOf(this.getConfig().getString("Items.Kit-Selector").split(" : ")[0].toUpperCase()))).setName(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("Items.Kit-Selector").split(" : ")[1])).build();
      this.ShopOpener = (new ItemStackBuilder(Material.valueOf(this.getConfig().getString("Items.Shop-Opener").split(" : ")[0].toUpperCase()))).setName(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("Items.Shop-Opener").split(" : ")[1])).build();
      this.ChallengesItem = (new ItemStackBuilder(Material.valueOf(this.getConfig().getString("Items.Challenges-Item").split(" : ")[0].toUpperCase()))).setName(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("Items.Challenges-Item").split(" : ")[1])).build();
      this.ProfileItem = (new ItemStackBuilder(Material.valueOf(this.getConfig().getString("Items.Profile-Item").split(" : ")[0].toUpperCase()))).setName(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("Items.Profile-Item").split(" : ")[1])).build();
      this.KitUnlocker = (new ItemStackBuilder(Material.valueOf(this.getConfig().getString("Items.Kit-Unlocker").split(" : ")[0].toUpperCase()))).setName(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("Items.Kit-Unlocker").split(" : ")[1])).build();
      this.TrailsOpener = (new ItemStackBuilder(Material.valueOf(this.getConfig().getString("Items.Trails-Opener").split(" : ")[0].toUpperCase()))).setName(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("Items.Trails-Opener").split(" : ")[1])).build();
      this.profileInventory = Bukkit.createInventory((InventoryHolder)null, 9, (String)this.msgs.inventories.get("Profile-Inventory"));
      this.cageInventory(this.profileInventory, true);
      this.profileInventory.setItem(2, (new ItemStackBuilder(Material.PAPER)).setName(ChatColor.LIGHT_PURPLE + "Stats").build());
      this.profileInventory.setItem(6, (new ItemStackBuilder(Material.ENDER_CHEST)).setName(ChatColor.LIGHT_PURPLE + "Achievements").build());
      this.trailsInventory = null;
      if (this.config.TrailsEnabled) {
         this.trailsInventory = Bukkit.createInventory((InventoryHolder)null, 54, (String)this.msgs.inventories.get("Trails-Inventory"));
         List var1 = this.fileManager.getConfig("trails_blacklist.yml").getStringList("Blacklisted-Trails");
         Effect[] var5;
         int var4 = (var5 = Effect.values()).length;

         for(int var3 = 0; var3 < var4; ++var3) {
            Effect var2 = var5[var3];
            if (!var1.contains(var2.name())) {
               this.trailsInventory.addItem(new ItemStack[]{(new ItemStackBuilder(Material.ENCHANTED_BOOK)).setName(this.colors[this.random.nextInt(this.colors.length)] + var2.name()).build()});
            }
         }

         this.trailsInventory.setItem(this.trailsInventory.getSize() - 1, (new ItemStackBuilder(Material.ARROW)).setName(ChatColor.RED + "NONE").build());
      }

      this.loadMaps();
      this.topSigns = new HashMap();
      final FileConfiguration var12 = this.fileManager.getConfig("signs.yml");
      if (var12.getConfigurationSection("Top") != null && !var12.getConfigurationSection("Top").getKeys(false).isEmpty()) {
         (new BukkitRunnable() {
            public void run() {
               Iterator var2 = var12.getConfigurationSection("Top").getKeys(false).iterator();

               while(var2.hasNext()) {
                  String var1 = (String)var2.next();
                  int var3 = Integer.valueOf(var1);
                  World var4 = Bukkit.getWorld(var12.getString("Top." + var3 + ".world"));
                  int var5 = var12.getInt("Top." + var1 + ".x");
                  int var6 = var12.getInt("Top." + var1 + ".y");
                  int var7 = var12.getInt("Top." + var1 + ".z");
                  Kitbattle.this.topSigns.put(new Location(var4, (double)var5, (double)var6, (double)var7), var3);
               }

               Kitbattle.this.fixUpdater();
            }
         }).runTaskLater(this, 10L);
      }

      this.shop = new SmartInventory(this, (String)this.msgs.inventories.get("Shop"));
      FileConfiguration var13 = this.fileManager.getConfig("shop.yml");
      if (var13.getConfigurationSection("Shops") == null || var13.getConfigurationSection("Shops").getKeys(false).isEmpty()) {
         this.createDefaultShop();
      }

      Iterator var15 = var13.getConfigurationSection("Shops").getKeys(false).iterator();

      while(var15.hasNext()) {
         String var14 = (String)var15.next();
         int var16 = this.shop.addInventory(ChatColor.translateAlternateColorCodes('&', var14));
         Iterator var7 = var13.getConfigurationSection("Shops." + var14).getKeys(false).iterator();

         while(var7.hasNext()) {
            String var6 = (String)var7.next();
            String var8 = var13.getString("Shops." + var14 + "." + var6).toLowerCase();
            ItemStack var9 = this.Kits.containsKey(var8.toLowerCase()) ? ((Kit)this.Kits.get(var8.toLowerCase())).getShopLogo() : null;
            if (var9 != null) {
               this.shop.setItem(var16, Integer.valueOf(var6), var9);
            }
         }

         this.shop.setItem(var16, 49, this.upgrades_itemstack);
         this.shop.setItem(var16, 50, this.yellow_pane_itemstack);
         this.shop.setItem(var16, 48, this.yellow_pane_itemstack);
      }

      if (!this.players.isEmpty() && this.config.scoreboardTitleAnimationEnabled && this.scoreboardTitleAnimationTask == null) {
         this.startTitleAnimation();
      }

      if (this.savingTask != null) {
         this.savingTask.cancel();
      }

      if (this.getConfig().getBoolean("Saving-Task.Enabled")) {
         this.savingTask = (new BukkitRunnable() {
            public void run() {
               final List var1 = Kitbattle.this.getOnlinePlayers();
               (new BukkitRunnable() {
                  int currentPlayer = 0;

                  public void run() {
                     if (this.currentPlayer >= var1.size()) {
                        this.cancel();
                        Bukkit.getConsoleSender().sendMessage(Kitbattle.this.kb + "Players stats have been saved!");
                     } else {
                        Player var1x = (Player)var1.get(this.currentPlayer);
                        if (var1x != null && var1x.isOnline()) {
                           PlayerData var2 = (PlayerData)Kitbattle.this.playerData.get(var1x.getName());
                           var2.saveStatsIntoFile(var1x, true);
                        }

                        ++this.currentPlayer;
                     }
                  }
               }).runTaskTimerAsynchronously(Kitbattle.this.plugin, 0L, 1L);
            }
         }).runTaskTimer(this, (long)(this.getConfig().getInt("Saving-Task.Save-Every-Minutes") * 1200), (long)(this.getConfig().getInt("Saving-Task.Save-Every-Minutes") * 1200));
      } else {
         this.savingTask = null;
      }

      this.checkForUpdates();
   }

   protected void createDefaultShop() {
      FileConfiguration var1 = this.fileManager.getConfig("shop.yml");
      int var2 = 0;
      ArrayList var3 = new ArrayList(this.Kits.values());
      Iterator var5 = this.Kits.keySet().iterator();

      while(true) {
         String var4;
         do {
            if (!var5.hasNext()) {
               for(int var10 = 1; (double)var10 < Math.ceil(Double.valueOf((double)var3.size()) / (double)this.smartSlots.length) + 1.0D; ++var10) {
                  String var11 = "Shops.&cList #" + var10 + ".";
                  int[] var9;
                  int var8 = (var9 = this.smartSlots).length;

                  for(int var7 = 0; var7 < var8; ++var7) {
                     int var6 = var9[var7];
                     if (var2 >= var3.size()) {
                        break;
                     }

                     var1.set(var11 + var6, ((Kit)var3.get(var2)).name);
                     ++var2;
                  }
               }

               this.fileManager.saveConfig("shop.yml");
               return;
            }

            var4 = (String)var5.next();
         } while(!this.config.defaultKits.contains(var4) && ((Kit)this.Kits.get(var4)).original == null);

         var3.remove(this.Kits.get(var4));
      }
   }

   public void loadMaps() {
      this.playingMaps.clear();
      this.tournamentMaps.clear();
      this.challengeMaps.clear();
      final FileConfiguration var1 = this.fileManager.getConfig("maps.yml");
      if (var1.getConfigurationSection("Maps") != null) {
         (new BukkitRunnable() {
            public void run() {
               Iterator var2 = var1.getConfigurationSection("Maps").getKeys(false).iterator();

               while(true) {
                  while(var2.hasNext()) {
                     String var1x = (String)var2.next();
                     String var3 = var1.getString("Maps." + var1x + ".Type").toLowerCase();
                     boolean var4 = var1.getBoolean("Maps." + var1x + ".Enabled");
                     ArrayList var5 = new ArrayList();
                     if (var1.getConfigurationSection("Maps." + var1x + ".Spawnpoints") != null && !var1.getConfigurationSection("Maps." + var1x + ".Spawnpoints").getKeys(false).isEmpty()) {
                        Iterator var7 = var1.getConfigurationSection("Maps." + var1x + ".Spawnpoints").getKeys(false).iterator();

                        while(var7.hasNext()) {
                           String var6 = (String)var7.next();
                           var5.add(Kitbattle.this.getLocationFromString(var1.getString("Maps." + var1x + ".Spawnpoints." + var6)));
                        }
                     }

                     if (var3.equals("tournament")) {
                        Kitbattle.this.tournamentMaps.put(var1x.toLowerCase(), new TournamentMap(Kitbattle.this.plugin, var1x, var5, var4));
                     } else if (var3.equals("challenge")) {
                        Kitbattle.this.challengeMaps.put(var1x.toLowerCase(), new ChallengeMap(Kitbattle.this.plugin, var1x, var5, var1.getInt("Maps." + var1x + ".Players-Per-Team"), true));
                     } else {
                        ArrayList var14 = new ArrayList();
                        if (var1.getConfigurationSection("Maps." + var1x + ".Spawn-Cuboids") != null && !var1.getConfigurationSection("Maps." + var1x + ".Spawn-Cuboids").getKeys(false).isEmpty()) {
                           Iterator var8 = var1.getConfigurationSection("Maps." + var1x + ".Spawn-Cuboids").getKeys(false).iterator();

                           while(var8.hasNext()) {
                              String var16 = (String)var8.next();
                              var14.add(new Cuboid(var1.getString("Maps." + var1x + ".Spawn-Cuboids." + var16)));
                           }
                        }

                        HashMap var17 = new HashMap();
                        if (var1.getConfigurationSection("Maps." + var1x + ".Signs") != null && !var1.getConfigurationSection("Maps." + var1x + ".Signs").getKeys(false).isEmpty()) {
                           Iterator var9 = var1.getConfigurationSection("Maps." + var1x + ".Signs").getKeys(false).iterator();

                           while(var9.hasNext()) {
                              String var19 = (String)var9.next();
                              var17.put(Kitbattle.this.getLocationFromString(var1.getString("Maps." + var1x + ".Signs." + var19)), Integer.valueOf(var19));
                           }
                        }

                        Kitbattle.this.playingMaps.put(var1x.toLowerCase(), new PlayingMap(Kitbattle.this.plugin, var1x, var5, var14, var4, var17));
                     }
                  }

                  if (Kitbattle.this.tournamentsManager != null && Kitbattle.this.tournamentMaps.isEmpty()) {
                     Kitbattle.this.tournamentsManager.stop();
                  } else if (Kitbattle.this.tournamentsManager == null && !Kitbattle.this.tournamentMaps.isEmpty()) {
                     Kitbattle.this.tournamentsManager = new TournamentManager(Kitbattle.this.plugin);
                  }

                  if (Kitbattle.this.challengesManager != null && Kitbattle.this.challengeMaps.isEmpty()) {
                     Kitbattle.this.challengesManager = null;
                  } else if (Kitbattle.this.challengesManager == null && !Kitbattle.this.challengeMaps.isEmpty()) {
                     Kitbattle.this.challengesManager = new ChallengesManager(Kitbattle.this.plugin);
                  }

                  var2 = Kitbattle.this.getPlayers(Kitbattle.this.players).iterator();

                  Player var10;
                  while(var2.hasNext()) {
                     var10 = (Player)var2.next();
                     PlayerData var11 = (PlayerData)Kitbattle.this.playerData.get(var10.getName());
                     if (var11.getMap() != null && (!Kitbattle.this.playingMaps.containsKey(var11.getMap().name.toLowerCase()) || !var11.getMap().isAvailable())) {
                        var11.getMap().removePlayers();
                     }

                     for(int var12 = 0; var12 < var11.kitsInventory.getSize(); ++var12) {
                        int[] var20;
                        int var18 = (var20 = Kitbattle.this.smartSlots).length;

                        for(int var15 = 0; var15 < var18; ++var15) {
                           int var13 = var20[var15];
                           ItemStack var21 = var11.kitsInventory.getItem(var12, var13);
                           if (var21 != null && !Kitbattle.this.Kits.containsKey(ChatColor.stripColor(var21.getItemMeta().getDisplayName().toLowerCase()))) {
                              var11.kitsInventory.removeItem(var12, var13);
                           }
                        }
                     }
                  }

                  if (Kitbattle.this.config.bungeeMode) {
                     if (Kitbattle.this.bungeeMode == null) {
                        Kitbattle.this.bungeeMode = new BungeeMode(Kitbattle.this.plugin);
                        if (Kitbattle.this.bungeeMode.getMap() != null) {
                           var2 = Kitbattle.this.getOnlinePlayers().iterator();

                           while(var2.hasNext()) {
                              var10 = (Player)var2.next();
                              Kitbattle.this.join(var10, Kitbattle.this.bungeeMode.getMap(), 10);
                           }
                        } else {
                           Kitbattle.this.bungeeMode.kickAll();
                        }
                     } else if (Kitbattle.this.playingMaps.containsKey(Kitbattle.this.bungeeMode.getMap().name.toLowerCase()) && ((PlayingMap)Kitbattle.this.playingMaps.get(Kitbattle.this.bungeeMode.getMap().name.toLowerCase())).isAvailable()) {
                        Kitbattle.this.bungeeMode.updateMap();
                     } else {
                        Kitbattle.this.bungeeMode.changeMap();
                     }
                  }

                  return;
               }
            }
         }).runTaskLater(this.plugin, (long)this.config.mapLoadDelay);
      }
   }

   public void clearData(Player var1) {
      var1.getInventory().clear();
      var1.getInventory().setArmorContents((ItemStack[])null);
      var1.setHealth(var1.getMaxHealth());
      var1.setFoodLevel(20);
      var1.setLevel(0);
      var1.setExp(0.0F);
      Iterator var3 = var1.getActivePotionEffects().iterator();

      while(var3.hasNext()) {
         PotionEffect var2 = (PotionEffect)var3.next();
         var1.removePotionEffect(var2.getType());
      }

      var1.setGameMode(GameMode.SURVIVAL);
      var1.setAllowFlight(false);
      var1.setFlying(false);
      var1.setFireTicks(0);
   }

   public void giveDefaultItems(Player var1) {
      var1.getInventory().addItem(new ItemStack[]{this.KitSelector});
      if ((this.tournamentsManager == null || this.tournamentsManager != null && (!this.tournamentsManager.isRunning() || this.tournamentsManager.isRunning() && !this.tournamentsManager.contains(var1))) && (this.challengesManager == null || this.challengesManager != null && !this.challengesManager.players.contains(var1.getName()))) {
         var1.getInventory().addItem(new ItemStack[]{this.ShopOpener});
         if (this.tournamentsManager != null) {
            var1.getInventory().setItem(3, (new ItemStackBuilder(Material.INK_SACK)).setDurability(!this.tournamentsManager.contains(var1) && !this.tournamentsManager.isQueueing(var1) ? 8 : 10).setName(ChatColor.AQUA + "Tournament: " + (!this.tournamentsManager.contains(var1) && !this.tournamentsManager.isQueueing(var1) ? ChatColor.RED + "Disabled" : ChatColor.GREEN + "Enabled")).build());
         }

         if (this.challengesManager != null) {
            var1.getInventory().setItem(4, this.ChallengesItem);
         }

         if (this.trailsInventory != null && var1.hasPermission("kitbattle.trails")) {
            var1.getInventory().setItem(6, this.TrailsOpener);
         }

         var1.getInventory().setItem(7, this.ProfileItem);
         if (((PlayerData)this.playerData.get(var1.getName())).kitUnlockers > 0) {
            ItemStack var2 = this.KitUnlocker.clone();
            var2.setAmount(((PlayerData)this.playerData.get(var1.getName())).kitUnlockers);
            var1.getInventory().setItem(8, var2);
         }
      }

      ((PlayerData)this.playerData.get(var1.getName())).clearCooldowns();
      var1.updateInventory();
   }

   public boolean isNumber(String var1) {
      try {
         Integer.parseInt(var1);
         return true;
      } catch (NumberFormatException var3) {
         return false;
      }
   }

   public boolean isMaterial(String var1) {
      try {
         Material.valueOf(var1);
         return true;
      } catch (IllegalArgumentException var3) {
         return false;
      }
   }

   public void join(final Player var1, PlayingMap var2, int var3) {
      final PlayerData var4 = (PlayerData)this.playerData.get(var1.getName());
      this.players.add(var1.getName());
      if (this.config.AllowBuilding && !this.editmode.contains(var1.getName())) {
         this.editmode.add(var1.getName());
      }

      var4.saveData(var1, var2);
      this.clearData(var1);
      (new BukkitRunnable() {
         public void run() {
            Kitbattle.this.giveDefaultItems(var1);

            try {
               var4.createScoreboard(var1);
            } catch (Exception var2) {
               Kitbattle.this.logger.info("[KitBattle] Couldn't create a scoreboard for the player " + var1.getName() + "! the player stats probably didn't load quickly enough");
               var2.printStackTrace();
            }

         }
      }).runTaskLater(this.plugin, (long)var3);
      var1.teleport(var2.getSpawnpoint());
      if (this.config.scoreboardTitleAnimationEnabled && this.scoreboardTitleAnimationTask == null) {
         this.startTitleAnimation();
      }

   }

   public void resetPlayerToMap(Player var1, Map var2, boolean var3) {
      this.spectating.remove(var1.getName());
      PlayerData var4 = (PlayerData)this.playerData.get(var1.getName());
      if (var3) {
         var4.setMap(var1, (PlayingMap)var2);
      }

      if (!var1.isDead()) {
         this.clearData(var1);
         var1.teleport(var2.getSpawnpoint());
         this.giveDefaultItems(var1);
      }

      var4.setKit(var1, (Kit)null);
      var4.killstreak = 0;
      var4.deathstreak = 0;
      var4.damagers.clear();
      if (var4.customScoreboard != null) {
         var4.customScoreboard.updatePlaceholder(var1, this, "killstreak", 0);
         var4.customScoreboard.updatePlaceholder(var1, this, "deathstreak", 0);
      }

   }

   private void startTitleAnimation() {
      this.scoreboardTitleAnimationTask = (new BukkitRunnable() {
         int index = 0;

         public void run() {
            String var1 = (String)Kitbattle.this.config.scoreboardTitleAnimationFrames.get(this.index);
            if (++this.index >= Kitbattle.this.plugin.config.scoreboardTitleAnimationFrames.size()) {
               this.index = 0;
            }

            List var2 = (List)Kitbattle.this.players.clone();
            Iterator var4 = var2.iterator();

            while(var4.hasNext()) {
               String var3 = (String)var4.next();
               PlayerData var5 = (PlayerData)Kitbattle.this.playerData.get(var3);
               if (var5.customScoreboard != null) {
                  var5.customScoreboard.setName(var1);
               }
            }

         }
      }).runTaskTimer(this, (long)this.config.scoreboardTitleAnimationInterval, (long)this.config.scoreboardTitleAnimationInterval);
   }

   public void leave(Player var1) {
      if (this.tournamentsManager != null) {
         this.tournamentsManager.remove(var1, true);
      }

      if (this.challengesManager != null) {
         if (this.challengesManager.players.contains(var1.getName())) {
            Iterator var3 = this.challengeMaps.values().iterator();

            while(var3.hasNext()) {
               ChallengeMap var2 = (ChallengeMap)var3.next();
               if (var2.players.containsKey(var1.getName())) {
                  var2.remove(var1, true);
                  break;
               }
            }
         } else {
            this.challengesManager.removeFromQueues(var1);
         }
      }

      this.players.remove(var1.getName());
      this.spectating.remove(var1.getName());
      PlayerData var4 = (PlayerData)this.playerData.get(var1.getName());
      var4.killstreak = 0;
      var4.deathstreak = 0;
      var4.restoreData(var1);
      var1.sendMessage(this.kb + this.msgs.PlayerLeave);
      if (this.scoreboardTitleAnimationTask != null && this.players.isEmpty()) {
         this.scoreboardTitleAnimationTask.cancel();
         this.scoreboardTitleAnimationTask = null;
      }

      if (this.bungeeMode != null && this.bungeeMode.playerVotes != null && this.bungeeMode.playerVotes.containsKey(var1.getName())) {
         String var5 = (String)this.bungeeMode.playerVotes.get(var1.getName());
         this.bungeeMode.playerVotes.remove(var1.getName());
         this.bungeeMode.updateVotes(var5);
      }

   }

   private void setupEcon() {
      RegisteredServiceProvider var1 = this.getServer().getServicesManager().getRegistration(Economy.class);
      if (var1 != null) {
         this.econ = (Economy)var1.getProvider();
      }

   }

   public void organizeInventory(Inventory var1) {
      int var2 = var1.getSize() - 1;

      for(int var3 = 0; var3 < var2; ++var3) {
         if (var1.getItem(var3) == null) {
            for(int var4 = var3; var4 < var2; ++var4) {
               var1.setItem(var4, var1.getItem(var4 + 1));
               var1.setItem(var4 + 1, new ItemStack(Material.AIR));
            }
         }
      }

   }

   protected boolean compareItem(ItemStack var1, ItemStack var2) {
      return var1 != null && var2 != null && var1.getType().equals(var2.getType()) && var1.getItemMeta().hasDisplayName() && var2.getItemMeta().hasDisplayName() && var1.getItemMeta().getDisplayName().equals(var2.getItemMeta().getDisplayName());
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

   public boolean isInTournament(Player var1) {
      return this.tournamentsManager != null && this.tournamentsManager.isRunning() && this.tournamentsManager.contains(var1);
   }

   public boolean isInChallenge(Player var1) {
      return this.challengesManager != null && this.challengesManager.players.contains(var1.getName());
   }

   public boolean fixUpdater() {
      FileConfiguration var1 = this.fileManager.getConfig("signs.yml");
      if (var1.getConfigurationSection("Top") != null && !var1.getConfigurationSection("Top").getKeys(false).isEmpty()) {
         if (this.Updater != null) {
            return false;
         } else {
            this.logger.info("[KitBattle] Top signs updater is about to start!");
            this.Updater = (new BukkitRunnable() {
               public void run() {
                  ++Kitbattle.this.totalUpdates;
                  Kitbattle.this.nextUpdate = System.currentTimeMillis() + (long)(Kitbattle.this.config.UpdateTopSignsEveryInMinutes * '\uea60');
                  Kitbattle.this.logger.info("[KitBattle] Updating top signs!");
                  final int var1 = 0;
                  final ArrayList var2 = new ArrayList();
                  Iterator var4 = Kitbattle.this.topSigns.keySet().iterator();

                  while(true) {
                     while(var4.hasNext()) {
                        Location var3 = (Location)var4.next();
                        Sign var5;
                        if (var3.getBlock() != null && var3.getBlock().getState() instanceof Sign && (var5 = (Sign)var3.getBlock().getState()).getLine(0).equals(Kitbattle.this.config.SignsPrefix) && var5.getLine(1).contains("Top #") && Kitbattle.this.isNumber(var5.getLine(1).replace("#", "").split(" ")[1])) {
                           int var6 = Integer.valueOf(var5.getLine(1).replace("#", "").split(" ")[1]);
                           if (var6 > var1) {
                              var1 = var6;
                           }
                        } else {
                           var2.add(var3);
                        }
                     }

                     (new BukkitRunnable() {
                        public void run() {
                           Iterator var2x = var2.iterator();

                           while(var2x.hasNext()) {
                              Location var1x = (Location)var2x.next();
                              Kitbattle.this.topSigns.remove(var1x);
                              Kitbattle.this.logger.info("[KitBattle] There is an invalid top sign at " + Kitbattle.this.getStringFromLocation(var1x, false));
                           }

                           final List var3 = Kitbattle.kitbattleApi.getTopPlayers(var1);
                           (new BukkitRunnable() {
                              public void run() {
                                 Iterator var2x = Kitbattle.this.topSigns.keySet().iterator();

                                 while(var2x.hasNext()) {
                                    Location var1x = (Location)var2x.next();
                                    Sign var3x = (Sign)var1x.getBlock().getState();
                                    int var4 = Integer.valueOf(var3x.getLine(1).replace("#", "").split(" ")[1]);
                                    String var5 = (String)((Entry)var3.get(var4 - 1)).getKey();
                                    var3x.setLine(3, "(" + ((Entry)var3.get(var4 - 1)).getValue() + ")");
                                    var3x.setLine(2, var5);
                                    var3x.update(true);
                                    Kitbattle.this.updateHead(var3x, var5);
                                 }

                              }
                           }).runTask(Kitbattle.this.plugin);
                        }
                     }).runTaskAsynchronously(Kitbattle.this.plugin);
                     return;
                  }
               }
            }).runTaskTimer(this, 0L, (long)(this.config.UpdateTopSignsEveryInMinutes * 1200));
            this.logger.info("[KitBattle] Top signs updater has started!");
            return true;
         }
      } else {
         if (this.Updater != null) {
            this.Updater.cancel();
         }

         this.Updater = null;
         this.nextUpdate = 0L;
         return false;
      }
   }

   private void updateHead(Sign var1, String var2) {
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

   public double getModifier(Player var1) {
      double var2 = 1.0D;
      Iterator var5 = this.plugin.config.modifiers.keySet().iterator();

      while(var5.hasNext()) {
         String var4 = (String)var5.next();
         if (var1.hasPermission(var4) && (Double)this.plugin.config.modifiers.get(var4) > var2) {
            var2 = (Double)this.plugin.config.modifiers.get(var4);
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

   protected void createDefaultAchievements() {
      FileConfiguration var1 = this.fileManager.getConfig("achievements.yml");
      AchievementsManager.AchievementType[] var5;
      int var4 = (var5 = AchievementsManager.AchievementType.values()).length;

      for(int var3 = 0; var3 < var4; ++var3) {
         AchievementsManager.AchievementType var2 = var5[var3];
         if (!var1.contains("Achievements." + var2.name().toLowerCase())) {
            int[] var9;
            int var8 = (var9 = var2.levels).length;

            for(int var7 = 0; var7 < var8; ++var7) {
               int var6 = var9[var7];
               String var10 = "Achievements." + var2.name().toLowerCase() + "." + var6 + ".";
               var1.set(var10 + "description", var2.defaultDescription.replace("%x%", String.valueOf(var6)));
               var1.set(var10 + "prize-description", "Earn " + var2.prizeMultiplier * var6 + " coins!");
               var1.set(var10 + "executed-command", "kb coins add %player% " + var2.prizeMultiplier * var6);
            }
         }
      }

      this.fileManager.saveConfig("achievements.yml");
   }

   public void checkForUpdates() {
      this.availableUpdate = false;
      if (this.getConfig().getBoolean("Check-For-Updates")) {
         (new BukkitRunnable() {
            public void run() {
               try {
                  HttpURLConnection var1 = (HttpURLConnection)(new URL("https://api.spigotmc.org/legacy/update.php?resource=2872")).openConnection();
                  var1.setRequestMethod("GET");
                  String var2 = (new BufferedReader(new InputStreamReader(var1.getInputStream()))).readLine();
                  Kitbattle.this.availableUpdate = !var2.equals(Kitbattle.this.getDescription().getVersion());
                  String var3 = Kitbattle.this.msgs.prefix + (Kitbattle.this.availableUpdate ? "Found a new available version! " + ChatColor.LIGHT_PURPLE + "download at https://goo.gl/acFc6M" : "Looks like you have the latest version installed!");
                  Bukkit.getConsoleSender().sendMessage(var3);
                  Iterator var5 = Kitbattle.this.getOnlinePlayers().iterator();

                  while(var5.hasNext()) {
                     Player var4 = (Player)var5.next();
                     if (var4.hasPermission("skywars.admin")) {
                        var4.sendMessage(var3);
                     }
                  }
               } catch (IOException var6) {
                  Bukkit.getConsoleSender().sendMessage(Kitbattle.this.msgs.prefix + "Couldn't check for an available update");
                  var6.printStackTrace();
               }

            }
         }).runTaskAsynchronously(this);
      }

   }

   private void checkMCVersion() {
      try {
         setDamageMethod = Class.forName("org.bukkit.inventory.meta.Damageable").getMethod("setDamage", Integer.TYPE);
      } catch (Exception var2) {
      }

      Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[KitBattle] The plugin is using the new durability system: " + (setDamageMethod != null));
   }

   public void loadSounds() {
      try {
         this.WITHER_SHOOT = Sound.valueOf("WITHER_SHOOT");
         this.CLICK = Sound.valueOf("CLICK");
         this.WITHER_SPAWN = Sound.valueOf("WITHER_SPAWN");
         this.ANVIL_LAND = Sound.valueOf("ANVIL_LAND");
         this.ITEM_PICKUP = Sound.valueOf("ITEM_PICKUP");
         this.NOTE_PLING = Sound.valueOf("NOTE_PLING");
         this.PISTON_EXTEND = Sound.valueOf("PISTON_EXTEND");
         this.BLAZE_DEATH = Sound.valueOf("BLAZE_DEATH");
         this.ENDERMAN_DEATH = Sound.valueOf("ENDERMAN_DEATH");
         this.ENDERMAN_SCREAM = Sound.valueOf("ENDERMAN_SCREAM");
         this.IRONGOLEM_DEATH = Sound.valueOf("IRONGOLEM_DEATH");
      } catch (IllegalArgumentException var4) {
         try {
            this.WITHER_SHOOT = Sound.valueOf("ENTITY_WITHER_SHOOT");
            this.CLICK = Sound.valueOf("UI_BUTTON_CLICK");
            this.WITHER_SPAWN = Sound.valueOf("ENTITY_WITHER_SPAWN");
            this.ANVIL_LAND = Sound.valueOf("BLOCK_ANVIL_LAND");
            this.ITEM_PICKUP = Sound.valueOf("ENTITY_ITEM_PICKUP");
            this.NOTE_PLING = Sound.valueOf("BLOCK_NOTE_PLING");
            this.PISTON_EXTEND = Sound.valueOf("BLOCK_PISTON_EXTEND");
            this.BLAZE_DEATH = Sound.valueOf("ENTITY_BLAZE_DEATH");
            this.ENDERMAN_DEATH = Sound.valueOf("ENTITY_ENDERMEN_DEATH");
            this.ENDERMAN_SCREAM = Sound.valueOf("ENTITY_ENDERMEN_SCREAM");
            this.IRONGOLEM_DEATH = Sound.valueOf("ENTITY_IRONGOLEM_DEATH");
         } catch (IllegalArgumentException var3) {
            this.WITHER_SHOOT = Sound.valueOf("ENTITY_WITHER_SHOOT");
            this.CLICK = Sound.valueOf("UI_BUTTON_CLICK");
            this.WITHER_SPAWN = Sound.valueOf("ENTITY_WITHER_SPAWN");
            this.ANVIL_LAND = Sound.valueOf("BLOCK_ANVIL_LAND");
            this.ITEM_PICKUP = Sound.valueOf("ENTITY_ITEM_PICKUP");
            this.NOTE_PLING = Sound.valueOf("BLOCK_NOTE_BLOCK_PLING");
            this.PISTON_EXTEND = Sound.valueOf("BLOCK_PISTON_EXTEND");
            this.BLAZE_DEATH = Sound.valueOf("ENTITY_BLAZE_DEATH");
            this.ENDERMAN_DEATH = Sound.valueOf("ENTITY_ENDERMAN_DEATH");
            this.ENDERMAN_SCREAM = Sound.valueOf("ENTITY_ENDERMAN_SCREAM");
            this.IRONGOLEM_DEATH = Sound.valueOf("ENTITY_IRON_GOLEM_DEATH");
         }
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

   public List getOnlinePlayers() {
      ArrayList var1 = Lists.newArrayList();
      Iterator var3 = Bukkit.getWorlds().iterator();

      while(var3.hasNext()) {
         World var2 = (World)var3.next();
         var1.addAll(var2.getPlayers());
      }

      return Collections.unmodifiableList(var1);
   }

   public String getPlaceholderValue(Player var1, PlayerData var2, String var3) {
      if (var3.equals("coins")) {
         return String.valueOf(var2.getCoins(var1));
      } else if (var3.equals("kills")) {
         return String.valueOf(var2.getKills());
      } else if (var3.equals("killstreak")) {
         return String.valueOf(var2.getKillstreak());
      } else if (var3.equals("deathstreak")) {
         return String.valueOf(var2.getDeathstreak());
      } else if (var3.equals("deaths")) {
         return String.valueOf(var2.getDeaths());
      } else if (var3.equals("player_exp")) {
         return String.valueOf(var2.getExp());
      } else if (var3.equals("player_rank")) {
         return String.valueOf(var2.getRank().getName());
      } else if (var3.equals("player_next_rank")) {
         return String.valueOf(var2.getNextRank() != null ? var2.getNextRank().getName() : "None");
      } else if (var3.equals("player_next_rank_exp")) {
         return String.valueOf(var2.getNextRank() != null ? var2.getNextRank().getRequiredExp() : "0");
      } else if (var3.equals("player_next_rank_exp_difference")) {
         return String.valueOf(var2.getNextRank() != null ? var2.getNextRank().getRequiredExp() - var2.getExp() : "0");
      } else if (var3.equals("map")) {
         return String.valueOf(var2.getMap() != null ? var2.getMap().name : "None");
      } else {
         return var3.equals("selected_kit") ? String.valueOf(var2.getKit() != null ? var2.getKit().getName() : "None") : null;
      }
   }
}
