package me.wazup.eggwars;

import io.puharesource.mc.titlemanager.api.ActionbarTitleObject;
import io.puharesource.mc.titlemanager.api.TitleObject;
import io.puharesource.mc.titlemanager.api.TitleObject.TitleType;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

class EggwarsListener implements Listener {
   private Eggwars plugin;

   public EggwarsListener(Eggwars var1) {
      this.plugin = var1;
   }

   @EventHandler
   public void onPlayerPreLogin(PlayerLoginEvent var1) {
      if (this.plugin.isOneGamePerServer()) {
         if (!((Arena)this.plugin.arenas.values().toArray()[0]).state.AVAILABLE()) {
            var1.disallow(Result.KICK_OTHER, (String)this.plugin.customization.messages.get("Arena-Not-Available"));
         }

      }
   }

   @EventHandler
   public void onPlayerJoin(PlayerJoinEvent var1) {
      Player var2 = var1.getPlayer();
      this.plugin.playerData.put(var2.getName(), new PlayerData(this.plugin, var2));
      if (this.plugin.config.bungee_mode_enabled) {
         if (this.plugin.lobbyLocation != null) {
            this.plugin.join(var2);
         } else {
            var2.sendMessage((String)this.plugin.customization.messages.get("Lobby-Not-Set"));
         }
      }

      if (var2.hasPermission("eggwars.admin")) {
         if (this.plugin.availableUpdate) {
            var2.sendMessage(this.plugin.customization.prefix + "Found a new available version! " + ChatColor.LIGHT_PURPLE + "download at https://goo.gl/dcsNEz");
         } else {
            this.plugin.checkUpdates(var2);
         }
      }

   }

   @EventHandler
   public void onPlayerQuit(PlayerQuitEvent var1) {
      Player var2 = var1.getPlayer();
      PlayerData var3 = (PlayerData)this.plugin.playerData.get(var2.getName());
      if (var3.party != null) {
         var3.party.leave(var2);
      }

      if (this.plugin.players.contains(var2.getName())) {
         this.awardKiller(var2);
         this.plugin.leave(var2, true);
      }

      var3.saveAsync(var2);
   }

   @EventHandler
   public void onPlayerInteract(PlayerInteractEvent var1) {
      Player var2 = var1.getPlayer();
      if (this.plugin.selectionMode.containsKey(var2.getName()) && this.plugin.compareItem(var2.getItemInHand(), this.plugin.wand_itemstack)) {
         var1.setCancelled(true);
         int var14 = var1.getAction().equals(Action.LEFT_CLICK_BLOCK) ? 0 : (var1.getAction().equals(Action.RIGHT_CLICK_BLOCK) ? 1 : 2);
         if (var14 != 2) {
            Location var16 = var1.getClickedBlock().getLocation();
            Location var21 = ((Location[])this.plugin.selectionMode.get(var2.getName()))[var14 == 1 ? 0 : 1];
            ((Location[])this.plugin.selectionMode.get(var2.getName()))[var14] = var16;
            if (var21 != null && !var21.getWorld().getName().equals(var16.getWorld().getName())) {
               var21 = null;
            }

            var2.sendMessage(this.plugin.customization.prefix + "You have set the " + ChatColor.LIGHT_PURPLE + "#" + (var14 + 1) + ChatColor.GRAY + " corner at " + this.plugin.getReadableLocationString(var16, false) + (var21 != null ? ChatColor.AQUA + " (" + (new Cuboid(var16, var21)).getSize() + ")" : ""));
         }
      } else {
         Spawner var20;
         if (var1.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Block var3 = var1.getClickedBlock();
            if (var3.getState() instanceof Sign) {
               Sign var4 = (Sign)var3.getState();
               if (var4.getLine(0).equals(this.plugin.customization.signs_title)) {
                  var1.setCancelled(true);
                  if (var4.getLine(2).equals(ChatColor.BOLD + "Generator")) {
                     PlayerData var19 = (PlayerData)this.plugin.playerData.get(var2.getName());
                     if (var19.arena != null && var19.arena.players.get(var2.getName()) != null && !var19.arena.state.AVAILABLE()) {
                        var20 = var19.arena.getSpawner(var4);
                        if (var20 != null) {
                           if (var20.upgradeInventory != null) {
                              var2.openInventory(var20.upgradeInventory);
                           } else {
                              var2.sendMessage((String)this.plugin.customization.messages.get("Spawner-Upgrade-Deny"));
                           }
                        }
                     }

                     return;
                  }

                  if (var2.getItemInHand().getType() != Material.AIR) {
                     var2.sendMessage((String)this.plugin.customization.messages.get("Must-Have-Empty-Hand"));
                     return;
                  }

                  if (((PlayerData)this.plugin.playerData.get(var2.getName())).hasCooldown(var2, "SIGN_INTERACT", 3)) {
                     return;
                  }

                  if (!this.plugin.lobbyPlayers.contains(var2.getName()) && (!var4.getLine(1).equals(this.plugin.customization.signs_join) || var4.getLine(1).equals(this.plugin.customization.signs_join) && (!this.plugin.joinSigns.containsKey(var4.getLocation()) || this.plugin.arenas.containsKey(ChatColor.stripColor(var4.getLine(2).toLowerCase()))))) {
                     var2.sendMessage((String)this.plugin.customization.messages.get("Not-In-Lobby"));
                     return;
                  }

                  if (var4.getLine(1).equals(this.plugin.customization.signs_join)) {
                     String var18 = ChatColor.stripColor(var4.getLine(2)).toLowerCase();
                     if (!this.plugin.arenas.containsKey(var18)) {
                        if (this.plugin.players.contains(var2.getName())) {
                           var2.sendMessage((String)this.plugin.customization.messages.get("Already-In-Game"));
                           return;
                        }

                        this.plugin.join(var2);
                     } else {
                        ((Arena)this.plugin.arenas.get(var18)).join(var2);
                     }
                  } else if (var4.getLine(1).equals(this.plugin.customization.signs_leave)) {
                     this.plugin.leave(var2, false);
                  } else if (var4.getLine(1).equals(this.plugin.customization.signs_autojoin)) {
                     this.plugin.autojoin(var2, var4.getLine(2).toUpperCase());
                  }

                  return;
               }
            }
         }

         if (this.plugin.players.contains(var2.getName())) {
            PlayerData var13 = (PlayerData)this.plugin.playerData.get(var2.getName());
            final Arena var15 = var13.arena;
            Block var5;
            if (this.plugin.protectedPlayers.contains(var2.getName())) {
               var1.setCancelled(true);
               if (this.plugin.spectatorMode != null && this.plugin.players.contains(var2.getName()) && this.plugin.protectedPlayers.contains(var2.getName()) && var2.getGameMode().equals(this.plugin.spectatorMode)) {
                  var2.openInventory(this.plugin.spectatorMenu);
                  return;
               } else {
                  if (var1.getAction().equals(Action.RIGHT_CLICK_AIR) || var1.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                     if (var1.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                        var5 = var1.getClickedBlock();
                        if (var5.getType().equals(this.plugin.mysteryBox.type)) {
                           if (var15 != null && !var15.state.AVAILABLE()) {
                              return;
                           }

                           if (!this.plugin.mysteryBox.ready) {
                              var2.sendMessage((String)this.plugin.customization.messages.get("Invalid-Mystery-Box"));
                              return;
                           }

                           var2.openInventory(this.plugin.mysteryBox.inventory);
                           return;
                        }
                     }

                     if (this.plugin.compareItem(var2.getItemInHand(), this.plugin.quit_itemstack)) {
                        var2.openInventory(this.plugin.quitInventory);
                        return;
                     }

                     if (this.plugin.compareItem(var2.getItemInHand(), this.plugin.profile_itemstack)) {
                        var2.openInventory(this.plugin.profileInventory);
                        return;
                     }

                     if (this.plugin.compareItem(var2.getItemInHand(), this.plugin.shop_itemstack)) {
                        this.plugin.shop.open(var2);
                        return;
                     }

                     if (this.plugin.compareItem(var2.getItemInHand(), this.plugin.play_itemstack)) {
                        this.plugin.arenaSelector.open(var2);
                        return;
                     }

                     if (var15 != null && var13.arena.state.AVAILABLE()) {
                        if (this.plugin.compareItem(var2.getItemInHand(), this.plugin.teamselector_itemstack)) {
                           var2.openInventory(var15.teamSelector);
                           return;
                        }

                        if (this.plugin.compareItem(var2.getItemInHand(), this.plugin.vote_itemstack)) {
                           var2.openInventory(this.plugin.votingOptions);
                           return;
                        }

                        if (this.plugin.compareItem(var2.getItemInHand(), this.plugin.inventory_itemstack)) {
                           var13.inventory.open(var2);
                           return;
                        }
                     }

                     if (this.plugin.lobbyPlayers.contains(var2.getName())) {
                        if (this.plugin.compareItem(var2.getItemInHand(), this.plugin.party_itemstack)) {
                           if (var13.party == null) {
                              var2.openInventory(this.plugin.partyMenu);
                           } else {
                              var2.openInventory(var13.party.gui);
                           }

                           return;
                        }

                        return;
                     }

                     if (this.plugin.compareItem(var2.getItemInHand(), this.plugin.teleporter_itemstack) && var15 != null && var15.spectators.contains(var2.getName())) {
                        if (var13.hasCooldown(var2, "TELEPORT_GUI", 3)) {
                           return;
                        }

                        var2.openInventory(var13.arena.getTeleporterInventory());
                        return;
                     }
                  }

                  return;
               }
            }

            if (var15 == null) {
               return;
            }

            if (var1.getAction().equals(Action.RIGHT_CLICK_BLOCK) || var1.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
               var5 = var1.getClickedBlock();
               if (var5.getType().equals(this.plugin.config.coreBlockType)) {
                  Iterator var7 = var13.arena.teams.keySet().iterator();

                  while(var7.hasNext()) {
                     Team var6 = (Team)var7.next();
                     if (var5.getLocation().distance(((Arena.TeamData)var13.arena.teams.get(var6)).egg) <= 2.0D) {
                        var1.setCancelled(true);
                        if (!((Arena.TeamData)var15.teams.get(var6)).egg_alive) {
                           return;
                        }

                        if (((Team)var15.players.get(var2.getName())).equals(var6)) {
                           var2.sendMessage((String)this.plugin.customization.messages.get("Egg-Break-Error"));
                           return;
                        }

                        ++var13.eggs_broken;
                        int var8 = this.plugin.config.coinsPerEggBreak * var13.modifier;
                        var13.addCoins(var2, var8);
                        var15.destroyCore(var6, true);
                        this.plugin.achievementsManager.checkPlayer(var2, Enums.AchievementType.EGGS_BROKEN, var13.eggs_broken);
                        var2.sendMessage(((String)this.plugin.customization.messages.get("Egg-Break-Money-Earn")).replace("%coins%", String.valueOf(var8)));
                        return;
                     }
                  }
               }
            }

            if (var1.getAction().equals(Action.RIGHT_CLICK_BLOCK) || var1.getAction().equals(Action.RIGHT_CLICK_AIR)) {
               ItemStack var17 = var2.getItemInHand();
               if (var1.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                  if (var2.getItemInHand().getType().equals(Material.MONSTER_EGG) || var2.getItemInHand().getType().equals(Material.TNT)) {
                     if (var2.getItemInHand().getType().equals(Material.MONSTER_EGG)) {
                        short var23 = var2.getItemInHand().getDurability();
                        EntityType var37 = EntityType.fromId(var23);
                        if (var37 == null && var2.getItemInHand().getItemMeta().getDisplayName() != null) {
                           String var38 = ChatColor.stripColor(var2.getItemInHand().getItemMeta().getDisplayName().toUpperCase());
                           var37 = EntityType.valueOf(var38);
                        }

                        if (var37 != null) {
                           final LivingEntity var39 = (LivingEntity)var2.getWorld().spawnEntity(var1.getClickedBlock().getRelative(var1.getBlockFace()).getLocation().add(0.5D, 0.0D, 0.5D), var37);
                           var39.setCustomName(ChatColor.AQUA + var2.getName() + "'s " + var39.getType().getName());
                           var39.setCustomNameVisible(true);
                           var39.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 999999, 3));
                           var39.setMetadata("EW_MOB", new FixedMetadataValue(this.plugin, true));
                           if (var39 instanceof Ageable) {
                              ((Ageable)var39).setAdult();
                           }

                           if (this.plugin.config.leashMobs) {
                              var39.setLeashHolder(var2);
                           }

                           if (var39 instanceof Creature) {
                              final String var46 = var2.getName();
                              (new BukkitRunnable() {
                                 public void run() {
                                    if (!var39.isDead() && var39.isValid()) {
                                       PlayerData var1;
                                       if (EggwarsListener.this.plugin.players.contains(var46) && (var1 = (PlayerData)EggwarsListener.this.plugin.playerData.get(var46)).arena != null && !var1.arena.state.AVAILABLE() && !var1.arena.spectators.contains(var46)) {
                                          List var2 = var1.arena.getEnemeies((Team)var1.arena.players.get(var46));
                                          if (var2.isEmpty()) {
                                             var39.remove();
                                          } else {
                                             int var3 = Integer.MAX_VALUE;
                                             Player var4 = null;
                                             Iterator var6 = var2.iterator();

                                             while(var6.hasNext()) {
                                                Player var5 = (Player)var6.next();
                                                if (var39.getWorld().getName().equals(var5.getWorld().getName())) {
                                                   double var7 = var5.getLocation().distance(var39.getLocation());
                                                   if (var7 < (double)var3) {
                                                      var3 = (int)var7;
                                                      var4 = var5;
                                                   }
                                                }
                                             }

                                             if (var4 != null) {
                                                ((Creature)var39).setTarget(var4);
                                             } else {
                                                var39.remove();
                                                this.cancel();
                                             }

                                          }
                                       } else {
                                          var39.remove();
                                          this.cancel();
                                       }
                                    } else {
                                       this.cancel();
                                    }
                                 }
                              }).runTaskTimer(this.plugin, 0L, 40L);
                           }
                        } else {
                           var2.sendMessage((String)this.plugin.customization.messages.get("Invalid-Entity"));
                        }
                     } else {
                        TNTPrimed var24 = (TNTPrimed)var2.getWorld().spawn(var1.getClickedBlock().getRelative(var1.getBlockFace()).getLocation().add(0.5D, 0.0D, 0.5D), TNTPrimed.class);
                        var24.setFuseTicks(30);
                     }

                     if (var2.getItemInHand().getAmount() > 1) {
                        var2.getItemInHand().setAmount(var2.getItemInHand().getAmount() - 1);
                     } else {
                        var2.setItemInHand(new ItemStack(Material.AIR));
                     }

                     var1.setCancelled(true);
                     return;
                  }

                  if (var1.getClickedBlock().getType().equals(Material.ENDER_CHEST)) {
                     var2.openInventory(((Arena.TeamData)var15.teams.get(var15.players.get(var2.getName()))).enderchest);
                     var1.setCancelled(true);
                     return;
                  }

                  if (var1.getClickedBlock().getState() instanceof InventoryHolder) {
                     if (!var15.openedHolders.contains(var1.getClickedBlock().getLocation())) {
                        var15.openedHolders.add(var1.getClickedBlock().getLocation());
                     }

                     return;
                  }

                  var20 = var15.getSpawner(var1.getClickedBlock().getLocation());
                  if (var20 != null) {
                     if (var20.upgradeInventory != null) {
                        var2.openInventory(var20.upgradeInventory);
                     } else {
                        var2.sendMessage((String)this.plugin.customization.messages.get("Spawner-Upgrade-Deny"));
                     }
                  }
               }

               if (!var17.getType().equals(Material.AIR) && var17.getItemMeta().hasDisplayName()) {
                  String var22 = ChatColor.stripColor(var2.getItemInHand().getItemMeta().getDisplayName());
                  if (!var22.startsWith("Special - ")) {
                     return;
                  }

                  var1.setCancelled(true);
                  if (var13.hasCooldown(var2, "SPECIAL_ITEM", 5)) {
                     return;
                  }

                  Iterator var10;
                  if (var22.equals("Special - Tracker")) {
                     int var33 = Integer.MAX_VALUE;
                     Player var34 = null;
                     var10 = var15.getEnemeies((Team)var15.players.get(var2.getName())).iterator();

                     while(var10.hasNext()) {
                        Player var43 = (Player)var10.next();
                        if (var2.getWorld().getName().equals(var43.getWorld().getName())) {
                           double var45 = var43.getLocation().distance(var2.getLocation());
                           if (var45 < (double)var33) {
                              var33 = (int)var45;
                              var34 = var43;
                           }
                        }
                     }

                     if (var34 != null) {
                        var2.setCompassTarget(var34.getLocation());
                        var2.sendMessage(((String)this.plugin.customization.messages.get("Tracking-Player")).replace("%target%", var34.getName()).replace("%distance%", String.valueOf(var33)));
                     } else {
                        (new ItemStackBuilder(var2.getItemInHand())).setName(ChatColor.RED + "No nearby players!").build();
                     }

                     return;
                  }

                  if (var17.getAmount() > 1) {
                     var2.getItemInHand().setAmount(var17.getAmount() - 1);
                  } else {
                     var2.setItemInHand(new ItemStack(Material.AIR));
                  }

                  Vector var31;
                  int var36;
                  if (var22.equals("Special - Teleporter")) {
                     Vector[] var32 = new Vector[]{new Vector(-1, 0, 0), new Vector(1, 0, 0), new Vector(0, 0, 1), new Vector(0, 0, -1)};
                     Vector[] var44 = var32;
                     int var42 = var32.length;

                     for(var36 = 0; var36 < var42; ++var36) {
                        var31 = var44[var36];
                        this.plugin.fireWorkEffect(var2.getLocation().add(var31), true);
                     }

                     var2.teleport(((Arena.TeamData)var15.teams.get(var15.players.get(var2.getName()))).spawnpoint);
                     var2.sendMessage((String)this.plugin.customization.messages.get("Player-Teleport"));
                     return;
                  }

                  if (var22.equals("Special - Bridge Builder")) {
                     ArrayList var30 = new ArrayList();
                     var31 = var2.getLocation().getDirection();

                     for(var36 = 0; var36 < 32; ++var36) {
                        var30.add(var2.getLocation().add(var31.getX() * (double)var36, -1.0D, var31.getZ() * (double)var36));
                     }

                     final Iterator var40 = var30.iterator();
                     (new BukkitRunnable() {
                        public void run() {
                           if (!var40.hasNext()) {
                              this.cancel();
                           } else {
                              Location var1 = (Location)var40.next();
                              if (var1.getBlock().getType().equals(Material.AIR) && var15.cuboid.contains(var1) && var15.isPlaceable(var1.getBlock())) {
                                 var1.getBlock().setType(Material.SANDSTONE);
                                 var1.getWorld().playEffect(var1, Effect.STEP_SOUND, Material.SANDSTONE.getId());
                              }

                           }
                        }
                     }).runTaskTimer(this.plugin, 0L, 5L);
                     return;
                  }

                  if (var22.equals("Special - Rescue Platform")) {
                     byte var28 = 3;
                     List var29 = this.circle(var2.getLocation().add(0.0D, -4.0D, 0.0D), Integer.valueOf(var28), 1, true, false);
                     var10 = var29.iterator();

                     while(var10.hasNext()) {
                        Location var9 = (Location)var10.next();
                        if (var9.getBlock().getType().equals(Material.AIR) && var15.cuboid.contains(var9) && var15.isPlaceable(var9.getBlock())) {
                           var9.getBlock().setType(Material.GRASS);
                        }
                     }

                     List var35 = this.circle(var2.getLocation().add(0.0D, -5.0D, 0.0D), var28 - 1, 1, false, false);
                     Iterator var11 = var35.iterator();

                     while(var11.hasNext()) {
                        Location var41 = (Location)var11.next();
                        if (var15.cuboid.contains(var41) && var15.isPlaceable(var41.getBlock())) {
                           if (var41.getBlock().getType().equals(Material.AIR)) {
                              var41.getBlock().setType(Material.GRASS);
                           }

                           if (var41.add(0.0D, 1.0D, 0.0D).getBlock().getType().equals(Material.AIR)) {
                              var41.getBlock().setType(Material.WATER);
                           }
                        }
                     }

                     return;
                  }

                  if (var22.equals("Special - Shop")) {
                     final Villager var27 = (Villager)var2.getLocation().getWorld().spawnEntity(var2.getLocation(), EntityType.VILLAGER);
                     var27.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 999999, 10));
                     var27.setAdult();
                     var27.setCanPickupItems(false);
                     var27.setCustomName((String)this.plugin.customization.inventories.get("Villager-Shop"));
                     var27.setCustomNameVisible(true);
                     var27.setMetadata("EW_VILLAGER", new FixedMetadataValue(this.plugin, true));
                     (new BukkitRunnable() {
                        public void run() {
                           if (!var27.isDead() && var27.isValid()) {
                              var27.remove();
                           }

                        }
                     }).runTaskLater(this.plugin, 300L);
                     return;
                  }

                  if (var22.equals("Special - Boost")) {
                     short var26 = 140;
                     var2.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, var26, 1));
                     var2.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, var26, 0));
                     var2.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, var26, 0));
                     var2.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, var26, 1));
                     var2.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, var26, 1));
                     return;
                  }

                  if (var22.equals("Special - TNT")) {
                     TNTPrimed var25 = (TNTPrimed)var2.getWorld().spawn(var2.getLocation().add(0.0D, 1.0D, 0.0D), TNTPrimed.class);
                     var25.setFuseTicks(40);
                     var25.setVelocity(var2.getLocation().getDirection().add(new Vector(0.0D, 0.3D, 0.0D)).multiply(1.2D));
                     return;
                  }
               }

               return;
            }
         }

      }
   }

   @EventHandler
   public void onPlayerDeath(PlayerDeathEvent var1) {
      final Player var2 = var1.getEntity();
      if (this.plugin.players.contains(var2.getName())) {
         final PlayerData var3 = (PlayerData)this.plugin.playerData.get(var2.getName());
         final Arena var4 = var3.arena;
         if (var4 != null && !var4.state.AVAILABLE() && !this.plugin.protectedPlayers.contains(var2.getName())) {
            this.plugin.protectedPlayers.add(var2.getName());
            var2.setHealth(var2.getMaxHealth());
            var2.setFoodLevel(20);
            if (this.plugin.config.clearDroppedItemsOnDeath) {
               ArrayList var5 = new ArrayList(var1.getDrops());
               Iterator var7 = var5.iterator();

               while(var7.hasNext()) {
                  ItemStack var6 = (ItemStack)var7.next();
                  if (var6 != null && !this.plugin.config.droppedItemsOnDeathExceptions.contains(var6.getType())) {
                     var1.getDrops().remove(var6);
                  }
               }
            }

            Iterator var9 = var1.getDrops().iterator();

            while(var9.hasNext()) {
               ItemStack var8 = (ItemStack)var9.next();
               var2.getWorld().dropItemNaturally(var2.getLocation(), var8);
            }

            var1.getDrops().clear();
            this.awardKiller(var2);
            var1.setDeathMessage((String)null);
            var4.kill(var2);
            if (var4.spectators.contains(var2.getName()) && this.plugin.spectatorMode != null) {
               return;
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
               public void run() {
                  if (var2.getVehicle() != null) {
                     var2.getVehicle().eject();
                  }

                  Iterator var2x = var2.getActivePotionEffects().iterator();

                  PotionEffect var1;
                  while(var2x.hasNext()) {
                     var1 = (PotionEffect)var2x.next();
                     var2.removePotionEffect(var1.getType());
                  }

                  var2.setFireTicks(0);
                  var2.setLevel(0);
                  var2.setExp(0.0F);
                  var2.setVelocity(new Vector());
                  var2.getInventory().clear();
                  if (var4.spectators.contains(var2.getName())) {
                     var3.makeSpectator(var2);
                  } else {
                     if (EggwarsListener.this.plugin.config.respawnWithKit && EggwarsListener.this.plugin.kits.containsKey(var3.selectedKit)) {
                        ((Kit)EggwarsListener.this.plugin.kits.get(var3.selectedKit)).apply(var2);
                     }

                     var2x = var4.potionEffects.iterator();

                     while(var2x.hasNext()) {
                        var1 = (PotionEffect)var2x.next();
                        var2.addPotionEffect(var1);
                     }
                  }

               }
            }, 2L);
         } else {
            var1.getDrops().clear();
         }
      }

   }

   @EventHandler
   public void onPlayerTeleport(PlayerTeleportEvent var1) {
      Player var2 = var1.getPlayer();
      if (this.plugin.spectatorMode != null && this.plugin.players.contains(var2.getName()) && this.plugin.protectedPlayers.contains(var2.getName()) && var2.getGameMode().equals(this.plugin.spectatorMode) && !var1.getCause().equals(TeleportCause.PLUGIN) && !var1.getCause().equals(TeleportCause.COMMAND)) {
         var1.setCancelled(true);
      }

   }

   @EventHandler
   public void onInventoryClick(InventoryClickEvent var1) {
      Player var2 = (Player)var1.getWhoClicked();
      Inventory var3 = var1.getInventory();
      String var4 = var1.getView().getTitle();
      ItemStack var5 = var1.getCurrentItem();
      int var6 = var1.getRawSlot();
      ItemStack var9;
      String var10;
      int var29;
      if (var4.contains(ChatColor.DARK_RED + "Editing: ")) {
         var1.setCancelled(true);
         if (var6 >= 0 && var6 <= 53 && var5 != null && !var5.getType().equals(Material.AIR) && !this.plugin.compareItem(var5, this.plugin.pane_itemstack)) {
            Arena var17 = (Arena)this.plugin.arenas.get(ChatColor.stripColor(var4.split(": ")[1].toLowerCase()));
            if (var17 == null) {
               var2.closeInventory();
            } else {
               boolean var33;
               if (!this.plugin.compareItem(var5, this.plugin.plus_itemstack) && !this.plugin.compareItem(var5, this.plugin.minus_itemstack)) {
                  if (var5.getType().equals(Material.INK_SACK)) {
                     var33 = !ChatColor.stripColor(var3.getItem(40).getItemMeta().getDisplayName().toLowerCase()).equals("enabled");
                     var3.setItem(40, (new ItemStackBuilder(Material.INK_SACK)).setName(var33 ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled").setDurability(var33 ? 10 : 8).build());
                  } else {
                     if (this.plugin.compareItem(var5, this.plugin.save_itemstack)) {
                        long var35 = System.currentTimeMillis();
                        HashMap var36 = new HashMap();

                        for(var29 = 9; var29 < 13; ++var29) {
                           var36.put(ChatColor.stripColor(var3.getItem(var29).getItemMeta().getDisplayName()).split(": ")[0].toLowerCase().replace(" ", "-"), Integer.valueOf(ChatColor.stripColor(var3.getItem(var29).getItemMeta().getDisplayName()).split(": ")[1]));
                        }

                        if ((Integer)var36.get("min-teams") < 2) {
                           var2.sendMessage(this.plugin.customization.prefix + "Min teams must be at least 2!");
                           return;
                        }

                        boolean var40 = ChatColor.stripColor(var3.getItem(40).getItemMeta().getDisplayName().toLowerCase()).equals("enabled");
                        var17.enabled = var40;
                        var17.teamSize = (Integer)var36.get("team-size");
                        var17.minTeams = (Integer)var36.get("min-teams");
                        var17.lobbyCountdown = (Integer)var36.get("lobby-countdown");
                        var17.gameLength = (Integer)var36.get("game-length");
                        var17.scoreboard.update((String)this.plugin.customization.scoreboard.get("Mode"), (String)this.plugin.customization.scoreboard.get("Mode") + ChatColor.WHITE + " " + var17.getMode(), false);
                        File var41 = new File(this.plugin.getDataFolder() + "/arenas/" + var17.name, "settings.yml");
                        YamlConfiguration var42 = YamlConfiguration.loadConfiguration(var41);
                        Iterator var45 = var36.keySet().iterator();

                        while(var45.hasNext()) {
                           String var44 = (String)var45.next();
                           var42.set(var44, var36.get(var44));
                        }

                        var42.set("enabled", var40);

                        try {
                           var42.save(var41);
                        } catch (IOException var16) {
                           var16.printStackTrace();
                        }

                        var17.minPlayersForCountdownShorten = var42.getInt("lobby-countdown-shortening.min-teams") * var17.teamSize;
                        var17.stop(true);
                        var2.sendMessage(this.plugin.customization.prefix + "Your settings have been saved & applied! took " + ChatColor.LIGHT_PURPLE + (System.currentTimeMillis() - var35) + "ms!");
                        var2.closeInventory();
                     }

                  }
               } else {
                  var33 = this.plugin.compareItem(var5, this.plugin.plus_itemstack);
                  var9 = var3.getItem(var33 ? var1.getSlot() + 9 : var1.getSlot() - 9);
                  var10 = ChatColor.stripColor(var9.getItemMeta().getDisplayName()).split(": ")[0];
                  var29 = Integer.valueOf(ChatColor.stripColor(var9.getItemMeta().getDisplayName()).split(": ")[1]) + (var33 ? 1 : -1);
                  if (var29 >= 1) {
                     (new ItemStackBuilder(var9)).setName(ChatColor.YELLOW + var10 + ": " + ChatColor.GOLD + var29).build();
                  }
               }
            }
         }
      } else {
         if (this.plugin.players.contains(var2.getName())) {
            PlayerData var7;
            int var12;
            int var13;
            if (this.plugin.protectedPlayers.contains(var2.getName())) {
               var1.setCancelled(true);
               if (var6 >= 0 && var6 <= var1.getInventory().getSize() - 1 && var5 != null && !var5.getType().equals(Material.AIR) && !this.plugin.compareItem(var5, this.plugin.pane_itemstack)) {
                  if (var4.equals(this.plugin.customization.inventories.get("Quit-Inventory"))) {
                     if (var5.getDurability() == 5) {
                        this.plugin.leave(var2, false);
                     } else if (var5.getDurability() == 14) {
                        var2.closeInventory();
                     }

                     return;
                  }

                  var7 = (PlayerData)this.plugin.playerData.get(var2.getName());
                  if (var4.equals(this.plugin.customization.inventories.get("Profile-Inventory"))) {
                     if (var7.hasCooldown(var2, "PROFILE_CLICK", 3)) {
                        return;
                     }

                     if (this.plugin.compareItem(var5, this.plugin.stats_itemstack)) {
                        var2.openInventory(var7.getStatsInventory(var2));
                        return;
                     }

                     if (this.plugin.compareItem(var5, this.plugin.inventory_itemstack)) {
                        var7.inventory.open(var2);
                        return;
                     }

                     if (this.plugin.compareItem(var5, this.plugin.achievements_itemstack)) {
                        var7.achievements.open(var2);
                        return;
                     }

                     return;
                  }

                  if (var4.equals(this.plugin.customization.inventories.get("Team-Selector"))) {
                     if (var7.arena == null || var7.arena != null && !var7.arena.state.AVAILABLE()) {
                        var2.closeInventory();
                        return;
                     }

                     if (var7.hasCooldown(var2, "TEAM_SELECT", 3)) {
                        return;
                     }

                     var7.arena.joinTeam(var2, var7.arena.getTeam(ChatColor.stripColor(var5.getItemMeta().getDisplayName().split(" ")[0])));
                     return;
                  }

                  String var18;
                  String var28;
                  if (var4.contains((CharSequence)this.plugin.customization.inventories.get("Arena-Settings"))) {
                     if (var7.arena != null && (var7.arena == null || var7.arena.state.AVAILABLE())) {
                        if (var4.contains((String)this.plugin.customization.inventories.get("Arena-Settings") + ": ")) {
                           if (this.plugin.compareItem(var5, this.plugin.back_itemstack)) {
                              var2.openInventory(this.plugin.votingOptions);
                              return;
                           }

                           if (var7.hasCooldown(var2, "SETTINGS_VOTE", 3)) {
                              return;
                           }

                           var18 = ChatColor.stripColor(var4.split(": ")[1]);
                           var28 = ChatColor.stripColor(var5.getItemMeta().getDisplayName());
                           var7.arena.votesManager.vote(var2, var18, var28);
                           var10 = ((String)this.plugin.customization.messages.get("Player-Vote")).replace("%player%", var2.getName()).replace("%option%", var18 + ": " + var28).replace("%votes%", String.valueOf(var7.arena.votesManager.getVotes(var18, var28)));
                           Iterator var38 = var7.arena.getPlayers().iterator();

                           while(var38.hasNext()) {
                              Player var34 = (Player)var38.next();
                              var34.sendMessage(var10);
                           }
                        } else {
                           var18 = ChatColor.stripColor(var5.getItemMeta().getDisplayName());
                           if ((!var18.equals("Time") || var2.hasPermission("eggwars.vote.time")) && (!var18.equals("Items") || var2.hasPermission("eggwars.vote.items")) && (!var18.equals("Health") || var2.hasPermission("eggwars.vote.health"))) {
                              var7.arena.votesManager.openEntry(var2, var18);
                           } else {
                              var2.sendMessage((String)this.plugin.customization.messages.get("No-Permission"));
                           }
                        }

                        return;
                     }

                     var2.closeInventory();
                     return;
                  }

                  Inventory var25;
                  if (var4.contains(var7.inventory.getName())) {
                     if (var7.inventory.handleClick(var2, var5, var3)) {
                        return;
                     }

                     if (this.plugin.compareItem(var5, this.plugin.back_itemstack)) {
                        var2.openInventory(this.plugin.profileInventory);
                        return;
                     }

                     if (var1.getAction().equals(InventoryAction.PICKUP_HALF)) {
                        var25 = Bukkit.createInventory(var2, 9, ChatColor.RED + "Are you sure about this?");
                        var25.setItem(2, this.plugin.confirm_itemstack);
                        var25.setItem(4, var5);
                        var25.setItem(6, this.plugin.cancel_itemstack);
                        var2.openInventory(var25);
                     } else {
                        if (var7.hasCooldown(var2, "ITEM_EQUIP", 3)) {
                           return;
                        }

                        var18 = ChatColor.stripColor(var5.getItemMeta().getDisplayName().toLowerCase());
                        if (this.plugin.kits.containsKey(var18)) {
                           var7.selectedKit = var18;
                           if (var7.lobbyScoreboard != null) {
                              var7.lobbyScoreboard.update((String)this.plugin.customization.scoreboard.get("Kit"), ((Kit)this.plugin.kits.get(var18)).name, true);
                           }

                           var2.sendMessage(((String)this.plugin.customization.messages.get("Item-Select")).replace("%item%", ((Kit)this.plugin.kits.get(var18)).name));
                        } else if (this.plugin.trails.containsKey(var18)) {
                           var7.selectedTrail = var18;
                           if (var7.lobbyScoreboard != null) {
                              var7.lobbyScoreboard.update((String)this.plugin.customization.scoreboard.get("Trail"), ((Trail)this.plugin.trails.get(var18)).name, true);
                           }

                           var2.sendMessage(((String)this.plugin.customization.messages.get("Item-Select")).replace("%item%", ((Trail)this.plugin.trails.get(var18)).name));
                        }
                     }

                     return;
                  }

                  if (var4.contains(var7.achievements.getName())) {
                     if (var7.achievements.handleClick(var2, var5, var3)) {
                        return;
                     }

                     if (this.plugin.compareItem(var5, this.plugin.back_itemstack)) {
                        var2.openInventory(this.plugin.profileInventory);
                        return;
                     }

                     return;
                  }

                  if (var4.equals(this.plugin.customization.inventories.get("Stats-Inventory"))) {
                     if (this.plugin.compareItem(var5, this.plugin.back_itemstack)) {
                        var2.openInventory(this.plugin.profileInventory);
                        return;
                     }

                     return;
                  }

                  if (var4.equals(ChatColor.RED + "Are you sure about this?")) {
                     if (this.plugin.compareItem(var5, this.plugin.cancel_itemstack)) {
                        var7.inventory.open(var2);
                        return;
                     }

                     if (!this.plugin.compareItem(var5, this.plugin.confirm_itemstack)) {
                        return;
                     }

                     var18 = ChatColor.stripColor(var3.getItem(4).getItemMeta().getDisplayName().toLowerCase());
                     Object var30 = this.plugin.kits.containsKey(var18) ? (Sellable)this.plugin.kits.get(var18) : (this.plugin.trails.containsKey(var18) ? (Trail)this.plugin.trails.get(var18) : null);
                     if (var30 == null) {
                        var2.closeInventory();
                        var2.sendMessage((String)this.plugin.customization.messages.get("Invalid-Item"));
                     }

                     int var31 = this.plugin.kits.containsKey(var18) ? 0 : 1;
                     int[] var43;
                     var13 = (var43 = this.plugin.smartSlots).length;

                     for(var12 = 0; var12 < var13; ++var12) {
                        var29 = var43[var12];
                        ItemStack var15 = var7.inventory.getItem(var31, var29);
                        if (var15 != null && this.plugin.compareItem(var15, var3.getItem(4))) {
                           var7.inventory.removeItem(var31, var29);
                           break;
                        }
                     }

                     if (var7.selectedKit.equals(var18)) {
                        var7.selectedKit = "";
                     } else if (var7.selectedTrail.equals(var18)) {
                        var7.selectedTrail = "";
                     }

                     var7.inventory.open(var2);
                     var29 = (int)((double)((Sellable)var30).value * this.plugin.config.selling_value);
                     var7.addCoins(var2, var29);
                     if (var7.lobbyScoreboard != null) {
                        var7.createScoreboard(var2);
                     }

                     var2.sendMessage(((String)this.plugin.customization.messages.get("Item-Sell")).replace("%value%", String.valueOf(var29)));
                     return;
                  }

                  if (var4.contains(this.plugin.arenaSelector.getName())) {
                     if (this.plugin.arenaSelector.handleClick(var2, var5, var3)) {
                        return;
                     }

                     if (var7.hasCooldown(var2, "ARENA_SELECT", 3)) {
                        return;
                     }

                     if (!this.plugin.compareItem(var5, this.plugin.autojoin_itemstack)) {
                        var18 = ChatColor.stripColor(var5.getItemMeta().getDisplayName().toLowerCase());
                        if (this.plugin.arenas.containsKey(var18)) {
                           ((Arena)this.plugin.arenas.get(var18)).join(var2);
                        } else {
                           var2.sendMessage((String)this.plugin.customization.messages.get("Invalid-Arena"));
                           var2.closeInventory();
                        }

                        return;
                     }

                     this.plugin.autojoin(var2, var7.party != null && var7.party.players.size() > 1 ? "TEAM" : "");
                     return;
                  }

                  if (var4.equals(ChatColor.BLUE + "Mystery Box:")) {
                     if (var7.arena != null && !var7.arena.state.AVAILABLE()) {
                        var2.closeInventory();
                        return;
                     }

                     if (this.plugin.compareItem(var5, this.plugin.cancel_itemstack)) {
                        var2.closeInventory();
                        return;
                     }

                     if (this.plugin.compareItem(var5, this.plugin.confirm_itemstack)) {
                        this.plugin.mysteryBox.open(var2);
                        return;
                     }

                     return;
                  }

                  if (var4.contains(this.plugin.shop.getName())) {
                     if (this.plugin.shop.handleClick(var2, var5, var3)) {
                        return;
                     }

                     var25 = Bukkit.createInventory(var2, 9, (String)this.plugin.customization.inventories.get("Confirm-Purchase"));
                     var25.setItem(1, this.plugin.confirm_itemstack);
                     var25.setItem(4, var5);
                     var25.setItem(7, this.plugin.cancel_itemstack);
                     var2.openInventory(var25);
                     return;
                  }

                  if (var4.equals(this.plugin.customization.inventories.get("Confirm-Purchase"))) {
                     if (this.plugin.compareItem(var5, this.plugin.confirm_itemstack)) {
                        ItemStack var24 = var3.getItem(4);
                        var28 = ChatColor.stripColor(var24.getItemMeta().getDisplayName().toLowerCase());
                        byte var32;
                        if (this.plugin.kits.containsKey(var28)) {
                           var29 = ((Kit)this.plugin.kits.get(var28)).value;
                           var10 = ((Kit)this.plugin.kits.get(var28)).permission;
                           var32 = 0;
                        } else {
                           if (!this.plugin.trails.containsKey(var28)) {
                              var2.sendMessage((String)this.plugin.customization.messages.get("Invalid-Item"));
                              var2.closeInventory();
                              return;
                           }

                           var29 = ((Trail)this.plugin.trails.get(var28)).value;
                           var10 = ((Trail)this.plugin.trails.get(var28)).permission;
                           var32 = 1;
                        }

                        if (!var10.isEmpty() && !var2.hasPermission(var10)) {
                           var2.sendMessage((String)this.plugin.customization.messages.get("No-Permission"));
                           return;
                        }

                        ItemStack var39 = (new ItemStackBuilder(var24.clone())).addLore(" ", (String)this.plugin.customization.lores.get("Inventory-Instructions")).build();
                        if (var7.getCoins(var2) < var29) {
                           var2.sendMessage((String)this.plugin.customization.messages.get("Not-Enough-Coins"));
                           var2.closeInventory();
                           return;
                        }

                        if (var7.inventory.getContents(var32).contains(var39)) {
                           var2.sendMessage((String)this.plugin.customization.messages.get("Item-Owned"));
                           var2.closeInventory();
                           return;
                        }

                        if (var7.inventory.addItem(var32, var39)) {
                           var7.removeCoins(var2, var29);
                           if (var7.lobbyScoreboard != null) {
                              var7.lobbyScoreboard.update((String)this.plugin.customization.scoreboard.get("Coins"), var7.getCoins(var2), true);
                           }

                           var2.sendMessage((String)this.plugin.customization.messages.get("Item-Purchase"));
                        } else {
                           var2.sendMessage((String)this.plugin.customization.messages.get("Not-Enough-Space"));
                        }

                        var2.closeInventory();
                        return;
                     }

                     if (this.plugin.compareItem(var5, this.plugin.cancel_itemstack)) {
                        this.plugin.shop.open(var2);
                        return;
                     }

                     return;
                  }

                  Player var20;
                  if (this.plugin.lobbyPlayers.contains(var2.getName())) {
                     Iterator var26;
                     if (var4.equals(ChatColor.RED + "Select your option!")) {
                        if (var5.getType().equals(Material.BEACON)) {
                           if (var7.hasCooldown(var2, "PARTY_CREATE", 15)) {
                              return;
                           }

                           int var21 = this.plugin.config.party_default_slots;
                           var26 = this.plugin.config.party_custom_slots.keySet().iterator();

                           while(var26.hasNext()) {
                              int var27 = (Integer)var26.next();
                              if (var27 > var21 && var2.hasPermission((String)this.plugin.config.party_custom_slots.get(var27))) {
                                 var21 = var27;
                              }
                           }

                           var7.party = new Party(this.plugin, var2, var21);
                           this.plugin.parties.add(var7.party);
                           this.plugin.updatePartiesInventory();
                           var2.openInventory(var7.party.gui);
                           return;
                        }

                        if (var5.getType().equals(Material.CHEST)) {
                           this.plugin.partySelector.open(var2);
                           return;
                        }

                        return;
                     }

                     if (var4.equals(this.plugin.customization.inventories.get("Party-Settings"))) {
                        if (var7.hasCooldown(var2, "PARTY_MODIFY", 2)) {
                           return;
                        }

                        if (var5.getType().equals(Material.EYE_OF_ENDER)) {
                           if (var7.party.leaderName.equals(var2.getName())) {
                              var7.party.setPrivacy(var7.party.privacy == Enums.PartyPrivacy.INVITE ? Enums.PartyPrivacy.PUBLIC : Enums.PartyPrivacy.INVITE);
                              var7.party.updateItem();
                           } else {
                              var2.sendMessage((String)this.plugin.customization.messages.get("Party-Must-Be-Leader"));
                           }

                           return;
                        }

                        if (var5.getType().equals(Material.CHEST)) {
                           var2.openInventory(var7.party.playersInventory);
                           return;
                        }

                        if (var5.getType().equals(Material.ENDER_CHEST)) {
                           if (var7.party.leaderName.equals(var2.getName())) {
                              this.plugin.playerInviter.open(var2);
                           } else {
                              var2.sendMessage((String)this.plugin.customization.messages.get("Party-Must-Be-Leader"));
                           }

                           return;
                        }

                        if (var5.getType().equals(Material.TNT)) {
                           var7.party.leave(var2);
                           return;
                        }

                        return;
                     }

                     if (var4.equals(this.plugin.customization.inventories.get("Party-Players"))) {
                        if (this.plugin.compareItem(var5, this.plugin.back_itemstack)) {
                           var2.openInventory(var7.party.gui);
                           return;
                        }

                        if (!var7.party.leaderName.equals(var2.getName())) {
                           var2.sendMessage((String)this.plugin.customization.messages.get("Party-Must-Be-Leader"));
                           return;
                        }

                        var18 = ChatColor.stripColor(var5.getItemMeta().getDisplayName());
                        if (var18.equals(var2.getName())) {
                           return;
                        }

                        var20 = Bukkit.getPlayer(var18);
                        if (var20 == null) {
                           var2.sendMessage((String)this.plugin.customization.messages.get("Unknown-Player"));
                           return;
                        }

                        if (var1.getAction().equals(InventoryAction.PICKUP_HALF)) {
                           if (var7.party.players.contains(var18)) {
                              var7.party.kick(var20);
                           } else {
                              var2.sendMessage((String)this.plugin.customization.messages.get("Party-Unknown-Player"));
                           }
                        } else {
                           var7.party.setLeader(var20);
                           var7.party.sendMessage(((String)this.plugin.customization.messages.get("Party-New-Leader")).replace("%player%", var20.getName()));
                           var7.party.updateItem();
                           var7.party.updatePlayers();
                        }

                        return;
                     }

                     if (!var4.contains(this.plugin.partySelector.getName())) {
                        if (var4.contains(this.plugin.playerInviter.getName())) {
                           if (this.plugin.playerInviter.handleClick(var2, var5, var3)) {
                              return;
                           }

                           if (this.plugin.compareItem(var5, this.plugin.back_itemstack)) {
                              var2.openInventory(var7.party.gui);
                              return;
                           }

                           if (var7.hasCooldown(var2, "PARTY_INVITE", 3)) {
                              return;
                           }

                           var18 = ChatColor.stripColor(var5.getItemMeta().getDisplayName());
                           var20 = Bukkit.getPlayer(var18);
                           if (var20 == null) {
                              var2.sendMessage((String)this.plugin.customization.messages.get("Unknown-Player"));
                              return;
                           }

                           if (var20.getName().equals(var2.getName())) {
                              var2.sendMessage((String)this.plugin.customization.messages.get("Party-Self-Invite"));
                              return;
                           }

                           if (!var7.party.invited.contains(var20.getName()) && !var7.party.players.contains(var20.getName())) {
                              var7.party.invite(var20);
                              return;
                           }

                           var2.sendMessage((String)this.plugin.customization.messages.get("Player-Already-Invited"));
                           return;
                        }

                        return;
                     }

                     if (this.plugin.partySelector.handleClick(var2, var5, var3)) {
                        return;
                     }

                     if (this.plugin.compareItem(var5, this.plugin.back_itemstack)) {
                        var2.openInventory(this.plugin.partyMenu);
                        return;
                     }

                     if (var7.hasCooldown(var2, "PARTY_JOIN", 3)) {
                        return;
                     }

                     var18 = ChatColor.stripColor(var5.getItemMeta().getDisplayName().replace("'s party", ""));
                     var26 = this.plugin.parties.iterator();

                     while(var26.hasNext()) {
                        Party var22 = (Party)var26.next();
                        if (var22.leaderName.equals(var18)) {
                           var22.join(var2);
                           return;
                        }
                     }

                     var2.closeInventory();
                     var2.sendMessage((String)this.plugin.customization.messages.get("Invalid-Party"));
                     return;
                  }

                  if (var4.equals(this.plugin.customization.inventories.get("Teleporter"))) {
                     if (var7.arena != null && var7.arena.spectators.contains(var2.getName())) {
                        var18 = ChatColor.stripColor(var5.getItemMeta().getDisplayName());
                        var20 = Bukkit.getPlayer(var18);
                        if (var20 == null) {
                           var2.sendMessage((String)this.plugin.customization.messages.get("Unknown-Player"));
                           var2.closeInventory();
                           return;
                        }

                        if (!var7.arena.players.containsKey(var20.getName()) || var7.arena.spectators.contains(var20.getName())) {
                           var2.sendMessage((String)this.plugin.customization.messages.get("Player-Inactive"));
                           var2.closeInventory();
                           return;
                        }

                        var2.teleport(var20.getLocation().add(0.0D, 3.0D, 0.0D));
                     } else {
                        var2.closeInventory();
                     }

                     return;
                  }

                  if (!var4.equals(this.plugin.customization.inventories.get("Spectator-Menu"))) {
                     return;
                  }

                  if (var7.arena != null && var7.arena.spectators.contains(var2.getName())) {
                     if (this.plugin.compareItem(var5, this.plugin.play_itemstack)) {
                        this.plugin.arenaSelector.open(var2);
                        return;
                     }

                     if (this.plugin.compareItem(var5, this.plugin.teleporter_itemstack)) {
                        if (var7.hasCooldown(var2, "TELEPORT_GUI", 5)) {
                           return;
                        }

                        var2.openInventory(var7.arena.getTeleporterInventory());
                        return;
                     }

                     if (this.plugin.compareItem(var5, this.plugin.profile_itemstack)) {
                        var2.openInventory(this.plugin.profileInventory);
                        return;
                     }

                     if (this.plugin.compareItem(var5, this.plugin.quit_itemstack)) {
                        var2.openInventory(this.plugin.quitInventory);
                        return;
                     }
                  } else {
                     var2.closeInventory();
                  }

                  return;
               }

               return;
            }

            var7 = (PlayerData)this.plugin.playerData.get(var2.getName());
            Arena var8 = var7.arena;
            if (var8 != null && !var8.state.AVAILABLE() && !var8.spectators.contains(var2.getName())) {
               if (var4.equals(this.plugin.customization.inventories.get("Spawner-Upgrade"))) {
                  if (var6 < var3.getSize()) {
                     var1.setCancelled(true);
                  }

                  if (var6 >= 0 && var6 <= var3.getSize() - 1 && var5 != null && !var5.getType().equals(Material.AIR) && !this.plugin.compareItem(var5, this.plugin.pane_itemstack)) {
                     if (var5.getType().equals(Material.EXP_BOTTLE)) {
                        Spawner var19 = var8.getSpawner(var3);
                        if (var19 != null) {
                           if (var19.canUpgrade()) {
                              ItemStack var23 = var19.getUpgradeItem();
                              if (this.plugin.hasItem(var2, var23, false)) {
                                 this.plugin.removeItem(var2, var23, false);
                                 var19.upgrade();
                                 this.plugin.fireWorkEffect(var19.spawningLocation, true);
                                 var2.sendMessage((String)this.plugin.customization.messages.get("Spawner-Upgrade"));
                              } else {
                                 var2.sendMessage(((String)this.plugin.customization.messages.get("Spawner-Upgrade-Cost-Error")).replace("%cost%", var23.getAmount() + " " + var23.getType().name() + "(s)"));
                              }
                           } else {
                              var2.sendMessage((String)this.plugin.customization.messages.get("Spawner-Upgrade-Deny"));
                           }
                        } else {
                           var2.sendMessage((String)this.plugin.customization.messages.get("Spawner-Upgrade-Error"));
                           var2.closeInventory();
                        }
                     }

                     return;
                  }

                  return;
               }

               if (var4.contains((CharSequence)this.plugin.customization.inventories.get("Villager-Shop"))) {
                  if (var6 < var3.getSize()) {
                     var1.setCancelled(true);
                  }

                  if (var6 >= 0 && var6 <= var3.getSize() - 1 && var5 != null && !var5.getType().equals(Material.AIR) && !this.plugin.compareItem(var5, this.plugin.pane_itemstack)) {
                     if (var4.contains((String)this.plugin.customization.inventories.get("Villager-Shop") + ": ")) {
                        if (this.plugin.compareItem(var5, this.plugin.back_itemstack)) {
                           this.plugin.villagerShop.openShop(var2, var8.votedShop);
                           return;
                        }

                        var9 = (new ItemStackBuilder(var5.clone())).removeLastLore().removeLastLore().build();
                        var10 = ChatColor.stripColor((new ItemStackBuilder(var5)).getLastLore().split(": ")[1]);
                        ItemStack var11 = new ItemStack(Material.getMaterial(var10.split(" ")[1].replace("(S)", "")), Integer.valueOf(var10.split(" ")[0]));
                        if (this.plugin.hasSpace(var2, var9)) {
                           var12 = this.plugin.getAmount(var2, var11, false);
                           if (var12 >= var11.getAmount()) {
                              if (var1.isShiftClick()) {
                                 var13 = var11.getAmount();

                                 int var14;
                                 for(var14 = var9.getAmount(); var12 - var11.getAmount() >= var13; var14 += var9.getAmount()) {
                                    var13 += var11.getAmount();
                                 }

                                 (new ItemStackBuilder(var11)).setAmount(var13).build();
                                 (new ItemStackBuilder(var9)).setAmount(var14).build();
                              }

                              this.plugin.removeItem(var2, var11, false);
                              var2.playSound(var2.getLocation(), this.plugin.ITEM_PICKUP, 1.0F, 1.0F);
                              if (var9.getType().equals(Material.WOOL)) {
                                 this.plugin.addItem(var2, (new ItemStackBuilder(Material.WOOL)).setAmount(var9.getAmount()).setDurability((short)this.plugin.filesManager.translateColorToDurability(((Team)var8.players.get(var2.getName())).getName())).build());
                                 return;
                              }

                              if (var9.getType().name().contains("LEATHER_")) {
                                 LeatherArmorMeta var37 = (LeatherArmorMeta)var9.getItemMeta();
                                 var37.setColor(this.plugin.filesManager.translateChatColorToColor(ChatColor.valueOf(((Team)var8.players.get(var2.getName())).getName())));
                                 var9.setItemMeta(var37);
                              }

                              var2.getInventory().addItem(new ItemStack[]{var9});
                           } else {
                              var2.sendMessage(((String)this.plugin.customization.messages.get("Villager-Shop-No-Required-Item")).replace("%cost%", var10));
                           }
                        } else {
                           var2.sendMessage((String)this.plugin.customization.messages.get("Not-Enough-Space"));
                        }
                     } else {
                        this.plugin.villagerShop.openCategory(var2, var8.votedShop, var6);
                     }

                     return;
                  }

                  return;
               }
            }
         }

      }
   }

   @EventHandler
   public void onEntityDamageByEntity(EntityDamageByEntityEvent var1) {
      if (!var1.isCancelled()) {
         final Player var2 = null;
         if (var1.getDamager().getType().equals(EntityType.PLAYER)) {
            var2 = (Player)var1.getDamager();
            Arena var3 = ((PlayerData)this.plugin.playerData.get(var2.getName())).arena;
            if (this.plugin.protectedPlayers.contains(var2.getName()) && (var3 == null || var3.spectators.contains(var2.getName()))) {
               var1.setCancelled(true);
               return;
            }
         }

         if (var1.getEntityType().equals(EntityType.PLAYER) || var1.getEntity().hasMetadata("EW_MOB")) {
            LivingEntity var8;
            if (var2 == null) {
               if (var1.getDamager() instanceof Projectile && ((Projectile)var1.getDamager()).getShooter() instanceof Player) {
                  var2 = (Player)((Projectile)var1.getDamager()).getShooter();
               } else if (var1.getDamager().hasMetadata("EW_MOB")) {
                  var8 = (LivingEntity)var1.getDamager();
                  String var4 = ChatColor.stripColor(var8.getCustomName().split("'s")[0]);
                  var2 = Bukkit.getPlayer(var4);
               }
            }

            if (var1.getEntityType().equals(EntityType.PLAYER)) {
               final Player var9 = (Player)var1.getEntity();
               PlayerData var10 = (PlayerData)this.plugin.playerData.get(var9.getName());
               if (var10 == null) {
                  return;
               }

               Arena var5 = var10.arena;
               if (var5 != null) {
                  if (var1.getDamager().getType().equals(EntityType.FIREWORK)) {
                     var1.setCancelled(true);
                     return;
                  }

                  if (var2 == null) {
                     return;
                  }

                  if (var5.players.containsKey(var2.getName())) {
                     if (var1.getDamager() instanceof Projectile) {
                        PlayerData var6 = (PlayerData)this.plugin.playerData.get(var2.getName());
                        if (var6 == null) {
                           return;
                        }

                        ++var6.projectiles_hit;
                        this.plugin.achievementsManager.checkPlayer(var2, Enums.AchievementType.PROJECTILES_HIT, var6.projectiles_hit);
                        if (var1.getDamager().getType().equals(EntityType.ARROW) && this.plugin.config.showHealthOnBowHit) {
                           Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
                              public void run() {
                                 var2.sendMessage(((String)EggwarsListener.this.plugin.customization.messages.get("Arrow-Hit")).replace("%player%", var9.getName()).replace("%health%", (new BigDecimal(var9.getHealth())).setScale(1, RoundingMode.HALF_UP).toString()).replace("%heart%", Enums.SPECIAL_CHARACTER.HEART.toString()));
                              }
                           }, 1L);
                        }
                     }

                     var10.lastHit = var2.getName();
                     var10.lastHitTime = System.currentTimeMillis();
                  }
               }
            } else if (var2 != null) {
               var8 = (LivingEntity)var1.getEntity();
               if (ChatColor.stripColor(var8.getCustomName().split("'s")[0]).equals(var2.getName())) {
                  var1.setCancelled(true);
                  return;
               }
            }
         }

      }
   }

   @EventHandler
   public void onEntityDamage(EntityDamageEvent var1) {
      if (var1.getEntityType().equals(EntityType.PLAYER)) {
         Player var2 = (Player)var1.getEntity();
         PlayerData var3 = (PlayerData)this.plugin.playerData.get(var2.getName());
         if (var3 == null) {
            return;
         }

         if (this.plugin.protectedPlayers.contains(var2.getName())) {
            var1.setCancelled(true);
            if (var3.arena != null) {
               if (var1.getCause().equals(DamageCause.VOID) && var3.arena.state.AVAILABLE()) {
                  var2.teleport(var3.arena.lobby);
               } else if (var3.arena.spectators.contains(var2.getName())) {
                  if (var1.getCause().equals(DamageCause.VOID)) {
                     var2.teleport(var3.arena.cuboid.getRandomLocation());
                  } else if (var1.getCause().equals(DamageCause.ENTITY_ATTACK) || var1.getCause().equals(DamageCause.ENTITY_EXPLOSION) || var1.getCause().equals(DamageCause.MAGIC) || var1.getCause().equals(DamageCause.PROJECTILE)) {
                     var3.arena.sendWarning(var2);
                  }
               }
            }
         } else if (var3.arena != null && var1.getCause().equals(DamageCause.VOID) && this.plugin.config.voidInstantDeath) {
            var1.setDamage(1000.0D);
         }
      } else if (var1.getEntityType().equals(EntityType.VILLAGER) && var1.getEntity().hasMetadata("EW_VILLAGER")) {
         var1.setCancelled(true);
      }

   }

   @EventHandler
   public void onBlockCanBuild(BlockCanBuildEvent var1) {
      if (!var1.isBuildable() && this.plugin.spectatorMode == null) {
         Block var2 = var1.getBlock();
         Iterator var4 = this.plugin.arenas.values().iterator();

         while(var4.hasNext()) {
            Arena var3 = (Arena)var4.next();
            if (var3.cuboid.contains(var2.getLocation())) {
               Iterator var6 = this.plugin.getPlayers(var3.spectators).iterator();

               Player var5;
               do {
                  if (!var6.hasNext()) {
                     return;
                  }

                  var5 = (Player)var6.next();
               } while(!var5.getWorld().getName().equals(var2.getWorld().getName()) || var5.getLocation().distance(var2.getLocation()) >= 6.0D);

               var3.sendWarning(var5);
               var1.setBuildable(true);
               break;
            }
         }

      }
   }

   @EventHandler
   public void onBlockPlace(BlockPlaceEvent var1) {
      Player var2 = var1.getPlayer();
      if (!var1.isCancelled()) {
         if (this.plugin.players.contains(var2.getName())) {
            if (this.plugin.protectedPlayers.contains(var2.getName())) {
               var1.setCancelled(true);
               return;
            }

            PlayerData var3 = (PlayerData)this.plugin.playerData.get(var2.getName());
            Arena var4 = var3.arena;
            if (var4 == null) {
               return;
            }

            if (!var4.cuboid.contains(var1.getBlock().getLocation())) {
               var1.setCancelled(true);
               Location var5 = var1.getBlockAgainst().getLocation().clone().add(0.5D, 1.0D, 0.5D);
               var5.setPitch(var2.getLocation().getPitch());
               var5.setYaw(var2.getLocation().getYaw());

               while(var5.getBlock().getType() != Material.AIR) {
                  var5.add(0.0D, 1.0D, 0.0D);
               }

               var2.teleport(var5);
            } else {
               if (!var4.isPlaceable(var1.getBlock())) {
                  var2.sendMessage((String)this.plugin.customization.messages.get("Build-Error"));
                  var1.setCancelled(true);
                  return;
               }

               if (this.plugin.config.buildingSoundEnabled) {
                  var2.playSound(var2.getLocation(), this.plugin.ITEM_PICKUP, 1.0F, 1.0F);
               }

               ++var3.blocks_placed;
               this.plugin.achievementsManager.checkPlayer(var2, Enums.AchievementType.BLOCKS_PLACED, var3.blocks_placed);
            }
         }

      }
   }

   @EventHandler
   public void onBlockBreak(BlockBreakEvent var1) {
      Player var2 = var1.getPlayer();
      if (!var1.isCancelled()) {
         Block var3 = var1.getBlock();
         if (var3.getState() instanceof Sign) {
            Sign var4 = (Sign)var3.getState();
            if (var4.getLine(0).equals(this.plugin.customization.signs_title) || var4.getLine(0).startsWith(ChatColor.AQUA + "Top #" + ChatColor.RED)) {
               if (!var2.hasPermission("eggwars.breaksigns")) {
                  var2.sendMessage((String)this.plugin.customization.messages.get("No-Permission"));
                  var1.setCancelled(true);
                  return;
               }

               if (var4.getLine(2).equals(ChatColor.BOLD + "Generator")) {
                  var1.setCancelled(true);
                  return;
               }

               int var5;
               if (var4.getLine(1).equals(this.plugin.customization.signs_join)) {
                  if (this.plugin.joinSigns.containsKey(var4.getLocation())) {
                     var5 = (Integer)this.plugin.joinSigns.get(var4.getLocation());
                     this.plugin.filesManager.getConfig("signs.yml").set("Signs.Join." + var5, (Object)null);
                     this.plugin.filesManager.saveConfig("signs.yml");
                     this.plugin.joinSigns.remove(var4.getLocation());
                     var2.sendMessage(this.plugin.customization.prefix + "You have " + ChatColor.RED + "removed" + ChatColor.GRAY + " the join sign with the id #" + ChatColor.LIGHT_PURPLE + var5);
                  } else {
                     String var14 = ChatColor.stripColor(var4.getLine(2)).toLowerCase();
                     Arena var15 = (Arena)this.plugin.arenas.get(var14);
                     if (var15 != null && var15.signs.containsKey(var4.getLocation())) {
                        int var16 = (Integer)var15.signs.get(var4.getLocation());
                        File var17 = new File(this.plugin.getDataFolder() + "/arenas/" + var15.name, "locations.dat");
                        YamlConfiguration var9 = YamlConfiguration.loadConfiguration(var17);
                        var9.set("Signs." + var16, (Object)null);

                        try {
                           var9.save(var17);
                        } catch (IOException var11) {
                           var11.printStackTrace();
                        }

                        var15.signs.remove(var4.getLocation());
                        var2.sendMessage(this.plugin.customization.prefix + "You have " + ChatColor.RED + "removed" + ChatColor.GRAY + " the join sign with the id #" + ChatColor.LIGHT_PURPLE + var16);
                     }
                  }

                  return;
               }

               if (this.plugin.topSigns.containsKey(var4.getLocation())) {
                  var5 = (Integer)this.plugin.topSigns.get(var4.getLocation());
                  this.plugin.filesManager.getConfig("signs.yml").set("Signs.Top." + var5, (Object)null);
                  this.plugin.filesManager.saveConfig("signs.yml");
                  this.plugin.topSigns.remove(var4.getLocation());
                  if (this.plugin.topSigns.isEmpty() && this.plugin.leaderboard_updater != null) {
                     this.plugin.leaderboard_updater.cancel();
                     this.plugin.leaderboard_updater = null;
                  }

                  var2.sendMessage(this.plugin.customization.prefix + "You have " + ChatColor.GREEN + "successfully" + ChatColor.GRAY + " removed the top sign with the id of " + ChatColor.LIGHT_PURPLE + "#" + var5);
               }
            }
         }

         if (this.plugin.players.contains(var2.getName())) {
            if (this.plugin.protectedPlayers.contains(var1.getPlayer().getName())) {
               var1.setCancelled(true);
               return;
            }

            PlayerData var12 = (PlayerData)this.plugin.playerData.get(var2.getName());
            if (var12.arena != null) {
               if (!var12.arena.isBreakable(var3) || !var12.arena.isPlaceable(var3)) {
                  var1.setCancelled(true);
                  return;
               }

               Team var13 = (Team)var12.arena.players.get(var2.getName());
               if (var13 != null && var13.getSize() > 1) {
                  Location var6 = var3.getLocation().add(0.0D, 1.0D, 0.0D);
                  Iterator var8 = var13.getPlayers().iterator();

                  while(var8.hasNext()) {
                     OfflinePlayer var7 = (OfflinePlayer)var8.next();
                     if (var7 instanceof Player && !var7.getName().equals(var2.getName()) && ((Player)var7).getLocation().getBlock().getLocation().equals(var6)) {
                        var2.sendMessage((String)this.plugin.customization.messages.get("Block-Break-Below-Team"));
                        var1.setCancelled(true);
                        return;
                     }
                  }
               }

               ++var12.blocks_broken;
               this.plugin.achievementsManager.checkPlayer(var2, Enums.AchievementType.BLOCKS_BROKEN, var12.blocks_broken);
            }
         }

      }
   }

   @EventHandler
   public void onProjectileLaunch(ProjectileLaunchEvent var1) {
      final Projectile var2 = var1.getEntity();
      if (var2.getShooter() instanceof Player) {
         Player var3 = (Player)var2.getShooter();
         if (this.plugin.players.contains(var3.getName())) {
            PlayerData var4 = (PlayerData)this.plugin.playerData.get(var3.getName());
            ++var4.projectiles_launched;
            this.plugin.achievementsManager.checkPlayer(var3, Enums.AchievementType.PROJECTILES_LAUNCHED, var4.projectiles_launched);
            if (var2.getType().equals(EntityType.ARROW) && this.plugin.trails.containsKey(var4.selectedTrail)) {
               final World var5 = var3.getWorld();
               final Trail var6 = (Trail)this.plugin.trails.get(var4.selectedTrail);
               (new BukkitRunnable() {
                  public void run() {
                     var5.playEffect(var2.getLocation(), Effect.STEP_SOUND, var6.typeId);
                     if (!var2.isValid() || var2.isOnGround() || var2.getLocation().getY() < 0.0D) {
                        this.cancel();
                     }

                  }
               }).runTaskTimer(this.plugin, 3L, 3L);
            }
         }
      }

   }

   @EventHandler
   public void onPlayerChat(AsyncPlayerChatEvent var1) {
      Player var2 = var1.getPlayer();
      if (this.plugin.players.contains(var2.getName())) {
         PlayerData var3 = (PlayerData)this.plugin.playerData.get(var2.getName());
         if (this.plugin.config.displayRankInChat) {
            var1.setFormat(ChatColor.GRAY + "[" + ChatColor.AQUA + var3.player_rank + ChatColor.GRAY + "] " + var1.getFormat());
         }

         if (var1.getMessage().startsWith("!") && var3.party != null) {
            var1.setMessage(var1.getMessage().replaceFirst("!", ""));
            var1.setFormat(ChatColor.GRAY + "[" + ChatColor.LIGHT_PURPLE + "PARTY" + ChatColor.GRAY + "] " + var1.getFormat());
            var1.getRecipients().clear();
            Iterator var10 = this.plugin.getPlayers(var3.party.players).iterator();

            while(var10.hasNext()) {
               Player var8 = (Player)var10.next();
               var1.getRecipients().add(var8);
            }

            return;
         }

         Arena var4 = var3.arena;
         Player var5;
         Iterator var6;
         if (var4 != null) {
            if (var4.spectators.contains(var2.getName())) {
               if (!this.plugin.config.spectatorsChat) {
                  return;
               }

               var1.setFormat(ChatColor.GRAY + "[SPEC] " + var1.getFormat());
               var1.getRecipients().clear();
               var6 = this.plugin.getPlayers((Collection)(var4.state.equals(Enums.ArenaState.ENDING) ? var4.players.keySet() : var4.spectators)).iterator();

               while(var6.hasNext()) {
                  var5 = (Player)var6.next();
                  var1.getRecipients().add(var5);
               }

               return;
            }

            Team var9 = (Team)var4.players.get(var2.getName());
            Iterator var7;
            Player var11;
            if (var9 != null && !var4.state.AVAILABLE()) {
               var1.setFormat(ChatColor.valueOf(var9.getName()) + "[" + var9.getName() + "] " + var1.getFormat());
               if (!this.plugin.config.teamChat) {
                  return;
               }

               var1.getRecipients().clear();
               if (var4.teamSize > 1 && !var1.getMessage().startsWith("@")) {
                  var7 = var9.getPlayers().iterator();

                  while(var7.hasNext()) {
                     OfflinePlayer var12 = (OfflinePlayer)var7.next();
                     if (var12 instanceof Player) {
                        var1.getRecipients().add((Player)var12);
                     }
                  }
               } else {
                  if (var1.getMessage().startsWith("@")) {
                     var1.setMessage(var1.getMessage().replaceFirst("@", ""));
                     var1.setFormat(ChatColor.GOLD + "[Shout] " + var1.getFormat());
                  }

                  var7 = var4.getPlayers().iterator();

                  while(var7.hasNext()) {
                     var11 = (Player)var7.next();
                     var1.getRecipients().add(var11);
                  }
               }

               return;
            }

            if (!var4.state.AVAILABLE()) {
               return;
            }

            if (!this.plugin.config.lobbyChat) {
               return;
            }

            var1.setFormat(ChatColor.GRAY + "[Lobby] " + var1.getFormat());
            var1.getRecipients().clear();
            var7 = var4.getPlayers().iterator();

            while(var7.hasNext()) {
               var11 = (Player)var7.next();
               var1.getRecipients().add(var11);
            }

            return;
         }

         if (this.plugin.lobbyPlayers.contains(var2.getName())) {
            if (!this.plugin.config.lobbyChat) {
               return;
            }

            var1.setFormat(ChatColor.GRAY + "[Lobby] " + var1.getFormat());
            var1.getRecipients().clear();
            var6 = this.plugin.getPlayers(this.plugin.lobbyPlayers).iterator();

            while(var6.hasNext()) {
               var5 = (Player)var6.next();
               var1.getRecipients().add(var5);
            }

            return;
         }
      }

   }

   @EventHandler
   public void onCreatureSpawn(CreatureSpawnEvent var1) {
      if (var1.getSpawnReason().equals(SpawnReason.NATURAL) && this.plugin.config.disableNaturalMobSpawning && this.plugin.arenas != null) {
         Iterator var3 = this.plugin.arenas.values().iterator();

         while(var3.hasNext()) {
            Arena var2 = (Arena)var3.next();
            if (var2.cuboid.contains(var1.getLocation())) {
               var1.setCancelled(true);
            }
         }
      }

   }

   @EventHandler
   public void onPlayerInteractWithEntity(PlayerInteractEntityEvent var1) {
      Player var2 = var1.getPlayer();
      if (this.plugin.players.contains(var2.getName())) {
         Entity var3 = var1.getRightClicked();
         PlayerData var4 = (PlayerData)this.plugin.playerData.get(var2.getName());
         if (this.plugin.protectedPlayers.contains(var2.getName())) {
            var1.setCancelled(true);
            if (var3.getType().equals(EntityType.PLAYER)) {
               Player var7 = (Player)var3;
               if (this.plugin.config.allowSpectatorsViewInventory && var4.arena != null && var4.arena.spectators.contains(var2.getName()) && var4.arena.players.containsKey(var7.getName()) && !var4.arena.spectators.contains(var7.getName())) {
                  var2.openInventory(var7.getInventory());
                  return;
               }

               if (var2.isSneaking() && this.plugin.lobbyPlayers.contains(var2.getName()) && this.plugin.lobbyPlayers.contains(var7.getName())) {
                  PlayerData var8 = (PlayerData)this.plugin.playerData.get(var7.getName());
                  if (var4.party != null) {
                     if (var4.hasCooldown(var2, "PARTY_INVITE", 3)) {
                        return;
                     }

                     if (!var4.party.leaderName.equals(var2.getName())) {
                        var2.sendMessage((String)this.plugin.customization.messages.get("Party-Must-Be-Leader"));
                        return;
                     }

                     if (var4.party.invited.contains(var7.getName())) {
                        var2.sendMessage((String)this.plugin.customization.messages.get("Player-Already-Invited"));
                        return;
                     }

                     var4.party.invite(var7);
                  } else if (var8.party != null && !var4.hasCooldown(var2, "PARTY_JOIN", 3)) {
                     var8.party.join(var2);
                  }

                  return;
               }

               return;
            }

            return;
         }

         if (var4.arena != null && !var4.arena.spectators.contains(var2.getName()) && !var4.arena.state.AVAILABLE()) {
            if (var3.getType().equals(EntityType.VILLAGER) && var3.hasMetadata("EW_VILLAGER")) {
               var1.setCancelled(true);
               if (this.plugin.villagerShop.containsShop(var4.arena.votedShop)) {
                  this.plugin.villagerShop.openShop(var2, var4.arena.votedShop);
               } else {
                  var2.sendMessage((String)this.plugin.customization.messages.get("Villager-Shop-Open-Error"));
               }

               return;
            }

            if (var2.isSneaking() && var3.hasMetadata("EW_MOB") && (var3.getType().equals(EntityType.ZOMBIE) || var3.getType().equals(EntityType.SKELETON)) && var3 instanceof Creature && ((LivingEntity)var3).getCustomName() != null && ChatColor.stripColor(((LivingEntity)var3).getCustomName().split("'s")[0]).equals(var2.getName())) {
               ItemStack var5 = var2.getItemInHand();
               EntityEquipment var6 = ((Creature)var3).getEquipment();
               if (var5 != null && !var5.getType().equals(Material.AIR)) {
                  var1.setCancelled(true);
                  if (var5.getType().name().contains("HELMET")) {
                     var2.setItemInHand(var6.getHelmet());
                     var6.setHelmet(var5);
                  } else if (var5.getType().name().contains("CHESTPLATE")) {
                     var2.setItemInHand(var6.getChestplate());
                     var6.setChestplate(var5);
                  } else if (var5.getType().name().contains("LEGGINGS")) {
                     var2.setItemInHand(var6.getLeggings());
                     var6.setLeggings(var5);
                  } else if (var5.getType().name().contains("BOOTS")) {
                     var2.setItemInHand(var6.getBoots());
                     var6.setBoots(var5);
                  } else {
                     var2.setItemInHand(var6.getItemInHand());
                     var6.setItemInHand(var5);
                  }

                  var2.updateInventory();
               }
            }

            return;
         }
      }

   }

   @EventHandler
   public void onEntityTarget(EntityTargetEvent var1) {
      if (var1.getTarget() != null) {
         if (var1.getEntity().hasMetadata("EW_MOB")) {
            var1.setCancelled(true);
         }

      }
   }

   @EventHandler
   public void onEntityExplode(EntityExplodeEvent var1) {
      if (this.plugin.arenas != null) {
         Iterator var3 = this.plugin.arenas.values().iterator();

         while(var3.hasNext()) {
            Arena var2 = (Arena)var3.next();
            if (var2.cuboid.contains(var1.getLocation())) {
               ArrayList var4 = new ArrayList(var1.blockList());
               Iterator var6 = var4.iterator();

               while(true) {
                  Block var5;
                  do {
                     if (!var6.hasNext()) {
                        return;
                     }

                     var5 = (Block)var6.next();
                  } while(var2.isBreakable(var5) && var2.isPlaceable(var5));

                  var1.blockList().remove(var5);
               }
            }
         }

      }
   }

   @EventHandler
   public void onEntityTeleport(PlayerTeleportEvent var1) {
      Player var2 = var1.getPlayer();
      if (this.plugin.config.teleportSoundEnabled && this.plugin.players.contains(var2.getName())) {
         var2.playSound(var2.getLocation(), this.plugin.ENDERMAN_TELEPORT, 1.0F, 1.0F);
      }

   }

   @EventHandler
   public void onSignChange(SignChangeEvent var1) {
      if (var1.getLine(0).equalsIgnoreCase("[ew]")) {
         Player var2 = var1.getPlayer();
         if (!var2.hasPermission("eggwars.createsigns")) {
            var2.sendMessage((String)this.plugin.customization.messages.get("No-Permission"));
            return;
         }

         var1.setLine(0, this.plugin.customization.signs_title);
         String var3 = var1.getLine(1).toLowerCase();
         String var4;
         int var7;
         Location var20;
         int var21;
         if (var3.equals("join")) {
            var1.setLine(1, this.plugin.customization.signs_join);
            var4 = var1.getLine(2);
            if (var4.isEmpty()) {
               FileConfiguration var12 = this.plugin.filesManager.getConfig("signs.yml");
               Location var17 = var1.getBlock().getLocation();
               var7 = 1;
               if (var12.getConfigurationSection("Signs.Join") != null && !var12.getConfigurationSection("Signs.Join").getKeys(false).isEmpty()) {
                  var7 = var12.getConfigurationSection("Signs.Join").getKeys(false).size() + 1;
                  if (var12.contains("Signs.Join." + var7)) {
                     var7 = 1;
                  }

                  while(var12.contains("Signs.Join." + var7)) {
                     ++var7;
                  }
               }

               var12.set("Signs.Join." + var7, this.plugin.getStringFromLocation(var17, false));
               this.plugin.filesManager.saveConfig("signs.yml");
               this.plugin.joinSigns.put(var17, var7);
               var1.setLine(2, "Players:");
               var1.setLine(3, String.valueOf(this.plugin.players.size()));
               var2.sendMessage(this.plugin.customization.prefix + "You have " + ChatColor.GREEN + "successfully" + ChatColor.GRAY + " created a " + ChatColor.AQUA + "join" + ChatColor.GRAY + " sign with the id of " + ChatColor.LIGHT_PURPLE + "#" + var7);
            } else {
               final Arena var13 = (Arena)this.plugin.arenas.get(var4.toLowerCase());
               if (var13 == null) {
                  var1.setLine(2, ChatColor.DARK_RED + "Invalid");
                  var2.sendMessage(this.plugin.customization.prefix + "Couldn't find an arena with that name!");
                  return;
               }

               File var19 = new File(this.plugin.getDataFolder() + "/arenas/" + var13.name, "locations.dat");
               YamlConfiguration var22 = YamlConfiguration.loadConfiguration(var19);
               var20 = var1.getBlock().getLocation();
               var21 = 1;
               if (var22.getConfigurationSection("Signs") != null && !var22.getConfigurationSection("Signs").getKeys(false).isEmpty()) {
                  var21 = var22.getConfigurationSection("Signs").getKeys(false).size() + 1;
                  if (var22.contains("Signs." + var21)) {
                     var21 = 1;
                  }

                  while(var22.contains("Signs." + var21)) {
                     ++var21;
                  }
               }

               var22.set("Signs." + var21, this.plugin.getStringFromLocation(var20, false));

               try {
                  var22.save(var19);
               } catch (IOException var11) {
                  var11.printStackTrace();
               }

               var13.signs.put(var20, var21);
               var1.setLine(2, this.plugin.customization.signs_arena_color + var13.name);
               Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
                  public void run() {
                     var13.updateItem(0);
                  }
               }, 5L);
               var2.sendMessage(this.plugin.customization.prefix + "You have " + ChatColor.GREEN + "successfully" + ChatColor.GRAY + " created a " + ChatColor.AQUA + "join" + ChatColor.GRAY + " sign with the id of " + ChatColor.LIGHT_PURPLE + "#" + var21);
            }

            return;
         }

         if (var3.equals("top")) {
            var4 = var1.getLine(2).toUpperCase();
            boolean var5 = false;
            Enums.Stat[] var9;
            int var8 = (var9 = Enums.Stat.values()).length;

            for(var7 = 0; var7 < var8; ++var7) {
               Enums.Stat var6 = var9[var7];
               if (var6.name().equals(var4)) {
                  var5 = true;
                  break;
               }
            }

            if (!var5) {
               var1.setLine(2, ChatColor.DARK_RED + "Invalid");
               String var16 = ChatColor.GREEN + Enums.Stat.values()[0].name();

               for(var7 = 1; var7 < Enums.Stat.values().length; ++var7) {
                  var16 = var16 + ChatColor.YELLOW + ", " + ChatColor.GREEN + Enums.Stat.values()[var7].name();
               }

               var2.sendMessage(this.plugin.customization.prefix + "Invalid stat! use one of the following stats: " + ChatColor.YELLOW + var16);
               return;
            }

            boolean var14 = false;
            int var15;
            if (this.plugin.checkNumbers(var1.getLine(3)) && (var15 = Integer.valueOf(var1.getLine(3))) >= 1) {
               FileConfiguration var18 = this.plugin.filesManager.getConfig("signs.yml");
               var20 = var1.getBlock().getLocation();
               var21 = 1;
               if (var18.getConfigurationSection("Signs.Top") != null && !var18.getConfigurationSection("Signs.Top").getKeys(false).isEmpty()) {
                  var21 = var18.getConfigurationSection("Signs.Top").getKeys(false).size() + 1;
                  if (var18.contains("Signs.Top." + var21)) {
                     var21 = 1;
                  }

                  while(var18.contains("Signs.Top." + var21)) {
                     ++var21;
                  }
               }

               var18.set("Signs.Top." + var21, this.plugin.getStringFromLocation(var20, false));
               this.plugin.filesManager.saveConfig("signs.yml");
               this.plugin.topSigns.put(var20, var21);
               var1.setLine(0, ChatColor.AQUA + "Top #" + ChatColor.RED + var15);
               var1.setLine(1, var1.getLine(2));
               var1.setLine(2, "Waiting...");
               var1.setLine(3, "(0)");
               this.plugin.startLeaderboardUpdater();
               var2.sendMessage(this.plugin.customization.prefix + "You have " + ChatColor.GREEN + "successfully" + ChatColor.GRAY + " created a " + ChatColor.AQUA + "top" + ChatColor.GRAY + " sign with the id of " + ChatColor.LIGHT_PURPLE + "#" + var21 + ChatColor.GRAY + "! use /ew list to check when their next update is!");
               return;
            }

            var1.setLine(3, ChatColor.DARK_RED + "Invalid");
            var2.sendMessage(this.plugin.customization.prefix + "Fourth line is the rank you are looking for!");
            return;
         }

         if (var3.equals("leave") || var3.equals("autojoin")) {
            var2.sendMessage(this.plugin.customization.prefix + "You have " + ChatColor.GREEN + "successfully" + ChatColor.GRAY + " created a(n) " + ChatColor.AQUA + var3 + ChatColor.GRAY + " sign!");
            if (var3.equals("leave")) {
               var1.setLine(1, this.plugin.customization.signs_leave);
            } else {
               var1.setLine(1, this.plugin.customization.signs_autojoin);
            }

            return;
         }

         var2.sendMessage(this.plugin.customization.prefix + "Second line must be join/leave/autojoin/top");
         var1.setLine(1, ChatColor.DARK_RED + "Invalid");
      }

   }

   @EventHandler
   public void onPlayerDropItem(PlayerDropItemEvent var1) {
      if (this.plugin.protectedPlayers.contains(var1.getPlayer().getName())) {
         var1.setCancelled(true);
      }

   }

   @EventHandler
   public void onPlayerPickupItem(PlayerPickupItemEvent var1) {
      if (this.plugin.protectedPlayers.contains(var1.getPlayer().getName())) {
         var1.setCancelled(true);
      }

   }

   @EventHandler
   public void onPlayerLoseHunger(FoodLevelChangeEvent var1) {
      if (var1.getEntityType().equals(EntityType.PLAYER) && this.plugin.protectedPlayers.contains(var1.getEntity().getName())) {
         var1.setCancelled(true);
      }

   }

   @EventHandler
   public void onCraftItem(CraftItemEvent var1) {
      if (this.plugin.players.contains(var1.getWhoClicked().getName())) {
         var1.setCancelled(true);
      }

   }

   @EventHandler
   public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent var1) {
      Player var2 = var1.getPlayer();
      String var3 = var1.getMessage().replace("/", "").split(" ")[0].toLowerCase();
      if (this.plugin.config.commandAliases.contains(var3)) {
         var3 = "eggwars";
         var1.setMessage(var1.getMessage().replace(var1.getMessage().split(" ")[0], "/" + var3));
      }

      if (this.plugin.players.contains(var2.getName())) {
         if (var2.hasPermission("eggwars.unblockcmds")) {
            return;
         }

         if (!this.plugin.config.allowedCommands.contains(var3)) {
            var1.setCancelled(true);
            var2.sendMessage((String)this.plugin.customization.messages.get("Command-Block"));
         }
      }

   }

   @EventHandler
   public void onServerPing(ServerListPingEvent var1) {
      if (this.plugin.isOneGamePerServer()) {
         var1.setMotd(((Arena)this.plugin.arenas.values().toArray()[0]).state.value);
      }

   }

   private String getDeathMessage(Player var1, PlayerData var2, Player var3) {
      if (var3 != null) {
         String var4 = var1.getName();
         String var5 = var3.getName();
         if (var2.arena.players.get(var4) != null) {
            var4 = ChatColor.valueOf(((Team)var2.arena.players.get(var1.getName())).getName()) + var1.getName();
         }

         if (var5.equals(var1.getName())) {
            return this.plugin.customization.player_suicide.replace("%player%", var4);
         } else {
            if (var2.arena.players.containsKey(var5) && var2.arena.players.get(var5) != null) {
               var5 = ChatColor.valueOf(((Team)var2.arena.players.get(var5)).getName()) + var5;
            }

            return ((String)this.plugin.customization.killMessages.get(this.plugin.r.nextInt(this.plugin.customization.killMessages.size()))).replace("%player%", var4).replace("%killer%", var5);
         }
      } else {
         return var1.getLastDamageCause() != null && this.plugin.customization.deathMessages.containsKey(var1.getLastDamageCause().getCause().name()) ? ((String)this.plugin.customization.deathMessages.get(var1.getLastDamageCause().getCause().name())).replace("%player%", var1.getName()) : ((String)this.plugin.customization.deathMessages.get("UNKNOWN")).replace("%player%", var1.getName());
      }
   }

   public void awardKiller(Player var1) {
      PlayerData var2 = (PlayerData)this.plugin.playerData.get(var1.getName());
      Arena var3 = var2.arena;
      if (var3 != null && var3.players.get(var1.getName()) != null) {
         ++var2.deaths;
         var2.killstreak = 0;
         Player var4 = var1.getKiller() != null ? var1.getKiller() : (!var2.lastHit.isEmpty() && (System.currentTimeMillis() - var2.lastHitTime) / 1000L <= 15L ? Bukkit.getPlayer(var2.lastHit) : null);
         if (var4 != null && !var4.getName().equals(var1.getName())) {
            boolean var5 = true;
            String var7;
            Iterator var8;
            if (!var3.killers.containsKey(var4.getName())) {
               ArrayList var10 = new ArrayList();
               var10.add(var1.getName());
               var3.killers.put(var4.getName(), var10);
            } else {
               if (((List)var3.killers.get(var4.getName())).size() >= this.plugin.config.samePlayerKillLimitPerArena) {
                  int var6 = 0;
                  var8 = ((List)var3.killers.get(var4.getName())).iterator();

                  while(var8.hasNext()) {
                     var7 = (String)var8.next();
                     if (var7.equals(var1.getName())) {
                        ++var6;
                     }
                  }

                  if (var6 >= this.plugin.config.samePlayerKillLimitPerArena) {
                     var5 = false;
                     var4.sendMessage(((String)this.plugin.customization.messages.get("Player-Kill-Error")).replace("%player%", var1.getName()));
                  }
               }

               if (var5) {
                  ((List)var3.killers.get(var4.getName())).add(var1.getName());
               }
            }

            if (var5) {
               PlayerData var11 = (PlayerData)this.plugin.playerData.get(var4.getName());
               ++var11.kills;
               ++var11.killstreak;
               var4.setLevel(var11.killstreak);
               if (this.plugin.config.killstreaks.containsKey(var11.killstreak)) {
                  var8 = ((List)this.plugin.config.killstreaks.get(var11.killstreak)).iterator();

                  while(var8.hasNext()) {
                     var7 = (String)var8.next();
                     Bukkit.dispatchCommand(Bukkit.getConsoleSender(), var7.replace("%player%", var4.getName()));
                  }

                  var4.sendMessage(((String)this.plugin.customization.messages.get("Killstreak-Award")).replace("%killstreak%", String.valueOf(var11.killstreak)));
                  var4.playSound(var4.getLocation(), this.plugin.NOTE_PLING, 1.0F, 1.0F);
                  TitleObject var13 = this.plugin.config.titles_enabled ? (new TitleObject(((String)this.plugin.customization.titles.get("Killstreak")).replace("%killstreak%", String.valueOf(var11.killstreak)), TitleType.TITLE)).setFadeIn(this.plugin.config.titles_fadeIn).setStay(this.plugin.config.titles_stay).setFadeOut(this.plugin.config.titles_fadeOut) : null;
                  if (var13 != null) {
                     var13.send(var4);
                  }
               }

               int var14 = this.plugin.config.coinsPerKill * var11.modifier;
               var11.addCoins(var4, var14);
               int var16 = (this.plugin.r.nextInt(this.plugin.config.maxExpPerKill - this.plugin.config.minExpPerKill) + this.plugin.config.minExpPerKill + (this.plugin.r.nextBoolean() ? 1 : 0)) * var11.modifier;
               var4.sendMessage(((String)this.plugin.customization.messages.get("Player-Kill")).replace("%target%", var1.getName()).replace("%coins%", String.valueOf(var14)).replace("%exp%", String.valueOf(var16)) + ChatColor.YELLOW + (var11.modifier > 1 ? " (x" + var11.modifier + ")" : ""));
               var4.playSound(var4.getLocation(), this.plugin.NOTE_PLING, 1.0F, 1.0F);
               this.plugin.ranksManager.addExp(var4, var16);
               this.plugin.achievementsManager.checkPlayer(var4, Enums.AchievementType.KILLS, var11.kills);
               if (this.plugin.config.actionbar_enabled) {
                  (new ActionbarTitleObject(ChatColor.RED + "You have: " + ChatColor.YELLOW + ((List)var3.killers.get(var4.getName())).size() + ChatColor.RED + " kills!")).send(var4);
               }
            }
         }

         String var9 = this.getDeathMessage(var1, var2, var4);
         Iterator var15 = var3.getPlayers().iterator();

         while(var15.hasNext()) {
            Player var12 = (Player)var15.next();
            var12.sendMessage(var9);
         }

      }
   }

   private List circle(Location var1, Integer var2, Integer var3, Boolean var4, Boolean var5) {
      ArrayList var6 = new ArrayList();
      int var7 = var1.getBlockX();
      int var8 = var1.getBlockY();
      int var9 = var1.getBlockZ();

      for(int var10 = var7 - var2; var10 <= var7 + var2; ++var10) {
         for(int var11 = var9 - var2; var11 <= var9 + var2; ++var11) {
            for(int var12 = var5 ? var8 - var2 : var8; var12 < (var5 ? var8 + var2 : var8 + var3); ++var12) {
               double var13 = (double)((var7 - var10) * (var7 - var10) + (var9 - var11) * (var9 - var11) + (var5 ? (var8 - var12) * (var8 - var12) : 0));
               if (var13 < (double)(var2 * var2) && (!var4 || var13 >= (double)((var2 - 1) * (var2 - 1)))) {
                  Location var15 = new Location(var1.getWorld(), (double)var10, (double)var12, (double)var11);
                  var6.add(var15);
               }
            }
         }
      }

      return var6;
   }
}
