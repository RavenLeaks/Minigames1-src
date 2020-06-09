package me.wazup.skywars;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

class ArenaSaveBlocksTask {
   public ArenaSaveBlocksTask(final Skywars var1, final Player var2, final String var3, final Cuboid var4) {
      final ArrayList var5 = new ArrayList();
      final ArrayList var6 = new ArrayList();
      final Iterator var7 = var4.iterator();
      (new BukkitRunnable() {
         int scannedBlocks = 0;
         int temp_counter = 0;
         int runs = 0;
         int scanSpeed;
         double totalBlocks;

         {
            this.scanSpeed = var1.config.rollback_scan_speed;
            this.totalBlocks = (double)var4.getSize();
         }

         public void run() {
            for(; var7.hasNext() && this.temp_counter < this.scanSpeed; ++this.temp_counter) {
               Block var1x = (Block)var7.next();
               if (var1x.getType() != Material.AIR) {
                  if (var1x.getType().equals(Material.CHEST)) {
                     var6.add(var1x.getX() + ":" + var1x.getY() + ":" + var1x.getZ() + ":" + var1x.getTypeId() + ":" + var1x.getData() + ":Default");
                  } else {
                     var5.add(var1x.getX() + ":" + var1x.getY() + ":" + var1x.getZ() + ":" + var1x.getTypeId() + (var1x.getData() != 0 ? ":" + var1x.getData() : ""));
                  }
               }
            }

            this.scannedBlocks += this.temp_counter;
            this.temp_counter = 0;
            ++this.runs;
            if (this.runs == var1.config.rollback_send_status_update_every) {
               ArenaSaveBlocksTask.this.sendUpdate(var2, var1, this.scannedBlocks, this.totalBlocks, var5.size(), var6.size(), this.scanSpeed, (int)((double)this.scannedBlocks / this.totalBlocks * 100.0D), (this.totalBlocks - (double)this.scannedBlocks) / (double)(this.scanSpeed * 20));
               this.runs = 0;
            }

            if (!var7.hasNext()) {
               this.cancel();
               ArenaSaveBlocksTask.this.sendUpdate(var2, var1, (int)this.totalBlocks, this.totalBlocks, var5.size(), var6.size(), this.scanSpeed, 100, 0.0D);
               File var7x = new File(var1.getDataFolder() + "/arenas/" + var3, "locations.dat");
               YamlConfiguration var2x = YamlConfiguration.loadConfiguration(var7x);
               File var3x = new File(var1.getDataFolder() + "/arenas/" + var3, "blocks.dat");
               YamlConfiguration var4x = YamlConfiguration.loadConfiguration(var3x);
               var2x.set("Cuboid", var4.toString());
               var2x.set("Chests", var6.toString());
               var4x.set("Blocks", var5.toString());

               try {
                  var2x.save(var7x);
                  var4x.save(var3x);
               } catch (IOException var6x) {
                  var6x.printStackTrace();
               }

               boolean var5x = var1.arenas.containsKey(var3.toLowerCase());
               new Arena(var1, var3);
               var1.updateArenasInventory();
               if (!var5x) {
                  var2.sendMessage(var1.customization.prefix + "Arena " + ChatColor.AQUA + var3 + ChatColor.GRAY + " has been " + ChatColor.GREEN + "successfully" + ChatColor.GRAY + " created!");
               } else {
                  var2.sendMessage(var1.customization.prefix + "Arena region has been updated!");
               }
            }

         }
      }).runTaskTimer(var1, 0L, 1L);
   }

   private void sendUpdate(Player var1, Skywars var2, int var3, double var4, int var6, int var7, int var8, int var9, double var10) {
      var1.sendMessage(ChatColor.GRAY + "=======================");
      var1.sendMessage(ChatColor.GRAY + "- Scanned blocks: " + ChatColor.AQUA + var3);
      var1.sendMessage(ChatColor.GRAY + "- Total blocks: " + ChatColor.YELLOW + (int)var4);
      var1.sendMessage(ChatColor.GRAY + "- Detected blocks to chests: " + ChatColor.AQUA + var6 + ChatColor.GRAY + " to " + ChatColor.LIGHT_PURPLE + var7);
      var1.sendMessage(ChatColor.GRAY + "- Scanning Speed: " + ChatColor.GREEN + var8 + " Block/Tick");
      var1.sendMessage(ChatColor.GRAY + "- Scanned Percentage: " + var2.getPercentageString(var9));
      var1.sendMessage(ChatColor.GRAY + "- Time left: " + ChatColor.LIGHT_PURPLE + var10 + "s");
      var1.sendMessage(ChatColor.GRAY + "=======================");
   }
}
