package me.wazup.kitbattle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class BungeeMode {
   private Kitbattle plugin;
   private PlayingMap map;
   private int mapIndex;
   private BukkitTask shuffler;
   private BukkitTask countdown;
   HashMap playerVotes;
   Inventory voteInventory;

   public BungeeMode(Kitbattle var1) {
      this.plugin = var1;
      this.mapIndex = 0;
      this.updateMap();
   }

   public void updateMap() {
      List var1 = this.getAvailableMaps();
      if (!var1.isEmpty()) {
         if (this.mapIndex >= var1.size()) {
            this.mapIndex = 0;
         }

         this.map = (PlayingMap)var1.get(this.mapIndex);
         if (var1.size() > 1) {
            if (this.shuffler == null) {
               this.startShuffler();
            }

            this.voteInventory = Bukkit.createInventory((InventoryHolder)null, this.plugin.getInventorySize(var1.size() - 1), (String)this.plugin.msgs.inventories.get("Map-Vote"));
            this.playerVotes = new HashMap();
            Iterator var3 = var1.iterator();

            while(var3.hasNext()) {
               PlayingMap var2 = (PlayingMap)var3.next();
               if (!this.map.name.equals(var2.name)) {
                  this.voteInventory.addItem(new ItemStack[]{(new ItemStackBuilder(Material.NAME_TAG)).setName(ChatColor.GREEN + var2.name).addLore("" + ChatColor.GOLD + ChatColor.BOLD + "Votes: " + ChatColor.YELLOW + 0).build()});
               }
            }
         } else {
            this.cancelTasks();
         }
      } else {
         this.kickAll();
      }

   }

   public void startShuffler() {
      this.shuffler = (new BukkitRunnable() {
         public void run() {
            BungeeMode.this.cancelTasks();
            BungeeMode.this.countdown = (new BukkitRunnable() {
               int seconds;

               {
                  this.seconds = BungeeMode.this.plugin.config.highestTimeShownBeforeShuffle;
               }

               public void run() {
                  if (BungeeMode.this.plugin.config.timeShownBeforeShuffle.contains(this.seconds)) {
                     String var1 = BungeeMode.this.plugin.kb + BungeeMode.this.plugin.msgs.MapSwitchCountDown.replace("%time%", String.valueOf(this.seconds));
                     Iterator var3 = BungeeMode.this.plugin.getOnlinePlayers().iterator();

                     while(var3.hasNext()) {
                        Player var2 = (Player)var3.next();
                        var2.sendMessage(var1);
                        var2.playSound(var2.getLocation(), BungeeMode.this.plugin.CLICK, 1.0F, 1.0F);
                     }
                  }

                  --this.seconds;
                  if (this.seconds == 0) {
                     BungeeMode.this.cancelTasks();
                     BungeeMode.this.changeMap();
                  }

               }
            }).runTaskTimer(BungeeMode.this.plugin, 0L, 20L);
         }
      }).runTaskLater(this.plugin, (long)((this.plugin.config.shuffleEveryMinutes * 60 - this.plugin.config.highestTimeShownBeforeShuffle) * 20));
   }

   public void changeMap() {
      ++this.mapIndex;
      if (this.playerVotes != null && !this.playerVotes.isEmpty()) {
         int var1 = 0;
         HashMap var2 = new HashMap();
         List var3 = this.getAvailableMaps();
         Iterator var5 = var3.iterator();

         int var6;
         while(var5.hasNext()) {
            PlayingMap var4 = (PlayingMap)var5.next();
            var6 = this.getVotes(var4.name.toLowerCase());
            var2.put(var4.name.toLowerCase(), var6);
            if (var6 > var1) {
               var1 = var6;
            }
         }

         ArrayList var9 = new ArrayList();
         Iterator var11 = var2.keySet().iterator();

         String var10;
         while(var11.hasNext()) {
            var10 = (String)var11.next();
            if (((Integer)var2.get(var10)).equals(var1)) {
               var9.add(var10);
            }
         }

         var10 = (String)var9.get(this.plugin.random.nextInt(var9.size()));

         for(var6 = 0; var6 < var3.size(); ++var6) {
            if (((PlayingMap)var3.get(var6)).name.toLowerCase().equals(var10)) {
               this.mapIndex = var6;
               break;
            }
         }
      }

      this.updateMap();
      if (this.map != null) {
         Iterator var8 = this.plugin.getOnlinePlayers().iterator();

         while(true) {
            while(var8.hasNext()) {
               Player var7 = (Player)var8.next();
               if (!this.plugin.isInTournament(var7) && !this.plugin.isInChallenge(var7)) {
                  this.plugin.resetPlayerToMap(var7, this.map, true);
               } else {
                  ((PlayerData)this.plugin.playerData.get(var7.getName())).setMap(var7, this.map);
               }
            }

            return;
         }
      } else {
         this.kickAll();
      }
   }

   public void vote(Player var1, ItemStack var2) {
      String var3 = ChatColor.stripColor(var2.getItemMeta().getDisplayName()).toLowerCase();
      String var4 = "";
      if (this.plugin.playingMaps.containsKey(var3) && ((PlayingMap)this.plugin.playingMaps.get(var3)).isAvailable()) {
         if (this.playerVotes.containsKey(var1.getName())) {
            if (((String)this.playerVotes.get(var1.getName())).equals(var3)) {
               return;
            }

            var4 = (String)this.playerVotes.get(var1.getName());
         }

         this.playerVotes.put(var1.getName(), var3);
         this.updateVotes(var3);
         if (!var4.isEmpty()) {
            this.updateVotes(var4);
         }

      }
   }

   public void updateVotes(String var1) {
      ItemStack[] var5;
      int var4 = (var5 = this.voteInventory.getContents()).length;

      for(int var3 = 0; var3 < var4; ++var3) {
         ItemStack var2 = var5[var3];
         if (var2 != null && ChatColor.stripColor(var2.getItemMeta().getDisplayName().toLowerCase()).equals(var1)) {
            (new ItemStackBuilder(var2)).replaceLore("Votes", "" + ChatColor.GOLD + ChatColor.BOLD + "Votes: " + ChatColor.YELLOW + this.getVotes(var1)).build();
            break;
         }
      }

   }

   public int getVotes(String var1) {
      int var2 = 0;
      Iterator var4 = this.playerVotes.values().iterator();

      while(var4.hasNext()) {
         String var3 = (String)var4.next();
         if (var3.equals(var1)) {
            ++var2;
         }
      }

      return var2;
   }

   public void kickAll() {
      Iterator var2 = this.plugin.getOnlinePlayers().iterator();

      while(var2.hasNext()) {
         Player var1 = (Player)var2.next();
         var1.kickPlayer(this.plugin.kb + this.plugin.msgs.NoAvailableMaps);
      }

      this.plugin.bungeeMode = null;
      this.cancelTasks();
   }

   public PlayingMap getMap() {
      return this.map;
   }

   public boolean isShufflerRunning() {
      return this.shuffler != null || this.countdown != null;
   }

   public List getAvailableMaps() {
      ArrayList var1 = new ArrayList();
      Iterator var3 = this.plugin.playingMaps.values().iterator();

      while(var3.hasNext()) {
         PlayingMap var2 = (PlayingMap)var3.next();
         if (var2.isAvailable()) {
            var1.add(var2);
         }
      }

      return var1;
   }

   private void cancelTasks() {
      if (this.shuffler != null) {
         this.shuffler.cancel();
         this.shuffler = null;
      }

      if (this.countdown != null) {
         this.countdown.cancel();
         this.countdown = null;
      }

   }
}
