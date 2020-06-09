package me.wazup.kitbattle;

import io.puharesource.mc.titlemanager.api.ActionbarTitleObject;
import io.puharesource.mc.titlemanager.api.TitleObject;
import io.puharesource.mc.titlemanager.api.TitleObject.TitleType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class KBListener implements Listener {
   String kb;
   private Kitbattle plugin;
   ItemStack soup;
   ItemStack air;
   ItemStack potion;

   public KBListener(Kitbattle var1, String var2) {
      this.soup = new ItemStack(Material.MUSHROOM_SOUP);
      this.air = new ItemStack(Material.AIR);
      this.plugin = var1;
      this.kb = var2;
      if (Material.getMaterial("SPLASH_POTION") != null) {
         ItemStackBuilder var3 = new ItemStackBuilder(Material.getMaterial("SPLASH_POTION"));
         var3.setPotionEffect(PotionType.INSTANT_HEAL, false, true);
         this.potion = var3.build();
      } else {
         this.potion = (new ItemStackBuilder(Material.POTION)).setDurability(16421).build();
      }

   }

   @EventHandler
   public void onPlayerBreakBlock(BlockBreakEvent var1) {
      Player var2 = var1.getPlayer();
      if (this.plugin.players.contains(var2.getName()) && !this.plugin.editmode.contains(var2.getName())) {
         var1.setCancelled(true);
      }

      if (!var1.isCancelled()) {
         if (var1.getBlock().getState() instanceof Sign) {
            Sign var3 = (Sign)var1.getBlock().getState();
            if (var3.getLine(0).equals(this.plugin.config.SignsPrefix)) {
               if (!var2.hasPermission("kitbattle.breaksigns")) {
                  var2.sendMessage(this.kb + this.plugin.msgs.nopermission);
                  var1.setCancelled(true);
                  return;
               }

               if (var3.getLine(1).contains("Top #") && this.plugin.topSigns.containsKey(var3.getLocation())) {
                  int var8 = (Integer)this.plugin.topSigns.get(var3.getLocation());
                  this.plugin.fileManager.getConfig("signs.yml").set("Top." + var8, (Object)null);
                  this.plugin.fileManager.saveConfig("signs.yml");
                  this.plugin.topSigns.remove(var3.getLocation());
                  var2.sendMessage(this.kb + "You have broken a top sign!");
                  this.plugin.fixUpdater();
                  if (this.plugin.Updater == null) {
                     var2.sendMessage(this.kb + "Top signs updater is no longer running");
                  }

                  return;
               }

               if (var3.getLine(1).equals(this.plugin.config.JoinPrefix)) {
                  String var4 = ChatColor.stripColor(var3.getLine(2).toLowerCase());
                  if (this.plugin.playingMaps.containsKey(var4)) {
                     PlayingMap var5 = (PlayingMap)this.plugin.playingMaps.get(var4);
                     if (var5.signs.containsKey(var3.getLocation())) {
                        int var6 = (Integer)var5.signs.get(var3.getLocation());
                        FileConfiguration var7 = this.plugin.fileManager.getConfig("maps.yml");
                        var7.set("Maps." + var5.name + ".Signs." + var6, (Object)null);
                        this.plugin.fileManager.saveConfig("maps.yml");
                        var5.signs.remove(var3.getLocation());
                        var2.sendMessage(this.kb + "You have " + ChatColor.RED + "removed" + ChatColor.GRAY + " the join sign with the id #" + ChatColor.LIGHT_PURPLE + var6);
                     }
                  }
               }
            }
         }

      }
   }

   @EventHandler
   public void onPlayerBlockPlace(BlockPlaceEvent var1) {
      Player var2 = var1.getPlayer();
      if (this.plugin.players.contains(var2.getName()) && !this.plugin.editmode.contains(var2.getName())) {
         var1.setCancelled(true);
      }

   }

   @EventHandler
   public void onPlayerLogin(PlayerLoginEvent var1) {
      if (this.plugin.config.bungeeMode && (this.plugin.bungeeMode == null || this.plugin.bungeeMode.getMap() == null)) {
         var1.disallow(Result.KICK_OTHER, this.kb + this.plugin.msgs.NoAvailableMaps);
      }

   }

   @EventHandler
   public void onPlayerJoin(PlayerJoinEvent var1) {
      Player var2 = var1.getPlayer();
      this.plugin.playerData.put(var2.getName(), new PlayerData(var2, this.plugin));
      if (this.plugin.getConfig().getBoolean("Check-For-Updates") && var2.hasPermission("kitbattle.admin")) {
         var2.sendMessage(this.plugin.msgs.prefix + (this.plugin.availableUpdate ? "Found a new available version! " + ChatColor.LIGHT_PURPLE + "download at https://goo.gl/acFc6M" : "Looks like you have the latest version installed!"));
      }

      if (this.plugin.bungeeMode != null) {
         this.plugin.join(var2, this.plugin.bungeeMode.getMap(), 10);
      }

   }

   @EventHandler
   public void onPlayerLeave(PlayerQuitEvent var1) {
      Player var2 = var1.getPlayer();
      if (this.plugin.players.contains(var2.getName())) {
         this.plugin.leave(var2);
      }

      ((PlayerData)this.plugin.playerData.get(var2.getName())).saveStatsIntoFile(var2, false);
      this.plugin.playerData.remove(var2.getName());
   }

   @EventHandler
   public void onPlayerRespawn(PlayerRespawnEvent var1) {
      Player var2 = var1.getPlayer();
      PlayerData var3 = (PlayerData)this.plugin.playerData.get(var2.getName());
      if (this.plugin.players.contains(var2.getName()) && var3.getMap() != null) {
         var1.setRespawnLocation(var3.getMap().getSpawnpoint());
      }

   }

   @EventHandler
   public void onPlayerDeath(PlayerDeathEvent var1) {
      final Player var2 = var1.getEntity();
      if (this.plugin.players.contains(var2.getName())) {
         if (!this.plugin.config.respawnScreenOnDeath) {
            var2.setHealth(var2.getMaxHealth());
            if (var2.getVehicle() != null) {
               var2.getVehicle().eject();
            }

            var2.closeInventory();
            var2.setFallDistance(0.0F);
         }

         Player var3 = var2.getKiller() != null && this.plugin.playerData.containsKey(var2.getKiller().getName()) ? var2.getKiller() : null;
         if (!this.plugin.config.DoPlayersDropItemsOnDeath) {
            var1.getDrops().clear();
         }

         var1.setDeathMessage((String)null);
         final PlayerData var4 = (PlayerData)this.plugin.playerData.get(var2.getName());
         var4.addDeaths();
         var4.setKit(var2, (Kit)null);
         var4.killstreak = 0;
         ++var4.deathstreak;
         Iterator var6;
         if (this.plugin.isInTournament(var2)) {
            this.plugin.tournamentsManager.kill(var2);
         } else if (this.plugin.isInChallenge(var2)) {
            var6 = this.plugin.challengeMaps.values().iterator();

            while(var6.hasNext()) {
               ChallengeMap var5 = (ChallengeMap)var6.next();
               if (var5.players.containsKey(var2.getName())) {
                  var5.kill(var2);
                  break;
               }
            }
         }

         Iterator var8;
         if (!var4.damagers.isEmpty()) {
            double var16 = 0.0D;

            double var7;
            for(Iterator var9 = var4.damagers.values().iterator(); var9.hasNext(); var16 += var7) {
               var7 = (Double)var9.next();
            }

            if (this.plugin.config.KillCoinsContribution) {
               var8 = var4.damagers.keySet().iterator();

               while(var8.hasNext()) {
                  String var22 = (String)var8.next();
                  Player var27 = Bukkit.getPlayer(var22);
                  if (var27 != null) {
                     double var10 = Double.valueOf((Double)var4.damagers.get(var22) / var16);
                     double var12 = this.plugin.getModifier(var27);
                     int var14 = (int)((double)this.plugin.config.EarnedCoinsPerKill * var10 * var12);
                     if (var14 != 0) {
                        PlayerData var15 = (PlayerData)this.plugin.playerData.get(var22);
                        var15.addCoins(var27, var14);
                        var27.sendMessage(this.kb + this.plugin.msgs.PlayerKill.replace("%percentage%", String.valueOf((int)(var10 * 100.0D))).replace("%player%", var2.getName()).replace("%coins%", String.valueOf(var14)) + (var12 > 1.0D ? ChatColor.GRAY + " (" + ChatColor.AQUA + "x" + var12 + ChatColor.GRAY + ")!" : ""));
                        if (var15.customScoreboard != null) {
                           var15.customScoreboard.updatePlaceholder(var27, this.plugin, "coins", var15.getCoins(var27));
                        }
                     }
                  }
               }
            }
         }

         var4.damagers.clear();
         if (this.plugin.config.SendDeathMessageToEveryone) {
            var6 = this.plugin.getPlayers(this.plugin.players).iterator();

            while(var6.hasNext()) {
               Player var17 = (Player)var6.next();
               var17.sendMessage(this.getDeathMessage(var2, var3));
            }
         }

         Iterator var23;
         if (this.plugin.config.Deathstreaks.containsKey(var4.deathstreak)) {
            String var18 = this.kb + this.plugin.msgs.DeathstreakBuff.replace("%player%", var2.getName()).replace("%player%", var2.getName()).replace("%deathstreak%", String.valueOf(var4.deathstreak));
            var23 = this.plugin.getPlayers(this.plugin.players).iterator();

            while(var23.hasNext()) {
               Player var19 = (Player)var23.next();
               var19.sendMessage(var18);
            }
         }

         if (var3 != null) {
            if (var3.getName() != var2.getName()) {
               if (this.plugin.config.titles_enabled) {
                  (new TitleObject(this.plugin.msgs.PlayerDeathMessage.replace("%killer%", var3.getName()), TitleType.TITLE)).setFadeIn(this.plugin.config.titles_fadeIn).setStay(this.plugin.config.titles_stay).setFadeOut(this.plugin.config.titles_fadeOut).send(var2);
               } else {
                  var2.sendMessage(this.kb + this.plugin.msgs.PlayerDeathMessage.replace("%killer%", var3.getName()));
               }

               PlayerData var20 = (PlayerData)this.plugin.playerData.get(var3.getName());
               var20.addKills(var3);
               ++var20.killstreak;
               String var21;
               Player var25;
               if (var20.deathstreak >= this.plugin.config.leastDeathstreak) {
                  var23 = this.plugin.config.DeathstreakEndCommands.iterator();

                  while(var23.hasNext()) {
                     var21 = (String)var23.next();
                     Bukkit.dispatchCommand(Bukkit.getConsoleSender(), var21.replace("%player%", var3.getName()));
                  }

                  var21 = this.kb + this.plugin.msgs.DeathstreakEnd.replace("%player%", var3.getName()).replace("%player%", var3.getName());
                  var8 = this.plugin.getPlayers(this.plugin.players).iterator();

                  while(var8.hasNext()) {
                     var25 = (Player)var8.next();
                     var25.sendMessage(var21);
                  }
               }

               var20.deathstreak = 0;
               var3.setLevel(var20.killstreak);
               if (this.plugin.config.Killstreaks.containsKey(var20.killstreak)) {
                  if (this.plugin.config.titles_enabled) {
                     (new ActionbarTitleObject(this.plugin.msgs.PlayerGetKillstreakSelfMessage.replace("%kills%", String.valueOf(var20.killstreak)))).send(var3);
                  }

                  if (this.plugin.config.SendKillstreaksToEveryone) {
                     var21 = this.kb + this.plugin.msgs.PlayerGetKillstreakAnnouncement.replace("%player%", var3.getName()).replace("%kills%", String.valueOf(var20.killstreak));
                     var8 = this.plugin.getPlayers(this.plugin.players).iterator();

                     while(var8.hasNext()) {
                        var25 = (Player)var8.next();
                        var25.sendMessage(var21);
                     }
                  } else {
                     var3.sendMessage(this.kb + this.plugin.msgs.PlayerGetKillstreakSelfMessage.replace("%kills%", String.valueOf(var20.killstreak)));
                  }

                  var23 = ((List)this.plugin.config.Killstreaks.get(var20.killstreak)).iterator();

                  while(var23.hasNext()) {
                     var21 = (String)var23.next();
                     Bukkit.dispatchCommand(Bukkit.getConsoleSender(), var21.replace("%player%", var3.getName()));
                  }

                  var20.addKillstreaksEarned(var3);
               }

               double var24 = this.plugin.getModifier(var3);
               int var26 = (int)((double)(Integer)this.plugin.config.possibleExp.get(this.plugin.random.nextInt(this.plugin.config.possibleExp.size())) * var24);
               if (!this.plugin.config.KillCoinsContribution) {
                  int var28 = (int)((double)this.plugin.config.EarnedCoinsPerKill * var24);
                  var20.addCoins(var3, var28);
                  var3.sendMessage(this.kb + this.plugin.msgs.PlayerKill.replace("%player%", var2.getName()).replace("%coins%", String.valueOf(var28)).replace("%exp%", String.valueOf(var26)) + (var24 > 1.0D ? ChatColor.GRAY + " (" + ChatColor.AQUA + "x" + var24 + ChatColor.GRAY + ")!" : ""));
               }

               boolean var29 = var20.addExp(var3, var26);
               if (this.plugin.config.titles_enabled) {
                  (new TitleObject(this.plugin.msgs.PlayerEarnExp.replace("%exp%", String.valueOf(var26)), TitleType.TITLE)).setFadeIn(this.plugin.config.titles_fadeIn).setStay(this.plugin.config.titles_stay).setFadeOut(this.plugin.config.titles_fadeOut).send(var3);
               }

               if (var20.customScoreboard != null) {
                  var20.customScoreboard.updatePlaceholder(var3, this.plugin, "kills", var20.getKills());
                  var20.customScoreboard.updatePlaceholder(var3, this.plugin, "killstreak", var20.getKillstreak());
                  var20.customScoreboard.updatePlaceholder(var3, this.plugin, "deathstreak", var20.getDeathstreak());
                  var20.customScoreboard.updatePlaceholder(var3, this.plugin, "player_exp", var20.getExp());
                  if (!this.plugin.config.KillCoinsContribution) {
                     var20.customScoreboard.updatePlaceholder(var3, this.plugin, "coins", var20.getCoins(var3));
                  }

                  if (var20.getNextRank() != null) {
                     var20.customScoreboard.updatePlaceholder(var3, this.plugin, "player_next_rank_exp", var20.getNextRank().getRequiredExp());
                     var20.customScoreboard.updatePlaceholder(var3, this.plugin, "player_next_rank_exp_difference", var20.getNextRank().getRequiredExp() - var20.getExp());
                  }

                  if (var29) {
                     var20.customScoreboard.updatePlaceholder(var3, this.plugin, "player_rank", var20.getRank().getName());
                     var20.customScoreboard.updatePlaceholder(var3, this.plugin, "player_next_rank", var20.getNextRank() != null ? var20.getNextRank().getName() : "None");
                  }
               }
            } else {
               var2.sendMessage(this.kb + this.plugin.msgs.PlayerSuicideMessage);
            }
         }

         Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
            public void run() {
               KBListener.this.plugin.giveDefaultItems(var2);
               Iterator var2x;
               if (!KBListener.this.plugin.config.respawnScreenOnDeath) {
                  var2.teleport(var4.getMap().getSpawnpoint());
                  var2.setFireTicks(0);
                  var2.setLevel(0);
                  var2.setExp(0.0F);
                  var2.setFoodLevel(20);
                  var2x = var2.getActivePotionEffects().iterator();

                  while(var2x.hasNext()) {
                     PotionEffect var1 = (PotionEffect)var2x.next();
                     var2.removePotionEffect(var1.getType());
                  }

                  var2.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 3));
                  var2.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 3));
                  if (KBListener.this.plugin.config.OpenKitsMenuOnRespawn) {
                     var4.kitsInventory.open(var2);
                  }
               }

               if (KBListener.this.plugin.isInChallenge(var2)) {
                  var2.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, KBListener.this.plugin.config.ChallengeRespawnProtectionSeconds * 20, 100));
                  var2.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, KBListener.this.plugin.config.ChallengeRespawnProtectionSeconds * 20, 100));
                  var2.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, KBListener.this.plugin.config.ChallengeRespawnProtectionSeconds * 20, 100));
                  var2x = KBListener.this.plugin.challengeMaps.values().iterator();

                  while(var2x.hasNext()) {
                     ChallengeMap var3 = (ChallengeMap)var2x.next();
                     if (var3.players.containsKey(var2.getName())) {
                        var2.teleport(var3.getSpawnpoint());
                        break;
                     }
                  }
               }

               var2.setAllowFlight(false);
               var2.setFlying(false);
               if (var4.customScoreboard != null) {
                  var4.customScoreboard.updatePlaceholder(var2, KBListener.this.plugin, "deaths", var4.getDeaths());
                  var4.customScoreboard.updatePlaceholder(var2, KBListener.this.plugin, "deathstreak", var4.getDeathstreak());
                  var4.customScoreboard.updatePlaceholder(var2, KBListener.this.plugin, "killstreak", var4.getKillstreak());
               }

            }
         }, 2L);
      }

   }

   @EventHandler
   public void onPlayerPickupItem(PlayerPickupItemEvent var1) {
      if (this.plugin.players.contains(var1.getPlayer().getName()) && !this.plugin.config.CanPlayersPickItemsOnGround) {
         var1.setCancelled(true);
      }

   }

   @EventHandler
   public void onPlayerDropItem(final PlayerDropItemEvent var1) {
      if (this.plugin.players.contains(var1.getPlayer().getName()) && !this.plugin.config.CanPlayersDropItemsOnGround) {
         if (var1.getItemDrop().getItemStack().getType().equals(Material.BOWL)) {
            if (this.plugin.config.SoupDropSound) {
               var1.getPlayer().playSound(var1.getPlayer().getLocation(), this.plugin.ITEM_PICKUP, 1.0F, 1.0F);
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
               public void run() {
                  var1.getItemDrop().remove();
               }
            }, 20L);
         } else {
            var1.setCancelled(true);
         }
      }

   }

   @EventHandler
   public void onFoodLevelChange(FoodLevelChangeEvent var1) {
      if (this.plugin.players.contains(var1.getEntity().getName()) && !this.plugin.config.DoPlayersLoseHunger) {
         var1.setCancelled(true);
      }

   }

   @EventHandler
   public void onPlayerInteract(PlayerInteractEvent var1) {
      final Player var2 = var1.getPlayer();
      ItemStack var3 = var2.getItemInHand();
      if (this.plugin.selectionMode.containsKey(var2.getName()) && this.plugin.compareItem(var3, this.plugin.wand_itemstack)) {
         var1.setCancelled(true);
         int var11 = var1.getAction().equals(Action.LEFT_CLICK_BLOCK) ? 0 : (var1.getAction().equals(Action.RIGHT_CLICK_BLOCK) ? 1 : 2);
         if (var11 != 2) {
            Location var14 = var1.getClickedBlock().getLocation();
            Location var18 = ((Location[])this.plugin.selectionMode.get(var2.getName()))[var11 == 1 ? 0 : 1];
            ((Location[])this.plugin.selectionMode.get(var2.getName()))[var11] = var14;
            if (var18 != null && !var18.getWorld().getName().equals(var14.getWorld().getName())) {
               var18 = null;
            }

            var2.sendMessage(this.kb + "You have set the " + ChatColor.LIGHT_PURPLE + "#" + (var11 + 1) + ChatColor.GRAY + " corner at " + this.plugin.getReadableLocationString(var14, false) + (var18 != null ? ChatColor.AQUA + " (" + (new Cuboid(var14, var18)).getSize() + ")" : ""));
         }
      } else {
         int var6;
         final int var17;
         if (var1.getAction().equals(Action.RIGHT_CLICK_BLOCK) && var1.getClickedBlock().getState() instanceof Sign) {
            Sign var4 = (Sign)var1.getClickedBlock().getState();
            if (var4.getLine(0).equals(this.plugin.config.SignsPrefix)) {
               if (var4.getLine(1).equals(this.plugin.config.JoinPrefix)) {
                  if (var2.getItemInHand().getType() != Material.AIR) {
                     var2.sendMessage(this.kb + this.plugin.msgs.JoinDenied);
                     return;
                  }

                  var2.performCommand("kitbattle join " + ChatColor.stripColor(var4.getLine(2)));
               } else if (var4.getLine(1).equals(this.plugin.config.LeavePrefix)) {
                  if (this.plugin.players.contains(var2.getName())) {
                     var2.getInventory().clear();
                     (new BukkitRunnable() {
                        public void run() {
                           var2.performCommand("kitbattle leave");
                           var2.updateInventory();
                        }
                     }).runTaskLater(this.plugin, 2L);
                  } else {
                     var2.sendMessage(this.kb + this.plugin.msgs.NotInAGame);
                  }
               } else {
                  int var12;
                  Inventory var13;
                  PlayerData var16;
                  if (var4.getLine(1).equals(this.plugin.config.SoupPrefix)) {
                     if (this.plugin.players.contains(var2.getName())) {
                        if (!var4.getLine(3).isEmpty() && this.plugin.isNumber(var4.getLine(3))) {
                           var12 = Integer.valueOf(var4.getLine(3));
                           var16 = (PlayerData)this.plugin.playerData.get(var2.getName());
                           var17 = var16.getCoins(var2);
                           if (var17 < var12) {
                              var2.sendMessage(this.kb + this.plugin.msgs.NotEnoughCoins);
                              return;
                           }

                           var16.removeCoins(var2, var12);
                           var2.sendMessage(this.kb + this.plugin.msgs.PlayerStatModificationThroughCommand.replace("%amount%", String.valueOf(var17 - var12)).replace("%stat%", "coins"));
                           if (var16.customScoreboard != null) {
                              var16.customScoreboard.updatePlaceholder(var2, this.plugin, "coins", var16.getCoins(var2));
                           }
                        }

                        var13 = Bukkit.createInventory(var2, 36, "Soup");

                        for(var6 = 0; var6 < 36; ++var6) {
                           var13.addItem(new ItemStack[]{this.soup});
                        }

                        var2.openInventory(var13);
                     } else {
                        var2.sendMessage(this.kb + this.plugin.msgs.NotInAGame);
                     }
                  } else if (var4.getLine(1).equals(this.plugin.config.PotionsPrefix)) {
                     if (this.plugin.players.contains(var2.getName())) {
                        if (!var4.getLine(3).isEmpty() && this.plugin.isNumber(var4.getLine(3))) {
                           var12 = Integer.valueOf(var4.getLine(3));
                           var16 = (PlayerData)this.plugin.playerData.get(var2.getName());
                           var17 = var16.getCoins(var2);
                           if (var17 < var12) {
                              var2.sendMessage(this.kb + this.plugin.msgs.NotEnoughCoins);
                              return;
                           }

                           var16.removeCoins(var2, var12);
                           var2.sendMessage(this.kb + this.plugin.msgs.PlayerStatModificationThroughCommand.replace("%amount%", String.valueOf(var17 - var12)).replace("%stat%", "coins"));
                           if (var16.customScoreboard != null) {
                              var16.customScoreboard.updatePlaceholder(var2, this.plugin, "coins", var16.getCoins(var2));
                           }
                        }

                        var13 = Bukkit.createInventory(var2, 36, "Potions");

                        for(var6 = 0; var6 < 36; ++var6) {
                           var13.addItem(new ItemStack[]{this.potion});
                        }

                        var2.openInventory(var13);
                     } else {
                        var2.sendMessage(this.kb + this.plugin.msgs.NotInAGame);
                     }
                  }
               }

               return;
            }
         }

         if (this.plugin.players.contains(var2.getName()) && (var1.getAction().equals(Action.RIGHT_CLICK_BLOCK) || var1.getAction().equals(Action.RIGHT_CLICK_AIR))) {
            if (var3.getType().equals(this.soup.getType())) {
               if (var2.getHealth() == var2.getMaxHealth()) {
                  return;
               }

               var2.setHealth(var2.getHealth() + 7.0D > var2.getMaxHealth() ? var2.getMaxHealth() : var2.getHealth() + 7.0D);
               var2.getItemInHand().setType(Material.BOWL);
               if (this.plugin.config.SoupAutoDisappear) {
                  (new BukkitRunnable() {
                     public void run() {
                        var2.setItemInHand(KBListener.this.air);
                     }
                  }).runTaskLater(this.plugin, 1L);
               }

               ((PlayerData)this.plugin.playerData.get(var2.getName())).addSoupsEaten(var2);
               return;
            }

            if (((PlayerData)this.plugin.playerData.get(var2.getName())).getKit() == null) {
               if (this.plugin.compareItem(var3, this.plugin.KitSelector)) {
                  ((PlayerData)this.plugin.playerData.get(var2.getName())).kitsInventory.open(var2);
                  return;
               }

               if (this.plugin.compareItem(var3, this.plugin.ProfileItem)) {
                  var2.openInventory(this.plugin.profileInventory);
                  return;
               }

               if (this.plugin.compareItem(var3, this.plugin.ShopOpener)) {
                  this.plugin.shop.open(var2);
                  return;
               }

               if (this.plugin.compareItem(var3, this.plugin.TrailsOpener)) {
                  if (this.plugin.trailsInventory != null && var2.hasPermission("kitbattle.trails")) {
                     var2.openInventory(this.plugin.trailsInventory);
                  } else {
                     var2.sendMessage(this.kb + this.plugin.msgs.nopermission);
                  }

                  return;
               }

               final PlayerData var10;
               if (var3.getType().equals(Material.INK_SACK)) {
                  if (this.plugin.tournamentsManager != null) {
                     var10 = (PlayerData)this.plugin.playerData.get(var2.getName());
                     if (var10.hasCooldown(var2, "Tournament")) {
                        return;
                     }

                     var10.setCooldown(var2, "Tournament", 4, false);
                     if (!this.plugin.tournamentsManager.isQueueing(var2) && !this.plugin.tournamentsManager.contains(var2)) {
                        this.plugin.tournamentsManager.add(var2);
                        var2.setItemInHand((new ItemStackBuilder(Material.INK_SACK)).setDurability(10).setName(ChatColor.AQUA + "Tournament: " + ChatColor.GREEN + "Enabled").build());
                        var2.playSound(var2.getLocation(), this.plugin.CLICK, 1.0F, 1.0F);
                     } else {
                        this.plugin.tournamentsManager.remove(var2, false);
                        var2.setItemInHand((new ItemStackBuilder(Material.INK_SACK)).setDurability(8).setName(ChatColor.AQUA + "Tournament: " + ChatColor.RED + "Disabled").build());
                        var2.playSound(var2.getLocation(), this.plugin.CLICK, 1.0F, 1.0F);
                     }
                  } else {
                     var2.setItemInHand(new ItemStack(Material.AIR));
                  }

                  return;
               }

               if (this.plugin.compareItem(var3, this.plugin.ChallengesItem)) {
                  if (this.plugin.challengesManager != null) {
                     this.plugin.challengesManager.openMenu(var2);
                     var2.playSound(var2.getLocation(), this.plugin.CLICK, 1.0F, 1.0F);
                  } else {
                     var2.setItemInHand(new ItemStack(Material.AIR));
                  }

                  return;
               }

               if (this.plugin.compareItem(var2.getItemInHand(), this.plugin.KitUnlocker)) {
                  var10 = (PlayerData)this.plugin.playerData.get(var2.getName());
                  if (var10.hasCooldown(var2, "Kitunlocker")) {
                     return;
                  }

                  var10.setCooldown(var2, "Kitunlocker", 5, false);
                  final ArrayList var5 = new ArrayList();

                  label239:
                  for(var6 = 0; var6 < this.plugin.shop.getSize(); ++var6) {
                     Iterator var8 = this.plugin.shop.getContents(var6).iterator();

                     while(true) {
                        ItemStack var7;
                        Kit var9;
                        do {
                           do {
                              if (!var8.hasNext()) {
                                 continue label239;
                              }

                              var7 = (ItemStack)var8.next();
                              if (var7 == null) {
                                 continue label239;
                              }

                              var9 = (Kit)this.plugin.Kits.get(ChatColor.stripColor(var7.getItemMeta().getDisplayName().toLowerCase()));
                           } while(!var9.isEnabled());
                        } while(var9.requirePermission && !var2.hasPermission(var9.permission));

                        var5.add(var7);
                     }
                  }

                  if (var5.isEmpty()) {
                     var2.sendMessage(this.kb + this.plugin.msgs.AlreadyUnlockedAllKits);
                     return;
                  }

                  if (var2.getItemInHand().getAmount() > 1) {
                     var2.getItemInHand().setAmount(var2.getItemInHand().getAmount() - 1);
                  } else {
                     var2.setItemInHand(new ItemStack(Material.AIR));
                  }

                  --var10.kitUnlockers;
                  var2.sendMessage(this.kb + this.plugin.msgs.PlayerOpenKitUnlocker);
                  final Inventory var15 = Bukkit.createInventory(var2, 54, (String)this.plugin.msgs.inventories.get("Kit-Unlocker"));
                  var2.openInventory(var15);
                  var2.playSound(var2.getLocation(), this.plugin.ITEM_PICKUP, 1.0F, 1.0F);

                  for(var17 = 0; var17 < 3; ++var17) {
                     var2.playEffect(var2.getLocation().add(0.0D, (double)var17, 0.0D), Effect.ENDER_SIGNAL, 1);
                  }

                  var17 = this.plugin.random.nextInt(22) + 18;
                  (new BukkitRunnable() {
                     int slot = 0;

                     public void run() {
                        if (this.slot > 0) {
                           var15.setItem(this.slot - 1, (ItemStack)null);
                        }

                        var15.setItem(this.slot, (ItemStack)var5.get(KBListener.this.plugin.random.nextInt(var5.size())));
                        if (this.slot != var17) {
                           ++this.slot;
                           var2.playSound(var2.getLocation(), KBListener.this.plugin.CLICK, 1.0F, 1.0F);
                        } else {
                           if (KBListener.this.plugin.playerData.containsKey(var2.getName())) {
                              var2.sendMessage(KBListener.this.kb + KBListener.this.plugin.msgs.PlayerWinKit.replace("%kit%", ChatColor.stripColor(var15.getItem(this.slot).getItemMeta().getDisplayName())));
                              Kit var1 = (Kit)KBListener.this.plugin.Kits.get(ChatColor.stripColor(var15.getItem(this.slot).getItemMeta().getDisplayName().toLowerCase()));
                              if (var10.kitsInventory.getAllContents().contains(var1.getLogo())) {
                                 int var2x = (int)((double)var1.getPrice() * KBListener.this.plugin.config.SellValue);
                                 var10.addCoins(var2, var2x);
                                 if (var10.customScoreboard != null) {
                                    var10.customScoreboard.updatePlaceholder(var2, KBListener.this.plugin, "coins", var10.getCoins(var2));
                                 }

                                 var2.sendMessage(KBListener.this.kb + KBListener.this.plugin.msgs.KitAlreadyUnlocked + ChatColor.GREEN + " (+" + var2x + ")");
                              } else {
                                 Entry var4 = var10.kitsInventory.getEmptySlot();
                                 var10.kitsInventory.setItem((Integer)var4.getKey(), (Integer)var4.getValue(), var1.getLogo());
                              }

                              Iterator var3 = KBListener.this.getSurroundingLocations(var2.getLocation()).iterator();

                              while(var3.hasNext()) {
                                 Location var5x = (Location)var3.next();
                                 KBListener.this.spawnFirework(var5x);
                              }
                           } else {
                              (new BukkitRunnable() {
                                 public void run() {
                                    String var1 = KBListener.this.plugin.config.UUID ? var2.getUniqueId().toString() : var2.getName();
                                    if (KBListener.this.plugin.config.useMySQL) {
                                       try {
                                          Statement var2x = KBListener.this.plugin.mysql.getConnection().createStatement();
                                          ResultSet var3 = var2x.executeQuery("SELECT Kits FROM " + KBListener.this.plugin.config.tableprefix + " WHERE " + (KBListener.this.plugin.config.UUID ? "player_uuid" : "player_name") + " = '" + var1 + "';");
                                          var3.next();
                                          String var4 = var3.getString("Kits");
                                          var4 = var4 + ", " + ChatColor.stripColor(var15.getItem(slot).getItemMeta().getDisplayName());
                                          KBListener.this.plugin.mysql.getConnection().prepareStatement("UPDATE " + KBListener.this.plugin.config.tableprefix + " SET Kits='" + var4 + "' WHERE " + (KBListener.this.plugin.config.UUID ? "player_uuid" : "player_name") + "='" + var1 + "';").executeUpdate();
                                       } catch (SQLException var5x) {
                                          var5x.printStackTrace();
                                       }
                                    } else {
                                       List var6 = KBListener.this.plugin.fileManager.getConfig("players.yml").getStringList("Players." + var1 + ".Kits");
                                       var6.add(ChatColor.stripColor(var15.getItem(slot).getItemMeta().getDisplayName()));
                                       KBListener.this.plugin.fileManager.getConfig("players.yml").set("Players." + var1 + ".Kits", var6);
                                       KBListener.this.plugin.fileManager.saveConfig("players.yml");
                                    }

                                 }
                              }).runTaskAsynchronously(KBListener.this.plugin);
                           }

                           this.cancel();
                        }
                     }
                  }).runTaskTimer(this.plugin, 0L, 3L);
               }
            }
         }

      }
   }

   @EventHandler
   public void onPlayerClickInventory(InventoryClickEvent var1) {
      Player var2 = (Player)var1.getWhoClicked();
      if (this.plugin.players.contains(var2.getName())) {
         Inventory var3 = var1.getInventory();
         String var4 = var1.getView().getTitle();
         ItemStack var5 = var1.getCurrentItem();
         PlayerData var6;
         Kit var19;
         if (var4.contains((CharSequence)this.plugin.msgs.inventories.get("Kits"))) {
            var6 = (PlayerData)this.plugin.playerData.get(var2.getName());
            var1.setCancelled(true);
            if (!var6.kitsInventory.handleClick(var2, var5, var3)) {
               if (var5 != null && !var5.getType().equals(Material.AIR)) {
                  var19 = (Kit)this.plugin.Kits.get(ChatColor.stripColor(var5.getItemMeta().getDisplayName().toLowerCase()));
                  if (var19 != null) {
                     if (!var1.getAction().equals(InventoryAction.PICKUP_HALF)) {
                        if (!var19.isEnabled()) {
                           var2.sendMessage(this.kb + this.plugin.msgs.KitDisabled);
                        } else if (var19.requirePermission && !var2.hasPermission(var19.permission)) {
                           var2.sendMessage(this.kb + this.plugin.msgs.NoPermissionForKit);
                        } else {
                           ((PlayerData)this.plugin.playerData.get(var2.getName())).setKit(var2, var19);
                           var19.giveItems(var2);
                           if (this.plugin.config.titles_enabled) {
                              (new TitleObject(this.plugin.msgs.PlayerSelectKit.replace("%kit%", var19.getName()), TitleType.TITLE)).setFadeIn(this.plugin.config.titles_fadeIn).setStay(this.plugin.config.titles_stay).setFadeOut(this.plugin.config.titles_fadeOut).send(var2);
                           } else {
                              var2.sendMessage(this.kb + this.plugin.msgs.PlayerSelectKit.replace("%kit%", var19.getName()));
                           }

                           var2.closeInventory();
                           Bukkit.getPluginManager().callEvent(new PlayerSelectKitEvent(var2, var19));
                        }
                     } else if (!var6.hasCooldown(var2, "Selling")) {
                        var6.setCooldown(var2, "Selling", 3, false);

                        Kit var22;
                        for(var22 = var19; var22.original != null; var22 = var22.original) {
                        }

                        if (!this.plugin.config.defaultKits.contains(var22.getName().toLowerCase())) {
                           Inventory var23 = Bukkit.createInventory(var2, 9, (String)this.plugin.msgs.inventories.get("Selling"));
                           var23.setItem(2, this.plugin.confirm_itemstack);
                           ItemStack var25 = (new ItemStackBuilder(var5.clone())).addLore(" ", ChatColor.RED + "Sell value: " + ChatColor.YELLOW + (int)((double)var19.getTotalPrice() * this.plugin.config.SellValue) + "$").build();
                           var23.setItem(4, var25);
                           var23.setItem(6, this.plugin.cancel_itemstack);
                           var2.openInventory(var23);
                        }
                     }
                  }
               }
            }
         } else if (var4.equals(this.plugin.msgs.inventories.get("Map-Vote"))) {
            var1.setCancelled(true);
            var6 = (PlayerData)this.plugin.playerData.get(var2.getName());
            if (var5 != null && !var5.getType().equals(Material.AIR) && var5.getType().equals(Material.NAME_TAG) && !var6.hasCooldown(var2, "Vote")) {
               var6.setCooldown(var2, "Vote", 1, false);
               this.plugin.bungeeMode.vote(var2, var5);
            }
         } else if (var4.equals(this.plugin.msgs.inventories.get("Profile-Inventory"))) {
            var1.setCancelled(true);
            if (var5 != null && !var5.getType().equals(Material.AIR)) {
               if (var5.getType().equals(Material.PAPER)) {
                  var2.openInventory(((PlayerData)this.plugin.playerData.get(var2.getName())).getStatsInventory(var2));
               } else if (var5.getType().equals(Material.ENDER_CHEST)) {
                  ((PlayerData)this.plugin.playerData.get(var2.getName())).achievements.open(var2);
               }

            }
         } else if (var4.equals(this.plugin.msgs.inventories.get("Trails-Inventory"))) {
            var1.setCancelled(true);
            if (var1.getRawSlot() <= 53 && var5 != null && !var5.getType().equals(Material.AIR)) {
               if (var5.getType().equals(Material.ARROW)) {
                  ((PlayerData)this.plugin.playerData.get(var2.getName())).selectedTrail = null;
               } else {
                  ((PlayerData)this.plugin.playerData.get(var2.getName())).selectedTrail = Effect.valueOf(ChatColor.stripColor(var5.getItemMeta().getDisplayName()));
               }

               var2.sendMessage(this.kb + this.plugin.msgs.TrailSelect.replace("%trail%", var5.getItemMeta().getDisplayName()));
            }
         } else if (!var4.equals(this.plugin.msgs.inventories.get("Stats-Inventory")) && !var4.contains((CharSequence)this.plugin.msgs.inventories.get("Achievements-Inventory"))) {
            Kit var16;
            if (var4.contains(this.plugin.shop.getName())) {
               var1.setCancelled(true);
               if (var5 != null && !var5.getType().equals(Material.AIR) && !this.plugin.compareItem(var5, this.plugin.pane_itemstack)) {
                  if (!this.plugin.shop.handleClick(var2, var5, var3)) {
                     if (this.plugin.compareItem(var5, this.plugin.upgrades_itemstack)) {
                        ((PlayerData)this.plugin.playerData.get(var2.getName())).openUpgrades(var2);
                     } else if (this.plugin.compareItem(var5, this.plugin.back_itemstack)) {
                        this.plugin.shop.open(var2);
                     } else {
                        var16 = (Kit)this.plugin.Kits.get(ChatColor.stripColor(var5.getItemMeta().getDisplayName().toLowerCase()));
                        if (var16 != null) {
                           if (var1.getAction().equals(InventoryAction.PICKUP_HALF)) {
                              var2.openInventory(var16.kitPreview);
                           } else if (!var16.isEnabled()) {
                              var2.sendMessage(this.kb + this.plugin.msgs.KitDisabled);
                           } else if (var16.requirePermission && !var2.hasPermission(var16.permission)) {
                              var2.sendMessage(this.kb + this.plugin.msgs.NoPermissionForKit);
                           } else {
                              if (((PlayerData)this.plugin.playerData.get(var2.getName())).getCoins(var2) >= var16.getPrice()) {
                                 Inventory var20 = Bukkit.createInventory(var2, 9, "" + ChatColor.DARK_RED + ChatColor.UNDERLINE + "Are you sure?");
                                 var20.setItem(4, var5);
                                 var20.setItem(2, this.plugin.confirm_itemstack);
                                 var20.setItem(6, this.plugin.cancel_itemstack);
                                 var2.openInventory(var20);
                              } else {
                                 var2.sendMessage(this.kb + this.plugin.msgs.NotEnoughCoins);
                              }

                           }
                        }
                     }
                  }
               }
            } else {
               if (var4.equals(this.plugin.msgs.inventories.get("Queue"))) {
                  var1.setCancelled(true);
                  if (this.plugin.challengesManager == null) {
                     var2.closeInventory();
                  }

                  if (var1.getRawSlot() < -1 || var1.getRawSlot() > var1.getInventory().getSize() || var5 == null || var5.getType().equals(Material.AIR)) {
                     return;
                  }

                  var6 = (PlayerData)this.plugin.playerData.get(var2.getName());
                  if (var6.hasCooldown(var2, "Challenge_Queue")) {
                     return;
                  }

                  var6.setCooldown(var2, "Challenge_Queue", 3, false);
                  int var7 = Integer.valueOf(String.valueOf(ChatColor.stripColor(var5.getItemMeta().getDisplayName()).charAt(0)));
                  if (this.plugin.challengesManager.isInQueue(var2, var7)) {
                     this.plugin.challengesManager.remove(var2, var7);
                     var3.setItem(var1.getRawSlot(), (new ItemStackBuilder(Material.INK_SACK)).setDurability(8).setName(ChatColor.AQUA + var7 + "v" + var7).build());
                     var2.playSound(var2.getLocation(), this.plugin.CLICK, 1.0F, 1.0F);
                  } else {
                     this.plugin.challengesManager.add(var2, var7);
                     var3.setItem(var1.getRawSlot(), (new ItemStackBuilder(Material.INK_SACK)).setDurability(10).setName(ChatColor.AQUA + var7 + "v" + var7).build());
                     var2.playSound(var2.getLocation(), this.plugin.CLICK, 1.0F, 1.0F);
                  }
               }

               String var17;
               if (var4.contains((CharSequence)this.plugin.msgs.inventories.get("Kit-Preview"))) {
                  var1.setCancelled(true);
                  if (var5 != null && !var5.getType().equals(Material.AIR) && !this.plugin.compareItem(var5, this.plugin.pane_itemstack) && var1.getRawSlot() >= -1 && var1.getRawSlot() <= var1.getInventory().getSize()) {
                     if (this.plugin.compareItem(var5, this.plugin.back_itemstack)) {
                        var17 = ChatColor.stripColor(var4.split(": ")[1]).toLowerCase();
                        if (this.plugin.Kits.containsKey(var17) && ((Kit)this.plugin.Kits.get(var17)).original != null) {
                           ((PlayerData)this.plugin.playerData.get(var2.getName())).openUpgrades(var2);
                        } else {
                           this.plugin.shop.open(var2);
                        }
                     }

                  }
               } else {
                  int var9;
                  int var10;
                  int var11;
                  if (var4.equals("" + ChatColor.DARK_RED + ChatColor.UNDERLINE + "Are you sure?")) {
                     var1.setCancelled(true);
                     if (var5 != null && !var5.getType().equals(Material.AIR)) {
                        var17 = ChatColor.stripColor(var3.getItem(4).getItemMeta().getDisplayName().toLowerCase());
                        var19 = (Kit)this.plugin.Kits.get(var17);
                        if (var19 != null) {
                           PlayerData var21 = (PlayerData)this.plugin.playerData.get(var2.getName());
                           if (var5.equals(this.plugin.cancel_itemstack)) {
                              if (var19.original != null) {
                                 var21.openUpgrades(var2);
                              } else {
                                 this.plugin.shop.open(var2);
                              }

                           } else {
                              if (var5.equals(this.plugin.confirm_itemstack)) {
                                 if (this.plugin.config.PurchaseableKitsArePermanent) {
                                    if (var21.kitsInventory.getAllContents().contains(var19.getLogo())) {
                                       var2.sendMessage(this.kb + this.plugin.msgs.KitAlreadyUnlocked);
                                       return;
                                    }

                                    var9 = 0;
                                    var10 = 0;
                                    if (var19.original == null) {
                                       Entry var24 = var21.kitsInventory.getEmptySlot();
                                       var10 = (Integer)var24.getKey();
                                       var9 = (Integer)var24.getValue();
                                    } else {
                                       for(var11 = 0; var11 < var21.kitsInventory.getSize(); ++var11) {
                                          int[] var15;
                                          int var14 = (var15 = this.plugin.smartSlots).length;

                                          for(int var13 = 0; var13 < var14; ++var13) {
                                             int var26 = var15[var13];
                                             if (var21.kitsInventory.getItem(var11, var26) != null && var21.kitsInventory.getItem(var11, var26).equals(var19.original.getLogo())) {
                                                var9 = var26;
                                                var10 = var11;
                                                break;
                                             }
                                          }
                                       }
                                    }

                                    var21.kitsInventory.setItem(var10, var9, var19.getLogo());
                                    if (var19.original == null) {
                                       this.plugin.shop.open(var2);
                                    } else {
                                       var2.closeInventory();
                                    }
                                 } else {
                                    var2.closeInventory();
                                    var19.giveItems(var2);
                                    var21.setKit(var2, var19);
                                    var2.sendMessage(this.kb + this.plugin.msgs.PlayerSelectKit.replace("%kit%", var19.getName()));
                                 }

                                 var21.removeCoins(var2, var19.getPrice());
                                 if (var21.customScoreboard != null) {
                                    var21.customScoreboard.updatePlaceholder(var2, this.plugin, "coins", var21.getCoins(var2));
                                 }

                                 var2.sendMessage(this.kb + this.plugin.msgs.PlayerPurchaseKit.replace("%kit%", var19.getName()).replace("%price%", String.valueOf(var19.getPrice())));
                              }

                           }
                        }
                     }
                  } else if (!var4.equals(this.plugin.msgs.inventories.get("Selling"))) {
                     if (var4.equals(this.plugin.msgs.inventories.get("Kit-Unlocker"))) {
                        var1.setCancelled(true);
                     }
                  } else {
                     var1.setCancelled(true);
                     if (var5 != null && !var5.getType().equals(Material.AIR)) {
                        if (this.plugin.compareItem(var5, this.plugin.cancel_itemstack)) {
                           ((PlayerData)this.plugin.playerData.get(var2.getName())).kitsInventory.open(var2);
                        } else if (this.plugin.compareItem(var5, this.plugin.confirm_itemstack)) {
                           var16 = (Kit)this.plugin.Kits.get(ChatColor.stripColor(var3.getItem(4).getItemMeta().getDisplayName().toLowerCase()));
                           if (var16 == null) {
                              var2.closeInventory();
                           } else {
                              PlayerData var18 = (PlayerData)this.plugin.playerData.get(var2.getName());

                              for(int var8 = 0; var8 < var18.kitsInventory.getSize(); ++var8) {
                                 int[] var12;
                                 var11 = (var12 = this.plugin.smartSlots).length;

                                 for(var10 = 0; var10 < var11; ++var10) {
                                    var9 = var12[var10];
                                    if (var18.kitsInventory.getItem(var8, var9) != null && var18.kitsInventory.getItem(var8, var9).getItemMeta().getDisplayName().equals(var16.getLogo().getItemMeta().getDisplayName())) {
                                       var18.kitsInventory.removeItem(var8, var9);
                                       var18.addCoins(var2, (int)((double)var16.getTotalPrice() * this.plugin.config.SellValue));
                                       if (var18.customScoreboard != null) {
                                          var18.customScoreboard.updatePlaceholder(var2, this.plugin, "coins", var18.getCoins(var2));
                                       }

                                       var18.kitsInventory.open(var2);
                                       return;
                                    }

                                    var2.closeInventory();
                                 }
                              }

                           }
                        }
                     }
                  }
               }
            }
         } else {
            var1.setCancelled(true);
            if (var5 != null && !var5.getType().equals(Material.AIR)) {
               if (this.plugin.compareItem(var5, this.plugin.back_itemstack)) {
                  var2.openInventory(this.plugin.profileInventory);
               } else if (var4.contains((CharSequence)this.plugin.msgs.inventories.get("Achievements-Inventory"))) {
                  ((PlayerData)this.plugin.playerData.get(var2.getName())).achievements.handleClick(var2, var5, var3);
               }

            }
         }
      }
   }

   @EventHandler
   public void blockCommand(PlayerCommandPreprocessEvent var1) {
      Player var2 = var1.getPlayer();
      if (!var2.hasPermission("kitbattle.unblockcmd")) {
         if (this.plugin.players.contains(var2.getName()) && !this.plugin.config.allowedCommands.contains(var1.getMessage().split(" ")[0].replace("/", "").toLowerCase())) {
            var1.setCancelled(true);
            var2.sendMessage(this.kb + this.plugin.msgs.CantUseCommands);
         }

      }
   }

   @EventHandler
   public void onPlayerMoveEvent(PlayerMoveEvent var1) {
      Player var2 = var1.getPlayer();
      if (this.plugin.players.contains(var2.getName())) {
         if (var1.getTo().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.SPONGE)) {
            var2.setVelocity(new Vector(0.0D, this.plugin.config.SpongeBoostUpwards, 0.0D));
            var2.setFallDistance((float)(-this.plugin.config.SpongeFallProtection));
            if (this.plugin.config.SpongeLaunchSound) {
               var2.playSound(var2.getLocation(), this.plugin.WITHER_SHOOT, 10.0F, 10.0F);
            }
         }

      }
   }

   @EventHandler
   public void onEntityDamage(EntityDamageEvent var1) {
      if (var1.getEntity().getType() == EntityType.PLAYER) {
         if (var1.getCause().equals(DamageCause.FALL)) {
            if (this.plugin.players.contains(((Player)var1.getEntity()).getName()) && !this.plugin.config.FallDamageEnabled) {
               var1.setCancelled(true);
            }

         }
      }
   }

   @EventHandler
   public void onEntityDamageByEntity(EntityDamageByEntityEvent var1) {
      if (var1.getDamager().getType().equals(EntityType.FIREWORK) && var1.getEntityType().equals(EntityType.PLAYER) && this.plugin.players.contains(((Player)var1.getEntity()).getName())) {
         var1.setCancelled(true);
      }

      if (!var1.isCancelled() && var1.getDamage() >= 1.0D) {
         Entity var2 = var1.getEntity();
         Entity var3 = var1.getDamager();
         if (var2.getType().equals(EntityType.PLAYER)) {
            Player var4 = (Player)var2;
            if (this.plugin.players.contains(var4.getName())) {
               Player var5 = var3.getType().equals(EntityType.PLAYER) ? (Player)var3 : null;
               if (var5 == null && var3 instanceof Projectile) {
                  Projectile var6 = (Projectile)var3;
                  if (var6.getShooter() instanceof Player) {
                     var5 = (Player)var6.getShooter();
                     if (this.plugin.players.contains(var5.getName()) && !var5.getName().equals(var4.getName())) {
                        ((PlayerData)this.plugin.playerData.get(var5.getName())).addProjectileHits(var5);
                     }
                  }
               }

               if (var5 != null && !var5.getName().equals(var4.getName()) && this.plugin.players.contains(var5.getName())) {
                  ((PlayerData)this.plugin.playerData.get(var4.getName())).addDamage(var5, var1.getDamage());
               }
            }
         }

      }
   }

   @EventHandler
   public void onEntityShootBowEvent(ProjectileLaunchEvent var1) {
      final Projectile var2 = var1.getEntity();
      if (var2.getShooter() != null && var2.getShooter() instanceof Player) {
         Player var3 = (Player)var2.getShooter();
         if (this.plugin.players.contains(var3.getName())) {
            PlayerData var4 = (PlayerData)this.plugin.playerData.get(var3.getName());
            if (var4.selectedTrail != null) {
               final World var5 = var3.getWorld();
               final Effect var6 = var4.selectedTrail;
               (new BukkitRunnable() {
                  public void run() {
                     var5.playEffect(var2.getLocation(), var6, 1);

                     for(int var1 = 1; var1 < KBListener.this.plugin.config.TrailsSize; ++var1) {
                        var5.playEffect(var2.getLocation().add(KBListener.this.plugin.random.nextDouble(), KBListener.this.plugin.random.nextDouble(), KBListener.this.plugin.random.nextDouble()), var6, 1);
                     }

                     if (!var2.isValid() || var2.isOnGround() || var2.getLocation().getY() < 0.0D) {
                        this.cancel();
                     }

                  }
               }).runTaskTimer(this.plugin, 0L, (long)this.plugin.config.TrailsInterval);
            }
         }
      }

   }

   @EventHandler
   public void onSignChangeEvent(SignChangeEvent var1) {
      if (var1.getLine(0).equalsIgnoreCase("[kb]")) {
         Player var2 = var1.getPlayer();
         if (!var2.hasPermission("kitbattle.createsigns")) {
            var2.sendMessage(this.kb + this.plugin.msgs.nopermission);
         } else {
            String var3 = var1.getLine(1).toLowerCase();
            if (!var3.equals("join") && !var3.equals("leave") && !var3.equals("soup") && !var3.equals("potions") && !var3.equals("stats") && !var3.equals("top")) {
               var2.sendMessage(this.kb + "Line 2 must be one of those options join/leave/soup/potions/top!");
            } else if (var3.equals("join")) {
               String var4 = var1.getLine(2).toLowerCase();
               if (this.plugin.playingMaps.containsKey(var4)) {
                  final PlayingMap var5 = (PlayingMap)this.plugin.playingMaps.get(var4);
                  FileConfiguration var6 = this.plugin.fileManager.getConfig("maps.yml");

                  int var7;
                  for(var7 = var6.getConfigurationSection("Maps." + var5.name + ".Signs") != null && !var6.getConfigurationSection("Maps." + var5.name + ".Signs").getKeys(false).isEmpty() ? var6.getConfigurationSection("Maps." + var5.name + ".Signs").getKeys(false).size() + 1 : 1; var6.contains("Maps." + var5.name + ".Signs." + var7); ++var7) {
                  }

                  var6.set("Maps." + var5.name + ".Signs." + var7, this.plugin.getStringFromLocation(var1.getBlock().getLocation(), false));
                  this.plugin.fileManager.saveConfig("maps.yml");
                  var5.signs.put(var1.getBlock().getLocation(), var7);
                  var1.setLine(0, this.plugin.config.SignsPrefix);
                  var1.setLine(1, this.plugin.config.JoinPrefix);
                  var1.setLine(2, this.plugin.config.JoinLine3Color + var5.name);
                  (new BukkitRunnable() {
                     public void run() {
                        var5.updateSignPlayers();
                     }
                  }).runTaskLater(this.plugin, 5L);
                  var2.sendMessage(this.kb + "You have created a new join sign for the map " + var5.name + " with the id of #" + ChatColor.AQUA + var7 + "!");
               } else {
                  var2.sendMessage(this.kb + "Line 3 should be a joinable map!");
               }
            } else if (var3.equals("leave")) {
               var1.setLine(0, this.plugin.config.SignsPrefix);
               var1.setLine(1, this.plugin.config.LeavePrefix);
               var2.sendMessage(this.kb + "You have created a leave sign!");
            } else if (var3.equals("soup")) {
               var1.setLine(0, this.plugin.config.SignsPrefix);
               var1.setLine(1, this.plugin.config.SoupPrefix);
               var2.sendMessage(this.kb + "You have created a soup sign!");
            } else if (var3.equals("potions")) {
               var1.setLine(0, this.plugin.config.SignsPrefix);
               var1.setLine(1, this.plugin.config.PotionsPrefix);
               var2.sendMessage(this.kb + "You have created a potions sign!");
            } else if (var3.equals("top")) {
               if (this.plugin.isNumber(var1.getLine(2))) {
                  int var10 = Integer.valueOf(var1.getLine(2));
                  if (var10 < 1) {
                     var2.sendMessage(this.kb + "Line 3 must be a number higher than 0!");
                     return;
                  }

                  FileConfiguration var11 = this.plugin.fileManager.getConfig("signs.yml");
                  int var12 = 1;
                  if (var11.getConfigurationSection("Top") != null && !var11.getConfigurationSection("Top").getKeys(false).isEmpty()) {
                     var12 = var11.getConfigurationSection("Top").getKeys(false).size() + 1;
                     if (var11.contains("Top." + var12)) {
                        var12 = 1;
                     }

                     while(var11.contains("Top." + var12)) {
                        ++var12;
                     }
                  }

                  var11.set("Top." + var12 + ".world", var1.getBlock().getWorld().getName());
                  var11.set("Top." + var12 + ".x", var1.getBlock().getLocation().getBlockX());
                  var11.set("Top." + var12 + ".y", var1.getBlock().getLocation().getBlockY());
                  var11.set("Top." + var12 + ".z", var1.getBlock().getLocation().getBlockZ());
                  this.plugin.fileManager.saveConfig("signs.yml");
                  this.plugin.topSigns.put(var1.getBlock().getLocation(), var12);
                  var1.setLine(0, this.plugin.config.SignsPrefix);
                  var1.setLine(1, "Top #" + var10);
                  var1.setLine(2, "Waiting...");
                  var1.setLine(3, "(0)");
                  boolean var13 = this.plugin.Updater != null;
                  this.plugin.fixUpdater();
                  var2.sendMessage(this.kb + "You have created a top sign!");
                  long var8 = this.plugin.nextUpdate == 0L ? 0L : (this.plugin.nextUpdate - System.currentTimeMillis()) / 1000L;
                  var2.sendMessage(this.plugin.kb + (var13 ? "Signs updater was already running! next update " + ChatColor.GRAY + "(" + ChatColor.LIGHT_PURPLE + var8 + ChatColor.GRAY + "s)" : "Top signs updater has been enabled! to check the next update use /kb info"));
               } else {
                  var2.sendMessage(this.kb + "Line 3 must be a number!");
               }
            }

         }
      }
   }

   @EventHandler
   public void onPlayerChatEvent(AsyncPlayerChatEvent var1) {
      Player var2 = var1.getPlayer();
      if (this.plugin.players.contains(var2.getName()) && this.plugin.config.ShowRankInChat) {
         if (var1.getMessage().contains("%")) {
            var2.sendMessage(this.kb + this.plugin.msgs.CantUseCharacter);
            var1.setCancelled(true);
            return;
         }

         Rank var3 = ((PlayerData)this.plugin.playerData.get(var2.getName())).getRank();
         var1.setFormat(var3.getPrefix().replace("%rank%", var3.getName()) + var1.getFormat());
      }

   }

   @EventHandler
   public void onCommand(PlayerCommandPreprocessEvent var1) {
      String var2 = var1.getMessage().replace("/", "").split(" ")[0].toLowerCase();
      Iterator var4 = this.plugin.config.aliases.iterator();

      while(var4.hasNext()) {
         String var3 = (String)var4.next();
         if (var3.equals(var2)) {
            var1.setMessage(var1.getMessage().replace(var1.getMessage().split(" ")[0], "/kitbattle"));
            return;
         }
      }

   }

   public ArrayList getSurroundingLocations(Location var1) {
      ArrayList var2 = new ArrayList();
      var2.add(var1.clone().add(-1.0D, 0.0D, 0.0D));
      var2.add(var1.clone().add(0.0D, 0.0D, 1.0D));
      var2.add(var1.clone().add(0.0D, 0.0D, -1.0D));
      var2.add(var1.clone().add(1.0D, 0.0D, 0.0D));
      return var2;
   }

   public void spawnFirework(Location var1) {
      Firework var2 = (Firework)var1.getWorld().spawn(var1, Firework.class);
      FireworkMeta var3 = var2.getFireworkMeta();
      Type var4 = Type.BALL;
      FireworkEffect var5 = FireworkEffect.builder().flicker(this.plugin.random.nextBoolean()).withColor(Color.fromBGR(this.plugin.random.nextInt(256), this.plugin.random.nextInt(256), this.plugin.random.nextInt(256))).withFade(Color.fromBGR(this.plugin.random.nextInt(256), this.plugin.random.nextInt(256), this.plugin.random.nextInt(256))).with(var4).trail(this.plugin.random.nextBoolean()).build();
      var3.addEffect(var5);
      var3.setPower(0);
      var2.setFireworkMeta(var3);
   }

   public String getDeathMessage(Player var1, Player var2) {
      if (var2 == null) {
         if (var1.getLastDamageCause() == null) {
            return this.kb + this.plugin.msgs.unknownMessage.replaceAll("%player%", var1.getName());
         } else {
            DamageCause var4 = var1.getLastDamageCause().getCause();
            if (var4.equals(DamageCause.FALL)) {
               return this.kb + this.plugin.msgs.fallMessage.replaceAll("%player%", var1.getName());
            } else if (var4.equals(DamageCause.LAVA)) {
               return this.kb + this.plugin.msgs.lavaMessage.replaceAll("%player%", var1.getName());
            } else if (!var4.equals(DamageCause.FIRE) && !var4.equals(DamageCause.FIRE_TICK)) {
               return !var4.equals(DamageCause.ENTITY_EXPLOSION) && !var4.equals(DamageCause.BLOCK_EXPLOSION) ? this.kb + this.plugin.msgs.unknownMessage.replaceAll("%player%", var1.getName()) : this.kb + this.plugin.msgs.explodeMessage.replaceAll("%player%", var1.getName());
            } else {
               return this.kb + this.plugin.msgs.fireMessage.replaceAll("%player%", var1.getName());
            }
         }
      } else {
         String var3 = ((PlayerData)this.plugin.playerData.get(var2.getName())).getKit() != null ? ((PlayerData)this.plugin.playerData.get(var2.getName())).getKit().name : "No Kit";
         return this.kb + ((String)this.plugin.msgs.killMessages.get(this.plugin.random.nextInt(this.plugin.msgs.killMessages.size()))).replaceAll("%player%", var1.getName()).replaceAll("%killer%", var1.getKiller().getName()).replace("%killerkit%", var3);
      }
   }
}
