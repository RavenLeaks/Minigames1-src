package me.wazup.eggwars;

import java.util.ArrayList;
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

class SmartInventory {
   private String mainName;
   private HashMap inventories = new HashMap();
   private ItemStack previous;
   private ItemStack next;
   private Eggwars plugin;

   public SmartInventory(Eggwars var1, String var2) {
      this.previous = (new ItemStackBuilder(Material.STAINED_GLASS)).setDurability(14).setName(ChatColor.RED + "Previous page").build();
      this.next = (new ItemStackBuilder(Material.STAINED_GLASS)).setDurability(5).setName(ChatColor.GREEN + "Next page").build();
      this.plugin = var1;
      this.mainName = var2 + ": ";
   }

   public int addInventory(String var1) {
      int var2 = this.inventories.size();
      Inventory var3 = Bukkit.createInventory((InventoryHolder)null, 54, this.mainName + var1);
      this.plugin.cageInventory(var3, false);
      if (var2 != 0) {
         var3.setItem(18, this.previous);
         var3.setItem(27, this.previous);
         Inventory var4 = (Inventory)this.inventories.get(var2 - 1);
         var4.setItem(26, this.next);
         var4.setItem(35, this.next);
      }

      this.inventories.put(var2, var3);
      return var2;
   }

   public void setItem(int var1, int var2, ItemStack var3) {
      ((Inventory)this.inventories.get(var1)).setItem(var2, var3);
   }

   public boolean addItem(int var1, ItemStack var2) {
      Inventory var3 = (Inventory)this.inventories.get(var1);
      int var4 = this.getEmptySlot(var1);
      if (var4 == -1) {
         return false;
      } else {
         var3.setItem(var4, var2);
         return true;
      }
   }

   public void removeItem(int var1, int var2) {
      this.setItem(var1, var2, new ItemStack(Material.AIR));
      this.organize(var1);
   }

   public ItemStack getItem(int var1, int var2) {
      return ((Inventory)this.inventories.get(var1)).getItem(var2);
   }

   public void clear(int var1) {
      Inventory var2 = (Inventory)this.inventories.get(var1);
      int[] var6;
      int var5 = (var6 = this.plugin.smartSlots).length;

      for(int var4 = 0; var4 < var5; ++var4) {
         int var3 = var6[var4];
         var2.setItem(var3, new ItemStack(Material.AIR));
      }

   }

   public void organize(int var1) {
      Inventory var2 = (Inventory)this.inventories.get(var1);
      ArrayList var3 = new ArrayList();
      int[] var7;
      int var6 = (var7 = this.plugin.smartSlots).length;

      int var4;
      for(int var5 = 0; var5 < var6; ++var5) {
         var4 = var7[var5];
         ItemStack var8 = var2.getItem(var4);
         if (var8 != null) {
            var3.add(var8);
            var2.setItem(var4, new ItemStack(Material.AIR));
         }
      }

      var4 = 0;

      for(Iterator var10 = var3.iterator(); var10.hasNext(); ++var4) {
         ItemStack var9 = (ItemStack)var10.next();
         var2.setItem(this.plugin.smartSlots[var4], var9);
      }

   }

   public int getEmptySlot(int var1) {
      Inventory var2 = (Inventory)this.inventories.get(var1);
      int[] var6;
      int var5 = (var6 = this.plugin.smartSlots).length;

      for(int var4 = 0; var4 < var5; ++var4) {
         int var3 = var6[var4];
         if (var2.getItem(var3) == null) {
            return var3;
         }
      }

      return -1;
   }

   public List getContents(int var1) {
      ArrayList var2 = new ArrayList();
      Inventory var3 = (Inventory)this.inventories.get(var1);
      int[] var7;
      int var6 = (var7 = this.plugin.smartSlots).length;

      for(int var5 = 0; var5 < var6; ++var5) {
         int var4 = var7[var5];
         ItemStack var8 = var3.getItem(var4);
         if (var8 != null) {
            var2.add(var8);
         }
      }

      return var2;
   }

   public boolean handleClick(Player var1, ItemStack var2, Inventory var3) {
      if (!this.plugin.compareItem(var2, this.next) && !this.plugin.compareItem(var2, this.previous)) {
         return false;
      } else {
         int var4 = 0;

         for(int var5 = 0; var5 < this.inventories.size(); ++var5) {
            var4 = var5;
            if (((Inventory)this.inventories.get(var5)).equals(var3)) {
               break;
            }
         }

         if (this.plugin.compareItem(var2, this.next)) {
            var1.openInventory((Inventory)this.inventories.get(var4 + 1));
         } else {
            var1.openInventory((Inventory)this.inventories.get(var4 - 1));
         }

         return true;
      }
   }

   public int getSize() {
      return this.inventories.size();
   }

   public String getName() {
      return this.mainName;
   }

   public void open(Player var1) {
      var1.openInventory((Inventory)this.inventories.get(0));
   }
}
