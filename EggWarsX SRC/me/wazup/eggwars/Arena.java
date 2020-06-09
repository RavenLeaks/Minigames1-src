package me.wazup.eggwars;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.puharesource.mc.titlemanager.api.TitleObject;
import io.puharesource.mc.titlemanager.api.TitleObject.TitleType;
import java.io.File;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import me.wazup.eggwars.events.EWArenaStartEvent;
import me.wazup.eggwars.events.EWArenaStopEvent;
import me.wazup.eggwars.events.EWPlayerJoinArenaEvent;
import me.wazup.eggwars.events.EWPlayerLeaveArenaEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

class Arena {
   private Eggwars plugin;
   String name;
   boolean enabled;
   Cuboid cuboid;
   Enums.ArenaState state;
   int teamSize;
   int minTeams;
   int lobbyCountdown;
   int minPlayersForCountdownShorten;
   int countdownShortenTo;
   int gameLength;
   HashMap events;
   List potionEffects;
   Location lobby;
   Location spectatorsLocation;
   HashMap teams;
   ArrayList spawners;
   ArrayList villagers;
   private static Vector[] villagersBox = new Vector[]{new Vector(1, -1, 0), new Vector(-1, -1, 0), new Vector(0, -1, 1), new Vector(0, -1, -1), new Vector(0, 1, 0)};
   HashMap players;
   HashMap killers;
   ArrayList spectators;
   ArrayList bannedPlayers;
   String votedShop;
   VotesManager votesManager;
   Inventory editor;
   Inventory teamSelector;
   int id;
   int slot;
   CustomScoreboard scoreboard;
   private String date;
   private BukkitTask[] tasks;
   ArrayList openedHolders;
   public HashMap signs;
   private HashMap blocks;

   public Arena(Eggwars var1, String var2) {
      this.plugin = var1;
      this.name = var2;

      try {
         File var3 = new File(var1.getDataFolder() + "/arenas/" + var2, "settings.yml");
         YamlConfiguration var4 = YamlConfiguration.loadConfiguration(var3);
         var4.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(var1.getResource("arena-settings.yml"))));
         var4.options().copyDefaults(true);
         var4.save(var3);
         this.enabled = var4.getBoolean("enabled");
         this.teamSize = var4.getInt("team-size");
         this.minTeams = var4.getInt("min-teams");
         if (this.minTeams < 2) {
            this.minTeams = 2;
         }

         this.lobbyCountdown = var4.getInt("lobby-countdown");
         this.minPlayersForCountdownShorten = var4.getInt("lobby-countdown-shortening.min-teams") * this.teamSize;
         this.countdownShortenTo = var4.getInt("lobby-countdown-shortening.shorten-to");
         this.gameLength = var4.getInt("game-length");
         this.events = new HashMap();
         this.potionEffects = new ArrayList();
         this.events.put(var4.getInt("eggs-auto-destroy-time"), "Deathmatch");
         Iterator var6 = var4.getIntegerList("generators-upgrade").iterator();

         while(var6.hasNext()) {
            int var5 = (Integer)var6.next();
            this.events.put(var5, "Upgrades");
         }

         var6 = var4.getStringList("effects").iterator();

         while(var6.hasNext()) {
            String var17 = (String)var6.next();
            String[] var7 = var17.split(" : ");
            if (var7.length == 3) {
               this.potionEffects.add(new PotionEffect(PotionEffectType.getByName(var7[0]), Integer.valueOf(var7[1]) * 20, Integer.valueOf(var7[2]) - 1));
            }
         }

         this.players = new HashMap();
         this.killers = new HashMap();
         this.tasks = new BukkitTask[4];
         this.spectators = new ArrayList();
         this.bannedPlayers = new ArrayList();
         this.openedHolders = new ArrayList();
         this.votesManager = new VotesManager(var1);
         this.state = this.enabled ? Enums.ArenaState.WAITING : Enums.ArenaState.DISABLED;
         this.date = ChatColor.GRAY + (new SimpleDateFormat("dd/MM/yyyy")).format(new Date());
         File var18 = new File(var1.getDataFolder() + "/arenas/" + var2, "locations.dat");
         YamlConfiguration var19 = YamlConfiguration.loadConfiguration(var18);
         this.cuboid = new Cuboid(var19.getString("Cuboid"));
         World var20 = Bukkit.getWorld(this.cuboid.worldName);
         if (var20 == null) {
            Bukkit.getConsoleSender().sendMessage("The arena " + var2 + " world seems to be unloaded! attempting to import it");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ew worldmanager import " + this.cuboid.worldName);
            var20 = Bukkit.getWorld(this.cuboid.worldName);
         }

         this.lobby = var19.contains("Lobby") ? var1.getLocationFromString(var19.getString("Lobby")) : null;
         this.spectatorsLocation = var19.contains("Spectators-Spawnpoint") ? var1.getLocationFromString(var19.getString("Spectators-Spawnpoint")) : null;
         this.scoreboard = new CustomScoreboard(var1, true, var1.customization.scoreboard_title, new String[]{var1.customization.scoreboard_header, (String)var1.customization.scoreboard.get("Time") + ChatColor.WHITE + " " + this.lobbyCountdown, " ", (String)var1.customization.scoreboard.get("Players") + ChatColor.WHITE + " 0", (String)var1.customization.scoreboard.get("Map") + ChatColor.WHITE + " " + var2, (String)var1.customization.scoreboard.get("Mode") + ChatColor.WHITE + " " + this.getMode(), " ", this.date, var1.customization.scoreboard_footer});
         this.teams = new HashMap();
         String var8;
         Iterator var9;
         if (var19.getConfigurationSection("Teams") != null && !var19.getConfigurationSection("Teams").getKeys(false).isEmpty()) {
            var9 = var19.getConfigurationSection("Teams").getKeys(false).iterator();

            while(var9.hasNext()) {
               var8 = (String)var9.next();
               if (var19.contains("Teams." + var8 + ".Spawnpoint") && var19.contains("Teams." + var8 + ".Egg-Location")) {
                  this.registerTeam(var8.toUpperCase(), var1.getLocationFromString(var19.getString("Teams." + var8 + ".Spawnpoint")), var1.getLocationFromString(var19.getString("Teams." + var8 + ".Egg-Location")));
               }
            }
         }

         this.spawners = new ArrayList();
         if (var19.getConfigurationSection("Spawners") != null && !var19.getConfigurationSection("Spawners").getKeys(false).isEmpty()) {
            var9 = var19.getConfigurationSection("Spawners").getKeys(false).iterator();

            label130:
            while(true) {
               do {
                  do {
                     if (!var9.hasNext()) {
                        break label130;
                     }

                     var8 = (String)var9.next();
                  } while(var19.getConfigurationSection("Spawners." + var8) == null);
               } while(var19.getConfigurationSection("Spawners." + var8).getKeys(false).isEmpty());

               Iterator var11 = var19.getConfigurationSection("Spawners." + var8).getKeys(false).iterator();

               while(var11.hasNext()) {
                  String var10 = (String)var11.next();
                  String var12 = var19.getString("Spawners." + var8 + "." + var10);
                  Location var13 = var1.getLocationFromString(var12.split(" : ")[2]);
                  int var14 = Integer.valueOf(var12.split(" : ")[0]);
                  var14 = var14 % 10 == 0 && var14 >= 10 ? var14 : 10;
                  boolean var15 = Boolean.valueOf(var12.split(" : ")[1]);
                  this.spawners.add(new Spawner(var1, var13, Material.getMaterial(var8.toUpperCase()), var14, var15));
               }
            }
         }

         this.villagers = new ArrayList();
         if (var19.getConfigurationSection("Villagers") != null && !var19.getConfigurationSection("Villagers").getKeys(false).isEmpty()) {
            var9 = var19.getConfigurationSection("Villagers").getKeys(false).iterator();

            while(var9.hasNext()) {
               var8 = (String)var9.next();
               this.villagers.add(var1.getLocationFromString(var19.getString("Villagers." + var8)));
            }
         }

         this.signs = new HashMap();
         int var22;
         if (var19.getConfigurationSection("Signs") != null && !var19.getConfigurationSection("Signs").getKeys(false).isEmpty()) {
            var9 = var19.getConfigurationSection("Signs").getKeys(false).iterator();

            while(var9.hasNext()) {
               var8 = (String)var9.next();
               var22 = Integer.valueOf(var8);
               Location var24 = var1.getLocationFromString(var19.getString("Signs." + var8));
               this.signs.put(var24, var22);
            }
         }

         this.blocks = new HashMap();
         String[] var21 = YamlConfiguration.loadConfiguration(new File(var1.getDataFolder() + "/arenas/" + var2, "blocks.dat")).getString("Blocks").replace("[", "").replace("]", "").split(", ");
         String[] var26 = var21;
         int var25 = var21.length;

         for(var22 = 0; var22 < var25; ++var22) {
            String var23 = var26[var22];
            String[] var27 = var23.split(":");
            this.blocks.put(new Arena.CLocation(Integer.valueOf(var27[0]), Integer.valueOf(var27[1]), Integer.valueOf(var27[2]), (Arena.CLocation)null), new Arena.CBlock(Integer.valueOf(var27[3]), var27.length > 4 ? Byte.valueOf(var27[4]) : 0, (Arena.CBlock)null));
         }

         this.createEditor();
         this.createTeamSelector();
         var1.arenas.put(var2.toLowerCase(), this);
      } catch (Exception var16) {
         Bukkit.getConsoleSender().sendMessage(var1.customization.prefix + "Couldn't load the arena " + var2 + ", please check for missing arena files or incorrect settings!");
         var16.printStackTrace();
      }

   }

   public void updateItem(int var1) {
      int var2 = this.players.size() - this.spectators.size();
      int var3 = this.teamSize * this.teams.size();
      int var4 = this.state.equals(Enums.ArenaState.WAITING) ? 5 : (this.state.equals(Enums.ArenaState.STARTING) ? 4 : 14);
      this.plugin.arenaSelector.setItem(this.id, this.slot, (new ItemStackBuilder(Material.STAINED_CLAY)).setName(ChatColor.GREEN + this.name).addLore((String)this.plugin.customization.lores.get("Lines"), ((String)this.plugin.customization.lores.get("Players")).replace("%players%", String.valueOf(var2)).replace("%maxplayers%", String.valueOf(var3)), ((String)this.plugin.customization.lores.get("Type")).replace("%type%", this.getMode()), ((String)this.plugin.customization.lores.get("State")).replace("%state%", this.state + (var1 != 0 ? ChatColor.LIGHT_PURPLE + " - " + this.plugin.getPercentageString(var1) : "")), (String)this.plugin.customization.lores.get("Lines")).setDurability(var4).build());
      Iterator var6 = this.signs.keySet().iterator();

      while(true) {
         Location var5;
         do {
            if (!var6.hasNext()) {
               return;
            }

            var5 = (Location)var6.next();
         } while(!var5.getBlock().getType().equals(Material.WALL_SIGN) && !var5.getBlock().getType().equals(Material.SIGN_POST));

         Sign var7 = (Sign)var5.getBlock().getState();
         if (!this.state.equals(Enums.ArenaState.ROLLBACKING)) {
            var7.setLine(3, ChatColor.AQUA + String.valueOf(var2) + ChatColor.YELLOW + "/" + ChatColor.AQUA + var3);
         } else if (var1 > 0) {
            var7.setLine(3, this.plugin.getPercentageString(var1));
         }

         var7.update(true);

         try {
            var7.getBlock().getRelative(((org.bukkit.material.Sign)var7.getData()).getAttachedFace()).setTypeIdAndData(this.plugin.config.blockBehindSigns, (byte)var4, false);
         } catch (Exception var9) {
         }
      }
   }

   public String getMode() {
      return ChatColor.LIGHT_PURPLE + (this.teamSize == 1 ? "Solo" : "Teams (" + this.teamSize + ")");
   }

   public void createEditor() {
      if (this.editor == null) {
         this.editor = Bukkit.createInventory((InventoryHolder)null, 54, ChatColor.DARK_RED + "Editing: " + ChatColor.BLUE + this.name);
      } else {
         this.editor.clear();
      }

      this.plugin.cageInventory(this.editor, true);

      int var1;
      for(var1 = 0; var1 < 4; ++var1) {
         this.editor.setItem(var1, this.plugin.plus_itemstack);
      }

      for(var1 = 18; var1 < 22; ++var1) {
         this.editor.setItem(var1, this.plugin.minus_itemstack);
      }

      ItemStackBuilder var6 = new ItemStackBuilder(Material.PAPER);
      this.editor.setItem(9, var6.setName(ChatColor.YELLOW + "Team size: " + ChatColor.GOLD + this.teamSize).build());
      this.editor.setItem(10, var6.setName(ChatColor.YELLOW + "Min teams: " + ChatColor.GOLD + this.minTeams).build());
      this.editor.setItem(11, var6.setName(ChatColor.YELLOW + "Lobby countdown: " + ChatColor.GOLD + this.lobbyCountdown).build());
      this.editor.setItem(12, var6.setName(ChatColor.YELLOW + "Game length: " + ChatColor.GOLD + this.gameLength).build());
      String[] var2 = this.cuboid.toString().split(", ");
      String var3 = ChatColor.AQUA + "- " + ChatColor.YELLOW;
      int var4 = (int)(this.plugin.filesManager.getSize(new File(this.plugin.getDataFolder() + "/arenas", this.name)) / 1000L);
      ItemStack var5 = (new ItemStackBuilder(Material.CHEST)).setName(ChatColor.YELLOW + "Information").addLore(var3 + "World: " + ChatColor.GOLD + var2[0], var3 + "Bottom corner: " + ChatColor.GOLD + var2[1] + ", " + var2[2] + ", " + var2[3], var3 + "Top corner: " + ChatColor.GOLD + var2[4] + ", " + var2[5] + ", " + var2[6], var3 + "Blocks: " + ChatColor.GOLD + this.cuboid.getSize(), var3 + "Files size: " + ChatColor.GOLD + var4 + " kb", var3 + "Size type: " + (var4 < 501 ? ChatColor.GREEN + "Small" : (var4 < 1001 ? ChatColor.YELLOW + "Medium" : ChatColor.RED + "Large"))).build();
      this.editor.setItem(37, var5);
      this.editor.setItem(40, (new ItemStackBuilder(Material.INK_SACK)).setName(this.enabled ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled").setDurability(this.enabled ? 10 : 8).build());
      this.editor.setItem(43, this.plugin.save_itemstack);
   }

   public void createTeamSelector() {
      this.teamSelector = Bukkit.createInventory((InventoryHolder)null, this.plugin.getInventorySize(this.teams.size()), (String)this.plugin.customization.inventories.get("Team-Selector"));
      this.plugin.cageInventory(this.teamSelector, true);
      int var1 = 0;

      for(Iterator var3 = this.teams.keySet().iterator(); var3.hasNext(); ++var1) {
         Team var2 = (Team)var3.next();
         ItemStackBuilder var4 = (new ItemStackBuilder(Material.WOOL)).setDurability(this.plugin.filesManager.translateColorToDurability(var2.getName())).setName(ChatColor.valueOf(var2.getName()) + var2.getName() + " Team").addLore(ChatColor.GRAY + " - Players: " + ChatColor.WHITE + var2.getSize() + "/" + this.teamSize, " ");
         Iterator var6 = var2.getPlayers().iterator();

         while(var6.hasNext()) {
            OfflinePlayer var5 = (OfflinePlayer)var6.next();
            if (var5 instanceof Player) {
               var4.addLore(ChatColor.GRAY + " - " + ChatColor.valueOf(var2.getName()) + var5.getName());
            }
         }

         ((Arena.TeamData)this.teams.get(var2)).teamSelectorSlot = var1;
         this.teamSelector.setItem(var1, var4.build());
      }

   }

   public void updateTeamSelector(Team var1) {
      ItemStackBuilder var2 = (new ItemStackBuilder(this.teamSelector.getItem(((Arena.TeamData)this.teams.get(var1)).teamSelectorSlot))).clearLore().addLore(ChatColor.GRAY + " - Players: " + ChatColor.WHITE + var1.getSize() + "/" + this.teamSize, " ");
      Iterator var4 = var1.getPlayers().iterator();

      while(var4.hasNext()) {
         OfflinePlayer var3 = (OfflinePlayer)var4.next();
         if (var3 instanceof Player) {
            var2.addLore(ChatColor.GRAY + " - " + ChatColor.valueOf(var1.getName()) + var3.getName());
         }
      }

      var2.build();
   }

   public void cancelTask(int var1) {
      if (this.tasks[var1] != null) {
         this.tasks[var1].cancel();
      }

      this.tasks[var1] = null;
   }

   public Team getAvailableTeam(int var1) {
      Iterator var3 = this.teams.keySet().iterator();

      while(var3.hasNext()) {
         Team var2 = (Team)var3.next();
         if (var2.getSize() + var1 <= this.teamSize) {
            return var2;
         }
      }

      return null;
   }

   public Team registerTeam(String var1, Location var2, Location var3) {
      Team var4 = this.scoreboard.registerTeam(var1);
      var4.setPrefix(ChatColor.valueOf(var1).toString());
      var4.setAllowFriendlyFire(false);
      this.teams.put(var4, new Arena.TeamData(var1, var2, var3));
      return var4;
   }

   public Team updateTeam(String var1, Location var2, Location var3) {
      Team var4 = this.getTeam(var1);
      this.teams.put(var4, new Arena.TeamData(var1, var2, var3));
      return var4;
   }

   public Team getTeam(String var1) {
      Iterator var3 = this.teams.keySet().iterator();

      while(var3.hasNext()) {
         Team var2 = (Team)var3.next();
         if (var2.getName().equalsIgnoreCase(var1)) {
            return var2;
         }
      }

      return null;
   }

   public List getPlayers() {
      return this.plugin.getPlayers(this.players.keySet());
   }

   public List getTopKillers() {
      while(this.killers.size() < 3) {
         this.killers.put("NONE" + (this.plugin.r.nextInt(5) + 1), new ArrayList());
      }

      LinkedList var1 = new LinkedList(this.killers.entrySet());
      Collections.sort(var1, new Comparator() {
         public int compare(Entry var1, Entry var2) {
            return ((List)var2.getValue()).size() - ((List)var1.getValue()).size();
         }
      });
      return var1;
   }

   public int getHighest(int var1, Collection var2) {
      int var3 = 0;
      Iterator var5 = var2.iterator();

      while(var5.hasNext()) {
         int var4 = (Integer)var5.next();
         if (var4 > var3 && var4 < var1) {
            var3 = var4;
         }
      }

      return var3;
   }

   public String getEvent(int var1) {
      if (var1 == 0) {
         return (String)this.plugin.customization.scoreboard.get("Ending");
      } else if (!this.events.containsKey(var1)) {
         return ChatColor.YELLOW + "NONE";
      } else if (((String)this.events.get(var1)).equals("Deathmatch")) {
         return (String)this.plugin.customization.scoreboard.get("Deathmatch");
      } else {
         return ((String)this.events.get(var1)).equals("Upgrades") ? ((String)this.plugin.customization.scoreboard.get("Upgrades")).replace("%randomcolor%", this.plugin.colors[this.plugin.r.nextInt(this.plugin.colors.length)].toString()) : ChatColor.YELLOW + "NONE";
      }
   }

   public List getAliveTeams() {
      ArrayList var1 = new ArrayList();
      Iterator var3 = this.teams.keySet().iterator();

      while(var3.hasNext()) {
         Team var2 = (Team)var3.next();
         if (var2.getSize() > 0) {
            var1.add(var2);
         }
      }

      return var1;
   }

   public void createScoreboardTeams() {
      byte var1 = 4;
      int var2 = 0;
      List var3 = this.getAliveTeams();
      this.scoreboard.registerLine(4, " ");
      Iterator var5 = var3.iterator();

      while(var5.hasNext()) {
         Team var4 = (Team)var5.next();
         ++var2;
         if (var2 > var1) {
            this.scoreboard.registerLine(4, (String)this.plugin.customization.scoreboard.get("Other-Teams") + " (" + (var3.size() - var1) + ")");
            return;
         }

         this.scoreboard.registerLine(4, (((Arena.TeamData)this.teams.get(var4)).egg_alive ? ChatColor.GREEN + Enums.SPECIAL_CHARACTER.CHECK_MARK.toString() : ChatColor.RED + Enums.SPECIAL_CHARACTER.XMARK.toString()) + " " + ChatColor.valueOf(var4.getName()) + var4.getName() + " (" + var4.getSize() + ")");
      }

   }

   public void deleteScoreboardTeams() {
      while(this.scoreboard.getCurrentSize() > this.scoreboard.getOriginalSize()) {
         this.scoreboard.deleteLine(4);
      }

   }

   public void sendWarning(Player var1) {
      PlayerData var2 = (PlayerData)this.plugin.playerData.get(var1.getName());
      ++var2.warnings;
      var1.teleport(this.cuboid.getRandomLocation());
      var1.sendMessage(((String)this.plugin.customization.messages.get("Warning-Receive")).replace("%warnings%", String.valueOf(var2.warnings)).replace("%maxwarnings%", String.valueOf(this.plugin.config.maxWarnings)));
      if (var2.warnings >= this.plugin.config.maxWarnings) {
         var1.sendMessage((String)this.plugin.customization.messages.get("Warning-Exceed"));
         this.bannedPlayers.add(var1.getName());
         this.leave(var1);
      }

   }

   public Inventory getTeleporterInventory() {
      Inventory var1 = Bukkit.createInventory((InventoryHolder)null, 54, (String)this.plugin.customization.inventories.get("Teleporter"));
      Iterator var3 = this.getPlayers().iterator();

      while(var3.hasNext()) {
         Player var2 = (Player)var3.next();
         if (!this.spectators.contains(var2.getName())) {
            var1.addItem(new ItemStack[]{this.plugin.getSkull(var2.getName(), ChatColor.AQUA + var2.getName())});
         }
      }

      return var1;
   }

   public List getEnemeies(Team var1) {
      ArrayList var2 = new ArrayList();
      if (this.scoreboard.getTeams().contains(var1)) {
         Iterator var4 = this.getPlayers().iterator();

         while(var4.hasNext()) {
            Player var3 = (Player)var4.next();
            if (!this.spectators.contains(var3.getName()) && !var1.hasPlayer(var3)) {
               var2.add(var3);
            }
         }
      }

      return var2;
   }

   public void destroyCore(Team var1, boolean var2) {
      final Arena.TeamData var3 = (Arena.TeamData)this.teams.get(var1);
      var3.egg_alive = false;
      this.scoreboard.update(var1.getName(), ChatColor.RED + Enums.SPECIAL_CHARACTER.XMARK.toString() + " " + ChatColor.valueOf(var1.getName()) + var1.getName() + " (" + var1.getSize() + ")", false);
      Iterator var5 = this.plugin.config.executed_commands_egg_destroy.iterator();

      String var4;
      while(var5.hasNext()) {
         var4 = (String)var5.next();
         Bukkit.dispatchCommand(Bukkit.getConsoleSender(), var4.replace("%team%", var1.getName()));
      }

      if (var2) {
         var4 = ((String)this.plugin.customization.messages.get("Egg-Break-Global-Notification")).replace("%team%", ChatColor.valueOf(var1.getName()) + var1.getName());
         TitleObject var8 = this.plugin.config.titles_enabled ? (new TitleObject((String)this.plugin.customization.titles.get("Egg-Break"), TitleType.TITLE)).setFadeIn(this.plugin.config.titles_fadeIn).setStay(this.plugin.config.titles_stay).setFadeOut(this.plugin.config.titles_fadeOut) : null;
         Iterator var7 = this.getPlayers().iterator();

         while(var7.hasNext()) {
            Player var6 = (Player)var7.next();
            var6.sendMessage(var4);
            var6.playSound(var6.getLocation(), this.plugin.ENDERDRAGON_GROWL, 1.0F, 1.0F);
         }

         var7 = var1.getPlayers().iterator();

         while(var7.hasNext()) {
            OfflinePlayer var9 = (OfflinePlayer)var7.next();
            if (var9 instanceof Player) {
               ((Player)var9).sendMessage((String)this.plugin.customization.messages.get("Egg-Break-Team-Notification"));
               if (var8 != null) {
                  var8.send((Player)var9);
               }
            }
         }
      }

      var3.egg.getBlock().setType(Material.AIR);
      (new BukkitRunnable() {
         public void run() {
            List var1 = var3.egg.getWorld().spawnEntity(var3.egg, EntityType.SNOWBALL).getNearbyEntities(1.5D, 1.0D, 1.5D);
            Iterator var3x = var1.iterator();

            while(var3x.hasNext()) {
               Entity var2 = (Entity)var3x.next();
               if (var2.getType().equals(EntityType.DROPPED_ITEM)) {
                  var2.remove();
               }
            }

         }
      }).runTaskLater(this.plugin, 2L);
   }

   public void kill(final Player var1) {
      Team var2 = (Team)this.players.get(var1.getName());
      Arena.TeamData var3 = (Arena.TeamData)this.teams.get(var2);
      if (var3.egg_alive) {
         if (var3.egg.getBlock().getType().equals(this.plugin.config.coreBlockType)) {
            Location var10 = var3.spawnpoint.clone();
            Location var11 = var10.clone().add(0.0D, 1.0D, 0.0D);

            while(!var10.getBlock().getType().equals(Material.AIR) || !var11.getBlock().getType().equals(Material.AIR)) {
               var10.add(0.0D, 1.0D, 0.0D);
               var11.add(0.0D, 1.0D, 0.0D);
            }

            var1.teleport(var10);
            (new BukkitRunnable() {
               public void run() {
                  if (Arena.this.players.containsKey(var1.getName()) && Arena.this.plugin.protectedPlayers.contains(var1.getName()) && !Arena.this.spectators.contains(var1.getName())) {
                     Arena.this.plugin.protectedPlayers.remove(var1.getName());
                  }

               }
            }).runTaskLater(this.plugin, (long)(this.plugin.config.deathProtectionInSeconds * 20));
            return;
         }

         this.destroyCore(var2, false);
      }

      this.spectators.add(var1.getName());
      var1.teleport(this.spectatorsLocation != null ? this.spectatorsLocation : this.cuboid.getRandomLocation());
      TitleObject var4 = this.plugin.config.titles_enabled ? (new TitleObject((String)this.plugin.customization.titles.get("Player-Spectate"), TitleType.TITLE)).setFadeIn(this.plugin.config.titles_fadeIn).setStay(this.plugin.config.titles_stay).setFadeOut(this.plugin.config.titles_fadeOut) : null;
      if (var4 != null) {
         var4.send(var1);
      }

      this.scoreboard.update((String)this.plugin.customization.scoreboard.get("Players"), (String)this.plugin.customization.scoreboard.get("Players") + " " + ChatColor.WHITE + (this.players.size() - this.spectators.size()), false);
      String var5 = ((String)this.plugin.customization.messages.get("Arena-Player-Eliminate")).replace("%player%", var1.getName());
      List var6 = this.getPlayers();
      Player var7;
      Iterator var8;
      if (this.plugin.spectatorMode != null) {
         var1.setGameMode(this.plugin.spectatorMode);
      } else {
         var8 = var6.iterator();

         while(var8.hasNext()) {
            var7 = (Player)var8.next();
            var7.hidePlayer(var1);
         }
      }

      var8 = var6.iterator();

      while(var8.hasNext()) {
         var7 = (Player)var8.next();
         var7.sendMessage(var5);
      }

      var2.removePlayer(var1);
      this.players.put(var1.getName(), (Object)null);
      String var12 = ((String)this.plugin.customization.messages.get("Team-Player-Eliminate")).replace("%player%", var1.getName()).replace("%teamsize%", String.valueOf(var2.getSize())).replace("%maxteamsize%", String.valueOf(this.teamSize));
      Iterator var9 = var2.getPlayers().iterator();

      while(var9.hasNext()) {
         OfflinePlayer var13 = (OfflinePlayer)var9.next();
         if (var13 instanceof Player) {
            ((Player)var13).sendMessage(var12);
         }
      }

      if (this.plugin.spectatorMode != null) {
         var1.sendMessage((String)this.plugin.customization.messages.get("Spectator-Tip"));
      }

      this.checkElimination(var2);
   }

   public void checkElimination(Team var1) {
      if (var1.getSize() == 0) {
         this.deleteScoreboardTeams();
         this.createScoreboardTeams();
         if (((Arena.TeamData)this.teams.get(var1)).egg_alive) {
            this.destroyCore(var1, false);
         }

         if (this.getAliveTeams().size() > 1) {
            String var2 = ((String)this.plugin.customization.messages.get("Team-Elimination")).replace("%team%", ChatColor.valueOf(var1.getName()) + var1.getName());
            TitleObject var3 = this.plugin.config.titles_enabled ? (new TitleObject(((String)this.plugin.customization.titles.get("Team-Elimination")).replace("%team%", ChatColor.valueOf(var1.getName()) + var1.getName()), TitleType.TITLE)).setFadeIn(this.plugin.config.titles_fadeIn).setStay(this.plugin.config.titles_stay).setFadeOut(this.plugin.config.titles_fadeOut) : null;
            Iterator var5 = this.getPlayers().iterator();

            while(var5.hasNext()) {
               Player var4 = (Player)var5.next();
               var4.sendMessage(var2);
               var4.playSound(var4.getLocation(), this.plugin.NOTE_PLING, 1.0F, 1.0F);
               if (var3 != null) {
                  var3.send(var4);
               }
            }
         } else {
            this.cancelTask(1);
            this.cancelTask(3);
            this.finish();
         }
      } else {
         this.scoreboard.update(var1.getName(), ChatColor.RED + Enums.SPECIAL_CHARACTER.XMARK.toString() + " " + ChatColor.valueOf(var1.getName()) + var1.getName() + " (" + var1.getSize() + ")", false);
      }

   }

   public void joinTeam(Player var1, Team var2) {
      PlayerData var3 = (PlayerData)this.plugin.playerData.get(var1.getName());
      Object var4 = new ArrayList();
      if (var3.party != null && var3.party.leaderName.equals(var1.getName())) {
         var4 = this.plugin.getPlayers(var3.party.players);
      } else {
         ((List)var4).add(var1);
      }

      Player var5;
      if (((List)var4).size() > 1) {
         label124: {
            Iterator var6 = ((List)var4).iterator();

            do {
               if (!var6.hasNext()) {
                  break label124;
               }

               var5 = (Player)var6.next();
            } while(((PlayerData)this.plugin.playerData.get(var5.getName())).arena != null && ((PlayerData)this.plugin.playerData.get(var5.getName())).arena.name.equals(this.name));

            Iterator var8 = ((List)var4).iterator();

            while(var8.hasNext()) {
               Player var7 = (Player)var8.next();
               var3.party.leave(var7);
            }

            return;
         }
      }

      if (!((Player)((List)var4).get(0)).getName().equals(var1.getName())) {
         var5 = (Player)((List)var4).get(0);

         for(int var12 = 1; var12 < ((List)var4).size(); ++var12) {
            if (((Player)((List)var4).get(var12)).getName().equals(var1.getName())) {
               ((List)var4).set(0, var1);
               ((List)var4).set(var12, var5);
               break;
            }
         }
      }

      HashMap var11 = new HashMap();
      Iterator var14 = this.teams.keySet().iterator();

      Team var13;
      while(var14.hasNext()) {
         var13 = (Team)var14.next();
         var11.put(var13, var13.getSize());
      }

      var14 = ((List)var4).iterator();

      while(true) {
         while(true) {
            Player var15;
            do {
               if (!var14.hasNext()) {
                  var14 = var11.keySet().iterator();

                  while(var14.hasNext()) {
                     var13 = (Team)var14.next();
                     if (var13.getSize() != (Integer)var11.get(var13)) {
                        this.updateTeamSelector(var13);
                     }
                  }

                  var11.clear();
                  return;
               }

               var15 = (Player)var14.next();
            } while(var2.getPlayers().contains(var15));

            if (var2.getSize() == this.teamSize) {
               var15.sendMessage((String)this.plugin.customization.messages.get("Player-Select-Team-Error"));
            } else {
               boolean var16 = false;
               Iterator var10 = this.teams.keySet().iterator();

               Team var9;
               while(var10.hasNext()) {
                  var9 = (Team)var10.next();
                  if (var9.getSize() > 0 && var9.getSize() <= var2.getSize() - 1) {
                     var15.sendMessage((String)this.plugin.customization.messages.get("Player-Select-Team-Error"));
                     var16 = true;
                     break;
                  }
               }

               if (!var16) {
                  var9 = (Team)this.players.get(var15.getName());
                  if (var9 != null) {
                     var9.removePlayer(var15);
                  }

                  this.players.put(var15.getName(), var2);
                  var2.addPlayer(var15);
                  var15.sendMessage(((String)this.plugin.customization.messages.get("Player-Select-Team")).replace("%team%", ChatColor.valueOf(var2.getName()) + var2.getName()));
               }
            }
         }
      }
   }

   public void join(Player var1) {
      if (!this.state.AVAILABLE()) {
         if (this.state.equals(Enums.ArenaState.INGAME) && this.plugin.lobbyPlayers.contains(var1.getName())) {
            this.joinSpectator(var1);
         } else {
            var1.sendMessage((String)this.plugin.customization.messages.get("Arena-Not-Available"));
         }

      } else if (this.lobby == null) {
         var1.sendMessage((String)this.plugin.customization.messages.get("Arena-No-Lobby"));
      } else if (this.teams.size() < 2) {
         var1.sendMessage((String)this.plugin.customization.messages.get("Arena-Not-Enough-Teams"));
      } else {
         PlayerData var2 = (PlayerData)this.plugin.playerData.get(var1.getName());
         if (this.players.size() + (var2.party != null ? var2.party.players.size() : 1) > this.teams.size() * this.teamSize) {
            var1.sendMessage((String)this.plugin.customization.messages.get("Arena-Full"));
         } else {
            if (var2.party != null) {
               if (!var2.party.leaderName.equals(var1.getName())) {
                  var1.sendMessage((String)this.plugin.customization.messages.get("Party-Must-Be-Leader"));
                  return;
               }

               Iterator var4 = var2.party.players.iterator();

               while(var4.hasNext()) {
                  String var3 = (String)var4.next();
                  if (!this.plugin.lobbyPlayers.contains(var3)) {
                     var1.sendMessage(((String)this.plugin.customization.messages.get("Party-Player-Not-Available")).replace("%member%", var3));
                     return;
                  }
               }
            }

            if (!this.plugin.lobbyPlayers.contains(var1.getName())) {
               if (var2.arena == null || !var2.arena.spectators.contains(var1.getName())) {
                  var1.sendMessage((String)this.plugin.customization.messages.get("Not-In-Lobby"));
                  return;
               }

               var2.arena.leave(var1);
            }

            List var10 = var2.party != null ? this.plugin.getPlayers(var2.party.players) : Arrays.asList(var1);
            Iterator var5 = var10.iterator();

            while(var5.hasNext()) {
               Player var11 = (Player)var5.next();
               this.scoreboard.apply(var11);
               this.players.put(var11.getName(), (Object)null);
               ((PlayerData)this.plugin.playerData.get(var11.getName())).arena = this;
               ((PlayerData)this.plugin.playerData.get(var11.getName())).lobbyScoreboard = null;
               this.plugin.lobbyPlayers.remove(var11.getName());
               var11.teleport(this.lobby);
               var11.getInventory().clear();
               var11.getInventory().addItem(new ItemStack[]{this.plugin.teamselector_itemstack});
               var11.getInventory().addItem(new ItemStack[]{this.plugin.vote_itemstack});
               var11.getInventory().addItem(new ItemStack[]{this.plugin.inventory_itemstack});
               var11.getInventory().setItem(4, this.plugin.profile_itemstack);
               var11.getInventory().setItem(5, this.plugin.shop_itemstack);
               var11.getInventory().setItem(8, this.plugin.quit_itemstack);
               var11.updateInventory();
               String var6 = ((String)this.plugin.customization.messages.get("Player-Join-Arena")).replace("%player%", var11.getName()).replace("%players%", String.valueOf(this.players.size())).replace("%maxplayers%", String.valueOf(this.teamSize * this.teams.size()));
               List var7 = this.getPlayers();
               Iterator var9 = var7.iterator();

               while(var9.hasNext()) {
                  Player var8 = (Player)var9.next();
                  var8.sendMessage(var6);
               }

               Bukkit.getPluginManager().callEvent(new EWPlayerJoinArenaEvent(var11, this.name, this.cuboid.worldName, var7));
            }

            this.scoreboard.update((String)this.plugin.customization.scoreboard.get("Players"), (String)this.plugin.customization.scoreboard.get("Players") + ChatColor.WHITE + " " + this.players.size(), false);
            if (this.players.size() >= this.minTeams * this.teamSize && this.tasks[0] == null) {
               this.countDown();
            }

            this.updateItem(0);
         }
      }
   }

   public void joinSpectator(Player var1) {
      if (this.bannedPlayers.contains(var1.getName())) {
         var1.sendMessage((String)this.plugin.customization.messages.get("Player-Spectate-Banned"));
      } else {
         PlayerData var2 = (PlayerData)this.plugin.playerData.get(var1.getName());
         this.players.put(var1.getName(), (Object)null);
         this.spectators.add(var1.getName());
         this.plugin.lobbyPlayers.remove(var1.getName());
         if (!this.plugin.protectedPlayers.contains(var1.getName())) {
            this.plugin.protectedPlayers.add(var1.getName());
         }

         var2.arena = this;
         var2.lobbyScoreboard = null;
         var2.warnings = 0;
         var2.killstreak = 0;
         var1.teleport(this.spectatorsLocation != null ? this.spectatorsLocation : this.cuboid.getRandomLocation());
         var1.getInventory().clear();
         if (this.plugin.spectatorMode != null) {
            var1.setGameMode(this.plugin.spectatorMode);
         } else {
            var2.makeSpectator(var1);
            Iterator var4 = this.getPlayers().iterator();

            while(var4.hasNext()) {
               Player var3 = (Player)var4.next();
               var3.hidePlayer(var1);
            }
         }

         var1.updateInventory();
         this.updateItem(0);
         this.scoreboard.apply(var1);
         var1.sendMessage((String)this.plugin.customization.messages.get("Player-Spectate"));
         if (this.plugin.spectatorMode != null) {
            var1.sendMessage((String)this.plugin.customization.messages.get("Spectator-Tip"));
         }

         TitleObject var5 = this.plugin.config.titles_enabled ? (new TitleObject((String)this.plugin.customization.titles.get("Player-Spectate"), TitleType.TITLE)).setFadeIn(this.plugin.config.titles_fadeIn).setStay(this.plugin.config.titles_stay).setFadeOut(this.plugin.config.titles_fadeOut) : null;
         if (var5 != null) {
            var5.send(var1);
         }

      }
   }

   public void leave(Player var1) {
      PlayerData var2 = (PlayerData)this.plugin.playerData.get(var1.getName());
      Object var3 = new ArrayList();
      if (this.state.AVAILABLE() && var2.party != null) {
         if (!var2.party.leaderName.equals(var1.getName())) {
            var1.sendMessage((String)this.plugin.customization.messages.get("Leave-Arena-Error"));
            return;
         }

         var3 = this.plugin.getPlayers(var2.party.players);
      } else {
         ((List)var3).add(var1);
      }

      Player var4;
      List var13;
      for(Iterator var5 = ((List)var3).iterator(); var5.hasNext(); Bukkit.getPluginManager().callEvent(new EWPlayerLeaveArenaEvent(var4, this.name, this.cuboid.worldName, var13))) {
         var4 = (Player)var5.next();
         PlayerData var6 = (PlayerData)this.plugin.playerData.get(var4.getName());
         var6.arena = null;
         Team var7 = (Team)this.players.get(var4.getName());
         if (var7 != null) {
            var7.removePlayer(var4);
         }

         this.players.remove(var4.getName());
         if (!this.plugin.protectedPlayers.contains(var4.getName())) {
            this.plugin.protectedPlayers.add(var4.getName());
         }

         this.plugin.lobbyPlayers.add(var4.getName());
         var4.teleport(this.plugin.lobbyLocation);
         ((PlayerData)this.plugin.playerData.get(var4.getName())).clearPlayer(var4);
         var4.sendMessage((String)this.plugin.customization.messages.get("Player-Leave"));
         Iterator var9 = this.plugin.getPlayers(this.spectators).iterator();

         while(var9.hasNext()) {
            Player var8 = (Player)var9.next();
            var4.showPlayer(var8);
         }

         var13 = this.getPlayers();
         if (this.spectators.contains(var4.getName())) {
            Iterator var17 = var13.iterator();

            while(var17.hasNext()) {
               Player var15 = (Player)var17.next();
               var15.showPlayer(var4);
            }

            this.spectators.remove(var4.getName());
            return;
         }

         String var14 = ((String)this.plugin.customization.messages.get("Player-Leave-Arena")).replace("%player%", var4.getName()).replace("%players%", String.valueOf(this.players.size())).replace("%maxplayers%", String.valueOf(this.teamSize * this.teams.size()));
         Iterator var11 = var13.iterator();

         while(var11.hasNext()) {
            Player var10 = (Player)var11.next();
            var10.sendMessage(var14);
         }

         Iterator var12;
         String var16;
         if (var7 != null) {
            var16 = ((String)this.plugin.customization.messages.get("Player-Leave-Team")).replace("%player%", var4.getName()).replace("%teamsize%", String.valueOf(var7.getSize())).replace("%maxteamsize%", String.valueOf(this.teamSize));
            var12 = var7.getPlayers().iterator();

            while(var12.hasNext()) {
               OfflinePlayer var18 = (OfflinePlayer)var12.next();
               if (var18 instanceof Player) {
                  ((Player)var18).sendMessage(var16);
               }
            }
         }

         if (!this.state.AVAILABLE()) {
            if (var7 != null) {
               this.checkElimination(var7);
            }
         } else {
            if (var7 != null) {
               this.updateTeamSelector(var7);
            }

            this.votesManager.removeVotes(var4);
            if (this.tasks[0] != null && this.players.size() < this.minTeams * this.teamSize) {
               this.cancelTask(0);
               this.state = Enums.ArenaState.WAITING;
               var16 = (String)this.plugin.customization.messages.get("Countdown-Cancel");
               var12 = var13.iterator();

               while(var12.hasNext()) {
                  Player var19 = (Player)var12.next();
                  var19.sendMessage(var16);
               }

               this.scoreboard.update((String)this.plugin.customization.scoreboard.get("Time"), (String)this.plugin.customization.scoreboard.get("Time") + ChatColor.WHITE + " " + this.lobbyCountdown, false);
            }
         }
      }

      this.scoreboard.update((String)this.plugin.customization.scoreboard.get("Players"), (String)this.plugin.customization.scoreboard.get("Players") + ChatColor.WHITE + " " + this.players.size(), false);
      this.updateItem(0);
   }

   public void countDown() {
      this.state = Enums.ArenaState.STARTING;
      Iterator var2 = this.plugin.config.executed_commands_arena_countdown.iterator();

      while(var2.hasNext()) {
         String var1 = (String)var2.next();
         Bukkit.dispatchCommand(Bukkit.getConsoleSender(), var1.replace("%arena%", this.name).replace("%seconds%", String.valueOf(this.lobbyCountdown)));
      }

      this.tasks[0] = (new BukkitRunnable() {
         int seconds;

         {
            this.seconds = Arena.this.lobbyCountdown;
         }

         public void run() {
            String var1;
            TitleObject var2;
            Player var3;
            Iterator var4;
            if (this.seconds > Arena.this.countdownShortenTo && Arena.this.players.size() >= Arena.this.minPlayersForCountdownShorten) {
               this.seconds = Arena.this.countdownShortenTo;
               var1 = ((String)Arena.this.plugin.customization.messages.get("Arena-Starting-Countdown-Shortening")).replace("%seconds%", String.valueOf(this.seconds));
               var2 = Arena.this.plugin.config.titles_enabled ? (new TitleObject(((String)Arena.this.plugin.customization.titles.get("Starting-Shortening")).replace("%seconds%", String.valueOf(this.seconds)), TitleType.TITLE)).setFadeIn(Arena.this.plugin.config.titles_fadeIn).setStay(Arena.this.plugin.config.titles_stay).setFadeOut(Arena.this.plugin.config.titles_fadeOut) : null;
               var4 = Arena.this.getPlayers().iterator();

               while(var4.hasNext()) {
                  var3 = (Player)var4.next();
                  var3.sendMessage(var1);
                  var3.playSound(var3.getLocation(), Arena.this.plugin.CLICK, 1.0F, 1.0F);
                  if (var2 != null) {
                     var2.send(var3);
                  }
               }
            } else if (Arena.this.plugin.config.broadcastTime.contains(this.seconds)) {
               var1 = ((String)Arena.this.plugin.customization.messages.get("Arena-Starting-Countdown")).replace("%seconds%", String.valueOf(this.seconds));
               var2 = Arena.this.plugin.config.titles_enabled ? (new TitleObject(((String)Arena.this.plugin.customization.titles.get("Starting")).replace("%seconds%", String.valueOf(this.seconds)), TitleType.TITLE)).setFadeIn(Arena.this.plugin.config.titles_fadeIn).setStay(Arena.this.plugin.config.titles_stay).setFadeOut(Arena.this.plugin.config.titles_fadeOut) : null;
               var4 = Arena.this.getPlayers().iterator();

               while(var4.hasNext()) {
                  var3 = (Player)var4.next();
                  var3.sendMessage(var1);
                  var3.playSound(var3.getLocation(), Arena.this.plugin.CLICK, 1.0F, 1.0F);
                  if (var2 != null) {
                     var2.send(var3);
                  }
               }
            }

            Arena.this.scoreboard.update((String)Arena.this.plugin.customization.scoreboard.get("Time"), (String)Arena.this.plugin.customization.scoreboard.get("Time") + ChatColor.WHITE + " " + this.seconds, false);
            if (this.seconds == 0) {
               Arena.this.start();
            }

            --this.seconds;
         }
      }).runTaskTimer(this.plugin, 0L, 20L);
   }

   public void start() {
      this.clearEntities();
      this.state = Enums.ArenaState.INGAME;
      this.cancelTask(0);
      this.updateItem(0);
      String var1 = (String)this.plugin.customization.messages.get("Arena-Start");
      Iterator var3 = this.plugin.config.executed_commands_arena_start.iterator();

      while(var3.hasNext()) {
         String var2 = (String)var3.next();
         Bukkit.dispatchCommand(Bukkit.getConsoleSender(), var2.replace("%world%", this.cuboid.worldName));
      }

      TitleObject var16 = this.plugin.config.titles_enabled ? (new TitleObject((String)this.plugin.customization.titles.get("Start"), TitleType.TITLE)).setFadeIn(this.plugin.config.titles_fadeIn).setStay(this.plugin.config.titles_stay).setFadeOut(this.plugin.config.titles_fadeOut) : null;
      this.votedShop = this.votesManager.getVoted("Items");
      String var17 = this.votesManager.getVoted("Time");
      if (var17 == "Default") {
         var17 = "Noon";
      }

      World var4 = Bukkit.getWorld(this.cuboid.worldName);
      if (var17.equals("Sunrise")) {
         var4.setTime(0L);
      } else if (var17.equals("Noon")) {
         var4.setTime(2000L);
      } else if (var17.equals("Sunset")) {
         var4.setTime(12000L);
      } else if (var17.equals("Midnight")) {
         var4.setTime(15000L);
      }

      String var5 = this.votesManager.getVoted("Health");
      int var6 = (var5.equals("Default") ? 10 : Integer.valueOf(ChatColor.stripColor(var5.split(" ")[0]))) * 2;
      String[] var7 = new String[]{ChatColor.DARK_AQUA + "======== " + ChatColor.AQUA + "Arena Settings" + ChatColor.DARK_AQUA + " =======", ChatColor.AQUA + " - " + ChatColor.GOLD + "Time: " + ChatColor.YELLOW + ChatColor.BOLD + var17, ChatColor.AQUA + " - " + ChatColor.GOLD + "Items: " + ChatColor.YELLOW + ChatColor.BOLD + this.votedShop, ChatColor.AQUA + " - " + ChatColor.GOLD + "Health: " + ChatColor.YELLOW + ChatColor.BOLD + var5, ChatColor.DARK_AQUA + "============================="};
      List var8 = this.getPlayers();
      Iterator var10 = var8.iterator();

      while(var10.hasNext()) {
         Player var9 = (Player)var10.next();
         PlayerData var11 = (PlayerData)this.plugin.playerData.get(var9.getName());
         var9.sendMessage(var1);
         var9.closeInventory();
         this.plugin.protectedPlayers.remove(var9.getName());
         var11.warnings = 0;
         var11.killstreak = 0;
         var9.setMaxHealth((double)var6);
         var9.setHealth(var9.getMaxHealth());
         var9.getInventory().clear();
         var9.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 5));
         Iterator var13 = this.potionEffects.iterator();

         while(var13.hasNext()) {
            PotionEffect var12 = (PotionEffect)var13.next();
            var9.addPotionEffect(var12);
         }

         if (this.plugin.kits.containsKey(var11.selectedKit)) {
            Kit var21 = (Kit)this.plugin.kits.get(var11.selectedKit);
            var21.apply(var9);
            var9.sendMessage(((String)this.plugin.customization.messages.get("Kit-Receive")).replace("%kit%", var21.name));
         }

         if (var16 != null) {
            var16.send(var9);
         }

         int var23;
         if (this.players.get(var9.getName()) == null) {
            Team var22 = null;
            var23 = this.teamSize;
            Iterator var15 = this.teams.keySet().iterator();

            while(var15.hasNext()) {
               Team var14 = (Team)var15.next();
               if (var14.getSize() < var23) {
                  var23 = var14.getSize();
                  var22 = var14;
               }
            }

            if (var22 == null) {
               this.leave(var9);
               return;
            }

            this.players.put(var9.getName(), var22);
            var22.addPlayer(var9);
         }

         var9.teleport(((Arena.TeamData)this.teams.get(this.players.get(var9.getName()))).spawnpoint);
         String[] var26 = var7;
         int var25 = var7.length;

         for(var23 = 0; var23 < var25; ++var23) {
            String var24 = var26[var23];
            var9.sendMessage(var24);
         }

         var9.sendMessage(((String)this.plugin.customization.messages.get("Team-Notification")).replace("%team%", ChatColor.valueOf(((Team)this.players.get(var9.getName())).getName()) + ((Team)this.players.get(var9.getName())).getName()));
      }

      this.scoreboard.update((String)this.plugin.customization.scoreboard.get("Time"), (String)this.plugin.customization.scoreboard.get("Next-Event"), false);
      this.createScoreboardTeams();
      var10 = this.teams.keySet().iterator();

      while(var10.hasNext()) {
         final Team var18 = (Team)var10.next();
         if (var18.getSize() > 0) {
            final Arena.TeamData var20 = (Arena.TeamData)this.teams.get(var18);
            if (!var20.egg.getBlock().getType().equals(this.plugin.config.coreBlockType)) {
               var20.egg.getBlock().setType(this.plugin.config.coreBlockType);
               (new BukkitRunnable() {
                  public void run() {
                     if (!var20.egg.getBlock().getType().equals(Arena.this.plugin.config.coreBlockType)) {
                        Arena.this.destroyCore(var18, false);
                     }

                  }
               }).runTaskLater(this.plugin, 2L);
            }
         } else {
            this.destroyCore(var18, false);
         }
      }

      this.tasks[1] = (new BukkitRunnable() {
         int seconds;
         int nextEvent;
         String eventName;

         {
            this.seconds = Arena.this.gameLength;
            this.nextEvent = Arena.this.getHighest(this.seconds, Arena.this.events.keySet());
            this.eventName = Arena.this.getEvent(this.nextEvent);
         }

         public void run() {
            Arena.this.scoreboard.update((String)Arena.this.plugin.customization.scoreboard.get("Next-Event"), this.eventName + ChatColor.WHITE + ": " + String.format("%02d:%02d", (this.seconds - this.nextEvent) / 60, (this.seconds - this.nextEvent) % 60), true);
            Iterator var2;
            if (this.seconds == this.nextEvent) {
               String var5;
               if (this.seconds == 0) {
                  var5 = (String)Arena.this.plugin.customization.messages.get("Game-Cancel");
                  Iterator var10 = Arena.this.getPlayers().iterator();

                  while(var10.hasNext()) {
                     Player var8 = (Player)var10.next();
                     var8.sendMessage(var5);
                  }

                  Arena.this.stop(true);
                  return;
               }

               Player var3;
               Iterator var4;
               TitleObject var7;
               if (((String)Arena.this.events.get(this.seconds)).equals("Deathmatch")) {
                  var5 = (String)Arena.this.plugin.customization.messages.get("Egg-Auto-Destroy");
                  var7 = Arena.this.plugin.config.titles_enabled ? (new TitleObject((String)Arena.this.plugin.customization.titles.get("Egg-Break"), TitleType.TITLE)).setFadeIn(Arena.this.plugin.config.titles_fadeIn).setStay(Arena.this.plugin.config.titles_stay).setFadeOut(Arena.this.plugin.config.titles_fadeOut) : null;
                  var4 = Arena.this.getPlayers().iterator();

                  while(var4.hasNext()) {
                     var3 = (Player)var4.next();
                     var3.sendMessage(var5);
                     var3.playSound(var3.getLocation(), Arena.this.plugin.ENDERDRAGON_GROWL, 1.0F, 1.0F);
                     if (var7 != null) {
                        var7.send(var3);
                     }
                  }

                  var4 = Arena.this.teams.keySet().iterator();

                  while(var4.hasNext()) {
                     Team var9 = (Team)var4.next();
                     Arena.this.destroyCore(var9, false);
                  }
               } else if (((String)Arena.this.events.get(this.seconds)).equals("Upgrades")) {
                  var2 = Arena.this.spawners.iterator();

                  while(var2.hasNext()) {
                     Spawner var1 = (Spawner)var2.next();
                     if (var1.canUpgrade()) {
                        var1.upgrade();
                     }
                  }

                  var5 = (String)Arena.this.plugin.customization.messages.get("Generators-Auto-Upgrade");
                  var7 = Arena.this.plugin.config.titles_enabled ? (new TitleObject((String)Arena.this.plugin.customization.titles.get("Generators-Auto-Upgrade"), TitleType.TITLE)).setFadeIn(Arena.this.plugin.config.titles_fadeIn).setStay(Arena.this.plugin.config.titles_stay).setFadeOut(Arena.this.plugin.config.titles_fadeOut) : null;
                  var4 = Arena.this.getPlayers().iterator();

                  while(var4.hasNext()) {
                     var3 = (Player)var4.next();
                     var3.sendMessage(var5);
                     var3.playSound(var3.getLocation(), Arena.this.plugin.NOTE_PLING, 1.0F, 1.0F);
                     if (var7 != null) {
                        var7.send(var3);
                     }
                  }
               }

               this.nextEvent = Arena.this.getHighest(this.seconds, Arena.this.events.keySet());
               this.eventName = Arena.this.getEvent(this.nextEvent);
            }

            var2 = Arena.this.getPlayers().iterator();

            while(var2.hasNext()) {
               Player var6 = (Player)var2.next();
               if (!Arena.this.cuboid.contains(var6.getLocation())) {
                  if (Arena.this.spectators.contains(var6.getName())) {
                     var6.sendMessage((String)Arena.this.plugin.customization.messages.get("Arena-Borders-Leave"));
                     var6.teleport(Arena.this.cuboid.getRandomLocation());
                  } else if (var6.getLocation().getBlockY() >= Arena.this.cuboid.getLowerY()) {
                     var6.sendMessage((String)Arena.this.plugin.customization.messages.get("Arena-Borders-Outside"));
                     var6.damage(2.0D);
                  }
               }
            }

            --this.seconds;
         }
      }).runTaskTimer(this.plugin, 0L, 20L);
      (new BukkitRunnable() {
         int i = 0;

         public void run() {
            if (this.i >= Arena.this.villagers.size()) {
               this.cancel();
            } else {
               Location var1 = (Location)Arena.this.villagers.get(this.i++);
               Villager var2 = (Villager)var1.getWorld().spawnEntity(var1, EntityType.VILLAGER);
               var2.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 999999, 10));
               var2.setAdult();
               var2.setCanPickupItems(false);
               var2.setCustomName((String)Arena.this.plugin.customization.inventories.get("Villager-Shop"));
               var2.setCustomNameVisible(true);
               var2.setMetadata("EW_VILLAGER", new FixedMetadataValue(Arena.this.plugin, true));
               Vector[] var6;
               int var5 = (var6 = Arena.villagersBox).length;

               for(int var4 = 0; var4 < var5; ++var4) {
                  Vector var3 = var6[var4];
                  Location var7 = var1.clone().add(var3);
                  if (var7.getBlock().getType() == Material.AIR) {
                     var7.getBlock().setType(Material.STEP);
                  }
               }
            }

         }
      }).runTaskTimer(this.plugin, 5L, 5L);
      var10 = this.spawners.iterator();

      while(var10.hasNext()) {
         Spawner var19 = (Spawner)var10.next();
         var19.createHologram();
         var19.createUpgradeSign();
      }

      this.tasks[3] = (new BukkitRunnable() {
         public void run() {
            Iterator var2 = Arena.this.spawners.iterator();

            while(var2.hasNext()) {
               Spawner var1 = (Spawner)var2.next();
               var1.update();
            }

         }
      }).runTaskTimer(this.plugin, 0L, 10L);
      (new BukkitRunnable() {
         public void run() {
            Iterator var2 = Arena.this.getPlayers().iterator();

            while(var2.hasNext()) {
               Player var1 = (Player)var2.next();
               if (!Arena.this.cuboid.contains(var1.getLocation())) {
                  Arena.this.leave(var1);
               }
            }

         }
      }).runTaskLater(this.plugin, 10L);
      Bukkit.getPluginManager().callEvent(new EWArenaStartEvent(this.name, this.cuboid.worldName, var8));
   }

   public void finish() {
      this.state = Enums.ArenaState.ENDING;
      this.scoreboard.update((String)this.plugin.customization.scoreboard.get("Next-Event"), ChatColor.RED + " Game Ended", true);
      Team var1 = null;
      Iterator var3 = this.teams.keySet().iterator();

      while(var3.hasNext()) {
         Team var2 = (Team)var3.next();
         if (var2.getSize() > 0) {
            var1 = var2;
            break;
         }
      }

      if (var1 == null) {
         this.stop(true);
      } else {
         final ArrayList var14 = new ArrayList();
         Iterator var4 = var1.getPlayers().iterator();

         while(var4.hasNext()) {
            OfflinePlayer var15 = (OfflinePlayer)var4.next();
            if (var15 instanceof Player) {
               var14.add(var15.getName());
            }
         }

         List var16 = this.getTopKillers();
         ChatColor var17 = this.plugin.colors[this.plugin.r.nextInt(this.plugin.colors.length)];
         String[] var5 = new String[]{"" + var17 + ChatColor.STRIKETHROUGH + "===============================", " ", " - " + ChatColor.YELLOW + Enums.SPECIAL_CHARACTER.STAR + " " + ChatColor.valueOf(var1.getName()) + var14.toString().replace("[", "").replace("]", "") + ChatColor.GRAY + " won the game! " + ChatColor.YELLOW + Enums.SPECIAL_CHARACTER.STAR, " ", ChatColor.GOLD + "    - 1st Killer - " + (String)((Entry)var16.get(0)).getKey() + " - " + ((List)((Entry)var16.get(0)).getValue()).size(), ChatColor.YELLOW + "   - 2nd Killer - " + (String)((Entry)var16.get(1)).getKey() + " - " + ((List)((Entry)var16.get(1)).getValue()).size(), ChatColor.RED + "     - 3rd Killer - " + (String)((Entry)var16.get(2)).getKey() + " - " + ((List)((Entry)var16.get(2)).getValue()).size(), "" + var17 + ChatColor.STRIKETHROUGH + "==============================="};
         Iterator var7 = this.getPlayers().iterator();

         while(var7.hasNext()) {
            Player var6 = (Player)var7.next();
            String[] var11 = var5;
            int var10 = var5.length;

            for(int var9 = 0; var9 < var10; ++var9) {
               String var8 = var11[var9];
               var6.sendMessage(var8);
            }
         }

         String var18 = (String)this.plugin.customization.messages.get("Arena-Finish");
         TitleObject var19 = this.plugin.config.titles_enabled ? (new TitleObject((String)this.plugin.customization.titles.get("Finish"), TitleType.TITLE)).setFadeIn(this.plugin.config.titles_fadeIn).setStay(this.plugin.config.titles_stay).setFadeOut(this.plugin.config.titles_fadeOut) : null;
         Iterator var21 = this.plugin.getPlayers(var14).iterator();

         while(var21.hasNext()) {
            Player var20 = (Player)var21.next();
            var20.sendMessage(var18);
            var20.setHealth(var20.getMaxHealth());
            if (!this.plugin.protectedPlayers.contains(var20.getName())) {
               this.plugin.protectedPlayers.add(var20.getName());
            }

            PlayerData var22 = (PlayerData)this.plugin.playerData.get(var20.getName());
            ++var22.wins;
            int var23 = this.plugin.config.coinsPerWin * var22.modifier;
            var22.addCoins(var20, var23);
            var20.sendMessage(((String)this.plugin.customization.messages.get("Arena-Finish-Prize")).replace("%coins%", String.valueOf(var23)) + ChatColor.YELLOW + (var22.modifier > 1 ? " (x" + var22.modifier + ")" : ""));
            this.plugin.achievementsManager.checkPlayer(var20, Enums.AchievementType.WINS, var22.wins);
            Iterator var13 = this.plugin.config.executed_commands_player_win.iterator();

            while(var13.hasNext()) {
               String var12 = (String)var13.next();
               Bukkit.dispatchCommand(Bukkit.getConsoleSender(), var12.replace("%player%", var20.getName()));
            }

            if (this.plugin.winnersMap != null) {
               var20.setItemInHand(this.plugin.winnersMap);
            }

            if (var19 != null) {
               var19.send(var20);
            }
         }

         this.tasks[2] = (new BukkitRunnable() {
            int seconds;
            boolean fireworks;

            {
               this.seconds = Arena.this.plugin.config.celebrationLength;
               this.fireworks = true;
            }

            public void run() {
               if (this.fireworks) {
                  Iterator var2 = Arena.this.plugin.getPlayers(var14).iterator();

                  while(var2.hasNext()) {
                     Player var1 = (Player)var2.next();
                     Arena.this.plugin.fireWorkEffect(var1.getLocation(), true);
                  }

                  this.fireworks = false;
               } else {
                  this.fireworks = true;
               }

               if (this.seconds == 0) {
                  Arena.this.stop(true);
               }

               --this.seconds;
            }
         }).runTaskTimer(this.plugin, 0L, 20L);
         this.updateItem(0);
      }
   }

   public void stop(boolean var1) {
      List var2 = this.getPlayers();
      Bukkit.getPluginManager().callEvent(new EWArenaStopEvent(this.name, this.cuboid.worldName, var2));
      Iterator var4 = this.plugin.config.executed_commands_arena_end.iterator();

      while(var4.hasNext()) {
         String var3 = (String)var4.next();
         Bukkit.dispatchCommand(Bukkit.getConsoleSender(), var3.replace("%world%", this.cuboid.worldName).replace("%arena%", this.name));
      }

      Iterator var5;
      Player var12;
      if (this.plugin.isOneGamePerServer()) {
         ByteArrayDataOutput var11 = ByteStreams.newDataOutput();
         var11.writeUTF("Connect");
         var11.writeUTF(this.plugin.config.bungee_mode_hub);
         var5 = var2.iterator();

         while(var5.hasNext()) {
            var12 = (Player)var5.next();
            var12.sendPluginMessage(this.plugin, "BungeeCord", var11.toByteArray());
         }

         (new BukkitRunnable() {
            public void run() {
               Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Arena.this.plugin.getConfig().getString("Bungee-Mode.restart-command"));
            }
         }).runTaskLater(this.plugin, 20L);
      } else {
         for(int var9 = 0; var9 < this.tasks.length; ++var9) {
            this.cancelTask(var9);
         }

         List var10 = this.plugin.getPlayers(this.spectators);
         var5 = var2.iterator();

         Iterator var8;
         while(var5.hasNext()) {
            var12 = (Player)var5.next();
            PlayerData var6 = (PlayerData)this.plugin.playerData.get(var12.getName());
            this.plugin.lobbyPlayers.add(var12.getName());
            if (!this.plugin.protectedPlayers.contains(var12.getName())) {
               this.plugin.protectedPlayers.add(var12.getName());
            }

            var12.teleport(this.plugin.lobbyLocation);
            var6.clearPlayer(var12);
            var6.arena = null;
            var8 = var10.iterator();

            while(var8.hasNext()) {
               Player var7 = (Player)var8.next();
               var12.showPlayer(var7);
            }
         }

         this.players.clear();
         this.spectators.clear();
         this.bannedPlayers.clear();
         this.killers.clear();
         var5 = this.teams.keySet().iterator();

         while(var5.hasNext()) {
            Team var13 = (Team)var5.next();
            ((Arena.TeamData)this.teams.get(var13)).egg_alive = true;
            ((Arena.TeamData)this.teams.get(var13)).enderchest.clear();
            Set var15 = var13.getPlayers();
            var8 = var15.iterator();

            while(var8.hasNext()) {
               OfflinePlayer var16 = (OfflinePlayer)var8.next();
               var13.removePlayer(var16);
            }

            this.updateTeamSelector(var13);
         }

         this.votesManager.reset();
         this.scoreboard.update((String)this.plugin.customization.scoreboard.get("Next-Event"), (String)this.plugin.customization.scoreboard.get("Time") + ChatColor.WHITE + " " + this.lobbyCountdown, false);
         this.scoreboard.update((String)this.plugin.customization.scoreboard.get("Time"), " ", true);
         this.scoreboard.update(this.date, this.date = ChatColor.GRAY + (new SimpleDateFormat("dd/MM/yyyy")).format(new Date()), false);
         this.deleteScoreboardTeams();
         var5 = this.spawners.iterator();

         while(var5.hasNext()) {
            Spawner var14 = (Spawner)var5.next();
            var14.reset();
         }

         if (var1 && !this.state.AVAILABLE()) {
            this.plugin.rollbackManager.add(this);
         } else {
            this.state = this.enabled ? Enums.ArenaState.WAITING : Enums.ArenaState.DISABLED;
         }

         this.updateItem(0);
      }
   }

   public boolean isPlaceable(Block var1) {
      Location var2 = var1.getLocation();
      String var3 = var2.getWorld().getName();
      Iterator var5 = this.villagers.iterator();

      Location var4;
      do {
         if (!var5.hasNext()) {
            var5 = this.spawners.iterator();

            Location var6;
            do {
               if (!var5.hasNext()) {
                  return true;
               }

               Spawner var7 = (Spawner)var5.next();
               var6 = var7.spawningLocation;
            } while(!var6.getWorld().getName().equals(var3) || var6.distance(var2) > (double)this.plugin.config.buildProtectionRange);

            return false;
         }

         var4 = (Location)var5.next();
      } while(!var4.getWorld().getName().equals(var3) || var4.distance(var2) > (double)this.plugin.config.buildProtectionRange);

      return false;
   }

   public boolean isBreakable(Block var1) {
      return !this.blocks.containsKey(new Arena.CLocation(var1.getLocation(), (Arena.CLocation)null));
   }

   public Spawner getSpawner(Location var1) {
      Iterator var3 = this.spawners.iterator();

      while(var3.hasNext()) {
         Spawner var2 = (Spawner)var3.next();
         if (this.plugin.compareLocation(var2.blockLocation, var1)) {
            return var2;
         }
      }

      return null;
   }

   public Spawner getSpawner(Sign var1) {
      Iterator var3 = this.spawners.iterator();

      while(var3.hasNext()) {
         Spawner var2 = (Spawner)var3.next();
         if (this.plugin.compareLocation(var2.sign.getLocation(), var1.getLocation())) {
            return var2;
         }
      }

      return null;
   }

   public Spawner getSpawner(Inventory var1) {
      Iterator var3 = this.spawners.iterator();

      Spawner var2;
      do {
         if (!var3.hasNext()) {
            return null;
         }

         var2 = (Spawner)var3.next();
      } while(var2.upgradeInventory == null || !var2.upgradeInventory.equals(var1));

      return var2;
   }

   public void rollback() {
      this.state = Enums.ArenaState.ROLLBACKING;
      final Iterator var2 = this.openedHolders.iterator();

      while(var2.hasNext()) {
         Location var1 = (Location)var2.next();
         Block var3 = var1.getBlock();
         if (var3.getState() instanceof InventoryHolder) {
            ((InventoryHolder)var3.getState()).getInventory().clear();
         }
      }

      this.openedHolders.clear();
      World var6 = Bukkit.getWorld(this.cuboid.worldName);
      var2 = this.cuboid.iterator();
      final double var7 = (double)this.cuboid.getSize();
      final int var5 = this.plugin.config.rollback_repair_speed;
      (new BukkitRunnable() {
         int checked = 0;
         int current = 0;
         int ticks = 0;

         public void run() {
            for(; var2.hasNext() && this.current < var5; ++this.current) {
               Block var1 = (Block)var2.next();
               Arena.CLocation var2x = Arena.this.new CLocation(var1.getLocation(), (Arena.CLocation)null);
               if (Arena.this.blocks.containsKey(var2x)) {
                  Arena.CBlock var3 = (Arena.CBlock)Arena.this.blocks.get(var2x);
                  if (var3.type != var1.getTypeId() || var3.data != var1.getData()) {
                     var1.setTypeIdAndData(var3.type, var3.data, false);
                  }
               } else if (var1.getType() != Material.AIR) {
                  var1.setType(Material.AIR);
               }
            }

            this.checked += this.current;
            this.current = 0;
            ++this.ticks;
            if (this.ticks == Arena.this.plugin.config.rollback_send_status_update_every) {
               int var4 = (int)((double)this.checked / var7 * 100.0D);
               Arena.this.updateItem(var4);
               this.ticks = 0;
            }

            if (!var2.hasNext()) {
               this.cancel();
               Arena.this.clearEntities();
               Arena.this.state = Arena.this.enabled ? Enums.ArenaState.WAITING : Enums.ArenaState.DISABLED;
               Arena.this.updateItem(0);
            }

         }
      }).runTaskTimer(this.plugin, 10L, 1L);
   }

   public void clearEntities() {
      World var1 = Bukkit.getWorld(this.cuboid.worldName);
      Iterator var3 = var1.getEntities().iterator();

      while(var3.hasNext()) {
         Entity var2 = (Entity)var3.next();
         if (this.cuboid.contains(var2.getLocation()) && !var2.getType().equals(EntityType.PLAYER)) {
            var2.remove();
         }
      }

   }

   private class CBlock {
      int type;
      byte data;

      private CBlock(int var2, byte var3) {
         this.type = var2;
         this.data = var3;
      }

      // $FF: synthetic method
      CBlock(int var2, byte var3, Arena.CBlock var4) {
         this(var2, var3);
      }
   }

   private class CLocation {
      int x;
      int y;
      int z;

      private CLocation(int var2, int var3, int var4) {
         this.x = var2;
         this.y = var3;
         this.z = var4;
      }

      private CLocation(Location var2) {
         this.x = var2.getBlockX();
         this.y = var2.getBlockY();
         this.z = var2.getBlockZ();
      }

      public boolean equals(Object var1) {
         Arena.CLocation var2 = (Arena.CLocation)var1;
         return this.x == var2.x && this.y == var2.y && this.z == var2.z;
      }

      public int hashCode() {
         return (String.valueOf(this.x) + this.y + this.z).hashCode();
      }

      // $FF: synthetic method
      CLocation(int var2, int var3, int var4, Arena.CLocation var5) {
         this(var2, var3, var4);
      }

      // $FF: synthetic method
      CLocation(Location var2, Arena.CLocation var3) {
         this(var2);
      }
   }

   public class TeamData {
      Location spawnpoint;
      Location egg;
      boolean egg_alive;
      Inventory enderchest;
      int teamSelectorSlot;

      public TeamData(String var2, Location var3, Location var4) {
         this.spawnpoint = var3;
         this.egg = var4;
         this.egg_alive = true;
         this.enderchest = Bukkit.createInventory((InventoryHolder)null, 27, ChatColor.valueOf(var2) + var2);
      }
   }
}
