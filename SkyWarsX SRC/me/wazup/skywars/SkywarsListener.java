package me.wazup.skywars;

import io.puharesource.mc.titlemanager.api.ActionbarTitleObject;
import io.puharesource.mc.titlemanager.api.TitleObject;
import io.puharesource.mc.titlemanager.api.TitleObject.TitleType;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
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
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

class SkywarsListener implements Listener {
   private Skywars plugin;

   public SkywarsListener(Skywars var1) {
      this.plugin = var1;
   }

   @EventHandler
   public void onPlayerPreLogin(PlayerLoginEvent var1) {
      try {
         if (!this.plugin.isOneGamePerServer()) {
            return;
         }

         if (!((Arena)this.plugin.arenas.values().toArray()[0]).state.AVAILABLE()) {
            var1.disallow(Result.KICK_OTHER, (String)this.plugin.customization.messages.get("Arena-Not-Available"));
         }
      } catch (NullPointerException var3) {
         var1.disallow(Result.KICK_OTHER, (String)this.plugin.customization.messages.get("Arena-Not-Available"));
      }

   }

   @EventHandler
   public void onServerPing(ServerListPingEvent var1) {
      if (this.plugin.isOneGamePerServer()) {
         var1.setMotd(((Arena)this.plugin.arenas.values().toArray()[0]).state.value);
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

      if (var2.hasPermission("skywars.admin")) {
         if (this.plugin.availableUpdate) {
            var2.sendMessage(this.plugin.customization.prefix + "Found a new available version! " + ChatColor.LIGHT_PURPLE + "download at http://goo.gl/GpTbwN");
         } else {
            this.plugin.checkUpdates(var2);
         }
      }

   }

   @EventHandler
   public void onPlayerQuit(PlayerQuitEvent var1) {
      Player var2 = var1.getPlayer();
      PlayerData var3 = (PlayerData)this.plugin.playerData.get(var2.getName());
      if (this.plugin.players.contains(var2.getName())) {
         if (var3.arena != null && !var3.arena.state.AVAILABLE() && !var3.arena.spectators.contains(var2.getName()) && !var3.lastHit.isEmpty() && (System.currentTimeMillis() - var3.lastHitTime) / 1000L <= 15L) {
            Player var4 = Bukkit.getPlayer(var3.lastHit);
            if (var4 != null && !var4.getName().equals(var2.getName()) && var3.arena.players.containsKey(var4.getName())) {
               ++var3.deaths;
               this.awardKiller(var4, var2);
            }
         }

         this.plugin.leave(var2, true);
      }

      var3.saveAsync(var2);
      if (var3.party != null) {
         var3.party.leave(var2);
      }

   }

   @EventHandler
   public void onPlayerInteract(PlayerInteractEvent var1) {
      final Player var2 = var1.getPlayer();
      Location var27;
      if (this.plugin.selectionMode.containsKey(var2.getName()) && this.plugin.compareItem(var2.getItemInHand(), this.plugin.wand_itemstack)) {
         var1.setCancelled(true);
         int var22 = var1.getAction().equals(Action.LEFT_CLICK_BLOCK) ? 0 : (var1.getAction().equals(Action.RIGHT_CLICK_BLOCK) ? 1 : 2);
         if (var22 != 2) {
            var27 = var1.getClickedBlock().getLocation();
            ((Location[])this.plugin.selectionMode.get(var2.getName()))[var22] = var27;
            var2.sendMessage(this.plugin.customization.prefix + "You have set the " + ChatColor.LIGHT_PURPLE + "#" + (var22 + 1) + ChatColor.GRAY + " corner at " + this.plugin.getReadableLocationString(var27, false));
         }
      } else {
         Block var3;
         Iterator var29;
         if (var2.getItemInHand() != null && var2.getItemInHand().getType().equals(this.plugin.chest_tool_itemstack.getType()) && var2.getItemInHand().getItemMeta().hasDisplayName() && var2.getItemInHand().getItemMeta().getDisplayName().equals(this.plugin.chest_tool_itemstack.getItemMeta().getDisplayName())) {
            if (var2.hasPermission("skywars.chestmanager")) {
               var1.setCancelled(true);
               if (var1.getAction().equals(Action.RIGHT_CLICK_BLOCK) || var1.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                  var3 = var1.getClickedBlock();
                  if (var3.getType().equals(Material.CHEST)) {
                     var27 = var3.getLocation();
                     var29 = this.plugin.arenas.values().iterator();

                     while(var29.hasNext()) {
                        Arena var30 = (Arena)var29.next();
                        Iterator var32 = var30.chests.keySet().iterator();

                        while(var32.hasNext()) {
                           Location var31 = (Location)var32.next();
                           if (var31.equals(var27)) {
                              String var9;
                              if (var1.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                                 var9 = ChatColor.stripColor(((String)var2.getItemInHand().getItemMeta().getLore().get(var2.getItemInHand().getItemMeta().getLore().size() - 1)).split(": ")[1]);
                                 if (((HashMap)this.plugin.chests.get("Normal")).containsKey(var9.toLowerCase())) {
                                    long var10 = System.currentTimeMillis();
                                    var30.chests.put(var31, var9.toLowerCase());
                                    File var12 = new File(this.plugin.getDataFolder() + "/arenas/" + var30.name, "locations.dat");
                                    YamlConfiguration var13 = YamlConfiguration.loadConfiguration(var12);
                                    ArrayList var14 = new ArrayList();
                                    Iterator var16 = var30.chests.keySet().iterator();

                                    while(var16.hasNext()) {
                                       Location var15 = (Location)var16.next();
                                       var14.add(var15.getBlockX() + ":" + var15.getBlockY() + ":" + var15.getBlockZ() + ":" + var15.getBlock().getTypeId() + ":" + var15.getBlock().getData() + ":" + (String)var30.chests.get(var15));
                                    }

                                    var13.set("Chests", var14.toString());

                                    try {
                                       var13.save(var12);
                                    } catch (IOException var17) {
                                       var17.printStackTrace();
                                    }

                                    var2.sendMessage(this.plugin.customization.prefix + "You have changed this chest type to " + ChatColor.AQUA + var9 + ChatColor.GRAY + "! took " + ChatColor.LIGHT_PURPLE + (System.currentTimeMillis() - var10) + "ms");
                                 } else {
                                    var2.sendMessage(this.plugin.customization.prefix + "You had an invalid " + this.plugin.chest_tool_itemstack.getItemMeta().getDisplayName());
                                    var2.setItemInHand(new ItemStack(Material.AIR));
                                 }
                              } else if (var1.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                                 var9 = (String)var30.chests.get(var31);
                                 var2.sendMessage(this.plugin.customization.prefix + "Arena: " + ChatColor.LIGHT_PURPLE + var30.name + ChatColor.GRAY + " - Chest type: " + ChatColor.AQUA + var9);
                              }

                              return;
                           }
                        }
                     }

                     var2.sendMessage(this.plugin.customization.prefix + "Couldn't find this chest in any of the arenas!");
                  } else {
                     var2.sendMessage(this.plugin.customization.prefix + "You can only use this tool on chests!");
                  }
               }
            } else {
               var2.setItemInHand(new ItemStack(Material.AIR));
            }

         } else {
            if (var1.getAction().equals(Action.RIGHT_CLICK_AIR) || var1.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
               String var5;
               if (var1.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                  var3 = var1.getClickedBlock();
                  if (var3.getType().equals(Material.WALL_SIGN) || var3.getType().equals(Material.SIGN_POST)) {
                     Sign var4 = (Sign)var3.getState();
                     if (var4.getLine(0).equals(this.plugin.customization.signs_title)) {
                        var1.setCancelled(true);
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
                           var5 = ChatColor.stripColor(var4.getLine(2)).toLowerCase();
                           if (!this.plugin.arenas.containsKey(var5)) {
                              if (this.plugin.players.contains(var2.getName())) {
                                 var2.sendMessage((String)this.plugin.customization.messages.get("Already-In-Game"));
                                 return;
                              }

                              this.plugin.join(var2);
                           } else {
                              ((Arena)this.plugin.arenas.get(var5)).join(var2);
                           }
                        } else if (var4.getLine(1).equals(this.plugin.customization.signs_leave)) {
                           this.plugin.leave(var2, false);
                        } else if (var4.getLine(1).equals(this.plugin.customization.signs_autojoin)) {
                           this.plugin.autojoin(var2, ChatColor.stripColor(var4.getLine(2).toUpperCase()));
                        }

                        return;
                     }
                  }
               }

               if (this.plugin.players.contains(var2.getName())) {
                  Player var25;
                  if (this.plugin.protectedPlayers.contains(var2.getName())) {
                     var1.setCancelled(true);
                     if (var1.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                        var3 = var1.getClickedBlock();
                        if (var3.getType().equals(this.plugin.mysteryBox.type)) {
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

                     if (this.plugin.compareItem(var2.getItemInHand(), this.plugin.play_itemstack)) {
                        this.plugin.arenaSelector.open(var2);
                        return;
                     }

                     if (this.plugin.compareItem(var2.getItemInHand(), this.plugin.vote_itemstack)) {
                        var2.openInventory(this.plugin.votingOptions);
                        return;
                     }

                     if (this.plugin.compareItem(var2.getItemInHand(), this.plugin.inventory_itemstack)) {
                        ((PlayerData)this.plugin.playerData.get(var2.getName())).inventory.open(var2);
                        return;
                     }

                     PlayerData var21;
                     if (this.plugin.lobbyPlayers.contains(var2.getName())) {
                        if (this.plugin.compareItem(var2.getItemInHand(), this.plugin.shop_itemstack)) {
                           this.plugin.shop.open(var2);
                           return;
                        }

                        if (this.plugin.compareItem(var2.getItemInHand(), this.plugin.party_itemstack)) {
                           var21 = (PlayerData)this.plugin.playerData.get(var2.getName());
                           if (var21.party == null) {
                              var2.openInventory(this.plugin.partyMenu);
                           } else {
                              var2.openInventory(var21.party.gui);
                           }

                           return;
                        }

                        return;
                     }

                     if (!this.plugin.compareItem(var2.getItemInHand(), this.plugin.teleporter_itemstack)) {
                        return;
                     }

                     var21 = (PlayerData)this.plugin.playerData.get(var2.getName());
                     if (var21.arena != null && var21.arena.spectators.contains(var2.getName())) {
                        if (var21.hasCooldown(var2, "TELEPORT_GUI", 5)) {
                           return;
                        }

                        Inventory var26 = Bukkit.createInventory(var2, 54, (String)this.plugin.customization.inventories.get("Spectator-Teleporter"));
                        var29 = var21.arena.getPlayers().iterator();

                        while(var29.hasNext()) {
                           var25 = (Player)var29.next();
                           if (!var21.arena.spectators.contains(var25.getName())) {
                              var26.addItem(new ItemStack[]{this.plugin.getSkull(var25.getName(), ChatColor.AQUA + var25.getName())});
                           }
                        }

                        var2.openInventory(var26);
                     }

                     return;
                  }

                  if (var2.getItemInHand().getType().equals(Material.COMPASS)) {
                     Arena var19 = ((PlayerData)this.plugin.playerData.get(var2.getName())).arena;
                     if (var19 != null && var19.players.containsKey(var2.getName()) && !var19.spectators.contains(var2.getName())) {
                        if (((PlayerData)this.plugin.playerData.get(var2.getName())).hasCooldown(var2, "COMPASS_TRACK", 5)) {
                           return;
                        }

                        int var23 = Integer.MAX_VALUE;
                        var25 = null;
                        Iterator var7 = var19.getEnemeies((Team)var19.players.get(var2.getName())).iterator();

                        while(var7.hasNext()) {
                           Player var28 = (Player)var7.next();
                           if (var2.getWorld().getName().equals(var28.getWorld().getName())) {
                              double var8 = var28.getLocation().distance(var2.getLocation());
                              if (var8 < (double)var23) {
                                 var23 = (int)var8;
                                 var25 = var28;
                              }
                           }
                        }

                        if (var25 != null) {
                           (new ItemStackBuilder(var2.getItemInHand())).setName(ChatColor.BOLD + "Tracking: " + ChatColor.RED + var25.getName() + ChatColor.WHITE + ChatColor.BOLD + " - DISTANCE: " + ChatColor.RED + var23 + ".0").build();
                           var2.setCompassTarget(var25.getLocation());
                           var2.sendMessage(((String)this.plugin.customization.messages.get("Tracking-Player")).replace("%target%", var25.getName()));
                        } else {
                           (new ItemStackBuilder(var2.getItemInHand())).setName(ChatColor.RED + "No nearby players!").build();
                        }
                     }

                     return;
                  }

                  if (var2.getItemInHand().getType().equals(Material.MONSTER_EGG)) {
                     if (var1.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                        short var18 = var2.getItemInHand().getDurability();
                        EntityType var20 = EntityType.fromId(var18);
                        if (var20 == null && var2.getItemInHand().getItemMeta().getDisplayName() != null) {
                           var5 = var2.getItemInHand().getItemMeta().getDisplayName();
                           var20 = EntityType.fromName(ChatColor.stripColor(var5));
                        }

                        if (var20 != null) {
                           final LivingEntity var24 = (LivingEntity)var2.getWorld().spawnEntity(var1.getClickedBlock().getRelative(var1.getBlockFace()).getLocation().add(0.5D, 0.0D, 0.5D), var20);
                           var24.setCustomName(ChatColor.AQUA + var2.getName() + "'s " + var24.getType().getName());
                           var24.setCustomNameVisible(true);
                           var24.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 999999, 3));
                           if (var24 instanceof Ageable) {
                              ((Ageable)var24).setAdult();
                           }

                           var24.setMetadata("SW_MOB", new FixedMetadataValue(this.plugin, true));
                           if (this.plugin.config.leashMobs) {
                              var24.setLeashHolder(var2);
                           }

                           if (var24 instanceof Creature) {
                              final String var6 = var2.getName();
                              (new BukkitRunnable() {
                                 public void run() {
                                    if (!var24.isDead() && var24.isValid()) {
                                       PlayerData var1;
                                       if (SkywarsListener.this.plugin.players.contains(var6) && (var1 = (PlayerData)SkywarsListener.this.plugin.playerData.get(var6)).arena != null && !var1.arena.state.AVAILABLE() && !var1.arena.spectators.contains(var6)) {
                                          if (SkywarsListener.this.plugin.config.teleportMobs && var2 != null && var24.getWorld().getName().equals(var2.getWorld().getName()) && var2.getLocation().distance(var24.getLocation()) > 20.0D) {
                                             var24.teleport(var2.getLocation());
                                          }

                                          List var2x = var1.arena.getEnemeies((Team)var1.arena.players.get(var6));
                                          if (var2x.isEmpty()) {
                                             var24.remove();
                                          } else {
                                             int var3 = Integer.MAX_VALUE;
                                             Player var4 = null;
                                             Iterator var6x = var2x.iterator();

                                             while(var6x.hasNext()) {
                                                Player var5 = (Player)var6x.next();
                                                if (var24.getWorld().getName().equals(var5.getWorld().getName())) {
                                                   double var7 = var5.getLocation().distance(var24.getLocation());
                                                   if (var7 < (double)var3) {
                                                      var3 = (int)var7;
                                                      var4 = var5;
                                                   }
                                                }
                                             }

                                             if (var4 != null) {
                                                ((Creature)var24).setTarget(var4);
                                             } else {
                                                var24.remove();
                                                this.cancel();
                                             }

                                          }
                                       } else {
                                          var24.remove();
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

                        if (var2.getItemInHand().getAmount() > 1) {
                           var2.getItemInHand().setAmount(var2.getItemInHand().getAmount() - 1);
                        } else {
                           var2.setItemInHand(new ItemStack(Material.AIR));
                        }

                        var1.setCancelled(true);
                     }

                     return;
                  }
               }
            }

         }
      }
   }

   @EventHandler
   public void onPlayerDeath(PlayerDeathEvent var1) {
      final Player var2 = var1.getEntity();
      if (this.plugin.players.contains(var2.getName())) {
         final PlayerData var3 = (PlayerData)this.plugin.playerData.get(var2.getName());
         Arena var4 = var3.arena;
         if (var4 != null && var4.players.containsKey(var2.getName()) && !this.plugin.protectedPlayers.contains(var2.getName())) {
            this.plugin.protectedPlayers.add(var2.getName());
            var2.setHealth(var2.getMaxHealth());
            var2.setFoodLevel(20);
            Iterator var6 = var1.getDrops().iterator();

            while(var6.hasNext()) {
               ItemStack var5 = (ItemStack)var6.next();
               var2.getWorld().dropItemNaturally(var2.getLocation(), var5);
            }

            var1.getDrops().clear();
            ++var3.deaths;
            Player var7 = var2.getKiller();
            if (var7 == null && !var3.lastHit.isEmpty() && (System.currentTimeMillis() - var3.lastHitTime) / 1000L <= 15L) {
               var7 = Bukkit.getPlayer(var3.lastHit);
            }

            if (var7 != null && !var7.getName().equals(var2.getName())) {
               this.awardKiller(var7, var2);
            }

            var1.setDeathMessage((String)null);
            String var8 = this.getDeathMessage(var2, var7);
            var4.kill(var2, var8);
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
               public void run() {
                  if (var2.getVehicle() != null) {
                     var2.getVehicle().eject();
                  }

                  Iterator var2x = var2.getActivePotionEffects().iterator();

                  while(var2x.hasNext()) {
                     PotionEffect var1 = (PotionEffect)var2x.next();
                     var2.removePotionEffect(var1.getType());
                  }

                  var2.setFireTicks(0);
                  var2.setLevel(0);
                  var2.setExp(0.0F);
                  var3.makeSpectator(var2);
               }
            }, 2L);
         } else {
            var1.getDrops().clear();
         }
      }

   }

   @EventHandler
   public void onInventoryClick(InventoryClickEvent var1) {
      Player var2 = (Player)var1.getWhoClicked();
      Inventory var3 = var1.getInventory();
      ItemStack var4 = var1.getCurrentItem();
      int var5 = var1.getRawSlot();
      int var10;
      String var25;
      Iterator var36;
      Iterator var49;
      if (var3.getName().contains(ChatColor.RED + "Editing: ")) {
         var1.setCancelled(true);
         if (var5 >= 0 && var5 <= 53 && var4 != null && !var4.getType().equals(Material.AIR) && !this.plugin.compareItem(var4, this.plugin.pane_itemstack)) {
            Arena var18 = (Arena)this.plugin.arenas.get(ChatColor.stripColor(var3.getName().split(": ")[1].toLowerCase()));
            if (var18 == null) {
               var2.closeInventory();
            } else if (!this.plugin.compareItem(var4, this.plugin.plus_itemstack) && !this.plugin.compareItem(var4, this.plugin.minus_itemstack)) {
               if (var4.getType().equals(Material.INK_SACK)) {
                  (new ItemStackBuilder(var4)).setDurability(var4.getDurability() == 8 ? 10 : 8).setName(var4.getDurability() == 10 ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled").build();
               } else {
                  if (this.plugin.compareItem(var4, this.plugin.save_itemstack)) {
                     long var34 = System.currentTimeMillis();
                     HashMap var38 = new HashMap();

                     for(var10 = 9; var10 < 14; ++var10) {
                        var38.put(ChatColor.stripColor(var3.getItem(var10).getItemMeta().getDisplayName()).split(": ")[0].toLowerCase().replace(" ", "-"), Integer.valueOf(ChatColor.stripColor(var3.getItem(var10).getItemMeta().getDisplayName()).split(": ")[1]));
                     }

                     if ((Integer)var38.get("min-teams") < 2) {
                        var2.sendMessage(this.plugin.customization.prefix + "Min teams must be at least 2!");
                        return;
                     }

                     if (var18.state.AVAILABLE()) {
                        var36 = var18.getAliveTeams().iterator();

                        while(var36.hasNext()) {
                           Team var44 = (Team)var36.next();
                           var18.destroyCage(var44);
                        }
                     }

                     boolean var46 = ChatColor.stripColor(var3.getItem(40).getItemMeta().getDisplayName().toLowerCase()).equals("enabled");
                     var18.enabled = var46;
                     var18.teamSize = (Integer)var38.get("team-size");
                     var18.minTeams = (Integer)var38.get("min-teams");
                     var18.maxTeams = (Integer)var38.get("max-teams");
                     var18.lobbyCountdown = (Integer)var38.get("lobby-countdown");
                     var18.gameLength = (Integer)var38.get("game-length");
                     var18.scoreboard.update((String)this.plugin.customization.scoreboard.get("Mode"), var18.getMode(), true);
                     File var48 = new File(this.plugin.getDataFolder() + "/arenas/" + var18.name, "settings.yml");
                     YamlConfiguration var43 = YamlConfiguration.loadConfiguration(var48);
                     var49 = var38.keySet().iterator();

                     while(var49.hasNext()) {
                        String var47 = (String)var49.next();
                        var43.set(var47, var38.get(var47));
                     }

                     var43.set("enabled", var46);

                     try {
                        var43.save(var48);
                     } catch (IOException var16) {
                        var16.printStackTrace();
                     }

                     var18.minPlayersForCountdownShorten = var43.getInt("lobby-countdown-shortening.min-teams") * var18.teamSize;
                     var18.stop(true);
                     var2.sendMessage(this.plugin.customization.prefix + "Your settings have been saved & applied! took " + ChatColor.LIGHT_PURPLE + (System.currentTimeMillis() - var34) + "ms!");
                     var2.closeInventory();
                  }

               }
            } else {
               boolean var32 = this.plugin.compareItem(var4, this.plugin.plus_itemstack);
               ItemStack var28 = var3.getItem(var32 ? var1.getSlot() + 9 : var1.getSlot() - 9);
               var25 = ChatColor.stripColor(var28.getItemMeta().getDisplayName()).split(": ")[0];
               var10 = Integer.valueOf(ChatColor.stripColor(var28.getItemMeta().getDisplayName()).split(": ")[1]) + (var32 ? 1 : -1);
               if (var10 >= 1) {
                  (new ItemStackBuilder(var28)).setName(ChatColor.YELLOW + var25 + ": " + ChatColor.GOLD + var10).build();
               }
            }
         }
      } else {
         String var7;
         int var12;
         int var29;
         if (var3.getName().contains(ChatColor.BLUE + "Editing: ")) {
            if (var5 >= 0 && var4 != null && !this.plugin.compareItem(var4, this.plugin.pane_itemstack)) {
               String var17 = ChatColor.stripColor(var3.getName().split(": ")[1].split(" #")[0]).toLowerCase();
               if (this.plugin.editingChests.containsKey(var2.getName()) && ((HashMap)this.plugin.chests.get(this.plugin.editingChests.get(var2.getName()))).containsKey(var17)) {
                  var7 = (String)this.plugin.editingChests.get(var2.getName());
                  ChestType var27 = (ChestType)((HashMap)this.plugin.chests.get(var7)).get(var17);
                  if (var27.editor.handleClick(var2, var4, var3)) {
                     var1.setCancelled(true);
                  } else if (var5 < 54 && var5 > 45) {
                     var1.setCancelled(true);
                     if (!this.plugin.compareItem(var4, this.plugin.plus_itemstack) && !this.plugin.compareItem(var4, this.plugin.minus_itemstack)) {
                        if (!this.plugin.compareItem(var4, this.plugin.save_itemstack)) {
                           if (var4.getType().equals(Material.EMERALD) && var5 == 53) {
                              var29 = var27.editor.addInventory(ChatColor.RED + var27.name + " #" + (var27.editor.getSize() + 1));
                              var27.addSettings(this.plugin, var29);
                           }
                        } else {
                           long var33 = System.currentTimeMillis();
                           ArrayList var42 = new ArrayList();

                           for(var12 = 0; var12 < var27.editor.getSize(); ++var12) {
                              var42.addAll(var27.editor.getContents(var12));
                           }

                           ArrayList var40 = new ArrayList();
                           var49 = var42.iterator();

                           while(var49.hasNext()) {
                              ItemStack var41 = (ItemStack)var49.next();
                              var40.add(this.plugin.getItemStackString(var41));
                           }

                           int var45 = Integer.valueOf(ChatColor.stripColor(var3.getItem(47).getItemMeta().getDisplayName()).split(": ")[1]);
                           int var50 = Integer.valueOf(ChatColor.stripColor(var3.getItem(51).getItemMeta().getDisplayName()).split(": ")[1]);
                           FileConfiguration var15 = this.plugin.filesManager.getConfig("chests.yml");
                           var15.set("Chests." + var7 + "." + var27.name + ".min-items", var45);
                           var15.set("Chests." + var7 + "." + var27.name + ".max-items", var50);
                           var15.set("Chests." + var7 + "." + var27.name + ".items", var40);
                           this.plugin.filesManager.saveConfig("chests.yml");
                           var27.items = var42;
                           var27.minItems = var45;
                           var27.maxItems = var50;
                           var2.closeInventory();
                           var2.sendMessage(this.plugin.customization.prefix + "You have " + ChatColor.GREEN + "successfully" + ChatColor.GRAY + " saved & applied the new settings! took " + ChatColor.LIGHT_PURPLE + (System.currentTimeMillis() - var33) + "ms!");
                        }
                     } else {
                        boolean var31 = this.plugin.compareItem(var4, this.plugin.plus_itemstack);
                        ItemStack var35 = var3.getItem(var31 ? var1.getSlot() - 1 : var1.getSlot() + 1);
                        String var39 = ChatColor.stripColor(var35.getItemMeta().getDisplayName()).split(": ")[0];
                        var12 = Integer.valueOf(ChatColor.stripColor(var35.getItemMeta().getDisplayName()).split(": ")[1]) + (var31 ? 1 : -1);
                        if (var12 >= 1) {
                           (new ItemStackBuilder(var35)).setName(ChatColor.YELLOW + var39 + ": " + ChatColor.GOLD + var12).build();
                        }
                     }
                  }
               } else {
                  var1.setCancelled(true);
                  var2.closeInventory();
               }
            } else {
               var1.setCancelled(true);
            }
         } else {
            if (this.plugin.protectedPlayers.contains(var2.getName())) {
               var1.setCancelled(true);
               if (var5 < 0 || var5 > var1.getInventory().getSize() - 1 || var4 == null || var4.getType().equals(Material.AIR) || this.plugin.compareItem(var4, this.plugin.pane_itemstack)) {
                  return;
               }

               if (var3.getName().equals(this.plugin.quitInventory.getName())) {
                  if (var4.getDurability() == 5) {
                     this.plugin.leave(var2, false);
                  } else if (var4.getDurability() == 14) {
                     var2.closeInventory();
                  }

                  return;
               }

               PlayerData var6 = (PlayerData)this.plugin.playerData.get(var2.getName());
               if (var3.getName().equals(this.plugin.profileInventory.getName())) {
                  if (var6.hasCooldown(var2, "PROFILE_CLICK", 3)) {
                     return;
                  }

                  if (this.plugin.compareItem(var4, this.plugin.stats_itemstack)) {
                     var2.openInventory(var6.getStatsInventory(var2));
                     return;
                  }

                  if (this.plugin.compareItem(var4, this.plugin.inventory_itemstack)) {
                     var6.inventory.open(var2);
                     return;
                  }

                  if (this.plugin.compareItem(var4, this.plugin.achievements_itemstack)) {
                     var6.achievements.open(var2);
                     return;
                  }

                  return;
               }

               Inventory var22;
               if (var3.getName().contains(var6.inventory.getName())) {
                  if (var6.inventory.handleClick(var2, var4, var3)) {
                     return;
                  }

                  if (this.plugin.compareItem(var4, this.plugin.back_itemstack)) {
                     var2.openInventory(this.plugin.profileInventory);
                     return;
                  }

                  if (var1.getAction().equals(InventoryAction.PICKUP_HALF)) {
                     var22 = Bukkit.createInventory(var2, 9, (String)this.plugin.customization.inventories.get("Sell-Confirm"));
                     var22.setItem(2, this.plugin.confirm_itemstack);
                     var22.setItem(4, var4);
                     var22.setItem(6, this.plugin.cancel_itemstack);
                     var2.openInventory(var22);
                  } else {
                     if (var6.hasCooldown(var2, "ITEM_EQUIP", 3)) {
                        return;
                     }

                     var7 = ChatColor.stripColor(var4.getItemMeta().getDisplayName().toLowerCase());
                     if (this.plugin.kits.containsKey(var7)) {
                        var6.selectedKit = var7;
                        if (var6.lobbyScoreboard != null) {
                           var6.lobbyScoreboard.update((String)this.plugin.customization.scoreboard.get("Kit"), ((Kit)this.plugin.kits.get(var7)).name, true);
                        }

                        var2.sendMessage(((String)this.plugin.customization.messages.get("Item-Select")).replace("%item%", ((Kit)this.plugin.kits.get(var7)).name));
                     } else if (this.plugin.cages.containsKey(var7)) {
                        var6.selectedCage = var7;
                        if (var6.lobbyScoreboard != null) {
                           var6.lobbyScoreboard.update((String)this.plugin.customization.scoreboard.get("Cage"), ((Cage)this.plugin.cages.get(var7)).name, true);
                        }

                        var2.sendMessage(((String)this.plugin.customization.messages.get("Item-Select")).replace("%item%", ((Cage)this.plugin.cages.get(var7)).name));
                     } else if (this.plugin.trails.containsKey(var7)) {
                        var6.selectedTrail = var7;
                        if (var6.lobbyScoreboard != null) {
                           var6.lobbyScoreboard.update((String)this.plugin.customization.scoreboard.get("Trail"), ((Trail)this.plugin.trails.get(var7)).name, true);
                        }

                        var2.sendMessage(((String)this.plugin.customization.messages.get("Item-Select")).replace("%item%", ((Trail)this.plugin.trails.get(var7)).name));
                     }
                  }

                  return;
               }

               if (var3.getName().contains(var6.achievements.getName())) {
                  if (var6.achievements.handleClick(var2, var4, var3)) {
                     return;
                  }

                  if (this.plugin.compareItem(var4, this.plugin.back_itemstack)) {
                     var2.openInventory(this.plugin.profileInventory);
                     return;
                  }

                  return;
               }

               if (var3.getName().equals(this.plugin.customization.inventories.get("Stats"))) {
                  if (this.plugin.compareItem(var4, this.plugin.back_itemstack)) {
                     var2.openInventory(this.plugin.profileInventory);
                     return;
                  }

                  return;
               }

               if (var3.getName().equals(this.plugin.customization.inventories.get("Sell-Confirm"))) {
                  if (this.plugin.compareItem(var4, this.plugin.cancel_itemstack)) {
                     var6.inventory.open(var2);
                     return;
                  }

                  if (!this.plugin.compareItem(var4, this.plugin.confirm_itemstack)) {
                     return;
                  }

                  var7 = ChatColor.stripColor(var3.getItem(4).getItemMeta().getDisplayName().toLowerCase());
                  Object var26 = this.plugin.kits.containsKey(var7) ? (Sellable)this.plugin.kits.get(var7) : (this.plugin.cages.containsKey(var7) ? (Sellable)this.plugin.cages.get(var7) : (this.plugin.trails.containsKey(var7) ? (Trail)this.plugin.trails.get(var7) : null));
                  if (var26 == null) {
                     var2.closeInventory();
                     var2.sendMessage((String)this.plugin.customization.messages.get("Invalid-Item"));
                  }

                  var29 = this.plugin.kits.containsKey(var7) ? 0 : (this.plugin.cages.containsKey(var7) ? 1 : 2);
                  int[] var13;
                  var12 = (var13 = this.plugin.smartSlots).length;

                  for(int var37 = 0; var37 < var12; ++var37) {
                     var10 = var13[var37];
                     ItemStack var14 = var6.inventory.getItem(var29, var10);
                     if (var14 != null && this.plugin.compareItem(var14, var3.getItem(4))) {
                        var6.inventory.removeItem(var29, var10);
                        break;
                     }
                  }

                  if (var6.selectedKit.equals(var7)) {
                     var6.selectedKit = "";
                  } else if (var6.selectedCage.equals(var7)) {
                     var6.selectedCage = "";
                  } else if (var6.selectedTrail.equals(var7)) {
                     var6.selectedTrail = "";
                  }

                  var6.inventory.open(var2);
                  var10 = (int)((double)((Sellable)var26).value * this.plugin.config.selling_value);
                  var6.addCoins(var2, var10);
                  if (var6.lobbyScoreboard != null) {
                     var6.createScoreboard(var2);
                  }

                  var2.sendMessage(((String)this.plugin.customization.messages.get("Item-Sell")).replace("%value%", String.valueOf(var10)));
                  return;
               }

               if (var3.getName().contains(this.plugin.arenaSelector.getName())) {
                  if (this.plugin.arenaSelector.handleClick(var2, var4, var3)) {
                     return;
                  }

                  if (var6.hasCooldown(var2, "ARENA_SELECT", 3)) {
                     return;
                  }

                  if (!this.plugin.compareItem(var4, this.plugin.autojoin_itemstack)) {
                     var7 = ChatColor.stripColor(var4.getItemMeta().getDisplayName().toLowerCase());
                     if (this.plugin.arenas.containsKey(var7)) {
                        ((Arena)this.plugin.arenas.get(var7)).join(var2);
                     } else {
                        var2.sendMessage((String)this.plugin.customization.messages.get("Invalid-Arena"));
                        var2.closeInventory();
                     }

                     return;
                  }

                  this.plugin.autojoin(var2, var6.party != null && var6.party.players.size() > 1 ? "TEAM" : "");
                  return;
               }

               String var24;
               if (var3.getName().contains((CharSequence)this.plugin.customization.inventories.get("Arena-Settings"))) {
                  if (var6.arena != null && (var6.arena == null || var6.arena.state.AVAILABLE())) {
                     if (var3.getName().contains((String)this.plugin.customization.inventories.get("Arena-Settings") + ": ")) {
                        if (this.plugin.compareItem(var4, this.plugin.back_itemstack)) {
                           var2.openInventory(this.plugin.votingOptions);
                           return;
                        }

                        if (var6.hasCooldown(var2, "SETTINGS_VOTE", 3)) {
                           return;
                        }

                        var7 = ChatColor.stripColor(var3.getName().split(": ")[1]);
                        var24 = ChatColor.stripColor(var4.getItemMeta().getDisplayName());
                        var6.arena.votesManager.vote(var2, var7, var24);
                        var25 = ((String)this.plugin.customization.messages.get("Player-Vote")).replace("%player%", var2.getName()).replace("%option%", var7 + ": " + var24).replace("%votes%", String.valueOf(var6.arena.votesManager.getVotes(var7, var24)));
                        var36 = var6.arena.getPlayers().iterator();

                        while(var36.hasNext()) {
                           Player var30 = (Player)var36.next();
                           var30.sendMessage(var25);
                        }
                     } else {
                        var7 = ChatColor.stripColor(var4.getItemMeta().getDisplayName());
                        if ((!var7.equals("Time") || var2.hasPermission("skywars.vote.time")) && (!var7.equals("Health") || var2.hasPermission("skywars.vote.health")) && (!var7.equals("Chests") || var2.hasPermission("skywars.vote.chests"))) {
                           var6.arena.votesManager.openEntry(var2, var7);
                        } else {
                           var2.sendMessage((String)this.plugin.customization.messages.get("No-Permission"));
                        }
                     }

                     return;
                  }

                  var2.closeInventory();
                  return;
               }

               Player var8;
               if (this.plugin.lobbyPlayers.contains(var2.getName())) {
                  if (var3.getName().contains(this.plugin.shop.getName())) {
                     if (this.plugin.shop.handleClick(var2, var4, var3)) {
                        return;
                     }

                     var22 = Bukkit.createInventory(var2, 9, (String)this.plugin.customization.inventories.get("Confirm-Purchase"));
                     var22.setItem(1, this.plugin.confirm_itemstack);
                     var22.setItem(4, var4);
                     var22.setItem(7, this.plugin.cancel_itemstack);
                     var2.openInventory(var22);
                     return;
                  }

                  if (var3.getName().equals(this.plugin.customization.inventories.get("Confirm-Purchase"))) {
                     if (this.plugin.compareItem(var4, this.plugin.confirm_itemstack)) {
                        ItemStack var21 = var3.getItem(4);
                        var24 = ChatColor.stripColor(var21.getItemMeta().getDisplayName().toLowerCase());
                        byte var11;
                        if (this.plugin.kits.containsKey(var24)) {
                           var10 = ((Kit)this.plugin.kits.get(var24)).value;
                           var25 = ((Kit)this.plugin.kits.get(var24)).permission;
                           var11 = 0;
                        } else if (this.plugin.cages.containsKey(var24)) {
                           var10 = ((Cage)this.plugin.cages.get(var24)).value;
                           var25 = ((Cage)this.plugin.cages.get(var24)).permission;
                           var11 = 1;
                        } else {
                           if (!this.plugin.trails.containsKey(var24)) {
                              var2.sendMessage((String)this.plugin.customization.messages.get("Invalid-Item"));
                              var2.closeInventory();
                              return;
                           }

                           var10 = ((Trail)this.plugin.trails.get(var24)).value;
                           var25 = ((Trail)this.plugin.trails.get(var24)).permission;
                           var11 = 2;
                        }

                        if (!var25.isEmpty() && !var2.hasPermission(var25)) {
                           var2.sendMessage((String)this.plugin.customization.messages.get("No-Permission"));
                           return;
                        }

                        if (var6.inventory.getContents(var11).contains(var21)) {
                           var2.sendMessage((String)this.plugin.customization.messages.get("Item-Owned"));
                           var2.closeInventory();
                           return;
                        }

                        if (var6.getCoins(var2) < var10) {
                           var2.sendMessage((String)this.plugin.customization.messages.get("Not-Enough-Coins"));
                           var2.closeInventory();
                           return;
                        }

                        if (var6.inventory.addItem(var11, var21)) {
                           var6.removeCoins(var2, var10);
                           if (var6.lobbyScoreboard != null) {
                              var6.lobbyScoreboard.update((String)this.plugin.customization.scoreboard.get("Coins"), var6.getCoins(var2), true);
                           }

                           var2.sendMessage((String)this.plugin.customization.messages.get("Item-Purchase"));
                        } else {
                           var2.sendMessage((String)this.plugin.customization.messages.get("Not-Enough-Space"));
                        }

                        var2.closeInventory();
                        return;
                     }

                     if (this.plugin.compareItem(var4, this.plugin.cancel_itemstack)) {
                        this.plugin.shop.open(var2);
                        return;
                     }

                     return;
                  }

                  Iterator var9;
                  if (var3.getName().equals(this.plugin.partyMenu.getName())) {
                     if (this.plugin.compareItem(var4, this.plugin.create_itemstack)) {
                        if (var6.hasCooldown(var2, "PARTY_CREATE", 15)) {
                           return;
                        }

                        int var19 = this.plugin.config.party_default_slots;
                        var9 = this.plugin.config.party_custom_slots.keySet().iterator();

                        while(var9.hasNext()) {
                           int var23 = (Integer)var9.next();
                           if (var23 > var19 && var2.hasPermission((String)this.plugin.config.party_custom_slots.get(var23))) {
                              var19 = var23;
                           }
                        }

                        var6.party = new Party(this.plugin, var2, var19);
                        this.plugin.parties.add(var6.party);
                        this.plugin.updatePartiesInventory();
                        var2.openInventory(var6.party.gui);
                        return;
                     }

                     if (this.plugin.compareItem(var4, this.plugin.join_itemstack)) {
                        this.plugin.partySelector.open(var2);
                        return;
                     }

                     return;
                  }

                  if (var3.getName().equals(this.plugin.customization.inventories.get("Party-Settings"))) {
                     if (var6.hasCooldown(var2, "PARTY_MODIFY", 2)) {
                        return;
                     }

                     if (var4.getType().equals(Material.EYE_OF_ENDER)) {
                        if (var6.party.leaderName.equals(var2.getName())) {
                           var6.party.setPrivacy(var6.party.privacy == Enums.PartyPrivacy.INVITE ? Enums.PartyPrivacy.PUBLIC : Enums.PartyPrivacy.INVITE);
                           var6.party.updateItem();
                        } else {
                           var2.sendMessage((String)this.plugin.customization.messages.get("Party-Must-Be-Leader"));
                        }

                        return;
                     }

                     if (var4.getType().equals(Material.CHEST)) {
                        var2.openInventory(var6.party.playersInventory);
                        return;
                     }

                     if (this.plugin.compareItem(var4, this.plugin.invite_itemstack)) {
                        if (var6.party.leaderName.equals(var2.getName())) {
                           this.plugin.playerInviter.open(var2);
                        } else {
                           var2.sendMessage((String)this.plugin.customization.messages.get("Party-Must-Be-Leader"));
                        }

                        return;
                     }

                     if (this.plugin.compareItem(var4, this.plugin.leave_itemstack)) {
                        var6.party.leave(var2);
                     }

                     return;
                  }

                  if (var3.getName().equals(this.plugin.customization.inventories.get("Party-Players"))) {
                     if (this.plugin.compareItem(var4, this.plugin.back_itemstack)) {
                        var2.openInventory(var6.party.gui);
                        return;
                     }

                     if (!var6.party.leaderName.equals(var2.getName())) {
                        var2.sendMessage((String)this.plugin.customization.messages.get("Party-Must-Be-Leader"));
                        return;
                     }

                     var7 = ChatColor.stripColor(var4.getItemMeta().getDisplayName());
                     if (var7.equals(var2.getName())) {
                        return;
                     }

                     var8 = Bukkit.getPlayer(var7);
                     if (var8 == null) {
                        var2.sendMessage((String)this.plugin.customization.messages.get("Unknown-Player"));
                        return;
                     }

                     if (var1.getAction().equals(InventoryAction.PICKUP_HALF)) {
                        if (var6.party.players.contains(var7)) {
                           var6.party.kick(var8);
                        } else {
                           var2.sendMessage((String)this.plugin.customization.messages.get("Party-Unknown-Player"));
                        }
                     } else {
                        var6.party.setLeader(var8);
                        var6.party.sendMessage(((String)this.plugin.customization.messages.get("Party-New-Leader")).replace("%player%", var8.getName()));
                        var6.party.updateItem();
                        var6.party.updatePlayers();
                     }

                     return;
                  }

                  if (!var3.getName().contains(this.plugin.partySelector.getName())) {
                     if (var3.getName().contains(this.plugin.playerInviter.getName())) {
                        if (this.plugin.playerInviter.handleClick(var2, var4, var3)) {
                           return;
                        }

                        if (this.plugin.compareItem(var4, this.plugin.back_itemstack)) {
                           var2.openInventory(var6.party.gui);
                           return;
                        }

                        if (var6.hasCooldown(var2, "PARTY_INVITE", 3)) {
                           return;
                        }

                        var7 = ChatColor.stripColor(var4.getItemMeta().getDisplayName());
                        var8 = Bukkit.getPlayer(var7);
                        if (var8 == null) {
                           var2.sendMessage((String)this.plugin.customization.messages.get("Unknown-Player"));
                           return;
                        }

                        if (var8.getName().equals(var2.getName())) {
                           var2.sendMessage((String)this.plugin.customization.messages.get("Party-Self-Invite"));
                           return;
                        }

                        if (!var6.party.invited.contains(var8.getName()) && !var6.party.players.contains(var8.getName())) {
                           var6.party.invite(var8);
                           return;
                        }

                        var2.sendMessage((String)this.plugin.customization.messages.get("Player-Already-Invited"));
                        return;
                     }

                     if (var3.getName().equals(this.plugin.mysteryBox.inventory.getName())) {
                        if (this.plugin.compareItem(var4, this.plugin.cancel_itemstack)) {
                           var2.closeInventory();
                           return;
                        }

                        if (this.plugin.compareItem(var4, this.plugin.confirm_itemstack)) {
                           this.plugin.mysteryBox.open(var2);
                           return;
                        }

                        return;
                     }

                     return;
                  }

                  if (this.plugin.partySelector.handleClick(var2, var4, var3)) {
                     return;
                  }

                  if (this.plugin.compareItem(var4, this.plugin.back_itemstack)) {
                     var2.openInventory(this.plugin.partyMenu);
                     return;
                  }

                  if (var6.hasCooldown(var2, "PARTY_JOIN", 3)) {
                     return;
                  }

                  var7 = ChatColor.stripColor(var4.getItemMeta().getDisplayName().replace("'s party", ""));
                  var9 = this.plugin.parties.iterator();

                  while(var9.hasNext()) {
                     Party var20 = (Party)var9.next();
                     if (var20.leaderName.equals(var7)) {
                        var20.join(var2);
                        return;
                     }
                  }

                  var2.closeInventory();
                  var2.sendMessage((String)this.plugin.customization.messages.get("Invalid-Party"));
                  return;
               }

               if (var3.getName().equals(this.plugin.customization.inventories.get("Spectator-Teleporter"))) {
                  if (var6.arena != null && var6.arena.spectators.contains(var2.getName())) {
                     var7 = ChatColor.stripColor(var4.getItemMeta().getDisplayName());
                     var8 = Bukkit.getPlayer(var7);
                     if (var8 == null) {
                        var2.sendMessage((String)this.plugin.customization.messages.get("Unknown-Player"));
                        var2.closeInventory();
                        return;
                     }

                     if (!var6.arena.players.containsKey(var8.getName()) || var6.arena.spectators.contains(var8.getName())) {
                        var2.sendMessage((String)this.plugin.customization.messages.get("Player-Inactive"));
                        var2.closeInventory();
                        return;
                     }

                     var2.teleport(var8.getLocation().add(0.0D, 3.0D, 0.0D));
                  } else {
                     var2.closeInventory();
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
         if (var1.getDamager().equals(EntityType.FIREWORK)) {
            if (var1.getEntity().getType().equals(EntityType.PLAYER) && this.plugin.players.contains(((Player)var1.getEntity()).getName())) {
               var1.setCancelled(true);
            }

         } else {
            if (var1.getDamager().getType().equals(EntityType.PLAYER)) {
               var2 = (Player)var1.getDamager();
               if (this.plugin.protectedPlayers.contains(var2.getName())) {
                  var1.setCancelled(true);
                  return;
               }
            }

            if (var1.getEntityType().equals(EntityType.PLAYER) || var1.getEntity().hasMetadata("SW_MOB")) {
               LivingEntity var3;
               if (var2 == null) {
                  if (var1.getDamager() instanceof Projectile && ((Projectile)var1.getDamager()).getShooter() instanceof Player) {
                     var2 = (Player)((Projectile)var1.getDamager()).getShooter();
                  } else if (var1.getDamager().hasMetadata("SW_MOB")) {
                     var3 = (LivingEntity)var1.getDamager();
                     String var4 = ChatColor.stripColor(var3.getCustomName().split("'s")[0]);
                     var2 = Bukkit.getPlayer(var4);
                  }
               }

               if (var1.getEntityType().equals(EntityType.PLAYER)) {
                  final Player var8 = (Player)var1.getEntity();
                  PlayerData var9 = (PlayerData)this.plugin.playerData.get(var8.getName());
                  if (var9 == null) {
                     return;
                  }

                  Arena var5 = var9.arena;
                  if (var5 != null) {
                     if (var5.spectators.contains(var8.getName())) {
                        var5.sendWarning(var8);
                     } else {
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
                                       var2.sendMessage(((String)SkywarsListener.this.plugin.customization.messages.get("Arrow-Hit")).replace("%player%", var8.getName()).replace("%health%", (new BigDecimal(var8.getHealth())).setScale(1, RoundingMode.HALF_UP).toString()).replace("%heart%", Enums.SPECIAL_CHARACTER.HEART.toString()));
                                    }
                                 }, 1L);
                              }
                           }

                           ((PlayerData)this.plugin.playerData.get(var8.getName())).lastHit = var2.getName();
                           ((PlayerData)this.plugin.playerData.get(var8.getName())).lastHitTime = System.currentTimeMillis();
                        }
                     }
                  }
               } else if (var2 != null) {
                  var3 = (LivingEntity)var1.getEntity();
                  if (ChatColor.stripColor(var3.getCustomName().split("'s")[0]).equals(var2.getName())) {
                     var1.setCancelled(true);
                     return;
                  }
               }
            }

         }
      }
   }

   @EventHandler
   public void onEntityDamage(EntityDamageEvent var1) {
      if (var1.getEntityType().equals(EntityType.PLAYER)) {
         Player var2 = (Player)var1.getEntity();
         if (this.plugin.protectedPlayers.contains(var2.getName())) {
            var1.setCancelled(true);
         } else if (var1.getCause().equals(DamageCause.VOID) && this.plugin.config.voidInstantKill) {
            PlayerData var3 = (PlayerData)this.plugin.playerData.get(var2.getName());
            if (var3 != null && var3.arena != null && !var3.arena.state.AVAILABLE() && !var3.arena.spectators.contains(var2.getName())) {
               var1.setDamage(1000.0D);
            }
         }
      } else if (var1.getEntity().hasMetadata("SW_MOB") && var1.getCause().equals(DamageCause.FALL)) {
         var1.setCancelled(true);
      }

   }

   @EventHandler
   public void onBlockCanBuild(BlockCanBuildEvent var1) {
      if (!var1.isBuildable()) {
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
            if (var4 != null && !var4.cuboid.contains(var1.getBlock().getLocation())) {
               var1.setCancelled(true);
               Location var5 = var1.getBlockAgainst().getLocation().clone().add(0.5D, 1.0D, 0.5D);
               var5.setPitch(var2.getLocation().getPitch());
               var5.setYaw(var2.getLocation().getYaw());

               while(var5.getBlock().getType() != Material.AIR) {
                  var5.add(0.0D, 1.0D, 0.0D);
               }

               var2.teleport(var5);
            } else {
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
         if (var3.getType().equals(Material.WALL_SIGN) || var3.getType().equals(Material.SIGN_POST)) {
            Sign var4 = (Sign)var3.getState();
            if (var4.getLine(0).equals(this.plugin.customization.signs_title) || var4.getLine(0).startsWith(ChatColor.AQUA + "Top #" + ChatColor.RED)) {
               if (!var2.hasPermission("skywars.breaksigns")) {
                  var2.sendMessage((String)this.plugin.customization.messages.get("No-Permission"));
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
               final int var6 = ((Trail)this.plugin.trails.get(var4.selectedTrail)).typeId;
               (new BukkitRunnable() {
                  public void run() {
                     var5.playEffect(var2.getLocation(), Effect.STEP_SOUND, var6);
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

         Player var7;
         Iterator var8;
         if (var1.getMessage().startsWith("!") && var3.party != null) {
            var1.setMessage(var1.getMessage().replaceFirst("!", ""));
            var1.setFormat(ChatColor.GRAY + "[" + ChatColor.LIGHT_PURPLE + "Party" + ChatColor.GRAY + "] " + var1.getFormat());
            var1.getRecipients().clear();
            var8 = this.plugin.getPlayers(var3.party.players).iterator();

            while(var8.hasNext()) {
               var7 = (Player)var8.next();
               var1.getRecipients().add(var7);
            }

            return;
         }

         if (this.plugin.lobbyPlayers.contains(var2.getName())) {
            if (this.plugin.config.lobbyChat) {
               var1.setFormat(ChatColor.GRAY + "[" + ChatColor.AQUA + "Lobby" + ChatColor.GRAY + "] " + var1.getFormat());
               var1.getRecipients().clear();
               var8 = this.plugin.getPlayers(this.plugin.lobbyPlayers).iterator();

               while(var8.hasNext()) {
                  var7 = (Player)var8.next();
                  var1.getRecipients().add(var7);
               }
            }

            return;
         }

         Arena var4 = var3.arena;
         if (var4 != null) {
            Player var5;
            Iterator var6;
            if (!var4.state.equals(Enums.ArenaState.ENDING) && var4.spectators.contains(var2.getName())) {
               if (this.plugin.config.spectatorsChat) {
                  var1.setFormat(ChatColor.GRAY + "[Spec] " + var1.getFormat());
                  var1.getRecipients().clear();
                  var6 = this.plugin.getPlayers(var4.spectators).iterator();

                  while(var6.hasNext()) {
                     var5 = (Player)var6.next();
                     var1.getRecipients().add(var5);
                  }

                  return;
               }
            } else if (this.plugin.config.inGameChat) {
               var1.setFormat(ChatColor.GRAY + "[" + ChatColor.GREEN + "Alive" + ChatColor.GRAY + "] " + var1.getFormat());
               var1.getRecipients().clear();
               var6 = var4.getPlayers().iterator();

               while(var6.hasNext()) {
                  var5 = (Player)var6.next();
                  var1.getRecipients().add(var5);
               }
            }
         }
      }

   }

   @EventHandler
   public void onEnchantItemEvent(EnchantItemEvent var1) {
      Player var2 = var1.getEnchanter();
      if (this.plugin.players.contains(var2.getName())) {
         PlayerData var3 = (PlayerData)this.plugin.playerData.get(var2.getName());
         if (var3.arena != null) {
            ++var3.items_enchanted;
            this.plugin.achievementsManager.checkPlayer(var2, Enums.AchievementType.ITEMS_ENCHANTED, var3.items_enchanted);
         }
      }

   }

   @EventHandler
   public void onCraftItem(CraftItemEvent var1) {
      Player var2 = (Player)var1.getWhoClicked();
      if (this.plugin.players.contains(var2.getName())) {
         PlayerData var3 = (PlayerData)this.plugin.playerData.get(var2.getName());
         if (var3.arena != null) {
            ++var3.items_crafted;
            this.plugin.achievementsManager.checkPlayer(var2, Enums.AchievementType.ITEMS_CRAFTED, var3.items_crafted);
         }
      }

   }

   @EventHandler
   public void onPlayerFish(PlayerFishEvent var1) {
      Player var2 = var1.getPlayer();
      if (var1.getState().equals(State.CAUGHT_FISH) && this.plugin.players.contains(var2.getName())) {
         PlayerData var3 = (PlayerData)this.plugin.playerData.get(var2.getName());
         if (var3.arena != null) {
            ++var3.fishes_caught;
            this.plugin.achievementsManager.checkPlayer(var2, Enums.AchievementType.FISHES_CAUGHT, var3.fishes_caught);
         }
      }

   }

   @EventHandler
   public void onCreatureSpawn(CreatureSpawnEvent var1) {
      if (var1.getSpawnReason().equals(SpawnReason.NATURAL) && this.plugin.config.disableNaturalMobSpawning) {
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
         PlayerData var3 = (PlayerData)this.plugin.playerData.get(var2.getName());
         if (this.plugin.protectedPlayers.contains(var2.getName())) {
            if (var1.getRightClicked().getType().equals(EntityType.PLAYER)) {
               Player var7 = (Player)var1.getRightClicked();
               PlayerData var8 = (PlayerData)this.plugin.playerData.get(var7.getName());
               if (this.plugin.config.allowSpectatorsViewInventory && var3.arena != null && var3.arena.spectators.contains(var2.getName()) && var3.arena.players.containsKey(var7.getName()) && !var3.arena.spectators.contains(var7.getName())) {
                  var2.openInventory(var7.getInventory());
                  return;
               }

               if (var2.isSneaking() && this.plugin.lobbyPlayers.contains(var2.getName()) && this.plugin.lobbyPlayers.contains(var7.getName())) {
                  if (var3.party != null) {
                     if (var3.hasCooldown(var2, "PARTY_INVITE", 3)) {
                        return;
                     }

                     if (!var3.party.leaderName.equals(var2.getName())) {
                        var2.sendMessage((String)this.plugin.customization.messages.get("Party-Must-Be-Leader"));
                        return;
                     }

                     if (var3.party.invited.contains(var7.getName())) {
                        var2.sendMessage((String)this.plugin.customization.messages.get("Player-Already-Invited"));
                        return;
                     }

                     var3.party.invite(var7);
                  } else if (var8.party != null && !var3.hasCooldown(var2, "PARTY_JOIN", 3)) {
                     var8.party.join(var2);
                  }
               }
            }

            return;
         }

         if (var2.isSneaking() && var3.arena != null && !var3.arena.state.AVAILABLE() && !var3.arena.spectators.contains(var2.getName())) {
            Entity var4 = var1.getRightClicked();
            if (var4 instanceof Creature && (var4.getType().equals(EntityType.ZOMBIE) || var4.getType().equals(EntityType.SKELETON)) && var4.hasMetadata("SW_MOB") && ((LivingEntity)var4).getCustomName() != null && ChatColor.stripColor(((LivingEntity)var4).getCustomName().split("'s")[0]).equals(var2.getName())) {
               ItemStack var5 = var2.getItemInHand();
               EntityEquipment var6 = ((Creature)var4).getEquipment();
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
         }
      }

   }

   @EventHandler
   public void onEntityTarget(EntityTargetEvent var1) {
      if (var1.getTarget() != null) {
         if (var1.getEntity().hasMetadata("SW_MOB")) {
            var1.setCancelled(true);
         }

      }
   }

   @EventHandler
   public void onSignChange(SignChangeEvent var1) {
      if (var1.getLine(0).equalsIgnoreCase("[sw]")) {
         Player var2 = var1.getPlayer();
         if (!var2.hasPermission("skywars.createsigns")) {
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
               var2.sendMessage(this.plugin.customization.prefix + "You have " + ChatColor.GREEN + "successfully" + ChatColor.GRAY + " created a " + ChatColor.AQUA + "top" + ChatColor.GRAY + " sign with the id of " + ChatColor.LIGHT_PURPLE + "#" + var21 + ChatColor.GRAY + "! use /sw list to check when their next update is!");
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
   public void onBlockFromToEvent(BlockFromToEvent var1) {
      Location var2 = var1.getBlock().getLocation();
      if (this.plugin.arenas != null) {
         Iterator var4 = this.plugin.arenas.values().iterator();

         Arena var3;
         do {
            if (!var4.hasNext()) {
               return;
            }

            var3 = (Arena)var4.next();
         } while(!var3.cuboid.contains(var2) || var3.cuboid.contains(var1.getToBlock().getLocation()));

         var1.setCancelled(true);
      }
   }

   @EventHandler
   public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent var1) {
      Player var2 = var1.getPlayer();
      if (this.plugin.players.contains(var2.getName())) {
         if (var2.hasPermission("skywars.unblockcmds")) {
            return;
         }

         if (!this.plugin.config.allowedCommands.contains(var1.getMessage().split(" ")[0].replace("/", "").toLowerCase())) {
            var1.setCancelled(true);
            var2.sendMessage((String)this.plugin.customization.messages.get("Command-Block"));
         }
      }

   }

   private String getDeathMessage(Player var1, Player var2) {
      if (var2 != null) {
         return var2.getName().equals(var1.getName()) ? this.plugin.customization.player_suicide.replace("%player%", var1.getName()) : ((String)this.plugin.customization.killMessages.get(this.plugin.r.nextInt(this.plugin.customization.killMessages.size()))).replace("%player%", var1.getName()).replace("%killer%", var2.getName());
      } else {
         return var1.getLastDamageCause() != null && this.plugin.customization.deathMessages.containsKey(var1.getLastDamageCause().getCause().name()) ? ((String)this.plugin.customization.deathMessages.get(var1.getLastDamageCause().getCause().name())).replace("%player%", var1.getName()) : ((String)this.plugin.customization.deathMessages.get("UNKNOWN")).replace("%player%", var1.getName());
      }
   }

   private void awardKiller(Player var1, Player var2) {
      PlayerData var3 = (PlayerData)this.plugin.playerData.get(var1.getName());
      Arena var4 = var3.arena;
      ++var3.kills;
      int var5 = this.plugin.config.coinsPerKill * var3.modifier;
      var3.addCoins(var1, var5);
      int var6 = (this.plugin.r.nextInt(this.plugin.config.maxExpPerKill - this.plugin.config.minExpPerKill) + this.plugin.config.minExpPerKill + (this.plugin.r.nextBoolean() ? 1 : 0)) * var3.modifier;
      var1.sendMessage(((String)this.plugin.customization.messages.get("Player-Kill")).replace("%target%", var2.getName()).replace("%coins%", String.valueOf(var5)).replace("%exp%", String.valueOf(var6)) + ChatColor.YELLOW + (var3.modifier > 1 ? " (x" + var3.modifier + ")" : ""));
      var1.playSound(var1.getLocation(), this.plugin.NOTE_PLING, 1.0F, 1.0F);
      var4.killers.put(var1.getName(), var4.killers.containsKey(var1.getName()) ? (Integer)var4.killers.get(var1.getName()) + 1 : 1);
      this.plugin.ranksManager.addExp(var1, var6);
      this.plugin.achievementsManager.checkPlayer(var1, Enums.AchievementType.KILLS, var3.kills);
      if (this.plugin.config.actionbar_enabled) {
         (new ActionbarTitleObject(ChatColor.RED + "You have: " + ChatColor.YELLOW + var4.killers.get(var1.getName()) + ChatColor.RED + " kills!")).send(var1);
      }

      int var7 = (Integer)var4.killers.get(var1.getName());
      if (this.plugin.config.killstreaks.containsKey(var7)) {
         Iterator var9 = ((List)this.plugin.config.killstreaks.get(var7)).iterator();

         while(var9.hasNext()) {
            String var8 = (String)var9.next();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), var8.replace("%player%", var1.getName()));
         }

         var1.sendMessage(((String)this.plugin.customization.messages.get("Killstreak-Award")).replace("%killstreak%", String.valueOf(var7)));
         if (this.plugin.config.titles_enabled) {
            (new TitleObject(((String)this.plugin.customization.titles.get("Killstreak")).replace("%killstreak%", String.valueOf(var7)), TitleType.TITLE)).setFadeIn(this.plugin.config.titles_fadeIn).setStay(this.plugin.config.titles_stay).setFadeOut(this.plugin.config.titles_fadeOut).send(var1);
         }
      }

   }
}
