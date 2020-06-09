package me.wazup.eggwars;

import java.util.ArrayList;
import java.util.Iterator;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

class RollbackManager {
   private ArrayList queue = new ArrayList();
   private ArrayList rollbacking = new ArrayList();
   private BukkitTask task;
   private Eggwars plugin;

   public RollbackManager(Eggwars var1) {
      this.plugin = var1;
   }

   public void add(Arena var1) {
      if (!this.queue.contains(var1) && !this.rollbacking.contains(var1)) {
         if (this.rollbacking.size() >= this.plugin.config.rollback_queue_size) {
            var1.state = Enums.ArenaState.QUEUED;
            var1.updateItem(0);
            this.queue.add(var1);
         } else {
            var1.rollback();
            this.rollbacking.add(var1);
            if (this.task == null) {
               this.task = (new BukkitRunnable() {
                  public void run() {
                     ArrayList var1 = (ArrayList)RollbackManager.this.rollbacking.clone();
                     Iterator var3 = var1.iterator();

                     while(true) {
                        Arena var2;
                        do {
                           if (!var3.hasNext()) {
                              return;
                           }

                           var2 = (Arena)var3.next();
                        } while(var2.state == Enums.ArenaState.ROLLBACKING);

                        RollbackManager.this.rollbacking.remove(var2);
                        if (RollbackManager.this.plugin.isOneGamePerServer()) {
                           Iterator var5 = RollbackManager.this.plugin.getOnlinePlayers().iterator();

                           while(var5.hasNext()) {
                              Player var4 = (Player)var5.next();
                              var2.join(var4);
                           }
                        }

                        if (RollbackManager.this.rollbacking.isEmpty() && RollbackManager.this.queue.isEmpty()) {
                           this.cancel();
                           RollbackManager.this.task = null;
                           return;
                        }

                        if (!RollbackManager.this.queue.isEmpty()) {
                           RollbackManager.this.rollbacking.add((Arena)RollbackManager.this.queue.get(0));
                           ((Arena)RollbackManager.this.queue.get(0)).rollback();
                           RollbackManager.this.queue.remove(0);
                        }
                     }
                  }
               }).runTaskTimer(this.plugin, 40L, 40L);
            }

         }
      }
   }

   public void reset() {
      this.queue.clear();
      this.rollbacking.clear();
      if (this.task != null) {
         this.task.cancel();
      }

      this.task = null;
   }
}
