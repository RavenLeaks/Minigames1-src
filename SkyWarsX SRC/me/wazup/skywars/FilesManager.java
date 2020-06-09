package me.wazup.skywars;

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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

class FilesManager {
   private HashMap configurations = new HashMap();
   private Skywars plugin;

   public FilesManager(Skywars var1) {
      this.plugin = var1;
      var1.reloadConfig();
      var1.getConfig().options().copyDefaults(true);
      var1.saveConfig();
      this.registerConfig("cages.yml");
      this.registerConfig("chests.yml");
      this.registerConfig("kits.yml");
      this.registerConfig("shop.yml");
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
      if (var1.exists()) {
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
}
