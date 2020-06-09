package me.wazup.skywars;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

class AchievementsManager {
   private HashMap achievements = new HashMap();
   private Skywars plugin;

   public AchievementsManager(Skywars var1) {
      this.plugin = var1;
      FileConfiguration var2 = var1.filesManager.getConfig("achievements.yml");
      Enums.AchievementType[] var6;
      int var5 = (var6 = Enums.AchievementType.values()).length;

      int var4;
      for(var4 = 0; var4 < var5; ++var4) {
         Enums.AchievementType var3 = var6[var4];
         String var7 = "Achievements." + var3.name().toLowerCase();
         ArrayList var8 = new ArrayList();
         if (!var2.getConfigurationSection(var7).getKeys(false).isEmpty()) {
            Iterator var10 = var2.getConfigurationSection(var7).getKeys(false).iterator();

            while(var10.hasNext()) {
               String var9 = (String)var10.next();
               var7 = "Achievements." + var3.name().toLowerCase() + "." + var9 + ".";
               var8.add(new AchievementsManager.Achievement(Integer.valueOf(var9), var2.getString(var7 + "description"), var2.getString(var7 + "prize-description"), var2.getString(var7 + "executed-command")));
            }
         }

         this.achievements.put(var3, var8);
      }

      List var11 = this.getAchievements();
      var4 = 0;

      for(var5 = 0; (double)var5 < Math.ceil(Double.valueOf((double)var11.size()) / (double)var1.smartSlots.length); ++var5) {
         int[] var15;
         int var14 = (var15 = var1.smartSlots).length;

         for(int var13 = 0; var13 < var14; ++var13) {
            int var12 = var15[var13];
            if (var4 >= var11.size()) {
               break;
            }

            ((AchievementsManager.Achievement)var11.get(var4)).id = var5;
            ((AchievementsManager.Achievement)var11.get(var4)).slot = var12;
            ++var4;
         }
      }

   }

   public SmartInventory getAchievements(PlayerData var1) {
      SmartInventory var2 = new SmartInventory(this.plugin, (String)this.plugin.customization.inventories.get("Achievements"));

      for(int var3 = 0; (double)var3 < Math.ceil(Double.valueOf((double)this.getSize()) / (double)this.plugin.smartSlots.length); ++var3) {
         var2.addInventory(ChatColor.BLUE + "List #" + (var3 + 1));
         var2.setItem(var3, 49, this.plugin.back_itemstack);
      }

      int[] var12 = new int[]{var1.kills, var1.wins, var1.projectiles_launched, var1.projectiles_hit, var1.player_rank, var1.blocks_placed, var1.blocks_broken, var1.items_enchanted, var1.items_crafted, var1.fishes_caught};
      if (var12.length != Enums.AchievementType.values().length) {
         return null;
      } else {
         Enums.AchievementType[] var7;
         int var6 = (var7 = Enums.AchievementType.values()).length;

         for(int var5 = 0; var5 < var6; ++var5) {
            Enums.AchievementType var4 = var7[var5];
            int var8 = var12[var4.ordinal()];
            Iterator var10 = ((ArrayList)this.achievements.get(var4)).iterator();

            while(var10.hasNext()) {
               AchievementsManager.Achievement var9 = (AchievementsManager.Achievement)var10.next();
               ItemStack var11 = (new ItemStackBuilder(var8 >= var9.score ? Material.DIAMOND_BLOCK : Material.COAL_BLOCK)).setName(ChatColor.AQUA + var9.description).addLore(var8 >= var9.score ? ChatColor.GREEN + "Unlocked" : ChatColor.RED + "Locked", " ", ChatColor.GOLD + "Prize: " + ChatColor.YELLOW + var9.prizeDescription).build();
               var2.setItem(var9.id, var9.slot, var11);
            }
         }

         return var2;
      }
   }

   public void checkPlayer(Player var1, Enums.AchievementType var2, int var3) {
      Iterator var5 = ((ArrayList)this.achievements.get(var2)).iterator();

      while(var5.hasNext()) {
         AchievementsManager.Achievement var4 = (AchievementsManager.Achievement)var5.next();
         if (var3 == var4.score) {
            var4.send(var1);
            break;
         }
      }

   }

   public List getAchievements() {
      ArrayList var1 = new ArrayList();
      Enums.AchievementType[] var5;
      int var4 = (var5 = Enums.AchievementType.values()).length;

      for(int var3 = 0; var3 < var4; ++var3) {
         Enums.AchievementType var2 = var5[var3];
         var1.addAll((Collection)this.achievements.get(var2));
      }

      return var1;
   }

   public int getSize() {
      return this.getAchievements().size();
   }

   public class Achievement {
      int score;
      String description;
      String prizeDescription;
      String executedCommand;
      int id;
      int slot;

      public Achievement(int var2, String var3, String var4, String var5) {
         this.score = var2;
         this.description = var3;
         this.prizeDescription = var4;
         this.executedCommand = var5;
      }

      public void send(Player var1) {
         ChatColor var2 = AchievementsManager.this.plugin.colors[AchievementsManager.this.plugin.r.nextInt(AchievementsManager.this.plugin.colors.length)];
         var1.sendMessage("" + var2 + ChatColor.STRIKETHROUGH + "-----" + ChatColor.DARK_RED + ChatColor.MAGIC + " AA" + ChatColor.YELLOW + " Achievement unlocked " + ChatColor.DARK_RED + ChatColor.MAGIC + "AA " + var2 + ChatColor.STRIKETHROUGH + "-----");
         var1.sendMessage(" ");
         var1.sendMessage(var2 + " Achievement -> " + ChatColor.GOLD + this.description);
         var1.sendMessage(var2 + " Prize -> " + ChatColor.YELLOW + this.prizeDescription);
         var1.sendMessage(" ");
         var1.sendMessage("" + var2 + ChatColor.STRIKETHROUGH + "-----------------------------------");
         Bukkit.dispatchCommand(Bukkit.getConsoleSender(), this.executedCommand.replace("%player%", var1.getName()));
         AchievementsManager.this.plugin.fireWorkEffect(var1, true);
         var1.playSound(var1.getLocation(), AchievementsManager.this.plugin.NOTE_PLING, 1.0F, 1.0F);
         (new ItemStackBuilder(((PlayerData)AchievementsManager.this.plugin.playerData.get(var1.getName())).achievements.getItem(this.id, this.slot))).setType(Material.DIAMOND_BLOCK).replaceLore(ChatColor.RED + "Locked", ChatColor.GREEN + "Unlocked").build();
      }
   }
}
