package me.wazup.skywars;

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
import me.wazup.skywars.events.SWArenaFinishEvent;
import me.wazup.skywars.events.SWArenaStartEvent;
import me.wazup.skywars.events.SWPlayerJoinArenaEvent;
import me.wazup.skywars.events.SWPlayerLeaveArenaEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Team;

class Arena {
   private Skywars plugin;
   String name;
   boolean enabled;
   Cuboid cuboid;
   Enums.ArenaState state;
   int teamSize;
   int minTeams;
   int maxTeams;
   int lobbyCountdown;
   int minPlayersForCountdownShorten;
   int countdownShortenTo;
   int gameLength;
   List refillTimes;
   List potionEffects;
   Location spectatorsLocation;
   VotesManager votesManager;
   HashMap teams;
   HashMap players;
   HashMap chests;
   HashMap killers;
   ArrayList spectators;
   Inventory editor;
   int id;
   int slot;
   CustomScoreboard scoreboard;
   private String date;
   private BukkitTask[] tasks;
   public HashMap signs;
   private HashMap blocks;

   public Arena(Skywars var1, String var2) {
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

         this.maxTeams = var4.getInt("max-teams");
         this.lobbyCountdown = var4.getInt("lobby-countdown");
         this.minPlayersForCountdownShorten = var4.getInt("lobby-countdown-shortening.min-teams") * this.teamSize;
         this.countdownShortenTo = var4.getInt("lobby-countdown-shortening.shorten-to");
         this.gameLength = var4.getInt("game-length");
         this.refillTimes = var4.getIntegerList("refill-times");
         this.potionEffects = new ArrayList();
         Iterator var6 = var4.getStringList("effects").iterator();

         while(var6.hasNext()) {
            String var5 = (String)var6.next();
            String[] var7 = var5.split(" : ");
            if (var7.length == 3) {
               this.potionEffects.add(new PotionEffect(PotionEffectType.getByName(var7[0]), Integer.valueOf(var7[1]) * 20, Integer.valueOf(var7[2]) - 1));
            }
         }

         this.votesManager = new VotesManager(var1);
         File var16 = new File(var1.getDataFolder() + "/arenas/" + var2, "locations.dat");
         YamlConfiguration var17 = YamlConfiguration.loadConfiguration(var16);
         this.cuboid = new Cuboid(var17.getString("Cuboid"));
         World var18 = Bukkit.getWorld(this.cuboid.worldName);
         if (var18 == null) {
            Bukkit.getConsoleSender().sendMessage("The arena " + var2 + " world seems to be unloaded! attempting to import it");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "sw worldmanager import " + this.cuboid.worldName);
            var18 = Bukkit.getWorld(this.cuboid.worldName);
         }

         this.spectatorsLocation = var17.contains("Spectators-Spawnpoint") ? var1.getLocationFromString(var17.getString("Spectators-Spawnpoint")) : null;
         this.chests = new HashMap();
         String[] var8 = var17.getString("Chests").replace("[", "").replace("]", "").split(", ");
         String[] var12 = var8;
         int var11 = var8.length;

         String var9;
         String[] var13;
         for(int var10 = 0; var10 < var11; ++var10) {
            var9 = var12[var10];
            var13 = var9.split(":");
            if (var13.length == 6) {
               this.chests.put(new Location(var18, (double)Integer.valueOf(var13[0]), (double)Integer.valueOf(var13[1]), (double)Integer.valueOf(var13[2])), var13[5].toLowerCase());
            }
         }

         this.signs = new HashMap();
         if (var17.getConfigurationSection("Signs") != null && !var17.getConfigurationSection("Signs").getKeys(false).isEmpty()) {
            Iterator var20 = var17.getConfigurationSection("Signs").getKeys(false).iterator();

            while(var20.hasNext()) {
               var9 = (String)var20.next();
               var11 = Integer.valueOf(var9);
               Location var22 = var1.getLocationFromString(var17.getString("Signs." + var9));
               this.signs.put(var22, var11);
            }
         }

         this.blocks = new HashMap();
         String[] var19 = (YamlConfiguration.loadConfiguration(new File(var1.getDataFolder() + "/arenas/" + var2, "blocks.dat")).getString("Blocks").replace("[", "").replace("]", "") + ", " + var17.getString("Chests").replace("[", "").replace("]", "")).split(", ");
         var13 = var19;
         int var24 = var19.length;

         String var21;
         for(var11 = 0; var11 < var24; ++var11) {
            var21 = var13[var11];
            String[] var14 = var21.split(":");
            this.blocks.put(new Arena.CLocation(Integer.valueOf(var14[0]), Integer.valueOf(var14[1]), Integer.valueOf(var14[2]), (Arena.CLocation)null), new Arena.CBlock(Integer.valueOf(var14[3]), var14.length > 4 ? Byte.valueOf(var14[4]) : 0, (Arena.CBlock)null));
         }

         this.players = new HashMap();
         this.killers = new HashMap();
         this.tasks = new BukkitTask[3];
         this.spectators = new ArrayList();
         this.state = this.enabled ? Enums.ArenaState.WAITING : Enums.ArenaState.DISABLED;
         this.date = ChatColor.GRAY + (new SimpleDateFormat("dd/MM/yyyy")).format(new Date());
         this.scoreboard = new CustomScoreboard(var1, true, var1.customization.scoreboard_title, new String[]{var1.customization.scoreboard_header, (String)var1.customization.scoreboard.get("Time"), String.valueOf(this.lobbyCountdown), " ", (String)var1.customization.scoreboard.get("Players"), String.valueOf(this.players.size()), " ", (String)var1.customization.scoreboard.get("Arena"), var2, " ", (String)var1.customization.scoreboard.get("Mode"), this.getMode(), " ", this.date, var1.customization.scoreboard_footer});
         this.teams = new HashMap();
         if (var17.getConfigurationSection("Spawnpoints") != null && !var17.getConfigurationSection("Spawnpoints").getKeys(false).isEmpty()) {
            Iterator var23 = var17.getConfigurationSection("Spawnpoints").getKeys(false).iterator();

            while(var23.hasNext()) {
               var21 = (String)var23.next();
               this.registerTeam(var21, var1.getLocationFromString(var17.getString("Spawnpoints." + var21)));
            }
         }

         this.createEditor();
         var1.arenas.put(var2.toLowerCase(), this);
      } catch (Exception var15) {
         Bukkit.getConsoleSender().sendMessage(var1.customization.prefix + "Couldn't load the arena " + var2 + ", please check for missing arena files or incorrect settings!");
         var15.printStackTrace();
      }

   }

   public void updateItem(int var1) {
      int var2 = this.players.size() - this.spectators.size();
      int var3 = this.teamSize * this.maxTeams;
      int var4 = this.state.equals(Enums.ArenaState.WAITING) ? 5 : (this.state.equals(Enums.ArenaState.STARTING) ? 4 : 14);
      this.plugin.arenaSelector.setItem(this.id, this.slot, (new ItemStackBuilder(Material.STAINED_CLAY)).setName(ChatColor.GREEN + this.name).addLore((String)this.plugin.customization.lores.get("Lines"), ((String)this.plugin.customization.lores.get("Players")).replace("%players%", String.valueOf(var2)).replace("%maxplayers%", String.valueOf(var3)), ((String)this.plugin.customization.lores.get("Teams")).replace("%teams%", String.valueOf(this.getAliveTeams().size())).replace("%maxteams%", String.valueOf(this.maxTeams)), ((String)this.plugin.customization.lores.get("Type")).replace("%type%", this.getMode()), ((String)this.plugin.customization.lores.get("State")).replace("%state%", this.state + (var1 != 0 ? ChatColor.LIGHT_PURPLE + " - " + this.plugin.getPercentageString(var1) : "")), (String)this.plugin.customization.lores.get("Lines")).setDurability(var4).build());
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
         Block var8 = var7.getBlock().getRelative(((org.bukkit.material.Sign)var7.getData()).getAttachedFace());
         var8.setTypeIdAndData(this.plugin.config.blockBehindSigns, (byte)var4, false);
      }
   }

   public String getMode() {
      return ChatColor.LIGHT_PURPLE + (this.teamSize == 1 ? "Solo" : "Teams (" + this.teamSize + ")");
   }

   public void createEditor() {
      this.editor = Bukkit.createInventory((InventoryHolder)null, 54, ChatColor.RED + "Editing: " + ChatColor.BLUE + this.name);
      this.plugin.cageInventory(this.editor, true);

      int var1;
      for(var1 = 0; var1 < 5; ++var1) {
         this.editor.setItem(var1, this.plugin.plus_itemstack);
      }

      for(var1 = 18; var1 < 23; ++var1) {
         this.editor.setItem(var1, this.plugin.minus_itemstack);
      }

      ItemStackBuilder var6 = new ItemStackBuilder(Material.PAPER);
      this.editor.setItem(9, var6.setName(ChatColor.YELLOW + "Team size: " + ChatColor.GOLD + this.teamSize).build());
      this.editor.setItem(10, var6.setName(ChatColor.YELLOW + "Min teams: " + ChatColor.GOLD + this.minTeams).build());
      this.editor.setItem(11, var6.setName(ChatColor.YELLOW + "Max teams: " + ChatColor.GOLD + this.maxTeams).build());
      this.editor.setItem(12, var6.setName(ChatColor.YELLOW + "Lobby countdown: " + ChatColor.GOLD + this.lobbyCountdown).build());
      this.editor.setItem(13, var6.setName(ChatColor.YELLOW + "Game length: " + ChatColor.GOLD + this.gameLength).build());
      String[] var2 = this.cuboid.toString().split(", ");
      String var3 = ChatColor.AQUA + "- " + ChatColor.YELLOW;
      int var4 = (int)(this.plugin.filesManager.getSize(new File(this.plugin.getDataFolder() + "/arenas", this.name)) / 1000L);
      ItemStack var5 = (new ItemStackBuilder(Material.CHEST)).setName(ChatColor.YELLOW + "Information").addLore(var3 + "World: " + ChatColor.GOLD + var2[0], var3 + "Bottom corner: " + ChatColor.GOLD + var2[1] + ", " + var2[2] + ", " + var2[3], var3 + "Top corner: " + ChatColor.GOLD + var2[4] + ", " + var2[5] + ", " + var2[6], var3 + "Blocks: " + ChatColor.GOLD + this.cuboid.getSize(), var3 + "Files size: " + ChatColor.GOLD + var4 + " kb", var3 + "Size type: " + (var4 < 501 ? ChatColor.GREEN + "Small" : (var4 < 1001 ? ChatColor.YELLOW + "Medium" : ChatColor.RED + "Large"))).build();
      this.editor.setItem(37, var5);
      this.editor.setItem(40, (new ItemStackBuilder(Material.INK_SACK)).setName(this.enabled ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled").setDurability(this.enabled ? 10 : 8).build());
      this.editor.setItem(43, this.plugin.save_itemstack);
   }

   public void registerTeam(String var1, Location var2) {
      Team var3 = this.scoreboard.registerTeam(var1);
      var3.setPrefix(this.plugin.config.teamColorRandomizerEnabled && this.plugin.config.teamColorRandomizerColors.length > 0 ? this.plugin.config.teamColorRandomizerColors[this.plugin.r.nextInt(this.plugin.config.teamColorRandomizerColors.length)].toString() : this.plugin.config.defaultTeamColor.toString());
      var3.setAllowFriendlyFire(false);
      this.teams.put(var3, new Arena.TeamData(var2));
   }

   public void cancelTasks() {
      for(int var1 = 0; var1 < this.tasks.length; ++var1) {
         this.cancelTask(var1);
      }

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

   public List getPlayers() {
      return this.plugin.getPlayers(this.players.keySet());
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

   public void destroyCage(Team var1) {
      Set var2 = (this.teamSize > 1 ? ((Arena.TeamData)this.teams.get(var1)).largeCage : ((Arena.TeamData)this.teams.get(var1)).smallCage).keySet();
      Iterator var4 = var2.iterator();

      while(var4.hasNext()) {
         Location var3 = (Location)var4.next();
         if (!var3.getBlock().getType().equals(Material.AIR)) {
            var3.getBlock().setType(Material.AIR);
         }
      }

   }

   public void kill(Player var1, String var2) {
      this.spectators.add(var1.getName());
      var1.teleport(this.spectatorsLocation != null ? this.spectatorsLocation : this.cuboid.getRandomLocation());
      this.scoreboard.update((String)this.plugin.customization.scoreboard.get("Players"), this.players.size() - this.spectators.size(), true);
      Iterator var4 = this.getPlayers().iterator();

      while(var4.hasNext()) {
         Player var3 = (Player)var4.next();
         var3.sendMessage(var2);
         var3.hidePlayer(var1);
      }

      Team var7 = (Team)this.players.get(var1.getName());
      var7.removePlayer(var1);
      String var8 = ((String)this.plugin.customization.messages.get("Team-Player-Eliminate")).replace("%player%", var1.getName()).replace("%teamsize%", String.valueOf(var7.getSize())).replace("%maxteamsize%", String.valueOf(this.teamSize));
      Iterator var6 = var7.getPlayers().iterator();

      while(var6.hasNext()) {
         OfflinePlayer var5 = (OfflinePlayer)var6.next();
         if (var5 instanceof Player) {
            ((Player)var5).sendMessage(var8);
         }
      }

      if (var7.getSize() == 0) {
         this.checkFinish();
      }

   }

   public void checkFinish() {
      if (this.getAliveTeams().size() < 2) {
         this.finish();
      }

   }

   public void sendWarning(Player var1) {
      PlayerData var2 = (PlayerData)this.plugin.playerData.get(var1.getName());
      ++var2.warnings;
      var1.teleport(this.spectatorsLocation != null ? this.spectatorsLocation : this.cuboid.getRandomLocation());
      var1.sendMessage(((String)this.plugin.customization.messages.get("Warning-Receive")).replace("%warnings%", String.valueOf(var2.warnings)).replace("%maxwarnings%", String.valueOf(this.plugin.config.maxWarnings)));
      if (var2.warnings >= this.plugin.config.maxWarnings) {
         var1.sendMessage((String)this.plugin.customization.messages.get("Warning-Exceed"));
         this.leave(var1);
      }

   }

   public List getEnemeies(Team var1) {
      ArrayList var2 = new ArrayList();
      Iterator var4 = this.getPlayers().iterator();

      while(var4.hasNext()) {
         Player var3 = (Player)var4.next();
         if (!this.spectators.contains(var3.getName()) && !var1.hasPlayer(var3)) {
            var2.add(var3);
         }
      }

      return var2;
   }

   public List getTopKillers() {
      while(this.killers.size() < 3) {
         this.killers.put("NONE" + (this.plugin.r.nextInt(5) + 1), 0);
      }

      LinkedList var1 = new LinkedList(this.killers.entrySet());
      Collections.sort(var1, new Comparator() {
         public int compare(Entry var1, Entry var2) {
            return (Integer)var2.getValue() - (Integer)var1.getValue();
         }
      });
      return var1;
   }

   public void fillChests() {
      HashMap var1 = (HashMap)this.plugin.chests.get(this.votesManager.getVoted("Chests").equals("Default") ? "Normal" : this.votesManager.getVoted("Chests"));
      Iterator var3 = this.chests.keySet().iterator();

      while(true) {
         Chest var4;
         ChestType var6;
         do {
            Location var2;
            do {
               if (!var3.hasNext()) {
                  return;
               }

               var2 = (Location)var3.next();
            } while(!var2.getBlock().getType().equals(Material.CHEST));

            var4 = (Chest)var2.getBlock().getState();
            var4.getInventory().clear();
            String var5 = (String)this.chests.get(var2);
            var6 = var1.containsKey(var5) ? (ChestType)var1.get(var5) : (ChestType)var1.get("default");
         } while(var6.items.isEmpty());

         int var7 = this.plugin.r.nextInt(var6.maxItems - var6.minItems) + var6.minItems + (this.plugin.r.nextBoolean() ? 1 : 0);

         for(int var8 = 0; var8 < var7; ++var8) {
            int var9 = 0;

            for(int var10 = 0; var10 < this.plugin.config.chestChecks_slotOverwrite && var4.getInventory().getItem(var9 = this.plugin.r.nextInt(var4.getInventory().getSize())) != null; ++var10) {
            }

            ItemStack var12 = null;

            for(int var11 = 0; var11 < this.plugin.config.chestChecks_itemDuplicate && var4.getInventory().contains((var12 = (ItemStack)var6.items.get(this.plugin.r.nextInt(var6.items.size()))).getType()); ++var11) {
            }

            var4.getInventory().setItem(var9, var12);
         }
      }
   }

   public int getHighest(Collection var1, int var2) {
      int var3 = 0;
      Iterator var5 = var1.iterator();

      while(var5.hasNext()) {
         int var4 = (Integer)var5.next();
         if (var4 > var3 && var4 < var2) {
            var3 = var4;
         }
      }

      return var3;
   }

   public String getEvent(int var1) {
      Iterator var3 = this.refillTimes.iterator();

      while(var3.hasNext()) {
         int var2 = (Integer)var3.next();
         if (var2 == var1) {
            return "Refill";
         }
      }

      return var1 == 0 ? "End" : "NONE";
   }

   public void join(Player var1) {
      if (!this.state.AVAILABLE()) {
         if (this.state.equals(Enums.ArenaState.INGAME) && this.plugin.lobbyPlayers.contains(var1.getName()) && this.plugin.config.allowSpectatorJoin) {
            this.joinSpectator(var1);
         } else {
            var1.sendMessage((String)this.plugin.customization.messages.get("Arena-Not-Available"));
         }

      } else if (this.teams.size() < this.maxTeams) {
         var1.sendMessage((String)this.plugin.customization.messages.get("Arena-Not-Enough-Spawnpoints"));
      } else {
         PlayerData var2 = (PlayerData)this.plugin.playerData.get(var1.getName());
         if (!this.plugin.lobbyPlayers.contains(var1.getName())) {
            if (var2.arena == null || !var2.arena.spectators.contains(var1.getName())) {
               var1.sendMessage((String)this.plugin.customization.messages.get("Not-In-Lobby"));
               return;
            }

            var2.arena.leave(var1);
         }

         if (var2.party != null) {
            if (!var2.party.leaderName.equals(var1.getName())) {
               var1.sendMessage((String)this.plugin.customization.messages.get("Party-Must-Be-Leader"));
               return;
            }

            if (var2.party.players.size() + this.players.size() > this.teamSize * this.maxTeams) {
               var1.sendMessage((String)this.plugin.customization.messages.get("Party-Join-Arena-No-Space"));
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

         Object var15 = new ArrayList();
         if (var2.party == null) {
            ((List)var15).add(var1);
         } else {
            var15 = this.plugin.getPlayers(var2.party.players);
         }

         Team var16 = this.getAvailableTeam(((List)var15).size());
         Iterator var6 = ((List)var15).iterator();

         while(var6.hasNext()) {
            Player var5 = (Player)var6.next();
            Team var7 = var16 == null ? this.getAvailableTeam(1) : var16;
            if (var7 == null) {
               var1.sendMessage((String)this.plugin.customization.messages.get("Arena-Full"));
               return;
            }

            PlayerData var8 = (PlayerData)this.plugin.playerData.get(var5.getName());
            Arena.TeamData var9 = (Arena.TeamData)this.teams.get(var7);
            if (var7.getSize() == 0) {
               Cage var10 = this.plugin.cages.containsKey(var2.selectedCage) ? (Cage)this.plugin.cages.get(var2.selectedCage) : (Cage)this.plugin.cages.get("default");
               var10.build(this.teamSize > 1 ? var9.largeCage : var9.smallCage);
            }

            var7.addPlayer(var5);
            this.scoreboard.apply(var5);
            this.players.put(var5.getName(), var7);
            var8.arena = this;
            var8.lobbyScoreboard = null;
            this.plugin.lobbyPlayers.remove(var5.getName());
            var5.teleport(var9.spawnpoint);
            var5.getInventory().clear();
            if (this.plugin.config.hotbarItems.containsKey("Profile")) {
               var5.getInventory().setItem((Integer)this.plugin.config.hotbarItems.get("Profile") - 1, this.plugin.profile_itemstack);
            }

            if (this.plugin.config.hotbarItems.containsKey("Quit")) {
               var5.getInventory().setItem((Integer)this.plugin.config.hotbarItems.get("Quit") - 1, this.plugin.quit_itemstack);
            }

            if (this.plugin.config.hotbarItems.containsKey("Vote")) {
               var5.getInventory().setItem((Integer)this.plugin.config.hotbarItems.get("Vote") - 1, this.plugin.vote_itemstack);
            }

            if (this.plugin.config.hotbarItems.containsKey("Inventory")) {
               var5.getInventory().setItem((Integer)this.plugin.config.hotbarItems.get("Inventory") - 1, this.plugin.inventory_itemstack);
            }

            var5.updateInventory();
            List var17 = this.getPlayers();
            String var11 = ((String)this.plugin.customization.messages.get("Player-Join-Arena")).replace("%player%", var5.getName()).replace("%players%", String.valueOf(this.players.size())).replace("%maxplayers%", String.valueOf(this.teamSize * this.maxTeams));
            Iterator var13 = var17.iterator();

            while(var13.hasNext()) {
               Player var12 = (Player)var13.next();
               var12.sendMessage(var11);
            }

            String var18 = ((String)this.plugin.customization.messages.get("Player-Join-Team")).replace("%player%", var5.getName()).replace("%teamsize%", String.valueOf(var7.getSize())).replace("%maxteamsize%", String.valueOf(this.teamSize));
            Iterator var14 = var7.getPlayers().iterator();

            while(var14.hasNext()) {
               OfflinePlayer var19 = (OfflinePlayer)var14.next();
               if (var5 instanceof Player && !var19.getName().equals(var5.getName())) {
                  ((Player)var19).sendMessage(var18);
               }
            }

            Bukkit.getPluginManager().callEvent(new SWPlayerJoinArenaEvent(var5, this.name, this.cuboid.worldName, var17));
         }

         this.scoreboard.update((String)this.plugin.customization.scoreboard.get("Players"), this.players.size(), true);
         if (this.getAliveTeams().size() >= this.minTeams && this.tasks[0] == null) {
            this.countDown();
         }

         this.updateItem(0);
      }
   }

   public void joinSpectator(Player var1) {
      PlayerData var2 = (PlayerData)this.plugin.playerData.get(var1.getName());
      var1.getInventory().clear();
      Iterator var4 = this.getPlayers().iterator();

      while(var4.hasNext()) {
         Player var3 = (Player)var4.next();
         var3.hidePlayer(var1);
      }

      var2.arena = this;
      this.plugin.lobbyPlayers.remove(var1.getName());
      if (!this.plugin.protectedPlayers.contains(var1.getName())) {
         this.plugin.protectedPlayers.add(var1.getName());
      }

      this.spectators.add(var1.getName());
      this.players.put(var1.getName(), (Object)null);
      var2.lobbyScoreboard = null;
      var2.warnings = 0;
      this.scoreboard.apply(var1);
      var1.teleport(this.spectatorsLocation != null ? this.spectatorsLocation : this.cuboid.getRandomLocation());
      var2.makeSpectator(var1);
      this.updateItem(0);
      var1.sendMessage((String)this.plugin.customization.messages.get("Player-Spectate"));
      TitleObject var5 = this.plugin.config.titles_enabled ? (new TitleObject((String)this.plugin.customization.titles.get("Player-Spectate"), TitleType.TITLE)).setFadeIn(this.plugin.config.titles_fadeIn).setStay(this.plugin.config.titles_stay).setFadeOut(this.plugin.config.titles_fadeOut) : null;
      if (var5 != null) {
         var5.send(var1);
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
      List var15;
      for(Iterator var5 = ((List)var3).iterator(); var5.hasNext(); Bukkit.getPluginManager().callEvent(new SWPlayerLeaveArenaEvent(var4, this.name, this.cuboid.worldName, var15))) {
         var4 = (Player)var5.next();
         PlayerData var6 = (PlayerData)this.plugin.playerData.get(var4.getName());
         var6.arena = null;
         Team var7 = (Team)this.players.get(var4.getName());
         if (var7 != null && var7.getPlayers().contains(var4)) {
            var7.removePlayer(var4);
         }

         this.players.remove(var4.getName());
         if (!this.state.AVAILABLE()) {
            ItemStack[] var11;
            int var10 = (var11 = var4.getInventory().getContents()).length;

            for(int var9 = 0; var9 < var10; ++var9) {
               ItemStack var8 = var11[var9];
               if (var8 != null) {
                  var4.getWorld().dropItemNaturally(var4.getLocation(), var8);
               }
            }
         }

         if (!this.plugin.protectedPlayers.contains(var4.getName())) {
            this.plugin.protectedPlayers.add(var4.getName());
         }

         this.plugin.lobbyPlayers.add(var4.getName());
         var4.teleport(this.plugin.lobbyLocation);
         var6.clearPlayer(var4);
         var4.sendMessage((String)this.plugin.customization.messages.get("Player-Leave"));
         Iterator var16 = this.plugin.getPlayers(this.spectators).iterator();

         while(var16.hasNext()) {
            Player var14 = (Player)var16.next();
            var4.showPlayer(var14);
         }

         var15 = this.getPlayers();
         if (this.spectators.contains(var4.getName())) {
            Iterator var21 = var15.iterator();

            while(var21.hasNext()) {
               Player var18 = (Player)var21.next();
               var18.showPlayer(var4);
            }

            this.spectators.remove(var4.getName());
            return;
         }

         String var17 = ((String)this.plugin.customization.messages.get("Player-Leave-Arena")).replace("%player%", var4.getName()).replace("%players%", String.valueOf(this.players.size() - this.spectators.size())).replace("%maxplayers%", String.valueOf(this.teamSize * this.maxTeams));
         Iterator var22 = var15.iterator();

         while(var22.hasNext()) {
            Player var19 = (Player)var22.next();
            var19.sendMessage(var17);
         }

         String var20 = ((String)this.plugin.customization.messages.get("Player-Leave-Team")).replace("%player%", var4.getName()).replace("%teamsize%", String.valueOf(var7.getSize())).replace("%maxteamsize%", String.valueOf(this.teamSize));
         Iterator var12 = var7.getPlayers().iterator();

         while(var12.hasNext()) {
            OfflinePlayer var23 = (OfflinePlayer)var12.next();
            if (var23 instanceof Player) {
               ((Player)var23).sendMessage(var20);
            }
         }

         if (this.state.AVAILABLE()) {
            this.votesManager.removeVotes(var4);
            if (var7.getSize() == 0) {
               this.destroyCage(var7);
            }

            if (this.tasks[0] != null && this.players.size() < this.teamSize * this.minTeams) {
               this.cancelTask(0);
               this.state = Enums.ArenaState.WAITING;
               String var24 = (String)this.plugin.customization.messages.get("Countdown-Cancel");
               Iterator var13 = var15.iterator();

               while(var13.hasNext()) {
                  Player var25 = (Player)var13.next();
                  var25.sendMessage(var24);
               }

               this.scoreboard.update((String)this.plugin.customization.scoreboard.get("Time"), this.lobbyCountdown, true);
            }
         } else if (var7.getSize() == 0) {
            this.checkFinish();
         }
      }

      this.scoreboard.update((String)this.plugin.customization.scoreboard.get("Players"), this.players.size() - this.spectators.size(), true);
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

            Arena.this.scoreboard.update((String)Arena.this.plugin.customization.scoreboard.get("Time"), this.seconds, true);
            if (this.seconds == 0) {
               Arena.this.start();
            }

            --this.seconds;
         }
      }).runTaskTimer(this.plugin, 0L, 20L);
   }

   public void start() {
      this.state = Enums.ArenaState.INGAME;
      this.cancelTask(0);
      Iterator var2 = this.teams.keySet().iterator();

      while(var2.hasNext()) {
         Team var1 = (Team)var2.next();
         this.destroyCage(var1);
      }

      String var16 = (String)this.plugin.customization.messages.get("Arena-Start");
      Iterator var3 = this.plugin.config.executed_commands_arena_start.iterator();

      while(var3.hasNext()) {
         String var17 = (String)var3.next();
         Bukkit.dispatchCommand(Bukkit.getConsoleSender(), var17.replace("%world%", this.cuboid.worldName));
      }

      TitleObject var18 = this.plugin.config.titles_enabled ? (new TitleObject((String)this.plugin.customization.titles.get("Start"), TitleType.TITLE)).setFadeIn(this.plugin.config.titles_fadeIn).setStay(this.plugin.config.titles_stay).setFadeOut(this.plugin.config.titles_fadeOut) : null;
      String var19 = this.votesManager.getVoted("Time");
      if (var19 == "Default") {
         var19 = "Noon";
      }

      World var4 = Bukkit.getWorld(this.cuboid.worldName);
      if (var19.equals("Sunrise")) {
         var4.setTime(0L);
      } else if (var19.equals("Noon")) {
         var4.setTime(2000L);
      } else if (var19.equals("Sunset")) {
         var4.setTime(12000L);
      } else if (var19.equals("Midnight")) {
         var4.setTime(15000L);
      }

      String var5 = this.votesManager.getVoted("Health");
      int var6 = (var5.equals("Default") ? 10 : Integer.valueOf(ChatColor.stripColor(var5.split(" ")[0]))) * 2;
      String[] var7 = this.plugin.config.hotbarItems.containsKey("Vote") ? new String[]{ChatColor.DARK_AQUA + "======== " + ChatColor.AQUA + "Arena Settings" + ChatColor.DARK_AQUA + " =======", ChatColor.AQUA + " - " + ChatColor.GOLD + "Time: " + ChatColor.YELLOW + ChatColor.BOLD + var19, ChatColor.AQUA + " - " + ChatColor.GOLD + "Chests: " + ChatColor.YELLOW + ChatColor.BOLD + (this.votesManager.getVoted("Chests").equals("Default") ? "Normal" : this.votesManager.getVoted("Chests")), ChatColor.AQUA + " - " + ChatColor.GOLD + "Health: " + ChatColor.YELLOW + ChatColor.BOLD + var5, ChatColor.DARK_AQUA + "============================"} : new String[0];
      List var8 = this.getPlayers();
      Iterator var10 = var8.iterator();

      while(var10.hasNext()) {
         Player var9 = (Player)var10.next();
         PlayerData var11 = (PlayerData)this.plugin.playerData.get(var9.getName());
         var9.sendMessage(var16);
         var9.closeInventory();
         this.plugin.protectedPlayers.remove(var9.getName());
         var11.warnings = 0;
         var9.setMaxHealth((double)var6);
         var9.setHealth(var9.getMaxHealth());
         var9.getInventory().clear();
         var9.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 5));
         Iterator var13 = this.potionEffects.iterator();

         while(var13.hasNext()) {
            PotionEffect var12 = (PotionEffect)var13.next();
            var9.addPotionEffect(var12);
         }

         String[] var15 = var7;
         int var14 = var7.length;

         for(int var22 = 0; var22 < var14; ++var22) {
            String var20 = var15[var22];
            var9.sendMessage(var20);
         }

         if (this.plugin.kits.containsKey(var11.selectedKit)) {
            Kit var21 = (Kit)this.plugin.kits.get(var11.selectedKit);
            var21.apply(var9);
            var9.sendMessage(((String)this.plugin.customization.messages.get("Kit-Receive")).replace("%kit%", var21.name));
         }

         if (var18 != null) {
            var18.send(var9);
         }
      }

      this.scoreboard.update((String)this.plugin.customization.scoreboard.get("Time"), (String)this.plugin.customization.scoreboard.get("Next-Event"), false);
      this.tasks[1] = (new BukkitRunnable() {
         int seconds;
         int nextEvent;
         String eventName;

         {
            this.seconds = Arena.this.gameLength;
            this.nextEvent = Arena.this.getHighest(Arena.this.refillTimes, this.seconds);
            this.eventName = Arena.this.getEvent(this.nextEvent);
         }

         public void run() {
            Arena.this.scoreboard.update((String)Arena.this.plugin.customization.scoreboard.get("Next-Event"), ChatColor.GREEN + String.format(this.eventName + " %02d:%02d", (this.seconds - this.nextEvent) / 60, (this.seconds - this.nextEvent) % 60), true);
            Iterator var2;
            if (this.seconds == this.nextEvent) {
               if (this.seconds == 0) {
                  String var6 = (String)Arena.this.plugin.customization.messages.get("Game-Cancel");
                  Iterator var8 = Arena.this.getPlayers().iterator();

                  while(var8.hasNext()) {
                     Player var7 = (Player)var8.next();
                     var7.sendMessage(var6);
                  }

                  Arena.this.stop(true);
                  return;
               }

               var2 = Arena.this.refillTimes.iterator();

               label55:
               while(var2.hasNext()) {
                  int var1 = (Integer)var2.next();
                  if (this.seconds == var1) {
                     Arena.this.fillChests();
                     Iterator var4 = Arena.this.getPlayers().iterator();

                     while(true) {
                        if (!var4.hasNext()) {
                           break label55;
                        }

                        Player var3 = (Player)var4.next();
                        var3.sendMessage((String)Arena.this.plugin.customization.messages.get("Chests-Refill"));
                     }
                  }
               }

               this.nextEvent = Arena.this.getHighest(Arena.this.refillTimes, this.seconds);
               this.eventName = Arena.this.getEvent(this.nextEvent);
            }

            var2 = Arena.this.getPlayers().iterator();

            while(var2.hasNext()) {
               Player var5 = (Player)var2.next();
               if (!Arena.this.cuboid.contains(var5.getLocation())) {
                  if (Arena.this.spectators.contains(var5.getName())) {
                     var5.sendMessage((String)Arena.this.plugin.customization.messages.get("Arena-Borders-Leave"));
                     var5.teleport(Arena.this.spectatorsLocation != null ? Arena.this.spectatorsLocation : Arena.this.cuboid.getRandomLocation());
                  } else if (var5.getLocation().getBlockY() >= Arena.this.cuboid.getLowerY()) {
                     var5.sendMessage((String)Arena.this.plugin.customization.messages.get("Arena-Borders-Outside"));
                     var5.damage(2.0D);
                  }
               }
            }

            --this.seconds;
         }
      }).runTaskTimer(this.plugin, 0L, 20L);
      this.fillChests();
      Bukkit.getPluginManager().callEvent(new SWArenaStartEvent(this.name, this.cuboid.worldName, var8));
      this.updateItem(0);
   }

   public void finish() {
      this.cancelTasks();
      this.state = Enums.ArenaState.ENDING;
      this.scoreboard.update((String)this.plugin.customization.scoreboard.get("Next-Event"), ChatColor.GREEN + "Game Ended", true);
      List var1 = this.getAliveTeams();
      if (var1.isEmpty()) {
         this.stop(true);
      } else {
         List var2 = this.getPlayers();
         final ArrayList var3 = new ArrayList();
         Team var4 = (Team)var1.get(0);
         Iterator var6 = var2.iterator();

         while(var6.hasNext()) {
            Player var5 = (Player)var6.next();
            if (this.players.get(var5.getName()) != null && ((Team)this.players.get(var5.getName())).equals(var4)) {
               var3.add(var5.getName());
            }
         }

         List var16 = this.getTopKillers();
         ChatColor var17 = this.plugin.colors[this.plugin.r.nextInt(this.plugin.colors.length)];
         String[] var7 = new String[]{"" + var17 + ChatColor.STRIKETHROUGH + "===============================", " ", "    " + ChatColor.YELLOW + Enums.SPECIAL_CHARACTER.STAR + " " + ChatColor.GREEN + var3.toString().replace("[", "").replace("]", "") + ChatColor.GRAY + " won the game! " + ChatColor.YELLOW + Enums.SPECIAL_CHARACTER.STAR, " ", ChatColor.GOLD + "       1st Killer - " + (String)((Entry)var16.get(0)).getKey() + " - " + ((Entry)var16.get(0)).getValue(), ChatColor.YELLOW + "        2nd Killer - " + (String)((Entry)var16.get(1)).getKey() + " - " + ((Entry)var16.get(1)).getValue(), ChatColor.RED + "         3rd Killer - " + (String)((Entry)var16.get(2)).getKey() + " - " + ((Entry)var16.get(2)).getValue(), " ", "" + var17 + ChatColor.STRIKETHROUGH + "==============================="};
         Iterator var9 = var2.iterator();

         while(var9.hasNext()) {
            Player var8 = (Player)var9.next();
            String[] var13 = var7;
            int var12 = var7.length;

            for(int var11 = 0; var11 < var12; ++var11) {
               String var10 = var13[var11];
               var8.sendMessage(var10);
            }
         }

         String var18 = (String)this.plugin.customization.messages.get("Arena-Finish");
         TitleObject var19 = this.plugin.config.titles_enabled ? (new TitleObject((String)this.plugin.customization.titles.get("Finish"), TitleType.TITLE)).setFadeIn(this.plugin.config.titles_fadeIn).setStay(this.plugin.config.titles_stay).setFadeOut(this.plugin.config.titles_fadeOut) : null;
         Iterator var21 = this.plugin.getPlayers(var3).iterator();

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
            Iterator var15 = this.plugin.config.executed_commands_player_win.iterator();

            while(var15.hasNext()) {
               String var14 = (String)var15.next();
               Bukkit.dispatchCommand(Bukkit.getConsoleSender(), var14.replace("%player%", var20.getName()).replace("%world%", this.cuboid.worldName));
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
                  Iterator var2 = Arena.this.plugin.getPlayers(var3).iterator();

                  while(var2.hasNext()) {
                     Player var1 = (Player)var2.next();
                     Arena.this.plugin.fireWorkEffect(var1, true);
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
         Bukkit.getPluginManager().callEvent(new SWArenaFinishEvent(this.name, this.cuboid.worldName, var2));
         this.updateItem(0);
      }
   }

   public void stop(boolean var1) {
      List var2 = this.getPlayers();
      Bukkit.getPluginManager().callEvent(new SWArenaFinishEvent(this.name, this.cuboid.worldName, var2));
      Player var4;
      Iterator var5;
      if (this.plugin.isOneGamePerServer()) {
         ByteArrayDataOutput var9 = ByteStreams.newDataOutput();
         var9.writeUTF("Connect");
         var9.writeUTF(this.plugin.config.bungee_mode_hub);
         var5 = var2.iterator();

         while(var5.hasNext()) {
            var4 = (Player)var5.next();
            var4.sendPluginMessage(this.plugin, "BungeeCord", var9.toByteArray());
         }

         (new BukkitRunnable() {
            public void run() {
               Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Arena.this.plugin.getConfig().getString("Bungee-Mode.restart-command"));
            }
         }).runTaskLater(this.plugin, 20L);
      } else {
         this.cancelTasks();
         List var3 = this.plugin.getPlayers(this.spectators);
         var5 = var2.iterator();

         while(var5.hasNext()) {
            var4 = (Player)var5.next();
            PlayerData var6 = (PlayerData)this.plugin.playerData.get(var4.getName());
            this.plugin.lobbyPlayers.add(var4.getName());
            if (!this.plugin.protectedPlayers.contains(var4.getName())) {
               this.plugin.protectedPlayers.add(var4.getName());
            }

            var4.teleport(this.plugin.lobbyLocation);
            var6.clearPlayer(var4);
            var6.arena = null;
            Iterator var8 = var3.iterator();

            while(var8.hasNext()) {
               Player var7 = (Player)var8.next();
               var4.showPlayer(var7);
            }
         }

         var5 = this.teams.keySet().iterator();

         while(var5.hasNext()) {
            Team var10 = (Team)var5.next();
            Iterator var12 = var10.getPlayers().iterator();

            while(var12.hasNext()) {
               OfflinePlayer var11 = (OfflinePlayer)var12.next();
               var10.removePlayer(var11);
            }
         }

         this.players.clear();
         this.spectators.clear();
         this.killers.clear();
         this.votesManager.reset();
         this.scoreboard.update((String)this.plugin.customization.scoreboard.get("Next-Event"), (String)this.plugin.customization.scoreboard.get("Time"), false);
         this.scoreboard.update((String)this.plugin.customization.scoreboard.get("Time"), this.lobbyCountdown, true);
         this.scoreboard.update(this.date, this.date = ChatColor.GRAY + (new SimpleDateFormat("dd/MM/yyyy")).format(new Date()), false);
         if (var1 && !this.state.AVAILABLE()) {
            this.plugin.rollbackManager.add(this);
         } else {
            this.state = this.enabled ? Enums.ArenaState.WAITING : Enums.ArenaState.DISABLED;
         }

         this.updateItem(0);
      }
   }

   public void rollback() {
      this.state = Enums.ArenaState.ROLLBACKING;
      final World var1 = Bukkit.getWorld(this.cuboid.worldName);
      final Iterator var2 = this.cuboid.iterator();
      final double var3 = (double)this.cuboid.getSize();
      final int var5 = this.plugin.config.rollback_repair_speed;
      (new BukkitRunnable() {
         int checked = 0;
         int current = 0;
         int ticks = 0;

         public void run() {
            for(; var2.hasNext() && this.current < var5; ++this.current) {
               Block var1x = (Block)var2.next();
               Arena.CLocation var2x = Arena.this.new CLocation(var1x.getLocation(), (Arena.CLocation)null);
               if (Arena.this.blocks.containsKey(var2x)) {
                  Arena.CBlock var3x = (Arena.CBlock)Arena.this.blocks.get(var2x);
                  if (var3x.type != var1x.getTypeId() || var3x.data != var1x.getData()) {
                     var1x.setTypeIdAndData(var3x.type, var3x.data, false);
                  }
               } else if (var1x.getType() != Material.AIR) {
                  var1x.setType(Material.AIR);
               }
            }

            this.checked += this.current;
            this.current = 0;
            ++this.ticks;
            if (this.ticks == Arena.this.plugin.config.rollback_send_status_update_every) {
               int var4 = (int)((double)this.checked / var3 * 100.0D);
               Arena.this.updateItem(var4);
               this.ticks = 0;
            }

            if (!var2.hasNext()) {
               this.cancel();
               Iterator var6 = var1.getEntities().iterator();

               while(var6.hasNext()) {
                  Entity var5x = (Entity)var6.next();
                  if (Arena.this.cuboid.contains(var5x.getLocation()) && !var5x.getType().equals(EntityType.PLAYER)) {
                     var5x.remove();
                  }
               }

               Arena.this.state = Arena.this.enabled ? Enums.ArenaState.WAITING : Enums.ArenaState.DISABLED;
               Arena.this.updateItem(0);
            }

         }
      }).runTaskTimer(this.plugin, 10L, 1L);
   }

   private class CBlock {
      int type;
      byte data;

      private CBlock(int var2, byte var3) {
         this.type = var2;
         this.data = var3;
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
   }

   public class TeamData {
      Location spawnpoint;
      HashMap largeCage;
      HashMap smallCage;

      public TeamData(Location var2) {
         this.spawnpoint = var2;
         this.loadSmallCage();
         this.loadLargeCage();
      }

      private void loadSmallCage() {
         this.smallCage = new HashMap();
         Location var1 = this.spawnpoint.clone();
         this.smallCage.put(var1.add(0.0D, -1.0D, 0.0D), 0);

         for(int var2 = 1; var2 < 4; ++var2) {
            Iterator var4 = Arrays.asList(var1.clone().add(1.0D, (double)var2, 0.0D), var1.clone().add(-1.0D, (double)var2, 0.0D), var1.clone().add(0.0D, (double)var2, 1.0D), var1.clone().add(0.0D, (double)var2, -1.0D)).iterator();

            while(var4.hasNext()) {
               Location var3 = (Location)var4.next();
               this.smallCage.put(var3, var2);
            }
         }

         this.smallCage.put(var1.clone().add(0.0D, 4.0D, 0.0D), 4);
      }

      private void loadLargeCage() {
         this.largeCage = new HashMap();
         Location var1 = this.spawnpoint.clone();
         Iterator var3 = Arrays.asList(var1.add(0.0D, -1.0D, 0.0D), var1.clone().add(1.0D, 0.0D, 0.0D), var1.clone().add(-1.0D, 0.0D, 0.0D), var1.clone().add(0.0D, 0.0D, 1.0D), var1.clone().add(0.0D, 0.0D, -1.0D), var1.clone().add(1.0D, 0.0D, 1.0D), var1.clone().add(-1.0D, 0.0D, 1.0D), var1.clone().add(1.0D, 0.0D, -1.0D), var1.clone().add(-1.0D, 0.0D, -1.0D)).iterator();

         Location var2;
         while(var3.hasNext()) {
            var2 = (Location)var3.next();
            this.largeCage.put(var2, 0);
         }

         for(int var5 = 1; var5 < 4; ++var5) {
            Iterator var4 = Arrays.asList(var1.clone().add(2.0D, (double)var5, 0.0D), var1.clone().add(-2.0D, (double)var5, 0.0D), var1.clone().add(0.0D, (double)var5, 2.0D), var1.clone().add(0.0D, (double)var5, -2.0D), var1.clone().add(2.0D, (double)var5, 1.0D), var1.clone().add(2.0D, (double)var5, -1.0D), var1.clone().add(-2.0D, (double)var5, 1.0D), var1.clone().add(-2.0D, (double)var5, -1.0D), var1.clone().add(1.0D, (double)var5, 2.0D), var1.clone().add(-1.0D, (double)var5, 2.0D), var1.clone().add(1.0D, (double)var5, -2.0D), var1.clone().add(-1.0D, (double)var5, -2.0D)).iterator();

            while(var4.hasNext()) {
               Location var6 = (Location)var4.next();
               this.largeCage.put(var6, var5);
            }
         }

         var1 = var1.clone();
         var3 = Arrays.asList(var1.add(0.0D, 4.0D, 0.0D), var1.clone().add(1.0D, 0.0D, 0.0D), var1.clone().add(-1.0D, 0.0D, 0.0D), var1.clone().add(0.0D, 0.0D, 1.0D), var1.clone().add(0.0D, 0.0D, -1.0D), var1.clone().add(1.0D, 0.0D, 1.0D), var1.clone().add(-1.0D, 0.0D, 1.0D), var1.clone().add(1.0D, 0.0D, -1.0D), var1.clone().add(-1.0D, 0.0D, -1.0D)).iterator();

         while(var3.hasNext()) {
            var2 = (Location)var3.next();
            this.largeCage.put(var2, 4);
         }

      }
   }
}
