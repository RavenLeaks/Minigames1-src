package me.wazup.kitbattle;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Team;

public class ChallengeMap extends Map {
   int playersPerTeam;
   BukkitTask grace;
   BukkitTask timer;
   BukkitTask finish;
   CustomScoreboard scoreboard;
   Team blueTeam;
   Team redTeam;
   HashMap players;

   public ChallengeMap(Kitbattle var1, String var2, List var3, int var4, boolean var5) {
      super(var1, var2, var3, var5);
      this.playersPerTeam = var4;
      this.scoreboard = new CustomScoreboard(var1, true, "" + ChatColor.RED + ChatColor.BOLD + var1.msgs.scoreboard_challenge_title, new String[]{ChatColor.GOLD + "~~~~~~~~~~~", ChatColor.BLUE + "> " + var1.msgs.scoreboard_team1 + ":", String.valueOf(0), " ", ChatColor.RED + "> " + var1.msgs.scoreboard_team2 + ":", String.valueOf(0), " ", ChatColor.AQUA + "> " + var1.msgs.scoreboard_map + ":", var2 + ChatColor.GREEN + " (" + var4 + "v" + var4 + ")", " ", ChatColor.GOLD + "~~~~~~~~~~~"});
      this.blueTeam = this.scoreboard.registerTeam("BLUE");
      this.blueTeam.setPrefix(ChatColor.BLUE.toString());
      this.blueTeam.setAllowFriendlyFire(false);
      this.redTeam = this.scoreboard.registerTeam("RED");
      this.redTeam.setPrefix(ChatColor.RED.toString());
      this.redTeam.setAllowFriendlyFire(false);
      this.players = new HashMap();
   }

   public boolean start(List var1) {
      Iterator var3 = var1.iterator();

      Player var2;
      while(var3.hasNext()) {
         var2 = (Player)var3.next();
         if (var2.isDead()) {
            return false;
         }
      }

      var3 = var1.iterator();

      while(var3.hasNext()) {
         var2 = (Player)var3.next();
         this.players.put(var2.getName(), this.plugin.config.ChallengeLives);
         if (this.plugin.challengesManager != null) {
            this.plugin.challengesManager.players.add(var2.getName());
         }

         this.scoreboard.apply(var2);
         if (this.blueTeam.getSize() < this.playersPerTeam) {
            this.blueTeam.addPlayer(var2);
         } else {
            this.redTeam.addPlayer(var2);
         }

         this.plugin.spectating.remove(var2.getName());
         this.plugin.clearData(var2);
         PlayerData var4 = (PlayerData)this.plugin.playerData.get(var2.getName());
         if (this.plugin.config.challengeKitLock && this.plugin.Kits.containsKey(this.plugin.config.challengeKit.toLowerCase())) {
            Kit var5 = (Kit)this.plugin.Kits.get(this.plugin.config.challengeKit.toLowerCase());
            var4.setKit(var2, var5);
            var5.giveItems(var2);
         } else {
            this.plugin.giveDefaultItems(var2);
            var4.setKit(var2, (Kit)null);
         }

         var4.customScoreboard = null;
         var4.killstreak = 0;
         var4.deathstreak = 0;
         var4.damagers.clear();
         var2.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, this.plugin.config.challengesGraceLength * 20, 9999999));
         var2.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, this.plugin.config.challengesGraceLength * 20, 9999999));
         var2.setNoDamageTicks(this.plugin.config.challengesGraceLength * 20);
         if (this.plugin.tournamentsManager != null) {
            this.plugin.tournamentsManager.remove(var2, false);
         }

         if (this.plugin.challengesManager != null) {
            this.plugin.challengesManager.removeFromQueues(var2);
         }
      }

      String var8 = this.plugin.kb + this.plugin.msgs.TeamColorNotification.replace("%team%", ChatColor.BLUE + "BLUE");
      String var9 = this.plugin.kb + this.plugin.msgs.TeamColorNotification.replace("%team%", ChatColor.RED + "RED");
      Location var10 = (Location)this.spawnpoints.get(0);
      Location var11 = (Location)this.spawnpoints.get(1);
      Iterator var7 = this.blueTeam.getPlayers().iterator();

      OfflinePlayer var6;
      while(var7.hasNext()) {
         var6 = (OfflinePlayer)var7.next();
         ((Player)var6).sendMessage(var8);
         ((Player)var6).teleport(var10);
      }

      var7 = this.redTeam.getPlayers().iterator();

      while(var7.hasNext()) {
         var6 = (OfflinePlayer)var7.next();
         ((Player)var6).sendMessage(var9);
         ((Player)var6).teleport(var11);
      }

      this.scoreboard.replace(this.plugin.msgs.scoreboard_team1, this.blueTeam.getSize(), true);
      this.scoreboard.replace(this.plugin.msgs.scoreboard_team2, this.redTeam.getSize(), true);
      this.grace = (new BukkitRunnable() {
         int seconds;

         {
            this.seconds = ChallengeMap.this.plugin.config.challengesGraceLength;
         }

         public void run() {
            if (ChallengeMap.this.plugin.config.challengesGracePeriodWarnings.contains(this.seconds)) {
               String var1 = ChallengeMap.this.plugin.kb + ChallengeMap.this.plugin.msgs.GraceWarning.replace("%seconds%", String.valueOf(this.seconds));
               Iterator var3 = ChallengeMap.this.plugin.getPlayers(ChallengeMap.this.players.keySet()).iterator();

               while(var3.hasNext()) {
                  Player var2 = (Player)var3.next();
                  var2.sendMessage(var1);
               }
            }

            --this.seconds;
            if (this.seconds == 0) {
               ChallengeMap.this.cancelTasks();
               ChallengeMap.this.startTimer();
               Iterator var5 = ChallengeMap.this.plugin.getPlayers(ChallengeMap.this.players.keySet()).iterator();

               while(var5.hasNext()) {
                  Player var4 = (Player)var5.next();
                  var4.sendMessage(ChallengeMap.this.plugin.kb + ChallengeMap.this.plugin.msgs.GraceEnd);
                  var4.playSound(var4.getLocation(), ChallengeMap.this.plugin.WITHER_SPAWN, 1.0F, 1.0F);
               }
            }

         }
      }).runTaskTimer(this.plugin, 0L, 20L);
      return true;
   }

   public void startTimer() {
      this.timer = (new BukkitRunnable() {
         public void run() {
            ChallengeMap.this.cancelTasks();
            ChallengeMap.this.timer = (new BukkitRunnable() {
               int seconds;

               {
                  this.seconds = ChallengeMap.this.plugin.config.challengesEndWarningsMax;
               }

               public void run() {
                  if (ChallengeMap.this.plugin.config.challengesEndWarnings.contains(this.seconds)) {
                     String var1 = ChallengeMap.this.plugin.kb + ChallengeMap.this.plugin.msgs.ChallengeEndWarning.replace("%seconds%", String.valueOf(this.seconds));
                     Iterator var3 = ChallengeMap.this.plugin.getPlayers(ChallengeMap.this.players.keySet()).iterator();

                     while(var3.hasNext()) {
                        Player var2 = (Player)var3.next();
                        var2.sendMessage(var1);
                        var2.playSound(var2.getLocation(), ChallengeMap.this.plugin.CLICK, 1.0F, 1.0F);
                     }
                  }

                  --this.seconds;
                  if (this.seconds == 0) {
                     ChallengeMap.this.stop();
                  }

               }
            }).runTaskTimer(ChallengeMap.this.plugin, 0L, 20L);
         }
      }).runTaskLater(this.plugin, (long)(this.plugin.config.maxChallengeTime * 20 - (this.plugin.config.challengesGracePeriodWarningsMax * 20 + this.plugin.config.challengesEndWarningsMax * 20)));
   }

   public void kill(final Player var1) {
      if ((Integer)this.players.get(var1.getName()) <= 1) {
         this.remove(var1, true);
      } else {
         this.players.put(var1.getName(), (Integer)this.players.get(var1.getName()) - 1);
         var1.sendMessage(this.plugin.kb + this.plugin.msgs.ChallengePlayerKilled.replace("%lives%", String.valueOf(this.players.get(var1.getName()))).replace("%seconds%", String.valueOf(this.plugin.config.ChallengeRespawnProtectionSeconds)));
         if (this.plugin.config.challengeKitLock && this.plugin.Kits.containsKey(this.plugin.config.challengeKit.toLowerCase())) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
               public void run() {
                  Kit var1x = (Kit)ChallengeMap.this.plugin.Kits.get(ChallengeMap.this.plugin.config.challengeKit.toLowerCase());
                  var1x.giveItems(var1);
               }
            }, 3L);
         }
      }

   }

   public void remove(Player var1, boolean var2) {
      this.players.remove(var1.getName());
      if (this.plugin.challengesManager != null) {
         this.plugin.challengesManager.players.remove(var1.getName());
      }

      if (((PlayerData)this.plugin.playerData.get(var1.getName())).getMap() == null) {
         this.plugin.leave(var1);
      } else {
         this.plugin.resetPlayerToMap(var1, ((PlayerData)this.plugin.playerData.get(var1.getName())).getMap(), false);
         ((PlayerData)this.plugin.playerData.get(var1.getName())).createScoreboard(var1);
      }

      String var3 = this.plugin.kb + (var2 ? this.plugin.msgs.ChallengePlayerEliminated : this.plugin.msgs.ChallengePlayerLeave).replace("%player%", (this.blueTeam.getPlayers().contains(var1) ? ChatColor.BLUE : ChatColor.RED) + var1.getName()).replace("%teamsize%", this.blueTeam.getPlayers().contains(var1) ? ChatColor.BLUE + String.valueOf(this.blueTeam.getSize() - 1) : ChatColor.RED + String.valueOf(this.redTeam.getSize() - 1)).replace("%maxteamsize%", String.valueOf(this.playersPerTeam));
      if (this.blueTeam.getPlayers().contains(var1)) {
         this.blueTeam.removePlayer(var1);
         this.scoreboard.replace(this.plugin.msgs.scoreboard_team1, this.blueTeam.getSize(), true);
      } else {
         this.redTeam.removePlayer(var1);
         this.scoreboard.replace(this.plugin.msgs.scoreboard_team2, this.redTeam.getSize(), true);
      }

      Iterator var5 = this.plugin.getPlayers(this.players.keySet()).iterator();

      while(var5.hasNext()) {
         Player var4 = (Player)var5.next();
         var4.sendMessage(var3);
      }

      if (this.blueTeam.getSize() == 0 || this.redTeam.getSize() == 0) {
         this.finish();
      }

   }

   public void finish() {
      this.cancelTasks();
      Iterator var2 = this.plugin.getPlayers(this.players.keySet()).iterator();

      while(var2.hasNext()) {
         OfflinePlayer var1 = (OfflinePlayer)var2.next();
         Player var3 = (Player)var1;
         var3.sendMessage(this.plugin.kb + this.plugin.msgs.ChallengePlayerWin);
         var3.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, this.plugin.config.challengeCelebrationLength * 20, 9999999));
         var3.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, this.plugin.config.challengesGraceLength * 20, 9999999));
         Iterator var5 = this.plugin.config.challengesWinnerCommands.iterator();

         while(var5.hasNext()) {
            String var4 = (String)var5.next();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), var4.replace("%winner%", var3.getName()));
         }

         ((PlayerData)this.plugin.playerData.get(var3.getName())).addChallengeWins(var3);
      }

      this.finish = (new BukkitRunnable() {
         int seconds;
         boolean fireworks;

         {
            this.seconds = ChallengeMap.this.plugin.config.challengeCelebrationLength;
            this.fireworks = false;
         }

         public void run() {
            if (ChallengeMap.this.plugin.config.challengesFireworksCelebration) {
               this.fireworks = !this.fireworks;
               if (this.fireworks) {
                  Iterator var2 = ChallengeMap.this.plugin.getPlayers(ChallengeMap.this.players.keySet()).iterator();

                  while(var2.hasNext()) {
                     Player var1 = (Player)var2.next();
                     ChallengeMap.this.plugin.listen.spawnFirework(var1.getLocation());
                  }
               }
            }

            --this.seconds;
            if (this.seconds == 0) {
               ChallengeMap.this.stop();
            }

         }
      }).runTaskTimer(this.plugin, 0L, 20L);
   }

   public void stop() {
      List var1 = this.plugin.getPlayers(this.players.keySet());
      if (this.plugin.challengesManager != null) {
         this.plugin.challengesManager.players.removeAll(this.players.keySet());
      }

      this.players.clear();
      Iterator var3 = this.blueTeam.getPlayers().iterator();

      OfflinePlayer var2;
      while(var3.hasNext()) {
         var2 = (OfflinePlayer)var3.next();
         this.blueTeam.removePlayer(var2);
      }

      var3 = this.redTeam.getPlayers().iterator();

      while(var3.hasNext()) {
         var2 = (OfflinePlayer)var3.next();
         this.redTeam.removePlayer(var2);
      }

      var3 = var1.iterator();

      while(var3.hasNext()) {
         Player var4 = (Player)var3.next();
         if (((PlayerData)this.plugin.playerData.get(var4.getName())).getMap() == null) {
            this.plugin.leave(var4);
         } else {
            this.plugin.resetPlayerToMap(var4, ((PlayerData)this.plugin.playerData.get(var4.getName())).getMap(), false);
            ((PlayerData)this.plugin.playerData.get(var4.getName())).createScoreboard(var4);
         }
      }

      this.cancelTasks();
      if (this.plugin.challengesManager != null) {
         this.plugin.challengesManager.checkQueue(this.playersPerTeam);
      }

   }

   public void cancelTasks() {
      if (this.grace != null) {
         this.grace.cancel();
         this.grace = null;
      }

      if (this.timer != null) {
         this.timer.cancel();
         this.timer = null;
      }

      if (this.finish != null) {
         this.finish.cancel();
         this.finish = null;
      }

   }

   public boolean isAvailable() {
      return this.enabled && this.spawnpoints.size() > 1 && !this.isRunning();
   }

   public boolean isRunning() {
      return this.grace != null || this.timer != null || this.finish != null;
   }
}
