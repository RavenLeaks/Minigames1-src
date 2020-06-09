package me.wazup.skywars;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class SkywarsAPI {
   private Skywars plugin;

   public SkywarsAPI(Skywars var1) {
      this.plugin = var1;
   }

   public PlayerData getPlayerData(Player var1) {
      return (PlayerData)this.plugin.playerData.get(var1.getName());
   }

   public HashMap getAllPlayersData() {
      HashMap var1 = new HashMap();
      String var5;
      if (this.plugin.config.mysql_enabled) {
         Connection var2 = this.plugin.mysql.getConnection();
         PreparedStatement var3 = var2.prepareStatement(this.plugin.mysql.SELECTALL);
         ResultSet var4 = var3.executeQuery();

         while(var4.next()) {
            var5 = var4.getString("player_name");
            var1.put(var5, this.plugin.playerData.containsKey(var5) ? ((PlayerData)this.plugin.playerData.get(var5)).getStats() : var4.getString("stats"));
         }

         var4.close();
         var3.close();
      } else {
         Iterator var7 = this.getPlayersFiles().iterator();

         while(var7.hasNext()) {
            File var6 = (File)var7.next();
            YamlConfiguration var8 = YamlConfiguration.loadConfiguration(var6);
            var5 = var8.getString("Name");
            var1.put(var5, this.plugin.playerData.containsKey(var5) ? ((PlayerData)this.plugin.playerData.get(var5)).getStats() : var8.getString("Stats"));
         }
      }

      return var1;
   }

   public List getTopPlayers(HashMap var1, Enums.Stat var2, int var3) {
      if (var3 < 1) {
         throw new IllegalArgumentException("Amount must be a number above 0!");
      } else {
         HashMap var4 = new HashMap();
         Iterator var6 = var1.keySet().iterator();

         while(var6.hasNext()) {
            String var5 = (String)var6.next();
            var4.put(var5, Integer.valueOf(((String)var1.get(var5)).split(":")[var2.id]));
         }

         if (var4.size() < var3) {
            int var7 = var3 - var4.size() + 1;

            for(int var9 = 1; var9 < var7; ++var9) {
               var4.put("NO_PLAYER" + var9, 0);
            }
         }

         LinkedList var8 = new LinkedList(var4.entrySet());
         Collections.sort(var8, new Comparator() {
            public int compare(Entry var1, Entry var2) {
               return (Integer)var2.getValue() - (Integer)var1.getValue();
            }
         });
         return var8;
      }
   }

   protected List getPlayersFiles() {
      ArrayList var1 = new ArrayList();
      File var2 = new File(this.plugin.getDataFolder(), "players");
      if (var2.exists() && var2.isDirectory()) {
         File[] var6;
         int var5 = (var6 = var2.listFiles()).length;

         for(int var4 = 0; var4 < var5; ++var4) {
            File var3 = var6[var4];
            if (var3.isFile()) {
               YamlConfiguration var7 = YamlConfiguration.loadConfiguration(var3);
               if (var7.contains("Name") && var7.contains("Stats")) {
                  var1.add(var3);
               }
            }
         }
      }

      return var1;
   }

   public boolean modifyOfflinePlayerStat(String var1, Enums.Stat var2, int var3, boolean var4) {
      boolean var5 = false;
      if (this.plugin.config.mysql_enabled) {
         Connection var6 = this.plugin.mysql.getConnection();
         PreparedStatement var7 = var6.prepareStatement(this.plugin.mysql.SELECT);
         var7.setString(1, var1);
         ResultSet var8 = var7.executeQuery();
         if (var8.next()) {
            String var9 = var8.getString("stats");
            int var10 = this.getCharsBeforePoint(var9, ':', var2.id);
            var9 = var9.substring(0, var10) + var9.substring(var10).replaceFirst(var9.split(":")[var2.id], String.valueOf(var4 ? Integer.valueOf(var9.split(":")[var2.id]) + var3 : var3));
            var7 = var6.prepareStatement(this.plugin.mysql.UPDATE_STATS);
            var7.setString(1, var9);
            var7.setString(2, var1);
            var7.execute();
            var5 = true;
         }

         var8.close();
         var7.close();
      } else {
         Iterator var15 = this.getPlayersFiles().iterator();

         while(var15.hasNext()) {
            File var14 = (File)var15.next();
            String var16 = this.plugin.config.useUUID ? YamlConfiguration.loadConfiguration(var14).getString("Name") : var14.getName();
            if (var16.equalsIgnoreCase(var1)) {
               YamlConfiguration var17 = YamlConfiguration.loadConfiguration(var14);
               String var18 = var17.getString("Stats");
               int var11 = this.getCharsBeforePoint(var18, ':', var2.id);
               var18 = var18.substring(0, var11) + var18.substring(var11).replaceFirst(var18.split(":")[var2.id], String.valueOf(var4 ? Integer.valueOf(var18.split(":")[var2.id]) + var3 : var3));
               var17.set("Stats", var18);

               try {
                  var17.save(var14);
               } catch (IOException var13) {
                  var13.printStackTrace();
               }

               var5 = true;
               break;
            }
         }
      }

      return var5;
   }

   public boolean isInArena(Player var1) {
      return ((PlayerData)this.plugin.playerData.get(var1.getName())).arena != null;
   }

   public boolean isSpectating(Player var1) {
      return this.isInArena(var1) && ((PlayerData)this.plugin.playerData.get(var1.getName())).arena.spectators.contains(var1.getName());
   }

   public boolean isPlaying(Player var1) {
      return this.isInArena(var1) && !this.isSpectating(var1) && !((PlayerData)this.plugin.playerData.get(var1.getName())).arena.state.AVAILABLE();
   }

   private int getCharsBeforePoint(String var1, char var2, int var3) {
      int var4 = 0;

      for(int var5 = 0; var5 < var1.length(); ++var5) {
         if (var1.charAt(var5) == var2) {
            ++var4;
         }

         if (var4 == var3) {
            return var5 + 1;
         }
      }

      return -1;
   }
}
