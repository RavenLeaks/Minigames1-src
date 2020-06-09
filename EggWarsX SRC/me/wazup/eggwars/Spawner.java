package me.wazup.eggwars;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class Spawner {
   private Eggwars plugin;
   Location spawningLocation;
   Location blockLocation;
   ItemStack item;
   int originalSpeed;
   boolean originalBroken;
   SpawnerData spawnerData;
   int itemLimit = -1;
   private int currentSpeed;
   private int counter;
   private boolean currentBroken;
   private int upgrades;
   private BukkitTask resumingTask;
   Sign sign;
   Inventory upgradeInventory;
   Hologram hologram;
   private static Vector[] surroundingBlocks = new Vector[]{new Vector(-1, 0, 0), new Vector(1, 0, 0), new Vector(0, 0, 1), new Vector(0, 0, -1)};

   public Spawner(Eggwars var1, Location var2, Material var3, int var4, boolean var5) {
      this.plugin = var1;
      this.spawningLocation = var2;
      this.blockLocation = var2.clone().add(0.0D, -1.0D, 0.0D).getBlock().getLocation();
      this.originalSpeed = var4;
      this.currentSpeed = var4;
      this.originalBroken = var5;
      this.currentBroken = var5;
      this.item = new ItemStack(var3);
      this.counter = 0;
      this.upgrades = 0;
      if (var1.config.spawnerData.containsKey(this.item.getType())) {
         this.spawnerData = (SpawnerData)var1.config.spawnerData.get(this.item.getType());
         this.itemLimit = this.spawnerData.itemLimit;
         if (this.spawnerData.fixCost != null && this.spawnerData.upgrades != null) {
            this.upgradeInventory = Bukkit.createInventory((InventoryHolder)null, 27, (String)var1.customization.inventories.get("Spawner-Upgrade"));
            var1.cageInventory(this.upgradeInventory, true);
         }
      }

      this.updateInventoryItems();
   }

   public void reset() {
      this.currentSpeed = this.originalSpeed;
      this.currentBroken = this.originalBroken;
      this.counter = 0;
      this.upgrades = 0;
      this.sign = null;
      this.updateInventoryItems();
      if (this.resumingTask != null) {
         this.resumingTask.cancel();
         this.resumingTask = null;
      }

      if (this.hologram != null) {
         this.hologram.delete();
      }

   }

   public boolean isBroken() {
      return this.currentBroken && this.resumingTask == null;
   }

   public boolean canUpgrade() {
      return this.spawnerData != null && (this.isBroken() && this.spawnerData.fixCost != null || !this.isBroken() && this.spawnerData.upgrades != null && this.upgrades < this.spawnerData.upgrades.size() && this.getUpgradeSpeed() != 0);
   }

   private int getUpgradeSpeed() {
      int var1 = 0;
      Iterator var3 = this.spawnerData.upgrades.values().iterator();

      while(var3.hasNext()) {
         int var2 = (Integer)var3.next();
         if (var2 > var1 && var2 < this.currentSpeed) {
            var1 = var2;
         }
      }

      return var1;
   }

   public ItemStack getUpgradeItem() {
      if (this.isBroken()) {
         return this.spawnerData.fixCost;
      } else {
         int var1 = this.getUpgradeSpeed();
         Iterator var3 = this.spawnerData.upgrades.keySet().iterator();

         while(var3.hasNext()) {
            ItemStack var2 = (ItemStack)var3.next();
            if ((Integer)this.spawnerData.upgrades.get(var2) == var1) {
               return var2;
            }
         }

         return null;
      }
   }

   public void upgrade() {
      if (!this.isBroken()) {
         ++this.upgrades;
         this.currentSpeed = this.getUpgradeSpeed();
      } else {
         this.currentBroken = false;
         if (this.hologram == null) {
            this.createHologram();
         }
      }

      this.updateInventoryItems();
      if (this.sign != null) {
         this.sign.setLine(1, ChatColor.DARK_GREEN + "WORKING");
         this.sign.setLine(3, "Level " + (this.upgrades + 1));
         this.sign.update();
      }

   }

   private void updateInventoryItems() {
      if (this.upgradeInventory != null) {
         ItemStackBuilder var1 = (new ItemStackBuilder(this.item.clone())).setAmount(this.upgrades + 1).setName(ChatColor.YELLOW + this.item.getType().name() + " Generator");
         if (this.isBroken()) {
            var1.addLore(ChatColor.RED + "BROKEN");
         } else {
            var1.addLore(ChatColor.GRAY + " - Level: " + ChatColor.WHITE + (this.upgrades + 1), ChatColor.GRAY + " - Speed: " + ChatColor.WHITE + (double)this.currentSpeed / 20.0D + "s");
         }

         ItemStackBuilder var2 = (new ItemStackBuilder(Material.EXP_BOTTLE)).setName(ChatColor.LIGHT_PURPLE + "Upgrade Generator");
         if (this.isBroken()) {
            if (this.spawnerData.fixCost != null) {
               var2.addLore(ChatColor.GRAY + " - Activate generator", ChatColor.GRAY + " - Requires: " + ChatColor.AQUA + this.spawnerData.fixCost.getAmount() + " " + this.spawnerData.fixCost.getType().name() + "(s)");
            } else {
               var2.addLore(ChatColor.GRAY + " - " + ChatColor.RED + "CANT BE FIXED");
            }
         } else if (this.canUpgrade()) {
            ItemStack var3 = this.getUpgradeItem();
            int var4 = 100 * (this.currentSpeed - this.getUpgradeSpeed()) / this.currentSpeed;
            var2.addLore(ChatColor.GRAY + " - Upgrade to level " + (this.upgrades + 2) + " (" + ChatColor.GREEN + var4 + "% Improvement" + ChatColor.GRAY + ")", ChatColor.GRAY + " - Requires: " + ChatColor.AQUA + var3.getAmount() + " " + var3.getType().name() + "(s)");
         } else {
            var2.addLore(ChatColor.GRAY + " - " + ChatColor.RED + "FULLY UPGRADED");
         }

         this.upgradeInventory.setItem(11, var1.build());
         this.upgradeInventory.setItem(15, var2.build());
      }
   }

   public void update() {
      if (!this.currentBroken) {
         this.counter += 10;
         if (this.counter >= this.currentSpeed) {
            Item var1 = this.spawningLocation.getWorld().dropItem(this.spawningLocation, this.item);
            var1.setVelocity(new Vector());
            this.counter = 0;
            if (this.itemLimit > 0 && this.getSpawnedAmount(var1) >= this.itemLimit) {
               this.currentBroken = true;
               this.resumingTask = (new BukkitRunnable() {
                  public void run() {
                     if (Spawner.this.getSpawnedAmount((Item)null) < Spawner.this.itemLimit) {
                        this.cancel();
                        Spawner.this.resumingTask = null;
                        Spawner.this.currentBroken = false;
                     }

                  }
               }).runTaskTimer(this.plugin, 100L, 100L);
            }
         }

         if (this.hologram != null && this.counter % 20 == 0) {
            this.plugin.hologramsManager.updateGenerator(this.hologram, (this.currentSpeed - this.counter) / 20);
         }

      }
   }

   public void createHologram() {
      if (!this.isBroken() && this.currentSpeed >= 200 && this.plugin.config.hologramsAboveGenerators && this.plugin.hologramsManager != null) {
         this.hologram = this.plugin.hologramsManager.createGenerator(this.spawningLocation.clone().add(0.0D, 2.0D, 0.0D), this.item, this.currentSpeed / 20);
      }

   }

   public void createUpgradeSign() {
      if (this.plugin.config.signsAboveGenerators) {
         if (this.spawningLocation.getBlock().getState() instanceof Sign) {
            this.sign = (Sign)this.spawningLocation.getBlock().getState();
         } else {
            Vector[] var4;
            int var3 = (var4 = surroundingBlocks).length;

            for(int var2 = 0; var2 < var3; ++var2) {
               Vector var1 = var4[var2];
               Block var5 = this.spawningLocation.clone().add(var1).getBlock();
               if (var5.getType() != Material.AIR) {
                  BlockFace var6;
                  if (var1.getX() == 1.0D) {
                     var6 = BlockFace.WEST;
                  } else if (var1.getX() == -1.0D) {
                     var6 = BlockFace.EAST;
                  } else if (var1.getZ() == 1.0D) {
                     var6 = BlockFace.NORTH;
                  } else {
                     var6 = BlockFace.SOUTH;
                  }

                  Block var7 = var5.getRelative(var6);
                  var7.setType(Material.WALL_SIGN);
                  if (var7.getState() instanceof Sign) {
                     this.sign = (Sign)var7.getState();
                     ((org.bukkit.material.Sign)this.sign.getData()).setFacingDirection(var6);
                  }
                  break;
               }
            }

            if (this.sign == null) {
               this.spawningLocation.getBlock().setType(Material.SIGN_POST);
               if (!(this.spawningLocation.getBlock().getState() instanceof Sign)) {
                  return;
               }

               this.sign = (Sign)this.spawningLocation.getBlock().getState();
            }
         }

         if (this.sign != null) {
            this.sign.setLine(0, this.plugin.customization.signs_title);
            this.sign.setLine(1, this.currentBroken ? ChatColor.DARK_RED + "BROKEN" : ChatColor.DARK_GREEN + "WORKING");
            this.sign.setLine(2, ChatColor.BOLD + "Generator");
            this.sign.setLine(3, "Level 1");
            this.sign.update();
         }

      }
   }

   private int getSpawnedAmount(Item var1) {
      int var3 = 0;
      List var2;
      if (var1 != null) {
         var2 = var1.getNearbyEntities(0.5D, 0.5D, 0.5D);
         var3 = var1.getItemStack().getAmount();
      } else {
         var2 = this.spawningLocation.getWorld().spawnEntity(this.spawningLocation, EntityType.SNOWBALL).getNearbyEntities(0.5D, 0.5D, 0.5D);
      }

      Iterator var5 = var2.iterator();

      while(var5.hasNext()) {
         Entity var4 = (Entity)var5.next();
         if (var4.getType().equals(EntityType.DROPPED_ITEM)) {
            ItemStack var6 = ((Item)var4).getItemStack();
            if (var6.getType().equals(this.item.getType())) {
               var3 += var6.getAmount();
            }
         }
      }

      return var3;
   }

   public boolean equals(Object var1) {
      Spawner var2 = (Spawner)var1;
      return this.blockLocation.getBlockX() == var2.blockLocation.getBlockX() && this.blockLocation.getBlockY() == var2.blockLocation.getBlockY() && this.blockLocation.getBlockZ() == var2.blockLocation.getBlockZ() && this.item.getType() == var2.item.getType();
   }

   public int hashCode() {
      return (String.valueOf(this.blockLocation.getBlockX()) + this.blockLocation.getBlockY() + this.blockLocation.getBlockZ() + this.item.getType().name()).hashCode();
   }
}
