package me.wazup.kitbattle;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class KitbattleAPI {
   private Kitbattle plugin;

   public KitbattleAPI(Kitbattle var1) {
      this.plugin = var1;
   }

   public List getTopPlayers(int var1) {
      HashMap var2 = new HashMap();
      String var6;
      if (this.plugin.config.useMySQL) {
         try {
            String var3 = "SELECT * FROM " + this.plugin.config.tableprefix + " ORDER BY Kills DESC";
            ResultSet var4 = this.plugin.mysql.getConnection().createStatement().executeQuery(var3);

            for(int var5 = 0; var5 < var1 && var4.next(); ++var5) {
               var6 = var4.getString("player_name");
               var2.put(var6, this.plugin.playerData.containsKey(var6) ? ((PlayerData)this.plugin.playerData.get(var6)).getKills() : var4.getInt("Kills"));
            }

            var4.close();
         } catch (SQLException var7) {
            var7.printStackTrace();
         }
      } else {
         FileConfiguration var8 = this.plugin.fileManager.getConfig("players.yml");
         if (var8.getConfigurationSection("Players") != null) {
            Iterator var12 = var8.getConfigurationSection("Players").getKeys(false).iterator();

            while(var12.hasNext()) {
               String var11 = (String)var12.next();
               var6 = var8.getString("Players." + var11 + ".Name");
               var2.put(var6, this.plugin.playerData.containsKey(var6) ? ((PlayerData)this.plugin.playerData.get(var6)).getKills() : var8.getInt("Players." + var11 + ".Kills"));
            }
         }
      }

      if (var2.size() < var1) {
         for(int var9 = var2.size(); var9 < var1; ++var9) {
            var2.put("NO_PLAYER" + var9, 0);
         }
      }

      LinkedList var10 = new LinkedList(var2.entrySet());
      Collections.sort(var10, new Comparator() {
         public int compare(Entry var1, Entry var2) {
            return (Integer)var2.getValue() - (Integer)var1.getValue();
         }
      });
      return var10;
   }

   public PlayerData getPlayerData(Player var1) {
      return (PlayerData)this.plugin.playerData.get(var1.getName());
   }
}
