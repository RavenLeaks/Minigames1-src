package me.wazup.eggwars;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

class FilesManager {
   private HashMap configurations = new HashMap();
   private Eggwars plugin;
   private HashMap enchantmentsNames = new HashMap();
   private HashMap colorsToDurability = new HashMap();
   private HashMap chatColorsToColors = new HashMap();

   public FilesManager(Eggwars var1) {
      this.plugin = var1;
      var1.reloadConfig();
      var1.getConfig().options().copyDefaults(true);
      var1.saveConfig();
      this.registerConfig("kits.yml");
      this.registerConfig("shop.yml");
      this.registerConfig("villager_shop.yml");
      this.registerConfig("achievements.yml");
      this.registerConfig("broadcaster.yml");
      this.registerConfig("trails.yml");
      this.registerConfig("signs.yml");
      this.registerConfig("customization.yml");
      Iterator var3 = this.configurations.keySet().iterator();

      while(var3.hasNext()) {
         String var2 = (String)var3.next();
         this.reloadConfig(var2);
         ((FileConfiguration)this.configurations.get(var2)).options().copyDefaults(true);
         this.saveConfig(var2);
      }

      this.loadEnchantmentsNames();
      this.loadColorsToDurability();
      this.loadChatColorsToColors();
   }

   private void registerConfig(String var1) {
      this.configurations.put(var1, YamlConfiguration.loadConfiguration(new File(this.plugin.getDataFolder(), var1)));
   }

   public FileConfiguration getConfig(String var1) {
      return (FileConfiguration)this.configurations.get(var1);
   }

   private void reloadConfig(String var1) {
      InputStream var2 = this.plugin.getResource(var1);
      if (var2 != null) {
         InputStreamReader var3 = new InputStreamReader(var2);
         YamlConfiguration var4 = YamlConfiguration.loadConfiguration(var3);
         ((FileConfiguration)this.configurations.get(var1)).setDefaults(var4);

         try {
            var3.close();
            var2.close();
         } catch (IOException var6) {
            var6.printStackTrace();
         }
      }

   }

   public void saveConfig(String var1) {
      try {
         ((FileConfiguration)this.configurations.get(var1)).save(new File(this.plugin.getDataFolder(), var1));
      } catch (IOException var3) {
         Bukkit.getConsoleSender().sendMessage(this.plugin.customization.prefix + "Couldn't save " + var1 + "!");
      }

   }

   public void deleteFile(File var1) {
      if (var1.exists() && var1.isDirectory()) {
         File[] var5;
         int var4 = (var5 = var1.listFiles()).length;

         for(int var3 = 0; var3 < var4; ++var3) {
            File var2 = var5[var3];
            if (var2.isDirectory()) {
               this.deleteFile(var2);
            } else {
               var2.delete();
            }
         }
      }

      var1.delete();
   }

   public void copyFile(File var1, File var2) {
      try {
         if (var1.isDirectory()) {
            if (!var2.exists()) {
               var2.mkdirs();
            }

            String[] var3 = var1.list();
            String[] var7 = var3;
            int var6 = var3.length;

            for(int var5 = 0; var5 < var6; ++var5) {
               String var4 = var7[var5];
               File var8 = new File(var1, var4);
               File var9 = new File(var2, var4);
               this.copyFile(var8, var9);
            }
         } else {
            FileInputStream var16 = new FileInputStream(var1);
            FileOutputStream var17 = new FileOutputStream(var2);
            FileChannel var18 = var16.getChannel();
            FileChannel var19 = var17.getChannel();

            try {
               var18.transferTo(0L, var18.size(), var19);
            } catch (IOException var13) {
               var13.printStackTrace();
            } finally {
               if (var18 != null) {
                  var18.close();
               }

               if (var19 != null) {
                  var19.close();
               }

               var16.close();
               var17.close();
            }
         }
      } catch (IOException var15) {
         Bukkit.getConsoleSender().sendMessage(this.plugin.customization.prefix + "Failed to copy files!");
         var15.printStackTrace();
      }

   }

   public long getSize(File var1) {
      long var2 = 0L;
      if (var1.isDirectory()) {
         String[] var7;
         int var6 = (var7 = var1.list()).length;

         for(int var5 = 0; var5 < var6; ++var5) {
            String var4 = var7[var5];
            var2 += this.getSize(new File(var1, var4));
         }
      } else {
         var2 = var1.length();
      }

      return var2;
   }

   private void loadEnchantmentsNames() {
      this.enchantmentsNames.put("AQUA_AFFINITY", "WATER_WORKER");
      this.enchantmentsNames.put("BANE_OF_ARTHROPODS", "DAMAGE_ARTHROPODS");
      this.enchantmentsNames.put("BLAST_PROTECTION", "PROTECTION_EXPLOSIONS");
      this.enchantmentsNames.put("CURSE_OF_BINDING", "BINDING_CURSE");
      this.enchantmentsNames.put("CURSE_OF_VANISHING", "VANISHING_CURSE");
      this.enchantmentsNames.put("EFFICIENCY", "DIG_SPEED");
      this.enchantmentsNames.put("FEATHER_FALLING", "PROTECTION_FALL");
      this.enchantmentsNames.put("FIRE_PROTECTION", "PROTECTION_FIRE");
      this.enchantmentsNames.put("FLAME", "ARROW_FIRE");
      this.enchantmentsNames.put("FORTUNE", "LOOT_BONUS_BLOCKS");
      this.enchantmentsNames.put("INFINITY", "ARROW_INFINITE");
      this.enchantmentsNames.put("LOOTING", "LOOT_BONUS_MOBS");
      this.enchantmentsNames.put("LUCK_OF_THE_SEA", "LUCK");
      this.enchantmentsNames.put("POWER", "ARROW_DAMAGE");
      this.enchantmentsNames.put("PROJECTILE_PROTECTION", "PROTECTION_PROJECTILE");
      this.enchantmentsNames.put("PROTECTION", "PROTECTION_ENVIRONMENTAL");
      this.enchantmentsNames.put("PUNCH", "ARROW_KNOCKBACK");
      this.enchantmentsNames.put("RESPIRATION", "OXYGEN");
      this.enchantmentsNames.put("SHARPNESS", "DAMAGE_ALL");
      this.enchantmentsNames.put("SMITE", "DAMAGE_UNDEAD");
      this.enchantmentsNames.put("UNBREAKING", "DURABILITY");
   }

   public String translateEnchantmentName(String var1) {
      return this.enchantmentsNames.containsKey(var1) ? (String)this.enchantmentsNames.get(var1) : var1;
   }

   private void loadColorsToDurability() {
      this.colorsToDurability.put("WHITE", 0);
      this.colorsToDurability.put("DARK_PURPLE", 10);
      this.colorsToDurability.put("LIGHT_PURPLE", 6);
      this.colorsToDurability.put("AQUA", 3);
      this.colorsToDurability.put("YELLOW", 4);
      this.colorsToDurability.put("GOLD", 1);
      this.colorsToDurability.put("GREEN", 5);
      this.colorsToDurability.put("DARK_GRAY", 7);
      this.colorsToDurability.put("GRAY", 8);
      this.colorsToDurability.put("DARK_AQUA", 9);
      this.colorsToDurability.put("BLUE", 11);
      this.colorsToDurability.put("DARK_BLUE", 11);
      this.colorsToDurability.put("DARK_GREEN", 13);
      this.colorsToDurability.put("RED", 14);
      this.colorsToDurability.put("DARK_RED", 14);
      this.colorsToDurability.put("BLACK", 15);
   }

   public int translateColorToDurability(String var1) {
      return this.colorsToDurability.containsKey(var1) ? (Integer)this.colorsToDurability.get(var1) : 0;
   }

   private void loadChatColorsToColors() {
      this.chatColorsToColors.put(ChatColor.AQUA, Color.fromRGB(85, 255, 255));
      this.chatColorsToColors.put(ChatColor.BLACK, Color.fromRGB(0, 0, 0));
      this.chatColorsToColors.put(ChatColor.BLUE, Color.fromRGB(85, 85, 255));
      this.chatColorsToColors.put(ChatColor.DARK_AQUA, Color.fromRGB(0, 170, 170));
      this.chatColorsToColors.put(ChatColor.DARK_BLUE, Color.fromRGB(0, 0, 170));
      this.chatColorsToColors.put(ChatColor.DARK_GRAY, Color.fromRGB(85, 85, 85));
      this.chatColorsToColors.put(ChatColor.DARK_GREEN, Color.fromRGB(0, 170, 0));
      this.chatColorsToColors.put(ChatColor.DARK_PURPLE, Color.fromRGB(170, 0, 170));
      this.chatColorsToColors.put(ChatColor.DARK_RED, Color.fromRGB(170, 0, 0));
      this.chatColorsToColors.put(ChatColor.GOLD, Color.fromRGB(255, 170, 0));
      this.chatColorsToColors.put(ChatColor.GRAY, Color.fromRGB(170, 170, 170));
      this.chatColorsToColors.put(ChatColor.GREEN, Color.fromRGB(85, 255, 85));
      this.chatColorsToColors.put(ChatColor.RED, Color.fromRGB(255, 85, 85));
      this.chatColorsToColors.put(ChatColor.WHITE, Color.fromRGB(255, 255, 255));
      this.chatColorsToColors.put(ChatColor.YELLOW, Color.fromRGB(255, 255, 85));
      this.chatColorsToColors.put(ChatColor.LIGHT_PURPLE, Color.fromRGB(255, 85, 255));
   }

   public Color translateChatColorToColor(ChatColor var1) {
      return this.chatColorsToColors.containsKey(var1) ? (Color)this.chatColorsToColors.get(var1) : Color.WHITE;
   }
}
