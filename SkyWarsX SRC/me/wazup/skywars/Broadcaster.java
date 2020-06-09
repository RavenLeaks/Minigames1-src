package me.wazup.skywars;

import java.util.ArrayList;
import java.util.Iterator;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

class Broadcaster {
   BukkitTask task;
   String top_line;
   String bottom_line;
   ArrayList messages = new ArrayList();
   private Skywars plugin;

   public Broadcaster(Skywars var1) {
      this.plugin = var1;
   }

   public void loadMessages(FileConfiguration var1) {
      this.top_line = ChatColor.translateAlternateColorCodes('&', var1.getString("top-line"));
      Iterator var3 = var1.getStringList("Messages").iterator();

      while(var3.hasNext()) {
         String var2 = (String)var3.next();
         this.messages.add(ChatColor.translateAlternateColorCodes('&', var2));
      }

      this.bottom_line = ChatColor.translateAlternateColorCodes('&', var1.getString("bottom-line"));
      if (this.task != null) {
         this.task.cancel();
      }

      if (!this.messages.isEmpty()) {
         this.start();
      }

   }

   public void start() {
      this.task = (new BukkitRunnable() {
         int index = 0;

         public void run() {
            if (this.index >= Broadcaster.this.messages.size()) {
               this.index = 0;
            }

            String var1 = (String)Broadcaster.this.messages.get(this.index);
            Iterator var3 = Broadcaster.this.plugin.getPlayers(Broadcaster.this.plugin.players).iterator();

            while(var3.hasNext()) {
               Player var2 = (Player)var3.next();
               var2.sendMessage(Broadcaster.this.top_line);
               var2.sendMessage(var1);
               var2.sendMessage(Broadcaster.this.bottom_line);
            }

            ++this.index;
         }
      }).runTaskTimer(this.plugin, (long)this.plugin.config.broadcasterSendEvery, (long)this.plugin.config.broadcasterSendEvery);
   }
}
