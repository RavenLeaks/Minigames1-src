package me.wazup.kitbattle;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;

public class PlayerData {
   boolean joined = false;
   private ItemStack[] items;
   private ItemStack[] armor;
   private Location location;
   private double health;
   private int food;
   private int levels;
   private float exp;
   private Scoreboard scoreboard;
   private Collection potions;
   private GameMode mode;
   private boolean flying;
   private Kitbattle plugin;
   private PlayingMap map;
   private Kit kit;
   SmartInventory kitsInventory;
   SmartInventory achievements;
   int killstreak = 0;
   int deathstreak = 0;
   HashMap damagers = new HashMap();
   private HashMap cooldowns = new HashMap();
   private BukkitTask abilityCooldownNotifier;
   CustomScoreboard customScoreboard;
   private int kills = 0;
   private int deaths = 0;
   private int dataexp = 0;
   private int projectiles_hit = 0;
   private int tournament_wins = 0;
   private int challenge_wins = 0;
   private int abilities_used = 0;
   private int soups_eaten = 0;
   private int killstreaks_earned = 0;
   int coins = 0;
   int kitUnlockers = 0;
   Rank rank;
   Rank nextRank;
   Effect selectedTrail;

   public PlayerData(Player var1, Kitbattle var2) {
      this.plugin = var2;
      this.loadStats(var1);
   }

   public void loadStats(final Player var1) {
      final String var2 = this.plugin.config.UUID ? var1.getUniqueId().toString() : var1.getName();
      (new BukkitRunnable() {
         public void run() {
            ArrayList var1x = new ArrayList();
            boolean var2x = false;
            String var4;
            if (PlayerData.this.plugin.config.useMySQL) {
               try {
                  Statement var3 = PlayerData.this.plugin.mysql.getConnection().createStatement();
                  var4 = PlayerData.this.plugin.config.tableprefix;
                  if (var3.executeQuery("SELECT * FROM " + var4 + " WHERE " + (PlayerData.this.plugin.config.UUID ? "player_uuid" : "player_name") + " = '" + var2 + "';").next()) {
                     ResultSet var5 = var3.executeQuery("SELECT * FROM " + var4 + " WHERE " + (PlayerData.this.plugin.config.UUID ? "player_uuid" : "player_name") + " = '" + var2 + "';");
                     var5.next();
                     PlayerData.this.coins = var5.getInt("Coins");
                     PlayerData.this.kills = var5.getInt("Kills");
                     PlayerData.this.deaths = var5.getInt("Deaths");
                     PlayerData.this.kitUnlockers = var5.getInt("Kitunlockers");
                     PlayerData.this.dataexp = var5.getInt("Exp");
                     String[] var6 = var5.getString("Kits").split(", ");

                     for(int var7 = 0; var7 < var6.length; ++var7) {
                        var1x.add(var6[var7].toLowerCase());
                     }

                     try {
                        PlayerData.this.loadStatisticsString(var5.getString("Statistics"));
                     } catch (Exception var8) {
                     }

                     var3.close();
                     var5.close();
                  } else {
                     PlayerData.this.resetPlayer(var1);
                     var2x = true;
                  }
               } catch (SQLException var9) {
                  var9.printStackTrace();
               }
            } else {
               FileConfiguration var10 = PlayerData.this.plugin.fileManager.getConfig("players.yml");
               if (var10.contains("Players." + var2)) {
                  PlayerData.this.coins = var10.getInt("Players." + var2 + ".Coins");
                  PlayerData.this.kills = var10.getInt("Players." + var2 + ".Kills");
                  PlayerData.this.deaths = var10.getInt("Players." + var2 + ".Deaths");
                  PlayerData.this.kitUnlockers = var10.getInt("Players." + var2 + ".Kit-Unlockers");
                  PlayerData.this.dataexp = var10.getInt("Players." + var2 + ".Exp");
                  Iterator var11 = var10.getStringList("Players." + var2 + ".Kits").iterator();

                  while(var11.hasNext()) {
                     var4 = (String)var11.next();
                     var1x.add(var4.toLowerCase());
                  }

                  if (var10.contains("Players." + var2 + ".Statistics")) {
                     PlayerData.this.loadStatisticsString(var10.getString("Players." + var2 + ".Statistics"));
                  }
               } else {
                  PlayerData.this.resetPlayer(var1);
                  var2x = true;
               }
            }

            PlayerData.this.updateRank();
            PlayerData.this.achievements = PlayerData.this.plugin.achievementsManager.getAchievements(PlayerData.this);
            if (!var2x) {
               PlayerData.this.loadKits(var1, var1x);
            }

         }
      }).runTaskLaterAsynchronously(this.plugin, 2L);
   }

   public void saveStatsIntoFile(final Player var1, boolean var2) {
      if (this.joined) {
         if (!var2) {
            (new BukkitRunnable() {
               public void run() {
                  if (PlayerData.this.plugin.config.useMySQL) {
                     PlayerData.this.saveStatsInMySQL(var1);
                  } else {
                     PlayerData.this.saveStatsInPlayersFile(var1);
                  }

               }
            }).runTaskAsynchronously(this.plugin);
         } else if (this.plugin.config.useMySQL) {
            this.saveStatsInMySQL(var1);
         } else {
            this.saveStatsInPlayersFile(var1);
         }

      }
   }

   private void saveStatsInMySQL(Player var1) {
      String var2 = "NO_KITS";
      ArrayList var3 = new ArrayList();
      Iterator var5 = this.kitsInventory.getAllContents().iterator();

      while(var5.hasNext()) {
         ItemStack var4 = (ItemStack)var5.next();
         var3.add(ChatColor.stripColor(var4.getItemMeta().getDisplayName()));
      }

      if (!var3.isEmpty()) {
         var2 = (String)var3.get(0);

         for(int var7 = 1; var7 < var3.size(); ++var7) {
            var2 = var2 + ", " + (String)var3.get(var7);
         }
      }

      String var8 = this.plugin.config.UUID ? var1.getUniqueId().toString() : var1.getName();

      try {
         Statement var9 = this.plugin.mysql.getConnection().createStatement();
         if (var9.executeQuery("SELECT * FROM " + this.plugin.config.tableprefix + " WHERE " + (this.plugin.config.UUID ? "player_uuid" : "player_name") + " = '" + var8 + "';").next()) {
            this.plugin.mysql.getConnection().prepareStatement("UPDATE " + this.plugin.config.tableprefix + " SET player_uuid='" + var1.getUniqueId().toString() + "', player_name='" + var1.getName() + "', Coins=" + this.coins + ", Kills=" + this.kills + ", Deaths=" + this.deaths + ", Exp=" + this.dataexp + ", Kitunlockers=" + this.kitUnlockers + ", Kits='" + var2 + "', Statistics='" + this.getStatisticsString(var1) + "' WHERE " + (this.plugin.config.UUID ? "player_uuid" : "player_name") + "='" + var8 + "';").executeUpdate();
         } else {
            var9.executeUpdate("INSERT INTO " + this.plugin.config.tableprefix + " (player_uuid, player_name, Coins, Kills, Deaths, Exp, Kitunlockers, Kits, Statistics) VALUES ('" + var1.getUniqueId().toString() + "', '" + var1.getName() + "', " + this.coins + ", " + this.kills + ", " + this.deaths + ", " + this.dataexp + ", " + this.kitUnlockers + ", '" + var2 + "', '" + this.getStatisticsString(var1) + "')");
         }

         var9.close();
      } catch (SQLException var6) {
         var6.printStackTrace();
      }

   }

   private void saveStatsInPlayersFile(Player var1) {
      FileConfiguration var2 = this.plugin.fileManager.getConfig("players.yml");
      String var3 = "Players." + (this.plugin.config.UUID ? var1.getUniqueId().toString() : var1.getName());
      var2.set(var3 + ".Name", var1.getName());
      var2.set(var3 + ".Kills", this.kills);
      var2.set(var3 + ".Deaths", this.deaths);
      var2.set(var3 + ".Coins", this.coins);
      var2.set(var3 + ".Kit-Unlockers", this.kitUnlockers);
      var2.set(var3 + ".Exp", this.dataexp);
      var2.set(var3 + ".Statistics", this.getStatisticsString(var1));
      ArrayList var4 = new ArrayList();
      Iterator var6 = this.kitsInventory.getAllContents().iterator();

      while(var6.hasNext()) {
         ItemStack var5 = (ItemStack)var6.next();
         var4.add(ChatColor.stripColor(var5.getItemMeta().getDisplayName()));
      }

      var2.set(var3 + ".Kits", var4);
      this.plugin.fileManager.saveConfig("players.yml");
   }

   private void loadStatisticsString(String var1) {
      String[] var2 = var1.split(":");
      this.projectiles_hit = Integer.valueOf(var2[0]);
      this.tournament_wins = Integer.valueOf(var2[1]);
      this.challenge_wins = Integer.valueOf(var2[2]);
      this.abilities_used = Integer.valueOf(var2[3]);
      this.soups_eaten = Integer.valueOf(var2[4]);
      this.killstreaks_earned = Integer.valueOf(var2[5]);
      if (var2.length > 6) {
         this.loadSelectedTrail(Integer.valueOf(var2[6]));
      }

   }

   private String getStatisticsString(Player var1) {
      return this.projectiles_hit + ":" + this.tournament_wins + ":" + this.challenge_wins + ":" + this.abilities_used + ":" + this.soups_eaten + ":" + this.killstreaks_earned + ":" + this.getSelectedTrailSlot(var1);
   }

   private void loadSelectedTrail(int var1) {
      if (this.plugin.trailsInventory != null && var1 >= 0 && var1 < this.plugin.trailsInventory.getSize()) {
         ItemStack var2 = this.plugin.trailsInventory.getItem(var1);
         if (var2 != null && !var2.getType().equals(Material.AIR)) {
            this.selectedTrail = Effect.valueOf(ChatColor.stripColor(var2.getItemMeta().getDisplayName()));
         }
      }

   }

   private int getSelectedTrailSlot(Player var1) {
      if (this.selectedTrail != null && var1.hasPermission("kitbattle.trails")) {
         for(int var2 = 0; var2 < this.plugin.trailsInventory.getSize(); ++var2) {
            if (this.plugin.trailsInventory.getItem(var2) != null && ChatColor.stripColor(this.plugin.trailsInventory.getItem(var2).getItemMeta().getDisplayName()).equals(this.selectedTrail.name())) {
               return var2;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   public void saveData(Player var1, PlayingMap var2) {
      this.joined = true;
      this.items = var1.getInventory().getContents();
      this.armor = var1.getInventory().getArmorContents();
      this.location = var1.getLocation();
      this.health = var1.getHealth();
      this.food = var1.getFoodLevel();
      this.levels = var1.getLevel();
      this.exp = var1.getExp();
      this.scoreboard = var1.getScoreboard();
      this.potions = var1.getActivePotionEffects();
      this.mode = var1.getGameMode();
      this.flying = var1.isFlying() || var1.getAllowFlight();
      this.setMap(var1, var2);
   }

   public void restoreData(Player var1) {
      var1.teleport(this.location);
      var1.getInventory().setContents(this.items);
      var1.getInventory().setArmorContents(this.armor);
      var1.setHealth(this.health);
      var1.setFoodLevel(this.food);
      var1.setLevel(this.levels);
      var1.setExp(this.exp);
      Iterator var3 = var1.getActivePotionEffects().iterator();

      while(var3.hasNext()) {
         PotionEffect var2 = (PotionEffect)var3.next();
         var1.removePotionEffect(var2.getType());
      }

      var1.addPotionEffects(this.potions);
      var1.setGameMode(this.mode);
      if (this.flying) {
         var1.setAllowFlight(true);
         var1.setFlying(true);
      } else {
         var1.setAllowFlight(false);
         var1.setFlying(false);
      }

      var1.setScoreboard(this.scoreboard);
      this.destroyData(var1);
   }

   public void setMap(Player var1, PlayingMap var2) {
      if (this.map != null) {
         this.map.players.remove(var1.getName());
         this.map.updateSignPlayers();
      }

      this.map = var2;
      if (var2 != null) {
         var2.players.add(var1.getName());
         var2.updateSignPlayers();
      }

      if (this.customScoreboard != null) {
         this.customScoreboard.updatePlaceholder(var1, this.plugin, "map", this.getMap() != null ? this.getMap().name : "None");
      }

   }

   public PlayingMap getMap() {
      return this.map;
   }

   public void destroyData(Player var1) {
      this.items = null;
      this.armor = null;
      this.location = null;
      this.health = 0.0D;
      this.food = 0;
      this.levels = 0;
      this.exp = 0.0F;
      this.potions = null;
      this.mode = null;
      this.setMap(var1, (PlayingMap)null);
      this.kit = null;
      this.clearCooldowns();
      this.customScoreboard = null;
   }

   public int getKills() {
      return this.kills;
   }

   public void addKills(Player var1) {
      ++this.kills;
      this.plugin.achievementsManager.checkPlayer(var1, AchievementsManager.AchievementType.KILLS, this.kills);
   }

   public int getDeaths() {
      return this.deaths;
   }

   public void addDeaths() {
      ++this.deaths;
   }

   public int getProjectileHits() {
      return this.projectiles_hit;
   }

   public void addProjectileHits(Player var1) {
      ++this.projectiles_hit;
      this.plugin.achievementsManager.checkPlayer(var1, AchievementsManager.AchievementType.PROJECTILES_HIT, this.projectiles_hit);
   }

   public int getTournamentWins() {
      return this.tournament_wins;
   }

   public void addTournamentWins(Player var1) {
      ++this.tournament_wins;
      this.plugin.achievementsManager.checkPlayer(var1, AchievementsManager.AchievementType.TOURNAMENTS_WON, this.tournament_wins);
   }

   public int getChallengeWins() {
      return this.challenge_wins;
   }

   public void addChallengeWins(Player var1) {
      ++this.challenge_wins;
      this.plugin.achievementsManager.checkPlayer(var1, AchievementsManager.AchievementType.CHALLENGES_WON, this.challenge_wins);
   }

   public int getAbilitiesUsed() {
      return this.abilities_used;
   }

   public void addAbilitiesUsed(Player var1) {
      ++this.abilities_used;
      this.plugin.achievementsManager.checkPlayer(var1, AchievementsManager.AchievementType.ABILITIES_USED, this.abilities_used);
   }

   public int getSoupsEaten() {
      return this.soups_eaten;
   }

   public void addSoupsEaten(Player var1) {
      ++this.soups_eaten;
      this.plugin.achievementsManager.checkPlayer(var1, AchievementsManager.AchievementType.SOUPS_EATEN, this.soups_eaten);
   }

   public int getKillstreaksEarned() {
      return this.killstreaks_earned;
   }

   public void addKillstreaksEarned(Player var1) {
      ++this.killstreaks_earned;
      this.plugin.achievementsManager.checkPlayer(var1, AchievementsManager.AchievementType.KILLSTREAKS_EARNED, this.killstreaks_earned);
   }

   public Kit getKit() {
      return this.kit;
   }

   public void setKit(Player var1, Kit var2) {
      this.kit = var2;
      if (this.customScoreboard != null) {
         this.customScoreboard.updatePlaceholder(var1, this.plugin, "selected_kit", this.getKit() != null ? this.getKit().getName() : "None");
      }

   }

   public boolean addExp(Player var1, int var2) {
      Rank var3 = this.getRank();
      this.setExp(var1, this.dataexp + var2);
      Rank var4 = this.getRank();
      return var3 != var4 && var4.getRequiredExp() > var3.getRequiredExp();
   }

   public int getExp() {
      return this.dataexp;
   }

   public void setExp(Player var1, int var2) {
      if (this.dataexp != var2) {
         int var3 = this.dataexp;
         this.dataexp = var2;
         if (this.dataexp <= var3 || this.getNextRank() != null && this.dataexp >= this.getNextRank().getRequiredExp()) {
            Rank var4 = this.getRank();
            this.updateRank();
            Rank var5 = this.getRank();
            if (!var5.getName().equals(var4.getName())) {
               if (var5.getRequiredExp() > var4.getRequiredExp()) {
                  ArrayList var6 = new ArrayList();
                  Iterator var8 = this.plugin.Ranks.values().iterator();

                  Rank var7;
                  while(var8.hasNext()) {
                     var7 = (Rank)var8.next();
                     if (var7.getRequiredExp() > var4.getRequiredExp() && var7.getRequiredExp() < var5.getRequiredExp()) {
                        var6.add(var7);
                     }
                  }

                  var6.add(var5);
                  var8 = var6.iterator();

                  while(var8.hasNext()) {
                     var7 = (Rank)var8.next();
                     Iterator var10;
                     if (this.plugin.config.BroadcastRankUp) {
                        Bukkit.broadcastMessage(this.plugin.kb + this.plugin.msgs.PlayerRankUpPublicMessage.replace("%player%", var1.getName()).replace("%rank%", var7.getName()));
                     } else {
                        var10 = this.plugin.getPlayers(this.plugin.players).iterator();

                        while(var10.hasNext()) {
                           Player var9 = (Player)var10.next();
                           var9.sendMessage(this.plugin.kb + this.plugin.msgs.PlayerRankUpPublicMessage.replace("%player%", var1.getName()).replace("%rank%", var7.getName()));
                        }
                     }

                     var10 = var7.getExcutedCommands().iterator();

                     while(var10.hasNext()) {
                        String var11 = (String)var10.next();
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), var11.replaceAll("%player%", var1.getName()));
                     }
                  }
               } else {
                  Bukkit.broadcastMessage(this.plugin.kb + this.plugin.msgs.PlayerDerankPublicMessage.replace("%player%", var1.getName()).replace("%rank%", var5.getName()));
               }
            }

         }
      }
   }

   public Rank getRank() {
      return this.rank;
   }

   public void setRank(Rank var1) {
      this.rank = var1;
   }

   public Rank getNextRank() {
      return this.nextRank;
   }

   public int getKillstreak() {
      return this.killstreak;
   }

   public int getDeathstreak() {
      return this.deathstreak;
   }

   public int getKitUnlockers() {
      return this.kitUnlockers;
   }

   public int getCoins(Player var1) {
      return this.plugin.econ != null ? (int)this.plugin.econ.getBalance(var1) : this.coins;
   }

   public void addCoins(Player var1, int var2) {
      if (this.plugin.econ != null) {
         this.plugin.econ.depositPlayer(var1, (double)var2);
      } else {
         this.coins += var2;
         if (!this.joined) {
            this.joined = true;
         }
      }

   }

   public void removeCoins(Player var1, int var2) {
      if (this.plugin.econ != null) {
         this.plugin.econ.withdrawPlayer(var1, (double)var2);
      } else {
         this.coins -= var2;
         if (!this.joined) {
            this.joined = true;
         }
      }

   }

   public void setCoins(Player var1, int var2) {
      if (this.plugin.econ != null) {
         int var3 = var2 - this.getCoins(var1);
         if (var3 > 0) {
            this.plugin.econ.depositPlayer(var1, (double)var3);
         } else {
            this.plugin.econ.withdrawPlayer(var1, (double)(-var3));
         }
      } else {
         this.coins = var2;
         if (!this.joined) {
            this.joined = true;
         }
      }

   }

   public void updateRank() {
      for(int var1 = 0; var1 < Rank.orderd.size(); ++var1) {
         Rank var2 = (Rank)Rank.orderd.get(var1);
         if (this.dataexp >= var2.getRequiredExp()) {
            this.rank = var2;
            this.nextRank = Rank.orderd.size() <= var1 + 1 ? null : (Rank)Rank.orderd.get(var1 + 1);
         }
      }

   }

   public void createScoreboard(Player var1) {
      if (this.plugin.config.ScoreboardEnabled) {
         this.customScoreboard = new CustomScoreboard(this.plugin, true, this.plugin.msgs.scoreboard_title, this.plugin.msgs.defaultScoreboard);
         this.customScoreboard.updatePlaceholder(var1, this.plugin, "kills", this.getKills());
         this.customScoreboard.updatePlaceholder(var1, this.plugin, "deaths", this.getDeaths());
         this.customScoreboard.updatePlaceholder(var1, this.plugin, "coins", this.getCoins(var1));
         this.customScoreboard.updatePlaceholder(var1, this.plugin, "killstreak", this.getKillstreak());
         this.customScoreboard.updatePlaceholder(var1, this.plugin, "deathstreak", this.getDeathstreak());
         this.customScoreboard.updatePlaceholder(var1, this.plugin, "player_exp", this.getExp());
         this.customScoreboard.updatePlaceholder(var1, this.plugin, "player_rank", this.getRank().getName());
         this.customScoreboard.updatePlaceholder(var1, this.plugin, "player_next_rank", this.getNextRank() != null ? this.getNextRank().getName() : "None");
         this.customScoreboard.updatePlaceholder(var1, this.plugin, "player_next_rank_exp", this.getNextRank() != null ? String.valueOf(this.getNextRank().getRequiredExp()) : "0");
         this.customScoreboard.updatePlaceholder(var1, this.plugin, "player_next_rank_exp_difference", this.getNextRank() != null ? String.valueOf(this.getNextRank().getRequiredExp() - this.getExp()) : "0");
         this.customScoreboard.updatePlaceholder(var1, this.plugin, "map", this.getMap() != null ? this.getMap().name : "None");
         this.customScoreboard.updatePlaceholder(var1, this.plugin, "selected_kit", this.getKit() != null ? this.getKit().getName() : "None");
         this.customScoreboard.apply(var1);
      }
   }

   public CustomScoreboard getScoreboard() {
      return this.customScoreboard;
   }

   public void addDamage(Player var1, double var2) {
      this.damagers.put(var1.getName(), this.damagers.containsKey(var1.getName()) ? (Double)this.damagers.get(var1.getName()) + var2 : var2);
   }

   public boolean hasCooldown(Player var1, String var2) {
      long var3 = this.cooldowns.containsKey(var2) ? (Long)this.cooldowns.get(var2) - System.currentTimeMillis() : 0L;
      if (var3 > 0L) {
         var1.sendMessage(this.plugin.kb + this.plugin.msgs.StillOnCooldown.replace("%time%", String.valueOf(String.valueOf((new BigDecimal(Double.valueOf((double)var3) / 1000.0D)).setScale(1, RoundingMode.HALF_UP).doubleValue()))));
         return true;
      } else {
         return false;
      }
   }

   public void clearCooldowns() {
      this.cooldowns.clear();
      if (this.abilityCooldownNotifier != null) {
         this.abilityCooldownNotifier.cancel();
         this.abilityCooldownNotifier = null;
      }

   }

   public void setCooldown(final Player var1, String var2, int var3, boolean var4) {
      this.cooldowns.put(var2, System.currentTimeMillis() + (long)(var3 * 1000));
      if (var4 && this.plugin.config.NotifyWhenCooldownOff) {
         this.abilityCooldownNotifier = (new BukkitRunnable() {
            public void run() {
               if (var1 != null && PlayerData.this.plugin.players.contains(var1.getName())) {
                  var1.sendMessage(PlayerData.this.plugin.kb + PlayerData.this.plugin.msgs.CooldownRemove);
                  PlayerData.this.abilityCooldownNotifier = null;
               }

            }
         }).runTaskLater(this.plugin, (long)(var3 * 20));
      }

   }

   public void resetPlayer(Player var1) {
      this.coins = this.plugin.config.StartingCoins;
      this.kills = 0;
      this.deaths = 0;
      this.dataexp = 0;
      this.projectiles_hit = 0;
      this.tournament_wins = 0;
      this.challenge_wins = 0;
      this.abilities_used = 0;
      this.soups_eaten = 0;
      this.killstreaks_earned = 0;
      this.updateRank();
      this.achievements = this.plugin.achievementsManager.getAchievements(this);
      this.kitUnlockers = this.plugin.config.StartingAmountOfKitUnlockers;
      this.loadKits(var1, new ArrayList());
   }

   public void loadKits(Player var1, List var2) {
      this.kitsInventory = new SmartInventory(this.plugin, (String)this.plugin.msgs.inventories.get("Kits"));
      if (var2.isEmpty()) {
         var2.addAll(this.plugin.config.defaultKits);
      }

      Iterator var3 = var2.iterator();

      for(int var4 = 0; (double)var4 < Math.ceil(Double.valueOf((double)var2.size()) / (double)this.plugin.smartSlots.length); ++var4) {
         if (var4 >= this.kitsInventory.getSize()) {
            this.kitsInventory.addInventory(ChatColor.RED + "List #" + (var4 + 1));
         }

         int[] var8;
         int var7 = (var8 = this.plugin.smartSlots).length;

         for(int var6 = 0; var6 < var7; ++var6) {
            int var5 = var8[var6];
            if (!var3.hasNext()) {
               break;
            }

            String var9 = (String)var3.next();
            if (this.plugin.Kits.containsKey(var9)) {
               this.kitsInventory.setItem(var4, var5, ((Kit)this.plugin.Kits.get(var9)).getLogo());
            }
         }
      }

   }

   public void openUpgrades(Player var1) {
      ArrayList var2 = new ArrayList();
      Iterator var4 = this.kitsInventory.getAllContents().iterator();

      while(true) {
         ItemStack var3;
         do {
            if (!var4.hasNext()) {
               if (var2.isEmpty()) {
                  var1.sendMessage(this.plugin.kb + this.plugin.msgs.NoUpgrades);
                  var1.closeInventory();
                  return;
               }

               Inventory var8 = Bukkit.createInventory(var1, 54, (String)this.plugin.msgs.inventories.get("Shop") + ": " + ChatColor.DARK_PURPLE + "Upgrades");
               var8.setItem(var8.getSize() - 1, this.plugin.back_itemstack);
               Iterator var10 = var2.iterator();

               while(var10.hasNext()) {
                  Kit var9 = (Kit)var10.next();
                  var8.addItem(new ItemStack[]{var9.getShopLogo()});
               }

               var1.openInventory(var8);
               return;
            }

            var3 = (ItemStack)var4.next();
         } while(var3.getType().equals(Material.AIR));

         String var5 = ChatColor.stripColor(var3.getItemMeta().getDisplayName()).toLowerCase();
         Iterator var7 = this.plugin.Kits.values().iterator();

         while(var7.hasNext()) {
            Kit var6 = (Kit)var7.next();
            if (var6.original != null && var6.original.name.toLowerCase().equals(var5)) {
               var2.add(var6);
            }
         }
      }
   }

   public Inventory getStatsInventory(Player var1) {
      Inventory var2 = Bukkit.createInventory((InventoryHolder)null, 36, (String)this.plugin.msgs.inventories.get("Stats-Inventory"));
      this.plugin.cageInventory(var2, false);
      var2.setItem(var2.getSize() - 5, this.plugin.back_itemstack);
      double var3 = (new BigDecimal(this.deaths > 1 ? Double.valueOf((double)this.kills) / (double)this.deaths : (double)this.kills)).setScale(2, RoundingMode.HALF_UP).doubleValue();
      ItemStackBuilder var5 = new ItemStackBuilder(Material.PAPER);
      var2.addItem(new ItemStack[]{var5.setName(ChatColor.GREEN + "Kills:").addLore(ChatColor.YELLOW + String.valueOf(this.kills)).setType(Material.IRON_SWORD).build()});
      var2.addItem(new ItemStack[]{var5.setName(ChatColor.GREEN + "Deaths:").addLore(ChatColor.YELLOW + String.valueOf(this.deaths)).setType(Material.REDSTONE).build()});
      var2.addItem(new ItemStack[]{var5.setName(ChatColor.GREEN + "KDR:").addLore(ChatColor.YELLOW + String.valueOf(var3)).setType(Material.ENCHANTED_BOOK).build()});
      var2.addItem(new ItemStack[]{var5.setName(ChatColor.GREEN + "Coins:").addLore(ChatColor.YELLOW + String.valueOf(this.getCoins(var1))).setType(Material.EMERALD).build()});
      var2.addItem(new ItemStack[]{var5.setName(ChatColor.GREEN + "Tournament Wins:").addLore(ChatColor.YELLOW + String.valueOf(this.tournament_wins)).setType(Material.DIAMOND).build()});
      var2.addItem(new ItemStack[]{var5.setName(ChatColor.GREEN + "Challenge Wins:").addLore(ChatColor.YELLOW + String.valueOf(this.challenge_wins)).setType(Material.GOLD_INGOT).build()});
      var2.addItem(new ItemStack[]{var5.setName(ChatColor.GREEN + "Projectiles Hit:").addLore(ChatColor.YELLOW + String.valueOf(this.projectiles_hit)).setType(Material.BOW).build()});
      var2.addItem(new ItemStack[]{var5.setName(ChatColor.GREEN + "Exp:").addLore(ChatColor.YELLOW + String.valueOf(this.dataexp)).setType(Material.EXP_BOTTLE).build()});
      var2.addItem(new ItemStack[]{var5.setName(ChatColor.GREEN + "Rank:").addLore(ChatColor.YELLOW + this.rank.getName()).setType(Material.CHEST).build()});
      var2.addItem(new ItemStack[]{var5.setName(ChatColor.GREEN + "Abilities used:").addLore(ChatColor.YELLOW + String.valueOf(this.abilities_used)).setType(Material.REDSTONE_TORCH_ON).build()});
      var2.addItem(new ItemStack[]{var5.setName(ChatColor.GREEN + "Soups Eaten:").addLore(ChatColor.YELLOW + String.valueOf(this.soups_eaten)).setType(Material.MUSHROOM_SOUP).build()});
      var2.addItem(new ItemStack[]{var5.setName(ChatColor.GREEN + "Killstreaks Earned:").addLore(ChatColor.YELLOW + String.valueOf(this.killstreaks_earned)).setType(Material.DIAMOND_AXE).build()});
      return var2;
   }

   public void sendStats(CommandSender var1, Player var2) {
      var1.sendMessage("" + ChatColor.DARK_AQUA + ChatColor.STRIKETHROUGH + " ----------" + ChatColor.AQUA + " " + var2.getName() + ChatColor.DARK_AQUA + " " + ChatColor.STRIKETHROUGH + "----------");
      var1.sendMessage(ChatColor.AQUA + " - " + ChatColor.GREEN + "Kills: " + ChatColor.YELLOW + this.kills);
      var1.sendMessage(ChatColor.AQUA + " - " + ChatColor.GREEN + "Deaths: " + ChatColor.YELLOW + this.deaths);
      var1.sendMessage(ChatColor.AQUA + " - " + ChatColor.GREEN + "KDR: " + ChatColor.YELLOW + (new BigDecimal(this.deaths > 1 ? Double.valueOf((double)this.kills) / (double)this.deaths : (double)this.kills)).setScale(2, RoundingMode.HALF_UP).doubleValue());
      var1.sendMessage(ChatColor.AQUA + " - " + ChatColor.GREEN + "Coins: " + ChatColor.YELLOW + this.getCoins(var2));
      var1.sendMessage(ChatColor.AQUA + " - " + ChatColor.GREEN + "Tournament Wins: " + ChatColor.YELLOW + this.tournament_wins);
      var1.sendMessage(ChatColor.AQUA + " - " + ChatColor.GREEN + "Challenge Wins: " + ChatColor.YELLOW + this.challenge_wins);
      var1.sendMessage(ChatColor.AQUA + " - " + ChatColor.GREEN + "Projectiles Hit: " + ChatColor.YELLOW + this.projectiles_hit);
      var1.sendMessage(ChatColor.AQUA + " - " + ChatColor.GREEN + "Exp: " + ChatColor.YELLOW + this.dataexp);
      var1.sendMessage(ChatColor.AQUA + " - " + ChatColor.GREEN + "Rank: " + ChatColor.YELLOW + this.rank.getName());
      var1.sendMessage(ChatColor.AQUA + " - " + ChatColor.GREEN + "Abilities used: " + ChatColor.YELLOW + this.abilities_used);
      var1.sendMessage(ChatColor.AQUA + " - " + ChatColor.GREEN + "Soups Eaten: " + ChatColor.YELLOW + this.soups_eaten);
      var1.sendMessage(ChatColor.AQUA + " - " + ChatColor.GREEN + "Killstreaks Earned: " + ChatColor.YELLOW + this.killstreaks_earned);
      var1.sendMessage("" + ChatColor.DARK_AQUA + ChatColor.STRIKETHROUGH + " -----------------------------");
   }
}
