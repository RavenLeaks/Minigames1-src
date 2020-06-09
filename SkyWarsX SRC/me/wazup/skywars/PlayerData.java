package me.wazup.skywars;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

public class PlayerData {
   private Skywars plugin;
   private Location location;
   private double maxhealth;
   private double health;
   private int food;
   private int level;
   private float exp;
   private GameMode gameMode;
   private boolean flying;
   private Collection effects;
   private ItemStack[] items;
   private ItemStack[] armor;
   private Scoreboard scoreboard;
   Arena arena;
   Party party;
   int warnings = 0;
   String lastHit = "";
   long lastHitTime = 0L;
   private HashMap cooldowns = new HashMap();
   long currentPlayTime;
   boolean save;
   Hologram hologram;
   String selectedKit;
   String selectedCage;
   String selectedTrail;
   private int coins;
   public int kills;
   public int deaths;
   public int wins;
   public int modifier;
   public int projectiles_launched;
   public int projectiles_hit;
   public int player_exp;
   public int player_rank;
   public int blocks_placed;
   public int blocks_broken;
   public int items_enchanted;
   public int items_crafted;
   public int fishes_caught;
   public int seconds;
   public int minutes;
   public int hours;
   public int days;
   SmartInventory inventory;
   SmartInventory achievements;
   CustomScoreboard lobbyScoreboard;

   protected PlayerData(Skywars var1, Player var2) {
      this.plugin = var1;
      this.load(var2);
   }

   protected void saveData(Player var1) {
      this.location = var1.getLocation();
      this.maxhealth = var1.getMaxHealth();
      this.health = var1.getHealth();
      this.food = var1.getFoodLevel();
      this.level = var1.getLevel();
      this.exp = var1.getExp();
      this.gameMode = var1.getGameMode();
      this.flying = var1.isFlying();
      this.effects = var1.getActivePotionEffects();
      this.items = var1.getInventory().getContents();
      this.armor = var1.getInventory().getArmorContents();
      this.scoreboard = var1.getScoreboard();
      this.currentPlayTime = System.currentTimeMillis();
      this.save = true;
   }

   protected void restoreData(Player var1) {
      var1.teleport(this.location);
      var1.setFallDistance(0.0F);
      var1.setMaxHealth(this.maxhealth);
      var1.setHealth(this.health);
      var1.setFoodLevel(this.food);
      var1.setLevel(this.level);
      var1.setExp(this.exp);
      var1.setGameMode(this.gameMode);
      if (this.flying) {
         var1.setAllowFlight(true);
         var1.setFlying(true);
      }

      Iterator var3 = var1.getActivePotionEffects().iterator();

      while(var3.hasNext()) {
         PotionEffect var2 = (PotionEffect)var3.next();
         var1.removePotionEffect(var2.getType());
      }

      var1.addPotionEffects(this.effects);
      var1.getInventory().setContents(this.items);
      var1.getInventory().setArmorContents(this.armor);
      var1.updateInventory();
      var1.setScoreboard(this.scoreboard);
      this.updatePlayTime();
      if (this.hologram != null) {
         this.hologram.delete();
      }

      this.destroyData();
   }

   protected void clearPlayer(Player var1) {
      var1.getInventory().clear();
      var1.getInventory().setArmorContents((ItemStack[])null);
      var1.setMaxHealth(20.0D);
      var1.setHealth(var1.getMaxHealth());
      var1.setFoodLevel(20);
      var1.setFireTicks(0);
      var1.setLevel(0);
      var1.setExp(0.0F);
      var1.setGameMode(GameMode.SURVIVAL);
      var1.setAllowFlight(false);
      var1.setFlying(false);
      Iterator var3 = var1.getActivePotionEffects().iterator();

      while(var3.hasNext()) {
         PotionEffect var2 = (PotionEffect)var3.next();
         var1.removePotionEffect(var2.getType());
      }

      this.createScoreboard(var1);
      if (this.plugin.config.hotbarItems.containsKey("Arena-Selector")) {
         var1.getInventory().setItem((Integer)this.plugin.config.hotbarItems.get("Arena-Selector") - 1, this.plugin.play_itemstack);
      }

      if (this.plugin.config.hotbarItems.containsKey("Shop")) {
         var1.getInventory().setItem((Integer)this.plugin.config.hotbarItems.get("Shop") - 1, this.plugin.shop_itemstack);
      }

      if (this.plugin.config.hotbarItems.containsKey("Party")) {
         var1.getInventory().setItem((Integer)this.plugin.config.hotbarItems.get("Party") - 1, this.plugin.party_itemstack);
      }

      if (this.plugin.config.hotbarItems.containsKey("Profile")) {
         var1.getInventory().setItem((Integer)this.plugin.config.hotbarItems.get("Profile") - 1, this.plugin.profile_itemstack);
      }

      if (this.plugin.config.hotbarItems.containsKey("Quit")) {
         var1.getInventory().setItem((Integer)this.plugin.config.hotbarItems.get("Quit") - 1, this.plugin.quit_itemstack);
      }

      var1.updateInventory();
      if (this.plugin.hologramsManager != null) {
         this.plugin.hologramsManager.createStats(var1, this);
      }

   }

   protected void makeSpectator(Player var1) {
      var1.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 99999, 3, true));
      var1.setAllowFlight(true);
      var1.setFlying(true);
      var1.getInventory().addItem(new ItemStack[]{this.plugin.play_itemstack});
      var1.getInventory().addItem(new ItemStack[]{this.plugin.teleporter_itemstack});
      var1.getInventory().setItem(4, this.plugin.profile_itemstack);
      var1.getInventory().setItem(8, this.plugin.quit_itemstack);
   }

   protected void destroyData() {
      this.location = null;
      this.maxhealth = 0.0D;
      this.health = 0.0D;
      this.food = 0;
      this.level = 0;
      this.exp = 0.0F;
      this.gameMode = null;
      this.effects = null;
      this.items = null;
      this.armor = null;
      this.scoreboard = null;
      this.lobbyScoreboard = null;
      this.hologram = null;
   }

   protected void createScoreboard(Player var1) {
      if (this.plugin.config.lobbyScoreboardEnabled) {
         this.lobbyScoreboard = new CustomScoreboard(this.plugin, false, ChatColor.BOLD + this.plugin.customization.scoreboard_title, new String[]{this.plugin.customization.scoreboard_header, (String)this.plugin.customization.scoreboard.get("Kills-Deaths"), this.kills + "/" + this.deaths, (String)this.plugin.customization.scoreboard.get("Coins"), String.valueOf(this.getCoins(var1)), (String)this.plugin.customization.scoreboard.get("Wins"), String.valueOf(this.wins), " ", (String)this.plugin.customization.scoreboard.get("Kit"), this.plugin.kits.containsKey(this.selectedKit) ? ((Kit)this.plugin.kits.get(this.selectedKit)).name : "None", (String)this.plugin.customization.scoreboard.get("Cage"), this.plugin.cages.containsKey(this.selectedCage) ? ((Cage)this.plugin.cages.get(this.selectedCage)).name : "Default", (String)this.plugin.customization.scoreboard.get("Trail"), this.plugin.trails.containsKey(this.selectedTrail) ? ((Trail)this.plugin.trails.get(this.selectedTrail)).name : "None", this.plugin.customization.scoreboard_footer});
         this.lobbyScoreboard.apply(var1);
      } else {
         var1.setScoreboard(this.scoreboard);
      }

   }

   protected void load(final Player var1) {
      final String var2 = this.plugin.config.useUUID ? var1.getUniqueId().toString() : var1.getName();
      (new BukkitRunnable() {
         public void run() {
            String var1x = "0:" + PlayerData.this.plugin.config.startingCoins + ":0:0:1:0:0:0:0:0:0:0:0:0D-0H-0M-0S";
            String var2x = "";
            String var3 = "";
            if (PlayerData.this.plugin.config.mysql_enabled) {
               try {
                  Connection var4 = PlayerData.this.plugin.mysql.getConnection();
                  PreparedStatement var5 = var4.prepareStatement(PlayerData.this.plugin.mysql.SELECT);
                  var5.setString(1, var2);
                  ResultSet var6 = var5.executeQuery();
                  if (var6.next()) {
                     var1x = var6.getString("stats");
                     var2x = var6.getString("inventory");
                     var3 = var6.getString("selected");
                  }

                  var5.close();
                  var6.close();
               } catch (SQLException var7) {
                  var7.printStackTrace();
               }
            } else {
               File var8 = new File(PlayerData.this.plugin.getDataFolder() + "/players/", var2);
               if (var8.exists()) {
                  YamlConfiguration var9 = YamlConfiguration.loadConfiguration(var8);
                  var1x = var9.getString("Stats");
                  var2x = var9.getString("Inventory");
                  var3 = var9.getString("Selected");
               }
            }

            PlayerData.this.loadStats(var1x);
            PlayerData.this.loadInventory(var2x);
            PlayerData.this.loadSelectedItems(var3);
            if (PlayerData.this.plugin.lobbyPlayers.contains(var1.getName())) {
               (new BukkitRunnable() {
                  public void run() {
                     PlayerData.this.createScoreboard(var1);
                  }
               }).runTask(PlayerData.this.plugin);
            }

         }
      }).runTaskAsynchronously(this.plugin);
   }

   protected void save(Player var1) {
      if (this.save) {
         String var2 = this.plugin.config.useUUID ? var1.getUniqueId().toString() : var1.getName();
         if (this.plugin.config.mysql_enabled) {
            try {
               Connection var3 = this.plugin.mysql.getConnection();
               PreparedStatement var4 = var3.prepareStatement(this.plugin.mysql.SELECT);
               var4.setString(1, var2);
               if (var4.executeQuery().next()) {
                  var4 = var3.prepareStatement(this.plugin.mysql.UPDATE);
                  var4.setString(1, var1.getName());
                  var4.setString(2, this.getStats());
                  var4.setString(3, this.getInventory());
                  var4.setString(4, this.getSelectedItems());
                  var4.setString(5, var2);
               } else {
                  var4 = var3.prepareStatement(this.plugin.mysql.INSERT);
                  var4.setString(1, var1.getUniqueId().toString());
                  var4.setString(2, var1.getName());
                  var4.setString(3, this.getStats());
                  var4.setString(4, this.getInventory());
                  var4.setString(5, this.getSelectedItems());
               }

               var4.execute();
               var4.close();
            } catch (SQLException var7) {
               var7.printStackTrace();
            }
         } else {
            File var8 = new File(this.plugin.getDataFolder() + "/players/", var2);
            YamlConfiguration var9 = YamlConfiguration.loadConfiguration(var8);
            var9.set("Name", var1.getName());
            var9.set("Stats", this.getStats());
            var9.set("Inventory", this.getInventory());
            var9.set("Selected", this.getSelectedItems());

            try {
               var9.save(var8);
            } catch (IOException var6) {
               var6.printStackTrace();
            }
         }

      }
   }

   protected void saveAsync(final Player var1) {
      if (this.save) {
         (new BukkitRunnable() {
            public void run() {
               PlayerData.this.save(var1);
            }
         }).runTaskAsynchronously(this.plugin);
      }
   }

   protected String getStats() {
      return this.kills + ":" + this.coins + ":" + this.deaths + ":" + this.wins + ":" + this.modifier + ":" + this.projectiles_launched + ":" + this.projectiles_hit + ":" + this.player_exp + ":" + this.blocks_placed + ":" + this.blocks_broken + ":" + this.items_enchanted + ":" + this.items_crafted + ":" + this.fishes_caught + ":" + this.getPlayTime();
   }

   private void loadStats(String var1) {
      String[] var2 = var1.split(":");
      this.kills = this.i(var2[0]);
      this.coins = this.i(var2[1]);
      this.deaths = this.i(var2[2]);
      this.wins = this.i(var2[3]);
      this.modifier = this.i(var2[4]);
      this.projectiles_launched = this.i(var2[5]);
      this.projectiles_hit = this.i(var2[6]);
      this.player_exp = this.i(var2[7]);
      this.blocks_placed = this.i(var2[8]);
      this.blocks_broken = this.i(var2[9]);
      this.items_enchanted = this.i(var2[10]);
      this.items_crafted = this.i(var2[11]);
      this.fishes_caught = this.i(var2[12]);
      this.player_rank = this.plugin.ranksManager.getRank(this);
      this.achievements = this.plugin.achievementsManager.getAchievements(this);
      this.loadPlayTime(var2[13]);
   }

   protected void updatePlayTime() {
      this.seconds = (int)((long)this.seconds + (System.currentTimeMillis() - this.currentPlayTime) / 1000L);

      for(this.currentPlayTime = System.currentTimeMillis(); this.seconds > 60; ++this.minutes) {
         this.seconds -= 60;
      }

      while(this.minutes > 60) {
         this.minutes -= 60;
         ++this.hours;
      }

      while(this.hours > 24) {
         this.hours -= 24;
         ++this.days;
      }

   }

   private String getPlayTime() {
      return this.days + "D-" + this.hours + "H-" + this.minutes + "M-" + this.seconds + "S";
   }

   private void loadPlayTime(String var1) {
      String[] var2 = var1.replace("D", "").replace("H", "").replace("M", "").replace("S", "").split("-");
      this.days = this.i(var2[0]);
      this.hours = this.i(var2[1]);
      this.minutes = this.i(var2[2]);
      this.seconds = this.i(var2[3]);
   }

   private int i(String var1) {
      return Integer.valueOf(var1);
   }

   private String getInventory() {
      String var1 = "";

      ItemStack var3;
      for(int var2 = 0; var2 < this.inventory.getSize(); ++var2) {
         for(Iterator var4 = this.inventory.getContents(var2).iterator(); var4.hasNext(); var1 = var1 + ChatColor.stripColor(var3.getItemMeta().getDisplayName()) + " : ") {
            var3 = (ItemStack)var4.next();
         }
      }

      return var1.substring(0, var1.isEmpty() ? 0 : var1.length() - 3);
   }

   private void loadInventory(String var1) {
      this.inventory = new SmartInventory(this.plugin, ChatColor.BLUE + "Inventory");
      this.inventory.addInventory(ChatColor.RED + "Kits");
      this.inventory.addInventory(ChatColor.BLUE + "Cages");
      this.inventory.addInventory(ChatColor.YELLOW + "Trails");

      for(int var2 = 0; var2 < this.inventory.getSize(); ++var2) {
         this.inventory.setItem(var2, 49, this.plugin.back_itemstack);
      }

      if (!var1.isEmpty()) {
         String[] var7 = var1.toLowerCase().split(" : ");
         String[] var6 = var7;
         int var5 = var7.length;

         for(int var4 = 0; var4 < var5; ++var4) {
            String var3 = var6[var4];
            if (this.plugin.kits.containsKey(var3)) {
               this.inventory.addItem(0, ((Kit)this.plugin.kits.get(var3)).item);
            } else if (this.plugin.cages.containsKey(var3)) {
               this.inventory.addItem(1, ((Cage)this.plugin.cages.get(var3)).item);
            } else if (this.plugin.trails.containsKey(var3)) {
               this.inventory.addItem(2, ((Trail)this.plugin.trails.get(var3)).item);
            }
         }

      }
   }

   private String getSelectedItems() {
      String var1 = "";
      if (!this.selectedKit.isEmpty()) {
         var1 = this.selectedKit;
      }

      if (!this.selectedCage.isEmpty()) {
         var1 = var1 + " : " + this.selectedCage;
      }

      if (!this.selectedTrail.isEmpty()) {
         var1 = var1 + " : " + this.selectedTrail;
      }

      return var1;
   }

   private void loadSelectedItems(String var1) {
      this.selectedKit = "";
      this.selectedCage = "";
      this.selectedTrail = "";
      String[] var2 = var1.toLowerCase().split(" : ");
      String[] var6 = var2;
      int var5 = var2.length;

      for(int var4 = 0; var4 < var5; ++var4) {
         String var3 = var6[var4];
         if (this.plugin.kits.containsKey(var3)) {
            this.selectedKit = var3;
         } else if (this.plugin.cages.containsKey(var3)) {
            this.selectedCage = var3;
         } else if (this.plugin.trails.containsKey(var3)) {
            this.selectedTrail = var3;
         }
      }

   }

   protected Inventory getStatsInventory(Player var1) {
      Inventory var2 = Bukkit.createInventory((InventoryHolder)null, 45, (String)this.plugin.customization.inventories.get("Stats"));
      this.updatePlayTime();
      this.plugin.cageInventory(var2, false);
      var2.setItem(var2.getSize() - 5, this.plugin.back_itemstack);
      double var3 = (new BigDecimal(this.deaths > 1 ? Double.valueOf((double)this.kills) / (double)this.deaths : (double)this.kills)).setScale(2, RoundingMode.HALF_UP).doubleValue();
      int var5 = (int)(this.projectiles_launched > 0 ? (double)this.projectiles_hit / Double.valueOf((double)this.projectiles_launched) * 100.0D : 0.0D);
      ItemStackBuilder var6 = new ItemStackBuilder(Material.PAPER);
      var2.addItem(new ItemStack[]{var6.setName(ChatColor.GREEN + "Kills:").addLore(ChatColor.YELLOW + String.valueOf(this.kills)).build()});
      var2.addItem(new ItemStack[]{var6.setName(ChatColor.GREEN + "Deaths:").addLore(ChatColor.YELLOW + String.valueOf(this.deaths)).build()});
      var2.addItem(new ItemStack[]{var6.setName(ChatColor.GREEN + "KDR:").addLore(ChatColor.YELLOW + String.valueOf(var3)).build()});
      var2.addItem(new ItemStack[]{var6.setName(ChatColor.GREEN + "Coins:").addLore(ChatColor.YELLOW + String.valueOf(this.getCoins(var1))).build()});
      var2.addItem(new ItemStack[]{var6.setName(ChatColor.GREEN + "Wins:").addLore(ChatColor.YELLOW + String.valueOf(this.wins)).build()});
      var2.addItem(new ItemStack[]{var6.setName(ChatColor.GREEN + "Modifier:").addLore(ChatColor.YELLOW + String.valueOf(this.modifier)).build()});
      var2.addItem(new ItemStack[]{var6.setName(ChatColor.GREEN + "Play Time:").addLore(ChatColor.YELLOW + this.getPlayTime()).build()});
      var2.addItem(new ItemStack[]{var6.setName(ChatColor.GREEN + "Projectiles Hit:").addLore(ChatColor.YELLOW + String.valueOf(this.projectiles_hit)).build()});
      var2.addItem(new ItemStack[]{var6.setName(ChatColor.GREEN + "Projectiles Launched:").addLore(ChatColor.YELLOW + String.valueOf(this.projectiles_launched)).build()});
      var2.addItem(new ItemStack[]{var6.setName(ChatColor.GREEN + "Accuracy:").addLore(ChatColor.YELLOW + String.valueOf(var5) + "%").build()});
      var2.addItem(new ItemStack[]{var6.setName(ChatColor.GREEN + "Exp:").addLore(ChatColor.YELLOW + String.valueOf(this.player_exp)).build()});
      var2.addItem(new ItemStack[]{var6.setName(ChatColor.GREEN + "Rank:").addLore(ChatColor.YELLOW + String.valueOf(this.player_rank)).build()});
      var2.addItem(new ItemStack[]{var6.setName(ChatColor.GREEN + "Next rank:").addLore(ChatColor.YELLOW + String.valueOf(this.plugin.ranksManager.getNextRankExp(this))).build()});
      var2.addItem(new ItemStack[]{var6.setName(ChatColor.GREEN + "Blocks placed:").addLore(ChatColor.YELLOW + String.valueOf(this.blocks_placed)).build()});
      var2.addItem(new ItemStack[]{var6.setName(ChatColor.GREEN + "Blocks broken:").addLore(ChatColor.YELLOW + String.valueOf(this.blocks_broken)).build()});
      var2.addItem(new ItemStack[]{var6.setName(ChatColor.GREEN + "Items enchanted:").addLore(ChatColor.YELLOW + String.valueOf(this.items_enchanted)).build()});
      var2.addItem(new ItemStack[]{var6.setName(ChatColor.GREEN + "Items crafted:").addLore(ChatColor.YELLOW + String.valueOf(this.items_crafted)).build()});
      var2.addItem(new ItemStack[]{var6.setName(ChatColor.GREEN + "Fishes caught:").addLore(ChatColor.YELLOW + String.valueOf(this.fishes_caught)).build()});
      return var2;
   }

   public boolean hasCooldown(Player var1, String var2, int var3) {
      long var4 = this.cooldowns.containsKey(var2) ? (Long)this.cooldowns.get(var2) - System.currentTimeMillis() : 0L;
      if (var4 > 0L) {
         var1.sendMessage(((String)this.plugin.customization.messages.get("Cooldown")).replace("%seconds%", String.valueOf((new BigDecimal(Double.valueOf((double)var4) / 1000.0D)).setScale(1, RoundingMode.HALF_UP).doubleValue())));
         return true;
      } else {
         this.cooldowns.put(var2, System.currentTimeMillis() + (long)(var3 * 1000));
         return false;
      }
   }

   public int getCoins(Player var1) {
      return this.plugin.vault != null ? (int)this.plugin.vault.getBalance(var1.getName()) : this.coins;
   }

   public void addCoins(Player var1, int var2) {
      if (this.plugin.vault != null) {
         this.plugin.vault.depositPlayer(var1.getName(), (double)var2);
      } else {
         this.coins += var2;
      }

   }

   public void removeCoins(Player var1, int var2) {
      if (this.plugin.vault != null) {
         this.plugin.vault.withdrawPlayer(var1.getName(), (double)var2);
      } else {
         this.coins -= var2;
      }

   }

   public void setCoins(Player var1, int var2) {
      if (this.plugin.vault != null) {
         int var3 = var2 - this.getCoins(var1);
         if (var3 > 0) {
            this.plugin.vault.depositPlayer(var1, (double)var3);
         } else {
            this.plugin.vault.withdrawPlayer(var1, (double)(-var3));
         }
      } else {
         this.coins = var2;
      }

   }

   public void updateLobbyScoreboard(String var1, String var2, Boolean var3) {
      if (this.lobbyScoreboard != null) {
         this.lobbyScoreboard.update(var1, var2, var3);
      }

   }
}
