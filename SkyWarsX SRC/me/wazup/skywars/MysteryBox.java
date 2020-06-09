package me.wazup.skywars;

import java.util.ArrayList;
import java.util.Arrays;
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

class MysteryBox {
   Material type;
   int cost;
   HashMap contents;
   HashMap rarities;
   Inventory inventory;
   ArrayList allItems = new ArrayList();
   List slots;
   List backgroundSlots;
   int size = 45;
   int seconds = 60;
   int slowDownAt = 40;
   boolean ready = false;
   private Skywars plugin;

   public MysteryBox(Skywars var1, Material var2, int var3, HashMap var4, HashMap var5) {
      this.plugin = var1;
      this.type = var2;
      this.cost = var3;
      this.contents = var4;
      this.rarities = var5;
      if (!var4.isEmpty() && !var5.isEmpty()) {
         ItemStackBuilder var6 = (new ItemStackBuilder(var2)).setName(ChatColor.BLUE + "Mystery Box");
         var6.addLore(ChatColor.YELLOW + "Cost: " + ChatColor.LIGHT_PURPLE + var3);
         var6.addLore(" ", ChatColor.YELLOW + "Content: ");
         Iterator var8 = var4.keySet().iterator();

         String var7;
         while(var8.hasNext()) {
            var7 = (String)var8.next();
            var6.addLore("- " + ChatColor.YELLOW + var7 + " " + ChatColor.LIGHT_PURPLE + var4.get(var7) + "%");
            if (!var7.equalsIgnoreCase("kits") && !var7.equalsIgnoreCase("cages") && !var7.equalsIgnoreCase("trails")) {
               return;
            }
         }

         var6.addLore(" ", ChatColor.YELLOW + "Rarities: ");
         var8 = var5.keySet().iterator();

         while(var8.hasNext()) {
            var7 = (String)var8.next();
            var6.addLore("- " + ChatColor.YELLOW + Enums.Rarity.valueOf(var7.toUpperCase()) + " " + ChatColor.LIGHT_PURPLE + var5.get(var7) + "%");
         }

         this.slots = Arrays.asList(4, 13, 22, 31, 40);
         this.backgroundSlots = new ArrayList();

         for(int var13 = 0; var13 < this.size; ++var13) {
            if (!this.slots.contains(var13) && var13 != 21 && var13 != 23) {
               this.backgroundSlots.add(var13);
            }
         }

         var8 = var4.keySet().iterator();

         while(true) {
            label69:
            while(var8.hasNext()) {
               var7 = (String)var8.next();
               Iterator var10;
               if (var7.equalsIgnoreCase("kits")) {
                  var10 = var1.kits.values().iterator();

                  while(var10.hasNext()) {
                     Kit var14 = (Kit)var10.next();
                     this.allItems.add(var14.item);
                  }
               } else if (var7.equalsIgnoreCase("cages")) {
                  var10 = var1.cages.values().iterator();

                  while(true) {
                     while(true) {
                        if (!var10.hasNext()) {
                           continue label69;
                        }

                        Cage var9 = (Cage)var10.next();
                        if (!var9.name.equalsIgnoreCase("default")) {
                           this.allItems.add(var9.item);
                        } else if (var7.equalsIgnoreCase("trails")) {
                           Iterator var12 = var1.trails.values().iterator();

                           while(var12.hasNext()) {
                              Trail var11 = (Trail)var12.next();
                              this.allItems.add(var11.item);
                           }
                        }
                     }
                  }
               }
            }

            this.inventory = Bukkit.createInventory((InventoryHolder)null, 9, ChatColor.BLUE + "Mystery Box:");
            this.inventory.setItem(2, var1.confirm_itemstack);
            this.inventory.setItem(4, var6.build());
            this.inventory.setItem(6, var1.cancel_itemstack);
            this.ready = true;
            return;
         }
      }
   }

   public void open(final Player var1) {
      PlayerData var2 = (PlayerData)this.plugin.playerData.get(var1.getName());
      if (!var2.hasCooldown(var1, "BOX_OPEN", this.seconds / 10)) {
         if (var2.getCoins(var1) < this.cost) {
            var1.sendMessage((String)this.plugin.customization.messages.get("Not-Enough-Coins"));
            var1.closeInventory();
         } else {
            final SmartInventory var3 = var2.inventory;

            for(int var4 = 0; var4 < var3.getSize(); ++var4) {
               if (var3.getEmptySlot(var4) == -1) {
                  var1.sendMessage((String)this.plugin.customization.messages.get("Not-Enough-Space"));
                  return;
               }
            }

            final ItemStack var12 = null;
            HashMap var5 = (HashMap)this.contents.clone();

            label82:
            while(var12 == null && !var5.isEmpty()) {
               String var6 = this.plugin.randomize(var5);
               Enums.Rarity var7 = Enums.Rarity.valueOf(this.plugin.randomize(this.rarities).toUpperCase());
               ArrayList var8 = new ArrayList();
               if (var6.equalsIgnoreCase("kits")) {
                  var8.addAll(this.plugin.kits.values());
               } else if (var6.equalsIgnoreCase("cages")) {
                  var8.addAll(this.plugin.cages.values());
               } else if (var6.equalsIgnoreCase("trails")) {
                  var8.addAll(this.plugin.trails.values());
               }

               ArrayList var9 = new ArrayList();
               Iterator var11 = var8.iterator();

               while(true) {
                  Sellable var10;
                  do {
                     if (!var11.hasNext()) {
                        var8.removeAll(var9);
                        if (!var8.isEmpty()) {
                           var12 = ((Sellable)var8.get(this.plugin.r.nextInt(var8.size()))).item;
                        } else {
                           var5.remove(var6);
                        }
                        continue label82;
                     }

                     var10 = (Sellable)var11.next();
                  } while(var10.rarity == var7 && (var10.permission.isEmpty() || var1.hasPermission(var10.permission)) && (!(var10 instanceof Cage) || !var10.name.equalsIgnoreCase("default")));

                  var9.add(var10);
               }
            }

            if (var12 == null) {
               var1.sendMessage((String)this.plugin.customization.messages.get("No-Available-Items"));
               var1.closeInventory();
            } else {
               final Inventory var13 = Bukkit.createInventory(var1, this.size, ChatColor.BLUE + "Unlocking item");
               ItemStack var14 = this.plugin.pane_itemstack.clone();

               for(int var15 = 0; var15 < this.size; ++var15) {
                  var13.setItem(var15, var14);
               }

               ItemStack var16 = (new ItemStackBuilder(Material.STAINED_GLASS)).setName(" ").setDurability(15).build();
               var13.setItem(21, var16);
               var13.setItem(23, var16);
               var2.removeCoins(var1, this.cost);
               if (var2.lobbyScoreboard != null) {
                  var2.lobbyScoreboard.update((String)this.plugin.customization.scoreboard.get("Coins"), var2.getCoins(var1), true);
               }

               var1.openInventory(var13);
               (new BukkitRunnable() {
                  int runs = 0;
                  boolean run = true;

                  public void run() {
                     if (this.runs >= MysteryBox.this.slowDownAt) {
                        this.run = !this.run;
                     }

                     if (this.run) {
                        Iterator var2 = MysteryBox.this.backgroundSlots.iterator();

                        int var1x;
                        while(var2.hasNext()) {
                           var1x = (Integer)var2.next();
                           var13.getItem(var1x).setDurability((short)(MysteryBox.this.plugin.r.nextInt(6) + 1));
                        }

                        for(var1x = MysteryBox.this.slots.size() - 1; var1x > 0; --var1x) {
                           var13.setItem((Integer)MysteryBox.this.slots.get(var1x), var13.getItem((Integer)MysteryBox.this.slots.get(var1x - 1)));
                        }

                        var13.setItem((Integer)MysteryBox.this.slots.get(0), (ItemStack)MysteryBox.this.allItems.get(MysteryBox.this.plugin.r.nextInt(MysteryBox.this.allItems.size())));
                        var1.playSound(var1.getLocation(), MysteryBox.this.plugin.CLICK, 1.0F, 1.0F);
                     }

                     ++this.runs;
                     if (this.runs == MysteryBox.this.seconds) {
                        this.cancel();
                        var13.setItem(22, var12);
                        MysteryBox.this.plugin.fireWorkEffect(var1, true);
                        String var3x = ChatColor.stripColor(var12.getItemMeta().getDisplayName()).toLowerCase();
                        int var4 = MysteryBox.this.plugin.kits.containsKey(var3x) ? 0 : (MysteryBox.this.plugin.cages.containsKey(var3x) ? 1 : 2);
                        var3.addItem(var4, var12);
                        var1.sendMessage(((String)MysteryBox.this.plugin.customization.messages.get("Mystery-Box-Item-Unlock")).replace("%item%", var12.getItemMeta().getDisplayName()));
                     }

                  }
               }).runTaskTimer(this.plugin, 0L, 2L);
            }
         }
      }
   }
}
