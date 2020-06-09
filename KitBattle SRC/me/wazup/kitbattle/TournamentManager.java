package me.wazup.kitbattle;

import java.util.ArrayList;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class TournamentManager {
   private Kitbattle plugin;
   private ArrayList players;
   private ArrayList queue;
   private BukkitTask countdown;
   private BukkitTask grace;
   private BukkitTask timer;
   private BukkitTask finishing;
   TournamentMap map;

   public TournamentManager(Kitbattle var1) {
      this.plugin = var1;
      this.players = new ArrayList();
      this.queue = new ArrayList();
   }

   public void add(Player var1) {
      if (this.isRunning() && !this.queue.contains(var1.getName())) {
         this.queue.add(var1.getName());
      } else {
         if (this.players.contains(var1.getName())) {
            return;
         }

         this.players.add(var1.getName());
         if (this.players.size() >= this.plugin.config.tournamentsMinPlayers && !this.isStarting()) {
            this.startCountdown();
         }
      }

   }

   private void startCountdown() {
      this.countdown = (new BukkitRunnable() {
         int seconds;

         {
            this.seconds = TournamentManager.this.plugin.config.tournamentsCountdownLength;
         }

         public void run() {
            if (TournamentManager.this.plugin.config.tournamentsTimeShownDuringCountdown.contains(this.seconds)) {
               String var1 = TournamentManager.this.plugin.kb + TournamentManager.this.plugin.msgs.TournamentCountdown.replace("%seconds%", String.valueOf(this.seconds));
               Iterator var3 = TournamentManager.this.plugin.getPlayers(TournamentManager.this.plugin.players).iterator();

               while(var3.hasNext()) {
                  Player var2 = (Player)var3.next();
                  var2.sendMessage(var1);
                  var2.playSound(var2.getLocation(), TournamentManager.this.plugin.CLICK, 1.0F, 1.0F);
               }
            }

            --this.seconds;
            if (this.seconds == 0) {
               TournamentManager.this.start();
            }

         }
      }).runTaskTimer(this.plugin, 0L, 20L);
   }

   private void start() {
      this.cancelTasks();
      ArrayList var1 = new ArrayList();
      Iterator var3 = this.plugin.tournamentMaps.values().iterator();

      while(var3.hasNext()) {
         TournamentMap var2 = (TournamentMap)var3.next();
         if (var2.isAvailable()) {
            var1.add(var2);
         }
      }

      if (var1.isEmpty()) {
         var3 = this.plugin.getPlayers(this.players).iterator();

         while(var3.hasNext()) {
            Player var6 = (Player)var3.next();
            var6.sendMessage(this.plugin.kb + this.plugin.msgs.TournamentCancelDueToMaps);
         }

         this.plugin.tournamentsManager = null;
      } else {
         this.map = (TournamentMap)var1.get(this.plugin.random.nextInt(var1.size()));
         String var5 = this.plugin.kb + this.plugin.msgs.TournamentStart.replace("%map%", this.map.name);
         Iterator var4 = this.plugin.getPlayers(this.plugin.players).iterator();

         Player var7;
         while(var4.hasNext()) {
            var7 = (Player)var4.next();
            var7.sendMessage(var5);
         }

         this.startGrace();
         var4 = this.plugin.getPlayers(this.players).iterator();

         while(var4.hasNext()) {
            var7 = (Player)var4.next();
            if (this.plugin.challengesManager != null) {
               this.plugin.challengesManager.removeFromQueues(var7);
            }

            if (!var7.isDead()) {
               this.plugin.resetPlayerToMap(var7, this.map, false);
               var7.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, this.plugin.config.tournamentsGracePeriod * 20, 9999999));
               var7.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, this.plugin.config.tournamentsGracePeriod * 20, 9999999));
               var7.setNoDamageTicks(this.plugin.config.tournamentsGracePeriod * 20);
            } else {
               this.players.remove(var7.getName());
            }
         }

         if (this.players.size() < 2) {
            this.stop();
         }

      }
   }

   private void startGrace() {
      this.cancelTasks();
      this.grace = (new BukkitRunnable() {
         int seconds;

         {
            this.seconds = TournamentManager.this.plugin.config.tournamentsGracePeriod;
         }

         public void run() {
            if (TournamentManager.this.plugin.config.tournamentsGracePeriodWarnings.contains(this.seconds)) {
               String var1 = TournamentManager.this.plugin.kb + TournamentManager.this.plugin.msgs.GraceWarning.replace("%seconds%", String.valueOf(this.seconds));
               Iterator var3 = TournamentManager.this.plugin.getPlayers(TournamentManager.this.players).iterator();

               while(var3.hasNext()) {
                  Player var2 = (Player)var3.next();
                  var2.sendMessage(var1);
               }
            }

            --this.seconds;
            if (this.seconds == 0) {
               TournamentManager.this.cancelTasks();
               TournamentManager.this.startTimer();
               Iterator var5 = TournamentManager.this.plugin.getPlayers(TournamentManager.this.players).iterator();

               while(var5.hasNext()) {
                  Player var4 = (Player)var5.next();
                  var4.sendMessage(TournamentManager.this.plugin.kb + TournamentManager.this.plugin.msgs.GraceEnd);
                  var4.playSound(var4.getLocation(), TournamentManager.this.plugin.WITHER_SPAWN, 1.0F, 1.0F);
               }
            }

         }
      }).runTaskTimer(this.plugin, 0L, 20L);
   }

   public void startTimer() {
      this.timer = (new BukkitRunnable() {
         public void run() {
            TournamentManager.this.cancelTasks();
            TournamentManager.this.timer = (new BukkitRunnable() {
               int seconds;

               {
                  this.seconds = TournamentManager.this.plugin.config.tournamentsEndWarningsMax;
               }

               public void run() {
                  if (TournamentManager.this.plugin.config.tournamentsEndWarnings.contains(this.seconds)) {
                     String var1 = TournamentManager.this.plugin.kb + TournamentManager.this.plugin.msgs.TournamentEndWarning.replace("%seconds%", String.valueOf(this.seconds));
                     Iterator var3 = TournamentManager.this.plugin.getPlayers(TournamentManager.this.players).iterator();

                     while(var3.hasNext()) {
                        Player var2 = (Player)var3.next();
                        var2.sendMessage(var1);
                        var2.playSound(var2.getLocation(), TournamentManager.this.plugin.CLICK, 1.0F, 1.0F);
                     }
                  }

                  --this.seconds;
                  if (this.seconds == 0) {
                     TournamentManager.this.stop();
                  }

               }
            }).runTaskTimer(TournamentManager.this.plugin, 0L, 20L);
         }
      }).runTaskLater(this.plugin, (long)(this.plugin.config.maxTournamentsTime * 20 - (this.plugin.config.tournamentsGracePeriodWarningsMax * 20 + this.plugin.config.tournamentsEndWarningsMax * 20)));
   }

   public void kill(Player var1) {
      this.players.remove(var1.getName());
      String var2 = this.plugin.kb + this.plugin.msgs.TournamentPlayerEliminated.replace("%player%", var1.getName()).replace("%remaining%", String.valueOf(this.players.size()));
      Iterator var4 = this.plugin.getPlayers(this.players).iterator();

      while(var4.hasNext()) {
         Player var3 = (Player)var4.next();
         var3.sendMessage(var2);
      }

      this.checkFinish();
   }

   public void remove(Player var1, boolean var2) {
      if (this.queue.contains(var1.getName())) {
         this.queue.remove(var1.getName());
      } else if (this.players.contains(var1.getName())) {
         this.players.remove(var1.getName());
         if (this.isStarting()) {
            if (this.players.size() < this.plugin.config.tournamentsMinPlayers) {
               Iterator var4 = this.plugin.getPlayers(this.plugin.players).iterator();

               while(var4.hasNext()) {
                  Player var3 = (Player)var4.next();
                  var3.sendMessage(this.plugin.kb + this.plugin.msgs.TournamentCancelDueToPlayers);
                  var3.playSound(var1.getLocation(), this.plugin.WITHER_SHOOT, 1.0F, 1.0F);
               }

               this.cancelTasks();
            }
         } else if (this.isRunning()) {
            PlayerData var7 = (PlayerData)this.plugin.playerData.get(var1.getName());
            if (var7.getMap() == null) {
               this.plugin.leave(var1);
            } else {
               this.plugin.resetPlayerToMap(var1, var7.getMap(), false);
            }

            if (var2) {
               String var8 = this.plugin.kb + this.plugin.msgs.TournamentPlayerLeave.replace("%player%", var1.getName()).replace("%remaining%", String.valueOf(this.players.size()));
               Iterator var6 = this.plugin.getPlayers(this.players).iterator();

               while(var6.hasNext()) {
                  Player var5 = (Player)var6.next();
                  var5.sendMessage(var8);
               }

               this.checkFinish();
            }
         }
      }

   }

   private void checkFinish() {
      if (this.players.size() < 2 && this.finishing == null) {
         this.finish();
      }

   }

   private void finish() {
      this.cancelTasks();
      if (this.players.size() == 1) {
         final Player var1 = Bukkit.getPlayer((String)this.players.get(0));
         if (var1 != null) {
            ((PlayerData)this.plugin.playerData.get(var1.getName())).addTournamentWins(var1);
            var1.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, this.plugin.config.celebrationLength * 20, 9999999));
            var1.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, this.plugin.config.challengesGraceLength * 20, 9999999));
            String var2 = this.plugin.kb + this.plugin.msgs.TournamentPlayerWin.replace("%player%", var1.getName());
            Iterator var4 = this.plugin.getPlayers(this.plugin.players).iterator();

            while(var4.hasNext()) {
               Player var3 = (Player)var4.next();
               var3.sendMessage(var2);
            }

            var4 = this.plugin.config.tournamentsWinnerCommands.iterator();

            while(var4.hasNext()) {
               String var5 = (String)var4.next();
               Bukkit.dispatchCommand(Bukkit.getConsoleSender(), var5.replace("%winner%", var1.getName()));
            }

            this.finishing = (new BukkitRunnable() {
               int seconds;
               boolean fireworks;

               {
                  this.seconds = TournamentManager.this.plugin.config.celebrationLength;
                  this.fireworks = false;
               }

               public void run() {
                  if (TournamentManager.this.plugin.config.tournamentsFireworksCelebration && var1 != null && TournamentManager.this.players.contains(var1.getName())) {
                     this.fireworks = !this.fireworks;
                     if (this.fireworks) {
                        TournamentManager.this.plugin.listen.spawnFirework(var1.getLocation());
                     }
                  }

                  --this.seconds;
                  if (this.seconds == 0 || var1 == null || !TournamentManager.this.players.contains(var1.getName())) {
                     TournamentManager.this.stop();
                  }

               }
            }).runTaskTimer(this.plugin, 0L, 20L);
         }
      }

   }

   public void stop() {
      Iterator var2 = this.plugin.getPlayers(this.players).iterator();

      while(var2.hasNext()) {
         Player var1 = (Player)var2.next();
         this.remove(var1, false);
      }

      this.cancelTasks();
      ArrayList var4 = new ArrayList();
      Iterator var3 = this.plugin.tournamentMaps.values().iterator();

      while(var3.hasNext()) {
         TournamentMap var5 = (TournamentMap)var3.next();
         if (var5.isAvailable()) {
            var4.add(var5);
         }
      }

      if (!var4.isEmpty()) {
         this.map = null;
         this.applyQueue();
      } else {
         this.plugin.tournamentsManager = null;
      }

   }

   private void applyQueue() {
      Iterator var2 = this.plugin.getPlayers(this.queue).iterator();

      while(var2.hasNext()) {
         Player var1 = (Player)var2.next();
         this.add(var1);
      }

      this.queue.clear();
   }

   public void clearQueue() {
      this.queue.clear();
   }

   public boolean contains(Player var1) {
      return this.players.contains(var1.getName());
   }

   public boolean isQueueing(Player var1) {
      return this.queue.contains(var1.getName());
   }

   public boolean isStarting() {
      return this.countdown != null;
   }

   public boolean isRunning() {
      return this.grace != null || this.timer != null || this.finishing != null;
   }

   public int getSize() {
      return this.players.size();
   }

   public void cancelTasks() {
      if (this.countdown != null) {
         this.countdown.cancel();
         this.countdown = null;
      }

      if (this.grace != null) {
         this.grace.cancel();
         this.grace = null;
      }

      if (this.timer != null) {
         this.timer.cancel();
         this.timer = null;
      }

      if (this.finishing != null) {
         this.finishing.cancel();
         this.finishing = null;
      }

   }
}
