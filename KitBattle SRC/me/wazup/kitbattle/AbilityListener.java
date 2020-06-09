package me.wazup.kitbattle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class AbilityListener implements Listener {
   private Kitbattle plugin;
   ArrayList toRemove = new ArrayList();
   ArrayList toRollback = new ArrayList();
   String kb;
   Random random = new Random();

   public AbilityListener(Kitbattle var1, String var2) {
      this.plugin = var1;
      this.kb = var2;
   }

   @EventHandler
   public void AbilityInteractEvent(PlayerInteractEvent var1) {
      final Player var2 = var1.getPlayer();
      final PlayerData var3 = (PlayerData)this.plugin.playerData.get(var2.getName());
      if (this.hasKit(var3) && !var3.getKit().getAbilities().isEmpty()) {
         Material var4 = var2.getItemInHand().getType();
         if (var4.equals(Material.FIREWORK)) {
            if (this.hasAbility(var3, Ability.KANGAROO)) {
               if (var3.getMap().isInSpawn(var2)) {
                  var2.sendMessage(this.plugin.kb + this.plugin.msgs.AbilityUseDeny);
                  var1.setCancelled(true);
                  return;
               }

               var1.setCancelled(true);
               if (var2.isOnGround()) {
                  if (var3.hasCooldown(var2, "Kangaroo")) {
                     return;
                  }

                  var3.setCooldown(var2, "Kangaroo", this.plugin.config.KangarooCooldown, true);
                  Vector var25 = var2.getEyeLocation().getDirection();
                  if (var2.isSneaking()) {
                     var25.setY(0.2D);
                     var25.multiply(4);
                  } else {
                     var25.setY(1.2D);
                  }

                  PlayerUseAbilityEvent var39 = new PlayerUseAbilityEvent(var2, Ability.KANGAROO);
                  Bukkit.getPluginManager().callEvent(var39);
                  var2.setVelocity(var25);
               }
            }

         } else if (var4.equals(Material.DISPENSER)) {
            if (this.hasAbility(var3, Ability.PRISONER)) {
               if (var3.getMap().isInSpawn(var2)) {
                  var2.sendMessage(this.plugin.kb + this.plugin.msgs.AbilityUseDeny);
                  var1.setCancelled(true);
                  return;
               }

               if (var3.hasCooldown(var2, "Prisoner")) {
                  return;
               }

               var3.setCooldown(var2, "Prisoner", this.plugin.config.PrisonerCooldown, true);
               this.sendUseAbility(var2, var3);
               ((Snowball)var2.launchProjectile(Snowball.class)).setMetadata("prison", new FixedMetadataValue(this.plugin, true));
            }

         } else {
            Entity var7;
            Iterator var8;
            Player var9;
            if (var4.equals(Material.REDSTONE)) {
               if (this.hasAbility(var3, Ability.DRACULA)) {
                  if (var3.getMap().isInSpawn(var2)) {
                     var2.sendMessage(this.plugin.kb + this.plugin.msgs.AbilityUseDeny);
                     var1.setCancelled(true);
                  } else if (!var3.hasCooldown(var2, "Dracula")) {
                     var3.setCooldown(var2, "Dracula", this.plugin.config.DraculaCooldown, true);
                     this.sendUseAbility(var2, var3);
                     final HashMap var22 = new HashMap();
                     final HashMap var36 = new HashMap();
                     var8 = var2.getNearbyEntities(20.0D, 20.0D, 20.0D).iterator();

                     while(true) {
                        PlayerData var35;
                        do {
                           do {
                              do {
                                 do {
                                    do {
                                       if (!var8.hasNext()) {
                                          if (!var22.isEmpty()) {
                                             (new BukkitRunnable() {
                                                public void run() {
                                                   Iterator var2x;
                                                   if (AbilityListener.this.hasKit(var3) && AbilityListener.this.hasAbility(var3, Ability.DRACULA)) {
                                                      var2x = var2.getActivePotionEffects().iterator();

                                                      while(var2x.hasNext()) {
                                                         PotionEffect var1 = (PotionEffect)var2x.next();
                                                         var2.removePotionEffect(var1.getType());
                                                      }
                                                   }

                                                   var2x = var22.keySet().iterator();

                                                   while(true) {
                                                      Player var3x;
                                                      PlayerData var4;
                                                      String var7;
                                                      do {
                                                         do {
                                                            do {
                                                               if (!var2x.hasNext()) {
                                                                  return;
                                                               }

                                                               var7 = (String)var2x.next();
                                                               var3x = Bukkit.getPlayer(var7);
                                                               var4 = (PlayerData)AbilityListener.this.plugin.playerData.get(var3x.getName());
                                                            } while(var3x == null);
                                                         } while(!AbilityListener.this.hasKit(var4));
                                                      } while(!var4.getKit().getName().equals(var36.get(var3x.getName())));

                                                      Iterator var6 = ((Collection)var22.get(var7)).iterator();

                                                      while(var6.hasNext()) {
                                                         PotionEffect var5 = (PotionEffect)var6.next();
                                                         var3x.addPotionEffect(var5);
                                                      }
                                                   }
                                                }
                                             }).runTaskLater(this.plugin, (long)(this.plugin.config.DraculaLastsFor * 20));
                                          }

                                          return;
                                       }

                                       var7 = (Entity)var8.next();
                                    } while(!var7.getType().equals(EntityType.PLAYER));

                                    var9 = (Player)var7;
                                    var35 = (PlayerData)this.plugin.playerData.get(var9.getName());
                                 } while(!this.hasKit(var35));
                              } while(var3.getMap().isInSpawn(var9));
                           } while(var9.getActivePotionEffects().isEmpty());
                        } while(this.hasAbility(var35, Ability.DRACULA));

                        var22.put(var9.getName(), var9.getActivePotionEffects());
                        var36.put(var9.getName(), var35.getKit().getName());
                        Iterator var12 = var9.getActivePotionEffects().iterator();

                        while(var12.hasNext()) {
                           PotionEffect var11 = (PotionEffect)var12.next();
                           var9.removePotionEffect(var11.getType());
                           var9.sendMessage(this.kb + this.plugin.msgs.DraculaSuckWarning.replace("%seconds%", String.valueOf(this.plugin.config.DraculaLastsFor)));
                           var2.addPotionEffect(var11);
                           var2.sendMessage(this.kb + this.plugin.msgs.DraculaReceiveEffect.replace("%effect%", var11.getType().getName()));
                        }
                     }
                  }
               }
            } else if (var4.equals(Material.WEB)) {
               if (this.hasAbility(var3, Ability.SPIDERMAN)) {
                  if (var3.getMap().isInSpawn(var2)) {
                     var2.sendMessage(this.plugin.kb + this.plugin.msgs.AbilityUseDeny);
                     var1.setCancelled(true);
                     return;
                  }

                  if (var3.hasCooldown(var2, "Spiderman")) {
                     return;
                  }

                  var3.setCooldown(var2, "Spiderman", this.plugin.config.SpidermanCooldown, true);
                  this.sendUseAbility(var2, var3);
                  ((Snowball)var2.launchProjectile(Snowball.class)).setMetadata("spiderman", new FixedMetadataValue(this.plugin, true));
               }

            } else {
               if (var1.getAction().equals(Action.RIGHT_CLICK_AIR) || var1.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                  PlayerUseAbilityEvent var5;
                  final ArrayList var18;
                  if (var4.equals(Material.BONE)) {
                     if (this.hasAbility(var3, Ability.HADES)) {
                        if (var3.getMap().isInSpawn(var2)) {
                           var2.sendMessage(this.plugin.kb + this.plugin.msgs.AbilityUseDeny);
                           var1.setCancelled(true);
                           return;
                        }

                        if (var3.hasCooldown(var2, "Hades")) {
                           return;
                        }

                        var3.setCooldown(var2, "Hades", this.plugin.config.HadesCooldown, true);
                        var5 = new PlayerUseAbilityEvent(var2, Ability.HADES);
                        Bukkit.getPluginManager().callEvent(var5);
                        this.sendUseAbility(var2, var3);
                        var18 = new ArrayList();

                        for(int var37 = 0; var37 < this.plugin.config.HadesAmountOfDogs; ++var37) {
                           Wolf var38 = (Wolf)var2.getWorld().spawnEntity(var2.getLocation(), EntityType.WOLF);
                           var38.setCustomName(var2.getName() + "'s Wolf");
                           var38.setOwner(var2);
                           var38.setMaxHealth(20.0D);
                           var38.setHealth(20.0D);
                           var38.setMetadata("toRemove", new FixedMetadataValue(this.plugin, true));
                           var38.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999, 2));
                           var18.add(var38);
                        }

                        this.toRemove.addAll(var18);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
                           public void run() {
                              Iterator var2 = var18.iterator();

                              while(var2.hasNext()) {
                                 Wolf var1 = (Wolf)var2.next();
                                 var1.remove();
                              }

                              AbilityListener.this.toRemove.removeAll(var18);
                           }
                        }, (long)this.plugin.config.HadesDogsLastFor);
                     }

                     return;
                  }

                  Entity var15;
                  Iterator var17;
                  if (var4.equals(Material.WOOD_AXE)) {
                     if (this.hasAbility(var3, Ability.THOR)) {
                        if (var3.getMap().isInSpawn(var2)) {
                           var2.sendMessage(this.plugin.kb + this.plugin.msgs.AbilityUseDeny);
                           var1.setCancelled(true);
                           return;
                        }

                        if (var3.hasCooldown(var2, "Thor")) {
                           return;
                        }

                        var3.setCooldown(var2, "Thor", this.plugin.config.ThorCooldown, true);
                        var5 = new PlayerUseAbilityEvent(var2, Ability.THOR);
                        Bukkit.getPluginManager().callEvent(var5);
                        this.sendUseAbility(var2, var3);
                        var17 = var2.getNearbyEntities((double)this.plugin.config.ThorStrikeRadius, (double)this.plugin.config.ThorStrikeRadius, (double)this.plugin.config.ThorStrikeRadius).iterator();

                        while(true) {
                           while(true) {
                              do {
                                 if (!var17.hasNext()) {
                                    return;
                                 }

                                 var15 = (Entity)var17.next();
                              } while(!(var15 instanceof Damageable));

                              if (var15 instanceof Player && (!this.hasKit((PlayerData)this.plugin.playerData.get(((Player)var15).getName())) || var3.getMap().isInSpawn((Player)var15))) {
                                 var2.sendMessage(this.kb + this.plugin.msgs.UseAbilityDeny);
                              } else {
                                 var2.getWorld().strikeLightningEffect(var15.getLocation());
                                 ((Damageable)var15).damage((double)this.plugin.config.ThorLightningDamage, var2);
                              }
                           }
                        }
                     }

                     return;
                  }

                  Location var14;
                  Player var27;
                  if (var4.equals(Material.SULPHUR)) {
                     if (this.hasAbility(var3, Ability.CENTAUR)) {
                        if (var3.getMap().isInSpawn(var2)) {
                           var2.sendMessage(this.plugin.kb + this.plugin.msgs.AbilityUseDeny);
                           var1.setCancelled(true);
                           return;
                        }

                        if (var3.hasCooldown(var2, "Centaur")) {
                           return;
                        }

                        var3.setCooldown(var2, "Centaur", this.plugin.config.CentaurCooldown, true);
                        var5 = new PlayerUseAbilityEvent(var2, Ability.CENTAUR);
                        Bukkit.getPluginManager().callEvent(var5);
                        this.sendUseAbility(var2, var3);
                        var2.getWorld().createExplosion(var2.getLocation(), 0.0F, false);
                        var2.playSound(var2.getLocation(), this.plugin.BLAZE_DEATH, 1.0F, 1.0F);
                        var2.damage((double)this.plugin.config.CentaurDamage * 0.5D);
                        var17 = this.plugin.listen.getSurroundingLocations(var2.getLocation()).iterator();

                        while(var17.hasNext()) {
                           var14 = (Location)var17.next();
                           var2.getWorld().createExplosion(var14, 0.0F, false);
                        }

                        var17 = var2.getNearbyEntities((double)this.plugin.config.CentaurDamageRadius, (double)this.plugin.config.CentaurDamageRadius, (double)this.plugin.config.CentaurDamageRadius).iterator();

                        while(true) {
                           while(true) {
                              do {
                                 if (!var17.hasNext()) {
                                    return;
                                 }

                                 var15 = (Entity)var17.next();
                              } while(!(var15 instanceof Damageable));

                              if (var15.getType().equals(EntityType.PLAYER) && (!this.hasKit((PlayerData)this.plugin.playerData.get(((Player)var15).getName())) || var3.getMap().isInSpawn((Player)var15))) {
                                 var2.sendMessage(this.kb + this.plugin.msgs.UseAbilityDeny);
                              } else {
                                 ((Damageable)var15).damage((double)this.plugin.config.CentaurDamage, var2);
                                 if (var15.getType().equals(EntityType.PLAYER)) {
                                    var27 = (Player)var15;
                                    var27.addPotionEffect(this.plugin.config.regenEffect);
                                    var27.sendMessage(this.plugin.kb + this.plugin.msgs.CentaurStrike.replace("%player%", var2.getName()));
                                    var27.playSound(var2.getLocation(), this.plugin.BLAZE_DEATH, 1.0F, 1.0F);
                                 }
                              }
                           }
                        }
                     }

                     return;
                  }

                  if (var4.equals(Material.CHEST)) {
                     if (this.hasAbility(var3, Ability.SOUPER)) {
                        if (var3.getMap().isInSpawn(var2)) {
                           var2.sendMessage(this.plugin.kb + this.plugin.msgs.AbilityUseDeny);
                           var1.setCancelled(true);
                           return;
                        }

                        if (var3.hasCooldown(var2, "Souper")) {
                           return;
                        }

                        var3.setCooldown(var2, "Souper", this.plugin.config.SouperCooldown, true);
                        var5 = new PlayerUseAbilityEvent(var2, Ability.SOUPER);
                        Bukkit.getPluginManager().callEvent(var5);
                        this.sendUseAbility(var2, var3);
                        final PlayerInventory var31 = var2.getInventory();
                        ArrayList var34 = new ArrayList();

                        int var29;
                        for(var29 = 0; var29 < 9; ++var29) {
                           if (var31.getItem(var29) == null || var31.getItem(var29).getType().equals(Material.BOWL)) {
                              var34.add(var29);
                           }
                        }

                        if (var34.size() > 0) {
                           var29 = 0;

                           int var30;
                           for(var30 = 9; var30 < 36; ++var30) {
                              if (var31.getItem(var30) != null && var31.getItem(var30).getType().equals(this.plugin.listen.soup.getType())) {
                                 var31.getItem(var30).setType(Material.BOWL);
                                 ++var29;
                                 if (var29 == var34.size()) {
                                    break;
                                 }
                              }
                           }

                           var30 = var34.size() - var29;

                           for(int var32 = 0; var32 < var30; ++var32) {
                              var34.remove(var34.size() - 1);
                           }

                           (new BukkitRunnable(var34) {
                              Iterator iterator;

                              {
                                 this.iterator = var2x.iterator();
                              }

                              public void run() {
                                 if (this.iterator.hasNext() && AbilityListener.this.hasKit(var3)) {
                                    var31.setItem((Integer)this.iterator.next(), AbilityListener.this.plugin.listen.soup);
                                    if (var2 != null) {
                                       var2.playSound(var2.getLocation(), AbilityListener.this.plugin.CLICK, 1.0F, 1.0F);
                                    }

                                 } else {
                                    this.cancel();
                                 }
                              }
                           }).runTaskTimer(this.plugin, 0L, 4L);
                        }
                     }

                     return;
                  }

                  if (var4.equals(Material.FIREBALL)) {
                     if (this.hasAbility(var3, Ability.DRAGON)) {
                        var1.setCancelled(true);
                        if (var3.getMap().isInSpawn(var2)) {
                           var2.sendMessage(this.plugin.kb + this.plugin.msgs.AbilityUseDeny);
                           var1.setCancelled(true);
                           return;
                        }

                        if (var3.hasCooldown(var2, "Dragon")) {
                           return;
                        }

                        var3.setCooldown(var2, "Dragon", this.plugin.config.DragonCooldown, true);
                        var5 = new PlayerUseAbilityEvent(var2, Ability.DRAGON);
                        Bukkit.getPluginManager().callEvent(var5);
                        this.sendUseAbility(var2, var3);
                        (new BukkitRunnable() {
                           int bursts = 0;
                           int r;

                           {
                              this.r = AbilityListener.this.plugin.config.DragonFireRange;
                           }

                           public void run() {
                              Location[] var1 = new Location[]{var2.getLocation(), var2.getLocation().clone().add(0.0D, (double)this.r, 0.0D), var2.getLocation().clone().add(0.0D, (double)(-this.r), 0.0D), var2.getLocation().clone().add((double)this.r, 0.0D, 0.0D), var2.getLocation().clone().add(0.0D, 0.0D, (double)this.r), var2.getLocation().clone().add((double)this.r, 0.0D, (double)this.r), var2.getLocation().clone().add((double)(-this.r), 0.0D, (double)(-this.r)), var2.getLocation().clone().add((double)this.r, 0.0D, (double)(-this.r)), var2.getLocation().clone().add((double)(-this.r), 0.0D, (double)this.r)};
                              Location[] var5 = var1;
                              int var4 = var1.length;

                              for(int var3x = 0; var3x < var4; ++var3x) {
                                 Location var2x = var5[var3x];
                                 var2.getWorld().playEffect(var2x, Effect.MOBSPAWNER_FLAMES, 1);
                              }

                              Iterator var7 = var2.getNearbyEntities((double)this.r, (double)this.r, (double)this.r).iterator();

                              while(true) {
                                 while(true) {
                                    Entity var6;
                                    do {
                                       do {
                                          if (!var7.hasNext()) {
                                             ++this.bursts;
                                             if (this.bursts >= AbilityListener.this.plugin.config.DragonAmountOfBursts) {
                                                this.cancel();
                                             }

                                             return;
                                          }

                                          var6 = (Entity)var7.next();
                                       } while(!(var6 instanceof Damageable));
                                    } while(var6.equals(var2));

                                    if (var6.getType().equals(EntityType.PLAYER) && (!AbilityListener.this.hasKit((PlayerData)AbilityListener.this.plugin.playerData.get(((Player)var6).getName())) || var3.getMap().isInSpawn((Player)var6))) {
                                       var2.sendMessage(AbilityListener.this.kb + AbilityListener.this.plugin.msgs.UseAbilityDeny);
                                    } else {
                                       ((Damageable)var6).damage((double)AbilityListener.this.plugin.config.DragonDamageDealt, var2);
                                       var6.setFireTicks(AbilityListener.this.plugin.config.DragonFireLastsFor);
                                    }
                                 }
                              }
                           }
                        }).runTaskTimer(this.plugin, 0L, 20L);
                     }

                     return;
                  }

                  if (var4.equals(Material.REDSTONE_TORCH_ON)) {
                     if (this.hasAbility(var3, Ability.SUICIDAL)) {
                        if (var3.getMap().isInSpawn(var2)) {
                           var2.sendMessage(this.plugin.kb + this.plugin.msgs.AbilityUseDeny);
                           var1.setCancelled(true);
                           return;
                        }

                        if (var3.hasCooldown(var2, "Suicidal")) {
                           return;
                        }

                        var3.setCooldown(var2, "Suicidal", this.plugin.config.SuicidalCooldown, true);
                        var5 = new PlayerUseAbilityEvent(var2, Ability.SUICIDAL);
                        Bukkit.getPluginManager().callEvent(var5);
                        this.sendUseAbility(var2, var3);
                        var17 = var2.getNearbyEntities(5.0D, 5.0D, 5.0D).iterator();

                        while(var17.hasNext()) {
                           var15 = (Entity)var17.next();
                           if (!(var15 instanceof Damageable)) {
                              return;
                           }

                           ((Damageable)var15).damage(1.0D, var2);
                        }

                        for(int var26 = 0; var26 < 2; ++var26) {
                           TNTPrimed var33 = (TNTPrimed)var2.getWorld().spawn(var2.getLocation().add(0.0D, 1.0D, 0.0D), TNTPrimed.class);
                           var33.setFuseTicks(1);
                           var33.setMetadata("tnts", new FixedMetadataValue(this.plugin, true));
                        }
                     }

                     return;
                  }

                  if (var4.equals(Material.BOOK)) {
                     if (this.hasAbility(var3, Ability.PHANTOM)) {
                        if (var3.getMap().isInSpawn(var2)) {
                           var2.sendMessage(this.plugin.kb + this.plugin.msgs.AbilityUseDeny);
                           var1.setCancelled(true);
                           return;
                        }

                        if (var3.hasCooldown(var2, "Phantom")) {
                           return;
                        }

                        var3.setCooldown(var2, "Phantom", this.plugin.config.PhantomCooldown, true);
                        var5 = new PlayerUseAbilityEvent(var2, Ability.PHANTOM);
                        Bukkit.getPluginManager().callEvent(var5);
                        var17 = var2.getNearbyEntities(50.0D, 50.0D, 50.0D).iterator();

                        while(var17.hasNext()) {
                           var15 = (Entity)var17.next();
                           if (var15.getType().equals(EntityType.PLAYER)) {
                              ((Player)var15).playSound(var15.getLocation(), this.plugin.WITHER_SPAWN, 1.0F, 1.0F);
                              ((Player)var15).sendMessage(this.kb + this.plugin.msgs.PhantomNotHacking.replace("%player%", var2.getName()));
                           }
                        }

                        var2.playSound(var2.getLocation(), this.plugin.WITHER_SPAWN, 1.0F, 1.0F);
                        var2.setAllowFlight(true);
                        this.sendUseAbility(var2, var3);
                        (new BukkitRunnable() {
                           int Seconds;

                           {
                              this.Seconds = AbilityListener.this.plugin.config.PhantomFlightLastsFor;
                           }

                           public void run() {
                              if (var2.isOnline() && AbilityListener.this.hasKit(var3)) {
                                 if (this.Seconds == 0) {
                                    var2.setAllowFlight(false);
                                    this.cancel();
                                 } else {
                                    var2.sendMessage(AbilityListener.this.kb + AbilityListener.this.plugin.msgs.PhantomFlyTimeLeft.replace("%seconds%", String.valueOf(this.Seconds)));
                                    --this.Seconds;
                                 }
                              } else {
                                 this.cancel();
                              }
                           }
                        }).runTaskTimer(this.plugin, 0L, 20L);
                     }

                     return;
                  }

                  if (var4.equals(Material.WATCH)) {
                     if (this.hasAbility(var3, Ability.TIMELORD)) {
                        if (var3.getMap().isInSpawn(var2)) {
                           var2.sendMessage(this.plugin.kb + this.plugin.msgs.AbilityUseDeny);
                           var1.setCancelled(true);
                           return;
                        }

                        if (var3.hasCooldown(var2, "Timelord")) {
                           return;
                        }

                        var3.setCooldown(var2, "Timelord", this.plugin.config.TimelordCooldown, true);
                        var5 = new PlayerUseAbilityEvent(var2, Ability.TIMELORD);
                        Bukkit.getPluginManager().callEvent(var5);
                        var2.playSound(var2.getLocation(), this.plugin.WITHER_SHOOT, 1.0F, 1.0F);
                        this.sendUseAbility(var2, var3);
                        var17 = var2.getNearbyEntities((double)this.plugin.config.TimelordFreezeRadius, (double)this.plugin.config.TimelordFreezeRadius, (double)this.plugin.config.TimelordFreezeRadius).iterator();

                        while(true) {
                           while(true) {
                              do {
                                 if (!var17.hasNext()) {
                                    return;
                                 }

                                 var15 = (Entity)var17.next();
                              } while(!var15.getType().equals(EntityType.PLAYER));

                              var27 = (Player)var15;
                              if (this.hasKit((PlayerData)this.plugin.playerData.get(var27.getName())) && !var3.getMap().isInSpawn(var27)) {
                                 var27.getWorld().playEffect(var27.getLocation(), Effect.STEP_SOUND, 152);
                                 var27.getWorld().playEffect(var27.getLocation().add(0.0D, 1.0D, 0.0D), Effect.STEP_SOUND, 152);
                                 var27.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, this.plugin.config.TimelordFreezeTime, 10));
                                 var27.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, this.plugin.config.TimelordFreezeTime, -4));
                                 var27.playSound(var2.getLocation(), this.plugin.WITHER_SHOOT, 1.0F, 1.0F);
                              } else {
                                 var2.sendMessage(this.kb + this.plugin.msgs.UseAbilityDeny);
                              }
                           }
                        }
                     }

                     return;
                  }

                  if (var4.equals(Material.BRICK)) {
                     if (this.hasAbility(var3, Ability.BURROWER)) {
                        if (var3.getMap().isInSpawn(var2)) {
                           var2.sendMessage(this.plugin.kb + this.plugin.msgs.AbilityUseDeny);
                           var1.setCancelled(true);
                           return;
                        }

                        if (var3.hasCooldown(var2, "Burrower")) {
                           return;
                        }

                        var3.setCooldown(var2, "Burrower", this.plugin.config.BurrowerCooldown, true);
                        var5 = new PlayerUseAbilityEvent(var2, Ability.BURROWER);
                        Bukkit.getPluginManager().callEvent(var5);
                        this.sendUseAbility(var2, var3);
                        var18 = this.getRoomLocations(var2.getLocation());
                        boolean var19 = true;
                        Iterator var23 = var18.iterator();

                        while(var23.hasNext()) {
                           Location var21 = (Location)var23.next();
                           if (var21.getBlock().getType() != Material.AIR) {
                              var19 = false;
                              break;
                           }
                        }

                        if (var19) {
                           final ArrayList var24 = new ArrayList();
                           Iterator var10 = var18.iterator();

                           while(var10.hasNext()) {
                              Location var28 = (Location)var10.next();
                              var24.add(var28.getBlock().getState());
                              var28.getBlock().setType(Material.BRICK);
                           }

                           ((Location)var18.get(0)).getBlock().setType(Material.GLOWSTONE);
                           this.toRollback.addAll(var24);
                           var2.teleport(var2.getLocation().add(0.0D, 10.0D, 0.0D));
                           Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
                              public void run() {
                                 Iterator var2 = var24.iterator();

                                 while(var2.hasNext()) {
                                    BlockState var1 = (BlockState)var2.next();
                                    AbilityListener.this.Rollback(var1);
                                 }

                                 AbilityListener.this.toRollback.removeAll(var24);
                              }
                           }, (long)this.plugin.config.BurrowerRoomLastsFor);
                        } else {
                           var2.sendMessage(this.kb + this.plugin.msgs.BurrowerNoSpace);
                        }
                     }

                     return;
                  }

                  if (var4.equals(Material.SLIME_BALL)) {
                     if (this.hasAbility(var3, Ability.ZEN)) {
                        if (var3.getMap().isInSpawn(var2)) {
                           var2.sendMessage(this.plugin.kb + this.plugin.msgs.AbilityUseDeny);
                           var1.setCancelled(true);
                           return;
                        }

                        if (var3.hasCooldown(var2, "Zen")) {
                           return;
                        }

                        var3.setCooldown(var2, "Zen", this.plugin.config.ZenCooldown, true);
                        var5 = new PlayerUseAbilityEvent(var2, Ability.ZEN);
                        Bukkit.getPluginManager().callEvent(var5);
                        this.sendUseAbility(var2, var3);
                        boolean var16 = false;
                        var8 = var2.getNearbyEntities((double)this.plugin.config.ZenMaxRange, (double)this.plugin.config.ZenMaxRange, (double)this.plugin.config.ZenMaxRange).iterator();

                        while(var8.hasNext()) {
                           var7 = (Entity)var8.next();
                           if (var7.getType() == EntityType.PLAYER) {
                              var9 = (Player)var7;
                              if (this.hasKit((PlayerData)this.plugin.playerData.get(var9.getName())) && !var3.getMap().isInSpawn(var9) && !this.plugin.isInTournament(var9) && !this.plugin.isInChallenge(var9)) {
                                 var16 = true;
                                 var2.getWorld().playEffect(var2.getLocation(), Effect.ENDER_SIGNAL, 1);
                                 var2.teleport(var9.getLocation());
                                 var2.getWorld().playEffect(var2.getLocation(), Effect.ENDER_SIGNAL, 1);
                                 break;
                              }
                           }
                        }

                        if (!var16) {
                           var2.sendMessage(this.kb + this.plugin.msgs.ZenNoPlayersFound);
                        }
                     }

                     return;
                  }

                  if (var4.equals(Material.PISTON_STICKY_BASE)) {
                     if (this.hasAbility(var3, Ability.HULK)) {
                        if (var3.getMap().isInSpawn(var2)) {
                           var2.sendMessage(this.plugin.kb + this.plugin.msgs.AbilityUseDeny);
                           var1.setCancelled(true);
                           return;
                        }

                        if (var3.hasCooldown(var2, "Hulk")) {
                           return;
                        }

                        var3.setCooldown(var2, "Hulk", this.plugin.config.HulkCooldown, true);
                        var5 = new PlayerUseAbilityEvent(var2, Ability.THOR);
                        Bukkit.getPluginManager().callEvent(var5);
                        this.sendUseAbility(var2, var3);
                        var2.getWorld().createExplosion(var2.getLocation(), 0.0F, false);
                        var17 = this.plugin.listen.getSurroundingLocations(var2.getLocation()).iterator();

                        while(var17.hasNext()) {
                           var14 = (Location)var17.next();
                           var2.getWorld().createExplosion(var14, 0.0F, false);
                        }

                        var17 = var2.getNearbyEntities((double)this.plugin.config.HulkDamageRadius, (double)this.plugin.config.HulkDamageRadius, (double)this.plugin.config.HulkDamageRadius).iterator();

                        while(true) {
                           while(true) {
                              do {
                                 if (!var17.hasNext()) {
                                    return;
                                 }

                                 var15 = (Entity)var17.next();
                              } while(!(var15 instanceof Damageable));

                              if (var15.getType().equals(EntityType.PLAYER) && (!this.hasKit((PlayerData)this.plugin.playerData.get(((Player)var15).getName())) || var3.getMap().isInSpawn((Player)var15))) {
                                 var2.sendMessage(this.kb + this.plugin.msgs.UseAbilityDeny);
                              } else {
                                 ((Damageable)var15).damage((double)this.plugin.config.HulkDamageDealt, var2);
                                 Vector var20 = var15.getType().equals(EntityType.PLAYER) ? ((Player)var15).getEyeLocation().getDirection() : var15.getVelocity();
                                 var20.multiply(-3);
                                 var20.setY(1.0D);
                                 var15.setVelocity(var20);
                              }
                           }
                        }
                     }

                     return;
                  }

                  if (var4.equals(Material.DIAMOND_BARDING)) {
                     if (this.hasAbility(var3, Ability.RIDER)) {
                        var1.setCancelled(true);
                        if (var3.getMap().isInSpawn(var2)) {
                           var2.sendMessage(this.plugin.kb + this.plugin.msgs.AbilityUseDeny);
                           var1.setCancelled(true);
                           return;
                        }

                        if (var3.hasCooldown(var2, "Rider")) {
                           return;
                        }

                        var3.setCooldown(var2, "Rider", this.plugin.config.RiderCooldown, true);
                        var5 = new PlayerUseAbilityEvent(var2, Ability.RIDER);
                        Bukkit.getPluginManager().callEvent(var5);
                        this.sendUseAbility(var2, var3);
                        final Horse var13 = (Horse)var2.getWorld().spawnEntity(var2.getLocation(), EntityType.HORSE);
                        var13.setAdult();
                        var13.setCustomName(var2.getName() + "'s Horse");
                        var13.setOwner(var2);
                        var13.setMaxHealth(40.0D);
                        var13.setHealth(40.0D);
                        var13.setMetadata("toRemove", new FixedMetadataValue(this.plugin, true));
                        var13.getInventory().setSaddle(new ItemStack(Material.SADDLE));
                        var13.setPassenger(var2);
                        this.toRemove.add(var13);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
                           public void run() {
                              AbilityListener.this.toRemove.remove(var13);
                              var13.remove();
                           }
                        }, (long)this.plugin.config.RiderHorseLastsFor);
                     }

                     return;
                  }

                  if (var4.equals(Material.IRON_BLOCK)) {
                     if (this.hasAbility(var3, Ability.SUMMONER)) {
                        if (var3.getMap().isInSpawn(var2)) {
                           var2.sendMessage(this.plugin.kb + this.plugin.msgs.AbilityUseDeny);
                           var1.setCancelled(true);
                           return;
                        }

                        if (var3.hasCooldown(var2, "Summoner")) {
                           return;
                        }

                        var3.setCooldown(var2, "Summoner", this.plugin.config.SummonerCooldown, true);
                        var5 = new PlayerUseAbilityEvent(var2, Ability.SUMMONER);
                        Bukkit.getPluginManager().callEvent(var5);
                        this.sendUseAbility(var2, var3);
                        final IronGolem var6 = (IronGolem)var2.getWorld().spawnEntity(var2.getLocation(), EntityType.IRON_GOLEM);
                        var6.setCustomName(var2.getName() + "'s Golem");
                        var6.setPassenger(var2);
                        var6.setMetadata("toRemove", new FixedMetadataValue(this.plugin, true));
                        var8 = var2.getNearbyEntities(10.0D, 10.0D, 10.0D).iterator();

                        while(var8.hasNext()) {
                           var7 = (Entity)var8.next();
                           if (var7.getType().equals(EntityType.PLAYER)) {
                              var6.setTarget((LivingEntity)var7);
                              break;
                           }
                        }

                        this.toRemove.add(var6);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
                           public void run() {
                              AbilityListener.this.toRemove.remove(var6);
                              var6.remove();
                           }
                        }, (long)this.plugin.config.SummonerGolemLastsFor);
                     }

                     return;
                  }
               }

            }
         }
      }
   }

   @EventHandler
   public void AbilityEntityDamageByEntity(EntityDamageByEntityEvent var1) {
      if (!var1.isCancelled()) {
         if (var1.getDamager().getType().equals(EntityType.PLAYER) && var1.getEntity().hasMetadata("toRemove") && var1.getEntity() instanceof LivingEntity) {
            LivingEntity var14 = (LivingEntity)var1.getEntity();
            if (var14.getCustomName() != null) {
               String var16 = var14.getCustomName().split("'s ")[0];
               if (((Player)var1.getDamager()).getName().equals(var16)) {
                  var1.setCancelled(true);
               }
            }

         } else if (var1.getDamager().getType().equals(EntityType.PLAYER) && var1.getEntity().getType().equals(EntityType.PLAYER)) {
            Player var13 = (Player)var1.getDamager();
            PlayerData var15 = (PlayerData)this.plugin.playerData.get(var13.getName());
            Player var17 = (Player)var1.getEntity();
            PlayerData var18 = (PlayerData)this.plugin.playerData.get(var17.getName());
            if (this.hasKit(var15) && this.hasKit(var18)) {
               if (this.hasAbility(var15, Ability.VIPER)) {
                  int var19 = this.random.nextInt(100) + 1;
                  if (var19 <= this.plugin.config.ViperPoisonChance) {
                     PlayerUseAbilityEvent var22 = new PlayerUseAbilityEvent(var17, Ability.VIPER);
                     Bukkit.getPluginManager().callEvent(var22);
                     this.sendUseAbility(var13, var15);
                     var17.removePotionEffect(PotionEffectType.POISON);
                     var17.addPotionEffect(this.plugin.config.poisonEffect);
                  }

               } else if (this.hasAbility(var15, Ability.DRACULA)) {
                  var13.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20, 2));
               }
            }
         } else {
            Player var3;
            PlayerData var4;
            Entity var5;
            PlayerUseAbilityEvent var6;
            if (var1.getDamager().getType().equals(EntityType.ARROW)) {
               Arrow var12 = (Arrow)var1.getDamager();
               if (var12.getShooter() instanceof Player) {
                  var3 = (Player)var12.getShooter();
                  var4 = (PlayerData)this.plugin.playerData.get(var3.getName());
                  if (this.hasKit(var4)) {
                     if (this.hasAbility(var4, Ability.BLINKER)) {
                        var1.setDamage(1.0D);
                        if (!var4.hasCooldown(var3, "Blinker")) {
                           var4.setCooldown(var3, "Blinker", this.plugin.config.BlinkerCooldown, true);
                           var5 = var1.getEntity();
                           if (!var5.getType().equals(EntityType.PLAYER) || this.hasKit((PlayerData)this.plugin.playerData.get(((Player)var5).getName())) && !var4.getMap().isInSpawn((Player)var5)) {
                              var6 = new PlayerUseAbilityEvent(var3, Ability.BLINKER);
                              Bukkit.getPluginManager().callEvent(var6);
                              this.sendUseAbility(var3, var4);
                              var3.teleport(var5.getLocation());
                              if (var5 instanceof Damageable) {
                                 ((Damageable)var5).damage(1.0D);
                              }

                              TNTPrimed var21 = (TNTPrimed)var3.getWorld().spawn(var3.getLocation().add(0.0D, 1.0D, 0.0D), TNTPrimed.class);
                              var21.setFuseTicks(30);
                              var21.setMetadata("tnts", new FixedMetadataValue(this.plugin, true));
                              var3.playSound(var3.getLocation(), this.plugin.ENDERMAN_DEATH, 1.0F, 1.0F);
                              if (var5.getType().equals(EntityType.PLAYER)) {
                                 ((Player)var5).playSound(var5.getLocation(), this.plugin.ENDERMAN_DEATH, 1.0F, 1.0F);
                                 ((Player)var5).sendMessage(this.kb + this.plugin.msgs.PlayerBlink.replace("%player%", var3.getName()));
                              }

                           } else {
                              var3.sendMessage(this.kb + this.plugin.msgs.UseAbilityDeny);
                           }
                        }
                     }
                  }
               }
            } else if (var1.getDamager().getType().equals(EntityType.SNOWBALL)) {
               Snowball var2 = (Snowball)var1.getDamager();
               if (var2.getShooter() instanceof Player) {
                  var3 = (Player)var2.getShooter();
                  var4 = (PlayerData)this.plugin.playerData.get(var3.getName());
                  if (this.hasKit(var4)) {
                     var5 = var1.getEntity();
                     if (!var5.getType().equals(EntityType.PLAYER) || this.hasKit((PlayerData)this.plugin.playerData.get(((Player)var5).getName())) && !var4.getMap().isInSpawn((Player)var5)) {
                        Location var20;
                        if (this.hasAbility(var4, Ability.SWITCHER)) {
                           var6 = new PlayerUseAbilityEvent(var3, Ability.SWITCHER);
                           Bukkit.getPluginManager().callEvent(var6);
                           this.sendUseAbility(var3, var4);
                           var20 = var3.getLocation();
                           var3.teleport(var5.getLocation());
                           var5.teleport(var20);
                           var3.playSound(var3.getLocation(), this.plugin.PISTON_EXTEND, 1.0F, 1.0F);
                           if (var5.getType().equals(EntityType.PLAYER)) {
                              ((Player)var5).playSound(var3.getLocation(), this.plugin.PISTON_EXTEND, 1.0F, 1.0F);
                           }

                        } else {
                           Block var9;
                           Iterator var11;
                           if (var2.hasMetadata("spiderman")) {
                              var6 = new PlayerUseAbilityEvent(var3, Ability.SPIDERMAN);
                              Bukkit.getPluginManager().callEvent(var6);
                              var20 = var5.getLocation();
                              final ArrayList var23 = new ArrayList();
                              var9 = var20.getBlock();
                              if (var9.getType().equals(Material.WATER) || var9.getType().equals(Material.LAVA)) {
                                 var20.add(0.0D, 1.0D, 0.0D);
                                 var9 = var20.getBlock();
                              }

                              var11 = this.plugin.listen.getSurroundingLocations(var20).iterator();

                              while(var11.hasNext()) {
                                 Location var27 = (Location)var11.next();
                                 if (!Material.WEB.equals(var27.getBlock().getType())) {
                                    var23.add(var27.getBlock().getState());
                                 }
                              }

                              if (!Material.WEB.equals(var9.getType())) {
                                 var23.add(var9.getState());
                              }

                              var11 = var23.iterator();

                              while(var11.hasNext()) {
                                 BlockState var28 = (BlockState)var11.next();
                                 this.toRollback.add(var28);
                                 var28.getBlock().setType(Material.WEB);
                              }

                              Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
                                 public void run() {
                                    Iterator var2 = var23.iterator();

                                    while(var2.hasNext()) {
                                       BlockState var1 = (BlockState)var2.next();
                                       var1.getWorld().createExplosion(var1.getLocation(), 0.0F, false);
                                       AbilityListener.this.Rollback(var1);
                                       AbilityListener.this.toRollback.remove(var1);
                                    }

                                 }
                              }, (long)this.plugin.config.SpidermanWebsLastFor);
                           } else if (var2.hasMetadata("prison")) {
                              var6 = new PlayerUseAbilityEvent(var3, Ability.PRISONER);
                              Bukkit.getPluginManager().callEvent(var6);
                              ArrayList var7 = this.getCageBlocks(var5.getLocation().add(0.0D, 9.0D, 0.0D));
                              boolean var8 = true;
                              Iterator var10 = var7.iterator();

                              while(var10.hasNext()) {
                                 var9 = (Block)var10.next();
                                 if (var9.getType() != Material.AIR) {
                                    var3.sendMessage(this.kb + this.plugin.msgs.PrisonerNoSpace);
                                    var8 = false;
                                    break;
                                 }
                              }

                              if (var8) {
                                 final ArrayList var24 = new ArrayList();
                                 var11 = var7.iterator();

                                 while(var11.hasNext()) {
                                    Block var25 = (Block)var11.next();
                                    var24.add(var25.getState());
                                 }

                                 ((Block)var7.get(0)).setType(Material.MOSSY_COBBLESTONE);

                                 for(int var26 = 1; var26 < 9; ++var26) {
                                    ((Block)var7.get(var26)).setType(Material.IRON_FENCE);
                                 }

                                 ((Block)var7.get(9)).setType(Material.MOSSY_COBBLESTONE);
                                 ((Block)var7.get(10)).setType(Material.LAVA);
                                 ((Damageable)var5).damage(1.0D, var3);
                                 var5.teleport(var5.getLocation().add(0.0D, 9.0D, 0.0D));
                                 this.toRollback.addAll(var24);
                                 Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
                                    public void run() {
                                       Iterator var2 = var24.iterator();

                                       while(var2.hasNext()) {
                                          BlockState var1 = (BlockState)var2.next();
                                          AbilityListener.this.Rollback(var1);
                                       }

                                       AbilityListener.this.toRollback.removeAll(var24);
                                    }
                                 }, (long)this.plugin.config.PrisonerPrisonLastsFor);
                              }
                           }
                        }
                     } else {
                        var3.sendMessage(this.kb + this.plugin.msgs.UseAbilityDeny);
                     }
                  }
               }
            }
         }
      }
   }

   @EventHandler
   public void AbilityEntityDamageEvent(EntityDamageEvent var1) {
      if (var1.getEntity().getType() == EntityType.PLAYER) {
         Player var2 = (Player)var1.getEntity();
         if (var1.getCause().equals(DamageCause.FALL)) {
            PlayerData var3 = (PlayerData)this.plugin.playerData.get(var2.getName());
            if (!this.hasKit(var3)) {
               return;
            }

            if (this.hasAbility(var3, Ability.STOMPER)) {
               PlayerUseAbilityEvent var4 = new PlayerUseAbilityEvent(var2, Ability.STOMPER);
               Bukkit.getPluginManager().callEvent(var4);
               double var5 = var1.getDamage();
               if (var5 > (double)this.plugin.config.StomperMaxFallDamage) {
                  var1.setDamage((double)this.plugin.config.StomperMaxFallDamage);
               }

               var2.playSound(var2.getLocation(), this.plugin.ANVIL_LAND, 1.0F, 1.0F);
               Iterator var8 = var2.getNearbyEntities((double)this.plugin.config.StomperStompRadius, (double)this.plugin.config.StomperStompRadius, (double)this.plugin.config.StomperStompRadius).iterator();

               while(true) {
                  while(true) {
                     while(var8.hasNext()) {
                        Entity var7 = (Entity)var8.next();
                        if (!(var7 instanceof Damageable)) {
                           return;
                        }

                        if (var7 instanceof Player) {
                           Player var9 = (Player)var7;
                           if (this.hasKit((PlayerData)this.plugin.playerData.get(var9.getName())) && !var3.getMap().isInSpawn(var9)) {
                              var9.playSound(var9.getLocation(), this.plugin.ANVIL_LAND, 1.0F, 1.0F);
                              if (!var9.isSneaking()) {
                                 var9.damage(var5, var2);
                              } else if (var5 > (double)this.plugin.config.StomperMaxDamageDealtWhenStompedWhileShifting) {
                                 var9.damage((double)this.plugin.config.StomperMaxDamageDealtWhenStompedWhileShifting);
                              } else {
                                 var9.damage(var5, var2);
                              }
                           } else {
                              var2.sendMessage(this.kb + this.plugin.msgs.UseAbilityDeny);
                           }
                        } else {
                           ((Damageable)var7).damage(var5, var2);
                        }
                     }

                     return;
                  }
               }
            }
         }

      }
   }

   @EventHandler
   public void AbilityShootArrowEvent(ProjectileLaunchEvent var1) {
      if (var1.getEntity().getType() == EntityType.ARROW) {
         Arrow var2 = (Arrow)var1.getEntity();
         if (var2.getShooter() instanceof Player) {
            Player var3 = (Player)var2.getShooter();
            PlayerData var4 = (PlayerData)this.plugin.playerData.get(var3.getName());
            if (this.hasKit(var4)) {
               if (this.hasAbility(var4, Ability.CLIMBER)) {
                  var1.setCancelled(true);
                  if (var4.getMap().isInSpawn(var3)) {
                     var3.sendMessage(this.plugin.kb + this.plugin.msgs.AbilityUseDeny);
                     var1.setCancelled(true);
                     return;
                  }

                  if (var4.hasCooldown(var3, "Climber")) {
                     return;
                  }

                  var4.setCooldown(var3, "Climber", this.plugin.config.ClimberCooldown, true);
                  PlayerUseAbilityEvent var5 = new PlayerUseAbilityEvent(var3, Ability.CLIMBER);
                  Bukkit.getPluginManager().callEvent(var5);
                  final Chicken var6 = (Chicken)var3.getWorld().spawnEntity(var3.getLocation(), EntityType.CHICKEN);
                  var6.setVelocity(var2.getVelocity().multiply(2));
                  var6.setPassenger(var3);
                  var6.setMetadata("toRemove", new FixedMetadataValue(this.plugin, true));
                  this.sendUseAbility(var3, var4);
                  this.toRemove.add(var6);
                  Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
                     public void run() {
                        AbilityListener.this.toRemove.remove(var6);
                        var6.remove();
                     }
                  }, (long)this.plugin.config.ClimberTimeUntilChickenDisappear);
               }

            }
         }
      }
   }

   @EventHandler
   public void AbilityEntityExplode(EntityExplodeEvent var1) {
      if (var1.getEntity() != null && var1.getEntity().hasMetadata("tnts")) {
         var1.blockList().clear();
      }

   }

   @EventHandler
   public void AbilityFishEvent(PlayerFishEvent var1) {
      Player var2 = var1.getPlayer();
      PlayerData var3 = (PlayerData)this.plugin.playerData.get(var2.getName());
      if (var1.getState().equals(State.CAUGHT_ENTITY) && this.hasKit(var3) && this.hasAbility(var3, Ability.FISHERMAN)) {
         if (var1.getCaught().getType().equals(EntityType.PLAYER) && (!this.hasKit((PlayerData)this.plugin.playerData.get(((Player)var1.getCaught()).getName())) || var3.getMap().isInSpawn((Player)var1.getCaught()))) {
            var2.sendMessage(this.kb + this.plugin.msgs.UseAbilityDeny);
         } else if (!var3.hasCooldown(var2, "Fisherman")) {
            var3.setCooldown(var2, "Fisherman", this.plugin.config.FishermanCooldown, true);
            this.sendUseAbility(var2, var3);
            PlayerUseAbilityEvent var4 = new PlayerUseAbilityEvent(var2, Ability.FISHERMAN);
            Bukkit.getPluginManager().callEvent(var4);
            var1.getCaught().teleport(var2.getLocation());
         }
      }
   }

   @EventHandler
   public void AbilityPlayerInteractEntityEvent(PlayerInteractEntityEvent var1) {
      if (!var1.isCancelled()) {
         if (var1.getRightClicked().getType().equals(EntityType.PLAYER)) {
            Player var2 = var1.getPlayer();
            PlayerData var3 = (PlayerData)this.plugin.playerData.get(var2.getName());
            if (this.hasKit(var3)) {
               Player var4;
               PlayerUseAbilityEvent var5;
               if (var2.getItemInHand().getType() == Material.BLAZE_ROD) {
                  if (this.hasAbility(var3, Ability.MONK)) {
                     if (var3.getMap().isInSpawn(var2)) {
                        var2.sendMessage(this.plugin.kb + this.plugin.msgs.AbilityUseDeny);
                        var1.setCancelled(true);
                        return;
                     }

                     if (var3.hasCooldown(var2, "Monk")) {
                        return;
                     }

                     var3.setCooldown(var2, "Monk", this.plugin.config.MonkCooldown, true);
                     var4 = (Player)var1.getRightClicked();
                     this.sendUseAbility(var2, var3);
                     if (!this.hasKit((PlayerData)this.plugin.playerData.get(var4.getName())) || var3.getMap().isInSpawn(var4)) {
                        var2.sendMessage(this.kb + this.plugin.msgs.UseAbilityDeny);
                        return;
                     }

                     var5 = new PlayerUseAbilityEvent(var2, Ability.MONK);
                     Bukkit.getPluginManager().callEvent(var5);
                     int var10 = var4.getInventory().getHeldItemSlot();

                     int var7;
                     for(var7 = this.random.nextInt(9); var10 == var7; var7 = this.random.nextInt(9)) {
                     }

                     ItemStack var8 = var4.getItemInHand();
                     ItemStack var9 = var4.getInventory().getItem(var7);
                     var4.getInventory().setItem(var10, var9);
                     var4.getInventory().setItem(var7, var8);
                     var4.updateInventory();
                  }

               } else if (var2.getItemInHand().getType() == Material.GOLD_HOE) {
                  if (this.hasAbility(var3, Ability.BANE)) {
                     if (var3.getMap().isInSpawn(var2)) {
                        var2.sendMessage(this.plugin.kb + this.plugin.msgs.AbilityUseDeny);
                        var1.setCancelled(true);
                        return;
                     }

                     if (var3.hasCooldown(var2, "Bane")) {
                        return;
                     }

                     var3.setCooldown(var2, "Bane", this.plugin.config.BaneCooldown, true);
                     var4 = (Player)var1.getRightClicked();
                     this.sendUseAbility(var2, var3);
                     if (!this.hasKit((PlayerData)this.plugin.playerData.get(var4.getName())) || var3.getMap().isInSpawn(var4)) {
                        var2.sendMessage(this.kb + this.plugin.msgs.UseAbilityDeny);
                        return;
                     }

                     var5 = new PlayerUseAbilityEvent(var2, Ability.BANE);
                     Bukkit.getPluginManager().callEvent(var5);
                     var4.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, this.plugin.config.BaneDuration, 3));
                     var4.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, this.plugin.config.BaneDuration, 3));
                     var4.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, this.plugin.config.BaneDuration, 3));
                     var4.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, this.plugin.config.BaneDuration, 2));
                     var4.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, this.plugin.config.BaneDuration, 2));
                     var4.sendMessage(this.kb + this.plugin.msgs.BaneCurse.replace("%player%", var2.getName()));
                     var4.playSound(var2.getLocation(), this.plugin.ENDERMAN_SCREAM, 1.0F, 1.0F);
                     var2.playSound(var2.getLocation(), this.plugin.ENDERMAN_SCREAM, 1.0F, 1.0F);
                  }

               } else if (var2.getItemInHand().getType() == Material.BEACON) {
                  if (this.hasAbility(var3, Ability.SUNDER)) {
                     if (var3.getMap().isInSpawn(var2)) {
                        var2.sendMessage(this.plugin.kb + this.plugin.msgs.AbilityUseDeny);
                        var1.setCancelled(true);
                        return;
                     }

                     if (var3.hasCooldown(var2, "Sunder")) {
                        return;
                     }

                     var3.setCooldown(var2, "Sunder", this.plugin.config.SunderCooldown, true);
                     var4 = (Player)var1.getRightClicked();
                     this.sendUseAbility(var2, var3);
                     if (!this.hasKit((PlayerData)this.plugin.playerData.get(var4.getName())) || var3.getMap().isInSpawn(var4)) {
                        var2.sendMessage(this.kb + this.plugin.msgs.UseAbilityDeny);
                        return;
                     }

                     var5 = new PlayerUseAbilityEvent(var2, Ability.SUNDER);
                     Bukkit.getPluginManager().callEvent(var5);
                     double var6 = var4.getHealth();
                     var4.setHealth(var2.getHealth());
                     var2.setHealth(var6);
                     var4.sendMessage(this.kb + this.plugin.msgs.SunderSwap.replace("%player%", var2.getName()));
                     var4.playSound(var2.getLocation(), this.plugin.IRONGOLEM_DEATH, 1.0F, 1.0F);
                     var2.playSound(var2.getLocation(), this.plugin.IRONGOLEM_DEATH, 1.0F, 1.0F);
                  }

               }
            }
         }
      }
   }

   @EventHandler
   public void AbilityEntityDeathEvent(EntityDeathEvent var1) {
      if (var1.getEntity().hasMetadata("toRemove")) {
         var1.setDroppedExp(0);
         var1.getDrops().clear();
      }

   }

   @EventHandler
   public void onEntityTargetEvent(EntityTargetEvent var1) {
      if (var1.getTarget() != null) {
         if (var1.getEntity() instanceof LivingEntity && var1.getTarget().getType().equals(EntityType.PLAYER)) {
            LivingEntity var2 = (LivingEntity)var1.getEntity();
            if (var2.hasMetadata("toRemove") && var2.getCustomName() != null) {
               String var3 = var2.getCustomName().split("'s ")[0];
               if (((Player)var1.getTarget()).getName().equals(var3)) {
                  var1.setCancelled(true);
               }
            }
         }

      }
   }

   public void sendUseAbility(Player var1, PlayerData var2) {
      var1.playSound(var1.getLocation(), this.plugin.CLICK, 1.0F, 1.0F);
      var2.addAbilitiesUsed(var1);
   }

   public boolean hasKit(PlayerData var1) {
      return var1.getKit() != null;
   }

   public boolean hasAbility(PlayerData var1, Ability var2) {
      return var1.getKit().getAbilities().contains(var2);
   }

   public ArrayList getCageBlocks(Location var1) {
      ArrayList var2 = new ArrayList();
      var2.add(var1.clone().add(0.0D, -1.0D, 0.0D).getBlock());
      var2.add(var1.clone().add(-1.0D, 0.0D, 0.0D).getBlock());
      var2.add(var1.clone().add(0.0D, 0.0D, 1.0D).getBlock());
      var2.add(var1.clone().add(0.0D, 0.0D, -1.0D).getBlock());
      var2.add(var1.clone().add(1.0D, 0.0D, 0.0D).getBlock());
      var2.add(var1.clone().add(-1.0D, 0.0D, -1.0D).getBlock());
      var2.add(var1.clone().add(-1.0D, 0.0D, 1.0D).getBlock());
      var2.add(var1.clone().add(1.0D, 0.0D, -1.0D).getBlock());
      var2.add(var1.clone().add(1.0D, 0.0D, 1.0D).getBlock());
      var2.add(var1.clone().add(0.0D, 2.0D, 0.0D).getBlock());
      var2.add(var1.getBlock());
      var2.add(var1.add(0.0D, 1.0D, 0.0D).getBlock());
      return var2;
   }

   public ArrayList getPlatForm(Location var1) {
      ArrayList var2 = new ArrayList();
      var2.add(var1.clone());
      var2.add(var1.clone().add(-1.0D, 0.0D, 0.0D));
      var2.add(var1.clone().add(0.0D, 0.0D, -1.0D));
      var2.add(var1.clone().add(1.0D, 0.0D, 0.0D));
      var2.add(var1.clone().add(0.0D, 0.0D, 1.0D));
      var2.add(var1.clone().add(-1.0D, 0.0D, -1.0D));
      var2.add(var1.clone().add(1.0D, 0.0D, -1.0D));
      var2.add(var1.clone().add(1.0D, 0.0D, 1.0D));
      var2.add(var1.clone().add(-1.0D, 0.0D, 1.0D));
      return var2;
   }

   public ArrayList getRoomLocations(Location var1) {
      ArrayList var2 = new ArrayList();
      var1.add(0.0D, 9.0D, 0.0D);
      var2.addAll(this.getPlatForm(var1));

      for(int var3 = 0; var3 < 3; ++var3) {
         var1.add(0.0D, 1.0D, 0.0D);
         var2.add(var1.clone().add(0.0D, 0.0D, -2.0D));
         var2.add(var1.clone().add(0.0D, 0.0D, 2.0D));
         var2.add(var1.clone().add(2.0D, 0.0D, 0.0D));
         var2.add(var1.clone().add(-2.0D, 0.0D, 0.0D));
         var2.add(var1.clone().add(-2.0D, 0.0D, 2.0D));
         var2.add(var1.clone().add(-2.0D, 0.0D, -2.0D));
         var2.add(var1.clone().add(2.0D, 0.0D, -2.0D));
         var2.add(var1.clone().add(2.0D, 0.0D, 2.0D));
         var2.add(var1.clone().add(1.0D, 0.0D, 2.0D));
         var2.add(var1.clone().add(-1.0D, 0.0D, 2.0D));
         var2.add(var1.clone().add(-2.0D, 0.0D, 1.0D));
         var2.add(var1.clone().add(-2.0D, 0.0D, -1.0D));
         var2.add(var1.clone().add(-1.0D, 0.0D, -2.0D));
         var2.add(var1.clone().add(1.0D, 0.0D, -2.0D));
         var2.add(var1.clone().add(2.0D, 0.0D, -1.0D));
         var2.add(var1.clone().add(2.0D, 0.0D, 1.0D));
      }

      var2.addAll(this.getPlatForm(var1.add(0.0D, 1.0D, 0.0D)));
      return var2;
   }

   public void Rollback(BlockState var1) {
      if (var1 instanceof Sign) {
         Sign var2 = (Sign)var1;
         Location var3 = var2.getLocation();
         var3.getWorld().getBlockAt(var3).setType(var1.getType());
         Sign var4 = (Sign)var3.getWorld().getBlockAt(var3).getState();

         for(int var5 = 0; var5 < 4; ++var5) {
            var4.setLine(var5, var2.getLines()[var5]);
         }

         var4.update(true);
      } else {
         var1.update(true);
      }

   }
}
