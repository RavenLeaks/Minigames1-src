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
import org.bukkit.inventory.ItemStack;

public class ChallengesManager {
   private Kitbattle plugin;
   HashMap queues;
   List players;

   public ChallengesManager(Kitbattle var1) {
      this.plugin = var1;
      this.queues = new HashMap();
      Iterator var3 = var1.challengeMaps.values().iterator();

      while(var3.hasNext()) {
         ChallengeMap var2 = (ChallengeMap)var3.next();
         if (!this.queues.containsKey(var2.playersPerTeam)) {
            this.queues.put(var2.playersPerTeam, new ArrayList());
         }
      }

      this.players = new ArrayList();
   }

   public void add(Player var1, int var2) {
      if (this.queues.containsKey(var2) && !((List)this.queues.get(var2)).contains(var1.getName())) {
         ((List)this.queues.get(var2)).add(var1.getName());
         this.checkQueue(var2);
      }
   }

   public void remove(Player var1, int var2) {
      if (this.queues.containsKey(var2)) {
         ((List)this.queues.get(var2)).remove(var1.getName());
      }
   }

   public boolean isInQueue(Player var1, int var2) {
      return !this.queues.containsKey(var2) ? false : ((List)this.queues.get(var2)).contains(var1.getName());
   }

   public void removeFromQueues(Player var1) {
      Iterator var3 = this.queues.keySet().iterator();

      while(var3.hasNext()) {
         int var2 = (Integer)var3.next();
         ((List)this.queues.get(var2)).remove(var1.getName());
      }

   }

   public void checkQueue(int var1) {
      List var2 = (List)this.queues.get(var1);
      if (var2.size() >= var1 * 2) {
         Iterator var4 = this.plugin.challengeMaps.values().iterator();

         while(var4.hasNext()) {
            ChallengeMap var3 = (ChallengeMap)var4.next();
            if (var3.playersPerTeam == var1 && var3.isAvailable()) {
               var3.start(this.plugin.getPlayers(((List)this.queues.get(var1)).subList(0, var1 * 2)));
               break;
            }
         }
      }

   }

   public void openMenu(Player var1) {
      Inventory var2 = Bukkit.createInventory(var1, 9, (String)this.plugin.msgs.inventories.get("Queue"));
      Iterator var4 = this.queues.keySet().iterator();

      while(var4.hasNext()) {
         int var3 = (Integer)var4.next();
         var2.addItem(new ItemStack[]{(new ItemStackBuilder(Material.INK_SACK)).setDurability(((List)this.queues.get(var3)).contains(var1.getName()) ? 10 : 8).setName(ChatColor.AQUA + var3 + "v" + var3).build()});
      }

      var1.openInventory(var2);
   }
}
