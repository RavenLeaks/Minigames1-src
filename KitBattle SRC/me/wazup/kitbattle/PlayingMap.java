package me.wazup.kitbattle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class PlayingMap extends Map {
   List spawnCuboids;
   HashMap signs;
   List players;

   public PlayingMap(Kitbattle var1, String var2, List var3, List var4, boolean var5, HashMap var6) {
      super(var1, var2, var3, var5);
      this.spawnCuboids = var4;
      this.players = new ArrayList();
      this.signs = var6;
   }

   public void removePlayers() {
      if (this.plugin.bungeeMode != null) {
         if (this.plugin.bungeeMode.getMap().name.equals(this.name)) {
            this.plugin.bungeeMode.changeMap();
         } else {
            this.plugin.bungeeMode.updateMap();
         }

      } else {
         int var1 = 0;
         Iterator var3 = this.plugin.playingMaps.values().iterator();

         while(var3.hasNext()) {
            PlayingMap var2 = (PlayingMap)var3.next();
            if (var2.enabled && var2.spawnpoints.size() > 0) {
               ++var1;
            }
         }

         if (var1 > 0) {
            var3 = this.plugin.getPlayers(this.plugin.players).iterator();

            while(true) {
               while(true) {
                  Player var5;
                  do {
                     if (!var3.hasNext()) {
                        return;
                     }

                     var5 = (Player)var3.next();
                  } while(!((PlayerData)this.plugin.playerData.get(var5.getName())).getMap().name.equals(this.name));

                  if (!this.plugin.isInTournament(var5) && !this.plugin.isInChallenge(var5)) {
                     var5.sendMessage(this.plugin.kb + this.plugin.msgs.MapDeletedSendToAnotherMap);
                     var5.performCommand("kb join " + this.plugin.playingMaps.values().iterator().next());
                  } else {
                     ((PlayerData)this.plugin.playerData.get(var5.getName())).setMap(var5, (PlayingMap)this.plugin.playingMaps.values().iterator().next());
                  }
               }
            }
         } else {
            ArrayList var6 = new ArrayList();
            Iterator var4 = this.plugin.getPlayers(this.plugin.players).iterator();

            Player var7;
            while(var4.hasNext()) {
               var7 = (Player)var4.next();
               if (((PlayerData)this.plugin.playerData.get(var7.getName())).getMap().name.equals(this.name)) {
                  var7.sendMessage(this.plugin.kb + this.plugin.msgs.MapDeletedKick);
                  var6.add(var7);
               }
            }

            var4 = var6.iterator();

            while(var4.hasNext()) {
               var7 = (Player)var4.next();
               var7.performCommand("kb leave");
            }

         }
      }
   }

   public boolean isInSpawn(Player var1) {
      Iterator var3 = this.spawnCuboids.iterator();

      while(var3.hasNext()) {
         Cuboid var2 = (Cuboid)var3.next();
         if (var2.contains(var1.getLocation())) {
            return true;
         }
      }

      return false;
   }

   public void updateSignPlayers() {
      Iterator var2 = this.signs.keySet().iterator();

      while(var2.hasNext()) {
         Location var1 = (Location)var2.next();
         if (var1.getBlock().getState() instanceof Sign) {
            Sign var3 = (Sign)var1.getBlock().getState();
            var3.setLine(3, ChatColor.AQUA + String.valueOf(this.players.size()) + ChatColor.YELLOW + " Players");
            var3.update(true);
         }
      }

   }
}
